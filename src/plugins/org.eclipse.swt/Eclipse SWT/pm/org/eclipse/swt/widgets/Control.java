package org.eclipse.swt.widgets;

/*
 * OS/2 version.
 * Copyright (c) 2002, 2004 EclipseOS2 Team.
 */

/*
 * Copyright (c) 2000, 2002 IBM Corp.  All rights reserved.
 * This file is made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 */

import org.eclipse.swt.internal.pm.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.accessibility.*;

/**
 * Control is the abstract superclass of all windowed user interface classes.
 * <p>
 * <dl>
 * <dt><b>Styles:</b>
 * <dd>BORDER</dd>
 * <dt><b>Events:</b>
 * <dd>FocusIn, FocusOut, Help, KeyDown, KeyUp, MouseDoubleClick, MouseDown, MouseEnter,
 *     MouseExit, MouseHover, MouseUp, MouseMove, Move, Paint, Resize</dd>
 * </dl>
 * <p>
 * IMPORTANT: This class is intended to be subclassed <em>only</em>
 * within the SWT implementation.
 * </p>
 */

public abstract class Control extends Widget implements Drawable {
    /**
     * the handle to the OS resource
     * (Warning: This field is platform dependent)
     */
    public int handle;

    int hps;

    Composite parent;
    int drawCount, hCursor;
    int foreground, background;
//@@TODO(dmik)
//    Menu menu;
    String toolTipText;
    Object layoutData;
    Accessible accessible;

    static final short [] ACCENTS = new short [] {'~', '`', '\'', '^', '"'};

/**
 * Prevents uninitialized instances from being created outside the package.
 */
Control () {
}

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
 * @param parent a composite control which will be the parent of the new instance (cannot be null)
 * @param style the style of control to construct
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li>
 *    <li>ERROR_INVALID_SUBCLASS - if this class is not an allowed subclass</li>
 * </ul>
 *
 * @see SWT#BORDER
 * @see Widget#checkSubclass
 * @see Widget#getStyle
 */
public Control (Composite parent, int style) {
    super (parent, style);
    this.parent = parent;
    createWidget ();
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
public void addControlListener(ControlListener listener) {
    checkWidget ();
    if (listener == null) error (SWT.ERROR_NULL_ARGUMENT);
    TypedListener typedListener = new TypedListener (listener);
    addListener (SWT.Resize,typedListener);
    addListener (SWT.Move,typedListener);
}

/**
 * Adds the listener to the collection of listeners who will
 * be notified when the control gains or loses focus, by sending
 * it one of the messages defined in the <code>FocusListener</code>
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
 * @see FocusListener
 * @see #removeFocusListener
 */
public void addFocusListener (FocusListener listener) {
    checkWidget ();
    if (listener == null) error (SWT.ERROR_NULL_ARGUMENT);
    TypedListener typedListener = new TypedListener (listener);
    addListener (SWT.FocusIn,typedListener);
    addListener (SWT.FocusOut,typedListener);
}

/**
 * Adds the listener to the collection of listeners who will
 * be notified when help events are generated for the control,
 * by sending it one of the messages defined in the
 * <code>HelpListener</code> interface.
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
 * @see HelpListener
 * @see #removeHelpListener
 */
public void addHelpListener (HelpListener listener) {
    checkWidget ();
    if (listener == null) error (SWT.ERROR_NULL_ARGUMENT);
    TypedListener typedListener = new TypedListener (listener);
    addListener (SWT.Help, typedListener);
}

/**
 * Adds the listener to the collection of listeners who will
 * be notified when keys are pressed and released on the system keyboard, by sending
 * it one of the messages defined in the <code>KeyListener</code>
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
 * @see KeyListener
 * @see #removeKeyListener
 */
public void addKeyListener (KeyListener listener) {
    checkWidget ();
    if (listener == null) error (SWT.ERROR_NULL_ARGUMENT);
    TypedListener typedListener = new TypedListener (listener);
    addListener (SWT.KeyUp,typedListener);
    addListener (SWT.KeyDown,typedListener);
}

/**
 * Adds the listener to the collection of listeners who will
 * be notified when mouse buttons are pressed and released, by sending
 * it one of the messages defined in the <code>MouseListener</code>
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
 * @see MouseListener
 * @see #removeMouseListener
 */
public void addMouseListener (MouseListener listener) {
    checkWidget ();
    if (listener == null) error (SWT.ERROR_NULL_ARGUMENT);
    TypedListener typedListener = new TypedListener (listener);
    addListener (SWT.MouseDown,typedListener);
    addListener (SWT.MouseUp,typedListener);
    addListener (SWT.MouseDoubleClick,typedListener);
}

/**
 * Adds the listener to the collection of listeners who will
 * be notified when the mouse passes or hovers over controls, by sending
 * it one of the messages defined in the <code>MouseTrackListener</code>
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
 * @see MouseTrackListener
 * @see #removeMouseTrackListener
 */
public void addMouseTrackListener (MouseTrackListener listener) {
    checkWidget ();
    if (listener == null) error (SWT.ERROR_NULL_ARGUMENT);
    TypedListener typedListener = new TypedListener (listener);
    addListener (SWT.MouseEnter,typedListener);
    addListener (SWT.MouseExit,typedListener);
    addListener (SWT.MouseHover,typedListener);
}

/**
 * Adds the listener to the collection of listeners who will
 * be notified when the mouse moves, by sending it one of the
 * messages defined in the <code>MouseMoveListener</code>
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
 * @see MouseMoveListener
 * @see #removeMouseMoveListener
 */
public void addMouseMoveListener (MouseMoveListener listener) {
    checkWidget ();
    if (listener == null) error (SWT.ERROR_NULL_ARGUMENT);
    TypedListener typedListener = new TypedListener (listener);
    addListener (SWT.MouseMove,typedListener);
}

/**
 * Adds the listener to the collection of listeners who will
 * be notified when the receiver needs to be painted, by sending it
 * one of the messages defined in the <code>PaintListener</code>
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
 * @see PaintListener
 * @see #removePaintListener
 */
public void addPaintListener (PaintListener listener) {
    checkWidget ();
    if (listener == null) error (SWT.ERROR_NULL_ARGUMENT);
    TypedListener typedListener = new TypedListener (listener);
    addListener (SWT.Paint,typedListener);
}

/**
 * Adds the listener to the collection of listeners who will
 * be notified when traversal events occur, by sending it
 * one of the messages defined in the <code>TraverseListener</code>
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
 * @see TraverseListener
 * @see #removeTraverseListener
 */
public void addTraverseListener (TraverseListener listener) {
    checkWidget ();
    if (listener == null) error (SWT.ERROR_NULL_ARGUMENT);
    TypedListener typedListener = new TypedListener (listener);
    addListener (SWT.Traverse,typedListener);
}

abstract int callWindowProc (int msg, int mp1, int mp2);

/**
 * Returns the preferred size of the receiver.
 * <p>
 * The <em>preferred size</em> of a control is the size that it would
 * best be displayed at. The width hint and height hint arguments
 * allow the caller to ask a control questions such as "Given a particular
 * width, how high does the control need to be to show all of the contents?"
 * To indicate that the caller does not wish to constrain a particular
 * dimension, the constant <code>SWT.DEFAULT</code> is passed for the hint.
 * </p>
 *
 * @param wHint the width hint (can be <code>SWT.DEFAULT</code>)
 * @param hHint the height hint (can be <code>SWT.DEFAULT</code>)
 * @return the preferred size of the control
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @see Layout
 * @see #getBorderWidth
 * @see #getBounds
 * @see #getSize
 * @see #pack
 * @see "computeTrim, getClientArea for controls that implement them"
 */
public Point computeSize (int wHint, int hHint) {
    return computeSize (wHint, hHint, true);
}

/**
 * Returns the preferred size of the receiver.
 * <p>
 * The <em>preferred size</em> of a control is the size that it would
 * best be displayed at. The width hint and height hint arguments
 * allow the caller to ask a control questions such as "Given a particular
 * width, how high does the control need to be to show all of the contents?"
 * To indicate that the caller does not wish to constrain a particular
 * dimension, the constant <code>SWT.DEFAULT</code> is passed for the hint.
 * </p><p>
 * If the changed flag is <code>true</code>, it indicates that the receiver's
 * <em>contents</em> have changed, therefore any caches that a layout manager
 * containing the control may have been keeping need to be flushed. When the
 * control is resized, the changed flag will be <code>false</code>, so layout
 * manager caches can be retained.
 * </p>
 *
 * @param wHint the width hint (can be <code>SWT.DEFAULT</code>)
 * @param hHint the height hint (can be <code>SWT.DEFAULT</code>)
 * @param changed <code>true</code> if the control's contents have changed, and <code>false</code> otherwise
 * @return the preferred size of the control.
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @see Layout
 * @see #getBorderWidth
 * @see #getBounds
 * @see #getSize
 * @see #pack
 * @see "computeTrim, getClientArea for controls that implement them"
 */
public Point computeSize (int wHint, int hHint, boolean changed) {
    checkWidget ();
    int width = DEFAULT_WIDTH;
    int height = DEFAULT_HEIGHT;
    if (wHint != SWT.DEFAULT) width = wHint;
    if (hHint != SWT.DEFAULT) height = hHint;
    int border = getBorderWidth ();
    width += border * 2;
    height += border * 2;
    return new Point (width, height);
}

Control computeTabGroup () {
    if (isTabGroup ()) return this;
    return parent.computeTabGroup ();
}

Control computeTabRoot () {
    Control [] tabList = parent._getTabList ();
    if (tabList != null) {
        int index = 0;
        while (index < tabList.length) {
            if (tabList [index] == this) break;
            index++;
        }
        if (index == tabList.length) {
            if (isTabGroup ()) return this;
        }
    }
    return parent.computeTabRoot ();
}

Control [] computeTabList () {
    if (isTabGroup ()) {
        if (getVisible () && getEnabled ()) {
            return new Control [] {this};
        }
    }
    return new Control [0];
}

void createHandle () {
    handle = OS.WinCreateWindow (
        parent.handle,
        windowClass (),
        windowTitle (),
        widgetStyle (),
        0, parent.getHeight (), 0, 0,
        parent.handle,
        // to mimic the Windows behavior
        OS.HWND_BOTTOM, //OS.HWND_TOP,
        0,
        0,
        0
    );
// System.out.println("Class = " + windowClass ());
//@@TODO (lpino): Remove
//    System.out.println("ERROR = " + OS.WinGetLastError(parent.getDisplay ().hab));
    if (handle == 0) error (SWT.ERROR_NO_HANDLES);
//@@TODO (dmik):
//  we use the low word of the window handle as the
//  window identifier to make it unique to be used in WM_CONTROL and
//  similar message handlers to detect the hwnd of the window which sent the
//  notification. But there's no guarrantie that low 16 bits of the handle
//  make the unique value within the owner context... possibly we need a
//  separate table of unique identifiers.
    OS.WinSetWindowUShort (handle, OS.QWS_ID, (short)(handle & 0xFFFF));

//@@TODO (dmik): debug code, remove when no more necessary 
//System.out.println (
//    "Control.createHandle(): Window has been created:\n" +
//    "    hwnd = " + Integer.toHexString (handle) + "\n" +
//    "    hwnd.parent = hwnd.owner = " + Integer.toHexString (parent.handle) + "\n" +
//    "    class = " + windowClass() + "\n" +
//    "    style = " + Integer.toHexString (widgetStyle())
//);

//@@TODO(dmik): DBCS handling?
//    if (OS.IsDBLocale && parent != null) {
//        int hIMC = OS.ImmGetContext (hwndParent);
//        OS.ImmAssociateContext (handle, hIMC);
//        OS.ImmReleaseContext (hwndParent, hIMC);
//    }
}

void createWidget () {
    createHandle ();
    register ();
    subclass ();
    setDefaultFont ();
    setForegroundPixel (OS.CLR_DEFAULT);
    setBackgroundPixel (OS.CLR_DEFAULT);
}

int defaultBackground () {
    return OS.SYSCLR_DIALOGBACKGROUND;
}

//@@TODO(dmik)
//int defaultFont () {
//      Display display = getDisplay ();
//      return display.systemFont ();
//}

int defaultForeground () {
    return OS.SYSCLR_WINDOWTEXT;
}

void deregister () {
    WidgetTable.remove (handle);
}

void destroyWidget () {
    int hwnd = handle;
    releaseHandle ();
    if (hwnd != 0) {
//@@TODO (dmik): debug code, remove when no more necessary 
//System.out.println (
//    "Control.destroyWidget(): Window is being derstroyed:\n    hwnd = " +
//    Integer.toHexString (hwnd)
//);
        OS.WinDestroyWindow (hwnd);
    }
}

//@@TODO(dmik)
//void drawBackground (int hps) {
//    RECT rect = new RECT ();
//    OS.GetClientRect (handle, rect); //@@ WinQueryWindowRect
//    drawBackground (hDC, rect);
//}

void drawBackground (int hps, RECTL rcl) {
    /*
     * Optimizing hint: if zero is passed as hps then don't check for the palette
     * and use this.hps instead, assuming that the palette is already realized.
     * This hint should be used only when called from WM_PAINT handler, where
     * this.hps is not zero and was properly initialized by GC.
     */
    if (hps != 0) {
        Display display = getDisplay ();
        int hPalette = display.hPalette;
        if (hPalette != 0) {
            if (this.hps != hps)
                OS.GpiSelectPalette (hps, hPalette);
            int[] cclr = new int[1];
            OS.WinRealizePalette (handle, hps, cclr);
        }
    }
    else {
        hps = this.hps;
    }
    OS.WinFillRect (hps, rcl, getBackgroundPixel ());
}

//@@TODO(dmik)
//int findBrush (int pixel) {
//      return parent.findBrush (pixel);
//}

//@@TODO(dmik)
//int findCursor () {
//      if (hCursor != 0) return hCursor;
//      return parent.findCursor ();
//}

char findMnemonic (String string) {
    int index = 0;
    int length = string.length ();
    do {
        while (index < length && string.charAt (index) != Mnemonic) index++;
        if (++index >= length) return '\0';
        if (string.charAt (index) != Mnemonic) return string.charAt (index);
        index++;
    } while (index < length);
    return '\0';
}

//@@TODO(dmik): guess we don't need this on OS/2 since
//  disabling the focus window or hidihg it doesn't take the focus away.
//void fixFocus () {
//    Shell shell = getShell ();
//    Control control = this;
//    while ((control = control.parent) != null) {
//        if (control.setFocus () || control == shell) return;
//    }
//    OS.WinSetFocus (OS.HWND_DESKTOP, OS.HWND_DESKTOP);
//}

/**
 * Forces the receiver to have the <em>keyboard focus</em>, causing
 * all keyboard events to be delivered to it.
 *
 * @return <code>true</code> if the control got focus, and <code>false</code> if it was unable to.
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @see #setFocus
 */
public boolean forceFocus () {
    checkWidget ();
    Decorations shell = menuShell ();
    shell.setSavedFocus (this);
    if (!isEnabled () || !isVisible () || !isActive ()) return false;
    if (isFocusControl ()) return true;
    shell.bringToTop ();
//@@TODO (dmik): does it relate to OS/2?    
//    /*
//    * This code is intentionally commented.
//    *
//    * When setting focus to a control, it is
//    * possible that application code can set
//    * the focus to another control inside of
//    * WM_SETFOCUS.  In this case, the original
//    * control will no longer have the focus
//    * and the call to setFocus() will return
//    * false indicating failure.
//    *
//    * We are still working on a solution at
//    * this time.
//    */
////    if (OS.GetFocus () != OS.SetFocus (handle)) return false;
    OS.WinSetFocus (OS.HWND_DESKTOP, handle);
    return isFocusControl ();
}

/**
 * Returns the accessible object for the receiver.
 * If this is the first time this object is requested,
 * then the object is created and returned.
 *
 * @return the accessible object
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @see Accessible#addAccessibleListener
 * @see Accessible#addAccessibleControlListener
 *
 * @since 2.0
 */
public Accessible getAccessible () {
    checkWidget ();
    if (accessible == null) accessible = new_Accessible (this);
    return accessible;
}

/**
 * Returns the receiver's background color.
 *
 * @return the background color
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public Color getBackground () {
    checkWidget ();
    return Color.pm_new (getDisplay (), getBackgroundPixel ());
}

int getBackgroundPixel () {
    if (background == OS.CLR_DEFAULT) return defaultBackground ();
    return background;
}

/**
 * Returns the receiver's border width.
 *
 * @return the border width
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public int getBorderWidth () {
    checkWidget ();
    // Individual controls will use OS.CV_CXBORDER (or other) when appropriate
    return 0;
//@@TODO(dmik): remove
//    int bits = OS.GetWindowLong (handle, OS.GWL_EXSTYLE);
//    if ((bits & OS.WS_EX_CLIENTEDGE) != 0) return OS.GetSystemMetrics (OS.SM_CXEDGE);
//    if ((bits & OS.WS_EX_STATICEDGE) != 0) return OS.GetSystemMetrics (OS.SM_CXBORDER);
}

/*
 *  Helper method. Adjusts the SWP structure after querying the window position
 *  to flip the coordinate space horizontally. Returns the height of the parent.
 */
int getBounds (SWP swp) {
    if (parent != null) {
        if (parent.pswp != 0) {
//@@TODO(dmik): remove
//            int oldHdwp = parent.hdwp;
//            parent.hdwp = 0;
//            OS.EndDeferWindowPos (oldHdwp);
//            int count = parent.getChildrenCount ();
//            parent.hdwp = OS.BeginDeferWindowPos (count);
            endDeferWindowPos (parent);
            int count = parent.getChildrenCount ();
            beginDeferWindowPos (parent, count);
        }
    }
    int parentHeight = parent.getHeight();
    OS.WinQueryWindowPos (handle, swp);
    swp.y = parentHeight - (swp.y + swp.cy);
    return parentHeight;
}

/**
 * Returns a rectangle describing the receiver's size and location
 * relative to its parent (or its display if its parent is null).
 *
 * @return the receiver's bounding rectangle
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public Rectangle getBounds () {
    checkWidget ();
    SWP swp = new SWP ();
    getBounds (swp);
    return new Rectangle (swp.x, swp.y, swp.cx, swp.cy);
}

/**
 * Returns the display that the receiver was created on.
 *
 * @return the receiver's display
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public Display getDisplay () {
    Composite parent = this.parent;
    if (parent == null) error (SWT.ERROR_WIDGET_DISPOSED);
    return parent.getDisplay ();
}

/**
 * Returns <code>true</code> if the receiver is enabled, and
 * <code>false</code> otherwise. A disabled control is typically
 * not selectable from the user interface and draws with an
 * inactive or "grayed" look.
 *
 * @return the receiver's enabled state
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @see #isEnabled
 */
public boolean getEnabled () {
    checkWidget ();
    return OS.WinIsWindowEnabled (handle);
}

/**
 * Returns the font that the receiver will use to paint textual information.
 *
 * @return the receiver's font
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public Font getFont () {
    checkWidget ();
    byte[] fontBytes = new byte [OS.FACESIZE];
    int len = OS.WinQueryPresParam (
        handle, OS.PP_FONTNAMESIZE, 0, null, fontBytes.length, fontBytes, 0);
    String fontNameSize = len != 0 ? new PSZ (fontBytes).toString() : null;
    Display display = getDisplay ();
    FATTRS hFont = display.matchFont (fontNameSize); 
    return Font.pm_new (display, hFont);
}

/**
 * Returns the foreground color that the receiver will use to draw.
 *
 * @return the receiver's foreground color
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public Color getForeground () {
    checkWidget ();
    return Color.pm_new (getDisplay (), getForegroundPixel ());
}

int getForegroundPixel () {
    if (foreground == OS.CLR_DEFAULT) return defaultForeground ();
    return foreground;
}

int getHeight () {
    RECTL rcl = new RECTL ();
    OS.WinQueryWindowRect (handle, rcl);
    return rcl.yTop;
}

/**
 * Returns layout data which is associated with the receiver.
 *
 * @return the receiver's layout data
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public Object getLayoutData () {
    checkWidget ();
    return layoutData;
}

/**
 * Returns a point describing the receiver's location relative
 * to its parent (or its display if its parent is null).
 *
 * @return the receiver's location
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public Point getLocation () {
    checkWidget ();
    SWP swp = new SWP ();
    getBounds (swp);
    return new Point (swp.x, swp.y);
}

/**
 * Returns the receiver's pop up menu if it has one, or null
 * if it does not. All controls may optionally have a pop up
 * menu that is displayed when the user requests one for
 * the control. The sequence of key strokes, button presses
 * and/or button releases that are used to request a pop up
 * menu is platform specific.
 *
 * @return the receiver's menu
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
//@@TODO(dmik)
//public Menu getMenu () {
//      checkWidget ();
//      return menu;
//}

/**
 * Returns the receiver's parent, which must be a <code>Composite</code>
 * or null when the receiver is a shell that was created with null or
 * a display for a parent.
 *
 * @return the receiver's parent
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public Composite getParent () {
    checkWidget ();
    return parent;
}

Control [] getPath () {
    int count = 0;
    Shell shell = getShell ();
    Control control = this;
    while (control != shell) {
        count++;
        control = control.parent;
    }
    control = this;
    Control [] result = new Control [count];
    while (control != shell) {
        result [--count] = control;
        control = control.parent;
    }
    return result;
}

/**
 * Returns the receiver's shell. For all controls other than
 * shells, this simply returns the control's nearest ancestor
 * shell. Shells return themselves, even if they are children
 * of other shells.
 *
 * @return the receiver's shell
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @see #getParent
 */
public Shell getShell () {
    checkWidget ();
    return parent.getShell ();
}

/**
 * Returns a point describing the receiver's size. The
 * x coordinate of the result is the width of the receiver.
 * The y coordinate of the result is the height of the
 * receiver.
 *
 * @return the receiver's size
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public Point getSize () {
    checkWidget ();
    SWP swp = new SWP ();
    getBounds (swp);
    return new Point (swp.cx, swp.cy);
}

/**
 * Returns the receiver's tool tip text, or null if it has
 * not been set.
 *
 * @return the receiver's tool tip text
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public String getToolTipText () {
    checkWidget ();
    return toolTipText;
}

/**
 * Returns <code>true</code> if the receiver is visible, and
 * <code>false</code> otherwise.
 * <p>
 * If one of the receiver's ancestors is not visible or some
 * other condition makes the receiver not visible, this method
 * may still indicate that it is considered visible even though
 * it may not actually be showing.
 * </p>
 *
 * @return the receiver's visibility state
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public boolean getVisible () {
    checkWidget ();
    int bits = OS.WinQueryWindowULong (handle, OS.QWL_STYLE);
    return (bits & OS.WS_VISIBLE) != 0;
}

//@@TODO(dmik)
//boolean hasCursor () {
//    RECT rect = new RECT ();
//    if (!OS.GetClientRect (handle, rect)) return false;
//    if (OS.MapWindowPoints (handle, 0, rect, 2) == 0) return false;
//    POINT pt = new POINT ();
//    return (OS.GetCursorPos (pt) && OS.PtInRect (rect, pt));
//}

boolean hasFocus () {
    /*
    * If a non-SWT child of the control has focus,
    * then this control is considered to have focus
    * even though it does not have focus in OS/2.
    */
    int hwndFocus = OS.WinQueryFocus (OS.HWND_DESKTOP);
    while (hwndFocus != OS.NULLHANDLE) {
        if (hwndFocus == handle) return true;
        if (WidgetTable.get (hwndFocus) != null) {
            return false;
        }
        hwndFocus = OS.WinQueryWindow (hwndFocus, OS.QW_PARENT);
    }
    return false;
}

/**
 * Invokes platform specific functionality to allocate a new GC handle.
 * <p>
 * <b>IMPORTANT:</b> This method is <em>not</em> part of the public
 * API for <code>Control</code>. It is marked public only so that it
 * can be shared within the packages provided by SWT. It is not
 * available on all platforms, and should never be called from
 * application code.
 * </p>
 *
 * @param data the platform specific GC data
 * @return the platform specific GC handle
 *
 * @private
 */
public int internal_new_GC (GCData data) {
    checkWidget();
    int hps;
    int hwnd = (data == null || data.hwnd == 0) ? handle : data.hwnd;
    if (data == null || data.rcl == null) {
        hps = OS.WinGetPS (hwnd);
    } else {
        hps = OS.WinBeginPaint (hwnd, data.hps, data.rcl);
    }
    if (hps == 0) SWT.error(SWT.ERROR_NO_HANDLES);
    if (data != null) {
        Display display = getDisplay ();
        data.device = display;
        data.hdc = OS.GpiQueryDevice (hps);
        if (data.hdc == 0) SWT.error(SWT.ERROR_NO_HANDLES);
        data.foreground = getForegroundPixel ();
        data.background = getBackgroundPixel ();
        byte[] fontBytes = new byte [OS.FACESIZE];
        int len = OS.WinQueryPresParam (
            hwnd, OS.PP_FONTNAMESIZE, 0, null, fontBytes.length, fontBytes, 0);
        String fontNameSize = len >= 0 ? new PSZ (fontBytes).toString() : null;
        data.hFont = display.matchFont (fontNameSize); 
        data.hwnd = hwnd;
    }
    return hps;
}

/**
 * Invokes platform specific functionality to dispose a GC handle.
 * <p>
 * <b>IMPORTANT:</b> This method is <em>not</em> part of the public
 * API for <code>Control</code>. It is marked public only so that it
 * can be shared within the packages provided by SWT. It is not
 * available on all platforms, and should never be called from
 * application code.
 * </p>
 *
 * @param handle the platform specific GC handle
 * @param data the platform specific GC data
 *
 * @private
 */
public void internal_dispose_GC (int hps, GCData data) {
    checkWidget ();
    if (data == null || data.rcl == null) {
        OS.WinReleasePS (hps);
    } else {
        OS.WinEndPaint (hps);
    }
}

boolean isActive () {
    Display display = getDisplay ();
    Shell modal = display.getModalShell ();
    if (modal != null && modal != this) {
        if ((modal.style & SWT.PRIMARY_MODAL) != 0) {
            Shell shell = getShell ();
            if (modal.parent == shell) {
                return false;
            }
        }
        int bits = SWT.APPLICATION_MODAL | SWT.SYSTEM_MODAL;
        if ((modal.style & bits) != 0) {
            Control control = this;
            while (control != null) {
                if (control == modal) break;
                control = control.parent;
            }
            if (control != modal) return false;
        }
    }
    return getShell ().getEnabled ();
}

public boolean isDisposed () {
    return handle == 0;
}

/**
 * Returns <code>true</code> if the receiver is enabled and all
 * of the receiver's ancestors are enabled, and <code>false</code>
 * otherwise. A disabled control is typically not selectable from the
 * user interface and draws with an inactive or "grayed" look.
 *
 * @return the receiver's enabled state
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @see #getEnabled
 */
public boolean isEnabled () {
    checkWidget ();
    return getEnabled () && parent.isEnabled ();
}

/**
 * Returns <code>true</code> if the receiver has the user-interface
 * focus, and <code>false</code> otherwise.
 *
 * @return the receiver's focus state
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public boolean isFocusControl () {
    checkWidget ();
    return hasFocus ();
}

//@@TODO(dmik): guess we don't need this on OS/2 since
//  disabling the focus window or hidihg it doesn't take the focus away.
//boolean isFocusAncestor () {
//    Display display = getDisplay ();
//    Control control = display.getFocusControl ();
//    while (control != null && control != this) {
//        control = control.parent;
//    }
//    return control == this;
//}

/**
 * Returns <code>true</code> if the underlying operating
 * system supports this reparenting, otherwise <code>false</code>
 *
 * @return <code>true</code> if the widget can be reparented, otherwise <code>false</code>
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
//@@TODO(dmik)
//public boolean isReparentable () {
//    checkWidget ();
//    return true;
//}

boolean isShowing () {
    return OS.WinIsWindowShowing (handle);
//@@TODO(dmik): what to do with this:
//    /*
//    * This is not complete.  Need to check if the
//    * widget is obscurred by a parent or sibling.
//    */
//    if (!isVisible ()) return false;
//    Control control = this;
//    while (control != null) {
//        Point size = control.getSize ();
//        if (size.x == 0 || size.y == 0) {
//            return false;
//        }
//        control = control.parent;
//    }
//    return true;
//    /*
//    * Check to see if current damage is included.
//    */
//    if (!OS.IsWindowVisible (handle)) return false;
//    int flags = OS.DCX_CACHE | OS.DCX_CLIPCHILDREN | OS.DCX_CLIPSIBLINGS;
//    int hDC = OS.GetDCEx (handle, 0, flags);
//    int result = OS.GetClipBox (hDC, new RECT ());
//    OS.ReleaseDC (handle, hDC);
//    return result != OS.NULLREGION;
}

