/*
*  SWT008_02.java
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
*  A simple testcase that allows to call the font dialog changing the font of the text displaying 
 * the fonts attribute.
*/

public class SWT008_02 extends SWTTestCase {
	private Font font;
        boolean drawCheckers = false;
        Class fmHndCls;
        String platform;
        private FontData newFont;
        Canvas canvas;        
        Image background = null;
        
	static {
		STEP = "008";
		TEST = "02";
		DESC = "Font Dialog";
	}
	
	public static void main (String [] args) {
		go (new SWT008_02 ());
	}
	
	Shell createTopShell (Display display) {
		return new Shell (display,SWT.SHELL_TRIM | SWT.NO_REDRAW_RESIZE);
	}
	
	void initComponents () {
		super.initComponents();
                //Initiates the font with SystemFont
                newFont = ((Font)(Display.getDefault().getSystemFont())).getFontData()[0];
                canvas = new Canvas (shell, 0);
                canvas.addPaintListener(new PaintListener () {
                    public void paintControl(PaintEvent event) {
                        GC gc = event.gc;

                        Rectangle r = new Rectangle (event.x, event.y, event.width, event.height);
                        gc.setClipping (r);

                        drawGrid (gc,
                            (r.x / 30) * 20, (r.y / 30) * 20,
                            r.width + 30,  r.height + 30, 10);

                        int y = 0;
                        y = drawFont (gc, newFont, 0, 0);
                    }
                });
                
                Composite bottom = new Composite (shell, 0);    
                Composite group1 = new Composite (bottom, SWT.NO_BACKGROUND);
                group1.setLayout (new FillLayout (SWT.HORIZONTAL));

		shell.setLayout(new GridLayout(2, false));
		Button button = new Button(group1, SWT.PUSH);
		button.setText("Font...");
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				FontDialog dlg = new FontDialog(shell);
                                FontData ret = null;
				FontData defaultFont = ((Font)(Display.getDefault().getSystemFont())).getFontData()[0];
				dlg.setFontData(defaultFont);
				ret = dlg.open();
				if (ret == null){
                                    return;
                                }
				else{
                                    newFont = ret;
                                    System.out.println("Height = " + newFont.getHeight());
                                    System.out.println("Name = " + newFont.getName());
                                    System.out.println("Style = " + newFont.getStyle());
                                    canvas.redraw();
                                }
			}
		});

                FormData fdata = new FormData();
                fdata.right = new FormAttachment (100, 0);
                fdata.bottom = new FormAttachment (100, 0);
                group1.setLayoutData (fdata);
                FormLayout formLayout = new FormLayout ();
                bottom.setLayout (formLayout);

                GridData gdata = new GridData (GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL | GridData.GRAB_VERTICAL);
                canvas.setLayoutData (gdata);
                gdata = new GridData (GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL | GridData.VERTICAL_ALIGN_CENTER);
                bottom.setLayoutData (gdata);
    
                GridLayout gridLayout = new GridLayout (1, false);
                // Win32 workaround: this forces canvas bounds to be properly
                // calculated for the maximized state, since windows in Windows
                // cannot be maximized when not visible, in that sense that their
                // real size won't change until the window is actually shown.

                Rectangle dr = display.getBounds ();
		Rectangle sr = new Rectangle (0, 0, 640, 100);
		sr.x = (dr.width-sr.width)/2;
		sr.y = (dr.height-sr.height)/2;
		shell.setBounds (sr);

	}
    void drawGrid (GC gc, int x0, int y0, int width, int height, int step) {
        if (drawCheckers) {
            if (background == null) {
                int w = 100, h = 100;
                Display display = Display.getDefault();
                background = new Image (display, w, h);
                GC bgc = new GC (background);
                bgc.setBackground (display.getSystemColor (SWT.COLOR_WHITE));
                bgc.fillRectangle(0, 0, 100, 100);
                bgc.fillRectangle(100, 100, 100, 100);
                bgc.setBackground (display.getSystemColor (SWT.COLOR_GRAY));
                bgc.fillRectangle(100, 0, 100, 100);
                bgc.fillRectangle(0, 100, 100, 100);
                bgc.dispose();
            }
            Rectangle r = background.getBounds();
            for (int y = 0; y <= height; y += r.height)
                for (int x = 0; x <= width; x += r.width)
                    gc.drawImage (background, x0 + x, y0 + y);
        } else {
            gc.fillRectangle (gc.getClipping());
        }
    }
    int drawFont (GC gc, FontData fd, int x0, int y0) {
//        Font f = fonts [idx];
//        FontData fd = f.getFontData() [0];
        String str = " " + fd.getHeight() + ".";
        String name;
        name = fd.getName(); 
        str += name.length() == 0 ? "<uninitialized>" : name;
        int style = fd.getStyle();
        if (style != SWT.NORMAL) {
            str += " [";
            if ((style & SWT.BOLD) != 0) str += "B";
            if ((style & SWT.ITALIC) != 0) str += "I";
            str += "]";
        }

        gc.setForeground (display.getSystemColor (SWT.COLOR_BLACK));
        gc.setBackground (display.getSystemColor (SWT.COLOR_CYAN));
        Font f = new Font(display, fd);
        gc.setFont (f);
        FontMetrics fm = gc.getFontMetrics();
        Point box;

        str += " " + getAdditionalFontInfo (fd, fm);
        gc.drawString (str, x0, y0, true);
        box = gc.stringExtent (str);

        return fm.getHeight();
    }
    
    String getAdditionalFontInfo (FontData fd, FontMetrics fm) {
        if (fmHndCls == null) {
            fmHndCls = fm.handle.getClass();
            platform = SWT.getPlatform();
        }

        int lMatch = 0;
        int actualHeight = 0;
        String faceName = "<not_available>";

        Point dpi = display.getDPI();
        actualHeight =
            Math.round ((float)((fm.getAscent() + fm.getDescent()) * 72) / dpi.y); 

        if (platform.equals ("pm")) {
            short fsDefn;
            short sNominalPointSize;
            byte[] szFacename = null;
            try {
                lMatch = fmHndCls.getField ("lMatch").getInt (fm.handle);
                fsDefn = fmHndCls.getField ("fsDefn").getShort (fm.handle);
                sNominalPointSize = fmHndCls.getField ("sNominalPointSize").getShort (fm.handle);
                szFacename = (byte[])fmHndCls.getField ("szFacename").get (fm.handle);

                if ((fsDefn & 0x0001 /* OS.FM_DEFN_OUTLINE */) == 0) {
                    actualHeight = sNominalPointSize / 10;
                }
                int i = 0;
                while (szFacename [i] != 0) i++;
                faceName = new String (szFacename, 0, i);
            } catch (NoSuchFieldException x) {
            } catch (IllegalAccessException x) {
            }
        }

        String str = "[" +
            actualHeight + "." +
            faceName + ";" +
            fm.getHeight() + "=" +
            fm.getLeading() + "+" + fm.getAscent() + "+" + fm.getDescent() + ";" +
            fm.getAverageCharWidth() + ";" + 
            lMatch +
            "] ";

        return str;
    }
	
}

