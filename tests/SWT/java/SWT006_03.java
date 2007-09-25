/*
 *  SWT006_03.java
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
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;

/**
 *  This testcase tests GC.copyArea() functionality as well as some clipping
 *  operations (GC.getClipping(Region) ad GC.isClipped()).
 *
 *  At first, an image is loaded. Then it is broken into four equal rectangles
 *  and these rectangles are swapped diagonally. Then swapping is repeated, so
 *  finally we should get the original image.  
 *
 *  After pressing the "Start" button the image inside a dotted rectangle
 *  starts scrolling diagonally, from top left to bottom right.
 *
 *  If the "Debug" check box is checked during scrolling the output should be:
 
Debug ON
paint = 0
update = Rectangle {0, 0, 632, 450}
is clipped = false
clip = Rectangle {0, 0, 632, 450}
clip region = Rectangle {0, 0, 632, 450}
is clipped (after) = true
paint = 1
update = Rectangle {170, 80, 280, 10}
is clipped = false
clip = Rectangle {170, 80, 280, 280}
clip region = Rectangle {170, 80, 280, 280}
is clipped (after) = true
paint = 0
update = Rectangle {170, 90, 10, 270}
is clipped = true
clip = Rectangle {170, 80, 280, 10}
clip region = Rectangle {170, 80, 280, 10}
is clipped (after) = true
... (last 12 lines are repeated until termination or turning debug off)

 *  to ensure that the update region is set correctly inside the paint event
 *  (only parts of the area being scrolled that are left uncovered after
 *  placing the area into a new origin should be updated) and the clipping is
 *  also reported correctly.   
 *
 *  NOTE. This testcase, when scrolling, causes a trap (SYS3175) in JITC.DLL
 *  wheh running under IBM JRE 1.3.1 (at least, builds co131-20030618 and
 *  co131-20020227). Most likely this is a bug inside the IBM JIT compiler --
 *  the trap disappears when we disable JIT.
 */

