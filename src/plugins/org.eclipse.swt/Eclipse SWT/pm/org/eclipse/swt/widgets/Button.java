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
import org.eclipse.swt.events.*;

/**
 * Instances of this class represent a selectable user interface object that
 * issues notification when pressed and released. 
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>ARROW, CHECK, PUSH, RADIO, TOGGLE, FLAT</dd>
 * <dd>UP, DOWN, LEFT, RIGHT, CENTER</dd>
 * <dt><b>Events:</b></dt>
 * <dd>Selection</dd>
 * </dl>
 * <p>
 * Note: Only one of the styles ARROW, CHECK, PUSH, RADIO, and TOGGLE 
 * may be specified.
 * </p><p>
 * Note: Only one of the styles LEFT, RIGHT, and CENTER may be specified.
 * </p><p>
 * Note: Only one of the styles UP, DOWN, LEFT, and RIGHT may be specified
 * when the ARROW style is specified.
 * </p><p>
 * IMPORTANT: This class is intended to be subclassed <em>only</em>
 * within the SWT implementation.
 * </p>
 */

public class Button extends Control {

String text = "";
Image image;

static final int ButtonProc;
static final PSZ ButtonClass = PSZ.getAtom (OS.WC_BUTTON);

static final int CheckWidth, CheckHeight;
static {
    int hBitmap = OS.WinGetSysBitmap (OS.HWND_DESKTOP, OS.SBMP_CHECKBOXES);
    if (hBitmap == OS.NULLHANDLE) {
        CheckWidth = OS.WinQuerySysValue (OS.HWND_DESKTOP, OS.SV_CXVSCROLL);
        CheckHeight = OS.WinQuerySysValue (OS.HWND_DESKTOP, OS.SV_CYHSCROLL);
    } else {
        BITMAPINFOHEADER2 bmpData = new BITMAPINFOHEADER2 ();
        OS.GpiQueryBitmapInfoHeader (hBitmap, bmpData);
        OS.GpiDeleteBitmap (hBitmap);
        CheckWidth = bmpData.cx / 4;
        CheckHeight =  bmpData.cy / 3;
    }
    CLASSINFO pclsiClassInfo = new CLASSINFO ();
    // No Display objects have been created at this point and no call
    // to WinInitialize, so we use NULLHANDLE as a hab -- it should work
//@@TODO (dmik): always use O as hab everywhere -- this is ok for OS/2
    OS.WinQueryClassInfo (OS.NULLHANDLE, ButtonClass, pclsiClassInfo);
    ButtonProc = pclsiClassInfo.pfnWindowProc;
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
 * @see SWT#ARROW
 * @see SWT#CHECK
 * @see SWT#PUSH
 * @see SWT#RADIO
 * @see SWT#TOGGLE
 * @see SWT#FLAT
 * @see SWT#LEFT
 * @see SWT#RIGHT
 * @see SWT#CENTER
 * @see Widget#checkSubclass
 * @see Widget#getStyle
 */
public Button (Composite parent, int style) {
	super (parent, checkStyle (style));
    /*
     *  Feature in OS/2. When BS_NOBORDER style is specified for pushbutton
     *  it uses its background color as text foreground to draw text when
     *  it is pressed (i.e. hilited). Set SYSCLS_BUTTONDARK as such color.
     */
//@@TODO (dmik): this should be done on every WN_SYSCOLORCHANGE
    if ((style & SWT.FLAT) != 0 && (style & (SWT.PUSH | SWT.TOGGLE)) != 0) {
        int[] value = {OS.SYSCLR_BUTTONDARK};
        int index = (value [0] < 0 || getDisplay().hPalette != 0) ?
            OS.PP_BACKGROUNDCOLORINDEX : OS.PP_BACKGROUNDCOLOR;
        OS.WinSetPresParam (handle, index, 4, value);
    }
}

/**
 * Adds the listener to the collection of listeners who will
 * be notified when the control is selected, by sending
 * it one of the messages defined in the <code>SelectionListener</code>
 * interface.
 * <p>
 * <code>widgetSelected</code> is called when the control is selected.
 * <code>widgetDefaultSelected</code> is not called.
 * </p>
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
 * @see SelectionEvent
 */
public void addSelectionListener (SelectionListener listener) {
	checkWidget ();
	if (listener == null) error (SWT.ERROR_NULL_ARGUMENT);
	TypedListener typedListener = new TypedListener (listener);
	addListener (SWT.Selection,typedListener);
	addListener (SWT.DefaultSelection,typedListener);
}

int callWindowProc (int msg, int mp1, int mp2) {
    if (handle == 0) return 0;
    // steal WM_SETFOCUS from the default window procedure (see WM_SETFOCUS())
    if (msg == OS.WM_SETFOCUS && (style & SWT.TOGGLE) != 0 && image == null)
        return 0;
    return OS.WinCallWindowProc (ButtonProc, handle, msg, mp1, mp2);
}

static int checkStyle (int style) {
	style = checkBits (style, SWT.PUSH, SWT.ARROW, SWT.CHECK, SWT.RADIO, SWT.TOGGLE, 0);
	if ((style & SWT.PUSH) != 0) {
		return checkBits (style, SWT.CENTER, SWT.LEFT, SWT.RIGHT, 0, 0, 0);
	}
	if ((style & (SWT.CHECK | SWT.RADIO | SWT.TOGGLE)) != 0) {
		return checkBits (style, SWT.LEFT, SWT.RIGHT, SWT.CENTER, 0, 0, 0);
	}
	if ((style & SWT.ARROW) != 0) {
		return checkBits (style, SWT.UP, SWT.DOWN, SWT.LEFT, SWT.RIGHT, 0, 0);
	}
	return style;
}

void click () {
	/*
	* Note: BM_CLICK sends WM_BUTTON1DOWN and WM_BUTTON1UP.
	*/
    OS.WinSendMsg (handle, OS.BM_CLICK, 0, 0);
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
	int border = getBorderWidth ();
	int width = border * 2, height = border * 2;
	if ((style & SWT.ARROW) != 0) {
		if ((style & (SWT.UP | SWT.DOWN)) != 0) {
			width += OS.WinQuerySysValue (OS.HWND_DESKTOP, OS.SV_CXVSCROLL);
			height += OS.WinQuerySysValue (OS.HWND_DESKTOP, OS.SV_CYVSCROLLARROW) + 4;
		} else {
			width += OS.WinQuerySysValue (OS.HWND_DESKTOP, OS.SV_CXHSCROLLARROW) + 4;
			height += OS.WinQuerySysValue (OS.HWND_DESKTOP, OS.SV_CYHSCROLL);
		}
		if (wHint != SWT.DEFAULT) width = wHint + (border * 2);
		if (hHint != SWT.DEFAULT) height = hHint + (border * 2);
		return new Point (width, height);
	}
	int extra = 0;
    int bits = OS.WinQueryWindowULong (handle, OS.QWL_STYLE);
//@@TODO (dmik): don't we use BS_BITMAP and BS_ICON styles at all?
//	if ((bits & (OS.BS_BITMAP | OS.BS_ICON)) == 0) {
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
    if ((style & (SWT.CHECK | SWT.RADIO)) != 0) {
        width += CheckWidth + extra;
        height = Math.max (height + 2, CheckHeight + 2);
    }
    if ((style & (SWT.PUSH | SWT.TOGGLE)) != 0) {
        width += 10; height += 10;
    }
    if (wHint != SWT.DEFAULT) width = wHint + (border * 2);
    if (hHint != SWT.DEFAULT) height = hHint + (border * 2);
	return new Point (width, height);
}

int defaultBackground () {
	if ((style & (SWT.PUSH | SWT.TOGGLE)) != 0) {
		return OS.SYSCLR_BUTTONMIDDLE;
	}
	return super.defaultBackground ();
}

int defaultForeground () {
    return OS.SYSCLR_MENUTEXT; 
}

/**
 * Returns a value which describes the position of the
 * text or image in the receiver. The value will be one of
 * <code>LEFT</code>, <code>RIGHT</code> or <code>CENTER</code>
 * unless the receiver is an <code>ARROW</code> button, in 
 * which case, the alignment will indicate the direction of
 * the arrow (one of <code>LEFT</code>, <code>RIGHT</code>, 
 * <code>UP</code> or <code>DOWN</code>).
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
	if ((style & SWT.ARROW) != 0) {
		if ((style & SWT.UP) != 0) return SWT.UP;
		if ((style & SWT.DOWN) != 0) return SWT.DOWN;
		if ((style & SWT.LEFT) != 0) return SWT.LEFT;
		if ((style & SWT.RIGHT) != 0) return SWT.RIGHT;
		return SWT.UP;
	}
	if ((style & SWT.LEFT) != 0) return SWT.LEFT;
	if ((style & SWT.CENTER) != 0) return SWT.CENTER;
	if ((style & SWT.RIGHT) != 0) return SWT.RIGHT;
	return SWT.LEFT;
}

public int getBorderWidth () {
    checkWidget ();
    if ((style & (SWT.PUSH | SWT.TOGGLE)) != 0)
        return OS.WinQuerySysValue (OS.HWND_DESKTOP, OS.SV_CXBORDER);
    return 0; 
}

boolean getDefault () {
	if ((style & SWT.PUSH) == 0) return false;
	int bits = OS.WinQueryWindowULong (handle, OS.QWL_STYLE);
	return (bits & OS.BS_DEFAULT) != 0;
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
 * Returns <code>true</code> if the receiver is selected,
 * and false otherwise.
 * <p>
 * When the receiver is of type <code>CHECK</code> or <code>RADIO</code>,
 * it is selected when it is checked. When it is of type <code>TOGGLE</code>,
 * it is selected when it is pushed in. If the receiver is of any other type,
 * this method returns false.
 *
 * @return the selection state
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public boolean getSelection () {
	checkWidget ();
	if ((style & (SWT.CHECK | SWT.RADIO | SWT.TOGGLE)) == 0) return false;
	int state = OS.WinSendMsg (handle, OS.BM_QUERYCHECK, 0, 0);
	return (state & 0xFFFF) != 0;
}

/**
 * Returns the receiver's text, which will be an empty
 * string if it has never been set.
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

boolean isTabItem () {
	//TEMPORARY CODE
	//if ((style & SWT.PUSH) != 0) return true;
	return super.isTabItem ();
}

boolean mnemonicHit (char ch) {
	if (!setFocus ()) return false;
//@@TODO (dmik): remove, not the case anymore    
//	/*
//	* Feature in OS/2.  When a radio button gets focus, 
//	* it selects the button in WM_SETFOCUS.  Therefore, it
//	* is not necessary to click the button or send events
//	* because this has already happened in WM_SETFOCUS.
//	*/
//	if ((style & SWT.RADIO) == 0) click ();
    click ();
	return true;
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
 * Removes the listener from the collection of listeners who will
 * be notified when the control is selected.
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
 * @see #addSelectionListener
 */
public void removeSelectionListener (SelectionListener listener) {
	checkWidget ();
	if (listener == null) error (SWT.ERROR_NULL_ARGUMENT);
	if (eventTable == null) return;
	eventTable.unhook (SWT.Selection, listener);
	eventTable.unhook (SWT.DefaultSelection,listener);	
}

void selectRadio () {
	Control [] children = parent._getChildren ();
	for (int i=0; i<children.length; i++) {
		Control child = children [i];
		if (this != child && child instanceof Button) {
			Button button = (Button) child;
			if ((button.style & SWT.RADIO) != 0) {
				if (button.getSelection ()) {
                    /*
                     *  Bug in OS/2. Sending BM_SETCHECK messages from here (i.e.
                     *  calling setSelection (false)) causes the focus rectangle
                     *  to be mistakenly drawn around the radiobutton that has
                     *  lost the focus and being deselected, if that button is
                     *  BS_USERBUTTON but returns DLGC_RADIOBUTTON in responce
                     *  to WM_QUERYDLGCODE (as we do for buttons with images
                     *  which we paint ourselves due to some OS/2 limitations).
                     *  The solution is to post BM_SETCHECK messages instead.
                     *
                     *  However, we cannot post the SWT.Selection event to the
                     *  widget (to indicate listeners the radiobuttion is
                     *  unselected) right after posting BM_SETCHECK, because
                     *  the event will always arrive before the message and
                     *  the listener will be confused by the return value of
                     *  the getSelection() method -- it coluld still be true.
                     *  So, we pass the magic code through the unused mp2 param
                     *  and post the SWT.Selection event from the button's
                     *  window procedure when it receives BM_SETCHECK with the
                     *  magic code.
                     */
                    OS.WinPostMsg (button.handle, OS.BM_SETCHECK, 0, button.handle);
				}
			}
		}
	}
    setSelection (true);
}

/**
 * Controls how text, images and arrows will be displayed
 * in the receiver. The argument should be one of
 * <code>LEFT</code>, <code>RIGHT</code> or <code>CENTER</code>
 * unless the receiver is an <code>ARROW</code> button, in 
 * which case, the argument indicates the direction of
 * the arrow (one of <code>LEFT</code>, <code>RIGHT</code>, 
 * <code>UP</code> or <code>DOWN</code>).
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
	if ((style & SWT.ARROW) != 0) {
		if ((style & (SWT.UP | SWT.DOWN | SWT.LEFT | SWT.RIGHT)) == 0) return; 
		style &= ~(SWT.UP | SWT.DOWN | SWT.LEFT | SWT.RIGHT);
		style |= alignment & (SWT.UP | SWT.DOWN | SWT.LEFT | SWT.RIGHT);
		OS.WinInvalidateRect (handle, null, false);
		return;
	}
	if ((alignment & (SWT.LEFT | SWT.RIGHT | SWT.CENTER)) == 0) return;
	style &= ~(SWT.LEFT | SWT.RIGHT | SWT.CENTER);
	style |= alignment & (SWT.LEFT | SWT.RIGHT | SWT.CENTER);
//@@TODO(dmik): simulate justify using owner drawn buttons?
//	int bits = OS.GetWindowLong (handle, OS.GWL_STYLE);
//	bits &= ~(OS.BS_LEFT | OS.BS_CENTER | OS.BS_RIGHT);
//	if ((style & SWT.LEFT) != 0) bits |= OS.BS_LEFT;
//	if ((style & SWT.CENTER) != 0) bits |= OS.BS_CENTER;
//	if ((style & SWT.RIGHT) != 0) bits |= OS.BS_RIGHT;
//	OS.SetWindowLong (handle, OS.GWL_STYLE, bits);
//	OS.InvalidateRect (handle, null, true);
}

void setDefault (boolean value) {
	if ((style & SWT.PUSH) == 0) return;
    if (getDefault() == value) return;
	if (value) {
        OS.WinSendMsg (handle, OS.BM_SETDEFAULT, 1, 0);
	} else {
        OS.WinSendMsg (handle, OS.BM_SETDEFAULT, 0, 0);
	}
}

public boolean setFocus () {
	checkWidget();
	if ((style & SWT.ARROW) != 0) return false;
	return super.setFocus ();
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
	if (image != null) {
		if (image.isDisposed()) error(SWT.ERROR_INVALID_ARGUMENT);
	}
    if ((style & (SWT.PUSH | SWT.CHECK | SWT.RADIO)) != 0) {
        if ((image != null && this.image == null) ||
            (image == null && this.image != null)
        ) {
            int bits = OS.WinQueryWindowULong (handle, OS.QWL_STYLE);
            bits &= ~OS.BS_PRIMARYSTYLES;
            if (image != null) {
                bits |= OS.BS_USERBUTTON;
            } else {
                if ((style & SWT.PUSH) != 0) bits |= OS.BS_PUSHBUTTON;
                else if ((style & SWT.CHECK) != 0) bits |= OS.BS_CHECKBOX;
                else if ((style & SWT.RADIO) != 0) bits |= OS.BS_RADIOBUTTON;
            }
            OS.WinSetWindowULong (handle, OS.QWL_STYLE, bits);
        }
    }
    this.image = image;
    OS.WinInvalidateRect (handle, null, false);
    
//@@TODO (dmik): don't we use BS_BITMAP and BS_ICON styles at all?
//@@TODO (dmik): remove    
//	checkWidget ();
//	int hImage = 0, imageBits = 0, fImageType = 0;
//	if (image != null) {
//		if (image.isDisposed()) error(SWT.ERROR_INVALID_ARGUMENT);
//		hImage = image.handle;
//		switch (image.type) {
//			case SWT.BITMAP:
//				imageBits = OS.BS_BITMAP;
//				fImageType = OS.IMAGE_BITMAP;
//				break;
//			case SWT.ICON:
//				imageBits = OS.BS_ICON;
//				fImageType = OS.IMAGE_ICON;
//				break;
//			default:
//				return;
//		}
//	}
//	this.image = image;
//	int newBits = OS.GetWindowLong (handle, OS.GWL_STYLE);
//	int oldBits = newBits;
//	newBits &= ~(OS.BS_BITMAP | OS.BS_ICON);
//	newBits |= imageBits;
//	if (newBits != oldBits) {
//		OS.SetWindowLong (handle, OS.GWL_STYLE, newBits);
//	}
//	OS.SendMessage (handle, OS.BM_SETIMAGE, fImageType, hImage);
}

void setPresBackground () {
    /*
     * OS/2 feature. It's possible to change the pushbutton background (via the
     * presentation parameters) but the same is impossible under Windows, so
     * disable it for compatibility.
     */
    if ((style & (SWT.PUSH | SWT.TOGGLE)) != 0) return;
    super.setPresBackground ();
}

void setPresForeground () {
    /*
     * OS/2 feature. It's possible to change the pushbutton foreground (via the
     * presentation parameters) but the same is impossible under Windows, so
     * disable it for compatibility.
     */
    if ((style & (SWT.PUSH | SWT.TOGGLE)) != 0) return;
    super.setPresForeground ();
}

boolean setRadioFocus () {
	if ((style & SWT.RADIO) == 0 || !getSelection ()) return false;
	return setFocus ();
}

/**
 * Sets the selection state of the receiver, if it is of type <code>CHECK</code>, 
 * <code>RADIO</code>, or <code>TOGGLE</code>.
 *
 * <p>
 * When the receiver is of type <code>CHECK</code> or <code>RADIO</code>,
 * it is selected when it is checked. When it is of type <code>TOGGLE</code>,
 * it is selected when it is pushed in.
 *
 * @param selected the new selection state
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void setSelection (boolean selected) {
	checkWidget ();
	if ((style & (SWT.CHECK | SWT.RADIO | SWT.TOGGLE)) == 0) return;
    
    /*
     *  Feature in OS/2. When BM_SETCHECK is used to set the checked
     *  state of a radio button, it sets the WM_TABSTOP style of the
     *  button if it becomes checked and resets this style otherwise.
     *  This is unwanted. The fix is to save and restore the window style
     *  bits.
     */
    int bits = OS.WinQueryWindowULong (handle, OS.QWL_STYLE);
    OS.WinSendMsg (handle, OS.BM_SETCHECK, selected ? 1 : 0, 0);
    OS.WinSetWindowULong (handle, OS.QWL_STYLE, bits);
    
