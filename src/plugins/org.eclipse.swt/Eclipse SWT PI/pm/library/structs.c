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

/**
 * JNI SWT object field getters and setters declarations for OS/2 structs
 */

#include "swt.h"
#include "structs.h"

#include <malloc.h>
#include <memory.h>

#if DEBUG_DLL
#include <conio.h>
#include <string.h>
#endif

/* PSZ handling routines */

jbyteArray getPSZArray(JNIEnv *env, jobject psz)
{
    static jfieldID PSZ_bytes = NULL;
    jbyteArray arr = NULL;
    jint length = 0;
    if ( PSZ_bytes == NULL )
    {
        jclass clazz = (*env)->GetObjectClass(env, psz);
        PSZ_bytes = (*env)->GetFieldID(env, clazz, "bytes", "[B");
    }
    arr = (*env)->GetObjectField(env, psz, PSZ_bytes);
    length = (*env)->GetArrayLength(env, arr);
    if(length > 1)
        return arr;
    else
        return NULL;
}

PSZ getPSZBytes(JNIEnv *env, jobject psz)
{
    return (PSZ)(*env)->GetByteArrayElements(env, getPSZArray(env, psz), NULL);
}

void releasePSZBytes(JNIEnv *env, jobject psz, PSZ c_psz)
{
    (*env)->ReleaseByteArrayElements(env, getPSZArray(env, psz), (jbyte*)c_psz, 0);
}

PAPSZ getPAPSZArray(JNIEnv *env, jobjectArray j_papsz)
{
#if DEBUG_DLL
    HFILE hFile;
    ULONG ulAction;
    ULONG pcbActual;
    ULONG ulLocation;
    DATETIME now;
    char buffer[256];
#endif

    PAPSZ papsz = NULL;
    jsize sz = (*env)->GetArrayLength(env, j_papsz);
    DEBUG_CALL("getPAPSZArray\n")
#if DEBUG_DLL
    DosGetDateTime(&now);
    sprintf(buffer, "j_papsz is %d.  %d:%d:%d\n",
        sz, now.hours, now.minutes, now.seconds);
    DosOpen("PSZStuff.txt", &hFile, &ulAction, 0, FILE_NORMAL,
        OPEN_ACTION_CREATE_IF_NEW | OPEN_ACTION_OPEN_IF_EXISTS,
        OPEN_FLAGS_SEQUENTIAL | OPEN_SHARE_DENYWRITE |
            OPEN_ACCESS_READWRITE, (PEAOP2)NULL);
    DosSetFilePtr(hFile, 0L, FILE_END, &ulLocation);
    DosWrite(hFile, (PVOID)buffer, strlen(buffer), &pcbActual);
#endif
    if (sz) {
        int i;
#if DEBUG_DLL
    sprintf(buffer, "size allocation %d.  %d:%d:%d\n",
        (sizeof(PSZ) * sz), now.hours, now.minutes, now.seconds);
    DosOpen("PSZStuff.txt", &hFile, &ulAction, 0, FILE_NORMAL,
        OPEN_ACTION_CREATE_IF_NEW | OPEN_ACTION_OPEN_IF_EXISTS,
        OPEN_FLAGS_SEQUENTIAL | OPEN_SHARE_DENYWRITE |
            OPEN_ACCESS_READWRITE, (PEAOP2)NULL);
    DosSetFilePtr(hFile, 0L, FILE_END, &ulLocation);
    DosWrite(hFile, (PVOID)buffer, strlen(buffer), &pcbActual);
#endif
        papsz = (PAPSZ) malloc(sizeof(PSZ) * sz);
        for (i = 0; i < sz; i++) {
            jobject psz = (*env)->GetObjectArrayElement(env, j_papsz, i);
            if (psz) {
                *papsz[i] = getPSZBytes(env, psz);
#if DEBUG_DLL
    sprintf(buffer, "item from j_papsz %d: %s.  %d:%d:%d\n",
        i, *papsz[i], now.hours, now.minutes, now.seconds);
    DosOpen("PSZStuff.txt", &hFile, &ulAction, 0, FILE_NORMAL,
        OPEN_ACTION_CREATE_IF_NEW | OPEN_ACTION_OPEN_IF_EXISTS,
        OPEN_FLAGS_SEQUENTIAL | OPEN_SHARE_DENYWRITE |
            OPEN_ACCESS_READWRITE, (PEAOP2)NULL);
    DosSetFilePtr(hFile, 0L, FILE_END, &ulLocation);
    DosWrite(hFile, (PVOID)buffer, strlen(buffer), &pcbActual);
#endif
            } else {
                *papsz[i] = NULL;
#if DEBUG_DLL
    sprintf(buffer, "there was no item from j_papsz %d.  %d:%d:%d\n",
        i, now.hours, now.minutes, now.seconds);
    DosOpen("PSZStuff.txt", &hFile, &ulAction, 0, FILE_NORMAL,
        OPEN_ACTION_CREATE_IF_NEW | OPEN_ACTION_OPEN_IF_EXISTS,
        OPEN_FLAGS_SEQUENTIAL | OPEN_SHARE_DENYWRITE |
            OPEN_ACCESS_READWRITE, (PEAOP2)NULL);
    DosSetFilePtr(hFile, 0L, FILE_END, &ulLocation);
    DosWrite(hFile, (PVOID)buffer, strlen(buffer), &pcbActual);
#endif
            }
        }
    }
#if DEBUG_DLL
    DosClose(hFile);
#endif
    return papsz;
}

// NOTE: it is intended that null array elements remain null
// in the native array, non-null elements remain non-null,
// and the size of the array has not been changed.
void releasePAPSZArray(JNIEnv *env, jobjectArray j_papsz, PAPSZ papsz)
{
    jclass objClass;
    jsize sz;
    sz = (*env)->GetArrayLength(env, j_papsz);
    if (sz) {
        int i;
        jobject psz;
        for (i = 0; i < sz; i++) {
            psz = (*env)->GetObjectArrayElement(env, j_papsz, i);
            if (psz)
                releasePSZBytes(env, psz, (PSZ)*papsz[i]);
        }
        free(papsz);
    }
}

/* OS/2 globals */

QMSG_FID_CACHE QMSGFc;
TIB_FID_CACHE TIBFc;
TIB2_FID_CACHE TIB2Fc;
PIB_FID_CACHE PIBFc;
CLASSINFO_FID_CACHE CLASSINFOFc;
RECTL_FID_CACHE RECTLFc;
FRAMECDATA_FID_CACHE FRAMECDATAFc;
SWP_FID_CACHE SWPFc;
RGNRECT_FID_CACHE RGNRECTFc;
BITMAPINFOHEADER2_FID_CACHE BITMAPINFOHEADER2Fc;
POINTERINFO_FID_CACHE POINTERINFOFc;
FATTRS_FID_CACHE FATTRSFc;
FONTMETRICS_FID_CACHE FONTMETRICSFc;
FONTDLG_FID_CACHE FONTDLGFc;
FILEDLG_FID_CACHE FILEDLGFc;
LBOXINFO_FID_CACHE LBOXINFOFc;
SBCDATA_FID_CACHE SBCDATAFc;

/*@@TODO (dmik): remove
FACENAMEDESC_FID_CACHE FACENAMEDESCFc;
*/

/* QMSG struct */

void cacheQMSGFids(JNIEnv *env, jobject lpObject, PQMSG_FID_CACHE lpCache)
{
    if (lpCache->cached) return;
    lpCache->clazz = (*env)->GetObjectClass(env, lpObject);
    lpCache->hwnd = (*env)->GetFieldID(env, lpCache->clazz, "hwnd", "I");
    lpCache->msg = (*env)->GetFieldID(env, lpCache->clazz, "msg", "I");
    lpCache->mp1 = (*env)->GetFieldID(env, lpCache->clazz, "mp1", "I");
    lpCache->mp2 = (*env)->GetFieldID(env, lpCache->clazz, "mp2", "I");
    lpCache->time = (*env)->GetFieldID(env, lpCache->clazz, "time", "I");
    lpCache->ptl_x = (*env)->GetFieldID(env, lpCache->clazz, "ptl_x", "I");
    lpCache->ptl_y = (*env)->GetFieldID(env, lpCache->clazz, "ptl_y", "I");
    lpCache->reserved = (*env)->GetFieldID(env, lpCache->clazz, "reserved", "I");
    lpCache->cached = 1;
}

PQMSG getQMSGFields(JNIEnv *env, jobject lpObject, PQMSG lpStruct, PQMSG_FID_CACHE lpCache)
{
    if (!lpCache->cached) cacheQMSGFids(env, lpObject, lpCache);
    lpStruct->hwnd = (HWND)(*env)->GetIntField(env, lpObject, lpCache->hwnd);
    lpStruct->msg = (*env)->GetIntField(env, lpObject, lpCache->msg);
    lpStruct->mp1 = (MPARAM)(*env)->GetIntField(env, lpObject, lpCache->mp1);
    lpStruct->mp2 = (MPARAM)(*env)->GetIntField(env, lpObject, lpCache->mp2);
    lpStruct->time = (*env)->GetIntField(env, lpObject, lpCache->time);
    lpStruct->ptl.x = (*env)->GetIntField(env, lpObject, lpCache->ptl_x);
    lpStruct->ptl.y = (*env)->GetIntField(env, lpObject, lpCache->ptl_y);
    lpStruct->reserved = (*env)->GetIntField(env, lpObject, lpCache->reserved);
    return lpStruct;
}

void setQMSGFields(JNIEnv *env, jobject lpObject, PQMSG lpStruct, PQMSG_FID_CACHE lpCache)
{
    if (!lpCache->cached) cacheQMSGFids(env, lpObject, lpCache);
    (*env)->SetIntField(env, lpObject, lpCache->hwnd, (jint)lpStruct->hwnd);
    (*env)->SetIntField(env, lpObject, lpCache->msg, lpStruct->msg);
    (*env)->SetIntField(env, lpObject, lpCache->mp1, (jint)lpStruct->mp1);
    (*env)->SetIntField(env, lpObject, lpCache->mp2, (jint)lpStruct->mp2);
    (*env)->SetIntField(env, lpObject, lpCache->time, lpStruct->time);
    (*env)->SetIntField(env, lpObject, lpCache->ptl_x, lpStruct->ptl.x);
    (*env)->SetIntField(env, lpObject, lpCache->ptl_y, lpStruct->ptl.y);
    (*env)->SetIntField(env, lpObject, lpCache->reserved, lpStruct->reserved);
}

/* TIB struct */

void cacheTIBFids(JNIEnv *env, jobject lpObject, PTIB_FID_CACHE lpCache)
{
    if (lpCache->cached) return;
    lpCache->clazz = (*env)->GetObjectClass(env, lpObject);
    lpCache->tib_pexchain = (*env)->GetFieldID(env, lpCache->clazz, "tib_pexchain", "I");
    lpCache->tib_pstack = (*env)->GetFieldID(env, lpCache->clazz, "tib_pstack", "I");
    lpCache->tib_pstacklimit = (*env)->GetFieldID(env, lpCache->clazz, "tib_pstacklimit", "I");
    lpCache->tib_ptib2 = (*env)->GetFieldID(env, lpCache->clazz, "tib_ptib2", "Lorg/eclipse/swt/internal/pm/TIB2;");
    lpCache->tib_version = (*env)->GetFieldID(env, lpCache->clazz, "tib_version", "I");
    lpCache->tib_ordinal = (*env)->GetFieldID(env, lpCache->clazz, "tib_ordinal", "I");
    lpCache->cached = 1;
}

PTIB getTIBFields(JNIEnv *env, jobject lpObject, PTIB lpStruct, PTIB_FID_CACHE lpCache)
{
    if (!lpCache->cached) cacheTIBFids(env, lpObject, lpCache);
    lpStruct->tib_pexchain = (PVOID)(*env)->GetIntField(env, lpObject, lpCache->tib_pexchain);
    lpStruct->tib_pstack = (PVOID)(*env)->GetIntField(env, lpObject, lpCache->tib_pstack);
    lpStruct->tib_pstacklimit = (PVOID)(*env)->GetIntField(env, lpObject, lpCache->tib_pstacklimit);
    {
        jobject ptib2 = (*env)->GetObjectField(env, lpObject, lpCache->tib_ptib2);
        if (ptib2 && lpStruct->tib_ptib2) getTIB2Fields(env, ptib2, lpStruct->tib_ptib2, &PGLOB(TIB2Fc));
    }
    lpStruct->tib_version = (*env)->GetIntField(env, lpObject, lpCache->tib_version);
    lpStruct->tib_ordinal = (*env)->GetIntField(env, lpObject, lpCache->tib_ordinal);
    return lpStruct;
}

