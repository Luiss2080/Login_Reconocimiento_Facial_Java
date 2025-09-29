package com.reconocimiento.facial.seguridad;

import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.regex.Pattern;

/**
 * Clase para cifrado y verificación de contraseñas
 * Utiliza BCrypt para hash de contraseñas y AES para cifrado adicional
 */
public class CifradorContrasenas {

    private static final Logger logger = LoggerFactory.getLogger(CifradorContrasenas.class);

    // Configuración de BCrypt
    private static final int ROUNDS_BCRYPT = 12; // Número de rondas para BCrypt (más alto = más seguro pero más lento)

    // Configuración de AES
    private static final String ALGORITMO_AES = "AES";
    private static final String TRANSFORMACION_AES = "AES/ECB/PKCS5Padding";

    // Generador de números aleatorios seguros
    private final SecureRandom secureRandom;

    // Patrones para validación de contraseñas
    private static final Pattern PATRON_MAYUSCULA = Pattern.compile("[A-Z]");
    private static final Pattern PATRON_MINUSCULA = Pattern.compile("[a-z]");
    private static final Pattern PATRON_NUMERO = Pattern.compile("[0-9]");
    private static final Pattern PATRON_CARACTER_ESPECIAL = Pattern.compile("[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]");

    // Configuración de política de contraseñas
    public static class PoliticaContrasena {
        public static final int LONGITUD_MINIMA = 8;
        public static final int LONGITUD_MAXIMA = 128;
        public static final boolean REQUIERE_MAYUSCULA = true;
        public static final boolean REQUIERE_MINUSCULA = true;
        public static final boolean REQUIERE_NUMERO = true;
        public static final boolean REQUIERE_CARACTER_ESPECIAL = false;
        public static final int MAXIMO_CARACTERES_REPETIDOS = 3;
    }

    public CifradorContrasenas() {
        this.secureRandom = new SecureRandom();
    }

    /**
     * Cifra una contraseña usando BCrypt
     */
    public String cifrarContrasena(String contrasenaTextoPlano) {
        if (contrasenaTextoPlano == null || contrasenaTextoPlano.isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede ser nula o vacía");
        }

        try {
            // Validar que la contraseña cumple con la política
            ResultadoValidacion validacion = validarContrasena(contrasenaTextoPlano);
            if (!validacion.esValida()) {
                throw new IllegalArgumentException("Contraseña no cumple con la política: " + validacion.getMensajesError());
            }

            // Generar salt y hash con BCrypt
            String salt = BCrypt.gensalt(ROUNDS_BCRYPT, secureRandom);
            String hashContrasena = BCrypt.hashpw(contrasenaTextoPlano, salt);

            logger.debug("Contraseña cifrada exitosamente");
            return hashContrasena;

        } catch (Exception e) {
            logger.error("Error cifrando contraseña: {}", e.getMessage());
            throw new RuntimeException("Error al cifrar contraseña", e);
        }
    }

