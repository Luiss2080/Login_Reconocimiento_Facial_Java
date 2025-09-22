package com.reconocimiento.facial.servicios;

import com.reconocimiento.facial.basedatos.ConexionBaseDatos;
import com.reconocimiento.facial.excepciones.ExcepcionBaseDatos;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servicio para gestionar auditoría de accesos y eventos del sistema
 */
public class ServicioAuditoria {
    
    private static final Logger LOGGER = Logger.getLogger(ServicioAuditoria.class.getName());
    private ConexionBaseDatos conexionBD;
    
    public ServicioAuditoria() {
        try {
            this.conexionBD = ConexionBaseDatos.obtenerInstancia();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al obtener instancia de base de datos", e);
            throw new RuntimeException("No se pudo conectar a la base de datos", e);
        }
    }
    
    public ServicioAuditoria(ConexionBaseDatos conexionBD) {
        this.conexionBD = conexionBD;
    }
    
    /**
     * Registra un intento de acceso en la auditoría
     */
    public void registrarIntentoAcceso(Long usuarioId, String tipoAcceso, boolean exitoso, String observaciones) throws ExcepcionBaseDatos {
        String sql = """
            INSERT INTO intentos_acceso (usuario_id, tipo_acceso, exitoso, fecha_hora, direccion_ip, observaciones)
            VALUES (?, ?, ?, ?, ?, ?)
        """;
        
        try (Connection conn = conexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, usuarioId);
            stmt.setString(2, tipoAcceso);
            stmt.setBoolean(3, exitoso);
            stmt.setObject(4, LocalDateTime.now());
            stmt.setString(5, obtenerDireccionIP());
            stmt.setString(6, observaciones);
            
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al registrar intento de acceso", e);
            throw new ExcepcionBaseDatos(ExcepcionBaseDatos.TipoErrorBD.ERROR_INTERNO_BD, "Error al registrar auditoría");
        }
    }
    
    /**
     * Registra un evento de autenticación
     */
    public void registrarEvento(String evento, String descripcion, Long usuarioId) throws ExcepcionBaseDatos {
        String sql = """
            INSERT INTO auditoria_eventos (evento, descripcion, usuario_id, fecha_hora, direccion_ip)
            VALUES (?, ?, ?, ?, ?)
        """;
        
        try (Connection conn = conexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, evento);
            stmt.setString(2, descripcion);
            stmt.setObject(3, usuarioId);
            stmt.setObject(4, LocalDateTime.now());
            stmt.setString(5, obtenerDireccionIP());
            
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al registrar evento de auditoría", e);
            throw new ExcepcionBaseDatos(ExcepcionBaseDatos.TipoErrorBD.ERROR_INTERNO_BD, "Error al registrar evento");
        }
    }
    
    /**
     * Registra un login exitoso
     */
    public void registrarLoginExitoso(Long usuarioId, String tipoLogin) {
        try {
            registrarIntentoAcceso(usuarioId, tipoLogin, true, "Login exitoso");
            registrarEvento("LOGIN_EXITOSO", "Usuario ingresó al sistema con " + tipoLogin, usuarioId);
        } catch (ExcepcionBaseDatos e) {
            LOGGER.log(Level.WARNING, "Error al registrar login exitoso", e);
        }
    }
    
    /**
     * Registra un login fallido
     */
    public void registrarLoginFallido(String nombreUsuario, String tipoLogin, String razon) {
        try {
            registrarEvento("LOGIN_FALLIDO", 
                String.format("Intento fallido de login para usuario: %s, tipo: %s, razón: %s", 
                    nombreUsuario, tipoLogin, razon), null);
        } catch (ExcepcionBaseDatos e) {
            LOGGER.log(Level.WARNING, "Error al registrar login fallido", e);
        }
    }
    
    /**
     * Registra un nuevo usuario registrado
     */
    public void registrarNuevoUsuario(Long usuarioId, String nombreUsuario) {
        try {
            registrarEvento("USUARIO_REGISTRADO", 
                "Nuevo usuario registrado: " + nombreUsuario, usuarioId);
        } catch (ExcepcionBaseDatos e) {
            LOGGER.log(Level.WARNING, "Error al registrar nuevo usuario", e);
        }
    }
    
    /**
     * Registra un cambio de contraseña
     */
    public void registrarCambioContrasena(Long usuarioId) {
        try {
            registrarEvento("CAMBIO_CONTRASENA", 
                "Usuario cambió su contraseña", usuarioId);
        } catch (ExcepcionBaseDatos e) {
            LOGGER.log(Level.WARNING, "Error al registrar cambio de contraseña", e);
        }
    }
    
    /**
     * Registra una actualización de perfil
     */
    public void registrarActualizacionPerfil(Long usuarioId) {
        try {
            registrarEvento("PERFIL_ACTUALIZADO", 
                "Usuario actualizó su perfil", usuarioId);
        } catch (ExcepcionBaseDatos e) {
            LOGGER.log(Level.WARNING, "Error al registrar actualización de perfil", e);
        }
    }
    
    /**
     * Obtiene la dirección IP (simulado para aplicación de escritorio)
     */
    private String obtenerDireccionIP() {
        return "127.0.0.1"; // Para aplicación de escritorio
    }
}