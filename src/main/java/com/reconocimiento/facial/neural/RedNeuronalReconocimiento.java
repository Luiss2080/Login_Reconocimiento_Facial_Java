package com.reconocimiento.facial.neural;

import com.reconocimiento.facial.modelos.Usuario;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.Color;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Optional;


public class RedNeuronalReconocimiento {

    // ========== CONFIGURACIÓN DE LA RED ==========
    private static final int DIMENSION_IMAGEN = 64; // 64x64 píxeles
    private static final int DIMENSION_ENTRADA = DIMENSION_IMAGEN * DIMENSION_IMAGEN; // 4096
    private static final int DIMENSION_CARACTERISTICAS = 128; // Vector de características faciales
    private static final int NEURONAS_CAPA_OCULTA_1 = 512;
    private static final int NEURONAS_CAPA_OCULTA_2 = 256;
    
    // Parámetros de entrenamiento
    // private static final double TASA_APRENDIZAJE = 0.001; // Para futuras implementaciones
    // private static final double DECAY_RATE = 0.95; // Para futuras implementaciones
    // private static final int EPOCAS_ENTRENAMIENTO = 50; // Para futuras implementaciones
    private static final double UMBRAL_RECONOCIMIENTO = 0.85; // 85% de confianza mínima
    
    // Parámetros de similitud (reservados para futuras implementaciones)
    // private static final double UMBRAL_SIMILITUD_COSENO = 0.75;
    // private static final double UMBRAL_DISTANCIA_EUCLIDIANA = 0.3;

    // ========== COMPONENTES DE LA RED ==========
    
    // Matrices de pesos
    private double[][] pesosEntradaOculta1;
    private double[][] pesosOculta1Oculta2;
    private double[][] pesosOculta2Salida;
    
    // Vectores de bias
    private double[] biasOculta1;
    private double[] biasOculta2;
    private double[] biasSalida;
    
    // Almacenamiento de perfiles faciales
    private Map<Integer, double[]> perfilesFaciales; // userId -> características
    private Map<Integer, String> nombresUsuarios; // userId -> nombre
    private Map<String, Integer> indiceUsuarios; // nombre -> userId
    
    // Estado de la red
    private boolean redInicializada;
    private boolean redEntrenada;
    private int contadorUsuarios;
    private Random random;
    
    // Métricas de rendimiento (para futuras implementaciones)
    // private double ultimaPrecision;
    private double ultimaConfianza;
    // private List<Double> historialPerdida;

    /**
     * Constructor principal
     */
    public RedNeuronalReconocimiento() {
        this.perfilesFaciales = new ConcurrentHashMap<>();
        this.nombresUsuarios = new ConcurrentHashMap<>();
        this.indiceUsuarios = new ConcurrentHashMap<>();
        // this.historialPerdida = new ArrayList<>(); // Para futuras implementaciones
        this.random = new Random(System.currentTimeMillis());
        this.contadorUsuarios = 0;
        this.redInicializada = false;
        this.redEntrenada = false;
        
        inicializarRed();
        System.out.println("🧠 Red neuronal inicializada correctamente");
    }

    /**
     * 🏗️ Inicializar la arquitectura de la red neuronal
     */
    private void inicializarRed() {
        try {
            // Inicializar matrices de pesos con distribución normal Xavier
            pesosEntradaOculta1 = inicializarMatriz(DIMENSION_ENTRADA, NEURONAS_CAPA_OCULTA_1);
            pesosOculta1Oculta2 = inicializarMatriz(NEURONAS_CAPA_OCULTA_1, NEURONAS_CAPA_OCULTA_2);
            pesosOculta2Salida = inicializarMatriz(NEURONAS_CAPA_OCULTA_2, DIMENSION_CARACTERISTICAS);
            
            // Inicializar bias con valores pequeños
            biasOculta1 = new double[NEURONAS_CAPA_OCULTA_1];
            biasOculta2 = new double[NEURONAS_CAPA_OCULTA_2];
            biasSalida = new double[DIMENSION_CARACTERISTICAS];
            
            Arrays.fill(biasOculta1, 0.01);
            Arrays.fill(biasOculta2, 0.01);
            Arrays.fill(biasSalida, 0.01);
            
            redInicializada = true;
            System.out.println("✅ Arquitectura de red neuronal configurada");
            
        } catch (Exception e) {
            System.err.println("❌ Error inicializando red neuronal: " + e.getMessage());
            throw new RuntimeException("Error crítico en inicialización de red neuronal", e);
        }
    }

