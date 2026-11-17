import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.pm.OS;
import org.eclipse.swt.widgets.*;

/*
 * Diagnostic, not a test (2026-11-17): what PM says about a submenu after MenuItem.setMenu (null),
 * as Menu.releaseChild does it when the submenu is disposed. Menu.releaseWidget then calls
 * getItems, which loops until MM_ITEMIDFROMPOSITION answers MIT_ERROR (-1): if the window of the
 * submenu is gone it answers 0 for ever (the hang of Eclipse, seen with hprof). Never disposes
 * the menus, so it cannot hang; ends with System.exit.
 */
public class DiagMenu {
	static void show(String when, int hwnd) {
		System.out.println(when + ": WinIsWindow " + OS.WinIsWindow(0, hwnd)
			+ ", MM_QUERYITEMCOUNT " + OS.WinSendMsg(hwnd, OS.MM_QUERYITEMCOUNT, 0, 0)
			+ ", MM_ITEMIDFROMPOSITION 0 -> " + OS.WinSendMsg(hwnd, OS.MM_ITEMIDFROMPOSITION, 0, 0)
			+ ", 99 -> " + OS.WinSendMsg(hwnd, OS.MM_ITEMIDFROMPOSITION, 99, 0));
	}
	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		Menu bar = new Menu(shell, SWT.BAR);
		shell.setMenuBar(bar);
		MenuItem cascade = new MenuItem(bar, SWT.CASCADE);
		cascade.setText("File");
		Menu sub = new Menu(shell, SWT.DROP_DOWN);
		cascade.setMenu(sub);
		new MenuItem(sub, SWT.PUSH).setText("One");
		new MenuItem(sub, SWT.PUSH).setText("Two");
		int hwnd = sub.handle;
		show("with the submenu", hwnd);
		System.out.println("getItems before: " + sub.getItems().length + " items");
		cascade.setMenu(null);
		show("after setMenu (null)", hwnd);
		System.exit(0);
	}
}
