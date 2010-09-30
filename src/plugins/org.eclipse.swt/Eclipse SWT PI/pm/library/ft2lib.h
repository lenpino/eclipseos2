/* $Id: ft2lib.h,v 1.40 2004/05/07 09:10:08 sandervl Exp $ */
/** @file
 *
 * InnoTek Font Engine for OS/2 - public header
 *
 * Note: You must include os2.h with INCL_PM defined before this header.
 *
 * (C) 2003 InnoTek Systemberatung GmbH
 *
 */

#ifndef __H_FT2LIB
#define __H_FT2LIB

/** Failure code returned by FT2 apis. */
#define FT2_ERROR                    0xffffffff



/*******************************************************************************
*   Structures and Typedefs                                                    *
*******************************************************************************/


typedef unsigned long DWORD;
typedef const char *  LPCSTR;
typedef char *        LPSTR;
typedef const USHORT *LPCWSTR;
typedef USHORT       *LPWSTR;
typedef USHORT        WCHAR;

/*******************************************************************************
*   APIs                                                                       *
*******************************************************************************/

/** Get the FT2LIB version. */
VOID APIENTRY Ft2GetVersion(LONG *plMajor, LONG *plMinor, LONG *plBuildNumber);
VOID APIENTRY Ft2EnableFontEngine(BOOL fEnable);
APIRET Ft2LibInit(void);


HPS     APIENTRY Ft2CreatePS(HAB hab, HDC hdc, PSIZEL psizlSize, ULONG flOptions);
BOOL    APIENTRY Ft2DestroyPS(HPS hps);
BOOL    APIENTRY Ft2Associate(HPS hps, HDC hdc);
BOOL    APIENTRY Ft2RestorePS(HPS hps, LONG lPSid);
LONG    APIENTRY Ft2SavePS(HPS hps);
BOOL    APIENTRY Ft2Erase(HPS hps);

BOOL    APIENTRY Ft2SetColor(HPS hps, LONG lColor);
LONG    APIENTRY Ft2QueryColor(HPS hps);
BOOL    APIENTRY Ft2SetAttrs(HPS hps, LONG lPrimType, ULONG flAttrMask, ULONG flDefMask, PBUNDLE ppbunAttrs);
BOOL    APIENTRY Ft2SetBackColor(HPS hps, LONG lColor);
LONG    APIENTRY Ft2QueryBackColor(HPS hps);
BOOL    APIENTRY Ft2SetMix(HPS hps, LONG lMixMode);
BOOL    APIENTRY Ft2SetBackMix(HPS hps, LONG lMixMode);
BOOL    APIENTRY Ft2SetCurrentPosition(HPS hps, PPOINTL pptlPoint);
BOOL    APIENTRY Ft2QueryCurrentPosition(HPS hps, PPOINTL pptlPoint);
LONG    APIENTRY Ft2SetPel(HPS hps, PPOINTL pptlPoint);
LONG    APIENTRY Ft2QueryPel(HPS hps, PPOINTL pptlPoint);
HBITMAP APIENTRY Ft2SetBitmap(HPS hps, HBITMAP hbm);

LONG    APIENTRY Ft2Box(HPS hps, LONG lControl, PPOINTL pptlPoint, LONG lHRound, LONG lVRound);
BOOL    APIENTRY Ft2Move(HPS hps, PPOINTL pptlPoint);

BOOL    APIENTRY Ft2QueryCharStringPosA(HPS hps, ULONG flOptions, LONG lCount,
                                        PCH pchString, PLONG alXincrements, PPOINTL aptlPositions);
BOOL    APIENTRY Ft2QueryCharStringPosW(HPS hps, ULONG flOptions, LONG lCount,
                                        LPWSTR pchString, PLONG alXincrements, PPOINTL aptlPositions);
