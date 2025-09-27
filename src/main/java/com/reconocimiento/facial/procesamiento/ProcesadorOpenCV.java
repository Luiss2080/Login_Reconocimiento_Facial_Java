package com.reconocimiento.facial.procesamiento;

import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_objdetect.*;
import org.bytedeco.opencv.opencv_face.*;
import org.bytedeco.javacv.*;
import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgproc.*;

import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * 🎯 PROCESADOR AVANZADO CON OPENCV
 * Implementación completa de OpenCV para reconocimiento facial avanzado
 * Incluye detección, preprocesamiento, extracción de características y reconocimiento
 */
public class ProcesadorOpenCV {

    // ========== COMPONENTES OPENCV ==========
    private CascadeClassifier clasificadorRostros;
    private CascadeClassifier clasificadorOjos;
    private FaceRecognizer reconocedorLBPH;
    private FaceRecognizer reconocedorEigen;
    private FaceRecognizer reconocedorFisher;
    
    // ========== CONVERTIDORES ==========
    private Java2DFrameConverter converterJava2D;
    private OpenCVFrameConverter.ToMat converterMat;
    
    // ========== CONFIGURACIÓN ==========
    private static final String RUTA_HAARCASCADES = "src/main/resources/haarcascade/";
    private static final String ARCHIVO_ROSTROS = "haarcascade_frontalface_alt.xml";
    private static final String ARCHIVO_OJOS = "haarcascade_eye.xml";
    
    // Parámetros de detección optimizados
    private static final double FACTOR_ESCALA = 1.1;
    private static final int MINIMOS_VECINOS = 3;
    private static final Size TAMANO_MINIMO = new Size(30, 30);
    private static final Size TAMANO_MAXIMO = new Size(300, 300);
    
    // Parámetros de reconocimiento
    private static final int ANCHO_IMAGEN = 100;
    private static final int ALTO_IMAGEN = 100;
    private static final double UMBRAL_CONFIANZA = 80.0;
    
    // ========== ESTADO ==========
    private boolean inicializado = false;
    private String ultimoError = "";
    private List<Mat> imageneEntrenamiento;
    private Mat etiquetasEntrenamiento;

    /**
     * Constructor - Inicializa todos los componentes de OpenCV
     */
    public ProcesadorOpenCV() {
        try {
            inicializarComponentes();
            cargarClasificadores();
            inicializarReconocedores();
            
            this.imageneEntrenamiento = new ArrayList<>();
            this.etiquetasEntrenamiento = new Mat();
            
            inicializado = true;
            System.out.println("✅ ProcesadorOpenCV inicializado correctamente");
            
        } catch (Exception e) {
            ultimoError = "Error inicializando ProcesadorOpenCV: " + e.getMessage();
            System.err.println("❌ " + ultimoError);
            e.printStackTrace();
        }
    }

    /**
     * Inicializar componentes básicos
     */
    private void inicializarComponentes() {
        converterJava2D = new Java2DFrameConverter();
        converterMat = new OpenCVFrameConverter.ToMat();
        
        System.out.println("🔧 Convertidores inicializados");
    }

    /**
     * Cargar clasificadores Haar Cascade
     */
    private void cargarClasificadores() throws Exception {
        // Cargar clasificador de rostros
        String rutaRostros = obtenerRutaClasificador(ARCHIVO_ROSTROS);
        clasificadorRostros = new CascadeClassifier(rutaRostros);
        
        if (clasificadorRostros.empty()) {
            throw new Exception("No se pudo cargar el clasificador de rostros: " + rutaRostros);
        }
        
        // Cargar clasificador de ojos
        String rutaOjos = obtenerRutaClasificador(ARCHIVO_OJOS);
        clasificadorOjos = new CascadeClassifier(rutaOjos);
        
        if (clasificadorOjos.empty()) {
            System.out.println("⚠️ Clasificador de ojos no disponible: " + rutaOjos);
        }
        
        System.out.println("✅ Clasificadores cargados correctamente");
    }

