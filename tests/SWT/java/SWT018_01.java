/*
 *  SWT018_01.java
 */

/*
 * Copyright (c) 2002, 2026 EclipseOS2 Team.
 * This file is made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 */

import java.lang.reflect.Field;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.pm.OS;
import org.eclipse.swt.internal.pm.PSZ;
import org.eclipse.swt.internal.pm.SWP;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

/**
 *  Validates the tool tips of the controls and of the tool items.
 *
 *  The first part is automatic: the window is shown, the pointer is put
 *  over the controls (WinSetPointerPos) and WM_MOUSEMOVE is sent to them;
 *  the window of the tool tip is read after each wait. The second part is
 *  interactive: rest the pointer on the controls and on the tool items.
 *
 *  To close the window automatically (for unattended runs) pass the system
 *  property swt.test.autoclose=<milliseconds> to the JVM.
 */
public class SWT018_01 extends SWTTestCase {

	static {
		STEP = "018";
		TEST = "01";
		DESC = "Tool tips";
	}

	int checks;
	Canvas a, b, c;
	ToolBar bar;
	ToolItem tool;

	Shell createTopShell(Display display) {
		return new Shell(display, SWT.SHELL_TRIM | SWT.NO_REDRAW_RESIZE);
	}

	/* Where the test put the pointer last (PM coordinates) */
	int[] placed;

	void check(boolean ok, String what) {
		checks++;
		say((ok ? "  ok    " : "  FAIL  ") + what);
		if (!ok) {
			/*
			 * The checks put the pointer themselves. In a VM with the integration of the
			 * mouse, the mouse of the host moving over the window of the VM puts it back
			 * where it is, and then another control has it.
			 */
			int[] now = new int[2];
			OS.WinQueryPointerPos(OS.HWND_DESKTOP, now);
			if (placed != null && (now[0] != placed[0] || now[1] != placed[1])) {
				say("        The pointer is not where the test put it (" + now[0] + "," + now[1] + " instead of " + placed[0] + "," + placed[1]
					+ "): was the mouse moved? Keep it out of the window of the VM during the automatic checks.");
			}
			throw new RuntimeException("Check failed: " + what);
		}
	}

	void run(int milliseconds) {
		OS.WinPostMsg(shell.handle, OS.WM_NULL, 0, 0);
		long end = System.currentTimeMillis() + milliseconds;
		do {
			while (display.readAndDispatch()) {}
			try { Thread.sleep(10); } catch (InterruptedException e) {}
		} while (System.currentTimeMillis() < end);
	}

	static int screenHeight() {
		return OS.WinQuerySysValue(OS.HWND_DESKTOP, OS.SV_CYSCREEN);
	}

	/** Puts the pointer at a point of the screen (SWT coordinates) and sends WM_MOUSEMOVE to the window there. */
	void pointAt(Point p) {
		int[] pt = new int[] {p.x, screenHeight() - 1 - p.y};
		placed = new int[] {pt[0], pt[1]};
		OS.WinSetPointerPos(OS.HWND_DESKTOP, pt[0], pt[1]);
		int hwnd = OS.WinWindowFromPoint(OS.HWND_DESKTOP, pt, true);
		OS.WinMapWindowPoints(OS.HWND_DESKTOP, hwnd, pt, 1);
		OS.WinSendMsg(hwnd, OS.WM_MOUSEMOVE, (pt[0] & 0xFFFF) | (pt[1] << 16), 0);
	}

	void moveTo(Control control, int x, int y) {
		pointAt(control.toDisplay(new Point(x, y)));
	}

	/** The window of the tool tip of the display (a field of its own, read by reflection). */
	int tip() {
		try {
			Field field = Display.class.getDeclaredField("hwndToolTip");
			field.setAccessible(true);
			return field.getInt(display);
		} catch (Exception e) {
			throw new RuntimeException(e.toString());
		}
	}

	boolean shown() {
		return tip() != 0 && OS.WinIsWindowVisible(tip());
	}

