package com.reconocimiento.facial.modelos;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;

/**
 * Modelo para almacenar las características faciales extraídas
 * Contiene el vector de características y métodos de comparación
 */
public class CaracteristicaFacial {
    private int idCaracteristica;
    private int idUsuario;
    private double[] vectorCaracteristicas;
    private String rutaImagen;
    private boolean esImagenEntrenamiento;
    private LocalDateTime fechaCreacion;
    private double calidadImagen;
    private String metodoExtraccion;

    // Constructor vacío
    public CaracteristicaFacial() {
        this.fechaCreacion = LocalDateTime.now();
        this.esImagenEntrenamiento = true;
        this.calidadImagen = 0.0;
        this.metodoExtraccion = "OpenCV";
    }

    // Constructor principal
    public CaracteristicaFacial(int idUsuario, double[] vectorCaracteristicas) {
        this();
        this.idUsuario = idUsuario;
        this.vectorCaracteristicas = vectorCaracteristicas;
    }

    // Constructor con ruta de imagen
    public CaracteristicaFacial(int idUsuario, double[] vectorCaracteristicas, String rutaImagen) {
        this(idUsuario, vectorCaracteristicas);
        this.rutaImagen = rutaImagen;
    }

    // Constructor completo
    public CaracteristicaFacial(int idUsuario, double[] vectorCaracteristicas, String rutaImagen,
                                boolean esImagenEntrenamiento, double calidadImagen) {
        this(idUsuario, vectorCaracteristicas, rutaImagen);
        this.esImagenEntrenamiento = esImagenEntrenamiento;
        this.calidadImagen = calidadImagen;
    }

