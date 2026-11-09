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
		/* The container always keeps one selected record: the first one that is inserted */
		check(single.getSelectionCount() == 1 && single.getSelection()[0] == a, "the first item is selected at first (container behavior)");
		single.setSelection(new TreeItem[] {b});
		check(single.getSelectionCount() == 1 && single.getSelection()[0] == b, "setSelection / getSelection");
		single.setSelection(new TreeItem[] {a, b});
		check(single.getSelectionCount() == 1 && single.getSelection()[0] == a, "a single selection tree keeps the first item only");
		single.deselectAll();
		check(single.getSelectionCount() <= 1, "deselectAll leaves at most the one selection the container keeps");
		single.selectAll();
		check(single.getSelectionCount() == 1, "selectAll does nothing in a single selection tree");
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
		/* The tree view of the container selects one record at a time, also by program */
		multi.selectAll();
		check(multi.getSelectionCount() >= 1, "selectAll leaves a selection: " + multi.getSelectionCount());
		multi.setSelection(new TreeItem[] {m2, m11});
		check(multi.getSelectionCount() >= 1 && multi.getSelection()[0] != null, "setSelection with two items: " + multi.getSelectionCount() + " selected");
		multi.setSelection(new TreeItem[] {m11});
		check(multi.getSelectionCount() == 1 && multi.getSelection()[0] == m11, "setSelection with one item");
		multi.dispose();
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
		checkEvents();
		checkDispose();
		checkImagesAndColors();
		say("\n" + checks + " checks passed.\n");

		sayObjective("Interactive: click, double click, press Enter, use the plus signs");
		shell.setLayout(null);
		final Tree tree = new Tree(shell, SWT.SINGLE | SWT.BORDER);
		final Text log = new Text(shell, SWT.BORDER | SWT.MULTI | SWT.READ_ONLY);
		/* Size first, and no redrawing while the items are added: nothing is painted twice */
		Rectangle area = shell.getClientArea();
		tree.setBounds(0, 0, area.width, Math.max(20, area.height - 40));
		log.setBounds(0, Math.max(20, area.height - 40), area.width, 40);
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
				if (j == 1) {
					TreeItem grandchild = new TreeItem(child, SWT.NONE);
					grandchild.setText("a grandchild");
					grandchild.setImage(image);
				}
			}
		}
		tree.setRedraw(true);
		tree.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String msg = "Selection: " + ((TreeItem) e.item).getText();
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
				Rectangle area = shell.getClientArea();
				int logHeight = 40;
				tree.setBounds(0, 0, area.width, Math.max(20, area.height - logHeight));
				log.setBounds(0, Math.max(20, area.height - logHeight), area.width, logHeight);
			}
		});
		shell.setSize(420, 340);
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
		go(new SWT011_01());
	}

}
