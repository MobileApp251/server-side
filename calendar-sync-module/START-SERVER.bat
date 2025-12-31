@echo off
echo ================================================
echo   Calendar Sync Module - Starting PHP Server
echo ================================================
echo.
echo Server will start at: http://localhost:8080
echo API Documentation: http://localhost:8080
echo.
echo Press Ctrl+C to stop the server
echo ================================================
echo.

php -S localhost:8080 -t .

pause