void setTIBFields(JNIEnv *env, jobject lpObject, PTIB lpStruct, PTIB_FID_CACHE lpCache)
{
    if (!lpCache->cached) cacheTIBFids(env, lpObject, lpCache);
    (*env)->SetIntField(env, lpObject, lpCache->tib_pexchain, (jint)lpStruct->tib_pexchain);
    (*env)->SetIntField(env, lpObject, lpCache->tib_pstack, (jint)lpStruct->tib_pstack);
    (*env)->SetIntField(env, lpObject, lpCache->tib_pstacklimit, (jint)lpStruct->tib_pstacklimit);
    {
        jobject ptib2 = (*env)->GetObjectField(env, lpObject, lpCache->tib_ptib2);
        if (ptib2 && lpStruct->tib_ptib2) setTIB2Fields(env, ptib2, lpStruct->tib_ptib2, &PGLOB(TIB2Fc));
    }
    (*env)->SetIntField(env, lpObject, lpCache->tib_version, lpStruct->tib_version);
    (*env)->SetIntField(env, lpObject, lpCache->tib_ordinal, lpStruct->tib_ordinal);
}

/* TIB2 struct */

void cacheTIB2Fids(JNIEnv *env, jobject lpObject, PTIB2_FID_CACHE lpCache)
{
    if (lpCache->cached) return;
    lpCache->clazz = (*env)->GetObjectClass(env, lpObject);
    lpCache->tib2_ultid = (*env)->GetFieldID(env, lpCache->clazz, "tib2_ultid", "I");
    lpCache->tib2_ulpri = (*env)->GetFieldID(env, lpCache->clazz, "tib2_ulpri", "I");
    lpCache->tib2_version = (*env)->GetFieldID(env, lpCache->clazz, "tib2_version", "I");
    lpCache->tib2_usMCCount = (*env)->GetFieldID(env, lpCache->clazz, "tib2_usMCCount", "S");
    lpCache->tib2_fMCForceFlag = (*env)->GetFieldID(env, lpCache->clazz, "tib2_fMCForceFlag", "S");
    lpCache->cached = 1;
}

PTIB2 getTIB2Fields(JNIEnv *env, jobject lpObject, PTIB2 lpStruct, PTIB2_FID_CACHE lpCache)
{
    if (!lpCache->cached) cacheTIB2Fids(env, lpObject, lpCache);
    lpStruct->tib2_ultid = (*env)->GetIntField(env, lpObject, lpCache->tib2_ultid);
    lpStruct->tib2_ulpri = (*env)->GetIntField(env, lpObject, lpCache->tib2_ulpri);
    lpStruct->tib2_version = (*env)->GetIntField(env, lpObject, lpCache->tib2_version);
    lpStruct->tib2_usMCCount = (*env)->GetShortField(env, lpObject, lpCache->tib2_usMCCount);
    lpStruct->tib2_fMCForceFlag = (*env)->GetShortField(env, lpObject, lpCache->tib2_fMCForceFlag);
    return lpStruct;
}

void setTIB2Fields(JNIEnv *env, jobject lpObject, PTIB2 lpStruct, PTIB2_FID_CACHE lpCache)
{
    if (!lpCache->cached) cacheTIB2Fids(env, lpObject, lpCache);
    (*env)->SetIntField(env, lpObject, lpCache->tib2_ultid, lpStruct->tib2_ultid);
    (*env)->SetIntField(env, lpObject, lpCache->tib2_ulpri, lpStruct->tib2_ulpri);
    (*env)->SetIntField(env, lpObject, lpCache->tib2_version, lpStruct->tib2_version);
    (*env)->SetShortField(env, lpObject, lpCache->tib2_usMCCount, lpStruct->tib2_usMCCount);
    (*env)->SetShortField(env, lpObject, lpCache->tib2_fMCForceFlag, lpStruct->tib2_fMCForceFlag);
}

/* PIB struct */

void cachePIBFids(JNIEnv *env, jobject lpObject, PPIB_FID_CACHE lpCache)
{
    if (lpCache->cached) return;
    lpCache->clazz = (*env)->GetObjectClass(env, lpObject);
    lpCache->pib_ulpid = (*env)->GetFieldID(env, lpCache->clazz, "pib_ulpid", "I");
    lpCache->pib_ulppid = (*env)->GetFieldID(env, lpCache->clazz, "pib_ulppid", "I");
    lpCache->pib_hmte = (*env)->GetFieldID(env, lpCache->clazz, "pib_hmte", "I");
    //lpCache->pib_pchcmd = (*env)->GetFieldID(env, lpCache->clazz, "pib_pchcmd", "[B");
    //lpCache->pib_pchenv = (*env)->GetFieldID(env, lpCache->clazz, "pib_pchenv", "[B");
    lpCache->pib_flstatus = (*env)->GetFieldID(env, lpCache->clazz, "pib_flstatus", "I");
    lpCache->pib_ultype = (*env)->GetFieldID(env, lpCache->clazz, "pib_ultype", "I");
    lpCache->cached = 1;
}

PPIB getPIBFields(JNIEnv *env, jobject lpObject, PPIB lpStruct, PPIB_FID_CACHE lpCache)
{
    if (!lpCache->cached) cachePIBFids(env, lpObject, lpCache);
    lpStruct->pib_ulpid = (*env)->GetIntField(env, lpObject, lpCache->pib_ulpid);
    lpStruct->pib_ulppid = (*env)->GetIntField(env, lpObject, lpCache->pib_ulppid);
    lpStruct->pib_hmte = (*env)->GetIntField(env, lpObject, lpCache->pib_hmte);
    // pib_pchcmd, pib_pchenv
    lpStruct->pib_flstatus = (*env)->GetIntField(env, lpObject, lpCache->pib_flstatus);
    lpStruct->pib_ultype = (*env)->GetIntField(env, lpObject, lpCache->pib_ultype);
    return lpStruct;
}

void setPIBFields(JNIEnv *env, jobject lpObject, PPIB lpStruct, PPIB_FID_CACHE lpCache)
{
    if (!lpCache->cached) cachePIBFids(env, lpObject, lpCache);
    (*env)->SetIntField(env, lpObject, lpCache->pib_ulpid, lpStruct->pib_ulpid);
    (*env)->SetIntField(env, lpObject, lpCache->pib_ulppid, lpStruct->pib_ulppid);
    (*env)->SetIntField(env, lpObject, lpCache->pib_hmte, lpStruct->pib_hmte);
    // pib_pchcmd, pib_pchenv
    (*env)->SetIntField(env, lpObject, lpCache->pib_flstatus, lpStruct->pib_flstatus);
    (*env)->SetIntField(env, lpObject, lpCache->pib_ultype, lpStruct->pib_ultype);
}

/* CLASSINFO struct */

void cacheCLASSINFOFids(JNIEnv *env, jobject lpObject, PCLASSINFO_FID_CACHE lpCache)
{
    if (lpCache->cached) return;
    lpCache->clazz = (*env)->GetObjectClass(env, lpObject);
    lpCache->flClassStyle = (*env)->GetFieldID(env, lpCache->clazz, "flClassStyle", "I");
    lpCache->pfnWindowProc = (*env)->GetFieldID(env, lpCache->clazz, "pfnWindowProc", "I");
    lpCache->cbWindowData = (*env)->GetFieldID(env, lpCache->clazz, "cbWindowData", "I");
    lpCache->cached = 1;
}

PCLASSINFO getCLASSINFOFields(JNIEnv *env, jobject lpObject, PCLASSINFO lpStruct, PCLASSINFO_FID_CACHE lpCache)
{
    if (!lpCache->cached) cacheCLASSINFOFids(env, lpObject, lpCache);
    lpStruct->flClassStyle = (*env)->GetIntField(env, lpObject, lpCache->flClassStyle);
    lpStruct->pfnWindowProc = (PFNWP)(*env)->GetIntField(env, lpObject, lpCache->pfnWindowProc);
    lpStruct->cbWindowData = (*env)->GetIntField(env, lpObject, lpCache->cbWindowData);
    return lpStruct;
}

void setCLASSINFOFields(JNIEnv *env, jobject lpObject, PCLASSINFO lpStruct, PCLASSINFO_FID_CACHE lpCache)
{
    if (!lpCache->cached) cacheCLASSINFOFids(env, lpObject, lpCache);
    (*env)->SetIntField(env, lpObject, lpCache->flClassStyle, lpStruct->flClassStyle);
    (*env)->SetIntField(env, lpObject, lpCache->pfnWindowProc, (jint)lpStruct->pfnWindowProc);
    (*env)->SetIntField(env, lpObject, lpCache->cbWindowData, lpStruct->cbWindowData);
}

/* RECTL struct */

void cacheRECTLFids(JNIEnv *env, jobject lpObject, PRECTL_FID_CACHE lpCache)
{
    if (lpCache->cached) return;
    lpCache->clazz = (*env)->GetObjectClass(env, lpObject);
    lpCache->xLeft = (*env)->GetFieldID(env, lpCache->clazz, "xLeft", "I");
    lpCache->yBottom = (*env)->GetFieldID(env, lpCache->clazz, "yBottom", "I");
    lpCache->xRight = (*env)->GetFieldID(env, lpCache->clazz, "xRight", "I");
    lpCache->yTop = (*env)->GetFieldID(env, lpCache->clazz, "yTop", "I");
    lpCache->cached = 1;
}

PRECTL getRECTLFields(JNIEnv *env, jobject lpObject, PRECTL lpStruct, PRECTL_FID_CACHE lpCache)
{
    if (!lpCache->cached) cacheRECTLFids(env, lpObject, lpCache);
    lpStruct->xLeft = (*env)->GetIntField(env, lpObject, lpCache->xLeft);
    lpStruct->yBottom = (*env)->GetIntField(env, lpObject, lpCache->yBottom);
    lpStruct->xRight= (*env)->GetIntField(env, lpObject, lpCache->xRight);
    lpStruct->yTop= (*env)->GetIntField(env, lpObject, lpCache->yTop);
    return lpStruct;
}

void setRECTLFields(JNIEnv *env, jobject lpObject, PRECTL lpStruct, PRECTL_FID_CACHE lpCache)
{
    if (!lpCache->cached) cacheRECTLFids(env, lpObject, lpCache);
    (*env)->SetIntField(env, lpObject, lpCache->xLeft, lpStruct->xLeft);
    (*env)->SetIntField(env, lpObject, lpCache->yBottom, lpStruct->yBottom);
    (*env)->SetIntField(env, lpObject, lpCache->xRight, lpStruct->xRight);
    (*env)->SetIntField(env, lpObject, lpCache->yTop, lpStruct->yTop);
}

/* FRAMECDATA struct */

void cacheFRAMECDATAFids(JNIEnv *env, jobject lpObject, PFRAMECDATA_FID_CACHE lpCache)
{
    if (lpCache->cached) return;
    lpCache->clazz = (*env)->GetObjectClass(env, lpObject);
    lpCache->flCreateFlags = (*env)->GetFieldID(env, lpCache->clazz, "flCreateFlags", "I");
    lpCache->hmodResources = (*env)->GetFieldID(env, lpCache->clazz, "hmodResources", "S");
    lpCache->idResources = (*env)->GetFieldID(env, lpCache->clazz, "idResources", "S");
    lpCache->cached = 1;
}

PFRAMECDATA getFRAMECDATAFields(JNIEnv *env, jobject lpObject, PFRAMECDATA lpStruct, PFRAMECDATA_FID_CACHE lpCache)
{
    if (!lpCache->cached) cacheFRAMECDATAFids(env, lpObject, lpCache);
    lpStruct->cb = sizeof(FRAMECDATA);
    lpStruct->flCreateFlags = (*env)->GetIntField(env, lpObject, lpCache->flCreateFlags);
    lpStruct->hmodResources = (*env)->GetShortField(env, lpObject, lpCache->hmodResources);
    lpStruct->idResources = (*env)->GetShortField(env, lpObject, lpCache->idResources);
    return lpStruct;
}

void setFRAMECDATAFields(JNIEnv *env, jobject lpObject, PFRAMECDATA lpStruct, PFRAMECDATA_FID_CACHE lpCache)
{
    if (!lpCache->cached) cacheFRAMECDATAFids(env, lpObject, lpCache);
    (*env)->SetIntField(env, lpObject, lpCache->flCreateFlags, lpStruct->flCreateFlags);
    (*env)->SetShortField(env, lpObject, lpCache->hmodResources, lpStruct->hmodResources);
    (*env)->SetShortField(env, lpObject, lpCache->idResources, lpStruct->idResources);
}

/* SWP struct */

void cacheSWPFids(JNIEnv *env, jobject lpObject, PSWP_FID_CACHE lpCache)
{
    if (lpCache->cached) return;
    lpCache->clazz = (*env)->GetObjectClass(env, lpObject);
    lpCache->fl = (*env)->GetFieldID(env, lpCache->clazz, "fl", "I");
    lpCache->cy = (*env)->GetFieldID(env, lpCache->clazz, "cy", "I");
    lpCache->cx = (*env)->GetFieldID(env, lpCache->clazz, "cx", "I");
    lpCache->y = (*env)->GetFieldID(env, lpCache->clazz, "y", "I");
    lpCache->x = (*env)->GetFieldID(env, lpCache->clazz, "x", "I");
    lpCache->hwndInsertBehind = (*env)->GetFieldID(env, lpCache->clazz, "hwndInsertBehind", "I");
    lpCache->hwnd = (*env)->GetFieldID(env, lpCache->clazz, "hwnd", "I");
    lpCache->ulReserved1 = (*env)->GetFieldID(env, lpCache->clazz, "ulReserved1", "I");
    lpCache->ulReserved2 = (*env)->GetFieldID(env, lpCache->clazz, "ulReserved2", "I");
    lpCache->cached = 1;
}

