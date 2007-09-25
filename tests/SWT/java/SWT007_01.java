/*
 *  SWT007_01.java
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
 * 	This testcase allows to create Buttons with all possible styles (except
 *  alignment styles that are not currently suppored by SWT for OS/2) and 
 *  lay them out in different ways. It also tests SWT.FocusIn.FocusOut,
 *  SWT.Selection and SWT.Activate/Deactivate/Iconify/Deiconify event delivery,
 *  as well as focus traversal using the keyboard in a standard way (TAB,
 *  arrows, mnemonics).
 *
 *  To check that the testcase succeeds, a careful comparison with SWT for
 *  Win32 should be done. 
 */

public class SWT007_01 extends SWTTestCase {

static {
    STEP = "007";
    TEST = "01";
    DESC = "Buttons & Focus Traversal";
}

public static void main (String [] args) {
    go (new SWT007_01 ());
}

int page = 1;
final static int LAST_PAGE = 1;

Image background = null;

Button next, prev;

boolean drawCheckers = false;
boolean isFillLayout = false;
boolean isFlat = false;
boolean isEnabled = true;
boolean isVisible = true;
boolean isColorFgnd = false;
boolean isColorBgnd = false;
String isImage = "t";

Color clrForeground = null;
Color clrBackground = null;
Image imgBitmap, imgIcon32x32, imgIcon48x48;

boolean isCustomFont = false;
Font fntCustom;

ArrayList buttons = new ArrayList();

final static FillLayout fillLayout = new FillLayout (SWT.VERTICAL); 
final static RowLayout rowLayout = new RowLayout (SWT.VERTICAL);
static {
    rowLayout.marginLeft = rowLayout.marginRight = 
        rowLayout.marginTop = rowLayout.marginBottom = 0;
    rowLayout.spacing = 4;
}

Canvas canvas;

final static String imageDir = System.getProperty ("user.dir") +
    "/tests/SWT/images/"; 

class MyFocusListener implements FocusListener {
    public void focusGained (FocusEvent e) {
        System.out.println("FocusIn: " + e.widget);
    }
    public void focusLost (FocusEvent e) {
        System.out.println("FocusOut: " + e.widget);
    }
}

class MySelectionListener implements SelectionListener {
    public void widgetDefaultSelected (SelectionEvent e) {
        System.out.println("DefaultSelected: " + e.widget + ", (" + e.item +"), [" +
            e.x + "," + e.y + ";" + e.width + "," + e.height + "], " +
            "selected=" + ((Button)e.widget).getSelection());
    }
    public void widgetSelected (SelectionEvent e) {
        System.out.println("Selected: " + e.widget + ", (" + e.item +"), [" +
            e.x + "," + e.y + ";" + e.width + "," + e.height + "], " +
            "selected=" + ((Button)e.widget).getSelection());
    }
}

class MyShellListener implements ShellListener {
    public void shellActivated (ShellEvent e) {
        System.out.println("shellActivated: " + e.widget);
    }
    public void shellClosed (ShellEvent e) {
        System.out.println("shellClosed: " + e.widget);
    }
    public void shellDeactivated (ShellEvent e) {
        System.out.println("shellDeactivated: " + e.widget);
    }
    public void shellDeiconified (ShellEvent e) {
        System.out.println("shellDeiconified: " + e.widget);
    }
    public void shellIconified (ShellEvent e) {
        System.out.println("shellIconified: " + e.widget);
    }
}

//@@TODO (dmik): seems to work well, disable for cleaner output 
//class MyTraverseListener implements TraverseListener {
//    public void keyTraversed (TraverseEvent e) { 
//        System.out.println("traversed: " + e.widget + ", detail=" + e.detail);
//    }
//}

Shell createTopShell (Display display) {
    return new Shell (display,
        SWT.SHELL_TRIM | SWT.NO_REDRAW_RESIZE); // | SWT.NO_BACKGROUND); // | SWT.NO_MERGE_PAINTS);
}

void createButtons (Composite composite, boolean isFlat) {
    Iterator it = buttons.iterator();
    while (it.hasNext()) ((Control)it.next()).dispose();
    buttons.clear();

    int flat = isFlat ? SWT.FLAT : 0;
    Button b;
    b = new Button (composite, SWT.PUSH | flat);
    b.setText ("pus&h");
    buttons.add (b);
    b = new Button (composite, SWT.TOGGLE | flat);
    b.setText ("to&ggle");
    buttons.add (b);
    b = new Button (composite, SWT.CHECK | flat);
    b.setText ("check 1");
    buttons.add (b);
    b = new Button (composite, SWT.CHECK | flat);
    b.setText ("check 2");
    buttons.add (b);
    b = new Button (composite, SWT.RADIO | flat);
    b.setText ("radio 1");
    buttons.add (b);
    b = new Button (composite, SWT.RADIO | flat);
    b.setText ("radio 2");
    buttons.add (b);
    b = new Button (composite, SWT.RADIO | flat);
    b.setText ("radio 3");
    buttons.add (b);
    b = new Button (composite, SWT.ARROW | SWT.UP | flat);
    b.setText ("arrow up");
    buttons.add (b);
    b = new Button (composite, SWT.ARROW | SWT.DOWN | flat);
    b.setText ("arrow down");
    buttons.add (b);
    b = new Button (composite, SWT.ARROW | SWT.LEFT | flat);
    b.setText ("arrow left");
    buttons.add (b);
    b = new Button (composite, SWT.ARROW | SWT.RIGHT | flat);
    b.setText ("arrow right");
    buttons.add (b);

    it = buttons.iterator();
    while (it.hasNext()) {
        b = ((Button)it.next());
        b.setEnabled (isEnabled);
        b.setVisible (isVisible);
        if (isColorFgnd) b.setForeground (clrForeground);
        if (isColorBgnd) b.setBackground (clrBackground);
        if (isImage.equals ("b")) b.setImage (imgBitmap);
        else if (isImage.equals ("i")) b.setImage (imgIcon32x32);
        else if (isImage.equals ("li")) b.setImage (imgIcon48x48);
        if (isCustomFont) b.setFont (fntCustom);
        b.addFocusListener (new MyFocusListener());
        b.addSelectionListener (new MySelectionListener());
//@@TODO (dmik): seems to work well, disable for cleaner output 
//        b.addTraverseListener (new MyTraverseListener());
    }
}

void initComponents () {
    imgBitmap = new Image (display, imageDir + "img01.24.bmp"); //"icon48_full.bmp");
//    imgBitmap = new Image (display, imageDir + "img01.1.tr-b.gif");
    imgIcon32x32 = new Image (display, imageDir + "test.ico");
//    imgIcon48x48 = new Image (display, imageDir + "test_20x20.ico");
    ImageData[] iconData = new ImageLoader().load (imageDir + "eclipse.ico");
    imgIcon48x48 = new Image (display, iconData[iconData.length - 1]);
    
    fntCustom = new Font (display, "Arial", 24, SWT.NORMAL);
    
    clrForeground = display.getSystemColor (SWT.COLOR_RED);
    clrBackground = display.getSystemColor (SWT.COLOR_GREEN); 

    shell.addDisposeListener (new DisposeListener () {
        public void widgetDisposed (DisposeEvent e) {
            if (background != null) background.dispose();
            imgBitmap.dispose();
            imgIcon32x32.dispose();
            imgIcon48x48.dispose();
            fntCustom.dispose();
        }
    });

    shell.addFocusListener (new MyFocusListener());
    shell.addShellListener (new MyShellListener());
    
    canvas = new Canvas (shell, SWT.NO_BACKGROUND);
    canvas.addPaintListener(new PaintListener () {    
        public void paintControl(PaintEvent event) {
            GC gc = event.gc;
            
            Rectangle r = new Rectangle (event.x, event.y, event.width, event.height);
            gc.setClipping (r);

            drawGrid (gc,
                (r.x / 20) * 20, (r.y / 20) * 20,
                r.width + 20,  r.height + 20, 10);
            
            switch (page) {
                case 1: {
					break;
                }
            }
        }
    });

    createButtons (canvas, isFlat);
    canvas.setLayout (isFillLayout ? (Layout) fillLayout : (Layout) rowLayout);
    
    // -------------------------------
    
    Composite bottom = new Composite (shell, 0);
    
    Composite group1 = new Composite (bottom, 0); //SWT.NO_BACKGROUND);
    group1.setLayout (new FillLayout (SWT.VERTICAL));
    
    prev = new Button (group1, SWT.PUSH);
    prev.setText ("< &Previous");
    prev.setEnabled (page > 1);
    prev.addSelectionListener (new SelectionAdapter () {
        public void widgetSelected (SelectionEvent e) {
            if (page > 1) {
                page --;
                updateTitle();
            }
            if (page == 1) prev.setEnabled (false);
            if (page == LAST_PAGE - 1) next.setEnabled (true);
        }
    });
    next = new Button (group1, SWT.PUSH);
    next.setText ("&Next >");
    next.setEnabled (LAST_PAGE > page);
    next.addSelectionListener (new SelectionAdapter () {
        public void widgetSelected (SelectionEvent e) {
            if (page < LAST_PAGE) {
                page ++;
                updateTitle();
            }
            if (page == LAST_PAGE) next.setEnabled (false);
            if (page == 2) prev.setEnabled (true);
        }
    });
    updateTitle();

    Composite group2 = new Composite (bottom, 0); //, SWT.NO_BACKGROUND);
    group2.setLayout (new RowLayout (SWT.VERTICAL));
    
    Button checks = new Button (group2, SWT.CHECK);
    checks.setText ("&Checkers");
    checks.setSelection (drawCheckers);
    checks.addSelectionListener (new SelectionAdapter() {
        public void widgetSelected (SelectionEvent e) {
            drawCheckers = ((Button)e.widget).getSelection();
            canvas.redraw();
        }
    });
    Button layout = new Button (group2, SWT.CHECK);
    layout.setText ("Fill &Layout");
    layout.setSelection (isFillLayout);
    layout.addSelectionListener (new SelectionAdapter() {
        public void widgetSelected (SelectionEvent e) {
            isFillLayout = ((Button)e.widget).getSelection();
            canvas.setLayout (isFillLayout ? (Layout) fillLayout : (Layout) rowLayout);
            canvas.layout();
        }
    });
    Button flat = new Button (group2, SWT.CHECK);
    flat.setText ("&Flat");
    flat.setSelection (isFlat);
    flat.addSelectionListener (new SelectionAdapter() {
        public void widgetSelected (SelectionEvent e) {
            isFlat = ((Button)e.widget).getSelection();
            createButtons (canvas, isFlat);
            canvas.layout();
        }
    });
    Button colorCanvas = new Button (group2, SWT.CHECK);
    colorCanvas.setText ("C&olor Canvas");
    colorCanvas.addSelectionListener (new SelectionAdapter() {
        public void widgetSelected (SelectionEvent e) {
            if (((Button)e.widget).getSelection())
                canvas.setBackground (display.getSystemColor (SWT.COLOR_CYAN));
            else
                canvas.setBackground (null);
        }
    });
    Button colorFgnd = new Button (group2, SWT.CHECK);
    colorFgnd.setText ("Colo&r Foreground");
    colorFgnd.setSelection (isColorFgnd);
    colorFgnd.addSelectionListener (new SelectionAdapter() {
        public void widgetSelected (SelectionEvent e) {
            isColorFgnd = ((Button)e.widget).getSelection();
            Color color = isColorFgnd ? clrForeground : null; 
            Iterator it = buttons.iterator();
            while (it.hasNext()) ((Button)it.next()).setForeground (color);
        }
    });
    Button colorBgnd = new Button (group2, SWT.CHECK);
    colorBgnd.setText ("Color &Background");
    colorFgnd.setSelection (isColorBgnd);
    colorBgnd.addSelectionListener (new SelectionAdapter() {
        public void widgetSelected (SelectionEvent e) {
            isColorBgnd = ((Button)e.widget).getSelection();
            Color color = isColorBgnd ? clrBackground : null; 
            Iterator it = buttons.iterator();
            while (it.hasNext()) ((Button)it.next()).setBackground (color);
        }
    });
    Button enabled = new Button (group2, SWT.CHECK);
    enabled.setText ("&Enabled");
    enabled.setSelection (isEnabled);
    enabled.addSelectionListener (new SelectionAdapter() {
        public void widgetSelected (SelectionEvent e) {
            isEnabled = ((Button)e.widget).getSelection();
            Iterator it = buttons.iterator();
            while (it.hasNext()) ((Control)it.next()).setEnabled (isEnabled);
        }
    });
    Button visible = new Button (group2, SWT.CHECK);
    visible.setText ("&Visible");
    visible.setSelection (isVisible);
    visible.addSelectionListener (new SelectionAdapter() {
        public void widgetSelected (SelectionEvent e) {
            isVisible = ((Button)e.widget).getSelection();
            Iterator it = buttons.iterator();
            while (it.hasNext()) ((Control)it.next()).setVisible (isVisible);
        }
    });
    Button customFont = new Button (group2, SWT.CHECK);
    customFont.setText ("Cu&stom Font");
    customFont.setSelection (false);
    customFont.addSelectionListener (new SelectionAdapter() {
        public void widgetSelected (SelectionEvent e) {
            isCustomFont = ((Button)e.widget).getSelection();
            Iterator it = buttons.iterator();
            while (it.hasNext())
                ((Control)it.next()).setFont (isCustomFont ? fntCustom : null);
            canvas.layout();
        }
    });
    SelectionListener imgSelAdapter = new SelectionAdapter() {
        public void widgetSelected (SelectionEvent e) {
            Button b = (Button) e.widget;
            if (b.getSelection()) {
                Image i = null;
                isImage = (String) b.getData();
                if (isImage.equals ("b")) i = imgBitmap;
                else if (isImage.equals ("i")) i = imgIcon32x32;
                else if (isImage.equals ("li")) i = imgIcon48x48;
                Iterator it = buttons.iterator();
                while (it.hasNext()) ((Button)it.next()).setImage (i);
            }
            canvas.layout();
        }
    };
//    Composite imageGroup = new Composite (group2, 0);
    Composite imageGroup = group2;
    Button b;
    b = new Button (imageGroup, SWT.RADIO);
    b.setText ("&Text"); b.setData (new String("t")); b.setSelection (true);
    b.addSelectionListener (imgSelAdapter);
    b = new Button (imageGroup, SWT.RADIO);
    b.setText ("B&itmap"); b.setData (new String("b"));
    b.addSelectionListener (imgSelAdapter);
    b = new Button (imageGroup, SWT.RADIO);
    b.setText ("Icon &32x32"); b.setData (new String("i"));
    b.addSelectionListener (imgSelAdapter);
    b = new Button (imageGroup, SWT.RADIO);
    b.setText ("Icon &48x48"); b.setData (new String("li"));
    b.addSelectionListener (imgSelAdapter);
    imageGroup.setLayout (new FillLayout (SWT.VERTICAL));

    Button checkAll = new Button (group2, SWT.PUSH);
    checkAll.setText ("Check &All");
    checkAll.addSelectionListener (new SelectionAdapter() {
        public void widgetSelected (SelectionEvent e) {
            Iterator it = buttons.iterator();
            while (it.hasNext()) ((Button)it.next()).setSelection (true);
        }
    });
    Button unCheckAll = new Button (group2, SWT.PUSH);
    unCheckAll.setText ("&Uncheck All");
    unCheckAll.addSelectionListener (new SelectionAdapter() {
        public void widgetSelected (SelectionEvent e) {
            Iterator it = buttons.iterator();
            while (it.hasNext()) ((Button)it.next()).setSelection (false);
        }
    });
    
    FormData fdata = new FormData();
    fdata.left = new FormAttachment (0, 0);
    fdata.right = new FormAttachment (100, 0);
    fdata.bottom = new FormAttachment (100, 0);
    group1.setLayoutData (fdata);
    fdata = new FormData();
    fdata.top = new FormAttachment (0, 0);
    group2.setLayoutData (fdata);
    FormLayout formLayout = new FormLayout ();
    formLayout.marginWidth = 0;
    formLayout.marginHeight = 0;
    bottom.setLayout (formLayout);
    
    GridData gdata = new GridData (
        GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL |
        GridData.VERTICAL_ALIGN_FILL | GridData.GRAB_VERTICAL);
    canvas.setLayoutData (gdata);
    gdata = new GridData (
        GridData.HORIZONTAL_ALIGN_FILL |
        GridData.VERTICAL_ALIGN_FILL);
    bottom.setLayoutData (gdata);
    
    GridLayout gridLayout = new GridLayout (2, false);
    shell.setLayout (gridLayout);

    Rectangle dr = display.getBounds ();
    //Rectangle sr = new Rectangle (0, 0, 640, 300);
    Rectangle sr = shell.getBounds();
    sr.x = (dr.width-sr.width)/2;
    sr.y = (dr.height-sr.height)/2;
    shell.setBounds (sr);
//    shell.setMaximized (true);
}

void updateTitle () {
    setTitle ("page #" + page + " of " + LAST_PAGE);
    shell.redraw ();
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

