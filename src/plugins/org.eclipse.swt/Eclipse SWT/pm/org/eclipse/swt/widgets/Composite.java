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

import org.eclipse.swt.internal.*;
import org.eclipse.swt.internal.pm.*;
import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;

/**
 * Instances of this class are controls which are capable
 * of containing other controls.
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>NO_BACKGROUND, NO_FOCUS, NO_MERGE_PAINTS, NO_REDRAW_RESIZE, NO_RADIO_GROUP</dd>
 * <dt><b>Events:</b></dt>
 * <dd>(none)</dd>
 * </dl>
 * <p>
 * Note: The <code>NO_BACKGROUND</code>, <code>NO_FOCUS</code>, <code>NO_MERGE_PAINTS</code>,
 * and <code>NO_REDRAW_RESIZE</code> styles are intended for use with <code>Canvas</code>.
 * They can be used with <code>Composite</code> if you are drawing your own, but their
 * behavior is undefined if they are used with subclasses of <code>Composite</code> other
 * than <code>Canvas</code>.
 * </p><p>
 * This class may be subclassed by custom control implementors
 * who are building controls that are constructed from aggregates
 * of other controls.
 * </p>
 *
 * @see Canvas
 */

public class Composite extends Scrollable {
	Layout layout;
//@@TODO(dmik)    
//	int font, hdwp;
    // memory for deferred positioning of child windows
    int pswp;
    int [] pswpHandles;
    Control [] tabList;
    
/**
 * Prevents uninitialized instances from being created outside the package.
 */
Composite () {
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
 * @param parent a widget which will be the parent of the new instance (cannot be null)
 * @param style the style of widget to construct
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li>
 * </ul>
 *
 * @see SWT#NO_BACKGROUND
 * @see SWT#NO_FOCUS
 * @see SWT#NO_MERGE_PAINTS
 * @see SWT#NO_REDRAW_RESIZE
 * @see SWT#NO_RADIO_GROUP
 * @see Widget#getStyle
 */
public Composite (Composite parent, int style) {
    super (parent, style);
}

Control [] _getChildren () {
	int count = 0;
	int hwndChild = OS.WinQueryWindow (handle, OS.QW_TOP);
	if (hwndChild == OS.NULLHANDLE) return new Control [0];
	while (hwndChild != OS.NULLHANDLE) {
		count++;
		hwndChild = OS.WinQueryWindow (hwndChild, OS.QW_NEXT);
	}
	Control [] children = new Control [count];
	int index = 0;
	hwndChild = OS.WinQueryWindow (handle, OS.QW_TOP);
	while (hwndChild != OS.NULLHANDLE) {
		Control control = WidgetTable.get (hwndChild);
		if (control != null && control != this) {
			children [index++] = control;
		}
		hwndChild = OS.WinQueryWindow (hwndChild, OS.QW_NEXT);
	}
	if (count == index) return children;
	Control [] newChildren = new Control [index];
	System.arraycopy (children, 0, newChildren, 0, index);
	return newChildren;
}

Control [] _getTabList () {
    if (tabList == null) return tabList;
    int count = 0;
    for (int i=0; i<tabList.length; i++) {
        if (!tabList [i].isDisposed ()) count++;
    }
    if (count == tabList.length) return tabList;
    Control [] newList = new Control [count];
    int index = 0;
    for (int i=0; i<tabList.length; i++) {
        if (!tabList [i].isDisposed ()) {
            newList [index++] = tabList [i];
        }
    }
    tabList = newList;
    return tabList;
}

protected void checkSubclass () {
    /* Do nothing - Subclassing is allowed */
}

Control [] computeTabList () {
	Control result [] = super.computeTabList ();
	if (result.length == 0) return result;
	Control [] list = tabList != null ? _getTabList () : _getChildren ();
	for (int i=0; i<list.length; i++) {
		Control child = list [i];
		Control [] childList = child.computeTabList ();
		if (childList.length != 0) {
			Control [] newResult = new Control [result.length + childList.length];
			System.arraycopy (result, 0, newResult, 0, result.length);
			System.arraycopy (childList, 0, newResult, result.length, childList.length);
			result = newResult;
		}
	}
	return result;
}

public Point computeSize (int wHint, int hHint, boolean changed) {
	checkWidget ();
	Point size;
	if (layout != null) {
		if (wHint == SWT.DEFAULT || hHint == SWT.DEFAULT) {
			size = layout.computeSize (this, wHint, hHint, changed);
		} else {
			size = new Point (wHint, hHint);
		}
	} else {
		size = minimumSize ();
	}
	if (size.x == 0) size.x = DEFAULT_WIDTH;
	if (size.y == 0) size.y = DEFAULT_HEIGHT;
	if (wHint != SWT.DEFAULT) size.x = wHint;
	if (hHint != SWT.DEFAULT) size.y = hHint;
	Rectangle trim = computeTrim (0, 0, size.x, size.y);
	return new Point (trim.width, trim.height);
}

void createHandle () {
    super.createHandle ();
    state |= CANVAS;
}

void drawBackground (int hps, RECTL rcl) {
    if ((state & CANVAS) != 0) {
        if ((style & SWT.NO_BACKGROUND) != 0) return;
    }
    super.drawBackground (hps, rcl);
}

/**
 * Returns an array containing the receiver's children.
 * <p>
 * Note: This is not the actual structure used by the receiver
 * to maintain its list of children, so modifying the array will
 * not affect the receiver. 
 * </p>
 *
 * @return an array of children
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public Control [] getChildren () {
	checkWidget ();
	return _getChildren ();
}

int getChildrenCount () {
	/*
	* NOTE: The current implementation will count
	* non-registered children.
	*/
	int count = 0;
	int hwndChild = OS.WinQueryWindow (handle, OS.QW_TOP);
	while (hwndChild != OS.NULLHANDLE) {
		count++;
		hwndChild = OS.WinQueryWindow (hwndChild, OS.QW_NEXT);
	}
	return count;
}

/**
 * Returns layout which is associated with the receiver, or
 * null if one has not been set.
 *
 * @return the receiver's layout or null
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public Layout getLayout () {
	checkWidget ();
	return layout;
}

/**
 * Gets the last specified tabbing order for the control.
 *
 * @return tabList the ordered list of controls representing the tab order
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @see #setTabList
 */
public Control [] getTabList () {
	checkWidget ();
	Control [] tabList = _getTabList ();
	if (tabList == null) {
		int count = 0;
		Control [] list =_getChildren ();
		for (int i=0; i<list.length; i++) {
			if (list [i].isTabGroup ()) count++;
		}
		tabList = new Control [count];
		int index = 0;
		for (int i=0; i<list.length; i++) {
			if (list [i].isTabGroup ()) {
				tabList [index++] = list [i];
			}
		}
	}
	return tabList;
}

/**
 * If the receiver has a layout, asks the layout to <em>lay out</em>
 * (that is, set the size and location of) the receiver's children. 
 * If the receiver does not have a layout, do nothing.
 * <p>
 * This is equivalent to calling <code>layout(true)</code>.
 * </p>
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void layout () {
	checkWidget ();
	layout (true);
}

/**
 * If the receiver has a layout, asks the layout to <em>lay out</em>
 * (that is, set the size and location of) the receiver's children. 
 * If the the argument is <code>true</code> the layout must not rely
 * on any cached information it is keeping about the children. If it
 * is <code>false</code> the layout may (potentially) simplify the
 * work it is doing by assuming that the state of the none of the
 * receiver's children has changed since the last layout.
 * If the receiver does not have a layout, do nothing.
 *
 * @param changed <code>true</code> if the layout must flush its caches, and <code>false</code> otherwise
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void layout (boolean changed) {
	checkWidget ();
	if (layout == null) return;
	int count = getChildrenCount ();
	if (count == 0) return;
//@@TODO(dmik): remove    
//	if (count > 1 && hdwp == 0) {
//		hdwp = OS.BeginDeferWindowPos (count);
//	}
    if (count > 1 && pswp == 0) {
        beginDeferWindowPos (this, count);
	}
	layout.layout (this, changed);
//@@TODO(dmik): remove
//	int oldHdwp = hdwp;
//	hdwp = 0;
//	if (oldHdwp != 0) OS.EndDeferWindowPos (oldHdwp);
    if (pswp != 0) {
        endDeferWindowPos (this);
    }
}

Point minimumSize () {
	Control [] children = _getChildren ();
	int width = 0, height = 0;
	for (int i=0; i<children.length; i++) {
		Rectangle rect = children [i].getBounds ();
		width = Math.max (width, rect.x + rect.width);
		height = Math.max (height, rect.y + rect.height);
	}
	return new Point (width, height);
}

void releaseChildren () {
	Control [] children = _getChildren ();
	for (int i=0; i<children.length; i++) {
		Control child = children [i];
		if (!child.isDisposed ()) {
			child.releaseWidget ();
			child.releaseHandle ();
		}
	}
}

void releaseWidget () {
//@@TODO (dmik): remove    
//	int oldHdwp = hdwp;
//	hdwp = 0;
//	if (oldHdwp != 0) OS.EndDeferWindowPos (oldHdwp);
    if (pswp != 0) {
        endDeferWindowPos (this);
    }
	releaseChildren ();
	super.releaseWidget ();
	layout = null;
	tabList = null;
}

public boolean setFocus () {
	checkWidget ();
	if ((style & SWT.NO_FOCUS) != 0) return false;
	Control [] children = _getChildren ();
	for (int i=0; i<children.length; i++) {
		Control child = children [i];
		if (child.setRadioFocus ()) return true;
	}
	for (int i=0; i<children.length; i++) {
		Control child = children [i];
		if (child.setFocus ()) return true;
	}
	return super.setFocus ();
}

/**
 * Sets the layout which is associated with the receiver to be
 * the argument which may be null.
 *
 * @param layout the receiver's new layout or null
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void setLayout (Layout layout) {
	checkWidget ();
	this.layout = layout;
}

/**
 * Sets the tabbing order for the specified controls to
 * match the order that they occur in the argument list.
 *
 * @param tabList the ordered list of controls representing the tab order or null
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_INVALID_ARGUMENT - if a widget in the tabList is null or has been disposed</li>
 *    <li>ERROR_INVALID_PARENT - if widget in the tabList is not in the same widget tree</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void setTabList (Control [] tabList) {
	checkWidget ();
	if (tabList != null) {
		for (int i=0; i<tabList.length; i++) {
			Control control = tabList [i];
			if (control == null) error (SWT.ERROR_INVALID_ARGUMENT);
			if (control.isDisposed ()) error (SWT.ERROR_INVALID_ARGUMENT);
			/*
			* This code is intentionally commented.
			* Tab lists are currently only supported
			* for the direct children of a composite.
			*/
//			Shell shell = control.getShell ();
//			while (control != shell && control != this) {
//				control = control.parent;
//			}
//			if (control != this) error (SWT.ERROR_INVALID_PARENT);
			if (control.parent != this) error (SWT.ERROR_INVALID_PARENT);
		}
		Control [] newList = new Control [tabList.length];
		System.arraycopy (tabList, 0, newList, 0, tabList.length);
		tabList = newList;
	}
	this.tabList = tabList;
}

