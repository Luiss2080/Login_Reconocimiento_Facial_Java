package com.reconocimiento.facial.utilidades;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.util.Base64;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 🖼️ UTILIDADES PARA MANEJO DE IMÁGENES
 * Herramientas para procesamiento, conversión y gestión de imágenes
 * Optimizado para el sistema de reconocimiento facial
 */
public class ManejadorImagenes {

    // Configuraciones por defecto
    private static final int DIMENSION_PREDETERMINADA = 64;
    private static final String FORMATO_PREDETERMINADO = "jpg";
    // Calidad JPEG removida por no uso - se puede restaurar si se necesita
    // private static final float CALIDAD_JPEG = 0.85f;

    /**
     * Redimensiona una imagen manteniendo la proporción
     * @param imagen Imagen original
     * @param ancho Ancho deseado
     * @param alto Alto deseado
     * @return Imagen redimensionada
     */
    public static BufferedImage redimensionarImagen(BufferedImage imagen, int ancho, int alto) {
        if (imagen == null) {
            throw new IllegalArgumentException("La imagen no puede ser null");
        }

        // Crear nueva imagen con las dimensiones especificadas
        BufferedImage imagenRedimensionada = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = imagenRedimensionada.createGraphics();
        
        // Configurar calidad de renderizado
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Dibujar imagen redimensionada
        g2d.drawImage(imagen, 0, 0, ancho, alto, null);
        g2d.dispose();
        
        return imagenRedimensionada;
    }

    /**
     * Redimensiona imagen a dimensiones cuadradas
     * @param imagen Imagen original
     * @param dimension Dimensión cuadrada (ancho = alto)
     * @return Imagen redimensionada
     */
    public static BufferedImage redimensionarCuadrada(BufferedImage imagen, int dimension) {
        return redimensionarImagen(imagen, dimension, dimension);
    }

    /**
     * Redimensiona imagen usando la dimensión predeterminada (64x64)
     * @param imagen Imagen original
     * @return Imagen redimensionada 64x64
     */
    public static BufferedImage redimensionarPredeterminada(BufferedImage imagen) {
        return redimensionarCuadrada(imagen, DIMENSION_PREDETERMINADA);
    }

    /**
     * Convierte imagen a escala de grises
     * @param imagen Imagen original en color
     * @return Imagen en escala de grises
     */
    public static BufferedImage convertirEscalaGrises(BufferedImage imagen) {
        if (imagen == null) {
            throw new IllegalArgumentException("La imagen no puede ser null");
        }

        BufferedImage imagenGris = new BufferedImage(
            imagen.getWidth(), 
            imagen.getHeight(), 
            BufferedImage.TYPE_BYTE_GRAY
        );
        
        Graphics2D g2d = imagenGris.createGraphics();
        g2d.drawImage(imagen, 0, 0, null);
        g2d.dispose();
        
        return imagenGris;
    }

    /**
     * Guarda imagen en el sistema de archivos
     * @param imagen Imagen a guardar
     * @param rutaArchivo Ruta donde guardar
     * @param formato Formato de imagen (jpg, png, etc.)
     * @throws IOException Si hay error al escribir archivo
     */
    public static void guardarImagen(BufferedImage imagen, String rutaArchivo, String formato) throws IOException {
        if (imagen == null) {
            throw new IllegalArgumentException("La imagen no puede ser null");
        }
        if (rutaArchivo == null || rutaArchivo.trim().isEmpty()) {
            throw new IllegalArgumentException("La ruta del archivo no puede estar vacía");
        }

        File archivo = new File(rutaArchivo);
        
        // Crear directorios padre si no existen
        File directorioPadre = archivo.getParentFile();
        if (directorioPadre != null && !directorioPadre.exists()) {
            directorioPadre.mkdirs();
        }
        
        ImageIO.write(imagen, formato != null ? formato : FORMATO_PREDETERMINADO, archivo);
    }

    /**
     * Carga imagen desde archivo
     * @param rutaArchivo Ruta del archivo de imagen
     * @return Imagen cargada
     * @throws IOException Si hay error al leer archivo
     */
    public static BufferedImage cargarImagen(String rutaArchivo) throws IOException {
        if (rutaArchivo == null || rutaArchivo.trim().isEmpty()) {
            throw new IllegalArgumentException("La ruta del archivo no puede estar vacía");
        }

        File archivo = new File(rutaArchivo);
        if (!archivo.exists()) {
            throw new IOException("El archivo no existe: " + rutaArchivo);
        }

        return ImageIO.read(archivo);
    }