boolean isTabGroup () {
    Control [] tabList = parent._getTabList ();
    if (tabList != null) {
        for (int i=0; i<tabList.length; i++) {
            if (tabList [i] == this) return true;
        }
    }
    int bits = OS.WinQueryWindowULong (handle, OS.QWL_STYLE);
    return (bits & OS.WS_TABSTOP) != 0;
}

boolean isTabItem () {
    Control [] tabList = parent._getTabList ();
    if (tabList != null) {
        for (int i=0; i<tabList.length; i++) {
            if (tabList [i] == this) return false;
        }
    }
    int bits = OS.WinQueryWindowULong (handle, OS.QWL_STYLE);
    if ((bits & OS.WS_TABSTOP) != 0) return false;
    
    int code = OS.WinSendMsg (handle, OS.WM_QUERYDLGCODE, 0, 0);
    if ((code & OS.DLGC_STATIC) != 0) return false;
    if ((code & OS.DLGC_MLE) != 0) return false;
    return true;
}

/**
 * Returns <code>true</code> if the receiver is visible and all
 * of the receiver's ancestors are visible and <code>false</code>
 * otherwise.
 *
 * @return the receiver's visibility state
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @see #getVisible
 */
public boolean isVisible () {
    checkWidget ();
    return OS.WinIsWindowVisible (handle);
}

Decorations menuShell () {
    return parent.menuShell ();
}

boolean mnemonicHit (char key) {
    return false;
}

boolean mnemonicMatch (char key) {
    return false;
}

/**
 * Moves the receiver above the specified control in the
 * drawing order. If the argument is null, then the receiver
 * is moved to the top of the drawing order. The control at
 * the top of the drawing order will not be covered by other
 * controls even if they occupy intersecting areas.
 *
 * @param the sibling control (or null)
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_INVALID_ARGUMENT - if the control has been disposed</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
//@@TODO(dmik)
//public void moveAbove (Control control) {
//    checkWidget ();
//    int hwndAbove = OS.HWND_TOP;
//    if (control != null) {
//        if (control.isDisposed ()) error(SWT.ERROR_INVALID_ARGUMENT);
//        if (parent != control.parent) return;
//        int hwnd = control.handle;
//        if (hwnd == 0 || hwnd == handle) return;
//        hwndAbove = OS.GetWindow (hwnd, OS.GW_HWNDPREV);
//        /*
//        * Bug in Windows.  For some reason, when GetWindow ()
//        * with GW_HWNDPREV is used to query the previous window
//        * in the z-order with the first child, Windows returns
//        * the first child instead of NULL.  The fix is to detect
//        * this case and move the control to the top.
//        */
//        if (hwndAbove == 0 || hwndAbove == hwnd) {
//            hwndAbove = OS.HWND_TOP;
//        }
//    }
//    int flags = OS.SWP_NOSIZE | OS.SWP_NOMOVE | OS.SWP_NOACTIVATE;
//    OS.SetWindowPos (handle, hwndAbove, 0, 0, 0, 0, flags);
//}

