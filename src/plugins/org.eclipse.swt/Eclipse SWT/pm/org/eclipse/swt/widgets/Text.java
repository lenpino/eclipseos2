package org.eclipse.swt.widgets;

/*
 * Copyright (c) 2000, 2002 IBM Corp.  All rights reserved.
 * This file is made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 */

import org.eclipse.swt.internal.*;
import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.internal.pm.*;

/**
 * Instances of this class are selectable user interface
 * objects that allow the user to enter and modify text.
 * <p>
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>MULTI, SINGLE, READ_ONLY, WRAP</dd>
 * <dt><b>Events:</b></dt>
 * <dd>DefaultSelection, Modify, Verify</dd>
 * </dl>
 * <p>
 * Note: Only one of the styles MULTI and SINGLE may be specified. 
 * </p><p>
 * IMPORTANT: This class is <em>not</em> intended to be subclassed.
 * </p>
 */
public class Text extends Scrollable {
	int tabs, oldStart, oldEnd;
        short textLimit = 0;
	boolean doubleClick, ignoreVerify, ignoreCharacter;
	
	public static final int LIMIT;
	public static final String DELIMITER;
	/*
	* These values can be different on different platforms.
	* Therefore they are not initialized in the declaration
	* to stop the compiler from inlining.
	*/
	static {
		LIMIT = 0x7FFF;
		DELIMITER = "\r\n";
	}
	
