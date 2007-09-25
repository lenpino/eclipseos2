/*
 *  SWT005_02.java
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

import java.util.ArrayList;
import java.util.Iterator;

/**
 *  This testcase demonstrates all fonts installed in the system. The
 *  testcase uses a number of pages to be able to draw all existing fonts
 *  without scrolling (which is not yet ready).
 *
 *  For every font, its height, family name, style and additional internal
 *  info are drawn as described in the comments to the SWT005_01 testcase. 
 *
 *  Buttons at the bottom of the screen are used to change the behavior of
 *  the list:
 *  -   Checkers -- when checked the checkboard is drawn;
 *  -   Metrics -- when checked font metrics are visualized;
 *  -   Scalable -- when checked scalable fonts are listed, otherwise
 *      bitmap fonts are listed;
 *  -   Arial only -- when checked only fonts from the Arial family are drawn
 *  -   National -- when checked three sequences of national characters are
 *      drawn: cyrillic, latin and japanese to test the locale support:
 *      * for cyrillic locale chars 0xA0 - 0xA7 of IBM-866 codepage are drawn;
 *      * for latin locale chars 0xD0 - 0xD7 of IBM-850 codepage are drawn;
 *      * for japanese locale chars 0xB0 - 0xB7 of IBM-942 codepage are drawn.
 *
 *  Note that currently there is no locale support in OS/2 version of SWT due
 *  to some system limitations and bugs. It will be added later when all
 *  problems are deeply explored. So at this time question marks are drawn
 *  instead of national characters for all three locales unless one of them
 *  is the default locale of the system the testcase runs on, in which case
 *  characters for that locale should be drawn correctly (according to
 *  codepages mentioned above).
 *
 *  Also note that output of to many different TrueType fonts is quite
 *  slow -- again, this is the limitation of the standard OS/2 TrueType
 *  font rasterizer. (It works a bit better with freetype.dll, an alternative
 *  TTF rasterizer; however, the latter has its own bugs). 
 * 
 */

