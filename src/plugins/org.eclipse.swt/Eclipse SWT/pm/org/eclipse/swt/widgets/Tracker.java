package org.eclipse.swt.widgets;

/*
 * Copyright (c) 2000, 2026 IBM Corp. and the EclipseOS2 Team.  All rights reserved.
 * This file is made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 */

import org.eclipse.swt.internal.pm.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.*;
import org.eclipse.swt.events.*;

/**
 *  Instances of this class implement rubber banding rectangles that are
 *  drawn onto a parent <code>Composite</code> or <code>Display</code>.
 *  These rectangles can be specified to respond to mouse and key events
 *  by either moving or resizing themselves accordingly.  Trackers are
 *  typically used to represent window geometries in a lightweight manner.
 *
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>LEFT, RIGHT, UP, DOWN, RESIZE</dd>
 * <dt><b>Events:</b></dt>
 * <dd>Move, Resize</dd>
 * </dl>
 * <p>
 * Note: Rectangle move behavior is assumed unless RESIZE is specified.
 * </p><p>
 * IMPORTANT: This class is <em>not</em> intended to be subclassed.
 * </p>
 */
public class Tracker extends Widget {
	Control parent;
	Display display;
	boolean tracking, stippled;
	Rectangle [] rectangles, proportions;
	int resizePointer, clientCursor, cursorOrientation = SWT.NONE;
	boolean inEvent = false;

