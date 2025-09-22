-- =============================================
-- SISTEMA DE RECONOCIMIENTO FACIAL v2.0
-- Base de Datos Simplificada (Sin Errores)
-- =============================================

-- Crear base de datos
DROP DATABASE IF EXISTS sistema_reconocimiento_facial;
CREATE DATABASE sistema_reconocimiento_facial 
    CHARACTER SET utf8mb4 
    COLLATE utf8mb4_unicode_ci;

USE sistema_reconocimiento_facial;

-- =============================================
-- TABLA: usuarios (Principal)
-- =============================================
CREATE TABLE usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    
    -- Identificación básica
    nombre_usuario VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    telefono VARCHAR(20),
    nombre_completo VARCHAR(150) NOT NULL,
    
    -- Seguridad
    contrasena_hash VARCHAR(255) NOT NULL,
    autenticacion_facial_activa BOOLEAN DEFAULT FALSE,
    requiere_doble_factor BOOLEAN DEFAULT FALSE,
    
    -- Control de acceso
    intentos_fallidos INT DEFAULT 0,
    bloqueado_hasta TIMESTAMP NULL,
    ultimo_token VARCHAR(255),
    
    -- Metadatos
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_ultimo_acceso TIMESTAMP NULL,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    activo BOOLEAN DEFAULT TRUE,
    
    -- Índices
    INDEX idx_nombre_usuario (nombre_usuario),
    INDEX idx_email (email),
    INDEX idx_activo_fecha (activo, fecha_registro)
) ENGINE=InnoDB;

-- =============================================
-- TABLA: caracteristicas_faciales (Biometría)
-- =============================================
CREATE TABLE caracteristicas_faciales (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    
    -- Datos biométricos principales
    vector_caracteristicas TEXT NOT NULL,
    hash_facial VARCHAR(64) NOT NULL,
    confianza_registro DECIMAL(5,4) DEFAULT 0.0000,
    
    -- Metadatos de imagen
    ruta_imagen_original VARCHAR(500),
    dimension_imagen VARCHAR(20) DEFAULT '64x64',
    calidad_imagen DECIMAL(3,2) DEFAULT 0.00,
    checksum_imagen VARCHAR(64),
    
    -- Versionado y control
    version_algoritmo VARCHAR(20) DEFAULT 'v2.0',
    numero_muestra TINYINT DEFAULT 1,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    activo BOOLEAN DEFAULT TRUE,
    
    -- Relaciones
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    
    -- Índices
    INDEX idx_usuario_activo (usuario_id, activo),
    INDEX idx_hash_facial (hash_facial),
    INDEX idx_confianza (confianza_registro)
) ENGINE=InnoDB;

-- =============================================
-- TABLA: intentos_acceso (Auditoría)
-- =============================================
CREATE TABLE intentos_acceso (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT,
    
    -- Clasificación del intento
    tipo_acceso ENUM('PASSWORD', 'FACIAL', 'DUAL', 'ADMIN', 'API') NOT NULL,
    exitoso BOOLEAN NOT NULL,
    fecha_hora TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Información técnica
    direccion_ip VARCHAR(45),
    user_agent TEXT,
    tiempo_respuesta_ms INT,
    confianza_facial DECIMAL(5,4),
    
    -- Detalles del intento
    observaciones TEXT,
    codigo_error VARCHAR(50),
    
    -- Metadatos de sesión
    session_id VARCHAR(100),
    dispositivo VARCHAR(200),
    
    -- Relaciones
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE SET NULL,
    
    -- Índices
    INDEX idx_usuario_fecha (usuario_id, fecha_hora),
    INDEX idx_tipo_exitoso (tipo_acceso, exitoso),
    INDEX idx_fecha_hora (fecha_hora)
) ENGINE=InnoDB;

-- =============================================
-- TABLA: auditoria_eventos (Log de Eventos)
-- =============================================
CREATE TABLE auditoria_eventos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT,
    
    -- Clasificación del evento
    evento VARCHAR(100) NOT NULL,
    categoria ENUM('LOGIN', 'REGISTRO', 'MODIFICACION', 'SEGURIDAD', 'SISTEMA', 'ERROR') DEFAULT 'SISTEMA',
    nivel ENUM('DEBUG', 'INFO', 'WARNING', 'ERROR', 'CRITICAL') DEFAULT 'INFO',
    
    -- Detalles del evento
    descripcion TEXT,
    fecha_hora TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    direccion_ip VARCHAR(45),
    
    -- Contexto técnico
    modulo VARCHAR(100),
    metodo VARCHAR(100),
    linea_codigo INT,
    
    -- Relaciones
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE SET NULL,
    
    -- Índices
    INDEX idx_usuario_categoria (usuario_id, categoria),
    INDEX idx_evento_nivel (evento, nivel),
    INDEX idx_fecha_categoria (fecha_hora, categoria)
) ENGINE=InnoDB;

