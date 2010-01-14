
package org.eclipse.swt.internal.pm;

/*
 * OS/2 version.
 * Copyright (c) 2002, 2009 EclipseOS2 Team.
 */

/*
 * Copyright (c) 2000, 2002 IBM Corp.  All rights reserved.
 * This file is made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 */

import org.eclipse.swt.internal.*;

public class OS {

    /*
    * SWT OS/2 flags.
    */
    public static final boolean IsWarp3;
    public static final boolean IsWarp4;
    public static final boolean IsAurora;
    public static final int OS2_MAJOR, OS2_MINOR, OS2_REVISION;

    /*
    * Initialize the OS/2 flags
    */
    static {

        /* Load the SWT library */
        Library.loadLibrary ("swt");

        int[] sv = new int[3];
        OS.DosQuerySysInfo (OS.QSV_VERSION_MAJOR, OS.QSV_VERSION_REVISION,sv, sv.length*4);
        OS2_MAJOR = sv[0];
        OS2_MINOR = sv[1];
        OS2_REVISION = sv[2];
        
        IsWarp3 = (OS2_MAJOR == 20 && OS2_MINOR == 30);
        IsWarp4 = (OS2_MAJOR == 20 && OS2_MINOR == 40);
        IsAurora = (OS2_MAJOR == 20 && OS2_MINOR == 45);
    }

    /*
    * NOTE:  There is a bug in JVM 1.2 where loading
    * a class with a large number of constants causes
    * a segment fault to occur sometime later after
    * the class is loaded.  The fix is to break the
    * class up into a hierarchy of classes that each
    * contain a smaller number of constants.  This
    * fix is not necessary at this time but is required
    * when all constants are uncommented.  We have not
    * done the research to determine the limit.
    */

    /* Constants */
    
    public static final int FALSE =             0;
    public static final int TRUE =              1;
    public static final int NULLHANDLE =        0;
    
    /* indices for DosQuerySysInfo */
//    public static final int QSV_MAX_PATH_LENGTH =   1;
//    public static final int QSV_MAX_TEXT_SESSIONS = 2;
//    public static final int QSV_MAX_PM_SESSIONS =   3;
//    public static final int QSV_MAX_VDM_SESSIONS =  4;
//    public static final int QSV_BOOT_DRIVE =        5;
//    public static final int QSV_DYN_PRI_VARIATION = 6;
//    public static final int QSV_MAX_WAIT =          7;
//    public static final int QSV_MIN_SLICE =         8;
//    public static final int QSV_MAX_SLICE =         9;
//    public static final int QSV_PAGE_SIZE =         10;
    public static final int QSV_VERSION_MAJOR =     11;
    public static final int QSV_VERSION_MINOR =     12;
    public static final int QSV_VERSION_REVISION =  13;
//    public static final int QSV_MS_COUNT =          14;
//    public static final int QSV_TIME_LOW =          15;
//    public static final int QSV_TIME_HIGH =         16;
//    public static final int QSV_TOTPHYSMEM =        17;
//    public static final int QSV_TOTRESMEM =         18;
//    public static final int QSV_TOTAVAILMEM =       19;
//    public static final int QSV_MAXPRMEM =          20;
//    public static final int QSV_MAXSHMEM =          21;
//    public static final int QSV_TIMER_INTERVAL =    22;
//    public static final int QSV_MAX_COMP_LENGTH =   23;
//    public static final int QSV_FOREGROUND_FS_SESSION = 24;
//    public static final int QSV_FOREGROUND_PROCESS =    25;
//    public static final int QSV_NUMPROCESSORS =     26;
//    public static final int QSV_MAXHPRMEM =         27;
//    public static final int QSV_MAXHSHMEM =         28;
//    public static final int QSV_MAXPROCESSES =      29;
//    public static final int QSV_VIRTUALADDRESSLIMIT =   30;
//    public static final int QSV_INT10ENABLED =      31;
//    public static final int QSV_MAX =               QSV_INT10ENABLED;

    /* Predefined window handles */
    public static final int HWND_DESKTOP =      1;
    //public static final int HWND_OBJECT =       2;
    public static final int HWND_TOP =          3;
    public static final int HWND_BOTTOM =       4;

    /* Class styles */
//    public static final int CS_MOVENOTIFY =     0x00000001;
//    public static final int CS_SIZEREDRAW =     0x00000004;
    public static final int CS_HITTEST =        0x00000008;
//    public static final int CS_PUBLIC =         0x00000010;
//    public static final int CS_FRAME =          0x00000020;
//    public static final int CS_CLIPCHILDREN =   0x20000000;
//    public static final int CS_CLIPSIBLINGS =   0x10000000;
//    public static final int CS_PARENTCLIP =     0x08000000;
    public static final int CS_SAVEBITS =       0x04000000;
//    public static final int CS_SYNCPAINT =      0x02000000;

    /* Standard Window Classes */
    public static final int WC_FRAME =          0xffff0001;
    public static final int WC_COMBOBOX =       0xffff0002;
    public static final int WC_BUTTON =         0xffff0003;
    public static final int WC_MENU =           0xffff0004;
    public static final int WC_STATIC =         0xffff0005;
    public static final int WC_ENTRYFIELD =     0xffff0006;
    public static final int WC_LISTBOX =        0xffff0007;
    public static final int WC_SCROLLBAR =      0xffff0008;
//    public static final int WC_TITLEBAR =       0xffff0009;
    public static final int WC_MLE =            0xffff000A;
//    public static final int WC_APPSTAT =        0xffff0010;
//    public static final int WC_KBDSTAT =        0xffff0011;
//    public static final int WC_PECIC =          0xffff0012;
//    public static final int WC_DBE_KKPOPUP =    0xffff0013;
//    public static final int WC_SPINBUTTON =     0xffff0020;
    public static final int WC_CONTAINER =      0xffff0025;
    public static final int WC_SLIDER =         0xffff0026;
//    public static final int WC_VALUESET =       0xffff0027;
    public static final int WC_NOTEBOOK =       0xffff0028;
//    public static final int WC_PENFIRST =       0xffff0029;
//    public static final int WC_PENLAST =        0xffff002C;
//    public static final int WC_MMPMFIRST =      0xffff0040;
//    public static final int WC_CIRCULARSLIDER = 0xffff0041;
//    public static final int WC_MMPMLAST =       0xffff004f;
//    public static final int WC_PRISTDDLGFIRST = 0xffff0050;
//    public static final int WC_PRISTDDLGLAST =  0xffff0057;
//    public static final int WC_PUBSTDDLGFIRST = 0xffff0058;
//    public static final int WC_PUBSTDDLGLAST =  0xffff005f;

    /* Standard Window Styles */
    public static final int WS_VISIBLE =        0x80000000;
//    public static final int WS_DISABLED =       0x40000000;
    public static final int WS_CLIPCHILDREN =   0x20000000;
    public static final int WS_CLIPSIBLINGS =   0x10000000;
    public static final int WS_PARENTCLIP =     0x08000000;
    public static final int WS_SAVEBITS =       0x04000000;
    public static final int WS_SYNCPAINT =      0x02000000;
    public static final int WS_MINIMIZED =      0x01000000;
    public static final int WS_MAXIMIZED =      0x00800000;
//    public static final int WS_ANIMATE =        0x00400000;

    /* Dialog manager styles */
    public static final int WS_GROUP =          0x00010000;
    public static final int WS_TABSTOP =        0x00020000;
//    public static final int WS_MULTISELECT =    0x00040000;
    public static final int WS_SYSMENU =          0x00080000;

    /* Button control styles */
    public static final int BS_PUSHBUTTON         = 0;
    public static final int BS_CHECKBOX           = 1;
//    public static final int BS_AUTOCHECKBOX       = 2;
    public static final int BS_RADIOBUTTON        = 3;
//    public static final int BS_AUTORADIOBUTTON    = 4;
//    public static final int BS_3STATE             = 5;
//    public static final int BS_AUTO3STATE         = 6;
    public static final int BS_USERBUTTON         = 7;
//    public static final int BS_NOTEBOOKBUTTON     = 8;
    public static final int BS_PRIMARYSTYLES      = 0x000f;
//    public static final int BS_TEXT               = 0x0010;
//    public static final int BS_MINIICON           = 0x0020;
    public static final int BS_BITMAP             = 0x0040;
    public static final int BS_ICON               = 0x0080;
//    public static final int BS_HELP               = 0x0100;
//    public static final int BS_SYSCOMMAND         = 0x0200;
    public static final int BS_DEFAULT            = 0x0400;
    public static final int BS_NOPOINTERFOCUS     = 0x0800;
    public static final int BS_NOBORDER           = 0x1000;
    public static final int BS_NOCURSORSELECT     = 0x2000;
//    public static final int BS_AUTOSIZE           = 0x4000;
    
    /* WinAlarm Codes */
    public static final int WA_WARNING =    0;
    public static final int WA_NOTE =       1;
    public static final int WA_ERROR =      2;
    public static final int WA_CWINALARMS = 13;
    
    /* WinPeekMsg constants */
    public static final int PM_REMOVE =     0x0001;
    public static final int PM_NOREMOVE =   0x0000;

    /* Standard Window Messages */
    public static final int WM_NULL =               0x0000;
    public static final int WM_CREATE =             0x0001;
    public static final int WM_DESTROY =            0x0002;

    public static final int WM_ENABLE =             0x0004;
    public static final int WM_SHOW =               0x0005;
    public static final int WM_MOVE =               0x0006;
    public static final int WM_SIZE =               0x0007;
    public static final int WM_ADJUSTWINDOWPOS =    0x0008;

    public static final int WM_CALCVALIDRECTS =     0x0009;

    public static final int WM_SETWINDOWPARAMS =    0x000a;
    public static final int WM_QUERYWINDOWPARAMS =  0x000b;
    public static final int WM_HITTEST =            0x000c;
    public static final int WM_ACTIVATE =           0x000d;
    public static final int WM_SETFOCUS =           0x000f;
    public static final int WM_SETSELECTION =       0x0010;

    /* language support Winproc */
//    public static final int WM_PPAINT =             0x0011;
//    public static final int WM_PSETFOCUS =          0x0012;
//    public static final int WM_PSYSCOLORCHANGE =    0x0013;
//    public static final int WM_PSIZE =              0x0014;
//    public static final int WM_PACTIVATE =          0x0015;
//    public static final int WM_PCONTROL =           0x0016;

    public static final int WM_COMMAND =            0x0020;
    public static final int WM_SYSCOMMAND =         0x0021;
    public static final int WM_HELP =               0x0022;
    public static final int WM_PAINT =              0x0023;

    public static final int WM_TIMER =              0x0024;
    public static final int WM_SEM1 =               0x0025;
    public static final int WM_SEM2 =               0x0026;
    public static final int WM_SEM3 =               0x0027;
    public static final int WM_SEM4 =               0x0028;

    public static final int WM_CLOSE =              0x0029;
    public static final int WM_QUIT =               0x002a;
    public static final int WM_SYSCOLORCHANGE =     0x002b;
    public static final int WM_SYSVALUECHANGED =    0x002d;
    public static final int WM_APPTERMINATENOTIFY = 0x002e;
    public static final int WM_PRESPARAMCHANGED =   0x002f;
    
    /* Control notification messages */
    public static final int WM_CONTROL =            0x0030;
    public static final int WM_VSCROLL =            0x0031;
    public static final int WM_HSCROLL =            0x0032;
    public static final int WM_INITMENU =           0x0033;
    public static final int WM_MENUSELECT =         0x0034;
    public static final int WM_MENUEND =            0x0035;
    public static final int WM_DRAWITEM =           0x0036;
    public static final int WM_MEASUREITEM =        0x0037;
//    public static final int WM_CONTROLPOINTER =     0x0038;
    public static final int WM_QUERYDLGCODE =       0x003a;
//    public static final int WM_INITDLG =            0x003b;
//    public static final int WM_SUBSTITUTESTRING =   0x003c;
//    public static final int WM_MATCHMNEMONIC =      0x003d;
//    public static final int WM_SAVEAPPLICATION =    0x003e;
//    public static final int WM_SEMANTICEVENT =      0x0490;
//    public static final int WM_HELPBASE =           0x0F00;
//    public static final int WM_HELPTOP =            0x0FFF;
    public static final int WM_USER =               0x1000;
    
    /* Frame window related messages */
//    public static final int WM_FLASHWINDOW =        0x0040;
    public static final int WM_FORMATFRAME =        0x0041;
    public static final int WM_UPDATEFRAME =        0x0042;
    public static final int WM_FOCUSCHANGE =        0x0043;
//    public static final int WM_SETBORDERSIZE =      0x0044;
//    public static final int WM_TRACKFRAME =         0x0045;
//    public static final int WM_MINMAXFRAME =        0x0046;
//    public static final int WM_SETICON =            0x0047;
//    public static final int WM_QUERYICON =          0x0048;
    public static final int WM_SETACCELTABLE =      0x0049;
    public static final int WM_QUERYACCELTABLE =    0x004a;
    public static final int WM_TRANSLATEACCEL =     0x004b;
//    public static final int WM_QUERYTRACKINFO =     0x004c;
    public static final int WM_QUERYBORDERSIZE =    0x004d;
    public static final int WM_NEXTMENU =           0x004e;
    public static final int WM_ERASEBACKGROUND =    0x004f;
//    public static final int WM_QUERYFRAMEINFO =     0x0050;
//    public static final int WM_QUERYFOCUSCHAIN =    0x0051;
//    public static final int WM_OWNERPOSCHANGE =     0x0052;
    public static final int WM_CALCFRAMERECT =      0x0053;
    public static final int WM_WINDOWPOSCHANGED =   0x0055;
//    public static final int WM_ADJUSTFRAMEPOS =     0x0056;
//    public static final int WM_QUERYFRAMECTLCOUNT = 0x0059;
//    public static final int WM_QUERYHELPINFO =      0x005B;
//    public static final int WM_SETHELPINFO =        0x005C;
//    public static final int WM_ERROR =              0x005D;
    public static final int WM_REALIZEPALETTE =     0x005E;
    public static final int WM_CONTEXTMENU =        0x0424;
    /* WM_QUERYFRAMEINFO constants */
//    public static final int FI_FRAME =              0x00000001;
//    public static final int FI_OWNERHIDE =          0x00000002;
//    public static final int FI_ACTIVATEOK =         0x00000004;
//    public static final int FI_NOMOVEWITHOWNER =    0x00000008;

    /* Key/Character input messages */
    public static final int WM_CHAR =       0x007a;
//    public static final int WM_VIOCHAR =    0x007b;
    
/*** Mouse input messages ***********************************************/
   public static final int WM_MOUSEMOVE               = 0x0070; 
   public static final int WM_BUTTON1DOWN             = 0x0071; 
   public static final int WM_BUTTON1UP               = 0x0072;
   public static final int WM_BUTTON1DBLCLK           = 0x0073;
   public static final int WM_BUTTON2DOWN             = 0x0074;
   public static final int WM_BUTTON2UP               = 0x0075;
   public static final int WM_BUTTON2DBLCLK           = 0x0076;
   public static final int WM_BUTTON3DOWN          = 0x0077;
   public static final int WM_BUTTON3UP            = 0x0078;
   public static final int WM_BUTTON3DBLCLK        = 0x0079;
   
	/*
	 Mouse messages.
	 These messages are generated by Warp 4 and superior versions
	 These messages are posted to a window when the mouse pointer enters
	 or leaves the window rectangle.
	 The default window procedure returns FALSE in both cases.
	*/

	 /* mouse getting inside the window area
	 mp1 : handle of the window the mouse is moving to
	 mp2 : handle of the window previously under the mouse
	*/
   public static final int WM_MOUSEENTER       = 0x041e;
	/*
	 mouse getting outside the window area
	 mp1 : handle of the window the mouse is moving away from
	 mp2 : handle of the window the mouse is moving to
	*/   
   public static final int WM_MOUSEEXIT        = 0x041f;

    /* WM_CHAR fs field bits */
//    public static final int KC_NONE =           0x0000;
    public static final int KC_CHAR =           0x0001;
    public static final int KC_VIRTUALKEY =     0x0002;
    public static final int KC_SCANCODE =       0x0004;
    public static final int KC_SHIFT =          0x0008;
    public static final int KC_CTRL =           0x0010;
    public static final int KC_ALT =            0x0020;
    public static final int KC_KEYUP =          0x0040;
    public static final int KC_PREVDOWN =       0x0080;
//    public static final int KC_LONEKEY =        0x0100;
//    public static final int KC_DEADKEY =        0x0200;
//    public static final int KC_COMPOSITE =      0x0400;
//    public static final int KC_INVALIDCOMP =    0x0800;
//    public static final int KC_TOGGLE =         0x1000;
//    public static final int KC_INVALIDCHAR =    0x2000;
//    public static final int KC_DBCSRSRVD1 =     0x4000;
//    public static final int KC_DBCSRSRVD2 =     0x8000;
   
    /* Virtual key values */
    public static final int VK_BUTTON1 =    0x01;
    public static final int VK_BUTTON2 =    0x02;
    public static final int VK_BUTTON3 =    0x03;
//    public static final int VK_BREAK =      0x04;
    public static final int VK_BACKSPACE =  0x05;
    public static final int VK_TAB =        0x06;
    public static final int VK_BACKTAB =    0x07;
    public static final int VK_NEWLINE =    0x08;
    public static final int VK_SHIFT =      0x09;
    public static final int VK_CTRL =       0x0A;
    public static final int VK_ALT =        0x0B;
    public static final int VK_ALTGRAF =    0x0C;
//    public static final int VK_PAUSE =      0x0D;
    public static final int VK_CAPSLOCK =   0x0E;
    public static final int VK_ESC =        0x0F;
    public static final int VK_SPACE =      0x10;
    public static final int VK_PAGEUP =     0x11;
    public static final int VK_PAGEDOWN =   0x12;
    public static final int VK_END =        0x13;
    public static final int VK_HOME =       0x14;
    public static final int VK_LEFT =       0x15;
    public static final int VK_UP =         0x16;
    public static final int VK_RIGHT =      0x17;
    public static final int VK_DOWN =       0x18;
//    public static final int VK_PRINTSCRN =  0x19;
    public static final int VK_INSERT =     0x1A;
    public static final int VK_DELETE =     0x1B;
    public static final int VK_SCRLLOCK =   0x1C;
    public static final int VK_NUMLOCK =    0x1D;
    public static final int VK_ENTER =      0x1E;
//    public static final int VK_SYSRQ =      0x1F;
    public static final int VK_F1 =         0x20;
    public static final int VK_F2 =         0x21;
    public static final int VK_F3 =         0x22;
    public static final int VK_F4 =         0x23;
    public static final int VK_F5 =         0x24;
    public static final int VK_F6 =         0x25;
    public static final int VK_F7 =         0x26;
    public static final int VK_F8 =         0x27;
    public static final int VK_F9 =         0x28;
    public static final int VK_F10 =        0x29;
    public static final int VK_F11 =        0x2A;
    public static final int VK_F12 =        0x2B;
//    public static final int VK_F13 =        0x2C;
//    public static final int VK_F14 =        0x2D;
//    public static final int VK_F15 =        0x2E;
//    public static final int VK_F16 =        0x2F;
//    public static final int VK_F17 =        0x30;
//    public static final int VK_F18 =        0x31;
//    public static final int VK_F19 =        0x32;
//    public static final int VK_F20 =        0x33;
//    public static final int VK_F21 =        0x34;
//    public static final int VK_F22 =        0x35;
//    public static final int VK_F23 =        0x36;
//    public static final int VK_F24 =        0x37;
//    public static final int VK_ENDDRAG =    0x38;
//    public static final int VK_CLEAR =      0x39;
//    public static final int VK_EREOF =      0x3A;
//    public static final int VK_PA1 =        0x3B;
//    public static final int VK_ATTN =       0x3C;
//    public static final int VK_CRSEL =      0x3D;
//    public static final int VK_EXSEL =      0x3E;
//    public static final int VK_COPY =       0x3F;
//    public static final int VK_BLK1 =       0x40;
//    public static final int VK_BLK2 =       0x41;
    public static final int VK_MENU =       VK_F10;
//    public static final int VK_DBCSFIRST =  0x0080;
//    public static final int VK_DBCSLAST =   0x00ff;
//    public static final int VK_BIDI_FIRST = 0xE0;
//    public static final int VK_BIDI_LAST =  0xFF;
//    public static final int VK_USERFIRST =  0x0100;
//    public static final int VK_USERLAST =   0x01ff;

