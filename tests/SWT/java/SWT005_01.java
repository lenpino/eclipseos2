/*
 *  SWT005_01.java
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
 * 	This testcase tests font drawing capabilities.
 * 
 *  Pages #1, #2 and #3
 *  -------------------
 * 
 * 	These pages test different ways of font creation, functionality of
 * 	the font matching algorithm, font drawing (i.e. Font, FontData classes
 *  and their methods, GC.drawString()), and also various FontMetrics.get*()
 *  methods and GC.stringExtent().
 * 
 *  The first font drawn is the system default font (used when the requested
 * 	font is not found). The second font is the default font with the "strikeout"
 *  emphasis. The third font is the widget default font. All other fonts represent
 * 	different font families drawin in four basic styles.
 * 
 * 	Every font is demonstrated using a string that describes it. The description
 * 	string contains:
 *  -	the requested font height in points;
 *  -	the requested font family;
 *  -	the requested style (B = Bold, I = Italic);
 *  -	the additional info in square or curly brackets.
 * 
 * 	Fonts drawn on the page #1 are created using the height, family name and style.
 * 	Fonts drawn on the page #2 are created from fonts on the page #1 using
 * 	the crossplatform string representation of the font (FontData.toString()).
 * 	Fonts drawn on the page #3 are created from fonts on the page #1 using
 *  their FontData objects directly (not through the string representation).
 * 
 *  Fonts used to draw strings on page #1 should match fonts drawn on pages
 *  #2 and #3. Font description strings should also match (except the third
 *  font from the top) and only the additional info differs depending on the
 *  page number:
 *
 *  -   page #1: font metrics (common and OS-specific) are drawn in square
 *      brackets:
 *      *   font style (B for bold and/or I for italic, or none)
 * 	    *   true font size (calculated as (ascent + descent) * 72 / dpi) for
 *          outline fonts and taken from font metrics for bitmap fonts)
 * 	    *   true font face name (OS/2 only)
 * 	    *   FontMetrics.getHeight(), which should be a sum of:
 * 		    +   FontMetrics.getLeading()
 * 		    +   FontMetrics.getAccent()
 * 		    +   FontMetrics.getDescent()
 * 		*   FontMetrics.getAverageCharWidth()
 * 		*   Unique internal system font ID (OS/2 only, zero in other cases)
 *  -   pages #2 and #3: the crossplatform string representation of the font
 *      (FontData.toString()) is drawn in curly brackets.
 *	
 * 	Also:
 * 
 *	-	dark red string box corners should exactly match the edges
 * 		of the string background (cyan) area (lay down on these edges);
 * 	-	letters should "sit" on the red (base) line along the string;
 * 	-	letter tops should be near the blue (accent) line along the string.
 *
 * 	Page #4
 * 	-------
 * 
 * 	This page tests GC.stringExtent(), GC.getAdvanceWidth() and
 * 	GC.getCharWidth():
 * 
 * 	a)	title should be drawn in the default window font;
 * 	b)	dark red string/char box corners should exactly match the edges
 * 		of the string/char background area (lay down on these edges);
 *  c)	letters drawn on the alternating background should be placed
 * 		exactly under letters drawn on the cyan background;
 *  d)	white and yellow strips should match widths of char bounding boxes
 *      and can overlap for some font styles, their width corresponds to
 *      the x field of the Point returned by gc.stringExtent();
 *  e)	red and dark red strips should match advance widths of chars and
 *      should not overlap, their width corresponds to gc.getAdvanceWidth();
 *  f)	blue and dark blue strips should be equal or shorter (but not longer)
 *      than red ones, their width corresponds to gc.getCharWidth().
 *
 *  Note that due to errors in calculation of font metrics in the
 *  OS/2 font engine c) is not true for Type 1 and TrueType fonts --
 *  strings drawn as a whole can differ in total length from strings
 *  drawn char by char. Also, the width of chars of monospaced TrueType
 *  fonts from one family can slightly differ (it can be seen on the third
 *  column where the Courier New family is drawn) although it must be equal.
 *  These things will be fixed in the future.   
 * 		
 * Page #5
 * -------
 * 
 * 	This page tests the ability to change fonts of Control objects.
 * 	It enables the Change chekbox at the left bottom of the Shell:
 * 
 * 	-	when Change is uncheked the Next button is drawn in the default
 * 		button font
 * 	-	when Change is checked the Next button is drawn in the Times
 * 		New Roman Bold font.
 * 
 * 	The string drawn at the top of the Shell should reflect the font
 *  change and be of the same font as the Next button. The text drawn
 *  is the FontData.toString() of the button's Font object. Note that
 *  this text always contains the family name of the existing font that
 *  matched the request we made when created an alternate font for the next
 *  button, even if we request non-existent one. This is because the
 *  information about the requested font family is discarded when the font
 *  is sent to the control object and then read back.
 * 	
 */

