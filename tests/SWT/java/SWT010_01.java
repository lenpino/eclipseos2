/*
 *  SWT010_01.java
 */

/*
 * Copyright (c) 2002, 2026 EclipseOS2 Team.
 * This file is made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 */

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.pm.OS;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

/**
 *  Validates the ToolBar and ToolItem widgets.
 *
 *  The first part is automatic: any failed check throws an exception, so
 *  the testcase is reported as FAILED. Mouse clicks are simulated by sending
 *  a Selection event to the internal button of the item. The second part is
 *  interactive: use the tool bars, resize the window (the second tool bar
 *  wraps), and watch the events printed to the console.
 *
 *  To close the window automatically (for unattended runs) pass the system
 *  property swt.test.autoclose=<milliseconds> to the JVM.
 */
public class SWT010_01 extends SWTTestCase {

	static {
		STEP = "010";
		TEST = "01";
		DESC = "ToolBar / ToolItem";
	}

	int checks;
	Image[] images = new Image[4];

	Shell createTopShell(Display display) {
		return new Shell(display, SWT.SHELL_TRIM | SWT.NO_REDRAW_RESIZE);
	}

	void check(boolean ok, String what) {
		checks++;
		say((ok ? "  ok    " : "  FAIL  ") + what);
		if (!ok) throw new RuntimeException("Check failed: " + what);
	}

	/*
	 * Deferred (posted) events run after a window message is dispatched, so
	 * an otherwise idle loop needs one more message to deliver them.
	 */
	void pump() {
		OS.WinPostMsg(shell.handle, OS.WM_NULL, 0, 0);
		while (display.readAndDispatch()) {}
	}

	Image makeImage(int rgbIndex) {
		Image image = new Image(display, 16, 16);
		GC gc = new GC(image);
		gc.setBackground(display.getSystemColor(new int[] {
				SWT.COLOR_RED, SWT.COLOR_GREEN, SWT.COLOR_BLUE, SWT.COLOR_YELLOW }[rgbIndex % 4]));
		gc.fillRectangle(1, 1, 14, 14);
		gc.dispose();
		return image;
	}

	/** The internal button that shows the item (or its arrow, for drop downs). */
	Button buttonOf(ToolBar bar, ToolItem item, boolean arrow) {
		Rectangle ib = item.getBounds();
		Control[] children = bar.getChildren();
		for (int i = 0; i < children.length; i++) {
			if (!(children[i] instanceof Button)) continue;
			Button b = (Button) children[i];
			boolean isArrow = (b.getStyle() & SWT.ARROW) != 0;
			Rectangle r = b.getBounds();
			if (arrow && isArrow && r.x + r.width == ib.x + ib.width && r.y == ib.y) return b;
			if (!arrow && !isArrow && r.x == ib.x && r.y == ib.y) return b;
		}
		return null;
	}

	void click(ToolBar bar, ToolItem item, boolean arrow) {
		Button b = buttonOf(bar, item, arrow);
		if (b == null) throw new RuntimeException("no button for item " + item.getText());
		b.notifyListeners(SWT.Selection, new Event());
		pump();
	}

	void checkCreation(Shell s) {
		saySubObjective("Creation and item list");
		ToolBar bar = new ToolBar(s, SWT.FLAT);
		check(bar.getItemCount() == 0, "new tool bar has no items");
		Point empty = bar.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		check(empty.x > 0 && empty.y > 0, "empty tool bar has a default size: " + empty);
		check((bar.getStyle() & SWT.HORIZONTAL) != 0, "style is HORIZONTAL by default");

		ToolItem push = new ToolItem(bar, SWT.PUSH);
		push.setText("Push");
		ToolItem check = new ToolItem(bar, SWT.CHECK);
		check.setText("Check");
		ToolItem last = new ToolItem(bar, SWT.PUSH);
		last.setText("Last");
		check(bar.getItemCount() == 3, "three items added");

		ToolItem inserted = new ToolItem(bar, SWT.PUSH, 1);
		inserted.setText("Inserted");
		check(bar.getItem(1) == inserted && bar.getItem(2) == check && bar.getItem(3) == last,
				"ToolItem(parent, style, index) inserts in the middle");
		check(bar.indexOf(last) == 3 && bar.indexOf(push) == 0, "indexOf follows the order");
		ToolItem[] copy = bar.getItems();
		copy[0] = null;
		check(bar.getItem(0) == push, "getItems returns a copy");
		check(push.getParent() == bar, "getParent");

		boolean thrown = false;
		try { bar.getItem(4); } catch (IllegalArgumentException e) { thrown = true; }
		check(thrown, "getItem out of range throws");
		thrown = false;
		try { new ToolItem(bar, SWT.PUSH, 9); } catch (IllegalArgumentException e) { thrown = true; }
		check(thrown, "insert out of range throws");
		check(bar.getItemCount() == 4, "failed insert leaves the list untouched");
		bar.dispose();
	}

