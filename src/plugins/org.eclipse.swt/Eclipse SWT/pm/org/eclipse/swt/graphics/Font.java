package org.eclipse.swt.graphics;

/*
 *  OS/2 version.
 *  Copyright (c) 2002, 2004 EclipseOS2 Team.
 */

/*
 *  Copyright (c) 2000, 2002 IBM Corp.  All rights reserved.
 *  This file is made available under the terms of the Common Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/cpl-v10.html
 */

import org.eclipse.swt.internal.pm.*;
import org.eclipse.swt.*;

/**
 * Instances of this class manage operating system resources that
 * define how text looks when it is displayed. Fonts may be constructed
 * by providing a device and either name, size and style information
 * or a <code>FontData</code> object which encapsulates this data.
 * <p>
 * Application code must explicitly invoke the <code>Font.dispose()</code> 
 * method to release the operating system resources managed by each instance
 * when those instances are no longer required.
 * </p>
 *
 * @see FontData
 */

public final class Font {

	/**
	 * the handle to the OS font resource
	 * (Warning: This field is platform dependent)
	 */
    private FATTRS handle;

    private long handleStamp;
	
	/**
	 * the device where this font was created
	 */
	private Device device;
	
/**
 * Prevents uninitialized instances from being created outside the package.
 */
Font() {
}

/**	 
 * Constructs a new font given a device and font data
 * which describes the desired font's appearance.
 * <p>
 * You must dispose the font when it is no longer required. 
 * </p>
 *
 * @param device the device to create the font on
 * @param fd the FontData that describes the desired font (must not be null)
 * 
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if device is null and there is no current device</li>
 *    <li>ERROR_NULL_ARGUMENT - if the fd argument is null</li>
 * </ul>
 * @exception SWTError <ul>
 *    <li>ERROR_NO_HANDLES - if a font could not be created from the given font data</li>
 * </ul>
 */
public Font(Device device, FontData fd) {
	if (device == null) device = Device.getDevice();
	if (device == null) SWT.error(SWT.ERROR_NULL_ARGUMENT);
	init(device, fd);
	if (device.tracking) device.new_Object(this);	
}

/**	 
 * Constructs a new font given a device, a font name,
 * the height of the desired font in points, and a font
 * style.
 * <p>
 * You must dispose the font when it is no longer required. 
 * </p>
 *
 * @param device the device to create the font on
 * @param name the name of the font (must not be null)
 * @param height the font height in points
 * @param style a bit or combination of NORMAL, BOLD, ITALIC
 * 
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if device is null and there is no current device</li>
 *    <li>ERROR_NULL_ARGUMENT - if the name argument is null</li>
 *    <li>ERROR_INVALID_ARGUMENT - if the height is negative</li>
 * </ul>
 * @exception SWTError <ul>
 *    <li>ERROR_NO_HANDLES - if a font could not be created from the given arguments</li>
 * </ul>
 */
public Font(Device device, String name, int height, int style) {
	if (device == null) device = Device.getDevice();
	if (device == null) SWT.error(SWT.ERROR_NULL_ARGUMENT);
	if (name == null) SWT.error(SWT.ERROR_NULL_ARGUMENT);
	init(device, new FontData (name, height, style));
	if (device.tracking) device.new_Object(this);	
}

/**
 * Disposes of the operating system resources associated with
 * the font. Applications must dispose of all fonts which
 * they allocate.
 */
public void dispose() {
    if (handle == null) return;
    handle = null;
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
public boolean equals(Object object) {
	if (object == this) return true;
	if (!(object instanceof Font)) return false;
	Font font = (Font) object;
    if (device != font.device) return false;
    if (handle == null || font.handle == null) return handle == font.handle;
    /*
    * On Windows two fonts are equal when they have the same HFONT, and
    * Control.getFont() gives the HFONT of the control each time. Here the
    * handle is a FATTRS that getFont() makes anew each time, so the fonts
    * are compared by what the FATTRS describes (difference 021).
    */
    return FontData.pm_new (handle).equals (FontData.pm_new (font.handle));
}

/**
 * Returns an array of <code>FontData</code>s representing the receiver.
 * On Windows, only one FontData will be returned per font. On X however, 
 * a <code>Font</code> object <em>may</em> be composed of multiple X 
 * fonts. To support this case, we return an array of font data objects.
 *
 * @return an array of font data objects describing the receiver
 *
 * @exception SWTException <ul>
 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
 * </ul>
 */
public FontData[] getFontData() {
	if (isDisposed()) SWT.error(SWT.ERROR_GRAPHIC_DISPOSED);
    return new FontData[] {FontData.pm_new (handle)};
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
    if (handle == null) return 0;
    return FontData.pm_new (handle).hashCode ();
}

void init (Device device, FontData fd) {
	if (fd == null) SWT.error(SWT.ERROR_NULL_ARGUMENT);
	this.device = device;
    handle = new FATTRS();
    handle.fm_szFamilyname = fd.name;
    handle.height = fd.height;
    handle.fm_usWeightClass = (short) ((fd.style & SWT.BOLD) != 0 ? 7 : 5);
    handle.fm_fsSelection = (short) ((fd.style & SWT.ITALIC) != 0 ? OS.FM_SEL_ITALIC : 0);
    System.arraycopy (
        fd.szFacename.getBytes(), 0, handle.szFacename, 0,
        Math.min (OS.FACESIZE - 1, fd.szFacename.length())
    );
    handle.fsSelection = fd.fsSelection;
    handleStamp = device.matchFont (handle, true);
}

/**
 * Returns the platform dependent handle of the receiver.
 * <p>
 * <b>IMPORTANT:</b> This method is <em>not</em> part of the public
 * API for <code>Control</code>. It is marked public only so that it
 * can be shared within the packages provided by SWT. It is not
 * available on all platforms, and should never be called from
 * application code.
 * </p>
 *
 * @return the platform dependent font handle
 *
 * @private
 */
public FATTRS internal_get_handle() {
    handleStamp = device.matchFont (handle, false);
    return handle;
}

/**
 * Returns <code>true</code> if the font has been disposed,
 * and <code>false</code> otherwise.
 * <p>
 * This method gets the dispose state for the font.
 * When a font has been disposed, it is an error to
 * invoke any other method using the font.
 *
 * @return <code>true</code> when the font is disposed and <code>false</code> otherwise
 */
public boolean isDisposed() {
    return handle == null;
}

/**
 * Returns a string containing a concise, human-readable
 * description of the receiver.
 *
 * @return a string representation of the receiver
 */
public String toString () {
	if (isDisposed()) return "Font {*DISPOSED*}";
	return "Font {" + hashCode() + "}";
}

/**	 
 * Invokes platform specific functionality to allocate a new font.
 * <p>
 * <b>IMPORTANT:</b> This method is <em>not</em> part of the public
 * API for <code>Font</code>. It is marked public only so that it
 * can be shared within the packages provided by SWT. It is not
 * available on all platforms, and should never be called from
 * application code.
 * </p>
 *
 * @param device the device on which to allocate the color
 * @param handle the handle for the font
 * 
 * @private
 */
public static Font pm_new(Device device, FATTRS fa) {
	if (device == null) device = Device.getDevice();
	Font font = new Font();
	font.device = device;
    font.handle = fa;
    font.handleStamp = device.fontCacheStamp;
	return font;
}

}

