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

/**
 * Instances of this class represent a selectable user interface object
 * that represents an item in a table.
 *
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>(none)</dd>
 * <dt><b>Events:</b></dt>
 * <dd>(none)</dd>
 * </dl>
 * <p>
 * IMPORTANT: This class is <em>not</em> intended to be subclassed.
 * </p>
 * <p>
 * OS/2 note: an item is a record of the container, and the text of each cell
 * a string in native memory pointed to from a slot of the record (one for
 * each column). Only the image of the first column is shown (it is the icon
 * of the record), the others are kept. The table draws the cells with the
 * colors of the item; the check box state is only kept. See the notes of
 * <code>Table</code>.
 * </p>
 */
public class TableItem extends Item {
	Table parent;
	/* The address of the record of the container */
	int handle;
	int background, foreground;
	boolean checked, grayed;
	int imageIndent;
	/* Texts, their strings in native memory and images, by slot of the column */
	String [] cellText = new String [Table.MAX_COLUMNS];
	int [] cellPtr = new int [Table.MAX_COLUMNS];
	Image [] cellImage = new Image [Table.MAX_COLUMNS];

/**
 * Constructs a new instance of this class given its parent
 * (which must be a <code>Table</code>) and a style value
 * describing its behavior and appearance. The item is added
 * to the end of the items maintained by its parent.
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
 * @see SWT
 * @see Widget#checkSubclass
 * @see Widget#getStyle
 */
public TableItem (Table parent, int style) {
	this (parent, style, checkNull (parent).getItemCount ());
}

/**
 * Constructs a new instance of this class given its parent
 * (which must be a <code>Table</code>), a style value
 * describing its behavior and appearance, and the index
 * at which to place it in the items maintained by its parent.
 *
 * @param parent a composite control which will be the parent of the new instance (cannot be null)
 * @param style the style of control to construct
 * @param index the index to store the receiver in its parent
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_INVALID_RANGE - if the index is not between 0 and the number of elements in the parent (inclusive)</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li>
 * </ul>
 *
 * @see SWT
 * @see Widget#checkSubclass
 * @see Widget#getStyle
 */
public TableItem (Table parent, int style, int index) {
	super (checkNull (parent), style);
	this.parent = parent;
	parent.createItem (this, index);
}

static Table checkNull (Table parent) {
	if (parent == null) SWT.error (SWT.ERROR_NULL_ARGUMENT);
	return parent;
}

protected void checkSubclass () {
	if (!isValidSubclass ()) error (SWT.ERROR_INVALID_SUBCLASS);
}

/*
 * The text of a cell goes away with its column.
 */
void clearCell (int slot) {
	if (handle != 0) Tree.poke (handle + Table.REC_SLOTS + 4 * slot, parent.emptyPtr);
	if (cellPtr [slot] != 0) OS.free (cellPtr [slot]);
	cellPtr [slot] = 0;
	cellText [slot] = null;
	cellImage [slot] = null;
}

void destroyWidget () {
	super.destroyWidget ();
	releaseHandle ();
}

/**
 * Returns the receiver's background color.
 *
 * @return the background color
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public Color getBackground () {
	checkWidget ();
	if (background == -1) return parent.getBackground ();
	return Color.pm_new (getDisplay (), background);
}

/**
 * Returns a rectangle describing the receiver's size and location
 * relative to its parent at a column in the table. The rectangle is
 * empty (all zeros) if the item is not shown or the column does not exist.
 *
 * @param index the index that specifies the column
 * @return the receiver's bounding column rectangle
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public Rectangle getBounds (int index) {
	checkWidget ();
	Rectangle row = getRowBounds ();
	if (row.height == 0 || index < 0 || index >= parent.fieldCount ()) return new Rectangle (0, 0, 0, 0);
	return new Rectangle (row.x + parent.offsetOf (index), row.y, parent.widthOf (index), row.height);
}

/*
 * The row of the item: CM_QUERYRECORDRECT gives it in the coordinates of the
 * container, whose y axis grows upwards. All zeros if it is not shown.
 */
Rectangle getRowBounds () {
	/* RECTL: xLeft, yBottom, xRight, yTop. QUERYRECORDRECT: cb, pRecord, fRightSplitWindow, fsExtent */
	int rect = Tree.allocate (16);
	int query = Tree.allocate (16);
	Tree.poke (query, 16);
	Tree.poke (query + 4, handle);
	Tree.poke (query + 12, OS.CMA_ICON | OS.CMA_TEXT);
	Rectangle result = new Rectangle (0, 0, 0, 0);
	parent.flush ();
	if (parent.send (OS.CM_QUERYRECORDRECT, rect, query) != 0) {
		int left = Tree.peek (rect), bottom = Tree.peek (rect + 4);
		int right = Tree.peek (rect + 8), top = Tree.peek (rect + 12);
		result = new Rectangle (left, parent.getHeight () - top, right - left, top - bottom);
	}
	OS.free (query);
	OS.free (rect);
	return result;
}

/**
 * Returns <code>true</code> if the receiver is checked,
 * and false otherwise.  When the parent does not have
 * the <code>CHECK style, return false.
 * <p>
 * OS/2 note: the container has no check boxes: the state is only kept.
 * </p>
 *
 * @return the checked state of the checkbox
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public boolean getChecked () {
	checkWidget ();
	if ((parent.style & SWT.CHECK) == 0) return false;
	return checked;
}

/**
 * Returns the display that the receiver was created on.
 */
public Display getDisplay () {
	Table parent = this.parent;
	if (parent == null) error (SWT.ERROR_WIDGET_DISPOSED);
	return parent.getDisplay ();
}

/**
 * Returns the foreground color that the receiver will use to draw.
 *
 * @return the receiver's foreground color
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public Color getForeground () {
	checkWidget ();
	if (foreground == -1) return parent.getForeground ();
	return Color.pm_new (getDisplay (), foreground);
}

/**
 * Returns <code>true</code> if the receiver is grayed,
 * and false otherwise. When the parent does not have
 * the <code>CHECK style, return false.
 *
 * @return the grayed state of the checkbox
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public boolean getGrayed () {
	checkWidget ();
	if ((parent.style & SWT.CHECK) == 0) return false;
	return grayed;
}

/**
 * Returns the image stored at the given column index in the receiver,
 * or null if the image has not been set or if the column does not exist.
 *
 * @param index the column index
 * @return the image stored at the given column index in the receiver
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public Image getImage (int index) {
	checkWidget ();
	int slot = parent.slotOf (index);
	return slot == -1 ? null : cellImage [slot];
}

public Image getImage () {
	return getImage (0);
}

/**
 * Returns a rectangle describing the size and location
 * relative to its parent of an image at a column in the
 * table.
 * <p>
 * OS/2 note: the image of the first column is drawn in a column of its own,
 * before the first one, and the images of the other columns are not drawn:
 * their rectangle has no width.
 * </p>
 *
 * @param index the index that specifies the column
 * @return the receiver's bounding image rectangle
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public Rectangle getImageBounds (int index) {
	checkWidget ();
	Rectangle cell = getBounds (index);
	Image image = getImage (index);
	/* Only the image of the first column is drawn, in the column of the images before it */
	if (index != 0 || image == null || cell.height == 0 || parent.iconField == 0) return new Rectangle (cell.x, cell.y, 0, cell.height);
	Rectangle size = image.getBounds ();
	int width = Math.min (size.width, Table.ICON_FIELD_WIDTH), height = Math.min (size.height, cell.height);
	Rectangle row = getRowBounds ();
	return new Rectangle (row.x + parent.imageOffset (width), row.y + (row.height - height) / 2, width, height);
}