PSWP getSWPFields(JNIEnv *env, jobject lpObject, PSWP lpStruct, PSWP_FID_CACHE lpCache)
{
    if (!lpCache->cached) cacheSWPFids(env, lpObject, lpCache);
    lpStruct->fl = (*env)->GetIntField(env, lpObject, lpCache->fl);
    lpStruct->cy = (*env)->GetIntField(env, lpObject, lpCache->cy);
    lpStruct->cx = (*env)->GetIntField(env, lpObject, lpCache->cx);
    lpStruct->y = (*env)->GetIntField(env, lpObject, lpCache->y);
    lpStruct->x = (*env)->GetIntField(env, lpObject, lpCache->x);
    lpStruct->hwndInsertBehind = (*env)->GetIntField(env, lpObject, lpCache->hwndInsertBehind);
    lpStruct->hwnd = (*env)->GetIntField(env, lpObject, lpCache->hwnd);
    lpStruct->ulReserved1 = (*env)->GetIntField(env, lpObject, lpCache->ulReserved1);
    lpStruct->ulReserved2 = (*env)->GetIntField(env, lpObject, lpCache->ulReserved2);
    return lpStruct;
}

void setSWPFields(JNIEnv *env, jobject lpObject, PSWP lpStruct, PSWP_FID_CACHE lpCache)
{
    if (!lpCache->cached) cacheSWPFids(env, lpObject, lpCache);
    (*env)->SetIntField(env, lpObject, lpCache->fl, lpStruct->fl);
    (*env)->SetIntField(env, lpObject, lpCache->cy, lpStruct->cy);
    (*env)->SetIntField(env, lpObject, lpCache->cx, lpStruct->cx);
    (*env)->SetIntField(env, lpObject, lpCache->y, lpStruct->y);
    (*env)->SetIntField(env, lpObject, lpCache->x, lpStruct->x);
    (*env)->SetIntField(env, lpObject, lpCache->hwndInsertBehind, lpStruct->hwndInsertBehind);
    (*env)->SetIntField(env, lpObject, lpCache->hwnd, lpStruct->hwnd);
    (*env)->SetIntField(env, lpObject, lpCache->ulReserved1, lpStruct->ulReserved1);
    (*env)->SetIntField(env, lpObject, lpCache->ulReserved2, lpStruct->ulReserved2);
}

/* RGNRECT struct */

void cacheRGNRECTFids(JNIEnv *env, jobject lpObject, PRGNRECT_FID_CACHE lpCache)
{
    if (lpCache->cached) return;
    lpCache->clazz = (*env)->GetObjectClass(env, lpObject);
    lpCache->ircStart = (*env)->GetFieldID(env, lpCache->clazz, "ircStart", "I");
    lpCache->crc = (*env)->GetFieldID(env, lpCache->clazz, "crc", "I");
    lpCache->crcReturned = (*env)->GetFieldID(env, lpCache->clazz, "crcReturned", "I");
    lpCache->ulDirection = (*env)->GetFieldID(env, lpCache->clazz, "ulDirection", "I");
    lpCache->cached = 1;
}

PRGNRECT getRGNRECTFields(JNIEnv *env, jobject lpObject, PRGNRECT lpStruct, PRGNRECT_FID_CACHE lpCache)
{
    if (!lpCache->cached) cacheRGNRECTFids(env, lpObject, lpCache);
    lpStruct->ircStart = (*env)->GetIntField(env, lpObject, lpCache->ircStart);
    lpStruct->crc = (*env)->GetIntField(env, lpObject, lpCache->crc);
    lpStruct->crcReturned = (*env)->GetIntField(env, lpObject, lpCache->crcReturned);
    lpStruct->ulDirection = (*env)->GetIntField(env, lpObject, lpCache->ulDirection);
    return lpStruct;
}

void setRGNRECTFields(JNIEnv *env, jobject lpObject, PRGNRECT lpStruct, PRGNRECT_FID_CACHE lpCache)
{
    if (!lpCache->cached) cacheRGNRECTFids(env, lpObject, lpCache);
    (*env)->SetIntField(env, lpObject, lpCache->ircStart, lpStruct->ircStart);
    (*env)->SetIntField(env, lpObject, lpCache->crc, lpStruct->crc);
    (*env)->SetIntField(env, lpObject, lpCache->crcReturned, lpStruct->crcReturned);
    (*env)->SetIntField(env, lpObject, lpCache->ulDirection, lpStruct->ulDirection);
}

/* BITMAPINFOHEADER2 struct */

void cacheBITMAPINFOHEADER2Fids(JNIEnv *env, jobject lpObject, PBITMAPINFOHEADER2_FID_CACHE lpCache)
{
    if (lpCache->cached) return;
    lpCache->clazz = (*env)->GetObjectClass(env, lpObject);
    lpCache->cx = (*env)->GetFieldID(env, lpCache->clazz, "cx", "I");
    lpCache->cy = (*env)->GetFieldID(env, lpCache->clazz, "cy", "I");
    lpCache->cPlanes = (*env)->GetFieldID(env, lpCache->clazz, "cPlanes", "S");
    lpCache->cBitCount = (*env)->GetFieldID(env, lpCache->clazz, "cBitCount", "S");
    lpCache->ulCompression = (*env)->GetFieldID(env, lpCache->clazz, "ulCompression", "I");
    lpCache->cbImage = (*env)->GetFieldID(env, lpCache->clazz, "cbImage", "I");
    lpCache->cxResolution = (*env)->GetFieldID(env, lpCache->clazz, "cxResolution", "I");
    lpCache->cyResolution = (*env)->GetFieldID(env, lpCache->clazz, "cyResolution", "I");
    lpCache->cclrUsed = (*env)->GetFieldID(env, lpCache->clazz, "cclrUsed", "I");
    lpCache->cclrImportant = (*env)->GetFieldID(env, lpCache->clazz, "cclrImportant", "I");
    lpCache->usUnits = (*env)->GetFieldID(env, lpCache->clazz, "usUnits", "S");
    lpCache->usReserved = (*env)->GetFieldID(env, lpCache->clazz, "usReserved", "S");
    lpCache->usRecording = (*env)->GetFieldID(env, lpCache->clazz, "usRecording", "S");
    lpCache->usRendering = (*env)->GetFieldID(env, lpCache->clazz, "usRendering", "S");
    lpCache->cSize1 = (*env)->GetFieldID(env, lpCache->clazz, "cSize1", "I");
    lpCache->cSize2 = (*env)->GetFieldID(env, lpCache->clazz, "cSize2", "I");
    lpCache->ulColorEncoding = (*env)->GetFieldID(env, lpCache->clazz, "ulColorEncoding", "I");
    lpCache->ulIdentifier = (*env)->GetFieldID(env, lpCache->clazz, "ulIdentifier", "I");
    lpCache->cached = 1;
}

PBITMAPINFOHEADER2 getBITMAPINFOHEADER2Fields(JNIEnv *env, jobject lpObject, PBITMAPINFOHEADER2 lpStruct, PBITMAPINFOHEADER2_FID_CACHE lpCache)
{
    if (!lpCache->cached) cacheBITMAPINFOHEADER2Fids(env, lpObject, lpCache);
    lpStruct->cbFix = sizeof(BITMAPINFOHEADER2);
    lpStruct->cx = (*env)->GetIntField(env, lpObject, lpCache->cx);
    lpStruct->cy = (*env)->GetIntField(env, lpObject, lpCache->cy);
    lpStruct->cPlanes = (*env)->GetShortField(env, lpObject, lpCache->cPlanes);
    lpStruct->cBitCount = (*env)->GetShortField(env, lpObject, lpCache->cBitCount);
    lpStruct->ulCompression = (*env)->GetIntField(env, lpObject, lpCache->ulCompression);
    lpStruct->cbImage = (*env)->GetIntField(env, lpObject, lpCache->cbImage);
    lpStruct->cxResolution = (*env)->GetIntField(env, lpObject, lpCache->cxResolution);
    lpStruct->cyResolution = (*env)->GetIntField(env, lpObject, lpCache->cyResolution);
    lpStruct->cclrUsed = (*env)->GetIntField(env, lpObject, lpCache->cclrUsed);
    lpStruct->cclrImportant = (*env)->GetIntField(env, lpObject, lpCache->cclrImportant);
    lpStruct->usUnits = (*env)->GetShortField(env, lpObject, lpCache->usUnits);
    lpStruct->usReserved = (*env)->GetShortField(env, lpObject, lpCache->usReserved);
    lpStruct->usRecording = (*env)->GetShortField(env, lpObject, lpCache->usRecording);
    lpStruct->usRendering = (*env)->GetShortField(env, lpObject, lpCache->usRendering);
    lpStruct->cSize1 = (*env)->GetIntField(env, lpObject, lpCache->cSize1);
    lpStruct->cSize2 = (*env)->GetIntField(env, lpObject, lpCache->cSize2);
    lpStruct->ulColorEncoding = (*env)->GetIntField(env, lpObject, lpCache->ulColorEncoding);
    lpStruct->ulIdentifier = (*env)->GetIntField(env, lpObject, lpCache->ulIdentifier);
    return lpStruct;
}

void setBITMAPINFOHEADER2Fields(JNIEnv *env, jobject lpObject, PBITMAPINFOHEADER2 lpStruct, PBITMAPINFOHEADER2_FID_CACHE lpCache)
{
    if (!lpCache->cached) cacheBITMAPINFOHEADER2Fids(env, lpObject, lpCache);
    (*env)->SetIntField(env, lpObject, lpCache->cx, lpStruct->cx);
    (*env)->SetIntField(env, lpObject, lpCache->cy, lpStruct->cy);
    (*env)->SetShortField(env, lpObject, lpCache->cPlanes, lpStruct->cPlanes);
    (*env)->SetShortField(env, lpObject, lpCache->cBitCount, lpStruct->cBitCount);
    (*env)->SetIntField(env, lpObject, lpCache->ulCompression, lpStruct->ulCompression);
    (*env)->SetIntField(env, lpObject, lpCache->cbImage, lpStruct->cbImage);
    (*env)->SetIntField(env, lpObject, lpCache->cxResolution, lpStruct->cxResolution);
    (*env)->SetIntField(env, lpObject, lpCache->cyResolution, lpStruct->cyResolution);
    (*env)->SetIntField(env, lpObject, lpCache->cclrUsed, lpStruct->cclrUsed);
    (*env)->SetIntField(env, lpObject, lpCache->cclrImportant, lpStruct->cclrImportant);
    (*env)->SetShortField(env, lpObject, lpCache->usUnits, lpStruct->usUnits);
    (*env)->SetShortField(env, lpObject, lpCache->usReserved, lpStruct->usReserved);
    (*env)->SetShortField(env, lpObject, lpCache->usRecording, lpStruct->usRecording);
    (*env)->SetShortField(env, lpObject, lpCache->usRendering, lpStruct->usRendering);
    (*env)->SetIntField(env, lpObject, lpCache->cSize1, lpStruct->cSize1);
    (*env)->SetIntField(env, lpObject, lpCache->cSize2, lpStruct->cSize2);
    (*env)->SetIntField(env, lpObject, lpCache->ulColorEncoding, lpStruct->ulColorEncoding);
    (*env)->SetIntField(env, lpObject, lpCache->ulIdentifier, lpStruct->ulIdentifier);
}

/* POINTERINFO struct */

void cachePOINTERINFOFids(JNIEnv *env, jobject lpObject, PPOINTERINFO_FID_CACHE lpCache)
{
    if (lpCache->cached) return;
    lpCache->clazz = (*env)->GetObjectClass(env, lpObject);
    lpCache->fPointer = (*env)->GetFieldID(env, lpCache->clazz, "fPointer", "I");
    lpCache->xHotSpot = (*env)->GetFieldID(env, lpCache->clazz, "xHotSpot", "I");
    lpCache->yHotSpot = (*env)->GetFieldID(env, lpCache->clazz, "yHotSpot", "I");
    lpCache->hbmPointer = (*env)->GetFieldID(env, lpCache->clazz, "hbmPointer", "I");
    lpCache->hbmColor = (*env)->GetFieldID(env, lpCache->clazz, "hbmColor", "I");
    lpCache->hbmMiniPointer = (*env)->GetFieldID(env, lpCache->clazz, "hbmMiniPointer", "I");
    lpCache->hbmMiniColor = (*env)->GetFieldID(env, lpCache->clazz, "hbmMiniColor", "I");
    lpCache->cached = 1;
}

