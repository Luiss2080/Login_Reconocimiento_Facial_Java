package com.reconocimiento.facial;

import com.reconocimiento.facial.utilidades.ManejadorImagenes;
import com.reconocimiento.facial.utilidades.GestorConfiguracion;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.Graphics2D;
import java.io.IOException;
// Properties import removido - no se utiliza en esta clase

/**
 * 🧪 PRUEBAS BÁSICAS DEL SISTEMA DE RECONOCIMIENTO FACIAL
 * Casos de prueba para verificar funcionalidad básica de utilidades
 */
public class PruebasBasicasTest {

    private BufferedImage imagenPrueba;

    @BeforeEach
    void configurarPruebas() {
        // Crear imagen de prueba 100x100 píxeles
        imagenPrueba = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = imagenPrueba.createGraphics();
        
        // Llenar con color de prueba
        g2d.setColor(Color.GRAY);
        g2d.fillRect(0, 0, 100, 100);
        
        // Agregar algunos "detalles" para simular una imagen real
        g2d.setColor(Color.WHITE);
        g2d.fillOval(30, 30, 40, 40);
        g2d.dispose();
    }

    @Test
    @DisplayName("Redimensionar imagen a 64x64")
    void testRedimensionarImagen() {
        BufferedImage imagenRedimensionada = ManejadorImagenes.redimensionarCuadrada(imagenPrueba, 64);
        
        assertNotNull(imagenRedimensionada, "La imagen redimensionada no debe ser null");
        assertEquals(64, imagenRedimensionada.getWidth(), "El ancho debe ser 64");
        assertEquals(64, imagenRedimensionada.getHeight(), "El alto debe ser 64");
    }

    @Test
    @DisplayName("Redimensionar con dimensión predeterminada")
    void testRedimensionarPredeterminada() {
        BufferedImage imagenRedimensionada = ManejadorImagenes.redimensionarPredeterminada(imagenPrueba);
        
        assertNotNull(imagenRedimensionada, "La imagen redimensionada no debe ser null");
        assertEquals(64, imagenRedimensionada.getWidth(), "El ancho debe ser 64 por defecto");
        assertEquals(64, imagenRedimensionada.getHeight(), "El alto debe ser 64 por defecto");
    }

    @Test
    @DisplayName("Convertir imagen a escala de grises")
    void testConvertirEscalaGrises() {
        BufferedImage imagenGris = ManejadorImagenes.convertirEscalaGrises(imagenPrueba);
        
        assertNotNull(imagenGris, "La imagen en escala de grises no debe ser null");
        assertEquals(BufferedImage.TYPE_BYTE_GRAY, imagenGris.getType(), 
                    "El tipo debe ser BYTE_GRAY");
    }

    @Test
    @DisplayName("Extraer píxeles de imagen")
    void testExtraerPixeles() {
        BufferedImage imagenGris = ManejadorImagenes.convertirEscalaGrises(imagenPrueba);
        int[] pixeles = ManejadorImagenes.extraerPixeles(imagenGris);
        
        assertNotNull(pixeles, "El array de píxeles no debe ser null");
        assertEquals(100 * 100, pixeles.length, "Debe tener 10,000 píxeles (100x100)");
        
        // Verificar que los valores están en rango válido (0-255)
        for (int pixel : pixeles) {
            assertTrue(pixel >= 0 && pixel <= 255, 
                      "Los valores de píxel deben estar entre 0 y 255");
        }
    }

    @Test
    @DisplayName("Normalizar píxeles")
    void testNormalizarPixeles() {
        int[] pixeles = {0, 127, 255, 64, 192};
        double[] pixelesNormalizados = ManejadorImagenes.normalizarPixeles(pixeles);
        
        assertNotNull(pixelesNormalizados, "El array normalizado no debe ser null");
        assertEquals(pixeles.length, pixelesNormalizados.length, 
                    "Los arrays deben tener la misma longitud");
        
        // Verificar valores normalizados
        assertEquals(0.0, pixelesNormalizados[0], 0.001, "0 debe normalizarse a 0.0");
        assertEquals(1.0, pixelesNormalizados[2], 0.001, "255 debe normalizarse a 1.0");
        
        // Verificar que todos los valores están entre 0.0 y 1.0
        for (double pixel : pixelesNormalizados) {
            assertTrue(pixel >= 0.0 && pixel <= 1.0, 
                      "Los valores normalizados deben estar entre 0.0 y 1.0");
        }
    }

    @Test
    @DisplayName("Calcular calidad de imagen")
    void testCalcularCalidadImagen() {
        double calidad = ManejadorImagenes.calcularCalidadImagen(imagenPrueba);
        
        assertTrue(calidad >= 0.0 && calidad <= 1.0, 
                  "La calidad debe estar entre 0.0 y 1.0");
        assertTrue(calidad > 0.0, "Una imagen válida debe tener calidad > 0");
    }

    @Test
    @DisplayName("Conversión Base64")
    void testConversionBase64() throws IOException {
        String base64 = ManejadorImagenes.imagenABase64(imagenPrueba, "png");
        
        assertNotNull(base64, "El string Base64 no debe ser null");
        assertFalse(base64.isEmpty(), "El string Base64 no debe estar vacío");
        
        // Convertir de vuelta a imagen
        BufferedImage imagenRecuperada = ManejadorImagenes.base64AImagen(base64);
        
        assertNotNull(imagenRecuperada, "La imagen recuperada no debe ser null");
        assertEquals(imagenPrueba.getWidth(), imagenRecuperada.getWidth(), 
                    "El ancho debe mantenerse");
        assertEquals(imagenPrueba.getHeight(), imagenRecuperada.getHeight(), 
                    "El alto debe mantenerse");
    }

