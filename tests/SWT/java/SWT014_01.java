/*
 *  SWT014_01.java
 */

/*
 * Copyright (c) 2002, 2026 EclipseOS2 Team.
 * This file is made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 */

import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.pm.OS;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tracker;

/**
 *  Validates the Tracker.
 *
 *  The first part is automatic: the keys and the mouse messages that the
 *  user would send are posted to the queue before Tracker.open(), whose
 *  own loop reads them, so nobody has to touch anything. The second part
 *  is interactive: drag the boxes, move and resize them with the buttons.
 *
 *  To close the window automatically (for unattended runs) pass the system
 *  property swt.test.autoclose=<milliseconds> to the JVM.
 */
public class SWT014_01 extends SWTTestCase {

	static {
		STEP = "014";
		TEST = "01";
		DESC = "Tracker";
	}

	int checks;
	/* The events that the tracker sent: "Move x y" or "Resize x y" */
	Vector events = new Vector();

	Shell createTopShell(Display display) {
		return new Shell(display, SWT.SHELL_TRIM | SWT.NO_REDRAW_RESIZE);
	}

	void check(boolean ok, String what) {
		checks++;
		say((ok ? "  ok    " : "  FAIL  ") + what);
		if (!ok) throw new RuntimeException("Check failed: " + what);
	}

	static String text(Rectangle r) {
		return r.x + "," + r.y + " " + r.width + "x" + r.height;
	}

	static String text(Rectangle[] rects) {
		String s = "";
		for (int i = 0; i < rects.length; i++) s += (i > 0 ? " | " : "") + text(rects[i]);
		return s;
	}

	/** Dispatches what is left in the queue, so that every check starts with it empty. */
	void flush() {
		while (display.readAndDispatch()) {}
	}

	void postKey(int vkey, int flags) {
		OS.WinPostMsg(shell.handle, OS.WM_CHAR, OS.KC_VIRTUALKEY | flags, vkey << 16);
	}

	void postKey(int vkey) {
		postKey(vkey, 0);
	}

	/** A mouse message at a point of the screen (SWT coordinates), sent to the shell. */
	void postMouse(int msg, Point screen) {
		int[] pt = new int[] {screen.x, OS.WinQuerySysValue(OS.HWND_DESKTOP, OS.SV_CYSCREEN) - 1 - screen.y};
		OS.WinMapWindowPoints(OS.HWND_DESKTOP, shell.handle, pt, 1);
		OS.WinPostMsg(shell.handle, msg, (pt[0] & 0xFFFF) | (pt[1] << 16), 0);
	}

	Tracker tracker(Composite parent, int style, Rectangle[] rects) {
		final Tracker t = parent != null ? new Tracker(parent, style) : new Tracker(display, style);
		t.setRectangles(rects);
		Listener listener = new Listener() {
			public void handleEvent(Event e) {
				events.addElement((e.type == SWT.Move ? "Move " : "Resize ") + e.x + " " + e.y);
			}
		};
		t.addListener(SWT.Move, listener);
		t.addListener(SWT.Resize, listener);
		return t;
	}

	/** Opens the tracker with the messages already posted; a timer closes it if they are not enough. */
	boolean open(final Tracker t) {
		events.removeAllElements();
		display.timerExec(3000, new Runnable() {
			public void run() {
				if (!t.isDisposed()) t.close();
			}
		});
		boolean result = t.open();
		check(OS.WinQueryCapture(OS.HWND_DESKTOP) == 0, "the capture of the pointer is released");
		return result;
	}

	Point pointer() {
		return display.getCursorLocation();
	}

