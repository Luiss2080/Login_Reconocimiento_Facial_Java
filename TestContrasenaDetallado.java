import com.reconocimiento.facial.seguridad.CifradorContrasenas;
import java.lang.reflect.Method;

public class TestContrasenaDetallado {
    public static void main(String[] args) {
        try {
            CifradorContrasenas cifrador = new CifradorContrasenas();
            String contrasena = "Leo2005.";
            
            System.out.println("=== ANÁLISIS DETALLADO DE CONTRASEÑA ===");
            System.out.println("Contraseña: '" + contrasena + "'");
            System.out.println("Longitud: " + contrasena.length());
            
            // Test validación completa
            CifradorContrasenas.ResultadoValidacion resultado = cifrador.validarContrasena(contrasena);
            System.out.println("\n=== VALIDACIÓN COMPLETA ===");
            System.out.println("¿Es válida? " + resultado.esValida());
            
            if (!resultado.esValida()) {
                System.out.println("Errores:");
                for (String error : resultado.getErrores()) {
                    System.out.println("- " + error);
                }
            }
            
            // Test de fuerza
            int fuerza = cifrador.calcularFuerzaContrasena(contrasena);
            System.out.println("Fuerza: " + fuerza + "/100");
            
            // Test específico de contraseña común usando reflexión
            try {
                Method metodoEsComun = CifradorContrasenas.class.getDeclaredMethod("esContrasenaComun", String.class);
                metodoEsComun.setAccessible(true);
                boolean esComun = (Boolean) metodoEsComun.invoke(cifrador, contrasena);
                System.out.println("¿Es contraseña común? " + esComun);
                
                // Test con variaciones
                String contrasenaLower = contrasena.toLowerCase();
                System.out.println("Contraseña en minúsculas: '" + contrasenaLower + "'");
                
                // Test patrones específicos
                System.out.println("¿Contiene '123'? " + contrasenaLower.matches(".*123.*"));
                System.out.println("¿Contiene 'abc'? " + contrasenaLower.matches(".*abc.*"));
                
                // Test palabras comunes
                String[] contrasenasComunes = {
                    "password", "123456", "12345678", "qwerty", "abc123",
                    "password123", "admin", "letmein", "welcome", "monkey",
                    "dragon", "master", "shadow", "superman", "michael",
                    "football", "baseball", "soccer", "charlie", "jordan"
                };
                
                System.out.println("\n=== TEST PALABRAS COMUNES ===");
                for (String comun : contrasenasComunes) {
                    if (contrasenaLower.contains(comun)) {
                        System.out.println("¡ENCONTRADA! Contiene: '" + comun + "'");
                    }
                }
                
            } catch (Exception e) {
                System.out.println("Error accediendo al método privado: " + e.getMessage());
            }
            
            // Test intentando cifrar
            System.out.println("\n=== TEST DE CIFRADO ===");
            try {
                String cifrada = cifrador.cifrarContrasena(contrasena);
                System.out.println("✅ Cifrado exitoso: " + cifrada.substring(0, 20) + "...");
            } catch (Exception e) {
                System.out.println("❌ Error en cifrado: " + e.getMessage());
                if (e.getCause() != null) {
                    System.out.println("Causa: " + e.getCause().getMessage());
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}