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

import org.eclipse.swt.internal.*;
import org.eclipse.swt.internal.pm.*;
import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.events.*;

/**
 * Instances of this class provide the appearance and
 * behavior of <code>Shells</code>, but are not top
 * level shells or dialogs. Class <code>Shell</code>
 * shares a significant amount of code with this class,
 * and is a subclass.
 * <p>
 * Instances are always displayed in one of the maximized, 
 * minimized or normal states:
 * <ul>
 * <li>
 * When an instance is marked as <em>maximized</em>, the
 * window manager will typically resize it to fill the
 * entire visible area of the display, and the instance
 * is usually put in a state where it can not be resized 
 * (even if it has style <code>RESIZE</code>) until it is
 * no longer maximized.
 * </li><li>
 * When an instance is in the <em>normal</em> state (neither
 * maximized or minimized), its appearance is controlled by
 * the style constants which were specified when it was created
 * and the restrictions of the window manager (see below).
 * </li><li>
 * When an instance has been marked as <em>minimized</em>,
 * its contents (client area) will usually not be visible,
 * and depending on the window manager, it may be
 * "iconified" (that is, replaced on the desktop by a small
 * simplified representation of itself), relocated to a
 * distinguished area of the screen, or hidden. Combinations
 * of these changes are also possible.
 * </li>
 * </ul>
 * </p>
 * Note: The styles supported by this class must be treated
 * as <em>HINT</em>s, since the window manager for the
 * desktop on which the instance is visible has ultimate
 * control over the appearance and behavior of decorations.
 * For example, some window managers only support resizable
 * windows and will always assume the RESIZE style, even if
 * it is not set.
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>BORDER, CLOSE, MIN, MAX, NO_TRIM, RESIZE, TITLE, ON_TOP, TOOL</dd>
 * <dt><b>Events:</b></dt>
 * <dd>(none)</dd>
 * </dl>
 * Class <code>SWT</code> provides two "convenience constants"
 * for the most commonly required style combinations:
 * <dl>
 * <dt><code>SHELL_TRIM</code></dt>
 * <dd>
 * the result of combining the constants which are required
 * to produce a typical application top level shell: (that 
 * is, <code>CLOSE | TITLE | MIN | MAX | RESIZE</code>)
 * </dd>
 * <dt><code>DIALOG_TRIM</code></dt>
 * <dd>
 * the result of combining the constants which are required
 * to produce a typical application dialog shell: (that 
 * is, <code>TITLE | CLOSE | BORDER</code>)
 * </dd>
 * </dl>
 * <p>
 * IMPORTANT: This class is intended to be subclassed <em>only</em>
 * within the SWT implementation.
 * </p>
 *
 * @see #getMinimized
 * @see #getMaximized
 * @see Shell
 * @see SWT
 */

public class Decorations extends Canvas {
//@@TODO(dmik)    
//	Image image;
	Menu menuBar;
	Menu [] menus;
	MenuItem [] items;
	Control savedFocus;
	Button defaultButton, saveDefault;
//@@TODO (dmik): later    
//	int swFlags, hAccel, nAccel;
//	int hwndCB, hwndTB, hIcon;
//	
//	/*
//	* The start value for WM_COMMAND id's.
//	* Windows reserves the values 0..100.
//	*/
//@@TODO (lpino): For now we keep this in place
	static final int ID_START = 100;

    int frameHandle;

    static final int FrameProc; 
    static final PSZ FrameClass = PSZ.getAtom (OS.WC_FRAME);
    static {
        CLASSINFO pclsiClassInfo = new CLASSINFO ();
        OS.WinQueryClassInfo (OS.NULLHANDLE, FrameClass, pclsiClassInfo);
        FrameProc = pclsiClassInfo.pfnWindowProc;
    }
    
