
#include "eclipseOS.h"

#define INCL_PM
#define INCL_DOSERRORS
#include <os2.h>
#include <process.h>
#include <stdio.h>
#include <string.h>
#include <errno.h>
#include <stdlib.h>

/* Global Variables */
char   dirSeparator  = '\\';
char   pathSeparator = ';';
char*  consoleVM     = "java.exe";
char*  defaultVM     = "javaw.exe";
char*  shippedVMDir  = "jre\\bin\\";
char*  guiList[] = { "os2", NULL };

/* Define the window system arguments for the Java VM. */
static char*  argVM[] = { NULL };

/* Define local variables for the main window. */
static HAB hab;
static HMQ hmq;
static HWND hwndFrame = NULLHANDLE;
static HWND hwndClient = NULLHANDLE;
static PFNWP oldProc;
#define SPLASH_WINDOW_ID 100
#define MSG_BOX_ID 11

/* Define local variables for running the JVM and detecting its exit. */
static char*   javaVM         = NULL;
static int     jvmProcess     = 0;
static int     jvmExitCode    = 0;

/* Define local variables for handling the splash window and its image. */
static char*   loResSplash = "splash_basic.bmp";
static char*   hiResSplash = "splash_full.bmp";
#define SPLASH_TIMER_ID 88
#define LO_RES_DEPTH 8

/* Local functions */
static HBITMAP   loadSplashImage(char *baseDir, char *fileName);
static void      splashTimeout(void);
static MRESULT EXPENTRY clientWndProc (HWND hwnd, ULONG msg, MPARAM mp1, MPARAM mp2);

/*
 * Overrides for platform-neutral macros
 */
#define PROGRAM_NAME "Eclipse for OS/2 (" DEFAULT_OS_ARCH " " __TIMESTAMP__ ")"


/******************************************************************************
*
*  Name        : ReadBitmap
*
*  Description :
*
* Get the bitmap from disk.
* Note that there are 2 formats for bitmap files, one of which is archaic.
* Both formats are supported here.  All new bitmaps should follow the format
* in BITMAPFILEHEADER.
*
*  Concepts    :
*
*  API's       :
*
*  Parameters  : HFILE hfile
*
*  Return      : BOOL
*
*  Notes:
*
*   File Layout - size and layout of following map are dependent on the
*   type of bitmap.  The cbFix field of the structures is used to determine
*   the size of the structures which in turn identifies them.
*
*
*           SINGLE BITMAP FILE FORMAT
*
*   ÚÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄ¿ offset 0
*   ³  Bitmap File Header             (bfh2)  ³ pbfh2->offBits contains data Ä¿
*   ³                                         ³ offset (from begin of file)   ³
*   ÃÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄ´                               ³
*   ³  Bitmap Information Header      (bmp2)  ³                               ³
*   ÃÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄ´                               ³
*   ³  Color Table of RGB Structures  (argb2) ³                               ³
*   ÃÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄ´ <ÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÙ
*   ³  Bitmap Data (scan lines)               ³
*   ³             .                           ³
*   ³             .                           ³
*   ³             .                           ³
*   ÀÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÙ
*
*
*         BITMAP-ARRAY FILE FORMAT
*
*   ÚÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄ¿ offset 0
*   ³  Bitmap Array File Header       (baf2)  ³
*   ³  (only for bitmap arrays)               ³
*   ÃÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄ´
*   ³  Bitmap File Header             (bfh2)  ³ pbfh2->offBits contains data Ä¿
*   ³                                         ³ offset (from begin of file)   ³
*   ÃÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄ´                               ³
*   ³  Bitmap Information Header      (bmp2)  ³                               ³
*   ÃÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄ´                               ³
*   ³  Color Table of RGB Structures  (argb2) ³                               ³
*   ÃÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄ´                               ³
*   ³     next Bitmap Array File Header       ³                               ³
*   ³             .                           ³                               ³
*   ³             .                           ³                               ³
*   ³             .                           ³                               ³
*   ÃÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄ´ <ÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÙ
*   ³  1st Bitmap Data (scan lines)           ³
*   ³                                         ³
*   ³                                         ³
*   ÃÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄ´
*   ³     next Bitmap Data (scan lines)       ³
*   ³             .                           ³
*   ³             .                           ³
*   ³             .                           ³
*   ÀÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÄÙ
*
******************************************************************************/



