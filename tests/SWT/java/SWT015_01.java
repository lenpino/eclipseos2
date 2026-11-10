/*
 *  SWT015_01.java
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
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.pm.OS;
import org.eclipse.swt.internal.pm.POINTERINFO;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tracker;

/**
 *  Validates the Cursor and Control.setCursor().
 *
 *  The first part is automatic: the cursors are created and drawn into an
 *  image whose pixels are read, and WM_MOUSEMOVE is sent to the controls to
 *  see which pointer they set. The second part is interactive: move the
 *  pointer over the buttons, each one has the cursor of its name.
 *
 *  To close the window automatically (for unattended runs) pass the system
 *  property swt.test.autoclose=<milliseconds> to the JVM.
 */
public class SWT015_01 extends SWTTestCase {

	static {
		STEP = "015";
		TEST = "01";
		DESC = "Cursor";
	}

	static final int[] STYLES = {
		SWT.CURSOR_ARROW, SWT.CURSOR_WAIT, SWT.CURSOR_CROSS, SWT.CURSOR_APPSTARTING,
		SWT.CURSOR_HELP, SWT.CURSOR_SIZEALL, SWT.CURSOR_SIZENESW, SWT.CURSOR_SIZENS,
		SWT.CURSOR_SIZENWSE, SWT.CURSOR_SIZEWE, SWT.CURSOR_SIZEN, SWT.CURSOR_SIZES,
		SWT.CURSOR_SIZEE, SWT.CURSOR_SIZEW, SWT.CURSOR_SIZENE, SWT.CURSOR_SIZESE,
		SWT.CURSOR_SIZESW, SWT.CURSOR_SIZENW, SWT.CURSOR_UPARROW, SWT.CURSOR_IBEAM,
		SWT.CURSOR_NO, SWT.CURSOR_HAND,
	};
	static final String[] NAMES = {
		"ARROW", "WAIT", "CROSS", "APPSTARTING", "HELP", "SIZEALL", "SIZENESW", "SIZENS",
		"SIZENWSE", "SIZEWE", "SIZEN", "SIZES", "SIZEE", "SIZEW", "SIZENE", "SIZESE",
		"SIZESW", "SIZENW", "UPARROW", "IBEAM", "NO", "HAND",
	};
	/* The system pointer of each style, 0 when PM has none and it is made by SWT */
	static final int[] SYSTEM = {
		OS.SPTR_ARROW, OS.SPTR_WAIT, 0, OS.SPTR_WAIT, OS.SPTR_ARROW, OS.SPTR_MOVE, OS.SPTR_SIZENESW, OS.SPTR_SIZENS,
		OS.SPTR_SIZENWSE, OS.SPTR_SIZEWE, OS.SPTR_SIZENS, OS.SPTR_SIZENS, OS.SPTR_SIZEWE, OS.SPTR_SIZEWE,
		OS.SPTR_SIZENESW, OS.SPTR_SIZENWSE, OS.SPTR_SIZENESW, OS.SPTR_SIZENWSE, 0, OS.SPTR_TEXT,
		OS.SPTR_ILLEGAL, 0,
	};

	int checks;

	Shell createTopShell(Display display) {
		return new Shell(display, SWT.SHELL_TRIM | SWT.NO_REDRAW_RESIZE);
	}

	void check(boolean ok, String what) {
		checks++;
		say((ok ? "  ok    " : "  FAIL  ") + what);
		if (!ok) throw new RuntimeException("Check failed: " + what);
	}

	static int sys(int id) {
		return OS.WinQuerySysPointer(OS.HWND_DESKTOP, id, false);
	}

	static int pointer() {
		return OS.WinQueryPointer(OS.HWND_DESKTOP);
	}

	/** What the control does when the pointer moves over it. */
	static int moveOver(Control control) {
		OS.WinSendMsg(control.handle, OS.WM_MOUSEMOVE, 0, 0);
		return pointer();
	}

