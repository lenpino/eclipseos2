/*
 *  SWT009_13.java
 */

/*
 * Copyright (c) 2002, 2010 EclipseOS2 Team.
 * This file is made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 */

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.pm.CLASSINFO;
import org.eclipse.swt.internal.pm.OS;
import org.eclipse.swt.internal.pm.PSZ;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 *  Validates the Sash widget, which is backed by the eCS toolkit splitter
 *  class (esplitter.h, WC_ER_SPLITTER) registered through TBInitialize().
 *
 *  The first part is automatic: any failed check throws an exception, so
 *  the testcase is reported as FAILED. The second part is interactive:
 *  drag the vertical sash and verify that both Text controls resize.
 *
 *  To close the window automatically (for unattended runs) pass the system
 *  property swt.test.autoclose=<milliseconds> to the JVM.
 */
public class SWT009_13 extends SWTTestCase {

	static {
		STEP = "013";
		TEST = "09";
		DESC = "Sash (esplitter)";
	}

	Text left, right, bottom;
	Sash vsash, hsash;
	int checks;
	double ratio = 0.5;	/* left pane share of the client width */
	double ratioH = 0.5;	/* top-right pane share of the client height */

	Shell createTopShell(Display display) {
		return new Shell(display, SWT.SHELL_TRIM | SWT.NO_REDRAW_RESIZE);
	}

	void check(boolean ok, String what) {
		checks++;
		say((ok ? "  ok    " : "  FAIL  ") + what);
		if (!ok) throw new RuntimeException("Check failed: " + what);
	}

	void checkClassRegistration() {
		saySubObjective("Splitter window class is registered");
		/* TBInitialize() must be idempotent: Sash already called it. */
		check(OS.TBInitialize(), "TBInitialize() returns true (second call)");

		CLASSINFO info = new CLASSINFO();
		boolean found = OS.WinQueryClassInfo(OS.NULLHANDLE,
				new PSZ(OS.WC_ER_SPLITTER), info);
		check(found, "WinQueryClassInfo finds \"" + OS.WC_ER_SPLITTER + "\"");
		check(info.pfnWindowProc != 0, "splitter window procedure is not null");
	}

	void checkSash(Sash sash, int orientation, String name) {
		saySubObjective(name + " Sash");
		check(sash.handle != 0, "native window created (handle != 0)");
		check((sash.getStyle() & orientation) != 0, "style has the requested orientation");

		PSZ buf = new PSZ(64);
		int len = OS.WinQueryClassName(sash.handle, 64, buf);
		String cls = String.valueOf(buf);
		int nul = cls.indexOf('\0');
		if (nul >= 0) cls = cls.substring(0, nul);
		check(len > 0 && cls.equals(OS.WC_ER_SPLITTER),
				"window class is \"" + OS.WC_ER_SPLITTER + "\" (got \"" + cls + "\")");

		Point size = sash.computeSize(SWT.DEFAULT, SWT.DEFAULT, false);
		check(size.x > 0 && size.y > 0, "computeSize is positive: " + size);
		if (orientation == SWT.HORIZONTAL)
			check(size.x > size.y, "horizontal sash is wider than tall");
		else
			check(size.y > size.x, "vertical sash is taller than wide");

		sash.setBounds(10, 20, 120, 40);
		Rectangle r = sash.getBounds();
		check(r.x == 10 && r.y == 20 && r.width == 120 && r.height == 40,
				"setBounds/getBounds round trip: " + r);
	}

	void checkSashes() {
		Shell probe = new Shell(display, SWT.SHELL_TRIM);
		probe.setSize(300, 200);
		try {
			checkSash(new Sash(probe, SWT.HORIZONTAL), SWT.HORIZONTAL, "Horizontal");
			checkSash(new Sash(probe, SWT.VERTICAL), SWT.VERTICAL, "Vertical");

			saySubObjective("Dispose");
			Sash s = new Sash(probe, SWT.VERTICAL);
			s.dispose();
			check(s.isDisposed(), "sash reports disposed");
		} finally {
			probe.dispose();
		}
	}

