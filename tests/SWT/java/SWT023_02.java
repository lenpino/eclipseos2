/*
 *  SWT023_02.java
 */

/*
 * Copyright (c) 2002, 2026 EclipseOS2 Team.
 * This file is made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 */

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.custom.TableTree;
import org.eclipse.swt.custom.TableTreeItem;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.pm.OS;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

/**
 *  Validates more custom widgets of Eclipse 2.0.1: ScrolledComposite, ViewForm,
 *  CLabel, TableTree, TableEditor, StackLayout and BusyIndicator.
 *
 *  The first part is automatic: the window is shown, the widgets are laid out
 *  and used, and the pixels of the screen are read. The second part is
 *  interactive: a view (ViewForm) with a title (CLabel) and a scrolled
 *  composite, and a table tree.
 *
 *  To close the window automatically (for unattended runs) pass the system
 *  property swt.test.autoclose=<milliseconds> to the JVM.
 */
public class SWT023_02 extends SWTTestCase {

	static {
		STEP = "023";
		TEST = "02";
		DESC = "More custom widgets";
	}

	int checks;
	Image image;

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

	int pel(Control control, int x, int y) {
		Point p = control.toDisplay(new Point(x, y));
		int hps = OS.WinGetScreenPS(OS.HWND_DESKTOP);
		OS.GpiCreateLogColorTable(hps, 0, OS.LCOLF_RGB, 0, 0, null);
		int pel = OS.GpiQueryPel(hps, new int[] {p.x, OS.WinQuerySysValue(OS.HWND_DESKTOP, OS.SV_CYSCREEN) - 1 - p.y}) & 0xFFFFFF;
		OS.WinReleasePS(hps);
		return pel;
	}

	/* The first and last x of the dark pixels in a control, or -1 */
	int[] darkSpan(Control control) {
		Point size = control.getSize();
		int first = -1, last = -1;
		for (int y = 0; y < size.y; y++) {
			for (int x = 0; x < size.x; x++) {
				int pel = pel(control, x, y);
				if (((pel >> 16) & 0xFF) < 80 && ((pel >> 8) & 0xFF) < 80 && (pel & 0xFF) < 80) {
					if (first == -1 || x < first) first = x;
					if (x > last) last = x;
				}
			}
		}
		return new int[] {first, last};
	}

	void checkScrolledComposite(Shell window) {
		saySubObjective("ScrolledComposite");
		ScrolledComposite scrolled = new ScrolledComposite(window, SWT.H_SCROLL | SWT.V_SCROLL);
		scrolled.setBounds(0, 0, 200, 150);
		Composite content = new Composite(scrolled, SWT.NONE);
		Button far = new Button(content, SWT.PUSH);
		far.setText("Far away");
		far.setBounds(500, 300, 80, 30);
		content.setSize(600, 400);
		scrolled.setContent(content);
		pump(scrolled);
		check(scrolled.getVerticalBar().getVisible() && scrolled.getHorizontalBar().getVisible(), "a content bigger than the composite shows both bars");
		Rectangle client = scrolled.getClientArea();
		check(client.width < 200 && client.height < 150, "and the client area is beside them: " + client);
		check(scrolled.getVerticalBar().getMaximum() == 400 && scrolled.getVerticalBar().getThumb() == client.height, "the vertical bar: the height of the content, the thumb the visible part: "
				+ scrolled.getVerticalBar().getMaximum() + ", " + scrolled.getVerticalBar().getThumb());
		scrolled.setOrigin(100, 50);
		check(scrolled.getOrigin().equals(new Point(100, 50)) && content.getLocation().equals(new Point(-100, -50)), "setOrigin moves the content: " + content.getLocation());
		check(scrolled.getVerticalBar().getSelection() == 50 && scrolled.getHorizontalBar().getSelection() == 100, "and the bars");
		/* A line down on the vertical bar, as PM sends it */
		OS.WinSendMsg(scrolled.handle, OS.WM_VSCROLL, OS.FID_VERTSCROLL, OS.SB_PAGEDOWN << 16);
		pump(scrolled);
		int selection = scrolled.getVerticalBar().getSelection();
		check(selection > 50 && content.getLocation().y == -selection, "the bar scrolls the content: " + selection + ", " + content.getLocation());
		scrolled.setOrigin(400, 250);
		pump(scrolled);
		Point at = far.getLocation();
		Point onScreen = content.toControl(far.toDisplay(new Point(0, 0)));
		check(onScreen.equals(at), "the children move with the content: " + at);
		content.setSize(100, 80);
		scrolled.layout(true);
		pump(scrolled);
		check(!scrolled.getVerticalBar().getVisible() && !scrolled.getHorizontalBar().getVisible(), "a content that fits hides the bars");
		check(scrolled.getClientArea().width == 200 && scrolled.getClientArea().height == 150, "and the client area is all of it: " + scrolled.getClientArea());
		scrolled.setExpandHorizontal(true);
		scrolled.setExpandVertical(true);
		scrolled.setMinSize(300, 100);
		scrolled.layout(true);
		pump(scrolled);
		check(content.getSize().x == 300 && content.getSize().y == scrolled.getClientArea().height, "expanded: the minimum width, the height of the client area: " + content.getSize());
		check(scrolled.getHorizontalBar().getVisible() && !scrolled.getVerticalBar().getVisible(), "only the horizontal bar is needed");
		scrolled.dispose();
	}