    /*
     *  OS/2 note: the below seems a bit pointless, but we leave it as is
     *  for now, for compatibility with SWT 2.0.1 for Win32. This behavior
     *  seems to be changed in the recent SWT versions, so later this code
     *  will be removed.
     */
	/*
	* Feature in Windows.  When a radio button gets focus, 
	* it selects the button in WM_SETFOCUS.  If the previous
	* saved focus widget was a radio button, allowing the shell
	* to automatically restore the focus to the previous radio
	* button will unexpectedly check that button.  The fix is
	* to set the saved focus widget for the shell to be the
	* radio button so that when focus is restored, the focus
	* widget will be the new radio button.
	*/
	if (!selected) return;
	if ((style & SWT.RADIO) == 0) return;
	menuShell ().setSavedFocus (this);      
}

boolean setTabItemFocus () {
    boolean result = super.setTabItemFocus ();
    if ((style & SWT.RADIO) != 0 && result) {
        /*
         *  Since we don't use the BS_AUTORADIOBUTTON style for radio
         *  buttons, we have to send them BN_CLICKED manually when they get
         *  focus as a result of pressing one of focus traversal keys, to
         *  provide the standard system behavior. 
         */
        click();
    }
    return result;
}

/**
 * Sets the receiver's text.
 * <p>
 * This method sets the button label.  The label may include
 * the mnemonic character but must not contain line delimiters.
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
    if (string == null) error (SWT.ERROR_NULL_ARGUMENT);
    
//@@TODO (dmik): don't we use BS_BITMAP and BS_ICON styles at all?
//	int newBits = OS.WinQueryWindowULong (handle, OS.QWL_STYLE);
//	int oldBits = newBits;
//	newBits &= ~(OS.BS_BITMAP | OS.BS_ICON);
//	if (newBits != oldBits) {
//		OS.WinSetWindowULong (handle, OS.QWL_STYLE, newBits);
//	}
    if (image != null && (style & (SWT.PUSH | SWT.CHECK | SWT.RADIO)) != 0) {
        int bits = OS.WinQueryWindowULong (handle, OS.QWL_STYLE);
        bits &= ~OS.BS_PRIMARYSTYLES;
        if ((style & SWT.PUSH) != 0) bits |= OS.BS_PUSHBUTTON;
        else if ((style & SWT.CHECK) != 0) bits |= OS.BS_CHECKBOX;
        else if ((style & SWT.RADIO) != 0) bits |= OS.BS_RADIOBUTTON;
        OS.WinSetWindowULong (handle, OS.QWL_STYLE, bits);
    }
    image = null;
    
    text = string;
    string = patchMnemonics (string);
    /* Use the character encoding for the default locale */
    PSZ title = new PSZ (string);
    OS.WinSetWindowText (handle, title);
}

