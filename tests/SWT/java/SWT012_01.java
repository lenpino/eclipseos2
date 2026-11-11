/*
 *  SWT012_01.java
 */

/*
 * Copyright (c) 2002, 2026 EclipseOS2 Team.
 * This file is made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 */

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.pm.OS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

/**
 *  Validates the Table, TableItem and TableColumn widgets, built on the detail
 *  view of the PM container control.
 *
 *  The first part is automatic: any failed check throws an exception, so the
 *  testcase is reported as FAILED. The notifications that the container sends
 *  to the user's actions are simulated by sending the same WM_CONTROL message
 *  to the parent. The window is not shown until it is done. The second part is
 *  interactive: use the table, resize the columns, and watch the events printed
 *  to the console and to the text at the bottom.
 *
 *  To close the window automatically (for unattended runs) pass the system
 *  property swt.test.autoclose=<milliseconds> to the JVM.
 */
public class SWT012_01 extends SWTTestCase {

	static {
		STEP = "012";
		TEST = "01";
		DESC = "Table / TableItem / TableColumn";
	}

	int checks;
	long t0 = System.currentTimeMillis();
	Image image;

	Shell createTopShell(Display display) {
		return new Shell(display, SWT.SHELL_TRIM | SWT.NO_REDRAW_RESIZE);
	}

	void check(boolean ok, String what) {
		checks++;
		say((ok ? "  ok    " : "  FAIL  ") + what + (Boolean.getBoolean("swt.test.time") ? "   [" + (System.currentTimeMillis() - t0) + " ms]" : ""));
		if (!ok) throw new RuntimeException("Check failed: " + what);
	}

	/* Deferred events run after a window message is dispatched, see SWT010_01 */
	void pump() {
		OS.WinPostMsg(shell.handle, OS.WM_NULL, 0, 0);
		while (display.readAndDispatch()) {}
	}

	boolean throwsIllegal(Runnable r) {
		try { r.run(); } catch (IllegalArgumentException e) { return true; }
		return false;
	}

	Table newTable(int style) {
		Table table = new Table(shell, style | SWT.BORDER);
		table.setBounds(0, 0, 400, 200);
		return table;
	}

	void checkItemsWithoutColumns() {
		saySubObjective("Items without columns");
		final Table table = newTable(SWT.SINGLE);
		check(table.getItemCount() == 0 && table.getItems().length == 0, "a new table has no items");
		check(table.getColumnCount() == 0 && table.getColumns().length == 0, "and no columns");
		check(!table.getHeaderVisible() && table.getHeaderHeight() == 0, "the header is not visible at first");
		check(!table.getLinesVisible(), "nor are the lines");

		TableItem a = new TableItem(table, SWT.NONE);
		a.setText("Alpha");
		TableItem c = new TableItem(table, SWT.NONE);
		c.setText("Charlie");
		TableItem b = new TableItem(table, SWT.NONE, 1);
		b.setText("Bravo");
		check(table.getItemCount() == 3, "three items");
		check(table.getItem(0) == a && table.getItem(1) == b && table.getItem(2) == c, "the index puts an item between the others");
		check(table.indexOf(b) == 1 && table.indexOf(c) == 2, "indexOf");
		check(a.getText().equals("Alpha") && a.getText(0).equals("Alpha"), "getText of the only column");
		check(a.getText(1).equals(""), "the text of a column that does not exist is empty");
		check(table.getItems()[2] == c, "getItems in order");
		check(a.getParent() == table, "getParent");

		check(throwsIllegal(new Runnable() { public void run() { table.getItem(3); } }), "getItem out of range throws");
		check(throwsIllegal(new Runnable() { public void run() { new TableItem(table, SWT.NONE, 9); } }), "an index out of range throws");
		check(throwsIllegal(new Runnable() { public void run() { new TableItem(table, SWT.NONE, -1); } }), "a negative index throws");
		check(table.getItemCount() == 3, "a failed insertion leaves the table as it was");

		a.setText("");
		check(a.getText().equals(""), "an empty text");
		a.setText("A longer text for the first item, to see that the string is replaced");
		check(a.getText().startsWith("A longer text"), "setText replaces the text");

		Rectangle ra = a.getBounds(0), rb = b.getBounds(0);
		check(ra.width > 0 && ra.height > 0, "an item has a size: " + ra);
		check(rb.y >= ra.y + ra.height, "the second item is below the first one: " + ra + " " + rb);
		check(table.getItemHeight() > 0, "getItemHeight: " + table.getItemHeight());
		check(table.getItem(new Point(rb.x + 3, rb.y + rb.height / 2)) == b, "getItem(Point) finds an item");
		check(table.getItem(new Point(-50, -50)) == null, "getItem(Point) outside gives null");
		table.dispose();
	}

