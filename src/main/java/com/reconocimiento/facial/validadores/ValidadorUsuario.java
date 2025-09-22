package com.reconocimiento.facial.validadores;

import com.reconocimiento.facial.dto.UsuarioDTO;
import com.reconocimiento.facial.excepciones.ExcepcionUsuario;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Validador para datos de usuario
 * Contiene todas las reglas de validación para usuarios
 */
public class ValidadorUsuario {

    // Patrones de validación
    private static final Pattern PATRON_EMAIL = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    private static final Pattern PATRON_NOMBRE_USUARIO = Pattern.compile(
        "^[a-zA-Z0-9_]{3,50}$"
    );

    private static final Pattern PATRON_NOMBRE_COMPLETO = Pattern.compile(
        "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]{2,100}$"
    );

    // Configuraciones
    private static final int MIN_LONGITUD_CONTRASENA = 8;
    private static final int MAX_LONGITUD_CONTRASENA = 128;
    private static final int MIN_LONGITUD_NOMBRE_USUARIO = 3;
    private static final int MAX_LONGITUD_NOMBRE_USUARIO = 50;
    private static final int MIN_LONGITUD_NOMBRE_COMPLETO = 2;
    private static final int MAX_LONGITUD_NOMBRE_COMPLETO = 100;

    /**
     * Valida datos completos para registro de usuario
     */
    public void validarDatosRegistro(UsuarioDTO usuario) throws ExcepcionUsuario {
        List<String> errores = new ArrayList<>();

        // Validar que el objeto no sea nulo
        if (usuario == null) {
            throw new ExcepcionUsuario(ExcepcionUsuario.TipoError.DATOS_INVALIDOS, "Los datos del usuario son requeridos");
        }

        // Validar nombre de usuario
        try {
            validarNombreUsuario(usuario.getNombreUsuario());
        } catch (ExcepcionUsuario e) {
            errores.add(e.getMessage());
        }

        // Validar correo electrónico
        try {
            validarCorreoElectronico(usuario.getCorreoElectronico());
        } catch (ExcepcionUsuario e) {
            errores.add(e.getMessage());
        }

        // Validar nombre completo
        try {
            validarNombreCompleto(usuario.getNombreCompleto());
        } catch (ExcepcionUsuario e) {
            errores.add(e.getMessage());
        }

        // Validar contraseña
        try {
            validarContrasena(usuario.getContrasena());
        } catch (ExcepcionUsuario e) {
            errores.add(e.getMessage());
        }

        // Si hay errores, lanzar excepción con todos los errores
        if (!errores.isEmpty()) {
            throw new ExcepcionUsuario(ExcepcionUsuario.TipoError.DATOS_INVALIDOS, String.join("; ", errores));
        }
    }

    /**
     * Valida datos para actualización de usuario
     */
    public void validarDatosActualizacion(UsuarioDTO usuario) throws ExcepcionUsuario {
        List<String> errores = new ArrayList<>();

        if (usuario == null) {
            throw new ExcepcionUsuario(ExcepcionUsuario.TipoError.DATOS_INVALIDOS, "Los datos del usuario son requeridos");
        }

        // ID usuario es requerido para actualización
        if (usuario.getIdUsuario() <= 0) {
            errores.add("ID de usuario es requerido para actualización");
        }

        // Validar correo electrónico
        try {
            validarCorreoElectronico(usuario.getCorreoElectronico());
        } catch (ExcepcionUsuario e) {
            errores.add(e.getMessage());
        }

        // Validar nombre completo
        try {
            validarNombreCompleto(usuario.getNombreCompleto());
        } catch (ExcepcionUsuario e) {
            errores.add(e.getMessage());
        }

        // Validar contraseña solo si se proporciona (opcional en actualización)
        if (StringUtils.isNotBlank(usuario.getContrasena())) {
            try {
                validarContrasena(usuario.getContrasena());
            } catch (ExcepcionUsuario e) {
                errores.add(e.getMessage());
            }
        }

        if (!errores.isEmpty()) {
            throw new ExcepcionUsuario(ExcepcionUsuario.TipoError.DATOS_INVALIDOS, String.join("; ", errores));
        }
    }

