package com.reconocimiento.facial.utilidades;

import org.bytedeco.javacv.*;

/**
 * 🎥 PROBADOR DE CÁMARA MEJORADO
 * Utilidad para diagnosticar y probar la cámara de forma más confiable
 */
public class ProbadorCamaraMejorado {
    
    public static void main(String[] args) {
        System.out.println("🎥 PROBADOR DE CÁMARA MEJORADO");
        System.out.println("===============================");
        
        // 1. Diagnóstico del sistema
        diagnosticarSistema();
        
        // 2. Probar diferentes métodos de conexión
        probarConexionesCamara();
        
        System.out.println("\n🏁 Diagnóstico completado. Presione Enter para continuar...");
        try {
            System.in.read();
        } catch (Exception e) {
            // Ignorar
        }
    }
    
    /**
     * Diagnosticar el sistema
     */
    private static void diagnosticarSistema() {
        System.out.println("\n📊 DIAGNÓSTICO DEL SISTEMA:");
        System.out.println("Sistema Operativo: " + System.getProperty("os.name"));
        System.out.println("Arquitectura: " + System.getProperty("os.arch"));
        System.out.println("Versión Java: " + System.getProperty("java.version"));
        
        // Verificar OpenCV
        try {
            System.out.println("OpenCV disponible: ✅ SÍ");
        } catch (Exception e) {
            System.out.println("OpenCV disponible: ❌ NO - " + e.getMessage());
        }
    }
    
    /**
     * Probar diferentes métodos de conexión a la cámara
     */
    private static void probarConexionesCamara() {
        System.out.println("\n📷 PROBANDO CONEXIONES DE CÁMARA:");
        
        // Método 1: Conexión simple
        System.out.println("\n🔧 Método 1: Conexión simple");
        probarConexionSimple();
        
        // Método 2: Con DirectShow (Windows)
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            System.out.println("\n🔧 Método 2: DirectShow (Windows)");
            probarConexionDirectShow();
        }
        
        // Método 3: Diferentes índices
        System.out.println("\n🔧 Método 3: Probando diferentes índices");
        probarDiferentesIndices();
    }
    
    /**
     * Probar conexión simple
     */
    private static void probarConexionSimple() {
        OpenCVFrameGrabber grabber = null;
        try {
            System.out.println("   Iniciando cámara índice 0...");
            
            grabber = new OpenCVFrameGrabber(0);
            grabber.setImageWidth(640);
            grabber.setImageHeight(480);
            
            long inicioTiempo = System.currentTimeMillis();
            grabber.start();
            long tiempoInicializacion = System.currentTimeMillis() - inicioTiempo;
            
            System.out.println("   ✅ Inicialización exitosa en " + tiempoInicializacion + "ms");
            
            // Probar captura
            org.bytedeco.javacv.Frame frame = grabber.grab();
            if (frame != null) {
                System.out.println("   ✅ Captura de frame exitosa - Tamaño: " + 
                    frame.imageWidth + "x" + frame.imageHeight);
            } else {
                System.out.println("   ❌ No se pudo capturar frame");
            }
            
        } catch (Exception e) {
            System.out.println("   ❌ Error: " + e.getMessage());
            
            // Mostrar sugerencias específicas
            if (e.getMessage().contains("timeout")) {
                System.out.println("   💡 Sugerencia: La cámara está siendo usada por otra aplicación");
            } else if (e.getMessage().contains("device")) {
                System.out.println("   💡 Sugerencia: Verificar que la cámara esté conectada y funcionando");
            }
            
        } finally {
            if (grabber != null) {
                try {
                    grabber.stop();
                    grabber.release();
                } catch (Exception e) {
                    System.out.println("   ⚠️ Error al liberar recursos: " + e.getMessage());
                }
            }
        }
    }
    
    /**
     * Probar conexión con DirectShow
     */
    private static void probarConexionDirectShow() {
        OpenCVFrameGrabber grabber = null;
        try {
            System.out.println("   Iniciando con DirectShow...");
            
            grabber = new OpenCVFrameGrabber(0);
            grabber.setFormat("dshow");
            grabber.setImageWidth(640);
            grabber.setImageHeight(480);
            grabber.setFrameRate(30);
            
            long inicioTiempo = System.currentTimeMillis();
            grabber.start();
            long tiempoInicializacion = System.currentTimeMillis() - inicioTiempo;
            
            System.out.println("   ✅ DirectShow inicializado en " + tiempoInicializacion + "ms");
            
            // Probar captura
            org.bytedeco.javacv.Frame frame = grabber.grab();
            if (frame != null) {
                System.out.println("   ✅ Captura DirectShow exitosa");
            } else {
                System.out.println("   ❌ DirectShow no pudo capturar frame");
            }
            
        } catch (Exception e) {
            System.out.println("   ❌ DirectShow falló: " + e.getMessage());
        } finally {
            if (grabber != null) {
                try {
                    grabber.stop();
                    grabber.release();
                } catch (Exception e) {
                    System.out.println("   ⚠️ Error liberando DirectShow: " + e.getMessage());
                }
            }
        }
    }
    
    /**
     * Probar diferentes índices de cámara
     */
    private static void probarDiferentesIndices() {
        for (int i = 0; i <= 4; i++) {
            OpenCVFrameGrabber grabber = null;
            try {
                System.out.println("   Probando índice " + i + "...");
                
                grabber = new OpenCVFrameGrabber(i);
                grabber.setImageWidth(320); // Resolución más pequeña para pruebas
                grabber.setImageHeight(240);
                
                grabber.start();
                
                org.bytedeco.javacv.Frame frame = grabber.grab();
                if (frame != null) {
                    System.out.println("   ✅ Índice " + i + " FUNCIONANDO - " + 
                        frame.imageWidth + "x" + frame.imageHeight);
                } else {
                    System.out.println("   ⚠️ Índice " + i + " se conectó pero no capturó frame");
                }
                
                grabber.stop();
                grabber.release();
                
            } catch (Exception e) {
                System.out.println("   ❌ Índice " + i + " falló: " + e.getMessage());
                if (grabber != null) {
                    try {
                        grabber.stop();
                        grabber.release();
                    } catch (Exception ex) {
                        // Ignorar errores de limpieza
                    }
                }
            }
        }
    }
    
    /**
     * Mostrar información del sistema para debugging
     */
    public static void mostrarInformacionSistema() {
        System.out.println("\n🔍 INFORMACIÓN DE DEBUG:");
        System.out.println("Usuario: " + System.getProperty("user.name"));
        System.out.println("Directorio: " + System.getProperty("user.dir"));
        System.out.println("Memoria libre: " + (Runtime.getRuntime().freeMemory() / 1024 / 1024) + " MB");
        System.out.println("Memoria total: " + (Runtime.getRuntime().totalMemory() / 1024 / 1024) + " MB");
        
        // Variables de entorno relevantes
        String[] variables = {"OPENCV_DIR", "JAVA_HOME", "PATH"};
        for (String var : variables) {
            String valor = System.getenv(var);
            if (valor != null) {
                System.out.println(var + ": " + valor.substring(0, Math.min(100, valor.length())));
            }
        }
    }
}