package org.eclipse.swt.graphics;

/*
 * OS/2 version.
 * Copyright (c) 2002, 2009 EclipseOS2 Team.
 */
 
/*
 * Copyright (c) 2000, 2002 IBM Corp.  All rights reserved.
 * This file is made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 */

import org.eclipse.swt.internal.pm.*;
import org.eclipse.swt.*;
import java.io.*;

/**
 * Instances of this class are graphics which have been prepared
 * for display on a specific device. That is, they are ready
 * to paint using methods such as <code>GC.drawImage()</code>
 * and display on widgets with, for example, <code>Button.setImage()</code>.
 * <p>
 * If loaded from a file format that supports it, an
 * <code>Image</code> may have transparency, meaning that certain
 * pixels are specified as being transparent when drawn. Examples
 * of file formats that support transparency are GIF and PNG.
 * </p><p>
 * There are two primary ways to use <code>Images</code>. 
 * The first is to load a graphic file from disk and create an
 * <code>Image</code> from it. This is done using an <code>Image</code>
 * constructor, for example:
 * <pre>
 *    Image i = new Image(device, "C:\\graphic.bmp");
 * </pre>
 * A graphic file may contain a color table specifying which
 * colors the image was intended to possess. In the above example,
 * these colors will be mapped to the closest available color in
 * SWT. It is possible to get more control over the mapping of
 * colors as the image is being created, using code of the form:
 * <pre>
 *    ImageData data = new ImageData("C:\\graphic.bmp"); 
 *    RGB[] rgbs = data.getRGBs(); 
 *    // At this point, rgbs contains specifications of all
 *    // the colors contained within this image. You may
 *    // allocate as many of these colors as you wish by
 *    // using the Color constructor Color(RGB), then
 *    // create the image:
 *    Image i = new Image(device, data);
 * </pre>
 * <p>
 * Applications which require even greater control over the image
 * loading process should use the support provided in class
 * <code>ImageLoader</code>.
 * </p><p>
 * Application code must explicitely invoke the <code>Image.dispose()</code> 
 * method to release the operating system resources managed by each instance
 * when those instances are no longer required.
 * </p>
 *
 * @see Color
 * @see ImageData
 * @see ImageLoader
 */

public final class Image implements Drawable {
	
	/**
	 * specifies whether the receiver is a bitmap or an icon
	 * (one of <code>SWT.BITMAP</code>, <code>SWT.ICON</code>)
	 */
	public int type;
	
	/**
	 * the OS resource of the image
	 * (Warning: This field is platform dependent)
	 */
	public int handle;
	
	/**
	 * the device where this image was created
	 */
	Device device;
	
	/**
	 * specifies the transparent pixel
	 * (Warning: This field is platform dependent)
	 */
	int transparentPixel = -1;
	
	/**
	 * the GC which is drawing on the image
	 * (Warning: This field is platform dependent)
	 */
	GC memGC;
	
	/**
	 * the alpha data for the image
	 * (Warning: This field is platform dependent)
	 */
	byte[] alphaData;
	
	/**
	 * the global alpha value to be used for every pixel
	 * (Warning: This field is platform dependent)
	 */
	int alpha = -1;
	
	/**
	 * the OS resource of the image mask
	 * (Warning: This field is platform dependent)
	 */
	/*
	 * the mask bitmap handle used when this image is an icon and its size
     * does not correspond to the system default icon or pointer size. In this
     * case the <code>handle</code> field contains the handle of the icon
     * bitmap, not the pointer handle. Otherwise this field is zero.
	 * Used only in OS/2
     */
	public int maskHandle;
	