	static final int EditProc;
        static final int FieldProc;
        static final PSZ EditClass =  PSZ.getAtom (OS.WC_MLE);
        static final PSZ FieldClass =  PSZ.getAtom (OS.WC_ENTRYFIELD);
	static {
            CLASSINFO pclsiClassInfo = new CLASSINFO ();
            OS.WinQueryClassInfo (OS.NULLHANDLE, EditClass, pclsiClassInfo);
            EditProc = pclsiClassInfo.pfnWindowProc;
            CLASSINFO pclsiClassInfoF = new CLASSINFO ();
            OS.WinQueryClassInfo (OS.NULLHANDLE, FieldClass, pclsiClassInfoF);
            FieldProc = pclsiClassInfoF.pfnWindowProc;

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
 * @see SWT#SINGLE
 * @see SWT#MULTI
 * @see SWT#READ_ONLY
 * @see SWT#WRAP
 * @see Widget#checkSubclass
 * @see Widget#getStyle
 */
public Text (Composite parent, int style) {
	super (parent, checkStyle (style));
        if ((style & SWT.SINGLE) != 0)
            textLimit = 32;
}

int callWindowProc (int msg, int mp1, int mp2) {
    if (handle == 0) return 0;
    if ((style & SWT.MULTI) != 0)
        return OS.WinCallWindowProc (EditProc, handle, msg, mp1, mp2);
    else
        return OS.WinCallWindowProc (FieldProc, handle, msg, mp1, mp2);
}

void createHandle () {
	super.createHandle ();
}

/**
 * Adds the listener to the collection of listeners who will
 * be notified when the receiver's text is modified, by sending
 * it one of the messages defined in the <code>ModifyListener</code>
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
 * @see ModifyListener
 * @see #removeModifyListener
 */
public void addModifyListener (ModifyListener listener) {
	checkWidget ();
	if (listener == null) error (SWT.ERROR_NULL_ARGUMENT);
	TypedListener typedListener = new TypedListener (listener);
	addListener (SWT.Modify, typedListener);
}

/**
 * Adds the listener to the collection of listeners who will
 * be notified when the control is selected, by sending
 * it one of the messages defined in the <code>SelectionListener</code>
 * interface.
 * <p>
 * <code>widgetSelected</code> is not called for texts.
 * <code>widgetDefaultSelected</code> is typically called when ENTER is pressed in a single-line text.
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

/**
 * Adds the listener to the collection of listeners who will
 * be notified when the receiver's text is verified, by sending
 * it one of the messages defined in the <code>VerifyListener</code>
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
 * @see VerifyListener
 * @see #removeVerifyListener
 */
public void addVerifyListener (VerifyListener listener) {
	checkWidget ();
	if (listener == null) error (SWT.ERROR_NULL_ARGUMENT);
	TypedListener typedListener = new TypedListener (listener);
	addListener (SWT.Verify, typedListener);
}

/**
 * Appends a string.
 * <p>
 * The new text is appended to the text at
 * the end of the widget.
 * </p>
 *
 * @param string the string to be appended
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void append (String string) {
	checkWidget ();
	if (string == null) error (SWT.ERROR_NULL_ARGUMENT);
	string = Display.withCrLf (string);
	int length = OS.WinQueryWindowTextLength (handle);
	if (hooks (SWT.Verify)) {
		string = verifyText (string, length, length);
		if (string == null) return;
	}
        PSZ buffer =new PSZ(string);
        int newlength = length + buffer.toString().length();
        if ((style & SWT.MULTI) != 0){
            OS.WinSendMsg (handle, OS.MLM_SETSEL, length, length);
            OS.WinSendMsg (handle, OS.MLM_INSERT, buffer, 0);
            //@@TODO (lpino): Check if the next call is necesary in OS/2
//            OS.WinSendMsg (handle, OS.MLM_SETSEL, newlength, newlength);
        }
        else{
            OS.WinSendMsg (handle, OS.EM_SETSEL, OS.MPFROM2SHORT((short)length, (short)length), 0);
            OS.WinSetWindowText(handle, buffer);
            OS.WinSendMsg(handle, OS.EM_SETFIRSTCHAR, (short)(newlength), 0);
        }
}

static int checkStyle (int style) {
	if ((style & SWT.SINGLE) != 0) style &= ~(SWT.H_SCROLL | SWT.V_SCROLL);
	if ((style & (SWT.SINGLE | SWT.MULTI)) != 0) return style;
	if ((style & (SWT.H_SCROLL | SWT.V_SCROLL)) != 0) return style | SWT.MULTI;
	return style | SWT.SINGLE;
}

/**
 * Clears the selection.
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void clearSelection () {
	checkWidget ();
        if ((style & SWT.MULTI) != 0)
            OS.WinSendMsg(handle, OS.MLM_SETSEL, -1, 0);
        else
            OS.WinSendMsg(handle, OS.EM_SETSEL, 0, 0);
}

/**
 * Sets the receiver's size and location to the rectangular
 * area specified by the arguments. The <code>x</code> and
 * <code>y</code> arguments are relative to the receiver's
 * parent (or its display if its parent is null).
 * <p>
 * Note: Attempting to set the width or height of the
 * receiver to a negative number will cause that
 * value to be set to zero instead.
 * </p>
 *
 * @param x the new x coordinate for the receiver
 * @param y the new y coordinate for the receiver
 * @param width the new width for the receiver
 * @param height the new height for the receiver
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void setBounds (int x, int y, int width, int height) {
    checkWidget ();
    if ((style & SWT.MULTI) != 0){        
        setBounds (x, y, Math.max (0, width), Math.max (0, height),OS.SWP_MOVE | OS.SWP_SIZE);
    }
    else{
         //@@TODO(lpino): Find out why the control doesn't honor the position and sizes
        //                          For now fix it with the values that seem to be consistent
        
        setBounds (x+3, y+3, Math.max (0, width-6), Math.max (0, height-6),OS.SWP_MOVE | OS.SWP_SIZE);
    }
}



public Point computeSize (int wHint, int hHint, boolean changed) {
	checkWidget ();
        Point size = new Point (0, 0);
	int count = ((style & SWT.MULTI) != 0)?OS.WinSendMsg (handle, OS.MLM_QUERYLINECOUNT, 0, 0):1;
        int hps = this.hps;
        if (hps == 0) hps = OS.WinGetPS (handle);
        FONTMETRICS fm = new FONTMETRICS();
        OS.GpiQueryFontMetrics (hps, FONTMETRICS.sizeof, fm);
	int height = count * fm.lEmHeight, width = 0;

	boolean wrap = (style & SWT.MULTI) != 0 && (style & SWT.WRAP) != 0;

/* 	int[] pts = new int[8];
	OS.WinSendMsg (handle, OS.MLM_QUERYFORMATRECT, pts, OS.MLFFMTRECT_MATCHWINDOW);
 */        
 	int length = OS.WinQueryWindowTextLength (handle);
	System.out.println("Text::computeSize -> text = " + getText());
	System.out.println("Text::computeSize -> lineas = " + count);
	System.out.println("Text::computeSize -> handle = " + Integer.toHex);
        byte[] text = null;
        if (length != 0) {
            RECTL rect = new RECTL();
            text = getText().getBytes();
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
        width += size.x;
	System.out.println("Text::computeSize -> ancho = " + width);
        if (this.hps == 0) OS.WinReleasePS (hps);
//	if (wrap && hHint == SWT.DEFAULT) {
//		int newHeight = rect.bottom - rect.top;
//		if (newHeight != 0) height = newHeight;
//	}
//	if (newFont != 0) OS.SelectObject (hDC, oldFont);
//	OS.ReleaseDC (handle, hDC);
        
	if (width == 0) width = DEFAULT_WIDTH;
	if (height == 0) height = DEFAULT_HEIGHT;
	if (wHint != SWT.DEFAULT) width = wHint;
	if (hHint != SWT.DEFAULT) height = hHint;
//
//	/* Calculate the margin width */
//	int margins = OS.SendMessage(handle, OS.EM_GETMARGINS, 0, 0);
//	int marginWidth = (margins & 0xFFFF) + ((margins >> 16) & 0xFFFF);
//	width += marginWidth;
//	
//	/*
//	* The preferred height of a single-line text widget
//	* has been hand-crafted to be the same height as
//	* the single-line text widget in an editable combo
//	* box.
//	*/
//	width += editRect.left * 2;
//	height += editRect.top * 2;
	if ((style & SWT.V_SCROLL) != 0) {
		width += OS.WinQuerySysValue (OS.HWND_DESKTOP, OS.SV_CXVSCROLL);
	}
	if ((style & SWT.H_SCROLL) != 0) {
		height += OS.WinQuerySysValue (OS.HWND_DESKTOP, OS.SV_CYHSCROLL);
		if ((style & SWT.BORDER) == 0) width++;
	}
	if ((style & SWT.BORDER) != 0) {
		int border = getBorderWidth ();
		width += (border * 2) + 3;
		height += (border * 2) + 1;
	}
	return new Point (width, height);
}

/**
 * Copies the selected text.
 * <p>
 * The current selection is copied to the clipboard.
 * </p>
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void copy () {
	checkWidget ();
        if ((style & SWT.MULTI) != 0)
            OS.WinSendMsg (handle, OS.MLM_COPY, 0, 0);
        else
            OS.WinSendMsg (handle, OS.EM_COPY, 0, 0);
}

void createWidget () {
	super.createWidget ();
	doubleClick = true;
//	setTabStops (tabs = 8);
}

/**
 * Cuts the selected text.
 * <p>
 * The current selection is first copied to the
 * clipboard and then deleted from the widget.
 * </p>
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void cut () {
	checkWidget ();
        if ((style & SWT.MULTI) != 0)
            OS.WinSendMsg (handle, OS.MLM_CUT, 0, 0);
        else
            OS.WinSendMsg (handle, OS.EM_CUT, 0, 0);
}

int defaultBackground () {
        if ((style & SWT.MULTI) != 0){
            if(OS.IsWarp3)
                return OS.WinSendMsg (handle, OS.MLM_QUERYBACKCOLOR, OS.MLE_INDEX, 0);
            else
                return OS.WinSendMsg (handle, OS.MLM_QUERYBACKCOLOR, OS.MLE_RGB, 0);
        }
        else
            return OS.WinQuerySysColor(handle, OS.SYSCLR_ENTRYFIELD,0);
}

/**
 * Gets the line number of the caret.
 * <p>
 * The line number of the caret is returned.
 * </p>
 *
 * @return the line number
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public int getCaretLineNumber () {
	checkWidget ();
        if ((style & SWT.MULTI) != 0)
            return OS.WinSendMsg(handle, OS.MLM_CHARFROMLINE, -1, 0);
        else
            return 1;
}

/**
 * Gets the location the caret.
 * <p>
 * The location of the caret is returned.
 * </p>
 *
 * @return a point, the location of the caret
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public Point getCaretLocation () {
	checkWidget ();
/* 	int pos = 0;
	if ((style & SWT.MULTI) != 0){
		pos = OS.WinSendMsg (handle, OS.MLM_QUERYSEL, OS.MLFQS_MINSEL, 0);
	} else {
		pos = OS.WinSendMsg (handle, OS.EM_QUERYSEL, 0, 0);
	}
	return new Point ((short) (pos & 0xFFFF), (short) (pos >> 16));
	*/
	return null;
}

/**
 * Gets the position of the caret.
 * <p>
 * The character position of the caret is returned.
 * </p>
 *
 * @return the position of the caret
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public int getCaretPosition () {
	checkWidget ();
        if ((style & SWT.MULTI) != 0){
            int min = OS.WinSendMsg (handle, OS.MLM_QUERYSEL, OS.MLFQS_MINSEL, 0);
            int max = OS.WinSendMsg (handle, OS.MLM_QUERYSEL, OS.MLFQS_MAXSEL, 0);
            int startLine = OS.WinSendMsg (handle, OS.MLM_LINEFROMCHAR, min, 0);
            int caretPos = OS.WinSendMsg (handle, OS.MLM_CHARFROMLINE, -1, 0);
            int caretLine = OS.WinSendMsg (handle, OS.MLM_LINEFROMCHAR, caretPos, 0);
            int caret = max;
            if (caretLine == startLine) caret = min;
            //@@TODO(lpino): Implement the DB supportz
//            if (OS.IsDBLocale) caret = mbcsToWcsPos (caret);
            return caret;
        }
        else{
            int pos = OS.WinSendMsg (handle, OS.EM_QUERYSEL, 0, 0);
            return OS.SHORT1FROMMP(pos);
        }
}

/**
 * Gets the number of characters.
 *
 * @return number of characters in the widget
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public int getCharCount () {
	checkWidget ();
	int length = OS.WinQueryWindowTextLength (handle);
         //@@TODO(lpino): Implement the DB support
//	if (OS.IsDBLocale) length = mbcsToWcsPos (length);
	return length;
}
//
String getClipboardText () {
	String string = "";
	int hab = parent.getDisplay ().hab;
	if (OS.WinOpenClipbrd (hab)) {
//		int hMem = OS.GetClipboardData (OS.IsUnicode ? OS.CF_UNICODETEXT : OS.CF_TEXT);
//		if (hMem != 0) {
//			int byteCount = OS.GlobalSize (hMem);
//			int ptr = OS.GlobalLock (hMem);
//			if (ptr != 0) {
//				/* Use the character encoding for the default locale */
//				TCHAR buffer = new TCHAR (0, byteCount / TCHAR.sizeof);
//				OS.MoveMemory (buffer, ptr, byteCount);
//				string = buffer.toString (0, buffer.strlen ());
//				OS.GlobalUnlock (hMem);
//			}
//		}
		OS.WinCloseClipbr (hab);
	}
	return string;
}

