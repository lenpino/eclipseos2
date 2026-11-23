/*
 *  SWT040_01.java
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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;

/**
 *  List bounds: a list box of PM snaps its height to whole rows and moves when it is given a size
 *  (a Windows list has LBS_NOINTEGRALHEIGHT): setBounds(10, 10, 100, 60) gave {9, 11, 104, 60} and
 *  setSize(120, 80) gave {8, 18, 124, 74}. The bounds of a list have to be the ones it is given.
 */
public class SWT040_01 extends SWTTestCase {

	static {
		STEP = "040";
		TEST = "01";
		DESC = "List bounds";
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
	int pixel(Control c, int x, int y) {
		org.eclipse.swt.graphics.Point p = c.toDisplay(new org.eclipse.swt.graphics.Point(x, y));
		int hps = OS.WinGetScreenPS(OS.HWND_DESKTOP);
		OS.GpiCreateLogColorTable(hps, 0, OS.LCOLF_RGB, 0, 0, null);
		int pel = OS.GpiQueryPel(hps, new int[] {p.x, OS.WinQuerySysValue(OS.HWND_DESKTOP, OS.SV_CYSCREEN) - 1 - p.y}) & 0xFFFFFF;
		OS.WinReleasePS(hps);
		return pel;
	}

	void settle() {
		for (int i = 0; i < 30; i++) while (display.readAndDispatch()) {}
	}

	void checkBounds(String name, List list, int x, int y, int w, int h) {
		list.setBounds(x, y, w, h);
		settle();
		Rectangle b = list.getBounds();
		check(b.x == x && b.y == y && b.width == w && b.height == h, name + ": setBounds(" + x + ", " + y + ", " + w + ", " + h + ") gives " + b);
	}

	void initComponents() {
		sayObjective("Automatic checks");
		shell.setSize(500, 400);
		shell.open();
		settle();
		int[] styles = {SWT.BORDER, SWT.NONE, SWT.BORDER | SWT.V_SCROLL, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL, SWT.BORDER | SWT.MULTI};
		String[] names = {"BORDER", "no style", "BORDER | V_SCROLL", "BORDER | H_SCROLL | V_SCROLL", "BORDER | MULTI"};
		for (int s = 0; s < styles.length; s++) {
			List list = new List(shell, styles[s]);
			for (int i = 0; i < 20; i++) list.add("Item " + i);
			checkBounds(names[s], list, 10, 10, 100, 60);
			checkBounds(names[s], list, 30, 40, 120, 80);
			checkBounds(names[s], list, 5, 7, 83, 47);
			checkBounds(names[s], list, 5, 7, 83, 47);
			Point size = list.getSize();
			list.setSize(160, 101);
			settle();
			check(list.getSize().x == 160 && list.getSize().y == 101, names[s] + ": setSize(160, 101) gives " + list.getSize());
			list.dispose();
		}
		/* The preferred size shows all the rows: the text of the last one is on the screen, inside the list */
		for (int s = 0; s < 3; s++) {
			List list = new List(shell, styles[s]);
			for (int i = 0; i < 5; i++) list.add("Row number " + i);
			list.setLocation(20, 20);
			list.pack();
			settle();
			Point size = list.getSize();
			int row = list.getItemHeight();
			say("  " + names[s] + ": packed to " + size + ", row " + row);
			check(size.y >= 5 * row, names[s] + ": packed high enough for 5 rows (" + size.y + " for " + (5 * row) + ")");
			check(size.y <= 5 * row + 8, names[s] + ": and not much more (" + size.y + ")");
			int face = pixel(list, 3, 3);
			boolean ink = false;
			for (int y = 4 * row + 1; y < 5 * row && !ink; y++) for (int x = 2; x < size.x - 2 && !ink; x++) if (pixel(list, x, y) != face && pixel(list, x, y) != 0xFFFFFF) ink = true;
			check(ink, names[s] + ": the last row has its text on the screen");
			list.dispose();
		}
		say("\n" + checks + " checks, " + failures + " failed.\n");
		if (failures > 0) throw new RuntimeException(failures + " checks failed");
		setTitle(null);
	}

	public static void main(String[] args) {
		go(new SWT040_01());
	}
}
