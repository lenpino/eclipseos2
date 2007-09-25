/*
 *  TestCase.java
 */

/*
 * Copyright (c) 2002, 2004 EclipseOS2 Team.
 * This file is made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 */

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 *  Simple test case class that implements the Runnable interface. Its run()
 *  method (which is done final for ...) sequentally calls init() to do
 *  initialization, exec() to do necessary test actions and done() to cleanup.
 *  init() and done() simply do nothing, exec() throws a dummy exception.  
 *
 *  The test is considered to be failed if an ecxeption is thown, otherwise
 *  it is considered to be passed.
 *
 *  This class also provides a simple testcase launcher -- the go() method,
 *  that handles exceptions and logs them to the console. It is intended to
 *  be called from the main() method of the testcase to make it possible to
 *  run the testase from the console. 
 *
 *  Subclasses should redefine the PART, STEP, TEST and DESC data members
 *  using the static initializer and override the exec() method to do actual
 *  test. They also should provide the main() method which simply creates a
 *  class instance and calls go() passing the created instance as its
 *	argument (see the main method below) to be able to run the testcase from
 *  the console and may override init() and done() to do any initialization
 *  and cleanup as required.
 */

public class TestCase implements Runnable {

static String PART = "PART";
static String STEP = "XXX";
static String TEST = "YY";
static String DESC = "Dummy Test";

static final String DASH_LINE =
    "============================================================";
static final String DOT_LINE =
    "............................................................";

public static void main (String [] args) {
    go (new TestCase ());
}

static void go (TestCase test) {
    try {
        sayHeader (test);
        test.run ();
        sayFooter (test, true);

    } catch (Throwable e) {
        StringWriter es = new StringWriter();
        e.printStackTrace (new PrintWriter (es));
        System.out.print (es);
        sayFooter (test, false);
    }
}

protected void init () {}

protected void done () {}

protected void exec () {
    sayObjective ("Dummy");
    throw new RuntimeException ("This is a dummy testcase and it will never pass!");
}

final public void run () {
    init();
    exec();
    done();
}

static void say () {
    System.out.println();
}

static void say (String text) {
    System.out.println(text);
}

static void sayObjective (String objective) {
    say ("\nOBJECTIVE: " + objective);
    say (DASH_LINE+'\n');
}

static void saySubObjective (String subObjective) {
    say (subObjective);
    say (DOT_LINE+'\n');
}

static String getName () {
    String rc = PART + STEP;
    if (TEST != null && TEST.length() > 0) rc += '_' + TEST;
    return rc;
}

static String getFullName () {
	if (DESC == null) return getName();
	return getName() + ": " + DESC;
}

static void sayHeader (TestCase test) {
    say (DASH_LINE);
    say (test.getFullName ());
    say (DASH_LINE+'\n');
}

static void sayFooter (TestCase test, boolean passed) {
    say ('\n'+DASH_LINE);
    say (test.getName () + ": Testcase " + (passed ? "PASSED OK" : "FAILED!!!"));
    say (DASH_LINE+'\n');
}

}