	/*
	* The following values mirror step sizes on Windows
	*/
	final static int STEPSIZE_SMALL = 1;
	final static int STEPSIZE_LARGE = 9;

/**
 * Constructs a new instance of this class given its parent
 * and a style value describing its behavior and appearance.
 * <p>
 * The style value is either one of the style constants defined in
 * class <code>SWT</code> which is applicable to instances of this
 * class, or must be built by <em>bitwise OR</em>'ing together
 * (that is, using the <code>int</code> "|" operator) two or more
 * of those <code>SWT</code> style constants. The class description
 * lists the style constants that are applicable to the class.
 * Style bits are also inherited from superclasses.
 * </p>
 *
 * @param parent a widget which will be the parent of the new instance (cannot be null)
 * @param style the style of widget to construct
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li>
 *    <li>ERROR_INVALID_SUBCLASS - if this class is not an allowed subclass</li>
 * </ul>
 *
 * @see SWT#LEFT
 * @see SWT#RIGHT
 * @see SWT#UP
 * @see SWT#DOWN
 * @see SWT#RESIZE
 * @see Widget#checkSubclass
 * @see Widget#getStyle
 */
public Tracker (Composite parent, int style) {
	super (parent, checkStyle (style));
	this.parent = parent;
	display = parent.getDisplay ();
}

/**
 * Constructs a new instance of this class given the display
 * to create it on and a style value describing its behavior
 * and appearance.
 * <p>
 * The style value is either one of the style constants defined in
 * class <code>SWT</code> which is applicable to instances of this
 * class, or must be built by <em>bitwise OR</em>'ing together
 * (that is, using the <code>int</code> "|" operator) two or more
 * of those <code>SWT</code> style constants. The class description
 * lists the style constants that are applicable to the class.
 * Style bits are also inherited from superclasses.
 * </p><p>
 * Note: Currently, null can be passed in for the display argument.
 * This has the effect of creating the tracker on the currently active
 * display if there is one. If there is no current display, the
 * tracker is created on a "default" display. <b>Passing in null as
 * the display argument is not considered to be good coding style,
 * and may not be supported in a future release of SWT.</b>
 * </p>
 *
 * @param display the display to create the tracker on
 * @param style the style of control to construct
 *
 * @exception SWTException <ul>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li>
 *    <li>ERROR_INVALID_SUBCLASS - if this class is not an allowed subclass</li>
 * </ul>
 *
 * @see SWT#LEFT
 * @see SWT#RIGHT
 * @see SWT#UP
 * @see SWT#DOWN
 */
public Tracker (Display display, int style) {
	if (display == null) display = Display.getCurrent ();
	if (display == null) display = Display.getDefault ();
	if (!display.isValidThread ()) {
		error (SWT.ERROR_THREAD_INVALID_ACCESS);
	}
	this.style = checkStyle (style);
	this.display = display;
}

/**
 * Adds the listener to the collection of listeners who will
 * be notified when the control is moved or resized, by sending
 * it one of the messages defined in the <code>ControlListener</code>
 * interface.
 *
 * @param listener the listener which should be notified
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @see ControlListener
 * @see #removeControlListener
 */
public void addControlListener (ControlListener listener) {
	checkWidget ();
	if (listener == null) error (SWT.ERROR_NULL_ARGUMENT);
	TypedListener typedListener = new TypedListener (listener);
	addListener (SWT.Move,typedListener);
}

/*
 * The y axis of the screen grows upwards in PM and downwards in SWT:
 * these two convert a point of the screen from one to the other.
 */
static int screenHeight () {
	return OS.WinQuerySysValue (OS.HWND_DESKTOP, OS.SV_CYSCREEN);
}

Point toScreen (int x, int y) {
	if (parent != null) return parent.toDisplay (new Point (x, y));
	return new Point (x, y);
}

Point setPointerPos (int x, int y) {
	Point pt = toScreen (x, y);
	OS.WinSetPointerPos (OS.HWND_DESKTOP, pt.x, screenHeight () - 1 - pt.y);
	return pt;
}

Point adjustMoveCursor () {
	Rectangle bounds = computeBounds ();
	return setPointerPos (bounds.x + bounds.width / 2, bounds.y);
}

Point adjustResizeCursor () {
	int newX, newY;
	Rectangle bounds = computeBounds ();

	if ((cursorOrientation & SWT.LEFT) != 0) {
		newX = bounds.x;
	} else if ((cursorOrientation & SWT.RIGHT) != 0) {
		newX = bounds.x + bounds.width;
	} else {
		newX = bounds.x + bounds.width / 2;
	}

	if ((cursorOrientation & SWT.UP) != 0) {
		newY = bounds.y;
	} else if ((cursorOrientation & SWT.DOWN) != 0) {
		newY = bounds.y + bounds.height;
	} else {
		newY = bounds.y + bounds.height / 2;
	}

	Point pt = setPointerPos (newX, newY);

	/*
	* If the client has not provided a custom cursor then choose the
	* pointer of the edge or corner that is being resized. The system
	* pointers belong to PM: they are not copied, so there is nothing
	* to destroy. The pointer stays while the tracker does not dispatch
	* the mouse messages (see open()).
	*/
	if (clientCursor != 0) return pt;
	int id;
	switch (cursorOrientation) {
		case SWT.UP:
		case SWT.DOWN:
			id = OS.SPTR_SIZENS;
			break;
		case SWT.LEFT:
		case SWT.RIGHT:
			id = OS.SPTR_SIZEWE;
			break;
		case SWT.LEFT | SWT.UP:
		case SWT.RIGHT | SWT.DOWN:
			id = OS.SPTR_SIZENWSE;
			break;
		case SWT.LEFT | SWT.DOWN:
		case SWT.RIGHT | SWT.UP:
			id = OS.SPTR_SIZENESW;
			break;
		default:
			id = OS.SPTR_MOVE;
			break;
	}
	resizePointer = OS.WinQuerySysPointer (OS.HWND_DESKTOP, id, false);
	OS.WinSetPointer (OS.HWND_DESKTOP, resizePointer);

	return pt;
}
static int checkStyle (int style) {
	if ((style & (SWT.LEFT | SWT.RIGHT | SWT.UP | SWT.DOWN)) == 0) {
		style |= SWT.LEFT | SWT.RIGHT | SWT.UP | SWT.DOWN;
	}
	return style;
}
/**
 * Stops displaying the tracker rectangles.  Note that this is not considered
 * to be a cancelation by the user.
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void close () {
	checkWidget ();
	tracking = false;
}

Rectangle computeBounds () {
	int xMin = rectangles [0].x;
	int yMin = rectangles [0].y;
	int xMax = rectangles [0].x + rectangles [0].width;
	int yMax = rectangles [0].y + rectangles [0].height;

	for (int i = 1; i < rectangles.length; i++) {
		if (rectangles [i].x < xMin) xMin = rectangles [i].x;
		if (rectangles [i].y < yMin) yMin = rectangles [i].y;
		int rectRight = rectangles [i].x + rectangles [i].width;
		if (rectRight > xMax) xMax = rectRight;
		int rectBottom = rectangles [i].y + rectangles [i].height;
		if (rectBottom > yMax) yMax = rectBottom;
	}

	return new Rectangle (xMin, yMin, xMax - xMin, yMax - yMin);
}

Rectangle [] computeProportions (Rectangle [] rects) {
	Rectangle [] result = new Rectangle [rects.length];
	Rectangle bounds = computeBounds ();
	/* Windows divides by zero with an empty rectangle (difference 016) */
	int width = Math.max (bounds.width, 1), height = Math.max (bounds.height, 1);
	for (int i = 0; i < rects.length; i++) {
		result[i] = new Rectangle (
			(rects[i].x - bounds.x) * 100 / width,
			(rects[i].y - bounds.y) * 100 / height,
			rects[i].width * 100 / width,
			rects[i].height * 100 / height);
	}
	return result;
}

