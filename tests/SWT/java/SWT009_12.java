/*
 *  SWT009_12java
 */

/*
 * Copyright (c) 2002, 2010 EclipseOS2 Team.
 * This file is made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 */

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.graphics.*;

public class SWT009_12 extends SWTTestCase {

	static {
		STEP = "012";
		TEST = "09";
		DESC = "Sash";
	}

	Shell createTopShell(Display display) {
		return new Shell(display, SWT.SHELL_TRIM | SWT.NO_REDRAW_RESIZE);
	}


	void initComponents() {
	    shell.setLayout(new FillLayout());
	    new Text(shell, SWT.BORDER);
	    new Sash(shell, SWT.HORIZONTAL);
	    new Text(shell, SWT.BORDER);
	
	    shell.pack();
	    shell.open();
                
	}


	void setTitle(String title) {
		super.setTitle(title);
	}
	
	public static void main(String[] args) {
		go(new SWT009_12());
	}

}
