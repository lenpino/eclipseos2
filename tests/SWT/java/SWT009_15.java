/*
 *  SWT009_15.java
 */

/*
 * Copyright (c) 2002, 2026 EclipseOS2 Team.
 * This file is made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 */

import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.pm.OS;
import org.eclipse.swt.internal.pm.SWP;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

/**
 *  Checks that a pop up menu is shown by Menu.setVisible(true), which was an
 *  empty method, and that it appears where setLocation() asks: the upper left
 *  corner of the menu at that point, in display coordinates.
 *
 *  The test is automatic: any failed check throws an exception.
 */
public class SWT009_15 extends SWTTestCase {

	static {
		STEP = "015";
		TEST = "09";
		DESC = "Pop up menu";
	}

	void check(boolean ok, String what) {
		say((ok ? "  ok    " : "  FAIL  ") + what);
		if (!ok) throw new RuntimeException("Check failed: " + what);
	}

	void initComponents() {
		Menu menu = new Menu(shell, SWT.POP_UP);
		for (int i = 0; i < 3; i++) {
			new MenuItem(menu, SWT.PUSH).setText("Choice " + i);
		}
		check(!menu.getVisible(), "the menu is not visible at first");

		int screenHeight = OS.WinQuerySysValue(OS.HWND_DESKTOP, OS.SV_CYSCREEN);
		menu.setLocation(300, 200);
		menu.setVisible(true);

		SWP swp = new SWP();
		check(OS.WinQueryWindowPos(menu.handle, swp), "the menu window can be queried");
		check(swp.cx > 0 && swp.cy > 0, "the menu has a size: " + swp.cx + "x" + swp.cy);
		check(swp.x == 300, "the left edge is at the requested x: " + swp.x);
		int top = screenHeight - (swp.y + swp.cy);
		check(top == 200, "the top edge is at the requested y: " + top);

		menu.setVisible(false);

		/* The menu does not go out of the screen: it opens upwards when it does not fit below */
		int screenWidth = OS.WinQuerySysValue(OS.HWND_DESKTOP, OS.SV_CXSCREEN);
		menu.setLocation(300, screenHeight - 10);
		menu.setVisible(true);
		check(OS.WinQueryWindowPos(menu.handle, swp), "the menu can be queried again after hiding it");
		check(swp.y >= 0 && swp.y + swp.cy <= screenHeight, "a menu near the bottom stays in the screen: y=" + swp.y);
		check(swp.y + swp.cy > 10 && swp.y < screenHeight - 10, "and it opens upwards from the point: y=" + swp.y);
		menu.setVisible(false);

		menu.setLocation(screenWidth - 5, 200);
		menu.setVisible(true);
		check(OS.WinQueryWindowPos(menu.handle, swp), "the menu can be queried a third time");
		check(swp.x >= 0 && swp.x + swp.cx <= screenWidth, "a menu near the right edge stays in the screen: x=" + swp.x);
		menu.setVisible(false);

		say("\nAll checks passed.\n");
		/* SWTTestCase opens the shell after initComponents(), so close it afterwards */
		display.asyncExec(new Runnable() {
			public void run() { shell.dispose(); }
		});
	}

	public static void main(String[] args) {
		go(new SWT009_15());
	}

}
