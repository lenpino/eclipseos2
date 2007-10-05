/*
 *  SWT009_04.java
 */

/*
 * Copyright (c) 2002, 2005 EclipseOS2 Team.
 * This file is made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 */

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.graphics.*;

public class SWT009_06 extends SWTTestCase {

	static {
		STEP = "009";
		TEST = "06";
		DESC = "TabFolder";
	}

	Shell createTopShell(Display display) {
		return new Shell(display, SWT.SHELL_TRIM | SWT.NO_REDRAW_RESIZE);
	}


	void initComponents() {
            shell.setText("Hello World TabFolder");
            shell.setSize(300, 300);
            shell.setLayout(new FillLayout());
            shell.setText("Simple Tabs");
            TabFolder tabFolder = new TabFolder(shell, SWT.NONE);
            TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
            shell.open();
                
	}


	void setTitle(String title) {
		super.setTitle(title);
	}
	
	public static void main(String[] args) {
		go(new SWT009_06());
	}

}