    /**
     * Valida nombre de usuario
     */
    public void validarNombreUsuario(String nombreUsuario) throws ExcepcionUsuario {
        if (StringUtils.isBlank(nombreUsuario)) {
            throw new ExcepcionUsuario(ExcepcionUsuario.TipoError.NOMBRE_USUARIO_INVALIDO, "El nombre de usuario es requerido");
        }

        if (nombreUsuario.length() < MIN_LONGITUD_NOMBRE_USUARIO) {
            throw new ExcepcionUsuario(ExcepcionUsuario.TipoError.NOMBRE_USUARIO_INVALIDO,
                "El nombre de usuario debe tener al menos " + MIN_LONGITUD_NOMBRE_USUARIO + " caracteres");
        }

        if (nombreUsuario.length() > MAX_LONGITUD_NOMBRE_USUARIO) {
            throw new ExcepcionUsuario(ExcepcionUsuario.TipoError.NOMBRE_USUARIO_INVALIDO,
                "El nombre de usuario no puede tener más de " + MAX_LONGITUD_NOMBRE_USUARIO + " caracteres");
        }

        if (!PATRON_NOMBRE_USUARIO.matcher(nombreUsuario).matches()) {
            throw new ExcepcionUsuario(ExcepcionUsuario.TipoError.NOMBRE_USUARIO_INVALIDO,
                "El nombre de usuario solo puede contener letras, números y guiones bajos");
        }

        // Verificar que no sea solo números
        if (nombreUsuario.matches("^[0-9]+$")) {
            throw new ExcepcionUsuario(ExcepcionUsuario.TipoError.NOMBRE_USUARIO_INVALIDO,
                "El nombre de usuario no puede ser solo números");
        }

        // Verificar palabras reservadas
        if (esNombreUsuarioReservado(nombreUsuario)) {
            throw new ExcepcionUsuario(ExcepcionUsuario.TipoError.NOMBRE_USUARIO_INVALIDO,
                "El nombre de usuario está reservado");
        }
    }

    /**
     * Valida correo electrónico
     */
    public void validarCorreoElectronico(String correo) throws ExcepcionUsuario {
        if (StringUtils.isBlank(correo)) {
            throw new ExcepcionUsuario(ExcepcionUsuario.TipoError.FORMATO_CORREO_INVALIDO, "El correo electrónico es requerido");
        }

        if (!PATRON_EMAIL.matcher(correo).matches()) {
            throw new ExcepcionUsuario(ExcepcionUsuario.TipoError.FORMATO_CORREO_INVALIDO,
                "El formato del correo electrónico no es válido");
        }

        if (correo.length() > 100) {
            throw new ExcepcionUsuario(ExcepcionUsuario.TipoError.FORMATO_CORREO_INVALIDO,
                "El correo electrónico no puede tener más de 100 caracteres");
        }

        // Verificar dominios temporales conocidos
        if (esDominioTemporal(correo)) {
            throw new ExcepcionUsuario(ExcepcionUsuario.TipoError.FORMATO_CORREO_INVALIDO,
                "No se permiten correos electrónicos temporales");
        }
    }

