@echo off
echo Starting DEMS Server
call mvn install
call cls
call cd target
call java -jar dems-server-1.0-SNAPSHOT.jar %1 %2 %3 %4 %5