    /**
     * 🎲 Inicializar matriz de pesos con distribución Xavier
     */
    private double[][] inicializarMatriz(int filas, int columnas) {
        double[][] matriz = new double[filas][columnas];
        double limite = Math.sqrt(6.0 / (filas + columnas)); // Xavier initialization
        
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                matriz[i][j] = (random.nextDouble() * 2 - 1) * limite;
            }
        }
        return matriz;
    }

    /**
     * 🖼️ Extraer características faciales de una imagen
     */
    public double[] extraerCaracteristicas(BufferedImage imagen) {
        try {
            // Preprocesar imagen
            double[] imagenNormalizada = preprocesarImagen(imagen);
            
            // Propagar hacia adelante por la red neuronal
            double[] caracteristicas = propagarHaciaAdelante(imagenNormalizada);
            
            return caracteristicas;
            
        } catch (Exception e) {
            System.err.println("❌ Error extrayendo características: " + e.getMessage());
            return new double[DIMENSION_CARACTERISTICAS]; // Vector vacío en caso de error
        }
    }

    /**
     * 🔄 Preprocesar imagen para la red neuronal
     */
    private double[] preprocesarImagen(BufferedImage imagen) {
        // Redimensionar a 64x64
        BufferedImage imagenRedimensionada = redimensionarImagen(imagen, DIMENSION_IMAGEN, DIMENSION_IMAGEN);
        
        // Convertir a escala de grises y normalizar
        double[] pixeles = new double[DIMENSION_ENTRADA];
        int indice = 0;
        
        for (int y = 0; y < DIMENSION_IMAGEN; y++) {
            for (int x = 0; x < DIMENSION_IMAGEN; x++) {
                Color color = new Color(imagenRedimensionada.getRGB(x, y));
                // Convertir a escala de grises usando luminancia
                double gris = 0.299 * color.getRed() + 0.587 * color.getGreen() + 0.114 * color.getBlue();
                // Normalizar a rango [0, 1]
                pixeles[indice++] = gris / 255.0;
            }
        }
        
        // Normalización adicional (z-score)
        return normalizarVector(pixeles);
    }

    /**
     * 📏 Redimensionar imagen
     */
    private BufferedImage redimensionarImagen(BufferedImage original, int ancho, int alto) {
        BufferedImage redimensionada = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = redimensionada.createGraphics();
        g2d.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION, 
                            java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(original, 0, 0, ancho, alto, null);
        g2d.dispose();
        return redimensionada;
    }

    /**
     * 📊 Normalizar vector usando z-score
     */
    private double[] normalizarVector(double[] vector) {
        double media = Arrays.stream(vector).average().orElse(0.0);
        double varianza = Arrays.stream(vector).map(x -> Math.pow(x - media, 2)).average().orElse(1.0);
        double desviacionTemporal = Math.sqrt(varianza);
        
        final double desviacion = (desviacionTemporal == 0) ? 1.0 : desviacionTemporal; // Evitar división por cero
        
        return Arrays.stream(vector).map(x -> (x - media) / desviacion).toArray();
    }

    /**
     * ➡️ Propagación hacia adelante
     */
    private double[] propagarHaciaAdelante(double[] entrada) {
        // Capa oculta 1
        double[] oculta1 = new double[NEURONAS_CAPA_OCULTA_1];
        for (int i = 0; i < NEURONAS_CAPA_OCULTA_1; i++) {
            double suma = biasOculta1[i];
            for (int j = 0; j < DIMENSION_ENTRADA; j++) {
                suma += entrada[j] * pesosEntradaOculta1[j][i];
            }
            oculta1[i] = funcionActivacionReLU(suma);
        }
        
        // Capa oculta 2
        double[] oculta2 = new double[NEURONAS_CAPA_OCULTA_2];
        for (int i = 0; i < NEURONAS_CAPA_OCULTA_2; i++) {
            double suma = biasOculta2[i];
            for (int j = 0; j < NEURONAS_CAPA_OCULTA_1; j++) {
                suma += oculta1[j] * pesosOculta1Oculta2[j][i];
            }
            oculta2[i] = funcionActivacionReLU(suma);
        }
        
        // Capa de salida (características)
        double[] salida = new double[DIMENSION_CARACTERISTICAS];
        for (int i = 0; i < DIMENSION_CARACTERISTICAS; i++) {
            double suma = biasSalida[i];
            for (int j = 0; j < NEURONAS_CAPA_OCULTA_2; j++) {
                suma += oculta2[j] * pesosOculta2Salida[j][i];
            }
            salida[i] = Math.tanh(suma); // Tanh para características normalizadas [-1, 1]
        }
        
        return salida;
    }

    /**
     * ⚡ Función de activación ReLU
     */
    private double funcionActivacionReLU(double x) {
        return Math.max(0, x);
    }

    /**
     * 👤 Registrar nuevo usuario con sus características faciales
     */
    public boolean registrarUsuario(String nombreUsuario, List<BufferedImage> imagenes) {
        try {
            if (imagenes.isEmpty()) {
                System.err.println("❌ No se proporcionaron imágenes para el usuario: " + nombreUsuario);
                return false;
            }
            
            System.out.println("📝 Registrando usuario: " + nombreUsuario);
            
            // Extraer características de todas las imágenes
            List<double[]> caracteristicasImagenes = new ArrayList<>();
            for (BufferedImage imagen : imagenes) {
                double[] caracteristicas = extraerCaracteristicas(imagen);
                caracteristicasImagenes.add(caracteristicas);
            }
            
            // Calcular perfil promedio del usuario
            double[] perfilPromedio = calcularPerfilPromedio(caracteristicasImagenes);
            
            // Asignar ID al usuario
            int userId = contadorUsuarios++;
            
            // Almacenar en los mapas
            perfilesFaciales.put(userId, perfilPromedio);
            nombresUsuarios.put(userId, nombreUsuario);
            indiceUsuarios.put(nombreUsuario, userId);
            
            System.out.println("✅ Usuario registrado: " + nombreUsuario + " (ID: " + userId + ")");
            System.out.println("📊 Características extraídas de " + imagenes.size() + " imágenes");
            
            return true;
            
        } catch (Exception e) {
            System.err.println("❌ Error registrando usuario " + nombreUsuario + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * 📊 Calcular perfil promedio de múltiples características
     */
    private double[] calcularPerfilPromedio(List<double[]> caracteristicas) {
        if (caracteristicas.isEmpty()) {
            return new double[DIMENSION_CARACTERISTICAS];
        }
        
        double[] promedio = new double[DIMENSION_CARACTERISTICAS];
        
        // Sumar todas las características
        for (double[] caract : caracteristicas) {
            for (int i = 0; i < DIMENSION_CARACTERISTICAS; i++) {
                promedio[i] += caract[i];
            }
        }
        
        // Dividir por el número de muestras
        for (int i = 0; i < DIMENSION_CARACTERISTICAS; i++) {
            promedio[i] /= caracteristicas.size();
        }
        
        return promedio;
    }

    /**
     * 🔍 Reconocer usuario a partir de una imagen
     */
    public Optional<Usuario> reconocerUsuario(BufferedImage imagen) {
        try {
            if (perfilesFaciales.isEmpty()) {
                System.out.println("⚠️ No hay usuarios registrados en el sistema");
                return Optional.empty();
            }
            
            // Extraer características de la imagen
            double[] caracteristicasImagen = extraerCaracteristicas(imagen);
            
            // Encontrar el mejor match
            ResultadoReconocimiento mejor = encontrarMejorCoincidencia(caracteristicasImagen);
            
            if (mejor != null && mejor.confianza >= UMBRAL_RECONOCIMIENTO) {
                ultimaConfianza = mejor.confianza;
                
                // Crear objeto Usuario (simulado)
                Usuario usuario = new Usuario();
                usuario.setIdUsuario(mejor.userId);
                usuario.setNombreUsuario(nombresUsuarios.get(mejor.userId));
                usuario.setNombreCompleto(nombresUsuarios.get(mejor.userId));
                
                System.out.println("✅ Usuario reconocido: " + usuario.getNombreUsuario() + 
                                 " (Confianza: " + String.format("%.2f%%", mejor.confianza * 100) + ")");
                
                return Optional.of(usuario);
            } else {
                String confianzaStr = mejor != null ? String.format("%.2f%%", mejor.confianza * 100) : "0%";
                System.out.println("❌ Usuario no reconocido (Confianza: " + confianzaStr + 
                                 ", Umbral: " + String.format("%.2f%%", UMBRAL_RECONOCIMIENTO * 100) + ")");
                return Optional.empty();
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error en reconocimiento: " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * 🎯 Encontrar la mejor coincidencia entre los usuarios registrados
     */
    private ResultadoReconocimiento encontrarMejorCoincidencia(double[] caracteristicasImagen) {
        ResultadoReconocimiento mejor = null;
        double mejorConfianza = 0.0;
        
        for (Map.Entry<Integer, double[]> entry : perfilesFaciales.entrySet()) {
            int userId = entry.getKey();
            double[] perfilUsuario = entry.getValue();
            
            // Calcular similitud coseno
            double similitudCoseno = calcularSimilitudCoseno(caracteristicasImagen, perfilUsuario);
            
            // Calcular distancia euclidiana normalizada
            double distanciaEuclidiana = calcularDistanciaEuclidiana(caracteristicasImagen, perfilUsuario);
            double similitudEuclidiana = 1.0 / (1.0 + distanciaEuclidiana);
            
            // Combinar métricas para confianza final
            double confianza = (similitudCoseno * 0.7) + (similitudEuclidiana * 0.3);
            
            if (confianza > mejorConfianza) {
                mejorConfianza = confianza;
                mejor = new ResultadoReconocimiento(userId, confianza, similitudCoseno, distanciaEuclidiana);
            }
        }
        
        return mejor;
    }

    /**
     * 📐 Calcular similitud coseno entre dos vectores
     */
    private double calcularSimilitudCoseno(double[] vector1, double[] vector2) {
        double productoEscalar = 0.0;
        double norma1 = 0.0;
        double norma2 = 0.0;
        
        for (int i = 0; i < vector1.length; i++) {
            productoEscalar += vector1[i] * vector2[i];
            norma1 += vector1[i] * vector1[i];
            norma2 += vector2[i] * vector2[i];
        }
        
        norma1 = Math.sqrt(norma1);
        norma2 = Math.sqrt(norma2);
        
        if (norma1 == 0.0 || norma2 == 0.0) {
            return 0.0;
        }
        
        return productoEscalar / (norma1 * norma2);
    }

    /**
     * 📏 Calcular distancia euclidiana entre dos vectores
     */
    private double calcularDistanciaEuclidiana(double[] vector1, double[] vector2) {
        double suma = 0.0;
        for (int i = 0; i < vector1.length; i++) {
            double diferencia = vector1[i] - vector2[i];
            suma += diferencia * diferencia;
        }
        return Math.sqrt(suma);
    }

    /**
     * 📊 Obtener estadísticas de la red neuronal
     */
    public String obtenerEstadisticas() {
        StringBuilder stats = new StringBuilder();
        stats.append("🧠 ESTADÍSTICAS DE RED NEURONAL\n");
        stats.append("=====================================\n");
        stats.append("• Red inicializada: ").append(redInicializada ? "✅" : "❌").append("\n");
        stats.append("• Red entrenada: ").append(redEntrenada ? "✅" : "❌").append("\n");
        stats.append("• Usuarios registrados: ").append(perfilesFaciales.size()).append("\n");
        stats.append("• Dimensión de entrada: ").append(DIMENSION_ENTRADA).append("\n");
        stats.append("• Dimensión de características: ").append(DIMENSION_CARACTERISTICAS).append("\n");
        stats.append("• Umbral de reconocimiento: ").append(String.format("%.2f%%", UMBRAL_RECONOCIMIENTO * 100)).append("\n");
        stats.append("• Última confianza: ").append(String.format("%.2f%%", ultimaConfianza * 100)).append("\n");
        
        if (!nombresUsuarios.isEmpty()) {
            stats.append("• Usuarios: ");
            nombresUsuarios.values().forEach(nombre -> stats.append(nombre).append(" "));
            stats.append("\n");
        }
        
        return stats.toString();
    }

    /**
     * 🔧 Métodos de utilidad y getters
     */
    public boolean isRedInicializada() { return redInicializada; }
    public boolean isRedEntrenada() { return redEntrenada; }
    public double getUltimaConfianza() { return ultimaConfianza; }
    public int getNumeroUsuariosRegistrados() { return perfilesFaciales.size(); }

    /**
     * 📋 Clase interna para resultados de reconocimiento
     */
    private static class ResultadoReconocimiento {
        final int userId;
        final double confianza;
        // final double similitudCoseno; // Para futuras implementaciones de métricas
        // final double distanciaEuclidiana; // Para futuras implementaciones de métricas
        
        public ResultadoReconocimiento(int userId, double confianza, double similitudCoseno, double distanciaEuclidiana) {
            this.userId = userId;
            this.confianza = confianza;
            // this.similitudCoseno = similitudCoseno;
            // this.distanciaEuclidiana = distanciaEuclidiana;
        }
    }

    /**
     * 🧪 Método de prueba para verificar funcionamiento
     */
    public void ejecutarPrueba() {
        System.out.println("🧪 Ejecutando prueba de red neuronal...");
        
        // Crear imagen de prueba
        BufferedImage imagenPrueba = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = imagenPrueba.createGraphics();
        g2d.setColor(Color.GRAY);
        g2d.fillRect(0, 0, 100, 100);
        g2d.dispose();
        
        // Extraer características
        double[] caracteristicas = extraerCaracteristicas(imagenPrueba);
        
        System.out.println("✅ Prueba completada");
        System.out.println("📊 Características extraídas: " + caracteristicas.length + " valores");
        System.out.println("🎯 Primer valor: " + String.format("%.6f", caracteristicas[0]));
    }
}