PPOINTERINFO getPOINTERINFOFields(JNIEnv *env, jobject lpObject, PPOINTERINFO lpStruct, PPOINTERINFO_FID_CACHE lpCache)
{
    if (!lpCache->cached) cachePOINTERINFOFids(env, lpObject, lpCache);
    lpStruct->fPointer = (*env)->GetIntField(env, lpObject, lpCache->fPointer);
    lpStruct->xHotspot = (*env)->GetIntField(env, lpObject, lpCache->xHotSpot);
    lpStruct->yHotspot = (*env)->GetIntField(env, lpObject, lpCache->yHotSpot);
    lpStruct->hbmPointer = (*env)->GetIntField(env, lpObject, lpCache->hbmPointer);
    lpStruct->hbmColor = (*env)->GetIntField(env, lpObject, lpCache->hbmColor);
    lpStruct->hbmMiniPointer = (*env)->GetIntField(env, lpObject, lpCache->hbmMiniPointer);
    lpStruct->hbmMiniColor = (*env)->GetIntField(env, lpObject, lpCache->hbmMiniColor);
    return lpStruct;
}

void setPOINTERINFOFields(JNIEnv *env, jobject lpObject, PPOINTERINFO lpStruct, PPOINTERINFO_FID_CACHE lpCache)
{
    if (!lpCache->cached) cachePOINTERINFOFids(env, lpObject, lpCache);
    (*env)->SetIntField(env, lpObject, lpCache->fPointer, lpStruct->fPointer);
    (*env)->SetIntField(env, lpObject, lpCache->xHotSpot, lpStruct->xHotspot);
    (*env)->SetIntField(env, lpObject, lpCache->yHotSpot, lpStruct->yHotspot);
    (*env)->SetIntField(env, lpObject, lpCache->hbmPointer, lpStruct->hbmPointer);
    (*env)->SetIntField(env, lpObject, lpCache->hbmColor, lpStruct->hbmColor);
    (*env)->SetIntField(env, lpObject, lpCache->hbmMiniPointer, lpStruct->hbmMiniPointer);
    (*env)->SetIntField(env, lpObject, lpCache->hbmMiniColor, lpStruct->hbmMiniColor);
}

/* FATTRS struct */

void cacheFATTRSFids(JNIEnv *env, jobject lpObject, PFATTRS_FID_CACHE lpCache)
{
    if (lpCache->cached) return;
    lpCache->clazz = (*env)->GetObjectClass(env, lpObject);
    lpCache->usRecordLength = (*env)->GetFieldID(env, lpCache->clazz, "usRecordLength", "S");
    lpCache->fsSelection = (*env)->GetFieldID(env, lpCache->clazz, "fsSelection", "S");
    lpCache->lMatch = (*env)->GetFieldID(env, lpCache->clazz, "lMatch", "I");
    lpCache->szFacename = (*env)->GetFieldID(env, lpCache->clazz, "szFacename", "[B");
    lpCache->idRegistry = (*env)->GetFieldID(env, lpCache->clazz, "idRegistry", "S");
    lpCache->usCodePage = (*env)->GetFieldID(env, lpCache->clazz, "usCodePage", "S");
    lpCache->lMaxBaselineExt = (*env)->GetFieldID(env, lpCache->clazz, "lMaxBaselineExt", "I");
    lpCache->lAveCharWidth = (*env)->GetFieldID(env, lpCache->clazz, "lAveCharWidth", "I");
    lpCache->fsType = (*env)->GetFieldID(env, lpCache->clazz, "fsType", "S");
    lpCache->fsFontUse = (*env)->GetFieldID(env, lpCache->clazz, "fsFontUse", "S");
    lpCache->cached = 1;	
}

PFATTRS getFATTRSFields(JNIEnv *env, jobject lpObject, PFATTRS lpStruct, PFATTRS_FID_CACHE lpCache)
{
	if (!lpCache->cached) cacheFATTRSFids(env, lpObject, lpCache);
	lpStruct->usRecordLength = (*env)->GetShortField(env, lpObject, lpCache->usRecordLength);
	lpStruct->fsSelection = (*env)->GetShortField(env, lpObject, lpCache->fsSelection);
	lpStruct->lMatch = (*env)->GetIntField(env, lpObject, lpCache->lMatch);	
	{
		jbyteArray szFacename = (*env)->GetObjectField(env, lpObject, lpCache->szFacename);
        if (szFacename) {		
            jbyte *c_szFacename = (*env)->GetByteArrayElements(env, szFacename, NULL);		
            if (c_szFacename) memcpy(lpStruct->szFacename, c_szFacename, FACESIZE);		
            (*env)->ReleaseByteArrayElements(env, szFacename, c_szFacename, 0);
        }
	}
	lpStruct->idRegistry = (*env)->GetShortField(env, lpObject, lpCache->idRegistry);
	lpStruct->usCodePage = (*env)->GetShortField(env, lpObject, lpCache->usCodePage);
	lpStruct->lMaxBaselineExt = (*env)->GetIntField(env, lpObject, lpCache->lMaxBaselineExt);
	lpStruct->lAveCharWidth = (*env)->GetIntField(env, lpObject, lpCache->lAveCharWidth);
	lpStruct->fsType = (*env)->GetShortField(env, lpObject, lpCache->fsType);
	lpStruct->fsFontUse = (*env)->GetShortField(env, lpObject, lpCache->fsFontUse);
	return lpStruct;
}

void setFATTRSFields(JNIEnv *env, jobject lpObject, PFATTRS lpStruct, PFATTRS_FID_CACHE lpCache)
{
	if (!lpCache->cached) cacheFATTRSFids(env, lpObject, lpCache);
	(*env)->SetShortField(env, lpObject, lpCache->usRecordLength, lpStruct->usRecordLength);
	(*env)->SetShortField(env, lpObject, lpCache->fsSelection, lpStruct->fsSelection);
	(*env)->SetIntField(env, lpObject, lpCache->lMatch, lpStruct->lMatch);
	{
		jbyteArray szFacename = (*env)->GetObjectField(env, lpObject, lpCache->szFacename);
        if (szFacename) {		
            jbyte *c_szFacename = (*env)->GetByteArrayElements(env, szFacename, NULL);		
            if (c_szFacename) memcpy(c_szFacename, lpStruct->szFacename, FACESIZE);		
            (*env)->ReleaseByteArrayElements(env, szFacename, c_szFacename, 0);
        }
	}
	(*env)->SetShortField(env, lpObject, lpCache->idRegistry, lpStruct->idRegistry);
	(*env)->SetShortField(env, lpObject, lpCache->usCodePage, lpStruct->usCodePage);
	(*env)->SetIntField(env, lpObject, lpCache->lMaxBaselineExt, lpStruct->lMaxBaselineExt);
	(*env)->SetIntField(env, lpObject, lpCache->lAveCharWidth, lpStruct->lAveCharWidth);
	(*env)->SetShortField(env, lpObject, lpCache->fsType, lpStruct->fsType);
	(*env)->SetShortField(env, lpObject, lpCache->fsFontUse, lpStruct->fsFontUse);
}

/* FONTMETRICS struct */

void cacheFONTMETRICSFids(JNIEnv *env, jobject lpObject, PFONTMETRICS_FID_CACHE lpCache)
{
	if (lpCache->cached) return;
	lpCache->clazz = (*env)->GetObjectClass(env, lpObject);
	lpCache->szFamilyname = (*env)->GetFieldID(env, lpCache->clazz, "szFamilyname", "[B");
	lpCache->szFacename = (*env)->GetFieldID(env, lpCache->clazz, "szFacename", "[B");
	lpCache->idRegistry = (*env)->GetFieldID(env, lpCache->clazz, "idRegistry", "S");
	lpCache->usCodePage = (*env)->GetFieldID(env, lpCache->clazz, "usCodePage", "S");
	lpCache->lEmHeight = (*env)->GetFieldID(env, lpCache->clazz, "lEmHeight", "I");
	lpCache->lXHeight = (*env)->GetFieldID(env, lpCache->clazz, "lXHeight", "I");
	lpCache->lMaxAscender = (*env)->GetFieldID(env, lpCache->clazz, "lMaxAscender", "I");
	lpCache->lMaxDescender = (*env)->GetFieldID(env, lpCache->clazz, "lMaxDescender", "I");
	lpCache->lLowerCaseAscent = (*env)->GetFieldID(env, lpCache->clazz, "lLowerCaseAscent", "I");
	lpCache->lLowerCaseDescent = (*env)->GetFieldID(env, lpCache->clazz, "lLowerCaseDescent", "I");
	lpCache->lInternalLeading = (*env)->GetFieldID(env, lpCache->clazz, "lInternalLeading", "I");
	lpCache->lExternalLeading = (*env)->GetFieldID(env, lpCache->clazz, "lExternalLeading", "I");
	lpCache->lAveCharWidth = (*env)->GetFieldID(env, lpCache->clazz, "lAveCharWidth", "I");
	lpCache->lMaxCharInc = (*env)->GetFieldID(env, lpCache->clazz, "lMaxCharInc", "I");
	lpCache->lEmInc = (*env)->GetFieldID(env, lpCache->clazz, "lEmInc", "I");
	lpCache->lMaxBaselineExt = (*env)->GetFieldID(env, lpCache->clazz, "lMaxBaselineExt", "I");
	lpCache->sCharSlope = (*env)->GetFieldID(env, lpCache->clazz, "sCharSlope", "S");
	lpCache->sInlineDir = (*env)->GetFieldID(env, lpCache->clazz, "sInlineDir", "S");
	lpCache->sCharRot = (*env)->GetFieldID(env, lpCache->clazz, "sCharRot", "S");
	lpCache->usWeightClass = (*env)->GetFieldID(env, lpCache->clazz, "usWeightClass", "S");
	lpCache->usWidthClass = (*env)->GetFieldID(env, lpCache->clazz, "usWidthClass", "S");
	lpCache->sXDeviceRes = (*env)->GetFieldID(env, lpCache->clazz, "sXDeviceRes", "S");
	lpCache->sYDeviceRes = (*env)->GetFieldID(env, lpCache->clazz, "sYDeviceRes", "S");
	lpCache->sFirstChar = (*env)->GetFieldID(env, lpCache->clazz, "sFirstChar", "S");
	lpCache->sLastChar = (*env)->GetFieldID(env, lpCache->clazz, "sLastChar", "S");
	lpCache->sDefaultChar = (*env)->GetFieldID(env, lpCache->clazz, "sDefaultChar", "S");
	lpCache->sBreakChar = (*env)->GetFieldID(env, lpCache->clazz, "sBreakChar", "S");
	lpCache->sNominalPointSize = (*env)->GetFieldID(env, lpCache->clazz, "sNominalPointSize", "S");
	lpCache->sMinimumPointSize = (*env)->GetFieldID(env, lpCache->clazz, "sMinimumPointSize", "S");
	lpCache->sMaximumPointSize = (*env)->GetFieldID(env, lpCache->clazz, "sMaximumPointSize", "S");
	lpCache->fsType = (*env)->GetFieldID(env, lpCache->clazz, "fsType", "S");
	lpCache->fsDefn = (*env)->GetFieldID(env, lpCache->clazz, "fsDefn", "S");
	lpCache->fsSelection = (*env)->GetFieldID(env, lpCache->clazz, "fsSelection", "S");
	lpCache->fsCapabilities = (*env)->GetFieldID(env, lpCache->clazz, "fsCapabilities", "S");
	lpCache->lSubscriptXSize = (*env)->GetFieldID(env, lpCache->clazz, "lSubscriptXSize", "I");
	lpCache->lSubscriptYSize = (*env)->GetFieldID(env, lpCache->clazz, "lSubscriptYSize", "I");
	lpCache->lSubscriptXOffset = (*env)->GetFieldID(env, lpCache->clazz, "lSubscriptXOffset", "I");
	lpCache->lSubscriptYOffset = (*env)->GetFieldID(env, lpCache->clazz, "lSubscriptYOffset", "I");
	lpCache->lSuperscriptXSize = (*env)->GetFieldID(env, lpCache->clazz, "lSuperscriptXSize", "I");
	lpCache->lSuperscriptYSize = (*env)->GetFieldID(env, lpCache->clazz, "lSuperscriptYSize", "I");
	lpCache->lSuperscriptXOffset = (*env)->GetFieldID(env, lpCache->clazz, "lSuperscriptXOffset", "I");
	lpCache->lSuperscriptYOffset = (*env)->GetFieldID(env, lpCache->clazz, "lSuperscriptYOffset", "I");
	lpCache->lUnderscoreSize = (*env)->GetFieldID(env, lpCache->clazz, "lUnderscoreSize", "I");
	lpCache->lUnderscorePosition = (*env)->GetFieldID(env, lpCache->clazz, "lUnderscorePosition", "I");
	lpCache->lStrikeoutSize = (*env)->GetFieldID(env, lpCache->clazz, "lStrikeoutSize", "I");
	lpCache->lStrikeoutPosition = (*env)->GetFieldID(env, lpCache->clazz, "lStrikeoutPosition", "I");
	lpCache->sKerningPairs = (*env)->GetFieldID(env, lpCache->clazz, "sKerningPairs", "S");
	lpCache->sFamilyClass = (*env)->GetFieldID(env, lpCache->clazz, "sFamilyClass", "S");
	lpCache->lMatch = (*env)->GetFieldID(env, lpCache->clazz, "lMatch", "I");
	lpCache->FamilyNameAtom = (*env)->GetFieldID(env, lpCache->clazz, "FamilyNameAtom", "I");
	lpCache->FaceNameAtom = (*env)->GetFieldID(env, lpCache->clazz, "FaceNameAtom", "I");
    /* PANOSE structure */
	lpCache->panose_bFamilyType = (*env)->GetFieldID(env, lpCache->clazz, "panose_bFamilyType", "B");
	lpCache->panose_bSerifStyle = (*env)->GetFieldID(env, lpCache->clazz, "panose_bSerifStyle", "B");
	lpCache->panose_bWeight = (*env)->GetFieldID(env, lpCache->clazz, "panose_bWeight", "B");
	lpCache->panose_bProportion = (*env)->GetFieldID(env, lpCache->clazz, "panose_bProportion", "B");
	lpCache->panose_bContrast = (*env)->GetFieldID(env, lpCache->clazz, "panose_bContrast", "B");
	lpCache->panose_bStrokeVariation = (*env)->GetFieldID(env, lpCache->clazz, "panose_bStrokeVariation", "B");
	lpCache->panose_bArmStyle = (*env)->GetFieldID(env, lpCache->clazz, "panose_bArmStyle", "B");
	lpCache->panose_bLetterform = (*env)->GetFieldID(env, lpCache->clazz, "panose_bLetterform", "B");
	lpCache->panose_bMidline = (*env)->GetFieldID(env, lpCache->clazz, "panose_bMidline", "B");
	lpCache->panose_bXHeight = (*env)->GetFieldID(env, lpCache->clazz, "panose_bXHeight", "B");
	lpCache->panose_fbPassedISO = (*env)->GetFieldID(env, lpCache->clazz, "panose_fbPassedISO", "B");
	lpCache->panose_fbFailedISO = (*env)->GetFieldID(env, lpCache->clazz, "panose_fbFailedISO", "B");	
    lpCache->cached = 1;	
}

