/*
 *  SWT034_01.java
 */

/*
 * Copyright (c) 2002, 2026 EclipseOS2 Team.
 * This file is made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 */

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.pm.OS;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

/**
 *  Tree items added after the shell is open (as the workbench creates its views): every
 *  row has to be painted after the events are dispatched, with no extra repaint.
 */
public class SWT034_01 extends SWTTestCase {

	static {
		STEP = "034";
		TEST = "01";
		DESC = "Tree items added after open";
	}

	static final String DIR = System.getProperty("user.dir") + "/tests/SWT/images/eclipse/";

	int checks, failures;

	Shell createTopShell(Display display) {
		return new Shell(display, SWT.SHELL_TRIM | SWT.NO_REDRAW_RESIZE);
	}

	void check(boolean ok, String what) {
		checks++;
		say((ok ? "  ok    " : "  FAIL  ") + what);
		if (!ok) failures++;
	}

	void pump(Control control) {
		OS.WinPostMsg(control.handle, OS.WM_NULL, 0, 0);
		for (int i = 0; i < 30; i++) while (display.readAndDispatch()) {}
		control.update();
		for (int i = 0; i < 30; i++) while (display.readAndDispatch()) {}
	}

	int pel(Control control, int x, int y) {
		Point p = control.toDisplay(new Point(x, y));
		int hps = OS.WinGetScreenPS(OS.HWND_DESKTOP);
		OS.GpiCreateLogColorTable(hps, 0, OS.LCOLF_RGB, 0, 0, null);
		int pel = OS.GpiQueryPel(hps, new int[] {p.x, OS.WinQuerySysValue(OS.HWND_DESKTOP, OS.SV_CYSCREEN) - 1 - p.y}) & 0xFFFFFF;
		OS.WinReleasePS(hps);
		return pel;
	}

	static int rgb(RGB c) {
		return (c.red << 16) | (c.green << 8) | c.blue;
	}

	/* The leftmost and rightmost x of the middle row of the bar that are of the given color */
	/* How many pixels of the row are not the background color of the tree */
	int ink(Tree tree, Rectangle row, int face) {
		int n = 0;
		for (int y = row.y; y < row.y + row.height; y++) for (int x = row.x; x < row.x + row.width && x < tree.getClientArea().width; x++) {
			if (pel(tree, x, y) != face) n++;
		}
		return n;
	}

	void checkRows(Tree tree, TreeItem[] items, String when) {
		int face = rgb(tree.getBackground().getRGB());
		for (int i = 0; i < items.length; i++) {
			Rectangle b = items[i].getBounds();
			Rectangle area = tree.getClientArea();
			boolean inside = b.y >= 0 && b.y + b.height <= area.height;
			int n = inside ? ink(tree, b, face) : 0;
			check(inside && n > 0, when + ": '" + items[i].getText() + "' is painted and visible (" + n + " pixels of ink in " + b + ", client area " + tree.getClientArea() + ")");
		}
	}

	void initComponents() {
		sayObjective("Automatic checks");
		shell.setLayout(null);
		shell.setSize(300, 300);
		shell.open();
		pump(shell);
		/*
		* As the views of the workbench (DiagView): the tree is created inside a composite with no size,
		* filled, and given its size afterwards by the layout.
		*/
		Composite box = new Composite(shell, SWT.NONE);
		Tree sized = new Tree(box, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		TreeItem[] unsized = new TreeItem[5];
		for (int i = 0; i < unsized.length; i++) {
			unsized[i] = new TreeItem(sized, SWT.NONE);
			unsized[i].setText("Item " + i);
		}
		box.setBounds(10, 10, 270, 120);
		sized.setBounds(1, 1, 268, 118);
		pump(sized);
		checkRows(sized, unsized, "items added before the tree has its size");
		box.dispose();
		pump(shell);
		/* As the workbench: the tree is created and filled after the window is open */
		Tree tree = new Tree(shell, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL);
		tree.setBounds(10, 10, 270, 250);
		TreeItem[] first = new TreeItem[4];
		for (int i = 0; i < first.length; i++) {
			first[i] = new TreeItem(tree, SWT.NONE);
			first[i].setText("First " + i);
		}
		pump(tree);
		checkRows(tree, first, "items added before the first paint");
		/* More items once the tree is on the screen */
		TreeItem[] later = new TreeItem[3];
		for (int i = 0; i < later.length; i++) {
			later[i] = new TreeItem(tree, SWT.NONE);
			later[i].setText("Later " + i);
		}
		pump(tree);
		checkRows(tree, later, "items added after the tree was painted");
		/* Children of an item, then expanded */
		TreeItem[] kids = new TreeItem[3];
		for (int i = 0; i < kids.length; i++) {
			kids[i] = new TreeItem(first[1], SWT.NONE);
			kids[i].setText("Child " + i);
		}
		first[1].setExpanded(true);
		pump(tree);
		checkRows(tree, kids, "children added and expanded");
		checkRows(tree, later, "items below the expanded one");
		/* Children added to an expanded item */
		TreeItem more = new TreeItem(first[1], SWT.NONE);
		more.setText("Child added to the expanded item");
		pump(tree);
		checkRows(tree, new TreeItem[] {more}, "child added to an expanded item");
		checkRows(tree, later, "items below, after adding a child");
		/* An item in the middle (index) */
		TreeItem mid = new TreeItem(tree, SWT.NONE, 1);
		mid.setText("Inserted at 1");
		pump(tree);
		checkRows(tree, new TreeItem[] {mid, first[1], later[0]}, "item inserted in the middle");
		say("\n" + checks + " checks, " + failures + " failed.\n");
		if (failures > 0) throw new RuntimeException(failures + " checks failed");
		setTitle(null);
	}

	public static void main(String[] args) {
		go(new SWT034_01());
	}
}
