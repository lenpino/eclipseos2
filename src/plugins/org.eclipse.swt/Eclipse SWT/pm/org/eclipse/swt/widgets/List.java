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
 * Instances of this class represent a selectable user interface
 * object that displays a list of strings and issues notificiation
 * when a string selected.  A list may be single or multi select.
 * <p>
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>SINGLE, MULTI</dd>
 * <dt><b>Events:</b></dt>
 * <dd>Selection, DefaultSelection</dd>
 * </dl>
 * <p>
 * Note: Only one of SINGLE and MULTI may be specified.
 * </p><p>
 * IMPORTANT: This class is <em>not</em> intended to be subclassed.
 * </p>
 */

public class List extends Scrollable {
    static final int ListProc;
    static final PSZ ListClass = PSZ.getAtom (OS.WC_LISTBOX);
	static {
            CLASSINFO pclsiClassInfo = new CLASSINFO ();
            OS.WinQueryClassInfo (OS.NULLHANDLE, ListClass, pclsiClassInfo);
            ListProc = pclsiClassInfo.pfnWindowProc;
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
 * @see Widget#checkSubclass
 * @see Widget#getStyle
 */
public List (Composite parent, int style) {
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
//	if ((style & SWT.H_SCROLL) != 0) setScrollWidth (buffer, true);
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
//	if ((style & SWT.H_SCROLL) != 0) setScrollWidth (buffer, true);
}

/**
 * Adds the listener to the collection of listeners who will
 * be notified when the receiver's selection changes, by sending
 * it one of the messages defined in the <code>SelectionListener</code>
 * interface.
 * <p>
 * <code>widgetSelected</code> is called when the selection changes.
 * <code>widgetDefaultSelected</code> is typically called when an item is double-clicked.
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
    if (handle == 0) return 0;
    return OS.WinCallWindowProc (ListProc, handle, msg, mp1, mp2);
}

static int checkStyle (int style) {
	return checkBits (style, SWT.SINGLE, SWT.MULTI, 0, 0, 0, 0);
}

public Point computeSize (int wHint, int hHint, boolean changed) {
	checkWidget ();
	int count = OS.WinSendMsg (handle, OS.LM_QUERYITEMCOUNT, 0, 0);
        //@@TODO(lpino): Implement 
//        int rc = wmMeasureItem(OS.WM_MEASUREITEM, handle, 0);
//        System.out.println("Alto = " + OS.SHORT1FROMMP (rc));
//        System.out.println("Ancho = " + OS.SHORT2FROMMP (rc));
        
//	int itemHeight = OS.SendMessage (handle, OS.LB_GETITEMHEIGHT, 0, 0);
//	int width = 0, height = count * itemHeight;
//	if ((style & SWT.H_SCROLL) != 0) {
//		width = OS.SendMessage (handle, OS.LB_GETHORIZONTALEXTENT, 0, 0);
//	} else {
//		int newFont, oldFont = 0;
//		int hDC = OS.GetDC (handle);
//		newFont = OS.SendMessage (handle, OS.WM_GETFONT, 0, 0);
//		if (newFont != 0) oldFont = OS.SelectObject (hDC, newFont);
//		RECT rect = new RECT ();
//		int flags = OS.DT_CALCRECT | OS.DT_SINGLELINE | OS.DT_NOPREFIX;
//		int cp = getCodePage ();
//		TCHAR buffer = new TCHAR (cp, 64 + 1);
//		for (int i=0; i<count; i++) {
//			int length = OS.SendMessage (handle, OS.LB_GETTEXTLEN, i, 0);
//			if (length != OS.LB_ERR) {
//				if (length + 1 > buffer.length ()) {
//					buffer = new TCHAR (cp, length + 1);
//				}
//				int result = OS.SendMessage (handle, OS.LB_GETTEXT, i, buffer);
//				if (result != OS.LB_ERR) {
//					OS.DrawText (hDC, buffer, length, rect, flags);
//					width = Math.max (width, rect.right - rect.left);
//				}
//			}
//		}	
//		if (newFont != 0) OS.SelectObject (hDC, oldFont);
//		OS.ReleaseDC (handle, hDC);
//	}
//	if (width == 0) width = DEFAULT_WIDTH;
//	if (height == 0) height = DEFAULT_HEIGHT;
//	if (wHint != SWT.DEFAULT) width = wHint;
//	if (hHint != SWT.DEFAULT) height = hHint;
//	int border = getBorderWidth ();
//	width += border * 2 + 3;
//	height += border * 2;
//	if ((style & SWT.V_SCROLL) != 0) {
//		width += OS.GetSystemMetrics (OS.SM_CXVSCROLL);
//	}
//	if ((style & SWT.H_SCROLL) != 0) {
//		height += OS.GetSystemMetrics (OS.SM_CYHSCROLL);
//	}
	return new Point (0, 0);
}

int defaultBackground () {
            return OS.WinQuerySysColor(handle, OS.SYSCLR_ENTRYFIELD,0);
}

/**
 * Deselects the items at the given zero-relative indices in the receiver.
 * If the item at the given zero-relative index in the receiver 
 * is selected, it is deselected.  If the item at the index
 * was not selected, it remains deselected. Indices that are out
 * of range and duplicate indices are ignored.
 *
 * @param indices the array of indices for the items to deselect
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void deselect (int [] indices) {
	checkWidget ();
	if (indices == null) error (SWT.ERROR_NULL_ARGUMENT);
	if (indices.length == 0) return;
	if ((style & SWT.SINGLE) != 0) {
		int oldIndex =OS.WinSendMsg (handle, OS.LM_QUERYSELECTION, 0, 0);
		if (oldIndex == OS.LIT_NONE) return;
		for (int i=0; i<indices.length; i++) {
			if (oldIndex == indices [i]) {
				OS.WinSendMsg (handle, OS.LM_SELECTITEM, oldIndex, OS.FALSE);
				return;
			}
		}
		return;
	}
	for (int i=0; i<indices.length; i++) {
		int index = indices [i];
		if (index != -1) {
			OS.WinSendMsg (handle, OS.LM_SELECTITEM, index, OS.FALSE);
		}
	}
}

/**
 * Deselects the item at the given zero-relative index in the receiver.
 * If the item at the index was already deselected, it remains
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
	if ((style & SWT.SINGLE) != 0) {
		int oldIndex =OS.WinSendMsg (handle, OS.LM_QUERYSELECTION, 0, 0);
		if (oldIndex == OS.LIT_NONE) return;
		if (oldIndex == index) OS.WinSendMsg (handle, OS.LM_SELECTITEM, index, OS.FALSE);
		return;
	} 
	OS.WinSendMsg (handle, OS.LM_SELECTITEM, index, OS.FALSE);
}

/**
 * Deselects the items at the given zero-relative indices in the receiver.
 * If the item at the given zero-relative index in the receiver 
 * is selected, it is deselected.  If the item at the index
 * was not selected, it remains deselected.  The range of the
 * indices is inclusive. Indices that are out of range are ignored.
 *
 * @param start the start index of the items to deselect
 * @param end the end index of the items to deselect
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void deselect (int start, int end) {
	checkWidget ();
	if (start > end) return;
	if ((style & SWT.SINGLE) != 0) {
		int oldIndex =OS.WinSendMsg (handle, OS.LM_QUERYSELECTION, 0, 0);
		if (oldIndex == OS.LIT_NONE) return;
		if (start <= oldIndex && oldIndex <= end) {
			OS.WinSendMsg (handle, OS.LM_SELECTITEM, oldIndex, OS.FALSE);
		}
		return;
	}
	/*
	* Ensure that at least one item is contained in
	* the range from start to end.  Note that when
	* start = end, LB_SELITEMRANGEEX deselects the
	* item.
	*/
	int count = OS.WinSendMsg (handle, OS.LM_QUERYITEMCOUNT, 0, 0);
	if (start < 0 && end < 0) return;
	if (start >= count && end >= count) return;
	start = Math.min (count - 1, Math.max (0, start));
	end = Math.min (count - 1, Math.max (0, end));
        for(int i=start;i<=end;i++){
            OS.WinSendMsg (handle, OS.LM_SELECTITEM, i, OS.FALSE);
        }
}

/**
 * Deselects all selected items in the receiver.
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void deselectAll () {
	checkWidget ();
	if ((style & SWT.SINGLE) != 0) {
		int oldIndex =OS.WinSendMsg (handle, OS.LM_QUERYSELECTION, 0, 0);
		if (oldIndex == OS.LIT_NONE) return;
                OS.WinSendMsg (handle, OS.LM_SELECTITEM, oldIndex, OS.FALSE);
		return;
	}
        int count = OS.WinSendMsg (handle, OS.LM_QUERYITEMCOUNT, 0, 0);
        int index = 0;
        for(int i=0;i<count;i++){
            index = OS.WinSendMsg (handle, OS.LM_QUERYSELECTION, index, 0);
            OS.WinSendMsg (handle, OS.LM_SELECTITEM, index, OS.FALSE);
        }
}

/**
 * Returns the zero-relative index of the item which is currently
 * has the focus in the receiver, or -1 if no item is has focus.
 *
 * @return the index of the selected item
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public int getFocusIndex () {
	checkWidget ();
	return OS.WinSendMsg (handle, OS.LM_QUERYSELECTION, OS.LIT_CURSOR, 0);
}

/**
 * Returns the item at the given, zero-relative index in the
 * receiver. Throws an exception if the index is out of range.
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
		int result = OS.WinSendMsg (handle, OS.LM_QUERYITEMTEXT, OS.MPFROM2SHORT((short)length,(short)index), buffer);
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
 * Returns the number of items contained in the receiver.
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
 * display <em>one</em> of the items in the tree.
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
 * in the receiver. 
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
 *    <li>ERROR_CANNOT_GET_ITEM - if the operation fails because of an operating system failure while getting an item</li>
 *    <li>ERROR_CANNOT_GET_COUNT - if the operation fails because of an operating system failure while getting the item count</li>
 * </ul>
 */
public String [] getItems () {
	checkWidget ();
	int count = getItemCount ();
	String [] result = new String [count];
	for (int i=0; i<count; i++) result [i] = getItem (i);
	return result;
}

/**
 * Returns an array of <code>String</code>s that are currently
 * selected in the receiver. An empty array indicates that no
 * items are selected.
 * <p>
 * Note: This is not the actual structure used by the receiver
 * to maintain its selection, so modifying the array will
 * not affect the receiver. 
 * </p>
 * @return an array representing the selection
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 * @exception SWTError <ul>
 *    <li>ERROR_CANNOT_GET_SELECTION - if the operation fails because of an operating system failure while getting the selection</li>
 *    <li>ERROR_CANNOT_GET_ITEM - if the operation fails because of an operating system failure while getting an item</li>
 * </ul>
 */
public String [] getSelection () {
	checkWidget ();
	int [] indices = getSelectionIndices ();
	String [] result = new String [indices.length];
	for (int i=0; i<indices.length; i++) {
		result [i] = getItem (indices [i]);
	}
	return result;
}

/**
 * Returns the number of selected items contained in the receiver.
 *
 * @return the number of selected items
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 * @exception SWTError <ul>
 *    <li>ERROR_CANNOT_GET_COUNT - if the operation fails because of an operating system failure</li>
 * </ul>
 */
public int getSelectionCount () {
	checkWidget ();
        int length = OS.WinSendMsg (handle, OS.LM_QUERYITEMCOUNT, 0, 0);
        int index = 0;
        int result = 0;
        for(int i=0;i<length;i++){
            index = OS.WinSendMsg (handle, OS.LM_QUERYSELECTION, index, 0);
            if(index != OS.LIT_NONE){
                result++;
            }
        }
	return result;
}

/**
 * Returns the zero-relative index of the item which is currently
 * selected in the receiver, or -1 if no item is selected.
 *
 * @return the index of the selected item
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 * @exception SWTError <ul>
 *    <li>ERROR_CANNOT_GET_SELECTION - if the operation fails because of an operating system failure</li>
 * </ul>
 */
public int getSelectionIndex () {
	checkWidget ();
        int index = OS.WinSendMsg (handle, OS.LM_QUERYSELECTION, OS.LIT_FIRST, 0);
        if(index == OS.LIT_NONE)
            return -1;
        else
            return index;
}

/**
 * Returns the zero-relative indices of the items which are currently
 * selected in the receiver.  The array is empty if no items are selected.
 * <p>
 * Note: This is not the actual structure used by the receiver
 * to maintain its selection, so modifying the array will
 * not affect the receiver. 
 * </p>
 * @return the array of indices of the selected items
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 * @exception SWTError <ul>
 *    <li>ERROR_CANNOT_GET_SELECTION - if the operation fails because of an operating system failure</li>
 * </ul>
 */
public int [] getSelectionIndices () {
	checkWidget ();
        int length = OS.WinSendMsg (handle, OS.LM_QUERYITEMCOUNT, 0, 0);
        int [] indices = new int [length];
        int index = 0;
        for(int i=0;i<length;i++){
            index = OS.WinSendMsg (handle, OS.LM_QUERYSELECTION, index, 0);
            if(index != OS.LIT_NONE){
                indices[i] = index;
                i++;
            }
        }
//	if (length == OS.LB_ERR) error (SWT.ERROR_CANNOT_GET_SELECTION);
//	if (result != length) error (SWT.ERROR_CANNOT_GET_SELECTION);
	return indices;
}

/**
 * Returns the zero-relative index of the item which is currently
 * at the top of the receiver. This index can change when items are
 * scrolled or new items are added or removed.
 *
 * @return the index of the top item
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public int getTopIndex () {
	checkWidget ();
	return OS.WinSendMsg (handle, OS.LM_QUERYTOPINDEX, 0, 0);
}

/**
 * Gets the index of an item.
 * <p>
 * The list is searched starting at 0 until an
 * item is found that is equal to the search item.
 * If no item is found, -1 is returned.  Indexing
 * is zero based.
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
 * @exception SWTError <ul>
 *    <li>ERROR_CANNOT_GET_COUNT - if the operation fails because of an operating system failure while getting the item count</li>
 *    <li>ERROR_CANNOT_GET_ITEM - if the operation fails because of an operating system failure while getting an item</li>
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

//	/* Use LM_SEARCHSTRING to search for the item */
	int count = OS.WinSendMsg (handle, OS.LM_QUERYITEMCOUNT, 0, 0);
	if (!((0 <= start) && (start < count))) return -1;
	int index = 0;
        PSZ buffer = new PSZ(string);
        index = OS.WinSendMsg (handle, OS.LM_SEARCHSTRING, OS.MPFROM2SHORT((short)OS.LSS_CASESENSITIVE,(short)(start)), buffer);
        if (index == OS.LIT_ERROR || index < start) return -1;
        return index;
}

