@echo off
echo Compiling Banking Application...
javac BankingAppGUI.java

if %ERRORLEVEL% NEQ 0 (
    echo Compilation failed!
    pause
    exit /b %ERRORLEVEL%
)

echo Compilation successful!
echo.
echo Running Banking Application...
java BankingAppGUI

pause 