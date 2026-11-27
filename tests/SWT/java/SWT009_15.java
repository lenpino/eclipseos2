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
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.internal.pm.OS;
import org.eclipse.swt.internal.pm.SWP;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

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

		checkContextMenus();
		say("\nAll checks passed.\n");
		/* SWTTestCase opens the shell after initComponents(), so close it afterwards */
		display.asyncExec(new Runnable() {
			public void run() { shell.dispose(); }
		});
	}

	void checkContextMenus() {
		shell.setBounds(80, 80, 420, 320);
		Canvas editor = new Canvas(shell, SWT.BORDER);
		editor.setBounds(10, 10, 180, 80);
		Tree tree = new Tree(shell, SWT.BORDER);
		tree.setBounds(10, 100, 180, 120);
		new TreeItem(tree, SWT.NONE).setText("Project");
		Table table = new Table(shell, SWT.BORDER);
		table.setBounds(200, 100, 180, 120);
		new TableColumn(table, SWT.NONE).setWidth(140);
		new TableItem(table, SWT.NONE).setText("Row");
		shell.open();
		int records = OS.WinWindowFromID(table.handle, OS.CID_LEFTDVWND);
		check(records != 0, "the table has a native detail window");
		checkContextMenu(editor, editor.handle, "editor");
		checkContextMenu(tree, tree.handle, "tree");
		checkContextMenu(table, records, "table detail window");
	}

	void checkContextMenu(Control control, int source, String name) {
		final Menu popup = new Menu(control);
		final int[] events = {0, 0};
		popup.addListener(SWT.Show, new Listener() {
			public void handleEvent(Event e) {
				events[0]++;
				new MenuItem(popup, SWT.PUSH).setText("Dynamic choice");
			}
		});
		popup.addListener(SWT.Hide, new Listener() {
			public void handleEvent(Event e) {
				events[1]++;
				for (int i = popup.getItemCount() - 1; i >= 0; i--) popup.getItem(i).dispose();
			}
		});
		control.setMenu(popup);
		int[] point = {25, 35};
		OS.WinMapWindowPoints(source, OS.HWND_DESKTOP, point, 1);
		int position = 25 | (35 << 16);
		int[] shellPoint = {25, 35};
		OS.WinMapWindowPoints(source, shell.handle, shellPoint, 1);
		int shellPosition = OS.MPFROM2SHORT((short) shellPoint[0], (short) shellPoint[1]);
		for (int i = 1; i <= 4; i++) {
			if (i != 1) {
				OS.WinPostMsg(source, OS.WM_BUTTON2DOWN, position, 0);
				if (i == 4) {
					/* As observed in Eclipse, PM routes the release to the shell. */
					OS.WinPostMsg(shell.handle, OS.WM_BUTTON2UP, shellPosition, 0);
				} else {
					OS.WinPostMsg(source, OS.WM_BUTTON2UP, position, 0);
				}
			}
			if (i == 1 || i == 3) OS.WinPostMsg(source, OS.WM_CONTEXTMENU, position, 0);
			while (display.readAndDispatch()) {}
			check(popup.getVisible() && events[0] == i, name + " opens and populates its context menu");
			SWP swp = new SWP();
			check(OS.WinQueryWindowPos(popup.handle, swp) && swp.x == point[0] && swp.y == point[1],
				name + " opens at the native pointer coordinates");
			int[] center = {swp.x + swp.cx / 2, swp.y + swp.cy / 2};
			int hit = OS.WinWindowFromPoint(OS.HWND_DESKTOP, center, true);
			check(hit == popup.handle, name + " menu is the top window at its on-screen position");
			popup.setVisible(false);
			while (display.readAndDispatch()) {}
			check(!popup.getVisible() && events[1] == i, name + " dismisses its context menu");
		}
		popup.dispose();
	}

	public static void main(String[] args) {
		go(new SWT009_15());
	}

}
