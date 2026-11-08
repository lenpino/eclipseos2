/*
 *  SWT009_16.java
 */

/*
 * Copyright (c) 2002, 2026 EclipseOS2 Team.
 * This file is made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 */

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;

/**
 *  Checks the conversion between control and display coordinates, and the
 *  cursor location. The y axis of PM grows upwards, and the one of SWT
 *  downwards: toDisplay(), toControl(), Display.getCursorLocation() and
 *  Display.setCursorLocation() used to pass the PM y through.
 *
 *  The test is automatic: any failed check throws an exception.
 */
public class SWT009_16 extends SWTTestCase {

	static {
		STEP = "016";
		TEST = "09";
		DESC = "Display coordinates";
	}

	void check(boolean ok, String what) {
		say((ok ? "  ok    " : "  FAIL  ") + what);
		if (!ok) throw new RuntimeException("Check failed: " + what);
	}

	void initComponents() {
		shell.setBounds(100, 150, 400, 300);
		shell.open();
		Rectangle bounds = shell.getBounds();

		Point origin = shell.toDisplay(new Point(0, 0));
		Point moved = shell.toDisplay(new Point(10, 20));
		check(moved.x - origin.x == 10, "x grows to the right: " + origin + " -> " + moved);
		check(moved.y - origin.y == 20, "y grows downwards: " + origin + " -> " + moved);
		check(origin.x >= bounds.x && origin.x < bounds.x + bounds.width
				&& origin.y >= bounds.y && origin.y < bounds.y + bounds.height,
				"the client origin lies inside the shell: " + origin + " in " + bounds);

		check(shell.toControl(origin).equals(new Point(0, 0)), "toControl undoes toDisplay at the origin");
		check(shell.toControl(moved).equals(new Point(10, 20)), "toControl undoes toDisplay: " + shell.toControl(moved));

		Composite child = new Composite(shell, SWT.NONE);
		child.setBounds(30, 40, 100, 80);
		Point childOrigin = child.toDisplay(new Point(0, 0));
		check(childOrigin.x == origin.x + 30 && childOrigin.y == origin.y + 40,
				"a child is placed relative to its parent: " + childOrigin);

		display.setCursorLocation(new Point(500, 400));
		Point cursor = display.getCursorLocation();
		check(cursor.x == 500 && cursor.y == 400, "get/setCursorLocation agree: " + cursor);
		display.setCursorLocation(new Point(500, 450));
		check(display.getCursorLocation().y == 450, "the cursor goes down when y grows");

		say("\nAll checks passed.\n");
		display.asyncExec(new Runnable() {
			public void run() { shell.dispose(); }
		});
	}

	public static void main(String[] args) {
		go(new SWT009_16());
	}

}
