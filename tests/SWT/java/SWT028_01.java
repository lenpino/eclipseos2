/*
 *  SWT028_01.java
 */

/*
 * Copyright (c) 2002, 2026 EclipseOS2 Team.
 * This file is made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 */

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.internal.pm.OS;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 *  Validates GC.fillGradientRectangle when both colors are the same, and the
 *  title of an active view of Eclipse: a CLabel with the gradient
 *  {TITLE_BACKGROUND, TITLE_BACKGROUND_GRADIENT, WIDGET_BACKGROUND} at 50 and
 *  100 percent.
 *
 *  PM has no color for the end of the gradient of a title, so both title
 *  colors are the same; fillGradientRectangle then drew an outline only, and
 *  the first half of the title of the views showed garbage (green on the
 *  screen, 2026-11-17).
 *
 *  All the checks are automatic and read the screen.
 *
 *  To close the window automatically (for unattended runs) pass the system
 *  property swt.test.autoclose=<milliseconds> to the JVM.
 */
public class SWT028_01 extends SWTTestCase {

	static {
		STEP = "028";
		TEST = "01";
		DESC = "Gradients of the titles";
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

	static int rgb(Color c) {
		RGB r = c.getRGB();
		return (r.red << 16) | (r.green << 8) | r.blue;
	}

	void initComponents() {
		sayObjective("Automatic checks");
		final Color green = display.getSystemColor(SWT.COLOR_GREEN);
		Shell s = new Shell(display, SWT.NO_TRIM);
		s.setBounds(20, 20, 400, 100);
		Canvas canvas = new Canvas(s, SWT.NO_BACKGROUND);
		canvas.setBounds(0, 0, 400, 40);
		canvas.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				e.gc.setBackground(display.getSystemColor(SWT.COLOR_BLACK));
				e.gc.fillRectangle(0, 0, 400, 40);
				e.gc.setForeground(green);
				e.gc.setBackground(green);
				e.gc.fillGradientRectangle(0, 10, 400, 20, false);
				e.gc.fillGradientRectangle(0, 10, 0, 20, true);
			}
		});
		Color title = display.getSystemColor(SWT.COLOR_TITLE_BACKGROUND);
		Color end = display.getSystemColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT);
		Color widget = display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
		CLabel label = new CLabel(s, SWT.NONE);
		label.setText("Package Explorer");
		label.setBounds(0, 50, 400, 30);
		label.setBackground(new Color[] {title, end, widget}, new int[] {50, 100});
		s.open();
		pump(s);

		saySubObjective("fillGradientRectangle with one color");
		check(pel(canvas, 200, 20) == 0x00FF00 && pel(canvas, 5, 15) == 0x00FF00 && pel(canvas, 395, 25) == 0x00FF00,
				"fills the rectangle (not only its outline): " + Integer.toHexString(pel(canvas, 200, 20)));

		saySubObjective("The title of an active view of Eclipse");
		check(pel(label, 150, 2) == rgb(title) && pel(label, 50, 27) == rgb(title),
				"the first half has the color of the title " + Integer.toHexString(rgb(title)) + ": " + Integer.toHexString(pel(label, 150, 2)));
		check(pel(label, 398, 2) != rgb(title), "the second half goes towards the background of the widgets");

		saySubObjective("System colors");
		check(rgb(display.getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT)) == rgb(display.getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND)),
				"the gradient of an inactive title is a background (PM has none of its own), not the text");
		s.dispose();
		say("\n" + checks + " checks passed.\n");
		shell.setSize(200, 100);
		setTitle(null);
	}

	public static void main(String[] args) {
		go(new SWT028_01());
	}
}
