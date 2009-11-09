package org.eclipse.swt.graphics;

/*
 * OS/2 version.
 * Copyright (c) 2002, 2004 EclipseOS2 Team.
 */

/*
 * Copyright (c) 2000, 2002 IBM Corp.  All rights reserved.
 * This file is made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 */

import org.eclipse.swt.internal.*;
import org.eclipse.swt.internal.pm.*;
import org.eclipse.swt.*;

/**
 * Class <code>GC</code> is where all of the drawing capabilities that are 
 * supported by SWT are located. Instances are used to draw on either an 
 * <code>Image</code>, a <code>Control</code>, or directly on a <code>Display</code>.
 * <p>
 * Application code must explicitly invoke the <code>GC.dispose()</code> 
 * method to release the operating system resources managed by each instance
 * when those instances are no longer required. This is <em>particularly</em>
 * important on Windows95 and Windows98 where the operating system has a limited
 * number of device contexts available.
 * </p>
 *
 * @see org.eclipse.swt.events.PaintEvent
 */

public final class GC {
	
    /**
     * the handle to the OS device context
     * (Warning: This field is platform dependent)
     */
    public int handle;

    Drawable drawable;
    GCData data;

    /*
     *  view matrix to do the automatic horizontal flipping
     *  (initially it is the IDENTITY matrix)
     */
    final int[] matrix = {0x00010000, 0, 0, 0, 0x00010000, 0, 0, 0, 1};
    
    /* current font */
    FATTRS hFont;
    FONTMETRICS hFontMetrics;
    
