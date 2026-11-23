/*
 *  SWT035_01.java
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
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

/**
 *  TreeItem.getBounds: as on Windows (TVM_GETITEMRECT with the text flag) the rectangle is
 *  the one of the text, not the one of the image and the text. The image is to its left.
 *  Seen on a red tree: the ink of the text starts at bounds.x and the image ink ends before it.
 */
public class SWT035_01 extends SWTTestCase {

	static {
		STEP = "035";
		TEST = "01";
		DESC = "TreeItem.getBounds is the text";
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
	/* The first and the last x of the row (inside from, to) that are not the background */
	int[] span(Tree tree, Rectangle row, int from, int to, int face) {
		int first = -1, last = -1;
		for (int x = from; x < to; x++) {
			for (int y = row.y; y < row.y + row.height; y++) {
				if (pel(tree, x, y) != face) { if (first == -1) first = x; last = x; break; }
			}
		}
		return new int[] {first, last};
	}

	/* The pixels in the rectangle that are neither the background nor the black of the text (the image has colors) */
	int notText(Tree tree, Rectangle b, int face) {
		int n = 0;
		for (int y = b.y; y < b.y + b.height; y++) for (int x = b.x; x < b.x + b.width; x++) {
			int p = pel(tree, x, y);
			if (p != face && p != 0) n++;
		}
		return n;
	}

	void initComponents() {
		sayObjective("Automatic checks");
		shell.setLayout(null);
		shell.setSize(300, 260);
		shell.open();
		pump(shell);
		Tree tree = new Tree(shell, SWT.SINGLE);
		tree.setBackground(new Color(display, 255, 0, 0));
		tree.setBounds(10, 10, 270, 200);
		ImageData data = new ImageData(DIR + "fldr_obj.gif");
		Image image = new Image(display, data, data.getTransparencyMask());
		TreeItem withImage = new TreeItem(tree, SWT.NONE);
		withImage.setText("With image");
		withImage.setImage(image);
		TreeItem plain = new TreeItem(tree, SWT.NONE);
		plain.setText("Without image");
		TreeItem child = new TreeItem(withImage, SWT.NONE);
		child.setText("Child with image");
		child.setImage(image);
		withImage.setExpanded(true);
		pump(tree);
		int face = 0xFF0000, width = tree.getClientArea().width;
		TreeItem[] all = {withImage, plain, child};
		for (int i = 0; i < all.length; i++) {
			TreeItem item = all[i];
			Rectangle b = item.getBounds();
			int[] ink = span(tree, b, 0, width, face);
			say("  '" + item.getText() + "' bounds " + b + ", ink from " + ink[0] + " to " + ink[1]);
			int[] textInk = span(tree, b, b.x, width, face);
			check(textInk[0] != -1 && textInk[0] <= b.x + 3, "'" + item.getText() + "': the text starts at the left of the bounds (" + textInk[0] + ", x " + b.x + ")");
			check(textInk[1] <= b.x + b.width + 1, "'" + item.getText() + "': the text ends inside the bounds (" + textInk[1] + ", right " + (b.x + b.width) + ")");
			check(notText(tree, b, face) == 0, "'" + item.getText() + "': inside the bounds there is only text, no image (" + notText(tree, b, face) + " pixels of other colors)");
			if (item.getImage() != null) {
				int[] left = span(tree, b, 0, b.x, face);
				check(left[0] != -1 && left[1] < b.x, "'" + item.getText() + "': the image is to the left of the bounds (ink " + left[0] + " to " + left[1] + ", x " + b.x + ")");
				/* The image is the 16 pixels before the gap that precedes the text */
				check(notText(tree, new Rectangle(b.x - 22, b.y, 16, b.height), face) > 0, "'" + item.getText() + "': the image is in the 16 pixels before the bounds");
			}
		}
		Rectangle w = withImage.getBounds(), p = plain.getBounds();
		check(w.height == p.height && w.height > 0, "all the rows are as high: " + w.height + ", " + p.height);
		check(Math.abs(w.x - p.x) <= 1, "an item with an image starts its text where one without image does: " + w.x + " and " + p.x);
		check(tree.getItem(new Point(w.x - 14, w.y + w.height / 2)) == withImage, "a point on the image still gives the item");
		check(tree.getItem(new Point(w.x + 2, w.y + w.height / 2)) == withImage, "a point on the text gives the item");
		say("\n" + checks + " checks, " + failures + " failed.\n");
		if (failures > 0) throw new RuntimeException(failures + " checks failed");
		setTitle(null);
	}

	public static void main(String[] args) {
		go(new SWT035_01());
	}
}