/**
 * Gets the double click enabled flag.
 * <p>
 * The double click flag enables or disables the
 * default action of the text widget when the user
 * double clicks.
 * </p>
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public boolean getDoubleClickEnabled () {
	checkWidget ();
	return doubleClick;
}

/**
 * Gets the echo character.
 * <p>
 * The echo character is the character that is
 * displayed when the user enters text or the
 * text is changed by the programmer.
 * </p>
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public char getEchoChar () {
	checkWidget ();
        //@@TODO(lpino): Find out how to implement this. Maybe there is no need
//	char echo = (char) OS.SendMessage (handle, OS.EM_GETPASSWORDCHAR, 0, 0);
//	if (echo != 0 && (echo = mbcsToWcs (echo, getCodePage ())) == 0) echo = '*';
//	return echo;
        return '*';
}

/**
 * Gets the editable state.
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public boolean getEditable () {
	checkWidget ();
	int bits = OS.WinQueryWindowULong (handle, OS.QWL_STYLE);
        if ((style & SWT.MULTI) != 0)
            return (bits & OS.MLS_READONLY) == 0;
        else
            return (bits & OS.ES_READONLY) == 0;
}
//
/**
 * Gets the number of lines.
 *
 * @return the number of lines in the widget
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public int getLineCount () {
	checkWidget ();
        if ((style & SWT.MULTI) != 0)
            return OS.WinSendMsg (handle, OS.MLM_QUERYLINECOUNT, 0, 0);
        else
            return 1;
}

/**
 * Gets the line delimiter.
 *
 * @return a string that is the line delimiter
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public String getLineDelimiter () {
	checkWidget ();
	return DELIMITER;
}

/**
 * Gets the height of a line.
 *
 * @return the height of a row of text
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public int getLineHeight () {
	checkWidget ();
//	int newFont, oldFont = 0;
//	int hDC = OS.GetDC (handle);
//	newFont = OS.SendMessage (handle, OS.WM_GETFONT, 0, 0);
//	if (newFont != 0) oldFont = OS.SelectObject (hDC, newFont);
//	TEXTMETRIC tm = new TEXTMETRIC ();
//	OS.GetTextMetrics (hDC, tm);
//	if (newFont != 0) OS.SelectObject (hDC, oldFont);
//	OS.ReleaseDC (handle, hDC);
//	return tm.tmHeight;
        
        //@@TODO(lpino): Implement MLM_QUERYFONT
//        if ((style & SWT.MULTI) != 0){
//            FATTRS hFont;
//            OS.WinSendMsg (handle, OS.MLM_QUERYFONT, hFont, 0);
//        }else{
            int hps = this.hps;
            if (hps == 0) hps = OS.WinGetPS (handle);
            FONTMETRICS fm = new FONTMETRICS();
            OS.GpiQueryFontMetrics (hps, FONTMETRICS.sizeof, fm);
            return fm.lEmHeight;
//        }
//        return 0;
}

/**
 * Gets the position of the selected text.
 * <p>
 * Indexing is zero based.  The range of
 * a selection is from 0..N where N is
 * the number of characters in the widget.
 * </p>
 * 
 * @return the start and end of the selection
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public Point getSelection () {
	checkWidget ();
	int pos = 0;
	if ((style & SWT.MULTI) != 0){
		pos = OS.WinSendMsg (handle, OS.MLM_QUERYSEL, (short)OS.MLFQS_MINMAXSEL , 0);
	}else{
		pos = OS.WinSendMsg (handle, OS.EM_QUERYSEL, 0, 0);
	}
        //@@TODO(lpino): Do we need to consider DB?
//	if (OS.IsDBLocale) {
//		start [0] = mbcsToWcsPos (start [0]);
//		end [0] = mbcsToWcsPos (end [0]);
//	}
	return new Point (OS.SHORT1FROMMP(pos), OS.SHORT2FROMMP(pos));
}

/**
 * Gets the number of selected characters.
 *
 * @return the number of selected characters.
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public int getSelectionCount () {
	checkWidget ();
	Point selection = getSelection ();
	return selection.y - selection.x;
}

/**
 * Gets the selected text.
 *
 * @return the selected text
 * 
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public String getSelectionText () {
	checkWidget ();
	/*
	* NOTE: The current implementation uses substring ()
	* which can reference a potentially large character
	* array.
	*/
	Point selection = getSelection ();
	return getText ().substring (selection.x, selection.y);
}

/**
 * Gets the number of tabs.
 * <p>
 * Tab stop spacing is specified in terms of the
 * space (' ') character.  The width of a single
 * tab stop is the pixel width of the spaces.
 * </p>
 *
 * @return the number of tab characters
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public int getTabs () {
	checkWidget ();
	return tabs;
}

int getTabWidth (int tabs) {
        int hps = this.hps;
        if (hps == 0) hps = OS.WinGetPS (handle);
        int extX = 0, dx = 0;
        int[] pnts = new int [OS.TXTBOX_COUNT * 2];
//        byte[] text = (new String(" ")).getBytes();
        PSZ buffer = new PSZ (" ");
        OS.GpiQueryTextBox (hps, 1, buffer.getBytes(), OS.TXTBOX_COUNT, pnts);
        extX += pnts [8];
        dx = pnts [4];
        if (dx < pnts [8]) dx = pnts [8];
        dx = dx - pnts [8];
//        System.out.println("TOTAL = " + (extX + dx));
//        for(int i=0;i<OS.TXTBOX_COUNT * 2;i++){
//            System.out.println("Punto " + i + "=" + pnts[i]);
//        }
	return extX + dx;
}

/**
 * Gets the widget text.
 * <p>
 * The text for a text widget is the characters in the widget.
 * </p>
 *
 * @return the widget text
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public String getText () {
	checkWidget ();
	int length = OS.WinQueryWindowTextLength (handle);
	if (length == 0) return "";
        PSZ buffer = new PSZ(length);
	OS.WinQueryWindowText (handle, length + 1, buffer);
	return buffer.toString ();
}

/**
 * Gets a range of text.
 * <p>
 * Indexing is zero based.  The range of
 * a selection is from 0..N-1 where N is
 * the number of characters in the widget.
 * </p>
 *
 * @param start the start of the range
 * @param end the end of the range
 * @return the range of text
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public String getText (int start, int end) {
	checkWidget ();
	/*
	* NOTE: The current implementation uses substring ()
	* which can reference a potentially large character
	* array.
	*/
	return getText ().substring (start, end + 1);
}

