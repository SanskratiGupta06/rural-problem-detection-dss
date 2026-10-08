@echo off
REM Compile and start the backend (needs JDK 17+). Usage: run.bat [port]
if not exist out mkdir out
javac -encoding UTF-8 -d out src\dss\*.java && java -Dfile.encoding=UTF-8 -cp out dss.Main %*
