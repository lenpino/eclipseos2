@echo off
setlocal

rem ------------------------------------------------------------------------
rem Specify here the location of your XMLmind XML Editor distrib directory.
rem ------------------------------------------------------------------------
set dist=D:\Apps\Java\xxe-m13p3-bin

set jars=%dist%\jaxp.jar;%dist%\sax2.jar;%dist%\xp.jar;%dist%\regexp.jar;%dist%\aptparser.jar;%dist%\azcheck.jar

if exist "%dist%\xxe.jar" goto jar
set cp=%dist%\..\class;%jars%
goto fi
:jar
set cp=%dist%\xxe.jar;%jars%
:fi

rem Notes about the java command line:
rem 
rem * Specify a HOME environment variable and add -DHOME=%HOME% to the java 
rem   command line if you do not want xxe.ini to be created under 
rem   C:\winnt\Profiles\<user>.
rem
rem * Add the following property to the java command line to have the native
rem   Windows LAF rather than the Metal one:
rem   -Dswing.defaultlaf=com.sun.java.swing.plaf.windows.WindowsLookAndFeel
rem
rem * Add the following property to the java command line to have the GUI
rem   in french whatever is your default locale:
rem   -Duser.language=fr

javaw -Xmx64m -classpath "%cp%" com.xmlmind.xmleditapp.App %1 %2 %3 %4 %5 %6 %7 %8 %9

endlocal
