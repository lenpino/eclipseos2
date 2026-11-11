/*
 *  SWT019_01.java
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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.pm.CURSORINFO;
import org.eclipse.swt.internal.pm.OS;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Caret;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 *  Validates Canvas.scroll().
 *
 *  The first part is automatic: the window is shown and the pixels of the
 *  screen are read before and after scrolling. The second part is
 *  interactive: the buttons scroll a canvas with lines of text.
 *
 *  To close the window automatically (for unattended runs) pass the system
 *  property swt.test.autoclose=<milliseconds> to the JVM.
 */
public class SWT019_01 extends SWTTestCase {

	static {
		STEP = "019";
		TEST = "01";
		DESC = "Canvas.scroll";
	}

	int checks;
	Canvas canvas;
	boolean square = true;

	Shell createTopShell(Display display) {
		return new Shell(display, SWT.SHELL_TRIM | SWT.NO_REDRAW_RESIZE);
	}

	void check(boolean ok, String what) {
		checks++;
		say((ok ? "  ok    " : "  FAIL  ") + what);
		if (!ok) throw new RuntimeException("Check failed: " + what);
	}

	void run(int milliseconds) {
		OS.WinPostMsg(shell.handle, OS.WM_NULL, 0, 0);
		long end = System.currentTimeMillis() + milliseconds;
		do {
			while (display.readAndDispatch()) {}
			try { Thread.sleep(10); } catch (InterruptedException e) {}
		} while (System.currentTimeMillis() < end);
	}

	/** 'B' black, 'W' white or '?' for the pixel of the screen at a point of the canvas. */
	char pixel(int x, int y) {
		Point p = canvas.toDisplay(new Point(x, y));
		int hps = OS.WinGetScreenPS(OS.HWND_DESKTOP);
		OS.GpiCreateLogColorTable(hps, 0, OS.LCOLF_RGB, 0, 0, null);
		int pel = OS.GpiQueryPel(hps, new int[] {p.x, OS.WinQuerySysValue(OS.HWND_DESKTOP, OS.SV_CYSCREEN) - 1 - p.y});
		OS.WinReleasePS(hps);
		int r = (pel >> 16) & 0xFF, g = (pel >> 8) & 0xFF, b = pel & 0xFF;
		if (r < 60 && g < 60 && b < 60) return 'B';
		if (r > 200 && g > 200 && b > 200) return 'W';
		return '?';
	}

	void checkCopy() {
		saySubObjective("The area is copied and the rest repainted");
		check(pixel(15, 15) == 'B' && pixel(75, 55) == 'W', "before: the square at 10,10: " + pixel(15, 15) + pixel(75, 55));
		/* From now on the canvas paints nothing: what is black was copied */
		square = false;
		canvas.scroll(60, 40, 0, 0, 100, 60, false);
		run(300);
		check(pixel(75, 55) == 'B' && pixel(85, 65) == 'B', "the square is copied 60 right and 40 down (the y of PM is turned): " + pixel(75, 55) + pixel(85, 65));
		check(pixel(15, 15) == 'W', "the place it left is repainted: " + pixel(15, 15));
		check(pixel(75, 5) == 'W' && pixel(15, 85) == 'W', "nothing else is drawn: " + pixel(75, 5) + pixel(15, 85));
		canvas.scroll(0, 0, 60, 40, 100, 60, false);
		run(300);
		check(pixel(15, 15) == 'B' && pixel(75, 55) == 'W', "and back, up and to the left: " + pixel(15, 15) + pixel(75, 55));
		square = true;
		canvas.redraw();
		run(300);
	}

	void checkChildren() {
		saySubObjective("The children");
		Canvas inside = new Canvas(canvas, SWT.BORDER);
		inside.setBounds(20, 70, 20, 20);
		Canvas outside = new Canvas(canvas, SWT.BORDER);
		outside.setBounds(150, 10, 20, 20);
		run(200);
		canvas.scroll(30, 50, 0, 50, 120, 70, false);
		run(200);
		check(inside.getBounds().x == 20, "without all the children stay: " + inside.getBounds());
		canvas.scroll(30, 50, 0, 50, 120, 70, true);
		run(200);
		check(inside.getBounds().equals(new Rectangle(50, 70, 20, 20)), "with all a child in the area moves with it: " + inside.getBounds());
		check(outside.getBounds().equals(new Rectangle(150, 10, 20, 20)), "a child out of the area stays, as on Windows: " + outside.getBounds());
		canvas.scroll(0, 20, 0, 0, 200, 120, true);
		run(200);
		check(inside.getBounds().y == 90 && outside.getBounds().y == 30, "a scroll of all the canvas moves all of them down: " + inside.getBounds() + " " + outside.getBounds());
		inside.dispose();
		outside.dispose();
		canvas.redraw();
		run(300);
	}

