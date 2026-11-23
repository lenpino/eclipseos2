/*
 *  SWT039_01.java
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
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;

/**
 *  Resize and Move events: one for each change of size or position, for every kind of
 *  control. PM sends WM_SIZE to some windows and not to others, and the SWT.Resize was
 *  sent from both WM_SIZE and WM_WINDOWPOSCHANGED: the controls that get both sent it twice.
 */
public class SWT039_01 extends SWTTestCase {

	static {
		STEP = "039";
		TEST = "01";
		DESC = "Resize and Move events";
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
	int resizes, moves;

	void listen(Control c) {
		c.addListener(SWT.Resize, new Listener() { public void handleEvent(Event e) { resizes++; } });
		c.addListener(SWT.Move, new Listener() { public void handleEvent(Event e) { moves++; } });
	}

	void settle() {
		for (int i = 0; i < 30; i++) while (display.readAndDispatch()) {}
	}

	void checkControl(Control c) {
		String name = c.getClass().getName();
		name = name.substring(name.lastIndexOf('.') + 1);
		listen(c);
		c.setBounds(10, 10, 100, 60);
		settle();
		resizes = moves = 0;
		c.setSize(120, 80);
		settle();
		check(resizes == 1, name + ": a change of size is one Resize (" + resizes + ")");
		/*
		* A list box of PM does not keep the bounds that it is given (it snaps to whole rows and moves,
		* {9, 11, 104, 60} for {10, 10, 100, 60}): known, not a Resize problem.
		*/
		if (!(c instanceof List)) check(moves == 0, name + ": and no Move (" + moves + ")");
		resizes = moves = 0;
		c.setLocation(30, 40);
		settle();
		check(moves == 1, name + ": a change of position is one Move (" + moves + ")");
		check(resizes == 0, name + ": and no Resize (" + resizes + ")");
		resizes = moves = 0;
		c.setBounds(50, 60, 140, 90);
		settle();
		check(resizes == 1 && moves == 1, name + ": both at once, one of each (Resize " + resizes + ", Move " + moves + ")");
		resizes = moves = 0;
		c.setSize(140, 90);
		settle();
		check(resizes == 0, name + ": the same size is no Resize (" + resizes + ")");
	}

	void initComponents() {
		sayObjective("Automatic checks");
		shell.setSize(400, 300);
		shell.open();
		settle();
		Control[] controls = {
			new Composite(shell, SWT.NONE),
			new Canvas(shell, SWT.NONE),
			new Button(shell, SWT.PUSH),
			new Label(shell, SWT.NONE),
			new Text(shell, SWT.BORDER),
			new Group(shell, SWT.NONE),
			new Combo(shell, SWT.DROP_DOWN),
			new List(shell, SWT.BORDER),
			new ProgressBar(shell, SWT.HORIZONTAL),
			new TabFolder(shell, SWT.NONE),
			new Tree(shell, SWT.BORDER),
			new Table(shell, SWT.BORDER),
		};
		for (int i = 0; i < controls.length; i++) checkControl(controls[i]);
		/* The shell itself */
		listen(shell);
		settle();
		resizes = moves = 0;
		shell.setSize(420, 320);
		settle();
		check(resizes == 1, "Shell: a change of size is one Resize (" + resizes + ")");
		say("\n" + checks + " checks, " + failures + " failed.\n");
		if (failures > 0) throw new RuntimeException(failures + " checks failed");
		setTitle(null);
	}

	public static void main(String[] args) {
		go(new SWT039_01());
	}
}