boolean setTabGroupFocus () {
	if (isTabItem ()) return setTabItemFocus ();
	if ((style & SWT.NO_FOCUS) == 0) {
		boolean takeFocus = true;
		if ((state & CANVAS) != 0) {
			takeFocus = hooks (SWT.KeyDown) || hooks (SWT.KeyUp);
		}
		if (takeFocus && setTabItemFocus ()) return true;
	}
	Control [] children = _getChildren ();
	for (int i=0; i<children.length; i++) {
		Control child = children [i];
		if (child.isTabItem () && child.setRadioFocus ()) return true;
	}
	for (int i=0; i<children.length; i++) {
		Control child = children [i];
		if (child.isTabItem () && child.setTabItemFocus ()) return true;
	}
	return false;
}

boolean setTabItemFocus () {
	if ((style & SWT.NO_FOCUS) == 0) {
		boolean takeFocus = true;
		if ((state & CANVAS) != 0) {
			takeFocus = hooks (SWT.KeyDown) || hooks (SWT.KeyUp);
		}
		if (takeFocus) {
			if (!isShowing ()) return false;
			if (forceFocus ()) return true;
		}
	}
	return super.setTabItemFocus ();
}

//@@TODO (dmik): later
//String toolTipText (NMTTDISPINFO hdr) {
//	if ((hdr.uFlags & OS.TTF_IDISHWND) == 0) {
//		return null;
//	}
//	int hwnd = hdr.idFrom;
//	if (hwnd == 0) return null;
//	Control control = WidgetTable.get (hwnd);
//	if (control == null) return null;
//	return control.toolTipText;
//}

