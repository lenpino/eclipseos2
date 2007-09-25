/*
 *  SWT008_02.java
 */

/*
 * Copyright (c) 2002, 2005 EclipseOS2 Team.
 * This file is made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 */

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;

/**
 * This example demonstrates the FileDialog.
 * Valid combinations are:
 * <ul>
 * <li>SAVE (with or without MULTI)</li>
 * <li>OPEN (with or without MULTI)</li>
 * </ul>
 */

public class SWT008_03 extends SWTTestCase {

static {
    STEP = "008";
    TEST = "03";
    DESC = "File Dialog";
}

static final String SAVE = "Save Dialog";
static final String OPEN = "Open Dialog";
static final String MULTI = "Select Multiple";

private Button show, exit;
// no sense in having a container holding the buttons as there are only three buttons.
private Button save, open, multi;

final static RowLayout dialogTypeLayout = new RowLayout (SWT.VERTICAL);
static {
    dialogTypeLayout.marginLeft = dialogTypeLayout.marginRight =
    dialogTypeLayout.marginTop = dialogTypeLayout.marginBottom = 12;
    dialogTypeLayout.spacing = 4;
}
final static RowLayout multiSelLayout = new RowLayout (SWT.VERTICAL);
static {
    multiSelLayout.marginLeft = multiSelLayout.marginRight =
    multiSelLayout.marginTop = multiSelLayout.marginBottom = 12;
    multiSelLayout.spacing = 4;
}

class MySelectionListener implements SelectionListener {
    public void widgetDefaultSelected (SelectionEvent e) {
        System.out.println("DefaultSelected: " + e.widget + ", (" + e.item +"), [" +
            e.x + "," + e.y + ";" + e.width + "," + e.height + "], " +
            "selected=" + ((Button)e.widget).getSelection());
    }
    public void widgetSelected (SelectionEvent e) {
        System.out.println("Selected: " + e.widget + ", (" + e.item +"), [" +
            e.x + "," + e.y + ";" + e.width + "," + e.height + "], " +
            "selected=" + ((Button)e.widget).getSelection());
    }
}

public static void main (String[] args) {
    go (new SWT008_03());
}

Shell createTopShell (Display display) {
    return new Shell(display, SWT.SHELL_TRIM | SWT.NO_REDRAW_RESIZE);
}

void createChoices (Composite dialogTypeComp, Composite multiSelComp)
{
    save = new Button (dialogTypeComp, SWT.RADIO);
    save.setText (SAVE);
    save.addSelectionListener (new MySelectionListener());
    open = new Button (dialogTypeComp, SWT.RADIO);
    open.setText (OPEN);
    open.addSelectionListener (new MySelectionListener());
    multi = new Button (multiSelComp, SWT.CHECK);
    multi.setText (MULTI);
    multi.addSelectionListener (new MySelectionListener());
}

void initComponents() {
    shell.setLayout (new FormLayout());
    setTitle (DESC + " Test");

    FormData formData = new FormData();

    Composite dialogTypeComp = new Composite (shell, SWT.NONE);
    dialogTypeComp.setLayout (dialogTypeLayout);
    formData.top = new FormAttachment (0, 5);
    formData.left = new FormAttachment (0, 5);
    formData.bottom = new FormAttachment (75, -5);
    formData.right = new FormAttachment (33, -5);
    dialogTypeComp.setLayoutData (formData);

    Composite multiSelComp = new Composite (shell, SWT.NONE);
    multiSelComp.setLayout (multiSelLayout);
    formData = new FormData();
    formData.top = new FormAttachment (0, 5);
    formData.left = new FormAttachment (dialogTypeComp, 5);
    formData.bottom = new FormAttachment (75, -5);
    formData.right = new FormAttachment (66, -5);
    multiSelComp.setLayoutData (formData);

    Composite shellControls = new Composite (shell, SWT.NONE);
    shellControls.setLayout (new RowLayout (SWT.HORIZONTAL));
    formData = new FormData();
    formData.top = new FormAttachment (dialogTypeComp, 5);
    formData.left = new FormAttachment (33, 5);
    formData.bottom = new FormAttachment (100, -5);
    formData.right = new FormAttachment (100, -5);
    shellControls.setLayoutData (formData);

    show = new Button (shellControls, SWT.PUSH);
    show.setText ("S&how");
    show.setLayoutData (new RowData (50, 25));
    show.addSelectionListener (new SelectionAdapter() {
        public void widgetSelected (SelectionEvent e) {
            int dialogTypeBits = SWT.NONE, multiSelBits = SWT.NONE;

            if (save != null && save.getSelection()) {
                dialogTypeBits = SWT.SAVE;
            }

            if (open != null && open.getSelection()) {
                dialogTypeBits = SWT.OPEN;
            }

            if (multi != null && multi.getSelection()) {
                multiSelBits = SWT.MULTI;
            }

            FileDialog fd = new FileDialog (shell, dialogTypeBits | multiSelBits);
            String dialogTitle = "", file = null;
            switch (dialogTypeBits) {
                case SWT.OPEN:
                    if (multiSelBits == SWT.MULTI) dialogTitle = OPEN + " " + MULTI;
                    else dialogTitle = OPEN;
                    break;
                case SWT.SAVE:
                    if (multiSelBits == SWT.MULTI) dialogTitle = SAVE + " " + MULTI;
                    else dialogTitle = SAVE;
                    break;
                default: break;
            }
            fd.setText (dialogTitle);
            file = fd.open();
            String[] stringArray = fd.getFileNames();
            if (stringArray != null && stringArray.length > 0) {
                say ("File selected list: ");
                for (int i = 0; i < stringArray.length; i++) {
                    say ("  " + stringArray[i]);
                }
            } else {
                say ("File selected: " + fd.getFileName());
            }
            stringArray = fd.getFilterExtensions();
            if (stringArray != null && stringArray.length > 0) {
                say ("Filter extension list: ");
                for (int i = 0; i < stringArray.length; i++) {
                    say (" " + stringArray[i]);
                }
            }
            stringArray = fd.getFilterNames();
            if (stringArray != null && stringArray.length > 0) {
                say ("Filter name list: ");
                for (int i = 0; i < stringArray.length; i++) {
                    say (" " + stringArray[i]);
                }
            }

            if (fd.getFilterPath() != null && fd.getFilterPath().length() > 0) {
                say ("Filter path " + fd.getFilterPath());
            }

            fd = null;
            MessageBox mb = new MessageBox (shell, SWT.OK);
            mb.setText (DESC + " test");
            mb.setMessage ("file/filter returned " + file);
            mb.open();
            mb = null;
/*
            System.out.println(strButton + ", " + strIcon + ", " + strModal + " - " + getReturnString(mbrc) + ".");
*/
        }
    });
    exit = new Button (shellControls, SWT.PUSH);
    exit.setText ("E&xit");
    exit.setLayoutData (new RowData (50, 25));
    exit.addSelectionListener (new SelectionAdapter() {
        public void widgetSelected (SelectionEvent e) {
            shell.getDisplay().dispose();
        }
    });

    createChoices (dialogTypeComp, multiSelComp);

    shell.pack();
}

private int getFlag (String buttonText)
{
    if (buttonText.equals (SAVE))
        return SWT.SAVE;
    if (buttonText.equals (OPEN))
        return SWT.OPEN;
    if (buttonText.equals (MULTI))
        return SWT.MULTI;

    return SWT.NONE;
}

}
