/*
 *  SWT005_03.java
 */

/*
 * Copyright (c) 2002, 2026 EclipseOS2 Team.
 * This file is made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 */

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 *  Validates the equality of fonts: Font.equals() and Font.hashCode().
 *
 *  Only automatic checks: the window shows a label with the result.
 *
 *  To close the window automatically (for unattended runs) pass the system
 *  property swt.test.autoclose=<milliseconds> to the JVM.
 */
public class SWT005_03 extends SWTTestCase {

	static {
		STEP = "005";
		TEST = "03";
		DESC = "Font equality";
	}

	int checks;

	void check(boolean ok, String what) {
		checks++;
		say((ok ? "  ok    " : "  FAIL  ") + what);
		if (!ok) throw new RuntimeException("Check failed: " + what);
	}

	void initComponents() {
		sayObjective("Automatic checks");
		Label label = new Label(shell, SWT.NONE);
		label.setBounds(10, 10, 300, 30);

		Font a = label.getFont(), b = label.getFont();
		check(a.equals(b) && a.hashCode() == b.hashCode(), "two getFont() of a control are equal (each one is a new FATTRS)");
		check(new Font(display, a.getFontData()[0]).equals(a), "a font made from the data of another one is equal to it");

		Font f = new Font(display, "Helv", 10, SWT.NORMAL);
		Font g = new Font(display, "Helv", 10, SWT.NORMAL);
		check(f.equals(g) && f.hashCode() == g.hashCode(), "two fonts with the same data are equal (on Windows they are not: two HFONTs)");
		Font bold = new Font(display, "Helv", 10, SWT.BOLD);
		Font big = new Font(display, "Helv", 14, SWT.NORMAL);
		check(!f.equals(bold) && !f.equals(big), "another style or size is another font");
		label.setFont(f);
		check(label.getFont().equals(f), "the font that was set is the one that is got");
		check(!f.equals(null) && !f.equals("Helv"), "not equal to null nor to something else");

		g.dispose();
		check(!f.equals(g) && !g.equals(f) && g.hashCode() == 0, "a disposed font is equal to no font, and its hash code does not throw");
		check(g.toString().indexOf("DISPOSED") >= 0, "toString of a disposed font");

		label.setText(checks + " checks passed");
		bold.dispose();
		big.dispose();
		say("\n" + checks + " checks passed.\n");
	}

	public static void main(String[] args) {
		go(new SWT005_03());
	}

}
