package com.reconocimiento.facial.utilidades;

import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.javacv.*;
import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgproc.*;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import javax.imageio.ImageIO;
import java.util.Base64;

/**
 * 🛠️ UTILIDADES OPENCV
 * Conjunto de herramientas para facilitar el trabajo con OpenCV
 * Incluye conversiones, validaciones y operaciones comunes
 */
public class UtilidadesOpenCV {

    // ========== CONVERTIDORES REUTILIZABLES ==========
    private static final Java2DFrameConverter java2DConverter = new Java2DFrameConverter();
    private static final OpenCVFrameConverter.ToMat matConverter = new OpenCVFrameConverter.ToMat();

    /**
     * 🔄 CONVERSIONES DE IMAGEN
     */

    /**
     * Convertir BufferedImage a Mat de OpenCV
     */
    public static Mat bufferedImageToMat(BufferedImage imagen) {
        if (imagen == null) {
            return null;
        }
        
        try {
            org.bytedeco.javacv.Frame frame = java2DConverter.convert(imagen);
            return matConverter.convert(frame);
        } catch (Exception e) {
            System.err.println("❌ Error convirtiendo BufferedImage a Mat: " + e.getMessage());
            return null;
        }
    }

    /**
     * Convertir Mat de OpenCV a BufferedImage
     */
    public static BufferedImage matToBufferedImage(Mat mat) {
        if (mat == null || mat.empty()) {
            return null;
        }
        
        try {
            org.bytedeco.javacv.Frame frame = matConverter.convert(mat);
            return java2DConverter.convert(frame);
        } catch (Exception e) {
            System.err.println("❌ Error convirtiendo Mat a BufferedImage: " + e.getMessage());
            return null;
        }
    }

    /**
     * 🎨 PROCESAMIENTO DE IMAGEN
     */

    /**
     * Redimensionar imagen manteniendo proporción
     */
    public static BufferedImage redimensionarImagen(BufferedImage imagenOriginal, int anchoDeseado, int altoDeseado) {
        if (imagenOriginal == null) {
            return null;
        }

        try {
            Mat matOriginal = bufferedImageToMat(imagenOriginal);
            if (matOriginal == null) {
                return null;
            }

            Mat matRedimensionado = new Mat();
            resize(matOriginal, matRedimensionado, new Size(anchoDeseado, altoDeseado));
            
            BufferedImage resultado = matToBufferedImage(matRedimensionado);
            
            // Limpiar recursos
            matOriginal.close();
            matRedimensionado.close();
            
            return resultado;
            
        } catch (Exception e) {
            System.err.println("❌ Error redimensionando imagen: " + e.getMessage());
            return null;
        }
    }

    /**
     * Convertir imagen a escala de grises
     */
    public static BufferedImage convertirAGris(BufferedImage imagenColor) {
        if (imagenColor == null) {
            return null;
        }

        try {
            Mat matColor = bufferedImageToMat(imagenColor);
            if (matColor == null) {
                return null;
            }

            Mat matGris;
            
            // Verificar si ya está en escala de grises
            if (matColor.channels() == 1) {
                matGris = matColor.clone();
            } else {
                matGris = new Mat();
                cvtColor(matColor, matGris, COLOR_BGR2GRAY);
            }
            
            BufferedImage resultado = matToBufferedImage(matGris);
            
            // Limpiar recursos
            matColor.close();
            matGris.close();
            
            return resultado;
            
        } catch (Exception e) {
            System.err.println("❌ Error convirtiendo a escala de grises: " + e.getMessage());
            return null;
        }
    }

    /**
     * Mejorar contraste de imagen usando ecualización de histograma
     */
    public static BufferedImage mejorarContraste(BufferedImage imagen) {
        if (imagen == null) {
            return null;
        }

        try {
            // Convertir a escala de grises primero
            BufferedImage imagenGris = convertirAGris(imagen);
            if (imagenGris == null) {
                return null;
            }
            
            Mat matGris = bufferedImageToMat(imagenGris);
            if (matGris == null) {
                return null;
            }

            Mat matMejorado = new Mat();
            equalizeHist(matGris, matMejorado);
            
            BufferedImage resultado = matToBufferedImage(matMejorado);
            
            // Limpiar recursos
            matGris.close();
            matMejorado.close();
            
            return resultado;
            
        } catch (Exception e) {
            System.err.println("❌ Error mejorando contraste: " + e.getMessage());
            return null;
        }
    }