	void checkApi() {
		saySubObjective("The API");
		Tracker t = new Tracker(shell, SWT.NONE);
		int all = SWT.LEFT | SWT.RIGHT | SWT.UP | SWT.DOWN;
		check((t.getStyle() & all) == all, "without directions it moves in all of them");
		check((new Tracker(shell, SWT.LEFT | SWT.RESIZE).getStyle() & all) == SWT.LEFT, "a direction leaves out the others");
		check(t.getDisplay() == display && new Tracker((Display) null, SWT.NONE).getDisplay() == display, "getDisplay, also with a null display");
		check(t.getRectangles() == null && !t.open(), "without rectangles open() returns false at once");
		boolean threw = false;
		try { t.setRectangles(null); } catch (IllegalArgumentException e) { threw = true; }
		check(threw, "setRectangles(null) throws");
		threw = false;
		try { new Tracker((Composite) null, SWT.NONE); } catch (IllegalArgumentException e) { threw = true; }
		check(threw, "a tracker without a parent throws");
		Rectangle[] rects = new Rectangle[] {new Rectangle(1, 2, 3, 4)};
		t.setRectangles(rects);
		check(t.getRectangles() == rects, "getRectangles");
		check(!t.getStippled(), "not stippled by default");
		t.setStippled(true);
		check(t.getStippled(), "setStippled");
		t.setRectangles(new Rectangle[] {new Rectangle(5, 5, 0, 0)});
		check(true, "an empty rectangle can be set");
		t.dispose();
		threw = false;
		try { t.open(); } catch (org.eclipse.swt.SWTException e) { threw = true; }
		check(threw, "a disposed tracker throws");
	}

	void checkKeys() {
		saySubObjective("Moved with the keys");
		flush();
		Tracker t = tracker(shell, SWT.NONE, new Rectangle[] {new Rectangle(20, 20, 60, 40)});
		Point start = shell.toDisplay(new Point(50, 20));
		postKey(OS.VK_RIGHT);
		postKey(OS.VK_DOWN, OS.KC_CTRL);
		postKey(OS.VK_NEWLINE);
		check(open(t), "Enter ends it and open() returns true");
		check(text(t.getRectangles()).equals("29,21 60x40"), "the arrows move 9 pixels, with Ctrl 1: " + text(t.getRectangles()));
		check(events.size() == 2, "a Move event for each key: " + events);
		check(events.elementAt(0).equals("Move " + (start.x + 9) + " " + start.y), "the event has the pointer in the screen: " + events.elementAt(0) + ", it started at " + start);
		Point top = shell.toDisplay(new Point(59, 21));
		check(pointer().equals(top), "the pointer is at the middle of the top side: " + pointer() + ", " + top + " was expected");
		t.dispose();

		flush();
		t = tracker(shell, SWT.NONE, new Rectangle[] {new Rectangle(20, 20, 60, 40)});
		postKey(OS.VK_RIGHT);
		postKey(OS.VK_ESC);
		check(!open(t), "Esc cancels it");
		check(text(t.getRectangles()).equals("29,20 60x40"), "and leaves the rectangles where they were moved, as Windows: " + text(t.getRectangles()));
		t.dispose();

		flush();
		t = tracker(shell, SWT.NONE, new Rectangle[] {new Rectangle(20, 20, 60, 40)});
		postKey(OS.VK_RIGHT, OS.KC_ALT);
		check(!open(t), "a key with Alt cancels it");
		check(events.size() == 0, "without moving: " + events);
		t.dispose();

		flush();
		t = tracker(shell, SWT.NONE, new Rectangle[] {new Rectangle(20, 20, 60, 40)});
		postKey(OS.VK_RIGHT, OS.KC_KEYUP);
		postKey(OS.VK_ENTER);
		check(open(t) && events.size() == 0, "a key that is released does nothing and Enter of the keypad ends it");
		t.dispose();

		flush();
		t = tracker(shell, SWT.RIGHT, new Rectangle[] {new Rectangle(20, 20, 60, 40)});
		postKey(OS.VK_LEFT);
		postKey(OS.VK_UP);
		postKey(OS.VK_RIGHT);
		postKey(OS.VK_NEWLINE);
		check(open(t), "a tracker that only goes to the right");
		check(text(t.getRectangles()).equals("29,20 60x40"), "does not go to the left nor up: " + text(t.getRectangles()));
		check(events.size() == 3, "but sends the events, as Windows: " + events);
		t.dispose();
	}

