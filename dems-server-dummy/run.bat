@echo off
echo Starting DEMS Server
call mvn install
call cls
call cd target
call java -Djava.net.preferIPv4Stack=true -jar dems-server-dummy-1.0-SNAPSHOT.jar