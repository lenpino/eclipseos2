/*
 *  SWT003_01.java
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
 *  This example tests the basic drawing capabilities.
 *  It draws different graphic primitives in different modes (outlined/filled,
 *  positive/negative dimensions and so on) on a grid with cells of 20
 *  pixels in size.
 *
 *  When the window is being closed for the first time everything is redrawn
 *  with the thickness set to 3 points.
 *
 *  When the window is being closed for the second time everything is redrawn in
 *  the XOR mode.
 */

public class SWT003_01 extends SWTTestCase {

static{
    STEP= "003";
    TEST = "01";
    DESC = "Simple Drawings";
}
    
int closes = 0;

public static void main (String [] args) {	
    go (new SWT003_01 ());
}

Shell createTopShell (Display display) {
    
    final Color red = new Color (display, 0xFF, 0, 0);    
    final Color blue = new Color (display, 0, 0, 0xFF);    
    final Color black = new Color (display, 0, 0, 0);    
    final Color grid = new Color (display, 0xA0, 0xA0, 0xA0);    
    
    final Shell shell = new Shell (display,
        SWT.SHELL_TRIM | /*SWT.NO_REDRAW_RESIZE |*/ SWT.NO_MERGE_PAINTS);
    
    shell.addDisposeListener (new DisposeListener () {
        public void widgetDisposed (DisposeEvent e) {
            grid.dispose();
            black.dispose();
            blue.dispose();
            red.dispose();
        }
    });

    shell.addShellListener (new ShellAdapter () {
        public void shellClosed (ShellEvent e) {
        	closes++;
        	e.doit = false;
        	switch (closes) {
        		case 1:
                    saySubObjective ("\nSetting thick line width for all primitives");
                    shell.redraw ();
		            break;
        		case 2:
                    saySubObjective ("\nSetting XOR mode for all primitives");
                    shell.redraw ();
		            break;
        		default: e.doit = true;
        	}
        }
    });
    
    shell.addPaintListener(new PaintListener () {
        public void paintControl(PaintEvent event) {
            GC gc = event.gc;
            
            if (closes == 1)
                gc.setLineWidth (3);
            else if (closes == 2)
                gc.setXORMode (true);
            
            say ("Line style: " + gc.getLineStyle ());
            say ("Line width: " + gc.getLineWidth ());
            say ("XOR mode: " + gc.getXORMode ());
                
            say ("PaintEvent: count = " + event.count);
            say (
                " [x = "+event.x +
                ", y = "+event.y +
                ", width = "+event.width +
                ", height = "+event.height + "]");

			Rectangle r = shell.getClientArea();
			
			gc.setForeground(grid);
			drawGrid (gc, 10, 10, r.width-20, r.height-20, 20);

			gc.setForeground(blue);

            gc.drawLine (0,0,10,10);
            r.width--; r.height--;
            gc.drawRectangle (r);
            
            gc.setBackground (red);
            drawCheckBoard (gc, 10, 10, false);
            drawCheckBoard (gc, 230, 10, true);

            drawStairs (gc, 10, 230, 0);
            drawStairs (gc, 10, 250, DS_FILLED);
            drawStairs (gc, 230, 230, DS_ROUNDED);
            drawStairs (gc, 230, 250, DS_FILLED | DS_ROUNDED);

            drawStairs (gc, 10, 230, DS_NEGATIVE);
            drawStairs (gc, 10, 250, DS_FILLED | DS_NEGATIVE);
            drawStairs (gc, 230, 230, DS_ROUNDED | DS_NEGATIVE);
            drawStairs (gc, 230, 250, DS_FILLED | DS_ROUNDED | DS_NEGATIVE);
            
			drawArcs (gc, 450, 10);
            
            gc.drawRoundRectangle (610, 10, 39, 39,  20, 40);
            gc.fillRoundRectangle (650, 10, 39, 39,  20, 40);
            gc.fillRoundRectangle (610, 50, 39, 39,  40, 20);
            gc.drawRoundRectangle (650, 50, 39, 39,  40, 20);

            for (int i = 0; i < 6; i++) {
                gc.drawArc (610, 110, 39, 39, 20+i*60, 40);
                gc.drawArc (650, 110, 40, 40, 20+i*60, 40);
                gc.fillArc (610, 150, 39, 39, 20+i*60, 40);
                gc.fillArc (650, 150, 40, 40, 20+i*60, 40);
            }
            
            int[] points = new int [] {
                10, 310, 110, 390, 70, 270, 30, 390, 130, 310
            };
            gc.drawPolyline (points);
            for (int i = 0; i < points.length; i += 2) points[i] += 140;
            gc.drawPolygon (points);
            for (int i = 0; i < points.length; i += 2) points[i] += 140;
            gc.fillPolygon (points);
            
			for (int i = 0; i < 3; i++) {
                switch (i) {
                    case 1: gc.setLineWidth (10); break;
                    case 2: gc.setLineWidth (0); break;
                    default:;
                }
	            gc.setLineStyle (SWT.LINE_SOLID);
	            drawZigzag (gc, 450 + i*120, 290);
	            gc.setLineStyle (SWT.LINE_DASH);
	            drawZigzag (gc, 450 + i*120, 310);
	            gc.setLineStyle (SWT.LINE_DOT);
	            drawZigzag (gc, 450 + i*120, 330);
	            gc.setLineStyle (SWT.LINE_DASHDOT);
	            drawZigzag (gc, 450 + i*120, 350);
	            gc.setLineStyle (SWT.LINE_DASHDOTDOT);
	            drawZigzag (gc, 450 + i*120, 370);
			}

			gc.setForeground(black);
            gc.setBackground (shell.getBackground());
            drawStairs (gc, 10, 410, DS_FOCUS);
            drawStairs (gc, 10, 410, DS_FOCUS | DS_NEGATIVE);

            gc.setBackground (red);
            gc.fillRectangle (230, 410, 20, 20);
            gc.drawFocus (235, 415, 10, 10);

            gc.setBackground (black);
            gc.setForeground (red);
            gc.fillRectangle (250, 410, 20, 20);
            gc.drawFocus (255, 415, 10, 10);

            gc.drawFocus (275, 415, 10, 10);
            
            // the following should finally not affect the picture
            drawStairs (gc, 10, 10, DS_FOCUS);
            drawStairs (gc, 10, 10, DS_FOCUS);
        }
    });
    
    Rectangle dr = display.getBounds ();
    Rectangle sr = shell.getBounds ();
    
    sr.x = (dr.width-sr.width)/2;
    sr.y = (dr.height-sr.height)/2;
    
    shell.setBounds (sr);
    
    System.out.println("bounds:");
    System.out.println("    shell.getBounds() = " + shell.getBounds ());
    System.out.println("    shell.getClientArea() = " + shell.getClientArea ());
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

static final int DS_NEGATIVE =  0x01;
static final int DS_FOCUS =     0x02;
static final int DS_FILLED =    0x04;
static final int DS_ROUNDED =   0x08;

void drawStairs (GC gc, int x0, int y0, int style)
{
    int sz = 20;
    int n;
    for (int i = 0; i < 10; i++)    {
    	if ((style & DS_NEGATIVE) != 0) n = -i;
    	else n = i;
        if ((style & DS_FOCUS) != 0) {
            gc.drawFocus (x0+i*sz, y0, n, n);
        } else {
            if ((style & DS_ROUNDED) != 0) {
                if ((style & DS_FILLED) != 0)
                    gc.fillRoundRectangle (x0+i*sz, y0, n, n, i/2, i/2);
                else
                    gc.drawRoundRectangle (x0+i*sz, y0, n, n, i/2, i/2);
            } else {
                if ((style & DS_FILLED) != 0)
                    gc.fillRectangle (x0+i*sz, y0, n, n);
                else
                    gc.drawRectangle (x0+i*sz, y0, n, n);
            }
        }
    }
}

void drawCheckBoard (GC gc, int x0, int y0, boolean rounded)
{
    int sz = 19, nx = 10, ny = 10, szrnd = 10;
    int xs, ys;
    boolean fill;
    
    for (int y = 0; y < ny; y++)
        for (int x = 0; x < nx; x++) {
            xs = x0 + x * (sz+1);
            ys = y0 + y * (sz+1);
            fill = ((y % 2) == 0 && (x % 2) != 0) || ((y % 2) != 0 && (x % 2) == 0);
            if (rounded) {
                if (fill) gc.fillRoundRectangle (xs, ys, sz, sz, szrnd, szrnd);
                else gc.drawRoundRectangle (xs, ys, sz, sz, szrnd, szrnd);
            }
            else {
                if (fill) gc.fillRectangle (xs, ys, sz, sz);
                else gc.drawRectangle (xs, ys, sz, sz);
            }
        }
}

void drawArcs (GC gc, int x0, int y0)
{
    int sz = 20;
    
    gc.drawArc (x0+sz, y0, sz*2, sz*2, 0, 90);
    gc.drawArc (x0, y0, sz*2, sz*2, 90, 90);
    gc.drawArc (x0, y0+sz, sz*2, sz*2, 180, 90);
    gc.drawArc (x0+sz, y0+sz, sz*2, sz*2, 270, 90);
    gc.fillArc (x0+sz*5, y0, sz*2, sz*2, 90, -90);
    gc.fillArc (x0+sz*4, y0, sz*2, sz*2, 180, -90);
    gc.fillArc (x0+sz*4, y0+sz, sz*2, sz*2, 270, -90);
    gc.fillArc (x0+sz*5, y0+sz, sz*2, sz*2, 360, -90);

    gc.drawArc (x0+sz, y0+sz*4, sz*2, sz*4, 0, 90);
    gc.drawArc (x0, y0+sz*4, sz*2, sz*4, 90, 90);
    gc.drawArc (x0, y0+sz*5, sz*2, sz*4, 180, 90);
    gc.drawArc (x0+sz, y0+sz*5, sz*2, sz*4, 270, 90);
    gc.fillArc (x0+sz*5, y0+sz*4, sz*2, sz*4, 90, -90);
    gc.fillArc (x0+sz*4, y0+sz*4, sz*2, sz*4, 180, -90);
    gc.fillArc (x0+sz*4, y0+sz*5, sz*2, sz*4, 270, -90);
    gc.fillArc (x0+sz*5, y0+sz*5, sz*2, sz*4, 360, -90);
    
    for (int i = 0; i < 9; i++) {
    	gc.drawArc (x0+i*sz, y0+sz*11, i+1, i+1, 0, 370);
    	gc.drawArc (x0+i*sz, y0+sz*11, -(i+1), -(i+1), 0, 370);
    	gc.fillArc (x0+i*sz, y0+sz*12, i+1, i+1, 0, 370);
    	gc.fillArc (x0+i*sz, y0+sz*12, -(i+1), -(i+1), 0, 370);
    }
}

void drawZigzag (GC gc, int x, int y) {
    int[] points = {0, 0, 20, 10, 40, 0, 60, 10, 80, 0, 100, 10};
    for (int i = 0; i < points.length; i += 2) {
        points[i] += x;
        points[i+1] += y;
    }
    gc.drawPolyline (points);
}

}
