@echo off
cd /d "%~dp0"
javac *.java
java -cp ".;lib/mysql-connector-j-9.6.0.jar" LoginFrame
pause
