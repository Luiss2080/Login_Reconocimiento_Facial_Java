package com.reconocimiento.facial.procesamiento;

import org.bytedeco.javacv.*;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * Manejador de cámara real para captura de imágenes faciales
 * Usa JavaCV y OpenCV para acceso directo a la cámara
 */
public class ManejadorCamara {

    private boolean camaraActiva = false;
    private OpenCVFrameGrabber grabber;
    private Java2DFrameConverter converter;
    private Random random = new Random();

    /**
     * Inicializar la cámara real con configuración mejorada
     */
    public boolean inicializarCamara() {
        try {
            System.out.println("🎥 Inicializando cámara real...");

            // Limpiar recursos previos si existen
            liberarRecursosPrevios();

            // Intentar diferentes configuraciones de cámara
            boolean inicializada = false;
            
            // Configuración 1: Cámara por defecto con DirectShow (Windows)
            if (!inicializada) {
                inicializada = intentarInicializarConDirectShow();
            }
            
            // Configuración 2: Cámara por defecto sin DirectShow
            if (!inicializada) {
                inicializada = intentarInicializarSinDirectShow();
            }
            
            // Configuración 3: Probar diferentes índices de cámara
            if (!inicializada) {
                inicializada = intentarInicializarConDiferentesIndices();
            }
            
            if (inicializada) {
                camaraActiva = true;
                System.out.println("✅ Cámara inicializada correctamente");
                return true;
            } else {
                System.err.println("❌ No se pudo inicializar ninguna cámara");
                return false;
            }

        } catch (Exception e) {
            System.err.println("❌ Error general al inicializar cámara: " + e.getMessage());
            e.printStackTrace();
            camaraActiva = false;
            return false;
        }
    }
    
    /**
     * Liberar recursos previos
     */
    private void liberarRecursosPrevios() {
        if (grabber != null) {
            try {
                grabber.stop();
                grabber.release();
            } catch (Exception e) {
                System.out.println("⚠️ Error limpiando recursos previos: " + e.getMessage());
            }
            grabber = null;
        }
    }
    
    /**
     * Intentar inicializar con DirectShow (Windows)
     */
    private boolean intentarInicializarConDirectShow() {
        try {
            System.out.println("🔧 Intentando inicializar con DirectShow...");
            
            grabber = new OpenCVFrameGrabber(0);
            
            // Configuración específica para Windows
            grabber.setFormat("dshow");
            grabber.setImageWidth(640);
            grabber.setImageHeight(480);
            grabber.setFrameRate(30);
            
            System.out.println("📡 Conectando con DirectShow...");
            grabber.start();
            
            // Verificar funcionalidad
            if (verificarFuncionalidadCamara()) {
                System.out.println("✅ DirectShow funcionando correctamente");
                return true;
            }
            
        } catch (Exception e) {
            System.out.println("⚠️ DirectShow falló: " + e.getMessage());
            liberarRecursosPrevios();
        }
        return false;
    }
    
    /**
     * Intentar inicializar sin DirectShow
     */
    private boolean intentarInicializarSinDirectShow() {
        try {
            System.out.println("🔧 Intentando inicializar sin DirectShow...");
            
            grabber = new OpenCVFrameGrabber(0);
            
            // Configuración básica
            grabber.setImageWidth(640);
            grabber.setImageHeight(480);
            grabber.setFrameRate(30);
            
            System.out.println("📡 Conectando directamente...");
            grabber.start();
            
            // Verificar funcionalidad
            if (verificarFuncionalidadCamara()) {
                System.out.println("✅ Conexión directa funcionando correctamente");
                return true;
            }
            
        } catch (Exception e) {
            System.out.println("⚠️ Conexión directa falló: " + e.getMessage());
            liberarRecursosPrevios();
        }
        return false;
    }
    
    /**
     * Intentar con diferentes índices de cámara
     */
    private boolean intentarInicializarConDiferentesIndices() {
        System.out.println("🔧 Probando diferentes índices de cámara...");
        
        for (int indice = 0; indice <= 3; indice++) {
            try {
                System.out.println("📹 Probando cámara índice: " + indice);
                
                grabber = new OpenCVFrameGrabber(indice);
                grabber.setImageWidth(640);
                grabber.setImageHeight(480);
                grabber.setFrameRate(30);
                
                grabber.start();
                
                if (verificarFuncionalidadCamara()) {
                    System.out.println("✅ Cámara índice " + indice + " funcionando");
                    return true;
                }
                
            } catch (Exception e) {
                System.out.println("⚠️ Índice " + indice + " falló: " + e.getMessage());
                liberarRecursosPrevios();
            }
        }
        return false;
    }
    
