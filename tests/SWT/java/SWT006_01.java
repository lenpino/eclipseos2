/*
 *  SWT006_01.java
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
 *  This example tests image drawing.
 *
 *  Page #0
 *  -------
 *
 *  This test checks scaling and XOR functions in different modes:
 *
 *  1.  a 8-bit image is drawn as is and then a part of it (marked on the
 *      original with a dotted rectangle) is drawn in different proportions,
 *      then the same is repeated in the XOR mode.
 *  2.  a blank image with the display preferred depth is created, some
 *      graphics is drawn on it and then it is scaled as above.
 *  3.  a combination of the above two: a 8-bit image is taken again
 *      and graphics is drawn on it.
 *  4.  a blank image is created with the abive 8-bit image color depth and
 *      palette and then everything is repeated.
 *
 *  Also, for images from 2 to 4 background and foreground colors are queried
 *  and printed to the log just after the GC is created and then after drawing
 *  the red circle (when the foreground is changed). Note that on images
 *  3 and 4 the circle is not actually in red, it has the color closest to the
 *  red in the image's palette.
 *
 *  For all images, after drawing, an image copy is created using the
 *  getImageData() method and then painted to the right. For the first
 *  image getImageData() is called BEFORE the image's GC is disposed, for all
 *  others -- AFTER it.
 *
 *  Note that the background of the image 4 is dark -- this is because it is
 *  created using a blank ImageData instance which initially fills the image data
 *  with zeroes, that corresponds to the 0th index in the image's palette (a
 *  color close to black in our case) for palette-based images or to the black
 *  RGB color for direct color images.
 *
 *  Page #1
 *  -------
 *
 *  Tests loading and drawing images from files of different image formats.
 *  All images in one row must look identically except 1-bit images at the
 *  bottom, where every image has its own color for "foreground" pixels.
 *
 *  Page #2
 *  -------
 *
 *  Tests image transparency:
 *
 *  1.  the 1-bit image is drawin and scaled (as on the page #0) having its
 *      "background" pixels as transparent ones.
 *  2.  the 1-bit image is drawin and scaled as above having its
 *      "foreground" pixels as transparent ones.
 *  3.  the 24-bit image is drawin and scaled as above having white pixels as
 *      transparent ones.
 *
 *  Page #3
 *  -------
 *
 *  Tests image alpha settings:
 *
 *  1.  the 8-bit image is the same as on the page #0, but with alpha data
 *      manually set to appear as half-transparent in its upper third.
 *  2.  the same image but loaded from the PNG file which contains complex alpha.
 *
 *  Page #4
 *  -------
 *
 *  Tests the GC.fillGradientRectangle() method which draws a gradient strip
 *  processing from the current background color to the foreground.
 *
 *  Page #5
 *  -------
 *  
 *  Tests icon scaling. First two icons are also copied using the getImageData()
 *  method and copies are drawn to the right.
 *  
 *  Two last rows are icons indirectly created from separate icon and mask
 *  image data using the corresponding Image constructor. Note that the last
 *  but one icon is drawn inverted where the last icon is transparent -- this
 *  is because the former is created from the palette-based image which doesn't
 *  have the black color in its palette nor the space to add it (transparent
 *  pixels must be black in order for the icon transparency to work).
 *
 *  Page #6
 *  -------
 *  
 *  Tests for the Image copy constructor (one that takes one of SWT_IMAGE_*
 *  conatants as its last parameter). First column is created using
 *  SWT_IMAGE_COPY, second -- using SWT_IMAGE_DISABLE and third -- using
 *  SWT_IMAGE_GRAY. First three rows are bitmaps, last three are icons.
 *
 *  Test output
 *  -----------
 *
 *  This testcase also performs tests of Image.getBackground() /
 *  setBackground() and GC.getBackground() / setBackground() for images and
 *  its output should contain the following:
 
[img2]:
gc bg (before) = Color {255, 255, 255}
gc fg (before) = Color {0, 0, 0}
gc fg (after) = Color {255, 0, 0}
[img3]:
gc bg (before) = Color {255, 255, 255}
gc fg (before) = Color {0, 0, 0}
gc fg (after) = Color {255, 0, 0}
[img4]:
gc bg (before) = Color {255, 255, 255}
gc fg (before) = Color {0, 0, 0}
gc fg (after) = Color {255, 0, 0}
img01.1.tr-b.gif: transparent (before) = Color {135, 0, 0}
img01.1.tr-f.gif: transparent (before) = Color {0, 0, 255}
img01.24.tr.png:  transparent (before) = Color {255, 255, 255}
img01.1.tr-b.gif: transparent (after) = Color {128, 128, 128}
img01.1.tr-f.gif: transparent (after) = Color {128, 128, 128}
img01.24.tr.png:  transparent (after) = Color {128, 128, 128}
 
 *  When the system is in 256-color mode the last but five line will be:

img01.1.tr-b.gif: transparent (before) = Color {128, 0, 0}
 
 *  This is because the default palette doesn't have the color with these
 *  RGB values, so the best match is returned.
 *
 *  This testcase also saves all 15 images from the page #1 to the current
 *  working directory for comparison with originals -- they should be the
 *  same. However, when the system is in 256-color mode saving images doesn't
 *  work as desired -- this issue is descrbed in the $CVSROOT/doc/diff.html,
 *  difference #007.
 *
 *
 *  General note. When the Chechers checkbox is un-checked, the "corruption"
 *  behavior of XORed images when sizing is expected because the widget is in
 *  SWT.NO_REDRAW_RESIZE mode (i.e. it isn't completely redrawn when sizing,
 *  so different parts of images can be XORed different number of times).
 *  The same applies to images that have the alpha channel.
 */

