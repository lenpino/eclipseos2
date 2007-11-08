/*
 * Copyright (c) 2001, 2002 IBM Corp and others.  All rights reserved.
 * This file is made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    Kevin Cornell (Rational Software Corporation)
 */


/* Eclipse Program Launcher
 *
 * This program performs the launching of the eclipse program along
 * with the splash window. If the splash window is to be displayed,
 * the java application will receive two extra arguments:
 *     -showsplash  <splashCommand>
 *
 * When the Java program starts, it should determine if a different
 * version of the splash bitmaps are to be used (uses the feature mechanism).
 * If feature specific bitmaps are to be used, the root directory where
 * those bitmaps reside (e.g., <rootDir>/splash/<bitmapFile>) should be
 * appended (in double quotes) to the splash command. If a directory is
 * not appended, the Eclipse install directory is used.
 *
 * The Java program initiates the displaying of the splash window
 * by executing the splash command as follows:
 *
 *     String command = splashCommand;
 *     String directory = getFeatureRootDirectory();
 *     if (directory != null)
 *         command += " \"" + directory + "\"";
 *     Process splashProcess = Runtime.getRuntime().exec( command );
 *
 * When the Java program initialization is complete, the splash window
 * is brought down by destroying the splash process as follows:
 *
 *     splashProcess.destroy();
 *
 * Therefore, when the splash window is visible, there are actually three
 * processes running:
 * 1) the main launcher process
 * 2) the Java VM process (Eclipse)
 * 3) the splash window process.
 *
 * The Java application does not need to know the format of the
 * <splashCommand> argument but the command format is:
 *   <fullPathOfLauncherProgram> -showsplash <magicNumber>
 *
 * In other words, another eclipse launcher program is run with
 * the arguments "-showsplash" and some magic number. On Windows
 * and Motif, the magic number is the ID of the top window that
 * the main launcher program creates for its message processing loop.
 *
 * When the splash window process starts, it creates its own window
 * for the bitmap and then sends a message back to the main launcher
 * program (using the magic number) with either its bitmap window ID
 * (Windows) or its process ID (Motif and Photon). The main launcher
 * process then waits for the Java VM to exit. If the splash window
 * is still being displayed or the splash process is still running,
 * the main launcher process terminates it.
 *
 * If the Java VM exits with a specific exit code, the main launcher
 * simply restarts the VM again with the same arguments.
 *
 * The options that can be specified by the user to the launcher:
 *  -vm <javaVM>               the Java VM to be used
 *  -application <class>       the starting workbench application class
 *  -os <opSys>                the operating system being run on
 *  -arch <osArch>             the hardware architecture of the OS: x86, sparc, hp9000
 *  -ws <gui>                  the window system to be used: win32, motif, gtk, ...
 *  -nosplash                  do not display the splash screen
 *  -vmargs <userVMarg> ...    a list of arguments for the VM itself
 *
 * The -vmargs option and all user specified VM arguments must appear
 * at the end of the command line, after all arguments that are
 * being passed to Java application.
 *
 * The argument order for the new Java VM process is as follows:
 *
 * <javaVM> <wsVMargs> <userVMargs> <wsSpecificArgs> -application <class>
 *     [-showsplash <splashCommand>] <userArgs>
 *
 * If the -nosplash option is given to the launcher, the java
 * application will not receive the "-showsplash" option or its
 * associated command.
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/types.h>
#include <sys/stat.h>

#ifdef _WIN32
#include <windows.h>
#include <direct.h>
#define getcwd _getcwd
#define strcasecmp _stricmp

//@@@OS2BEGIN
#endif
#ifdef __OS2__
#  include <direct.h>
#  define getcwd _getcwd
#  define strcasecmp stricmp
//@@@OS2END

#else /* Linux and UNIX */
#include <unistd.h>
#endif

#include "eclipseOS.h"

#define MAX_PATH_LENGTH   2000

/* Global Data */
static char*  program     = NULL;       /* full pathname of the program */
static char*  homeDir     = NULL;       /* directory where program resides */
static char*  javaVM      = NULL;       /* full pathname of the Java VM to run */