/*
 * Inverts the pixels of a rectangle given in PM coordinates. As in
 * the rest of GpiBitBlt calls, the upper right corner is not included.
 */
static void invert (int hps, int xLeft, int yBottom, int xRight, int yTop, int rop) {
	if (xRight <= xLeft || yTop <= yBottom) return;
	OS.GpiBitBlt (hps, 0, 2, new int [] {xLeft, yBottom, xRight, yTop}, rop, 0);
}

/**
 * Draw the rectangles displayed by the tracker.
 */
void drawRectangles (Rectangle [] rects) {
	if (parent != null) {
		if (parent.isDisposed ()) return;
		parent.getShell ().update ();
	} else {
		display.update ();
	}
	/*
	* The presentation space of the parent covers its children (SWT
	* composites do not have WS_CLIPCHILDREN) but not the windows that
	* are above it, like the device context that Windows uses.
	*/
	int hps, height;
	if (parent != null) {
		hps = OS.WinGetPS (parent.handle);
		height = parent.getHeight ();
	} else {
		hps = OS.WinGetScreenPS (OS.HWND_DESKTOP);
		height = screenHeight ();
	}
	int bandWidth = 1, rop = OS.ROP_DSTINVERT;
	if (stippled) {
		bandWidth = 3;
		/* The pixels of the halftone pattern are all ones (inverted) or all zeros (kept) */
		OS.GpiSetPattern (hps, OS.PATSYM_HALFTONE);
		OS.GpiSetColor (hps, OS.CLR_TRUE);
		OS.GpiSetBackColor (hps, OS.CLR_FALSE);
		rop = OS.ROP_PATINVERT;
	}
	for (int i=0; i<rects.length; i++) {
		Rectangle rect = rects [i];
		int left = rect.x, right = rect.x + rect.width;
		int top = height - rect.y, bottom = height - rect.y - rect.height;
		invert (hps, left, top - bandWidth, right, top, rop);
		invert (hps, left, bottom + bandWidth, left + bandWidth, top - bandWidth, rop);
		invert (hps, right - bandWidth, bottom + bandWidth, right, top - bandWidth, rop);
		invert (hps, left, bottom, right, bottom + bandWidth, rop);
	}
	OS.WinReleasePS (hps);
}

public Display getDisplay () {
	return display;
}