	void checkColumns() {
		saySubObjective("Columns and the text of the cells");
		final Table table = newTable(SWT.SINGLE);
		TableItem item = new TableItem(table, SWT.NONE);
		item.setText("first column");

		TableColumn c0 = new TableColumn(table, SWT.LEFT);
		c0.setText("Name");
		c0.setWidth(120);
		check(table.getColumnCount() == 1 && table.getColumn(0) == c0, "the first column");
		check(item.getText(0).equals("first column"), "the first column takes the text that the items had");
		check(c0.getText().equals("Name") && c0.getWidth() == 120, "the text and the width of a column: " + c0.getWidth());

		TableColumn c1 = new TableColumn(table, SWT.RIGHT);
		c1.setText("Size");
		c1.setWidth(80);
		TableColumn c2 = new TableColumn(table, SWT.CENTER);
		c2.setText("Kind");
		c2.setWidth(60);
		check(table.getColumnCount() == 3, "three columns");
		check(table.indexOf(c0) == 0 && table.indexOf(c1) == 1 && table.indexOf(c2) == 2, "indexOf a column");
		check(c0.getAlignment() == SWT.LEFT && c1.getAlignment() == SWT.RIGHT && c2.getAlignment() == SWT.CENTER, "the alignments");
		c2.setAlignment(SWT.RIGHT);
		check(c2.getAlignment() == SWT.RIGHT, "setAlignment");
		check(c0.getParent() == table, "getParent of a column");

		item.setText(new String[] {"photo.jpg", "12 KB", "image"});
		check(item.getText(0).equals("photo.jpg") && item.getText(1).equals("12 KB") && item.getText(2).equals("image"),
				"the text of every column");
		item.setText(1, "13 KB");
		check(item.getText(1).equals("13 KB") && item.getText(0).equals("photo.jpg"), "setText(index, text) changes one cell only");
		check(item.getText().equals("photo.jpg"), "getText() is the first column");
		check(item.getText(3).equals(""), "the text of a column that does not exist");

		TableColumn first = new TableColumn(table, SWT.LEFT, 0);
		first.setText("Icon");
		first.setWidth(40);
		check(table.getColumn(0) == first && table.getColumn(1) == c0 && table.getColumn(3) == c2, "a column can be inserted by index");
		check(item.getText(0).equals("") && item.getText(1).equals("photo.jpg") && item.getText(2).equals("13 KB"),
				"the texts stay with their columns when another one is put before them");
		TableColumn between = new TableColumn(table, SWT.LEFT, 2);
		check(table.indexOf(between) == 2 && table.getColumnCount() == 5, "a column in the middle");
		check(item.getText(3).equals("13 KB") && item.getText(2).equals(""), "the text keeps with its column: " + item.getText(3));

		check(throwsIllegal(new Runnable() { public void run() { table.getColumn(9); } }), "getColumn out of range throws");
		check(throwsIllegal(new Runnable() { public void run() { new TableColumn(table, SWT.NONE, 9); } }), "a column index out of range throws");

		first.dispose();
		between.dispose();
		check(table.getColumnCount() == 3 && first.isDisposed(), "dispose removes a column");
		check(item.getText(0).equals("photo.jpg") && item.getText(1).equals("13 KB"), "and the others keep their texts");
		c1.dispose();
		check(table.getColumn(1) == c2 && item.getText(1).equals("image"), "the column that follows comes forward with its text");

		c0.setWidth(150);
		check(c0.getWidth() == 150, "setWidth");
		c0.pack();
		check(c0.getWidth() > 0 && c0.getWidth() != 150, "pack gives a width that fits the texts: " + c0.getWidth());

		c0.dispose();
		c2.dispose();
		check(table.getColumnCount() == 0, "all the columns can be removed");
		item.setText("without columns again");
		check(item.getText().equals("without columns again"), "and the items go back to a single column");
		check(new TableItem(table, SWT.NONE).getText().equals(""), "a new item has no text");
		table.dispose();
	}

