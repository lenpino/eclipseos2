/*
 * OS/2 version.
 * Copyright (c) 2002, 2008 EclipseOS2 Team.
 */

/*
 * Copyright (c) 2000, 2002 IBM Corp.  All rights reserved.
 * This file is made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 */

/**
 * SWT OS natives implementation.
 */

#include "swt.h"
#include "structs.h"
#include "editcol.h"
#include <malloc.h>
#include <memory.h>

#include <float.h>

#include <string.h>

#ifdef DEBUG_DLL
void writeStat(char* resource, jint handle, char* status, char* api)
{
    HFILE hFile;
    ULONG ulAction;
    ULONG pcbActual;
    ULONG ulLocation;
    DATETIME now;
    char buffer[256];

    DosGetDateTime(&now);
    sprintf(buffer, "%s,%#011x,%s,%s,%d:%d:%d;",
        resource, handle, status, api, now.hours, now.minutes, now.seconds);
    DosOpen("swtstats.scratch.log", &hFile, &ulAction, 0, FILE_NORMAL,
        OPEN_ACTION_CREATE_IF_NEW | OPEN_ACTION_OPEN_IF_EXISTS,
        OPEN_FLAGS_SEQUENTIAL | OPEN_SHARE_DENYWRITE |
            OPEN_ACCESS_READWRITE, (PEAOP2)NULL);
    DosSetFilePtr(hFile, 0L, FILE_END, &ulLocation);
    DosWrite(hFile, (PVOID)buffer, strlen(buffer), &pcbActual);
    DosClose(hFile);
}
#endif

/* Natives */

JNIEXPORT jint JNICALL Java_org_eclipse_swt_internal_pm_OS_malloc
        (JNIEnv *env, jclass that, jint size)
{
    DEBUG_CALL("malloc\n")

    return (jint)malloc((size_t)size);
}

JNIEXPORT void JNICALL Java_org_eclipse_swt_internal_pm_OS_free
        (JNIEnv *env, jclass that, jint block)
{
    DEBUG_CALL("free\n")

    free((void*)block);
}

JNIEXPORT void JNICALL Java_org_eclipse_swt_internal_pm_OS_memcpy
        (JNIEnv *env, jclass that, jint dest, jint src, jint n)
{
    DEBUG_CALL("memcpy\n")

    memcpy((void*)dest, (void*)src, (size_t)n);
}

JNIEXPORT void JNICALL Java_org_eclipse_swt_internal_pm_OS_memset
        (JNIEnv *env, jclass that, jint str, jint ch, jint n)
{
    DEBUG_CALL("memset\n")

    memset((void*)str, (int)ch, (size_t)n);
}

/* Native<->Java Object copiers */

JNIEXPORT void JNICALL Java_org_eclipse_swt_internal_pm_OS_objcpy__I_3I
        (JNIEnv *env, jclass that, jint parray, jintArray array)
{
    jint *c_array = NULL;

    DEBUG_CALL("objcpy\n")

    if (array) c_array = (*env)->GetIntArrayElements(env, array, NULL);
    
    memcpy ((void*)parray, (void*)c_array, (size_t)(*env)->GetArrayLength(env, array));

    if (array) (*env)->ReleaseIntArrayElements(env, array, c_array, 0);
}

JNIEXPORT void JNICALL Java_org_eclipse_swt_internal_pm_OS_objcpy___3II
        (JNIEnv *env, jclass that, jintArray array, jint parray)
{
    jint *c_array = NULL;

    DEBUG_CALL("objcpy\n")

    if (array) c_array = (*env)->GetIntArrayElements(env, array, NULL);
    
    memcpy ((void*)c_array, (void*)parray, (size_t)(*env)->GetArrayLength(env, array));

    if (array) (*env)->ReleaseIntArrayElements(env, array, c_array, 0);
}

JNIEXPORT void JNICALL Java_org_eclipse_swt_internal_pm_OS_objcpy__ILorg_eclipse_swt_internal_pm_TIB_2
        (JNIEnv *env, jclass that, jint ptib, jobject tib)
{
    DECL_GLOB(pGlob)

    DEBUG_CALL("objcpy\n")
    DEBUG_CHECK_NULL(env, (ptib && tib))

    if (ptib && tib) getTIBFields(env, tib, (PTIB)ptib, &PGLOB(TIBFc));
}

JNIEXPORT void JNICALL Java_org_eclipse_swt_internal_pm_OS_objcpy__Lorg_eclipse_swt_internal_pm_TIB_2I
        (JNIEnv *env, jclass that, jobject tib, jint ptib)
{
    DECL_GLOB(pGlob)

    DEBUG_CALL("objcpy\n")
    DEBUG_CHECK_NULL(env, (tib && ptib))

    if (tib && ptib) setTIBFields(env, tib, (PTIB)ptib, &PGLOB(TIBFc));
}

JNIEXPORT void JNICALL Java_org_eclipse_swt_internal_pm_OS_objcpy__ILorg_eclipse_swt_internal_pm_PIB_2
        (JNIEnv *env, jclass that, jint ppib, jobject pib)
{
    DECL_GLOB(pGlob)

    DEBUG_CALL("objcpy\n")
    DEBUG_CHECK_NULL(env, (ppib && pib))

    if (ppib && pib) getPIBFields(env, pib, (PPIB)ppib, &PGLOB(PIBFc));
}

JNIEXPORT void JNICALL Java_org_eclipse_swt_internal_pm_OS_objcpy__Lorg_eclipse_swt_internal_pm_PIB_2I
        (JNIEnv *env, jclass that, jobject pib, jint ppib)
{
    DECL_GLOB(pGlob)

    DEBUG_CALL("objcpy\n")
    DEBUG_CHECK_NULL(env, (pib && ppib))

    if (pib && ppib) setPIBFields(env, pib, (PPIB)ppib, &PGLOB(PIBFc));
}

JNIEXPORT void JNICALL Java_org_eclipse_swt_internal_pm_OS_objcpy__ILorg_eclipse_swt_internal_pm_RECTL_2
    (JNIEnv *env, jclass that, jint prcl, jobject rcl)
{
    DECL_GLOB(pGlob)

    DEBUG_CALL("objcpy\n")
    DEBUG_CHECK_NULL(env, (prcl && rcl))

    if (prcl && rcl) getRECTLFields(env, rcl, (PRECTL)prcl, &PGLOB(RECTLFc));
}

JNIEXPORT void JNICALL Java_org_eclipse_swt_internal_pm_OS_objcpy__Lorg_eclipse_swt_internal_pm_RECTL_2I
    (JNIEnv *env, jclass that, jobject rcl, jint prcl)
{
    DECL_GLOB(pGlob)

    DEBUG_CALL("objcpy\n")
    DEBUG_CHECK_NULL(env, (rcl && prcl))

    if (rcl && prcl) setRECTLFields(env, rcl, (PRECTL)prcl, &PGLOB(RECTLFc));
}

JNIEXPORT void JNICALL Java_org_eclipse_swt_internal_pm_OS_objcpy__ILorg_eclipse_swt_internal_pm_SWP_2
    (JNIEnv *env, jclass that, jint pswp, jobject swp)
{
    DECL_GLOB(pGlob)

    DEBUG_CALL("objcpy\n")
    DEBUG_CHECK_NULL(env, (pswp && swp))

    if (pswp && swp) getSWPFields(env, swp, (PSWP)pswp, &PGLOB(SWPFc));
}

JNIEXPORT void JNICALL Java_org_eclipse_swt_internal_pm_OS_objcpy__Lorg_eclipse_swt_internal_pm_SWP_2I
    (JNIEnv *env, jclass that, jobject swp, jint pswp)
{
    DECL_GLOB(pGlob)

    DEBUG_CALL("objcpy\n")
    DEBUG_CHECK_NULL(env, (swp && pswp))

    if (swp && pswp) setSWPFields(env, swp, (PSWP)pswp, &PGLOB(SWPFc));
}

/* PM functions */

JNIEXPORT jint JNICALL Java_org_eclipse_swt_internal_pm_OS_DevOpenDC
        (JNIEnv *env, jclass that, jint hab, jint lType, jobject pszToken,
        jint lCount, jobjectArray pdopData, jint hdcComp)
{
    PSZ c_pszToken = NULL;
    DEVOPENSTRUC devData;
    jobject psz;
    int i;
    jint rc;

    DEBUG_CALL("DevOpenDC\n")

    if (pszToken) c_pszToken = getPSZBytes(env, pszToken);

    for (i = 0; i < lCount; i++) {
        psz = (*env)->GetObjectArrayElement(env, pdopData, i);
        if (psz) ((PDEVOPENDATA)&devData)[i] = getPSZBytes(env, psz);
    }

    rc = (jint)DevOpenDC((HAB)hab, lType, c_pszToken, lCount, (PDEVOPENDATA)&devData, (HDC)hdcComp);

    for (i = 0; i < lCount; i++) {
        psz = (*env)->GetObjectArrayElement(env, pdopData, i);
        if (psz) releasePSZBytes(env, psz, ((PDEVOPENDATA)&devData)[i]);
    }

    if (pszToken) releasePSZBytes(env, pszToken, c_pszToken);

    return rc;
}

JNIEXPORT jint JNICALL Java_org_eclipse_swt_internal_pm_OS_DevCloseDC
        (JNIEnv *env, jclass that, jint hdc)
{
    DEBUG_CALL("DevCloseDC\n")

    return (jint)DevCloseDC((HDC)hdc);
}

JNIEXPORT jboolean JNICALL Java_org_eclipse_swt_internal_pm_OS_DevQueryCaps
        (JNIEnv *env, jclass that, jint hdc, jint lStart, jint lCount, jintArray alArray)
{
    jint *c_alArray = NULL;
    jboolean rc;

    DEBUG_CALL("DevQueryCaps\n")

    if (alArray) c_alArray = (*env)->GetIntArrayElements(env, alArray, NULL);

    rc = (jboolean)DevQueryCaps((HDC)hdc, lStart, lCount, c_alArray);

    if (alArray) (*env)->ReleaseIntArrayElements(env, alArray, c_alArray, 0);

    return rc;
}

JNIEXPORT jboolean JNICALL Java_org_eclipse_swt_internal_pm_OS_WinAlarm
        (JNIEnv *env, jclass that, jint hwndDeskTop, jint flStyle)
{
    DEBUG_CALL("WinAlarm\n")

    return (jboolean)WinAlarm((HWND)hwndDeskTop, flStyle);
}

JNIEXPORT jint JNICALL Java_org_eclipse_swt_internal_pm_OS_WinInitialize
        (JNIEnv *env, jclass that, jint flOptions)
{
    DEBUG_CALL("WinInitialize\n")

    return (jint)WinInitialize(flOptions);
}

JNIEXPORT jboolean JNICALL Java_org_eclipse_swt_internal_pm_OS_WinTerminate
        (JNIEnv *env, jclass that, jint hab)
{
    DEBUG_CALL("WinTerminate\n")

    return (jboolean)WinTerminate(hab);
}

JNIEXPORT jint JNICALL Java_org_eclipse_swt_internal_pm_OS_WinGetLastError
        (JNIEnv *env, jclass that, jint hab)
{
    DEBUG_CALL("WinGetLastError\n")

    return (jint)WinGetLastError(hab);
}

JNIEXPORT jint JNICALL Java_org_eclipse_swt_internal_pm_OS_WinCreateMsgQueue
        (JNIEnv *env, jclass that, jint hab, jint lQueuesize)
{
    jint rc;
    
    DEBUG_CALL("WinCreateMsgQueue\n")

    rc = (jint)WinCreateMsgQueue(hab, lQueuesize);
    /*
     *  Bug in OS/2. WinCreateMsgQueue() does not restore the FPU Control Word
     *  value as it was before this call. This sometimes causes a trap
     *  (XCPT_FLOAT_STACK_CHECK) inside the GoldenCode JVM because this JVM is
     *  made in the strict accordance with the Sun JVM specs which require that
     *  the FPU CW must be always equal to 0x027F inside the JVM. The solution
     *  is to restore this value after this API call. 
     */
    _control87(0x027F, 0xFFFF); 
    
    return rc;
}

JNIEXPORT jboolean JNICALL Java_org_eclipse_swt_internal_pm_OS_WinDestroyMsgQueue
        (JNIEnv *env, jclass that, jint hmq)
{
    DEBUG_CALL("WinDestroyMsgQueue\n")

    return (jboolean)WinDestroyMsgQueue(hmq);
}

JNIEXPORT jboolean JNICALL Java_org_eclipse_swt_internal_pm_OS_WinRegisterClass
        (JNIEnv *env, jclass that, jint hab, jobject pszClassName,
            jint pfnWndProc, jint flStyle, jint cbWindowData)
{
    PSZ c_pszClassName = NULL;
    jboolean rc;

    DEBUG_CALL("WinRegisterClass\n")

    if (pszClassName) c_pszClassName = getPSZBytes(env, pszClassName);

    rc = (jboolean)WinRegisterClass( hab, c_pszClassName, (PFNWP)pfnWndProc, flStyle, cbWindowData);

    if (pszClassName) releasePSZBytes(env, pszClassName, c_pszClassName);

    return rc;
}

JNIEXPORT jint JNICALL Java_org_eclipse_swt_internal_pm_OS_WinCreateWindow
        (JNIEnv *env, jclass that, jint hwndParent, jobject pszClass,
            jobject pszName, jint flStyle, jint x, jint y, jint cx, jint cy, jint hwndOwner,
            jint hwndInsertBehind, jint id, jint pCtlData, jint pPresParams)
{
    PSZ c_pszClass = NULL;
    PSZ c_pszName = NULL;
    jint hwnd;

    DEBUG_CALL("WinCreateWindow\n")

    if (pszClass) c_pszClass = getPSZBytes(env, pszClass);
    if (pszName) c_pszName = getPSZBytes(env, pszName);

    hwnd = (jint)WinCreateWindow((HWND)hwndParent, c_pszClass, c_pszName, flStyle,
        x, y, cx, cy, hwndOwner, hwndInsertBehind, id, (PVOID)pCtlData, (PVOID)pPresParams);

    if (pszName) releasePSZBytes(env, pszName, c_pszName);
    if (pszClass) releasePSZBytes(env, pszClass, c_pszClass);

    return hwnd;
}

JNIEXPORT jboolean JNICALL Java_org_eclipse_swt_internal_pm_OS_WinDestroyWindow
        (JNIEnv *env, jclass that, jint hwnd)
{
    DEBUG_CALL("WinDestroyWindow\n")

    return (jboolean)WinDestroyWindow((HWND)hwnd);
}

