package org.eclipse.swt.dnd;

/*
 * OS/2 version.
 * Copyright (c) 2002, 2026 EclipseOS2 Team.
 */

/*
 * Copyright (c) 2000, 2002 IBM Corp.  All rights reserved.
 * This file is made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 */

/**
 * The <code>TransferData</code> class is a platform specific data structure for
 * describing the type and the contents of data being converted by a transfer agent.
 *
 * <p>As an application writer, you do not need to know the specifics of
 * TransferData.  You should just be aware that it holds the type of the data.
 * Only the transfer agents need to know what it contains.</p>
 * <p>
 * OS/2 note: the type is a format of the clipboard of PM (an atom of the system
 * atom table), and the data is the address that the clipboard gives or takes.
 * </p>
 */
public class TransferData {
	/**
	 * The format of the data in the clipboard of PM.
	 * (Warning: This field is platform dependent)
	 */
	public int type;

	/**
	 * The address of the data, in memory of the clipboard (shared and giveable).
	 * (Warning: This field is platform dependent)
	 */
	public int data;

	/**
	 * Whether the conversion succeeded: 1, else 0.
	 * (Warning: This field is platform dependent)
	 */
	public int result;
}
