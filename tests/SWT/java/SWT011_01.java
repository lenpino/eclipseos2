/*
 *  SWT011_01.java
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
import org.eclipse.swt.events.TreeAdapter;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.pm.OS;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

/**
 *  Validates the Tree and TreeItem widgets, built on the tree view of the PM
 *  container control.
 *
 *  The first part is automatic: any failed check throws an exception, so
 *  the testcase is reported as FAILED. The notifications that the container
 *  sends to the user's actions (a click, the Enter key, the plus sign) are
 *  simulated by sending the same WM_CONTROL message to the parent. The second
 *  part is interactive: use the tree, and watch the events printed to the
 *  console and to the text at the bottom.
 *
 *  To close the window automatically (for unattended runs) pass the system
 *  property swt.test.autoclose=<milliseconds> to the JVM.
 */
public class SWT011_01 extends SWTTestCase {

	static {
		STEP = "011";
		TEST = "01";
		DESC = "Tree / TreeItem";
	}

	int checks;
	Image image;

	Shell createTopShell(Display display) {
		return new Shell(display, SWT.SHELL_TRIM | SWT.NO_REDRAW_RESIZE);
	}

	void check(boolean ok, String what) {
		checks++;
		say((ok ? "  ok    " : "  FAIL  ") + what);
		if (!ok) throw new RuntimeException("Check failed: " + what);
	}

	/* Deferred events run after a window message is dispatched, see SWT010_01 */
	void pump() {
		OS.WinPostMsg(shell.handle, OS.WM_NULL, 0, 0);
		while (display.readAndDispatch()) {}
	}

	boolean throwsRange(Runnable r) {
		try { r.run(); } catch (IllegalArgumentException e) { return true; }
		return false;
	}

	void checkHierarchy() {
		saySubObjective("Hierarchy, indexes and texts");
		final Tree tree = new Tree(shell, SWT.BORDER);
		tree.setBounds(0, 0, 300, 200);
		check(tree.getItemCount() == 0 && tree.getItems().length == 0, "a new tree has no items");
		check(tree.getParentItem() == null, "a tree has no parent item");

		TreeItem root1 = new TreeItem(tree, SWT.NONE);
		root1.setText("Root 1");
		TreeItem root2 = new TreeItem(tree, SWT.NONE);
		root2.setText("Root 2");
		check(root1.getText().equals("Root 1") && root2.getText().equals("Root 2"), "getText gives what setText set");
		check(tree.getItemCount() == 2, "two roots");
		check(tree.getItems()[0] == root1 && tree.getItems()[1] == root2, "the roots are in the order they were created");

		TreeItem child11 = new TreeItem(root1, SWT.NONE);
		child11.setText("Child 1.1");
		TreeItem child12 = new TreeItem(root1, SWT.NONE);
		child12.setText("Child 1.2");
		check(root1.getItemCount() == 2 && root2.getItemCount() == 0, "the children count for their own parent only");
		check(root1.getItems()[0] == child11 && root1.getItems()[1] == child12, "children in order");
		check(child11.getParentItem() == root1 && root1.getParentItem() == null, "getParentItem");
		check(child11.getParent() == tree && root2.getParent() == tree, "getParent");
		check(tree.getItemCount() == 2, "the children are not roots");

		TreeItem middle = new TreeItem(tree, SWT.NONE, 1);
		middle.setText("Middle");
		check(tree.getItems()[0] == root1 && tree.getItems()[1] == middle && tree.getItems()[2] == root2,
				"TreeItem(tree, style, 1) goes between the roots");
		TreeItem last = new TreeItem(tree, SWT.NONE, 3);
		check(tree.getItems()[3] == last, "an index equal to the count appends");
		TreeItem first = new TreeItem(tree, SWT.NONE, 0);
		check(tree.getItems()[0] == first && tree.getItems()[1] == root1, "index 0 goes first");
		TreeItem firstChild = new TreeItem(root1, SWT.NONE, 0);
		TreeItem midChild = new TreeItem(root1, SWT.NONE, 2);
		check(root1.getItems()[0] == firstChild && root1.getItems()[1] == child11
				&& root1.getItems()[2] == midChild && root1.getItems()[3] == child12, "indexes among the children");

		final TreeItem target = root2;
		check(throwsRange(new Runnable() { public void run() { new TreeItem(tree, SWT.NONE, 99); } }), "an index out of range throws");
		check(throwsRange(new Runnable() { public void run() { new TreeItem(target, SWT.NONE, 1); } }), "an index out of range of a child list throws");
		check(throwsRange(new Runnable() { public void run() { new TreeItem(tree, SWT.NONE, -1); } }), "a negative index throws");

		root1.setText("A longer text for the first root, to see that the string is replaced");
		check(root1.getText().startsWith("A longer text"), "setText replaces the text");
		root1.setText("");
		check(root1.getText().equals(""), "an empty text");
		root1.setText("Root 1");
		tree.dispose();
	}

