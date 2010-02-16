package org.eclipse.swt.internal.pm;

/*
 * Copyright (c) 2002, 2010 EclipseOS2 Team.
 * This file is made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 */

public class CNRINFO
{
  public int cb;               				/*  Structure size. */
  public int pSortRecord;         			/*  Pointer to the comparison function for sorting container records, or NULL. */
  public int pFieldInfoLast;      			/*  Pointer to the last column in the left window of the split details view, or NULL. */
  public int pFieldInfoObject;    		/*  Pointer to a column that represents an object in the details view. */
  public PSZ pszCnrTitle =  new PSZ();    /*  Title text, or NULL. */
  public int flWindowAttr;        			/*  Window attributes. */
  public POINTL ptlOrigin =  new POINTL();           /*  Workspace origin. */
  public int cDelta;              			/*  Threshold. */
  public int cRecords;            			/*  Number of records. */
  public SIZEL slBitmapOrIcon = new SIZEL();      		/*  Icon/bit-map size. */
  public SIZEL slTreeBitmapOrIcon = new SIZEL();  		/*  Icon/bit-map size. */
  public int hbmExpanded;         		/*  Bit-map handle. */
  public int hbmCollapsed;        		/*  Bit-map handle. */
  public int hptrExpanded;        		/*  Icon handle. */
  public int hptrCollapsed;       		/*  Icon handle. */
  public int cyLineSpacing;       		/*  Vertical space. */
  public int cxTreeIndent;        		/*  Horizontal space. */
  public int cxTreeLine;          			/*  Line width. */
  public int cFields;             			/*  Number of columns. */
  public int xVertSplitbar;       			/*  Split bar position. */
  public static final int sizeof = 92;
}
