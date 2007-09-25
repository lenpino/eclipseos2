package org.eclipse.swt.widgets;

/*
 * Copyright (c) 2000, 2002 IBM Corp.  All rights reserved.
 * This file is made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 */

import org.eclipse.swt.internal.pm.*;
import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;

/**
 * Instances of this class allow the user to select a font
 * from all available fonts in the system.
 * <p>
 * IMPORTANT: This class is intended to be subclassed <em>only</em>
 * within the SWT implementation.
 * </p>
 */
public class FontDialog extends Dialog {
	FontData fontData;
	
/**
 * Constructs a new instance of this class given only its
 * parent.
 * <p>
 * Note: Currently, null can be passed in for the parent.
 * This has the effect of creating the dialog on the currently active
 * display if there is one. If there is no current display, the 
 * dialog is created on a "default" display. <b>Passing in null as
 * the parent is not considered to be good coding style,
 * and may not be supported in a future release of SWT.</b>
 * </p>
 *
 * @param parent a shell which will be the parent of the new instance
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li>
 *    <li>ERROR_INVALID_SUBCLASS - if this class is not an allowed subclass</li>
 * </ul>
 */
public FontDialog (Shell parent) {
	this (parent, SWT.PRIMARY_MODAL);
}

/**
 * Constructs a new instance of this class given its parent
 * and a style value describing its behavior and appearance.
 * <p>
 * The style value is either one of the style constants defined in
 * class <code>SWT</code> which is applicable to instances of this
 * class, or must be built by <em>bitwise OR</em>'ing together 
 * (that is, using the <code>int</code> "|" operator) two or more
 * of those <code>SWT</code> style constants. The class description
 * lists the style constants that are applicable to the class.
 * Style bits are also inherited from superclasses.
 * </p>
 * Note: Currently, null can be passed in for the parent.
 * This has the effect of creating the dialog on the currently active
 * display if there is one. If there is no current display, the 
 * dialog is created on a "default" display. <b>Passing in null as
 * the parent is not considered to be good coding style,
 * and may not be supported in a future release of SWT.</b>
 * </p>
 *
 * @param parent a shell which will be the parent of the new instance
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li>
 *    <li>ERROR_INVALID_SUBCLASS - if this class is not an allowed subclass</li>
 * </ul>
 */
public FontDialog (Shell parent, int style) {
	super (parent, style);
	checkSubclass ();
}

/**
 * Returns a FontData object describing the font that was
 * selected in the dialog, or null if none is available.
 * 
 * @return the FontData for the selected font, or null
 */
public FontData getFontData() {
	return fontData;
}

/**
 * Makes the dialog visible and brings it to the front
 * of the display.
 *
 * @return a FontData object describing the font that was selected,
 *         or null if the dialog was cancelled or an error occurred
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the dialog has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the dialog</li>
 * </ul>
 */
public FontData open () {
	 /* Get the owner HWND for the dialog */
	int hwndOwner = 0;
	if (parent != null) 
            hwndOwner = parent.handle;
	 //Setup fontname buffer
	 FONTDLG fontDlg = new FONTDLG();
         int hps = OS.WinGetPS (hwndOwner);
         fontDlg.hpsScreen = hps; 
	 //Set cbsize (mandatory)
	 fontDlg.cbSize = FONTDLG.sizeof;
	 //Init from FATTRS
	 fontDlg.fl = OS.FNTS_CENTER|OS.FNTS_INITFROMFATTRS;
	 //Size fontname buffer
	 fontDlg.usFamilyBufLen = OS.FACESIZE;
	 //Fontname buffer
         byte[] szCurrentFont = new byte[OS.FACESIZE];
         byte[] buffer = fontData.getName().getBytes();
         for(int i=0;i<buffer.length&&i<szCurrentFont.length;i++)
             szCurrentFont[i] = buffer[i];
	 fontDlg.pszFamilyname = new PSZ(szCurrentFont);
	 //Foreground color
	 fontDlg.clrFore = OS.CLR_BLACK;
	 //Background color
	 fontDlg.clrBack = OS.CLR_WHITE;
         //Text for the window title
         fontDlg.pszTitle = new PSZ("Fonts");
         //Text shown in the preview area
         fontDlg.pszPreview = new PSZ("Eclipse/2");
	 if (fontData != null) {
		 fontDlg.fAttrs = fontData.getFAttrs();
	 }
	 int rc = OS.WinFontDlg(OS.HWND_DESKTOP, hwndOwner, fontDlg);
	 if(fontDlg.lReturn == OS.DID_OK){
                fontDlg.fAttrs.height = fontDlg.fxPointSize;
                fontDlg.fAttrs.fm_szFamilyname = fontDlg.pszFamilyname.toString();
                fontDlg.fAttrs.fm_usWeightClass = fontDlg.usWeight;
                fontDlg.fAttrs.fm_fsSelection = (short)fontDlg.flType;
		fontData = FontData.pm_new(fontDlg.fAttrs);
                return fontData;
	 }
 
	// /*
	// * This code is intentionally commented.  On some
	// * platforms, the owner window is repainted right
	// * away when a dialog window exits.  This behavior
	// * is currently unspecified.
	// */
 //	if (hwndOwner != 0) OS.UpdateWindow (hwndOwner);
	//
         OS.WinReleasePS (hps);
	return null;
}

/**
 * Sets a FontData object describing the font to be
 * selected by default in the dialog, or null to let
 * the platform choose one.
 * 
 * @param fontData the FontData to use initially, or null
 */
public void setFontData (FontData fontData) {
	this.fontData = fontData;
}

}
