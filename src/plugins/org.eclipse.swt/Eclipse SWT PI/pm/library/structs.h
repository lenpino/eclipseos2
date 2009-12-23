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

/**
 * JNI SWT object field getters and setters declarations for OS/2 structs.
 */

#ifndef INC_structs_H
#define INC_structs_H

#define INCL_BASE
#define INCL_PM
#include <os2.h>

/* PSZ handling routines */

/**
 *  Returns a reference to 'bytes' field (which is of byte[] type) of PSZ
 *  class instance given as the 'psz' argument.
 *  Note: psz should not be NULL
 */
jbyteArray getPSZArray(JNIEnv *env, jobject j_psz);

/**
 *  Returns a pointer to the newly allocated string constructed
 *  from the 'bytes' array elements of PSZ class instance given as
 *  the 'psz' argument.
 *  Note: psz should not be NULL
 */
PSZ getPSZBytes(JNIEnv *env, jobject j_psz);

/**
 *  Frees memory allocated by the getPSZBytes call.
 *  c_pzs should be the pointer returned by that routine.
 *  Note: psz should not be NULL
 */
void releasePSZBytes(JNIEnv *env, jobject j_psz, PSZ psz);

PAPSZ getPAPSZArray(JNIEnv *env, jobjectArray j_papsz);
void releasePAPSZArray(JNIEnv *env, jobjectArray j_papsz, PAPSZ papsz);

/* QMSG struct */
typedef struct QMSG_FID_CACHE {
    int cached;
    jclass clazz;
    jfieldID
        hwnd,
        msg,
        mp1,
        mp2,
        time,
        ptl_x,
        ptl_y,
        reserved;
} QMSG_FID_CACHE;
typedef QMSG_FID_CACHE *PQMSG_FID_CACHE;

void cacheQMSGFids(JNIEnv *env, jobject lpObject, PQMSG_FID_CACHE lpCache);
PQMSG getQMSGFields(JNIEnv *env, jobject lpObject, PQMSG lpStruct, PQMSG_FID_CACHE lpCache);
void setQMSGFields(JNIEnv *env, jobject lpObject, PQMSG lpStruct, PQMSG_FID_CACHE lpCache);

/* TIB struct */
typedef struct TIB_FID_CACHE {
    int cached;
    jclass clazz;
    jfieldID
        tib_pexchain,
        tib_pstack,
        tib_pstacklimit,
        tib_ptib2,
        tib_version,
        tib_ordinal;
} TIB_FID_CACHE;
typedef TIB_FID_CACHE *PTIB_FID_CACHE;

void cacheTIBFids(JNIEnv *env, jobject lpObject, PTIB_FID_CACHE lpCache);
PTIB getTIBFields(JNIEnv *env, jobject lpObject, PTIB lpStruct, PTIB_FID_CACHE lpCache);
void setTIBFields(JNIEnv *env, jobject lpObject, PTIB lpStruct, PTIB_FID_CACHE lpCache);

/* TIB2 struct */
typedef struct TIB2_FID_CACHE {
    int cached;
    jclass clazz;
    jfieldID
        tib2_ultid,
        tib2_ulpri,
        tib2_version,
        tib2_usMCCount,
        tib2_fMCForceFlag;
} TIB2_FID_CACHE;
typedef TIB2_FID_CACHE *PTIB2_FID_CACHE;

void cacheTIB2Fids(JNIEnv *env, jobject lpObject, PTIB2_FID_CACHE lpCache);
PTIB2 getTIB2Fields(JNIEnv *env, jobject lpObject, PTIB2 lpStruct, PTIB2_FID_CACHE lpCache);
void setTIB2Fields(JNIEnv *env, jobject lpObject, PTIB2 lpStruct, PTIB2_FID_CACHE lpCache);

/* PIB struct */
typedef struct PIB_FID_CACHE {
    int cached;
    jclass clazz;
    jfieldID
        pib_ulpid,
        pib_ulppid,
        pib_hmte,
//        pib_pchcmd,
//        pib_pchenv,
        pib_flstatus,
        pib_ultype;
} PIB_FID_CACHE;
typedef PIB_FID_CACHE *PPIB_FID_CACHE;

