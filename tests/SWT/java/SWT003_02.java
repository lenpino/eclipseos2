/*
 *  SWT003_02.java
 */

/*
 * Copyright (c) 2002, 2004 EclipseOS2 Team.
 * This file is made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 */

import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;

/**
 *  This exaple tests multicolor capabilities.
 *  It draws three gradient strips (from black to each of the base colors: red,
 *  green and blue) and the outlined rectangle using the control's foreground
 *  color.
 *
 *  The first attempt to close the window changes control's background and
 *  foreground colors to see the effect.
 *
 *  The examle also checks the GC color setter/getter methods.
 */

public class SWT003_02 extends SWTTestCase {

static {
    STEP= "003";
    TEST = "02";
    DESC = "Colors";
}
    
public static void main (String [] args) {
    go (new SWT003_02 ());
}

Shell createTopShell (Display display) {
    
    final Shell shell = new Shell (display,
        SWT.SHELL_TRIM | SWT.NO_REDRAW_RESIZE );

    final HashMap palette = new HashMap();
    
    final Color bg = new Color (display, 254, 254, 180);
    final Color fg = new Color (display, 255, 100, 100);
    palette.put (bg.getRGB (), bg);
    palette.put (fg.getRGB (), fg);

    shell.addDisposeListener (new DisposeListener () {
        public void widgetDisposed (DisposeEvent e)
        {
            Iterator i = palette.values ().iterator ();
            while (i.hasNext ())
                ((Color)i.next ()).dispose();
        }
    });

    shell.addShellListener (new ShellAdapter () {
	    int closes = 0;
        public void shellClosed (ShellEvent e) {
        	closes++;
        	e.doit = false;
        	switch (closes) {
        		case 1:
                    saySubObjective ("\nChanging shell's background and foreground");
                    say ("Foreground trying to be set: " + fg);
                    say ("Background trying to be set: " + bg);
		            shell.setBackground (bg);
		            shell.setForeground (fg);
		            break;
        		default: e.doit = true;
        	}
        }
    });
    
    shell.addPaintListener(new PaintListener () {
        public void paintControl(PaintEvent event){
            GC gc = event.gc;
            say ("GC foreground actually set: " + gc.getForeground ());
            say ("GC background actually set: " + gc.getBackground ());
            gc.drawRectangle (10, 100, 255, 20);
            drawGradientStrip (gc, 10, 70, 20, 2, palette);
            drawGradientStrip (gc, 10, 10, 20, 1, palette);
            drawGradientStrip (gc, 10, 40, 20, 0, palette);
        }
    });

    System.out.println("Shell default foreground: "+shell.getForeground ());
    System.out.println("Shell default background: "+shell.getBackground ());
    
    Rectangle dr = display.getClientArea ();
    Rectangle sr = new Rectangle (0, 0, 300, 200); //shell.getBounds ();
    sr.x = dr.x + (dr.width-sr.width)/2;
    sr.y = dr.y + (dr.height-sr.height)/2;
    shell.setBounds (sr);
    return shell;
}

void drawGradientStrip (GC gc, int x0, int y0, int height, int mode, HashMap palette)
{
    Color color;
    RGB rgb;
    
    for (int i = 0; i < 256; i++) {
        switch (mode) {
            case 0: rgb = new RGB (i, 0, 0); break;
            case 1: rgb = new RGB (0, i, 0); break;
            default: rgb = new RGB (0, 0, i); break;
        }
        color = (Color)palette.get (rgb);
        if (color == null) {
            color = new Color (null, rgb);
            palette.put (rgb, color);
        }
        gc.setForeground (color);
        gc.drawRectangle (x0 + i, y0, 0, height);
    }
}

}
