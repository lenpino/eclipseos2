/*
 *  SWT027_01.java
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
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.internal.pm.OS;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 *  Validates the images of Eclipse: GIFs of 1, 2, 3, 4 and 5 bits with a
 *  transparent pixel, taken from the plugins of Eclipse 2.0.1 (the three
 *  versions of a tool icon, etool16, ctool16 and dtool16, and icons of
 *  objects), drawn with GC.drawImage on white and on black.
 *
 *  Every pixel drawn is read from the screen and compared with the one the
 *  ImageData gives: its color in the palette, or the background where it is
 *  transparent. Also getImageData and the IMAGE_GRAY copy.
 *
 *  To close the window automatically (for unattended runs) pass the system
 *  property swt.test.autoclose=<milliseconds> to the JVM.
 */
public class SWT027_01 extends SWTTestCase {

	static {
		STEP = "027";
		TEST = "01";
		DESC = "Images of Eclipse";
	}

	static final String DIR = System.getProperty("user.dir") + "/tests/SWT/images/eclipse/";
	static final String[] FILES = {"new_wiz_e.gif", "new_wiz_c.gif", "new_wiz_d.gif", "fldr_obj.gif",
		"prj_obj.gif", "package_obj.gif", "jar_obj.gif", "save_edit_e.gif", "save_edit_d.gif", "print_edit_d.gif",
		"backward_nav_e.gif", "backward_nav_d.gif"};

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

	/* What the pixel should look like over the background */
	static int expected(ImageData data, int x, int y, int background) {
		int pixel = data.getPixel(x, y);
		if (pixel == data.transparentPixel) return background;
		return rgb(data.palette.getRGB(pixel));
	}

	/* The pixels of the image drawn at (x0, y0) of the control that differ from the expected ones */
	String compare(Control control, int x0, int y0, ImageData data, int background, int[] count) {
		String first = null;
		for (int y = 0; y < data.height; y++) {
			for (int x = 0; x < data.width; x++) {
				int want = expected(data, x, y, background), got = pel(control, x0 + x, y0 + y);
				if (want != got) {
					count[0]++;
					if (first == null) first = "first at " + x + "," + y + ": " + Integer.toHexString(got) + " instead of " + Integer.toHexString(want)
						+ " (pixel " + data.getPixel(x, y) + (data.getPixel(x, y) == data.transparentPixel ? ", transparent" : "") + ")";
				}
			}
		}
		return first;
	}

