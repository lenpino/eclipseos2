/* REXX */
parse arg args
/* Drive-independent: the VM keeps everything on X:, the physical ArcaOS box on C:.  */
/* JAVA_HOME and ANT_HOME win if set; otherwise the first drive that has them is used. */
drives = 'X: C:'
java_home = value('JAVA_HOME', , 'OS2ENVIRONMENT')
if java_home = '' then do
  do i = 1 to words(drives)
    d = word(drives, i)
    if stream(d'\java131\jre\bin\java.exe', 'C', 'QUERY EXISTS') \= '' then do
      java_home = d'\java131'
      leave
    end
  end
end
ant_home = value('ANT_HOME', , 'OS2ENVIRONMENT')
if ant_home = '' then do
  do i = 1 to words(drives)
    d = word(drives, i)
    if stream(d'\apps\dev\tools\ant-1.5.4\lib\ant.jar', 'C', 'QUERY EXISTS') \= '' then do
      ant_home = d'\apps\dev\tools\ant-1.5.4'
      leave
    end
  end
end
if java_home = '' | ant_home = '' then do
  say 'ant.cmd: cannot find java131 or ant-1.5.4 (set JAVA_HOME / ANT_HOME)'
  exit 1
end
ant_classpath = java_home'\lib\tools.jar;'ant_home'\lib\ant-1.5.4-patch-os2.jar;'ant_home'\lib\ant.jar;'ant_home'\lib\optional.jar;'ant_home'\lib\xercesImpl.jar;'ant_home'\lib\xml-apis.jar;'
'@'java_home'\jre\bin\java.exe -cp' ant_classpath 'org.apache.tools.ant.Main' args
exit rc
