package org.eclipse.swt.dnd;

/*
 * OS/2 version.
 * Copyright (c) 2002, 2026 EclipseOS2 Team.
 */

/*
 * Copyright (c) 2000, 2002 IBM Corp.  All rights reserved.
 * This file is made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 */

import org.eclipse.swt.*;
import org.eclipse.swt.internal.pm.*;
import org.eclipse.swt.widgets.*;

/**
 * The <code>Clipboard</code> provides a mechanism for transferring data from one
 * application to another or within an application.
 *
 * <p>IMPORTANT: This class is <em>not</em> intended to be subclassed.</p>
 * <p>
 * OS/2 note: this is the clipboard of PM. The data of every format is put in it
 * at once, when <code>setContents</code> is called (PM can ask the owner for it
 * later, which is not used), and is shared memory that the clipboard frees when
 * it is emptied.
 * </p>
 */
public class Clipboard {

	Display display;
	/* The clipboard can be in use by another program: it is tried again, every 10 ms */
	static final int MAX_RETRIES = 10;

/**
 * Constructs a new instance of this class.  Creating an instance of a Clipboard
 * may cause system resources to be allocated depending on the platform.  It is therefore
 * mandatory that the Clipboard instance be disposed when no longer required.
 *
 * @param display the display on which to allocate the clipboard
 *
 * @exception SWTException <ul>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li>
 * </ul>
 *
 * @see Clipboard#dispose
 * @see Clipboard#checkSubclass
 */
public Clipboard(Display display) {
	checkSubclass ();
	if (display == null) {
		display = Display.getCurrent();
		if (display == null) {
			display = Display.getDefault();
		}
	}
	if (display.getThread() != Thread.currentThread()) {
		SWT.error(SWT.ERROR_THREAD_INVALID_ACCESS);
	}
	this.display = display;
}

/**
 * Checks that this class can be subclassed.
 * <p>
 * The SWT class library is intended to be subclassed
 * only at specific, controlled points. This method enforces this
 * rule unless it is overridden.
 * </p><p>
 * <em>IMPORTANT:</em> By providing an implementation of this
 * method that allows a subclass of a class which does not
 * normally allow subclassing to be created, the implementer
 * agrees to be fully responsible for the fact that any such
 * subclass will likely fail between SWT releases and will be
 * strongly platform specific. No support is provided for
 * user-written classes which are implemented in this fashion.
 * </p><p>
 * The ability to subclass outside of the allowed SWT classes
 * is intended purely to enable those not on the SWT development
 * team to implement patches in order to get around specific
 * limitations in advance of when those limitations can be
 * addressed by the team. Subclassing should not be attempted
 * without an intimate and detailed understanding of the hierarchy.
 * </p>
 *
 * @exception SWTException <ul>
 *    <li>ERROR_INVALID_SUBCLASS - if this class is not an allowed subclass</li>
 * </ul>
 */
protected void checkSubclass () {
	String name = getClass().getName ();
	String validName = Clipboard.class.getName();
	if (!validName.equals(name)) {
		DND.error (SWT.ERROR_INVALID_SUBCLASS);
	}
}

/**
 * Disposes of the operating system resources associated with the clipboard.
 * The data will still be available on the system clipboard after the dispose
 * method is called.
 *
 * <p>NOTE: On some platforms the data will not be available once the application
 * has exited or the display has been disposed.</p>
 */
public void dispose () {
	display = null;
}

/**
 * Retrieve the data of the specified type currently available on the system clipboard.  Refer to the
 * specific subclass of <code>Tramsfer</code> to determine the type of object returned.
 *
 * <p>The following snippet shows text and RTF text being retrieved from the clipboard:</p>
 *
 *    <code><pre>
 *    Clipboard clipboard = new Clipboard(display);
 *    TextTransfer textTransfer = TextTransfer.getInstance();
 *    String textData = (String)clipboard.getContents(textTransfer);
 *    if (textData != null) System.out.println("Text is "+textData);
 *    clipboard.dispose();
 *    </code></pre>
 *
 * @see Transfer
 *
 * @param transfer the transfer agent for the type of data being requested
 *
 * @return the data obtained from the clipboard or null if no data of this type is available
 */
public Object getContents(Transfer transfer) {
	if (transfer == null) DND.error(SWT.ERROR_NULL_ARGUMENT);
	int hab = Display.internal_get_HAB ();
	if (!open (hab)) return null;
	try {
		TransferData[] allowed = transfer.getSupportedTypes();
		for (int i = 0; i < allowed.length; i++) {
			int data = OS.WinQueryClipbrdData (hab, allowed[i].type);
			if (data != 0) {
				allowed[i].data = data;
				return transfer.nativeToJava(allowed[i]);
			}
		}
		return null;
	} finally {
		OS.WinCloseClipbrd (hab);
	}
}

/**
 * Place data of the specified type on the system clipboard.  More than one type of
 * data can be placed on the system clipboard at the same time.  Setting the data
 * clears any previous data of the same type from the system clipboard and also
 * clears data of any other type currently on the system clipboard.
 *
 * <p>NOTE: On some platforms, the data is immediately copied to the system
 * clipboard but on other platforms it is provided upon request.  As a result, if the
 * application modifes the data object it has set on the clipboard, that modification
 * may or may not be available when the data is subsequently requested.</p>
 *
 * <p>The following snippet shows text and RTF text being set on the clipboard:</p>
 *
 * <code><pre>
 * 	Clipboard clipboard = new Clipboard(display);
 *		String textData = "Hello World";
 *		String rtfData = "{\\rtf1\\b\\i Hello World}";
 *		TextTransfer textTransfer = TextTransfer.getInstance();
 *		RTFTransfer rtfTransfer = RTFTransfer.getInstance();
 *		clipboard.setContents(new Object[]{textData, rtfData}, new Transfer[]{textTransfer, rtfTransfer});
 *		clipboard.dispose();
 * </code></pre>
 *
 * @param data the data to be set in the clipboard
 * @param dataTypes the transfer agents that will convert the data to its platform
 * specific format; each entry in the data array must have a corresponding dataType
 *
 * @exception SWTException <ul>
 *    <li>ERROR_INVALID_ARGUMENT - if data is null or datatypes is null
 *          or the length of data is not the same as the length of dataTypes</li>
 * </ul>
 *  @exception SWTError <ul>
 *    <li>ERROR_CANNOT_SET_CLIPBOARD - if the clipboard is locked or
 *         otherwise unavailable</li>
 * </ul>
 */
public void setContents(Object[] data, Transfer[] dataTypes){
	if (data == null || dataTypes == null || data.length != dataTypes.length) {
		DND.error(SWT.ERROR_INVALID_ARGUMENT);
	}
	int hab = Display.internal_get_HAB ();
	if (!open (hab)) DND.error(DND.ERROR_CANNOT_SET_CLIPBOARD);
	boolean set = true;
	try {
		OS.WinEmptyClipbrd (hab);
		for (int i = 0; i < dataTypes.length; i++) {
			TransferData[] types = dataTypes[i].getSupportedTypes();
			for (int j = 0; j < types.length; j++) {
				dataTypes[i].javaToNative(data[i], types[j]);
				if (types[j].result != 1 || types[j].data == 0) continue;
				/* The clipboard owns the memory once it has taken it */
				if (!OS.WinSetClipbrdData (hab, types[j].data, types[j].type, OS.CFI_POINTER)) {
					OS.DosFreeMem (types[j].data);
					set = false;
				}
			}
		}
	} finally {
		OS.WinCloseClipbrd (hab);
	}
	if (!set) DND.error(DND.ERROR_CANNOT_SET_CLIPBOARD);
}

/**
 * Returns a platform specific list of the data types currently available on the
 * system clipboard.
 *
 * <p>Note: <code>getAvailableTypeNames</code> is a utility for writing a Transfer
 * sub-class.  It should NOT be used within an application because it provides
 * platform specific information.</p>
 *
 * @return a platform specific list of the data types currently available on the
 * system clipboard
 */
public String[] getAvailableTypeNames() {
	int hab = Display.internal_get_HAB ();
	if (!open (hab)) return new String[0];
	java.util.Vector names = new java.util.Vector ();
	try {
		int atoms = OS.WinQuerySystemAtomTable ();
		for (int format = OS.WinEnumClipbrdFmts (hab, 0); format != 0; format = OS.WinEnumClipbrdFmts (hab, format)) {
			names.addElement (formatName (atoms, format));
		}
	} finally {
		OS.WinCloseClipbrd (hab);
	}
	String[] result = new String[names.size ()];
	names.copyInto (result);
	return result;
}

/* The name of a format: the predefined ones of PM, or the atom of a registered one */
static String formatName (int atoms, int format) {
	switch (format) {
		case OS.CF_TEXT: return "CF_TEXT";
		case OS.CF_BITMAP: return "CF_BITMAP";
		case OS.CF_DSPTEXT: return "CF_DSPTEXT";
		case OS.CF_DSPBITMAP: return "CF_DSPBITMAP";
		case OS.CF_METAFILE: return "CF_METAFILE";
		case OS.CF_DSPMETAFILE: return "CF_DSPMETAFILE";
		case OS.CF_PALETTE: return "CF_PALETTE";
	}
	PSZ buffer = new PSZ (256);
	int length = OS.WinQueryAtomName (atoms, format, buffer, 256);
	return length > 0 ? buffer.toString () : "Unknown format " + format;
}

/* Opens the clipboard, waiting a little while another program has it */
static boolean open (int hab) {
	for (int retries = 0; retries < MAX_RETRIES; retries++) {
		if (OS.WinOpenClipbrd (hab)) return true;
		try {Thread.sleep(10);} catch (InterruptedException e) {}
	}
	return false;
}
}
