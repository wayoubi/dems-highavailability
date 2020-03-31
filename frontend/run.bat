@echo off
echo Starting Frontend Server
call mvn install
call cls
call cd target
call -Djava.net.preferIPv4Stack=true java -jar frontend-1.0-SNAPSHOT.jar %1 %2 %3 %4 %5 %6