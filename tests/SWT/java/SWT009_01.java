/*
 *  SWT009_01.java
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

public class SWT009_01 extends SWTTestCase {

	static {
		STEP = "009";
		TEST = "01";
		DESC = "Label";
	}

	Shell createTopShell(Display display) {
		return new Shell(display, SWT.SHELL_TRIM | SWT.NO_REDRAW_RESIZE);
	}


	void initComponents() {
		shell.setText("Hello World LABEL");
		shell.setSize(200, 200);
                Image image = new Image(display, 16, 16);
                Color color = display.getSystemColor(SWT.COLOR_RED);
                GC gc = new GC(image);
                gc.setBackground(color);
                gc.fillRectangle(image.getBounds());
                gc.dispose();
                shell.setLayout(new FillLayout());
//		Label label = new Label(shell, SWT.WRAP);
                Label label2 = new Label(shell, SWT.BORDER);
                label2.setImage(image);
//		label.setText("Hola Mundo");
//                Point punto = label.computeSize(SWT.DEFAULT,SWT.DEFAULT);
//                System.out.println("WIDTH = " + punto.x);
//                System.out.println("HIGHT = " + punto.y);
//                label.setAlignment(SWT.RIGHT);
//                int align = label.getAlignment();
//                switch(align){
//                    case SWT.RIGHT:{
//                        System.out.println("DERECHA");
//                        break;
//                    }
//                    case SWT.LEFT:{
//                        System.out.println("IZQUIERDA");
//                        break;                        
//                    }
//                    case SWT.CENTER:{
//                        System.out.println("MITAD");
//                        break;
//                    }
//                }
		shell.open();
	}


	void setTitle(String title) {
		super.setTitle(title);
	}
	
	public static void main(String[] args) {
		go(new SWT009_01());
	}

}
