import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.pm.OS;
import org.eclipse.swt.widgets.*;

/*
 * Diagnostic, not a test (2026-11-17): the system colors of the titles, and what
 * fillGradientRectangle and a CLabel with the gradient of an active view of Eclipse paint,
 * read from the screen.
 */
public class DiagGradient {
	static Display display;
	static int pel(Control control, int x, int y) {
		Point p = control.toDisplay(new Point(x, y));
		int hps = OS.WinGetScreenPS(OS.HWND_DESKTOP);
		OS.GpiCreateLogColorTable(hps, 0, OS.LCOLF_RGB, 0, 0, null);
		int pel = OS.GpiQueryPel(hps, new int[] {p.x, OS.WinQuerySysValue(OS.HWND_DESKTOP, OS.SV_CYSCREEN) - 1 - p.y}) & 0xFFFFFF;
		OS.WinReleasePS(hps);
		return pel;
	}
	static String row(Control c, int y, int width) {
		StringBuffer b = new StringBuffer();
		for (int x = 0; x < width; x += width / 10) b.append(Integer.toHexString(pel(c, x, y))).append(' ');
		return b.toString();
	}
	public static void main(String[] args) {
		display = new Display();
		String[] names = {"TITLE_FOREGROUND", "TITLE_BACKGROUND", "TITLE_BACKGROUND_GRADIENT", "TITLE_INACTIVE_FOREGROUND", "TITLE_INACTIVE_BACKGROUND", "TITLE_INACTIVE_BACKGROUND_GRADIENT", "WIDGET_BACKGROUND"};
		int[] ids = {SWT.COLOR_TITLE_FOREGROUND, SWT.COLOR_TITLE_BACKGROUND, SWT.COLOR_TITLE_BACKGROUND_GRADIENT, SWT.COLOR_TITLE_INACTIVE_FOREGROUND, SWT.COLOR_TITLE_INACTIVE_BACKGROUND, SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT, SWT.COLOR_WIDGET_BACKGROUND};
		for (int i = 0; i < ids.length; i++) System.out.println(names[i] + " " + display.getSystemColor(ids[i]).getRGB());
		final Shell shell = new Shell(display, SWT.NO_TRIM);
		shell.setBounds(20, 20, 400, 120);
		Canvas canvas = new Canvas(shell, SWT.NO_BACKGROUND);
		canvas.setBounds(0, 0, 400, 60);
		canvas.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				e.gc.setBackground(display.getSystemColor(SWT.COLOR_BLACK));
				e.gc.fillRectangle(0, 0, 400, 60);
				e.gc.setForeground(display.getSystemColor(SWT.COLOR_RED));
				e.gc.setBackground(display.getSystemColor(SWT.COLOR_BLUE));
				e.gc.fillGradientRectangle(0, 0, 400, 20, false);
				e.gc.setForeground(display.getSystemColor(SWT.COLOR_GREEN));
				e.gc.setBackground(display.getSystemColor(SWT.COLOR_GREEN));
				e.gc.fillGradientRectangle(0, 30, 400, 20, false);
			}
		});
		CLabel label = new CLabel(shell, SWT.NONE);
		label.setText("Package Explorer");
		label.setBounds(0, 70, 400, 30);
		label.setBackground(new Color[] {display.getSystemColor(SWT.COLOR_TITLE_BACKGROUND), display.getSystemColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT), display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND)}, new int[] {50, 100});
		shell.open();
		for (int i = 0; i < 50; i++) while (display.readAndDispatch()) {}
		canvas.update(); label.update();
		for (int i = 0; i < 50; i++) while (display.readAndDispatch()) {}
		System.out.println("red to blue:   " + row(canvas, 10, 400));
		System.out.println("green to green: " + row(canvas, 40, 400));
		System.out.println("CLabel:        " + row(label, 3, 400));
		shell.dispose();
		display.dispose();
	}
}