/**
 * Returns the maximum number of characters that the receiver is capable of holding. 
 * <p>
 * If this has not been changed by <code>setTextLimit()</code>,
 * it will be the constant <code>Text.LIMIT</code>.
 * </p>
 * 
 * @return the text limit
 * 
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public int getTextLimit () {
	checkWidget ();
        if ((style & SWT.MULTI) != 0)
            return OS.WinSendMsg (handle, OS.MLM_QUERYTEXTLIMIT, 0, 0);
        else
            return textLimit;
}

/**
 * Returns the zero-relative index of the line which is currently
 * at the top of the receiver.
 * <p>
 * This index can change when lines are scrolled or new lines are added or removed.
 * </p>
 *
 * @return the index of the top line
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public int getTopIndex () {
	checkWidget ();
	 if ((style & SWT.MULTI) == 0) return 0;
        int pos = OS.WinSendMsg(handle, OS.MLM_QUERYFIRSTCHAR, 0, 0);
	return OS.WinSendMsg (handle, OS.MLM_LINEFROMCHAR, pos, 0);
}

/**
 * Gets the top pixel.
 * <p>
 * The top pixel is the pixel position of the line
 * that is currently at the top of the widget.  On
 * some platforms, a text widget can be scrolled by
 * pixels instead of lines so that a partial line
 * is displayed at the top of the widget.
 * </p><p>
 * The top pixel changes when the widget is scrolled.
 * The top pixel does not include the widget trimming.
 * </p>
 *
 * @return the pixel position of the top line
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
//public int getTopPixel () {
//	checkWidget ();
//	/*
//	* Note, EM_GETSCROLLPOS is implemented in Rich Edit 3.0
//	* and greater.  The plain text widget and previous versions
//	* of Rich Edit return zero.
//	*/
//	int [] buffer = new int [2];
//	int code = OS.SendMessage (handle, OS.EM_GETSCROLLPOS, 0, buffer);
//	if (code == 1) return buffer [1];
//	return getTopIndex () * getLineHeight ();
//}

/*
* Currently not used.
*/
boolean getWrap () {
	checkWidget ();
	int bits = OS.WinQueryWindowULong (handle, OS.QWL_STYLE);
	if ((style & SWT.MULTI) == 0) return false;
	return (bits & (OS.MLS_HSCROLL)) == 0;
}

/**
 * Inserts a string.
 * <p>
 * The old selection is replaced with the new text.
 * </p>
 *
 * @param string the string
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void insert (String string) {
	checkWidget ();
	if (string == null) error (SWT.ERROR_NULL_ARGUMENT);
	string = Display.withCrLf (string);
        if ((style & SWT.MULTI) != 0){
            if (hooks (SWT.Verify)) {
                    int min = OS.WinSendMsg (handle, OS.MLM_QUERYSEL, OS.MLFQS_MINSEL, 0);
                    int max = OS.WinSendMsg (handle, OS.MLM_QUERYSEL, OS.MLFQS_MAXSEL, 0);
                    string = verifyText (string, min, max);
                    if (string == null) return;
            }
            PSZ buffer = new PSZ (string);
            OS.WinSendMsg (handle, OS.MLM_INSERT, buffer, 0);
        }
        else{
            if (hooks (SWT.Verify)) {
                int pos = OS.WinSendMsg (handle, OS.EM_QUERYSEL, 0, 0);
                string = verifyText (string, OS.SHORT1FROMMP(pos), OS.SHORT2FROMMP(pos));
                if (string == null) return;
            }
            PSZ buffer = new PSZ (string);
            OS.WinSetWindowText(handle, buffer);
        }
}
//
//int mbcsToWcsPos (int mbcsPos) {
//	if (mbcsPos == 0) return 0;
//	if (OS.IsUnicode) return mbcsPos;
//	int cp = getCodePage ();
//	int wcsTotal = 0, mbcsTotal = 0;
//	byte [] buffer = new byte [128];
//	String delimiter = getLineDelimiter();
//	int delimiterSize = delimiter.length ();
//	int count = OS.SendMessageA (handle, OS.EM_GETLINECOUNT, 0, 0);
//	for (int line=0; line<count; line++) {
//		int wcsSize = 0;
//		int linePos = OS.SendMessageA (handle, OS.EM_LINEINDEX, line, 0);
//		int mbcsSize = OS.SendMessageA (handle, OS.EM_LINELENGTH, linePos, 0);
//		if (mbcsSize != 0) {
//			if (mbcsSize + delimiterSize > buffer.length) {
//				buffer = new byte [mbcsSize + delimiterSize];
//			}
//			buffer [0] = (byte) (mbcsSize & 0xFF);
//			buffer [1] = (byte) (mbcsSize >> 8);
//			mbcsSize = OS.SendMessageA (handle, OS.EM_GETLINE, line, buffer);
//			wcsSize = OS.MultiByteToWideChar (cp, OS.MB_PRECOMPOSED, buffer, mbcsSize, null, 0);
//		}
//		if (line - 1 != count) {
//			for (int i=0; i<delimiterSize; i++) {
//				buffer [mbcsSize++] = (byte) delimiter.charAt (i);
//			}
//			wcsSize += delimiterSize;
//		}
//		if ((mbcsTotal + mbcsSize) >= mbcsPos) {
//			int bufferSize = mbcsPos - mbcsTotal;
//			wcsSize = OS.MultiByteToWideChar (cp, OS.MB_PRECOMPOSED, buffer, bufferSize, null, 0);
//			return wcsTotal + wcsSize;
//		}
//		wcsTotal += wcsSize;
//		mbcsTotal += mbcsSize;
//	}
//	return wcsTotal;
//}

/**
 * Pastes text from clipboard.
 * <p>
 * The selected text is deleted from the widget
 * and new text inserted from the clipboard.
 * </p>
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void paste () {
	checkWidget ();
        if ((style & SWT.MULTI) != 0)
            OS.WinSendMsg (handle, OS.MLM_PASTE, 0, 0);
        else
            OS.WinSendMsg (handle, OS.EM_PASTE, 0, 0);
}

/**
 * Removes the listener from the collection of listeners who will
 * be notified when the receiver's text is modified.
 *
 * @param listener the listener which should no longer be notified
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @see ModifyListener
 * @see #addModifyListener
 */
