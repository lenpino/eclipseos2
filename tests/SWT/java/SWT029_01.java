/*
 *  SWT029_01.java
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
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 *  Validates the images of labels, read from the screen: the transparent pixels
 *  of a bitmap or icon show the background of the label (a contrasting red
 *  here, as the label of the Welcome page sets it), the opaque ones are exact.
 *
 *  In Eclipse (2026-11-22) the arrows of the Welcome page were black or green
 *  squares: the label drew its bitmap opaque (SS_BITMAP), and the transparent
 *  entry of the palette is not the color of the transparent pixel.
 *
 *  To close the window automatically (for unattended runs) pass the system
 *  property swt.test.autoclose=<milliseconds> to the JVM.
 */
public class SWT029_01 extends SWTTestCase {

	static {
		STEP = "029";
		TEST = "01";
		DESC = "Images in labels";
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

	/* The icon is searched in the label: the offset with the fewest pixels that differ */
	void checkIcon(String what, Control button, ImageData data, int face) {
		Rectangle b = button.getBounds();
		int[][] screen = new int[b.height][b.width];
		for (int y = 0; y < b.height; y++) for (int x = 0; x < b.width; x++) screen[y][x] = pel(button, x, y);
		int minX = 999, minY = 999, maxX = -1, maxY = -1;
		for (int y = 0; y < b.height; y++) for (int x = 0; x < b.width; x++) if (screen[y][x] != face) {
			minX = Math.min(minX, x); minY = Math.min(minY, y); maxX = Math.max(maxX, x); maxY = Math.max(maxY, y);
		}
		say("  label " + b.width + "x" + b.height + ", image " + data.width + "x" + data.height + ", painted box " + minX + "," + minY + " to " + maxX + "," + maxY);
		int best = Integer.MAX_VALUE, bestOpaque = 0, bestTransparent = 0, bx = 0, by = 0;
		for (int dy = 0; dy + data.height <= b.height; dy++) {
			for (int dx = 0; dx + data.width <= b.width; dx++) {
				int opaque = 0, transparent = 0;
				for (int y = 0; y < data.height; y++) {
					for (int x = 0; x < data.width; x++) {
						int pixel = data.getPixel(x, y), got = screen[dy + y][dx + x];
						if (pixel == data.transparentPixel) {
							if (got != face) transparent++;
						} else if (got != rgb(data.palette.getRGB(pixel))) {
							opaque++;
						}
					}
				}
				if (opaque + transparent < best) {
					best = opaque + transparent;
					bestOpaque = opaque;
					bestTransparent = transparent;
					bx = dx;
					by = dy;
				}
			}
		}
		check(best == 0, what + ": at " + bx + "," + by + " in the label, " + bestOpaque + " opaque pixels differ, "
			+ bestTransparent + " transparent ones are not the face " + Integer.toHexString(face));
	}

	void initComponents() {
		sayObjective("Automatic checks");
		String[] names = {"fldr_obj.gif", "new_wiz_c.gif", "save_edit_d.gif", "backward_nav_e.gif"};
		Color red = new Color(display, 255, 0, 0);
		int face = rgb(red.getRGB());
		Shell s = new Shell(display, SWT.NO_TRIM);
		s.setBackground(red);
		s.setBounds(20, 20, 300, 120);
		ImageData[] data = new ImageData[names.length];
		Image[] bitmaps = new Image[names.length], icons = new Image[names.length];
		Label[] bitmapLabels = new Label[names.length], iconLabels = new Label[names.length];
		for (int i = 0; i < names.length; i++) {
			data[i] = new ImageData(DIR + names[i]);
			bitmaps[i] = new Image(display, data[i]);
			icons[i] = new Image(display, data[i], data[i].getTransparencyMask());
			bitmapLabels[i] = new Label(s, SWT.NONE);
			bitmapLabels[i].setImage(bitmaps[i]);
			bitmapLabels[i].setBackground(red);
			bitmapLabels[i].setBounds(10 + i * 40, 10, 32, 32);
			iconLabels[i] = new Label(s, SWT.NONE);
			iconLabels[i].setImage(icons[i]);
			iconLabels[i].setBackground(red);
			iconLabels[i].setBounds(10 + i * 40, 60, 32, 32);
		}
		s.open();
		pump(s);
		saySubObjective("Bitmaps (as Eclipse makes the images of the Welcome page)");
		for (int i = 0; i < names.length; i++) checkIcon("bitmap " + names[i], bitmapLabels[i], data[i], face);
		saySubObjective("Icons (data and mask)");
		for (int i = 0; i < names.length; i++) checkIcon("icon " + names[i], iconLabels[i], data[i], face);
		saySubObjective("Without an image again");
		bitmapLabels[0].setImage(null);
		bitmapLabels[0].setText("text");
		pump(s);
		check(bitmapLabels[0].getImage() == null, "the label has no image");
		s.dispose();
		for (int i = 0; i < names.length; i++) {
			bitmaps[i].dispose();
			icons[i].dispose();
		}
		red.dispose();
		say("\n" + checks + " checks, " + failures + " failed.\n");
		if (failures > 0) throw new RuntimeException(failures + " checks failed");
		shell.setSize(200, 100);
		setTitle(null);
	}

	public static void main(String[] args) {
		go(new SWT029_01());
	}
}