/* Define the special exit codes returned from Eclipse. */
static int   startupProblemExitCode = 13;
static int   vmVersionExitCode      = 14;
static int   isRunningExitCode      = 15;
static int   restartExitCode        = 23;

/* Define the maximum time (in seconds) for the splash window to remain visible. */
static char*  splashTimeout = "600";   /* 10 minutes */

/* Define the required VM arguments (all platforms). */
#define       startupJarName "startup.jar"
static char*  reqVMarg[] = { "-cp", startupJarName, "org.eclipse.core.launcher.Main", NULL };

/* Define error messages. (non-NLS) */
static char* exitMsg = "JVM terminated. Exit code=%d\n%s";
static char* goVMMsg = "Start VM: %s\n";
static char* pathMsg = "%s\n'%s' in your current PATH";
static char* showMsg = "Could not load splash bitmap:\n%s";
static char* noVMMsg =
"A Java Runtime Environment (JRE) or Java Development Kit (JDK)\n\
must be available in order to run Eclipse. No Java virtual machine\n\
was found after searching the following locations:\n\
%s";

static char* homeMsg =
"The Eclipse executable launcher was unable to locate its \n\
companion startup.jar file (in the same directory as the executable).";

static char* startupProblemMsg =
"Problems during startup. Check the \".log\" file\n\
in the \".metadata\" directory of your workspace.";

static char* vmVersionMsg = "Please use a newer VM. Eclipse requires at least 1.3.0.";

static char* isRunningMsg =
"Another session is already running in the associated workspace.\n\
If this is not the case, please delete the \".lock\" file in the\n\
workspace \".metadata\" directory and try starting the platform again.";


/* Define constants for the options recognized by the launcher. */
#define CONSOLELOG   "-consoleLog"
#define DEBUG        "-debug"
#define OS           "-os"
#define OSARCH       "-arch"
#define NOSPLASH     "-nosplash"
#define SHOWSPLASH   "-showsplash"
#define VM           "-vm"
#define WS           "-ws"
#define VMARGS       "-vmargs"				/* special option processing required */

/* Define the variables to receive the option values. */
static int    needConsole   = 0;			/* True: user wants a console	*/
static int    debug         = 0;			/* True: output debugging info	*/
static int    noSplash      = 0;			/* True: do not show splash win	*/
static char*  osArg         = "os2";/*DEFAULT_OS;   */
static char*  osArchArg     = "x86";/*DEFAULT_OS_ARCH;    */
static char*  showSplashArg = NULL;     	/* showsplash data (main launcher window) */
static char*  vmName        = NULL;     	/* Java VM that the user wants to run */
static char*  wsArg         = "pm";/* DEFAULT_WS;*/	/* the SWT supported GUI to be used */
static char** userVMarg     = NULL;     	/* user specific args for the Java VM  */


/* Define a table for processing command line options. */
typedef struct
{
	char*  name;		/* the option recognized by the launcher */
	char** value;		/* the variable where the option value is saved */
	int*   flag;		/* the variable that is set if the option is defined */
	int    remove;		/* the number of argments to remove from the list */
} Option;

static Option options[] = {
    { CONSOLELOG,	NULL,			&needConsole,	0 },
    { DEBUG,		NULL,			&debug,			0 },
    { NOSPLASH,     NULL,           &noSplash,		1 },
    { OS,			&osArg,			NULL,			2 },
    { OSARCH,		&osArchArg,		NULL,			2 },
    { SHOWSPLASH,   &showSplashArg,	NULL,			2 },
    { VM,           &vmName,		NULL,			2 },
    { WS,			&wsArg,			NULL,			2 } };
static int optionsSize = (sizeof(options) / sizeof(options[0]));

/* Local methods */
static void   parseArgs( int* argc, char* argv[] );
static char** getVMCommand( int argc, char* argv[] );
	   char*  findCommand( char* command );
static char*  formatVmCommandMsg( char* args[] );
static char*  getInstallDir();