    // Métodos getter
    public int getIdCaracteristica() {
        return idCaracteristica;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public double[] getVectorCaracteristicas() {
        return vectorCaracteristicas;
    }

    public String getRutaImagen() {
        return rutaImagen;
    }

    public boolean isEsImagenEntrenamiento() {
        return esImagenEntrenamiento;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public double getCalidadImagen() {
        return calidadImagen;
    }

    public String getMetodoExtraccion() {
        return metodoExtraccion;
    }

    // Métodos setter
    public void setIdCaracteristica(int idCaracteristica) {
        this.idCaracteristica = idCaracteristica;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public void setVectorCaracteristicas(double[] vectorCaracteristicas) {
        this.vectorCaracteristicas = vectorCaracteristicas;
    }

    public void setRutaImagen(String rutaImagen) {
        this.rutaImagen = rutaImagen;
    }

    public void setEsImagenEntrenamiento(boolean esImagenEntrenamiento) {
        this.esImagenEntrenamiento = esImagenEntrenamiento;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public void setCalidadImagen(double calidadImagen) {
        this.calidadImagen = calidadImagen;
    }

    public void setMetodoExtraccion(String metodoExtraccion) {
        this.metodoExtraccion = metodoExtraccion;
    }

    // Métodos de comparación y similitud

    /**
     * Calcula la similitud euclidiana entre dos vectores de características
     * @param otroVector Vector a comparar
     * @return Valor de similitud entre 0 y 1 (1 = más similar)
     */
    public double calcularSimilitudEuclidiana(double[] otroVector) {
        if (!sonVectoresCompatibles(otroVector)) {
            throw new IllegalArgumentException("Los vectores deben tener la misma dimensión");
        }

        double sumaCuadrados = 0.0;
        for (int i = 0; i < this.vectorCaracteristicas.length; i++) {
            double diferencia = this.vectorCaracteristicas[i] - otroVector[i];
            sumaCuadrados += diferencia * diferencia;
        }

        double distancia = Math.sqrt(sumaCuadrados);
        // Convertir distancia a similitud (0-1, donde 1 es más similar)
        return 1.0 / (1.0 + distancia);
    }

    /**
     * Calcula la similitud coseno entre dos vectores de características
     * @param otroVector Vector a comparar
     * @return Valor de similitud entre -1 y 1 (1 = más similar)
     */
    public double calcularSimilitudCoseno(double[] otroVector) {
        if (!sonVectoresCompatibles(otroVector)) {
            throw new IllegalArgumentException("Los vectores deben tener la misma dimensión");
        }

        double productoEscalar = 0.0;
        double normaA = 0.0;
        double normaB = 0.0;

        for (int i = 0; i < this.vectorCaracteristicas.length; i++) {
            productoEscalar += this.vectorCaracteristicas[i] * otroVector[i];
            normaA += this.vectorCaracteristicas[i] * this.vectorCaracteristicas[i];
            normaB += otroVector[i] * otroVector[i];
        }

        double denominador = Math.sqrt(normaA) * Math.sqrt(normaB);
        return denominador != 0 ? productoEscalar / denominador : 0.0;
    }

    /**
     * Calcula la distancia Manhattan entre dos vectores
     * @param otroVector Vector a comparar
     * @return Distancia Manhattan
     */
    public double calcularDistanciaManhattan(double[] otroVector) {
        if (!sonVectoresCompatibles(otroVector)) {
            throw new IllegalArgumentException("Los vectores deben tener la misma dimensión");
        }

        double suma = 0.0;
        for (int i = 0; i < this.vectorCaracteristicas.length; i++) {
            suma += Math.abs(this.vectorCaracteristicas[i] - otroVector[i]);
        }
        return suma;
    }

    /**
     * Determina si dos vectores son compatibles para comparación
     * @param otroVector Vector a verificar
     * @return true si son compatibles, false en caso contrario
     */
    private boolean sonVectoresCompatibles(double[] otroVector) {
        return this.vectorCaracteristicas != null &&
               otroVector != null &&
               this.vectorCaracteristicas.length == otroVector.length &&
               this.vectorCaracteristicas.length > 0;
    }

    /**
     * Verifica si las características son válidas
     * @return true si las características son válidas
     */
    public boolean sonCaracteristicasValidas() {
        return vectorCaracteristicas != null &&
               vectorCaracteristicas.length > 0 &&
               idUsuario > 0 &&
               calidadImagen >= 0.0 && calidadImagen <= 1.0;
    }

    /**
     * Obtiene el tamaño del vector de características
     * @return Tamaño del vector
     */
    public int obtenerTamanoVector() {
        return vectorCaracteristicas != null ? vectorCaracteristicas.length : 0;
    }

    /**
     * Convierte el vector de características a JSON para almacenamiento
     * @return String JSON del vector
     */
    public String vectorAJson() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(vectorCaracteristicas);
        } catch (Exception e) {
            System.err.println("Error convirtiendo vector a JSON: " + e.getMessage());
            return "[]";
        }
    }

    /**
     * Carga el vector de características desde JSON
     * @param jsonVector String JSON del vector
     */
    public void cargarVectorDesdeJson(String jsonVector) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            this.vectorCaracteristicas = mapper.readValue(jsonVector, double[].class);
        } catch (Exception e) {
            System.err.println("Error cargando vector desde JSON: " + e.getMessage());
            this.vectorCaracteristicas = new double[0];
        }
    }

    @Override
    public String toString() {
        return "CaracteristicaFacial{" +
                "idCaracteristica=" + idCaracteristica +
                ", idUsuario=" + idUsuario +
                ", tamanoVector=" + obtenerTamanoVector() +
                ", rutaImagen='" + rutaImagen + '\'' +
                ", esImagenEntrenamiento=" + esImagenEntrenamiento +
                ", calidadImagen=" + calidadImagen +
                ", metodoExtraccion='" + metodoExtraccion + '\'' +
                ", fechaCreacion=" + fechaCreacion +
                '}';
    }

    /**
     * Obtiene un resumen de las características
     * @return String con información resumida
     */
    public String obtenerResumen() {
        return String.format("Características faciales - Usuario: %d, Vector: %d elementos, " +
                           "Calidad: %.2f, Entrenamiento: %s",
                           idUsuario, obtenerTamanoVector(), calidadImagen,
                           esImagenEntrenamiento ? "Sí" : "No");
    }
}