public class SWT006_01 extends SWTTestCase {

static {
    STEP= "006";
    TEST = "01";
    DESC = "Images";
}


int page = 0;
final static int LAST_PAGE = 6;

Shell shell;
Button next;
static String Title;

boolean drawCheckers = true;
Image background = null;

final static String imageDir = System.getProperty ("user.dir") +
    "/tests/SWT/images/"; 

public static void main (String [] args) {
	Title = "Images";
    go (new SWT006_01 ());
}

Shell createTopShell (Display display) {

    shell = new Shell (display,
        SWT.SHELL_TRIM | SWT.NO_REDRAW_RESIZE);// | SWT.NO_MERGE_PAINTS);

    final Image img1 = new Image (display, imageDir + "icon48_full.bmp");
    ImageData imgData = img1.getImageData ();
    final Image img1_copy = new Image (display, imgData);
    
    final Image img2 = new Image (display, 48, 48);
    say ("[img2]:");
    final Image img2_copy = drawTwoCircles (img2, true);

    final Image img3 = new Image (display, imgData);
    say ("[img3]:");
    drawTwoCircles (img3, false);
    final Image img3_copy = new Image (display, img3.getImageData ());
    
//    RGB[] rgbs = new RGB [16];
//    for (int i = 0; i < rgbs.length; i++) rgbs [i] = imgData.palette.colors[i];
//    rgbs [0] = rgbs [2];
    PaletteData palData = new PaletteData (new RGB[] {
        new RGB (100, 100, 100), 
        new RGB (222, 0, 0), 
        new RGB (50, 50, 50), 
    });
    final Image img4 = new Image (display,
//        new ImageData (48, 48, 4, new PaletteData (rgbs)));
        new ImageData (48, 48, 4, palData));
    say ("[img4]:");
    drawTwoCircles (img4, false);
    final Image img4_copy = new Image (display, img4.getImageData ());
    
    for (int y = 0; y < imgData.height; y++) {
      for (int x = 0; x < imgData.width; x++) {
          imgData.setAlpha (x, y, y < imgData.height / 3 ? 128 : 255);
      }
    }
    final Image img1_alpha = new Image (display, imgData);
    final Image img1_alpha2 = new Image (display,
        imageDir + "icon48_full.alpha.png");
    
    final Image img5 = new Image (display, imageDir + "img01.1.tr-b.gif");
    final Image img6 = new Image (display, imageDir + "img01.1.tr-f.gif");
    final Image img7 = new Image (display, imageDir + "img01.24.tr.png");
    say ("img01.1.tr-b.gif: transparent (before) = " + img5.getBackground());
    say ("img01.1.tr-f.gif: transparent (before) = " + img6.getBackground());
    say ("img01.24.tr.png:  transparent (before) = " + img7.getBackground());
    {
        Color clr = new Color (display, new RGB (128, 128, 128)); 
        img5.setBackground (clr);
        img6.setBackground (clr);
        img7.setBackground (clr);
        clr.dispose();
    }
    say ("img01.1.tr-b.gif: transparent (after) = " + img5.getBackground());
    say ("img01.1.tr-f.gif: transparent (after) = " + img6.getBackground());
    say ("img01.24.tr.png:  transparent (after) = " + img7.getBackground());
    
    final ArrayList images = new ArrayList ();
    final String[] imgFiles = {
        imageDir + "img01.24.bmp",
        imageDir + "img01.24.jpg",
        imageDir + "img01.24.png",
        imageDir + "img01.8.bmp",
        imageDir + "img01.8.gif",
        imageDir + "img01.8.png",
        imageDir + "img01.gray.bmp",
        imageDir + "img01.gray.gif",
        imageDir + "img01.gray.png",
        imageDir + "img01.4.bmp",
        imageDir + "img01.4.gif",
        imageDir + "img01.4.png",
        imageDir + "img01.1.bmp",
        imageDir + "img01.1.gif",
        imageDir + "img01.1.png"
    };
    for (int i = 0; i < imgFiles.length; i ++) {
        Image img = new Image (display, imgFiles[i]);
        images.add (img);
        String name = imgFiles [i];
        name = "out-" + name.substring (imageDir.length());
        saveImage (img, name);
    }
    
    final Image ico1 = new Image (display, imageDir + "test.ico");
    final Image ico1_copy = new Image (display, ico1.getImageData());
    final Image ico2 = new Image (display, imageDir + "test_bw.ico");
    final Image ico2_copy = new Image (display, ico2.getImageData());
    ImageLoader il = new ImageLoader ();
    ImageData[] iconData = il.load (imageDir + "eclipse.ico");
    final Image[] icons = new Image[iconData.length];
    for (int i = 0; i < iconData.length; i++) {
    	icons[i] = new Image (display, iconData[i]);
    }

    final Image img5_copy = new Image (display, img5, SWT.IMAGE_COPY); 
    final Image img6_copy = new Image (
        display, (Image) images.get (4), SWT.IMAGE_COPY); 
    final Image img7_copy = new Image (display, img7, SWT.IMAGE_COPY); 
    final Image img5_dis = new Image (display, img5, SWT.IMAGE_DISABLE); 
    final Image img6_dis = new Image (
        display, (Image) images.get (4), SWT.IMAGE_DISABLE); 
    final Image img7_dis = new Image (display, img7, SWT.IMAGE_DISABLE); 
    final Image img5_gray = new Image (display, img5, SWT.IMAGE_GRAY); 
    final Image img6_gray = new Image (
        display, (Image) images.get (4), SWT.IMAGE_GRAY); 
    final Image img7_gray = new Image (display, img7, SWT.IMAGE_GRAY); 

    final Image ico1_copy2 = new Image (display, ico1, SWT.IMAGE_COPY); 
    final Image ico2_copy2 = new Image (display, ico2, SWT.IMAGE_COPY); 
    final Image ico3_copy2 = new Image (
        display, icons [icons.length - 3], SWT.IMAGE_COPY); 
    final Image ico1_dis = new Image (display, ico1, SWT.IMAGE_DISABLE); 
    final Image ico2_dis = new Image (display, ico2, SWT.IMAGE_DISABLE); 
    final Image ico3_dis = new Image (
        display, icons [icons.length - 3], SWT.IMAGE_DISABLE); 
    final Image ico1_gray = new Image (display, ico1, SWT.IMAGE_GRAY); 
    final Image ico2_gray = new Image (display, ico2, SWT.IMAGE_GRAY); 
    final Image ico3_gray = new Image (
        display, icons [icons.length - 3], SWT.IMAGE_GRAY); 

    final Image ico1_indirect;
    final Image ico2_indirect;
    {
        ImageData data = new ImageData (
            48, 48, 1, new PaletteData (new RGB[] {
                new RGB (0, 0, 0), new RGB (255, 255, 255)
            })
        ); 
        Image mask = new Image (display, data);
        GC gc = new GC (mask);
        gc.setForeground(gc.getBackground());
        gc.fillOval (6, 5, 40, 40);
        gc.dispose ();
        data = img1.getImageData();
        ico1_indirect = new Image (display, data, mask.getImageData());
        data = img1_alpha2.getImageData();
        ico2_indirect = new Image (display, data, mask.getImageData());
        mask.dispose();
    }
        
    shell.addDisposeListener (new DisposeListener () {
        public void widgetDisposed (DisposeEvent e) {
            if (background != null) background.dispose();
            img1.dispose();
            img1_copy.dispose();
            img2.dispose();
            img2_copy.dispose();
            img3.dispose();
            img3_copy.dispose();
            img4.dispose();
            img4_copy.dispose();
            img1_alpha.dispose();
            img1_alpha2.dispose();
            img5.dispose();
            img6.dispose();
            img7.dispose();
            ico1.dispose();
            ico2.dispose();
            ico1_copy.dispose();
            ico2_copy.dispose();
            Iterator it = images.iterator();
            while (it.hasNext ()) ((Image)it.next()).dispose();
            for (int i = 0; i < icons.length; i++) icons [i].dispose();
            img5_copy.dispose();
            img6_copy.dispose();
            img7_copy.dispose();
            img5_dis.dispose();
            img6_dis.dispose();
            img7_dis.dispose();
            img5_gray.dispose();
            img6_gray.dispose();
            img7_gray.dispose();
            ico1_copy2.dispose();
            ico2_copy2.dispose();
            ico3_copy2.dispose();
            ico1_dis.dispose();
            ico2_dis.dispose();
            ico3_dis.dispose();
            ico1_gray.dispose();
            ico2_gray.dispose();
            ico3_gray.dispose();
            ico1_indirect.dispose();
            ico2_indirect.dispose();
        }
    });

    shell.addPaintListener(new PaintListener () {
        public void paintControl(PaintEvent event) {
            GC gc = event.gc;

            Rectangle r = new Rectangle (event.x, event.y, event.width, event.height);
            gc.setClipping (r);
            drawGrid (gc,
                (r.x / 20) * 20, (r.y / 20) * 20,
                r.width + 20,  r.height + 20, 10);
            
            switch (page) {
                case 0:
                    drawImgPieces (gc, img1, 10, 10, 10, 0, 0, 24, 24);
                    gc.setXORMode (true);
                    drawImgPieces (gc, img1, 10, 80, 10, 0, 0, 24, 24);
                    gc.setXORMode (false);
                    drawImgPieces (gc, img2, 10, 150, 10, 0, 0, 24, 24);
                    gc.setXORMode (true);
                    drawImgPieces (gc, img2, 10, 220, 10, 0, 0, 24, 24);
                    gc.setXORMode (false);
                    drawImgPieces (gc, img3, 10, 290, 10, 0, 0, 24, 24);
                    gc.setXORMode (true);
                    drawImgPieces (gc, img3, 10, 360, 10, 0, 0, 24, 24);
                    gc.setXORMode (false);
                    drawImgPieces (gc, img4, 10, 430, 10, 0, 0, 24, 24);
                    gc.setXORMode (true);
                    drawImgPieces (gc, img4, 10, 500, 10, 0, 0, 24, 24);
                    gc.setXORMode (false);
                    gc.drawImage (img1_copy, 480, 10);
                    gc.drawImage (img2_copy, 480, 150);
                    gc.drawImage (img3_copy, 480, 290);
                    gc.drawImage (img4_copy, 480, 430);
                    break;
                case 1:
                    for (int i = 0; i < images.size (); i++) {
                        Image img = (Image)images.get (i);
                        gc.drawImage (img, 10 + (i % 3) * 260, 10 + (i / 3) * 120);
                    }
                    break;
                case 2:
                    drawImgPieces (gc, img5, 10, 10, 10, 0, 40, 40, 40);
                    drawImgPieces (gc, img6, 10, 130, 10, 0, 40, 40, 40);
                    drawImgPieces (gc, img7, 10, 250, 10, 0, 40, 40, 40);
                    break;
                case 3:
                    drawImgPieces (gc, img1_alpha, 10, 10, 10, 0, 0, 24, 24);
                    drawImgPieces (gc, img1_alpha2, 10, 80, 10, 0, 0, 24, 24);
                    break;
                case 4:
                    gc.fillGradientRectangle (10, 10, 200, 40, false);
                    gc.fillGradientRectangle (210, 60, -200, 40, false);
                    gc.fillGradientRectangle (10, 110, 40, 200, true);
                    gc.fillGradientRectangle (60, 310, 40, -200, true);
                    break;
                case 5:
                    drawImgPieces (gc, ico1, 10, 10, 10, 0, 0, 24, 24);
                    gc.drawImage (ico1_copy, 480, 10);
                    drawImgPieces (gc, ico2, 10, 70, 10, 0, 0, 24, 24);
                    gc.drawImage (ico2_copy, 480, 70);
                    int h = 130;
                    for (int i = 0; i < icons.length; i++) {
                    	Rectangle ib = icons[i].getBounds();
                    	drawImgPieces (gc, icons[i], 10, h, 10, 0, 0,
                    		ib.width / 2, ib.height / 2);
                    	h += 10 + ib.height;
                    }
                    drawImgPieces (gc, ico1_indirect, 10, 400, 10, 0, 0, 24, 24);
                    drawImgPieces (gc, ico2_indirect, 10, 460, 10, 0, 0, 24, 24);
                    break;
                case 6:
                    gc.drawImage (img5_copy, 10, 10);
                    gc.drawImage (img6_copy, 10, 130);
                    gc.drawImage (img7_copy, 10, 250);
                    gc.drawImage (img5_dis, 270, 10);
                    gc.drawImage (img6_dis, 270, 130);
                    gc.drawImage (img7_dis, 270, 250);
                    gc.drawImage (img5_gray, 530, 10);
                    gc.drawImage (img6_gray, 530, 130);
                    gc.drawImage (img7_gray, 530, 250);
                    gc.drawImage (ico1_copy2, 10, 380);
                    gc.drawImage (ico2_copy2, 10, 430);
                    gc.drawImage (ico3_copy2, 10, 480);
                    gc.drawImage (ico1_dis, 270, 380);
                    gc.drawImage (ico2_dis, 270, 430);
                    gc.drawImage (ico3_dis, 270, 480);
                    gc.drawImage (ico1_gray, 530, 380);
                    gc.drawImage (ico2_gray, 530, 430);
                    gc.drawImage (ico3_gray, 530, 480);
                    break;
            }
        }
    });

    next = new Button (shell, SWT.PUSH);
    next.setText ("Next >");
    next.addSelectionListener (new SelectionAdapter () {
        String title;
        public void widgetSelected (SelectionEvent e) {
            ++page;
            if (page <= LAST_PAGE)
                updateTitleAndButton();
            else
                shell.close();
        }
    });
    updateTitleAndButton();
    
    Button checks = new Button (shell, SWT.CHECK);
    checks.setText ("Checkers");
    checks.setSelection (drawCheckers);
    checks.addSelectionListener (new SelectionAdapter () {
        String title;
        public void widgetSelected (SelectionEvent e) {
            drawCheckers = ((Button)e.widget).getSelection ();
            shell.redraw();
        }
    });
    
    FormData fdata = new FormData();
    fdata.right = new FormAttachment (100, -10);
    fdata.bottom = new FormAttachment (100, -10);
    next.setLayoutData (fdata);
    fdata = new FormData();
    fdata.left = new FormAttachment (0, 10);
    fdata.bottom = new FormAttachment (100, -10);
    checks.setLayoutData (fdata);
    FormLayout formLayout = new FormLayout ();
    shell.setLayout (formLayout);
    
//    Rectangle dr = display.getBounds ();
//    Rectangle sr = shell.getBounds ();
    Rectangle dr = display.getClientArea ();
    Rectangle sr = new Rectangle (0, 0, 880, 680);
    sr.x = (dr.width-sr.width)/2;
    sr.y = (dr.height-sr.height)/2;
    shell.setBounds (sr);
//    shell.setMaximized (true);
    return shell;
}

Image drawTwoCircles (Image img, boolean returnInUseCopy) {
    Image img_copy = null;
    Color clr = new Color (display, 255, 0, 0);
    GC gc = new GC (img);
    System.out.println("gc bg (before) = " + gc.getBackground ());
    System.out.println("gc fg (before) = " + gc.getForeground ());
    gc.drawOval (0, 0, 40, 40);
    gc.setForeground (clr);
    System.out.println("gc fg (after) = " + gc.getForeground ());
    gc.drawOval (5, 5, 40, 40);
    if (returnInUseCopy) {
        img_copy = new Image (display, img.getImageData ());
    }
    gc.dispose ();
    clr.dispose ();
    return img_copy;
}

void drawImgPieces (GC gc, Image img,
    int x0, int y0, int gap, int xp, int yp, int wp, int hp) {
    Rectangle bounds = img.getBounds ();
    int w0 = bounds.width;
    int h0 = bounds.height;
    gc.drawImage (img, x0, y0);
    gc.drawFocus (x0 + xp,  y0 + yp, wp, hp);
    int w = w0, h, last = x0;
    for (int i = 0; i < 9; i++) {
        last += w + gap;
        last += (gap - (last % gap));
        int wb = i % 3;
        int hb = i / 3;
        if (wb == 0) w = wp / 2;
        else if (wb == 1) w = wp;
        else w = wp * 2;
        if (hb == 0) h = hp / 2;
        else if (hb == 1) h = hp;
        else h = hp * 2;
        gc.drawImage (img, xp, yp, wp, hp, last, y0, w, h);
    }
}

void drawGrid (GC gc, int x0, int y0, int width, int height, int step) {
    if (drawCheckers) {
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
    } else {
        int i;
            for (i = 0; i <= width/step; i++)
                    gc.drawLine (x0+i*step, y0, x0+i*step, y0+height);
            for (i = 0; i <= height/step; i++)
                    gc.drawLine (x0, y0+i*step, x0+width, y0+i*step);
    }
}

void updateTitleAndButton () {
    if (page <= LAST_PAGE) {
        if (page == LAST_PAGE)
            next.setText ("Finish");
        shell.redraw ();
    }
    shell.setText (getName() + ": " + Title + " [page #" + page + "]");
}

void saveImage (Image image, String baseName) {
    ImageLoader il = new ImageLoader();
    ImageData id = image.getImageData();
    il.data = new ImageData[] {id};
//    String name = baseName + "." + id.depth + ".";
//    if (id.transparentPixel != -1 || id.maskData != null) name += "tr.";
//    if (id.alpha != -1 || id.alphaData != null) name += "alpha.";
//    name += "bmp";
    String name = baseName + ".bmp";
    il.save (name, SWT.IMAGE_BMP);
}

}

