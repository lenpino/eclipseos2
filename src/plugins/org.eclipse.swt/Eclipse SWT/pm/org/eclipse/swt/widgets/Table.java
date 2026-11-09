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
 * Instances of this class implement a selectable user interface
 * object that displays a list of images and strings and issues
 * notification when selected.
 * <p>
 * The item children that may be added to instances of this class
 * must be of type <code>TableItem</code>.
 * </p><p>
 * Note that although this class is a subclass of <code>Composite</code>,
 * it does not make sense to add <code>Control</code> children to it,
 * or set a layout on it.
 * </p><p>
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>SINGLE, MULTI, CHECK, FULL_SELECTION, HIDE_SELECTION</dd>
 * <dt><b>Events:</b></dt>
 * <dd>Selection, DefaultSelection</dd>
 * </dl>
 * </p><p>
 * Note: Only one of the styles SINGLE, and MULTI may be specified.
 * </p><p>
 * IMPORTANT: This class is <em>not</em> intended to be subclassed.
 * </p>
 * <p>
 * OS/2 note: this is the container control of PM in its detail view. Every
 * column is a <code>FIELDINFO</code> and every item a record, whose cells are
 * pointers to strings in a slot of the record: all of it lives in native
 * memory and is handled through addresses, as in <code>Tree</code>. There are
 * at most 32 columns. See difference 014 in the project notes.
 * </p>
 */
public class Table extends Composite {
	/* Items by id (the id is stored in the extra bytes of the record), and in the order they are shown */
	TableItem [] items;
	java.util.Vector order;
	/* The columns in the order they are shown. An implicit one is used while there are none. */
	java.util.Vector columns;
	Field implicit;
	boolean [] slotUsed;
	boolean linesVisible, headerVisible;
	/* Programmatic changes do not send the events of the user's ones */
	boolean ignoreSelect;
	/* The selection that was last reported, to report only a change */
	int selectionKey;
	/* The container keeps the pointer to the structure of CM_SETCNRINFO: it has to live as long as the table */
	int cnrInfoPtr;
	/* A string with nothing: what the cells point to until they have a text (the container does not take a null pointer) */
	int emptyPtr;

	static final int TableProc;
	static final PSZ TableClass = PSZ.getAtom (OS.WC_CONTAINER);
	static {
		CLASSINFO pclsiClassInfo = new CLASSINFO ();
		OS.WinQueryClassInfo (OS.NULLHANDLE, TableClass, pclsiClassInfo);
		TableProc = pclsiClassInfo.pfnWindowProc;
	}

	/*
	* The extra bytes of a record: the id of the item, and then a pointer to the
	* text of the cell of each column (the slot of the column).
	*/
	static final int MAX_COLUMNS = 32;
	static final int REC_ID = Tree.REC_SIZE;
	static final int REC_SLOTS = Tree.REC_SIZE + 4;
	static final int REC_EXTRA = 4 + 4 * MAX_COLUMNS;

	/* FIELDINFO: cb, flData, flTitle, pTitleData, offStruct, pUserData, pNextFieldInfo, cxWidth */
	static final int FI_FLDATA = 4;
	static final int FI_FLTITLE = 8;
	static final int FI_TITLE = 12;
	static final int FI_OFFSTRUCT = 16;
	static final int FI_NEXT = 24;
	static final int FI_CXWIDTH = 28;
	static final int FI_SIZE = 32;

	static final int DEFAULT_WIDTH = 64;
	static final int DEFAULT_HEIGHT = 64;
	static final int DEFAULT_COLUMN_WIDTH = 100;

