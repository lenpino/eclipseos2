/*
 *  SWT027_03.java
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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

/**
 *  Validates the icons of Eclipse in the items of a tree, read from the
 *  screen: as bitmaps, and as icons made from the data and its mask (as JFace
 *  makes them). The transparent pixels show the background of the tree.
 *
 *  The Package Explorer of Eclipse showed no icons (2026-11-17). Windows draws
 *  the images of a tree from an image list, with their mask.
 *
 *  To close the window automatically (for unattended runs) pass the system
 *  property swt.test.autoclose=<milliseconds> to the JVM.
 */
public class SWT027_03 extends SWTTestCase {

	static {
		STEP = "027";
		TEST = "03";
		DESC = "Icons in a tree";
	}

	static final String DIR = System.getProperty("user.dir") + "/tests/SWT/images/eclipse/";
	static final String[] FILES = {"prj_obj.gif", "package_obj.gif", "jar_obj.gif", "fldr_obj.gif"};

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

	/* The icon is searched in the row of the item, left of its text: the offset with the fewest pixels that differ */
	void checkIcon(String what, Tree tree, TreeItem item, ImageData data) {
		Rectangle text = item.getBounds();
		int background = rgb(tree.getBackground().getRGB());
		/* In the row of the item (getBounds starts at the image on PM, at the text on Windows) */
		int left = 0, top = Math.max(0, text.y - 4), width = text.x + 40, height = text.height + 8;
		int[][] screen = new int[height][width];
		for (int y = 0; y < height; y++) for (int x = 0; x < width; x++) screen[y][x] = pel(tree, left + x, top + y);
		int best = Integer.MAX_VALUE, bestOpaque = 0, bestTransparent = 0, bx = 0, by = 0;
		for (int dy = 0; dy + data.height <= height; dy++) {
			for (int dx = 0; dx + data.width <= width; dx++) {
				int opaque = 0, transparent = 0;
				for (int y = 0; y < data.height; y++) {
					for (int x = 0; x < data.width; x++) {
						int pixel = data.getPixel(x, y), got = screen[dy + y][dx + x];
						if (pixel == data.transparentPixel) {
							if (got != background) transparent++;
						} else if (got != rgb(data.palette.getRGB(pixel))) {
							opaque++;
						}
					}
				}
				if (opaque + transparent < best) {
					best = opaque + transparent;
					bestOpaque = opaque;
					bestTransparent = transparent;
					bx = left + dx;
					by = top + dy;
				}
			}
		}
		check(best == 0, what + ": at " + bx + "," + by + " (text at " + text.x + "," + text.y + ", " + text.height + " high), " + bestOpaque + " opaque pixels differ, "
			+ bestTransparent + " transparent ones are not the background " + Integer.toHexString(background));
	}

	void initComponents() {
		sayObjective("Automatic checks");
		ImageData[] datas = new ImageData[FILES.length];
		Image[] bitmaps = new Image[FILES.length], icons = new Image[FILES.length];
		for (int i = 0; i < FILES.length; i++) {
			datas[i] = new ImageData(DIR + FILES[i]);
			bitmaps[i] = new Image(display, datas[i]);
			icons[i] = new Image(display, datas[i], datas[i].getTransparencyMask());
		}
		Shell s = new Shell(display, SWT.NO_TRIM);
		s.setBounds(20, 20, 300, 260);
		Tree tree = new Tree(s, SWT.NONE);
		tree.setBounds(0, 0, 300, 260);
		TreeItem[] bitmapItems = new TreeItem[FILES.length], iconItems = new TreeItem[FILES.length];
		TreeItem root = new TreeItem(tree, SWT.NONE);
		root.setText("Bitmaps");
		root.setImage(bitmaps[0]);
		for (int i = 0; i < FILES.length; i++) {
			bitmapItems[i] = new TreeItem(root, SWT.NONE);
			bitmapItems[i].setText(FILES[i]);
			bitmapItems[i].setImage(bitmaps[i]);
		}
		TreeItem iconRoot = new TreeItem(tree, SWT.NONE);
		iconRoot.setText("Icons");
		iconRoot.setImage(icons[0]);
		for (int i = 0; i < FILES.length; i++) {
			iconItems[i] = new TreeItem(iconRoot, SWT.NONE);
			iconItems[i].setText(FILES[i]);
			iconItems[i].setImage(icons[i]);
		}
		root.setExpanded(true);
		iconRoot.setExpanded(true);
		s.open();
		pump(s);

		saySubObjective("Bitmaps");
		checkIcon("a root item", tree, root, datas[0]);
		for (int i = 0; i < FILES.length; i++) checkIcon(FILES[i], tree, bitmapItems[i], datas[i]);
		saySubObjective("Icons made from data and mask, as JFace does");
		checkIcon("a root item", tree, iconRoot, datas[0]);
		for (int i = 0; i < FILES.length; i++) checkIcon(FILES[i], tree, iconItems[i], datas[i]);
		saySubObjective("A selected item keeps its icon");
		tree.setSelection(new TreeItem[] {bitmapItems[1]});
		pump(s);
		checkIcon("selected " + FILES[1], tree, bitmapItems[1], datas[1]);

		s.dispose();
		for (int i = 0; i < FILES.length; i++) {
			bitmaps[i].dispose();
			icons[i].dispose();
		}
		say("\n" + checks + " checks, " + failures + " failed.\n");
		if (failures > 0) throw new RuntimeException(failures + " checks failed");
		shell.setSize(200, 100);
		setTitle(null);
	}

	public static void main(String[] args) {
		go(new SWT027_03());
	}
}