	void checkStyles() {
		saySubObjective("The styles");
		for (int i = 0; i < STYLES.length; i++) {
			Cursor c = new Cursor(display, STYLES[i]);
			boolean ok = c.handle != 0 && !c.isDisposed();
			if (SYSTEM[i] != 0) ok &= c.handle == sys(SYSTEM[i]);
			check(ok, NAMES[i] + (SYSTEM[i] != 0 ? " is the system pointer " + SYSTEM[i] : " is made by SWT") + ": " + c);
			c.dispose();
			check(c.isDisposed(), NAMES[i] + " is disposed");
		}
		check(sys(OS.SPTR_ARROW) != 0 && new Cursor(display, SWT.CURSOR_ARROW).handle == sys(OS.SPTR_ARROW),
				"disposing a system pointer does not destroy it");
		boolean threw = false;
		try { new Cursor(display, 12345); } catch (IllegalArgumentException e) { threw = true; }
		check(threw, "an unknown style throws");
		Cursor a = new Cursor(display, SWT.CURSOR_HAND), b = new Cursor(display, SWT.CURSOR_HAND);
		check(!a.equals(b) && a.equals(a) && a.hashCode() == a.handle, "each hand is a pointer of its own");
		a.dispose();
		b.dispose();
		a.dispose();
		check(a.isDisposed(), "disposing twice does nothing");
	}

	/** The colors of the pointer drawn on red, as a picture: '.' red (transparent), '#' black, 'o' white, '?' another */
	String[] picture(Cursor cursor) {
		Image image = new Image(display, 32, 32);
		GC gc = new GC(image);
		gc.setBackground(display.getSystemColor(SWT.COLOR_RED));
		gc.fillRectangle(0, 0, 32, 32);
		OS.WinDrawPointer(gc.handle, 0, 0, cursor.handle, OS.DP_NORMAL);
		/*
		 * WinDrawPointer works in device coordinates, GpiQueryPel in world ones: the
		 * GC turns the y axis with its default view matrix, so y is counted from the top.
		 */
		OS.GpiCreateLogColorTable(gc.handle, 0, OS.LCOLF_RGB, 0, 0, null);
		String[] rows = new String[32];
		for (int y = 0; y < 32; y++) {
			StringBuffer row = new StringBuffer();
			for (int x = 0; x < 32; x++) {
				int pel = OS.GpiQueryPel(gc.handle, new int[] {x, y});
				int r = (pel >> 16) & 0xFF, g = (pel >> 8) & 0xFF, b = pel & 0xFF;
				/* Lenient with the colors, in case the screen has less than 24 bits */
				row.append(r > 200 && g < 60 && b < 60 ? '.' : r < 60 && g < 60 && b < 60 ? '#' : r > 200 && g > 200 && b > 200 ? 'o' : '?');
			}
			rows[y] = row.toString();
		}
		gc.dispose();
		image.dispose();
		return rows;
	}

	void checkShapes() {
		saySubObjective("The pointers made by SWT, drawn and read back");
		Cursor cross = new Cursor(display, SWT.CURSOR_CROSS);
		String[] p = picture(cross);
		for (int y = 0; y < 32; y++) say("    " + p[y]);
		check(p[15].charAt(5) == '#' && p[5].charAt(15) == '#', "the cross is black: " + p[15].charAt(5) + p[5].charAt(15));
		check(p[14].charAt(5) == 'o' && p[5].charAt(14) == 'o', "with a white border: " + p[14].charAt(5) + p[5].charAt(14));
		check(p[5].charAt(5) == '.' && p[31].charAt(31) == '.', "and transparent around: " + p[5].charAt(5) + p[31].charAt(31));
		POINTERINFO info = new POINTERINFO();
		OS.WinQueryPointerInfo(cross.handle, info);
		check(info.fPointer != 0 && info.xHotSpot == 15 && info.yHotSpot == 16, "a pointer with the hot spot at the center (PM counts y from the bottom): " + info.xHotSpot + "," + info.yHotSpot);
		cross.dispose();

		Cursor hand = new Cursor(display, SWT.CURSOR_HAND);
		p = picture(hand);
		check(p[0].charAt(5) == '#' && p[2].charAt(6) == 'o' && p[2].charAt(0) == '.', "the hand of Windows: outline black, inside white, transparent around");
		OS.WinQueryPointerInfo(hand.handle, info);
		check(info.xHotSpot == 5 && info.yHotSpot == 31, "the hot spot is the tip of the finger: " + info.xHotSpot + "," + info.yHotSpot);
		hand.dispose();

		Cursor up = new Cursor(display, SWT.CURSOR_UPARROW);
		p = picture(up);
		check(p[1].charAt(15) == '#' && p[8].charAt(8) == '#' && p[20].charAt(15) == '#' && p[20].charAt(5) == '.', "the up arrow: " + p[1].charAt(15) + p[8].charAt(8) + p[20].charAt(15) + p[20].charAt(5));
		up.dispose();
	}