JNIEXPORT jint JNICALL Java_org_eclipse_swt_internal_pm_OS_WinDefWindowProc
        (JNIEnv *env, jclass that, jint hwnd, jint ulMsgid, jint mpParam1, jint mpParam2)
{
    DEBUG_CALL("WinDefWindowProc\n")

    return (jint)WinDefWindowProc((HWND)hwnd, ulMsgid, (MPARAM)mpParam1, (MPARAM)mpParam2);
}

JNIEXPORT jboolean JNICALL Java_org_eclipse_swt_internal_pm_OS_WinPeekMsg
        (JNIEnv *env, jclass that, jint hab, jobject qmsg,
            jint hwndFilter, jint ulFirst, jint ulLast, jint flOptions)
{
    DECL_GLOB(pGlob)
    QMSG c_qmsg, *c_pqmsg = NULL;
    jboolean rc;

    DEBUG_CALL("WinPeekMsg\n")

    if (qmsg) c_pqmsg = getQMSGFields(env, qmsg, &c_qmsg, &PGLOB(QMSGFc));

    rc = WinPeekMsg(hab, c_pqmsg, hwndFilter, ulFirst, ulLast, flOptions);

    if (qmsg) setQMSGFields(env, qmsg, c_pqmsg, &PGLOB(QMSGFc));

    return rc;
}

JNIEXPORT jint JNICALL Java_org_eclipse_swt_internal_pm_OS_WinDispatchMsg
        (JNIEnv *env, jclass that, jint hab, jobject qmsgMsg)
{
    DECL_GLOB(pGlob)
    QMSG c_qmsgMsg, *c_pqmsgMsg = NULL;
    jint rc;

    DEBUG_CALL("WinDispatchMsg\n")

    if (qmsgMsg) c_pqmsgMsg = getQMSGFields(env, qmsgMsg, &c_qmsgMsg, &PGLOB(QMSGFc));

    rc = (jint)WinDispatchMsg(hab, c_pqmsgMsg);

    if (qmsgMsg) setQMSGFields(env, qmsgMsg, c_pqmsgMsg, &PGLOB(QMSGFc));

    return rc;
}

JNIEXPORT jboolean JNICALL Java_org_eclipse_swt_internal_pm_OS_WinPostQueueMsg
        (JNIEnv *env, jclass that, jint hmq, jint msg, jint mp1, jint mp2)
{
    DEBUG_CALL("WinPostQueueMsg\n")

    return (jboolean)WinPostQueueMsg((HMQ)hmq, msg, (MPARAM)mp1, (MPARAM)mp2);
}

JNIEXPORT jboolean JNICALL Java_org_eclipse_swt_internal_pm_OS_WinWaitMsg
        (JNIEnv *env, jclass that, jint hab, jint ulFirst, jint ulLast)
{
    DEBUG_CALL("WinWaitMsg\n")

    return (jboolean)WinWaitMsg((HAB)hab, ulFirst, ulLast);
}

JNIEXPORT jint JNICALL Java_org_eclipse_swt_internal_pm_OS_WinSendMsg__IIII
        (JNIEnv *env, jclass that, jint hwnd, jint ulMsgid, jint mpParam1, jint mpParam2)
{
    DEBUG_CALL("WinSendMsg\n")

    return (jint)WinSendMsg((HWND)hwnd, ulMsgid, (MPARAM)mpParam1, (MPARAM)mpParam2);
}

JNIEXPORT jint JNICALL Java_org_eclipse_swt_internal_pm_OS_WinSendMsg__IILorg_eclipse_swt_internal_pm_RECTL_2I
        (JNIEnv *env, jclass that, jint hwnd, jint ulMsgid, jobject mpParam1, jint mpParam2)
{
    DECL_GLOB(pGlob)
    RECTL c_mpParam1, *c_pmpParam1 = NULL;
    jint rc;

    DEBUG_CALL("WinSendMsg\n")

    if (mpParam1) c_pmpParam1 = getRECTLFields(env, mpParam1, &c_mpParam1, &PGLOB(RECTLFc));

    rc = (jint)WinSendMsg((HWND)hwnd, ulMsgid, (MPARAM)c_pmpParam1, (MPARAM)mpParam2);

    if (mpParam1) setRECTLFields(env, mpParam1, c_pmpParam1, &PGLOB(RECTLFc));

    return rc;
}
 
JNIEXPORT jint JNICALL Java_org_eclipse_swt_internal_pm_OS_WinSendMsg__IILorg_eclipse_swt_internal_pm_PSZ_2I
        (JNIEnv *env, jclass that, jint hwnd, jint ulMsgid, jobject mpParam1, jint mpParam2)
{
    DECL_GLOB(pGlob)
    PSZ c_mpParam1 = NULL;
    jint rc;

    DEBUG_CALL("WinSendMsg\n")

    if (mpParam1) c_mpParam1 = getPSZBytes(env, mpParam1);

    rc = (jint)WinSendMsg((HWND)hwnd, ulMsgid, (MPARAM)c_mpParam1, (MPARAM)mpParam2);

    if (mpParam1) releasePSZBytes(env, mpParam1, c_mpParam1);

    return rc;
}

JNIEXPORT jint JNICALL Java_org_eclipse_swt_internal_pm_OS_WinSendMsg__IIILorg_eclipse_swt_internal_pm_PSZ_2
        (JNIEnv *env, jclass that, jint hwnd, jint ulMsgid, jint mpParam1, jobject mpParam2)
{
    DECL_GLOB(pGlob)
    PSZ c_mpParam2 = NULL;
    jint rc;

    DEBUG_CALL("WinSendMsg\n")

    if (mpParam2) c_mpParam2 = getPSZBytes(env, mpParam2);

    rc = (jint)WinSendMsg((HWND)hwnd, ulMsgid, (MPARAM)mpParam1, (MPARAM)c_mpParam2);

    if (mpParam2) releasePSZBytes(env, mpParam2, c_mpParam2);

    return rc;
}

JNIEXPORT jint JNICALL Java_org_eclipse_swt_internal_pm_OS_WinSendMsg__IILorg_eclipse_swt_internal_pm_LBOXINFO_2_3Lorg_eclipse_swt_internal_pm_PSZ_2
        (JNIEnv *env, jclass that, jint hwnd, jint ulMsgid, jobject mpParam1, jobjectArray mpParam2)
{
    PAPSZ c_mpParam2 = NULL;
    LBOXINFO c_mpParam1, *c_pmpParam1 = NULL;
    jint rc;

    if (mpParam2) 
        c_mpParam2 = getPAPSZArray(env, mpParam2);
    if (mpParam1) 
        c_pmpParam1 = getLBOXINFOFields(env, mpParam1, &c_mpParam1, &PGLOB(LBOXINFOFc));

    rc = (jint)WinSendMsg((HWND)hwnd, ulMsgid, c_pmpParam1, c_mpParam2);

    if (mpParam2)
        releasePAPSZArray(env, mpParam2, c_mpParam2);
    if (mpParam1)
        setLBOXINFOFields(env, mpParam1, c_pmpParam1, &PGLOB(LBOXINFOFc));
    return rc;
}

JNIEXPORT jint JNICALL Java_org_eclipse_swt_internal_pm_OS_WinSendMsg__IILorg_eclipse_swt_internal_pm_MENUITEM_2Lorg_eclipse_swt_internal_pm_PSZ_2
         (JNIEnv *env, jclass that, jint hwnd, jint ulMsgid, jobject mpParam1, jobject mpParam2)
{
    PSZ c_mpParam2 = NULL;
    MENUITEM c_mpParam1, *c_pmpParam1 = NULL;
    jint rc;
    if (mpParam2) 
        c_mpParam2 = getPSZBytes(env, mpParam2);
    if (mpParam1) 
        c_pmpParam1 = getMENUITEMFields(env, mpParam1, &c_mpParam1, &PGLOB(MENUITEMFc));
    rc = (jint)WinSendMsg((HWND)hwnd, ulMsgid, c_pmpParam1, c_mpParam2);
    if (mpParam2)
        releasePSZBytes(env, mpParam2, c_mpParam2);
    if (mpParam1)
        setMENUITEMFields(env, mpParam1, c_pmpParam1, &PGLOB(MENUITEMFc));
    return rc;
}

JNIEXPORT jboolean JNICALL Java_org_eclipse_swt_internal_pm_OS_WinSendMsg__IIILorg_eclipse_swt_internal_pm_MENUITEM_2
         (JNIEnv *env, jclass that, jint hwnd, jint ulMsgid, jint mpParam1, jobject mpParam2)
{

    MENUITEM c_mpParam2, *c_pmpParam2 = NULL;
    jboolean rc, rc2;

    if (mpParam2) 
        c_pmpParam2 = getMENUITEMFields(env, mpParam2, &c_mpParam2, &PGLOB(MENUITEMFc));

    rc = (jboolean)WinSendMsg((HWND)hwnd, ulMsgid, (MPARAM)mpParam1, MPFROMP(c_pmpParam2));
    if (mpParam2)
        setMENUITEMFields(env, mpParam2, c_pmpParam2, &PGLOB(MENUITEMFc));
    return rc;
}

JNIEXPORT jboolean JNICALL Java_org_eclipse_swt_internal_pm_OS_WinPostMsg
        (JNIEnv *env, jclass that, jint hwnd, jint ulMsgid, jint mpParam1, jint mpParam2)
{
    DEBUG_CALL("WinPostMsg\n")

    return (jboolean)WinPostMsg((HWND)hwnd, ulMsgid, (MPARAM)mpParam1, (MPARAM)mpParam2);
}

JNIEXPORT jint JNICALL Java_org_eclipse_swt_internal_pm_OS_WinQueryMsgTime
        (JNIEnv *env, jclass that, jint hab)
{
    DEBUG_CALL("WinQueryMsgTime\n")

    return (jint)WinQueryMsgTime((HAB)hab);
}

JNIEXPORT jboolean JNICALL Java_org_eclipse_swt_internal_pm_OS_WinQueryMsgPos
        (JNIEnv *env, jclass that, jint hab, jintArray pptl)
{
    jint *c_pptl = NULL;
    jboolean rc;

    DEBUG_CALL("WinQueryMsgPos\n")

    if (pptl) c_pptl = (*env)->GetIntArrayElements(env, pptl, NULL);

    rc = (jboolean)WinQueryMsgPos((HAB)hab, (PPOINTL)c_pptl);

    if (pptl) (*env)->ReleaseIntArrayElements(env, pptl, c_pptl, 0);

    return rc;
}

JNIEXPORT jint JNICALL Java_org_eclipse_swt_internal_pm_OS_WinQueryWindowTextLength
        (JNIEnv *env, jclass that, jint hwnd)
{
    DEBUG_CALL("WinQueryWindowTextLength\n")

    return (jint)WinQueryWindowTextLength((HWND)hwnd);
}

JNIEXPORT jint JNICALL Java_org_eclipse_swt_internal_pm_OS_WinQueryWindowText
        (JNIEnv *env, jclass that, jint hwnd, jint lLength, jobject pszBuffer)
{
    DECL_GLOB(pGlob)
    PSZ c_pszBuffer = NULL;
    jint rc;

    DEBUG_CALL("WinQueryWindowText\n")

    if (pszBuffer) c_pszBuffer = getPSZBytes(env, pszBuffer);

    rc = WinQueryWindowText((HWND)hwnd, lLength, c_pszBuffer);

    if (pszBuffer) releasePSZBytes(env, pszBuffer, c_pszBuffer);

    return rc;
}

JNIEXPORT jboolean JNICALL Java_org_eclipse_swt_internal_pm_OS_WinSetWindowText
        (JNIEnv *env, jclass that, jint hwnd, jobject pszString)
{
    DECL_GLOB(pGlob)
    PSZ c_pszString = NULL;
    jboolean rc;

    DEBUG_CALL("WinSetWindowText\n")

    if (pszString) c_pszString = getPSZBytes(env, pszString);

    rc = (jboolean)WinSetWindowText((HWND)hwnd, c_pszString);

    if (pszString) releasePSZBytes(env, pszString, c_pszString);

    return rc;
}

JNIEXPORT jint JNICALL Java_org_eclipse_swt_internal_pm_OS_WinQueryWindowULong
        (JNIEnv *env, jclass that, jint hwnd, jint index)
{
    DEBUG_CALL("WinQueryWindowULong\n")

    return (jint)WinQueryWindowULong((HWND)hwnd, index);
}

JNIEXPORT jboolean JNICALL Java_org_eclipse_swt_internal_pm_OS_WinSetWindowULong
        (JNIEnv *env, jclass that, jint hwnd, jint index, jint ul)
{
    DEBUG_CALL("WinSetWindowULong\n")

    return (jboolean)WinSetWindowULong((HWND)hwnd, index, ul);
}

JNIEXPORT jshort JNICALL Java_org_eclipse_swt_internal_pm_OS_WinQueryWindowUShort
        (JNIEnv *env, jclass that, jint hwnd, jint index)
{
    DEBUG_CALL("WinQueryWindowUShort\n")

    return (jshort)WinQueryWindowUShort((HWND)hwnd, index);
}

JNIEXPORT jboolean JNICALL Java_org_eclipse_swt_internal_pm_OS_WinSetWindowUShort
        (JNIEnv *env, jclass that, jint hwnd, jint index, jshort us)
{
    DEBUG_CALL("WinSetWindowUShort\n")

    return (jboolean)WinSetWindowUShort((HWND)hwnd, index, us);
}

JNIEXPORT jint JNICALL Java_org_eclipse_swt_internal_pm_OS_WinQueryWindowPtr
        (JNIEnv *env, jclass that, jint hwnd, jint index)
{
    DEBUG_CALL("WinQueryWindowPtr\n")

    return (jint)WinQueryWindowPtr((HWND)hwnd, index);
}

JNIEXPORT jboolean JNICALL Java_org_eclipse_swt_internal_pm_OS_WinSetWindowPtr
        (JNIEnv *env, jclass that, jint hwnd, jint lb, jint pp)
{
    DEBUG_CALL("WinSetWindowPtr\n")

    return (jboolean)WinSetWindowPtr((HWND)hwnd, lb, (PVOID)pp);
}

