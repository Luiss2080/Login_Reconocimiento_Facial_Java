package com.reconocimiento.facial.utilidades;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * 🔍 PROBADOR DE CONEXIÓN A BASE DE DATOS
 * Utilidad para verificar la conexión antes de ejecutar la aplicación principal
 */
public class ProbadorConexion {

    public static void main(String[] args) {
        System.out.println("===========================================");
        System.out.println("🔍 PROBADOR DE CONEXIÓN A BASE DE DATOS");
        System.out.println("===========================================");
        
        // Configuraciones por defecto
        String host = "localhost";
        String puerto = "3307";
        String baseDatos = "sistema_reconocimiento_facial";
        String usuario = "root";
        String password = ""; // Cambiar por tu contraseña
        
        // Si se pasan argumentos, usarlos
        if (args.length >= 1) usuario = args[0];
        if (args.length >= 2) password = args[1];
        if (args.length >= 3) host = args[2];
        if (args.length >= 4) puerto = args[3];
        
        String url = String.format("jdbc:mysql://%s:%s/%s?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC", 
                                  host, puerto, baseDatos);
        
        System.out.println("📋 Configuración:");
        System.out.println("   Host: " + host);
        System.out.println("   Puerto: " + puerto);
        System.out.println("   Base de datos: " + baseDatos);
        System.out.println("   Usuario: " + usuario);
        System.out.println("   Password: " + (password.isEmpty() ? "(vacía)" : "***"));
        System.out.println("   URL: " + url);
        System.out.println();
        
        try {
            System.out.println("🔄 Intentando conectar...");
            
            // Cargar driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("✅ Driver MySQL cargado correctamente");
            
            // Conectar
            Connection conn = DriverManager.getConnection(url, usuario, password);
            System.out.println("✅ Conexión establecida exitosamente!");
            
            // Probar consulta simple
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as total_tablas FROM information_schema.tables WHERE table_schema = '" + baseDatos + "'");
            
            if (rs.next()) {
                int totalTablas = rs.getInt("total_tablas");
                System.out.println("✅ Base de datos encontrada con " + totalTablas + " tablas");
                
                if (totalTablas >= 5) {
                    System.out.println("✅ Todas las tablas del sistema están presentes");
                    
                    // Verificar tablas específicas
                    String[] tablasRequeridas = {"usuarios", "caracteristicas_faciales", "intentos_acceso", 
                                                "auditoria_eventos", "configuracion_sistema"};
                    
                    for (String tabla : tablasRequeridas) {
                        ResultSet rsTabla = stmt.executeQuery("SHOW TABLES LIKE '" + tabla + "'");
                        if (rsTabla.next()) {
                            System.out.println("   ✅ Tabla '" + tabla + "' encontrada");
                        } else {
                            System.out.println("   ❌ Tabla '" + tabla + "' NO encontrada");
                        }
                        rsTabla.close();
                    }
                } else {
                    System.out.println("⚠️  Faltan tablas. Ejecuta el script database.sql en MySQL Workbench");
                }
            }
            
            rs.close();
            stmt.close();
            conn.close();
            
            System.out.println();
            System.out.println("🎉 CONEXIÓN EXITOSA - El sistema está listo!");
            
        } catch (ClassNotFoundException e) {
            System.err.println("❌ ERROR: Driver MySQL no encontrado");
            System.err.println("   Asegúrate de que mysql-connector-java esté en el classpath");
            System.err.println("   Detalle: " + e.getMessage());
        } catch (java.sql.SQLException e) {
            System.err.println("❌ ERROR DE CONEXIÓN SQL:");
            System.err.println("   Código: " + e.getErrorCode());
            System.err.println("   Mensaje: " + e.getMessage());
            System.err.println();
            System.err.println("💡 Posibles soluciones:");
            System.err.println("   1. Verifica que MySQL Server esté ejecutándose");
            System.err.println("   2. Confirma usuario y contraseña");
            System.err.println("   3. Asegúrate de que la base de datos '" + baseDatos + "' existe");
            System.err.println("   4. Verifica que el puerto " + puerto + " esté correcto");
        } catch (Exception e) {
            System.err.println("❌ ERROR INESPERADO: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("===========================================");
    }
}