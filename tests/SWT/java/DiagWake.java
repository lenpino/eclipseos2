import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;

/*
 * Diagnostic, not a test: other threads call asyncExec (Display.wake, a call to PM) without stop
 * while the user interface makes garbage, so that the collector of the JVM runs often. Prints a
 * line every second; if it stops printing, the program hangs.
 */
public class DiagWake {
	static volatile int runs;
	public static void main(String[] args) {
		final Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setText("DiagWake");
		shell.setBounds(20, 20, 300, 100);
		shell.open();
		final long end = System.currentTimeMillis() + 30000;
		for (int t = 0; t < 3; t++) {
			Thread thread = new Thread() {
				public void run() {
					while (System.currentTimeMillis() < end && !display.isDisposed()) {
						try {
							display.asyncExec(new Runnable() {
								public void run() { runs++; }
							});
						} catch (Exception e) { return; }
						Thread.yield();
					}
				}
			};
			thread.setDaemon(true);
			thread.start();
		}
		long next = System.currentTimeMillis() + 1000;
		int second = 0;
		while (System.currentTimeMillis() < end) {
			Object [] garbage = new Object [1000];
			for (int i = 0; i < garbage.length; i++) garbage [i] = new int [100];
			while (display.readAndDispatch()) {}
			if (System.currentTimeMillis() >= next) {
				next += 1000;
				System.out.println("second " + (++second) + ", runs " + runs);
			}
		}
		display.dispose();
		System.out.println("done, no hang");
	}
}