JNIEXPORT jboolean JNICALL Java_org_eclipse_swt_internal_pm_OS_WinQueryWindowRect
        (JNIEnv *env, jclass that, jint hwnd, jobject prclDest)
{
    DECL_GLOB(pGlob)
    RECTL c_rclDest, *c_prclDest = NULL;
    jboolean rc;

    DEBUG_CALL("WinQueryWindowRect\n")

    if (prclDest) c_prclDest = getRECTLFields(env, prclDest, &c_rclDest, &PGLOB(RECTLFc));

    rc = (jboolean)WinQueryWindowRect((HWND)hwnd, c_prclDest);

    if (prclDest) setRECTLFields(env, prclDest, c_prclDest, &PGLOB(RECTLFc));

    return rc;
}

JNIEXPORT jboolean JNICALL Java_org_eclipse_swt_internal_pm_OS_WinMapWindowPoints
        (JNIEnv *env, jclass that, jint hwndFrom, jint hwndTo, jintArray prgptl, jint cwpt)
{
    jint *c_prgptl = NULL;
    jboolean rc;

    DEBUG_CALL("WinMapWindowPoints\n")

    if (prgptl) c_prgptl = (*env)->GetIntArrayElements(env, prgptl, NULL);

    rc = (jboolean)WinMapWindowPoints((HWND)hwndFrom, (HWND)hwndTo, (PPOINTL)c_prgptl, cwpt);

    if (prgptl) (*env)->ReleaseIntArrayElements(env, prgptl, c_prgptl, 0);

    return rc;
}

JNIEXPORT jboolean JNICALL Java_org_eclipse_swt_internal_pm_OS_WinQueryWindowPos
        (JNIEnv *env, jclass that, jint hwnd, jobject pswp)
{
    DECL_GLOB(pGlob)
    SWP c_swp, *c_pswp = NULL;
    jboolean rc;

    DEBUG_CALL("WinQueryWindowPos\n")

    if (pswp) c_pswp = getSWPFields(env, pswp, &c_swp, &PGLOB(SWPFc));

    rc = (jboolean)WinQueryWindowPos((HWND)hwnd, c_pswp);

    if (pswp) setSWPFields(env, pswp, c_pswp, &PGLOB(SWPFc));

    return rc;
}

JNIEXPORT jboolean JNICALL Java_org_eclipse_swt_internal_pm_OS_WinSetWindowPos
        (JNIEnv *env, jclass that, jint hwnd, jint hwndInsertBehind,
        jint x, jint y, jint cx, jint cy, jint fl)
{
    DEBUG_CALL("WinSetWindowPos\n")

    return (jboolean)WinSetWindowPos((HWND)hwnd, hwndInsertBehind, x, y, cx, cy, fl);
}

JNIEXPORT jboolean JNICALL Java_org_eclipse_swt_internal_pm_OS_WinSetMultWindowPos
        (JNIEnv *env, jclass that, jint hab, jint pswp, jint cswp)
{
    DEBUG_CALL("WinSetMultWindowPos\n")

    return (jboolean)WinSetMultWindowPos((HAB)hab, (PSWP)pswp, cswp);
}

JNIEXPORT jboolean JNICALL Java_org_eclipse_swt_internal_pm_OS_WinGetMaxPosition
        (JNIEnv *env, jclass that, jint hwnd, jobject pswp)
{
    DECL_GLOB(pGlob)
    SWP c_swp, *c_pswp = NULL;
    jboolean rc;

    DEBUG_CALL("WinGetMaxPosition\n")

    if (pswp) c_pswp = getSWPFields(env, pswp, &c_swp, &PGLOB(SWPFc));

    rc = (jboolean)WinGetMaxPosition((HWND)hwnd, c_pswp);

    if (pswp) setSWPFields(env, pswp, c_pswp, &PGLOB(SWPFc));

    return rc;
}

JNIEXPORT jboolean JNICALL Java_org_eclipse_swt_internal_pm_OS_WinQueryClassInfo
        (JNIEnv *env, jclass that, jint hab, jobject pszClassName, jobject pclsiClassInfo)
{
    DECL_GLOB(pGlob)
    PSZ c_pszClassName = NULL;
    CLASSINFO c_clsiClassInfo, *c_pclsiClassInfo = NULL;
    jboolean rc;

    DEBUG_CALL("WinQueryClassInfo\n")

    if (pszClassName) c_pszClassName = getPSZBytes(env, pszClassName);
    if (pclsiClassInfo) c_pclsiClassInfo = getCLASSINFOFields(env, pclsiClassInfo, &c_clsiClassInfo, &PGLOB(CLASSINFOFc));

    rc = (jboolean)WinQueryClassInfo((HAB)hab, c_pszClassName, c_pclsiClassInfo);

    if (pclsiClassInfo) setCLASSINFOFields(env, pclsiClassInfo, c_pclsiClassInfo, &PGLOB(CLASSINFOFc));
    if (pszClassName) releasePSZBytes(env, pszClassName, c_pszClassName);

    return rc;
}

JNIEXPORT jint JNICALL Java_org_eclipse_swt_internal_pm_OS_WinQuerySysValue
        (JNIEnv *env, jclass that, jint hwndDeskTop, jint iSysValue)
{
    DEBUG_CALL("WinQuerySysValue\n")

    return (jint)WinQuerySysValue((HWND)hwndDeskTop, iSysValue);
}

JNIEXPORT jint JNICALL Java_org_eclipse_swt_internal_pm_OS_WinQuerySysColor
        (JNIEnv *env, jclass that, jint hwndDeskTop, jint clr, jint lReserved)
{
    DEBUG_CALL("WinQuerySysColor\n")

    return (jint)WinQuerySysColor((HWND)hwndDeskTop, clr, lReserved);
}

JNIEXPORT jint JNICALL Java_org_eclipse_swt_internal_pm_OS_WinQueryTaskSizePos
        (JNIEnv *env, jclass that, jint hab, jint ulID, jobject pswp)
{
    DECL_GLOB(pGlob)
    SWP c_swp, *c_pswp = NULL;
    jint rc;

    DEBUG_CALL("WinQueryTaskSizePos\n")

    if (pswp) c_pswp = getSWPFields(env, pswp, &c_swp, &PGLOB(SWPFc));

    rc = (jint)WinQueryTaskSizePos((HAB)hab, ulID, c_pswp);

    if (pswp) setSWPFields(env, pswp, c_pswp, &PGLOB(SWPFc));

    return rc;
}

JNIEXPORT jboolean JNICALL Java_org_eclipse_swt_internal_pm_OS_WinIsWindow
        (JNIEnv *env, jclass that, jint hab, jint hwnd)
{
    DEBUG_CALL("WinIsWindow\n")

    return (jboolean)WinIsWindow((HAB)hab, (HWND)hwnd);
}

JNIEXPORT jboolean JNICALL Java_org_eclipse_swt_internal_pm_OS_WinIsWindowEnabled
        (JNIEnv *env, jclass that, jint hwnd)
{
    DEBUG_CALL("WinIsWindowEnabled\n")

    return (jboolean)WinIsWindowEnabled((HWND)hwnd);
}

JNIEXPORT jboolean JNICALL Java_org_eclipse_swt_internal_pm_OS_WinIsWindowVisible
        (JNIEnv *env, jclass that, jint hwnd)
{
    DEBUG_CALL("WinIsWindowVisible\n")

    return (jboolean)WinIsWindowVisible((HWND)hwnd);
}

JNIEXPORT jboolean JNICALL Java_org_eclipse_swt_internal_pm_OS_WinIsWindowShowing
        (JNIEnv *env, jclass that, jint hwnd)
{
    DEBUG_CALL("WinIsWindowShowing\n")

    return (jboolean)WinIsWindowShowing((HWND)hwnd);
}

JNIEXPORT jboolean JNICALL Java_org_eclipse_swt_internal_pm_OS_WinEnableWindowUpdate
        (JNIEnv *env, jclass that, jint hwnd, jboolean fEnable)
{
    DEBUG_CALL("WinEnableWindowUpdate\n")

    return (jboolean)WinEnableWindowUpdate((HWND)hwnd, fEnable);
}

JNIEXPORT jint JNICALL Java_org_eclipse_swt_internal_pm_OS_WinQueryFocus
        (JNIEnv *env, jclass that, jint hwndDeskTop)
{
    DEBUG_CALL("WinQueryFocus\n")

    return (jint)WinQueryFocus((HWND)hwndDeskTop);
}

JNIEXPORT jboolean JNICALL Java_org_eclipse_swt_internal_pm_OS_WinSetFocus
        (JNIEnv *env, jclass that, jint hwndDeskTop, jint hwndNewFocus)
{
    DEBUG_CALL("WinSetFocus\n")

    return (jboolean)WinSetFocus((HWND)hwndDeskTop, (HWND)hwndNewFocus);
}

JNIEXPORT jint JNICALL Java_org_eclipse_swt_internal_pm_OS_WinQueryWindow
        (JNIEnv *env, jclass that, jint hwnd, jint lCode)
{
    DEBUG_CALL("WinQueryWindow\n")

    return (jint)WinQueryWindow((HWND)hwnd, (LONG)lCode);
}

JNIEXPORT jboolean JNICALL Java_org_eclipse_swt_internal_pm_OS_WinSetParent
        (JNIEnv *env, jclass that, jint hwnd, jint hwndNewParent, jboolean fRedraw)
{
    DEBUG_CALL("WinSetParent\n")

    return (jboolean)WinSetParent((HWND)hwnd, (HWND)hwndNewParent, (BOOL)fRedraw);
}

JNIEXPORT jboolean JNICALL Java_org_eclipse_swt_internal_pm_OS_WinShowWindow
        (JNIEnv *env, jclass that, jint hwnd, jboolean fNewVisibility)
{
    DEBUG_CALL("WinShowWindow\n")

    return (jboolean)WinShowWindow((HWND)hwnd, (BOOL)fNewVisibility);
}

JNIEXPORT jboolean JNICALL Java_org_eclipse_swt_internal_pm_OS_WinEnableWindow
        (JNIEnv *env, jclass that, jint hwnd, jboolean fNewEnabled)
{
    DEBUG_CALL("WinEnableWindow\n")

    return (jboolean)WinEnableWindow((HWND)hwnd, (BOOL)fNewEnabled);
}

JNIEXPORT jint JNICALL Java_org_eclipse_swt_internal_pm_OS_WinWindowFromID
        (JNIEnv *env, jclass that, jint hwndParent, jint id)
{
    DEBUG_CALL("WinWindowFromID\n")

    return (jint)WinWindowFromID((HWND)hwndParent, (ULONG)id);
}

JNIEXPORT jint JNICALL Java_org_eclipse_swt_internal_pm_OS_WinGetPS
        (JNIEnv *env, jclass that, jint hwnd)
{
    DEBUG_CALL("WinGetPS\n")

    return (jint)WinGetPS((HWND)hwnd);
}

JNIEXPORT jint JNICALL Java_org_eclipse_swt_internal_pm_OS_WinGetScreenPS
        (JNIEnv *env, jclass that, jint hwndDeskTop)
{
    DEBUG_CALL("WinGetScreenPS\n")

    return (jint)WinGetScreenPS((HWND)hwndDeskTop);
}

JNIEXPORT jboolean JNICALL Java_org_eclipse_swt_internal_pm_OS_WinReleasePS
        (JNIEnv *env, jclass that, jint hps)
{
    DEBUG_CALL("WinReleasePS\n")

    return (jboolean)WinReleasePS((HPS)hps);
}

JNIEXPORT jint JNICALL Java_org_eclipse_swt_internal_pm_OS_WinBeginPaint
        (JNIEnv *env, jclass that, jint hwnd, jint hps, jobject prclPaint)
{
    DECL_GLOB(pGlob)
    RECTL c_rclPaint, *c_prclPaint = NULL;
    jint rc;

    DEBUG_CALL("WinBeginPaint\n")

    if (prclPaint) c_prclPaint = getRECTLFields(env, prclPaint, &c_rclPaint, &PGLOB(RECTLFc));

    rc = WinBeginPaint((HWND)hwnd, (HPS)hps, c_prclPaint);

    if (prclPaint) setRECTLFields(env, prclPaint, c_prclPaint, &PGLOB(RECTLFc));

    return rc;
}

JNIEXPORT jboolean JNICALL Java_org_eclipse_swt_internal_pm_OS_WinEndPaint
        (JNIEnv *env, jclass that, jint hps)
{
    DEBUG_CALL("WinEndPaint\n")

    return (jboolean)WinEndPaint((HPS)hps);
}

JNIEXPORT jboolean JNICALL Java_org_eclipse_swt_internal_pm_OS_WinInvalidateRect
        (JNIEnv *env, jclass that, jint hwnd, jobject pwrc, jboolean fIncludeChildren)
{
    DECL_GLOB(pGlob)
    RECTL c_wrc, *c_pwrc = NULL;
    jboolean rc;

    DEBUG_CALL("WinInvalidateRect\n")

    if (pwrc) c_pwrc = getRECTLFields(env, pwrc, &c_wrc, &PGLOB(RECTLFc));

    rc = (jboolean)WinInvalidateRect((HWND)hwnd, c_pwrc, fIncludeChildren);

    if (pwrc) setRECTLFields(env, pwrc, c_pwrc, &PGLOB(RECTLFc));

    return rc;
}

JNIEXPORT jboolean JNICALL Java_org_eclipse_swt_internal_pm_OS_WinUpdateWindow
        (JNIEnv *env, jclass that, jint hwnd)
{
    DEBUG_CALL("WinUpdateWindow\n")

    return (jboolean)WinUpdateWindow((HWND)hwnd);
}

JNIEXPORT jint JNICALL Java_org_eclipse_swt_internal_pm_OS_WinScrollWindow
        (JNIEnv *env, jclass that, jint hwnd, jint lDx, jint lDy,
        jobject prclScroll, jobject prclClip, jint hrgnUpdateRgn,
        jobject prclUpdate, jint flOptions)
{
    DECL_GLOB(pGlob)
    RECTL c_rclScroll, *c_prclScroll = NULL;
    RECTL c_rclClip, *c_prclClip = NULL;
    RECTL c_rclUpdate, *c_prclUpdate = NULL;
    jint rc;

    DEBUG_CALL("WinScrollWindow\n")

    if (prclScroll) c_prclScroll = getRECTLFields(env, prclScroll, &c_rclScroll, &PGLOB(RECTLFc));
    if (prclClip) c_prclClip = getRECTLFields(env, prclClip, &c_rclClip, &PGLOB(RECTLFc));
    if (prclUpdate) c_prclUpdate = getRECTLFields(env, prclUpdate, &c_rclUpdate, &PGLOB(RECTLFc));

    rc = (jint)WinScrollWindow(
        (HWND)hwnd, lDx, lDy, c_prclScroll, c_prclClip,
        (HRGN)hrgnUpdateRgn, c_prclUpdate, flOptions
    );

    if (prclUpdate) setRECTLFields(env, prclUpdate, c_prclUpdate, &PGLOB(RECTLFc));
    if (prclClip) setRECTLFields(env, prclClip, c_prclClip, &PGLOB(RECTLFc));
    if (prclScroll) setRECTLFields(env, prclScroll, c_prclScroll, &PGLOB(RECTLFc));

    return rc;
}