	/* A column of the container */
	static final class Field {
		int handle;
		int slot;
		int titlePtr;
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
 * </ul>
 *
 * @see SWT#SINGLE
 * @see SWT#MULTI
 * @see SWT#CHECK
 * @see SWT#FULL_SELECTION
 * @see SWT#HIDE_SELECTION
 * @see Widget#checkSubclass
 * @see Widget#getStyle
 */
public Table (Composite parent, int style) {
	super (parent, checkStyle (style));
}

int send (int msg, int mp1, int mp2) {
	return OS.WinSendMsg (handle, msg, mp1, mp2);
}

/*
 * Slots of the cells in the record (one for every column).
 */
int allocateSlot () {
	for (int i=0; i<MAX_COLUMNS; i++) {
		if (!slotUsed [i]) {
			slotUsed [i] = true;
			return i;
		}
	}
	SWT.error (SWT.ERROR_NO_HANDLES);
	return -1;
}

/*
 * The slot of the cells of the column with the given index, or -1.
 */
int slotOf (int index) {
	if (columns.size () == 0) return index == 0 && implicit != null ? implicit.slot : -1;
	if (index < 0 || index >= columns.size ()) return -1;
	return ((TableColumn) columns.elementAt (index)).field.slot;
}

int fieldCount () {
	return columns.size () == 0 ? 1 : columns.size ();
}

/**
 * Adds the listener to the collection of listeners who will
 * be notified when the receiver's selection changes, by sending
 * it one of the messages defined in the <code>SelectionListener</code>
 * interface.
 * <p>
 * When <code>widgetSelected</code> is called, the item field of the event object is valid.
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
public void addSelectionListener (SelectionListener listener) {
	checkWidget ();
	if (listener == null) error (SWT.ERROR_NULL_ARGUMENT);
	TypedListener typedListener = new TypedListener (listener);
	addListener (SWT.Selection, typedListener);
	addListener (SWT.DefaultSelection, typedListener);
}

int callWindowProc (int msg, int mp1, int mp2) {
	if (handle == 0) return 0;
	return OS.WinCallWindowProc (TableProc, handle, msg, mp1, mp2);
}

static int checkStyle (int style) {
	/*
	* To be compatible with Windows, force the H_SCROLL
	* and V_SCROLL style bits.  On Windows, it is not
	* possible to create a table without scroll bars.
	*/
	style |= SWT.H_SCROLL | SWT.V_SCROLL;
	return checkBits (style, SWT.SINGLE, SWT.MULTI, 0, 0, 0, 0);
}

protected void checkSubclass () {
	if (!isValidSubclass ()) error (SWT.ERROR_INVALID_SUBCLASS);
}

public Point computeSize (int wHint, int hHint, boolean changed) {
	checkWidget ();
	int width = 0;
	for (int i=0; i<columns.size (); i++) width += ((TableColumn) columns.elementAt (i)).getWidth ();
	int height = getHeaderHeight () + order.size () * getItemHeight ();
	if (width == 0) width = DEFAULT_WIDTH;
	if (height == 0) height = DEFAULT_HEIGHT;
	if (wHint != SWT.DEFAULT) width = wHint;
	if (hHint != SWT.DEFAULT) height = hHint;
	Rectangle trim = computeTrim (0, 0, width, height);
	return new Point (trim.width, trim.height);
}

void createColumn (TableColumn column, int index) {
	int count = columns.size ();
	if (!(0 <= index && index <= count)) error (SWT.ERROR_INVALID_RANGE);
	if (count == 0 && implicit != null) {
		/* The first column takes the place of the implicit one, with the texts of the items */
		column.field = implicit;
		implicit = null;
		setFieldWidth (column.field, DEFAULT_COLUMN_WIDTH);
		columns.addElement (column);
		return;
	}
	int slot = allocateSlot ();
	int hAfter = index == 0 ? OS.CMA_FIRST : ((TableColumn) columns.elementAt (index - 1)).field.handle;
	column.field = newField (slot, DEFAULT_COLUMN_WIDTH, "", hAfter);
	columns.insertElementAt (column, index);
	updateColumnWidth ();
}

void createHandle () {
	super.createHandle ();
	state &= ~CANVAS;

	/*
	* The detail view of the container, with the titles of the columns and the
	* mini icons. Without CA_DRAWBITMAP it draws icons only, and the images are
	* bitmaps. The structure has to live as long as the container (see above).
	*/
	emptyPtr = Tree.allocate (1);
	cnrInfoPtr = Tree.allocate (92);
	Tree.poke (cnrInfoPtr, 92);
	Tree.poke (cnrInfoPtr + 20, containerAttributes ());
	Tree.poke (cnrInfoPtr + 40, 16);
	Tree.poke (cnrInfoPtr + 44, 16);
	send (OS.CM_SETCNRINFO, cnrInfoPtr, OS.CMA_FLWINDOWATTR | OS.CMA_SLBITMAPORICON);

	/* The column that is there until the first one is created */
	implicit = newField (allocateSlot (), DEFAULT_COLUMN_WIDTH, "", OS.CMA_END);
}

int containerAttributes () {
	int attr = OS.CV_DETAIL | OS.CV_MINI | OS.CA_DRAWBITMAP;
	if (headerVisible) attr |= OS.CA_DETAILSVIEWTITLES;
	return attr;
}

void createItem (TableItem item, int index) {
	if (!(0 <= index && index <= order.size ())) error (SWT.ERROR_INVALID_RANGE);
	item.foreground = item.background = -1;
	int id = 0;
	while (id < items.length && items [id] != null) id++;
	if (id == items.length) {
		TableItem [] newItems = new TableItem [items.length + 4];
		System.arraycopy (items, 0, newItems, 0, items.length);
		items = newItems;
	}

	/* The record, with the id of the item and the slots of the cells in its extra bytes */
	int hItem = send (OS.CM_ALLOCRECORD, REC_EXTRA, 1);
	if (hItem == 0 || hItem == -1) error (SWT.ERROR_NO_HANDLES);
	Tree.poke (hItem + REC_ID, id);
	item.handle = hItem;
	/* Every cell points to an empty string before the record is inserted: it is painted at once */
	byte [] cells = new byte [4 * MAX_COLUMNS];
	for (int i=0; i<MAX_COLUMNS; i++) {
		cells [4 * i] = (byte) emptyPtr;
		cells [4 * i + 1] = (byte) (emptyPtr >> 8);
		cells [4 * i + 2] = (byte) (emptyPtr >> 16);
		cells [4 * i + 3] = (byte) (emptyPtr >> 24);
	}
	OS.objcpy (hItem + REC_SLOTS, cells);

	/* RECORDINSERT: cb, pRecordOrder, pRecordParent, fInvalidateRecord, zOrder, cRecordsInsert */
	int hAfter = index == order.size () ? OS.CMA_END : (index == 0 ? OS.CMA_FIRST : ((TableItem) order.elementAt (index - 1)).handle);
	int insert = Tree.allocate (24);
	Tree.poke (insert, 24);
	Tree.poke (insert + 4, hAfter);
	Tree.poke (insert + 12, OS.TRUE);
	Tree.poke (insert + 16, OS.CMA_TOP);
	Tree.poke (insert + 20, 1);
	boolean oldIgnore = ignoreSelect;
	ignoreSelect = true;
	int count = send (OS.CM_INSERTRECORD, hItem, insert);
	OS.free (insert);
	if (count != 0 && (Tree.peek (hItem + Tree.REC_FLAGS) & OS.CRA_SELECTED) != 0) {
		/* The container selects the first record that is inserted: a new table has no selection */
		send (OS.CM_SETRECORDEMPHASIS, hItem, OS.MPFROM2SHORT ((short) 0, (short) OS.CRA_SELECTED));
	}
	ignoreSelect = oldIgnore;
	if (count == 0) {
		item.handle = 0;
		error (SWT.ERROR_ITEM_NOT_ADDED);
	}
	items [id] = item;
	order.insertElementAt (item, index);
	selectionKey = selectionKey ();
}

void createWidget () {
	/*
	* Before the container is created, which is done by the constructor of the
	* superclass: the initializers of the fields would run after it.
	*/
	items = new TableItem [4];
	order = new java.util.Vector ();
	columns = new java.util.Vector ();
	slotUsed = new boolean [MAX_COLUMNS];
	super.createWidget ();
}

int defaultBackground () {
	return OS.WinQuerySysColor (super.handle, OS.SYSCLR_WINDOW, 0);
}

/**
 * Deselects the items at the given zero-relative indices in the receiver.
 * If the item at the given zero-relative index in the receiver
 * is selected, it is deselected.  If the item at the index
 * was not selected, it remains deselected.  Indices that are out
 * of range and duplicate indices are ignored.
 *
 * @param indices the array of indices for the items to deselect
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the set of indices is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void deselect (int [] indices) {
	checkWidget ();
	if (indices == null) error (SWT.ERROR_NULL_ARGUMENT);
	for (int i=0; i<indices.length; i++) setSelected (indices [i], false);
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
	setSelected (index, false);
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
	for (int i=Math.max (start, 0); i<=end && i<order.size (); i++) setSelected (i, false);
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
	boolean oldIgnore = ignoreSelect;
	ignoreSelect = true;
	for (int i=0; i<order.size (); i++) {
		TableItem item = (TableItem) order.elementAt (i);
		if (isRecordSelected (item.handle)) {
			send (OS.CM_SETRECORDEMPHASIS, item.handle, OS.MPFROM2SHORT ((short) 0, (short) OS.CRA_SELECTED));
		}
	}
	ignoreSelect = oldIgnore;
	selectionKey = selectionKey ();
}

/*
 * Removes the record of an item from the container.
 */
void destroyItem (TableItem item) {
	if (item.handle == 0) return;
	int array = Tree.allocate (4);
	Tree.poke (array, item.handle);
	send (OS.CM_REMOVERECORD, array, OS.MPFROM2SHORT ((short) 1, (short) (OS.CMA_FREE | OS.CMA_INVALIDATE)));
	OS.free (array);
	int id = Tree.peek (item.handle + REC_ID);
	if (0 <= id && id < items.length && items [id] == item) items [id] = null;
	order.removeElement (item);
	selectionKey = selectionKey ();
}

/*
 * A column of the container (a FIELDINFO), inserted after another one.
 */
Field newField (int slot, int width, String title, int hAfter) {
	int fi = send (OS.CM_ALLOCDETAILFIELDINFO, 1, 0);
	if (fi == 0 || fi == -1) SWT.error (SWT.ERROR_NO_HANDLES);
	Field field = new Field ();
	field.handle = fi;
	field.slot = slot;
	Tree.poke (fi + FI_FLDATA, fieldData (SWT.LEFT));
	Tree.poke (fi + FI_FLTITLE, OS.CFA_LEFT);
	Tree.poke (fi + FI_OFFSTRUCT, REC_SLOTS + 4 * slot);
	Tree.poke (fi + FI_CXWIDTH, width);
	setFieldTitle (field, title);
	/* FIELDINFOINSERT: cb, pFieldInfoOrder, fInvalidateFieldInfo, cFieldInfoInsert */
	int insert = Tree.allocate (16);
	Tree.poke (insert, 16);
	Tree.poke (insert + 4, hAfter);
	Tree.poke (insert + 8, OS.TRUE);
	Tree.poke (insert + 12, 1);
	int count = send (OS.CM_INSERTDETAILFIELDINFO, fi, insert);
	OS.free (insert);
	if (count == 0) SWT.error (SWT.ERROR_NO_HANDLES);
	return field;
}

int fieldData (int alignment) {
	int bits = OS.CFA_STRING | OS.CFA_FIREADONLY | OS.CFA_VCENTER;
	if ((alignment & SWT.RIGHT) != 0) bits |= OS.CFA_RIGHT;
	else if ((alignment & SWT.CENTER) != 0) bits |= OS.CFA_CENTER;
	else bits |= OS.CFA_LEFT;
	if (linesVisible) bits |= OS.CFA_SEPARATOR | OS.CFA_HORZSEPARATOR;
	return bits;
}

void setFieldTitle (Field field, String title) {
	int ptr = Tree.allocateString (title);
	Tree.poke (field.handle + FI_TITLE, ptr);
	if (field.titlePtr != 0) OS.free (field.titlePtr);
	field.titlePtr = ptr;
}

void setFieldWidth (Field field, int width) {
	Tree.poke (field.handle + FI_CXWIDTH, Math.max (width, 0));
}

void invalidateRecord (int hRecord, int flags) {
	int array = Tree.allocate (4);
	Tree.poke (array, hRecord);
	send (OS.CM_INVALIDATERECORD, array, OS.MPFROM2SHORT ((short) 1, (short) flags));
	OS.free (array);
}

/* The width of the column with the given index (the implicit one, when there are none) */
int widthOf (int index) {
	if (columns.size () == 0) return implicit == null ? 0 : Tree.peek (implicit.handle + FI_CXWIDTH);
	return ((TableColumn) columns.elementAt (index)).getWidth ();
}

void invalidateFields () {
	send (OS.CM_INVALIDATEDETAILFIELDINFO, 0, 0);
}

/*
 * The column of an item that has no columns takes all the width.
 */
void updateColumnWidth () {
	if (implicit == null || handle == 0) return;
	setFieldWidth (implicit, getClientArea ().width);
	invalidateFields ();
}

/*
 * Removes a column from the container, and the texts of its cells.
 */
void destroyColumn (TableColumn column) {
	int index = columns.indexOf (column);
	if (index == -1) return;
	Field field = column.field;
	for (int i=0; i<order.size (); i++) ((TableItem) order.elementAt (i)).clearCell (field.slot);
	int array = Tree.allocate (4);
	Tree.poke (array, field.handle);
	send (OS.CM_REMOVEDETAILFIELDINFO, array, OS.MPFROM2SHORT ((short) 1, (short) (OS.CMA_FREE | OS.CMA_INVALIDATE)));
	OS.free (array);
	if (field.titlePtr != 0) OS.free (field.titlePtr);
	slotUsed [field.slot] = false;
	columns.removeElementAt (index);
	column.field = null;
	if (columns.size () == 0) {
		/* Back to the column of the items without columns */
		implicit = newField (allocateSlot (), getClientArea ().width, "", OS.CMA_END);
	}
}

/*
 * Selection of the records.
 */
boolean isRecordSelected (int hRecord) {
	return (Tree.peek (hRecord + Tree.REC_FLAGS) & OS.CRA_SELECTED) != 0;
}

void setSelected (int index, boolean selected) {
	if (index < 0 || index >= order.size ()) return;
	TableItem item = (TableItem) order.elementAt (index);
	boolean oldIgnore = ignoreSelect;
	ignoreSelect = true;
	if (selected && (style & SWT.SINGLE) != 0) {
		for (int i=0; i<order.size (); i++) {
			TableItem other = (TableItem) order.elementAt (i);
			if (other != item && isRecordSelected (other.handle)) {
				send (OS.CM_SETRECORDEMPHASIS, other.handle, OS.MPFROM2SHORT ((short) 0, (short) OS.CRA_SELECTED));
			}
		}
	}
	int emphasis = OS.CRA_SELECTED | (selected ? OS.CRA_CURSORED : 0);
	send (OS.CM_SETRECORDEMPHASIS, item.handle, OS.MPFROM2SHORT ((short) (selected ? 1 : 0), (short) emphasis));
	ignoreSelect = oldIgnore;
	selectionKey = selectionKey ();
}

/*
 * A number that changes when the selection does: what was last reported.
 */
int selectionKey () {
	int key = 0, count = 0;
	for (int i=0; i<order.size (); i++) {
		TableItem item = (TableItem) order.elementAt (i);
		if (isRecordSelected (item.handle)) {
			key = key * 31 + item.handle;
			count++;
		}
	}
	return key + count;
}

/**
 * Returns the column at the given, zero-relative index in the
 * receiver. Throws an exception if the index is out of range.
 * If no <code>TableColumn</code>s were created by the programmer,
 * this method will throw <code>ERROR_INVALID_RANGE</code> despite
 * the fact that a single column of data may be visible in the table.
 *
 * @param index the index of the column to return
 * @return the column at the given index
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_INVALID_RANGE - if the index is not between 0 and the number of elements in the list minus 1 (inclusive)</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public TableColumn getColumn (int index) {
	checkWidget ();
	if (!(0 <= index && index < columns.size ())) error (SWT.ERROR_INVALID_RANGE);
	return (TableColumn) columns.elementAt (index);
}

/**
 * Returns the number of columns contained in the receiver.
 * If no <code>TableColumn</code>s were created by the programmer,
 * this value is zero, despite the fact that visually, one column
 * of items may be visible. This occurs when the programmer uses
 * the table like a list, adding items but never creating a column.
 *
 * @return the number of columns
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public int getColumnCount () {
	checkWidget ();
	return columns.size ();
}

/**
 * Returns an array of <code>TableColumn</code>s which are the
 * columns in the receiver.  If no <code>TableColumn</code>s were
 * created by the programmer, the array is empty, despite the fact
 * that visually, one column of items may be visible. This occurs
 * when the programmer uses the table like a list, adding items but
 * never creating a column.
 * <p>
 * Note: This is not the actual structure used by the receiver
 * to maintain its list of items, so modifying the array will
 * not affect the receiver.
 * </p>
 *
 * @return the items in the receiver
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public TableColumn [] getColumns () {
	checkWidget ();
	TableColumn [] result = new TableColumn [columns.size ()];
	columns.copyInto (result);
	return result;
}

/**
 * Returns the width in pixels of a grid line.
 *
 * @return the width of a grid line in pixels
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public int getGridLineWidth () {
	checkWidget ();
	return 1;
}

/**
 * Returns the height of the receiver's header
 *
 * @return the height of the header or zero if the header is not visible
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public int getHeaderHeight () {
	checkWidget ();
	if (!headerVisible) return 0;
	/* The visible part of the records starts under the titles (the y axis of PM grows upwards) */
	int rect = Tree.allocate (16);
	int result = send (OS.CM_QUERYVIEWPORTRECT, rect, OS.MPFROM2SHORT ((short) OS.CMA_WINDOW, (short) 0));
	int height = result != 0 ? getHeight () - Tree.peek (rect + 12) : 0;
	OS.free (rect);
	return Math.max (height, 0);
}

