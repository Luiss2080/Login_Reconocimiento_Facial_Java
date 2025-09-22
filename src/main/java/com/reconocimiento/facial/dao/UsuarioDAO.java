package com.reconocimiento.facial.dao;

import com.reconocimiento.facial.basedatos.ConexionBaseDatos;
import com.reconocimiento.facial.excepciones.ExcepcionBaseDatos;
import com.reconocimiento.facial.modelos.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object para la entidad Usuario
 * Maneja todas las operaciones de base de datos relacionadas con usuarios
 */
public class UsuarioDAO {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioDAO.class);

    private final ConexionBaseDatos conexionBaseDatos;

    // Consultas SQL preparadas
    private static final String SQL_INSERTAR_USUARIO =
        "INSERT INTO usuarios (nombre_usuario, correo_electronico, contrasena_cifrada, nombre_completo, " +
        "fecha_creacion, fecha_actualizacion, esta_activo) VALUES (?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_BUSCAR_POR_ID =
        "SELECT * FROM usuarios WHERE id_usuario = ?";

    private static final String SQL_BUSCAR_POR_NOMBRE_USUARIO =
        "SELECT * FROM usuarios WHERE nombre_usuario = ?";

    private static final String SQL_BUSCAR_POR_CORREO =
        "SELECT * FROM usuarios WHERE correo_electronico = ?";

    private static final String SQL_ACTUALIZAR_USUARIO =
        "UPDATE usuarios SET nombre_usuario = ?, correo_electronico = ?, contrasena_cifrada = ?, " +
        "nombre_completo = ?, fecha_actualizacion = ?, esta_activo = ?, ultimo_acceso = ? WHERE id_usuario = ?";

    private static final String SQL_OBTENER_USUARIOS_ACTIVOS =
        "SELECT * FROM usuarios WHERE esta_activo = TRUE ORDER BY nombre_completo";

    private static final String SQL_OBTENER_TODOS_USUARIOS =
        "SELECT * FROM usuarios ORDER BY fecha_creacion DESC";

    private static final String SQL_CONTAR_USUARIOS =
        "SELECT COUNT(*) FROM usuarios WHERE esta_activo = TRUE";

    private static final String SQL_EXISTE_USUARIO =
        "SELECT COUNT(*) FROM usuarios WHERE nombre_usuario = ?";

    private static final String SQL_EXISTE_CORREO =
        "SELECT COUNT(*) FROM usuarios WHERE correo_electronico = ?";

    private static final String SQL_BUSCAR_USUARIOS_POR_PATRON =
        "SELECT * FROM usuarios WHERE (nombre_usuario LIKE ? OR nombre_completo LIKE ? OR correo_electronico LIKE ?) " +
        "AND esta_activo = TRUE ORDER BY nombre_completo";

    private static final String SQL_ACTUALIZAR_ULTIMO_ACCESO =
        "UPDATE usuarios SET ultimo_acceso = ? WHERE id_usuario = ?";

    public UsuarioDAO() {
        try {
            this.conexionBaseDatos = ConexionBaseDatos.obtenerInstancia();
        } catch (SQLException e) {
            logger.error("Error inicializando UsuarioDAO: {}", e.getMessage());
            throw new RuntimeException("No se pudo inicializar UsuarioDAO", e);
        }
    }

    /**
     * Guarda un nuevo usuario en la base de datos
     */
    public Usuario guardarUsuario(Usuario usuario) throws ExcepcionBaseDatos {
        logger.debug("Guardando usuario: {}", usuario.getNombreUsuario());

        Connection conexion = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            conexion = conexionBaseDatos.obtenerConexion();
            statement = conexion.prepareStatement(SQL_INSERTAR_USUARIO, Statement.RETURN_GENERATED_KEYS);

            statement.setString(1, usuario.getNombreUsuario());
            statement.setString(2, usuario.getCorreoElectronico());
            statement.setString(3, usuario.getContrasenaCifrada());
            statement.setString(4, usuario.getNombreCompleto());
            statement.setTimestamp(5, Timestamp.valueOf(usuario.getFechaCreacion()));
            statement.setTimestamp(6, Timestamp.valueOf(usuario.getFechaActualizacion()));
            statement.setBoolean(7, usuario.isEstaActivo());

            int filasAfectadas = statement.executeUpdate();

            if (filasAfectadas == 0) {
                throw new ExcepcionBaseDatos("No se pudo insertar el usuario");
            }

            resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                usuario.setIdUsuario(resultSet.getInt(1));
            }

            logger.info("Usuario guardado exitosamente con ID: {}", usuario.getIdUsuario());
            return usuario;

        } catch (SQLException e) {
            logger.error("Error guardando usuario {}: {}", usuario.getNombreUsuario(), e.getMessage());
            throw new ExcepcionBaseDatos("Error al guardar usuario", e);
        } finally {
            cerrarRecursos(conexion, statement, resultSet);
        }
    }

    /**
     * Busca un usuario por su ID
     */
    public Optional<Usuario> buscarPorId(int idUsuario) throws ExcepcionBaseDatos {
        logger.debug("Buscando usuario por ID: {}", idUsuario);

        Connection conexion = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            conexion = conexionBaseDatos.obtenerConexion();
            statement = conexion.prepareStatement(SQL_BUSCAR_POR_ID);
            statement.setInt(1, idUsuario);

            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                Usuario usuario = mapearResultSetAUsuario(resultSet);
                logger.debug("Usuario encontrado: {}", usuario.getNombreUsuario());
                return Optional.of(usuario);
            }

            logger.debug("Usuario no encontrado con ID: {}", idUsuario);
            return Optional.empty();

        } catch (SQLException e) {
            logger.error("Error buscando usuario por ID {}: {}", idUsuario, e.getMessage());
            throw new ExcepcionBaseDatos("Error al buscar usuario por ID", e);
        } finally {
            cerrarRecursos(conexion, statement, resultSet);
        }
    }

    /**
     * Busca un usuario por su nombre de usuario
     */
    public Optional<Usuario> buscarPorNombreUsuario(String nombreUsuario) throws ExcepcionBaseDatos {
        logger.debug("Buscando usuario por nombre: {}", nombreUsuario);

        Connection conexion = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            conexion = conexionBaseDatos.obtenerConexion();
            statement = conexion.prepareStatement(SQL_BUSCAR_POR_NOMBRE_USUARIO);
            statement.setString(1, nombreUsuario);

            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                Usuario usuario = mapearResultSetAUsuario(resultSet);
                logger.debug("Usuario encontrado por nombre: {}", nombreUsuario);
                return Optional.of(usuario);
            }

            logger.debug("Usuario no encontrado con nombre: {}", nombreUsuario);
            return Optional.empty();

        } catch (SQLException e) {
            logger.error("Error buscando usuario por nombre {}: {}", nombreUsuario, e.getMessage());
            throw new ExcepcionBaseDatos("Error al buscar usuario por nombre", e);
        } finally {
            cerrarRecursos(conexion, statement, resultSet);
        }
    }

    /**
     * Busca un usuario por su correo electrónico
     */
    public Optional<Usuario> buscarPorCorreo(String correoElectronico) throws ExcepcionBaseDatos {
        logger.debug("Buscando usuario por correo: {}", correoElectronico);

        Connection conexion = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            conexion = conexionBaseDatos.obtenerConexion();
            statement = conexion.prepareStatement(SQL_BUSCAR_POR_CORREO);
            statement.setString(1, correoElectronico);

            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                Usuario usuario = mapearResultSetAUsuario(resultSet);
                logger.debug("Usuario encontrado por correo: {}", correoElectronico);
                return Optional.of(usuario);
            }

            logger.debug("Usuario no encontrado con correo: {}", correoElectronico);
            return Optional.empty();

        } catch (SQLException e) {
            logger.error("Error buscando usuario por correo {}: {}", correoElectronico, e.getMessage());
            throw new ExcepcionBaseDatos("Error al buscar usuario por correo", e);
        } finally {
            cerrarRecursos(conexion, statement, resultSet);
        }
    }

    /**
     * Actualiza un usuario existente
     */
    public Usuario actualizarUsuario(Usuario usuario) throws ExcepcionBaseDatos {
        logger.debug("Actualizando usuario: {}", usuario.getNombreUsuario());

        Connection conexion = null;
        PreparedStatement statement = null;

        try {
            conexion = conexionBaseDatos.obtenerConexion();
            statement = conexion.prepareStatement(SQL_ACTUALIZAR_USUARIO);

            statement.setString(1, usuario.getNombreUsuario());
            statement.setString(2, usuario.getCorreoElectronico());
            statement.setString(3, usuario.getContrasenaCifrada());
            statement.setString(4, usuario.getNombreCompleto());
            statement.setTimestamp(5, Timestamp.valueOf(usuario.getFechaActualizacion()));
            statement.setBoolean(6, usuario.isEstaActivo());

            if (usuario.getUltimoAcceso() != null) {
                statement.setTimestamp(7, Timestamp.valueOf(usuario.getUltimoAcceso()));
            } else {
                statement.setNull(7, Types.TIMESTAMP);
            }

            statement.setInt(8, usuario.getIdUsuario());

            int filasAfectadas = statement.executeUpdate();

            if (filasAfectadas == 0) {
                throw new ExcepcionBaseDatos("Usuario no encontrado para actualizar");
            }

            logger.info("Usuario actualizado exitosamente: {}", usuario.getNombreUsuario());
            return usuario;

        } catch (SQLException e) {
            logger.error("Error actualizando usuario {}: {}", usuario.getNombreUsuario(), e.getMessage());
            throw new ExcepcionBaseDatos("Error al actualizar usuario", e);
        } finally {
            cerrarRecursos(conexion, statement, null);
        }
    }

    /**
     * Obtiene todos los usuarios activos
     */
    public List<Usuario> obtenerUsuariosActivos() throws ExcepcionBaseDatos {
        logger.debug("Obteniendo usuarios activos");

        Connection conexion = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        List<Usuario> usuarios = new ArrayList<>();

        try {
            conexion = conexionBaseDatos.obtenerConexion();
            statement = conexion.prepareStatement(SQL_OBTENER_USUARIOS_ACTIVOS);

            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                usuarios.add(mapearResultSetAUsuario(resultSet));
            }

            logger.debug("Encontrados {} usuarios activos", usuarios.size());
            return usuarios;

        } catch (SQLException e) {
            logger.error("Error obteniendo usuarios activos: {}", e.getMessage());
            throw new ExcepcionBaseDatos("Error al obtener usuarios activos", e);
        } finally {
            cerrarRecursos(conexion, statement, resultSet);
        }
    }

    /**
     * Obtiene todos los usuarios (activos e inactivos)
     */
    public List<Usuario> obtenerTodosLosUsuarios() throws ExcepcionBaseDatos {
        logger.debug("Obteniendo todos los usuarios");

        Connection conexion = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        List<Usuario> usuarios = new ArrayList<>();

        try {
            conexion = conexionBaseDatos.obtenerConexion();
            statement = conexion.prepareStatement(SQL_OBTENER_TODOS_USUARIOS);

            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                usuarios.add(mapearResultSetAUsuario(resultSet));
            }

            logger.debug("Encontrados {} usuarios en total", usuarios.size());
            return usuarios;

        } catch (SQLException e) {
            logger.error("Error obteniendo todos los usuarios: {}", e.getMessage());
            throw new ExcepcionBaseDatos("Error al obtener todos los usuarios", e);
        } finally {
            cerrarRecursos(conexion, statement, resultSet);
        }
    }

    /**
     * Busca usuarios por patrón en nombre, usuario o correo
     */
    public List<Usuario> buscarUsuariosPorPatron(String patron) throws ExcepcionBaseDatos {
        logger.debug("Buscando usuarios por patrón: {}", patron);

        Connection conexion = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        List<Usuario> usuarios = new ArrayList<>();

        try {
            conexion = conexionBaseDatos.obtenerConexion();
            statement = conexion.prepareStatement(SQL_BUSCAR_USUARIOS_POR_PATRON);

            String patronBusqueda = "%" + patron + "%";
            statement.setString(1, patronBusqueda);
            statement.setString(2, patronBusqueda);
            statement.setString(3, patronBusqueda);

            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                usuarios.add(mapearResultSetAUsuario(resultSet));
            }

            logger.debug("Encontrados {} usuarios que coinciden con el patrón", usuarios.size());
            return usuarios;

        } catch (SQLException e) {
            logger.error("Error buscando usuarios por patrón {}: {}", patron, e.getMessage());
            throw new ExcepcionBaseDatos("Error al buscar usuarios por patrón", e);
        } finally {
            cerrarRecursos(conexion, statement, resultSet);
        }
    }

    /**
     * Verifica si existe un usuario con el nombre dado
     */
    public boolean existeUsuario(String nombreUsuario) throws ExcepcionBaseDatos {
        Connection conexion = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            conexion = conexionBaseDatos.obtenerConexion();
            statement = conexion.prepareStatement(SQL_EXISTE_USUARIO);
            statement.setString(1, nombreUsuario);

            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }

            return false;

        } catch (SQLException e) {
            logger.error("Error verificando existencia de usuario {}: {}", nombreUsuario, e.getMessage());
            throw new ExcepcionBaseDatos("Error al verificar existencia de usuario", e);
        } finally {
            cerrarRecursos(conexion, statement, resultSet);
        }
    }

    /**
     * Verifica si existe un usuario con el correo dado
     */
    public boolean existeCorreo(String correoElectronico) throws ExcepcionBaseDatos {
        Connection conexion = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            conexion = conexionBaseDatos.obtenerConexion();
            statement = conexion.prepareStatement(SQL_EXISTE_CORREO);
            statement.setString(1, correoElectronico);

            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }

            return false;

        } catch (SQLException e) {
            logger.error("Error verificando existencia de correo {}: {}", correoElectronico, e.getMessage());
            throw new ExcepcionBaseDatos("Error al verificar existencia de correo", e);
        } finally {
            cerrarRecursos(conexion, statement, resultSet);
        }
    }

    /**
     * Cuenta el número total de usuarios activos
     */
    public int contarUsuariosActivos() throws ExcepcionBaseDatos {
        Connection conexion = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            conexion = conexionBaseDatos.obtenerConexion();
            statement = conexion.prepareStatement(SQL_CONTAR_USUARIOS);

            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt(1);
            }

            return 0;

        } catch (SQLException e) {
            logger.error("Error contando usuarios activos: {}", e.getMessage());
            throw new ExcepcionBaseDatos("Error al contar usuarios activos", e);
        } finally {
            cerrarRecursos(conexion, statement, resultSet);
        }
    }

    /**
     * Actualiza solo el último acceso de un usuario
     */
    public void actualizarUltimoAcceso(int idUsuario, LocalDateTime ultimoAcceso) throws ExcepcionBaseDatos {
        Connection conexion = null;
        PreparedStatement statement = null;

        try {
            conexion = conexionBaseDatos.obtenerConexion();
            statement = conexion.prepareStatement(SQL_ACTUALIZAR_ULTIMO_ACCESO);

            statement.setTimestamp(1, Timestamp.valueOf(ultimoAcceso));
            statement.setInt(2, idUsuario);

            statement.executeUpdate();

        } catch (SQLException e) {
            logger.error("Error actualizando último acceso para usuario {}: {}", idUsuario, e.getMessage());
            throw new ExcepcionBaseDatos("Error al actualizar último acceso", e);
        } finally {
            cerrarRecursos(conexion, statement, null);
        }
    }

    // Métodos auxiliares privados

    /**
     * Mapea un ResultSet a un objeto Usuario
     */
    private Usuario mapearResultSetAUsuario(ResultSet resultSet) throws SQLException {
        Usuario usuario = new Usuario();

        usuario.setIdUsuario(resultSet.getInt("id_usuario"));
        usuario.setNombreUsuario(resultSet.getString("nombre_usuario"));
        usuario.setCorreoElectronico(resultSet.getString("correo_electronico"));
        usuario.setContrasenaCifrada(resultSet.getString("contrasena_cifrada"));
        usuario.setNombreCompleto(resultSet.getString("nombre_completo"));
        usuario.setEstaActivo(resultSet.getBoolean("esta_activo"));

        // Manejo de fechas
        Timestamp fechaCreacion = resultSet.getTimestamp("fecha_creacion");
        if (fechaCreacion != null) {
            usuario.setFechaCreacion(fechaCreacion.toLocalDateTime());
        }

        Timestamp fechaActualizacion = resultSet.getTimestamp("fecha_actualizacion");
        if (fechaActualizacion != null) {
            usuario.setFechaActualizacion(fechaActualizacion.toLocalDateTime());
        }

        Timestamp ultimoAcceso = resultSet.getTimestamp("ultimo_acceso");
        if (ultimoAcceso != null) {
            usuario.setUltimoAcceso(ultimoAcceso.toLocalDateTime());
        }

        return usuario;
    }

    /**
     * Cierra los recursos de base de datos de forma segura
     */
    private void cerrarRecursos(Connection conexion, PreparedStatement statement, ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                logger.warn("Error cerrando ResultSet: {}", e.getMessage());
            }
        }

        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                logger.warn("Error cerrando PreparedStatement: {}", e.getMessage());
            }
        }

        if (conexion != null) {
            conexionBaseDatos.liberarConexion(conexion);
        }
    }
}