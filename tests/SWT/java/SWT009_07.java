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
//            shell.setSize(300, 300);
            shell.setLayout(new FillLayout());
            shell.setText("Group Examples");
            Group group0 = new Group(shell, SWT.NULL);
            group0.setLayout(new FillLayout());
            Label label = new Label(group0, SWT.NULL);
            label.setAlignment(SWT.CENTER);
            label.setText("a group without title.");

            Group group1 = new Group(shell, SWT.NULL);
            group1.setText("SWT.NULL");

            Group group2 = new Group(shell, SWT.SHADOW_ETCHED_IN);
            group2.setText("SWT.SHADOW_ETCHED_IN");  

            Group group3 = new Group(shell, SWT.SHADOW_ETCHED_OUT);
            group3.setText("SWT.SHADOW_ETCHED_OUT");    

            Group group4 = new Group(shell, SWT.SHADOW_IN);
            group4.setText("SWT.SHADOW_IN");  

            Group group5 = new Group(shell, SWT.SHADOW_OUT);
            group5.setText("SWT.SHADOW_OUT");


            Group[] groups = new Group[]{group0, group1, group2, group3, group4, group5};

            for(int i=0; i<groups.length; i++) {
              groups[i].setBounds(10, 10 + i * 50, 300, 40);
            }

            shell.pack();
            shell.open();
                
	}


	void setTitle(String title) {
		super.setTitle(title);
	}
	
	public static void main(String[] args) {
		go(new SWT009_07());
	}

}