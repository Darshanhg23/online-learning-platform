@echo off
title LearnHub - Online Learning Platform
echo.
echo  ==========================================
echo    LearnHub - Online Learning Platform
echo  ==========================================
echo.
echo  Starting Spring Boot server...
echo  This may take a moment on first run.
echo.

set "MAVEN_HOME=%USERPROFILE%\maven\apache-maven-3.9.6"
set "PATH=%MAVEN_HOME%\bin;%PATH%"

cd /d "%~dp0"
mvn spring-boot:run

echo.
echo  Server stopped.
pause
