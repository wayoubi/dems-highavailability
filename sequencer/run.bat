@echo off
echo Starting Sequencer
call mvn install
call cls
call cd target
call -Djava.net.preferIPv4Stack=true java -jar sequencer-1.0-SNAPSHOT.jar %1 %2 %3