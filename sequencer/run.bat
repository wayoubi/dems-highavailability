@echo off
echo Starting Sequencer
call mvn install
call cls
call cd target
call java -jar sequencer-1.0-SNAPSHOT.jar %1 %2 %3