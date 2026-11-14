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

/**
 * The class <code>RTFTransfer</code> provides a platform specific mechanism
 * for converting text in RTF format represented as a java <code>String</code>
 * to a platform specific representation of the data and vice versa.  See
 * <code>Transfer</code> for additional information.
 *
 * <p>An example of a java <code>String</code> containing RTF text is shown
 * below:</p>
 *
 * <code><pre>
 *     String rtfData = "{\\rtf1{\\colortbl;\\red255\\green0\\blue0;}\\uc1\\b\\i Hello World}";
 * </code></pre>
 * <p>
 * OS/2 note: PM has no predefined format for RTF: it is the registered format
 * "Rich Text Format" (the name of Windows), whose data is the text in the code
 * page of the system preceded by its length (see <code>ByteArrayTransfer</code>).
 * </p>
 */
public class RTFTransfer extends ByteArrayTransfer {

	private static RTFTransfer _instance = new RTFTransfer();
	private static final String CF_RTF = "Rich Text Format";
	private static final int CF_RTFID = registerType(CF_RTF);

private RTFTransfer() {}

/**
 * Returns the singleton instance of the RTFTransfer class.
 *
 * @return the singleton instance of the RTFTransfer class
 */
public static RTFTransfer getInstance () {
	return _instance;
}

public void javaToNative (Object object, TransferData transferData){
	if (object == null || !(object instanceof String)) {
		transferData.result = 0;
		return;
	}
	super.javaToNative(((String)object).getBytes(), transferData);
}

public Object nativeToJava(TransferData transferData){
	byte[] bytes = (byte[])super.nativeToJava(transferData);
	return bytes == null ? null : new String(bytes);
}

protected int[] getTypeIds(){
	return new int[] {CF_RTFID};
}

protected String[] getTypeNames(){
	return new String[] {CF_RTF};
}
}
