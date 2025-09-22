@echo off
echo ========================================
echo  Descargando clasificadores OpenCV
echo ========================================

set BASE_URL=https://raw.githubusercontent.com/opencv/opencv/master/data/haarcascades
set TARGET_DIR=%~dp0

echo Descargando haarcascade_frontalface_alt.xml...
powershell -Command "Invoke-WebRequest -Uri '%BASE_URL%/haarcascade_frontalface_alt.xml' -OutFile '%TARGET_DIR%haarcascade_frontalface_alt.xml'"

echo Descargando haarcascade_profileface.xml...
powershell -Command "Invoke-WebRequest -Uri '%BASE_URL%/haarcascade_profileface.xml' -OutFile '%TARGET_DIR%haarcascade_profileface.xml'"

echo Descargando haarcascade_eye.xml...
powershell -Command "Invoke-WebRequest -Uri '%BASE_URL%/haarcascade_eye.xml' -OutFile '%TARGET_DIR%haarcascade_eye.xml'"

echo Descargando haarcascade_smile.xml...
powershell -Command "Invoke-WebRequest -Uri '%BASE_URL%/haarcascade_smile.xml' -OutFile '%TARGET_DIR%haarcascade_smile.xml'"

echo.
echo ========================================
echo  Descarga completada!
echo ========================================
echo Archivos descargados en: %TARGET_DIR%
pause