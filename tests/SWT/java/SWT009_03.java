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

public class SWT009_03 extends SWTTestCase {

	static {
		STEP = "009";
		TEST = "03";
		DESC = "List";
	}

	Shell createTopShell(Display display) {
		return new Shell(display, SWT.SHELL_TRIM | SWT.NO_REDRAW_RESIZE);
	}


	void initComponents() {
		shell.setText("Hello World List");
		shell.setSize(300, 300);
                final List l = new List(shell, SWT.MULTI | SWT.BORDER);
                l.setBounds(50, 50, 75, 75);
                l.add("Item One");
                l.add("Item Two");
                l.add("Item Three");
                l.add("Item Four");
                l.add("Item Five");
                System.out.println("Element at index " + 1 + " = " + l.getItem(1));
                System.out.println("Number of elements on the List = " + l.getItemCount());
                System.out.println("Height of an element of the List = " + l.getItemHeight());
                String[] ele = {"Item Six", "Item Seven"};
                System.out.println("Items selected = " + l.getSelectionCount());
                System.out.println("Index of string = " + l.indexOf("Item One"));
//                l.select(1);
//                int[] selected = {1,2,3};
//                l.removeAll();
                l.setItems(ele);
//                l.computeSize(20,20,false);
		shell.open();
	}


	void setTitle(String title) {
		super.setTitle(title);
	}
	
	public static void main(String[] args) {
		go(new SWT009_03());
	}

}