void cachePIBFids(JNIEnv *env, jobject lpObject, PPIB_FID_CACHE lpCache);
PPIB getPIBFields(JNIEnv *env, jobject lpObject, PPIB lpStruct, PPIB_FID_CACHE lpCache);
void setPIBFields(JNIEnv *env, jobject lpObject, PPIB lpStruct, PPIB_FID_CACHE lpCache);

/* CLASSINFO struct */
typedef struct CLASSINFO_FID_CACHE {
    int cached;
    jclass clazz;
    jfieldID
        flClassStyle,
        pfnWindowProc,
        cbWindowData;
} CLASSINFO_FID_CACHE;
typedef CLASSINFO_FID_CACHE *PCLASSINFO_FID_CACHE;

void cacheCLASSINFOFids(JNIEnv *env, jobject lpObject, PCLASSINFO_FID_CACHE lpCache);
PCLASSINFO getCLASSINFOFields(JNIEnv *env, jobject lpObject, PCLASSINFO lpStruct, PCLASSINFO_FID_CACHE lpCache);
void setCLASSINFOFields(JNIEnv *env, jobject lpObject, PCLASSINFO lpStruct, PCLASSINFO_FID_CACHE lpCache);

/* RECTL struct */
typedef struct RECTL_FID_CACHE {
    int cached;
    jclass clazz;
    jfieldID
        xLeft,
        yBottom,
        xRight,
        yTop;
} RECTL_FID_CACHE;
typedef RECTL_FID_CACHE *PRECTL_FID_CACHE;

void cacheRECTLFids(JNIEnv *env, jobject lpObject, PRECTL_FID_CACHE lpCache);
PRECTL getRECTLFields(JNIEnv *env, jobject lpObject, PRECTL lpStruct, PRECTL_FID_CACHE lpCache);
void setRECTLFields(JNIEnv *env, jobject lpObject, PRECTL lpStruct, PRECTL_FID_CACHE lpCache);

/* FRAMECDATA struct */
typedef struct FRAMECDATA_FID_CACHE {
    int cached;
    jclass clazz;
    jfieldID
        /*cb;*/
        flCreateFlags,
        hmodResources,
        idResources;
} FRAMECDATA_FID_CACHE;
typedef FRAMECDATA_FID_CACHE *PFRAMECDATA_FID_CACHE;

void cacheFRAMECDATAFids(JNIEnv *env, jobject lpObject, PFRAMECDATA_FID_CACHE lpCache);
PFRAMECDATA getFRAMECDATAFields(JNIEnv *env, jobject lpObject, PFRAMECDATA lpStruct, PFRAMECDATA_FID_CACHE lpCache);
void setFRAMECDATAFields(JNIEnv *env, jobject lpObject, PFRAMECDATA lpStruct, PFRAMECDATA_FID_CACHE lpCache);

/* SWP struct */
typedef struct SWP_FID_CACHE {
    int cached;
    jclass clazz;
    jfieldID
        fl,
        cy,
        cx,
        y,
        x,
        hwndInsertBehind,
        hwnd,
        ulReserved1,
        ulReserved2;
} SWP_FID_CACHE;
typedef SWP_FID_CACHE *PSWP_FID_CACHE;

void cacheSWPFids(JNIEnv *env, jobject lpObject, PSWP_FID_CACHE lpCache);
PSWP getSWPFields(JNIEnv *env, jobject lpObject, PSWP lpStruct, PSWP_FID_CACHE lpCache);
void setSWPFields(JNIEnv *env, jobject lpObject, PSWP lpStruct, PSWP_FID_CACHE lpCache);

/* RGNRECT struct */
typedef struct RGNRECT_FID_CACHE {
    int cached;
    jclass clazz;
    jfieldID
        ircStart,
        crc,
        crcReturned,
        ulDirection;
} RGNRECT_FID_CACHE;
typedef RGNRECT_FID_CACHE *PRGNRECT_FID_CACHE;

