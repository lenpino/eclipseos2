package org.eclipse.swt.internal.pm;

/*
 * Copyright (c) 2002, 2009 EclipseOS2 Team.
 * This file is made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 */

public class RECORDCORE
{
  public int cb = 56;               								/*  Structure size. */
  public int flRecordAttr;     								/*  Container record attributes. */
  public POINTL ptlIcon = new POINTL();        					/*  Position of a container record in the icon view. */
  public int preccNextRecord;								/*  Pointer to the next linked record. */
  public PSZ pszIcon = new PSZ();          					/*  Text for the icon view (CV_ICON). */
  public int hptrIcon;         				/*  Icon that is displayed when the CV_MINI style bit is not specified. */
  public int hptrMiniIcon;     				/*  Icon that is displayed when the CV_MINI style bit is specified. */
  public int hbmBitmap;        				/*  Bit map displayed when the CV_MINI style bit is not specified. */
  public int hbmMiniBitmap;    				/*  Bit map displayed when the CV_MINI style bit is specified. */
  public TREEITEMDESC pTreeItemDesc = new TREEITEMDESC();    /*  Pointer to a TREEITEMDESC structure. */
  public PSZ pszText = new PSZ();          					/*  Text for the text view (CV_TEXT). */
  public PSZ pszName = new PSZ();          					/*  Text for the name view (CV_NAME). */
  public PSZ pszTree = new PSZ();          					/*  Text for the tree view (CV_TREE). */
}
