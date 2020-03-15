mvn clean install
cls
cd target
java -Djava.net.preferIPv4Stack=true -jar replica-manager-1.0-SNAPSHOT.jar $1 $2 $3 $4