/**
 * Gets the image indent.
 *
 * @return the indent
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public int getImageIndent () {
	checkWidget ();
	return imageIndent;
}

/**
 * Returns the receiver's parent, which must be a <code>Table</code>.
 *
 * @return the receiver's parent
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public Table getParent () {
	checkWidget ();
	return parent;
}

/**
 * Returns the text stored at the given column index in the receiver,
 * or empty string if the text has not been set.
 *
 * @param index the column index
 * @return the text stored at the given column index in the receiver
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public String getText (int index) {
	checkWidget ();
	int slot = parent.slotOf (index);
	if (slot == -1 || cellText [slot] == null) return "";
	return cellText [slot];
}

public String getText () {
	return getText (0);
}

void releaseChild () {
	super.releaseChild ();
	parent.destroyItem (this);
}

void releaseHandle () {
	handle = 0;
}

void releaseWidget () {
	super.releaseWidget ();
	for (int i=0; i<Table.MAX_COLUMNS; i++) {
		if (cellPtr [i] != 0) OS.free (cellPtr [i]);
		cellPtr [i] = 0;
	}
	cellText = new String [Table.MAX_COLUMNS];
	cellImage = new Image [Table.MAX_COLUMNS];
	parent = null;
}

/**
 * Sets the receiver's background color to the color specified
 * by the argument, or to the default system color for the item
 * if the argument is null.
 *
 * @param color the new color (or null)
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_INVALID_ARGUMENT - if the argument has been disposed</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void setBackground (Color color) {
	checkWidget ();
	if (color != null && color.isDisposed ()) error (SWT.ERROR_INVALID_ARGUMENT);
	int pixel = color == null ? -1 : color.handle;
	if (pixel == background) return;
	background = pixel;
	/* The table draws the cells with the colors of the item */
	if (handle != 0) parent.invalidateRecord (handle, OS.CMA_NOREPOSITION);
}