boolean translateMnemonic (char key) {
	if (super.translateMnemonic (key)) return true;
	Control [] children = _getChildren ();
	for (int i=0; i<children.length; i++) {
		Control child = children [i];
		if (child.translateMnemonic (key)) return true;
	}
	return false;
}

//@@TODO (dmik): later
//void updateFont (Font oldFont, Font newFont) {
//	Control [] children = _getChildren ();
//	for (int i=0; i<children.length; i++) {
//		Control control = children [i];
//		if (!control.isDisposed ()) {
//			control.updateFont (oldFont, newFont);
//		}
//	}
//	super.updateFont (oldFont, newFont);
//	layout (true);
//}

int widgetStyle () {
	/* Temporary code to force SWT.CLIP_SIBLINGS */
	return super.widgetStyle ();// | OS.WS_CLIPCHILDREN;
}

MRESULT WM_CALCVALIDRECTS (int mp1, int mp2) {
    int result = callWindowProc (OS.WM_CALCVALIDRECTS, mp1, mp2);
    if ((state & CANVAS) != 0) {
		if ((style & SWT.NO_REDRAW_RESIZE) == 0) {
			if (hooks (SWT.Paint)) {
                OS.WinInvalidateRect (handle, null, false);
			}
		}
	}
    return new MRESULT (result);
}

