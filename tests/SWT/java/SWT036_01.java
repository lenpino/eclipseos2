/*
 *  SWT036_01.java
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
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

/**
 *  Mnemonics ("&Name"): the text is shown with the letter underlined and no '&' or '~' (OS/2's
 *  own prefix). Seen by comparing the ink of "&Name" with the ink of "Name": it has the same
 *  width, the underline only adds a row.
 */
public class SWT036_01 extends SWTTestCase {

	static {
		STEP = "036";
		TEST = "01";
		DESC = "Mnemonics";
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
	/* The first and the last x with the black of the text (the frames and the faces are other colors) */
	int[] ink(Control c, Rectangle area) {
		int first = -1, last = -1;
		for (int x = area.x; x < area.x + area.width; x++) {
			for (int y = area.y; y < area.y + area.height; y++) {
				if (pel(c, x, y) == 0) { if (first == -1) first = x; last = x; break; }
			}
		}
		return new int[] {first, last};
	}

	void same(String what, Control plain, Control mnemonic, Rectangle area) {
		int[] a = ink(plain, area), b = ink(mnemonic, area);
		say("  " + what + ": 'Name' ink " + a[0] + " to " + a[1] + ", '&Name' ink " + b[0] + " to " + b[1]);
		check(a[0] != -1 && b[0] != -1 &&  Math.abs((a[1] - a[0]) - (b[1] - b[0])) <= 1, what + ": '&Name' is as wide as 'Name', 1 pixel more for the underline (" + (b[1] - b[0] + 1) + " and " + (a[1] - a[0] + 1) + " pixels), no '&' or '~' shown");
	}

	void initComponents() {
		sayObjective("Automatic checks");
		shell.setLayout(null);
		shell.setSize(420, 340);
		shell.open();
		pump(shell);
		/* The focus goes to another control: the focus rectangle of a button is black as well */
		Button dummy = new Button(shell, SWT.CHECK);
		dummy.setBounds(300, 10, 20, 20);
		dummy.setFocus();
		/* Label */
		Label l1 = new Label(shell, SWT.NONE), l2 = new Label(shell, SWT.NONE);
		l1.setText("Name");
		l2.setText("&Name");
		l1.setBounds(10, 10, 100, 20);
		l2.setBounds(120, 10, 100, 20);
		/* Button */
		Button b1 = new Button(shell, SWT.PUSH), b2 = new Button(shell, SWT.PUSH);
		b1.setText("Name");
		b2.setText("&Name");
		b1.setBounds(10, 40, 100, 30);
		b2.setBounds(120, 40, 100, 30);
		/* Group */
		Group g1 = new Group(shell, SWT.NONE), g2 = new Group(shell, SWT.NONE);
		g1.setText("Name");
		g2.setText("&Name");
		g1.setBounds(10, 80, 100, 60);
		g2.setBounds(120, 80, 100, 60);
		/* Tabs */
		TabFolder f1 = new TabFolder(shell, SWT.NONE), f2 = new TabFolder(shell, SWT.NONE);
		TabItem t1 = new TabItem(f1, SWT.NONE), t2 = new TabItem(f2, SWT.NONE);
		t1.setText("Name");
		t2.setText("&Name");
		f1.setBounds(10, 150, 200, 120);
		f2.setBounds(220, 150, 200, 120);
		pump(shell);
		pump(f1);
		same("Label", l1, l2, new Rectangle(1, 1, 98, 18));
		same("Button", b1, b2, new Rectangle(6, 6, 88, 17));
		same("Group", g1, g2, new Rectangle(1, 0, 98, 14));
		Rectangle tab1 = new Rectangle(3, 3, 190, 30);
		int[] ta = ink(f1, tab1), tb = ink(f2, tab1);
		say("  TabItem: 'Name' ink " + ta[0] + " to " + ta[1] + ", '&Name' ink " + tb[0] + " to " + tb[1]);
		check(ta[0] != -1 && tb[0] != -1 &&  Math.abs((ta[1] - ta[0]) - (tb[1] - tb[0])) <= 1, "TabItem: '&Name' is as wide as 'Name', 1 pixel more for the underline (" + (tb[1] - tb[0] + 1) + " and " + (ta[1] - ta[0] + 1) + " pixels)");
		/* Literal characters: "&&" is one '&', and a '~' is a '~' (not the OS/2 prefix) */
		String[] texts = {"ab", "a&&b", "a~b", "a~~b"};
		int[] widths = new int[texts.length];
		for (int i = 0; i < texts.length; i++) {
			Label l = new Label(shell, SWT.NONE);
			l.setText(texts[i]);
			l.setBounds(10 + i * 70, 280, 60, 20);
			pump(l);
			int[] k = ink(l, new Rectangle(1, 1, 58, 18));
			widths[i] = k[1] - k[0] + 1;
		}
		say("  literal characters: ab " + widths[0] + ", a&&b " + widths[1] + ", a~b " + widths[2] + ", a~~b " + widths[3]);
		check(widths[1] - widths[0] >= 4 && widths[1] - widths[0] <= 12, "'a&&b' shows one '&' (" + widths[1] + " against " + widths[0] + " pixels of 'ab')");
		check(widths[2] - widths[0] >= 4 && widths[2] - widths[0] <= 12, "'a~b' shows one '~' (" + widths[2] + " against " + widths[0] + " pixels of 'ab')");
		check(widths[3] - widths[2] >= 4, "'a~~b' shows two (" + widths[3] + " against " + widths[2] + ")");
		say("\n" + checks + " checks, " + failures + " failed.\n");
		if (failures > 0 && System.getProperty("keep") == null) throw new RuntimeException(failures + " checks failed");
		setTitle(null);
	}

	public static void main(String[] args) {
		go(new SWT036_01());
	}
}