    /*
     *  Feature in OS/2. When null or zero length string is passed to
     *  WinCreateWindow() when creating WC_FRAME OS/2 uses the title of the
     *  parent process as the window title in the window list. We use the
     *  one-space string to override this behavior.
     */
    static final PSZ EmptyTitle = new PSZ (" ");
    
/**
 * Prevents uninitialized instances from being created outside the package.
 */
Decorations () {
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
 * @see SWT#CLOSE
 * @see SWT#MIN
 * @see SWT#MAX
 * @see SWT#RESIZE
 * @see SWT#TITLE
 * @see SWT#NO_TRIM
 * @see SWT#SHELL_TRIM
 * @see SWT#DIALOG_TRIM
 * @see SWT#ON_TOP
 * @see SWT#TOOL
 * @see Widget#checkSubclass
 * @see Widget#getStyle
 */
public Decorations (Composite parent, int style) {
    super (parent, checkStyle (style));
}


void add (Menu menu) {
	if (menus == null) menus = new Menu [4];
	for (int i=0; i<menus.length; i++) {
		if (menus [i] == null) {
			menus [i] = menu;
			return;
		}
	}
	Menu [] newMenus = new Menu [menus.length + 4];
	newMenus [menus.length] = menu;
	System.arraycopy (menus, 0, newMenus, 0, menus.length);
	menus = newMenus;
}

void add (MenuItem item) {
	if (items == null) items = new MenuItem [12];
	for (int i=0; i<items.length; i++) {
		if (items [i] == null) {
			item.id = i + ID_START;
			items [i] = item;
			return;
		}
	}
	item.id = items.length + ID_START;
	MenuItem [] newItems = new MenuItem [items.length + 12];
	newItems [items.length] = item;
	System.arraycopy (items, 0, newItems, 0, items.length);
	items = newItems;
}

void bringToTop () {
//@@TODO(dmik): how to take SWT.ON_TOP flag into account under OS/2?
//    /*
//    * This code is intentionally commented.  On some platforms,
//    * the ON_TOP style creates a shell that will stay on top
//    * of every other shell on the desktop.  Using SetWindowPos ()
//    * with HWND_TOP caused problems on Windows so this code is
//    * commented out until this functionality is specified and
//    * the problems are fixed.
//    */
////    if ((style & SWT.ON_TOP) != 0) {
////	  int flags = OS.SWP_NOSIZE | OS.SWP_NOMOVE | OS.SWP_NOACTIVATE; 
////        OS.SetWindowPos (handle, OS.HWND_TOP, 0, 0, 0, 0, flags);
////    } else {
//        OS.BringWindowToTop (handle);
////    }
    OS.WinSetWindowPos (frameHandle, OS.HWND_TOP, 0, 0, 0, 0, 
        OS.SWP_ZORDER | OS.SWP_ACTIVATE);
}

static int checkStyle (int style) {
    /* 
     *  FCF_CLOSEBUTTON requires FCF_SYSMENU. But currently SWT.CLOSE =
     *  SWT.MENU, so we don't actually need the following line.
     */
    //if ((style & (SWT.CLOSE)) != 0) style |= SWT.MENU;
    
    /*  
     *  Altough OS/2 allows to create min/max/sysmenu/close
     *  buttons without creating a title, here we disable this behavior
     *  because otherwise it will be left blank by the default frame
     *  window procedure leaves title space -- it doesn't look pretty.
     */
    if ((style & (SWT.MENU | SWT.MIN | SWT.MAX | SWT.CLOSE)) != 0) {
        style |= SWT.TITLE;
    }
    return style;
}

protected void checkSubclass () {
    if (!isValidSubclass ()) error (SWT.ERROR_INVALID_SUBCLASS);
}

Control computeTabGroup () {
	return this;
}

Control computeTabRoot () {
	return this;
}

public Rectangle computeTrim (int x, int y, int width, int height) {
	checkWidget ();
    
//@@TEMP(dmik): in the future should take scroll bars into account (and menu)?
//	/* Get the size of the trimmings */
//	RECT rect = new RECT ();
//	OS.SetRect (rect, x, y, x + width, y + height);
//	int bits = OS.GetWindowLong (handle, OS.GWL_STYLE);
//	boolean hasMenu = OS.IsWinCE ? false : OS.GetMenu (handle) != 0;
//	OS.AdjustWindowRectEx (rect, bits, hasMenu, OS.GetWindowLong (handle, OS.GWL_EXSTYLE));
//
//	/* Get the size of the scroll bars */
//	if (horizontalBar != null) rect.bottom += OS.GetSystemMetrics (OS.SM_CYHSCROLL);
//	if (verticalBar != null) rect.right += OS.GetSystemMetrics (OS.SM_CXVSCROLL);
//
//	/* Get the height of the menu bar */
//	if (hasMenu) {
//		RECT testRect = new RECT ();
//		OS.SetRect (testRect, 0, 0, rect.right - rect.left, rect.bottom - rect.top);
//		OS.SendMessage (handle, OS.WM_NCCALCSIZE, 0, testRect);
//		while ((testRect.bottom - testRect.top) < height) {
//			rect.top -= OS.GetSystemMetrics (OS.SM_CYMENU) - OS.GetSystemMetrics (OS.SM_CYBORDER);
//			OS.SetRect(testRect, 0, 0, rect.right - rect.left, rect.bottom - rect.top);
//			OS.SendMessage (handle, OS.WM_NCCALCSIZE, 0, testRect);
//		}
//	}
//	return new Rectangle (rect.left, rect.top, rect.right - rect.left, rect.bottom - rect.top);
    RECTL rcl = new RECTL ();
    rcl.xLeft = x; rcl.yBottom = y;
    rcl.xRight = x + width; rcl.yTop = y + height;
    OS.WinCalcFrameRect (frameHandle, rcl, false);
    return new Rectangle (rcl.xLeft, rcl.yBottom,
        rcl.xRight - rcl.xLeft, rcl.yTop - rcl.yBottom);
}

//@@TODO(dmik)
//void createAcceleratorTable () {
//	hAccel = nAccel = 0;
//	int maxAccel = 0;
//	if (menuBar == null || items == null) {
//		if (!OS.IsPPC) return;
//		maxAccel = 1;
//	} else {
//		maxAccel = OS.IsPPC ? items.length + 1 : items.length;
//	}
//	int size = ACCEL.sizeof;
//	ACCEL accel = new ACCEL ();
//	byte [] buffer1 = new byte [size];	
//	byte [] buffer2 = new byte [maxAccel * size];
//	if (menuBar != null && items != null) {
//		for (int i=0; i<items.length; i++) {
//			MenuItem item = items [i];
//			if (item != null && item.accelerator != 0) {
//				Menu parent = item.parent;
//				while (parent != null && parent != menuBar) {
//					parent = parent.getParentMenu ();
//				}
//				if (parent == menuBar) {
//					item.fillAccel (accel);
//					OS.MoveMemory (buffer1, accel, size);
//					System.arraycopy (buffer1, 0, buffer2, nAccel * size, size);
//					nAccel++;
//				}
//			}
//		}
//	}
//	if (nAccel != 0) hAccel = OS.CreateAcceleratorTable (buffer2, nAccel);
//}

void createHandle () {
    /*
     *  Create a frame window.
     *
     *  Note: if the parent and owned differ, the owner must be a WC_FRAME
     *  window (except the sutiation when the parent is HWND_DESKTOP and the
     *  owner is NULLHANDLE). Otherwise, destroyinh the owner will most likely
     *  not destroy the ownee, but SWT relies on this (see
     *  Widget.destroyWidget()). Also, if the owner is not an SWT window,
     *  resources for this ownee window are not freed.
     *
     *  For embedded Shell windows it is solved by handling the WM_DESTROY
     *  message, but the shell's parent (in SWT terminology) still has to be
     *  a WC_FRAME window since its handle is used as an owner (see below).
     */
    int hwndOwner = OS.NULLHANDLE;
    if (parent != null){
        if (parent instanceof Shell){
            hwndOwner = ((Shell) parent).frameHandle;
        }
        else{
            hwndOwner = parent.handle;
        }
    }
    int hwndParent = OS.HWND_DESKTOP;
    /*
     *  Shell constructor presets the handle to OS.HWND_DESKTOP (which is
     *  sensible if it has a parent) to cause secondary Shells to be created.
     */
    if (handle != 0)
        hwndParent = handle;
    else
        if (parent != null)
            if (parent instanceof Shell)
                hwndParent = ((Shell) parent).frameHandle;
            else
                hwndParent = parent.handle;
    frameHandle = OS.WinCreateWindow (
        hwndParent,
        windowFrameClass (),
        null,
        widgetFrameStyle (),
        0, getParentHeight(), 0, 0,
        hwndOwner,
        OS.HWND_TOP,
        0,
        0,
        0
    );
    if (frameHandle == 0) error (SWT.ERROR_NO_HANDLES);
//@@TODO (dmik): debug code, remove when no more necessary 
//System.out.println (
//    "Decorations.createHandle(): WC_FRAME has been created:\n" +
//    "    hwnd = " + Integer.toHexString (frameHandle) + "\n" +
//    "    hwnd.parent = " + Integer.toHexString (hwndParent) + "\n" +
//    "    hwnd.owner = " + Integer.toHexString (hwndOwner) + "\n" +
//    "    class = " + windowFrameClass() + "\n" +
//    "    style = " + Integer.toHexString (widgetFrameStyle())
//);
    FRAMECDATA fcdata = new FRAMECDATA ();
    fcdata.flCreateFlags = widgetFrameFlags ();
    OS.WinCreateFrameControls (frameHandle, fcdata, null);
    OS.WinSendMsg (frameHandle, OS.WM_UPDATEFRAME, fcdata.flCreateFlags, 0);
    /*
     *  Create a cliend window.
     */
    handle = OS.WinCreateWindow (
        frameHandle,
        windowClass (),
        EmptyTitle,
        widgetStyle (),
        0, 0, 0, 0,
        frameHandle,
        OS.HWND_BOTTOM,
        OS.FID_CLIENT,
        0,
        0
    );
    if (handle == 0) error (SWT.ERROR_NO_HANDLES);
    state |= CANVAS;
//@@TODO (dmik): debug code, remove when no more necessary 
//System.out.println (
//    "Decorations.createHandle(): FID_CLIENT window has been created:\n" +
//    "    hwnd = " + Integer.toHexString (handle) + "\n" +
//    "    hwnd.parent = hwnd.owner = " + Integer.toHexString (frameHandle) + "\n" +
//    "    class = " + windowClass() + "\n" +
//    "    style = " + Integer.toHexString (widgetStyle())
//);
//@@TODO(dmik): do something for MDI windows if needed    
//    if (parent == null) return;
//    setParent ();
//    setSystemMenu ();
}

//@@TODO(dmik): remove/later?
//void createWidget () {
//    super.createWidget ();
//    swFlags = OS.IsWinCE ? OS.SW_SHOWMAXIMIZED : OS.SW_SHOWNOACTIVATE;
//    hAccel = -1;
//}

//@@TODO (dmik): later
//void destroyAcceleratorTable () {
//	if (hAccel != 0 && hAccel != -1) OS.DestroyAcceleratorTable (hAccel);
//	hAccel = -1;
//}

void destroyWidget () {
    int hwnd = frameHandle;
    releaseHandle ();
    if (hwnd != 0) {
//@@TEMP(dmik): debug code, remove when no more necessary 
//System.out.println (
//    "Decorations.destroyWidget(): WC_FRAME is being derstroyed:\n    hwnd = " +
//    Integer.toHexString (hwnd)
//);
        OS.WinDestroyWindow (hwnd);
    }
}


Menu findMenu (int hMenu) {
	if (menus == null) return null;
	for (int i=0; i<menus.length; i++) {
		Menu menu = menus [i];
		if ((menu != null) && (hMenu == menu.handle)) return menu;
	}
	return null;
}

MenuItem findMenuItem (int id) {
	if (items == null) return null;
	id = id - ID_START;
	if (0 <= id && id < items.length) return items [id];
	return null;
}

int getBounds (SWP swp) {
    if ((OS.WinQueryWindowULong (frameHandle, OS.QWL_STYLE) & OS.WS_MINIMIZED) != 0) {
        int parentHeight = getParentHeight();
        swp.x = OS.WinQueryWindowUShort (frameHandle, OS.QWS_XRESTORE);
        swp.y = OS.WinQueryWindowUShort (frameHandle, OS.QWS_YRESTORE);
        swp.cx = OS.WinQueryWindowUShort (frameHandle, OS.QWS_CXRESTORE);
        swp.cy = OS.WinQueryWindowUShort (frameHandle, OS.QWS_CYRESTORE);
        swp.y = parentHeight - (swp.y + swp.cy);
        return parentHeight;
    } else {
        int parentHeight = getParentHeight();
        OS.WinQueryWindowPos (frameHandle, swp);
        swp.y = parentHeight - (swp.y + swp.cy);
        return parentHeight;
    }
}

public Rectangle getClientArea () {
	checkWidget ();
    int bits = OS.WinQueryWindowULong (frameHandle, OS.QWL_STYLE);
    if ((bits & OS.WS_MINIMIZED) != 0) {
        /*  
         *  Feature in OS/2. If the frame is minimized, WM_CALCFRAMERECT does
         *  not calculate the client area size -- it simply leaves the passed
         *  rectangle unchanged. The solution is to temporary clear the
         *  WS_MINIMIZED bit. 
         */
        SWP swp = new SWP ();
        getBounds (swp);
        RECTL rcl = new RECTL ();
        rcl.xLeft = 0; rcl.yBottom = 0;
        rcl.xRight = swp.cx; rcl.yTop = swp.cy;
        OS.WinSetWindowULong (frameHandle, OS.QWL_STYLE, bits & (~OS.WS_MINIMIZED));
        OS.WinSendMsg (frameHandle, OS.WM_CALCFRAMERECT, rcl, OS.TRUE);
        OS.WinSetWindowULong (frameHandle, OS.QWL_STYLE, bits);
        return new Rectangle (0, 0, rcl.xRight - rcl.xLeft, rcl.yTop - rcl.yBottom);
    } else {
//@@TODO (dmik): in the future should take scroll bars into account
        RECTL rcl = new RECTL ();
        OS.WinQueryWindowRect (handle, rcl);
        return new Rectangle (0, 0, rcl.xRight, rcl.yTop);
    }
}

/**
 * Returns the receiver's default button if one had
 * previously been set, otherwise returns null.
 *
 * @return the default button or null
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @see #setDefaultButton
 */
public Button getDefaultButton () {
	checkWidget ();
	return defaultButton;
}

//@@TODO(dmik)
///**
// * Returns the receiver's image if it had previously been 
// * set using <code>setImage()</code>. The image is typically
// * displayed by the window manager when the instance is
// * marked as iconified, and may also be displayed somewhere
// * in the trim when the instance is in normal or maximized
// * states.
// * <p>
// * Note: This method will return null if called before
// * <code>setImage()</code> is called. It does not provide
// * access to a window manager provided, "default" image
// * even if one exists.
// * </p>
// * 
// * @return the image
// *
// * @exception SWTException <ul>
// *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
// *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
// * </ul>
// */
//public Image getImage () {
//	checkWidget ();
//	return image;
//}

/**
 * Returns <code>true</code> if the receiver is currently
 * maximized, and false otherwise. 
 * <p>
 *
 * @return the maximized state
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @see #setMaximized
 */
public boolean getMaximized () {
	checkWidget ();
    return (OS.WinQueryWindowULong (frameHandle, OS.QWL_STYLE) & OS.WS_MAXIMIZED) != 0;
}

//@@TODO(dmik)    
///**
// * Returns the receiver's menu bar if one had previously
// * been set, otherwise returns null.
// *
// * @return the menu bar or null
// *
// * @exception SWTException <ul>
// *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
// *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
// * </ul>
// */
//public Menu getMenuBar () {
//	checkWidget ();
//	return menuBar;
//}

/**
 * Returns <code>true</code> if the receiver is currently
 * minimized, and false otherwise. 
 * <p>
 *
 * @return the minimized state
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @see #setMinimized
 */
public boolean getMinimized () {
	checkWidget ();
    return (OS.WinQueryWindowULong (frameHandle, OS.QWL_STYLE) & OS.WS_MINIMIZED) != 0;
}

String getNameText () {
	return getText ();
}

int getParentHeight() {
    return parent.getHeight();
}

/**
 * Returns the receiver's text, which is the string that the
 * window manager will typically display as the receiver's
 * <em>title</em>. If the text has not previously been set, 
 * returns an empty string.
 *
 * @return the text
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public String getText () {
    checkWidget ();
    int length = OS.WinQueryWindowTextLength (frameHandle);
    if (length == 0) return "";
    /* Use the character encoding for the default locale */
    PSZ buffer = new PSZ (length);
    OS.WinQueryWindowText (frameHandle, length+1, buffer);
    return buffer.toString ();
}

boolean isTabGroup () {
	return true;
}

boolean isTabItem () {
	return false;
}

Decorations menuShell () {
	return this;
}

//@@TODO(dmik)
//boolean moveMenu (int hMenuSrc, int hMenuDest) {
//	boolean success = true;
//	TCHAR lpNewItem = new TCHAR (0, "", true);
//	int index = 0, cch = 128;
//	int byteCount = cch * TCHAR.sizeof;
//	int hHeap = OS.GetProcessHeap ();
//	int pszText = OS.HeapAlloc (hHeap, OS.HEAP_ZERO_MEMORY, byteCount);
//	MENUITEMINFO lpmii = new MENUITEMINFO ();
//	lpmii.cbSize = MENUITEMINFO.sizeof;
//	lpmii.fMask = OS.MIIM_STATE | OS.MIIM_ID | OS.MIIM_TYPE | OS.MIIM_DATA | OS.MIIM_SUBMENU;
//	lpmii.dwTypeData = pszText;
//	lpmii.cch = cch;
//	while (OS.GetMenuItemInfo (hMenuSrc, 0, true, lpmii)) {
//		int uFlags = OS.MF_BYPOSITION | OS.MF_ENABLED;
//		int uIDNewItem = lpmii.wID;
//		if ((lpmii.fType & OS.MFT_SEPARATOR) != 0) {
//			uFlags |= OS.MFT_SEPARATOR;
//		} else if (lpmii.hSubMenu != 0) {
//			uFlags |= OS.MF_POPUP;
//			uIDNewItem = lpmii.hSubMenu;
//		}
//		success = OS.InsertMenu (hMenuDest, index, uFlags, uIDNewItem, lpNewItem);
//		if (!success) break;
//		/* Set application data and text info */
//		if ((lpmii.fType & OS.MFT_SEPARATOR) != 0) {
//			lpmii.fMask = OS.MIIM_DATA;
//			success = OS.SetMenuItemInfo (hMenuDest, index, true, lpmii);
//			if (!success) break;
//		} else {
//			lpmii.fMask = OS.MIIM_DATA | OS.MIIM_TYPE;
//			success = OS.SetMenuItemInfo (hMenuDest, index, true, lpmii);
//			if (!success) break;
//			if ((lpmii.fState & (OS.MFS_DISABLED | OS.MFS_GRAYED)) != 0) {
//				OS.EnableMenuItem (hMenuDest, index, OS.MF_BYPOSITION | OS.MF_GRAYED);
//			}
//			if ((lpmii.fState & OS.MFS_CHECKED) != 0) {
//				OS.CheckMenuItem (hMenuDest, index, OS.MF_BYPOSITION | OS.MF_CHECKED);
//			}
//		}
//		OS.RemoveMenu (hMenuSrc, 0, OS.MF_BYPOSITION);
//		index++;
//		lpmii.fMask = OS.MIIM_STATE | OS.MIIM_ID | OS.MIIM_TYPE | OS.MIIM_DATA | OS.MIIM_SUBMENU;
//		/*
//		* Bug in WinCE.  Calling GetItemInfo on an item of type MFT_SEPARATOR with the mask MIIM_TYPE
//		* modifies the value of the field dwTypeData.  The workaround is to reset the field.
//		*/
//		lpmii.dwTypeData = pszText;
//		lpmii.cch = cch;
//	}
//	if (pszText != 0) OS.HeapFree (hHeap, 0, pszText);
//	return success;
//}

void redrawTrimmings () {
    if (!OS.WinIsWindowVisible (frameHandle)) return;
    RECTL rcl = new RECTL ();
    OS.WinQueryWindowRect (frameHandle, rcl);
    int width = rcl.xRight;
    int height = rcl.yTop;
    SWP swp = new SWP ();
    OS.WinQueryWindowPos (handle, swp);
    /*rcl.xLeft = 0; rcl.yBottom = 0;*/
    rcl.xRight = swp.x; /*rcl.yTop = height;*/
    OS.WinInvalidateRect (frameHandle, rcl, true);
    /*rcl.xLeft = 0; rcl.yBottom = 0;*/
    rcl.xRight = width; rcl.yTop = swp.y;
    OS.WinInvalidateRect (frameHandle, rcl, true);
    /*rcl.xLeft = 0;*/ rcl.yBottom = swp.y + swp.cy;
    /*rcl.xRight = width;*/ rcl.yTop = height;
    OS.WinInvalidateRect (frameHandle, rcl, true);
    rcl.xLeft = swp.x + swp.cx; rcl.yBottom = 0;
    /*rcl.xRight = width; rcl.yTop = height;*/
    OS.WinInvalidateRect (frameHandle, rcl, true);
}

public void redraw () {
    checkWidget ();
    redrawTrimmings ();
    super.redraw ();
}

public void redraw (int x, int y, int width, int height, boolean all) {
    checkWidget ();
    if (width <= 0 || height <= 0) return;
    redrawTrimmings ();
    super.redraw (x, y, width, height, all);
    
}

void releaseHandle () {
    super.releaseHandle();
    frameHandle = 0;
}

void releaseWidget () {   
	if (menuBar != null) {
		menuBar.releaseWidget ();
		menuBar.releaseHandle ();
	}
	menuBar = null;
	if (menus != null) {
		for (int i=0; i<menus.length; i++) {
			Menu menu = menus [i];
			if (menu != null && !menu.isDisposed ()) {
				menu.dispose ();
			}
		}
	}
	menus = null;

    super.releaseWidget ();
    
//@@TODO (dmik): later    
//	if (hIcon != 0) OS.DestroyIcon (hIcon);
//	hIcon = 0;
//	items = null;
//	image = null;
//	savedFocus = null;
//	defaultButton = saveDefault = null;
//	if (hAccel != 0 && hAccel != -1) OS.DestroyAcceleratorTable (hAccel);
//	hAccel = -1;
//	hwndCB = 0;
}

void remove (Menu menu) {
	if (menus == null) return;
	for (int i=0; i<menus.length; i++) {
		if (menus [i] == menu) {
			menus [i] = null;
			return;
		}
	}
}

void remove (MenuItem item) {
	if (items == null) return;
	items [item.id - ID_START] = null;
	item.id = -1;
}

boolean restoreFocus () {
	if (savedFocus != null && savedFocus.isDisposed ()) savedFocus = null;
	if (savedFocus != null && savedFocus.forceFocus ()) return true;
	if (defaultButton != null && !defaultButton.isDisposed ()) {
		if (defaultButton.setFocus ()) return true;
	}
	return false;
}

void saveFocus () {
	Control control = getDisplay ().getFocusControl ();
	if (control != null) setSavedFocus (control);
}

void setBounds (int x, int y, int width, int height, int flags) {
    //@@TODO(dmik): should we take the maximized state into account?    
    if ((OS.WinQueryWindowULong (frameHandle, OS.QWL_STYLE) & OS.WS_MINIMIZED) != 0) {
        SWP swp = new SWP();
        int parentHeight = getBounds (swp);
        if ((flags & OS.SWP_MOVE) != 0) {
            swp.x = x;
            swp.y = y; 
        }
        if ((flags & OS.SWP_SIZE) != 0) {
            swp.cx = width;
            swp.cy = height;
            flags |= OS.SWP_MOVE;
            OS.WinSetWindowUShort (frameHandle, OS.QWS_CXRESTORE, (short)swp.cx);
            OS.WinSetWindowUShort (frameHandle, OS.QWS_CYRESTORE, (short)swp.cy);
        }
        if ((flags & OS.SWP_MOVE) != 0 ) {
            swp.y = parentHeight - (swp.y + swp.cy);
            OS.WinSetWindowUShort (frameHandle, OS.QWS_XRESTORE, (short)swp.x);
            OS.WinSetWindowUShort (frameHandle, OS.QWS_YRESTORE, (short)swp.y);
        }
        return;
    } else {
        SWP swp = new SWP();
        int parentHeight = getBounds (swp);
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
        OS.WinSetWindowPos (frameHandle, 0, swp.x, swp.y, swp.cx, swp.cy, flags);        
    }
}

/**
 * If the argument is not null, sets the receiver's default
 * button to the argument, and if the argument is null, sets
 * the receiver's default button to the first button which
 * was set as the receiver's default button (called the 
 * <em>saved default button</em>). If no default button had
 * previously been set, or the saved default button was
 * disposed, the receiver's default button will be set to
 * null. 
 *
 * @param the new default button
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_INVALID_ARGUMENT - if the button has been disposed</li> 
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void setDefaultButton (Button button) {
	checkWidget ();
	setDefaultButton (button, true);
}

void setDefaultButton (Button button, boolean save) {
	if (button == null) {
		if (defaultButton == saveDefault) return;
	} else {
		if (button.isDisposed()) error(SWT.ERROR_INVALID_ARGUMENT);
		if ((button.style & SWT.PUSH) == 0) return;
		if (button == defaultButton) return;
	}
	if (defaultButton != null) {
		if (!defaultButton.isDisposed ()) defaultButton.setDefault (false);
	}
	if ((defaultButton = button) == null) defaultButton = saveDefault;
	if (defaultButton != null) {
		if (!defaultButton.isDisposed ()) defaultButton.setDefault (true);
	}
	if (save || saveDefault == null) saveDefault = defaultButton;
	if (saveDefault != null && saveDefault.isDisposed ()) saveDefault = null;
}

void setDefaultFont () {
}

//@@TODO (dmik): guess remove (note: force focus forwards message to client)
//public boolean setFocus () {
//	checkWidget ();
//	if (this instanceof Shell) return super.setFocus ();
//	/*
//	* Bug in Windows.  Setting the focus to a child of the
//	* receiver interferes with moving and resizing of the
//	* parent shell.  The fix (for now) is to always set the
//	* focus to the shell.
//	*/
//	int hwndFocus = OS.SetFocus (getShell ().handle);
//	return hwndFocus == OS.GetFocus ();
//}

//@@TODO(dmik)
///**
// * Sets the receiver's image to the argument, which may
// * be null. The image is typically displayed by the window
// * manager when the instance is marked as iconified, and
// * may also be displayed somewhere in the trim when the
// * instance is in normal or maximized states.
// * 
// * @param image the new image (or null)
// *
// * @exception IllegalArgumentException <ul>
// *    <li>ERROR_INVALID_ARGUMENT - if the image has been disposed</li> 
// * </ul>
// * @exception SWTException <ul>
// *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
// *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
// * </ul>
// */
//public void setImage (Image image) {
//	checkWidget ();
//	if (image != null && image.isDisposed ()) error (SWT.ERROR_INVALID_ARGUMENT);
//	/*
//	* Feature in WinCE.  WM_SETICON and WM_GETICON set the icon
//	* for the window class, not the window instance.  This means
//	* that it is possible to set an icon into a window and then
//	* later free the icon, thus freeing the icon for every window.
//	* The fix is to avoid the API.
//	* 
//	* On WinCE PPC, icons in windows are not displayed anyways.
//	*/
//	if (OS.IsWinCE) {
//		this.image = image;
//		return;
//	}
//	int hImage = 0;
//	if (image != null) {
//		if (hIcon != 0) OS.DestroyIcon (hIcon);
//		hIcon = 0;
//		switch (image.type) {
//			case SWT.BITMAP:
//				/* Copy the bitmap in case it's a DIB */
//				int hBitmap = image.handle;
//				BITMAP bm = new BITMAP ();
//				OS.GetObject (hBitmap, BITMAP.sizeof, bm);
//				byte [] lpvBits = new byte [(bm.bmWidth + 15) / 16 * 2 * bm.bmHeight];
//				int hMask = OS.CreateBitmap (bm.bmWidth, bm.bmHeight, 1, 1, lpvBits);
//				int hDC = OS.GetDC (handle);
//				int hdcMem = OS.CreateCompatibleDC (hDC);
//				int hColor = OS.CreateCompatibleBitmap (hDC, bm.bmWidth, bm.bmHeight);
//				OS.SelectObject (hdcMem, hColor);
//				int hdcBmp = OS.CreateCompatibleDC (hDC);
//				OS.SelectObject (hdcBmp, hBitmap);
//				OS.BitBlt (hdcMem, 0, 0, bm.bmWidth, bm.bmHeight, hdcBmp, 0, 0, OS.SRCCOPY);
//				ICONINFO info = new ICONINFO ();
//				info.fIcon = true;
//				info.hbmMask = hMask;
//				info.hbmColor = hColor;
//				hImage = hIcon = OS.CreateIconIndirect (info);
//				OS.DeleteObject (hMask);
//				OS.DeleteObject(hColor);
//				OS.DeleteDC (hdcBmp);
//				OS.DeleteDC (hdcMem);
//				OS.ReleaseDC (handle, hDC);
//				break;
//			case SWT.ICON:
//				hImage = image.handle;
//				break;
//			default:
//				return;
//		}
//	}
//	this.image = image;
//	OS.SendMessage (handle, OS.WM_SETICON, OS.ICON_BIG, hImage);
//	
//	/*
//	* Bug in Windows.  When WM_SETICON is used to remove an
//	* icon from the window trimmings for a window with the
//	* extended style bits WS_EX_DLGMODALFRAME, the window
//	* trimmings do not redraw to hide the previous icon.
//	* The fix is to force a redraw.
//	*/
//	if (!OS.IsWinCE) {
//		if (hIcon == 0 && (style & SWT.BORDER) != 0) {
//			int flags = OS.RDW_FRAME | OS.RDW_INVALIDATE;
//			OS.RedrawWindow (handle, null, 0, flags);
//		}
//	}
//}

/**
 * Sets the maximized state of the receiver.
 * If the argument is <code>true</code> causes the receiver
 * to switch to the maximized state, and if the argument is
 * <code>false</code> and the receiver was previously maximized,
 * causes the receiver to switch back to either the minimized
 * or normal states.
 * <p>
 * Note: The result of intermixing calls to<code>setMaximized(true)</code>
 * and <code>setMinimized(true)</code> will vary by platform. Typically,
 * the behavior will match the platform user's expectations, but not
 * always. This should be avoided if possible.
 * </p>
 *
 * @param the new maximized state
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @see #setMinimized
 */
public void setMaximized (boolean maximized) {
	checkWidget ();
    if (maximized ==
        ((OS.WinQueryWindowULong (frameHandle, OS.QWL_STYLE) & OS.WS_MAXIMIZED) != 0))
        return;
	int flags = maximized ? OS.SWP_MAXIMIZE | OS.SWP_ACTIVATE : OS.SWP_RESTORE;
    OS.WinSetWindowPos (frameHandle, 0, 0, 0, 0, 0, flags);
}

//@@TODO (dmik): later
///**
// * Sets the receiver's menu bar to the argument, which
// * may be null.
// *
// * @param menu the new menu bar
// *
// * @exception IllegalArgumentException <ul>
// *    <li>ERROR_INVALID_ARGUMENT - if the menu has been disposed</li> 
// *    <li>ERROR_INVALID_PARENT - if the menu is not in the same widget tree</li>
// * </ul>
// * @exception SWTException <ul>
// *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
// *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
// * </ul>
// */
//public void setMenuBar (Menu menu) {
//	checkWidget ();
//	if (menuBar == menu) return;
//	if (menu != null) {
//		if (menu.isDisposed()) error(SWT.ERROR_INVALID_ARGUMENT);
//		if ((menu.style & SWT.BAR) == 0) error (SWT.ERROR_MENU_NOT_BAR);
//		if (menu.parent != this) error (SWT.ERROR_INVALID_PARENT);
//	}	
//	if (OS.IsPPC) {
//		/*
//		* Note in WinCE PPC.  MenuBar is a separate popup window. If
//		* the Shell is full screen, resize its window to leave
//		* space for the MenuBar.
//		*/
//		boolean resize = (getMaximized() && menuBar != menu);
//		if (menuBar != null) {
//			OS.CommandBar_Destroy (hwndCB);
//			hwndCB = 0;
//			hwndTB = 0;
//		}
//		menuBar = menu;
//		if (menuBar != null) {		
//			SHMENUBARINFO mbi = new SHMENUBARINFO ();
//			mbi.cbSize = mbi.sizeof;
//			mbi.hwndParent = handle;
//			mbi.dwFlags = 0;
//			mbi.nToolBarId = 100; /* as defined in .rc file */
//			mbi.hInstRes = OS.GetLibraryHandle ();
//			boolean success = OS.SHCreateMenuBar (mbi);
//			hwndCB = mbi.hwndMB;
//
//			/* get toolbar */
//			if (success && hwndCB != 0) hwndTB = OS.GetWindow (hwndCB, OS.GW_CHILD);
//						
//			if (hwndTB == 0) {
//				/* we can't use the menubar */
//				if (hwndCB != 0) OS.CommandBar_Destroy (hwndCB);
//				return;
//			}
//			/* remove the menu item coming from the resource file */
//			OS.SendMessage (hwndTB, OS.TB_DELETEBUTTON, 0, 0);
//			
//			/* populate tool bar mapping menu items to tool items */
//			if (menuBar.getItemCount () > 0) {
//				MenuItem[] items = menuBar.getItems ();
//				TBBUTTON lpButton = new TBBUTTON ();
//				TBBUTTONINFO info = new TBBUTTONINFO ();
//				info.cbSize = TBBUTTONINFO.sizeof;
//				info.dwMask = OS.TBIF_TEXT;
//				
//				int cch = 128;
//				int hHeap = OS.GetProcessHeap ();
//				int byteCount = cch * TCHAR.sizeof;
//				int pszText = OS.HeapAlloc (hHeap, OS.HEAP_ZERO_MEMORY, byteCount);
//				MENUITEMINFO mii = new MENUITEMINFO ();
//				mii.cbSize = MENUITEMINFO.sizeof;
//				mii.fMask = OS.MIIM_STATE | OS.MIIM_TYPE;
//				mii.dwTypeData = pszText;
//
//				for (int i = 0; i < items.length; i++) {
//					MenuItem item = items[i];
//					/* insert item */
//					lpButton.idCommand = item.id;
//					lpButton.fsStyle = (byte) (OS.TBSTYLE_DROPDOWN | OS.TBSTYLE_AUTOSIZE | 0x80);
//					lpButton.fsState = (byte) OS.TBSTATE_ENABLED;
//					lpButton.iBitmap = OS.I_IMAGENONE;
//					if ((item.style & SWT.SEPARATOR) != 0) {
//						lpButton.fsStyle = (byte) OS.BTNS_SEP;
//					}
//					OS.SendMessage (hwndTB, OS.TB_INSERTBUTTON, i, lpButton);
//
//					if ((item.style & SWT.SEPARATOR) == 0) {
//						/* set text and state info */
//						Menu parent = item.parent;
//						int id = item.id;
//						int hMenu = parent.handle;
//												
//						mii.cch = cch;
//						success = OS.GetMenuItemInfo (hMenu, i, true, mii);
//						if (!success) error (SWT.ERROR_INVALID_ARGUMENT);
//						boolean enabled = (mii.fState & (OS.MFS_DISABLED | OS.MFS_GRAYED)) == 0;
//						if (!enabled) {
//							info.dwMask |= OS.TBIF_STATE;
//							info.fsStyle = 0;
//						}						
//						info.pszText = pszText;
//						OS.SendMessage (hwndTB, OS.TB_SETBUTTONINFO, item.id, info);
//
//						/* set menu */
//						Menu menu2 = item.menu;
//						if (menu2 != null) {
//							OS.SendMessage (hwndCB, OS.SHCMBM_SETSUBMENU, item.id, menu2.handle);
//						}
//					}
//				}
//				if (pszText != 0) OS.HeapFree (hHeap, 0, pszText);
//			}
//		}
//		if (resize) setMaximized (true);
//	} else if (OS.IsHPC) {
//		boolean resize = menuBar != menu;
//		if (menuBar != null) {
//			/*
//			* Because CommandBar_Destroy destroys the menu bar, it
//			* is necessary to move the current items into a new menu
//			* before it is called.
//			*/
//			int hMenu = OS.CreateMenu ();
//			if (!moveMenu (menuBar.handle, hMenu)) {
//				error (SWT.ERROR_CANNOT_SET_MENU);
//			}
//			menuBar.handle = hMenu;
//			if (hwndCB != 0) OS.CommandBar_Destroy (hwndCB);
//			hwndCB = 0;
//		}
//		menuBar = menu;
//		if (menuBar != null) {		
//			hwndCB = OS.CommandBar_Create (OS.GetModuleHandle (null), handle, 1);
//			OS.CommandBar_InsertMenubarEx (hwndCB, 0, menuBar.handle, 0);
//			/*
//			* The command bar hosts the 'close' button when the window does not
//			* have a caption.
//			*/
//			if ((style & SWT.CLOSE) != 0 && (style & SWT.TITLE) == 0) {
//				OS.CommandBar_AddAdornments(hwndCB, 0, 0);
//			}
//		}
//		if (resize) {
//			sendEvent (SWT.Resize);
//			layout (false);
//		}
//	} else {
//		menuBar = menu;
//		int hMenu = 0;
//		if (menuBar != null) hMenu = menuBar.handle;
//		OS.SetMenu (handle, hMenu);
//	}
//	destroyAcceleratorTable ();
//}

/**
 * Sets the minimized stated of the receiver.
 * If the argument is <code>true</code> causes the receiver
 * to switch to the minimized state, and if the argument is
 * <code>false</code> and the receiver was previously minimized,
 * causes the receiver to switch back to either the maximized
 * or normal states.
 * <p>
 * Note: The result of intermixing calls to<code>setMaximized(true)</code>
 * and <code>setMinimized(true)</code> will vary by platform. Typically,
 * the behavior will match the platform user's expectations, but not
 * always. This should be avoided if possible.
 * </p>
 *
 * @param the new maximized state
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 *
 * @see #setMaximized
 */
public void setMinimized (boolean minimized) {
	checkWidget ();
    if (minimized == 
        ((OS.WinQueryWindowULong (frameHandle, OS.QWL_STYLE) & OS.WS_MINIMIZED) != 0))
        return;
	int flags = minimized ? OS.SWP_MINIMIZE : OS.SWP_RESTORE;
    OS.WinSetWindowPos (frameHandle, 0, 0, 0, 0, 0, flags);
}

//@@TODO(dmik)
//void setParent () {
//	/*
//	* In order for an MDI child window to support
//	* a menu bar, setParent () is needed to reset
//	* the parent.  Otherwise, the MDI child window
//	* will appear as a separate shell.  This is an
//	* undocumented and possibly dangerous Windows
//	* feature.
//	*/
//	Display display = getDisplay ();
//	int hwndParent = parent.handle;
//	display.lockActiveWindow = true;
//	OS.SetParent (handle, hwndParent);
//	if (!OS.IsWindowVisible (hwndParent)) {
//		OS.ShowWindow (handle, OS.SW_SHOWNA);
//	}
//	display.lockActiveWindow = false;
//}

public void setRedraw (boolean redraw) {
    checkWidget ();
    if (redraw) {
        if (--drawCount == 0) {
            OS.WinEnableWindowUpdate (frameHandle, true);
            OS.WinInvalidateRect (frameHandle, null, true);
        }
    } else {
        if (drawCount++ == 0) {
            OS.WinEnableWindowUpdate (frameHandle, false);
        }
    }
}

void setSavedFocus (Control control) {
	if (this == control) {
		savedFocus = null;
		return;
	}
	if (this != control.menuShell ()) return;
	savedFocus = control;
}

//@@TODO (dmik): later
//void setSystemMenu () {
//	if (OS.IsWinCE) return;
//	int hMenu = OS.GetSystemMenu (handle, false);
//	if (hMenu == 0) return;
//	int oldCount = OS.GetMenuItemCount (hMenu);
//	if ((style & SWT.RESIZE) == 0) {
//		OS.DeleteMenu (hMenu, OS.SC_SIZE, OS.MF_BYCOMMAND);
//	}
//	if ((style & SWT.MIN) == 0) {
//		OS.DeleteMenu (hMenu, OS.SC_MINIMIZE, OS.MF_BYCOMMAND);
//	}
//	if ((style & SWT.MAX) == 0) {
//		OS.DeleteMenu (hMenu, OS.SC_MAXIMIZE, OS.MF_BYCOMMAND);
//	}
//	if ((style & (SWT.MIN | SWT.MAX)) == 0) {
//		OS.DeleteMenu (hMenu, OS.SC_RESTORE, OS.MF_BYCOMMAND);
//	}
//	int newCount = OS.GetMenuItemCount (hMenu);
//	if ((style & SWT.CLOSE) == 0 || newCount != oldCount) {	
//		OS.DeleteMenu (hMenu, OS.SC_TASKLIST, OS.MF_BYCOMMAND);
//		MENUITEMINFO info = new MENUITEMINFO ();
//		info.cbSize = MENUITEMINFO.sizeof;
//		info.fMask = OS.MIIM_ID;
//		int index = 0;
//		while (index < newCount) {
//			if (OS.GetMenuItemInfo (hMenu, index, true, info)) {
//				if (info.wID == OS.SC_CLOSE) break;
//			}
//			index++;
//		}
//		if (index != newCount) {
//			OS.DeleteMenu (hMenu, index - 1, OS.MF_BYPOSITION);
//			if ((style & SWT.CLOSE) == 0) {
//				OS.DeleteMenu (hMenu, OS.SC_CLOSE, OS.MF_BYCOMMAND);
//			}
//		}
//	}
//}

/**
 * Sets the receiver's text, which is the string that the
 * window manager will typically display as the receiver's
 * <em>title</em>, to the argument, which may not be null. 
 *
 * @param text the new text
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void setText (String string) {
    checkWidget ();
    if (string == null) error (SWT.ERROR_NULL_ARGUMENT);
    /* Use the character encoding for the default locale */
    PSZ title = string.length() > 0 ? new PSZ (string) : EmptyTitle;
    OS.WinSetWindowText (frameHandle, title);
}

