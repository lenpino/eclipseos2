/*
 *  SWT004_01.java
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
 *  This example tests the sizing/positioning methods of Shell widgets.
 *  It creates five windows arranged chessboard fashion with the top window
 *  in the center and secondary windows exactly in the corners of the screen.
 *  First time the sizing/positioning is made using setBounds() method,
 *  second time (after the top window is closed) -- using setLocation()/setSize()
 *  methods. 
 */

public class SWT004_01 extends SWTTestCase {

static {
    STEP= "004";
    TEST = "01";
    DESC = "Sizing/Positioning";	
}

boolean separate;    
String title;

public static void main (String [] args) {
    go (new SWT004_01 ());
}    

protected void exec () {
    sayObjective ("Test of size/position methods");
    title = "SWT004_01 [Part 1]: ";
    saySubObjective ("Part 1: Positioning using setBounds():");
    super.exec ();
    separate = true;
    title = "SWT004_01 [Part 2]: ";
    saySubObjective ("\nPart 2: Positioning using setLocation()/setSize():");
    super.exec ();
}
    
void testSetBounds (Shell shell, int x, int y, int w, int h, boolean separate) {
    if (separate) {
        shell.setLocation (x, y);
        shell.setSize (w, h);
    }
    else
        shell.setBounds (x, y, w, h);
}

void testMinMax (Shell shell) {
    System.out.println ("Min/Max Subtest [" + shell.getText () + "]:");
    
    System.out.println ("    bounds when normal:");
    System.out.println ("        shell.getBounds() = " + shell.getBounds ());
    System.out.println ("        shell.getClientArea() = " + shell.getClientArea ());

    shell.setMinimized (true);
    System.out.println ("    bounds when minimized:");
    System.out.println ("        shell.getBounds() = " + shell.getBounds ());
    System.out.println ("        shell.getClientArea() = " + shell.getClientArea ());

    shell.setMaximized (true);
    System.out.println ("    bounds when maximized:");
    System.out.println ("        shell.getBounds() = " + shell.getBounds ());
    System.out.println ("        shell.getClientArea() = " + shell.getClientArea ());
    
    shell.setMaximized (false);
}

Shell createTopShell (Display display) {
    
    Shell shell = new Shell (display);

    DisposeListener dl = new DisposeListener () {
        public void widgetDisposed (DisposeEvent e)
        {
            System.out.println ("DisposeListener.widgetDisposed():");
            if (e.widget instanceof Control)
            {
                System.out.println ("    hwnd = " +
                    Integer.toHexString (((Control)e.widget).handle));
                System.out.println ("    bounds = " +
                    ((Control)e.widget).getBounds());
            }
            if (e.widget instanceof Decorations)
                System.out.println ("    title = [" +
                    ((Decorations)e.widget).getText() + "]");
        }
    };
    shell.addDisposeListener (dl);
    shell.setText (title + "Top Shell");
    
    //Rectangle r = display.getBounds ();
    Rectangle r = display.getClientArea ();
    int sh, sw, ssh, ssw;
    ssw = r.width / 3;
    sw = r.width - ssw * 2;
    ssh = r.height / 3;
    sh = r.height - ssh * 2;
    
    System.out.println ("bounds after creation: " + shell.getBounds()); 
    testSetBounds (shell, ssw, ssh, sw, sh, separate);
    
    for( int i = 1; i <= 4; i++ )
    {
        Shell s = new Shell (shell);
        System.out.println ("bounds after creation: " + s.getBounds()); 
        s.addDisposeListener(dl);
        s.setText (title + "Secondary Shell #" + i);
        switch( i ) {
            case  1:
                testSetBounds (s, 0, 0, ssw, ssh, separate); break;
            case  2:
                testSetBounds (s, ssw+sw, 0, ssw, ssh, separate); break;
            case  3:
                testSetBounds (s, 0, ssh+sh, ssw, ssh, separate); break;
            case  4:
                testSetBounds (s, ssw+sw, ssh+sh, ssw, ssh, separate); break;
        }
        s.open ();
        testMinMax (s);
    }

    shell.open ();
    testMinMax (shell);
    return shell;
}

}
