/*
 * Diagnostic (2026-11-16): Ctrl+Break for the JVM of IBM, which then writes its javacore (the
 * stacks of all the Java threads), without a keyboard or the focus. OS/2 sends the signal only
 * within a session (rc 5 from ssh), so brk starts the program as its child in its own session
 * and waits on an event semaphore, without the clock, until brk post (from any session):
 *   brk run <program> <arguments...>   starts the program and sends it Break on each post
 *   brk post                           wakes brk run
 *   brk <pid in hex>                   sends Break to that process (same session only)
 *   brk kill <pid in hex>              ends that process and its children (any session)
 */
#define INCL_DOSEXCEPTIONS
#define INCL_DOSSEMAPHORES
#define INCL_DOSPROCESS
#define INCL_DOSERRORS
#include <os2.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define SEM_NAME "\\SEM32\\SWTBRK"

static HEV sem;
static PID child;
static volatile int ended;

static void _Optlink waitChild(void *arg)
{
    RESULTCODES result;
    PID pid;
    DosWaitChild(DCWA_PROCESS, DCWW_WAIT, &result, &pid, child);
    ended = 1;
    DosPostEventSem(sem);
}

static int run(int argc, char *argv[])
{
    static char args[4096];
    char failure[256];
    RESULTCODES result;
    ULONG count;
    APIRET rc;
    char *p;
    int i;
    /* The name of the program, a null, the arguments separated by blanks, two nulls */
    strcpy(args, argv[2]);
    p = args + strlen(args) + 1;
    *p = 0;
    for (i = 3; i < argc; i++) {
        if (i > 3) strcat(p, " ");
        strcat(p, argv[i]);
    }
    p[strlen(p) + 1] = 0;
    rc = DosCreateEventSem(SEM_NAME, &sem, 0, FALSE);
    if (rc != NO_ERROR) {
        fprintf(stderr, "brk: DosCreateEventSem = %lu\n", rc);
        return rc;
    }
    rc = DosExecPgm(failure, sizeof(failure), EXEC_ASYNCRESULT, args, NULL, &result, argv[2]);
    if (rc != NO_ERROR) {
        fprintf(stderr, "brk: DosExecPgm = %lu (%s)\n", rc, failure);
        return rc;
    }
    child = result.codeTerminate;
    fprintf(stderr, "brk: started %s as pid 0x%lx\n", argv[2], child);
    fflush(stderr);
    /* Above the busy program it watches */
    DosSetPriority(PRTYS_THREAD, PRTYC_TIMECRITICAL, 0, 0);
    _beginthread(waitChild, NULL, 16384, NULL);
    while (1) {
        DosWaitEventSem(sem, SEM_INDEFINITE_WAIT);
        DosResetEventSem(sem, &count);
        if (ended) break;
        rc = DosSendSignalException(child, XCPT_SIGNAL_BREAK);
        fprintf(stderr, "brk: DosSendSignalException(0x%lx, BREAK) = %lu\n", child, rc);
        fflush(stderr);
    }
    return 0;
}

int main(int argc, char *argv[])
{
    APIRET rc;
    if (argc >= 3 && strcmp(argv[1], "run") == 0) return run(argc, argv);
    if (argc == 2 && strcmp(argv[1], "post") == 0) {
        rc = DosOpenEventSem(SEM_NAME, &sem);
        if (rc == NO_ERROR) rc = DosPostEventSem(sem);
        printf("brk post = %lu\n", rc);
        return rc;
    }
    if (argc == 3 && strcmp(argv[1], "kill") == 0) {
        PID pid = strtoul(argv[2], NULL, 16);
        rc = DosKillProcess(DKP_PROCESSTREE, pid);
        printf("DosKillProcess(0x%lx) = %lu\n", pid, rc);
        return rc;
    }
    if (argc == 2) {
        PID pid = strtoul(argv[1], NULL, 16);
        rc = DosSendSignalException(pid, XCPT_SIGNAL_BREAK);
        printf("DosSendSignalException(0x%lx, BREAK) = %lu\n", pid, rc);
        return rc;
    }
    printf("usage: brk run <program> <arguments...> | brk post | brk kill <pid> | brk <pid in hex>\n");
    return 1;
}
