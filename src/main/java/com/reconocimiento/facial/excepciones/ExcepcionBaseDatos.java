package com.reconocimiento.facial.excepciones;

import java.sql.SQLException;

/**
 * Excepción específica para operaciones de base de datos
 * Encapsula errores relacionados con acceso a datos y operaciones SQL
 */
public class ExcepcionBaseDatos extends Exception {

    private static final long serialVersionUID = 1L;

    public enum TipoErrorBD {
        CONEXION_FALLIDA("Error de conexión a la base de datos"),
        CONSULTA_FALLIDA("Error ejecutando consulta SQL"),
        DATOS_DUPLICADOS("Violación de restricción de unicidad"),
        INTEGRIDAD_REFERENCIAL("Violación de integridad referencial"),
        TIMEOUT("Timeout en operación de base de datos"),
        POOL_AGOTADO("Pool de conexiones agotado"),
        TRANSACCION_FALLIDA("Error en transacción de base de datos"),
        ESQUEMA_INVALIDO("Esquema de base de datos inválido"),
        PERMISOS_INSUFICIENTES("Permisos insuficientes en base de datos"),
        BASE_DATOS_NO_DISPONIBLE("Base de datos no disponible"),
        ERROR_INTERNO_BD("Error interno de base de datos");

        private final String descripcion;

        TipoErrorBD(String descripcion) {
            this.descripcion = descripcion;
        }

        public String getDescripcion() {
            return descripcion;
        }
    }

    private final TipoErrorBD tipoError;
    private final String consultaSQL;
    private final int codigoErrorSQL;

    public ExcepcionBaseDatos(String mensaje) {
        super(mensaje);
        this.tipoError = TipoErrorBD.ERROR_INTERNO_BD;
        this.consultaSQL = null;
        this.codigoErrorSQL = -1;
    }

    public ExcepcionBaseDatos(String mensaje, Throwable causa) {
        super(mensaje, causa);
        this.tipoError = determinarTipoError(causa);
        this.consultaSQL = null;
        this.codigoErrorSQL = extraerCodigoError(causa);
    }

    public ExcepcionBaseDatos(TipoErrorBD tipoError, String mensaje) {
        super(mensaje);
        this.tipoError = tipoError;
        this.consultaSQL = null;
        this.codigoErrorSQL = -1;
    }

    public ExcepcionBaseDatos(TipoErrorBD tipoError, String mensaje, String consultaSQL) {
        super(mensaje);
        this.tipoError = tipoError;
        this.consultaSQL = consultaSQL;
        this.codigoErrorSQL = -1;
    }

    public ExcepcionBaseDatos(TipoErrorBD tipoError, String mensaje, Throwable causa) {
        super(mensaje, causa);
        this.tipoError = tipoError;
        this.consultaSQL = null;
        this.codigoErrorSQL = extraerCodigoError(causa);
    }

    public ExcepcionBaseDatos(TipoErrorBD tipoError, String mensaje, String consultaSQL, Throwable causa) {
        super(mensaje, causa);
        this.tipoError = tipoError;
        this.consultaSQL = consultaSQL;
        this.codigoErrorSQL = extraerCodigoError(causa);
    }

    public TipoErrorBD getTipoError() {
        return tipoError;
    }

    public String getConsultaSQL() {
        return consultaSQL;
    }

    public int getCodigoErrorSQL() {
        return codigoErrorSQL;
    }

    /**
     * Obtiene el mensaje completo del error incluyendo detalles técnicos
     */
    public String getMensajeCompleto() {
        StringBuilder mensaje = new StringBuilder();
        mensaje.append(tipoError.getDescripcion());

        if (getMessage() != null && !getMessage().isEmpty()) {
            mensaje.append(": ").append(getMessage());
        }

        if (codigoErrorSQL != -1) {
            mensaje.append(" (Código SQL: ").append(codigoErrorSQL).append(")");
        }

        if (consultaSQL != null && !consultaSQL.isEmpty()) {
            mensaje.append("\nConsulta: ").append(consultaSQL);
        }

        return mensaje.toString();
    }

