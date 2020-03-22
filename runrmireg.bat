@echo off
echo "Starting RMI Registry"
cd %JAVA_HOME%/bin
call ./rmiregistry -J-Djava.class.path=/Users/i857625/Desktop/Concordia/Classes/COMP6231.DISTRIBUTEDSYSTEMDESIGN/dems-highavailability/common/target/common-1.0-SNAPSHOT.jar