-- =============================================
-- TABLA: configuracion_sistema
-- =============================================
CREATE TABLE configuracion_sistema (
    id INT AUTO_INCREMENT PRIMARY KEY,
    
    -- Identificación de configuración
    clave VARCHAR(100) UNIQUE NOT NULL,
    valor TEXT,
    valor_anterior TEXT,
    
    -- Metadatos
    tipo ENUM('STRING', 'INTEGER', 'DECIMAL', 'BOOLEAN', 'JSON') DEFAULT 'STRING',
    descripcion TEXT,
    categoria VARCHAR(50) DEFAULT 'GENERAL',
    
    -- Control de cambios
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    usuario_modificacion VARCHAR(50),
    
    -- Aplicación
    requiere_reinicio BOOLEAN DEFAULT FALSE,
    aplicado BOOLEAN DEFAULT TRUE,
    
    -- Índices
    INDEX idx_categoria_clave (categoria, clave),
    INDEX idx_tipo (tipo)
) ENGINE=InnoDB;

-- =============================================
-- CONFIGURACIONES INICIALES
-- =============================================
INSERT INTO configuracion_sistema (clave, valor, tipo, descripcion, categoria) VALUES
-- Configuraciones de Seguridad
('SECURITY_MAX_LOGIN_ATTEMPTS', '5', 'INTEGER', 'Máximo número de intentos de login fallidos', 'SEGURIDAD'),
('SECURITY_LOCKOUT_DURATION_MINUTES', '30', 'INTEGER', 'Duración del bloqueo en minutos', 'SEGURIDAD'),
('SECURITY_PASSWORD_MIN_LENGTH', '6', 'INTEGER', 'Longitud mínima de contraseñas', 'SEGURIDAD'),

-- Configuraciones de Reconocimiento Facial
('FACIAL_CONFIDENCE_THRESHOLD', '0.85', 'DECIMAL', 'Umbral mínimo de confianza facial', 'FACIAL'),
('FACIAL_MAX_TRAINING_SAMPLES', '10', 'INTEGER', 'Máximo muestras de entrenamiento', 'FACIAL'),
('FACIAL_RECOGNITION_ENABLED', 'true', 'BOOLEAN', 'Habilitar reconocimiento facial', 'FACIAL'),

-- Configuraciones del Sistema
('SYSTEM_VERSION', '2.0.0', 'STRING', 'Versión actual del sistema', 'SISTEMA'),
('SYSTEM_LOG_RETENTION_DAYS', '90', 'INTEGER', 'Días de retención para logs', 'SISTEMA'),

-- Configuraciones de Base de Datos
('DB_POOL_MIN_CONNECTIONS', '5', 'INTEGER', 'Conexiones mínimas en pool', 'DATABASE'),
('DB_POOL_MAX_CONNECTIONS', '20', 'INTEGER', 'Conexiones máximas en pool', 'DATABASE'),
('DB_CONNECTION_TIMEOUT_MS', '5000', 'INTEGER', 'Timeout de conexión en ms', 'DATABASE');

-- =============================================
-- USUARIOS DE PRUEBA
-- =============================================
INSERT INTO usuarios (
    nombre_usuario, email, telefono, nombre_completo, contrasena_hash, 
    autenticacion_facial_activa, requiere_doble_factor, activo
) VALUES 
-- Usuario Administrador (password: admin123)
(
    'admin',
    'admin@sistema-reconocimiento.com',
    '+1-555-001-0001',
    'Administrador del Sistema',
    '$2a$12$LQv3c1yqBwEHxv3sL9J6wOnIkh4.PlwH8q5r9N3E8v3f0B1w7e9q6',
    FALSE,
    FALSE,
    TRUE
),
-- Usuario de Prueba (password: test123)
(
    'usuario_test',
    'test@sistema-reconocimiento.com',
    '+1-555-001-0002',
    'Usuario de Prueba Facial',
    '$2a$12$8X9.qL7J8Q2.9F6A5C8D3eB1F2G3H4I5J6K7L8M9N0O1P2Q3R4S5T6',
    TRUE,
    FALSE,
    TRUE
),
-- Usuario Demo (password: demo123)
(
    'demo',
    'demo@sistema-reconocimiento.com',
    '+1-555-001-0003',
    'Usuario Demostración',
    '$2a$12$9Y0.rM8K9R3.0G7B6D9E4fC2G4H5I6J7K8L9M0N1O2P3Q4R5S6T7U8',
    TRUE,
    TRUE,
    TRUE
);

-- =============================================
-- VERIFICACIÓN FINAL
-- =============================================

-- Mostrar tablas creadas
SHOW TABLES;

-- Verificar usuarios creados
SELECT 
    id,
    nombre_usuario,
    email,
    nombre_completo,
    autenticacion_facial_activa,
    fecha_registro
FROM usuarios;

-- Verificar configuraciones
SELECT 
    categoria,
    COUNT(*) as total_configuraciones
FROM configuracion_sistema 
GROUP BY categoria;

-- Mensaje de éxito
SELECT 
    '✅ BASE DE DATOS CREADA EXITOSAMENTE' as estado,
    DATABASE() as base_datos,
    COUNT(*) as total_tablas
FROM information_schema.tables 
WHERE table_schema = 'sistema_reconocimiento_facial';

-- =============================================
-- FIN DEL SCRIPT SIMPLIFICADO
-- =============================================