int widgetStyle () {
	int bits = super.widgetStyle ();
	if ((style & SWT.FLAT) != 0) bits |= OS.BS_NOBORDER;
	if ((style & SWT.ARROW) != 0) {
        return bits |
            OS.BS_PUSHBUTTON | OS.BS_USERBUTTON |
            OS.BS_NOCURSORSELECT | OS.BS_NOPOINTERFOCUS;
    }
//@@TODO(dmik): are we going to ignore these flags for now?    
//	if ((style & SWT.LEFT) != 0) bits |= OS.BS_LEFT;
//	if ((style & SWT.CENTER) != 0) bits |= OS.BS_CENTER;
//	if ((style & SWT.RIGHT) != 0) bits |= OS.BS_RIGHT;
    if ((style & SWT.PUSH) != 0) return bits | OS.BS_PUSHBUTTON | OS.WS_TABSTOP;
	if ((style & SWT.CHECK) != 0) return bits | OS.BS_CHECKBOX;
	if ((style & SWT.RADIO) != 0) return bits | OS.BS_RADIOBUTTON;
    if ((style & SWT.TOGGLE) != 0) return bits | OS.BS_PUSHBUTTON | OS.BS_USERBUTTON;
	return bits | OS.BS_PUSHBUTTON | OS.WS_TABSTOP;
}