    /**
     * Verifica si el error es crítico y requiere intervención inmediata
     */
    public boolean esCritico() {
        return tipoError == TipoErrorBD.BASE_DATOS_NO_DISPONIBLE ||
               tipoError == TipoErrorBD.CONEXION_FALLIDA ||
               tipoError == TipoErrorBD.ESQUEMA_INVALIDO ||
               tipoError == TipoErrorBD.ERROR_INTERNO_BD;
    }

    /**
     * Verifica si el error es temporal y puede recuperarse con reintento
     */
    public boolean esTemporal() {
        return tipoError == TipoErrorBD.TIMEOUT ||
               tipoError == TipoErrorBD.POOL_AGOTADO ||
               tipoError == TipoErrorBD.CONEXION_FALLIDA;
    }

    /**
     * Verifica si el error es debido a violación de restricciones
     */
    public boolean esViolacionRestriccion() {
        return tipoError == TipoErrorBD.DATOS_DUPLICADOS ||
               tipoError == TipoErrorBD.INTEGRIDAD_REFERENCIAL;
    }

    /**
     * Determina el tipo de error basado en la causa
     */
    private static TipoErrorBD determinarTipoError(Throwable causa) {
        if (causa instanceof SQLException) {
            SQLException sqlEx = (SQLException) causa;
            int codigo = sqlEx.getErrorCode();
            String estado = sqlEx.getSQLState();

            // Códigos comunes de MySQL
            switch (codigo) {
                case 1062: // Duplicate entry
                    return TipoErrorBD.DATOS_DUPLICADOS;
                case 1452: // Foreign key constraint fails
                    return TipoErrorBD.INTEGRIDAD_REFERENCIAL;
                case 1045: // Access denied
                    return TipoErrorBD.PERMISOS_INSUFICIENTES;
                case 2002: // Can't connect
                case 2003:
                    return TipoErrorBD.CONEXION_FALLIDA;
                case 1049: // Unknown database
                    return TipoErrorBD.BASE_DATOS_NO_DISPONIBLE;
                default:
                    if (estado != null) {
                        if (estado.startsWith("08")) { // Connection exception
                            return TipoErrorBD.CONEXION_FALLIDA;
                        } else if (estado.startsWith("23")) { // Integrity constraint violation
                            return TipoErrorBD.INTEGRIDAD_REFERENCIAL;
                        } else if (estado.startsWith("42")) { // Syntax error or access rule violation
                            return TipoErrorBD.CONSULTA_FALLIDA;
                        }
                    }
                    return TipoErrorBD.ERROR_INTERNO_BD;
            }
        }

        return TipoErrorBD.ERROR_INTERNO_BD;
    }

    /**
     * Extrae el código de error SQL de la causa
     */
    private static int extraerCodigoError(Throwable causa) {
        if (causa instanceof SQLException) {
            return ((SQLException) causa).getErrorCode();
        }
        return -1;
    }

    /**
     * Obtiene sugerencias para resolver el error
     */
    public String getSugerenciasResolucion() {
        switch (tipoError) {
            case CONEXION_FALLIDA:
                return "Verificar que el servidor de base de datos esté ejecutándose y " +
                       "que la configuración de conexión sea correcta.";

            case DATOS_DUPLICADOS:
                return "El registro que intenta insertar ya existe. " +
                       "Verificar campos únicos como usuario o correo electrónico.";

            case INTEGRIDAD_REFERENCIAL:
                return "Violación de clave foránea. Verificar que las referencias " +
                       "a otras tablas sean válidas.";

            case TIMEOUT:
                return "La operación tardó demasiado tiempo. Considerar optimizar " +
                       "la consulta o aumentar el timeout.";

            case POOL_AGOTADO:
                return "No hay conexiones disponibles en el pool. " +
                       "Verificar que las conexiones se estén liberando correctamente.";

            case PERMISOS_INSUFICIENTES:
                return "El usuario de base de datos no tiene permisos suficientes " +
                       "para realizar esta operación.";

            case BASE_DATOS_NO_DISPONIBLE:
                return "La base de datos especificada no existe o no está disponible.";

            default:
                return "Revisar los logs del sistema para más detalles.";
        }
    }

    @Override
    public String toString() {
        return "ExcepcionBaseDatos{" +
                "tipoError=" + tipoError +
                ", mensaje='" + getMessage() + '\'' +
                ", consultaSQL='" + consultaSQL + '\'' +
                ", codigoErrorSQL=" + codigoErrorSQL +
                '}';
    }
}