/**
 * Returns <code>true</code> if the item is selected,
 * and <code>false</code> otherwise.  Indices out of
 * range are ignored.
 *
 * @param index the index of the item
 * @return the visibility state of the item at the index
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public boolean isSelected (int index) {
	checkWidget ();
	int result = OS.WinSendMsg (handle, OS.LM_QUERYSELECTION, index, 0);
	return (result != 0) && (result != OS.LIT_NONE);
}

/**
 * Removes the items from the receiver at the given
 * zero-relative indices.
 *
 * @param indices the array of indices of the items
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
public void remove (int [] indices) {
	checkWidget ();
	if (indices == null) error (SWT.ERROR_NULL_ARGUMENT);
	int [] newIndices = new int [indices.length];
	System.arraycopy (indices, 0, newIndices, 0, indices.length);
	sort (newIndices);
	int count =  OS.WinSendMsg (handle, OS.LM_QUERYITEMCOUNT, 0, 0);
	int topIndex =  OS.WinSendMsg (handle, OS.LM_QUERYTOPINDEX, 0, 0);
//	RECT rect = null;
//	int hDC = 0, oldFont = 0, newFont = 0, newWidth = 0;
//	if ((style & SWT.H_SCROLL) != 0) {
//		rect = new RECT ();
//		hDC = OS.GetDC (handle);
//		newFont = OS.SendMessage (handle, OS.WM_GETFONT, 0, 0);
//		if (newFont != 0) oldFont = OS.SelectObject (hDC, newFont);
//	}
//	int cp = getCodePage ();
	int i = 0, topCount = 0, last = -1;
	while (i < newIndices.length) {
		int index = newIndices [i];
		if (index != last || i == 0) {
//			TCHAR buffer = null;
//			if ((style & SWT.H_SCROLL) != 0) {
//				int length = OS.SendMessage (handle, OS.LB_GETTEXTLEN, index, 0);
//				if (length == OS.LB_ERR) break;
//				buffer = new TCHAR (cp, length + 1);
//				int result = OS.SendMessage (handle, OS.LB_GETTEXT, index, buffer);
//				if (result == OS.LB_ERR) break;
//			}
			int result =  OS.WinSendMsg (handle, OS.LM_DELETEITEM, index, 0);
//			if (result == OS.LB_ERR) break;
//			if ((style & SWT.H_SCROLL) != 0) {
//				int flags = OS.DT_CALCRECT | OS.DT_SINGLELINE | OS.DT_NOPREFIX;
//				OS.DrawText (hDC, buffer, -1, rect, flags);
//				newWidth = Math.max (newWidth, rect.right - rect.left);
//			}
			if (index < topIndex) topCount++;
			last = index;
		}
		i++;
	}
//	if ((style & SWT.H_SCROLL) != 0) {
//		if (newFont != 0) OS.SelectObject (hDC, oldFont);
//		OS.ReleaseDC (handle, hDC);
//		setScrollWidth (newWidth, false);
//	}
	if (topCount > 0) {
		topIndex -= topCount;
		OS.WinSendMsg (handle, OS.LM_SETTOPINDEX, (short)topIndex, 0);
	}
	if (i < newIndices.length) {
		int index = newIndices [i];
		if (0 <= index && index < count) error (SWT.ERROR_ITEM_NOT_REMOVED);
		error (SWT.ERROR_INVALID_RANGE);
	}
}

/**
 * Removes the item from the receiver at the given
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
	PSZ buffer = null;
	if ((style & SWT.H_SCROLL) != 0) {
		int length = OS.WinSendMsg (handle, OS.LM_QUERYITEMTEXTLENGTH,(short)index, 0);
		if (length == OS.LIT_ERROR) error (SWT.ERROR_ITEM_NOT_REMOVED);
		buffer = new PSZ (length + 1);
		int result = OS.WinSendMsg (handle, OS.LM_QUERYITEMTEXT, OS.MPFROM2SHORT((short)length,(short)index), buffer);
		if (result <= 0) error (SWT.ERROR_ITEM_NOT_REMOVED);
	}
	int topIndex = OS.WinSendMsg (handle, OS.LM_QUERYTOPINDEX, 0, 0);
	int result = OS.WinSendMsg (handle, OS.LM_DELETEITEM,(short) index, 0);
	if (result <= 0) {
		int count = OS.WinSendMsg (handle, OS.LM_QUERYITEMCOUNT, 0, 0);
		if (0 <= index && index < count) error (SWT.ERROR_ITEM_NOT_REMOVED);
		error (SWT.ERROR_INVALID_RANGE);
	}
//@@TODO(lpino): 
//	if ((style & SWT.H_SCROLL) != 0) setScrollWidth (buffer, false);
	if (index < topIndex) {
		OS.WinSendMsg (handle, OS.LM_SETTOPINDEX, (short)(topIndex-1), 0);
	}
}

/**
 * Removes the items from the receiver which are
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
			int result = OS.WinSendMsg (handle, OS.LM_QUERYITEMTEXT, OS.MPFROM2SHORT((short)length,(short)start), buffer);
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
	checkWidget ();
	int index = indexOf (string, 0);
	if (index == -1) error (SWT.ERROR_INVALID_ARGUMENT);
	remove (index);
}

/**
 * Removes all of the items from the receiver.
 * <p>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void removeAll () {
	checkWidget ();
	OS.WinSendMsg (handle, OS.LM_DELETEALL, 0, 0);
//@@TODO(lpino):
//	if ((style & SWT.H_SCROLL) != 0) {
//		OS.SendMessage (handle, OS.LB_SETHORIZONTALEXTENT, 0, 0);
//	}
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
public void removeSelectionListener(SelectionListener listener) {
	checkWidget ();
	if (listener == null) error (SWT.ERROR_NULL_ARGUMENT);
	if (eventTable == null) return;
	eventTable.unhook (SWT.Selection, listener);
	eventTable.unhook (SWT.DefaultSelection,listener);	
}

/**
 * Selects the items at the given zero-relative indices in the receiver.
 * If the item at the given zero-relative index in the receiver 
 * is not selected, it is selected.  If the item at the index
 * was selected, it remains selected. Indices that are out
 * of range and duplicate indices are ignored.
 *
 * @param indices the array of indices for the items to select
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void select (int [] indices) {
	checkWidget ();
	int focusIndex = getFocusIndex ();
	for (int i=0; i<indices.length; i++) {
		int index = indices [i];
		if (index != -1) {
			OS.WinSendMsg (handle, OS.LM_SELECTITEM, (short)index, OS.TRUE);
		}
	}
	if (focusIndex != -1) setFocusIndex (focusIndex);
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
	int rc = OS.WinSendMsg (handle, OS.LM_SELECTITEM, (short)index, OS.TRUE);
//@@TODO(lpino): This could implemented using the WM_MEASUREITEM message
//	RECT itemRect = new RECT (), selectedRect = null;
//	OS.SendMessage (handle, OS.LB_GETITEMRECT, index, itemRect);
//	boolean redraw = drawCount == 0 && OS.IsWindowVisible (handle);
//	if (redraw) {
//		OS.UpdateWindow (handle);
//		OS.SendMessage (handle, OS.WM_SETREDRAW, 0, 0);
//	}
//	int focusIndex = -1;
//	if ((style & SWT.SINGLE) != 0) {
//		int oldIndex = OS.SendMessage (handle, OS.LB_GETCURSEL, 0, 0);
//		if (oldIndex != -1) {
//			selectedRect = new RECT ();
//			OS.SendMessage (handle, OS.LB_GETITEMRECT, oldIndex, selectedRect);
//		}
//		OS.SendMessage (handle, OS.LB_SETCURSEL, index, 0);
//	} else {
//		focusIndex = OS.SendMessage (handle, OS.LB_GETCARETINDEX, 0, 0);
//		OS.SendMessage (handle, OS.LB_SETSEL, 1, index);
//	}
//	OS.SendMessage (handle, OS.LB_SETTOPINDEX, topIndex, 0);
//	if (redraw) {
//		OS.SendMessage (handle, OS.WM_SETREDRAW, 1, 0);
//		OS.ValidateRect (handle, null);
//		OS.InvalidateRect (handle, itemRect, true);
//		if (selectedRect != null) {
//			OS.InvalidateRect (handle, selectedRect, true);
//		}
//	}
//	if ((style & SWT.MULTI) != 0) {
//		if (focusIndex != -1) {
//			OS.SendMessage (handle, OS.LB_SETCARETINDEX, index, 0);
//		}
//	}
}

/**
 * Selects the items at the given zero-relative indices in the receiver.
 * If the item at the index was already selected, it remains
 * selected. The range of the indices is inclusive. Indices that are
 * out of range are ignored.
 *
 * @param start the start of the range
 * @param end the end of the range
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void select (int start, int end) {
	checkWidget ();
	if (start > end) return;
	int count = OS.WinSendMsg (handle, OS.LM_QUERYITEMCOUNT, 0, 0);
	if (start < 0 && end < 0) return;
	if (start >= count && end >= count) return;
	start = Math.min (count - 1, Math.max (0, start));
	end = Math.min (count - 1, Math.max (0, end));
	if (start == end) {
		select (start);
		return;
	}
        for(int i=start;i<=end;i++)
            select(i);
}

/**
 * Selects all the items in the receiver.
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void selectAll () {
	checkWidget ();
	if ((style & SWT.SINGLE) != 0) return;
        int count = OS.WinSendMsg (handle, OS.LM_QUERYITEMCOUNT, 0, 0);
        for(int i=0;i<count;i++)
            select(i);
}

//void setBounds (int x, int y, int width, int height, int flags) {
//	/*
//	* Bug in Windows.  If the receiver is scrolled horizontally
//	* and is resized, the list does not redraw properly.  The fix
//	* is to redraw the receiver.
//	*/
//	if ((parent.hdwp != 0) || ((flags & OS.SWP_NOSIZE) != 0)) {	
//		super.setBounds (x, y, width, height, flags);
//		return;
//	}
//	RECT rect = new RECT ();
//	OS.GetWindowRect (handle, rect);
//	super.setBounds (x, y, width, height, flags);
//	if (((rect.right - rect.left) == width) || ((rect.bottom - rect.top) == height)) return;
//	SCROLLINFO info = new SCROLLINFO ();
//	info.cbSize = SCROLLINFO.sizeof;
//	info.fMask = OS.SIF_POS;
//	if (!OS.GetScrollInfo (handle, OS.SB_HORZ, info)) return;
//	if (info.nPos != 0) OS.InvalidateRect (handle, null, true);
//}

