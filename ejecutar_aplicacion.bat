@echo off
echo.
echo ===============================================
echo   🎯 SISTEMA DE RECONOCIMIENTO FACIAL 
echo   Iniciando aplicacion...
echo ===============================================
echo.

cd "C:\Users\LuissxD\Downloads\Login_Reconocimiento_Facial_Java"

echo 📦 Verificando dependencias...
call mvn dependency:copy-dependencies -q

echo 🔧 Compilando proyecto...
call mvn compile -q

echo 🚀 Iniciando aplicacion...
echo.
echo ⚠️  NOTA: La inicializacion de camara puede tomar 30-60 segundos
echo    El progreso se mostrara en la interfaz
echo.

java -cp "target/classes;target/dependency/*" com.reconocimiento.facial.AplicacionPrincipal

echo.
echo 👋 Aplicacion cerrada
pause