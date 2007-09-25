package org.eclipse.swt.widgets;

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
 * This class contains static methods which provide a map from the
 * platform representation of a widget to the SWT control. 
 */

class WidgetTable {
    static int FreeSlot = 0;
    static int GrowSize = 1024;
    static int [] IndexTable = new int [GrowSize];
    static Control [] ControlTable = new Control [GrowSize];
    static {
        for (int i=0; i<GrowSize-1; i++) IndexTable [i] = i + 1;
        IndexTable [GrowSize - 1] = -1;
    }

public static synchronized Control get (int handle) {
    if (handle == 0) return null;
    int index = OS.WinQueryWindowULong (handle, OS.QWL_USER) - 1;
    if (0 <= index && index < ControlTable.length) return ControlTable [index];
    return null;
}

public synchronized static void put (int handle, Control control) {
    if (handle == 0) return;
    if (FreeSlot == -1) {
        int length = (FreeSlot = IndexTable.length) + GrowSize;
        int [] newIndexTable = new int [length];
        Control [] newControlTable = new Control [length];
        System.arraycopy (IndexTable, 0, newIndexTable, 0, FreeSlot);
        System.arraycopy (ControlTable, 0, newControlTable, 0, FreeSlot);
        for (int i=FreeSlot; i<length-1; i++) newIndexTable [i] = i + 1;
        newIndexTable [length - 1] = -1;
        IndexTable = newIndexTable;
        ControlTable = newControlTable;
    }
    OS.WinSetWindowULong (handle, OS.QWL_USER, FreeSlot + 1);
    int oldSlot = FreeSlot;
    FreeSlot = IndexTable [oldSlot];
    IndexTable [oldSlot] = -2;
    ControlTable [oldSlot] = control;
}

public static synchronized Control remove (int handle) {
    if (handle == 0) return null;
    Control control = null;
    int index = OS.WinQueryWindowULong (handle, OS.QWL_USER) - 1;
    if (0 <= index && index < ControlTable.length) {
        control = ControlTable [index];
        ControlTable [index] = null;
        IndexTable [index] = FreeSlot;
        FreeSlot = index;
        OS.WinSetWindowULong (handle, OS.QWL_USER, 0);
    }
    return control;
}

public static synchronized Shell [] shells () {
    int size = 0;
    for (int i=0; i<ControlTable.length; i++) {
        Control control = ControlTable [i];
        if (control != null && control instanceof Shell) size++;
    }

    int index = 0;
    Shell [] result = new Shell [size];
    for (int i=0; i<ControlTable.length; i++) {
        Control control = ControlTable [i];
        if (control != null && control instanceof Shell) {
            result [index++] = (Shell) control;
        }
    }
    return result;
}

public static synchronized int size () {
    int length = 0;
    for (int i=0; i<ControlTable.length; i++) {
        if (ControlTable [i] != null) length++;
    }
    return length;
}

}
