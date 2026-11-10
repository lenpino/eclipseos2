package org.eclipse.swt.widgets;

/*
 * Copyright (c) 2000, 2026 IBM Corp. and the EclipseOS2 Team.  All rights reserved.
 * This file is made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 */

import java.io.File;
import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TreeAdapter;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.graphics.Rectangle;

/**
 * Instances of this class allow the user to navigate
 * the file system and select a directory.
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>(none)</dd>
 * <dt><b>Events:</b></dt>
 * <dd>(none)</dd>
 * </dl>
 * <p>
 * IMPORTANT: This class is intended to be subclassed <em>only</em>
 * within the SWT implementation.
 * </p>
 * <p>
 * PM has no dialog to choose a directory (<code>WinFileDlg</code> chooses files),
 * so this one is made of SWT widgets: a tree of the drives and directories that
 * reads the children of a directory when it is expanded.
 * </p>
 */
public class DirectoryDialog extends Dialog {
	String filterPath = "";
	String message = "";
	String result;

	static final int WIDTH = 340, HEIGHT = 380, MARGIN = 8, BUTTON_WIDTH = 80, BUTTON_HEIGHT = 26, LABEL_HEIGHT = 34;

	/* A child that every directory has until it is expanded, so that it shows a plus sign */
	static final String PENDING = "...";

/**
 * Constructs a new instance of this class given only its
 * parent.
 *
 * @param parent a shell which will be the parent of the new instance
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li>
 * </ul>
 */
public DirectoryDialog (Shell parent) {
	this (parent, SWT.PRIMARY_MODAL);
}

/**
 * Constructs a new instance of this class given its parent
 * and a style value describing its behavior and appearance.
 *
 * @param parent a shell which will be the parent of the new instance
 * @param style the style of dialog to construct
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li>
 * </ul>
 */
public DirectoryDialog (Shell parent, int style) {
	super (parent, style);
	checkSubclass ();
}

/**
 * Returns the path which the dialog will use to filter
 * the directories it shows, or the directory that was
 * selected when the dialog was closed.
 *
 * @return the filter path
 */
public String getFilterPath () {
	return filterPath;
}

/**
 * Returns the dialog's message, which is a description of
 * the purpose for which it was opened.
 *
 * @return the message
 */
public String getMessage () {
	return message;
}

/**
 * Sets the path that the dialog will use to filter
 * the directories it shows to the argument, which may
 * be null. The directory is selected and shown when the
 * dialog is opened.
 *
 * @param string the filter path
 */
public void setFilterPath (String string) {
	filterPath = string;
}

/**
 * Sets the dialog's message, which is a description of
 * the purpose for which it was opened.
 *
 * @param string the message
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the string is null</li>
 * </ul>
 */
public void setMessage (String string) {
	if (string == null) error (SWT.ERROR_NULL_ARGUMENT);
	message = string;
}

/**
 * Makes the dialog visible and brings it to the front
 * of the display.
 *
 * @return a string describing the absolute path of the selected directory,
 *         or null if the dialog was cancelled or an error occurred
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the dialog has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the dialog</li>
 * </ul>
 */
public String open () {
	result = null;
	final Display display = parent.getDisplay ();
	final Shell shell = new Shell (parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
	shell.setText (title);
	final Label label = new Label (shell, SWT.WRAP);
	label.setText (message);
	final Tree tree = new Tree (shell, SWT.SINGLE | SWT.BORDER);
	final Button ok = new Button (shell, SWT.PUSH);
	ok.setText ("OK");
	final Button cancel = new Button (shell, SWT.PUSH);
	cancel.setText ("Cancel");

	shell.setSize (WIDTH, HEIGHT);
	layout (shell, label, tree, ok, cancel);
	shell.addListener (SWT.Resize, new Listener () {
		public void handleEvent (Event event) {
			layout (shell, label, tree, ok, cancel);
		}
	});

	File [] roots = File.listRoots ();
	if (roots != null) {
		for (int i=0; i<roots.length; i++) {
			TreeItem item = new TreeItem (tree, SWT.NONE);
			item.setText (roots [i].getPath ());
			item.setData (roots [i]);
			new TreeItem (item, SWT.NONE).setText (PENDING);
		}
	}
	tree.addTreeListener (new TreeAdapter () {
		public void treeExpanded (TreeEvent e) {
			populate ((TreeItem) e.item);
		}
	});
	showPath (tree, filterPath);

	tree.addSelectionListener (new SelectionAdapter () {
		public void widgetDefaultSelected (SelectionEvent e) {
			accept (tree, shell);
		}
	});
	ok.addSelectionListener (new SelectionAdapter () {
		public void widgetSelected (SelectionEvent e) {
			accept (tree, shell);
		}
	});
	cancel.addSelectionListener (new SelectionAdapter () {
		public void widgetSelected (SelectionEvent e) {
			shell.dispose ();
		}
	});

	/* Centered over the parent, or over the screen */
	Rectangle area = parent.isVisible () ? parent.getBounds () : display.getBounds ();
	Rectangle size = shell.getBounds ();
	shell.setLocation (area.x + Math.max (0, (area.width - size.width) / 2), area.y + Math.max (0, (area.height - size.height) / 2));

	shell.open ();
	while (!shell.isDisposed ()) {
		if (!display.readAndDispatch ()) display.sleep ();
	}
	if (result != null) filterPath = result;
	return result;
}

void layout (Shell shell, Label label, Tree tree, Button ok, Button cancel) {
	Rectangle area = shell.getClientArea ();
	int width = Math.max (area.width - 2 * MARGIN, 10);
	int buttons = area.height - MARGIN - BUTTON_HEIGHT;
	label.setBounds (MARGIN, MARGIN, width, LABEL_HEIGHT);
	tree.setBounds (MARGIN, 2 * MARGIN + LABEL_HEIGHT, width, Math.max (buttons - 2 * MARGIN - LABEL_HEIGHT, 10));
	cancel.setBounds (area.width - MARGIN - BUTTON_WIDTH, buttons, BUTTON_WIDTH, BUTTON_HEIGHT);
	ok.setBounds (area.width - 2 * MARGIN - 2 * BUTTON_WIDTH, buttons, BUTTON_WIDTH, BUTTON_HEIGHT);
}

void accept (Tree tree, Shell shell) {
	TreeItem [] selection = tree.getSelection ();
	if (selection.length == 0 || !(selection [0].getData () instanceof File)) return;
	result = ((File) selection [0].getData ()).getPath ();
	shell.dispose ();
}

/* Replaces the child that stands for the directories with the subdirectories of the directory */
void populate (TreeItem item) {
	if (item == null || item.isDisposed ()) return;
	TreeItem [] children = item.getItems ();
	if (children.length != 1 || !PENDING.equals (children [0].getText ())) return;
	children [0].dispose ();
	File directory = (File) item.getData ();
	String [] names = directory == null ? null : directory.list ();
	if (names == null) return;
	Arrays.sort (names, String.CASE_INSENSITIVE_ORDER);
	for (int i=0; i<names.length; i++) {
		File file = new File (directory, names [i]);
		if (!file.isDirectory ()) continue;
		TreeItem child = new TreeItem (item, SWT.NONE);
		child.setText (names [i]);
		child.setData (file);
		new TreeItem (child, SWT.NONE).setText (PENDING);
	}
}

/* Expands the drive and directories that lead to the path, and selects it */
void showPath (Tree tree, String path) {
	if (path == null || path.length () == 0) return;
	File file = new File (path).getAbsoluteFile ();
	java.util.Vector chain = new java.util.Vector ();
	for (File f = file; f != null; f = f.getParentFile ()) chain.insertElementAt (f, 0);
	TreeItem [] level = tree.getItems ();
	TreeItem found = null;
	for (int i=0; i<chain.size (); i++) {
		File wanted = (File) chain.elementAt (i);
		TreeItem next = null;
		for (int j=0; j<level.length; j++) {
			File candidate = (File) level [j].getData ();
			if (candidate != null && candidate.getPath ().equalsIgnoreCase (wanted.getPath ())) {
				next = level [j];
				break;
			}
		}
		if (next == null) break;
		found = next;
		if (i < chain.size () - 1) {
			populate (next);
			next.setExpanded (true);
			level = next.getItems ();
		}
	}
	if (found != null) {
		tree.setSelection (new TreeItem [] {found});
		tree.showItem (found);
	}
}

}
