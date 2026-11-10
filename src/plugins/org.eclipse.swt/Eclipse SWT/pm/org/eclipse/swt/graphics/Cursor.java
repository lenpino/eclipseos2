package org.eclipse.swt.graphics;

/*
 * Copyright (c) 2000, 2026 IBM Corp. and the EclipseOS2 Team.  All rights reserved.
 * This file is made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 */

import org.eclipse.swt.internal.pm.*;
import org.eclipse.swt.*;

/**
 * Instances of this class manage operating system resources that
 * specify the appearance of the on-screen pointer. To create a
 * cursor you specify the device and either a simple cursor style
 * describing one of the standard operating system provided cursors
 * or the image and mask data for the desired appearance.
 * <p>
 * Application code must explicitly invoke the <code>Cursor.dispose()</code> 
 * method to release the operating system resources managed by each instance
 * when those instances are no longer required.
 * </p>
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>
 *   CURSOR_ARROW, CURSOR_WAIT, CURSOR_CROSS, CURSOR_APPSTARTING, CURSOR_HELP,
 *   CURSOR_SIZEALL, CURSOR_SIZENESW, CURSOR_SIZENS, CURSOR_SIZENWSE, CURSOR_SIZEWE,
 *   CURSOR_SIZEN, CURSOR_SIZES, CURSOR_SIZEE, CURSOR_SIZEW, CURSOR_SIZENE, CURSOR_SIZESE,
 *   CURSOR_SIZESW, CURSOR_SIZENW, CURSOR_UPARROW, CURSOR_IBEAM, CURSOR_NO, CURSOR_HAND
 * </dd>
 * </dl>
 * <p>
 * Note: Only one of the above styles may be specified.
 * </p>
 */

public final class Cursor {
	
	/**
	 * the handle to the OS cursor resource
	 * (Warning: This field is platform dependent)
	 */
	public int handle;
	
	/**
	 * the device where this cursor was created
	 */
	Device device;

	/*
	 * The system pointers (WinQuerySysPointer) belong to PM and
	 * must not be destroyed; the ones created here must.
	 */
	boolean created;
	
