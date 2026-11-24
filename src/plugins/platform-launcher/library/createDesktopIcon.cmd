/* REXX: creates (or replaces) the Eclipse program object on the OS/2 desktop.
 * Usage: createDesktopIcon [eclipse_dir] [java_dir] [workspace]
 * The object runs the launcher (eclipse.exe: splash screen, then the workbench) with the
 * Java VM of the console (java.exe: javaw.exe hangs when it is started by the launcher). */
parse arg eclipseDir javaDir workspace
if eclipseDir = '' then eclipseDir = 'x:\apps\dev\eclipse'
if javaDir = '' then javaDir = 'x:\java131'
if workspace = '' then workspace = eclipseDir'\workspace'
call RxFuncAdd 'SysCreateObject', 'RexxUtil', 'SysCreateObject'
call RxFuncAdd 'SysDestroyObject', 'RexxUtil', 'SysDestroyObject'
id = '<ECLIPSE_OS2_LAUNCHER>'
call SysDestroyObject id
setup = 'OBJECTID='id';EXENAME='eclipseDir'\eclipse.exe;STARTUPDIR='eclipseDir';' ,
     || 'PARAMETERS=-vm 'javaDir'\jre\bin\java.exe -os os2 -ws pm -arch x86 -data 'workspace';' ,
     || 'ICONFILE='eclipseDir'\eclipse.ico;PROGTYPE=PM;MINIMIZED=YES'
if SysCreateObject('WPProgram', 'Eclipse', '<WP_DESKTOP>', setup, 'R') then
   say 'Eclipse object created on the desktop.'
else
   say 'The Eclipse object could not be created.'