	void checkResize() {
		saySubObjective("Resized with the keys");
		flush();
		Tracker t = tracker(shell, SWT.RESIZE, new Rectangle[] {new Rectangle(20, 20, 60, 40)});
		postKey(OS.VK_RIGHT);
		postKey(OS.VK_DOWN);
		postKey(OS.VK_NEWLINE);
		check(open(t), "a resize tracker");
		check(text(t.getRectangles()).equals("20,20 69x49"), "the right and the bottom sides follow the keys: " + text(t.getRectangles()));
		check(events.size() == 2 && ((String) events.elementAt(0)).startsWith("Resize "), "with Resize events: " + events);
		Point corner = shell.toDisplay(new Point(89, 69));
		check(pointer().equals(corner), "the pointer is at the corner that is moved: " + pointer() + ", " + corner + " was expected");
		t.dispose();

		flush();
		t = tracker(shell, SWT.RESIZE, new Rectangle[] {new Rectangle(20, 20, 60, 40)});
		postKey(OS.VK_LEFT);
		postKey(OS.VK_UP, OS.KC_CTRL);
		postKey(OS.VK_NEWLINE);
		check(open(t), "to the left and up");
		check(text(t.getRectangles()).equals("11,19 69x41"), "the left and the top sides move: " + text(t.getRectangles()));
		t.dispose();

		flush();
		t = tracker(shell, SWT.RESIZE, new Rectangle[] {new Rectangle(0, 0, 50, 50), new Rectangle(50, 0, 50, 50)});
		postKey(OS.VK_RIGHT);
		postKey(OS.VK_NEWLINE);
		check(open(t), "two rectangles");
		check(text(t.getRectangles()).equals("0,0 54x50 | 54,0 54x50"), "keep their proportions: " + text(t.getRectangles()));
		t.dispose();

		flush();
		t = tracker(shell, SWT.NONE, new Rectangle[] {new Rectangle(0, 0, 50, 50), new Rectangle(50, 10, 50, 50)});
		postKey(OS.VK_DOWN);
		postKey(OS.VK_NEWLINE);
		check(open(t) && text(t.getRectangles()).equals("0,9 50x50 | 50,19 50x50"), "and move together: " + text(t.getRectangles()));
		t.dispose();
	}

	void checkMouse() {
		saySubObjective("Moved with the mouse");
		flush();
		Tracker t = tracker(shell, SWT.NONE, new Rectangle[] {new Rectangle(20, 20, 60, 40)});
		Point start = shell.toDisplay(new Point(50, 20));
		Point end = new Point(start.x + 20, start.y + 15);
		postMouse(OS.WM_MOUSEMOVE, end);
		postMouse(OS.WM_BUTTON1UP, end);
		check(open(t), "releasing the button ends it");
		check(text(t.getRectangles()).equals("40,35 60x40"), "the rectangles follow the pointer: " + text(t.getRectangles()));
		check(events.size() == 1 && events.elementAt(0).equals("Move " + end.x + " " + end.y), "with a Move event where the pointer is: " + events);
		t.dispose();

		flush();
		t = tracker(shell, SWT.RESIZE, new Rectangle[] {new Rectangle(20, 20, 60, 40)});
		start = shell.toDisplay(new Point(50, 40));
		postMouse(OS.WM_MOUSEMOVE, new Point(start.x + 10, start.y));
		/* The pointer is put at the side that is resized: the button is released there */
		postMouse(OS.WM_BUTTON1UP, shell.toDisplay(new Point(90, 40)));
		check(open(t), "a resize tracker with the mouse");
		check(text(t.getRectangles()).equals("20,20 70x40"), "the side goes where the pointer goes: " + text(t.getRectangles()));
		t.dispose();
	}

	void checkListeners() {
		saySubObjective("What the listeners can do");
		flush();
		final Tracker t = tracker(shell, SWT.NONE, new Rectangle[] {new Rectangle(20, 20, 60, 40)});
		t.addListener(SWT.Move, new Listener() {
			public void handleEvent(Event e) {
				t.close();
			}
		});
		postKey(OS.VK_RIGHT);
		postKey(OS.VK_RIGHT);
		postKey(OS.VK_NEWLINE);
		check(open(t), "close() ends it and is not a cancellation");
		check(events.size() == 1, "after the first event: " + events);
		t.dispose();

		flush();
		final Tracker d = tracker(shell, SWT.NONE, new Rectangle[] {new Rectangle(20, 20, 60, 40)});
		d.addListener(SWT.Move, new Listener() {
			public void handleEvent(Event e) {
				d.dispose();
			}
		});
		postKey(OS.VK_RIGHT);
		postKey(OS.VK_NEWLINE);
		check(!open(d) && d.isDisposed(), "disposing it in a listener makes open() return false");

		flush();
		final Tracker c = tracker(shell, SWT.NONE, new Rectangle[] {new Rectangle(20, 20, 60, 40)});
		check(open(c), "if nothing comes, the timer of the test closes it (the loop dispatches the timers)");
		c.dispose();
	}

