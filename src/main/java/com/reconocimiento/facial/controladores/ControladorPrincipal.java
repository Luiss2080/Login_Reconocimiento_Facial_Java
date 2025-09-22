package com.reconocimiento.facial.controladores;

import com.reconocimiento.facial.configuracion.ConfiguracionSistema;
import com.reconocimiento.facial.servicios.ServicioUsuarioMejorado;
import com.reconocimiento.facial.modelos.Usuario;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * 🎮 CONTROLADOR PRINCIPAL DEL SISTEMA
 * Coordina todas las operaciones del sistema de reconocimiento facial
 * Integra servicios, configuración y formularios
 */
public class ControladorPrincipal {
    
    // ========== COMPONENTES DEL SISTEMA ==========
    private ConfiguracionSistema configuracion;
    private ServicioUsuarioMejorado servicioUsuario;
    
    // ========== ESTADO DEL SISTEMA ==========
    private Usuario usuarioActual;
    private boolean sistemaInicializado;
    
    /**
     * Constructor principal
     */
    public ControladorPrincipal() {
        try {
            System.out.println("🚀 Iniciando Sistema de Reconocimiento Facial...");
            System.out.println("⏰ " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            
            // Inicializar componentes
            this.configuracion = ConfiguracionSistema.obtenerInstancia();
            this.servicioUsuario = new ServicioUsuarioMejorado();
            
            this.sistemaInicializado = true;
            
            System.out.println("✅ Sistema inicializado correctamente");
            mostrarInformacionSistema();
            
        } catch (Exception e) {
            System.err.println("❌ Error crítico inicializando sistema: " + e.getMessage());
            e.printStackTrace();
            
            // Mostrar error al usuario
            JOptionPane.showMessageDialog(null, 
                "Error crítico iniciando el sistema:\n" + e.getMessage(),
                "Error del Sistema", 
                JOptionPane.ERROR_MESSAGE);
            
            System.exit(1);
        }
    }
    
    /**
     * 🚀 Iniciar la aplicación
     */
    public void iniciarAplicacion() {
        if (!sistemaInicializado) {
            System.err.println("❌ Sistema no inicializado correctamente");
            return;
        }
        
        try {
            // Configurar Look and Feel
            configurarLookAndFeel();
            
            System.out.println("✅ Aplicación lista para usar");
            
        } catch (Exception e) {
            System.err.println("❌ Error iniciando aplicación: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 🎨 Configurar apariencia de la aplicación
     */
    private void configurarLookAndFeel() {
        try {
            // Usar look and feel nativo
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
            
            // Configuraciones adicionales de UI
            UIManager.put("OptionPane.yesButtonText", "Sí");
            UIManager.put("OptionPane.noButtonText", "No");
            UIManager.put("OptionPane.cancelButtonText", "Cancelar");
            
            System.out.println("✅ Look and Feel configurado");
            
        } catch (Exception e) {
            System.err.println("⚠️ No se pudo configurar Look and Feel: " + e.getMessage());
        }
    }
    
    /**
     * 🔐 Procesar autenticación por credenciales
     */
    public boolean autenticarUsuario(String nombreUsuario, String contrasena) {
        try {
            System.out.println("🔐 Procesando autenticación por credenciales...");
            
            Optional<Usuario> usuarioOpt = servicioUsuario.autenticarUsuario(nombreUsuario, contrasena);
            
            if (usuarioOpt.isPresent()) {
                Usuario usuario = usuarioOpt.get();
                this.usuarioActual = usuario;
                System.out.println("✅ Autenticación exitosa: " + usuario.getNombreUsuario());
                return true;
                
            } else {
                System.out.println("❌ Autenticación fallida");
                return false;
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error en autenticación: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 📷 Procesar autenticación facial
     */
    public boolean autenticarConReconocimientoFacial(BufferedImage imagenRostro) {
        try {
            System.out.println("📷 Procesando autenticación facial...");
            
            if (imagenRostro == null) {
                System.err.println("❌ Imagen de rostro es null");
                return false;
            }
            
            Optional<Usuario> usuarioOpt = servicioUsuario.autenticarConReconocimientoFacial(imagenRostro);
            
            if (usuarioOpt.isPresent()) {
                Usuario usuario = usuarioOpt.get();
                this.usuarioActual = usuario;
                System.out.println("✅ Reconocimiento facial exitoso: " + usuario.getNombreUsuario());
                return true;
                
            } else {
                System.out.println("❌ Reconocimiento facial fallido");
                return false;
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error en reconocimiento facial: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 📊 Cerrar sesión
     */
    public void cerrarSesion() {
        try {
            System.out.println("🚪 Cerrando sesión...");
            
            if (usuarioActual != null) {
                System.out.println("🚪 Sesión cerrada para: " + usuarioActual.getNombreUsuario());
                usuarioActual = null;
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error cerrando sesión: " + e.getMessage());
        }
    }
    
    /**
     * 🚪 Salir del sistema
     */
    public void salirDelSistema() {
        try {
            System.out.println("🚪 Saliendo del sistema...");
            System.out.println("✅ Sistema cerrado correctamente");
            System.exit(0);
            
        } catch (Exception e) {
            System.err.println("❌ Error saliendo del sistema: " + e.getMessage());
            System.exit(1);
        }
    }
    
    /**
     * 📊 Mostrar información del sistema
     */
    private void mostrarInformacionSistema() {
        try {
            System.out.println("\n" + configuracion.obtenerResumenConfiguracion());
            System.out.println(servicioUsuario.obtenerEstadisticas());
            
        } catch (Exception e) {
            System.err.println("⚠️ Error mostrando información del sistema: " + e.getMessage());
        }
    }
    
    /**
     * 🧪 Ejecutar pruebas del sistema
     */
    public void ejecutarPruebasSistema() {
        System.out.println("🧪 Ejecutando pruebas del sistema completo...");
        
        try {
            // Probar configuración
            configuracion.ejecutarPruebas();
            
            // Probar servicio de usuarios
            servicioUsuario.ejecutarPruebas();
            
            System.out.println("✅ Todas las pruebas del sistema completadas");
            
        } catch (Exception e) {
            System.err.println("❌ Error en pruebas del sistema: " + e.getMessage());
        }
    }
    
    // ========== GETTERS ==========
    
    public ConfiguracionSistema getConfiguracion() { return configuracion; }
    public ServicioUsuarioMejorado getServicioUsuario() { return servicioUsuario; }
    public Usuario getUsuarioActual() { return usuarioActual; }
    public boolean isSistemaInicializado() { return sistemaInicializado; }
}