    /* Button control messages */
    public static final int BM_CLICK                = 0x0120;
//    public static final int BM_QUERYCHECKINDEX      = 0x0121;
    public static final int BM_QUERYHILITE          = 0x0122;
    public static final int BM_SETHILITE            = 0x0123;
    public static final int BM_QUERYCHECK           = 0x0124;
    public static final int BM_SETCHECK             = 0x0125;
    public static final int BM_SETDEFAULT           = 0x0126;
//    public static final int BM_AUTOSIZE             = 0x0128;
    /* Button notification codes */
    public static final int BN_CLICKED              = 1;
    public static final int BN_DBLCLICKED           = 2;
    public static final int BN_PAINT                = 3;
    /* BN_PAINT button draw state codes (must be in high byte) */
    public static final int BDS_HILITED             = 0x0100;
    public static final int BDS_DISABLED            = 0x0200;
    public static final int BDS_DEFAULT             = 0x0400;
    
    /* Standard WinQueryWindowUShort/ULong() indices */
    public static final int QWS_USER =      0;
    public static final int QWS_ID =        (-1);
//    public static final int QWS_MIN =       (-1);
    public static final int QWL_USER =      0;
    public static final int QWL_STYLE =     (-2);
    public static final int QWP_PFNWP =     (-3);
//    public static final int QWL_HMQ =       (-4);
//    public static final int QWL_RESERVED =  (-5);
//    public static final int QWL_PENDATA =   (-7);
//    public static final int QWL_BD_ATTR =   (-9);
//    public static final int QWL_BD_STAT =   (-10);
//    public static final int QWL_KBDLAYER =  (-11);
//    public static final int QWL_MIN =       (-11);
    /* WC_FRAME WinQueryWindowUShort/ULong() indices */
//    public static final int QWL_HHEAP =             0x0004;
//    public static final int QWL_HWNDFOCUSSAVE =     0x0018;
//    public static final int QWL_DEFBUTTON =         0x0040;
//    public static final int QWL_PSSCBLK =           0x0048;
//    public static final int QWL_PFEPBLK =           0x004c;
//    public static final int QWL_PSTATBLK =          0x0050;
//    public static final int QWS_FLAGS =             0x0008;
//    public static final int QWS_RESULT =            0x000a;
    public static final int QWS_XRESTORE =          0x000c;
    public static final int QWS_YRESTORE =          0x000e;
    public static final int QWS_CXRESTORE =         0x0010;
    public static final int QWS_CYRESTORE =         0x0012;
//    public static final int QWS_XMINIMIZE =         0x0014;
//    public static final int QWS_YMINIMIZE =         0x0016;

    /* WinQueryWindow() codes */
    public static final int QW_NEXT =       0;
//    public static final int QW_PREV =       1;
    public static final int QW_TOP =        2;
//    public static final int QW_BOTTOM =     3;
//    public static final int QW_OWNER =      4;
    public static final int QW_PARENT =     5;
//    public static final int QW_NEXTTOP =    6;
//    public static final int QW_PREVTOP =    7;
//    public static final int QW_FRAMEOWNER = 8;

    /* Win[Query|Set]SysValue constants */
//    public static final int SV_SWAPBUTTON =         0;
    public static final int SV_DBLCLKTIME =         1;
//    public static final int SV_CXDBLCLK =           2;
//    public static final int SV_CYDBLCLK =           3;
//    public static final int SV_CXSIZEBORDER =       4;
//    public static final int SV_CYSIZEBORDER =       5;
//    public static final int SV_ALARM =              6;
//    public static final int SV_CURSORRATE =         9;
//    public static final int SV_FIRSTSCROLLRATE =    10;
//    public static final int SV_SCROLLRATE =         11;
//    public static final int SV_NUMBEREDLISTS =      12;
//    public static final int SV_WARNINGFREQ =        13;
//    public static final int SV_NOTEFREQ =           14;
//    public static final int SV_ERRORFREQ =          15;
//    public static final int SV_WARNINGDURATION =    16;
//    public static final int SV_NOTEDURATION =       17;
//    public static final int SV_ERRORDURATION =      18;
    public static final int SV_CXSCREEN =           20;
    public static final int SV_CYSCREEN =           21;
    public static final int SV_CXVSCROLL =          22;
    public static final int SV_CYHSCROLL =          23;
    public static final int SV_CYVSCROLLARROW =     24;
    public static final int SV_CXHSCROLLARROW =     25;
    public static final int SV_CXBORDER =           26;
    public static final int SV_CYBORDER =           27;
//    public static final int SV_CXDLGFRAME =         28;
//    public static final int SV_CYDLGFRAME =         29;
//    public static final int SV_CYTITLEBAR =         30;
//    public static final int SV_CYVSLIDER =          31;
//    public static final int SV_CXHSLIDER =          32;
//    public static final int SV_CXMINMAXBUTTON =     33;
//    public static final int SV_CYMINMAXBUTTON =     34;
//    public static final int SV_CYMENU =             35;
//    public static final int SV_CXFULLSCREEN =       36;
//    public static final int SV_CYFULLSCREEN =       37;
    public static final int SV_CXICON =             38;
    public static final int SV_CYICON =             39;
    public static final int SV_CXPOINTER =          40;
    public static final int SV_CYPOINTER =          41;
//    public static final int SV_DEBUG =              42;
//    public static final int SV_CMOUSEBUTTONS =      43;
//    public static final int SV_CPOINTERBUTTONS =    43;
//    public static final int SV_POINTERLEVEL =       44;
//    public static final int SV_CURSORLEVEL =        45;
//    public static final int SV_TRACKRECTLEVEL =     46;
//    public static final int SV_CTIMERS =            47;
//    public static final int SV_MOUSEPRESENT =       48;
//    public static final int SV_CXBYTEALIGN =        49;
//    public static final int SV_CXALIGN =            49;
//    public static final int SV_CYBYTEALIGN =        50;
//    public static final int SV_CYALIGN =            50;
//    public static final int SV_NOTRESERVED =        56;
//    public static final int SV_EXTRAKEYBEEP =       57;
//    public static final int SV_SETLIGHTS =          58;
//    public static final int SV_INSERTMODE =         59;
//    public static final int SV_MENUROLLDOWNDELAY =  64;
//    public static final int SV_MENUROLLUPDELAY =    65;
//    public static final int SV_ALTMNEMONIC =        66;
//    public static final int SV_TASKLISTMOUSEACCESS =   67;
//    public static final int SV_CXICONTEXTWIDTH =    68;
//    public static final int SV_CICONTEXTLINES =     69;
//    public static final int SV_CHORDTIME =          70;
//    public static final int SV_CXCHORD =            71;
//    public static final int SV_CYCHORD =            72;
//    public static final int SV_CXMOTIONSTART =      73;
//    public static final int SV_CYMOTIONSTART =      74;
//    public static final int SV_BEGINDRAG =          75;
//    public static final int SV_ENDDRAG =            76;
//    public static final int SV_SINGLESELECT =       77;
//    public static final int SV_OPEN =               78;
//    public static final int SV_CONTEXTMENU =        79;
//    public static final int SV_CONTEXTHELP =        80;
//    public static final int SV_TEXTEDIT =           81;
//    public static final int SV_BEGINSELECT =        82;
//    public static final int SV_ENDSELECT =          83;
//    public static final int SV_BEGINDRAGKB =        84;
//    public static final int SV_ENDDRAGKB =          85;
//    public static final int SV_SELECTKB =           86;
//    public static final int SV_OPENKB =             87;
//    public static final int SV_CONTEXTMENUKB =      88;
//    public static final int SV_CONTEXTHELPKB =      89;
//    public static final int SV_TEXTEDITKB =         90;
//    public static final int SV_BEGINSELECTKB =      91;
//    public static final int SV_ENDSELECTKB =        92;
//    public static final int SV_ANIMATION =          93;
//    public static final int SV_ANIMATIONSPEED =     94;
//    public static final int SV_MONOICONS =          95;
//    public static final int SV_KBDALTERED =         96;
//    public static final int SV_PRINTSCREEN =        97;
//    public static final int SV_LOCKSTARTINPUT =     98;
//    public static final int SV_CSYSVALUES =         108;

    /* WinSetWindowPos() flags */
    public static final int SWP_SIZE =              0x0001;
    public static final int SWP_MOVE =              0x0002;
    public static final int SWP_ZORDER =            0x0004;
    public static final int SWP_SHOW =              0x0008;
//    public static final int SWP_HIDE =              0x0010;
    public static final int SWP_NOREDRAW =          0x0020;
    public static final int SWP_NOADJUST =          0x0040;
    public static final int SWP_ACTIVATE =          0x0080;
//    public static final int SWP_DEACTIVATE =        0x0100;
//    public static final int SWP_EXTSTATECHANGE =    0x0200;
    public static final int SWP_MINIMIZE =          0x0400;
    public static final int SWP_MAXIMIZE =          0x0800;
    public static final int SWP_RESTORE =           0x1000;
//    public static final int SWP_FOCUSACTIVATE =     0x2000;
//    public static final int SWP_FOCUSDEACTIVATE =   0x4000;
//    public static final int SWP_NOAUTOCLOSE =       0x8000;

    /* WM_CALCVALIDRECTS return flags */
//    public static final int CVR_ALIGNLEFT =     0x0001;
//    public static final int CVR_ALIGNBOTTOM =   0x0002;
//    public static final int CVR_ALIGNRIGHT =    0x0004;
//    public static final int CVR_ALIGNTOP =      0x0008;
    public static final int CVR_REDRAW =        0x0010;
    
    /* Frame window styles */
    public static final int FCF_TITLEBAR =          0x00000001;
    public static final int FCF_SYSMENU =           0x00000002;
    public static final int FCF_MENU =              0x00000004;
//    public static final int FCF_SIZEBORDER =        0x00000008;
    public static final int FCF_MINBUTTON =         0x00000010;
    public static final int FCF_MAXBUTTON =         0x00000020;
//    public static final int FCF_MINMAX =            0x00000030;
//    public static final int FCF_VERTSCROLL =        0x00000040;
//    public static final int FCF_HORZSCROLL =        0x00000080;
//    public static final int FCF_DLGBORDER =         0x00000100;
//    public static final int FCF_BORDER =            0x00000200;
//    public static final int FCF_SHELLPOSITION =     0x00000400;
//    public static final int FCF_TASKLIST =          0x00000800;
//    public static final int FCF_NOBYTEALIGN =       0x00001000;
//    public static final int FCF_NOMOVEWITHOWNER =   0x00002000;
//    public static final int FCF_ICON =              0x00004000;
    public static final int FCF_ACCELTABLE =        0x00008000;
//    public static final int FCF_SYSMODAL =          0x00010000;
//    public static final int FCF_SCREENALIGN =       0x00020000;
//    public static final int FCF_MOUSEALIGN =        0x00040000;
//    public static final int FCF_HIDEBUTTON =        0x01000000;
//    public static final int FCF_HIDEMAX =           0x01000020;
    public static final int FCF_CLOSEBUTTON =       0x04000000;
//    public static final int FCF_DBE_APPSTAT =       0x80000000;
//    public static final int FCF_AUTOICON =          0x40000000;
//    public static final int FCF_STANDARD =          0x0000CC3F;
//    public static final int FS_ICON =               0x00000001;
//    public static final int FS_ACCELTABLE =         0x00000002;
    public static final int FS_SHELLPOSITION =      0x00000004;
    public static final int FS_TASKLIST =           0x00000008;
    public static final int FS_NOBYTEALIGN =        0x00000010;
    public static final int FS_NOMOVEWITHOWNER =    0x00000020;
//    public static final int FS_SYSMODAL =           0x00000040;
    public static final int FS_DLGBORDER =          0x00000080;
    public static final int FS_BORDER =             0x00000100;
//    public static final int FS_SCREENALIGN =        0x00000200;
//    public static final int FS_MOUSEALIGN =         0x00000400;
    public static final int FS_SIZEBORDER =         0x00000800;
//    public static final int FS_AUTOICON =           0x00001000;
//    public static final int FS_DBE_APPSTAT =        0x00008000;
//    public static final int FS_STANDARD =           0x0000000F;
    /* Frame Window Flags accessed via WinSet/QueryWindowUShort(QWS_FLAGS) */
//    public static final int FF_FLASHWINDOW =        0x0001;
//    public static final int FF_ACTIVE =             0x0002;
//    public static final int FF_FLASHHILITE =        0x0004;
//    public static final int FF_OWNERHIDDEN =        0x0008;
//    public static final int FF_DLGDISMISSED =       0x0010;
//    public static final int FF_OWNERDISABLED =      0x0020;
//    public static final int FF_SELECTED =           0x0040;
//    public static final int FF_NOACTIVATESWP =      0x0080;
//    public static final int FF_DIALOGBOX =          0x0100;

    /* Frame control IDs    */
    public static final int FID_SYSMENU =           0x8002;
//    public static final int FID_TITLEBAR =          0x8003;
//    public static final int FID_MINMAX =            0x8004;
    public static final int FID_MENU =              0x8005;
//    public static final int FID_VERTSCROLL =        0x8006;
//    public static final int FID_HORZSCROLL =        0x8007;
    public static final int FID_CLIENT =            0x8008;

    /* General GPI return values */
    public static final int GPI_ERROR =     0;
    public static final int GPI_OK =        1;
    public static final int GPI_HITS =      2;
    //public static final int GPI_ALTERROR =  (-1);
    
    /* return code to indicate type of region for GpiCombineRegion and others */
    public static final int RGN_ERROR =     0;
    public static final int RGN_NULL =      1;
    public static final int RGN_RECT =      2;
    public static final int RGN_COMPLEX =   3;

    /* fill options for GpiBox/GpiFullArc/GpiOval */
    public static final int DRO_FILL =          1;
    public static final int DRO_OUTLINE =       2;
    public static final int DRO_OUTLINEFILL =   3;
    //public static final int DRO_EXCLUSIVE =     0x10000000;

    /* type of transformation for GpiSetSegmentTransformMatrix */
    public static final int TRANSFORM_REPLACE =     0;
//    public static final int TRANSFORM_ADD =         1;
//    public static final int TRANSFORM_PREEMPT =     2;

    /* ulDirection of returned region data for GpiQueryRegionRects */
    public static final int RECTDIR_LFRT_TOPBOT =   1;
//    public static final int RECTDIR_RTLF_TOPBOT =   2;
//    public static final int RECTDIR_LFRT_BOTTOP =   3;
//    public static final int RECTDIR_RTLF_BOTTOP =   4;

