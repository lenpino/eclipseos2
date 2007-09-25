/*
 *  SWT001.java
 */

/*
 * Copyright (c) 2002, 2004 EclipseOS2 Team.
 * This file is made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 */

import java.io.*;

import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.internal.*;
import org.eclipse.swt.internal.pm.*;

public class SWT001 extends SWTTestCase{
    
static {
    STEP= "001";
    TEST = null;
    DESC = "SWT Internals";
}
	
public static void main (String[] args) {
    go (new SWT001());
}

protected void exec() {
    System.out.println( "OBJECTIVE: o.e.swt.internal.Library AAALLLLLL" );
    System.out.println( "-------------------------------------------\n" );
    System.out.println( "java.library.path = " + System.getProperty( "java.library.path" ) + '\n' );
    System.out.println( "Trying to load SWT native library...\n" );
    Library.loadLibrary( "swt" );       

    System.out.println( "OBJECTIVE: o.e.swt.internal.Callback.getPlatform()" );
    System.out.println( "-------------------------------------------\n" );
    System.out.println( "o.e.swt.internal.Callback.getPlatform() = " + Callback.getPlatform ());
    System.out.println( "o.e.swt.SWT.getPlatform() = " + SWT.getPlatform () );
    System.out.println();

    System.out.println( "OBJECTIVE: OS/2 version info (o.e.swt.internal.OS constants)" );
    System.out.println( "-------------------------------------------\n" );
    System.out.println( "IsWarp3 = "+OS.IsWarp3 );
    System.out.println( "IsWarp4 = "+OS.IsWarp4 );
    System.out.println( "IsAurora = "+OS.IsAurora );
    System.out.println( "OS2_MAJOR = "+OS.OS2_MAJOR+", OS2_MINOR = "+OS.OS2_MINOR+
        ", OS2_REVISION = "+OS.OS2_REVISION );
    System.out.println();

    System.out.println( "OBJECTIVE: o.e.swt.internal.OS.WinAlarm()" );
    System.out.println( "-------------------------------------------\n" );
    try {
        System.out.println( "o.e.swt.internal.OS.WinAlarm(OS.WA_WARNING) = " + OS.WinAlarm (OS.HWND_DESKTOP, OS.WA_WARNING) );
        Thread.currentThread ().sleep (500);
        System.out.println( "o.e.swt.internal.OS.WinAlarm(OS.WA_NOTE) = " + OS.WinAlarm (OS.HWND_DESKTOP, OS.WA_NOTE) );
        Thread.currentThread ().sleep (500);
        System.out.println( "o.e.swt.internal.OS.WinAlarm(OS.WA_ERROR) = " + OS.WinAlarm (OS.HWND_DESKTOP, OS.WA_ERROR) );
        Thread.currentThread ().sleep (500);
        System.out.println( "o.e.swt.internal.OS.WinAlarm(4) = " + OS.WinAlarm (OS.HWND_DESKTOP, 4) );
    } catch (InterruptedException x) {
    }
    System.out.println();

    System.out.println( "OBJECTIVE: PM initialization sequences" );
    System.out.println( "-------------------------------------------\n" );
    int hab = OS.WinInitialize (0);
    System.out.println( "o.e.swt.internal.OS.WinInitalize(0) = " + hab );
    System.out.println( "o.e.swt.internal.OS.WinGetLastError(hab) = " + OS.WinGetLastError (hab) );
    int hmq = OS.WinCreateMsgQueue (hab, 0);
    System.out.println( "o.e.swt.internal.OS.WinCreateMsgQueue(hmq) = " + hmq );
    System.out.println( "o.e.swt.internal.OS.WinGetLastError(hab) = " + OS.WinGetLastError (hab) );
    System.out.println( "o.e.swt.internal.OS.WinDestroyMsgQueue(hmq) = " + OS.WinDestroyMsgQueue (hmq) );
    System.out.println( "o.e.swt.internal.OS.WinTerminate(hab) = " + OS.WinTerminate (hab) );
    System.out.println();

    System.out.println( "OBJECTIVE: o.e.swt.internal.OS.DosGetInfoBlocks()" );
    System.out.println( "-------------------------------------------\n" );
    int[] ptib = new int[1];
    int[] ppib = new int[1];
    TIB tib = new TIB();
    PIB pib = new PIB();
    System.out.println( "o.e.swt.internal.OS.DosGetInfoBlocks() = " + OS.DosGetInfoBlocks (ptib, ppib) );
    OS.objcpy (tib, ptib[0]);
    OS.objcpy (pib, ppib[0]);
    System.out.println( "pib.pib_ulpid = " + pib.pib_ulpid );
    System.out.println( "pib.pib_ulppid = " + pib.pib_ulppid );
    System.out.println( "pib.pib_hmte = " + pib.pib_hmte );
    System.out.println( "pib.pib_ultype = " + pib.pib_ultype );
    System.out.println( "tib.tib_pstack = " + tib.tib_pstack );
    System.out.println( "tib.tib_version = " + tib.tib_version );
    System.out.println( "tib.tib_ordinal = " + tib.tib_ordinal );
    System.out.println( "tib.tib_ptib2.tib2_ultid = " + tib.tib_ptib2.tib2_ultid );
    System.out.println( "tib.tib_ptib2.tib2_ulpri = " + tib.tib_ptib2.tib2_ulpri );
    System.out.println("// setting MAX_PRIORITY for a thread...");
    int pri = Thread.currentThread().getPriority();
    Thread.currentThread().setPriority (Thread.MAX_PRIORITY);
    OS.objcpy (tib, ptib[0]);
    System.out.println( "tib.tib_ptib2.tib2_ulpri = " + tib.tib_ptib2.tib2_ulpri);
    Thread.currentThread().setPriority (pri);
    System.out.println();
}

}

