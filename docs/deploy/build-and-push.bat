@echo off
REM Usage: build-and-push.bat your-dockerhub-id 0.0.1
REM Example: build-and-push.bat mjc123 0.0.1

setlocal

if "%~1"=="" (
    echo Please provide your Docker Hub id.
    echo Usage: build-and-push.bat your-dockerhub-id 0.0.1
    exit /b 1
)

set DOCKERHUB_ID=%~1

if "%~2"=="" (
    set VERSION=0.0.1
) else (
    set VERSION=%~2
)

set IMAGE=%DOCKERHUB_ID%/hotel-backend:%VERSION%

echo ==============================
echo  Docker Hub login
echo ==============================
docker login
if errorlevel 1 (
    echo Login failed. Stopping.
    exit /b 1
)

echo ==============================
echo  Building image: %IMAGE%
echo ==============================
docker build -f Dockerfile.prod -t %IMAGE% .
if errorlevel 1 (
    echo Build failed. Stopping.
    exit /b 1
)

echo ==============================
echo  Tagging as latest
echo ==============================
docker tag %IMAGE% %DOCKERHUB_ID%/hotel-backend:latest

echo ==============================
echo  Pushing to Docker Hub
echo ==============================
docker push %IMAGE%
if errorlevel 1 (
    echo Push failed. Stopping.
    exit /b 1
)
docker push %DOCKERHUB_ID%/hotel-backend:latest

echo ==============================
echo  Done: %IMAGE%
echo ==============================

endlocal
