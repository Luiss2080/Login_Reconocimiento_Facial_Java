package com.reconocimiento.facial.procesamiento;

import com.reconocimiento.facial.procesamiento.ProcesadorOpenCV.ResultadoReconocimiento;
import org.bytedeco.opencv.opencv_core.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * 🤖 INTEGRADOR DE OPENCV CON SISTEMA DE RECONOCIMIENTO
 * Combina detección, preprocesamiento y reconocimiento facial avanzado
 * Proporciona una interfaz unificada para todo el procesamiento de imágenes
 */
public class IntegradorOpenCV {

    // ========== COMPONENTES PRINCIPALES ==========
    private final ProcesadorOpenCV procesadorOpenCV;
    private final ManejadorCamara manejadorCamara;
    
    // ========== ESTADO DEL SISTEMA ==========
    private boolean sistemaInicializado = false;
    private Map<Integer, String> mapaUsuarios; // ID -> Nombre de usuario
    private int proximaEtiqueta = 1;
    
    // ========== CONFIGURACIÓN ==========
    private static final double UMBRAL_CONFIANZA_ALTA = 85.0;
    private static final double UMBRAL_CONFIANZA_MEDIA = 70.0;
    private static final int MAX_INTENTOS_DETECCION = 3;

    /**
     * Constructor
     */
    public IntegradorOpenCV() {
        try {
            this.procesadorOpenCV = new ProcesadorOpenCV();
            this.manejadorCamara = new ManejadorCamara();
            this.mapaUsuarios = new HashMap<>();
            
            verificarInicializacion();
            
        } catch (Exception e) {
            System.err.println("❌ Error inicializando IntegradorOpenCV: " + e.getMessage());
            throw new RuntimeException("Error crítico en IntegradorOpenCV", e);
        }
    }

    /**
     * Verificar que todos los componentes estén inicializados
     */
    private void verificarInicializacion() {
        sistemaInicializado = procesadorOpenCV.isInicializado();
        
        if (sistemaInicializado) {
            System.out.println("✅ IntegradorOpenCV inicializado correctamente");
        } else {
            System.err.println("❌ Algunos componentes no se inicializaron correctamente");
            System.err.println("   - ProcesadorOpenCV: " + procesadorOpenCV.getUltimoError());
        }
    }

