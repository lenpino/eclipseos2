/*
 *  SWT020_01.java
 */

/*
 * Copyright (c) 2002, 2026 EclipseOS2 Team.
 * This file is made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 */

import java.lang.reflect.Field;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.pm.OS;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

/**
 *  Validates the CoolBar (the emulated one of Eclipse 2.0.1).
 *
 *  The first part is automatic: the layout of the items, the pixels of the
 *  grabbers on the screen and the dragging (the mouse events are sent to the
 *  cool bar, the pointer is not moved). The second part is interactive: a
 *  cool bar to drag, wrap and shrink.
 *
 *  To close the window automatically (for unattended runs) pass the system
 *  property swt.test.autoclose=<milliseconds> to the JVM.
 */
public class SWT020_01 extends SWTTestCase {

	static {
		STEP = "020";
		TEST = "01";
		DESC = "CoolBar";
	}

	int checks;
	CoolBar bar;
	CoolItem[] items;
	ToolBar[] tools;
	int height;
	Event selection;

	void check(boolean ok, String what) {
		checks++;
		say((ok ? "  ok    " : "  FAIL  ") + what);
		if (!ok) throw new RuntimeException("Check failed: " + what);
	}

	void run(int milliseconds) {
		OS.WinPostMsg(shell.handle, OS.WM_NULL, 0, 0);
		long end = System.currentTimeMillis() + milliseconds;
		do {
			while (display.readAndDispatch()) {}
			try { Thread.sleep(10); } catch (InterruptedException e) {}
		} while (System.currentTimeMillis() < end);
	}

	Object field(Object object, Class c, String name) {
		try {
			Field f = c.getDeclaredField(name);
			f.setAccessible(true);
			return f.get(object);
		} catch (Exception e) {
			throw new RuntimeException(e.toString());
		}
	}

	int pointer() {
		return ((Integer) field(bar, Control.class, "hCursor")).intValue();
	}

	int cursor(String name) {
		return ((Cursor) field(bar, CoolBar.class, name)).handle;
	}

	ToolBar chevron(CoolItem item) {
		return (ToolBar) field(item, CoolItem.class, "chevron");
	}

	boolean chevronShown(CoolItem item) {
		return chevron(item) != null && chevron(item).getVisible();
	}

	String chevronState(CoolItem item) {
		ToolBar c = chevron(item);
		return item.getSize().x + "/" + item.getPreferredSize().x + (c == null ? " none" : " " + c.getVisible() + " " + c.getBounds());
	}

	boolean near(int pel, RGB rgb) {
		int r = (pel >> 16) & 0xFF, g = (pel >> 8) & 0xFF, b = pel & 0xFF;
		return Math.abs(r - rgb.red) < 16 && Math.abs(g - rgb.green) < 16 && Math.abs(b - rgb.blue) < 16;
	}

	/** The color of the screen at a point of a control. */
	int pel(Control control, int x, int y) {
		Point p = control.toDisplay(new Point(x, y));
		int hps = OS.WinGetScreenPS(OS.HWND_DESKTOP);
		OS.GpiCreateLogColorTable(hps, 0, OS.LCOLF_RGB, 0, 0, null);
		int pel = OS.GpiQueryPel(hps, new int[] {p.x, OS.WinQuerySysValue(OS.HWND_DESKTOP, OS.SV_CYSCREEN) - 1 - p.y});
		OS.WinReleasePS(hps);
		return pel;
	}

