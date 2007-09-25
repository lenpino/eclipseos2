package org.eclipse.swt.internal.pm;

/*
 * Copyright (c) 2002, 2004 EclipseOS2 Team.
 * This file is made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 */

public class TIB
{
    public int tib_pexchain;
    public int tib_pstack;
    public int tib_pstacklimit;
    public TIB2 tib_ptib2 = new TIB2();
    public int tib_version;
    public int tib_ordinal;
//    public public static final int sizeof = 6*4;
}
