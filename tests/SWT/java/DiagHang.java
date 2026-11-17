import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;

/*
 * Diagnostic, not a test: the user interface stays away from its loop in two ways the watchdog
 * must see without the clock (it runs at a fifth of the real speed in the VM): first many calls
 * to checkWidget, then arrays that grow a little each time with one measure of text each, as
 * the loop of Eclipse does, then the same arrays with no call to SWT. Run with -Dswt.watchdog=5000 [-Dswt.watchdog.stop=true].
 */
public class DiagHang {
	public static void main(String[] args) {
		final Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setBounds(20, 20, 200, 100);
		shell.open();
		display.asyncExec(new Runnable() {
			public void run() {
				try {
					System.out.println("calls: 3000000 checkWidget");
					for (int i = 0; i < 3000000; i++) shell.getText();
					System.out.println("calls ended normally");
				} catch (Error e) {
					System.out.println("calls stopped: " + e.getMessage());
				}
			}
		});
		display.asyncExec(new Runnable() {
			public void run() {
				GC gc = new GC(shell);
				try {
					System.out.println("growth: arrays 36 bytes larger each time");
					int size = 200000;
					for (int i = 0; i < 12000; i++) {
						char [] chars = new char [size / 2];
						size += 36;
						gc.textExtent("x" + chars.length);
					}
					System.out.println("growth ended normally");
				} catch (Error e) {
					System.out.println("growth stopped: " + e.getMessage());
				} finally {
					gc.dispose();
				}
			}
		});
		display.asyncExec(new Runnable() {
			public void run() {
				try {
					System.out.println("pure: arrays, no call to SWT");
					int size = 200000;
					long sum = 0;
					for (int i = 0; i < 8000; i++) {
						char [] chars = new char [size / 2];
						size += 36;
						sum += chars.length;
					}
					System.out.println("pure ended normally " + sum);
				} catch (Error e) {
					System.out.println("pure stopped: " + e.getMessage());
				}
				shell.dispose();
			}
		});
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) display.sleep();
		}
		display.dispose();
		System.out.println("done");
	}
}
