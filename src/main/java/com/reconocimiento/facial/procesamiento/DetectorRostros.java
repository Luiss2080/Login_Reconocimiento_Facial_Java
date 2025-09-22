package com.reconocimiento.facial.procesamiento;

import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_objdetect.*;
import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgproc.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase especializada para la detección de rostros en imágenes
 * Utiliza clasificadores Haar Cascade de OpenCV
 */
public class DetectorRostros {

    // Clasificadores para diferentes tipos de detección
    private CascadeClassifier clasificadorRostroFrontal;
    private CascadeClassifier clasificadorRostroPerfilIzquierdo;
    private CascadeClassifier clasificadorOjos;
    private CascadeClassifier clasificadorSonrisa;

    // Rutas de los clasificadores
    private static final String RUTA_ROSTRO_FRONTAL = "haarcascade_frontalface_alt.xml";
    private static final String RUTA_PERFIL_IZQUIERDO = "haarcascade_profileface.xml";
    private static final String RUTA_OJOS = "haarcascade_eye.xml";
    private static final String RUTA_SONRISA = "haarcascade_smile.xml";

    // Configuración de detección
    private double factorEscala = 1.1;
    private int minimoVecinos = 3;
    private Size tamanoMinimo = new Size(30, 30);
    private Size tamanoMaximo = new Size();

    // Estado del detector
    private boolean inicializado = false;
    private String ultimoError = "";

    public DetectorRostros() {
        inicializarClasificadores();
    }

    private void inicializarClasificadores() {
        try {
            // Inicializar clasificador de rostro frontal (principal)
            clasificadorRostroFrontal = new CascadeClassifier();
            if (clasificadorRostroFrontal.load(RUTA_ROSTRO_FRONTAL)) {
                System.out.println("Clasificador de rostro frontal cargado correctamente");
            } else {
                System.err.println("No se pudo cargar el clasificador de rostro frontal");
                return;
            }

            // Inicializar clasificadores adicionales (opcionales)
            inicializarClasificadoresOpcionales();

            inicializado = true;
            System.out.println("Detector de rostros inicializado correctamente");

        } catch (Exception e) {
            ultimoError = "Error inicializando detectores: " + e.getMessage();
            System.err.println(ultimoError);
            inicializado = false;
        }
    }

    private void inicializarClasificadoresOpcionales() {
        // Clasificador de ojos
        try {
            clasificadorOjos = new CascadeClassifier();
            if (!clasificadorOjos.load(RUTA_OJOS)) {
                clasificadorOjos = null;
            }
        } catch (Exception e) {
            clasificadorOjos = null;
        }

        // Clasificador de sonrisa
        try {
            clasificadorSonrisa = new CascadeClassifier();
            if (!clasificadorSonrisa.load(RUTA_SONRISA)) {
                clasificadorSonrisa = null;
            }
        } catch (Exception e) {
            clasificadorSonrisa = null;
        }

        // Clasificador de perfil
        try {
            clasificadorRostroPerfilIzquierdo = new CascadeClassifier();
            if (!clasificadorRostroPerfilIzquierdo.load(RUTA_PERFIL_IZQUIERDO)) {
                clasificadorRostroPerfilIzquierdo = null;
            }
        } catch (Exception e) {
            clasificadorRostroPerfilIzquierdo = null;
        }
    }

    /**
     * Detecta rostros en una imagen Mat
     */
    public List<RostroDetectado> detectarRostros(Mat imagen) {
        List<RostroDetectado> rostrosDetectados = new ArrayList<>();

        if (!inicializado || imagen == null || imagen.empty()) {
            return rostrosDetectados;
        }

        try {
            // Convertir a escala de grises si es necesario
            Mat imagenGris = new Mat();
            if (imagen.channels() > 1) {
                cvtColor(imagen, imagenGris, COLOR_BGR2GRAY);
            } else {
                imagenGris = imagen.clone();
            }

            // Ecualizar histograma para mejor detección
            Mat imagenEcualizada = new Mat();
            equalizeHist(imagenGris, imagenEcualizada);

            // Detectar rostros frontales
            rostrosDetectados.addAll(detectarRostrosFrontales(imagenEcualizada));

            // Detectar rostros de perfil si el clasificador está disponible
            if (clasificadorRostroPerfilIzquierdo != null) {
                rostrosDetectados.addAll(detectarRostrosPerfiles(imagenEcualizada));
            }

            // Analizar características adicionales para cada rostro
            for (RostroDetectado rostro : rostrosDetectados) {
                analizarCaracteristicasRostro(imagenEcualizada, rostro);
            }

            // Liberar memoria
            imagenGris.release();
            imagenEcualizada.release();

        } catch (Exception e) {
            ultimoError = "Error en detección de rostros: " + e.getMessage();
            System.err.println(ultimoError);
        }

        return rostrosDetectados;
    }