	void checkLayout(Shell s) {
		saySubObjective("Layout and bounds");
		ToolBar bar = new ToolBar(s, SWT.FLAT);
		ToolItem a = new ToolItem(bar, SWT.PUSH);
		a.setText("One");
		ToolItem sep = new ToolItem(bar, SWT.SEPARATOR);
		ToolItem b = new ToolItem(bar, SWT.PUSH);
		b.setText("Two");
		Rectangle ra = a.getBounds(), rs = sep.getBounds(), rb = b.getBounds();
		check(ra.width > 0 && ra.height > 0, "item has a size: " + ra);
		check(rs.x == ra.x + ra.width && rb.x == rs.x + rs.width, "items follow one another: " + ra + " " + rs + " " + rb);
		check(a.getWidth() == ra.width, "getWidth matches the bounds");
		check(bar.getItem(new Point(rb.x + 1, rb.y + 1)) == b, "getItem(Point) finds the item");
		check(bar.getItem(new Point(-5, -5)) == null, "getItem(Point) outside gives null");

		sep.setWidth(30);
		check(sep.getWidth() == 30, "setWidth changes a separator: " + sep.getWidth());
		check(b.getBounds().x == sep.getBounds().x + 30, "following items move with the separator");

		Point size = bar.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		check(size.x >= rb.x + rb.width + 20 && size.y >= rb.height, "computeSize covers all the items: " + size);
		check(bar.getRowCount() == 1, "one row without WRAP");

		a.dispose();
		check(bar.getItemCount() == 2 && a.isDisposed(), "dispose removes the item");
		check(sep.getBounds().x == 0, "the next item moves to the start: " + sep.getBounds());
		bar.dispose();
		check(sep.isDisposed() && b.isDisposed(), "disposing the tool bar disposes its items");
	}

	void checkWrap(Shell s) {
		saySubObjective("WRAP and VERTICAL");
		ToolBar bar = new ToolBar(s, SWT.FLAT | SWT.WRAP);
		for (int i = 0; i < 6; i++) new ToolItem(bar, SWT.PUSH).setText("Item " + i);
		Point full = bar.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		Point narrow = bar.computeSize(full.x / 3, SWT.DEFAULT);
		check(narrow.y > full.y, "computeSize with a narrow hint takes more rows: " + full + " -> " + narrow);
		bar.setSize(narrow);
		check(bar.getRowCount() > 1, "WRAP bar reports several rows: " + bar.getRowCount());
		Rectangle last = bar.getItem(5).getBounds();
		check(last.y > 0, "last item went to a lower row: " + last);
		bar.setSize(full);
		check(bar.getRowCount() == 1, "back to one row when wide enough");
		bar.dispose();

		ToolBar vbar = new ToolBar(s, SWT.FLAT | SWT.VERTICAL);
		new ToolItem(vbar, SWT.PUSH).setText("A");
		new ToolItem(vbar, SWT.PUSH).setText("B");
		Rectangle r0 = vbar.getItem(0).getBounds(), r1 = vbar.getItem(1).getBounds();
		check(r1.y == r0.y + r0.height && r1.x == r0.x, "VERTICAL bar stacks the items");
		vbar.dispose();
	}

