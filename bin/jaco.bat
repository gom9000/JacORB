@echo off
rem call java interpreter
java -Xbootclasspath:C:\Work\JacORB\lib\jacorb.jar;c:\jdk1.3\jre\lib\rt.jar;%CLASSPATH% -Dorg.omg.CORBA.ORBClass=org.jacorb.orb.ORB -Dorg.omg.CORBA.ORBSingletonClass=org.jacorb.orb.ORBSingleton %*

