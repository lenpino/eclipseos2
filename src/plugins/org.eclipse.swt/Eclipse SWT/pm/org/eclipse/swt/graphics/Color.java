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

import org.eclipse.swt.internal.pm.*;
import org.eclipse.swt.*;

/**
 * Instances of this class manage the operating system resources that
 * implement SWT's RGB color model. To create a color you can either
 * specify the individual color components as integers in the range 
 * 0 to 255 or provide an instance of an <code>RGB</code>. 
 * <p>
 * Application code must explicitly invoke the <code>Color.dispose()</code> 
 * method to release the operating system resources managed by each instance
 * when those instances are no longer required.
 * </p>
 *
 * @see RGB
 * @see Device#getSystemColor
 */

public final class Color {
	
	/**
	 * the handle to the OS color resource 
	 * (Warning: This field is platform dependent)
	 */
	public int handle;
	
	/**
	 *  for palette-based devices this contains the RGB value of the color
     *  because the handle is occupied by the index in the device palette;
     *  for other devices both contain the RGB value
	 */
    int rgb;

    /**
	 * the device where this color was created
	 */
	Device device;

/**
 * Prevents uninitialized instances from being created outside the package.
 */
Color() {	
}

/**	 
 * Constructs a new instance of this class given a device and the
 * desired red, green and blue values expressed as ints in the range
 * 0 to 255 (where 0 is black and 255 is full brightness). On limited
 * color devices, the color instance created by this call may not have
 * the same RGB values as the ones specified by the arguments. The
 * RGB values on the returned instance will be the color values of 
 * the operating system color.
 * <p>
 * You must dispose the color when it is no longer required. 
 * </p>
 *
 * @param device the device on which to allocate the color
 * @param red the amount of red in the color
 * @param green the amount of green in the color
 * @param blue the amount of blue in the color
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if device is null and there is no current device</li>
 *    <li>ERROR_INVALID_ARGUMENT - if the red, green or blue argument is not between 0 and 255</li>
 * </ul>
 *
 * @see #dispose
 */
public Color (Device device, int red, int green, int blue) {
	if (device == null) device = Device.getDevice();
	if (device == null) SWT.error(SWT.ERROR_NULL_ARGUMENT);
	init(device, red, green, blue);
	if (device.tracking) device.new_Object(this);
}

/**	 
 * Constructs a new instance of this class given a device and an
 * <code>RGB</code> describing the desired red, green and blue values.
 * On limited color devices, the color instance created by this call
 * may not have the same RGB values as the ones specified by the
 * argument. The RGB values on the returned instance will be the color
 * values of the operating system color.
 * <p>
 * You must dispose the color when it is no longer required. 
 * </p>
 *
 * @param device the device on which to allocate the color
 * @param RGB the RGB values of the desired color
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if device is null and there is no current device</li>
 *    <li>ERROR_NULL_ARGUMENT - if the rgb argument is null</li>
 *    <li>ERROR_INVALID_ARGUMENT - if the red, green or blue components of the argument are not between 0 and 255</li>
 * </ul>
 *
 * @see #dispose
 */
public Color (Device device, RGB rgb) {
	if (device == null) device = Device.getDevice();
	if (device == null) SWT.error(SWT.ERROR_NULL_ARGUMENT);
	if (rgb == null) SWT.error(SWT.ERROR_NULL_ARGUMENT);
	init(device, rgb.red, rgb.green, rgb.blue);
	if (device.tracking) device.new_Object(this);
}

/**
 * Disposes of the operating system resources associated with
 * the color. Applications must dispose of all colors which
 * they allocate.
 */
public void dispose() {
	if (handle == -1) return;
	/*
	 * If this is a palette-based device,
	 * Decrease the reference count for this color.
	 * If the reference count reaches 0, the slot may
	 * be reused when another color is allocated.
	 */
	int hPal = device.hPalette;
	if (hPal != 0) {
		int[] colorRefCount = device.colorRefCount;
		if (colorRefCount[handle] > 0) {
			colorRefCount[handle]--;
		}
	}
	handle = -1;
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
	if (!(object instanceof Color)) return false;
	Color color = (Color) object;
	return device == color.device &&
        handle == color.handle && (rgb & 0xFFFFFF) == (color.rgb & 0xFFFFFF);
}

/**
 * Returns the amount of blue in the color, from 0 to 255.
 *
 * @return the blue component of the color
 *
 * @exception SWTException <ul>
 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
 * </ul>
 */
public int getBlue () {
	if (isDisposed()) SWT.error(SWT.ERROR_GRAPHIC_DISPOSED);
	return rgb & 0xFF;
}

/**
 * Returns the amount of green in the color, from 0 to 255.
 *
 * @return the green component of the color
 *
 * @exception SWTException <ul>
 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
 * </ul>
 */
public int getGreen () {
	if (isDisposed()) SWT.error(SWT.ERROR_GRAPHIC_DISPOSED);
	return (rgb & 0xFF00) >> 8 ;
}

/**
 * Returns the amount of red in the color, from 0 to 255.
 *
 * @return the red component of the color
 *
 * @exception SWTException <ul>
 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
 * </ul>
 */
public int getRed () {
	if (isDisposed()) SWT.error(SWT.ERROR_GRAPHIC_DISPOSED);
	return (rgb & 0xFF0000) >> 16;
}

/**
 * Returns an <code>RGB</code> representing the receiver.
 *
 * @exception SWTException <ul>
 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
 * </ul>
 */
public RGB getRGB () {
	if (isDisposed()) SWT.error(SWT.ERROR_GRAPHIC_DISPOSED);
	return new RGB((rgb & 0xFF0000) >> 16, (rgb & 0xFF00) >> 8, rgb & 0xFF);
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
	return rgb;
}

/**
 * Allocates the operating system resources associated 
 * with the receiver.
 *
 * @param device the device on which to allocate the color
 * @param red the amount of red in the color
 * @param green the amount of green in the color
 * @param blue the amount of blue in the color
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_INVALID_ARGUMENT - if the red, green or blue argument is not between 0 and 255</li>
 * </ul>
 *
 * @see #dispose
 */
void init(Device device, int red, int green, int blue) {
	if (red > 255 || red < 0 || green > 255 || green < 0 || blue > 255 || blue < 0) {
		SWT.error(SWT.ERROR_INVALID_ARGUMENT);
	}
	this.device = device;
	rgb = ((red & 0xFF) << 16) | ((green & 0xFF) << 8) | (blue & 0xFF);
    handle = rgb;
	
	/* If this is not a palette-based device, return */
	int hPal = device.hPalette;
	if (hPal == 0) return;
	
	int[] colorRefCount = device.colorRefCount;
	/* Add this color to the default palette now */
	/* First find out if the color already exists */
    handle = OS.GpiQueryNearestPaletteIndex (hPal, rgb);
	/* See if the nearest color actually is the color */
    int [] entry = new int [1];
    OS.GpiQueryPaletteInfo (hPal, OS.NULLHANDLE, 0, handle, 1, entry);
    if ((entry[0] & 0xFFFFFF) == (rgb & 0xFFFFFF)) {
			/* Found the color. Increment the ref count and return */
			colorRefCount[handle]++;
			return;
	}
	/* Didn't find the color, allocate it now. Find the first free entry */
	int i = 0;
	while (i < colorRefCount.length) {
		if (colorRefCount[i] == 0) {
			handle = i;
			break;
		}
		i++;
	}
	if (i == colorRefCount.length) {
		/* No free entries, use the closest one */
		/* Remake the handle from the actual rgbs */
		rgb = entry[0];
	} else {
		/* Found a free entry */
		entry[0] = rgb;
        OS.GpiSetPaletteEntries (hPal, OS.LCOLF_CONSECRGB, handle, 1, entry);
        /*
         *  In fact, that the color can be created inside the paint listener,
         *  when the palette is already selected into the widget's hps and will
         *  not be properly updated with the new color until the next paint
         *  event resulting in a wrong color displayed.
         *  To fix this, we "broadcast" the following message, which is handled
         *  insinde the main message loop in the Display class and causes all
         *  Shell widgets to select and realize the new palette and to
         *  invalidate themselves.
         */
        OS.WinPostMsg (OS.NULLHANDLE, OS.WM_REALIZEPALETTE, 0, 0);
	}
	colorRefCount[handle]++;
}

/**
 * Returns <code>true</code> if the color has been disposed,
 * and <code>false</code> otherwise.
 * <p>
 * This method gets the dispose state for the color.
 * When a color has been disposed, it is an error to
 * invoke any other method using the color.
 *
 * @return <code>true</code> when the color is disposed and <code>false</code> otherwise
 */
public boolean isDisposed() {
	return handle == -1;
}

/**
 * Returns a string containing a concise, human-readable
 * description of the receiver.
 *
 * @return a string representation of the receiver
 */
public String toString () {
	if (isDisposed()) return "Color {*DISPOSED*}";
	return "Color {" + getRed() + ", " + getGreen() + ", " + getBlue() + "}";
}

/**	 
 * Invokes platform specific functionality to allocate a new color.
 * <p>
 * <b>IMPORTANT:</b> This method is <em>not</em> part of the public
 * API for <code>Color</code>. It is marked public only so that it
 * can be shared within the packages provided by SWT. It is not
 * available on all platforms, and should never be called from
 * application code.
 * </p>
 *
 * @param device the device on which to allocate the color
 * @param handle the handle for the color
 * 
 * @private
 */
public static Color pm_new(Device device, int handle) {
	if (device == null) device = Device.getDevice();
	Color color = new Color();
	color.handle = handle;
    int rgb = handle;
    int hPal = device.hPalette;
    if (handle < 0) {
        rgb = OS.WinQuerySysColor (OS.HWND_DESKTOP, handle, 0);
        if (hPal != 0)
            handle = OS.GpiQueryNearestPaletteIndex (hPal, rgb);
    }
    if (hPal != 0) {
        int[] entry = new int [1];
        OS.GpiQueryPaletteInfo (hPal, OS.NULLHANDLE, 0, handle, 1, entry);
        rgb = entry[0] & 0xFFFFFF;
    }
    color.rgb = rgb;
	color.device = device;
	return color;
}

}