	void checkSelectionEvent() {
		saySubObjective("Splitter position message (informational)");
		Shell probe = new Shell(display, SWT.SHELL_TRIM);
		probe.setSize(300, 200);
		try {
			final int[] hits = new int[1];
			final Rectangle[] got = new Rectangle[1];
			Sash s = new Sash(probe, SWT.VERTICAL);
			s.setBounds(100, 0, 4, 150);
			s.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					hits[0]++;
					got[0] = new Rectangle(e.x, e.y, e.width, e.height);
				}
			});
			probe.open();
			say("  info  pos before: " + OS.WinSendMsg(s.handle, OS.SBM_GETSPLITTERPOS, 0, 0));
			OS.WinSendMsg(s.handle, OS.SBM_SETSPLITTERPOS, 140, 0);
			while (display.readAndDispatch()) {}
			say("  info  pos after (no windows): " + OS.WinSendMsg(s.handle, OS.SBM_GETSPLITTERPOS, 0, 0)
					+ ", events: " + hits[0]);

			say("  info  Selection events received: " + hits[0]
					+ (got[0] != null ? ", last " + got[0] : ""));
			/* The splitter notifies only on a user drag, not on SBM_SETSPLITTERPOS:
			 * the Selection path is validated by the interactive part below. */
		} finally {
			probe.dispose();
		}
	}

	void initComponents() {
		sayObjective("Automatic checks");
		checkClassRegistration();
		checkSashes();
		checkSelectionEvent();
		say("\n" + checks + " checks passed.\n");

		sayObjective("Interactive: drag both sashes, the neighbour texts must resize");
		shell.setLayout(null);
		left = new Text(shell, SWT.BORDER | SWT.MULTI);
		left.setText("left");
		right = new Text(shell, SWT.BORDER | SWT.MULTI);
		right.setText("top right");
		bottom = new Text(shell, SWT.BORDER | SWT.MULTI);
		bottom.setText("bottom right");
		vsash = new Sash(shell, SWT.VERTICAL);
		hsash = new Sash(shell, SWT.HORIZONTAL);
		vsash.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				say("Selection: x=" + e.x + " y=" + e.y
						+ " w=" + e.width + " h=" + e.height
						+ (e.detail == SWT.DRAG ? " (drag)" : ""));
				if (e.detail == SWT.DRAG) return;
				Rectangle area = shell.getClientArea();
				int x = Math.max(20, Math.min(e.x, area.width - 20));
				ratio = (double) x / area.width;
				layoutPanes();
			}
		});
		hsash.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				say("Selection (horizontal): x=" + e.x + " y=" + e.y
						+ " w=" + e.width + " h=" + e.height
						+ (e.detail == SWT.DRAG ? " (drag)" : ""));
				if (e.detail == SWT.DRAG) return;
				Rectangle area = shell.getClientArea();
				int y = Math.max(20, Math.min(e.y, area.height - 20));
				ratioH = (double) y / area.height;
				layoutPanes();
			}
		});
		shell.addListener(SWT.Resize, new org.eclipse.swt.widgets.Listener() {
			public void handleEvent(org.eclipse.swt.widgets.Event e) {
				layoutPanes();
			}
		});
		shell.setSize(450, 300);
		layoutPanes();
		setTitle(null);

		final String auto = System.getProperty("swt.test.autoclose");
		if (auto != null) {
			display.timerExec(Integer.parseInt(auto), new Runnable() {
				public void run() { shell.dispose(); }
			});
		}
	}

	void layoutPanes() {
		Rectangle area = shell.getClientArea();
		int x = (int) (area.width * ratio);
		vsash.setBounds(x, 0, 4, area.height);
		left.setBounds(0, 0, x, area.height);
		int x2 = x + 4, w = area.width - x2;
		int y = (int) (area.height * ratioH);
		hsash.setBounds(x2, y, w, 4);
		right.setBounds(x2, 0, w, y);
		bottom.setBounds(x2, y + 4, w, area.height - y - 4);
	}

	void setTitle(String title) {
		super.setTitle(title);
	}

	public static void main(String[] args) {
		go(new SWT009_13());
	}

}
