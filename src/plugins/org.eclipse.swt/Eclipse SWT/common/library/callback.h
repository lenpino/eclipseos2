/*
 * OS/2 version.
 * Copyright (c) 2002, 2004 EclipseOS2 Team.
 */

/*
 * Copyright (c) 2000, 2002 IBM Corp.  All rights reserved.
 * This file is made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 */

/**
 * Callback implementation.
 */
#ifndef INC_callback_H
#define INC_callback_H

int callback(int index, ...);

#if defined (WIN32) || defined (_WIN32_WCE)
#include "windows.h"
#define PLATFORM "win32"
#define RETURN_TYPE LRESULT CALLBACK
#define RETURN_CAST (LRESULT)
#endif

//@@OS2+
#if defined (__OS2__)
#include "os2def.h"
#include <memory.h>
#define PLATFORM "pm"
#define RETURN_TYPE MRESULT EXPENTRY
#define RETURN_CAST (MRESULT)
#endif
//@@OS2-

#ifdef MOTIF
#define PLATFORM "motif"
#endif

#ifdef GTK
#define PLATFORM "gtk"
#endif

#ifdef PHOTON
#define PLATFORM "photon"
#endif

#ifndef PLATFORM
#define PLATFORM "unknown"
#endif

#ifndef RETURN_TYPE
#define RETURN_TYPE int
#endif

#ifndef RETURN_CAST
#define RETURN_CAST
#endif

#define MAX_CALLBACKS 128
#define MAX_ARGS 12

typedef struct SWT_CALLBACKINFO {
    jobject callin;
    JNIEnv *env;
    jmethodID methodID;
} SWT_CALLBACKINFO;

#endif /* ifndef INC_callback_H */

