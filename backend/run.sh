#!/bin/sh
# Compile and start the backend (needs JDK 17+). Usage: ./run.sh [port]
mkdir -p out
javac -encoding UTF-8 -d out src/dss/*.java && java -Dfile.encoding=UTF-8 -cp out dss.Main "$@"
