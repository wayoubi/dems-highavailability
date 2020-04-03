@echo off
echo "Starting RMI Registry"
cd %JAVA_HOME%/bin
call ./rmiregistry -J-Djava.class.path=%DEMSHA_HOME%/common/target/common-1.0-SNAPSHOT.jar