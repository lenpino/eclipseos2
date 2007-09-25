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

import java.util.Arrays;
import java.util.ArrayList;

/**
 * This class is the abstract superclass of all device objects,
 * such as the Display device and the Printer device. Devices
 * can have a graphics context (GC) created for them, and they
 * can be drawn on by sending messages to the associated GC.
 */
public abstract class Device implements Drawable {
    
    /* Debugging */
    public static boolean DEBUG;
    boolean debug = DEBUG;
    boolean tracking = DEBUG;
    Error [] errors;
    Object [] objects;

    /* Palette */
    public int hPalette = 0;
    int [] colorRefCount;

    boolean disposed;
    
    /* Warning: This field is platform dependent */
    public boolean has32bitDepth;

    /*
     *  Font cache.
     *  Warning: These fields are platform dependent
     */
    String[] fontFamilies = null;
    FATTRS[][] fontAttrs = null;
    int defFontFamilyID = -1;
    int defFontAttrsID = -1;
    int defFontHeight; 
    long fontCacheStamp;
    FATTRS sysFont = null;

    /*
    * TEMPORARY CODE. When a graphics object is
    * created and the device parameter is null,
    * the current Display is used. This presents
    * a problem because SWT graphics does not
    * reference classes in SWT widgets. The correct
    * fix is to remove this feature. Unfortunately,
    * too many application programs rely on this
    * feature.
    *
    * This code will be removed in the future.
    */
    protected static Device CurrentDevice;
    protected static Runnable DeviceFinder;
    static {
        try {
            Class.forName ("org.eclipse.swt.widgets.Display");
        } catch (Throwable e) {}
    }

/*
* TEMPORARY CODE.
*/
static Device getDevice () {
    if (DeviceFinder != null) DeviceFinder.run();
    Device device = CurrentDevice;
    CurrentDevice = null;
    return device;
}

/**
 * Constructs a new instance of this class.
 * <p>
 * You must dispose the device when it is no longer required.
 * </p>
 *
 * @param data the DeviceData which describes the receiver
 *
 * @see #create
 * @see #init
 * @see DeviceData
 */
public Device(DeviceData data) {
    if (data != null) {
        debug = data.debug;
        tracking = data.tracking;
    }
    create (data);
    init ();
    if (tracking) {
        errors = new Error [128];
        objects = new Object [128];
    }

    /*
     *  Detect the best bit count and number of planes, and
     *  whether the 32-bit depth is supported.
     */
    GCData hpsData = new GCData();
    int hps = internal_new_GC (hpsData);
    int[] formatCount = new int[1];
    OS.DevQueryCaps (hpsData.hdc, OS.CAPS_BITMAP_FORMATS, 1, formatCount);
    int[] formats = new int [formatCount[0] * 2];
    OS.GpiQueryDeviceBitmapFormats (hps, formats.length, formats);
    internal_dispose_GC (hps, hpsData);
    for (int i = 0; i < formats.length; i += 2) {
        if (formats[i] == 1 && formats[i + 1] == 32) {
            has32bitDepth = true;
            break;
        }
    }
}

/**
 * Throws an <code>SWTException</code> if the receiver can not
 * be accessed by the caller. This may include both checks on
 * the state of the receiver and more generally on the entire
 * execution context. This method <em>should</em> be called by
 * device implementors to enforce the standard SWT invariants.
 * <p>
 * Currently, it is an error to invoke any method (other than
 * <code>isDisposed()</code> and <code>dispose()</code>) on a
 * device that has had its <code>dispose()</code> method called.
 * </p><p>
 * In future releases of SWT, there may be more or fewer error
 * checks and exceptions may be thrown for different reasons.
 * <p>
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 * </ul>
 */
protected void checkDevice () {
    if (disposed) SWT.error(SWT.ERROR_DEVICE_DISPOSED);
}

boolean checkFontCache() {
    boolean changed =
        (OS.GpiQueryFontAction (0, OS.QFA_PUBLIC) & OS.QFA_PUBLIC) == OS.QFA_PUBLIC;
    if (!changed && defFontFamilyID != -1) return true;
    
    /* chache fonts */
    
    fontFamilies = new String [8];
    fontAttrs = new FATTRS [8][];
    defFontFamilyID = -1;
    defFontAttrsID = -1;

    PSZ app = new PSZ ("PM_SystemFonts");
    PSZ key = new PSZ ("WindowText");
    int[] keyLen = new int [1];
    OS.PrfQueryProfileSize (OS.HINI_USERPROFILE, app, key, keyLen);
    byte[] buf = new byte [keyLen [0]];
    OS.PrfQueryProfileString (OS.HINI_USERPROFILE, app, key, null, buf, keyLen [0]);
    sysFont = decomposeFontNameSize (new PSZ (buf).toString());

    GCData hpsData = new GCData();
    int hps = internal_new_GC (hpsData);

    int[] cFonts = new int [1];
    cFonts [0] = OS.GpiQueryFonts (hps, OS.QF_PUBLIC, null, cFonts, 0, null);
    byte[] fm = new byte [cFonts [0] * FONTMETRICS.sizeof];
    OS.GpiQueryFonts (hps, OS.QF_PUBLIC, null, cFonts, FONTMETRICS.sizeof, fm);

    int info[] = new int [3];
    OS.DevQueryCaps (hpsData.hdc, OS.CAPS_GRAPHICS_CHAR_HEIGHT, 3, info);
    int xRes = info [1], yRes = info [2];
    defFontHeight = Compatibility.round (info [0] * 72, yRes);

    internal_dispose_GC (hps, hpsData);
    
    fontCacheStamp = System.currentTimeMillis();

    byte[] defFm = new byte [FONTMETRICS.sizeof];
    byte[] curFm = new byte [FONTMETRICS.sizeof];
    int sysFontHeight = sysFont.height;
    if (sysFontHeight == 0) sysFontHeight = defFontHeight;
    FATTRS sfa = null;

    int off = 0;
    for (int i = 0; i < cFonts [0]; i++, off += FONTMETRICS.sizeof) {
        int lMatch = 
            (fm [off + 204] & 0xFF) | ((fm [off + 205] & 0xFF) << 8) |
            ((fm [off + 206] & 0xFF) << 16) | ((fm [off + 207] & 0xFF) << 24);
        if (lMatch == 0) {
            // default font found
            System.arraycopy (fm, off, defFm, 0, FONTMETRICS.sizeof);
            continue;
        }
        short fsDefn = (short) ((fm [off + 146] & 0xFF) | ((fm [off + 147] & 0xFF) << 8));
        short fsCaps = (short) ((fm [off + 150] & 0xFF) | ((fm [off + 151] & 0xFF) << 8));
        short sXDeviceRes = (short) ((fm [off + 126] & 0xFF) | ((fm [off + 127] & 0xFF) << 8));
        short sYDeviceRes = (short) ((fm [off + 128] & 0xFF) | ((fm [off + 129] & 0xFF) << 8));

        // ignore bitmap fonts that do not match the current device resolution
        if ((fsDefn & OS.FM_DEFN_OUTLINE) == 0 && (
            (sXDeviceRes != xRes || sYDeviceRes != yRes)
        ))  {
            continue;
        }

        FATTRS fa = new FATTRS();

        int j = 0;
        while (j < OS.FACESIZE && fm [off + j] != 0) j++;
        fa.fm_szFamilyname = new String (fm, off, j);
        System.arraycopy (fm, off + 32, fa.szFacename, 0, OS.FACESIZE);
        
        fa.lMatch = lMatch;
        fa.idRegistry = (short) ((fm [off + 64] & 0xFF) | ((fm [off + 65] & 0xFF) << 8));
        fa.usCodePage = (short) ((fm [off + 66] & 0xFF) | ((fm [off + 67] & 0xFF) << 8));
        fa.lMaxBaselineExt =
            (fm [off + 112] & 0xFF) | ((fm [off + 113] & 0xFF) << 8) |
            ((fm [off + 114] & 0xFF) << 16) | ((fm [off + 115] & 0xFF) << 24);
        fa.lAveCharWidth = 
            (fm [off + 100] & 0xFF) | ((fm [off + 101] & 0xFF) << 8) |
            ((fm [off + 102] & 0xFF) << 16) | ((fm [off + 103] & 0xFF) << 24);
        fa.fsType = (short) ((fm [off + 144] & 0xFF) | ((fm [off + 145] & 0xFF) << 8));

        if ((fsDefn & OS.FM_DEFN_OUTLINE) == OS.FM_DEFN_OUTLINE)
            fa.fsFontUse |= OS.FATTR_FONTUSE_OUTLINE;
        if ((fsCaps & OS.FM_CAP_NOMIX) == OS.FM_CAP_NOMIX)
            fa.fsFontUse |= OS.FATTR_FONTUSE_NOMIX;

        fa.fm_usWeightClass = (short) ((fm [off + 122] & 0xFF) | ((fm [off + 123] & 0xFF) << 8));
        fa.fm_fsSelection = (short) ((fm [off + 148] & 0xFF) | ((fm [off + 149] & 0xFF) << 8));
        // FONTMETRICS.sNominalPointSize is in tenths of points, convert it to points
        fa.height = ((short) ((fm [off + 138] & 0xFF) | ((fm [off + 139] & 0xFF) << 8))) / 10;
        
        /* find (allocate) an index for the family */
        for (j = 0; j < fontFamilies.length; j++) {
            if (fontFamilies [j] == null || fontFamilies [j].equals (fa.fm_szFamilyname))
                break;
        }
        if (j == fontFamilies.length) {
            String[] newFontFamilies = new String [j + 8];
            System.arraycopy (fontFamilies, 0, newFontFamilies, 0, j);
            fontFamilies = newFontFamilies;
            FATTRS[][] newFontAttrs = new FATTRS [j + 8][];
            System.arraycopy (fontAttrs, 0, newFontAttrs, 0, j);
            fontAttrs = newFontAttrs;
        }
        if (fontFamilies [j] == null) {
            fontFamilies [j] = fa.fm_szFamilyname;
            fontAttrs [j] = new FATTRS [4];
        }

        /* allocate an index for the font data and store it */
        FATTRS[] fAttrs = fontAttrs [j];
        int k;
        for (k = 0; k < fAttrs.length; k++) {
            // break if we found a duplicate facename that has the same
            // type (outline or bitmap) and, if bitmap, also the same height 
            if (fAttrs [k] == null || (
                    (fAttrs [k].fsFontUse & OS.FATTR_FONTUSE_OUTLINE) ==
                        (fa.fsFontUse & OS.FATTR_FONTUSE_OUTLINE) &&
                    ((fa.fsFontUse & OS.FATTR_FONTUSE_OUTLINE) != 0 ||
                        fAttrs [k].height == fa.height) &&
                    Arrays.equals (fAttrs [k].szFacename, fa.szFacename)
                )
            )   break;
        }
        if (k == fAttrs.length) {
            fAttrs = new FATTRS [k + 4];
            System.arraycopy (fontAttrs [j], 0, fAttrs, 0, k);
            fontAttrs [j] = fAttrs;
        } else if (fAttrs [k] != null) {
            // prevent duplicates -- always keep the first font met
            continue;
        }
        fAttrs [k] = fa;
        
        /* check for the default font */
        if (defFontFamilyID == -1) {
            System.arraycopy (fm, off, curFm, 0, FONTMETRICS.sizeof);
            curFm [204] = 0; curFm [205] = 0;
            curFm [206] = 0; curFm [207] = 0;
            if (Arrays.equals (curFm, defFm)) {
                // true metrics of the default font (with lMatch != 0)
                defFontFamilyID = j;
                defFontAttrsID = k;
            }
        }

        /* check for the system font */
        if ((fa.fsFontUse & OS.FATTR_FONTUSE_OUTLINE) == 0) {
            // prefer bitmap fonts (OS/2 does the same)
            if ((sfa == null || (sfa.fsFontUse & OS.FATTR_FONTUSE_OUTLINE) != 0) &&
                sysFontHeight == fa.height &&
                Arrays.equals (sysFont.szFacename, fa.szFacename)
            ) {
                sfa = fa;
            }
        } else if (sfa == null &&
            Arrays.equals (sysFont.szFacename, fa.szFacename)
        ) {
            sfa = fa;
        }
    }

    if (sfa == null) {
        sfa = fontAttrs [defFontFamilyID] [defFontAttrsID];
        System.arraycopy (sfa.szFacename, 0, sysFont.szFacename, 0, OS.FACESIZE);
        sysFont.height = sfa.height;
        sysFont.fsSelection = 0;
    }
    sysFont.fsFontUse = sfa.fsFontUse;
    sysFont.lMatch = sfa.lMatch;
    sysFont.fm_szFamilyname = sfa.fm_szFamilyname;
    sysFont.fm_fsSelection = sfa.fm_fsSelection;
    sysFont.fm_usWeightClass = sfa.fm_usWeightClass;
    
    return false;
}

protected String composeFontNameSize (FATTRS fa) {
    String composed = fa.height + "." + new PSZ (fa.szFacename).toString();
    String simStyle = "";
    if ((fa.fsSelection & OS.FATTR_SEL_BOLD) != 0) simStyle += " Bold";
    if ((fa.fsSelection & OS.FATTR_SEL_ITALIC) != 0) simStyle += " Italic";
    if ((fa.fsSelection & OS.FATTR_SEL_UNDERSCORE) != 0) simStyle += " Underscore";
    if ((fa.fsSelection & OS.FATTR_SEL_STRIKEOUT) != 0) simStyle += " Strikeout";
    if ((fa.fsSelection & OS.FATTR_SEL_OUTLINE) != 0) simStyle += " Outline";
    if (simStyle.length() > 0) {
        composed += "." + simStyle.substring (1);
    }
    return composed;
}

/**
 * Creates the device in the operating system.  If the device
 * does not have a handle, this method may do nothing depending
 * on the device.
 * <p>
 * This method is called before <code>init</code>.
 * </p><p>
 * Subclasses are supposed to reimplement this method and not
 * call the <code>super</code> implementation.
 * </p>
 *
 * @param data the DeviceData which describes the receiver
 *
 * @see #init
 */
protected void create (DeviceData data) {
}

FATTRS decomposeFontNameSize (String fontNameSize) {
    int pnt = fontNameSize.indexOf ('.');
    int height = 0;
    if (pnt > 0) {
        try {
            height = Integer.parseInt (fontNameSize.substring (0, pnt));
        } catch (NumberFormatException x) {
        }
    }
    int pnt2 = fontNameSize.indexOf ('.', ++ pnt);
    String faceName;
    String simStyle;
    if (pnt2 > 0) {
        faceName = fontNameSize.substring (pnt, pnt2);
        simStyle = fontNameSize.substring (pnt2) + ".";
    } else {
        faceName = fontNameSize.substring (pnt);
        simStyle = ".";
    }
    simStyle = simStyle.toUpperCase();

    FATTRS decomposed = new FATTRS();
    int len = faceName.length();
    if (len >= OS.FACESIZE) len = OS.FACESIZE - 1;
    System.arraycopy (faceName.getBytes(), 0, decomposed.szFacename, 0, len);
    decomposed.szFacename [len] = 0;
    if (simStyle.indexOf (".BOLD.") >= 0) decomposed.fsSelection |= OS.FATTR_SEL_BOLD;
    if (simStyle.indexOf (".ITALIC.") >= 0) decomposed.fsSelection |= OS.FATTR_SEL_ITALIC;
    if (simStyle.indexOf (".UNDERSCORE.") >= 0) decomposed.fsSelection |= OS.FATTR_SEL_UNDERSCORE;
    if (simStyle.indexOf (".UNDERLINED.") >= 0) decomposed.fsSelection |= OS.FATTR_SEL_UNDERSCORE;
    if (simStyle.indexOf (".STRIKEOUT.") >= 0) decomposed.fsSelection |= OS.FATTR_SEL_STRIKEOUT;
    if (simStyle.indexOf (".OUTLINE.") >= 0) decomposed.fsSelection |= OS.FATTR_SEL_OUTLINE;
    decomposed.height = height;
    return decomposed;
}

/**
 * Destroys the device in the operating system and releases
 * the device's handle.  If the device does not have a handle,
 * this method may do nothing depending on the device.
 * <p>
 * This method is called after <code>release</code>.
 * </p><p>
 * Subclasses are supposed to reimplement this method and not
 * call the <code>super</code> implementation.
 * </p>
 *
 * @see #dispose
 * @see #release
 */
protected void destroy () {
}

/**
 * Disposes of the operating system resources associated with
 * the receiver. After this method has been invoked, the receiver
 * will answer <code>true</code> when sent the message
 * <code>isDisposed()</code>.
 *
 * @see #release
 * @see #destroy
 * @see #checkDevice
 */
public void dispose () {
    if (isDisposed()) return;
    checkDevice ();
    release ();
    destroy ();
    disposed = true;
    if (tracking) {
        objects = null;
        errors = null;
    }
}

void dispose_Object (Object object) {
    for (int i=0; i<objects.length; i++) {
        if (objects [i] == object) {
            objects [i] = null;
            errors [i] = null;
            return;
        }
    }
}

/**
 * Returns a rectangle describing the receiver's size and location.
 *
 * @return the bounding rectangle
 *
 * @exception SWTException <ul>
 *    <li>ERROR_DEVICE_DISPOSED - if the receiver has been disposed</li>
 * </ul>
 */
public Rectangle getBounds () {
    checkDevice ();
    GCData data = new GCData ();
    int hps = internal_new_GC (data);
    int[] caps = new int[2];
    OS.DevQueryCaps (data.hdc, OS.CAPS_WIDTH, caps.length, caps);
    internal_dispose_GC (hps, data);
    return new Rectangle (0, 0, caps[0], caps[1]);
}

/**
 * Returns a <code>DeviceData</code> based on the receiver.
 * Modifications made to this <code>DeviceData</code> will not
 * affect the receiver.
 *
 * @return a <code>DeviceData</code> containing the device's data and attributes
 *
 * @exception SWTException <ul>
 *    <li>ERROR_DEVICE_DISPOSED - if the receiver has been disposed</li>
 * </ul>
 *
 * @see DeviceData
 */
public DeviceData getDeviceData () {
    checkDevice();
    DeviceData data = new DeviceData ();
    data.debug = debug;
    data.tracking = tracking;
    int count = 0, length = 0;
    if (tracking) length = objects.length;
    for (int i=0; i<length; i++) {
        if (objects [i] != null) count++;
    }
    int index = 0;
    data.objects = new Object [count];
    data.errors = new Error [count];
    for (int i=0; i<length; i++) {
        if (objects [i] != null) {
            data.objects [index] = objects [i];
            data.errors [index] = errors [i];
            index++;
        }
    }
    return data;
}

/**
 * Returns a rectangle which describes the area of the
 * receiver which is capable of displaying data.
 *
 * @return the client area
 *
 * @exception SWTException <ul>
 *    <li>ERROR_DEVICE_DISPOSED - if the receiver has been disposed</li>
 * </ul>
 *
 * @see #getBounds
 */
public Rectangle getClientArea () {
    return getBounds ();
}

/**
 * Returns the bit depth of the screen, which is the number of
 * bits it takes to represent the number of unique colors that
 * the screen is currently capable of displaying. This number
 * will typically be one of 1, 8, 15, 16, 24 or 32.
 *
 * @return the depth of the screen
 *
 * @exception SWTException <ul>
 *    <li>ERROR_DEVICE_DISPOSED - if the receiver has been disposed</li>
 * </ul>
 */
public int getDepth () {
    checkDevice ();
    GCData hpsData = new GCData();
    int hps = internal_new_GC (hpsData);
    int colorInfo[] = new int [2];
    OS.DevQueryCaps (hpsData.hdc, OS.CAPS_COLOR_PLANES, 2, colorInfo);
    internal_dispose_GC (hps, hpsData);
    return colorInfo [0] * colorInfo [1];
}

/**
 * Returns a point whose x coordinate is the horizontal
 * dots per inch of the display, and whose y coordinate
 * is the vertical dots per inch of the display.
 *
 * @return the horizontal and vertical DPI
 *
 * @exception SWTException <ul>
 *    <li>ERROR_DEVICE_DISPOSED - if the receiver has been disposed</li>
 * </ul>
 */
public Point getDPI () {
    checkDevice ();
    GCData hpsData = new GCData();
    int hps = internal_new_GC (hpsData);
    int dpiInfo[] = new int [2];
    OS.DevQueryCaps (hpsData.hdc, OS.CAPS_HORIZONTAL_FONT_RES, 2, dpiInfo);
    internal_dispose_GC (hps, hpsData);
    return new Point (dpiInfo [0], dpiInfo [1]);
}

/**
 * Returns <code>FontData</code> objects which describe
 * the fonts that match the given arguments. If the
 * <code>faceName</code> is null, all fonts will be returned.
 *
 * @param faceName the name of the font to look for, or null
 * @param scalable true if scalable fonts should be returned.
 * @return the matching font data
 *
 * @exception SWTException <ul>
 *    <li>ERROR_DEVICE_DISPOSED - if the receiver has been disposed</li>
 * </ul>
 */
public FontData[] getFontList (String faceName, boolean scalable) {
    checkDevice ();
    checkFontCache();
    
    ArrayList list = new ArrayList();
    FATTRS fa;
    for (int i = 0; i < fontAttrs.length; i++) {
        if (fontAttrs [i] == null) break;
        if (faceName != null && !fontFamilies [i].equals (faceName)) continue;
        for (int j = 0; j < fontAttrs [i].length; j++) {
            fa = fontAttrs [i][j];
            if (fa == null) break;
            if (((fa.fsFontUse & OS.FATTR_FONTUSE_OUTLINE) != 0) != scalable) continue;
            list.add (FontData.pm_new (fa));
        }
        if (faceName != null) break;
    }
    FontData[] fontList = new FontData [list.size()];
    return (FontData []) list.toArray (fontList);
}

/**
 * Returns the matching standard color for the given
 * constant, which should be one of the color constants
 * specified in class <code>SWT</code>. Any value other
 * than one of the SWT color constants which is passed
 * in will result in the color black. This color should
 * not be free'd because it was allocated by the system,
 * not the application.
 *
 * @param id the color constant
 * @return the matching color
 *
 * @exception SWTException <ul>
 *    <li>ERROR_DEVICE_DISPOSED - if the receiver has been disposed</li>
 * </ul>
 *
 * @see SWT
 */
public Color getSystemColor (int id) {
    checkDevice ();
    int pixel = 0;
    switch (id) {
        case SWT.COLOR_WHITE:       pixel = hPalette != 0 ? 15 : 0x00FFFFFF; break;
        case SWT.COLOR_BLACK:       pixel = hPalette != 0 ? 0  : 0x00000000; break;
        case SWT.COLOR_RED:         pixel = hPalette != 0 ? 9  : 0x00FF0000; break;
        case SWT.COLOR_DARK_RED:    pixel = hPalette != 0 ? 1  : 0x00800000; break;
        case SWT.COLOR_GREEN:       pixel = hPalette != 0 ? 10 : 0x0000FF00; break;
        case SWT.COLOR_DARK_GREEN:  pixel = hPalette != 0 ? 2  : 0x00008000; break;
        case SWT.COLOR_YELLOW:      pixel = hPalette != 0 ? 11 : 0x00FFFF00; break;
        case SWT.COLOR_DARK_YELLOW: pixel = hPalette != 0 ? 3  : 0x00808000; break;
        case SWT.COLOR_BLUE:        pixel = hPalette != 0 ? 12 : 0x000000FF; break;
        case SWT.COLOR_DARK_BLUE:   pixel = hPalette != 0 ? 4  : 0x00000080; break;
        case SWT.COLOR_MAGENTA:     pixel = hPalette != 0 ? 13 : 0x00FF00FF; break;
        case SWT.COLOR_DARK_MAGENTA:pixel = hPalette != 0 ? 5  : 0x00800080; break;
        case SWT.COLOR_CYAN:        pixel = hPalette != 0 ? 14 : 0x0000FFFF; break;
        case SWT.COLOR_DARK_CYAN:   pixel = hPalette != 0 ? 6  : 0x00008080; break;
        case SWT.COLOR_GRAY:        pixel = hPalette != 0 ? 7  : 0x00C0C0C0; break;
        case SWT.COLOR_DARK_GRAY:   pixel = hPalette != 0 ? 8  : 0x00808080; break;
    }
    return Color.pm_new (this, pixel);
}

/**
 * Returns a reasonable font for applications to use.
 * On some platforms, this will match the "default font"
 * or "system font" if such can be found.  This font
 * should not be free'd because it was allocated by the
 * system, not the application.
 * <p>
 * Typically, applications which want the default look
 * should simply not set the font on the widgets they
 * create. Widgets are always created with the correct
 * default font for the class of user-interface component
 * they represent.
 * </p>
 *
 * @return a font
 *
 * @exception SWTException <ul>
 *    <li>ERROR_DEVICE_DISPOSED - if the receiver has been disposed</li>
 * </ul>
 */
public Font getSystemFont () {
    checkDevice();
    return Font.pm_new (this, systemFont());
}

/**
 * Returns <code>true</code> if the underlying window system prints out
 * warning messages on the console, and <code>setWarnings</code>
 * had previously been called with <code>true</code>.
 *
 * @return <code>true</code>if warnings are being handled, and <code>false</code> otherwise
 *
 * @exception SWTException <ul>
 *    <li>ERROR_DEVICE_DISPOSED - if the receiver has been disposed</li>
 * </ul>
 */
public boolean getWarnings () {
    checkDevice ();
    return false;
}

/**
 * Initializes any internal resources needed by the
 * device.
 * <p>
 * This method is called after <code>create</code>.
 * </p><p>
 * If subclasses reimplement this method, they must
 * call the <code>super</code> implementation.
 * </p>
 *
 * @see #create
 */
protected void init () {

    /*
     * If we're not on a device which supports palettes,
     * don't create one.
     */
    GCData data = new GCData ();
    int hps = internal_new_GC (data);
    int addcaps[] = new int [1];
    int colors[] = new int [1];
    OS.DevQueryCaps (data.hdc, OS.CAPS_ADDITIONAL_GRAPHICS, 1, addcaps);
    OS.DevQueryCaps (data.hdc, OS.CAPS_COLORS, 1, colors);
    internal_dispose_GC (hps, data);
    
    if ((addcaps[0] & OS.CAPS_PALETTE_MANAGER) == 0 || colors[0] != 256) return;

    /*
     * The following colors are listed in the Windows
     * Programmer's Reference as the colors guaranteed
     * to be in the default system palette.
     * The same should be in OS/2.
     */
    RGB [] rgbs = new RGB [] {
        new RGB (0,0,0),
        new RGB (0x80,0,0),
        new RGB (0,0x80,0),
        new RGB (0x80,0x80,0),
        new RGB (0,0,0x80),
        new RGB (0x80,0,0x80),
        new RGB (0,0x80,0x80),
        new RGB (0xC0,0xC0,0xC0),
        new RGB (0x80,0x80,0x80),
        new RGB (0xFF,0,0),
        new RGB (0,0xFF,0),
        new RGB (0xFF,0xFF,0),
        new RGB (0,0,0xFF),
        new RGB (0xFF,0,0xFF),
        new RGB (0,0xFF,0xFF),
        new RGB (0xFF,0xFF,0xFF)
    };

    int[] logPalette = new int [256];
    
    /* Create the palette and reference counter */
    colorRefCount = new int [256];
    for (int i = 0; i < rgbs.length; i++) {
        colorRefCount [i] = 1;
        logPalette[i] = (rgbs[i].red << 16) | (rgbs[i].green << 8) | rgbs[i].blue;
    }
    //@@TODO(dmik): pass zero as hab: does it have any side effects? 
    hPalette = OS.GpiCreatePalette (0, 0, OS.LCOLF_CONSECRGB, 256, logPalette);
}

/**
 * Invokes platform specific functionality to allocate a new GC handle.
 * <p>
 * <b>IMPORTANT:</b> This method is <em>not</em> part of the public
 * API for <code>Device</code>. It is marked public only so that it
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
public abstract int internal_new_GC (GCData data);

/**
 * Invokes platform specific functionality to dispose a GC handle.
 * <p>
 * <b>IMPORTANT:</b> This method is <em>not</em> part of the public
 * API for <code>Device</code>. It is marked public only so that it
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
public abstract void internal_dispose_GC (int hDC, GCData data);

/**
 * Returns <code>true</code> if the device has been disposed,
 * and <code>false</code> otherwise.
 * <p>
 * This method gets the dispose state for the device.
 * When a device has been disposed, it is an error to
 * invoke any other method using the device.
 *
 * @return <code>true</code> when the device is disposed and <code>false</code> otherwise
 */
public boolean isDisposed () {
    return disposed;
}

void new_Object (Object object) {
    for (int i=0; i<objects.length; i++) {
        if (objects [i] == null) {
            objects [i] = object;
            errors [i] = new Error ();
            return;
        }
    }
    Object [] newObjects = new Object [objects.length + 128];
    System.arraycopy (objects, 0, newObjects, 0, objects.length);
    newObjects [objects.length] = object;
    objects = newObjects;
    Error [] newErrors = new Error [errors.length + 128];
    System.arraycopy (errors, 0, newErrors, 0, errors.length);
    newErrors [errors.length] = new Error ();
    errors = newErrors;
}

long matchFont (FATTRS handle, boolean firstTime) {
    if (checkFontCache() && !firstTime) return fontCacheStamp;
    
    int familyID = -1;
    for (int i = 0; i < fontFamilies.length && fontFamilies [i] != null; i++) {
        if (fontFamilies [i].equals (handle.fm_szFamilyname)) {
            familyID = i;
            break;
        }
    }
    if (familyID == -1) familyID = defFontFamilyID;

    /* find the best match */
    
    int attrsID = -1;
    int maxCost = 0;
    int maxHeightCost = 0;
    FATTRS[] fAttrs = fontAttrs [familyID];
    FATTRS fa;

    for (int i = 0; i < fAttrs.length && fAttrs [i] != null; i++) {
        int cost = 0;
        fa = fAttrs [i];
        boolean vector = (fa.fsFontUse & OS.FATTR_FONTUSE_OUTLINE) != 0;

        // ignore bold fonts when a non-bold one is requested
        if (handle.fm_usWeightClass <= 5 && fa.fm_usWeightClass > 5)
            continue;
        // ignore true italic fonts a non-italic one is requested
        if (
            (handle.fm_fsSelection & OS.FM_SEL_ITALIC) == 0 &&
            (fa.fm_fsSelection & OS.FM_SEL_ITALIC) != 0
        )   continue;
        // prefer bitmap fonts (OS/2 does the same)
        cost += vector ? 200 : 201;
        // prefer the exact (or the nearest) bold match
        int weight = handle.fm_usWeightClass;
        if (weight == fa.fm_usWeightClass) {
            cost += 100;
        } else {
            if (weight <= 5) {
                weight -= fa.fm_usWeightClass;
                if (weight > 0) weight = (weight << 1) - 1;
                else weight = ((-weight) << 1);
            } else {
                weight -= fa.fm_usWeightClass;
                if (weight < 0) weight = ((-weight) << 1) - 1;
                else weight = (weight << 1);
            }
            cost += 100 - weight;
        }
        // prefer the exact italc match
        cost += ((handle.fm_fsSelection & OS.FM_SEL_ITALIC) ==
            (fa.fm_fsSelection & OS.FM_SEL_ITALIC)) ? 71 : 70;
        // prefer exact facename match
        cost += (Arrays.equals (handle.szFacename, fa.szFacename)) ? 51 : 50;
        // prefer the closest height match for bitmap fonts  
        int hCost = handle.height;
        if (hCost == 0) hCost = defFontHeight;
        if (vector || hCost == fa.height) {
            hCost = Integer.MAX_VALUE;
        } else {
            hCost -= fa.height;
            if (hCost > 0) hCost = (hCost << 1) - 1;
            else hCost = ((-hCost) << 1);
            hCost = Integer.MAX_VALUE - hCost;
        }
        
        if (cost > maxCost || (cost == maxCost && hCost > maxHeightCost)) {
            maxCost = cost;
            maxHeightCost = hCost;
            attrsID = i;
        }
    }
    
    if (attrsID >= 0) {
        fa = fontAttrs [familyID] [attrsID];
    } else {
        fa = fontAttrs [defFontFamilyID] [defFontAttrsID];
    }
    System.arraycopy (fa.szFacename, 0, handle.szFacename, 0, OS.FACESIZE);
    // emulate bold and italic when no the exact match
    if (handle.fm_usWeightClass > 5 && fa.fm_usWeightClass <= 5)
        handle.fsSelection |= OS.FATTR_SEL_BOLD;
    if ((handle.fm_fsSelection & OS.FM_SEL_ITALIC) != 0 &&
        (fa.fm_fsSelection & OS.FM_SEL_ITALIC) == 0
    )   handle.fsSelection |= OS.FATTR_SEL_ITALIC;
    handle.fsFontUse = fa.fsFontUse;
    handle.lMatch = fa.lMatch;
    
    return fontCacheStamp;
}

protected FATTRS matchFont (String fontNameSize) {
    if (fontNameSize == null) return fontAttrs [defFontFamilyID] [defFontAttrsID];

    checkFontCache();
    FATTRS handle = decomposeFontNameSize (fontNameSize), ffa = null;
    int height = handle.height;
    if (height == 0) height = defFontHeight;
    
    for (int i = 0; i < fontAttrs.length && fontAttrs [i] != null; i++) {
        for (int j = 0; j < fontAttrs [i].length && fontAttrs [i][j] != null; j++) {
            int cost = 0;
            FATTRS fa = fontAttrs [i][j];
            
            if ((fa.fsFontUse & OS.FATTR_FONTUSE_OUTLINE) == 0) {
                if (height == fa.height &&
                    Arrays.equals (handle.szFacename, fa.szFacename)
                ) {
                    // prefer bitmap fonts (OS/2 does the same)
                    ffa = fa;
                    break;
                }
            } else if (ffa == null &&
                Arrays.equals (handle.szFacename, fa.szFacename)
            ) {
                ffa = fa;
            }
        }
    }
    if (ffa == null) {
        ffa = fontAttrs [defFontFamilyID] [defFontAttrsID];
        System.arraycopy (ffa.szFacename, 0, handle.szFacename, 0, OS.FACESIZE);
        handle.height = ffa.height;
        handle.fsSelection = 0;
    }
    handle.fsFontUse = ffa.fsFontUse;
    handle.lMatch = ffa.lMatch;
    handle.fm_szFamilyname = ffa.fm_szFamilyname;
    handle.fm_fsSelection = ffa.fm_fsSelection;
    handle.fm_usWeightClass = ffa.fm_usWeightClass;

    return handle;
}

/**
 * Releases any internal resources back to the operating
 * system and clears all fields except the device handle.
 * <p>
 * When a device is destroyed, resources that were acquired
 * on behalf of the programmer need to be returned to the
 * operating system.  For example, if the device allocated a
 * font to be used as the system font, this font would be
 * freed in <code>release</code>.  Also,to assist the garbage
 * collector and minimize the amount of memory that is not
 * reclaimed when the programmer keeps a reference to a
 * disposed device, all fields except the handle are zero'd.
 * The handle is needed by <code>destroy</code>.
 * </p>
 * This method is called before <code>destroy</code>.
 * </p><p>
 * If subclasses reimplement this method, they must
 * call the <code>super</code> implementation.
 * </p>
 *
 * @see #dispose
 * @see #destroy
 */
protected void release () {
    if (hPalette != 0) OS.GpiDeletePalette (hPalette);
    hPalette = 0;
    colorRefCount = null;
}

/**
 * If the underlying window system supports printing warning messages
 * to the console, setting warnings to <code>true</code> prevents these
 * messages from being printed. If the argument is <code>false</code>
 * message printing is not blocked.
 *
 * @param warnings <code>true</code>if warnings should be handled, and <code>false</code> otherwise
 *
 * @exception SWTException <ul>
 *    <li>ERROR_DEVICE_DISPOSED - if the receiver has been disposed</li>
 * </ul>
 */
public void setWarnings (boolean warnings) {
    checkDevice ();
}

protected FATTRS systemFont() {
    checkFontCache();
    return sysFont;
}

}
