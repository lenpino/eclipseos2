/*
 * Copyright (c) 2002 IBM Corp and others.  All rights reserved.
 * This file is made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors: 
 *    Kevin Cornell (Rational Software Corporation)
 */
 

#ifndef ECLIPSE_UTIL_H
#define ECLIPSE_UTIL_H
 
/* Eclipse Launcher Utility Methods */

/* Is the given Java VM J9 */
extern int isJ9VM( char* vm );


#ifdef AIX 
/* Get the version of the VM */
extern char* getVMVersion( char* vm );

/* Compare JVM Versions */
extern int versionCmp( char* ver1, char* ver2 );
#endif

#endif /* ECLIPSE_UTIL_H */
