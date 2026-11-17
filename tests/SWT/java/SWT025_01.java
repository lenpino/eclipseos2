/*
 *  SWT025_01.java
 */

/*
 * Copyright (c) 2002, 2026 EclipseOS2 Team.
 * This file is made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 */

import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.pm.MENUITEM;
import org.eclipse.swt.internal.pm.OS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

/**
 *  Validates MenuItem.setMenu (null) and the disposal of menus with submenus.
 *
 *  Eclipse hung at startup (2026-11-17): MenuManager disposed a cascade item,
 *  MenuItem.setMenu (null) deleted the item with MM_DELETEITEM, which destroys
 *  the window of the submenu, and Menu.getItems then looped for ever on that
 *  window (MM_ITEMIDFROMPOSITION answers 0, never MIT_ERROR).
 *
 *  All the checks are automatic. The first ones ask PM whether the window of
 *  the submenu is still alive before any call that would hang on a dead one,
 *  so the testcase fails instead of hanging. The window then shows a menu bar
 *  built again after the checks: open its menus by hand.
 *
 *  To close the window automatically (for unattended runs) pass the system
 *  property swt.test.autoclose=<milliseconds> to the JVM.
 */
public class SWT025_01 extends SWTTestCase {

	static {
		STEP = "025";
		TEST = "01";
		DESC = "Menu dispose and setMenu (null)";
	}

	int checks;

	Shell createTopShell(Display display) {
		return new Shell(display, SWT.SHELL_TRIM | SWT.NO_REDRAW_RESIZE);
	}

	void check(boolean ok, String what) {
		checks++;
		say((ok ? "  ok    " : "  FAIL  ") + what);
		if (!ok) throw new RuntimeException("Check failed: " + what);
	}

	boolean alive(Menu menu) {
		return OS.WinIsWindow(0, menu.handle);
	}

	/* What PM keeps for the item at a position of a menu */
	MENUITEM itemAt(Menu menu, int position) {
		int id = OS.WinSendMsg(menu.handle, OS.MM_ITEMIDFROMPOSITION, position, 0);
		MENUITEM info = new MENUITEM();
		OS.WinSendMsg(menu.handle, OS.MM_QUERYITEM, OS.MPFROM2SHORT((short)id, (short)OS.FALSE), info);
		return info;
	}

	void checkSetMenuNull(Shell s) {
		saySubObjective("setMenu (null) keeps the submenu");
		Menu bar = new Menu(s, SWT.BAR);
		s.setMenuBar(bar);
		MenuItem first = new MenuItem(bar, SWT.PUSH);
		first.setText("First");
		MenuItem cascade = new MenuItem(bar, SWT.CASCADE);
		cascade.setText("File");
		MenuItem last = new MenuItem(bar, SWT.PUSH);
		last.setText("Last");
		Menu sub = new Menu(s, SWT.DROP_DOWN);
		cascade.setMenu(sub);
		new MenuItem(sub, SWT.PUSH).setText("One");
		new MenuItem(sub, SWT.PUSH).setText("Two");
		check(alive(sub) && sub.getItems().length == 2, "the submenu has its window and two items");
		MENUITEM info = itemAt(bar, 1);
		check((info.afStyle & OS.MIS_SUBMENU) != 0 && info.hwndSubMenu == sub.handle,
				"PM: the cascade item points to the submenu");

		cascade.setMenu(null);
		/* Before any call that would loop on a dead window */
		check(alive(sub), "PM: the window of the submenu survives setMenu (null)");
		check(sub.getItems().length == 2, "the submenu keeps its two items");
		check(cascade.getMenu() == null && sub.getParentItem() == null, "getMenu and getParentItem are null");
		info = itemAt(bar, 1);
		check((info.afStyle & OS.MIS_SUBMENU) == 0 && info.hwndSubMenu == 0,
				"PM: the cascade item no longer points to a submenu");
		check(bar.getItemCount() == 3 && bar.getItem(1) == cascade && bar.indexOf(last) == 2,
				"the bar keeps its items in order");
		check("File".equals(cascade.getText()), "the cascade item keeps its text");

		cascade.setMenu(sub);
		info = itemAt(bar, 1);
		check(cascade.getMenu() == sub && (info.afStyle & OS.MIS_SUBMENU) != 0 && info.hwndSubMenu == sub.handle,
				"the same submenu can be set again");
		Menu other = new Menu(s, SWT.DROP_DOWN);
		new MenuItem(other, SWT.PUSH).setText("Other");
		cascade.setMenu(other);
		check(alive(sub) && sub.getItems().length == 2 && sub.getParentItem() == null,
				"replacing the submenu keeps the old one alive and detached");
		check(itemAt(bar, 1).hwndSubMenu == other.handle, "PM: the cascade item points to the new submenu");
		s.setMenuBar(null);
		bar.dispose();
		check(other.isDisposed() && cascade.isDisposed(), "disposing the bar disposes its items and their submenus");
		check(alive(sub) && sub.getItems().length == 2, "PM: a detached submenu survives the disposal of the bar");
		sub.dispose();
		check(sub.isDisposed(), "a detached submenu is disposed alone");
	}