	void checkViewForm(Shell window) {
		saySubObjective("ViewForm and CLabel");
		ViewForm form = new ViewForm(window, SWT.BORDER | SWT.FLAT);
		form.setBounds(0, 0, 300, 180);
		CLabel title = new CLabel(form, SWT.NONE);
		title.setText("The title of a view");
		title.setImage(image);
		Text text = new Text(form, SWT.MULTI);
		text.setText("The content of the view");
		form.setTopLeft(title);
		form.setContent(text);
		form.layout();
		pump(form);
		Rectangle top = title.getBounds(), body = text.getBounds();
		check(top.y < body.y && top.y + top.height <= body.y + 1, "the title is above the content: " + top + ", " + body);
		check(body.x >= 0 && body.x + body.width <= 300 && body.y + body.height <= 180, "the content is in the form: " + body);
		check(form.getTopLeft() == title && form.getContent() == text, "getTopLeft, getContent");
		GC gc = new GC(title);
		int textWidth = gc.stringExtent("The title of a view").x;
		gc.dispose();
		Point size = title.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		check(size.x > textWidth + 16 && size.y >= 16, "the size of a CLabel fits its image and its text: " + size + ", text " + textWidth);
		CLabel label = new CLabel(window, SWT.NONE);
		label.setText("Right");
		label.setBounds(0, 190, 200, 24);
		pump(label);
		int[] left = darkSpan(label);
		label.setAlignment(SWT.RIGHT);
		pump(label);
		int[] right = darkSpan(label);
		check(left[0] >= 0 && left[0] < 20 && right[1] > 170, "the text of a CLabel is painted, on the left and then on the right: " + left[0] + ".." + left[1] + " -> " + right[0] + ".." + right[1]);
		label.dispose();
		form.dispose();
	}

	void checkTableTree(Shell window) {
		saySubObjective("TableTree");
		TableTree tree = new TableTree(window, SWT.BORDER | SWT.SINGLE);
		tree.setBounds(0, 0, 300, 180);
		TableTreeItem[] roots = new TableTreeItem[3];
		for (int i = 0; i < roots.length; i++) {
			roots[i] = new TableTreeItem(tree, SWT.NONE);
			roots[i].setText("Root " + i);
			for (int j = 0; j < 2; j++) new TableTreeItem(roots[i], SWT.NONE).setText("Child " + i + "." + j);
		}
		pump(tree);
		Table table = tree.getTable();
		check(tree.getItemCount() == 3 && table.getItemCount() == 3, "three roots, their children are not shown: " + table.getItemCount() + " rows");
		roots[1].setExpanded(true);
		pump(tree);
		check(table.getItemCount() == 5 && table.getItem(2).getText().equals("Child 1.0"), "setExpanded shows the children below: " + table.getItemCount() + " rows");
		roots[1].setExpanded(false);
		pump(tree);
		check(table.getItemCount() == 3, "and hides them");
		/* A click on the plus sign (the image of the first column) of the first root */
		Rectangle plus = table.getItem(0).getImageBounds(0);
		check(plus.width > 0, "the plus sign is the image of the row: " + plus);
		Event down = new Event();
		down.button = 1;
		down.x = plus.x + plus.width / 2;
		down.y = plus.y + plus.height / 2;
		table.notifyListeners(SWT.MouseDown, down);
		pump(tree);
		check(roots[0].getExpanded() && table.getItemCount() == 5, "a MouseDown on it expands the root: " + table.getItemCount() + " rows");
		/* The same with a real click: it goes to the window of the records of the container */
		roots[0].setExpanded(false);
		pump(tree);
		plus = table.getItem(1).getImageBounds(0);
		int records = OS.WinWindowFromID(table.handle, OS.CID_LEFTDVWND);
		int[] point = {plus.x + plus.width / 2, table.getSize().y - 1 - (plus.y + plus.height / 2)};
		OS.WinMapWindowPoints(table.handle, records, point, 1);
		int mp1 = (point[0] & 0xFFFF) | (point[1] << 16);
		OS.WinPostMsg(records, OS.WM_BUTTON1DOWN, mp1, 0);
		OS.WinPostMsg(records, OS.WM_BUTTON1UP, mp1, 0);
		OS.WinPostMsg(records, OS.WM_BUTTON1CLICK, mp1, 0);
		pump(tree);
		check(roots[1].getExpanded() && table.getItemCount() == 5, "a click of the mouse on the plus sign expands it: " + table.getItemCount() + " rows");
		tree.dispose();
	}