PSZ windowClass () {
    return ButtonClass;
}

int windowProc () {
	return ButtonProc;
}

int windowProc (int msg, int mp1, int mp2) {
    int result = super.windowProc (msg, mp1, mp2);
    if (msg == OS.BM_SETCHECK) {
        /*
         *  Post the SWT.Selection event if BM_SETCHECK has been sent from
         *  selectRadio() (as indicated by the magic mp2 value).
         */
        if (mp2 == handle) postEvent (SWT.Selection);                
    }
    return result;
}
    
void drawFocusRect () {
    int hps = OS.WinGetPS (handle);
    RECTL rcl = new RECTL();
    OS.WinQueryWindowRect (handle, rcl);
    boolean doit = false;
    if (image == null) {
        FONTMETRICS fm = new FONTMETRICS();
        Point size = new Point (0, 0);
        OS.GpiQueryFontMetrics (hps, FONTMETRICS.sizeof, fm);
        if (computeWindowTextSize (hps, fm, size) != null) {
            rcl.xLeft = (rcl.xRight - size.x) / 2 - 1;    
            rcl.yBottom = (rcl.yTop - size.y) / 2 - 1;
            rcl.xRight = rcl.xLeft + size.x + 2;  
            rcl.yTop = rcl.yBottom + size.y + 2;  
            doit = true;
        }
    } else {
        if ((rcl.xRight > rcl.xLeft) && (rcl.yTop > rcl.yBottom))
            doit = true;
    }
    if (doit) {
        int[] lineBundle = {
            0, // lColor
            OS.CLR_WHITE, // lBackColor
            (OS.BM_XOR << 16) | OS.FM_LEAVEALONE, // usMixMode, usBackMixMode
            0, // fxWidth
            0, // lGeomWidth
            OS.LINETYPE_ALTERNATE, // usType & usEnd
            0, // usJoin & usReserved
        };
        OS.GpiSetAttrs (
            hps, OS.PRIM_LINE,
            OS.LBB_BACK_COLOR | OS.LBB_MIX_MODE | OS.LBB_BACK_MIX_MODE | OS.LBB_TYPE,
            0, lineBundle
        );
        int[] pnt = new int[] {rcl.xLeft, rcl.yBottom};
        OS.GpiMove (hps, pnt);
        pnt [0] = rcl.xRight - 1; pnt[ 1] = rcl.yTop - 1;
        OS.GpiBox (hps, OS.DRO_OUTLINE, pnt, 0, 0);
    }
    OS.WinReleasePS (hps);
}

