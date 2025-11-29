package com.ues.edu.controladores;

import com.ues.edu.modelo.Encriptar;
import com.ues.edu.modelo.Roles;
import com.ues.edu.modelo.Usuario;
import com.ues.edu.modelo.dao.UsuarioDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

@WebServlet(name = "UsuarioServlet", urlPatterns = {"/UsuarioServlet"})
public class UsuarioServlet extends HttpServlet {

    private UsuarioDAO usuarioDAO;

    @Override
    public void init() throws ServletException {
        usuarioDAO = new UsuarioDAO();
    }

    private static String nvl(String s) {
        return (s == null) ? "" : s;
    }

    private static String esc(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        String accion = request.getParameter("accion");
        if (accion == null) {
            JSONArray arr = new JSONArray();
            arr.put(new JSONObject()
                    .put("resultado", "error")
                    .put("mensaje", "Parámetro 'accion' requerido"));
            response.getWriter().write(arr.toString());
            return;
        }

        switch (accion) {
            case "roles"     -> cargarRoles(response);
            case "listar"    -> listarUsuarios(response);
            case "guardar"   -> guardarUsuario(request, response);
            case "obtener"   -> obtenerUsuario(request, response);
            case "actualizar"-> actualizarUsuario(request, response);
            case "eliminar"  -> eliminarUsuario(request, response);
            default -> {
                JSONArray arr = new JSONArray();
                arr.put(new JSONObject()
                        .put("resultado", "error")
                        .put("mensaje", "Acción no válida: " + accion));
                response.getWriter().write(arr.toString());
            }
        }
    }

    // =============== ROLES ===============
    private void cargarRoles(HttpServletResponse response) throws IOException {
        JSONArray arr = new JSONArray();
        JSONObject json = new JSONObject();
        try {
            List<Roles> roles = usuarioDAO.listarRoles();
            StringBuilder opciones = new StringBuilder();
            for (Roles r : roles) {
                opciones.append("<option value=\"")
                        .append(r.getIdRol())
                        .append("\">")
                        .append(esc(r.getNombre()))
                        .append("</option>");
            }
            json.put("resultado", "ok");
            json.put("roles", opciones.toString());
        } catch (SQLException e) {
            json.put("resultado", "error")
                .put("mensaje", e.getMessage());
        }
        arr.put(json);
        response.getWriter().write(arr.toString());
    }

    // =============== LISTAR ===============
    private void listarUsuarios(HttpServletResponse response) throws IOException {
        JSONArray arr = new JSONArray();
        JSONObject json = new JSONObject();
        try {
            List<Usuario> lista = usuarioDAO.listar();
            StringBuilder html = new StringBuilder(2048);

            html.append("<table id=\"tablaUsuarios\" ")
                .append("class=\"table table-striped table-hover align-middle\">")
                .append("<thead><tr>")
                .append("<th>ID</th>")
                .append("<th>Nombre</th>")
                .append("<th>Usuario</th>")
                .append("<th>Rol</th>")
                .append("<th class=\"text-center\">Acciones</th>")
                .append("</tr></thead><tbody>");

            for (Usuario u : lista) {
                html.append("<tr>")
                    .append("<td>").append(u.getId()).append("</td>")
                    .append("<td>").append(esc(u.getNombre())).append("</td>")
                    .append("<td>").append(esc(u.getUsuario())).append("</td>")
                    .append("<td>")
                    .append(u.getRol() != null ? esc(u.getRol().getNombre()) : "")
                    .append("</td>")
                    .append("<td class=\"text-center\">")
                    .append("<button class=\"btn btn-sm btn-outline-primary me-1 btn-editar\" ")
                    .append("data-id=\"").append(u.getId()).append("\">")
                    .append("<i class=\"bi bi-pencil-square\"></i></button>")
                    .append("<button class=\"btn btn-sm btn-outline-danger btn-eliminar\" ")
                    .append("data-id=\"").append(u.getId()).append("\">")
                    .append("<i class=\"bi bi-trash\"></i></button>")
                    .append("</td>")
                    .append("</tr>");
            }
            html.append("</tbody></table>");

            json.put("resultado", "ok");
            json.put("tabla", html.toString());
        } catch (SQLException e) {
            json.put("resultado", "error")
                .put("mensaje", e.getMessage());
        }
        arr.put(json);
        response.getWriter().write(arr.toString());
    }