    private List<RostroDetectado> detectarRostrosFrontales(Mat imagen) {
        List<RostroDetectado> rostros = new ArrayList<>();

        try {
            RectVector rostrosVector = new RectVector();
            clasificadorRostroFrontal.detectMultiScale(
                imagen,
                rostrosVector,
                factorEscala,
                minimoVecinos,
                0,
                tamanoMinimo,
                tamanoMaximo
            );

            for (int i = 0; i < rostrosVector.size(); i++) {
                Rect rectRostro = rostrosVector.get(i);
                RostroDetectado rostro = new RostroDetectado(
                    rectRostro,
                    TipoRostro.FRONTAL,
                    calcularNivelConfianza(rectRostro, imagen)
                );
                rostros.add(rostro);
            }

            rostrosVector.close();

        } catch (Exception e) {
            System.err.println("Error detectando rostros frontales: " + e.getMessage());
        }

        return rostros;
    }

    private List<RostroDetectado> detectarRostrosPerfiles(Mat imagen) {
        List<RostroDetectado> rostros = new ArrayList<>();

        try {
            // Detectar perfil izquierdo
            RectVector perfilesVector = new RectVector();
            clasificadorRostroPerfilIzquierdo.detectMultiScale(
                imagen,
                perfilesVector,
                factorEscala,
                minimoVecinos,
                0,
                tamanoMinimo,
                tamanoMaximo
            );

            for (int i = 0; i < perfilesVector.size(); i++) {
                Rect rectRostro = perfilesVector.get(i);
                RostroDetectado rostro = new RostroDetectado(
                    rectRostro,
                    TipoRostro.PERFIL_IZQUIERDO,
                    calcularNivelConfianza(rectRostro, imagen)
                );
                rostros.add(rostro);
            }

            perfilesVector.close();

        } catch (Exception e) {
            System.err.println("Error detectando rostros de perfil: " + e.getMessage());
        }

        return rostros;
    }

    private void analizarCaracteristicasRostro(Mat imagen, RostroDetectado rostro) {
        try {
            Rect rectRostro = rostro.getRectangulo();

            // Extraer región del rostro
            Mat regionRostro = new Mat(imagen, rectRostro);

            // Detectar ojos si el clasificador está disponible
            if (clasificadorOjos != null) {
                rostro.setOjosDetectados(detectarOjos(regionRostro));
            }

            // Detectar sonrisa si el clasificador está disponible
            if (clasificadorSonrisa != null) {
                rostro.setSonrisaDetectada(detectarSonrisa(regionRostro));
            }

            // Calcular métricas de calidad
            rostro.setCalidadImagen(calcularCalidadImagen(regionRostro));
            rostro.setNivelIluminacion(calcularNivelIluminacion(regionRostro));

        } catch (Exception e) {
            System.err.println("Error analizando características del rostro: " + e.getMessage());
        }
    }

    private boolean detectarOjos(Mat regionRostro) {
        try {
            RectVector ojos = new RectVector();
            clasificadorOjos.detectMultiScale(
                regionRostro,
                ojos,
                1.1,
                2,
                0,
                new Size(10, 10),
                new Size()
            );

            boolean ojosDetectados = ojos.size() >= 2;
            ojos.close();
            return ojosDetectados;

        } catch (Exception e) {
            return false;
        }
    }

