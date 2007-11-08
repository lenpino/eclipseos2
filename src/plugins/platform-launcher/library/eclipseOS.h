/*
 * Copyright (c) 2001, 2002 IBM Corp and others.  All rights reserved.
 * This file is made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors: 
 *    Kevin Cornell (Rational Software Corporation)
 */

#ifndef ECLIPSE_OS_H
#define ECLIPSE_OS_H
 
/* Operating System Dependent Information */

/*** See eclipse.c for information on the launcher runtime architecture ***/

/* Define the official name of the program. */
#define getOfficialName() PROGRAM_NAME


/* Global Variables */

extern char   dirSeparator;         /* '/' or '\\' */
extern char   pathSeparator;        /* separator used in PATH variable */
extern char*  consoleVM;   			/* name of VM to use for debugging */
extern char*  defaultVM;   			/* name of VM to use normally      */
extern char*  shippedVMDir;			/* VM bin directory with separator */
extern char*  guiList[];            /* list of possible supported GUIs */


/* OS Specific Functions */

/** Display a Message
 *
 * This method is called to display a message to the user.
 * The method should not return until the user has acknowledged
 * the message. This method will only be called after the window
 * system has been initialized.
 */
extern void displayMessage( char* message );


/** Initialize the Window System
 *
 * This method is called after the command line arguments have been
 * parsed. Its purpose is to initialize the corresponding window system.
 *
 * The showSplash flag indicates the splash window will be displayed by
 * this process (e.g., value will be zero for the main launcher).
 */
extern void initWindowSystem( int* argc, char* argv[], int showSplash );


/** Show the Splash Window
 *
 * This method is called to display the actual splash window. It will only
 * be called by the splash window process and not the main launcher process.
 * The splash ID passed corresponds to the string returned from initWindowSystem().
 * If possible, this ID should be used to communicate some piece of data back
 * to the main launcher program for two reasons:
 * 1) to detect when the splash window process terminates
 * 2) to terminate the splash window process should the JVM terminate before it
 *    completes its initialization.
 *
 * Two parameters are passed: the install home directory and a specific bitmap image
 * file for a feature. The feature's image file is tried first and if it cannot be 
 * displayed, the images from the install directory are used. 
 *
 * Return (exit code):
 * 0        - success
 * non-zero - could not find a splash image to display
 */
extern int showSplash( char* splashId, char* homeDir, char* featureImage );


/** Get List of Java VM Arguments
 *
 * A given Java VM might require a special set of arguments in order to
 * optimize its performance. This method returns a NULL terminated array
 * of strings, where each string is a separate VM argument.
 */
extern char** getArgVM( char *vm );


/* Start the Java VM and Wait For It to Terminate
 *
 * This method is responsible for starting the Java VM and for
 * detecting its termination. The resulting JVM exit code should
 * be returned to the main launcher, which will display a message if
 * the termination was not normal.
 */
extern int startJavaVM( char* args[] );

#endif /* ECLIPSE_OS_H */

