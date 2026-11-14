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

import org.eclipse.swt.internal.pm.*;

/**
 * The class <code>TextTransfer</code> provides a platform specific mechanism
 * for converting plain text represented as a java <code>String</code>
 * to a platform specific representation of the data and vice versa.  See
 * <code>Transfer</code> for additional information.
 *
 * <p>An example of a java <code>String</code> containing plain text is shown
 * below:</p>
 *
 * <code><pre>
 *     String textData = "Hello World";
 * </code></pre>
 * <p>
 * OS/2 note: the text is <code>CF_TEXT</code> of PM, in the code page of the
 * system and ended by a zero, as the other programs of PM read and write it.
 * </p>
 */
public class TextTransfer extends ByteArrayTransfer {

	private static TextTransfer _instance = new TextTransfer();

private TextTransfer() {}

/**
 * Returns the singleton instance of the TextTransfer class.
 *
 * @return the singleton instance of the TextTransfer class
 */
public static TextTransfer getInstance () {
	return _instance;
}

public void javaToNative (Object object, TransferData transferData){
	transferData.result = 0;
	if (object == null || !(object instanceof String) || !isSupportedType(transferData)) return;
	byte[] bytes = ((String)object).getBytes();
	int memory = allocate(bytes.length + 1);
	if (memory == 0) return;
	byte[] text = new byte[bytes.length + 1];
	System.arraycopy(bytes, 0, text, 0, bytes.length);
	OS.objcpy(memory, text);
	transferData.data = memory;
	transferData.result = 1;
}

public Object nativeToJava(TransferData transferData){
	if (!isSupportedType(transferData) || transferData.data == 0) return null;
	byte[] bytes = new byte[OS.strlen(transferData.data)];
	if (bytes.length > 0) OS.objcpy(bytes, transferData.data);
	return new String(bytes);
}

protected int[] getTypeIds(){
	return new int[] {OS.CF_TEXT};
}

protected String[] getTypeNames(){
	return new String[] {"CF_TEXT"};
}
}
