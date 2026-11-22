/*
 *  SWT031_01.java
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
import org.eclipse.swt.widgets.Shell;

/**
 *  Validates Image.setBackground: the color that replaces the transparent pixel
 *  of an image, read back with all three channels and drawn on the screen.
 *
 *  In the 2026-11-22 code the color of the palette entry was made as
 *  (red << 16) | (red << 8) | blue: the green channel was a copy of the red one.
 *
 *  To close the window automatically (for unattended runs) pass the system
 *  property swt.test.autoclose=<milliseconds> to the JVM.
 */
public class SWT031_01 extends SWTTestCase {

	static {
		STEP = "031";
		TEST = "01";
		DESC = "Image.setBackground";
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

	void checkBackground(String name, int r, int g, int b) {
		ImageData data = new ImageData(DIR + name);
		Image image = new Image(display, data);
		Color color = new Color(display, r, g, b);
		int want = (r << 16) | (g << 8) | b;
		check(data.transparentPixel != -1, name + " has a transparent pixel (" + data.transparentPixel + ", depth " + data.depth + ")");
		image.setBackground(color);
		check(image.internal_transparent_rgb() == want, name + ": the transparent entry is " + Integer.toHexString(image.internal_transparent_rgb()) + " after setBackground(" + r + ", " + g + ", " + b + "), want " + Integer.toHexString(want));
		ImageData back = image.getImageData();
		RGB rgb = back.transparentPixel == -1 ? null : back.palette.getRGB(back.transparentPixel);
		check(rgb != null && rgb.red == r && rgb.green == g && rgb.blue == b, name + ": getImageData gives the transparent pixel as " + rgb);
		/* The opaque pixels do not change */
		boolean same = true;
		for (int y = 0; y < data.height && same; y++) for (int x = 0; x < data.width; x++) {
			if (data.getPixel(x, y) == data.transparentPixel) continue;
			RGB was = data.palette.getRGB(data.getPixel(x, y)), is = back.palette.getRGB(back.getPixel(x, y));
			if (!was.equals(is)) { same = false; break; }
		}
		check(same, name + ": the opaque pixels are the same");
		image.dispose();
		color.dispose();
	}

	void initComponents() {
		sayObjective("Automatic checks");
		checkBackground("fldr_obj.gif", 10, 200, 30);
		checkBackground("new_wiz_c.gif", 250, 40, 120);
		checkBackground("save_edit_d.gif", 0, 255, 0);
		/* A direct color image (24 bits) with a transparent pixel: the other branch of setBackground */
		ImageData direct = new ImageData(8, 8, 24, new PaletteData(0xFF0000, 0xFF00, 0xFF));
		for (int y = 0; y < 8; y++) for (int x = 0; x < 8; x++) direct.setPixel(x, y, x < 4 ? 0xFF00FF : 0x2040C0);
		direct.transparentPixel = 0xFF00FF;
		Image directImage = new Image(display, direct);
		Color c = new Color(display, 10, 200, 30);
		directImage.setBackground(c);
		ImageData got = directImage.getImageData();
		RGB left = got.palette.getRGB(got.getPixel(1, 1)), right = got.palette.getRGB(got.getPixel(6, 6));
		say("  24 bits: transparentPixel " + got.transparentPixel + ", left " + left + ", right " + right);
		check(left.red == 10 && left.green == 200 && left.blue == 30, "24 bits: the transparent pixels have the new color: " + left);
		check(right.red == 0x20 && right.green == 0x40 && right.blue == 0xC0, "24 bits: the opaque pixels are the same: " + right);
		directImage.dispose();
		c.dispose();
		/* An image without transparency is left as it is */
		Image plain = new Image(display, 16, 16);
		plain.setBackground(display.getSystemColor(SWT.COLOR_RED));
		check(plain.internal_transparent_rgb() == -1, "an image without a transparent pixel is not changed");
		plain.dispose();
		say("\n" + checks + " checks, " + failures + " failed.\n");
		if (failures > 0) throw new RuntimeException(failures + " checks failed");
		shell.setSize(200, 100);
		setTitle(null);
	}

	public static void main(String[] args) {
		go(new SWT031_01());
	}
}
