package org.eclipse.swt.widgets;

/*
 * OS/2 version.
 * Copyright (c) 2002, 2009 EclipseOS2 Team.
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
import org.eclipse.swt.events.*;

/**
 * Instances of the receiver represent a selectable user
 * interface object that present a range of continuous
 * numeric values.
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>HORIZONTAL, VERTICAL</dd>
 * <dt><b>Events:</b></dt>
 * <dd>Selection</dd>
 * </dl>
 * <p>
 * Note: Only one of the styles HORIZONTAL and VERTICAL may be specified.
 * </p><p>
 * <p>
 * IMPORTANT: This class is intended to be subclassed <em>only</em>
 * within the SWT implementation.
 * </p>
 */

public class Scale extends Control {
        int MAX_RANGE;
        int MIN_RANGE;
        int INCREMENT;
        int pixelRange;
	static final int TrackBarProc;
        static final PSZ TrackBarClass = PSZ.getAtom (OS.WC_SLIDER);
	static {
		CLASSINFO lpWndClass = new CLASSINFO ();
		OS.WinQueryClassInfo (OS.NULLHANDLE, TrackBarClass, lpWndClass);
		TrackBarProc = lpWndClass.pfnWindowProc;
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
 * @see SWT#HORIZONTAL
 * @see SWT#VERTICAL
 * @see Widget#checkSubclass
 * @see Widget#getStyle
 */
public Scale (Composite parent, int style) {
//	super (parent, checkStyle (style));
    this.parent = parent;
    this.style = style;
    MAX_RANGE = 100;
    MIN_RANGE = 0;
    INCREMENT = 10;

    SLDCDATA sldcData = new SLDCDATA();
    sldcData.usScale1Increments = 10;
    sldcData.usScale1Spacing = 0;

    handle = OS.WinCreateWindow (
        parent.handle,
        windowClass (),
        windowTitle (),
        widgetStyle (),
        0, parent.getHeight (), 0, 0,
        parent.handle,
        OS.HWND_BOTTOM,
        0,
        sldcData,
        0
    );
    if (handle == 0) error (SWT.ERROR_NO_HANDLES);
    
    OS.WinSetWindowUShort (handle, OS.QWS_ID, (short)(handle & 0xFFFF));
    OS.WinSendMsg(handle, OS.SLM_SETTICKSIZE, OS.MPFROM2SHORT((short)OS.SMA_SETALLTICKS, (short)6), 0);

    super.register ();
    super.subclass ();
    super.setDefaultFont ();
    super.setForegroundPixel (OS.CLR_DEFAULT);
    super.setBackgroundPixel (OS.CLR_DEFAULT);

}

/**
 * Adds the listener to the collection of listeners who will
 * be notified when the receiver's value changes, by sending
 * it one of the messages defined in the <code>SelectionListener</code>
 * interface.
 *
 * @param listener the listener which should be notified
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @see SelectionListener
 * @see #removeSelectionListener
 */
public void addSelectionListener(SelectionListener listener) {
	checkWidget ();
	if (listener == null) error (SWT.ERROR_NULL_ARGUMENT);
	TypedListener typedListener = new TypedListener (listener);
	addListener (SWT.Selection,typedListener);
	addListener (SWT.DefaultSelection,typedListener);
}

int callWindowProc (int msg, int mp1, int mp2) {
	if (handle == 0) return 0;
        return OS.WinCallWindowProc(TrackBarProc, handle, msg, mp1, mp2);
}

static int checkStyle (int style) {
	return checkBits (style, SWT.HORIZONTAL, SWT.VERTICAL, 0, 0, 0, 0);
}

//public Point computeSize (int wHint, int hHint, boolean changed) {
//	checkWidget ();
//	int border = getBorderWidth ();
//	int width = border * 2, height = border * 2;
//	RECT rect = new RECT ();
//	OS.SendMessage (handle, OS.TBM_GETTHUMBRECT, 0, rect);
//	if ((style & SWT.HORIZONTAL) != 0) {
//		width += OS.GetSystemMetrics (OS.SM_CXHSCROLL) * 10;
//		int scrollY = OS.GetSystemMetrics (OS.SM_CYHSCROLL);
//		height += (rect.top * 2) + scrollY + (scrollY / 3);
//	} else {
//		int scrollX = OS.GetSystemMetrics (OS.SM_CXVSCROLL);
//		width += (rect.left * 2) + scrollX + (scrollX / 3);
//		height += OS.GetSystemMetrics (OS.SM_CYVSCROLL) * 10;
//	}
//	if (wHint != SWT.DEFAULT) width = wHint + (border * 2);
//	if (hHint != SWT.DEFAULT) height = hHint + (border * 2);
//	return new Point (width, height);
//}

void createHandle () {
	super.createHandle ();
}

int defaultForeground () {
	return OS.WinQuerySysColor(handle, OS.SYSCLR_SCROLLBAR, 0);
}

///**
// * Returns the amount that the receiver's value will be
// * modified by when the up/down (or right/left) arrows
// * are pressed.
// *
// * @return the increment
// *
// * @exception SWTException <ul>
// *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
// *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
// * </ul>
// */
public int getIncrement () {
	checkWidget ();
        return 1;
//        return OS.WinSendMsg(handle, OS.SLM_QUERYSLIDERINFO, OS.MPFROM2SHORT((short)OS.SMA_SLIDERARMPOSITION, (short)OS.SMA_INCREMENTVALUE), 0);
//	return OS.SendMessage (handle, OS.TBM_GETLINESIZE, 0, 0);
}

///**
// * Returns the maximum value which the receiver will allow.
// *
// * @return the maximum
// *
// * @exception SWTException <ul>
// *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
// *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
// * </ul>
// */
public int getMaximum () {
	checkWidget ();
        return MAX_RANGE;
//	return OS.SendMessage (handle, OS.TBM_GETRANGEMAX, 0, 0);
}

///**
// * Returns the minimum value which the receiver will allow.
// *
// * @return the minimum
// *
// * @exception SWTException <ul>
// *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
// *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
// * </ul>
// */
public int getMinimum () {
	checkWidget ();
	return MIN_RANGE;
}

///**
// * Returns the amount that the receiver's value will be
// * modified by when the page increment/decrement areas
// * are selected.
// *
// * @return the page increment
// *
// * @exception SWTException <ul>
// *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
// *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
// * </ul>
// */
public int getPageIncrement () {
	checkWidget ();
        return INCREMENT;
//	return OS.SendMessage (handle, OS.TBM_GETPAGESIZE, 0, 0);
}

///**
// * Returns the single <em>selection</em> that is the receiver's position.
// *
// * @return the selection
// *
// * @exception SWTException <ul>
// *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
// *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
// * </ul>
// */
public int getSelection () {
	checkWidget ();
        int pixelInfo = OS.WinSendMsg(handle, OS.SLM_QUERYSLIDERINFO, OS.MPFROM2SHORT((short)OS.SMA_SLIDERARMPOSITION, (short)OS.SMA_RANGEVALUE ), 0);
        int posPixels = OS.SHORT1FROMMP(pixelInfo);
        int totalPixels = ((pixelInfo & 0xFFFF0000)>>16) & 0xFFFF;
        int pos = ((MAX_RANGE*posPixels)/totalPixels);
        return pos;
}

///**
// * Removes the listener from the collection of listeners who will
// * be notified when the receiver's value changes.
// *
// * @param listener the listener which should no longer be notified
// *
// * @exception IllegalArgumentException <ul>
// *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
// * </ul>
// * @exception SWTException <ul>
// *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
// *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
// * </ul>
// *
// * @see SelectionListener
// * @see #addSelectionListener
// */
public void removeSelectionListener(SelectionListener listener) {
	checkWidget ();
	if (listener == null) error (SWT.ERROR_NULL_ARGUMENT);
	if (eventTable == null) return;
	eventTable.unhook (SWT.Selection, listener);
	eventTable.unhook (SWT.DefaultSelection,listener);	
}

///**
// * Sets the amount that the receiver's value will be
// * modified by when the up/down (or right/left) arrows
// * are pressed to the argument, which must be at least 
// * one.
// *
// * @param value the new increment (must be greater than zero)
// *
// * @exception SWTException <ul>
// *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
// *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
// * </ul>
// */
public void setIncrement (int increment) {
	checkWidget ();
	if (increment < 1) return;
	int minimum = MIN_RANGE;
	int maximum = MAX_RANGE;
	if (increment > maximum - minimum) return;
	INCREMENT = increment;
}

///**
// * Sets the maximum value which the receiver will allow
// * to be the argument which must be greater than or
// * equal to zero.
// *
// * @param value the new maximum (must be zero or greater)
// *
// * @exception SWTException <ul>
// *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
// *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
// * </ul>
// */
public void setMaximum (int value) {
	checkWidget ();
//	int minimum = OS.SendMessage (handle, OS.TBM_GETRANGEMIN, 0, 0);
	if (0 <= MIN_RANGE && MIN_RANGE < value) {
		MAX_RANGE = value;
	}
}

///**
// * Sets the minimum value which the receiver will allow
// * to be the argument which must be greater than or
// * equal to zero.
// *
// * @param value the new minimum (must be zero or greater)
// *
// * @exception SWTException <ul>
// *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
// *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
// * </ul>
// */
public void setMinimum (int value) {
	checkWidget ();
//	int maximum = OS.SendMessage (handle, OS.TBM_GETRANGEMAX, 0, 0);
	if (0 <= value && value < MAX_RANGE) {
		MAX_RANGE = value;
	}
}

///**
// * Sets the amount that the receiver's value will be
// * modified by when the page increment/decrement areas
// * are selected to the argument, which must be at least
// * one.
// *
// * @return the page increment (must be greater than zero)
// *
// * @exception SWTException <ul>
// *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
// *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
// * </ul>
// */
public void setPageIncrement (int pageIncrement) {
	checkWidget ();
	if (pageIncrement < 1) return;
	int minimum = MIN_RANGE;
	int maximum = MAX_RANGE;
	if (pageIncrement > maximum - minimum) return;
        INCREMENT = pageIncrement;
//	OS.SendMessage (handle, OS.TBM_SETPAGESIZE, 0, pageIncrement);
//	OS.SendMessage (handle, OS.TBM_SETTICFREQ, pageIncrement, 0);
}

///**
// * Sets the single <em>selection</em> that is the receiver's
// * value to the argument which must be greater than or equal
// * to zero.
// *
// * @param value the new selection (must be zero or greater)
// *
// * @exception SWTException <ul>
// *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
// *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
// * </ul>
// */
public void setSelection (int value) {
	checkWidget ();
	if (value < 0) return;
        int pixels = OS.WinSendMsg(handle, OS.SLM_QUERYSLIDERINFO, OS.MPFROM2SHORT((short)OS.SMA_SLIDERARMPOSITION, (short)OS.SMA_RANGEVALUE ), 0);
        int totalPixels = ((pixels & 0xFFFF0000)>>16) & 0xFFFF;
        int pixelPos = ((value*totalPixels)/MAX_RANGE);
        OS.WinSendMsg(handle, OS.SLM_SETSLIDERINFO, OS.MPFROM2SHORT((short)OS.SMA_SLIDERARMPOSITION, (short)OS.SMA_RANGEVALUE), pixelPos);
}

int widgetStyle () {
	int bits = super.widgetStyle () | OS.SLS_CENTER | OS.SLS_HOMELEFT | OS.SLS_PRIMARYSCALE1;
	if ((style & SWT.VERTICAL) != 0) return bits | OS.SLS_VERTICAL | OS.SLS_HOMETOP;
	return bits;
}

PSZ windowClass () {
	return TrackBarClass;
}

int windowProc () {
	return TrackBarProc;
}

MRESULT wmControlChild (int mp1, int mp2) {
    int code = OS.SHORT2FROMMP(mp1);
    int pos = mp2;
    System.out.println("Pos = " + pos);
    Event event = new Event ();
//	switch (code) {
//		/*
//		* This line is intentionally commented.  Do not set the detail
//		* field to DRAG to indicate that the dragging has ended when the
//		* scroll bar is finally positioned in TB_THUMBPOSITION.
//		*/
////		case OS.TB_THUMBPOSITION: 	break;
//		case OS.TB_THUMBTRACK:		event.detail = SWT.DRAG;  break;
//		case OS.TB_TOP: 			event.detail = SWT.HOME;  break;
//		case OS.TB_BOTTOM:		event.detail = SWT.END;  break;
//		case OS.TB_LINEDOWN:		event.detail = SWT.ARROW_DOWN;  break;
//		case OS.TB_LINEUP: 		event.detail = SWT.ARROW_UP;  break;
//		case OS.TB_PAGEDOWN: 		event.detail = SWT.PAGE_DOWN;  break;
//		case OS.TB_PAGEUP: 		event.detail = SWT.PAGE_UP;  break;
//	}
    return null;
}

//MRESULT wmScrollChild (int mp1, int mp2) {
//	
//	/* Do nothing when scrolling is ending */
////	int code = wParam & 0xFFFF;
////	if (code == OS.TB_ENDTRACK) return null;
//	System.out.println("test");
//	/*
//	* This code is intentionally commented.  The event
//	* detail field is not currently supported on all
//	* platforms.
//	*/
//	Event event = new Event ();
//	
//	/*
//	* Send the event because WM_HSCROLL and WM_VSCROLL
//	* are sent from a modal message loop in windows that
//	* is active when the user is scrolling.
//	*/
//	sendEvent (SWT.Selection, event);
//	// widget could be disposed at this point
//	return null;
//}

}