	void checkSelection(Shell s) {
		saySubObjective("Selection, radio group, enabled and events");
		ToolBar bar = new ToolBar(s, SWT.FLAT);
		final ToolItem push = new ToolItem(bar, SWT.PUSH);
		push.setText("Push");
		ToolItem check = new ToolItem(bar, SWT.CHECK);
		check.setText("Check");
		ToolItem r1 = new ToolItem(bar, SWT.RADIO);
		r1.setText("R1");
		ToolItem r2 = new ToolItem(bar, SWT.RADIO);
		r2.setText("R2");
		ToolItem drop = new ToolItem(bar, SWT.DROP_DOWN);
		drop.setText("Drop");

		check(!push.getSelection(), "a PUSH item is never selected");
		push.setSelection(true);
		check(!push.getSelection(), "setSelection is ignored for PUSH");
		check.setSelection(true);
		check(check.getSelection(), "CHECK item can be selected");
		check.setSelection(false);
		check(!check.getSelection(), "CHECK item can be unselected");

		r1.setSelection(true);
		click(bar, r2, false);
		check(r2.getSelection() && !r1.getSelection(), "clicking a RADIO clears the other one of its group");
		click(bar, r1, false);
		check(r1.getSelection() && !r2.getSelection(), "and back");

		final int[] hits = new int[1];
		final int[] detail = new int[] {-1};
		push.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				hits[0]++;
				detail[0] = e.detail;
			}
		});
		click(bar, push, false);
		check(hits[0] == 1 && detail[0] == 0, "clicking a PUSH item sends Selection (hits=" + hits[0] + ")");

		final int[] arrowDetail = new int[] {-1};
		drop.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				arrowDetail[0] = e.detail;
			}
		});
		check(buttonOf(bar, drop, true) != null, "drop down item has an arrow");
		click(bar, drop, true);
		check(arrowDetail[0] == SWT.ARROW, "clicking the arrow sends Selection with detail ARROW");

		push.setEnabled(false);
		check(!push.getEnabled() && !push.isEnabled(), "setEnabled(false)");
		check(!buttonOf(bar, push, false).getEnabled(), "the button of a disabled item is disabled");
		push.setEnabled(true);
		check(buttonOf(bar, push, false).getEnabled(), "and enabled again");

		push.setToolTipText("tip");
		check("tip".equals(push.getToolTipText()), "tool tip text is kept");
		bar.dispose();
	}

	void checkImagesAndControls(Shell s) {
		saySubObjective("Images and controls in separators");
		ToolBar bar = new ToolBar(s, SWT.FLAT);
		ToolItem item = new ToolItem(bar, SWT.PUSH);
		item.setText("Text");
		Rectangle textBounds = item.getBounds();
		Image image = makeImage(0);
		item.setImage(image);
		check(item.getImage() == image, "setImage/getImage");
		check(item.getBounds().width >= 16, "an item with an image is at least as wide as the image: " + item.getBounds());
		item.setDisabledImage(makeImage(1));
		item.setHotImage(makeImage(2));
		check(item.getDisabledImage() != null && item.getHotImage() != null, "disabled and hot images are kept");
		item.setEnabled(false);
		item.setEnabled(true);
		item.setImage(null);
		check(item.getText().equals("Text"), "the text is back when the image is removed");

		ToolItem sep = new ToolItem(bar, SWT.SEPARATOR);
		Text text = new Text(bar, SWT.BORDER);
		sep.setWidth(text.computeSize(SWT.DEFAULT, SWT.DEFAULT).x + 10);
		sep.setControl(text);
		Rectangle sb = sep.getBounds(), tb = text.getBounds();
		check(sep.getControl() == text, "getControl");
		check(tb.x >= sb.x && tb.x + tb.width <= sb.x + sb.width && tb.y >= sb.y && tb.y + tb.height <= sb.y + sb.height,
				"the control lies inside the separator: " + tb + " in " + sb);

		boolean thrown = false;
		Text stranger = new Text(s, SWT.BORDER);
		try { sep.setControl(stranger); } catch (IllegalArgumentException e) { thrown = true; }
		check(thrown, "a control of another parent is refused");
		stranger.dispose();
		bar.dispose();
		image.dispose();
	}

	void initComponents() {
		sayObjective("Automatic checks");
		Shell probe = new Shell(display, SWT.SHELL_TRIM);
		probe.setSize(500, 200);
		try {
			checkCreation(probe);
			checkLayout(probe);
			checkWrap(probe);
			checkSelection(probe);
			checkImagesAndControls(probe);
		} finally {
			probe.dispose();
		}
		say("\n" + checks + " checks passed.\n");

		sayObjective("Interactive: use the tool bars, resize the window (the lower one wraps)");
		shell.setLayout(null);
		for (int i = 0; i < images.length; i++) images[i] = makeImage(i);
		final Text log = new Text(shell, SWT.BORDER | SWT.MULTI | SWT.READ_ONLY);

		final ToolBar flat = new ToolBar(shell, SWT.FLAT);
		SelectionAdapter report = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				ToolItem item = (ToolItem) e.widget;
				String msg = "Selection: " + item.getText() + " sel=" + item.getSelection()
						+ (e.detail == SWT.ARROW ? " (arrow)" : "");
				say(msg);
				log.setText(msg);
			}
		};
		String[] names = {"Open", "Save", "Print"};
		for (int i = 0; i < names.length; i++) {
			ToolItem item = new ToolItem(flat, SWT.PUSH);
			item.setImage(images[i]);
			item.setText(names[i]);
			item.setToolTipText(names[i]);
			item.addSelectionListener(report);
		}
		new ToolItem(flat, SWT.SEPARATOR);
		ToolItem bold = new ToolItem(flat, SWT.CHECK);
		bold.setText("Bold");
		bold.addSelectionListener(report);
		ToolItem left = new ToolItem(flat, SWT.RADIO);
		left.setText("Left");
		left.setSelection(true);
		left.addSelectionListener(report);
		ToolItem center = new ToolItem(flat, SWT.RADIO);
		center.setText("Center");
		center.addSelectionListener(report);
		new ToolItem(flat, SWT.SEPARATOR);
		ToolItem drop = new ToolItem(flat, SWT.DROP_DOWN);
		drop.setText("Drop");
		/* The arrow only sends Selection (detail ARROW): showing the menu is up to the application */
		final Menu menu = new Menu(shell, SWT.POP_UP);
		String[] choices = {"First choice", "Second choice", "Third choice"};
		for (int i = 0; i < choices.length; i++) {
			MenuItem choice = new MenuItem(menu, SWT.PUSH);
			choice.setText(choices[i]);
			final String name = choices[i];
			choice.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					say("Menu: " + name);
					log.setText("Menu: " + name);
				}
			});
		}
		drop.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				say("Selection: Drop" + (e.detail == SWT.ARROW ? " (arrow) at " + e.x + "," + e.y : ""));
				if (e.detail != SWT.ARROW) return;
				/*
				* Display coordinates, y grows downwards (not as in PM): the upper
				* left corner of the menu touches the upper right one of the item.
				*/
				Rectangle r = ((ToolItem) e.widget).getBounds();
				Point p = flat.toDisplay(new Point(r.x + r.width, r.y));
				menu.setLocation(p.x, p.y);
				menu.setVisible(true);
			}
		});
		ToolItem sep = new ToolItem(flat, SWT.SEPARATOR);
		Text field = new Text(flat, SWT.BORDER);
		field.setText("control");
		sep.setWidth(90);
		sep.setControl(field);
		ToolItem off = new ToolItem(flat, SWT.PUSH);
		off.setText("Disabled");
		off.setImage(images[3]);
		off.setDisabledImage(images[0]);
		off.setEnabled(false);

		final ToolBar wrap = new ToolBar(shell, SWT.FLAT | SWT.WRAP);
		for (int i = 0; i < 12; i++) {
			ToolItem item = new ToolItem(wrap, SWT.PUSH);
			item.setText("Tool " + (i + 1));
			item.addSelectionListener(report);
		}

		shell.addListener(SWT.Resize, new org.eclipse.swt.widgets.Listener() {
			public void handleEvent(Event e) {
				Rectangle area = shell.getClientArea();
				Point p1 = flat.computeSize(area.width, SWT.DEFAULT);
				flat.setBounds(0, 0, area.width, p1.y);
				Point p2 = wrap.computeSize(area.width, SWT.DEFAULT);
				wrap.setBounds(0, p1.y + 4, area.width, p2.y);
				int top = p1.y + p2.y + 8;
				log.setBounds(0, top, area.width, Math.max(20, area.height - top));
			}
		});
		shell.setSize(520, 220);
		setTitle(null);

		final String auto = System.getProperty("swt.test.autoclose");
		if (auto != null) {
			display.timerExec(Integer.parseInt(auto), new Runnable() {
				public void run() { shell.dispose(); }
			});
		}
	}

	protected void done() {
		for (int i = 0; i < images.length; i++) {
			if (images[i] != null) images[i].dispose();
		}
		super.done();
	}

	void setTitle(String title) {
		super.setTitle(title);
	}

	public static void main(String[] args) {
		go(new SWT010_01());
	}

}