int main( int argc, char* argv[] )
{
	char*   splashBitmap;	
    char*   ch;
    char*   shippedVM    = NULL;
    char*   vmSearchPath = NULL;
    char**  vmCommand;
    char*   vmCommandMsg = NULL;
    char*   errorMsg;
    int     exitCode;

	/* Strip off any extroneous <CR> from the last argument. If a shell script
	    on Linux is created in DOS format (lines end with <CR><LF>), the C-shell
	    does not strip off the <CR> and hence the argument is bogus and may
	    not be recognized by the launcher or eclipse itself. */
	 ch = strchr( argv[ argc - 1 ], (int) '\r' );
	 if (ch != NULL)
	 {
	     *ch = '\0';
	 }
	
	/* Determine the full pathname of this program. */
    program = findCommand( argv[0] );
    if (program == NULL)
    {
#ifdef _WIN32
    	program = malloc( MAX_PATH_LENGTH + 1 );
    	GetModuleFileName( NULL, program, MAX_PATH_LENGTH );
#else
    	program = malloc( strlen( argv[0] ) + 1 );
    	strcpy( program, argv[0] );
#endif
    }

    /* Parse command line arguments (looking for the VM to use). */
    parseArgs( &argc, argv );

    /* Initialize the window system. */
    initWindowSystem( &argc, argv, (showSplashArg != NULL) );

    /* Find the home directory where the Eclipse is installed. */
    homeDir = getInstallDir();
    if (homeDir == NULL)
    {
    	displayMessage( homeMsg );
    	exit( 1 );
    }

    /* If the showsplash option was given */
    if (showSplashArg != NULL)
    {
    	/* If an extra argument was given, pass it as the image to display. */
    	splashBitmap = (argc > 1 ? argv[1] : NULL);
    	exitCode = showSplash( showSplashArg, homeDir, splashBitmap );
    	if (exitCode && debug)
    	{
    		if (splashBitmap == NULL)
    			splashBitmap = homeDir;
        	errorMsg = (char*) malloc( strlen(showMsg) + strlen(splashBitmap) + 10 );
        	sprintf( errorMsg, showMsg, splashBitmap );
        	displayMessage( errorMsg );
        	free( errorMsg );
     	}
    	exit( exitCode );
    }

    /* If the user did not specify a VM to be used */
    if (vmName == NULL)
    {
    	/* Determine which type of VM should be used. */
    	vmName = ((debug || needConsole) ? consoleVM : defaultVM);
    	
        /* Try to find the VM shipped with eclipse. */
        shippedVM = malloc( strlen( homeDir ) + strlen( vmName ) + 10 );
        sprintf( shippedVM, "%s%s%s", homeDir, shippedVMDir, vmName );
        javaVM = findCommand( shippedVM );

        /* Format a message to indicate the default VM search path. */
        vmSearchPath = malloc( strlen( pathMsg ) + strlen( shippedVM ) + strlen( vmName ) + 10 );
        sprintf( vmSearchPath, pathMsg, shippedVM, vmName );
        free( shippedVM );
        shippedVM = NULL;
	}
	
	/* If a Java VM has not been found yet */
	if (javaVM == NULL)
	{
		/* Either verify the VM specified by the user or
		   attempt to find the VM in the user's PATH. */
		javaVM = findCommand( vmName );
		
		/* If the VM was not found, display a message and exit. */
		if (javaVM == NULL)
		{
			if (vmSearchPath != NULL) vmName = vmSearchPath; /* used default VM searching */
        	errorMsg = (char*) malloc( strlen(noVMMsg) + strlen(vmName) + 10 );
        	sprintf( errorMsg, noVMMsg, vmName );
        	displayMessage( errorMsg );
        	free( errorMsg );
        	exit(1);
		}
	}

    /* Get the command to start the Java VM. */
    vmCommand = getVMCommand( argc, argv );
    vmCommandMsg = formatVmCommandMsg( vmCommand );

    /* While the Java VM should be restarted */
    exitCode = restartExitCode;
    while (exitCode == restartExitCode)
    {
    	/* Start the Java virtual machine and wait for it to exit. */
    	if (debug) printf( goVMMsg, vmCommandMsg );
    	exitCode = startJavaVM( vmCommand );
    	
    	/* If the JVM exited abnormally (and was not a restart) */
    	if (exitCode != 0 && exitCode != restartExitCode)
    	{
        	/* Format and output an error message. */
       		if (exitCode == startupProblemExitCode)
        	{
        		displayMessage( startupProblemMsg );
        	}
        	else if (exitCode == vmVersionExitCode)
        	{
        		displayMessage( vmVersionMsg );
        	}
        	else if (exitCode == isRunningExitCode)
        	{
        		displayMessage( isRunningMsg );
        	}
        	else
        	{
        		errorMsg = (char*) malloc( strlen(exitMsg) + strlen(vmCommandMsg) + 10 );
        		sprintf( errorMsg, exitMsg, exitCode, vmCommandMsg );
        		displayMessage( errorMsg );
        		free( errorMsg );
        	}
    	}
    }

    /* Cleanup time. */
    free( homeDir );
    free( program );
	free( vmCommandMsg );
    if (vmSearchPath != NULL) free( vmSearchPath );

    return 0;
}