public void setVisible (boolean visible) {
    checkWidget ();
    if (OS.WinIsWindowVisible (frameHandle) == visible) return;
    if (visible) {
        /*
        * It is possible (but unlikely), that application
        * code could have disposed the widget in the show
        * event.  If this happens, just return.
        */
        sendEvent (SWT.Show);
        if (isDisposed ()) return;
    }

    OS.WinShowWindow (frameHandle, visible);

    if (!visible) {
        /*
        * It is possible (but unlikely), that application
        * code could have disposed the widget in the show
        * event.  If this happens, just return.
        */
        sendEvent (SWT.Hide);
        if (isDisposed ()) return;
    }

//@@TODO (dmik): think do we need special handlng like below? 
//	checkWidget ();
//	if (visible == OS.IsWindowVisible (handle)) return;
//	if (visible) {
//		/*
//		* It is possible (but unlikely), that application
//		* code could have disposed the widget in the show
//		* event.  If this happens, just return.
//		*/
//		sendEvent (SWT.Show);
//		if (isDisposed ()) return;
//		if (OS.IsHPC) OS.CommandBar_DrawMenuBar (hwndCB, 0);
//		if (OS.IsWinCE) {
//			OS.ShowWindow (handle, OS.SW_SHOW);
//		} else {
//			OS.DrawMenuBar (handle);
//			OS.ShowWindow (handle, swFlags);
//		}
//		OS.UpdateWindow (handle);
//	} else {
//		if (!OS.IsWinCE) {
//			if (OS.IsIconic (handle)) {
//				swFlags = OS.SW_SHOWMINNOACTIVE;
//			} else {
//				if (OS.IsZoomed (handle)) {
//					swFlags = OS.SW_SHOWMAXIMIZED;
//				} else {
//					if (handle == OS.GetActiveWindow ()) {
//						swFlags = OS.SW_RESTORE;
//					} else {
//						swFlags = OS.SW_SHOWNOACTIVATE;
//					}
//				}
//			}
//		}
//		OS.ShowWindow (handle, OS.SW_HIDE);
//		sendEvent (SWT.Hide);
//	}
}