    /* codes for DevQueryCaps */
//    public static final int CAPS_FAMILY =                   0;
//    public static final int CAPS_IO_CAPS =                  1;
//    public static final int CAPS_TECHNOLOGY =               2;
//    public static final int CAPS_DRIVER_VERSION =           3;
    public static final int CAPS_WIDTH =                    4;      /* pels            */
    public static final int CAPS_HEIGHT =                   5;      /* pels            */
//    public static final int CAPS_WIDTH_IN_CHARS =           6;
//    public static final int CAPS_HEIGHT_IN_CHARS =          7;
//    public static final int CAPS_HORIZONTAL_RESOLUTION =    8;       /* pels per meter  */
//    public static final int CAPS_VERTICAL_RESOLUTION =      9;       /* pels per meter  */
//    public static final int CAPS_CHAR_WIDTH =               10;      /* pels            */
//    public static final int CAPS_CHAR_HEIGHT =              11;      /* pels            */
//    public static final int CAPS_SMALL_CHAR_WIDTH =         12;      /* pels            */
//    public static final int CAPS_SMALL_CHAR_HEIGHT =        13;      /* pels            */
    public static final int CAPS_COLORS =                   14;
    public static final int CAPS_COLOR_PLANES =             15;
//    public static final int CAPS_COLOR_BITCOUNT =           16;
//    public static final int CAPS_COLOR_TABLE_SUPPORT =      17;
//    public static final int CAPS_MOUSE_BUTTONS =            18;
//    public static final int CAPS_FOREGROUND_MIX_SUPPORT =   19;
//    public static final int CAPS_BACKGROUND_MIX_SUPPORT =   20;
//    public static final int CAPS_VIO_LOADABLE_FONTS =       21;
//    public static final int CAPS_WINDOW_BYTE_ALIGNMENT =    22;
    public static final int CAPS_BITMAP_FORMATS =           23;
//    public static final int CAPS_RASTER_CAPS =              24;
//    public static final int CAPS_MARKER_HEIGHT =            25;      /* pels            */
//    public static final int CAPS_MARKER_WIDTH =             26;      /* pels            */
//    public static final int CAPS_DEVICE_FONTS =             27;
//    public static final int CAPS_GRAPHICS_SUBSET =          28;
//    public static final int CAPS_GRAPHICS_VERSION =         29;
//    public static final int CAPS_GRAPHICS_VECTOR_SUBSET =   30; 
//    public static final int CAPS_DEVICE_WINDOWING =         31;
    public static final int CAPS_ADDITIONAL_GRAPHICS =      32;
//    public static final int CAPS_PHYS_COLORS =              33;
//    public static final int CAPS_COLOR_INDEX =              34;
//    public static final int CAPS_GRAPHICS_CHAR_WIDTH =      35;
    public static final int CAPS_GRAPHICS_CHAR_HEIGHT =     36;
    public static final int CAPS_HORIZONTAL_FONT_RES =      37;
    public static final int CAPS_VERTICAL_FONT_RES =        38;
//    public static final int CAPS_DEVICE_FONT_SIM =          39;
//    public static final int CAPS_LINEWIDTH_THICK =          40;
//    public static final int CAPS_DEVICE_POLYSET_POINTS =    41;
    /* Constants for CAPS_IO_CAPS */
//    public static final int CAPS_IO_DUMMY =                 1;
//    public static final int CAPS_IO_SUPPORTS_OP =           2;
//    public static final int CAPS_IO_SUPPORTS_IP =           3;
//    public static final int CAPS_IO_SUPPORTS_IO =           4; 
    /* Constants for CAPS_TECHNOLOGY */
//    public static final int CAPS_TECH_UNKNOWN =             0;
//    public static final int CAPS_TECH_VECTOR_PLOTTER =      1;
//    public static final int CAPS_TECH_RASTER_DISPLAY =      2;
//    public static final int CAPS_TECH_RASTER_PRINTER =      3;
//    public static final int CAPS_TECH_RASTER_CAMERA =       4;
//    public static final int CAPS_TECH_POSTSCRIPT =          5;
    /* Constants for CAPS_COLOR_TABLE_SUPPORT */
//    public static final int CAPS_COLTABL_RGB_8 =            1;
//    public static final int CAPS_COLTABL_RGB_8_PLUS =       2;
//    public static final int CAPS_COLTABL_TRUE_MIX =         4;
//    public static final int CAPS_COLTABL_REALIZE =          8;
    /* Constants for CAPS_FOREGROUND_MIX_SUPPORT */
//    public static final int CAPS_FM_OR =                    1;
//    public static final int CAPS_FM_OVERPAINT =             2;
//    public static final int CAPS_FM_XOR =                   8;
//    public static final int CAPS_FM_LEAVEALONE =            16;
//    public static final int CAPS_FM_AND =                   32;
//    public static final int CAPS_FM_GENERAL_BOOLEAN =       64;
    /* Constants for CAPS_BACKGROUND_MIX_SUPPORT */
//    public static final int CAPS_BM_OR =                    1;
//    public static final int CAPS_BM_OVERPAINT =             2;
//    public static final int CAPS_BM_XOR =                   8;
//    public static final int CAPS_BM_LEAVEALONE =            16;
//    public static final int CAPS_BM_AND =                   32;
//    public static final int CAPS_BM_GENERAL_BOOLEAN =       64;
//    public static final int CAPS_BM_SRCTRANSPARENT =        128;
//    public static final int CAPS_BM_DESTTRANSPARENT =       256;
    /* Constants for CAPS_DEVICE_WINDOWING */
//    public static final int CAPS_DEV_WINDOWING_SUPPORT =    1;
    /* Constants for CAPS_DEV_FONT_SIM     */
//    public static final int CAPS_DEV_FONT_SIM_BOLD =        1;
//    public static final int CAPS_DEV_FONT_SIM_ITALIC =      2;
//    public static final int CAPS_DEV_FONT_SIM_UNDERSCORE =  4;
//    public static final int CAPS_DEV_FONT_SIM_STRIKEOUT =   8;
    /* Constants for CAPS_ADDITIONAL_GRAPHICS */
//    public static final int CAPS_VDD_DDB_TRANSFER =         1;
//    public static final int CAPS_GRAPHICS_KERNING_SUPPORT = 2;
//    public static final int CAPS_FONT_OUTLINE_DEFAULT =     4;
//    public static final int CAPS_FONT_IMAGE_DEFAULT =       8;
//    public static final int CAPS_SCALED_DEFAULT_MARKERS =   64;
//    public static final int CAPS_COLOR_CURSOR_SUPPORT =     128;
    public static final int CAPS_PALETTE_MANAGER =          256;
//    public static final int CAPS_COSMETIC_WIDELINE_SUPPORT =512;
//    public static final int CAPS_DIRECT_FILL =              1024;
//    public static final int CAPS_REBUILD_FILLS =            2048;
//    public static final int CAPS_CLIP_FILLS =               0x00001000; /* 4096L  */
//    public static final int CAPS_ENHANCED_FONTMETRICS =     0x00002000; /* 8192L  */
//    public static final int CAPS_TRANSFORM_SUPPORT =        0x00004000; /* 16384L */
//    public static final int CAPS_EXTERNAL_16_BITCOUNT =     0x00008000; /* 32768L */
//    public static final int CAPS_EXTERNAL_32_BITCOUNT =     0x00010000; /* 65536L */
    /* Constants for CAPS_WINDOW_BYTE_ALIGNMENT */
//    public static final int CAPS_BYTE_ALIGN_REQUIRED =      0;
//    public static final int CAPS_BYTE_ALIGN_RECOMMENDED =   1;
//    public static final int CAPS_BYTE_ALIGN_NOT_REQUIRED =  2;
    /* Constants for CAPS_RASTER_CAPS */
//    public static final int CAPS_RASTER_BITBLT =            1;
//    public static final int CAPS_RASTER_BANDING =           2;
//    public static final int CAPS_RASTER_BITBLT_SCALING =    4;
//    public static final int CAPS_RASTER_SET_PEL =           16;
//    public static final int CAPS_RASTER_FONTS =             32;
//    public static final int CAPS_RASTER_FLOOD_FILL =        64;
    
    /* options for GpiCreateLogColorTable and others */
//    public static final int LCOL_RESET =                    0x0001;
//    public static final int LCOL_REALIZABLE =               0x0002;
//    public static final int LCOL_PURECOLOR =                0x0004;
//    public static final int LCOL_OVERRIDE_DEFAULT_COLORS =  0x0008;
//    public static final int LCOL_REALIZED =                 0x0010;
    /* format of logical lColor table for GpiCreateLogColorTable and others */
//    public static final int LCOLF_DEFAULT =                 0;
//    public static final int LCOLF_INDRGB =                  1;
    public static final int LCOLF_CONSECRGB =               2;
    public static final int LCOLF_RGB =                     3;
//    public static final int LCOLF_PALETTE =                 4;

    /* foreground mixes */
//    public static final int FM_ERROR =                      -1;
//    public static final int FM_DEFAULT =                    0;
//    public static final int FM_OR =                         1;
    public static final int FM_OVERPAINT =                  2;
    public static final int FM_LEAVEALONE =                 5;
    public static final int FM_XOR =                        4;
//    public static final int FM_AND =                        6;
//    public static final int FM_SUBTRACT =                   7;
//    public static final int FM_MASKSRCNOT =                 8;
//    public static final int FM_ZERO =                       9;
//    public static final int FM_NOTMERGESRC =                10;
//    public static final int FM_NOTXORSRC =                  11;
//    public static final int FM_INVERT =                     12;
//    public static final int FM_MERGESRCNOT =                13;
//    public static final int FM_NOTCOPYSRC =                 14;
//    public static final int FM_MERGENOTSRC =                15;
//    public static final int FM_NOTMASKSRC =                 16;
//    public static final int FM_ONE =                        17;
    
    /* background mixes */
//    public static final int BM_ERROR =                      -1;
//    public static final int BM_DEFAULT =                    0;
//    public static final int BM_OR =                         1;
    public static final int BM_OVERPAINT =                  2;
    public static final int BM_LEAVEALONE =                 5;
    public static final int BM_XOR =                        4;
//    public static final int BM_AND =                        6;
//    public static final int BM_SUBTRACT =                   7;
//    public static final int BM_MASKSRCNOT =                 8;
//    public static final int BM_ZERO =                       9;
//    public static final int BM_NOTMERGESRC =                10;
//    public static final int BM_NOTXORSRC =                  11;
//    public static final int BM_INVERT =                     12;
//    public static final int BM_MERGESRCNOT =                13;
//    public static final int BM_NOTCOPYSRC =                 14;
//    public static final int BM_MERGENOTSRC =                15;
//    public static final int BM_NOTMASKSRC =                 16;
//    public static final int BM_ONE =                        17;
    public static final int BM_SRCTRANSPARENT =             18;
    public static final int BM_DESTTRANSPARENT =            19;

    /* basic pattern symbols */
//    public static final int PATSYM_ERROR =                  -1;
//    public static final int PATSYM_DEFAULT =                0;
//    public static final int PATSYM_DENSE1 =                 1;
//    public static final int PATSYM_DENSE2 =                 2;
//    public static final int PATSYM_DENSE3 =                 3;
//    public static final int PATSYM_DENSE4 =                 4;
//    public static final int PATSYM_DENSE5 =                 5;
//    public static final int PATSYM_DENSE6 =                 6;
//    public static final int PATSYM_DENSE7 =                 7;
//    public static final int PATSYM_DENSE8 =                 8;
//    public static final int PATSYM_VERT =                   9;
//    public static final int PATSYM_HORIZ =                  10;
//    public static final int PATSYM_DIAG1 =                  11;
//    public static final int PATSYM_DIAG2 =                  12;
//    public static final int PATSYM_DIAG3 =                  13;
//    public static final int PATSYM_DIAG4 =                  14;
//    public static final int PATSYM_NOSHADE =                15;
    public static final int PATSYM_SOLID =                  16;
    public static final int PATSYM_HALFTONE =               17;
//    public static final int PATSYM_HATCH =                  18;
//    public static final int PATSYM_DIAGHATCH =              19;
    public static final int PATSYM_BLANK =                  64;

    public static final int SYSCLR_SHADOWHILITEBGND =       -50;
    public static final int SYSCLR_SHADOWHILITEFGND =       -49;
    public static final int SYSCLR_SHADOWTEXT =             -48;
    public static final int SYSCLR_ENTRYFIELD =             -47;
    public static final int SYSCLR_MENUDISABLEDTEXT =       -46;
    public static final int SYSCLR_MENUHILITE =             -45;
    public static final int SYSCLR_MENUHILITEBGND =         -44;
    public static final int SYSCLR_PAGEBACKGROUND =         -43;
    public static final int SYSCLR_FIELDBACKGROUND =        -42;
    public static final int SYSCLR_BUTTONLIGHT =            -41;
    public static final int SYSCLR_BUTTONMIDDLE =           -40;
    public static final int SYSCLR_BUTTONDARK =             -39;
    public static final int SYSCLR_BUTTONDEFAULT =          -38;
//    public static final int SYSCLR_TITLEBOTTOM =            -37;
    public static final int SYSCLR_SHADOW =                 -36;
//    public static final int SYSCLR_ICONTEXT =               -35;
    public static final int SYSCLR_DIALOGBACKGROUND =       -34;
//    public static final int SYSCLR_HILITEFOREGROUND =       -33;
//    public static final int SYSCLR_HILITEBACKGROUND =       -32;
//    public static final int SYSCLR_INACTIVETITLETEXTBGND =  -31;
//    public static final int SYSCLR_ACTIVETITLETEXTBGND =    -30;
    public static final int SYSCLR_INACTIVETITLETEXT =      -29;
    public static final int SYSCLR_ACTIVETITLETEXT =        -28;
//    public static final int SYSCLR_OUTPUTTEXT =             -27;
//    public static final int SYSCLR_WINDOWSTATICTEXT =       -26;
    public static final int SYSCLR_SCROLLBAR =              -25;
    public static final int SYSCLR_BACKGROUND =             -24;
    public static final int SYSCLR_ACTIVETITLE =            -23;
    public static final int SYSCLR_INACTIVETITLE =          -22;
    public static final int SYSCLR_MENU =                   -21;
    public static final int SYSCLR_WINDOW =                 -20;
    public static final int SYSCLR_WINDOWFRAME =            -19;
    public static final int SYSCLR_MENUTEXT =               -18;
    public static final int SYSCLR_WINDOWTEXT =             -17;
//    public static final int SYSCLR_TITLETEXT =              -16;
//    public static final int SYSCLR_ACTIVEBORDER =           -15;
//    public static final int SYSCLR_INACTIVEBORDER =         -14;
//    public static final int SYSCLR_APPWORKSPACE =           -13;
//    public static final int SYSCLR_HELPBACKGROUND =         -12;
//    public static final int SYSCLR_HELPTEXT =               -11;
//    public static final int SYSCLR_HELPHILITE =             -10;

    /* control flags used by GpiBeginArea */
//    public static final int BA_NOBOUNDARY =                 0;
//    public static final int BA_BOUNDARY =                   0x0001;
//    public static final int BA_ALTERNATE =                  0;
//    public static final int BA_WINDING =                    0x0002;
//    public static final int BA_INCL =                       0;
//    public static final int BA_EXCL =                       8;

    /* basic line type styles */
//    public static final int LINETYPE_ERROR =                -1;
//    public static final int LINETYPE_DEFAULT =              0;
    public static final int LINETYPE_DOT =                  1;
//    public static final int LINETYPE_SHORTDASH =            2;
    public static final int LINETYPE_DASHDOT =              3;
//    public static final int LINETYPE_DOUBLEDOT =            4;
    public static final int LINETYPE_LONGDASH =             5;
    public static final int LINETYPE_DASHDOUBLEDOT =        6;
    public static final int LINETYPE_SOLID =                7;
//    public static final int LINETYPE_INVISIBLE =            8;
    public static final int LINETYPE_ALTERNATE =            9;

    /* cosmetic line widths */
//    public static final int LINEWIDTH_ERROR =               -1;
//    public static final int LINEWIDTH_DEFAULT =             0;
    public static final int LINEWIDTH_NORMAL =              0x00010000;
//    public static final int LINEWIDTH_THICK =               0x00020000;

    /* line end styles */
//    public static final int LINEEND_ERROR =                 -1;
//    public static final int LINEEND_DEFAULT =               0;
//    public static final int LINEEND_FLAT =                  1;
//    public static final int LINEEND_SQUARE =                2;
    public static final int LINEEND_ROUND =                 3;

    /* line join styles */
//    public static final int LINEJOIN_ERROR =                -1;
//    public static final int LINEJOIN_DEFAULT =              0;
//    public static final int LINEJOIN_BEVEL =                1;
    public static final int LINEJOIN_ROUND =                2;
//    public static final int LINEJOIN_MITRE =                3;

    /* options for GpiCombineRegion */
    public static final int CRGN_OR =                       1;
    public static final int CRGN_COPY =                     2;
//    public static final int CRGN_XOR =                      4;
//    public static final int CRGN_AND =                      6;
//    public static final int CRGN_DIFF =                     7;

    /* return codes for GpiPtInRegion */
//    public static final int PRGN_ERROR =                    0;
//    public static final int PRGN_OUTSIDE =                  1;
    public static final int PRGN_INSIDE =                   2;

    /* return codes for GpiRectInRegion */
//    public static final int RRGN_ERROR =                    0;
//    public static final int RRGN_OUTSIDE =                  1;
    public static final int RRGN_PARTIAL =                  2;
    public static final int RRGN_INSIDE =                   3;

    /* System bitmaps (NOTE: these are 1-based) */
//    public static final int SBMP_OLD_SYSMENU =              1;
//    public static final int SBMP_OLD_SBUPARROW =            2;
//    public static final int SBMP_OLD_SBDNARROW =            3;
//    public static final int SBMP_OLD_SBRGARROW =            4;
//    public static final int SBMP_OLD_SBLFARROW =            5;
//    public static final int SBMP_MENUCHECK =                6;
//    public static final int SBMP_OLD_CHECKBOXES =           7;
//    public static final int SBMP_BTNCORNERS =               8;
//    public static final int SBMP_OLD_MINBUTTON =            9;
//    public static final int SBMP_OLD_MAXBUTTON =            10;
//    public static final int SBMP_OLD_RESTOREBUTTON =        11;
//    public static final int SBMP_OLD_CHILDSYSMENU =         12;
//    public static final int SBMP_DRIVE =                    15;
//    public static final int SBMP_FILE =                     16;
//    public static final int SBMP_FOLDER =                   17;
//    public static final int SBMP_TREEPLUS =                 18;
//    public static final int SBMP_TREEMINUS =                19;
//    public static final int SBMP_PROGRAM =                  22;
//    public static final int SBMP_MENUATTACHED =             23;
//    public static final int SBMP_SIZEBOX =                  24;
//    public static final int SBMP_SYSMENU =                  25;
//    public static final int SBMP_MINBUTTON =                26;
//    public static final int SBMP_MAXBUTTON =                27;
//    public static final int SBMP_RESTOREBUTTON =            28;
//    public static final int SBMP_CHILDSYSMENU =             29;
//    public static final int SBMP_SYSMENUDEP =               30;
//    public static final int SBMP_MINBUTTONDEP =             31;
//    public static final int SBMP_MAXBUTTONDEP =             32;
//    public static final int SBMP_RESTOREBUTTONDEP =         33;
//    public static final int SBMP_CHILDSYSMENUDEP =          34;
//    public static final int SBMP_SBUPARROW =                35;
//    public static final int SBMP_SBDNARROW =                36;
//    public static final int SBMP_SBLFARROW =                37;
//    public static final int SBMP_SBRGARROW =                38;
//    public static final int SBMP_SBUPARROWDEP =             39;
//    public static final int SBMP_SBDNARROWDEP =             40;
//    public static final int SBMP_SBLFARROWDEP =             41;
//    public static final int SBMP_SBRGARROWDEP =             42;
//    public static final int SBMP_SBUPARROWDIS =             43;
//    public static final int SBMP_SBDNARROWDIS =             44;
//    public static final int SBMP_SBLFARROWDIS =             45;
//    public static final int SBMP_SBRGARROWDIS =             46;
//    public static final int SBMP_COMBODOWN =                47;
    public static final int SBMP_CHECKBOXES =               48;
//    public static final int SBMP_HIDE =                     50;
//    public static final int SBMP_HIDEDEP =                  51;
//    public static final int SBMP_CLOSE =                    52;
//    public static final int SBMP_CLOSEDEP =                 53;