/**
 * Moves the receiver below the specified control in the
 * drawing order. If the argument is null, then the receiver
 * is moved to the bottom of the drawing order. The control at
 * the bottom of the drawing order will be covered by all other
 * controls which occupy intersecting areas.
 *
 * @param the sibling control (or null)
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_INVALID_ARGUMENT - if the control has been disposed</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
//@@TODO(dmik)
//public void moveBelow (Control control) {
//    checkWidget ();
//    int hwndAbove = OS.HWND_BOTTOM;
//    if (control != null) {
//        if (control.isDisposed ()) error(SWT.ERROR_INVALID_ARGUMENT);
//        if (parent != control.parent) return;
//        hwndAbove = control.handle;
//    }
//    if (hwndAbove == 0 || hwndAbove == handle) return;
//    int flags = OS.SWP_NOSIZE | OS.SWP_NOMOVE | OS.SWP_NOACTIVATE;
//    OS.SetWindowPos (handle, hwndAbove, 0, 0, 0, 0, flags);
//}

Accessible new_Accessible (Control control) {
    return Accessible.internal_new_Accessible (this);
}

/**
 * Causes the receiver to be resized to its preferred size.
 * For a composite, this involves computing the preferred size
 * from its layout, if there is one.
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @see #computeSize
 */
public void pack () {
    checkWidget ();
    pack (true);
}

/**
 * Causes the receiver to be resized to its preferred size.
 * For a composite, this involves computing the preferred size
 * from its layout, if there is one.
 * <p>
 * If the changed flag is <code>true</code>, it indicates that the receiver's
 * <em>contents</em> have changed, therefore any caches that a layout manager
 * containing the control may have been keeping need to be flushed. When the
 * control is resized, the changed flag will be <code>false</code>, so layout
 * manager caches can be retained.
 * </p>
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @see #computeSize
 */
public void pack (boolean changed) {
    checkWidget ();
    setSize (computeSize (SWT.DEFAULT, SWT.DEFAULT, changed));
}

/* convert all Eclipse mnemonic prefixes to OS/2 mnemonic prefixes */
String patchMnemonics (String string) {
    StringBuffer buf = new StringBuffer (string);
    /* the last mnemonic char takes precedence over others if any */
    int mnemonicIdx = string.lastIndexOf (Mnemonic);
    boolean lastIsMnemonic = false;
    for (int i = 0; i < buf.length(); i++) {
        boolean isMnemonic = buf.charAt (i) == Mnemonic;
        if (lastIsMnemonic) {
            if (isMnemonic) {
                buf.deleteCharAt (i--);
                if (mnemonicIdx >= 0) mnemonicIdx --;
                lastIsMnemonic = false;
                continue;
            } else {
                if ((i - 1) == mnemonicIdx) {
                    buf.replace (i - 1, i, "~");
                    mnemonicIdx = -1;
                } else {
                    buf.deleteCharAt ((i--) - 1);
                    if (mnemonicIdx >= 0) mnemonicIdx --;
                }
            }
        }
        if (!isMnemonic) {
            if (buf.charAt (i) == '~') {
                buf.insert (i++, '~');
                if (mnemonicIdx >= 0) mnemonicIdx ++;
            }
        }
        lastIsMnemonic = isMnemonic;
    }
    if (lastIsMnemonic) {
        buf.setLength (buf.length() - 1);
    }
    return buf.toString();
}

/**
 * Causes the entire bounds of the receiver to be marked
 * as needing to be redrawn. The next time a paint request
 * is processed, the control will be completely painted.
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @see #update
 */
public void redraw () {
    checkWidget ();
    if (!OS.WinIsWindowVisible (handle)) return;
//@@TODO (dmik): remove    
//    int flags = OS.RDW_ERASE | OS.RDW_FRAME | OS.RDW_INVALIDATE;
//    OS.RedrawWindow (handle, null, 0, flags);
//@@TODO(dmik): should we always invalidate children?
    OS.WinInvalidateRect (handle, null, false);
}

/**
 * Causes the rectangular area of the receiver specified by
 * the arguments to be marked as needing to be redrawn.
 * The next time a paint request is processed, that area of
 * the receiver will be painted. If the <code>all</code> flag
 * is <code>true</code>, any children of the receiver which
 * intersect with the specified area will also paint their
 * intersecting areas. If the <code>all</code> flag is
 * <code>false</code>, the children will not be painted.
 *
 * @param x the x coordinate of the area to draw
 * @param y the y coordinate of the area to draw
 * @param width the width of the area to draw
 * @param height the height of the area to draw
 * @param all <code>true</code> if children should redraw, and <code>false</code> otherwise
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @see #update
 */
public void redraw (int x, int y, int width, int height, boolean all) {
    checkWidget ();
    if (width <= 0 || height <= 0) return;
    if (!OS.WinIsWindowVisible (handle)) return;
    RECTL rcl = new RECTL ();
    rcl.xLeft = x; rcl.xRight = x + width;
    rcl.yTop = getHeight () - y; rcl.yBottom = rcl.yTop - height;
    OS.WinInvalidateRect (handle, rcl, all);
}

void register () {
    WidgetTable.put (handle, this);
}

void releaseHandle () {
    handle = 0;
}

void releaseWidget () {
    // release a long-term cached micro presentation space
    if (hps != 0) {
        if (getDisplay().hPalette != 0)
            OS.GpiSelectPalette (hps, OS.NULLHANDLE);
        internal_dispose_GC (hps, null);
    }
    super.releaseWidget ();
//@@TODO(dmik)
//    if (OS.IsDBLocale) {
//        OS.ImmAssociateContext (handle, 0);
//    }
//    if (toolTipText != null) {
//        Shell shell = getShell ();
//        shell.setToolTipText (handle, null);
//    }
    toolTipText = null;
//@@TODO(dmik)
//    if (menu != null && !menu.isDisposed ()) {
//            menu.dispose ();
//    }
//    menu = null;
    deregister ();
    unsubclass ();
    parent = null;
    layoutData = null;
//@@TODO(dmik)
//    if (accessible != null) {
//        accessible.internal_dispose_Accessible ();
//    }
//    accessible = null;
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
    eventTable.unhook (SWT.Resize, listener);
}

/**
 * Removes the listener from the collection of listeners who will
 * be notified when the control gains or loses focus.
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
 * @see FocusListener
 * @see #addFocusListener
 */
public void removeFocusListener(FocusListener listener) {
    checkWidget ();
    if (listener == null) error (SWT.ERROR_NULL_ARGUMENT);
    if (eventTable == null) return;
    eventTable.unhook (SWT.FocusIn, listener);
    eventTable.unhook (SWT.FocusOut, listener);
}

/**
 * Removes the listener from the collection of listeners who will
 * be notified when the help events are generated for the control.
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
 * @see HelpListener
 * @see #addHelpListener
 */
public void removeHelpListener (HelpListener listener) {
    checkWidget ();
    if (listener == null) error (SWT.ERROR_NULL_ARGUMENT);
    if (eventTable == null) return;
    eventTable.unhook (SWT.Help, listener);
}

/**
 * Removes the listener from the collection of listeners who will
 * be notified when keys are pressed and released on the system keyboard.
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
 * @see KeyListener
 * @see #addKeyListener
 */
public void removeKeyListener(KeyListener listener) {
    checkWidget ();
    if (listener == null) error (SWT.ERROR_NULL_ARGUMENT);
    if (eventTable == null) return;
    eventTable.unhook (SWT.KeyUp, listener);
    eventTable.unhook (SWT.KeyDown, listener);
}

/**
 * Removes the listener from the collection of listeners who will
 * be notified when the mouse passes or hovers over controls.
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
 * @see MouseTrackListener
 * @see #addMouseTrackListener
 */
public void removeMouseTrackListener(MouseTrackListener listener) {
    checkWidget ();
    if (listener == null) error (SWT.ERROR_NULL_ARGUMENT);
    if (eventTable == null) return;
    eventTable.unhook (SWT.MouseEnter, listener);
    eventTable.unhook (SWT.MouseExit, listener);
    eventTable.unhook (SWT.MouseHover, listener);
}

/**
 * Removes the listener from the collection of listeners who will
 * be notified when mouse buttons are pressed and released.
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
 * @see MouseListener
 * @see #addMouseListener
 */
public void removeMouseListener (MouseListener listener) {
    checkWidget ();
    if (listener == null) error (SWT.ERROR_NULL_ARGUMENT);
    if (eventTable == null) return;
    eventTable.unhook (SWT.MouseDown, listener);
    eventTable.unhook (SWT.MouseUp, listener);
    eventTable.unhook (SWT.MouseDoubleClick, listener);
}

/**
 * Removes the listener from the collection of listeners who will
 * be notified when the mouse moves.
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
 * @see MouseMoveListener
 * @see #addMouseMoveListener
 */
public void removeMouseMoveListener(MouseMoveListener listener) {
    checkWidget ();
    if (listener == null) error (SWT.ERROR_NULL_ARGUMENT);
    if (eventTable == null) return;
    eventTable.unhook (SWT.MouseMove, listener);
}

/**
 * Removes the listener from the collection of listeners who will
 * be notified when the receiver needs to be painted.
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
 * @see PaintListener
 * @see #addPaintListener
 */
public void removePaintListener(PaintListener listener) {
    checkWidget ();
    if (listener == null) error (SWT.ERROR_NULL_ARGUMENT);
    if (eventTable == null) return;
    eventTable.unhook(SWT.Paint, listener);
}

/**
 * Removes the listener from the collection of listeners who will
 * be notified when traversal events occur.
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
 * @see TraverseListener
 * @see #addTraverseListener
 */
public void removeTraverseListener(TraverseListener listener) {
    checkWidget ();
    if (listener == null) error (SWT.ERROR_NULL_ARGUMENT);
    if (eventTable == null) return;
    eventTable.unhook (SWT.Traverse, listener);
}

boolean sendKeyEvent (int type, int msg, int mp1, int mp2) {
    Event event = new Event ();
    
    switch (msg) {
        case OS.WM_CHAR: {
            short fsflags = OS.SHORT1FROMMP (mp1);
            short usch = OS.SHORT1FROMMP (mp2);
            short usvk = OS.SHORT2FROMMP (mp2);
            
            /* Ignore repeating modifier keys by testing key prev. down state */
            if (
                (usvk == OS.VK_SHIFT) || (usvk == OS.VK_ALT) ||
                (usvk == OS.VK_ALTGRAF) || (usvk == OS.VK_CTRL) ||
                (usvk == OS.VK_CAPSLOCK) || (usvk == OS.VK_NUMLOCK) ||
                (usvk == OS.VK_SCRLLOCK)
            ) {
                if ((fsflags & OS.KC_PREVDOWN) != 0) return true;
            }
//@@TODO (dmik): getting a character for DBCS charsets below will not work...            
            /* Use the character encoding for the default locale */                
            event.character = new String (new byte[] {(byte) usch}).charAt (0);
            
            if ((fsflags & OS.KC_VIRTUALKEY) != 0) {
                if (usvk == OS.VK_DELETE) {
                    /* 
                     * A special case: map VK_DELETE to an ASCII char (127)
                     * instead of treating it as a virtual key.
                     */
                    event.character = 127;
                } else {
                    event.keyCode = Display.translateKey (usvk);
                    /* 
                     * Feature in OS/2. The character for cursor move, edit
                     * and some other virtual keys actually equals to
                     * (<scan> << 8) | 0xEO, but we need zero as the character
                     * value for these keys.
                     */
                    if (event.keyCode != 0)
                        event.character = 0;
                }
            }
        }
    }

    if (event.keyCode == 0 && event.character == 0)
        return true;

    if (!setInputState (event, type)) return true;
    
    return sendKeyEvent (type, msg, mp1, mp2, event);
}

boolean sendKeyEvent (int type, int msg, int mp1, int mp2, Event event) {
    postEvent (type, event);
    return true;
}

//@@TODO(dmik): later
//boolean sendMouseEvent (int type, int button, int msg, int wParam, int lParam) {
//    Event event = new Event ();
//    event.button = button;
//    event.x = (short) (lParam & 0xFFFF);
//    event.y = (short) (lParam >> 16);
//    if (OS.GetKeyState (OS.VK_MENU) < 0) event.stateMask |= SWT.ALT;
//    if ((wParam & OS.MK_SHIFT) != 0) event.stateMask |= SWT.SHIFT;
//    if ((wParam & OS.MK_CONTROL) != 0) event.stateMask |= SWT.CONTROL;
//    if ((wParam & OS.MK_LBUTTON) != 0) event.stateMask |= SWT.BUTTON1;
//    if ((wParam & OS.MK_MBUTTON) != 0) event.stateMask |= SWT.BUTTON2;
//    if ((wParam & OS.MK_RBUTTON) != 0) event.stateMask |= SWT.BUTTON3;
//    switch (type) {
//        case SWT.MouseDown:
//        case SWT.MouseDoubleClick:
//            if (button == 1) event.stateMask &= ~SWT.BUTTON1;
//            if (button == 2) event.stateMask &= ~SWT.BUTTON2;
//            if (button == 3) event.stateMask &= ~SWT.BUTTON3;
//            break;
//        case SWT.MouseUp:
//            if (button == 1) event.stateMask |= SWT.BUTTON1;
//            if (button == 2) event.stateMask |= SWT.BUTTON2;
//            if (button == 3) event.stateMask |= SWT.BUTTON3;
//            break;
//    }
//    return sendMouseEvent (type, msg, wParam, lParam, event);
//}

//@@TODO(dmik)
//boolean sendMouseEvent (int type, int msg, int wParam, int lParam, Event event) {
//    postEvent (type, event);
//    return true;
//}

/**
 * Sets the receiver's background color to the color specified
 * by the argument, or to the default system color for the control
 * if the argument is null.
 *
 * @param color the new color (or null)
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_INVALID_ARGUMENT - if the argument has been disposed</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void setBackground (Color color) {
    checkWidget ();
    int pixel = OS.CLR_DEFAULT;
    if (color != null) {
        if (color.isDisposed()) SWT.error(SWT.ERROR_INVALID_ARGUMENT);
        pixel = color.handle;
    }
    setBackgroundPixel (pixel);
}

void setBackgroundPixel (int pixel) {
    if (background == pixel) return;
    background = pixel;
    setPresBackground();
    /*
     *  OS/2 feature. Invalidate children also, because some of them
     *  can be using this parent's parameter when painting themselves.
     */
    OS.WinInvalidateRect (handle, null, true);
}