//@@TODO(dmik): think of it, not sure that it is processed in the right way here...
//  And may be better to move this code to Decorations class...
MRESULT WM_ERASEBACKGROUND (int mp1, int mp2) {
    if ((state & CANVAS) != 0) {
        return super.WM_ERASEBACKGROUND (mp1, mp2);
    }
    RECTL rcl = new RECTL();
    OS.objcpy (rcl, mp2);
    drawBackground (mp1, rcl);
    return MRESULT.FALSE;
}

MRESULT WM_QUERYDLGCODE (int mp1, int mp2) {
	MRESULT result = super.WM_QUERYDLGCODE (mp1, mp2);
	if (result != null) return result;
	if ((state & CANVAS) != 0) {
		if ((style & SWT.NO_FOCUS) != 0) return new MRESULT (OS.DLGC_STATIC);
		if (hooks (SWT.KeyDown) || hooks (SWT.KeyUp)) {
			int flags = OS.DLGC_MLE;
			return new MRESULT (flags);
		}
		int count = getChildrenCount ();
		if (count != 0) return new MRESULT (OS.DLGC_STATIC);
	}
	return result;
}

//@@TODO (dmik): later
//LRESULT WM_GETFONT (int wParam, int lParam) {
//	LRESULT result = super.WM_GETFONT (wParam, lParam);
//	if (result != null) return result;
//	int code = callWindowProc (OS.WM_GETFONT, wParam, lParam);
//	if (code != 0) return new LRESULT (code);
//	if (font == 0) font = defaultFont ();
//	return new LRESULT (font);
//}
//
//LRESULT WM_LBUTTONDOWN (int wParam, int lParam) {
//	LRESULT result = super.WM_LBUTTONDOWN (wParam, lParam);
//
//	/* Set focus for a canvas with no children */
//	if ((state & CANVAS) != 0) {
//		if ((style & SWT.NO_FOCUS) != 0) return result;
//		if (OS.GetWindow (handle, OS.GW_CHILD) == 0) setFocus ();
//	}
//	return result;
//}
//
//LRESULT WM_NOTIFY (int wParam, int lParam) {
//	if (!OS.IsWinCE) {
//		NMHDR hdr = new NMHDR ();
//		OS.MoveMemory (hdr, lParam, NMHDR.sizeof);
//		switch (hdr.code) {
//			/*
//			* Bug in Windows 98.  For some reason, the tool bar control
//			* sends both TTN_GETDISPINFOW and TTN_GETDISPINFOA to get
//			* the tool tip text and the tab folder control sends only 
//			* TTN_GETDISPINFOW.  The fix is to handle only TTN_GETDISPINFOW,
//			* even though it should never be sent on Windows 98.
//			*
//			* NOTE:  Because the size of NMTTDISPINFO differs between
//			* Windows 98 and NT, guard against the case where the wrong
//			* kind of message occurs by copy inlining the memory moves
//			* and UNICODE conversion code.
//			*/
//			case OS.TTN_GETDISPINFOA: {
//				NMTTDISPINFO lpnmtdi = new NMTTDISPINFO ();
//				OS.MoveMemoryA (lpnmtdi, lParam, NMTTDISPINFO.sizeofA);
//				String string = toolTipText (lpnmtdi);
//				if (string != null && string.length () != 0) {
//					Shell shell = getShell ();
//					string = Display.withCrLf (string);
//					int length = string.length ();
//					char [] chars = new char [length];
//					string.getChars (0, length, chars, 0);
//					byte [] bytes = new byte [chars.length * 2 + 1];
//					OS.WideCharToMultiByte (OS.CP_ACP, 0, chars, chars.length, bytes, bytes.length, null, null);
//					shell.setToolTipText (lpnmtdi, bytes);
//					OS.MoveMemoryA (lParam, lpnmtdi, NMTTDISPINFO.sizeofA);
//					return LRESULT.ZERO;
//				}
//				break;
//			}
//			case OS.TTN_GETDISPINFOW: {
//				NMTTDISPINFO lpnmtdi = new NMTTDISPINFO ();
//				OS.MoveMemoryW (lpnmtdi, lParam, NMTTDISPINFO.sizeofW);
//				String string = toolTipText (lpnmtdi);
//				if (string != null && string.length () != 0) {
//					Shell shell = getShell ();
//					string = Display.withCrLf (string);
//					int length = string.length ();
//					char [] buffer = new char [length + 1];
//					string.getChars (0, length, buffer, 0);
//					shell.setToolTipText (lpnmtdi, buffer);
//					OS.MoveMemoryW (lParam, lpnmtdi, NMTTDISPINFO.sizeofW);
//					return LRESULT.ZERO;
//				}
//				break;
//			}
//		}
//	}
//	return super.WM_NOTIFY (wParam, lParam);
//}

