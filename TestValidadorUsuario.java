import com.reconocimiento.facial.validadores.ValidadorUsuario;
import com.reconocimiento.facial.excepciones.ExcepcionUsuario;

public class TestValidadorUsuario {
    public static void main(String[] args) {
        ValidadorUsuario validador = new ValidadorUsuario();
        String contrasena = "Leo2005.";
        
        System.out.println("=== TEST VALIDADOR USUARIO ===");
        System.out.println("Contraseña: '" + contrasena + "'");
        
        try {
            validador.validarContrasena(contrasena);
            System.out.println("✅ ValidadorUsuario: CONTRASEÑA VÁLIDA");
        } catch (ExcepcionUsuario e) {
            System.out.println("❌ ValidadorUsuario: CONTRASEÑA RECHAZADA");
            System.out.println("Mensaje: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("💥 Error inesperado: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Test con contraseñas que sabemos que funcionan
        String[] contrasenasTest = {
            "MiCasa123",
            "Familia2025", 
            "Trabajo456",
            "Sistema2024"
        };
        
        System.out.println("\n=== TEST CONTRASEÑAS ALTERNATIVAS ===");
        for (String pwd : contrasenasTest) {
            try {
                validador.validarContrasena(pwd);
                System.out.println("✅ '" + pwd + "' - VÁLIDA");
            } catch (ExcepcionUsuario e) {
                System.out.println("❌ '" + pwd + "' - RECHAZADA: " + e.getMessage());
            }
        }
    }
}