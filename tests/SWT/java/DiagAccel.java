import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;

/*
 * Diagnostic, not a test (2026-11-23): a menu with F1..F12 accelerators; prints the items selected by
 * real keys (send them from the host with VBoxManage controlvm keyboardputscancode). Ends after
 * swt.diag.close milliseconds.
 */
public class DiagAccel {
	public static void main(String[] args) {
		final Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setText("DiagAccel");
		shell.setBounds(40, 40, 400, 200);
		Menu bar = new Menu(shell, SWT.BAR);
		shell.setMenuBar(bar);
		MenuItem file = new MenuItem(bar, SWT.CASCADE);
		file.setText("&File");
		Menu menu = new Menu(shell, SWT.DROP_DOWN);
		file.setMenu(menu);
		int[] keys = {SWT.F1, SWT.F2, SWT.F3, SWT.F4, SWT.F5, SWT.F6, SWT.F7, SWT.F8, SWT.F9, SWT.F10, SWT.F11, SWT.F12};
		for (int k = 0; k < keys.length; k++) {
			final int n = k + 1;
			MenuItem item = new MenuItem(menu, SWT.PUSH);
			item.setText("Item F" + n + "\tF" + n);
			item.setAccelerator(keys[k]);
			item.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event e) { System.out.println("SELECTED F" + n); }
			});
		}
		shell.open();
		display.timerExec(Integer.getInteger("swt.diag.close", 40000).intValue(), new Runnable() {
			public void run() { shell.dispose(); }
		});
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) display.sleep();
		}
		display.dispose();
	}
}
