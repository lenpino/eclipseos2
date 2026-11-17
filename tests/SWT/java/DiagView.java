import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.*;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.*;

/*
 * Diagnostic, not a test (2026-11-17): the structure of the views of Eclipse (a ViewForm with a
 * tree as content, a table with columns below, a StyledText as the Welcome, in a SashForm), laid
 * out after the shell opens and maximized 3 s later,
 * on a black shell so that what is not painted shows. Left open for a screenshot of the VM;
 * ends after swt.diag.close milliseconds.
 */
public class DiagView {
	public static void main(String[] args) {
		final Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setText("DiagView");
		shell.setBackground(display.getSystemColor(SWT.COLOR_BLACK));
		shell.setLayout(new FillLayout());
		shell.setBounds(40, 40, 700, 500);
		shell.open();
		while (display.readAndDispatch()) {}
		/* As the workbench: the parts are created after the window is open */
		SashForm sash = new SashForm(shell, SWT.VERTICAL);
		ViewForm form = new ViewForm(sash, SWT.BORDER | SWT.FLAT);
		CLabel title = new CLabel(form, SWT.NONE);
		title.setText("Navigator");
		form.setTopLeft(title);
		Tree tree = new Tree(form, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		for (int i = 0; i < 5; i++) new TreeItem(tree, SWT.NONE).setText("Item " + i);
		form.setContent(tree);
		ViewForm form2 = new ViewForm(sash, SWT.BORDER | SWT.FLAT);
		Table table = new Table(form2, SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		String[] names = {"!", "Description", "Resource", "In Folder"};
		int[] widths = {20, 300, 100, 120};
		for (int i = 0; i < names.length; i++) {
			TableColumn column = new TableColumn(table, SWT.LEFT);
			column.setText(names[i]);
			column.setWidth(widths[i]);
		}
		form2.setContent(table);
		StyledText text = new StyledText(sash, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.WRAP | SWT.READ_ONLY);
		text.setText("This page will help familiarize you with the Eclipse Workbench.\nTo get started, read the sections below.\nThird line of the welcome text.");
		final Control[] all = {tree, table, text};
		final Listener resize = new Listener() {
			public void handleEvent(Event e) {
				System.out.println("Resize of " + e.widget + " to " + ((Control)e.widget).getBounds());
			}
		};
		for (int i = 0; i < all.length; i++) all[i].addListener(SWT.Resize, resize);
		shell.layout(true);
		System.out.println("tree bounds " + tree.getBounds() + ", form client " + form.getClientArea());
		System.out.println("table bounds " + table.getBounds() + ", form2 client " + form2.getClientArea());
		display.timerExec(3000, new Runnable() {
			public void run() {
				System.out.println("maximizing");
				shell.setMaximized(true);
			}
		});
		display.timerExec(Integer.getInteger("swt.diag.close", 20000).intValue(), new Runnable() {
			public void run() { shell.dispose(); }
		});
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) display.sleep();
		}
		display.dispose();
	}
}
