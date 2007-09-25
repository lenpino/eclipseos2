/*
*  SWT008_01.java
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
*  A simple testcase that listents for key press and key release events
*  (SWT.KeyEvent) ant prints the event info to the console.
*/

public class SWT008_01 extends SWTTestCase {
	
	static {
		STEP = "008";
		TEST = "01";
		DESC = "Message Dialog";
	}
	
	public static void main (String [] args) {
		go (new SWT008_01 ());
	}
	
	Shell createTopShell (Display display) {
		return new Shell (display,SWT.SHELL_TRIM | SWT.NO_REDRAW_RESIZE);
	}
	
	void initComponents () {
		super.initComponents();
		//Warning button
		Button button = new Button(shell, SWT.PUSH);
		button.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				MessageBox messageBox = new MessageBox(shell, SWT.ICON_WARNING | SWT.ABORT | SWT.RETRY | SWT.IGNORE);
				messageBox.setText("Warning");
				messageBox.setMessage("Save the changes before exiting?");
				int buttonID = messageBox.open();
				System.out.println(buttonID);
			}
		});
		button.setText("Warning!");
		button.setBounds(0, 10, 80, 30);
		
		//Error button
		Button buttonError = new Button(shell, SWT.PUSH);
		buttonError.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK | SWT.CANCEL);
				messageBox.setText("Error");
				messageBox.setMessage("The program is not working!");
				int buttonID = messageBox.open();
				System.out.println(buttonID);
			}
		});
		buttonError.setText("Error!");
		buttonError.setBounds(0, 50, 80, 30);
		
		//Information button
		Button buttonInfo = new Button(shell, SWT.PUSH);
		buttonInfo.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				MessageBox messageBox = new MessageBox(shell, SWT.ICON_INFORMATION | SWT.YES | SWT.NO);
				messageBox.setText("Information");
				messageBox.setMessage("You have a bug in your code");
				int buttonID = messageBox.open();
				System.out.println(buttonID);
			}
		});
		buttonInfo.setText("Information!");
		buttonInfo.setBounds(0, 90, 80, 30);
		
		//Question  button
		Button buttonQ = new Button(shell, SWT.PUSH);
		buttonQ.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				MessageBox messageBox = new MessageBox(shell, SWT.ICON_QUESTION | SWT.OK | SWT.CANCEL);
				messageBox.setText("Question");
				messageBox.setMessage("Did you sleep enough?");
				int buttonID = messageBox.open();
				System.out.println(buttonID);
			}
		});
		buttonQ.setText("Question!");
		buttonQ.setBounds(0, 130, 80, 30);
		
		//Working button
		Button buttonW = new Button(shell, SWT.PUSH);
		buttonW.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				MessageBox messageBox = new MessageBox(shell, SWT.ICON_INFORMATION | SWT.OK | SWT.CANCEL);
				messageBox.setText("Working");
				messageBox.setMessage("You are still compiling");
				int buttonID = messageBox.open();
				System.out.println(buttonID);
			}
		});
		buttonW.setText("Working!");
		buttonW.setBounds(0, 170, 80, 30);
		
		Rectangle dr = display.getBounds ();
		Rectangle sr = new Rectangle (0, 0, 280, 250);
		sr.x = (dr.width-sr.width)/2;
		sr.y = (dr.height-sr.height)/2;
		shell.setBounds (sr);
	}
	
}