	/**
	 * data used to create a HAND cursor.
	 */
	static final byte[] HAND_SOURCE = {
		(byte)0xf9,(byte)0xff,(byte)0xff,(byte)0xff,
		(byte)0xf0,(byte)0xff,(byte)0xff,(byte)0xff,
		(byte)0xf0,(byte)0xff,(byte)0xff,(byte)0xff,
		(byte)0xf0,(byte)0xff,(byte)0xff,(byte)0xff,
		(byte)0xf0,(byte)0x3f,(byte)0xff,(byte)0xff,
		(byte)0xf0,(byte)0x07,(byte)0xff,(byte)0xff,
		(byte)0xf0,(byte)0x03,(byte)0xff,(byte)0xff,
		(byte)0xf0,(byte)0x00,(byte)0xff,(byte)0xff,

		(byte)0x10,(byte)0x00,(byte)0x7f,(byte)0xff,
		(byte)0x00,(byte)0x00,(byte)0x7f,(byte)0xff,
		(byte)0x80,(byte)0x00,(byte)0x7f,(byte)0xff,
		(byte)0xc0,(byte)0x00,(byte)0x7f,(byte)0xff,
		(byte)0xe0,(byte)0x00,(byte)0x7f,(byte)0xff,
		(byte)0xf0,(byte)0x00,(byte)0x7f,(byte)0xff,
		(byte)0xf8,(byte)0x00,(byte)0xff,(byte)0xff,
		(byte)0xfc,(byte)0x01,(byte)0xff,(byte)0xff,

		(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,
		(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,
		(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,
		(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,
		(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,
		(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,
		(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,
		(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,

		(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,
		(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,
		(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,
		(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,
		(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,
		(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,
		(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,
		(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff
	};
	static final byte[] HAND_MASK = {
		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,
		(byte)0x06,(byte)0x00,(byte)0x00,(byte)0x00,
		(byte)0x06,(byte)0x00,(byte)0x00,(byte)0x00,
		(byte)0x06,(byte)0x00,(byte)0x00,(byte)0x00,
		(byte)0x06,(byte)0x00,(byte)0x00,(byte)0x00,
		(byte)0x06,(byte)0xc0,(byte)0x00,(byte)0x00,
		(byte)0x06,(byte)0xd8,(byte)0x00,(byte)0x00,
		(byte)0x06,(byte)0xd8,(byte)0x00,(byte)0x00,

		(byte)0x07,(byte)0xdb,(byte)0x00,(byte)0x00,
		(byte)0x67,(byte)0xfb,(byte)0x00,(byte)0x00,
		(byte)0x3f,(byte)0xff,(byte)0x00,(byte)0x00,
		(byte)0x1f,(byte)0xff,(byte)0x00,(byte)0x00,
		(byte)0x0f,(byte)0xff,(byte)0x00,(byte)0x00,
		(byte)0x07,(byte)0xff,(byte)0x00,(byte)0x00,
		(byte)0x03,(byte)0xfe,(byte)0x00,(byte)0x00,
		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,

		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,
		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,
		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,
		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,
		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,
		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,
		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,
		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,

		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,
		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,
		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,
		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,
		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,
		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,
		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,
		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00
	};

/**
 * Prevents uninitialized instances from being created outside the package.
 */
/*
 * The shapes of the pointers that PM does not have, 32x32: '#' is black
 * and is surrounded by white, so that it is seen on any background.
 */
static final String[] CROSS_SHAPE = {
	"15,0,1,31", "0,15,31,1",
};
static final String[] UPARROW_SHAPE = {
	"15,1,1,1", "14,2,3,1", "13,3,5,1", "12,4,7,1", "11,5,9,1", "10,6,11,1",
	"9,7,13,1", "8,8,15,1", "13,9,5,21",
};

/*
 * Returns the AND and the XOR planes (32x32, 4 bytes a row) of a shape
 * given as rectangles "x,y,width,height".
 */
static byte[][] drawShape(String[] shape) {
	boolean[][] black = new boolean[32][32];
	for (int i = 0; i < shape.length; i++) {
		java.util.StringTokenizer t = new java.util.StringTokenizer(shape[i], ",");
		int x = Integer.parseInt(t.nextToken()), y = Integer.parseInt(t.nextToken());
		int w = Integer.parseInt(t.nextToken()), h = Integer.parseInt(t.nextToken());
		for (int r = y; r < y + h; r++) for (int c = x; c < x + w; c++) black[r][c] = true;
	}
	byte[] and = new byte[32 * 4], xor = new byte[32 * 4];
	for (int r = 0; r < 32; r++) {
		for (int c = 0; c < 32; c++) {
			boolean white = false;
			for (int dr = -1; dr <= 1 && !black[r][c]; dr++) {
				for (int dc = -1; dc <= 1; dc++) {
					int rr = r + dr, cc = c + dc;
					if (rr >= 0 && rr < 32 && cc >= 0 && cc < 32 && black[rr][cc]) white = true;
				}
			}
			int bit = 0x80 >> (c % 8), index = r * 4 + c / 8;
			/* Transparent: AND 1, XOR 0. Black: 0, 0. White: 0, 1 */
			if (!black[r][c] && !white) and[index] |= bit;
			if (white) xor[index] |= bit;
		}
	}
	return new byte[][] {and, xor};
}

/*
 * Creates a pointer from the AND and XOR planes of Windows (rows from the
 * top). PM keeps both in one monochrome bitmap of double height, made
 * of rows from the bottom: the XOR plane in the lower half and the AND
 * plane in the upper one. Its hot spot is also counted from the bottom.
 */
static int createPointer(int width, int height, byte[] and, int andBpl, byte[] xor, int xorBpl, int hotspotX, int hotspotY) {
	int bpl = (width + 31) / 32 * 4;
	byte[] data = new byte[bpl * height * 2];
	for (int y = 0; y < height; y++) {
		System.arraycopy(xor, y * xorBpl, data, (height - 1 - y) * bpl, Math.min(bpl, xorBpl));
		System.arraycopy(and, y * andBpl, data, (2 * height - 1 - y) * bpl, Math.min(bpl, andBpl));
	}
	BITMAPINFOHEADER2 hdr = new BITMAPINFOHEADER2();
	hdr.cx = width;
	hdr.cy = height * 2;
	hdr.cPlanes = 1;
	hdr.cBitCount = 1;
	hdr.ulCompression = OS.BCA_UNCOMP;
	hdr.usUnits = OS.BRU_METRIC;
	hdr.usRecording = OS.BRA_BOTTOMUP;
	hdr.usRendering = OS.BRH_NOTHALFTONED;
	hdr.ulColorEncoding = OS.BCE_RGB;
	int[] bmi = new int[BITMAPINFOHEADER2.int_sizeof + 2];
	bmi[0] = BITMAPINFOHEADER2.sizeof;
	bmi[1] = width;
	bmi[2] = height * 2;
	bmi[3] = (1 << 16) | 1;
	bmi[4] = OS.BCA_UNCOMP;
	bmi[8] = 2;
	bmi[14] = OS.BCE_RGB;
	bmi[BITMAPINFOHEADER2.int_sizeof] = 0x000000;
	bmi[BITMAPINFOHEADER2.int_sizeof + 1] = 0xFFFFFF;
	int hps = OS.WinGetScreenPS(OS.HWND_DESKTOP);
	int hbm = OS.GpiCreateBitmap(hps, hdr, OS.CBM_INIT, data, bmi);
	OS.WinReleasePS(hps);
	if (hbm == 0) return 0;
	POINTERINFO info = new POINTERINFO();
	info.fPointer = 1;
	info.xHotSpot = hotspotX;
	info.yHotSpot = height - 1 - hotspotY;
	info.hbmPointer = hbm;
	int hptr = OS.WinCreatePointerIndirect(OS.HWND_DESKTOP, info);
	/* PM keeps a copy of the bitmap */
	OS.GpiDeleteBitmap(hbm);
	return hptr;
}

Cursor() {
}

/**	 
 * Constructs a new cursor given a device and a style
 * constant describing the desired cursor appearance.
 * <p>
 * You must dispose the cursor when it is no longer required. 
 * </p>
 *
 * @param device the device on which to allocate the cursor
 * @param style the style of cursor to allocate
 * 
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if device is null and there is no current device</li>
 *    <li>ERROR_INVALID_ARGUMENT - when an unknown style is specified</li>
 * </ul>
 * @exception SWTError <ul>
 *    <li>ERROR_NO_HANDLES - if a handle could not be obtained for cursor creation</li>
 * </ul>
 *
 * @see SWT#CURSOR_ARROW
 * @see SWT#CURSOR_WAIT
 * @see SWT#CURSOR_CROSS
 * @see SWT#CURSOR_APPSTARTING
 * @see SWT#CURSOR_HELP
 * @see SWT#CURSOR_SIZEALL
 * @see SWT#CURSOR_SIZENESW
 * @see SWT#CURSOR_SIZENS
 * @see SWT#CURSOR_SIZENWSE
 * @see SWT#CURSOR_SIZEWE
 * @see SWT#CURSOR_SIZEN
 * @see SWT#CURSOR_SIZES
 * @see SWT#CURSOR_SIZEE
 * @see SWT#CURSOR_SIZEW
 * @see SWT#CURSOR_SIZENE
 * @see SWT#CURSOR_SIZESE
 * @see SWT#CURSOR_SIZESW
 * @see SWT#CURSOR_SIZENW
 * @see SWT#CURSOR_UPARROW
 * @see SWT#CURSOR_IBEAM
 * @see SWT#CURSOR_NO
 * @see SWT#CURSOR_HAND
 */
public Cursor(Device device, int style) {
	if (device == null) device = Device.getDevice();
	if (device == null) SWT.error(SWT.ERROR_NULL_ARGUMENT);
	this.device = device;
	int id = 0;
	switch (style) {
		case SWT.CURSOR_HAND: 		break;
		case SWT.CURSOR_ARROW: 		id = OS.SPTR_ARROW; break;
		case SWT.CURSOR_WAIT: 		id = OS.SPTR_WAIT; break;
		case SWT.CURSOR_CROSS: 		break;
		case SWT.CURSOR_APPSTARTING: 	id = OS.SPTR_WAIT; break;
		case SWT.CURSOR_HELP: 		id = OS.SPTR_ARROW; break;
		case SWT.CURSOR_SIZEALL: 	id = OS.SPTR_MOVE; break;
		case SWT.CURSOR_SIZENESW: 	id = OS.SPTR_SIZENESW; break;
		case SWT.CURSOR_SIZENS: 	id = OS.SPTR_SIZENS; break;
		case SWT.CURSOR_SIZENWSE: 	id = OS.SPTR_SIZENWSE; break;
		case SWT.CURSOR_SIZEWE: 	id = OS.SPTR_SIZEWE; break;
		case SWT.CURSOR_SIZEN: 		id = OS.SPTR_SIZENS; break;
		case SWT.CURSOR_SIZES: 		id = OS.SPTR_SIZENS; break;
		case SWT.CURSOR_SIZEE: 		id = OS.SPTR_SIZEWE; break;
		case SWT.CURSOR_SIZEW: 		id = OS.SPTR_SIZEWE; break;
		case SWT.CURSOR_SIZENE: 	id = OS.SPTR_SIZENESW; break;
		case SWT.CURSOR_SIZESE: 	id = OS.SPTR_SIZENWSE; break;
		case SWT.CURSOR_SIZESW: 	id = OS.SPTR_SIZENESW; break;
		case SWT.CURSOR_SIZENW: 	id = OS.SPTR_SIZENWSE; break;
		case SWT.CURSOR_UPARROW: 	break;
		case SWT.CURSOR_IBEAM: 		id = OS.SPTR_TEXT; break;
		case SWT.CURSOR_NO: 		id = OS.SPTR_ILLEGAL; break;
		default:
			SWT.error(SWT.ERROR_INVALID_ARGUMENT);
	}
	if (id != 0) {
		handle = OS.WinQuerySysPointer(OS.HWND_DESKTOP, id, false);
	} else {
		/*
		* PM has no hand, cross or up arrow pointer: they are made
		* from the planes of Windows (see createPointer()).
		*/
		byte[][] planes;
		int hotspotX, hotspotY;
		switch (style) {
			case SWT.CURSOR_HAND:
				planes = new byte[][] {HAND_SOURCE, HAND_MASK};
				hotspotX = 5;  hotspotY = 0;
				break;
			case SWT.CURSOR_CROSS:
				planes = drawShape(CROSS_SHAPE);
				hotspotX = 15;  hotspotY = 15;
				break;
			default:
				planes = drawShape(UPARROW_SHAPE);
				hotspotX = 15;  hotspotY = 1;
				break;
		}
		handle = createPointer(32, 32, planes[0], 4, planes[1], 4, hotspotX, hotspotY);
		created = true;
	}
	if (handle == 0) SWT.error(SWT.ERROR_NO_HANDLES);
	if (device.tracking) device.new_Object(this);
}

/**	 
 * Constructs a new cursor given a device, image and mask
 * data describing the desired cursor appearance, and the x
 * and y co-ordinates of the <em>hotspot</em> (that is, the point
 * within the area covered by the cursor which is considered
 * to be where the on-screen pointer is "pointing").
 * <p>
 * The mask data is allowed to be null, but in this case the source
 * must be an ImageData representing an icon that specifies both
 * color data and mask data.
 * <p>
 * You must dispose the cursor when it is no longer required. 
 * </p>
 *
 * @param device the device on which to allocate the cursor
 * @param source the color data for the cursor
 * @param mask the mask data for the cursor (or null)
 * @param hotspotX the x coordinate of the cursor's hotspot
 * @param hotspotY the y coordinate of the cursor's hotspot
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if device is null and there is no current device</li>
 *    <li>ERROR_NULL_ARGUMENT - if the source is null</li>
 *    <li>ERROR_NULL_ARGUMENT - if the mask is null and the source does not have a mask</li>
 *    <li>ERROR_INVALID_ARGUMENT - if the source and the mask are not the same 
 *          size, or either is not of depth one, or if the hotspot is outside 
 *          the bounds of the image</li>
 * </ul>
 * @exception SWTError <ul>
 *    <li>ERROR_NO_HANDLES - if a handle could not be obtained for cursor creation</li>
 * </ul>
 */
public Cursor(Device device, ImageData source, ImageData mask, int hotspotX, int hotspotY) {
	if (device == null) device = Device.getDevice();
	if (device == null) SWT.error(SWT.ERROR_NULL_ARGUMENT);
	this.device = device;
	if (source == null) SWT.error(SWT.ERROR_NULL_ARGUMENT);
	if (mask == null) {
		if (source.getTransparencyType() != SWT.TRANSPARENCY_MASK) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}
		mask = source.getTransparencyMask();
	}
	/* Check the bounds. Mask must be the same size as source */
	if (mask.width != source.width || mask.height != source.height) {
		SWT.error(SWT.ERROR_INVALID_ARGUMENT);
	}
	/* Check color depths */
	if (mask.depth != 1) SWT.error(SWT.ERROR_INVALID_ARGUMENT);
	if (source.depth != 1) SWT.error(SWT.ERROR_INVALID_ARGUMENT);
	/* Check the hotspots */
	if (hotspotX >= source.width || hotspotX < 0 ||
		hotspotY >= source.height || hotspotY < 0) {
		SWT.error(SWT.ERROR_INVALID_ARGUMENT);
	}
	/* Create the cursor */
	handle = createPointer(source.width, source.height, source.data, source.bytesPerLine,
		mask.data, mask.bytesPerLine, hotspotX, hotspotY);
	if (handle == 0) SWT.error(SWT.ERROR_NO_HANDLES);
	created = true;
	if (device.tracking) device.new_Object(this);
}

/**
 * Disposes of the operating system resources associated with
 * the cursor. Applications must dispose of all cursors which
 * they allocate.
 */
public void dispose () {
	if (handle == 0) return;
	if (created) {
		/* Do not leave PM showing a pointer that does not exist any more */
		if (OS.WinQueryPointer(OS.HWND_DESKTOP) == handle) {
			OS.WinSetPointer(OS.HWND_DESKTOP, OS.WinQuerySysPointer(OS.HWND_DESKTOP, OS.SPTR_ARROW, false));
		}
		OS.WinDestroyPointer(handle);
	}
	handle = 0;
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
	if (!(object instanceof Cursor)) return false;
	Cursor cursor = (Cursor) object;
	return device == cursor.device && handle == cursor.handle;
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

/**
 * Returns <code>true</code> if the cursor has been disposed,
 * and <code>false</code> otherwise.
 * <p>
 * This method gets the dispose state for the cursor.
 * When a cursor has been disposed, it is an error to
 * invoke any other method using the cursor.
 *
 * @return <code>true</code> when the cursor is disposed and <code>false</code> otherwise
 */
public boolean isDisposed() {
	return handle == 0;
}

/**
 * Returns a string containing a concise, human-readable
 * description of the receiver.
 *
 * @return a string representation of the receiver
 */
public String toString () {
	if (isDisposed()) return "Cursor {*DISPOSED*}";
	return "Cursor {" + handle + "}";
}

/**	 
 * Invokes platform specific functionality to allocate a new cursor.
 * <p>
 * <b>IMPORTANT:</b> This method is <em>not</em> part of the public
 * API for <code>Cursor</code>. It is marked public only so that it
 * can be shared within the packages provided by SWT. It is not
 * available on all platforms, and should never be called from
 * application code.
 * </p>
 *
 * @param device the device on which to allocate the color
 * @param handle the handle for the cursor
 * 
 * @private
 */
public static Cursor pm_new(Device device, int handle) {
	if (device == null) device = Device.getDevice();
	Cursor cursor = new Cursor();
	cursor.handle = handle;
	cursor.device = device;
	return cursor;
}

}