	void checkEditorAndOthers(Shell window) {
		saySubObjective("TableEditor, StackLayout, BusyIndicator");
		Table table = new Table(window, SWT.BORDER);
		table.setBounds(0, 0, 300, 150);
		new TableColumn(table, SWT.NONE).setWidth(100);
		new TableColumn(table, SWT.NONE).setWidth(120);
		TableItem item = new TableItem(table, SWT.NONE);
		item.setText(new String[] {"name", "value"});
		pump(table);
		TableEditor editor = new TableEditor(table);
		editor.grabHorizontal = true;
		editor.grabVertical = true;
		Text text = new Text(table, SWT.NONE);
		text.setText("edited");
		editor.setEditor(text, item, 1);
		pump(table);
		Rectangle cell = item.getBounds(1), bounds = text.getBounds();
		check(bounds.x == cell.x && bounds.width == cell.width && bounds.y == cell.y, "a TableEditor puts its control over the cell: " + bounds + ", cell " + cell);
		editor.dispose();
		table.dispose();

		Composite stack = new Composite(window, SWT.NONE);
		stack.setBounds(0, 0, 200, 100);
		StackLayout layout = new StackLayout();
		stack.setLayout(layout);
		Button first = new Button(stack, SWT.PUSH), second = new Button(stack, SWT.PUSH);
		layout.topControl = second;
		stack.layout();
		check(second.getVisible() && !first.getVisible() && second.getSize().equals(new Point(200, 100)), "StackLayout shows the top control only, with all the room: " + second.getSize());
		stack.dispose();

		final boolean[] ran = new boolean[1];
		BusyIndicator.showWhile(display, new Runnable() {
			public void run() { ran[0] = true; }
		});
		check(ran[0], "BusyIndicator.showWhile runs the code");
	}

	void initComponents() {
		image = new Image(display, 16, 16);
		GC gc = new GC(image);
		gc.setBackground(display.getSystemColor(SWT.COLOR_GREEN));
		gc.fillRectangle(0, 0, 16, 16);
		gc.dispose();
		Shell window = new Shell(display, SWT.NO_TRIM);
		window.setBounds(10, 10, 320, 230);
		window.open();
		sayObjective("Automatic checks");
		checkScrolledComposite(window);
		checkViewForm(window);
		checkTableTree(window);
		checkEditorAndOthers(window);
		window.dispose();
		say("\n" + checks + " checks passed.\n");

		sayObjective("Interactive: scroll the view, expand the table tree with its plus signs, edit the cell");
		shell.setLayout(new FillLayout());
		ViewForm form = new ViewForm(shell, SWT.BORDER | SWT.FLAT);
		CLabel title = new CLabel(form, SWT.NONE);
		title.setText("A view with a scrolled composite");
		title.setImage(image);
		form.setTopLeft(title);
		ScrolledComposite scrolled = new ScrolledComposite(form, SWT.H_SCROLL | SWT.V_SCROLL);
		Composite content = new Composite(scrolled, SWT.NONE);
		content.setLayout(new org.eclipse.swt.layout.GridLayout(4, true));
		for (int i = 0; i < 40; i++) new Button(content, SWT.PUSH).setText("Button " + (i + 1));
		content.setSize(content.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		scrolled.setContent(content);
		form.setContent(scrolled);
		TableTree tree = new TableTree(shell, SWT.BORDER);
		for (int i = 0; i < 5; i++) {
			TableTreeItem root = new TableTreeItem(tree, SWT.NONE);
			root.setText("Root " + (i + 1));
			for (int j = 0; j < 3; j++) new TableTreeItem(root, SWT.NONE).setText("Child " + (i + 1) + "." + (j + 1));
		}
		shell.setSize(560, 300);
	}

	protected void done() {
		if (image != null) image.dispose();
		super.done();
	}

	void setTitle(String title) {
		super.setTitle(title);
	}

	public static void main(String[] args) {
		go(new SWT023_02());
	}

}