public void removeModifyListener (ModifyListener listener) {
	checkWidget ();
	if (listener == null) error (SWT.ERROR_NULL_ARGUMENT);
	if (eventTable == null) return;
	eventTable.unhook (SWT.Modify, listener);	
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

/**
 * Removes the listener from the collection of listeners who will
 * be notified when the control is verified.
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
 * @see VerifyListener
 * @see #addVerifyListener
 */
public void removeVerifyListener (VerifyListener listener) {
	checkWidget ();
	if (listener == null) error (SWT.ERROR_NULL_ARGUMENT);
	if (eventTable == null) return;
	eventTable.unhook (SWT.Verify, listener);	
}

/**
 * Selects all the text in the receiver.
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void selectAll () {
	checkWidget ();
	OS.WinSendMsg (handle, OS.EM_SETSEL, OS.MPFROM2SHORT((short)0,textLimit), 0);
}

boolean sendKeyEvent (int type, int msg, int mp1, int mp2, Event event) {
	if (!super.sendKeyEvent (type, msg, mp1, mp2, event)) {
		return false;
	}
	if (ignoreVerify) return true;
	if (type != SWT.KeyDown) return true;
	if (msg != OS.WM_CHAR /*&& msg != OS.WM_KEYDOWN && msg != OS.WM_IME_CHAR*/) {
		return true;
	}
	if (event.character == 0) return true;
	if (!hooks (SWT.Verify)) return true;
	char key = event.character;
	int stateMask = event.stateMask;
	
	/* Disable all magic keys that could modify the text */
	switch (msg) {
		case OS.WM_CHAR:{
			if (key != 0x08 && key != 0x7F && key != '\r' && key != '\t' && key != '\n') 
				break;
			else{
				if ((stateMask & (SWT.CTRL | SWT.SHIFT | SWT.ALT)) != 0) return false;
				break;
			}
		}
			// FALL THROUGH
//		case OS.WM_KEYDOWN:
//			if ((stateMask & (SWT.CTRL | SWT.SHIFT | SWT.ALT)) != 0) return false;
//			break;
	}

	/*
	* If the left button is down, the text widget
	* refuses the character.
	*/
	if (OS.WinGetKeyState (OS.HWND_DESKTOP, OS.VK_BUTTON1) < 0) {
		return true;
	}

	/* Verify the character */
	String oldText = "";
	int  start = 0, end = 0;
	int pos = ((style & SWT.MULTI) != 0)?OS.WinSendMsg(handle, OS.MLM_QUERYSEL, OS.MLFQS_MINMAXSEL, 0):OS.WinSendMsg (handle, OS.EM_QUERYSEL, 0, 0);
	start = OS.SHORT1FROMMP(pos);
	end = OS.SHORT2FROMMP(pos);
	switch (key) {
		case 0x08:	/* Bs */
			if (start == end ) {
				if (start == 0) return true;
				int lineStart = ((style & SWT.MULTI) != 0)?OS.WinSendMsg (handle, OS.MLM_CHARFROMLINE, -1, 0):OS.WinSendMsg(handle, OS.EM_QUERYFIRSTCHAR, 0, 0);
				if (start == lineStart) {
					start = start - DELIMITER.length ();
				} else {
					start = start - 1;
//					if (OS.IsDBLocale) {
//						int [] newStart = new int [1], newEnd = new int [1];
//						OS.SendMessage (handle, OS.EM_SETSEL, start [0], end [0]);
//						OS.SendMessage (handle, OS.EM_GETSEL, newStart, newEnd);
//						if (start [0] != newStart [0]) start [0] = start [0] - 1;
//					}
				}
				start = Math.max (start, 0);
			}
			break;
		case 0x7F:	/* Del */
			if (start == end) {
				int length = OS.WinQueryWindowTextLength (handle);
				if (start == length) return true;
				int line = ((style & SWT.MULTI) != 0)?OS.WinSendMsg(handle, OS.MLM_LINEFROMCHAR, end, 0):1;
				int lineStart = ((style & SWT.MULTI) != 0)?OS.WinSendMsg (handle, OS.MLM_CHARFROMLINE, line + 1, 0):OS.WinSendMsg(handle, OS.EM_QUERYFIRSTCHAR, 0, 0);
				if (end == lineStart - DELIMITER.length ()) {
					end = end + DELIMITER.length ();
				} else {
					end = end + 1;
//					if (OS.IsDBLocale) {
//						int [] newStart = new int [1], newEnd = new int [1];
//						OS.SendMessage (handle, OS.EM_SETSEL, start [0], end [0]);
//						OS.SendMessage (handle, OS.EM_GETSEL, newStart, newEnd);
//						if (end [0] != newEnd [0]) end [0] = end [0] + 1;
//					}
				}
				end = Math.min (end, length);
			}
			break;
		case '\r':	/* Return */
			if ((style & SWT.MULTI) == 0) return true;
			oldText = DELIMITER;
			break;
		default:	/* Tab and other characters */
			if (key != '\t' && key < 0x20) return true;
			oldText = new String (new char [] {key});
			break;
	}
	String newText = verifyText (oldText, start, end, event);
	if (newText == null) return false;
	if (newText == oldText) return true;
	newText = Display.withCrLf (newText);
	PSZ buffer = new PSZ(newText);
	if ((style & SWT.MULTI) != 0){ 
		OS.WinSendMsg (handle, OS.MLM_SETSEL, start, end);
		OS.WinSendMsg (handle, OS.MLM_INSERT, buffer, 0);
	}
	else{
		int length = OS.WinQueryWindowTextLength (handle);
		int newlength = length + buffer.toString().length();
		OS.WinSendMsg (handle, OS.EM_SETSEL, OS.MPFROM2SHORT((short)start, (short)end), 0);
		OS.WinSetWindowText(handle, buffer);
		OS.WinSendMsg(handle, OS.EM_SETFIRSTCHAR, (short)(newlength), 0);
	}
	return false;
}

/**
 * Sets the double click enabled flag.
 * <p>
 * The double click flag enables or disables the
 * default action of the text widget when the user
 * double clicks.
 * </p>
 * 
 * @param doubleClick the new double click flag
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void setDoubleClickEnabled (boolean doubleClick) {
	checkWidget ();
	this.doubleClick = doubleClick;
}

/**
 * Sets the echo character.
 * <p>
 * The echo character is the character that is
 * displayed when the user enters text or the
 * text is changed by the programmer. Setting
 * the echo character to '\0' clears the echo
 * character and redraws the original text.
 * If for any reason the echo character is invalid,
 * the default echo character for the platform
 * is used.
 * </p>
 *
 * @param echo the new echo character
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void setEchoChar (char echo) {
	checkWidget ();
//	if (echo != 0) {
//		if ((echo = (char) wcsToMbcs (echo, getCodePage ())) == 0) echo = '*';
//	}
//	OS.SendMessage (handle, OS.EM_SETPASSWORDCHAR, echo, 0);
//	/*
//	* Bug in Windows.  When the password character is changed,
//	* Windows does not redraw to show the new password character.
//	* The fix is to force a redraw when the character is set.
//	*/
//	OS.InvalidateRect (handle, null, true);
}

/**
 * Sets the editable state.
 *
 * @param editable the new editable state
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void setEditable (boolean editable) {
	checkWidget ();
	style &= ~SWT.READ_ONLY;
	if (!editable) style |= SWT.READ_ONLY;
        if ((style & SWT.MULTI) != 0)             
            OS.WinSendMsg (handle, OS.MLM_SETREADONLY, editable ? 0 : 1, 0);
         else
            OS.WinSendMsg (handle, OS.EM_SETREADONLY, editable ? 0 : 1, 0);             
}

public void setFont (Font font) {
	checkWidget ();
	super.setFont (font);
//	setTabStops (tabs);
}

/**
 * Sets the selection.
 * <p>
 * Indexing is zero based.  The range of
 * a selection is from 0..N where N is
 * the number of characters in the widget.
 * </p><p>
 * Text selections are specified in terms of
 * caret positions.  In a text widget that
 * contains N characters, there are N+1 caret
 * positions, ranging from 0..N.  This differs
 * from other functions that address character
 * position such as getText () that use the
 * regular array indexing rules.
 * </p>
 *
 * @param start new caret position
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void setSelection (int start) {
	checkWidget ();
//	if (OS.IsDBLocale) start = wcsToMbcsPos (start);
        if ((style & SWT.MULTI) != 0){
            OS.WinSendMsg (handle, OS.MLM_SETSEL, start, start);
//            OS.SendMessage (handle, OS.EM_SCROLLCARET, 0, 0);
        }
        else{
            OS.WinSendMsg (handle, OS.EM_SETSEL, OS.MPFROM2SHORT((short)start,(short)start),0);
//            OS.SendMessage (handle, OS.EM_SCROLLCARET, 0, 0);
        }
}

/**
 * Sets the selection.
 * <p>
 * Indexing is zero based.  The range of
 * a selection is from 0..N where N is
 * the number of characters in the widget.
 * </p><p>
 * Text selections are specified in terms of
 * caret positions.  In a text widget that
 * contains N characters, there are N+1 caret
 * positions, ranging from 0..N.  This differs
 * from other functions that address character
 * position such as getText () that use the
 * usual array indexing rules.
 * </p>
 *
 * @param start the start of the range
 * @param end the end of the range
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void setSelection (int start, int end) {
	checkWidget ();
//	if (OS.IsDBLocale) {
//		start = wcsToMbcsPos (start);
//		end = wcsToMbcsPos (end);
//	}
        if ((style & SWT.MULTI) != 0)
            OS.WinSendMsg (handle, OS.MLM_SETSEL, start, end);
        else
            OS.WinSendMsg (handle, OS.EM_SETSEL, OS.MPFROM2SHORT((short)start,(short)end), 0);
}

public void setRedraw (boolean redraw) {
	checkWidget ();
	super.setRedraw (redraw);
//	/*
//	* Feature in Windows.  When WM_SETREDRAW is used to turn
//	* redraw off, the text control is not scrolled to show the
//	* i-beam.  The fix is to detect that the i-beam has moved
//	* while redraw is turned off and force it to be visible
//	* when redraw is restored.
//	*/
//	if (drawCount != 0) return;
//	int [] start = new int [1], end = new int [1];
//	OS.SendMessage (handle, OS.EM_GETSEL, start, end);
//	if (!redraw) {
//		oldStart = start [0];  oldEnd = end [0];
//		return;
//	}
//	if (oldStart == start [0] && oldEnd == end [0]) return;
//	OS.SendMessage (handle, OS.EM_SCROLLCARET, 0, 0);		
}