	void checkHeaderAndLines() {
		saySubObjective("Header and lines");
		Table table = newTable(SWT.SINGLE);
		TableColumn c = new TableColumn(table, SWT.NONE);
		c.setText("Column");
		new TableItem(table, SWT.NONE).setText("x");
		table.setHeaderVisible(true);
		check(table.getHeaderVisible(), "setHeaderVisible(true)");
		int header = table.getHeaderHeight();
		check(header > 0 && header < 60, "the header has a height: " + header);
		Rectangle first = table.getItem(0).getBounds(0);
		check(first.y >= header, "the items start under the header: " + first + " header " + header);
		table.setHeaderVisible(false);
		check(!table.getHeaderVisible() && table.getHeaderHeight() == 0, "setHeaderVisible(false)");
		check(table.getItem(0).getBounds(0).y < first.y, "the items go up when it is hidden");
		table.setLinesVisible(true);
		check(table.getLinesVisible() && table.getGridLineWidth() > 0, "setLinesVisible(true)");
		table.setLinesVisible(false);
		check(!table.getLinesVisible(), "setLinesVisible(false)");
		table.dispose();
	}

	void checkBounds() {
		saySubObjective("Bounds of the cells");
		Table table = newTable(SWT.SINGLE);
		table.setHeaderVisible(true);
		new TableColumn(table, SWT.NONE).setWidth(100);
		new TableColumn(table, SWT.NONE).setWidth(70);
		new TableColumn(table, SWT.NONE).setWidth(50);
		TableItem item = new TableItem(table, SWT.NONE);
		item.setText(new String[] {"a", "b", "c"});
		Rectangle b0 = item.getBounds(0), b1 = item.getBounds(1), b2 = item.getBounds(2);
		check(b0.width == 100 && b1.width == 70 && b2.width == 50, "the width of a cell is the one of its column: " + b0.width + "," + b1.width + "," + b2.width);
		check(b1.x == b0.x + 100 && b2.x == b1.x + 70, "the cells follow one another: " + b0.x + "," + b1.x + "," + b2.x);
		check(b0.y == b1.y && b0.height == b1.height, "in the same row");
		check(item.getBounds(3).width == 0 && item.getBounds(-1).width == 0, "a column that does not exist has no bounds");
		check(table.computeSize(SWT.DEFAULT, SWT.DEFAULT).x >= 220, "computeSize covers the columns: " + table.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		table.dispose();
	}

	void checkSelection() {
		saySubObjective("Selection");
		Table single = newTable(SWT.SINGLE);
		for (int i = 0; i < 6; i++) new TableItem(single, SWT.NONE).setText("Item " + i);
		check(single.getSelectionCount() == 0 && single.getSelectionIndex() == -1 && single.getSelection().length == 0,
				"nothing is selected at first");
		single.select(2);
		check(single.isSelected(2) && single.getSelectionIndex() == 2, "select / isSelected");
		single.select(4);
		check(single.getSelectionCount() == 1 && single.isSelected(4) && !single.isSelected(2), "a single selection table keeps one item");
		single.deselect(4);
		check(single.getSelectionCount() == 0, "deselect: the selection can be empty");
		single.setSelection(1);
		check(single.getSelectionIndices().length == 1 && single.getSelectionIndices()[0] == 1, "setSelection(int)");
		single.setSelection(new int[] {1, 3});
		check(single.getSelectionCount() == 0, "several indices are all ignored in a single selection table, after the selection is cleared");
		single.deselectAll();
		check(single.getSelectionCount() == 0, "deselectAll leaves nothing selected");
		single.selectAll();
		check(single.getSelectionCount() == 0, "selectAll does nothing in a single selection table");
		single.setSelection(new TableItem[] {single.getItem(5)});
		check(single.getSelection()[0] == single.getItem(5), "setSelection(TableItem[]) / getSelection");
		check(!single.isSelected(-1) && !single.isSelected(99), "isSelected out of range is false");
		single.select(99);
		single.select(-3);
		check(single.getSelectionCount() == 1, "select out of range is ignored");
		boolean threw = false;
		try { single.setSelection((int[]) null); } catch (IllegalArgumentException e) { threw = true; }
		check(threw, "setSelection(null) throws");
		single.dispose();

		Table multi = newTable(SWT.MULTI);
		for (int i = 0; i < 8; i++) new TableItem(multi, SWT.NONE).setText("Row " + i);
		check(multi.getSelectionCount() == 0, "nothing is selected at first (MULTI)");
		multi.select(new int[] {1, 3, 5});
		check(multi.getSelectionCount() == 3 && multi.isSelected(1) && multi.isSelected(3) && multi.isSelected(5), "several items can be selected");
		multi.select(6, 7);
		check(multi.getSelectionCount() == 5, "select(start, end) adds to the selection");
		multi.deselect(3);
		multi.deselect(new int[] {1, 5});
		multi.deselect(6, 7);
		check(multi.getSelectionCount() == 0, "deselect, by index, array and range");
		multi.selectAll();
		check(multi.getSelectionCount() == 8, "selectAll selects them all");
		multi.setSelection(2, 4);
		check(multi.getSelectionCount() == 3 && multi.getSelectionIndices()[0] == 2 && multi.getSelectionIndices()[2] == 4, "setSelection(start, end) replaces the selection");
		multi.setSelection(new int[] {7});
		check(multi.getSelectionCount() == 1 && multi.isSelected(7), "setSelection(int[]) replaces it");
		multi.deselectAll();
		check(multi.getSelectionCount() == 0, "deselectAll");
		multi.dispose();
	}

	/** Sends the parent the WM_CONTROL that the container sends for a user action. */
	void notifyParent(Table table, int code, int mp2) {
		int id = OS.WinQueryWindowUShort(table.handle, OS.QWS_ID);
		OS.WinSendMsg(shell.handle, OS.WM_CONTROL, OS.MPFROM2SHORT((short) id, (short) code), mp2);
	}

	int allocate(int size) {
		int block = OS.malloc(size);
		OS.memset(block, 0, size);
		return block;
	}

	void poke(int address, int value) {
		OS.objcpy(address, new byte[] {(byte) value, (byte) (value >> 8), (byte) (value >> 16), (byte) (value >> 24)});
	}

	void checkEvents() {
		saySubObjective("Events (the notifications of the container are simulated)");
		final Table table = newTable(SWT.SINGLE);
		TableItem first = new TableItem(table, SWT.NONE);
		first.setText("one");
		TableItem second = new TableItem(table, SWT.NONE);
		second.setText("two");
		TableItem third = new TableItem(table, SWT.NONE);
		third.setText("three");

		final TableItem[] selected = new TableItem[1], entered = new TableItem[1];
		final int[] counts = new int[2];
		table.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) { selected[0] = (TableItem) e.item; counts[0]++; }
			public void widgetDefaultSelected(SelectionEvent e) { entered[0] = (TableItem) e.item; counts[1]++; }
		});
		table.setSelection(0);
		table.select(1);
		pump();
		check(counts[0] == 0, "select and setSelection send no Selection event");

		/*
		 * A click of the user: the container selects the record (here with the message, not
		 * through Table) and sends the notification. NOTIFYRECORDEMPHASIS: hwndCnr, pRecord, fEmphasisMask.
		 */
		table.deselectAll();
		OS.WinSendMsg(table.handle, OS.CM_SETRECORDEMPHASIS, third_handle(table, 2), OS.MPFROM2SHORT((short) 1, (short) OS.CRA_SELECTED));
		int emphasis = allocate(12);
		poke(emphasis, table.handle);
		poke(emphasis + 4, third_handle(table, 2));
		poke(emphasis + 8, OS.CRA_SELECTED);
		notifyParent(table, OS.CN_EMPHASIS, emphasis);
		pump();
		check(counts[0] == 1 && selected[0] == third, "CN_EMPHASIS of a selected record sends Selection with the item (events: " + counts[0] + ")");
		notifyParent(table, OS.CN_EMPHASIS, emphasis);
		notifyParent(table, OS.CN_EMPHASIS, emphasis);
		pump();
		check(counts[0] == 1, "the same notification again is not reported twice (events: " + counts[0] + ")");

		/* In a single selection table the record that was clicked is the one that stays selected */
		OS.WinSendMsg(table.handle, OS.CM_SETRECORDEMPHASIS, third_handle(table, 0), OS.MPFROM2SHORT((short) 1, (short) OS.CRA_SELECTED));
		poke(emphasis + 4, third_handle(table, 0));
		notifyParent(table, OS.CN_EMPHASIS, emphasis);
		pump();
		check(counts[0] == 2 && selected[0] == first, "a click on another item is a new Selection (events: " + counts[0] + ")");
		check(table.getSelectionCount() == 1 && table.isSelected(0), "and only that item stays selected: " + table.getSelectionCount());

		/* The container may report a record before it marks it as selected: the event still has the item (it was null) */
		table.setSelection(0);
		OS.WinSendMsg(table.handle, OS.CM_SETRECORDEMPHASIS, third_handle(table, 0), OS.MPFROM2SHORT((short) 0, (short) OS.CRA_SELECTED));
		poke(emphasis + 4, third_handle(table, 2));
		notifyParent(table, OS.CN_EMPHASIS, emphasis);
		pump();
		/* The container itself also notifies when the record is deselected here, so only that the item is never null counts */
		check(counts[0] >= 3 && selected[0] != null, "a notification before the record is marked selected still gives an item (events: " + counts[0] + ", item " + (selected[0] == null ? "null" : selected[0].getText()) + ")");
		OS.free(emphasis);

		/*
		 * A whole click of the user, as the container does it: it clears the record that was
		 * selected, selects the new one and moves the cursor to it, with a notification for
		 * each change (sent to the container directly, so that it notifies by itself).
		 * Windows sends one Selection for it, with the item that has the focus.
		 */
		table.setSelection(0);
		pump();
		int before = counts[0];
		OS.WinSendMsg(table.handle, OS.CM_SETRECORDEMPHASIS, third_handle(table, 0), OS.MPFROM2SHORT((short) 0, (short) OS.CRA_SELECTED));
		OS.WinSendMsg(table.handle, OS.CM_SETRECORDEMPHASIS, third_handle(table, 1), OS.MPFROM2SHORT((short) 1, (short) OS.CRA_SELECTED));
		OS.WinSendMsg(table.handle, OS.CM_SETRECORDEMPHASIS, third_handle(table, 1), OS.MPFROM2SHORT((short) 1, (short) OS.CRA_CURSORED));
		pump();
		check(counts[0] - before == 1 && selected[0] == second, "a click is one Selection, with the item clicked (events: " + (counts[0] - before) + ", item " + (selected[0] == null ? "null" : selected[0].getText()) + ")");

		/* NOTIFYRECORDENTER: hwndCnr, fKey, pRecord */
		int enter = allocate(12);
		poke(enter, table.handle);
		poke(enter + 8, third_handle(table, 1));
		notifyParent(table, OS.CN_ENTER, enter);
		pump();
		check(counts[1] == 1 && entered[0] == second, "CN_ENTER sends DefaultSelection with the item");
		OS.free(enter);
		table.dispose();
	}

	int third_handle(Table table, int index) {
		/* The record of an item: TableItem keeps it, and the tests live in the default package */
		try {
			java.lang.reflect.Field f = TableItem.class.getDeclaredField("handle");
			f.setAccessible(true);
			return ((Integer) f.get(table.getItem(index))).intValue();
		} catch (Exception e) {
			throw new RuntimeException(e.toString());
		}
	}

	void checkRemove() {
		saySubObjective("Remove, dispose and removeAll");
		final Table table = newTable(SWT.MULTI);
		TableItem[] items = new TableItem[10];
		for (int i = 0; i < items.length; i++) {
			items[i] = new TableItem(table, SWT.NONE);
			items[i].setText("Item " + i);
		}
		items[2].dispose();
		check(items[2].isDisposed() && table.getItemCount() == 9 && table.getItem(2) == items[3], "dispose removes an item");
		table.remove(0);
		check(table.getItemCount() == 8 && table.getItem(0) == items[1], "remove(index)");
		table.remove(new int[] {0, 3});
		check(table.getItemCount() == 6 && items[1].isDisposed() && items[5].isDisposed(), "remove(int[])");
		table.remove(1, 2);
		check(table.getItemCount() == 4, "remove(start, end): " + table.getItemCount());
		check(throwsIllegal(new Runnable() { public void run() { table.remove(40); } }), "remove out of range throws");
		check(throwsIllegal(new Runnable() { public void run() { table.remove(new int[] {0, 40}); } }), "remove(int[]) out of range throws");
		check(table.getItemCount() == 4, "and removes nothing");
		table.getItem(0).dispose();
		table.setSelection(0);
		check(table.getSelectionCount() == 1, "the table keeps working after removing");

		TableItem[] all = table.getItems();
		table.removeAll();
		check(table.getItemCount() == 0, "removeAll leaves no items");
		boolean gone = true;
		for (int i = 0; i < all.length; i++) gone &= all[i].isDisposed();
		check(gone, "removeAll disposes the items");
		TableItem again = new TableItem(table, SWT.NONE);
		again.setText("Again");
		check(table.getItemCount() == 1 && table.getItem(0) == again, "items can be added after removeAll");

		for (int i = 0; i < 300; i++) new TableItem(table, SWT.NONE).setText("Many " + i);
		check(table.getItemCount() == 301, "many items: " + table.getItemCount());
		table.dispose();
		check(again.isDisposed(), "disposing the table disposes its items");
	}

	void checkScrolling() {
		saySubObjective("Scrolling");
		Table table = newTable(SWT.SINGLE);
		table.setHeaderVisible(true);
		new TableColumn(table, SWT.NONE).setWidth(200);
		for (int i = 0; i < 100; i++) new TableItem(table, SWT.NONE).setText("Row " + i);
		int height = table.getClientArea().height;
		check(table.getTopIndex() == 0, "the top index is 0 at first");
		TableItem last = table.getItem(99);
		check(last.getBounds(0).y >= height, "the last item is not shown: " + last.getBounds(0));
		table.showItem(last);
		Rectangle b = last.getBounds(0);
		check(b.y >= table.getHeaderHeight() && b.y + b.height <= height, "showItem scrolls it into view: " + b + " in " + height);
		check(table.getTopIndex() > 0, "the top index moved: " + table.getTopIndex());
		table.setTopIndex(30);
		check(table.getTopIndex() == 30, "setTopIndex(30): " + table.getTopIndex());
		table.setTopIndex(0);
		check(table.getTopIndex() == 0, "setTopIndex(0): " + table.getTopIndex());
		table.setSelection(70);
		Rectangle s = table.getItem(70).getBounds(0);
		check(s.y >= table.getHeaderHeight() && s.y + s.height <= height, "setSelection shows the item: " + s);
		table.showItem(table.getItem(0));
		check(table.getTopIndex() == 0, "showItem of the first one goes back up");
		table.dispose();
	}

	void checkSizeAndRedraw() {
		saySubObjective("Items added before the table has a size, and with the redrawing off");
		Table a = new Table(shell, SWT.BORDER);
		Table b = new Table(shell, SWT.BORDER);
		a.setHeaderVisible(true);
		b.setHeaderVisible(true);
		TableItem[] x = new TableItem[4], y = new TableItem[4];
		b.setRedraw(false);
		for (int i = 0; i < 4; i++) {
			x[i] = new TableItem(a, SWT.NONE);
			x[i].setText("Row " + i);
			x[i].setImage(image);
			y[i] = new TableItem(b, SWT.NONE);
			y[i].setText("Row " + i);
			y[i].setImage(image);
		}
		b.setRedraw(true);
		a.setBounds(0, 0, 300, 200);
		b.setBounds(0, 0, 300, 200);
		boolean apart = true, inside = true, same = true;
		for (int i = 0; i < 4; i++) {
			Rectangle p = x[i].getBounds(0), q = y[i].getBounds(0);
			if (i > 0) apart &= p.y >= x[i - 1].getBounds(0).y + x[i - 1].getBounds(0).height;
			inside &= p.y >= 0 && p.y + p.height <= 200;
			same &= p.equals(q);
		}
		check(apart, "the items are one below the other after the table gets a size: " + x[0].getBounds(0) + " " + x[3].getBounds(0));
		check(inside, "and they are in the visible part");
		check(same, "the same placement with the redrawing off as with it on");
		Rectangle before = x[0].getBounds(0);
		a.setBounds(0, 0, 350, 300);
		/* Only the width follows the table (the column that has no name takes all of it) */
		check(x[0].getBounds(0).y == before.y && x[0].getBounds(0).height == before.height && x[3].getBounds(0).y + x[3].getBounds(0).height <= 300,
				"the items stay where they were after a bigger size: " + before + " -> " + x[0].getBounds(0));
		check(x[0].getBounds(0).width == 350, "and take the new width: " + x[0].getBounds(0).width);
		a.setBounds(0, 0, 250, 150);
		check(x[0].getBounds(0).y == before.y && x[0].getBounds(0).width == 250, "and after a smaller size: " + x[0].getBounds(0));
		a.dispose();
		b.dispose();
	}

	void checkImagesAndColors() {
		saySubObjective("Images and colors");
		Table table = newTable(SWT.BORDER);
		new TableColumn(table, SWT.NONE).setWidth(120);
		new TableColumn(table, SWT.NONE).setWidth(80);
		TableItem item = new TableItem(table, SWT.NONE);
		item.setText(new String[] {"with image", "other"});
		item.setImage(image);
		check(item.getImage() == image && item.getImage(0) == image, "setImage / getImage");
		check(item.getImage(1) == null, "no image in the other column");
		item.setImage(1, image);
		check(item.getImage(1) == image, "the image of another column is kept");
		check(item.getImageBounds(0).width > 0 && item.getImageBounds(1).width > 0, "the bounds of the images: " + item.getImageBounds(0));
		item.setImageIndent(6);
		check(item.getImageIndent() == 6, "setImageIndent");
		item.setImage((Image) null);
		check(item.getImage() == null, "the image can be removed");
		item.setImage(new Image[] {image, null});
		check(item.getImage(0) == image, "setImage(Image[])");

		check(item.getBackground().equals(table.getBackground()) && item.getForeground().equals(table.getForeground()), "the default colors are those of the table");
		Color red = display.getSystemColor(SWT.COLOR_RED), blue = display.getSystemColor(SWT.COLOR_BLUE);
		item.setBackground(red);
		item.setForeground(blue);
		check(item.getBackground().equals(red) && item.getForeground().equals(blue), "setBackground / setForeground");
		item.setBackground(null);
		check(item.getBackground().equals(table.getBackground()), "null restores the default color");
		check(!item.getChecked() && !item.getGrayed(), "no checked or grayed state without SWT.CHECK");
		table.dispose();
	}

	void initComponents() {
		image = new Image(display, 16, 16);
		GC gc = new GC(image);
		gc.setBackground(display.getSystemColor(SWT.COLOR_GREEN));
		gc.fillRectangle(1, 1, 14, 14);
		gc.dispose();

		/*
		 * The window is not shown during the automatic checks (SWTTestCase shows it after
		 * initComponents): the tables of the checks would appear and go away in it.
		 */
		shell.setSize(520, 340);
		sayObjective("Automatic checks");
		checkItemsWithoutColumns();
		checkColumns();
		checkHeaderAndLines();
		checkBounds();
		checkSelection();
		checkEvents();
		checkRemove();
		checkScrolling();
		checkSizeAndRedraw();
		checkImagesAndColors();
		say("\n" + checks + " checks passed.\n");

		sayObjective("Interactive: click, Ctrl and Shift click, drag the borders of the headers, press Enter");
		shell.setLayout(null);
		final Table table = new Table(shell, SWT.MULTI | SWT.BORDER);
		final Text log = new Text(shell, SWT.BORDER | SWT.MULTI | SWT.READ_ONLY);
		Rectangle area = shell.getClientArea();
		table.setBounds(0, 0, area.width, Math.max(20, area.height - 40));
		log.setBounds(0, Math.max(20, area.height - 40), area.width, 40);
		table.setRedraw(false);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		String[] titles = {"Name", "Size", "Kind"};
		int[] aligns = {SWT.LEFT, SWT.RIGHT, SWT.CENTER};
		int[] widths = {170, 90, 110};
		for (int i = 0; i < titles.length; i++) {
			TableColumn column = new TableColumn(table, aligns[i]);
			column.setText(titles[i]);
			column.setWidth(widths[i]);
		}
		String[] kinds = {"image", "text", "audio", "folder"};
		for (int i = 0; i < 40; i++) {
			TableItem item = new TableItem(table, SWT.NONE);
			item.setText(new String[] {"File " + (i + 1), String.valueOf((i + 1) * 137) + " KB", kinds[i % kinds.length]});
			item.setImage(image);
		}
		table.setRedraw(true);
		table.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String msg = "Selection: " + (e.item == null ? "(none)" : ((TableItem) e.item).getText()) + " (" + table.getSelectionCount() + " selected)";
				say(msg);
				log.setText(msg);
			}
			public void widgetDefaultSelected(SelectionEvent e) {
				String msg = "DefaultSelection: " + (e.item == null ? "(none)" : ((TableItem) e.item).getText());
				say(msg);
				log.setText(msg);
			}
		});
		shell.addListener(SWT.Resize, new org.eclipse.swt.widgets.Listener() {
			public void handleEvent(Event e) {
				Rectangle r = shell.getClientArea();
				table.setBounds(0, 0, r.width, Math.max(20, r.height - 40));
				log.setBounds(0, Math.max(20, r.height - 40), r.width, 40);
			}
		});
		setTitle(null);
	}

	protected void done() {
		if (image != null) image.dispose();
		super.done();
	}

	void setTitle(String title) {
		super.setTitle(title);
	}

	public static void main(String[] args) {
		go(new SWT012_01());
	}

}
