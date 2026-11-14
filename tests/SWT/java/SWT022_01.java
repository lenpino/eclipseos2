/*
 *  SWT022_01.java
 */

/*
 * Copyright (c) 2002, 2026 EclipseOS2 Team.
 * This file is made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 */

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 *  Validates the clipboard: Clipboard, TextTransfer and ByteArrayTransfer.
 *
 *  The first part is automatic: texts and bytes go to the clipboard and back,
 *  also through the text fields of PM, which use the clipboard by themselves.
 *  The second part is interactive: copy a text in another program (the system
 *  editor), paste it with the button, and the other way round.
 *
 *  To close the window automatically (for unattended runs) pass the system
 *  property swt.test.autoclose=<milliseconds> to the JVM.
 */
public class SWT022_01 extends SWTTestCase {

	static {
		STEP = "022";
		TEST = "01";
		DESC = "Clipboard";
	}

	int checks;

	Shell createTopShell(Display display) {
		return new Shell(display, SWT.SHELL_TRIM);
	}

	void check(boolean ok, String what) {
		checks++;
		say((ok ? "  ok    " : "  FAIL  ") + what);
		if (!ok) throw new RuntimeException("Check failed: " + what);
	}

	/* A transfer of bytes with a type of its own, registered by name */
	static class BytesTransfer extends ByteArrayTransfer {
		static final String NAME = "SWT022 bytes";
		static final int ID = registerType(NAME);
		protected int[] getTypeIds() { return new int[] {ID}; }
		protected String[] getTypeNames() { return new String[] {NAME}; }
	}

	boolean throwsIllegal(Runnable r) {
		try { r.run(); } catch (IllegalArgumentException e) { return true; }
		return false;
	}

	boolean contains(String[] names, String name) {
		for (int i = 0; i < names.length; i++) if (names[i].equals(name)) return true;
		return false;
	}

	String list(String[] names) {
		String result = "";
		for (int i = 0; i < names.length; i++) result += (i > 0 ? ", " : "") + names[i];
		return result;
	}

	void checkText() {
		saySubObjective("Texts");
		final Clipboard clipboard = new Clipboard(display);
		TextTransfer text = TextTransfer.getInstance();
		check(TextTransfer.getInstance() == text, "TextTransfer is a singleton");
		clipboard.setContents(new Object[] {"Hello, OS/2"}, new Transfer[] {text});
		check("Hello, OS/2".equals(clipboard.getContents(text)), "a text goes and comes back: " + clipboard.getContents(text));
		check(contains(clipboard.getAvailableTypeNames(), "CF_TEXT"), "the clipboard has CF_TEXT: " + list(clipboard.getAvailableTypeNames()));
		String lines = "first line\r\nsecond line\r\n\tthird";
		clipboard.setContents(new Object[] {lines}, new Transfer[] {text});
		check(lines.equals(clipboard.getContents(text)), "several lines and a tab");
		String accents = "España, árbol, niño";
		clipboard.setContents(new Object[] {accents}, new Transfer[] {text});
		check(accents.equals(clipboard.getContents(text)), "letters of the code page: " + clipboard.getContents(text));
		clipboard.setContents(new Object[] {""}, new Transfer[] {text});
		check("".equals(clipboard.getContents(text)), "an empty text");
		check(throwsIllegal(new Runnable() { public void run() { clipboard.setContents(null, new Transfer[0]); } }), "null data throws");
		check(throwsIllegal(new Runnable() { public void run() { clipboard.setContents(new Object[] {"a", "b"}, new Transfer[] {TextTransfer.getInstance()}); } }), "data and types of different lengths throw");
		check(throwsIllegal(new Runnable() { public void run() { clipboard.getContents(null); } }), "getContents(null) throws");
		clipboard.dispose();
	}

