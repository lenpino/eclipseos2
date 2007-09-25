package org.eclipse.swt.widgets;

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
 * Instances of this class are used used to inform or warn the user.
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>ICON_ERROR, ICON_INFORMATION, ICON_QUESTION, ICON_WARNING, ICON_WORKING</dd>
 * <dd>OK, OK | CANCEL</dd>
 * <dd>YES | NO, YES | NO | CANCEL</dd>
 * <dd>RETRY | CANCEL</dd>
 * <dd>ABORT | RETRY | IGNORE</dd>
 * <dt><b>Events:</b></dt>
 * <dd>(none)</dd>
 * </dl>
 * <p>
 * Note: Only one of the styles ICON_ERROR, ICON_INFORMATION, ICON_QUESTION,
 * ICON_WARNING and ICON_WORKING may be specified.
 * </p><p>
 * IMPORTANT: This class is intended to be subclassed <em>only</em>
 * within the SWT implementation.
 * </p>
 */
public  class MessageBox extends Dialog {
	String message = "";
	
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
public MessageBox (Shell parent) {
	this (parent, SWT.OK | SWT.ICON_INFORMATION | SWT.APPLICATION_MODAL);
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
public MessageBox (Shell parent, int style) {
	super (parent, checkStyle (style));
	checkSubclass ();
}

static int checkStyle (int style) {
	if ((style & (SWT.PRIMARY_MODAL | SWT.APPLICATION_MODAL | SWT.SYSTEM_MODAL)) == 0) style |= SWT.APPLICATION_MODAL;
	int mask = (SWT.YES | SWT.NO | SWT.OK | SWT.CANCEL | SWT.ABORT | SWT.RETRY | SWT.IGNORE);
	int bits = style & mask;
	if (bits == SWT.OK || bits == SWT.CANCEL || bits == (SWT.OK | SWT.CANCEL)) return style;
	if (bits == SWT.YES || bits == SWT.NO || bits == (SWT.YES | SWT.NO) || bits == (SWT.YES | SWT.NO | SWT.CANCEL)) return style;
	if (bits == (SWT.RETRY | SWT.CANCEL) || bits == (SWT.ABORT | SWT.RETRY | SWT.IGNORE)) return style;
	style = (style & ~mask) | SWT.OK;
	return style;
}

/**
 * Returns the dialog's message, which is a description of
 * the purpose for which it was opened. This message will be
 * visible on the dialog while it is open.
 *
 * @return the message
 */
public String getMessage () {
	return message;
}

/**
 * Makes the dialog visible and brings it to the front
 * of the display.
 *
 * @return the ID of the button that was selected to dismiss the
 *         message box (e.g. SWT.OK, SWT.CANCEL, etc...)
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the dialog has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the dialog</li>
 * </ul>
 */
public int open () {

	/* Compute the MessageBox style */
	int buttonBits = 0;
	if ((style & SWT.OK) == SWT.OK) buttonBits = OS.MB_OK;
	if ((style & (SWT.OK | SWT.CANCEL)) == (SWT.OK | SWT.CANCEL)) buttonBits = OS.MB_OKCANCEL;
	if ((style & (SWT.YES | SWT.NO)) == (SWT.YES | SWT.NO)) buttonBits = OS.MB_YESNO;
	if ((style & (SWT.YES | SWT.NO | SWT.CANCEL)) == (SWT.YES | SWT.NO | SWT.CANCEL)) buttonBits = OS.MB_YESNOCANCEL;
	if ((style & (SWT.RETRY | SWT.CANCEL)) == (SWT.RETRY | SWT.CANCEL)) buttonBits = OS.MB_RETRYCANCEL;
	if ((style & (SWT.ABORT | SWT.RETRY | SWT.IGNORE)) == (SWT.ABORT | SWT.RETRY | SWT.IGNORE)) buttonBits = OS.MB_ABORTRETRYIGNORE;
	if (buttonBits == 0) buttonBits = OS.MB_OK;

	int iconBits = 0;
	if ((style & SWT.ICON_ERROR) != 0) iconBits = OS.MB_ERROR;
	if ((style & SWT.ICON_INFORMATION) != 0) iconBits = OS.MB_INFORMATION;
	if ((style & SWT.ICON_QUESTION) != 0) iconBits = OS.MB_QUERY;
	if ((style & SWT.ICON_WARNING) != 0) iconBits = OS.MB_WARNING;
	if ((style & SWT.ICON_WORKING) != 0) iconBits = OS.MB_INFORMATION;

	int modalBits = 0;
	/*
	* Feature in OS/2.  Primary modal is not supported
	* on OS/2.  The fix is to convert primary
	* modal to application modal.
	*/
	if ((style & SWT.PRIMARY_MODAL) != 0) modalBits = OS.MB_APPLMODAL;
	if ((style & SWT.APPLICATION_MODAL) != 0) modalBits = OS.MB_APPLMODAL;
	if ((style & SWT.SYSTEM_MODAL) != 0) modalBits = OS.MB_SYSTEMMODAL;
	
	int bits = buttonBits | iconBits | modalBits | OS.MB_MOVEABLE;
	
	/* Get the parent's handle. */
	int hwndOwner = 0;
	if (parent != null) {
		hwndOwner = parent.handle;
	}

	/* Open the message box */
	/* Use the character encoding for the default locale */
	PSZ buffer1 = new PSZ (message);
	PSZ buffer2 = new PSZ (title);
	int code = OS.WinMessageBox (OS.HWND_DESKTOP, hwndOwner, buffer1, buffer2, 0, bits);
	
	/*
	* This code is intentionally commented.  On some
	* platforms, the owner window is repainted right
	* away when a dialog window exits.  This behavior
	* is currently unspecified.
	*/
//	if (hwndOwner != 0) OS.WinUpdateWindow (hwndOwner);
	
	/* Compute and return the result */
	if (code != 0) {
		int type = bits & 0x0F;
		if (type == OS.MB_OK) return SWT.OK;
		if (type == OS.MB_OKCANCEL) {
			return (code == OS.MBID_OK) ? SWT.OK : SWT.CANCEL;
		}
		if (type == OS.MB_YESNO) {
			return (code == OS.MBID_YES) ? SWT.YES : SWT.NO;
		}
		if (type == OS.MB_YESNOCANCEL) {
			if (code == OS.MBID_YES) return SWT.YES;
			if (code == OS.MBID_NO) return SWT.NO;
			return SWT.CANCEL;
		}
		if (type == OS.MB_RETRYCANCEL) {
			return (code == OS.MBID_RETRY) ? SWT.RETRY : SWT.CANCEL;
		}
		if (type == OS.MB_ABORTRETRYIGNORE) {
			if (code == OS.MBID_RETRY) return SWT.RETRY;
			if (code == OS.MBID_ABORT) return SWT.ABORT;
			return SWT.IGNORE;
		}
	}
	return SWT.CANCEL;
}

/**
 * Sets the dialog's message, which is a description of
 * the purpose for which it was opened. This message will be
 * visible on the dialog while it is open.
 *
 * @param string the message
 */
public void setMessage (String string) {
	message = string;
}

}
