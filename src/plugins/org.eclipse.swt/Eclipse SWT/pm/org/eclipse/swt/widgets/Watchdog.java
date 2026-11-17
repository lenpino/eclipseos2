package org.eclipse.swt.widgets;

/*
 * OS/2 version.
 * Copyright (c) 2002, 2026 EclipseOS2 Team.
 * This file is made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 */

/*
 * A help to find where the user interface hangs (2026-11-15), off unless the system property
 * swt.watchdog gives a number of milliseconds. The thread of the user interface leaves a mark
 * when it goes into a message of a window or an event, and takes it away when it comes out; a
 * thread of its own writes the marks to System.err when the user interface has not come back
 * to its loop (readAndDispatch) for that long and is not waiting for messages (sleep), and
 * also when it waits for messages that long, with the threads of Java that are alive. With
 * swt.watchdog.stop=true the thread of the user interface is stopped (Thread.stop) when it has
 * not come back for three times that long, and is not waiting: the error is thrown where it is, and its stack trace
 * tells it (Eclipse writes it to its log). With swt.watchdog.file the reports go to that file
 * instead of System.err, which may be redirected or locked by the thread that hangs. With
 * swt.watchdog.trace=true it also writes every second the time, the marks and the threads: the
 * last of those lines tells when a program that freezes whole (all its threads) stopped.
 *
 * A busy user interface (raised by PM) may starve the thread of the watchdog, so the user
 * interface also checks itself in Widget.checkWidget and in the text methods of GC: when it has
 * called check swt.watchdog.calls times (200000), or has seen swt.watchdog.gcs garbage
 * collections (100), without coming back to its loop, it writes its marks and its own stack, and
 * again each time one of the two counts doubles. Counts, not time: the clock of the virtual
 * machine runs at a fifth of the real speed (VirtualBox gives up catching up). With
 * swt.watchdog.stop=true it throws an error from where it is at sixteen times either count.
 * Public only for GC.
 */
