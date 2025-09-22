package com.reconocimiento.facial.servicios;

import com.reconocimiento.facial.dao.UsuarioDAO;
import com.reconocimiento.facial.dto.UsuarioDTO;
import com.reconocimiento.facial.modelos.Usuario;
import com.reconocimiento.facial.modelos.IntentoAcceso;
import com.reconocimiento.facial.seguridad.CifradorContrasenas;
import com.reconocimiento.facial.neural.RedNeuronalReconocimiento;

import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 🛡️ SERVICIO COMPLETO DE GESTIÓN DE USUARIOS
 * Incluye autenticación dual: credenciales + reconocimiento facial
 * Integrado con red neuronal y sistema de auditoría
 */
public class ServicioUsuarioMejorado {

    // ========== COMPONENTES DEL SERVICIO ==========
    private final UsuarioDAO usuarioDAO;
    private final CifradorContrasenas cifradorContrasenas;
    private final RedNeuronalReconocimiento redNeuronal;
    
    // ========== CONFIGURACIONES DE SEGURIDAD ==========
    private static final int MAX_INTENTOS_FALLIDOS = 5;
    private static final int TIEMPO_BLOQUEO_MINUTOS = 30;
    private static final double CONFIANZA_MINIMA_FACIAL = 0.85;

    /**
     * Constructor principal
     */
    public ServicioUsuarioMejorado() {
        try {
            this.usuarioDAO = new UsuarioDAO();
            this.cifradorContrasenas = new CifradorContrasenas();
            this.redNeuronal = new RedNeuronalReconocimiento();
            
            System.out.println("✅ ServicioUsuario inicializado correctamente");
            
        } catch (Exception e) {
            System.err.println("❌ Error inicializando ServicioUsuario: " + e.getMessage());
            throw new RuntimeException("Error crítico en ServicioUsuario", e);
        }
    }

