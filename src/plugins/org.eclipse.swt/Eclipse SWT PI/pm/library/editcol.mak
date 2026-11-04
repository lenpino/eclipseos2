# editcol.mak
# Creato dal MakeMake del WorkFrame/2 IBM alle 17:40:21 del 28 Gennaio 1998
#
# Le azioni incluse in questo file Make sono:
#  Compile::Resource Compiler
#  Compile::C++ Compiler
#  Link::Linker
#  Bind::Resource Bind

.SUFFIXES: .c .obj .rc .res 

.all: \
    .\editcol.dll

.rc.res:
    @echo " Compile::Resource Compiler "
    rc.exe -r %s %|dpfF.RES

{F:\WORKDESK\Progetti\customcontrol\colorwheel}.rc.res:
    @echo " Compile::Resource Compiler "
    rc.exe -r %s %|dpfF.RES

.c.obj:
    @echo " Compile::C++ Compiler "
    icc.exe /Ss /Q /V"EDITCOL.DLL V. 1.0 * (c) 1998 * Alessandro Cantatore * Team OS/2 Italy" /Oc /Rn /Ge- /Gu /Ol /C %s

{F:\WORKDESK\Progetti\customcontrol\colorwheel}.c.obj:
    @echo " Compile::C++ Compiler "
    icc.exe /Ss /Q /V"EDITCOL.DLL V. 1.0 * (c) 1998 * Alessandro Cantatore * Team OS/2 Italy" /Oc /Rn /Ge- /Gu /Ol /C %s

.\editcol.dll: \
    .\editcol.obj \
    .\editcol.res \
    {$(LIB)}editcol.def
    @echo " Link::Linker "
    @echo " Bind::Resource Bind "
    icc.exe @<<
     /B" /exepack:2 /pmtype:pm /packd /optfunc /nologo"
     /Feeditcol.dll 
     editcol.def
     .\editcol.obj
<<
    rc.exe -x2 .\editcol.res editcol.dll

.\editcol.res: \
    F:\WORKDESK\Progetti\customcontrol\colorwheel\editcol.rc \
    {F:\WORKDESK\Progetti\customcontrol\colorwheel;$(INCLUDE)}editcol.h

.\editcol.obj: \
    F:\WORKDESK\Progetti\customcontrol\colorwheel\editcol.c \
    {F:\WORKDESK\Progetti\customcontrol\colorwheel;$(INCLUDE);}editcol.h
