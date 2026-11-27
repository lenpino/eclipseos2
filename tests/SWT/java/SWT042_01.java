/*
 * Copyright (c) 2026 EclipseOS2 Team.
 * This file is made available under the terms of the Common Public License v1.0
 * which accompanies this distribution.
 */

import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.program.Program;

/** Exercises the SWT API used by the Eclipse editor registry. */
public class SWT042_01 extends SWTTestCase {
    static {
        STEP = "042";
        TEST = "01";
        DESC = "Workplace Shell program associations";
    }

    void initComponents() {
        check(Program.findProgram("") == null, "Empty extension has no association");
        check(Program.findProgram(".swt_unregistered_probe") == null,
            "An unregistered extension permits the workbench's icon fallback");

        String[] extensions = Program.getExtensions();
        Program.getPrograms();
        for (int i = 0; i < extensions.length; i++) {
            Program program = Program.findProgram(extensions[i]);
            check(program != null && program.equals(Program.findProgram(extensions[i].substring(1))),
                "The dotted and undotted extension resolve to the same program");
            ImageData icon = program.getImageData();
            check(icon == null || (icon.width > 0 && icon.height > 0),
                "An available program icon has valid dimensions");
        }
        say("Checked " + extensions.length + " file-backed associations.");
        display.asyncExec(new Runnable() {
            public void run() { shell.dispose(); }
        });
    }

    void check(boolean result, String what) {
        if (!result) throw new IllegalStateException(what);
    }

    public static void main(String[] args) {
        go(new SWT042_01());
    }
}
