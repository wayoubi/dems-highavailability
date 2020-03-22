@echo off
echo Starting Replica Manager
call mvn install
call cls
call cd target
call java -Djava.net.preferIPv4Stack=true -jar replica-manager-1.0-SNAPSHOT.jar %1 %2 %3 %4 %5 %6