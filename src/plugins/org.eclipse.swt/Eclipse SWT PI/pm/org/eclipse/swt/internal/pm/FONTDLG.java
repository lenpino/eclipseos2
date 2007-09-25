package org.eclipse.swt.internal.pm;

/*
 * Copyright (c) 2002, 2004 EclipseOS2 Team.
 * This file is made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 */
 public class FONTDLG{
	 public int cbSize;
	 public int hpsScreen;
	 public int hpsPrinter;
	 public PSZ pszTitle = new PSZ();
	 public PSZ pszPreview = new PSZ();
	 public PSZ pszPtSizeList = new PSZ();
	 public int pfnDlgProc;
	 public PSZ pszFamilyname = new PSZ();
	 public int fxPointSize;
	 public int fl;
	 public int flFlags;
	 public int flType;
	 public int flTypeMask;
	 public int flStyle;
	 public int flStyleMask;
	 public int clrFore;
	 public int clrBack;
	 public int ulUser;
	 public int lReturn;
	 public int lSRC;
	 public int lEmHeight;
	 public int lXHeight;
	 public int lExternalLeading;
	 public int hMod;
	 public FATTRS fAttrs = new FATTRS();
	 public short sNominalPointSize;
	 public short usWeight;
	 public short usWidth;
	 public short x;
	 public short y;
	 public short usDlgId;
	 public short usFamilyBufLen;
	 public short usReserved;
	 public static final int sizeof = 168;
 }
