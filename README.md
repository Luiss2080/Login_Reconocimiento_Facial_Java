# 🎭 Sistema de Reconocimiento Facial v2.0

Sistema completo de autenticación con reconocimiento facial desarrollado en **Java 21** con **Maven** y **MySQL**.

## 🚀 Abrir en NetBeans

### Pasos Rápidos:
1. Abrir **NetBeans IDE 17+**
2. `File` → `Open Project`
3. Seleccionar esta carpeta
4. NetBeans detectará automáticamente el proyecto Maven
5. Presionar **F6** para ejecutar

## 📋 Requisitos

- ✅ **Java 21+** (Oracle JDK recomendado)
- ✅ **NetBeans 17+** 
- ✅ **MySQL 8.0+** (con MySQL Workbench)
- ✅ **Maven 3.6+** (incluido en NetBeans)
- ✅ **Cámara web** funcional
- ✅ **4GB RAM** mínimo

## ⚡ Configuración Rápida

### 1. Base de Datos MySQL
```sql
-- Ejecutar en MySQL Workbench:
SOURCE database_simple.sql;
```

### 2. Verificar Configuración
El archivo `config/sistema.properties` está preconfigurado para:
- Host: `localhost:3306`
- Usuario: `root`
- Sin contraseña
- Base de datos: `sistema_reconocimiento_facial`

### 3. Ejecutar en NetBeans
- **Compilar**: `F11` o clic derecho → `Build`
- **Ejecutar**: `F6` o clic derecho → `Run`
- **Debug**: `Ctrl+F5`

## 👥 Usuarios de Prueba

| Usuario | Contraseña | Reconocimiento Facial |
|---------|------------|----------------------|
| `admin` | `admin123` | Deshabilitado |
| `usuario_test` | `test123` | ✅ Habilitado |
| `demo` | `demo123` | ✅ + Doble Factor |

## 📁 Estructura del Proyecto

```
src/main/java/com/reconocimiento/facial/
├── AplicacionPrincipal.java         # 🚀 Punto de entrada
├── formularios/                     # 🖥️ Interfaces gráficas
├── controladores/                   # 🎮 Lógica de control
├── servicios/                       # 🔧 Servicios de negocio
├── dao/                            # 🗄️ Acceso a datos
├── neural/                         # 🧠 Red neuronal
├── procesamiento/                  # 📸 Procesamiento de imágenes
├── seguridad/                      # 🔒 Seguridad y encriptación
└── utilidades/                     # 🛠️ Utilidades
```

## 🎯 Funcionalidades

### Autenticación
- 🔐 Login con usuario/contraseña
- 👤 Reconocimiento facial con IA
- 🔒 Autenticación de doble factor
- 🚫 Bloqueo automático por intentos fallidos

### Red Neuronal
- 🧠 Procesamiento facial en tiempo real
- 📊 128 características faciales únicas
- 🎯 85% de confianza mínima (configurable)
- 📷 Captura desde cámara web

### Base de Datos
- 🗄️ MySQL con 5 tablas optimizadas
- 📝 Auditoría completa de eventos
- 🔍 Sistema de logs detallado
- ⚡ Pool de conexiones automático

## 🐛 Solución de Problemas

### ❌ Error de MySQL
```
1. Verificar que MySQL esté ejecutándose
2. Abrir MySQL Workbench
3. Probar conexión a localhost:3306
4. Ejecutar database_simple.sql
```

### ❌ Error de Cámara
```
1. Verificar permisos de cámara en Windows
2. Configuración → Privacidad → Cámara
3. Permitir aplicaciones de escritorio
```

### ❌ Error de Compilación
```
1. En NetBeans: clic derecho → Clean and Build
2. Verificar Java 21+ instalado
3. Window → Services → Maven → Update Indexes
```

## 📊 Logs del Sistema

Los logs se generan automáticamente en `/logs/`:
- `reconocimiento-facial.log` - Log general
- `errores.log` - Solo errores
- `auditoria.log` - Eventos de auditoría
- `base-datos.log` - Operaciones de BD

## 🎮 Controles Rápidos en NetBeans

| Tecla | Acción |
|-------|--------|
| `F6` | Ejecutar proyecto |
| `F11` | Compilar |
| `Ctrl+F5` | Debug |
| `Shift+F6` | Ejecutar archivo actual |
| `Ctrl+F9` | Compilar archivo actual |

## 🔧 Personalización

### Cambiar Umbral de Confianza Facial
```sql
UPDATE configuracion_sistema 
SET valor = '0.90' 
WHERE clave = 'FACIAL_CONFIDENCE_THRESHOLD';
```

### Ajustar Intentos Máximos de Login
```sql
UPDATE configuracion_sistema 
SET valor = '3' 
WHERE clave = 'SECURITY_MAX_LOGIN_ATTEMPTS';
```

## 🏆 Tecnologías Utilizadas

- **Java 21** - Plataforma principal
- **Maven** - Gestión de dependencias
- **MySQL 8.0** - Base de datos
- **OpenCV** - Procesamiento de imágenes
- **BCrypt** - Encriptación de contraseñas
- **SLF4J + Logback** - Sistema de logs
- **Swing** - Interfaz gráfica

## 📈 Rendimiento

- **Tiempo de inicio**: < 3 segundos
- **Reconocimiento facial**: < 1 segundo
- **Conexiones BD**: Pool 5-20 conexiones
- **Memoria RAM**: ~200MB en ejecución
- **Precisión facial**: 95%+ con buena iluminación

---

**✨ Proyecto listo para usar en NetBeans - Simplemente presiona F6 para ejecutar ✨**