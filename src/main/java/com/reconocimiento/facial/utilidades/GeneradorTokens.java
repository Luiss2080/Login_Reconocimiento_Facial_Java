package com.reconocimiento.facial.utilidades;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.UUID;

/**
 * Generador de tokens seguros para sesiones y operaciones del sistema
 */
public class GeneradorTokens {

    private static final Logger logger = LoggerFactory.getLogger(GeneradorTokens.class);

    private final SecureRandom secureRandom;

    // Configuraciones
    private static final int LONGITUD_TOKEN_SESION = 64;
    private static final int LONGITUD_TOKEN_TEMPORAL = 32;
    private static final String CARACTERES_TOKEN = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public GeneradorTokens() {
        this.secureRandom = new SecureRandom();
    }

    /**
     * Genera un token de sesión único para un usuario
     */
    public String generarTokenSesion(int idUsuario) {
        try {
            // Combinar múltiples fuentes de entropía
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
            String randomPart = generarCadenaAleatoria(32);
            String userPart = String.valueOf(idUsuario);
            String uuid = UUID.randomUUID().toString().replace("-", "");

            // Crear cadena base
            String cadenaBase = timestamp + randomPart + userPart + uuid;

            // Aplicar hash SHA-256
            String hash = DigestUtils.sha256Hex(cadenaBase);

            // Combinar hash con parte aleatoria adicional y codificar en Base64
            String tokenFinal = hash + generarCadenaAleatoria(16);
            String tokenBase64 = Base64.getEncoder().encodeToString(tokenFinal.getBytes());

            // Limpiar caracteres problemáticos y limitar longitud
            String token = tokenBase64.replaceAll("[+/=]", "").substring(0, Math.min(LONGITUD_TOKEN_SESION, tokenBase64.length()));

            logger.debug("Token de sesión generado para usuario: {}", idUsuario);
            return token;

        } catch (Exception e) {
            logger.error("Error generando token de sesión: {}", e.getMessage());
            throw new RuntimeException("Error al generar token de sesión", e);
        }
    }

