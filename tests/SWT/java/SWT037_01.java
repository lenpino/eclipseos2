/*
 *  SWT037_01.java
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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

/**
 *  Accelerators of the menus: the table is made with the first key that the window gets
 *  (Decorations.createAcceleratorTable, WinCreateAccelTable). The native function wrote the
 *  entries after a one entry ACCELTABLE on the stack and set the fields back through a NULL
 *  pointer: with accelerators the process ended with a trap (Eclipse, 60 accelerators, died
 *  with the first key pressed).
 */
public class SWT037_01 extends SWTTestCase {

	static {
		STEP = "037";
		TEST = "01";
		DESC = "Menu accelerators";
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
	MenuItem[] items;

	/* A key as the keyboard makes it: down and up, virtual key in the high word of mp2 */
	void key(int vk, int modifiers) {
		int fs = OS.KC_VIRTUALKEY | modifiers;
		OS.WinPostMsg(shell.handle, OS.WM_CHAR, fs | (1 << 16), vk << 16);
		OS.WinPostMsg(shell.handle, OS.WM_CHAR, fs | OS.KC_KEYUP | OS.KC_PREVDOWN | (1 << 16), vk << 16);
	}

	void initComponents() {
		sayObjective("Automatic checks");
		shell.setSize(300, 200);
		Menu bar = new Menu(shell, SWT.BAR);
		shell.setMenuBar(bar);
		MenuItem file = new MenuItem(bar, SWT.CASCADE);
		file.setText("&File");
		Menu menu = new Menu(shell, SWT.DROP_DOWN);
		file.setMenu(menu);
		/* F1 to F12 alone, with Ctrl and with Shift: 36 accelerators */
		int[] keys = {SWT.F1, SWT.F2, SWT.F3, SWT.F4, SWT.F5, SWT.F6, SWT.F7, SWT.F8, SWT.F9, SWT.F10, SWT.F11, SWT.F12};
		int[] mods = {0, SWT.CTRL, SWT.SHIFT};
		items = new MenuItem[keys.length * mods.length];
		for (int m = 0; m < mods.length; m++) {
			for (int k = 0; k < keys.length; k++) {
				int n = m * keys.length + k;
				items[n] = new MenuItem(menu, SWT.PUSH);
				items[n].setText("Item " + n);
				items[n].setAccelerator(mods[m] | keys[k]);
			}
		}
		shell.open();
		for (int i = 0; i < 30; i++) while (display.readAndDispatch()) {}
		/* The table is made in the event loop (not by the first key): the window answers its size and its handle */
		int n = OS.WinSendMsg(shell.handle, OS.WM_USER, 0, 0);
		int h = OS.WinSendMsg(shell.handle, OS.WM_USER + 1, 0, 0);
		check(n == items.length, "the table has the " + items.length + " accelerators: " + n);
		check(h != 0 && h != -1, "and a handle: " + Integer.toHexString(h));
		/* A key goes through WinTranslateAccel: the process must be alive after it (the selection is seen with real keys, DiagAccel) */
		key(OS.VK_F5, 0);
		for (int i = 0; i < 30; i++) while (display.readAndDispatch()) {}
		check(true, "a key went through the table (the process is alive)");
		say("\n" + checks + " checks, " + failures + " failed.\n");
		if (failures > 0) throw new RuntimeException(failures + " checks failed");
		setTitle(null);
	}

	public static void main(String[] args) {
		go(new SWT037_01());
	}
}