JNIEXPORT jboolean JNICALL Java_org_eclipse_swt_internal_pm_OS_WinCreateFrameControls
        (JNIEnv *env, jclass that, jint hwndFrame, jobject pfcdata, jobject pszTitle)
{
    DECL_GLOB(pGlob)
    PSZ c_pszTitle = NULL;
    FRAMECDATA c_fcdata, *c_pfcdata = NULL;
    jboolean rc;

    DEBUG_CALL("WinCreateFrameControls\n")

    if (pszTitle) c_pszTitle = getPSZBytes(env, pszTitle);
    if (pfcdata) c_pfcdata= getFRAMECDATAFields(env, pfcdata, &c_fcdata, &PGLOB(FRAMECDATAFc));

    rc = (jboolean)WinCreateFrameControls((HWND)hwndFrame, c_pfcdata, c_pszTitle);

    if (pfcdata) setFRAMECDATAFields(env, pfcdata, c_pfcdata, &PGLOB(FRAMECDATAFc));
    if (pszTitle) releasePSZBytes(env, pszTitle, c_pszTitle);

    return rc;
}

JNIEXPORT jboolean JNICALL Java_org_eclipse_swt_internal_pm_OS_WinCalcFrameRect
        (JNIEnv *env, jclass that, jint hwndFrame, jobject prcl, jboolean fClient)
{
    DECL_GLOB(pGlob)
    RECTL c_rcl, *c_prcl = NULL;
    jboolean rc;

    DEBUG_CALL("WinCalcFrameRect\n")

    if (prcl) c_prcl = getRECTLFields(env, prcl, &c_rcl, &PGLOB(RECTLFc));

    rc = (jboolean)WinCalcFrameRect((HWND)hwndFrame, c_prcl, fClient);

    if (prcl) setRECTLFields(env, prcl, c_prcl, &PGLOB(RECTLFc));

    return rc;
}

JNIEXPORT jboolean JNICALL Java_org_eclipse_swt_internal_pm_OS_WinFillRect
        (JNIEnv *env, jclass that, jint hps, jobject prcl, jint lColor)
{
    DECL_GLOB(pGlob)
    RECTL c_rcl, *c_prcl = NULL;
    jboolean rc;

    DEBUG_CALL("WinFillRect\n")

    if (prcl) c_prcl = getRECTLFields(env, prcl, &c_rcl, &PGLOB(RECTLFc));

    rc = (jboolean)WinFillRect((HPS)hps, c_prcl, lColor);

    if (prcl) setRECTLFields(env, prcl, c_prcl, &PGLOB(RECTLFc));

    return rc;
}

JNIEXPORT jint JNICALL Java_org_eclipse_swt_internal_pm_OS_WinQueryUpdateRegion
        (JNIEnv *env, jclass that, jint hwnd, jint hrgn)
{
    DEBUG_CALL("WinQueryUpdateRegion\n")

    return (jint)WinQueryUpdateRegion((HWND)hwnd, (HRGN)hrgn);
}

JNIEXPORT jboolean JNICALL Java_org_eclipse_swt_internal_pm_OS_WinInvalidateRegion
        (JNIEnv *env, jclass that, jint hwnd, jint hrgn, jboolean fIncludeChildren)
{
    DEBUG_CALL("WinInvalidateRegion\n")

    return (jboolean)WinInvalidateRegion((HWND)hwnd, (HRGN)hrgn, fIncludeChildren);
}

JNIEXPORT jint JNICALL Java_org_eclipse_swt_internal_pm_OS_WinRealizePalette
        (JNIEnv *env, jclass that, jint hwnd, jint hps, jintArray pcclr)
{
    jint *c_pcclr = NULL;
    jint rc;

    DEBUG_CALL("WinRealizePalette\n")

    if (pcclr) c_pcclr = (*env)->GetIntArrayElements(env, pcclr, NULL);

    rc = (jint)WinRealizePalette((HWND)hwnd, (HPS)hps, (PULONG)c_pcclr);

    if (pcclr) (*env)->ReleaseIntArrayElements(env, pcclr, c_pcclr, 0);

    return rc;
}

JNIEXPORT jint JNICALL Java_org_eclipse_swt_internal_pm_OS_WinGetSysBitmap
        (JNIEnv *env, jclass that, jint hwndDesktop, jint ibm)
{
    DEBUG_CALL("WinGetSysBitmap\n")

    return (jint)WinGetSysBitmap((HWND)hwndDesktop, ibm);
}

JNIEXPORT jboolean JNICALL Java_org_eclipse_swt_internal_pm_OS_WinSetPresParam__III_3I
        (JNIEnv *env, jclass that, jint hwnd, jint idAttrType, jint cbAttrValueLen, jintArray pAttrValue)
{
    jint *c_pAttrValue = NULL;
    jboolean rc;

    DEBUG_CALL("WinSetPresParam\n")

    if (pAttrValue) c_pAttrValue = (*env)->GetIntArrayElements(env, pAttrValue, NULL);

    rc = (jboolean)WinSetPresParam((HWND)hwnd, (ULONG)idAttrType, (ULONG)cbAttrValueLen, (PVOID)c_pAttrValue);

    if (pAttrValue) (*env)->ReleaseIntArrayElements(env, pAttrValue, c_pAttrValue, 0);

    return rc;
}

JNIEXPORT jboolean JNICALL Java_org_eclipse_swt_internal_pm_OS_WinSetPresParam__III_3B
        (JNIEnv *env, jclass that, jint hwnd, jint idAttrType, jint cbAttrValueLen, jbyteArray pAttrValue)
{
    jbyte *c_pAttrValue = NULL;
    jboolean rc;

    DEBUG_CALL("WinSetPresParam\n")

    if (pAttrValue) c_pAttrValue = (*env)->GetByteArrayElements(env, pAttrValue, NULL);

    rc = (jboolean)WinSetPresParam((HWND)hwnd, (ULONG)idAttrType, (ULONG)cbAttrValueLen, (PVOID)c_pAttrValue);

    if (pAttrValue) (*env)->ReleaseByteArrayElements(env, pAttrValue, c_pAttrValue, 0);

    return rc;
}

JNIEXPORT jint JNICALL Java_org_eclipse_swt_internal_pm_OS_WinQueryPresParam
        (JNIEnv *env, jclass that, jint hwnd, jint idAttrType1, jint idAttrType2,
        jintArray pidAttrTypeFound, jint cbAttrValueLen, jbyteArray pAttrValue,
        jint flOptions)
{
    jint *c_pidAttrTypeFound = NULL;
    jbyte *c_pAttrValue = NULL;
    jint rc;

    DEBUG_CALL("WinQueryPresParam\n")

    if (pidAttrTypeFound) c_pidAttrTypeFound = (*env)->GetIntArrayElements(env, pidAttrTypeFound, NULL);
    if (pAttrValue) c_pAttrValue = (*env)->GetByteArrayElements(env, pAttrValue, NULL);

    rc = (jint)WinQueryPresParam((HWND)hwnd, (ULONG)idAttrType1, (ULONG)idAttrType2,
        (PULONG)c_pidAttrTypeFound, (ULONG)cbAttrValueLen, (PVOID)c_pAttrValue, (ULONG)flOptions);

    if (pAttrValue) (*env)->ReleaseByteArrayElements(env, pAttrValue, c_pAttrValue, 0);
    if (pidAttrTypeFound) (*env)->ReleaseIntArrayElements(env, pidAttrTypeFound, c_pidAttrTypeFound, 0);

    return rc;
}

JNIEXPORT jint JNICALL Java_org_eclipse_swt_internal_pm_OS_WinCreatePointerIndirect
        (JNIEnv *env, jclass that, jint hwndDesktop, jobject pptri)
{
    DECL_GLOB(pGlob)
    POINTERINFO c_ptri, *c_pptri = NULL;
    jint rc;

    DEBUG_CALL("WinCreatePointerIndirect\n")

    if (pptri) c_pptri = getPOINTERINFOFields(env, pptri, &c_ptri, &PGLOB(POINTERINFOFc));

    rc = (jint)WinCreatePointerIndirect((HWND)hwndDesktop, c_pptri);

    if (pptri) setPOINTERINFOFields(env, pptri, c_pptri, &PGLOB(POINTERINFOFc));

    return rc;
}

JNIEXPORT jboolean JNICALL Java_org_eclipse_swt_internal_pm_OS_WinDestroyPointer
        (JNIEnv *env, jclass that, jint hptrPointer)
{
    DEBUG_CALL("WinDestroyPointer\n")

    return (jboolean)WinDestroyPointer((HWND)hptrPointer);
}

JNIEXPORT jboolean JNICALL Java_org_eclipse_swt_internal_pm_OS_WinQueryPointerInfo
        (JNIEnv *env, jclass that, jint hptr, jobject pptri)
{
    DECL_GLOB(pGlob)
    POINTERINFO c_ptri, *c_pptri = NULL;
    jboolean rc;

    DEBUG_CALL("WinQueryPointerInfo\n")

    if (pptri) c_pptri = getPOINTERINFOFields(env, pptri, &c_ptri, &PGLOB(POINTERINFOFc));

    rc = (jboolean)WinQueryPointerInfo((HPOINTER)hptr, c_pptri);

    if (pptri) setPOINTERINFOFields(env, pptri, c_pptri, &PGLOB(POINTERINFOFc));

    return rc;
}

JNIEXPORT jboolean JNICALL Java_org_eclipse_swt_internal_pm_OS_WinDrawPointer
        (JNIEnv *env, jclass that, jint hps, jint lx, jint ly, jint hptrPointer,
        jint ulHalftone)
{
    DEBUG_CALL("WinDrawPointer\n")

    return (jboolean)WinDrawPointer((HWND)hps, lx, ly, (HPOINTER)hptrPointer, ulHalftone);
}

JNIEXPORT jint JNICALL Java_org_eclipse_swt_internal_pm_OS_WinDrawText
/*@@TODO (dmik): Unicode*/     
/*        (JNIEnv *env, jclass that, jint hps, jint cchText, jcharArray lpchText, */
        (JNIEnv *env, jclass that, jint hps, jint cchText, jbyteArray lpchText,
        jobject prcl, jint clrFore, jint clrBack, jint flCmd)
{
    DECL_GLOB(pGlob)
/*@@TODO (dmik): Unicode*/     
/*    jchar *c_lpchText = NULL;*/
    jbyte *c_lpchText = NULL;
    RECTL c_rcl, *c_prcl = NULL;
    jint rc;

    DEBUG_CALL("WinDrawText\n")

/*@@TODO (dmik): Unicode*/     
/*    if (lpchText) c_lpchText = (*env)->GetCharArrayElements(env, lpchText, NULL);*/
    if (lpchText) c_lpchText = (*env)->GetByteArrayElements(env, lpchText, NULL);
    if (prcl) c_prcl = getRECTLFields(env, prcl, &c_rcl, &PGLOB(RECTLFc));

    rc = (jint)WinDrawText((HPS)hps, (LONG)cchText, (PCH)c_lpchText,
        c_prcl, (LONG)clrFore, (LONG)clrBack, (LONG)flCmd); 

    if (prcl) setRECTLFields(env, prcl, c_prcl, &PGLOB(RECTLFc));
/*@@TODO (dmik): Unicode*/     
/*    if (lpchText) (*env)->ReleaseCharArrayElements(env, lpchText, c_lpchText, 0);*/
    if (lpchText) (*env)->ReleaseByteArrayElements(env, lpchText, c_lpchText, 0);

    return rc;
}

JNIEXPORT jint JNICALL Java_org_eclipse_swt_internal_pm_OS_WinGetKeyState
        (JNIEnv *env, jclass that, jint hwndDesktop, jint vkey)
{
    DEBUG_CALL("WinGetKeyState\n")

    return (jint)WinGetKeyState((HWND)hwndDesktop, vkey);
}

JNIEXPORT jint JNICALL Java_org_eclipse_swt_internal_pm_OS_WinMessageBox
    (JNIEnv *env, jclass that, jint hwndParent, jint hwndOwner,
    jobject pszMessage, jobject pszTitle, jint ulWinId, jint flStyle)
{
    PSZ c_pszMessage = NULL;
    PSZ c_pszTitle = NULL;
    jint rc;
    
    DEBUG_CALL("WinMessageBox");
    
    if(pszMessage) c_pszMessage = getPSZBytes(env, pszMessage);
    if(pszTitle) c_pszTitle = getPSZBytes(env, pszTitle);
    
    rc = (jint)WinMessageBox((HWND)hwndParent, (HWND)hwndOwner,        
        c_pszMessage, c_pszTitle, (ULONG)ulWinId, (ULONG)flStyle);
    
    if(pszTitle) releasePSZBytes(env, pszTitle, c_pszTitle);
    if(pszMessage) releasePSZBytes(env, pszMessage, c_pszMessage);
    
    return rc;
}

JNIEXPORT jint JNICALL Java_org_eclipse_swt_internal_pm_OS_WinFontDlg
        (JNIEnv *env, jclass that, jint hwndParent, jint hwndOwner, jobject pfntdlg)
{
    DECL_GLOB(pGlob)
    FONTDLG c_pfntdlg, *c_ppfntdlg = NULL;
    jint rc;

    DEBUG_CALL("WinFontDlg\n")
    if (pfntdlg)
	    c_ppfntdlg = getFONTDLGFields(env, pfntdlg, &c_pfntdlg, &PGLOB(FONTDLGFc));
    rc = (jint)WinFontDlg((HWND)hwndParent, (HWND)hwndOwner, c_ppfntdlg);
    if (pfntdlg)
	    setFONTDLGFields(env, pfntdlg, c_ppfntdlg, &PGLOB(FONTDLGFc));
    return rc;
}

JNIEXPORT jint JNICALL Java_org_eclipse_swt_internal_pm_OS_WinEditColorDlg
        (JNIEnv *env, jclass that, jint hwndParent, jint hwndOwner, jint color, jobject pszTitle)
{
    PSZ c_pszTitle = NULL;
    jint rc;
    if (pszTitle) c_pszTitle = getPSZBytes(env, pszTitle);
    rc = (jint)WinEditColorDlg((HWND)hwndParent, (HWND)hwndOwner, (ULONG)color, c_pszTitle);
    if (pszTitle) releasePSZBytes(env, pszTitle, c_pszTitle);

    return rc;
}

