/*
 *  SWT023_01.java
 */

/*
 * Copyright (c) 2002, 2026 EclipseOS2 Team.
 * This file is made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 */

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.pm.OS;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 *  Validates the custom widgets of Eclipse 2.0.1, which are made of the other
 *  widgets in pure Java: StyledText, CTabFolder, SashForm, CCombo.
 *
 *  The first part is automatic: the window is shown, keys and clicks are sent
 *  to the widgets, and the pixels of the screen are read. The second part is
 *  interactive: an editor (StyledText) in the tabs of a CTabFolder.
 *
 *  To close the window automatically (for unattended runs) pass the system
 *  property swt.test.autoclose=<milliseconds> to the JVM.
 */
public class SWT023_01 extends SWTTestCase {

	static {
		STEP = "023";
		TEST = "01";
		DESC = "Custom widgets";
	}

	int checks;

	Shell createTopShell(Display display) {
		return new Shell(display, SWT.SHELL_TRIM);
	}

	void check(boolean ok, String what) {
		checks++;
		say((ok ? "  ok    " : "  FAIL  ") + what);
		if (!ok) throw new RuntimeException("Check failed: " + what);
	}

	void pump(Control control) {
		OS.WinPostMsg(control.handle, OS.WM_NULL, 0, 0);
		for (int i = 0; i < 30; i++) while (display.readAndDispatch()) {}
		control.update();
		for (int i = 0; i < 30; i++) while (display.readAndDispatch()) {}
	}

	/* A character typed: WM_CHAR with KC_CHAR, posted as the system does */
	void type(Control control, char c) {
		OS.WinPostMsg(control.handle, OS.WM_CHAR, OS.KC_CHAR | (1 << 16), c);
		pump(control);
	}

	/* A virtual key (an arrow), posted */
	void key(Control control, int virtualKey) {
		OS.WinPostMsg(control.handle, OS.WM_CHAR, OS.KC_VIRTUALKEY | (1 << 16), virtualKey << 16);
		pump(control);
	}

	/* A virtual key that is also a character (backspace is 8), as PM sends it */
	void key(Control control, int virtualKey, char c) {
		OS.WinPostMsg(control.handle, OS.WM_CHAR, OS.KC_VIRTUALKEY | OS.KC_CHAR | (1 << 16), c | (virtualKey << 16));
		pump(control);
	}

	/* A click of the first button at a point of a control without a border */
	void click(Control control, int x, int y) {
		int height = control.getSize().y;
		int mp1 = (x & 0xFFFF) | ((height - 1 - y) << 16);
		OS.WinSendMsg(control.handle, OS.WM_BUTTON1DOWN, mp1, 0);
		OS.WinSendMsg(control.handle, OS.WM_BUTTON1UP, mp1, 0);
		pump(control);
	}

	int pel(Control control, int x, int y) {
		Point p = control.toDisplay(new Point(x, y));
		int hps = OS.WinGetScreenPS(OS.HWND_DESKTOP);
		OS.GpiCreateLogColorTable(hps, 0, OS.LCOLF_RGB, 0, 0, null);
		int pel = OS.GpiQueryPel(hps, new int[] {p.x, OS.WinQuerySysValue(OS.HWND_DESKTOP, OS.SV_CYSCREEN) - 1 - p.y}) & 0xFFFFFF;
		OS.WinReleasePS(hps);
		return pel;
	}

	boolean dark(int pel) {
		return ((pel >> 16) & 0xFF) < 80 && ((pel >> 8) & 0xFF) < 80 && (pel & 0xFF) < 80;
	}

	boolean red(int pel) {
		return ((pel >> 16) & 0xFF) > 180 && ((pel >> 8) & 0xFF) < 80 && (pel & 0xFF) < 80;
	}

	/* How many pixels of a rectangle of a control are dark, and how many red */
	int[] count(Control control, Rectangle r) {
		int[] result = new int[2];
		for (int y = r.y; y < r.y + r.height; y++) {
			for (int x = r.x; x < r.x + r.width; x++) {
				int pel = pel(control, x, y);
				if (dark(pel)) result[0]++;
				if (red(pel)) result[1]++;
			}
		}
		return result;
	}