void setFocusIndex (int index) {
	OS.WinSendMsg (handle, OS.LM_SELECTITEM, (short)index, OS.TRUE);
}

public void setFont (Font font) {
	checkWidget ();
	super.setFont (font);
//@@TODO(lpino): Implement setScrollWidth ()
//        if ((style & SWT.H_SCROLL) != 0) setScrollWidth ();
}

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
	int topIndex = getTopIndex ();
	boolean isSelected = isSelected (index);
	remove (index);
	add (string, index);
	if (isSelected) select (index);
	setTopIndex (topIndex);
}

/**
 * Sets the receiver's items to be the given array of items.
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
        LBOXINFO lboxdata = new LBOXINFO();
        lboxdata.lItemIndex = 0;
        lboxdata.ulItemCount = items.length;
        for(int i=0;i<items.length;i++)
            items2[i] = new PSZ(items[i]);
         int count = OS.WinSendMsg (handle, OS.LM_INSERTMULTITEMS, lboxdata, items2);
//@@TODO(lpino): Implement with LM_INSERTMULTITEMS
//	int oldProc = OS.GetWindowLong (handle, OS.GWL_WNDPROC);
//	OS.SetWindowLong (handle, OS.GWL_WNDPROC, ListProc);
//	boolean redraw = drawCount == 0 && OS.IsWindowVisible (handle);
//	if (redraw) {
//		OS.SendMessage (handle, OS.WM_SETREDRAW, 0, 0);
//	}
//	RECT rect = null;
//	boolean failed = false;
//	int hDC = 0, oldFont = 0, newFont = 0, newWidth = 0;
//	if ((style & SWT.H_SCROLL) != 0) {
//		rect = new RECT ();
//		hDC = OS.GetDC (handle);
//		newFont = OS.SendMessage (handle, OS.WM_GETFONT, 0, 0);
//		if (newFont != 0) oldFont = OS.SelectObject (hDC, newFont);
//		OS.SendMessage (handle, OS.LB_SETHORIZONTALEXTENT, 0, 0);
//	}
//	int length = items.length;
//	OS.SendMessage (handle, OS.LB_RESETCONTENT, 0, 0);
//	OS.SendMessage (handle, OS.LB_INITSTORAGE, length, length * 32);
//	int index = 0;
//	int cp = getCodePage ();
//	while (index < length) {
//		String string = items [index];
//		if (string == null) break;
//		TCHAR buffer = new TCHAR (cp, string, true);
//		int result = OS.SendMessage (handle, OS.LB_ADDSTRING, 0, buffer);
//		if (result == OS.LB_ERR || result == OS.LB_ERRSPACE) break;
//		if ((style & SWT.H_SCROLL) != 0) {
//			int flags = OS.DT_CALCRECT | OS.DT_SINGLELINE | OS.DT_NOPREFIX;
//			OS.DrawText (hDC, buffer, buffer.length (), rect, flags);
//			newWidth = Math.max (newWidth, rect.right - rect.left);
//		}
//		index++;
//	}
//	if ((style & SWT.H_SCROLL) != 0) {
//		if (newFont != 0) OS.SelectObject (hDC, oldFont);
//		OS.ReleaseDC (handle, hDC);
//		OS.SendMessage (handle, OS.LB_SETHORIZONTALEXTENT, newWidth + 3, 0);
//	}
//	if (redraw) {
//		OS.SendMessage (handle, OS.WM_SETREDRAW, 1, 0);
//		/*
//		* This code is intentionally commented.  The window proc
//		* for the list box implements WM_SETREDRAW to invalidate
//		* and erase the widget.  This is undocumented behavior.
//		* The commented code below shows what is actually happening
//		* and reminds us that we are relying on this undocumented
//		* behavior.
//		*/
////		int flags = OS.RDW_ERASE | OS.RDW_FRAME | OS.RDW_INVALIDATE;
////		OS.RedrawWindow (handle, null, 0, flags);
//	}
//	OS.SetWindowLong (handle, OS.GWL_WNDPROC, oldProc);
//	if (index < items.length) error (SWT.ERROR_ITEM_NOT_ADDED);
}
//@@TODO(lpino): Implement ?
//void setScrollWidth () {
//	int newWidth = 0;
//	RECT rect = new RECT ();
//	int newFont, oldFont = 0;
//	int hDC = OS.GetDC (handle);
//	newFont = OS.SendMessage (handle, OS.WM_GETFONT, 0, 0);
//	if (newFont != 0) oldFont = OS.SelectObject (hDC, newFont);
//	int cp = getCodePage ();
//	int count = OS.SendMessage (handle, OS.LB_GETCOUNT, 0, 0);
//	int flags = OS.DT_CALCRECT | OS.DT_SINGLELINE | OS.DT_NOPREFIX;
//	for (int i=0; i<count; i++) {
//		int length = OS.SendMessage (handle, OS.LB_GETTEXTLEN, i, 0);
//		if (length != OS.LB_ERR) {
//			TCHAR buffer = new TCHAR (cp, length + 1);
//			int result = OS.SendMessage (handle, OS.LB_GETTEXT, i, buffer);
//			if (result != OS.LB_ERR) {
//				OS.DrawText (hDC, buffer, -1, rect, flags);
//				newWidth = Math.max (newWidth, rect.right - rect.left);
//			}
//		}
//	}
//	if (newFont != 0) OS.SelectObject (hDC, oldFont);
//	OS.ReleaseDC (handle, hDC);
//	OS.SendMessage (handle, OS.LB_SETHORIZONTALEXTENT, newWidth + 3, 0);
//}
//@@TODO(lpino): Implement ?
//void setScrollWidth (TCHAR buffer, boolean grow) {
//	RECT rect = new RECT ();
//	int newFont, oldFont = 0;
//	int hDC = OS.GetDC (handle);
//	newFont = OS.SendMessage (handle, OS.WM_GETFONT, 0, 0);
//	if (newFont != 0) oldFont = OS.SelectObject (hDC, newFont);
//	int flags = OS.DT_CALCRECT | OS.DT_SINGLELINE | OS.DT_NOPREFIX;
//	OS.DrawText (hDC, buffer, -1, rect, flags);
//	if (newFont != 0) OS.SelectObject (hDC, oldFont);
//	OS.ReleaseDC (handle, hDC);
//	setScrollWidth (rect.right - rect.left, grow);
//}

