package org.eclipse.swt.internal.pm;

/*
 * Copyright (c) 2002, 2004 EclipseOS2 Team.
 * This file is made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 */

/**
 * This class implements the conversions between unicode characters
 * and the <em>platform supported</em> representation for characters.
 * <p>
 * Note that, unicode characters which can not be found in the platform
 * encoding will be converted to an arbitrary platform specific character.
 * </p>
 */

public class PSZ {
    
byte[] bytes;

static byte[] EMPTY = new byte[1];

public PSZ() {
    bytes = EMPTY;
}

/**
 *  This method creates and returns the string representation
 *  of the integer atom that has a form of "#ddddd", where ddddd is
 *  a low word of an integer value given as an <code>atom</code>
 *  parameter. If its high word is not 0xFFFF, null is returned.
 */
public static PSZ getAtom (int atom) {
    if ((atom & 0xFFFF0000) != 0xFFFF0000) return null;
    return new PSZ ("#"+String.valueOf (atom & 0x0000FFFF));
}

public PSZ (String string) {
    if (string == null) {
        bytes = EMPTY;
    } else {
        int length = string.length();
        bytes = new byte [length + 1];
        System.arraycopy (string.getBytes(), 0, bytes, 0, length);
    }
}

public PSZ (int length) {
    bytes = new byte [length + 1];
}

public PSZ (byte[] bytes) {
    this.bytes = bytes;
}

public byte[] getBytes() {
    return bytes;
}

public String toString() { 
    int length = 0;
    while (length < bytes.length && bytes [length] != 0) length++;
    return new String (bytes, 0, length);
}

public boolean equals (Object obj) {
    if (!(obj instanceof PSZ)) return false;
    return (obj == this || java.util.Arrays.equals (((PSZ)obj).bytes, bytes));
}

}
