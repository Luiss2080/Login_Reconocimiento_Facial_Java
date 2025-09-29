package com.reconocimiento.facial.configuracion;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 🔧 CONFIGURACIÓN DEL SISTEMA
 * Maneja todas las configuraciones centralizadas del sistema
 * de autenticación y reconocimiento facial
 */
public class ConfiguracionSistema {
    
    private static ConfiguracionSistema instancia;
    private Properties propiedades;
    
    // ========== CONFIGURACIONES POR DEFECTO ==========
    private static final String ARCHIVO_CONFIG = "config/sistema.properties";
    
    // Configuraciones de seguridad por defecto
    private static final int MAX_INTENTOS_FALLIDOS_DEFAULT = 5;
    private static final int TIEMPO_BLOQUEO_MINUTOS_DEFAULT = 30;
    private static final double CONFIANZA_MINIMA_FACIAL_DEFAULT = 0.85;
    private static final int LONGITUD_MINIMA_PASSWORD_DEFAULT = 6;
    
    // Configuraciones de red neuronal por defecto
    private static final int TAMAÑO_IMAGEN_DEFAULT = 64;
    private static final int MUESTRAS_MINIMAS_REGISTRO_DEFAULT = 3;
    private static final int MUESTRAS_MAXIMAS_REGISTRO_DEFAULT = 10;
    
    // Configuraciones de base de datos por defecto
    private static final String BD_HOST_DEFAULT = "localhost";
    private static final String BD_PUERTO_DEFAULT = "3307";
    private static final String BD_NOMBRE_DEFAULT = "sistema_reconocimiento_facial";
    private static final String BD_USUARIO_DEFAULT = "root";
    private static final String BD_PASSWORD_DEFAULT = "";
    
    /**
     * Constructor privado para patrón Singleton
     */
    private ConfiguracionSistema() {
        cargarConfiguracion();
    }
    
    /**
     * 🏗️ Obtener instancia única del configurador
     */
    public static synchronized ConfiguracionSistema obtenerInstancia() {
        if (instancia == null) {
            instancia = new ConfiguracionSistema();
        }
        return instancia;
    }
    
    /**
     * 📁 Cargar configuración desde archivo o usar valores por defecto
     */
    private void cargarConfiguracion() {
        propiedades = new Properties();
        
        try {
            // Intentar cargar desde archivo
            InputStream input = new FileInputStream(ARCHIVO_CONFIG);
            propiedades.load(input);
            input.close();
            
            System.out.println("✅ Configuración cargada desde: " + ARCHIVO_CONFIG);
            
        } catch (IOException e) {
            System.out.println("⚠️ No se pudo cargar archivo de configuración, usando valores por defecto");
            System.out.println("   Archivo esperado: " + ARCHIVO_CONFIG);
            
            // Usar configuraciones por defecto
            establecerConfiguracionesPorDefecto();
        }
        
        // Validar configuraciones
        validarConfiguraciones();
    }
    
    /**
     * 🛡️ Establecer configuraciones por defecto
     */
    private void establecerConfiguracionesPorDefecto() {
        // Seguridad
        propiedades.setProperty("seguridad.max_intentos_fallidos", String.valueOf(MAX_INTENTOS_FALLIDOS_DEFAULT));
        propiedades.setProperty("seguridad.tiempo_bloqueo_minutos", String.valueOf(TIEMPO_BLOQUEO_MINUTOS_DEFAULT));
        propiedades.setProperty("seguridad.confianza_minima_facial", String.valueOf(CONFIANZA_MINIMA_FACIAL_DEFAULT));
        propiedades.setProperty("seguridad.longitud_minima_password", String.valueOf(LONGITUD_MINIMA_PASSWORD_DEFAULT));
        
        // Red neuronal
        propiedades.setProperty("neural.tamaño_imagen", String.valueOf(TAMAÑO_IMAGEN_DEFAULT));
        propiedades.setProperty("neural.muestras_minimas_registro", String.valueOf(MUESTRAS_MINIMAS_REGISTRO_DEFAULT));
        propiedades.setProperty("neural.muestras_maximas_registro", String.valueOf(MUESTRAS_MAXIMAS_REGISTRO_DEFAULT));
        
        // Base de datos
        propiedades.setProperty("bd.host", BD_HOST_DEFAULT);
        propiedades.setProperty("bd.puerto", BD_PUERTO_DEFAULT);
        propiedades.setProperty("bd.nombre", BD_NOMBRE_DEFAULT);
        propiedades.setProperty("bd.usuario", BD_USUARIO_DEFAULT);
        propiedades.setProperty("bd.password", BD_PASSWORD_DEFAULT);
    }
    
