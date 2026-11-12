package org.eclipse.swt.widgets;

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
 * Instances of this class are controls that allow the user
 * to choose an item from a list of items, or optionally 
 * enter a new value by typing it into an editable text
 * field. Often, <code>Combo</code>s are used in the same place
 * where a single selection <code>List</code> widget could
 * be used but space is limited. A <code>Combo</code> takes
 * less space than a <code>List</code> widget and shows
 * similar information.
 * <p>
 * Note: Since <code>Combo</code>s can contain both a list
 * and an editable text field, it is possible to confuse methods
 * which access one versus the other (compare for example,
 * <code>clearSelection()</code> and <code>deselectAll()</code>).
 * The API documentation is careful to indicate either "the
 * receiver's list" or the "the receiver's text field" to 
 * distinguish between the two cases.
 * </p><p>
 * Note that although this class is a subclass of <code>Composite</code>,
 * it does not make sense to add children to it, or set a layout on it.
 * </p>
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>DROP_DOWN, READ_ONLY, SIMPLE</dd>
 * <dt><b>Events:</b></dt>
 * <dd>DefaultSelection, Modify, Selection</dd>
 * </dl>
 * <p>
 * Note: Only one of the styles DROP_DOWN and SIMPLE 
 * may be specified.
 * </p><p>
 * IMPORTANT: This class is <em>not</em> intended to be subclassed.
 * </p>
 *
 * @see List
 */

public class Combo extends Composite {
//	boolean noSelection;
	