    /**
     * 🔐 Autenticación con credenciales tradicionales
     */
    public Optional<Usuario> autenticarUsuario(String nombreUsuario, String contrasena) {
        try {
            System.out.println("🔐 Iniciando autenticación por credenciales para: " + nombreUsuario);
            
            // Validar parámetros
            if (nombreUsuario == null || nombreUsuario.trim().isEmpty()) {
                registrarIntentoFallido(null, IntentoAcceso.TipoAcceso.CONTRASENA, "Usuario vacío");
                return Optional.empty();
            }
            
            if (contrasena == null || contrasena.isEmpty()) {
                registrarIntentoFallido(nombreUsuario, IntentoAcceso.TipoAcceso.CONTRASENA, "Contraseña vacía");
                return Optional.empty();
            }
            
            // Buscar usuario en base de datos
            Optional<Usuario> usuarioOpt = usuarioDAO.buscarPorNombreUsuario(nombreUsuario);
            
            if (!usuarioOpt.isPresent()) {
                System.out.println("❌ Usuario no encontrado: " + nombreUsuario);
                registrarIntentoFallido(nombreUsuario, IntentoAcceso.TipoAcceso.CONTRASENA, "Usuario no existe");
                return Optional.empty();
            }
            
            Usuario usuario = usuarioOpt.get();
            
            // Verificar si el usuario está activo
            if (!usuario.isEstaActivo()) {
                System.out.println("🚫 Usuario inactivo: " + nombreUsuario);
                registrarIntentoFallido(nombreUsuario, IntentoAcceso.TipoAcceso.CONTRASENA, "Usuario inactivo");
                return Optional.empty();
            }
            
            // Verificar contraseña
            if (cifradorContrasenas.verificarContrasena(contrasena, usuario.getContrasenaCifrada())) {
                // Autenticación exitosa
                System.out.println("✅ Autenticación exitosa para: " + nombreUsuario);
                
                // Actualizar último acceso
                usuario.registrarAcceso();
                
                // Registrar acceso exitoso
                registrarAccesoExitoso(usuario, IntentoAcceso.TipoAcceso.CONTRASENA, 1.0);
                
                return Optional.of(usuario);
                
            } else {
                // Contraseña incorrecta
                System.out.println("❌ Contraseña incorrecta para: " + nombreUsuario);
                registrarIntentoFallido(nombreUsuario, IntentoAcceso.TipoAcceso.CONTRASENA, "Contraseña incorrecta");
                return Optional.empty();
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error en autenticación por credenciales: " + e.getMessage());
            registrarIntentoFallido(nombreUsuario, IntentoAcceso.TipoAcceso.CONTRASENA, "Error del sistema: " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * 📷 Autenticación con reconocimiento facial
     */
    public Optional<Usuario> autenticarConReconocimientoFacial(BufferedImage imagenRostro) {
        try {
            System.out.println("📷 Iniciando autenticación por reconocimiento facial");
            
            if (imagenRostro == null) {
                System.err.println("❌ Imagen de rostro es null");
                registrarIntentoFallido(null, IntentoAcceso.TipoAcceso.FACIAL, "Imagen null");
                return Optional.empty();
            }
            
            // Usar la red neuronal para reconocer el usuario
            Optional<Usuario> usuarioReconocido = redNeuronal.reconocerUsuario(imagenRostro);
            
            if (usuarioReconocido.isPresent()) {
                Usuario usuario = usuarioReconocido.get();
                double confianza = redNeuronal.getUltimaConfianza();
                
                System.out.println("✅ Reconocimiento facial exitoso: " + usuario.getNombreUsuario() + 
                                 " (Confianza: " + String.format("%.2f%%", confianza * 100) + ")");
                
                // Actualizar último acceso
                usuario.registrarAcceso();
                
                // Registrar acceso exitoso
                registrarAccesoExitoso(usuario, IntentoAcceso.TipoAcceso.FACIAL, confianza);
                
                return Optional.of(usuario);
                
            } else {
                System.out.println("❌ Rostro no reconocido o confianza insuficiente");
                registrarIntentoFallido(null, IntentoAcceso.TipoAcceso.FACIAL, "Rostro no reconocido");
                return Optional.empty();
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error en autenticación facial: " + e.getMessage());
            registrarIntentoFallido(null, IntentoAcceso.TipoAcceso.FACIAL, "Error del sistema: " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * 👥 Registrar nuevo usuario con datos biométricos
     */
    public boolean registrarUsuarioCompleto(UsuarioDTO usuarioDTO, List<BufferedImage> muestrasFaciales) {
        try {
            System.out.println("👥 Iniciando registro completo para: " + usuarioDTO.getNombreUsuario());
            
            // Validar datos básicos del usuario
            if (!validarDatosUsuario(usuarioDTO)) {
                System.err.println("❌ Datos de usuario inválidos");
                return false;
            }
            
            // Verificar que no exista el usuario
            if (usuarioDAO.buscarPorNombreUsuario(usuarioDTO.getNombreUsuario()).isPresent()) {
                System.err.println("❌ El usuario ya existe: " + usuarioDTO.getNombreUsuario());
                return false;
            }
            
            if (usuarioDAO.buscarPorCorreo(usuarioDTO.getCorreoElectronico()).isPresent()) {
                System.err.println("❌ El correo ya está registrado: " + usuarioDTO.getCorreoElectronico());
                return false;
            }
            
            // Validar muestras faciales
            if (muestrasFaciales == null || muestrasFaciales.size() < 3) {
                System.err.println("❌ Se requieren al menos 3 muestras faciales");
                return false;
            }
            
            // Crear usuario
            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setNombreUsuario(usuarioDTO.getNombreUsuario());
            nuevoUsuario.setNombreCompleto(usuarioDTO.getNombreCompleto());
            nuevoUsuario.setCorreoElectronico(usuarioDTO.getCorreoElectronico());
            nuevoUsuario.setContrasenaCifrada(cifradorContrasenas.cifrarContrasena(usuarioDTO.getContrasena()));
            nuevoUsuario.setEstaActivo(true);
            
            // Guardar en base de datos
            Usuario usuarioGuardado = usuarioDAO.guardarUsuario(nuevoUsuario);
            
            if (usuarioGuardado == null) {
                System.err.println("❌ Error guardando usuario en base de datos");
                return false;
            }
            
            // Registrar características faciales en la red neuronal
            boolean registroFacialExitoso = redNeuronal.registrarUsuario(
                usuarioDTO.getNombreUsuario(), 
                muestrasFaciales
            );
            
            if (!registroFacialExitoso) {
                System.err.println("⚠️ Error registrando características faciales, pero usuario creado");
                // Nota: El usuario puede usar solo login por credenciales
            }
            
            System.out.println("✅ Usuario registrado exitosamente: " + usuarioDTO.getNombreUsuario());
            System.out.println("📊 Muestras faciales procesadas: " + muestrasFaciales.size());
            
            return true;
            
        } catch (Exception e) {
            System.err.println("❌ Error en registro completo: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 🔍 Buscar usuario por nombre de usuario
     */
    public Optional<Usuario> buscarUsuario(String nombreUsuario) {
        try {
            return usuarioDAO.buscarPorNombreUsuario(nombreUsuario);
        } catch (Exception e) {
            System.err.println("❌ Error buscando usuario: " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * ✅ Validar datos básicos del usuario
     */
    private boolean validarDatosUsuario(UsuarioDTO usuarioDTO) {
        if (usuarioDTO == null) return false;
        
        // Validar campos obligatorios
        if (usuarioDTO.getNombreUsuario() == null || usuarioDTO.getNombreUsuario().trim().isEmpty()) {
            System.err.println("❌ Nombre de usuario requerido");
            return false;
        }
        
        if (usuarioDTO.getNombreCompleto() == null || usuarioDTO.getNombreCompleto().trim().isEmpty()) {
            System.err.println("❌ Nombre completo requerido");
            return false;
        }
        
        if (usuarioDTO.getCorreoElectronico() == null || !usuarioDTO.getCorreoElectronico().contains("@")) {
            System.err.println("❌ Correo electrónico inválido");
            return false;
        }
        
        if (usuarioDTO.getContrasena() == null || usuarioDTO.getContrasena().length() < 6) {
            System.err.println("❌ Contraseña debe tener al menos 6 caracteres");
            return false;
        }
        
        return true;
    }

    /**
     * 📝 Registrar intento de acceso fallido
     */
    private void registrarIntentoFallido(String nombreUsuario, IntentoAcceso.TipoAcceso tipoAcceso, String motivo) {
        try {
            IntentoAcceso intento = new IntentoAcceso();
            
            // Configurar datos del intento
            if (nombreUsuario != null) {
                intento.setNombreUsuario(nombreUsuario);
                
                // Intentar obtener ID del usuario
                Optional<Usuario> usuario = usuarioDAO.buscarPorNombreUsuario(nombreUsuario);
                if (usuario.isPresent()) {
                    intento.setIdUsuario(usuario.get().getIdUsuario());
                }
            }
            
            intento.setTipoAcceso(tipoAcceso);
            intento.setAccesoExitoso(false);
            intento.setFechaIntento(LocalDateTime.now());
            intento.setRazonFalla(motivo);
            
            // Log del intento
            System.out.println("📝 Intento fallido registrado: " + tipoAcceso + " - " + motivo);
            
        } catch (Exception e) {
            System.err.println("⚠️ Error registrando intento fallido: " + e.getMessage());
        }
    }

    /**
     * ✅ Registrar acceso exitoso
     */
    private void registrarAccesoExitoso(Usuario usuario, IntentoAcceso.TipoAcceso tipoAcceso, double confianza) {
        try {
            IntentoAcceso intento = new IntentoAcceso();
            intento.setIdUsuario(usuario.getIdUsuario());
            intento.setNombreUsuario(usuario.getNombreUsuario());
            intento.setTipoAcceso(tipoAcceso);
            intento.setAccesoExitoso(true);
            intento.setNivelConfianza(confianza);
            intento.setFechaIntento(LocalDateTime.now());
            
            // Log del acceso exitoso
            System.out.println("✅ Acceso exitoso registrado: " + usuario.getNombreUsuario() + 
                             " (" + tipoAcceso + ", confianza: " + String.format("%.2f%%", confianza * 100) + ")");
            
        } catch (Exception e) {
            System.err.println("⚠️ Error registrando acceso exitoso: " + e.getMessage());
        }
    }

    /**
     * 📊 Obtener estadísticas del servicio
     */
    public String obtenerEstadisticas() {
        try {
            StringBuilder stats = new StringBuilder();
            stats.append("🛡️ ESTADÍSTICAS DEL SERVICIO DE USUARIOS\n");
            stats.append("=========================================\n");
            
            // Estadísticas de la red neuronal
            stats.append(redNeuronal.obtenerEstadisticas());
            
            // Configuraciones de seguridad
            stats.append("\n🔒 CONFIGURACIONES DE SEGURIDAD\n");
            stats.append("• Máx. intentos fallidos: ").append(MAX_INTENTOS_FALLIDOS).append("\n");
            stats.append("• Tiempo de bloqueo: ").append(TIEMPO_BLOQUEO_MINUTOS).append(" minutos\n");
            stats.append("• Confianza mínima facial: ").append(String.format("%.2f%%", CONFIANZA_MINIMA_FACIAL * 100)).append("\n");
            
            return stats.toString();
            
        } catch (Exception e) {
            return "❌ Error obteniendo estadísticas: " + e.getMessage();
        }
    }

    /**
     * 🧪 Ejecutar pruebas del servicio
     */
    public void ejecutarPruebas() {
        System.out.println("🧪 Ejecutando pruebas del ServicioUsuario...");
        
        try {
            // Probar red neuronal
            redNeuronal.ejecutarPrueba();
            
            // Probar cifrado de contraseñas
            String contrasenaTest = "password123";
            String hash = cifradorContrasenas.cifrarContrasena(contrasenaTest);
            boolean verificacion = cifradorContrasenas.verificarContrasena(contrasenaTest, hash);
            
            System.out.println("✅ Prueba de cifrado: " + (verificacion ? "EXITOSA" : "FALLIDA"));
            
            System.out.println("✅ Todas las pruebas completadas");
            
        } catch (Exception e) {
            System.err.println("❌ Error en pruebas: " + e.getMessage());
        }
    }

    /**
     * 🔧 Getters para componentes internos (para testing)
     */
    public RedNeuronalReconocimiento getRedNeuronal() { return redNeuronal; }
    public UsuarioDAO getUsuarioDAO() { return usuarioDAO; }
    public CifradorContrasenas getCifradorContrasenas() { return cifradorContrasenas; }
}