	void checkDispose(Shell s) {
		saySubObjective("Disposal of menus with submenus");
		Menu bar = new Menu(s, SWT.BAR);
		s.setMenuBar(bar);
		MenuItem cascade = new MenuItem(bar, SWT.CASCADE);
		cascade.setText("File");
		Menu sub = new Menu(s, SWT.DROP_DOWN);
		cascade.setMenu(sub);
		new MenuItem(sub, SWT.PUSH).setText("One");
		MenuItem nested = new MenuItem(sub, SWT.CASCADE);
		nested.setText("More");
		Menu subsub = new Menu(s, SWT.DROP_DOWN);
		nested.setMenu(subsub);
		new MenuItem(subsub, SWT.PUSH).setText("Deep");

		sub.dispose();
		check(sub.isDisposed() && subsub.isDisposed() && nested.isDisposed(), "disposing a submenu disposes its items and their submenus");
		check(!cascade.isDisposed() && cascade.getMenu() == null, "its cascade item stays, without a menu");
		check((itemAt(bar, 0).afStyle & OS.MIS_SUBMENU) == 0, "PM: the cascade item no longer points to a submenu");

		Menu again = new Menu(s, SWT.DROP_DOWN);
		new MenuItem(again, SWT.PUSH).setText("Again");
		cascade.setMenu(again);
		cascade.dispose();
		check(cascade.isDisposed() && again.isDisposed(), "disposing a cascade item disposes its submenu (as MenuManager does)");
		check(bar.getItemCount() == 0, "the bar has no items left");

		MenuItem item = new MenuItem(bar, SWT.CASCADE);
		item.setText("Edit");
		Menu menu = new Menu(s, SWT.DROP_DOWN);
		item.setMenu(menu);
		new MenuItem(menu, SWT.PUSH).setText("Copy");
		OS.WinDestroyWindow(menu.handle);
		check(menu.getItems().length == 0, "getItems on a window destroyed behind its back returns (no endless loop)");
		s.setMenuBar(null);
		bar.dispose();
		check(menu.isDisposed(), "and the menu can still be disposed");

		Shell other = new Shell(display);
		Menu otherBar = new Menu(other, SWT.BAR);
		other.setMenuBar(otherBar);
		MenuItem otherItem = new MenuItem(otherBar, SWT.CASCADE);
		Menu otherSub = new Menu(other, SWT.DROP_DOWN);
		otherItem.setMenu(otherSub);
		new MenuItem(otherSub, SWT.PUSH).setText("Item");
		other.dispose();
		check(otherBar.isDisposed() && otherSub.isDisposed(), "disposing a shell disposes its menu bar and submenus");
	}

	void initComponents() {
		sayObjective("Automatic checks");
		Shell s = new Shell(display);
		checkSetMenuNull(s);
		checkDispose(s);
		s.dispose();
		say("\n" + checks + " checks passed.\n");

		sayObjective("Interactive: open the menus (File has a submenu with a nested one)");
		Menu bar = new Menu(shell, SWT.BAR);
		shell.setMenuBar(bar);
		MenuItem file = new MenuItem(bar, SWT.CASCADE);
		file.setText("&File");
		Menu sub = new Menu(shell, SWT.DROP_DOWN);
		file.setMenu(sub);
		new MenuItem(sub, SWT.PUSH).setText("&Open");
		MenuItem more = new MenuItem(sub, SWT.CASCADE);
		more.setText("&More");
		Menu subsub = new Menu(shell, SWT.DROP_DOWN);
		more.setMenu(subsub);
		new MenuItem(subsub, SWT.PUSH).setText("&Deep");
		/* Detached and attached again: must still open */
		file.setMenu(null);
		file.setMenu(sub);
		new MenuItem(bar, SWT.PUSH).setText("&Help");
		shell.setSize(300, 200);
		setTitle(null);
	}

	public static void main(String[] args) {
		go(new SWT025_01());
	}
}
