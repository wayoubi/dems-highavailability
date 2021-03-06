![DEMS Use Case Diagram](resources/ginacody.jpeg)
<p>
  <img alt="Version" src="https://img.shields.io/badge/version-0.9.01-blue.svg?cacheSeconds=2592000" />
  <a href="https://github.com/wayoubi/RiskGame/wiki">
    <img alt="Documentation" src="https://img.shields.io/badge/documentation-yes-brightgreen.svg" target="_blank" />
  </a>
  <a href="https://github.com/kefranabg/readme-md-generator/graphs/commit-activity">
    <img alt="Maintenance" src="https://img.shields.io/badge/Maintained%3F-yes-green.svg" target="_blank" />
  </a>
  <h1>Distributed Event Management System with High Availability Fault Tolerance</h1>
  <br/>
</p>

![DEMS Use Case Diagram](resources/banner.png)

## How to Build and Run the System
Make sure you have Maven, github and Java 8 jdk installed on your computer

See how to install git  
[here](https://git-scm.com/book/en/v2/Getting-Started-Installing-Git)

See how to install jdk1.8  
[here](https://docs.oracle.com/javase/8/docs/technotes/guides/install/install_overview.html)

See how to install maven  
[here](https://maven.apache.org/install.html)

Please make sure your Java SDK 1.8 on the path, for example use
```sh 
$ java -version
java version "1.8.0_144"
Java(TM) SE Runtime Environment (build 1.8.0_144-b01)
Java HotSpot(TM) 64-Bit Server VM (build 25.144-b01, mixed mode)
```
Please make sure your Maven on the path, for example use
```sh 
$ mvn -version
Apache Maven 3.6.2 (40f52333136460af0dc0d7232c0dc0bcf0d9e117; 2019-08-27T11:06:16-04:00)
Maven home: /Users/i857625/Work/tools/apache-maven-3.6.2
Java version: 1.8.0_212, vendor: Oracle Corporation, runtime: /Library/Java/JavaVirtualMachines/jdk1.8.0_212.jdk/Contents/Home/jre
Default locale: en_CA, platform encoding: UTF-8
OS name: "mac os x", version: "10.15.3", arch: "x86_64", family: "mac"
```


Download the source code from github, on your computer open new terminal
```sh 
$ git clone https://github.com/wayoubi/dems-highavailability.git
```

Compile and Build all system components
```sh
cd dems-highavailability
./mvnw.sh clean install 
```
If you are using Windows
```sh
cd dems-highavailability
mvnw.cmd clean install 
```

Run the ORBD
```sh 
# default CORBA Naming Service host is localhost
# default CORBA Naming Service port is 1050

$ ./runorbd.sh 

# use run.bat if you're on Windows
$ runorbd.bat

#You can also run the ORBD from the Java bin folder
$ orbd -ORBInitialPort 1050

```

Run the Frontend
```sh 
# While you are in the dems-highavailability folder
# Required parameters in order 
# 1 CORBA Naming Serivce Host, default localhost
# 2 CORBA Naming Serivce Port, default 1050
# 3 Sequincer Host, default localhost
# 4 Sequencer Port, default 8080
# 5 Mutlicast IP, dfault 224.0.0.110
# 6 Multicast Port, default 4443

cd frontend
./run.sh localhost 1050 localhost 8080 224.0.0.110 4446

# use run.bat if you're on Windows
cd frontend
run.bat localhost 1050 localhost 8080 224.0.0.110 4446
```

Run the Sequencer
```sh 
# While you are in the dems-highavailability folder
# Required parameters in order 
# 1 UDP Unicast Listening Port, default 8080 
# 2 Mutlicast IP, default 224.0.0.110
# 3 Multicast Port, default 4443
# 4 Starting Sequence default 0

cd sequencer
./run.sh 8080 224.0.0.110 4446 0

# use run.bat if you're on Windows
cd sequencer
run.bat 8080 224.0.0.110 4446 0

```

Run the Replica Manager
```sh 
# While you are in the dems-highavailability folder
# Required parameters in order
# 1 Replica Manager Name, default RM$ where $ is 1,2,3 and so on
# 2 Mutlicast IP, default 224.0.0.110
# 3 Multicast Port, default 4443 
# 4 Frontend Host, default localhost
# 5 RMI Registry Host, default localhost
# 6 RMI Registry Port, default 1099

cd replica-manager
./run.sh RM1 224.0.0.110 4446 localhost localhost 1099

# use run.bat if you're on Windows
cd replica-manager
run.bat RM1 224.0.0.110 4446 localhost localhost 1099

```

Run the DMES Server
```sh 
# While you are in the dems-highavailability folder
# Required parameters in order
# Sever Location, default MTL 
# Replica Manager Name, default RM1
# RMI Host, default localhost 
# RMI Port, default 1099
# UDP Port, default 8080

cd dems-server
./run.sh MTL RM1 localhost 1099 7070 

# use run.bat if you're on Windows
cd dems-server
run.bat MTL RM1 localhost 1099 7070 
```

Run the DEMS Client
```sh 
# While you are in the dems-highavailability folder

cd client
./run.sh

# use run.bat if you're on Windows
cd client
run.bat

```

## Introduction
The Distributed Event Management System (DEMS) is an event management solution designed to help businesses to manage events and bookings over remote locations (cities) in a client server and peer to peer architecture style. The events database of each city is stored in the local server of that location offering high availability and avoiding single point of failure. The system is designed to support concurrency, low latency and thread safety. The system is designed and implemented to be presented for the assignments and the project required for Distributed System Design [COMP 6231] course offered during Winter 2020 at Concordia University. 

The two major functional requirements of the system are to manage events (create, update, remove and list), Events Management should be performed by Managers only. On the other hand, Booking Management (Make, Cancel and List Bookings) can be performed by both Event Managers and Costumers. The following Use Case diagram showing the use cases (functional requirements) of DEMS

![DEMS Use Case Diagram](resources/usecase.png)

## Architecture

![DEMS Use Case Diagram](resources/architecture.png)
