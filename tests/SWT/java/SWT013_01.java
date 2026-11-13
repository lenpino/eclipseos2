/*
 *  SWT013_01.java
 */

/*
 * Copyright (c) 2002, 2026 EclipseOS2 Team.
 * This file is made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 */

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

/**
 *  Validates the DirectoryDialog.
 *
 *  The first part is automatic: the dialog is opened and a task that runs
 *  inside its event loop looks at its widgets and presses its buttons, so
 *  nobody has to touch the window. The second part is interactive: press the
 *  button, choose a directory and see the result.
 *
 *  To close the window automatically (for unattended runs) pass the system
 *  property swt.test.autoclose=<milliseconds> to the JVM.
 */
public class SWT013_01 extends SWTTestCase {

	static {
		STEP = "013";
		TEST = "01";
		DESC = "DirectoryDialog";
	}

	int checks;
	/* Parent of the dialogs of the automatic checks */
	Shell owner;
	String failure;

	Shell createTopShell(Display display) {
		return new Shell(display, SWT.SHELL_TRIM | SWT.NO_REDRAW_RESIZE);
	}

	void check(boolean ok, String what) {
		checks++;
		say((ok ? "  ok    " : "  FAIL  ") + what);
		if (!ok) throw new RuntimeException("Check failed: " + what);
	}

	/** The shell of the dialog that is open: the only one besides the test window. */
	Shell dialogShell(String title) {
		Shell[] shells = display.getShells();
		for (int i = 0; i < shells.length; i++) {
			if (shells[i] != shell && title.equals(shells[i].getText())) return shells[i];
		}
		return null;
	}

	Control find(Control parent, Class type, String text) {
		if (!(parent instanceof org.eclipse.swt.widgets.Composite)) return null;
		Control[] children = ((org.eclipse.swt.widgets.Composite) parent).getChildren();
		for (int i = 0; i < children.length; i++) {
			if (type.isInstance(children[i])) {
				if (text == null) return children[i];
				if (children[i] instanceof Button && text.equals(((Button) children[i]).getText())) return children[i];
			}
		}
		return null;
	}

	void press(Button button) {
		Event event = new Event();
		event.widget = button;
		button.notifyListeners(SWT.Selection, event);
	}

	/**
	 * Opens the dialog and, from inside its loop, runs the task with the dialog's shell.
	 * The dialog is closed if the task fails, and the failure is thrown afterwards.
	 */
	String openAnd(final DirectoryDialog dialog, final Task task) {
		failure = null;
		final String title = dialog.getText();
		display.asyncExec(new Runnable() {
			public void run() {
				Shell d = dialogShell(title);
				try {
					if (d == null) throw new RuntimeException("The dialog is not open");
					task.run(d);
				} catch (Throwable t) {
					failure = t.toString();
					t.printStackTrace();
				}
				if (d != null && !d.isDisposed()) d.dispose();
			}
		});
		String result = dialog.open();
		if (failure != null) throw new RuntimeException(failure);
		return result;
	}

	interface Task {
		void run(Shell d);
	}

	static boolean same(String a, String b) {
		return a != null && b != null && a.equalsIgnoreCase(b);
	}

	void checkApi() {
		saySubObjective("The API");
		final DirectoryDialog dialog = new DirectoryDialog(owner);
		check(dialog.getFilterPath().equals("") && dialog.getMessage().equals("") && dialog.getText().equals(""), "the dialog is empty at first");
		check(dialog.getParent() == owner, "getParent");
		check((dialog.getStyle() & SWT.PRIMARY_MODAL) != 0, "the style is primary modal by default");
		dialog.setFilterPath("C:\\");
		dialog.setMessage("Choose a directory");
		dialog.setText("Directories");
		check(dialog.getFilterPath().equals("C:\\") && dialog.getMessage().equals("Choose a directory") && dialog.getText().equals("Directories"),
				"the setters");
		boolean threw = false;
		try { dialog.setMessage(null); } catch (IllegalArgumentException e) { threw = true; }
		check(threw, "setMessage(null) throws");
		threw = false;
		try { new DirectoryDialog(null); } catch (IllegalArgumentException e) { threw = true; }
		check(threw, "a dialog without a parent throws");
		dialog.setFilterPath(null);
		check(dialog.getFilterPath() == null, "the filter path can be null");
	}

