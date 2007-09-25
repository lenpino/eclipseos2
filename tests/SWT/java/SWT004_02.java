/*
 *  SWT004_02.java
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
 *  This example tests the basics of layout management and some button
 *  functionality.
 *
 *  The Next button is used to go through sub-objectives. The Check Me! button
 *  can be used to disable size/move logging.
 */

public class SWT004_02 extends SWTTestCase {

static {
    STEP= "004";
    TEST = "02";
    DESC = "Buttons";
}

boolean sizeMoveLogging = true;

public static void main (String [] args) {
    go (new SWT004_02 ());
}    

Shell createTopShell (Display display) {
    
    final Shell shell = new Shell (display); //, SWT.SHELL_TRIM | SWT.NO_REDRAW_RESIZE);
    final Color bg = new Color (display, 254, 254, 180);
    
    shell.addDisposeListener (new DisposeListener () {
        public void widgetDisposed (DisposeEvent e) {
            bg.dispose ();
        }
    });

    shell.addShellListener (new ShellAdapter () {
        public void shellIconified (ShellEvent e) {
            say ("Shell Iconified: " + e.widget);
            say ("    minimized = " + shell.getMinimized ());
            say ("    maximized = " + shell.getMaximized ());
        }
        public void shellDeiconified (ShellEvent e) {
            say ("Shell Deiconified: " + e.widget);
            say ("    minimized = " + shell.getMinimized ());
            say ("    maximized = " + shell.getMaximized ());
        }
    });

    ControlListener cl = new ControlAdapter () {
        public void controlMoved (ControlEvent e) {
            if (!sizeMoveLogging) return;
            say ("controlMoved: " + e.widget +
                ", bounds = " + ((Control)e.widget).getBounds ());
        }
        public void controlResized (ControlEvent e) {
            if (!sizeMoveLogging) return;
            say ("controlResized: " + e.widget +
                (e.widget instanceof Shell ?
                    ", client = " + ((Shell)e.widget).getClientArea () :
                    ", bounds = " + ((Control)e.widget).getBounds ()));
        }
    };
    
    SelectionListener sl = new SelectionAdapter () {
        public void widgetSelected (SelectionEvent e) {
            say ("controlSelected: " + e.widget +
                ", time = " + Integer.toHexString (e.time) +
                ", detail = " + Integer.toHexString (e.detail) +
                ", item = " + e.item +
                ", sel = " + (
                    ((e.widget instanceof Button) ?
                        ((Button)e.widget).getSelection () ?
                            "yes" : "no" : "[NA]")
                )
            );
        }
    };

    final PaintListener pl = new PaintListener () {
        public void paintControl(PaintEvent e) {
            GC gc = e.gc;
			Rectangle r = null;
            if (e.widget instanceof Shell)
                r = ((Shell)e.widget).getClientArea();
            else if (e.widget instanceof Control)
                r = ((Control)e.widget).getBounds();
            if (r != null)
                drawGrid (gc, 0, 0, r.width, r.height, 20);
        }
    };

    final Button push = new Button (shell, SWT.PUSH);
    push.addControlListener (cl);
    push.addSelectionListener (sl);
    push.setText ("Push me!");
    push.pack();

    final Button check = new Button (shell, SWT.CHECK);
    check.addControlListener (cl);
    check.addSelectionListener (sl);
    check.setText ("Check me!");
    check.pack();
    check.setLocation (40, 40);
    check.addSelectionListener (new SelectionAdapter () {
        public void widgetSelected (SelectionEvent e) {
            sizeMoveLogging = !((Button)e.widget).getSelection ();
            if (sizeMoveLogging)
                say ("Size/move logging enabled");
            else
                say ("Size/move logging disabled");
        }
    });

    final Button next = new Button (shell, SWT.PUSH);
    next.addControlListener (cl);
    next.addSelectionListener (sl);
    next.setText ("Next >");
    next.setBackground (bg);
    next.setBounds (100, 100, 120, 40);
    next.addSelectionListener (new SelectionAdapter () {
        int timesPressed = 0;
        public void widgetSelected (SelectionEvent e) {
            switch (++timesPressed) {
                case 1:
                    saySubObjective ("\nAdd shell painter");
                    shell.addPaintListener (pl);
                    shell.setBackground (bg);
                    push.setBackground (bg);
                    check.setBackground (bg);
                    break;
                case 2:
                    saySubObjective ("\nResize and move 'Next' button");
                    next.setBounds (50, 10, 200, 40);
                    break;
                case 3:
                    saySubObjective ("\nSet FillLayout into the shell and restore its background color");
                    shell.setBackground (null);
                    shell.setLayout (new FillLayout (SWT.VERTICAL));
                    shell.pack();
                    break;
                case 4:
                    saySubObjective ("\nSet push button painter");
                    push.addPaintListener (pl);
                    push.redraw ();
                    break;
                case 5:
                    saySubObjective ("\nSet RowLayout into the shell");
                    push.removePaintListener (pl);
                    //push.redraw ();
                    RowLayout rowLayout = new RowLayout();
                    rowLayout.wrap = false;
                    rowLayout.pack = false;
                    rowLayout.justify = true;
                    rowLayout.type = SWT.VERTICAL;
                    rowLayout.marginLeft = 5;
                    rowLayout.marginTop = 5;
                    rowLayout.marginRight = 5;
                    rowLayout.marginBottom = 5;
                    rowLayout.spacing = 0;
                    shell.setLayout(rowLayout);
                    shell.pack();
                    
                    next.setText ("Finish");
                    break;
                default:
                    shell.close ();
        	}
        }
    });
        
    shell.addControlListener (cl);

    say ("shell.getBorderWidth () = " + shell.getBorderWidth ());
    say ("button.getBorderWidth () = " + push.getBorderWidth ());

    Rectangle dr = display.getBounds ();
    Rectangle sr = new Rectangle (0, 0, 512, 300); //shell.getBounds ();
    sr.x = (dr.width-sr.width)/2;
    sr.y = (dr.height-sr.height)/2;
    shell.setBounds (sr);

    return shell;
}

void drawGrid (GC gc, int x0, int y0, int width, int height, int step)
{
    int i;
	for (i = 0; i <= width/step; i++)
		gc.drawLine (x0+i*step, y0, x0+i*step, y0+height);
	for (i = 0; i <= height/step; i++)
		gc.drawLine (x0, y0+i*step, x0+width, y0+i*step);
}

}
