/*
 * Copyright (c) 2001, 2002 IBM Corp and others.  All rights reserved.
 * This file is made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors: 
 *    Kevin Cornell (Rational Software Corporation)
 */ 
 
// This is a sample program to test the eclipse launcher.
// 
// If you are running this test program on Windows, the launcher
// should use java.exe and not javaw.exe. This can be done using
// either the -vm or -debug switch.
//
// To select a feature directory, use the option: -feature <directory>
// To perform a restart, you must run the launcher in an xterm
// (or use java.exe on Windows) and use the -debug switch.


package org.eclipse.core.launcher;

import java.io.*;


public class Main {

private static final String ENDSPLASH  = "-endsplash";
private static final String SHOWSPLASH = "-showsplash";
private static final String FEATURE    = "-feature";
private static final String DEBUG      = "-debug";
private static String    showSplashCmd = null;
private static String    endSplashCmd  = null;
private static Process   splashProcess = null;
private static String    featureDir = null;
private static boolean  debug = false;

private static String getSplashPath()
{
	return featureDir;
}

public static void endSplash()
{
	// If the new splash mechanism was used
	if (splashProcess != null)
	{
		// Terminate the splash screen process.
		splashProcess.destroy();
	}
	
	// else if the old splash mechanism was used
	else if (endSplashCmd != null)
	{
		// Bring down the splash screen nicely.
		try 
		{
			if (debug)
				System.out.println( "Eclipse: Executing '" + endSplashCmd + "'");
			Runtime.getRuntime().exec(endSplashCmd);
		} 
		catch (Exception e) { }
	}
	
	// else no splash screen was displayed.
}

private static void showSplash() 
{
	if (showSplashCmd == null)
		return; // running with original -endsplash

	// Parse the showsplash command into its separate arguments.
	// The command format is: 
	//     <executable> -showsplash <magicNum> [<featureDir>]
	// If either the <executable> or the <featureDir> arguments contain a
	// space, Runtime.getRuntime().exec( String ) will not work properly, even
	// if both arguments are enclosed in double-quotes. The solution is to
	// use the Runtime.getRuntime().exec( String[] ) method.
	String path  = getSplashPath();
	String[] cmd = new String[ (path != null ? 4 : 3) ];
	int sIndex = 0;
	int eIndex = showSplashCmd.indexOf( " -show" );
	cmd[0] = showSplashCmd.substring( sIndex, eIndex );
	sIndex = eIndex + 1;
	eIndex = showSplashCmd.indexOf( " ", sIndex );
	cmd[1] = showSplashCmd.substring( sIndex, eIndex );
	cmd[2] = showSplashCmd.substring( eIndex+1 );
	if (path != null)
		cmd[3] = path;
	try 
	{
		if (debug)
		{
			System.out.print( "Eclipse: Executing");
			for (int i = 0; i < cmd.length; i++)
			    System.out.print( " " + cmd[i] );
			System.out.println("");
		}
		splashProcess = Runtime.getRuntime().exec(cmd);
	} 
	catch (Exception e) 
	{
		if (debug)
			System.out.println("Eclipse: Exception in showSplash\n" + e);
	}
}

public static void main( String args[] ) 
{
	byte[]  answer = new byte[ 500 ];
	int     nRead;
	boolean console = true;

    // Extract the commands to show and end the splash window. 
    for (int index = 0; index < args.length; index++)
    {
        if (args[ index ].equalsIgnoreCase( ENDSPLASH ) && (index+1) < args.length)
        {
            endSplashCmd = args[ index + 1 ];
        }

        else if (args[ index ].equalsIgnoreCase( SHOWSPLASH ) && (index+1) < args.length)
        {
            showSplashCmd = args[ index + 1 ];
        }

        else if (args[ index ].equalsIgnoreCase( FEATURE ) && (index+1) < args.length)
        {
            featureDir = args[ index + 1 ];
        }

        else if (args[ index ].equalsIgnoreCase( DEBUG ))
        {
            debug = true;
        }
    }


    // Print the commands if debugging. 
    if (debug)
    {
    	System.out.println( "Eclipse: Program started." );
    	for (int index = 0; index < args.length; index++)
        	System.out.println( "    args[" + index + "] = '" + args[index] + "'" );
    }
    
    // Attempt to show the splash screen.
    showSplash();

	// Wait for the initialization to complete.
	if (debug)
	{
    	try
    	{
    		System.out.print("Eclipse: Hit return to end initialization. ");
    		nRead = System.in.read(answer);
    	}
    	catch (Exception e) {}
	}
	else
    {
    	// Quietly wait for 5 seconds
    	try 
    	{
        	Thread.sleep( 5000 );
    	}
    	catch (Exception e) {}
    }
	
    // Bring down the splash screen.
    endSplash();

    // Simulate the actual work of eclipse. (wait 3 seconds)
    try 
    {
        Thread.sleep( 3000 );
    }
    catch (Exception e) {}
    
    // Determine if a restart is required.
    if (debug)
    {
	    int exitCode = 0;
	    try
	    {
	    	System.out.println( "Eclipse special exit codes:\n" +
	    					    "  0  - normal exit\n" +
	    					    "  13 - problem during startup\n" +
	    					    "  14 - wrong JVM being used\n" +
	    					    "  15 - workbench already running\n" +
	    					    "  23 - restart Eclipse" );
	    	System.out.print("Eclipse: Enter the exit code: ");
	    	nRead = System.in.read(answer);
	    	String value = new String(answer).trim();
	    	if (value.length() > 0)
	    		exitCode = Integer.parseInt(value);
	    }
	    catch (Exception e) {}
	    System.exit(exitCode);
	       
    }
}
}