/**
 * Returns the bounds that are being drawn, expressed relative to the parent
 * widget.  If the parent is a <code>Display</code> then these are screen
 * coordinates.
 *
 * @return the bounds of the Rectangles being drawn
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public Rectangle [] getRectangles () {
	checkWidget ();
	return rectangles;
}

/**
 * Returns <code>true</code> if the rectangles are drawn with a stippled line, <code>false</code> otherwise.
 *
 * @return the stippled effect of the rectangles
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public boolean getStippled () {
	checkWidget ();
	return stippled;
}

void moveRectangles (int xChange, int yChange) {
	if (xChange < 0 && ((style & SWT.LEFT) == 0)) return;
	if (xChange > 0 && ((style & SWT.RIGHT) == 0)) return;
	if (yChange < 0 && ((style & SWT.UP) == 0)) return;
	if (yChange > 0 && ((style & SWT.DOWN) == 0)) return;
	for (int i = 0; i < rectangles.length; i++) {
		rectangles [i].x += xChange;
		rectangles [i].y += yChange;
	}
}

/*
 * The position of the pointer of a mouse message, in SWT coordinates
 * of the screen. It is taken from the message, and not from the
 * position of the pointer, so that a posted message works as well.
 */
static Point messagePos (QMSG msg) {
	int [] pt = new int [] {(short) (msg.mp1 & 0xFFFF), (short) (msg.mp1 >> 16)};
	OS.WinMapWindowPoints (msg.hwnd, OS.HWND_DESKTOP, pt, 1);
	return new Point (pt [0], screenHeight () - 1 - pt [1]);
}

static boolean isMouseMessage (int message) {
	if (OS.WM_MOUSEMOVE <= message && message <= OS.WM_BUTTON3DBLCLK) return true;
	/* WM_CHORD, WM_BUTTONxMOTIONSTART/END, WM_BUTTONxCLICK and the like */
	return 0x0410 <= message && message <= 0x041F;
}

