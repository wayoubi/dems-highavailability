@echo off
echo Staring DEMS Client
call mvn clean install
call cls
call mvn -q -pl . spring-boot:run