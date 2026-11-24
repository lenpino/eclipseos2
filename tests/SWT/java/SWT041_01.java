/*
 *  SWT041_01.java
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
 *  Shell.setImage: the image is the icon of the window, in the system menu at the left of the title
 *  bar. WM_SETICON got the handle of a bitmap (an icon of 16x16 is a pair of bitmaps in this port,
 *  not a PM pointer) and the title bar showed nothing. The pixels of the image are looked for in the
 *  corner of the frame.
 */
public class SWT041_01 extends SWTTestCase {

	static {
		STEP = "041";
		TEST = "01";
		DESC = "Shell.setImage";
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
	int pixel(Control c, int x, int y) {
		org.eclipse.swt.graphics.Point p = c.toDisplay(new org.eclipse.swt.graphics.Point(x, y));
		int hps = OS.WinGetScreenPS(OS.HWND_DESKTOP);
		OS.GpiCreateLogColorTable(hps, 0, OS.LCOLF_RGB, 0, 0, null);
		int pel = OS.GpiQueryPel(hps, new int[] {p.x, OS.WinQuerySysValue(OS.HWND_DESKTOP, OS.SV_CYSCREEN) - 1 - p.y}) & 0xFFFFFF;
		OS.WinReleasePS(hps);
		return pel;
	}

	void settle() {
		for (int i = 0; i < 30; i++) while (display.readAndDispatch()) {}
	}

	int pixel(int x, int y) {
		int hps = OS.WinGetScreenPS(OS.HWND_DESKTOP);
		OS.GpiCreateLogColorTable(hps, 0, OS.LCOLF_RGB, 0, 0, null);
		int pel = OS.GpiQueryPel(hps, new int[] {x, OS.WinQuerySysValue(OS.HWND_DESKTOP, OS.SV_CYSCREEN) - 1 - y}) & 0xFFFFFF;
		OS.WinReleasePS(hps);
		return pel;
	}

	/* The best number of pixels of the (opaque) image that are in the same place, looking in the corner of the frame */
	int best(ImageData data, Rectangle frame) {
		int w = data.width, h = data.height, best = 0;
		int[] want = new int[w * h];
		for (int y = 0; y < h; y++) for (int x = 0; x < w; x++) {
			RGB c = data.palette.getRGB(data.getPixel(x, y));
			want[y * w + x] = (c.red << 16) | (c.green << 8) | c.blue;
		}
		for (int oy = 0; oy < 34; oy++) for (int ox = 0; ox < 50; ox++) {
			int n = 0;
			for (int y = 0; y < h; y++) for (int x = 0; x < w; x++) if (pixel(frame.x + ox + x, frame.y + oy + y) == want[y * w + x]) n++;
			if (n > best) best = n;
		}
		return best;
	}

	void initComponents() {
		sayObjective("Automatic checks");
		shell.setBounds(60, 60, 300, 120);
		shell.open();
		settle();
		ImageData small = new ImageData(DIR + "eclipse.gif");
		ImageData big = new ImageData(DIR + "eclipse32.gif");
		Rectangle frame = shell.getBounds();
		int none = best(small, frame);
		say("  without an image: best match " + none + " of " + small.width * small.height);
		check(none < 60, "no image: the corner does not have the image (" + none + " pixels)");
		Image image = new Image(display, small);
		shell.setImage(image);
		settle();
		int got = best(small, frame);
		say("  with eclipse.gif: best match " + got);
		check(got >= 200, "setImage(16x16 bitmap): the icon is in the title bar (" + got + " of " + small.width * small.height + " pixels)");
		check(shell.getImage() == image, "getImage answers the image");
		Image image32 = new Image(display, big);
		shell.setImage(image32);
		settle();
		int got32 = best(small, frame);
		say("  with eclipse32.gif: match with the small one " + got32);
		check(got32 < got, "a different image changes the icon (" + got32 + " < " + got + ")");
		shell.setImage(null);
		settle();
		int back = best(small, frame);
		check(back < 60, "setImage(null) takes the icon away (" + back + " pixels)");
		image.dispose();
		image32.dispose();
		say("\n" + checks + " checks, " + failures + " failed.\n");
		if (failures > 0) throw new RuntimeException(failures + " checks failed");
		setTitle(null);
	}

	public static void main(String[] args) {
		go(new SWT041_01());
	}
}
