package org.eclipse.swt.internal.pm;

/*
 * Copyright (c) 2002, 2004 EclipseOS2 Team.
 * This file is made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 */

public class MRESULT {
    public int value;
    public static final MRESULT TRUE = new MRESULT (OS.TRUE);
    public static final MRESULT FALSE = new MRESULT (OS.FALSE);
    public static final MRESULT ZERO = new MRESULT (0);
    public MRESULT (int value) {
        this.value = value;
    }
}
