import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;

/* Diagnostic, not a test (2026-11-17): a tree with icons of Eclipse left open for a screenshot */
public class DiagTree {
	public static void main(String[] args) {
		Display display = new Display();
		String dir = System.getProperty("user.dir") + "/tests/SWT/images/eclipse/";
		ImageData data = new ImageData(dir + "package_obj.gif");
		Image bitmap = new Image(display, data), icon = new Image(display, data, data.getTransparencyMask());
		final Shell shell = new Shell(display);
		shell.setText("DiagTree");
		shell.setBounds(40, 40, 260, 200);
		Tree tree = new Tree(shell, SWT.NONE);
		tree.setBounds(0, 0, 250, 170);
		TreeItem a = new TreeItem(tree, SWT.NONE);
		a.setText("bitmap");
		a.setImage(bitmap);
		TreeItem b = new TreeItem(tree, SWT.NONE);
		b.setText("icon");
		b.setImage(icon);
		shell.open();
		display.timerExec(20000, new Runnable() { public void run() { shell.dispose(); } });
		while (!shell.isDisposed()) if (!display.readAndDispatch()) display.sleep();
		display.dispose();
	}
}