    /**
     * 📋 REGISTRAR USUARIO FACIAL
     * Procesa las imágenes faciales y entrena el modelo para un nuevo usuario
     */
    public boolean registrarUsuarioFacial(String nombreUsuario, List<BufferedImage> imagenesFaciales) {
        if (!sistemaInicializado || nombreUsuario == null || imagenesFaciales == null || imagenesFaciales.isEmpty()) {
            System.err.println("❌ Parámetros inválidos para registro facial");
            return false;
        }

        try {
            System.out.println("🔄 Procesando registro facial para: " + nombreUsuario);
            
            // Asignar etiqueta única al usuario
            int etiquetaUsuario = proximaEtiqueta++;
            mapaUsuarios.put(etiquetaUsuario, nombreUsuario);
            
            // Validar y preprocesar imágenes
            List<BufferedImage> imagenesValidas = validarImagenesFaciales(imagenesFaciales);
            if (imagenesValidas.size() < 3) {
                System.err.println("❌ Se necesitan al menos 3 imágenes válidas con rostros detectables");
                return false;
            }
            
            // Crear lista de etiquetas
            List<Integer> etiquetas = new ArrayList<>();
            for (int i = 0; i < imagenesValidas.size(); i++) {
                etiquetas.add(etiquetaUsuario);
            }
            
            // Entrenar modelo
            boolean exito = procesadorOpenCV.entrenarReconocedor(imagenesValidas, etiquetas);
            
            if (exito) {
                System.out.println("✅ Usuario facial registrado exitosamente: " + nombreUsuario + 
                                 " (Etiqueta: " + etiquetaUsuario + ")");
            } else {
                // Revertir si falló
                mapaUsuarios.remove(etiquetaUsuario);
                proximaEtiqueta--;
                System.err.println("❌ Error entrenando modelo para: " + nombreUsuario);
            }
            
            return exito;
            
        } catch (Exception e) {
            System.err.println("❌ Excepción durante registro facial: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 🎯 AUTENTICAR USUARIO FACIAL
     * Reconoce un rostro y devuelve información del usuario
     */
    public ResultadoAutenticacionFacial autenticarUsuarioFacial(BufferedImage imagenRostro) {
        if (!sistemaInicializado || imagenRostro == null) {
            return new ResultadoAutenticacionFacial(false, null, 0.0, "Sistema no inicializado o imagen inválida");
        }

        try {
            System.out.println("🔍 Iniciando autenticación facial...");
            
            // Intentar reconocimiento múltiple para mayor precisión
            ResultadoReconocimiento mejorResultado = null;
            double mejorConfianza = 0.0;
            
            for (int intento = 0; intento < MAX_INTENTOS_DETECCION; intento++) {
                ResultadoReconocimiento resultado = procesadorOpenCV.reconocerRostro(imagenRostro);
                
                if (resultado.esReconocido() && resultado.getConfianza() > mejorConfianza) {
                    mejorResultado = resultado;
                    mejorConfianza = resultado.getConfianza();
                }
                
                // Si tenemos alta confianza, no necesitamos más intentos
                if (mejorConfianza >= UMBRAL_CONFIANZA_ALTA) {
                    break;
                }
            }
            
            // Evaluar resultado
            if (mejorResultado != null && mejorResultado.esReconocido()) {
                String nombreUsuario = mapaUsuarios.get(mejorResultado.getEtiqueta());
                String nivelConfianza = determinarNivelConfianza(mejorConfianza);
                
                System.out.println("✅ Usuario reconocido: " + nombreUsuario + 
                                 " (Confianza: " + String.format("%.2f", mejorConfianza) + "% - " + nivelConfianza + ")");
                
                return new ResultadoAutenticacionFacial(
                    true, 
                    nombreUsuario, 
                    mejorConfianza,
                    "Autenticación exitosa - " + nivelConfianza
                );
            } else {
                System.out.println("❌ No se pudo reconocer el rostro");
                return new ResultadoAutenticacionFacial(
                    false, 
                    null, 
                    mejorResultado != null ? mejorResultado.getConfianza() : 0.0,
                    "Rostro no reconocido"
                );
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error durante autenticación facial: " + e.getMessage());
            return new ResultadoAutenticacionFacial(false, null, 0.0, "Error del sistema: " + e.getMessage());
        }
    }

    /**
     * 🔍 DETECTAR ROSTROS EN IMAGEN
     * Detecta y cuenta rostros en una imagen
     */
    public InformacionDeteccionRostros detectarRostrosEnImagen(BufferedImage imagen) {
        if (!sistemaInicializado || imagen == null) {
            return new InformacionDeteccionRostros(0, false, "Sistema no inicializado");
        }

        try {
            List<Rect> rostrosDetectados = procesadorOpenCV.detectarRostros(imagen);
            
            boolean hayRostros = !rostrosDetectados.isEmpty();
            String mensaje = hayRostros ? 
                "Detectados " + rostrosDetectados.size() + " rostro(s)" : 
                "No se detectaron rostros";
            
            return new InformacionDeteccionRostros(rostrosDetectados.size(), hayRostros, mensaje);
            
        } catch (Exception e) {
            return new InformacionDeteccionRostros(0, false, "Error en detección: " + e.getMessage());
        }
    }

    /**
     * 🔄 CAPTURAR Y PROCESAR DESDE CÁMARA
     * Captura una imagen desde la cámara y la procesa
     */
    public BufferedImage capturarYProcesarDesdeCamara() {
        try {
            if (!manejadorCamara.inicializarCamara()) {
                System.err.println("❌ No se pudo inicializar la cámara");
                return null;
            }
            
            BufferedImage imagenCapturada = manejadorCamara.capturarImagenBuffered();
            if (imagenCapturada == null) {
                System.err.println("❌ No se pudo capturar imagen");
                return null;
            }
            
            // Verificar que hay rostros detectables
            InformacionDeteccionRostros info = detectarRostrosEnImagen(imagenCapturada);
            if (!info.hayRostros()) {
                System.out.println("⚠️ Imagen capturada sin rostros detectables");
            }
            
            return imagenCapturada;
            
        } catch (Exception e) {
            System.err.println("❌ Error capturando desde cámara: " + e.getMessage());
            return null;
        }
    }

    /**
     * 🛠️ MÉTODOS AUXILIARES PRIVADOS
     */
    
    /**
     * Validar que las imágenes contengan rostros detectables
     */
    private List<BufferedImage> validarImagenesFaciales(List<BufferedImage> imagenes) {
        List<BufferedImage> imagenesValidas = new ArrayList<>();
        
        for (int i = 0; i < imagenes.size(); i++) {
            BufferedImage imagen = imagenes.get(i);
            if (imagen != null) {
                InformacionDeteccionRostros info = detectarRostrosEnImagen(imagen);
                if (info.hayRostros()) {
                    imagenesValidas.add(imagen);
                    System.out.println("✅ Imagen " + (i + 1) + " válida - " + info.getMensaje());
                } else {
                    System.out.println("⚠️ Imagen " + (i + 1) + " sin rostros detectables");
                }
            }
        }
        
        return imagenesValidas;
    }
    
    /**
     * Determinar nivel de confianza basado en el porcentaje
     */
    private String determinarNivelConfianza(double confianza) {
        if (confianza >= UMBRAL_CONFIANZA_ALTA) {
            return "ALTA CONFIANZA";
        } else if (confianza >= UMBRAL_CONFIANZA_MEDIA) {
            return "CONFIANZA MEDIA";
        } else {
            return "BAJA CONFIANZA";
        }
    }

    /**
     * 📊 ESTADÍSTICAS DEL SISTEMA
     */
    public String obtenerEstadisticasSistema() {
        return String.format(
            "📊 ESTADÍSTICAS INTEGRADOR OPENCV\\n" +
            "Sistema inicializado: %s\\n" +
            "Usuarios registrados: %d\\n" +
            "Próxima etiqueta: %d\\n" +
            "Estado ProcesadorOpenCV: %s",
            sistemaInicializado ? "✅ SÍ" : "❌ NO",
            mapaUsuarios.size(),
            proximaEtiqueta,
            procesadorOpenCV.isInicializado() ? "✅ OK" : "❌ ERROR"
        );
    }

    /**
     * 🧹 LIMPIAR RECURSOS
     */
    public void liberarRecursos() {
        try {
            if (procesadorOpenCV != null) {
                procesadorOpenCV.liberarRecursos();
            }
            
            if (manejadorCamara != null) {
                manejadorCamara.liberarCamara();
            }
            
            mapaUsuarios.clear();
            System.out.println("🧹 Recursos IntegradorOpenCV liberados");
            
        } catch (Exception e) {
            System.err.println("⚠️ Error liberando recursos IntegradorOpenCV: " + e.getMessage());
        }
    }

    // ========== CLASES DE RESULTADO ==========
    
    /**
     * 📋 RESULTADO DE AUTENTICACIÓN FACIAL
     */
    public static class ResultadoAutenticacionFacial {
        private final boolean autenticado;
        private final String nombreUsuario;
        private final double confianza;
        private final String mensaje;
        
        public ResultadoAutenticacionFacial(boolean autenticado, String nombreUsuario, double confianza, String mensaje) {
            this.autenticado = autenticado;
            this.nombreUsuario = nombreUsuario;
            this.confianza = confianza;
            this.mensaje = mensaje;
        }
        
        public boolean isAutenticado() { return autenticado; }
        public String getNombreUsuario() { return nombreUsuario; }
        public double getConfianza() { return confianza; }
        public String getMensaje() { return mensaje; }
        
        @Override
        public String toString() {
            return String.format("ResultadoAutenticacionFacial{autenticado=%s, usuario='%s', confianza=%.2f%%, mensaje='%s'}",
                               autenticado, nombreUsuario, confianza, mensaje);
        }
    }
    
    /**
     * 📊 INFORMACIÓN DE DETECCIÓN DE ROSTROS
     */
    public static class InformacionDeteccionRostros {
        private final int cantidadRostros;
        private final boolean hayRostros;
        private final String mensaje;
        
        public InformacionDeteccionRostros(int cantidadRostros, boolean hayRostros, String mensaje) {
            this.cantidadRostros = cantidadRostros;
            this.hayRostros = hayRostros;
            this.mensaje = mensaje;
        }
        
        public int getCantidadRostros() { return cantidadRostros; }
        public boolean hayRostros() { return hayRostros; }
        public String getMensaje() { return mensaje; }
        
        @Override
        public String toString() {
            return String.format("DeteccionRostros{cantidad=%d, hay=%s, mensaje='%s'}",
                               cantidadRostros, hayRostros, mensaje);
        }
    }

    // ========== GETTERS ==========
    public boolean isSistemaInicializado() { return sistemaInicializado; }
    public int getCantidadUsuariosRegistrados() { return mapaUsuarios.size(); }
}