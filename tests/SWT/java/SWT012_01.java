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

	/*
	 * Where the columns are on the screen. The container gives every field more width than
	 * it is told (margins that depend on the font): the separators of the lines are looked
	 * for in the pixels of an empty row, and they must be where the cells end. With the
	 * default font and a big one, and with the column of the images.
	 */
	void checkColumnGeometry() {
		saySubObjective("Columns on the screen");
		Shell window = new Shell(display, SWT.NO_TRIM);
		window.setBounds(10, 10, 600, 200);
		window.open();
		String[] fonts = {null, "Helvetica"};
		for (int f = 0; f < fonts.length; f++) {
			for (int withImage = 0; withImage < 2; withImage++) {
				Table table = new Table(window, SWT.SINGLE | SWT.BORDER);
				table.setBounds(0, 0, 600, 200);
				org.eclipse.swt.graphics.Font font = fonts[f] == null ? null : new org.eclipse.swt.graphics.Font(display, fonts[f], 14, SWT.NORMAL);
				if (font != null) table.setFont(font);
				table.setLinesVisible(true);
				int[] widths = {37, 161, 120};
				TableColumn[] cols = new TableColumn[widths.length];
				for (int i = 0; i < widths.length; i++) {
					cols[i] = new TableColumn(table, SWT.NONE);
					cols[i].setWidth(widths[i]);
				}
				TableItem item = new TableItem(table, SWT.NONE);
				if (withImage == 1) item.setImage(image);
				for (int i = 0; i < 50; i++) display.readAndDispatch();
				String what = (fonts[f] == null ? "default font" : fonts[f] + " 14") + (withImage == 1 ? ", with an image" : "");
				Rectangle row = item.getBounds(0);
				String expected = "";
				int end = 0;
				for (int i = 1; i <= widths.length; i++) {
					end = i < widths.length ? item.getBounds(i).x : item.getBounds(i - 1).x + item.getBounds(i - 1).width;
					expected += end + " ";
				}
				/* Up to a little after the last column: the scroll bar is further */
				String strip = separators(table, row.y + row.height / 2, end + 6);
				String found = "";
				for (int x = 0; x < strip.length(); x++) if (strip.charAt(x) == '|') found += x + " ";
				check(found.equals(expected), "the separators on the screen are where the cells end (" + what + "): found " + found + "expected " + expected);
				boolean same = true;
				for (int i = 0; i < widths.length; i++) same &= cols[i].getWidth() == widths[i] && item.getBounds(i).width == widths[i];
				check(same, "a column is as wide on the screen as its width (" + what + ")");
				table.dispose();
				if (font != null) font.dispose();
			}
		}
		window.dispose();
	}

	/* A row of pixels of a table, with '|' where a separator is (a gray between the white and the black of the texts) */
	String separators(Table table, int y, int width) {
		Point p = table.toDisplay(new Point(0, y));
		int screen = OS.WinQuerySysValue(OS.HWND_DESKTOP, OS.SV_CYSCREEN);
		int hps = OS.WinGetScreenPS(OS.HWND_DESKTOP);
		OS.GpiCreateLogColorTable(hps, 0, OS.LCOLF_RGB, 0, 0, null);
		StringBuffer b = new StringBuffer();
		for (int x = 0; x < width; x++) {
			int pel = OS.GpiQueryPel(hps, new int[] {p.x + x, screen - 1 - p.y});
			int lum = (((pel >> 16) & 0xFF) + ((pel >> 8) & 0xFF) + (pel & 0xFF)) / 3;
			b.append(lum >= 100 && lum < 180 ? '|' : lum < 100 ? '#' : '.');
		}
		OS.WinReleasePS(hps);
		return b.toString();
	}

	/*
	 * The events of the columns. A change of width is a Resize of the column and a Move of
	 * the ones that follow (the Windows header reports both); a click on a title is a
	 * Selection of its column, simulated by posting WM_BUTTON1CLICK to the title window of
	 * the container, as the system does (it goes through the message queue).
	 */
	void checkColumnEvents() {
		saySubObjective("Events of the columns");
		final Table table = newTable(SWT.SINGLE);
		table.setHeaderVisible(true);
		final TableColumn[] cols = new TableColumn[3];
		final int[][] counts = new int[3][3];
		final int RESIZE = 0, MOVE = 1, SELECT = 2;
		for (int i = 0; i < cols.length; i++) {
			final int index = i;
			cols[i] = new TableColumn(table, SWT.NONE);
			cols[i].setText("Column " + i);
			cols[i].setWidth(100);
			cols[i].addListener(SWT.Resize, new org.eclipse.swt.widgets.Listener() {
				public void handleEvent(Event e) { counts[index][RESIZE]++; }
			});
			cols[i].addListener(SWT.Move, new org.eclipse.swt.widgets.Listener() {
				public void handleEvent(Event e) { counts[index][MOVE]++; }
			});
			cols[i].addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) { counts[index][SELECT]++; }
			});
		}
		TableItem item = new TableItem(table, SWT.NONE);
		item.setText(new String[] {"a", "b", "c"});

		cols[1].setWidth(130);
		check(counts[1][RESIZE] == 1 && counts[0][RESIZE] == 0 && counts[2][RESIZE] == 0, "setWidth sends Resize to the column: " + counts[0][RESIZE] + counts[1][RESIZE] + counts[2][RESIZE]);
		check(counts[2][MOVE] == 1 && counts[0][MOVE] == 0 && counts[1][MOVE] == 0, "and Move to the columns that follow: " + counts[0][MOVE] + counts[1][MOVE] + counts[2][MOVE]);
		cols[1].setWidth(130);
		check(counts[1][RESIZE] == 1 && counts[2][MOVE] == 1, "the same width again sends nothing");
		cols[0].setWidth(80);
		check(counts[0][RESIZE] == 1 && counts[1][MOVE] == 1 && counts[2][MOVE] == 2, "a change of the first column moves the other two");
		cols[2].pack();
		check(counts[2][RESIZE] == 1, "pack sends Resize when the width changes: " + cols[2].getWidth());
		cols[0].addListener(SWT.Resize, new org.eclipse.swt.widgets.Listener() {
			public void handleEvent(Event e) { cols[1].dispose(); }
		});
		cols[0].setWidth(90);
		check(cols[1].isDisposed() && counts[1][MOVE] == 1 && counts[2][MOVE] == 3, "a column disposed in the Resize listener gets no Move, the others do");
		cols[2].setWidth(100);
		cols[0].setWidth(100);
		pump();

		/* The title window of the container: a click in the middle of each column selects it */
		int title = OS.WinWindowFromID(table.handle, OS.CID_LEFTCOLTITLEWND);
		check(title != 0, "the container has a window for the titles of the columns");
		int[] counted = {counts[0][SELECT], counts[2][SELECT]};
		check(counted[0] == 0 && counted[1] == 0, "no Selection of a column before a click");
		clickTitle(table, title, item.getBounds(1).x + 50);
		check(counts[2][SELECT] == 0, "the click is posted: nothing until the queue is read");
		pump();
		check(counts[2][SELECT] == 1 && counts[0][SELECT] == 0, "a click on the title of the second column selects it: " + counts[0][SELECT] + "," + counts[2][SELECT]);
		clickTitle(table, title, item.getBounds(0).x + 3);
		pump();
		check(counts[0][SELECT] == 1 && counts[2][SELECT] == 1, "a click near the left of the first title selects the first column");
		clickTitle(table, title, item.getBounds(0).x + 98);
		pump();
		check(counts[0][SELECT] == 2 && counts[2][SELECT] == 1, "a click near its right edge too");
		clickTitle(table, title, item.getBounds(1).x + 2);
		pump();
		check(counts[2][SELECT] == 2, "and one just after the edge selects the next column");
		clickTitle(table, title, item.getBounds(1).x + 150);
		pump();
		check(counts[0][SELECT] == 2 && counts[2][SELECT] == 2, "a click at the right of the last column selects nothing");

		/* With the image column before the others, and scrolled to the right */
		item.setImage(image);
		cols[0].setWidth(250);
		cols[2].setWidth(250);
		Rectangle before = item.getBounds(1);
		OS.WinSendMsg(table.handle, OS.CM_SCROLLWINDOW, OS.CMA_HORIZONTAL, 120);
		Rectangle after = item.getBounds(1);
		check(after.x < before.x, "the table scrolls to the right: " + before.x + " -> " + after.x);
		pump();
		check(item.getBounds(1).x == after.x, "and stays scrolled when the messages that were waiting are read: " + item.getBounds(1).x);
		clickTitle(table, title, after.x + 20);
		pump();
		check(counts[2][SELECT] == 3 && counts[0][SELECT] == 2, "a click on a scrolled title selects its column: " + counts[0][SELECT] + "," + counts[2][SELECT]);
		clickTitle(table, title, after.x - 20);
		pump();
		check(counts[0][SELECT] == 3, "and the one before it the column before");
		table.dispose();
	}

	/* A click posted to the title window at x of the table, in the middle of its height */
	void clickTitle(Table table, int title, int x) {
		int[] point = {x, 0};
		OS.WinMapWindowPoints(table.handle, title, point, 1);
		org.eclipse.swt.internal.pm.RECTL rect = new org.eclipse.swt.internal.pm.RECTL();
		OS.WinQueryWindowRect(title, rect);
		int y = (rect.yBottom + rect.yTop) / 2;
		OS.WinPostMsg(title, OS.WM_BUTTON1CLICK, (point[0] & 0xFFFF) | (y << 16), 0);
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

	/*
	 * The direction of the scrolling, on the screen: only the items 30 and 70 have an
	 * image (green), and after setTopIndex() or showItem() the green must be in the row
	 * that the item should be in. The positions that the container reports are not used.
	 */
	void checkScrollingOnScreen() {
		saySubObjective("Scrolling, seen on the screen");
		Shell window = new Shell(display, SWT.NO_TRIM);
		window.setBounds(10, 10, 300, 200);
		window.open();
		Table table = new Table(window, SWT.SINGLE | SWT.BORDER);
		table.setBounds(0, 0, 300, 200);
		table.setHeaderVisible(true);
		new TableColumn(table, SWT.NONE).setWidth(200);
		for (int i = 0; i < 100; i++) {
			TableItem item = new TableItem(table, SWT.NONE);
			item.setText("Row " + i);
			if (i == 30 || i == 70) item.setImage(image);
		}
		pumpWhile(table);
		int header = table.getHeaderHeight(), row = table.getItemHeight();
		check(greenRows(table, header).equals(""), "at first no green row is shown (" + greenRows(table, header) + ")");
		table.setTopIndex(30);
		pumpWhile(table);
		check(greenRows(table, header).equals("0 "), "setTopIndex(30) shows the item 30 in the first row (green rows: " + greenRows(table, header) + ", row " + row + ")");
		table.setTopIndex(70);
		pumpWhile(table);
		check(greenRows(table, header).equals("0 "), "setTopIndex(70) shows the item 70 in the first row (green rows: " + greenRows(table, header) + ")");
		table.setTopIndex(0);
		pumpWhile(table);
		check(greenRows(table, header).equals(""), "setTopIndex(0) goes back up (green rows: " + greenRows(table, header) + ")");
		table.showItem(table.getItem(30));
		pumpWhile(table);
		int visible = (table.getClientArea().height - header) / row;
		check(greenRows(table, header).equals((visible - 1) + " "), "showItem from above puts the item in the last full row " + (visible - 1) + " (green rows: " + greenRows(table, header) + ")");
		table.setTopIndex(50);
		table.showItem(table.getItem(30));
		pumpWhile(table);
		check(greenRows(table, header).equals("0 "), "showItem from below puts it in the first row (green rows: " + greenRows(table, header) + ")");
		window.dispose();
	}

	void pumpWhile(Table table) {
		pump();
		for (int i = 0; i < 20; i++) display.readAndDispatch();
		table.update();
	}

	/* The rows under the header, counted from 0, with green pixels in the column of the images */
	String greenRows(Table table, int header) {
		int row = table.getItemHeight(), height = table.getClientArea().height;
		Point origin = table.toDisplay(new Point(0, 0));
		int screen = OS.WinQuerySysValue(OS.HWND_DESKTOP, OS.SV_CYSCREEN);
		int hps = OS.WinGetScreenPS(OS.HWND_DESKTOP);
		OS.GpiCreateLogColorTable(hps, 0, OS.LCOLF_RGB, 0, 0, null);
		String rows = "";
		int last = -1;
		for (int y = header; y < height; y++) {
			boolean green = false;
			for (int x = 0; x < 40 && !green; x++) {
				int pel = OS.GpiQueryPel(hps, new int[] {origin.x + x, screen - 1 - (origin.y + y)});
				green = ((pel >> 8) & 0xFF) > 200 && ((pel >> 16) & 0xFF) < 60 && (pel & 0xFF) < 60;
			}
			int index = (y - header) / row;
			if (green && index != last) {
				rows += index + " ";
				last = index;
			}
		}
		OS.WinReleasePS(hps);
		return rows;
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

	/*
	 * Where the image of an item is drawn: in the column of the images, before the first
	 * column (see Table.ensureIconField). The green of the test image (it fills the image
	 * but its border of 1 pixel) is looked for on the screen, with two fonts.
	 */
	void checkImageOnScreen() {
		saySubObjective("Images, seen on the screen");
		Shell window = new Shell(display, SWT.NO_TRIM);
		window.setBounds(10, 10, 400, 200);
		window.open();
		String[] fonts = {null, "Helvetica"};
		for (int f = 0; f < fonts.length; f++) {
			Table table = new Table(window, SWT.SINGLE | SWT.BORDER);
			table.setBounds(0, 0, 400, 200);
			org.eclipse.swt.graphics.Font font = fonts[f] == null ? null : new org.eclipse.swt.graphics.Font(display, fonts[f], 14, SWT.NORMAL);
			if (font != null) table.setFont(font);
			table.setHeaderVisible(true);
			new TableColumn(table, SWT.NONE).setWidth(150);
			new TableColumn(table, SWT.NONE).setWidth(100);
			new TableItem(table, SWT.NONE).setText(new String[] {"first", "x"});
			TableItem item = new TableItem(table, SWT.NONE);
			item.setText(new String[] {"with image", "x"});
			item.setImage(image);
			item.setImage(1, image);
			pump();
			for (int i = 0; i < 20; i++) display.readAndDispatch();
			table.update();
			/* In the column of the images only: the image of the column 1 is green too */
			Rectangle green = greenBox(table, new Rectangle(0, 0, item.getBounds(0).x, table.getClientArea().height));
			Rectangle bounds = item.getImageBounds(0);
			Rectangle expected = green == null ? null : new Rectangle(green.x - 1, green.y - 1, green.width + 2, green.height + 2);
			String what = fonts[f] == null ? "default font" : fonts[f] + " 14";
			check(bounds.equals(expected), "getImageBounds(0) is where the image is drawn (" + what + "): " + bounds + ", on the screen " + expected);
			Rectangle row = item.getBounds(0);
			check(bounds.x + bounds.width <= row.x, "it is before the first column, where the image is drawn: " + bounds + " " + row);
			Rectangle other = item.getImageBounds(1);
			check(other.width == 16 && item.getBounds(1).contains(other.x, other.y), "the image of another column is in its cell (" + other + ")");
			table.dispose();
			if (font != null) font.dispose();
		}
		window.dispose();
	}

	/*
	 * The cells are drawn by the table (owner draw): the text must be where the container put
	 * it (the dark pixels of the texts below were measured on the container itself, before,
	 * with these widths and two fonts), with the colors of the item, and the highlight on the
	 * cells of a selected row.
	 */
	void checkCellsOnScreen() {
		saySubObjective("Cells and colors, seen on the screen");
		Shell window = new Shell(display, SWT.NO_TRIM);
		window.setBounds(10, 10, 400, 200);
		window.open();
		String[] fonts = {null, "Helvetica"};
		String[] expected = {"9..37 63..99 42..65", "12..60 30..97 34..75"};
		for (int f = 0; f < fonts.length; f++) {
			Table table = new Table(window, SWT.MULTI | SWT.BORDER);
			table.setBounds(0, 0, 400, 200);
			org.eclipse.swt.graphics.Font font = fonts[f] == null ? null : new org.eclipse.swt.graphics.Font(display, fonts[f], 14, SWT.NORMAL);
			if (font != null) table.setFont(font);
			table.setHeaderVisible(true);
			int[] aligns = {SWT.LEFT, SWT.RIGHT, SWT.CENTER};
			for (int c = 0; c < 3; c++) {
				TableColumn column = new TableColumn(table, aligns[c]);
				column.setText("Col " + c);
				column.setWidth(110);
			}
			TableItem[] items = new TableItem[4];
			for (int i = 0; i < items.length; i++) {
				items[i] = new TableItem(table, SWT.NONE);
				items[i].setText(new String[] {"Item " + i, "WWW " + i, "xyz " + i});
			}
			items[2].setImage(image);
			settleTable(table);
			String what = fonts[f] == null ? "default font" : fonts[f] + " 14";
			String place = darkSpan(table, items[0], 0) + " " + darkSpan(table, items[0], 1) + " " + darkSpan(table, items[0], 2);
			check(place.equals(expected[f]), "the texts are where the container put them (" + what + "): " + place + ", the container: " + expected[f]);
			Color red = new Color(display, 255, 0, 0), yellow = new Color(display, 255, 255, 0);
			items[1].setForeground(red);
			items[2].setBackground(yellow);
			items[3].setForeground(red);
			table.setSelection(new int[] {3});
			settleTable(table);
			int hilite = OS.WinQuerySysColor(OS.HWND_DESKTOP, OS.SYSCLR_HILITEBACKGROUND, 0) & 0xFFFFFF;
			String all = "";
			for (int i = 0; i < items.length; i++) all += i + ":" + cellColors(table, items[i], hilite) + " ";
			check(cellColors(table, items[0], hilite).equals("dark white"), "a row without colors (" + what + "): " + all);
			check(cellColors(table, items[1], hilite).equals("red white"), "the foreground in every cell: " + all);
			check(cellColors(table, items[2], hilite).equals("dark yellow"), "the background fills the cells: " + all);
			check(cellColors(table, items[3], hilite).equals("white hilite"), "a selected row has the highlight, not its colors: " + all);
			Rectangle icon = items[2].getImageBounds(0);
			check(pel(table, icon.x + icon.width / 2, icon.y + icon.height / 2) == 0x00FF00, "the image is still drawn by the container");
			items[1].setForeground(null);
			table.deselectAll();
			settleTable(table);
			check(cellColors(table, items[1], hilite).equals("dark white") && cellColors(table, items[3], hilite).equals("red white"),
					"null gives the color of the table again, and a deselected row its own: " + cellColors(table, items[1], hilite) + ", " + cellColors(table, items[3], hilite));
			table.dispose();
			red.dispose();
			yellow.dispose();
			if (font != null) font.dispose();
		}
		window.dispose();
	}

	/*
	 * The images of the other columns, drawn by the table at the start of the cell with the
	 * text after them: the green of the test image (all but its border of 1 pixel) is looked
	 * for in each cell and must be where getImageBounds() says, and the text right of it.
	 */
	void checkColumnImagesOnScreen() {
		saySubObjective("Images of the other columns, seen on the screen");
		Shell window = new Shell(display, SWT.NO_TRIM);
		window.setBounds(10, 10, 400, 200);
		window.open();
		String[] fonts = {null, "Helvetica"};
		for (int f = 0; f < fonts.length; f++) {
			Table table = new Table(window, SWT.MULTI | SWT.BORDER);
			table.setBounds(0, 0, 400, 200);
			org.eclipse.swt.graphics.Font font = fonts[f] == null ? null : new org.eclipse.swt.graphics.Font(display, fonts[f], 14, SWT.NORMAL);
			if (font != null) table.setFont(font);
			int[] aligns = {SWT.LEFT, SWT.LEFT, SWT.RIGHT};
			for (int c = 0; c < 3; c++) new TableColumn(table, aligns[c]).setWidth(110);
			TableItem plain = new TableItem(table, SWT.NONE);
			plain.setText(new String[] {"no image", "text", "text"});
			TableItem item = new TableItem(table, SWT.NONE);
			item.setText(new String[] {"first", "second", "third"});
			item.setImage(new Image[] {null, image, image});
			/* A row as high as the text (15 pixels with the default font) cuts the image: first that */
			settleTable(table);
			Rectangle low = item.getImageBounds(1), lowGreen = greenBox(table, item.getBounds(1)), lowRow = item.getBounds(1);
			check(lowGreen != null && low.contains(lowGreen.x, lowGreen.y) && low.contains(lowGreen.x + lowGreen.width - 1, lowGreen.y + lowGreen.height - 1)
					&& lowRow.contains(low.x, low.y) && low.y + low.height <= lowRow.y + lowRow.height,
					"an image in a low row stays in getImageBounds() and in the row: " + low + ", green " + lowGreen + ", row " + lowRow);
			/* With an image in the first column the rows are as high as the images (the column of the images) */
			TableItem other = new TableItem(table, SWT.NONE);
			other.setImage(image);
			table.select(1);
			table.deselectAll();
			settleTable(table);
			String what = fonts[f] == null ? "default font" : fonts[f] + " 14";
			for (int c = 1; c < 3; c++) {
				Rectangle cell = item.getBounds(c);
				Rectangle green = greenBox(table, cell);
				Rectangle expected = green == null ? null : new Rectangle(green.x - 1, green.y - 1, green.width + 2, green.height + 2);
				Rectangle bounds = item.getImageBounds(c);
				check(bounds.equals(expected), "the image of the column " + c + " is drawn where getImageBounds() says (" + what + "): " + bounds + ", on the screen " + expected);
				String span = darkSpan(table, item, c);
				int first = Integer.parseInt(span.substring(0, span.indexOf('.')));
				check(cell.x + first > bounds.x + bounds.width, "and its text is right of it: text from " + (cell.x + first) + ", image to " + (bounds.x + bounds.width));
			}
			check(greenBox(table, item.getBounds(0)) == null, "a column without an image has none (its image is in the column of the images)");
			check(greenBox(table, plain.getBounds(1)) == null && plain.getImageBounds(1).width == 0, "nor the cells of an item without images");
			table.dispose();
			if (font != null) font.dispose();
		}
		window.dispose();
	}

	/* The box of the green pixels in a rectangle of a table, or null */
	Rectangle greenBox(Table table, Rectangle area) {
		int left = Integer.MAX_VALUE, top = Integer.MAX_VALUE, right = -1, bottom = -1;
		for (int y = area.y; y < area.y + area.height; y++) {
			for (int x = area.x; x < area.x + area.width; x++) {
				int pel = pel(table, x, y);
				if (((pel >> 8) & 0xFF) > 200 && ((pel >> 16) & 0xFF) < 60 && (pel & 0xFF) < 60) {
					left = Math.min(left, x); right = Math.max(right, x);
					top = Math.min(top, y); bottom = Math.max(bottom, y);
				}
			}
		}
		return right < 0 ? null : new Rectangle(left, top, right - left + 1, bottom - top + 1);
	}

	/*
	 * The check boxes of SWT.CHECK, drawn by the table at the start of the first cell. The box
	 * is found by its dark frame in the pixels; a click on it (posted to the window of the
	 * records, as the system does) and the space bar change it and send a Selection with
	 * SWT.CHECK, without changing the selection.
	 */
	void checkChecksOnScreen() {
		saySubObjective("Check boxes, seen on the screen");
		Shell window = new Shell(display, SWT.NO_TRIM);
		window.setBounds(10, 10, 400, 200);
		window.open();
		final Table table = new Table(window, SWT.MULTI | SWT.CHECK | SWT.BORDER);
		table.setBounds(0, 0, 400, 200);
		for (int c = 0; c < 2; c++) new TableColumn(table, SWT.NONE).setWidth(120);
		TableItem[] items = new TableItem[4];
		for (int i = 0; i < items.length; i++) {
			items[i] = new TableItem(table, SWT.NONE);
			items[i].setText(new String[] {"Item " + i, "x"});
		}
		final int[] events = new int[2];
		final TableItem[] last = new TableItem[1];
		table.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (e.detail == SWT.CHECK) events[0]++; else events[1]++;
				last[0] = (TableItem) e.item;
			}
		});
		items[1].setChecked(true);
		items[2].setChecked(true);
		items[2].setGrayed(true);
		settleTable(table);
		check(events[0] == 0 && events[1] == 0, "setChecked and setGrayed send no event");
		Rectangle[] boxes = new Rectangle[items.length];
		for (int i = 0; i < items.length; i++) boxes[i] = box(table, items[i].getBounds(0));
		check(boxes[0] != null && boxes[0].width >= 9 && boxes[0].width == boxes[0].height, "a check box is drawn in the first cell: " + boxes[0]);
		String span = darkSpan(table, items[0], 0);
		check(boxes[0] != null && items[0].getBounds(0).x + Integer.parseInt(span.substring(span.indexOf("..") + 2)) > boxes[0].x + boxes[0].width + 4,
				"the text is after it: " + span + " from the cell, box " + boxes[0]);
		check(inside(table, boxes[0]).equals("light"), "an unchecked box is empty: " + inside(table, boxes[0]));
		check(inside(table, boxes[1]).equals("dark light"), "a checked one has the mark: " + inside(table, boxes[1]));
		check(inside(table, boxes[2]).equals("dark gray"), "a grayed one is gray, with the mark: " + inside(table, boxes[2]));
		check(box(table, items[0].getBounds(1)) == null, "no box in the other columns");

		table.setSelection(3);
		settleTable(table);
		setCellsDrawn(table, 0);
		postClick(table, boxes[0].x + boxes[0].width / 2, boxes[0].y + boxes[0].height / 2);
		int drawn = cellsDrawn(table);
		check(drawn > 0 && drawn <= 4, "only the row of the box is painted again (no flicker of the table): " + drawn + " cells");
		check(items[0].getChecked() && events[0] == 1 && last[0] == items[0], "a click on the box checks the item, with a Selection of SWT.CHECK: " + events[0]);
		check(table.getSelectionCount() == 1 && table.isSelected(3), "and the selection does not change: " + table.getSelectionCount());
		check(inside(table, boxes[0]).equals("dark light"), "the mark is drawn");
		postClick(table, boxes[1].x + boxes[1].width / 2, boxes[1].y + boxes[1].height / 2);
		check(!items[1].getChecked() && events[0] == 2, "a click on a checked box unchecks it");
		Rectangle text = items[2].getBounds(1);
		postClick(table, text.x + 5, text.y + text.height / 2);
		check(items[2].getChecked() && events[0] == 2, "a click elsewhere in the row does not change it");

		/* The space bar, for the item that has the cursor */
		int record = recordOf(items[0]);
		OS.WinSendMsg(table.handle, OS.CM_SETRECORDEMPHASIS, record, OS.MPFROM2SHORT((short) 1, (short) OS.CRA_CURSORED));
		OS.WinPostMsg(table.handle, OS.WM_CHAR, OS.KC_VIRTUALKEY | OS.KC_CHAR, ' ' | (OS.VK_SPACE << 16));
		settleTable(table);
		check(!items[0].getChecked() && events[0] == 3 && last[0] == items[0], "the space bar changes the box of the item with the cursor: " + items[0].getChecked() + ", events " + events[0]);
		window.dispose();
	}

	int recordOf(TableItem item) {
		try {
			java.lang.reflect.Field f = TableItem.class.getDeclaredField("handle");
			f.setAccessible(true);
			return ((Integer) f.get(item)).intValue();
		} catch (Exception e) {
			throw new RuntimeException(e.toString());
		}
	}

	int cellsDrawn(Table table) {
		try {
			java.lang.reflect.Field f = Table.class.getDeclaredField("cellsDrawn");
			f.setAccessible(true);
			return ((Integer) f.get(table)).intValue();
		} catch (Exception e) {
			throw new RuntimeException(e.toString());
		}
	}

	void setCellsDrawn(Table table, int value) {
		try {
			java.lang.reflect.Field f = Table.class.getDeclaredField("cellsDrawn");
			f.setAccessible(true);
			f.setInt(table, value);
		} catch (Exception e) {
			throw new RuntimeException(e.toString());
		}
	}

	/* A click posted to the window of the records at a point of the table */
	void postClick(Table table, int x, int y) {
		int records = OS.WinWindowFromID(table.handle, OS.CID_LEFTDVWND);
		int[] point = {x, table.getSize().y - 1 - y};
		OS.WinMapWindowPoints(table.handle, records, point, 1);
		int mp1 = (point[0] & 0xFFFF) | (point[1] << 16);
		OS.WinPostMsg(records, OS.WM_BUTTON1DOWN, mp1, 0);
		OS.WinPostMsg(records, OS.WM_BUTTON1UP, mp1, 0);
		/* The system sends it after the button goes up: the container selects with it */
		OS.WinPostMsg(records, OS.WM_BUTTON1CLICK, mp1, 0);
		settleTable(table);
	}

	/* The square of a dark frame in a rectangle: a column of dark pixels at least 9 high, and a row as long from its top */
	Rectangle box(Table table, Rectangle area) {
		for (int x = area.x; x < area.x + area.width; x++) {
			int top = -1, run = 0;
			for (int y = area.y; y < area.y + area.height; y++) {
				if (darkPel(pel(table, x, y))) {
					if (run == 0) top = y;
					run++;
					if (run >= 9 && (y + 1 == area.y + area.height || !darkPel(pel(table, x, y + 1)))) {
						/* A square: the top side is as long, dark too (a letter is not) */
						boolean square = true;
						for (int i = 0; i < run && square; i++) square = darkPel(pel(table, x + i, top));
						if (square) return new Rectangle(x, top, run, run);
					}
				} else {
					run = 0;
				}
			}
		}
		return null;
	}

	boolean darkPel(int pel) {
		return ((pel >> 16) & 0xFF) < 60 && ((pel >> 8) & 0xFF) < 60 && (pel & 0xFF) < 60;
	}

	/* What is inside the frame of a box: dark (the mark), light (white) and gray pixels */
	String inside(Table table, Rectangle box) {
		if (box == null) return "no box";
		boolean dark = false, light = false, gray = false;
		for (int y = box.y + 1; y < box.y + box.height - 1; y++) {
			for (int x = box.x + 1; x < box.x + box.width - 1; x++) {
				int pel = pel(table, x, y);
				int lum = (((pel >> 16) & 0xFF) + ((pel >> 8) & 0xFF) + (pel & 0xFF)) / 3;
				if (darkPel(pel)) dark = true;
				else if (lum > 240) light = true;
				else gray = true;
			}
		}
		return ((dark ? "dark " : "") + (light ? "light " : "") + (gray ? "gray" : "")).trim();
	}

	void settleTable(Table table) {
		pump();
		for (int i = 0; i < 20; i++) display.readAndDispatch();
		table.update();
		for (int i = 0; i < 20; i++) display.readAndDispatch();
	}

	int pel(Table table, int x, int y) {
		Point p = table.toDisplay(new Point(x, y));
		int hps = OS.WinGetScreenPS(OS.HWND_DESKTOP);
		OS.GpiCreateLogColorTable(hps, 0, OS.LCOLF_RGB, 0, 0, null);
		int pel = OS.GpiQueryPel(hps, new int[] {p.x, OS.WinQuerySysValue(OS.HWND_DESKTOP, OS.SV_CYSCREEN) - 1 - p.y}) & 0xFFFFFF;
		OS.WinReleasePS(hps);
		return pel;
	}

	/* The first and last x, from the left of the cell, of the dark pixels in it */
	String darkSpan(Table table, TableItem item, int column) {
		Rectangle r = item.getBounds(column);
		int first = -1, last = -1;
		for (int y = r.y; y < r.y + r.height; y++) {
			for (int x = r.x; x < r.x + r.width; x++) {
				int pel = pel(table, x, y);
				if ((((pel >> 16) & 0xFF) + ((pel >> 8) & 0xFF) + (pel & 0xFF)) / 3 < 60) {
					if (first == -1 || x - r.x < first) first = x - r.x;
					if (x - r.x > last) last = x - r.x;
				}
			}
		}
		return first + ".." + last;
	}

	/* The colors in the cells of a row (not the column of the images), in a fixed order */
	String cellColors(Table table, TableItem item, int hilite) {
		boolean isDark = false, isRed = false, isWhite = false, isYellow = false, isHilite = false;
		for (int c = 0; c < 3; c++) {
			Rectangle r = item.getBounds(c);
			for (int y = r.y + 1; y < r.y + r.height - 1; y++) {
				for (int x = r.x + 1; x < r.x + r.width - 1; x++) {
					int pel = pel(table, x, y);
					int red = (pel >> 16) & 0xFF, green = (pel >> 8) & 0xFF, blue = pel & 0xFF;
					if (pel == hilite) isHilite = true;
					else if (red < 60 && green < 60 && blue < 60) isDark = true;
					else if (red > 200 && green < 60 && blue < 60) isRed = true;
					else if (red > 200 && green > 200 && blue < 60) isYellow = true;
					else if (red > 240 && green > 240 && blue > 240) isWhite = true;
				}
			}
		}
		String result = "";
		if (isDark) result += "dark ";
		if (isRed) result += "red ";
		if (isWhite) result += "white ";
		if (isYellow) result += "yellow ";
		if (isHilite) result += "hilite ";
		return result.trim();
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
		check(item.getImageBounds(0).width > 0, "the bounds of the image: " + item.getImageBounds(0));
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
		checkColumnGeometry();
		checkColumnEvents();
		checkRemove();
		checkScrolling();
		checkScrollingOnScreen();
		checkSizeAndRedraw();
		checkImagesAndColors();
		checkImageOnScreen();
		checkCellsOnScreen();
		checkColumnImagesOnScreen();
		checkChecksOnScreen();
		say("\n" + checks + " checks passed.\n");

		sayObjective("Interactive: click, Ctrl and Shift click, press Enter, click the titles of the columns, the check boxes and the space bar");
		shell.setLayout(null);
		final Table table = new Table(shell, SWT.MULTI | SWT.CHECK | SWT.BORDER);
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
		org.eclipse.swt.widgets.Listener columnListener = new org.eclipse.swt.widgets.Listener() {
			public void handleEvent(Event e) {
				TableColumn column = (TableColumn) e.widget;
				String type = e.type == SWT.Selection ? "Selection" : e.type == SWT.Resize ? "Resize" : "Move";
				String msg = type + " of the column " + column.getText() + " (width " + column.getWidth() + ")";
				say(msg);
				log.setText(msg);
			}
		};
		for (int i = 0; i < titles.length; i++) {
			TableColumn column = new TableColumn(table, aligns[i]);
			column.setText(titles[i]);
			column.setWidth(widths[i]);
			column.addListener(SWT.Selection, columnListener);
			column.addListener(SWT.Resize, columnListener);
			column.addListener(SWT.Move, columnListener);
		}
		String[] kinds = {"image", "text", "audio", "folder"};
		for (int i = 0; i < 40; i++) {
			TableItem item = new TableItem(table, SWT.NONE);
			item.setText(new String[] {"File " + (i + 1), String.valueOf((i + 1) * 137) + " KB", kinds[i % kinds.length]});
			item.setImage(image);
			/* Colors to look at: a red text every five rows, a yellow background */
			if (i % 5 == 4) item.setForeground(display.getSystemColor(SWT.COLOR_RED));
			if (i == 2) item.setBackground(display.getSystemColor(SWT.COLOR_YELLOW));
			/* Some boxes checked at first, one grayed */
			if (i % 4 == 1) item.setChecked(true);
			if (i == 5) item.setGrayed(true);
			/* An image in the column Size every three rows */
			if (i % 3 == 0) item.setImage(1, image);
		}
		table.setRedraw(true);
		table.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String msg = e.detail == SWT.CHECK
					? "Check: " + ((TableItem) e.item).getText() + " is " + (((TableItem) e.item).getChecked() ? "checked" : "not checked")
					: "Selection: " + (e.item == null ? "(none)" : ((TableItem) e.item).getText()) + " (" + table.getSelectionCount() + " selected)";
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
