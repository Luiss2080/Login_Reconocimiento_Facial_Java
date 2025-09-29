@echo off
echo ========================================
echo REPARANDO Y CONFIGURANDO MYSQL
echo ========================================

echo.
echo 1. Deteniendo MySQL...
net stop MySQL

echo.
echo 2. Reparando tablas del sistema...
"C:\xampp\mysql\bin\myisamchk.exe" --recover --extend-check "C:\xampp\mysql\data\mysql\*.MYI"

echo.
echo 3. Iniciando MySQL...
net start MySQL

echo.
echo 4. Esperando que MySQL esté listo...
timeout /t 5

echo.
echo 5. Creando base de datos...
"C:\xampp\mysql\bin\mysql.exe" -u root -e "DROP DATABASE IF EXISTS sistema_reconocimiento_facial; CREATE DATABASE sistema_reconocimiento_facial CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

echo.
echo 6. Ejecutando script SQL...
"C:\xampp\mysql\bin\mysql.exe" -u root sistema_reconocimiento_facial < database_simple.sql

echo.
echo 7. Verificando estructura...
"C:\xampp\mysql\bin\mysql.exe" -u root -e "USE sistema_reconocimiento_facial; DESCRIBE usuarios;"

echo.
echo ========================================
echo CONFIGURACION COMPLETADA
echo ========================================
pause