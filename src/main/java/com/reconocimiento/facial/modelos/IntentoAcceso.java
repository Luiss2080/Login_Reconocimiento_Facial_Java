package com.reconocimiento.facial.modelos;

import java.time.LocalDateTime;

/**
 * Modelo para registrar intentos de acceso al sistema
 * Permite auditoría y seguimiento de seguridad
 */
public class IntentoAcceso {

    public enum TipoAcceso {
        FACIAL("facial"),
        CONTRASENA("contrasena");

        private final String valor;

        TipoAcceso(String valor) {
            this.valor = valor;
        }

        public String getValor() {
            return valor;
        }

        public static TipoAcceso fromString(String texto) {
            for (TipoAcceso tipo : TipoAcceso.values()) {
                if (tipo.valor.equalsIgnoreCase(texto)) {
                    return tipo;
                }
            }
            return CONTRASENA; // Valor por defecto
        }
    }

    private int idIntento;
    private int idUsuario;
    private String nombreUsuario;
    private TipoAcceso tipoAcceso;
    private boolean accesoExitoso;
    private double nivelConfianza;
    private String direccionIp;
    private String agenteUsuario;
    private LocalDateTime fechaIntento;
    private String razonFalla;
    private long tiempoRespuesta; // en milisegundos
    private String detallesAdicionales;

    // Constructor vacío
    public IntentoAcceso() {
        this.fechaIntento = LocalDateTime.now();
        this.nivelConfianza = 0.0;
        this.tiempoRespuesta = 0L;
    }

    // Constructor para acceso por contraseña
    public IntentoAcceso(int idUsuario, String nombreUsuario, boolean accesoExitoso, String direccionIp) {
        this();
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.tipoAcceso = TipoAcceso.CONTRASENA;
        this.accesoExitoso = accesoExitoso;
        this.direccionIp = direccionIp;
    }

    // Constructor para acceso facial
    public IntentoAcceso(int idUsuario, String nombreUsuario, boolean accesoExitoso,
                        double nivelConfianza, String direccionIp) {
        this();
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.tipoAcceso = TipoAcceso.FACIAL;
        this.accesoExitoso = accesoExitoso;
        this.nivelConfianza = nivelConfianza;
        this.direccionIp = direccionIp;
    }

    // Constructor completo
    public IntentoAcceso(int idUsuario, String nombreUsuario, TipoAcceso tipoAcceso,
                        boolean accesoExitoso, double nivelConfianza, String direccionIp,
                        String agenteUsuario, String razonFalla) {
        this();
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.tipoAcceso = tipoAcceso;
        this.accesoExitoso = accesoExitoso;
        this.nivelConfianza = nivelConfianza;
        this.direccionIp = direccionIp;
        this.agenteUsuario = agenteUsuario;
        this.razonFalla = razonFalla;
    }

