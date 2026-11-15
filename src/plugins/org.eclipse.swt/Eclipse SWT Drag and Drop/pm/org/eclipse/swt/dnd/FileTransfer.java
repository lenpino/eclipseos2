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
 * The class <code>FileTransfer</code> provides a platform specific mechanism
 * for converting a list of files represented as a java <code>String[]</code> to a
 * platform specific representation of the data and vice versa.
 * Each <code>String</code> in the array contains the absolute path for a single
 * file or directory.
 * See <code>Transfer</code> for additional information.
 * <p>
 * OS/2 note: the list is the registered format "FileName" with the paths
 * separated by a zero, in the code page of the system.
 * </p>
 */
public class FileTransfer extends ByteArrayTransfer {

	private static FileTransfer _instance = new FileTransfer();
	private static final String TYPENAME = "FileName";
	private static final int TYPEID = registerType(TYPENAME);

private FileTransfer() {}

/**
 * Returns the singleton instance of the FileTransfer class.
 *
 * @return the singleton instance of the FileTransfer class
 */
public static FileTransfer getInstance () {
	return _instance;
}

public void javaToNative(Object object, TransferData transferData) {
	if (object == null || !(object instanceof String[])) {
		transferData.result = 0;
		return;
	}
	String[] files = (String[])object;
	StringBuffer buffer = new StringBuffer();
	for (int i = 0; i < files.length; i++) {
		if (i > 0) buffer.append('\0');
		buffer.append(files[i]);
	}
	super.javaToNative(buffer.toString().getBytes(), transferData);
}

public Object nativeToJava(TransferData transferData) {
	byte[] data = (byte[])super.nativeToJava(transferData);
	if (data == null) return null;
	String string = new String(data);
	java.util.Vector names = new java.util.Vector();
	int start = 0;
	while (start <= string.length()) {
		int end = string.indexOf('\0', start);
		if (end == -1) end = string.length();
		if (end > start) names.addElement(string.substring(start, end));
		start = end + 1;
	}
	String[] result = new String[names.size()];
	names.copyInto(result);
	return result;
}

protected int[] getTypeIds(){
	return new int[]{TYPEID};
}

protected String[] getTypeNames(){
	return new String[]{TYPENAME};
}
}