    /**
     * Inicializar reconocedores faciales
     */
    private void inicializarReconocedores() {
        try {
            // LBPH (Local Binary Patterns Histograms) - Más robusto
            reconocedorLBPH = LBPHFaceRecognizer.create();
            
            // EigenFaces - Rápido y eficiente
            reconocedorEigen = EigenFaceRecognizer.create();
            
            // FisherFaces - Mejor discriminación
            reconocedorFisher = FisherFaceRecognizer.create();
            
            System.out.println("✅ Reconocedores faciales inicializados");
            
        } catch (Exception e) {
            System.err.println("⚠️ Error inicializando reconocedores: " + e.getMessage());
        }
    }

    /**
     * Obtener ruta completa del clasificador
     */
    private String obtenerRutaClasificador(String archivo) {
        // Primero buscar en resources
        Path rutaResources = Paths.get(RUTA_HAARCASCADES + archivo);
        if (rutaResources.toFile().exists()) {
            return rutaResources.toString();
        }
        
        // Buscar en target/classes
        Path rutaTarget = Paths.get("target/classes/haarcascade/" + archivo);
        if (rutaTarget.toFile().exists()) {
            return rutaTarget.toString();
        }
        
        // Usar ruta por defecto
        return RUTA_HAARCASCADES + archivo;
    }

