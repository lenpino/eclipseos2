/*
 *  SWT009_11.java
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

public class SWT009_11 extends SWTTestCase {

	static {
		STEP = "011";
		TEST = "09";
		DESC = "Tree";
	}

	Shell createTopShell(Display display) {
		return new Shell(display, SWT.SHELL_TRIM | SWT.NO_REDRAW_RESIZE);
	}


	void initComponents() {
		Tree tree;
     shell.setLayout(new GridLayout());
    
    tree = new Tree(shell, SWT.BORDER);
    
    tree.setLayoutData(new GridData(GridData.FILL_BOTH));
    
    TreeItem item = new TreeItem(tree, SWT.NULL);
    item.setText("ITEM");
    
    TreeItem item2 = new TreeItem(item, SWT.NULL);
    item2.setText("ITEM2");
    
    TreeItem item3 = new TreeItem(item2, SWT.NULL);
    item3.setText("ITEM3");
    
    System.out.println("item: " + item.getParent() + ", " + item.getParentItem());
    System.out.println("item2: " + item2.getParent() + ", " + item2.getParentItem());
    
//    System.out.println(tree.getItemCount());
//    System.out.println(tree.getItems().length);
    
    shell.setSize(300, 200);
    shell.open();
                
	}


	void setTitle(String title) {
		super.setTitle(title);
	}
	
	public static void main(String[] args) {
		go(new SWT009_11());
	}

}