void cacheRGNRECTFids(JNIEnv *env, jobject lpObject, PRGNRECT_FID_CACHE lpCache);
PRGNRECT getRGNRECTFields(JNIEnv *env, jobject lpObject, PRGNRECT lpStruct, PRGNRECT_FID_CACHE lpCache);
void setRGNRECTFields(JNIEnv *env, jobject lpObject, PRGNRECT lpStruct, PRGNRECT_FID_CACHE lpCache);

/* BITMAPINFOHEADER2 struct */
typedef struct BITMAPINFOHEADER2_FID_CACHE {
    int cached;
    jclass clazz;
    jfieldID
        cx,
        cy,
        cPlanes,
        cBitCount,
        ulCompression,
        cbImage,
        cxResolution,
        cyResolution,
        cclrUsed,
        cclrImportant,
        usUnits,
        usReserved,
        usRecording,
        usRendering,
        cSize1,
        cSize2,
        ulColorEncoding,
        ulIdentifier;
} BITMAPINFOHEADER2_FID_CACHE;
typedef BITMAPINFOHEADER2_FID_CACHE *PBITMAPINFOHEADER2_FID_CACHE;

void cacheBITMAPINFOHEADER2Fids(JNIEnv *env, jobject lpObject, PBITMAPINFOHEADER2_FID_CACHE lpCache);
PBITMAPINFOHEADER2 getBITMAPINFOHEADER2Fields(JNIEnv *env, jobject lpObject, PBITMAPINFOHEADER2 lpStruct, PBITMAPINFOHEADER2_FID_CACHE lpCache);
void setBITMAPINFOHEADER2Fields(JNIEnv *env, jobject lpObject, PBITMAPINFOHEADER2 lpStruct, PBITMAPINFOHEADER2_FID_CACHE lpCache);

/* POINTERINFO struct */
typedef struct POINTERINFO_FID_CACHE {
    int cached;
    jclass clazz;
    jfieldID
        fPointer,
        xHotSpot,
        yHotSpot,
        hbmPointer,
        hbmColor,
        hbmMiniPointer,
        hbmMiniColor;
} POINTERINFO_FID_CACHE;
typedef POINTERINFO_FID_CACHE *PPOINTERINFO_FID_CACHE;

void cachePOINTERINFOFids(JNIEnv *env, jobject lpObject, PPOINTERINFO_FID_CACHE lpCache);
PPOINTERINFO getPOINTERINFOFields(JNIEnv *env, jobject lpObject, PPOINTERINFO lpStruct, PPOINTERINFO_FID_CACHE lpCache);
void setPOINTERINFOFields(JNIEnv *env, jobject lpObject, PPOINTERINFO lpStruct, PPOINTERINFO_FID_CACHE lpCache);

/* FATTRS struct */
typedef struct FATTRS_FID_CACHE {
	int cached;
	jclass clazz;
	jfieldID
		usRecordLength,
		fsSelection,
		lMatch,
		szFacename,
		idRegistry,
		usCodePage,
		lMaxBaselineExt,
		lAveCharWidth,
		fsType,
		fsFontUse;
} FATTRS_FID_CACHE;
typedef FATTRS_FID_CACHE *PFATTRS_FID_CACHE;

void cacheFATTRSFids(JNIEnv *env, jobject lpObject, PFATTRS_FID_CACHE lpCache);
PFATTRS getFATTRSFields(JNIEnv *env, jobject lpObject, PFATTRS lpStruct, PFATTRS_FID_CACHE lpCache);
void setFATTRSFields(JNIEnv *env, jobject lpObject, PFATTRS lpStruct, PFATTRS_FID_CACHE lpCache);