MRESULT WM_QUERYDLGCODE (int mp1, int mp2) {
	MRESULT result = super.WM_QUERYDLGCODE (mp1, mp2);
	if (result != null) return result;
	if ((style & SWT.ARROW) != 0) {
		return new MRESULT (OS.DLGC_STATIC);
	}
	if ((style & SWT.RADIO) != 0) {
        return new MRESULT (OS.DLGC_BUTTON | OS.DLGC_RADIOBUTTON);
    }
	if ((style & (SWT.CHECK | SWT.TOGGLE)) != 0) {
        return new MRESULT (OS.DLGC_BUTTON | OS.DLGC_CHECKBOX);
    }
	return result;
}

MRESULT WM_SETFOCUS (int mp1, int mp2) {
    boolean gotFocus = OS.SHORT1FROMMP (mp2) > 0;
    MRESULT result = null;
    if (gotFocus) {    
        /*
         *  Feature in OS/2. When OS/2 sets focus to
         *  a radio button, it sets the WM_TABSTOP style.
         *  This is undocumented and unwanted.  The fix is
         *  to save and restore the window style bits.
         */
        int bits = 0;
        if ((style & SWT.RADIO) != 0) {
            bits = OS.WinQueryWindowULong (handle, OS.QWL_STYLE);
        }
        result = super.WM_SETFOCUS (mp1, mp2);
        if ((style & SWT.RADIO) != 0) {
            OS.WinSetWindowULong (handle, OS.QWL_STYLE, bits);
        }
        if ((style & SWT.PUSH) != 0) {
            menuShell ().setDefaultButton (this, false);
        }
    } else {
        result = super.WM_SETFOCUS (mp1, mp2);
        if ((style & SWT.PUSH) != 0 && getDefault ()) {
            menuShell ().setDefaultButton (null, false);
        }
    }
    /*
     *  Feature in OS/2. The standard WC_BUTTON window procedure always
     *  draws the focus rectangle itself, even for BS_USERBUTTON windows.
     *  In the latter case, the focus rectangle is the same as the window
     *  rectagle, which is unwanted for SWT.TOGGLE buttons with text. To
     *  avoid this we don't call the WC_BUTTON window procedure for the
     *  WM_SETFOCUS message (see callWindowProc()), but draw the focus
     *  rectangle ourselves (here and in BN_PAINT).
     */
    if (gotFocus) {
        if ((style & SWT.TOGGLE) != 0 && image == null) {
            OS.WinInvalidateRect (handle, null, false);
        } else {
            Widget oldFocus = WidgetTable.get (mp1);
            if (oldFocus != null && oldFocus instanceof Button) {
                Button b = (Button) oldFocus;
                if ((b.style & SWT.TOGGLE) != 0 && b.image == null)
                    OS.WinInvalidateRect (b.handle, null, false);
            }
        }
    } else {
        if ((style & SWT.TOGGLE) != 0 && image == null)
            if (WidgetTable.get (mp1) == null)
                drawFocusRect();
    }
    
    return result;    
}

MRESULT wmCommandChild (int mp1, int mp2) {
    if ((style & (SWT.CHECK | SWT.TOGGLE | SWT.RADIO)) == 0)
        postEvent (SWT.Selection);
	return super.wmCommandChild (mp1, mp2);
}

MRESULT wmControlChild (int mp1, int mp2) {
    int code = mp1 >> 16;
    switch (code) {
        case OS.BN_CLICKED:
        case OS.BN_DBLCLICKED:
            if ((style & (SWT.PUSH | SWT.ARROW)) != 0)
                break;
			if ((style & (SWT.CHECK | SWT.TOGGLE)) != 0) {
				setSelection (!getSelection ());
			} else {
				if ((style & SWT.RADIO) != 0) {
					if ((parent.getStyle () & SWT.NO_RADIO_GROUP) == 0)
						selectRadio ();
                    else
						setSelection (!getSelection ());
				}
			}
			postEvent (SWT.Selection);
            break;
        case OS.BN_PAINT:
            return wmDrawChild (mp1, mp2);
    }
	return super.wmControlChild (mp1, mp2);
}