boolean translateAccelerator (QMSG qmsg) {
    return false;
//@@TODO (dmik): later    
//	if (!isEnabled () || !isActive ()) return false;
//	if (menuBar != null && !menuBar.isEnabled ()) return false;
//	if (hAccel == -1) createAcceleratorTable ();
//	if (hAccel == 0) return false;
//	return OS.TranslateAccelerator (handle, hAccel, msg) != 0;
}

boolean traverseItem (boolean next) {
	return false;
}

boolean traverseReturn () {
	if (defaultButton == null || defaultButton.isDisposed ()) return false;
	if (!defaultButton.isVisible () || !defaultButton.isEnabled ()) return false;
	defaultButton.click ();
	return true;
}

int windowProc (int msg, int mp1, int mp2) {
//@@TODO (dmik): remove
//    switch (msg) {
//@@TODO(dmik)
//        case OS.WM_APP:
//        case OS.WM_APP+1:
//            if (hAccel == -1) createAcceleratorTable ();
//            return msg == OS.WM_APP ? nAccel : hAccel;
//    }
    return super.windowProc (msg, mp1, mp2);
}

//@@TODO (dmik): later
//int widgetExtStyle () {
//	int bits = 0;
//	if ((style & SWT.NO_TRIM) != 0) return bits;
//	if (OS.IsPPC) {
//		if ((style & SWT.CLOSE) != 0) bits |= OS.WS_EX_CAPTIONOKBTN;
//	}
//	if ((style & SWT.TOOL) != 0) bits |= OS.WS_EX_TOOLWINDOW;
//	if ((style & SWT.RESIZE) != 0) return bits;
//	if ((style & SWT.BORDER) != 0) bits |= OS.WS_EX_DLGMODALFRAME;
//	return bits;
//}