/*
 * Parse arguments of the command.
 */
static void parseArgs( int* pArgc, char* argv[] )
{
	Option* option;
    int     remArgs;
    int     index;
    int     i;

    /* Ensure the list of user argument is NULL terminated. */
    argv[ *pArgc ] = NULL;

	/* For each user defined argument (excluding the program) */
    for (index = 1; index < *pArgc; index++){
        remArgs = 0;

        /* Find the corresponding argument is a option supported by the launcher */
        option = NULL;
        for (i = 0; option == NULL && i < optionsSize; i++)
        {
        	if (strcasecmp( argv[ index ], options[ i ].name ) == 0)
        	    option = &options[ i ];
       	}
       	
       	/* If the option is recognized by the launcher */
       	if (option != NULL)
       	{
       		/* If the option requires a value and there is one, extract the value. */
       		if (option->value != NULL && (index+1) < *pArgc)
       			*option->value = argv[ index+1 ];
       		
       		/* If the option requires a flag to be set, set it. */
       		if (option->flag != NULL)
       			*option->flag = 1;
       		remArgs = option->remove;
       	}

        /* All of the remaining arguments are user VM args. */
        else if (strcasecmp( argv[ index ], VMARGS ) == 0)
        {
            userVMarg = &argv[ index+1 ];
            argv[ index ] = NULL;
            *pArgc = index;
        }

		/* Remove any matched arguments from the list. */
        if (remArgs > 0)
        {
            for (i = (index + remArgs); i <= *pArgc; i++)
            {
                argv[ i - remArgs ] = argv[ i ];
            }
            index--;
            *pArgc -= remArgs;
        }
    }
}

/*
 * Find the absolute pathname to where a command resides.
 *
 * The string returned by the function must be freed.
 */
