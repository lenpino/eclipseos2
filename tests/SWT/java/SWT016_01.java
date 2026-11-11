/*
 *  SWT016_01.java
 */

/*
 * Copyright (c) 2002, 2026 EclipseOS2 Team.
 * This file is made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 */

import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.internal.pm.OS;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

/**
 *  Validates the events of the mouse: MouseMove, MouseEnter, MouseExit,
 *  MouseHover, and the coordinates of MouseDown.
 *
 *  The first part is automatic: the mouse messages are sent to the controls
 *  and the events that come out are compared. The second part is
 *  interactive: move the pointer over the boxes and look at the list.
 *
 *  To close the window automatically (for unattended runs) pass the system
 *  property swt.test.autoclose=<milliseconds> to the JVM.
 */
public class SWT016_01 extends SWTTestCase {

	static {
		STEP = "016";
		TEST = "01";
		DESC = "Mouse events";
	}

	static final int[] TYPES = {SWT.MouseMove, SWT.MouseEnter, SWT.MouseExit, SWT.MouseHover, SWT.MouseDown, SWT.MouseUp};
	static final String[] NAMES = {"Move", "Enter", "Exit", "Hover", "Down", "Up"};

	int checks;
	Vector events = new Vector();
	Canvas a, b, inner;

	Shell createTopShell(Display display) {
		return new Shell(display, SWT.SHELL_TRIM | SWT.NO_REDRAW_RESIZE);
	}

	void check(boolean ok, String what) {
		checks++;
		say((ok ? "  ok    " : "  FAIL  ") + what);
		if (!ok) throw new RuntimeException("Check failed: " + what);
	}

	static String name(int type) {
		for (int i = 0; i < TYPES.length; i++) if (TYPES[i] == type) return NAMES[i];
		return "?";
	}

	/** Records "Type widget x,y" and, when there is a state, "+state". */
	Listener recorder = new Listener() {
		public void handleEvent(Event e) {
			String s = name(e.type) + " " + e.widget.getData() + " " + e.x + "," + e.y;
			if (e.stateMask != 0) s += " +" + Integer.toHexString(e.stateMask);
			events.addElement(s);
		}
	};

	void record(Control c, String name) {
		c.setData(name);
		for (int i = 0; i < TYPES.length; i++) c.addListener(TYPES[i], recorder);
	}

	/**
	 * Runs the event loop for a while: the posted events and the timers. The events
	 * are posted (as on Windows) and run after a message is dispatched; the messages
	 * of the test are sent, not posted, so a WM_NULL goes to the queue.
	 */
	void run(int milliseconds) {
		OS.WinPostMsg(shell.handle, OS.WM_NULL, 0, 0);
		long end = System.currentTimeMillis() + milliseconds;
		do {
			while (display.readAndDispatch()) {}
			try { Thread.sleep(10); } catch (InterruptedException e) {}
		} while (System.currentTimeMillis() < end);
		while (display.readAndDispatch()) {}
	}

	/** What happened since the last call, as one string. */
	String taken() {
		return taken(null);
	}

	/** The same, only with the events of the given types ("Move Down"): the timer of the display may add an Exit. */
	String taken(String types) {
		run(0);
		String s = "";
		for (int i = 0; i < events.size(); i++) {
			String e = (String) events.elementAt(i);
			if (types != null && (" " + types + " ").indexOf(" " + e.substring(0, e.indexOf(' ')) + " ") < 0) continue;
			s += (s.length() > 0 ? " | " : "") + e;
		}
		events.removeAllElements();
		return s;
	}

	/** A mouse message at a point of the control in SWT coordinates. */
	void send(Control c, int msg, int x, int y, int keys) {
		int height = c.getSize().y;
		/* The window of a Canvas has no border: its height is the one of getSize() */
		OS.WinSendMsg(c.handle, msg, (x & 0xFFFF) | ((height - 1 - y) << 16), keys << 16);
	}

	void move(Control c, int x, int y) {
		send(c, OS.WM_MOUSEMOVE, x, y, 0);
	}

	/** Puts the pointer of the screen there too, so that the timer of the display sees it over the control. */
	void moveTo(Control c, int x, int y) {
		org.eclipse.swt.graphics.Point p = c.toDisplay(new org.eclipse.swt.graphics.Point(x, y));
		OS.WinSetPointerPos(OS.HWND_DESKTOP, p.x, OS.WinQuerySysValue(OS.HWND_DESKTOP, OS.SV_CYSCREEN) - 1 - p.y);
		move(c, x, y);
	}