int widgetFrameFlags () {
    /*
     *  frame styles for OS/2:
     *  FCF_TITLEBAR - can be alone
     *  FCF_SYSMENU - can be alone
     *  FCF_SYSMENU | FCF_MINBUTTON - assumes FCF_CLOSEBUTTON
     *  FCF_SYSMENU | FCF_MAXBUTTON - assumes FCF_CLOSEBUTTON
     *  FCF_CLOSEBUTTON -- cannot be used alone, only with FCF_SYSMENU
     */
    int bits = 0;
    /* Set the title bits */
    if ((style & SWT.NO_TRIM) != 0) return bits;
    if ((style & SWT.TITLE) != 0) bits |= OS.FCF_TITLEBAR;
    
    /* Set the min and max button bits */
    if ((style & SWT.MIN) != 0) bits |= OS.FCF_MINBUTTON;
    if ((style & SWT.MAX) != 0) bits |= OS.FCF_MAXBUTTON;

    /* Set the system menu and close box bits */
    if ((style & SWT.CLOSE) != 0) bits |= OS.FCF_SYSMENU | OS.FCF_CLOSEBUTTON;

    return bits;
}

int widgetFrameStyle () {
    int bits = super.widgetStyle ();
    bits &= ~(OS.WS_VISIBLE | OS.WS_TABSTOP);

    /* Set the no-trim bits */
    bits &= ~OS.FS_BORDER;
    if ((style & SWT.NO_TRIM) != 0) return bits;

    /* Set the resize, dialog border or border bits */
    if ((style & SWT.RESIZE) != 0) {
        bits |= OS.FS_SIZEBORDER;	
    } else {
        if ((style & SWT.BORDER) == 0) bits |= OS.FS_BORDER;
        else bits |= OS.FS_DLGBORDER;
    }

    /*
     * Disable the default annoying secondary window behavior and set the
     * clipping of children to avoid the titlebar flicker when the frame
     * is resized. Also disable the byte alignment when moving.
     */
    return bits | OS.FS_NOMOVEWITHOWNER | /*OS.WS_CLIPCHILDREN |*/ OS.FS_NOBYTEALIGN;
    /*
     *  OS_WS_CLIPCHILDREN above is intentionally commented since it is now
     *  always set to 1 in Composite.widgetStyle().
     */
}

