package com.ues.edu.controladores;

import com.ues.edu.modelo.Usuario;
import com.ues.edu.modelo.dao.UsuarioDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

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

        // Si ya hay usuario logueado, enviarlo al inicio
        HttpSession ses = req.getSession(false);
        if (ses != null && ses.getAttribute("usuario") != null) {
            resp.sendRedirect(req.getContextPath() + "/index.jsp");
            return;
        }

        // Mostrar pantalla de login
        req.getRequestDispatcher("/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {

        req.setCharacterEncoding("UTF-8");

        String usuarioParam    = nvl(req.getParameter("usuario"));
        String contrasenaParam = nvl(req.getParameter("password"));
        if (contrasenaParam.isEmpty()) {
            contrasenaParam = nvl(req.getParameter("contrasena"));
        }

        // Validación básica
        if (usuarioParam.isEmpty() || contrasenaParam.isEmpty()) {
            req.setAttribute("error", "Usuario y contraseña son obligatorios.");
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
            return;
        }

        try {
            Usuario u = usuarioDAO.autenticar(usuarioParam, contrasenaParam);

            if (u == null) {
                // Credenciales incorrectas
                req.setAttribute("error", "Usuario o contraseña incorrectos.");
                req.getRequestDispatcher("/login.jsp").forward(req, resp);
            } else {
                // Login correcto: guardar en sesión
                HttpSession ses = req.getSession(true);

                // OBJETO COMPLETO (nombre que usan tus JSP en el cast)
                ses.setAttribute("usuario", u);
                // Lo dejo también por si lo usas en otros lados
                ses.setAttribute("usuarioSesion", u);

                // CAMPOS AUXILIARES
                ses.setAttribute("idUsuario", u.getId());
                ses.setAttribute("nombreUsuario", u.getNombre());

                if (u.getRol() != null) {
                    // ESTOS DOS SON LOS QUE USAN TUS JSP INDIRECTAMENTE
                    ses.setAttribute("rol", u.getRol().getNombre());
                    ses.setAttribute("rolNombre", u.getRol().getNombre());
                    ses.setAttribute("rolId", u.getRol().getIdRol());
                }

                // Redirigir al inicio (index.jsp)
                resp.sendRedirect(req.getContextPath() + "/index.jsp");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            req.setAttribute("error", "Error de base de datos: " + e.getMessage());
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
        }
    }
}
