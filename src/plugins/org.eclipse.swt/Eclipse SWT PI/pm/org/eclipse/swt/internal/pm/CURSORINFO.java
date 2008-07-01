package org.eclipse.swt.internal.pm;

/*
 * Copyright (c) 2002, 2004 EclipseOS2 Team.
 * This file is made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 */

public class CURSORINFO
{
  public int hwnd;     /*  Window handle. */
  public int x;        /*  X-coordinate. */
  public int y;        /*  Y-coordinate. */
  public int cx;       /*  Cursor width. */
  public int cy;       /*  Cursor height. */
  public int fs;       /*  Options. */
  public RECTL rclClip = new RECTL();  /*  Cursor box. */
}
