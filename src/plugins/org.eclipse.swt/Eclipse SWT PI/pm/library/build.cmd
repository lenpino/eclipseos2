@echo off

rem Copyright (c) 2002, 2004 EclipseOS2 Team.
rem This file is made available under the terms of the Common Public License v1.0
rem which accompanies this distribution, and is available at
rem http://www.eclipse.org/legal/cpl-v10.html

if "%VACPPMAIN%"=="" goto NOVAC

call %VACPPMAIN%\bin\setenv.cmd
set INCLUDE=..\..\..\Eclipse SWT\common\library;%INCLUDE%
nmake -f make_os2.mak %1 %2 %3 %4 %5 %6 %7 %8 %9
goto END

:NOVAC

echo.
echo There's no VACPPMAIN environment variable defined.
echo Possibly, Visual Age C/C++ 4.0 is not installed.
echo.

:END