	void checkExpandAndBounds() {
		saySubObjective("Expanding, bounds and showing");
		Tree tree = new Tree(shell, SWT.BORDER);
		tree.setBounds(0, 0, 300, 200);
		TreeItem root = new TreeItem(tree, SWT.NONE);
		root.setText("Root");
		TreeItem child = new TreeItem(root, SWT.NONE);
		child.setText("Child");
		TreeItem grand = new TreeItem(child, SWT.NONE);
		grand.setText("Grandchild");
		TreeItem other = new TreeItem(tree, SWT.NONE);
		other.setText("Other root");

		check(!root.getExpanded(), "an item is collapsed at first");
		root.setExpanded(true);
		check(root.getExpanded(), "setExpanded(true)");
		root.setExpanded(false);
		check(!root.getExpanded(), "setExpanded(false)");

		Rectangle rb = root.getBounds(), ob = other.getBounds();
		check(rb.width > 0 && rb.height > 0, "a root has a size: " + rb);
		check(ob.y > rb.y && ob.y >= rb.y + rb.height, "the second root is below the first one: " + rb + " " + ob);
		Rectangle hidden = child.getBounds();
		check(hidden.width == 0 && hidden.height == 0, "a child of a collapsed item has no bounds: " + hidden);

		root.setExpanded(true);
		Rectangle cb = child.getBounds();
		check(cb.width > 0 && cb.height > 0 && cb.y > rb.y, "an expanded child is shown below its parent: " + cb);
		check(other.getBounds().y > cb.y, "the next root moves down: " + other.getBounds());
		check(tree.getItemHeight() > 0 && tree.getItemHeight() <= rb.height + 2, "getItemHeight: " + tree.getItemHeight());
		say("  info  row height " + rb.height + " (the icon cell is 16x16)");

		Point inside = new Point(rb.x + rb.width / 2, rb.y + rb.height / 2);
		check(tree.getItem(inside) == root, "getItem(Point) finds the root");
		check(tree.getItem(new Point(cb.x + cb.width / 2, cb.y + cb.height / 2)) == child, "getItem(Point) finds the child");
		check(tree.getItem(new Point(-50, -50)) == null, "getItem(Point) outside gives null");

		root.setExpanded(false);
		check(!grand.getExpanded(), "a grandchild is not expanded");
		tree.showItem(grand);
		check(root.getExpanded() && child.getExpanded(), "showItem expands the ancestors");
		tree.dispose();
	}

	/*
	 * The container did not place its records again when it changed of size: the
	 * ones inserted while it had no size were all in the same place and, when it
	 * got one, they went out of the visible part (the tree seemed empty).
	 */
	void checkSizeAfterItems() {
		saySubObjective("Items added before the tree has a size");
		Tree tree = new Tree(shell, SWT.BORDER);
		TreeItem[] roots = new TreeItem[4];
		for (int i = 0; i < roots.length; i++) {
			roots[i] = new TreeItem(tree, SWT.NONE);
			roots[i].setText("Root " + i);
			new TreeItem(roots[i], SWT.NONE).setText("Child " + i);
		}
		tree.setBounds(0, 0, 300, 200);
		for (int pass = 0; pass < 2; pass++) {
			boolean apart = true, inside = true;
			for (int i = 0; i < roots.length; i++) {
				Rectangle b = roots[i].getBounds();
				if (i > 0) apart &= b.y >= roots[i - 1].getBounds().y + roots[i - 1].getBounds().height;
				inside &= b.y >= 0 && b.y + b.height <= 200 + pass * 100;
			}
			check(apart, "the roots are one below the other after the tree gets a size (pass " + pass + "): "
					+ roots[0].getBounds() + " " + roots[1].getBounds() + " " + roots[3].getBounds());
			check(inside, "and they are in the visible part (pass " + pass + ")");
			tree.setBounds(0, 0, 350, 300);
		}
		tree.dispose();
	}

	/*
	 * With the redrawing off the container is not asked to paint what is added: it
	 * is painted once when the redrawing is on again (the user saw the items drawn
	 * one by one, without images, and then all again). The placement must be the
	 * same as with the redrawing on.
	 */
	void checkRedrawOff() {
		saySubObjective("Filling the tree with the redrawing off");
		Tree on = new Tree(shell, SWT.BORDER);
		Tree off = new Tree(shell, SWT.BORDER);
		on.setBounds(0, 0, 300, 200);
		off.setBounds(0, 0, 300, 200);
		TreeItem[] a = new TreeItem[3], b = new TreeItem[3];
		off.setRedraw(false);
		for (int i = 0; i < 3; i++) {
			a[i] = new TreeItem(on, SWT.NONE);
			a[i].setText("Root " + i);
			a[i].setImage(image);
			new TreeItem(a[i], SWT.NONE).setText("Child");
			b[i] = new TreeItem(off, SWT.NONE);
			b[i].setText("Root " + i);
			b[i].setImage(image);
			new TreeItem(b[i], SWT.NONE).setText("Child");
		}
		off.setRedraw(true);
		boolean same = true;
		for (int i = 0; i < 3; i++) same &= a[i].getBounds().equals(b[i].getBounds());
		check(same, "the same placement with the redrawing off as with it on: " + a[2].getBounds() + " and " + b[2].getBounds());
		check(b[0].getBounds().width > 0 && b[2].getBounds().y > b[0].getBounds().y, "the items are placed one below the other");
		b[1].setExpanded(true);
		check(b[1].getExpanded(), "the items work after being added with the redrawing off");
		on.dispose();
		off.dispose();
	}