JNIEXPORT jboolean JNICALL Java_org_eclipse_swt_internal_pm_OS_WinFileDlg
    (JNIEnv *env, jclass that, jint hwndParent, jint hwndOwner, jobject pfild)
{
    DECL_GLOB(pGlob)
    FILEDLG c_pfild, *c_ppfild = NULL;
    jboolean rc;
    jboolean rcWf;
    DEBUG_CALL("WinFileDlg\n")

    if (pfild) c_ppfild = getFILEDLGFields(env, pfild, &c_pfild, &PGLOB(FILEDLGFc));

    rc = (jboolean)WinFileDlg((HWND)hwndParent, (HWND)hwndOwner, c_ppfild);

    if (pfild) setFILEDLGFields(env, pfild, c_ppfild, &PGLOB(FILEDLGFc));

    if(c_ppfild->papszFQFilename){
        rcWf = (jboolean)WinFreeFileDlgList(c_ppfild->papszFQFilename);
    }
    return rc;
}

JNIEXPORT jboolean JNICALL Java_org_eclipse_swt_internal_pm_OS_WinFreeFileDlgList
    (JNIEnv *env, jclass that, jobjectArray papszFQFilename)
{
    PAPSZ c_pppapszFQFilename = NULL;
    jobjectArray c_papszFQFilenameList;
    jboolean rc;

    if (papszFQFilename) {
        c_pppapszFQFilename = getPAPSZArray(env, papszFQFilename);;
    }

    DEBUG_CALL("WinFreeFileDlgList\n")

    rc = (jboolean)WinFreeFileDlgList(c_pppapszFQFilename);
    if (papszFQFilename)
        releasePAPSZArray(env, c_papszFQFilenameList, c_pppapszFQFilename);

    return rc;
}
JNIEXPORT jint JNICALL Java_org_eclipse_swt_internal_pm_OS_WinOpenWindowDC
        (JNIEnv *env, jclass that, jint hwnd)
{
    DEBUG_CALL("WinOpenWindowDC\n")

    return (jint)WinOpenWindowDC((HWND)hwnd);
}
JNIEXPORT jboolean JNICALL Java_org_eclipse_swt_internal_pm_OS_WinPopupMenu
        (JNIEnv *env, jclass that, jint hwndParent, jint hwndOwner, jint hwndMenu, jint x, jint y, jint idItem, jshort fs)
{
    DEBUG_CALL("WinPopupMenu\n")

    return (jboolean)WinPopupMenu((HWND)hwndParent, (HWND)hwndOwner, (HWND)hwndMenu, x, y, idItem, fs);
}
JNIEXPORT jint JNICALL Java_org_eclipse_swt_internal_pm_OS_WinCreateAccelTable
        (JNIEnv *env, jclass that, jint hab, jobject pacctAccelTable)
{
    DEBUG_CALL("WinCreateAccelTable\n")
    jint haccelhAccel;
    ACCELTABLE c_pacctAccelTable, *c_ppacctAccelTable = NULL;

    if (pacctAccelTable) c_ppacctAccelTable = getACCELTABLEFields(env, pacctAccelTable, &c_pacctAccelTable, &PGLOB(ACCELTABLEFc));

    haccelhAccel = (jint)WinCreateAccelTable((HAB)hab, c_ppacctAccelTable);

    if (pacctAccelTable) setACCELTABLEFields(env, pacctAccelTable, c_ppacctAccelTable, &PGLOB(ACCELTABLEFc));

    return haccelhAccel;
}
JNIEXPORT jboolean JNICALL Java_org_eclipse_swt_internal_pm_OS_WinDestroyAccelTable
        (JNIEnv *env, jclass that, jint haccelAccel)
{
    DEBUG_CALL("WinDestroyAccelTable\n")

    return (jboolean)WinDestroyAccelTable((HACCEL)haccelAccel);
}
JNIEXPORT jboolean JNICALL Java_org_eclipse_swt_internal_pm_OS_WinSetAccelTable
        (JNIEnv *env, jclass that, jint hab, jint haccelAccel, jint hwndFrame)
{
    DEBUG_CALL("WinSetAccelTable\n")

    return (jboolean)WinSetAccelTable((HAB)hab, (HACCEL)haccelAccel, (HWND)hwndFrame);
}
JNIEXPORT jboolean JNICALL Java_org_eclipse_swt_internal_pm_OS_WinTranslateAccel
        (JNIEnv *env, jclass that, jint hab, jint hwnd, jint haccelAccel, jobject qmsg)
{
    QMSG c_qmsg, *c_pqmsg = NULL;
    jboolean rc;
    DEBUG_CALL("WinTranslateAccel\n")

    if (qmsg) c_pqmsg = getQMSGFields(env, qmsg, &c_qmsg, &PGLOB(QMSGFc));

    rc = (jboolean)WinTranslateAccel((HAB)hab, (HWND)hwnd, (HACCEL)haccelAccel, c_pqmsg);

    if (qmsg) setQMSGFields(env, qmsg, c_pqmsg, &PGLOB(QMSGFc));

    return rc;
}


JNIEXPORT jboolean JNICALL Java_org_eclipse_swt_internal_pm_OS_PrfQueryProfileSize
        (JNIEnv *env, jclass that, jint hini, jobject pszApp, jobject pszKey,
        jintArray pulDataLen)
{
    PSZ c_pszApp = NULL;
    PSZ c_pszKey = NULL;
    jint *c_pulDataLen = NULL;
    jboolean rc;

    DEBUG_CALL("PrfQueryProfileSize\n")

    if (pszApp) c_pszApp = getPSZBytes(env, pszApp);
    if (pszKey) c_pszKey = getPSZBytes(env, pszKey);
    if (pulDataLen) c_pulDataLen = (*env)->GetIntArrayElements(env, pulDataLen, NULL);

    rc = (jboolean)PrfQueryProfileSize((HINI)hini, c_pszApp, c_pszKey, (PULONG)c_pulDataLen);

    if (pulDataLen) (*env)->ReleaseIntArrayElements(env, pulDataLen, c_pulDataLen, 0);
    if (pszKey) releasePSZBytes(env, pszKey, c_pszKey);
    if (pszApp) releasePSZBytes(env, pszApp, c_pszApp);

    return rc;
}

JNIEXPORT jint JNICALL Java_org_eclipse_swt_internal_pm_OS_PrfQueryProfileString
        (JNIEnv *env, jclass that, jint hini, jobject pszApp, jobject pszKey,
        jobject pszDefault, jbyteArray pBuffer, jint ulBufferMax)
{
    PSZ c_pszApp = NULL;
    PSZ c_pszKey = NULL;
    PSZ c_pszDefault = NULL;
    jbyte *c_pBuffer = NULL;
    jint rc;

    DEBUG_CALL("PrfQueryProfileString\n")

    if (pszApp) c_pszApp = getPSZBytes(env, pszApp);
    if (pszKey) c_pszKey = getPSZBytes(env, pszKey);
    if (pszDefault) c_pszDefault = getPSZBytes(env, pszDefault);
    if (pBuffer) c_pBuffer = (*env)->GetByteArrayElements(env, pBuffer, NULL);

    rc = (jint)PrfQueryProfileString((HINI)hini, c_pszApp, c_pszKey, c_pszDefault,
        (PVOID)c_pBuffer, (ULONG)ulBufferMax);

    if (pBuffer) (*env)->ReleaseByteArrayElements(env, pBuffer, c_pBuffer, 0);
    if (pszDefault) releasePSZBytes(env, pszDefault, c_pszDefault);
    if (pszKey) releasePSZBytes(env, pszKey, c_pszKey);
    if (pszApp) releasePSZBytes(env, pszApp, c_pszApp);

    return rc;
}
    
/* PM helpers */

JNIEXPORT jint JNICALL Java_org_eclipse_swt_internal_pm_OS_WinCallWindowProc
        (JNIEnv *env, jclass that, jint pWindowProc, jint hwnd, jint ulMsgid,
        jint mpParam1, jint mpParam2)
{
    DEBUG_CALL("WinCallWindowProc\n")

    return (jint)((PFNWP)pWindowProc)((HWND)hwnd, ulMsgid, (MPARAM)mpParam1, (MPARAM)mpParam2);
}

/* Base functions */

JNIEXPORT jint JNICALL Java_org_eclipse_swt_internal_pm_OS_DosGetInfoBlocks
        (JNIEnv *env, jclass that, jintArray ptib, jintArray ppib)
{
    jint *c_ptib = NULL;
    jint *c_ppib = NULL;
    jint rc;

    DEBUG_CALL("DosGetInfoBlocks\n")

    if (ptib) c_ptib = (*env)->GetIntArrayElements(env, ptib, NULL);
    if (ppib) c_ppib = (*env)->GetIntArrayElements(env, ppib, NULL);

    rc = (jint)DosGetInfoBlocks( (PTIB *)c_ptib, (PPIB *)c_ppib );

    if (ppib) (*env)->ReleaseIntArrayElements(env, ppib, c_ppib, 0);
    if (ptib) (*env)->ReleaseIntArrayElements(env, ptib, c_ptib, 0);

    return rc;
}

JNIEXPORT jint JNICALL Java_org_eclipse_swt_internal_pm_OS_DosQuerySysInfo
        (JNIEnv *env, jclass that, jint iStart, jint iLast, jintArray pBuf, jint cbSize)
{
    jint *c_pBuf = NULL;
    jint rc;

    DEBUG_CALL("DosQuerySysInfo\n")

    if (pBuf) c_pBuf = (*env)->GetIntArrayElements(env, pBuf, NULL);

    rc = DosQuerySysInfo( iStart, iLast, c_pBuf, cbSize );

    if (pBuf) (*env)->ReleaseIntArrayElements(env, pBuf, c_pBuf, 0);

    return rc;
}

/* GPI functions */

JNIEXPORT jint JNICALL Java_org_eclipse_swt_internal_pm_OS_GpiCreatePS
        (JNIEnv *env, jclass that, jint hab, jint hdc, jintArray psizlSize, jint flOptions)
{
    jint *c_psizlSize = NULL;
    jint rc;

    DEBUG_CALL("GpiCreatePS\n")

    if (psizlSize) c_psizlSize = (*env)->GetIntArrayElements(env, psizlSize, NULL);

    rc = (jint)GpiCreatePS((HAB)hab, (HDC)hdc, (PSIZEL)c_psizlSize, flOptions);

    if (psizlSize) (*env)->ReleaseIntArrayElements(env, psizlSize, c_psizlSize, 0);

    return rc;
}

JNIEXPORT jboolean JNICALL Java_org_eclipse_swt_internal_pm_OS_GpiDestroyPS
        (JNIEnv *env, jclass that, jint hps)
{
    DEBUG_CALL("GpiDestroyPS\n")

    return (jboolean)GpiDestroyPS((HPS)hps);
}

JNIEXPORT jint JNICALL Java_org_eclipse_swt_internal_pm_OS_GpiQueryDevice
        (JNIEnv *env, jclass that, jint hps)
{
    DEBUG_CALL("GpiQueryDevice\n")

    return (jint)GpiQueryDevice((HPS)hps);
}

JNIEXPORT jboolean JNICALL Java_org_eclipse_swt_internal_pm_OS_GpiQueryDeviceBitmapFormats
        (JNIEnv *env, jclass that, jint hps, jint lCount, jintArray alArray)
{
    jint *c_alArray = NULL;
    jboolean rc;

    DEBUG_CALL("GpiQueryDeviceBitmapFormats\n")

    if (alArray) c_alArray = (*env)->GetIntArrayElements(env, alArray, NULL);

    rc = (jboolean)GpiQueryDeviceBitmapFormats((HPS)hps, lCount, (PLONG)c_alArray);

    if (alArray) (*env)->ReleaseIntArrayElements(env, alArray, c_alArray, 0);

    return rc;
}

JNIEXPORT jint JNICALL Java_org_eclipse_swt_internal_pm_OS_GpiCreatePalette
        (JNIEnv *env, jclass that, jint hab, jint flOptions, jint ulFormat, jint ulCount,
        jintArray aulTable)
{
    jint *c_aulTable = NULL;
    jint rc;

    DEBUG_CALL("GpiCreatePalette\n")

    if (aulTable) c_aulTable= (*env)->GetIntArrayElements(env, aulTable, NULL);

    rc = (jint)GpiCreatePalette((HAB)hab, flOptions, ulFormat, ulCount, (PULONG)c_aulTable);

    if (aulTable) (*env)->ReleaseIntArrayElements(env, aulTable, c_aulTable, 0);

    return rc;
}

JNIEXPORT jboolean JNICALL Java_org_eclipse_swt_internal_pm_OS_GpiDeletePalette
        (JNIEnv *env, jclass that, jint hpal)
{
    DEBUG_CALL("GpiDeletePalette\n")

    return (jboolean)GpiDeletePalette((HPAL)hpal);
}

JNIEXPORT jint JNICALL Java_org_eclipse_swt_internal_pm_OS_GpiQueryNearestPaletteIndex
        (JNIEnv *env, jclass that, jint hpal, jint color)
{
    DEBUG_CALL("GpiQueryNearestPaletteIndex\n")

    return (jint)GpiQueryNearestPaletteIndex((HPAL)hpal, color);
}

JNIEXPORT jint JNICALL Java_org_eclipse_swt_internal_pm_OS_GpiQueryPaletteInfo
        (JNIEnv *env, jclass that, jint hpal, jint hps, jint flOptions, jint ulStart,
        jint ulCount, jintArray aulArray)
{
    jint *c_aulArray = NULL;
    jint rc;

    DEBUG_CALL("GpiQueryPaletteInfo\n")

    if (aulArray) c_aulArray = (*env)->GetIntArrayElements(env, aulArray, NULL);

    rc = (jint)GpiQueryPaletteInfo((HPAL)hpal, (HPS)hps, flOptions, ulStart, ulCount, (PULONG)c_aulArray);

    if (aulArray) (*env)->ReleaseIntArrayElements(env, aulArray, c_aulArray, 0);

    return rc;
}