/**
 * Sets the selection.
 * <p>
 * Indexing is zero based.  The range of
 * a selection is from 0..N where N is
 * the number of characters in the widget.
 * </p><p>
 * Text selections are specified in terms of
 * caret positions.  In a text widget that
 * contains N characters, there are N+1 caret
 * positions, ranging from 0..N.  This differs
 * from other functions that address character
 * position such as getText () that use the
 * usual array indexing rules.
 * </p>
 *
 * @param selection the point
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the point is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void setSelection (Point selection) {
	checkWidget ();
	if (selection == null) error (SWT.ERROR_NULL_ARGUMENT);
	setSelection (selection.x, selection.y);
}

/**
 * Sets the number of tabs.
 * <p>
 * Tab stop spacing is specified in terms of the
 * space (' ') character.  The width of a single
 * tab stop is the pixel width of the spaces.
 * </p>
 *
 * @param tabs the number of tabs
 *
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void setTabs (int tabs) {
	checkWidget ();
	if (tabs < 0) return;
	setTabStops (this.tabs = tabs);
}

void setTabStops (int tabs) {
        int width = getTabWidth (tabs);
	OS.WinSendMsg (handle, OS.MLM_SETTABSTOP, width, 0);
}

/**
 * Sets the contents of the receiver to the given string. If the receiver has style
 * SINGLE and the argument contains multiple lines of text, the result of this
 * operation is undefined and may vary from platform to platform.
 *
 * @param text the new text
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the string is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void setText (String string) {
	checkWidget ();
	if (string == null) error (SWT.ERROR_NULL_ARGUMENT);
	string = Display.withCrLf (string);
	if (hooks (SWT.Verify)) {
		int length = OS.WinQueryWindowTextLength (handle);
		string = verifyText (string, 0, length);
		if (string == null) return;
	}
	PSZ buffer = new PSZ (string);
        if ((style & SWT.MULTI) != 0){
            OS.WinSendMsg(handle, OS.MLM_INSERT, buffer, 0);
        }
        else{
            OS.WinSetWindowText(handle, buffer);
        }
}
//
/**
 * Sets the maximum number of characters that the receiver
 * is capable of holding to be the argument.
 * <p>
 * Instead of trying to set the text limit to zero, consider
 * creating a read-only text widget.
 * </p><p>
 * To reset this value to the default, use <code>setTextLimit(Text.LIMIT)</code>.
 * </p>
 *
 * @param limit new text limit
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_CANNOT_BE_ZERO - if the limit is zero</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void setTextLimit (int limit) {
	checkWidget ();
	if (limit == 0) error (SWT.ERROR_CANNOT_BE_ZERO);
        if ((style & SWT.MULTI) != 0)
            OS.WinSendMsg (handle, OS.MLM_SETTEXTLIMIT, limit, 0);
        else{
            OS.WinSendMsg (handle, OS.EM_SETTEXTLIMIT, limit, 0);
            textLimit = (short)limit;
        }
}

/**
 * Sets the zero-relative index of the line which is currently
 * at the top of the receiver. This index can change when lines
 * are scrolled or new lines are added and removed.
 *
 * @param index the index of the top item
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void setTopIndex (int index) {
	checkWidget ();
	if ((style & SWT.MULTI) == 0) return;
/* 	int bits = OS.WinQueryWindowULong (handle, OS.QWL_STYLE);
	if ((bits & OS.ES_MULTILINE) == 0) return; */
	int count = OS.WinSendMsg (handle, OS.MLM_QUERYLINECOUNT, 0, 0);
	index = Math.min (Math.max (index, 0), count - 1);
	/* In PM this is the actual char position */
	int topIndex = OS.WinSendMsg (handle, OS.MLM_QUERYFIRSTCHAR, 0, 0);
	/* This will cause a scroll to the selected position */
	OS.WinSendMsg (handle, OS.MLM_SETSEL, topIndex, topIndex);
}

/*
* Not currently used.
*/
void setWrap (boolean wrap) {
	if (wrap == getWrap ()) return;
	style &= ~SWT.WRAP;
	if (wrap) style |= SWT.WRAP;
}

/**
 * Shows the selection.
 * <p>
 * If the selection is already showing
 * in the receiver, this method simply returns.  Otherwise,
 * lines are scrolled until the selection is visible.
 * </p>
 * 
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void showSelection () {
	checkWidget ();
//	OS.SendMessage (handle, OS.EM_SCROLLCARET, 0, 0);
}

String verifyText (String string, int start, int end) {
	return verifyText (string, start, end, null);
}

String verifyText (String string, int start, int end, Event keyEvent) {
	if (ignoreVerify) return string;
	Event event = new Event ();
	event.text = string;
	event.start = start;
	event.end = end;
	if (keyEvent != null) {
		event.character = keyEvent.character;
		event.keyCode = keyEvent.keyCode;
		event.stateMask = keyEvent.stateMask;
	}
//	if (OS.IsDBLocale) {
//		event.start = mbcsToWcsPos (start);
//		event.end = mbcsToWcsPos (end);
//	}
	/*
	* It is possible (but unlikely), that application
	* code could have disposed the widget in the verify
	* event.  If this happens, answer null to cancel
	* the operation.
	*/
	sendEvent (SWT.Verify, event);
	if (!event.doit || isDisposed ()) return null;
	return event.text;
}