/* FONTMETRICS struct */
typedef struct FONTMETRICS_FID_CACHE
{
	int cached;
	jclass clazz;
	jfieldID
		szFamilyname, 
		szFacename,
		idRegistry,
		usCodePage,
		lEmHeight,
		lXHeight,
		lMaxAscender,
		lMaxDescender,
		lLowerCaseAscent,
		lLowerCaseDescent,
		lInternalLeading,
		lExternalLeading,
		lAveCharWidth,
		lMaxCharInc,
		lEmInc,
		lMaxBaselineExt,
		sCharSlope,
		sInlineDir,
		sCharRot,
		usWeightClass,
		usWidthClass,
		sXDeviceRes,
		sYDeviceRes,
		sFirstChar,
		sLastChar,
		sDefaultChar,
		sBreakChar,
		sNominalPointSize,
		sMinimumPointSize,
		sMaximumPointSize,
		fsType,
		fsDefn,
		fsSelection,
		fsCapabilities,
	    lSubscriptXSize,
	    lSubscriptYSize,
	    lSubscriptXOffset,
	    lSubscriptYOffset,
	    lSuperscriptXSize,
	    lSuperscriptYSize,
	    lSuperscriptXOffset,
	    lSuperscriptYOffset,
	    lUnderscoreSize,
	    lUnderscorePosition,
	    lStrikeoutSize,
	    lStrikeoutPosition,
	    sKerningPairs,
	    sFamilyClass,
	    lMatch,
	    FamilyNameAtom,
	    FaceNameAtom,
	    panose_bFamilyType,
	    panose_bSerifStyle,
	    panose_bWeight,
	    panose_bProportion,
	    panose_bContrast,
	    panose_bStrokeVariation,
	    panose_bArmStyle,
	    panose_bLetterform,
	    panose_bMidline,
	    panose_bXHeight,
	    panose_fbPassedISO,
	    panose_fbFailedISO;	
} FONTMETRICS_FID_CACHE;
typedef FONTMETRICS_FID_CACHE *PFONTMETRICS_FID_CACHE;

void cacheFONTMETRICSFids(JNIEnv *env, jobject lpObject, PFONTMETRICS_FID_CACHE lpCache);
PFONTMETRICS getFONTMETRICSFields(JNIEnv *env, jobject lpObject, PFONTMETRICS lpStruct, PFONTMETRICS_FID_CACHE lpCache);
void setFONTMETRICSFields(JNIEnv *env, jobject lpObject, PFONTMETRICS lpStruct, PFONTMETRICS_FID_CACHE lpCache);

/* FONTDLG struct */
typedef struct FONTDLG_FID_CACHE {
    int cached;
    jclass clazz;
    jfieldID
	 cbSize,
	 hpsScreen,
	 hpsPrinter,
	 pszTitle,
	 pszPreview,
	 pszPtSizeList,
	 pfnDlgProc,
	 pszFamilyname,
	 fxPointSize,
	 fl,
	 flFlags,
	 flType,
	 flTypeMask,
	 flStyle,
	 flStyleMask,
	 clrFore,
	 clrBack,
	 ulUser,
	 lReturn,
	 lSRC,
	 lEmHeight,
	 lXHeight,
	 lExternalLeading,
	 hMod,
	 fAttrs,
	 sNominalPointSize,
	 usWeight,
	 usWidth,
	 x,
	 y,
	 usDlgId,
	 usFamilyBufLen,
	 usReserved;
} FONTDLG_FID_CACHE;
typedef FONTDLG_FID_CACHE *PFONTDLG_FID_CACHE;

void cacheFONTDLGFids(JNIEnv *env, jobject lpObject, PFONTDLG_FID_CACHE lpCache);
PFONTDLG getFONTDLGFields(JNIEnv *env, jobject lpObject, PFONTDLG lpStruct, PFONTDLG_FID_CACHE lpCache);
void setFONTDLGFields(JNIEnv *env, jobject lpObject, PFONTDLG lpStruct, PFONTDLG_FID_CACHE lpCache);

