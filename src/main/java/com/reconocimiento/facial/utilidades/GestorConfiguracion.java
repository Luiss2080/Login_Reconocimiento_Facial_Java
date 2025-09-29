package com.reconocimiento.facial.utilidades;

import java.io.*;
import java.util.Properties;
import java.util.Map;
import java.util.HashMap;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 📄 GESTOR DE ARCHIVOS DE CONFIGURACIÓN
 * Utilidades para leer, escribir y gestionar archivos de configuración del sistema
 */
public class GestorConfiguracion {

    private static final String ARCHIVO_CONFIG_PRINCIPAL = "config/sistema.properties";
    private static final String ARCHIVO_CONFIG_BACKUP = "config/sistema_backup.properties";
    private static final String COMENTARIO_AUTO = "# Archivo generado automáticamente el ";
    
    private static Properties propiedades = null;
    private static String ultimaRutaCargada = null;

    /**
     * Carga las propiedades desde el archivo principal
     * @return Properties cargadas
     * @throws IOException Si hay error al leer el archivo
     */
    public static Properties cargarConfiguracion() throws IOException {
        return cargarConfiguracion(ARCHIVO_CONFIG_PRINCIPAL);
    }

    /**
     * Carga propiedades desde una ruta específica
     * @param rutaArchivo Ruta del archivo de propiedades
     * @return Properties cargadas
     * @throws IOException Si hay error al leer el archivo
     */
    public static Properties cargarConfiguracion(String rutaArchivo) throws IOException {
        if (rutaArchivo == null || rutaArchivo.trim().isEmpty()) {
            throw new IllegalArgumentException("La ruta del archivo no puede estar vacía");
        }

        File archivo = new File(rutaArchivo);
        if (!archivo.exists()) {
            throw new FileNotFoundException("Archivo de configuración no encontrado: " + rutaArchivo);
        }

        Properties props = new Properties();
        try (InputStream input = new FileInputStream(archivo)) {
            props.load(input);
            propiedades = props;
            ultimaRutaCargada = rutaArchivo;
            return props;
        }
    }

    /**
     * Guarda propiedades en el archivo principal
     * @param props Properties a guardar
     * @throws IOException Si hay error al escribir
     */
    public static void guardarConfiguracion(Properties props) throws IOException {
        guardarConfiguracion(props, ARCHIVO_CONFIG_PRINCIPAL);
    }

    /**
     * Guarda propiedades en una ruta específica
     * @param props Properties a guardar
     * @param rutaArchivo Ruta donde guardar
     * @throws IOException Si hay error al escribir
     */
    public static void guardarConfiguracion(Properties props, String rutaArchivo) throws IOException {
        if (props == null) {
            throw new IllegalArgumentException("Las propiedades no pueden ser null");
        }
        if (rutaArchivo == null || rutaArchivo.trim().isEmpty()) {
            throw new IllegalArgumentException("La ruta del archivo no puede estar vacía");
        }

        File archivo = new File(rutaArchivo);
        File directorio = archivo.getParentFile();
        
        // Crear directorio si no existe
        if (directorio != null && !directorio.exists()) {
            directorio.mkdirs();
        }

        String comentario = COMENTARIO_AUTO + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        
        try (OutputStream output = new FileOutputStream(archivo)) {
            props.store(output, comentario);
        }
    }

    /**
     * Obtiene valor de configuración como String
     * @param clave Clave de la propiedad
     * @param valorPorDefecto Valor por defecto si no existe
     * @return Valor de la propiedad
     */
    public static String obtenerString(String clave, String valorPorDefecto) {
        if (propiedades == null) {
            try {
                cargarConfiguracion();
            } catch (IOException e) {
                return valorPorDefecto;
            }
        }
        return propiedades.getProperty(clave, valorPorDefecto);
    }

    /**
     * Obtiene valor de configuración como int
     * @param clave Clave de la propiedad
     * @param valorPorDefecto Valor por defecto si no existe o hay error
     * @return Valor entero
     */
    public static int obtenerInt(String clave, int valorPorDefecto) {
        String valor = obtenerString(clave, String.valueOf(valorPorDefecto));
        try {
            return Integer.parseInt(valor);
        } catch (NumberFormatException e) {
            return valorPorDefecto;
        }
    }

    /**
     * Obtiene valor de configuración como double
     * @param clave Clave de la propiedad
     * @param valorPorDefecto Valor por defecto si no existe o hay error
     * @return Valor decimal
     */
    public static double obtenerDouble(String clave, double valorPorDefecto) {
        String valor = obtenerString(clave, String.valueOf(valorPorDefecto));
        try {
            return Double.parseDouble(valor);
        } catch (NumberFormatException e) {
            return valorPorDefecto;
        }
    }

    /**
     * Obtiene valor de configuración como boolean
     * @param clave Clave de la propiedad
     * @param valorPorDefecto Valor por defecto si no existe
     * @return Valor booleano
     */
    public static boolean obtenerBoolean(String clave, boolean valorPorDefecto) {
        String valor = obtenerString(clave, String.valueOf(valorPorDefecto));
        return "true".equalsIgnoreCase(valor) || "yes".equalsIgnoreCase(valor) || "1".equals(valor);
    }

    /**
     * Establece valor de configuración
     * @param clave Clave de la propiedad
     * @param valor Valor a establecer
     */
    public static void establecerValor(String clave, String valor) {
        if (propiedades == null) {
            propiedades = new Properties();
        }
        propiedades.setProperty(clave, valor);
    }

