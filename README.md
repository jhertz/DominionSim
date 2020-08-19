# DominionSim

DominionSim is a simulator for the Dominion card game.

## Prerequisites

You will need the following to build and run the simulator:
- Java SE SDK 1.8+: http://www.oracle.com/technetwork/java/javase/downloads
- Maven: https://maven.apache.org/download.cgi

## Build

To build the simulator JAR file:
```
$ mvn package
```

## Run

To run the simulator:
```
$ mvn exec:java
```

## Command Line

To run two bots with the UI:

```
$ mvn clean package -DskipTests=true #alliance not currently building
```
```
$ mvn exec:java -Dmy.mainClass="be.aga.dominionSimulator.BasicCMDSimulation" -Dexec.args="10000 Magpie \"Big Money Ultimate\""
```
