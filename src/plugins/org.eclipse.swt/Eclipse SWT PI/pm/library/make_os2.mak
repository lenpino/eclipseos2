#**********************************************************************
# OS/2 version.
# Copyright (c) 2002, 2004 EclipseOS2 Team.
#**********************************************************************
# Copyright (c) 1999, 2002 IBM Corp.  All rights reserved.
# This file is made available under the terms of the Common Public License v1.0
# which accompanies this distribution, and is available at
# http://www.eclipse.org/legal/cpl-v10.html
#**********************************************************************
#
# Makefile for SWT libraries on OS/2

!include <make_common.mak>

# library name must be constructed in the different way under OS/2
# (since it does not support long names of DLL files)
# so we're not using the common scheme for this.
# Under OS/2 the dll name will be swtpNNNN.dll

SWT_PREFIX  = swt
# WS_PREFIX   = pm
WS_PREFIX   = p
SWT_VERSION = $(maj_ver)$(min_ver)
# SWT_LIB     = $(SWT_PREFIX)-$(WS_PREFIX)-$(SWT_VERSION).dll
SWT_LIB     = $(SWT_PREFIX)$(WS_PREFIX)$(SWT_VERSION).dll

SWT_LOCATION = ..\..\..\..\org.eclipse.swt.pm\os\os2\x86

all:
    @set SWT_LIB=$(SWT_LIB)
    vacbld make_os2.icc
    copy $(SWT_LIB) $(SWT_LOCATION)

clean:
    -del *.obj *.res *.dll *.lib *.ics $(SWT_LOCATION)\*.dll
