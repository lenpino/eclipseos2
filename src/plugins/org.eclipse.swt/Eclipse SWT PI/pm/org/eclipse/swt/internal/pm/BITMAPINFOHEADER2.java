package org.eclipse.swt.internal.pm;

/*
 * Copyright (c) 2002, 2004 EclipseOS2 Team.
 * This file is made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 */

public class BITMAPINFOHEADER2
{
    //public int cbFix;
    public int cx;
    public int cy;
    public short cPlanes;
    public short cBitCount;
    public int ulCompression;
    public int cbImage;
    public int cxResolution;
    public int cyResolution;
    public int cclrUsed;
    public int cclrImportant;
    public short usUnits;
    public short usReserved;
    public short usRecording;
    public short usRendering;
    public int cSize1;
    public int cSize2;
    public int ulColorEncoding;
    public int ulIdentifier;
    public final static int sizeof = 16 * 4;
    public final static int int_sizeof = 16;
}