	void checkScreen() {
		saySubObjective("A tracker on the screen");
		flush();
		Tracker t = tracker(null, SWT.NONE, new Rectangle[] {new Rectangle(100, 100, 80, 60)});
		postKey(OS.VK_RIGHT);
		postKey(OS.VK_DOWN);
		postKey(OS.VK_NEWLINE);
		check(open(t), "on the display");
		check(text(t.getRectangles()).equals("109,109 80x60"), "the rectangles are in screen coordinates: " + text(t.getRectangles()));
		check(pointer().equals(new Point(149, 109)), "and so is the pointer: " + pointer());
		t.dispose();

		flush();
		t = tracker(null, SWT.NONE, new Rectangle[] {new Rectangle(100, 100, 80, 60)});
		t.setStippled(true);
		postKey(OS.VK_LEFT);
		postKey(OS.VK_ESC);
		check(!open(t), "a stippled one");
		t.dispose();
	}

	/* The pixels of a rectangle of the screen (SWT coordinates), as RGB */
	int[][] pels(Rectangle r) {
		int cy = OS.WinQuerySysValue(OS.HWND_DESKTOP, OS.SV_CYSCREEN);
		int hps = OS.WinGetScreenPS(OS.HWND_DESKTOP);
		OS.GpiCreateLogColorTable(hps, 0, OS.LCOLF_RGB, 0, 0, null);
		int[][] result = new int[r.height][r.width];
		for (int y = 0; y < r.height; y++) {
			for (int x = 0; x < r.width; x++) {
				result[y][x] = OS.GpiQueryPel(hps, new int[] {r.x + x, cy - 1 - (r.y + y)});
			}
		}
		OS.WinReleasePS(hps);
		return result;
	}

	/**
	 * Opens a tracker on the screen and reads the pixels while it shows the rectangle.
	 * The pointer goes to the middle of the top side: the columns where it is drawn are left out.
	 */
	void checkBands(boolean stippled) {
		final Rectangle r = new Rectangle(20, 20, 200, 30);
		int band = stippled ? 3 : 1;
		flush();
		int[][] before = pels(r);
		final Tracker t = tracker(null, SWT.NONE, new Rectangle[] {new Rectangle(r.x, r.y, r.width, r.height)});
		t.setStippled(stippled);
		final int[][][] during = new int[1][][];
		display.timerExec(300, new Runnable() {
			public void run() {
				during[0] = pels(r);
				t.close();
			}
		});
		check(open(t) && during[0] != null, (stippled ? "stippled" : "plain") + ": the pixels are read while the tracker is open");
		int[][] after = pels(r);
		int bandPels = 0, changed = 0, inside = 0, odd = 0, even = 0, traces = 0;
		for (int y = 0; y < r.height; y++) {
			for (int x = 0; x < r.width; x++) {
				if (x >= r.width / 2 - 4 && x < r.width / 2 + 40) continue;
				boolean inBand = x < band || y < band || x >= r.width - band || y >= r.height - band;
				boolean diff = during[0][y][x] != before[y][x];
				if (after[y][x] != before[y][x]) traces++;
				if (!inBand) {
					if (diff) inside++;
					continue;
				}
				bandPels++;
				if (diff) {
					changed++;
					if ((x + y) % 2 == 0) even++; else odd++;
				}
			}
		}
		check(inside == 0, "nothing is drawn inside the rectangle: " + inside + " pixels");
		if (stippled) {
			check(changed * 2 >= bandPels - 2 && changed * 2 <= bandPels + 2 && (even == 0 || odd == 0),
					"half of the band is inverted, in a checkerboard: " + changed + " of " + bandPels + " (even " + even + ", odd " + odd + ")");
		} else {
			check(changed == bandPels, "the whole line is inverted, corners included: " + changed + " of " + bandPels);
		}
		check(traces == 0, "the screen is as it was afterwards: " + traces + " pixels differ");
		t.dispose();
	}