	/**
	 * the operating system limit for the number of characters
	 * that the text field in an instance of this class can hold
	 */
//	public static final int LIMIT;
        short textLimit = 32;
        static final int ComboProc;
        static final PSZ ComboClass;
	/*
	 * These are the undocumented control id's for the children of
	 * a combo box.  Since there are no constants for these values,
	 * they may change with different versions of Windows (but have
	 * been the same since Windows 3.0).
	 */
        static 
        {
            ComboClass = PSZ.getAtom(OS.WC_COMBOBOX);
            CLASSINFO pclsiClassInfo = new CLASSINFO();
            OS.WinQueryClassInfo(0, ComboClass, pclsiClassInfo);
            ComboProc = pclsiClassInfo.pfnWindowProc;
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
 * @see SWT#DROP_DOWN
 * @see SWT#READ_ONLY
 * @see SWT#SIMPLE
 * @see Widget#checkSubclass
 * @see Widget#getStyle
 */
public Combo (Composite parent, int style) {
	super (parent, checkStyle (style));
}

/**
 * Adds the argument to the end of the receiver's list.
 *
 * @param string the new item
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the string is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 * @exception SWTError <ul>
 *    <li>ERROR_ITEM_NOT_ADDED - if the operation fails because of an operating system failure</li>
 * </ul>
 *
 * @see #add(String,int)
 */
public void add (String string) {
	checkWidget ();
	if (string == null) error (SWT.ERROR_NULL_ARGUMENT);
	PSZ buffer = new PSZ(string);
	int result = OS.WinSendMsg (handle, OS.LM_INSERTITEM,  OS.LIT_SORTASCENDING, buffer);
	if (result == OS.LIT_MEMERROR) error (SWT.ERROR_ITEM_NOT_ADDED);
	if (result == OS.LIT_ERROR) error (SWT.ERROR_ITEM_NOT_ADDED);
}

/**
 * Adds the argument to the receiver's list at the given
 * zero-relative index.
 * <p>
 * Note: To add an item at the end of the list, use the
 * result of calling <code>getItemCount()</code> as the
 * index or use <code>add(String)</code>.
 * </p>
 *
 * @param string the new item
 * @param index the index for the item
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the string is null</li>
 *    <li>ERROR_INVALID_RANGE - if the index is not between 0 and the number of elements in the list (inclusive)</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 * @exception SWTError <ul>
 *    <li>ERROR_ITEM_NOT_ADDED - if the operation fails because of an operating system failure</li>
 * </ul>
 *
 * @see #add(String)
 */
public void add (String string, int index) {
	checkWidget ();
	if (string == null) error (SWT.ERROR_NULL_ARGUMENT);
	if (index == -1) error (SWT.ERROR_INVALID_RANGE);
	PSZ buffer = new PSZ(string);
	int result = OS.WinSendMsg (handle, OS.LM_INSERTITEM,  index, buffer);
	if (result == OS.LIT_MEMERROR) error (SWT.ERROR_ITEM_NOT_ADDED);
	if (result == OS.LIT_ERROR) {
		int count = OS.WinSendMsg (handle, OS.LM_QUERYITEMCOUNT, 0, 0);
		if (0 <= index && index <= count) {
			error (SWT.ERROR_ITEM_NOT_ADDED);
		} else {
			error (SWT.ERROR_INVALID_RANGE);
		}
	}
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
 * be notified when the receiver's selection changes, by sending
 * it one of the messages defined in the <code>SelectionListener</code>
 * interface.
 * <p>
 * <code>widgetSelected</code> is called when the combo's list selection changes.
 * <code>widgetDefaultSelected</code> is typically called when ENTER is pressed the combo's text area.
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
public void addSelectionListener(SelectionListener listener) {
	checkWidget ();
	if (listener == null) error (SWT.ERROR_NULL_ARGUMENT);
	TypedListener typedListener = new TypedListener (listener);
	addListener (SWT.Selection,typedListener);
	addListener (SWT.DefaultSelection,typedListener);
}

int callWindowProc (int msg, int mp1, int mp2) {
        if(super.handle == 0)
        {
            return 0;
        } else
        {
            return OS.WinCallWindowProc(ComboProc, super.handle, msg, mp1, mp2);
        }
}

//protected void checkSubclass () {
//	if (!isValidSubclass ()) error (SWT.ERROR_INVALID_SUBCLASS);
//}

static int checkStyle (int style) {
	/*
	 * Feature in Windows.  It is not possible to create
	 * a combo box that has a border using Windows style
	 * bits.  All combo boxes draw their own border and
	 * do not use the standard Windows border styles.
	 * Therefore, no matter what style bits are specified,
	 * clear the BORDER bits so that the SWT style will
	 * match the Windows widget.
	 *
	 * The Windows behavior is currently implemented on
	 * all platforms.
	 */
	style &= ~SWT.BORDER;
	
	/*
	 * Even though it is legal to create this widget
	 * with scroll bars, they serve no useful purpose
	 * because they do not automatically scroll the
	 * widget's client area.  The fix is to clear
	 * the SWT style.
	 */
	style &= ~(SWT.H_SCROLL | SWT.V_SCROLL);
	style = checkBits (style, SWT.DROP_DOWN, SWT.SIMPLE, 0, 0, 0, 0);
	if ((style & SWT.SIMPLE) != 0) return style & ~SWT.READ_ONLY;
	return style;
}

/**
 * Sets the selection in the receiver's text field to an empty
 * selection starting just before the first character. If the
 * text field is editable, this has the effect of placing the
 * i-beam at the start of the text.
 * <p>
 * Note: To clear the selected items in the receiver's list, 
 * use <code>deselectAll()</code>.
 * </p>
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @see #deselectAll
 */
public void clearSelection () {
	checkWidget ();
	OS.WinSendMsg(handle, OS.EM_SETSEL, 0, 0);
}

public Point computeSize (int wHint, int hHint, boolean changed) {
	checkWidget ();
	int count = OS.WinSendMsg (handle, OS.LM_QUERYITEMCOUNT, 0, 0);
	int itemHeight = getItemHeight();
	int width = 0, height = 0;
	if ((style & SWT.SIMPLE) != 0) height = count * itemHeight;
	int hps = this.hps;
        if (hps == 0) hps = OS.WinGetPS (handle);
	PSZ buffer = new PSZ(64+1);
	int[] pnts = new int [OS.TXTBOX_COUNT * 2];
	for (int i=0; i<count; i++) {
		int length = OS.WinSendMsg (handle, OS.LM_QUERYITEMTEXTLENGTH,(short)i, 0);
		if (length != OS.LIT_ERROR) {
			if (length + 1 > buffer.length()) {
				buffer = new PSZ( length + 1);
			}
			int result = OS.WinSendMsg (handle, OS.LM_QUERYITEMTEXT, OS.MPFROM2SHORT((short)i,(short)length), buffer);
			if (result != 0) {
				OS.GpiQueryTextBox (hps, length, buffer.getBytes(), OS.TXTBOX_COUNT, pnts);
				width = Math.max (width, pnts[4] - pnts[0]);

			}
		}
	}	
	if (this.hps == 0) OS.WinReleasePS (hps);
	if (width == 0) width = DEFAULT_WIDTH;
	if (height == 0) height = DEFAULT_HEIGHT;
	if (wHint != SWT.DEFAULT) width = wHint;
	if (hHint != SWT.DEFAULT) height = hHint;
	int textHeight = getItemHeight();
	width += OS.WinQuerySysValue (OS.HWND_DESKTOP, OS.SV_CXVSCROLL) + 8;
//	width += OS.GetSystemMetrics (OS.SM_CXVSCROLL) + 8;
	if ((style & SWT.DROP_DOWN) != 0) {
		height = textHeight + 6;
	} else {
		height += textHeight + 10;
	}
	return new Point (width, height);
}

void createHandle () {
	super.createHandle ();
	state &= ~CANVAS;
}

int defaultBackground () {
	return OS.WinQuerySysColor(super.handle, OS.SYSCLR_ENTRYFIELD, 0);
}

/**
 * Deselects the item at the given zero-relative index in the receiver's 
 * list.  If the item at the index was already deselected, it remains
 * deselected. Indices that are out of range are ignored.
 *
 * @param index the index of the item to deselect
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void deselect (int index) {
	checkWidget ();
	if (index == -1) return;
	int oldIndex =OS.WinSendMsg (handle, OS.LM_QUERYSELECTION, 0, 0);
	if (oldIndex == OS.LIT_NONE) return;
	if (oldIndex == index) OS.WinSendMsg (handle, OS.LM_SELECTITEM, index, OS.FALSE);
	sendEvent (SWT.Modify);
	// widget could be disposed at this point
}

/**
 * Deselects all selected items in the receiver's list.
 * <p>
 * Note: To clear the selection in the receiver's text field,
 * use <code>clearSelection()</code>.
 * </p>
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @see #clearSelection
 */
public void deselectAll () {
	checkWidget ();
        int count = OS.WinSendMsg (handle, OS.LM_QUERYITEMCOUNT, 0, 0);
        int index = 0;
        for(int i=0;i<count;i++){
            index = OS.WinSendMsg (handle, OS.LM_QUERYSELECTION, index, 0);
            OS.WinSendMsg (handle, OS.LM_SELECTITEM, index, OS.FALSE);
        }
	sendEvent (SWT.Modify);
	// widget could be disposed at this point
}

boolean getEditable () {
	int bits = OS.WinQueryWindowULong (handle, OS.QWL_STYLE);
	if ((bits & OS.CBS_DROPDOWNLIST ) != 0)
		return false;
	return true;
}

/**
 * Returns the item at the given, zero-relative index in the
 * receiver's list. Throws an exception if the index is out
 * of range.
 *
 * @param index the index of the item to return
 * @return the item at the given index
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_INVALID_RANGE - if the index is not between 0 and the number of elements in the list minus 1 (inclusive)</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 * @exception SWTError <ul>
 *    <li>ERROR_CANNOT_GET_ITEM - if the operation fails because of an operating system failure</li>
 * </ul>
 */
public String getItem (int index) {
	checkWidget ();
	int length = OS.WinSendMsg (handle, OS.LM_QUERYITEMTEXTLENGTH, (short)index, 0);
	if (length != OS.LIT_ERROR) {
		PSZ buffer = new PSZ(length+1);
		int result = OS.WinSendMsg (handle, OS.LM_QUERYITEMTEXT, OS.MPFROM2SHORT((short)index,(short)length), buffer);
		if (result != 0) return buffer.toString ();
	}
	int count = OS.WinSendMsg (handle, OS.LM_QUERYITEMCOUNT, 0, 0);
	if (0 <= index && index < count) {
		error (SWT.ERROR_CANNOT_GET_ITEM);
	} else {
		error (SWT.ERROR_INVALID_RANGE);
	}
	return null;
}

/**
 * Returns the number of items contained in the receiver's list.
 *
 * @return the number of items
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 * @exception SWTError <ul>
 *    <li>ERROR_CANNOT_GET_COUNT - if the operation fails because of an operating system failure</li>
 * </ul>
 */
public int getItemCount () {
	checkWidget ();
	int result = OS.WinSendMsg (handle, OS.LM_QUERYITEMCOUNT, 0, 0);
	if (result == OS.LIT_ERROR) error (SWT.ERROR_CANNOT_GET_COUNT);
	return result;
}

/**
 * Returns the height of the area which would be used to
 * display <em>one</em> of the items in the receiver's list.
 *
 * @return the height of one item
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 * @exception SWTError <ul>
 *    <li>ERROR_CANNOT_GET_ITEM_HEIGHT - if the operation fails because of an operating system failure</li>
 * </ul>
 */
public int getItemHeight () {
	checkWidget ();
        int hps = this.hps;
        if (hps == 0) hps = OS.WinGetPS (handle);
        FONTMETRICS fm = new FONTMETRICS();
        OS.GpiQueryFontMetrics (hps, FONTMETRICS.sizeof, fm);
        int result = fm.lMaxBaselineExt;
        if (this.hps == 0) OS.WinReleasePS (hps);
        //There is no equivalent message on OS/2
        //Since for now List only hold text we can expect this code to work
        //If it needs to paint then WM_MEASUREITEM message needs to be handle
//	int result = OS.SendMessage (handle, OS.LB_GETITEMHEIGHT, 0, 0);
//	if (result == OS.LB_ERR) error (SWT.ERROR_CANNOT_GET_ITEM_HEIGHT);
	return result;
}

/**
 * Returns an array of <code>String</code>s which are the items
 * in the receiver's list. 
 * <p>
 * Note: This is not the actual structure used by the receiver
 * to maintain its list of items, so modifying the array will
 * not affect the receiver. 
 * </p>
 *
 * @return the items in the receiver's list
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 * @exception SWTError <ul>
 *    <li>ERROR_CANNOT_GET_ITEM - if the operation fails because of an operating system failure</li>
 * </ul>
 */
public String [] getItems () {
	checkWidget ();
	int count = getItemCount ();
	String [] result = new String [count];
	for (int i=0; i<count; i++) result [i] = getItem (i);
	return result;
}

String getNameText () {
	return getText ();
}

/**
 * Returns a <code>Point</code> whose x coordinate is the start
 * of the selection in the receiver's text field, and whose y
 * coordinate is the end of the selection. The returned values
 * are zero-relative. An "empty" selection as indicated by
 * the the x and y coordinates having the same value.
 *
 * @return a point representing the selection start and end
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public Point getSelection () {
        int pos = 0;
	pos = OS.WinSendMsg (handle, OS.EM_QUERYSEL, 0, 0);
        //@@TODO(lpino): Do we need to consider DB?
//	if (OS.IsDBLocale) {
//		start [0] = mbcsToWcsPos (start [0]);
//		end [0] = mbcsToWcsPos (end [0]);
//	}
	return new Point (OS.SHORT1FROMMP(pos), OS.SHORT2FROMMP(pos));
}

/**
 * Returns the zero-relative index of the item which is currently
 * selected in the receiver's list, or -1 if no item is selected.
 *
 * @return the index of the selected item
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public int getSelectionIndex () {
	checkWidget ();
        int index = OS.WinSendMsg (handle, OS.LM_QUERYSELECTION, OS.LIT_FIRST, 0);
	if (index == OS.LIT_NONE) return -1;
	return index;
}

/**
 * Returns a string containing a copy of the contents of the
 * receiver's text field.
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
	int index = OS.WinSendMsg (handle, OS.LM_QUERYSELECTION, (short)OS.LIT_CURSOR, 0);
	int length = OS.WinSendMsg (handle, OS.LM_QUERYITEMTEXTLENGTH, (short)index, 0);
	if (length == 0) return "";
        PSZ buffer = new PSZ(length);
	OS.WinSendMsg (handle, OS.LM_QUERYITEMTEXT, OS.MPFROM2SHORT((short)index,(short)length), buffer);
	return buffer.toString ();
}

String getText (int start, int stop) {
	/*
	 * NOTE: The current implementation uses substring ()
	 * which can reference a potentially large character
	 * array.
	 */
	return getText ().substring (start, stop - 1);
}

/**
 * Returns the height of the receivers's text field.
 *
 * @return the text height
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 * @exception SWTError <ul>
 *    <li>ERROR_CANNOT_GET_ITEM_HEIGHT - if the operation fails because of an operating system failure</li>
 * </ul>
 */
public int getTextHeight () {
	checkWidget ();
        int hps = this.hps;
        if (hps == 0) hps = OS.WinGetPS (handle);
        FONTMETRICS fm = new FONTMETRICS();
        OS.GpiQueryFontMetrics (hps, FONTMETRICS.sizeof, fm);
        int result = fm.lMaxBaselineExt;
        if (this.hps == 0) OS.WinReleasePS (hps);
//	checkWidget ();
//	int result = OS.SendMessage (handle, OS.CB_GETITEMHEIGHT, -1, 0);
//	if (result == OS.CB_ERR) error (SWT.ERROR_CANNOT_GET_ITEM_HEIGHT);
	return result + 6;
}

/**
 * Returns the maximum number of characters that the receiver's
 * text field is capable of holding. If this has not been changed
 * by <code>setTextLimit()</code>, it will be the constant
 * <code>Combo.LIMIT</code>.
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
        return textLimit;
}

boolean hasFocus () {
	int hwndFocus = OS.WinQueryFocus (OS.HWND_DESKTOP);
	if (hwndFocus == handle) return true;
	if (hwndFocus == 0) return false;
	int hwndText = OS.WinWindowFromID(handle, OS.CBID_EDIT);
	if (hwndFocus == hwndText) return true;
	int hwndList = OS.WinWindowFromID(handle, OS.CBID_LIST);
	if (hwndFocus == hwndList) return true;
	return false;
}

/**
 * Searches the receiver's list starting at the first item
 * (index 0) until an item is found that is equal to the 
 * argument, and returns the index of that item. If no item
 * is found, returns -1.
 *
 * @param string the search item
 * @return the index of the item
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the string is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public int indexOf (String string) {
	return indexOf (string, 0);
}

/**
 * Searches the receiver's list starting at the given, 
 * zero-relative index until an item is found that is equal
 * to the argument, and returns the index of that item. If
 * no item is found or the starting index is out of range,
 * returns -1.
 *
 * @param string the search item
 * @return the index of the item
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the string is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public int indexOf (String string, int start) {
	checkWidget ();
	if (string == null) error (SWT.ERROR_NULL_ARGUMENT);
	
	/*
	* Bug in Windows.  For some reason, LB_FINDSTRINGEXACT
	* will not find empty strings even though it is legal
	* to insert an empty string into a list.  The fix is
	* to search the list, an item at a time.
        * The same applies to OS/2 and LM_SEARCHSTRING
	*/
	if (string.length () == 0) {
		int count = getItemCount ();
		for (int i=start; i<count; i++) {
			if (string.equals (getItem (i))) return i;
		}
		return -1;
	}

	/* Use LM_SEARCHSTRING to search for the item */
	int count = OS.WinSendMsg (handle, OS.LM_QUERYITEMCOUNT, 0, 0);
	if (!((0 <= start) && (start < count))) return -1;
	int index = start - 1;
        PSZ buffer = new PSZ(string);
        index = OS.WinSendMsg (handle, OS.LM_SEARCHSTRING, OS.MPFROM2SHORT((short)OS.LSS_CASESENSITIVE,(short)(index)), buffer);
        if (index == OS.LIT_ERROR || index < start) return -1;
        return index;
}

/**
 * Removes the item from the receiver's list at the given
 * zero-relative index.
 *
 * @param index the index for the item
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_INVALID_RANGE - if the index is not between 0 and the number of elements in the list minus 1 (inclusive)</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 * @exception SWTError <ul>
 *    <li>ERROR_ITEM_NOT_REMOVED - if the operation fails because of an operating system failure</li>
 * </ul>
 */
public void remove (int index) {
	checkWidget ();

	int length = OS.WinSendMsg (handle, OS.LM_QUERYITEMTEXTLENGTH,(short)index, 0);
	if (length == OS.LIT_ERROR) error (SWT.ERROR_ITEM_NOT_REMOVED);
	
	int count = OS.WinSendMsg (handle, OS.LM_QUERYITEMCOUNT, 0, 0);
	if (index <0 || index > count) error (SWT.ERROR_INVALID_RANGE);
	
	int leftCount = OS.WinSendMsg (handle, OS.LM_DELETEITEM,(short) index, 0);

	if (leftCount == count) error (SWT.ERROR_ITEM_NOT_REMOVED);
	
	/*
	* It is possible (but unlikely), that application
	* code could have disposed the widget in the modify
	* event.  If this happens, just return.
	*/
        sendEvent (SWT.Modify);
	if (isDisposed ()) return;
	/*
	* Bug in Windows.  When the combo box is read only
	* with exactly one item that is currently selected
	* and that item is removed, the combo box does not
	* redraw to clear the text area.  The fix is to
	* force a redraw.
	*/
	//@@TODO(lpino): Do we need to consider this case in OS/2
//	if ((style & SWT.READ_ONLY) != 0) {		
//		int count = OS.SendMessage (handle, OS.CB_GETCOUNT, 0, 0);
//		if (count == 0) OS.InvalidateRect (handle, null, false);
//	}
}

/**
 * Removes the items from the receiver's list which are
 * between the given zero-relative start and end 
 * indices (inclusive).
 *
 * @param start the start of the range
 * @param end the end of the range
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_INVALID_RANGE - if either the start or end are not between 0 and the number of elements in the list minus 1 (inclusive)</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 * @exception SWTError <ul>
 *    <li>ERROR_ITEM_NOT_REMOVED - if the operation fails because of an operating system failure</li>
 * </ul>
 */
public void remove (int start, int end) {
	checkWidget ();
	if (start > end) return;
	int count = OS.WinSendMsg (handle, OS.LM_QUERYITEMCOUNT, 0, 0);
	int topIndex = OS.WinSendMsg (handle, OS.LM_QUERYTOPINDEX, 0, 0);
//	RECT rect = null;
//	int hDC = 0, oldFont = 0, newFont = 0, newWidth = 0;
//	if ((style & SWT.H_SCROLL) != 0) {
//		rect = new RECT ();
//		hDC = OS.GetDC (handle);
//		newFont = OS.SendMessage (handle, OS.WM_GETFONT, 0, 0);
//		if (newFont != 0) oldFont = OS.SelectObject (hDC, newFont);
//	}
//	int cp = getCodePage ();
	int index = start;
//	int flags = OS.DT_CALCRECT | OS.DT_SINGLELINE | OS.DT_NOPREFIX;
	while (index <= end) {
		PSZ buffer = null;
		if ((style & SWT.H_SCROLL) != 0) {
			int length =OS.WinSendMsg (handle, OS.LM_QUERYITEMTEXTLENGTH,(short)start, 0);
			if (length == OS.LIT_ERROR) break;
			buffer = new PSZ (length + 1);
			int result = OS.WinSendMsg (handle, OS.LM_QUERYITEMTEXT, OS.MPFROM2SHORT((short)start,(short)length), buffer);
			if (result <= 0) break;
		}
		int result =OS.WinSendMsg (handle, OS.LM_DELETEITEM,(short) start, 0);
		if (result <=0) break;
//		if ((style & SWT.H_SCROLL) != 0) {
//			OS.DrawText (hDC, buffer, -1, rect, flags);
//			newWidth = Math.max (newWidth, rect.right - rect.left);
//		}
		index++;
	}
//	if ((style & SWT.H_SCROLL) != 0) {
//		if (newFont != 0) OS.SelectObject (hDC, oldFont);
//		OS.ReleaseDC (handle, hDC);
//		setScrollWidth (newWidth, false);
//	}
	if (end < topIndex) {
		topIndex -= end - start + 1;
		OS.WinSendMsg (handle, OS.LM_SETTOPINDEX, (short)topIndex, 0);
	}
	if (index <= end) {
		if (0 <= index && index < count) error (SWT.ERROR_ITEM_NOT_REMOVED);
		error (SWT.ERROR_INVALID_RANGE);
	}
}

/**
 * Searches the receiver's list starting at the first item
 * until an item is found that is equal to the argument, 
 * and removes that item from the list.
 *
 * @param string the item to remove
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the string is null</li>
 *    <li>ERROR_INVALID_ARGUMENT - if the string is not found in the list</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 * @exception SWTError <ul>
 *    <li>ERROR_ITEM_NOT_REMOVED - if the operation fails because of an operating system failure</li>
 * </ul>
 */
public void remove (String string) {
	int index = indexOf (string, 0);
	if (index == -1) error (SWT.ERROR_INVALID_ARGUMENT);
	remove (index);
}

/**
 * Removes all of the items from the receiver's list.
 * <p>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void removeAll () {
	checkWidget ();
	OS.WinSendMsg (handle, OS.LM_DELETEALL, 0, 0);
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
 * be notified when the receiver's selection changes.
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
 * Selects the item at the given zero-relative index in the receiver's 
 * list.  If the item at the index was already selected, it remains
 * selected. Indices that are out of range are ignored.
 *
 * @param index the index of the item to select
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void select (int index) {
	checkWidget ();
	if (index == -1) return;
	int count = OS.WinSendMsg (handle, OS.LM_QUERYITEMCOUNT, 0, 0);
	if(0 <= index && index < count){
		int selection =  OS.WinSendMsg (handle, OS.LM_QUERYSELECTION, (short)OS.LIT_CURSOR, 0);
		int rc = OS.WinSendMsg (handle, OS.LM_SELECTITEM, (short)index, OS.TRUE);
		if(rc != OS.FALSE && rc != selection)
		    sendEvent (SWT.Modify);
	}
}

//void setBackgroundPixel (int pixel) {
//	if (background == pixel) return;
//	super.setBackgroundPixel (pixel);
//	int hwndText = OS.GetDlgItem (handle, CBID_EDIT);
//	if (hwndText != 0) OS.InvalidateRect (hwndText, null, true);
//	int hwndList = OS.GetDlgItem (handle, CBID_LIST);
//	if (hwndList != 0) OS.InvalidateRect (hwndList, null, true);
//}

public Rectangle getBounds () {
	checkWidget ();
	Rectangle bounds = super.getBounds ();
	/*
	* Windows gives the height of the text field of a drop down combo
	* box. The window of PM includes the list (see setBounds), so the
	* height is that of the text field, as computeSize gives it.
	*/
	if ((style & SWT.DROP_DOWN) != 0) bounds.height = getTextHeight ();
	return bounds;
}

