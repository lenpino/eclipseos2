/*
 *  SWT002.java
 *  It's just a bit enhanced o.e.swt.examples.helloworld.HelloWorld1.java
 */

/*
 * Copyright (c) 2002, 2004 EclipseOS2 Team.
 */

/*
 * Copyright (c) 2000, 2002 IBM Corp.  All rights reserved.
 * This file is made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 */

import java.io.PrintWriter;
import java.io.StringWriter;

import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.events.*;

/**
 *  This example demonstrates the minimum amount of code required
 *  to open an SWT Shell and process the events.
 */

public class SWT002 extends TestCase {

static {
    STEP= "002";
    TEST = null;
    DESC = "Simple Shells";
}

public static void main (String [] args) {
    go (new SWT002());
}

protected void exec() {
    System.out.println( "OBJECTIVE: Slightly modified HelloWorld1" );
    System.out.println( "-------------------------------------------\n" );

    Display display = new Display ();
    Shell shell = new SWT002 ().open (display);
    while (!shell.isDisposed ()) {
        if (!display.readAndDispatch ()){
            display.sleep ();
        }
    }
    display.dispose ();
}

public Shell open (Display display) {
    
    DisposeListener dl = new DisposeListener() {
        public void widgetDisposed( DisposeEvent e )
        {
            System.out.println ("DisposeListener.widgetDisposed():");
            if( e.widget instanceof Control )
                System.out.println("    hwnd = "+
                    Integer.toHexString (((Control)e.widget).handle));
            if( e.widget instanceof Decorations )
                System.out.println("    title = ["+
                    ((Decorations)e.widget).getText()+"]");
        }
    };
    
    Shell shell = new Shell (display);
    shell.addDisposeListener(dl);
    shell.setText ("SWT002: Top Shell");
    shell.open ();
    
//    Shell shell2 = new Shell (shell);
//    shell2.addDisposeListener(dl);
//    shell2.setText ("SWT002: Secondary Shell");
//    shell2.open ();
    
    return shell;
}
}
