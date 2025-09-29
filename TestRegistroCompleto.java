import com.reconocimiento.facial.seguridad.CifradorContrasenas;
import com.reconocimiento.facial.servicios.ServicioUsuarioMejorado;
import com.reconocimiento.facial.modelos.Usuario;

public class TestRegistroCompleto {
    public static void main(String[] args) {
        try {
            System.out.println("=== TEST COMPLETO DE REGISTRO ===");
            
            // Test 1: Validación de contraseña
            System.out.println("\n1. Probando CifradorContrasenas...");
            CifradorContrasenas cifrador = new CifradorContrasenas();
            String contrasena = "Leo2005.";
            
            CifradorContrasenas.ResultadoValidacion resultado = cifrador.validarContrasena(contrasena);
            System.out.println("¿Es válida? " + resultado.esValida());
            
            if (!resultado.esValida()) {
                System.out.println("Errores:");
                for (String error : resultado.getErrores()) {
                    System.out.println("- " + error);
                }
                return;
            }
            
            // Test 2: Cifrado de contraseña
            System.out.println("\n2. Cifrando contraseña...");
            String contrasenaHash = cifrador.cifrarContrasena(contrasena);
            System.out.println("✅ Contraseña cifrada exitosamente");
            
            // Test 3: Crear usuario completo
            System.out.println("\n3. Creando usuario...");
            Usuario usuario = new Usuario();
            usuario.setNombreUsuario("Leo");
            usuario.setNombreCompleto("Leonardo Peña");
            usuario.setCorreoElectronico("leo123@gmail.com");
            usuario.setContrasena(contrasena);
            usuario.setTelefono("75678458");
            
            // Test 4: Registro usando el servicio
            System.out.println("\n4. Registrando usuario con servicio...");
            ServicioUsuarioMejorado servicio = new ServicioUsuarioMejorado();
            
            // Simular registro sin imágenes faciales (solo credenciales)
            java.util.List<byte[]> muestrasVacias = new java.util.ArrayList<>();
            
            boolean resultado_registro = servicio.registrarUsuarioCompleto(usuario, muestrasVacias);
            
            if (resultado_registro) {
                System.out.println("✅ ¡REGISTRO EXITOSO!");
                System.out.println("Usuario '" + usuario.getNombreUsuario() + "' registrado correctamente");
            } else {
                System.out.println("❌ Error en el registro");
            }
            
        } catch (Exception e) {
            System.out.println("💥 Error durante la prueba: " + e.getMessage());
            e.printStackTrace();
        }
    }
}