    /**
     * ✅ Validar que las configuraciones sean válidas
     */
    private void validarConfiguraciones() {
        try {
            // Validar configuraciones de seguridad
            int maxIntentos = getMaxIntentosFallidos();
            if (maxIntentos < 1 || maxIntentos > 20) {
                System.err.println("⚠️ max_intentos_fallidos inválido, usando valor por defecto: " + MAX_INTENTOS_FALLIDOS_DEFAULT);
                propiedades.setProperty("seguridad.max_intentos_fallidos", String.valueOf(MAX_INTENTOS_FALLIDOS_DEFAULT));
            }
            
            double confianzaMinima = getConfianzaMinimaFacial();
            if (confianzaMinima < 0.0 || confianzaMinima > 1.0) {
                System.err.println("⚠️ confianza_minima_facial inválida, usando valor por defecto: " + CONFIANZA_MINIMA_FACIAL_DEFAULT);
                propiedades.setProperty("seguridad.confianza_minima_facial", String.valueOf(CONFIANZA_MINIMA_FACIAL_DEFAULT));
            }
            
            // Validar configuraciones de red neuronal
            int tamañoImagen = getTamañoImagen();
            if (tamañoImagen < 32 || tamañoImagen > 512) {
                System.err.println("⚠️ tamaño_imagen inválido, usando valor por defecto: " + TAMAÑO_IMAGEN_DEFAULT);
                propiedades.setProperty("neural.tamaño_imagen", String.valueOf(TAMAÑO_IMAGEN_DEFAULT));
            }
            
            System.out.println("✅ Configuraciones validadas correctamente");
            
        } catch (Exception e) {
            System.err.println("❌ Error validando configuraciones: " + e.getMessage());
        }
    }
    
    // ========== GETTERS PARA CONFIGURACIONES DE SEGURIDAD ==========
    
    public int getMaxIntentosFallidos() {
        return Integer.parseInt(propiedades.getProperty("seguridad.max_intentos_fallidos", String.valueOf(MAX_INTENTOS_FALLIDOS_DEFAULT)));
    }
    
    public int getTiempoBloqueoMinutos() {
        return Integer.parseInt(propiedades.getProperty("seguridad.tiempo_bloqueo_minutos", String.valueOf(TIEMPO_BLOQUEO_MINUTOS_DEFAULT)));
    }
    
    public double getConfianzaMinimaFacial() {
        return Double.parseDouble(propiedades.getProperty("seguridad.confianza_minima_facial", String.valueOf(CONFIANZA_MINIMA_FACIAL_DEFAULT)));
    }
    
    public int getLongitudMinimaPassword() {
        return Integer.parseInt(propiedades.getProperty("seguridad.longitud_minima_password", String.valueOf(LONGITUD_MINIMA_PASSWORD_DEFAULT)));
    }
    
    // ========== GETTERS PARA CONFIGURACIONES DE RED NEURONAL ==========
    
    public int getTamañoImagen() {
        return Integer.parseInt(propiedades.getProperty("neural.tamaño_imagen", String.valueOf(TAMAÑO_IMAGEN_DEFAULT)));
    }
    
    public int getMuestrasMinimas() {
        return Integer.parseInt(propiedades.getProperty("neural.muestras_minimas_registro", String.valueOf(MUESTRAS_MINIMAS_REGISTRO_DEFAULT)));
    }
    
    public int getMuestrasMaximas() {
        return Integer.parseInt(propiedades.getProperty("neural.muestras_maximas_registro", String.valueOf(MUESTRAS_MAXIMAS_REGISTRO_DEFAULT)));
    }
    
