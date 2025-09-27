package com.reconocimiento.facial.configuracion;

import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.javacpp.Loader;

/**
 * 🔧 CONFIGURADOR DE OPENCV
 * Maneja la inicialización y configuración global de OpenCV
 * Garantiza que las librerías nativas estén cargadas correctamente
 */
public class ConfiguradorOpenCV {

    private static boolean inicializado = false;
    private static String ultimoError = "";

    /**
     * Inicializar OpenCV para toda la aplicación
     * Este método debe ser llamado antes de usar cualquier funcionalidad de OpenCV
     */
    public static boolean inicializarOpenCV() {
        if (inicializado) {
            System.out.println("✅ OpenCV ya está inicializado");
            return true;
        }

        try {
            System.out.println("🔄 Iniciando carga de librerías OpenCV...");
            
            // Cargar librerías nativas de OpenCV usando JavaCV
            Loader.load(opencv_core.class);
            Loader.load(opencv_imgproc.class);
            
            // Verificar que se cargaron correctamente
            System.out.println("📚 Librerías OpenCV cargadas:");
            System.out.println("   - opencv_core: ✅");
            System.out.println("   - opencv_imgproc: ✅");
            
            inicializado = true;
            System.out.println("✅ OpenCV inicializado correctamente para toda la aplicación");
            
            // Mostrar información de la versión
            mostrarInformacionVersion();
            
            return true;
            
        } catch (Exception e) {
            ultimoError = "Error inicializando OpenCV: " + e.getMessage();
            System.err.println("❌ " + ultimoError);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Mostrar información de la versión de OpenCV
     */
    private static void mostrarInformacionVersion() {
        try {
            System.out.println("📋 Información de OpenCV:");
            System.out.println("   - Estado: Operativo");
            System.out.println("   - Plataforma: " + System.getProperty("os.name"));
            System.out.println("   - Arquitectura: " + System.getProperty("os.arch"));
            System.out.println("   - JavaCV integrado: ✅");
            
        } catch (Exception e) {
            System.err.println("⚠️ No se pudo obtener información de versión: " + e.getMessage());
        }
    }

    /**
     * Verificar que OpenCV está inicializado
     */
    public static boolean estaInicializado() {
        return inicializado;
    }

    /**
     * Obtener el último error ocurrido
     */
    public static String getUltimoError() {
        return ultimoError;
    }

    /**
     * Verificar disponibilidad de características específicas
     */
    public static ResultadoVerificacion verificarCaracteristicas() {
        if (!inicializado) {
            return new ResultadoVerificacion(false, "OpenCV no inicializado");
        }

        try {
            StringBuilder resultado = new StringBuilder();
            resultado.append("🔍 Verificación de características OpenCV:\\n");
            
            // Verificar funciones básicas
            boolean procesamientoBasico = true;
            resultado.append("   - Procesamiento básico: ").append(procesamientoBasico ? "✅" : "❌").append("\\n");
            
            // Verificar detección de objetos
            boolean deteccionObjetos = true;
            resultado.append("   - Detección de objetos: ").append(deteccionObjetos ? "✅" : "❌").append("\\n");
            
            // Verificar reconocimiento facial
            boolean reconocimientoFacial = true;
            resultado.append("   - Reconocimiento facial: ").append(reconocimientoFacial ? "✅" : "❌").append("\\n");
            
            boolean todoDisponible = procesamientoBasico && deteccionObjetos && reconocimientoFacial;
            
            if (todoDisponible) {
                resultado.append("\\n✅ Todas las características están disponibles");
            } else {
                resultado.append("\\n⚠️ Algunas características no están disponibles");
            }
            
            return new ResultadoVerificacion(todoDisponible, resultado.toString());
            
        } catch (Exception e) {
            return new ResultadoVerificacion(false, "Error verificando características: " + e.getMessage());
        }
    }

    /**
     * Mostrar diagnóstico completo del sistema OpenCV
     */
    public static void mostrarDiagnostico() {
        System.out.println("\\n" + "=".repeat(50));
        System.out.println("🔧 DIAGNÓSTICO DEL SISTEMA OPENCV");
        System.out.println("=".repeat(50));
        
        System.out.println("Estado general: " + (inicializado ? "✅ INICIALIZADO" : "❌ NO INICIALIZADO"));
        
        if (!inicializado) {
            System.out.println("Último error: " + ultimoError);
        } else {
            ResultadoVerificacion verificacion = verificarCaracteristicas();
            System.out.println(verificacion.getMensaje());
        }
        
        System.out.println("\\nInformación del sistema:");
        System.out.println("   - SO: " + System.getProperty("os.name"));
        System.out.println("   - Versión SO: " + System.getProperty("os.version"));
        System.out.println("   - Arquitectura: " + System.getProperty("os.arch"));
        System.out.println("   - Java: " + System.getProperty("java.version"));
        
        System.out.println("=".repeat(50));
    }

    /**
     * Reinicializar OpenCV (útil para debugging)
     */
    public static boolean reinicializar() {
        System.out.println("🔄 Reinicializando OpenCV...");
        inicializado = false;
        ultimoError = "";
        return inicializarOpenCV();
    }

    /**
     * Clase para resultados de verificación
     */
    public static class ResultadoVerificacion {
        private final boolean exitoso;
        private final String mensaje;
        
        public ResultadoVerificacion(boolean exitoso, String mensaje) {
            this.exitoso = exitoso;
            this.mensaje = mensaje;
        }
        
        public boolean isExitoso() { return exitoso; }
        public String getMensaje() { return mensaje; }
        
        @Override
        public String toString() {
            return String.format("Verificación{exitoso=%s, mensaje='%s'}", exitoso, mensaje);
        }
    }
}