	void checkImageData() {
		saySubObjective("A cursor from image data");
		PaletteData palette = new PaletteData(new RGB[] {new RGB(0, 0, 0), new RGB(255, 255, 255)});
		ImageData source = new ImageData(32, 32, 1, palette);
		ImageData mask = new ImageData(32, 32, 1, palette);
		/* As on Windows the source is the AND plane and the mask the XOR plane */
		for (int y = 0; y < 32; y++) {
			for (int x = 0; x < 32; x++) {
				boolean square = x >= 4 && x < 20 && y >= 4 && y < 20;
				source.setPixel(x, y, square ? 0 : 1);
				mask.setPixel(x, y, square && x < 12 ? 1 : 0);
			}
		}
		Cursor c = new Cursor(display, source, mask, 4, 6);
		String[] p = picture(c);
		check(p[10].charAt(6) == 'o' && p[10].charAt(16) == '#' && p[2].charAt(2) == '.' && p[25].charAt(25) == '.',
				"its planes: " + p[10].charAt(6) + p[10].charAt(16) + p[2].charAt(2) + p[25].charAt(25));
		POINTERINFO info = new POINTERINFO();
		OS.WinQueryPointerInfo(c.handle, info);
		check(info.xHotSpot == 4 && info.yHotSpot == 25, "and its hot spot: " + info.xHotSpot + "," + info.yHotSpot);
		c.dispose();

		boolean threw = false;
		try { new Cursor(display, source, new ImageData(16, 16, 1, palette), 0, 0); } catch (IllegalArgumentException e) { threw = true; }
		check(threw, "a mask of another size throws");
		threw = false;
		try { new Cursor(display, source, mask, 32, 0); } catch (IllegalArgumentException e) { threw = true; }
		check(threw, "a hot spot out of the image throws");
		threw = false;
		try { new Cursor(display, new ImageData(32, 32, 4, new PaletteData(new RGB[] {
				new RGB(0, 0, 0), new RGB(1, 1, 1), new RGB(2, 2, 2), new RGB(3, 3, 3), new RGB(4, 4, 4), new RGB(5, 5, 5),
				new RGB(6, 6, 6), new RGB(7, 7, 7), new RGB(8, 8, 8), new RGB(9, 9, 9), new RGB(10, 10, 10), new RGB(11, 11, 11),
				new RGB(12, 12, 12), new RGB(13, 13, 13), new RGB(14, 14, 14), new RGB(15, 15, 15)})), mask, 0, 0); } catch (IllegalArgumentException e) { threw = true; }
		check(threw, "a source of more than one bit throws");
		threw = false;
		try { new Cursor(display, source, null, 0, 0); } catch (IllegalArgumentException e) { threw = true; }
		check(threw, "without a mask, a source without transparency throws");
	}

