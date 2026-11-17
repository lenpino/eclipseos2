/*
 *  SWT026_02.java
 */

/*
 * Copyright (c) 2002, 2026 EclipseOS2 Team.
 * This file is made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 */

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.internal.pm.OS;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

/**
 *  Validates that sibling controls clip each other when they paint.
 *
 *  The views of Eclipse showed gray (2026-11-17): the ViewForm of a view is a
 *  sibling placed over its CTabFolder, and without WS_CLIPSIBLINGS the folder
 *  painted over the whole view each time it was repainted (at startup, when
 *  the window was maximized, when a menu above it closed).
 *
 *  All the checks are automatic and read the screen: a big red canvas below,
 *  a green canvas and a tree (white) over it as siblings, on a black shell.
 *
 *  To close the window automatically (for unattended runs) pass the system
 *  property swt.test.autoclose=<milliseconds> to the JVM.
 */
public class SWT026_02 extends SWTTestCase {

	static {
		STEP = "026";
		TEST = "02";
		DESC = "Siblings clip each other";
	}

	static final int RED = 0xFF0000, GREEN = 0x00FF00, WHITE = 0xFFFFFF;

	int checks;

	Shell createTopShell(Display display) {
		return new Shell(display, SWT.SHELL_TRIM | SWT.NO_REDRAW_RESIZE);
	}

	void check(boolean ok, String what) {
		checks++;
		say((ok ? "  ok    " : "  FAIL  ") + what);
		if (!ok) throw new RuntimeException("Check failed: " + what);
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

	Canvas filled(Shell s, final int rgb) {
		Canvas canvas = new Canvas(s, SWT.NO_BACKGROUND);
		canvas.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				org.eclipse.swt.graphics.Color color = new org.eclipse.swt.graphics.Color(display, rgb >> 16, (rgb >> 8) & 0xFF, rgb & 0xFF);
				e.gc.setBackground(color);
				e.gc.fillRectangle(0, 0, 1000, 1000);
				color.dispose();
			}
		});
		return canvas;
	}

	void checkSiblings() {
		saySubObjective("A sibling repainted below does not paint over the ones above");
		Shell s = new Shell(display, SWT.NO_TRIM);
		s.setBackground(display.getSystemColor(SWT.COLOR_BLACK));
		s.setBounds(20, 20, 300, 200);
		Canvas below = filled(s, RED);
		below.setBounds(0, 0, 300, 200);
		Canvas above = filled(s, GREEN);
		above.setBounds(20, 20, 100, 60);
		above.moveAbove(below);
		Tree tree = new Tree(s, SWT.NONE);
		new TreeItem(tree, SWT.NONE).setText("Item");
		tree.setBounds(150, 20, 120, 150);
		tree.moveAbove(below);
		s.open();
		pump(s);
		check(pel(above, 50, 30) == GREEN, "the canvas above is green after opening: " + Integer.toHexString(pel(above, 50, 30)));
		check(pel(tree, 60, 120) == WHITE, "the tree above is white after opening: " + Integer.toHexString(pel(tree, 60, 120)));
		check(pel(below, 200, 190) == RED, "the canvas below is red around them");

		below.redraw();
		pump(below);
		check(pel(above, 50, 30) == GREEN, "repainting the canvas below leaves the canvas above green: " + Integer.toHexString(pel(above, 50, 30)));
		check(pel(tree, 60, 120) == WHITE, "repainting the canvas below leaves the tree above white: " + Integer.toHexString(pel(tree, 60, 120)));

		s.redraw();
		pump(s);
		check(pel(above, 50, 30) == GREEN && pel(tree, 60, 120) == WHITE, "repainting the shell leaves both as they were");
		check((OS.WinQueryWindowULong(below.handle, OS.QWL_STYLE) & OS.WS_CLIPSIBLINGS) != 0,
				"PM: controls have WS_CLIPSIBLINGS (as on Windows)");
		s.dispose();
	}

	void initComponents() {
		sayObjective("Automatic checks");
		checkSiblings();
		say("\n" + checks + " checks passed.\n");
		shell.setSize(200, 100);
		setTitle(null);
	}

	public static void main(String[] args) {
		go(new SWT026_02());
	}
}