typedef struct FILEDLG_FID_CACHE  {
    int cached;
    jclass clazz;
    jfieldID
        fl,
        ulUser,
        lReturn,
        lSRC,
        pszTitle,
        pszOKButton,
        pfnDlgProc,
        pszIType,
        papszITypeList,
        pszIDrive,
        papszIDriveList,
        hMod,
        szFullFile,
        papszFQFilename,
        ulFQFCount,
        usDlgId,
        x,
        y,
        sEAType;
} FILEDLG_FID_CACHE;
typedef FILEDLG_FID_CACHE *PFILEDLG_FID_CACHE;

void cacheFILEDLGFids(JNIEnv *env, jobject lpObject, PFILEDLG_FID_CACHE lpCache);
PFILEDLG getFILEDLGFields(JNIEnv *env, jobject lpObject, PFILEDLG lpStruct, PFILEDLG_FID_CACHE lpCache);
void setFILEDLGFields(JNIEnv *env, jobject lpObject, PFILEDLG lpStruct, PFILEDLG_FID_CACHE lpCache);

/* LBOXINFO struct */
typedef struct LBOXINFO_FID_CACHE {
    int cached;
    jclass clazz;
    jfieldID
    lItemIndex,
    ulItemCount,
    reserved, 
    reserved2;
} LBOXINFO_FID_CACHE;
typedef LBOXINFO_FID_CACHE *PLBOXINFO_FID_CACHE;

void cacheLBOXINFOFids(JNIEnv *env, jobject lpObject, PLBOXINFO_FID_CACHE lpCache);
PLBOXINFO getLBOXINFOFields(JNIEnv *env, jobject lpObject, PLBOXINFO lpStruct, PLBOXINFO_FID_CACHE lpCache);
void setLBOXINFOFields(JNIEnv *env, jobject lpObject, PLBOXINFO lpStruct, PLBOXINFO_FID_CACHE lpCache);

/* MENUITEM struct */
typedef struct MENUITEM_FID_CACHE {
    int cached;
    jclass clazz;
    jfieldID
    iPosition,
    afStyle,
    afAttribute,
    id,
    hwndSubMenu,
    hItem;
} MENUITEM_FID_CACHE;
typedef MENUITEM_FID_CACHE *PMENUITEM_FID_CACHE;

void cacheMENUITEMFids(JNIEnv *env, jobject lpObject, PMENUITEM_FID_CACHE lpCache);
PMENUITEM getMENUITEMFields(JNIEnv *env, jobject lpObject, PMENUITEM lpStruct, PMENUITEM_FID_CACHE lpCache);
void setMENUITEMFields(JNIEnv *env, jobject lpObject, PMENUITEM lpStruct, PMENUITEM_FID_CACHE lpCache);


typedef struct SBCDATA_FID_CACHE {
	int cached;
	jclass clazz;
	jfieldID
        cb,
        sHilite,
        posFirst,
        posLast,
        posThumb,
        cVisible,
        cTotal;
} SBCDATA_FID_CACHE;
typedef SBCDATA_FID_CACHE *PSBCDATA_FID_CACHE;

void cacheSBCDATAFids(JNIEnv *env, jobject lpObject, PSBCDATA_FID_CACHE lpCache);
PSBCDATA getSBCDATAFields(JNIEnv *env, jobject lpObject, PSBCDATA lpStruct, PSBCDATA_FID_CACHE lpCache);
void setSBCDATAFields(JNIEnv *env, jobject lpObject, PSBCDATA lpStruct, PSBCDATA_FID_CACHE lpCache);

/* OWNERITEM stuct */
typedef struct OWNERITEM_FID_CACHE {
    int cached;
    jclass clazz;
    jfieldID
    hwnd,
    hps,
    fsState,
    fsAttribute,
    fsStateOld,
    fsAttributeOld,
    rclItem,
     idItem,
    hItem;
} OWNERITEM_FID_CACHE;
typedef OWNERITEM_FID_CACHE *POWNERITEM_FID_CACHE;