	/**
	 * specifies the default scanline padding
	 * (Warning: This field is platform dependent)
	 */
	static final int DEFAULT_SCANLINE_PAD = 4;
    
/**
 * Prevents uninitialized instances from being created outside the package.
 */
Image () {
}

/**
 * Constructs an empty instance of this class with the
 * specified width and height. The result may be drawn upon
 * by creating a GC and using any of its drawing operations,
 * as shown in the following example:
 * <pre>
 *    Image i = new Image(device, width, height);
 *    GC gc = new GC(i);
 *    gc.drawRectangle(0, 0, 50, 50);
 *    gc.dispose();
 * </pre>
 * <p>
 * Note: Some platforms may have a limitation on the size
 * of image that can be created (size depends on width, height,
 * and depth). For example, Windows 95, 98, and ME do not allow
 * images larger than 16M.
 * </p>
 *
 * @param device the device on which to create the image
 * @param width the width of the new image
 * @param height the height of the new image
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if device is null and there is no current device</li>
 *    <li>ERROR_INVALID_ARGUMENT - if either the width or height is negative or zero</li>
 * </ul>
 * @exception SWTError <ul>
 *    <li>ERROR_NO_HANDLES if a handle could not be obtained for image creation</li>
 * </ul>
 */
public Image(Device device, int width, int height) {
	if (device == null) device = Device.getDevice();
	if (device == null) SWT.error(SWT.ERROR_NULL_ARGUMENT);
	init(device, width, height);
	if (device.tracking) device.new_Object(this);	
}

/**
 * Constructs a new instance of this class based on the
 * provided image, with an appearance that varies depending
 * on the value of the flag. The possible flag values are:
 * <dl>
 * <dt><b>IMAGE_COPY</b></dt>
 * <dd>the result is an identical copy of srcImage</dd>
 * <dt><b>IMAGE_DISABLE</b></dt>
 * <dd>the result is a copy of srcImage which has a <em>disabled</em> look</dd>
 * <dt><b>IMAGE_GRAY</b></dt>
 * <dd>the result is a copy of srcImage which has a <em>gray scale</em> look</dd>
 * </dl>
 *
 * @param device the device on which to create the image
 * @param srcImage the image to use as the source
 * @param flag the style, either <code>IMAGE_COPY</code>, <code>IMAGE_DISABLE</code> or <code>IMAGE_GRAY</code>
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if device is null and there is no current device</li>
 *    <li>ERROR_NULL_ARGUMENT - if srcImage is null</li>
 *    <li>ERROR_INVALID_ARGUMENT - if the flag is not one of <code>IMAGE_COPY</code>, <code>IMAGE_DISABLE</code> or <code>IMAGE_GRAY</code></li>
 *    <li>ERROR_INVALID_ARGUMENT - if the image has been disposed</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_INVALID_IMAGE - if the image is not a bitmap or an icon, or
 *          is otherwise in an invalid state</li>
 * </ul>
 * @exception SWTError <ul>
 *    <li>ERROR_NO_HANDLES if a handle could not be obtained for image creation</li>
 * </ul>
 */
public Image(Device device, Image srcImage, int flag) {
	if (device == null) device = Device.getDevice();
	if (device == null) SWT.error (SWT.ERROR_NULL_ARGUMENT);
	this.device = device;
	if (srcImage == null) SWT.error (SWT.ERROR_NULL_ARGUMENT);
	if (srcImage.isDisposed()) SWT.error (SWT.ERROR_INVALID_ARGUMENT);
	switch (flag) {
		case SWT.IMAGE_COPY: {
			this.type = srcImage.type;
			switch (type) {
				case SWT.BITMAP:
                    boolean mustRestore = false;
                    GC memGC = srcImage.memGC;
                    if (memGC != null && !memGC.isDisposed()) {
                        mustRestore = true;
                        OS.GpiSetBitmap (memGC.handle, 0);
                    }
					/* Copy the bitmap */
                    handle = createBitmap (
                        0, 0, 0, 0, null, null, null, srcImage.handle); 
                    if (mustRestore) {
                        OS.GpiSetBitmap (memGC.handle, srcImage.handle);
                    }
                    transparentPixel = srcImage.transparentPixel;
					alpha = srcImage.alpha;
					if (srcImage.alphaData != null) {
						alphaData = new byte [srcImage.alphaData.length];
						System.arraycopy (srcImage.alphaData, 0, alphaData, 0, alphaData.length);
					}
					break;
				case SWT.ICON:
                    POINTERINFO info = new POINTERINFO ();
                    int hbmColor = srcImage.handle;
                    int hbmPointer = srcImage.maskHandle;
                    if (hbmPointer == 0) {
                        OS.WinQueryPointerInfo (srcImage.handle, info);
                        hbmColor = info.hbmColor;
                        hbmPointer = info.hbmPointer;
                    }
                    int[] bitmapPS = new int [1];
					/* Copy the mask bitmap */
                    maskHandle = createBitmap (
                        0, 0, 1, 1, null, null, bitmapPS, hbmPointer);
                    int bmpPS = bitmapPS [0];
					/* Copy the icon bitmap */
                    if (hbmColor != 0) {
                        handle = createBitmap (
                            0, 0, 0, 0, null, null, bitmapPS, hbmColor);
                    }
                    dispose_compatible_GC (bmpPS, null);
					/* Create the icon in system terms when appropriate */
                    if (srcImage.maskHandle == 0) {
                        info.hbmColor = handle;
                        info.hbmPointer = maskHandle;
                        int hIcon = OS.WinCreatePointerIndirect (OS.HWND_DESKTOP, info);
                        if (hIcon == 0) SWT.error(SWT.ERROR_NO_HANDLES);
                        OS.GpiDeleteBitmap (handle);
                        OS.GpiDeleteBitmap (maskHandle);
                        handle = hIcon;
                        maskHandle = 0;
                    }
					break;
				default:
					SWT.error (SWT.ERROR_UNSUPPORTED_FORMAT);
			}
			if (device.tracking) device.new_Object (this);	
			return;
		}
		case SWT.IMAGE_DISABLE: {
			this.type = srcImage.type;

            ImageData srcData = srcImage.getImageData();
            int width = srcData.width;
            int height = srcData.height;
            
            /* Get the HPS for the device */
            GCData hPSData = new GCData();
            int hPS = device.internal_new_GC (hPSData);
            
            int hpsBW = new_compatible_GC (null, hPSData.hdc, width, height * 2);
            int hpsBmp = new_compatible_GC (null, hPSData.hdc, width, height);

            /*
             *  OS/2 does not support adequate conversion of bitmaps
             *  from color to monochrome, so do it manually.
             */
            int bwBytesPerLine = ((width + 31) / 32) * 4;
            byte[] bwData = new byte [bwBytesPerLine * height]; 
            int offset = bwData.length;
            PaletteData palette = srcData.palette;
            RGB[] rgbs = palette.getRGBs();
            int[] srcPixels = new int [width];
            int redMask = palette.redMask;
            int greenMask = palette.greenMask;
            int blueMask = palette.blueMask;
            int redShift = palette.redShift;
            int greenShift = palette.greenShift;
            int blueShift = palette.blueShift;
            for (int y = 0; y < height; y++) {
                srcData.getPixels (0, y, width, srcPixels, 0);
                offset -= bwBytesPerLine;
                for (int x = 0; x < width; x++) {
                    int pixel = srcPixels [x];
                    int red = 0, green = 0, blue = 0;
                    if (palette.isDirect) {
                        red = pixel & redMask;
                        red = (redShift < 0) ? red >>> -redShift : red << redShift;
                        green = pixel & greenMask;
                        green = (greenShift < 0) ? green >>> -greenShift : green << greenShift;
                        blue = pixel & blueMask;
                        blue = (blueShift < 0) ? blue >>> -blueShift : blue << blueShift;
                        
                    } else {
                        red = rgbs [pixel].red;
                        green = rgbs [pixel].green;
                        blue = rgbs [pixel].blue;
                    }
                    int intensity = (red+red+green+green+green+green+green+blue) >> 3;
                    /*
                     *  white bits should actually be zeros (and black bits should
                     *  be ones) in order to use the BM_SRCTRANSPARENT mix below
                     */
                    int bit = (intensity >= 128 ? 0 : 0x80) >> (x % 8);
                    bwData [offset + (x / 8)] |= (byte) bit;  
                }
            }
            
            /* Create the BITMAPINFO2 */
            final int bmi_cbFix = 4;
            int[] bmi = new int [bmi_cbFix + 2];
            bmi [0] = bmi_cbFix * 4; // cbFix
            bmi [1] = width; // cx
            bmi [2] = height; // cy
            bmi [3] = 0x00010001; // cPlanes, cBitCount
            bmi [4] = 0x00000000; // black color
            bmi [5] = 0x00FFFFFF; // white color
            /* Create the monochrome (intermediate) bitmap */
            int hbmBW = createBitmap (
                width, height, 1, 1, bwData, bmi, new int[] {hpsBW}, 0);
                
            /* Get system colors for the disabled effect */
            int cDark = OS.SYSCLR_BUTTONDARK; 
            int cMiddle = OS.SYSCLR_BUTTONMIDDLE; 
            int cLight = OS.SYSCLR_BUTTONLIGHT;
            int hPalette = device.hPalette; 
            if (hPalette != 0) {
                cDark = OS.WinQuerySysColor (OS.HWND_DESKTOP, cDark, 0); 
                cMiddle = OS.WinQuerySysColor (OS.HWND_DESKTOP, cMiddle, 0); 
                cLight = OS.WinQuerySysColor (OS.HWND_DESKTOP, cLight, 0);
                cDark = OS.GpiQueryNearestPaletteIndex (device.hPalette, cDark);
                cMiddle = OS.GpiQueryNearestPaletteIndex (device.hPalette, cMiddle);
                cLight = OS.GpiQueryNearestPaletteIndex (device.hPalette, cLight);
            }
                
			switch (type) {
				case SWT.BITMAP:
                    handle = createBitmap (
                        width, height, 0, 0,
                        null, null, new int[] {hpsBmp}, 0
                    );
                    /* Fill the bitmap with the SYSCLR_BUTTONMIDDLE color. */
                    OS.GpiSetColor (hpsBmp, cMiddle);
                    OS.GpiBox (hpsBmp, OS.DRO_FILL, new int[] {width, height}, 0, 0); 
					/*
					 *  BitBlt the black bits in the monochrome bitmap into
					 *  SYSCLR_BUTTONLIGHT bits in the destination PS and
                     *  white bits into SYSCLR_BUTTONMIDDLE bits.
					 */
                    OS.GpiSetColor (hpsBmp, cLight);
                    OS.GpiSetBackColor (hpsBmp, cMiddle);
                    OS.GpiBitBlt (hpsBmp, hpsBW, 4,
                        new int[] {1, 0, width, height - 1, 0, 1, width - 1, height},
                        OS.ROP_SRCCOPY, OS.BBO_IGNORE
                    );
					/*
					 *  BitBlt the black bits in the monochrome bitmap into 
					 *  SYSCLR_BUTTONDARK bits in the destination PS.
					 */
                    OS.GpiSetColor (hpsBmp, cDark);
                    OS.GpiSetBackMix (hpsBmp, OS.BM_SRCTRANSPARENT);
                    OS.GpiBitBlt (hpsBmp, hpsBW, 4,
                        new int[] {0, 0, width, height, 0, 0, width, height},
                        OS.ROP_SRCCOPY, OS.BBO_IGNORE
                    );
					break;
				case SWT.ICON:
					/* Get icon information */
                    POINTERINFO info = new POINTERINFO ();
                    int hbmColor = srcImage.handle;
                    int hbmPointer = srcImage.maskHandle;
                    if (hbmPointer == 0) {
                        OS.WinQueryPointerInfo (srcImage.handle, info);
                        hbmColor = info.hbmColor;
                        hbmPointer = info.hbmPointer;
                    }
					/* Copy the mask bitmap */
                    maskHandle = createBitmap (
                        0, 0, 1, 1, null, null, new int[] {hpsBmp}, hbmPointer);
					/* Create a new icon bitmap */
                    handle = createBitmap (
                        width, height, 0, 0,
                        null, null, new int[] {hpsBmp}, 0
                    );
                    /* Fill the bitmap with the SYSCLR_BUTTONMIDDLE color. */
                    OS.GpiSetColor (hpsBmp, cMiddle);
                    OS.GpiBox (hpsBmp, OS.DRO_FILL, new int[] {width, height}, 0, 0); 
					/*
					 *  BitBlt the black bits in the monochrome bitmap into
					 *  SYSCLR_BUTTONDARK bits in the destination PS and
                     *  white bits into OS.SYSCLR_BUTTONMIDDLE bits.
					 */
                    OS.GpiSetColor (hpsBmp, cDark);
                    OS.GpiSetBackColor (hpsBmp, cMiddle);
                    OS.GpiBitBlt (hpsBmp, hpsBW, 4,
                        new int[] {0, 0, width, height, 0, 0, width, height},
                        OS.ROP_SRCCOPY, OS.BBO_IGNORE
                    );
                    /*
                     *  Make transparent icon pixels black using icon's AND mask.
                     *  Although it is not necessary in OS/2, it is done for
                     *  compatibility with the Windows ICO format (otherwise we
                     *  can get the wrong icon if we save it using
                     *  ImageLoader.save() method).
                     */
                    OS.GpiSetBitmap (hpsBW, maskHandle);
                    OS.GpiSetColor (hpsBmp, OS.CLR_BLACK);
                    OS.GpiSetBackMix (hpsBmp, OS.BM_SRCTRANSPARENT);
                    OS.GpiBitBlt (hpsBmp, hpsBW, 4,
                        new int[] {0, 0, width, height, 0, height, width, height * 2},
                        OS.ROP_SRCCOPY, OS.BBO_IGNORE
                    );
                    /* Erase the XOR part of the mask */
                    OS.GpiBitBlt (hpsBW, 0, 2,
                        new int[] {0, 0, width, height},
                        OS.ROP_ZERO, 0
                    );
                    OS.GpiSetBitmap (hpsBmp, 0);
                    OS.GpiSetBitmap (hpsBW, 0);

					/* Create the icon in system terms when appropriate */
                    if (srcImage.maskHandle == 0) {
                        info.hbmColor = handle;
                        info.hbmPointer = maskHandle;
                        int hIcon = OS.WinCreatePointerIndirect (OS.HWND_DESKTOP, info);
                        if (hIcon == 0) SWT.error(SWT.ERROR_NO_HANDLES);
                        OS.GpiDeleteBitmap (handle);
                        OS.GpiDeleteBitmap (maskHandle);
                        handle = hIcon;
                        maskHandle = 0;
                    }
					break;
				default:
					SWT.error(SWT.ERROR_UNSUPPORTED_FORMAT);
			}
            /* Free resources */
            dispose_compatible_GC (hpsBmp, null);
            dispose_compatible_GC (hpsBW, null);
            OS.GpiDeleteBitmap (hbmBW);

            /* Release the HPS for the device */	
            device.internal_dispose_GC (hPS, hPSData);

			if (device.tracking) device.new_Object(this);	
			return;
		}
		case SWT.IMAGE_GRAY: {
			ImageData data = srcImage.getImageData();
            int width = data.width;
            int height = data.height;
			PaletteData palette = data.palette;
			ImageData newData = data;
			if (!palette.isDirect) {
				/* Convert the palette entries to gray. */
				RGB [] rgbs = palette.getRGBs();
				for (int i=0; i<rgbs.length; i++) {
					if (data.transparentPixel != i) {
						RGB color = rgbs [i];
						int red = color.red;
						int green = color.green;
						int blue = color.blue;
						int intensity = (red+red+green+green+green+green+green+blue) >> 3;
						color.red = color.green = color.blue = intensity;
					}
				}
				newData.palette = new PaletteData(rgbs);
			} else {
				/* Create a 8 bit depth image data with a gray palette. */
				RGB[] rgbs = new RGB[256];
				for (int i=0; i<rgbs.length; i++) {
					rgbs[i] = new RGB(i, i, i);
				}
				newData = new ImageData(width, height, 8, new PaletteData(rgbs));
				newData.maskData = data.maskData;
				newData.maskPad = data.maskPad;
                
				/* Convert the pixels. */
				int[] scanline = new int[width];
				int redMask = palette.redMask;
				int greenMask = palette.greenMask;
				int blueMask = palette.blueMask;
				int redShift = palette.redShift;
				int greenShift = palette.greenShift;
				int blueShift = palette.blueShift;
				for (int y=0; y<height; y++) {
					int offset = y * newData.bytesPerLine;
					data.getPixels(0, y, width, scanline, 0);
					for (int x=0; x<width; x++) {
						int pixel = scanline[x];
                        int red = pixel & redMask;
                        red = (redShift < 0) ? red >>> -redShift : red << redShift;
                        int green = pixel & greenMask;
                        green = (greenShift < 0) ? green >>> -greenShift : green << greenShift;
                        int blue = pixel & blueMask;
                        blue = (blueShift < 0) ? blue >>> -blueShift : blue << blueShift;
                        newData.data[offset++] =
                            (byte)((red+red+green+green+green+green+green+blue) >> 3);
					}
				}
			}
			init (device, newData);
			if (device.tracking) device.new_Object(this);	
			return;
		}
		default:
			SWT.error(SWT.ERROR_INVALID_ARGUMENT);
	}
}

/**
 * Constructs an empty instance of this class with the
 * width and height of the specified rectangle. The result
 * may be drawn upon by creating a GC and using any of its
 * drawing operations, as shown in the following example:
 * <pre>
 *    Image i = new Image(device, boundsRectangle);
 *    GC gc = new GC(i);
 *    gc.drawRectangle(0, 0, 50, 50);
 *    gc.dispose();
 * </pre>
 * <p>
 * Note: Some platforms may have a limitation on the size
 * of image that can be created (size depends on width, height,
 * and depth). For example, Windows 95, 98, and ME do not allow
 * images larger than 16M.
 * </p>
 *
 * @param device the device on which to create the image
 * @param bounds a rectangle specifying the image's width and height (must not be null)
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if device is null and there is no current device</li>
 *    <li>ERROR_NULL_ARGUMENT - if the bounds rectangle is null</li>
 *    <li>ERROR_INVALID_ARGUMENT - if either the rectangle's width or height is negative</li>
 * </ul>
 * @exception SWTError <ul>
 *    <li>ERROR_NO_HANDLES if a handle could not be obtained for image creation</li>
 * </ul>
 */
public Image(Device device, Rectangle bounds) {
	if (device == null) device = Device.getDevice();
	if (device == null) SWT.error(SWT.ERROR_NULL_ARGUMENT);
	if (bounds == null) SWT.error(SWT.ERROR_NULL_ARGUMENT);
	init(device, bounds.width, bounds.height);
	if (device.tracking) device.new_Object(this);	
}

/**
 * Constructs an instance of this class from the given
 * <code>ImageData</code>.
 *
 * @param device the device on which to create the image
 * @param data the image data to create the image from (must not be null)
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if device is null and there is no current device</li>
 *    <li>ERROR_NULL_ARGUMENT - if the image data is null</li>
 * </ul>
 * @exception SWTError <ul>
 *    <li>ERROR_NO_HANDLES if a handle could not be obtained for image creation</li>
 * </ul>
 */
public Image(Device device, ImageData data) {
	if (device == null) device = Device.getDevice();
	if (device == null) SWT.error(SWT.ERROR_NULL_ARGUMENT);
	init(device, data);
	if (device.tracking) device.new_Object(this);	
}

/**
 * Constructs an instance of this class, whose type is 
 * <code>SWT.ICON</code>, from the two given <code>ImageData</code>
 * objects. The two images must be the same size, and the mask image
 * must have a color depth of 1. Pixel transparency in either image
 * will be ignored. If either image is an icon to begin with, an
 * exception is thrown.
 * <p>
 * The mask image should contain white wherever the icon is to be visible,
 * and black wherever the icon is to be transparent. In addition,
 * the source image should contain black wherever the icon is to be
 * transparent.
 * </p>
 *
 * @param device the device on which to create the icon
 * @param source the color data for the icon
 * @param mask the mask data for the icon
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if device is null and there is no current device</li>
 *    <li>ERROR_NULL_ARGUMENT - if either the source or mask is null </li>
 *    <li>ERROR_INVALID_ARGUMENT - if source and mask are different sizes or
 *          if the mask is not monochrome, or if either the source or mask
 *          is already an icon</li>
 * </ul>
 * @exception SWTError <ul>
 *    <li>ERROR_NO_HANDLES if a handle could not be obtained for image creation</li>
 * </ul>
 */
public Image(Device device, ImageData source, ImageData mask) {
	if (device == null) device = Device.getDevice();
	if (device == null) SWT.error(SWT.ERROR_NULL_ARGUMENT);
	if (source == null) SWT.error(SWT.ERROR_NULL_ARGUMENT);
	if (mask == null) SWT.error(SWT.ERROR_NULL_ARGUMENT);
	if (source.width != mask.width || source.height != mask.height) {
		SWT.error(SWT.ERROR_INVALID_ARGUMENT);
	}
	if (mask.depth != 1) {
		/*
		 *  Feature in OS/2. 1-bit bitmaps are buggy on OS/2 (see createBitmap()),
         *  so we create 4-bit ones instead when given a 1-bit ImageData. In
         *  order to allow users to draw on the masks, we must also support 4-bit
         *  masks in icon creation by converting them into 1-bit masks.
		 */
		if (mask.depth != 4) SWT.error(SWT.ERROR_INVALID_ARGUMENT);
		PaletteData palette = new PaletteData(new RGB[] {new RGB(0, 0, 0), new RGB(255,255,255)});
		ImageData tempMask = new ImageData(mask.width, mask.height, 1, palette);
		/* Find index of black in mask palette */
		RGB[] rgbs = mask.getRGBs();
		int blackIndex = 0;
		while (blackIndex < rgbs.length) {
			if (rgbs[blackIndex].equals(palette.colors[0])) break;
			blackIndex++;
		}
		if (blackIndex == rgbs.length) SWT.error(SWT.ERROR_INVALID_ARGUMENT);
		int[] pixels = new int[mask.width];
		for (int y = 0; y < mask.height; y++) {
			mask.getPixels(0, y, mask.width, pixels, 0);
			for (int i = 0; i < pixels.length; i++) {
				if (pixels[i] == blackIndex) {
					pixels[i] = 0;
				} else {
					pixels[i] = 1;
				}
			}
			tempMask.setPixels(0, y, mask.width, pixels, 0);
		}
		mask = tempMask;
	}
	/* Create a temporary image and locate the black pixel */
	ImageData image;
	int blackIndex = 0;
	if (source.palette.isDirect) {
		image = new ImageData(source.width, source.height, source.depth, source.palette);
	} else {
		RGB black = new RGB(0, 0, 0);
		RGB[] rgbs = source.getRGBs();
		if (source.transparentPixel != -1) {
			/*
			 * The source had transparency, so we can use the transparent pixel
			 * for black.
			 */
			RGB[] newRGBs = new RGB[rgbs.length];
			System.arraycopy(rgbs, 0, newRGBs, 0, rgbs.length);
			if (source.transparentPixel >= newRGBs.length) {
				/* Grow the palette with black */
				rgbs = new RGB[source.transparentPixel + 1];
				System.arraycopy(newRGBs, 0, rgbs, 0, newRGBs.length);
				for (int i = newRGBs.length; i <= source.transparentPixel; i++) {
					rgbs[i] = new RGB(0, 0, 0);
				}
			} else {
				newRGBs[source.transparentPixel] = black;
				rgbs = newRGBs;
			}
			blackIndex = source.transparentPixel;
			image = new ImageData(source.width, source.height, source.depth, new PaletteData(rgbs));
		} else {
			while (blackIndex < rgbs.length) {
				if (rgbs[blackIndex].equals(black)) break;
				blackIndex++;
			}
			if (blackIndex == rgbs.length) {
				/*
				 * We didn't find black in the palette, and there is no transparent
				 * pixel we can use.
				 */
				if ((1 << source.depth) > rgbs.length) {
					/* We can grow the palette and add black */
					RGB[] newRGBs = new RGB[rgbs.length + 1];
					System.arraycopy(rgbs, 0, newRGBs, 0, rgbs.length);
					newRGBs[rgbs.length] = black;
					rgbs = newRGBs;
				} else {
					/* No room to grow the palette */
					blackIndex = -1;
				}
			}
			image = new ImageData(source.width, source.height, source.depth, new PaletteData(rgbs));
		}
	}
	if (blackIndex == -1) {
		/* There was no black in the palette, so just copy the data over */
		System.arraycopy(source.data, 0, image.data, 0, image.data.length);
	} else {
		System.arraycopy(source.data, 0, image.data, 0, image.data.length);
		/* Modify the source image to contain black wherever the mask is 0 */
		int[] imagePixels = new int[image.width];
		int[] maskPixels = new int[mask.width];
		for (int y = 0; y < image.height; y++) {
			source.getPixels(0, y, image.width, imagePixels, 0);
			mask.getPixels(0, y, mask.width, maskPixels, 0);
			for (int i = 0; i < imagePixels.length; i++) {
				if (maskPixels[i] == 0) imagePixels[i] = blackIndex;
			}
			image.setPixels(0, y, source.width, imagePixels, 0);
		}
	}
	/*
	 * Make sure the mask is padded properly. OS/2 requires icon masks
	 * to have a scanline pad of 4.
	 */
	int bytesPerLine = ((mask.width + 31) / 32) * 4;
	byte[] newMaskData = new byte[bytesPerLine * mask.height];
	ImageData newMask = new ImageData(mask.width, mask.height, 1, mask.palette, 2, newMaskData);
	int[] maskPixels = new int[mask.width];
	for (int y = 0; y < mask.height; y++) {
		mask.getPixels(0, y, mask.width, maskPixels, 0);
//		System.out.println(getBits(maskPixels[0]) + getBits(maskPixels[1]) + getBits(maskPixels[2]) + getBits(maskPixels[3]));
		newMask.setPixels(0, y, newMask.width, maskPixels, 0);
	}
	/* Set the fields and create the icon */
	image.maskPad = newMask.scanlinePad;
	image.maskData = newMask.data;
	init(device, image);
	if (device.tracking) device.new_Object(this);	
}

/**
 * Constructs an instance of this class by loading its representation
 * from the specified input stream. Throws an error if an error
 * occurs while loading the image, or if the result is an image
 * of an unsupported type.
 * <p>
 * This constructor is provided for convenience when loading a single
 * image only. If the stream contains multiple images, only the first
 * one will be loaded. To load multiple images, use 
 * <code>ImageLoader.load()</code>.
 * </p><p>
 * This constructor may be used to load a resource as follows:
 * </p>
 * <pre>
 *     new Image(device, clazz.getResourceAsStream("file.gif"));
 * </pre>
 *
 * @param device the device on which to create the image
 * @param stream the input stream to load the image from
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if device is null and there is no current device</li>
 *    <li>ERROR_NULL_ARGUMENT - if the stream is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_INVALID_IMAGE - if the image file contains invalid data </li>
 *    <li>ERROR_IO - if an IO error occurs while reading data</li>
 * </ul>
 * @exception SWTError <ul>
 *    <li>ERROR_NO_HANDLES if a handle could not be obtained for image creation</li>
 * </ul>
 */
public Image (Device device, InputStream stream) {
	if (device == null) device = Device.getDevice();
	if (device == null) SWT.error(SWT.ERROR_NULL_ARGUMENT);
	init(device, new ImageData(stream));
	if (device.tracking) device.new_Object(this);	
}

/**
 * Constructs an instance of this class by loading its representation
 * from the file with the specified name. Throws an error if an error
 * occurs while loading the image, or if the result is an image
 * of an unsupported type.
 * <p>
 * This constructor is provided for convenience when loading
 * a single image only. If the specified file contains
 * multiple images, only the first one will be used.
 *
 * @param device the device on which to create the image
 * @param filename the name of the file to load the image from
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if device is null and there is no current device</li>
 *    <li>ERROR_NULL_ARGUMENT - if the file name is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_INVALID_IMAGE - if the image file contains invalid data </li>
 *    <li>ERROR_IO - if an IO error occurs while reading data</li>
 * </ul>
 * @exception SWTError <ul>
 *    <li>ERROR_NO_HANDLES if a handle could not be obtained for image creation</li>
 * </ul>
 */
public Image (Device device, String filename) {
	if (device == null) device = Device.getDevice();
	if (device == null) SWT.error(SWT.ERROR_NULL_ARGUMENT);
	init(device, new ImageData(filename));
	if (device.tracking) device.new_Object(this);	
}

/**
 *  Creates a bitmap.
 *
 *  @param width
 *      The width of the bitmap. If not zero the next argument should be
 *      not zero too. If zero the width of srcBitmap is used if srcBitmap is
 *      not zero.
 *  @param height
 *      The height of the bitmap. If not zero the previous argument should be
 *      not zero too. If zero the height of srcBitmap is used if srcBitmap is
 *      not zero.
 *  @param bitCount
 *      The number of bits per pixel. If not zero the next argument should be
 *      not zero too. If zero then high word of bitmapInfo[3] is used if
 *      bitmapInfo is not null, or the number of bits in srcBitmap if
 *      srcBitmap is not zero (the latter takes precedence). Otherwise
 *      driver-preferred bitCount value is used.
 *  @param planes
 *      The number of planes per pixel. If not zero the previous argument should be
 *      not zero too. If zero then low word of bitmapInfo[3] is used if
 *      bitmapInfo is not null, or number of planes in srcBitmap if
 *      srcBitmap is not zero (the latter takes precedence). Otherwise
 *      driver-preferred planes value is used.
 *  @param bitData
 *      The data to initialize the created bitmap with. If null the bitmap is
 *      filled with ROP_ONE value. Note that this and the next argument
 *      are overriden inside this method by data taken from srcBitmap if the
 *      latter is not zero.
 *  @param bitmapInfo
 *      Describes the data in bitData. Must not be null if bitData is not null.
 *  @param bitmapPS
 *      If not null and bitmapPS[0] is not zero the latter will be used
 *      as a PS to create a bitmap. Otherwise device will be used (so it must
 *      not be null) to create a PS, and if bitmapPS is not null, the created
 *      PS is returned to a caller in bitmapPS[0] -- it is the caller's
 *      responsibility to destroy this PS using dispose_compatible_GC() call.
 *      If bitmapPS is not null the created bitmap remains selected into
 *      the PS after this method is invoked.
 *  @param srcBitmap
 *      if not zero this bitmap is used to initialize the bitmap being created,
 *      bitData and bitmapInfo are ignored in this case.
 */
int createBitmap (
    int width, int height, int bitCount, int planes,
    byte[] bitData, int[] bitmapInfo,
    int[] bitmapPS, int srcBitmap
) {
    int memPS = bitmapPS == null ? 0 : bitmapPS [0];
    if (memPS == 0) {
        /* Get the HPS for the device */
        GCData hPSData = new GCData();
        int hPS = device.internal_new_GC (hPSData);
        memPS = new_compatible_GC (null, hPSData.hdc, width, height);
        /* Release the HPS for the device */	
        device.internal_dispose_GC (hPS, hPSData);
    }

    /* Override bitData and bitmapInfo if srcBitmap is not null */
    if (srcBitmap != 0) {
        BITMAPINFOHEADER2 bm = new BITMAPINFOHEADER2();
        OS.GpiQueryBitmapInfoHeader (srcBitmap, bm);
        /* Create the BITMAPINFO2 */
        int depth = bm.cBitCount * bm.cPlanes;
        int numColors = 0;
        if (depth <= 8) numColors = 1 << depth;
        final int bmi_cbFix = 4;
        bitmapInfo = new int [bmi_cbFix + numColors];
        bitmapInfo [0] = bmi_cbFix * 4; // cbFix
        bitmapInfo [3] = (depth << 16) | 1; // cPlanes, cBitCount
        /* Find the size of the image and allocate data */
        int imageSize = (((depth * bm.cx + 31) / 32) * 4) * bm.cy;
        bitData = new byte [imageSize];
        OS.GpiSetBitmap (memPS, srcBitmap);
        OS.GpiQueryBitmapBits (memPS, 0, bm.cy, bitData, bitmapInfo);
        OS.GpiSetBitmap (memPS, 0);
    }
    
    if (width == 0 || height == 0) {
        width = bitmapInfo [1];
        height = bitmapInfo [2];
    }

    if (bitCount == 0 || planes == 0) {
        if (bitData != null) {
            /*
             *  Bug in OS/2 (or maybe in SDD video drivers). Monochrome bitmaps are
             *  handled incorrectly:
             *  -   it's impossible to retrieve original colors of the monochrome
             *      bitmap (0x00000000 and 0x00FFFFFF are always returned);
             *  -   it's impossible to draw the transparent foreground without using
             *      an intermediate bitmap even if the device reports that it supports
             *      the BM_DESTTRANSPARENT background mix -- nothing is drawn in this
             *      mix mode.
             *  As a solution to the above we create a 4-bit image from the given
             *  1-bit image data.
             */
            if (bitmapInfo[3] == 0x00010001) {
                planes = 1;
                bitCount = 4;
            } else {
                planes = bitmapInfo[3] & 0xFFFF;
                bitCount = bitmapInfo[3] >>> 16;
            }
        } else {
            /* detect the best format */
            int[] formats = new int [2];
            OS.GpiQueryDeviceBitmapFormats (memPS, formats.length, formats);
            planes = formats [0];
            bitCount = formats [1];
        }
    }
    
	/* Create the bitmap */
    BITMAPINFOHEADER2 hdr = new BITMAPINFOHEADER2();
    hdr.cx = width;
    hdr.cy = height;
    hdr.cPlanes = (short) planes;
    hdr.cBitCount = (short) bitCount;
    hdr.ulCompression = OS.BCA_UNCOMP;
    hdr.cbImage = 0;
    hdr.cxResolution = 0;
    hdr.cyResolution = 0;
    hdr.cclrUsed = 0;
    hdr.cclrImportant = 0;
    hdr.usUnits = OS.BRU_METRIC;
    hdr.usReserved = 0;
    hdr.usRecording = OS.BRA_BOTTOMUP;
    hdr.usRendering = OS.BRH_NOTHALFTONED;
    hdr.cSize1 = 0;
    hdr.cSize2 = 0;
    hdr.ulColorEncoding = OS.BCE_RGB;
    hdr.ulIdentifier = 0;
	int hBitmap = OS.GpiCreateBitmap (
        memPS, hdr, bitData == null ? 0 : OS.CBM_INIT, bitData, bitmapInfo);
    if (hBitmap == 0) SWT.error(SWT.ERROR_NO_HANDLES);
    OS.GpiSetBitmap (memPS, hBitmap);
    /* fill the bitmap with white color */ 
    if (bitData == null) {
        if (device.hPalette != 0) {
            // white is the 15th element of the default palette set in Device.init()
            OS.GpiSetColor (memPS, 15);
            OS.GpiMove (memPS, new int[] {0, 0});
            OS.GpiBox (memPS, OS.DRO_FILL, new int[] {width, height}, 0, 0);
        } else {
            OS.GpiBitBlt (memPS, 0, 2, new int[] {0, 0, width, height}, OS.ROP_ONE, 0);
        }
    }
    
    if (bitmapPS == null) {
        dispose_compatible_GC (memPS, null);
    } else {
        bitmapPS [0] = memPS;
    }
    return hBitmap;
}

/**
 * Disposes of the operating system resources associated with
 * the image. Applications must dispose of all images which
 * they allocate.
 */
public void dispose () {
	if (handle == 0) return;
	if (type == SWT.ICON) {
        if (maskHandle == 0)
            OS.WinDestroyPointer (handle);
        else {
            OS.GpiDeleteBitmap (handle);
            OS.GpiDeleteBitmap (maskHandle);
        }
	} else {
        OS.GpiDeleteBitmap (handle);
	}
	handle = 0;
    //@@TODO(dmik): what to do if the memGC is not null
    // (i.e. this image has been selected into some PS)?
	memGC = null;
	if (device.tracking) device.dispose_Object(this);
	device = null;
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
	if (object == this) return true;
	if (!(object instanceof Image)) return false;
	Image image = (Image) object;
	return device == image.device && handle == image.handle;
}

/**
 * Returns the color to which to map the transparent pixel, or null if
 * the receiver has no transparent pixel.
 * <p>
 * There are certain uses of Images that do not support transparency
 * (for example, setting an image into a button or label). In these cases,
 * it may be desired to simulate transparency by using the background
 * color of the widget to paint the transparent pixels of the image.
 * Use this method to check which color will be used in these cases
 * in place of transparency. This value may be set with setBackground().
 * <p>
 *
 * @return the background color of the image, or null if there is no transparency in the image
 *
 * @exception SWTException <ul>
 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
 * </ul>
 */
public Color getBackground() {
	if (isDisposed()) SWT.error(SWT.ERROR_GRAPHIC_DISPOSED);
	if (transparentPixel == -1) return null;

	/* Compute the background color */
    int transRGB = 0;
    BITMAPINFOHEADER2 bm = new BITMAPINFOHEADER2();
    OS.GpiQueryBitmapInfoHeader (handle, bm);
    int depth = bm.cBitCount * bm.cPlanes;
    if (depth <= 8) {
        /* Get the HPS for the device */
        GCData hPSData = new GCData();
        int hPS = device.internal_new_GC (hPSData);

        int bmpPS = new_compatible_GC (null, hPSData.hdc, bm.cx, bm.cy);
        /* Release the HPS for the device */	
        device.internal_dispose_GC (hPS, hPSData);

        int numColors = 1 << depth;
        /* Create the BITMAPINFO2 */
        final int bmi_cbFix = 4;
        int[] bmi = new int[bmi_cbFix + numColors];
        bmi[0] = bmi_cbFix * 4; // cbFix
        bmi[3] = (bm.cBitCount << 16) | bm.cPlanes; // cPlanes, cBitCount
        OS.GpiSetBitmap (bmpPS, handle);
        OS.GpiQueryBitmapBits (bmpPS, 0, 0, null, bmi);
        int offset = bmi_cbFix + transparentPixel;
        transRGB = bmi [offset];
        dispose_compatible_GC (bmpPS, null);
    } else {
        switch (depth) {
            case 24:
            case 32:
                transRGB =
                    ((transparentPixel & 0xFF0000) >> 16) |
                    (transparentPixel & 0xFF00) |
                    ((transparentPixel & 0xFF) << 16);
                break;
            default:
                return null;
        }
    }

	int color = transRGB;
    if (device.hPalette != 0)
        color = OS.GpiQueryNearestPaletteIndex (device.hPalette, color);
    return Color.pm_new (device, color);
}

/**
 * Returns the bounds of the receiver. The rectangle will always
 * have x and y values of 0, and the width and height of the
 * image.
 *
 * @return a rectangle specifying the image's bounds
 *
 * @exception SWTException <ul>
 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_INVALID_IMAGE - if the image is not a bitmap or an icon</li>
 * </ul>
 */
public Rectangle getBounds() {
	if (isDisposed()) SWT.error(SWT.ERROR_GRAPHIC_DISPOSED);
    BITMAPINFOHEADER2 bm = new BITMAPINFOHEADER2();
	switch (type) {
		case SWT.BITMAP:
            OS.GpiQueryBitmapInfoHeader (handle, bm);
            return new Rectangle(0, 0, bm.cx, bm.cy);
		case SWT.ICON:
            int hbmColor = handle;
            int hbmPointer = maskHandle;
            if (hbmPointer == 0) {
                POINTERINFO info = new POINTERINFO ();
                OS.WinQueryPointerInfo (handle, info);
                hbmColor = info.hbmColor;
                hbmPointer = info.hbmPointer;
            }
            int hBitmap = hbmColor;
            if (hBitmap == 0) hBitmap = hbmPointer;
            OS.GpiQueryBitmapInfoHeader (hBitmap, bm);
            if (hBitmap == hbmPointer) bm.cy /= 2;
			return new Rectangle(0, 0, bm.cx, bm.cy);
		default:
			SWT.error(SWT.ERROR_UNSUPPORTED_FORMAT);
			return null;
	}
}

/**
 * Returns an <code>ImageData</code> based on the receiver
 * Modifications made to this <code>ImageData</code> will not
 * affect the Image.
 *
 * @return an <code>ImageData</code> containing the image's data and attributes
 *
 * @exception SWTException <ul>
 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_INVALID_IMAGE - if the image is not a bitmap or an icon</li>
 * </ul>
 *
 * @see ImageData
 */
public ImageData getImageData() {
	if (isDisposed()) SWT.error(SWT.ERROR_GRAPHIC_DISPOSED);
    BITMAPINFOHEADER2 bm = new BITMAPINFOHEADER2(); 
	int depth, width, height;
	switch (type) {
		case SWT.ICON: {
            int hbmColor = handle;
            int hbmPointer = maskHandle;
            if (hbmPointer == 0) {
                POINTERINFO info = new POINTERINFO ();
                OS.WinQueryPointerInfo (handle, info);
                hbmColor = info.hbmColor;
                hbmPointer = info.hbmPointer;
            }
			/* Get the basic BITMAP information */
            int hBitmap = hbmColor;
            if (hBitmap == 0) hBitmap = hbmPointer;
            OS.GpiQueryBitmapInfoHeader (hBitmap, bm);
            depth = bm.cBitCount * bm.cPlanes;
			width = bm.cx;
			if (hBitmap == hbmPointer) bm.cy /= 2;
			height = bm.cy;
			int numColors = 0;
			if (depth <= 8) numColors = 1 << depth;

            /* Create the BITMAPINFO2 */
            final int bmi_cbFix = 4;
            int[] bmi = new int [bmi_cbFix + numColors];
            // cbFix
            bmi [0] = bmi_cbFix * 4;
            // cPlanes, cBitCount
            bmi [3] = (depth << 16) | 1;

            /* Create an auxiliary PS and bitmap */
            int[] bitmapPS = new int [1];
            hBitmap = createBitmap (
                0, 0, 0, 0, null, null, bitmapPS, hBitmap);
            int bmpPS = bitmapPS [0];
            
            /* determine black and white colors */
            int cWhite = OS.CLR_WHITE;
            int cBlack = OS.CLR_BLACK;
            if (device.hPalette != 0) {
                cWhite = 15;
                cBlack = 0; 
            }
                
            /*
             *  Invert XORed icon pixels back to get SWT and Windows ICO
             *  compatible icon data (see comments inside the init() method).
             */
            OS.GpiSetColor (bmpPS, cWhite);
            OS.GpiSetBackColor (bmpPS, cBlack);
            OS.GpiWCBitBlt (bmpPS, hbmPointer, 4,
                new int[] {0, 0, width - 1, height - 1, 0, 0, width, height},
                OS.ROP_SRCINVERT, OS.BBO_IGNORE);
            
            /* Find the size of the image and allocate data */
            byte[] data = null;
            int imageSize = (((depth * width + 31) / 32) * 4) * height;
            data = new byte [imageSize];
            /* Get the bitmap data */
            OS.GpiQueryBitmapBits (bmpPS, 0, height, data, bmi);

            /*
             *  In OS/2, bitmap data starts from the bottom scanline, so do the
             *  conversion if requested.
             */
            int bpl = data.length / height;
            byte[] line = new byte [bpl];
            int topPtr = 0;
            int bottomPtr = data.length - bpl;
            while (topPtr < bottomPtr) {
                System.arraycopy (data, topPtr, line, 0, bpl);
                System.arraycopy (data, bottomPtr, data, topPtr, bpl);
                System.arraycopy (line, 0, data, bottomPtr, bpl);
                topPtr += bpl;
                bottomPtr -= bpl;
            }

            /* Calculate the palette */
            PaletteData palette = null;
            if (depth <= 8) {
                RGB[] rgbs = new RGB [numColors];
                int srcIndex = bmi_cbFix;
                for (int i = 0; i < numColors; i++) {
                    int rgb = bmi [srcIndex];
                    rgbs [i] = new RGB (
                        (rgb & 0xFF0000) >> 16, (rgb & 0xFF00) >> 8, rgb & 0xFF);
                    srcIndex ++;
                }
                palette = new PaletteData (rgbs);

            } else {
                if (depth == 24) {
                    palette = new PaletteData (0xFF, 0xFF00, 0xFF0000);
                } else if (depth == 32) {
                    palette = new PaletteData (0xFF00, 0xFF0000, 0xFF000000);
                } else {
                    SWT.error(SWT.ERROR_UNSUPPORTED_DEPTH);
                }
            }
            
            /* Do the mask (take only the inverted AND part) */
			byte[] maskData = null;
            int maskSize = (((width + 31) / 32) * 4) * height;
            maskData = new byte [maskSize];
            // cPlanes, cBitCount
            bmi [3] = 0x00010001;
            OS.GpiSetBitmap (bmpPS, hbmPointer);
            OS.GpiBitBlt (bmpPS, 0, 2,
                new int[] {0, height, width, height * 2}, OS.ROP_DSTINVERT, 0);
            OS.GpiQueryBitmapBits (bmpPS, height, height, maskData, bmi);
            OS.GpiBitBlt (bmpPS, 0, 2,
                new int[] {0, height, width, height * 2}, OS.ROP_DSTINVERT, 0);
            /* Flip data bottom to top */
            bpl = maskData.length / height;
            topPtr = 0;
            bottomPtr = maskData.length - bpl;
            while (topPtr < bottomPtr) {
                System.arraycopy (maskData, topPtr, line, 0, bpl);
                System.arraycopy (maskData, bottomPtr, maskData, topPtr, bpl);
                System.arraycopy (line, 0, maskData, bottomPtr, bpl);
                topPtr += bpl;
                bottomPtr -= bpl;
            }
            
            /* Clean up */
            dispose_compatible_GC (bmpPS, null);
            OS.GpiDeleteBitmap (hBitmap);

			/* Construct and return the ImageData */
			ImageData imageData = new ImageData (width, height, depth, palette, 4, data);
			imageData.maskData = maskData;
			imageData.maskPad = 4;
			return imageData;
		}
		case SWT.BITMAP: {
            OS.GpiQueryBitmapInfoHeader (handle, bm);
            width = bm.cx;
            height = bm.cy;
            depth = bm.cBitCount * bm.cPlanes;
            /*
             *  In OS/2 we only know for sure how 24-bit and 32-bit color data
             *  is stored, so convert the image if its color depth differs.
             *  Note, though, that the 32-bit color data format is just a
             *  suggestion, we rely on it only to keep the 32-bit image depth
             *  on systems which support it since it can speed up image
             *  processing in some cases.
             */
            if (depth > 8) {
                if (depth != 24) {
                    if (device.has32bitDepth) {
                        if (depth != 32) depth = 32;
                    } else {
                        depth = 24;
                    }
                }
            }
            /* Calculate number of colors */
            int numColors = 0;
            if (depth <= 8) {
                numColors = 1 << depth;
            }
            /* Create the BITMAPINFO2 */
            final int bmi_cbFix = 4;
            int[] bmi = new int [bmi_cbFix + numColors];
            // cbFix
            bmi [0] = bmi_cbFix * 4;
            // cPlanes, cBitCount
            bmi [3] = (depth << 16) | 1;

            boolean mustRestore = false;
            GC memGC = this.memGC;
            if (memGC != null && !memGC.isDisposed()) {
                mustRestore = true;
                OS.GpiSetBitmap (memGC.handle, 0);
            }
            
            /* Get the HPS for the device */
            GCData hpsData = new GCData();
            int hps = device.internal_new_GC (hpsData);
            /* Create the bitmap PS */
            int bmpPS = new_compatible_GC (null, hpsData.hdc, width, height);
            /* Release the HPS for the device */
            device.internal_dispose_GC (hps, hpsData);

            OS.GpiSetBitmap (bmpPS, handle);
            
            /* Find the size of the image and allocate data */
            byte[] data = null;
            int imageSize = (((depth * width + 31) / 32) * 4) * height;
            data = new byte [imageSize];
            /* Get the bitmap data */
            OS.GpiQueryBitmapBits (bmpPS, 0, height, data, bmi);
            
            /*
             *  In OS/2, bitmap data starts from the bottom scanline, so do the
             *  conversion.
             */
            int bpl = data.length / height;
            byte[] line = new byte [bpl];
            int topPtr = 0;
            int bottomPtr = data.length - bpl;
            while (topPtr < bottomPtr) {
                System.arraycopy (data, topPtr, line, 0, bpl);
                System.arraycopy (data, bottomPtr, data, topPtr, bpl);
                System.arraycopy (line, 0, data, bottomPtr, bpl);
                topPtr += bpl;
                bottomPtr -= bpl;
            }
            
            /* Clean up */
            dispose_compatible_GC (bmpPS, null);
            if (mustRestore) {
                OS.GpiSetBitmap (memGC.handle, handle);
            }

            /* Calculate the palette */
            PaletteData palette = null;
            if (depth <= 8) {
                RGB[] rgbs = new RGB [numColors];
                int srcIndex = bmi_cbFix;
                for (int i = 0; i < numColors; i++) {
                    int rgb = bmi [srcIndex];
                    rgbs [i] = new RGB (
                        (rgb & 0xFF0000) >> 16, (rgb & 0xFF00) >> 8, rgb & 0xFF);
                    srcIndex ++;
                }
                palette = new PaletteData (rgbs);
            } else {
                if (depth == 24) {
                    palette = new PaletteData (0xFF, 0xFF00, 0xFF0000);
                } else if (depth == 32) {
                    palette = new PaletteData (0xFF00, 0xFF0000, 0xFF000000);
                } else {
                    SWT.error(SWT.ERROR_UNSUPPORTED_DEPTH);
                }
            }

			/* Construct and return the ImageData */
			ImageData imageData = new ImageData(width, height, depth, palette, 4, data);
			imageData.transparentPixel = this.transparentPixel;
			imageData.alpha = alpha;
			if (alpha == -1 && alphaData != null) {
				imageData.alphaData = new byte [alphaData.length];
				System.arraycopy(alphaData, 0, imageData.alphaData, 0, alphaData.length);
			}
			return imageData;
		}
		default:
			SWT.error(SWT.ERROR_UNSUPPORTED_FORMAT);
			return null;
	}
}

/**
 * Returns an integer hash code for the receiver. Any two 
 * objects which return <code>true</code> when passed to 
 * <code>equals</code> must return the same value for this
 * method.
 *
 * @return the receiver's hash
 *
 * @see #equals
 */
public int hashCode () {
	return handle;
}

void init(Device device, int width, int height) {
	if (width <= 0 || height <= 0) {
		SWT.error (SWT.ERROR_INVALID_ARGUMENT);
	}
	this.device = device;
	type = SWT.BITMAP;
	handle = createBitmap (width, height, 0, 0, null, null, null, 0);
}

static int[] init(Device device, Image image, ImageData i) {

    if (image != null) image.device = device;

    /*
     *  There are 4 standard bitmap formats in OS/2: 1-, 4-, 8- and 24-bits
     *  per pixel (plus formats that are supported internally by the driver).
     *  First 3 are palette-based formats, the last is the direct color format.
     *  The 24-bit RGB data is always stored in most significant byte order
     *  and has a mask of (0xFF, 0xFF00, 0xFF0000) for red, green, blue
     *  respectively (i.e. the order of bytes is B, G, R). Make sure the image
     *  is OS/2-supported by converting image data to the nearest possible
     *  format without a loss of the color depth when possible.
	 */
	if (i.depth == 2) {
		ImageData img = new ImageData(i.width, i.height, 4, i.palette);
		ImageData.blit(ImageData.BLIT_SRC, i.data, i.depth, i.bytesPerLine, i.getByteOrder(), 
			0, 0, i.width, i.height, null, null, null,
			ImageData.ALPHA_OPAQUE, null, 0,
			img.data, img.depth, img.bytesPerLine, i.getByteOrder(),
			0, 0, img.width, img.height, null, null, null, 
			false, false);
		img.transparentPixel = i.transparentPixel;
		img.maskPad = i.maskPad;
		img.maskData = i.maskData;
		img.alpha = i.alpha;
		img.alphaData = i.alphaData;
		i = img;
	}
	if (i.palette.isDirect) {
		final PaletteData palette = i.palette;
		boolean convert = false;	
		switch (i.depth) {
		    case 24:
			if (palette.redMask == 0xFF &&
			    palette.greenMask == 0xFF00 &&
			    palette.blueMask == 0xFF0000)
			{
			    break;
			}
		    case 8:
		    case 16:
		    case 32:
			convert = true;
			break;
		    default:
			SWT.error(SWT.ERROR_UNSUPPORTED_DEPTH);
		}
			
		if (convert || i.getByteOrder() != ImageData.MSB_FIRST) {
			PaletteData newPalette = new PaletteData (0xFF, 0xFF00, 0xFF0000);
			ImageData img = new ImageData (i.width, i.height, 24, newPalette);
			ImageData.blit (ImageData.BLIT_SRC, 
			i.data, i.depth, i.bytesPerLine, i.getByteOrder(),
			0, 0, i.width, i.height,
			palette.redMask, palette.greenMask, palette.blueMask,
			ImageData.ALPHA_OPAQUE, null, 0,
			img.data, img.depth, img.bytesPerLine, ImageData.MSB_FIRST,
			0, 0, img.width, img.height,
			newPalette.redMask, newPalette.greenMask, newPalette.blueMask,
			false, false);
			if (i.transparentPixel != -1) {
				img.transparentPixel = newPalette.getPixel(palette.getRGB(i.transparentPixel));
			}
			img.maskPad = i.maskPad;
			img.maskData = i.maskData;
			img.alpha = i.alpha;
			img.alphaData = i.alphaData;
			i = img;
		}
    }

    /* Construct bitmap info header by hand */
    RGB[] rgbs = i.palette.getRGBs();
    int[] bmi = new int [BITMAPINFOHEADER2.int_sizeof +(i.palette.isDirect ? 0 : rgbs.length)];
    // cbFix
    bmi [0] = BITMAPINFOHEADER2.sizeof;
    // cx
    bmi [1] = i.width;
    // cy
    bmi [2] = i.height;
    // cPlanes, cBitCount
    bmi [3] = (i.depth << 16) | 1;
    // ulCompression
    bmi [4] = OS.BCA_UNCOMP;
    // cbImage
    bmi [5] = 0;
    // cxResolution
    bmi [6] = 0;
    // cyResolution
    bmi [7] = 0;
    // cclrUsed
    bmi [8] = rgbs == null ? 0 : rgbs.length;
    // cclrImportant
    bmi [9] = 0;
    // usUnits, usReserved = BRU_METRIC (0), 0
    bmi [10] = 0;
    // usRecording, usRendering = BRA_BOTTOMUP (0), BRH_NOTHALFTONED (0)
    bmi [11] = 0;
    // cSize1
    bmi [12] = 0;
    // cSize2
    bmi [13] = 0;
    // ulColorEncoding
    bmi [14] = OS.BCE_RGB;
    // ulIdentifier
    bmi [15] = 0;
    
    /* Set the rgb colors into the bitmap info */
    int offset = BITMAPINFOHEADER2.int_sizeof;
    if (!i.palette.isDirect) {
	for (int j = 0; j < rgbs.length; j++) {
            bmi[offset] = (rgbs[j].red << 16) | (rgbs[j].green << 8) | rgbs[j].blue;
	    offset++;
	}
    }
    
    /*
     *  In OS/2, bitmap data starts from the bottom scanline, so do the
     *  conversion. Also, in case of a scanline pad other than 4, pad data
     *  to this value.
     */
    int bpl = i.bytesPerLine;
    byte[] data = i.data;
    int newBpl = (i.scanlinePad != 4 && (bpl % 4 != 0)) ? bpl + (4 - (bpl % 4)) : bpl;
    byte[] newData = new byte[i.height * newBpl];
    int srcPtr = 0;
    int destPtr = newData.length - newBpl;
    for (int y = 0; y < i.height; y++) {
        System.arraycopy(data, srcPtr, newData, destPtr, bpl);
        srcPtr += bpl;
        destPtr -= newBpl;
    }
    data = newData;

    /* Do the same for the mask if it is an icon */
    byte[] maskData = i.maskData;
    if (i.getTransparencyType() == SWT.TRANSPARENCY_MASK) {
        bpl = ((i.width + 7) / 8 + (i.maskPad - 1)) / i.maskPad * i.maskPad;
        newBpl = (i.maskPad != 4 && (bpl % 4 != 0)) ? bpl + (4 - (bpl % 4)) : bpl;
        /* in OS/2 the mask always has double height (for XOR and AND patterns) */   
        newData = new byte[i.height * newBpl * 2];
        srcPtr = 0;
        destPtr = newData.length - newBpl;
        for (int y = 0; y < i.height; y++) {
            System.arraycopy(maskData, srcPtr, newData, destPtr, bpl);
            srcPtr += bpl;
            destPtr -= newBpl;
        }
        maskData = newData;
    }

    int[] bitmapPS = new int [1];
    int hBitmap = image.createBitmap (i.width, i.height, 0, 0, data, bmi, bitmapPS, 0);
    int bmpPS = bitmapPS [0];
    
    int[] result = null;
    if (i.getTransparencyType() == SWT.TRANSPARENCY_MASK) {
		/* Create the mask */
		bmi [2] *= 2;
		bmi [3] = 0x00010001;
		bmi [8] = 2;
		int hMask = image.createBitmap (i.width, i.height * 2, 1, 1, maskData, bmi, bitmapPS, 0);
		OS.GpiSetBitmap (bmpPS, hMask);
		/* invert icon mask to get AND mask */
		OS.GpiBitBlt (bmpPS, 0, 2, new int[] {0, i.height, i.width, i.height * 2}, OS.ROP_DSTINVERT, 0);
		/* determine black and white colors */
		int cWhite = OS.CLR_WHITE;
		int cBlack = OS.CLR_BLACK;
		if (device.hPalette != 0) {
			cWhite = 15;
			cBlack = 0; 
		}
		/*
		 *  Make the XOR mask by extracting non-zero icon pixels that are out of
		 *  the non-transparent area.
		 */
		OS.GpiSetColor (bmpPS, cWhite);
		OS.GpiSetBackColor (bmpPS, cBlack);
		OS.GpiWCBitBlt (bmpPS, hBitmap, 4, new int[] {0, 0, i.width - 1, i.height - 1, 0, 0, i.width, i.height}, OS.ROP_SRCCOPY, OS.BBO_IGNORE);
		OS.GpiBitBlt (bmpPS, bmpPS, 4, new int[] {0, 0, i.width, i.height, 0, i.height, i.width, i.height * 2}, OS.ROP_SRCAND, OS.BBO_IGNORE);
		/*
		 *  In OS/2 "inverted" icon pixels (those that invert colors of the
		 *  underlying screen area/bitmap) should be actually in black color
		 *  (not in white like in Windows) because OS/2 uses the separate
		 *  XOR mask (as defined above) which is applied after the AND mask is
		 *  applied and the icon is drawn using OR operation (in Windows the
		 *  AND mask is applied and then the icon is just drawn using XOR
		 *  operation). We should meet this requirement in order to create an
		 *  icon compatible with PM routines and having the same look like in
		 *  Windows -- so we simply invert these pixels. Inverting them also
		 *  gives the identical to Windows look for pixels in the
		 *  transparent area other than black, which can happen if the icon
		 *  has been created manually, not by the standard; however this only
		 *  works when the icon is drawn by SWT (for instance, when it has
		 *  non-standard size or when scaling), when drawn by standard PM
		 *  routines the result is different because of OR operation mentioned
		 *  above. 
		 */
		OS.GpiSetBitmap (bmpPS, hBitmap);
		OS.GpiWCBitBlt (bmpPS, hMask, 4, new int [] {0, 0, i.width - 1, i.height - 1, 0, 0, i.width, i.height}, OS.ROP_SRCINVERT, OS.BBO_IGNORE);
		OS.GpiSetBitmap (bmpPS, 0);
		if (image == null) {
			result = new int[] {hBitmap, hMask}; 
		} else {
			/* Create the icon */
		    int sysIconWidth = OS.WinQuerySysValue (OS.HWND_DESKTOP, OS.SV_CXICON);
		    int sysIconHeight = OS.WinQuerySysValue (OS.HWND_DESKTOP, OS.SV_CYICON);
		    int sysPtrWidth = OS.WinQuerySysValue (OS.HWND_DESKTOP, OS.SV_CXPOINTER);
		    int sysPtrHeight = OS.WinQuerySysValue (OS.HWND_DESKTOP, OS.SV_CYPOINTER);
		    int fPointer = -1;
		    /*
		     *  Bug in OS/2. When we query bitmap data from the bitmap handle that
		     *  is a part of an icon or pointer (i.e. it is obtained via the
		     *  WinQueryPointerInfo() call) OS/2 always returns the bitmap converted
		     *  to match the default system palette corresponding the icon's color
		     *  depth (so the "true" bitmap palette and bits used to create the icon
		     *  are lost). As a temporary solution the code below is commented
		     *  which prevents usage of OS/2 icons (pointers) at all -- every icon
		     *  in SWT just becomes a pair of bitmaps that are handled manually
		     *  by SWT drawing routines.
		     *
		     *  @@TODO (dmik): make it permanent? 
		     */
		    if (i.width == sysIconWidth) fPointer = 0;
		    else if (i.width == sysPtrWidth) fPointer = 1;
		    // @@TODO (lpino): Remove if not necessary in the future
/* 		    boolean isMini = false;
		    boolean needCenter = false;
		    switch(i.width){
		    	case 16:
				isMini = true;
				needCenter = true;
				break;
			case 20:
				isMini = true;
				break;
			case 32:
				needCenter = true;
				break;
			case 40:
				break;
		    }
 */		    if (fPointer >= 0) {
			POINTERINFO info = new POINTERINFO();
			info.fPointer = fPointer;
			info.hbmColor = hBitmap;
			info.hbmPointer = hMask;
			info.hbmMiniPointer = 0;
			info.hbmMiniColor = 0;
			int hIcon = OS.WinCreatePointerIndirect (OS.HWND_DESKTOP, info);
			if (hIcon == 0) SWT.error(SWT.ERROR_NO_HANDLES);
			OS.GpiDeleteBitmap (hBitmap);
			OS.GpiDeleteBitmap (hMask);
			image.handle = hIcon;			  		
 		    } else {
			image.handle = hBitmap;
			image.maskHandle = hMask;
		    }
		    image.type = SWT.ICON;
		}
	} else {
		if (image == null) {
		result = new int[] {hBitmap};
		} else {
			image.handle = hBitmap;
			image.type = SWT.BITMAP;
			image.transparentPixel = i.transparentPixel;
			if (image.transparentPixel == -1) {
				image.alpha = i.alpha;
				if (i.alpha == -1 && i.alphaData != null) {
					int length = i.alphaData.length;
					image.alphaData = new byte[length];
					System.arraycopy(i.alphaData, 0, image.alphaData, 0, length);
				}
			}
		}
	}
    image.dispose_compatible_GC (bmpPS, null);
	return result;
}

void init(Device device, ImageData i) {
	if (i == null) SWT.error(SWT.ERROR_NULL_ARGUMENT);
	init(device, this, i);
}

int new_compatible_GC (GCData data, int compatDC, int width, int height) {
    int hab = org.eclipse.swt.widgets.Display.internal_get_HAB ();
    int memDC = OS.DevOpenDC (
        hab, OS.OD_MEMORY, new PSZ ("*"), 4,
        new PSZ[] {new PSZ ("Display"), null, null, null}, compatDC);
	if (memDC == 0) SWT.error(SWT.ERROR_NO_HANDLES);
    int memPS = OS.GpiCreatePS (
        hab, memDC, new int[] {width, height},
        OS.PU_PELS | OS.GPIA_ASSOC | OS.GPIT_MICRO);
	if (memPS == 0) SWT.error(SWT.ERROR_NO_HANDLES);
    if (data != null) {
		/* Set the GCData fields */
        data.hdc = memDC;
	}
    /*
     *  Feature in OS/2. We should always select the device palette if we are
     *  on the palette-based device, otherwise we will get wrong colors
     *  when working with this PS.
     */
    int hPalette = device.hPalette; 
    if (hPalette != 0) OS.GpiSelectPalette (memPS, hPalette);
	return memPS;
}

void dispose_compatible_GC (int hps, GCData data) {
    int hdc;
    if (data != null) hdc = data.hdc;
    else hdc = OS.GpiQueryDevice (hps);
    OS.GpiDestroyPS (hps);
    OS.DevCloseDC (hdc);
}

/**	 
 * Invokes platform specific functionality to allocate a new GC handle.
 * <p>
 * <b>IMPORTANT:</b> This method is <em>not</em> part of the public
 * API for <code>Image</code>. It is marked public only so that it
 * can be shared within the packages provided by SWT. It is not
 * available on all platforms, and should never be called from
 * application code.
 * </p>
 *
 * @param data the platform specific GC data 
 * @return the platform specific GC handle
 *
 * @private
 */
public int internal_new_GC (GCData data) {
	if (handle == 0) SWT.error(SWT.ERROR_GRAPHIC_DISPOSED);
	/*
	* Create a new GC that can draw into the image.
	* Only supported for bitmaps.
	*/
	if (type != SWT.BITMAP || memGC != null) {
		SWT.error(SWT.ERROR_INVALID_ARGUMENT);
	}

    GCData tmpData = new GCData();
    int hps = device.internal_new_GC (tmpData);
    int imagePS = new_compatible_GC (data, tmpData.hdc, 0, 0);
    device.internal_dispose_GC(hps, tmpData);
	
    if (data != null) {
		/* Set the GCData fields */
		data.device = device;
		data.image = this;
		data.hFont = device.systemFont();
	}
    return imagePS;
}

/**	 
 * Invokes platform specific functionality to dispose a GC handle.
 * <p>
 * <b>IMPORTANT:</b> This method is <em>not</em> part of the public
 * API for <code>Image</code>. It is marked public only so that it
 * can be shared within the packages provided by SWT. It is not
 * available on all platforms, and should never be called from
 * application code.
 * </p>
 *
 * @param handle the platform specific GC handle
 * @param data the platform specific GC data 
 *
 * @private
 */
public void internal_dispose_GC (int hps, GCData data) {
    dispose_compatible_GC (hps, data);
}

/**
 * Returns <code>true</code> if the image has been disposed,
 * and <code>false</code> otherwise.
 * <p>
 * This method gets the dispose state for the image.
 * When an image has been disposed, it is an error to
 * invoke any other method using the image.
 *
 * @return <code>true</code> when the image is disposed and <code>false</code> otherwise
 */
public boolean isDisposed() {
	return handle == 0;
}

/**
 *  Refreshes the palette-based bitmap by querying its data and then setting it
 *  back. This is necessary after some drawing has taken place on it because we
 *  draw on any bitmap in LCOLF_RGB mode when the device is in direct color
 *  mode -- in such case OS/2 draws on the bitmap using direct RGB colors, not
 *  its palette, and as a result we can get colors that are missing in the
 *  bitmap palette. Resetting bitmap data solves this problem.
 *
 *  This method assumes that the memGC field contains the valid GC currently
 *  used to draw on this image.
 */
void refreshBitmap () {
    BITMAPINFOHEADER2 bm = new BITMAPINFOHEADER2();
    OS.GpiQueryBitmapInfoHeader (handle, bm);
    int depth = bm.cBitCount * bm.cPlanes;
    if (depth > 8) return;
    int numColors = 1 << depth;
    final int bmi_cbFix = 4;
    int[] bmi = new int [bmi_cbFix + numColors];
    bmi [0] = bmi_cbFix * 4;
    bmi [3] = depth << 16 | 1;
    byte[] bitData = new byte [(((depth * bm.cx + 31) / 32) * 4) * bm.cy];
    OS.GpiQueryBitmapBits (memGC.handle, 0, bm.cx, bitData, bmi);
    OS.GpiSetBitmapBits (memGC.handle, 0, bm.cx, bitData, bmi);
}

/**
 * Sets the color to which to map the transparent pixel.
 * <p>
 * There are certain uses of <code>Images</code> that do not support
 * transparency (for example, setting an image into a button or label).
 * In these cases, it may be desired to simulate transparency by using
 * the background color of the widget to paint the transparent pixels
 * of the image. This method specifies the color that will be used in
 * these cases. For example:
 * <pre>
 *    Button b = new Button();
 *    image.setBackground(b.getBackground());>
 *    b.setImage(image);
 * </pre>
 * </p><p>
 * The image may be modified by this operation (in effect, the
 * transparent regions may be filled with the supplied color).  Hence
 * this operation is not reversible and it is not legal to call
 * this function twice or with a null argument.
 * </p><p>
 * This method has no effect if the receiver does not have a transparent
 * pixel value.
 * </p>
 *
 * @param color the color to use when a transparent pixel is specified
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the color is null</li>
 *    <li>ERROR_INVALID_ARGUMENT - if the color has been disposed</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
 * </ul>
 */
public void setBackground(Color color) {
	if (isDisposed()) SWT.error(SWT.ERROR_GRAPHIC_DISPOSED);
	if (color == null) SWT.error(SWT.ERROR_NULL_ARGUMENT);
	if (color.isDisposed()) SWT.error(SWT.ERROR_INVALID_ARGUMENT);
	if (transparentPixel == -1) return;

    int transRGB = 0;
    BITMAPINFOHEADER2 bm = new BITMAPINFOHEADER2();
    OS.GpiQueryBitmapInfoHeader (handle, bm);
    int depth = bm.cBitCount * bm.cPlanes;

    /* Get the HPS for the device */
    GCData hPSData = new GCData();
    int hPS = device.internal_new_GC (hPSData);

    int bmpPS = new_compatible_GC (null, hPSData.hdc, bm.cx, bm.cy);
    /* Release the HPS for the device */	
    device.internal_dispose_GC (hPS, hPSData);

    int numColors = 0;
    if (depth <= 8) numColors = 1 << depth;

    /* Create the BITMAPINFO2 */
    final int bmi_cbFix = 4;
    int[] bmi = new int[bmi_cbFix + numColors];
    bmi[0] = bmi_cbFix * 4; // cbFix
    bmi[3] = (bm.cBitCount << 16) | bm.cPlanes; // cPlanes, cBitCount
    int bytesPerLine = (((depth * bm.cx + 31) / 32) * 4);
    byte[] data = new byte [bytesPerLine * bm.cy];
    OS.GpiSetBitmap (bmpPS, handle);
    OS.GpiQueryBitmapBits (bmpPS, 0, bm.cy, data, bmi);

    if (depth <= 8) {
        int newTransRGB = (color.getRed() << 16) | (color.getRed() << 8) | color.getBlue();
        int offset = bmi_cbFix + transparentPixel;
        bmi [offset] = newTransRGB;
    } else {
        int newTransPixel =
            color.getRed() | (color.getGreen() << 8) | (color.getBlue() << 16); 
        ImageData id = new ImageData (
            bm.cx, bm.cy, depth, new PaletteData (0xFF, 0xFF00, 0xFF0000), 4, data);
        int[] pixels = new int [bm.cx];
        for (int y = 0; y < bm.cy; y++) {
            id.getPixels (0, y, bm.cx, pixels, 0);
            for (int x = 0; x < bm.cx; x++) {
                if (pixels [x] == transparentPixel) pixels [x] = newTransPixel;
            }
            id.setPixels (0, y, bm.cx, pixels, 0);
        }
        transparentPixel = newTransPixel;
    }

    OS.GpiSetBitmapBits (bmpPS, 0, bm.cy, data, bmi);
    dispose_compatible_GC (bmpPS, null);
}

/**
 * Returns a string containing a concise, human-readable
 * description of the receiver.
 *
 * @return a string representation of the receiver
 */
public String toString () {
	if (isDisposed()) return "Image {*DISPOSED*}";
	return "Image {" + handle + "}";
}

/**	 
 * Invokes platform specific functionality to allocate a new image.
 * <p>
 * <b>IMPORTANT:</b> This method is <em>not</em> part of the public
 * API for <code>Image</code>. It is marked public only so that it
 * can be shared within the packages provided by SWT. It is not
 * available on all platforms, and should never be called from
 * application code.
 * </p>
 *
 * @param device the device on which to allocate the color
 * @param type the type of the image (<code>SWT.BITMAP</code> or <code>SWT.ICON</code>)
 * @param handle the OS handle for the image
 * @param hPalette the OS handle for the palette, or 0
 *
 * @private
 */
public static Image pm_new(Device device, int type, int handle) {
	if (device == null) device = Device.getDevice();
	Image image = new Image();
	image.type = type;
	image.handle = handle;
	image.device = device;
	return image;
}

private String getBits(int value){
	int displayMask = 1 << 31;
	StringBuffer buf = new StringBuffer(35);
	for(int c= 1; c<=32; c++){
		buf.append((value & displayMask) == 0 ? '0' : '1' );
		value <<=1;
		if(c %8 == 0)
			buf.append(' ');
	}
	return buf.toString();
}

}