    /**
     * Aplicar filtro de suavizado Gaussiano
     */
    public static BufferedImage aplicarFiltroGaussiano(BufferedImage imagen, int kernelSize) {
        if (imagen == null || kernelSize <= 0) {
            return null;
        }

        try {
            Mat matOriginal = bufferedImageToMat(imagen);
            if (matOriginal == null) {
                return null;
            }

            Mat matSuavizado = new Mat();
            
            // Asegurar que el kernel size sea impar
            if (kernelSize % 2 == 0) {
                kernelSize++;
            }
            
            GaussianBlur(matOriginal, matSuavizado, new Size(kernelSize, kernelSize), 0);
            
            BufferedImage resultado = matToBufferedImage(matSuavizado);
            
            // Limpiar recursos
            matOriginal.close();
            matSuavizado.close();
            
            return resultado;
            
        } catch (Exception e) {
            System.err.println("❌ Error aplicando filtro Gaussiano: " + e.getMessage());
            return null;
        }
    }

    /**
     * 📏 UTILIDADES DE INFORMACIÓN
     */

    /**
     * Obtener información detallada de una imagen
     */
    public static InformacionImagen obtenerInformacionImagen(BufferedImage imagen) {
        if (imagen == null) {
            return new InformacionImagen(0, 0, 0, "Imagen nula", false);
        }

        try {
            Mat mat = bufferedImageToMat(imagen);
            if (mat == null) {
                return new InformacionImagen(0, 0, 0, "Error convirtiendo imagen", false);
            }

            int ancho = mat.cols();
            int alto = mat.rows();
            int canales = mat.channels();
            
            String tipoImagen;
            switch (canales) {
                case 1: tipoImagen = "Escala de grises"; break;
                case 3: tipoImagen = "Color (BGR)"; break;
                case 4: tipoImagen = "Color con transparencia (BGRA)"; break;
                default: tipoImagen = "Tipo desconocido (" + canales + " canales)"; break;
            }
            
            mat.close();
            
            return new InformacionImagen(ancho, alto, canales, tipoImagen, true);
            
        } catch (Exception e) {
            return new InformacionImagen(0, 0, 0, "Error: " + e.getMessage(), false);
        }
    }

    /**
     * 💾 UTILIDADES DE CODIFICACIÓN
     */

    /**
     * Convertir BufferedImage a Base64
     */
    public static String imagenToBase64(BufferedImage imagen, String formato) {
        if (imagen == null) {
            return null;
        }

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(imagen, formato, baos);
            byte[] imageBytes = baos.toByteArray();
            return Base64.getEncoder().encodeToString(imageBytes);
            
        } catch (Exception e) {
            System.err.println("❌ Error convirtiendo imagen a Base64: " + e.getMessage());
            return null;
        }
    }

    /**
     * Convertir Base64 a BufferedImage
     */
    public static BufferedImage base64ToImagen(String base64String) {
        if (base64String == null || base64String.isEmpty()) {
            return null;
        }

        try {
            byte[] imageBytes = Base64.getDecoder().decode(base64String);
            ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes);
            return ImageIO.read(bais);
            
        } catch (Exception e) {
            System.err.println("❌ Error convirtiendo Base64 a imagen: " + e.getMessage());
            return null;
        }
    }

    /**
     * ✅ VALIDACIONES
     */

    /**
     * Validar que una imagen es apta para procesamiento
     */
    public static boolean esImagenValida(BufferedImage imagen) {
        if (imagen == null) {
            return false;
        }

        // Verificar dimensiones mínimas
        if (imagen.getWidth() < 10 || imagen.getHeight() < 10) {
            return false;
        }

        // Verificar dimensiones máximas razonables
        if (imagen.getWidth() > 4000 || imagen.getHeight() > 4000) {
            System.out.println("⚠️ Imagen muy grande: " + imagen.getWidth() + "x" + imagen.getHeight());
        }

        return true;
    }

    /**
     * Validar que OpenCV puede procesar la imagen
     */
    public static boolean puedeSerProcesadaPorOpenCV(BufferedImage imagen) {
        if (!esImagenValida(imagen)) {
            return false;
        }

        try {
            Mat mat = bufferedImageToMat(imagen);
            if (mat == null || mat.empty()) {
                return false;
            }
            
            mat.close();
            return true;
            
        } catch (Exception e) {
            System.err.println("❌ Error validando compatibilidad con OpenCV: " + e.getMessage());
            return false;
        }
    }

    /**
     * 📊 CLASE INFORMACIÓN DE IMAGEN
     */
    public static class InformacionImagen {
        private final int ancho;
        private final int alto;
        private final int canales;
        private final String descripcion;
        private final boolean valida;

        public InformacionImagen(int ancho, int alto, int canales, String descripcion, boolean valida) {
            this.ancho = ancho;
            this.alto = alto;
            this.canales = canales;
            this.descripcion = descripcion;
            this.valida = valida;
        }

        public int getAncho() { return ancho; }
        public int getAlto() { return alto; }
        public int getCanales() { return canales; }
        public String getDescripcion() { return descripcion; }
        public boolean isValida() { return valida; }
        public long getTamanoPixeles() { return (long) ancho * alto; }

        @Override
        public String toString() {
            return String.format("Imagen{%dx%d, %s, válida=%s}",
                               ancho, alto, descripcion, valida);
        }
    }
}