	void checkControls() {
		saySubObjective("The pointer of the controls");
		Cursor cross = new Cursor(display, SWT.CURSOR_CROSS);
		Cursor hand = new Cursor(display, SWT.CURSOR_HAND);
		Canvas canvas = new Canvas(shell, SWT.NONE);
		Canvas inner = new Canvas(canvas, SWT.NONE);
		Button button = new Button(shell, SWT.PUSH);
		Text text = new Text(shell, SWT.SINGLE | SWT.BORDER);
		canvas.setBounds(0, 0, 100, 100);
		inner.setBounds(10, 10, 50, 50);
		button.setBounds(0, 110, 80, 28);
		text.setBounds(100, 110, 80, 28);

		check(moveOver(canvas) == sys(OS.SPTR_ARROW), "without cursors a canvas shows the arrow");
		check(moveOver(text) == sys(OS.SPTR_TEXT), "and a text the I-beam of the entry field");
		shell.setCursor(cross);
		check(moveOver(canvas) == cross.handle && moveOver(inner) == cross.handle, "the cursor of the shell is shown over its children");
		canvas.setCursor(hand);
		check(moveOver(canvas) == hand.handle && moveOver(inner) == hand.handle, "the cursor of a child, over it and its children");
		check(moveOver(button) == cross.handle, "a button without a cursor shows the one of its parent");
		text.setCursor(hand);
		check(moveOver(text) == hand.handle, "the cursor of a text replaces the I-beam");
		canvas.setCursor(null);
		check(moveOver(inner) == cross.handle, "without its cursor, the child shows again the one of the parent");
		shell.setCursor(null);
		text.setCursor(null);
		check(moveOver(canvas) == sys(OS.SPTR_ARROW) && moveOver(text) == sys(OS.SPTR_TEXT), "and the default pointers come back");
		boolean threw = false;
		Cursor gone = new Cursor(display, SWT.CURSOR_HAND);
		gone.dispose();
		try { canvas.setCursor(gone); } catch (IllegalArgumentException e) { threw = true; }
		check(threw, "a disposed cursor cannot be set");

		saySubObjective("The pointer of the tracker");
		final Tracker t = new Tracker(shell, SWT.RESIZE);
		t.setRectangles(new Rectangle[] {new Rectangle(10, 10, 50, 50)});
		final int[] seen = new int[1];
		display.timerExec(200, new Runnable() {
			public void run() {
				seen[0] = pointer();
				t.close();
			}
		});
		t.open();
		check(seen[0] == sys(OS.SPTR_MOVE), "a resize tracker shows the pointer to move before it knows the side");
		t.setCursor(hand);
		display.timerExec(200, new Runnable() {
			public void run() {
				seen[0] = pointer();
				t.close();
			}
		});
		t.open();
		check(seen[0] == hand.handle, "with setCursor() it shows that cursor");
		t.dispose();

		canvas.dispose();
		button.dispose();
		text.dispose();
		cross.dispose();
		hand.dispose();
	}

	void initComponents() {
		automatic();
		interactive();
	}

	void automatic() {
		/* The window is not shown during the automatic checks (see SWT012_01) */
		shell.setSize(500, 330);
		sayObjective("Automatic checks");
		checkStyles();
		checkShapes();
		checkImageData();
		checkControls();
		say("\n" + checks + " checks passed.\n");
	}

	void interactive() {
		sayObjective("Interactive: move the pointer over the buttons, the text, the label and the window");
		for (int i = 0; i < STYLES.length; i++) {
			Button b = new Button(shell, SWT.PUSH);
			b.setText(NAMES[i]);
			b.setBounds(10 + (i % 4) * 120, 10 + (i / 4) * 32, 112, 28);
			b.setCursor(new Cursor(display, STYLES[i]));
		}
		Text text = new Text(shell, SWT.SINGLE | SWT.BORDER);
		text.setText("A text with the HAND");
		text.setBounds(10, 210, 230, 28);
		text.setCursor(new Cursor(display, SWT.CURSOR_HAND));
		Label label = new Label(shell, SWT.BORDER);
		label.setText("A label with the CROSS");
		label.setBounds(250, 210, 230, 28);
		label.setCursor(new Cursor(display, SWT.CURSOR_CROSS));
		final Cursor wait = new Cursor(display, SWT.CURSOR_WAIT);
		final Button busy = new Button(shell, SWT.CHECK);
		busy.setText("The window has the WAIT cursor");
		busy.setBounds(10, 250, 300, 28);
		busy.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				shell.setCursor(busy.getSelection() ? wait : null);
			}
		});
	}

	public static void main(String[] args) {
		go(new SWT015_01());
	}

}
