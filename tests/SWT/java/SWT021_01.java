/*
 *  SWT021_01.java
 */

/*
 * Copyright (c) 2002, 2026 EclipseOS2 Team.
 * This file is made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 */

import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.pm.OS;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

/**
 *  Validates the Help event (F1).
 *
 *  The first part is automatic: the key F1 is posted to the controls as the
 *  system does (PM does not make a WM_HELP of a posted key), and WM_HELP is
 *  sent as PM may send it, from the keyboard and from a menu. The second part
 *  is interactive: press F1 on the controls and in the menu, and watch the
 *  events printed to the console and to the text at the bottom.
 *
 *  To close the window automatically (for unattended runs) pass the system
 *  property swt.test.autoclose=<milliseconds> to the JVM.
 */
public class SWT021_01 extends SWTTestCase {

	static {
		STEP = "021";
		TEST = "01";
		DESC = "Help event";
	}

	int checks;
	String helps = "";

	Shell createTopShell(Display display) {
		return new Shell(display, SWT.SHELL_TRIM);
	}

	void check(boolean ok, String what) {
		checks++;
		say((ok ? "  ok    " : "  FAIL  ") + what);
		if (!ok) throw new RuntimeException("Check failed: " + what);
	}

	void pump() {
		OS.WinPostMsg(shell.handle, OS.WM_NULL, 0, 0);
		for (int i = 0; i < 20; i++) while (display.readAndDispatch()) {}
	}

	/* Each Help event is noted with the name in the data of the widget */
	Listener helpListener = new Listener() {
		public void handleEvent(Event e) {
			helps += e.widget.getData() + " ";
		}
	};

	Widget named(Widget widget, String name) {
		widget.setData(name);
		return widget;
	}

	/* A key posted to a control: WM_CHAR with a virtual key, its scan code and the flags */
	void key(Widget control, int virtualKey, int scanCode, int flags) {
		int handle = control instanceof Shell ? ((Shell) control).handle : ((org.eclipse.swt.widgets.Control) control).handle;
		OS.WinPostMsg(handle, OS.WM_CHAR, OS.KC_VIRTUALKEY | OS.KC_SCANCODE | flags | (1 << 16) | (scanCode << 24), virtualKey << 16);
		pump();
	}

	void f1(Widget control) {
		key(control, OS.VK_F1, 0x3B, 0);
	}

	String take() {
		String result = helps.trim();
		helps = "";
		return result;
	}

	int idOf(MenuItem item) {
		try {
			java.lang.reflect.Field f = MenuItem.class.getDeclaredField("id");
			f.setAccessible(true);
			return ((Integer) f.get(item)).intValue();
		} catch (Exception e) {
			throw new RuntimeException(e.toString());
		}
	}

	void checkKey() {
		saySubObjective("F1 on the controls");
		Shell window = (Shell) named(new Shell(display), "shell");
		window.setLayout(new GridLayout());
		Composite group = (Composite) named(new Composite(window, SWT.NONE), "group");
		group.setLayout(new GridLayout());
		Button plain = (Button) named(new Button(group, SWT.PUSH), "plain");
		plain.setText("No listener");
		Button helped = (Button) named(new Button(group, SWT.PUSH), "helped");
		helped.setText("Listener");
		Button alone = (Button) named(new Button(window, SWT.PUSH), "alone");
		alone.setText("In the shell");
		window.pack();
		window.open();
		final int[] keys = new int[1];
		plain.addListener(SWT.KeyDown, new Listener() {
			public void handleEvent(Event e) { if (e.keyCode == SWT.F1) keys[0]++; }
		});
		pump();
		f1(plain);
		check(take().equals(""), "F1 without listeners sends nothing");
		check(keys[0] == 1, "and the KeyDown of F1 still comes: " + keys[0]);
		window.addListener(SWT.Help, helpListener);
		group.addListener(SWT.Help, helpListener);
		helped.addListener(SWT.Help, helpListener);
		plain.setFocus();
		pump();
		f1(plain);
		check(take().equals("group"), "F1 on a control without a listener: the first parent that has one");
		helped.setFocus();
		pump();
		f1(helped);
		check(take().equals("helped"), "F1 on a control with a listener: only that one");
		alone.setFocus();
		pump();
		f1(alone);
		check(take().equals("shell"), "a control right in the shell: the shell");
		plain.setFocus();
		pump();
		key(plain, OS.VK_F1, 0x3B, OS.KC_SHIFT);
		key(plain, OS.VK_F1, 0x3B, OS.KC_CTRL);
		key(plain, OS.VK_F1, 0x3B, OS.KC_KEYUP);
		key(plain, OS.VK_F1, 0x3B, OS.KC_PREVDOWN);
		check(take().equals(""), "Shift or Ctrl with F1, the key going up and its repetition send nothing");
		key(plain, OS.VK_F2, 0x3C, 0);
		check(take().equals(""), "nor another key");
		window.dispose();
	}