/**
 * Sets the receiver's size and location to the rectangular
 * area specified by the arguments. The <code>x</code> and
 * <code>y</code> arguments are relative to the receiver's
 * parent (or its display if its parent is null).
 * <p>
 * Note: Attempting to set the width or height of the
 * receiver to a negative number will cause that
 * value to be set to zero instead.
 * </p>
 *
 * @param x the new x coordinate for the receiver
 * @param y the new y coordinate for the receiver
 * @param width the new width for the receiver
 * @param height the new height for the receiver
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void setBounds (int x, int y, int width, int height) {
    checkWidget ();
    setBounds (x, y, Math.max (0, width), Math.max (0, height),
        OS.SWP_MOVE | OS.SWP_SIZE);
}

static void beginDeferWindowPos (Composite parent, int count) {
    parent.pswp = OS.malloc (SWP.sizeof * count);
    OS.memset (parent.pswp, 0, SWP.sizeof * count);
    parent.pswpHandles = new int [count];
}

static void endDeferWindowPos (Composite parent) {
    int pswp = parent.pswp;
    int count = parent.pswpHandles.length;
    int hab = parent.getDisplay ().hab;
    int diff = 0;
    //@@TODO(dmik): do we really need the code below?
    for (int i = 0; i < count; i++) {
        if (!OS.WinIsWindow (hab, parent.pswpHandles[i])) {
            int j = i - diff;
            OS.memcpy (
                pswp + (SWP.sizeof * j),
                pswp + (SWP.sizeof * (j + 1)),
                SWP.sizeof * (count - i - 1));
            diff++;
        }
    }
    count -= diff;
    parent.pswp = 0;
    parent.pswpHandles = null;
    OS.WinSetMultWindowPos (hab,  pswp, count);
    OS.free (pswp);
}

static void deferWindowPos (Composite parent, SWP swp) {
    int i, free = -1;
    int count = parent.pswpHandles.length;
    for (i = 0; i < count; i++) {
        if (parent.pswpHandles[i] == swp.hwnd) break;
        if (parent.pswpHandles[i] == 0 && free < 0) free = i;
    }
    if (i == count) {
        if (free >= 0) {
            i = free;
        } else {
            int pswpNew = OS.malloc (SWP.sizeof * (count + 4));
            OS.memcpy (pswpNew, parent.pswp, SWP.sizeof * count);
            OS.memset (pswpNew + SWP.sizeof * count, 0, SWP.sizeof * 4);
            int[] pswpHandlesNew = new int [count + 4];
            System.arraycopy (parent.pswpHandles, 0, pswpHandlesNew, 0, count);
            OS.free (parent.pswp);
            parent.pswp = pswpNew;
            parent.pswpHandles = pswpHandlesNew;
            i = count;
        }
        parent.pswpHandles[i] = swp.hwnd;
    }
    OS.objcpy (parent.pswp + (SWP.sizeof * i), swp);
}

static int getDeferredWindowPos (Composite parent, SWP swp) {
    int i;
    int count = parent.pswpHandles.length;
    for (i = 0; i < count; i++) {
        if (parent.pswpHandles[i] == swp.hwnd) break;
    }
    if (i == count) {
        return -1;
    }
    int parentHeight = parent.getHeight ();
    OS.objcpy (swp, parent.pswp + (SWP.sizeof * i));
    swp.y = parentHeight - (swp.y + swp.cy);
    return parentHeight;
}

void setBounds (int x, int y, int width, int height, int flags) {
    SWP swp = new SWP ();
    int parentHeight = -1;
    if (parent != null && parent.pswp != 0) {
        parentHeight = getDeferredWindowPos (parent, swp);
    }
    if (parentHeight == -1) {
        parentHeight = getBounds (swp);
    }
    if ((flags & OS.SWP_MOVE) != 0) {
        swp.x = x;
        swp.y = y;
    }
    if ((flags & OS.SWP_SIZE) != 0) {
        swp.cx = width;
        swp.cy = height;
        flags |= OS.SWP_MOVE;
    }
    swp.y = parentHeight - (swp.y + swp.cy);
    if (parent == null) {
        OS.WinSetWindowPos (handle, 0, swp.x, swp.y, swp.cx, swp.cy, flags);
        return;
    }
    swp.hwnd = handle;
    swp.fl = flags;
    int count = parent.getChildrenCount ();
    if (parent.pswp == 0) {
//@@TODO(dmik)
//        if (count > 1) {
//            int bits = OS.GetWindowLong (handle, OS.GWL_STYLE);
//            if ((bits & OS.WS_CLIPSIBLINGS) == 0) flags |= OS.SWP_NOCOPYBITS;
//        }
//        OS.SetWindowPos (handle, 0, x, y, width, height, flags);
        OS.WinSetWindowPos (handle, 0, swp.x, swp.y, swp.cx, swp.cy, flags);
        return;
    }
//@@TODO(dmik): do we need to return some value from deferWindowPos()
//              to force structures to be reallocated?
//    int hdwp = OS.DeferWindowPos (parent.hdwp, handle, 0, x, y, width, height, flags);
//    if (hdwp == 0) {
//        int oldHdwp = parent.hdwp;
//        parent.hdwp = 0;
//        OS.EndDeferWindowPos (oldHdwp);
//        if (count > 1) hdwp = OS.BeginDeferWindowPos (count);
//    }
//    parent.hdwp = hdwp;
    deferWindowPos (parent, swp);
}

/**
 * Sets the receiver's size and location to the rectangular
 * area specified by the argument. The <code>x</code> and
 * <code>y</code> fields of the rectangle are relative to
 * the receiver's parent (or its display if its parent is null).
 * <p>
 * Note: Attempting to set the width or height of the
 * receiver to a negative number will cause that
 * value to be set to zero instead.
 * </p>
 *
 * @param rect the new bounds for the receiver
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void setBounds (Rectangle rect) {
    checkWidget ();
    if (rect == null) error (SWT.ERROR_NULL_ARGUMENT);
    setBounds (rect.x, rect.y, rect.width, rect.height);
}

/**
 * If the argument is <code>true</code>, causes the receiver to have
 * all mouse events delivered to it until the method is called with
 * <code>false</code> as the argument.
 *
 * @param capture <code>true</code> to capture the mouse, and <code>false</code> to release it
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
//@@TODO(dmik)
//public void setCapture (boolean capture) {
//    checkWidget ();
//    if (capture) {
//        OS.SetCapture (handle);
//    } else {
//        if (OS.GetCapture () == handle) {
//                OS.ReleaseCapture ();
//        }
//    }
//}

//@@TODO(dmik)
///**
// * Sets the receiver's cursor to the cursor specified by the
// * argument, or to the default cursor for that kind of control
// * if the argument is null.
// * <p>
// * When the mouse pointer passes over a control its appearance
// * is changed to match the control's cursor.
// * </p>
// *
// * @param cursor the new cursor (or null)
// *
// * @exception IllegalArgumentException <ul>
// *    <li>ERROR_INVALID_ARGUMENT - if the argument has been disposed</li>
// * </ul>
// * @exception SWTException <ul>
// *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
// *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
// * </ul>
// */
//public void setCursor (Cursor cursor) {
//    checkWidget ();
//    hCursor = 0;
//    if (cursor != null) {
//        if (cursor.isDisposed()) SWT.error(SWT.ERROR_INVALID_ARGUMENT);
//        hCursor = cursor.handle;
//    }
//    int hwndCursor = OS.GetCapture ();
//    if (hwndCursor == 0) {
//        POINT pt = new POINT ();
//        if (!OS.GetCursorPos (pt)) return;
//        int hwnd = hwndCursor = OS.WindowFromPoint (pt);
//        while (hwnd != 0 && hwnd != handle) {
//            hwnd = OS.GetParent (hwnd);
//        }
//        if (hwnd == 0) return;
//    }
//    int lParam = OS.HTCLIENT | (OS.WM_MOUSEMOVE << 16);
//    OS.SendMessage (hwndCursor, OS.WM_SETCURSOR, hwndCursor, lParam);
//}

void setDefaultFont () {
    Display display = getDisplay ();
    String fontName = display.composeFontNameSize (display.systemFont());
    byte[] fontBytes = new PSZ (fontName).getBytes();
    OS.WinSetPresParam (handle, OS.PP_FONTNAMESIZE, fontBytes.length, fontBytes); 
}

/**
 * Enables the receiver if the argument is <code>true</code>,
 * and disables it otherwise. A disabled control is typically
 * not selectable from the user interface and draws with an
 * inactive or "grayed" look.
 *
 * @param enabled the new enabled state
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void setEnabled (boolean enabled) {
    checkWidget ();
    OS.WinEnableWindow (handle, enabled);

//@@TODO(dmik): guess we don't need this on OS/2 since
//  disabling the focus window or hidihg it doesn't take the focus away.
//    /*
//    * Feature in Windows.  If the receiver has focus, disabling
//    * the receiver causes no window to have focus.  The fix is
//    * to assign focus to the first ancestor window that takes
//    * focus.  If no window will take focus, set focus to the
//    * desktop.
//    */
//    boolean fixFocus = false;
//    if (!enabled) fixFocus = isFocusAncestor ();
//    OS.EnableWindow (handle, enabled);
//    if (fixFocus) fixFocus ();
}

/**
 * Causes the receiver to have the <em>keyboard focus</em>,
 * such that all keyboard events will be delivered to it.
 *
 * @return <code>true</code> if the control got focus, and <code>false</code> if it was unable to.
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @see #forceFocus
 */
public boolean setFocus () {
    checkWidget ();
    return forceFocus ();
}

/**
 * Sets the font that the receiver will use to paint textual information
 * to the font specified by the argument, or to the default font for that
 * kind of control if the argument is null.
 *
 * @param font the new font (or null)
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_INVALID_ARGUMENT - if the argument has been disposed</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void setFont (Font font) {
    checkWidget ();
    FATTRS hFont;
    Display display = getDisplay ();
    if (font != null) {
        if (font.isDisposed()) SWT.error(SWT.ERROR_INVALID_ARGUMENT);
        hFont = font.internal_get_handle();
    } else {
        hFont = display.systemFont();
    }
    String fontName = display.composeFontNameSize (hFont);
    byte[] fontBytes = new PSZ (fontName).getBytes();
    OS.WinSetPresParam (handle, OS.PP_FONTNAMESIZE, fontBytes.length, fontBytes); 
}

/**
 * Sets the receiver's foreground color to the color specified
 * by the argument, or to the default system color for the control
 * if the argument is null.
 *
 * @param color the new color (or null)
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_INVALID_ARGUMENT - if the argument has been disposed</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void setForeground (Color color) {
    checkWidget ();
    int pixel = OS.CLR_DEFAULT;
    if (color != null) {
        if (color.isDisposed()) SWT.error(SWT.ERROR_INVALID_ARGUMENT);
        pixel = color.handle;
    }
    setForegroundPixel (pixel);
}

void setForegroundPixel (int pixel) {
    if (foreground == pixel) return;
    foreground = pixel;
    setPresForeground ();
    /*
     *  OS/2 feature. Invalidate children also, because some of them
     *  can be using this parent's parameter when painting themselves.
     */
    OS.WinInvalidateRect (handle, null, true);
}

boolean setInputState (Event event, int type) {
    if ((OS.WinGetKeyState (OS.HWND_DESKTOP, OS.VK_ALT) & 0x8000) != 0)
        event.stateMask |= SWT.ALT;
    if ((OS.WinGetKeyState (OS.HWND_DESKTOP, OS.VK_ALTGRAF) & 0x8000) != 0)
        event.stateMask |= SWT.ALT;
    if ((OS.WinGetKeyState (OS.HWND_DESKTOP, OS.VK_SHIFT) & 0x8000) != 0)
        event.stateMask |= SWT.SHIFT;
    if ((OS.WinGetKeyState (OS.HWND_DESKTOP, OS.VK_CTRL) & 0x8000) != 0)
        event.stateMask |= SWT.CONTROL;
    if ((OS.WinGetKeyState (OS.HWND_DESKTOP, OS.VK_BUTTON1) & 0x8000) != 0)
        event.stateMask |= SWT.BUTTON1;
    if ((OS.WinGetKeyState (OS.HWND_DESKTOP, OS.VK_BUTTON2) & 0x8000) != 0)
        event.stateMask |= SWT.BUTTON2;
    if ((OS.WinGetKeyState (OS.HWND_DESKTOP, OS.VK_BUTTON3) & 0x8000) != 0)
        event.stateMask |= SWT.BUTTON3;
    switch (type) {
        case SWT.KeyDown:
        case SWT.Traverse:
            if (event.keyCode == SWT.ALT) event.stateMask &= ~SWT.ALT;
            if (event.keyCode == SWT.SHIFT) event.stateMask &= ~SWT.SHIFT;
            if (event.keyCode == SWT.CONTROL) event.stateMask &= ~SWT.CONTROL;
            break;
        case SWT.KeyUp:
            if (event.keyCode == SWT.ALT) event.stateMask |= SWT.ALT;
            if (event.keyCode == SWT.SHIFT) event.stateMask |= SWT.SHIFT;
            if (event.keyCode == SWT.CONTROL) event.stateMask |= SWT.CONTROL;
            break;
    }
    return true;
}

//@@TODO(dmik): guess we don't need this
//boolean setKeyState (Event event, int type) {
//    Display display = getDisplay ();
//    if (display.lastAscii != 0) {
//        event.character = mbcsToWcs ((char) display.lastAscii);
//    }
//    if (display.lastVirtual) {
//        event.keyCode = Display.translateKey (display.lastKey);
//    }
//    if (event.keyCode == 0 && event.character == 0) {
//        return false;
//    }
//    return setInputState (event, type);
//}

/**
 * Sets the layout data associated with the receiver to the argument.
 *
 * @param layoutData the new layout data for the receiver.
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void setLayoutData (Object layoutData) {
    checkWidget ();
    this.layoutData = layoutData;
}

/**
 * Sets the receiver's location to the point specified by
 * the arguments which are relative to the receiver's
 * parent (or its display if its parent is null).
 *
 * @param x the new x coordinate for the receiver
 * @param y the new y coordinate for the receiver
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void setLocation (int x, int y) {
    checkWidget ();
    setBounds (x, y, 0, 0, OS.SWP_MOVE);
}

/**
 * Sets the receiver's location to the point specified by
 * the argument which is relative to the receiver's
 * parent (or its display if its parent is null).
 *
 * @param location the new location for the receiver
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void setLocation (Point location) {
    checkWidget ();
    if (location == null) error (SWT.ERROR_NULL_ARGUMENT);
    setLocation (location.x, location.y);
}

/**
 * Sets the receiver's pop up menu to the argument.
 * All controls may optionally have a pop up
 * menu that is displayed when the user requests one for
 * the control. The sequence of key strokes, button presses
 * and/or button releases that are used to request a pop up
 * menu is platform specific.
 *
 * @param menu the new pop up menu
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_MENU_NOT_POP_UP - the menu is not a pop up menu</li>
 *    <li>ERROR_INVALID_PARENT - if the menu is not in the same widget tree</li>
 *    <li>ERROR_INVALID_ARGUMENT - if the menu has been disposed</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
//@@TODO(dmik)
//public void setMenu (Menu menu) {
//    checkWidget ();
//    if (menu != null) {
//        if (menu.isDisposed()) SWT.error(SWT.ERROR_INVALID_ARGUMENT);
//        if ((menu.style & SWT.POP_UP) == 0) {
//            error (SWT.ERROR_MENU_NOT_POP_UP);
//        }
//        if (menu.parent != menuShell ()) {
//            error (SWT.ERROR_INVALID_PARENT);
//        }
//    }
//    this.menu = menu;
//}

void setPresBackground () {
    int[] value = {getBackgroundPixel ()};
    int index = (value[0] < 0 || getDisplay ().hPalette != 0) ?
        OS.PP_BACKGROUNDCOLORINDEX : OS.PP_BACKGROUNDCOLOR;
    OS.WinSetPresParam (handle, index, 4, value);
    /*
     *  "Feature" in OS/2. checkboxes and radiobuttons use the background
     *  color of the parent window to draw the halftoned pattern when
     *  disabled. Sometimes they refuse to pickup the newly set parent's
     *  background if it is the system color (negative value) until other
     *  l & f change of the ckeckbox or radiobutton itself takes place
     *  (for example, the button text color). However, if we always set
     *  presentation colors as RGB it doesn't work either. I really don't
     *  kwow why does it happen, but the code below helps.
     */
    //@@TODO (dmik):
    //  this won't be necessary when we will draw these types of buttons
    //  ourselves.
    if (index == OS.PP_BACKGROUNDCOLORINDEX) 
        OS.WinSetPresParam (handle, OS.PP_BACKGROUNDCOLOR, 4,
            new int[] {OS.WinQuerySysColor (OS.HWND_DESKTOP, value [0], 0)});
    
    index = index == OS.PP_BACKGROUNDCOLORINDEX ?
        OS.PP_DISABLEDBACKGROUNDCOLORINDEX : OS.PP_DISABLEDBACKGROUNDCOLOR;
    OS.WinSetPresParam (handle, index, 4, value);
}

void setPresForeground () {
    int[] value = {getForegroundPixel ()};
    int index = (value[0] < 0 || getDisplay ().hPalette != 0) ?
        OS.PP_FOREGROUNDCOLORINDEX : OS.PP_FOREGROUNDCOLOR;
    OS.WinSetPresParam (handle, index, 4, value);
    index = index == OS.PP_FOREGROUNDCOLORINDEX ?
        OS.PP_DISABLEDFOREGROUNDCOLORINDEX : OS.PP_DISABLEDFOREGROUNDCOLOR;
    OS.WinSetPresParam (handle, index, 4, value);
}

