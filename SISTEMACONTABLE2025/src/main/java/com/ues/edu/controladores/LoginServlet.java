package com.ues.edu.controladores;

import com.ues.edu.modelo.Usuario;
import com.ues.edu.modelo.dao.UsuarioDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import org.json.JSONObject;

@WebServlet(name = "LoginServlet", urlPatterns = {"/LoginServlet"})
public class LoginServlet extends HttpServlet {

    private UsuarioDAO usuarioDAO;

    @Override
    public void init() throws ServletException {
        usuarioDAO = new UsuarioDAO();
    }

    private static String nvl(String s) {
        return (s == null) ? "" : s.trim();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {

        String accion = nvl(req.getParameter("accion"));

        // Logout: /LoginServlet?accion=logout
        if ("logout".equalsIgnoreCase(accion)) {
            HttpSession ses = req.getSession(false);
            if (ses != null) {
                ses.invalidate();
            }
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        // Cualquier otro GET muestra la pantalla de login
        req.getRequestDispatcher("/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=UTF-8");

        String usuarioParam    = nvl(req.getParameter("usuario"));
        String contrasenaParam = nvl(req.getParameter("contrasena"));

        JSONObject json = new JSONObject();

        if (usuarioParam.isEmpty() || contrasenaParam.isEmpty()) {
            json.put("resultado", "error");
            json.put("mensaje", "Usuario y contraseña son obligatorios.");
            try (PrintWriter out = resp.getWriter()) {
                out.print(json.toString());
            }
            return;
        }

        try {
            Usuario u = usuarioDAO.autenticar(usuarioParam, contrasenaParam);

            if (u == null) {
                json.put("resultado", "error");
                json.put("mensaje", "Usuario o contraseña incorrectos.");
            } else {
                // Guardar en sesión
                HttpSession ses = req.getSession(true);
                ses.setAttribute("usuarioSesion", u);
                ses.setAttribute("idUsuario", u.getId());
                ses.setAttribute("nombreUsuario", u.getNombre());
                if (u.getRol() != null) {
                    ses.setAttribute("rolId", u.getRol().getIdRol());
                    ses.setAttribute("rolNombre", u.getRol().getNombre());
                }

                json.put("resultado", "ok");
                json.put("usuario", u.getNombre());
                json.put("rol", (u.getRol() != null) ? u.getRol().getNombre() : "");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            json.put("resultado", "error");
            json.put("mensaje", "Error de base de datos: " + e.getMessage());
        }

        try (PrintWriter out = resp.getWriter()) {
            out.print(json.toString());
        }
    }
}