//void setScrollWidth (int newWidth, boolean grow) {
//	int width = OS.SendMessage (handle, OS.LB_GETHORIZONTALEXTENT, 0, 0);
//	if (grow) {
//		if (newWidth <= width) return;
//		OS.SendMessage (handle, OS.LB_SETHORIZONTALEXTENT, newWidth + 3, 0);
//	} else {
//		if (newWidth < width) return;
//		setScrollWidth ();
//	}
//}

/**
 * Selects the items at the given zero-relative indices in the receiver. 
 * The current selection is first cleared, then the new items are selected.
 *
 * @param indices the indices of the items to select
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @see List#deselectAll()
 * @see List#select(int[])
 */
public void setSelection(int [] indices) {
	checkWidget ();
	if (indices == null) error (SWT.ERROR_NULL_ARGUMENT);
	if ((style & SWT.MULTI) != 0) deselectAll ();
	select (indices);
	if ((style & SWT.MULTI) != 0) {
		if (indices.length != 0) {
			int focusIndex = indices [0];
			if (focusIndex != -1) setFocusIndex (focusIndex);
		}
	}
}

/**
 * Sets the receiver's selection to be the given array of items.
 * The current selected is first cleared, then the new items are
 * selected.
 *
 * @param items the array of items
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @see List#deselectAll()
 * @see List#select(int)
 */
