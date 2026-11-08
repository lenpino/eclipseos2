/*
 *  SWT009_14.java
 */

/*
 * Copyright (c) 2002, 2026 EclipseOS2 Team.
 * This file is made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 */

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 *  Checks that setText() replaces the text of the controls and accepts an
 *  empty string. Before the fixes, an empty string aborted the VM ("Passed
 *  wrong C array to Release<type>ArrayElements") because the JNI wrappers
 *  cannot release a one-element array, and Text.setText() of a multi line
 *  text inserted the string in front of the existing text.
 *
 *  The test is automatic: any failed check throws an exception.
 */
public class SWT009_14 extends SWTTestCase {

	static {
		STEP = "014";
		TEST = "09";
		DESC = "setText() replaces the text";
	}

	void check(boolean ok, String what) {
		say((ok ? "  ok    " : "  FAIL  ") + what);
		if (!ok) throw new RuntimeException("Check failed: " + what);
	}

	void initComponents() {
		Text single = new Text(shell, SWT.BORDER);
		single.setText("abc");
		check(single.getText().equals("abc"), "single line setText");
		single.setText("de");
		check(single.getText().equals("de"), "single line setText replaces");
		single.setText("");
		check(single.getText().equals(""), "single line setText(\"\") clears");

		Text multi = new Text(shell, SWT.BORDER | SWT.MULTI);
		multi.setText("first");
		multi.setText("second");
		check(multi.getText().equals("second"), "multi line setText replaces: '" + multi.getText() + "'");
		multi.setText("");
		check(multi.getText().equals(""), "multi line setText(\"\") clears: '" + multi.getText() + "'");
		multi.setText("again");
		check(multi.getText().equals("again"), "multi line text can be set after clearing");

		Text readOnly = new Text(shell, SWT.BORDER | SWT.MULTI | SWT.READ_ONLY);
		readOnly.setText("first");
		readOnly.setText("second");
		check(readOnly.getText().equals("second"), "read only multi line setText replaces");

		Button button = new Button(shell, SWT.PUSH);
		button.setText("abc");
		button.setText("");
		check(button.getText().equals(""), "Button.setText(\"\")");
		Label label = new Label(shell, SWT.NONE);
		label.setText("abc");
		label.setText("");
		check(label.getText().equals(""), "Label.setText(\"\")");

		say("\nAll checks passed.\n");
		/* SWTTestCase opens the shell after initComponents(), so close it afterwards */
		display.asyncExec(new Runnable() {
			public void run() { shell.dispose(); }
		});
	}

	public static void main(String[] args) {
		go(new SWT009_14());
	}

}