	void checkCaret() {
		saySubObjective("The caret");
		Caret caret = new Caret(canvas, SWT.NONE);
		caret.setBounds(150, 80, 2, 15);
		canvas.setFocus();
		run(200);
		if (!canvas.isFocusControl()) {
			say("  SKIPPED  the program cannot take the focus (see SWT017_01)");
			caret.dispose();
			return;
		}
		canvas.scroll(0, 10, 0, 0, 200, 110, false);
		run(200);
		CURSORINFO info = new CURSORINFO();
		boolean has = OS.WinQueryCursorInfo(OS.HWND_DESKTOP, info) && info.hwnd == canvas.handle;
		int height = canvas.getSize().y;
		check(has && info.x == 150 && height - info.y - info.cy == 80, "the caret is where it was, not scrolled: " + (has ? info.x + "," + (height - info.y - info.cy) : "none"));
		caret.dispose();
	}

	void initComponents() {
		automatic();
		interactive();
	}

	void automatic() {
		shell.setSize(460, 330);
		sayObjective("Automatic checks");
		canvas = new Canvas(shell, SWT.NONE);
		canvas.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		canvas.setBounds(10, 10, 200, 120);
		canvas.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				if (!square) return;
				e.gc.setBackground(display.getSystemColor(SWT.COLOR_BLACK));
				e.gc.fillRectangle(10, 10, 20, 20);
			}
		});
		/* The pixels are read from the screen: the window is shown */
		shell.setLocation(40, 40);
		shell.open();
		run(400);
		checkCopy();
		checkChildren();
		checkCaret();
		shell.setVisible(false);
		run(100);
		canvas.dispose();
		say("\n" + checks + " checks passed.\n");
	}

	void interactive() {
		sayObjective("Interactive: the buttons scroll the text; it has to stay readable, without traces");
		final String[] lines = new String[40];
		for (int i = 0; i < lines.length; i++) lines[i] = "Line " + (i + 1) + ": the quick brown fox jumps over the lazy dog";
		final int[] top = new int[1];
		final int lineHeight = 16;
		final Canvas text = new Canvas(shell, SWT.BORDER);
		text.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		text.setBounds(10, 10, 430, 200);
		text.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				int first = Math.max(0, e.y / lineHeight);
				int last = Math.min(lines.length - 1 - top[0], (e.y + e.height) / lineHeight);
				for (int i = first; i <= last; i++) e.gc.drawString(lines[i + top[0]], 4, i * lineHeight);
			}
		});
		final Caret caret = new Caret(text, SWT.NONE);
		caret.setBounds(4, 2, 2, 14);
		String[] names = {"Down 1", "Down 5", "Up 1", "Up 5"};
		final int[] deltas = {1, 5, -1, -5};
		for (int i = 0; i < names.length; i++) {
			final int delta = deltas[i];
			Button b = new Button(shell, SWT.PUSH);
			b.setText(names[i]);
			b.setBounds(10 + i * 90, 220, 80, 28);
			b.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					int newTop = Math.max(0, Math.min(lines.length - 1, top[0] + delta));
					int count = newTop - top[0];
					if (count == 0) return;
					Rectangle area = text.getClientArea();
					top[0] = newTop;
					/* Scrolling down moves the content up */
					if (count > 0) {
						text.scroll(0, 0, 0, count * lineHeight, area.width, area.height - count * lineHeight, false);
					} else {
						text.scroll(0, -count * lineHeight, 0, 0, area.width, area.height + count * lineHeight, false);
					}
					text.setFocus();
				}
			});
		}
		text.setFocus();
	}

	public static void main(String[] args) {
		go(new SWT019_01());
	}

}
