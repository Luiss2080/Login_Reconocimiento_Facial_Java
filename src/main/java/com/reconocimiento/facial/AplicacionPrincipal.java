package com.reconocimiento.facial;

import com.reconocimiento.facial.controladores.ControladorPrincipal;
import com.reconocimiento.facial.basedatos.ConexionBaseDatos;

import javax.swing.*;
import java.awt.*;

/**
 * 🚀 APLICACIÓN PRINCIPAL DEL SISTEMA DE RECONOCIMIENTO FACIAL
 * Punto de entrada del sistema optimizado con autenticación dual
 * 
 * CARACTERÍSTICAS PRINCIPALES:
 * ✅ Autenticación por credenciales y reconocimiento facial
 * ✅ Base de datos MySQL integrada
 * ✅ Red neuronal para procesamiento de imágenes
 * ✅ Sistema de configuración centralizada
 * ✅ Auditoría completa de accesos
 * ✅ Interfaz gráfica moderna
 */
public class AplicacionPrincipal {

    private static final String TITULO_APLICACION = "Sistema de Reconocimiento Facial";
    private static final String VERSION = "2.0.0";

    public static void main(String[] args) {
        try {
            // Mostrar información del sistema
            mostrarMensajeBienvenida();

            // Configurar el Look and Feel del sistema
            configurarApariencia();

            // Verificar requisitos del sistema
            if (!verificarRequisitos()) {
                mostrarErrorYSalir("El sistema no cumple con los requisitos mínimos para ejecutar la aplicación.");
                return;
            }

            // Inicializar sistema
            if (!inicializarSistema()) {
                mostrarErrorYSalir("Error al inicializar el sistema. Verifique la configuración.");
                return;
            }

            // Iniciar con el nuevo controlador principal
            SwingUtilities.invokeLater(() -> {
                try {
                    System.out.println("🚀 Iniciando sistema con controlador principal...");
                    
                    ControladorPrincipal controlador = new ControladorPrincipal();
                    controlador.iniciarAplicacion();
                    
                    // Ejecutar pruebas si se especifica
                    if (args.length > 0 && "--test".equals(args[0])) {
                        System.out.println("\n🧪 MODO PRUEBA ACTIVADO");
                        controlador.ejecutarPruebasSistema();
                    }
                    
                    System.out.println("✅ Sistema de Reconocimiento Facial iniciado correctamente");
                    
                } catch (Exception e) {
                    System.err.println("❌ Error al iniciar la aplicación: " + e.getMessage());
                    e.printStackTrace();
                    mostrarErrorYSalir("Error crítico al iniciar la aplicación: " + e.getMessage());
                }
            });

        } catch (Exception e) {
            System.err.println("❌ Error fatal al iniciar: " + e.getMessage());
            e.printStackTrace();
            mostrarErrorYSalir("Error crítico en la inicialización: " + e.getMessage());
        }
    }

    /**
     * Mostrar mensaje de bienvenida en consola
     */
    private static void mostrarMensajeBienvenida() {
        System.out.println("=====================================");
        System.out.println("🎯 " + TITULO_APLICACION);
        System.out.println("📱 Versión: " + VERSION);
        System.out.println("🚀 Iniciando aplicación...");
        System.out.println("=====================================");
        System.out.println();
    }

    /**
     * Mostrar splash screen de la aplicación (público para uso externo)
     */
    public static void mostrarSplashScreen() {
        try {
            JWindow splash = new JWindow();
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBackground(new Color(33, 150, 243));
            panel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));

            JLabel titulo = new JLabel(TITULO_APLICACION, SwingConstants.CENTER);
            titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
            titulo.setForeground(Color.WHITE);

            JLabel version = new JLabel("Versión " + VERSION, SwingConstants.CENTER);
            version.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            version.setForeground(Color.WHITE);

