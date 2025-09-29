/**
 * 🧪 SCRIPT DE PRUEBA - REGISTRO DE USUARIO
 * Prueba rápida del nuevo sistema de registro por credenciales
 */

import com.reconocimiento.facial.servicios.ServicioUsuarioMejorado;
import com.reconocimiento.facial.dto.UsuarioDTO;
import java.util.ArrayList;

public class PruebaRegistro {
    
    public static void main(String[] args) {
        System.out.println("🧪 INICIANDO PRUEBA DE REGISTRO");
        System.out.println("===============================");
        
        try {
            // Crear servicio de usuario
            ServicioUsuarioMejorado servicio = new ServicioUsuarioMejorado();
            
            // Crear datos de usuario de prueba
            UsuarioDTO usuario = new UsuarioDTO();
            usuario.setNombreUsuario("usuario_prueba");
            usuario.setNombreCompleto("Usuario de Prueba");
            usuario.setCorreoElectronico("prueba@test.com");
            usuario.setContrasena("TestPass123!");
            
            System.out.println("📝 Datos del usuario:");
            System.out.println("   Usuario: " + usuario.getNombreUsuario());
            System.out.println("   Nombre: " + usuario.getNombreCompleto());
            System.out.println("   Email: " + usuario.getCorreoElectronico());
            System.out.println();
            
            // Intentar registro SIN imágenes faciales
            System.out.println("🔄 Registrando usuario SIN imágenes faciales...");
            boolean exito = servicio.registrarUsuarioCompleto(usuario, new ArrayList<>());
            
            if (exito) {
                System.out.println("✅ REGISTRO EXITOSO");
                System.out.println("   El usuario puede loguearse con credenciales");
            } else {
                System.out.println("❌ REGISTRO FALLIDO");
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error en la prueba: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println();
        System.out.println("🏁 Prueba finalizada");
    }
}