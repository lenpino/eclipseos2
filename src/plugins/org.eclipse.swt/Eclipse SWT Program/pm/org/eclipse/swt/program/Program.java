/*
 * Copyright (c) 2000, 2002 IBM Corp. All rights reserved.
 * This file is made available under the Common Public License v1.0.
 */
package org.eclipse.swt.program;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.internal.pm.OS;
import org.eclipse.swt.internal.pm.PSZ;
import org.eclipse.swt.widgets.Display;

/** A filesystem program associated with files by the OS/2 Workplace Shell. */
public final class Program {
    private static final PSZ ASSOCIATIONS = new PSZ ("PMWP_ASSOC_FILTER");
    private final String path;

    private Program (String path) {
        this.path = path;
    }

    /**
     * Finds the first file-backed handler for a plain extension filter.
     * An extension alone does not identify a WPS file type or a filename rule.
     */
    public static Program findProgram (String extension) {
        if (extension == null) SWT.error (SWT.ERROR_NULL_ARGUMENT);
        if (!extension.startsWith (".")) extension = "." + extension;
        if (!isExtension (extension)) return null;
        String filter = "*" + extension;
        String[] keys = readAssociation (null);
        for (int i = 0; i < keys.length; i++) {
            if (keys [i].equalsIgnoreCase (filter)) {
                String[] handles = readAssociation (new PSZ (keys [i]));
                // The first handle is the configured default. Do not substitute another.
                return handles.length == 0 ? null : fromHandle (handles [0]);
            }
        }
        return null;
    }

    /** Returns the extensions with a supported default association. */
    public static String[] getExtensions () {
        ArrayList extensions = new ArrayList ();
        String[] keys = readAssociation (null);
        for (int i = 0; i < keys.length; i++) {
            if (!keys [i].startsWith ("*.")) continue;
            String extension = keys [i].substring (1).toLowerCase (Locale.ENGLISH);
            if (!isExtension (extension) || extensions.contains (extension)) continue;
            String[] handles = readAssociation (new PSZ (keys [i]));
            if (handles.length > 0 && fromHandle (handles [0]) != null) extensions.add (extension);
        }
        return (String[]) extensions.toArray (new String [extensions.size ()]);
    }

    /** Returns the file-backed programs registered in the WPS name filters. */
    public static Program[] getPrograms () {
        ArrayList programs = new ArrayList ();
        String[] keys = readAssociation (null);
        for (int i = 0; i < keys.length; i++) {
            String[] handles = readAssociation (new PSZ (keys [i]));
            for (int j = 0; j < handles.length; j++) {
                Program program = fromHandle (handles [j]);
                if (program != null && !programs.contains (program)) programs.add (program);
            }
        }
        return (Program[]) programs.toArray (new Program [programs.size ()]);
    }

    /** Opens an existing file using its default Workplace Shell view. */
    public static boolean launch (String fileName) {
        if (fileName == null) SWT.error (SWT.ERROR_NULL_ARGUMENT);
        if (!validPath (fileName)) return false;
        return OS.WinOpenDefaultObject (new PSZ (new File (fileName).getAbsolutePath ()));
    }

    /** Starts this file-backed program with the supplied file as an argument. */
    public boolean execute (String fileName) {
        if (fileName == null) SWT.error (SWT.ERROR_NULL_ARGUMENT);
        if (fileName.length () != 0 && !validPath (fileName)) return false;
        try {
            String[] args = fileName.length () == 0 ? new String[] {path} : new String[] {path, fileName};
            Runtime.getRuntime ().exec (args);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /** Returns the program icon, or null when no icon is available. */
    public ImageData getImageData () {
        Display display = Display.getCurrent ();
        if (display == null) return null;
        int icon = OS.WinLoadFileIcon (new PSZ (path));
        if (icon == 0) return null;
        try {
            return Image.pm_new (display, SWT.ICON, icon).getImageData ();
        } finally {
            OS.WinFreeFileIcon (icon);
        }
    }

    public String getName () {
        return new File (path).getName ();
    }

    public boolean equals (Object object) {
        return object instanceof Program && path.equalsIgnoreCase (((Program) object).path);
    }

    public int hashCode () {
        return path.toUpperCase (Locale.ENGLISH).hashCode ();
    }

    public String toString () {
        return "Program {" + getName () + "}";
    }

    private static boolean validPath (String value) {
        return value.length () > 0 && value.indexOf ('\0') == -1;
    }

    private static boolean isExtension (String extension) {
        return extension.length () > 1 && extension.indexOf ('.', 1) == -1 &&
            extension.indexOf ('*') == -1 && extension.indexOf ('?') == -1 &&
            extension.indexOf ('\\') == -1 && extension.indexOf ('/') == -1 &&
            extension.indexOf (':') == -1 && extension.indexOf ('\0') == -1;
    }

    private static Program fromHandle (String value) {
        try {
            long handle = Long.parseLong (value);
            if (handle <= 0 || handle > 0xffffffffL) return null;
            byte[] path = new byte [OS.CCHMAXPATH];
            if (OS.WinQueryObjectPath ((int) handle, path)) {
                String fileName = new PSZ (path).toString ();
                if (validPath (fileName)) return new Program (fileName);
            }
        } catch (NumberFormatException e) {
            // Ignore malformed profile entries.
        }
        return null;
    }

    private static String[] readAssociation (PSZ key) {
        int[] length = new int [1];
        if (!OS.PrfQueryProfileSize (OS.HINI_USERPROFILE, ASSOCIATIONS, key, length) || length [0] <= 0) {
            return new String [0];
        }
        byte[] data = new byte [length [0] + 1];
        if (!OS.PrfQueryProfileData (OS.HINI_USERPROFILE, ASSOCIATIONS, key, data, length)) {
            return new String [0];
        }
        ArrayList entries = new ArrayList ();
        int start = 0;
        for (int end = 0; end <= length [0]; end++) {
            if (data [end] != 0) continue;
            if (end == start) break;
            entries.add (new String (data, start, end - start));
            start = end + 1;
        }
        return (String[]) entries.toArray (new String [entries.size ()]);
    }
}