PSZ windowFrameClass () {
    return FrameClass;
}

int windowFrameProc () {
    return FrameProc;
}

int windowFrameProc (int msg, int mp1, int mp2) {
    MRESULT result = null;
    switch (msg) {
        case OS.WM_ACTIVATE: result = FRAME_WM_ACTIVATE (mp1, mp2); break;
        case OS.WM_ADJUSTWINDOWPOS: result = FRAME_WM_ADJUSTWINDOWPOS (mp1, mp2); break;
        case OS.WM_REALIZEPALETTE: result = FRAME_WM_REALIZEPALETTE (mp1, mp2); break;
        case OS.WM_WINDOWPOSCHANGED: result = FRAME_WM_WINDOWPOSCHANGED (mp1, mp2); break;
        case OS.WM_CHAR: result = FRAME_WM_CHAR (mp1, mp2); break;
    }
    
    if (result != null) return result.value;
    return callWindowFrameProc (msg, mp1, mp2);
}

int callWindowFrameProc (int msg, int mp1, int mp2) {
    if (frameHandle == 0) return 0;
    return OS.WinCallWindowProc (FrameProc, frameHandle, msg, mp1, mp2);
}

void subclass () {
    super.subclass ();
    int oldProc = windowFrameProc ();
    int newProc = getDisplay ().windowFrameProc;
    if (oldProc == newProc) return;
    OS.WinSetWindowPtr (frameHandle, OS.QWP_PFNWP, newProc);
}