	void checkSelection() {
		saySubObjective("Selection");
		Tree single = new Tree(shell, SWT.SINGLE | SWT.BORDER);
		single.setBounds(0, 0, 300, 200);
		TreeItem a = new TreeItem(single, SWT.NONE);
		a.setText("A");
		TreeItem b = new TreeItem(single, SWT.NONE);
		b.setText("B");
		check(single.getSelectionCount() == 0 && single.getSelection().length == 0, "a new tree has no selection: " + single.getSelectionCount());
		single.setSelection(new TreeItem[] {b});
		check(single.getSelectionCount() == 1 && single.getSelection()[0] == b, "setSelection / getSelection");
		single.setSelection(new TreeItem[] {a, b});
		check(single.getSelectionCount() == 1 && single.getSelection()[0] == a, "a single selection tree keeps the first item only");
		single.deselectAll();
		check(single.getSelectionCount() == 0, "deselectAll leaves nothing selected: " + single.getSelectionCount());
		single.selectAll();
		check(single.getSelectionCount() == 0, "selectAll does nothing in a single selection tree");
		single.setSelection(new TreeItem[0]);
		check(single.getSelectionCount() == 0, "an empty array clears the selection");
		boolean threw = false;
		try { single.setSelection(null); } catch (IllegalArgumentException e) { threw = true; }
		check(threw, "setSelection(null) throws");
		single.dispose();

		Tree multi = new Tree(shell, SWT.MULTI | SWT.BORDER);
		multi.setBounds(0, 0, 300, 200);
		TreeItem m1 = new TreeItem(multi, SWT.NONE);
		m1.setText("M1");
		TreeItem m2 = new TreeItem(multi, SWT.NONE);
		m2.setText("M2");
		TreeItem m11 = new TreeItem(m1, SWT.NONE);
		m11.setText("M1.1");
		multi.selectAll();
		check(multi.getSelectionCount() == 3, "selectAll selects every item, also the collapsed ones: " + multi.getSelectionCount());
		TreeItem[] all = multi.getSelection();
		check(all[0] == m1 && all[1] == m11 && all[2] == m2, "getSelection is in the order of the tree");
		multi.setSelection(new TreeItem[] {m2, m11});
		TreeItem[] two = multi.getSelection();
		check(two.length == 2 && two[0] == m11 && two[1] == m2, "setSelection with two items selects both: " + two.length);
		multi.deselectAll();
		check(multi.getSelectionCount() == 0, "deselectAll (MULTI)");
		m11.dispose();
		multi.selectAll();
		check(multi.getSelectionCount() == 2, "a disposed item is not in the selection");
		multi.dispose();
	}

