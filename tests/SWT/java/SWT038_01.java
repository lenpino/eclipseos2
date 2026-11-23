/*
 *  SWT038_01.java
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
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

/**
 *  MenuItem(parent, style, index): the item is inserted at the position (Menu.createItem used
 *  MIT_END for all of them). JFace removes the items of a menu and inserts them again at
 *  their places between the separators that it keeps: the separators ended up together under
 *  File > New, as empty rows.
 */
public class SWT038_01 extends SWTTestCase {

	static {
		STEP = "038";
		TEST = "01";
		DESC = "Menu items inserted at an index";
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
	String names(Menu menu) {
		MenuItem[] items = menu.getItems();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < items.length; i++) sb.append((items[i].getStyle() & SWT.SEPARATOR) != 0 ? "-" : items[i].getText()).append(' ');
		return sb.toString().trim();
	}

	void initComponents() {
		sayObjective("Automatic checks");
		shell.setSize(300, 200);
		Menu bar = new Menu(shell, SWT.BAR);
		shell.setMenuBar(bar);
		MenuItem file = new MenuItem(bar, SWT.CASCADE);
		file.setText("File");
		Menu menu = new Menu(shell, SWT.DROP_DOWN);
		file.setMenu(menu);
		new MenuItem(menu, SWT.PUSH).setText("C");
		new MenuItem(menu, SWT.PUSH).setText("E");
		check(names(menu).equals("C E"), "appended: " + names(menu));
		new MenuItem(menu, SWT.PUSH, 1).setText("D");
		check(names(menu).equals("C D E"), "inserted in the middle: " + names(menu));
		new MenuItem(menu, SWT.PUSH, 0).setText("A");
		check(names(menu).equals("A C D E"), "inserted at the start: " + names(menu));
		new MenuItem(menu, SWT.SEPARATOR, 1);
		check(names(menu).equals("A - C D E"), "a separator in the middle: " + names(menu));
		new MenuItem(menu, SWT.PUSH, menu.getItemCount()).setText("F");
		check(names(menu).equals("A - C D E F"), "inserted at the end (index = count): " + names(menu));
		/* As JFace updates a menu: remove the items, insert them again at their places between the separators that stay */
		MenuItem[] items = menu.getItems();
		for (int i = 0; i < items.length; i++) if ((items[i].getStyle() & SWT.SEPARATOR) == 0) items[i].dispose();
		check(names(menu).equals("-"), "only the separator is left: " + names(menu));
		new MenuItem(menu, SWT.PUSH, 0).setText("Before");
		new MenuItem(menu, SWT.PUSH, 2).setText("After");
		check(names(menu).equals("Before - After"), "the items go around the separator, not after it: " + names(menu));
		/* A cascade at an index */
		MenuItem sub = new MenuItem(menu, SWT.CASCADE, 1);
		sub.setText("Sub");
		check(names(menu).equals("Before Sub - After"), "a cascade item at an index: " + names(menu));
		check(menu.getItem(1) == sub && menu.indexOf(sub) == 1, "getItem and indexOf agree");
		say("\n" + checks + " checks, " + failures + " failed.\n");
		if (failures > 0) throw new RuntimeException(failures + " checks failed");
		setTitle(null);
	}

	public static void main(String[] args) {
		go(new SWT038_01());
	}
}
