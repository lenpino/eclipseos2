package org.eclipse.swt.widgets;

/*
 * OS/2 version.
 * Copyright (c) 2002, 2026 EclipseOS2 Team.
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
 * Instances of this class provide a selectable user interface object
 * that displays a hierarchy of items and issue notificiation when an
 * item in the hierarchy is selected.
 * <p>
 * The item children that may be added to instances of this class
 * must be of type <code>TreeItem</code>.
 * </p><p>
 * Note that although this class is a subclass of <code>Composite</code>,
 * it does not make sense to add <code>Control</code> children to it,
 * or set a layout on it.
 * </p><p>
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>SINGLE, MULTI, CHECK</dd>
 * <dt><b>Events:</b></dt>
 * <dd>Selection, DefaultSelection, Collapse, Expand</dd>
 * </dl>
 * <p>
 * Note: Only one of the styles SINGLE and MULTI may be specified.
 * </p><p>
 * IMPORTANT: This class is <em>not</em> intended to be subclassed.
 * </p>
 * <p>
 * OS/2 note: this is the container control of PM in its tree view. The items
 * are records of the container, which lives in native memory: they are handled
 * through their addresses (see <code>peek()</code> and <code>poke()</code>),
 * and the texts are allocated apart because the container keeps pointers to
 * them. The container has no check boxes, so <code>SWT.CHECK</code> has no
 * effect. In its tree view the container always has one selected record and
 * no more, so the selection is kept by the tree, which draws the texts of the
 * records (owner draw): the record of the container is the cursor. With the
 * keyboard the cursor moves and selects one item (Shift and Ctrl do not extend
 * the selection). See difference 013 in the project notes.
 * </p>
 */
public class Tree extends Composite {
	TreeItem [] items;
	/* Programmatic changes must not send the events of the user's ones */
	boolean ignoreSelect, ignoreExpand;
	/*
	* The selection is kept by the tree, in the items, and drawn by it: the tree view of the
	* container always has one selected record, which cannot be deselected, and selects one at
	* a time (difference 013). The record that the container selects is only the cursor (the
	* focused item of Windows); its notifications for it are told apart from those of the
	* keyboard by remembering the record that the mouse went down on.
	*/
	int cursorHandle;
	/* Where a range that is selected with Shift starts */
	TreeItem anchor;
	int cnrInfoPtr;

	static final int TreeProc;
	static final PSZ TreeClass =  PSZ.getAtom (OS.WC_CONTAINER);
	static {
		CLASSINFO pclsiClassInfo = new CLASSINFO ();
		OS.WinQueryClassInfo (OS.NULLHANDLE, TreeClass, pclsiClassInfo);
		TreeProc = pclsiClassInfo.pfnWindowProc;
	}

	/*
	* Offsets in a RECORDCORE of the container. The extra bytes asked for
	* with CM_ALLOCRECORD follow it: they hold the id of the TreeItem.
	*/
	static final int REC_FLAGS = 4;
	static final int REC_PSZICON = 20;
	static final int REC_HBMBITMAP = 32;
	static final int REC_PSZTEXT = 44;
	static final int REC_PSZNAME = 48;
	static final int REC_PSZTREE = 52;
	static final int REC_SIZE = 56;