JNIEXPORT jboolean JNICALL Java_org_eclipse_swt_internal_pm_OS_GpiSetPaletteEntries
        (JNIEnv *env, jclass that, jint hpal, jint ulFormat, jint ulStart, jint ulCount,
        jintArray aulTable)
{
    jint *c_aulTable = NULL;
    jboolean rc;

    DEBUG_CALL("GpiSetPaletteEntries\n")

    if (aulTable) c_aulTable= (*env)->GetIntArrayElements(env, aulTable, NULL);

    rc = (jboolean)GpiSetPaletteEntries((HPAL)hpal, ulFormat, ulStart, ulCount, (PULONG)c_aulTable);

    if (aulTable) (*env)->ReleaseIntArrayElements(env, aulTable, c_aulTable, 0);

    return rc;
}

JNIEXPORT jint JNICALL Java_org_eclipse_swt_internal_pm_OS_GpiQueryPalette
        (JNIEnv *env, jclass that, jint hps)
{
    DEBUG_CALL("GpiQueryPalette\n")

    return (jint)GpiQueryPalette((HPS)hps);
}

JNIEXPORT jint JNICALL Java_org_eclipse_swt_internal_pm_OS_GpiSelectPalette
        (JNIEnv *env, jclass that, jint hps, jint hpal)
{
    DEBUG_CALL("GpiSelectPalette\n")

    return (jint)GpiSelectPalette((HPS)hps, (HPAL)hpal);
}

JNIEXPORT jboolean JNICALL Java_org_eclipse_swt_internal_pm_OS_GpiCreateLogColorTable
        (JNIEnv *env, jclass that, jint hps, jint flOptions, jint lFormat, jint lStart, jint lCount,
        jintArray alTable)
{
    jint *c_alTable = NULL;
    jboolean rc;

    DEBUG_CALL("GpiCreateLogColorTable\n")

    if (alTable) c_alTable= (*env)->GetIntArrayElements(env, alTable, NULL);

    rc = (jboolean)GpiCreateLogColorTable((HPS)hps, flOptions, lFormat, lStart, lCount, (PLONG)c_alTable);

    if (alTable) (*env)->ReleaseIntArrayElements(env, alTable, c_alTable, 0);

    return rc;
}

JNIEXPORT jint JNICALL Java_org_eclipse_swt_internal_pm_OS_GpiQueryColor
        (JNIEnv *env, jclass that, jint hps)
{
    DEBUG_CALL("GpiQueryColor\n")

    return (jint)GpiQueryColor((HPS)hps);
}

JNIEXPORT jboolean JNICALL Java_org_eclipse_swt_internal_pm_OS_GpiSetColor
        (JNIEnv *env, jclass that, jint hps, jint lColor)
{
    DEBUG_CALL("GpiSetColor\n")

    return (jboolean)GpiSetColor((HPS)hps, lColor);
}

JNIEXPORT jint JNICALL Java_org_eclipse_swt_internal_pm_OS_GpiQueryBackColor
        (JNIEnv *env, jclass that, jint hps)
{
    DEBUG_CALL("GpiQueryBackColor\n")

    return (jint)GpiQueryBackColor((HPS)hps);
}

JNIEXPORT jboolean JNICALL Java_org_eclipse_swt_internal_pm_OS_GpiSetBackColor
        (JNIEnv *env, jclass that, jint hps, jint lColor)
{
    DEBUG_CALL("GpiSetBackColor\n")

    return (jboolean)GpiSetBackColor((HPS)hps, lColor);
}

JNIEXPORT jint JNICALL Java_org_eclipse_swt_internal_pm_OS_GpiQueryMix
        (JNIEnv *env, jclass that, jint hps)
{
    DEBUG_CALL("GpiQueryMix\n")

    return (jint)GpiQueryMix((HPS)hps);
}

JNIEXPORT jboolean JNICALL Java_org_eclipse_swt_internal_pm_OS_GpiSetMix
        (JNIEnv *env, jclass that, jint hps, jint lMixMode)
{
    DEBUG_CALL("GpiSetMix\n")

    return (jboolean)GpiSetMix((HPS)hps, lMixMode);
}

JNIEXPORT jint JNICALL Java_org_eclipse_swt_internal_pm_OS_GpiQueryBackMix
        (JNIEnv *env, jclass that, jint hps)
{
    DEBUG_CALL("GpiQueryBackMix\n")

    return (jint)GpiQueryBackMix((HPS)hps);
}

JNIEXPORT jboolean JNICALL Java_org_eclipse_swt_internal_pm_OS_GpiSetBackMix
        (JNIEnv *env, jclass that, jint hps, jint lMixMode)
{
    DEBUG_CALL("GpiSetBackMix\n")

    return (jboolean)GpiSetBackMix((HPS)hps, lMixMode);
}

JNIEXPORT jint JNICALL Java_org_eclipse_swt_internal_pm_OS_GpiQueryPattern
        (JNIEnv *env, jclass that, jint hps)
{
    DEBUG_CALL("GpiQueryPattern\n")

    return (jint)GpiQueryPattern((HPS)hps);
}

JNIEXPORT jboolean JNICALL Java_org_eclipse_swt_internal_pm_OS_GpiSetPattern
        (JNIEnv *env, jclass that, jint hps, jint lPatternSymbol)
{
    DEBUG_CALL("GpiSetPattern\n")

    return (jboolean)GpiSetPattern((HPS)hps, lPatternSymbol);
}

JNIEXPORT jint JNICALL Java_org_eclipse_swt_internal_pm_OS_GpiQueryLineType
        (JNIEnv *env, jclass that, jint hps)
{
    DEBUG_CALL("GpiQueryLineType\n")

    return (jint)GpiQueryLineType((HPS)hps);
}

JNIEXPORT jboolean JNICALL Java_org_eclipse_swt_internal_pm_OS_GpiSetLineType
        (JNIEnv *env, jclass that, jint hps, jint lLineType)
{
    DEBUG_CALL("GpiSetLineType\n")

    return (jboolean)GpiSetLineType((HPS)hps, lLineType);
}

/*
JNIEXPORT jboolean JNICALL Java_org_eclipse_swt_internal_pm_OS_GpiSetLineWidth
        (JNIEnv *env, jclass that, jint hps, jint fxLineWidth)
{
    DEBUG_CALL("GpiSetLineWidth\n")

    return (jboolean)GpiSetLineWidth((HPS)hps, (FIXED)fxLineWidth);
}
*/

JNIEXPORT jint JNICALL Java_org_eclipse_swt_internal_pm_OS_GpiQueryClipBox
        (JNIEnv *env, jclass that, jint hps, jobject prclBound)
{
    DECL_GLOB(pGlob)
    RECTL c_rclBound, *c_prclBound = NULL;
    jint rc;

    DEBUG_CALL("GpiQueryClipBox\n")

    if (prclBound) c_prclBound = getRECTLFields(env, prclBound, &c_rclBound, &PGLOB(RECTLFc));

    rc = (jint)GpiQueryClipBox((HPS)hps, c_prclBound);

    if (prclBound) setRECTLFields(env, prclBound, c_prclBound, &PGLOB(RECTLFc));

    return rc;
}

JNIEXPORT jint JNICALL Java_org_eclipse_swt_internal_pm_OS_GpiQueryClipRegion
        (JNIEnv *env, jclass that, jint hps)
{
    DEBUG_CALL("GpiQueryClipRegion\n")

    return (jint)GpiQueryClipRegion((HPS)hps);
}

JNIEXPORT jint JNICALL Java_org_eclipse_swt_internal_pm_OS_GpiSetClipRegion
        (JNIEnv *env, jclass that, jint hps, jint hrgn, jintArray phrgnOld)
{
    jint *c_phrgnOld = NULL;
    jint rc;

    DEBUG_CALL("GpiSetClipRegion\n")

    if (phrgnOld) c_phrgnOld = (*env)->GetIntArrayElements(env, phrgnOld, NULL);

    rc = (jint)GpiSetClipRegion((HPS)hps, (HRGN)hrgn, (PHRGN)c_phrgnOld);

    if (phrgnOld) (*env)->ReleaseIntArrayElements(env, phrgnOld, c_phrgnOld, 0);

    return rc;
}

JNIEXPORT jint JNICALL Java_org_eclipse_swt_internal_pm_OS_GpiCreateRegion
        (JNIEnv *env, jclass that, jint hps, jint lCount, jintArray arclRectangles)
{
    jint *c_arclRectangles = NULL;
    jint rc;

    DEBUG_CALL("GpiCreateRegion\n")

    if (arclRectangles) c_arclRectangles = (*env)->GetIntArrayElements(env, arclRectangles, NULL);

    rc = (jint)GpiCreateRegion((HPS)hps, lCount, (PRECTL)c_arclRectangles);

    if (arclRectangles) (*env)->ReleaseIntArrayElements(env, arclRectangles, c_arclRectangles, 0);

    return rc;
}

JNIEXPORT jboolean JNICALL Java_org_eclipse_swt_internal_pm_OS_GpiSetRegion
        (JNIEnv *env, jclass that, jint hps, jint hrgn, jint lCount, jintArray arclRectangles)
{
    jint *c_arclRectangles = NULL;
    jboolean rc;

    DEBUG_CALL("GpiSetRegion\n")

    if (arclRectangles) c_arclRectangles = (*env)->GetIntArrayElements(env, arclRectangles, NULL);

    rc = (jboolean)GpiSetRegion((HPS)hps, (HRGN)hrgn, lCount, (PRECTL)c_arclRectangles);

    if (arclRectangles) (*env)->ReleaseIntArrayElements(env, arclRectangles, c_arclRectangles, 0);

    return rc;
}

JNIEXPORT jboolean JNICALL Java_org_eclipse_swt_internal_pm_OS_GpiDestroyRegion
        (JNIEnv *env, jclass that, jint hps, jint hrgn)
{
    DEBUG_CALL("GpiDestroyRegion\n")

    return (jboolean)GpiDestroyRegion((HPS)hps, (HRGN)hrgn);
}

JNIEXPORT jboolean JNICALL Java_org_eclipse_swt_internal_pm_OS_GpiQueryRegionRects
        (JNIEnv *env, jclass that, jint hps, jint hrgn, jobject prclBound, jobject prgnrcControl,
        jintArray prclRect)
{
    DECL_GLOB(pGlob)
    RGNRECT c_rgnrcControl, *c_prgnrcControl = NULL;
    RECTL c_rclBound, *c_prclBound = NULL;
    jint *c_prclRect = NULL;
    jboolean rc;

    DEBUG_CALL("GpiQueryRegionRects\n")

    if (prgnrcControl) c_prgnrcControl = getRGNRECTFields(env, prgnrcControl, &c_rgnrcControl, &PGLOB(RGNRECTFc));
    if (prclBound) c_prclBound = getRECTLFields(env, prclBound, &c_rclBound, &PGLOB(RECTLFc));
    if (prclRect) c_prclRect = (*env)->GetIntArrayElements(env, prclRect, NULL);

    rc = (jboolean)GpiQueryRegionRects((HPS)hps, (HRGN)hrgn, c_prclBound, c_prgnrcControl, (PRECTL)c_prclRect);

    if (prclRect) (*env)->ReleaseIntArrayElements(env, prclRect, c_prclRect, 0);
    if (prclBound) setRECTLFields(env, prclBound, c_prclBound, &PGLOB(RECTLFc));
    if (prgnrcControl) setRGNRECTFields(env, prgnrcControl, c_prgnrcControl, &PGLOB(RGNRECTFc));

    return rc;
}

JNIEXPORT jint JNICALL Java_org_eclipse_swt_internal_pm_OS_GpiQueryRegionBox
        (JNIEnv *env, jclass that, jint hps, jint hrgn, jobject prclBound)
{
    DECL_GLOB(pGlob)
    RECTL c_rclBound, *c_prclBound = NULL;
    jint rc;

    DEBUG_CALL("GpiQueryRegionBox\n")

    if (prclBound) c_prclBound = getRECTLFields(env, prclBound, &c_rclBound, &PGLOB(RECTLFc));

    rc = (jint)GpiQueryRegionBox((HPS)hps, (HRGN)hrgn, c_prclBound);

    if (prclBound) setRECTLFields(env, prclBound, c_prclBound, &PGLOB(RECTLFc));

    return rc;
}

JNIEXPORT jint JNICALL Java_org_eclipse_swt_internal_pm_OS_GpiCombineRegion
        (JNIEnv *env, jclass that, jint hps, jint hrgnDest, jint hrgnSrc1, jint hrgnSrc2, jint lMode)
{
    DEBUG_CALL("GpiCombineRegion\n")

    return (jint)GpiCombineRegion((HPS)hps, (HRGN)hrgnDest, (HRGN)hrgnSrc1, (HRGN)hrgnSrc2, lMode);
}

JNIEXPORT jint JNICALL Java_org_eclipse_swt_internal_pm_OS_GpiPtInRegion
        (JNIEnv *env, jclass that, jint hps, jint hrgn, jintArray pptlPoint)
{
    jint *c_pptlPoint = NULL;
    jint rc;

    DEBUG_CALL("GpiPtInRegion\n")

    if (pptlPoint) c_pptlPoint = (*env)->GetIntArrayElements(env, pptlPoint, NULL);

    rc = (jint)GpiPtInRegion((HPS)hps, (HRGN)hrgn, (PPOINTL)c_pptlPoint);

    if (pptlPoint) (*env)->ReleaseIntArrayElements(env, pptlPoint, c_pptlPoint, 0);

    return rc;
}

JNIEXPORT jint JNICALL Java_org_eclipse_swt_internal_pm_OS_GpiRectInRegion
        (JNIEnv *env, jclass that, jint hps, jint hrgn, jobject prclRect)
{
    DECL_GLOB(pGlob)
    RECTL c_rclRect, *c_prclRect = NULL;
    jint rc;

    DEBUG_CALL("GpiRectInRegion\n")

    if (prclRect) c_prclRect = getRECTLFields(env, prclRect, &c_rclRect, &PGLOB(RECTLFc));

    rc = (jint)GpiRectInRegion((HPS)hps, (HRGN)hrgn, c_prclRect);

    if (prclRect) setRECTLFields(env, prclRect, c_prclRect, &PGLOB(RECTLFc));

    return rc;
}

JNIEXPORT jboolean JNICALL Java_org_eclipse_swt_internal_pm_OS_GpiQueryCurrentPosition
        (JNIEnv *env, jclass that, jint hps, jintArray pptlPoint)
{
    jint *c_pptlPoint = NULL;
    jboolean rc;

    DEBUG_CALL("GpiQueryCurrentPosition\n")

    if (pptlPoint) c_pptlPoint = (*env)->GetIntArrayElements(env, pptlPoint, NULL);

    rc = (jboolean)GpiQueryCurrentPosition((HPS)hps, (PPOINTL)c_pptlPoint);

    if (pptlPoint) (*env)->ReleaseIntArrayElements(env, pptlPoint, c_pptlPoint, 0);

    return rc;
}

