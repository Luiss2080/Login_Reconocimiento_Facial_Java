@echo off
echo 🎥 PROBADOR DE CÁMARA - Sistema de Reconocimiento Facial
echo ========================================================

REM Usar Java para ejecutar la clase de pruebas de cámara
java -cp "target\classes;target\dependency\*" com.reconocimiento.facial.utilidades.ProbadorCamaraMejorado

pause