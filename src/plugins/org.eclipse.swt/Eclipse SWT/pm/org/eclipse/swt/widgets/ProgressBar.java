package org.eclipse.swt.widgets;

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
 * Instances of the receiver represent is an unselectable
 * user interface object that is used to display progress,
 * typically in the form of a bar.
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>SMOOTH, HORIZONTAL, VERTICAL</dd>
 * <dt><b>Events:</b></dt>
 * <dd>(none)</dd>
 * </dl>
 * <p>
 * Note: Only one of the styles HORIZONTAL and VERTICAL may be specified.
 * </p><p>
 * IMPORTANT: This class is intended to be subclassed <em>only</em>
 * within the SWT implementation.
 * </p>
 */
public class ProgressBar extends Control {
        int maxRange = 100;
        int minRange = 0;
        int position;
	static final int ProgressBarProc;
        static final PSZ ProgressBarClass = PSZ.getAtom (OS.WC_SLIDER);
	static {
		CLASSINFO lpWndClass = new CLASSINFO ();
		OS.WinQueryClassInfo (OS.NULLHANDLE, ProgressBarClass, lpWndClass);
		ProgressBarProc = lpWndClass.pfnWindowProc;
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
* @see SWT#SMOOTH
* @see SWT#HORIZONTAL
* @see SWT#VERTICAL
* @see Widget#checkSubclass
* @see Widget#getStyle
*/
public ProgressBar (Composite parent, int style) {
	super (parent, checkStyle (style));
	OS.WinSendMsg(handle, OS.SLM_SETSLIDERINFO, OS.MPFROM2SHORT((short)OS.SMA_SHAFTDIMENSIONS, (short)0), 15);
}

int callWindowProc (int msg, int mp1, int mp2) {
	if (handle == 0) return 0;
        return OS.WinCallWindowProc(ProgressBarProc, handle, msg, mp1, mp2);
}

static int checkStyle (int style) {
	return checkBits (style, SWT.HORIZONTAL, SWT.VERTICAL, 0, 0, 0, 0);
}

public Point computeSize (int wHint, int hHint, boolean changed) {
	checkWidget ();
	int border = getBorderWidth ();
	int width = border * 2, height = border * 2;
        int shaftLength = OS.WinSendMsg(handle, OS.SLM_QUERYSLIDERINFO, OS.MPFROM2SHORT((short)OS.SMA_SHAFTDIMENSIONS, (short)OS.SMA_RANGEVALUE), 0);
        int arm = OS.WinSendMsg(handle, OS.SLM_QUERYSLIDERINFO, OS.MPFROM2SHORT((short)OS.SMA_SLIDERARMDIMENSIONS, (short)OS.SMA_RANGEVALUE), 0);
        int tick = OS.WinSendMsg(handle, OS.SLM_QUERYTICKSIZE, 1, 0);
	if ((style & SWT.HORIZONTAL) != 0) {
		width += OS.WinQuerySysValue (OS.HWND_DESKTOP, OS.SV_CXHSCROLLARROW ) * 10;
		height += OS.WinQuerySysValue (OS.HWND_DESKTOP, OS.SV_CYHSCROLL) + tick + OS.SHORT2FROMMP(arm);
	} else {
		width += OS.WinQuerySysValue (OS.HWND_DESKTOP, OS.SV_CXVSCROLL) + tick + OS.SHORT2FROMMP(arm);
		height += OS.WinQuerySysValue (OS.HWND_DESKTOP, OS.SV_CYVSCROLLARROW ) * 10;
	}
	if (wHint != SWT.DEFAULT) width = wHint + (border * 2);
	if (hHint != SWT.DEFAULT) height = hHint + (border * 2);
	return new Point (width, height);
}

void createHandle () {
	super.createHandle ();
}

int defaultForeground () {
	return OS.WinQuerySysColor(handle, OS.SYSCLR_WINDOWTEXT, 0);
}

/**
* Returns the maximum value which the receiver will allow.
*
* @return the maximum
*
* @exception SWTException <ul>
*    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
*    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
* </ul>
*/
public int getMaximum () {
	checkWidget ();
	return maxRange;
}

/**
* Returns the minimum value which the receiver will allow.
*
* @return the minimum
*
* @exception SWTException <ul>
*    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
*    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
* </ul>
*/
public int getMinimum () {
	checkWidget ();
	return minRange;
}

/**
* Returns the single <em>selection</em> that is the receiver's position.
*
* @return the selection
*
* @exception SWTException <ul>
*    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
*    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
* </ul>
*/
public int getSelection () {
	checkWidget ();
	return position;
}

//void setBackgroundPixel (int pixel) {
//	if (background == pixel) return;
//	background = pixel;
//	/*
//	* Feature in Windows.  Setting the color to be
//	* the current default is not correct because the
//	* widget will not change colors when the colors
//	* are changed from the control panel.  There is
//	* no fix at this time.
//	*/
//	if (pixel == -1) pixel = defaultBackground ();
//	OS.SendMessage (handle, OS.PBM_SETBKCOLOR, 0, pixel);
//}

public boolean setFocus () {
	checkWidget();
	return false;
}

public void setBounds (int x, int y, int width, int height) {
	OS.WinSendMsg(handle, OS.SLM_SETSLIDERINFO, OS.MPFROM2SHORT((short)OS.SMA_SHAFTDIMENSIONS, (short)0), height);
	super.setBounds(x, y, width, height);
}
//void setForegroundPixel (int pixel) {
//	if (foreground == pixel) return;
//	foreground = pixel;
//	/*
//	* Feature in Windows.  Setting the color to be
//	* the current default is not correct because the
//	* widget will not change colors when the colors
//	* are changed from the control panel.  There is
//	* no fix at this time.
//	*/
//	if (pixel == -1) pixel = defaultForeground ();
//	OS.SendMessage (handle, OS.PBM_SETBARCOLOR, 0, pixel);
//}
//
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
	if (0 <= minRange && minRange < value) {
		maxRange = value;
	}
}

/**
* Sets the minimum value which the receiver will allow
* to be the argument which must be greater than or
* equal to zero.
*
* @param value the new minimum (must be zero or greater)
*
* @exception SWTException <ul>
*    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
*    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
* </ul>
*/
public void setMinimum (int value) {
	checkWidget ();
	if (0 <= value && value < maxRange) {
		minRange = value;
	}
}

/**
* Sets the single <em>selection</em> that is the receiver's
* position to the argument which must be greater than or equal
* to zero.
*
* @param value the new selection (must be zero or greater)
*
* @exception SWTException <ul>
*    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
*    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
* </ul>
*/
public void setSelection (int value) {
	checkWidget ();
	if (value < 0) return;
        int pixels = OS.WinSendMsg(handle, OS.SLM_QUERYSLIDERINFO, OS.MPFROM2SHORT((short)OS.SMA_SHAFTDIMENSIONS, (short)OS.SMA_RANGEVALUE ), 0);
	int totalPixels = OS.SHORT1FROMMP(pixels);
        int pixelPos = ((value*totalPixels)/maxRange);
        OS.WinSendMsg(handle, OS.SLM_SETSLIDERINFO, OS.MPFROM2SHORT((short)OS.SMA_SLIDERARMPOSITION, (short)OS.SMA_RANGEVALUE), pixelPos);
}

public void setSize (int width, int height) {
	OS.WinSendMsg(handle, OS.SLM_SETSLIDERINFO, OS.MPFROM2SHORT((short)OS.SMA_SHAFTDIMENSIONS, (short)0), height);
	super.setSize(width, height);
}

int widgetStyle () {
	int bits = super.widgetStyle () | OS.SLS_CENTER | OS.WS_TABSTOP | OS.SLS_RIBBONSTRIP | OS.SLS_READONLY | OS.SLS_PRIMARYSCALE1 | OS.SLS_OWNERDRAW;
	if ((style & SWT.HORIZONTAL) != 0) return bits | OS.SLS_HORIZONTAL | OS.SLS_HOMELEFT; 
	return bits | OS.SLS_VERTICAL | OS.SLS_HOMETOP;
}

PSZ windowClass () {
	return ProgressBarClass;
}

int windowProc () {
	return ProgressBarProc;
}

MRESULT wmDrawChild (int mp1, int mp2) {
    OWNERITEM struct = new OWNERITEM();
    OS.objcpy(struct, mp2);
    System.out.println("ProgressBar:WM_DRAWITEM");
    return null;
/*
switch(SHORT1FROMMP(mp1)){
case SLD_VOLUME:
    powner = (POWNERITEM) mp2;
    switch(powner->idItem)
    {
	case SDA_RIBBONSTRIP:
	    sldmax = SHORT2FROMMR(WinSendDlgItemMsg (hwnd, SLD_VOLUME, SLM_QUERYSLIDERINFO, MPFROM2SHORT(SMA_SLIDERARMPOSITION, SMA_RANGEVALUE), MPVOID));
	    sldwidth = SHORT1FROMMR(WinSendDlgItemMsg (hwnd, SLD_VOLUME, SLM_QUERYSLIDERINFO, MPFROMSHORT(SMA_SLIDERARMDIMENSIONS), MPVOID));
	    rectmax = powner->rclItem;
	    rectmax.xRight = rectmax.xLeft + sldmax;
	    rectg = recty = rectr = rectmax;
	    rectg.xRight = recty.xLeft = rectmax.xLeft + (sldmax * 4) / 5 + (sldwidth / 2);
	    rectr.xLeft = recty.xRight = rectmax.xLeft + (sldmax * 8) / 9 + (sldwidth / 2);
	    WinFillRect (powner->hps, &rectg, CLR_GREEN);
	    WinFillRect (powner->hps, &recty, CLR_YELLOW);
	    WinFillRect (powner->hps, &rectr, CLR_RED);
	    return ((MRESULT) TRUE);
    }
}

break; */
}
}
