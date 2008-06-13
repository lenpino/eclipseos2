/*
 *  SWT009_08.java
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

public class SWT009_08 extends SWTTestCase {

	static {
		STEP = "009";
		TEST = "08";
		DESC = "Menu";
	}

	Shell createTopShell(Display display) {
		return new Shell(display, SWT.SHELL_TRIM | SWT.NO_REDRAW_RESIZE);
	}


	void initComponents() {
            shell.setText("Hello World Menu");
            Menu bar = new Menu(shell, SWT.BAR);
            shell.setMenuBar(bar);
            MenuItem fileItem = new MenuItem(bar, SWT.CASCADE);
            fileItem.setText("File");
            Menu submenu = new Menu(shell, SWT.DROP_DOWN);
            fileItem.setMenu(submenu);
            MenuItem item = new MenuItem(submenu, SWT.PUSH);
            item.addListener(SWT.Selection, new Listener() {
              public void handleEvent(Event e) {
                System.out.println("Select All");
              }
            });
            item.setText("Select & All\tCtrl+A");
            item.setAccelerator(SWT.CTRL + 'A');
            
            //POP-UP
            Composite c1 = new Composite (shell, SWT.BORDER);
            c1.setSize (300, 300);
            Menu menu = new Menu (shell, SWT.POP_UP);
            MenuItem itempu = new MenuItem (menu, SWT.PUSH);
            itempu.setText ("Popup");
            c1.setMenu (menu);
            shell.setMenu (menu);

            
            shell.setSize(300, 300);
            shell.open();
//
//            shell.pack();
//            shell.open();
                
	}


	void setTitle(String title) {
		super.setTitle(title);
	}
	
	public static void main(String[] args) {
		go(new SWT009_08());
	}

}