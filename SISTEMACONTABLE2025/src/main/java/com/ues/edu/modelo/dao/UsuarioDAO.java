package com.ues.edu.modelo.dao;

import com.ues.edu.conexion.Conexion;
import com.ues.edu.modelo.Roles;
import com.ues.edu.modelo.Usuario;
import com.ues.edu.modelo.Encriptar;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    private final Conexion conexion;

    public UsuarioDAO() {
        this.conexion = new Conexion();
    }

    // ==================== SQL ====================
    private static final String SQL_LISTAR =
            "SELECT u.id, u.nombre, u.usuario, u.contraseña, " +
            "       r.idrol, r.nombre AS rol_nombre " +
            "FROM public.usuario u " +
            "JOIN public.roles r ON r.idrol = u.idrol " +
            "ORDER BY u.id";

    private static final String SQL_OBTENER =
            "SELECT u.id, u.nombre, u.usuario, u.contraseña, " +
            "       r.idrol, r.nombre AS rol_nombre " +
            "FROM public.usuario u " +
            "JOIN public.roles r ON r.idrol = u.idrol " +
            "WHERE u.id = ?";

    private static final String SQL_INSERTAR =
            "INSERT INTO public.usuario(nombre, usuario, contraseña, idrol) " +
            "VALUES (?, ?, ?, ?)";

    private static final String SQL_ACTUALIZAR =
            "UPDATE public.usuario " +
            "SET nombre = ?, usuario = ?, idrol = ? " +
            "WHERE id = ?";

    private static final String SQL_ELIMINAR =
            "DELETE FROM public.usuario WHERE id = ?";

    private static final String SQL_LISTAR_ROLES =
            "SELECT idrol, nombre FROM public.roles ORDER BY nombre";

    private static final String SQL_EXISTE_USUARIO =
            "SELECT 1 FROM public.usuario WHERE LOWER(usuario) = LOWER(?)";

    private static final String SQL_EXISTE_USUARIO_OTRO =
            "SELECT 1 FROM public.usuario WHERE LOWER(usuario) = LOWER(?) AND id <> ?";

    private static final String SQL_LOGIN =
            "SELECT u.id, u.nombre, u.usuario, u.contraseña, " +
            "       r.idrol, r.nombre AS rol_nombre " +
            "FROM public.usuario u " +
            "JOIN public.roles r ON r.idrol = u.idrol " +
            "WHERE LOWER(u.usuario) = LOWER(?) AND u.contraseña = ?";

    // ==================== Helpers ====================

    private boolean existeUsuario(String usuario) throws SQLException {
        try (Connection c = conexion.getConexion();
             PreparedStatement ps = c.prepareStatement(SQL_EXISTE_USUARIO)) {
            ps.setString(1, usuario);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private boolean existeUsuarioEnOtro(String usuario, int id) throws SQLException {
        try (Connection c = conexion.getConexion();
             PreparedStatement ps = c.prepareStatement(SQL_EXISTE_USUARIO_OTRO)) {
            ps.setString(1, usuario);
            ps.setInt(2, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    // ==================== CRUD ====================

    public List<Usuario> listar() throws SQLException {
        List<Usuario> lista = new ArrayList<>();
        try (Connection c = conexion.getConexion();
             PreparedStatement ps = c.prepareStatement(SQL_LISTAR);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Usuario u = new Usuario();
                u.setId(rs.getInt("id"));
                u.setNombre(rs.getString("nombre"));
                u.setUsuario(rs.getString("usuario"));
                u.setContraseña(rs.getString("contraseña"));

                Roles rol = new Roles();
                rol.setIdRol(rs.getInt("idrol"));
                rol.setNombre(rs.getString("rol_nombre"));
                u.setRol(rol);

                lista.add(u);
            }
        }
        return lista;
    }

    public Usuario obtenerPorId(int id) throws SQLException {
        Usuario u = null;
        try (Connection c = conexion.getConexion();
             PreparedStatement ps = c.prepareStatement(SQL_OBTENER)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    u = new Usuario();
                    u.setId(rs.getInt("id"));
                    u.setNombre(rs.getString("nombre"));
                    u.setUsuario(rs.getString("usuario"));
                    u.setContraseña(rs.getString("contraseña"));

                    Roles rol = new Roles();
                    rol.setIdRol(rs.getInt("idrol"));
                    rol.setNombre(rs.getString("rol_nombre"));
                    u.setRol(rol);
                }
            }
        }
        return u;
    }

    /**
     * Inserta usuario con contraseña YA encriptada.
     * Devuelve:
     *   "ok"
     *   "duplicado_usuario"
     *   "error_sql"
     */
    public String insertar(Usuario u) throws SQLException {
        if (existeUsuario(u.getUsuario())) {
            return "duplicado_usuario";
        }

        try (Connection c = conexion.getConexion();
             PreparedStatement ps = c.prepareStatement(SQL_INSERTAR)) {
            ps.setString(1, u.getNombre());
            ps.setString(2, u.getUsuario());
            ps.setString(3, u.getContraseña());
            ps.setInt(4, u.getRol().getIdRol());

            int filas = ps.executeUpdate();
            return (filas > 0) ? "ok" : "error_sql";
        } catch (SQLException e) {
            e.printStackTrace();
            return "error_sql";
        }
    }

    /**
     * Actualiza (no cambia contraseña).
     * Devuelve:
     *   "ok"
     *   "duplicado_usuario"
     *   "error_sql"
     */
    public String actualizar(Usuario u) throws SQLException {
        if (existeUsuarioEnOtro(u.getUsuario(), u.getId())) {
            return "duplicado_usuario";
        }

        try (Connection c = conexion.getConexion();
             PreparedStatement ps = c.prepareStatement(SQL_ACTUALIZAR)) {
            ps.setString(1, u.getNombre());
            ps.setString(2, u.getUsuario());
            ps.setInt(3, u.getRol().getIdRol());
            ps.setInt(4, u.getId());

            int filas = ps.executeUpdate();
            return (filas > 0) ? "ok" : "error_sql";
        } catch (SQLException e) {
            e.printStackTrace();
            return "error_sql";
        }
    }

    public boolean eliminar(int id) throws SQLException {
        try (Connection c = conexion.getConexion();
             PreparedStatement ps = c.prepareStatement(SQL_ELIMINAR)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public List<Roles> listarRoles() throws SQLException {
        List<Roles> roles = new ArrayList<>();
        try (Connection c = conexion.getConexion();
             PreparedStatement ps = c.prepareStatement(SQL_LISTAR_ROLES);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Roles r = new Roles();
                r.setIdRol(rs.getInt("idrol"));
                r.setNombre(rs.getString("nombre"));
                roles.add(r);
            }
        }
        return roles;
    }

    // ==================== LOGIN ====================

    /**
     * Autentica por usuario y contraseña en texto plano.
     * Devuelve el Usuario con su rol si es válido, o null si falla.
     */
    public Usuario autenticar(String usuario, String passwordPlano) throws SQLException {
        String hash = Encriptar.getStringMessageDigest(passwordPlano, Encriptar.SHA256);

        try (Connection c = conexion.getConexion();
             PreparedStatement ps = c.prepareStatement(SQL_LOGIN)) {
            ps.setString(1, usuario);
            ps.setString(2, hash);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Usuario u = new Usuario();
                    u.setId(rs.getInt("id"));
                    u.setNombre(rs.getString("nombre"));
                    u.setUsuario(rs.getString("usuario"));
                    u.setContraseña(null); // no exponemos hash

                    Roles rol = new Roles();
                    rol.setIdRol(rs.getInt("idrol"));
                    rol.setNombre(rs.getString("rol_nombre"));
                    u.setRol(rol);

                    return u;
                }
            }
        }
        return null;
    }
}
