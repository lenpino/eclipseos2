/*
*  SWT008_04.java
*/

/*
* Copyright (c) 2002, 2004 EclipseOS2 Team.
* This file is made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
*/

import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;

/**
*  A simple testcase that allows to call the font dialog changing the font of the text displaying 
 * the fonts attribute.
*/

public class SWT008_04 extends SWTTestCase {
        boolean drawCheckers = false;
        Class fmHndCls;
        String platform;        
        Image background = null;
        
	static {
		STEP = "008";
		TEST = "04";
		DESC = "Color Dialog";
	}
	
	public static void main (String [] args) {
		go (new SWT008_04 ());
	}
	
	Shell createTopShell (Display display) {
		return new Shell (display,SWT.SHELL_TRIM | SWT.NO_REDRAW_RESIZE);
	}
	
	void initComponents () {
		super.initComponents();
                //Initiates the font with SystemFont
                
                Composite bottom = new Composite (shell, 0);    
                Composite group1 = new Composite (bottom, SWT.NO_BACKGROUND);
                group1.setLayout (new FillLayout (SWT.HORIZONTAL));

		shell.setLayout(new GridLayout(2, false));
		final Button button = new Button(group1, SWT.PUSH);
		button.setText("Change Color...");
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				ColorDialog dlg = new ColorDialog(shell);
                                dlg.setRGB(new RGB(255, 255, 255));
                                RGB newColor = dlg.open();
				if (newColor == null){
                                    return;
                                }
				else{
                                    shell.setBackground(new Color(display, newColor));
                                    System.out.println("RED = " + newColor.red);
                                    System.out.println("GREEN = " + newColor.green);
                                    System.out.println("BLUE = " + newColor.blue);
                                }
			}
		});

                FormData fdata = new FormData();
                fdata.right = new FormAttachment (100, 0);
                fdata.bottom = new FormAttachment (100, 0);
                group1.setLayoutData (fdata);
                FormLayout formLayout = new FormLayout ();
                bottom.setLayout (formLayout);

                Rectangle dr = display.getBounds ();
		Rectangle sr = new Rectangle (0, 0, 300, 100);
		sr.x = (dr.width-sr.width)/2;
		sr.y = (dr.height-sr.height)/2;
		shell.setBounds (sr);

	}
}


