package org.eclipse.swt.widgets;

/*
 * Copyright (c) 2000, 2002 IBM Corp.  All rights reserved.
 * This file is made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 */

import org.eclipse.swt.internal.pm.*;
import org.eclipse.swt.*;

/**
 * Instances of this class allow the user to navigate
 * the file system and select or enter a file name.
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>SAVE, OPEN, MULTI</dd>
 * <dt><b>Events:</b></dt>
 * <dd>(none)</dd>
 * </dl>
 * <p>
 * IMPORTANT: This class is intended to be subclassed <em>only</em>
 * within the SWT implementation.
 * </p>
 */
public class FileDialog extends Dialog {
    String [] filterNames = new String [0];
    String [] filterExtensions = new String [0];
    String [] fileNames = new String [0];
    String filterPath = "", fileName = "";
    String fullPath = "";
    static final String FILTER = "*";
    
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
    public FileDialog(Shell parent) {
        this(parent, SWT.PRIMARY_MODAL);
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
    public FileDialog(Shell parent, int style) {
        super(parent, style);
        checkSubclass();
    }
    
    /**
     * Returns the path of the first file that was
     * selected in the dialog relative to the filter path,
     * or empty string if the dialog was cancelled.
     *
     * @return the relative path of the file
     */
    public String getFileName() {
        return fileName;
    }
    
    /**
     * Returns the paths of all files that were selected
     * in the dialog relative to the filter path, or null
     * if none are available.
     *
     * @return the relative paths of the files
     */
    public String [] getFileNames() {
        return fileNames;
    }
    
    /**
     * Returns the file extensions which the dialog will
     * use to filter the files it shows.
     *
     * @return the file extensions filter
     */
    public String [] getFilterExtensions() {
        return filterExtensions;
    }
    
    /**
     * Returns the file names which the dialog will
     * use to filter the files it shows.
     *
     * @return the file name filter
     */
    public String [] getFilterNames() {
        return filterNames;
    }
    
    /**
     * Returns the directory path that the dialog will use.
     * File names in this path will appear in the dialog,
     * filtered according to the filter extensions.
     *
     * @return the directory path string
     *
     * @see #setFilterExtensions
     */
    public String getFilterPath() {
        return filterPath;
    }
    
    /**
     * Makes the dialog visible and brings it to the front
     * of the display.
     *
     * @return a string describing the absolute path of the first selected file,
     *         or null if the dialog was cancelled or an error occurred
     *
     * @exception SWTException <ul>
     *    <li>ERROR_WIDGET_DISPOSED - if the dialog has been disposed</li>
     *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the dialog</li>
     * </ul>
     */
    public String open() {
        /* Get the owner HWND for the dialog */
        int hwndOwner = OS.HWND_DESKTOP;
        if (parent != null) hwndOwner = parent.handle;
        
        if (title == null) title = "";
        if (filterPath == null) filterPath = "";
        
        /* Compute filter(s) */
        String strFilter = "";
        if (filterNames == null) filterNames = new String [0];
        if (filterExtensions == null) filterExtensions = new String [0];
        for (int i=0; i<filterExtensions.length; i++) {
            String filterName = filterExtensions [i];
            if (i < filterNames.length) filterName = filterNames [i];
            strFilter = strFilter + filterName + '\0' + filterExtensions [i] + '\0';
        }
        if (filterExtensions.length == 0) {
            strFilter = strFilter + FILTER + '\0' + FILTER + '\0';
        }
        
        /* Create the file dialog struct and initialize return fields */
        FILEDLG fileDlg = new FILEDLG();
        fileDlg.ulUser = 0;
        fileDlg.lReturn = 0;
        fileDlg.lSRC = 0;
        fileDlg.pfnDlgProc = 0;
        fileDlg.pszIType = null;
        fileDlg.papszITypeList = null;
        fileDlg.pszIDrive = null;
        fileDlg.papszIDriveList = null;
        fileDlg.hMod = 0;
        fileDlg.papszFQFilename = null;
        fileDlg.ulFQFCount = 0;
        fileDlg.usDlgId = 0;
        fileDlg.x = 0;
        fileDlg.y = 0;
        fileDlg.sEAType = 0;
        
        fileDlg.fl = OS.FDS_PRELOAD_VOLINFO | OS.FDS_CENTER;
        if ((style & SWT.MULTI) != 0) {
            fileDlg.fl |= OS.FDS_MULTIPLESEL;
        }
        
        fileDlg.pszTitle = new PSZ(title);
        
        /* Convert the fileName to C strings */
        if (fileName == null) fileName = "";
        if (fileName != null && fileName.length() > 0) {
            System.arraycopy(fileName.getBytes(), 0, fileDlg.szFullFile, 0, fileName.getBytes().length);
        } else {
            System.arraycopy(strFilter.getBytes(), 0, fileDlg.szFullFile, 0, strFilter.getBytes().length);
        }
        
    /*
     * Open the dialog.  If the open fails due to an invalid
     * file name, use an empty file name and open it again.
     */
        boolean save = (style & SWT.SAVE) != 0;
        if (save) {
            fileDlg.pszOKButton = new PSZ("Save");
            fileDlg.fl |= (OS.FDS_SAVEAS_DIALOG | OS.FDS_ENABLEFILELB);
        } else {
            fileDlg.pszOKButton = new PSZ("Open");
            fileDlg.fl |= OS.FDS_OPEN_DIALOG;
        }
        
        boolean success = OS.WinFileDlg(OS.HWND_DESKTOP, hwndOwner, fileDlg);
        
        if (fileDlg.lSRC == OS.FDS_ERR_INVALID_PATHFILE) {
            fileDlg.lSRC = 0;
            System.arraycopy(new String("").getBytes(), 0, fileDlg.szFullFile, 0, 1);
            success = OS.WinFileDlg(OS.HWND_DESKTOP, hwndOwner, fileDlg);
        }
        if (fileDlg.lReturn == OS.DID_ERROR) {
            success = false;
        }
        
        /* Set the new path, file name and filter */
        fullPath = null;
        if (success) {
            String separator = System.getProperty("file.separator");
            String buffer;
            String bufferArray[];
            if ((style & SWT.MULTI) == 0) {
                bufferArray = new String[] {new String(fileDlg.szFullFile)};
            } else {
                bufferArray = new String [fileDlg.ulFQFCount];
                for (int i = 0 ; i < fileDlg.ulFQFCount; i++) {
                    if (fileDlg.papszFQFilename[i] != null){
                        bufferArray[i] = ((PSZ)fileDlg.papszFQFilename[i]).toString();
                        System.out.println("FILE = " + bufferArray[i]);
                    }
                }
            }
            
            buffer = new String(fileDlg.szFullFile);
            filterPath = buffer.substring(0, buffer.lastIndexOf(separator));
            fileName = buffer.substring(buffer.lastIndexOf(separator), buffer.length());
            fileNames = new String [bufferArray.length];
            try {
                for (int i = 0; i < bufferArray.length; i++) {
                    fileNames[i] = new String(bufferArray[i].substring(
                            bufferArray[i].lastIndexOf(separator) + 1, bufferArray[i].length()));
                }
            } catch (Exception e) {
                e.printStackTrace(System.err);
                System.err.flush();
            }
            fullPath = filterPath + fileName;
        }
//if(fileDlg.papszFQFilename != null)
//    OS.WinFreeFileDlgList (fileDlg.papszFQFilename);
        
    /*
     * This code is intentionally commented.  On some
     * platforms, the owner window is repainted right
     * away when a dialog window exits.  This behavior
     * is currently unspecified.
     */
//  if (hwndOwner != 0) OS.WinUpdateWindow (hwndOwner);
        
        /* Answer the full path or null */
        return fullPath;
    }
    
    /**
     * Set the initial filename which the dialog will
     * select by default when opened to the argument,
     * which may be null.  The name will be prefixed with
     * the filter path when one is supplied.
     *
     * @param string the file name
     */
    public void setFileName(String string) {
        fileName = string;
    }
    
    /**
     * Set the file extensions which the dialog will
     * use to filter the files it shows to the argument,
     * which may be null.
     *
     * @param extensions the file extension filter
     */
    public void setFilterExtensions(String [] extensions) {
        filterExtensions = extensions;
    }
    
    /**
     * Sets the file names which the dialog will
     * use to filter the files it shows to the argument,
     * which may be null.
     *
     * @param names the file name filter
     */
    public void setFilterNames(String [] names) {
        filterNames = names;
    }
    
    /**
     * Sets the directory path that the dialog will use
     * to the argument, which may be null. File names in this
     * path will appear in the dialog, filtered according
     * to the filter extensions.
     *
     * @param string the directory path
     *
     * @see #setFilterExtensions
     */
    public void setFilterPath(String string) {
        filterPath = string;
    }
    
}
