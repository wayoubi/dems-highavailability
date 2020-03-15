@echo off
echo Startinf Frontend Server
call mvn clean install
call cls
call cd target
call java -jar frontend-1.0-SNAPSHOT.jar $1 $2 $3 $4 $5 $6