/**
 * Returns <code>true</code> if the receiver's header is visible,
 * and <code>false</code> otherwise.
 *
 * @return the receiver's header's visibility state
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public boolean getHeaderVisible () {
	checkWidget ();
	return headerVisible;
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
 */
public TableItem getItem (int index) {
	checkWidget ();
	if (!(0 <= index && index < order.size ())) error (SWT.ERROR_INVALID_RANGE);
	return (TableItem) order.elementAt (index);
}

/**
 * Returns the item at the given point in the receiver
 * or null if no such item exists. The point is in the
 * coordinate system of the receiver.
 *
 * @param point the point used to locate the item
 * @return the item that is specified by the point
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the point is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public TableItem getItem (Point point) {
	checkWidget ();
	if (point == null) error (SWT.ERROR_NULL_ARGUMENT);
	int area = getClientArea ().height;
	for (int i=Math.max (getTopIndex (), 0); i<order.size (); i++) {
		TableItem item = (TableItem) order.elementAt (i);
		Rectangle bounds = item.getRowBounds ();
		if (bounds.y > area) break;
		if (bounds.contains (point)) return item;
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
 */
public int getItemCount () {
	checkWidget ();
	return order.size ();
}

/**
 * Returns the height of the area which would be used to
 * display <em>one</em> of the items in the receiver's.
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
	if (order.size () > 0) {
		int height = ((TableItem) order.elementAt (0)).getRowBounds ().height;
		if (height > 0) return height;
	}
	/* Nothing to measure: the height of the font, plus the space between rows */
	return getFont ().getFontData () [0].getHeight () + 6;
}