	void checkMove() {
		saySubObjective("MouseMove");
		move(a, 10, 20);
		taken();
		move(a, 11, 20);
		String s = taken();
		check(s.indexOf("Move a 11,20") >= 0 && taken("Move").length() == 0, "a move gives the position, with y counted from the top: " + s);
		move(a, 11, 20);
		check(taken("Move").equals(""), "the same position again gives nothing (PM repeats WM_MOUSEMOVE)");
		send(a, OS.WM_MOUSEMOVE, 12, 0, OS.KC_SHIFT | OS.KC_CTRL);
		s = taken("Move");
		check(s.equals("Move a 12,0 +" + Integer.toHexString(SWT.SHIFT | SWT.CONTROL)), "the top row is y 0, and the keys are in the state: " + s);
		move(a, 13, 59);
		s = taken("Move");
		check(s.equals("Move a 13,59"), "and the bottom row is the height less one: " + s);
	}

	void checkDown() {
		saySubObjective("MouseDown and MouseUp");
		move(a, 5, 0);
		taken();
		send(a, OS.WM_BUTTON1DOWN, 5, 0, 0);
		send(a, OS.WM_BUTTON1UP, 5, 0, 0);
		String s = taken("Down Up");
		check(s.startsWith("Down a 5,0") && s.indexOf("Up a 5,0") > 0, "the top row is y 0 also for the buttons (it was 1): " + s);
	}

	void checkEnterExit() {
		saySubObjective("MouseEnter and MouseExit between controls");
		/* The window is shown and the pointer goes where the messages say (see moveTo) */
		shell.setLocation(40, 40);
		shell.open();
		run(300);
		moveTo(a, 30, 30);
		taken();
		moveTo(b, 1, 2);
		String s = taken();
		check(s.startsWith("Exit a ") && s.indexOf("Enter b 1,2") > 0, "going to another control, the first one gets Exit and the second Enter: " + s);
		moveTo(b, 3, 2);
		moveTo(b, 4, 2);
		s = taken();
		check(s.equals("Move b 3,2 | Move b 4,2"), "Enter only once: " + s);
		moveTo(a, 30, 30);
		moveTo(b, 5, 5);
		s = taken();
		check(s.indexOf("Exit a") >= 0 && s.indexOf("Enter b 5,5") >= 0 && s.indexOf("Exit a") < s.indexOf("Enter b"), "Exit of the old one before Enter of the new one, at the position of the message: " + s);
		moveTo(inner, 2, 3);
		s = taken();
		check(s.startsWith("Exit b ") && s.indexOf("Enter inner 2,3") > 0, "going into a child is leaving the parent, as on Windows: " + s);
		moveTo(b, 6, 6);
		s = taken();
		check(s.startsWith("Exit inner ") && s.indexOf("Enter b 6,6") > 0, "and coming back is entering it again: " + s);
		run(300);
		check(taken().equals(""), "while the pointer stays over it, the timer sends nothing");
		/* The pointer leaves the window */
		org.eclipse.swt.graphics.Rectangle r = shell.getBounds();
		OS.WinSetPointerPos(OS.HWND_DESKTOP, r.x + r.width + 20, OS.WinQuerySysValue(OS.HWND_DESKTOP, OS.SV_CYSCREEN) - 1 - (r.y + r.height + 20));
		run(300);
		s = taken();
		check(s.startsWith("Exit b "), "when it goes out of the window, the timer sends Exit: " + s);
		shell.setVisible(false);
		run(100);
		taken();
	}

	void checkLeave() {
		saySubObjective("Leaving the program and resting on a control");
		move(a, 30, 30);
		/* A tick of the timer that was already due may bring the Exit at once: it counts too */
		String first = taken();
		/* The window is hidden again: the pointer of the screen is not over the control */
		run(300);
		String s = first + " | " + taken();
		check(s.indexOf("Exit a ") >= 0, "a hidden control loses the pointer too: " + s);
		run(500);
		check(taken().equals(""), "once");

		/* With the capture the control keeps the pointer, wherever it is */
		a.setCapture(true);
		move(a, 31, 30);
		taken();
		run(250);
		check(taken().equals(""), "no Hover before 400 ms");
		run(400);
		s = taken();
		check(s.startsWith("Hover a ") && s.indexOf("|") < 0, "a Hover when the pointer rests: " + s);
		run(600);
		check(taken().equals(""), "only one while it does not move");
		move(a, 32, 30);
		run(600);
		s = taken();
		check(s.startsWith("Move a 32,30 | Hover a "), "after a move, another one: " + s);
		a.setCapture(false);
		run(300);
		s = taken();
		check(s.startsWith("Exit a "), "without the capture, Exit: " + s);
	}

