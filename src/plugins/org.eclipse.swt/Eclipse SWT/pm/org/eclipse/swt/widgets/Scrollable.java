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
 * This class is the abstract superclass of all classes which
 * represent controls that have standard scroll bars.
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>H_SCROLL, V_SCROLL</dd>
 * <dt><b>Events:</b>
 * <dd>(none)</dd>
 * </dl>
 * <p>
 * IMPORTANT: This class is intended to be subclassed <em>only</em>
 * within the SWT implementation.
 * </p>
 */

public abstract class Scrollable extends Control {
//@@TODO(dmik)
	ScrollBar horizontalBar, verticalBar;

/**
 * Prevents uninitialized instances from being created outside the package.
 */
Scrollable () {
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
 * @see SWT#H_SCROLL
 * @see SWT#V_SCROLL
 * @see Widget#checkSubclass
 * @see Widget#getStyle
 */
public Scrollable (Composite parent, int style) {
	super (parent, style);
}

int callWindowProc (int msg, int mp1, int mp2) {
//@@TODO(dmik): think of it
    if (handle == 0) return 0;
    return OS.WinDefWindowProc (handle, msg, mp1, mp2);
}

/**
 * Given a desired <em>client area</em> for the receiver
 * (as described by the arguments), returns the bounding
 * rectangle which would be required to produce that client
 * area.
 * <p>
 * In other words, it returns a rectangle such that, if the
 * receiver's bounds were set to that rectangle, the area
 * of the receiver which is capable of displaying data
 * (that is, not covered by the "trimmings") would be the
 * rectangle described by the arguments (relative to the
 * receiver's parent).
 * </p>
 * 
 * @return the required bounds to produce the given client area
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @see #getClientArea
 */
public Rectangle computeTrim (int x, int y, int width, int height) {
	checkWidget ();
	RECTL rect = new RECTL();
        rect.xLeft = x;
        rect.xRight = x + width;
        rect.yTop = y + height;
        rect.yBottom = y;
        OS.WinCalcFrameRect (handle, rect, false);
	if (horizontalBar != null) rect.yTop += OS.WinQuerySysValue (handle, OS.SV_CYHSCROLL);
	if (verticalBar != null) rect.xRight += OS.WinQuerySysValue (handle, OS.SV_CXVSCROLL);
	int nWidth = rect.xRight - rect.xLeft;
        int nHeight = rect.yTop - rect.yBottom;
	return new Rectangle (rect.xLeft, rect.yBottom, nWidth, nHeight);
}


ScrollBar createScrollBar (int type) {
	ScrollBar bar = new ScrollBar (this, type);
	if ((state & CANVAS) != 0) {
		bar.setMaximum (100);
		bar.setThumb (10);
	}
	return bar;
}

void createWidget () {
	super.createWidget ();
	if ((style & SWT.H_SCROLL) != 0) horizontalBar = createScrollBar (SWT.H_SCROLL);
	if ((style & SWT.V_SCROLL) != 0) verticalBar = createScrollBar (SWT.V_SCROLL);
	layoutBars ();
}

/**
 * Returns a rectangle which describes the area of the
 * receiver which is capable of displaying data (that is,
 * not covered by the "trimmings").
 * 
 * @return the client area
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @see #computeTrim
 */
public Rectangle getClientArea () {
	checkWidget ();
	if (parent != null && parent.pswp != 0) {
        endDeferWindowPos (parent);
        int count = parent.getChildrenCount ();
        beginDeferWindowPos (parent, count);
    }

    Rectangle rect = getBounds();
    rect.x = 0; rect.y = 0;
    /* The scroll bars of a canvas are windows in it (see ScrollBar): the client area is beside them */
    if (verticalBar != null && verticalBar.hwndBar != 0 && verticalBar.getVisible ()) {
        rect.width = Math.max (0, rect.width - OS.WinQuerySysValue (OS.HWND_DESKTOP, OS.SV_CXVSCROLL));
    }
    if (horizontalBar != null && horizontalBar.hwndBar != 0 && horizontalBar.getVisible ()) {
        rect.height = Math.max (0, rect.height - OS.WinQuerySysValue (OS.HWND_DESKTOP, OS.SV_CYHSCROLL));
    }
    return rect;
}

/*
 * Places the scroll bars of a canvas (windows of their own, see ScrollBar) on the right and
 * at the bottom of the window, the vertical one above the horizontal one.
 */
void layoutBars () {
	boolean vertical = verticalBar != null && verticalBar.hwndBar != 0 && verticalBar.getVisible ();
	boolean horizontal = horizontalBar != null && horizontalBar.hwndBar != 0 && horizontalBar.getVisible ();
	if (!vertical && !horizontal) return;
	RECTL rect = new RECTL ();
	OS.WinQueryWindowRect (handle, rect);
	int cx = OS.WinQuerySysValue (OS.HWND_DESKTOP, OS.SV_CXVSCROLL);
	int cy = OS.WinQuerySysValue (OS.HWND_DESKTOP, OS.SV_CYHSCROLL);
	int width = rect.xRight - rect.xLeft, height = rect.yTop - rect.yBottom;
	/* PM coordinates: y = 0 is the bottom */
	if (vertical) {
		int bottom = horizontal ? cy : 0;
		OS.WinSetWindowPos (verticalBar.hwndBar, 0, Math.max (0, width - cx), bottom, cx, Math.max (0, height - bottom), OS.SWP_MOVE | OS.SWP_SIZE);
	}
	if (horizontal) {
		OS.WinSetWindowPos (horizontalBar.hwndBar, 0, 0, 0, Math.max (0, width - (vertical ? cx : 0)), cy, OS.SWP_MOVE | OS.SWP_SIZE);
	}
}

/**
 * Returns the receiver's horizontal scroll bar if it has
 * one, and null if it does not.
 *
 * @return the horizontal scroll bar (or null)
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public ScrollBar getHorizontalBar () {
	checkWidget ();
	return horizontalBar;
}
//
///**
// * Returns the receiver's vertical scroll bar if it has
// * one, and null if it does not.
// *
// * @return the vertical scroll bar (or null)
// *
// * @exception SWTException <ul>
// *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
// *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
// * </ul>
// */
public ScrollBar getVerticalBar () {
	checkWidget ();
	return verticalBar;
}

void releaseWidget () {
    if (horizontalBar != null) horizontalBar.releaseWidget ();
    if (verticalBar != null) verticalBar.releaseWidget ();
    horizontalBar = verticalBar = null;
    super.releaseWidget ();
}

//@@TODO(dmik)
//int widgetExtStyle () {
//	return super.widgetExtStyle ();
//	/*
//	* This code is intentionally commented.  In future,
//	* we may wish to support different standard Windows
//	* edge styles.  The issue here is that not all of
//	* these styles are available on the other platforms
//	* this would need to be a hint.
//	*/
////	if ((style & SWT.BORDER) != 0) return OS.WS_EX_CLIENTEDGE;
////	if ((style & SWT.SHADOW_IN) != 0) return OS.WS_EX_STATICEDGE;
////	return super.widgetExtStyle ();
//}

int widgetStyle () {
	int bits = super.widgetStyle () | OS.WS_TABSTOP;    
	if ((style & SWT.H_SCROLL) != 0) bits |= OS.SBS_HORZ;
	if ((style & SWT.V_SCROLL) != 0) bits |= OS.SBS_VERT;
	return bits;
}

PSZ windowClass () {
    return getDisplay ().windowClass;
}

int windowProc () {
    return getDisplay ().windowProc;
}


MRESULT WM_HSCROLL (int mp1, int mp2) {
	/* mp1 = (id of the bar, 0): the bar of a canvas, or a child control */
	if (horizontalBar != null && horizontalBar.hwndBar != 0 && (OS.SHORT1FROMMP (mp1) & 0xFFFF) == OS.FID_HORZSCROLL) {
		horizontalBar.scrolled (mp2);
		return MRESULT.ZERO;
	}
	MRESULT result = super.WM_HSCROLL (mp1, mp2);
	if (result != null) return result;
	return result;
}


//@@TODO(lpino)
//LRESULT WM_MOUSEWHEEL (int wParam, int lParam) {
//	LRESULT result = super.WM_MOUSEWHEEL (wParam, lParam);
//	if (result != null) return result;
//	
//	/*
//	* Translate WM_MOUSEWHEEL to WM_VSCROLL or WM_HSCROLL.
//	*/
//	if ((state & CANVAS) != 0) {
//		if ((wParam & (OS.MK_SHIFT | OS.MK_CONTROL)) != 0) return result;
//		int delta = (short) (wParam >> 16);
//		int code = delta < 0 ? OS.SB_LINEDOWN : OS.SB_LINEUP;
//		delta = Math.abs (delta);
//		if (delta < OS.WHEEL_DELTA) return result;
//		if (verticalBar != null) {
//			int [] value = new int [1];
//   			OS.SystemParametersInfo (OS.SPI_GETWHEELSCROLLLINES, 0, value, 0);
//			int count = value [0] * delta / OS.WHEEL_DELTA;
//			for (int i=0; i<count; i++) {
//				OS.SendMessage (handle, OS.WM_VSCROLL, code, 0);
//			}
//			return LRESULT.ZERO;
//		}
//		if (horizontalBar != null) {
//			int count = delta / OS.WHEEL_DELTA;
//			for (int i=0; i<count; i++) {
//				OS.SendMessage (handle, OS.WM_HSCROLL, code, 0);
//			}
//			return LRESULT.ZERO;
//		}
//		return result;
//	}
//		
//	/*
//	* When the native widget scrolls inside WM_MOUSEWHEEL, it
//	* may or may not send a WM_VSCROLL or WM_HSCROLL to do the
//	* actual scrolling.  This depends on the implementation of
//	* each native widget.  In order to ensure that application
//	* code is notified when the scroll bar moves, compare the
//	* scroll bar position before and after the WM_MOUSEWHEEL.
//	* If the native control sends a WM_VSCROLL or WM_HSCROLL,
//	* then the application has already been notified.  If not
//	* explicity send the event.
//	*/
//	int vPosition = verticalBar == null ? 0 : verticalBar.getSelection ();
//	int hPosition = horizontalBar == null ? 0 : horizontalBar.getSelection ();
//	int code = callWindowProc (OS.WM_MOUSEWHEEL, wParam, lParam);
//	if (verticalBar != null) {
//		if (verticalBar.getSelection () != vPosition) {
//			Event event = new Event ();
//			event.detail = SWT.DRAG; 
//			verticalBar.sendEvent (SWT.Selection, event);
//		}
//	}
//	if (horizontalBar != null) {
//		if (horizontalBar.getSelection () != hPosition) {
//			Event event = new Event ();
//			event.detail = SWT.DRAG; 
//			horizontalBar.sendEvent (SWT.Selection, event);
//		}
//	}
//	return new LRESULT (code);
//}
//
MRESULT WM_SIZE (int mp1, int mp2) {
	layoutBars ();
	int code = callWindowProc (OS.WM_SIZE, mp1, mp2);
	super.WM_SIZE (mp1, mp2);
	// widget may be disposed at this point
	if (code == 0) return MRESULT.ZERO;
	return new MRESULT (code);
}

MRESULT WM_VSCROLL (int mp1, int mp2) {
	if (verticalBar != null && verticalBar.hwndBar != 0 && (OS.SHORT1FROMMP (mp1) & 0xFFFF) == OS.FID_VERTSCROLL) {
		verticalBar.scrolled (mp2);
		return MRESULT.ZERO;
	}
	MRESULT result = super.WM_VSCROLL (mp1, mp2);
	if (result != null) return result;
	return result;
}


//@@TODO(lpino)
MRESULT wmScroll (int msg, int mp1, int mp2) {
	int type = OS.SBS_HORZ;
	ScrollBar bar = horizontalBar;
	if (msg == OS.WM_VSCROLL) {
		type = OS.SBS_VERT;
		bar = verticalBar;
	}
	if (bar == null) return null;
        int nPos = bar.getSelection();
        int nMax = bar.getMaximum();
        int nMin = bar.getMinimum();
        
	switch (OS.SHORT2FROMMP(mp2)) {
		case OS.SB_ENDSCROLL:  return null;
		case OS.SB_SLIDERTRACK:
                case OS.SB_SLIDERPOSITION:
//		case OS.SB_TOP:
//			info.nPos = info.nMin;
//			break;
//		case OS.SB_BOTTOM:
//			info.nPos = info.nMax;
//			break;
		case OS.SB_LINEDOWN:
			nPos += bar.getIncrement ();
			break;
		case OS.SB_LINEUP:
			int increment = bar.getIncrement ();
			nPos = Math.max (nMin, nPos - increment);
			break;
		case OS.SB_PAGEDOWN:
			nPos += bar.getPageIncrement ();
			break;
		case OS.SB_PAGEUP:
			int pageIncrement = bar.getPageIncrement ();
			nPos = Math.max (nMin, nPos - pageIncrement);
			break;
	}
	OS.WinSendMsg (handle, OS.SBM_SETPOS, nPos, 0);
	return null;
}

}
