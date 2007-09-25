package org.eclipse.swt.internal.pm;

/*
 * Copyright (c) 2002, 2005 EclipseOS2 Team.
 * This file is made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 */

public class FILEDLG
{
  public int    fl;                      /*  FDS_* flags. */
  public int    ulUser;                  /*  Used by the application. */
  public int    lReturn;                 /*  Result code. */
  public int    lSRC;                    /*  System return code. */
  public PSZ    pszTitle;                /*  Dialog title string. */
  public PSZ    pszOKButton;             /*  OK push button text. */
  public int    pfnDlgProc;              /*  Custom dialog procedure. */
  public PSZ    pszIType;                /*  Extended-attribute type filter. */
  public PSZ[]  papszITypeList;          /*  Pointer to a table of pointers to extended-attribute types. */
  public PSZ    pszIDrive;               /*  The initial drive. */
  public PSZ[]  papszIDriveList;         /*  Pointer to a table of pointers to drives. */
  public int    hMod;                    /*  Module for custom dialog resources. */
  public byte[] szFullFile = new byte[OS.CCHMAXPATH];  /*  Character array. */
  public PSZ[]  papszFQFilename;         /*  Pointer to a table of pointers to fully-qualified file names. */
  public int    ulFQFCount;              /*  Number of file names. */
  public short  usDlgId;                 /*  Custom dialog ID. */
  public short  x;                       /*  X-axis dialog position. */
  public short  y;                       /*  Y-axis dialog position. */
  public short  sEAType;                 /*  Selected extended-attribute type. */
}
