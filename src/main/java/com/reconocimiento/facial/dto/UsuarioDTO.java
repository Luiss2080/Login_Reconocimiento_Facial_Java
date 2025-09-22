package com.reconocimiento.facial.dto;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Data Transfer Object para Usuario
 * Representa los datos de usuario que se transfieren entre capas
 * No incluye información sensible como contraseñas
 */
public class UsuarioDTO {

    private int idUsuario;
    private String nombreUsuario;
    private String correoElectronico;
    private String nombreCompleto;
    private String telefono;
    private boolean estaActivo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    private LocalDateTime ultimoAcceso;

    // Campo temporal para operaciones que requieren contraseña
    private String contrasena; // Solo para registro/actualización, no se persiste

    // Información adicional opcional
    private int numeroCaracteristicasFaciales;
    private LocalDateTime ultimoAccesoFacial;
    private boolean tieneRegistroFacial;

    // Constructor vacío
    public UsuarioDTO() {
    }

    // Constructor con campos principales
    public UsuarioDTO(String nombreUsuario, String correoElectronico, String nombreCompleto) {
        this.nombreUsuario = nombreUsuario;
        this.correoElectronico = correoElectronico;
        this.nombreCompleto = nombreCompleto;
        this.estaActivo = true;
    }

    // Constructor completo
    public UsuarioDTO(int idUsuario, String nombreUsuario, String correoElectronico,
                     String nombreCompleto, boolean estaActivo) {
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.correoElectronico = correoElectronico;
        this.nombreCompleto = nombreCompleto;
        this.estaActivo = estaActivo;
    }

    // Getters y Setters

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getCorreoElectronico() {
        return correoElectronico;
    }

    public void setCorreoElectronico(String correoElectronico) {
        this.correoElectronico = correoElectronico;
    }