    // =============== GUARDAR (INSERT) ===============
    private void guardarUsuario(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JSONArray arr = new JSONArray();
        JSONObject json = new JSONObject();

        String nombre  = nvl(request.getParameter("nombre")).trim();
        String usuario = nvl(request.getParameter("usuario")).trim();
        String pass    = nvl(request.getParameter("contrasena")).trim();
        String idrolSt = nvl(request.getParameter("idrol")).trim();

        if (nombre.isEmpty() || usuario.isEmpty() || pass.isEmpty() || idrolSt.isEmpty()) {
            json.put("resultado", "error_validacion")
                .put("mensaje", "Todos los campos son obligatorios.");
            arr.put(json);
            response.getWriter().write(arr.toString());
            return;
        }

        try {
            int idrol = Integer.parseInt(idrolSt);

            Usuario u = new Usuario();
            u.setNombre(nombre);
            u.setUsuario(usuario);

            String hash = Encriptar.getStringMessageDigest(pass, Encriptar.SHA256);
            u.setContraseña(hash);

            Roles rol = new Roles();
            rol.setIdRol(idrol);
            u.setRol(rol);

            String r = usuarioDAO.insertar(u);
            switch (r) {
                case "ok" -> json.put("resultado", "ok")
                                  .put("mensaje", "Usuario registrado correctamente.");
                case "duplicado_usuario" -> json.put("resultado", "error_validacion")
                                  .put("mensaje", "El nombre de usuario ya existe.");
                default -> json.put("resultado", "error")
                               .put("mensaje", "No se pudo guardar el usuario.");
            }

        } catch (NumberFormatException | SQLException e) {
            json.put("resultado", "error")
                .put("mensaje", e.getMessage());
        }

        arr.put(json);
        response.getWriter().write(arr.toString());
    }

    // =============== OBTENER (POR ID) ===============
    private void obtenerUsuario(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JSONArray arr = new JSONArray();
        JSONObject json = new JSONObject();

        String idStr = nvl(request.getParameter("id")).trim();
        if (idStr.isEmpty()) {
            json.put("resultado", "error")
                .put("mensaje", "ID requerido.");
            arr.put(json);
            response.getWriter().write(arr.toString());
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            Usuario u = usuarioDAO.obtenerPorId(id);
            if (u == null) {
                json.put("resultado", "error")
                    .put("mensaje", "Usuario no encontrado.");
            } else {
                json.put("resultado", "ok")
                    .put("id", u.getId())
                    .put("nombre", nvl(u.getNombre()))
                    .put("usuario", nvl(u.getUsuario()))
                    .put("idrol", u.getRol() != null ? u.getRol().getIdRol() : 0);
            }
        } catch (NumberFormatException | SQLException e) {
            json.put("resultado", "error")
                .put("mensaje", e.getMessage());
        }

        arr.put(json);
        response.getWriter().write(arr.toString());
    }

    // =============== ACTUALIZAR ===============
    private void actualizarUsuario(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JSONArray arr = new JSONArray();
        JSONObject json = new JSONObject();

        String idStr    = nvl(request.getParameter("id")).trim();
        String nombre   = nvl(request.getParameter("nombre")).trim();
        String usuario  = nvl(request.getParameter("usuario")).trim();
        String idrolStr = nvl(request.getParameter("idrol")).trim();

        if (idStr.isEmpty() || nombre.isEmpty() || usuario.isEmpty() || idrolStr.isEmpty()) {
            json.put("resultado", "error_validacion")
                .put("mensaje", "Todos los campos son obligatorios.");
            arr.put(json);
            response.getWriter().write(arr.toString());
            return;
        }

        try {
            int id    = Integer.parseInt(idStr);
            int idrol = Integer.parseInt(idrolStr);

            Usuario u = new Usuario();
            u.setId(id);
            u.setNombre(nombre);
            u.setUsuario(usuario);
            Roles rol = new Roles();
            rol.setIdRol(idrol);
            u.setRol(rol);

            String r = usuarioDAO.actualizar(u);
            switch (r) {
                case "ok" -> json.put("resultado", "ok")
                                  .put("mensaje", "Usuario actualizado correctamente.");
                case "duplicado_usuario" -> json.put("resultado", "error_validacion")
                                  .put("mensaje", "El nombre de usuario ya existe para otro registro.");
                default -> json.put("resultado", "error")
                               .put("mensaje", "No se pudo actualizar el usuario.");
            }

        } catch (NumberFormatException | SQLException e) {
            json.put("resultado", "error")
                .put("mensaje", e.getMessage());
        }

        arr.put(json);
        response.getWriter().write(arr.toString());
    }

    // =============== ELIMINAR ===============
    private void eliminarUsuario(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JSONArray arr = new JSONArray();
        JSONObject json = new JSONObject();

        String idStr = nvl(request.getParameter("id")).trim();
        if (idStr.isEmpty()) {
            json.put("resultado", "error")
                .put("mensaje", "ID requerido.");
            arr.put(json);
            response.getWriter().write(arr.toString());
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            boolean ok = usuarioDAO.eliminar(id);
            if (ok) {
                json.put("resultado", "ok")
                    .put("mensaje", "Usuario eliminado correctamente.");
            } else {
                json.put("resultado", "error")
                    .put("mensaje", "No se eliminó ningún registro.");
            }
        } catch (NumberFormatException | SQLException e) {
            json.put("resultado", "error")
                .put("mensaje", e.getMessage());
        }

        arr.put(json);
        response.getWriter().write(arr.toString());
    }
}