	void checkDrawn(final ImageData[] datas, final Image[] images) {
		saySubObjective("GC.drawImage on white and on black, read from the screen");
		Shell s = new Shell(display, SWT.NO_TRIM);
		s.setBounds(20, 20, 40 + 24 * images.length, 70);
		final int[] backgrounds = {0xFFFFFF, 0x000000};
		final Canvas canvas = new Canvas(s, SWT.NO_BACKGROUND);
		canvas.setBounds(0, 0, 40 + 24 * images.length, 70);
		canvas.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				for (int b = 0; b < backgrounds.length; b++) {
					Color color = new Color(display, backgrounds[b] >> 16, (backgrounds[b] >> 8) & 0xFF, backgrounds[b] & 0xFF);
					e.gc.setBackground(color);
					e.gc.fillRectangle(0, b * 35, 40 + 24 * images.length, 35);
					color.dispose();
					for (int i = 0; i < images.length; i++) e.gc.drawImage(images[i], 10 + 24 * i, 10 + b * 35);
				}
			}
		});
		s.open();
		pump(s);
		for (int i = 0; i < images.length; i++) {
			for (int b = 0; b < backgrounds.length; b++) {
				int[] count = {0};
				String first = compare(canvas, 10 + 24 * i, 10 + b * 35, datas[i], backgrounds[b], count);
				check(count[0] == 0, FILES[i] + " on " + (b == 0 ? "white" : "black") + ": " + count[0] + " pixels of 256 differ" + (first == null ? "" : ", " + first));
			}
		}
		s.dispose();
	}

	void checkImageData(ImageData[] datas, Image[] images) {
		saySubObjective("getImageData gives the colors back");
		for (int i = 0; i < images.length; i++) {
			ImageData back = images[i].getImageData();
			int differ = 0;
			String first = null;
			for (int y = 0; y < datas[i].height; y++) {
				for (int x = 0; x < datas[i].width; x++) {
					if (datas[i].getPixel(x, y) == datas[i].transparentPixel) continue;
					int want = rgb(datas[i].palette.getRGB(datas[i].getPixel(x, y)));
					int got = rgb(back.palette.getRGB(back.getPixel(x, y)));
					if (want != got) {
						differ++;
						if (first == null) first = "first at " + x + "," + y + ": " + Integer.toHexString(got) + " instead of " + Integer.toHexString(want);
					}
				}
			}
			check(differ == 0, FILES[i] + ": " + differ + " opaque pixels differ (depth " + datas[i].depth + " -> " + back.depth + ")" + (first == null ? "" : ", " + first));
		}
	}

	void checkGray(ImageData[] datas, Image[] images) {
		saySubObjective("IMAGE_GRAY gives gray pixels and keeps the transparent ones");
		final Image[] grays = new Image[images.length];
		for (int i = 0; i < images.length; i++) grays[i] = new Image(display, images[i], SWT.IMAGE_GRAY);
		Shell s = new Shell(display, SWT.NO_TRIM);
		s.setBounds(20, 20, 40 + 24 * images.length, 40);
		final Canvas canvas = new Canvas(s, SWT.NO_BACKGROUND);
		canvas.setBounds(0, 0, 40 + 24 * images.length, 40);
		canvas.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				e.gc.setBackground(display.getSystemColor(SWT.COLOR_RED));
				e.gc.fillRectangle(0, 0, 40 + 24 * grays.length, 40);
				for (int i = 0; i < grays.length; i++) e.gc.drawImage(grays[i], 10 + 24 * i, 10);
			}
		});
		s.open();
		pump(s);
		for (int i = 0; i < images.length; i++) {
			int notGray = 0, lost = 0;
			for (int y = 0; y < 16; y++) {
				for (int x = 0; x < 16; x++) {
					int got = pel(canvas, 10 + 24 * i + x, 10 + y);
					boolean transparent = datas[i].getPixel(x, y) == datas[i].transparentPixel;
					if (transparent && got != 0xFF0000) lost++;
					if (!transparent && !((got >> 16) == ((got >> 8) & 0xFF) && ((got >> 8) & 0xFF) == (got & 0xFF))) notGray++;
				}
			}
			check(notGray == 0 && lost == 0, FILES[i] + " gray: " + notGray + " opaque pixels not gray, " + lost + " transparent ones not red");
			grays[i].dispose();
		}
		s.dispose();
	}

	void initComponents() {
		sayObjective("Automatic checks");
		ImageData[] datas = new ImageData[FILES.length];
		Image[] images = new Image[FILES.length];
		for (int i = 0; i < FILES.length; i++) {
			datas[i] = new ImageData(DIR + FILES[i]);
			images[i] = new Image(display, datas[i]);
			say("  " + FILES[i] + ": " + datas[i].width + "x" + datas[i].height + ", depth " + datas[i].depth
				+ ", " + datas[i].palette.colors.length + " colors, transparent pixel " + datas[i].transparentPixel);
		}
		checkImageData(datas, images);
		checkDrawn(datas, images);
		saySubObjective("The same as icons made from data and mask, as JFace does");
		Image[] icons = new Image[FILES.length];
		for (int i = 0; i < FILES.length; i++) icons[i] = new Image(display, datas[i], datas[i].getTransparencyMask());
		checkDrawn(datas, icons);
		for (int i = 0; i < icons.length; i++) icons[i].dispose();
		checkGray(datas, images);
		for (int i = 0; i < images.length; i++) images[i].dispose();
		say("\n" + checks + " checks, " + failures + " failed.\n");
		if (failures > 0) throw new RuntimeException(failures + " checks failed");
		shell.setSize(200, 100);
		setTitle(null);
	}

	public static void main(String[] args) {
		go(new SWT027_01());
	}
}
