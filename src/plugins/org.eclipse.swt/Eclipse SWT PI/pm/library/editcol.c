//=========================================================================\
// editcol.c : color wheel dlg procedure                                   |
// version 1.1                                                             |
//=========================================================================/

#pragma strings(readonly)

#define INCL_WIN
//#define INCL_GPI
#define INCL_DOSMODULEMGR
#define INCL_DOSMISC
#include <os2.h>
#include "editcol.h"


//=========================================================================\
// WinEditColorDlg():                                                      |
// parameters: ----------------------------------------------------------- |
// HWND hwndParent  : parent handle                                        |
// HWND hwndOwner   : owner handle                                         |
// COLOR color      : RGB color (LONG)                                     |
// PSZ pszTitle     : dialog title                                         |
// returned value: ------------------------------------------------------- |
// LONG lColor      : RGB color (success), CLR_ERROR (failure)             |
//=========================================================================/


LONG _Export APIENTRY WinEditColorDlg(HWND hwndParent,
                                      HWND hwndOwner,
                                      COLOR color,
                                      PSZ pszTitle) {
   PAPPDATA pad;
   ULONG i;
   int rc;

   // allocate storage for application data
   if (DosAllocMem((PPVOID)&pad, sizeof(APPDATA),
       PAG_READ | PAG_WRITE | PAG_COMMIT))
      return CLR_ERROR;
   // initialize APPDATA structure (size)
   pad->cbSize = sizeof(APPDATA);
   // anchor block
   pad->hab = WinQueryAnchorBlock(hwndOwner);
   // window handles
   pad->hwndParent = hwndParent;
   pad->hwndOwner = hwndOwner;
   // query the OS version
   DosQuerySysInfo(QSV_VERSION_MINOR, QSV_VERSION_MINOR,
                   &pad->ulVer, sizeof(ULONG));
   // sets msg value according to OS version
   if (pad->ulVer > 30) {                  // warp 4
      pad->cwChangedMsg = WM_COLORWHEELCHANGED;
      pad->cwSetMsg = CWM_COLORWHEELSETVAL;
   } else {                                // warp 3 (warp 2?)
      pad->cwChangedMsg = WM_COLORWHEELCHANGED3;
      pad->cwSetMsg = CWM_COLORWHEELSETVAL3;
   } /* endif */
   // register the color wheel control
   if (NULLHANDLE == (pad->hlib = WinLoadLibrary(pad->hab, "WPCONFIG.DLL")))
      return CLR_ERROR;
   // get the module handle to load the dialog from the DLL
   if (rc = DosLoadModule(NULL, 0, SZ_MODULE, &pad->hDlgMod))
      return CLR_ERROR;
   // copy the start color
   pad->clrCurr.lClr = pad->clrUndo.lClr = color;
   // gets the dialog title, if pszTitle is NULL set the default
   if (!pszTitle) pszTitle = SZ_DEFTITLE;
   // code equivalent to strncpy to make DLL smaller
   for (i = 0;
        pad->achDlgTitle[i++] = *pszTitle++;
        i == MAXTITLESIZE - 1? pad->achDlgTitle[i] = 0, pszTitle = "": 0);
   return (LONG)WinDlgBox(hwndParent,
                          hwndOwner,
                          EditColorDlgProc,
                          pad->hDlgMod,
                          DLG_CWHEEL,
                          pad);
}


//=========================================================================\
// EditColorDlgProc(): color wheel dlg procedure                           |
// processed messages: - WM_INITDLG                                        |
//                     - WM_COLORWHEELCHANGED                              |
//                     - WM_CONTROL                                        |
//                     - WM_COMMAND                                        |
//                     - WM_CLOSE                                          |
//                     - WM_DESTROY                                        |
//=========================================================================/

