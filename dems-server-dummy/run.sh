mvn clean install
clear
cd target
java -Djava.net.preferIPv4Stack=true -jar dems-server-dummy-1.0-SNAPSHOT.jar