public class SWT006_03 extends SWTTestCase {

static {
    STEP= "006";
    TEST = "03";
    DESC = "Images - Scrolling";
}

boolean started; 

Shell shell;

Button startStop;

Image background = null;

final static String imageDir = System.getProperty ("user.dir") +
    "/tests/SWT/images/"; 

public static void main (String [] args) {
    go (new SWT006_03 ());
}

boolean debug = false;

class ScrollData {
    int dx = 0;
    int dy = 0;
    int delta = 1;
    int delay = 10;
    boolean terminate;
}

ScrollData sData = new ScrollData();
int sWidth = 280;
int sHeight = 280;

Shell createTopShell (Display display) {

    shell = new Shell (display,
        SWT.DIALOG_TRIM | SWT.NO_REDRAW_RESIZE | SWT.NO_MERGE_PAINTS | SWT.NO_BACKGROUND);

    final Image img = new Image (display, imageDir + "img01.24.bmp");
    {
        Rectangle r = img.getBounds();
        GC gc = new GC (img);
        int w = r.width / 2;
        int h = r.height / 2;
        Image i = new Image (null, w, h);
        for (int j = 0; j < 2; j++) {
            gc.copyArea (i, w, h);
            gc.copyArea (0, 0, w, h, w, h);  
            gc.copyArea (w, 0, w, h, 0, 0);  
            gc.copyArea (0, h, w, h, w, 0);
            gc.copyArea (0, 0, w, h, 0, h);
            gc.drawImage (i, 0, 0);
        }
        i.dispose();
        gc.dispose();
    }

    final Image rotImg = new Image (display, sWidth, sHeight);
    
    final Display dspl = display;
	final Thread runner = new Thread () {
        public synchronized void run () {
			while (!sData.terminate) {
				synchronized (sData) {
                    dspl.asyncExec (new Runnable () {
                        public void run() {
                            if (!shell.isDisposed ()) {
					            Rectangle cr = shell.getClientArea();
					            int x0 = ((cr.width - sWidth) / 2) / 10 * 10;
					            int y0 = ((cr.height - sHeight) / 2) / 10 * 10;
                                GC gc = new GC (shell);
                                gc.setClipping (new Rectangle (x0, y0, sWidth, sHeight));
                                gc.copyArea (
                                	x0, y0, sWidth, sHeight,
                                	x0 + sData.delta, y0 + sData.delta);
                                gc.dispose();
                                sData.dx += sData.delta; sData.dy += sData.delta;
                                if (sData.dx >= sWidth) sData.dx = 0;
                                if (sData.dy >= sHeight) sData.dy = 0;
                            }
                        }
                    });
                }
                try {
                    Thread.sleep (sData.delay);
                } catch (InterruptedException e) {
                }
            }
			System.out.println ("runner: terminated.");
        }
    };
        
    shell.addDisposeListener (new DisposeListener () {
        public void widgetDisposed (DisposeEvent e) {
            synchronized (sData) {
                sData.terminate = true;
                sData.notifyAll ();
            }
            synchronized (runner) {}
            if (background != null) background.dispose();
            img.dispose();
            rotImg.dispose();
        }
    });

    shell.addPaintListener(new PaintListener () {
        public void paintControl(PaintEvent event) {
            GC gc = event.gc;

            Rectangle cr = shell.getClientArea();
            int x0 = ((cr.width - sWidth) / 2) / 10 * 10;
            int y0 = ((cr.height - sHeight) / 2) / 10 * 10;
            
            Rectangle r = new Rectangle (event.x, event.y, event.width, event.height);
            
            if (debug) {
                System.out.println ("paint = " + event.count);
                System.out.println ("update = " + r);
                System.out.println ("is clipped = " + gc.isClipped());
                System.out.println ("clip = " + gc.getClipping());
                Region rgn = new Region();
                gc.getClipping (rgn);
                System.out.println ("clip region = " + rgn.getBounds());
                rgn.dispose();
            }

            // narrow clipping area according to the current rectangle of the
            // complex update region to optimize image drawing (current clipping
            // is used inside GC.dtawImage() for optimizations).
            gc.setClipping (r);
            if (debug) System.out.println ("is clipped (after) = " + gc.isClipped());
              
            Rectangle interRect = new Rectangle (x0, y0, sWidth, sHeight);
            interRect = interRect.intersection (r);
            if (!started || !interRect.equals (r)) {
                drawCheckers (gc,
                    (r.x / 20) * 20, (r.y / 20) * 20,
                    r.width + 20, r.height + 20
                );
                gc.drawFocus (x0 - 1, y0 - 1, sWidth + 2, sHeight + 2);
            }
            
            if (started) {
                synchronized (sData) {
                    gc.drawImage (rotImg,
                        0, 0, sWidth - sData.dx, sHeight - sData.dy,
                        x0 + sData.dx, y0 + sData.dy, sWidth - sData.dx, sHeight - sData.dy
                    );
                    gc.drawImage (rotImg,
                        sWidth - sData.dx, sHeight - sData.dy, sData.dx, sData.dy,
                        x0, y0, sData.dx, sData.dy
                    );
                    gc.drawImage (rotImg,
                        sWidth - sData.dx, 0, sData.dx, sHeight - sData.dy,
                        x0, y0 + sData.dy, sData.dx, sHeight - sData.dy
                    );
                    gc.drawImage (rotImg,
                        0, sHeight - sData.dy, sWidth - sData.dx, sData.dy,
                        x0 + sData.dx, y0, sWidth - sData.dx, sData.dy
                    );
                }
            } else {
                Rectangle ir = img.getBounds();
                int x = ((cr.width - ir.width) / 2) / 10 * 10 - 5;
                int y = ((cr.height - ir.height) / 2) / 10 * 10 - 5;
                gc.drawImage (img, x, y);
                gc.copyArea (rotImg, x0, y0); 
            }
        }
    });
    
    startStop = new Button (shell, SWT.PUSH);
    startStop.setText ("Start");
    startStop.addSelectionListener (new SelectionAdapter () {
        public void widgetSelected (SelectionEvent e) {
            if (started) {
                shell.close();
            } else {
                started = true;
                startStop.setText ("Stop");
                runner.start();
            }
        }
    });
    
    Button checks = new Button (shell, SWT.CHECK);
    checks.setText ("Debug");
    checks.setSelection (debug);
    checks.addSelectionListener (new SelectionAdapter () {
        public void widgetSelected (SelectionEvent e) {
            synchronized (sData) {
                debug = ((Button)e.widget).getSelection ();
                if (debug) {
                    if (sData.dx % 10 != 0) {
                        sData.dx += 10 - (sData.dx % 10);
                        if (sData.dx >= sWidth) sData.dx = 0;
                    }
                    if (sData.dy % 10 != 0) {
                        sData.dy += 10 - (sData.dy % 10);
                        if (sData.dy >= sHeight) sData.dy = 0;
                    }
                    sData.delta = 10;
                    sData.delay = 1000;
                    System.out.println ("Debug ON");
                } else {
                    sData.delta = 1;
                    sData.delay = 10;
                    System.out.println ("Debug OFF");
                }
            }
            shell.redraw();
        }
    });
    
    FormData fdata = new FormData();
    fdata.right = new FormAttachment (100, -10);
    fdata.bottom = new FormAttachment (100, -10);
    startStop.setLayoutData (fdata);
    fdata = new FormData();
    fdata.left = new FormAttachment (0, 10);
    fdata.bottom = new FormAttachment (100, -10);
    checks.setLayoutData (fdata);
    FormLayout formLayout = new FormLayout ();
    shell.setLayout (formLayout);
    
    Rectangle dr = display.getClientArea ();
    Rectangle sr = new Rectangle (0, 0, 640, 480);
    sr.x = (dr.width-sr.width)/2;
    sr.y = (dr.height-sr.height)/2;
    shell.setBounds (sr);
    
    return shell;
}

void drawCheckers (GC gc, int x0, int y0, int width, int height) {
    if (background == null) {
        int w = 20, h = 20;
        Display display = Display.getDefault();
        background = new Image (display, w, h);
        GC bgc = new GC (background);
        Color c1 = new Color (display, 255, 255, 255);
        Color c2 = new Color (display, 192, 192, 192);
        bgc.setBackground (c1);
        bgc.fillRectangle(0, 0, 10, 10);
        bgc.fillRectangle(10, 10, 10, 10);
        bgc.setBackground (c2);
        bgc.fillRectangle(10, 0, 10, 10);
        bgc.fillRectangle(0, 10, 10, 10);
        c1.dispose();
        c2.dispose();
        bgc.dispose();
    }
    Rectangle r = background.getBounds();
    for (int y = 0; y <= height; y += r.height)
        for (int x = 0; x <= width; x += r.width)
            gc.drawImage (background, x0 + x, y0 + y);
}

}

