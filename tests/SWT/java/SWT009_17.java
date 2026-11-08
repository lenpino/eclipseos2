/*
 *  SWT009_17.java
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
import org.eclipse.swt.widgets.Text;

/**
 *  Checks that setBounds(), setSize() and setLocation() place a Text exactly
 *  where they are asked to, with and without border, single and multi line.
 *  A single line Text with a border (the PM entry field with ES_MARGIN) moved
 *  itself by 3 pixels and grew by 6 each time it was positioned, and only
 *  setBounds() compensated for it, so setSize() and setLocation() misplaced
 *  it, and repeated calls made it drift.
 *
 *  The test is automatic: any failed check throws an exception.
 */
public class SWT009_17 extends SWTTestCase {

	static {
		STEP = "017";
		TEST = "09";
		DESC = "Text bounds with a border";
	}

	int failures;

	void check(boolean ok, String what) {
		say((ok ? "  ok    " : "  FAIL  ") + what);
		if (!ok) failures++;
	}

	void checkText(String name, int style) {
		saySubObjective(name);
		Text text = new Text(shell, style);
		text.setBounds(5, 7, 100, 30);
		check(text.getBounds().equals(new Rectangle(5, 7, 100, 30)), "setBounds(5,7,100,30) -> " + text.getBounds());
		text.setSize(77, 26);
		check(text.getBounds().equals(new Rectangle(5, 7, 77, 26)), "setSize(77,26) keeps the location -> " + text.getBounds());
		text.setLocation(33, 12);
		check(text.getBounds().equals(new Rectangle(33, 12, 77, 26)), "setLocation(33,12) keeps the size -> " + text.getBounds());
		for (int i = 0; i < 3; i++) {
			text.setSize(77, 26);
			text.setLocation(33, 12);
		}
		check(text.getBounds().equals(new Rectangle(33, 12, 77, 26)), "repeating them does not drift -> " + text.getBounds());
		text.setBounds(new Rectangle(0, 0, 60, 24));
		check(text.getBounds().equals(new Rectangle(0, 0, 60, 24)), "setBounds(Rectangle) -> " + text.getBounds());
		check(text.getSize().equals(new Point(60, 24)) && text.getLocation().equals(new Point(0, 0)), "getSize and getLocation agree");
		text.dispose();
	}

	void initComponents() {
		shell.setSize(300, 200);
		checkText("Single line with a border", SWT.BORDER);
		checkText("Single line without a border", SWT.NONE);
		checkText("Multi line with a border", SWT.BORDER | SWT.MULTI);
		checkText("Multi line without a border", SWT.MULTI);

		say("");
		if (failures > 0) throw new RuntimeException(failures + " check(s) failed");
		say("All checks passed.\n");
		display.asyncExec(new Runnable() {
			public void run() { shell.dispose(); }
		});
	}

	public static void main(String[] args) {
		go(new SWT009_17());
	}

}