	void checkBytes() {
		saySubObjective("Bytes of a registered type");
		Clipboard clipboard = new Clipboard(display);
		check(BytesTransfer.ID != 0 && Transfer.registerType(BytesTransfer.NAME) == BytesTransfer.ID, "registerType gives a type, the same one again: " + BytesTransfer.ID);
		check(Transfer.registerType("SWT022 other") != BytesTransfer.ID, "and another one for another name");
		BytesTransfer bytes = new BytesTransfer();
		byte[] data = {0, 1, 2, (byte) 255, 0, 42};
		clipboard.setContents(new Object[] {data, "with text"}, new Transfer[] {bytes, TextTransfer.getInstance()});
		byte[] back = (byte[]) clipboard.getContents(bytes);
		check(back != null && java.util.Arrays.equals(back, data), "bytes with zeros go and come back: " + (back == null ? "null" : back.length + " bytes"));
		check("with text".equals(clipboard.getContents(TextTransfer.getInstance())), "and the text set with them");
		String[] names = clipboard.getAvailableTypeNames();
		check(contains(names, "CF_TEXT") && contains(names, BytesTransfer.NAME), "both types are in the clipboard: " + list(names));
		clipboard.setContents(new Object[] {"only text"}, new Transfer[] {TextTransfer.getInstance()});
		check(clipboard.getContents(bytes) == null, "setContents clears the other types");
		TransferData type = bytes.getSupportedTypes()[0];
		check(bytes.isSupportedType(type) && !TextTransfer.getInstance().isSupportedType(type), "isSupportedType");
		clipboard.dispose();
	}

	void checkTextFields() {
		saySubObjective("With the text fields of PM");
		Clipboard clipboard = new Clipboard(display);
		Shell window = new Shell(display);
		Text single = new Text(window, SWT.SINGLE | SWT.BORDER);
		single.setBounds(0, 0, 200, 24);
		Text multi = new Text(window, SWT.MULTI | SWT.BORDER);
		multi.setBounds(0, 30, 200, 80);
		clipboard.setContents(new Object[] {"pasted by PM"}, new Transfer[] {TextTransfer.getInstance()});
		single.paste();
		check(single.getText().equals("pasted by PM"), "a text field pastes what the clipboard was given: " + single.getText());
		single.setText("copied by PM");
		single.selectAll();
		check(single.getSelectionCount() == 12, "selectAll selects all the text of a text field: " + single.getSelection());
		single.copy();
		check("copied by PM".equals(clipboard.getContents(TextTransfer.getInstance())), "and the clipboard gives what a text field copied: " + clipboard.getContents(TextTransfer.getInstance()));
		multi.setText("one\r\ntwo");
		multi.selectAll();
		check(multi.getSelectionCount() == multi.getCharCount() && multi.getCharCount() > 0, "and of a multi line one: " + multi.getSelection() + " of " + multi.getCharCount());
		multi.copy();
		String copied = (String) clipboard.getContents(TextTransfer.getInstance());
		check(copied != null && copied.indexOf("one") == 0 && copied.indexOf("two") > 3, "several lines copied by a text field: " + copied);
		window.dispose();
		clipboard.dispose();
	}

	void initComponents() {
		sayObjective("Automatic checks");
		checkText();
		checkBytes();
		checkTextFields();
		say("\n" + checks + " checks passed.\n");

		sayObjective("Interactive: copy a text in another program and press Paste; press Copy and paste it in another program");
		shell.setLayout(new GridLayout(2, false));
		final Text field = new Text(shell, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);
		GridData data = new GridData(GridData.FILL_BOTH);
		data.horizontalSpan = 2;
		field.setLayoutData(data);
		field.setText("Some text to copy");
		final Clipboard clipboard = new Clipboard(display);
		Button copy = new Button(shell, SWT.PUSH);
		copy.setText("Copy (Clipboard)");
		copy.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				clipboard.setContents(new Object[] {field.getText()}, new Transfer[] {TextTransfer.getInstance()});
				say("Copied: " + field.getText());
			}
		});
		Button paste = new Button(shell, SWT.PUSH);
		paste.setText("Paste (Clipboard)");
		paste.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Object text = clipboard.getContents(TextTransfer.getInstance());
				say("Pasted: " + text + " (types: " + list(clipboard.getAvailableTypeNames()) + ")");
				if (text != null) field.setText((String) text);
			}
		});
		shell.setSize(360, 240);
	}

	void setTitle(String title) {
		super.setTitle(title);
	}

	public static void main(String[] args) {
		go(new SWT022_01());
	}

}
