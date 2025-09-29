@echo off
echo ========================================
echo CONFIGURANDO BASE DE DATOS (Puerto 3307)
echo ========================================

echo.
echo 1. Intentando crear base de datos...
"C:\xampp\mysql\bin\mysql.exe" -u root -P 3307 -h localhost -e "CREATE DATABASE IF NOT EXISTS sistema_reconocimiento_facial CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;" 2>nul

echo.
echo 2. Creando tabla usuarios...
"C:\xampp\mysql\bin\mysql.exe" -u root -P 3307 -h localhost sistema_reconocimiento_facial -e "DROP TABLE IF EXISTS usuarios; CREATE TABLE usuarios (id BIGINT AUTO_INCREMENT PRIMARY KEY, nombre_usuario VARCHAR(50) UNIQUE NOT NULL, email VARCHAR(100) UNIQUE NOT NULL, telefono VARCHAR(20), nombre_completo VARCHAR(150) NOT NULL, contrasena_hash VARCHAR(255) NOT NULL, autenticacion_facial_activa BOOLEAN DEFAULT FALSE, requiere_doble_factor BOOLEAN DEFAULT FALSE, intentos_fallidos INT DEFAULT 0, bloqueado_hasta TIMESTAMP NULL, ultimo_token VARCHAR(255), fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP, fecha_ultimo_acceso TIMESTAMP NULL, fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, activo BOOLEAN DEFAULT TRUE, INDEX idx_nombre_usuario (nombre_usuario), INDEX idx_email (email), INDEX idx_activo_fecha (activo, fecha_registro)) ENGINE=InnoDB;"

if %ERRORLEVEL% EQU 0 (
    echo ✅ Tabla usuarios creada exitosamente
) else (
    echo ❌ Error creando tabla usuarios
)

echo.
echo 3. Verificando estructura...
"C:\xampp\mysql\bin\mysql.exe" -u root -P 3307 -h localhost -e "USE sistema_reconocimiento_facial; DESCRIBE usuarios;"

echo.
echo ========================================
echo BASE DE DATOS CONFIGURADA
echo ========================================
pause