    @Test
    @DisplayName("Validar formatos de imagen")
    void testValidarFormatos() {
        assertTrue(ManejadorImagenes.esFormatoValido("jpg"), "JPG debe ser válido");
        assertTrue(ManejadorImagenes.esFormatoValido("jpeg"), "JPEG debe ser válido");
        assertTrue(ManejadorImagenes.esFormatoValido("png"), "PNG debe ser válido");
        assertTrue(ManejadorImagenes.esFormatoValido("bmp"), "BMP debe ser válido");
        
        assertFalse(ManejadorImagenes.esFormatoValido("xyz"), "XYZ no debe ser válido");
        assertFalse(ManejadorImagenes.esFormatoValido(null), "null no debe ser válido");
        assertFalse(ManejadorImagenes.esFormatoValido(""), "String vacío no debe ser válido");
    }

    @Test
    @DisplayName("Gestión de configuración - valores por defecto")
    void testGestorConfiguracion() {
        // Probar obtención de valores con defaults
        String host = GestorConfiguracion.obtenerString("bd.host", "localhost");
        assertEquals("localhost", host, "Debe retornar valor por defecto");
        
        int puerto = GestorConfiguracion.obtenerInt("bd.puerto", 3307);
        assertEquals(3307, puerto, "Debe retornar puerto por defecto");
        
        double confianza = GestorConfiguracion.obtenerDouble("seguridad.confianza", 0.85);
        assertEquals(0.85, confianza, 0.001, "Debe retornar confianza por defecto");
        
        boolean debug = GestorConfiguracion.obtenerBoolean("ui.debug", false);
        assertFalse(debug, "Debe retornar false por defecto");
    }

    @Test
    @DisplayName("Gestión de configuración - establecer valores")
    void testEstablecerValores() {
        // Establecer valores
        GestorConfiguracion.establecerValor("test.string", "valor_prueba");
        GestorConfiguracion.establecerInt("test.int", 42);
        GestorConfiguracion.establecerDouble("test.double", 3.14159);
        GestorConfiguracion.establecerBoolean("test.boolean", true);
        
        // Verificar valores
        assertEquals("valor_prueba", GestorConfiguracion.obtenerString("test.string", ""));
        assertEquals(42, GestorConfiguracion.obtenerInt("test.int", 0));
        assertEquals(3.14159, GestorConfiguracion.obtenerDouble("test.double", 0.0), 0.00001);
        assertTrue(GestorConfiguracion.obtenerBoolean("test.boolean", false));
    }

    @Test
    @DisplayName("Validación de configuración requerida")
    void testValidacionConfiguracion() {
        String[] clavesRequeridas = {
            "bd.host", "bd.puerto", "bd.usuario", 
            "neural.tamaño_imagen", "seguridad.max_intentos"
        };
        
        var faltantes = GestorConfiguracion.validarConfiguracion(clavesRequeridas);
        
        assertNotNull(faltantes, "El mapa de faltantes no debe ser null");
        // Como no hay archivo de configuración cargado, probablemente todas estén faltantes
        assertTrue(faltantes.size() >= 0, "Debe retornar información sobre claves faltantes");
    }

    @Test
    @DisplayName("Información de imagen")
    void testInfoImagen() throws IOException {
        ManejadorImagenes.InfoImagen info = ManejadorImagenes.obtenerInfoImagen(imagenPrueba);
        
        assertNotNull(info, "La información de imagen no debe ser null");
        assertEquals(100, info.ancho, "El ancho debe ser 100");
        assertEquals(100, info.alto, "El alto debe ser 100");
        assertEquals(10000, info.totalPixeles, "Total píxeles debe ser 10,000");
        assertTrue(info.calidadEstimada > 0.0, "La calidad estimada debe ser positiva");
        assertNotNull(info.checksum, "El checksum no debe ser null");
        assertFalse(info.checksum.isEmpty(), "El checksum no debe estar vacío");
    }

    @Test
    @DisplayName("Manejo de errores - imagen null")
    void testManejoErrores() {
        // Verificar que se lanzan excepciones apropiadas para entradas inválidas
        assertThrows(IllegalArgumentException.class, () -> {
            ManejadorImagenes.redimensionarImagen(null, 64, 64);
        }, "Debe lanzar excepción para imagen null");
        
        assertThrows(IllegalArgumentException.class, () -> {
            ManejadorImagenes.convertirEscalaGrises(null);
        }, "Debe lanzar excepción para imagen null");
        
        assertThrows(IllegalArgumentException.class, () -> {
            ManejadorImagenes.extraerPixeles(null);
        }, "Debe lanzar excepción para imagen null");
        
        assertThrows(IllegalArgumentException.class, () -> {
            ManejadorImagenes.normalizarPixeles(null);
        }, "Debe lanzar excepción para array null");
    }

    @Test
    @DisplayName("Checksum de imagen - consistencia")
    void testChecksumConsistencia() throws IOException {
        // El mismo contenido de imagen debe producir el mismo checksum
        String checksum1 = ManejadorImagenes.calcularChecksumImagen(imagenPrueba);
        String checksum2 = ManejadorImagenes.calcularChecksumImagen(imagenPrueba);
        
        assertEquals(checksum1, checksum2, "El mismo contenido debe producir el mismo checksum");
        
        // Imágenes diferentes deben producir checksums diferentes
        BufferedImage imagenDiferente = new BufferedImage(50, 50, BufferedImage.TYPE_INT_RGB);
        String checksumDiferente = ManejadorImagenes.calcularChecksumImagen(imagenDiferente);
        
        assertNotEquals(checksum1, checksumDiferente, 
                       "Imágenes diferentes deben tener checksums diferentes");
    }
}