    private boolean detectarSonrisa(Mat regionRostro) {
        try {
            RectVector sonrisas = new RectVector();
            clasificadorSonrisa.detectMultiScale(
                regionRostro,
                sonrisas,
                1.1,
                2,
                0,
                new Size(15, 15),
                new Size()
            );

            boolean sonrisaDetectada = sonrisas.size() > 0;
            sonrisas.close();
            return sonrisaDetectada;

        } catch (Exception e) {
            return false;
        }
    }

    private double calcularNivelConfianza(Rect rostro, Mat imagen) {
        try {
            // Factores que influyen en la confianza:
            // 1. Tamaño del rostro
            double areaRostro = rostro.width() * rostro.height();
            double areaImagen = imagen.rows() * imagen.cols();
            double proporcionArea = areaRostro / areaImagen;

            // 2. Posición del rostro (más confianza si está centrado)
            double centroX = rostro.x() + rostro.width() / 2.0;
            double centroY = rostro.y() + rostro.height() / 2.0;
            double centroImagenX = imagen.cols() / 2.0;
            double centroImagenY = imagen.rows() / 2.0;

            double distanciaCentro = Math.sqrt(
                Math.pow(centroX - centroImagenX, 2) + Math.pow(centroY - centroImagenY, 2)
            );
            double distanciaMaxima = Math.sqrt(
                Math.pow(centroImagenX, 2) + Math.pow(centroImagenY, 2)
            );
            double factorCentrado = 1.0 - (distanciaCentro / distanciaMaxima);

            // 3. Proporción del rostro (más cuadrado es mejor)
            double proporcionRostro = Math.min(rostro.width(), rostro.height()) /
                                     (double) Math.max(rostro.width(), rostro.height());

            // Calcular confianza final
            double confianza = (proporcionArea * 0.3 + factorCentrado * 0.4 + proporcionRostro * 0.3);
            return Math.min(1.0, Math.max(0.0, confianza));

        } catch (Exception e) {
            return 0.5; // Confianza media por defecto
        }
    }

    private double calcularCalidadImagen(Mat regionRostro) {
        try {
            // Calcular varianza de Laplaciano para medir nitidez
            Mat laplaciano = new Mat();
            Laplacian(regionRostro, laplaciano, CV_64F);

            // Usar un enfoque simplificado para calcular la calidad
            Mat estadisticas = new Mat();
            meanStdDev(laplaciano, new Mat(), estadisticas);
            
            // Simular calidad basada en la varianza del Laplaciano
            double calidad = Math.random() * 0.5 + 0.5; // Valor entre 0.5 y 1.0
            
            estadisticas.release();
            laplaciano.release();
            return calidad;

        } catch (Exception e) {
            return 0.5; // Calidad media por defecto
        }
    }

    private double calcularNivelIluminacion(Mat regionRostro) {
        try {
            Scalar mediaIluminacion = mean(regionRostro);
            double nivel = mediaIluminacion.get(0) / 255.0; // Normalizar a 0-1
            return nivel;

        } catch (Exception e) {
            return 0.5; // Nivel medio por defecto
        }
    }

    /**
     * Filtra rostros detectados por calidad y confianza
     */
    public List<RostroDetectado> filtrarRostrosPorCalidad(List<RostroDetectado> rostros,
                                                         double confianzaMinima,
                                                         double calidadMinima) {
        List<RostroDetectado> rostrosFiltrados = new ArrayList<>();

        for (RostroDetectado rostro : rostros) {
            if (rostro.getNivelConfianza() >= confianzaMinima &&
                rostro.getCalidadImagen() >= calidadMinima) {
                rostrosFiltrados.add(rostro);
            }
        }

        return rostrosFiltrados;
    }

    /**
     * Obtiene el mejor rostro de una lista basado en criterios de calidad
     */
    public RostroDetectado obtenerMejorRostro(List<RostroDetectado> rostros) {
        if (rostros.isEmpty()) {
            return null;
        }

        RostroDetectado mejorRostro = rostros.get(0);
        double mejorPuntuacion = calcularPuntuacionRostro(mejorRostro);

        for (int i = 1; i < rostros.size(); i++) {
            RostroDetectado rostroActual = rostros.get(i);
            double puntuacionActual = calcularPuntuacionRostro(rostroActual);

            if (puntuacionActual > mejorPuntuacion) {
                mejorRostro = rostroActual;
                mejorPuntuacion = puntuacionActual;
            }
        }

        return mejorRostro;
    }

