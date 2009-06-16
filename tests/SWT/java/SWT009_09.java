/*
 *  SWT009_09.java
 */

/*
 * Copyright (c) 2002, 2007 EclipseOS2 Team.
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

public class SWT009_09 extends SWTTestCase {

	static {
		STEP = "009";
		TEST = "09";
		DESC = "Scale";
	}

	Shell createTopShell(Display display) {
		return new Shell(display, SWT.SHELL_TRIM | SWT.NO_REDRAW_RESIZE);
	}


	void initComponents() {
            final Scale scaleH = new Scale(shell, SWT.NULL);
            Scale scaleV = new Scale(shell, SWT.VERTICAL);
//
            scaleH.setBounds(0, 0, 100, 50);
            scaleV.setBounds(0, 50, 50, 100);
//
            System.out.println("Increment: " + scaleH.getIncrement());
            scaleH.setSelection(10);
            System.out.println("Get Selection: " + scaleH.getSelection());
            
            scaleH.addListener(SWT.Selection, new Listener() {
              public void handleEvent(Event event) {
                System.out.println("Scroll detail -> " + scaleH.getSelection());
              }
            });

            shell.pack();
            shell.open();
                
	}


	void setTitle(String title) {
		super.setTitle(title);
	}
	
	public static void main(String[] args) {
		go(new SWT009_09());
	}

}