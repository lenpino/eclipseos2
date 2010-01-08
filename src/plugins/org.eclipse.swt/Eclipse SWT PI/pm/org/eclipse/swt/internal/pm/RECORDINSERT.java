package org.eclipse.swt.internal.pm;

/*
 * Copyright (c) 2002, 2009 EclipseOS2 Team.
 * This file is made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 */

public class RECORDINSERT
{
  public int cb;               				/*  Structure size. */
  public int pRecordOrder;       		/*  Record order. */
  public int pRecordParent;      		/*  Pointer to record parent. */
  public int fInvalidateRecord;  		/*  Update flag. */
  public int zOrder;             			/*  Record z-order. */
  public int cRecordsInsert;     			/*  Number of root-level structures. */
}
