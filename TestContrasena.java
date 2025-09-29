import com.reconocimiento.facial.seguridad.CifradorContrasenas;

public class TestContrasena {
    public static void main(String[] args) {
        CifradorContrasenas cifrador = new CifradorContrasenas();
        String contrasena = "Leo2005.";
        
        System.out.println("Probando contraseña: " + contrasena);
        
        CifradorContrasenas.ResultadoValidacion resultado = cifrador.validarContrasena(contrasena);
        
        System.out.println("¿Es válida? " + resultado.esValida());
        
        if (!resultado.esValida()) {
            System.out.println("Errores encontrados:");
            for (String error : resultado.getErrores()) {
                System.out.println("- " + error);
            }
        }
        
        System.out.println("Fuerza de la contraseña: " + cifrador.calcularFuerzaContrasena(contrasena) + "/100");
    }
}