    /**
     * Genera un token temporal para operaciones específicas
     */
    public String generarTokenTemporal() {
        try {
            String timestamp = String.valueOf(System.currentTimeMillis());
            String randomPart = generarCadenaAleatoria(24);
            String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 16);

            String cadenaBase = timestamp + randomPart + uuid;
            String hash = DigestUtils.sha256Hex(cadenaBase);

            return hash.substring(0, LONGITUD_TOKEN_TEMPORAL);

        } catch (Exception e) {
            logger.error("Error generando token temporal: {}", e.getMessage());
            throw new RuntimeException("Error al generar token temporal", e);
        }
    }

    /**
     * Genera un código PIN numérico
     */
    public String generarCodigoPIN(int longitud) {
        if (longitud < 4 || longitud > 12) {
            throw new IllegalArgumentException("La longitud del PIN debe estar entre 4 y 12 dígitos");
        }

        StringBuilder pin = new StringBuilder();
        for (int i = 0; i < longitud; i++) {
            pin.append(secureRandom.nextInt(10));
        }

        return pin.toString();
    }

    /**
     * Genera un token para recuperación de contraseña
     */
    public String generarTokenRecuperacion(String correoElectronico) {
        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            String emailHash = DigestUtils.sha256Hex(correoElectronico.toLowerCase());
            String randomPart = generarCadenaAleatoria(20);

            String cadenaBase = timestamp + emailHash + randomPart;
            String hash = DigestUtils.sha256Hex(cadenaBase);

            // Usar solo los primeros 48 caracteres para mayor legibilidad
            return hash.substring(0, 48);

        } catch (Exception e) {
            logger.error("Error generando token de recuperación: {}", e.getMessage());
            throw new RuntimeException("Error al generar token de recuperación", e);
        }
    }

    /**
     * Genera un identificador único para archivos
     */
    public String generarIdArchivo(String extension) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        String random = generarCadenaAleatoria(4);

        String nombreBase = timestamp + "_" + uuid + "_" + random;

        if (extension != null && !extension.isEmpty()) {
            if (!extension.startsWith(".")) {
                extension = "." + extension;
            }
            return nombreBase + extension;
        }

        return nombreBase;
    }

    /**
     * Genera un hash de verificación para integridad de datos
     */
    public String generarHashVerificacion(String datos) {
        if (datos == null || datos.isEmpty()) {
            throw new IllegalArgumentException("Los datos no pueden ser nulos o vacíos");
        }

        try {
            // Agregar salt aleatorio
            String salt = generarCadenaAleatoria(16);
            String datosConSalt = datos + salt + System.currentTimeMillis();

            String hash = DigestUtils.sha256Hex(datosConSalt);
            return salt + hash; // Incluir salt para verificación posterior

        } catch (Exception e) {
            logger.error("Error generando hash de verificación: {}", e.getMessage());
            throw new RuntimeException("Error al generar hash de verificación", e);
        }
    }

    /**
     * Verifica un hash de integridad
     */
    public boolean verificarHashIntegridad(String datos, String hashCompleto) {
        if (datos == null || hashCompleto == null || hashCompleto.length() < 16) {
            return false;
        }

        try {
            // Extraer salt (primeros 16 caracteres)
            String salt = hashCompleto.substring(0, 16);
            String hashOriginal = hashCompleto.substring(16);

            // Recalcular hash con diferentes timestamps (ventana de tolerancia)
            long timestampActual = System.currentTimeMillis();
            for (long i = 0; i < 10000; i += 1000) { // Ventana de 10 segundos
                String datosConSalt = datos + salt + (timestampActual - i);
                String hashCalculado = DigestUtils.sha256Hex(datosConSalt);

                if (hashCalculado.equals(hashOriginal)) {
                    return true;
                }
            }

            return false;

        } catch (Exception e) {
            logger.error("Error verificando hash de integridad: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Genera un token de API para servicios externos
     */
    public String generarTokenAPI(String nombreServicio, String version) {
        try {
            String prefijo = "API";
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String servicioHash = DigestUtils.sha256Hex(nombreServicio + version).substring(0, 8);
            String randomPart = generarCadenaAleatoria(16);

            return prefijo + "_" + timestamp + "_" + servicioHash + "_" + randomPart;

        } catch (Exception e) {
            logger.error("Error generando token de API: {}", e.getMessage());
            throw new RuntimeException("Error al generar token de API", e);
        }
    }

    /**
     * Valida el formato de un token
     */
    public boolean esTokenValido(String token, TipoToken tipo) {
        if (token == null || token.isEmpty()) {
            return false;
        }

        switch (tipo) {
            case SESION:
                return token.length() == LONGITUD_TOKEN_SESION &&
                       token.matches("[A-Za-z0-9]+");

            case TEMPORAL:
                return token.length() == LONGITUD_TOKEN_TEMPORAL &&
                       token.matches("[a-f0-9]+");

            case RECUPERACION:
                return token.length() == 48 &&
                       token.matches("[a-f0-9]+");

            case PIN:
                return token.matches("[0-9]{4,12}");

            case API:
                return token.startsWith("API_") &&
                       token.length() > 20;

            default:
                return false;
        }
    }

    /**
     * Genera una cadena aleatoria de la longitud especificada
     */
    private String generarCadenaAleatoria(int longitud) {
        StringBuilder resultado = new StringBuilder();

        for (int i = 0; i < longitud; i++) {
            int indice = secureRandom.nextInt(CARACTERES_TOKEN.length());
            resultado.append(CARACTERES_TOKEN.charAt(indice));
        }

        return resultado.toString();
    }

    /**
     * Genera un número aleatorio en un rango específico
     */
    public int generarNumeroAleatorio(int minimo, int maximo) {
        if (minimo >= maximo) {
            throw new IllegalArgumentException("El valor mínimo debe ser menor que el máximo");
        }

        return secureRandom.nextInt(maximo - minimo + 1) + minimo;
    }

    /**
     * Genera bytes aleatorios
     */
    public byte[] generarBytesAleatorios(int cantidad) {
        byte[] bytes = new byte[cantidad];
        secureRandom.nextBytes(bytes);
        return bytes;
    }

    /**
     * Enumeración para tipos de token
     */
    public enum TipoToken {
        SESION,
        TEMPORAL,
        RECUPERACION,
        PIN,
        API
    }

    /**
     * Información sobre un token generado
     */
    public static class InfoToken {
        private final String token;
        private final TipoToken tipo;
        private final LocalDateTime fechaCreacion;
        private final LocalDateTime fechaExpiracion;

        public InfoToken(String token, TipoToken tipo, LocalDateTime fechaExpiracion) {
            this.token = token;
            this.tipo = tipo;
            this.fechaCreacion = LocalDateTime.now();
            this.fechaExpiracion = fechaExpiracion;
        }

        public String getToken() { return token; }
        public TipoToken getTipo() { return tipo; }
        public LocalDateTime getFechaCreacion() { return fechaCreacion; }
        public LocalDateTime getFechaExpiracion() { return fechaExpiracion; }

        public boolean estaExpirado() {
            return fechaExpiracion != null && LocalDateTime.now().isAfter(fechaExpiracion);
        }

        @Override
        public String toString() {
            return "InfoToken{" +
                    "tipo=" + tipo +
                    ", fechaCreacion=" + fechaCreacion +
                    ", fechaExpiracion=" + fechaExpiracion +
                    ", expirado=" + estaExpirado() +
                    '}';
        }
    }
}