void cacheOWNERITEMFids(JNIEnv *env, jobject lpObject, POWNERITEM_FID_CACHE lpCache);
POWNERITEM getOWNERITEMFields(JNIEnv *env, jobject lpObject, POWNERITEM lpStruct, POWNERITEM_FID_CACHE lpCache);
void setOWNERITEMFields(JNIEnv *env, jobject lpObject, POWNERITEM lpStruct, POWNERITEM_FID_CACHE lpCache);

/* ACCEL struct */
typedef struct ACCEL_FID_CACHE {
    int cached;
    jclass clazz;
    jfieldID
        fs,
        key,
        cmd;
} ACCEL_FID_CACHE;
typedef ACCEL_FID_CACHE *PACCEL_FID_CACHE;

void cacheACCELFids(JNIEnv *env, jobject lpObject, PACCEL_FID_CACHE lpCache);
PACCEL getACCELFields(JNIEnv *env, jobject lpObject, PACCEL lpStruct, PACCEL_FID_CACHE lpCache);
void setACCELFields(JNIEnv *env, jobject lpObject, PACCEL lpStruct, PACCEL_FID_CACHE lpCache);

/* ACCELTABLE struct */
typedef struct ACCELTABLE_FID_CACHE {
    int cached;
    jclass clazz;
    jfieldID
        cAccel,
        codepage,
        aaccel;
} ACCELTABLE_FID_CACHE;
typedef ACCELTABLE_FID_CACHE *PACCELTABLE_FID_CACHE;

void cacheACCELTABLEFids(JNIEnv *env, jobject lpObject, PACCELTABLE_FID_CACHE lpCache);
PACCELTABLE getACCELTABLEFields(JNIEnv *env, jobject lpObject, PACCELTABLE lpStruct, PACCELTABLE_FID_CACHE lpCache);
void setACCELTABLEFields(JNIEnv *env, jobject lpObject, PACCELTABLE lpStruct, PACCELTABLE_FID_CACHE lpCache);

/* CURSORINFO struct */
typedef struct CURSORINFO_FID_CACHE {
    int cached;
    jclass clazz;
    jfieldID
        hwnd,     
        x,        
        y,        
        cx,       
        cy,       
        fs,     
        rclClip;
} CURSORINFO_FID_CACHE;
typedef CURSORINFO_FID_CACHE *PCURSORINFO_FID_CACHE;

void cacheCURSORINFOFids(JNIEnv *env, jobject lpObject, PCURSORINFO_FID_CACHE lpCache);
PCURSORINFO getCURSORINFOFields(JNIEnv *env, jobject lpObject, PCURSORINFO lpStruct, PCURSORINFO_FID_CACHE lpCache);
void setCURSORINFOFields(JNIEnv *env, jobject lpObject, PCURSORINFO lpStruct, PCURSORINFO_FID_CACHE lpCache);

/* POINTL struct */
typedef struct POINTL_FID_CACHE {
    int cached;
    jclass clazz;
    jfieldID
        x,        
        y;        
} POINTL_FID_CACHE;
typedef POINTL_FID_CACHE *PPOINTL_FID_CACHE;

void cachePOINTLFids(JNIEnv *env, jobject lpObject, PPOINTL_FID_CACHE lpCache);
PPOINTL getPOINTLFields(JNIEnv *env, jobject lpObject, PPOINTL lpStruct, PPOINTL_FID_CACHE lpCache);
void setPOINTLFields(JNIEnv *env, jobject lpObject, PPOINTL lpStruct, PPOINTL_FID_CACHE lpCache);

/* SLDCDATA struct */
typedef struct SLDCDATA_FID_CACHE {
    int cached;
    jclass clazz;
    jfieldID
        cbSize,
        usScale1Increments,
        usScale1Spacing,
        usScale2Increments,
        usScale2Spacing;        
} SLDCDATA_FID_CACHE;
typedef SLDCDATA_FID_CACHE *PSLDCDATA_FID_CACHE;