    /* Presentation parameter types. */
    public static final int PP_FOREGROUNDCOLOR              = 1;
    public static final int PP_FOREGROUNDCOLORINDEX         = 2;
    public static final int PP_BACKGROUNDCOLOR              = 3;
    public static final int PP_BACKGROUNDCOLORINDEX         = 4;
//    public static final int PP_HILITEFOREGROUNDCOLOR        = 5;
//    public static final int PP_HILITEFOREGROUNDCOLORINDEX   = 6;
//    public static final int PP_HILITEBACKGROUNDCOLOR        = 7;
//    public static final int PP_HILITEBACKGROUNDCOLORINDEX   = 8;
    public static final int PP_DISABLEDFOREGROUNDCOLOR      = 9;
    public static final int PP_DISABLEDFOREGROUNDCOLORINDEX = 10;
    public static final int PP_DISABLEDBACKGROUNDCOLOR      = 11;
    public static final int PP_DISABLEDBACKGROUNDCOLORINDEX = 12;
//    public static final int PP_BORDERCOLOR                  = 13;
//    public static final int PP_BORDERCOLORINDEX             = 14;
    public static final int PP_FONTNAMESIZE                 = 15;
//    public static final int PP_FONTHANDLE                   = 16;
//    public static final int PP_RESERVED                     = 17;
//    public static final int PP_ACTIVECOLOR                  = 18;
//    public static final int PP_ACTIVECOLORINDEX             = 19;
//    public static final int PP_INACTIVECOLOR                = 20;
//    public static final int PP_INACTIVECOLORINDEX           = 21;
//    public static final int PP_ACTIVETEXTFGNDCOLOR          = 22;
//    public static final int PP_ACTIVETEXTFGNDCOLORINDEX     = 23;
//    public static final int PP_ACTIVETEXTBGNDCOLOR          = 24;
//    public static final int PP_ACTIVETEXTBGNDCOLORINDEX     = 25;
//    public static final int PP_INACTIVETEXTFGNDCOLOR        = 26;
//    public static final int PP_INACTIVETEXTFGNDCOLORINDEX   = 27;
//    public static final int PP_INACTIVETEXTBGNDCOLOR        = 28;
//    public static final int PP_INACTIVETEXTBGNDCOLORINDEX   = 29;
//    public static final int PP_SHADOW                       = 30;
    public static final int PP_MENUFOREGROUNDCOLOR          = 31;
    public static final int PP_MENUFOREGROUNDCOLORINDEX     = 32;
    public static final int PP_MENUBACKGROUNDCOLOR          = 33;
    public static final int PP_MENUBACKGROUNDCOLORINDEX     = 34;
    public static final int PP_MENUHILITEFGNDCOLOR          = 35;
    public static final int PP_MENUHILITEFGNDCOLORINDEX     = 36;
    public static final int PP_MENUHILITEBGNDCOLOR          = 37;
    public static final int PP_MENUHILITEBGNDCOLORINDEX     = 38;
    public static final int PP_MENUDISABLEDFGNDCOLOR        = 39;
    public static final int PP_MENUDISABLEDFGNDCOLORINDEX   = 40;
    public static final int PP_MENUDISABLEDBGNDCOLOR        = 41;
    public static final int PP_MENUDISABLEDBGNDCOLORINDEX   = 42;
//    public static final int PP_SHADOWTEXTCOLOR              = 43;
//    public static final int PP_SHADOWTEXTCOLORINDEX         = 44;
//    public static final int PP_SHADOWHILITEFGNDCOLOR        = 45;
//    public static final int PP_SHADOWHILITEFGNDCOLORINDEX   = 46;
//    public static final int PP_SHADOWHILITEBGNDCOLOR        = 47;
//    public static final int PP_SHADOWHILITEBGNDCOLORINDEX   = 48;
//    public static final int PP_ICONTEXTBACKGROUNDCOLOR      = 49;
//    public static final int PP_ICONTEXTBACKGROUNDCOLORINDEX = 50;
//    public static final int PP_BORDERLIGHTCOLOR             = 51;
//    public static final int PP_BORDERDARKCOLOR              = 52;
//    public static final int PP_BORDER2COLOR                 = 53;
//    public static final int PP_BORDER2LIGHTCOLOR            = 54;
//    public static final int PP_BORDER2DARKCOLOR             = 55;
//    public static final int PP_BORDERDEFAULTCOLOR           = 56;
//    public static final int PP_FIELDBACKGROUNDCOLOR         = 57;
//    public static final int PP_BUTTONBACKGROUNDCOLOR        = 58;
//    public static final int PP_BUTTONBORDERLIGHTCOLOR       = 59;
//    public static final int PP_BUTTONBORDERDARKCOLOR        = 60;
//    public static final int PP_ARROWCOLOR                   = 61;
//    public static final int PP_ARROWBORDERLIGHTCOLOR        = 62;
//    public static final int PP_ARROWBORDERDARKCOLOR         = 63;
//    public static final int PP_ARROWDISABLEDCOLOR           = 64;
//    public static final int PP_CHECKLIGHTCOLOR              = 65;
//    public static final int PP_CHECKMIDDLECOLOR             = 66;
//    public static final int PP_CHECKDARKCOLOR               = 67;
//    public static final int PP_PAGEFOREGROUNDCOLOR          = 68;
//    public static final int PP_PAGEBACKGROUNDCOLOR          = 69;
//    public static final int PP_MAJORTABFOREGROUNDCOLOR      = 70;
//    public static final int PP_MAJORTABBACKGROUNDCOLOR      = 71;
//    public static final int PP_MINORTABFOREGROUNDCOLOR      = 72;
//    public static final int PP_MINORTABBACKGROUNDCOLOR      = 73;
//    public static final int PP_BIDI_FIRST                   = 0x0100;
//    public static final int PP_BIDI_LAST                    = 0x012F;
//    public static final int PP_USER                         = 0x8000;

    /* Flags for WinQueryPresParams() */
//    public static final int QPF_NOINHERIT           = 0x0001;
//    public static final int QPF_ID1COLORINDEX       = 0x0002;
//    public static final int QPF_ID2COLORINDEX       = 0x0004;
//    public static final int QPF_PURERGBCOLOR        = 0x0008;
//    public static final int QPF_VALIDFLAGS          = 0x000F;

    /* Values returned from WM_ADJUSTWINDOWPOS and passed to WM_WINDOWPOSCHANGED */
    public static final int AWP_MINIMIZED           = 0x00010000;
    public static final int AWP_MAXIMIZED           = 0x00020000;
    public static final int AWP_RESTORED            = 0x00040000;
    public static final int AWP_ACTIVATE            = 0x00080000;
    public static final int AWP_DEACTIVATE          = 0x00100000;

    /* WM_COMMAND msg source codes */
    public static final int CMDSRC_PUSHBUTTON       = 1;
    public static final int CMDSRC_MENU             = 2;
    public static final int CMDSRC_ACCELERATOR      = 3;
    public static final int CMDSRC_FONTDLG          = 4;
    public static final int CMDSRC_FILEDLG          = 5;
    public static final int CMDSRC_PRINTDLG         = 6;
    public static final int CMDSRC_COLORDLG         = 7;
    public static final int CMDSRC_OTHER            = 0;

    /* Dialog codes returned by WM_QUERYDLGCODE msg */
    public static final int DLGC_ENTRYFIELD         = 0x0001;
    public static final int DLGC_BUTTON             = 0x0002;
    public static final int DLGC_RADIOBUTTON        = 0x0004;
    public static final int DLGC_STATIC             = 0x0008;
    public static final int DLGC_DEFAULT            = 0x0010;
    public static final int DLGC_PUSHBUTTON         = 0x0020;
    public static final int DLGC_CHECKBOX           = 0x0040;
    public static final int DLGC_SCROLLBAR          = 0x0080;
    public static final int DLGC_MENU               = 0x0100;
    public static final int DLGC_TABONCLICK         = 0x0200;
    public static final int DLGC_MLE                = 0x0400;
    
    /* DC type for DevOpenDC */
//    public static final int OD_SCREEN               = 0;
//    public static final int OD_QUEUED               = 2;
//    public static final int OD_DIRECT               = 5;
//    public static final int OD_INFO                 = 6;
//    public static final int OD_METAFILE             = 7;
    public static final int OD_MEMORY               = 8;
//    public static final int OD_METAFILE_NOQUERY     = 9;

    /* units for GpiCreatePS and others */
    public static final int PU_ARBITRARY            = 0x0004;
    public static final int PU_PELS                 = 0x0008;
//    public static final int PU_LOMETRIC             = 0x000C;
//    public static final int PU_HIMETRIC             = 0x0010;
//    public static final int PU_LOENGLISH            = 0x0014;
//    public static final int PU_HIENGLISH            = 0x0018;
//    public static final int PU_TWIPS                = 0x001C;

    /* format for GpiCreatePS */
//    public static final int GPIF_DEFAULT            = 0;
//    public static final int GPIF_SHORT              = 0x0100;
//    public static final int GPIF_LONG               = 0x0200;

    /* PS type for GpiCreatePS */
//    public static final int GPIT_NORMAL             = 0;
    public static final int GPIT_MICRO              = 0x1000;
//    public static final int GPIT_INK                = 0x2000;

    /* implicit associate flag for GpiCreatePS */
//    public static final int GPIA_NOASSOC            = 0;
    public static final int GPIA_ASSOC              = 0x4000;

    /* PS Fill type for GpiCreatePS */
//    public static final int GPIM_AREAEXCL           = 0x8000;

    /* various flags for GpiCreateBitmap */
    public static final int CBM_INIT                = 0x0004;
    public static final int BCA_UNCOMP              = 0;
//    public static final int BCA_HUFFMAN1D           = 3;
//    public static final int BCA_RLE4                = 2;
//    public static final int BCA_RLE8                = 1;
//    public static final int BCA_RLE24               = 4;
    public static final int BRU_METRIC              = 0;
    public static final int BRA_BOTTOMUP            = 0;
    public static final int BRH_NOTHALFTONED        = 0;
//    public static final int BRH_ERRORDIFFUSION      = 1;
//    public static final int BRH_PANDA               = 2;
//    public static final int BRH_SUPERCIRCLE         = 3;
//    public static final int BCE_PALETTE             = -1;
    public static final int BCE_RGB                 = 0;

    /* raster operations defined for GpiBitBlt */
    public static final int ROP_SRCCOPY             = 0x00CC;
    public static final int ROP_SRCPAINT            = 0x00EE;
    public static final int ROP_SRCAND              = 0x0088;
    public static final int ROP_SRCINVERT           = 0x0066;
//    public static final int ROP_SRCERASE            = 0x0044;
//    public static final int ROP_NOTSRCCOPY          = 0x0033;
//    public static final int ROP_NOTSRCERASE         = 0x0011;
//    public static final int ROP_MERGECOPY           = 0x00C0;
//    public static final int ROP_MERGEPAINT          = 0x00BB;
//    public static final int ROP_PATCOPY             = 0x00F0;
//    public static final int ROP_PATPAINT            = 0x00FB;
//    public static final int ROP_PATINVERT           = 0x005A;
    public static final int ROP_DSTINVERT           = 0x0055;
    public static final int ROP_ZERO                = 0x0000;
    public static final int ROP_ONE                 = 0x00FF;

    /* Blt options for GpiBitBlt */
    public static final int BBO_OR                  = 0;
    public static final int BBO_AND                 = 1;
    public static final int BBO_IGNORE              = 2;
    public static final int BBO_PAL_COLORS          = 4;
    public static final int BBO_NO_COLOR_INFO       = 8;

    /* WinDrawPointer() constants */
    public static final int DP_NORMAL               = 0x0000;
//    public static final int DP_HALFTONED            = 0x0001;
//    public static final int DP_INVERTED             = 0x0002;
    public static final int DP_MINI                 = 0x0004;

    /* WinScrollWindow() flags */
    public static final int SW_SCROLLCHILDREN       = 0x0001;
    public static final int SW_INVALIDATERGN        = 0x0002;

    /* error return code for GpiSetRegion */
    public static final int HRGN_ERROR              = -1;

    /* character text alignments */
//    public static final int TA_NORMAL_HORIZ         = 0x0001;
//    public static final int TA_LEFT                 = 0x0002;
//    public static final int TA_CENTER               = 0x0003;
//    public static final int TA_RIGHT                = 0x0004;
    public static final int TA_STANDARD_HORIZ       = 0x0005;
//    public static final int TA_NORMAL_VERT          = 0x0100;
    public static final int TA_TOP                  = 0x0200;
//    public static final int TA_HALF                 = 0x0300;
//    public static final int TA_BASE                 = 0x0400;
//    public static final int TA_BOTTOM               = 0x0500;
//    public static final int TA_STANDARD_VERT        = 0x0600;

    public static final int FACESIZE                = 32;    

//@@TODO (dmik): remove!    
    /* FACENAMEDESC options for GpiQueryFaceString */
   public static final int FWEIGHT_DONT_CARE           = 0;
   public static final int FWEIGHT_ULTRA_LIGHT         = 1;
   public static final int FWEIGHT_EXTRA_LIGHT         = 2;
   public static final int FWEIGHT_LIGHT               = 3;
   public static final int FWEIGHT_SEMI_LIGHT          = 4;
   public static final int FWEIGHT_NORMAL              = 5;
   public static final int FWEIGHT_SEMI_BOLD           = 6;
   public static final int FWEIGHT_BOLD                = 7;
   public static final int FWEIGHT_EXTRA_BOLD          = 8;
   public static final int FWEIGHT_ULTRA_BOLD          = 9;
   public static final int FWIDTH_DONT_CARE            = 0;
   public static final int FWIDTH_ULTRA_CONDENSED      = 1;
   public static final int FWIDTH_EXTRA_CONDENSED      = 2;
   public static final int FWIDTH_CONDENSED            = 3;
   public static final int FWIDTH_SEMI_CONDENSED       = 4;
   public static final int FWIDTH_NORMAL               = 5;
   public static final int FWIDTH_SEMI_EXPANDED        = 6;
   public static final int FWIDTH_EXPANDED             = 7;
   public static final int FWIDTH_EXTRA_EXPANDED       = 8;
   public static final int FWIDTH_ULTRA_EXPANDED       = 9;
   public static final int FTYPE_ITALIC                = 0x0001;
   public static final int FTYPE_ITALIC_DONT_CARE      = 0x0002;
   public static final int FTYPE_OBLIQUE               = 0x0004;
   public static final int FTYPE_OBLIQUE_DONT_CARE     = 0x0008;
   public static final int FTYPE_ROUNDED               = 0x0010;
   public static final int FTYPE_ROUNDED_DONT_CARE     = 0x0020;

    /* values for fields of FATTRS structure */
    public static final int FATTR_SEL_ITALIC            = 0x0001;
    public static final int FATTR_SEL_UNDERSCORE        = 0x0002;
    public static final int FATTR_SEL_OUTLINE           = 0x0008;
    public static final int FATTR_SEL_STRIKEOUT         = 0x0010;
    public static final int FATTR_SEL_BOLD              = 0x0020;
    public static final int FATTR_SEL_MUST_COLOR        = 0x0100;
    public static final int FATTR_SEL_MUST_MIXEDMODES   = 0x0200;
    public static final int FATTR_SEL_MUST_HOLLOW       = 0x0400;
    public static final int FATTR_TYPE_KERNING          = 0x0004;
    public static final int FATTR_TYPE_MBCS             = 0x0008;
    public static final int FATTR_TYPE_DBCS             = 0x0010;
    public static final int FATTR_TYPE_ANTIALIASED      = 0x0020;
    public static final int FATTR_FONTUSE_NOMIX         = 0x0002;
    public static final int FATTR_FONTUSE_OUTLINE       = 0x0004;
//    public static final int FATTR_FONTUSE_TRANSFORMABLE = 0x0008;

    /* values of fsType field of FONTMETRICS structure */
//    public static final int FM_TYPE_FIXED           = 0x0001;
//    public static final int FM_TYPE_LICENSED        = 0x0002;
//    public static final int FM_TYPE_KERNING         = 0x0004;
//    public static final int FM_TYPE_DBCS            = 0x0010;
//    public static final int FM_TYPE_MBCS            = 0x0018;
//    public static final int FM_TYPE_UNICODE         = 0x0040;
//    public static final int FM_TYPE_64K             = 0x8000;
//    public static final int FM_TYPE_ATOMS           = 0x4000;
    public static final int FM_TYPE_FAMTRUNC        = 0x2000;
    public static final int FM_TYPE_FACETRUNC       = 0x1000;
    public static final int FM_DEFN_OUTLINE         = 0x0001;
//    public static final int FM_DEFN_IFI             = 0x0002;
//    public static final int FM_DEFN_WIN             = 0x0004;
//    public static final int FM_DEFN_GENERIC         = 0x8000;
//    public static final int FM_DEFN_LATIN1          = 0x0010;
//    public static final int FM_DEFN_PC              = 0x0020;
//    public static final int FM_DEFN_LATIN2          = 0x0040;
//    public static final int FM_DEFN_CYRILLIC        = 0x0080;
//    public static final int FM_DEFN_HEBREW          = 0x0100;
//    public static final int FM_DEFN_GREEK           = 0x0200;
//    public static final int FM_DEFN_ARABIC          = 0x0400;
//    public static final int FM_DEFN_UGLEXT          = 0x0800;
//    public static final int FM_DEFN_KANA            = 0x1000;
//    public static final int FM_DEFN_THAI            = 0x2000;
//    public static final int FM_DEFN_UGL383          = 0x0070;
//    public static final int FM_DEFN_UGL504          = 0x00F0;
//    public static final int FM_DEFN_UGL767          = 0x0FF0;
//    public static final int FM_DEFN_UGL1105         = 0x3FF0;
    public static final int FM_SEL_ITALIC           = 0x0001;
//    public static final int FM_SEL_UNDERSCORE       = 0x0002;
//    public static final int FM_SEL_NEGATIVE         = 0x0004;
//    public static final int FM_SEL_OUTLINE          = 0x0008;
//    public static final int FM_SEL_STRIKEOUT        = 0x0010;
    public static final int FM_SEL_BOLD             = 0x0020;
//    public static final int FM_SEL_ISO9241_TESTED   = 0x0040;
//    public static final int FM_SEL_JAPAN            = 0x1000;
//    public static final int FM_SEL_TAIWAN           = 0x2000;
//    public static final int FM_SEL_CHINA            = 0x4000;
//    public static final int FM_SEL_KOREA            = 0x8000;
//    public static final int FM_SEL_DBCSMASK         = 0xF000;
    public static final int FM_CAP_NOMIX            = 0x0001;
//    public static final int FM_CAP_NO_COLOR         = 0x0002;
//    public static final int FM_CAP_NO_MIXEDMODES    = 0x0004;
//    public static final int FM_CAP_NO_HOLLOW        = 0x0008;

/* actions for GpiQueryFontAction */
    public static final int QFA_PUBLIC      = 1;
//    public static final int QFA_PRIVATE     = 2;
//    public static final int QFA_ERROR       = GPI_ALTERROR;

    /* options for GpiQueryFonts */
    public static final int QF_PUBLIC       = 0x0001;
//    public static final int QF_PRIVATE      = 0x0002;
//    public static final int QF_NO_GENERIC   = 0x0004;
//    public static final int QF_NO_DEVICE    = 0x0008;

    /* return codes from GpiCreateLogFont */
//    public static final int FONT_DEFAULT    = 1;
//    public static final int FONT_MATCH      = 2;

    /* array indices for GpiQueryTextBox */
    public static final int TXTBOX_TOPLEFT      = 0;
    public static final int TXTBOX_BOTTOMLEFT   = 1;
    public static final int TXTBOX_TOPRIGHT     = 2;
    public static final int TXTBOX_BOTTOMRIGHT  = 3;
    public static final int TXTBOX_CONCAT       = 4;
    public static final int TXTBOX_COUNT        = 5;

    /* formatting options for GpiCharStringPosAt */
//    public static final int CHS_OPAQUE          = 0x0001;
//    public static final int CHS_VECTOR          = 0x0002;
//    public static final int CHS_LEAVEPOS        = 0x0008;
//    public static final int CHS_CLIP            = 0x0010;
    public static final int CHS_UNDERSCORE      = 0x0200;
//    public static final int CHS_STRIKEOUT       = 0x0400;
    
    /* WinDrawText()/WinDrawTabbedText() codes */
    public static final int DT_LEFT             = 0x00000000;
    public static final int DT_QUERYEXTENT      = 0x00000002;
    public static final int DT_UNDERSCORE       = 0x00000010;
    public static final int DT_STRIKEOUT        = 0x00000020;
    public static final int DT_TEXTATTRS        = 0x00000040;
    public static final int DT_EXTERNALLEADING  = 0x00000080;
    public static final int DT_CENTER           = 0x00000100;
    public static final int DT_RIGHT            = 0x00000200;
    public static final int DT_TOP              = 0x00000000;
    public static final int DT_VCENTER          = 0x00000400;
    public static final int DT_BOTTOM           = 0x00000800;
    public static final int DT_HALFTONE         = 0x00001000;
    public static final int DT_MNEMONIC         = 0x00002000;
    public static final int DT_WORDBREAK        = 0x00004000;
    public static final int DT_ERASERECT        = 0x00008000;