/**
 * Displays the Tracker rectangles for manipulation by the user.  Returns when
 * the user has either finished manipulating the rectangles or has cancelled the
 * Tracker.
 *
 * @return <code>true</code> if the user did not cancel the Tracker, <code>false</code> otherwise
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public boolean open () {
	checkWidget ();
	if (rectangles == null) return false;
	boolean cancelled = false;
	tracking = true;
	Event event = new Event ();
	QMSG msg = new QMSG ();
	/*
	* Windows creates a transparent window that covers the screen to get
	* the mouse messages that occur over the desktop. In PM the window
	* that captures the pointer gets them wherever the pointer is. When
	* the tracker is opened while a button is down, the control that got
	* the button already has the capture and keeps it, so that it gets
	* the button up message too.
	*/
	int hwndCapture = 0;
	if (OS.WinQueryCapture (OS.HWND_DESKTOP) == 0) {
		hwndCapture = parent != null ? parent.handle : display.hwndShell;
		OS.WinSetCapture (OS.HWND_DESKTOP, hwndCapture);
	}
	boolean mouseDown = (OS.WinGetKeyState (OS.HWND_DESKTOP, OS.VK_BUTTON1) & 0x8000) != 0;

	drawRectangles (rectangles);
	if (clientCursor != 0) OS.WinSetPointer (OS.HWND_DESKTOP, clientCursor);
	Point cursorPos;
	if (mouseDown) {
		cursorPos = display.getCursorLocation ();
	} else {
		if ((style & SWT.RESIZE) != 0) {
			cursorPos = adjustResizeCursor ();
		} else {
			cursorPos = adjustMoveCursor ();
		}
	}

	int oldX = cursorPos.x, oldY = cursorPos.y;
	/*
	* Tracker behaves like a Dialog with its own OS event loop.
	*/
	while (tracking && !cancelled) {
		if (parent != null && parent.isDisposed ()) break;
		if (!OS.WinGetMsg (display.hab, msg, 0, 0, 0)) {
			/* WM_QUIT: leave it for the loop of the application */
			OS.WinPostQueueMsg (display.hmq, OS.WM_QUIT, msg.mp1, msg.mp2);
			cancelled = true;
			break;
		}
		int message = msg.msg;
		switch (message) {
			case OS.WM_BUTTON1UP:
			case OS.WM_MOUSEMOVE:
				Point newPos = messagePos (msg);
				int newX = newPos.x;
				int newY = newPos.y;
				if (newX != oldX || newY != oldY) {
					drawRectangles (rectangles);
					event.x = newX;
					event.y = newY;
					if ((style & SWT.RESIZE) != 0) {
						resizeRectangles (newX - oldX, newY - oldY);
						cursorPos = adjustResizeCursor ();
						newX = cursorPos.x; newY = cursorPos.y;
						inEvent = true;
						sendEvent (SWT.Resize, event);
					} else {
						moveRectangles (newX - oldX, newY - oldY);
						inEvent = true;
						sendEvent (SWT.Move, event);
					}
					inEvent = false;
					/*
					* It is possible (but unlikely), that application
					* code could have disposed the widget in the move
					* event.  If this happens, return false to indicate
					* that the tracking has failed.
					*/
					if (isDisposed ()) {
						if (hwndCapture != 0) OS.WinSetCapture (OS.HWND_DESKTOP, OS.NULLHANDLE);
						return false;
					}
					drawRectangles (rectangles);
					oldX = newX;  oldY = newY;
				}
				tracking = message != OS.WM_BUTTON1UP;
				break;
			case OS.WM_CHAR:
				int fs = msg.mp1 & 0xFFFF;
				if ((fs & OS.KC_KEYUP) != 0) break;
				/* Windows cancels with WM_SYSKEYDOWN, a key pressed with Alt */
				if ((fs & OS.KC_ALT) != 0) {
					cancelled = true;
					tracking = false;
					break;
				}
				if ((fs & OS.KC_VIRTUALKEY) == 0) break;
				int stepSize = (fs & OS.KC_CTRL) != 0 ? STEPSIZE_SMALL : STEPSIZE_LARGE;
				int xChange = 0, yChange = 0;
				switch ((msg.mp2 >> 16) & 0xFFFF) {
					case OS.VK_ESC:
						cancelled = true;
						tracking = false;
						break;
					case OS.VK_NEWLINE:
					case OS.VK_ENTER:
						tracking = false;
						break;
					case OS.VK_LEFT:
						xChange = -stepSize;
						break;
					case OS.VK_RIGHT:
						xChange = stepSize;
						break;
					case OS.VK_UP:
						yChange = -stepSize;
						break;
					case OS.VK_DOWN:
						yChange = stepSize;
						break;
				}
				if (xChange != 0 || yChange != 0) {
					drawRectangles (rectangles);
					newX = oldX + xChange;
					newY = oldY + yChange;
					event.x = newX;
					event.y = newY;
					if ((style & SWT.RESIZE) != 0) {
						resizeRectangles (xChange, yChange);
						cursorPos = adjustResizeCursor ();
						inEvent = true;
						sendEvent (SWT.Resize, event);
					} else {
						moveRectangles (xChange, yChange);
						cursorPos = adjustMoveCursor ();
						inEvent = true;
						sendEvent (SWT.Move, event);
					}
					inEvent = false;
					/*
					* It is possible (but unlikely) that application
					* code could have disposed the widget in the move
					* event.  If this happens return false to indicate
					* that the tracking has failed.
					*/
					if (isDisposed ()) {
						if (hwndCapture != 0) OS.WinSetCapture (OS.HWND_DESKTOP, OS.NULLHANDLE);
						return false;
					}
					drawRectangles (rectangles);
					oldX = cursorPos.x;  oldY = cursorPos.y;
				}
				break;
		}
		/*
		* Don't dispatch mouse and key events in general, EXCEPT once this
		* tracker has finished its work.
		*/
		if (tracking && !cancelled) {
			if (message == OS.WM_CHAR) continue;
			if (isMouseMessage (message)) continue;
		}
		OS.WinDispatchMsg (display.hab, msg);
	}
	drawRectangles (rectangles);
	/*
	* Cleanup: release the capture if the tracker took it. The pointer
	* is set again by the window under it with the next mouse message.
	*/
	if (hwndCapture != 0) {
		OS.WinSetCapture (OS.HWND_DESKTOP, OS.NULLHANDLE);
	}
	resizePointer = 0;
	tracking = false;
	return !cancelled;
}

