@echo off
echo ========================================
echo VERIFICANDO Y CREANDO BASE DE DATOS
echo ========================================

echo.
echo 1. Conectando a MySQL...
"C:\xampp\mysql\bin\mysql.exe" -u root -P 3307 -e "DROP DATABASE IF EXISTS sistema_reconocimiento_facial; CREATE DATABASE sistema_reconocimiento_facial CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

if %ERRORLEVEL% EQU 0 (
    echo ✅ Base de datos creada exitosamente
) else (
    echo ❌ Error creando la base de datos
    pause
    exit /b 1
)

echo.
echo 2. Ejecutando script SQL...
"C:\xampp\mysql\bin\mysql.exe" -u root -P 3307 sistema_reconocimiento_facial < database_simple.sql

if %ERRORLEVEL% EQU 0 (
    echo ✅ Estructura de tablas creada exitosamente
) else (
    echo ❌ Error ejecutando el script SQL
    pause
    exit /b 1
)

echo.
echo 3. Verificando estructura de la tabla usuarios...
"C:\xampp\mysql\bin\mysql.exe" -u root -P 3307 -e "USE sistema_reconocimiento_facial; DESCRIBE usuarios;"

echo.
echo ========================================
echo BASE DE DATOS LISTA PARA USAR
echo ========================================
pause