PFONTMETRICS getFONTMETRICSFields(JNIEnv *env, jobject lpObject, PFONTMETRICS lpStruct, PFONTMETRICS_FID_CACHE lpCache)
{
	if (!lpCache->cached) cacheFONTMETRICSFids(env, lpObject, lpCache);
	{
		jbyteArray szFamilyname = (*env)->GetObjectField(env, lpObject, lpCache->szFamilyname);
		jbyteArray szFacename = (*env)->GetObjectField(env, lpObject, lpCache->szFacename);
        if (szFamilyname) {		
            jbyte *c_szFamilyname = (*env)->GetByteArrayElements(env, szFamilyname, NULL);		
            if (c_szFamilyname) memcpy(lpStruct->szFamilyname, c_szFamilyname, FACESIZE);		
            (*env)->ReleaseByteArrayElements(env, szFamilyname, c_szFamilyname, 0);
        }
        if (szFacename) {		
            jbyte *c_szFacename = (*env)->GetByteArrayElements(env, szFacename, NULL);		
            if (c_szFacename) memcpy(lpStruct->szFacename, c_szFacename, FACESIZE);		
            (*env)->ReleaseByteArrayElements(env, szFacename, c_szFacename, 0);
        }
	}
	lpStruct->idRegistry = (*env)->GetShortField(env, lpObject, lpCache->idRegistry);
	lpStruct->usCodePage = (*env)->GetShortField(env, lpObject, lpCache->usCodePage);
	lpStruct->lEmHeight = (*env)->GetIntField(env, lpObject, lpCache->lEmHeight);
	lpStruct->lXHeight = (*env)->GetIntField(env, lpObject, lpCache->lXHeight);
	lpStruct->lMaxAscender = (*env)->GetIntField(env, lpObject, lpCache->lMaxAscender);
	lpStruct->lMaxDescender = (*env)->GetIntField(env, lpObject, lpCache->lMaxDescender);
	lpStruct->lLowerCaseAscent = (*env)->GetIntField(env, lpObject, lpCache->lLowerCaseAscent);
	lpStruct->lLowerCaseDescent = (*env)->GetIntField(env, lpObject, lpCache->lLowerCaseDescent);
	lpStruct->lInternalLeading = (*env)->GetIntField(env, lpObject, lpCache->lInternalLeading);
	lpStruct->lExternalLeading = (*env)->GetIntField(env, lpObject, lpCache->lExternalLeading);
	lpStruct->lAveCharWidth = (*env)->GetIntField(env, lpObject, lpCache->lAveCharWidth);
	lpStruct->lMaxCharInc = (*env)->GetIntField(env, lpObject, lpCache->lMaxCharInc);
	lpStruct->lEmInc = (*env)->GetIntField(env, lpObject, lpCache->lEmInc);
	lpStruct->lMaxBaselineExt = (*env)->GetIntField(env, lpObject, lpCache->lMaxBaselineExt);
	lpStruct->sCharSlope = (*env)->GetShortField(env, lpObject, lpCache->sCharSlope);
	lpStruct->sInlineDir = (*env)->GetShortField(env, lpObject, lpCache->sInlineDir);
	lpStruct->sCharRot = (*env)->GetShortField(env, lpObject, lpCache->sCharRot);
	lpStruct->usWeightClass = (*env)->GetShortField(env, lpObject, lpCache->usWeightClass);
	lpStruct->usWidthClass = (*env)->GetShortField(env, lpObject, lpCache->usWidthClass);
	lpStruct->sXDeviceRes = (*env)->GetShortField(env, lpObject, lpCache->sXDeviceRes);
	lpStruct->sYDeviceRes = (*env)->GetShortField(env, lpObject, lpCache->sYDeviceRes);
	lpStruct->sFirstChar = (*env)->GetShortField(env, lpObject, lpCache->sFirstChar);
	lpStruct->sLastChar = (*env)->GetShortField(env, lpObject, lpCache->sLastChar);
	lpStruct->sDefaultChar = (*env)->GetShortField(env, lpObject, lpCache->sDefaultChar);
	lpStruct->sBreakChar = (*env)->GetShortField(env, lpObject, lpCache->sBreakChar);
	lpStruct->sNominalPointSize = (*env)->GetShortField(env, lpObject, lpCache->sNominalPointSize);
	lpStruct->sMinimumPointSize = (*env)->GetShortField(env, lpObject, lpCache->sMinimumPointSize);
	lpStruct->sMaximumPointSize = (*env)->GetShortField(env, lpObject, lpCache->sMaximumPointSize);
	lpStruct->fsType = (*env)->GetShortField(env, lpObject, lpCache->fsType);
	lpStruct->fsDefn = (*env)->GetShortField(env, lpObject, lpCache->fsDefn);	
	lpStruct->fsSelection = (*env)->GetShortField(env, lpObject, lpCache->fsSelection);
	lpStruct->fsCapabilities = (*env)->GetShortField(env, lpObject, lpCache->fsCapabilities);
	lpStruct->lSubscriptXSize = (*env)->GetIntField(env, lpObject, lpCache->lSubscriptXSize);
	lpStruct->lSubscriptYSize = (*env)->GetIntField(env, lpObject, lpCache->lSubscriptYSize);
	lpStruct->lSubscriptXOffset = (*env)->GetIntField(env, lpObject, lpCache->lSubscriptXOffset);
	lpStruct->lSubscriptYOffset = (*env)->GetIntField(env, lpObject, lpCache->lSubscriptYOffset);
	lpStruct->lSuperscriptXSize = (*env)->GetIntField(env, lpObject, lpCache->lSuperscriptXSize);
	lpStruct->lSuperscriptYSize = (*env)->GetIntField(env, lpObject, lpCache->lSuperscriptYSize);
	lpStruct->lSuperscriptXOffset = (*env)->GetIntField(env, lpObject, lpCache->lSuperscriptXOffset);
	lpStruct->lSuperscriptYOffset = (*env)->GetIntField(env, lpObject, lpCache->lSuperscriptYOffset);
	lpStruct->lUnderscoreSize = (*env)->GetIntField(env, lpObject, lpCache->lUnderscoreSize);
	lpStruct->lUnderscorePosition = (*env)->GetIntField(env, lpObject, lpCache->lUnderscorePosition);
	lpStruct->lStrikeoutSize = (*env)->GetIntField(env, lpObject, lpCache->lStrikeoutSize);
	lpStruct->lStrikeoutPosition = (*env)->GetIntField(env, lpObject, lpCache->lStrikeoutPosition);
	lpStruct->sKerningPairs = (*env)->GetShortField(env, lpObject, lpCache->sKerningPairs);
	lpStruct->sFamilyClass = (*env)->GetShortField(env, lpObject, lpCache->sFamilyClass);
	lpStruct->lMatch = (*env)->GetIntField(env, lpObject, lpCache->lMatch);
	lpStruct->FamilyNameAtom = (*env)->GetIntField(env, lpObject, lpCache->FamilyNameAtom);
	lpStruct->FaceNameAtom = (*env)->GetIntField(env, lpObject, lpCache->FaceNameAtom);
    {
        PANOSE *ppanose = &lpStruct->panose;
        ppanose->bFamilyType = (*env)->GetByteField(env, lpObject, lpCache->panose_bFamilyType);
        ppanose->bSerifStyle = (*env)->GetByteField(env, lpObject, lpCache->panose_bSerifStyle);
        ppanose->bWeight = (*env)->GetByteField(env, lpObject, lpCache->panose_bWeight);
        ppanose->bProportion = (*env)->GetByteField(env, lpObject, lpCache->panose_bProportion);
        ppanose->bContrast = (*env)->GetByteField(env, lpObject, lpCache->panose_bContrast);
        ppanose->bStrokeVariation = (*env)->GetByteField(env, lpObject, lpCache->panose_bStrokeVariation);
        ppanose->bArmStyle = (*env)->GetByteField(env, lpObject, lpCache->panose_bArmStyle);
        ppanose->bLetterform = (*env)->GetByteField(env, lpObject, lpCache->panose_bLetterform);
        ppanose->bMidline = (*env)->GetByteField(env, lpObject, lpCache->panose_bMidline);
        ppanose->bXHeight = (*env)->GetByteField(env, lpObject, lpCache->panose_bXHeight);
        ppanose->fbPassedISO = (*env)->GetByteField(env, lpObject, lpCache->panose_fbPassedISO);
        ppanose->fbFailedISO = (*env)->GetByteField(env, lpObject, lpCache->panose_fbFailedISO);
    }		
	return lpStruct;
}

