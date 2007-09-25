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

public class SWT009_05 extends SWTTestCase {

	static {
		STEP = "009";
		TEST = "05";
		DESC = "Slider";
	}

	Shell createTopShell(Display display) {
		return new Shell(display, SWT.SHELL_TRIM | SWT.NO_REDRAW_RESIZE);
	}


	void initComponents() {
            shell.setText("Hello World Slider");
            shell.setSize(300, 300);
            Slider slider = new Slider(shell, SWT.HORIZONTAL);
            slider.setBounds(10, 10, 200, 32);
            slider.addListener(SWT.Selection, new Listener() {
              public void handleEvent(Event event) {
                String string = "SWT.NONE";
                switch (event.detail) {
                case SWT.DRAG:
                  string = "SWT.DRAG";
                  break;
                case SWT.HOME:
                  string = "SWT.HOME";
                  break;
                case SWT.END:
                  string = "SWT.END";
                  break;
                case SWT.ARROW_DOWN:
                  string = "SWT.ARROW_DOWN";
                  break;
                case SWT.ARROW_UP:
                  string = "SWT.ARROW_UP";
                  break;
                case SWT.PAGE_DOWN:
                  string = "SWT.PAGE_DOWN";
                  break;
                case SWT.PAGE_UP:
                  string = "SWT.PAGE_UP";
                  break;
                }
                System.out.println("Scroll detail -> " + string);
              }
            });

//            final Combo combo = new Combo(shell, SWT.READ_ONLY);
//            combo.setBounds(50, 50, 150, 65);
//            String[] languages = new String[]{"Java", "C", "C++", "SmallTalk"};
//            combo.setItems(languages);
//            combo.add("Hola", 0);
//            System.out.println(combo.indexOf("C"));
            shell.open();
                
	}


	void setTitle(String title) {
		super.setTitle(title);
	}
	
	public static void main(String[] args) {
		go(new SWT009_05());
	}

}