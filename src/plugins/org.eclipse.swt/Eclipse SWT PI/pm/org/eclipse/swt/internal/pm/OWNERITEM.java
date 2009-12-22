package org.eclipse.swt.internal.pm;

/*
 * Copyright (c) 2002, 2005 EclipseOS2 Team.
 * This file is made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 */

public class OWNERITEM
{
  public int hwnd;            /*  Window handle. */
  public int hps;             /*  Presentation-space handle. */
  public int fsState;         /*  State. */
  public int fsAttribute;     /*  Attribute. */
  public int fsStateOld;      /*  Old state. */
  public int fsAttributeOld;  /*  Old attribute. */
  public RECTL rclItem = new RECTL();         /*  Item rectangle. */
  public int idItem;          /*  Item identity. */
  public int hItem;           /*  Item. */
  public final static int sizeof = 48;
}
