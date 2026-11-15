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

import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;

/**
 * <code>DragSource</code> defines the source object for a drag and drop transfer.
 * <p>
 * OS/2 note: drag and drop is not ported yet: the API is there, the transfers and the listeners are kept, but the user cannot drag or drop, and the listeners are never called (as on GTK in Eclipse 2.0.1).
 * </p>
 */
public final class DragSource extends Widget {

	Control control;
	Transfer[] transferAgents = new Transfer[0];

/**
 * Creates a new <code>DragSource</code> to handle the drag and drop of the given control.
 *
 * @param control the <code>Control</code> that the user drags and drops on
 * @param style the bitwise OR'ing of allowed operations; this may be a combination of any of
 *		DND.DROP_NONE, DND.DROP_COPY, DND.DROP_MOVE, DND.DROP_LINK
 */
public DragSource(Control control, int style) {
	super(control, checkStyle(style));
	this.control = control;
	/* It goes with its control */
	control.addListener(SWT.Dispose, new Listener() {
		public void handleEvent(Event event) {
			if (!isDisposed()) dispose();
		}
	});
}

static int checkStyle(int style) {
	if (style == SWT.NONE) return DND.DROP_MOVE;
	return style;
}

/**
 * Adds the listener to the collection of listeners who will
 * be notified of the drag and drop operations.
 *
 * @param listener the listener which should be notified
 *
 * @exception SWTError <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
 * </ul>
 */
public void addDragListener(DragSourceListener listener) {
	if (listener == null) DND.error (SWT.ERROR_NULL_ARGUMENT);
	DNDListener typedListener = new DNDListener (listener);
	addListener (DND.DragStart, typedListener);
	addListener (DND.DragSetData, typedListener);
	addListener (DND.DragEnd, typedListener);
}

protected void checkSubclass () {
	String name = getClass().getName ();
	String validName = DragSource.class.getName();
	if (!validName.equals(name)) {
		DND.error (SWT.ERROR_INVALID_SUBCLASS);
	}
}

/**
 * Returns the Control which is registered for this DragSource.
 *
 * @return the Control which is registered for this DragSource
 */
public Control getControl () {
	return control;
}

public Display getDisplay () {
	if (control == null) DND.error(SWT.ERROR_WIDGET_DISPOSED);
	return control.getDisplay ();
}

/**
 * Returns the list of data types that can be transferred.
 *
 * @return the list of data types that can be transferred
 */
public Transfer[] getTransfer(){
	return transferAgents;
}

/**
 * Removes the listener from the collection of listeners.
 *
 * @param listener the listener which should no longer be notified
 *
 * @exception SWTError <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
 * </ul>
 */
public void removeDragListener(DragSourceListener listener) {
	if (listener == null) DND.error (SWT.ERROR_NULL_ARGUMENT);
	removeListener (DND.DragStart, listener);
	removeListener (DND.DragSetData, listener);
	removeListener (DND.DragEnd, listener);
}

/**
 * Specifies the list of data types that can be transferred.
 *
 * @param transferAgents a list of Transfer objects which define the types of data that can be transferred
 */
public void setTransfer(Transfer[] transferAgents){
	if (transferAgents == null) DND.error (SWT.ERROR_NULL_ARGUMENT);
	this.transferAgents = transferAgents;
}
}