MRESULT WM_PAINT (int mp1, int mp2) {
    if ((state & CANVAS) == 0) {
		return super.WM_PAINT (mp1, mp2);
	}

    /*
	* This code is intentionally commented.  Don't exit
	* early because the background must still be painted,
	* even though no application code will be painting
	* the widget.
	*
	* Do not uncomment this code.
	*/
//	if (!hooks (SWT.Paint)) return null;
    /* lazily obtain a long-term cached micro presentation space */
	GCData data = new GCData ();
    if (hps == 0) {
        hps = internal_new_GC (data);
        if (hps == 0) SWT.error(SWT.ERROR_NO_HANDLES);
        data.doInit = true;
    }

	/* Get the damage */
    int [] rgnRects = null;
	boolean isComplex = false;
	boolean exposeRegion = false;
	if ((style & SWT.NO_MERGE_PAINTS) != 0) {
        int hrgn = OS.GpiCreateRegion (hps, 0, null);
        isComplex = OS.WinQueryUpdateRegion (handle, hrgn) == OS.RGN_COMPLEX;
        if (isComplex) {
            RGNRECT rgnctl = new RGNRECT();
            rgnctl.ircStart = 1;
            rgnctl.ulDirection = OS.RECTDIR_LFRT_TOPBOT;
            exposeRegion = OS.GpiQueryRegionRects (hps, hrgn, null, rgnctl, null);
            if (exposeRegion) {
                rgnRects = new int [rgnctl.crcReturned * 4];
                rgnctl.crc = rgnctl.crcReturned;
                exposeRegion = OS.GpiQueryRegionRects (hps, hrgn, null, rgnctl, rgnRects);
            }
        }
        OS.GpiDestroyRegion (hps, hrgn);
    }

	/* Set the clipping bits */
	int oldBits = 0;
    oldBits = OS.WinQueryWindowULong (handle, OS.QWL_STYLE);
    int newBits = oldBits | OS.WS_CLIPSIBLINGS | OS.WS_CLIPCHILDREN;	
    OS.WinSetWindowULong (handle, OS.QWL_STYLE, newBits);

	/* Create the paint GC */
    data.rcl = new RECTL ();
    data.hps = hps;
    GC gc = GC.pm_new (this, data);
//@@TODO(dmik): WinBeginPaint should always return the same hps
//  as passed to it if this hps is not null and valid.
//    int ahps = gc.handle;
    int height = getHeight ();
	
    /*
     *  Feature in OS/2. When resizing a window that doesn't have the
     *  CS_SIZEREDRAW flag, it receives WM_PAINT messages even when getting
     *  smaller, with its update rectangle being empty. So, detect this
     *  situation and don't send an SWT.Paint event.
     */
    if ((style & SWT.NO_REDRAW_RESIZE) == 0 ||
        (data.rcl.xRight != data.rcl.xLeft) ||
        (data.rcl.yTop != data.rcl.yBottom))
    {
        /* Send the paint event */
        Event event = new Event ();
        event.gc = gc;
        if (isComplex && exposeRegion) {
            RECTL rcl = new RECTL ();
            int[] hrgnClip = new int[] {OS.NULLHANDLE};
            int nCount = rgnRects.length / 4;
            for (int i = 0; i < nCount; i++) {
                if (i > 0) {
                    /*
                     *  Deselect the clip region. After the last paint event
                     *  it will be deselected by gc.dispose().
                     */
                    OS.GpiSetClipRegion (hps, OS.NULLHANDLE, hrgnClip);
                }
                rcl.xLeft = rgnRects [(i << 2)];
                rcl.yBottom = rgnRects [(i << 2) + 1];
                rcl.xRight = rgnRects [(i << 2) + 2];
                rcl.yTop = rgnRects [(i << 2) + 3];
                drawBackground (0, rcl);
                event.x = rcl.xLeft;
                event.y = height - rcl.yTop;
                event.width = rcl.xRight - rcl.xLeft;
                event.height = rcl.yTop - rcl.yBottom;
    			event.count = nCount - 1 - i;
                /*
                 *  Restore the clip region if was set by the last paint listener.
                 *  Although preserving the custom region between to pseudo
                 *  paint events seems to be wrong behavior (it is not preserved
                 *  between two normal paint events that is logically correct)
                 *  this is done for compatibility with Windows version.
                 *
                 *  Note that other GC attributes (color, font) are also
                 *  preserved (i.e. not being reset to acceptable defaults)
                 *  between pseudo paint events for the same reason.
                 */
                if (hrgnClip[0] != OS.NULLHANDLE) {
                    OS.GpiSetClipRegion (hps, hrgnClip[0], hrgnClip);
                }
                /*
    			* It is possible (but unlikely), that application
    			* code could have disposed the widget in the paint
    			* event.  If this happens, attempt to give back the
    			* paint GC anyways because this is a scarce Windows
    			* resource.
    			*/
    			sendEvent (SWT.Paint, event);
                if (isDisposed ()) break;
            }
        } else {
            drawBackground (0, data.rcl);
            event.x = data.rcl.xLeft;
            event.y = height - data.rcl.yTop;
            event.width = data.rcl.xRight - data.rcl.xLeft;
            event.height = data.rcl.yTop - data.rcl.yBottom;
            sendEvent (SWT.Paint, event);
        }
        event.gc = null;
    }
    // widget could be disposed at this point

	/* Dispose the paint GC */
	gc.dispose ();

    /*
    * It is possible (but unlikely), that application
    * code could have disposed the widget in the paint
    * event.  If this happens, don't attempt to restore
    * the style.
    */
    if (!isDisposed ()) {
        OS.WinSetWindowULong (handle, OS.QWL_STYLE, oldBits);
    }
	return MRESULT.ZERO;
}