void setFONTMETRICSFields(JNIEnv *env, jobject lpObject, PFONTMETRICS lpStruct, PFONTMETRICS_FID_CACHE lpCache)
{
	if (!lpCache->cached) cacheFONTMETRICSFids(env, lpObject, lpCache);
	{
		jbyteArray szFamilyname = (*env)->GetObjectField(env, lpObject, lpCache->szFamilyname);
		jbyteArray szFacename = (*env)->GetObjectField(env, lpObject, lpCache->szFacename);
        if (szFamilyname) {		
            jbyte *c_szFamilyname = (*env)->GetByteArrayElements(env, szFamilyname, NULL);		
            if (c_szFamilyname) memcpy(c_szFamilyname, lpStruct->szFamilyname, FACESIZE);		
            (*env)->ReleaseByteArrayElements(env, szFamilyname, c_szFamilyname, 0);
        }
        if (szFacename) {		
            jbyte *c_szFacename = (*env)->GetByteArrayElements(env, szFacename, NULL);		
            if (c_szFacename) memcpy(c_szFacename, lpStruct->szFacename, FACESIZE);		
            (*env)->ReleaseByteArrayElements(env, szFacename, c_szFacename, 0);
        }
	}
	(*env)->SetShortField(env, lpObject, lpCache->idRegistry, lpStruct->idRegistry);
	(*env)->SetShortField(env, lpObject, lpCache->usCodePage, lpStruct->usCodePage);
	(*env)->SetIntField(env, lpObject, lpCache->lEmHeight, lpStruct->lEmHeight);
	(*env)->SetIntField(env, lpObject, lpCache->lXHeight, lpStruct->lXHeight);
	(*env)->SetIntField(env, lpObject, lpCache->lMaxAscender, lpStruct->lMaxAscender);
	(*env)->SetIntField(env, lpObject, lpCache->lMaxDescender, lpStruct->lMaxDescender);
	(*env)->SetIntField(env, lpObject, lpCache->lLowerCaseAscent, lpStruct->lLowerCaseAscent);
	(*env)->SetIntField(env, lpObject, lpCache->lLowerCaseDescent, lpStruct->lLowerCaseDescent);
	(*env)->SetIntField(env, lpObject, lpCache->lInternalLeading, lpStruct->lInternalLeading);
	(*env)->SetIntField(env, lpObject, lpCache->lExternalLeading, lpStruct->lExternalLeading);
	(*env)->SetIntField(env, lpObject, lpCache->lAveCharWidth, lpStruct->lAveCharWidth);
	(*env)->SetIntField(env, lpObject, lpCache->lMaxCharInc, lpStruct->lMaxCharInc);
	(*env)->SetIntField(env, lpObject, lpCache->lEmInc, lpStruct->lEmInc);
	(*env)->SetIntField(env, lpObject, lpCache->lMaxBaselineExt, lpStruct->lMaxBaselineExt);
	(*env)->SetShortField(env, lpObject, lpCache->sCharSlope, lpStruct->sCharSlope);
	(*env)->SetShortField(env, lpObject, lpCache->sInlineDir, lpStruct->sInlineDir);
	(*env)->SetShortField(env, lpObject, lpCache->sCharRot, lpStruct->sCharRot);
	(*env)->SetShortField(env, lpObject, lpCache->usWeightClass, lpStruct->usWeightClass);
	(*env)->SetShortField(env, lpObject, lpCache->usWidthClass, lpStruct->usWidthClass);
	(*env)->SetShortField(env, lpObject, lpCache->sXDeviceRes, lpStruct->sXDeviceRes);
	(*env)->SetShortField(env, lpObject, lpCache->sYDeviceRes, lpStruct->sYDeviceRes);
	(*env)->SetShortField(env, lpObject, lpCache->sFirstChar, lpStruct->sFirstChar);
	(*env)->SetShortField(env, lpObject, lpCache->sLastChar, lpStruct->sLastChar);
	(*env)->SetShortField(env, lpObject, lpCache->sDefaultChar, lpStruct->sDefaultChar);
	(*env)->SetShortField(env, lpObject, lpCache->sBreakChar, lpStruct->sBreakChar);
	(*env)->SetShortField(env, lpObject, lpCache->sNominalPointSize, lpStruct->sNominalPointSize);
	(*env)->SetShortField(env, lpObject, lpCache->sMinimumPointSize, lpStruct->sMinimumPointSize);
	(*env)->SetShortField(env, lpObject, lpCache->sMaximumPointSize, lpStruct->sMaximumPointSize);
	(*env)->SetShortField(env, lpObject, lpCache->fsType, lpStruct->fsType);
	(*env)->SetShortField(env, lpObject, lpCache->fsDefn, lpStruct->fsDefn);	
	(*env)->SetShortField(env, lpObject, lpCache->fsSelection, lpStruct->fsSelection);
	(*env)->SetShortField(env, lpObject, lpCache->fsCapabilities, lpStruct->fsCapabilities);
	(*env)->SetIntField(env, lpObject, lpCache->lSubscriptXSize, lpStruct->lSubscriptXSize);
	(*env)->SetIntField(env, lpObject, lpCache->lSubscriptYSize, lpStruct->lSubscriptYSize);
	(*env)->SetIntField(env, lpObject, lpCache->lSubscriptXOffset, lpStruct->lSubscriptXOffset);
	(*env)->SetIntField(env, lpObject, lpCache->lSubscriptYOffset, lpStruct->lSubscriptYOffset);
	(*env)->SetIntField(env, lpObject, lpCache->lSuperscriptXSize, lpStruct->lSuperscriptXSize);
	(*env)->SetIntField(env, lpObject, lpCache->lSuperscriptYSize, lpStruct->lSuperscriptYSize);
	(*env)->SetIntField(env, lpObject, lpCache->lSuperscriptXOffset, lpStruct->lSuperscriptXOffset);
	(*env)->SetIntField(env, lpObject, lpCache->lSuperscriptYOffset, lpStruct->lSuperscriptYOffset);
	(*env)->SetIntField(env, lpObject, lpCache->lUnderscoreSize, lpStruct->lUnderscoreSize);
	(*env)->SetIntField(env, lpObject, lpCache->lUnderscorePosition, lpStruct->lUnderscorePosition);
	(*env)->SetIntField(env, lpObject, lpCache->lStrikeoutSize, lpStruct->lStrikeoutSize);
	(*env)->SetIntField(env, lpObject, lpCache->lStrikeoutPosition, lpStruct->lStrikeoutPosition);
	(*env)->SetShortField(env, lpObject, lpCache->sKerningPairs, lpStruct->sKerningPairs);
	(*env)->SetShortField(env, lpObject, lpCache->sFamilyClass, lpStruct->sFamilyClass);
	(*env)->SetIntField(env, lpObject, lpCache->lMatch, lpStruct->lMatch);
	(*env)->SetIntField(env, lpObject, lpCache->FamilyNameAtom, lpStruct->FamilyNameAtom);
	(*env)->SetIntField(env, lpObject, lpCache->FaceNameAtom, lpStruct->FaceNameAtom);
    {
        PANOSE *ppanose = &lpStruct->panose;
        (*env)->SetByteField(env, lpObject, lpCache->panose_bFamilyType, ppanose->bFamilyType);
        (*env)->SetByteField(env, lpObject, lpCache->panose_bSerifStyle, ppanose->bSerifStyle);
        (*env)->SetByteField(env, lpObject, lpCache->panose_bWeight, ppanose->bWeight);
        (*env)->SetByteField(env, lpObject, lpCache->panose_bProportion, ppanose->bProportion);
        (*env)->SetByteField(env, lpObject, lpCache->panose_bContrast, ppanose->bContrast);
        (*env)->SetByteField(env, lpObject, lpCache->panose_bStrokeVariation, ppanose->bStrokeVariation);
        (*env)->SetByteField(env, lpObject, lpCache->panose_bArmStyle, ppanose->bArmStyle);
        (*env)->SetByteField(env, lpObject, lpCache->panose_bLetterform, ppanose->bLetterform);
        (*env)->SetByteField(env, lpObject, lpCache->panose_bMidline, ppanose->bMidline);
        (*env)->SetByteField(env, lpObject, lpCache->panose_bXHeight, ppanose->bXHeight);
        (*env)->SetByteField(env, lpObject, lpCache->panose_fbPassedISO, ppanose->fbPassedISO);
        (*env)->SetByteField(env, lpObject, lpCache->panose_fbFailedISO, ppanose->fbFailedISO);
    }
}

/* FONTDLG struct */

void cacheFONTDLGFids(JNIEnv *env, jobject lpObject, PFONTDLG_FID_CACHE lpCache)
{
    if (lpCache->cached)
	    return;
    lpCache->clazz = (*env)->GetObjectClass(env, lpObject);
    lpCache->cbSize = (*env)->GetFieldID(env, lpCache->clazz, "cbSize", "I");
    lpCache->hpsScreen = (*env)->GetFieldID(env, lpCache->clazz, "hpsScreen", "I");
    lpCache->hpsPrinter = (*env)->GetFieldID(env, lpCache->clazz, "hpsPrinter", "I");
    lpCache->pszTitle = (*env)->GetFieldID(env, lpCache->clazz, "pszTitle", "Lorg/eclipse/swt/internal/pm/PSZ;");
    lpCache->pszPreview = (*env)->GetFieldID(env, lpCache->clazz, "pszPreview", "Lorg/eclipse/swt/internal/pm/PSZ;");
    lpCache->pszPtSizeList = (*env)->GetFieldID(env, lpCache->clazz, "pszPtSizeList", "Lorg/eclipse/swt/internal/pm/PSZ;");
    lpCache->pfnDlgProc = (*env)->GetFieldID(env, lpCache->clazz, "pfnDlgProc", "I");
    lpCache->pszFamilyname = (*env)->GetFieldID(env, lpCache->clazz, "pszFamilyname", "Lorg/eclipse/swt/internal/pm/PSZ;");
    lpCache->fxPointSize = (*env)->GetFieldID(env, lpCache->clazz, "fxPointSize", "I");
    lpCache->fl = (*env)->GetFieldID(env, lpCache->clazz, "fl", "I");
    lpCache->flFlags = (*env)->GetFieldID(env, lpCache->clazz, "flFlags", "I");
    lpCache->flType = (*env)->GetFieldID(env, lpCache->clazz, "flType", "I");
    lpCache->flTypeMask = (*env)->GetFieldID(env, lpCache->clazz, "flTypeMask", "I");
    lpCache->flStyle= (*env)->GetFieldID(env, lpCache->clazz, "flStyle", "I");
    lpCache->flStyleMask = (*env)->GetFieldID(env, lpCache->clazz, "flStyleMask", "I");
    lpCache->clrFore = (*env)->GetFieldID(env, lpCache->clazz, "clrFore", "I");
    lpCache->clrBack = (*env)->GetFieldID(env, lpCache->clazz, "clrBack", "I");
    lpCache->ulUser = (*env)->GetFieldID(env, lpCache->clazz, "ulUser", "I");
    lpCache->lReturn = (*env)->GetFieldID(env, lpCache->clazz, "lReturn", "I");
    lpCache->lSRC = (*env)->GetFieldID(env, lpCache->clazz, "lSRC", "I");
    lpCache->lEmHeight = (*env)->GetFieldID(env, lpCache->clazz, "lEmHeight", "I");
    lpCache->lXHeight = (*env)->GetFieldID(env, lpCache->clazz, "lXHeight", "I");
    lpCache->lExternalLeading = (*env)->GetFieldID(env, lpCache->clazz, "lExternalLeading", "I");
    lpCache->hMod = (*env)->GetFieldID(env, lpCache->clazz, "hMod", "I");
    lpCache->fAttrs = (*env)->GetFieldID(env, lpCache->clazz, "fAttrs", "Lorg/eclipse/swt/internal/pm/FATTRS;");
    lpCache->sNominalPointSize = (*env)->GetFieldID(env, lpCache->clazz, "sNominalPointSize", "S");
    lpCache->usWeight = (*env)->GetFieldID(env, lpCache->clazz, "usWeight", "S");
    lpCache->usWidth = (*env)->GetFieldID(env, lpCache->clazz, "usWidth", "S");
    lpCache->x = (*env)->GetFieldID(env, lpCache->clazz, "x", "S");
    lpCache->y = (*env)->GetFieldID(env, lpCache->clazz, "y", "S");
    lpCache->usDlgId = (*env)->GetFieldID(env, lpCache->clazz, "usDlgId", "S");
    lpCache->usFamilyBufLen = (*env)->GetFieldID(env, lpCache->clazz, "usFamilyBufLen", "S");
    lpCache->usReserved = (*env)->GetFieldID(env, lpCache->clazz, "usReserved", "S");
    lpCache->cached = 1;
}

PFONTDLG getFONTDLGFields(JNIEnv *env, jobject lpObject, PFONTDLG lpStruct, PFONTDLG_FID_CACHE lpCache)
{
    if (!lpCache->cached) cacheFONTDLGFids(env, lpObject, lpCache);
    lpStruct->cbSize = (*env)->GetIntField(env, lpObject, lpCache->cbSize);
    lpStruct->hpsScreen = (*env)->GetIntField(env, lpObject, lpCache->hpsScreen);
    lpStruct->hpsPrinter = (*env)->GetIntField(env, lpObject, lpCache->hpsPrinter);
   {
        jobject psz = (*env)->GetObjectField(env, lpObject, lpCache->pszTitle);
        if(psz)
            lpStruct->pszTitle = getPSZBytes(env,psz);
    }
    {
        jobject pszP = (*env)->GetObjectField(env, lpObject, lpCache->pszPreview);
        if(pszP)
            lpStruct->pszPreview = getPSZBytes(env,pszP);
    }
    {
        jobject pszS = (*env)->GetObjectField(env, lpObject, lpCache->pszPtSizeList);
        if (pszS)
            lpStruct->pszPtSizeList = getPSZBytes(env, pszS);
    }
    lpStruct->pfnDlgProc = (*env)->GetIntField(env, lpObject, lpCache->pfnDlgProc);
    {
        jobject pszF = (*env)->GetObjectField(env, lpObject, lpCache->pszFamilyname);
        if(pszF)
            lpStruct->pszFamilyname = getPSZBytes(env, pszF);
    }
    lpStruct->fxPointSize = (*env)->GetIntField(env, lpObject, lpCache->fxPointSize);
    lpStruct->fl = (*env)->GetIntField(env, lpObject, lpCache->fl);
    lpStruct->flFlags = (*env)->GetIntField(env, lpObject, lpCache->flFlags);
    lpStruct->flType = (*env)->GetIntField(env, lpObject, lpCache->flType);
    lpStruct->flTypeMask = (*env)->GetIntField(env, lpObject, lpCache->flTypeMask);
    lpStruct->flStyle = (*env)->GetIntField(env, lpObject, lpCache->flStyle);
    lpStruct->flStyleMask = (*env)->GetIntField(env, lpObject, lpCache->flStyleMask);
    lpStruct->clrFore = (*env)->GetIntField(env, lpObject, lpCache->clrFore);
    lpStruct->clrBack = (*env)->GetIntField(env, lpObject, lpCache->clrBack);
    lpStruct->ulUser = (*env)->GetIntField(env, lpObject, lpCache->ulUser);
    lpStruct->lReturn = (*env)->GetIntField(env, lpObject, lpCache->lReturn);
    lpStruct->lSRC = (*env)->GetIntField(env, lpObject, lpCache->lSRC);
    lpStruct->lEmHeight = (*env)->GetIntField(env, lpObject, lpCache->lEmHeight);
    lpStruct->lXHeight = (*env)->GetIntField(env, lpObject, lpCache->lXHeight);
    lpStruct->lExternalLeading = (*env)->GetIntField(env, lpObject, lpCache->lExternalLeading);
    lpStruct->hMod = (*env)->GetIntField(env, lpObject, lpCache->hMod);
    {
        jobject pfattrs = (*env)->GetObjectField(env, lpObject, lpCache->fAttrs);
        if (pfattrs){
		if(&(lpStruct->fAttrs)){
			getFATTRSFields(env, pfattrs, &(lpStruct->fAttrs), &PGLOB(FATTRSFc));
		}
	}
    }
    lpStruct->usWeight = (*env)->GetShortField(env, lpObject, lpCache->usWeight);
    lpStruct->usWidth= (*env)->GetShortField(env, lpObject, lpCache->usWidth);
    lpStruct->x = (*env)->GetShortField(env, lpObject, lpCache->x);
    lpStruct->y = (*env)->GetShortField(env, lpObject, lpCache->y);
    lpStruct->usDlgId = (*env)->GetShortField(env, lpObject, lpCache->usDlgId);
    lpStruct->usFamilyBufLen = (*env)->GetShortField(env, lpObject, lpCache->usFamilyBufLen);
    lpStruct->usReserved = (*env)->GetIntField(env, lpObject, lpCache->usReserved);
    return lpStruct;
}