#define EXTRA 20
char* findCommand( char* command )
{
    char*  cmdPath;
    int    length;
    char*  ch;
    char*  dir;
    char*  path;
    struct stat stats;

    /* If the command was an abolute pathname, use it as is. */
    if (command[0] == dirSeparator ||
       (strlen( command ) > 2 && command[1] == ':'))
    {
        length = strlen( command );
        cmdPath = malloc( length + EXTRA ); /* add extra space for a possible ".exe" extension */
        strcpy( cmdPath, command );
    }

    else
    {
        /* If the command string contains a path separator */
        if (strchr( command, (int) dirSeparator ) != NULL)
        {
            /* It must be relative to the current directory. */
            length = MAX_PATH_LENGTH + EXTRA + strlen( command );
            cmdPath = malloc( length );
            getcwd( cmdPath, length );
            if (cmdPath[ strlen( cmdPath ) - 1 ] != dirSeparator)
            {
                length = strlen( cmdPath );
                cmdPath[ length ] = dirSeparator;
                cmdPath[ length+1 ] = '\0';
            }
            strcat( cmdPath, command );
        }

        /* else the command must be in the PATH somewhere */
        else
        {
            /* Get the directory PATH where executables reside. */
            path = getenv( "PATH" );
            length = strlen( path ) + strlen( command ) + MAX_PATH_LENGTH;
            cmdPath = malloc( length );

            /* Foreach directory in the PATH */
            dir = path;
            while (dir != NULL && *dir != '\0')
            {
                ch = strchr( dir, (int)pathSeparator );
                if (ch == NULL)
                {
                    strcpy( cmdPath, dir );
                }
                else
                {
                    length = ch - dir;
                    strncpy( cmdPath, dir, length );
                    cmdPath[ length ] = '\0';
                    ch++;
                }
                dir = ch; /* advance for the next iteration */

                /* Determine if the executable resides in this directory. */
                if (cmdPath[0] == '.' &&
                   (strlen(cmdPath) == 1 || (strlen(cmdPath) == 2 && cmdPath[1] == dirSeparator)))
                {
                	getcwd( cmdPath, MAX_PATH_LENGTH );
                }
                if (cmdPath[ strlen( cmdPath ) - 1 ] != dirSeparator)
                {
                    length = strlen( cmdPath );
                    cmdPath[ length ] = dirSeparator;
                    cmdPath[ length+1 ] = '\0';
                }
                strcat( cmdPath, command );

                /* If the file is not a directory and can be executed */
                if (stat( cmdPath, &stats ) == 0 && (stats.st_mode & S_IFREG) != 0)
                {
                    /* Stop searching */
                    dir = NULL;
                }
            }
        }
    }

#ifdef _WIN32
	/* If the command does not exist */
    if (stat( cmdPath, &stats ) != 0 || (stats.st_mode & S_IFREG) == 0)
    {
    	/* If the command does not end with .exe, append it an try again. */
    	length = strlen( cmdPath );
    	if (length > 4 && strcasecmp( &cmdPath[ length - 4 ], ".exe" ) != 0)
    	    strcat( cmdPath, ".exe" );
    }
#endif
	
    /* Verify the resulting command actually exists. */
    if (stat( cmdPath, &stats ) != 0 || (stats.st_mode & S_IFREG) == 0)
    {
        free( cmdPath );
        cmdPath = NULL;
    }

    /* Return the absolute command pathname. */
    return cmdPath;
}



/*
 * Get the command and arguments to start the Java VM.
 *
 * Memory allocated by this function is assumed to be
 * deallocated when the program terminates.
 *
 * Some of the arguments returned by this function were
 * passed directly from the main( argv ) array so they
 * should not be deallocated.
 */
