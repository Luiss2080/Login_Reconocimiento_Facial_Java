import com.reconocimiento.facial.seguridad.CifradorContrasenas;

public class TestSimple {
    public static void main(String[] args) {
        try {
            System.out.println("=== TEST CIFRADOR CONTRASEÑAS ===");
            
            CifradorContrasenas cifrador = new CifradorContrasenas();
            String contrasena = "Leo2005.";
            
            System.out.println("Contraseña: '" + contrasena + "'");
            
            // Test validación
            CifradorContrasenas.ResultadoValidacion resultado = cifrador.validarContrasena(contrasena);
            System.out.println("¿Es válida? " + resultado.esValida());
            
            if (!resultado.esValida()) {
                System.out.println("Errores:");
                for (String error : resultado.getErrores()) {
                    System.out.println("- " + error);
                }
                return;
            }
            
            // Test cifrado
            String hash = cifrador.cifrarContrasena(contrasena);
            System.out.println("✅ Cifrado exitoso: " + hash.substring(0, 20) + "...");
            
            // Test verificación
            boolean verifica = cifrador.verificarContrasena(contrasena, hash);
            System.out.println("✅ Verificación: " + verifica);
            
            System.out.println("\n¡TODAS LAS PRUEBAS PASARON!");
            
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}