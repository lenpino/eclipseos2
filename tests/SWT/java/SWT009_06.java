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
            shell.setText("Tab Folder Example");
            shell.setSize(450, 250);
            final TabFolder tabFolder = new TabFolder(shell, SWT.BORDER);
            for (int loopIndex = 0; loopIndex < 10; loopIndex++) {
                TabItem tabItem = new TabItem(tabFolder, SWT.NULL);
                tabItem.setText("Tab " + loopIndex);
                Text text = new Text(tabFolder, SWT.BORDER);
                text.setText("This is page " + loopIndex);
                tabItem.setControl(text);
            }
            tabFolder.setSize(400, 200);
            shell.open();
                
	}


	void setTitle(String title) {
		super.setTitle(title);
	}
	
	public static void main(String[] args) {
		go(new SWT009_06());
	}

}