void unsubclass () {
    int newProc = windowFrameProc ();
    int oldProc = getDisplay ().windowFrameProc;
    if (oldProc == newProc) return;
    OS.WinSetWindowPtr (frameHandle, OS.QWP_PFNWP, newProc);
    super.unsubclass();
}

void register () {
    super.register ();
    WidgetTable.put (frameHandle, this);
}

void deregister () {
    WidgetTable.remove (frameHandle);
    super.deregister ();
}

MRESULT FRAME_WM_ACTIVATE (int mp1, int mp2) {
	if ((OS.SHORT1FROMMP (mp1)) == 0) {
		/*
		* It is possible (but unlikely), that application
		* code could have disposed the widget in the deactivate
		* event.  If this happens, end the processing of the
		* Windows message by returning zero as the result of
		* the window proc.
		*/
		Shell shell = getShell ();
		shell.setActiveControl (null);
		if (isDisposed ()) return MRESULT.ZERO;
		sendEvent (SWT.Deactivate);
		if (isDisposed ()) return MRESULT.ZERO;
		saveFocus ();
	} else {
		/*
		* It is possible (but unlikely), that application
		* code could have disposed the widget in the activate
		* event.  If this happens, end the processing of the
		* Windows message by returning zero as the result of
		* the window proc.
		*/
		sendEvent (SWT.Activate);
		if (isDisposed ()) return MRESULT.ZERO;
//@@TODO (dmik): remove        
//		if (restoreFocus ()) return MRESULT.ZERO;
		restoreFocus ();
	}
	return null;
}