	void checkStyledText(Shell window) {
		saySubObjective("StyledText");
		final StyledText text = new StyledText(window, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
		text.setBounds(0, 0, 360, 160);
		pump(text);
		text.setText("first line\nsecond line\nthird line");
		check(text.getLineCount() == 3 && text.getText().equals("first line\nsecond line\nthird line"), "setText, getText and the lines: " + text.getLineCount());
		check(text.getLineAtOffset(13) == 1 && text.getOffsetAtLine(2) == 23, "offsets and lines");
		text.replaceTextRange(0, 5, "1st");
		check(text.getContent().getLine(0).equals("1st line"), "replaceTextRange: " + text.getContent().getLine(0));
		text.setSelection(4, 8);
		check(text.getSelectionText().equals("line"), "setSelection, getSelectionText: " + text.getSelectionText());
		pump(text);

		/* The text on the screen, where the widget says it is */
		Point second = text.getLocationAtOffset(text.getOffsetAtLine(1));
		int height = text.getLineHeight();
		int[] line = count(text, new Rectangle(second.x, second.y, 60, height));
		int[] below = count(text, new Rectangle(0, 3 * height + 4, 200, height));
		check(line[0] > 20 && below[0] == 0, "the second line is painted where getLocationAtOffset says, nothing under the last one: " + line[0] + ", " + below[0] + " dark pixels");
		Color red = new Color(display, 255, 0, 0);
		text.setStyleRange(new StyleRange(text.getOffsetAtLine(2), 5, red, null));
		pump(text);
		Point third = text.getLocationAtOffset(text.getOffsetAtLine(2));
		int[] styled = count(text, new Rectangle(third.x, third.y, 40, height));
		check(styled[1] > 10, "a StyleRange with a red foreground is red: " + styled[1] + " red pixels");

		/* The clipboard (step 022), and with the text fields of PM */
		Clipboard clipboard = new Clipboard(display);
		text.setSelection(4, 8);
		text.copy();
		check("line".equals(clipboard.getContents(TextTransfer.getInstance())), "copy puts the selection in the clipboard: " + clipboard.getContents(TextTransfer.getInstance()));
		clipboard.setContents(new Object[] {"PASTED "}, new Transfer[] {TextTransfer.getInstance()});
		text.setSelection(0);
		text.paste();
		check(text.getContent().getLine(0).equals("PASTED 1st line"), "paste inserts what the clipboard has: " + text.getContent().getLine(0));
		text.setSelection(0, 7);
		text.cut();
		check(text.getContent().getLine(0).equals("1st line") && "PASTED ".equals(clipboard.getContents(TextTransfer.getInstance())), "cut removes it and puts it in the clipboard");
		Text field = new Text(window, SWT.SINGLE | SWT.BORDER);
		field.setBounds(0, 170, 200, 24);
		field.setText("from PM");
		field.selectAll();
		field.copy();
		text.setSelection(0);
		text.paste();
		check(text.getContent().getLine(0).startsWith("from PM"), "a text copied by a text field of PM is pasted: " + text.getContent().getLine(0));
		field.dispose();
		clipboard.dispose();

		/* The keyboard and the mouse */
		text.setText("abc");
		text.setFocus();
		text.setCaretOffset(3);
		type(text, 'd');
		check(text.getText().equals("abcd") && text.getCaretOffset() == 4, "a typed character is inserted at the caret: " + text.getText());
		key(text, OS.VK_LEFT);
		key(text, OS.VK_LEFT);
		check(text.getCaretOffset() == 2, "the arrow keys move the caret: " + text.getCaretOffset());
		key(text, OS.VK_BACKSPACE, (char) 8);
		check(text.getText().equals("acd"), "backspace deletes: " + text.getText());
		text.setText("0123456789\nsecond");
		pump(text);
		Point at = text.getLocationAtOffset(5);
		click(text, at.x + 1, at.y + text.getLineHeight() / 2);
		check(text.getCaretOffset() == 5, "a click puts the caret there: " + text.getCaretOffset());

		/* Scrolling */
		StringBuffer many = new StringBuffer();
		for (int i = 0; i < 100; i++) many.append("line " + i + "\n");
		text.setText(many.toString());
		text.setTopIndex(50);
		pump(text);
		check(text.getTopIndex() == 50, "setTopIndex: " + text.getTopIndex());
		text.setCaretOffset(text.getOffsetAtLine(99));
		text.showSelection();
		pump(text);
		check(text.getTopIndex() > 80, "the caret on the last line is shown: top " + text.getTopIndex());
		red.dispose();
		text.dispose();
	}

	void checkTabs(Shell window) {
		saySubObjective("CTabFolder");
		CTabFolder folder = new CTabFolder(window, SWT.BORDER);
		folder.setBounds(0, 0, 360, 160);
		final int[] events = new int[1];
		folder.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) { events[0]++; }
		});
		CTabItem[] items = new CTabItem[3];
		for (int i = 0; i < items.length; i++) {
			items[i] = new CTabItem(folder, SWT.NONE);
			items[i].setText("Tab number " + i);
			StyledText page = new StyledText(folder, SWT.MULTI);
			page.setText("Contents of the tab " + i);
			items[i].setControl(page);
		}
		folder.setSelection(0);
		pump(folder);
		check(folder.getItemCount() == 3 && folder.getSelectionIndex() == 0, "three tabs, the first selected");
		check(items[0].getControl().isVisible() && !items[1].getControl().isVisible(), "the control of the selected tab is the one shown");
		Rectangle tab = items[1].getBounds();
		check(tab.width > 40 && tab.height > 10, "a tab has bounds: " + tab);
		int[] drawn = count(folder, tab);
		check(drawn[0] > 20, "the text of a tab is painted: " + drawn[0] + " dark pixels");
		click(folder, tab.x + tab.width / 2, tab.y + tab.height / 2);
		check(folder.getSelectionIndex() == 1 && events[0] == 1, "a click on a tab selects it, with a Selection: " + folder.getSelectionIndex() + ", events " + events[0]);
		check(items[1].getControl().isVisible() && !items[0].getControl().isVisible(), "and shows its control");
		folder.setSelection(2);
		check(folder.getSelectionIndex() == 2 && events[0] == 1, "setSelection sends no event");
		folder.dispose();
	}

	void checkOthers(Shell window) {
		saySubObjective("SashForm and CCombo");
		SashForm sash = new SashForm(window, SWT.HORIZONTAL);
		sash.setBounds(0, 0, 300, 100);
		Button left = new Button(sash, SWT.PUSH);
		Button right = new Button(sash, SWT.PUSH);
		sash.setWeights(new int[] {1, 2});
		sash.layout();
		pump(sash);
		int l = left.getSize().x, r = right.getSize().x;
		check(l > 60 && r > 2 * l - 10 && r < 2 * l + 10, "SashForm divides by the weights: " + l + " and " + r);
		sash.dispose();
		CCombo combo = new CCombo(window, SWT.BORDER);
		combo.setBounds(0, 0, 200, 24);
		combo.setItems(new String[] {"alpha", "beta", "gamma"});
		combo.select(1);
		check(combo.getText().equals("beta") && combo.getSelectionIndex() == 1, "CCombo: items and selection: " + combo.getText());
		combo.setText("other");
		check(combo.getText().equals("other"), "CCombo: setText");
		combo.dispose();
	}

	void initComponents() {
		Shell window = new Shell(display, SWT.NO_TRIM);
		window.setBounds(10, 10, 380, 220);
		window.open();
		sayObjective("Automatic checks");
		checkStyledText(window);
		checkTabs(window);
		checkOthers(window);
		window.dispose();
		say("\n" + checks + " checks passed.\n");

		sayObjective("Interactive: type in the editors, select, copy and paste (Ctrl+C, Ctrl+V, also with other programs), change the tabs");
		shell.setLayout(new FillLayout());
		SashForm sash = new SashForm(shell, SWT.VERTICAL);
		CTabFolder folder = new CTabFolder(sash, SWT.BORDER);
		for (int i = 0; i < 3; i++) {
			CTabItem item = new CTabItem(folder, SWT.NONE);
			item.setText("Editor " + (i + 1));
			StyledText editor = new StyledText(folder, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
			StringBuffer content = new StringBuffer();
			for (int j = 0; j < 40; j++) content.append("Line " + (j + 1) + " of the editor " + (i + 1) + "\n");
			editor.setText(content.toString());
			item.setControl(editor);
		}
		folder.setSelection(0);
		Text field = new Text(sash, SWT.MULTI | SWT.BORDER);
		field.setText("A text field of PM, to copy and paste to and from the editors");
		sash.setWeights(new int[] {4, 1});
		shell.setSize(460, 360);
	}

	void setTitle(String title) {
		super.setTitle(title);
	}

	public static void main(String[] args) {
		go(new SWT023_01());
	}

}