//@@TODO(dmik)
//LRESULT WM_SETFONT (int wParam, int lParam) {
//	return super.WM_SETFONT (font = wParam, lParam);
//}

MRESULT WM_SIZE (int mp1, int mp2) {
	/*
	* Begin deferred window positioning
	*/
	if (pswp == 0) {
    	int count = getChildrenCount ();
        if (count > 1) beginDeferWindowPos (this, count);
	}
    
	/* Layout and resize */
	if (layout != null) layout.layout (this, false);
    
    MRESULT result = super.WM_SIZE (mp1, mp2);
	
	/*
	* It is possible (but unlikely), that application
	* code could have disposed the widget in the resize
	* event.  If this happens, end the processing of the
	* Windows message by returning the result of the
	* WM_SIZE message.
	*/
	if (isDisposed ()) return result;
	
	/* End deferred window positioning */
    if (pswp != 0) {
        endDeferWindowPos (this);
    }
	
//@@TODO(dmik): We should not handle NO_REDRAW_RESIZE here,
//  it is handled in WM_CALCVALIDRECTS. Remove.
//
//	/* Damage the widget to cause a repaint */
//	if ((state & CANVAS) != 0) {
//		if ((style & SWT.NO_REDRAW_RESIZE) == 0) {
//			if (hooks (SWT.Paint)) {
//				OS.InvalidateRect (handle, null, true);
//			}
//		}
//	}
	return result;
}