void cacheSLDCDATAFids(JNIEnv *env, jobject lpObject, PSLDCDATA_FID_CACHE lpCache);
PSLDCDATA getSLDCDATAFields(JNIEnv *env, jobject lpObject, PSLDCDATA lpStruct, PSLDCDATA_FID_CACHE lpCache);
void setSLDCDATAFields(JNIEnv *env, jobject lpObject, PSLDCDATA lpStruct, PSLDCDATA_FID_CACHE lpCache);

/* POINTS struct */
typedef struct POINTS_FID_CACHE {
    int cached;
    jclass clazz;
    jfieldID
        x,        
        y;        
} POINTS_FID_CACHE;
typedef POINTS_FID_CACHE *PPOINTS_FID_CACHE;

void cachePOINTSFids(JNIEnv *env, jobject lpObject, PPOINTS_FID_CACHE lpCache);
PPOINTS getPOINTSFields(JNIEnv *env, jobject lpObject, PPOINTS lpStruct, PPOINTS_FID_CACHE lpCache);
void setPOINTSFields(JNIEnv *env, jobject lpObject, PPOINTS lpStruct, PPOINTS_FID_CACHE lpCache);

/* MINIRECORDCORE struct */
typedef struct MINIRECORDCORE_FID_CACHE {
    int cached;
    jclass clazz;
    jfieldID
        cb,        
        flRecordAttr,
	ptlIcon,
	preccNextRecord,
	pszIcon,
	hptrIcon;        
} MINIRECORDCORE_FID_CACHE;
typedef MINIRECORDCORE_FID_CACHE *PMINIRECORDCORE_FID_CACHE;

void cacheMINIRECORDCOREFids(JNIEnv *env, jobject lpObject, PMINIRECORDCORE_FID_CACHE lpCache);
PMINIRECORDCORE getMINIRECORDCOREFields(JNIEnv *env, jobject lpObject, PMINIRECORDCORE lpStruct, PMINIRECORDCORE_FID_CACHE lpCache);
void setMINIRECORDCOREFields(JNIEnv *env, jobject lpObject, PMINIRECORDCORE lpStruct, PMINIRECORDCORE_FID_CACHE lpCache);

/* TREEITEMDESC struct */
typedef struct TREEITEMDESC_FID_CACHE {
    int cached;
    jclass clazz;
    jfieldID
        hbmExpanded,        
        hbmCollapsed,
	hptrExpanded,
	hptrCollapsed;        
} TREEITEMDESC_FID_CACHE;
typedef TREEITEMDESC_FID_CACHE *PTREEITEMDESC_FID_CACHE;

void cacheTREEITEMDESCFids(JNIEnv *env, jobject lpObject, PTREEITEMDESC_FID_CACHE lpCache);
PTREEITEMDESC getTREEITEMDESCFields(JNIEnv *env, jobject lpObject, PTREEITEMDESC lpStruct, PTREEITEMDESC_FID_CACHE lpCache);
void setTREEITEMDESCFields(JNIEnv *env, jobject lpObject, PTREEITEMDESC lpStruct, PTREEITEMDESC_FID_CACHE lpCache);

/* RECORDINSERT struct */
typedef struct RECORDINSERT_FID_CACHE {
    int cached;
    jclass clazz;
    jfieldID
        cb,        
        pRecordOrder,
	pRecordParent,
	fInvalidateRecord,
	zOrder,
	cRecordsInsert;       
} RECORDINSERT_FID_CACHE;
typedef RECORDINSERT_FID_CACHE *PRECORDINSERT_FID_CACHE;

void cacheRECORDINSERTFids(JNIEnv *env, jobject lpObject, PRECORDINSERT_FID_CACHE lpCache);
PRECORDINSERT getRECORDINSERTFields(JNIEnv *env, jobject lpObject, PRECORDINSERT lpStruct, PRECORDINSERT_FID_CACHE lpCache);
void setRECORDINSERTFields(JNIEnv *env, jobject lpObject, PRECORDINSERT lpStruct, PRECORDINSERT_FID_CACHE lpCache);