public void setSelection (String [] items) {
	checkWidget ();
	if (items == null) error (SWT.ERROR_NULL_ARGUMENT);
	if ((style & SWT.MULTI) != 0) deselectAll ();
	int focusIndex = -1;
	for (int i=items.length-1; i>=0; --i) {
		String string = items [i];
		int index = 0;
		if (string != null) {
			int localFocus = -1;
			while ((index = indexOf (string, index)) != -1) {
				if (localFocus == -1) localFocus = index;
				select (index);
				if ((style & SWT.SINGLE) != 0 && isSelected (index)) {
					/*
					* Return and rely on the fact that select ()
					* for single-select lists clears the previous
					* selection.
					*/
					return;
				}
				index++;
			}
			if (localFocus != -1) focusIndex = localFocus;
		}
	}
	if ((style & SWT.SINGLE) != 0) deselectAll ();
	if ((style & SWT.MULTI) != 0) {
		if (focusIndex != -1) setFocusIndex (focusIndex);
	}
}

/**
 * Selects the item at the given zero-relative index in the receiver. 
 * If the item at the index was already selected, it remains selected.
 * The current selected is first cleared, then the new items are selected.
 * Indices that are out of range are ignored.
 *
 * @param index the index of the item to select
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 * @see List#deselectAll()
 * @see List#select(int)
 */