	void checkDrawing() {
		saySubObjective("The pixels of the bands");
		checkBands(false);
		checkBands(true);
	}

	void initComponents() {
		automatic();
		interactive();
	}

	void automatic() {
		/* The window is not shown during the automatic checks (see SWT012_01) */
		shell.setSize(460, 330);
		sayObjective("Automatic checks");
		checkApi();
		checkKeys();
		checkResize();
		checkMouse();
		checkListeners();
		checkScreen();
		checkDrawing();
		flush();
		say("\n" + checks + " checks passed.\n");
	}

	Canvas box(int x, int y, int width, int height, int color) {
		Canvas box = new Canvas(shell, SWT.BORDER);
		box.setBackground(display.getSystemColor(color));
		box.setBounds(x, y, width, height);
		return box;
	}

	Button button(String text, int x, int y, int width) {
		Button b = new Button(shell, SWT.PUSH);
		b.setText(text);
		b.setBounds(x, y, width, 28);
		return b;
	}

	void interactive() {
		sayObjective("Interactive: drag the boxes; the buttons move or resize them with the mouse or the keys (Enter ends, Esc cancels)");
		final Canvas first = box(20, 80, 120, 80, SWT.COLOR_DARK_BLUE);
		final Canvas second = box(170, 80, 80, 120, SWT.COLOR_DARK_GREEN);
		final Button stippled = new Button(shell, SWT.CHECK);
		stippled.setText("Stippled");
		stippled.setBounds(330, 10, 110, 28);
		final Label result = new Label(shell, SWT.BORDER);
		result.setBounds(10, 260, 430, 28);
		result.setText("(nothing done yet)");

		final Listener drag = new Listener() {
			public void handleEvent(Event e) {
				Control box = (Control) e.widget;
				Tracker t = new Tracker(shell, SWT.NONE);
				t.setStippled(stippled.getSelection());
				t.setRectangles(new Rectangle[] {box.getBounds()});
				boolean done = t.open();
				if (done) box.setBounds(t.getRectangles()[0]);
				result.setText("Drag: " + (done ? text(t.getRectangles()) : "cancelled"));
				t.dispose();
			}
		};
		first.addListener(SWT.MouseDown, drag);
		second.addListener(SWT.MouseDown, drag);

		button("Move", 10, 10, 70).addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				track(SWT.NONE, new Control[] {first}, stippled.getSelection(), result);
			}
		});
		button("Resize", 85, 10, 70).addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				track(SWT.RESIZE, new Control[] {first}, stippled.getSelection(), result);
			}
		});
		button("Resize both", 160, 10, 90).addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				track(SWT.RESIZE, new Control[] {first, second}, stippled.getSelection(), result);
			}
		});
		button("Window", 255, 10, 70).addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				/* A tracker on the display, with the bounds of the window */
				Tracker t = new Tracker(display, SWT.NONE);
				t.setStippled(stippled.getSelection());
				t.setRectangles(new Rectangle[] {shell.getBounds()});
				boolean done = t.open();
				if (done) shell.setLocation(t.getRectangles()[0].x, t.getRectangles()[0].y);
				result.setText("Window: " + (done ? text(t.getRectangles()) : "cancelled"));
				t.dispose();
			}
		});
	}

	void track(int style, Control[] boxes, boolean stippled, Label result) {
		Tracker t = new Tracker(shell, style);
		t.setStippled(stippled);
		Rectangle[] rects = new Rectangle[boxes.length];
		for (int i = 0; i < boxes.length; i++) rects[i] = boxes[i].getBounds();
		t.setRectangles(rects);
		boolean done = t.open();
		if (done) {
			for (int i = 0; i < boxes.length; i++) boxes[i].setBounds(t.getRectangles()[i]);
		}
		result.setText((style == SWT.RESIZE ? "Resize: " : "Move: ") + (done ? text(t.getRectangles()) : "cancelled"));
		t.dispose();
	}

	public static void main(String[] args) {
		go(new SWT014_01());
	}

}