    /**
     * Valida nombre completo
     */
    public void validarNombreCompleto(String nombreCompleto) throws ExcepcionUsuario {
        if (StringUtils.isBlank(nombreCompleto)) {
            throw new ExcepcionUsuario(ExcepcionUsuario.TipoError.DATOS_INVALIDOS, "El nombre completo es requerido");
        }

        if (nombreCompleto.length() < MIN_LONGITUD_NOMBRE_COMPLETO) {
            throw new ExcepcionUsuario(ExcepcionUsuario.TipoError.DATOS_INVALIDOS,
                "El nombre completo debe tener al menos " + MIN_LONGITUD_NOMBRE_COMPLETO + " caracteres");
        }

        if (nombreCompleto.length() > MAX_LONGITUD_NOMBRE_COMPLETO) {
            throw new ExcepcionUsuario(ExcepcionUsuario.TipoError.DATOS_INVALIDOS,
                "El nombre completo no puede tener más de " + MAX_LONGITUD_NOMBRE_COMPLETO + " caracteres");
        }

        if (!PATRON_NOMBRE_COMPLETO.matcher(nombreCompleto).matches()) {
            throw new ExcepcionUsuario(ExcepcionUsuario.TipoError.DATOS_INVALIDOS,
                "El nombre completo solo puede contener letras y espacios");
        }

        // Verificar que no sea solo espacios
        if (nombreCompleto.trim().isEmpty()) {
            throw new ExcepcionUsuario(ExcepcionUsuario.TipoError.DATOS_INVALIDOS,
                "El nombre completo no puede estar vacío");
        }

        // Verificar que tenga al menos un nombre y un apellido
        String[] partes = nombreCompleto.trim().split("\\s+");
        if (partes.length < 2) {
            throw new ExcepcionUsuario(ExcepcionUsuario.TipoError.DATOS_INVALIDOS,
                "Debe proporcionar al menos nombre y apellido");
        }
    }

    /**
     * Valida contraseña
     */
    public void validarContrasena(String contrasena) throws ExcepcionUsuario {
        if (StringUtils.isBlank(contrasena)) {
            throw new ExcepcionUsuario(ExcepcionUsuario.TipoError.CONTRASENA_DEBIL, "La contraseña es requerida");
        }

        if (contrasena.length() < MIN_LONGITUD_CONTRASENA) {
            throw new ExcepcionUsuario(ExcepcionUsuario.TipoError.CONTRASENA_DEBIL,
                "La contraseña debe tener al menos " + MIN_LONGITUD_CONTRASENA + " caracteres");
        }

        if (contrasena.length() > MAX_LONGITUD_CONTRASENA) {
            throw new ExcepcionUsuario(ExcepcionUsuario.TipoError.CONTRASENA_DEBIL,
                "La contraseña no puede tener más de " + MAX_LONGITUD_CONTRASENA + " caracteres");
        }

        List<String> errores = new ArrayList<>();

        // Verificar mayúsculas
        if (!contrasena.matches(".*[A-Z].*")) {
            errores.add("al menos una letra mayúscula");
        }

        // Verificar minúsculas
        if (!contrasena.matches(".*[a-z].*")) {
            errores.add("al menos una letra minúscula");
        }

        // Verificar números
        if (!contrasena.matches(".*[0-9].*")) {
            errores.add("al menos un número");
        }

        // Verificar caracteres especiales (opcional pero recomendado)
        if (!contrasena.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) {
            errores.add("al menos un carácter especial (recomendado)");
        }

        if (!errores.isEmpty() && errores.size() > 1) { // Solo requerir si faltan múltiples elementos
            throw new ExcepcionUsuario(ExcepcionUsuario.TipoError.CONTRASENA_DEBIL,
                "La contraseña debe contener: " + String.join(", ", errores));
        }

        // Verificar patrones débiles
        if (esContrasenaDebil(contrasena)) {
            throw new ExcepcionUsuario(ExcepcionUsuario.TipoError.CONTRASENA_DEBIL,
                "La contraseña es demasiado común o predecible");
        }
    }

