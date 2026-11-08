/*
 *  SWT009_18.java
 */

/*
 * Copyright (c) 2002, 2026 EclipseOS2 Team.
 * This file is made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 */

import org.eclipse.swt.internal.pm.OS;

/**
 *  Checks the PM message parameter macros of the OS class. MPFROM2SHORT()
 *  used to add its first argument to the shifted second one, so a negative
 *  first short, which is sign extended, changed the high word.
 *
 *  The test is automatic: any failed check throws an exception.
 */
public class SWT009_18 extends SWTTestCase {

	static {
		STEP = "018";
		TEST = "09";
		DESC = "PM message macros";
	}

	int failures;

	void check(boolean ok, String what) {
		say((ok ? "  ok    " : "  FAIL  ") + what);
		if (!ok) failures++;
	}

	void checkPair(short s1, short s2) {
		int mp = OS.MPFROM2SHORT(s1, s2);
		check(OS.SHORT1FROMMP(mp) == s1 && OS.SHORT2FROMMP(mp) == s2,
				"MPFROM2SHORT(" + s1 + ", " + s2 + ") = 0x" + Integer.toHexString(mp)
				+ " gives back " + OS.SHORT1FROMMP(mp) + ", " + OS.SHORT2FROMMP(mp));
	}

	void initComponents() {
		check(OS.MPFROM2SHORT((short) 100, (short) 1) == 0x00010064, "MPFROM2SHORT(100, 1) = 0x00010064");
		check(OS.MPFROM2SHORT((short) -1, (short) 5) == 0x0005FFFF, "MPFROM2SHORT(-1, 5) = 0x0005FFFF");
		check(OS.MPFROM2SHORT((short) 1, (short) -1) == 0xFFFF0001, "MPFROM2SHORT(1, -1) = 0xFFFF0001");
		check(OS.MPFROM2SHORT((short) -1, (short) -1) == 0xFFFFFFFF, "MPFROM2SHORT(-1, -1) = 0xFFFFFFFF");
		check(OS.MPFROM2SHORT((short) 0x8000, (short) 0x7FFF) == 0x7FFF8000, "MPFROM2SHORT(0x8000, 0x7FFF) = 0x7FFF8000");
		checkPair((short) 0, (short) 0);
		checkPair((short) -32768, (short) 32767);
		checkPair((short) -2, (short) -300);
		checkPair((short) 1234, (short) -4321);

		say("");
		if (failures > 0) throw new RuntimeException(failures + " check(s) failed");
		say("All checks passed.\n");
		display.asyncExec(new Runnable() {
			public void run() { shell.dispose(); }
		});
	}

	public static void main(String[] args) {
		go(new SWT009_18());
	}

}
