package org.eclipse.swt.internal.pm;

/*
 * Copyright (c) 2002, 2005 EclipseOS2 Team.
 * This file is made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 */

public class MENUITEM {
  public short  iPosition;    /*  Position. */
  public short afStyle;      /*  Style. */
  public short afAttribute;  /*  Attribute. */
  public short id;           /*  Identity. */
  public int hwndSubMenu;  /*  Submenu. */
  public int hItem;        /*  Item. */
  public final static int sizeof = 16;
}