    // ========== GETTERS PARA CONFIGURACIONES DE BASE DE DATOS ==========
    
    public String getBDHost() {
        return propiedades.getProperty("bd.host", BD_HOST_DEFAULT);
    }
    
    public String getBDPuerto() {
        return propiedades.getProperty("bd.puerto", BD_PUERTO_DEFAULT);
    }
    
    public String getBDNombre() {
        return propiedades.getProperty("bd.nombre", BD_NOMBRE_DEFAULT);
    }
    
    public String getBDUsuario() {
        return propiedades.getProperty("bd.usuario", BD_USUARIO_DEFAULT);
    }
    
    public String getBDPassword() {
        return propiedades.getProperty("bd.password", BD_PASSWORD_DEFAULT);
    }
    
    public String getBDUrlCompleta() {
        return String.format("jdbc:mysql://%s:%s/%s?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC",
                getBDHost(), getBDPuerto(), getBDNombre());
    }
    
    // ========== MÉTODOS DE UTILIDAD ==========
    
    /**
     * 📊 Obtener resumen de configuraciones activas
     */
    public String obtenerResumenConfiguracion() {
        StringBuilder resumen = new StringBuilder();
        resumen.append("🔧 CONFIGURACIÓN DEL SISTEMA\n");
        resumen.append("============================\n\n");
        
        resumen.append("🛡️ SEGURIDAD:\n");
        resumen.append("• Máx. intentos fallidos: ").append(getMaxIntentosFallidos()).append("\n");
        resumen.append("• Tiempo bloqueo: ").append(getTiempoBloqueoMinutos()).append(" minutos\n");
        resumen.append("• Confianza mín. facial: ").append(String.format("%.2f%%", getConfianzaMinimaFacial() * 100)).append("\n");
        resumen.append("• Long. mín. password: ").append(getLongitudMinimaPassword()).append(" caracteres\n\n");
        
        resumen.append("🧠 RED NEURONAL:\n");
        resumen.append("• Tamaño imagen: ").append(getTamañoImagen()).append("x").append(getTamañoImagen()).append("\n");
        resumen.append("• Muestras mínimas: ").append(getMuestrasMinimas()).append("\n");
        resumen.append("• Muestras máximas: ").append(getMuestrasMaximas()).append("\n\n");
        
        resumen.append("🗄️ BASE DE DATOS:\n");
        resumen.append("• Host: ").append(getBDHost()).append(":").append(getBDPuerto()).append("\n");
        resumen.append("• Base de datos: ").append(getBDNombre()).append("\n");
        resumen.append("• Usuario: ").append(getBDUsuario()).append("\n");
        
        return resumen.toString();
    }
    
    /**
     * 🔄 Recargar configuración desde archivo
     */
    public void recargarConfiguracion() {
        System.out.println("🔄 Recargando configuración...");
        cargarConfiguracion();
        System.out.println("✅ Configuración recargada");
    }
    
    /**
     * 💾 Obtener propiedad personalizada
     */
    public String getPropiedad(String clave, String valorPorDefecto) {
        return propiedades.getProperty(clave, valorPorDefecto);
    }
    
    /**
     * 🧪 Ejecutar pruebas de configuración
     */
    public void ejecutarPruebas() {
        System.out.println("🧪 Ejecutando pruebas de configuración...");
        
        try {
            // Probar todas las configuraciones
            System.out.println("• Max intentos fallidos: " + getMaxIntentosFallidos());
            System.out.println("• Tiempo bloqueo: " + getTiempoBloqueoMinutos());
            System.out.println("• Confianza mínima: " + getConfianzaMinimaFacial());
            System.out.println("• Tamaño imagen: " + getTamañoImagen());
            System.out.println("• URL BD: " + getBDUrlCompleta());
            
            System.out.println("✅ Pruebas de configuración completadas");
            
        } catch (Exception e) {
            System.err.println("❌ Error en pruebas de configuración: " + e.getMessage());
        }
    }
}