    /**
     * Convierte imagen a Base64
     * @param imagen Imagen a convertir
     * @param formato Formato de imagen
     * @return String en Base64
     * @throws IOException Si hay error en la conversión
     */
    public static String imagenABase64(BufferedImage imagen, String formato) throws IOException {
        if (imagen == null) {
            throw new IllegalArgumentException("La imagen no puede ser null");
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(imagen, formato != null ? formato : FORMATO_PREDETERMINADO, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.getEncoder().encodeToString(imageBytes);
    }

    /**
     * Convierte Base64 a imagen
     * @param base64 String en Base64
     * @return Imagen convertida
     * @throws IOException Si hay error en la conversión
     */
    public static BufferedImage base64AImagen(String base64) throws IOException {
        if (base64 == null || base64.trim().isEmpty()) {
            throw new IllegalArgumentException("El string Base64 no puede estar vacío");
        }

        byte[] imageBytes = Base64.getDecoder().decode(base64);
        ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes);
        return ImageIO.read(bais);
    }

    /**
     * Calcula checksum MD5 de una imagen
     * @param imagen Imagen para calcular checksum
     * @return Checksum MD5 en hexadecimal
     * @throws IOException Si hay error al procesar imagen
     */
    public static String calcularChecksumImagen(BufferedImage imagen) throws IOException {
        if (imagen == null) {
            throw new IllegalArgumentException("La imagen no puede ser null");
        }

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(imagen, "png", baos);
            byte[] imageBytes = baos.toByteArray();
            
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(imageBytes);
            
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Algoritmo MD5 no disponible", e);
        }
    }

    /**
     * Extrae array de píxeles de una imagen en escala de grises
     * @param imagen Imagen en escala de grises
     * @return Array de valores de píxeles (0-255)
     */
    public static int[] extraerPixeles(BufferedImage imagen) {
        if (imagen == null) {
            throw new IllegalArgumentException("La imagen no puede ser null");
        }

        int ancho = imagen.getWidth();
        int alto = imagen.getHeight();
        int[] pixeles = new int[ancho * alto];
        
        for (int y = 0; y < alto; y++) {
            for (int x = 0; x < ancho; x++) {
                int rgb = imagen.getRGB(x, y);
                // Extraer componente rojo (en escala de grises, R=G=B)
                int valor = (rgb >> 16) & 0xFF;
                pixeles[y * ancho + x] = valor;
            }
        }
        
        return pixeles;
    }

    /**
     * Normaliza array de píxeles a rango 0.0 - 1.0
     * @param pixeles Array de píxeles (0-255)
     * @return Array normalizado (0.0-1.0)
     */
    public static double[] normalizarPixeles(int[] pixeles) {
        if (pixeles == null) {
            throw new IllegalArgumentException("El array de píxeles no puede ser null");
        }

        double[] pixelesNormalizados = new double[pixeles.length];
        for (int i = 0; i < pixeles.length; i++) {
            pixelesNormalizados[i] = pixeles[i] / 255.0;
        }
        
        return pixelesNormalizados;
    }

    /**
     * Valida formato de imagen
     * @param formato Formato a validar
     * @return true si el formato es válido
     */
    public static boolean esFormatoValido(String formato) {
        if (formato == null) {
            return false;
        }
        
        String[] formatosValidos = {"jpg", "jpeg", "png", "bmp", "gif"};
        String formatoLower = formato.toLowerCase();
        
        for (String formatoValido : formatosValidos) {
            if (formatoValido.equals(formatoLower)) {
                return true;
            }
        }
        
        return false;
    }

    /**
     * Calcula calidad de imagen basada en resolución y nitidez
     * @param imagen Imagen a evaluar
     * @return Valor de calidad entre 0.0 y 1.0
     */
    public static double calcularCalidadImagen(BufferedImage imagen) {
        if (imagen == null) {
            return 0.0;
        }

        int ancho = imagen.getWidth();
        int alto = imagen.getHeight();
        
        // Factor de resolución (imágenes más grandes = mejor calidad base)
        double factorResolucion = Math.min(1.0, (ancho * alto) / (320.0 * 240.0));
        
        // Factor de proporción (imágenes cuadradas o cercanas son mejores para reconocimiento facial)
        double proporcion = (double) Math.min(ancho, alto) / Math.max(ancho, alto);
        double factorProporcion = proporcion * 0.3 + 0.7; // Bonus por buena proporción
        
        return factorResolucion * factorProporcion;
    }

    /**
     * Información de imagen
     */
    public static class InfoImagen {
        public final int ancho;
        public final int alto;
        public final int totalPixeles;
        public final double calidadEstimada;
        public final String checksum;

        public InfoImagen(int ancho, int alto, int totalPixeles, double calidadEstimada, String checksum) {
            this.ancho = ancho;
            this.alto = alto;
            this.totalPixeles = totalPixeles;
            this.calidadEstimada = calidadEstimada;
            this.checksum = checksum;
        }
    }

    /**
     * Obtiene información completa de una imagen
     * @param imagen Imagen a analizar
     * @return Información de la imagen
     * @throws IOException Si hay error al procesar
     */
    public static InfoImagen obtenerInfoImagen(BufferedImage imagen) throws IOException {
        if (imagen == null) {
            throw new IllegalArgumentException("La imagen no puede ser null");
        }

        int ancho = imagen.getWidth();
        int alto = imagen.getHeight();
        int totalPixeles = ancho * alto;
        double calidad = calcularCalidadImagen(imagen);
        String checksum = calcularChecksumImagen(imagen);

        return new InfoImagen(ancho, alto, totalPixeles, calidad, checksum);
    }
}