    /**
     * Verifica si el nombre de usuario está reservado
     */
    private boolean esNombreUsuarioReservado(String nombreUsuario) {
        String[] reservados = {
            "admin", "administrator", "root", "system", "user", "guest",
            "test", "demo", "null", "undefined", "api", "www", "mail",
            "ftp", "ssh", "support", "help", "info", "contact", "service"
        };

        String nombreLower = nombreUsuario.toLowerCase();
        for (String reservado : reservados) {
            if (nombreLower.equals(reservado) || nombreLower.startsWith(reservado)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Verifica si el dominio de correo es temporal
     */
    private boolean esDominioTemporal(String correo) {
        String[] dominiosTemporales = {
            "10minutemail.com", "tempmail.org", "guerrillamail.com",
            "mailinator.com", "temp-mail.org", "throwaway.email"
        };

        String dominio = correo.substring(correo.indexOf("@") + 1).toLowerCase();
        for (String temporal : dominiosTemporales) {
            if (dominio.equals(temporal)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Verifica si la contraseña es débil o común
     */
    private boolean esContrasenaDebil(String contrasena) {
        String contrasenaLower = contrasena.toLowerCase();

        // Contraseñas comunes
        String[] contrasenasComunes = {
            "password", "123456", "12345678", "qwerty", "abc123",
            "password123", "admin", "letmein", "welcome", "monkey",
            "dragon", "master", "shadow", "superman", "michael"
        };

        for (String comun : contrasenasComunes) {
            if (contrasenaLower.contains(comun)) {
                return true;
            }
        }

        // Verificar secuencias
        if (tieneSecuenciasConsecutivas(contrasena)) {
            return true;
        }

        // Verificar repetición excesiva
        if (tieneRepeticionExcesiva(contrasena)) {
            return true;
        }

        return false;
    }

    /**
     * Verifica secuencias consecutivas en la contraseña
     */
    private boolean tieneSecuenciasConsecutivas(String contrasena) {
        String secuenciasComunes = "0123456789abcdefghijklmnopqrstuvwxyzqwertyuiopasdfghjklzxcvbnm";

        for (int i = 0; i <= secuenciasComunes.length() - 4; i++) {
            String secuencia = secuenciasComunes.substring(i, i + 4);
            if (contrasena.toLowerCase().contains(secuencia)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Verifica repetición excesiva de caracteres
     */
    private boolean tieneRepeticionExcesiva(String contrasena) {
        int repeticionMaxima = 2;
        char caracterAnterior = 0;
        int contadorRepeticion = 1;

        for (char caracter : contrasena.toCharArray()) {
            if (caracter == caracterAnterior) {
                contadorRepeticion++;
                if (contadorRepeticion > repeticionMaxima) {
                    return true;
                }
            } else {
                contadorRepeticion = 1;
            }
            caracterAnterior = caracter;
        }

        return false;
    }

    /**
     * Calcula el puntaje de fortaleza de una contraseña
     */
    public int calcularFortalezaContrasena(String contrasena) {
        if (StringUtils.isBlank(contrasena)) {
            return 0;
        }

        int puntaje = 0;

        // Longitud
        puntaje += Math.min(contrasena.length() * 4, 40);

        // Mayúsculas
        if (contrasena.matches(".*[A-Z].*")) puntaje += 15;

        // Minúsculas
        if (contrasena.matches(".*[a-z].*")) puntaje += 15;

        // Números
        if (contrasena.matches(".*[0-9].*")) puntaje += 15;

        // Caracteres especiales
        if (contrasena.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) puntaje += 15;

        // Penalizaciones
        if (esContrasenaDebil(contrasena)) puntaje -= 30;
        if (tieneSecuenciasConsecutivas(contrasena)) puntaje -= 20;
        if (tieneRepeticionExcesiva(contrasena)) puntaje -= 15;

        return Math.max(0, Math.min(100, puntaje));
    }

    /**
     * Obtiene descripción de la fortaleza de contraseña
     */
    public String obtenerDescripcionFortaleza(int puntaje) {
        if (puntaje < 30) return "Muy débil";
        if (puntaje < 50) return "Débil";
        if (puntaje < 70) return "Regular";
        if (puntaje < 85) return "Fuerte";
        return "Muy fuerte";
    }
}