public final class Watchdog extends Thread {
	static final int LIMIT = Integer.getInteger ("swt.watchdog", 0).intValue ();
	public static final boolean ON = LIMIT > 0;
	static final boolean STOP = ON && Boolean.getBoolean ("swt.watchdog.stop");
	static final boolean TRACE = ON && Boolean.getBoolean ("swt.watchdog.trace");
	/* swt.watchdog.dump: a file; creating <file>.go writes the windows of PM into it */
	static final String DUMP = ON ? System.getProperty ("swt.watchdog.dump") : null;
	static Display display;
	/* swt.watchdog.exit: milliseconds of the clock away from the loop before System.exit */
	static final int EXIT = ON ? Integer.getInteger ("swt.watchdog.exit", 0).intValue () : 0;
	static final String [] marks = new String [64];
	static int depth;
	static volatile long lastLoop = System.currentTimeMillis ();
	static volatile boolean waiting;
	static Watchdog thread;
	static Thread userInterface;
	static java.io.PrintStream out = System.err;
	static final int FIRST_CALLS = Integer.getInteger ("swt.watchdog.calls", 200000).intValue ();
	static final int FIRST_GCS = Integer.getInteger ("swt.watchdog.gcs", 100).intValue ();
	static final int STOP_CALLS = 16 * FIRST_CALLS, STOP_GCS = 16 * FIRST_GCS;
	static int calls, gcs, reports, nextCalls = FIRST_CALLS, nextGcs = FIRST_GCS;
	static boolean thrown;
	static volatile int loops;
	/* Cleared by the collector: the user interface counts collections without the clock */
	static java.lang.ref.WeakReference sentinel = new java.lang.ref.WeakReference (new Object ());

static void watch (Display device) {
	if (!ON || thread != null) return;
	display = device;
	userInterface = Thread.currentThread ();
	String file = System.getProperty ("swt.watchdog.file");
	if (file != null) {
		try {
			out = new java.io.PrintStream (new java.io.FileOutputStream (file), true);
		} catch (java.io.IOException e) {}
	}
	thread = new Watchdog ();
	thread.setDaemon (true);
	/* PM raises the thread of the user interface: a busy one would starve a normal watchdog */
	thread.setPriority (Thread.MAX_PRIORITY);
	thread.start ();
	/* A busy user interface starves every other thread (2026-11-16), even at MAX_PRIORITY */
	if (Boolean.getBoolean ("swt.watchdog.lower")) userInterface.setPriority (Thread.MIN_PRIORITY);
	Thread collections = new Thread ("SWT watchdog of the collections") {
		public void run () {
			collections ();
		}
	};
	collections.setDaemon (true);
	collections.setPriority (Thread.MAX_PRIORITY);
	collections.start ();
}

/*
 * Woken by every garbage collection (a weak reference enqueued), not by the clock: writes the
 * marks when the user interface has seen swt.watchdog.gcs collections (and twice, four times
 * that...) without coming back to its loop, for a loop that calls no method with check. With
 * swt.watchdog.stop=true it stops the user interface (Thread.stop) at 32 times that, after
 * check has had its chance: the stack trace of that error is the one of this thread, but who
 * catches it (Eclipse logs it) tells where the user interface was.
 */
static void collections () {
	java.lang.ref.ReferenceQueue queue = new java.lang.ref.ReferenceQueue ();
	int seenLoops = -1, away = 0, next = FIRST_GCS;
	boolean stopped = false;
	while (true) {
		/* Kept here: a weak reference that is collected itself is never enqueued */
		java.lang.ref.WeakReference reference = new java.lang.ref.WeakReference (new Object (), queue);
		try {
			queue.remove ();
		} catch (InterruptedException e) {}
		if (reference.get () != null) continue;
		if (waiting || loops != seenLoops) {
			seenLoops = loops;
			away = 0;
			next = FIRST_GCS;
			continue;
		}
		if (++away < next) continue;
		next *= 2;
		StringBuffer report = new StringBuffer ();
		int count = Math.min (depth, marks.length);
		for (int i=0; i<count; i++) report.append ("    " + marks [i] + "\n");
		out.println ("SWT watchdog (collections): the user interface has not come back to its loop for " + away + " garbage collections, check called " + calls + " times; marks (outermost first):");
		out.print (count == 0 ? "    nothing marked\n" : report.toString ());
		out.print (threads ());
		out.flush ();
		if (STOP && !stopped && away >= 32 * FIRST_GCS && userInterface != null) {
			stopped = true;
			out.println ("SWT watchdog (collections): stopping the user interface");
			out.flush ();
			userInterface.stop (new Error ("SWT watchdog: the user interface was stopped after " + away + " garbage collections away from its loop"));
		}
	}
}

static void push (String mark) {
	if (depth < marks.length) marks [depth] = mark;
	depth++;
}

static void pop () {
	if (depth > 0) depth--;
}

/* The user interface is in its loop */
static void loop () {
	lastLoop = System.currentTimeMillis ();
	loops++;
	calls = gcs = 0;
	nextCalls = FIRST_CALLS;
	nextGcs = FIRST_GCS;
}

static void waiting (boolean value) {
	waiting = value;
	lastLoop = System.currentTimeMillis ();
}

/* Called by the user interface from Widget.checkWidget and from the text methods of GC */
public static void check () {
	if (Thread.currentThread () != userInterface) return;
	calls++;
	if (sentinel.get () == null) {
		gcs++;
		sentinel = new java.lang.ref.WeakReference (new Object ());
	}
	if (calls < nextCalls && gcs < nextGcs) return;
	while (nextCalls <= calls) nextCalls *= 2;
	while (nextGcs <= gcs) nextGcs *= 2;
	Runtime runtime = Runtime.getRuntime ();
	out.println ("SWT watchdog: report " + (++reports) + ": the user interface has called check " + calls + " times and seen " + gcs + " garbage collections without coming back to its loop, in " + (System.currentTimeMillis () - lastLoop) + " ms of the clock; heap " + (runtime.totalMemory () - runtime.freeMemory ()) + " used of " + runtime.totalMemory () + "; marks (outermost first):");
	int count = Math.min (depth, marks.length);
	for (int i=0; i<count; i++) out.println ("    " + marks [i]);
	new Throwable ("busy here").printStackTrace (out);
	out.flush ();
	if (STOP && !thrown && (gcs >= STOP_GCS || calls >= STOP_CALLS)) {
		thrown = true;
		out.println ("SWT watchdog: throwing an error from the user interface to break the loop");
		out.flush ();
		throw new Error ("SWT watchdog: the user interface did not come back to its loop (" + calls + " calls, " + gcs + " garbage collections)");
	}
}

/*
 * Writes the windows of PM of every shell, children in the order of PM (the top one first):
 * the position and size PM has (relative to the parent, y from the bottom), the styles, and
 * the widget of SWT with its bounds, for what is not painted or is covered.
 */
static void dump () {
	try {
		java.io.PrintStream file = new java.io.PrintStream (new java.io.FileOutputStream (DUMP, true), true);
		file.println ("==== windows of PM, " + new java.util.Date ());
		Shell [] shells = WidgetTable.shells ();
		for (int i=0; i<shells.length; i++) {
			if (!shells [i].isDisposed ()) dumpWindow (file, shells [i].frameHandle, "");
		}
		file.close ();
	} catch (java.io.IOException e) {}
}

static void dumpWindow (java.io.PrintStream file, int hwnd, String indent) {
	org.eclipse.swt.internal.pm.SWP swp = new org.eclipse.swt.internal.pm.SWP ();
	org.eclipse.swt.internal.pm.OS.WinQueryWindowPos (hwnd, swp);
	int bits = org.eclipse.swt.internal.pm.OS.WinQueryWindowULong (hwnd, org.eclipse.swt.internal.pm.OS.QWL_STYLE);
	org.eclipse.swt.internal.pm.PSZ name = new org.eclipse.swt.internal.pm.PSZ (64);
	org.eclipse.swt.internal.pm.OS.WinQueryClassName (hwnd, 64, name);
	StringBuffer line = new StringBuffer (indent);
	line.append (Integer.toHexString (hwnd) + " " + name + " at " + swp.x + "," + swp.y + " size " + swp.cx + "x" + swp.cy);
	line.append ((bits & org.eclipse.swt.internal.pm.OS.WS_VISIBLE) != 0 ? " visible" : " HIDDEN");
	if ((bits & org.eclipse.swt.internal.pm.OS.WS_CLIPCHILDREN) != 0) line.append (" clipchildren");
	if ((bits & org.eclipse.swt.internal.pm.OS.WS_CLIPSIBLINGS) != 0) line.append (" clipsiblings");
	line.append (" style " + Integer.toHexString (bits));
	Control control = WidgetTable.get (hwnd);
	if (control != null && !control.isDisposed ()) {
		line.append (" = " + control.getClass ().getName () + " " + control.getBounds () + (control.getVisible () ? "" : " getVisible false"));
		line.append (" bg " + control.getBackground ().getRGB ());
	}
	file.println (line.toString ());
	int child = org.eclipse.swt.internal.pm.OS.WinQueryWindow (hwnd, org.eclipse.swt.internal.pm.OS.QW_TOP);
	while (child != 0) {
		dumpWindow (file, child, indent + "  ");
		child = org.eclipse.swt.internal.pm.OS.WinQueryWindow (child, org.eclipse.swt.internal.pm.OS.QW_NEXT);
	}
}

/* The threads of Java that are alive, by name */
static String threads () {
	ThreadGroup group = Thread.currentThread ().getThreadGroup ();
	while (group.getParent () != null) group = group.getParent ();
	Thread [] all = new Thread [group.activeCount () + 8];
	int count = group.enumerate (all, true);
	StringBuffer result = new StringBuffer ();
	for (int i=0; i<count; i++) {
		result.append ("    thread \"" + all [i].getName () + "\"" + (all [i] == userInterface ? " (the user interface)" : "") + (all [i].isDaemon () ? " daemon" : "") + "\n");
	}
	return result.toString ();
}

public void run () {
	String last = null;
	boolean stopped = false;
	while (true) {
		try { Thread.sleep (1000); } catch (InterruptedException e) {}
		long idle = System.currentTimeMillis () - lastLoop;
		if (DUMP != null && new java.io.File (DUMP + ".go").delete ()) {
			display.asyncExec (new Runnable () {
				public void run () {
					dump ();
				}
			});
		}
		if (TRACE) {
			out.println ("SWT watchdog trace " + new java.util.Date () + ": idle " + idle + " ms" + (waiting ? " in sleep()" : "") + ", " + depth + " marks");
			int count = Math.min (depth, marks.length);
			for (int i=0; i<count; i++) out.println ("    " + marks [i]);
			out.print (threads ());
		}
		if (idle < LIMIT) {
			last = null;
			continue;
		}
		/* So that a profiler (-Xrunhprof) writes what it saw, and the hung program ends */
		if (EXIT > 0 && !waiting && idle >= EXIT) {
			out.println ("SWT watchdog: ending the program (System.exit) after " + idle + " ms away from its loop");
			out.flush ();
			System.exit (3);
		}
		/* Not while it waits for messages: an idle program does that */
		if (STOP && !stopped && !waiting && idle >= 3 * LIMIT && userInterface != null) {
			stopped = true;
			out.println ("SWT watchdog: stopping the user interface after " + idle + " ms" + "; the stack trace of the error tells where it was");
			out.flush ();
			userInterface.stop (new Error ("SWT watchdog: the user interface was hung here for " + idle + " ms"));
		}
		if (waiting) {
			String text = "waiting";
			if (text.equals (last)) continue;
			last = text;
			out.println ("SWT watchdog: the user interface has been waiting for messages (sleep) for " + idle + " ms; the threads:");
			out.print (threads ());
			out.flush ();
			continue;
		}
		int count = Math.min (depth, marks.length);
		StringBuffer report = new StringBuffer ();
		for (int i=0; i<count; i++) report.append ("    " + marks [i] + "\n");
		if (depth > marks.length) report.append ("    ... (" + depth + " in all)\n");
		String text = report.toString ();
		/* Again only when the marks change */
		if (text.equals (last)) continue;
		last = text;
		out.println ("SWT watchdog: the user interface has not come back to its loop for " + idle + " ms; it is in (outermost first):");
		out.print (count == 0 ? "    nothing marked (between messages, or in readAndDispatch)\n" : text);
		out.print (threads ());
		out.flush ();
	}
}
}