BOOL    APIENTRY Ft2QueryTextBox(HPS hps, LONG lCount1, PCH pchString,LONG lCount2, PPOINTL aptlPoints);
BOOL    APIENTRY Ft2QueryTextBoxW(HPS hps, LONG lCount1, LPWSTR pchString,LONG lCount2, PPOINTL aptlPoints);
BOOL    APIENTRY Ft2SetCharSet(HPS hps, LONG llcid);
LONG    APIENTRY Ft2QueryCharSet(HPS hps);
BOOL    APIENTRY Ft2SetCharBox(HPS hps, PSIZEF psizfxBox);
BOOL    APIENTRY Ft2QueryCharBox(HPS hps, PSIZEF psizfxSize);
BOOL    APIENTRY Ft2SetCharAngle(HPS hps, PGRADIENTL pgradlAngle);
BOOL    APIENTRY Ft2QueryCharAngle(HPS hps, PGRADIENTL pgradlAngle);
BOOL    APIENTRY Ft2SetTextAlignment(HPS hps, LONG lHoriz, LONG lVert);
BOOL    APIENTRY Ft2QueryTextAlignment(HPS hps, PLONG plHoriz, PLONG plVert);
BOOL    APIENTRY Ft2SetCharBreakExtra(HPS hps, FIXED  BreakExtra);
BOOL    APIENTRY Ft2SetCharDirection(HPS hps, LONG lDirection);
LONG    APIENTRY Ft2CharString(HPS hps, LONG lCount, PCH pchString);
LONG    APIENTRY Ft2CharStringAt(HPS hps, PPOINTL pptlPoint, LONG lCount,
                                 PCH pchString);
LONG    APIENTRY Ft2CharStringPos(HPS hps, PRECTL prclRect, ULONG flOptions,
                                  LONG lCount, PCH pchString, PLONG alAdx);
LONG    APIENTRY Ft2CharStringPosAt(HPS hps, PPOINTL pptlStart, PRECTL prclRect,
                                    ULONG flOptions, LONG lCount, PCH pchString,
                                    PLONG alAdx);
LONG    APIENTRY Ft2CharStringPosAtA(HPS hps, PPOINTL pptlStart, PRECTL prclRect,
                                     ULONG flOptions, LONG lCount, PCH pchString, PLONG alAdx, ULONG fuWin32Options);
LONG    APIENTRY Ft2CharStringPosAtW(HPS hps, PPOINTL pptlStart, PRECTL prclRect,
                                     ULONG flOptions, LONG lCount, LPWSTR pchString, PLONG alAdx, ULONG fuWin32Options);
BOOL    APIENTRY Ft2QueryCharStringPosAtA(HPS hps, PPOINTL pptlStart, ULONG flOptions, LONG lCount,
                                          PCH pchString, PLONG alXincrements, PPOINTL aptlPositions);
BOOL    APIENTRY Ft2QueryCharStringPosAtW(HPS hps, PPOINTL pptlStart, ULONG flOptions, LONG lCount,
                                          LPWSTR pchString, PLONG alXincrements, PPOINTL aptlPositions);
BOOL    APIENTRY Ft2SetCp(HPS hps, ULONG ulCodePage);
ULONG   APIENTRY Ft2QueryCp(HPS hps);


LONG    APIENTRY Ft2CreateLogFont(HPS hps, PSTR8 pName, LONG lLcid, PFATTRS pfatAttrs);
BOOL    APIENTRY Ft2DeleteSetId(HPS hps, LONG lLcid);
BOOL    APIENTRY Ft2QueryLogicalFont(HPS PS, LONG lcid, PSTR8 name,
                                     PFATTRS attrs, LONG length);
LONG    APIENTRY Ft2QueryFonts(HPS hps, ULONG flOptions, PSZ pszFacename,
                                PLONG plReqFonts, LONG lMetricsLength, PFONTMETRICS afmMetrics);
BOOL    APIENTRY Ft2QueryFontMetrics(HPS hps, LONG lMetricsLength, PFONTMETRICS pfmMetrics);
LONG    APIENTRY Ft2QueryKerningPairs(HPS hps, LONG lCount, PKERNINGPAIRS akrnprData);
BOOL    APIENTRY Ft2LoadPublicFonts(HAB hab, PSZ psz);
BOOL    APIENTRY Ft2UnloadPublicFonts(HAB hab, PSZ psz);
BOOL    APIENTRY Ft2LoadFonts(HAB hab, PSZ psz);
BOOL    APIENTRY Ft2UnloadFonts(HAB hab, PSZ psz);


HPAL    APIENTRY Ft2CreatePalette(HAB hab, ULONG flOptions, ULONG ulFormat, ULONG ulCount, PULONG aulTable);
BOOL    APIENTRY Ft2DeletePalette(HPAL hpal);


PSZ Ft2LibGetErrorMessage();

#endif // __H_FT2LIB


