/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.ues.edu.controladores;

import com.ues.edu.modelo.dao.TipoCategoria_DAO;
import com.ues.edu.modelo.TipoCategoria;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author herna
 */
@WebServlet(name = "RegCatalogoTipo", urlPatterns = {"/RegCatalogoTipo"})
public class RegCatalogoTipo extends HttpServlet {

    private TipoCategoria_DAO dao;
    private ArrayList<TipoCategoria> listaTipoCategorias;
    private TipoCategoria tipoCategoria = null;

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet ReCatalogoTipo</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ReCatalogoTipo at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json; charset=UTF-8");

        PrintWriter out = resp.getWriter();
        String filtro = req.getParameter("opcion");

        System.out.println("SERVLET CATALOGO TIPO recibe opcion = " + filtro);

        if (filtro == null) {
            out.write(new JSONObject()
                    .put("resultado", "error")
                    .put("mensaje", "Parámetro 'opcion' es requerido")
                    .toString());
            return;
        }

        switch (filtro) {
            case "cargarTabla": {

                JSONArray array = new JSONArray();
                JSONObject json = new JSONObject();

                try {
                    dao = new TipoCategoria_DAO();
                    listaTipoCategorias = dao.mostrar();

                    StringBuilder html = new StringBuilder(1024);

                    html.append("<table id='tabla_idServlet' ")
                            .append("class='display nowrap table table-striped align-middle' style='width:100%'>");

                    html.append("<thead><tr>")
                            .append("<th>Código</th>")
                            .append("<th>Nombre</th>")
                            .append("<th>Porcentaje</th>")
                            .append("<th class='text-center'>Acciones</th>")
                            .append("</tr></thead>");

                    html.append("<tbody>");

                    for (TipoCategoria tipo : listaTipoCategorias) {
                        // Formatear idTipo a 4 dígitos con ceros a la izquierda
                        String codigoFormateado = String.format("%04d", tipo.getIdTipo());

                        html.append("<tr>")
                                .append("<td>").append(codigoFormateado).append("</td>")
                                .append("<td>").append(nvl(tipo.getNombre())).append("</td>")
                                .append("<td>").append(String.format("%.2f%%", tipo.getPorcentaje())).append("</td>")
                                .append("<td class='text-center'>")
                                .append("<div class='btn-group'>")
                                // BOTÓN EDITAR - enviamos el idTipo (número)
                                .append("<button class='btn btn-sm btn-outline-primary btn_editar me-1' ")
                                .append("data-id='").append(tipo.getIdTipo()).append("'>")
                                .append("<i class='bi bi-pencil-square'></i></button>")
                                // BOTÓN ELIMINAR
                                .append("<button class='btn btn-sm btn-outline-danger btn_eliminar' ")
                                .append("data-id='").append(tipo.getIdTipo()).append("'>")
                                .append("<i class='bi bi-trash'></i></button>")
                                .append("</div></td>")
                                .append("</tr>");
                    }

                    html.append("</tbody></table>");

                    json.put("resultado", "exito")
                            .put("tabla", html.toString());

                } catch (Exception e) {
                    json.put("resultado", "error_sql")
                            .put("mensaje", e.getMessage());
                    e.printStackTrace();
                }

                array.put(json);
                out.write(array.toString());
                break;
            }

            case "si_registro": {

                JSONArray array = new JSONArray();
                JSONObject json = new JSONObject();

                try {
                    dao = new TipoCategoria_DAO();
                    tipoCategoria = new TipoCategoria();

                    // Obtener parámetros
                    String codigoStr = req.getParameter("txt_codigo");
                    String nombre = req.getParameter("txt_nombre");
                    String porcentajeStr = req.getParameter("txt_porcentaje");

                    // Validar código (4 dígitos numéricos)
                    if (codigoStr == null || !codigoStr.matches("\\d{4}")) {
                        json.put("resultado", "error")
                                .put("mensaje", "El código debe tener exactamente 4 números");
                        array.put(json);
                        out.write(array.toString());
                        return;
                    }

                    // Convertir código de String a int (ej: "0001" → 1)
                    int codigo;
                    try {
                        codigo = Integer.parseInt(codigoStr);
                        // Validar rango (1-9999)
                        if (codigo < 1 || codigo > 9999) {
                            json.put("resultado", "error")
                                    .put("mensaje", "El código debe estar entre 0001 y 9999");
                            array.put(json);
                            out.write(array.toString());
                            return;
                        }
                    } catch (NumberFormatException e) {
                        json.put("resultado", "error")
                                .put("mensaje", "El código debe ser un número válido");
                        array.put(json);
                        out.write(array.toString());
                        return;
                    }

                    // Validar nombre
                    if (nombre == null || nombre.trim().isEmpty()) {
                        json.put("resultado", "error")
                                .put("mensaje", "El nombre es requerido");
                        array.put(json);
                        out.write(array.toString());
                        return;
                    }

                    // Validar porcentaje (0-100)
                    double porcentaje;
                    try {
                        porcentaje = Double.parseDouble(porcentajeStr);
                        if (porcentaje < 0 || porcentaje > 100) {
                            json.put("resultado", "error")
                                    .put("mensaje", "El porcentaje debe estar entre 0 y 100");
                            array.put(json);
                            out.write(array.toString());
                            return;
                        }
                    } catch (NumberFormatException e) {
                        json.put("resultado", "error")
                                .put("mensaje", "El porcentaje debe ser un número válido");
                        array.put(json);
                        out.write(array.toString());
                        return;
                    }

                    // Verificar si el código ya existe
                    if (dao.existeIdTipo(codigo)) {
                        json.put("resultado", "error")
                                .put("mensaje", "Ya existe un tipo con el código " + String.format("%04d", codigo));
                        array.put(json);
                        out.write(array.toString());
                        return;
                    }

                    // Verificar si el nombre ya existe
                    if (dao.existeNombre(nombre.trim())) {
                        json.put("resultado", "error")
                                .put("mensaje", "Ya existe un tipo de categoría con ese nombre");
                        array.put(json);
                        out.write(array.toString());
                        return;
                    }

                    tipoCategoria.setIdTipo(codigo);
                    tipoCategoria.setNombre(nombre.trim());
                    tipoCategoria.setPorcentaje(porcentaje);

                    String r = dao.insertar(tipoCategoria);

                    if ("exito".equals(r)) {
                        json.put("resultado", "exito")
                                .put("mensaje", "Tipo de categoría registrado correctamente.");
                    } else if ("error_idtipo_invalido".equals(r)) {
                        json.put("resultado", "error")
                                .put("mensaje", "El código debe estar entre 0001 y 9999");
                    } else if ("error_idtipo_duplicado".equals(r)) {
                        json.put("resultado", "error")
                                .put("mensaje", "Ya existe un tipo con ese código");
                    } else if ("error_porcentaje_invalido".equals(r)) {
                        json.put("resultado", "error")
                                .put("mensaje", "El porcentaje debe estar entre 0 y 100");
                    } else {
                        json.put("resultado", "error_sql")
                                .put("detalle", r);
                    }

                } catch (Exception e) {
                    json.put("resultado", "error_sql")
                            .put("error_mostrado", e.getMessage());
                    e.printStackTrace();
                }

                array.put(json);
                out.write(array.toString());
                break;
            }

            case "si_tipo_especifico": {

                JSONArray array = new JSONArray();
                JSONObject json = new JSONObject();

                try {
                    dao = new TipoCategoria_DAO();
                    // El id viene como String del data-id (pero es número)
                    int id = Integer.parseInt(req.getParameter("id"));

                    tipoCategoria = dao.buscarPorId(id);

                    if (tipoCategoria != null) {
                        // Enviar código formateado a 4 dígitos
                        String codigoFormateado = String.format("%04d", tipoCategoria.getIdTipo());

                        json.put("resultado", "exito")
                                .put("ID", tipoCategoria.getIdTipo())
                                .put("CODIGO", codigoFormateado) // Enviamos formateado
                                .put("NOMBRE", tipoCategoria.getNombre())
                                .put("PORCENTAJE", tipoCategoria.getPorcentaje());
                    } else {
                        json.put("resultado", "no_encontrado")
                                .put("mensaje", "No existe el tipo de categoría solicitado.");
                    }

                } catch (Exception e) {
                    json.put("resultado", "error_sql")
                            .put("error_mostrado", e.getMessage());
                    e.printStackTrace();
                }

                array.put(json);
                out.write(array.toString());
                break;
            }

            case "si_actualizo": {

                JSONArray array = new JSONArray();
                JSONObject json = new JSONObject();

                try {
                    dao = new TipoCategoria_DAO();
                    tipoCategoria = new TipoCategoria();

                    // Obtener parámetros
                    String codigoStr = req.getParameter("txt_codigo");
                    String nombre = req.getParameter("txt_nombre");
                    String porcentajeStr = req.getParameter("txt_porcentaje");
                    // txt_id contiene el idTipo original (número)
                    int idOriginal = Integer.parseInt(req.getParameter("txt_id"));

                    // Validar código (4 dígitos numéricos)
                    if (codigoStr == null || !codigoStr.matches("\\d{4}")) {
                        json.put("resultado", "error")
                                .put("mensaje", "El código debe tener exactamente 4 números");
                        array.put(json);
                        out.write(array.toString());
                        return;
                    }

                    // Convertir código de String a int
                    int codigo;
                    try {
                        codigo = Integer.parseInt(codigoStr);
                        // Validar rango (1-9999)
                        if (codigo < 1 || codigo > 9999) {
                            json.put("resultado", "error")
                                    .put("mensaje", "El código debe estar entre 0001 y 9999");
                            array.put(json);
                            out.write(array.toString());
                            return;
                        }
                    } catch (NumberFormatException e) {
                        json.put("resultado", "error")
                                .put("mensaje", "El código debe ser un número válido");
                        array.put(json);
                        out.write(array.toString());
                        return;
                    }

                    // Validar que si cambió el código, no exista ya
                    if (codigo != idOriginal && dao.existeIdTipo(codigo)) {
                        json.put("resultado", "error")
                                .put("mensaje", "Ya existe otro tipo con el código " + String.format("%04d", codigo));
                        array.put(json);
                        out.write(array.toString());
                        return;
                    }

                    // Validar nombre
                    if (nombre == null || nombre.trim().isEmpty()) {
                        json.put("resultado", "error")
                                .put("mensaje", "El nombre es requerido");
                        array.put(json);
                        out.write(array.toString());
                        return;
                    }

                    // Validar porcentaje (0-100)
                    double porcentaje;
                    try {
                        porcentaje = Double.parseDouble(porcentajeStr);
                        if (porcentaje < 0 || porcentaje > 100) {
                            json.put("resultado", "error")
                                    .put("mensaje", "El porcentaje debe estar entre 0 y 100");
                            array.put(json);
                            out.write(array.toString());
                            return;
                        }
                    } catch (NumberFormatException e) {
                        json.put("resultado", "error")
                                .put("mensaje", "El porcentaje debe ser un número válido");
                        array.put(json);
                        out.write(array.toString());
                        return;
                    }

                    // Verificar si el nombre ya existe (excluyendo el actual)
                    if (dao.existeNombreExcluyendo(nombre.trim(), idOriginal)) {
                        json.put("resultado", "error")
                                .put("mensaje", "Ya existe otro tipo de categoría con ese nombre");
                        array.put(json);
                        out.write(array.toString());
                        return;
                    }

                    tipoCategoria.setIdTipo(codigo); // Usamos el nuevo código
                    tipoCategoria.setNombre(nombre.trim());
                    tipoCategoria.setPorcentaje(porcentaje);

                    String r = dao.modificar(tipoCategoria);

                    if ("exito".equals(r)) {
                        json.put("resultado", "exito")
                                .put("mensaje", "Registro actualizado correctamente.");
                    } else if ("error_porcentaje_invalido".equals(r)) {
                        json.put("resultado", "error")
                                .put("mensaje", "El porcentaje debe estar entre 0 y 100");
                    } else {
                        json.put("resultado", "error_sql")
                                .put("detalle", r);
                    }

                } catch (Exception e) {
                    json.put("resultado", "error_sql")
                            .put("error_mostrado", e.getMessage());
                    e.printStackTrace();
                }

                array.put(json);
                out.write(array.toString());
                break;
            }

            case "si_elimina": {
                JSONArray array = new JSONArray();
                JSONObject json = new JSONObject();

                try {
                    dao = new TipoCategoria_DAO();
                    int id = Integer.parseInt(req.getParameter("id"));

                    TipoCategoria tipo = dao.buscarPorId(id);
                    if (tipo == null) {
                        json.put("resultado", "error")
                                .put("mensaje", "El tipo de categoría no existe");
                        array.put(json);
                        out.write(array.toString());
                        return;
                    }

                    String r = dao.eliminar(id);

                    if ("exito".equals(r)) {
                        json.put("resultado", "exito")
                                .put("mensaje", "Tipo de categoría eliminado correctamente.");
                    } else if ("error_relacionado_con_activo".equals(r)) {
                        String codigoFormateado = String.format("%04d", id);
                        json.put("resultado", "error")
                                .put("mensaje", "No se puede eliminar el tipo de categoría " + codigoFormateado
                                        + " porque está relacionado con activos registrados.");
                    } else {
                        json.put("resultado", "error_sql")
                                .put("detalle", r);
                    }

                } catch (Exception e) {
                    json.put("resultado", "error_sql")
                            .put("error_mostrado", e.getMessage());
                    e.printStackTrace();
                }

                array.put(json);
                out.write(array.toString());
                break;
            }

            default: {
                out.write(new JSONObject()
                        .put("resultado", "error")
                        .put("mensaje", "Opción no válida: " + filtro)
                        .toString());
            }
        }
    }

    // Método auxiliar para manejar valores nulos
    private String nvl(Object obj) {
        return obj == null ? "" : obj.toString();
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