    /* bundle codes for GpiSetAttributes and GpiQueryAttributes */
    public static final int PRIM_LINE           = 1;
    public static final int PRIM_CHAR           = 2;
    public static final int PRIM_MARKER         = 3;
    public static final int PRIM_AREA           = 4;
    public static final int PRIM_IMAGE          = 5;

    /* line bundle mask bits */
    public static final int LBB_COLOR           = 0x0001;
    public static final int LBB_BACK_COLOR      = 0x0002;
    public static final int LBB_MIX_MODE        = 0x0004;
    public static final int LBB_BACK_MIX_MODE   = 0x0008;
    public static final int LBB_WIDTH           = 0x0010;
    public static final int LBB_GEOM_WIDTH      = 0x0020;
    public static final int LBB_TYPE            = 0x0040;
    public static final int LBB_END             = 0x0080;
    public static final int LBB_JOIN            = 0x0100;

    /* ini file handles */
//    public static final int HINI_PROFILE        = 0;
    public static final int HINI_USERPROFILE    = -1;
//    public static final int HINI_SYSTEMPROFILE  = -2;
    
    /* Message box types */
    public static final int MB_OK               = 0x0000;
    public static final int MB_OKCANCEL         = 0x0001;
    public static final int MB_RETRYCANCEL      = 0x0002;
    public static final int MB_ABORTRETRYIGNORE = 0x0003;
    public static final int MB_YESNO            = 0x0004;
    public static final int MB_YESNOCANCEL      = 0x0005;
    public static final int MB_CANCEL           = 0x0006;
    public static final int MB_ENTER            = 0x0007;
    public static final int MB_ENTERCANCEL      = 0x0008;
    public static final int MB_NOICON           = 0x0000;
    public static final int MB_CUANOTIFICATION  = 0x0000;
    public static final int MB_ICONQUESTION     = 0x0010;
    public static final int MB_ICONEXCLAMATION  = 0x0020;
    public static final int MB_CUAWARNING       = 0x0020;
    public static final int MB_ICONASTERISK     = 0x0030;
    public static final int MB_ICONHAND         = 0x0040;
    public static final int MB_CUACRITICAL      = 0x0040;
    public static final int MB_QUERY            = MB_ICONQUESTION;
    public static final int MB_WARNING          = MB_CUAWARNING;
    public static final int MB_INFORMATION      = MB_ICONASTERISK;
    public static final int MB_CRITICAL         = MB_CUACRITICAL;
    public static final int MB_ERROR            = MB_CRITICAL;
//    public static final int MB_CUSTOMICON       = 0x0080;
//    public static final int MB_DEFBUTTON1       = 0x0000;
//    public static final int MB_DEFBUTTON2       = 0x0100;
//    public static final int MB_DEFBUTTON3       = 0x0200;
    public static final int MB_APPLMODAL        = 0x0000;
    public static final int MB_SYSTEMMODAL      = 0x1000;
//    public static final int MB_HELP             = 0x2000;
    public static final int MB_MOVEABLE         = 0x4000;
//    public static final int MB_NONMODAL         = 0x8000;

   /* Message box return codes */
    public static final int MBID_OK             = 1;
    public static final int MBID_CANCEL         = 2;
    public static final int MBID_ABORT          = 3;
    public static final int MBID_RETRY          = 4;
    public static final int MBID_IGNORE         = 5;
    public static final int MBID_YES            = 6;
    public static final int MBID_NO             = 7;
    public static final int MBID_HELP           = 8;
    public static final int MBID_ENTER          = 9;
    public static final int MBID_ERROR          = 0xffff;
    
   /**********************************************************************/
   /* Font Dialog Style Flags                                            */
   /**********************************************************************/
   public static final int FNTS_CENTER              = 0x00000001; /* Center in owner dialog*/
   public static final int FNTS_CUSTOM             = 0x00000002; /* Use custom template   */
   public static final int FNTS_OWNERDRAWPREVIEW    = 0x00000004; /* Allow app to draw     */
   public static final int FNTS_HELPBUTTON          = 0x00000008; /* Display Help button   */
   public static final int FNTS_APPLYBUTTON         = 0x00000010; /* Display Apply button  */
   public static final int FNTS_RESETBUTTON         = 0x00000020; /* Display Reset button  */
   public static final int FNTS_MODELESS            = 0x00000040; /* Make dialog modeless  */
   public static final int FNTS_INITFROMFATTRS      = 0x00000080; /* Initialize from FATTRs*/
   public static final int FNTS_BITMAPONLY          = 0x00000100; /* Only allow bitmap font*/
   public static final int FNTS_VECTORONLY          = 0x00000200; /* Only allow vector font*/
   public static final int FNTS_FIXEDWIDTHONLY      = 0x00000400; /* Only allow monospaced */
   public static final int FNTS_PROPORTIONALONLY    = 0x00000800; /* Only proportional font*/
   public static final int FNTS_NOSYNTHESIZEDFONTS  = 0x00001000; /* Don't synthesize fonts*/
   public static final int FNTS_NATIONAL_LANGUAGE   = 0x80000000; /* Reserved: bidirectio- */

   /**********************************************************************/
   /* Font Dialog Flags                                                  */
   /**********************************************************************/
   public static final int FNTF_NOVIEWSCREENFONTS      = 1;
   public static final int FNTF_NOVIEWPRINTERFONTS     = 2;
   public static final int FNTF_SCREENFONTSELECTED     = 4;
   public static final int FNTF_PRINTERFONTSELECTED    = 8;
   
   /**********************************************************************/
   /* Filter List message string identifiers                             */
   /**********************************************************************/
   public static final int FNTI_BITMAPFONT         = 0x0001;
   public static final int FNTI_VECTORFONT         = 0x0002;
   public static final int FNTI_FIXEDWIDTHFONT     = 0x0004;
   public static final int FNTI_PROPORTIONALFONT   = 0x0008;
   public static final int FNTI_SYNTHESIZED        = 0x0010;
   public static final int FNTI_DEFAULTLIST        = 0x0020;
   public static final int FNTI_FAMILYNAME         =0x0100;
   public static final int FNTI_STYLENAME          = 0x0200;
   public static final int FNTI_POINTSIZE          = 0x0400;
   
   /**********************************************************************/
   /* Error Return Codes from dialog (self defining)                     */
   /**********************************************************************/
   public static final int FNTS_SUCCESSFUL                           = 0;
   public static final int FNTS_ERR_INVALID_DIALOG                   = 3;
   public static final int FNTS_ERR_ALLOC_SHARED_MEM                 = 4;
   public static final int FNTS_ERR_INVALID_PARM                     = 5;
   public static final int FNTS_ERR_OUT_OF_MEMORY                    = 7;
   public static final int FNTS_ERR_INVALID_VERSION                 = 10;
   public static final int FNTS_ERR_DIALOG_LOAD_ERROR               = 12;
   
  /* default color table indices */

   public static final int CLR_FALSE			= -5;
   public static final int CLR_TRUE			= -4;   
   public static final int CLR_ERROR			= -255;
   public static final int CLR_DEFAULT			= -3;
   public static final int CLR_WHITE			= -2;
   public static final int CLR_BLACK			= -1;
   public static final int CLR_BACKGROUND		= 0;
   public static final int CLR_BLUE			= 1;
   public static final int CLR_RED				= 2;
   public static final int CLR_PINK			= 3;
   public static final int CLR_GREEN			= 4;
   public static final int CLR_CYAN			= 5;
   public static final int CLR_YELLOW			= 6;
   public static final int CLR_NEUTRAL			= 7;
   public static final int CLR_DARKGRAY		= 8;
   public static final int CLR_DARKBLUE		= 9;
   public static final int CLR_DARKRED			= 10;
   public static final int CLR_DARKPINK		= 11;
   public static final int CLR_DARKGREEN		= 12;
   public static final int CLR_DARKCYAN		= 13;
   public static final int CLR_BROWN			= 14;
   public static final int CLR_PALEGRAY		= 15;

   public static final int DID_OK		=1;
   public static final int DID_CANCEL	=2;
   public static final int DID_ERROR	=0xFFFF;

   public static final int CCHMAXPATH          = 260;

    /* WinFileDlg constants */
   public static final int FDS_CENTER          = 0x00000001; /* Center within owner wnd */
   public static final int FDS_CUSTOM          = 0x00000002; /* Use custom user template */
   public static final int FDS_FILTERUNION     = 0x00000004; /* Use union of filters */
   public static final int FDS_HELPBUTTON      = 0x00000008; /* Display Help button */
   public static final int FDS_APPLYBUTTON     = 0x00000010; /* Display Apply button */
   public static final int FDS_PRELOAD_VOLINFO = 0x00000020; /* Preload volume info */
   public static final int FDS_MODELESS        = 0x00000040; /* Make dialog modeless */
//   public static final int FDS_INCLUDE_EAS     = 0x00000080; /* Always load EA info */
   public static final int FDS_OPEN_DIALOG     = 0x00000100; /* Select Open dialog */
   public static final int FDS_SAVEAS_DIALOG   = 0x00000200; /* Select SaveAs dialog */
   public static final int FDS_MULTIPLESEL     = 0x00000400; /* Enable multiple selection */
   public static final int FDS_ENABLEFILELB    = 0x00000800; /* Enable SaveAs Listbox */
   public static final int FDS_ERR_DEALLOCATE_MEMORY        =  1;
//   public static final int FDS_ERR_FILTER_TRUNC             =  2;
   public static final int FDS_ERR_INVALID_DIALOG           =  3;
   public static final int FDS_ERR_INVALID_DRIVE            =  4;
   public static final int FDS_ERR_INVALID_FILTER           =  5;
   public static final int FDS_ERR_INVALID_PATHFILE         =  6;
//   public static final int FDS_ERR_OUT_OF_MEMORY            =  7;
   public static final int FDS_ERR_PATH_TOO_LONG            =  8;
   public static final int FDS_ERR_TOO_MANY_FILE_TYPES      =  9;
//   public static final int FDS_ERR_INVALID_VERSION          = 10;
//   public static final int FDS_ERR_INVALID_CUSTOM_HANDLE    = 11;
//   public static final int FDS_ERR_DIALOG_LOAD_ERROR        = 12;
//   public static final int FDS_ERR_DRIVE_ERROR              = 13;
   
    /* Static control styles: */
   public static final int SS_TEXT                    =0x0001;
   public static final int SS_GROUPBOX               = 0x0002;
   public static final int SS_ICON                    =0x0003;
   public static final int SS_BITMAP                  =0x0004;
   public static final int SS_FGNDRECT                =0x0005;
   public static final int SS_HALFTONERECT            =0x0006;
   public static final int SS_BKGNDRECT               =0x0007;
   public static final int SS_FGNDFRAME               =0x0008;
   public static final int SS_HALFTONEFRAME           =0x0009;
   public static final int SS_BKGNDFRAME              =0x000a;
   public static final int SS_SYSICON                 =0x000b;
   public static final int SS_AUTOSIZE         =0x0040;
   /* Static control messages */   
   public static final int SM_SETHANDLE               =0x0100;
   public static final int SM_QUERYHANDLE             =0x0101;

      /*** Scroll Bar controls Subsection */
   
   /* Scroll Bar styles */
   
   public static final int SBS_HORZ                   =0;
   public static final int SBS_VERT                   =1;
   public static final int SBS_THUMBSIZE              =2;
   public static final int SBS_AUTOTRACK              =4;
   public static final int SBS_AUTOSIZE        =0x2000;
   
   
   
   /* Scroll Bar messages */
   
   public static final int SBM_SETSCROLLBAR          = 0x01a0;
   public static final int SBM_SETPOS                 =0x01a1;
   public static final int SBM_QUERYPOS               =0x01a2;
   public static final int SBM_QUERYRANGE             =0x01a3;
   public static final int SBM_SETTHUMBSIZE           =0x01a6;
   
   /* Scroll Bar Commands */
   
   public static final int SB_LINEUP                  =1;
   public static final int SB_LINEDOWN               = 2;
   public static final int SB_LINELEFT                =1;
   public static final int SB_LINERIGHT               =2;
   public static final int SB_PAGEUP                  =3;
   public static final int SB_PAGEDOWN                =4;
   public static final int SB_PAGELEFT                =3;
   public static final int SB_PAGERIGHT               =4;
   public static final int SB_SLIDERTRACK             =5;
   public static final int SB_SLIDERPOSITION          =6;
   public static final int SB_ENDSCROLL               =7;
   
   /*************************************************************************
   * MLE Window styles ( in addition to WS_* )
   *************************************************************************/

   public static final int  MLS_WORDWRAP               = 0x00000001;
   public static final int  MLS_BORDER                 = 0x00000002;
   public static final int  MLS_VSCROLL                = 0x00000004;
   public static final int  MLS_HSCROLL                = 0x00000008;
   public static final int  MLS_READONLY               = 0x00000010;
   public static final int  MLS_IGNORETAB              = 0x00000020;
   public static final int  MLS_DISABLEUNDO            = 0x00000040;
   public static final int  MLS_LIMITVSCROLL           = 0x00000080;

   /*************************************************************************
   * MLE colors - MLM_QUERY(TEXT/BACK)COLOR, MLM_SET(TEXT/BACK)COLOR
   * MLE_INDEX For version 3, or lower, of the OS/2 operating system the default is
   * MLE_INDEX=0; MLE does not support dithered colors
   * MLE_RGB For versions, higher than version 3, of the OS/2 operating system colors default
   * to the RGB mode.
   *************************************************************************/
   public static final int MLE_INDEX  = 0;
   public static final int MLE_RGB    = 1;

   /*************************************************************************
   * MLE messages - MLM from 0x01b0 to 0x01de; MLN from 0x0001 to 0x000f
   *************************************************************************/
   /* formatting messages */
   public static final int MLM_SETTEXTLIMIT           = 0x01b0;
   public static final int MLM_QUERYTEXTLIMIT         = 0x01b1;
   public static final int MLM_SETFORMATRECT          = 0x01b2;
   public static final int MLM_QUERYFORMATRECT        = 0x01b3;
   public static final int MLM_SETWRAP                = 0x01b4;
   public static final int MLM_QUERYWRAP              = 0x01b5;
   public static final int MLM_SETTABSTOP             = 0x01b6;
   public static final int MLM_QUERYTABSTOP           = 0x01b7;
   public static final int MLM_SETREADONLY            = 0x01b8;
   public static final int MLM_QUERYREADONLY          = 0x01b9;
   
   /* text content manipulation and queries messages */
   public static final int MLM_QUERYCHANGED           = 0x01ba;
   public static final int MLM_SETCHANGED             = 0x01bb;
   public static final int MLM_QUERYLINECOUNT         = 0x01bc;
   public static final int MLM_CHARFROMLINE           = 0x01bd;
   public static final int MLM_LINEFROMCHAR           = 0x01be;
   public static final int MLM_QUERYLINELENGTH        = 0x01bf;
   public static final int MLM_QUERYTEXTLENGTH        = 0x01c0;
   
   /* text import and export messages */
   public static final int MLM_FORMAT                 = 0x01c1;
   public static final int MLM_SETIMPORTEXPORT        = 0x01c2;
   public static final int MLM_IMPORT                 = 0x01c3;
   public static final int MLM_EXPORT                 = 0x01c4;
   public static final int MLM_DELETE                 = 0x01c6;
   public static final int MLM_QUERYFORMATLINELENGTH  = 0x01c7;
   public static final int MLM_QUERYFORMATTEXTLENGTH  = 0x01c8;
   public static final int MLM_INSERT                 = 0x01c9;
   
   /* selection messages */
   public static final int MLM_SETSEL                 = 0x01ca;
   public static final int MLM_QUERYSEL               = 0x01cb;
   public static final int MLM_QUERYSELTEXT           = 0x01cc;
   
   /* undo and redo messages */
   public static final int MLM_QUERYUNDO              = 0x01cd;
   public static final int MLM_UNDO                   = 0x01ce;
   public static final int MLM_RESETUNDO              = 0x01cf;
   
   /* text attributes messages */
   public static final int MLM_QUERYFONT              = 0x01d0;
   public static final int MLM_SETFONT                = 0x01d1;
   public static final int MLM_SETTEXTCOLOR           = 0x01d2;
   public static final int MLM_QUERYTEXTCOLOR         = 0x01d3;
   public static final int MLM_SETBACKCOLOR           = 0x01d4;
   public static final int MLM_QUERYBACKCOLOR         = 0x01d5;
   
   /* scrolling messages */
   public static final int MLM_QUERYFIRSTCHAR         = 0x01d6;
   public static final int MLM_SETFIRSTCHAR           = 0x01d7;
   
   /* clipboard messages */
   public static final int MLM_CUT                    = 0x01d8;
   public static final int MLM_COPY                   = 0x01d9;
   public static final int MLM_PASTE                  = 0x01da;
   public static final int MLM_CLEAR                  = 0x01db;
   
   /* display manipulation messages */
   public static final int MLM_ENABLEREFRESH          = 0x01dc;
   public static final int MLM_DISABLEREFRESH         = 0x01dd;
   
   /* search message */
   public static final int MLM_SEARCH                 = 0x01de;
   public static final int MLM_QUERYIMPORTEXPORT      = 0x01df;
   
   /* notification messages */
   public static final int MLN_OVERFLOW               = 0x0001;
   public static final int MLN_PIXHORZOVERFLOW        = 0x0002;
   public static final int MLN_PIXVERTOVERFLOW        = 0x0003;
   public static final int MLN_TEXTOVERFLOW           = 0x0004;
   public static final int MLN_VSCROLL                = 0x0005;
   public static final int MLN_HSCROLL                = 0x0006;
   public static final int MLN_CHANGE                 = 0x0007;
   public static final int MLN_SETFOCUS               = 0x0008;
   public static final int MLN_KILLFOCUS              = 0x0009;
   public static final int MLN_MARGIN                 = 0x000a;
   public static final int MLN_SEARCHPAUSE            = 0x000b;
   public static final int MLN_MEMERROR               = 0x000c;
   public static final int MLN_UNDOOVERFLOW           = 0x000d;
   public static final int MLN_CLPBDFAIL              = 0x000f;
   
   /*************************************************************************
   * MLM_QUERYSELECTION flags
   *************************************************************************/
   public static final int MLFQS_MINMAXSEL           =  0;
   public static final int MLFQS_MINSEL               = 1;
   public static final int MLFQS_MAXSEL               = 2;
   public static final int MLFQS_ANCHORSEL            = 3;
   public static final int MLFQS_CURSORSEL            = 4;

   /*************************************************************************
   * afFormatFlags mask
   *************************************************************************/
   public static final int MLFFMTRECT_LIMITHORZ       = 0x00000001;
   public static final int MLFFMTRECT_LIMITVERT       = 0x00000002;
   public static final int MLFFMTRECT_MATCHWINDOW     = 0x00000004;
   public static final int MLFFMTRECT_FORMATRECT      = 0x00000007;

   /* Entry field  styles */
   
