/*
 *  SWT009_10.java
 */

/*
 * Copyright (c) 2002, 2010 EclipseOS2 Team.
 * This file is made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 */

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.graphics.*;

public class SWT009_10 extends SWTTestCase {

	static {
		STEP = "009";
		TEST = "10";
		DESC = "ProgressBar";
	}

	Shell createTopShell(Display display) {
		return new Shell(display, SWT.SHELL_TRIM | SWT.NO_REDRAW_RESIZE);
	}


	void initComponents() {
	    final ProgressBar bar =new ProgressBar(shell,SWT.HORIZONTAL);
	    bar.setBounds(0, 0, 200, 20);
	    shell.pack();
	    shell.open();
	    final int maximum = bar.getMaximum();
		new Thread() {
		public void run() {
		    for (int i = 1; i <= maximum; i++) {
			try {
			    Thread.sleep(100);
			} catch (Throwable th) {
			}
			final int index = i;
			display.asyncExec(new Runnable() {
			    public void run() {
				bar.setSelection(index);
			    }
			});
		    }
		}
		}.start();
	}
	void setTitle(String title) {
		super.setTitle(title);
	}

	public static void main(String[] args) {
		go(new SWT009_10());
	}

}