//int wcsToMbcsPos (int wcsPos) {
//	if (wcsPos == 0) return 0;
//	if (OS.IsUnicode) return wcsPos;
//	int cp = getCodePage ();
//	int wcsTotal = 0, mbcsTotal = 0;
//	byte [] buffer = new byte [128];
//	String delimiter = getLineDelimiter ();
//	int delimiterSize = delimiter.length ();
//	int count = OS.SendMessageA (handle, OS.EM_GETLINECOUNT, 0, 0);
//	for (int line=0; line<count; line++) {
//		int wcsSize = 0;
//		int linePos = OS.SendMessageA (handle, OS.EM_LINEINDEX, line, 0);
//		int mbcsSize = OS.SendMessageA (handle, OS.EM_LINELENGTH, linePos, 0);
//		if (mbcsSize != 0) {
//			if (mbcsSize + delimiterSize > buffer.length) {
//				buffer = new byte [mbcsSize + delimiterSize];
//			}
//			//ENDIAN
//			buffer [0] = (byte) (mbcsSize & 0xFF);
//			buffer [1] = (byte) (mbcsSize >> 8);
//			mbcsSize = OS.SendMessageA (handle, OS.EM_GETLINE, line, buffer);
//			wcsSize = OS.MultiByteToWideChar (cp, OS.MB_PRECOMPOSED, buffer, mbcsSize, null, 0);
//		}
//		if (line - 1 != count) {
//			for (int i=0; i<delimiterSize; i++) {
//				buffer [mbcsSize++] = (byte) delimiter.charAt (i);
//			}
//			wcsSize += delimiterSize;
//		}
//		if ((wcsTotal + wcsSize) >= wcsPos) {
//			wcsSize = 0;
//			int index = 0;
//			while (index < mbcsSize) {
//				if ((wcsTotal + wcsSize) == wcsPos) {
//					return mbcsTotal + index;
//				}
//				if (OS.IsDBCSLeadByte (buffer [index++])) index++;
//				wcsSize++;
//			}
//			return mbcsTotal + mbcsSize;
//		}
//		wcsTotal += wcsSize;
//		mbcsTotal += mbcsSize;
//	}
//	return mbcsTotal;
//}

int widgetStyle () {
	int bits = super.widgetStyle ();
        if ((style & SWT.MULTI) != 0){
            if ((style & SWT.READ_ONLY) != 0) bits |= OS.MLS_READONLY;	
            if ((style & SWT.WRAP) != 0) bits |= OS.MLS_WORDWRAP;
            if ((style & SWT.V_SCROLL) != 0) bits |= OS.MLS_VSCROLL;
            if ((style & SWT.H_SCROLL) != 0) bits |= OS.MLS_HSCROLL;
	    if ((style & SWT.BORDER) != 0) bits |= OS.MLS_BORDER;
            return bits | OS.WS_VISIBLE;
        }
        else{
            bits &= ~ (OS.ES_AUTOSIZE | OS.ES_MARGIN);
            if ((style & SWT.READ_ONLY) != 0) bits |= OS.ES_READONLY;
            if ((style & SWT.BORDER) != 0) bits |= OS.ES_MARGIN;
            return bits | OS.WS_VISIBLE | OS.ES_AUTOSCROLL;
        }
}

PSZ windowClass () {
        if ((style & SWT.MULTI) != 0)
            return EditClass;
        else
            return FieldClass;
}

int windowProc () {
        if ((style & SWT.MULTI) != 0)
            return EditProc;
        else 
            return FieldProc;
}

//int windowProc (int msg, int mp1, int mp2) {
//    int result = super.windowProc (msg, mp1, mp2);
//    switch(msg){
//        case OS.MLM_CUT:{
//            OS.WinSendMsg(handle, OS.MLM_CUT,0,0);
//            break;
//        }
//    }
//    return result;
//}

