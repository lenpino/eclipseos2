package org.eclipse.swt.internal.pm;

/*
 * Copyright (c) 2002, 2004 EclipseOS2 Team.
 * This file is made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 */

public class FONTMETRICS
{
	public byte[] szFamilyname = new byte[OS.FACESIZE];
	public byte[] szFacename = new byte[OS.FACESIZE];
	public short idRegistry;
	public short usCodePage;
	public int lEmHeight;
	public int lXHeight;
	public int lMaxAscender;
	public int lMaxDescender;
	public int lLowerCaseAscent;
	public int lLowerCaseDescent;
	public int lInternalLeading;
	public int lExternalLeading;
	public int lAveCharWidth;
	public int lMaxCharInc;
	public int lEmInc;
	public int lMaxBaselineExt;
	public short sCharSlope;
	public short sInlineDir;
	public short sCharRot;
	public short usWeightClass;
	public short usWidthClass;
	public short sXDeviceRes;
	public short sYDeviceRes;
	public short sFirstChar;
	public short sLastChar;
	public short sDefaultChar;
	public short sBreakChar;
	public short sNominalPointSize;
	public short sMinimumPointSize;
	public short sMaximumPointSize;
	public short fsType;
	public short fsDefn;
	public short fsSelection;
	public short fsCapabilities;
	public int lSubscriptXSize;
	public int lSubscriptYSize;
	public int lSubscriptXOffset;
	public int lSubscriptYOffset;
	public int lSuperscriptXSize;
	public int lSuperscriptYSize;
	public int lSuperscriptXOffset;
	public int lSuperscriptYOffset;
	public int lUnderscoreSize;
	public int lUnderscorePosition;
	public int lStrikeoutSize;
	public int lStrikeoutPosition;
	public short sKerningPairs;
	public short sFamilyClass;
	public int lMatch;
	public int FamilyNameAtom;
	public int FaceNameAtom;
	/* PANOSE structure */
	public byte panose_bFamilyType;
	public byte panose_bSerifStyle;
	public byte panose_bWeight;
	public byte panose_bProportion;
	public byte panose_bContrast;
	public byte panose_bStrokeVariation;
	public byte panose_bArmStyle;
	public byte panose_bLetterform;
	public byte panose_bMidline;
	public byte panose_bXHeight;
	public byte panose_fbPassedISO;
	public byte panose_fbFailedISO;
    public final static int sizeof = 228;
}