/*****************************************************************************
 * Name      : APIRET BmpReadFile
 * Purpose   : Reads a bitmap object from a specified file handle
 * Parameter : HWND hwndClient, HFILE hBmpFile, PHBITMAP phBitmap
 * Variables :
 * Result    : API-Returncode and HBITMAP-Handle
 * Remark    : Snippet from JIGSAW.C
 *
 * Author    : Patrick Haller [Dienstag, 26.09.1995 17.57.39]
 *****************************************************************************/

APIRET BmpRead (HPS      hpsBitmap,
                HFILE    hBmpFile,
                PHBITMAP phBitmap)
{
  APIRET             rc  = NO_ERROR;            /* Rckgabewert - Fehlercode */
  APIRET             rc2 = NO_ERROR;            /* Rckgabewert - Fehlercode */
  FILESTATUS3        fFileStatus;
  PBITMAPFILEHEADER2 pBitmapFileHeader2;       /* can address any file types */
  PBITMAPINFOHEADER2 pBitmapInfoHeader2;         /* address any info headers */
  PBITMAPINFO2       pBitmapInfo2;
  PBYTE              pFileBegin = NULL;          /* beginning of bitmap file */
  ULONG              cbRead;             /* Number of bytes read by DosRead. */
  PBYTE              pbImageBits;          /* Zeiger auf die Bits der Bitmap */

  if (phBitmap == NULL)                              /* Parameterberprfung */
    return (ERROR_INVALID_PARAMETER);                /* Fehler signalisieren */

        /* Find out how big the file is, allocate that much memory, and read */
                                                    /* in the entire bitmap. */
  rc = DosQueryFileInfo(hBmpFile,
                        FIL_STANDARD,
                        &fFileStatus,
                        sizeof(fFileStatus));
  if (rc != NO_ERROR)                   /* Ist hier ein Fehler aufgetreten ? */
    return (rc);

  rc = DosAllocMem((PPVOID) &pFileBegin,
                   (ULONG)  fFileStatus.cbFile,
                   (ULONG)  PAG_READ | PAG_WRITE | PAG_COMMIT);
  if (rc != NO_ERROR)                   /* Ist hier ein Fehler aufgetreten ? */
    return (rc);

  rc = DosRead( hBmpFile,
                (PVOID)pFileBegin,
                fFileStatus.cbFile,
                &cbRead);
  if (rc != NO_ERROR)                   /* Ist hier ein Fehler aufgetreten ? */
  {
    DosFreeMem( pFileBegin );
    return (rc);                                             /* signal error */
  }


         /* If it's a bitmap-array, point to common file header.  Otherwise, */
                                              /* point to beginning of file. */
                     /*   Ab hier steht die "Rohbitmap" komplett im Speicher */
  pBitmapFileHeader2 = (PBITMAPFILEHEADER2) pFileBegin;
  pBitmapInfoHeader2 = &(pBitmapFileHeader2->bmp2);
                                      /* only set this when we validate type */
  pbImageBits = (PBYTE) ((PBYTE)pBitmapFileHeader2
                          + pBitmapFileHeader2->offBits);

                                     /* Liegt ein spezieller Bitmaptyp vor ? */
  switch (pBitmapFileHeader2->usType)
  {
    case BFT_BITMAPARRAY:
                  /* If this is a Bitmap-Array, adjust pointer to the normal */
                     /* file header.  We'll just use the first bitmap in the */
                                     /* array and ignore other device forms. */
       pBitmapFileHeader2 = &(((PBITMAPARRAYFILEHEADER2) pFileBegin)->bfh2);
       pBitmapInfoHeader2 = &pBitmapFileHeader2->bmp2;
                                                   /* pointer to info header */
       break;

    case BFT_BMAP:
       pBitmapInfoHeader2 = &pBitmapFileHeader2->bmp2;
                                                   /* pointer to info header */
       break;

    default:           /* these formats aren't supported; don't set any ptrs */
    case BFT_ICON:
    case BFT_POINTER:
    case BFT_COLORICON:
    case BFT_COLORPOINTER:
       break;
  }                                            /* end switch (pbfh2->usType) */

  pBitmapInfo2 = (PBITMAPINFO2)pBitmapInfoHeader2;  /* Beide beginnen gleich */
  if (pBitmapInfoHeader2 == NULL)               /* File format NOT SUPPORTED */
  {
    rc = ERROR_NOT_SUPPORTED;

    DosFreeMem( pFileBegin );
    return (rc);                                             /* signal error */
  }

 /*   Check to see if BMP file has an old structure, a new structure, or     */
 /*   Windows structure.  Capture the common data and treat all bitmaps      */
 /*   generically with pointer to new format.  API's will determine format   */
 /*   using cbFixed field.                                                   */
 /*   Windows bitmaps have the new format, but with less data fields         */
 /*   than PM.  The old strucuture has some different size fields,           */
 /*   though the field names are the same.                                   */
 /*   NOTE: bitmap data is located by offsetting the beginning of the file   */
 /*         by the offset contained in pbfh2->offBits.  This value is in     */
 /*         the same relatie location for different format bitmap files.     */

             /*** !!! GpiCreatePalette wird nicht benutzt.             !!! ***/
             /*** !!! Auf 256-Farben-Displays k”nnte es Probleme geben !!! ***/
                                                  /* Nun die Bitmap erzeugen */
  *phBitmap = GpiCreateBitmap(hpsBitmap,        /* presentation-space handle */
                              pBitmapInfoHeader2,   /* addr structure format */
                              CBM_INIT,                            /* options */
                              pbImageBits,/* address of buffer of image data */
                              pBitmapInfo2); /* adr structure color & format */

  if (*phBitmap == NULLHANDLE)                        /* Fehler aufgetreten  */
    rc = ERROR_INVALID_DATA;                /* set an appropriate error code */

       /* Hier wird zur Fehlerbehandlung eingesprungen. Allerdings ist diese */
                                   /* mit dem normale Clean-Up identisch ... */
  rc2 = DosFreeMem( pFileBegin );
  if (rc == NO_ERROR)             /* do not overlay previously occured error */
    rc = rc2;

  return (rc);                                       /* Rckgabewert liefern */
} /* APIRET BmpReadFile */


