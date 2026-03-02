@ECHO OFF
SETLOCAL

SET BASE_DIR=%~dp0
SET WRAPPER_PROPERTIES=%BASE_DIR%\.mvn\wrapper\maven-wrapper.properties

IF NOT EXIST "%WRAPPER_PROPERTIES%" (
  ECHO Error: "%WRAPPER_PROPERTIES%" is missing.
  EXIT /B 1
)

FOR /F "tokens=1,2 delims==" %%A IN (%WRAPPER_PROPERTIES%) DO (
  IF "%%A"=="distributionUrl" SET DISTRIBUTION_URL=%%B
)

IF "%JAVA_HOME%"=="" (
  SET JAVACMD=java.exe
) ELSE (
  SET JAVACMD=%JAVA_HOME%\bin\java.exe
)

WHERE "%JAVACMD%" >NUL 2>NUL
IF ERRORLEVEL 1 (
  ECHO Error: Java was not found. Set JAVA_HOME or add java to PATH.
  EXIT /B 1
)

IF "%MAVEN_USER_HOME%"=="" SET MAVEN_USER_HOME=%USERPROFILE%\.m2
SET WRAPPER_DISTS=%MAVEN_USER_HOME%\wrapper\dists
IF NOT EXIST "%WRAPPER_DISTS%" mkdir "%WRAPPER_DISTS%"

SET DIST_NAME=apache-maven-3.9.9-bin.zip
SET DIST_DIR=%WRAPPER_DISTS%\apache-maven-3.9.9-bin
SET MAVEN_HOME=%DIST_DIR%\apache-maven-3.9.9

IF NOT EXIST "%MAVEN_HOME%\bin\mvn.cmd" (
  IF NOT EXIST "%DIST_DIR%" mkdir "%DIST_DIR%"
  IF NOT EXIST "%DIST_DIR%\%DIST_NAME%" (
    powershell -NoProfile -ExecutionPolicy Bypass -Command "Invoke-WebRequest -UseBasicParsing '%DISTRIBUTION_URL%' -OutFile '%DIST_DIR%\%DIST_NAME%'"
    IF ERRORLEVEL 1 EXIT /B 1
  )
  powershell -NoProfile -ExecutionPolicy Bypass -Command "Expand-Archive -Force '%DIST_DIR%\%DIST_NAME%' '%DIST_DIR%'"
  IF ERRORLEVEL 1 EXIT /B 1
)

CALL "%MAVEN_HOME%\bin\mvn.cmd" %*