   public static final int ES_LEFT                    = 0x00000000;
   public static final int ES_CENTER                  = 0x00000001;
   public static final int ES_RIGHT                   = 0x00000002;
   public static final int ES_AUTOSCROLL              = 0x00000004;
   public static final int ES_MARGIN                  = 0x00000008;
   public static final int ES_AUTOTAB                 = 0x00000010;
   public static final int ES_READONLY                = 0x00000020;
   public static final int ES_COMMAND                 = 0x00000040;
   public static final int ES_UNREADABLE              = 0x00000080;
   public static final int ES_AUTOSIZE                = 0x00000200;
   public static final int ES_ANY                     = 0x00000000;
   public static final int ES_SBCS                    = 0x00001000;
   public static final int ES_DBCS                    = 0x00002000;
   public static final int ES_MIXED                   = 0x00003000;
   
   /* Entry Field  messages */
   
   public static final int EM_QUERYCHANGED            = 0x0140;
   public static final int EM_QUERYSEL                = 0x0141;
   public static final int EM_SETSEL                  = 0x0142;
   public static final int EM_SETTEXTLIMIT            = 0x0143;
   public static final int EM_CUT                     = 0x0144;
   public static final int EM_COPY                    = 0x0145;
   public static final int EM_CLEAR                   = 0x0146;
   public static final int EM_PASTE                   = 0x0147;
   public static final int EM_QUERYFIRSTCHAR          = 0x0148;
   public static final int EM_SETFIRSTCHAR            = 0x0149;
   public static final int EM_QUERYREADONLY           = 0x014a;
   public static final int EM_SETREADONLY             = 0x014b;
   public static final int EM_SETINSERTMODE           = 0x014c;
   
   /* Entry Field notification messages */
   
   public static final int EN_SETFOCUS                = 0x0001;
   public static final int EN_KILLFOCUS               = 0x0002;
   public static final int EN_CHANGE                  = 0x0004;
   public static final int EN_SCROLL                  = 0x0008;
  public static final int EN_MEMERROR                = 0x0010;
  public static final int EN_OVERFLOW                = 0x0020;
  public static final int EN_INSERTMODETOGGLE        = 0x0040;

     /* List box styles */
   
   public static final int LS_MULTIPLESEL             = 0x00000001;
   public static final int LS_OWNERDRAW               = 0x00000002;
   public static final int LS_NOADJUSTPOS             = 0x00000004;
   public static final int LS_HORZSCROLL              = 0x00000008;
   public static final int LS_EXTENDEDSEL             = 0x00000010;
   
   /* List box messages */
   
   public static final int LM_QUERYITEMCOUNT         = 0x0160;
   public static final int LM_INSERTITEM              =0x0161;
   public static final int LM_SETTOPINDEX             = 0x0162;
   public static final int LM_DELETEITEM              = 0x0163;
   public static final int LM_SELECTITEM              = 0x0164;
   public static final int LM_QUERYSELECTION          = 0x0165;
   public static final int LM_SETITEMTEXT             = 0x0166;
   public static final int LM_QUERYITEMTEXTLENGTH     = 0x0167;
   public static final int LM_QUERYITEMTEXT           = 0x0168;
   
   public static final int LM_SETITEMHANDLE           = 0x0169;
   public static final int LM_QUERYITEMHANDLE         = 0x016a;
   public static final int LM_SEARCHSTRING            = 0x016b;
   public static final int LM_SETITEMHEIGHT            = 0x016c;
   public static final int LM_QUERYTOPINDEX           = 0x016d;
   public static final int LM_DELETEALL               = 0x016e;
   public static final int LM_INSERTMULTITEMS         = 0x016f;
   public static final int LM_SETITEMWIDTH            = 0x0660;
   
   
   /* List box constants */
   
   public static final int LIT_CURSOR          = -4;
   public static final int LIT_ERROR                  = -3;
   public static final int LIT_MEMERROR               = -2;
   public static final int LIT_NONE                   = -1;
   public static final int LIT_FIRST                  = -1;
   
   /* For LM_INSERTITEM msg */
   
   public static final int LIT_END                    = -1;
   public static final int LIT_SORTASCENDING          = -2;
   public static final int LIT_SORTDESCENDING         = -3;
   
   /* For LM_SEARCHSTRING msg */
   
   public static final int LSS_SUBSTRING              = 0x0001;
   public static final int LSS_PREFIX                 = 0x0002;
   public static final int LSS_CASESENSITIVE          = 0x0004;

  /* List box notification messages */
   
   public static final int LN_SELECT                  =1;
   public static final int LN_SETFOCUS              =2;
   public static final int LN_KILLFOCUS              =3;
   public static final int LN_SCROLL                  =4;
   public static final int LN_ENTER                   =5;
   
   /*  combo box styles*/
   public static final int CBS_SIMPLE                 = 0x0001;
   public static final int CBS_DROPDOWN            = 0x0002;
   public static final int CBS_DROPDOWNLIST           = 0x0004;

   /* IDs of combobox entry field and listbox.*/
   public static final int CBID_LIST                   = 0x029A;
   public static final int CBID_EDIT                   = 0x029B;
   
   public static final int CBM_SHOWLIST               = 0x0170;
   public static final int CBM_HILITE                 = 0x0171;
   public static final int CBM_ISLISTSHOWING          = 0x0172;
   
   public static final int CBN_EFCHANGE               = 1;
   public static final int CBN_EFSCROLL              = 2;
   public static final int CBN_MEMERROR               = 3;
   public static final int CBN_LBSELECT               = 4;
   public static final int CBN_LBSCROLL               = 5;
   public static final int CBN_SHOWLIST               = 6;
   public static final int CBN_ENTER                  = 7;
   /**********************************************************************/
   /*                                                                    */
   /*                          N O T E B O O K                           */
   /*                                                                    */
   /**********************************************************************/
   
   /**********************************************************************/
   /* Message ids                                                        */
   /**********************************************************************/
   public static final int BKM_CALCPAGERECT                 = 0x0353;  /* Calc book/page rectangle  */
   public static final int BKM_DELETEPAGE                       = 0x0354;  /* Delete page(s)            */
   public static final int BKM_INSERTPAGE                        = 0x0355;  /* Insert page               */
   public static final int BKM_INVALIDATETABS                 = 0x0356;  /* Invalidate tab area       */
   public static final int BKM_TURNTOPAGE                      = 0x0357;  /* Turn to page              */
   public static final int BKM_QUERYPAGECOUNT           = 0x0358;  /* Query number of pages     */
   public static final int BKM_QUERYPAGEID                      =0x0359;  /* Query page identifier     */
   public static final int BKM_QUERYPAGEDATA                = 0x035a;  /* Query page user data      */
   public static final int BKM_QUERYPAGEWINDOWHWND  = 0x035b;  /* Query page window handle  */
   public static final int BKM_QUERYTABBITMAP       = 0x035c;  /* Query tab bitmap handle   */
   public static final int BKM_QUERYTABTEXT         = 0x035d;  /* Query tab text pointer    */
   public static final int BKM_SETDIMENSIONS        = 0x035e;  /* Set tab/dogear dimensions */
   public static final int BKM_SETPAGEDATA          = 0x035f;  /* Set page user data        */
   public static final int BKM_SETPAGEWINDOWHWND    = 0x0360;  /* Set page window handle    */
   public static final int BKM_SETSTATUSLINETEXT    = 0x0361;  /* Set status line text      */
   public static final int BKM_SETTABBITMAP         = 0x0362;  /* Set tab bitmap            */
   public static final int BKM_SETTABTEXT           = 0x0363;  /* Set tab text              */
   public static final int BKM_SETNOTEBOOKCOLORS    = 0x0364;  /* Set Notebook colors       */
   public static final int BKM_QUERYPAGESTYLE       = 0x0365;  /* Query page style          */
   public static final int BKM_QUERYSTATUSLINETEXT  = 0x0366;  /* Query status line text    */
   public static final int BKM_SETPAGEINFO          = 0x0367;   /* Set page information     */
   public static final int BKM_QUERYPAGEINFO        = 0x0368;   /* Query page information   */
   public static final int BKM_SETTABCOLOR          = 0x0374;  /* Set book tab color        */
   public static final int BKM_SETNOTEBOOKBUTTONS   = 0x0375;  /* Set common pushbuttons    */
   
   public static final int BKN_PAGESELECTED         = 130;     /* New page selected by user */
   public static final int BKN_NEWPAGESIZE          = 131;     /* App page size changed     */
   public static final int BKN_HELP                 = 132;     /* Help notification         */
   public static final int BKN_PAGEDELETED          = 133;     /* Page deleted notification */
   public static final int BKN_PAGESELECTEDPENDING  = 134;     /* New page selected by user */
   
   /**********************************************************************/
   /* Page deletion flags (usDeleteFlag)                                 */
   /**********************************************************************/
   public static final int BKA_ALL                  = 0x0001;  /* all pages                 */
   public static final int BKA_SINGLE               = 0x0002;  /* single page               */
   public static final int BKA_TAB                  = 0x0004;  /* minor/major section       */
   
   /**********************************************************************/
   /* Page insertion/query order (usPageOrder, usQueryOrder)             */
   /**********************************************************************/
   public static final int BKA_LAST                 = 0x0002;  /* Insert/Query last page    */
   public static final int BKA_FIRST                = 0x0004;  /* Insert/Query first page   */
   public static final int BKA_NEXT                 = 0x0008;  /* Insert/Query after page   */
   public static final int BKA_PREV                 = 0x0010;  /* Insert/Query before page  */
   public static final int BKA_TOP                  = 0x0020;  /* Query topmost page        */
   
   /**********************************************************************/
   /* Notebook region types (usBookRegion, usType)                       */
   /**********************************************************************/
   public static final int BKA_MAJORTAB             = 0x0001;  /* Major Tab                 */
   public static final int BKA_MINORTAB             = 0x0002;  /* Minor Tab                 */
   public static final int BKA_PAGEBUTTON           = 0x0100;  /* Page Turning Button       */
   
   /**********************************************************************/
   /* Page insertion/query styles (usPageStyle,usQueryEnd)               */
   /**********************************************************************/
   public static final int BKA_STATUSTEXTON         = 0x0001;  /* status area text          */
   public static final int BKA_MAJOR                = 0x0040;  /* Major Tab                 */
   public static final int BKA_MINOR                = 0x0080;  /* Minor Tab                 */
   public static final int BKA_AUTOPAGESIZE         = 0x0100;  /* Page window position/size */
   public static final int BKA_END                  = 0x0200;  /* Query to end of book      */
   
   /**********************************************************************/
   /* Tab window contents (usTabDisplay)                                 */
   /**********************************************************************/
   public static final int BKA_TEXT                 = 0x0400;  /* text data                 */
   public static final int BKA_BITMAP               = 0x0800;  /* bitmap                    */
   
   /**********************************************************************/
   /* Tabbed dialog-specific constants                                   */
   /**********************************************************************/
   public static final int BKA_AUTOCOLOR            = (-1);    /* Automatically color tabs  */
   public static final int BKA_MAXBUTTONID          = 7999;    /* Max id for common buttons */
   
   /**********************************************************************/
   /* Notebook window styles (ulNotebookStyles)                          */
   /**********************************************************************/
   
   /**********************************************************************/
   /* Backpage Orientation                                               */
   /**********************************************************************/
   public static final int BKS_BACKPAGESBR          = 0x00000001;  /* Bottom right          */
   public static final int BKS_BACKPAGESBL          = 0x00000002;  /* Bottom left           */
   public static final int BKS_BACKPAGESTR          = 0x00000004;   /* Top right             */
   public static final int BKS_BACKPAGESTL          = 0x00000008;  /* Top left              */
   
   /**********************************************************************/
   /* Major Tab Side                                                     */
   /**********************************************************************/
   public static final int BKS_MAJORTABRIGHT        = 0x00000010;  /* Major tabs right      */
   public static final int BKS_MAJORTABLEFT         = 0x00000020;  /* Major tabs left       */
   public static final int BKS_MAJORTABTOP          = 0x00000040;  /* Major tabs top        */
   public static final int BKS_MAJORTABBOTTOM       = 0x00000080;  /* Major tabs bottom     */
   
   /**********************************************************************/
   /* Tab Type                                                           */
   /**********************************************************************/
   public static final int BKS_SQUARETABS           = 0x00000000;  /* Square edged tabs     */
   public static final int BKS_ROUNDEDTABS          = 0x00000100;  /* Round edged tabs      */
   public static final int BKS_POLYGONTABS          = 0x00000200;  /* Polygon edged tabs    */
   
   /**********************************************************************/
   /* Binding type                                                       */
   /**********************************************************************/
   public static final int BKS_SOLIDBIND            = 0x00000000;  /* Solid binding         */
   public static final int BKS_SPIRALBIND           = 0x00000400;  /* Spiral binding        */
   
   /**********************************************************************/
   /* Status line text justification                                     */
   /**********************************************************************/
   public static final int BKS_STATUSTEXTLEFT       = 0x00000000;  /* Left justify text     */
   public static final int BKS_STATUSTEXTRIGHT      = 0x00001000;  /* Right justify text    */
   public static final int BKS_STATUSTEXTCENTER     = 0x00002000;  /* Center text           */
   
   /**********************************************************************/
   /* Tab text justification                                             */
   /**********************************************************************/
   public static final int BKS_TABTEXTLEFT          = 0x00000000;  /* Left justify tab text */
   public static final int BKS_TABTEXTRIGHT         = 0x00004000;  /* Right justify tab text*/
   public static final int BKS_TABTEXTCENTER        = 0x00008000;  /* Center tab text       */
   
   /**********************************************************************/
   /* Tabbed dialog styles                                               */
   /**********************************************************************/
   public static final int BKS_TABBEDDIALOG         = 0x00000800;  /* Tabbed dialog         */
   public static final int BKS_BUTTONAREA           = 0x00000200;  /* Reserve space for     */
   
   /**********************************************************************/
   /* Notebook color presentation param attributes                       */
   /**********************************************************************/
   public static final int BKA_BACKGROUNDPAGECOLORINDEX  = 0x0001; /* Page Background       */
   public static final int BKA_BACKGROUNDPAGECOLOR       = 0x0002;
   public static final int BKA_BACKGROUNDMAJORCOLORINDEX = 0x0003; /* Major Tab Background  */
   public static final int BKA_BACKGROUNDMAJORCOLOR      = 0x0004;
   public static final int BKA_BACKGROUNDMINORCOLORINDEX = 0x0005; /* Minor Tab Background  */
   public static final int BKA_BACKGROUNDMINORCOLOR      = 0x0006;
   public static final int BKA_FOREGROUNDMAJORCOLORINDEX = 0x0007; /* Major Tab Text        */
   public static final int BKA_FOREGROUNDMAJORCOLOR      = 0x0008;
   public static final int BKA_FOREGROUNDMINORCOLORINDEX = 0x0009; /* Minor Tab Text        */
   public static final int BKA_FOREGROUNDMINORCOLOR      = 0x000A;

      /* Menu control styles */
   
   public static final int MS_ACTIONBAR               = 0x00000001;
   public static final int MS_TITLEBUTTON            =  0x00000002;
   public static final int MS_VERTICALFLIP            = 0x00000004;
   
   public static final int MS_CONDITIONALCASCADE      = 0x00000040;
           
   /* Menu control messages */
   
   public static final int MM_INSERTITEM              = 0x0180;
   public static final int MM_DELETEITEM              = 0x0181;
   public static final int MM_QUERYITEM               = 0x0182;
   public static final int MM_SETITEM                 = 0x0183;
   public static final int MM_QUERYITEMCOUNT          = 0x0184;
   public static final int MM_STARTMENUMODE           = 0x0185;
   public static final int MM_ENDMENUMODE             = 0x0186;
   public static final int MM_REMOVEITEM              = 0x0188;
   public static final int MM_SELECTITEM              = 0x0189;
   public static final int MM_QUERYSELITEMID          = 0x018a;
   public static final int MM_QUERYITEMTEXT           = 0x018b;
   public static final int MM_QUERYITEMTEXTLENGTH     = 0x018c;
   public static final int MM_SETITEMHANDLE           = 0x018d;
   public static final int MM_SETITEMTEXT             = 0x018e;
   public static final int MM_ITEMPOSITIONFROMID      = 0x018f;
   public static final int MM_ITEMIDFROMPOSITION      = 0x0190;
   public static final int MM_QUERYITEMATTR           = 0x0191;
   public static final int MM_SETITEMATTR             = 0x0192;
   public static final int MM_ISITEMVALID             = 0x0193;
   public static final int MM_QUERYITEMRECT           = 0x0194;
   
   public static final int MM_QUERYDEFAULTITEMID      = 0x0431;
   public static final int MM_SETDEFAULTITEMID        = 0x0432;

   public static final int MIT_END                   =  -1;
   public static final int MIT_NONE                   = -1;
   public static final int MIT_MEMERROR               = -1;
   public static final int MIT_ERROR                  = -1;
   public static final int MIT_FIRST                  = -2;
   public static final int MIT_LAST                   = -3;
//   public static final int MID_NONE                   
   public static final int MID_ERROR                  = -1;
   
   /* Menu item styles & attributes */
   
   public static final int MIS_TEXT                   = 0x0001;
   
   public static final int MIS_BITMAP                 = 0x0002;
   public static final int MIS_SEPARATOR              = 0x0004;
   
   public static final int MIS_OWNERDRAW             =  0x0008;
   
   public static final int MIS_SUBMENU                = 0x0010;
   public static final int MIS_MULTMENU               = 0x0020;      /* multiple choice submenu     */
   public static final int MIS_SYSCOMMAND             = 0x0040;
   public static final int MIS_HELP                   = 0x0080;
   public static final int MIS_STATIC                 = 0x0100;
   public static final int MIS_BUTTONSEPARATOR        = 0x0200;
   public static final int MIS_BREAK                 =  0x0400;
   public static final int MIS_BREAKSEPARATOR        =  0x0800;
   public static final int MIS_GROUP                  = 0x1000;      /* multiple choice group start */
   /* In multiple choice submenus a style of 'single' denotes the item is a
   ** radiobutton.  Absence of this style defaults the item to a checkbox.       */
   public static final int MIS_SINGLE                 = 0x2000;
   
   public static final int MIA_NODISMISS              = 0x0020;
   public static final int MIA_FRAMED                 = 0x1000;
   public static final int MIA_CHECKED                = 0x2000;
   public static final int MIA_DISABLED               = 0x4000;
   public static final int MIA_HILITED                = 0x8000;

   public static final int PU_POSITIONONITEM          = 0x0001; /* Need idItem parameter */
   
   public static final int PU_HCONSTRAIN              = 0x0002; /* Keep menu on left and right edge */
   public static final int PU_VCONSTRAIN              = 0x0004; /* Keep menu on top and bottom edge */
   
   public static final int PU_NONE                    = 0x0000; /* If invoked by keyboard */
   public static final int PU_MOUSEBUTTON1DOWN        = 0x0008; /* If invoked by button 1 */
   public static final int PU_MOUSEBUTTON2DOWN        = 0x0010; /* If invoked by button 2 */
   public static final int PU_MOUSEBUTTON3DOWN        = 0x0018; /* If invoked by button 3 */
   
   public static final int PU_SELECTITEM              = 0x0020; /* Set selected item (use with kbd) */
   
