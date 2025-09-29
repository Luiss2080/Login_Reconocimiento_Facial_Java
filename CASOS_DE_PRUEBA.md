# 🧪 CASOS DE PRUEBA - REGISTRO FLEXIBLE

## 🎯 **Escenarios de Prueba**

### **✅ Prueba 1: Registro Completo CON Reconocimiento Facial**
**Objetivo**: Verificar que el registro con reconocimiento facial funciona normalmente

**Pasos**:
1. Ejecutar `FormularioRegistroCompleto`
2. Completar todos los campos:
   - **Usuario**: `testuser1`
   - **Nombre**: `Juan Carlos Pérez`
   - **Correo**: `juan.perez@email.com`
   - **Teléfono**: `555-0123`
   - **Contraseña**: `MiContra123!`
   - **Confirmar**: `MiContra123!`
3. Capturar 5 muestras faciales con la cámara
4. Hacer clic en "Registrar"

**Resultado Esperado**:
- ✅ Estado: "Listo para registrar CON reconocimiento facial"
- ✅ Registro exitoso con muestras faciales guardadas
- ✅ Mensaje: "INFO: Registrando con 5 muestras faciales"

---

### **✅ Prueba 2: Registro SIN Reconocimiento Facial**
**Objetivo**: Verificar que el registro funciona sin capturar muestras faciales

**Pasos**:
1. Ejecutar `FormularioRegistroCompleto`
2. Completar solo los campos obligatorios:
   - **Usuario**: `testuser2`
   - **Nombre**: `María Elena García`
   - **Correo**: `maria.garcia@email.com`
   - **Teléfono**: *(opcional, puede dejarse vacío)*
   - **Contraseña**: `Segura456@`
   - **Confirmar**: `Segura456@`
3. NO capturar muestras faciales (dejar en 0/5)
4. Hacer clic en "Registrar"
5. Confirmar en el diálogo: "¿Deseas continuar sin reconocimiento facial?"

**Resultado Esperado**:
- ✅ Estado: "Listo para registrar SIN reconocimiento facial (opcional)"
- ⚠️ Diálogo de confirmación aparece
- ✅ Registro exitoso sin muestras faciales
- ✅ Mensaje: "INFO: Registrando SIN muestras faciales"

---

### **🔒 Prueba 3: Validación de Contraseñas**
**Objetivo**: Verificar que las validaciones de seguridad siguen funcionando

**Pasos**:
1. Intentar registrar con contraseña débil: `123456`
2. Intentar registrar con contraseñas que no coinciden
3. Registrar con contraseña segura: `SuperSegura789!`

**Resultado Esperado**:
- ❌ Contraseña débil rechazada con mensaje específico
- ❌ Contraseñas diferentes rechazadas
- ✅ Contraseña segura aceptada

---

### **📧 Prueba 4: Validación de Correo**
**Objetivo**: Verificar validación de formato de correo electrónico

**Pasos**:
1. Intentar con correo inválido: `correo-malo`
2. Intentar con correo válido: `usuario@dominio.com`

**Resultado Esperado**:
- ❌ Correo inválido rechazado
- ✅ Correo válido aceptado

---

### **👤 Prueba 5: Usuarios Duplicados**
**Objetivo**: Verificar que no se pueden crear usuarios duplicados

**Pasos**:
1. Registrar usuario: `testdup`
2. Intentar registrar otro usuario con el mismo nombre: `testdup`

**Resultado Esperado**:
- ✅ Primer registro exitoso
- ❌ Segundo registro rechazado con mensaje de usuario existente

---

## 🛠️ **Comandos de Prueba**

### **Ejecutar Formulario de Registro**:
```bash
cd "c:\xampp\htdocs\Login_Reconocimiento_Facial_Java"
mvn exec:java -Dexec.mainClass="com.reconocimiento.facial.formularios.FormularioRegistroCompleto"
```

### **Ejecutar Aplicación Principal**:
```bash
cd "c:\xampp\htdocs\Login_Reconocimiento_Facial_Java"
mvn exec:java -Dexec.mainClass="com.reconocimiento.facial.AplicacionPrincipal"
```

### **Compilar Proyecto**:
```bash
mvn compile
```

### **Verificar Base de Datos**:
```sql
-- Ver usuarios registrados
SELECT nombre_usuario, nombre_completo, correo, tiene_biometria 
FROM usuarios 
ORDER BY fecha_registro DESC;

-- Contar muestras faciales por usuario
SELECT u.nombre_usuario, COUNT(cf.id) as muestras_faciales
FROM usuarios u
LEFT JOIN caracteristicas_faciales cf ON u.id = cf.usuario_id
GROUP BY u.id, u.nombre_usuario;
```

---

## 📊 **Datos de Prueba Sugeridos**

### **✅ Usuarios de Prueba Válidos**:

**Usuario 1 - Con Reconocimiento Facial**:
- Usuario: `admin_test`
- Nombre: `Administrador de Pruebas`
- Correo: `admin@test.com`
- Contraseña: `AdminTest123!`

**Usuario 2 - Sin Reconocimiento Facial**:
- Usuario: `simple_user`
- Nombre: `Usuario Simple`
- Correo: `user@simple.com`
- Contraseña: `Simple987@`

**Usuario 3 - Caso Especial**:
- Usuario: `special.user`
- Nombre: `José María Hernández-López`
- Correo: `jose.maria@empresa.co.mx`
- Contraseña: `Empresa2024#`

### **❌ Casos Inválidos para Probar**:

**Contraseñas Débiles**:
- `123456`
- `password`
- `abc123`
- `usuario`

**Correos Inválidos**:
- `correo-sin-arroba`
- `@dominio.com`
- `usuario@`
- `usuario.dominio.com`

**Campos Vacíos**:
- Usuario en blanco
- Nombre en blanco  
- Correo en blanco
- Contraseña en blanco

---

## 🎯 **Criterios de Éxito**

### **✅ El sistema debe permitir**:
- Registro con todos los campos + reconocimiento facial
- Registro con solo campos obligatorios (sin reconocimiento facial)
- Validación correcta de contraseñas seguras
- Validación correcta de formatos de correo
- Prevención de usuarios duplicados

### **❌ El sistema debe rechazar**:
- Contraseñas que no cumplan la política de seguridad
- Correos con formato inválido
- Campos obligatorios vacíos
- Nombres de usuario duplicados

### **⚠️ El sistema debe informar**:
- Cuándo el reconocimiento facial es opcional
- Qué campos son obligatorios vs opcionales
- Errores específicos con mensajes claros
- Estado actual del formulario en tiempo real

---

**Última actualización**: 29 de septiembre de 2025  
**Responsable de pruebas**: Sistema automatizado  
**Estado**: ✅ Listo para pruebas manuales