public void setSelection (int index) {
	checkWidget ();
	if ((style & SWT.MULTI) != 0) deselectAll ();
	select (index);
	if ((style & SWT.MULTI) != 0) {
		if (index != -1) setFocusIndex (index);
	}
}

/**
 * Selects the items at the given zero-relative indices in the receiver. 
 * The current selected if first cleared, then the new items are selected.
 *
 * @param start the start index of the items to select
 * @param end the end index of the items to select
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @see Table#deselectAll()
 * @see Table#select(int,int)
 */
public void setSelection (int start, int end) {
	checkWidget ();
	if ((style & SWT.MULTI) != 0) deselectAll ();
	select (start, end);
	if ((style & SWT.MULTI) != 0) {
		if (start != -1) setFocusIndex (start);
	}
}

/**
 * Sets the zero-relative index of the item which is currently
 * at the top of the receiver. This index can change when items
 * are scrolled or new items are added and removed.
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
	int result = OS.WinSendMsg (handle, OS.LM_SETTOPINDEX, (short)index, 0);
	if (result == OS.FALSE) {
		int count = OS.WinSendMsg (handle, OS.LM_QUERYITEMCOUNT, 0, 0);
		index = Math.min (count - 1, Math.max (0, index));
		OS.WinSendMsg (handle, OS.LM_SETTOPINDEX, (short)index, 0);
	}
}

/**
 * Shows the selection.  If the selection is already showing in the receiver,
 * this method simply returns.  Otherwise, the items are scrolled until
 * the selection is visible.
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
	int index;
        int result = OS.WinSendMsg (handle, OS.LM_QUERYSELECTION, OS.LIT_FIRST, 0);
        index = result;
	if (result == OS.LIT_NONE) return;
//@@TODO(lpino): Test border conditions to see if this code is necesary
//	int count = OS.SendMessage (handle, OS.LB_GETCOUNT, 0, 0);
//	if (count == 0) return;
//	int height = OS.SendMessage (handle, OS.LB_GETITEMHEIGHT, 0, 0);
//	RECT rect = new RECT ();
//	OS.GetClientRect (handle, rect);
//	int topIndex = OS.SendMessage (handle, OS.LB_GETTOPINDEX, 0, 0);
//	int visibleCount = Math.max (rect.bottom / height, 1);
//	int bottomIndex = Math.min (topIndex + visibleCount + 1, count - 1);
//	if ((topIndex <= index) && (index <= bottomIndex)) return;
//	int newTop = Math.min (Math.max (index - (visibleCount / 2), 0), count - 1);
	OS.WinSendMsg (handle, OS.LM_SETTOPINDEX,(short) index, 0);
}

int widgetStyle () {
	int bits = super.widgetStyle () | OS.LS_NOADJUSTPOS;
	if ((style & SWT.SINGLE) != 0) return bits;
	if ((style & SWT.MULTI) != 0) {
		if ((style & SWT.SIMPLE) != 0)
                    bits |= OS.LS_MULTIPLESEL;
                else
                    bits |= OS.LS_EXTENDEDSEL;
	}
        if ((style & SWT.H_SCROLL) != 0)
            bits |= OS.LS_HORZSCROLL;
	return bits;
}

PSZ windowClass () {
	return ListClass;
}

int windowProc () {
	return ListProc;
}
//@@TODO(lpino): Implement this call
//MRESULT WM_MEASUREITEM (int mp1, int mp2) {
//    return super.WM_MEASUREITEM (mp1, mp2);
//}

MRESULT wmControlChild (int mp1, int mp2) {
    int code = mp1 >> 16;
    switch (code) {
        case OS.LN_SELECT:
            postEvent (SWT.Selection);
            break;
        case OS.LN_ENTER:
           postEvent(SWT.DefaultSelection);
           break;
    }
    return super.wmControlChild (mp1, mp2);
}

MRESULT wmScroll (int msg, int mp1, int mp2) {
	int code = callWindowProc (msg, mp1, mp2);
	if (code == 0) return MRESULT.ZERO;
	return new MRESULT (code);
}

//@@TODO(lpino): Implement this
//int wmMeasureItem (int msg, int mp1, int mp2) {
//	int code = callWindowProc (msg, mp1, mp2);
////	if (code == 0) return MRESULT.ZERO;
//	return code;    
//}

}
