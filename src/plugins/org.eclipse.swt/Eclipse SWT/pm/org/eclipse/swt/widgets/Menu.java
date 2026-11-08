package org.eclipse.swt.widgets;
/*
 * OS/2 version.
 * Copyright (c) 2002, 2008 EclipseOS2 Team.
 */

/*
 * Copyright (c) 2000, 2002 IBM Corp.  All rights reserved.
 * This file is made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 */
 
import org.eclipse.swt.internal.pm.*;
import org.eclipse.swt.*;
import org.eclipse.swt.events.*;

/**
 * Instances of this class are user interface objects that contain
 * menu items.
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>BAR, DROP_DOWN, POP_UP</dd>
 * <dt><b>Events:</b></dt>
 * <dd>Help, Hide, Show </dd>
 * </dl>
 * <p>
 * Note: Only one of BAR, DROP_DOWN and POP_UP may be specified.
 * </p><p>
 * IMPORTANT: This class is <em>not</em> intended to be subclassed.
 * </p>
 */

public class Menu extends Widget {
	public int handle;
	int x, y;
	boolean hasLocation;
	MenuItem cascade;
	Decorations parent;

/**
 * Constructs a new instance of this class given its parent,
 * and sets the style for the instance so that the instance
 * will be a popup menu on the given parent's shell.
 *
 * @param parent a control which will be the parent of the new instance (cannot be null)
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li>
 *    <li>ERROR_INVALID_SUBCLASS - if this class is not an allowed subclass</li>
 * </ul>
 *
 * @see SWT#POP_UP
 * @see Widget#checkSubclass
 * @see Widget#getStyle
 */
public Menu (Control parent) {
	this (checkNull (parent).getShell (), SWT.POP_UP);
}

/**
 * Constructs a new instance of this class given its parent
 * (which must be a <code>Decorations</code>) and a style value
 * describing its behavior and appearance.
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
 * @param parent a decorations control which will be the parent of the new instance (cannot be null)
 * @param style the style of menu to construct
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li>
 *    <li>ERROR_INVALID_SUBCLASS - if this class is not an allowed subclass</li>
 * </ul>
 *
 * @see SWT#BAR
 * @see SWT#DROP_DOWN
 * @see SWT#POP_UP
 * @see Widget#checkSubclass
 * @see Widget#getStyle
 */
public Menu (Decorations parent, int style) {
	super (parent, checkStyle (style));
	this.parent = parent;
	createWidget ();
}

/**
 * Constructs a new instance of this class given its parent
 * (which must be a <code>Menu</code>) and sets the style
 * for the instance so that the instance will be a drop-down
 * menu on the given parent's parent.
 *
 * @param parent a menu which will be the parent of the new instance (cannot be null)
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li>
 *    <li>ERROR_INVALID_SUBCLASS - if this class is not an allowed subclass</li>
 * </ul>
 *
 * @see SWT#DROP_DOWN
 * @see Widget#checkSubclass
 * @see Widget#getStyle
 */
public Menu (Menu parentMenu) {
	this (checkNull (parentMenu).parent, SWT.DROP_DOWN);
}

/**
 * Constructs a new instance of this class given its parent
 * (which must be a <code>MenuItem</code>) and sets the style
 * for the instance so that the instance will be a drop-down
 * menu on the given parent's parent menu.
 *
 * @param parent a menu item which will be the parent of the new instance (cannot be null)
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li>
 *    <li>ERROR_INVALID_SUBCLASS - if this class is not an allowed subclass</li>
 * </ul>
 *
 * @see SWT#DROP_DOWN
 * @see Widget#checkSubclass
 * @see Widget#getStyle
 */
public Menu (MenuItem parentItem) {
	this (checkNull (parentItem).parent);
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
 * be notified when menus are hidden or shown, by sending it
 * one of the messages defined in the <code>MenuListener</code>
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
 * @see MenuListener
 * @see #removeMenuListener
 */
public void addMenuListener (MenuListener listener) {
	checkWidget ();
	if (listener == null) error (SWT.ERROR_NULL_ARGUMENT);
	TypedListener typedListener = new TypedListener (listener);
	addListener (SWT.Hide,typedListener);
	addListener (SWT.Show,typedListener);
}

static Control checkNull (Control control) {
	if (control == null) SWT.error (SWT.ERROR_NULL_ARGUMENT);
	return control;
}

static Menu checkNull (Menu menu) {
	if (menu == null) SWT.error (SWT.ERROR_NULL_ARGUMENT);
	return menu;
}

static MenuItem checkNull (MenuItem item) {
	if (item == null) SWT.error (SWT.ERROR_NULL_ARGUMENT);
	return item;
}

static int checkStyle (int style) {
	return checkBits (style, SWT.POP_UP, SWT.BAR, SWT.DROP_DOWN, 0, 0, 0);
}

void createHandle () {
	if ((style & SWT.BAR) != 0) {
//@@TODO (lpino): debug code, remove when no more necessary
//            System.out.println("Menu:createHandle:  Menu Bar");
            handle = OS.WinCreateWindow (parent.frameHandle,	/* parent window */
                                                        PSZ.getAtom (OS.WC_MENU),	/* class name */
                                                        null,	/* window text */
                                                        OS.MS_ACTIONBAR,	/* window style */
                                                        0, 0, 0, 0, /* size and position */
                                                        parent.frameHandle,	/* owner window */
                                                        OS.HWND_TOP, /* sibling window */
                                                        OS.FID_MENU,	/* ID */
                                                        OS.NULLHANDLE,	/* control data */
                                                        0		/* presentation parameters */
                                                        );
//@@TODO (lpino): debug code, remove when no more necessary
//System.out.println (
//    "Menu.createHandle(): FID_MENU window has been created:\n" +
//    "    hwnd = " + Integer.toHexString (handle) + "\n" +
//    "    hwnd.parent = hwnd.owner = " + Integer.toHexString (handle) + "\n" +
//    "    class = WC_MENU\n" +
//    "    style = MS_ACTIONBAR"
//);
	} else  if ((style & SWT.POP_UP) != 0) {
            handle = OS.WinCreateWindow (OS.HWND_DESKTOP,                     /* parent window */
                                                        PSZ.getAtom (OS.WC_MENU),	/* class name */
                                                        null,                                               /* window text */
                                                        0,                                                   /* window style */
                                                        0, 0, 0, 0,                                       /* size and position */
                                                        OS.HWND_DESKTOP,                              /* owner window */
                                                        OS.HWND_TOP,                            /* sibling window */
                                                        0,                                                  /* ID */
                                                        OS.NULLHANDLE,                          /* control data */
                                                        0                                                   /* presentation parameters */
                                                        );
//@@TODO (lpino): debug code, remove when no more necessary
//System.out.println (
//    "Menu.createHandle(): POPUP menu window has been created:\n" +
//    "    hwnd = " + Integer.toHexString (handle) + "\n" +
//    "    hwnd.parent = hwnd.owner = " + Integer.toHexString (parent.handle) + "\n" +
//    "    class = WC_MENU\n" +
//    "    style = 0"
//);
              
	} else if ((style & SWT.DROP_DOWN) != 0) {
 //@@TODO (lpino): debug code, remove when no more necessary
//           System.out.println("Menu:createHandle:  Menu DropDown");
            handle = OS.WinCreateWindow (OS.HWND_DESKTOP,	/* parent window */
                                                        PSZ.getAtom (OS.WC_MENU),	/* class name */
                                                        null,	/* window text */
                                                        0,	/* window style */
                                                        0, 0, 0, 0, /* size and position */
                                                        OS.HWND_DESKTOP,	/* owner window */
                                                        OS.HWND_TOP, /* sibling window */
                                                        OS.FID_MENU,	/* ID */
                                                        OS.NULLHANDLE,	/* control data */
                                                        0		/* presentation parameters */
                                                        );
//@@TODO (lpino): debug code, remove when no more necessary
//System.out.println (
//    "Menu.createHandle(): A menu has been created:\n" +
//    "    hwnd = " + Integer.toHexString (handle) + "\n" +
//    "    hwnd.parent = hwnd.owner = " + Integer.toHexString (handle) + "\n" +
//    "    class = WC_MENU\n" +
//    "    style = 0"
//);
	}
	if (handle == 0) error (SWT.ERROR_NO_HANDLES);
}

void createItem (MenuItem item, int index) {
	int count = GetMenuItemCount (handle);
	if (!(0 <= index && index <= count)) error (SWT.ERROR_INVALID_RANGE);
	parent.add (item);
	boolean success = false;
        
        MENUITEM info = new MENUITEM();
        info.iPosition = OS.MIT_END;
        info.afStyle = (short)item.widgetStyle();
        info.afAttribute = 0;
        info.id = (short)item.id;
        info.hwndSubMenu = OS.NULLHANDLE;
        info.hItem = 0;
        int rc = OS.WinSendMsg (handle, OS.MM_INSERTITEM,  info, new PSZ(" "));
        if(rc != OS.MIT_MEMERROR && rc != OS.MIT_ERROR)
            success = true;
	if (!success) {
		parent.remove (item);
		error (SWT.ERROR_ITEM_NOT_ADDED);
	}
	redraw ();
}

void createWidget () {
	createHandle ();
	parent.add (this);
}

/*
* Currently not used.
*/
int defaultBackground () {
	return OS.WinQuerySysColor(handle, OS.SYSCLR_MENU, 0);
}

/*
* Currently not used.
*/
int defaultForeground () {
	return OS.WinQuerySysColor(handle, OS.SYSCLR_MENUTEXT, 0);
}

void destroyAcceleratorTable () {
	parent.destroyAcceleratorTable ();
}

void destroyItem (MenuItem item) {
    int count = GetMenuItemCount (handle);
    int rc = OS.WinSendMsg (handle, OS.MM_DELETEITEM,  OS.MPFROM2SHORT ((short)item.id, (short)OS.TRUE), 0);
		if (rc == count) {
			error (SWT.ERROR_ITEM_NOT_REMOVED);
		}
	redraw ();
}

void destroyWidget () {
	int hMenu = handle;
	releaseHandle ();
}

/**
 * Returns the default menu item or null if none has
 * been previously set.
 *
 * @return the default menu item.
 *
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public MenuItem getDefaultItem () {
	checkWidget ();
        int id = OS.WinSendMsg (handle, OS.MM_QUERYDEFAULTITEMID,  0, 0);
	if (id == 0) return null;
	MENUITEM info = new MENUITEM();
        if(OS.WinSendMsg (handle, OS.MM_QUERYITEM,  OS.MPFROM2SHORT ((short)id, (short)OS.TRUE), info))
            return parent.findMenuItem (info.id);
	return null;
}

public Display getDisplay () {
	Decorations parent = this.parent;
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
	return (state & DISABLED) == 0;
}

/**
 * Returns the item at the given, zero-relative index in the
 * receiver. Throws an exception if the index is out of range.
 *
 * @param index the index of the item to return
 * @return the item at the given index
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_INVALID_RANGE - if the index is not between 0 and the number of elements in the list minus 1 (inclusive)</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public MenuItem getItem (int index) {
	checkWidget ();
        MENUITEM info = new MENUITEM();
	int id = OS.WinSendMsg (handle, OS.MM_ITEMIDFROMPOSITION,  (short)index, 0); 
        if(id == OS.MIT_ERROR){
            error (SWT.ERROR_INVALID_RANGE);
	}
        OS.WinSendMsg (handle, OS.MM_QUERYITEM,  OS.MPFROM2SHORT ((short)id, (short)OS.TRUE), info);	
	return parent.findMenuItem (info.id);
}

/**
 * Returns the number of items contained in the receiver.
 *
 * @return the number of items
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public int getItemCount () {
	checkWidget ();
	return OS.WinSendMsg (handle, OS.MM_QUERYITEMCOUNT,  0, 0);
}

/**
 * Returns an array of <code>MenuItem</code>s which are the items
 * in the receiver. 
 * <p>
 * Note: This is not the actual structure used by the receiver
 * to maintain its list of items, so modifying the array will
 * not affect the receiver. 
 * </p>
 *
 * @return the items in the receiver
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public MenuItem [] getItems () {
	checkWidget ();
	int index = 0;
	int length = OS.WinSendMsg (handle, OS.MM_QUERYITEMCOUNT,  0, 0);
	MenuItem [] items = new MenuItem [length];
	MENUITEM info = new MENUITEM();
        int idItem = 0;
//      Revisa si esto es redundante o  no
	while ((idItem=OS.WinSendMsg (handle, OS.MM_ITEMIDFROMPOSITION,  index, 0)) != OS.MIT_ERROR) {
		if (index == items.length) {
			MenuItem [] newItems = new MenuItem [index + 4];
			System.arraycopy (items, 0, newItems, 0, index);
			items = newItems;
		}
                OS.WinSendMsg (handle, OS.MM_QUERYITEM, OS.MPFROM2SHORT ((short)idItem, (short)OS.TRUE), info);
		items [index++] = parent.findMenuItem (info.id);
	}
	if (index == items.length) return items;
	MenuItem [] result = new MenuItem [index];
	System.arraycopy (items, 0, result, 0, index);
	return result;
}

int GetMenuItemCount (int handle) {
	checkWidget ();
	return OS.WinSendMsg(handle, OS.MM_QUERYITEMCOUNT, 0, 0);
}

String getNameText () {
	String result = "";
	MenuItem [] items = getItems ();
	int length = items.length;
	if (length > 0) {
		for (int i=0; i<length-1; i++) {
			result = result + items [i].getNameText() + ", ";
		}
		result = result + items [length-1].getNameText ();
	}
	return result;
}

/**
 * Returns the receiver's parent, which must be a <code>Decorations</code>.
 *
 * @return the receiver's parent
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public Decorations getParent () {
	checkWidget ();
	return parent;
}

/**
 * Returns the receiver's parent item, which must be a
 * <code>MenuItem</code> or null when the receiver is a
 * root.
 *
 * @return the receiver's parent item
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public MenuItem getParentItem () {
	checkWidget ();
	return cascade;
}

/**
 * Returns the receiver's parent item, which must be a
 * <code>Menu</code> or null when the receiver is a
 * root.
 *
 * @return the receiver's parent item
 *
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public Menu getParentMenu () {
	checkWidget ();
	if (cascade != null) return cascade.parent;
	return null;
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
	if ((style & SWT.BAR) != 0) {
		return this == parent.menuShell ().menuBar;
	}
	return this == getShell ().activeMenu;
}

/**
 * Searches the receiver's list starting at the first item
 * (index 0) until an item is found that is equal to the 
 * argument, and returns the index of that item. If no item
 * is found, returns -1.
 *
 * @param item the search item
 * @return the index of the item
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_NULL_ARGUMENT - if the string is null</li>
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public int indexOf (MenuItem item) {
	checkWidget ();
	if (item == null) error (SWT.ERROR_NULL_ARGUMENT);
	int index = 0;
        MENUITEM info = new MENUITEM();
        int idItem = 0;
	while ((idItem=OS.WinSendMsg (handle, OS.MM_ITEMIDFROMPOSITION,  index, 0)) != OS.MIT_ERROR) {
            //@@TODO(lpino) Check if the next call is necesary or we could use the idItem directly
                OS.WinSendMsg (handle, OS.MM_QUERYITEM, OS.MPFROM2SHORT ((short)idItem, (short)OS.TRUE), info);
		if (info.id == item.id) return index;
		index++;
	}
	return -1;
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
	Menu parentMenu = getParentMenu ();
	if (parentMenu == null) return getEnabled ();
	return getEnabled () && parentMenu.isEnabled ();
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
	return getVisible ();
}

void redraw () {
	if ((style & SWT.BAR) != 0) {
            OS.WinSendMsg (parent.frameHandle, OS.WM_UPDATEFRAME, OS.FCF_MENU, 0);
//		OS.DrawMenuBar (parent.handle);
		return;
	}

//        boolean hasCheck = false, hasImage = false;
//	MenuItem [] items = getItems ();
//	for (int i=0; i<items.length; i++) {
//		MenuItem item = items [i];
//		if (item.getImage () != null) {
//			if ((hasImage = true) && hasCheck) break;
//		}
//		if ((item.getStyle () & (SWT.CHECK | SWT.RADIO)) != 0) {
//			if ((hasCheck = true) && hasImage) break;
//		}
//	}
//	MENUINFO lpcmi = new MENUINFO ();
//	lpcmi.cbSize = MENUINFO.sizeof;
//	lpcmi.fMask = OS.MIM_STYLE;
//	OS.GetMenuInfo (handle, lpcmi);
//	if (hasImage && !hasCheck) {
//		lpcmi.dwStyle |= OS.MNS_CHECKORBMP;
//	} else {
//		lpcmi.dwStyle &= ~OS.MNS_CHECKORBMP;
//	}
//	OS.SetMenuInfo (handle, lpcmi);
}

void releaseChild () {
	super.releaseChild ();
	if (cascade != null) cascade.setMenu (null);
	if ((style & SWT.BAR) != 0 && this == parent.menuBar) {
		parent.setMenuBar (null);
	}
}

void releaseHandle () {
	handle = 0;
}

void releaseWidget () {
	MenuItem [] items = getItems ();
	for (int i=0; i<items.length; i++) {
		MenuItem item = items [i];
		if (!item.isDisposed ()) item.releaseWidget ();
	}
	super.releaseWidget ();
	if (parent != null) parent.remove (this);
	parent = null;
	cascade = null;
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
 * be notified when the menu events are generated for the control.
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
 * @see MenuListener
 * @see #addMenuListener
 */
public void removeMenuListener (MenuListener listener) {
	checkWidget ();
	if (listener == null) error (SWT.ERROR_NULL_ARGUMENT);
	if (eventTable == null) return;
	eventTable.unhook (SWT.Hide, listener);
	eventTable.unhook (SWT.Show, listener);
}

/**
 * Sets the default menu item to the argument or removes
 * the default emphasis when the argument is <code>null</code>.
 * 
 * @param item the default menu item or null
 *
 * @exception IllegalArgumentException <ul>
 *    <li>ERROR_INVALID_ARGUMENT - if the menu item has been disposed</li> 
 * </ul>
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void setDefaultItem (MenuItem item) {
	checkWidget ();
	int command = -1;
	if (item != null) {
		if (item.isDisposed()) error(SWT.ERROR_INVALID_ARGUMENT);
		command = item.id;
	}
        OS.WinSendMsg (handle, OS.MM_SETDEFAULTITEMID,  command, 0);
	redraw ();
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
	state &= ~DISABLED;
	if (!enabled) state |= DISABLED;
}

/**
 * Sets the receiver's location to the point specified by
 * the arguments which are relative to the display.
 * <p>
 * Note:  This is different from most widgets where the
 * location of the widget is relative to the parent.
 * </p>
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
	if ((style & (SWT.BAR | SWT.DROP_DOWN)) != 0) return;
	this.x = x;
	this.y = y;
	hasLocation = true;
}

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
	if ((style & (SWT.BAR | SWT.DROP_DOWN)) != 0) return;
	if (!visible) {
		OS.WinSendMsg (handle, OS.MM_ENDMENUMODE, 1, 0);
		return;
	}
	/*
	* The position is in display coordinates, where y grows downwards,
	* while WinPopupMenu() takes desktop coordinates with the origin in
	* the lower left corner. Without a position set with setLocation()
	* the menu is shown at the pointer.
	*/
	int screenWidth = OS.WinQuerySysValue (OS.HWND_DESKTOP, OS.SV_CXSCREEN);
	int screenHeight = OS.WinQuerySysValue (OS.HWND_DESKTOP, OS.SV_CYSCREEN);
	int popupX, popupY;
	if (hasLocation) {
		popupX = x;
		popupY = screenHeight - y;
	} else {
		int [] pnt = new int [] {0, 0};
		OS.WinQueryMsgPos (0, pnt);
		popupX = pnt [0];
		popupY = pnt [1];
	}
	/*
	* WinPopupMenu() puts the lower left corner of the menu at the given
	* point, while the location of an SWT menu is its upper left corner,
	* and the size of the menu is known only after it has been shown. To
	* avoid showing it at the wrong place first, it is shown below the
	* screen, then moved: its upper left corner goes to the point, or its
	* lower left one if it does not fit below, and it is kept in the screen.
	*/
	short flOptions = (short) (OS.PU_KEYBOARD | OS.PU_MOUSEBUTTON1);
	OS.WinPopupMenu (OS.HWND_DESKTOP, parent.handle, handle, popupX, -screenHeight, 0, flOptions);
	SWP swp = new SWP ();
	if (OS.WinQueryWindowPos (handle, swp) && swp.cy > 0) {
		int left = Math.max (0, Math.min (popupX, screenWidth - swp.cx));
		int bottom = popupY - swp.cy;
		if (bottom < 0) bottom = popupY;
		bottom = Math.max (0, Math.min (bottom, screenHeight - swp.cy));
		OS.WinSetWindowPos (handle, 0, left, bottom, 0, 0, OS.SWP_MOVE);
	}
}
}