public class SWT005_02 extends SWTTestCase {

static {
    STEP= "005";
    TEST = "02";
    DESC = "Font List";
}

public static void main (String [] args) {
    go (new SWT005_02 ());
}

int page = 1;
int lastPage = 1;

Button next, prev;

boolean drawCheckers = false;
boolean drawMetrics = false;
boolean drawScalable = true;
boolean drawArialOnly = false;
boolean drawNational = false;

Image background = null;

Canvas canvas;

Font[] fonts = new Font [0];
Font[] fonts_ru_RU = new Font [0];
Font[] fonts_en_GB = new Font [0];
Font[] fonts_ja_JP = new Font [0];
int[] fontHeights;
Integer[] fontPages = new Integer[] {new Integer (0)};

Class fmHndCls;
String platform;

final String strCYR = "[CYR \u0430\u0431\u0432\u0433\u0434\u0435\u0436\u0437]";
final String strLAT = "[LAT \u00F0\u00D0\u00CA\u00CB\u00C8\u20AC\u00CD\u00CE]"; 
final String strJPN = "[JPN \uFF70\uFF71\uFF72\uFF73\uFF74\uFF75\uFF76\uFF77]";
            
Shell createTopShell (Display display) {
    Shell sh = new Shell (display,
        SWT.DIALOG_TRIM | SWT.NO_REDRAW_RESIZE);
    return sh;
}

void initComponents () {
    shell.addDisposeListener (new DisposeListener () {
        public void widgetDisposed (DisposeEvent e) {
        	destroyFonts (fonts);
        	destroyFonts (fonts_ja_JP);
            if (background != null) background.dispose();
        }
    });
    
    canvas = new Canvas (shell, 0);
    canvas.addPaintListener(new PaintListener () {
        public void paintControl(PaintEvent event) {
            GC gc = event.gc;

            Rectangle r = new Rectangle (event.x, event.y, event.width, event.height);
            gc.setClipping (r);

            drawGrid (gc,
                (r.x / 20) * 20, (r.y / 20) * 20,
                r.width + 20,  r.height + 20, 10);
                
            int y = 0;
            for (
            	int fi = fontPages [page - 1].intValue();
            	fi < (page < lastPage ? fontPages [page].intValue() : fonts.length);
            	fi++
            ) {
            	y += drawFont (gc, fi, 0, y) + 2;
            }
        }
    });
    
    Composite bottom = new Composite (shell, 0);
    
    Composite group1 = new Composite (bottom, SWT.NO_BACKGROUND);
    group1.setLayout (new FillLayout (SWT.HORIZONTAL));

    prev = new Button (group1, SWT.PUSH);
    prev.setText ("< Previous");
    prev.addSelectionListener (new SelectionAdapter () {
        public void widgetSelected (SelectionEvent e) {
            if (page > 1) {
                page --;
                updateTitle();
            }
            if (page == 1) prev.setEnabled (false);
            if (page == lastPage - 1) next.setEnabled (true);
        }
    });
    next = new Button (group1, SWT.PUSH);
    next.setText ("Next >");
    next.addSelectionListener (new SelectionAdapter () {
        public void widgetSelected (SelectionEvent e) {
            if (page < lastPage) {
                page ++;
                updateTitle();
            }
            if (page == lastPage) next.setEnabled (false);
            if (page == 2) prev.setEnabled (true);
        }
    });
    updateTitle();

    Composite group2 = new Composite (bottom, SWT.NO_BACKGROUND);
    group2.setLayout (new FillLayout (SWT.HORIZONTAL));
    
    Button checks = new Button (group2, SWT.CHECK);
    checks.setText ("Checkers");
    checks.setSelection (drawCheckers);
    checks.addSelectionListener (new SelectionAdapter() {
        public void widgetSelected (SelectionEvent e) {
            drawCheckers = ((Button)e.widget).getSelection();
            canvas.redraw();
        }
    });
    Button metrics = new Button (group2, SWT.CHECK);
    metrics.setText ("Metrics");
    metrics.setSelection (drawMetrics);
    metrics.addSelectionListener (new SelectionAdapter() {
        public void widgetSelected (SelectionEvent e) {
            drawMetrics = ((Button)e.widget).getSelection();
            canvas.redraw();
        }
    });
    Button scalable = new Button (group2, SWT.CHECK);
    scalable.setText ("Scalable");
    scalable.setSelection (drawScalable);
    scalable.addSelectionListener (new SelectionAdapter() {
        public void widgetSelected (SelectionEvent e) {
            drawScalable = ((Button)e.widget).getSelection();
            createFontLists (drawScalable, drawArialOnly);
        }
    });
    Button arialOnly = new Button (group2, SWT.CHECK);
    arialOnly.setText ("Arial Only");
    arialOnly.setSelection (drawArialOnly);
    arialOnly.addSelectionListener (new SelectionAdapter() {
        public void widgetSelected (SelectionEvent e) {
            drawArialOnly = ((Button)e.widget).getSelection();
            createFontLists (drawScalable, drawArialOnly);
        }
    });
    Button national = new Button (group2, SWT.CHECK);
    national.setText ("National");
    national.setSelection (drawNational);
    national.addSelectionListener (new SelectionAdapter() {
        public void widgetSelected (SelectionEvent e) {
            drawNational = ((Button)e.widget).getSelection();
            updateTitle();
        }
    });

    FormData fdata = new FormData();
    fdata.right = new FormAttachment (100, 0);
    fdata.bottom = new FormAttachment (100, 0);
    group1.setLayoutData (fdata);
    fdata = new FormData();
    fdata.left = new FormAttachment (0, 0);
    fdata.bottom = new FormAttachment (100, 0);
    group2.setLayoutData (fdata);
    FormLayout formLayout = new FormLayout ();
    bottom.setLayout (formLayout);
    
    GridData gdata = new GridData (
        GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL |
        GridData.VERTICAL_ALIGN_FILL | GridData.GRAB_VERTICAL);
    canvas.setLayoutData (gdata);
    gdata = new GridData (
        GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL |
        GridData.VERTICAL_ALIGN_CENTER);
    bottom.setLayoutData (gdata);
    
    GridLayout gridLayout = new GridLayout (1, false);
    shell.setLayout (gridLayout);
    shell.setMaximized (true);
    // Win32 workaround: this forces canvas bounds to be properly
    // calculated for the maximized state, since windows in Windows
    // cannot be maximized when not visible, in that sense that their
    // real size won't change until the window is actually shown.
    shell.open();
    
    createFontLists (drawScalable, drawArialOnly);
}

void updateTitle () {
    String title = fonts.length + " fonts - page #" + page + " of " + lastPage;
    if (drawNational) {
        title += "  " + strCYR + "  " + strLAT + "  " + strJPN;
    }
    setTitle (title);
    canvas.redraw();
}

void createFontLists (boolean scalable, boolean arialOnly) {
    String faceName = arialOnly ? "Arial" : null;
    FontData[] list = display.getFontList (faceName, scalable);
    fonts = new Font [list.length];
    fonts_ru_RU = new Font [list.length];
    fonts_en_GB = new Font [list.length];
    fonts_ja_JP = new Font [list.length];
    fontHeights = new int [list.length];
    ArrayList pages = new ArrayList();
    pages.add (new Integer (0));
    if (list.length > 0) {
        Rectangle r = canvas.getClientArea();
        int height = 0;
        for (int i = 0; i < list.length; i++) {
            fonts [i] = new Font (display, list [i]);
            list [i].setLocale ("ru_RU");
            fonts_ru_RU [i] = new Font (display, list [i]);
            list [i].setLocale ("en_GB");
            fonts_en_GB [i] = new Font (display, list [i]);
            list [i].setLocale ("ja_JP");
            fonts_ja_JP [i] = new Font (display, list [i]);
            
            int h = ((display.getDPI().y * list [i].getHeight()) / 72) * 33 / 24;
            if (height + h > r.height) {
                pages.add (new Integer (i));
                height = 0;
            }
            height += h;
        }
    }
    lastPage = pages.size();
    fontPages = new Integer [lastPage];
    fontPages = (Integer[]) pages.toArray (fontPages);
   	page = 1;
    prev.setEnabled (false);
    next.setEnabled (lastPage > 1);
    updateTitle();
}

void destroyFonts (Font[] fonts) {
	for (int i = 0; i < fonts.length; i++) {
		Font f = fonts[i];
        if (!f.equals (display.getSystemFont())) f.dispose();
	}
}

int drawFont (GC gc, int idx, int x0, int y0) {
    Font f = fonts [idx];
    FontData fd = f.getFontData() [0];
    String str = " " + fd.getHeight() + ".";
    String name;
    if (f.equals (display.getSystemFont())) name = "<System Font>";
    else name = fd.getName(); 
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
    gc.setFont (f);
    FontMetrics fm = gc.getFontMetrics();
    Point box;

    if (drawNational) {
        gc.drawString (str, x0, y0, !drawMetrics);
        box = gc.stringExtent (str);
        str = "  " + strCYR; 
        gc.setFont (fonts_ru_RU [idx]);
        gc.drawText (str, x0 + box.x, y0, !drawMetrics);
        box.x += gc.textExtent (str).x;
        str = "  " + strLAT; 
        gc.setFont (fonts_en_GB [idx]);
        gc.drawText (str, x0 + box.x, y0, !drawMetrics);
        box.x += gc.textExtent (str).x;
        str = "  " + strJPN; 
        gc.setFont (fonts_ja_JP [idx]);
        gc.drawText (str, x0 + box.x, y0, !drawMetrics);
        box.x += gc.textExtent (str).x;
    } else {
        str += " " + getAdditionalFontInfo (fd, fm);
        gc.drawString (str, x0, y0, !drawMetrics);
        box = gc.stringExtent (str);
    }
    
    if (drawMetrics) {
        int h = box.y / 3;
    	drawCorners (gc, x0, y0, box.x - 1, box.y - 1, h);
        h = fm.getHeight() - fm.getDescent();
        gc.setForeground (display.getSystemColor (SWT.COLOR_RED));
        gc.drawLine (x0, y0 + h, x0 + box.x - 1, y0 + h);
        h = fm.getLeading();
        gc.setForeground (display.getSystemColor (SWT.COLOR_BLUE));
        gc.drawLine (x0, y0 + h, x0 + box.x - 1, y0 + h);
    }
    
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

void drawCorners (GC gc, int x, int y, int dx, int dy, int size) {
    Color c = gc.getForeground();
    gc.setForeground (display.getSystemColor (SWT.COLOR_DARK_RED));
    gc.drawPolyline (
    	new int[] {x, y + size, x, y, x + size, y});
    gc.drawPolyline (
    	new int[] {x + dx, y + dy - size, x + dx, y + dy, x + dx - size, y + dy});
    gc.setForeground (c);
}

void drawGrid (GC gc, int x0, int y0, int width, int height, int step) {
    if (drawCheckers) {
        if (background == null) {
            int w = 20, h = 20;
            Display display = Display.getDefault();
            background = new Image (display, w, h);
            GC bgc = new GC (background);
            bgc.setBackground (display.getSystemColor (SWT.COLOR_WHITE));
            bgc.fillRectangle(0, 0, 10, 10);
            bgc.fillRectangle(10, 10, 10, 10);
            bgc.setBackground (display.getSystemColor (SWT.COLOR_GRAY));
            bgc.fillRectangle(10, 0, 10, 10);
            bgc.fillRectangle(0, 10, 10, 10);
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

}