   public static final int PU_MOUSEBUTTON1            = 0x0040; /* If button1 use allowed */
   public static final int PU_MOUSEBUTTON2            = 0x0080; /* If button2 use allowed */
   public static final int PU_MOUSEBUTTON3            = 0x0100; /* If button3 use allowed */
   public static final int PU_KEYBOARD                = 0x0200; /* If keyboard use allowed */
   /* ACCEL fs bits */

  public static final int AF_CHAR                    = 0x0001;
  public static final int AF_VIRTUALKEY              = 0x0002;
  public static final int AF_SCANCODE                = 0x0004;
  public static final int AF_SHIFT                   = 0x0008;
  public static final int AF_CONTROL                 = 0x0010;
  public static final int AF_ALT                     = 0x0020;
  public static final int AF_LONEKEY                 = 0x0040;
  public static final int AF_SYSCOMMAND              = 0x0100;
  public static final int AF_HELP                    = 0x0200;
  
/* Standard WM_SYSCOMMAND command values */
  public static final int SC_SIZE                    = 0x8000;
  public static final int SC_MOVE                    = 0x8001;
  public static final int SC_MINIMIZE                = 0x8002;
  public static final int SC_MAXIMIZE                = 0x8003;
  public static final int SC_CLOSE                   = 0x8004;
  public static final int SC_NEXT                    = 0x8005;
  public static final int SC_APPMENU                 = 0x8006;
  public static final int SC_SYSMENU                 = 0x8007;
  public static final int SC_RESTORE                 = 0x8008;
  public static final int SC_NEXTFRAME               = 0x8009;
  public static final int SC_NEXTWINDOW              = 0x8010;
  public static final int SC_TASKMANAGER             = 0x8011;
  public static final int SC_HELPKEYS                = 0x8012;
  public static final int SC_HELPINDEX               = 0x8013;
  public static final int SC_HELPEXTENDED            = 0x8014;
  public static final int SC_SWITCHPANELIDS          = 0x8015;
  public static final int SC_DBE_FIRST               = 0x8018;
  public static final int SC_DBE_LAST                = 0x801F;

  public static final int SC_BEGINDRAG               = 0x8020;
  public static final int SC_ENDDRAG                 = 0x8021;
  public static final int SC_SELECT                  = 0x8022;
  public static final int SC_OPEN                    = 0x8023;
  public static final int SC_CONTEXTMENU             = 0x8024;
  public static final int SC_CONTEXTHELP             = 0x8025;
  public static final int SC_TEXTEDIT                = 0x8026;
  public static final int SC_BEGINSELECT             = 0x8027;
  public static final int SC_ENDSELECT               = 0x8028;
  public static final int SC_WINDOW                  = 0x8029;
  public static final int SC_HIDE                    = 0x802a;
   /**********************************************************************/
   /*                                                                    */
   /*                            S L I D E R                             */
   /*                                                                    */
   /**********************************************************************/
   
   /**********************************************************************/
   /* Define messages for the slider control                             */
   /**********************************************************************/
   public static final int SLM_ADDDETENT          = 0x0369;    /* Add detent niche          */
   public static final int SLM_QUERYDETENTPOS     = 0x036a;    /* Query position of detent  */
   public static final int SLM_QUERYSCALETEXT     = 0x036b;    /* Query text at tick number */
   public static final int SLM_QUERYSLIDERINFO    = 0x036c;    /* Query slider information  */
   public static final int SLM_QUERYTICKPOS       = 0x036d;    /* Query position of tick    */
   public static final int SLM_QUERYTICKSIZE      = 0x036e;    /* Query size of tick        */
   public static final int SLM_REMOVEDETENT       = 0x036f;    /* Remove detent niche       */
   public static final int SLM_SETSCALETEXT       = 0x0370;    /* Set text above tick       */
   public static final int SLM_SETSLIDERINFO      = 0x0371;    /* Set slider parameters     */
   public static final int SLM_SETTICKSIZE        = 0x0372;    /* Set size of tick          */
   public static final int SLN_CHANGE             = 1;         /* Slider position changed   */
   public static final int SLN_SLIDERTRACK        = 2;         /* Slider dragged by user    */
   public static final int SLN_SETFOCUS           = 3;         /* Slider gaining focus      */
   public static final int SLN_KILLFOCUS          = 4;         /* Slider losing focus       */
   /**********************************************************************/
   /* Slider control style flag definition                               */
   /**********************************************************************/
   public static final int SLS_HORIZONTAL       = 0x00000000; /* Orient slider horizontally*/
   public static final int SLS_VERTICAL         = 0x00000001; /* Orient slider vertically  */
   public static final int SLS_CENTER           = 0x00000000; /* Center shaft in window    */
   public static final int SLS_BOTTOM           = 0x00000002; /* Offset shaft to bottom (H)*/
   public static final int SLS_TOP              = 0x00000004; /* Offset shaft to top (H)   */
   public static final int SLS_LEFT             = 0x00000002; /* Offset shaft to left (V)  */
   public static final int SLS_RIGHT            = 0x00000004; /* Offset shaft to right (V) */
   public static final int SLS_SNAPTOINCREMENT  = 0x00000008; /* Snap to nearest increment */
   public static final int SLS_BUTTONSBOTTOM    = 0x00000010; /* Add buttons at shaft bot. */
   public static final int SLS_BUTTONSTOP       = 0x00000020; /* Add buttons at shaft top  */
   public static final int SLS_BUTTONSLEFT      = 0x00000010; /* Add buttons left of shaft */
   public static final int SLS_BUTTONSRIGHT     = 0x00000020; /* Add buttons right of shaft*/
   public static final int SLS_OWNERDRAW        = 0x00000040; /* Owner draw some fields    */
   public static final int SLS_READONLY         = 0x00000080; /* Provide a read only slider*/
   public static final int SLS_RIBBONSTRIP      = 0x00000100; /* Provide a ribbon strip    */
   public static final int SLS_HOMEBOTTOM       = 0x00000000; /* Set home position at bot. */
   public static final int SLS_HOMETOP          = 0x00000200; /* Set home position at top  */
   public static final int SLS_HOMELEFT         = 0x00000000; /* Set home position at left */
   public static final int SLS_HOMERIGHT        = 0x00000200; /* Set home position at right*/
   public static final int SLS_PRIMARYSCALE1    = 0x00000000; /* Scale 1 is primary scale  */
   public static final int SLS_PRIMARYSCALE2    = 0x00000400; /* Scale 2 is primary scale  */
   
   /**********************************************************************/
   /* Message attributes for setting and querying slider components      */
   /**********************************************************************/
   public static final int SMA_SCALE1               = 0x0001;
   public static final int SMA_SCALE2               = 0x0002;
   public static final int SMA_SHAFTDIMENSIONS      = 0x0000;
   public static final int SMA_SHAFTPOSITION        = 0x0001;
   public static final int SMA_SLIDERARMDIMENSIONS  = 0x0002;
   public static final int SMA_SLIDERARMPOSITION    = 0x0003;
   public static final int SMA_RANGEVALUE           = 0x0000;
   public static final int SMA_INCREMENTVALUE       = 0x0001;
   public static final int SMA_SETALLTICKS          = 0xFFFF;
   
   /**********************************************************************/
   /* Ownerdraw flag definitions                                         */
   /**********************************************************************/
   public static final int SDA_RIBBONSTRIP          = 0x0001;
   public static final int SDA_SLIDERSHAFT          = 0x0002;
   public static final int SDA_BACKGROUND           = 0x0003;
   public static final int SDA_SLIDERARM            = 0x0004;
   
   /*** WM_HITTEST return codes ********************************************/
   public static final int HT_NORMAL                   = 0;
   public static final int HT_TRANSPARENT          = -1;
   public static final int HT_DISCARD                  =-2;
   public static final int HT_ERROR                      =-3;

   public static final int SPTR_ARROW                  = 1;
   public static final int SPTR_TEXT                  = 2;
   public static final int SPTR_WAIT                  = 3;
   public static final int SPTR_SIZE                  = 4;
   public static final int SPTR_MOVE                  = 5;
   public static final int SPTR_SIZENWSE              = 6;
   public static final int SPTR_SIZENESW              = 7;
   public static final int SPTR_SIZEWE                = 8;
   public static final int SPTR_SIZENS                = 9;
   public static final int SPTR_APPICON               = 10;
   
   public static final int SPTR_ICONINFORMATION       = 11;
   public static final int SPTR_ICONQUESTION          = 12;
   public static final int SPTR_ICONERROR             = 13;
   public static final int SPTR_ICONWARNING           = 14;
   
   public static final int SPTR_ILLEGAL               = 18;
   public static final int SPTR_FILE                  = 19;
   public static final int SPTR_FOLDER                = 20;
   public static final int SPTR_MULTFILE              = 21;
   public static final int SPTR_PROGRAM               = 22;
   public static final int SPTR_DISPLAY_PTRS          = 22;
   public static final int SPTR_PENFIRST              = 23;
   public static final int SPTR_PENLAST               = 39;

   /* image bundle mask bits */
   public static final int IBB_COLOR                  = 0x0001;
   public static final int IBB_BACK_COLOR             = 0x0002;
   public static final int IBB_MIX_MODE               = 0x0004;
   public static final int IBB_BACK_MIX_MODE           = 0x0008;

   /*
      * Standard Clipboard formats
   */
   public static final int CF_TEXT                    = 1;
   public static final int CF_BITMAP                  = 2;
   public static final int CF_DSPTEXT                 = 3;
   public static final int CF_DSPBITMAP               = 4;
   public static final int CF_METAFILE                = 5;
   public static final int CF_DSPMETAFILE             = 6;
   public static final int CF_PALETTE                 = 9;
   public static final int CF_MMPMFIRST              = 10;
   public static final int CF_MMPMLAST               = 19;

   /**********************************************************************/
   /* Container control styles.                                          */
   /**********************************************************************/
   public static final int CCS_EXTENDSEL             = 1;
   public static final int CCS_MULTIPLESEL           = 2;
   public static final int CCS_SINGLESEL             = 4;
   public static final int CCS_AUTOPOSITION          = 8;
   public static final int CCS_VERIFYPOINTERS        = 10;
   public static final int CCS_READONLY              = 20;
   public static final int CCS_MINIRECORDCORE        = 40;
   /* Style to have container support mini icons with the minirecord. */
   public static final int CCS_MINIICONS             = 800;
   /* don't send WM_CONTROLPOINTER on WM_MOUSEMOVE                    */
   public static final int CCS_NOCONTROLPTR          = 1000;
   
   /**********************************************************************/
   /* view identifiers           (flWindowAttr)                          */
   /**********************************************************************/
   public static final int CV_TEXT                  = 1;  /* text view            */
   public static final int CV_NAME                  = 2;  /* name view            */
   public static final int CV_ICON                  = 4;  /* icon view            */
   public static final int CV_DETAIL                = 8;  /* detail view          */
   public static final int CV_FLOW                  = 10;  /* flow items           */
   public static final int CV_MINI                  = 20;  /* use mini icon        */
   public static final int CV_TREE                  = 40;  /* tree view            */
   public static final int CV_GRID                  = 80;  /* gridded icon view    */
   public static final int CV_EXACTLENGTH           = 10000000;  /* Exact match for SearchString */
   
   /**********************************************************************/
   /* Container Attributes        (flWindowAttr)                         */
   /**********************************************************************/
   public static final int CA_CONTAINERTITLE        = 200;
   public static final int CA_TITLESEPARATOR        = 400;
   public static final int CA_TITLELEFT             = 800;
   public static final int CA_TITLERIGHT            = 1000;
   public static final int CA_TITLECENTER           = 2000;
   public static final int CA_OWNERDRAW             = 4000;
   public static final int CA_DETAILSVIEWTITLES     = 8000;
   public static final int CA_ORDEREDTARGETEMPH     = 10000;
   public static final int CA_DRAWBITMAP            = 20000;
   public static final int CA_DRAWICON              = 40000;
   public static final int CA_TITLEREADONLY         = 80000;
   public static final int CA_OWNERPAINTBACKGROUND  = 100000;
   public static final int CA_MIXEDTARGETEMPH       = 200000;
   public static final int CA_TREELINE              = 400000;
   
   /**********************************************************************/
   /* child window IDs                                                   */
   /**********************************************************************/
   public static final int CID_LEFTCOLTITLEWND     = 0x7FF0;   /* column title (left)       */
   public static final int CID_RIGHTCOLTITLEWND    = 0x7FF1;   /* right column title        */
   public static final int CID_BLANKBOX            = 0x7FF2;   /* blank box at bottom right */
   public static final int CID_HSCROLL             = 0x7FF3;   /* horizontal scroll bar     */
   public static final int CID_RIGHTHSCROLL        = 0x7FF4;   /* right horz scroll bar     */
   public static final int CID_CNRTITLEWND         = 0x7FF5;   /* Container title window    */
   public static final int CID_LEFTDVWND           = 0x7FF7;   /* Left Details View window  */
   public static final int CID_RIGHTDVWND          = 0x7FF8;   /* Right Details View window */
   public static final int CID_VSCROLL             = 0x7FF9;   /* vertical scroll bar       */
   public static final int CID_MLE                 = 0x7FFA;   /* MLE window for direct edit*/

   /**********************************************************************/
   /* Container messages                                                 */
   /**********************************************************************/
   public static final int CM_ALLOCDETAILFIELDINFO       = 0x0330;
   public static final int CM_ALLOCRECORD                = 0x0331;
   public static final int CM_ARRANGE                    = 0x0332;
   public static final int CM_ERASERECORD                = 0x0333;
   public static final int CM_FILTER                     = 0x0334;
   public static final int CM_FREEDETAILFIELDINFO        = 0x0335;
   public static final int CM_FREERECORD                 = 0x0336;
   public static final int CM_HORZSCROLLSPLITWINDOW      = 0x0337;
   public static final int CM_INSERTDETAILFIELDINFO      = 0x0338;
   public static final int CM_INSERTRECORD               = 0x0339;
   public static final int CM_INVALIDATEDETAILFIELDINFO  = 0x033a;
   public static final int CM_INVALIDATERECORD           = 0x033b;
   public static final int CM_PAINTBACKGROUND            = 0x033c;
   public static final int CM_QUERYCNRINFO               = 0x033d;
   public static final int CM_QUERYDETAILFIELDINFO       = 0x033e;
   public static final int CM_QUERYDRAGIMAGE             = 0x033f;
   public static final int CM_QUERYRECORD                = 0x0340;
   public static final int CM_QUERYRECORDEMPHASIS        = 0x0341;
   public static final int CM_QUERYRECORDFROMRECT        = 0x0342;
   public static final int CM_QUERYRECORDRECT            = 0x0343;
   public static final int CM_QUERYVIEWPORTRECT          = 0x0344;
   public static final int CM_REMOVEDETAILFIELDINFO      = 0x0345;
   public static final int CM_REMOVERECORD               = 0x0346;
   public static final int CM_SCROLLWINDOW               = 0x0347;
   public static final int CM_SEARCHSTRING               = 0x0348;
   public static final int CM_SETCNRINFO                 = 0x0349;
   public static final int CM_SETRECORDEMPHASIS          = 0x034a;
   public static final int CM_SORTRECORD                 = 0x034b;
   public static final int CM_OPENEDIT                   = 0x034c;
   public static final int CM_CLOSEEDIT                  = 0x034d;
   public static final int CM_COLLAPSETREE               = 0x034e;
   public static final int CM_EXPANDTREE                 = 0x034f;
   public static final int CM_QUERYRECORDINFO            = 0x0350;
   public static final int CM_INSERTRECORDARRAY          = 0x0351;
   public static final int CM_MOVETREE                   = 0x0352;
   public static final int CM_SETTEXTVISIBILITY          = 0x0353;
   public static final int CM_SETGRIDINFO                = 0x0354;
   public static final int CM_QUERYGRIDINFO              = 0x0355;
   public static final int CM_SNAPTOGRID                 = 0x0356;
   
   /**********************************************************************/
   /* Container notifications                                            */
   /**********************************************************************/
   public static final int CN_DRAGAFTER                  = 101;
   public static final int CN_DRAGLEAVE                  = 102;
   public static final int CN_DRAGOVER                   = 103;
   public static final int CN_DROP                          = 104;
   public static final int CN_DROPHELP                   = 105;
   public static final int CN_ENTER                         = 106;
   public static final int CN_INITDRAG                   = 107;
   public static final int CN_EMPHASIS                   = 108;
   public static final int CN_KILLFOCUS                  = 109;
   public static final int CN_SCROLL                        = 110;
   public static final int CN_QUERYDELTA                 = 111;
   public static final int CN_SETFOCUS                   = 112;
   public static final int CN_REALLOCPSZ                 = 113;
   public static final int CN_BEGINEDIT                  = 114;
   public static final int CN_ENDEDIT                    = 115;
   public static final int CN_COLLAPSETREE               = 116;
   public static final int CN_EXPANDTREE                 = 117;
   public static final int CN_HELP                       = 118;
   public static final int CN_CONTEXTMENU                = 119;
   public static final int CN_VERIFYEDIT                 = 134;
   public static final int CN_PICKUP                     = 135;
   public static final int CN_DROPNOTIFY                 = 136;
   public static final int CN_GRIDRESIZED                = 137;

   /**********************************************************************/
   /* Message parameter flags                                            */
   /**********************************************************************/
   public static final int CMA_TOP              = 0x0001;       /* Place at top of zorder   */

