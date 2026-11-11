package org.eclipse.swt.widgets;

/*
 * OS/2 version.
 * Copyright (c) 2002, 2004 EclipseOS2 Team.
 */

/*
 * Copyright (c) 2000, 2002 IBM Corp.  All rights reserved.
 * This file is made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 */

import org.eclipse.swt.internal.pm.*;
import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;

/**
 * Instances of this class provide a surface for drawing
 * arbitrary graphics.
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>(none)</dd>
 * <dt><b>Events:</b></dt>
 * <dd>(none)</dd>
 * </dl>
 * <p>
 * This class may be subclassed by custom control implementors
 * who are building controls that are <em>not</em> constructed
 * from aggregates of other controls. That is, they are either
 * painted using SWT graphics calls or are handled by native
 * methods.
 * </p>
 *
 * @see Composite
 */

public class Canvas extends Composite {
	Caret caret;
	
/**
 * Prevents uninitialized instances from being created outside the package.
 */
Canvas () {
}

/**
 * Constructs a new instance of this class given its parent
 * and a style value describing its behavior and appearance.
 * <p>
 * The style value is either one of the style constants defined in
 * class <code>SWT</code> which is applicable to instances of this
 * class, or must be built by <em>bitwise OR</em>'ing together 
 * (that is, using the <code>int</code> "|" operator) two or more
 * of those <code>SWT</code> style constants. The class description
 * lists the style constants that are applicable to the class.
 * Style bits are also inherited from superclasses.
 * </p>
 *
 * @param parent a composite control which will be the parent of the new instance (cannot be null)
 * @param style the style of control to construct
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li>
 *    <li>ERROR_INVALID_SUBCLASS - if this class is not an allowed subclass</li>
 * </ul>
 *
 * @see SWT
 * @see Widget#checkSubclass
 * @see Widget#getStyle
 */
public Canvas (Composite parent, int style) {
	super (parent, style);
}

//@@TODO (dmik): 
///*
//* Not currently used.
//*/
//void clearArea (int x, int y, int width, int height) {
//	checkWidget ();
//	if (OS.IsWindowVisible (handle)) return;
//	RECT rect = new RECT ();
//	OS.SetRect (rect, x, y, x + width, y + height);
//	int hDC = OS.GetDCEx (handle, 0, OS.DCX_CACHE | OS.DCX_CLIPCHILDREN | OS.DCX_CLIPSIBLINGS);
//	drawBackground (hDC, rect);
//	OS.ReleaseDC (handle, hDC);
//}
//
/**
 * Returns the caret.
 * <p>
 * The caret for the control is automatically hidden
 * and shown when the control is painted or resized,
 * when focus is gained or lost and when an the control
 * is scrolled.  To avoid drawing on top of the caret,
 * the programmer must hide and show the caret when
 * drawing in the window any other time.
 * </p>
 *
 * @return the caret
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public Caret getCaret () {
	checkWidget ();
	return caret;
}

void releaseWidget () {
	if (caret != null) caret.releaseWidget ();
	caret = null;
	super.releaseWidget ();
}
//
/**
 * Scrolls a rectangular area of the receiver by first copying 
 * the source area to the destination and then causing the area
 * of the source which is not covered by the destination to
 * be repainted. Children that intersect the rectangle are
 * optionally moved during the operation. In addition, outstanding
 * paint events are flushed before the source area is copied to
 * ensure that the contents of the canvas are drawn correctly.
 *
 * @param destX the x coordinate of the destination
 * @param destY the y coordinate of the destination
 * @param x the x coordinate of the source
 * @param y the y coordinate of the source
 * @param width the width of the area
 * @param height the height of the area
 * @param all <code>true</code>if children should be scrolled, and <code>false</code> otherwise
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void scroll (int destX, int destY, int x, int y, int width, int height, boolean all) {
	checkWidget ();
		
	/* Remove the caret so it won't get scrolled */
	boolean isFocus = caret != null && caret.isFocusCaret ();
	if (isFocus) caret.killFocus ();

	/* Flush outstanding WM_PAINT's and scroll the window */
	OS.WinUpdateWindow (handle);
	/* The y of PM grows upwards: the rectangle is turned and so is the move */
	int windowHeight = getHeight ();
	RECTL rect = new RECTL ();
	rect.xLeft = x;  rect.xRight = x + width;
	rect.yTop = windowHeight - y;  rect.yBottom = windowHeight - (y + height);
	int deltaX = destX - x, deltaY = destY - y;
	OS.WinScrollWindow (handle, deltaX, -deltaY, rect, null, 0, null, OS.SW_INVALIDATERGN);

	/*
	* Windows moves the children that intersect the area. They are moved
	* here, so that it is the same whatever SW_SCROLLCHILDREN of PM does
	* with the ones out of the area.
	*/
	if (all) {
		Rectangle area = new Rectangle (x, y, width, height);
		Control [] children = _getChildren ();
		for (int i=0; i<children.length; i++) {
			Rectangle bounds = children [i].getBounds ();
			if (bounds.intersects (area)) {
				children [i].setLocation (bounds.x + deltaX, bounds.y + deltaY);
			}
		}
	}

	/* Restore the caret */
	if (isFocus) caret.setFocus ();
}
//
/**
 * Sets the receiver's caret.
 * <p>
 * The caret for the control is automatically hidden
 * and shown when the control is painted or resized,
 * when focus is gained or lost and when an the control
 * is scrolled.  To avoid drawing on top of the caret,
 * the programmer must hide and show the caret when
 * drawing in the window any other time.
 * </p>
 * @param caret the new caret for the receiver, may be null
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_INVALID_ARGUMENT - if the caret has been disposed</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void setCaret (Caret caret) {
	checkWidget ();
	Caret newCaret = caret;
	Caret oldCaret = this.caret;
	this.caret = newCaret;
	if (isFocusControl ()) {
		if (oldCaret != null) oldCaret.killFocus ();
		if (newCaret != null) {
			if (newCaret.isDisposed()) error(SWT.ERROR_INVALID_ARGUMENT);
			newCaret.setFocus ();
		}
	}
}

public void setFont (Font font) {
	checkWidget ();
	super.setFont (font);
	if (caret != null) caret.setFont (font);
}
//
/*
* PM has one message for both: the cursor of PM is created when the
* canvas gets the focus and destroyed when it loses it, as Windows does
* with WM_SETFOCUS and WM_KILLFOCUS.
*/
MRESULT WM_SETFOCUS (int mp1, int mp2) {
	MRESULT result = super.WM_SETFOCUS (mp1, mp2);
	if (caret != null && !caret.isDisposed ()) {
		if (OS.SHORT1FROMMP (mp2) > 0) {
			caret.setFocus ();
		} else {
			caret.killFocus ();
		}
	}
	return result;
}

}