/**
 * Sets the checked state of the checkbox for this item.  This state change
 * only applies if the Table was created with the SWT.CHECK style.
 *
 * @param checked the new checked state of the checkbox
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void setChecked (boolean checked) {
	checkWidget ();
	if ((parent.style & SWT.CHECK) == 0) return;
	this.checked = checked;
}

/**
 * Sets the receiver's foreground color to the color specified
 * by the argument, or to the default system color for the item
 * if the argument is null.
 *
 * @param color the new color (or null)
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_INVALID_ARGUMENT - if the argument has been disposed</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void setForeground (Color color) {
	checkWidget ();
	if (color != null && color.isDisposed ()) error (SWT.ERROR_INVALID_ARGUMENT);
	int pixel = color == null ? -1 : color.handle;
	if (pixel == foreground) return;
	foreground = pixel;
	if (handle != 0) parent.invalidateRecord (handle, OS.CMA_NOREPOSITION);
}

/**
 * Sets the grayed state of the checkbox for this item.  This state change
 * only applies if the Table was created with the SWT.CHECK style.
 *
 * @param grayed the new grayed state of the checkbox
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void setGrayed (boolean grayed) {
	checkWidget ();
	if ((parent.style & SWT.CHECK) == 0) return;
	this.grayed = grayed;
}

/**
 * Sets the image for multiple columns in the Table.
 *
 * @param images the array of new images
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the array of images is null</li>
 *    <li>ERROR_INVALID_ARGUMENT - if one of the images has been disposed</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void setImage (Image [] images) {
	checkWidget ();
	if (images == null) error (SWT.ERROR_NULL_ARGUMENT);
	for (int i=0; i<images.length; i++) setImage (i, images [i]);
}

/**
 * Sets the receiver's image at a column.
 * <p>
 * OS/2 note: only the image of the first column is shown, as the icon of the
 * record; the other ones are kept.
 * </p>
 *
 * @param index the column index
 * @param image the new image
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_INVALID_ARGUMENT - if the image has been disposed</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void setImage (int index, Image image) {
	checkWidget ();
	if (image != null && image.isDisposed ()) error (SWT.ERROR_INVALID_ARGUMENT);
	int slot = parent.slotOf (index);
	if (slot == -1) return;
	cellImage [slot] = image;
	if (index != 0) return;
	super.setImage (image);
	if (image != null) parent.ensureIconField ();
	/* RECORDCORE: hptrIcon 24, hptrMiniIcon 28, hbmBitmap 32, hbmMiniBitmap 36 */
	int bitmap = 0, icon = 0;
	if (image != null) {
		if (image.type == SWT.ICON) icon = image.handle;
		else bitmap = image.handle;
	}
	Tree.poke (handle + 24, icon);
	Tree.poke (handle + 28, icon);
	Tree.poke (handle + 32, bitmap);
	Tree.poke (handle + 36, bitmap);
	parent.invalidateRecord (handle, OS.CMA_NOREPOSITION);
}

public void setImage (Image image) {
	setImage (0, image);
}

/**
 * Sets the indent of the first column's image.
 * <p>
 * OS/2 note: the indent is only kept, the container centers the image in its
 * column.
 * </p>
 *
 * @param indent the new indent
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void setImageIndent (int indent) {
	checkWidget ();
	if (indent < 0) return;
	imageIndent = indent;
}

/**
 * Sets the text for multiple columns in the table.
 *
 * @param strings the array of new strings
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the text is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void setText (String [] strings) {
	checkWidget ();
	if (strings == null) error (SWT.ERROR_NULL_ARGUMENT);
	for (int i=0; i<strings.length; i++) {
		String string = strings [i];
		if (string != null) setText (i, string);
	}
}

/**
 * Sets the receiver's text at a column
 *
 * @param index the column index
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
public void setText (int index, String string) {
	checkWidget ();
	if (string == null) error (SWT.ERROR_NULL_ARGUMENT);
	int slot = parent.slotOf (index);
	if (slot == -1) return;
	/* The container keeps the pointer: the new string is in place before the old one is freed */
	int ptr = Tree.allocateString (string);
	Tree.poke (handle + Table.REC_SLOTS + 4 * slot, ptr);
	if (cellPtr [slot] != 0) OS.free (cellPtr [slot]);
	cellPtr [slot] = ptr;
	cellText [slot] = string;
	if (index == 0) super.setText (string);
	parent.invalidateRecord (handle, OS.CMA_TEXTCHANGED);
}

public void setText (String string) {
	setText (0, string);
}

}
