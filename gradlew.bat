@echo off
where gradle >nul 2>nul
if %errorlevel%==0 (gradle %* & exit /b %errorlevel%)
echo Gradle non e installato. Aprire in Android Studio o installare Gradle 9.5.0.
exit /b 1
