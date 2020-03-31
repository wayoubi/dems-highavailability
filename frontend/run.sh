mvn clean install
clear
cd target
java -Djava.net.preferIPv4Stack=true -jar frontend-1.0-SNAPSHOT.jar $1 $2 $3 $4 $5 $6