	/*
	 * The handles of windows that came from WinWindowFromPoint, WinBeginEnumWindows and
	 * WinGetNextWindow were cut to one byte: their C functions returned jboolean.
	 */
	void checkHandles() {
		saySubObjective("Natives that return a window");
		org.eclipse.swt.graphics.Point p = a.toDisplay(new org.eclipse.swt.graphics.Point(5, 5));
		shell.open();
		run(200);
		int[] pt = new int[] {p.x, OS.WinQuerySysValue(OS.HWND_DESKTOP, OS.SV_CYSCREEN) - 1 - p.y};
		int hwnd = OS.WinWindowFromPoint(OS.HWND_DESKTOP, pt, true);
		check(hwnd == a.handle, "WinWindowFromPoint gives the whole handle: " + Integer.toHexString(hwnd) + " " + Integer.toHexString(a.handle));
		OS.WinSetPointerPos(OS.HWND_DESKTOP, pt[0], pt[1]);
		check(display.getCursorControl() == a, "getCursorControl() is the control under the pointer (it used the text cursor): " + display.getCursorControl());
		shell.setVisible(false);
		run(100);
		taken();
		org.eclipse.swt.widgets.Composite holder = new org.eclipse.swt.widgets.Composite(shell, SWT.NONE);
		Canvas c1 = new Canvas(holder, SWT.NONE), c2 = new Canvas(holder, SWT.NONE), c3 = new Canvas(holder, SWT.NONE);
		Control[] before = holder.getChildren();
		c3.moveAbove(before[0]);
		check(holder.getChildren()[0] == c3, "moveAbove() of the first one puts it on top");
		c1.moveAbove(c2);
		Control[] now = holder.getChildren();
		int i1 = 0, i2 = 0;
		for (int i = 0; i < now.length; i++) { if (now[i] == c1) i1 = i; if (now[i] == c2) i2 = i; }
		check(i1 + 1 == i2, "moveAbove() of another one puts it just above (it used WinGetNextWindow)");
		c2.moveAbove(null);
		check(holder.getChildren()[0] == c2, "moveAbove(null) puts it on top");
		holder.dispose();
	}

	void checkCursor() {
		saySubObjective("The cursor still works");
		Cursor cross = new Cursor(display, SWT.CURSOR_CROSS);
		a.setCursor(cross);
		move(a, 40, 40);
		check(OS.WinQueryPointer(OS.HWND_DESKTOP) == cross.handle, "WM_MOUSEMOVE sets the cursor of the control");
		a.setCursor(null);
		cross.dispose();
		move(b, 1, 1);
		run(300);
		taken();
	}

	void initComponents() {
		automatic();
		interactive();
	}

	void automatic() {
		/* The window is not shown during the automatic checks (see SWT012_01) */
		shell.setSize(460, 330);
		sayObjective("Automatic checks");
		a = new Canvas(shell, SWT.NONE);
		a.setBounds(10, 10, 100, 60);
		b = new Canvas(shell, SWT.NONE);
		b.setBounds(120, 10, 100, 60);
		inner = new Canvas(b, SWT.NONE);
		inner.setBounds(50, 10, 40, 40);
		record(a, "a");
		record(b, "b");
		record(inner, "inner");
		checkMove();
		checkDown();
		checkEnterExit();
		checkLeave();
		checkHandles();
		checkCursor();
		a.dispose();
		b.dispose();
		say("\n" + checks + " checks passed.\n");
	}

	void interactive() {
		sayObjective("Interactive: move the pointer over the boxes, rest on them, leave the window; the list shows the events");
		final Canvas outer = new Canvas(shell, SWT.BORDER);
		outer.setBackground(display.getSystemColor(SWT.COLOR_DARK_BLUE));
		outer.setBounds(10, 10, 200, 150);
		final Canvas box = new Canvas(outer, SWT.BORDER);
		box.setBackground(display.getSystemColor(SWT.COLOR_DARK_GREEN));
		box.setBounds(50, 40, 100, 70);
		final List list = new List(shell, SWT.BORDER | SWT.V_SCROLL);
		list.setBounds(220, 10, 220, 240);
		final Label last = new Label(shell, SWT.BORDER);
		last.setBounds(10, 170, 200, 28);
		outer.setData("blue");
		box.setData("green");
		Listener listener = new Listener() {
			public void handleEvent(Event e) {
				String s = name(e.type) + " " + e.widget.getData() + " " + e.x + "," + e.y;
				if (e.type == SWT.MouseMove) {
					last.setText(s);
					return;
				}
				list.add(s, 0);
				if (list.getItemCount() > 100) list.remove(100);
			}
		};
		for (int i = 0; i < TYPES.length; i++) {
			outer.addListener(TYPES[i], listener);
			box.addListener(TYPES[i], listener);
		}
	}

	public static void main(String[] args) {
		go(new SWT016_01());
	}

}
