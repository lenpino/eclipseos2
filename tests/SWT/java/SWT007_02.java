/*
 *  SWT007_02.java
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

public class SWT007_02 extends SWTTestCase {

static {
    STEP = "007";
    TEST = "02";
    DESC = "Keyboard Input";
}

public static void main (String [] args) {
    go (new SWT007_02 ());
}

Shell createTopShell (Display display) {
    return new Shell (display,
        SWT.SHELL_TRIM | SWT.NO_REDRAW_RESIZE);
}

void initComponents () {
    super.initComponents();
    
    shell.addKeyListener (new KeyListener() {
        public void keyPressed (KeyEvent e) {
            System.out.println( "keyPress (" + e.widget + ")   :" +
                " key=" + Integer.toHexString (e.keyCode) +
                " char=" + Integer.toHexString (e.character) + " [" + e.character + "]" +
                " mask=" + Integer.toHexString (e.stateMask)
            );
        }
        public void keyReleased (KeyEvent e) {
            System.out.println( "keyRelease (" + e.widget + ") :" +
                " key=" + Integer.toHexString (e.keyCode) +
                " char=" + Integer.toHexString (e.character) + " [" + e.character + "]" +
                " mask=" + Integer.toHexString (e.stateMask)
            );
        }
    });

    Rectangle dr = display.getBounds ();
    Rectangle sr = new Rectangle (0, 0, 400, 300);
    sr.x = (dr.width-sr.width)/2;
    sr.y = (dr.height-sr.height)/2;
    shell.setBounds (sr);
}

}