/**
 * Returns an array of <code>TableItem</code>s which are the items
 * in the receiver.
 * <p>
 * Note: This is not the actual structure used by the receiver
 * to maintain its list of items, so modifying the array will
 * not affect the receiver.
 * </p>
 *
 * @return the items in the receiver
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public TableItem [] getItems () {
	checkWidget ();
	TableItem [] result = new TableItem [order.size ()];
	order.copyInto (result);
	return result;
}

/**
 * Returns <code>true</code> if the receiver's lines are visible,
 * and <code>false</code> otherwise.
 *
 * @return the visibility state of the lines
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public boolean getLinesVisible () {
	checkWidget ();
	return linesVisible;
}

/**
 * Returns an array of <code>TableItem</code>s that are currently
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
public TableItem [] getSelection () {
	checkWidget ();
	int [] indices = getSelectionIndices ();
	TableItem [] result = new TableItem [indices.length];
	for (int i=0; i<indices.length; i++) result [i] = (TableItem) order.elementAt (indices [i]);
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
 */
public int getSelectionCount () {
	checkWidget ();
	return getSelectionIndices ().length;
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
 */
public int getSelectionIndex () {
	checkWidget ();
	int [] indices = getSelectionIndices ();
	return indices.length == 0 ? -1 : indices [0];
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
 */
public int [] getSelectionIndices () {
	checkWidget ();
	int count = 0;
	for (int i=0; i<order.size (); i++) {
		if (isRecordSelected (((TableItem) order.elementAt (i)).handle)) count++;
	}
	int [] result = new int [count];
	count = 0;
	for (int i=0; i<order.size (); i++) {
		if (isRecordSelected (((TableItem) order.elementAt (i)).handle)) result [count++] = i;
	}
	return result;
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
	/* The first item whose bottom is under the header: the positions grow with the index */
	int header = getHeaderHeight ();
	int low = 0, high = order.size () - 1, result = 0;
	while (low <= high) {
		int middle = (low + high) / 2;
		Rectangle bounds = ((TableItem) order.elementAt (middle)).getRowBounds ();
		if (bounds.y + bounds.height > header) {
			result = middle;
			high = middle - 1;
		} else {
			low = middle + 1;
		}
	}
	return result;
}

/**
 * Searches the receiver's list starting at the first column
 * (index 0) until a column is found that is equal to the
 * argument, and returns the index of that column. If no column
 * is found, returns -1.
 *
 * @param column the search column
 * @return the index of the column
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the string is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public int indexOf (TableColumn column) {
	checkWidget ();
	if (column == null) error (SWT.ERROR_NULL_ARGUMENT);
	return columns.indexOf (column);
}

/**
 * Searches the receiver's list starting at the first item
 * (index 0) until an item is found that is equal to the
 * argument, and returns the index of that item. If no item
 * is found, returns -1.
 *
 * @param item the search item
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
public int indexOf (TableItem item) {
	checkWidget ();
	if (item == null) error (SWT.ERROR_NULL_ARGUMENT);
	return order.indexOf (item);
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
	if (index < 0 || index >= order.size ()) return false;
	return isRecordSelected (((TableItem) order.elementAt (index)).handle);
}

void releaseWidget () {
	for (int i=0; i<columns.size (); i++) {
		TableColumn column = (TableColumn) columns.elementAt (i);
		if (!column.isDisposed ()) {
			column.releaseWidget ();
			column.releaseHandle ();
		}
	}
	for (int i=0; i<items.length; i++) {
		TableItem item = items [i];
		if (item != null && !item.isDisposed ()) {
			item.releaseWidget ();
			item.releaseHandle ();
		}
	}
	if (implicit != null && implicit.titlePtr != 0) OS.free (implicit.titlePtr);
	implicit = null;
	columns = new java.util.Vector ();
	items = null;
	order = new java.util.Vector ();
	if (cnrInfoPtr != 0) OS.free (cnrInfoPtr);
	cnrInfoPtr = 0;
	if (emptyPtr != 0) OS.free (emptyPtr);
	emptyPtr = 0;
	super.releaseWidget ();
}

/**
 * Removes the items from the receiver at the given
 * zero-relative indices.
 *
 * @param indices the array of indices of the items
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_INVALID_RANGE - if the index is not between 0 and the number of elements in the list minus 1 (inclusive)</li>
 *    <li>ERROR_NULL_ARGUMENT - if the indices array is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void remove (int [] indices) {
	checkWidget ();
	if (indices == null) error (SWT.ERROR_NULL_ARGUMENT);
	TableItem [] doomed = new TableItem [indices.length];
	for (int i=0; i<indices.length; i++) {
		if (!(0 <= indices [i] && indices [i] < order.size ())) error (SWT.ERROR_INVALID_RANGE);
		doomed [i] = (TableItem) order.elementAt (indices [i]);
	}
	for (int i=0; i<doomed.length; i++) {
		if (!doomed [i].isDisposed ()) doomed [i].dispose ();
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
 */
public void remove (int index) {
	checkWidget ();
	if (!(0 <= index && index < order.size ())) error (SWT.ERROR_INVALID_RANGE);
	((TableItem) order.elementAt (index)).dispose ();
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
 */
public void remove (int start, int end) {
	checkWidget ();
	if (start > end) return;
	if (!(0 <= start && start <= end && end < order.size ())) error (SWT.ERROR_INVALID_RANGE);
	int [] indices = new int [end - start + 1];
	for (int i=0; i<indices.length; i++) indices [i] = start + i;
	remove (indices);
}

/**
 * Removes all of the items from the receiver.
 * <p>
 *
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
		TableItem item = items [i];
		if (item != null && !item.isDisposed ()) {
			item.releaseWidget ();
			item.releaseHandle ();
		}
	}
	items = new TableItem [4];
	order.removeAllElements ();
	selectionKey = 0;
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
	eventTable.unhook (SWT.DefaultSelection, listener);
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
 *    <li>ERROR_NULL_ARGUMENT - if the array of indices is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void select (int [] indices) {
	checkWidget ();
	if (indices == null) error (SWT.ERROR_NULL_ARGUMENT);
	int length = indices.length;
	if (length == 0 || ((style & SWT.SINGLE) != 0 && length > 1)) return;
	for (int i=0; i<length; i++) setSelected (indices [i], true);
}

/**
 * Selects the item at the given zero-relative index in the receiver.
 * If the item at the index was already selected, it remains
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
	setSelected (index, true);
}

/**
 * Selects the items in the range specified by the given zero-relative
 * indices in the receiver. The range of indices is inclusive.
 * The current selection is not cleared before the new items are selected.
 * <p>
 * If an item in the given range is not selected, it is selected.
 * If an item in the given range was already selected, it remains selected.
 * Indices that are out of range are ignored and no items will be selected
 * if start is greater than end.
 * If the receiver is single-select and there is more than one item in the
 * given range, then all indices are ignored.
 * </p>
 *
 * @param start the start of the range
 * @param end the end of the range
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @see Table#setSelection(int,int)
 */
public void select (int start, int end) {
	checkWidget ();
	if (end < 0 || start > end || ((style & SWT.SINGLE) != 0 && start != end)) return;
	for (int i=Math.max (start, 0); i<=end && i<order.size (); i++) setSelected (i, true);
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
	for (int i=0; i<order.size (); i++) setSelected (i, true);
}

/**
 * Sets the receiver's header to be visible if the argument is <code>true</code>,
 * and marks it invisible otherwise.
 *
 * @param show the new visibility state
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void setHeaderVisible (boolean show) {
	checkWidget ();
	headerVisible = show;
	Tree.poke (cnrInfoPtr + 20, containerAttributes ());
	send (OS.CM_SETCNRINFO, cnrInfoPtr, OS.CMA_FLWINDOWATTR);
}

/**
 * Sets the receiver's lines to be visible if the argument is <code>true</code>,
 * and marks it invisible otherwise.
 *
 * @param show the new visibility state
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void setLinesVisible (boolean show) {
	checkWidget ();
	linesVisible = show;
	for (int i=0; i<columns.size (); i++) ((TableColumn) columns.elementAt (i)).updateData ();
	if (implicit != null) Tree.poke (implicit.handle + FI_FLDATA, fieldData (SWT.LEFT));
	invalidateFields ();
}

/**
 * If the argument is <code>false</code>, causes the receiver to have
 * all drawing operations ignored until it is called again with
 * the argument <code>true</code>.
 * <p>
 * OS/2 note: the container paints what is added to it even if its updating
 * is disabled, so it is hidden meanwhile, and shown, with its records placed
 * again, when the drawing is on.
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
		if (order.size () > 0) {
			send (OS.CM_INVALIDATERECORD, 0, OS.MPFROM2SHORT ((short) 0, (short) OS.CMA_REPOSITION));
		}
		OS.WinShowWindow (handle, true);
	}
}

/**
 * Selects the items at the given zero-relative indices in the receiver.
 * The current selection is first cleared, then the new items are selected.
 *
 * @param indices the indices of the items to select
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the array of indices is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @see Table#deselectAll()
 * @see Table#select(int[])
 */
public void setSelection (int [] indices) {
	checkWidget ();
	if (indices == null) error (SWT.ERROR_NULL_ARGUMENT);
	deselectAll ();
	int length = indices.length;
	if (length == 0 || ((style & SWT.SINGLE) != 0 && length > 1)) return;
	select (indices);
	showSelection ();
}

/**
 * Sets the receiver's selection to be the given array of items.
 * The current selected is first cleared, then the new items are
 * selected.
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
 *
 * @see Table#deselectAll()
 * @see Table#select(int[])
 * @see Table#setSelection(int[])
 */
public void setSelection (TableItem [] items) {
	checkWidget ();
	if (items == null) error (SWT.ERROR_NULL_ARGUMENT);
	int length = items.length;
	for (int i=0; i<length; i++) {
		if (items [i] == null || items [i].isDisposed ()) error (SWT.ERROR_INVALID_ARGUMENT);
	}
	deselectAll ();
	if (length == 0 || ((style & SWT.SINGLE) != 0 && length > 1)) return;
	int [] indices = new int [length];
	for (int i=0; i<length; i++) indices [i] = order.indexOf (items [i]);
	select (indices);
	showSelection ();
}

/**
 * Selects the item at the given zero-relative index in the receiver.
 * The current selection is first cleared, then the new item is selected.
 *
 * @param index the index of the item to select
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @see Table#deselectAll()
 * @see Table#select(int)
 */
public void setSelection (int index) {
	checkWidget ();
	deselectAll ();
	select (index);
	showSelection ();
}

/**
 * Selects the items in the range specified by the given zero-relative
 * indices in the receiver. The range of indices is inclusive.
 * The current selection is cleared before the new items are selected.
 * <p>
 * Indices that are out of range are ignored and no items will be selected
 * if start is greater than end.
 * If the receiver is single-select and there is more than one item in the
 * given range, then all indices are ignored.
 * </p>
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
	deselectAll ();
	if (end < 0 || start > end || ((style & SWT.SINGLE) != 0 && start != end)) return;
	select (Math.max (start, 0), end);
	showSelection ();
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
	if (!(0 <= index && index < order.size ())) return;
	scrollBy (((TableItem) order.elementAt (index)).getRowBounds ().y - getHeaderHeight ());
}

/*
 * Scrolls the records: a positive amount of pixels shows the ones that are lower.
 */
void scrollBy (int delta) {
	if (delta != 0) send (OS.CM_SCROLLWINDOW, OS.CMA_VERTICAL, SCROLL_SIGN * delta);
}

/* The sign of the amount of CM_SCROLLWINDOW for showing the lower records */
static final int SCROLL_SIGN = 1;

/**
 * Shows the item.  If the item is already showing in the receiver,
 * this method simply returns.  Otherwise, the items are scrolled until
 * the item is visible.
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
 * @see Table#showSelection()
 */
public void showItem (TableItem item) {
	checkWidget ();
	if (item == null) error (SWT.ERROR_NULL_ARGUMENT);
	if (item.isDisposed ()) error (SWT.ERROR_INVALID_ARGUMENT);
	Rectangle bounds = item.getRowBounds ();
	if (bounds.height == 0) return;
	int header = getHeaderHeight ();
	int bottom = getClientArea ().height;
	if (bounds.y < header) scrollBy (bounds.y - header);
	else if (bounds.y + bounds.height > bottom) scrollBy (bounds.y + bounds.height - bottom);
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
 * @see Table#showItem(TableItem)
 */
public void showSelection () {
	checkWidget ();
	int index = getSelectionIndex ();
	if (index != -1) showItem ((TableItem) order.elementAt (index));
}

int widgetStyle () {
	/* Scrollable adds SBS_VERT, which is CCS_EXTENDSEL in the container: see Tree */
	int bits = (super.widgetStyle () & ~OS.SBS_VERT) | OS.CCS_READONLY;
	/*
	* The extended selection: a click replaces the selection, Ctrl and Shift extend it, and the
	* selection can be empty and be set by program (the single selection mode of the container
	* cannot be empty). A SINGLE table is kept to one item in wmControlChild().
	*/
	bits |= OS.CCS_EXTENDSEL;
	return bits;
}

PSZ windowClass () {
	return TableClass;
}

int windowProc () {
	return TableProc;
}

/*
 * A message that the table posts to itself to place the records again once a resize that
 * was deferred (a layout) has been done.
 */
static final int WM_REPOSITION = OS.WM_USER + 0x321;

int windowProc (int msg, int mp1, int mp2) {
	if (msg == WM_REPOSITION) {
		reposition ();
		return 0;
	}
	int result = super.windowProc (msg, mp1, mp2);
	if (msg == OS.WM_SIZE && items != null && handle != 0 && !isDisposed ()) {
		updateColumnWidth ();
		OS.WinPostMsg (handle, WM_REPOSITION, 0, 0);
	}
	return result;
}

/*
 * The container does not place its records again when it changes of size: they go up
 * by the height that it gains, and the viewport does not follow (see Tree). Asking for
 * it from inside WM_SIZE is too early (the container has not taken its new viewport
 * yet), and sending WM_SIZE again there makes it worse: it is done after the resize.
 */
void reposition () {
	if (handle == 0 || order == null || order.size () == 0) return;
	send (OS.CM_INVALIDATERECORD, 0, OS.MPFROM2SHORT ((short) 0, (short) (OS.CMA_REPOSITION | OS.CMA_TEXTCHANGED | OS.CMA_ERASE)));
}

void setBounds (int x, int y, int width, int height, int flags) {
	super.setBounds (x, y, width, height, flags);
	if ((flags & OS.SWP_SIZE) != 0 && items != null && !isDisposed ()) reposition ();
}

/*
 * Notifications of the container (WM_CONTROL): mp1 = (id, code) and mp2
 * is a pointer to a structure that depends on the code.
 */
MRESULT wmControlChild (int mp1, int mp2) {
	switch (OS.SHORT2FROMMP (mp1)) {
		case OS.CN_EMPHASIS: {
			/* NOTIFYRECORDEMPHASIS: hwndCnr, pRecord, fEmphasisMask */
			int hRecord = Tree.peek (mp2 + 4);
			int mask = Tree.peek (mp2 + 8);
			if (!ignoreSelect && (mask & OS.CRA_SELECTED) != 0 && hRecord != 0) {
				/* A SINGLE table keeps the record that was clicked */
				if ((style & SWT.SINGLE) != 0 && isRecordSelected (hRecord)) {
					boolean oldIgnore = ignoreSelect;
					ignoreSelect = true;
					for (int i=0; i<order.size (); i++) {
						TableItem other = (TableItem) order.elementAt (i);
						if (other.handle != hRecord && isRecordSelected (other.handle)) {
							send (OS.CM_SETRECORDEMPHASIS, other.handle, OS.MPFROM2SHORT ((short) 0, (short) OS.CRA_SELECTED));
						}
					}
					ignoreSelect = oldIgnore;
				}
				int key = selectionKey ();
				if (key != selectionKey) {
					selectionKey = key;
					TableItem item = itemOf (hRecord);
					if (item == null || !isRecordSelected (hRecord)) {
						TableItem [] selection = getSelection ();
						item = selection.length > 0 ? selection [0] : null;
					}
					Event event = new Event ();
					event.item = item;
					postEvent (SWT.Selection, event);
				}
			}
			break;
		}
		case OS.CN_ENTER: {
			/* NOTIFYRECORDENTER: hwndCnr, fKey, pRecord */
			TableItem item = itemOf (Tree.peek (mp2 + 8));
			if (item != null) {
				Event event = new Event ();
				event.item = item;
				postEvent (SWT.DefaultSelection, event);
			}
			break;
		}
	}
	return super.wmControlChild (mp1, mp2);
}

TableItem itemOf (int hRecord) {
	if (hRecord == 0 || hRecord == -1 || items == null) return null;
	int id = Tree.peek (hRecord + REC_ID);
	if (id < 0 || id >= items.length) return null;
	TableItem item = items [id];
	return item != null && item.handle == hRecord ? item : null;
}

}