public class SWT005_01 extends SWTTestCase {

static {
    STEP = "005";
    TEST = "01";
    DESC = "Fonts";
}

public static void main (String [] args) {
    go (new SWT005_01 ());
}

int page = 1;
final static int LAST_PAGE = 5;

Button next, prev, change;

boolean drawCheckers = false;
boolean drawMetrics = true;
boolean changeFont = false;
Image background = null;

String [] fontDescs1 = {
    "11.nonexistent",
    "9.WarpSans",
    "18.Times New Roman",
    "10.Lucida Sans Typewriter",
    "13.Helv",
    "0.Arial Black"
};

String [] fontDescs2 = {
    "24.Helv", // bitmap font
    "24.Helvetica", // type1 font (in OS/2)
    "24.Courier New", // truetype fonr
};

ArrayList fonts1_1 = new ArrayList();
ArrayList fonts1_2 = new ArrayList();
ArrayList fonts1_3 = new ArrayList();
ArrayList fonts2 = new ArrayList();

Font nextFont;

static String platform = SWT.getPlatform();;
Class fmHndCls;
Class fdCls;

Shell createTopShell (Display display) {
    return new Shell (display,
        SWT.SHELL_TRIM | SWT.NO_REDRAW_RESIZE | SWT.NO_BACKGROUND); // | SWT.NO_MERGE_PAINTS);
}

void initComponents () {
    /* create fonts for 1st drawFont() */
    FontData fd = new FontData();
    // uninitialized font
    fonts1_1.add (new Font (display, fd));
    // striked out font
    fd.setName ("<striked out>");
    setStrikeOut (fd);
    fonts1_1.add (new Font (display, fd));
    // system font
    fonts1_1.add (display.getSystemFont());
    // other fonts
	createFonts (fontDescs1, fonts1_1);

	/* create fonts for 2nd and 3rd drawFont() */
    Iterator it = fonts1_1.iterator();
    while (it.hasNext()) {
        Font f = (Font) it.next();
        fd = f.getFontData() [0];
        fonts1_2.add (new Font (display, new FontData (fd.toString())));
        fonts1_3.add (new Font (display, fd));
    }

	/* create fonts for testFont() */
	createFonts (fontDescs2, fonts2);
	
    nextFont = new Font (display, "<unknown_font>", 12, SWT.ITALIC);
        
    shell.addDisposeListener (new DisposeListener () {
        public void widgetDisposed (DisposeEvent e) {
        	destroyFonts (fonts1_1);
        	destroyFonts (fonts1_2);
        	destroyFonts (fonts1_3);
        	destroyFonts (fonts2);
        	nextFont.dispose();
            if (background != null) background.dispose();
        }
    });

    shell.addPaintListener(new PaintListener () {
        public void paintControl(PaintEvent event) {
            GC gc = event.gc;
            
            System.out.println ("Font before PAINT: " + gc.getFont().getFontData()[0]);

            Rectangle r = new Rectangle (event.x, event.y, event.width, event.height);
            gc.setClipping (r);

            drawGrid (gc,
                (r.x / 20) * 20, (r.y / 20) * 20,
                r.width + 20,  r.height + 20, 10);
            
			Iterator it;
			int x, y, i;
            switch (page) {
                case 1:
                case 2:
                case 3:
                    y = 10;
                    it =
                        page == 1 ? fonts1_1.iterator() :
                        page == 2 ? fonts1_2.iterator() :
                        fonts1_3.iterator();
                    while (it.hasNext()) {
                        Font f = (Font) it.next();
                        y += drawFont (gc, f, 10, y) + 2;
                    }
                    break;
                case 4:
                    x = 10; y = 10; i = 0;
                	it = fonts2.iterator();
                    while (it.hasNext()) {
                    	if (i % 4 == 0) {
                    		gc.setFont (null);
                    		String str = fontDescs2 [i / 4] + ": ";
                    		switch (i / 4) {
                    			case 0: str += "Bitmap font"; break;
                    			case 1: str += "Type 1 font (only in OS/2)"; break;
                    			case 2: str += "TrueType font"; break;
                    		}
                    		gc.drawString (str, x, y, true);
                    		y += 30;
                    	}
                        Font f = (Font) it.next();
                        y += testFont (gc, f, x, y) + 5;
                        i ++;
                        if (i % 4 == 0) {
                        	x += SWT005_01.this.display.getDPI().x * 25 * 8 / 72;
                            y = 10;
                        }
                    }
                    break;
                case 5: {
                	Font f = next.getFont();
                	gc.setFont (f);
                	String str =
                		"This is the font from the Next button:\n" +
						f.getFontData()[0] + "\n\n" +
						"Check the Change checkbox at the bottom to change it.\n";
					y = 10;
					gc.drawText (str, 10, y);
					y += gc.textExtent(str).y;
					y += testTextBox(gc, 10, y, 0) + 10;
					y += testTextBox(gc, 10, y, SWT.DRAW_DELIMITER | SWT.DRAW_TAB | SWT.DRAW_MNEMONIC) + 10;
					y += testTextBox(gc, 10, y, SWT.DRAW_DELIMITER) + 10;
					y += testTextBox(gc, 10, y, SWT.DRAW_TAB) + 10;
					y += testTextBox(gc, 10, y, SWT.DRAW_MNEMONIC) + 10;
					break;
                }
            }
            System.out.println ("Font after PAINT: " + gc.getFont().getFontData()[0]);
        }
    });

    Composite group1 = new Composite (shell, SWT.NO_BACKGROUND);
    group1.setLayout (new FillLayout (SWT.HORIZONTAL));

    prev = new Button (group1, SWT.PUSH);
    prev.setText ("< Previous");
    prev.setEnabled (false);
    prev.addSelectionListener (new SelectionAdapter () {
        public void widgetSelected (SelectionEvent e) {
            if (page > 1) {
                page --;
                updateTitle();
            }
            if (page == 1) prev.setEnabled (false);
            if (page == LAST_PAGE - 1) next.setEnabled (true);
           	change.setEnabled (page == 5);
        }
    });
    next = new Button (group1, SWT.PUSH);
    next.setText ("Next >");
    next.addSelectionListener (new SelectionAdapter () {
        public void widgetSelected (SelectionEvent e) {
            if (page < LAST_PAGE) {
                page ++;
                updateTitle();
            }
            if (page == LAST_PAGE) next.setEnabled (false);
            if (page == 2) prev.setEnabled (true);
           	change.setEnabled (page == 5);
        }
    });
    updateTitle();

    Composite group2 = new Composite (shell, SWT.NO_BACKGROUND);
    group2.setLayout (new FillLayout (SWT.HORIZONTAL));
    
    Button checks = new Button (group2, SWT.CHECK);
    checks.setText ("Checkers");
    checks.setSelection (drawCheckers);
    checks.addSelectionListener (new SelectionAdapter() {
        public void widgetSelected (SelectionEvent e) {
            drawCheckers = ((Button)e.widget).getSelection();
            shell.redraw();
        }
    });
    Button metrics = new Button (group2, SWT.CHECK);
    metrics.setText ("Metrics");
    metrics.setSelection (drawMetrics);
    metrics.addSelectionListener (new SelectionAdapter() {
        public void widgetSelected (SelectionEvent e) {
            drawMetrics = ((Button)e.widget).getSelection();
            shell.redraw();
        }
    });
    change = new Button (group2, SWT.CHECK);
    change.setText ("Change");
    change.setSelection (changeFont);
    change.setEnabled (false);
    change.addSelectionListener (new SelectionAdapter() {
        public void widgetSelected (SelectionEvent e) {
            changeFont = ((Button)e.widget).getSelection();
            if (changeFont) next.setFont (nextFont);
            else next.setFont (null);
            shell.layout();
            shell.redraw();
        }
    });

    FormData fdata = new FormData();
    fdata.right = new FormAttachment (100, -10);
    fdata.bottom = new FormAttachment (100, -10);
    group1.setLayoutData (fdata);

    fdata = new FormData();
    fdata.left = new FormAttachment (0, 10);
    fdata.bottom = new FormAttachment (100, -10);
    group2.setLayoutData (fdata);

    FormLayout formLayout = new FormLayout ();
    shell.setLayout (formLayout);

    shell.setMaximized (true);
}

void updateTitle () {
    setTitle ("page #" + page + " of " + LAST_PAGE);
    shell.redraw ();
}

void createFonts (String[] fontDescs, ArrayList fonts) {
    for (int i = 0; i < fontDescs.length; i++) {
        String desc = fontDescs [i];
        int height = 0;
        int pnt = desc.indexOf ('.');
        if (pnt >= 0) {
            try {
                height = Integer.parseInt (desc.substring (0, pnt));
            } catch (NumberFormatException x) {
            }
        }
        String name = desc.substring (pnt + 1);
        for (int j = 0; j < 4; j ++) {
            fonts.add (new Font (display, new FontData (name, height, j)));
        }
    }
}

void destroyFonts (ArrayList fonts) {
    Iterator it = fonts.iterator();
    while (it.hasNext()) {
        Font f = (Font) it.next();
        if (!f.equals (display.getSystemFont())) f.dispose();
    }
}

int drawFont (GC gc, Font f, int x0, int y0) {
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

    gc.setFont (f);
    FontMetrics fm = gc.getFontMetrics();
    str += " " + getAdditionalFontInfo (fd, fm);

    Point box = gc.stringExtent (str);
    int hh = box.y / 3;

    gc.setForeground (display.getSystemColor (SWT.COLOR_BLACK));
    gc.setBackground (display.getSystemColor (SWT.COLOR_CYAN));
    gc.drawString (str, x0, y0, !drawMetrics);
    
    if (drawMetrics) {
    	drawCorners (gc, x0, y0, box.x - 1, box.y - 1, hh);
    
        int h;
        h = fm.getHeight() - fm.getDescent();
        gc.setForeground (display.getSystemColor (SWT.COLOR_RED));
        gc.drawLine (x0, y0 + h, x0 + box.x - 1, y0 + h);
        h = fm.getLeading();
        gc.setForeground (display.getSystemColor (SWT.COLOR_BLUE));
        gc.drawLine (x0, y0 + h, x0 + box.x - 1, y0 + h);
    }
    
    return fm.getHeight();
}

void setStrikeOut (FontData fd) {
    if (fdCls == null) {
        fdCls = fd.getClass();
    }
    try {
        if (platform.equals ("pm")) {
            fdCls.getField ("fsSelection").setShort (fd, (short)0x0010 /* FATTR_SEL_STRIKEOUT */);
        } else if (platform.equals ("win32")) {
            Object data = fdCls.getField ("data").get (fd);
            data.getClass().getField ("lfStrikeOut").setByte (data, (byte)1);
        }
    } catch (NoSuchFieldException x) {
    } catch (IllegalAccessException x) {
    }
}

String getAdditionalFontInfo (FontData fd, FontMetrics fm) {
    if (fmHndCls == null) {
        fmHndCls = fm.handle.getClass();
    }

    String str = null;
    if (page == 1) {
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
        str = "[" +
            actualHeight + "." +
            faceName + ";" +
            fm.getHeight() + "=" +
            fm.getLeading() + "+" + fm.getAscent() + "+" + fm.getDescent() + ";" +
            fm.getAverageCharWidth() + ";" + 
            lMatch +
            "] ";
    } else {
        str = "{" + fd.toString() + "} "; 
    }
    return str;
}

int testFont (GC gc, Font f, int x0, int y0) {
    gc.setFont (f);
    String str = "1234 abcd ABCD";
    Point ext = gc.stringExtent (str);
    gc.setBackground (display.getSystemColor (SWT.COLOR_CYAN));
    gc.drawString (str, x0, y0, !drawMetrics);
    if (drawMetrics) drawCorners (gc, x0, y0, ext.x - 1, ext.y - 1, 3);

    int x = x0, y = y0 + ext.y, y1 = y + ext.y;
    char[] chars = new char [str.length()];
    str.getChars(0, str.length(), chars, 0);
    for (int i = 0; i < chars.length; i++) {
        char ch = chars [i];
        String s = new String (chars, i, 1);
        Point e = gc.stringExtent (s);
        gc.setBackground (display.getSystemColor (
            i % 2 == 0 ? SWT.COLOR_WHITE : SWT.COLOR_YELLOW));
        gc.drawString (s, x, y, !drawMetrics);
        if (drawMetrics) {
			drawCorners (gc, x, y, e.x - 1, e.y - 1, 3);
			int dy;
	        gc.setBackground (display.getSystemColor (
	            i % 2 == 0 ? SWT.COLOR_WHITE : SWT.COLOR_DARK_YELLOW));
	        dy = (i % 2 == 0) ? 3 : 6;
	        gc.fillRectangle (x, y1 + dy, e.x, 3);
	        gc.setBackground (display.getSystemColor (
	            i % 2 == 0 ? SWT.COLOR_RED : SWT.COLOR_DARK_RED));
	        dy = (i % 2 == 0) ? 12 : 15;
	        gc.fillRectangle (x, y1 + dy, gc.getAdvanceWidth (ch), 3);
	        gc.setBackground (display.getSystemColor (
	            i % 2 == 0 ? SWT.COLOR_BLUE : SWT.COLOR_DARK_BLUE));
	        dy = (i % 2 == 0) ? 21 : 24;
	        gc.fillRectangle (x, y1 + dy, gc.getCharWidth (ch), 3);
        }
        x += gc.getAdvanceWidth (ch);
    }
    return y1 + 30 - y0;
}

int testTextBox (GC gc,  int x0, int y0, int flags) {
	if (drawMetrics) flags &= ~SWT.DRAW_TRANSPARENT;
	else flags |= SWT.DRAW_TRANSPARENT;
	
	String str = "";
	if ((flags & SWT.DRAW_DELIMITER) != 0) str += " DRAW_DELIMITER";
	if ((flags & SWT.DRAW_MNEMONIC) != 0) str += " DRAW_MNEMONIC";
	if ((flags & SWT.DRAW_TAB) != 0) str += " DRAW_TAB";
	if ((flags & SWT.DRAW_TRANSPARENT) != 0) str += " DRAW_TRANSPARENT";
	if (str.length() == 0) str = "Flags: none\n\n";
	else str = "Flags: " + str + "\n\n";
	str +=
		"* &One Mnemonic [\\n]\n" +
		"* Trying &Two M&nemonics [\\r]\r" +
		"* Regular Line [\\r\\n]\r\n" + 
		"*        Spaced Line [\\n\\r]\n\r" +
		"*\tTabbed Line\n" +
        "\t1\t2\t3\t4\n" +
		"*\t\tDouble Tabbed Line";
		
	Point pnt = gc.textExtent(str, flags);
	gc.setForeground (display.getSystemColor (SWT.COLOR_BLACK));
	gc.setBackground (display.getSystemColor (SWT.COLOR_CYAN));
	gc.drawText(str, x0, y0, flags);
	if (drawMetrics) {
		gc.setForeground(display.getSystemColor(SWT.COLOR_RED));
		gc.drawRectangle(x0, y0, pnt.x - 1, pnt.y - 1);
	}
	return pnt.y;
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

