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
$ mvn exec:java -Dmy.mainClass="be.aga.dominionSimulator.exec.BasicCMDSimulation" -Dexec.args="10000 Magpie \"Big Money Ultimate\""
```

## To generate a series of bots with a parameter permuted 

(currently limited to integer-valued right hand condition) - I'm having to reverse engineer this 

```
mvn exec:java -Dmy.mainClass="be.aga.dominionSimulator.exec.OptimizeParameter" -Dexec.args="\"Big Money Ultimate\" Province 14 22 outputDir"
```

This generates variants of the Big Money Ultimate bot, varying the province buy when deck has X money for X between 14 and 22, writing results to outputDir.  Useful for some kind of hill climbing / optimization stuff.

## To generate a tournament for a set of bots

```
mvn exec:java -Dmy.mainClass="be.aga.dominionSimulator.exec.Tournament"  -Dexec.args="outputDir 1000"
```

Runs a tournament with the bots in the outputDir with 1000 games per pair.  Prints out the crosstable and the standings.
