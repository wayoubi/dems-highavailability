@echo off
echo Staring DEMS Client
call mvn install
call cls
call mvn -q -pl . spring-boot:run