	void checkMessage() {
		saySubObjective("WM_HELP, from the keyboard and from a menu");
		Shell window = (Shell) named(new Shell(display), "shell");
		window.setLayout(new GridLayout());
		Composite group = (Composite) named(new Composite(window, SWT.NONE), "group");
		group.setLayout(new GridLayout());
		Text text = (Text) named(new Text(group, SWT.BORDER), "text");
		Menu bar = (Menu) named(new Menu(window, SWT.BAR), "bar");
		window.setMenuBar(bar);
		MenuItem file = (MenuItem) named(new MenuItem(bar, SWT.CASCADE), "file");
		file.setText("File");
		Menu menu = (Menu) named(new Menu(window, SWT.DROP_DOWN), "menu");
		file.setMenu(menu);
		MenuItem open = (MenuItem) named(new MenuItem(menu, SWT.PUSH), "open");
		open.setText("Open");
		MenuItem close = (MenuItem) named(new MenuItem(menu, SWT.PUSH), "close");
		close.setText("Close");
		window.pack();
		window.open();
		window.addListener(SWT.Help, helpListener);
		group.addListener(SWT.Help, helpListener);
		open.addListener(SWT.Help, helpListener);
		menu.addListener(SWT.Help, helpListener);
		text.setFocus();
		pump();
		boolean focused = display.getFocusControl() == text;
		/* WM_HELP: mp1 = (usCmd, 0), mp2 = (usSource, fPointer) */
		OS.WinSendMsg(window.handle, OS.WM_HELP, 0, OS.CMDSRC_ACCELERATOR);
		pump();
		String expected = focused ? "group" : "shell";
		check(take().equals(expected), "WM_HELP of the keyboard goes to the control with the focus or its parents: " + expected + (focused ? "" : " (the window has no focus)"));
		OS.WinSendMsg(window.handle, OS.WM_HELP, idOf(open), OS.CMDSRC_MENU);
		pump();
		check(take().equals("open"), "WM_HELP of a menu item with a listener: the item");
		OS.WinSendMsg(window.handle, OS.WM_HELP, idOf(close), OS.CMDSRC_MENU);
		pump();
		check(take().equals("menu"), "of an item without one: its menu");
		close.setEnabled(false);
		OS.WinSendMsg(window.handle, OS.WM_HELP, idOf(close), OS.CMDSRC_MENU);
		pump();
		check(take().equals(""), "of a disabled item: nothing");
		window.dispose();
	}

	void initComponents() {
		sayObjective("Automatic checks");
		checkKey();
		checkMessage();
		say("\n" + checks + " checks passed.\n");

		sayObjective("Interactive: press F1 on each control and on the items of the menu");
		shell.setLayout(new GridLayout());
		final Text log = new Text(shell, SWT.BORDER | SWT.READ_ONLY);
		Listener show = new Listener() {
			public void handleEvent(Event e) {
				String msg = "Help: " + e.widget.getData();
				say(msg);
				log.setText(msg);
			}
		};
		Composite group = (Composite) named(new Composite(shell, SWT.BORDER), "the group (for the button without a listener)");
		group.setLayout(new GridLayout());
		group.addListener(SWT.Help, show);
		new Button(group, SWT.PUSH).setText("Without a listener");
		Button with = (Button) named(new Button(group, SWT.PUSH), "the button with a listener");
		with.setText("With a listener");
		with.addListener(SWT.Help, show);
		Text text = (Text) named(new Text(shell, SWT.BORDER), "the shell (for the text)");
		text.setText("A text right in the shell");
		shell.setData("the shell");
		shell.addListener(SWT.Help, show);
		Menu bar = new Menu(shell, SWT.BAR);
		shell.setMenuBar(bar);
		MenuItem file = new MenuItem(bar, SWT.CASCADE);
		file.setText("&File");
		Menu menu = (Menu) named(new Menu(shell, SWT.DROP_DOWN), "the menu File (for Close)");
		file.setMenu(menu);
		menu.addListener(SWT.Help, show);
		MenuItem open = (MenuItem) named(new MenuItem(menu, SWT.PUSH), "the item Open");
		open.setText("&Open");
		open.addListener(SWT.Help, show);
		new MenuItem(menu, SWT.PUSH).setText("&Close");
		log.setLayoutData(new org.eclipse.swt.layout.GridData(org.eclipse.swt.layout.GridData.FILL_HORIZONTAL));
		shell.setSize(360, 240);
	}

	void setTitle(String title) {
		super.setTitle(title);
	}

	public static void main(String[] args) {
		go(new SWT021_01());
	}

}