boolean setRadioFocus () {
    return false;
}

/**
 * If the argument is <code>false</code>, causes subsequent drawing
 * operations in the receiver to be ignored. No drawing of any kind
 * can occur in the receiver until the flag is set to true.
 * Graphics operations that occurred while the flag was
 * <code>false</code> are lost. When the flag is set to <code>true</code>,
 * the entire widget is marked as needing to be redrawn.
 * <p>
 * Note: This operation is a hint and may not be supported on some
 * platforms or for some widgets.
 * </p>
 *
 * @param redraw the new redraw state
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @see #redraw
 * @see #update
 */
public void setRedraw (boolean redraw) {
    checkWidget ();
    if (redraw) {
        if (--drawCount == 0) {
            OS.WinEnableWindowUpdate (handle, true);
            OS.WinInvalidateRect (handle, null, true);
        }
    } else {
        if (drawCount++ == 0) {
            OS.WinEnableWindowUpdate (handle, false);
        }
    }
}

/**
 * Sets the receiver's size to the point specified by the arguments.
 * <p>
 * Note: Attempting to set the width or height of the
 * receiver to a negative number will cause that
 * value to be set to zero instead.
 * </p>
 *
 * @param width the new width for the receiver
 * @param height the new height for the receiver
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void setSize (int width, int height) {
    checkWidget ();
    setBounds (0, 0, Math.max (0, width), Math.max (0, height), OS.SWP_SIZE);
}

/**
 * Sets the receiver's size to the point specified by the argument.
 * <p>
 * Note: Attempting to set the width or height of the
 * receiver to a negative number will cause them to be
 * set to zero instead.
 * </p>
 *
 * @param size the new size for the receiver
 * @param height the new height for the receiver
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the point is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void setSize (Point size) {
    checkWidget ();
    if (size == null) error (SWT.ERROR_NULL_ARGUMENT);
    setSize (size.x, size.y);
}

boolean setTabGroupFocus () {
    return setTabItemFocus ();
}

boolean setTabItemFocus () {
    if (!isShowing ()) return false;
    return setFocus ();
}

/**
 * Sets the receiver's tool tip text to the argument, which
 * may be null indicating that no tool tip text should be shown.
 *
 * @param string the new tool tip text (or null)
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
//@@TODO(dmik)
//public void setToolTipText (String string) {
//    checkWidget ();
//    Shell shell = getShell ();
//    shell.setToolTipText (handle, toolTipText = string);
//}

/**
 * Marks the receiver as visible if the argument is <code>true</code>,
 * and marks it invisible otherwise.
 * <p>
 * If one of the receiver's ancestors is not visible or some
 * other condition makes the receiver not visible, marking
 * it visible may not actually cause it to be displayed.
 * </p>
 *
 * @param visible the new visibility state
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void setVisible (boolean visible) {
    checkWidget ();
    if (OS.WinIsWindowVisible (handle) == visible) return;
    if (visible) {
        /*
        * It is possible (but unlikely), that application
        * code could have disposed the widget in the show
        * event.  If this happens, just return.
        */
        sendEvent (SWT.Show);
        if (isDisposed ()) return;
    }

//@@TODO(dmik): guess we don't need this on OS/2 since
//  disabling the focus window or hidihg it doesn't take the focus away.
//    /*
//    * Feature in Windows.  If the receiver has focus, hiding
//    * the receiver causes no window to have focus.  The fix is
//    * to assign focus to the first ancestor window that takes
//    * focus.  If no window will take focus, set focus to the
//    * desktop.
//    */
//    boolean fixFocus = false;
//    if (!visible) fixFocus = isFocusAncestor ();
    OS.WinShowWindow (handle, visible);

    if (!visible) {
        /*
        * It is possible (but unlikely), that application
        * code could have disposed the widget in the show
        * event.  If this happens, just return.
        */
        sendEvent (SWT.Hide);
        if (isDisposed ()) return;
    }
//@@TODO(dmik): guess we don't need this on OS/2 since
//  disabling the focus window or hidihg it doesn't take the focus away.
//    if (fixFocus) fixFocus ();
}


void sort (int [] items) {
      /* Shell Sort from K&R, pg 108 */
      int length = items.length;
      for (int gap=length/2; gap>0; gap/=2) {
              for (int i=gap; i<length; i++) {
                      for (int j=i-gap; j>=0; j-=gap) {
                              if (items [j] <= items [j + gap]) {
                                      int swap = items [j];
                                      items [j] = items [j + gap];
                                      items [j + gap] = swap;
                              }
              }
          }
      }
}

void subclass () {
    int oldProc = windowProc ();
    int newProc = getDisplay ().windowProc;
    if (oldProc == newProc) return;
    OS.WinSetWindowPtr (handle, OS.QWP_PFNWP, newProc);
}

/**
 * Returns a point which is the result of converting the
 * argument, which is specified in display relative coordinates,
 * to coordinates relative to the receiver.
 * <p>
 * @param point the point to be translated (must not be null)
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the point is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
//@@TODO(dmik)
//public Point toControl (Point point) {
//    checkWidget ();
//    if (point == null) error (SWT.ERROR_NULL_ARGUMENT);
//    POINT pt = new POINT ();
//    pt.x = point.x;  pt.y = point.y;
//    OS.ScreenToClient (handle, pt);
//    return new Point (pt.x, pt.y);
//}

/**
 * Returns a point which is the result of converting the
 * argument, which is specified in coordinates relative to
 * the receiver, to display relative coordinates.
 * <p>
 * @param point the point to be translated (must not be null)
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the point is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
//@@TODO(dmik)
//public Point toDisplay (Point point) {
//    checkWidget ();
//    if (point == null) error (SWT.ERROR_NULL_ARGUMENT);
//    POINT pt = new POINT ();
//    pt.x = point.x;  pt.y = point.y;
//    OS.ClientToScreen (handle, pt);
//    return new Point (pt.x, pt.y);
//}

boolean translateAccelerator (QMSG qmsg) {
    return menuShell ().translateAccelerator (qmsg);
}

boolean translateMnemonic (char key) {
    if (!isVisible () || !isEnabled ()) return false;
    Event event = new Event ();
    event.doit = mnemonicMatch (key);
    event.detail = SWT.TRAVERSE_MNEMONIC;
    event.keyCode = 0;
    event.character = key;
    if (!setInputState (event, SWT.Traverse)) {
        return false;
    }
    return traverse (event);
}

boolean translateMnemonic (QMSG qmsg) {
    int hwnd = qmsg.hwnd;
    if ((OS.WinGetKeyState (OS.HWND_DESKTOP, OS.VK_ALT) & 0x8000) == 0 &&
        (OS.WinGetKeyState (OS.HWND_DESKTOP, OS.VK_ALTGRAF) & 0x8000) == 0
    ) {
        int code = OS.WinSendMsg (hwnd, OS.WM_QUERYDLGCODE, 0, 0);
        if ((code & OS.DLGC_BUTTON) == 0) return false;
    }
    Decorations shell = menuShell ();
    if (shell.isVisible () && shell.isEnabled ()) {
        short usch = OS.SHORT1FROMMP (qmsg.mp2);
        /* Use the character encoding for the default locale */                
        char ch = new String (new byte[] {(byte) usch}).charAt (0);
        return ch != 0 && shell.translateMnemonic (ch);
    }
    return false;
}

boolean translateTraversal (QMSG qmsg) {
    short fsflags = OS.SHORT1FROMMP (qmsg.mp1);
    short usvk = OS.SHORT2FROMMP (qmsg.mp2);
    int hwnd = qmsg.hwnd;
    int detail = SWT.TRAVERSE_NONE;
    boolean doit = true, all = false;
    int keyCode = 0; 
    char character = 0;
    int code = OS.WinSendMsg (hwnd, OS.WM_QUERYDLGCODE, 0, 0);
    switch (usvk) {
        case OS.VK_ESC: {
            all = true;
            character = 27;
            if ((code & OS.DLGC_MLE) != 0) doit = false;
            detail = SWT.TRAVERSE_ESCAPE;
            break;
        }
        case OS.VK_ENTER:
        case OS.VK_NEWLINE: {
            all = true;
            character = '\r';
            if ((code & OS.DLGC_MLE) != 0) doit = false;
            detail = SWT.TRAVERSE_RETURN;
            break;
        }
        case OS.VK_TAB:
        case OS.VK_BACKTAB: {
            /*
            * NOTE: This code causes Shift+Tab and Ctrl+Tab to
            * always attempt traversal which is not correct.
            * The default should the same as a plain Tab key.
            * This behavior is currently relied on by StyledText.
            */
            character = '\t';
            boolean next = (usvk == OS.VK_TAB) && ((fsflags & OS.KC_SHIFT) == 0); 
            if ((code & OS.DLGC_MLE) != 0) {
                  if (next && (fsflags & OS.KC_CTRL) == 0)
                        doit = false;
            }
            detail = next ? SWT.TRAVERSE_TAB_NEXT : SWT.TRAVERSE_TAB_PREVIOUS;
            break;
        }
        case OS.VK_UP:
        case OS.VK_LEFT:
        case OS.VK_DOWN:
        case OS.VK_RIGHT: {
            keyCode = getDisplay ().translateKey (usvk);
            if ((code & OS.DLGC_MLE) != 0) doit = false;
            boolean next = usvk == OS.VK_DOWN || usvk == OS.VK_RIGHT;
            detail = next ? SWT.TRAVERSE_ARROW_NEXT : SWT.TRAVERSE_ARROW_PREVIOUS;
            break;
        }
        case OS.VK_PAGEUP:
        case OS.VK_PAGEDOWN: {
            all = true;
            keyCode = getDisplay ().translateKey (usvk);
            if ((OS.WinGetKeyState (OS.HWND_DESKTOP, OS.VK_CTRL) & 0x8000) == 0)
                return false;
            if ((code & OS.DLGC_MLE) != 0) doit = false;
            detail = usvk == OS.VK_PAGEUP ?
                SWT.TRAVERSE_PAGE_PREVIOUS : SWT.TRAVERSE_PAGE_NEXT;
            break;
        }
        default:
            return false;
    }
    Event event = new Event ();
    event.doit = doit;
    event.detail = detail;
    event.keyCode = keyCode;
    event.character = character;
    if (!setInputState (event, SWT.Traverse)) {
        return false;
    }
    Shell shell = getShell ();
    Control control = this;
    do {
        if (control.traverse (event)) return true;
        if (!event.doit && control.hooks (SWT.Traverse)) {
            return false;
        }
        if (control == shell) return false;
        control = control.parent;
    } while (all && control != null);
    return false;
}

boolean traverse (Event event) {
    /*
    * It is possible (but unlikely), that application
    * code could have disposed the widget in the traverse
    * event.  If this happens, return true to stop further
    * event processing.
    */
    sendEvent (SWT.Traverse, event);
    if (isDisposed ()) return false;
    if (!event.doit) return false;
    switch (event.detail) {
        case SWT.TRAVERSE_NONE:                 return true;
        case SWT.TRAVERSE_ESCAPE:               return traverseEscape ();
        case SWT.TRAVERSE_RETURN:               return traverseReturn ();
        case SWT.TRAVERSE_TAB_NEXT:             return traverseGroup (true);
        case SWT.TRAVERSE_TAB_PREVIOUS:         return traverseGroup (false);
        case SWT.TRAVERSE_ARROW_NEXT:           return traverseItem (true);
        case SWT.TRAVERSE_ARROW_PREVIOUS:       return traverseItem (false);
        case SWT.TRAVERSE_MNEMONIC:             return traverseMnemonic (event.character);
        case SWT.TRAVERSE_PAGE_NEXT:            return traversePage (true);
        case SWT.TRAVERSE_PAGE_PREVIOUS:        return traversePage (false);
    }
    return false;
}

/**
 * Based on the argument, perform one of the expected platform
 * traversal action. The argument should be one of the constants:
 * <code>SWT.TRAVERSE_ESCAPE</code>, <code>SWT.TRAVERSE_RETURN</code>,
 * <code>SWT.TRAVERSE_TAB_NEXT</code>, <code>SWT.TRAVERSE_TAB_PREVIOUS</code>,
 * <code>SWT.TRAVERSE_ARROW_NEXT</code> and <code>SWT.TRAVERSE_ARROW_PREVIOUS</code>.
 *
 * @param traversal the type of traversal
 * @return true if the traversal succeeded
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public boolean traverse (int traversal) {
      checkWidget ();
      if (!isFocusControl () && !setFocus ()) return false;
      Event event = new Event ();
      event.doit = true;
      event.detail = traversal;
      return traverse (event);
}

boolean traverseEscape () {
      return false;
}

boolean traverseGroup (boolean next) {
    Control root = computeTabRoot ();
    Control group = computeTabGroup ();
    Control [] list = root.computeTabList ();
    int length = list.length;
    int index = 0;
    while (index < length) {
        if (list [index] == group) break;
        index++;
    }
    /*
    * It is possible (but unlikely), that application
    * code could have disposed the widget in focus in
    * or out events.  Ensure that a disposed widget is
    * not accessed.
    */
    if (index == length) return false;
    int start = index, offset = (next) ? 1 : -1;
    while ((index = ((index + offset + length) % length)) != start) {
        Control control = list [index];
        if (!control.isDisposed () && control.setTabGroupFocus ()) {
            if (!isDisposed () && !isFocusControl ()) return true;
        }
    }
    if (group.isDisposed ()) return false;
    return group.setTabGroupFocus ();
}

boolean traverseItem (boolean next) {
    Control [] children = parent._getChildren ();
    int length = children.length;
    int index = 0;
    while (index < length) {
        if (children [index] == this) break;
        index++;
    }
    /*
    * It is possible (but unlikely), that application
    * code could have disposed the widget in focus in
    * or out events.  Ensure that a disposed widget is
    * not accessed.
    */
    int start = index, offset = (next) ? 1 : -1;
    while ((index = (index + offset + length) % length) != start) {
        Control child = children [index];
        if (!child.isDisposed () && child.isTabItem ()) {
            if (child.setTabItemFocus ()) return true;
        }
    }
    return false;
}

boolean traverseMnemonic (char key) {
    return mnemonicHit (key);
}

boolean traversePage (boolean next) {
    return false;
}

boolean traverseReturn () {
    return false;
}

void unsubclass () {
    int newProc = windowProc ();
    int oldProc = getDisplay ().windowProc;
    if (oldProc == newProc) return;
    OS.WinSetWindowPtr (handle, OS.QWP_PFNWP, newProc);
}

/**
 * Forces all outstanding paint requests for the widget tree
 * to be processed before this method returns.
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @see #redraw
 */
public void update () {
    checkWidget ();
    OS.WinUpdateWindow (handle);
}

void updateFont (Font oldFont, Font newFont) {
      Font font = getFont ();
      if (font.equals (oldFont)) setFont (newFont);
}

//@@TODO(dmik): need to make borders under OS/2?
//int widgetExtStyle () {
//      if ((style & SWT.BORDER) != 0) return OS.WS_EX_CLIENTEDGE;
//      return 0;
//}

int widgetStyle () {
    /* Force strict clipping by setting WS_CLIPSIBLINGS */
    return OS.WS_VISIBLE | OS.WS_CLIPSIBLINGS;

    /*
    * This code is intentionally commented.  When strict
    * clipping (clipping of both siblings and children)
    * was not enforced on all widgets, poorly written
    * application code could draw outside of the control.
    */
//    int bits = OS.WS_VISIBLE;
//    if ((style & SWT.CLIP_SIBLINGS) != 0) bits |= OS.WS_CLIPSIBLINGS;
//    if ((style & SWT.CLIP_CHILDREN) != 0) bits |= OS.WS_CLIPCHILDREN;
//    return bits;
}

/**
 * Changes the parent of the widget to be the one provided if
 * the underlying operating system supports this feature.
 * Answers <code>true</code> if the parent is successfully changed.
 *
 * @param parent the new parent for the control.
 * @return <code>true</code> if the parent is changed and <code>false</code> otherwise.
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_INVALID_ARGUMENT - if the argument has been disposed</li>
 * </ul>
 * @exception SWTError <ul>
 *              <li>ERROR_THREAD_INVALID_ACCESS when called from the wrong thread</li>
 *              <li>ERROR_WIDGET_DISPOSED when the widget has been disposed</li>
 *      </ul>
 */
