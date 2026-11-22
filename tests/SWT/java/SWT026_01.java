/*
 *  SWT026_01.java
 */

/*
 * Copyright (c) 2002, 2026 EclipseOS2 Team.
 * This file is made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 */

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.internal.pm.OS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 *  Validates what the dialogs of Eclipse do with a Label: an image taken
 *  away with setImage (null), and text after an image.
 *
 *  Every dialog of JFace with a title area (the wizards, the external tools)
 *  failed to open (2026-11-17): TitleAreaDialog.showMessage calls
 *  setImage (null) on its label when the message has no icon, and
 *  Label.setImage used the handle of the null image.
 *
 *  All the checks are automatic; the window then shows the labels.
 *
 *  To close the window automatically (for unattended runs) pass the system
 *  property swt.test.autoclose=<milliseconds> to the JVM.
 */
public class SWT026_01 extends SWTTestCase {

	static {
		STEP = "026";
		TEST = "01";
		DESC = "Label.setImage (null)";
	}

	int checks;

	Shell createTopShell(Display display) {
		return new Shell(display, SWT.SHELL_TRIM | SWT.NO_REDRAW_RESIZE);
	}

	void check(boolean ok, String what) {
		checks++;
		say((ok ? "  ok    " : "  FAIL  ") + what);
		if (!ok) throw new RuntimeException("Check failed: " + what);
	}

	/* The type of a static of PM is a value in these bits, not a bit of its own */
	static final int TYPE = 0x1F;

	int styleOf(Label label) {
		return OS.WinQueryWindowULong(label.handle, OS.QWL_STYLE);
	}

	Image makeImage() {
		Image image = new Image(display, 16, 16);
		GC gc = new GC(image);
		gc.setBackground(display.getSystemColor(SWT.COLOR_RED));
		gc.fillRectangle(0, 0, 16, 16);
		gc.dispose();
		return image;
	}

	void checkImage(Shell s, Image image) {
		saySubObjective("setImage (null)");
		Label label = new Label(s, SWT.NONE);
		boolean thrown = false;
		try {
			label.setImage(null);
		} catch (RuntimeException e) {
			thrown = true;
		}
		check(!thrown, "setImage (null) on a label without an image does not throw");
		check(label.getImage() == null, "getImage is null");

		label.setImage(image);
		/* The label paints the image itself (step 029): the static keeps its type, PM would resize an SS_BITMAP or SS_ICON one */
		check(label.getImage() == image && (styleOf(label) & TYPE) == OS.SS_TEXT, "setImage keeps the image in the label, the static stays SS_TEXT");
		thrown = false;
		try {
			label.setImage(null);
		} catch (RuntimeException e) {
			thrown = true;
		}
		check(!thrown, "setImage (null) takes the image away without throwing (TitleAreaDialog)");
		check(label.getImage() == null && (styleOf(label) & TYPE) == OS.SS_TEXT,
				"no image left, neither in SWT nor in PM (the type is SS_TEXT again)");
		label.setText("Text after an image");
		check("Text after an image".equals(label.getText()), "the label takes a text after its image");
		label.setImage(image);
		label.setText("Text over an image");
		check((styleOf(label) & TYPE) == OS.SS_TEXT,
				"setText after setImage makes a text (SS_BITMAP | SS_TEXT would be SS_GROUPBOX), type " + (styleOf(label) & TYPE));
		label.dispose();

		Label separator = new Label(s, SWT.SEPARATOR | SWT.HORIZONTAL);
		separator.setImage(null);
		check(separator.getImage() == null, "a separator ignores setImage (null)");
		separator.dispose();
	}

	void initComponents() {
		sayObjective("Automatic checks");
		Image image = makeImage();
		Shell s = new Shell(display);
		checkImage(s, image);
		s.dispose();
		say("\n" + checks + " checks passed.\n");

		sayObjective("Interactive: a red square, then a label whose image was taken away (only its text)");
		Label withImage = new Label(shell, SWT.NONE);
		withImage.setImage(image);
		withImage.setBounds(10, 10, 16, 16);
		Label taken = new Label(shell, SWT.NONE);
		taken.setImage(image);
		taken.setImage(null);
		taken.setText("No image here");
		taken.setBounds(10, 40, 200, 20);
		shell.setSize(260, 120);
		setTitle(null);
	}

	public static void main(String[] args) {
		go(new SWT026_01());
	}
}