	/*
	 * The clicks of the user, sent to the tree as the mouse messages (the container does not
	 * report a click on a record that it already has selected, nor the keys), and the highlight
	 * of the selected items on the screen, found by its color.
	 */
	void checkClicks() {
		saySubObjective("Clicks and the highlight, seen on the screen");
		Shell window = new Shell(display, SWT.NO_TRIM);
		window.setBounds(10, 10, 300, 200);
		window.open();
		Tree tree = new Tree(window, SWT.MULTI | SWT.BORDER);
		tree.setBounds(0, 0, 300, 200);
		final TreeItem[] items = new TreeItem[5];
		for (int i = 0; i < items.length; i++) {
			items[i] = new TreeItem(tree, SWT.NONE);
			items[i].setText("Item " + i);
		}
		TreeItem child = new TreeItem(items[4], SWT.NONE);
		child.setText("Child");
		final int[] events = new int[1];
		final TreeItem[] last = new TreeItem[1];
		tree.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) { events[0]++; last[0] = (TreeItem) e.item; }
		});
		settle(tree);
		check(lit(tree, items).equals(""), "nothing is highlighted at first: " + lit(tree, items));

		click(tree, items[1], 0);
		check(selection(tree).equals("1 ") && events[0] == 1 && last[0] == items[1], "a click selects the item: " + selection(tree) + ", events " + events[0]);
		check(lit(tree, items).equals("1 "), "and it is highlighted: " + lit(tree, items));
		click(tree, items[3], OS.KC_CTRL);
		check(selection(tree).equals("1 3 ") && events[0] == 2, "Ctrl and a click adds an item: " + selection(tree));
		check(lit(tree, items).equals("1 3 "), "both are highlighted: " + lit(tree, items));
		click(tree, items[1], OS.KC_CTRL);
		check(selection(tree).equals("3 "), "Ctrl and a click on a selected item removes it: " + selection(tree));
		/* The anchor is the last item clicked without Shift (the item 1, as on Windows) */
		click(tree, items[3], OS.KC_SHIFT);
		check(selection(tree).equals("1 2 3 "), "Shift and a click selects from the anchor: " + selection(tree));
		check(lit(tree, items).equals("1 2 3 "), "all of them highlighted: " + lit(tree, items));
		click(tree, items[0], OS.KC_SHIFT);
		check(selection(tree).equals("0 1 "), "and again from the same anchor, upwards: " + selection(tree));
		click(tree, items[2], 0);
		check(selection(tree).equals("2 "), "a click alone leaves one item: " + selection(tree));

		tree.deselectAll();
		settle(tree);
		check(selection(tree).equals("") && lit(tree, items).equals(""), "deselectAll clears the selection and the highlight: " + lit(tree, items));
		int before = events[0];
		click(tree, items[2], 0);
		check(selection(tree).equals("2 ") && events[0] == before + 1, "a click on the item that the container still has selected selects it again: " + selection(tree));

		/* The plus sign of the item 4 is left of its icon (measured: from 20 to 7 pixels left of it) */
		Rectangle b4 = items[4].getBounds();
		before = events[0];
		mouse(tree, b4.x - 13, b4.y + b4.height / 2, 0);
		settle(tree);
		/* The container does not expand with these messages (it looks at the real mouse): that is seen by hand */
		check(selection(tree).equals("2 ") && events[0] == before, "and does not select it: " + selection(tree) + ", events " + (events[0] - before));
		check(lit(tree, items).equals("2 "), "the highlight did not move: " + lit(tree, items));
		window.dispose();
	}

	/* A click with the keys (KC_CTRL, KC_SHIFT) in the middle of the text of an item */
	void click(Tree tree, TreeItem item, int keys) {
		Rectangle b = item.getBounds();
		mouse(tree, b.x + b.width / 2, b.y + b.height / 2, keys);
		settle(tree);
	}

	void mouse(Tree tree, int x, int y, int keys) {
		int height = tree.getSize().y;
		int mp1 = (x & 0xFFFF) | ((height - 1 - y) << 16);
		OS.WinSendMsg(tree.handle, OS.WM_BUTTON1DOWN, mp1, keys << 16);
		OS.WinSendMsg(tree.handle, OS.WM_BUTTON1UP, mp1, keys << 16);
	}

	/* The selected items, by the number in their text */
	String selection(Tree tree) {
		TreeItem[] selection = tree.getSelection();
		String result = "";
		for (int i = 0; i < selection.length; i++) result += selection[i].getText().substring(5) + " ";
		return result;
	}

	/* The items whose row has the color of the highlight on the screen */
	String lit(Tree tree, TreeItem[] items) {
		int hilite = OS.WinQuerySysColor(OS.HWND_DESKTOP, OS.SYSCLR_HILITEBACKGROUND, 0) & 0xFFFFFF;
		Point origin = tree.toDisplay(new Point(0, 0));
		int width = tree.getClientArea().width;
		int screen = OS.WinQuerySysValue(OS.HWND_DESKTOP, OS.SV_CYSCREEN);
		int hps = OS.WinGetScreenPS(OS.HWND_DESKTOP);
		OS.GpiCreateLogColorTable(hps, 0, OS.LCOLF_RGB, 0, 0, null);
		String result = "";
		for (int i = 0; i < items.length; i++) {
			Rectangle r = items[i].getBounds();
			int count = 0;
			for (int y = r.y; y < r.y + r.height; y++) {
				for (int x = 0; x < width; x++) {
					if ((OS.GpiQueryPel(hps, new int[] {origin.x + x, screen - 1 - (origin.y + y)}) & 0xFFFFFF) == hilite) count++;
				}
			}
			/* The dotted frame of the cursor has the color too, a few pixels */
			if (count > 100) result += i + " ";
		}
		OS.WinReleasePS(hps);
		return result;
	}

	/** Sends the parent the WM_CONTROL that the container sends for a user action. */
	void notifyParent(Tree tree, int code, int mp2) {
		int id = OS.WinQueryWindowUShort(tree.handle, OS.QWS_ID);
		OS.WinSendMsg(shell.handle, OS.WM_CONTROL, OS.MPFROM2SHORT((short) id, (short) code), mp2);
	}

	void checkEvents() {
		saySubObjective("Events (the notifications of the container are simulated)");
		final Tree tree = new Tree(shell, SWT.SINGLE | SWT.BORDER);
		tree.setBounds(0, 0, 300, 200);
		TreeItem parentItem = new TreeItem(tree, SWT.NONE);
		parentItem.setText("Parent");
		TreeItem childItem = new TreeItem(parentItem, SWT.NONE);
		childItem.setText("Child");

		final TreeItem[] selected = new TreeItem[1], entered = new TreeItem[1], expanded = new TreeItem[1], collapsed = new TreeItem[1];
		final int[] counts = new int[4];
		tree.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) { selected[0] = (TreeItem) e.item; counts[0]++; }
			public void widgetDefaultSelected(SelectionEvent e) { entered[0] = (TreeItem) e.item; counts[1]++; }
		});
		tree.addTreeListener(new TreeAdapter() {
			public void treeExpanded(TreeEvent e) { expanded[0] = (TreeItem) e.item; counts[2]++; }
			public void treeCollapsed(TreeEvent e) { collapsed[0] = (TreeItem) e.item; counts[3]++; }
		});

		parentItem.setExpanded(true);
		parentItem.setExpanded(false);
		pump();
		check(counts[2] == 0 && counts[3] == 0, "setExpanded sends no Expand or Collapse event");
		tree.setSelection(new TreeItem[] {childItem});
		pump();
		check(counts[0] == 0, "setSelection sends no Selection event");
		check(tree.getSelection().length == 1 && tree.getSelection()[0] == childItem, "the child is selected (its parent is expanded by setSelection)");

		/*
		 * A click of the user: the container selects the record (here with the message,
		 * without going through Tree, which would note the selection) and then sends
		 * the notification. NOTIFYRECORDEMPHASIS: hwndCnr, pRecord, fEmphasisMask.
		 */
		tree.setSelection(new TreeItem[] {parentItem});
		OS.WinSendMsg(tree.handle, OS.CM_SETRECORDEMPHASIS, childItem.handle, OS.MPFROM2SHORT((short) 1, (short) OS.CRA_SELECTED));
		int emphasis = Tree_allocate(12);
		poke(emphasis, tree.handle);
		poke(emphasis + 4, childItem.handle);
		poke(emphasis + 8, OS.CRA_SELECTED);
		notifyParent(tree, OS.CN_EMPHASIS, emphasis);
		pump();
		check(counts[0] == 1 && selected[0] == childItem, "CN_EMPHASIS of a selected record sends Selection with the item (events: " + counts[0] + ", item: " + (selected[0] == null ? "none" : selected[0].getText()) + ")");
		notifyParent(tree, OS.CN_EMPHASIS, emphasis);
		notifyParent(tree, OS.CN_EMPHASIS, emphasis);
		pump();
		check(counts[0] == 1, "the same notification again is not reported twice (events: " + counts[0] + ")");
		tree.setSelection(new TreeItem[] {parentItem});
		notifyParent(tree, OS.CN_EMPHASIS, emphasis);
		pump();
		check(counts[0] == 1, "CN_EMPHASIS of a record that is not selected sends nothing (events: " + counts[0] + ")");
		OS.free(emphasis);

		/* NOTIFYRECORDENTER: hwndCnr, fKey, pRecord */
		int enter = Tree_allocate(12);
		poke(enter, tree.handle);
		poke(enter + 8, parentItem.handle);
		notifyParent(tree, OS.CN_ENTER, enter);
		pump();
		check(counts[1] == 1 && entered[0] == parentItem, "CN_ENTER sends DefaultSelection with the item");
		OS.free(enter);

		notifyParent(tree, OS.CN_EXPANDTREE, parentItem.handle);
		check(counts[2] == 1 && expanded[0] == parentItem, "CN_EXPANDTREE sends Expand with the item");
		notifyParent(tree, OS.CN_COLLAPSETREE, parentItem.handle);
		check(counts[3] == 1 && collapsed[0] == parentItem, "CN_COLLAPSETREE sends Collapse with the item");
		tree.dispose();
	}

	/* Helpers to build the notification structures in memory of the process */
	int Tree_allocate(int size) {
		int block = OS.malloc(size);
		OS.memset(block, 0, size);
		return block;
	}

	void poke(int address, int value) {
		OS.objcpy(address, new byte[] {(byte) value, (byte) (value >> 8), (byte) (value >> 16), (byte) (value >> 24)});
	}

	void checkDispose() {
		saySubObjective("Dispose and removeAll");
		Tree tree = new Tree(shell, SWT.BORDER);
		tree.setBounds(0, 0, 300, 200);
		TreeItem r1 = new TreeItem(tree, SWT.NONE);
		r1.setText("R1");
		TreeItem r2 = new TreeItem(tree, SWT.NONE);
		r2.setText("R2");
		TreeItem c1 = new TreeItem(r1, SWT.NONE);
		c1.setText("C1");
		TreeItem c2 = new TreeItem(r1, SWT.NONE);
		c2.setText("C2");
		TreeItem g = new TreeItem(c1, SWT.NONE);
		g.setText("G");

		c2.dispose();
		check(c2.isDisposed() && r1.getItemCount() == 1, "disposing a child removes it from its parent");
		r1.dispose();
		check(r1.isDisposed() && c1.isDisposed() && g.isDisposed(), "disposing an item disposes its descendants");
		check(tree.getItemCount() == 1 && tree.getItems()[0] == r2, "the other root stays");
		r2.dispose();
		check(tree.getItemCount() == 0, "no items left");

		for (int i = 0; i < 5; i++) {
			TreeItem item = new TreeItem(tree, SWT.NONE);
			item.setText("Item " + i);
			new TreeItem(item, SWT.NONE).setText("Child " + i);
		}
		TreeItem[] all = tree.getItems();
		tree.removeAll();
		check(tree.getItemCount() == 0, "removeAll leaves no items");
		boolean allGone = true;
		for (int i = 0; i < all.length; i++) allGone &= all[i].isDisposed();
		check(allGone, "removeAll disposes the items");
		TreeItem again = new TreeItem(tree, SWT.NONE);
		again.setText("Again");
		check(tree.getItemCount() == 1 && tree.getItems()[0] == again, "items can be added after removeAll");

		for (int i = 0; i < 300; i++) new TreeItem(tree, SWT.NONE).setText("Many " + i);
		check(tree.getItemCount() == 301, "many items: " + tree.getItemCount());
		tree.dispose();
		check(again.isDisposed(), "disposing the tree disposes its items");
	}

	void checkImagesAndColors() {
		saySubObjective("Images and colors");
		Tree tree = new Tree(shell, SWT.BORDER);
		tree.setBounds(0, 0, 300, 200);
		TreeItem item = new TreeItem(tree, SWT.NONE);
		item.setText("With image");
		item.setImage(image);
		check(item.getImage() == image, "setImage / getImage");
		check(item.getBounds().width > 0, "an item with an image has bounds: " + item.getBounds());
		item.setImage(null);
		check(item.getImage() == null, "the image can be removed");

		check(item.getBackground().equals(tree.getBackground()) && item.getForeground().equals(tree.getForeground()),
				"the default colors are those of the tree");
		Color red = display.getSystemColor(SWT.COLOR_RED), blue = display.getSystemColor(SWT.COLOR_BLUE);
		item.setBackground(red);
		item.setForeground(blue);
		check(item.getBackground().equals(red) && item.getForeground().equals(blue), "setBackground / setForeground");
		item.setBackground(null);
		check(item.getBackground().equals(tree.getBackground()), "null restores the default color");
		check(!item.getChecked() && !item.getGrayed(), "no checked or grayed state without SWT.CHECK");
		tree.dispose();
	}

	/*
	 * The direction of the scrolling of showItem(), on the screen: only the items 30 and
	 * 70 have an image (green), and the green must be at the top or at the bottom of the
	 * tree. The positions that the container reports are not used.
	 */
	void checkScrollingOnScreen() {
		saySubObjective("Scrolling, seen on the screen");
		Shell window = new Shell(display, SWT.NO_TRIM);
		window.setBounds(10, 10, 300, 200);
		window.open();
		Tree tree = new Tree(window, SWT.SINGLE | SWT.BORDER);
		tree.setBounds(0, 0, 300, 200);
		TreeItem[] items = new TreeItem[100];
		for (int i = 0; i < items.length; i++) {
			items[i] = new TreeItem(tree, SWT.NONE);
			items[i].setText("Item " + i);
			if (i == 30 || i == 70) items[i].setImage(image);
		}
		settle(tree);
		int height = tree.getClientArea().height, row = items[0].getBounds().height;
		check(green(tree) == null, "at first no green is shown");
		tree.showItem(items[30]);
		settle(tree);
		int[] g = green(tree);
		check(g != null && g[1] > height - row - 4, "showItem from above shows the item at the bottom (green " + span(g) + ", height " + height + ", row " + row + ", item 30 at " + items[30].getBounds() + ")");
		tree.showItem(items[70]);
		settle(tree);
		g = green(tree);
		check(g != null && g[1] > height - row - 4, "and the next one too (green " + span(g) + ")");
		tree.showItem(items[30]);
		settle(tree);
		g = green(tree);
		check(g != null && g[0] < row + 2, "showItem from below shows it at the top (green " + span(g) + ")");
		tree.showItem(items[0]);
		settle(tree);
		check(green(tree) == null, "showItem of the first one goes back up (green " + span(green(tree)) + ")");
		window.dispose();
	}

	/*
	 * The colors of the items on the screen: the text with its foreground, on its background,
	 * and a selected item with the colors of the highlight, as on Windows. The rest of what
	 * the container draws (the images, the plus sign, the items without colors) must stay
	 * as it was: the tree draws the texts in RGB and gives the presentation space back.
	 */
	void checkColorsOnScreen() {
		saySubObjective("Colors of the items, seen on the screen");
		Shell window = new Shell(display, SWT.NO_TRIM);
		window.setBounds(10, 10, 300, 200);
		window.open();
		Tree tree = new Tree(window, SWT.MULTI | SWT.BORDER);
		tree.setBounds(0, 0, 300, 200);
		Color red = new Color(display, 255, 0, 0), yellow = new Color(display, 255, 255, 0), cyan = new Color(display, 0, 255, 255);
		String[] names = {"Plain", "Red text", "On yellow", "Red on yellow", "Image, red", "Selected red"};
		TreeItem[] items = new TreeItem[names.length];
		for (int i = 0; i < names.length; i++) {
			items[i] = new TreeItem(tree, SWT.NONE);
			items[i].setText(names[i] + " WWWW");
		}
		new TreeItem(items[0], SWT.NONE).setText("child");
		items[1].setForeground(red);
		items[2].setBackground(yellow);
		items[3].setForeground(red);
		items[3].setBackground(yellow);
		items[4].setImage(image);
		items[4].setForeground(red);
		items[5].setForeground(red);
		tree.setSelection(new TreeItem[] {items[5]});
		settle(tree);
		int hilite = OS.WinQuerySysColor(OS.HWND_DESKTOP, OS.SYSCLR_HILITEBACKGROUND, 0) & 0xFFFFFF;
		String counts = "";
		for (int i = 0; i < items.length; i++) counts += i + ":" + colors(tree, items[i], hilite) + " ";
		check(colors(tree, items[0], hilite).equals("dark white"), "an item without colors: dark text on white (" + counts + ")");
		check(colors(tree, items[1], hilite).equals("red white"), "the foreground is the color of the text (" + counts + ")");
		check(colors(tree, items[2], hilite).equals("dark yellow"), "the background is behind the text (" + counts + ")");
		check(colors(tree, items[3], hilite).equals("red yellow"), "both colors (" + counts + ")");
		check(colors(tree, items[4], hilite).equals("red white") && colors(tree, items[4], hilite, 0).indexOf("green") != -1, "the image is still drawn next to a colored text (" + counts + ", all the item: " + colors(tree, items[4], hilite, 0) + ")");
		check(colors(tree, items[5], hilite).equals("white hilite"), "a selected item has the colors of the highlight, not its own (" + counts + ")");
		Rectangle b0 = items[0].getBounds();
		int box = 0;
		for (int x = b0.x - 20; x < b0.x - 7; x++) if (dark(pel(tree, x, b0.y + b0.height / 2))) box++;
		check(box >= 2, "the plus sign is still drawn: " + box + " dark pixels across it");
		items[1].setForeground(null);
		settle(tree);
		check(colors(tree, items[1], hilite).equals("dark white"), "null gives the text the color of the tree again: " + colors(tree, items[1], hilite));
		tree.setBackground(cyan);
		settle(tree);
		check(colors(tree, items[0], hilite).equals("dark cyan"), "the text of an item without colors is on the background of the tree: " + colors(tree, items[0], hilite));
		window.dispose();
		red.dispose();
		yellow.dispose();
		cyan.dispose();
	}

	/*
	 * The colors found in the text of an item, in a fixed order: the text, the background, the image.
	 * The bounds of an item start with its icon: the text is 22 pixels further (measured, WM_DRAWITEM).
	 */
	String colors(Tree tree, TreeItem item, int hilite) {
		return colors(tree, item, hilite, 22);
	}

	String colors(Tree tree, TreeItem item, int hilite, int from) {
		Rectangle b = item.getBounds();
		boolean isDark = false, isRed = false, isWhite = false, isYellow = false, isCyan = false, isGreen = false, isHilite = false;
		/* The rectangle of the text is lower than the row (13 of 16 pixels): its middle */
		for (int y = b.y + b.height / 2 - 3; y <= b.y + b.height / 2 + 3; y++) {
			for (int x = b.x + from; x < b.x + b.width; x++) {
				int pel = pel(tree, x, y);
				int r = (pel >> 16) & 0xFF, g = (pel >> 8) & 0xFF, bl = pel & 0xFF;
				if (pel == hilite) isHilite = true;
				else if (dark(pel)) isDark = true;
				else if (r > 200 && g < 60 && bl < 60) isRed = true;
				else if (r > 200 && g > 200 && bl < 60) isYellow = true;
				else if (r < 60 && g > 200 && bl > 200) isCyan = true;
				else if (r < 60 && g > 200 && bl < 60) isGreen = true;
				else if (r > 240 && g > 240 && bl > 240) isWhite = true;
			}
		}
		String result = "";
		if (isDark) result += "dark ";
		if (isRed) result += "red ";
		if (isWhite) result += "white ";
		if (isYellow) result += "yellow ";
		if (isCyan) result += "cyan ";
		if (isHilite) result += "hilite ";
		if (isGreen) result += "green ";
		return result.trim();
	}

	boolean dark(int pel) {
		return ((pel >> 16) & 0xFF) < 60 && ((pel >> 8) & 0xFF) < 60 && (pel & 0xFF) < 60;
	}

	int pel(Tree tree, int x, int y) {
		Point p = tree.toDisplay(new Point(x, y));
		int hps = OS.WinGetScreenPS(OS.HWND_DESKTOP);
		OS.GpiCreateLogColorTable(hps, 0, OS.LCOLF_RGB, 0, 0, null);
		int pel = OS.GpiQueryPel(hps, new int[] {p.x, OS.WinQuerySysValue(OS.HWND_DESKTOP, OS.SV_CYSCREEN) - 1 - p.y}) & 0xFFFFFF;
		OS.WinReleasePS(hps);
		return pel;
	}

	void settle(Tree tree) {
		pump();
		for (int i = 0; i < 20; i++) display.readAndDispatch();
		tree.update();
	}

	String span(int[] g) {
		return g == null ? "none" : g[0] + ".." + g[1];
	}

	/* The first and last y of the client area with green pixels near the left, or null */
	int[] green(Tree tree) {
		int height = tree.getClientArea().height;
		Point origin = tree.toDisplay(new Point(0, 0));
		int screen = OS.WinQuerySysValue(OS.HWND_DESKTOP, OS.SV_CYSCREEN);
		int hps = OS.WinGetScreenPS(OS.HWND_DESKTOP);
		OS.GpiCreateLogColorTable(hps, 0, OS.LCOLF_RGB, 0, 0, null);
		int[] result = null;
		for (int y = 0; y < height; y++) {
			boolean green = false;
			for (int x = 0; x < 60 && !green; x++) {
				int pel = OS.GpiQueryPel(hps, new int[] {origin.x + x, screen - 1 - (origin.y + y)});
				green = ((pel >> 8) & 0xFF) > 200 && ((pel >> 16) & 0xFF) < 60 && (pel & 0xFF) < 60;
			}
			if (green) {
				if (result == null) result = new int[] {y, y};
				result[1] = y;
			}
		}
		OS.WinReleasePS(hps);
		return result;
	}

	void initComponents() {
		image = new Image(display, 16, 16);
		GC gc = new GC(image);
		gc.setBackground(display.getSystemColor(SWT.COLOR_GREEN));
		gc.fillRectangle(1, 1, 14, 14);
		gc.dispose();

		/*
		 * The window is not shown during the automatic checks (SWTTestCase shows it after
		 * initComponents): the trees of the checks, with their 301 items and their scroll
		 * bars, appeared and went away in it, before the one of the interactive part.
		 */
		shell.setSize(420, 320);
		sayObjective("Automatic checks");
		checkHierarchy();
		checkExpandAndBounds();
		checkSizeAfterItems();
		checkRedrawOff();
		checkSelection();
		checkClicks();
		checkEvents();
		checkDispose();
		checkImagesAndColors();
		checkColorsOnScreen();
		checkScrollingOnScreen();
		say("\n" + checks + " checks passed.\n");

		sayObjective("Interactive: click, Ctrl and Shift click, the arrow keys, double click, Enter, the plus signs (they do not select), the buttons Show the last / Show the first");
		shell.setLayout(null);
		final Tree tree = new Tree(shell, SWT.MULTI | SWT.BORDER);
		final Text log = new Text(shell, SWT.BORDER | SWT.MULTI | SWT.READ_ONLY);
		final Button showLast = new Button(shell, SWT.PUSH), showFirst = new Button(shell, SWT.PUSH);
		showLast.setText("Show the last");
		showFirst.setText("Show the first");
		/* Size first, and no redrawing while the items are added: nothing is painted twice */
		layoutInteractive(tree, log, showLast, showFirst);
		tree.setRedraw(false);
		String[] names = {"Documents", "Pictures", "Music", "Empty"};
		for (int i = 0; i < names.length; i++) {
			TreeItem root = new TreeItem(tree, SWT.NONE);
			root.setText(names[i]);
			root.setImage(image);
			for (int j = 0; j < (i == 3 ? 0 : 3); j++) {
				TreeItem child = new TreeItem(root, SWT.NONE);
				child.setText(names[i] + " / item " + (j + 1));
				child.setImage(image);
				/* Colors to look at: a red text, a yellow background */
				if (i == 1 && j == 0) child.setForeground(display.getSystemColor(SWT.COLOR_RED));
				if (i == 2 && j == 0) child.setBackground(display.getSystemColor(SWT.COLOR_YELLOW));
				if (j == 1) {
					TreeItem grandchild = new TreeItem(child, SWT.NONE);
					grandchild.setText("a grandchild");
					grandchild.setImage(image);
				}
			}
		}
		/* Enough items to scroll: showItem() of the last one and of the first one, with the buttons */
		TreeItem many = new TreeItem(tree, SWT.NONE);
		many.setText("Many");
		many.setImage(image);
		final TreeItem[] ends = new TreeItem[2];
		for (int i = 1; i <= 40; i++) {
			TreeItem child = new TreeItem(many, SWT.NONE);
			child.setText("Many / item " + i);
			if (i == 1) ends[0] = child;
			if (i == 40) ends[1] = child;
		}
		tree.setRedraw(true);
		org.eclipse.swt.widgets.Listener show = new org.eclipse.swt.widgets.Listener() {
			public void handleEvent(Event e) {
				TreeItem item = e.widget == showLast ? ends[1] : ends[0];
				tree.showItem(item);
				String msg = "showItem(" + item.getText() + "): at " + item.getBounds() + " in a tree " + tree.getClientArea().height + " high";
				say(msg);
				log.setText(msg);
			}
		};
		showLast.addListener(SWT.Selection, show);
		showFirst.addListener(SWT.Selection, show);
		tree.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String msg = "Selection: " + ((TreeItem) e.item).getText() + " (" + tree.getSelectionCount() + " selected)";
				say(msg);
				log.setText(msg);
			}
			public void widgetDefaultSelected(SelectionEvent e) {
				String msg = "DefaultSelection: " + ((TreeItem) e.item).getText();
				say(msg);
				log.setText(msg);
			}
		});
		tree.addTreeListener(new TreeAdapter() {
			public void treeExpanded(TreeEvent e) { say("Expand: " + ((TreeItem) e.item).getText()); }
			public void treeCollapsed(TreeEvent e) { say("Collapse: " + ((TreeItem) e.item).getText()); }
		});
		shell.addListener(SWT.Resize, new org.eclipse.swt.widgets.Listener() {
			public void handleEvent(Event e) {
				layoutInteractive(tree, log, showLast, showFirst);
			}
		});
		shell.setSize(420, 340);
		setTitle(null);
	}

	/* The tree on top, then a row with the buttons, and the log at the bottom */
	void layoutInteractive(Tree tree, Text log, Button showLast, Button showFirst) {
		Rectangle area = shell.getClientArea();
		int logHeight = 40, buttonHeight = 26;
		int treeHeight = Math.max(20, area.height - logHeight - buttonHeight);
		tree.setBounds(0, 0, area.width, treeHeight);
		showLast.setBounds(0, treeHeight, 130, buttonHeight);
		showFirst.setBounds(134, treeHeight, 130, buttonHeight);
		log.setBounds(0, treeHeight + buttonHeight, area.width, logHeight);
	}

	protected void done() {
		if (image != null) image.dispose();
		super.done();
	}

	void setTitle(String title) {
		super.setTitle(title);
	}

	public static void main(String[] args) {
		go(new SWT011_01());
	}

}
