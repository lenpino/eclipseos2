#ifndef OS2_INCLUDED
   #include <os2.h>
#endif
//=========================================================================\
// edit color dialog based on the undocumented color wheel OS/2 control    |
// from an idea of Paul Ratcliffe (E-mail: paulr@orac.clara.co.uk or       |
// paul.ratcliffe@bbc.co.uk)                                               |
// coded by Alessandro Cantatore (alexcant@tin.it                          |
// version 1.1                                                             |
//=========================================================================/

// color wheel class ------------------------------------------------------

#define WC_COLORWHEEL               "ColorSelectClass"


//=========================================================================\
// messages:                                                               |
// WM_COLORWHEELCHANGED : mp1 = LONG RGB color                             |
//                        mp2 = MPVOID                                     |
//                        is sent by the color wheel control to its owner  |
//                        when the color wheel color changes               |
//                        Is is also sent from the "edit color" dialog to  |
//                        its owner window                                 |
// CWM_COLORWHEELSETVAL : mp1 = LONG RGB color                             |
//                        mp2 = MPVOID                                     |
//                        is sent to the color wheel to set the current    |
//                        color                                            |
//=========================================================================/


// warp 4 messages
#define WM_COLORWHEELCHANGED        0x0601
#define CWM_COLORWHEELSETVAL        0x0602

// warp 3 messages
#define WM_COLORWHEELCHANGED3       0x130C
#define CWM_COLORWHEELSETVAL3       0x1384


// various constants ------------------------------------------------------

#define MAXTITLESIZE           128     // max length of dialog title
#define SZ_MODULE     "SWTP2049.DLL"    // DLL name


// application structures -------------------------------------------------

#pragma pack(1)

typedef union {
   LONG lClr;
   struct {
      UCHAR blu, grn, red, x;
   } rgb;
} CLR, * PCLR;

#pragma pack(2)

typedef struct {
   USHORT cbSize;                  // structure size
   USHORT res;                     // not used
   HAB hab;                        // anchor block handle
   HLIB hlib;                      // WPCONFIG.DLL handle
   HMODULE hDlgMod;                // COLWHEEL.DLL handle
   HWND hwndParent;                // handle parent window
   HWND hwndOwner;                 // handle owner window
   ULONG ulVer;                    // OS version
   ULONG cwChangedMsg;             // WM_COLORWHEELCHANGED(-3)
   ULONG cwSetMsg;                 // CWM_COLORWHEELSETVAL(-3)
   CHAR achDlgTitle[MAXTITLESIZE]; // dialog title
   BOOL flUpd;                     // update color-wheel/spinbuttons flag
   CLR clrUndo;                    // start color
   CLR clrCurr;                    // current color
   RECTL rclPar;                   // parent window rectangle
   RECTL rcl;                      // dialog window rectangle
} APPDATA, * PAPPDATA;

#pragma pack()

// functions --------------------------------------------------------------

LONG APIENTRY WinEditColorDlg(HWND hwndParent,  // parent handle
                                      HWND hwndOwner,   // owner handle
                                      COLOR color,      // RGB color (LONG)
                                      PSZ pszTitle);    // dialog title

MRESULT EXPENTRY EditColorDlgProc(HWND hwnd,
                                  ULONG msg,
                                  MPARAM mp1,
                                  MPARAM mp2);

VOID SetSpins(HWND hwnd, CLR clr);


// strings ----------------------------------------------------------------
#define SZ_DEFTITLE                 "Edit Color"
#define SZ_RED                      "~Red:"
#define SZ_GREEN                    "~Green:"
#define SZ_BLUE                     "~Blue:"
#define SZ_OK                       "~OK"
#define SZ_UNDO                     "~Undo"
#define SZ_CANCEL                   "~Cancel"


// dlg controls -----------------------------------------------------------
#define DLG_CWHEEL                  100
#define CWHEEL                      101
#define TXT_RED                     102
#define SPN_RED                     103
#define TXT_GREEN                   104
#define SPN_GREEN                   105
#define TXT_BLUE                    106
#define SPN_BLUE                    107
#define BTN_OK                      108
#define BTN_UNDO                    109
#define BTN_CANCEL                  110
