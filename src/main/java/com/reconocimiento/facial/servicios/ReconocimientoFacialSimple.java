package com.reconocimiento.facial.servicios;

import com.reconocimiento.facial.basedatos.ConexionBaseDatos;
import com.reconocimiento.facial.modelos.Usuario;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.sql.*;
import java.util.Optional;

/**
 * Servicio simplificado para reconocimiento facial por comparación de imágenes
 */
public class ReconocimientoFacialSimple {
    
    /**
     * Autentica un usuario comparando la imagen actual con las guardadas en la base de datos
     */
    public Optional<Usuario> autenticarPorImagen(BufferedImage imagenActual) {
        try {
            System.out.println("🔍 Comparando imagen con fotos guardadas en base de datos...");
            
            if (imagenActual == null) {
                System.err.println("❌ Imagen actual es null");
                return Optional.empty();
            }
            
            // Obtener todas las imágenes faciales de la base de datos
            Connection conexion = ConexionBaseDatos.obtenerInstancia().obtenerConexion();
            String sql = "SELECT cf.usuario_id, cf.imagen_facial, cf.hash_facial, u.nombre_usuario, u.nombre_completo " +
                        "FROM caracteristicas_faciales cf " +
                        "JOIN usuarios u ON cf.usuario_id = u.id " +
                        "WHERE cf.activo = TRUE AND cf.imagen_facial IS NOT NULL " +
                        "ORDER BY cf.usuario_id, cf.numero_muestra";
            
            PreparedStatement stmt = conexion.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            
            double mejorSimilitud = 0.0;
            Usuario usuarioCoincidente = null;
            String hashCoincidente = "";
            int imagenesProcesadas = 0;
            
            while (rs.next()) {
                try {
                    imagenesProcesadas++;
                    
                    // Obtener imagen guardada
                    byte[] imagenBytes = rs.getBytes("imagen_facial");
                    if (imagenBytes == null || imagenBytes.length == 0) {
                        System.out.println("⚠️ Imagen vacía para usuario: " + rs.getString("nombre_usuario"));
                        continue;
                    }
                    
                    // Convertir bytes a BufferedImage
                    ByteArrayInputStream bis = new ByteArrayInputStream(imagenBytes);
                    BufferedImage imagenGuardada = javax.imageio.ImageIO.read(bis);
                    
                    if (imagenGuardada == null) {
                        System.out.println("⚠️ No se pudo leer imagen para usuario: " + rs.getString("nombre_usuario"));
                        continue;
                    }
                    
                    // Comparar imágenes usando método simple
                    double similitud = compararImagenesSimple(imagenActual, imagenGuardada);
                    
                    String nombreUsuario = rs.getString("nombre_usuario");
                    String hashFacial = rs.getString("hash_facial");
                    
                    System.out.println("📊 Usuario: " + nombreUsuario + " - Similitud: " + String.format("%.2f%%", similitud * 100));
                    
                    // Si la similitud es mayor al 60% (umbral más permisivo), consideramos que es una coincidencia
                    if (similitud > 0.60 && similitud > mejorSimilitud) {
                        mejorSimilitud = similitud;
                        hashCoincidente = hashFacial;
                        
                        // Crear objeto Usuario
                        usuarioCoincidente = new Usuario();
                        usuarioCoincidente.setIdUsuario(rs.getInt("usuario_id"));
                        usuarioCoincidente.setNombreUsuario(nombreUsuario);
                        usuarioCoincidente.setNombreCompleto(rs.getString("nombre_completo"));
                        usuarioCoincidente.setEstaActivo(true);
                    }
                    
                } catch (Exception e) {
                    System.err.println("⚠️ Error procesando imagen guardada: " + e.getMessage());
                }
            }
            
            rs.close();
            stmt.close();
            
            System.out.println("📈 Procesadas " + imagenesProcesadas + " imágenes guardadas");
            
            if (usuarioCoincidente != null) {
                System.out.println("✅ ¡COINCIDENCIA ENCONTRADA!");
                System.out.println("   👤 Usuario: " + usuarioCoincidente.getNombreUsuario());
                System.out.println("   📊 Similitud: " + String.format("%.2f%%", mejorSimilitud * 100));
                System.out.println("   🔗 Hash: " + hashCoincidente);
                return Optional.of(usuarioCoincidente);
            } else {
                System.out.println("❌ No se encontró ninguna coincidencia suficiente (umbral: 60%)");
                return Optional.empty();
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error comparando con imágenes guardadas: " + e.getMessage());
            e.printStackTrace();
            return Optional.empty();
        }
    }
    
    /**
     * Método simple de comparación de imágenes basado en diferencia de píxeles
     */
    private double compararImagenesSimple(BufferedImage img1, BufferedImage img2) {
        try {
            // Redimensionar ambas imágenes al mismo tamaño para comparación justa
            int ancho = 64;
            int alto = 64;
            
            BufferedImage img1Resized = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);
            BufferedImage img2Resized = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);
            
            java.awt.Graphics2D gfx1 = img1Resized.createGraphics();
            gfx1.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION, java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            gfx1.drawImage(img1, 0, 0, ancho, alto, null);
            gfx1.dispose();
            
            java.awt.Graphics2D gfx2 = img2Resized.createGraphics();
            gfx2.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION, java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            gfx2.drawImage(img2, 0, 0, ancho, alto, null);
            gfx2.dispose();
            
            // Comparar píxel por píxel usando una métrica más permisiva
            long diferenciaTotal = 0;
            long pixelesTotales = ancho * alto;
            
            for (int x = 0; x < ancho; x++) {
                for (int y = 0; y < alto; y++) {
                    int rgb1 = img1Resized.getRGB(x, y);
                    int rgb2 = img2Resized.getRGB(x, y);
                    
                    // Extraer componentes RGB
                    int r1 = (rgb1 >> 16) & 0xFF;
                    int g1 = (rgb1 >> 8) & 0xFF;
                    int b1 = rgb1 & 0xFF;
                    
                    int r2 = (rgb2 >> 16) & 0xFF;
                    int g2 = (rgb2 >> 8) & 0xFF;
                    int b2 = rgb2 & 0xFF;
                    
                    // Calcular diferencia absoluta (más simple y permisivo)
                    int diffR = Math.abs(r1 - r2);
                    int diffG = Math.abs(g1 - g2);
                    int diffB = Math.abs(b1 - b2);
                    
                    long diff = diffR + diffG + diffB;
                    diferenciaTotal += diff;
                }
            }
            
            // Calcular similitud (0.0 = muy diferente, 1.0 = idéntico)
            double diferenciaMaxima = pixelesTotales * 765.0; // 765 = 255 * 3 (máxima diferencia por píxel)
            double diferenciaNormalizada = (double) diferenciaTotal / diferenciaMaxima;
            double similitud = 1.0 - diferenciaNormalizada;
            
            return Math.max(0.0, Math.min(1.0, similitud)); // Asegurar que esté entre 0 y 1
            
        } catch (Exception e) {
            System.err.println("⚠️ Error comparando imágenes: " + e.getMessage());
            return 0.0;
        }
    }
}