    // Métodos getter
    public int getIdIntento() {
        return idIntento;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public TipoAcceso getTipoAcceso() {
        return tipoAcceso;
    }

    public boolean isAccesoExitoso() {
        return accesoExitoso;
    }

    public double getNivelConfianza() {
        return nivelConfianza;
    }

    public String getDireccionIp() {
        return direccionIp;
    }

    public String getAgenteUsuario() {
        return agenteUsuario;
    }

    public LocalDateTime getFechaIntento() {
        return fechaIntento;
    }

    public String getRazonFalla() {
        return razonFalla;
    }

    public long getTiempoRespuesta() {
        return tiempoRespuesta;
    }

    public String getDetallesAdicionales() {
        return detallesAdicionales;
    }

    // Métodos setter
    public void setIdIntento(int idIntento) {
        this.idIntento = idIntento;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public void setTipoAcceso(TipoAcceso tipoAcceso) {
        this.tipoAcceso = tipoAcceso;
    }

    public void setAccesoExitoso(boolean accesoExitoso) {
        this.accesoExitoso = accesoExitoso;
    }

    public void setNivelConfianza(double nivelConfianza) {
        this.nivelConfianza = Math.max(0.0, Math.min(1.0, nivelConfianza)); // Limitar entre 0 y 1
    }

    public void setDireccionIp(String direccionIp) {
        this.direccionIp = direccionIp;
    }

    public void setAgenteUsuario(String agenteUsuario) {
        this.agenteUsuario = agenteUsuario;
    }

    public void setFechaIntento(LocalDateTime fechaIntento) {
        this.fechaIntento = fechaIntento;
    }

    public void setRazonFalla(String razonFalla) {
        this.razonFalla = razonFalla;
    }

    public void setTiempoRespuesta(long tiempoRespuesta) {
        this.tiempoRespuesta = tiempoRespuesta;
    }

    public void setDetallesAdicionales(String detallesAdicionales) {
        this.detallesAdicionales = detallesAdicionales;
    }

    // Métodos de utilidad

    /**
     * Verifica si el intento es válido
     * @return true si el intento tiene datos válidos
     */
    public boolean esIntentoValido() {
        return nombreUsuario != null && !nombreUsuario.trim().isEmpty() &&
               tipoAcceso != null &&
               fechaIntento != null;
    }

    /**
     * Verifica si es un intento de acceso facial
     * @return true si es acceso facial
     */
    public boolean esAccesoFacial() {
        return tipoAcceso == TipoAcceso.FACIAL;
    }

    /**
     * Verifica si es un intento de acceso por contraseña
     * @return true si es acceso por contraseña
     */
    public boolean esAccesoContrasena() {
        return tipoAcceso == TipoAcceso.CONTRASENA;
    }

    /**
     * Verifica si el nivel de confianza es alto (para acceso facial)
     * @param umbralMinimo Umbral mínimo de confianza
     * @return true si la confianza es alta
     */
    public boolean tieneConfianzaAlta(double umbralMinimo) {
        return esAccesoFacial() && nivelConfianza >= umbralMinimo;
    }

    /**
     * Obtiene una descripción del resultado del intento
     * @return String descriptivo del resultado
     */
    public String obtenerDescripcionResultado() {
        if (accesoExitoso) {
            if (esAccesoFacial()) {
                return String.format("Acceso facial exitoso (Confianza: %.2f%%)", nivelConfianza * 100);
            } else {
                return "Acceso por contraseña exitoso";
            }
        } else {
            String razon = razonFalla != null ? razonFalla : "Razón no especificada";
            return String.format("Acceso fallido: %s", razon);
        }
    }

    /**
     * Obtiene el tiempo de respuesta en formato legible
     * @return String con el tiempo de respuesta
     */
    public String obtenerTiempoRespuestaFormateado() {
        if (tiempoRespuesta < 1000) {
            return tiempoRespuesta + " ms";
        } else {
            return String.format("%.2f s", tiempoRespuesta / 1000.0);
        }
    }

    /**
     * Verifica si el intento representa un riesgo de seguridad
     * @return true si es potencialmente riesgoso
     */
    public boolean esRiesgoSeguridad() {
        return !accesoExitoso && (
            razonFalla != null && (
                razonFalla.contains("múltiples intentos") ||
                razonFalla.contains("IP bloqueada") ||
                razonFalla.contains("actividad sospechosa")
            )
        );
    }

    @Override
    public String toString() {
        return "IntentoAcceso{" +
                "idIntento=" + idIntento +
                ", nombreUsuario='" + nombreUsuario + '\'' +
                ", tipoAcceso=" + tipoAcceso +
                ", accesoExitoso=" + accesoExitoso +
                ", nivelConfianza=" + nivelConfianza +
                ", direccionIp='" + direccionIp + '\'' +
                ", fechaIntento=" + fechaIntento +
                ", tiempoRespuesta=" + tiempoRespuesta + "ms" +
                '}';
    }

    /**
     * Obtiene un resumen del intento para logs
     * @return String con resumen del intento
     */
    public String obtenerResumenParaLog() {
        return String.format("[%s] %s - %s %s desde %s - %s",
                fechaIntento,
                nombreUsuario,
                tipoAcceso.getValor(),
                accesoExitoso ? "EXITOSO" : "FALLIDO",
                direccionIp != null ? direccionIp : "IP desconocida",
                accesoExitoso ? obtenerDescripcionResultado() : razonFalla);
    }
}