public boolean setParent (Composite parent) {
    checkWidget ();
    if (parent == null) error (SWT.ERROR_NULL_ARGUMENT);
    if (parent.isDisposed()) SWT.error(SWT.ERROR_INVALID_ARGUMENT);
    if (!OS.WinSetParent (handle, parent.handle, true)) {
        return false;
    }
    this.parent = parent;
    return true;
}

abstract PSZ windowClass ();

/*
 *  Returns the pointer to the original window procedure
 *  regarding to subclassing.
 */
abstract int windowProc ();

int windowProc (int msg, int mp1, int mp2) {
    MRESULT result = null;
    switch (msg) {
        case OS.WM_CALCVALIDRECTS: result = WM_CALCVALIDRECTS (mp1, mp2); break;
        case OS.WM_CHAR: result = WM_CHAR (mp1, mp2); break;
//@@TODO (dmik): later        
//        case OS.WM_CLEAR: result = WM_CLEAR (wParam, lParam); break;
        case OS.WM_CLOSE: result = WM_CLOSE (mp1, mp2); break;
        case OS.WM_COMMAND: result = WM_COMMAND (mp1, mp2); break;
//@@TODO (dmik): later
//        case OS.WM_CONTEXTMENU: result = WM_CONTEXTMENU (wParam, lParam); break;
        case OS.WM_CONTROL: result = WM_CONTROL (mp1, mp2); break;
//@@TODO (dmik): later        
//        case OS.WM_CTLCOLORBTN:
//        case OS.WM_CTLCOLORDLG:
//        case OS.WM_CTLCOLOREDIT:
//        case OS.WM_CTLCOLORLISTBOX:
//        case OS.WM_CTLCOLORMSGBOX:
//        case OS.WM_CTLCOLORSCROLLBAR:
//        case OS.WM_CTLCOLORSTATIC: result = WM_CTLCOLOR (wParam, lParam); break;
//        case OS.WM_CUT: result = WM_CUT (wParam, lParam); break;
//        case OS.WM_DESTROY: result = WM_DESTROY (wParam, lParam); break;
//        case OS.WM_DRAWITEM: result = WM_DRAWITEM (wParam, lParam); break;
//        case OS.WM_ENDSESSION: result = WM_ENDSESSION (wParam, lParam); break;
        case OS.WM_ERASEBACKGROUND: result = WM_ERASEBACKGROUND (mp1, mp2); break;
        case OS.WM_QUERYDLGCODE: result = WM_QUERYDLGCODE (mp1, mp2); break;
//        case OS.WM_HELP: result = WM_HELP (wParam, lParam); break;
        case OS.WM_HSCROLL: result = WM_HSCROLL (mp1, mp2); break;
//        case OS.WM_IME_CHAR: result = WM_IME_CHAR (wParam, lParam); break;
//        case OS.WM_IME_COMPOSITION: result = WM_IME_COMPOSITION (wParam, lParam); break;
//        case OS.WM_INITMENUPOPUP: result = WM_INITMENUPOPUP (wParam, lParam); break;
//        case OS.WM_GETFONT: result = WM_GETFONT (wParam, lParam); break;
//        case OS.WM_GETOBJECT: result = WM_GETOBJECT (wParam, lParam); break;
//        case OS.WM_KEYDOWN: result = WM_KEYDOWN (wParam, lParam); break;
//        case OS.WM_KEYUP: result = WM_KEYUP (wParam, lParam); break;
//        case OS.WM_LBUTTONDBLCLK: result = WM_LBUTTONDBLCLK (wParam, lParam); break;
//        case OS.WM_LBUTTONDOWN: result = WM_LBUTTONDOWN (wParam, lParam); break;
//        case OS.WM_LBUTTONUP: result = WM_LBUTTONUP (wParam, lParam); break;
//        case OS.WM_MBUTTONDBLCLK: result = WM_MBUTTONDBLCLK (wParam, lParam); break;
//        case OS.WM_MBUTTONDOWN: result = WM_MBUTTONDOWN (wParam, lParam); break;
//        case OS.WM_MBUTTONUP: result = WM_MBUTTONUP (wParam, lParam); break;
        case OS.WM_MEASUREITEM: result = WM_MEASUREITEM (mp1, mp2); break;
//        case OS.WM_MENUCHAR: result = WM_MENUCHAR (wParam, lParam); break;
//        case OS.WM_MENUSELECT: result = WM_MENUSELECT (wParam, lParam); break;
//        case OS.WM_MOUSEACTIVATE: result = WM_MOUSEACTIVATE (wParam, lParam); break;
//        case OS.WM_MOUSEHOVER: result = WM_MOUSEHOVER (wParam, lParam); break;
//        case OS.WM_MOUSELEAVE: result = WM_MOUSELEAVE (wParam, lParam); break;
//        case OS.WM_MOUSEMOVE: result = WM_MOUSEMOVE (wParam, lParam); break;
//        case OS.WM_MOUSEWHEEL: result = WM_MOUSEWHEEL (wParam, lParam); break;
//        case OS.WM_NCACTIVATE: result = WM_NCACTIVATE (wParam, lParam); break;
//        case OS.WM_NCCALCSIZE: result = WM_NCCALCSIZE (wParam, lParam); break;
//        case OS.WM_NCHITTEST: result = WM_NCHITTEST (wParam, lParam); break;
//        case OS.WM_NOTIFY: result = WM_NOTIFY (wParam, lParam); break;
        case OS.WM_PAINT: result = WM_PAINT (mp1, mp2); break;
//@@TODO (dmik): later
//        case OS.WM_PASTE: result = WM_PASTE (wParam, lParam); break;
//        case OS.WM_PRINTCLIENT: result = WM_PRINTCLIENT (wParam, lParam); break;
//        case OS.WM_QUERYENDSESSION: result = WM_QUERYENDSESSION (wParam, lParam); break;
//        case OS.WM_RBUTTONDBLCLK: result = WM_RBUTTONDBLCLK (wParam, lParam); break;
//        case OS.WM_RBUTTONDOWN: result = WM_RBUTTONDOWN (wParam, lParam); break;
//        case OS.WM_RBUTTONUP: result = WM_RBUTTONUP (wParam, lParam); break;
//        case OS.WM_SETCURSOR: result = WM_SETCURSOR (wParam, lParam); break;
        case OS.WM_SETFOCUS: result = WM_SETFOCUS (mp1, mp2); break;
//@@TODO (dmik): later        
//        case OS.WM_SETFONT: result = WM_SETFONT (wParam, lParam); break;
//        case OS.WM_SETTINGCHANGE: result = WM_SETTINGCHANGE (wParam, lParam); break;
//        case OS.WM_SETREDRAW: result = WM_SETREDRAW (wParam, lParam); break;
//        case OS.WM_SHOWWINDOW: result = WM_SHOWWINDOW (wParam, lParam); break;
        case OS.WM_SIZE: result = WM_SIZE (mp1, mp2); break;
//@@TODO (dmik): later        
//        case OS.WM_SYSCHAR: result = WM_SYSCHAR (wParam, lParam); break;
//        case OS.WM_SYSCOLORCHANGE: result = WM_SYSCOLORCHANGE (wParam, lParam); break;
//        case OS.WM_SYSCOMMAND: result = WM_SYSCOMMAND (wParam, lParam); break;
//        case OS.WM_SYSKEYDOWN: result = WM_SYSKEYDOWN (wParam, lParam); break;
//        case OS.WM_SYSKEYUP: result = WM_SYSKEYUP (wParam, lParam); break;
//        case OS.WM_TIMER: result = WM_TIMER (wParam, lParam); break;
//        case OS.WM_UNDO: result = WM_UNDO (wParam, lParam); break;
        case OS.WM_VSCROLL: result = WM_VSCROLL (mp1, mp2); break;
//        case OS.WM_WINDOWPOSCHANGING: result = WM_WINDOWPOSCHANGING (wParam, lParam); break;
        case OS.WM_WINDOWPOSCHANGED: result = WM_WINDOWPOSCHANGED (mp1, mp2); break;
    }
    if (result != null) return result.value;
    return callWindowProc (msg, mp1, mp2);
}

PSZ windowTitle() {
    return null;
}

MRESULT WM_CALCVALIDRECTS (int mp1, int mp2) {
    return null;
}

MRESULT WM_CHAR (int mp1, int mp2) {
    boolean keyUp = (OS.SHORT1FROMMP (mp1) & OS.KC_KEYUP) != 0;
//@@TODO (dmik): debug, remove    
//System.out.println("WM_CHAR ("+this+"): vk="+OS.SHORT2FROMMP (mp2)+" keyup="+keyUp);
    sendKeyEvent (keyUp ? SWT.KeyUp : SWT.KeyDown, OS.WM_CHAR, mp1, mp2);
    return null;
}

//@@TODO (dmik): later
//LRESULT WM_CLEAR (int wParam, int lParam) {
//      return null;
//}

MRESULT WM_CLOSE (int mp1, int mp2) {
    return null;
}

MRESULT WM_COMMAND (int mp1, int mp2) {
//@@TODO(dmik): do we need this?
//      /*
//      * When the WM_COMMAND message is sent from a
//      * menu, the HWND parameter in LPARAM is zero.
//      */
//      if (lParam == 0) {
//              Decorations shell = menuShell ();
//              if (shell.isEnabled ()) {
//                      int id = wParam & 0xFFFF;
//                      MenuItem item = shell.findMenuItem (id);
//                      if (item != null && item.isEnabled ()) {
//                              return item.wmCommandChild (wParam, lParam);
//                      }
//              }
//              return null;
//      }
//      Control control = WidgetTable.get (lParam);
//      if (control == null) return null;
//      return control.wmCommandChild (wParam, lParam);
    switch (OS.SHORT1FROMMP (mp2)) {
        //@@TODO(dmik)
        //case OS.CMDSRC_MENU:
        //    return null;
        case OS.CMDSRC_PUSHBUTTON:
        //@@TODO(lpino)
        case OS.CMDSRC_FONTDLG:
        case OS.CMDSRC_FILEDLG:
        case OS.CMDSRC_COLORDLG:
        //case OS.CMDSRC_PRINTDLG:
            int hwndChild = OS.WinWindowFromID (handle, OS.SHORT1FROMMP (mp1));
            Control control = WidgetTable.get (hwndChild);
            if (control == null) return null;
            return control.wmCommandChild (mp1, mp2);
    }
    return null;
}

////@@TODO (dmik): later
//LRESULT WM_CONTEXTMENU (int wParam, int lParam) {
//      /*
//      * Because context menus can be shared between controls
//      * and the parent of all menus is the shell, the menu may
//      * have been destroyed but not removed from the control.
//      */
//      if (wParam != handle) return null;
//      if (menu != null && !menu.isDisposed ()) {
//              menu.setVisible (true);
//              return LRESULT.ZERO;
//      }
//      return null;
//}

MRESULT WM_CONTROL (int mp1, int mp2) {
    int hwndChild = OS.WinWindowFromID (handle, OS.SHORT1FROMMP (mp1));
        Control control = WidgetTable.get (hwndChild);
        if (control == null) return null;
        return control.wmControlChild (mp1, mp2);
}

//@@TODO(dmik)
//LRESULT WM_CTLCOLOR (int wParam, int lParam) {
//      Display display = getDisplay ();
//      int hPalette = display.hPalette;
//      if (hPalette != 0) {
//              OS.SelectPalette (wParam, hPalette, false);
//              OS.RealizePalette (wParam);
//      }
//      Control control = WidgetTable.get (lParam);
//      if (control == null) return null;
//      return control.wmColorChild (wParam, lParam);
//}
//
//LRESULT WM_CUT (int wParam, int lParam) {
//      return null;
//}
//
//LRESULT WM_DESTROY (int wParam, int lParam) {
//      return null;
//}
//
//LRESULT WM_DRAWITEM (int wParam, int lParam) {
//      DRAWITEMSTRUCT struct = new DRAWITEMSTRUCT ();
//      OS.MoveMemory (struct, lParam, DRAWITEMSTRUCT.sizeof);
//      if (struct.CtlType == OS.ODT_MENU) {
//              Decorations shell = menuShell ();
//              MenuItem item = shell.findMenuItem (struct.itemID);
//              if (item == null) return null;
//              return item.wmDrawChild (wParam, lParam);
//      }
//      Control control = WidgetTable.get (struct.hwndItem);
//      if (control == null) return null;
//      return control.wmDrawChild (wParam, lParam);
//}
//
//LRESULT WM_ENDSESSION (int wParam, int lParam) {
//      return null;
//}

MRESULT WM_ERASEBACKGROUND (int mp1, int mp2) {
    return null;
}

MRESULT WM_MEASUREITEM (int mp1, int mp2) {
    return null;
}

MRESULT WM_QUERYDLGCODE (int mp1, int mp2) {
      return null;
}

//@@TODO (dmik): later
//LRESULT WM_GETFONT (int wParam, int lParam) {
//      return null;
//}
//
//LRESULT WM_GETOBJECT (int wParam, int lParam) {
//      if (accessible != null) {
//              int result = accessible.internal_WM_GETOBJECT (wParam, lParam);
//              if (result != 0) return new LRESULT (result);
//      }
//      return null;
//}
//
//LRESULT WM_HELP (int wParam, int lParam) {
//      if (OS.IsWinCE) return null;
//      HELPINFO lphi = new HELPINFO ();
//      OS.MoveMemory (lphi, lParam, HELPINFO.sizeof);
//      Decorations shell = menuShell ();
//      if (!shell.isEnabled ()) return null;
//      if (lphi.iContextType == OS.HELPINFO_MENUITEM) {
//              MenuItem item = shell.findMenuItem (lphi.iCtrlId);
//              if (item != null && item.isEnabled ()) {
//                      Widget widget = null;
//                      if (item.hooks (SWT.Help)) {
//                              widget = item;
//                      } else {
//                              Menu menu = item.parent;
//                              if (menu.hooks (SWT.Help)) widget = menu;
//                      }
//                      if (widget != null) {
//                              int hwndShell = shell.handle;
//                              OS.SendMessage (hwndShell, OS.WM_CANCELMODE, 0, 0);
//                              widget.postEvent (SWT.Help);
//                              return LRESULT.ONE;
//                      }
//              }
//              return null;
//      }
//      if (hooks (SWT.Help)) {
//              postEvent (SWT.Help);
//              return LRESULT.ONE;
//      }
//      return null;
//}

MRESULT WM_HSCROLL (int mp1, int mp2) {
      int hwndChild = OS.WinWindowFromID (handle, OS.SHORT1FROMMP (mp1));
      Control control = WidgetTable.get (hwndChild);
      if (control == null) return null;
      return control.wmScrollChild (mp1, mp2);
}