/*****************************************************************************
 * Name      : APIRET BmpReadFile
 * Purpose   : Reads a bitmap from a given file
 * Parameter : HWND hwndClient, PSZ pszBmpFile, PHBITMAP phBitmap
 * Variables :
 * Result    : API-Returncode
 * Remark    :
 *
 * Author    : Patrick Haller [Dienstag, 26.09.1995 18.21.06]
 *****************************************************************************/

APIRET BmpReadFile (HPS      hpsBitmap,
                    PSZ      pszBmpFile,
                    PHBITMAP phBitmap)
{
  APIRET rc;                                    /* Rckgabewert - Fehlercode */
  HFILE  hBmpFile;                             /* Dateihandle fr die Bitmap */
  ULONG  ulAction;                            /* Action-Variable fr DosOpen */

  if ( (pszBmpFile == NULL) ||                       /* Parameterberprfung */
       (phBitmap   == NULL) )
    return (ERROR_INVALID_PARAMETER);                /* Fehler signalisieren */

  rc = DosOpen (pszBmpFile,                                      /* Filename */
                &hBmpFile,                                     /* Filehandle */
                &ulAction,                                    /* ActionTaken */
                0L,                                    /* Estimated Filesize */
                0L,                                        /* File Attribute */
                OPEN_ACTION_FAIL_IF_NEW |                       /* OpenFlags */
                OPEN_ACTION_OPEN_IF_EXISTS,
                OPEN_FLAGS_FAIL_ON_ERROR |                       /* OpenMode */
                OPEN_FLAGS_NO_CACHE |
                OPEN_FLAGS_SEQUENTIAL |
                OPEN_FLAGS_NOINHERIT |
                OPEN_SHARE_DENYWRITE |
                OPEN_ACCESS_READONLY,
                0L);                                             /* EABuffer */

  if (rc == NO_ERROR)
  {
    rc = BmpRead (hpsBitmap,
                  hBmpFile,
                  phBitmap);                               /* Einlesevorgang */

    DosClose (hBmpFile);
  }

  return (rc);                                       /* Rckgabewert liefern */
} /* APIRET BmpReadFile */



/*****************************************************************************
 * Name      : HBITMAP MMLoadBitmap
 * Purpose   : Reads a bitmap object from a specified file
 * Parameter : HAB hab
 *             PSZ pszFeatureImage
 * Variables :
 * Result    : API-Returncode and HBITMAP-Handle
 * Remark    : Shortcut version of the function from MMToolkit.h,
 *             avoids loading WPS/SOM/MMPARTS
 *             although this one only supports WIN/OS2 bitmaps
 *
 * Author    : Patrick Haller [Dienstag, 26.09.1995 17.57.39]
 *****************************************************************************/

