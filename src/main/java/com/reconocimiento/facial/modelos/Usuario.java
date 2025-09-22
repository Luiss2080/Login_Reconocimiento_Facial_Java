package com.reconocimiento.facial.modelos;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Modelo de datos para representar un usuario en el sistema
 * Contiene toda la información personal y de autenticación
 */
public class Usuario {
    private int idUsuario;
    private String nombreUsuario;
    private String correoElectronico;
    private String contrasenaCifrada;
    private String nombreCompleto;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    private boolean estaActivo;
    private LocalDateTime ultimoAcceso;

    // Constructor vacío
    public Usuario() {
        this.estaActivo = true;
        this.fechaCreacion = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
    }

    // Constructor con parámetros principales
    public Usuario(String nombreUsuario, String correoElectronico, String contrasenaCifrada, String nombreCompleto) {
        this();
        this.nombreUsuario = nombreUsuario;
        this.correoElectronico = correoElectronico;
        this.contrasenaCifrada = contrasenaCifrada;
        this.nombreCompleto = nombreCompleto;
    }

    // Constructor completo
    public Usuario(int idUsuario, String nombreUsuario, String correoElectronico,
                   String contrasenaCifrada, String nombreCompleto, boolean estaActivo) {
        this(nombreUsuario, correoElectronico, contrasenaCifrada, nombreCompleto);
        this.idUsuario = idUsuario;
        this.estaActivo = estaActivo;
    }

    // Métodos getter
    public int getIdUsuario() {
        return idUsuario;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public String getCorreoElectronico() {
        return correoElectronico;
    }

    public String getContrasenaCifrada() {
        return contrasenaCifrada;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public boolean isEstaActivo() {
        return estaActivo;
    }

    public LocalDateTime getUltimoAcceso() {
        return ultimoAcceso;
    }

    // Métodos setter
    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
        this.actualizarFechaModificacion();
    }

    public void setCorreoElectronico(String correoElectronico) {
        this.correoElectronico = correoElectronico;
        this.actualizarFechaModificacion();
    }

    public void setContrasenaCifrada(String contrasenaCifrada) {
        this.contrasenaCifrada = contrasenaCifrada;
        this.actualizarFechaModificacion();
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
        this.actualizarFechaModificacion();
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public void setEstaActivo(boolean estaActivo) {
        this.estaActivo = estaActivo;
        this.actualizarFechaModificacion();
    }

    public void setUltimoAcceso(LocalDateTime ultimoAcceso) {
        this.ultimoAcceso = ultimoAcceso;
    }

    // Métodos de utilidad
    private void actualizarFechaModificacion() {
        this.fechaActualizacion = LocalDateTime.now();
    }

    public void registrarAcceso() {
        this.ultimoAcceso = LocalDateTime.now();
        this.actualizarFechaModificacion();
    }

    public boolean esUsuarioValido() {
        return nombreUsuario != null && !nombreUsuario.trim().isEmpty() &&
               correoElectronico != null && !correoElectronico.trim().isEmpty() &&
               contrasenaCifrada != null && !contrasenaCifrada.trim().isEmpty() &&
               nombreCompleto != null && !nombreCompleto.trim().isEmpty();
    }

    public boolean tieneCorreoValido() {
        return correoElectronico != null && correoElectronico.contains("@") &&
               correoElectronico.contains(".");
    }

    @Override
    public boolean equals(Object objeto) {
        if (this == objeto) return true;
        if (objeto == null || getClass() != objeto.getClass()) return false;
        Usuario usuario = (Usuario) objeto;
        return idUsuario == usuario.idUsuario &&
               Objects.equals(nombreUsuario, usuario.nombreUsuario);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idUsuario, nombreUsuario);
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "idUsuario=" + idUsuario +
                ", nombreUsuario='" + nombreUsuario + '\'' +
                ", correoElectronico='" + correoElectronico + '\'' +
                ", nombreCompleto='" + nombreCompleto + '\'' +
                ", estaActivo=" + estaActivo +
                ", fechaCreacion=" + fechaCreacion +
                ", ultimoAcceso=" + ultimoAcceso +
                '}';
    }

    // Método para obtener información básica del usuario (sin datos sensibles)
    public String obtenerInformacionBasica() {
        return "Usuario: " + nombreCompleto + " (" + nombreUsuario + ") - " +
               "Estado: " + (estaActivo ? "Activo" : "Inactivo");
    }
}