	static final int DEFAULT_WIDTH = 64;
	static final int DEFAULT_HEIGHT = 64;

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
 * </ul>
 *
 * @see SWT#SINGLE
 * @see SWT#MULTI
 * @see SWT#CHECK
 * @see Widget#checkSubclass
 * @see Widget#getStyle
 */
public Tree (Composite parent, int style) {
	super (parent, checkStyle (style));
}

/*
 * Memory of the process, for the records and the strings of the container.
 */
static int peek (int address) {
	byte [] bytes = new byte [4];
	OS.objcpy (bytes, address);
	return (bytes [0] & 0xFF) | ((bytes [1] & 0xFF) << 8) | ((bytes [2] & 0xFF) << 16) | ((bytes [3] & 0xFF) << 24);
}

static void poke (int address, int value) {
	byte [] bytes = {(byte) value, (byte) (value >> 8), (byte) (value >> 16), (byte) (value >> 24)};
	OS.objcpy (address, bytes);
}

static int allocate (int size) {
	int block = OS.malloc (size);
	if (block == 0) SWT.error (SWT.ERROR_NO_HANDLES);
	OS.memset (block, 0, size);
	return block;
}

static int allocateString (String string) {
	/*
	* String.getBytes() takes about 9 ms for each call in the Java 1.3.1 of OS/2 (it
	* looks the converter up again): ASCII, which is nearly all of it, is converted here.
	*/
	byte [] bytes = new byte [string.length ()];
	for (int i=0; i<bytes.length; i++) {
		char c = string.charAt (i);
		if (c >= 0x80) {
			bytes = string.getBytes ();
			break;
		}
		bytes [i] = (byte) c;
	}
	int block = allocate (bytes.length + 1);
	if (bytes.length > 0) OS.objcpy (block, bytes);
	return block;
}

int send (int msg, int mp1, int mp2) {
	return OS.WinSendMsg (handle, msg, mp1, mp2);
}

/*
 * Records of the container: first child, next sibling and parent of a record
 * (0 stands for the root level), and the TreeItem that owns a record.
 */
int query (int hRecord, int flag) {
	int result = send (OS.CM_QUERYRECORD, hRecord, OS.MPFROM2SHORT ((short) flag, (short) OS.CMA_ITEMORDER));
	return result == -1 ? 0 : result;
}

int firstChild (int hParent) {
	return hParent == 0 ? query (0, OS.CMA_FIRST) : query (hParent, OS.CMA_FIRSTCHILD);
}

TreeItem itemOf (int hRecord) {
	if (hRecord == 0 || hRecord == -1 || items == null) return null;
	int id = peek (hRecord + REC_SIZE);
	if (id < 0 || id >= items.length) return null;
	TreeItem item = items [id];
	return item != null && item.handle == hRecord ? item : null;
}

TreeItem [] childrenOf (int hParent) {
	int count = 0;
	for (int h = firstChild (hParent); h != 0; h = query (h, OS.CMA_NEXT)) count++;
	TreeItem [] result = new TreeItem [count];
	int index = 0;
	for (int h = firstChild (hParent); h != 0 && index < count; h = query (h, OS.CMA_NEXT)) {
		result [index++] = itemOf (h);
	}
	return result;
}

/*
 * The record after which a new item with the given index goes.
 */
int insertionPoint (int hParent, int index) {
	if (index == 0) return OS.CMA_FIRST;
	int h = firstChild (hParent);
	for (int i = 1; h != 0 && i < index; i++) h = query (h, OS.CMA_NEXT);
	if (h == 0) error (SWT.ERROR_INVALID_RANGE);
	return h;
}

void invalidateRecord (int hRecord, int flags) {
	int array = allocate (4);
	poke (array, hRecord);
	send (OS.CM_INVALIDATERECORD, array, OS.MPFROM2SHORT ((short) 1, (short) flags));
	OS.free (array);
}

/**
 * Adds the listener to the collection of listeners who will
 * be notified when the receiver's selection changes, by sending
 * it one of the messages defined in the <code>SelectionListener</code>
 * interface.
 * <p>
 * When <code>widgetSelected</code> is called, the item field of the event object is valid.
 * <code>widgetDefaultSelected</code> is called when an item is double-clicked.
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
	addListener (SWT.Selection, typedListener);
	addListener (SWT.DefaultSelection, typedListener);
}

/**
 * Adds the listener to the collection of listeners who will
 * be notified when an item in the receiver is expanded or collapsed
 * by sending it one of the messages defined in the <code>TreeListener</code>
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
 * @see TreeListener
 * @see #removeTreeListener
 */
public void addTreeListener(TreeListener listener) {
	checkWidget ();
	if (listener == null) error (SWT.ERROR_NULL_ARGUMENT);
	TypedListener typedListener = new TypedListener (listener);
	addListener (SWT.Expand, typedListener);
	addListener (SWT.Collapse, typedListener);
}

int callWindowProc (int msg, int mp1, int mp2) {
	if (handle == 0) return 0;
	return OS.WinCallWindowProc (TreeProc, handle, msg, mp1, mp2);
}

static int checkStyle (int style) {
	/*
	* Feature in Windows.  Even when WS_HSCROLL or
	* WS_VSCROLL is not specified, Windows creates
	* trees with scroll bars.  The fix is to set
	* H_SCROLL and V_SCROLL.
	*
	* NOTE: This code appears on all platforms so that
	* applications have consistent scroll bar behavior.
	*/
	style |= SWT.H_SCROLL | SWT.V_SCROLL;
	return checkBits (style, SWT.SINGLE, SWT.MULTI, 0, 0, 0, 0);
}

protected void checkSubclass () {
	if (!isValidSubclass ()) error (SWT.ERROR_INVALID_SUBCLASS);
}

public Point computeSize (int wHint, int hHint, boolean changed) {
	checkWidget ();
	int width = DEFAULT_WIDTH, height = DEFAULT_HEIGHT;
	if (wHint != SWT.DEFAULT) width = wHint;
	if (hHint != SWT.DEFAULT) height = hHint;
	Rectangle trim = computeTrim (0, 0, width, height);
	return new Point (trim.width, trim.height);
}

void createHandle () {
	super.createHandle ();
	state &= ~CANVAS;

	/*
	* The tree view of the container, with the lines and the mini icons. Without
	* CA_DRAWBITMAP it draws icons only, and the images are bitmaps; the icon
	* cell is 32x32 by default, 16x16 makes the rows as high as the text.
	*/
	/*
	* The container keeps the pointer to the structure of CM_SETCNRINFO, it does
	* not copy it: it must live as long as the container does (freeing it, or
	* passing a structure of the stack, made the records of a container that was
	* resized be placed wrongly). It is built in memory of the process.
	*/
	cnrInfoPtr = allocate (92);
	poke (cnrInfoPtr, 92);
	poke (cnrInfoPtr + 20, OS.CV_TREE | OS.CV_ICON | OS.CV_MINI | OS.CA_TREELINE | OS.CA_DRAWBITMAP | OS.CA_OWNERDRAW);
	poke (cnrInfoPtr + 40, 16);
	poke (cnrInfoPtr + 44, 16);
	send (OS.CM_SETCNRINFO, cnrInfoPtr, OS.CMA_FLWINDOWATTR | OS.CMA_SLBITMAPORICON);
}

void createItem (TreeItem item, int hParent, int hInsertAfter) {
	item.foreground = item.background = -1;
	int id = 0;
	while (id < items.length && items [id] != null) id++;
	if (id == items.length) {
		TreeItem [] newItems = new TreeItem [items.length + 4];
		System.arraycopy (items, 0, newItems, 0, items.length);
		items = newItems;
	}

	/* The record, with the id of the item in its extra bytes, and its (empty) text */
	int hItem = send (OS.CM_ALLOCRECORD, 4, 1);
	if (hItem == 0 || hItem == -1) error (SWT.ERROR_NO_HANDLES);
	poke (hItem + REC_SIZE, id);
	item.handle = hItem;
	item.setRecordText (hItem, "");

	/* RECORDINSERT: cb, pRecordOrder, pRecordParent, fInvalidateRecord, zOrder, cRecordsInsert */
	int insert = allocate (24);
	poke (insert, 24);
	poke (insert + 4, hInsertAfter);
	poke (insert + 8, hParent);
	poke (insert + 12, OS.TRUE);
	poke (insert + 16, OS.CMA_TOP);
	poke (insert + 20, 1);
	/*
	* The container selects the first record that is inserted in it, and
	* tells so: it is the cursor, the tree has no selection (see the fields).
	*/
	boolean oldIgnore = ignoreSelect;
	ignoreSelect = true;
	int count = send (OS.CM_INSERTRECORD, hItem, insert);
	OS.free (insert);
	ignoreSelect = oldIgnore;
	if (count != 0 && (peek (hItem + REC_FLAGS) & OS.CRA_SELECTED) != 0) cursorHandle = hItem;
	if (count == 0) {
		item.freeText ();
		item.handle = 0;
		error (SWT.ERROR_ITEM_NOT_ADDED);
	}
	items [id] = item;
}

void createWidget () {
	super.createWidget ();
	items = new TreeItem [4];
}

int defaultBackground () {
	return OS.WinQuerySysColor (super.handle, OS.SYSCLR_WINDOW, 0);
}

/*
 * Removes the record of an item (its children first) from the container.
 */
void destroyItem (TreeItem item) {
	if (item.handle == 0) return;
	TreeItem [] children = childrenOf (item.handle);
	for (int i=0; i<children.length; i++) {
		if (children [i] != null && !children [i].isDisposed ()) children [i].dispose ();
	}
	int array = allocate (4);
	poke (array, item.handle);
	send (OS.CM_REMOVERECORD, array, OS.MPFROM2SHORT ((short) 1, (short) (OS.CMA_FREE | OS.CMA_INVALIDATE)));
	OS.free (array);
	int id = peek (item.handle + REC_SIZE);
	if (0 <= id && id < items.length && items [id] == item) items [id] = null;
	if (cursorHandle == item.handle) cursorHandle = 0;
	if (anchor == item) anchor = null;
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
	setSelected (getSelection (), false);
}

/**
 * Returns the item at the given point in the receiver
 * or null if no such item exists. The point is in the
 * coordinate system of the receiver.
 *
 * @param point the point used to locate the item
 * @return the item at the given point
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the point is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public TreeItem getItem (Point point) {
	checkWidget ();
	if (point == null) error (SWT.ERROR_NULL_ARGUMENT);
	TreeItem [] all = visibleItems ();
	for (int i=0; i<all.length; i++) {
		if (all [i].getBounds ().contains (point)) return all [i];
	}
	return null;
}

/**
 * Returns the number of items contained in the receiver
 * that are direct item children of the receiver.
 *
 * @return the number of items
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public int getItemCount () {
	checkWidget ();
	int count = 0;
	for (int h = firstChild (0); h != 0; h = query (h, OS.CMA_NEXT)) count++;
	return count;
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
 */
public int getItemHeight () {
	checkWidget ();
	TreeItem [] roots = getItems ();
	if (roots.length > 0) {
		int height = roots [0].getBounds ().height;
		if (height > 0) return height;
	}
	/* Nothing to measure: the height of the font, plus the space between rows */
	return getFont ().getFontData () [0].getHeight () + 6;
}

/**
 * Returns a (possibly empty) array of items contained in the
 * receiver that are direct item children of the receiver.  These
 * are the roots of the tree.
 * <p>
 * Note: This is not the actual structure used by the receiver
 * to maintain its list of items, so modifying the array will
 * not affect the receiver.
 * </p>
 *
 * @return the items
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public TreeItem [] getItems () {
	checkWidget ();
	return childrenOf (0);
}

/**
 * Returns the receiver's parent item, which must be a
 * <code>TreeItem</code> or null when the receiver is a
 * root.
 *
 * @return the receiver's parent item
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public TreeItem getParentItem () {
	checkWidget ();
	return null;
}

/**
 * Returns an array of <code>TreeItem</code>s that are currently
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
 */
public TreeItem [] getSelection () {
	checkWidget ();
	java.util.Vector result = new java.util.Vector ();
	collectSelected (0, result);
	TreeItem [] array = new TreeItem [result.size ()];
	result.copyInto (array);
	return array;
}

/* The selected items under a record, in the order of the tree */
void collectSelected (int hParent, java.util.Vector result) {
	for (int h = firstChild (hParent); h != 0; h = query (h, OS.CMA_NEXT)) {
		TreeItem item = itemOf (h);
		if (item == null) continue;
		if (item.selected) result.addElement (item);
		collectSelected (h, result);
	}
}

/*
 * Selects or deselects items and paints them again: the text of a record is drawn
 * by the tree (see wmDrawChild()).
 */
void setSelected (TreeItem [] items, boolean selected) {
	TreeItem [] changed = new TreeItem [items.length];
	int count = 0;
	for (int i=0; i<items.length; i++) {
		if (items [i].selected != selected) {
			items [i].selected = selected;
			changed [count++] = items [i];
		}
	}
	if (count == 0 || handle == 0) return;
	/* Many records are painted again at once */
	if (count > 8) {
		OS.WinInvalidateRect (handle, null, true);
		return;
	}
	for (int i=0; i<count; i++) invalidateRecord (changed [i].handle, OS.CMA_NOREPOSITION);
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
 */
public int getSelectionCount () {
	checkWidget ();
	return getSelection ().length;
}

void releaseWidget () {
	for (int i=0; i<items.length; i++) {
		TreeItem item = items [i];
		if (item != null && !item.isDisposed ()) {
			item.releaseWidget ();
			item.releaseHandle ();
		}
	}
	items = null;
	if (cnrInfoPtr != 0) OS.free (cnrInfoPtr);
	cnrInfoPtr = 0;
	super.releaseWidget ();
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
	/* No records at all, and the container frees the ones it allocated */
	send (OS.CM_REMOVERECORD, 0, OS.MPFROM2SHORT ((short) 0, (short) (OS.CMA_FREE | OS.CMA_INVALIDATE)));
	for (int i=0; i<items.length; i++) {
		TreeItem item = items [i];
		if (item != null && !item.isDisposed ()) {
			item.releaseWidget ();
			item.releaseHandle ();
		}
	}
	items = new TreeItem [4];
	cursorHandle = 0;
	anchor = null;
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
	eventTable.unhook (SWT.DefaultSelection, listener);
}

/**
 * Removes the listener from the collection of listeners who will
 * be notified when items in the receiver are expanded or collapsed.
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
 * @see TreeListener
 * @see #addTreeListener
 */
public void removeTreeListener(TreeListener listener) {
	checkWidget ();
	if (listener == null) error (SWT.ERROR_NULL_ARGUMENT);
	if (eventTable == null) return;
	eventTable.unhook (SWT.Expand, listener);
	eventTable.unhook (SWT.Collapse, listener);
}

/**
 * Selects all the items in the receiver.
 * <p>
 * If the receiver is single-select, do nothing.
 * </p>
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void selectAll () {
	checkWidget ();
	if ((style & SWT.SINGLE) != 0) return;
	int count = 0;
	for (int i=0; i<items.length; i++) if (items [i] != null) count++;
	TreeItem [] all = new TreeItem [count];
	for (int i=0, j=0; i<items.length; i++) if (items [i] != null) all [j++] = items [i];
	setSelected (all, true);
}

/**
 * Display a mark indicating the point at which an item will be inserted.
 * The drop insert item has a visual hint to show where a dragged item
 * will be inserted when dropped on the tree.
 * <p>
 * OS/2 note: it does nothing, the container has no insert mark.
 * </p>
 *
 * @param item the insert item.  Null will clear the insertion mark.
 * @param before true places the insert mark above 'item'. false places the insert mark below 'item'.
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_INVALID_ARGUMENT - if the item has been disposed</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void setInsertMark (TreeItem item, boolean before) {
	checkWidget ();
	if (item != null && item.isDisposed ()) error (SWT.ERROR_INVALID_ARGUMENT);
}

/**
 * Sets the receiver's selection to be the given array of items.
 * The current selected is first cleared, then the new items are selected.
 *
 * @param items the array of items
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the array of items is null</li>
 *    <li>ERROR_INVALID_ARGUMENT - if one of the items has been disposed</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void setSelection (TreeItem [] items) {
	checkWidget ();
	if (items == null) error (SWT.ERROR_NULL_ARGUMENT);
	for (int i=0; i<items.length; i++) {
		if (items [i] == null || items [i].isDisposed ()) error (SWT.ERROR_INVALID_ARGUMENT);
	}
	int count = (style & SWT.SINGLE) != 0 ? Math.min (items.length, 1) : items.length;
	TreeItem [] selection = new TreeItem [count];
	System.arraycopy (items, 0, selection, 0, count);
	deselectAll ();
	setSelected (selection, true);
	if (count == 0) return;
	/* The first one is shown and gets the cursor (the container does not select a record that is not shown) */
	showItem (selection [0]);
	setCursor (selection [0]);
	anchor = selection [0];
}

/* Moves the cursor of the container (its selected record) without reporting it */
void setCursor (TreeItem item) {
	boolean oldIgnore = ignoreSelect;
	ignoreSelect = true;
	send (OS.CM_SETRECORDEMPHASIS, item.handle, OS.MPFROM2SHORT ((short) 1, (short) (OS.CRA_SELECTED | OS.CRA_CURSORED)));
	ignoreSelect = oldIgnore;
	cursorHandle = item.handle;
}

/**
 * Shows the item.  If the item is already showing in the receiver,
 * this method simply returns.  Otherwise, the items are scrolled
 * and expanded until the item is visible.
 *
 * @param item the item to be shown
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the item is null</li>
 *    <li>ERROR_INVALID_ARGUMENT - if the item has been disposed</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @see Tree#showSelection()
 */
public void showItem (TreeItem item) {
	checkWidget ();
	if (item == null) error (SWT.ERROR_NULL_ARGUMENT);
	if (item.isDisposed ()) error (SWT.ERROR_INVALID_ARGUMENT);
	/* Expand the parents, from the root down */
	TreeItem parentItem = item.getParentItem ();
	if (parentItem != null) {
		showItem (parentItem);
		if (!parentItem.getExpanded ()) parentItem.setExpanded (true);
	}
	/* Scroll if the item is out of the visible part of the container */
	Rectangle bounds = item.getBounds ();
	if (bounds.height == 0) return;
	Rectangle area = getClientArea ();
	int delta = 0;
	if (bounds.y < 0) delta = bounds.y;
	else if (bounds.y + bounds.height > area.height) delta = bounds.y + bounds.height - area.height;
	if (delta != 0) {
		/* CM_SCROLLWINDOW: a positive amount shows the records that are lower, as in Table (seen on the screen 2026-11-13) */
		send (OS.CM_SCROLLWINDOW, OS.CMA_VERTICAL, delta);
	}
}

/**
 * Shows the selection.  If the selection is already showing in the receiver,
 * this method simply returns.  Otherwise, the items are scrolled until
 * the selection is visible.
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @see Tree#showItem(TreeItem)
 */
public void showSelection () {
	checkWidget ();
	TreeItem [] selection = getSelection ();
	if (selection.length > 0) showItem (selection [0]);
}

/**
 * If the argument is <code>false</code>, causes the receiver to have
 * all drawing operations ignored until it is called again with
 * the argument <code>true</code>.
 * <p>
 * OS/2 note: the container paints what is added to it even if its updating
 * is disabled (each item, without the images that come after), so it is hidden
 * meanwhile, and shown, with its records placed again, when the drawing is on.
 * </p>
 *
 * @param redraw the new redraw state
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void setRedraw (boolean redraw) {
	checkWidget ();
	if (!redraw && drawCount == 0) OS.WinShowWindow (handle, false);
	super.setRedraw (redraw);
	if (redraw && drawCount == 0) {
		if (items != null && firstChild (0) != 0) {
			send (OS.CM_INVALIDATERECORD, 0, OS.MPFROM2SHORT ((short) 0, (short) OS.CMA_REPOSITION));
		}
		OS.WinShowWindow (handle, true);
	}
}

/*
 * The items that are on the screen (the expanded ones), in order.
 */
TreeItem [] visibleItems () {
	java.util.Vector result = new java.util.Vector ();
	collectVisible (0, result);
	TreeItem [] array = new TreeItem [result.size ()];
	result.copyInto (array);
	return array;
}

void collectVisible (int hParent, java.util.Vector result) {
	for (int h = firstChild (hParent); h != 0; h = query (h, OS.CMA_NEXT)) {
		TreeItem item = itemOf (h);
		if (item == null) continue;
		result.addElement (item);
		if ((peek (h + REC_FLAGS) & OS.CRA_EXPANDED) != 0) collectVisible (h, result);
	}
}

int widgetStyle () {
	/*
	* Scrollable adds SBS_VERT, whose value (1) is CCS_EXTENDSEL in the
	* container: it would be always in extended selection mode.
	*/
	int bits = (super.widgetStyle () & ~OS.SBS_VERT) | OS.CCS_READONLY | OS.CCS_AUTOPOSITION;
	bits |= (style & SWT.MULTI) != 0 ? OS.CCS_EXTENDSEL : OS.CCS_SINGLESEL;
	return bits;
}

PSZ windowClass () {
	return TreeClass;
}

int windowProc () {
	return TreeProc;
}

int windowProc (int msg, int mp1, int mp2) {
	if (msg == OS.WM_BUTTON1DOWN && items != null && handle != 0) {
		/*
		* A click selects the item under the pointer (its icon or its text, not the plus
		* sign). The container selects the record of the row, also from the plus sign: that
		* record becomes the cursor, whose notification is not taken as a move of the keyboard.
		*/
		Point point = new Point ((short) (mp1 & 0xFFFF), getHeight () - 1 - (short) (mp1 >> 16));
		TreeItem item = null, row = null;
		TreeItem [] visible = visibleItems ();
		for (int i=0; i<visible.length && row == null; i++) {
			Rectangle bounds = visible [i].getBounds ();
			if (bounds.y <= point.y && point.y < bounds.y + bounds.height) {
				row = visible [i];
				if (bounds.contains (point)) item = row;
			}
		}
		if (row != null) cursorHandle = row.handle;
		int result = super.windowProc (msg, mp1, mp2);
		if (item != null && !isDisposed () && !item.isDisposed ()) clickItem (item, OS.SHORT2FROMMP (mp2));
		return result;
	}
	int result = super.windowProc (msg, mp1, mp2);
	/*
	* The container does not place its records again when it changes of
	* size: the ones inserted while it had no size are all in the same
	* place, and when it grows they go out of the visible part (they are
	* moved by the old height). It has to be told, once it has taken the
	* new size: no array of records and a count of 0 stand for all of them.
	* Nothing is done for an empty container, and the texts are not measured
	* again (they do not depend on the size), to repaint as little as possible.
	*/
	if (msg == OS.WM_SIZE && items != null && handle != 0 && !isDisposed () && firstChild (0) != 0) {
		send (OS.CM_INVALIDATERECORD, 0, OS.MPFROM2SHORT ((short) 0, (short) OS.CMA_REPOSITION));
	}
	return result;
}

/*
 * Notifications of the container (WM_CONTROL): mp1 = (id, code) and mp2
 * is a pointer to a structure that depends on the code.
 */
/*
 * A click of the user on an item, with the keys that were down: Ctrl adds or removes it,
 * and Shift selects the items between the anchor and it, in a MULTI tree, as on Windows.
 */
void clickItem (TreeItem item, int keys) {
	TreeItem [] old = getSelection ();
	boolean multi = (style & SWT.MULTI) != 0;
	if (multi && (keys & OS.KC_CTRL) != 0) {
		setSelected (new TreeItem [] {item}, !item.selected);
		anchor = item;
	} else if (multi && (keys & OS.KC_SHIFT) != 0 && anchor != null && !anchor.isDisposed ()) {
		TreeItem [] visible = visibleItems ();
		int from = -1, to = -1;
		for (int i=0; i<visible.length; i++) {
			if (visible [i] == anchor) from = i;
			if (visible [i] == item) to = i;
		}
		if (from == -1) from = to;
		TreeItem [] range = new TreeItem [Math.abs (to - from) + 1];
		System.arraycopy (visible, Math.min (from, to), range, 0, range.length);
		setSelected (old, false);
		setSelected (range, true);
	} else {
		setSelected (old, false);
		setSelected (new TreeItem [] {item}, true);
		anchor = item;
	}
	/* A SINGLE tree reports a change only, a MULTI one every click (Windows) */
	if (!multi && old.length == 1 && old [0] == item) return;
	Event event = new Event ();
	event.item = item;
	postEvent (SWT.Selection, event);
}

/*
 * The text of a record (WM_DRAWITEM of the container, CA_OWNERDRAW): with the colors of the
 * highlight when the item is selected. The rest (the icon, the lines, the plus sign and the
 * dotted frame of the cursor) is drawn by the container. OWNERITEM.hItem points to a
 * CNRDRAWITEMINFO: pRecord, pFieldInfo.
 */
MRESULT wmDrawChild (int mp1, int mp2) {
	OWNERITEM struct = new OWNERITEM ();
	OS.objcpy (struct, mp2);
	if (struct.idItem != OS.CMA_TEXT || struct.hItem == 0) return null;
	TreeItem item = itemOf (peek (struct.hItem));
	if (item == null) return null;
	byte [] text = item.getText ().getBytes ();
	int foreground = item.selected ? OS.SYSCLR_HILITEFOREGROUND : OS.SYSCLR_WINDOWTEXT;
	int background = item.selected ? OS.SYSCLR_HILITEBACKGROUND : OS.SYSCLR_WINDOW;
	OS.WinDrawText (struct.hps, text.length, text, struct.rclItem, foreground, background, OS.DT_LEFT | OS.DT_VCENTER | OS.DT_ERASERECT);
	return new MRESULT (OS.TRUE);
}

MRESULT wmControlChild (int mp1, int mp2) {
	switch (OS.SHORT2FROMMP (mp1)) {
		case OS.CN_EMPHASIS: {
			/* NOTIFYRECORDEMPHASIS: hwndCnr, pRecord, fEmphasisMask */
			int hRecord = peek (mp2 + 4);
			int mask = peek (mp2 + 8);
			/*
			* The container sends several notifications for one move of its cursor
			* (for the record that gets it and for the one that loses it). A record
			* that is not the cursor yet was moved to with the keyboard: it is the
			* selection, as on Windows. The clicks are handled in windowProc().
			*/
			if (!ignoreSelect && (mask & OS.CRA_SELECTED) != 0 && hRecord != 0 && hRecord != cursorHandle) {
				TreeItem item = itemOf (hRecord);
				if (item != null && (peek (hRecord + REC_FLAGS) & OS.CRA_SELECTED) != 0) {
					cursorHandle = hRecord;
					anchor = item;
					setSelected (getSelection (), false);
					setSelected (new TreeItem [] {item}, true);
					Event event = new Event ();
					event.item = item;
					postEvent (SWT.Selection, event);
				}
			}
			break;
		}
		case OS.CN_ENTER: {
			/* NOTIFYRECORDENTER: hwndCnr, fKey, pRecord */
			TreeItem item = itemOf (peek (mp2 + 8));
			if (item != null) {
				Event event = new Event ();
				event.item = item;
				postEvent (SWT.DefaultSelection, event);
			}
			break;
		}
		case OS.CN_EXPANDTREE:
		case OS.CN_COLLAPSETREE: {
			/* The notification is the pointer to the record itself */
			TreeItem item = itemOf (mp2);
			if (item != null && !ignoreExpand) {
				Event event = new Event ();
				event.item = item;
				sendEvent (OS.SHORT2FROMMP (mp1) == OS.CN_EXPANDTREE ? SWT.Expand : SWT.Collapse, event);
			}
			break;
		}
	}
	return super.wmControlChild (mp1, mp2);
}

}