//LRESULT WM_IME_CHAR (int wParam, int lParam) {
//      Display display = getDisplay ();
//      display.lastKey = 0;
//      display.lastAscii = wParam;
//      display.lastVirtual = false;
//      sendKeyEvent (SWT.KeyDown, OS.WM_IME_CHAR, wParam, lParam);
//      sendKeyEvent (SWT.KeyUp, OS.WM_IME_CHAR, wParam, lParam);
//      display.lastKey = display.lastAscii = 0;
//      return LRESULT.ZERO;
//}
//
//LRESULT WM_IME_COMPOSITION (int wParam, int lParam) {
//      return null;
//}
//
//LRESULT WM_INITMENUPOPUP (int wParam, int lParam) {
//
//      /* Ignore WM_INITMENUPOPUP for an accelerator */
//      Display display = getDisplay ();
//      if (display.accelKeyHit) return null;
//
//      /*
//      * If the high order word of LPARAM is non-zero,
//      * the menu is the system menu and we can ignore
//      * WPARAM.  Otherwise, use WPARAM to find the menu.
//      */
//      Shell shell = getShell ();
//      Menu oldMenu = shell.activeMenu, newMenu = null;
//      if ((lParam >> 16) == 0) {
//              newMenu = menuShell ().findMenu (wParam);
//      }
//      Menu menu = newMenu;
//      while (menu != null && menu != oldMenu) {
//              menu = menu.getParentMenu ();
//      }
//      if (menu == null) {
//              menu = shell.activeMenu;
//              while (menu != null) {
//                      /*
//                      * It is possible (but unlikely), that application
//                      * code could have disposed the widget in the hide
//                      * event.  If this happens, stop searching up the
//                      * ancestor list because there is no longer a link
//                      * to follow.
//                      */
//                      menu.sendEvent (SWT.Hide);
//                      if (menu.isDisposed ()) break;
//                      menu = menu.getParentMenu ();
//                      Menu ancestor = newMenu;
//                      while (ancestor != null && ancestor != menu) {
//                              ancestor = ancestor.getParentMenu ();
//                      }
//                      if (ancestor != null) break;
//              }
//      }
//
//      /*
//      * The shell and the new menu may be disposed because of
//      * sending the hide event to the ancestor menus but setting
//      * a field to null in a disposed shell is not harmful.
//      */
//      if (newMenu != null && newMenu.isDisposed ()) newMenu = null;
//      shell.activeMenu = newMenu;
//
//      /*
//      * Send the show event
//      */
//      if (newMenu != null && newMenu != oldMenu) {
//              /*
//              * SWT.Selection events are posted to allow stepping
//              * in the VA/Java debugger.  SWT.Show events are
//              * sent to ensure that application event handler
//              * code runs before the menu is displayed.  This
//              * means that SWT.Show events would normally occur
//              * before SWT.Selection events.  While this is not
//              * strictly incorrect, applications often use the
//              * SWT.Selection event to update the state of menu
//              * items and would like the ordering of events to
//              * be the other way around.
//              *
//              * The fix is to run the deferred events before
//              * the menu is shown.  This means that stepping
//              * through a selection event that was caused by
//              * a popup menu will fail in VA/Java.
//              */
//              display.runDeferredEvents ();
//              newMenu.sendEvent (SWT.Show);
//              // widget could be disposed at this point
//      }
//      return null;
//}

//@@TODO (dmik): later
//LRESULT WM_LBUTTONDBLCLK (int wParam, int lParam) {
//      /*
//      * Feature in Windows. Windows sends the following
//      * messages when the user double clicks the mouse:
//      *
//      *       WM_LBUTTONDOWN          - mouse down
//      *       WM_LBUTTONUP            - mouse up
//      *       WM_LBUTTONDBLCLK        - double click
//      *       WM_LBUTTONUP            - mouse up
//      *
//      * Applications that expect matching mouse down/up
//      * pairs will not see the second mouse down.  The
//      * fix is to send a mouse down event.
//      */
//      sendMouseEvent (SWT.MouseDown, 1, OS.WM_LBUTTONDOWN, wParam, lParam);
//      sendMouseEvent (SWT.MouseDoubleClick, 1, OS.WM_LBUTTONDBLCLK, wParam, lParam);
//      int result = callWindowProc (OS.WM_LBUTTONDBLCLK, wParam, lParam);
//      if (OS.GetCapture () != handle) OS.SetCapture (handle);
//      return new LRESULT (result);
//}
//
//LRESULT WM_LBUTTONDOWN (int wParam, int lParam) {
//      boolean dragging = false, mouseDown = true;
//      if (hooks (SWT.DragDetect)) {
//              POINT pt = new POINT ();
//              pt.x = (short) (lParam & 0xFFFF);
//              pt.y = (short) (lParam >> 16);
//              if (!OS.IsWinCE) {
//                      /*
//                      * Feature in Windows.  It's possible that the drag
//                      * operation will not be started while the mouse is
//                      * down, meaning that the mouse should be captured.
//                      * This can happen when the user types the ESC key
//                      * to cancel the drag.  The fix is to query the state
//                      * of the mouse and capture the mouse accordingly.
//                      */
//                      dragging = OS.DragDetect (handle, pt);
//                      mouseDown = OS.GetKeyState (OS.VK_LBUTTON) < 0;
//              }
//      }
//      sendMouseEvent (SWT.MouseDown, 1, OS.WM_LBUTTONDOWN, wParam, lParam);
//      int result = callWindowProc (OS.WM_LBUTTONDOWN, wParam, lParam);
//      if (mouseDown) {
//              if (OS.GetCapture () != handle) OS.SetCapture (handle);
//      }
//      if (dragging) {
//              postEvent (SWT.DragDetect);
//      } else {
//              if (hooks (SWT.DragDetect)) {
//                      /*
//                      * Feature in Windows.  DragDetect() captures the mouse
//                      * and tracks its movement until the user releases the
//                      * left mouse button, presses the ESC key, or moves the
//                      * mouse outside the drag rectangle.  If the user moves
//                      * the mouse outside of the drag rectangle, DragDetect
//                      * returns true and a drag and drop operation can be
//                      * started.  When the left mouse button is released or
//                      * the ESC key is pressed, these events are consumed by
//                      * DragDetect() so that application code that matches
//                      * mouse down/up pairs or looks for the ESC key will not
//                      * function properly.  The fix is to send these events
//                      * when the drag has not started.
//                      *
//                      * NOTE: For now, don't send a fake WM_KEYDOWN/WM_KEYUP
//                      * events for the ESC key.  This would require computing
//                      * wParam (the key) and lParam (the repeat count, scan code,
//                      * extended-key flag, context code, previous key-state flag,
//                      * and transition-state flag) which is non-trivial.
//                      */
//                      if (OS.GetKeyState (OS.VK_ESCAPE) >= 0) {
//                              OS.SendMessage (handle, OS.WM_LBUTTONUP, wParam, lParam);
//                      }
//              }
//      }
//      return new LRESULT (result);
//}
//
//LRESULT WM_LBUTTONUP (int wParam, int lParam) {
//      sendMouseEvent (SWT.MouseUp, 1, OS.WM_LBUTTONUP, wParam, lParam);
//      int result = callWindowProc (OS.WM_LBUTTONUP, wParam, lParam);
//      if ((wParam & (OS.MK_LBUTTON | OS.MK_MBUTTON | OS.MK_RBUTTON)) == 0) {
//              if (OS.GetCapture () == handle) OS.ReleaseCapture ();
//      }
//      return new LRESULT (result);
//}
//
//LRESULT WM_MBUTTONDBLCLK (int wParam, int lParam) {
//      /*
//      * Feature in Windows. Windows sends the following
//      * messages when the user double clicks the mouse:
//      *
//      *       WM_MBUTTONDOWN          - mouse down
//      *       WM_MBUTTONUP            - mouse up
//      *       WM_MLBUTTONDBLCLK       - double click
//      *       WM_MBUTTONUP            - mouse up
//      *
//      * Applications that expect matching mouse down/up
//      * pairs will not see the second mouse down.  The
//      * fix is to send a mouse down event.
//      */
//      sendMouseEvent (SWT.MouseDown, 2, OS.WM_MBUTTONDOWN, wParam, lParam);
//      sendMouseEvent (SWT.MouseDoubleClick, 2, OS.WM_MBUTTONDBLCLK, wParam, lParam);
//      int result = callWindowProc (OS.WM_MBUTTONDBLCLK, wParam, lParam);
//      if (OS.GetCapture () != handle) OS.SetCapture (handle);
//      return new LRESULT (result);
//}
//
//LRESULT WM_MBUTTONDOWN (int wParam, int lParam) {
//      sendMouseEvent (SWT.MouseDown, 2, OS.WM_MBUTTONDOWN, wParam, lParam);
//      int result = callWindowProc (OS.WM_MBUTTONDOWN, wParam, lParam);
//      if (OS.GetCapture () != handle) OS.SetCapture(handle);
//      return new LRESULT (result);
//}
//
//LRESULT WM_MBUTTONUP (int wParam, int lParam) {
//      sendMouseEvent (SWT.MouseUp, 2, OS.WM_MBUTTONUP, wParam, lParam);
//      int result = callWindowProc (OS.WM_MBUTTONUP, wParam, lParam);
//      if ((wParam & (OS.MK_LBUTTON | OS.MK_MBUTTON | OS.MK_RBUTTON)) == 0) {
//              if (OS.GetCapture () == handle) OS.ReleaseCapture ();
//      }
//      return new LRESULT (result);
//}
//
//LRESULT WM_MEASUREITEM (int wParam, int lParam) {
//      MEASUREITEMSTRUCT struct = new MEASUREITEMSTRUCT ();
//      OS.MoveMemory (struct, lParam, MEASUREITEMSTRUCT.sizeof);
//      if (struct.CtlType == OS.ODT_MENU) {
//              Decorations shell = menuShell ();
//              MenuItem item = shell.findMenuItem (struct.itemID);
//              if (item == null) return null;
//              return item.wmMeasureChild (wParam, lParam);
//      }
//      int hwnd = OS.GetDlgItem (handle, struct.CtlID);
//      Control control = WidgetTable.get (hwnd);
//      if (control == null) return null;
//      return control.wmMeasureChild (wParam, lParam);
//}
//
//LRESULT WM_MENUCHAR (int wParam, int lParam) {
//      Display display = getDisplay ();
//      display.mnemonicKeyHit = false;
//      /*
//      * Feature in Windows.  When the user types Alt+<key>
//      * and <key> does not match a mnemonic in the System
//      * menu or the menu bar, Windows beeps.  This beep is
//      * unexpected and unwanted by applications that look
//      * for Alt+<key>.  The fix is to detect the case and
//      * stop Windows from beeping by closing the menu.
//      */
//      int type = wParam >> 16;
//      if (type == 0 || type == OS.MF_SYSMENU) {
//              return new LRESULT (OS.MNC_CLOSE << 16);
//      }
//      return null;
//}
//
//LRESULT WM_MENUSELECT (int wParam, int lParam) {
//      int code = wParam >> 16;
//      Shell shell = getShell ();
//      if (code == -1 && lParam == 0) {
//              Menu menu = shell.activeMenu;
//              while (menu != null) {
//                      /*
//                      * It is possible (but unlikely), that application
//                      * code could have disposed the widget in the hide
//                      * event.  If this happens, stop searching up the
//                      * parent list because there is no longer a link
//                      * to follow.
//                      */
//                      menu.sendEvent (SWT.Hide);
//                      if (menu.isDisposed ()) break;
//                      menu = menu.getParentMenu ();
//              }
//              /*
//              * The shell may be disposed because of sending the hide
//              * event to the last active menu menu but setting a field
//              * to null in a destroyed widget is not harmful.
//              */
//              shell.activeMenu = null;
//              return null;
//      }
//      if ((code & OS.MF_SYSMENU) != 0) return null;
//      if ((code & OS.MF_HILITE) != 0) {
//              MenuItem item = null;
//              Decorations menuShell = menuShell ();
//              if ((code & OS.MF_POPUP) != 0) {
//                      int index = wParam & 0xFFFF;
//                      MENUITEMINFO info = new MENUITEMINFO ();
//                      info.cbSize = MENUITEMINFO.sizeof;
//                      info.fMask = OS.MIIM_SUBMENU;
//                      if (OS.GetMenuItemInfo (lParam, index, true, info)) {
//                              Menu newMenu = menuShell.findMenu (info.hSubMenu);
//                              if (newMenu != null) item = newMenu.cascade;
//                      }
//              } else {
//                      Menu newMenu = menuShell.findMenu (lParam);
//                      if (newMenu != null) {
//                              int id = wParam & 0xFFFF;
//                              item = menuShell.findMenuItem (id);
//                      }
//                      Menu oldMenu = shell.activeMenu;
//                      if (oldMenu != null) {
//                              Menu ancestor = oldMenu;
//                              while (ancestor != null && ancestor != newMenu) {
//                                      ancestor = ancestor.getParentMenu ();
//                              }
//                              if (ancestor == newMenu) {
//                                      ancestor = oldMenu;
//                                      while (ancestor != newMenu) {
//                                              /*
//                                              * It is possible (but unlikely), that application
//                                              * code could have disposed the widget in the hide
//                                              * event or the item about to be armed.  If this
//                                              * happens, stop searching up the ancestor list
//                                              * because there is no longer a link to follow.
//                                              */
//                                              ancestor.sendEvent (SWT.Hide);
//                                              if (ancestor.isDisposed ()) break;
//                                              ancestor = ancestor.getParentMenu ();
//                                      }
//                                      /*
//                                      * The shell and/or the item could be disposed when
//                                      * processing hide events from above.  If this happens,
//                                      * ensure that the shell is not accessed and that no
//                                      * arm event is sent to the item.
//                                      */
//                                      if (!shell.isDisposed ()) {
//                                              if (newMenu != null && newMenu.isDisposed ()) {
//                                                      newMenu = null;
//                                              }
//                                              shell.activeMenu = newMenu;
//                                      }
//                                      if (item != null && item.isDisposed ()) item = null;
//                              }
//                      }
//              }
//              if (item != null) item.sendEvent (SWT.Arm);
//      }
//      return null;
//}
//
//LRESULT WM_MOUSEACTIVATE (int wParam, int lParam) {
//      return null;
//}
//
//LRESULT WM_MOUSEHOVER (int wParam, int lParam) {
//      int pos = OS.GetMessagePos ();
//      Event event = new Event ();
//      POINT pt = new POINT ();
//      pt.x = (short) (pos & 0xFFFF);
//      pt.y = (short) (pos >> 16);
//      OS.ScreenToClient (handle, pt);
//      event.x = pt.x;
//      event.y = pt.y;
//      postEvent (SWT.MouseHover, event);
//      return null;
//}
//
//LRESULT WM_MOUSELEAVE (int wParam, int lParam) {
//      int pos = OS.GetMessagePos ();
//      Event event = new Event ();
//      POINT pt = new POINT ();
//      pt.x = (short) (pos & 0xFFFF);
//      pt.y = (short) (pos >> 16);
//      OS.ScreenToClient (handle, pt);
//      event.x = pt.x;
//      event.y = pt.y;
//      postEvent (SWT.MouseExit, event);
//      return null;
//}
//
//LRESULT WM_MOUSEMOVE (int wParam, int lParam) {
//      if (!OS.IsWinCE) {
//              boolean hooksEnter = hooks (SWT.MouseEnter);
//              if (hooksEnter || hooks (SWT.MouseExit) || hooks (SWT.MouseHover)) {
//                      TRACKMOUSEEVENT lpEventTrack = new TRACKMOUSEEVENT ();
//                      lpEventTrack.cbSize = TRACKMOUSEEVENT.sizeof;
//                      lpEventTrack.dwFlags = OS.TME_QUERY;
//                      lpEventTrack.hwndTrack = handle;
//                      OS.TrackMouseEvent (lpEventTrack);
//                      if (lpEventTrack.dwFlags == 0) {
//                              lpEventTrack.dwFlags = OS.TME_LEAVE | OS.TME_HOVER;
//                              lpEventTrack.hwndTrack = handle;
//                              OS.TrackMouseEvent (lpEventTrack);
//                              if (hooksEnter) {
//                                      Event event = new Event ();
//                                      event.x = (short) (lParam & 0xFFFF);
//                                      event.y = (short) (lParam >> 16);
//                                      postEvent (SWT.MouseEnter, event);
//                              }
//                      } else {
//                              lpEventTrack.dwFlags = OS.TME_HOVER;
//                              OS.TrackMouseEvent (lpEventTrack);
//                      }
//              }
//      }
//      Display display = getDisplay ();
//      int pos = OS.GetMessagePos ();
//      if (pos != display.lastMouse) {
//              display.lastMouse = pos;
//              sendMouseEvent (SWT.MouseMove, 0, OS.WM_MOUSEMOVE, wParam, lParam);
//      }
//      return null;
//}
//
//LRESULT WM_MOUSEWHEEL (int wParam, int lParam) {
//      return null;
//}