   public static final int CMA_FIRST            = 0x0010;       /* Add record as first      */
   public static final int CMA_LAST             = 0x0020;
   public static final int CMA_END              = 0x0040;       /* Add record to end of list*/
   public static final int CMA_PREV             = 0x0080;
   public static final int CMA_NEXT             = 0x0100;

/* PM macros */

public static final short SHORT1FROMMP (int mp) { return (short)mp; }    
public static final short SHORT2FROMMP (int mp) { return (short)(((mp & 0xFFFF0000)>>16) & 0xFFFF); }
//((pixels & 0xFFFF0000)>>16) & 0xFFFF
//public static final int MPFROM2SHORT(short s1, short s2){ return (((int)((short)s1)) | ((int)((short)s2)) << 16);}
public static final int MPFROM2SHORT(short s1, short s2){ return ((s2 << 16) + s1);}
//public static final int MAKELONG (short l, short h) {
//    return ((int)l) | (((int)h) << 16);
//}
//public static final int MAKEFIXED (short intpart, short fractpart) {
//    return MAKELONG (fractpart, intpart);
//}

/** Natives */

public static final native int malloc (int size);
public static final native void free (int block);
public static final native void memcpy (int dest, int src, int n);
public static final native void memset (int str, int ch, int n);
public static final native int strlen (int str);

/* Native<->Java Object copiers */
    
public static final native void objcpy (int to, int[] from);    
public static final native void objcpy (int[] to, int from);

public static final native void objcpy (int to, byte[] from);    
public static final native void objcpy (byte[] to, int from);

public static final native void objcpy (int to, TIB from);    
public static final native void objcpy (TIB to, int from);

public static final native void objcpy (int to, PIB from);    
public static final native void objcpy (PIB to, int from);

public static final native void objcpy (int to, RECTL from);    
public static final native void objcpy (RECTL to, int from);

public static final native void objcpy (int to, SWP from);    
public static final native void objcpy (SWP to, int from);

public static final native void objcpy (int to, OWNERITEM from);    
public static final native void objcpy (OWNERITEM to, int from);

public static final native void objcpy (int to, RECORDCORE from);    
public static final native void objcpy (RECORDCORE to, int from);

/* PM functions */

public static final native int DevOpenDC (int hab, int lType, PSZ pszToken,
    int lCount, PSZ []pdopData, int hdcComp);
public static final native int DevCloseDC (int hdc);

public static final native boolean DevQueryCaps (int hdc, int lStart, int lCount,
    int[] alArray);

public static final native boolean WinAlarm (int hwndDeskTop, int flStyle);

public static final native int WinInitialize (int flOptions);
public static final native boolean WinTerminate (int hab);
public static final native int WinGetLastError (int hab);
public static final native int WinCreateMsgQueue (int hab, int lQueuesize);
public static final native boolean WinDestroyMsgQueue (int hmq);

public static final native boolean WinRegisterClass (int hab, PSZ pszClassName,
    int pfnWndProc, int flStyle, int cbWindowData );

public static final native int WinCreateWindow (int hwndParent, PSZ pszClass,
    PSZ pszName, int flStyle, int x, int y, int cx, int cy, int hwndOwner,
    int hwndInsertBehind, int id, int pCtlData, int pPresParams);
public static final native int WinCreateWindow (int hwndParent, PSZ pszClass,
    PSZ pszName, int flStyle, int x, int y, int cx, int cy, int hwndOwner,
    int hwndInsertBehind, int id, SLDCDATA pCtlData, int pPresParams);
public static final native boolean WinDestroyWindow (int hwnd);
public static final native int WinDefWindowProc (int hwnd, int ulMsgid,
    int mpParam1, int mpParam2);

public static final native boolean WinPeekMsg (int hab, QMSG qmsg,
    int hwndFilter, int ulFirst, int ulLast, int flOptions);
public static final native int WinDispatchMsg (int hab, QMSG qmsgMsg);
public static final native boolean WinPostQueueMsg (int hmq, int msg, int mp1,
    int mp2);
public static final native boolean WinWaitMsg (int hab, int ulFirst, int ulLast);
public static final native int WinSendMsg (int hwnd, int ulMsgid, int mpParam1, int mpParam2);
public static final native int WinSendMsg (int hwnd, int ulMsgid, RECTL mpParam1, int mpParam2);
public static final native int WinSendMsg (int hwnd, int ulMsgid, RECTL mpParam1, boolean mpParam2);
public static final native int WinSendMsg (int hwnd, int ulMsgid, PSZ mpParam1, int mpParam2);
public static final native int WinSendMsg (int hwnd, int ulMsgid, int mpParam1, PSZ mpParam2);
public static final native int WinSendMsg (int hwnd, int ulMsgid, LBOXINFO mpParam1, PSZ[] mpParam2);
public static final native int WinSendMsg (int hwnd, int ulMsgid, MENUITEM mpParam1, PSZ mpParam2);
public static final native int WinSendMsg (int hwnd, int ulMsgid, RECORDCORE mpParam1, RECORDINSERT mpParam2);
public static final native int WinSendMsg (int hwnd, int ulMsgid, int[] mpParam1, int mpParam2);
public static final native boolean WinSendMsg (int hwnd, int ulMsgid, int mpParam1, MENUITEM mpParam2);
public static final native boolean WinSendMsg (int hwnd, int ulMsgid, FATTRS mpParam1, int mpParam2);
public static final native boolean WinPostMsg (int hwnd, int ulMsgid,int mpParam1, int mpParam2);
public static final native int WinQueryMsgTime (int hab);
public static final native boolean WinQueryMsgPos (int hab, int[] pptl);

public static final native int WinQueryWindowTextLength (int hwnd);
public static final native int WinQueryWindowText (int hwnd, int lLength,
    PSZ pszBuffer);
public static final native boolean WinSetWindowText (int hwnd, PSZ pszString);
public static final native int WinQueryWindowULong (int hwnd, int index);
public static final native boolean WinSetWindowULong (int hwnd, int index, int ul);
public static final native short WinQueryWindowUShort (int hwnd, int index);
public static final native boolean WinSetWindowUShort (int hwnd, int index, short us);
public static final native int WinQueryWindowPtr (int hwnd, int index);
public static final native boolean WinSetWindowPtr (int hwnd, int lb, int pp);
public static final native boolean WinQueryWindowRect (int hwnd, RECTL prclDest);
public static final native boolean WinMapWindowPoints (int hwndFrom, int hwndTo,
       int [] prgptl, int cwpt);
       
public static final native boolean WinQueryWindowPos (int hwnd, SWP pswp);
public static final native boolean WinSetWindowPos (int hwnd, int hwndInsertBehind,
    int x, int y, int cx, int cy, int fl);
public static final native boolean WinSetMultWindowPos (int hab, int pswp, int cswp);

public static final native boolean WinGetMaxPosition (int hwnd, SWP pswp);

public static final native boolean WinQueryClassInfo (int hab, PSZ pszClassName,
    CLASSINFO pclsiClassInfo);
public static final native int WinQuerySysValue (int hwndDeskTop, int iSysValue);
public static final native int WinQuerySysColor (int hwndDesktop, int clr,
    int lReserved);
public static final native int WinQueryTaskSizePos (int hab, int ulID, SWP pswp);

public static final native boolean WinIsWindow (int hab, int hwnd);
public static final native boolean WinIsWindowEnabled (int hwnd);
public static final native boolean WinIsWindowVisible (int hwnd);
public static final native boolean WinIsWindowShowing (int hwnd);
public static final native boolean WinEnableWindowUpdate (int hwnd, boolean fEnable);

public static final native int WinQueryFocus (int hwndDeskTop);
public static final native boolean WinSetFocus (int hwndDeskTop, int hwndNewFocus);

public static final native int WinQueryWindow (int hwnd, int lCode);
public static final native boolean WinSetParent (int hwnd, int hwndNewParent,
    boolean fRedraw);
public static final native boolean WinShowWindow (int hwnd, boolean fNewVisibility);
public static final native boolean WinEnableWindow (int hwnd, boolean fNewEnabled);

public static final native int WinWindowFromID (int hwndParent, int id);

public static final native int WinGetPS (int hwnd);
public static final native int WinGetScreenPS (int hwndDeskTop);
public static final native boolean WinReleasePS (int hps);
public static final native int WinBeginPaint (int hwnd, int hps, RECTL prclPaint);
public static final native boolean WinEndPaint (int hps);
public static final native boolean WinInvalidateRect (int hwnd, RECTL pwrc, 
    boolean fIncludeChildren);
public static final native boolean WinUpdateWindow (int hwnd);
public static final native int WinScrollWindow (int hwnd, int lDx, int lDy,
    RECTL prclScroll, RECTL prclClip, int hrgnUpdateRgn, RECTL prclUpdate, int flOptions);

public static final native boolean WinCreateFrameControls (int hwndFrame,
    FRAMECDATA pfcdata, PSZ pszTitle);
public static final native boolean WinCalcFrameRect (int hwndFrame, RECTL prcl,
    boolean fClient);

public static final native boolean WinFillRect (int hps, RECTL prcl, int lColor);

public static final native int WinQueryUpdateRegion (int hwnd, int hrgn);
public static final native boolean WinInvalidateRegion (int hwnd, int hrgn,
    boolean fIncludeChildren);

public static final native int WinRealizePalette (int hwnd, int hps, int[] pcclr);

public static final native int WinGetSysBitmap (int hwndDesktop, int ibm);

public static final native boolean WinSetPresParam (int hwnd, int idAttrType, 
    int cbAttrValueLen, int[] pAttrValue);
public static final native boolean WinSetPresParam (int hwnd, int idAttrType, 
    int cbAttrValueLen, byte[] pAttrValue);
public static final native int WinQueryPresParam (int hwnd, int idAttrType1,
    int idAttrType2, int[] pidAttrTypeFound, int cbAttrValueLen, byte[] pAttrValue,
    int flOptions);

public static final native int WinCreatePointerIndirect (int hwndDesktop,
    POINTERINFO pptri);
public static final native boolean WinDestroyPointer (int hptrPointer);
public static final native boolean WinQueryPointerInfo (int hptr,
    POINTERINFO pptriPointerInfo);
public static final native boolean WinDrawPointer (int hps, int lx, int ly,
    int hptrPointer, int ulHalftone);

public static final native int WinDrawText (int hps, int cchText,
//@@TODO (dmik): Unicode     
//    char[] lpchText, RECTL prcl, int clrFore, int clrBack, int flCmd);
    byte[] lpchText, RECTL prcl, int clrFore, int clrBack, int flCmd);
    
public static final native int WinGetKeyState (int hwndDesktop, int vkey);
    
public static final native int WinMessageBox (int hwndParent, int hwndOwner,
    PSZ pszMessage, PSZ pszTitle, int ulWinId, int flStyle);

public static final native int WinFontDlg (int hwndParent, int hwndOwner, FONTDLG pfntdlg);
public static final native int WinEditColorDlg (int hwndParent, int hwndOwner, int color, PSZ pszTitle);

public static final native boolean WinFileDlg (int hwndParent, int hwndOwner, FILEDLG filedlg);
public static final native boolean WinFreeFileDlgList (PSZ[] papszFQFilename);
public static final native int WinOpenWindowDC(int hwnd);
public static final native boolean WinPopupMenu(int hwndParent, int hwndOwner, int hwndMenu, int x, int y, int idItem, short fs);
public static final native int WinCreateAccelTable(int hab, ACCELTABLE acctAccelTable);
public static final native boolean WinDestroyAccelTable(int haccelAccel);
public static final native boolean WinSetAccelTable(int hab, int haccelAccel, int hwndFrame);
public static final native boolean WinTranslateAccel(int hab, int hwnd, int haccelAccel, QMSG qmsg);
public static final native int WinQueryActiveWindow(int hwndParent);
public static final native boolean WinGetMsg(int hab, QMSG qmsg, int hwndFilter, int ulFisrt, int ulLast);
public static final native int WinQuerySystemAtomTable();
public static final native int WinFindAtom(int hatomtblAtomTbl, PSZ pszAtomName);
public static final native int WinQueryClassName(int hwnd, int lLength, PSZ PCHBuffer);
public static final native boolean WinQueryCursorInfo(int hwndDeskTop, CURSORINFO pcsriCursorInfo);
public static final native int WinWindowFromPoint(int hwndParent, POINTL pptlPoint, boolean fEnumChildren);
public static final native boolean WinSetPointerPos(int hwndDeskTop, int lx, int ly);
public static final native boolean WinSetCapture(int hwndDeskTop, int hwnd);
public static final native int WinQueryCapture(int hwndDeskTop);
public static final native boolean WinSetActiveWindow(int hwndDeskTop, int hwnd);
public static final native boolean WinQueryUpdateRect(int hwnd, RECTL prclPrc);
public static final native boolean WinOpenClipbrd(int hab);
public static final native boolean WinCloseClipbrd(int hab);
public static final native int WinQueryClipbrdData(int hab, int fmt);
public static final native int WinUpperChar(int hab, int ulCodepage, int ulCountry, int ulInchar);
public static final native int WinStartTimer(int hab, int hwnd, int idTimer, int dtTimeout);
public static final native boolean WinStopTimer(int hab, int hwnd, int ulTimer);
public static final native int WinBeginEnumWindows(int hwnd);
public static final native boolean WinEndEnumWindows(int henum);
public static final native int WinGetNextWindow(int henum);
public static final native void Ft2EnableFontEngine(boolean fEnable);
public static final native boolean PrfQueryProfileSize (int hini, PSZ pszApp,
    PSZ pszKey, int[] pulDataLen);
public static final native int PrfQueryProfileString (int hini, PSZ pszApp,
    PSZ pszKey, PSZ pszDefault, byte[] pBuffer, int ulBufferMax);
    
// PM helpers

public static final native int WinCallWindowProc (int pWindowProc, int hwnd,
    int ulMsgid, int mpParam1, int mpParam2);

/* Base functions */

public static final native int DosGetInfoBlocks (int[] tib, int[] pib);
public static final native int DosQuerySysInfo( int iStart, int iLast, int[] pBuf, int cbBuf);

/* GPI functions */

public static final native int GpiCreatePS (int hab, int hdc, int[] psizlSize, int flOptions);
public static final native boolean GpiDestroyPS (int hps);

public static final native int GpiQueryDevice (int hps);
public static final native boolean GpiQueryDeviceBitmapFormats (int hps, int lCount,
       int [] alArray);

public static final native int GpiCreatePalette (int hab, int flOptions,
    int ulFormat, int ulCount, int[] aulTable);
public static final native boolean GpiDeletePalette (int hpal);
public static final native int GpiQueryNearestPaletteIndex (int hpal, int color);
public static final native int GpiQueryPaletteInfo (int hpal, int hps,
    int flOptions, int ulStart, int ulCount, int[] aulArray);
public static final native boolean GpiSetPaletteEntries (int hpal, int ulFormat,
    int ulStart, int ulCount, int[] aulTable);
public static final native int GpiQueryPalette (int hps);
public static final native int GpiSelectPalette (int hps, int hpal);
public static final native boolean GpiCreateLogColorTable (int hps, int flOptions,
    int lFormat, int lStart, int lCount, int[] alTable);

public static final native int GpiQueryColor (int hps);
public static final native boolean GpiSetColor (int hps, int lColor);
public static final native int GpiQueryBackColor (int hps);
public static final native boolean GpiSetBackColor (int hps, int lColor);
public static final native int GpiQueryMix (int hps);
public static final native boolean GpiSetMix (int hps, int lMixMode);
public static final native int GpiQueryBackMix (int hps);
public static final native boolean GpiSetBackMix (int hps, int lMixMode);
public static final native int GpiQueryPattern (int hps);
public static final native boolean GpiSetPattern (int hps, int lPatternSymbol);
public static final native int GpiQueryLineType (int hps);
public static final native boolean GpiSetLineType (int hps, int lLineType);
//public static final native boolean GpiSetLineWidth (int hps, int fxLineWidth);

public static final native int GpiQueryClipBox (int hps, RECTL prclBound);
public static final native int GpiQueryClipRegion (int hps);
public static final native int GpiSetClipRegion (int hps, int hrgn, int[] phrgnOld);

public static final native int GpiCreateRegion (int hps, int lCount,
    int[] arclRectangles);
public static final native boolean GpiSetRegion (int hps, int hrgn, int lCount,
    int[] arclRectangles);
public static final native boolean GpiDestroyRegion (int hps, int hrgn);
public static final native boolean GpiQueryRegionRects (int hps, int hrgn,
    RECTL prclBound, RGNRECT prgnrcControl, int[] prclRect);
public static final native int GpiQueryRegionBox (int hps, int hrgn, RECTL prclBound);
public static final native int GpiCombineRegion (int hps, int hrgnDest,
    int hrgnSrc1, int hrgnSrc2, int lMode);
public static final native int GpiPtInRegion (int hps, int hrgn, int[] pptlPoint);
public static final native int GpiRectInRegion (int hps, int hrgn, RECTL prclRect);

public static final native boolean GpiQueryCurrentPosition (int hps, int[] pptlPoint);
public static final native int GpiSetPel (int hps, int[] pptlPoint);
public static final native boolean GpiMove (int hps, int[] pptlPoint);
public static final native int GpiLine (int hps, int[] pptlEndPoint);
public static final native int GpiBox (int hps, int lControl, int[] pptlPoint,
    int lHRound, int lVRound);
public static final native boolean GpiSetArcParams (int hps, int[] parcpArcParams);
public static final native int GpiPartialArc (int hps, int[] pptlCenter,
    int fxMultiplier, int fxStartAngle, int fxSweepAngle);
public static final native int GpiPolyLine (int hps, int lCount, int[] aptlPoints);

public static final native boolean GpiBeginArea (int hps, int flOptions);
public static final native int GpiEndArea (int hps);
public static final native boolean GpiBeginPath (int hps, int lPath);
public static final native boolean GpiEndPath (int hps);
public static final native int GpiStrokePath (int hps, int lPath, int flOptions);
public static final native int GpiQueryLineWidthGeom (int hps);
public static final native boolean GpiSetLineWidthGeom (int hps, int lLineWidth);
public static final native boolean GpiSetLineEnd (int hps, int lLineEnd);
public static final native boolean GpiSetLineJoin (int hps, int lLineJoin);

public static final native int GpiQueryAttrs (int hps, int lPrimType,
    int flAttrMask, int[] ppbunAttrs);
public static final native boolean GpiSetAttrs (int hps, int lPrimType,
    int flAttrMask, int flDefMask, int[] ppbunAttrs);
       
public static final native boolean GpiQueryDefaultViewMatrix (int hps, int lCount,
    int[] pmatlfArray);
public static final native boolean GpiSetDefaultViewMatrix (int hps, int lCount,
    int[] pmatlfArray, int lOptions);

public static final native int GpiSetBitmap (int hps, int hbm);
public static final native int GpiCreateBitmap (int hps, BITMAPINFOHEADER2 pbmpNew,
    int flOptions, byte[] pbInitData, int[] pbmiInfoTable);
public static final native boolean GpiDeleteBitmap (int hbm);
public static final native boolean GpiQueryBitmapInfoHeader (int hbm,
    BITMAPINFOHEADER2 pbmpData);
public static final native int GpiQueryBitmapBits (int hps, int lScanStart,
    int lScans, byte[] pbBuffer, int[] pbmiInfoTable);
public static final native int GpiSetBitmapBits (int hps, int lScanStart,
    int lScans, byte[] pbBuffer, int[] pbmiInfoTable);
public static final native int GpiBitBlt (int hpsTarget, int hpsSource, int lCount,
    int[] aptlPoints, int lRop, int flOptions);
public static final native int GpiWCBitBlt (int hpsTarget, int hbmSource, int lCount,
    int[] aptlPoints, int lRop, int flOptions);
public static final native int GpiDrawBits (int hps, byte[] pBits, int[] pbmiInfoTable,
    int lCount, int[] aptlPoints, int lRop, int flOptions);

public static final native boolean GpiSetTextAlignment (int hps, int lHoriz, int lVert);
public static final native int GpiCharStringAt (int hps, int[] pptlPoint, int lCount,
//@@TODO (dmik): Unicode     
//    char[] pchString);
    byte[] pchString);
public static final native boolean GpiQueryTextBox (int hps, int lCount1,
//@@TODO (dmik): Unicode     
//    char[] pchString, int lCount2, int[] aptlPoints);
    byte[] pchString, int lCount2, int[] aptlPoints);

public static final native int GpiQueryFontAction (int hab, int options);
public static final native int GpiQueryFonts (int hps, int flOptions,
    PSZ pszFacename, int[] plReqFonts, int lMetricsLength, byte[] afmMetrics);
public static final native int GpiCreateLogFont (int hps, byte[] pName, int lLcid,
    FATTRS pfatAttrs);
public static final native boolean GpiSetCharSet (int hps, int llcid);
public static final native boolean GpiSetCharBox (int hps, int[] psizfxBox);                
public static final native boolean GpiQueryFontMetrics (int hps, int lMetricsLength, FONTMETRICS pfmMetrics);
}