//LRESULT WM_SYSCOLORCHANGE (int wParam, int lParam) {
//	Control [] children = _getChildren ();
//	for (int i=0; i<children.length; i++) {
//		int hwndChild = children [i].handle;
//		OS.SendMessage (hwndChild, OS.WM_SYSCOLORCHANGE, 0, 0);
//	}
//	return null;
//}
//
//LRESULT WM_SYSCOMMAND (int wParam, int lParam) {
//	LRESULT result = super.WM_SYSCOMMAND (wParam, lParam);
//	if (result != null) return result;
//		
//	/*
//	* Check to see if the command is a system command or
//	* a user menu item that was added to the system menu.
//	*/
//	if ((wParam & 0xF000) == 0) return result;
//
//	/*
//	* Bug in Windows.  When a vertical or horizontal scroll bar is
//	* hidden or shown while the opposite scroll bar is being scrolled
//	* by the user (with WM_HSCROLL code SB_LINEDOWN), the scroll bar
//	* does not redraw properly.  The fix is to detect this case and
//	* redraw the non-client area.
//	*/
//	if (!OS.IsWinCE) {
//		int cmd = wParam & 0xFFF0;
//		switch (cmd) {
//			case OS.SC_HSCROLL:
//			case OS.SC_VSCROLL:
//				boolean showHBar = horizontalBar != null && horizontalBar.getVisible ();
//				boolean showVBar = verticalBar != null && verticalBar.getVisible ();
//				int code = callWindowProc (OS.WM_SYSCOMMAND, wParam, lParam);
//				if ((showHBar != (horizontalBar != null && horizontalBar.getVisible ())) ||
//					(showVBar != (verticalBar != null && verticalBar.getVisible ()))) {
//						int flags = OS.RDW_FRAME | OS.RDW_INVALIDATE | OS.RDW_UPDATENOW;
//						OS.RedrawWindow (handle, null, 0, flags);
//					}		
//				if (code == 0) return LRESULT.ZERO;
//				return new LRESULT (code);
//		}
//	}
//	/* Return the result */
//	return result;
//}

}
