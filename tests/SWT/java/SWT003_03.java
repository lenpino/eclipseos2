/*
 *  SWT003_03.java
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

/**
 *  This example tests partial invalidation and clipping.
 * 	When the test is started it draws the grid 20 by 20 pixels.
 * 
 * 	Upon the first closure the grid 10 by 10 pixels is drawn and
 * 	only the part of the window is invalidated to see the effect.
 *
 *  When the shell is closed for the 2nd time the grid 5 by 5 is
 *  drawn after the clipping rectangle is set. Other (clipped) parts
 *  of the shell should stay dirty when, for example, another window
 *  is being moved over them.
 *
 *  When the shell is closed for the 3rd time the same as above happens,
 *  but the clipping area is the region that consists of two intersecting
 *  rectangles.
 *
 *  Being closed for the 4rd time the shell turns off its redraw state causing
 *  all further drawing operations to be ignored. The screen area under the
 *  shell should become dirty.
 *
 *  And upon the final closure the shell sets the redraw state back to true
 *  which should result into the correct cleanup (no screen area should be
 *  left durty after the shell is closed).
 */

public class SWT003_03 extends SWTTestCase {

static {
    STEP= "003";
    TEST = "03";
    DESC = "Invalidation & Clipping";	
}    

int closes = 0;    

public static void main (String [] args) {
    go (new SWT003_03 ());
}

Shell createTopShell (Display display) {
    
    final Shell shell = new Shell (display,
        SWT.SHELL_TRIM | /*SWT.NO_REDRAW_RESIZE |*/ SWT.NO_MERGE_PAINTS);
    
    shell.addShellListener (new ShellAdapter () {
        public void shellClosed (ShellEvent e) {
        	closes++;
        	e.doit = false;
        	switch (closes) {
        		case 1:
                    saySubObjective ("\nInvalidate");
                    shell.redraw (110, 110, 101, 101, true);
		            break;
        		case 2:
                    saySubObjective ("\nRectangle Clip");
                    shell.redraw ();
                    break;
        		case 3:
                    saySubObjective ("\nRegion Clip");
                    shell.redraw ();
                    break;
        		case 4:
                    saySubObjective ("\nNo redraw: On");
                    shell.setRedraw (false);
                    break;
        		default:
                    saySubObjective ("\nNo redraw: Off");
                    shell.setRedraw (true);
        			e.doit = true;
        	}
        }
    });
    
    final Color red = new Color (shell.getDisplay (), 255,0,0);
    shell.addPaintListener(new PaintListener () {
        public void paintControl(PaintEvent event) {
            GC gc = event.gc;
			Rectangle r = shell.getClientArea();

            switch (closes) {
				case 1 :
					drawGrid (gc, 0, 0, r.width, r.height, 10);
					break;
				case 2 :
                    say ("Clip before: " + gc.getClipping ().toString ());
                    gc.setClipping (200, 100, 41, 201);
                    say ("Clip after: " + gc.getClipping ().toString ());
					drawGrid (gc, 0, 0, r.width, r.height, 5);
					break;
				case 3 :
                    say ("Clip before: " + gc.getClipping ().toString ());
                    Region rg1 = new Region ();
                    rg1.add (new Rectangle (100, 100, 81, 81));
                    rg1.add (new Rectangle (160, 160, 81, 81));
                    Region rg2 = new Region ();
                    rg2.add (new Rectangle (300, 100, 81, 81));
                    rg2.add (new Rectangle (360, 160, 81, 81));
                    rg1.add (rg2);
                    gc.setClipping (rg1);
                    say ("Clip after: " + gc.getClipping ().toString ());
					drawGrid (gc, 0, 0, r.width, r.height, 5);
                    rg1.dispose ();
                    rg2.dispose ();
					break;
				default :
					drawGrid (gc, 0, 0, r.width, r.height, 20);
					break;
			}
        }
    });
    
    Rectangle dr = display.getBounds ();
    Rectangle sr = shell.getBounds ();
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