//@@TODO(dmik)
//LRESULT WM_NCACTIVATE (int wParam, int lParam) {
//      return null;
//}
//
//LRESULT WM_NCCALCSIZE (int wParam, int lParam) {
//      return null;
//}
//
//LRESULT WM_NCHITTEST (int wParam, int lParam) {
//      if (!isActive ()) return new LRESULT (OS.HTTRANSPARENT);
//      return null;
//}
//
//LRESULT WM_NOTIFY (int wParam, int lParam) {
//      NMHDR hdr = new NMHDR ();
//      OS.MoveMemory (hdr, lParam, NMHDR.sizeof);
//      int hwnd = hdr.hwndFrom;
//      if (hwnd == 0) return null;
//      Control control = WidgetTable.get (hwnd);
//      if (control == null) return null;
//      return control.wmNotifyChild (wParam, lParam);
//}

MRESULT WM_PAINT (int mp1, int mp2) {
    /* Exit early - don't draw the background */
    if (!hooks (SWT.Paint)) return null;

    /* lazily obtain a long-term cached micro presentation space */
    GCData data = new GCData ();
    if (hps == 0) {
        hps = internal_new_GC (data);
        if (hps == 0) SWT.error(SWT.ERROR_NO_HANDLES);
        data.doInit = true;
    }

    /* Get the damage */
    int hrgn = OS.GpiCreateRegion (hps, 0, null);
    OS.WinQueryUpdateRegion (handle, hrgn);
    
    callWindowProc (OS.WM_PAINT, mp1, mp2);

    //@@TODO(dmik): is there any solution other than below??
    /*
     * Save WS_SYNCPAINT bit and set it to 0 to safely
     * invalidate ourselves during WM_PAINT.
     */
    int bits = OS.WinQueryWindowULong (handle, OS.QWL_STYLE);
    OS.WinSetWindowULong (handle, OS.QWL_STYLE, bits & (~OS.WS_SYNCPAINT));
    OS.WinInvalidateRegion (handle, hrgn, false);
    OS.WinSetWindowULong (handle, OS.QWL_STYLE, bits);
    OS.GpiDestroyRegion (hps, hrgn);

    /* Create the paint GC */
    data.rcl = new RECTL ();
    data.hps = hps;
    GC gc = GC.pm_new (this, data);
    
    /* Send the paint event */
    Event event = new Event ();
    event.gc = gc;
    event.x = data.rcl.xLeft;
    event.y = getHeight () - data.rcl.yTop;
    event.width = data.rcl.xRight - data.rcl.xLeft;
    event.height = data.rcl.yTop - data.rcl.yBottom;
    /*
    * It is possible (but unlikely), that application
    * code could have disposed the widget in the paint
    * event.  If this happens, attempt to give back the
    * paint GC anyways because this is a scarce Windows
    * resource.
    */
    sendEvent (SWT.Paint, event);
    // widget could be disposed at this point

    /* Dispose the paint GC */
    event.gc = null;
    gc.dispose ();

    return MRESULT.ZERO;
}

//@@TODO(dmik)
//LRESULT WM_PASTE (int wParam, int lParam) {
//      return null;
//}
//
//LRESULT WM_PRINTCLIENT (int wParam, int lParam) {
//      return null;
//}
//
//LRESULT WM_QUERYENDSESSION (int wParam, int lParam) {
//      return null;
//}
//
//LRESULT WM_RBUTTONDBLCLK (int wParam, int lParam) {
//      /*
//      * Feature in Windows. Windows sends the following
//      * messages when the user double clicks the mouse:
//      *
//      *       WM_RBUTTONDOWN          - mouse down
//      *       WM_RBUTTONUP            - mouse up
//      *       WM_RBUTTONDBLCLK        - double click
//      *       WM_LBUTTONUP            - mouse up
//      *
//      * Applications that expect matching mouse down/up
//      * pairs will not see the second mouse down.  The
//      * fix is to send a mouse down event.
//      */
//      sendMouseEvent (SWT.MouseDown, 3, OS.WM_RBUTTONDOWN, wParam, lParam);
//      sendMouseEvent (SWT.MouseDoubleClick, 3, OS.WM_RBUTTONDBLCLK, wParam, lParam);
//      int result = callWindowProc (OS.WM_RBUTTONDBLCLK, wParam, lParam);
//      if (OS.GetCapture () != handle) OS.SetCapture (handle);
//       return new LRESULT (result);
//}
//
//LRESULT WM_RBUTTONDOWN (int wParam, int lParam) {
//      sendMouseEvent (SWT.MouseDown, 3, OS.WM_RBUTTONDOWN, wParam, lParam);
//      int result = callWindowProc (OS.WM_RBUTTONDOWN, wParam, lParam);
//      if (OS.GetCapture () != handle) OS.SetCapture (handle);
//      return new LRESULT (result);
//}
//
//LRESULT WM_RBUTTONUP (int wParam, int lParam) {
//      sendMouseEvent (SWT.MouseUp, 3, OS.WM_RBUTTONUP, wParam, lParam);
//      int result = callWindowProc (OS.WM_RBUTTONUP, wParam, lParam);
//      if ((wParam & (OS.MK_LBUTTON | OS.MK_MBUTTON | OS.MK_RBUTTON)) == 0) {
//              if (OS.GetCapture () == handle) OS.ReleaseCapture ();
//      }
//      return new LRESULT (result);
//}

//@@TODO(dmik)
//LRESULT WM_SETCURSOR (int wParam, int lParam) {
//      int hitTest = lParam & 0xFFFF;
//      if (hitTest == OS.HTCLIENT) {
//              Control control = WidgetTable.get (wParam);
//              if (control == null) return null;
//              int hCursor = control.findCursor ();
//              if (hCursor != 0) {
//                      OS.SetCursor (hCursor);
//                      return LRESULT.ONE;
//              }
//      }
//      return null;
//}

MRESULT WM_SETFOCUS (int mp1, int mp2) {
    callWindowProc (OS.WM_SETFOCUS, mp1, mp2);
    Shell shell = getShell ();
    
    boolean gotFocus = OS.SHORT1FROMMP (mp2) > 0;
    
    /*
    * It is possible (but unlikely), that application
    * code could have disposed the widget in the focus
    * in/out event.  If this happens keep going to send
    * the activate/deactivate events.
    */
    if (gotFocus)
        sendEvent (SWT.FocusIn);
    else
        sendEvent (SWT.FocusOut);
    // widget could be disposed at this point
    
    /*
    * It is possible that the shell may be
    * disposed at this point.  If this happens
    * don't send the activate and deactivate
    * events.
    */
    if (!shell.isDisposed ()) {
        if (gotFocus) {
            shell.setActiveControl (this);
        } else {
            Display display = getDisplay ();
            Control control = display.findControl (mp1);
            if (control == null || shell != control.getShell ()) {
                shell.setActiveControl (null);
            }
        }
    }
    
    return MRESULT.ZERO;
}

//@@TODO (dmik): later
//LRESULT WM_SETTINGCHANGE (int wParam, int lParam) {
//      return null;
//}
//
//LRESULT WM_SETFONT (int wParam, int lParam) {
//      return null;
//}
//
//LRESULT WM_SETREDRAW (int wParam, int lParam) {
//      return null;
//}
//
//LRESULT WM_SHOWWINDOW (int wParam, int lParam) {
//      return null;
//}

MRESULT WM_SIZE (int mp1, int mp2) {
    // set the default view matrix to do the horizontal flipping
    if (hps != 0) {
        final int[] matrix = {0x00010000, 0, 0, 0, 0xFFFF0000, 0, 0, 0, 1};
        matrix[7] = OS.SHORT2FROMMP (mp2) - 1;
        OS.GpiSetDefaultViewMatrix (hps, matrix.length, matrix, OS.TRANSFORM_REPLACE);
    }
    sendEvent (SWT.Resize);
    // widget could be disposed at this point
    return null;
}

//@@TODO (dmik): later
//LRESULT WM_SYSCHAR (int wParam, int lParam) {
//      Display display = getDisplay ();
//
//      /* Set last key and last ascii because a new key has been typed */
//      display.lastAscii = display.lastKey = wParam;
//      display.lastVirtual = display.isVirtualKey (display.lastKey);
//
//      /* Do not issue a key down if a menu bar mnemonic was invoked */
//      if (!hooks (SWT.KeyDown)) return null;
//      display.mnemonicKeyHit = true;
//      int result = callWindowProc (OS.WM_SYSCHAR, wParam, lParam);
//      if (!display.mnemonicKeyHit) {
//              sendKeyEvent (SWT.KeyDown, OS.WM_SYSCHAR, wParam, lParam);
//      }
//      display.mnemonicKeyHit = false;
//      return new LRESULT (result);
//}
//
//LRESULT WM_SYSCOLORCHANGE (int wParam, int lParam) {
//      return null;
//}
//
//LRESULT WM_SYSCOMMAND (int wParam, int lParam) {
//      /*
//      * Check to see if the command is a system command or
//      * a user menu item that was added to the System menu.
//      * When a user item is added to the System menu,
//      * WM_SYSCOMMAND must always return zero.
//      */
//      if ((wParam & 0xF000) == 0) {
//              Decorations shell = menuShell ();
//              if (shell.isEnabled ()) {
//                      MenuItem item = shell.findMenuItem (wParam & 0xFFFF);
//                      if (item != null) item.wmCommandChild (wParam, lParam);
//              }
//              return LRESULT.ZERO;
//      }
//
//      /* Process the System Command */
//      int cmd = wParam & 0xFFF0;
//      switch (cmd) {
//              case OS.SC_CLOSE:
//                      int hwndShell = menuShell ().handle;
//                      int bits = OS.GetWindowLong (hwndShell, OS.GWL_STYLE);
//                      if ((bits & OS.WS_SYSMENU) == 0) return LRESULT.ZERO;
//                      break;
//              case OS.SC_KEYMENU:
//                      if (hooks (SWT.KeyDown) || hooks (SWT.KeyUp)) {
//                              Decorations shell = menuShell ();
//                              Menu menu = shell.getMenuBar ();
//                              if (menu != null) {
//                                      char key = mbcsToWcs (lParam);
//                                      if (key != 0) {
//                                              key = Character.toUpperCase (key);
//                                              MenuItem [] items = menu.getItems ();
//                                              for (int i=0; i<items.length; i++) {
//                                                      MenuItem item = items [i];
//                                                      String text = item.getText ();
//                                                      char mnemonic = findMnemonic (text);
//                                                      if (text.length () > 0 && mnemonic == 0) {
//                                                              char ch = text.charAt (0);
//                                                              if (Character.toUpperCase (ch) == key) {
//                                                                      Display display = getDisplay ();
//                                                                      display.mnemonicKeyHit = false;
//                                                                      return LRESULT.ZERO;
//                                                              }
//                                                      }
//                                              }
//                                      }
//                              }
//                      }
//                      // FALL THROUGH
//              case OS.SC_HSCROLL:
//              case OS.SC_VSCROLL:
//                      /*
//                      * Do not allow keyboard traversal of the menu bar
//                      * or scrolling when the shell is not enabled.
//                      */
//                      Decorations shell = menuShell ();
//                      if (!shell.isEnabled () || !shell.isActive ()) {
//                              return LRESULT.ZERO;
//                      }
//                      break;
//              case OS.SC_MINIMIZE:
//                      /* Save the focus widget when the shell is minimized */
//                      menuShell ().saveFocus ();
//                      break;
//      }
//      return null;
//}
//
//LRESULT WM_SYSKEYDOWN (int wParam, int lParam) {
//      Display display = getDisplay ();
//
//      /*
//      * Feature in Windows.  WM_SYSKEYDOWN is sent when
//      * the user presses ALT-<aKey> or F10 without the ALT key.
//      * In order to issue events for F10 (without the ALT key)
//      * but ignore all other key presses without the ALT key,
//      * make F10 a special case.
//      */
//      if (wParam != OS.VK_F10) {
//              /* Make sure WM_SYSKEYDOWN was sent by ALT-<aKey>. */
//              if ((lParam & 0x20000000) == 0) return null;
//      }
//
//      /* If are going to get a WM_SYSCHAR, ignore this message. */
//      if (OS.MapVirtualKey (wParam, 2) != 0) return null;
//
//      /* Ignore repeating keys for modifiers by testing key down state. */
//      if ((wParam == OS.VK_SHIFT) || (wParam == OS.VK_MENU) ||
//              (wParam == OS.VK_CONTROL) || (wParam == OS.VK_CAPITAL) ||
//              (wParam == OS.VK_NUMLOCK) || (wParam == OS.VK_SCROLL))
//                      if ((lParam & 0x40000000) != 0) return null;
//
//      /* Set last key and clear last ascii because a new key has been typed. */
//      display.lastAscii = 0;
//      display.lastKey = wParam;
//      display.lastVirtual = true;
//
//      if (!sendKeyEvent (SWT.KeyDown, OS.WM_SYSKEYDOWN, wParam, lParam)) {
//              return LRESULT.ZERO;
//      }
//      return null;
//}
//
//LRESULT WM_SYSKEYUP (int wParam, int lParam) {
//      return WM_KEYUP (wParam, lParam);
//}
//
//LRESULT WM_TIMER (int wParam, int lParam) {
//      return null;
//}
//
//LRESULT WM_UNDO (int wParam, int lParam) {
//      return null;
//}
//
  MRESULT WM_VSCROLL (int mp1, int mp2) {
      int hwndChild = OS.WinWindowFromID (handle, OS.SHORT1FROMMP (mp1));
      Control control = WidgetTable.get (hwndChild);
      if (control == null) return null;
      return control.wmScrollChild (mp1, mp2);
  }
//
//LRESULT WM_WINDOWPOSCHANGING (int wParam, int lParam) {
//      return null;
//}

MRESULT WM_WINDOWPOSCHANGED (int mp1, int mp2) {
    int result = callWindowProc (OS.WM_WINDOWPOSCHANGED, mp1, mp2);
    /*
     *  Feature in OS/2. Regular windows do not receive the WM_MOVE message
     *  unless their window class has the CS_MOVENOTIFY flag (which is
     *  the case for all standard controls). From the other side, some windows
     *  (it looks like all except the client window) do not receive the WM_SIZE
     *  message. So, we send SWT.Move and SWT.Resize events from here (also
     *  taking the coordinate space flipping into account).
     */
    SWP swpNew = new SWP ();
    OS.objcpy (swpNew, mp1);
    if ((swpNew.fl & OS.SWP_NOADJUST) == 0) {
        SWP swpOld = new SWP ();
        OS.objcpy (swpOld, (mp1 + SWP.sizeof));
        if ((swpNew.x != swpOld.x) || (swpNew.y + swpNew.cy != swpOld.y + swpOld.cy)) {
            sendEvent (SWT.Move);
            // widget could be disposed at this point
        }
        if ((swpNew.cx != swpOld.cx || swpNew.cy != swpOld.cy)) {
            WM_SIZE ((swpOld.cy << 16) | swpOld.cx, (swpNew.cy << 16) | swpNew.cx);
            // widget could be disposed at this point
        }
    }
    return new MRESULT (result);
}

//@@TODO(dmik): seems we don't need it (presparams replace this)
//LRESULT wmColorChild (int wParam, int lParam) {
//      if (background == -1 && foreground == -1) return null;
//      int forePixel = foreground, backPixel = background;
//      if (forePixel == -1) forePixel = defaultForeground ();
//      if (backPixel == -1) backPixel = defaultBackground ();
//      OS.SetTextColor (wParam, forePixel);
//      OS.SetBkColor (wParam, backPixel);
//      return new LRESULT (findBrush (backPixel));
//}

MRESULT wmCommandChild (int mp1, int mp2) {
        return null;
}

MRESULT wmControlChild (int mp1, int mp2) {
        return null;
}

MRESULT wmDrawChild (int mp1, int mp2) {
      return null;
}

//@@TODO (dmik): 
//LRESULT wmMeasureChild (int wParam, int lParam) {
//      return null;
//}
//
//LRESULT wmNotifyChild (int wParam, int lParam) {
//      return null;
//}

MRESULT wmScrollChild (int mp1, int mp2) {
      return null;
}

}

