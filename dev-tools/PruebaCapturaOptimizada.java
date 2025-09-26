package com.reconocimiento.facial.test;

import com.reconocimiento.facial.procesamiento.ManejadorCamara;

/**
 * Prueba específica del nuevo sistema de captura optimizada
 */
public class PruebaCapturaOptimizada {
    
    public static void main(String[] args) {
        System.out.println("🎯 PRUEBA DE CAPTURA OPTIMIZADA");
        System.out.println("================================");
        
        ManejadorCamara manejador = new ManejadorCamara();
        
        try {
            System.out.println("📋 Inicializando cámara con método optimizado...");
            long startInit = System.currentTimeMillis();
            
            if (manejador.inicializarCamara()) {
                long initTime = System.currentTimeMillis() - startInit;
                System.out.println("✅ Cámara inicializada en " + initTime + "ms");
                
                // Realizar 3 capturas para probar el sistema de reintentos
                for (int i = 1; i <= 3; i++) {
                    System.out.println("\n🔄 Captura #" + i + ":");
                    long startCapture = System.currentTimeMillis();
                    
                    byte[] imagenBytes = manejador.capturarImagen();
                    long captureTime = System.currentTimeMillis() - startCapture;
                    
                    if (imagenBytes != null && imagenBytes.length > 0) {
                        System.out.println("✅ Captura exitosa - Tamaño: " + imagenBytes.length + " bytes");
                        System.out.println("⏱️ Tiempo total de captura: " + captureTime + "ms");
                    } else {
                        System.out.println("❌ Error en la captura #" + i);
                    }
                    
                    // Pausa entre capturas
                    if (i < 3) {
                        Thread.sleep(2000);
                    }
                }
                
                manejador.detenerCamara();
                System.out.println("\n🛑 Cámara detenida correctamente");
                
            } else {
                System.err.println("❌ No se pudo inicializar la cámara");
            }
            
        } catch (Exception e) {
            System.err.println("💥 Error durante la prueba: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("\n🏁 Prueba completada");
    }
}