	/** The lines of the tool tip, from the top. */
	Vector lines() {
		Vector result = new Vector();
		int inside = OS.WinQueryWindow(tip(), OS.QW_TOP);
		Vector windows = new Vector();
		for (int hwnd = OS.WinQueryWindow(inside, OS.QW_TOP); hwnd != 0; hwnd = OS.WinQueryWindow(hwnd, OS.QW_NEXT)) {
			windows.addElement(new Integer(hwnd));
		}
		/* The last line created is on top of the others: the order of z is the reverse one */
		for (int i = windows.size() - 1; i >= 0; i--) {
			PSZ buffer = new PSZ(256);
			OS.WinQueryWindowText(((Integer) windows.elementAt(i)).intValue(), 256, buffer);
			result.addElement(buffer.toString());
		}
		return result;
	}

	String text() {
		Vector lines = lines();
		String s = "";
		for (int i = 0; i < lines.size(); i++) s += (i > 0 ? "|" : "") + lines.elementAt(i);
		return s;
	}

	void checkApi() {
		saySubObjective("The API");
		check(a.getToolTipText() == null, "no tool tip by default");
		a.setToolTipText("Hello");
		check("Hello".equals(a.getToolTipText()), "setToolTipText");
		tool.setToolTipText("Tool");
		check("Tool".equals(tool.getToolTipText()), "and of a tool item");
		RGB info = display.getSystemColor(SWT.COLOR_INFO_BACKGROUND).getRGB();
		check(info.equals(new RGB(255, 255, 225)), "COLOR_INFO_BACKGROUND is the color of the tool tips, not the one of the desktop: " + info);
	}

	void checkShow() {
		saySubObjective("Showing and hiding");
		int shells = display.getShells().length;
		moveTo(b, 5, 5);
		run(200);
		moveTo(a, 20, 10);
		run(250);
		check(!shown(), "not before 500 ms at rest");
		run(500);
		check(shown() && text().equals("Hello"), "then the text of the control: " + text());
		SWP swp = new SWP();
		OS.WinQueryWindowPos(tip(), swp);
		Point p = a.toDisplay(new Point(20, 10));
		int pointerY = screenHeight() - 1 - p.y;
		check(swp.y + swp.cy <= pointerY && swp.x <= p.x + 1 && swp.cx > 10, "below the pointer: " + swp.x + "," + swp.y + " " + swp.cx + "x" + swp.cy + ", pointer " + p.x + "," + pointerY);
		check(display.getShells().length == shells, "it is not a shell");
		moveTo(a, 21, 10);
		run(300);
		check(shown(), "moving inside the control keeps it");

		OS.WinPostMsg(a.handle, OS.WM_BUTTON1DOWN, (21 & 0xFFFF) | ((a.getSize().y - 11) << 16), 0);
		OS.WinPostMsg(a.handle, OS.WM_BUTTON1UP, (21 & 0xFFFF) | ((a.getSize().y - 11) << 16), 0);
		run(150);
		check(!shown(), "a click takes it away");
		run(800);
		check(!shown(), "until the pointer leaves the control");

		moveTo(b, 5, 5);
		run(800);
		check(!shown(), "a control without a tool tip shows none");
		moveTo(c, 5, 5);
		run(800);
		String s = text();
		check(shown() && s.equals("Two|lines"), "a text with a new line gives two lines: " + s);
		OS.WinQueryWindowPos(tip(), swp);
		int twoLines = swp.cy;
		moveTo(b, 6, 6);
		run(200);
		check(!shown(), "leaving the control takes it away");
		moveTo(a, 20, 10);
		run(800);
		OS.WinQueryWindowPos(tip(), swp);
		check(shown() && twoLines > swp.cy, "one line is lower than two: " + swp.cy + " " + twoLines);
	}

	void checkChanges() {
		saySubObjective("Changes while it is shown");
		a.setToolTipText("Changed");
		run(400);
		check(shown() && text().equals("Changed"), "a new text replaces it: " + text() + " shown " + shown() + " tip " + Integer.toHexString(tip()));
		a.setToolTipText(null);
		run(800);
		check(!shown(), "no text takes it away");
		a.setToolTipText("Hello");
		run(800);
		check(shown(), "and a text brings it back while the pointer rests");
		Canvas gone = new Canvas(shell, SWT.NONE);
		gone.setBounds(10, 200, 50, 30);
		gone.setToolTipText("Gone");
		moveTo(gone, 5, 5);
		run(800);
		check(shown() && text().equals("Gone"), "on another control");
		gone.dispose();
		run(300);
		check(!shown(), "disposing the control takes it away");
	}