	void checkOpenAndAccept() throws Exception {
		saySubObjective("Open, shows the directory and OK");
		final File here = new File(".").getCanonicalFile();
		final DirectoryDialog dialog = new DirectoryDialog(owner);
		dialog.setText("Pick one");
		dialog.setMessage("The current directory has to be selected");
		dialog.setFilterPath(here.getPath());
		String result = openAnd(dialog, new Task() {
			public void run(Shell d) {
				check(d.getText().equals("Pick one"), "the title of the dialog");
				Label label = (Label) find(d, Label.class, null);
				check(label != null && label.getText().equals("The current directory has to be selected"), "the message is shown");
				Tree tree = (Tree) find(d, Tree.class, null);
				check(tree != null, "the dialog has a tree");
				check(tree.getItemCount() >= 1, "the tree has the drives: " + tree.getItemCount());
				TreeItem[] selection = tree.getSelection();
				check(selection.length == 1 && same(((File) selection[0].getData()).getPath(), here.getPath()),
						"the filter path is selected: " + (selection.length > 0 ? ((File) selection[0].getData()).getPath() : "nothing"));
				TreeItem parent = selection[0].getParentItem();
				check(parent != null && parent.getExpanded(), "and the directories above it are expanded");
				check(selection[0].getParentItem().getItemCount() >= 1, "with their subdirectories");
				press((Button) find(d, Button.class, "OK"));
			}
		});
		check(same(result, here.getPath()), "OK returns the selected directory: " + result);
		check(same(dialog.getFilterPath(), here.getPath()), "and keeps it as the filter path");
	}

	void checkCancel() throws Exception {
		saySubObjective("Cancel");
		final DirectoryDialog dialog = new DirectoryDialog(owner);
		dialog.setText("Cancel it");
		dialog.setFilterPath(new File(".").getCanonicalPath());
		String before = dialog.getFilterPath();
		String result = openAnd(dialog, new Task() {
			public void run(Shell d) {
				press((Button) find(d, Button.class, "Cancel"));
			}
		});
		check(result == null, "Cancel returns null");
		check(dialog.getFilterPath().equals(before), "and does not change the filter path");

		final DirectoryDialog closed = new DirectoryDialog(owner);
		closed.setText("Closed");
		result = openAnd(closed, new Task() {
			public void run(Shell d) {
				d.dispose();
			}
		});
		check(result == null, "closing the window returns null");
	}

	void checkMissingPath() throws Exception {
		saySubObjective("A path that does not exist");
		File top = new File(".").getCanonicalFile();
		while (top.getParentFile() != null) top = top.getParentFile();
		final File drive = top;
		final DirectoryDialog dialog = new DirectoryDialog(owner);
		dialog.setText("No such path");
		dialog.setFilterPath(new File(drive, "no\\such\\directory").getPath());
		String result = openAnd(dialog, new Task() {
			public void run(Shell d) {
				Tree tree = (Tree) find(d, Tree.class, null);
				/* The dialog selects the deepest directory that exists (difference 015) */
				TreeItem[] selection = tree.getSelection();
				check(selection.length == 1 && same(((File) selection[0].getData()).getPath(), drive.getPath()),
						"the deepest directory that exists is selected: " + (selection.length > 0 ? ((File) selection[0].getData()).getPath() : "nothing"));
				press((Button) find(d, Button.class, "OK"));
			}
		});
		check(same(result, drive.getPath()), "OK returns it: " + result);

		final DirectoryDialog gone = new DirectoryDialog(owner);
		gone.setText("No such drive");
		gone.setFilterPath("Y:\\no\\such\\directory");
		result = openAnd(gone, new Task() {
			public void run(Shell d) {
				Tree tree = (Tree) find(d, Tree.class, null);
				check(tree.getSelectionCount() == 1 && tree.getSelection()[0].getData() instanceof File, "a drive that is not there leaves the first item selected");
				press((Button) find(d, Button.class, "Cancel"));
			}
		});
		check(result == null, "and it can be cancelled");
	}

