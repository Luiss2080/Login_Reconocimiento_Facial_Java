package com.reconocimiento.facial.excepciones;

/**
 * Excepción específica para operaciones relacionadas con usuarios
 * Proporciona información detallada sobre errores en la gestión de usuarios
 */
public class ExcepcionUsuario extends Exception {

    private static final long serialVersionUID = 1L;

    public enum TipoError {
        USUARIO_NO_ENCONTRADO("Usuario no encontrado"),
        USUARIO_YA_EXISTE("Usuario ya existe"),
        CORREO_YA_EXISTE("Correo electrónico ya registrado"),
        CREDENCIALES_INVALIDAS("Credenciales inválidas"),
        CUENTA_INACTIVA("Cuenta desactivada"),
        CUENTA_BLOQUEADA("Cuenta bloqueada temporalmente"),
        DATOS_INVALIDOS("Datos de usuario inválidos"),
        CONTRASENA_DEBIL("Contraseña no cumple requisitos de seguridad"),
        FORMATO_CORREO_INVALIDO("Formato de correo electrónico inválido"),
        NOMBRE_USUARIO_INVALIDO("Nombre de usuario inválido"),
        PERMISOS_INSUFICIENTES("Permisos insuficientes para la operación"),
        ERROR_INTERNO("Error interno del sistema");

        private final String descripcion;

        TipoError(String descripcion) {
            this.descripcion = descripcion;
        }

        public String getDescripcion() {
            return descripcion;
        }
    }

    private final TipoError tipoError;
    private final String detalles;

    public ExcepcionUsuario(String mensaje) {
        super(mensaje);
        this.tipoError = TipoError.ERROR_INTERNO;
        this.detalles = null;
    }

    public ExcepcionUsuario(String mensaje, Throwable causa) {
        super(mensaje, causa);
        this.tipoError = TipoError.ERROR_INTERNO;
        this.detalles = null;
    }

    public ExcepcionUsuario(TipoError tipoError) {
        super(tipoError.getDescripcion());
        this.tipoError = tipoError;
        this.detalles = null;
    }

    public ExcepcionUsuario(TipoError tipoError, String detalles) {
        super(tipoError.getDescripcion() + ": " + detalles);
        this.tipoError = tipoError;
        this.detalles = detalles;
    }

    public ExcepcionUsuario(TipoError tipoError, String detalles, Throwable causa) {
        super(tipoError.getDescripcion() + ": " + detalles, causa);
        this.tipoError = tipoError;
        this.detalles = detalles;
    }

    public TipoError getTipoError() {
        return tipoError;
    }

    public String getDetalles() {
        return detalles;
    }

    /**
     * Obtiene el mensaje completo del error
     */
    public String getMensajeCompleto() {
        StringBuilder mensaje = new StringBuilder(tipoError.getDescripcion());

        if (detalles != null && !detalles.isEmpty()) {
            mensaje.append(": ").append(detalles);
        }

        return mensaje.toString();
    }

    /**
     * Verifica si el error es crítico
     */
    public boolean esCritico() {
        return tipoError == TipoError.ERROR_INTERNO;
    }

    /**
     * Verifica si el error es relacionado con autenticación
     */
    public boolean esErrorAutenticacion() {
        return tipoError == TipoError.CREDENCIALES_INVALIDAS ||
               tipoError == TipoError.CUENTA_INACTIVA ||
               tipoError == TipoError.CUENTA_BLOQUEADA;
    }

    /**
     * Verifica si el error es de validación de datos
     */
    public boolean esErrorValidacion() {
        return tipoError == TipoError.DATOS_INVALIDOS ||
               tipoError == TipoError.CONTRASENA_DEBIL ||
               tipoError == TipoError.FORMATO_CORREO_INVALIDO ||
               tipoError == TipoError.NOMBRE_USUARIO_INVALIDO;
    }

    @Override
    public String toString() {
        return "ExcepcionUsuario{" +
                "tipoError=" + tipoError +
                ", mensaje='" + getMessage() + '\'' +
                ", detalles='" + detalles + '\'' +
                '}';
    }
}