MRESULT FRAME_WM_ADJUSTWINDOWPOS (int mp1, int mp2) {
    SWP swp = new SWP ();
    OS.objcpy (swp, mp1);
    if ((swp.fl & OS.SWP_MINIMIZE) != 0) {
		sendEvent (SWT.Iconify);
		// widget could be disposed at this point
	} else if ((swp.fl & OS.SWP_RESTORE) != 0 && getMinimized ()) {
        sendEvent (SWT.Deiconify);
        // widget could be disposed at this point
    }
	return null;
}

MRESULT FRAME_WM_CHAR (int mp1, int mp2) {
//@@TODO (dmik): debug, remove    
//boolean keyUp = (OS.SHORT1FROMMP (mp1) & OS.KC_KEYUP) != 0;
//System.out.println("FRAME_WM_CHAR ("+this+"): keyup="+keyUp);
    /*
     *  Wedon't pass WM_CHAR to the WC_FRAME window procedure to disable
     *  the handling of TAB, arrows etc. by the system, otherwise it leads to
     *  loosing the focus because our frame window doesn't act like a standard
     *  OS/2 dialog window.
     */
    return MRESULT.ZERO;
}

MRESULT FRAME_WM_REALIZEPALETTE (int mp1, int mp2) {
    return null;
}

MRESULT FRAME_WM_WINDOWPOSCHANGED (int mp1, int mp2) {
    /*
     *  Feature in OS/2. WC_FRAME always receives this message with the
     *  SWP_NOADJUST flag is set, so send it SWT.Move event anyway (as opposed to
     *  Control.WM_WINDOWPOSCHANGED() logic). We cannot do this in responce to
     *  WM_MOVE because it is sent by OS/2 in the context of the original
     *  (bottom-based) coordinate space where, for example, resizing the bottom
     *  edge of the window also means it is being moved, but it is not the case
     *  in the context of top-based coordinate space. WM_SIZE is never sent
     *  from here since it is useless (the client window handles WM_SIZE and does
     *  the necessary work such as doing layout).
     */
    int result = callWindowFrameProc (OS.WM_WINDOWPOSCHANGED, mp1, mp2);
    SWP swpNew = new SWP ();
    OS.objcpy (swpNew, mp1);
    SWP swpOld = new SWP ();
    OS.objcpy (swpOld, (mp1 + SWP.sizeof));
    if ((swpNew.x != swpOld.x) || (swpNew.y + swpNew.cy != swpOld.y + swpOld.cy)) {
        sendEvent (SWT.Move);
        // widget could be disposed at this point
    }
    return new MRESULT (result);
}

MRESULT WM_CLOSE (int mp1, int mp2) {
    MRESULT result = super.WM_CLOSE (mp1, mp2);
    if (result != null) return result;
    Event event = new Event ();
    sendEvent (SWT.Close, event);
    // the widget could be disposed at this point
    if (event.doit && !isDisposed ()) dispose ();
    return MRESULT.ZERO;
}

MRESULT WM_SETFOCUS (int mp1, int mp2) {
    MRESULT result  = super.WM_SETFOCUS (mp1, mp2);
    boolean gotFocus = OS.SHORT1FROMMP (mp2) > 0;
    if (gotFocus) {
        restoreFocus();
    } else {
        saveFocus ();
    }
	return result;
}

//@@TODO (dmik): later
//LRESULT WM_NCACTIVATE (int wParam, int lParam) {
//	LRESULT result  = super.WM_NCACTIVATE (wParam, lParam);
//	if (result != null) return result;
//	if (wParam == 0) {
//		Display display = getDisplay ();
//		if (display.lockActiveWindow) return LRESULT.ZERO;
//	}
//	return result;
//}

//@@TODO (dmik): guess remove
//LRESULT WM_WINDOWPOSCHANGING (int wParam, int lParam) {
//	LRESULT result = super.WM_WINDOWPOSCHANGING (wParam,lParam);
//	if (result != null) return result;
//	Display display = getDisplay ();
//	if (display.lockActiveWindow) {
//		WINDOWPOS lpwp = new WINDOWPOS ();
//		OS.MoveMemory (lpwp, lParam, WINDOWPOS.sizeof);
//		lpwp.flags |= OS.SWP_NOZORDER;
//		OS.MoveMemory (lParam, lpwp, WINDOWPOS.sizeof);
//	}
//	return result;
//}

}