	void checkExpand() throws Exception {
		saySubObjective("The directories are read when they are expanded");
		final File root = new File(".").getCanonicalFile();
		File top = root;
		while (top.getParentFile() != null) top = top.getParentFile();
		final File drive = top;
		final DirectoryDialog dialog = new DirectoryDialog(owner);
		dialog.setText("Expand");
		String result = openAnd(dialog, new Task() {
			public void run(Shell d) {
				Tree tree = (Tree) find(d, Tree.class, null);
				check(tree.getSelectionCount() <= 1, "an empty filter path selects nothing in particular");
				TreeItem item = null;
				TreeItem[] roots = tree.getItems();
				for (int i = 0; i < roots.length; i++) if (same(((File) roots[i].getData()).getPath(), drive.getPath())) item = roots[i];
				check(item != null, "the drive of the current directory is in the tree: " + drive);
				check(item.getItemCount() == 1 && item.getItems()[0].getText().equals("..."), "a drive has a placeholder before it is expanded");
				check(item.getImage() != null, "a drive has a picture");
				Event event = new Event();
				event.item = item;
				tree.notifyListeners(SWT.Expand, event);
				String[] names = drive.list();
				int dirs = 0;
				for (int i = 0; names != null && i < names.length; i++) if (new File(drive, names[i]).isDirectory()) dirs++;
				check(item.getItemCount() == dirs, "expanding it shows its subdirectories: " + item.getItemCount() + " of " + dirs);
				boolean sorted = true, real = true;
				for (int i = 0; i < item.getItemCount(); i++) {
					real &= ((File) item.getItems()[i].getData()).isDirectory();
					if (i > 0) sorted &= item.getItems()[i - 1].getText().compareToIgnoreCase(item.getItems()[i].getText()) <= 0;
				}
				check(real && sorted, "they are directories and sorted");
				boolean pictures = true;
				for (int i = 0; i < item.getItemCount(); i++) pictures &= item.getItems()[i].getImage() != null && item.getItems()[i].getImage() != item.getImage();
				check(pictures, "each directory has the picture of a folder, not the one of a drive");
				if (item.getItemCount() > 0) check(item.getItems()[0].getItemCount() == 1, "each one has its own placeholder");
				tree.notifyListeners(SWT.Expand, event);
				check(item.getItemCount() == dirs, "expanding again does not repeat them");
				tree.setSelection(new TreeItem[] {item});
				press((Button) find(d, Button.class, "OK"));
			}
		});
		check(same(result, drive.getPath()), "a drive can be chosen: " + result);
	}

	void initComponents() {
		try {
			automatic();
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e.toString());
		}
		interactive();
	}

	void automatic() throws Exception {
		/* The window is not shown during the automatic checks (see SWT012_01) */
		shell.setSize(420, 150);
		sayObjective("Automatic checks");
		/* The dialogs are driven without being shown: PM keeps a window that is out of the screen at its corner */
		owner = shell;
		System.setProperty("swt.dialog.invisible", "true");
		try {
			checkApi();
			checkOpenAndAccept();
			checkCancel();
			checkMissingPath();
			checkExpand();
		} finally {
			System.setProperty("swt.dialog.invisible", "false");
		}
		say("\n" + checks + " checks passed.\n");
	}

	void interactive() {
		sayObjective("Interactive: choose a directory, expand some, press OK, Cancel, or Enter on a directory");
		final Label result = new Label(shell, SWT.BORDER);
		final Button choose = new Button(shell, SWT.PUSH);
		choose.setText("Choose a directory...");
		Rectangle area = shell.getClientArea();
		choose.setBounds(10, 10, 180, 28);
		result.setBounds(10, 50, Math.max(area.width - 20, 50), 28);
		result.setText("(nothing chosen yet)");
		choose.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dialog = new DirectoryDialog(shell);
				dialog.setText("Choose a directory");
				dialog.setMessage("Choose the directory to use");
				dialog.setFilterPath(result.getText().startsWith("(") ? "" : result.getText());
				String path = dialog.open();
				say("DirectoryDialog: " + path);
				result.setText(path == null ? "(cancelled)" : path);
			}
		});
	}

	public static void main(String[] args) {
		go(new SWT013_01());
	}

}
