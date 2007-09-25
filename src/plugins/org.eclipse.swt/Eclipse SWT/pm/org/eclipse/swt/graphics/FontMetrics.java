package org.eclipse.swt.graphics;

/*
 *  OS/2 version.
 *  Copyright (c) 2002, 2004 EclipseOS2 Team.
 */
 
/*
 *  Copyright (c) 2000, 2002 IBM Corp.  All rights reserved.
 *  This file is made available under the terms of the Common Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/cpl-v10.html
 */

import org.eclipse.swt.internal.pm.*;

import java.util.Arrays;

/**
 * Instances of this class provide measurement information
 * about fonts including ascent, descent, height, leading
 * space between rows, and average character width.
 * <code>FontMetrics</code> are obtained from <code>GC</code>s
 * using the <code>getFontMetrics()</code> method.
 *
 * @see GC#getFontMetrics
 */

public final class FontMetrics {

	/**
	 * On Windows, handle is a Win32 TEXTMETRIC struct
	 * On OS/2, handle is an OS/2 FONTMETRICS struct
	 * On Photon, handle is a Photon FontQueryInfo struct
	 * (Warning: This field is platform dependent)
	 */
	public FONTMETRICS handle;


/**
 * Prevents instances from being created outside the package.
 */
FontMetrics() {
}

/**
 * Compares the argument to the receiver, and returns true
 * if they represent the <em>same</em> object using a class
 * specific comparison.
 *
 * @param object the object to compare with this object
 * @return <code>true</code> if the object is the same as this object and <code>false</code> otherwise
 *
 * @see #hashCode
 */
public boolean equals (Object object) {
    if (object == this) return true;
    if (!(object instanceof FontMetrics)) return false;
    FONTMETRICS metric = ((FontMetrics)object).handle;
    return
        Arrays.equals (handle.szFamilyname, metric.szFamilyname) &&
        Arrays.equals (handle.szFacename, metric.szFacename) &&
        handle.idRegistry == metric.idRegistry &&
        handle.usCodePage == metric.usCodePage &&
        handle.lEmHeight == metric.lEmHeight &&
        handle.lXHeight == metric.lXHeight &&
        handle.lMaxAscender == metric.lMaxAscender &&
        handle.lMaxDescender == metric.lMaxDescender &&
        handle.lLowerCaseAscent == metric.lLowerCaseAscent &&
        handle.lLowerCaseDescent == metric.lLowerCaseDescent &&
        handle.lInternalLeading == metric.lInternalLeading &&
        handle.lExternalLeading == metric.lExternalLeading &&
        handle.lAveCharWidth == metric.lAveCharWidth &&
        handle.lMaxCharInc == metric.lMaxCharInc &&
        handle.lEmInc == metric.lEmInc &&
        handle.lMaxBaselineExt == metric.lMaxBaselineExt &&
        handle.sCharSlope == metric.sCharSlope &&
        handle.sInlineDir == metric.sInlineDir &&
        handle.sCharRot == metric.sCharRot &&
        handle.usWeightClass == metric.usWeightClass &&
        handle.usWidthClass == metric.usWidthClass &&
        handle.sXDeviceRes == metric.sXDeviceRes &&
        handle.sYDeviceRes == metric.sYDeviceRes &&
//        handle.sFirstChar == metric.sFirstChar &&
//        handle.sLastChar == metric.sLastChar &&
//        handle.sDefaultChar == metric.sDefaultChar &&
//        handle.sBreakChar == metric.sBreakChar &&
        handle.sNominalPointSize == metric.sNominalPointSize &&
        handle.sMinimumPointSize == metric.sMinimumPointSize &&
        handle.sMaximumPointSize == metric.sMaximumPointSize &&
        handle.fsType == metric.fsType &&
        handle.fsDefn == metric.fsDefn &&
        handle.fsSelection == metric.fsSelection &&
        handle.fsCapabilities == metric.fsCapabilities &&
//        handle.lSubscriptXSize == metric.lSubscriptXSize &&
//        handle.lSubscriptYSize == metric.lSubscriptYSize &&
//        handle.lSubscriptXOffset == metric.lSubscriptXOffset &&
//        handle.lSubscriptYOffset == metric.lSubscriptYOffset &&
//        handle.lSuperscriptXSize == metric.lSuperscriptXSize &&
//        handle.lSuperscriptYSize == metric.lSuperscriptYSize &&
//        handle.lSuperscriptXOffset == metric.lSuperscriptXOffset &&
//        handle.lSuperscriptYOffset == metric.lSuperscriptYOffset &&
        handle.lUnderscoreSize == metric.lUnderscoreSize &&
        handle.lUnderscorePosition == metric.lUnderscorePosition &&
        handle.lStrikeoutSize == metric.lStrikeoutSize &&
        handle.lStrikeoutPosition == metric.lStrikeoutPosition &&
        handle.sKerningPairs == metric.sKerningPairs &&
        handle.sFamilyClass == metric.sFamilyClass &&
        handle.lMatch == metric.lMatch &&
        handle.FamilyNameAtom == metric.FamilyNameAtom &&
        handle.FaceNameAtom == metric.FaceNameAtom;
//        handle.panose_bFamilyType == metric.panose_bFamilyType &&
//        handle.panose_bSerifStyle == metric.panose_bSerifStyle &&
//        handle.panose_bWeight == metric.panose_bWeight &&
//        handle.panose_bProportion == metric.panose_bProportion &&
//        handle.panose_bContrast == metric.panose_bContrast &&
//        handle.panose_bStrokeVariation == metric.panose_bStrokeVariation &&
//        handle.panose_bArmStyle == metric.panose_bArmStyle &&
//        handle.panose_bLetterform == metric.panose_bLetterform &&
//        handle.panose_bMidline == metric.panose_bMidline &&
//        handle.panose_bXHeight == metric.panose_bXHeight &&
//        handle.panose_fbPassedISO == metric.panose_fbPassedISO &&
//        handle.panose_fbFailedISO == metric.panose_fbFailedISO;
}

/**
 * Returns the ascent of the font described by the receiver. A
 * font's <em>ascent</em> is the distance from the baseline to the
 * top of actual characters, not including any of the leading area,
 * measured in pixels.
 *
 * @return the ascent of the font
 */
public int getAscent() {
    return handle.lMaxAscender - handle.lInternalLeading;
}

/**
 * Returns the average character width, measured in pixels,
 * of the font described by the receiver.
 *
 * @return the average character width of the font
 */
public int getAverageCharWidth() {
	return handle.lAveCharWidth;
}

/**
 * Returns the descent of the font described by the receiver. A
 * font's <em>descent</em> is the distance from the baseline to the
 * bottom of actual characters, not including any of the leading area,
 * measured in pixels.
 *
 * @return the descent of the font
 */
public int getDescent() {
    /*
     *  Bug in OS/2. For unknown reason, FONTMETRICS.lMaxDescender is greater
     *  than lMaxBaselineExt - lMaxAscender for some truetype fonts at some
     *  sizes (for example, Times New Roman at 24 points), which causes
     *  the value returned by FontMetrics.getHeight() to be smaller than
     *  getLeading() + getAcsent() + getDescent(). The solution is to
     *  use the lMaxBaselineExt - lMaxAscender instead of lMaxDescender.
     */
//	return handle.lMaxDescender;
	return handle.lMaxBaselineExt - handle.lMaxAscender;
}

/**
 * Returns the height of the font described by the receiver, 
 * measured in pixels. A font's <em>height</em> is the sum of
 * its ascent, descent and leading area.
 *
 * @return the height of the font
 *
 * @see #getAscent
 * @see #getDescent
 * @see #getLeading
 */
public int getHeight() {
	return handle.lMaxBaselineExt;
}

/**
 * Returns the leading area of the font described by the
 * receiver. A font's <em>leading area</em> is the space
 * above its ascent which may include accents or other marks.
 *
 * @return the leading space of the font
 */
public int getLeading() {
	return handle.lInternalLeading;
}

/**
 * Returns an integer hash code for the receiver. Any two 
 * objects which return <code>true</code> when passed to 
 * <code>equals</code> must return the same value for this
 * method.
 *
 * @return the receiver's hash
 *
 * @see #equals
 */
public int hashCode() {
    int h = 0;
    for (int i = 0; i < OS.FACESIZE; i++) {
        h ^= handle.szFacename [i] ^ handle.szFamilyname [i];
    }
    return
        h ^
        handle.idRegistry ^
        handle.usCodePage ^
        handle.lEmHeight ^
        handle.lXHeight ^
        handle.lMaxAscender ^
        handle.lMaxDescender ^
        handle.lLowerCaseAscent ^
        handle.lLowerCaseDescent ^
        handle.lInternalLeading ^
        handle.lExternalLeading ^
        handle.lAveCharWidth ^
        handle.lMaxCharInc ^
        handle.lEmInc ^
        handle.lMaxBaselineExt ^
        handle.sCharSlope ^
        handle.sInlineDir ^
        handle.sCharRot ^
        handle.usWeightClass ^
        handle.usWidthClass ^
        handle.sXDeviceRes ^
        handle.sYDeviceRes ^
//        handle.sFirstChar ^
//        handle.sLastChar ^
//        handle.sDefaultChar ^
//        handle.sBreakChar ^
        handle.sNominalPointSize ^
        handle.sMinimumPointSize ^
        handle.sMaximumPointSize ^
        handle.fsType ^
        handle.fsDefn ^
        handle.fsSelection ^
        handle.fsCapabilities ^
//        handle.lSubscriptXSize ^
//        handle.lSubscriptYSize ^
//        handle.lSubscriptXOffset ^
//        handle.lSubscriptYOffset ^
//        handle.lSuperscriptXSize ^
//        handle.lSuperscriptYSize ^
//        handle.lSuperscriptXOffset ^
//        handle.lSuperscriptYOffset ^
        handle.lUnderscoreSize ^
        handle.lUnderscorePosition ^
        handle.lStrikeoutSize ^
        handle.lStrikeoutPosition ^
        handle.sKerningPairs ^
        handle.sFamilyClass ^
        handle.lMatch ^
        handle.FamilyNameAtom ^
        handle.FaceNameAtom;
//        handle.panose_bFamilyType ^
//        handle.panose_bSerifStyle ^
//        handle.panose_bWeight ^
//        handle.panose_bProportion ^
//        handle.panose_bContrast ^
//        handle.panose_bStrokeVariation ^
//        handle.panose_bArmStyle ^
//        handle.panose_bLetterform ^
//        handle.panose_bMidline ^
//        handle.panose_bXHeight ^
//        handle.panose_fbPassedISO ^
//        handle.panose_fbFailedISO;
}

/**	 
 * Invokes platform specific functionality to allocate a new font metrics.
 * <p>
 * <b>IMPORTANT:</b> This method is <em>not</em> part of the public
 * API for <code>FontMetrics</code>. It is marked public only so that
 * it can be shared within the packages provided by SWT. It is not
 * available on all platforms, and should never be called from
 * application code.
 * </p>
 *
 * @param handle the <code>FONTMETRICS</code> containing information about a font
 *
 * @private
 */
public static FontMetrics pm_new (FONTMETRICS handle) {
	FontMetrics fontMetrics = new FontMetrics();
	fontMetrics.handle = handle;
	return fontMetrics;
}

}

