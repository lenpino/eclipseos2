/*
 *  SWT024_01.java
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
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.pm.OS;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Shell;

/**
 *  Validates the scroll bars of a canvas or a composite (SWT.H_SCROLL and
 *  SWT.V_SCROLL), which are windows of their own in PM.
 *
 *  The first part is automatic: the window is shown and its pixels are read,
 *  the values of the bars are set, and the commands of the bars are sent. The
 *  second part is interactive: a canvas that draws a grid and scrolls it with
 *  its bars.
 *
 *  To close the window automatically (for unattended runs) pass the system
 *  property swt.test.autoclose=<milliseconds> to the JVM.
 */
public class SWT024_01 extends SWTTestCase {

	static {
		STEP = "024";
		TEST = "01";
		DESC = "Scroll bars of a canvas";
	}

	int checks;

	Shell createTopShell(Display display) {
		return new Shell(display, SWT.SHELL_TRIM);
	}

	void check(boolean ok, String what) {
		checks++;
		say((ok ? "  ok    " : "  FAIL  ") + what);
		if (!ok) throw new RuntimeException("Check failed: " + what);
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

	/* How many pixels of a rectangle are white */
	int white(Control control, Rectangle r) {
		int count = 0;
		for (int y = r.y; y < r.y + r.height; y++) {
			for (int x = r.x; x < r.x + r.width; x++) {
				if (pel(control, x, y) == 0xFFFFFF) count++;
			}
		}
		return count;
	}

	/* A command of a bar, as PM sends it: mp1 = (id of the bar, 0), mp2 = (position, command) */
	void command(Control control, int message, int id, int command, int position) {
		OS.WinSendMsg(control.handle, message, id, (position & 0xFFFF) | (command << 16));
	}

	String values(ScrollBar bar) {
		return bar.getSelection() + " in " + bar.getMinimum() + ".." + bar.getMaximum() + " thumb " + bar.getThumb();
	}

	void checkLayout(Shell window) {
		saySubObjective("The bars on the screen");
		Canvas canvas = new Canvas(window, SWT.H_SCROLL | SWT.V_SCROLL);
		canvas.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		canvas.setBounds(0, 0, 200, 150);
		pump(canvas);
		int cx = OS.WinQuerySysValue(OS.HWND_DESKTOP, OS.SV_CXVSCROLL), cy = OS.WinQuerySysValue(OS.HWND_DESKTOP, OS.SV_CYHSCROLL);
		ScrollBar vertical = canvas.getVerticalBar(), horizontal = canvas.getHorizontalBar();
		check(vertical != null && horizontal != null, "a canvas with H_SCROLL and V_SCROLL has two bars");
		Rectangle client = canvas.getClientArea();
		check(client.width == 200 - cx && client.height == 150 - cy, "the client area is beside the bars: " + client + " (" + cx + ", " + cy + ")");
		check(vertical.getSize().x == cx && vertical.getSize().y == 150 - cy && horizontal.getSize().y == cy && horizontal.getSize().x == 200 - cx,
				"the sizes of the bars: " + vertical.getSize() + ", " + horizontal.getSize());
		int inClient = white(canvas, new Rectangle(0, 0, client.width, client.height));
		int inVertical = white(canvas, new Rectangle(client.width + 1, 2, cx - 2, client.height - 4));
		int inHorizontal = white(canvas, new Rectangle(2, client.height + 1, client.width - 4, cy - 2));
		check(inClient == client.width * client.height, "the client area is the white background: " + inClient + " of " + client.width * client.height);
		check(inVertical < (cx - 2) * (client.height - 4) / 4 && inHorizontal < (client.width - 4) * (cy - 2) / 4,
				"the bars are drawn where they are, not the background: " + inVertical + " and " + inHorizontal + " white pixels");

		canvas.setSize(260, 180);
		pump(canvas);
		client = canvas.getClientArea();
		check(client.width == 260 - cx && client.height == 180 - cy && vertical.getSize().y == 180 - cy, "a resize moves the bars: " + client + ", " + vertical.getSize());
		inVertical = white(canvas, new Rectangle(client.width + 1, 2, cx - 2, client.height - 4));
		check(inVertical < (cx - 2) * (client.height - 4) / 4, "and the vertical bar is drawn at the new right: " + inVertical + " white pixels");

		horizontal.setVisible(false);
		pump(canvas);
		client = canvas.getClientArea();
		check(!horizontal.getVisible() && client.height == 180 && vertical.getSize().y == 180, "a hidden bar leaves its room: " + client + ", " + vertical.getSize());
		horizontal.setVisible(true);
		check(canvas.getClientArea().height == 180 - cy, "and takes it again when shown");
		canvas.dispose();

		Composite composite = new Composite(window, SWT.V_SCROLL);
		composite.setBounds(0, 0, 200, 100);
		composite.setLayout(new GridLayout());
		Button button = new Button(composite, SWT.PUSH);
		button.setText("A child");
		button.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		composite.layout();
		Control[] children = composite.getChildren();
		check(children.length == 1 && children[0] == button, "the bars are not children: " + children.length);
		check(button.getBounds().x + button.getBounds().width <= 200 - cx, "a layout puts the children beside the bar: " + button.getBounds());
		composite.dispose();
		Canvas plain = new Canvas(window, SWT.NONE);
		check(plain.getVerticalBar() == null && plain.getHorizontalBar() == null, "a canvas without the styles has no bars");
		plain.dispose();
	}

	void checkValues(Shell window) {
		saySubObjective("The values of a bar");
		Canvas canvas = new Canvas(window, SWT.V_SCROLL);
		canvas.setBounds(0, 0, 200, 150);
		ScrollBar bar = canvas.getVerticalBar();
		check(bar.getSelection() == 0 && bar.getMinimum() == 0 && bar.getMaximum() == 100 && bar.getThumb() == 10, "the defaults are those of Windows: " + values(bar));
		check(bar.getIncrement() == 1 && bar.getPageIncrement() == 10, "and the increments");
		bar.setValues(20, 0, 200, 50, 5, 25);
		check(bar.getSelection() == 20 && bar.getMaximum() == 200 && bar.getThumb() == 50 && bar.getIncrement() == 5 && bar.getPageIncrement() == 25, "setValues: " + values(bar));
		bar.setSelection(500);
		check(bar.getSelection() == 150, "the selection goes up to the maximum less the thumb: " + bar.getSelection());
		bar.setSelection(-3);
		check(bar.getSelection() == 150, "a negative one is ignored");
		bar.setMaximum(40);
		check(bar.getMaximum() == 200, "a maximum that leaves no room for the thumb is ignored: " + bar.getMaximum());
		bar.setMaximum(300);
		check(bar.getMaximum() == 300 && bar.getSelection() == 150, "setMaximum: " + values(bar));
		bar.setThumb(400);
		check(bar.getThumb() == 300 && bar.getSelection() == 0, "a thumb as big as the range: everything visible, at the start: " + values(bar));
		bar.setValues(0, 10, 110, 20, 1, 10);
		check(bar.getMinimum() == 10 && bar.getSelection() == 10, "a minimum: the selection starts there: " + values(bar));
		canvas.dispose();
	}

	void checkEvents(Shell window) {
		saySubObjective("The commands of a bar");
		Canvas canvas = new Canvas(window, SWT.V_SCROLL | SWT.H_SCROLL);
		canvas.setBounds(0, 0, 200, 150);
		final ScrollBar bar = canvas.getVerticalBar();
		bar.setValues(0, 0, 200, 50, 5, 25);
		final int[] details = new int[1];
		final int[] count = new int[1];
		bar.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) { details[0] = e.detail; count[0]++; }
		});
		command(canvas, OS.WM_VSCROLL, OS.FID_VERTSCROLL, OS.SB_LINEDOWN, 0);
		check(bar.getSelection() == 5 && details[0] == SWT.ARROW_DOWN && count[0] == 1, "a line down: the increment, ARROW_DOWN: " + bar.getSelection());
		command(canvas, OS.WM_VSCROLL, OS.FID_VERTSCROLL, OS.SB_PAGEDOWN, 0);
		check(bar.getSelection() == 30 && details[0] == SWT.PAGE_DOWN, "a page down: the page increment, PAGE_DOWN: " + bar.getSelection());
		command(canvas, OS.WM_VSCROLL, OS.FID_VERTSCROLL, OS.SB_SLIDERTRACK, 100);
		check(bar.getSelection() == 100 && details[0] == SWT.DRAG, "dragging the thumb: DRAG: " + bar.getSelection());
		command(canvas, OS.WM_VSCROLL, OS.FID_VERTSCROLL, OS.SB_SLIDERPOSITION, 120);
		check(bar.getSelection() == 120 && details[0] == 0, "the end of the drag: no detail: " + bar.getSelection());
		command(canvas, OS.WM_VSCROLL, OS.FID_VERTSCROLL, OS.SB_PAGEDOWN, 0);
		command(canvas, OS.WM_VSCROLL, OS.FID_VERTSCROLL, OS.SB_PAGEDOWN, 0);
		check(bar.getSelection() == 150, "no further than the maximum less the thumb: " + bar.getSelection());
		bar.setSelection(2);
		command(canvas, OS.WM_VSCROLL, OS.FID_VERTSCROLL, OS.SB_PAGEUP, 0);
		check(bar.getSelection() == 0 && details[0] == SWT.PAGE_UP, "nor before the minimum: " + bar.getSelection());
		int before = count[0];
		command(canvas, OS.WM_VSCROLL, OS.FID_VERTSCROLL, OS.SB_ENDSCROLL, 0);
		check(count[0] == before, "the end of the scrolling sends nothing");
		final int[] horizontal = new int[1];
		canvas.getHorizontalBar().addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) { horizontal[0]++; }
		});
		command(canvas, OS.WM_HSCROLL, OS.FID_HORZSCROLL, OS.SB_LINEDOWN, 0);
		check(horizontal[0] == 1 && count[0] == before, "WM_HSCROLL goes to the horizontal bar");
		canvas.dispose();
	}

	void initComponents() {
		Shell window = new Shell(display, SWT.NO_TRIM);
		window.setBounds(10, 10, 300, 220);
		window.open();
		sayObjective("Automatic checks");
		checkLayout(window);
		checkValues(window);
		checkEvents(window);
		window.dispose();
		say("\n" + checks + " checks passed.\n");

		sayObjective("Interactive: scroll the grid with the bars (arrows, page, drag), resize the window");
		final Canvas canvas = new Canvas(shell, SWT.H_SCROLL | SWT.V_SCROLL | SWT.NO_BACKGROUND);
		final int size = 1000;
		final ScrollBar vertical = canvas.getVerticalBar(), horizontal = canvas.getHorizontalBar();
		canvas.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				Rectangle client = canvas.getClientArea();
				e.gc.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
				e.gc.fillRectangle(client);
				int dx = horizontal.getSelection(), dy = vertical.getSelection();
				for (int i = 0; i <= size; i += 50) {
					e.gc.drawLine(i - dx, 0, i - dx, client.height);
					e.gc.drawLine(0, i - dy, client.width, i - dy);
					e.gc.drawString(String.valueOf(i), i - dx + 2, 2);
					e.gc.drawString(String.valueOf(i), 2, i - dy + 2);
				}
			}
		});
		Listener update = new Listener() {
			public void handleEvent(Event e) {
				Rectangle client = canvas.getClientArea();
				vertical.setValues(vertical.getSelection(), 0, size, Math.max(1, Math.min(size, client.height)), 10, Math.max(1, client.height));
				horizontal.setValues(horizontal.getSelection(), 0, size, Math.max(1, Math.min(size, client.width)), 10, Math.max(1, client.width));
				canvas.redraw();
			}
		};
		canvas.addListener(SWT.Resize, update);
		Listener scrolled = new Listener() {
			public void handleEvent(Event e) {
				say("Selection of the " + (e.widget == vertical ? "vertical" : "horizontal") + " bar: " + ((ScrollBar) e.widget).getSelection() + " (detail " + e.detail + ")");
				canvas.redraw();
			}
		};
		vertical.addListener(SWT.Selection, scrolled);
		horizontal.addListener(SWT.Selection, scrolled);
		shell.setLayout(new org.eclipse.swt.layout.FillLayout());
		shell.setSize(360, 280);
	}

	void setTitle(String title) {
		super.setTitle(title);
	}

	public static void main(String[] args) {
		go(new SWT024_01());
	}

}
