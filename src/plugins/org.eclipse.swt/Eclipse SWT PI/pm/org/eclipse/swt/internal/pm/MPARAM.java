package org.eclipse.swt.internal.pm;

/*
 * Copyright (c) 2002, 2004 EclipseOS2 Team.
 * This file is made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 */

public class MPARAM 
{
    public int value;
    
    public MPARAM (int value) 
    {
        this.value = value;
    }
    public MPARAM (short value) 
    {
        this.value = value;
    }
    public MPARAM (byte value) 
    {
        this.value = value;
    }
    public MPARAM (char value) 
    {
        this.value = value;
    }
    public MPARAM (long value) 
    {
        this.value = (int)value;
    }
    public MPARAM (boolean value) 
    {
        this.value = value ? 0 : 1;
    }
}
