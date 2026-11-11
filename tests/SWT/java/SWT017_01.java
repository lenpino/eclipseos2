/*
 *  SWT017_01.java
 */

/*
 * Copyright (c) 2002, 2026 EclipseOS2 Team.
 * This file is made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 */

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.pm.CURSORINFO;
import org.eclipse.swt.internal.pm.OS;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Caret;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 *  Validates the Caret.
 *
 *  The first part is automatic: the window is shown, the canvas gets the
 *  focus, and the text cursor of PM is read with WinQueryCursorInfo after
 *  each change. The second part is interactive: type in the canvas, move
 *  with the arrows, and move the focus to the text and back.
 *
 *  To close the window automatically (for unattended runs) pass the system
 *  property swt.test.autoclose=<milliseconds> to the JVM.
 */
public class SWT017_01 extends SWTTestCase {

	static {
		STEP = "017";
		TEST = "01";
		DESC = "Caret";
	}

	int checks;
	boolean skipped;
	Canvas canvas;
	Button other;

	Shell createTopShell(Display display) {
		return new Shell(display, SWT.SHELL_TRIM | SWT.NO_REDRAW_RESIZE);
	}

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

	/** The cursor of PM in SWT terms ("x,y wxh" in the canvas), or "none" when the canvas has none. */
	String cursor() {
		CURSORINFO info = new CURSORINFO();
		if (!OS.WinQueryCursorInfo(OS.HWND_DESKTOP, info) || info.hwnd != canvas.handle) return "none";
		/* The y of PM is the lower edge, from the bottom of the window */
		int height = canvas.getSize().y;
		return info.x + "," + (height - info.y - info.cy) + " " + info.cx + "x" + info.cy;
	}

	void checkApi() {
		saySubObjective("The API");
		Caret c = new Caret(canvas, SWT.NONE);
		check(canvas.getCaret() == c && c.getParent() == canvas, "the first caret of a canvas becomes its caret");
		Caret second = new Caret(canvas, SWT.NONE);
		check(canvas.getCaret() == c, "a second one does not replace it");
		second.dispose();
		check(canvas.getCaret() == c, "and disposing it changes nothing");
		c.setBounds(10, 20, 2, 15);
		check(c.getBounds().equals(new Rectangle(10, 20, 2, 15)) && c.getLocation().equals(new Point(10, 20)) && c.getSize().equals(new Point(2, 15)), "bounds, location and size");
		check(c.getVisible() && !c.isVisible(), "visible, but not shown without the focus");
		check(c.getFont().equals(canvas.getFont()), "the font is the one of the canvas by default");
		Image image = new Image(display, 8, 12);
		c.setImage(image);
		check(c.getImage() == image && c.getSize().equals(new Point(8, 12)) && c.getBounds().equals(new Rectangle(10, 20, 8, 12)), "with an image the size is the one of the image");
		c.setImage(null);
		image.dispose();
		boolean threw = false;
		try { c.setImage(image); } catch (IllegalArgumentException e) { threw = true; }
		check(threw, "a disposed image throws");
		Font font = new Font(display, "Helv", 10, SWT.NORMAL);
		font.dispose();
		threw = false;
		try { c.setFont(font); } catch (IllegalArgumentException e) { threw = true; }
		check(threw, "a disposed font throws");
		c.dispose();
		check(canvas.getCaret() == null, "disposing the caret of the canvas leaves it without one");
		threw = false;
		try { c.setVisible(true); } catch (SWTException e) { threw = true; }
		check(threw, "a disposed caret throws");
	}

	void checkFocus() {
		saySubObjective("The text cursor of PM");
		shell.setLocation(40, 40);
		shell.open();
		Caret c = new Caret(canvas, SWT.NONE);
		c.setBounds(10, 20, 2, 15);
		canvas.setFocus();
		run(200);
		if (!canvas.isFocusControl()) {
			/*
			 * PM does not let a program take the focus when it was started from the
			 * SSH session of the VM (WinSetFocus fails and no window is active).
			 */
			say("  SKIPPED  the program cannot take the focus (started from SSH?): run it from a console of the VM");
			skipped = true;
			c.dispose();
			shell.setVisible(false);
			run(100);
			return;
		}
		check(c.isVisible(), "with the focus the caret is shown");
		check(cursor().equals("10,20 2x15"), "the canvas has a cursor of PM where the caret is: " + cursor());
		c.setLocation(30, 5);
		check(cursor().equals("30,5 2x15"), "setLocation moves it: " + cursor());
		c.setSize(3, 20);
		check(cursor().equals("30,5 3x20"), "setSize changes it: " + cursor());
		c.setBounds(0, 0, 1, 10);
		check(cursor().equals("0,0 1x10"), "setBounds: " + cursor());
		Image image = new Image(display, 6, 9);
		c.setImage(image);
		check(cursor().equals("0,0 6x9"), "an image gives its size (PM draws no image): " + cursor());
		c.setImage(null);
		image.dispose();

		other.setFocus();
		run(200);
		check(cursor().equals("none") && !c.isVisible(), "without the focus the cursor is destroyed: " + cursor());
		c.setLocation(40, 10);
		check(cursor().equals("none"), "and moving the caret does not create it");
		canvas.setFocus();
		run(200);
		check(cursor().equals("40,10 1x10"), "with the focus again it comes back where the caret is now: " + cursor());

		c.setVisible(false);
		check(!c.isVisible() && cursor().equals("40,10 1x10"), "hidden, the cursor stays (WinShowCursor hides it)");
		c.setLocation(50, 10);
		c.setVisible(true);
		check(cursor().equals("50,10 1x10"), "shown again where it was moved meanwhile: " + cursor());

		canvas.setCaret(null);
		check(cursor().equals("none"), "setCaret(null) destroys it");
		canvas.setCaret(c);
		check(cursor().equals("50,10 1x10"), "setCaret() creates it again");
		c.dispose();
		check(cursor().equals("none") && canvas.getCaret() == null, "and disposing it destroys it");
		shell.setVisible(false);
		run(100);
	}

