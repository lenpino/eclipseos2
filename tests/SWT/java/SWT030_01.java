/*
 *  SWT030_01.java
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
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.pm.OS;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 *  The layout of the Welcome page of Eclipse 2.0.1 (WelcomeEditor): a label with
 *  an arrow (16x16, horizontalIndent 20) and a StyledText in the next column,
 *  on a white background. Reads the screen: the first letter of each line of the
 *  text must be painted where StyledText says the text starts, and the label
 *  must not cover the text.
 *
 *  In Eclipse (2026-11-22) the titles lost their first letters ("stalled
 *  Features"), and a line like "To find out" showed "o find out".
 *
 *  To close the window automatically (for unattended runs) pass the system
 *  property swt.test.autoclose=<milliseconds> to the JVM.
 */
public class SWT030_01 extends SWTTestCase {

	static {
		STEP = "030";
		TEST = "01";
		DESC = "The first letters of the Welcome page";
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

	/* The x of the first column (from x0) that has ink in the rows y0..y1 of the control, or -1 */
	int firstInk(Control c, int x0, int x1, int y0, int y1, int bg) {
		for (int x = x0; x < x1; x++) for (int y = y0; y < y1; y++) if (pel(c, x, y) != bg) return x;
		return -1;
	}

	void initComponents() {
		sayObjective("Automatic checks");
		/* As JFace creates the images of its registry: an icon from the data and its transparency mask */
		ImageData arrowData = new ImageData(DIR + "welcome_item.gif");
		Image arrow = new Image(display, arrowData, arrowData.getTransparencyMask());
		say("  arrow: type " + (arrow.type == SWT.ICON ? "ICON" : "BITMAP") + ", bounds " + arrow.getBounds());
		Color white = new Color(display, 255, 255, 255);
		int bg = rgb(white.getRGB());
		Shell s = new Shell(display, SWT.NO_TRIM);
		s.setBounds(20, 20, 520, 120);
		Composite info = new Composite(s, SWT.NONE);
		info.setBackground(white);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		info.setLayout(layout);
		String[] texts = {"Installed Features\nTo find out more about the features", "Perspectives, views and editors\nA window contains one or more"};
		Label[] labels = new Label[texts.length];
		StyledText[] styled = new StyledText[texts.length];
		for (int i = 0; i < texts.length; i++) {
			labels[i] = new Label(info, SWT.NONE);
			labels[i].setBackground(white);
			labels[i].setImage(arrow);
			GridData gd = new GridData();
			gd.horizontalIndent = 20;
			gd.verticalAlignment = GridData.VERTICAL_ALIGN_BEGINNING;
			labels[i].setLayoutData(gd);
			styled[i] = new StyledText(info, SWT.MULTI | SWT.READ_ONLY);
			styled[i].setBackground(white);
			styled[i].setForeground(display.getSystemColor(SWT.COLOR_BLACK));
			styled[i].setText(texts[i]);
			StyleRange bold = new StyleRange(0, texts[i].indexOf('\n'), null, null, SWT.BOLD);
			styled[i].setStyleRange(bold);
			gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.verticalAlignment = GridData.VERTICAL_ALIGN_BEGINNING;
			styled[i].setLayoutData(gd);
		}
		info.setBounds(0, 0, 520, 120);
		s.open();
		pump(s);
		for (int i = 0; i < texts.length; i++) {
			Rectangle lb = labels[i].getBounds(), tb = styled[i].getBounds();
			say("  label " + lb.x + "," + lb.y + " " + lb.width + "x" + lb.height + ", text " + tb.x + "," + tb.y + " " + tb.width + "x" + tb.height
				+ ", text starts at x=" + styled[i].getLocationAtOffset(0).x + ", line height " + styled[i].getLineHeight());
			say("  label computeSize " + labels[i].computeSize(SWT.DEFAULT, SWT.DEFAULT));
			check(lb.x + lb.width <= tb.x, "the label (right edge " + (lb.x + lb.width) + ") does not cover the text (starts at " + tb.x + ")");
			int start = styled[i].getLocationAtOffset(0).x;
			int lh = styled[i].getLineHeight();
			int ink = firstInk(styled[i], 0, tb.width, 0, lh, bg);
			check(ink >= 0 && ink <= start + 3, "the first letter of the title " + (i + 1) + " is painted: first ink at x=" + ink + ", the text starts at " + start);
			ink = firstInk(styled[i], 0, tb.width, lh, lh * 2, bg);
			check(ink >= 0 && ink <= start + 3, "the first letter of the second line " + (i + 1) + " is painted: first ink at x=" + ink + ", the text starts at " + start);
		}
		s.dispose();
		arrow.dispose();
		white.dispose();
		say("\n" + checks + " checks, " + failures + " failed.\n");
		if (failures > 0) throw new RuntimeException(failures + " checks failed");
		shell.setSize(200, 100);
		setTitle(null);
	}

	public static void main(String[] args) {
		go(new SWT030_01());
	}
}