    // Alias para compatibilidad
    public void setCorreo(String correo) {
        this.correoElectronico = correo;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public boolean isEstaActivo() {
        return estaActivo;
    }

    public void setEstaActivo(boolean estaActivo) {
        this.estaActivo = estaActivo;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public LocalDateTime getUltimoAcceso() {
        return ultimoAcceso;
    }

    public void setUltimoAcceso(LocalDateTime ultimoAcceso) {
        this.ultimoAcceso = ultimoAcceso;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public int getNumeroCaracteristicasFaciales() {
        return numeroCaracteristicasFaciales;
    }

    public void setNumeroCaracteristicasFaciales(int numeroCaracteristicasFaciales) {
        this.numeroCaracteristicasFaciales = numeroCaracteristicasFaciales;
    }

    public LocalDateTime getUltimoAccesoFacial() {
        return ultimoAccesoFacial;
    }

    public void setUltimoAccesoFacial(LocalDateTime ultimoAccesoFacial) {
        this.ultimoAccesoFacial = ultimoAccesoFacial;
    }

    public boolean isTieneRegistroFacial() {
        return tieneRegistroFacial;
    }

    public void setTieneRegistroFacial(boolean tieneRegistroFacial) {
        this.tieneRegistroFacial = tieneRegistroFacial;
    }

    // Métodos de utilidad

    /**
     * Verifica si los datos básicos del usuario son válidos
     */
    public boolean sonDatosValidos() {
        return nombreUsuario != null && !nombreUsuario.trim().isEmpty() &&
               correoElectronico != null && !correoElectronico.trim().isEmpty() &&
               nombreCompleto != null && !nombreCompleto.trim().isEmpty();
    }

    /**
     * Verifica si el correo electrónico tiene un formato válido
     */
    public boolean esCorreoValido() {
        if (correoElectronico == null || correoElectronico.trim().isEmpty()) {
            return false;
        }

        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return correoElectronico.matches(regex);
    }

    /**
     * Verifica si el nombre de usuario tiene un formato válido
     */
    public boolean esNombreUsuarioValido() {
        if (nombreUsuario == null || nombreUsuario.trim().isEmpty()) {
            return false;
        }

        // Solo letras, números y guiones bajos, entre 3 y 50 caracteres
        String regex = "^[a-zA-Z0-9_]{3,50}$";
        return nombreUsuario.matches(regex);
    }

    /**
     * Verifica si tiene una contraseña válida (solo para validación temporal)
     */
    public boolean esContrasenaValida() {
        if (contrasena == null || contrasena.isEmpty()) {
            return false;
        }

        // Al menos 8 caracteres, una mayúscula, una minúscula y un número
        return contrasena.length() >= 8 &&
               contrasena.matches(".*[A-Z].*") &&
               contrasena.matches(".*[a-z].*") &&
               contrasena.matches(".*[0-9].*");
    }

    /**
     * Obtiene el estado del usuario como texto
     */
    public String getEstadoTexto() {
        return estaActivo ? "Activo" : "Inactivo";
    }

    /**
     * Obtiene información resumida del usuario
     */
    public String getResumen() {
        return String.format("%s (%s) - %s", nombreCompleto, nombreUsuario, getEstadoTexto());
    }

    /**
     * Obtiene información del último acceso formateada
     */
    public String getUltimoAccesoFormateado() {
        if (ultimoAcceso == null) {
            return "Nunca";
        }

        // Formatear fecha de manera legible
        return ultimoAcceso.toString().replace("T", " ");
    }

    /**
     * Verifica si el usuario ha accedido recientemente (últimas 24 horas)
     */
    public boolean haAccedidoRecientemente() {
        if (ultimoAcceso == null) {
            return false;
        }

        LocalDateTime hace24Horas = LocalDateTime.now().minusDays(1);
        return ultimoAcceso.isAfter(hace24Horas);
    }

    /**
     * Limpia la contraseña temporal por seguridad
     */
    public void limpiarContrasena() {
        this.contrasena = null;
    }

    /**
     * Crea una copia del DTO sin información sensible
     */
    public UsuarioDTO crearCopiaSegura() {
        UsuarioDTO copia = new UsuarioDTO();
        copia.idUsuario = this.idUsuario;
        copia.nombreUsuario = this.nombreUsuario;
        copia.correoElectronico = this.correoElectronico;
        copia.nombreCompleto = this.nombreCompleto;
        copia.estaActivo = this.estaActivo;
        copia.fechaCreacion = this.fechaCreacion;
        copia.fechaActualizacion = this.fechaActualizacion;
        copia.ultimoAcceso = this.ultimoAcceso;
        copia.numeroCaracteristicasFaciales = this.numeroCaracteristicasFaciales;
        copia.ultimoAccesoFacial = this.ultimoAccesoFacial;
        copia.tieneRegistroFacial = this.tieneRegistroFacial;
        // No copiar la contraseña por seguridad
        return copia;
    }

    @Override
    public boolean equals(Object objeto) {
        if (this == objeto) return true;
        if (objeto == null || getClass() != objeto.getClass()) return false;

        UsuarioDTO that = (UsuarioDTO) objeto;
        return idUsuario == that.idUsuario &&
               Objects.equals(nombreUsuario, that.nombreUsuario);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idUsuario, nombreUsuario);
    }

    @Override
    public String toString() {
        return "UsuarioDTO{" +
                "idUsuario=" + idUsuario +
                ", nombreUsuario='" + nombreUsuario + '\'' +
                ", correoElectronico='" + correoElectronico + '\'' +
                ", nombreCompleto='" + nombreCompleto + '\'' +
                ", estaActivo=" + estaActivo +
                ", fechaCreacion=" + fechaCreacion +
                ", ultimoAcceso=" + ultimoAcceso +
                ", tieneRegistroFacial=" + tieneRegistroFacial +
                '}';
    }

    /**
     * Representación segura para logs (sin información sensible)
     */
    public String toStringSeguro() {
        return "UsuarioDTO{" +
                "idUsuario=" + idUsuario +
                ", nombreUsuario='" + nombreUsuario + '\'' +
                ", nombreCompleto='" + nombreCompleto + '\'' +
                ", estaActivo=" + estaActivo +
                ", tieneRegistroFacial=" + tieneRegistroFacial +
                '}';
    }
}