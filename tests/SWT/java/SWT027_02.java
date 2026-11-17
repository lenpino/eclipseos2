/*
 *  SWT027_02.java
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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

/**
 *  Validates the icons of Eclipse in tool items and buttons, read from the
 *  screen: the transparent pixels show the face of the button, and a disabled
 *  tool item shows its disabled image as it is.
 *
 *  In Eclipse (2026-11-17) the transparent part of the icons in buttons was
 *  black (the button copied the bitmap, SRCCOPY), and the disabled tool items
 *  were dithered boxes (a halftone over the disabled image).
 *
 *  To close the window automatically (for unattended runs) pass the system
 *  property swt.test.autoclose=<milliseconds> to the JVM.
 */
public class SWT027_02 extends SWTTestCase {

	static {
		STEP = "027";
		TEST = "02";
		DESC = "Icons in tool items and buttons";
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

	Button buttonOf(ToolBar bar, ToolItem item) {
		Rectangle ib = item.getBounds();
		Control[] children = bar.getChildren();
		for (int i = 0; i < children.length; i++) {
			if (!(children[i] instanceof Button)) continue;
			Rectangle r = children[i].getBounds();
			if (r.x == ib.x && r.y == ib.y && (children[i].getStyle() & SWT.ARROW) == 0) return (Button) children[i];
		}
		return null;
	}

	/* The icon is searched in the button: the offset with the fewest pixels that differ */
	void checkIcon(String what, Control button, ImageData data) {
		Rectangle b = button.getBounds();
		int face = rgb(display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND).getRGB());
		int[][] screen = new int[b.height][b.width];
		for (int y = 0; y < b.height; y++) for (int x = 0; x < b.width; x++) screen[y][x] = pel(button, x, y);
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
		check(best == 0, what + ": at " + bx + "," + by + " in the button, " + bestOpaque + " opaque pixels differ, "
			+ bestTransparent + " transparent ones are not the face " + Integer.toHexString(face));
	}

	void initComponents() {
		sayObjective("Automatic checks");
		ImageData color = new ImageData(DIR + "new_wiz_c.gif");
		ImageData gray = new ImageData(DIR + "new_wiz_e.gif");
		ImageData disabled = new ImageData(DIR + "new_wiz_d.gif");
		ImageData folder = new ImageData(DIR + "fldr_obj.gif");
		Image colorImage = new Image(display, color), grayImage = new Image(display, gray);
		Image disabledImage = new Image(display, disabled), folderImage = new Image(display, folder);

		Shell s = new Shell(display, SWT.NO_TRIM);
		s.setBounds(20, 20, 300, 80);
		ToolBar bar = new ToolBar(s, SWT.FLAT);
		ToolItem enabledItem = new ToolItem(bar, SWT.PUSH);
		enabledItem.setImage(colorImage);
		ToolItem disabledItem = new ToolItem(bar, SWT.PUSH);
		disabledItem.setImage(grayImage);
		disabledItem.setDisabledImage(disabledImage);
		disabledItem.setEnabled(false);
		ImageData saveDisabled = new ImageData(DIR + "save_edit_d.gif"), printDisabled = new ImageData(DIR + "print_edit_d.gif");
		ImageData saveGray = new ImageData(DIR + "save_edit_e.gif");
		Image saveDisabledImage = new Image(display, saveDisabled), printDisabledImage = new Image(display, printDisabled);
		Image saveGrayImage = new Image(display, saveGray);
		ToolItem saveItem = new ToolItem(bar, SWT.PUSH);
		saveItem.setImage(saveGrayImage);
		saveItem.setDisabledImage(saveDisabledImage);
		saveItem.setEnabled(false);
		ToolItem printItem = new ToolItem(bar, SWT.PUSH);
		printItem.setImage(saveGrayImage);
		printItem.setDisabledImage(printDisabledImage);
		printItem.setEnabled(false);
		Image copyImage = new Image(display, saveDisabledImage, SWT.IMAGE_COPY);
		ToolItem copyItem = new ToolItem(bar, SWT.PUSH);
		copyItem.setImage(copyImage);
		Image disableImage = new Image(display, saveGrayImage, SWT.IMAGE_DISABLE);
		ToolItem disableItem = new ToolItem(bar, SWT.PUSH);
		disableItem.setImage(disableImage);
		/* As JFace makes the images of the tool items: an icon from the data and its mask */
		Image iconImage = new Image(display, saveDisabled, saveDisabled.getTransparencyMask());
		ToolItem iconItem = new ToolItem(bar, SWT.PUSH);
		iconItem.setImage(iconImage);
		Image colorIcon = new Image(display, color, color.getTransparencyMask());
		ToolItem colorIconItem = new ToolItem(bar, SWT.PUSH);
		colorIconItem.setImage(colorIcon);
		bar.setBounds(0, 0, 300, 30);
		Button push = new Button(s, SWT.PUSH);
		push.setImage(folderImage);
		push.setBounds(10, 40, 30, 30);
		s.open();
		pump(s);

		saySubObjective("Tool items");
		checkIcon("an enabled tool item (ctool16)", buttonOf(bar, enabledItem), color);
		checkIcon("a disabled tool item shows its disabled image (dtool16), not dithered", buttonOf(bar, disabledItem), disabled);
		disabledItem.setEnabled(true);
		pump(s);
		checkIcon("enabled again it shows its image (etool16)", buttonOf(bar, disabledItem), gray);
		checkIcon("disabled save (dtool16, 2 bits)", buttonOf(bar, saveItem), saveDisabled);
		checkIcon("disabled print (dtool16, 1 bit)", buttonOf(bar, printItem), printDisabled);
		saveItem.setEnabled(true);
		pump(s);
		checkIcon("enabled save (etool16, 3 bits)", buttonOf(bar, saveItem), saveGray);
		checkIcon("a copy (IMAGE_COPY) of an icon", buttonOf(bar, copyItem), saveDisabled);
		ImageData disableData = disableImage.getImageData();
		say("  IMAGE_DISABLE: depth " + disableData.depth + ", transparent pixel " + disableData.transparentPixel);
		checkIcon("the IMAGE_DISABLE version of an icon", buttonOf(bar, disableItem), disableData);
		checkIcon("an icon made from data and mask, as JFace does (dtool16)", buttonOf(bar, iconItem), saveDisabled);
		checkIcon("an icon made from data and mask, as JFace does (ctool16)", buttonOf(bar, colorIconItem), color);
		saySubObjective("Buttons");
		checkIcon("a push button with an image", push, folder);

		s.dispose();
		colorImage.dispose();
		grayImage.dispose();
		disabledImage.dispose();
		folderImage.dispose();
		say("\n" + checks + " checks, " + failures + " failed.\n");
		if (failures > 0) throw new RuntimeException(failures + " checks failed");
		shell.setSize(200, 100);
		setTitle(null);
	}

	public static void main(String[] args) {
		go(new SWT027_02());
	}
}
