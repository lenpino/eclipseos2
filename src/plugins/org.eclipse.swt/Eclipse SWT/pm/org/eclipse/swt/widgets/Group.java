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
 * Instances of this class provide an etched border
 * with an optional title.
 * <p>
 * Shadow styles are hints and may not be honoured
 * by the platform.  To create a group with the
 * default shadow style for the platform, do not
 * specify a shadow style.
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>SHADOW_ETCHED_IN, SHADOW_ETCHED_OUT, SHADOW_IN, SHADOW_OUT, SHADOW_NONE</dd>
 * <dt><b>Events:</b></dt>
 * <dd>(none)</dd>
 * </dl>
 * <p>
 * Note: Only one of the above styles may be specified.
 * </p><p>
 * IMPORTANT: This class is <em>not</em> intended to be subclassed.
 * </p>
 */

public class Group extends Composite {
        String text = "";
	static final int GroupProc;
        static final PSZ GroupClass =  PSZ.getAtom (OS.WC_STATIC);
	static {
              CLASSINFO pclsiClassInfo = new CLASSINFO ();
            OS.WinQueryClassInfo (OS.NULLHANDLE, GroupClass, pclsiClassInfo);
            GroupProc = pclsiClassInfo.pfnWindowProc;
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
 * @see SWT#SHADOW_ETCHED_IN
 * @see SWT#SHADOW_ETCHED_OUT
 * @see SWT#SHADOW_IN
 * @see SWT#SHADOW_OUT
 * @see SWT#SHADOW_NONE
 * @see Widget#checkSubclass
 * @see Widget#getStyle
 */
public Group (Composite parent, int style) {
	super (parent, checkStyle (style));
}

int callWindowProc (int msg, int mp1, int mp2) {
	if (handle == 0) return 0;
        return OS.WinCallWindowProc (GroupProc, handle, msg, mp1, mp2);
}

static int checkStyle (int style) {
	style |= SWT.NO_FOCUS;
	/*
	* Even though it is legal to create this widget
	* with scroll bars, they serve no useful purpose
	* because they do not automatically scroll the
	* widget's client area.  The fix is to clear
	* the SWT style.
	*/
	return style & ~(SWT.H_SCROLL | SWT.V_SCROLL);
}

protected void checkSubclass () {
	if (!isValidSubclass ()) error (SWT.ERROR_INVALID_SUBCLASS);
}

byte[] computeWindowTextSize (int hps, FONTMETRICS fm, Point size) {
    size.x = 0;
    size.y = fm.lMaxBaselineExt;
    int length = OS.WinQueryWindowTextLength (handle);
    byte[] text = null;
    if (length != 0) {
        PSZ buffer = new PSZ (length);
        OS.WinQueryWindowText (handle, length + 1, buffer);
        RECTL rect = new RECTL();
        text = buffer.getBytes();
        int[] pnts = new int [OS.TXTBOX_COUNT * 2];
        /*
         *  Feature in OS/2. The maximum length of the string in string-related
         *  GPI functions is 512 chars. Do the cycle to handle larger strings.
         */
        int extX = 0, dx = 0;
        int is = 0;
        while (is < length) {
//@@TODO (dmik): Unicode    
//                int ie = is + 256;
            int ie = is + 512;
            if (ie > length) ie = length;
            if (is != 0) {
                System.arraycopy (text, is, text, 0, ie - is);  
            }
//@@TODO (dmik): Unicode    
//                OS.GpiQueryTextBox (hps, (ie - is) << 1, text, OS.TXTBOX_COUNT, pnts);
            OS.GpiQueryTextBox (hps, ie - is, text, OS.TXTBOX_COUNT, pnts);
            extX += pnts [8];
            dx = pnts [4];
            if (dx < pnts [8]) dx = pnts [8];
            dx = dx - pnts [8];
            is = ie;
        }
        size.x = extX + dx;
    }
    /*
     *  Feature in OS/2. WinQueryWindowText() removes all mnemonic prefixes
     *  from the window's text string before returning it, but we need the
     *  return value of this method to contain these prefixes. So, restore them.
     */
    text = patchMnemonics (this.text).getBytes();
    return text;
}

public Point computeSize (int wHint, int hHint, boolean changed) {
	checkWidget ();
	int width = 0, height = 0;
        
        Point sizeRect = new Point (0, 0);
        int hps = this.hps;
        if (hps == 0) hps = OS.WinGetPS (handle);
        FONTMETRICS fm = new FONTMETRICS();
        OS.GpiQueryFontMetrics (hps, FONTMETRICS.sizeof, fm);
        computeWindowTextSize (hps, fm, sizeRect);
         if (this.hps == 0) 
             OS.WinReleasePS (hps);
	Point size;
	if (layout != null) {
		size = layout.computeSize (this, wHint, hHint, changed);
	} else {
		size = minimumSize ();
	}
	width = size.x;  height = size.y;
	if (width == 0) width = DEFAULT_WIDTH;
	if (height == 0) height = DEFAULT_HEIGHT;
	if (wHint != SWT.DEFAULT) width = wHint;
	if (hHint != SWT.DEFAULT) height = hHint;
        
	Rectangle trim = computeTrim (0, 0, width, height);
	width = Math.max (trim.width, sizeRect.x + 6);
	height = trim.height;
	return new Point (width, height);
}

public Rectangle computeTrim (int x, int y, int width, int height) {
	checkWidget ();
	Rectangle trim = super.computeTrim (x, y, width, height);
	int newFont, oldFont = 0;       
        Point size = new Point (0, 0);
        int hps = this.hps;
        if (hps == 0) hps = OS.WinGetPS (handle);
        FONTMETRICS fm = new FONTMETRICS();
        OS.GpiQueryFontMetrics (hps, FONTMETRICS.sizeof, fm);
        if (this.hps == 0) OS.WinReleasePS (hps);
	int inset = 3;
	trim.x -= inset;
        trim.y -=fm.lEmHeight;
	trim.width += (inset * 2);
        trim.height += fm.lEmHeight + inset;
	return trim;
}

void createHandle () {
	super.createHandle ();
	state &= ~CANVAS;
}

public Rectangle getClientArea () {
	checkWidget ();
	if (parent != null && parent.pswp != 0) {
            endDeferWindowPos (parent);
            int count = parent.getChildrenCount ();
            beginDeferWindowPos (parent, count);
        }
        RECTL rcl = new RECTL();
        OS.WinQueryWindowRect (handle, rcl);
        int hps = this.hps;
        if (hps == 0) hps = OS.WinGetPS (handle);
        FONTMETRICS fm = new FONTMETRICS();
        OS.GpiQueryFontMetrics (hps, FONTMETRICS.sizeof, fm);
        if (this.hps == 0) OS.WinReleasePS (hps);
	int inset = 3, x = inset, y = fm.lEmHeight;
	return new Rectangle (x, y, (rcl.xRight - rcl.xLeft) - (inset * 2), (rcl.yTop - rcl.yBottom) - y - inset);
}

String getNameText () {
	return getText ();
}

/**
 * Returns the receiver's text, which is the string that the
 * is used as the <em>title</em>. If the text has not previously
 * been set, returns an empty string.
 *
 * @return the text
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public String getText () {
	checkWidget ();
//	int length = OS.GetWindowTextLength (handle);
//	if (length == 0) return "";
//	TCHAR buffer = new TCHAR (getCodePage (), length + 1);
//	OS.GetWindowText (handle, buffer, length + 1);
//	return buffer.toString (0, length);
         return text;
}

boolean mnemonicHit (char key) {
	return setFocus ();
}

boolean mnemonicMatch (char key) {
	char mnemonic = findMnemonic (getText ());
	if (mnemonic == '\0') return false;
	return Character.toUpperCase (key) == Character.toUpperCase (mnemonic);
}

/**
 * Sets the receiver's text, which is the string that will
 * be displayed as the receiver's <em>title</em>, to the argument,
 * which may not be null. 
 *
 * @param text the new text
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void setText (String string) {
	checkWidget ();
	if (string == null) error (SWT.ERROR_NULL_ARGUMENT);
        string = patchMnemonics (string);
        text = string;
        /* Use the character encoding for the default locale */
        PSZ title = new PSZ (string);
        OS.WinSetWindowText (handle, title);
}

int widgetStyle () {
	return super.widgetStyle () | OS.SS_GROUPBOX | OS.WS_TABSTOP | OS.SS_AUTOSIZE;
}
PSZ windowClass () {
	return GroupClass;
}

int windowProc () {
	return GroupProc;
}

//MRESULT WM_ERASEBACKGROUND (int mp1, int mp2) {
//    System.out.println("Group::WM_ERASEBACKGROUND -> HANDLE = " + Integer.toHexString(handle));
////    if ((state & CANVAS) != 0) {
////        //@@TODO(lpino): This invalidation forces a whole window repaint to prevent clipping errors when window is partially obscure
////        //it has a negative impact on performance. As my understanding improves maybe this problem can be solved 
//////        OS.WinInvalidateRect(handle, null, true);
//////        OS.WinUpdateWindow(handle);
//////        OS.WinFillRect(mp1, mp2, getBackgroundPixel ());
////        return MRESULT.TRUE;
//////        return super.WM_ERASEBACKGROUND (mp1, mp2);
////    }
////    RECTL rcl = new RECTL();
////    OS.objcpy (rcl, mp2);
////    drawBackground (mp1, rcl);
//    return MRESULT.TRUE;
//}

MRESULT WM_HITTEST (int mp1, int mp2) {
	MRESULT result = super.WM_HITTEST (mp1, mp2);
	if (result != null) return result;
//	/*
//	* Feature in OS/2.  The window proc for the group box
//	* returns HT_TRANSPARENT indicating that mouse messages
//	* should not be delivered to the receiver and any children.
//	* Normally, group boxes in OS/2 do not have children and
//	* this is the correct behavior for this case.  Because we
//	* allow children, answer HT_NORMAL to allow mouse messages
//	* to be delivered to the children.
//	*/
	int code = callWindowProc (OS.WM_HITTEST, mp1, mp2);
	if (code == OS.HT_TRANSPARENT) code = OS.HT_NORMAL;
	return new MRESULT (code);
}
//MRESULT WM_PAINT (int mp1, int mp2) {
//    System.out.println("Group::WM_PAINT -> HANDLE  =  " + Integer.toHexString(handle));
//    GCData data = new GCData ();
//    if (hps == 0) {
//        hps = internal_new_GC (data);
//        if (hps == 0) SWT.error(SWT.ERROR_NO_HANDLES);
//        data.doInit = true;
//    }
//    
//    /* Create the paint GC */
//    data.rcl = new RECTL ();
//    data.hps = hps;
// 
//    drawBackground(hps);
//    /* Dispose the paint GC */
//    System.out.println("Group::WM_PAINT -> HPS = " + Integer.toHexString(hps));
//    callWindowProc(OS.WM_PAINT, mp1, mp2);
//    return MRESULT.ZERO;
//}
}