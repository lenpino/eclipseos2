/*
 *  SWT032_01.java
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
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

/**
 *  Table images with transparency: the image of a cell (column 0 drawn by the
 *  container, the other columns by the table), as a bitmap and as an icon (JFace
 *  makes the images with transparency as icons), over a red table background.
 *  The opaque pixels must be found as they are and the transparent ones must show red.
 */
public class SWT032_01 extends SWTTestCase {

	static {
		STEP = "032";
		TEST = "01";
		DESC = "Table images with transparency";
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

	static final int RED = 0xFF0000;

	/* Where the image is drawn inside the area: the position where all its opaque pixels are found (or null) */
	Point find(Table table, ImageData data, Rectangle area) {
		for (int oy = area.y; oy + data.height <= area.y + area.height; oy++) {
			for (int ox = area.x; ox + data.width <= area.x + area.width; ox++) {
				boolean all = true;
				for (int y = 0; y < data.height && all; y++) for (int x = 0; x < data.width; x++) {
					int p = data.getPixel(x, y);
					if (p == data.transparentPixel) continue;
					if (pel(table, ox + x, oy + y) != rgb(data.palette.getRGB(p))) { all = false; break; }
				}
				if (all) return new Point(ox, oy);
			}
		}
		return null;
	}

	void checkCell(Table table, TableItem item, int column, String name, ImageData data, boolean icon) {
		String what = name + (icon ? " as icon" : " as bitmap") + " in column " + column;
		Rectangle b = item.getBounds(column);
		/* column 0: the images are in the field before the text */
		Rectangle area = column == 0 ? new Rectangle(0, b.y, b.x + 2, b.height) : new Rectangle(b.x, b.y, Math.max(b.width, 40), b.height);
		/* Search only a few pixels around: column 0 starts at the left of the table */
		Point at = find(table, data, area);
		check(at != null, what + ": the opaque pixels are in the cell" + (at == null ? " (cell " + b + ")" : " at " + at));
		if (at == null) return;
		int wrong = 0, n = 0;
		for (int y = 0; y < data.height; y++) for (int x = 0; x < data.width; x++) {
			if (data.getPixel(x, y) != data.transparentPixel) continue;
			n++;
			if (pel(table, at.x + x, at.y + y) != RED) wrong++;
		}
		check(n > 0 && wrong == 0, what + ": " + n + " transparent pixels, " + wrong + " not red");
	}

	void initComponents() {
		sayObjective("Automatic checks");
		Table table = new Table(shell, SWT.SINGLE | SWT.FULL_SELECTION);
		table.setHeaderVisible(true);
		table.setBackground(new Color(display, 255, 0, 0));
		TableColumn c0 = new TableColumn(table, SWT.LEFT);
		c0.setText("A");
		c0.setWidth(120);
		TableColumn c1 = new TableColumn(table, SWT.LEFT);
		c1.setText("B");
		c1.setWidth(120);
		String[] names = {"fldr_obj.gif", "new_wiz_c.gif"};
		ImageData[] datas = new ImageData[names.length];
		Image[] bitmaps = new Image[names.length], icons = new Image[names.length];
		for (int i = 0; i < names.length; i++) {
			datas[i] = new ImageData(DIR + names[i]);
			bitmaps[i] = new Image(display, datas[i]);
			icons[i] = new Image(display, datas[i], datas[i].getTransparencyMask());
		}
		/* row 0: icon in column 0, bitmap in column 1; row 1: bitmap in column 0, icon in column 1 */
		TableItem a = new TableItem(table, SWT.NONE);
		a.setText(new String[] {"one", "uno"});
		a.setImage(0, icons[0]);
		a.setImage(1, bitmaps[1]);
		TableItem b = new TableItem(table, SWT.NONE);
		b.setText(new String[] {"two", "dos"});
		b.setImage(0, bitmaps[0]);
		b.setImage(1, icons[1]);
		table.setBounds(0, 0, 300, 120);
		shell.setSize(330, 200);
		shell.open();
		pump(table);
		checkCell(table, a, 0, names[0], datas[0], true);
		checkCell(table, a, 1, names[1], datas[1], false);
		checkCell(table, b, 0, names[0], datas[0], false);
		checkCell(table, b, 1, names[1], datas[1], true);
		say("\n" + checks + " checks, " + failures + " failed.\n");
		if (failures > 0) throw new RuntimeException(failures + " checks failed");
		setTitle(null);
	}

	public static void main(String[] args) {
		go(new SWT032_01());
	}
}