HBITMAP  MMLoadBitmap(HAB hab,
                      PSZ featureImage)
{
  HPS     hps = WinGetScreenPS( HWND_DESKTOP );
  HBITMAP hbmp;
  APIRET  rc = BmpReadFile(hps,
                       featureImage,
                       &hbmp);
  WinReleasePS( hps );

  if (NO_ERROR != rc)
    return NULLHANDLE;
  else
    return hbmp;
}


/* Display a Message */
void displayMessage( char* message )
{
   WinMessageBox( HWND_DESKTOP, hwndFrame, message, NULL, MSG_BOX_ID, MB_OK );
}

/* Initialize Window System
 *
 * Create a pop window to display the bitmap image.
 */
void initWindowSystem( int* pArgc, char* argv[], int showSplash )
{
   FRAMECDATA fcdata;

   hab = WinInitialize(0);
   hmq = WinCreateMsgQueue(hab, 0);

   /* Create a window that has no decorations. */
   fcdata.cb = sizeof(FRAMECDATA);
   fcdata.flCreateFlags = FCF_TASKLIST;
   fcdata.hmodResources = NULLHANDLE;
   fcdata.idResources = 0;

   hwndFrame = WinCreateWindow(HWND_DESKTOP,
                               WC_FRAME,
                               "Eclipse splash screen",
                               0,
                               0, 0, 0, 0,
                               NULLHANDLE,
                               HWND_TOP,
                               SPLASH_WINDOW_ID,
                               &fcdata,
                               NULL);
   if(hwndFrame == NULLHANDLE) {
      displayMessage("Error creating splash frame window.");
   }
   hwndClient = WinCreateWindow(hwndFrame,
                                WC_STATIC,
                                "#501", /* default resource bitmap for static control (required) */
                                SS_BITMAP,
                                0, 0, 0, 0,
                                hwndFrame,
                                HWND_BOTTOM,
                                FID_CLIENT,
                                NULL,
                                NULL);
   if(hwndClient == NULLHANDLE) {
      displayMessage("Error creating splash client window.");
   }
   oldProc = WinSubclassWindow(hwndClient, clientWndProc);
}


/* Show the Splash Window
 *
 * Open the bitmap, insert into the splash window and display it.
 *
 */
int showSplash( char* timeoutString, char* homeDir, char* featureImage )
{
    int     timeout = 0;
    HBITMAP hBitmap = NULLHANDLE;
    BITMAPINFOHEADER2 bmpHdr;
    HDC     hDC;
    LONG devCaps[2];
    int     depth;
    int     x, y;
    int     width, height;
    QMSG     qmsg;

	/* Determine the splash timeout value (in seconds). */
	if (timeoutString != NULL && strlen( timeoutString ) > 0)
	{
	    sscanf( timeoutString, "%d", &timeout );
	}

    /* Load the bitmap for the feature. */
   hDC = WinOpenWindowDC(hwndClient);
   DevQueryCaps(hDC, CAPS_COLOR_PLANES, 2, devCaps);
   depth = devCaps[0] * devCaps[1];

    if (featureImage != NULL)
    	hBitmap = MMLoadBitmap(hab, featureImage);
    /* If the bitmap could not be found, try again using the install directory. */
    if (hBitmap == NULLHANDLE)
    	 hBitmap = loadSplashImage( homeDir, depth <= LO_RES_DEPTH ? loResSplash : hiResSplash );
    /* If the bitmap could not be found, return an error. */
    if (hBitmap == NULLHANDLE)
    	return ERROR_FILE_NOT_FOUND;

	/* Load the bitmap into the splash popup window. */
    WinSendMsg( hwndClient, SM_SETHANDLE, (MPARAM) hBitmap, 0 );

    /* Centre the splash window and display it. */
   bmpHdr.cbFix = sizeof(BITMAPINFOHEADER2);
   GpiQueryBitmapInfoHeader(hBitmap, &bmpHdr);
   width = WinQuerySysValue(HWND_DESKTOP, SV_CXSCREEN);
   height = WinQuerySysValue(HWND_DESKTOP, SV_CYSCREEN);
   x = (width - bmpHdr.cx) / 2;
   y = (height - bmpHdr.cy) / 2;
   WinSetWindowPos(hwndFrame, 0, x, y, bmpHdr.cx, bmpHdr.cy, SWP_MOVE | SWP_SIZE | SWP_SHOW);
	
	/* If a timeout for the splash window was given */
	if (timeout != 0)
	{
		/* Add a timeout (in milliseconds) to bring down the splash screen. */
        WinStartTimer( hab, hwndClient, SPLASH_TIMER_ID, (timeout * 1000) );
	}

    /* Process messages until the splash window is closed or process is terminated. */
   	while (WinGetMsg( hab, &qmsg, NULLHANDLE, 0, 0 ))
   	{
		WinDispatchMsg( hab, &qmsg );
	}
      WinDestroyWindow(hwndFrame);
      WinDestroyMsgQueue(hmq);
      WinTerminate(hab);
	
	return 0;
}