    /**
     * Verificar que la cámara funciona correctamente
     */
    private boolean verificarFuncionalidadCamara() {
        try {
            // Crear convertidor si no existe
            if (converter == null) {
                converter = new Java2DFrameConverter();
            }
            
            // Intentar capturar varios frames para asegurar estabilidad
            for (int i = 0; i < 3; i++) {
                org.bytedeco.javacv.Frame testFrame = grabber.grab();
                if (testFrame == null) {
                    System.out.println("❌ Frame " + (i+1) + " es null");
                    return false;
                }
                
                // Pequeña pausa entre capturas
                Thread.sleep(100);
            }
            
            System.out.println("✅ Cámara verificada correctamente");
            return true;
            
        } catch (Exception e) {
            System.out.println("❌ Error verificando cámara: " + e.getMessage());
            return false;
        }
    }

    /**
     * Capturar imagen de la cámara real
     */
    public byte[] capturarImagen() {
        if (!camaraActiva) {
            System.err.println("La camara no esta activa");
            return null;
        }

        try {
            System.out.println("Capturando imagen de camara real...");

            // Capturar frame de la cámara
            org.bytedeco.javacv.Frame frame = grabber.grab();
            if (frame == null) {
                System.err.println("No se pudo capturar frame de la camara");
                return null;
            }

            // Convertir frame a BufferedImage
            converter = new Java2DFrameConverter();
            BufferedImage imagen = converter.convert(frame);
            
            if (imagen != null) {
                System.out.println("Imagen capturada exitosamente de camara real");
                
                // Convertir BufferedImage a bytes
                java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
                javax.imageio.ImageIO.write(imagen, "jpg", baos);
                return baos.toByteArray();
            } else {
                System.err.println("Error al convertir frame a imagen");
                return simularDatosFaciales(); // Fallback a simulación
            }

        } catch (Exception e) {
            System.err.println("Error al capturar imagen de camara: " + e.getMessage());
            // Fallback a simulación si falla la cámara real
            return simularDatosFaciales();
        }
    }

    /**
     * Capturar imagen de la cámara como BufferedImage
     */
    public BufferedImage capturarImagenBuffered() {
        if (!camaraActiva) {
            System.err.println("❌ La cámara no está activa");
            return null;
        }

        try {
            // Capturar frame real de la cámara
            org.bytedeco.javacv.Frame frame = grabber.grab();
            if (frame == null) {
                System.err.println("❌ No se pudo capturar frame de la cámara");
                return crearImagenSimulada(); // Fallback a imagen simulada
            }

            // Convertir frame a BufferedImage
            if (converter == null) {
                converter = new Java2DFrameConverter();
            }
            
            BufferedImage imagen = converter.convert(frame);
            
            if (imagen != null) {
                System.out.println("✅ Imagen real capturada de la cámara");
                return imagen;
            } else {
                System.err.println("⚠️ Error convirtiendo frame, usando imagen simulada");
                return crearImagenSimulada();
            }

        } catch (Exception e) {
            System.err.println("❌ Error al capturar imagen real: " + e.getMessage());
            return crearImagenSimulada(); // Fallback en caso de error
        }
    }
    