void setFONTDLGFields(JNIEnv *env, jobject lpObject, PFONTDLG lpStruct, PFONTDLG_FID_CACHE lpCache)
{
    if (!lpCache->cached) cacheFONTDLGFids(env, lpObject, lpCache);
    (*env)->SetIntField(env, lpObject, lpCache->cbSize, lpStruct->cbSize);
    (*env)->SetIntField(env, lpObject, lpCache->hpsScreen, lpStruct->hpsScreen);
    (*env)->SetIntField(env, lpObject, lpCache->hpsPrinter, lpStruct->hpsPrinter);
    {
        jobject pszTitle = (*env)->GetObjectField(env, lpObject, lpCache->pszTitle);
        if (pszTitle)
            releasePSZBytes(env, pszTitle, lpStruct->pszTitle);
    }
    {
        jobject pszPreview = (*env)->GetObjectField(env, lpObject, lpCache->pszPreview);
        if(pszPreview)
            releasePSZBytes(env, pszPreview, lpStruct->pszPreview);
    }

    {
        jobject psz = (*env)->GetObjectField(env, lpObject, lpCache->pszPtSizeList);
        jbyteArray pszPtSizeList = getPSZArray(env,psz);
        if (pszPtSizeList){
            jbyte *c_pszPtSizeList = (*env)->GetByteArrayElements(env, pszPtSizeList, NULL);
            if(c_pszPtSizeList){
                (*env)->ReleaseByteArrayElements(env, pszPtSizeList, c_pszPtSizeList, 0);
            }
        }
    }

    (*env)->SetIntField(env, lpObject, lpCache->pfnDlgProc, lpStruct->pfnDlgProc);

    {
        jobject psz = (*env)->GetObjectField(env, lpObject, lpCache->pszFamilyname);
        jbyteArray pszFamilyname = getPSZArray(env,psz);
       if (pszFamilyname) {
	    jbyte *c_pszFamilyname = (*env)->GetByteArrayElements(env, pszFamilyname, NULL);
            if (c_pszFamilyname)
		    memcpy(c_pszFamilyname, lpStruct->pszFamilyname, FACESIZE);		
            (*env)->ReleaseByteArrayElements(env, pszFamilyname, c_pszFamilyname, 0);
       }
    }

    (*env)->SetIntField(env, lpObject, lpCache->fxPointSize, (FIXEDINT(lpStruct->fxPointSize)));
    (*env)->SetIntField(env, lpObject, lpCache->fl, lpStruct->fl);
    (*env)->SetIntField(env, lpObject, lpCache->flFlags, lpStruct->flFlags);
    (*env)->SetIntField(env, lpObject, lpCache->flType, lpStruct->flType);
    (*env)->SetIntField(env, lpObject, lpCache->flTypeMask, lpStruct->flTypeMask);
    (*env)->SetIntField(env, lpObject, lpCache->flStyle, lpStruct->flStyle);
    (*env)->SetIntField(env, lpObject, lpCache->flStyleMask, lpStruct->flStyleMask);
    (*env)->SetIntField(env, lpObject, lpCache->clrFore, lpStruct->clrFore);
    (*env)->SetIntField(env, lpObject, lpCache->clrBack, lpStruct->clrBack);
    (*env)->SetIntField(env, lpObject, lpCache->ulUser, lpStruct->ulUser);
    (*env)->SetIntField(env, lpObject, lpCache->lReturn, lpStruct->lReturn);
    (*env)->SetIntField(env, lpObject, lpCache->lSRC, lpStruct->lSRC);
    (*env)->SetIntField(env, lpObject, lpCache->lEmHeight, lpStruct->lEmHeight);
    (*env)->SetIntField(env, lpObject, lpCache->lXHeight, lpStruct->lXHeight);
    (*env)->SetIntField(env, lpObject, lpCache->lExternalLeading, lpStruct->lExternalLeading);
    (*env)->SetIntField(env, lpObject, lpCache->hMod, lpStruct->hMod);
    {
        jobject pfattrs = (*env)->GetObjectField(env, lpObject, lpCache->fAttrs);
        if (pfattrs && &(lpStruct->fAttrs))
		setFATTRSFields(env, pfattrs, &(lpStruct->fAttrs), &PGLOB(FATTRSFc));
    }
    (*env)->SetShortField(env, lpObject, lpCache->sNominalPointSize, lpStruct->sNominalPointSize);
    (*env)->SetShortField(env, lpObject, lpCache->usWeight, lpStruct->usWeight);
    (*env)->SetShortField(env, lpObject, lpCache->usWidth, lpStruct->usWidth);
    (*env)->SetShortField(env, lpObject, lpCache->x, lpStruct->x);
    (*env)->SetShortField(env, lpObject, lpCache->y, lpStruct->y);
    (*env)->SetShortField(env, lpObject, lpCache->usDlgId, lpStruct->usDlgId);
    (*env)->SetShortField(env, lpObject, lpCache->usFamilyBufLen, lpStruct->usFamilyBufLen);
    (*env)->SetShortField(env, lpObject, lpCache->usReserved, lpStruct->usReserved);
}

void cacheFILEDLGFids(JNIEnv *env, jobject lpObject, PFILEDLG_FID_CACHE lpCache)
{
    if (lpCache->cached) return;
    lpCache->clazz = (*env)->GetObjectClass(env, lpObject);
    lpCache->fl = (*env)->GetFieldID(env, lpCache->clazz, "fl", "I");
    lpCache->ulUser = (*env)->GetFieldID(env, lpCache->clazz, "ulUser", "I");
    lpCache->lReturn = (*env)->GetFieldID(env, lpCache->clazz, "lReturn", "I");
    lpCache->lSRC = (*env)->GetFieldID(env, lpCache->clazz, "lSRC", "I");
    lpCache->pszTitle = (*env)->GetFieldID(env, lpCache->clazz, "pszTitle", "Lorg/eclipse/swt/internal/pm/PSZ;");
    lpCache->pszOKButton = (*env)->GetFieldID(env, lpCache->clazz, "pszOKButton", "Lorg/eclipse/swt/internal/pm/PSZ;");
    lpCache->pfnDlgProc = (*env)->GetFieldID(env, lpCache->clazz, "pfnDlgProc", "I");
    lpCache->pszIType = (*env)->GetFieldID(env, lpCache->clazz, "pszIType", "Lorg/eclipse/swt/internal/pm/PSZ;");
    lpCache->papszITypeList = (*env)->GetFieldID(env, lpCache->clazz, "papszITypeList", "[Lorg/eclipse/swt/internal/pm/PSZ;");
    lpCache->pszIDrive = (*env)->GetFieldID(env, lpCache->clazz, "pszIDrive", "Lorg/eclipse/swt/internal/pm/PSZ;");
    lpCache->papszIDriveList = (*env)->GetFieldID(env, lpCache->clazz, "papszIDriveList", "[Lorg/eclipse/swt/internal/pm/PSZ;");
    lpCache->hMod = (*env)->GetFieldID(env, lpCache->clazz, "hMod", "I");
    lpCache->szFullFile = (*env)->GetFieldID(env, lpCache->clazz, "szFullFile", "[B");
    lpCache->papszFQFilename = (*env)->GetFieldID(env, lpCache->clazz, "papszFQFilename", "[Lorg/eclipse/swt/internal/pm/PSZ;");
    lpCache->ulFQFCount = (*env)->GetFieldID(env, lpCache->clazz, "ulFQFCount", "I");
    lpCache->usDlgId = (*env)->GetFieldID(env, lpCache->clazz, "usDlgId", "S");
    lpCache->x = (*env)->GetFieldID(env, lpCache->clazz, "x", "S");
    lpCache->y = (*env)->GetFieldID(env, lpCache->clazz, "y", "S");
    lpCache->sEAType = (*env)->GetFieldID(env, lpCache->clazz, "sEAType", "S");
    lpCache->cached = 1;
}

PFILEDLG getFILEDLGFields(JNIEnv *env, jobject lpObject, PFILEDLG lpStruct, PFILEDLG_FID_CACHE lpCache)
{
    DEBUG_CALL("getFILEDLGFields\n")
    if (!lpCache->cached) cacheFILEDLGFids(env, lpObject, lpCache);
    lpStruct->cbSize = sizeof(FILEDLG);
    lpStruct->fl = (*env)->GetIntField(env, lpObject, lpCache->fl);
    lpStruct->ulUser = (*env)->GetIntField(env, lpObject, lpCache->ulUser);
    lpStruct->lReturn = (*env)->GetIntField(env, lpObject, lpCache->lReturn);
    lpStruct->lSRC = (*env)->GetIntField(env, lpObject, lpCache->lSRC);
    {
        jobject pszTitle = (*env)->GetObjectField(env, lpObject, lpCache->pszTitle);
        jobject pszOKButton = (*env)->GetObjectField(env, lpObject, lpCache->pszOKButton);
        lpStruct->pszTitle = NULL;
        if (pszTitle)
            lpStruct->pszTitle = getPSZBytes(env, pszTitle);
        lpStruct->pszOKButton = NULL;
        if (pszOKButton)
            lpStruct->pszOKButton = getPSZBytes(env, pszOKButton);
    }
    lpStruct->pfnDlgProc = (PFNWP)(*env)->GetIntField(env, lpObject, lpCache->pfnDlgProc);
    {
        jobject pszIType = (*env)->GetObjectField(env, lpObject, lpCache->pszIType);
        jobjectArray papszITypeList = (*env)->GetObjectField(env, lpObject, lpCache->papszITypeList);
        jobject pszIDrive = (*env)->GetObjectField(env, lpObject, lpCache->pszIDrive);
        jobjectArray papszIDriveList = (*env)->GetObjectField(env, lpObject, lpCache->papszIDriveList);
        lpStruct->pszIType = NULL;
        if (pszIType) {
            lpStruct->pszIType = getPSZBytes(env, pszIType);
        }
        lpStruct->papszITypeList = NULL;
        if (papszITypeList) {
            lpStruct->papszITypeList = getPAPSZArray(env, papszITypeList);
        }
        lpStruct->pszIDrive = NULL;
        if (pszIDrive) {
            DEBUG_CALL("getFILEDLGFields:pszIDrive bytes.\n")
            lpStruct->pszIDrive = getPSZBytes(env, pszIDrive);
        }
        lpStruct->papszIDriveList = NULL;
        if (papszIDriveList) {
            DEBUG_CALL("getFILEDLGFields:papszIDriveList array.\n")
            lpStruct->papszIDriveList = getPAPSZArray(env, papszIDriveList);
        }
    }
    lpStruct->hMod = (*env)->GetIntField(env, lpObject, lpCache->hMod);
    {
        jbyteArray szFullFile = (*env)->GetObjectField(env, lpObject, lpCache->szFullFile);
        jobjectArray papszFQFilenameList = (*env)->GetObjectField(env, lpObject, lpCache->papszFQFilename); 
        if (szFullFile) {
            jbyte *c_szFullFile = (*env)->GetByteArrayElements(env, szFullFile, NULL);
            if (c_szFullFile) memcpy(lpStruct->szFullFile, c_szFullFile, CCHMAXPATH);
            (*env)->ReleaseByteArrayElements(env, szFullFile, c_szFullFile, 0);
        }
/*        lpStruct->papszFQFilename = NULL; */
        if (papszFQFilenameList) {
            DEBUG_CALL("getFILEDLGFields:papszFQFilenameList array.\n")
            lpStruct->papszFQFilename = getPAPSZArray(env, papszFQFilenameList);
        }

    }
    lpStruct->ulFQFCount = (*env)->GetIntField(env, lpObject, lpCache->ulFQFCount);
    lpStruct->usDlgId = (*env)->GetShortField(env, lpObject, lpCache->usDlgId);
    lpStruct->x = (*env)->GetShortField(env, lpObject, lpCache->x);
    lpStruct->y = (*env)->GetShortField(env, lpObject, lpCache->y);
    lpStruct->sEAType = (*env)->GetShortField(env, lpObject, lpCache->sEAType);
    return lpStruct;
}

