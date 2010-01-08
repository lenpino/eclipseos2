package org.eclipse.swt.internal.pm;

/*
 * Copyright (c) 2002, 2009 EclipseOS2 Team.
 * This file is made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 */

public class MINIRECORDCORE
{
  public int cb;               					/*  Structure size. */
  public int flRecordAttr;     				/*  Attributes of container records. */
  public POINTL  ptlIcon = new POINTL();       /*  Record position. */
  public int preccNextRecord;  				/*  Pointer to the next linked record. */
  public PSZ pszIcon = new PSZ();          	/*  Record text. */
  public int hptrIcon;         				/*  Record icon. */
}
