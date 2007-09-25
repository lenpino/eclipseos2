/*
 *  SWT009_02.java
 */

/*
 * Copyright (c) 2002, 2005 EclipseOS2 Team.
 * This file is made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 */

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.graphics.*;

public class SWT009_02 extends SWTTestCase {

	static {
		STEP = "009";
		TEST = "02";
		DESC = "Text";
	}

	Shell createTopShell(Display display) {
		return new Shell(display, SWT.SHELL_TRIM | SWT.NO_REDRAW_RESIZE);
	}


	void initComponents() {
		shell.setText("Hello World Text");
		shell.setSize(300, 300);
                final Text text1 = new Text(shell, SWT.MULTI | SWT.V_SCROLL);
                text1.setBounds(100, 50, 100, 70);
                final Text text2 = new Text(shell, SWT.SINGLE | SWT.BORDER);
                text2.setBounds(100, 160, 100, 25);
                text1.setText("HOLA");
                text2.setText("HELLO");
                System.out.println("Length = " + text1.getCharCount());
                System.out.println("Text = " + text1.getText());
		shell.open();
	}


	void setTitle(String title) {
		super.setTitle(title);
	}
	
	public static void main(String[] args) {
		go(new SWT009_02());
	}

}
