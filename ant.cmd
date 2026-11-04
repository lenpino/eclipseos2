/* REXX */
parse arg args
java_home = value('JAVA_HOME', 'c:\java131', 'OS2ENVIRONMENT');
ant_home = 'x:\apps\dev\tools\ant-1.5.4'
ant_classpath = java_home'\lib\tools.jar;'ant_home'\lib\ant-1.5.4-patch-os2.jar;'ant_home'\lib\ant.jar;'ant_home'\lib\optional.jar;'ant_home'\lib\xercesImpl.jar;'ant_home'\lib\xml-apis.jar;'
'@java -cp' ant_classpath 'org.apache.tools.ant.Main' args