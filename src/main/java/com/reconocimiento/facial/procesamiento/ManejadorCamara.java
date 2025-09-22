package com.reconocimiento.facial.procesamiento;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * Manejador simplificado de cámara para captura de imágenes faciales
 * Simula la captura de cámara con funcionalidad básica
 */
public class ManejadorCamara {

    private boolean camaraActiva = false;
    private Random random = new Random();

    /**
     * Inicializar la cámara
     */
    public boolean inicializarCamara() {
        try {
            System.out.println("🎥 Inicializando cámara...");

            // Simular tiempo de inicialización
            Thread.sleep(1000);

            camaraActiva = true;
            System.out.println("✅ Cámara inicializada correctamente");
            return true;

        } catch (Exception e) {
            System.err.println("❌ Error al inicializar cámara: " + e.getMessage());
            return false;
        }
    }

    /**
     * Capturar imagen de la cámara
     */
    public byte[] capturarImagen() {
        if (!camaraActiva) {
            System.err.println("❌ La cámara no está activa");
            return null;
        }

        try {
            System.out.println("📸 Capturando imagen...");

            // Simular tiempo de captura
            Thread.sleep(500);

            // Simular datos de imagen facial
            byte[] imagenSimulada = simularDatosFaciales();

            System.out.println("✅ Imagen capturada exitosamente");
            return imagenSimulada;

        } catch (Exception e) {
            System.err.println("❌ Error al capturar imagen: " + e.getMessage());
            return null;
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
            System.out.println("📸 Capturando imagen...");

            // Simular tiempo de captura
            Thread.sleep(500);

            // Crear una imagen simulada de 64x64 píxeles (escala de grises)
            BufferedImage imagen = new BufferedImage(64, 64, BufferedImage.TYPE_BYTE_GRAY);
            java.awt.Graphics2D g2d = imagen.createGraphics();
            
            // Simular un patrón facial básico
            g2d.setColor(java.awt.Color.LIGHT_GRAY);
            g2d.fillRect(0, 0, 64, 64);
            
            // Simular características faciales básicas
            g2d.setColor(java.awt.Color.DARK_GRAY);
            g2d.fillOval(15, 20, 8, 8);  // Ojo izquierdo
            g2d.fillOval(41, 20, 8, 8);  // Ojo derecho
            g2d.fillOval(30, 35, 4, 6);  // Nariz
            g2d.drawArc(25, 45, 14, 8, 0, -180); // Boca
            
            g2d.dispose();

            System.out.println("✅ Imagen BufferedImage capturada exitosamente");
            return imagen;

        } catch (Exception e) {
            System.err.println("❌ Error al capturar imagen: " + e.getMessage());
            return null;
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