MRESULT WM_CHAR (int mp1, int mp2) {
	if (ignoreCharacter) return null;
	MRESULT result = super.WM_CHAR (mp1, mp2);
	if (result != null) return result;
	return result;
}
//
//LRESULT WM_CLEAR (int wParam, int lParam) {
//	LRESULT result = super.WM_CLEAR (wParam, lParam);
//	if (result != null) return result;
//	if (!hooks (SWT.Verify)) return result;
//	int bits = OS.GetWindowLong (handle, OS.GWL_STYLE);
//	if ((bits & OS.ES_READONLY) != 0) return result;
//	int [] start = new int [1], end = new int [1];
//	OS.SendMessage (handle, OS.EM_GETSEL, start, end);
//	if (start [0] == end [0]) return result;
//	String newText = verifyText ("", start [0], end [0]);
//	if (newText == null) return LRESULT.ZERO;
//	if (newText.length () != 0) {
//		result = new LRESULT (callWindowProc (OS.WM_CLEAR, 0, 0));	
//		newText = Display.withCrLf (newText);
//		TCHAR buffer = new TCHAR (getCodePage (), newText, true);
//		OS.SendMessage (handle, OS.EM_REPLACESEL, 0, buffer);
//	}
//	return result;
//}
//
//LRESULT WM_CUT (int wParam, int lParam) {
//	LRESULT result = super.WM_CUT (wParam, lParam);
//	if (result != null) return result;
//	if (!hooks (SWT.Verify)) return result;
//	int bits = OS.GetWindowLong (handle, OS.GWL_STYLE);
//	if ((bits & OS.ES_READONLY) != 0) return result;
//	int [] start = new int [1], end = new int [1];
//	OS.SendMessage (handle, OS.EM_GETSEL, start, end);
//	if (start [0] == end [0]) return result;
//	String newText = verifyText ("", start [0], end [0]);
//	if (newText == null) return LRESULT.ZERO;
//	if (newText.length () != 0) {
//		result = new LRESULT (callWindowProc (OS.WM_CUT, 0, 0));	
//		newText = Display.withCrLf (newText);
//		TCHAR buffer = new TCHAR (getCodePage (), newText, true);
//		OS.SendMessage (handle, OS.EM_REPLACESEL, 0, buffer);
//	}
//	return result;
//}
//
//LRESULT WM_GETDLGCODE (int wParam, int lParam) {
//	LRESULT result = super.WM_GETDLGCODE (wParam, lParam);
//	if (result != null) return result;
//	/*
//	* Feature in Windows.  Despite the fact that the
//	* text control is read only, it still returns a
//	* dialog code indicating that it wants keys.  The
//	* fix is to detect this case and clear the bits.
//	*/
//	if ((style & SWT.READ_ONLY) != 0) {
//		int code = callWindowProc (OS.WM_GETDLGCODE, wParam, lParam);
//		code &= ~(OS.DLGC_WANTALLKEYS | OS.DLGC_WANTTAB | OS.DLGC_WANTARROWS);
//		return new LRESULT (code);
//	}
//	return null;
//}
//
//LRESULT WM_IME_CHAR (int wParam, int lParam) {
//
//	/* Process a DBCS character */
//	Display display = getDisplay ();
//	display.lastKey = 0;
//	display.lastAscii = wParam;
//	display.lastVirtual = false;
//	if (!sendKeyEvent (SWT.KeyDown, OS.WM_IME_CHAR, wParam, lParam)) {
//		return LRESULT.ZERO;
//	}
//	sendKeyEvent (SWT.KeyUp, OS.WM_IME_CHAR, wParam, lParam);
//	display.lastKey = display.lastAscii = 0;
//
//	/*
//	* Feature in Windows.  The Windows text widget uses
//	* two 2 WM_CHAR's to process a DBCS key instead of
//	* using WM_IME_CHAR.  The fix is to allow the text
//	* widget to get the WM_CHAR's but ignore sending
//	* them to the application.
//	*/
//	ignoreCharacter = true;
//	int result = callWindowProc (OS.WM_IME_CHAR, wParam, lParam);
//	MSG msg = new MSG ();
//	while (OS.PeekMessage (msg, handle, OS.WM_CHAR, OS.WM_CHAR, OS.PM_REMOVE)) {
//		OS.TranslateMessage (msg);
//		OS.DispatchMessage (msg);
//	}
//	ignoreCharacter = false;
//	return new LRESULT (result);
//}
//
//LRESULT WM_LBUTTONDBLCLK (int wParam, int lParam) {
//	/*
//	* Prevent Windows from processing WM_LBUTTONDBLCLK
//	* when double clicking behavior is disabled by not
//	* calling the window proc.
//	*/
//	sendMouseEvent (SWT.MouseDown, 1, OS.WM_LBUTTONDOWN, wParam, lParam);
//	sendMouseEvent (SWT.MouseDoubleClick, 1, OS.WM_LBUTTONDBLCLK, wParam, lParam);
//	if (OS.GetCapture () != handle) OS.SetCapture (handle);
//	if (!doubleClick) return LRESULT.ZERO;
//		
//	/*
//	* Bug in Windows.  When the last line of text in the
//	* widget is double clicked and the line is empty, Windows
//	* hides the i-beam then moves it to the first line in
//	* the widget but does not scroll to show the user.
//	* If the user types without clicking the mouse, invalid
//	* characters are displayed at the end of each line of
//	* text in the widget.  The fix is to detect this case
//	* and avoid calling the window proc.
//	*/
//	int [] start = new int [1], end = new int [1];
//	OS.SendMessage (handle, OS.EM_GETSEL, start, end);
//	if (start [0] == end [0]) {
//		int length = OS.GetWindowTextLength (handle);
//		if (length == start [0]) {
//			int result = OS.SendMessage (handle, OS.EM_LINELENGTH, length, 0);
//			if (result == 0) return LRESULT.ZERO;
//		}
//	}
//	return null;
//}
//
//LRESULT WM_PASTE (int wParam, int lParam) {
//	LRESULT result = super.WM_PASTE (wParam, lParam);
//	if (result != null) return result;
//	if (!hooks (SWT.Verify)) return result;
//	int bits = OS.GetWindowLong (handle, OS.GWL_STYLE);
//	if ((bits & OS.ES_READONLY) != 0) return result;
//	String oldText = getClipboardText ();
//	if (oldText == null) return result;
//	int [] start = new int [1], end = new int [1];
//	OS.SendMessage (handle, OS.EM_GETSEL, start, end);
//	String newText = verifyText (oldText, start [0], end [0]);
//	if (newText == null) return LRESULT.ZERO;
//	if (newText != oldText) {
//		newText = Display.withCrLf (newText);
//		TCHAR buffer = new TCHAR (getCodePage (), newText, true);
//		OS.SendMessage (handle, OS.EM_REPLACESEL, 0, buffer);
//		return LRESULT.ZERO;
//	}
//	return result;
//}
//
//LRESULT WM_SIZE (int wParam, int lParam) {
//	LRESULT result = super.WM_SIZE (wParam, lParam);
//	// widget may be disposed at this point
//	if (handle == 0) return result;
//	
//	/*
//	* Feature in Windows.  When the caret is moved,
//	* the text widget scrolls to show the new location.
//	* This means that the text widget may be scrolled
//	* to the left in order to show the caret when the
//	* widget is not large enough to show both the caret
//	* location and all the text.  Unfortunately, when
//	* the text widget is resized such that all the text
//	* and the caret could be visible, Windows does not
//	* scroll the widget back.  The fix is to save the
//	* current selection, set the selection to the start
//	* of the text and then restore the selection.  This
//	* will cause the text widget recompute the left
//	* scroll position.
//	*
//	* NOTE: Currently, this work around is only applied
//	* to single line text widgets that are not visible.
//	* If the widget is resized when it is visible, this
//	* is fine because the user has already seen that the
//	* text is scrolled.
//	*/
//	int bits = OS.GetWindowLong (handle, OS.GWL_STYLE); 
//	if ((bits & OS.ES_MULTILINE) != 0) return result;
//	if (OS.IsWindowVisible (handle)) return result;
//	int [] start = new int [1], end = new int [1];
//	OS.SendMessage (handle, OS.EM_GETSEL, start, end);
//	if (start [0] == 0 && end [0] == 0) return result;
//	OS.SendMessage (handle, OS.EM_SETSEL, 0, 0);
//	OS.SendMessage (handle, OS.EM_SETSEL, start [0], end [0]);
//	return result;
//}
//
//LRESULT WM_UNDO (int wParam, int lParam) {
//	LRESULT result = super.WM_UNDO (wParam, lParam);
//	if (result != null) return result;
//	if (!hooks (SWT.Verify)) return result;
//
//	/* Undo and then Redo to get the Undo text */
//	if (OS.SendMessage (handle, OS.EM_CANUNDO, 0, 0) == 0) {
//		return result;
//	}
//	ignoreVerify = true;
//	callWindowProc (OS.WM_UNDO, wParam, lParam);
//	String oldText = getSelectionText ();
//	callWindowProc (OS.WM_UNDO, wParam, lParam);
//	ignoreVerify = false;
//
//	/* Verify the Undo operation */
//	int [] start = new int [1], end = new int [1];
//	OS.SendMessage (handle, OS.EM_GETSEL, start, end);
//	String newText = verifyText (oldText, start [0], end [0]);
//	if (newText == null) return LRESULT.ZERO;
//	if (newText != oldText) {
//		newText = Display.withCrLf (newText);
//		TCHAR buffer = new TCHAR (getCodePage (), newText, true);
//		OS.SendMessage (handle, OS.EM_REPLACESEL, 0, buffer);
//		return LRESULT.ZERO;
//	}
//	
//	/* Do the original Undo */
//	ignoreVerify = true;
//	callWindowProc (OS.WM_UNDO, wParam, lParam);
//	ignoreVerify = false;
//	return LRESULT.ONE;
//}

MRESULT wmControlChild (int mp1, int mp2) {
    int code = mp1 >> 16;
    switch (code) {
        case OS.MLN_CHANGE:
            sendEvent (SWT.Modify);
            if (isDisposed ()) return MRESULT.ZERO;
            break;
        case OS.EN_CHANGE: 
                /*
                * It is possible (but unlikely), that application
                * code could have disposed the widget in the modify
                * event.  If this happens, end the processing of the
                * Windows message by returning zero as the result of
                * the window proc.
                */
                sendEvent (SWT.Modify);
                if (isDisposed ()) return MRESULT.ZERO;
                break;
    }
    return super.wmControlChild (mp1, mp2);
}

//MRESULT wmCommandChild (int mp1, int mp2) {
//	int code = mp1 >> 16;
//	switch (code) {
//		case OS.EN_CHANGE: 
//			/*
//			* It is possible (but unlikely), that application
//			* code could have disposed the widget in the modify
//			* event.  If this happens, end the processing of the
//			* Windows message by returning zero as the result of
//			* the window proc.
//			*/
//			sendEvent (SWT.Modify);
//			if (isDisposed ()) return MRESULT.ZERO;
//			break;
//	}
//	return super.wmCommandChild (mp1, mp2);
//}

MRESULT wmScroll (int msg, int mp1, int mp2) {
	int code = callWindowProc (msg, mp1, mp2);
	if (code == 0) return MRESULT.ZERO;
	return new MRESULT (code);
}

}
