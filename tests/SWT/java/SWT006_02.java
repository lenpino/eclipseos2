/*
 *  SWT006_02.java
 */

/*
 * Copyright (c) 2002, 2004 EclipseOS2 Team.
 * This file is made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 */

import java.util.ArrayList;
import java.util.Iterator;
import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;

/**
 *  This example tests the rate of image blitting operations in
 * 	different modes as well as the overall speed of painting (including
 * 	the time to prepare the next frame and to process events).
 *
 *  Numbers shown in the title have the following meanings:
 *  B = rate of blitting operations (blits/second)
 *  O = overal rate of the output of one frame (blits/second)
 *  T = total time spent to blitting (milliseconds)
 *
 *  Note that these values are very relative and hardly depend on the current
 *  mashine configuration as well as on intervals defined at the beginning of
 *  the SWT006_02 class. This relativeness is caused by unpreciseness of the
 *  method used to measure time (System.currentTimeMillis()), but it is enough
 *  to compare different modes of blitting. 
 * 
 * 	Due to a very small amount of time needed to blit one image without scaling,
 *  transparency and alpha (less than 1 ms on today computers) an image is
 *  drawn 100 times per every frame to make calculations a little more precise.
 * 
 * 	Both test threads (UI and frame generator) are running on the
 * 	maximum priority (Thread.MAX_PRIORITY) for better results.
 *
 *  Note that when the display is in 256-color mode the moving wheel picture
 *  will be completely invisible due to palette-based mode limitations (its
 *  alpha is 4 out of 256 and the system matches its colors to colors of the
 *  background image).
 *  
 */

public class SWT006_02 extends SWTTestCase {

static {
    STEP= "006";
    TEST = "02";
    DESC = "Image output rate";
}

// time to sleep before drawing the next frame
final long sleepInterval = 5;
// how frequently to update the FPS data
final long updateInterval = 100;
// how many times an image is drawn into every frame
final int imagesPerFrame = 100;

//final long sleepInterval = 1000;
//final long updateInterval = 1000;
//final int imagesPerFrame = 1;
    
Display display;
Shell shell;

public static void main (String [] args) {
    go (new SWT006_02 ());
}

class FrameData {
	static final int x = 10;
	static final int y = 10;
	static final int w = 200;
	static final int h = 200;
	int sw = w, sh = h;
	
	boolean ready;
	boolean terminate;
	boolean reset = true;

	int mode = 0;
	static final int LAST_MODE = 5;
	
