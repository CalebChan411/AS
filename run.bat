@echo off
cd /d "%~dp0"
javac *.java
if errorlevel 1 (
    echo Compilation failed.
    exit /b 1
)
java Main
exit /b %ERRORLEVEL%