/**
 * Removes the listener from the collection of listeners who will
 * be notified when the control is moved or resized.
 *
 * @param listener the listener which should be notified
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @see ControlListener
 * @see #addControlListener
 */
public void removeControlListener (ControlListener listener) {
	checkWidget ();
	if (listener == null) error (SWT.ERROR_NULL_ARGUMENT);
	if (eventTable == null) return;
	eventTable.unhook (SWT.Move, listener);
}

void resizeRectangles (int xChange, int yChange) {
	/*
	* If the cursor orientation has not been set in the orientation of
	* this change then try to set it here.
	*/
	if (xChange < 0 && ((style & SWT.LEFT) != 0) && ((cursorOrientation & SWT.RIGHT) == 0)) {
		cursorOrientation |= SWT.LEFT;
	} else if (xChange > 0 && ((style & SWT.RIGHT) != 0) && ((cursorOrientation & SWT.LEFT) == 0)) {
		cursorOrientation |= SWT.RIGHT;
	} else if (yChange < 0 && ((style & SWT.UP) != 0) && ((cursorOrientation & SWT.DOWN) == 0)) {
		cursorOrientation |= SWT.UP;
	} else if (yChange > 0 && ((style & SWT.DOWN) != 0) && ((cursorOrientation & SWT.UP) == 0)) {
		cursorOrientation |= SWT.DOWN;
	}
	Rectangle bounds = computeBounds ();
	if ((cursorOrientation & SWT.LEFT) != 0) {
		bounds.x += xChange;
		bounds.width -= xChange;
	} else if ((cursorOrientation & SWT.RIGHT) != 0) {
		bounds.width += xChange;
	}
	if ((cursorOrientation & SWT.UP) != 0) {
		bounds.y += yChange;
		bounds.height -= yChange;
	} else if ((cursorOrientation & SWT.DOWN) != 0) {
		bounds.height += yChange;
	}
	/*
	* The following are conditions under which the resize should not be applied
	*/
	if (bounds.width < 0 || bounds.height < 0) return;

	Rectangle [] newRects = new Rectangle [rectangles.length];
	for (int i = 0; i < rectangles.length; i++) {
		Rectangle proportion = proportions[i];
		newRects[i] = new Rectangle (
			proportion.x * bounds.width / 100 + bounds.x,
			proportion.y * bounds.height / 100 + bounds.y,
			proportion.width * bounds.width / 100,
			proportion.height * bounds.height / 100);
	}
	rectangles = newRects;
}

/**
 * Sets the <code>Cursor</code> of the Tracker.  If this cursor is <code>null</code>
 * then the cursor reverts to the default.
 *
 * @param newCursor the new <code>Cursor</code> to display
 * 
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void setCursor(Cursor newCursor) {
	checkWidget();
	clientCursor = 0;
	if (newCursor != null) {
		clientCursor = newCursor.handle;
		if (inEvent) OS.WinSetPointer (OS.HWND_DESKTOP, clientCursor);
	}
}

/**
 * Specifies the rectangles that should be drawn, expressed relative to the parent
 * widget.  If the parent is a Display then these are screen coordinates.
 *
 * @param rectangles the bounds of the rectangles to be drawn
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void setRectangles (Rectangle [] rectangles) {
	checkWidget ();
	if (rectangles == null) error (SWT.ERROR_NULL_ARGUMENT);
	this.rectangles = rectangles;
	proportions = computeProportions (rectangles);
}

/**
 * Changes the appearance of the line used to draw the rectangles.
 *
 * @param stippled <code>true</code> if rectangle should appear stippled
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void setStippled (boolean stippled) {
	checkWidget ();
	this.stippled = stippled;
}
}