JNIEXPORT jint JNICALL Java_org_eclipse_swt_internal_pm_OS_GpiSetPel
        (JNIEnv *env, jclass that, jint hps, jintArray pptlPoint)
{
    jint *c_pptlPoint = NULL;
    jint rc;

    DEBUG_CALL("GpiSetPel\n")

    if (pptlPoint) c_pptlPoint = (*env)->GetIntArrayElements(env, pptlPoint, NULL);

    rc = (jint)GpiSetPel((HPS)hps, (PPOINTL)c_pptlPoint);

    if (pptlPoint) (*env)->ReleaseIntArrayElements(env, pptlPoint, c_pptlPoint, 0);

    return rc;
}

JNIEXPORT jboolean JNICALL Java_org_eclipse_swt_internal_pm_OS_GpiMove
        (JNIEnv *env, jclass that, jint hps, jintArray pptlPoint)
{
    jint *c_pptlPoint = NULL;
    jboolean rc;

    DEBUG_CALL("GpiMove\n")

    if (pptlPoint) c_pptlPoint = (*env)->GetIntArrayElements(env, pptlPoint, NULL);

    rc = (jboolean)GpiMove((HPS)hps, (PPOINTL)c_pptlPoint);

    if (pptlPoint) (*env)->ReleaseIntArrayElements(env, pptlPoint, c_pptlPoint, 0);

    return rc;
}

JNIEXPORT jint JNICALL Java_org_eclipse_swt_internal_pm_OS_GpiLine
        (JNIEnv *env, jclass that, jint hps, jintArray pptlEndPoint)
{
    jint *c_pptlEndPoint = NULL;
    jint rc;

    DEBUG_CALL("GpiLine\n")

    if (pptlEndPoint) c_pptlEndPoint = (*env)->GetIntArrayElements(env, pptlEndPoint, NULL);

    rc = (jint)GpiLine((HPS)hps, (PPOINTL)c_pptlEndPoint);

    if (pptlEndPoint) (*env)->ReleaseIntArrayElements(env, pptlEndPoint, c_pptlEndPoint, 0);

    return rc;
}

JNIEXPORT jint JNICALL Java_org_eclipse_swt_internal_pm_OS_GpiBox
        (JNIEnv *env, jclass that, jint hps, jint lControl, jintArray pptlPoint, jint lHRound, jint lVRound)
{
    jint *c_pptlPoint = NULL;
    jint rc;

    DEBUG_CALL("GpiBox\n")

    if (pptlPoint) c_pptlPoint = (*env)->GetIntArrayElements(env, pptlPoint, NULL);

    rc = (jint)GpiBox((HPS)hps, lControl, (PPOINTL)c_pptlPoint, lHRound, lVRound);

    if (pptlPoint) (*env)->ReleaseIntArrayElements(env, pptlPoint, c_pptlPoint, 0);

    return rc;
}

JNIEXPORT jboolean JNICALL Java_org_eclipse_swt_internal_pm_OS_GpiSetArcParams
        (JNIEnv *env, jclass that, jint hps, jintArray parcpArcParams)
{
    jint *c_parcpArcParams = NULL;
    jboolean rc;

    DEBUG_CALL("GpiSetArcParams\n")

    if (parcpArcParams) c_parcpArcParams= (*env)->GetIntArrayElements(env, parcpArcParams, NULL);

    rc = (jboolean)GpiSetArcParams((HPS)hps, (PARCPARAMS)c_parcpArcParams);

    if (parcpArcParams) (*env)->ReleaseIntArrayElements(env, parcpArcParams, c_parcpArcParams, 0);

    return rc;
}

JNIEXPORT jint JNICALL Java_org_eclipse_swt_internal_pm_OS_GpiPartialArc
        (JNIEnv *env, jclass that, jint hps, jintArray pptlCenter,
        jint fxMultiplier, jint fxStartAngle, jint fxSweepAngle)
{
    jint *c_pptlCenter = NULL;
    jint rc;

    DEBUG_CALL("GpiPartialArc\n")

    if (pptlCenter) c_pptlCenter = (*env)->GetIntArrayElements(env, pptlCenter, NULL);

    rc = (jint)GpiPartialArc((HPS)hps, (PPOINTL)c_pptlCenter, fxMultiplier, fxStartAngle, fxSweepAngle);

    if (pptlCenter) (*env)->ReleaseIntArrayElements(env, pptlCenter, c_pptlCenter, 0);

    return rc;
}

JNIEXPORT jint JNICALL Java_org_eclipse_swt_internal_pm_OS_GpiPolyLine
        (JNIEnv *env, jclass that, jint hps, jint lCount, jintArray aptlPoints)
{
    jint *c_aptlPoints = NULL;
    jint rc;

    DEBUG_CALL("GpiPolyLine\n")

    if (aptlPoints) c_aptlPoints = (*env)->GetIntArrayElements(env, aptlPoints, NULL);

    rc = (jint)GpiPolyLine((HPS)hps, lCount, (PPOINTL)c_aptlPoints);

    if (aptlPoints) (*env)->ReleaseIntArrayElements(env, aptlPoints, c_aptlPoints, 0);

    return rc;
}

JNIEXPORT jboolean JNICALL Java_org_eclipse_swt_internal_pm_OS_GpiBeginArea
        (JNIEnv *env, jclass that, jint hps, jint flOptions)
{
    DEBUG_CALL("GpiBeginArea\n")

    return (jboolean)GpiBeginArea((HPS)hps, flOptions);
}

JNIEXPORT jint JNICALL Java_org_eclipse_swt_internal_pm_OS_GpiEndArea
        (JNIEnv *env, jclass that, jint hps)
{
    DEBUG_CALL("GpiEndArea\n")

    return (jint)GpiEndArea((HPS)hps);
}

JNIEXPORT jboolean JNICALL Java_org_eclipse_swt_internal_pm_OS_GpiBeginPath
        (JNIEnv *env, jclass that, jint hps, jint lPath)
{
    DEBUG_CALL("GpiBeginPath\n")

    return (jboolean)GpiBeginPath((HPS)hps, lPath);
}

JNIEXPORT jboolean JNICALL Java_org_eclipse_swt_internal_pm_OS_GpiEndPath
        (JNIEnv *env, jclass that, jint hps)
{
    DEBUG_CALL("GpiEndPath\n")

    return (jboolean)GpiEndPath((HPS)hps);
}

JNIEXPORT jint JNICALL Java_org_eclipse_swt_internal_pm_OS_GpiStrokePath
        (JNIEnv *env, jclass that, jint hps, jint lPath, jint flOptions)
{
    DEBUG_CALL("GpiStrokePath\n")

    return (jint)GpiStrokePath((HPS)hps, lPath, flOptions);
}

JNIEXPORT jint JNICALL Java_org_eclipse_swt_internal_pm_OS_GpiQueryLineWidthGeom
        (JNIEnv *env, jclass that, jint hps)
{
    DEBUG_CALL("GpiQueryLineWidthGeom\n")

    return (jint)GpiQueryLineWidthGeom((HPS)hps);
}

JNIEXPORT jboolean JNICALL Java_org_eclipse_swt_internal_pm_OS_GpiSetLineWidthGeom
        (JNIEnv *env, jclass that, jint hps, jint lLineWidth)
{
    DEBUG_CALL("GpiSetLineWidthGeom\n")

    return (jboolean)GpiSetLineWidthGeom((HPS)hps, lLineWidth);
}

JNIEXPORT jboolean JNICALL Java_org_eclipse_swt_internal_pm_OS_GpiSetLineEnd
        (JNIEnv *env, jclass that, jint hps, jint lLineEnd)
{
    DEBUG_CALL("GpiSetLineEnd\n")

    return (jboolean)GpiSetLineEnd((HPS)hps, lLineEnd);
}

JNIEXPORT jboolean JNICALL Java_org_eclipse_swt_internal_pm_OS_GpiSetLineJoin
        (JNIEnv *env, jclass that, jint hps, jint lLineJoin)
{
    DEBUG_CALL("GpiSetLineJoin\n")

    return (jboolean)GpiSetLineJoin((HPS)hps, lLineJoin);
}

JNIEXPORT jint JNICALL Java_org_eclipse_swt_internal_pm_OS_GpiQueryAttrs
        (JNIEnv *env, jclass that, jint hps, jint lPrimType, jint flAttrMask,
        jintArray ppbunAttrs)
{
    jint *c_ppbunAttrs = NULL;
    jint rc;

    DEBUG_CALL("GpiQueryAttrs\n")

    if (ppbunAttrs) c_ppbunAttrs = (*env)->GetIntArrayElements(env, ppbunAttrs, NULL);

    rc = (jint)GpiQueryAttrs((HPS)hps, (LONG)lPrimType, (ULONG)flAttrMask,
        (PBUNDLE)c_ppbunAttrs);

    if (ppbunAttrs) (*env)->ReleaseIntArrayElements(env, ppbunAttrs, c_ppbunAttrs, 0);

    return rc;
}
    
JNIEXPORT jboolean JNICALL Java_org_eclipse_swt_internal_pm_OS_GpiSetAttrs
        (JNIEnv *env, jclass that, jint hps, jint lPrimType, jint flAttrMask,
        jint flDefMask, jintArray ppbunAttrs)
{
    jint *c_ppbunAttrs = NULL;
    jboolean rc;

    DEBUG_CALL("GpiSetAttrs\n")

    if (ppbunAttrs) c_ppbunAttrs = (*env)->GetIntArrayElements(env, ppbunAttrs, NULL);

    rc = (jboolean)GpiSetAttrs((HPS)hps, (LONG)lPrimType, (ULONG)flAttrMask,
        (ULONG)flDefMask, (PBUNDLE)c_ppbunAttrs);

    if (ppbunAttrs) (*env)->ReleaseIntArrayElements(env, ppbunAttrs, c_ppbunAttrs, 0);

    return rc;
}

JNIEXPORT jboolean JNICALL Java_org_eclipse_swt_internal_pm_OS_GpiQueryDefaultViewMatrix
        (JNIEnv *env, jclass that, jint hps, jint lCount, jintArray pmatlfArray)
{
    jint *c_pmatlfArray = NULL;
    jboolean rc;

    DEBUG_CALL("GpiQueryDefaultViewMatrix\n")

    if (pmatlfArray) c_pmatlfArray = (*env)->GetIntArrayElements(env, pmatlfArray, NULL);

    rc = (jboolean)GpiQueryDefaultViewMatrix((HPS)hps, lCount, (PMATRIXLF)c_pmatlfArray);

    if (pmatlfArray) (*env)->ReleaseIntArrayElements(env, pmatlfArray, c_pmatlfArray, 0);

    return rc;
}

JNIEXPORT jboolean JNICALL Java_org_eclipse_swt_internal_pm_OS_GpiSetDefaultViewMatrix
        (JNIEnv *env, jclass that, jint hps, jint lCount, jintArray pmatlfArray, jint lOptions)
{
    jint *c_pmatlfArray = NULL;
    jboolean rc;

    DEBUG_CALL("GpiSetDefaultViewMatrix\n")

    if (pmatlfArray) c_pmatlfArray = (*env)->GetIntArrayElements(env, pmatlfArray, NULL);

    rc = (jboolean)GpiSetDefaultViewMatrix((HPS)hps, lCount, (PMATRIXLF)c_pmatlfArray, lOptions);

    if (pmatlfArray) (*env)->ReleaseIntArrayElements(env, pmatlfArray, c_pmatlfArray, 0);

    return rc;
}

JNIEXPORT jint JNICALL Java_org_eclipse_swt_internal_pm_OS_GpiSetBitmap
        (JNIEnv *env, jclass that, jint hps, jint hbm)
{
    DEBUG_CALL("GpiSetBitmap\n")

    return (jint)GpiSetBitmap((HPS)hps, (HBITMAP)hbm);
}

JNIEXPORT jint JNICALL Java_org_eclipse_swt_internal_pm_OS_GpiCreateBitmap
        (JNIEnv *env, jclass that, jint hps, jobject pbmpNew, jint flOptions,
        jbyteArray pbInitData, jintArray pbmiInfoTable)
{
    DECL_GLOB(pGlob)
    BITMAPINFOHEADER2 c_bmpNew, *c_pbmpNew = NULL;
    jbyte *c_pbInitData = NULL;
    jint *c_pbmiInfoTable = NULL;
    jint rc;

    DEBUG_CALL("GpiCreateBitmap\n")

    if (pbmpNew) c_pbmpNew = getBITMAPINFOHEADER2Fields(env, pbmpNew, &c_bmpNew, &PGLOB(BITMAPINFOHEADER2Fc));
    if (pbInitData) c_pbInitData = (*env)->GetByteArrayElements(env, pbInitData, NULL);
    if (pbmiInfoTable) c_pbmiInfoTable = (*env)->GetIntArrayElements(env, pbmiInfoTable, NULL);

    rc = (jint)GpiCreateBitmap((HPS)hps, c_pbmpNew, flOptions, (PBYTE)c_pbInitData, (PBITMAPINFO2)c_pbmiInfoTable);

    if (pbmiInfoTable) (*env)->ReleaseIntArrayElements(env, pbmiInfoTable, c_pbmiInfoTable, 0);
    if (pbInitData) (*env)->ReleaseByteArrayElements(env, pbInitData, c_pbInitData, 0);
    if (pbmpNew) setBITMAPINFOHEADER2Fields(env, pbmpNew, c_pbmpNew, &PGLOB(BITMAPINFOHEADER2Fc));

    return rc;
}

JNIEXPORT jboolean JNICALL Java_org_eclipse_swt_internal_pm_OS_GpiDeleteBitmap
        (JNIEnv *env, jclass that, jint hbm)
{
    DEBUG_CALL("GpiDeleteBitmap\n")

    return (jboolean)GpiDeleteBitmap((HBITMAP)hbm);
}

