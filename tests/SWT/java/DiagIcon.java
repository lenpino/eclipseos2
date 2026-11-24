import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.*;

/*
 * Diagnostic, not a test (2026-11-23): a shell with the image of the Eclipse window set with
 * Shell.setImage, to see the icon of the title bar (system menu) in a screenshot. Left open for
 * swt.diag.close milliseconds. -Dicon=<file> chooses the gif (default eclipse.gif).
 */
public class DiagIcon {
	public static void main(String[] args) {
		final Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setText("DiagIcon");
		shell.setBounds(40, 40, 400, 150);
		String file = System.getProperty("icon", "eclipse.gif");
		ImageData data = new ImageData(System.getProperty("user.dir") + "/tests/SWT/images/eclipse/" + file);
		Image image = data.transparentPixel != -1 ? new Image(display, data, data.getTransparencyMask()) : new Image(display, data);
		System.out.println("image " + file + " " + data.width + "x" + data.height + " transparent " + data.transparentPixel);
		shell.setImage(image);
		shell.open();
		display.timerExec(Integer.getInteger("swt.diag.close", 30000).intValue(), new Runnable() {
			public void run() { shell.dispose(); }
		});
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) display.sleep();
		}
		display.dispose();
	}
}
