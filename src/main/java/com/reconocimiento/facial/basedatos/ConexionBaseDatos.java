package com.reconocimiento.facial.basedatos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.ArrayList;

/**
 * Gestor de conexiones a la base de datos MySQL
 * Sistema optimizado de pool de conexiones para el reconocimiento facial
 */
public class ConexionBaseDatos {
    private static ConexionBaseDatos instancia;
    private BlockingQueue<Connection> poolConexiones;
    private List<Connection> conexionesUsadas;
    private boolean poolInicializado = false;

    // Configuración de la base de datos
    private static final String DB_URL = "jdbc:mysql://localhost:3306/sistema_reconocimiento_facial";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";
    private static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";
    
    // Configuración del pool
    private static final int POOL_SIZE_MIN = 5;
    private static final int POOL_SIZE_MAX = 20;
    private static final int TIMEOUT_MS = 5000;

    private ConexionBaseDatos() throws SQLException {
        conexionesUsadas = new ArrayList<>();
        inicializarPoolConexiones();
    }

    public static ConexionBaseDatos obtenerInstancia() throws SQLException {
        if (instancia == null) {
            synchronized (ConexionBaseDatos.class) {
                if (instancia == null) {
                    instancia = new ConexionBaseDatos();
                }
            }
        }
        return instancia;
    }

    private void inicializarPoolConexiones() throws SQLException {
        try {
            // Cargar el driver de MySQL
            Class.forName(DB_DRIVER);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver MySQL no encontrado: " + DB_DRIVER, e);
        }

        poolConexiones = new ArrayBlockingQueue<>(POOL_SIZE_MAX);

        // Crear conexiones iniciales
        for (int i = 0; i < POOL_SIZE_MIN; i++) {
            Connection conexion = crearNuevaConexion();
            poolConexiones.offer(conexion);
        }

        poolInicializado = true;
        System.out.println("✅ Pool de conexiones inicializado con " + POOL_SIZE_MIN + " conexiones.");
        System.out.println("📂 Base de datos: sistema_reconocimiento_facial");
    }

    private Connection crearNuevaConexion() throws SQLException {
        try {
            Connection conexion = DriverManager.getConnection(
                DB_URL,
                DB_USER,
                DB_PASSWORD
            );

            // Configurar propiedades de la conexión
            conexion.setAutoCommit(true);
            
            // Configurar encoding UTF-8
            try (Statement stmt = conexion.createStatement()) {
                stmt.execute("SET CHARACTER SET utf8mb4");
                stmt.execute("SET NAMES utf8mb4");
            }

            return conexion;
        } catch (SQLException e) {
            System.err.println("❌ Error creando nueva conexión: " + e.getMessage());
            throw e;
        }
    }

    public Connection obtenerConexion() throws SQLException {
        if (!poolInicializado) {
            throw new SQLException("Pool de conexiones no inicializado");
        }

        try {
            Connection conexion = poolConexiones.poll(TIMEOUT_MS, TimeUnit.MILLISECONDS);

            if (conexion == null) {
                // Si no hay conexiones disponibles, crear una nueva
                if (conexionesUsadas.size() < POOL_SIZE_MAX) {
                    conexion = crearNuevaConexion();
                } else {
                    throw new SQLException("Pool de conexiones agotado. Máximo: " + POOL_SIZE_MAX);
                }
            }

            // Validar si la conexión está activa
            if (conexion == null || conexion.isClosed() || !conexion.isValid(5)) {
                conexion = crearNuevaConexion();
            }

            synchronized (conexionesUsadas) {
                conexionesUsadas.add(conexion);
            }

            return conexion;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SQLException("Timeout obteniendo conexión de la base de datos", e);
        }
    }

    public void liberarConexion(Connection conexion) {
        if (conexion != null) {
            try {
                synchronized (conexionesUsadas) {
                    conexionesUsadas.remove(conexion);
                }

                if (!conexion.isClosed() && conexion.isValid(2)) {
                    // Resetear estado de la conexión
                    conexion.setAutoCommit(true);

                    // Devolver al pool si hay espacio
                    if (!poolConexiones.offer(conexion)) {
                        // Si el pool está lleno, cerrar la conexión
                        conexion.close();
                    }
                } else {
                    conexion.close();
                }
            } catch (SQLException e) {
                System.err.println("Error liberando conexión: " + e.getMessage());
                try {
                    conexion.close();
                } catch (SQLException ex) {
                    System.err.println("Error cerrando conexión: " + ex.getMessage());
                }
            }
        }
    }

    public boolean probarConexion() {
        try (Connection conexion = obtenerConexion()) {
            return conexion != null && !conexion.isClosed() && conexion.isValid(5);
        } catch (SQLException e) {
            System.err.println("Error probando conexión: " + e.getMessage());
            return false;
        }
    }

    public void cerrarTodasLasConexiones() {
        // Cerrar conexiones en uso
        synchronized (conexionesUsadas) {
            for (Connection conexion : conexionesUsadas) {
                try {
                    if (!conexion.isClosed()) {
                        conexion.close();
                    }
                } catch (SQLException e) {
                    System.err.println("Error cerrando conexión en uso: " + e.getMessage());
                }
            }
            conexionesUsadas.clear();
        }

        // Cerrar conexiones en el pool
        while (!poolConexiones.isEmpty()) {
            try {
                Connection conexion = poolConexiones.poll();
                if (conexion != null && !conexion.isClosed()) {
                    conexion.close();
                }
            } catch (SQLException e) {
                System.err.println("Error cerrando conexión del pool: " + e.getMessage());
            }
        }

        poolInicializado = false;
        System.out.println("Todas las conexiones han sido cerradas.");
    }

    public int obtenerConexionesDisponibles() {
        return poolConexiones.size();
    }

    public int obtenerConexionesEnUso() {
        synchronized (conexionesUsadas) {
            return conexionesUsadas.size();
        }
    }

    public void mostrarEstadisticasPool() {
        System.out.println("=== Estadísticas del Pool de Conexiones ===");
        System.out.println("Conexiones disponibles: " + obtenerConexionesDisponibles());
        System.out.println("Conexiones en uso: " + obtenerConexionesEnUso());
        System.out.println("Tamaño máximo del pool: " + POOL_SIZE_MAX);
        System.out.println("Tamaño mínimo del pool: " + POOL_SIZE_MIN);
        System.out.println("Pool inicializado: " + poolInicializado);
        System.out.println("============================================");
    }

    /**
     * Método para probar la conectividad de la base de datos
     */
    public static boolean probarConectividad() {
        try {
            Class.forName(DB_DRIVER);
            try (Connection conexion = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                return conexion.isValid(5);
            }
        } catch (Exception e) {
            System.err.println("❌ Error probando conectividad: " + e.getMessage());
            return false;
        }
    }

    /**
     * Obtener información de la configuración actual
     */
    public String obtenerConfiguracion() {
        return String.format("BD: %s | Usuario: %s | Pool: %d-%d", 
                           DB_URL, DB_USER, POOL_SIZE_MIN, POOL_SIZE_MAX);
    }
}