/*
 *  SWT009_07.java
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

public class SWT009_07 extends SWTTestCase {

	static {
		STEP = "009";
		TEST = "07";
		DESC = "Group";
	}

	Shell createTopShell(Display display) {
		return new Shell(display, SWT.SHELL_TRIM | SWT.NO_REDRAW_RESIZE);
	}


	void initComponents() {
            shell.setText("Hello World Group");
            shell.setSize(200, 200);    
            shell.setText("A Group Example");
            final Group g = new Group(shell, SWT.SHADOW_ETCHED_IN);
            g.setSize(110, 75);
            g.setText("Options Group");
            final Button b1;
            final Button b2;
            final Button b3;
            b1 = new Button(g, SWT.RADIO);
            b1.setBounds(10, 20, 75, 15);
            b1.setText("Option One");
            b2 = new Button(g, SWT.RADIO);
            b2.setBounds(10, 35, 75, 15);
            b2.setText("Option Two");
            b3 = new Button(g, SWT.RADIO);
            b3.setBounds(10, 50, 80, 15);
            b3.setText("Option Three");
            g.pack();
            g.setLocation(20, 20);
            shell.open();
                
	}


	void setTitle(String title) {
		super.setTitle(title);
	}
	
	public static void main(String[] args) {
		go(new SWT009_07());
	}

}