    /**
     * Verifica si una contraseña coincide con su hash
     */
    public boolean verificarContrasena(String contrasenaTextoPlano, String hashAlmacenado) {
        if (contrasenaTextoPlano == null || hashAlmacenado == null) {
            return false;
        }

        try {
            boolean coincide = BCrypt.checkpw(contrasenaTextoPlano, hashAlmacenado);
            logger.debug("Verificación de contraseña: {}", coincide ? "exitosa" : "fallida");
            return coincide;

        } catch (Exception e) {
            logger.error("Error verificando contraseña: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Valida que una contraseña cumple con las políticas de seguridad
     */
    public ResultadoValidacion validarContrasena(String contrasena) {
        ResultadoValidacion resultado = new ResultadoValidacion();

        if (contrasena == null) {
            resultado.agregarError("La contraseña no puede ser nula");
            return resultado;
        }

        // Validar longitud
        if (contrasena.length() < PoliticaContrasena.LONGITUD_MINIMA) {
            resultado.agregarError("La contraseña debe tener al menos " + PoliticaContrasena.LONGITUD_MINIMA + " caracteres");
        }

        if (contrasena.length() > PoliticaContrasena.LONGITUD_MAXIMA) {
            resultado.agregarError("La contraseña no puede tener más de " + PoliticaContrasena.LONGITUD_MAXIMA + " caracteres");
        }

        // Validar mayúsculas
        if (PoliticaContrasena.REQUIERE_MAYUSCULA && !PATRON_MAYUSCULA.matcher(contrasena).find()) {
            resultado.agregarError("La contraseña debe contener al menos una letra mayúscula");
        }

        // Validar minúsculas
        if (PoliticaContrasena.REQUIERE_MINUSCULA && !PATRON_MINUSCULA.matcher(contrasena).find()) {
            resultado.agregarError("La contraseña debe contener al menos una letra minúscula");
        }

        // Validar números
        if (PoliticaContrasena.REQUIERE_NUMERO && !PATRON_NUMERO.matcher(contrasena).find()) {
            resultado.agregarError("La contraseña debe contener al menos un número");
        }

        // Validar caracteres repetidos
        if (tieneDemasiadosCaracteresRepetidos(contrasena)) {
            resultado.agregarError("La contraseña no puede tener más de " + PoliticaContrasena.MAXIMO_CARACTERES_REPETIDOS + " caracteres iguales consecutivos");
        }

        // Validar patrones comunes débiles
        if (esContrasenaComun(contrasena)) {
            resultado.agregarError("La contraseña es demasiado común y fácil de adivinar");
        }

        return resultado;
    }

    /**
     * Genera una contraseña aleatoria segura
     */
    public String generarContrasenaSegura(int longitud) {
        if (longitud < PoliticaContrasena.LONGITUD_MINIMA) {
            longitud = PoliticaContrasena.LONGITUD_MINIMA;
        }

        String mayusculas = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String minusculas = "abcdefghijklmnopqrstuvwxyz";
        String numeros = "0123456789";
        String especiales = "!@#$%^&*()_+-=[]{}|;:,.<>?";

        StringBuilder contrasena = new StringBuilder();
        StringBuilder caracteresDisponibles = new StringBuilder();

        // Asegurar que incluya al menos un carácter de cada tipo requerido
        if (PoliticaContrasena.REQUIERE_MAYUSCULA) {
            contrasena.append(mayusculas.charAt(secureRandom.nextInt(mayusculas.length())));
            caracteresDisponibles.append(mayusculas);
        }

        if (PoliticaContrasena.REQUIERE_MINUSCULA) {
            contrasena.append(minusculas.charAt(secureRandom.nextInt(minusculas.length())));
            caracteresDisponibles.append(minusculas);
        }

        if (PoliticaContrasena.REQUIERE_NUMERO) {
            contrasena.append(numeros.charAt(secureRandom.nextInt(numeros.length())));
            caracteresDisponibles.append(numeros);
        }

        if (PoliticaContrasena.REQUIERE_CARACTER_ESPECIAL) {
            contrasena.append(especiales.charAt(secureRandom.nextInt(especiales.length())));
            caracteresDisponibles.append(especiales);
        }

        // Completar el resto de la contraseña
        while (contrasena.length() < longitud) {
            char caracter = caracteresDisponibles.charAt(secureRandom.nextInt(caracteresDisponibles.length()));
            contrasena.append(caracter);
        }

        // Mezclar los caracteres para que no sean predecibles
        return mezclarCaracteres(contrasena.toString());
    }

    /**
     * Cifra texto usando AES (para datos adicionales sensibles)
     */
    public String cifrarTextoAES(String texto, String clave) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(clave.getBytes(), ALGORITMO_AES);
            Cipher cipher = Cipher.getInstance(TRANSFORMACION_AES);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            byte[] textoCifrado = cipher.doFinal(texto.getBytes());
            return Base64.getEncoder().encodeToString(textoCifrado);

        } catch (Exception e) {
            logger.error("Error cifrando texto con AES: {}", e.getMessage());
            throw new RuntimeException("Error al cifrar texto", e);
        }
    }

    /**
     * Descifra texto usando AES
     */
    public String descifrarTextoAES(String textoCifrado, String clave) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(clave.getBytes(), ALGORITMO_AES);
            Cipher cipher = Cipher.getInstance(TRANSFORMACION_AES);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);

