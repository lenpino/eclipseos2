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
 * The class <code>Transfer</code> provides a mechanism for converting a Java object to a
 * platform specific format that can be passed around in a Drag and Drop operation and vice versa.
 *
 * <p>You should only need to become familiar with this class if you are implementing
 * a Transfer subclass and you are unable to subclass the ByteArrayTransfer class.</p>
 */
public abstract class Transfer {

/**
 * Returns a list of the data types that can be transferred using this Transfer agent.
 *
 * <p>Only the data type fields of the TransferData Object are filled in.</p>
 *
 * @return a list of the data types that can be transferred using this Transfer agent
 */
abstract public TransferData[] getSupportedTypes();

/**
 * Returns true if the TransferData data type can be transferred using this Transfer agent.
 *
 * @param transferData a platform specific description of a data type; only the data type fields
 *                         of the TransferData Object need to be filled in
 *
 * @return true if the transferData data type can be transferred using this Transfer agent
 */
abstract public boolean isSupportedType(TransferData transferData);

/**
 * Returns the platform specific ids of the  data types that can be transferred using this Transfer agent.
 *
 * @return the platform specific ids of the data types that can be transferred using this Transfer agent
 */
abstract protected int[] getTypeIds();

/**
 * Returns the platform specfic names of the  data types that can be transferred using this Transfer agent.
 *
 * @return the platform specfic names of the data types that can be transferred using this Transfer agent.
 */
abstract protected String[] getTypeNames();

/**
 * Converts a java representation of data to a platform specific representation of the data.
 *
 * @param object a java representation of the data to be converted; the type of
 * Object that is passed in will depend on the <code>Transfer</code> subclass.
 *
 * @param transferData an empty TransferData object; this object will be
 * filled in on return with the platform specific representation of the data
 */
abstract protected void javaToNative (Object object, TransferData transferData);

/**
 * Converts a platform specific representation of data to a java representation.
 *
 * @param transferData the platform specific representation of the data that has
 * been transferred
 *
 * @return a java representation of the converted data; the type of Object that is
 * returned will depend on the <code>Transfer</code> subclass.
 */
abstract protected Object nativeToJava(TransferData transferData);

/**
 * Registers a name for a data type and returns the associated unique identifier.
 *
 * <p>You may register the same type more than once, the same unique identifier will be returned if the
 * type has been previously registered.</p>
 *
 * <p>Note: Do <b>not</b> call this method with pre-defined Transfer types.  Pre-defined Transfer types
 * have their own way of determining their identifier.</p>
 * <p>
 * OS/2 note: the identifier is an atom of the system atom table, as the
 * formats of the clipboard of PM that are not predefined.
 * </p>
 *
 * @param formatName the name of a data type
 *
 * @return the unique identifier associated with this data type
 */
public static int registerType(String formatName){
	return OS.WinAddAtom (OS.WinQuerySystemAtomTable (), new PSZ (formatName)) & 0xFFFF;
}
}
