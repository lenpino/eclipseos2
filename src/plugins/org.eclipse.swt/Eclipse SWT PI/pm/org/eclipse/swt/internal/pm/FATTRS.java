package org.eclipse.swt.internal.pm;

/*
 * Copyright (c) 2002, 2004 EclipseOS2 Team.
 * This file is made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 */

public class FATTRS
{
	public short usRecordLength;
	public short fsSelection;
	public int lMatch;
	public byte[] szFacename = new byte [OS.FACESIZE];
	public short idRegistry;
	public short usCodePage;
	public int lMaxBaselineExt;
	public int lAveCharWidth;
	public short fsType;
	public short fsFontUse;
    public final static int sizeof = 56;

    /***
     *  Note. The fields below are not the part of the OS/2 FATTRS structure,
     *  they are added for convenience and used on the Java level only (GPI 
     *  calls use only the above standard fields). These fields are from the
     *  FONTMETRICS structure except the last one which is the height of the
     *  font in points (i.e. FONTMETRICS.sNominalPointSize divided by 10).
     */
    public String fm_szFamilyname;
    public short fm_usWeightClass;
    public short fm_fsSelection;
    public int height;
}