	void checkTime() {
		saySubObjective("It goes away after 5 seconds");
		moveTo(b, 5, 5);
		run(200);
		moveTo(a, 20, 10);
		run(800);
		check(shown(), "shown");
		run(5000);
		check(!shown(), "and gone after 5 seconds at rest");
	}

	void checkToolItem() {
		saySubObjective("The tool tip of a tool item");
		Rectangle r = tool.getBounds();
		pointAt(bar.toDisplay(new Point(r.x + r.width / 2, r.y + r.height / 2)));
		run(800);
		check(shown() && text().equals("Tool"), "over the item, its text: " + text());
		moveTo(b, 5, 5);
		run(200);
	}

	/**
	 * Where the tabs of a folder begin, found on the screen: the light top
	 * edge of each tab starts after the shadow of the one to its left.
	 */
	int[] tabStarts(TabFolder folder) {
		Vector starts = new Vector();
		int hps = OS.WinGetScreenPS(OS.HWND_DESKTOP);
		OS.GpiCreateLogColorTable(hps, 0, OS.LCOLF_RGB, 0, 0, null);
		boolean light = false;
		int width = folder.getSize().x;
		for (int x = 0; x < width; x++) {
			Point p = folder.toDisplay(new Point(x, 1));
			int pel = OS.GpiQueryPel(hps, new int[] {p.x, screenHeight() - 1 - p.y});
			boolean now = ((pel >> 16) & 0xFF) > 240;
			if (now && !light) starts.addElement(new Integer(x));
			light = now;
		}
		OS.WinReleasePS(hps);
		int[] result = new int[starts.size()];
		for (int i = 0; i < result.length; i++) result[i] = ((Integer) starts.elementAt(i)).intValue();
		return result;
	}

	void checkTabItems() {
		saySubObjective("The tool tips of the tabs of a TabFolder");
		TabFolder folder = new TabFolder(shell, SWT.NONE);
		/* Sized before it has items: getTabIndex took the empty places of the pages for page 0 (NullPointerException) */
		folder.setBounds(10, 140, 420, 150);
		String[] names = {"One", "Second tab", "3"};
		TabItem[] items = new TabItem[names.length];
		for (int i = 0; i < names.length; i++) {
			items[i] = new TabItem(folder, SWT.NONE);
			items[i].setText(names[i]);
		}
		items[0].setToolTipText("First tab");
		items[1].setToolTipText("Second\nline");
		check("First tab".equals(items[0].getToolTipText()), "getToolTipText of a tab item");
		folder.setToolTipText("The folder");
		run(300);
		int[] starts = tabStarts(folder);
		check(starts.length == 3, "the three tabs are found on the screen: " + starts.length);
		int y = 8;
		moveTo(b, 5, 5);
		run(200);
		moveTo(folder, (starts[0] + starts[1]) / 2, y);
		run(800);
		check(shown() && text().equals("First tab"), "over the first tab, its text: " + text());
		moveTo(folder, (starts[1] + starts[2]) / 2, y);
		run(200);
		check(!shown(), "over another tab the tool tip goes away");
		run(600);
		check(shown() && text().equals("Second|line"), "and the one of that tab comes: " + text());
		moveTo(folder, starts[1] + 3, y);
		run(800);
		check(shown() && text().equals("Second|line"), "near the left edge of the tab: " + text());
		moveTo(folder, starts[2] - 7, y);
		run(800);
		check(shown() && text().equals("Second|line"), "near its right edge: " + text());
		moveTo(folder, starts[2] + 6, y);
		run(800);
		check(!shown(), "a tab without a tool tip shows none, not the one of the folder");
		moveTo(folder, 200, 80);
		run(800);
		check(shown() && text().equals("The folder"), "on the page, the tool tip of the folder: " + text());
		moveTo(folder, (starts[0] + starts[1]) / 2, y);
		run(800);
		items[0].setToolTipText("Changed");
		run(400);
		check(shown() && text().equals("Changed"), "a new text of the tab replaces it: " + text());
		items[0].setToolTipText(null);
		run(800);
		check(!shown(), "no text takes it away");
		/* The tabs grow with the font: the measure of the port follows it */
		moveTo(b, 5, 5);
		run(200);
		org.eclipse.swt.graphics.Font big = new org.eclipse.swt.graphics.Font(display, "Helv", 14, SWT.NORMAL);
		folder.setFont(big);
		for (int i = 0; i < names.length; i++) items[i].setText(names[i]);
		run(300);
		int[] bigStarts = tabStarts(folder);
		check(bigStarts.length == 3 && bigStarts[1] - bigStarts[0] > starts[1] - starts[0], "with a bigger font the tabs are wider: " + bigStarts.length);
		moveTo(folder, bigStarts[1] + 3, y);
		run(800);
		check(shown() && text().equals("Second|line"), "near the left edge of a tab, with Helv 14: " + text());
		moveTo(folder, bigStarts[2] - 7, y);
		run(800);
		check(shown() && text().equals("Second|line"), "near its right edge: " + text());
		moveTo(folder, bigStarts[2] + 6, y);
		run(800);
		check(!shown(), "and the next tab, without a tool tip");
		moveTo(b, 5, 5);
		run(200);
		folder.dispose();
		big.dispose();
	}