/* Get the window system specific VM args */
char** getArgVM( char *vm )
{
   return argVM;
}


/* Start the Java VM
 *
 * This method is called to start the Java virtual machine and to wait until it
 * terminates. The function returns the exit code from the JVM.
 */
int startJavaVM( char* args[] )
{
	int   index;
	char* newArg;
 int exitCode;

	/* If this first time the VM was run */
	if (javaVM == NULL)
	{
		/* Make a copy of the JVM (before double quotes are added or else _spawnv() fails) */
	    javaVM = malloc( strlen(args[0]) + 1 );
	    strcpy( javaVM, args[0] );
		
		/* Any argument with spaces must be in double quotes. */
		for (index = 0; args[index] != NULL; index++)
		{
			if (strchr( args[ index ], (int) ' ' ) != NULL)
			{
				newArg = malloc( strlen( args[ index ] ) + 3 );
				sprintf( newArg, "\"%s\"", args[ index ] );
				args[ index ] = newArg;
			}
		}
	}

	/* Start the Java virtual machine */
	jvmProcess = _spawnv( P_NOWAIT, javaVM, args );
	
	/* If the child process (JVM) would not start */
	if (jvmProcess == -1)
	{
		/* Return the error number. */
		jvmExitCode = errno;
		jvmProcess  = 0;
	}
	
	/* else */
	else
	{
      wait( &exitCode );
      jvmExitCode = ((exitCode & 0x00ff) == 0 ? (exitCode >> 8) : exitCode);
	}
	
	/* Return the exit code from the JVM. */
	return jvmExitCode;
}

/* Local functions */

/* Splash Timeout */
static void splashTimeout(void)
{
   /* Stop the timer. */
   WinStopTimer( hab, hwndClient, SPLASH_TIMER_ID );

   WinPostMsg( hwndClient, WM_QUIT, 0, 0 );
}


/* Load the splash image depending on the current locale.
 *
 * This is the search sequence for an USA english locale:
 *
 *     1 - <homeDir>splash\en_US\<fileName>
 *     2 - <homeDir>splash\en\<fileName>
 *     3 - <homeDir>splash\<fileName>
 *
 * Returns an HBITMAP or NULL if the file can not be found.
 */
static HBITMAP loadSplashImage(char* baseDir, char *fileName)
{
	HBITMAP hBitmap = NULLHANDLE;
	char *langEnvVar;
	char lang[4] = {0};
	char country[4] = {0};
	char *splashFile;

   langEnvVar = getenv("LANG");
   if(langEnvVar != NULL && strlen(langEnvVar) == 5) {
      lang[0] = langEnvVar[0];
      lang[1] = langEnvVar[1];
      lang[2] = '\0';
      country[0] = langEnvVar[3];
      country[1] = langEnvVar[4];
      country[2] = '\0';
   }

   splashFile = malloc ( strlen( baseDir ) + 256 );

   sprintf( splashFile, "%s\\splash\\%s_%s\\%s", baseDir, lang, country, fileName );
   hBitmap = MMLoadBitmap(hab, splashFile);
   if(hBitmap == NULLHANDLE) {
      sprintf( splashFile, "%s\\splash\\%s\\%s", baseDir, lang, fileName );
      hBitmap = MMLoadBitmap(hab, splashFile);
   }

   if(hBitmap == NULLHANDLE) {
      sprintf( splashFile, "%s\\splash\\%s", baseDir, fileName );
      hBitmap = MMLoadBitmap(hab, splashFile);
   }

   free (splashFile);
   return hBitmap;
}


/* Window Procedure for the Spash window.
 */
static MRESULT EXPENTRY clientWndProc (HWND hwnd, ULONG msg, MPARAM mp1, MPARAM mp2)
{
   switch (msg)
   {
      case WM_TIMER:
         switch ((USHORT) mp1)
         {
            case SPLASH_TIMER_ID:
               splashTimeout();
               break;
         }
         break;
      case WM_CLOSE:
         WinPostMsg( hwnd, WM_QUIT, 0, 0 );
         break;
   }
   return oldProc(hwnd, msg, mp1, mp2);
}

