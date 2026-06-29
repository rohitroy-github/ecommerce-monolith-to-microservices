:: Utility: Starts the e-commerce backend microservices and frontend client in the correct sequence with startup delays.
@echo off
setlocal EnableExtensions
title E-Commerce Project - Initializer

:: Resolve project root relative to this script location.
set "BASE_PATH=%~dp0"
if "%BASE_PATH:~-1%"=="\" set "BASE_PATH=%BASE_PATH:~0,-1%"

set "BACKEND_PATH=%BASE_PATH%\backend-microservices"
set "FRONTEND_PATH=%BASE_PATH%\frontend-client"

:: Tune startup delays here (in seconds).
set "SERVICE_DELAY=8"
set "GATEWAY_DELAY=25"
set "SEEDER_DELAY=5"

echo ====================================================
echo Starting E-Commerce Microservices and Frontend Client in order...
echo Base path: %BASE_PATH%
echo ====================================================

call :requireDir "%BACKEND_PATH%\productservice" "Product Service"
call :requireDir "%BACKEND_PATH%\orderservice" "Order Service"
call :requireDir "%BACKEND_PATH%\paymentservice" "Payment Service"
call :requireDir "%BACKEND_PATH%\userservice" "User Service"
call :requireDir "%BACKEND_PATH%\apigateway" "API Gateway"
call :requireDir "%BACKEND_PATH%\dataseeder" "DataSeeder"
call :requireDir "%FRONTEND_PATH%" "Frontend Client"

call :launchService "1/7" "Product Service" "%BACKEND_PATH%\productservice" ".\mvnw.cmd spring-boot:run" %SERVICE_DELAY%
call :launchService "2/7" "Order Service" "%BACKEND_PATH%\orderservice" ".\mvnw.cmd spring-boot:run" %SERVICE_DELAY%
call :launchService "3/7" "Payment Service" "%BACKEND_PATH%\paymentservice" ".\mvnw.cmd spring-boot:run" %SERVICE_DELAY%
call :launchService "4/7" "User Service" "%BACKEND_PATH%\userservice" ".\mvnw.cmd spring-boot:run" %SERVICE_DELAY%
call :launchService "5/7" "API Gateway" "%BACKEND_PATH%\apigateway" ".\mvnw.cmd spring-boot:run" %GATEWAY_DELAY%
call :launchService "6/7" "DataSeeder" "%BACKEND_PATH%\dataseeder" ".\mvnw.cmd spring-boot:run" %SEEDER_DELAY%

echo [7/7] Launching Frontend Client...
if not exist "%FRONTEND_PATH%\node_modules" (
    echo Frontend dependencies not found. Running npm install first...
    start "Frontend Install" cmd /c "cd /d "%FRONTEND_PATH%" && npm install"
    timeout /t 5 /nobreak >nul
)
start "Frontend Client" cmd /k "cd /d "%FRONTEND_PATH%" && npm run dev"

echo ====================================================
echo All 7 stages initialized. Keep this master window open.
echo ====================================================
pause
exit /b 0

:launchService
set "STAGE=%~1"
set "NAME=%~2"
set "DIR=%~3"
set "CMD=%~4"
set "WAIT=%~5"

echo [%STAGE%] Launching %NAME%...
start "%NAME%" cmd /k "cd /d "%DIR%" && %CMD%"
timeout /t %WAIT% /nobreak >nul
exit /b 0

:requireDir
if not exist "%~1" (
    echo ERROR: %~2 path not found: %~1
    echo Exiting initializer.
    pause
    exit /b 1
)
exit /b 0