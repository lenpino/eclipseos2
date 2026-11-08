/*
 *  SWTTestCase.java
 */

/*
 * Copyright (c) 2002, 2004 EclipseOS2 Team.
 * This file is made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 */

import java.io.PrintWriter;
import java.io.StringWriter;

import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;

/**
 *  Common SWT test case class. It overrides init() and done() methods of the
 *  base class to create and destroy the display. Also the overriden exec()
 *  method calls createTopShell() to create the top shell, initComponents()
 *	to initialize shell components and then opens the top shell and starts the
 *	main execution loop.
 *
 *  Subclasses should normally override only the main() method to call go()
 *  as described in the TestCase class, and the initComponents() method to
 *  create necessary widgets besides the top shell, register necessary
 *	listeners and do other stuff as required by the testcase.
 *
 *	By default initComponents() simply calls the setTitle() method. setTitle()
 *	(which is not intended to be subclassed) can be used by subclasses to set
 *	the current title of the top shell widget, which is composed of the test
 *	description string returned by the getFullName() and the argument of the
 *	setTitle() method. Default initComponents() implementation calls setTitle()
 *	with null argument, which means that only the string returned by
 *	getFullName() is set as the widget title.
 */
 
public class SWTTestCase extends TestCase {

static {
    PART = "SWT";
}

Display display;
Shell shell;

public static void main (String [] args) {
    go (new SWTTestCase ());
}

protected void init () {
    display = new Display ();
}

protected void done () {
    display.dispose ();
}

protected void exec () {
    shell = createTopShell (display);
	initComponents ();
    shell.open ();
    /* Unattended runs: -Dswt.test.autoclose=<milliseconds> closes the window */
    String auto = System.getProperty ("swt.test.autoclose");
    if (auto != null) {
        display.timerExec (Integer.parseInt (auto), new Runnable () {
            public void run () {
                if (!shell.isDisposed ()) shell.dispose ();
            }
        });
    }
    while (!shell.isDisposed ()) {
        if (!display.readAndDispatch ()) display.sleep ();
    }
}

Shell createTopShell (Display display) {
    return new Shell (display);
}

void initComponents() {
	String t = shell.getText();
	if (t == null || t.length() == 0) setTitle (null);
}

void setTitle (String title) {
	String t = getFullName();
	if (title != null) {
		t += " [" + title + "]";
		sayObjective (title);
	}
	shell.setText (t);	
}

}