    /**
     * 🔍 DETECTAR ROSTROS EN IMAGEN
     * Detecta rostros usando Haar Cascades
     */
    public List<Rect> detectarRostros(BufferedImage imagen) {
        if (!inicializado || imagen == null) {
            return new ArrayList<>();
        }

        try {
            // Convertir BufferedImage a Mat
            Mat matImagen = bufferedImageToMat(imagen);
            
            // Convertir a escala de grises
            Mat imagenGris = new Mat();
            cvtColor(matImagen, imagenGris, COLOR_BGR2GRAY);
            
            // Ecualizar histograma para mejor detección
            equalizeHist(imagenGris, imagenGris);
            
            // Detectar rostros
            RectVector rostrosDetectados = new RectVector();
            clasificadorRostros.detectMultiScale(
                imagenGris, 
                rostrosDetectados,
                FACTOR_ESCALA,
                MINIMOS_VECINOS,
                0,
                TAMANO_MINIMO,
                TAMANO_MAXIMO
            );
            
            // Convertir a lista Java
            List<Rect> rostros = new ArrayList<>();
            for (int i = 0; i < rostrosDetectados.size(); i++) {
                rostros.add(new Rect(rostrosDetectados.get(i)));
            }
            
            System.out.println("🔍 Detectados " + rostros.size() + " rostros");
            return rostros;
            
        } catch (Exception e) {
            System.err.println("❌ Error detectando rostros: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * 🎨 PREPROCESAR IMAGEN FACIAL
     * Normaliza y mejora la imagen para reconocimiento
     */
    public Mat preprocesarImagenFacial(BufferedImage imagen, Rect rostro) {
        if (!inicializado || imagen == null || rostro == null) {
            return null;
        }

        try {
            // Convertir a Mat
            Mat matImagen = bufferedImageToMat(imagen);
            
            // Extraer región del rostro
            Mat rostroMat = new Mat(matImagen, rostro);
            
            // Convertir a escala de grises
            Mat rostroGris = new Mat();
            cvtColor(rostroMat, rostroGris, COLOR_BGR2GRAY);
            
            // Redimensionar a tamaño estándar
            Mat rostroRedimensionado = new Mat();
            resize(rostroGris, rostroRedimensionado, new Size(ANCHO_IMAGEN, ALTO_IMAGEN));
            
            // Ecualizar histograma
            Mat rostroEcualizado = new Mat();
            equalizeHist(rostroRedimensionado, rostroEcualizado);
            
            // Aplicar filtro Gaussiano para suavizar
            Mat rostroSuavizado = new Mat();
            GaussianBlur(rostroEcualizado, rostroSuavizado, new Size(3, 3), 0);
            
            System.out.println("🎨 Imagen facial preprocesada correctamente");
            return rostroSuavizado;
            
        } catch (Exception e) {
            System.err.println("❌ Error preprocesando imagen: " + e.getMessage());
            return null;
        }
    }

    /**
     * 📚 ENTRENAR RECONOCEDOR
     * Entrena el modelo con imágenes de rostros y etiquetas
     */
    public boolean entrenarReconocedor(List<BufferedImage> imagenes, List<Integer> etiquetas) {
        if (!inicializado || imagenes == null || etiquetas == null || imagenes.size() != etiquetas.size()) {
            return false;
        }

        try {
            imageneEntrenamiento.clear();
            
            // Procesar cada imagen
            for (int i = 0; i < imagenes.size(); i++) {
                BufferedImage imagen = imagenes.get(i);
                
                // Detectar rostro
                List<Rect> rostros = detectarRostros(imagen);
                if (rostros.isEmpty()) {
                    System.out.println("⚠️ No se detectó rostro en imagen " + i);
                    continue;
                }
                
                // Usar el rostro más grande
                Rect rostroMasGrande = obtenerRostroMasGrande(rostros);
                
                // Preprocesar
                Mat rostroProcessed = preprocesarImagenFacial(imagen, rostroMasGrande);
                if (rostroProcessed != null) {
                    imageneEntrenamiento.add(rostroProcessed);
                }
            }
            
            if (imageneEntrenamiento.isEmpty()) {
                System.err.println("❌ No hay imágenes válidas para entrenamiento");
                return false;
            }
            
            // Crear matriz de etiquetas
            etiquetasEntrenamiento = new Mat(etiquetas.size(), 1, CV_32SC1);
            for (int i = 0; i < etiquetas.size(); i++) {
                etiquetasEntrenamiento.ptr(i, 0).putInt(etiquetas.get(i));
            }
            
            // Entrenar reconocedores
            entrenarTodosLosReconocedores();
            
            System.out.println("✅ Reconocedor entrenado con " + imageneEntrenamiento.size() + " imágenes");
            return true;
            
        } catch (Exception e) {
            System.err.println("❌ Error entrenando reconocedor: " + e.getMessage());
            return false;
        }
    }

    /**
     * Entrenar todos los reconocedores disponibles
     */
    private void entrenarTodosLosReconocedores() {
        // Convertir lista a MatVector
        MatVector imagenesVector = new MatVector(imageneEntrenamiento.size());
        for (int i = 0; i < imageneEntrenamiento.size(); i++) {
            imagenesVector.put(i, imageneEntrenamiento.get(i));
        }
        
        try {
            if (reconocedorLBPH != null) {
                reconocedorLBPH.train(imagenesVector, etiquetasEntrenamiento);
                System.out.println("✅ LBPH entrenado");
            }
        } catch (Exception e) {
            System.err.println("⚠️ Error entrenando LBPH: " + e.getMessage());
        }
        
        try {
            if (reconocedorEigen != null && imageneEntrenamiento.size() > 1) {
                reconocedorEigen.train(imagenesVector, etiquetasEntrenamiento);
                System.out.println("✅ EigenFaces entrenado");
            }
        } catch (Exception e) {
            System.err.println("⚠️ Error entrenando EigenFaces: " + e.getMessage());
        }
        
        try {
            if (reconocedorFisher != null && imageneEntrenamiento.size() > 1) {
                reconocedorFisher.train(imagenesVector, etiquetasEntrenamiento);
                System.out.println("✅ FisherFaces entrenado");
            }
        } catch (Exception e) {
            System.err.println("⚠️ Error entrenando FisherFaces: " + e.getMessage());
        }
    }

    /**
     * 🎯 RECONOCER ROSTRO
     * Identifica un rostro usando el modelo entrenado
     */
    public ResultadoReconocimiento reconocerRostro(BufferedImage imagen) {
        if (!inicializado || imagen == null || imageneEntrenamiento.isEmpty()) {
            return new ResultadoReconocimiento(-1, 0.0, false);
        }

        try {
            // Detectar rostros
            List<Rect> rostros = detectarRostros(imagen);
            if (rostros.isEmpty()) {
                return new ResultadoReconocimiento(-1, 0.0, false);
            }
            
            // Usar el rostro más grande
            Rect rostroMasGrande = obtenerRostroMasGrande(rostros);
            
            // Preprocesar
            Mat rostroProcessed = preprocesarImagenFacial(imagen, rostroMasGrande);
            if (rostroProcessed == null) {
                return new ResultadoReconocimiento(-1, 0.0, false);
            }
            
            // Reconocer con LBPH (más confiable)
            int[] etiqueta = new int[1];
            double[] confianza = new double[1];
            
            if (reconocedorLBPH != null) {
                reconocedorLBPH.predict(rostroProcessed, etiqueta, confianza);
                
                boolean reconocido = confianza[0] < UMBRAL_CONFIANZA;
                double porcentajeConfianza = Math.max(0, 100.0 - confianza[0]);
                
                System.out.println("🎯 Reconocimiento - Etiqueta: " + etiqueta[0] + 
                                 ", Confianza: " + String.format("%.2f", porcentajeConfianza) + "%");
                
                return new ResultadoReconocimiento(etiqueta[0], porcentajeConfianza, reconocido);
            }
            
            return new ResultadoReconocimiento(-1, 0.0, false);
            
        } catch (Exception e) {
            System.err.println("❌ Error en reconocimiento: " + e.getMessage());
            return new ResultadoReconocimiento(-1, 0.0, false);
        }
    }

    /**
     * 🔧 UTILIDADES PRIVADAS
     */
    
    /**
     * Convertir BufferedImage a Mat
     */
    private Mat bufferedImageToMat(BufferedImage imagen) {
        Frame frame = converterJava2D.convert(imagen);
        return converterMat.convert(frame);
    }
    
    /**
     * Obtener el rostro más grande detectado
     */
    private Rect obtenerRostroMasGrande(List<Rect> rostros) {
        Rect mayor = rostros.get(0);
        for (Rect rostro : rostros) {
            if (rostro.area() > mayor.area()) {
                mayor = rostro;
            }
        }
        return mayor;
    }

    /**
     * 📊 CLASE RESULTADO DEL RECONOCIMIENTO
     */
    public static class ResultadoReconocimiento {
        private final int etiqueta;
        private final double confianza;
        private final boolean reconocido;
        
        public ResultadoReconocimiento(int etiqueta, double confianza, boolean reconocido) {
            this.etiqueta = etiqueta;
            this.confianza = confianza;
            this.reconocido = reconocido;
        }
        
        public int getEtiqueta() { return etiqueta; }
        public double getConfianza() { return confianza; }
        public boolean esReconocido() { return reconocido; }
        
        @Override
        public String toString() {
            return String.format("Reconocimiento{etiqueta=%d, confianza=%.2f%%, reconocido=%b}",
                               etiqueta, confianza, reconocido);
        }
    }

    /**
     * 🧹 LIMPIEZA DE RECURSOS
     */
    public void liberarRecursos() {
        try {
            if (clasificadorRostros != null) clasificadorRostros.close();
            if (clasificadorOjos != null) clasificadorOjos.close();
            if (reconocedorLBPH != null) reconocedorLBPH.close();
            if (reconocedorEigen != null) reconocedorEigen.close();
            if (reconocedorFisher != null) reconocedorFisher.close();
            
            for (Mat imagen : imageneEntrenamiento) {
                if (imagen != null) imagen.close();
            }
            
            if (etiquetasEntrenamiento != null) etiquetasEntrenamiento.close();
            
            System.out.println("🧹 Recursos OpenCV liberados");
            
        } catch (Exception e) {
            System.err.println("⚠️ Error liberando recursos: " + e.getMessage());
        }
    }

    // ========== GETTERS ==========
    public boolean isInicializado() { return inicializado; }
    public String getUltimoError() { return ultimoError; }
}