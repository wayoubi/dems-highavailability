mvn clean install
clear
cd target
java -Djava.net.preferIPv4Stack=true -Djava.security.policy=$DEMSHA_HOME/rmiregistry.policy -jar dems-server-1.0-SNAPSHOT.jar $1 $2 $3 $4 $5