public Point getSize () {
	checkWidget ();
	Point size = super.getSize ();
	/* See getBounds */
	if ((style & SWT.DROP_DOWN) != 0) size.y = getTextHeight ();
	return size;
}

void setBounds (int x, int y, int width, int height, int flags) {
	/*
	* Feature in Windows. If the combo box has the CBS_DROPDOWN
	* or CBS_DROPDOWNLIST style, Windows uses the height that the
	* programmer sets in SetWindowPos () to control height of the
	* drop down list.  When the width is non-zero, Windows remembers
	* this value and sets the height to be the height of the text
	* field part of the combo box.  If the width is zero, Windows
	* allows the height to have any value.  Therefore, when the
	* programmer sets and then queries the height, the values can
	* be different depending on the width.  The problem occurs when
	* the programmer uses computeSize () to determine the preferred
	* height (always the height of the text field) and then uses
	* this value to set the height of the combo box.  The result
	* is a combo box with a zero size drop down list.  The fix, is
	* to always set the height to show a fixed number of combo box
	* items and ignore the height value that the programmer supplies.
	*/
	if ((style & SWT.DROP_DOWN) != 0) {
		int textHeight = getTextHeight();
		super.setBounds (x, y, width, textHeight + (textHeight * 5) + 2, flags);
		return;
	}

	/*
	* Bug in Windows.  If the combo box has the CBS_SIMPLE style,
	* the list portion of the combo box is not redrawn when the
	* combo box is resized.  The fix is to force a redraw when
	* the size has changed.
	*/
//	if (parent.hdwp != 0 || (flags & OS.SWP_NOSIZE) != 0 || !OS.IsWindowVisible (handle)) {
//		super.setBounds (x, y, width, height, flags);
//		return;
//	}
         int hps = OS.WinGetPS (handle);
	 RECTL rcl = new RECTL ();
        OS.WinQueryWindowRect (handle, rcl);
        OS.WinBeginPaint(handle, hps, rcl);
	super.setBounds (x, y, width, height, flags);
//	if ((rect.right - rect.left) != width || (rect.bottom - rect.top) != height) {
//			int uFlags = OS.RDW_ERASE | OS.RDW_INVALIDATE | OS.RDW_ALLCHILDREN;
//			OS.RedrawWindow (handle, null, 0, uFlags);
//	}

        OS.WinReleasePS (hps);
}