	void initComponents() {
		automatic();
		interactive();
	}

	void automatic() {
		shell.setSize(460, 330);
		sayObjective("Automatic checks");
		canvas = new Canvas(shell, SWT.NONE);
		canvas.setBounds(10, 10, 200, 100);
		other = new Button(shell, SWT.PUSH);
		other.setBounds(220, 10, 80, 28);
		checkApi();
		checkFocus();
		canvas.dispose();
		other.dispose();
		say("\n" + checks + " checks passed." + (skipped ? " The checks of the focus were SKIPPED." : "") + "\n");
	}

	void interactive() {
		sayObjective("Interactive: type in the white box, use the arrows and Backspace, go to the text with the mouse and come back");
		final StringBuffer text = new StringBuffer("Text: ");
		final int[] position = new int[] {text.length()};
		/*
		 * The box paints its own background (NO_BACKGROUND) and each key repaints
		 * only from the changed letter on: repainting all of it after every key
		 * (erasing first) made the text flicker.
		 */
		final Canvas box = new Canvas(shell, SWT.BORDER | SWT.NO_BACKGROUND);
		box.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		box.setBounds(10, 10, 300, 40);
		final Caret caret = new Caret(box, SWT.NONE);
		final Runnable place = new Runnable() {
			public void run() {
				GC gc = new GC(box);
				int x = 4 + gc.stringExtent(text.substring(0, position[0])).x;
				int height = gc.getFontMetrics().getHeight();
				gc.dispose();
				caret.setBounds(x, 6, caret.getSize().x == 0 ? 2 : caret.getSize().x, height);
			}
		};
		box.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				e.gc.fillRectangle(e.x, e.y, e.width, e.height);
				e.gc.drawString(text.toString(), 4, 6);
			}
		});
		box.addListener(SWT.KeyDown, new Listener() {
			public void handleEvent(Event e) {
				boolean changed = true;
				if (e.keyCode == SWT.ARROW_LEFT && position[0] > 0) { position[0]--; changed = false; }
				else if (e.keyCode == SWT.ARROW_RIGHT && position[0] < text.length()) { position[0]++; changed = false; }
				else if (e.character == SWT.BS && position[0] > 0) text.deleteCharAt(--position[0]);
				else if (e.character >= ' ') text.insert(position[0]++, e.character);
				else return;
				int from = caret.getLocation().x;
				place.run();
				if (!changed) return;
				/* Only from the first letter that changed to the end */
				from = Math.min(from, caret.getLocation().x) - 1;
				Rectangle area = box.getClientArea();
				box.redraw(from, 0, area.width - from, area.height, false);
			}
		});
		box.addListener(SWT.MouseDown, new Listener() {
			public void handleEvent(Event e) {
				box.setFocus();
			}
		});
		place.run();
		Text field = new Text(shell, SWT.SINGLE | SWT.BORDER);
		field.setBounds(10, 60, 300, 28);
		field.setText("Click here: the caret of the box goes away");
		final Button visible = new Button(shell, SWT.CHECK);
		visible.setText("The caret is visible");
		visible.setSelection(true);
		visible.setBounds(10, 100, 200, 28);
		visible.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				caret.setVisible(visible.getSelection());
				box.setFocus();
			}
		});
		final Button wide = new Button(shell, SWT.CHECK);
		wide.setText("A wide caret (10 pixels)");
		wide.setBounds(10, 135, 200, 28);
		wide.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Rectangle r = caret.getBounds();
				caret.setBounds(r.x, r.y, wide.getSelection() ? 10 : 2, r.height);
				box.setFocus();
			}
		});
		box.setFocus();
	}

	public static void main(String[] args) {
		go(new SWT017_01());
	}

}