	String lastBlit = "{notready}";
	String lastOverall = "{notready}";
	String lastTime = "{notready}";
	String getResults () {
		return "[#"+mode+"] B=" + fd.lastBlit + " O=" + fd.lastOverall + " T=" + fd.lastTime;
	}
}

final FrameData fd = new FrameData ();

Shell createTopShell (Display display) {

    this.display = display;
    final Image frame = new Image (display, fd.w * 2, fd.h * 2);

    shell = new Shell (display,
        SWT.SHELL_TRIM | SWT.NO_REDRAW_RESIZE | SWT.NO_BACKGROUND);

	final Thread runner = new Thread () {

        final Display display = SWT006_02.this.display;
        boolean reset;

        public synchronized void run () {
			int x = 0, y = 0;
			boolean dir = true;
        	int maxXY = Math.min (fd.w, fd.h) - 40;

			Image back = new Image (display, fd.w * 2, fd.h * 2);
	    	Image fore = new Image (display, fd.w, fd.h);
			GC frameGc = new GC (frame);
			GC backGc = new GC (back);
			GC foreGc = new GC (fore);
        	
        	long startTime, lastUpdate, sleepTime, blitTime, frames;
			startTime = lastUpdate = sleepTime = blitTime = frames = 0;
        	long time;
			
			{
				Color clr = new Color (display, 255, 0, 0);
				backGc.setForeground (clr);
				Rectangle r = back.getBounds();
				drawGrid (backGc, 0, 0, r.width, r.height, 10);
				clr.dispose();
			}

			while (!fd.terminate) {
				synchronized (fd) {
                    try {
                        while (fd.ready) {
                            fd.wait();
                        }
                    } catch (InterruptedException e) {
                        break;
					}
                    reset = fd.reset;
					if (fd.reset) {
						x = 0; y = 0;
						dir = true;
						sleepTime = blitTime = frames = 0;
						startTime = lastUpdate = System.currentTimeMillis();
						switch (fd.mode) {
							case 0 :
								fd.sw = fd.w;
								fd.sh = fd.h;
								break;
							case 1 :
								fd.sw = fd.w * 2;
								break;
							case 2 : {
								ImageData i = fore.getImageData();
								ImageData id = new ImageData (fd.w, fd.h, i.depth, i.palette);
								id.transparentPixel = id.palette.getPixel(new RGB (255, 255, 255));
								id.alpha = -1;
								foreGc.dispose();
								fore.dispose();
								fore = new Image (display, id);
								foreGc = new GC (fore);
								fd.sw = fd.w;
								fd.sh = fd.h;
                                break;
                            }
							case 3 :
								fd.sw = fd.w * 2;
								break;
							case 4 : {
								ImageData i = fore.getImageData();
								ImageData id = new ImageData (fd.w, fd.h, i.depth, i.palette);
								id.transparentPixel = -1;
								id.alpha = 4;
								foreGc.dispose();
								fore.dispose();
								fore = new Image (display, id);
								foreGc = new GC (fore);
								fd.sw = fd.w;
								fd.sh = fd.h;
                                break;
                            }
							case 5 :
								fd.sw = fd.w * 2;
								break;
						}
						fd.reset = false;
					} else {
	                    frames += imagesPerFrame;
	                    time = System.currentTimeMillis();
					    if (time - lastUpdate >= updateInterval) {
					    	final String info1 =
								div1000 (frames * 1000,
									time - startTime - sleepTime);
					    	final String info2 =
								div1000 (frames * 1000, blitTime);
					    	final String info3 =
								div1000 (blitTime, 1000);
							lastUpdate = time;	
							display.asyncExec( new Runnable () {
								public void run() {
		                            if (!shell.isDisposed ())
										SWT006_02.this.updateTitle (info1, info2, info3);
								}
							});
						}
						if (sleepInterval > 0) {
							time = System.currentTimeMillis();
							try {
								fd.wait (sleepInterval);
							} catch (InterruptedException e) {
		                        break;
							}
							sleepTime += System.currentTimeMillis() - time;
						}
					}
				}

			    foreGc.fillRectangle (fore.getBounds());
			    Color clr = foreGc.getBackground();
			    foreGc.setBackground (foreGc.getForeground());
			    foreGc.fillOval (x, y, 40, 40);
			    foreGc.setBackground (clr);
			    foreGc.fillOval (x + 10, y + 10, 20, 20);
			    
			    frameGc.drawImage (back, 0, 0);
			    
				time = System.currentTimeMillis();
                for (int i = 0; i < imagesPerFrame; i++) {
                	frameGc.drawImage (fore, 0, 0, fd.w, fd.h,
                    	0, 0, fd.sw, fd.sh);
                }
				blitTime += System.currentTimeMillis() - time;
			    
			    if (dir) {
			    	x ++; y ++;
			    	if (x == maxXY) dir = false;
			    } else {
			    	x --; y --;
			    	if (x == 0) dir = true;
			    }

				synchronized (fd) {
					if (!fd.reset) {
	                    fd.ready = true;
						display.asyncExec( new Runnable () {
							public void run() {
	                            if (!shell.isDisposed ()) {
                                    if (reset) {
                                        shell.redraw();
                                    } else {
                                        shell.redraw (
                                            fd.x, fd.y,
                                            fd.sw, fd.sh, false);
                                    }
	                            }
							}
						});
					}
				}			
			}
			foreGc.dispose();
			backGc.dispose();
			frameGc.dispose();
			fore.dispose();
			back.dispose();
			System.out.println ("runner: terminated.");
		}
	};

    shell.addDisposeListener (new DisposeListener () {
        public void widgetDisposed (DisposeEvent e) {
            synchronized (fd) {
	            System.out.println (fd.getResults());
                fd.ready = false;
                fd.terminate = true;
                fd.notifyAll ();
            }
            synchronized (runner) {;}
            frame.dispose();
        }
    });
    
    shell.addPaintListener(new PaintListener () {

        public void paintControl(PaintEvent event) {
        	
            GC gc = event.gc;
            Rectangle r = new Rectangle (fd.x, fd.y, fd.sw, fd.sh);

            if (!gc.getClipping ().equals (r)) {
                r = shell.getClientArea();
                int frameX2 = fd.sw + fd.x;
                int frameY2 = fd.sh + fd.y;
                gc.fillRectangle (0, 0, frameX2, fd.y);
                gc.fillRectangle (0, 0, fd.x, frameY2);
                if (r.width > frameX2)
                    gc.fillRectangle(frameX2, 0, r.width, r.height);
                if (r.height > frameY2)
                    gc.fillRectangle(0, frameY2, frameX2, r.height);
                gc.drawRectangle (fd.x - 1, fd.y - 1, fd.sw + 1, fd.sh + 1);
            }
            
        	synchronized (fd) {
        		if (fd.ready) {
            		gc.drawImage (frame, 0, 0, fd.sw, fd.sh,
            			fd.x, fd.y, fd.sw, fd.sh);
            		fd.ready = false;
            		fd.notify();
                }
        	}
        }
    });

    final Button next = new Button (shell, SWT.PUSH);
    next.setText ("Next >");
    next.addSelectionListener (new SelectionAdapter () {
        public void widgetSelected (SelectionEvent e) {
			if (fd.mode == fd.LAST_MODE)
				shell.close();
			else {
				synchronized (fd) {
	                if (fd.mode != fd.LAST_MODE)
		            	System.out.println (fd.getResults());
					fd.mode++;
	                if (fd.mode == fd.LAST_MODE)
	                    next.setText ("Finish");
					fd.ready = false;
					fd.reset = true;
                    fd.notify();
	            }
			}
        }
    });
    
    FormData fdata = new FormData();
    fdata.right = new FormAttachment (100, -10);
    fdata.bottom = new FormAttachment (100, -10);
    next.setLayoutData (fdata);
    FormLayout formLayout = new FormLayout ();
    shell.setLayout (formLayout);
    
    Rectangle dr = display.getBounds ();
    Rectangle sr = new Rectangle (0, 0, 640, 300);
    sr.x = (dr.width-sr.width)/2;
    sr.y = (dr.height-sr.height)/2;
    shell.setBounds (sr);

	Thread.currentThread ().setPriority (Thread.MAX_PRIORITY);
    runner.setPriority (Thread.MAX_PRIORITY);
    runner.start();

    return shell;
}

static void drawGrid (GC gc, int x0, int y0, int width, int height, int step)
{
    int i;
        for (i = 0; i <= width/step; i++)
                gc.drawLine (x0+i*step, y0, x0+i*step, y0+height);
        for (i = 0; i <= height/step; i++)
                gc.drawLine (x0, y0+i*step, x0+width, y0+i*step);
}

static String div1000 (long a, long b) {
	if (b <= 0) return "{infinite}";
    String rc =
    	"000000" + (a / b) + "." + (((a % b) * 1000) / b) + "000";
    int h = rc.indexOf ('.') - 6;
    return rc.substring (h, h + 10);
}

String title;

void updateTitle (String overall, String blit, String time) {
	if (title == null) {
		title = shell.getText();
	}
	if (overall != null) fd.lastOverall = overall;
	if (blit != null) fd.lastBlit = blit;
	if (time != null) fd.lastTime = time;
	shell.setText (title + " " + fd.getResults());
}

}