	/** 'S' shadow, 'H' highlight, 'B' background of the bar or '?' for the pixel of the screen at a point of the bar. */
	char pixel(int x, int y) {
		int pel = pel(bar, x, y);
		if (near(pel, display.getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW).getRGB())) return 'S';
		if (near(pel, display.getSystemColor(SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW).getRGB())) return 'H';
		if (near(pel, bar.getBackground().getRGB())) return 'B';
		return '?';
	}

	/** The number of dark pixels (text) on the screen in a control of the bar. */
	int text(Control control) {
		Rectangle b = control.getBounds();
		Point origin = bar.toDisplay(new Point(b.x, b.y));
		int screen = OS.WinQuerySysValue(OS.HWND_DESKTOP, OS.SV_CYSCREEN);
		int hps = OS.WinGetScreenPS(OS.HWND_DESKTOP);
		OS.GpiCreateLogColorTable(hps, 0, OS.LCOLF_RGB, 0, 0, null);
		int count = 0;
		for (int y = 0; y < b.height; y++) {
			for (int x = 0; x < b.width; x++) {
				int pel = OS.GpiQueryPel(hps, new int[] {origin.x + x, screen - 1 - (origin.y + y)});
				if (((pel >> 16) & 0xFF) < 60 && ((pel >> 8) & 0xFF) < 60 && (pel & 0xFF) < 60) count++;
			}
		}
		OS.WinReleasePS(hps);
		return count;
	}

	/** The pixels of the separator and the grabber of an item, from left to right and the top and bottom of the grabber. */
	String grabber(CoolItem item) {
		Rectangle b = item.getBounds();
		int middle = b.y + b.height / 2, top = b.y + 2, bottom = b.y + b.height - 3;
		return "" + pixel(b.x, middle) + pixel(b.x + 1, middle) + pixel(b.x + 4, middle) + pixel(b.x + 5, middle) + pixel(b.x + 6, middle)
			+ " " + pixel(b.x + 5, top) + pixel(b.x + 5, bottom);
	}

	/** For the failures: the pixels across the middle of the grabber and down its middle column. */
	String strips(CoolItem item) {
		Rectangle b = item.getBounds();
		String across = "", down = "";
		for (int x = b.x - 2; x < b.x + 12; x++) across += pixel(x, b.y + b.height / 2);
		for (int y = b.y; y < b.y + b.height; y++) down += pixel(b.x + 5, y);
		Point p = bar.toDisplay(new Point(b.x + 5, b.y + b.height / 2));
		int hps = OS.WinGetScreenPS(OS.HWND_DESKTOP);
		OS.GpiCreateLogColorTable(hps, 0, OS.LCOLF_RGB, 0, 0, null);
		int pel = OS.GpiQueryPel(hps, new int[] {p.x, OS.WinQuerySysValue(OS.HWND_DESKTOP, OS.SV_CYSCREEN) - 1 - p.y});
		OS.WinReleasePS(hps);
		return " (across from x-2: " + across + ", down x+5 from y: " + down + "; x+5 is " + Integer.toHexString(pel)
			+ ", shadow " + display.getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW).getRGB()
			+ ", highlight " + display.getSystemColor(SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW).getRGB()
			+ ", background " + bar.getBackground().getRGB() + ")";
	}

	void mouse(int type, int x, int y) {
		Event event = new Event();
		event.x = x;
		event.y = y;
		event.button = 1;
		bar.notifyListeners(type, event);
	}

	String order(int[] array) {
		String s = "";
		for (int i = 0; i < array.length; i++) s += (i > 0 ? "," : "") + array[i];
		return "{" + s + "}";
	}

	ToolBar toolBar(Control parent, String[] names) {
		ToolBar tb = new ToolBar((CoolBar) parent, SWT.FLAT);
		for (int i = 0; i < names.length; i++) new ToolItem(tb, SWT.PUSH).setText(names[i]);
		tb.pack();
		return tb;
	}

	CoolItem coolItem(CoolBar parent, Control control) {
		CoolItem item = new CoolItem(parent, SWT.NONE);
		/* The size before setControl, which puts the control in the item (still empty) */
		Point size = control.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		item.setControl(control);
		Point preferred = item.computeSize(size.x, size.y);
		item.setPreferredSize(preferred);
		item.setSize(preferred);
		return item;
	}

	void initComponents() {
		automatic();
		interactive();
	}

	void checkLayout() {
		saySubObjective("The layout of a row");
		Rectangle[] b = new Rectangle[3];
		for (int i = 0; i < 3; i++) b[i] = items[i].getBounds();
		height = tools[0].computeSize(SWT.DEFAULT, SWT.DEFAULT).y + 4;
		check(bar.getItemCount() == 3 && bar.indexOf(items[1]) == 1 && bar.getItem(2) == items[2], "three items, in order");
		check(b[0].x == 0 && b[0].y == 0 && b[0].height == height && b[0].width == items[0].getPreferredSize().x,
			"the first one at 0,0 with its preferred width and the height of its tool bar and the margins: " + b[0]);
		check(b[1].x == b[0].x + b[0].width && b[2].x == b[1].x + b[1].width && b[1].y == 0 && b[2].y == 0, "the others follow it: " + b[1] + " " + b[2]);
		check(b[2].x + b[2].width == bar.getSize().x, "the last one fills the row: " + b[2] + " " + bar.getSize());
		check(bar.getBorderWidth() == 0 && bar.getSize().y == height, "the bar, without border, is as high as the row: " + bar.getBorderWidth() + " " + bar.getSize());
		Rectangle c = tools[1].getBounds();
		check(c.x == b[1].x + 10 && c.y == 2, "the control of an item after its grabber: " + c);
		check(!chevronShown(items[0]) && !chevronShown(items[1]), "no chevron when the items have their preferred width: " + chevronState(items[0]) + " " + chevronState(items[1]));
	}

	void checkPixels() {
		saySubObjective("The separator and the grabber on the screen");
		RGB shadow = display.getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW).getRGB();
		RGB light = display.getSystemColor(SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW).getRGB();
		RGB background = display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND).getRGB();
		check(shadow.red + 40 < background.red && light.red > background.red, "the shadow is darker than the background and the highlight lighter: "
			+ shadow + " " + background + " " + light);
		run(300);
		String s = grabber(items[1]);
		check(s.equals("SHHBS HS"), "separator, grabber and its top and bottom, left to right (SH H B S, top H, bottom S): " + s + (s.equals("SHHBS HS") ? "" : strips(items[1])));
		int before = text(tools[1]);
		check(before > 20, "the text of the buttons of a tool bar is on the screen: " + before + " dark pixels");
		bar.redraw();
		run(300);
		int after = text(tools[1]);
		check(after == before, "the bar repainted does not paint over its children (WS_CLIPCHILDREN): " + after + " dark pixels");
	}

	void checkDrag() {
		saySubObjective("Dragging a grabber");
		Rectangle b0 = items[0].getBounds(), b1 = items[1].getBounds();
		int x = b1.x + 5, y = b1.y + 5;
		mouse(SWT.MouseMove, x, y);
		check(pointer() == cursor("hoverCursor"), "over a grabber the pointer is the one to move left and right");
		mouse(SWT.MouseMove, x + 20, y);
		check(pointer() == 0, "out of it, the one of the parent");
		mouse(SWT.MouseDown, x, y);
		check(pointer() == cursor("dragCursor"), "pressed, the one to move to all sides");
		mouse(SWT.MouseMove, x - 20, y);
		Rectangle a0 = items[0].getBounds(), a1 = items[1].getBounds();
		check(a1.x == b1.x - 20 && a1.width == b1.width + 20 && a0.width == b0.width - 20, "20 to the left, the item to the left is narrower: " + a0 + " " + a1);
		check(tools[1].getBounds().x == a1.x + 10, "its control moves with it: " + tools[1].getBounds());
		check(chevronShown(items[0]), "the item narrower than its preferred width shows a chevron");
		run(300);
		String s = grabber(items[1]);
		check(s.equals("SHHBS HS"), "the grabber is drawn where it is now: " + s);
		mouse(SWT.MouseMove, x, y);
		check(items[1].getBounds().equals(b1) && items[0].getBounds().equals(b0) && !chevronShown(items[0]), "and back: " + items[1].getBounds());
		mouse(SWT.MouseMove, x, b1.y + b1.height + 5);
		Rectangle a2 = items[1].getBounds();
		check(a2.x == 0 && a2.y == height + 2 && a2.width == bar.getSize().x, "below the row, to a new row: " + a2);
		check(bar.getSize().y == 2 * height + 2, "the bar is higher: " + bar.getSize());
		check(order(bar.getItemOrder()).equals("{0,2,1}") && order(bar.getWrapIndices()).equals("{2}"), "item order and wrap indices: " + order(bar.getItemOrder()) + " " + order(bar.getWrapIndices()));
		check(tools[0].getBounds().y == 2 && tools[1].getBounds().y == height + 4,
			"the controls are in their rows, also those of the first one when the bar grows: " + tools[0].getBounds().y + " " + tools[1].getBounds().y);
		mouse(SWT.MouseUp, x, b1.y + b1.height + 5);
		check(pointer() == 0 && field(bar, CoolBar.class, "dragging") == null, "released, the pointer of the parent and no drag");
		run(300);
		s = grabber(items[1]);
		check(s.equals("SHHBS HS"), "the grabber in the second row: " + s);
	}

	void checkDoubleClick() {
		saySubObjective("Double click on a grabber");
		Rectangle b = items[0].getBounds();
		mouse(SWT.MouseDoubleClick, b.x + 5, b.y + 5);
		Rectangle a0 = items[0].getBounds(), a2 = items[2].getBounds();
		check(a0.x + a0.width == a2.x && a2.x + a2.width == bar.getSize().x && a0.width > b.width, "the item takes the row: " + a0 + " " + a2);
		check(chevronShown(items[2]), "the other one shows its chevron");
		mouse(SWT.MouseDoubleClick, b.x + 5, b.y + 5);
		a0 = items[0].getBounds();
		a2 = items[2].getBounds();
		check(a0.width < items[0].getPreferredSize().x && a0.x + a0.width == a2.x && chevronShown(items[0]), "again, it takes its minimum width: " + a0 + " " + a2);
		bar.setCursor(null);
	}

	void checkLocked() {
		saySubObjective("Locked");
		bar.setLocked(true);
		check(bar.getLocked(), "getLocked");
		Rectangle b = items[2].getBounds();
		mouse(SWT.MouseDown, b.x + 5, b.y + 5);
		mouse(SWT.MouseMove, b.x - 15, b.y + 5);
		mouse(SWT.MouseUp, b.x - 15, b.y + 5);
		check(items[2].getBounds().equals(b) && pointer() == 0, "the grabber cannot be dragged: " + items[2].getBounds());
		run(300);
		String s = grabber(items[2]);
		check(s.startsWith("SHBBB"), "only the separator is drawn: " + s);
		check(text(tools[2]) > 20, "and the text of the tool bar is still there: " + text(tools[2]) + " dark pixels");
		bar.setLocked(false);
	}

	void checkItemLayout(Point[] sizes) {
		saySubObjective("setItemLayout");
		bar.setItemLayout(new int[] {2, 0, 1}, new int[] {1}, new Point[] {sizes[2], sizes[0], sizes[1]});
		check(order(bar.getItemOrder()).equals("{2,0,1}") && order(bar.getWrapIndices()).equals("{1}"), "item order and wrap indices: " + order(bar.getItemOrder()) + " " + order(bar.getWrapIndices()));
		Rectangle b2 = items[2].getBounds(), b0 = items[0].getBounds();
		check(b2.x == 0 && b2.y == 0 && b0.x == 0 && b0.y == height + 2, "the third one at the top, the first one starts the second row: " + b2 + " " + b0);
		check(tools[0].getBounds().y == height + 4 && tools[2].getBounds().y == 2, "and their controls: " + tools[0].getBounds() + " " + tools[2].getBounds());
		bar.setWrapIndices(new int[0]);
		check(bar.getSize().y == height && items[0].getBounds().y == 0 && tools[2].getBounds().y == 2, "without wraps, one row again: " + bar.getSize());
		bar.setItemLayout(new int[] {0, 1, 2}, new int[0], sizes);
	}

	void checkChevron() {
		saySubObjective("The chevron");
		Point preferred = items[0].getPreferredSize();
		items[0].setSize(preferred.x - 20, preferred.y);
		ToolBar chevron = chevron(items[0]);
		check(chevronShown(items[0]), "narrower than its preferred width, the item shows the chevron");
		Rectangle c = chevron.getBounds(), b = items[0].getBounds();
		check(c.x + c.width + 2 == b.x + b.width && c.y == b.y + 2, "at its right: " + c + " " + b);
		check(tools[0].getBounds().x + tools[0].getBounds().width <= c.x, "the control does not go under it: " + tools[0].getBounds());
		chevron.getItem(0).notifyListeners(SWT.Selection, new Event());
		run(100);
		check(selection != null && selection.detail == SWT.ARROW && selection.x == c.x && selection.y == c.y + c.height,
			"its selection is a selection of the item, with ARROW and the point below the chevron: "
			+ (selection == null ? "none" : selection.detail + " " + selection.x + "," + selection.y));
	}

	void automatic() {
		shell.setSize(500, 300);
		sayObjective("Automatic checks (do not move the mouse over the window)");
		bar = new CoolBar(shell, SWT.NONE);
		tools = new ToolBar[] {
			toolBar(bar, new String[] {"New", "Open", "Save"}),
			toolBar(bar, new String[] {"Cut", "Copy", "Paste"}),
			toolBar(bar, new String[] {"Back", "Forward"}),
		};
		items = new CoolItem[3];
		Point[] sizes = new Point[3];
		for (int i = 0; i < 3; i++) {
			items[i] = coolItem(bar, tools[i]);
			sizes[i] = items[i].getSize();
		}
		items[0].addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				selection = e;
			}
		});
		Point size = bar.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		bar.setBounds(10, 10, 460, size.y);
		/* The pixels are read from the screen: the window is shown */
		/* setVisible (false) before the shell is open did nothing on PM */
		Button hidden = new Button(shell, SWT.PUSH);
		hidden.setVisible(false);
		shell.setLocation(40, 40);
		shell.open();
		run(400);
		check(!hidden.getVisible() && !OS.WinIsWindowVisible(hidden.handle), "a control hidden before the shell is opened stays hidden");
		hidden.dispose();
		Combo combo = new Combo(shell, SWT.DROP_DOWN);
		combo.setBounds(10, 200, 120, 24);
		int comboHeight = combo.computeSize(SWT.DEFAULT, SWT.DEFAULT).y;
		check(combo.getSize().y == comboHeight && combo.getBounds().height == comboHeight && combo.getBounds().y == 200,
			"a drop down combo is as high as its text field, as on Windows (the window of PM includes the list): " + combo.getBounds() + " " + comboHeight);
		/* A window with a combo opened over a black one: what is not painted stays black */
		Point corner = shell.toDisplay(new Point(250, 100));
		Shell black = new Shell(shell, SWT.NO_TRIM);
		black.setBackground(display.getSystemColor(SWT.COLOR_BLACK));
		black.setBounds(corner.x, corner.y, 200, 150);
		black.open();
		run(300);
		Shell front = new Shell(shell, SWT.NO_TRIM);
		front.setBounds(corner.x, corner.y, 200, 150);
		Combo frontCombo = new Combo(front, SWT.DROP_DOWN);
		frontCombo.setBounds(10, 10, 120, 24);
		front.open();
		run(300);
		RGB frontBackground = front.getBackground().getRGB();
		String below = "";
		for (int y = comboHeight + 1; y < comboHeight + 60; y += 6) below += near(pel(frontCombo, 60, y), frontBackground) ? 'B' : '?';
		check(below.indexOf('?') < 0, "below its text field the background of the parent is seen, not the screen behind (the parent clips its children): "
			+ below + " " + Integer.toHexString(pel(frontCombo, 60, comboHeight + 10)));
		check(!near(pel(frontCombo, 60, comboHeight / 2), frontBackground), "and the text field is not painted over: " + Integer.toHexString(pel(frontCombo, 60, comboHeight / 2)));
		front.dispose();
		black.dispose();
		combo.dispose();
		checkLayout();
		checkPixels();
		checkDrag();
		checkDoubleClick();
		checkLocked();
		checkItemLayout(sizes);
		checkChevron();
		Combo barCombo = new Combo(bar, SWT.DROP_DOWN);
		barCombo.setItems(new String[] {"one", "two"});
		/* The first one of the row: the last one fills it */
		CoolItem comboItem = new CoolItem(bar, SWT.NONE, 0);
		Point comboSize = barCombo.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		comboItem.setControl(barCombo);
		Point preferred = comboItem.computeSize(comboSize.x, comboSize.y);
		comboItem.setPreferredSize(preferred);
		comboItem.setSize(preferred.x - 20, preferred.y);
		check(chevronShown(comboItem) && barCombo.getSize().y == comboSize.y,
			"a narrow item of a combo, as high as its text field, shows its chevron: " + comboItem.getBounds() + " " + barCombo.getSize());
		comboItem.dispose();
		barCombo.dispose();
		saySubObjective("Disposing");
		ToolBar chevron = chevron(items[0]);
		items[1].dispose();
		check(bar.getItemCount() == 2 && bar.getItem(1) == items[2] && items[2].getBounds().x == items[0].getBounds().width, "an item disposed, the next one takes its place: " + items[2].getBounds());
		Cursor hover = (Cursor) field(bar, CoolBar.class, "hoverCursor");
		shell.setVisible(false);
		run(100);
		bar.dispose();
		check(items[0].isDisposed() && chevron.isDisposed() && hover.isDisposed(), "the bar disposed, its items, chevrons and cursors too");
		say("\n" + checks + " checks passed.\n");
	}

	void interactive() {
		sayObjective("Interactive: drag the grabbers left and right and to other rows, double click them, "
			+ "make the window narrow and use the chevrons");
		shell.setSize(500, 300);
		final CoolBar coolBar = new CoolBar(shell, SWT.NONE);
		String[][] names = {{"New", "Open", "Save"}, {"Cut", "Copy", "Paste"}, {"Back", "Forward"}};
		Listener chevronListener = new Listener() {
			public void handleEvent(Event e) {
				if (e.detail != SWT.ARROW) return;
				CoolItem item = (CoolItem) e.widget;
				ToolBar tb = (ToolBar) item.getControl();
				int visible = tb.getSize().x;
				Menu menu = new Menu(shell, SWT.POP_UP);
				ToolItem[] toolItems = tb.getItems();
				for (int i = 0; i < toolItems.length; i++) {
					Rectangle r = toolItems[i].getBounds();
					if (r.x + r.width > visible) new MenuItem(menu, SWT.PUSH).setText(toolItems[i].getText());
				}
				Point p = coolBar.toDisplay(new Point(e.x, e.y));
				menu.setLocation(p.x, p.y);
				menu.setVisible(true);
			}
		};
		for (int i = 0; i < names.length; i++) {
			coolItem(coolBar, toolBar(coolBar, names[i])).addListener(SWT.Selection, chevronListener);
		}
		Combo combo = new Combo(coolBar, SWT.DROP_DOWN);
		combo.setItems(new String[] {"one", "two", "three"});
		combo.setText("one");
		combo.pack();
		coolItem(coolBar, combo);
		final Label label = new Label(shell, SWT.WRAP);
		label.setText("Drag the grabbers (the small bars at the left of the items). A double click "
			+ "on a grabber makes the item take the row, or its minimum width. A narrow item shows "
			+ "a chevron (>>): it shows a menu with the hidden buttons.");
		final Button locked = new Button(shell, SWT.CHECK);
		locked.setText("Locked");
		locked.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				coolBar.setLocked(locked.getSelection());
			}
		});
		final Runnable layout = new Runnable() {
			public void run() {
				Rectangle area = shell.getClientArea();
				int barHeight = coolBar.getSize().y;
				label.setBounds(10, barHeight + 10, area.width - 20, 60);
				locked.setBounds(10, barHeight + 80, 100, 24);
			}
		};
		coolBar.addListener(SWT.Resize, new Listener() {
			public void handleEvent(Event e) {
				layout.run();
			}
		});
		shell.addListener(SWT.Resize, new Listener() {
			public void handleEvent(Event e) {
				Rectangle area = shell.getClientArea();
				coolBar.setSize(area.width, coolBar.computeSize(area.width, SWT.DEFAULT).y);
				layout.run();
			}
		});
		Rectangle area = shell.getClientArea();
		coolBar.setSize(area.width, coolBar.computeSize(area.width, SWT.DEFAULT).y);
		layout.run();
		shell.setVisible(true);
	}

	public static void main(String[] args) {
		go(new SWT020_01());
	}

}