MRESULT wmDrawChild (int mp1, int mp2) {
    SWP swp = new SWP();
    getBounds (swp);
    if (swp.cx == 0 || swp.cy == 0) return MRESULT.ZERO;

    int[] userbutton = new int [4 * 4];
    OS.objcpy (userbutton, mp2);
    int hpsButton = userbutton [1];
    int state = userbutton [2];
    int oldState = userbutton [3];
    
    boolean checked = OS.WinSendMsg (handle, OS.BM_QUERYCHECK, 0, 0) != 0;
    boolean hilited = (state & OS.BDS_HILITED) != 0;
    boolean def = (state & OS.BDS_DEFAULT) != 0;
    boolean enabled = (state & OS.BDS_DISABLED) == 0;
    
    boolean flat = (style & SWT.FLAT) != 0;
    boolean checkOrRadio = (style & (SWT.CHECK | SWT.RADIO)) != 0;

    Display display = getDisplay();
    int[] pnt = new int [] {0, 0};
    
    int focusX = 0, focusY = 0, focusW = 0, focusH = 0;
    
    /*
     *  Optimization of the TOGGLE button. We do not draw the "not hilited"
     *  state if it has arrived right after the "hilited" assuming that the
     *  WM_COMMAND handler (wmCommandChild()) will immediately switch the
     *  "checked" and we will be called again.
     */
    if ((style & SWT.TOGGLE) != 0 && !hilited && (oldState & OS.BDS_HILITED) != 0) {
        OS.WinQueryMsgPos (0, pnt);
        OS.WinMapWindowPoints (OS.HWND_DESKTOP, handle, pnt, 1);
        if (pnt [0] > 0 && pnt [1] > 0 && pnt [0] < swp.cx && pnt [1] < swp.cy)
            return MRESULT.ZERO;
    }
    
    int hps = hpsButton;
    int hpsMem = 0, hdcMem = 0, hbmMem = 0;
    
    /*
     *  Use doublebuffer only when drawing images or big arrows. Also use it
     *  for toggle buttons to avoid artefacts when drawing outlined boxes using
     *  LINETYPE_ALTERNATE (i.e. focus rects) on some videocads/videodrivers.
     */
    if (
        image != null ||
        ((style & SWT.ARROW) != 0 && swp.cx > 22 && swp.cy > 22) ||
        (style & SWT.TOGGLE) != 0
    ) {
        hdcMem = OS.DevOpenDC (
            0, OS.OD_MEMORY, new PSZ ("*"), 4,
            new PSZ[] {new PSZ ("Display"), null, null, null}, 0
        );
        hpsMem = OS.GpiCreatePS (
            0, hdcMem, new int[] {swp.cx, swp.cy},
            OS.PU_PELS | OS.GPIA_ASSOC | OS.GPIT_MICRO
        );
        BITMAPINFOHEADER2 hdr = new BITMAPINFOHEADER2();
        hdr.cx = swp.cx;
        hdr.cy = swp.cy;
        hdr.cPlanes = 1;
        hdr.cBitCount = (short) getDisplay().getDepth();
        hdr.ulCompression = OS.BCA_UNCOMP;
        hdr.cbImage = 0;
        hdr.cxResolution = 0;
        hdr.cyResolution = 0;
        hdr.cclrUsed = 0;
        hdr.cclrImportant = 0;
        hdr.usUnits = OS.BRU_METRIC;
        hdr.usReserved = 0;
        hdr.usRecording = OS.BRA_BOTTOMUP;
        hdr.usRendering = OS.BRH_NOTHALFTONED;
        hdr.cSize1 = 0;
        hdr.cSize2 = 0;
        hdr.ulColorEncoding = OS.BCE_RGB;
        hdr.ulIdentifier = 0;
        hbmMem = OS.GpiCreateBitmap (hpsMem, hdr, 0, null, null);
        OS.GpiSetBitmap (hpsMem, hbmMem);
//@@TODO (dmik): replace with GpiQueryLogicalFont/GpiCreateLogicalFont
        if (image == null)
            GC.internal_set_font (hpsMem, hdcMem, display, getFont().internal_get_handle());
        hps = hpsMem;
    }

    do {
        // hps is unitialized here, do some initializations
        OS.GpiSetMix (hps, OS.FM_OVERPAINT);
        OS.GpiSetBackMix (hps, OS.BM_OVERPAINT);
        OS.GpiSetPattern (hps, OS.PATSYM_BLANK);
        int hPalette = getDisplay ().hPalette;
        if (hPalette == 0)
            OS.GpiCreateLogColorTable (hps, 0, OS.LCOLF_RGB, 0, 0, null);
        else
            OS.GpiSelectPalette (hps, hPalette);
    
        int parentBackground = parent.getBackgroundPixel();
        int bw = ((style & (SWT.PUSH | SWT.TOGGLE)) != 0) ? 1 : 0;
        int hrgn = 0, hrgnOld[] = new int [1];
        
        // padding
        OS.GpiSetColor (hps, parentBackground);
        if (bw > 0) {
            pnt [0] = 0; pnt [1] = 0;
            OS.GpiMove (hps, pnt);
            pnt [0] = swp.cx - 1; pnt [1] = swp.cy - 1;
            OS.GpiBox (hps, OS.DRO_OUTLINE, pnt, 0, 0);
        }
        
        if (flat || checkOrRadio) {
            pnt [0] = bw; pnt [1] = bw;
            OS.GpiMove (hps, pnt);
            pnt [0] = swp.cx - bw - 1; pnt [1] = swp.cy - bw - 1;
            if ((style & SWT.TOGGLE) != 0 && checked) {
                OS.GpiSetColor (hps, parentBackground);
                OS.GpiSetBackColor (hps, OS.SYSCLR_BUTTONLIGHT);
                OS.GpiSetPattern (hps, OS.PATSYM_HALFTONE);
            } else {
                if (checkOrRadio) OS.GpiSetBackColor (hps, getBackgroundPixel());
                else OS.GpiSetBackColor (hps, parentBackground);
            }
            OS.GpiBox (hps, OS.DRO_FILL, pnt, 0, 0);
        } else {
            pnt [0] = bw; pnt [1] = bw;
            OS.GpiSetPel (hps, pnt);
            pnt [0] = swp.cx - bw - 1; pnt [1] = swp.cy - bw - 1;
            OS.GpiSetPel (hps, pnt);
            OS.GpiSetColor (hps, OS.SYSCLR_BUTTONDARK);
            pnt[0] = bw; pnt [1] = bw + 1; OS.GpiMove (hps, pnt);
            pnt [1] = swp.cy - bw - 1; OS.GpiLine (hps, pnt);
            pnt [0] = swp.cx - bw - 2; OS.GpiLine (hps, pnt);
            OS.GpiSetColor (hps, OS.SYSCLR_BUTTONLIGHT);
            pnt[0] ++; pnt [1] --; OS.GpiMove (hps, pnt);
            pnt [1] = bw; OS.GpiLine (hps, pnt);
            pnt [0] = bw + 1; OS.GpiLine (hps, pnt);
            
            int clr1, clr2;
            if (hilited || checked) {
                clr1 = OS.SYSCLR_BUTTONDARK;
                clr2 = OS.SYSCLR_BUTTONLIGHT;
            } else {
                clr1 = OS.SYSCLR_BUTTONLIGHT;
                clr2 = OS.SYSCLR_BUTTONDARK;
            }
            OS.GpiSetColor (hps, clr1);
            pnt [1] = swp.cy - bw - 2; OS.GpiLine (hps, pnt);
            pnt [0] = swp.cx - bw - 3; OS.GpiLine (hps, pnt);
            pnt [0] --; pnt [1] --; OS.GpiMove (hps, pnt);
            pnt [0] = bw + 2; OS.GpiLine (hps, pnt);
            pnt [1] = bw + 2; OS.GpiLine (hps, pnt);
            OS.GpiSetColor (hps, clr2);
            pnt [1] --; OS.GpiMove (hps, pnt);
            pnt [0] = swp.cx - bw - 2; OS.GpiLine (hps, pnt);
            pnt [1] = swp.cy - bw - 2; OS.GpiLine (hps, pnt);
            pnt [0] --; pnt [1] --; OS.GpiMove (hps, pnt);
            pnt [1] = bw + 2; OS.GpiLine (hps, pnt);
            pnt [0] = bw + 3; OS.GpiLine (hps, pnt);
        
            // do not do the rest if too small
            int lim = 6 + (bw << 1);
            if (swp.cx <= lim || swp.cy <= lim) break;
            
            pnt [1] ++;
            OS.GpiMove (hps, pnt);
            pnt [0] = swp.cx - bw - 4; pnt [1] = swp.cy - bw - 4;
            if ((style & SWT.TOGGLE) != 0 && checked && enabled) {
                OS.GpiSetColor (hps, OS.SYSCLR_BUTTONMIDDLE);
                OS.GpiSetBackColor (hps, OS.SYSCLR_BUTTONLIGHT);
                OS.GpiSetPattern (hps, OS.PATSYM_HALFTONE);
            } else {
                OS.GpiSetBackColor (hps, OS.SYSCLR_BUTTONMIDDLE);
            }
            OS.GpiBox (hps, OS.DRO_FILL, pnt, 0, 0);
        
            // set the clipping region to the internal button area
            hrgn = OS.GpiCreateRegion (hps, 1,
                new int[] {bw + 3, bw + 3, pnt [0] + 1, pnt [1] + 1});  
            OS.GpiSetClipRegion (hps, hrgn, hrgnOld);
        }        
    
        if ((style & SWT.ARROW) != 0) {
            pnt [0] = swp.cx / 2; pnt [1] = swp.cy / 2;
            if (hilited && !flat) {
                pnt [0] ++;
                pnt [1] --;
            }
            int half = swp.cx < swp.cy ? swp.cx : swp.cy;
            half -= (bw * 2) + 8;
            half /= 2;
            half -= 1; // -- inclusive
            if (half < 0) half = 0;
            int dx = 0, dy = 0;
            if ((style & (SWT.UP | SWT.DOWN)) != 0) {
                if ((style & SWT.UP) != 0) pnt [1] += half / 2;
                else pnt [1] -= half / 2 + 1;
                dx = (swp.cx % 2 == 0) ? 1 : 0;
            } else {
                if ((style & SWT.LEFT) != 0) pnt [0] += half / 2;
                else pnt [0] -= half / 2 + 1;
                dy = (swp.cy % 2 == 0) ? 1 : 0;
            }
            int pnts[] = new int [6];
    
            if (enabled) {
                OS.GpiSetBackColor (hps,
                    hilited && flat ? OS.SYSCLR_BUTTONDARK : OS.SYSCLR_MENUTEXT); 
            } else {
                OS.GpiSetColor (hps, OS.SYSCLR_MENUTEXT);
                OS.GpiSetBackColor (hps, OS.SYSCLR_BUTTONMIDDLE); 
                OS.GpiSetPattern (hps, OS.PATSYM_HALFTONE);
            }
    
            OS.GpiBeginArea (hps, 0);
            OS.GpiMove (hps, pnt);
            if ((style & (SWT.RIGHT | SWT.DOWN)) != 0) {
                // SE
                pnts [0] = pnt [0]; pnts [1] = pnt [1];
                pnts [2] = pnt [0]; pnts [3] = pnt [1] + half;
                pnts [4] = pnt [0] + half; pnts [5] = pnt [1]; 
                OS.GpiPolyLine (hps, pnts.length / 2, pnts);
            }
            if ((style & (SWT.DOWN | SWT.LEFT)) != 0) {
                // SW
                pnts [0] = pnt [0] - dx; pnts [1] = pnt [1];
                pnts [2] = pnt [0] - dx; pnts [3] = pnt [1] + half;
                pnts [4] = pnt [0] - dx - half; pnts [5] = pnt [1]; 
                OS.GpiPolyLine (hps, pnts.length / 2, pnts);
            }
            if ((style & (SWT.LEFT | SWT.UP)) != 0) {
                // NW
                pnts [0] = pnt [0] - dx; pnts [1] = pnt [1] - dy;
                pnts [2] = pnt [0] - dx; pnts [3] = pnt [1] - dy - half;
                pnts [4] = pnt [0] - dx - half; pnts [5] =  pnt [1] - dy; 
                OS.GpiPolyLine (hps, pnts.length / 2, pnts);
            }
            if ((style & (SWT.UP | SWT.RIGHT)) != 0) {
                // NE
                pnts [0] = pnt [0]; pnts [1] = pnt [1] - dy;
                pnts [2] = pnt [0]; pnts [3] = pnt [1] - dy - half;
                pnts [4] = pnt [0] + half; pnts [5] = pnt [1] - dy; 
                OS.GpiPolyLine (hps, pnts.length / 2, pnts);
            }
            OS.GpiEndArea (hps);
        } else if ((style & SWT.TOGGLE) != 0 && image == null) {
            FONTMETRICS fm = new FONTMETRICS();
            OS.GpiQueryFontMetrics (hps, FONTMETRICS.sizeof, fm);
            Point size = new Point (0, 0);
            byte[] text = computeWindowTextSize (hpsButton, fm, size);
            if (text != null) {
                RECTL rcl = new RECTL();
                int cx = swp.cx; 
                if (!flat) cx --; // for compatibility with the standard pushbutton
                rcl.xLeft = (cx - size.x) / 2;
                rcl.yBottom = (swp.cy - size.y) / 2;
                if (hilited && !flat) {
                    rcl.xLeft ++; rcl.yBottom --;
                }
                rcl.xRight = rcl.xLeft + size.x;
                rcl.yTop = rcl.yBottom + size.y;
                OS.GpiSetBackMix (hps, OS.BM_LEAVEALONE);
                OS.GpiSetPattern (hps, OS.PATSYM_SOLID);
                OS.WinDrawText (
                    hps, -1, text, rcl,
                    hilited && flat ? OS.SYSCLR_BUTTONDARK : OS.SYSCLR_MENUTEXT, 0,
                    OS.DT_MNEMONIC
                );
                if (OS.WinQueryFocus (OS.HWND_DESKTOP) == handle && !hilited) {
                    // prepare to draw the focus rect
                    focusX = (swp.cx - size.x) / 2 - 1;
                    focusY = (swp.cy - size.y) / 2 - 1;
                    focusW = size.x + 2;
                    focusH = size.y + 2;
                }
            }
        } else if (checkOrRadio) {
            int hBitmap = OS.WinGetSysBitmap (OS.HWND_DESKTOP, OS.SBMP_CHECKBOXES);
            int y = (swp.cy - CheckHeight) / 2;
            int dy = (style & SWT.RADIO) != 0 ? 1 : 2;
            dy = dy * CheckHeight;
            int dx = hilited ? 2 : 0;
            if (checked) dx ++;
            dx = dx * CheckWidth;
            OS.GpiWCBitBlt (hps, hBitmap, 4,
                new int[] {
                    0, y, CheckWidth - 1, y + CheckHeight - 1,
                    dx, dy, dx + CheckWidth, dy + CheckHeight},
                OS.ROP_SRCCOPY, OS.BBO_IGNORE);
            OS.GpiDeleteBitmap (hBitmap);
        }
        
        if (image != null && (style & SWT.ARROW) == 0) {
            Rectangle bounds = image.getBounds();
            int x = 0;
            int y = (swp.cy - bounds.height) / 2;
            if (checkOrRadio) {
                x = CheckWidth + 4;
            } else {
                x = (swp.cx - bounds.width) / 2;
                if (hilited) {            
                    x ++; y --;
                }
            }
            if (image.type == SWT.BITMAP) {
                OS.GpiWCBitBlt (hps, image.handle, 4,
                    new int[] {
                        x, y, x + bounds.width - 1, y + bounds.height - 1,
                        0, 0, bounds.width, bounds.height
                    },
                    OS.ROP_SRCCOPY, OS.BBO_IGNORE);
            } else if (image.type == SWT.ICON) {
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
        }
        
        if (hrgn != 0) {
            OS.GpiSetClipRegion (hps, hrgnOld [0], hrgnOld);
            OS.GpiDestroyRegion (hps, hrgn);
        }
        
        // draw the focus rect
        if (focusW > 0 && focusH > 0) {
            int[] lineBundle = {
                0, // lColor
                OS.CLR_WHITE, // lBackColor
                (OS.BM_XOR << 16) | OS.FM_LEAVEALONE, // usMixMode, usBackMixMode
                0, // fxWidth
                0, // lGeomWidth
                OS.LINETYPE_ALTERNATE, // usType & usEnd
                0, // usJoin & usReserved
            };
            OS.GpiSetAttrs (
                hps, OS.PRIM_LINE,
                OS.LBB_BACK_COLOR | OS.LBB_MIX_MODE |
                    OS.LBB_BACK_MIX_MODE | OS.LBB_TYPE,
                0, lineBundle
            );
            pnt[0] = focusX;
            pnt[1] = focusY;
            OS.GpiMove (hps, pnt);
            pnt[0] = focusX + focusW - 1;
            pnt[1] = focusY + focusH - 1;
            OS.GpiBox (hps, OS.DRO_OUTLINE, pnt, 0, 0);
        }
    
        // draw the default button rect
        if (!flat && def && (style & SWT.PUSH) != 0) {
            OS.GpiSetColor (hps, OS.SYSCLR_BUTTONDEFAULT);
            pnt [0] = 0; pnt [1] = 0;
            OS.GpiMove (hps, pnt);
            pnt [0] = swp.cx - 1; pnt [1] = swp.cy - 1;
            OS.GpiBox (hps, OS.DRO_OUTLINE, pnt, 0, 0);
            pnt [0] = 1; pnt [1] = 1;
            OS.GpiMove (hps, pnt);
            pnt [0] = swp.cx - 2; pnt [1] = swp.cy - 2;
            OS.GpiBox (hps, OS.DRO_OUTLINE, pnt, 0, 0);
        }
        
        if (!enabled && ((style & (SWT.PUSH | SWT.TOGGLE)) != 0 || checkOrRadio)) {
            OS.GpiSetColor (hps, checkOrRadio ? parentBackground : OS.SYSCLR_BUTTONMIDDLE);
            OS.GpiSetBackMix (hps, OS.BM_LEAVEALONE);
            OS.GpiSetPattern (hps, OS.PATSYM_HALFTONE);
            pnt [0] = 0; pnt [1] = 0;
            OS.GpiMove (hps, pnt);
            pnt [0] = swp.cx - 1; pnt [1] = swp.cy - 1;
            OS.GpiBox (hps, OS.DRO_FILL, pnt, 0, 0);
        }
    } while (false);

    if (hpsMem != 0) {
        OS.GpiBitBlt (hpsButton, hpsMem, 4,
            new int[] {
                0, 0, swp.cx, swp.cy,
                0, 0, swp.cx, swp.cy
            },
            OS.ROP_SRCCOPY, OS.BBO_IGNORE
        );
        OS.GpiDestroyPS (hpsMem);
        OS.DevCloseDC (hdcMem);
        OS.GpiDeleteBitmap (hbmMem);
    }

//@@TODO (dmik): debug code. remove    
//    System.out.print("BN_PAINT: " + getText());
//    System.out.print("  checked = " + checked);
//    System.out.print("  hilited = " + hilited);
////    System.out.println("  default = " + def);
//    System.out.println("  enabled = " + enabled);

    return MRESULT.ZERO;
}

}