//void setEditable (boolean editable) {
//	error (SWT.ERROR_NOT_IMPLEMENTED);
//}
//
//void setForegroundPixel (int pixel) {
//	if (foreground == pixel) return;
//	super.setForegroundPixel (pixel);
//	int hwndText = OS.GetDlgItem (handle, CBID_EDIT);
//	if (hwndText != 0) OS.InvalidateRect (hwndText, null, true);
//	int hwndList = OS.GetDlgItem (handle, CBID_LIST);
//	if (hwndList != 0) OS.InvalidateRect (hwndList, null, true);
//}

/**
 * Sets the text of the item in the receiver's list at the given
 * zero-relative index to the string argument. This is equivalent
 * to <code>remove</code>'ing the old item at the index, and then
 * <code>add</code>'ing the new item at that index.
 *
 * @param index the index for the item
 * @param string the new text for the item
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_INVALID_RANGE - if the index is not between 0 and the number of elements in the list minus 1 (inclusive)</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 * @exception SWTError <ul>
 *    <li>ERROR_ITEM_NOT_REMOVED - if the remove operation fails because of an operating system failure</li>
 *    <li>ERROR_ITEM_NOT_ADDED - if the add operation fails because of an operating system failure</li>
 * </ul>
 */
public void setItem (int index, String string) {
	checkWidget ();
	if (string == null) error (SWT.ERROR_NULL_ARGUMENT);
//	int topIndex = getTopIndex ();
//	boolean isSelected = isSelected (index);
	remove (index);
        if (isDisposed ()) return;
        add (string, index);
//	if (isSelected) select (index);
//	setTopIndex (topIndex);
}

