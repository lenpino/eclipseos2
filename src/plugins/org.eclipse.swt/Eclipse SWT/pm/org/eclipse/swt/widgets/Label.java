package org.eclipse.swt.widgets;
/*
 * OS/2 version.
 * Copyright (c) 2002, 2007 EclipseOS2 Team.
 */

/*
 * Copyright (c) 2000, 2002 IBM Corp.  All rights reserved.
 * This file is made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 */

import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.*;
import org.eclipse.swt.internal.pm.*;
import org.eclipse.swt.*;
import org.eclipse.swt.events.*;

/**
 * Instances of this class represent a non-selectable
 * user interface object that displays a string or image.
 * When SEPARATOR is specified, displays a single
 * vertical or horizontal line.
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>SEPARATOR, HORIZONTAL, VERTICAL</dd>
 * <dd>SHADOW_IN, SHADOW_OUT, SHADOW_NONE</dd>
 * <dd>CENTER, LEFT, RIGHT, WRAP</dd>
 * <dt><b>Events:</b></dt>
 * <dd>(none)</dd>
 * </dl>
 * <p>
 * Note: Only one of SHADOW_IN, SHADOW_OUT and SHADOW_NONE may be specified.
 * SHADOW_NONE is a HINT. Only one of HORIZONTAL and VERTICAL may be specified.
 * Only one of CENTER, LEFT and RIGHT may be specified.
 * </p><p>
 * IMPORTANT: This class is intended to be subclassed <em>only</em>
 * within the SWT implementation.
 * </p>
 */
