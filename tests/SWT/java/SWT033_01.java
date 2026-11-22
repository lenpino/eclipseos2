/*
 *  SWT033_01.java
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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;

/**
 *  A progress dialog as JFace makes it ("Saving workspace"): the bar has to fill the
 *  whole control at 100% (the PM slider keeps the width of its arm at each end, so
 *  the bar looked short), and the background of the dialog has to be painted.
 */
public class SWT033_01 extends SWTTestCase {

	static {
		STEP = "033";
		TEST = "01";
		DESC = "ProgressBar width and dialog background";
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
	int[] extent(ProgressBar bar, int face) {
		Rectangle r = bar.getBounds();
		int y = r.height / 2, left = -1, right = -1;
		for (int x = 0; x < r.width; x++) {
			if (pel(bar, x, y) == face) { if (left == -1) left = x; right = x; }
		}
		return new int[] {left, right, r.width};
	}

	String runs(ProgressBar bar) {
		Rectangle r = bar.getBounds();
		StringBuffer sb = new StringBuffer();
		int y = r.height / 2, prev = -1, start = 0;
		for (int x = 0; x <= r.width; x++) {
			int p = x < r.width ? pel(bar, x, y) : -2;
			if (p != prev) {
				if (prev != -1) sb.append(start + "-" + (x - 1) + ":" + Integer.toHexString(prev) + " ");
				prev = p; start = x;
			}
		}
		return sb.toString();
	}

	void initComponents() {
		sayObjective("Automatic checks");
		shell.setLayout(new GridLayout());
		Label message = new Label(shell, SWT.WRAP);
		message.setText("Saving workspace.");
		message.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		ProgressBar bar = new ProgressBar(shell, SWT.HORIZONTAL);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.widthHint = 300;
		bar.setLayoutData(gd);
		bar.setMinimum(0);
		bar.setMaximum(100);
		Label sub = new Label(shell, SWT.WRAP);
		sub.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Button cancel = new Button(shell, SWT.PUSH);
		cancel.setText("Cancel");
		shell.pack();
		shell.open();
		/*
		* Nothing is dispatched: as when the UI thread goes on working (saving the workspace)
		* after opening the dialog. win32 paints it in setVisible (UpdateWindow).
		*/
		int face = rgb(shell.getBackground().getRGB());
		Rectangle sb = shell.getClientArea();
		int bg = pel(shell, sb.width - 2, 2);
		check(bg == face, "just after open() the dialog background is painted: " + Integer.toHexString(bg) + ", want " + Integer.toHexString(face));
		int mid = bar.getBounds().height / 2;
		int frame = pel(bar, 0, mid);
		check(frame == 0x808080, "just after open() the frame of the bar is painted: " + Integer.toHexString(frame));
		pump(bar);
		bar.setSelection(50);
		pump(bar);
		int w = bar.getBounds().width;
		int fill = pel(bar, 10, mid);
		say("  fill color " + Integer.toHexString(fill) + ", at 50%: " + runs(bar));
		check(fill != face && pel(bar, w / 2 + 20, mid) == face, "at 50% the left half is filled and the right one is not");
		bar.setSelection(100);
		pump(bar);
		say("  at 100%: " + runs(bar));
		check(pel(bar, 10, mid) == fill && pel(bar, w - 10, mid) == fill, "at 100% the bar is filled to the right edge");
		check(bar.getSelection() == 100, "getSelection answers what was set: " + bar.getSelection());
		bar.setSelection(0);
		pump(bar);
		check(pel(bar, 10, mid) != fill, "at 0% the bar is empty");
		say("\n" + checks + " checks, " + failures + " failed.\n");
		if (failures > 0) throw new RuntimeException(failures + " checks failed");
		setTitle(null);
	}

	public static void main(String[] args) {
		go(new SWT033_01());
	}
}