MRESULT EXPENTRY EditColorDlgProc(HWND hwnd,
                                  ULONG msg,
                                  MPARAM mp1,
                                  MPARAM mp2) {
   switch (msg) {
   // init -----------------------------------------------------------------
      case WM_INITDLG: {
         PAPPDATA pad = (PAPPDATA)mp2;
         WinSetWindowPtr(hwnd, 0L, (PVOID)mp2);
         WinSetWindowText(hwnd, pad->achDlgTitle);
         SetSpins(hwnd, pad->clrUndo); // initialize spinbutton
         // initialize the color wheel control
         WinSendDlgItemMsg(hwnd, CWHEEL, pad->cwSetMsg,
                           (MPARAM)pad->clrUndo.lClr, MPVOID);
         pad->flUpd = TRUE;            // sets the update flag
         // show the dialog in the middle of the parent window
         WinQueryWindowRect(pad->hwndParent, &pad->rclPar);
         WinQueryWindowRect(hwnd, &pad->rcl);
         WinSetWindowPos(hwnd, NULLHANDLE,
                         (pad->rclPar.xRight - pad->rcl.xRight) / 2,
                         (pad->rclPar.yTop - pad->rcl.yTop) / 2,
                         0, 0, SWP_MOVE | SWP_SHOW);
         break;
      } // end case WM_INITDLG
   // a new color has been set through the color wheel ---------------------
      case WM_COLORWHEELCHANGED3:
      case WM_COLORWHEELCHANGED: {
         PAPPDATA pad = WinQueryWindowPtr(hwnd, 0L);
         pad->clrCurr.lClr = (LONG)mp1;
         pad->flUpd = FALSE;           // reset update flag
         SetSpins(hwnd, pad->clrCurr);
         pad->flUpd = TRUE;            // set update flag
         // notify new color to owner
         WinSendMsg(pad->hwndOwner, msg, mp1, mp2);
         break;
      } // end case WM_COLORWHEELCHANGED
   // spinbutton value has changed -----------------------------------------
      case WM_CONTROL:
         if ((SHORT1FROMMP(mp1) == SPN_RED ||      // if a spin button
              SHORT1FROMMP(mp1) == SPN_GREEN ||    // originated SPBN_CHANGE
              SHORT1FROMMP(mp1) == SPN_BLUE) &&    // or SPBN_ENDSPIN
             (SHORT2FROMMP(mp1) == SPBN_CHANGE ||
              SHORT2FROMMP(mp1) == SPBN_ENDSPIN)) {
            PAPPDATA pad = WinQueryWindowPtr(hwnd, 0L);
            ULONG ulVal;
            if (!pad->flUpd) break; // value changed on init or by colorwheel
            // query new spinbutton value
            WinSendMsg(HWNDFROMMP(mp2), SPBM_QUERYVALUE,
                       (MPARAM)&ulVal, MPFROM2SHORT(0, SPBQ_ALWAYSUPDATE));
            switch (SHORT1FROMMP(mp1)) {
               case SPN_RED:     pad->clrCurr.rgb.red = (BYTE)ulVal; break;
               case SPN_GREEN:   pad->clrCurr.rgb.grn = (BYTE)ulVal; break;
               default:          pad->clrCurr.rgb.blu = (BYTE)ulVal; break;
            } /* endswitch */
            WinSendDlgItemMsg(hwnd, CWHEEL, pad->cwSetMsg,// update colorwheel
                              (MPARAM)pad->clrCurr.lClr, MPVOID);
            WinSendMsg(pad->hwndOwner, pad->cwChangedMsg, // notify owner
                          (MPARAM)pad->clrCurr.lClr, MPVOID);
         } // end if
         break;
   // user clicked on a PUSHBUTTON -----------------------------------------
      case WM_COMMAND: {
         PAPPDATA pad = WinQueryWindowPtr(hwnd, 0L);
         switch ((USHORT)mp1) {
            case BTN_OK:            // OK: return current color
               WinDismissDlg(hwnd, pad->clrCurr.lClr);
               break;
            case BTN_UNDO:          // UNDO: restore start color
               SetSpins(hwnd, pad->clrUndo);
               WinSendDlgItemMsg(hwnd, CWHEEL, pad->cwSetMsg,
                                 (MPARAM)pad->clrUndo.lClr, MPVOID);
               break;
            default:                // CANCEL: return start color
               WinDismissDlg(hwnd, pad->clrUndo.lClr);
              break;
         } /* endswitch */
         break;
      } // end case WM_COMMAND
   // user closed the "edit color" dialog window ---------------------------
      case WM_CLOSE: {              // return start color
         PAPPDATA pad = WinQueryWindowPtr(hwnd, 0L);
         WinDismissDlg(hwnd, pad->clrUndo.lClr);
         break;
      } // end case WM_CLOSE
      case WM_DESTROY: {
         PAPPDATA pad = WinQueryWindowPtr(hwnd, 0L);
         WinDeleteLibrary(pad->hab, pad->hlib);
         DosFreeMem(pad);
         break;
      } // end case WM_DESTROY
      default:
         return WinDefDlgProc(hwnd, msg, mp1, mp2);
   } /* endswitch */
   return (MRESULT)FALSE;
}


//=========================================================================\
// SetSpins() : sets the spinbutton values on initialization and when a    |
//              new color is set through the Color Wheel control           |
// parameters: ----------------------------------------------------------- |
// HWND hwnd  : window handle                                              |
// CLR clr    : RGB color structure containing new RGB values              |
// returned value: ------------------------------------------------------- |
// VOID                                                                    |
//=========================================================================/


VOID SetSpins(HWND hwnd, CLR clr) {
   WinSendDlgItemMsg(hwnd, SPN_RED, SPBM_SETCURRENTVALUE,
                     (MPARAM)clr.rgb.red, MPVOID);
   WinSendDlgItemMsg(hwnd, SPN_GREEN, SPBM_SETCURRENTVALUE,
                     (MPARAM)clr.rgb.grn, MPVOID);
   WinSendDlgItemMsg(hwnd, SPN_BLUE, SPBM_SETCURRENTVALUE,
                     (MPARAM)clr.rgb.blu, MPVOID);
}
