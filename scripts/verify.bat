@echo off

echo =======================================
echo  Spatial Repository Verification
echo =======================================

call gradlew.bat --no-daemon verifyRepository

if errorlevel 1 exit /b %errorlevel%

echo.
echo Repository verification completed successfully.