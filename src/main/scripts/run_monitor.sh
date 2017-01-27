#!/bin/bash
java -Dlog4j.configuration=log4j.txt -classpath "./lib/*" com.kint.citizenmaths.services.monitor.Monitor -cf monitor-config.properties