/* RECORDCORE struct */
typedef struct RECORDCORE_FID_CACHE {
    int cached;
    jclass clazz;
    jfieldID
        cb,        
        flRecordAttr,
	ptlIcon,
	preccNextRecord,
	pszIcon,
	hptrIcon,
	hptrMiniIcon,
	hbmBitmap,
	hbmMiniBitmap,
	pTreeItemDesc,
	pszText,
	pszName,
	pszTree;       
} RECORDCORE_FID_CACHE;
typedef RECORDCORE_FID_CACHE *PRECORDCORE_FID_CACHE;

void cacheRECORDCOREFids(JNIEnv *env, jobject lpObject, PRECORDCORE_FID_CACHE lpCache);
PRECORDCORE getRECORDCOREFields(JNIEnv *env, jobject lpObject, PRECORDCORE lpStruct, PRECORDCORE_FID_CACHE lpCache);
void setRECORDCOREFields(JNIEnv *env, jobject lpObject, PRECORDCORE lpStruct, PRECORDCORE_FID_CACHE lpCache);

/* FACENAMEDESC struct */
/*@@TODO (dmik): remove!
typedef struct FACENAMEDESC_FID_CACHE {
	int cached;
	jclass clazz;
	jfieldID
        usSize,
        usWeightClass,
        usWidthClass,
        usReserved,
        flOptions;
} FACENAMEDESC_FID_CACHE;
typedef FACENAMEDESC_FID_CACHE *PFACENAMEDESC_FID_CACHE;

void cacheFACENAMEDESCFids(JNIEnv *env, jobject lpObject, PFACENAMEDESC_FID_CACHE lpCache);
PFACENAMEDESC getFACENAMEDESCFields(JNIEnv *env, jobject lpObject, PFACENAMEDESC lpStruct, PFACENAMEDESC_FID_CACHE lpCache);
void setFACENAMEDESCFields(JNIEnv *env, jobject lpObject, PFACENAMEDESC lpStruct, PFACENAMEDESC_FID_CACHE lpCache);
*/



/* OS/2 globals */
extern QMSG_FID_CACHE QMSGFc;
extern TIB_FID_CACHE TIBFc;
extern TIB2_FID_CACHE TIB2Fc;
extern PIB_FID_CACHE PIBFc;
extern CLASSINFO_FID_CACHE CLASSINFOFc;
extern RECTL_FID_CACHE RECTLFc;
extern FRAMECDATA_FID_CACHE FRAMECDATAFc;
extern SWP_FID_CACHE SWPFc;
extern RGNRECT_FID_CACHE RGNRECTFc;
extern BITMAPINFOHEADER2_FID_CACHE BITMAPINFOHEADER2Fc;
extern POINTERINFO_FID_CACHE POINTERINFOFc;
extern FATTRS_FID_CACHE FATTRSFc;
extern FONTMETRICS_FID_CACHE FONTMETRICSFc;
extern FONTDLG_FID_CACHE FONTDLGFc;
extern FILEDLG_FID_CACHE FILEDLGFc;
extern LBOXINFO_FID_CACHE LBOXINFOFc;
extern SBCDATA_FID_CACHE SBCDATAFc;
extern MENUITEM_FID_CACHE MENUITEMFc;
extern OWNERITEM_FID_CACHE OWNERITEMFc;
extern ACCEL_FID_CACHE ACCELFc;
extern ACCELTABLE_FID_CACHE ACCELTABLEFc;
extern CURSORINFO_FID_CACHE CURSORINFOFc;
extern POINTL_FID_CACHE POINTLFc;
extern SLDCDATA_FID_CACHE SLDCDATAFc;
extern POINTS_FID_CACHE POINTSFc;
extern MINIRECORDCORE_FID_CACHE MINIRECORDCOREFc;
extern TREEITEMDESC_FID_CACHE TREEITEMDESCFc;
extern RECORDINSERT_FID_CACHE RECORDINSERTFc;
extern RECORDCORE_FID_CACHE RECORDCOREFc;
/*@@TODO (dmik): remove! 
extern FACENAMEDESC_FID_CACHE FACENAMEDESCFc;
*/

#endif // INC_structs_H