    /**
     * Establece valor entero
     * @param clave Clave de la propiedad
     * @param valor Valor entero
     */
    public static void establecerInt(String clave, int valor) {
        establecerValor(clave, String.valueOf(valor));
    }

    /**
     * Establece valor decimal
     * @param clave Clave de la propiedad
     * @param valor Valor decimal
     */
    public static void establecerDouble(String clave, double valor) {
        establecerValor(clave, String.valueOf(valor));
    }

    /**
     * Establece valor booleano
     * @param clave Clave de la propiedad
     * @param valor Valor booleano
     */
    public static void establecerBoolean(String clave, boolean valor) {
        establecerValor(clave, String.valueOf(valor));
    }

    /**
     * Crea backup de la configuración actual
     * @throws IOException Si hay error al crear backup
     */
    public static void crearBackup() throws IOException {
        if (propiedades == null) {
            cargarConfiguracion();
        }
        guardarConfiguracion(propiedades, ARCHIVO_CONFIG_BACKUP);
    }

    /**
     * Restaura configuración desde backup
     * @throws IOException Si hay error al restaurar
     */
    public static void restaurarBackup() throws IOException {
        Properties propsBackup = cargarConfiguracion(ARCHIVO_CONFIG_BACKUP);
        guardarConfiguracion(propsBackup);
        propiedades = propsBackup;
    }

    /**
     * Valida que todas las claves requeridas estén presentes
     * @param clavesRequeridas Array de claves que deben existir
     * @return Map con claves faltantes y sus valores sugeridos
     */
    public static Map<String, String> validarConfiguracion(String[] clavesRequeridas) {
        if (propiedades == null) {
            try {
                cargarConfiguracion();
            } catch (IOException e) {
                // Si no se puede cargar, todas las claves están faltantes
                Map<String, String> faltantes = new HashMap<>();
                for (String clave : clavesRequeridas) {
                    faltantes.put(clave, "valor_requerido");
                }
                return faltantes;
            }
        }

        Map<String, String> faltantes = new HashMap<>();
        for (String clave : clavesRequeridas) {
            if (!propiedades.containsKey(clave) || propiedades.getProperty(clave).trim().isEmpty()) {
                faltantes.put(clave, obtenerValorSugerido(clave));
            }
        }

        return faltantes;
    }

    /**
     * Obtiene valores sugeridos para claves comunes
     * @param clave Clave de configuración
     * @return Valor sugerido
     */
    private static String obtenerValorSugerido(String clave) {
        switch (clave) {
            case "bd.host": return "localhost";
            case "bd.puerto": return "3307";
            case "bd.usuario": return "root";
            case "bd.password": return "";
            case "bd.nombre": return "sistema_reconocimiento_facial";
            case "neural.tamaño_imagen": return "64";
            case "seguridad.max_intentos_fallidos": return "5";
            case "seguridad.confianza_minima_facial": return "0.85";
            case "camara.indice": return "0";
            case "camara.resolucion_ancho": return "640";
            case "camara.resolucion_alto": return "480";
            default: return "configurar_valor";
        }
    }

    /**
     * Recarga configuración desde archivo
     * @throws IOException Si hay error al recargar
     */
    public static void recargarConfiguracion() throws IOException {
        if (ultimaRutaCargada != null) {
            cargarConfiguracion(ultimaRutaCargada);
        } else {
            cargarConfiguracion();
        }
    }

    /**
     * Verifica si la configuración ha sido cargada
     * @return true si está cargada
     */
    public static boolean estaConfiguracin() {
        return propiedades != null && !propiedades.isEmpty();
    }

    /**
     * Obtiene todas las propiedades cargadas
     * @return Properties actuales (copia de solo lectura)
     */
    public static Properties obtenerTodasLasPropiedades() {
        if (propiedades == null) {
            return new Properties();
        }
        
        Properties copia = new Properties();
        copia.putAll(propiedades);
        return copia;
    }

    /**
     * Lista todas las claves disponibles
     * @return Array de claves
     */
    public static String[] listarClaves() {
        if (propiedades == null) {
            return new String[0];
        }
        
        return propiedades.stringPropertyNames().toArray(new String[0]);
    }

    /**
     * Información sobre el estado de la configuración
     */
    public static class InfoConfiguracion {
        public final boolean cargada;
        public final String rutaArchivo;
        public final int totalPropiedades;
        public final String[] clavesFaltantes;

        public InfoConfiguracion(boolean cargada, String rutaArchivo, int totalPropiedades, String[] clavesFaltantes) {
            this.cargada = cargada;
            this.rutaArchivo = rutaArchivo;
            this.totalPropiedades = totalPropiedades;
            this.clavesFaltantes = clavesFaltantes;
        }
    }

    /**
     * Obtiene información del estado actual de configuración
     * @param clavesRequeridas Claves que se consideran obligatorias
     * @return Información de configuración
     */
    public static InfoConfiguracion obtenerInfoConfiguracion(String[] clavesRequeridas) {
        boolean cargada = propiedades != null;
        String ruta = ultimaRutaCargada != null ? ultimaRutaCargada : ARCHIVO_CONFIG_PRINCIPAL;
        int total = cargada ? propiedades.size() : 0;
        
        Map<String, String> faltantesMap = validarConfiguracion(clavesRequeridas);
        String[] faltantes = faltantesMap.keySet().toArray(new String[0]);
        
        return new InfoConfiguracion(cargada, ruta, total, faltantes);
    }
}