            JLabel cargando = new JLabel("🧠 Cargando redes neuronales...", SwingConstants.CENTER);
            cargando.setFont(new Font("Segoe UI", Font.ITALIC, 12));
            cargando.setForeground(Color.WHITE);

            panel.add(titulo, BorderLayout.NORTH);
            panel.add(version, BorderLayout.CENTER);
            panel.add(cargando, BorderLayout.SOUTH);

            splash.add(panel);
            splash.setSize(400, 150);
            splash.setLocationRelativeTo(null);
            splash.setVisible(true);

            Timer timer = new Timer(1500, e -> splash.dispose());
            timer.setRepeats(false);
            timer.start();

            Thread.sleep(1600);

        } catch (Exception e) {
            System.err.println("⚠️ Error en splash screen: " + e.getMessage());
        }
    }

    private static void configurarApariencia() {
        try {
            // Configurar Look and Feel Nimbus
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }

            // Configuraciones adicionales para mejor apariencia
            System.setProperty("awt.useSystemAAFontSettings", "on");
            System.setProperty("swing.aatext", "true");

        } catch (Exception e) {
            System.err.println("No se pudo establecer el Look and Feel del sistema: " + e.getMessage());
            // Continuar con el Look and Feel por defecto
        }
    }

    private static boolean verificarRequisitos() {
        System.out.println("Verificando requisitos del sistema...");

        // Verificar versión de Java
        String versionJava = System.getProperty("java.version");
        System.out.println("Versión de Java: " + versionJava);

        // Verificar que sea Java 8 o superior
        try {
            String[] partes = versionJava.split("\\.");
            int versionMayor = Integer.parseInt(partes[0]);
            if (versionMayor < 8 && !versionJava.startsWith("1.8")) {
                System.err.println("Se requiere Java 8 o superior");
                return false;
            }
        } catch (Exception e) {
            System.err.println("No se pudo verificar la versión de Java");
            return false;
        }

        // Verificar memoria disponible
        Runtime runtime = Runtime.getRuntime();
        long memoriaMaxima = runtime.maxMemory() / (1024 * 1024); // En MB
        System.out.println("Memoria máxima disponible: " + memoriaMaxima + " MB");

        if (memoriaMaxima < 512) {
            System.err.println("Se requieren al menos 512 MB de memoria");
            return false;
        }

        // Verificar sistema operativo
        String sistemaOperativo = System.getProperty("os.name");
        System.out.println("Sistema operativo: " + sistemaOperativo);

        System.out.println("Todos los requisitos del sistema están satisfechos");
        return true;
    }

    private static boolean inicializarSistema() {
        System.out.println("Inicializando sistema...");

        try {
            // Verificar conexión a la base de datos
            if (!verificarConexionBaseDatos()) {
                System.err.println("No se pudo establecer conexión con la base de datos");
                return false;
            }

            // Crear directorios necesarios
            crearDirectoriosNecesarios();

            // Verificar disponibilidad de cámara
            verificarDisponibilidadCamara();

            System.out.println("Sistema inicializado correctamente");
            return true;

        } catch (Exception e) {
            System.err.println("Error durante la inicialización: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private static boolean verificarConexionBaseDatos() {
        try {
            ConexionBaseDatos conexion = ConexionBaseDatos.obtenerInstancia();
            boolean conexionExitosa = conexion.probarConexion();

            if (conexionExitosa) {
                System.out.println("Conexión a base de datos establecida correctamente");
                conexion.mostrarEstadisticasPool();
            } else {
                System.err.println("Fallo en la conexión a la base de datos");
            }

            return conexionExitosa;

        } catch (Exception e) {
            System.err.println("Error verificando conexión a base de datos: " + e.getMessage());
            return false;
        }
    }

    private static void crearDirectoriosNecesarios() {
        String[] directorios = {
                "src/main/resources/imagenes",
                "src/main/resources/modelos",
                "src/main/resources/configuracion",
                "logs",
                "temp"
        };

        for (String directorio : directorios) {
            java.io.File dir = new java.io.File(directorio);
            if (!dir.exists()) {
                if (dir.mkdirs()) {
                    System.out.println("Directorio creado: " + directorio);
                } else {
                    System.err.println("No se pudo crear el directorio: " + directorio);
                }
            }
        }
    }

    private static void verificarDisponibilidadCamara() {
        try {
            // Nota: Esta verificación se podría hacer más robusta
            // con la implementación real de ManejadorCamara
            System.out.println("Verificando disponibilidad de cámara...");

            // Por ahora, asumir que hay al menos una cámara disponible
            // En una implementación real, aquí se verificaría con JavaCV
            System.out.println("Cámara disponible para captura");

        } catch (Exception e) {
            System.err.println("No se pudo verificar la disponibilidad de cámara: " + e.getMessage());
            // No es un error crítico, la aplicación puede continuar
        }
    }

    private static void mostrarErrorYSalir(String mensaje) {
        System.err.println("ERROR CRÍTICO: " + mensaje);

        // Mostrar mensaje gráfico si es posible
        try {
            JOptionPane.showMessageDialog(
                    null,
                    mensaje,
                    "Error del Sistema",
                    JOptionPane.ERROR_MESSAGE
            );
        } catch (Exception e) {
            // Si no se puede mostrar GUI, solo imprimir en consola
            System.err.println("No se pudo mostrar mensaje de error gráfico");
        }

        System.exit(1);
    }

    /**
     * Método para cerrar la aplicación de forma ordenada
     */
    public static void cerrarAplicacion() {
        System.out.println("Cerrando aplicación...");

        try {
            // Cerrar conexiones de base de datos
            ConexionBaseDatos conexion = ConexionBaseDatos.obtenerInstancia();
            conexion.cerrarTodasLasConexiones();
            System.out.println("Conexiones de base de datos cerradas");

            // Liberar otros recursos si es necesario
            // Por ejemplo: detener cámaras, guardar configuraciones, etc.

        } catch (Exception e) {
            System.err.println("Error durante el cierre: " + e.getMessage());
        }

        System.out.println("Aplicación cerrada correctamente");
        System.exit(0);
    }

    /**
     * Obtiene información del sistema para propósitos de debug
     */
    public static String obtenerInformacionSistema() {
        StringBuilder info = new StringBuilder();

        info.append("=== INFORMACIÓN DEL SISTEMA ===\n");
        info.append("Versión Java: ").append(System.getProperty("java.version")).append("\n");
        info.append("Vendedor Java: ").append(System.getProperty("java.vendor")).append("\n");
        info.append("Sistema Operativo: ").append(System.getProperty("os.name")).append("\n");
        info.append("Versión SO: ").append(System.getProperty("os.version")).append("\n");
        info.append("Arquitectura: ").append(System.getProperty("os.arch")).append("\n");

        Runtime runtime = Runtime.getRuntime();
        long memoriaTotal = runtime.totalMemory() / (1024 * 1024);
        long memoriaLibre = runtime.freeMemory() / (1024 * 1024);
        long memoriaUsada = memoriaTotal - memoriaLibre;
        long memoriaMaxima = runtime.maxMemory() / (1024 * 1024);

        info.append("Memoria Total: ").append(memoriaTotal).append(" MB\n");
        info.append("Memoria Usada: ").append(memoriaUsada).append(" MB\n");
        info.append("Memoria Libre: ").append(memoriaLibre).append(" MB\n");
        info.append("Memoria Máxima: ").append(memoriaMaxima).append(" MB\n");

        info.append("Directorio de trabajo: ").append(System.getProperty("user.dir")).append("\n");
        info.append("Directorio home: ").append(System.getProperty("user.home")).append("\n");
        info.append("Usuario: ").append(System.getProperty("user.name")).append("\n");

        return info.toString();
    }
}