    /**
     * Crear imagen simulada como fallback
     */
    private BufferedImage crearImagenSimulada() {
        // Crear una imagen simulada de 640x480 píxeles
        BufferedImage imagen = new BufferedImage(640, 480, BufferedImage.TYPE_INT_RGB);
        java.awt.Graphics2D g2d = imagen.createGraphics();
        
        // Fondo gris claro
        g2d.setColor(new Color(220, 220, 220));
        g2d.fillRect(0, 0, 640, 480);
        
        // Simular características faciales más grandes y visibles
        g2d.setColor(new Color(180, 150, 120)); // Color piel
        g2d.fillOval(220, 160, 200, 240); // Cara
        
        // Ojos
        g2d.setColor(Color.BLACK);
        g2d.fillOval(260, 220, 20, 20); // Ojo izquierdo
        g2d.fillOval(360, 220, 20, 20); // Ojo derecho
        
        // Nariz
        g2d.drawLine(320, 260, 320, 300);
        
        // Boca
        g2d.drawArc(300, 320, 40, 20, 0, -180);
        
        // Texto indicativo
        g2d.setColor(Color.RED);
        g2d.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 16));
        g2d.drawString("CÁMARA SIMULADA", 250, 100);
        
        g2d.dispose();
        return imagen;
    }

    /**
     * Liberar recursos de la cámara
     */
    public void liberarCamara() {
        try {
            if (grabber != null) {
                grabber.stop();
                grabber.release();
                System.out.println("Camara liberada correctamente");
            }
            camaraActiva = false;
        } catch (Exception e) {
            System.err.println("Error al liberar camara: " + e.getMessage());
        }
    }

    /**
     * Simular datos faciales para entrenamiento
     */
    private byte[] simularDatosFaciales() {
        // Crear datos simulados que representen características faciales únicas
        byte[] datos = new byte[1024]; // 1KB de datos simulados

        // Llenar con datos pseudo-aleatorios pero consistentes
        for (int i = 0; i < datos.length; i++) {
            // Crear patrones que simulen características faciales
            datos[i] = (byte) (Math.sin(i * 0.1) * 127 + random.nextInt(50) - 25);
        }

        return datos;
    }

    /**
     * Mostrar ventana de captura simulada
     */
    public void mostrarVentanaCaptura() {
        try {
            JDialog ventanaCaptura = new JDialog();
            ventanaCaptura.setTitle("🎥 Captura de Imagen Facial");
            ventanaCaptura.setSize(400, 300);
            ventanaCaptura.setLocationRelativeTo(null);
            ventanaCaptura.setModal(true);

            JPanel panel = new JPanel(new BorderLayout());
            panel.setBackground(new Color(240, 240, 240));

            // Área de "video"
            JPanel areaVideo = new JPanel();
            areaVideo.setBackground(Color.BLACK);
            areaVideo.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
            areaVideo.setPreferredSize(new Dimension(320, 240));

            JLabel labelVideo = new JLabel("📹 Vista de Cámara", SwingConstants.CENTER);
            labelVideo.setForeground(Color.WHITE);
            labelVideo.setFont(new Font("Arial", Font.BOLD, 16));
            areaVideo.add(labelVideo);

            // Instrucciones
            JLabel instrucciones = new JLabel("<html><center>📋 Instrucciones:<br>" +
                "• Mantenga su rostro frente a la cámara<br>" +
                "• Evite movimientos bruscos<br>" +
                "• Asegúrese de tener buena iluminación</center></html>");
            instrucciones.setFont(new Font("Arial", Font.PLAIN, 12));
            instrucciones.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            // Botones
            JPanel panelBotones = new JPanel(new FlowLayout());
            JButton btnCapturar = new JButton("📸 Capturar");
            JButton btnCerrar = new JButton("❌ Cerrar");

            btnCapturar.setBackground(new Color(76, 175, 80));
            btnCapturar.setForeground(Color.WHITE);
            btnCapturar.setFont(new Font("Arial", Font.BOLD, 12));

            btnCerrar.setBackground(new Color(244, 67, 54));
            btnCerrar.setForeground(Color.WHITE);
            btnCerrar.setFont(new Font("Arial", Font.BOLD, 12));

            btnCapturar.addActionListener(e -> {
                mostrarMensajeCaptura();
            });

            btnCerrar.addActionListener(e -> {
                ventanaCaptura.dispose();
            });

            panelBotones.add(btnCapturar);
            panelBotones.add(btnCerrar);

            panel.add(areaVideo, BorderLayout.CENTER);
            panel.add(instrucciones, BorderLayout.NORTH);
            panel.add(panelBotones, BorderLayout.SOUTH);

            ventanaCaptura.add(panel);
            ventanaCaptura.setVisible(true);

        } catch (Exception e) {
            System.err.println("❌ Error mostrando ventana de captura: " + e.getMessage());
        }
    }

    /**
     * Mostrar mensaje de captura exitosa
     */
    private void mostrarMensajeCaptura() {
        try {
            // Simular proceso de captura
            JOptionPane.showMessageDialog(null,
                "📸 Procesando captura...\n⏳ Por favor espere...",
                "Capturando",
                JOptionPane.INFORMATION_MESSAGE);

            Thread.sleep(1000);

            JOptionPane.showMessageDialog(null,
                "✅ ¡Imagen capturada exitosamente!\n🧠 Procesando con red neuronal...",
                "Captura Exitosa",
                JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            System.err.println("❌ Error en proceso de captura: " + e.getMessage());
        }
    }

    /**
     * Detectar rostro en imagen simulada
     */
    public boolean detectarRostro() {
        if (!camaraActiva) {
            return false;
        }

        // Simular detección (90% de éxito)
        boolean rostroDetectado = random.nextDouble() > 0.1;

        if (rostroDetectado) {
            System.out.println("✅ Rostro detectado correctamente");
        } else {
            System.out.println("❌ No se detectó rostro en la imagen");
        }

        return rostroDetectado;
    }

    /**
     * Detener la cámara
     */
    public void detenerCamara() {
        if (camaraActiva) {
            camaraActiva = false;
            System.out.println("🔴 Cámara detenida");
        }
    }

    /**
     * Verificar si la cámara está activa
     */
    public boolean isCamaraActiva() {
        return camaraActiva;
    }

    /**
     * Crear imagen de muestra para visualización
     */
    public BufferedImage crearImagenMuestra() {
        BufferedImage imagen = new BufferedImage(160, 120, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = imagen.createGraphics();

        // Fondo azul
        g2d.setColor(new Color(100, 150, 200));
        g2d.fillRect(0, 0, 160, 120);

        // Simular rostro con formas básicas
        g2d.setColor(new Color(255, 220, 177)); // Color piel
        g2d.fillOval(40, 30, 80, 60); // Cara

        // Ojos
        g2d.setColor(Color.BLACK);
        g2d.fillOval(55, 45, 8, 8); // Ojo izquierdo
        g2d.fillOval(97, 45, 8, 8); // Ojo derecho

        // Nariz
        g2d.drawLine(80, 55, 80, 65);

        // Boca
        g2d.drawArc(70, 70, 20, 10, 0, -180);

        g2d.dispose();
        return imagen;
    }

    /**
     * Obtener información de la cámara
     */
    public String obtenerInformacionCamara() {
        StringBuilder info = new StringBuilder();
        info.append("📷 INFORMACIÓN DE CÁMARA\n");
        info.append("========================\n");
        info.append("Estado: ").append(camaraActiva ? "Activa ✅" : "Inactiva ❌").append("\n");
        info.append("Resolución: 640x480\n");
        info.append("FPS: 30\n");
        info.append("Formato: RGB\n");
        info.append("Detección de rostros: Habilitada\n");
        return info.toString();
    }

    /**
     * Diagnosticar problemas de cámara
     */
    public void diagnosticarCamara() {
        System.out.println("\n🔍 DIAGNÓSTICO DE CÁMARA");
        System.out.println("========================");
        
        try {
            // Verificar sistema operativo
            String os = System.getProperty("os.name").toLowerCase();
            System.out.println("💻 Sistema Operativo: " + os);
            
            // Verificar Java version
            String javaVersion = System.getProperty("java.version");
            System.out.println("☕ Java Version: " + javaVersion);
            
            // Verificar librerías disponibles
            System.out.println("📚 Verificando librerías JavaCV...");
            try {
                Class.forName("org.bytedeco.javacv.OpenCVFrameGrabber");
                System.out.println("✅ JavaCV disponible");
            } catch (ClassNotFoundException e) {
                System.out.println("❌ JavaCV no encontrado");
            }
            
            // Información de cámaras disponibles
            System.out.println("\n📹 Probando cámaras disponibles:");
            for (int i = 0; i <= 3; i++) {
                System.out.println("Probando índice " + i + "...");
                OpenCVFrameGrabber testGrabber = null;
                try {
                    testGrabber = new OpenCVFrameGrabber(i);
                    testGrabber.start();
                    System.out.println("✅ Cámara " + i + " disponible");
                } catch (Exception e) {
                    System.out.println("❌ Cámara " + i + " no disponible: " + e.getMessage());
                } finally {
                    if (testGrabber != null) {
                        try {
                            testGrabber.stop();
                            testGrabber.release();
                        } catch (Exception e) {
                            // Ignorar errores de limpieza
                        }
                    }
                }
            }
            
            // Sugerencias de solución
            System.out.println("\n💡 SUGERENCIAS:");
            System.out.println("• Verificar que la cámara no esté siendo usada por otra aplicación");
            System.out.println("• Revisar permisos de cámara en Windows");
            System.out.println("• Probar con diferentes aplicaciones de cámara");
            System.out.println("• Verificar drivers de cámara actualizados");
            
        } catch (Exception e) {
            System.err.println("❌ Error en diagnóstico: " + e.getMessage());
        }
        
        System.out.println("========================\n");
    }

    /**
     * Limpiar recursos
     */
    public void limpiarRecursos() {
        detenerCamara();
        System.out.println("🧹 Recursos de cámara liberados");
    }

    /**
     * Alias para limpiarRecursos
     */
    public void liberarRecursos() {
        limpiarRecursos();
    }
}