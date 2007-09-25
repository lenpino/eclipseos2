package org.eclipse.swt.graphics;

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

import org.eclipse.swt.internal.pm.*;

/**
 * Instances of this class are descriptions of GCs in terms
 * of unallocated platform-specific data fields.
 * <p>
 * <b>IMPORTANT:</b> This class is <em>not</em> part of the public
 * API for SWT. It is marked public only so that it can be shared
 * within the packages provided by SWT. It is not available on all
 * platforms, and should never be called from application code.
 * </p>
 *
 * @private
 */

public final class GCData {
    public Device device;
    public Image image;
    /*
     *  The CLR_DEFAULT constant is used in SWT only to mark that the default
     *  color should be actually set and it is never used directly as a value
     *  for the GpiSet[Back]Color() call. 
     */
    public int foreground = OS.CLR_DEFAULT;
    public int background = OS.CLR_DEFAULT;
//@@TODO(dmik):
//    public int hFont;
    public FATTRS hFont;
    /* Filled up by Control.internal_new_GC() */
    public int hwnd; 
    /* The following two are used by WinBeginPaint in Control.internal_new_GC() */
    public int hps; 
    public RECTL rcl;
    /* Filled up by Drawable.internal_new_GC(). */
    public int hdc;
    /* Indicates to the need to initialize hps in GC.init() */
    public boolean doInit;
}