static char** getVMCommand( int argc, char* argv[] )
{
	char** defVMarg;
    int    nDefVMarg = 0;
    int    nReqVMarg = 0;
    int    nUserVMarg = 0;
    int    totalArgs;
    char** execArg;
    char*  jarFile = NULL;
    char*  splashExec;
    int    src;
    int    dst;

 	/* Calculate the number of user VM arguments. */
 	if (userVMarg != NULL)
 	{
	 	while (userVMarg[ nUserVMarg ] != NULL)
 			nUserVMarg++;
 	}
 	
 	/* Calculate the number of default VM arguments. */
 	defVMarg = getArgVM( javaVM );
 	while (defVMarg[ nDefVMarg ] != NULL)
 		nDefVMarg++;
 	
 	/* Calculate the number of required VM arguments. */
 	while (reqVMarg[ nReqVMarg ] != NULL)
 		nReqVMarg++;
 		
    /* Allocate the arg list for the exec call.
       (VM + user VM args + default VM args  + required VM args + -os OS + -ws GUI + -osArch arch
           + SHOWSPLASH X + argv[] + NULL) */
    totalArgs  = 1 + nUserVMarg + nDefVMarg + nReqVMarg + 2 + 2 + 2 + 2 + argc + 1;
    execArg = (char**) malloc( totalArgs * sizeof( char* ) );
    dst = 0;
    execArg[ dst++ ] = javaVM;

    /* If the user specified "-vmargs", add them instead of the default VM args. */
    if (userVMarg != NULL)
    {
    	for (src = 0; src < nUserVMarg; src++)
	    	execArg[ dst++ ] = userVMarg[ src ];
	}
	else
	{
    	for (src = 0; src < nDefVMarg; src++)
	    	execArg[ dst++ ] = defVMarg[ src ];
	}
		

    /* For each required VM arg */
    for (src = 0; src < nReqVMarg; src++)
    {
    	/* If the argument is not the startup jar, use it as is. */
     	if (strcmp( reqVMarg[ src ], startupJarName ) != 0)
     	{
	        execArg[ dst++ ] = reqVMarg[ src ];
	    }
	
	    /* else use the absolute path of the jar file. */
	    else
	    {
			jarFile = malloc( strlen( homeDir ) + strlen( startupJarName ) + 1 );
    		strcpy( jarFile, homeDir );
    		strcat( jarFile, startupJarName );
	        execArg[ dst++ ] = jarFile;
	    }
    }

	/* Append the required options. */
    execArg[ dst++ ] = OS;
    execArg[ dst++ ] = osArg;
    execArg[ dst++ ] = WS;
    execArg[ dst++ ] = wsArg;
    execArg[ dst++ ] = OSARCH;
    execArg[ dst++ ] = osArchArg;
	
	/* Append the show splash window command, if defined. */
    if (!noSplash)
    {
        execArg[ dst++ ] = SHOWSPLASH;
		splashExec = malloc( strlen( program ) + strlen( splashTimeout ) + 20 );
		sprintf( splashExec, "%s %s %s", program, SHOWSPLASH, splashTimeout );
	    execArg[ dst++ ] = splashExec;
    }

	/* Append the remaining user defined argments. */
    for (src = 1; src < argc; src++)
    {
        execArg[ dst++ ] = argv[ src ];
    }
    execArg[ dst++ ] = NULL;

	return execArg;
 }

 /* Format the JVM start command for error messages
  *
  * This method formats a string with the JVM start command (and all arguments)
  * that can be used in displaying error messages. The string returned from this
  * method is probably not NLS compliant and must be deallocated by the caller.
  */
static char* formatVmCommandMsg( char* args[] )
{
	int   index;
    int   length;
    char* ch;
    char* message;

	/* Determine the length of the message buffer. */
	length = 0;
	for (index = 0; args[index] != NULL; index++)
	{
		length += strlen(args[index]) + 1;
	}
	message = (char*) malloc( length + 5 );
	
	/* Format the message such that options (args starting with '-') begin
	   on a new line. Otherwise, the Motif MessageBox does not automatically wrap
	   the messages and the message window can extend beyond both sides of the display. */
	ch = message;
	for (index = 0; args[index] != NULL; index++)
	{
		if (args[index][0] == '-' && *(ch-1) == ' ')
			*(ch-1) = '\n';
		strcpy( ch, args[index] );
		ch += strlen( args[index] );
		*ch++ = ' ';
	}
	*ch = '\0';
	
	return message;
}

/* Determine the Installation Directory
 *
 * This function takes the directory where program executable resides and
 * determines the installation directory. The installation directory must
 * contain the startup JAR file and the executable must be either in the
 * install directory or in a subdirectory.
 */
static char* getInstallDir( )
{
	char*  ch;
	char*  installDir;
	char   path[ MAX_PATH_LENGTH ];
    struct stat stats;
	
	/* Start with the directory where the executable resides. */
    installDir = (char*) malloc( strlen( program ) + 1 );
    strcpy( installDir, program );
    ch = strrchr( installDir, (int)dirSeparator );

    /* For each directory in the hierarchy */
    while (ch != NULL)
    {
    	/* Format the pathname of the startup jar file. */
    	*(ch+1) = '\0';
    	sprintf( path, "%s%s", installDir, startupJarName );
    	
    	/* If the jar file exists */
    	if (stat( path, &stats ) == 0 && (stats.st_mode & S_IFREG) != 0)
    	{
    		return installDir;
    	}
    	else
    	{
    		/* Try the next directory up in the hierarchy. */
    		*ch = '\0';		
    		ch = strrchr( installDir, (int)dirSeparator );
    	}
    }

    free( installDir );
    return NULL;
}