void setFILEDLGFields(JNIEnv *env, jobject lpObject, PFILEDLG lpStruct, PFILEDLG_FID_CACHE lpCache)
{
    DEBUG_CALL("setFILEDLGFields\n")
    if (!lpCache->cached) cacheFILEDLGFids(env, lpObject, lpCache);
    (*env)->SetIntField(env, lpObject, lpCache->fl, lpStruct->fl);
    (*env)->SetIntField(env, lpObject, lpCache->ulUser, lpStruct->ulUser);
    (*env)->SetIntField(env, lpObject, lpCache->lReturn, lpStruct->lReturn);
    (*env)->SetIntField(env, lpObject, lpCache->lSRC, lpStruct->lSRC);
    DEBUG_CALL("ha1\n")
    {
        jobject pszTitle = (*env)->GetObjectField(env, lpObject, lpCache->pszTitle);
        jobject pszOKButton = (*env)->GetObjectField(env, lpObject, lpCache->pszOKButton);
        if (pszTitle)
            releasePSZBytes(env, pszTitle, lpStruct->pszTitle);
        if (pszOKButton)
            releasePSZBytes(env, pszOKButton, lpStruct->pszOKButton);
    }
    (*env)->SetIntField(env, lpObject, lpCache->pfnDlgProc, (jint)lpStruct->pfnDlgProc);
    DEBUG_CALL("ha2\n")
    {
        jobject pszIType = (*env)->GetObjectField(env, lpObject, lpCache->pszIType);
        jobjectArray papszITypeList = (*env)->GetObjectField(env, lpObject, lpCache->papszITypeList);
        jobject pszIDrive = (*env)->GetObjectField(env, lpObject, lpCache->pszIDrive);
        jobjectArray papszIDriveList = (*env)->GetObjectField(env, lpObject, lpCache->papszIDriveList);
        if (pszIType) {
            releasePSZBytes(env, pszIType, lpStruct->pszIType);
        }
        if (papszITypeList) {
            releasePAPSZArray(env, papszITypeList, lpStruct->papszITypeList);
        }
        if (pszIDrive) {
            DEBUG_CALL("setFILEDLGFields:pszIDrive bytes.\n")
            releasePSZBytes(env, pszIDrive, lpStruct->pszIDrive);
        }
        if (papszIDriveList) {
            DEBUG_CALL("setFILEDLGFields:papszIDriveList array.\n")
            releasePAPSZArray(env, papszIDriveList, lpStruct->papszIDriveList);
        }
    }
    DEBUG_CALL("ha3\n")
    (*env)->SetIntField(env, lpObject, lpCache->hMod, lpStruct->hMod);
    {
        jobjectArray test;
        jbyteArray szFullFile = (*env)->GetObjectField(env, lpObject, lpCache->szFullFile);
        jobjectArray papszFQFilenameList = (*env)->GetObjectField(env, lpObject, lpCache->papszFQFilename);
        if (szFullFile) {
            jbyte *c_szFullFile = (*env)->GetByteArrayElements(env, szFullFile, NULL);
            if (c_szFullFile) memcpy(c_szFullFile, lpStruct->szFullFile, CCHMAXPATH);
            (*env)->ReleaseByteArrayElements(env, szFullFile, c_szFullFile, 0);
        }
        if (lpStruct->fl & FDS_MULTIPLESEL) {
            DEBUG_CALL("setFILEDLGFields:papszFQFilenameList array.\n")
            if(papszFQFilenameList == NULL){
                jobject pszTitle = (*env)->GetObjectField(env, lpObject, lpCache->pszTitle);
                papszFQFilenameList=(*env)->NewObjectArray(env, lpStruct->ulFQFCount, (*env)->FindClass(env,"org/eclipse/swt/internal/pm/PSZ"), NULL);
            }
            (*env)->SetObjectField(env, lpObject, lpCache->papszFQFilename, papszFQFilenameList);
            releasePAPSZArray(env, papszFQFilenameList, lpStruct->papszFQFilename);
        }
    }
     DEBUG_CALL("ha4\n")
    (*env)->SetIntField(env, lpObject, lpCache->ulFQFCount, lpStruct->ulFQFCount);
    (*env)->SetShortField(env, lpObject, lpCache->usDlgId, lpStruct->usDlgId);
    (*env)->SetShortField(env, lpObject, lpCache->x, lpStruct->x);
    (*env)->SetShortField(env, lpObject, lpCache->y, lpStruct->y);
    (*env)->SetShortField(env, lpObject, lpCache->sEAType, lpStruct->sEAType);
    DEBUG_CALL("ha5\n")
}
/* LBOXINFO struct */
void cacheLBOXINFOFids(JNIEnv *env, jobject lpObject, PLBOXINFO_FID_CACHE lpCache)
{
    if (lpCache->cached) return;
    lpCache->clazz = (*env)->GetObjectClass(env, lpObject);
    lpCache->lItemIndex = (*env)->GetFieldID(env, lpCache->clazz, "lItemIndex", "I");
    lpCache->ulItemCount = (*env)->GetFieldID(env, lpCache->clazz, "ulItemCount", "I");
    lpCache->reserved = (*env)->GetFieldID(env, lpCache->clazz, "reserved", "I");
    lpCache->reserved2 = (*env)->GetFieldID(env, lpCache->clazz, "reserved2", "I");
    lpCache->cached = 1;
}

PLBOXINFO getLBOXINFOFields(JNIEnv *env, jobject lpObject, PLBOXINFO lpStruct, PLBOXINFO_FID_CACHE lpCache)
{
    if (!lpCache->cached) cacheLBOXINFOFids(env, lpObject, lpCache);
    lpStruct->lItemIndex = (*env)->GetIntField(env, lpObject, lpCache->lItemIndex);
    lpStruct->ulItemCount = (*env)->GetIntField(env, lpObject, lpCache->ulItemCount);
    lpStruct->reserved = (*env)->GetIntField(env, lpObject, lpCache->reserved);
    lpStruct->reserved2 = (*env)->GetIntField(env, lpObject, lpCache->reserved2);
    return lpStruct;
}

void setLBOXINFOFields(JNIEnv *env, jobject lpObject, PLBOXINFO lpStruct, PLBOXINFO_FID_CACHE lpCache)
{
    if (!lpCache->cached) cacheLBOXINFOFids(env, lpObject, lpCache);
    (*env)->SetIntField(env, lpObject, lpCache->lItemIndex, lpStruct->lItemIndex);
    (*env)->SetIntField(env, lpObject, lpCache->ulItemCount, lpStruct->ulItemCount);
    (*env)->SetIntField(env, lpObject, lpCache->reserved, lpStruct->reserved);
    (*env)->SetIntField(env, lpObject, lpCache->reserved2, lpStruct->reserved2);
}
/* SBCDATA struct */
void cacheSBCDATAFids(JNIEnv *env, jobject lpObject, PSBCDATA_FID_CACHE lpCache)
{
    if (lpCache->cached) return;
    lpCache->clazz = (*env)->GetObjectClass(env, lpObject);
    lpCache->cb = (*env)->GetFieldID(env, lpCache->clazz, "cb", "S");
    lpCache->sHilite = (*env)->GetFieldID(env, lpCache->clazz, "sHilite", "S");
    lpCache->posFirst = (*env)->GetFieldID(env, lpCache->clazz, "posFirst", "S");
    lpCache->posLast = (*env)->GetFieldID(env, lpCache->clazz, "posLast", "S");
    lpCache->posThumb = (*env)->GetFieldID(env, lpCache->clazz, "posThumb", "S");
    lpCache->cVisible = (*env)->GetFieldID(env, lpCache->clazz, "cVisible", "S");
    lpCache->cTotal = (*env)->GetFieldID(env, lpCache->clazz, "cTotal", "S");
    lpCache->cached = 1;
}

PSBCDATA getSBCDATAFields(JNIEnv *env, jobject lpObject, PSBCDATA lpStruct, PSBCDATA_FID_CACHE lpCache)
{
    if (!lpCache->cached) cacheSBCDATAFids(env, lpObject, lpCache);
    lpStruct->cb = (*env)->GetShortField(env, lpObject, lpCache->cb);
    lpStruct->sHilite = (*env)->GetShortField(env, lpObject, lpCache->sHilite);
    lpStruct->posFirst = (*env)->GetShortField(env, lpObject, lpCache->posFirst);
    lpStruct->posLast = (*env)->GetShortField(env, lpObject, lpCache->posLast);
    lpStruct->posThumb = (*env)->GetShortField(env, lpObject, lpCache->posThumb);
    lpStruct->cVisible = (*env)->GetShortField(env, lpObject, lpCache->cVisible);
    lpStruct->cTotal = (*env)->GetShortField(env, lpObject, lpCache->cTotal);
    return lpStruct;
}

void setSBCDATAFields(JNIEnv *env, jobject lpObject, PSBCDATA lpStruct, PSBCDATA_FID_CACHE lpCache)
{
    if (!lpCache->cached) cacheSBCDATAFids(env, lpObject, lpCache);
    (*env)->SetShortField(env, lpObject, lpCache->cb, lpStruct->cb);
    (*env)->SetShortField(env, lpObject, lpCache->sHilite, lpStruct->sHilite);
    (*env)->SetShortField(env, lpObject, lpCache->posFirst, lpStruct->posFirst);
    (*env)->SetShortField(env, lpObject, lpCache->posLast, lpStruct->posLast);
    (*env)->SetShortField(env, lpObject, lpCache->posThumb, lpStruct->posThumb);
    (*env)->SetShortField(env, lpObject, lpCache->cVisible, lpStruct->cVisible);
    (*env)->SetShortField(env, lpObject, lpCache->cTotal, lpStruct->cTotal);
}

/* FACENAMEDESC struct */
/*@@TODO (dmik): don't need, remove!
void cacheFACENAMEDESCFids(JNIEnv *env, jobject lpObject, PFACENAMEDESC_FID_CACHE lpCache)
{
    if (lpCache->cached) return;
    lpCache->clazz = (*env)->GetObjectClass(env, lpObject);
    lpCache->usSize = (*env)->GetFieldID(env, lpCache->clazz, "usSize", "S");
    lpCache->usWeightClass = (*env)->GetFieldID(env, lpCache->clazz, "usWeightClass", "S");
    lpCache->usWidthClass = (*env)->GetFieldID(env, lpCache->clazz, "usWidthClass", "S");
    lpCache->usReserved = (*env)->GetFieldID(env, lpCache->clazz, "usReserved", "S");
    lpCache->flOptions = (*env)->GetFieldID(env, lpCache->clazz, "flOptions", "I");
    lpCache->cached = 1;
}

PFACENAMEDESC getFACENAMEDESCFields(JNIEnv *env, jobject lpObject, PFACENAMEDESC lpStruct, PFACENAMEDESC_FID_CACHE lpCache)
{
    if (!lpCache->cached) cacheFACENAMEDESCFids(env, lpObject, lpCache);
    lpStruct->usSize = (*env)->GetShortField(env, lpObject, lpCache->usSize);
    lpStruct->usWeightClass = (*env)->GetShortField(env, lpObject, lpCache->usWeightClass);
    lpStruct->usWidthClass = (*env)->GetShortField(env, lpObject, lpCache->usWidthClass);
    lpStruct->usReserved = (*env)->GetShortField(env, lpObject, lpCache->usReserved);
    lpStruct->flOptions = (*env)->GetIntField(env, lpObject, lpCache->flOptions);
    return lpStruct;
}

void setFACENAMEDESCFields(JNIEnv *env, jobject lpObject, PFACENAMEDESC lpStruct, PFACENAMEDESC_FID_CACHE lpCache)
{
    if (!lpCache->cached) cacheFACENAMEDESCFids(env, lpObject, lpCache);
    (*env)->SetShortField(env, lpObject, lpCache->usSize, lpStruct->usSize);
    (*env)->SetShortField(env, lpObject, lpCache->usWeightClass, lpStruct->usWeightClass);
    (*env)->SetShortField(env, lpObject, lpCache->usWidthClass, lpStruct->usWidthClass);
    (*env)->SetShortField(env, lpObject, lpCache->usReserved, lpStruct->usReserved);
    (*env)->SetIntField(env, lpObject, lpCache->flOptions, lpStruct->flOptions);
}
*/