	void initComponents() {
		automatic();
		interactive();
	}

	void automatic() {
		shell.setSize(460, 330);
		sayObjective("Automatic checks");
		a = new Canvas(shell, SWT.BORDER);
		a.setBounds(10, 10, 100, 60);
		b = new Canvas(shell, SWT.BORDER);
		b.setBounds(120, 10, 100, 60);
		c = new Canvas(shell, SWT.BORDER);
		c.setBounds(230, 10, 100, 60);
		c.setToolTipText("Two\nlines");
		bar = new ToolBar(shell, SWT.FLAT);
		bar.setBounds(10, 100, 200, 30);
		tool = new ToolItem(bar, SWT.PUSH);
		tool.setText("Item");
		checkApi();
		/* The window is shown and the pointer goes where the messages say (see SWT016_01) */
		say("The pointer is moved by the test: do not touch the mouse until the interactive part.");
		shell.setLocation(40, 40);
		shell.open();
		run(300);
		checkShow();
		checkChanges();
		checkTime();
		checkToolItem();
		checkTabItems();
		shell.setVisible(false);
		run(100);
		a.dispose();
		b.dispose();
		c.dispose();
		bar.dispose();
		say("\n" + checks + " checks passed.\n");
	}

	void interactive() {
		sayObjective("Interactive: rest the pointer on the controls, on the tool items and on the tabs (None has no tool tip)");
		Button button = new Button(shell, SWT.PUSH);
		button.setText("A button");
		button.setBounds(10, 10, 120, 28);
		button.setToolTipText("The tool tip of a button");
		Text text = new Text(shell, SWT.SINGLE | SWT.BORDER);
		text.setBounds(140, 10, 150, 28);
		text.setToolTipText("A text field\nwith a tool tip of two lines");
		Label label = new Label(shell, SWT.BORDER);
		label.setText("A label");
		label.setBounds(300, 10, 140, 28);
		label.setToolTipText("The tool tip of a label");
		Canvas box = new Canvas(shell, SWT.BORDER);
		box.setBackground(display.getSystemColor(SWT.COLOR_DARK_BLUE));
		box.setBounds(10, 50, 120, 60);
		box.setToolTipText("A canvas near the left edge of the window");
		ToolBar tools = new ToolBar(shell, SWT.FLAT);
		tools.setBounds(140, 50, 300, 30);
		String[] names = {"New", "Open", "Save", "Print"};
		for (int i = 0; i < names.length; i++) {
			ToolItem item = new ToolItem(tools, SWT.PUSH);
			item.setText(names[i]);
			item.setToolTipText(names[i] + " (the tool tip of a tool item)");
		}
		Label none = new Label(shell, SWT.NONE);
		none.setText("Below: a control without a tool tip");
		none.setBounds(10, 130, 300, 20);
		Canvas plain = new Canvas(shell, SWT.BORDER);
		plain.setBounds(10, 155, 120, 40);
		TabFolder folder = new TabFolder(shell, SWT.NONE);
		folder.setBounds(140, 155, 300, 130);
		folder.setToolTipText("The folder (on its page)");
		String[] tabs = {"General", "Advanced options", "Help", "None"};
		for (int i = 0; i < tabs.length; i++) {
			TabItem item = new TabItem(folder, SWT.NONE);
			item.setText(tabs[i]);
			if (i < 3) item.setToolTipText("The tab " + tabs[i]);
		}
	}

	public static void main(String[] args) {
		go(new SWT018_01());
	}

}