JNIEXPORT jboolean JNICALL Java_org_eclipse_swt_internal_pm_OS_GpiQueryBitmapInfoHeader
        (JNIEnv *env, jclass that, jint hbm, jobject pbmpData)
{
    DECL_GLOB(pGlob)
    BITMAPINFOHEADER2 c_bmpData, *c_pbmpData = NULL;
    jboolean rc;

    DEBUG_CALL("GpiQueryBitmapInfoHeader\n")

    if (pbmpData) c_pbmpData = getBITMAPINFOHEADER2Fields(env, pbmpData, &c_bmpData, &PGLOB(BITMAPINFOHEADER2Fc));

    rc = (jboolean)GpiQueryBitmapInfoHeader((HBITMAP)hbm, c_pbmpData);

    if (pbmpData) setBITMAPINFOHEADER2Fields(env, pbmpData, c_pbmpData, &PGLOB(BITMAPINFOHEADER2Fc));

    return rc;
}

JNIEXPORT jint JNICALL Java_org_eclipse_swt_internal_pm_OS_GpiQueryBitmapBits
        (JNIEnv *env, jclass that, jint hps, jint lScanStart, jint lScans,
        jbyteArray pbBuffer, jintArray pbmiInfoTable)
{
    jbyte *c_pbBuffer = NULL;
    jint *c_pbmiInfoTable = NULL;
    jint rc;

    DEBUG_CALL("GpiQueryBitmapBits\n")

    if (pbBuffer) c_pbBuffer = (*env)->GetByteArrayElements(env, pbBuffer, NULL);
    if (pbmiInfoTable) c_pbmiInfoTable = (*env)->GetIntArrayElements(env, pbmiInfoTable, NULL);

    rc = (jint)GpiQueryBitmapBits((HPS)hps, lScanStart, lScans, (PBYTE)c_pbBuffer, (PBITMAPINFO2)c_pbmiInfoTable);

    if (pbmiInfoTable) (*env)->ReleaseIntArrayElements(env, pbmiInfoTable, c_pbmiInfoTable, 0);
    if (pbBuffer) (*env)->ReleaseByteArrayElements(env, pbBuffer, c_pbBuffer, 0);

    return rc;
}

JNIEXPORT jint JNICALL Java_org_eclipse_swt_internal_pm_OS_GpiSetBitmapBits
        (JNIEnv *env, jclass that, jint hps, jint lScanStart, jint lScans,
        jbyteArray pbBuffer, jintArray pbmiInfoTable)
{
    jbyte *c_pbBuffer = NULL;
    jint *c_pbmiInfoTable = NULL;
    jint rc;

    DEBUG_CALL("GpiSetBitmapBits\n")

    if (pbBuffer) c_pbBuffer = (*env)->GetByteArrayElements(env, pbBuffer, NULL);
    if (pbmiInfoTable) c_pbmiInfoTable = (*env)->GetIntArrayElements(env, pbmiInfoTable, NULL);

    rc = (jint)GpiSetBitmapBits((HPS)hps, lScanStart, lScans, (PBYTE)c_pbBuffer, (PBITMAPINFO2)c_pbmiInfoTable);

    if (pbmiInfoTable) (*env)->ReleaseIntArrayElements(env, pbmiInfoTable, c_pbmiInfoTable, 0);
    if (pbBuffer) (*env)->ReleaseByteArrayElements(env, pbBuffer, c_pbBuffer, 0);

    return rc;
}

JNIEXPORT jint JNICALL Java_org_eclipse_swt_internal_pm_OS_GpiBitBlt
        (JNIEnv *env, jclass that, jint hpsTarget, jint hpsSource, jint lCount,
        jintArray aptlPoints, jint lRop, jint flOptions)
{
    jint *c_aptlPoints = NULL;
    jint rc;

    DEBUG_CALL("GpiBitBlt\n")

    if (aptlPoints) c_aptlPoints = (*env)->GetIntArrayElements(env, aptlPoints, NULL);

    rc = (jint)GpiBitBlt((HPS)hpsTarget, (HPS)hpsSource, lCount, (PPOINTL)c_aptlPoints, lRop, flOptions);

    if (aptlPoints) (*env)->ReleaseIntArrayElements(env, aptlPoints, c_aptlPoints, 0);

    return rc;
}

JNIEXPORT jint JNICALL Java_org_eclipse_swt_internal_pm_OS_GpiWCBitBlt
        (JNIEnv *env, jclass that, jint hpsTarget, jint hbmSource, jint lCount,
        jintArray aptlPoints, jint lRop, jint flOptions)
{
    jint *c_aptlPoints = NULL;
    jint rc;

    DEBUG_CALL("GpiWCBitBlt\n")

    if (aptlPoints) c_aptlPoints = (*env)->GetIntArrayElements(env, aptlPoints, NULL);

    rc = (jint)GpiWCBitBlt((HPS)hpsTarget, (HBITMAP)hbmSource, lCount, (PPOINTL)c_aptlPoints, lRop, flOptions);

    if (aptlPoints) (*env)->ReleaseIntArrayElements(env, aptlPoints, c_aptlPoints, 0);

    return rc;
}

JNIEXPORT jint JNICALL Java_org_eclipse_swt_internal_pm_OS_GpiDrawBits
        (JNIEnv *env, jclass that, jint hps, jbyteArray pBits, jintArray pbmiInfoTable,
        jint lCount, jintArray aptlPoints, jint lRop, jint flOptions)
{
    jbyte *c_pBits = NULL;
    jint *c_pbmiInfoTable = NULL;
    jint *c_aptlPoints = NULL;
    jint rc;

    DEBUG_CALL("GpiDrawBits\n")

    if (pBits) c_pBits = (*env)->GetByteArrayElements(env, pBits, NULL);
    if (pbmiInfoTable) c_pbmiInfoTable = (*env)->GetIntArrayElements(env, pbmiInfoTable, NULL);
    if (aptlPoints) c_aptlPoints = (*env)->GetIntArrayElements(env, aptlPoints, NULL);

    rc = (jint)GpiDrawBits((HPS)hps, (PVOID)c_pBits, (PBITMAPINFO2)c_pbmiInfoTable,
        lCount, (PPOINTL)c_aptlPoints, lRop, flOptions);

    if (aptlPoints) (*env)->ReleaseIntArrayElements(env, aptlPoints, c_aptlPoints, 0);
    if (pbmiInfoTable) (*env)->ReleaseIntArrayElements(env, pbmiInfoTable, c_pbmiInfoTable, 0);
    if (pBits) (*env)->ReleaseByteArrayElements(env, pBits, c_pBits, 0);

    return rc;
}

JNIEXPORT jboolean JNICALL Java_org_eclipse_swt_internal_pm_OS_GpiSetTextAlignment
        (JNIEnv *env, jclass that, jint hps, jint lHoriz, jint lVert)
{
    DEBUG_CALL("GpiSetTextAlignment\n")

    return (jboolean)GpiSetTextAlignment((HPS)hps, (LONG)lHoriz, (LONG)lVert);
}

JNIEXPORT jint JNICALL Java_org_eclipse_swt_internal_pm_OS_GpiCharStringAt
        (JNIEnv *env, jclass that, jint hps, jintArray pptlPoint, jint lCount,
/*@@TODO (dmik): Unicode*/     
/*        jcharArray pchString)*/
        jbyteArray pchString)
{
    jint *c_pptlPoint = NULL;
/*@@TODO (dmik): Unicode*/     
/*    jchar *c_pchString = NULL;*/
    jbyte *c_pchString = NULL;
    jint rc;

    DEBUG_CALL("GpiCharStringAt\n")

    if (pptlPoint) c_pptlPoint = (*env)->GetIntArrayElements(env, pptlPoint, NULL);
/*@@TODO (dmik): Unicode*/     
/*    if (pchString) c_pchString = (*env)->GetCharArrayElements(env, pchString, NULL);*/
    if (pchString) c_pchString = (*env)->GetByteArrayElements(env, pchString, NULL);

    rc = (jint)GpiCharStringAt((HPS)hps, (PPOINTL)c_pptlPoint, (LONG)lCount, (PCH)c_pchString);

/*@@TODO (dmik): Unicode*/     
/*    if (pchString) (*env)->ReleaseCharArrayElements(env, pchString, c_pchString, 0);*/
    if (pchString) (*env)->ReleaseByteArrayElements(env, pchString, c_pchString, 0);
    if (pptlPoint) (*env)->ReleaseIntArrayElements(env, pptlPoint, c_pptlPoint, 0);

    return rc;
}

JNIEXPORT jboolean JNICALL Java_org_eclipse_swt_internal_pm_OS_GpiQueryTextBox
/*@@TODO (dmik): Unicode*/     
/*        (JNIEnv *env, jclass that, jint hps, jint lCount1, jcharArray pchString,*/
        (JNIEnv *env, jclass that, jint hps, jint lCount1, jbyteArray pchString,
        jint lCount2, jintArray aptlPoints)
{
/*@@TODO (dmik): Unicode*/     
/*    jchar *c_pchString = NULL;*/
    jbyte *c_pchString = NULL;
    jint *c_aptlPoints = NULL;
    jboolean rc;

    DEBUG_CALL("GpiQueryTextBox\n")

/*@@TODO (dmik): Unicode*/     
/*    if (pchString) c_pchString = (*env)->GetCharArrayElements(env, pchString, NULL);*/
    if (pchString) c_pchString = (*env)->GetByteArrayElements(env, pchString, NULL);
    if (aptlPoints) c_aptlPoints = (*env)->GetIntArrayElements(env, aptlPoints, NULL);

    rc = (jboolean)GpiQueryTextBox((HPS)hps, (LONG)lCount1, (PCH)c_pchString,
        (LONG)lCount2, (PPOINTL)c_aptlPoints);

    if (aptlPoints) (*env)->ReleaseIntArrayElements(env, aptlPoints, c_aptlPoints, 0);
/*@@TODO (dmik): Unicode*/     
/*    if (pchString) (*env)->ReleaseCharArrayElements(env, pchString, c_pchString, 0);*/
    if (pchString) (*env)->ReleaseByteArrayElements(env, pchString, c_pchString, 0);

    return rc;
}

JNIEXPORT jint JNICALL Java_org_eclipse_swt_internal_pm_OS_GpiQueryFontAction
        (JNIEnv *env, jclass that, jint hab, jint options)
{
    DEBUG_CALL("GpiQueryFontAction\n")

    return (jint)GpiQueryFontAction((HAB)hab, (ULONG)options);
}

JNIEXPORT jint JNICALL Java_org_eclipse_swt_internal_pm_OS_GpiQueryFonts
        (JNIEnv *env, jclass that, jint hps, jint flOptions, jobject pszFacename,
        jintArray plReqFonts, jint lMetricsLength, jbyteArray afmMetrics)
{
    PSZ c_pszFacename = NULL;
    jint *c_plReqFonts = NULL;
    jbyte *c_afmMetrics = NULL;
    jint rc;

    DEBUG_CALL("GpiQueryFonts\n")

    if (pszFacename) c_pszFacename = getPSZBytes(env, pszFacename);
    if (plReqFonts) c_plReqFonts = (*env)->GetIntArrayElements(env, plReqFonts, NULL);
    if (afmMetrics) c_afmMetrics = (*env)->GetByteArrayElements(env, afmMetrics, NULL);

    rc = (jint)GpiQueryFonts(hps, flOptions, c_pszFacename, (PLONG)c_plReqFonts,
        lMetricsLength, (PFONTMETRICS)c_afmMetrics);

    if (afmMetrics) (*env)->ReleaseByteArrayElements(env, afmMetrics, c_afmMetrics, 0);
    if (plReqFonts) (*env)->ReleaseIntArrayElements(env, plReqFonts, c_plReqFonts, 0);
    if (pszFacename) releasePSZBytes(env, pszFacename, c_pszFacename);

    return rc;
}

JNIEXPORT jint JNICALL Java_org_eclipse_swt_internal_pm_OS_GpiCreateLogFont
        (JNIEnv *env, jclass that, jint hps, jbyteArray pName, jint lLcid, jobject pfatAttrs)
{
    DECL_GLOB(pGlob)
    jbyte *c_pName = NULL;
    FATTRS c_fatAttrs, *c_pfatAttrs = NULL;
    jint rc;

    DEBUG_CALL("GpiCreateLogFont\n")

    if (pName) c_pName = (*env)->GetByteArrayElements(env, pName, NULL);
    if (pfatAttrs) c_pfatAttrs = getFATTRSFields(env, pfatAttrs, &c_fatAttrs, &PGLOB(FATTRSFc));

    rc = (jint)GpiCreateLogFont((HPS)hps, (PSTR8)c_pName, (LONG)lLcid, (PFATTRS)c_pfatAttrs);

    if (pfatAttrs) setFATTRSFields(env, pfatAttrs, c_pfatAttrs, &PGLOB(FATTRSFc));
    if (pName) (*env)->ReleaseByteArrayElements(env, pName, c_pName, 0);

    return rc;
}

JNIEXPORT jboolean JNICALL Java_org_eclipse_swt_internal_pm_OS_GpiSetCharSet
        (JNIEnv *env, jclass that, jint hps, jint llcid)
{
    DEBUG_CALL("GpiSetCharSet\n")

    return (jboolean)GpiSetCharSet((HPS)hps, (LONG)llcid);
}

JNIEXPORT jboolean JNICALL Java_org_eclipse_swt_internal_pm_OS_GpiSetCharBox
        (JNIEnv *env, jclass that, jint hps, jintArray psizfxBox)
{
    jint *c_psizfxBox = NULL;
    jboolean rc;

    DEBUG_CALL("GpiSetCharBox\n")

    if (psizfxBox) c_psizfxBox = (*env)->GetIntArrayElements(env, psizfxBox, NULL);

    rc = (jboolean)GpiSetCharBox((HPS)hps, (PSIZEF)c_psizfxBox);

    if (psizfxBox) (*env)->ReleaseIntArrayElements(env, psizfxBox, c_psizfxBox, 0);
    
    return rc;
}

JNIEXPORT jboolean JNICALL Java_org_eclipse_swt_internal_pm_OS_GpiQueryFontMetrics
        (JNIEnv *env, jclass that, jint hps, jint lMetricsLength, jobject pfmMetrics)
{
    DECL_GLOB(pGlob)
    FONTMETRICS c_pfmMetrics, *c_ppfmMetrics = NULL;
    jboolean rc;

    DEBUG_CALL("GpiQueryFontMetrics\n")

    if (pfmMetrics) c_ppfmMetrics = getFONTMETRICSFields(env, pfmMetrics, &c_pfmMetrics, &PGLOB(FONTMETRICSFc));

    rc = (jboolean)GpiQueryFontMetrics((HPS)hps, (LONG)lMetricsLength, c_ppfmMetrics);

    if (pfmMetrics) setFONTMETRICSFields(env, pfmMetrics, c_ppfmMetrics, &PGLOB(FONTMETRICSFc));

    return rc;
}