public class Label extends Control {
        String text = "";
	Image image;
	int font;
	static final int LabelProc;
	static final PSZ LabelClass =  PSZ.getAtom (OS.WC_STATIC);
        static {
            CLASSINFO pclsiClassInfo = new CLASSINFO ();
            // No Display objects have been created at this point and no call
            // to WinInitialize, so we use NULLHANDLE as a hab -- it should work
        //@@TODO (dmik): always use O as hab everywhere -- this is ok for OS/2
            OS.WinQueryClassInfo (OS.NULLHANDLE, LabelClass, pclsiClassInfo);
            LabelProc = pclsiClassInfo.pfnWindowProc;
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
 * @see SWT#SEPARATOR
 * @see SWT#HORIZONTAL
 * @see SWT#VERTICAL
 * @see SWT#SHADOW_IN
 * @see SWT#SHADOW_OUT
 * @see SWT#SHADOW_NONE
 * @see SWT#CENTER
 * @see SWT#LEFT
 * @see SWT#RIGHT
 * @see SWT#WRAP
 * @see Widget#checkSubclass
 * @see Widget#getStyle
 */
public Label (Composite parent, int style) {
	super (parent, checkStyle (style));
}

int callWindowProc (int msg, int mp1, int mp2) {
    if (handle == 0) return 0;
    // steal WM_SETFOCUS from the default window procedure (see WM_SETFOCUS())
    if (msg == OS.WM_SETFOCUS)
        return 0;
    return OS.WinCallWindowProc (LabelProc, handle, msg, mp1, mp2);
}

static int checkStyle (int style) {
	if ((style & SWT.SEPARATOR) != 0) return style;
	return checkBits (style, SWT.LEFT, SWT.CENTER, SWT.RIGHT, 0, 0, 0);
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
	int border = getBorderWidth ();
	if ((style & SWT.SEPARATOR) != 0) {
		int lineWidth = OS.WinQuerySysValue (OS.HWND_DESKTOP, OS.SV_CXBORDER);
		if ((style & SWT.HORIZONTAL) != 0) {
			width = DEFAULT_WIDTH;
                        height = lineWidth * 2;
		} else {
			width = lineWidth * 2; height = DEFAULT_HEIGHT;
		}
		if (wHint != SWT.DEFAULT) width = wHint;
		if (hHint != SWT.DEFAULT) height = hHint;
		width += border * 2; height += border * 2;
		return new Point (width, height);
	}
	int extra = 0;
        int bits = OS.WinQueryWindowULong (handle, OS.QWL_STYLE);
//@@TODO (lpino): don't we use BS_BITMAP and BS_ICON styles at all?
    if (image == null) {
        Point size = new Point (0, 0);
        int hps = this.hps;
        if (hps == 0) hps = OS.WinGetPS (handle);
        FONTMETRICS fm = new FONTMETRICS();
        OS.GpiQueryFontMetrics (hps, FONTMETRICS.sizeof, fm);
        if (computeWindowTextSize (hps, fm, size) != null) {
			extra = Math.max (8, fm.lAveCharWidth);
			width += size.x;
        }
        height += size.y;
        if (this.hps == 0) OS.WinReleasePS (hps);
	} else {
//@@TODO (dmik): remove?        
//		if (image != null) {
			Rectangle rect = image.getBounds ();
			width = rect.width;
			height = rect.height;
			extra = 4;
//		}
	}
	if (wHint != SWT.DEFAULT) width = wHint;
	if (hHint != SWT.DEFAULT) height = hHint;
	width += border * 2; height += border * 2;

        return new Point (width, height);
}

/**
 * Returns a value which describes the position of the
 * text or image in the receiver. The value will be one of
 * <code>LEFT</code>, <code>RIGHT</code> or <code>CENTER</code>
 * unless the receiver is a <code>SEPARATOR</code> label, in 
 * which case, <code>NONE</code> is returned.
 *
 * @return the alignment 
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public int getAlignment () {
	checkWidget ();
	if ((style & SWT.SEPARATOR) != 0) return 0;
	if ((style & SWT.LEFT) != 0) return SWT.LEFT;
	if ((style & SWT.CENTER) != 0) return SWT.CENTER;
	if ((style & SWT.RIGHT) != 0) return SWT.RIGHT;
	return SWT.LEFT;
}

/**
 * Returns the receiver's image if it has one, or null
 * if it does not.
 *
 * @return the receiver's image
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public Image getImage () {
	checkWidget ();
	return image;
}

String getNameText () {
	return getText ();
}

/**
 * Returns the receiver's text, which will be an empty
 * string if it has never been set or if the receiver is
 * a <code>SEPARATOR</code> label.
 *
 * @return the receiver's text
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public String getText () {
    checkWidget ();
    return text;
}

/*
* Not currently used.
*/
boolean getWrap () {
	int bits = OS.WinQueryWindowULong (handle, OS.QWL_STYLE);
	if ((bits & (OS.DT_RIGHT | OS.DT_CENTER)) != 0) return true;
	if ((bits & OS.DT_WORDBREAK) == 0) return false;
	return true;
}

boolean mnemonicHit (char key) {
	Composite control = this.parent;
	while (control != null) {
		Control [] children = control._getChildren ();
		int index = 0;
		while (index < children.length) {
			if (children [index] == this) break;
			index++;
		}
		index++;
		if (index < children.length) {
			if (children [index].setFocus ()) return true;
		}
		control = control.parent;
	}
	return false;
}

boolean mnemonicMatch (char key) {
	char mnemonic = findMnemonic (getText ());
	if (mnemonic == '\0') return false;
	return Character.toUpperCase (key) == Character.toUpperCase (mnemonic);
}

void releaseWidget () {
	super.releaseWidget ();
	image = null;
}

/**
 * Controls how text and images will be displayed in the receiver.
 * The argument should be one of <code>LEFT</code>, <code>RIGHT</code>
 * or <code>CENTER</code>.  If the receiver is a <code>SEPARATOR</code>
 * label, the argument is ignored and the alignment is not changed.
 *
 * @param alignment the new alignment 
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void setAlignment (int alignment) {
	checkWidget ();
	if ((style & SWT.SEPARATOR) != 0) return;
	if ((alignment & (SWT.LEFT | SWT.RIGHT | SWT.CENTER)) == 0) return;
	style &= ~(SWT.LEFT | SWT.RIGHT | SWT.CENTER);
	style |= alignment & (SWT.LEFT | SWT.RIGHT | SWT.CENTER);
	int bits = OS.WinQueryWindowULong (handle, OS.QWL_STYLE);
	bits &= ~(OS.DT_WORDBREAK | OS.DT_CENTER | OS.DT_RIGHT);
	if ((style & SWT.LEFT) != 0 && (style & SWT.WRAP) != 0) {
		bits |= OS.DT_WORDBREAK;
	}
	if ((style & SWT.CENTER) != 0) bits |= OS.DT_CENTER;
	if ((style & SWT.RIGHT) != 0) bits |= OS.DT_RIGHT;
	OS.WinSetWindowULong (handle, OS.QWL_STYLE, bits);
	OS.WinInvalidateRect (handle, null, false);
}

public boolean setFocus () {
	checkWidget();
	return false;
}

/**
 * Sets the receiver's image to the argument, which may be
 * null indicating that no image should be displayed.
 *
 * @param image the image to display on the receiver (may be null)
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_INVALID_ARGUMENT - if the image has been disposed</li> 
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void setImage (Image image) {
    checkWidget ();
    if ((style & SWT.SEPARATOR) != 0) return;
    int hImage = 0, imageBits = 0;
    if (image != null) {
            if (image.isDisposed()) error(SWT.ERROR_INVALID_ARGUMENT);
            switch (image.type) {
                    case SWT.BITMAP:
                            imageBits = OS.SS_BITMAP;
                            break;
                    case SWT.ICON:
                            imageBits = OS.SS_ICON;
                            break;
                    default:
                            return;
            }
    }
    this.image = image;
    int newBits = OS.WinQueryWindowULong (handle, OS.QWL_STYLE);
    int oldBits = newBits;
    newBits &= ~(OS.SS_BITMAP | OS.SS_ICON);
    newBits |= imageBits;
    if (newBits != oldBits) {
        OS.WinSetWindowULong (handle, OS.QWL_STYLE, newBits);
    }
    OS.WinSendMsg(handle, OS.SM_SETHANDLE, image.handle, 0); 
    OS.WinInvalidateRect (handle, null, false);
}

/**
 * Sets the receiver's text.
 * <p>
 * This method sets the widget label.  The label may include
 * the mnemonic characters and line delimiters.
 * </p>
 * 
 * @param string the new text
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the text is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void setText (String string) {
	checkWidget ();
        if (string == null) 
            error (SWT.ERROR_NULL_ARGUMENT);
	if ((style & SWT.SEPARATOR) != 0) 
            return;
        
         int bits = OS.WinQueryWindowULong (handle, OS.QWL_STYLE);
         
         bits |= OS.SS_TEXT;
         if ((style & SWT.LEFT) != 0)
             bits |= OS.DT_LEFT;
         if ((style & SWT.CENTER) != 0)
             bits |=OS.DT_CENTER |  OS.DT_VCENTER;
         if ((style & SWT.RIGHT) != 0)
             bits |= OS.DT_RIGHT;
         
         OS.WinSetWindowULong (handle, OS.QWL_STYLE, bits);
         
        string = patchMnemonics (string);
        text = string;
        /* Use the character encoding for the default locale */
        PSZ title = new PSZ (string);
        OS.WinSetWindowText (handle, title);
}

/*
* Not currently used.
*/
void setWrap (boolean wrap) {
	int bits = OS.WinQueryWindowULong (handle, OS.QWL_STYLE);
	if ((bits & (OS.DT_RIGHT | OS.DT_CENTER)) != 0) return;
	bits &= OS.DT_WORDBREAK;
	if (!wrap) bits |= ~OS.DT_WORDBREAK;
	OS.WinSetWindowULong (handle, OS.QWL_STYLE, bits);
	OS.WinInvalidateRect (handle, null, false);
}
//
//int widgetExtStyle () {
//	if ((style & SWT.BORDER) != 0) return OS.WS_EX_STATICEDGE;
//	return super.widgetExtStyle ();
//}

int widgetStyle () {
	int bits = super.widgetStyle () | OS.SS_TEXT;
//	if ((style & SWT.SEPARATOR) != 0) return bits | OS.SS_OWNERDRAW;
	if ((style & SWT.CENTER) != 0) return bits | OS.DT_CENTER | OS.DT_BOTTOM;
	if ((style & SWT.RIGHT) != 0) return bits | OS.DT_RIGHT;
	if ((style & SWT.WRAP) != 0) return bits | OS.DT_WORDBREAK;;
	if ((style & SWT.LEFT) != 0) return bits | OS.DT_LEFT;
	return bits;
}

PSZ windowClass () {
	return LabelClass;
}

int windowProc () {
	return LabelProc;
}


//MRESULT WM_ERASEBKGND (int mp1, int mp2) {
//	LRESULT result = super.WM_ERASEBKGND (wParam, lParam);
//	if (result != null) return result;
//	if ((style & SWT.SEPARATOR) != 0) return LRESULT.ONE;
//	/*
//	* Bug in Windows.  When a label has the SS_BITMAP
//	* or SS_ICON style, the label does not draw the
//	* background.  The fix is to draw the background
//	* when the label is showing a bitmap or icon.
//	*
//	* NOTE: SS_BITMAP and SS_ICON are not single bit
//	* masks so it is necessary to test for all of the
//	* bits in these masks.
//	*/
//	int bits = OS.GetWindowLong (handle, OS.GWL_STYLE);
//	if ((bits & OS.SS_BITMAP) != OS.SS_BITMAP && (bits & OS.SS_ICON) != OS.SS_ICON) {
//		return result;
//	}
//	drawBackground (wParam);
//        System.out.println("WM_ERASEBKGND");
//	return null;
//}
//

MRESULT WM_SIZE (int mp1, int mp2) {
	MRESULT result = super.WM_SIZE (mp1, mp2);
	/*
	* It is possible (but unlikely), that application
	* code could have disposed the widget in the resize
	* event.  If this happens, end the processing of the
	* Windows message by returning the result of the
	* WM_SIZE message.
	*/
	if (isDisposed ()) return result;
//	if ((style & SWT.SEPARATOR) != 0) {
//		OS.InvalidateRect (handle, null, true);
//		return result;
//	}
	
//	/*
//	* Bug in Windows.  For some reason, a label with
//	* style SS_LEFT, SS_CENTER or SS_RIGHT does not
//	* redraw the text in the new position when resized.
//	* Note that SS_LEFTNOWORDWRAP does not have the problem.
//	* The fix is to force the redraw.
//	*/
//	if ((style & (SWT.WRAP | SWT.CENTER | SWT.RIGHT)) != 0) {
//		OS.InvalidateRect (handle, null, true);
//	}
	return result;
}

MRESULT WM_PAINT (int mp1, int mp2) {
//	DRAWITEMSTRUCT struct = new DRAWITEMSTRUCT ();
//	OS.MoveMemory (struct, lParam, DRAWITEMSTRUCT.sizeof);
//	drawBackground (struct.hDC);
//	if ((style & SWT.SHADOW_NONE) != 0) return null;
//	RECT rect = new RECT ();
//	int lineWidth = OS.GetSystemMetrics (OS.SM_CXBORDER);
//	int flags = OS.EDGE_ETCHED;
//	if ((style & SWT.SHADOW_IN) != 0) flags = OS.EDGE_SUNKEN;
//	if ((style & SWT.HORIZONTAL) != 0) {
//		int bottom = struct.top + Math.max (lineWidth * 2, (struct.bottom - struct.top) / 2);
//		OS.SetRect (rect, struct.left, struct.top, struct.right, bottom);
//		OS.DrawEdge (struct.hDC, rect, flags, OS.BF_BOTTOM);
//	} else {
//		int right = struct.left + Math.max (lineWidth * 2, (struct.right - struct.left) / 2);
//		OS.SetRect (rect, struct.left, struct.top, right, struct.bottom);
//		OS.DrawEdge (struct.hDC, rect, flags, OS.BF_RIGHT);
//	}
	if (this.image == null) return null;
	if(this.image.getImageData().width != 16 && this.image.getImageData().width != 20) return null;

        // hps is unitialized here, do some initializations
	if (hps == 0) hps = OS.WinGetPS (handle);
	
        OS.GpiSetMix (hps, OS.FM_OVERPAINT);
        OS.GpiSetBackMix (hps, OS.BM_OVERPAINT);
        OS.GpiSetPattern (hps, OS.PATSYM_BLANK);
        int hPalette = getDisplay ().hPalette;
        if (hPalette == 0)
            OS.GpiCreateLogColorTable (hps, 0, OS.LCOLF_RGB, 0, 0, null);
        else
            OS.GpiSelectPalette (hps, hPalette);
	
            Rectangle bounds = image.getBounds();
            int x = 0;
            int y = 0;
	    if (image.type == SWT.ICON) {
                int hbmColor = image.handle;
                int hbmPointer = image.maskHandle;
                if (hbmPointer == 0) {
                    OS.WinDrawPointer (hps, x, y, hbmColor, OS.DP_NORMAL);
                } else {
                    OS.GpiSetColor (hps, (hPalette != 0) ? 15 : 0xFFFFFF);
                    OS.GpiSetBackColor (hps, 0);
                    /* draw AND mask */
                    OS.GpiWCBitBlt (hps, hbmPointer, 4,
                        new int[] {
                            x, y, x + bounds.width - 1, y + bounds.height - 1,
                            0, bounds.height, bounds.width, bounds.height << 1
                        },
                        OS.ROP_SRCAND, OS.BBO_IGNORE
                    );
                    /* draw icon */
                    OS.GpiWCBitBlt (hps, hbmColor, 4,
                        new int[] {
                            x, y, x + bounds.width - 1, y + bounds.height - 1,
                            0, 0, bounds.width, bounds.height
                        },
                        OS.ROP_SRCINVERT, OS.BBO_IGNORE
                    );
                    /* draw XOR mask */
                    OS.GpiWCBitBlt (hps, hbmPointer, 4,
                        new int[] {
                            x, y, x + bounds.width - 1, y + bounds.height - 1,
                            0, 0, bounds.width, bounds.height
                        },
                        OS.ROP_SRCINVERT, OS.BBO_IGNORE
                    );
                }
            }
	if (this.hps == 0) OS.WinReleasePS (hps);    
	return null;
}

}