/**
 * Sets the receiver's list to be the given array of items.
 *
 * @param items the array of items
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 * @exception SWTError <ul>
 *    <li>ERROR_ITEM_NOT_ADDED - if the operation fails because of an operating system failure</li>
 * </ul>
 */
public void setItems (String [] items) {
	checkWidget ();
	if (items == null) error (SWT.ERROR_NULL_ARGUMENT);
        OS.WinSendMsg (handle, OS.LM_DELETEALL, 0, 0);
        PSZ[] items2 = new PSZ[items.length];
        for(int i=0;i<items.length;i++){
            OS.WinSendMsg (handle, OS.LM_INSERTITEM, OS.LIT_END, new PSZ(items[i]));
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
private void setSelection (int start, int end) {
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

/**
 * Sets the selection in the receiver's text field to the
 * range specified by the argument whose x coordinate is the
 * start of the selection and whose y coordinate is the end
 * of the selection. 
 *
 * @param a point representing the new selection start and end
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
 * Sets the contents of the receiver's text field to the
 * given string.
 * <p>
 * Note: The text field in a <code>Combo</code> is typically
 * only capable of displaying a single line of text. Thus,
 * setting the text to a string containing line breaks or
 * other special characters will probably cause it to 
 * display incorrectly.
 * </p>
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
	if ((style & SWT.READ_ONLY) != 0) {
		int index = indexOf (string);
		if (index != -1) select (index);
		return;
	}
	PSZ buffer = new PSZ (string);
	if (OS.WinSetWindowText(handle, buffer)) {
		sendEvent (SWT.Modify);
		// widget could be disposed at this point
	}
}

/**
 * Sets the maximum number of characters that the receiver's
 * text field is capable of holding to be the argument.
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
        OS.WinSendMsg (handle, OS.EM_SETTEXTLIMIT, limit, 0);
        textLimit = (short)limit;
}

//boolean translateAccelerator (MSG msg) {
//	if (super.translateAccelerator (msg)) return true;
//	
//	/*
//	* In order to see key events for the text widget in a combo box,
//	* filter the key events before they are dispatched to the text
//	* widget and invoke the cooresponding key handler for the combo
//	* box as if the key was sent directly to the combo box, not the
//	* text field.  The key is still dispatched to the text widget, 
//	* in the normal fashion.  Note that we must call TranslateMessage
//	* in order to process accented keys properly.
//	*/
//	int hwndText = OS.GetDlgItem (handle, CBID_EDIT);
//	if (msg.hwnd == hwndText) {
//		switch (msg.message) {
//			case OS.WM_CHAR:
//			case OS.WM_SYSCHAR:
//			case OS.WM_KEYDOWN:  {
//				Display display = getDisplay ();
//				if (msg.message == OS.WM_KEYDOWN) {
//					if (display.translateTraversal (msg, this)) return true;
//				} else {
//					if (display.translateMnemonic (msg, this)) return true;
//				}
//			}
//		}
//		OS.TranslateMessage (msg);
//		switch (msg.message) {
//			case OS.WM_CHAR:		WM_CHAR (msg.wParam, msg.lParam); break;
//			case OS.WM_IME_CHAR:	WM_IME_CHAR (msg.wParam, msg.lParam); break;
//			case OS.WM_KEYDOWN:	WM_KEYDOWN (msg.wParam, msg.lParam); break;
//			case OS.WM_KEYUP:		WM_KEYUP (msg.wParam, msg.lParam); break;
//			case OS.WM_SYSCHAR:	WM_SYSCHAR (msg.wParam, msg.lParam); break;
//			case OS.WM_SYSKEYDOWN:	WM_SYSKEYDOWN (msg.wParam, msg.lParam); break;
//			case OS.WM_SYSKEYUP:	WM_SYSKEYUP (msg.wParam, msg.lParam); break;
//		}
//		OS.DispatchMessage (msg);
//		return true;
//	}
//	return false;
//}
//
//boolean translateTraversal (MSG msg) {
//	/*
//	* Feature in Windows.  For some reason, when the
//	* user presses tab, return or escape, Windows beeps.
//	* The fix is to look for these keys and not call
//	* the window proc.
//	*/
//	switch (msg.wParam) {
//		case OS.VK_ESCAPE:
//			/* Allow the escape key to close the combo box */
//			if (OS.SendMessage (handle, OS.CB_GETDROPPEDSTATE, 0, 0) != 0) {
//				return false;
//			}
//			// FALL THROUGH
//		case OS.VK_TAB:
//		case OS.VK_RETURN:
//			boolean translated = super.translateTraversal (msg);
//			if (!translated && msg.wParam == OS.VK_RETURN) {
//				sendEvent (SWT.DefaultSelection);
//				// widget could be disposed at this point
//			}
//			return true;
//	}
//	return super.translateTraversal (msg);
//}
//
int widgetStyle () {
	int bits = super.widgetStyle () | OS.WS_VISIBLE;
	if ((style & SWT.SIMPLE) != 0) return bits | OS.CBS_SIMPLE;
	if ((style & SWT.READ_ONLY) != 0) return bits | OS.CBS_DROPDOWNLIST;
	return bits | OS.CBS_DROPDOWN;
}

PSZ windowClass()
{
    return ComboClass;
}

int windowProc()
{
    return ComboProc;
}

MRESULT WM_CHAR (int mp1, int mp2){
	MRESULT result = super.WM_CHAR (mp1, mp2);
	if (result != null) return result;
	if (OS.SHORT2FROMMP (mp2) == OS.VK_ENTER || OS.SHORT2FROMMP (mp2) == OS.VK_NEWLINE) {
		postEvent (SWT.DefaultSelection);
	}
	return result;
}

//LRESULT WM_CTLCOLOR (int wParam, int lParam) {
//	return wmColorChild (wParam, lParam);
//}
//
//LRESULT WM_GETDLGCODE (int wParam, int lParam) {
//	int code = callWindowProc (OS.WM_GETDLGCODE, wParam, lParam);
//	return new LRESULT (code | OS.DLGC_WANTARROWS);
//}
//

//LRESULT WM_SIZE (int wParam, int lParam) {	
//	/*
//	* Feature in Windows.  When an editable drop down combo box
//	* contains text that does not correspond to an item in the
//	* list, when the widget is resized, it selects the closest
//	* match from the list.  The fix is to remember the original
//	* text and reset it after the widget is resized.
//	*/
//	if ((style & SWT.READ_ONLY) != 0 || (style & SWT.DROP_DOWN) == 0) {
//		return super.WM_SIZE (wParam, lParam);
//	}
//	int index = OS.SendMessage (handle, OS.CB_GETCURSEL, 0, 0);
//	boolean redraw = false;
//	TCHAR buffer = null;
//	int [] start = null, end = null;
//	if (index == OS.CB_ERR) {
//		int length = OS.GetWindowTextLength (handle);
//		if (length != 0) {
//			buffer = new TCHAR (getCodePage (), length + 1);
//			OS.GetWindowText (handle, buffer, length + 1);
//			start = new int [1];  end = new int [1];
//			OS.SendMessage (handle, OS.CB_GETEDITSEL, start, end);
//			redraw = drawCount == 0 && OS.IsWindowVisible (handle);
//			if (redraw) setRedraw (false);
//		}
//	}
//	LRESULT result = super.WM_SIZE (wParam, lParam);
//	/*
//	* It is possible (but unlikely), that application
//	* code could have disposed the widget in the resize
//	* event.  If this happens, end the processing of the
//	* Windows message by returning the result of the
//	* WM_SIZE message.
//	*/
//	if (isDisposed ()) return result;
//	if (buffer != null) {
//		OS.SetWindowText (handle, buffer);
//		int bits = start [0] | (end [0] << 16);
//		OS.SendMessage (handle, OS.CB_SETEDITSEL, 0, bits);
//		if (redraw) setRedraw (true);
//	}
//	return result; 
//}

MRESULT wmControlChild (int mp1, int mp2) {
    int code = mp1 >> 16;
	switch (code) {
                case OS.CBN_EFCHANGE:
			sendEvent (SWT.Modify);
			if (isDisposed ()) return MRESULT.ZERO;
			break;
		case OS.CBN_LBSELECT:
			if (isDisposed ()) return MRESULT.ZERO;
			postEvent (SWT.Selection);
			break;
		case OS.LN_SETFOCUS:
		case OS.LN_KILLFOCUS:
			/*
			* It is possible (but unlikely), that application
			* code could have disposed the widget in the focus
			* event.  If this happens, end the processing of the
			* Windows message by returning zero as the result of
			* the window proc.
			*/
			sendEvent (code == OS.LN_SETFOCUS ? SWT.FocusIn : SWT.FocusOut);
			if (isDisposed ()) return MRESULT.ZERO;
			break;                
	}
	return super.wmControlChild (mp1, mp2);
}

}
