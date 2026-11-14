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
 * The class <code>ByteArrayTransfer</code> provides a platform specific mechanism
 * for converting a java <code>byte[]</code> to a platform specific representation
 * of the byte array and vice versa.  See <code>Transfer</code> for additional information.
 *
 * <p><code>ByteArrayTransfer</code> is never used directly but is sub-classed
 * by transfer agents that convert between data in a java format such as a
 * <code>String</code> and a platform specific byte array.</p>
 * <p>
 * OS/2 note: the clipboard of PM does not keep the size of the data that it is
 * given by address, so the bytes are preceded by their count (4 bytes). The
 * memory is shared and giveable (<code>DosAllocSharedMem</code>), as the
 * clipboard wants it.
 * </p>
 */
public abstract class ByteArrayTransfer extends Transfer {

public TransferData[] getSupportedTypes(){
	int[] types = getTypeIds();
	TransferData[] data = new TransferData[types.length];
	for (int i = 0; i < types.length; i++) {
		data[i] = new TransferData();
		data[i].type = types[i];
	}
	return data;
}

public boolean isSupportedType(TransferData transferData){
	if (transferData == null) return false;
	int[] types = getTypeIds();
	for (int i = 0; i < types.length; i++) {
		if (transferData.type == types[i]) return true;
	}
	return false;
}

protected void javaToNative (Object object, TransferData transferData){
	transferData.result = 0;
	if (object == null || !(object instanceof byte[]) || !isSupportedType(transferData)) return;
	byte[] data = (byte[])object;
	int memory = allocate(4 + data.length);
	if (memory == 0) return;
	OS.objcpy(memory, new byte[] {(byte) data.length, (byte) (data.length >> 8), (byte) (data.length >> 16), (byte) (data.length >> 24)});
	if (data.length > 0) OS.objcpy(memory + 4, data);
	transferData.data = memory;
	transferData.result = 1;
}

protected Object nativeToJava(TransferData transferData){
	if (!isSupportedType(transferData) || transferData.data == 0) return null;
	byte[] count = new byte[4];
	OS.objcpy(count, transferData.data);
	int size = (count[0] & 0xFF) | ((count[1] & 0xFF) << 8) | ((count[2] & 0xFF) << 16) | ((count[3] & 0xFF) << 24);
	if (size < 0) return null;
	byte[] data = new byte[size];
	if (size > 0) OS.objcpy(data, transferData.data + 4);
	return data;
}

/*
 * Memory that the clipboard can take: shared, giveable, committed. The clipboard frees it
 * when it is emptied. Zero if it cannot be had.
 */
static int allocate(int size) {
	int[] address = new int[1];
	int rc = OS.DosAllocSharedMem(address, Math.max(size, 1), OS.OBJ_GIVEABLE | OS.PAG_COMMIT | OS.PAG_READ | OS.PAG_WRITE);
	return rc == 0 ? address[0] : 0;
}
}