            byte[] textoDescifrado = cipher.doFinal(Base64.getDecoder().decode(textoCifrado));
            return new String(textoDescifrado);

        } catch (Exception e) {
            logger.error("Error descifrando texto con AES: {}", e.getMessage());
            throw new RuntimeException("Error al descifrar texto", e);
        }
    }

    /**
     * Genera una clave AES aleatoria
     */
    public String generarClaveAES() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITMO_AES);
            keyGenerator.init(256, secureRandom);
            SecretKey secretKey = keyGenerator.generateKey();
            return Base64.getEncoder().encodeToString(secretKey.getEncoded());

        } catch (NoSuchAlgorithmException e) {
            logger.error("Error generando clave AES: {}", e.getMessage());
            throw new RuntimeException("Error al generar clave AES", e);
        }
    }

    /**
     * Calcula la fuerza de una contraseña (0-100)
     */
    public int calcularFuerzaContrasena(String contrasena) {
        if (contrasena == null || contrasena.isEmpty()) {
            return 0;
        }

        int puntuacion = 0;

        // Puntuación por longitud
        if (contrasena.length() >= 8) puntuacion += 20;
        if (contrasena.length() >= 12) puntuacion += 10;
        if (contrasena.length() >= 16) puntuacion += 10;

        // Puntuación por variedad de caracteres
        if (PATRON_MINUSCULA.matcher(contrasena).find()) puntuacion += 15;
        if (PATRON_MAYUSCULA.matcher(contrasena).find()) puntuacion += 15;
        if (PATRON_NUMERO.matcher(contrasena).find()) puntuacion += 15;
        if (PATRON_CARACTER_ESPECIAL.matcher(contrasena).find()) puntuacion += 15;

        // Penalización por patrones comunes
        if (esContrasenaComun(contrasena)) puntuacion -= 30;
        if (tieneDemasiadosCaracteresRepetidos(contrasena)) puntuacion -= 20;

        return Math.max(0, Math.min(100, puntuacion));
    }

    // Métodos auxiliares privados

    private boolean tieneDemasiadosCaracteresRepetidos(String contrasena) {
        int repetidos = 1;
        char caracterAnterior = 0;

        for (char caracter : contrasena.toCharArray()) {
            if (caracter == caracterAnterior) {
                repetidos++;
                if (repetidos > PoliticaContrasena.MAXIMO_CARACTERES_REPETIDOS) {
                    return true;
                }
            } else {
                repetidos = 1;
            }
            caracterAnterior = caracter;
        }

        return false;
    }

    private boolean esContrasenaComun(String contrasena) {
        String contrasenaLower = contrasena.toLowerCase();
        
        // Si la contraseña cumple con criterios de fortaleza, no la rechazamos por patrones simples
        if (contrasena.length() >= 8 && 
            contrasena.matches(".*[A-Z].*") && 
            contrasena.matches(".*[a-z].*") && 
            contrasena.matches(".*[0-9].*")) {
            
            // Solo rechazar si es EXACTAMENTE una contraseña común completa
            String[] contrasenasExactasComunes = {
                "password", "123456", "12345678", "qwerty", "abc123",
                "password123", "admin", "letmein", "welcome", "monkey",
                "dragon", "master", "shadow", "superman", "michael",
                "football", "baseball", "soccer", "charlie", "jordan"
            };
            
            for (String comun : contrasenasExactasComunes) {
                if (contrasenaLower.equals(comun)) {
                    return true;
                }
            }
            
            // Para contraseñas fuertes, solo rechazar secuencias muy obvias
            if (contrasenaLower.equals("123456789") || 
                contrasenaLower.equals("abcdefgh") ||
                contrasenaLower.matches("^123+$") ||
                contrasenaLower.matches("^abc+$")) {
                return true;
            }
            
            return false;
        }
        
        // Para contraseñas débiles, usar validación estricta original
        String[] contrasenasComunes = {
            "password", "123456", "12345678", "qwerty", "abc123",
            "password123", "admin", "letmein", "welcome", "monkey",
            "dragon", "master", "shadow", "superman", "michael",
            "football", "baseball", "soccer", "charlie", "jordan"
        };

        for (String comun : contrasenasComunes) {
            if (contrasenaLower.contains(comun)) {
                return true;
            }
        }

        // Verificar secuencias numéricas solo para contraseñas débiles
        if (contrasenaLower.matches(".*123.*") || contrasenaLower.matches(".*abc.*")) {
            return true;
        }

        return false;
    }

    private String mezclarCaracteres(String texto) {
        char[] caracteres = texto.toCharArray();

        // Algoritmo Fisher-Yates para mezclar
        for (int i = caracteres.length - 1; i > 0; i--) {
            int j = secureRandom.nextInt(i + 1);
            char temp = caracteres[i];
            caracteres[i] = caracteres[j];
            caracteres[j] = temp;
        }

        return new String(caracteres);
    }

    /**
     * Clase interna para resultados de validación
     */
    public static class ResultadoValidacion {
        private final java.util.List<String> errores;

        public ResultadoValidacion() {
            this.errores = new java.util.ArrayList<>();
        }

        public void agregarError(String error) {
            errores.add(error);
        }

        public boolean esValida() {
            return errores.isEmpty();
        }

        public java.util.List<String> getErrores() {
            return new java.util.ArrayList<>(errores);
        }

        public String getMensajesError() {
            return String.join("; ", errores);
        }

        @Override
        public String toString() {
            return "ResultadoValidacion{" +
                    "valida=" + esValida() +
                    ", errores=" + errores +
                    '}';
        }
    }
}