package com.reconocimiento.facial.utilidades;

import com.reconocimiento.facial.procesamiento.ManejadorCamara;

/**
 * Programa simple para probar la cámara antes de ejecutar el sistema completo
 */
public class PruebaCamara {
    public static void main(String[] args) {
        System.out.println("🎥 PRUEBA DE CÁMARA - Sistema de Reconocimiento Facial");
        System.out.println("========================================================");
        
        ManejadorCamara manejador = new ManejadorCamara();
        
        // Ejecutar diagnóstico
        manejador.diagnosticarCamara();
        
        // Intentar inicializar la cámara
        System.out.println("🔧 Intentando inicializar la cámara...");
        boolean exito = manejador.inicializarCamara();
        
        if (exito) {
            System.out.println("✅ ¡Cámara inicializada exitosamente!");
            System.out.println("📊 " + manejador.obtenerInformacionCamara());
            
            // Probar captura de imagen
            System.out.println("📸 Probando captura de imagen...");
            byte[] imagen = manejador.capturarImagen();
            
            if (imagen != null && imagen.length > 0) {
                System.out.println("✅ Imagen capturada exitosamente (" + imagen.length + " bytes)");
            } else {
                System.out.println("❌ Error capturando imagen");
            }
            
            // Limpiar recursos
            manejador.liberarCamara();
            System.out.println("🧹 Recursos liberados");
            
        } else {
            System.out.println("❌ No se pudo inicializar la cámara");
            System.out.println("\n💡 SOLUCIONES SUGERIDAS:");
            System.out.println("• Asegúrese de que no hay otras aplicaciones usando la cámara");
            System.out.println("• Verifique los permisos de cámara en Windows:");
            System.out.println("  - Configuración > Privacidad > Cámara > Permitir aplicaciones de escritorio");
            System.out.println("• Pruebe cerrar aplicaciones como Skype, Teams, Chrome con videollamadas");
            System.out.println("• Verifique que los drivers de la cámara estén actualizados");
        }
        
        System.out.println("\n🏁 Prueba completada. Presione Enter para salir...");
        try {
            System.in.read();
        } catch (Exception e) {
            // Ignorar
        }
    }
}