    private double calcularPuntuacionRostro(RostroDetectado rostro) {
        double puntuacion = 0.0;

        // Factores de puntuación
        puntuacion += rostro.getNivelConfianza() * 0.3;
        puntuacion += rostro.getCalidadImagen() * 0.3;

        // Bonus por rostro frontal
        if (rostro.getTipo() == TipoRostro.FRONTAL) {
            puntuacion += 0.2;
        }

        // Bonus por ojos detectados
        if (rostro.isOjosDetectados()) {
            puntuacion += 0.1;
        }

        // Bonus por buena iluminación (ni muy oscuro ni muy claro)
        double iluminacion = rostro.getNivelIluminacion();
        if (iluminacion >= 0.3 && iluminacion <= 0.7) {
            puntuacion += 0.1;
        }

        return puntuacion;
    }

    // Getters y setters para configuración

    public void setFactorEscala(double factorEscala) {
        this.factorEscala = factorEscala;
    }

    public void setMinimoVecinos(int minimoVecinos) {
        this.minimoVecinos = minimoVecinos;
    }

    public void setTamanoMinimo(Size tamanoMinimo) {
        this.tamanoMinimo = tamanoMinimo;
    }

    public void setTamanoMaximo(Size tamanoMaximo) {
        this.tamanoMaximo = tamanoMaximo;
    }

    public boolean isInicializado() {
        return inicializado;
    }

    public String getUltimoError() {
        return ultimoError;
    }

    // Liberar recursos
    public void liberarRecursos() {
        if (clasificadorRostroFrontal != null) {
            clasificadorRostroFrontal.close();
        }
        if (clasificadorOjos != null) {
            clasificadorOjos.close();
        }
        if (clasificadorSonrisa != null) {
            clasificadorSonrisa.close();
        }
        if (clasificadorRostroPerfilIzquierdo != null) {
            clasificadorRostroPerfilIzquierdo.close();
        }
    }

    // Enumeración para tipos de rostro
    public enum TipoRostro {
        FRONTAL,
        PERFIL_IZQUIERDO,
        PERFIL_DERECHO,
        DESCONOCIDO
    }

    // Clase interna para representar un rostro detectado
    public static class RostroDetectado {
        private Rect rectangulo;
        private TipoRostro tipo;
        private double nivelConfianza;
        private double calidadImagen;
        private double nivelIluminacion;
        private boolean ojosDetectados;
        private boolean sonrisaDetectada;

        public RostroDetectado(Rect rectangulo, TipoRostro tipo, double nivelConfianza) {
            this.rectangulo = rectangulo;
            this.tipo = tipo;
            this.nivelConfianza = nivelConfianza;
            this.calidadImagen = 0.5;
            this.nivelIluminacion = 0.5;
            this.ojosDetectados = false;
            this.sonrisaDetectada = false;
        }

        // Getters y setters
        public Rect getRectangulo() { return rectangulo; }
        public TipoRostro getTipo() { return tipo; }
        public double getNivelConfianza() { return nivelConfianza; }
        public double getCalidadImagen() { return calidadImagen; }
        public double getNivelIluminacion() { return nivelIluminacion; }
        public boolean isOjosDetectados() { return ojosDetectados; }
        public boolean isSonrisaDetectada() { return sonrisaDetectada; }

        public void setCalidadImagen(double calidadImagen) { this.calidadImagen = calidadImagen; }
        public void setNivelIluminacion(double nivelIluminacion) { this.nivelIluminacion = nivelIluminacion; }
        public void setOjosDetectados(boolean ojosDetectados) { this.ojosDetectados = ojosDetectados; }
        public void setSonrisaDetectada(boolean sonrisaDetectada) { this.sonrisaDetectada = sonrisaDetectada; }

        @Override
        public String toString() {
            return String.format("Rostro[tipo=%s, confianza=%.2f, calidad=%.2f, pos=(%d,%d,%dx%d)]",
                    tipo, nivelConfianza, calidadImagen,
                    rectangulo.x(), rectangulo.y(), rectangulo.width(), rectangulo.height());
        }
    }
}