    /* this should match to Widget.Mnemonic */
    static final char Mnemonic = '&';
    
/**
 * Prevents uninitialized instances from being created outside the package.
 */
GC() {
}

/**	 
 * Constructs a new instance of this class which has been
 * configured to draw on the specified drawable. Sets the
 * foreground and background color in the GC to match those
 * in the drawable.
 * <p>
 * You must dispose the graphics context when it is no longer required. 
 * </p>
 * @param drawable the drawable to draw on
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the drawable is null</li>
 *    <li>ERROR_NULL_ARGUMENT - if there is no current device</li>
 *    <li>ERROR_INVALID_ARGUMENT
 *          - if the drawable is an image that is not a bitmap or an icon
 *          - if the drawable is an image or printer that is already selected
 *            into another graphics context</li>
 * </ul>
 * @exception SWTError <ul>
 *    <li>ERROR_NO_HANDLES if a handle could not be obtained for gc creation</li>
 * </ul>
 */

public GC(Drawable drawable) {
	if (drawable == null) SWT.error(SWT.ERROR_NULL_ARGUMENT);
	GCData data = new GCData ();
	data.doInit = true;
	int hps = drawable.internal_new_GC (data);
	Device device = data.device;
	if (device == null) device = Device.getDevice();
	if (device == null) SWT.error(SWT.ERROR_NULL_ARGUMENT);
	data.device = device;
	init (drawable, data, hps);
	if (device.tracking) device.new_Object(this);	
}

/**
 * Copies a rectangular area of the receiver at the specified
 * position into the image, which must be of type <code>SWT.BITMAP</code>.
 *
 * @param x the x coordinate in the receiver of the area to be copied
 * @param y the y coordinate in the receiver of the area to be copied
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the image is null</li>
 *    <li>ERROR_INVALID_ARGUMENT - if the image is not a bitmap or has been disposed</li> 
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
 * </ul>
 */
public void copyArea(Image image, int x, int y) {
	if (handle == 0) SWT.error(SWT.ERROR_GRAPHIC_DISPOSED);
	if (image == null) SWT.error(SWT.ERROR_NULL_ARGUMENT);
	if (image.type != SWT.BITMAP || image.isDisposed()) SWT.error(SWT.ERROR_INVALID_ARGUMENT);

	Rectangle rect = image.getBounds();
    int bmpPS = image.new_compatible_GC (null, data.hdc, rect.width, rect.height);
    OS.GpiSetBitmap (bmpPS, image.handle);

    /* flip the y coordinate if the flipping is in force */
    if (matrix [4] == 0xFFFF0000) {
        y = matrix [7] + 1 - (y + rect.height);
    }
 	/* Copy the bitmap area */
    OS.GpiBitBlt (bmpPS, handle, 4,
        new int[] {
            0, 0, rect.width, rect.height,
            x, y, x + rect.width, y + rect.height
        },
        OS.ROP_SRCCOPY, OS.BBO_IGNORE
    );
    
    image.dispose_compatible_GC (bmpPS, null);
}

/**
 * Copies a rectangular area of the receiver at the source
 * position onto the receiver at the destination position.
 *
 * @param srcX the x coordinate in the receiver of the area to be copied
 * @param srcY the y coordinate in the receiver of the area to be copied
 * @param width the width of the area to copy
 * @param height the height of the area to copy
 * @param destX the x coordinate in the receiver of the area to copy to
 * @param destY the y coordinate in the receiver of the area to copy to
 *
 * @exception SWTException <ul>
 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
 * </ul>
 */
public void copyArea(int srcX, int srcY, int width, int height, int destX, int destY) {
	if (handle == 0) SWT.error(SWT.ERROR_GRAPHIC_DISPOSED);

    /* flip the y coordinates if the matrix is in force */
    if (matrix [4] == 0xFFFF0000) {
        int h = matrix[7] + 1;
        srcY = h - (srcY + height);
        destY = h - (destY + height);
    }
    
	int hwnd = data.hwnd;
	if (hwnd == 0) {
        /* drawable is not a Control */
        OS.GpiBitBlt (handle, handle, 4,
            new int[] {
                destX, destY, destX + width, destY + height,
                srcX, srcY, srcX + width, srcY + height
            },
            OS.ROP_SRCCOPY, OS.BBO_IGNORE
        );
	} else {
        RECTL rclClip = null;
        int[] pHrgn = new int [1];
        OS.GpiSetClipRegion (handle, 0, pHrgn);
        int hrgn = pHrgn [0];
        if (hrgn != 0 && hrgn != OS.HRGN_ERROR) {
            rclClip = new RECTL();
            OS.GpiQueryRegionBox (handle, hrgn, rclClip);
            OS.GpiSetClipRegion (handle, hrgn, pHrgn); 
        }
        RECTL rclScroll = new RECTL();
        rclScroll.xLeft = srcX;
        rclScroll.yBottom = srcY;
        rclScroll.xRight = srcX + width;
        rclScroll.yTop = srcY + height;
        int rc = OS.WinScrollWindow (
            hwnd, destX - srcX, destY - srcY,
            rclScroll, rclClip, 0, null, OS.SW_INVALIDATERGN
        );
	}
}

/**
 * Disposes of the operating system resources associated with
 * the graphics context. Applications must dispose of all GCs
 * which they allocate.
 */
public void dispose() {
	if (handle == 0) return;
	
	Image image = data.image;
	if (image != null) {
        if (image.device.hPalette == 0) image.refreshBitmap ();
        OS.GpiSetBitmap (handle, 0);
        image.memGC = null;
    }
    /*
	 *  Feature in OS/2. If the clip region is changed after WinBeginPaint()
     *  on this hps it will result in screen artefacts when
     *  drawing the background next time. Therefore, we cannot set it to default
     *  among other things in the init() method -- we do it here.
	 */
    setClipping ((Rectangle)null);
    /* Dispose the HPS */
	Device device = data.device;
	drawable.internal_dispose_GC(handle, data);
	drawable = null;
	handle = 0;
	data.image = null;
	data.rcl = null;
	if (device.tracking) device.dispose_Object(this);
	data.device = null;
	data = null;
}

/**
 * Draws the outline of a circular or elliptical arc 
 * within the specified rectangular area.
 * <p>
 * The resulting arc begins at <code>startAngle</code> and extends  
 * for <code>arcAngle</code> degrees, using the current color.
 * Angles are interpreted such that 0 degrees is at the 3 o'clock
 * position. A positive value indicates a counter-clockwise rotation
 * while a negative value indicates a clockwise rotation.
 * </p><p>
 * The center of the arc is the center of the rectangle whose origin 
 * is (<code>x</code>, <code>y</code>) and whose size is specified by the 
 * <code>width</code> and <code>height</code> arguments. 
 * </p><p>
 * The resulting arc covers an area <code>width + 1</code> pixels wide
 * by <code>height + 1</code> pixels tall.
 * </p>
 *
 * @param x the x coordinate of the upper-left corner of the arc to be drawn
 * @param y the y coordinate of the upper-left corner of the arc to be drawn
 * @param width the width of the arc to be drawn
 * @param height the height of the arc to be drawn
 * @param startAngle the beginning angle
 * @param arcAngle the angular extent of the arc, relative to the start angle
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_INVALID_ARGUMENT - if any of the width, height or endAngle is zero.</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
 * </ul>
 */
public void drawArc (int x, int y, int width, int height, int startAngle, int endAngle) {
	if (handle == 0) SWT.error(SWT.ERROR_GRAPHIC_DISPOSED);

    if (width < 0) {
		x += width;
		width = -width;
	}
	if (height < 0) {
		y += height;
		height = -height;
	}
    if (endAngle < 0) {
        startAngle += endAngle;
        endAngle = -endAngle;
    }

    if (width == 0 || height == 0 || endAngle == 0) {
		SWT.error(SWT.ERROR_INVALID_ARGUMENT);
	}

    boolean geomLine = OS.GpiQueryLineWidthGeom (handle) != 1;
    int saved = -1;

    int axr = width / 2;
    int ayr = height / 2;
    int[] arcparams = {axr, -ayr, 0, 0};
    OS.GpiSetArcParams (handle, arcparams);
    int xc, yc;
    xc = x + axr;
    yc = y + ayr;
    int[] pnt = {xc, yc};
    int[] pnt2 = new int[2];
    
    /*
     *  In order to draw arcs those bounding reactangle has even width
     *  and/or height we draw each quarter of the arc separately,
     *  correspondingly moving its center point by one pixel.
     */
    int extX = width % 2;
    int extY = height % 2;
    if (extX != 0 || extY != 0) {
        int sa, ea;
        for (int i = 0; i < 4; i++) {
            sa = i * 90;
            ea = sa + 90;
            if (sa < startAngle) sa = startAngle;
            if (ea > startAngle + endAngle) ea = startAngle + endAngle;
            if (ea <= sa) continue;
            ea -= sa;
            switch (i) {
                case 0: pnt[0] = xc + extX; pnt[1] = yc;        break;
                case 1: pnt[0] = xc;        pnt[1] = yc;        break;
                case 2: pnt[0] = xc;        pnt[1] = yc + extY; break;
                case 3: pnt[0] = xc + extX; pnt[1] = yc + extY;
            }
            if (sa == startAngle) {
                int oldMix = OS.GpiQueryMix (handle);
                OS.GpiSetMix (handle, OS.FM_LEAVEALONE);
                // set the current position to the start of the arc
                OS.GpiPartialArc (handle, pnt, 0x00010000, sa << 16, 0);
                OS.GpiSetMix (handle, oldMix);
                // cause the start point to be drawn (seems like a bug in OS/2)
                OS.GpiQueryCurrentPosition (handle, pnt2);
                OS.GpiMove (handle, pnt2);
            }
            if (geomLine) saved = beginGeomLine();
            OS.GpiPartialArc (handle, pnt, 0x00010000, sa << 16, ea << 16);
            if (geomLine) endGeomLine (saved);
        }
    } else {
        int oldMix = OS.GpiQueryMix (handle);
        OS.GpiSetMix (handle, OS.FM_LEAVEALONE);
        // set the current position to the start of the arc
        OS.GpiPartialArc (handle, pnt, 0x00010000, startAngle << 16, 0);
        OS.GpiSetMix (handle, oldMix);
        // cause the start point to be drawn (seems like a bug in OS/2)
        OS.GpiQueryCurrentPosition (handle, pnt2);
        OS.GpiMove (handle, pnt2);
        if (geomLine) saved = beginGeomLine();
        OS.GpiPartialArc (handle, pnt, 0x00010000,
            startAngle << 16, endAngle << 16);
        if (geomLine) endGeomLine (saved);
    }
}

/** 
 * Draws a rectangle, based on the specified arguments, which has
 * the appearance of the platform's <em>focus rectangle</em> if the
 * platform supports such a notion, and otherwise draws a simple
 * rectangle in the receiver's forground color.
 *
 * @param x the x coordinate of the rectangle
 * @param y the y coordinate of the rectangle
 * @param width the width of the rectangle
 * @param height the height of the rectangle
 *
 * @exception SWTException <ul>
 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
 * </ul>
 *
 * @see #drawRectangle
 */	 
public void drawFocus (int x, int y, int width, int height) {
	if (handle == 0) SWT.error(SWT.ERROR_GRAPHIC_DISPOSED);
    /* emulate Windows functionality */
    if (width < 0 || height < 0) return;
    int[] pnt = {x, y};
    int oldType = OS.GpiQueryLineType (handle);
    int oldMix = OS.GpiQueryMix (handle);
    int oldColor = OS.GpiQueryColor (handle);
    int newColor;
    Color fg = Color.pm_new (data.device, oldColor);
    Color bg = Color.pm_new (data.device, OS.GpiQueryBackColor (handle));
    newColor = fg.rgb ^ bg.rgb;
    int hPal = data.device.hPalette;
    if (hPal != 0) newColor = OS.GpiQueryNearestPaletteIndex (hPal, newColor);
    OS.GpiSetLineType (handle, OS.LINETYPE_ALTERNATE);
    OS.GpiSetMix (handle, OS.FM_XOR);
    OS.GpiSetColor (handle, newColor);
    OS.GpiMove (handle, pnt);
    /*
     *  We decrease the width and height by one for comatibility with Windows
     *  although it contradicts the drawRectangle() logic (i.e. given the same
     *  argumens this method and drawRectangle() will produce rectangles
     *  of different size -- the former will be 1 pixel smaller than the latter).
     */
    pnt[0] += width - 1;
    pnt[1] += height - 1;
    OS.GpiBox (handle, OS.DRO_OUTLINE, pnt, 0, 0);
    OS.GpiSetLineType (handle, oldType);
    OS.GpiSetMix (handle, oldMix);
    OS.GpiSetColor (handle, oldColor);
}

/**
 * Draws the given image in the receiver at the specified
 * coordinates.
 *
 * @param image the image to draw
 * @param x the x coordinate of where to draw
 * @param y the y coordinate of where to draw
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the image is null</li>
 *    <li>ERROR_INVALID_ARGUMENT - if the image has been disposed</li>
 *    <li>ERROR_INVALID_ARGUMENT - if the given coordinates are outside the bounds of the image</li>
 * @exception SWTError <ul>
 *    <li>ERROR_NO_HANDLES - if no handles are available to perform the operation</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
 * </ul>
 */
public void drawImage(Image image, int x, int y) {
	if (handle == 0) SWT.error(SWT.ERROR_GRAPHIC_DISPOSED);
	if (image == null) SWT.error (SWT.ERROR_NULL_ARGUMENT);
	if (image.isDisposed()) SWT.error(SWT.ERROR_INVALID_ARGUMENT);
    drawImage(image, 0, 0, -1, -1, x, y, -1, -1, true);
}

/**
 * Copies a rectangular area from the source image into a (potentially
 * different sized) rectangular area in the receiver. If the source
 * and destination areas are of differing sizes, then the source
 * area will be stretched or shrunk to fit the destination area
 * as it is copied. The copy fails if any part of the source rectangle
 * lies outside the bounds of the source image, or if any of the width
 * or height arguments are negative.
 *
 * @param image the source image
 * @param srcX the x coordinate in the source image to copy from
 * @param srcY the y coordinate in the source image to copy from
 * @param srcWidth the width in pixels to copy from the source
 * @param srcHeight the height in pixels to copy from the source
 * @param destX the x coordinate in the destination to copy to
 * @param destY the y coordinate in the destination to copy to
 * @param destWidth the width in pixels of the destination rectangle
 * @param destHeight the height in pixels of the destination rectangle
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the image is null</li>
 *    <li>ERROR_INVALID_ARGUMENT - if the image has been disposed</li>
 *    <li>ERROR_INVALID_ARGUMENT - if any of the width or height arguments are negative.
 *    <li>ERROR_INVALID_ARGUMENT - if the source rectangle is not contained within the bounds of the source image</li>
 * </ul>
 * @exception SWTError <ul>
 *    <li>ERROR_NO_HANDLES - if no handles are available to perform the operation</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
 * </ul>
 */
public void drawImage(Image image, int srcX, int srcY, int srcWidth, int srcHeight, int destX, int destY, int destWidth, int destHeight) {
	if (handle == 0) SWT.error(SWT.ERROR_GRAPHIC_DISPOSED);
	if (srcWidth == 0 || srcHeight == 0 || destWidth == 0 || destHeight == 0) return;
	if (srcX < 0 || srcY < 0 || srcWidth < 0 || srcHeight < 0 || destWidth < 0 || destHeight < 0) {
		SWT.error (SWT.ERROR_INVALID_ARGUMENT);
	}
	if (image == null) SWT.error (SWT.ERROR_NULL_ARGUMENT);
	if (image.isDisposed()) SWT.error(SWT.ERROR_INVALID_ARGUMENT);
	drawImage(image, srcX, srcY, srcWidth, srcHeight, destX, destY, destWidth, destHeight, false);	
}

void drawImage(Image srcImage, int srcX, int srcY, int srcWidth, int srcHeight, int destX, int destY, int destWidth, int destHeight, boolean simple) {
	switch (srcImage.type) {
		case SWT.BITMAP:
			drawBitmap(srcImage, srcX, srcY, srcWidth, srcHeight, destX, destY, destWidth, destHeight, simple);
			break;
		case SWT.ICON:
			drawIcon(srcImage, srcX, srcY, srcWidth, srcHeight, destX, destY, destWidth, destHeight, simple);
			break;
		default:
			SWT.error(SWT.ERROR_UNSUPPORTED_FORMAT);
	}
}

void drawIcon(Image srcImage, int srcX, int srcY, int srcWidth, int srcHeight, int destX, int destY, int destWidth, int destHeight, boolean simple) {
    int hbmColor = srcImage.handle;
    int hbmPointer = srcImage.maskHandle;
    
    if (hbmPointer == 0) {
        /* Get the icon info */
        POINTERINFO srcInfo = new POINTERINFO ();
        OS.WinQueryPointerInfo (srcImage.handle, srcInfo);
        hbmColor = srcInfo.hbmColor;
        hbmPointer = srcInfo.hbmPointer;
    }

	/* Get the icon width and height */
	int hBitmap = hbmColor;
	if (hBitmap == 0) hBitmap = hbmPointer;
	BITMAPINFOHEADER2 bm = new BITMAPINFOHEADER2();
	OS.GpiQueryBitmapInfoHeader (hBitmap, bm);
	int iconWidth = bm.cx, iconHeight = bm.cy;
	if (hBitmap == hbmPointer) iconHeight /= 2;

    if (srcX + srcWidth > iconWidth || srcY + srcHeight > iconHeight)    
        SWT.error(SWT.ERROR_INVALID_ARGUMENT);
    
	if (simple) {
		srcWidth = destWidth = iconWidth;
		srcHeight = destHeight = iconHeight;
	}

    /* flip the src y coordinate */
    srcY = iconHeight - (srcY + srcHeight);
    /* flip the dest y coordinate if the flipping is in force */
    if (matrix [4] == 0xFFFF0000) {
        destY = matrix [7] + 1 - (destY + destHeight);
    }

	/* Simple case: no stretching, entire icon */
	if (simple && srcImage.maskHandle == 0) {
        OS.WinDrawPointer (handle, destX, destY, srcImage.handle, OS.DP_NORMAL);
		return;
	}

    int oldColor = OS.GpiQueryColor (handle);
    int oldBackColor = OS.GpiQueryBackColor (handle);
    OS.GpiSetDefaultViewMatrix (handle, 0, null, OS.TRANSFORM_REPLACE);

    /* Set colors to WHITE and BLACK for AND and XOR masks to work properly */
    int newColor = 0xFFFFFF;
    int newBackColor = 0x000000;
    int hPalette = data.device.hPalette;
    if (hPalette != 0) {
        // white is the 15th and black is the 0th element of the default
        // palette set in Device.init()
        newColor = 15;
    }
    OS.GpiSetColor (handle, newColor);
    OS.GpiSetBackColor (handle, newBackColor);
    
    /* draw AND mask */
    OS.GpiWCBitBlt (handle, hbmPointer, 4,
        new int[] {
            destX, destY, destX + destWidth - 1, destY + destHeight - 1,
            srcX, srcY + iconHeight, srcX + srcWidth, srcY + iconHeight + srcHeight
        },
        OS.ROP_SRCAND, OS.BBO_IGNORE
    );

    /* draw icon */
    OS.GpiWCBitBlt (handle, hbmColor, 4,
        new int[] {
            destX, destY, destX + destWidth - 1, destY + destHeight - 1,
            srcX, srcY, srcX + srcWidth, srcY + srcHeight
        },
        OS.ROP_SRCINVERT, OS.BBO_IGNORE
    );

    /* draw XOR mask */
    OS.GpiWCBitBlt (handle, hbmPointer, 4,
        new int[] {
            destX, destY, destX + destWidth - 1, destY + destHeight - 1,
            srcX, srcY, srcX + srcWidth, srcY + srcHeight
        },
        OS.ROP_SRCINVERT, OS.BBO_IGNORE
    );

    OS.GpiSetDefaultViewMatrix (handle, 9, matrix, OS.TRANSFORM_REPLACE);
    OS.GpiSetBackColor (handle, oldBackColor);
    OS.GpiSetColor (handle, oldColor);
}

void drawBitmap(Image srcImage, int srcX, int srcY, int srcWidth, int srcHeight, int destX, int destY, int destWidth, int destHeight, boolean simple) {
    BITMAPINFOHEADER2 bm = new BITMAPINFOHEADER2 ();
    OS.GpiQueryBitmapInfoHeader (srcImage.handle, bm);
    int imgWidth = bm.cx;
	int imgHeight = bm.cy;
	if (simple) {
		srcWidth = destWidth = imgWidth;
		srcHeight = destHeight = imgHeight;
	} else {
		if (srcX + srcWidth > imgWidth || srcY + srcHeight > imgHeight) {
			SWT.error (SWT.ERROR_INVALID_ARGUMENT);
		}
		simple = srcX == 0 && srcY == 0 && 
			srcWidth == destWidth && destWidth == imgWidth &&
			srcHeight == destHeight && destHeight == imgHeight;
	}
	boolean mustRestore = false;
	GC memGC = srcImage.memGC;
	if (memGC != null && !memGC.isDisposed()) {
        mustRestore = true;
        if (srcImage.device.hPalette == 0) srcImage.refreshBitmap();
        OS.GpiSetBitmap(memGC.handle, 0);
	}
	if (srcImage.alpha != -1 || srcImage.alphaData != null) {
		drawBitmapAlpha(srcImage, srcX, srcY, srcWidth, srcHeight, destX, destY, destWidth, destHeight, simple, bm, imgWidth, imgHeight);
	} else if (srcImage.transparentPixel != -1) {
		drawBitmapTransparent(srcImage, srcX, srcY, srcWidth, srcHeight, destX, destY, destWidth, destHeight, simple, bm, imgWidth, imgHeight);
	} else {
		drawBitmap(srcImage, srcX, srcY, srcWidth, srcHeight, destX, destY, destWidth, destHeight, simple, bm, imgWidth, imgHeight);
	}
	if (mustRestore) {
		OS.GpiSetBitmap(memGC.handle, srcImage.handle);
	}
}

void drawBitmapAlpha (Image srcImage,
    int srcX, int srcY, int srcWidth, int srcHeight,
    int destX, int destY, int destWidth, int destHeight,
    boolean simple, BITMAPINFOHEADER2 bm, int imgWidth, int imgHeight
) {
	/* Simple cases */
	if (srcImage.alpha == 0) return;
	if (srcImage.alpha == 255) {
		drawBitmap(srcImage,
            srcX, srcY, srcWidth, srcHeight,
            destX, destY, destWidth, destHeight,
            simple, bm, imgWidth, imgHeight);
		return;
	}

	/* Check clipping */
	Rectangle rect = getClipping();
    Rectangle destRect = new Rectangle(destX, destY, destWidth, destHeight);
    rect = rect.intersection(destRect);
	if (rect.isEmpty()) return;

	/* 
	 *  Optimization. Recalculate src and dest rectangles so that
	 *  only the clipping area is drawn. Do it only when the clipped
     *  rectange differs from the original.
	 */
    if (!rect.equals (destRect)) {
        simple = false;
        int sx1 = srcX + (((rect.x - destX) * srcWidth) / destWidth);
        int sx2 = srcX + ((((rect.x + rect.width) - destX) * srcWidth) / destWidth);
        int sy1 = srcY + (((rect.y - destY) * srcHeight) / destHeight);
        int sy2 = srcY + ((((rect.y + rect.height) - destY) * srcHeight) / destHeight);
        destX = rect.x;
        destY = rect.y;
        destWidth = rect.width;
        destHeight = rect.height;
        srcX = sx1;
        srcY = sy1;
        srcWidth = Math.max(1, sx2 - sx1);
        srcHeight = Math.max(1, sy2 - sy1);
    }

    /*
     *  Always 24-bit or 32-bit image.
     *  Use 32-bit when possible to speed up operations.
     */
    int depth = data.device.has32bitDepth ? 32 : 24;

    /* Create resources */
    int destPS = srcImage.new_compatible_GC (null, data.hdc, destWidth, destHeight);    
    int destBitmap = srcImage.createBitmap (
        destWidth, destHeight, depth, 1, null, null, new int[] {destPS}, 0);

    /* Create the BITMAPINFO2 */
    final int bmi_cbFix = 4;
    int[] bmi = new int[bmi_cbFix];
    // cbFix
    bmi[0] = bmi_cbFix * 4;
    // cPlanes, cBitCount
    bmi[3] = depth << 16 | 1;

    int destBytesPerLine = (((depth * destWidth + 31) / 32) * 4);
    int destSizeInBytes = destBytesPerLine * destHeight;

    /* reset the transformation matrix */
    OS.GpiSetDefaultViewMatrix (handle, 0, null, OS.TRANSFORM_REPLACE);

    /* flip the src y coordinate */
    srcY = imgHeight - (srcY + srcHeight);
    /* flip the dest y coordinate if the flipping is in force */
    if (matrix [4] == 0xFFFF0000) {
        destY = matrix [7] + 1 - (destY + destHeight);
    }
    
    /* Get the foreground pixels */
    boolean fixStretch = (srcWidth != destWidth || srcHeight != destHeight);
    if (simple) {
        OS.GpiSetBitmap (destPS, srcImage.handle);
    } else {
        int oldColor = 0;
        int oldPattern = 0;
        int rop = OS.ROP_SRCCOPY;
        if (fixStretch) {
            oldColor = OS.GpiQueryColor (destPS);
            OS.GpiSetColor (destPS, OS.CLR_BLACK);
            oldPattern = OS.GpiQueryPattern (destPS);
            OS.GpiSetPattern (destPS, OS.PATSYM_SOLID);
            rop = 0xFC; // SRC OR PAT
        }
        OS.GpiWCBitBlt (destPS, srcImage.handle, 4,
            new int[] {
                0, 0, destWidth - 1, destHeight - 1,
                srcX, srcY, srcX + srcWidth, srcY + srcHeight},
            rop, OS.BBO_IGNORE);
        if (fixStretch) {
            OS.GpiSetColor (destPS, oldColor);
            OS.GpiSetPattern (destPS, oldPattern);
        }
    }
    byte[] srcData = new byte[destSizeInBytes];
    OS.GpiQueryBitmapBits (destPS, 0, destHeight, srcData, bmi);

	/* Get the background pixels */
    OS.GpiSetBitmap (destPS, destBitmap);
    OS.GpiBitBlt (destPS, handle, 4,
        new int [] {
            0, 0, destWidth, destHeight,
            destX, destY, destX + destWidth, destY + destHeight},
        OS.ROP_SRCCOPY, OS.BBO_IGNORE);
    byte[] destData = new byte[destSizeInBytes];
    OS.GpiQueryBitmapBits (destPS, 0, destHeight, destData, bmi);

	/* Scale the alpha channel */
	byte[] srcAlphaData = null;
    if (srcImage.alpha == -1) {
		srcAlphaData = new byte[destWidth * destHeight];
		ImageData.blit (ImageData.BLIT_SRC,
			srcImage.alphaData, 8, imgWidth, ImageData.MSB_FIRST,
            srcX, imgHeight - (srcY + srcHeight), srcWidth, srcHeight, null, null, null,
			ImageData.ALPHA_OPAQUE, null, 0,
			srcAlphaData, 8, destWidth, ImageData.MSB_FIRST,
            0, 0, destWidth, destHeight, null, null, null,
			false, true);
    }
    
    /* Compose the pixels */
	int alpha = srcImage.alpha;
	final boolean hasAlphaChannel = (alpha == -1);
    final int dpinc = destBytesPerLine - destWidth * (depth >> 3);
    int dp = 0, ap = 0;
	for (int y = 0; y < destHeight; ++y) {
		for (int x = 0; x < destWidth; ++x) {
            if (hasAlphaChannel) alpha = srcAlphaData[ap] & 0xff;
			destData[dp] += ((srcData[dp] & 0xff) - (destData[dp] & 0xff)) * alpha / 255;
			destData[dp + 1] += ((srcData[dp + 1] & 0xff) - (destData[dp + 1] & 0xff)) * alpha / 255;
			destData[dp + 2] += ((srcData[dp + 2] & 0xff) - (destData[dp + 2] & 0xff)) * alpha / 255;
            dp += (depth >> 3);            
            ap ++;
		}
        dp += dpinc;
	}

	/* Draw the composed pixels */
    OS.GpiDrawBits (handle, destData, bmi, 4,
        new int[] {
            destX, destY, destX + destWidth - 1, destY + destHeight - 1,
            0, 0, destWidth, destHeight},
        OS.ROP_SRCCOPY, OS.BBO_IGNORE);

    OS.GpiSetDefaultViewMatrix (handle, 9, matrix, OS.TRANSFORM_REPLACE);
            
	/* Free resources */
    srcImage.dispose_compatible_GC (destPS, null);
    OS.GpiDeleteBitmap (destBitmap);
}

void drawBitmapTransparent (Image srcImage,
    int srcX, int srcY, int srcWidth, int srcHeight,
    int destX, int destY, int destWidth, int destHeight,
    boolean simple, BITMAPINFOHEADER2 bm, int imgWidth, int imgHeight
) {
	/* Check clipping */
	Rectangle rect = getClipping();
    Rectangle destRect = new Rectangle(destX, destY, destWidth, destHeight);
    rect = rect.intersection(destRect);
	if (rect.isEmpty()) return;

	/* 
	 *  Optimization. Recalculate src and dest rectangles so that
	 *  only the clipping area is drawn. Do it only when the clipped
     *  rectange differs from the original.
	 */
    if (!rect.equals (destRect)) {
        simple = false;
        int sx1 = srcX + (((rect.x - destX) * srcWidth) / destWidth);
        int sx2 = srcX + ((((rect.x + rect.width) - destX) * srcWidth) / destWidth);
        int sy1 = srcY + (((rect.y - destY) * srcHeight) / destHeight);
        int sy2 = srcY + ((((rect.y + rect.height) - destY) * srcHeight) / destHeight);
        destX = rect.x;
        destY = rect.y;
        destWidth = rect.width;
        destHeight = rect.height;
        srcX = sx1;
        srcY = sy1;
        srcWidth = Math.max(1, sx2 - sx1);
        srcHeight = Math.max(1, sy2 - sy1);
    }
    
    /* Find the RGB values for the transparent pixel. */
    int transRGB = 0;
    int depth = bm.cBitCount * bm.cPlanes;
    final int bmi_cbFix = 4;
    int[] bmi = null;
    int bmpPS = 0;

    if (depth <= 8) {
        /* Palette-based bitmap */
        int numColors = 1 << depth;
        /* Create the BITMAPINFO2 */
        bmi = new int[bmi_cbFix + numColors];
        // cbFix
        bmi[0] = bmi_cbFix * 4;
        // cPlanes, cBitCount
        bmi[3] = (bm.cBitCount << 16) | bm.cPlanes;

        bmpPS = srcImage.new_compatible_GC (null, data.hdc, imgWidth, imgHeight);
        OS.GpiSetBitmap (bmpPS, srcImage.handle);
        OS.GpiQueryBitmapBits (bmpPS, 0, 0, null, bmi);
        int offset = bmi_cbFix + srcImage.transparentPixel;
        transRGB = bmi [offset];
	} else {
		/* Direct color image */
		int pixel = srcImage.transparentPixel;
        transRGB =
            ((pixel & 0xFF0000) >> 16) | (pixel & 0xFF00) | ((pixel & 0xFF) << 16);
	}

    int transColor = transRGB;
    int hPalette = data.device.hPalette;
    if (hPalette != 0) {
        transColor = OS.GpiQueryNearestPaletteIndex (hPalette, transRGB);
    }
    
    /* prepare for transparency */
    int oldBackColor = OS.GpiQueryBackColor (handle);
    int oldMix = OS.GpiQueryBackMix (handle);
    OS.GpiSetBackColor (handle, transColor);
    OS.GpiSetBackMix (handle, OS.BM_SRCTRANSPARENT);

    /* flip the src y coordinate */
    srcY = imgHeight - (srcY + srcHeight);
    byte[] srcData = null;

    int rop = OS.ROP_SRCCOPY;
    boolean fixStretch = (srcWidth != destWidth || srcHeight != destHeight);
    int oldColor = 0;
    int oldPattern = 0;
    if (fixStretch) {
        /*
         *  Bug in OS/2. When the target PS is the display and the ROP operation
         *  involves the pattern the BM_SRCTRANSPARENT background mix is ignored,
         *  so we cannot apply the workaround used in the drawBitmap() method below.
         *  The solution is to fix the stretch problem by querying the bitmap data,
         *  sacling it manually and drawing with GpiDrawBits(). Note that we cannot
         *  do scaling automatically in GpiDrawBits() because it will also smooth
         *  the image. Actually, when the depth is 24 bit or lower GpiDrawBits()
         *  doesn't do smoothing as opposed to Gpi[WC]BitBlt (at least with SDD
         *  drivers) but we cannot rely on this behavior because it's undocumented
         *  and can be absent in video drivers other than SDD.
         */
        if (data.hwnd != 0) {
            if (bmi == null) {
                int numColors = 0;
                if (depth <= 8) {
                    numColors = 1 << depth;
                } else {
                    /* Use 32-bit when possible to speed up operations */
                    if (data.device.has32bitDepth) {
                        depth = bm.cBitCount = 32;
                        bm.cPlanes = 1;
                    }
                };
                /* Create the BITMAPINFO2 */
                bmi = new int[bmi_cbFix + numColors];
                // cbFix
                bmi[0] = bmi_cbFix * 4;
                // cPlanes, cBitCount
                bmi[3] = (bm.cBitCount << 16) | bm.cPlanes;
                bmpPS = srcImage.new_compatible_GC (null, data.hdc, imgWidth, imgHeight);
                OS.GpiSetBitmap (bmpPS, srcImage.handle);
            }
            int srcBytesPerLine = (((depth * imgWidth + 31) / 32) * 4);
            srcData = new byte[srcBytesPerLine * srcHeight];
            OS.GpiQueryBitmapBits (bmpPS, srcY, srcHeight, srcData, bmi);

            /* Scale the source image to the destination size */
            int newBytesPerLine = (((depth * destWidth + 31) / 32) * 4);
            byte[] newData = new byte[newBytesPerLine * destHeight];
            if (depth > 8) {
                ImageData.blit (ImageData.BLIT_SRC,
                    srcData, depth, srcBytesPerLine, ImageData.MSB_FIRST,
                    srcX, 0, srcWidth, srcHeight, 0, 0, 0,
                    ImageData.ALPHA_OPAQUE, null, 0,
                    newData, depth, newBytesPerLine, ImageData.MSB_FIRST,
                    0, 0, destWidth, destHeight, 0, 0, 0,
                    false, false);
            } else {
                ImageData.blit (ImageData.BLIT_SRC,
                    srcData, depth, srcBytesPerLine, ImageData.MSB_FIRST,
                    srcX, 0, srcWidth, srcHeight, null, null, null,
                    ImageData.ALPHA_OPAQUE, null, 0,
                    newData, depth, newBytesPerLine, ImageData.MSB_FIRST,
                    0, 0, destWidth, destHeight, null, null, null,
                    false, false);
            }
            srcData = newData;
            /* Correct cx and cy values */
            bmi[1] = destWidth;
            bmi[2] = destHeight;
        } else {
            oldColor = OS.GpiQueryColor (handle);
            OS.GpiSetColor (handle, OS.CLR_BLACK);
            oldPattern = OS.GpiQueryPattern (handle);
            OS.GpiSetPattern (handle, OS.PATSYM_SOLID);
            rop = 0xFC; // SRC OR PAT
        }
    }

    if (bmpPS != 0) {
        srcImage.dispose_compatible_GC (bmpPS, null);
    }
    
    /* reset the transformation matrix */
    OS.GpiSetDefaultViewMatrix (handle, 0, null, OS.TRANSFORM_REPLACE);
    
    /* flip the dest y coordinate if the flipping is in force */
    if (matrix [4] == 0xFFFF0000) {
        destY = matrix [7] + 1 - (destY + destHeight);
    }

    if (srcData != null) {
        OS.GpiDrawBits (handle,
            srcData, bmi, 4,
            new int[] {
                destX, destY, destX + destWidth - 1, destY + destHeight - 1,
                0, 0, destWidth, destHeight},
            OS.ROP_SRCCOPY, OS.BBO_IGNORE);
    } else {
        OS.GpiWCBitBlt (handle, srcImage.handle, 4,
            new int[] {
                destX, destY, destX + destWidth - 1, destY + destHeight - 1,
                srcX, srcY, srcX + srcWidth, srcY + srcHeight},
            rop, OS.BBO_IGNORE);
    }

    OS.GpiSetDefaultViewMatrix (handle, 9, matrix, OS.TRANSFORM_REPLACE);
    if (fixStretch && srcData == null) {
        OS.GpiSetColor (handle, oldColor);
        OS.GpiSetPattern (handle, oldPattern);
    }
    OS.GpiSetBackColor (handle, oldBackColor);
    OS.GpiSetBackMix (handle, oldMix);
}

void drawBitmap (Image srcImage,
    int srcX, int srcY, int srcWidth, int srcHeight,
    int destX, int destY, int destWidth, int destHeight,
    boolean simple, BITMAPINFOHEADER2 bm, int imgWidth, int imgHeight
) {
	/* Check clipping */
	Rectangle rect = getClipping();
    Rectangle destRect = new Rectangle(destX, destY, destWidth, destHeight);
    rect = rect.intersection(destRect);
	if (rect.isEmpty()) return;

	/* 
	 *  Optimization. Recalculate src and dest rectangles so that
	 *  only the clipping area is drawn. Do it only when the clipped
     *  rectange differs from the original.
	 */
    if (!rect.equals (destRect)) {
        simple = false;
        int sx1 = srcX + (((rect.x - destX) * srcWidth) / destWidth);
        int sx2 = srcX + ((((rect.x + rect.width) - destX) * srcWidth) / destWidth);
        int sy1 = srcY + (((rect.y - destY) * srcHeight) / destHeight);
        int sy2 = srcY + ((((rect.y + rect.height) - destY) * srcHeight) / destHeight);
        destX = rect.x;
        destY = rect.y;
        destWidth = rect.width;
        destHeight = rect.height;
        srcX = sx1;
        srcY = sy1;
        srcWidth = Math.max(1, sx2 - sx1);
        srcHeight = Math.max(1, sy2 - sy1);
    }

    int rop = OS.GpiQueryMix (handle) == OS.FM_XOR ? OS.ROP_SRCINVERT : OS.ROP_SRCCOPY;
    /*
     *  Feature in OS/2. When the mixing function is ROP_SRCCOPY it smoothens
     *  an image when stretching (i.e. when the target rectangle is bigger than the
     *  source), which is unnecessary and slows down the blitting. Also there is
     *  a bug in OS/2 on some video drivers (for instance, SDD): when the image
     *  is compressed horizontally and stretched vertically OS/2 draws the black
     *  strip on the right side of the image in the target PS. The following
     *  fixes both of these things.
     */
    boolean fixStretch = rop == OS.ROP_SRCCOPY &&
        (srcWidth != destWidth || srcHeight != destHeight);
    int oldColor = 0;
    int oldPattern = 0;
    if (fixStretch) {
        oldColor = OS.GpiQueryColor (handle);
        OS.GpiSetColor (handle, OS.CLR_BLACK);
        oldPattern = OS.GpiQueryPattern (handle);
        OS.GpiSetPattern (handle, OS.PATSYM_SOLID);
        rop = 0xFC; // SRC OR PAT
    }
    /*
     *  Blitting without stretching or compression works faster when there is
     *  no transformation matrix (i.e. the default identity matrix is in force) --
     *  temporarily reset the matrix to default (we cannot use GpiBitBlt instead
     *  of this because it works slower desipte the fact that it doesn't involve
     *  any matrix transformations).
     */
    OS.GpiSetDefaultViewMatrix (handle, 0, null, OS.TRANSFORM_REPLACE);
    
    /* flip the src y coordinate */
    srcY = imgHeight - (srcY + srcHeight);
    /* flip the dest y coordinate if the flipping is in force */
    if (matrix [4] == 0xFFFF0000) {
        destY = matrix [7] + 1 - (destY + destHeight);
    }
    
    OS.GpiWCBitBlt (handle, srcImage.handle, 4,
        new int[] {
            destX, destY, destX + destWidth - 1, destY + destHeight - 1,
            srcX, srcY, srcX + srcWidth, srcY + srcHeight},
        rop, OS.BBO_IGNORE);
            
    OS.GpiSetDefaultViewMatrix (handle, 9, matrix, OS.TRANSFORM_REPLACE);
    if (fixStretch) {
        OS.GpiSetColor (handle, oldColor);
        OS.GpiSetPattern (handle, oldPattern);
    }
}

/** 
 * Draws a line, using the foreground color, between the points 
 * (<code>x1</code>, <code>y1</code>) and (<code>x2</code>, <code>y2</code>).
 *
 * @param x1 the first point's x coordinate
 * @param y1 the first point's y coordinate
 * @param x2 the second point's x coordinate
 * @param y2 the second point's y coordinate
 *
 * @exception SWTException <ul>
 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
 * </ul>
 */
public void drawLine (int x1, int y1, int x2, int y2) {
	if (handle == 0) SWT.error(SWT.ERROR_GRAPHIC_DISPOSED);
    boolean geomLine = OS.GpiQueryLineWidthGeom (handle) != 1;
    int saved = -1;
    if (geomLine) saved = beginGeomLine();
    int[] pnt = {x1, y1};
    OS.GpiMove (handle, pnt);
    pnt[0] = x2; pnt[1] = y2;
    OS.GpiLine (handle, pnt);
    if (geomLine) endGeomLine(saved);
}

/** 
 * Draws the outline of an oval, using the foreground color,
 * within the specified rectangular area.
 * <p>
 * The result is a circle or ellipse that fits within the 
 * rectangle specified by the <code>x</code>, <code>y</code>, 
 * <code>width</code>, and <code>height</code> arguments. 
 * </p><p> 
 * The oval covers an area that is <code>width + 1</code> 
 * pixels wide and <code>height + 1</code> pixels tall.
 * </p>
 *
 * @param x the x coordinate of the upper left corner of the oval to be drawn
 * @param y the y coordinate of the upper left corner of the oval to be drawn
 * @param width the width of the oval to be drawn
 * @param height the height of the oval to be drawn
 *
 * @exception SWTException <ul>
 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
 * </ul>
 */	 
public void drawOval (int x, int y, int width, int height) {
	if (handle == 0) SWT.error(SWT.ERROR_GRAPHIC_DISPOSED);
    // When using GpiFullArc it's not possible to draw ellipses with even
    // width/height, so we use drawArc which is capable of doing it. 
    drawArc (x, y, width, height, 0, 360);
}

/** 
 * Draws the closed polygon which is defined by the specified array
 * of integer coordinates, using the receiver's foreground color. The array 
 * contains alternating x and y values which are considered to represent
 * points which are the vertices of the polygon. Lines are drawn between
 * each consecutive pair, and between the first pair and last pair in the
 * array.
 *
 * @param pointArray an array of alternating x and y values which are the vertices of the polygon
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT if pointArray is null</li>
 * </ul>	
 * @exception SWTException <ul>
 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
 * </ul>
 */
public void drawPolygon(int[] pointArray) {
	if (handle == 0) SWT.error(SWT.ERROR_GRAPHIC_DISPOSED);
	if (pointArray == null) SWT.error(SWT.ERROR_NULL_ARGUMENT);
    int len = pointArray.length;
    if (len < 2) return;
    boolean geomLine = OS.GpiQueryLineWidthGeom (handle) != 1;
    int saved = -1;
    if (geomLine) saved = beginGeomLine();
    int[] pnt = new int [] {pointArray[len-2], pointArray[len-1]};
    OS.GpiMove (handle, pnt);
    OS.GpiPolyLine (handle, len / 2, pointArray);
    if (geomLine) endGeomLine(saved);
}

/** 
 * Draws the polyline which is defined by the specified array
 * of integer coordinates, using the receiver's foreground color. The array 
 * contains alternating x and y values which are considered to represent
 * points which are the corners of the polyline. Lines are drawn between
 * each consecutive pair, but not between the first pair and last pair in
 * the array.
 *
 * @param pointArray an array of alternating x and y values which are the corners of the polyline
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the point array is null</li>
 * </ul>	
 * @exception SWTException <ul>
 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
 * </ul>
 */
public void drawPolyline(int[] pointArray) {
	if (handle == 0) SWT.error(SWT.ERROR_GRAPHIC_DISPOSED);
	if (pointArray == null) SWT.error(SWT.ERROR_NULL_ARGUMENT);
    boolean geomLine = OS.GpiQueryLineWidthGeom (handle) != 1;
    int saved = -1;
    if (geomLine) saved = beginGeomLine();
    OS.GpiMove (handle, pointArray);
    OS.GpiPolyLine (handle, pointArray.length / 2, pointArray);
    if (geomLine) endGeomLine (saved);
}

/** 
 * Draws the outline of the rectangle specified by the arguments,
 * using the receiver's foreground color. The left and right edges
 * of the rectangle are at <code>x</code> and <code>x + width</code>. 
 * The top and bottom edges are at <code>y</code> and <code>y + height</code>. 
 *
 * @param x the x coordinate of the rectangle to be drawn
 * @param y the y coordinate of the rectangle to be drawn
 * @param width the width of the rectangle to be drawn
 * @param height the height of the rectangle to be drawn
 *
 * @exception SWTException <ul>
 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
 * </ul>
 */
public void drawRectangle (int x, int y, int width, int height) {
    drawRoundRectangle (x, y, width, height, 0, 0);
}

/** 
 * Draws the outline of the specified rectangle, using the receiver's
 * foreground color. The left and right edges of the rectangle are at
 * <code>rect.x</code> and <code>rect.x + rect.width</code>. The top 
 * and bottom edges are at <code>rect.y</code> and 
 * <code>rect.y + rect.height</code>. 
 *
 * @param rect the rectangle to draw
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the rectangle is null</li>
 * </ul>	
 * @exception SWTException <ul>
 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
 * </ul>
 */
public void drawRectangle (Rectangle rect) {
	if (rect == null) SWT.error(SWT.ERROR_NULL_ARGUMENT);
	drawRectangle (rect.x, rect.y, rect.width, rect.height);
}

/** 
 * Draws the outline of the round-cornered rectangle specified by 
 * the arguments, using the receiver's foreground color. The left and
 * right edges of the rectangle are at <code>x</code> and <code>x + width</code>. 
 * The top and bottom edges are at <code>y</code> and <code>y + height</code>.
 * The <em>roundness</em> of the corners is specified by the 
 * <code>arcWidth</code> and <code>arcHeight</code> arguments. 
 *
 * @param x the x coordinate of the rectangle to be drawn
 * @param y the y coordinate of the rectangle to be drawn
 * @param width the width of the rectangle to be drawn
 * @param height the height of the rectangle to be drawn
 * @param arcWidth the horizontal diameter of the arc at the four corners
 * @param arcHeight the vertical diameter of the arc at the four corners
 *
 * @exception SWTException <ul>
 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
 * </ul>
 */
public void drawRoundRectangle (int x, int y, int width, int height, int arcWidth, int arcHeight) {
	if (handle == 0) SWT.error(SWT.ERROR_GRAPHIC_DISPOSED);
    boolean geomLine = OS.GpiQueryLineWidthGeom (handle) != 1;
    int saved = -1;
    if (geomLine) saved = beginGeomLine();
    int[] pnt = {x, y};
    OS.GpiMove (handle, pnt);
    pnt[0] += width;
    pnt[1] += height;
    OS.GpiBox (handle, OS.DRO_OUTLINE, pnt, arcWidth, arcHeight);
    if (geomLine) endGeomLine(saved);
}

/** 
 * Draws the given string, using the receiver's current font and
 * foreground color. No tab expansion or carriage return processing
 * will be performed. The background of the rectangular area where
 * the string is being drawn will be filled with the receiver's
 * background color.
 *
 * @param string the string to be drawn
 * @param x the x coordinate of the top left corner of the rectangular area where the string is to be drawn
 * @param y the y coordinate of the top left corner of the rectangular area where the string is to be drawn
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the string is null</li>
 * </ul>	
 * @exception SWTException <ul>
 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
 * </ul>
 */
public void drawString (String string, int x, int y) {
    drawString (string, x, y, false);
}

/** 
 * Draws the given string, using the receiver's current font and
 * foreground color. No tab expansion or carriage return processing
 * will be performed. If <code>isTransparent</code> is <code>true</code>,
 * then the background of the rectangular area where the string is being
 * drawn will not be modified, otherwise it will be filled with the
 * receiver's background color.
 *
 * @param string the string to be drawn
 * @param x the x coordinate of the top left corner of the rectangular area where the string is to be drawn
 * @param y the y coordinate of the top left corner of the rectangular area where the string is to be drawn
 * @param isTransparent if <code>true</code> the background will be transparent, otherwise it will be opaque
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the string is null</li>
 * </ul>	
 * @exception SWTException <ul>
 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
 * </ul>
 */
public void drawString (String string, int x, int y, boolean isTransparent) {
	if (handle == 0) SWT.error(SWT.ERROR_GRAPHIC_DISPOSED);
	if (string == null) SWT.error(SWT.ERROR_NULL_ARGUMENT);
    int length = string.length(); 
    if (length == 0) return;

//@@TODO (dmik): Unicode     
//    char[] text = new char [length];
//    string.getChars (0, length, text, 0);
    byte[] text = string.getBytes();
    int[] pnts = new int [OS.TXTBOX_COUNT * 2];
    int[] pnt = new int [2];
    /*
     *  Feature in OS/2. Temporarily disable the transform matrix and do
     *  manual flipping, otherwise we get flipped font letters.
     */
    OS.GpiSetDefaultViewMatrix (handle, 0, null, OS.TRANSFORM_REPLACE);
    if (matrix [4] == 0xFFFF0000) {
        y = matrix [7] - y;
    }
    /*
     *  Feature in OS/2. The maximum length of the string in string-related
     *  GPI functions is 512 chars. Do the cycle to handle larger strings.
     */
    int backX = x;
    int is = 0;
    while (is < length) {
//@@TODO (dmik): Unicode     
//        int ie = is + 256;
        int ie = is + 512;
        if (ie > length) ie = length;
        if (is != 0) {
            System.arraycopy (text, is, text, 0, ie - is);  
        }
//@@TODO (dmik): Unicode     
//        OS.GpiQueryTextBox (handle, (ie - is) << 1, text, OS.TXTBOX_COUNT, pnts);
        OS.GpiQueryTextBox (handle, ie - is, text, OS.TXTBOX_COUNT, pnts);
        /*
         *  Bug in OS/2. The string background it draws can mismatch the actual
         *  bounding rectangle in some cases and also can leave unpainted areas
         *  at the beginning and at the end of the string. The solution is to
         *  paint the background ourselves.
         */
        if (!isTransparent) {
            int maxX = pnts [4];
            if (maxX < pnts [8]) maxX = pnts [8];
            pnt [0] = backX;
            /*
             *  Bug in OS/2. The height of the string calculated from points
             *  returned by GpiQueryTextBox() is one pixel larger than the
             *  actual font height (lMaxBaselineExt) for some TTF fonts.
             *  So, use lMaxBaselineExt from font metrics instead.
             */
//            pnt [1] = y - (pnts [1] - pnts [3]) + 1;
            pnt [1] = y - hFontMetrics.lMaxBaselineExt + 1;
            OS.GpiMove (handle, pnt);
            backX = x + maxX;
            pnt [0] = backX - 1; pnt [1] = y;
            OS.GpiBox (handle, OS.DRO_FILL, pnt, 0, 0);
        }
        int oldBackMix = OS.GpiQueryBackMix (handle);
        OS.GpiSetBackMix (handle, OS.BM_LEAVEALONE);
        pnt [0] = x; pnt [1] = y - (hFontMetrics.lMaxAscender - 1);
//@@TODO (dmik): Unicode     
//        OS.GpiCharStringAt (handle, pnt, (ie - is) << 1, text);
        OS.GpiCharStringAt (handle, pnt, ie - is, text);
        OS.GpiSetBackMix (handle, oldBackMix);
        x += pnts [8];
        is = ie;
    }
    OS.GpiSetDefaultViewMatrix (handle, 9, matrix, OS.TRANSFORM_REPLACE);
}

Point drawTabbedString (
//@@TODO (dmik): Unicode     
//    char[] text, int start, int end, int x, int y,
    byte[] text, int start, int end, int x, int y,
    int flags, boolean simulate
) {
    int[] pnts = new int [OS.TXTBOX_COUNT * 2];
    int[] pnt = new int [2];
    /*
     *  Bug in OS/2. The height of the string calculated from points
     *  returned by GpiQueryTextBox() (i.e. pnts [1] - pnts [3]) is one pixel
     *  larger than the actual font height (lMaxBaselineExt) for some TTF
     *  fonts. So, use lMaxBaselineExt from font metrics instead.
     */
    int height = hFontMetrics.lMaxBaselineExt;
    int py = y - (hFontMetrics.lMaxAscender - 1);
    int px = x, backX = x;
    int mnemX = -1;
//@@TODO (dmik): Unicode     
//    char mnemChar = 0;
    byte mnemChar = 0;

    int is = start;
    while (is <= end) {
        int ie = is;
        while (
            ie < end &&
            ((flags & SWT.DRAW_TAB) == 0 || text [ie] != '\t') &&
            ((flags & SWT.DRAW_MNEMONIC) == 0 || text [ie] != Mnemonic) &&
            // feature in OS/2: max string length is 512 bytes
//@@TODO (dmik): Unicode     
//            ie < is + 256
            ie < is + 512
        )   ie ++;
        if (is != 0) {
            System.arraycopy (text, is, text, 0, ie - is);  
        }
        if (ie > is) {
//@@TODO (dmik): Unicode     
//            OS.GpiQueryTextBox (handle, (ie - is) << 1, text, OS.TXTBOX_COUNT, pnts);
            OS.GpiQueryTextBox (handle, ie - is, text, OS.TXTBOX_COUNT, pnts);
            int maxX = pnts [4];
            if (maxX < pnts [8]) maxX = pnts [8];
            if (!simulate) {
                /*
                 *  Bug in OS/2. The string background it draws can mismatch the actual
                 *  bounding rectangle in some cases and also can leave unpainted areas
                 *  at the beginning and at the end of the string. The solution is to
                 *  paint the background ourselves.
                 */
                if ((flags & SWT.DRAW_TRANSPARENT) == 0) {
                    OS.GpiSetBackMix (handle, OS.BM_OVERPAINT);
                    pnt [0] = backX; pnt [1] = y - height + 1;
                    OS.GpiMove (handle, pnt);
                    pnt [0] = px + maxX - 1; pnt [1] = y;
                    OS.GpiBox (handle, OS.DRO_FILL, pnt, 0, 0);
                    OS.GpiSetBackMix (handle, OS.BM_LEAVEALONE);
                }
                pnt [0] = px; pnt [1] = py;
//@@TODO (dmik): Unicode     
//                OS.GpiCharStringAt (handle, pnt, (ie - is) << 1, text);
                OS.GpiCharStringAt (handle, pnt, ie - is, text);
            }
            backX = px + maxX;
            px += pnts [8];
        }
        if (ie < end) {
            if ((flags & SWT.DRAW_TAB) != 0 && text [ie] == '\t') {
                int tabWidth = hFontMetrics.lAveCharWidth * 8;
                px = x + ((px - x) / tabWidth + 1) * tabWidth;
                backX = px;
            }
            if ((flags & SWT.DRAW_MNEMONIC) != 0 && text [ie] == Mnemonic) {
                mnemX = px;
                mnemChar = text [ie + 1];
            }
//@@TODO (dmik): Unicode     
//            if (ie == is + 512) ie --;
            if (ie == is + 256) ie --;
        }
        is = ie + 1;
    }
    if (!simulate && mnemX >= 0 && mnemChar != '\t') {
        /*
         *  Bug in OS/2. The GHS_UNDERSCORE option in GpiCharStringPosAt()
         *  does not work with bitmap fonts -- it is simply ignored. So we
         *  don't use this function but draw underscores ourselves (it seems
         *  that WinDraw[Tabbed]Text() does the same, but we don't use it
         *  because it doesn't handle line delimiters and its tabulation
         *  support is not documented). 
         */
//@@TODO (dmik): Unicode    
//        OS.GpiQueryTextBox (handle, 2, new char[] {mnemChar}, OS.TXTBOX_COUNT, pnts);
        OS.GpiQueryTextBox (handle, 1, new byte[] {mnemChar}, OS.TXTBOX_COUNT, pnts);
        int backColor = OS.GpiQueryBackColor (handle);
        OS.GpiSetBackColor (handle, OS.GpiQueryColor (handle));
        OS.GpiSetBackMix (handle, OS.BM_OVERPAINT);
        pnt [0] = mnemX;
        pnt [1] = py - hFontMetrics.lUnderscorePosition - 1; 
        OS.GpiMove (handle, pnt);
        pnt [0] = mnemX + pnts [8] - 1;
        pnt [1] += hFontMetrics.lUnderscoreSize - 1; 
        OS.GpiBox (handle, OS.DRO_FILL, pnt, 0, 0);
        OS.GpiSetBackMix (handle, OS.BM_LEAVEALONE);
        OS.GpiSetBackColor (handle, backColor);
    }
    return new Point (backX - x, height);
}

Point drawTabbedText (String string, int x, int y, int flags, boolean simulate) {
    int length = string.length();
//@@TODO (dmik): Unicode     
//    char[] text = new char [length]; 
//    string.getChars (0, length, text, 0);
    byte[] text = string.getBytes(); 
    /*
     *  Feature in OS/2. Temporarily disable the transform matrix and do
     *  manual flipping, otherwise we get flipped font letters.
     */
    OS.GpiSetDefaultViewMatrix (handle, 0, null, OS.TRANSFORM_REPLACE);
    if (matrix [4] == 0xFFFF0000) {
        y = matrix [7] - y;
    }
    int oldBackMix = OS.GpiQueryBackMix (handle);
    OS.GpiSetBackMix (handle, OS.BM_LEAVEALONE);

    Point pnt;
    int is = 0, maxWidth = 0, totHeight = 0;
    if ((flags & SWT.DRAW_DELIMITER) != 0) {
        while (is <= length) {
            int ie = is; 
            while (ie < length && text [ie] != '\n' && text [ie] != '\r') ie ++; 
            pnt = drawTabbedString (text, is, ie, x, y, flags, simulate);
            if (simulate) {
                totHeight += pnt.y;
                if (pnt.x > maxWidth) maxWidth = pnt.x;
            } else {
                y -= pnt.y;
            }
            if (ie < length - 1) {
                if (text [ie] == '\n') {
                    if (text [ie + 1] == '\r') ie ++;
                } else {
                    if (text [ie + 1] == '\n') ie ++;
                }
            }
            ie ++;
            is = ie;
        }
    } else {
        pnt = drawTabbedString (text, 0, length, x, y, flags, simulate);
        maxWidth = pnt.x;
        totHeight = pnt.y;
    }
    
    OS.GpiSetBackMix (handle, oldBackMix);
    OS.GpiSetDefaultViewMatrix (handle, 9, matrix, OS.TRANSFORM_REPLACE);
    if (simulate) return new Point (maxWidth, totHeight);
    return null;
}

/** 
 * Draws the given string, using the receiver's current font and
 * foreground color. Tab expansion and carriage return processing
 * are performed. The background of the rectangular area where
 * the text is being drawn will be filled with the receiver's
 * background color.
 *
 * @param string the string to be drawn
 * @param x the x coordinate of the top left corner of the rectangular area where the text is to be drawn
 * @param y the y coordinate of the top left corner of the rectangular area where the text is to be drawn
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the string is null</li>
 * </ul>	
 * @exception SWTException <ul>
 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
 * </ul>
 */
public void drawText (String string, int x, int y) {
	drawText(string, x, y, SWT.DRAW_DELIMITER | SWT.DRAW_TAB);
}

/** 
 * Draws the given string, using the receiver's current font and
 * foreground color. Tab expansion and carriage return processing
 * are performed. If <code>isTransparent</code> is <code>true</code>,
 * then the background of the rectangular area where the text is being
 * drawn will not be modified, otherwise it will be filled with the
 * receiver's background color.
 *
 * @param string the string to be drawn
 * @param x the x coordinate of the top left corner of the rectangular area where the text is to be drawn
 * @param y the y coordinate of the top left corner of the rectangular area where the text is to be drawn
 * @param isTransparent if <code>true</code> the background will be transparent, otherwise it will be opaque
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the string is null</li>
 * </ul>	
 * @exception SWTException <ul>
 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
 * </ul>
 */
public void drawText (String string, int x, int y, boolean isTransparent) {
	int flags = SWT.DRAW_DELIMITER | SWT.DRAW_TAB;
	if (isTransparent) flags |= SWT.DRAW_TRANSPARENT;
	drawText(string, x, y, flags);
}

/** 
 * Draws the given string, using the receiver's current font and
 * foreground color. Tab expansion, line delimiter and mnemonic
 * processing are performed according to the specified flags. If
 * <code>flags</code> includes <code>DRAW_TRANSPARENT</code>,
 * then the background of the rectangular area where the text is being
 * drawn will not be modified, otherwise it will be filled with the
 * receiver's background color.
 * <p>
 * The parameter <code>flags</code> may be a combination of:
 * <dl>
 * <dt><b>DRAW_DELIMITER</b></dt>
 * <dd>draw multiple lines</dd>
 * <dt><b>DRAW_TAB</b></dt>
 * <dd>expand tabs</dd>
 * <dt><b>DRAW_MNEMONIC</b></dt>
 * <dd>underline the mnemonic character</dd>
 * <dt><b>DRAW_TRANSPARENT</b></dt>
 * <dd>transparent background</dd>
 * </dl>
 * </p>
 *
 * @param string the string to be drawn
 * @param x the x coordinate of the top left corner of the rectangular area where the text is to be drawn
 * @param y the y coordinate of the top left corner of the rectangular area where the text is to be drawn
 * @param flags the flags specifing how to process the text
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the string is null</li>
 * </ul>	
 * @exception SWTException <ul>
 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
 * </ul>
 */
public void drawText (String string, int x, int y, int flags) {
	if (handle == 0) SWT.error(SWT.ERROR_GRAPHIC_DISPOSED);
	if (string == null) SWT.error(SWT.ERROR_NULL_ARGUMENT);
    drawTabbedText (string, x, y, flags, false);
}

/**
 * Compares the argument to the receiver, and returns true
 * if they represent the <em>same</em> object using a class
 * specific comparison.
 *
 * @param object the object to compare with this object
 * @return <code>true</code> if the object is the same as this object and <code>false</code> otherwise
 *
 * @see #hashCode
 */
public boolean equals (Object object) {
	return (object == this) || ((object instanceof GC) && (handle == ((GC)object).handle));
}

/**
 * Fills the interior of a circular or elliptical arc within
 * the specified rectangular area, with the receiver's background
 * color.
 * <p>
 * The resulting arc begins at <code>startAngle</code> and extends  
 * for <code>arcAngle</code> degrees, using the current color.
 * Angles are interpreted such that 0 degrees is at the 3 o'clock
 * position. A positive value indicates a counter-clockwise rotation
 * while a negative value indicates a clockwise rotation.
 * </p><p>
 * The center of the arc is the center of the rectangle whose origin 
 * is (<code>x</code>, <code>y</code>) and whose size is specified by the 
 * <code>width</code> and <code>height</code> arguments. 
 * </p><p>
 * The resulting arc covers an area <code>width + 1</code> pixels wide
 * by <code>height + 1</code> pixels tall.
 * </p>
 *
 * @param x the x coordinate of the upper-left corner of the arc to be filled
 * @param y the y coordinate of the upper-left corner of the arc to be filled
 * @param width the width of the arc to be filled
 * @param height the height of the arc to be filled
 * @param startAngle the beginning angle
 * @param arcAngle the angular extent of the arc, relative to the start angle
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_INVALID_ARGUMENT - if any of the width, height or endAngle is zero.</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
 * </ul>
 *
 * @see #drawArc
 */
public void fillArc (int x, int y, int width, int height, int startAngle, int endAngle) {
	if (handle == 0) SWT.error(SWT.ERROR_GRAPHIC_DISPOSED);
		
	if (width < 0) {
		x += width;
		width = -width;
	}
	if (height < 0) {
		y += height;
		height = -height;
	}
    if (endAngle < 0) {
        startAngle += endAngle;
        endAngle = -endAngle;
    }
	
	if (width == 0 || height == 0 || endAngle == 0) {
		SWT.error (SWT.ERROR_INVALID_ARGUMENT);
	}

    // for compatibility with Windows version
    width --;
    height --;
    
    int axr = width / 2;
    int ayr = height / 2;
    int[] arcparams = {axr, -ayr, 0, 0};
    OS.GpiSetArcParams (handle, arcparams);
    int xc, yc;
    xc = x + axr;
    yc = y + ayr;
    int[] pnt = {xc, yc};
    
    OS.GpiBeginArea (handle, 0);

    /*
     *  In order to draw arcs whose bounding reactangle has even width
     *  and/or height we draw each quarter of such arc separately, placing 
     *  the quarter's center to each of four pixels nearest to the geometric
     *  center of the arc.
     */
    int extX = width % 2;
    int extY = height % 2;
    if (extX != 0 || extY != 0) {
        int sa, ea;
        for (int i = 0; i < 4; i++) {
            sa = i * 90;
            ea = sa + 90;
            if (sa < startAngle) sa = startAngle;
            if (ea > startAngle + endAngle) ea = startAngle + endAngle;
            if (ea <= sa) continue;
            ea -= sa;
            switch (i) {
                case 0: pnt[0] = xc + extX; pnt[1] = yc;        break;
                case 1: pnt[0] = xc;        pnt[1] = yc;        break;
                case 2: pnt[0] = xc;        pnt[1] = yc + extY; break;
                case 3: pnt[0] = xc + extX; pnt[1] = yc + extY;
            }
            if (sa == startAngle) OS.GpiMove (handle, pnt);
            OS.GpiPartialArc (handle, pnt, 0x00010000, sa << 16, ea << 16);
        }
    } else {
        OS.GpiMove (handle, pnt);
        OS.GpiPartialArc (handle, pnt, 0x00010000,
            startAngle << 16, endAngle << 16);
    }

    OS.GpiEndArea (handle);
}

/**
 * Fills the interior of the specified rectangle with a gradient
 * sweeping from left to right or top to bottom progressing
 * from the receiver's foreground color to its background color.
 *
 * @param x the x coordinate of the rectangle to be filled
 * @param y the y coordinate of the rectangle to be filled
 * @param width the width of the rectangle to be filled, may be negative
 *        (inverts direction of gradient if horizontal)
 * @param height the height of the rectangle to be filled, may be negative
 *        (inverts direction of gradient if vertical)
 * @param vertical if true sweeps from top to bottom, else 
 *        sweeps from left to right
 *
 * @exception SWTException <ul>
 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
 * </ul>
 *
 * @see #drawRectangle
 */
public void fillGradientRectangle(int x, int y, int width, int height, boolean vertical) {
	if (handle == 0) SWT.error(SWT.ERROR_GRAPHIC_DISPOSED);
	if (width == 0 || height == 0) return;
	int fromColor = Color.pm_new (data.device, OS.GpiQueryColor (handle)).rgb;
	int toColor = Color.pm_new (data.device, OS.GpiQueryBackColor (handle)).rgb;
	boolean swapColors = false;
	if (width < 0) {
		x += width; width = -width;
		if (! vertical) swapColors = true;
	}
	if (height < 0) {
		y += height; height = -height;
		if (vertical) swapColors = true;
	}
	if (swapColors) {
		final int t = fromColor;
		fromColor = toColor;
		toColor = t;
	}
	if (fromColor == toColor) {
        drawRoundRectangle (x, y, width, height, 0, 0);
		return;
	}
	final RGB fromRGB = new RGB ((fromColor >>> 16) & 0xff, (fromColor >>> 8) & 0xff, fromColor & 0xff);
	final RGB toRGB = new RGB ((toColor >>> 16) & 0xff, (toColor >>> 8) & 0xff, toColor & 0xff);

	int colorInfo[] = new int[2];
    OS.DevQueryCaps (data.hdc, OS.CAPS_COLOR_PLANES, 2, colorInfo);
    final int depth = colorInfo[0] * colorInfo[1];
	final int bitResolution = (depth >= 24) ? 8 : (depth >= 15) ? 5 : 0;
	ImageData.fillGradientRectangle(this, data.device,
		x, y, width, height, vertical, fromRGB, toRGB,
		bitResolution, bitResolution, bitResolution);
}

/** 
 * Fills the interior of an oval, within the specified
 * rectangular area, with the receiver's background
 * color.
 *
 * @param x the x coordinate of the upper left corner of the oval to be filled
 * @param y the y coordinate of the upper left corner of the oval to be filled
 * @param width the width of the oval to be filled
 * @param height the height of the oval to be filled
 *
 * @exception SWTException <ul>
 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
 * </ul>
 *
 * @see #drawOval
 */	 
public void fillOval (int x, int y, int width, int height) {
	if (handle == 0) SWT.error(SWT.ERROR_GRAPHIC_DISPOSED);
    /*
     *  Feature in OS/2. Using GpiFullArc it's not possible to draw
     *  ellipses with even width/height, so we use fillArc which is capable
     *  of doing it.
     */
    fillArc (x, y, width, height, 0, 360);
}

/** 
 * Fills the interior of the closed polygon which is defined by the
 * specified array of integer coordinates, using the receiver's
 * background color. The array contains alternating x and y values
 * which are considered to represent points which are the vertices of
 * the polygon. Lines are drawn between each consecutive pair, and
 * between the first pair and last pair in the array.
 *
 * @param pointArray an array of alternating x and y values which are the vertices of the polygon
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT if pointArray is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
 * </ul>
 *
 * @see #drawPolygon	
 */
public void fillPolygon(int[] pointArray) {
	if (handle == 0) SWT.error(SWT.ERROR_GRAPHIC_DISPOSED);
    OS.GpiBeginArea (handle, 0);
    OS.GpiMove (handle, pointArray);
    OS.GpiPolyLine (handle, pointArray.length / 2, pointArray);
    OS.GpiEndArea (handle);
}

/** 
 * Fills the interior of the rectangle specified by the arguments,
 * using the receiver's background color. 
 *
 * @param x the x coordinate of the rectangle to be filled
 * @param y the y coordinate of the rectangle to be filled
 * @param width the width of the rectangle to be filled
 * @param height the height of the rectangle to be filled
 *
 * @exception SWTException <ul>
 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
 * </ul>
 *
 * @see #drawRectangle
 */
public void fillRectangle (int x, int y, int width, int height) {
    fillRoundRectangle (x, y, width, height, 0, 0);
}

/** 
 * Fills the interior of the specified rectangle, using the receiver's
 * background color. 
 *
 * @param rectangle the rectangle to be filled
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the rectangle is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
 * </ul>
 *
 * @see #drawRectangle
 */
public void fillRectangle (Rectangle rect) {
	if (rect == null) SWT.error(SWT.ERROR_NULL_ARGUMENT);
	fillRectangle (rect.x, rect.y, rect.width, rect.height);
}

/** 
 * Fills the interior of the round-cornered rectangle specified by 
 * the arguments, using the receiver's background color. 
 *
 * @param x the x coordinate of the rectangle to be filled
 * @param y the y coordinate of the rectangle to be filled
 * @param width the width of the rectangle to be filled
 * @param height the height of the rectangle to be filled
 * @param arcWidth the horizontal diameter of the arc at the four corners
 * @param arcHeight the vertical diameter of the arc at the four corners
 *
 * @exception SWTException <ul>
 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
 * </ul>
 *
 * @see #drawRoundRectangle
 */
public void fillRoundRectangle (int x, int y, int width, int height, int arcWidth, int arcHeight) {
	if (handle == 0) SWT.error(SWT.ERROR_GRAPHIC_DISPOSED);
    
    if (width < 0) {
		x += width;
		width = -width;
	}
	if (height < 0) {
		y += height;
		height = -height;
	}

    // for compatibility with Windows version
    if (width == 0 || height == 0) return;
    width --;
    height --;

    /*
     *  Bug in OS/2. With some video drivers (for instance, SDD) the round rectangle
     *  drawn is 1 pixel taller then requested, and this extra line sometimes is
     *  not redrawn when the window is invalidated. So instead we use lines and arcs
     *  to build the rectangle when both arcWidth and arcHeight are not zero.
     */
    if (arcWidth == 0 || arcHeight == 0) {
        int[] pnt = {x, y};
        OS.GpiMove (handle, pnt);
        pnt[0] += width;
        pnt[1] += height;
        OS.GpiBox (handle, OS.DRO_FILL, pnt, 0, 0);
        return;
    }

    if (arcWidth < 0) arcWidth = -arcWidth;
    if (arcHeight < 0) arcHeight = -arcHeight;
    if (arcWidth > (width + 1)) arcWidth = width + 1;
    if (arcHeight > (height + 1)) arcHeight = height + 1;
    
    int axr = arcWidth / 2;
    int ayr = arcHeight / 2;
    int x1 = x + width;
    int y1 = y + width;
    if (axr == 0) axr = 1;
    if (ayr == 0) ayr = 1;
    int[] arcparams = {axr, -ayr, 0, 0};
    OS.GpiSetArcParams (handle, arcparams);

    OS.GpiBeginArea (handle, 0);
    int[] pnt = {x1, y + ayr};
    OS.GpiMove (handle, pnt);
    pnt[0] = x1 - axr; pnt[1] = y + ayr;
    OS.GpiPartialArc (handle, pnt, 0x00010000, 0, 90 << 16);
    pnt[0] = x + axr; /* pnt[1] = y + ayr; */
    OS.GpiPartialArc (handle, pnt, 0x00010000, 90 << 16, 90 << 16);
    /* pnt[0] = x + axr; */ pnt[1] = y1 - ayr;
    OS.GpiPartialArc (handle, pnt, 0x00010000, 180 << 16, 90 << 16);
    pnt[0] = x1 - axr; /* pnt[1] = y1 - ayr; */
    OS.GpiPartialArc (handle, pnt, 0x00010000, 270 << 16, 90 << 16);
    OS.GpiEndArea (handle);
}

/**
 * Returns the <em>advance width</em> of the specified character in
 * the font which is currently selected into the receiver.
 * <p>
 * The advance width is defined as the horizontal distance the cursor
 * should move after printing the character in the selected font.
 * </p>
 *
 * @param ch the character to measure
 * @return the distance in the x direction to move past the character before painting the next
 *
 * @exception SWTException <ul>
 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
 * </ul>
 */
public int getAdvanceWidth(char ch) {
	if (handle == 0) SWT.error(SWT.ERROR_GRAPHIC_DISPOSED);
//@@TODO (dmik): Unicode     
//    char[] text = new char[] {ch};
    byte[] text = new String (new char[] {ch}).getBytes();
    int[] pnts = new int [OS.TXTBOX_COUNT * 2];
    /*
     *  Feature in OS/2. Temporarily disable the transform matrix, otherwise we
     *  get the positive height for truetype fonts and negative for all others.
     *  When the matrix is disabled we get positives for all.
     */
    OS.GpiSetDefaultViewMatrix (handle, 0, null, OS.TRANSFORM_REPLACE);
//@@TODO (dmik): Unicode     
//    OS.GpiQueryTextBox (handle, 2, text, OS.TXTBOX_COUNT, pnts);
    OS.GpiQueryTextBox (handle, 1, text, OS.TXTBOX_COUNT, pnts);
    OS.GpiSetDefaultViewMatrix (handle, 9, matrix, OS.TRANSFORM_REPLACE);
    return pnts [8];
}

/** 
 * Returns the background color.
 *
 * @return the receiver's background color
 *
 * @exception SWTException <ul>
 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
 * </ul>
 */
public Color getBackground() {
	if (handle == 0) SWT.error(SWT.ERROR_GRAPHIC_DISPOSED);
	int color = OS.GpiQueryBackColor (handle);
    return Color.pm_new(data.device, color);
}

/**
 * Returns the width of the specified character in the font
 * selected into the receiver. 
 * <p>
 * The width is defined as the space taken up by the actual
 * character, not including the leading and tailing whitespace
 * or overhang.
 * </p>
 *
 * @param ch the character to measure
 * @return the width of the character
 *
 * @exception SWTException <ul>
 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
 * </ul>
 */
public int getCharWidth(char ch) {
	if (handle == 0) SWT.error(SWT.ERROR_GRAPHIC_DISPOSED);
//@@TODO (dmik): Unicode     
//    char[] text = new char[] {ch};
    byte[] text = new String (new char[] {ch}).getBytes();
    int[] pnts = new int [OS.TXTBOX_COUNT * 2];
    /*
     *  Feature in OS/2. Temporarily disable the transform matrix, otherwise we
     *  get the positive height for truetype fonts and negative for all others.
     *  When the matrix is disabled we get positives for all.
     */
    OS.GpiSetDefaultViewMatrix (handle, 0, null, OS.TRANSFORM_REPLACE);
//@@TODO (dmik): Unicode     
//    OS.GpiQueryTextBox (handle, 2, text, OS.TXTBOX_COUNT, pnts);
    OS.GpiQueryTextBox (handle, 1, text, OS.TXTBOX_COUNT, pnts);
    OS.GpiSetDefaultViewMatrix (handle, 9, matrix, OS.TRANSFORM_REPLACE);
    return (pnts [4] - pnts [0]);
}

/** 
 * Returns the bounding rectangle of the receiver's clipping
 * region. If no clipping region is set, the return value
 * will be a rectangle which covers the entire bounds of the
 * object the receiver is drawing on.
 *
 * @return the bounding rectangle of the clipping region
 *
 * @exception SWTException <ul>
 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
 * </ul>
 */
public Rectangle getClipping() {
	if (handle == 0) SWT.error(SWT.ERROR_GRAPHIC_DISPOSED);
	RECTL rcl = new RECTL();
    if (OS.GpiQueryClipBox (handle, rcl) == OS.RGN_NULL) {
        rcl.xLeft = rcl.xRight = rcl.yBottom = rcl.yTop = 0;
    } else {
        rcl.xRight++;
        rcl.yTop++;
    }
    /* transformation matrix is in force -- don't convert the y coordiante */
    return new Rectangle (rcl.xLeft, rcl.yBottom,
        rcl.xRight - rcl.xLeft, rcl.yTop - rcl.yBottom);
}

/** 
 * Sets the region managed by the argument to the current
 * clipping region of the receiver.
 *
 * @param region the region to fill with the clipping region
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the region is null</li>
 * </ul>	
 * @exception SWTException <ul>
 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
 * </ul>
 */
public void getClipping (Region region) {
	if (handle == 0) SWT.error(SWT.ERROR_GRAPHIC_DISPOSED);
	if (region == null) SWT.error (SWT.ERROR_NULL_ARGUMENT);

    int[] pHrgn = new int [1];
    OS.GpiSetClipRegion (handle, 0, pHrgn);
    int hrgn = pHrgn [0];
    if (hrgn != 0 && hrgn != OS.HRGN_ERROR) {
        if (matrix [4] == 0xFFFF0000) {
            /*
             * Feature in OS/2. The default transformation matrix (and others) is
             * not applied to region coordinates when qwerying the current clip
             * region. The solution is to get region rectangles and transform
             * each of them manually.
             */
            RGNRECT rgnctl = new RGNRECT();
            rgnctl.ircStart = 1;
            rgnctl.ulDirection = OS.RECTDIR_LFRT_TOPBOT;
            OS.GpiQueryRegionRects (handle, hrgn, null, rgnctl, null);
            int[] rgnRects = new int [rgnctl.crcReturned * 4];
            rgnctl.crc = rgnctl.crcReturned;
            OS.GpiQueryRegionRects (handle, hrgn, null, rgnctl, rgnRects);
                
            int y, height = matrix [7] + 1, cnt = rgnRects.length / 4;
            for (int i = 0; i < cnt; i++) {
                y = rgnRects [(i << 2) + 1];
                rgnRects [(i << 2) + 1] = height - rgnRects [(i << 2) + 3];
                rgnRects [(i << 2) + 3] = height - y;
            }
            OS.GpiSetRegion (handle, region.handle, cnt, rgnRects);
        } else {
            OS.GpiCombineRegion (handle, region.handle, hrgn, 0, OS.CRGN_COPY);
        }
        OS.GpiSetClipRegion (handle, hrgn, pHrgn); 
        return;
    }
	RECTL rcl = new RECTL();
    int lCount = 0;
    int rcls[] = null;
    if (OS.GpiQueryClipBox (handle, rcl) != OS.RGN_NULL) {
        lCount = 1;
        rcls = new int[] {rcl.xLeft, rcl.yBottom, rcl.xRight + 1, rcl.yTop + 1}; 
    }
    OS.GpiSetRegion (handle, region.handle, lCount, rcls);
}

/** 
 * Returns the font currently being used by the receiver
 * to draw and measure text.
 *
 * @return the receiver's font
 *
 * @exception SWTException <ul>
 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
 * </ul>
 */
public Font getFont () {
	if (handle == 0) SWT.error(SWT.ERROR_GRAPHIC_DISPOSED);
	return Font.pm_new (data.device, hFont);
}

/**
 * Returns a FontMetrics which contains information
 * about the font currently being used by the receiver
 * to draw and measure text.
 *
 * @return font metrics for the receiver's font
 *
 * @exception SWTException <ul>
 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
 * </ul>
 */
public FontMetrics getFontMetrics() {
	if (handle == 0) SWT.error(SWT.ERROR_GRAPHIC_DISPOSED);
	return FontMetrics.pm_new (hFontMetrics);
}

/** 
 * Returns the receiver's foreground color.
 *
 * @return the color used for drawing foreground things
 *
 * @exception SWTException <ul>
 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
 * </ul>
 */
public Color getForeground() {
	if (handle == 0) SWT.error(SWT.ERROR_GRAPHIC_DISPOSED);
	int color = OS.GpiQueryColor (handle);
	return Color.pm_new(data.device, color);
}

/** 
 * Returns the receiver's line style, which will be one
 * of the constants <code>SWT.LINE_SOLID</code>, <code>SWT.LINE_DASH</code>,
 * <code>SWT.LINE_DOT</code>, <code>SWT.LINE_DASHDOT</code> or
 * <code>SWT.LINE_DASHDOTDOT</code>.
 *
 * @return the style used for drawing lines
 *
 * @exception SWTException <ul>
 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
 * </ul>
 */
public int getLineStyle() {
	if (handle == 0) SWT.error(SWT.ERROR_GRAPHIC_DISPOSED);
    int type = OS.GpiQueryLineType (handle);
    switch (type) {
        case OS.LINETYPE_SOLID:         return SWT.LINE_SOLID;
        case OS.LINETYPE_LONGDASH:      return SWT.LINE_DASH;
        case OS.LINETYPE_DOT:           return SWT.LINE_DOT;
        case OS.LINETYPE_DASHDOT:       return SWT.LINE_DASHDOT;
        case OS.LINETYPE_DASHDOUBLEDOT: return SWT.LINE_DASHDOTDOT;
        default:                        return SWT.LINE_SOLID;
	}
}

/** 
 * Returns the width that will be used when drawing lines
 * for all of the figure drawing operations (that is,
 * <code>drawLine</code>, <code>drawRectangle</code>, 
 * <code>drawPolyline</code>, and so forth.
 *
 * @return the receiver's line width 
 *
 * @exception SWTException <ul>
 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
 * </ul>
 */
public int getLineWidth() {
	if (handle == 0) SWT.error(SWT.ERROR_GRAPHIC_DISPOSED);
	return OS.GpiQueryLineWidthGeom (handle);
}

/** 
 * Returns <code>true</code> if this GC is drawing in the mode
 * where the resulting color in the destination is the
 * <em>exclusive or</em> of the color values in the source
 * and the destination, and <code>false</code> if it is
 * drawing in the mode where the destination color is being
 * replaced with the source color value.
 *
 * @return <code>true</code> true if the receiver is in XOR mode, and false otherwise
 *
 * @exception SWTException <ul>
 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
 * </ul>
 */
public boolean getXORMode() {
	if (handle == 0) SWT.error(SWT.ERROR_GRAPHIC_DISPOSED);
    /*
     *  The background mix should be in XOR mode too
     *  (as it set by setXORMode()), so it's possible to check either.
     *  According to the current functionality they cannot be different.
     */
	int mix = OS.GpiQueryMix (handle);
	return mix == OS.FM_XOR;
}

void init (Drawable drawable, GCData data, int hps) {
    Image image = data.image;
    int hPalette = data.device.hPalette;
    /*
     *  If a long-term presentation space is obtained for the first time
     *  or it is a temporary PS (for which the flag below should always be true)
     *  then do its basic setup.
     */
    if (data.doInit) {
        BITMAPINFOHEADER2 bm = null;
        /*
         *  Set the default view matrix to do the horizontal flipping
         *  when the drawable is the Control (data.hwnd != 0)
         *  or Image (data.image != null).
         */
        int height = -1;
        if (image != null) {
            bm = new BITMAPINFOHEADER2 ();
            OS.GpiQueryBitmapInfoHeader (image.handle, bm);
            height = bm.cy;
        } else if (data.hwnd != 0) {
            RECTL rcl = new RECTL();
            OS.WinQueryWindowRect (data.hwnd, rcl);
            height = rcl.yTop;
        }
        if (height != -1) {
            // setup flipping of the y axis
            matrix [4] = 0xFFFF0000;
            matrix [7] = height - 1;
            OS.GpiSetDefaultViewMatrix (hps, matrix.length, matrix, OS.TRANSFORM_REPLACE);
        }
        if (hPalette == 0)
            OS.GpiCreateLogColorTable (hps, 0, OS.LCOLF_RGB, 0, 0, null);
        else
            OS.GpiSelectPalette (hps, hPalette);
    } else {
        /* fill in the matrix structure with actual values for future reference */
        OS.GpiQueryDefaultViewMatrix (hps, matrix.length, matrix);
    }
    OS.GpiSetMix (hps, OS.FM_OVERPAINT);
    OS.GpiSetBackMix (hps, OS.BM_OVERPAINT);
    OS.GpiSetPattern (hps, OS.PATSYM_BLANK);
    OS.GpiSetLineEnd (hps, OS.LINEEND_ROUND);
    OS.GpiSetLineJoin (hps, OS.LINEJOIN_ROUND);
    OS.GpiSetLineType (hps, OS.LINETYPE_SOLID);
    OS.GpiSetLineWidthGeom (hps, 1);

    int foreground = data.foreground;
    /* default foreground is black */
    if (foreground == OS.CLR_DEFAULT) {
        foreground = 0x000000;
        // black is the 0th element of the default palette set in Device.init()
        // so the same value is suitable the for palette-based device
    }
    OS.GpiSetColor (hps, foreground);

    int background = data.background;
    /* default background is white */
    if (background == OS.CLR_DEFAULT) {
        background = 0xFFFFFF;
        // white is the 15th element of the default palette set in Device.init()
        if (hPalette != 0) background = 15;
    }
    OS.GpiSetBackColor (hps, background);

    if (image != null) {
        image.memGC = this;
        OS.GpiSetBitmap (hps, image.handle);
    } else if (data.hwnd != 0 && hPalette != 0) {
        int[] cclr = new int[1];
        OS.WinRealizePalette (data.hwnd, hps, cclr);
    }

    hFont = data.hFont == null ? data.device.sysFont : data.hFont;
    internal_set_font (hps, data.hdc, data.device, hFont);
    hFontMetrics = new FONTMETRICS();
    OS.GpiQueryFontMetrics (hps, FONTMETRICS.sizeof, hFontMetrics);

    this.drawable = drawable;
    this.data = data;
    handle = hps;
}

/**
 * Returns an integer hash code for the receiver. Any two 
 * objects which return <code>true</code> when passed to 
 * <code>equals</code> must return the same value for this
 * method.
 *
 * @return the receiver's hash
 *
 * @exception SWTException <ul>
 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
 * </ul>
 *
 * @see #equals
 */
public int hashCode () {
	return handle;
}

/**
 * Returns <code>true</code> if the receiver has a clipping
 * region set into it, and <code>false</code> otherwise.
 * If this method returns false, the receiver will draw on all
 * available space in the destination. If it returns true, 
 * it will draw only in the area that is covered by the region
 * that can be accessed with <code>getClipping(region)</code>.
 *
 * @return <code>true</code> if the GC has a clipping region, and <code>false</code> otherwise
 *
 * @exception SWTException <ul>
 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
 * </ul>
 */
public boolean isClipped() {
	if (handle == 0) SWT.error(SWT.ERROR_GRAPHIC_DISPOSED);
    return (OS.GpiQueryClipRegion (handle) > 0);
}

/**
 * Returns <code>true</code> if the GC has been disposed,
 * and <code>false</code> otherwise.
 * <p>
 * This method gets the dispose state for the GC.
 * When a GC has been disposed, it is an error to
 * invoke any other method using the GC.
 *
 * @return <code>true</code> when the GC is disposed and <code>false</code> otherwise
 */
public boolean isDisposed() {
	return handle == 0;
}

/**
 * Sets the background color. The background color is used
 * for fill operations and as the background color when text
 * is drawn.
 *
 * @param color the new background color for the receiver
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the color is null</li>
 *    <li>ERROR_INVALID_ARGUMENT - if the color has been disposed</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
 * </ul>
 */
public void setBackground (Color color) {
	if (handle == 0) SWT.error(SWT.ERROR_GRAPHIC_DISPOSED);
	if (color == null) SWT.error(SWT.ERROR_NULL_ARGUMENT);
	if (color.isDisposed()) SWT.error(SWT.ERROR_INVALID_ARGUMENT);
    int hColor = color.handle;
    int hPalette = data.device.hPalette;
    if (hPalette != 0 && !data.device.equals (color.device)) {
        /*
         *  We must do this check inspite of its absence in the original code 
         *  because the color handle (index) obtained for one device will be
         *  most lilkey invalid for another.
         *  (add this to javadoc when confirmed...)
         */
        SWT.error(SWT.ERROR_INVALID_ARGUMENT);
    }
	if (OS.GpiQueryBackColor (handle) == hColor) return;
	OS.GpiSetBackColor (handle, hColor);
}

/**
 * Sets the area of the receiver which can be changed
 * by drawing operations to the rectangular area specified
 * by the arguments.
 *
 * @param x the x coordinate of the clipping rectangle
 * @param y the y coordinate of the clipping rectangle
 * @param width the width of the clipping rectangle
 * @param height the height of the clipping rectangle
 *
 * @exception SWTException <ul>
 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
 * </ul>
 */
public void setClipping (int x, int y, int width, int height) {
	if (handle == 0) SWT.error(SWT.ERROR_GRAPHIC_DISPOSED);
    // flip the y coordinate if the flipping is in force (i.e. drawable is the Control)
    if (matrix [4] == 0xFFFF0000) {
        y = matrix [7] + 1 - (y + height);
    }
	int hrgn = OS.GpiCreateRegion (handle, 1,
        new int[] {x, y, x + width, y + height});
    int[] hrgnOld = new int[1];
    OS.GpiSetClipRegion (handle, hrgn, hrgnOld);
    if (hrgnOld[0] != OS.NULLHANDLE)
        OS.GpiDestroyRegion (handle, hrgnOld[0]);
}

/**
 * Sets the area of the receiver which can be changed
 * by drawing operations to the rectangular area specified
 * by the argument.
 *
 * @param rect the clipping rectangle
 *
 * @exception SWTException <ul>
 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
 * </ul>
 */
public void setClipping (Rectangle rect) {
	if (handle == 0) SWT.error(SWT.ERROR_GRAPHIC_DISPOSED);
	if (rect == null) {
        int[] hrgnOld = new int[1];
        OS.GpiSetClipRegion (handle, OS.NULLHANDLE, hrgnOld);
        if (hrgnOld[0] != OS.NULLHANDLE)
            OS.GpiDestroyRegion (handle, hrgnOld[0]);
		return;
	}
	setClipping (rect.x, rect.y, rect.width, rect.height);
}

/**
 * Sets the area of the receiver which can be changed
 * by drawing operations to the region specified
 * by the argument.
 *
 * @param region the clipping region.
 *
 * @exception SWTException <ul>
 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
 * </ul>
 */
public void setClipping (Region region) {
	if (handle == 0) SWT.error(SWT.ERROR_GRAPHIC_DISPOSED);
	int hRegion = OS.NULLHANDLE;
	if (region != null) {
        /* 
         * Feature in OS/2. If the region is selected into the presentation
         * space, most region operations on it are prohibited. So, make a
         * copy.
         */
        if (matrix [4] == 0xFFFF0000) {
            /*
             * Feature in OS/2. The default transformation matrix (and others) is
             * not applied to region coordinates when selecting it as a clip
             * region. The solution is to get region rectangles and transform
             * each of them manually.
             */
            RGNRECT rgnctl = new RGNRECT();
            rgnctl.ircStart = 1;
            rgnctl.ulDirection = OS.RECTDIR_LFRT_TOPBOT;
            OS.GpiQueryRegionRects (handle, region.handle, null, rgnctl, null);
            int[] rgnRects = new int [rgnctl.crcReturned * 4];
            rgnctl.crc = rgnctl.crcReturned;
            OS.GpiQueryRegionRects (handle, region.handle, null, rgnctl, rgnRects);
                
            int y, height = matrix [7] + 1, cnt = rgnRects.length / 4;
            for (int i = 0; i < cnt; i++) {
                y = rgnRects [(i << 2) + 1];
                rgnRects [(i << 2) + 1] = height - rgnRects [(i << 2) + 3];
                rgnRects [(i << 2) + 3] = height - y;
            }
            hRegion = OS.GpiCreateRegion (handle, cnt, rgnRects);
        } else {
            hRegion = OS.GpiCreateRegion (handle, 0, null);
            OS.GpiCombineRegion (handle, hRegion, region.handle,
                OS.NULLHANDLE, OS.CRGN_COPY);
        }
    }
    int[] hrgnOld = new int[1];
    OS.GpiSetClipRegion (handle, hRegion, hrgnOld);
    if (hrgnOld[0] != OS.NULLHANDLE)
        OS.GpiDestroyRegion (handle, hrgnOld[0]);
}

/**	 
 * Invokes platform specific functionality to select a font into a graphics
 * context.
 * <p>
 * <b>IMPORTANT:</b> This method is <em>not</em> part of the public
 * API for <code>GC</code>. It is marked public only so that it
 * can be shared within the packages provided by SWT. It is not
 * available on all platforms, and should never be called from
 * application code.
 * </p>
 *
 * @param hps graphics context handle
 * @param font attributes handle
 *
 * @private
 */
public static void internal_set_font (int hps, int hdc, Device device, FATTRS hFont) {
    /*
     *  If fa points to the FATTRS entry from the font cache then some bits in
     *  fsType can be invalid from the GpiCreateLogFont point of view, which will
     *  cause it to fail. The solution is to temporarily clean out these bits.
     */ 
    short fsType = hFont.fsType;
    hFont.fsType &= 0x0FFC;
    hFont.usRecordLength = FATTRS.sizeof;
//@@TODO (dmik): Unicode    
//    /*
//     *  Feature in OS/2. When we set the Unicode codepage (IBM-1200) the GPI
//     *  is switched to Unicode mode and treats all text strings passed to
//     *  related API calls as arrays of 16-bit unicode characters. 
//     */
//    hFont.usCodePage = 1200;
    OS.GpiCreateLogFont (hps, null, 1, hFont);
    hFont.fsType = fsType;

    OS.GpiSetCharSet (hps, 1);
    if ((hFont.fsFontUse & OS.FATTR_FONTUSE_OUTLINE) != 0) {
        int height = hFont.height;
        if (height == 0) height = device.defFontHeight;
        int dpiInfo[] = new int [1];
        OS.DevQueryCaps (hdc, OS.CAPS_VERTICAL_FONT_RES, 1, dpiInfo);
        int hPixels = Compatibility.round (height * dpiInfo[0], 72) << 16;
        OS.GpiSetCharBox (hps, new int[] {hPixels, hPixels});
    }
}

/** 
 * Sets the font which will be used by the receiver
 * to draw and measure text to the argument. If the
 * argument is null, then a default font appropriate
 * for the platform will be used instead.
 *
 * @param font the new font for the receiver, or null to indicate a default font
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_INVALID_ARGUMENT - if the font has been disposed</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
 * </ul>
 */
public void setFont (Font font) {
	if (handle == 0) SWT.error(SWT.ERROR_GRAPHIC_DISPOSED);
    Device device = data.device;
	if (font == null) {
        device.checkFontCache();
        hFont = device.sysFont;
	} else {
		if (font.isDisposed()) SWT.error(SWT.ERROR_INVALID_ARGUMENT);
        hFont = font.internal_get_handle();
    }
    internal_set_font (handle, data.hdc, data.device, hFont);
    OS.GpiQueryFontMetrics (handle, FONTMETRICS.sizeof, hFontMetrics);
}

/**
 * Sets the foreground color. The foreground color is used
 * for drawing operations including when text is drawn.
 *
 * @param color the new foreground color for the receiver
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the color is null</li>
 *    <li>ERROR_INVALID_ARGUMENT - if the color has been disposed</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
 * </ul>
 */
public void setForeground (Color color) {
	if (handle == 0) SWT.error(SWT.ERROR_GRAPHIC_DISPOSED);
	if (color == null) SWT.error(SWT.ERROR_NULL_ARGUMENT);
	if (color.isDisposed()) SWT.error(SWT.ERROR_INVALID_ARGUMENT);
    int hColor = color.handle;
    int hPalette = data.device.hPalette;
    if (hPalette != 0 && !data.device.equals (color.device)) {
        /*
         *  We must do this check although it is absent in the original code 
         *  because the color handle (index) obtained for one device will be
         *  most lilkey invalid for another.
         *  (add this to javadoc when confirmed...)
         */
        SWT.error(SWT.ERROR_INVALID_ARGUMENT);
    }
    if (OS.GpiQueryColor (handle) == hColor) return;
    OS.GpiSetColor (handle, hColor);
}

/** 
 * Sets the receiver's line style to the argument, which must be one
 * of the constants <code>SWT.LINE_SOLID</code>, <code>SWT.LINE_DASH</code>,
 * <code>SWT.LINE_DOT</code>, <code>SWT.LINE_DASHDOT</code> or
 * <code>SWT.LINE_DASHDOTDOT</code>.
 *
 * @param lineStyle the style to be used for drawing lines
 *
 * @exception SWTException <ul>
 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
 * </ul>
 */
public void setLineStyle(int lineStyle) {
	if (handle == 0) SWT.error(SWT.ERROR_GRAPHIC_DISPOSED);
	int style = -1;
	switch (lineStyle) {
		case SWT.LINE_SOLID:      style = OS.LINETYPE_SOLID; break;
		case SWT.LINE_DASH:       style = OS.LINETYPE_LONGDASH; break;
		case SWT.LINE_DOT:        style = OS.LINETYPE_DOT; break;
		case SWT.LINE_DASHDOT:    style = OS.LINETYPE_DASHDOT; break;
		case SWT.LINE_DASHDOTDOT: style = OS.LINETYPE_DASHDOUBLEDOT; break;
		default:
			SWT.error(SWT.ERROR_INVALID_ARGUMENT);
	}
    OS.GpiSetLineType (handle, style);
}

/** 
 * Sets the width that will be used when drawing lines
 * for all of the figure drawing operations (that is,
 * <code>drawLine</code>, <code>drawRectangle</code>, 
 * <code>drawPolyline</code>, and so forth.
 *
 * @param lineWidth the width of a line
 *
 * @exception SWTException <ul>
 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
 * </ul>
 */
public void setLineWidth(int lineWidth) {
	if (handle == 0) SWT.error(SWT.ERROR_GRAPHIC_DISPOSED);
    OS.GpiSetLineWidthGeom (handle, lineWidth);
}

/** 
 * If the argument is <code>true</code>, puts the receiver
 * in a drawing mode where the resulting color in the destination
 * is the <em>exclusive or</em> of the color values in the source
 * and the destination, and if the argument is <code>false</code>,
 * puts the receiver in a drawing mode where the destination color
 * is replaced with the source color value.
 *
 * @param xor if <code>true</code>, then <em>xor</em> mode is used, otherwise <em>source copy</em> mode is used
 *
 * @exception SWTException <ul>
 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
 * </ul>
 */
public void setXORMode(boolean xor) {
	if (handle == 0) SWT.error(SWT.ERROR_GRAPHIC_DISPOSED);
	if (xor) {
        OS.GpiSetMix (handle, OS.FM_XOR);
        OS.GpiSetBackMix (handle, OS.BM_XOR);
	} else {
        OS.GpiSetMix (handle, OS.FM_OVERPAINT);
        OS.GpiSetBackMix (handle, OS.BM_OVERPAINT);
	}
}

/**
 * Returns the extent of the given string. No tab
 * expansion or carriage return processing will be performed.
 * <p>
 * The <em>extent</em> of a string is the width and height of
 * the rectangular area it would cover if drawn in a particular
 * font (in this case, the current font in the receiver).
 * </p>
 *
 * @param string the string to measure
 * @return a point containing the extent of the string
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the string is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
 * </ul>
 */
public Point stringExtent(String string) {
	if (handle == 0) SWT.error(SWT.ERROR_GRAPHIC_DISPOSED);
	if (string == null) SWT.error (SWT.ERROR_NULL_ARGUMENT);
    if (string.length() == 0) {
        return new Point (0, hFontMetrics.lMaxBaselineExt);
    }
    int length = string.length();
//@@TODO (dmik): Unicode    
//    char[] text = new char [length];
//    string.getChars (0, length, text, 0);
    byte[] text = string.getBytes();
    int[] pnts = new int [OS.TXTBOX_COUNT * 2];
    /*
     *  Feature in OS/2. Temporarily disable the transform matrix, otherwise we
     *  get the positive height for truetype fonts and negative for all others.
     *  When the matrix is disabled we get positives for all.
     */
    OS.GpiSetDefaultViewMatrix (handle, 0, null, OS.TRANSFORM_REPLACE);
    /*
     *  Feature in OS/2. The maximum length of the string in string-related
     *  GPI functions is 512 chars. Do the cycle to handle larger strings.
     */
    int extX = 0, dx = 0;
    int is = 0;
    while (is < text.length) {
//@@TODO (dmik): Unicode    
//        int ie = is + 256;
        int ie = is + 512;
        if (ie > text.length) ie = text.length;
        if (is != 0) {
            System.arraycopy (text, is, text, 0, ie - is);  
        }
//@@TODO (dmik): Unicode    
//        OS.GpiQueryTextBox (handle, (ie - is) << 1, text, OS.TXTBOX_COUNT, pnts);
        OS.GpiQueryTextBox (handle, ie - is, text, OS.TXTBOX_COUNT, pnts);
        extX += pnts [8];
        dx = pnts [4];
        if (dx < pnts [8]) dx = pnts [8];
        dx = dx - pnts [8];
        is = ie;
    }
    OS.GpiSetDefaultViewMatrix (handle, 9, matrix, OS.TRANSFORM_REPLACE);
    /*
     *  Bug in OS/2. The height of the string calculated from points
     *  returned by GpiQueryTextBox() (pnts [1] - pnts [3]) is one pixel
     *  larger than the actual font height (lMaxBaselineExt) for some TTF
     *  fonts. So, use lMaxBaselineExt from font metrics instead.
     */
    return new Point (extX + dx, hFontMetrics.lMaxBaselineExt);
}

/**
 * Returns the extent of the given string. Tab expansion and
 * carriage return processing are performed.
 * <p>
 * The <em>extent</em> of a string is the width and height of
 * the rectangular area it would cover if drawn in a particular
 * font (in this case, the current font in the receiver).
 * </p>
 *
 * @param string the string to measure
 * @return a point containing the extent of the string
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the string is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
 * </ul>
 */
public Point textExtent(String string) {
	return textExtent(string, SWT.DRAW_DELIMITER | SWT.DRAW_TAB);
}

/**
 * Returns the extent of the given string. Tab expansion, line
 * delimiter and mnemonic processing are performed according to
 * the specified flags, which can be a combination of:
 * <dl>
 * <dt><b>DRAW_DELIMITER</b></dt>
 * <dd>draw multiple lines</dd>
 * <dt><b>DRAW_TAB</b></dt>
 * <dd>expand tabs</dd>
 * <dt><b>DRAW_MNEMONIC</b></dt>
 * <dd>underline the mnemonic character</dd>
 * <dt><b>DRAW_TRANSPARENT</b></dt>
 * <dd>transparent background</dd>
 * </dl>
 * <p>
 * The <em>extent</em> of a string is the width and height of
 * the rectangular area it would cover if drawn in a particular
 * font (in this case, the current font in the receiver).
 * </p>
 *
 * @param string the string to measure
 * @param flags the flags specifing how to process the text
 * @return a point containing the extent of the string
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the string is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
 * </ul>
 */
public Point textExtent(String string, int flags) {
	if (handle == 0) SWT.error(SWT.ERROR_GRAPHIC_DISPOSED);
	if (string == null) SWT.error (SWT.ERROR_NULL_ARGUMENT);
    return drawTabbedText (string, 0, 0, flags, true); 
}

/**
 * Returns a string containing a concise, human-readable
 * description of the receiver.
 *
 * @return a string representation of the receiver
 */
public String toString () {
	if (isDisposed()) return "GC {*DISPOSED*}";
	return "GC {" + handle + "}";
}

int beginGeomLine() {
    int pattern = OS.GpiQueryPattern (handle);
    OS.GpiBeginPath (handle, 1);
    OS.GpiSetPattern (handle, OS.PATSYM_SOLID);
    return pattern;
}

void endGeomLine(int savedData) {
    OS.GpiEndPath (handle);
    OS.GpiStrokePath (handle, 1, 0);
    OS.GpiSetPattern (handle, savedData);
}

/**	 
 * Invokes platform specific functionality to allocate a new graphics context.
 * <p>
 * <b>IMPORTANT:</b> This method is <em>not</em> part of the public
 * API for <code>GC</code>. It is marked public only so that it
 * can be shared within the packages provided by SWT. It is not
 * available on all platforms, and should never be called from
 * application code.
 * </p>
 *
 * @param drawable the Drawable for the receiver.
 * @param data the data for the receiver.
 *
 * @return a new <code>GC</code>
 *
 * @private
 */
public static GC pm_new (Drawable drawable, GCData data) {
    GC gc = new GC();
    int hps = drawable.internal_new_GC(data);
    gc.init(drawable, data, hps);
    return gc;
}

}
