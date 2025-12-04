/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.ues.edu.controladores;

import com.ues.edu.modelo.dao.TipoUsado_DAO;
import com.ues.edu.modelo.TipoUsado;
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
@WebServlet(name = "RegTipoUsado", urlPatterns = {"/RegTipoUsado"})
public class RegTipoUsado extends HttpServlet {

    private TipoUsado_DAO dao;
    private ArrayList<TipoUsado> listaTipoUsados;
    private TipoUsado tipoUsado = null;

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
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet RegTipoUsado</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet RegTipoUsado at " + request.getContextPath() + "</h1>");
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

        System.out.println("SERVLET TIPO USADO recibe opcion = " + filtro);

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
                    dao = new TipoUsado_DAO();
                    listaTipoUsados = dao.mostrar();

                    StringBuilder html = new StringBuilder(1024);

                    html.append("<table id='tabla_idServlet' ")
                            .append("class='display nowrap table table-striped align-middle' style='width:100%'>");

                    html.append("<thead><tr>")
                            .append("<th>Años</th>")
                            .append("<th>Porcentaje</th>")
                            .append("<th class='text-center'>Acciones</th>")
                            .append("</tr></thead>");

                    html.append("<tbody>");

                    for (TipoUsado tipo : listaTipoUsados) {
                        html.append("<tr>")
                                .append("<td>").append(tipo.getAnyos()).append(" años</td>")
                                .append("<td>").append(tipo.getPorcentaje()).append("%</td>")
                                .append("<td class='text-center'>")
                                .append("<div class='btn-group'>")
                                // BOTÓN EDITAR
                                .append("<button class='btn btn-sm btn-outline-primary btn_editar me-1' ")
                                .append("data-id='").append(tipo.getIdTipoUsado()).append("'>")
                                .append("<i class='bi bi-pencil-square'></i></button>")
                                // BOTÓN ELIMINAR
                                .append("<button class='btn btn-sm btn-outline-danger btn_eliminar' ")
                                .append("data-id='").append(tipo.getIdTipoUsado()).append("'>")
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
                    dao = new TipoUsado_DAO();
                    tipoUsado = new TipoUsado();

                    // Obtener parámetros
                    String anyosStr = req.getParameter("txt_anyos");
                    String porcentajeStr = req.getParameter("txt_porcentaje");

                    // Validar años
                    int anyos;
                    try {
                        anyos = Integer.parseInt(anyosStr);
                        if (anyos <= 0) {
                            json.put("resultado", "error")
                                    .put("mensaje", "Los años deben ser un número positivo");
                            array.put(json);
                            out.write(array.toString());
                            return;
                        }
                    } catch (NumberFormatException e) {
                        json.put("resultado", "error")
                                .put("mensaje", "Los años deben ser un número válido");
                        array.put(json);
                        out.write(array.toString());
                        return;
                    }

                    // Validar porcentaje (20-80)
                    int porcentaje;
                    try {
                        porcentaje = Integer.parseInt(porcentajeStr);
                        if (porcentaje < 20 || porcentaje > 80) {
                            json.put("resultado", "error")
                                    .put("mensaje", "El porcentaje debe estar entre 20 y 80");
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

                    // Verificar si los años ya existen
                    if (dao.existeAnyos(anyos)) {
                        json.put("resultado", "error")
                                .put("mensaje", "Ya existe un registro con " + anyos + " años");
                        array.put(json);
                        out.write(array.toString());
                        return;
                    }

                    tipoUsado.setAnyos(anyos);
                    tipoUsado.setPorcentaje(porcentaje);

                    String r = dao.insertar(tipoUsado);

                    if ("exito".equals(r)) {
                        json.put("resultado", "exito")
                                .put("mensaje", "Tipo usado registrado correctamente.");
                    } else if ("error_porcentaje_invalido".equals(r)) {
                        json.put("resultado", "error")
                                .put("mensaje", "El porcentaje debe estar entre 20 y 80");
                    } else if ("error_anyos_invalido".equals(r)) {
                        json.put("resultado", "error")
                                .put("mensaje", "Los años deben ser un número positivo");
                    } else if ("error_anyos_duplicado".equals(r)) {
                        json.put("resultado", "error")
                                .put("mensaje", "Ya existe un registro con esos años");
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
                    dao = new TipoUsado_DAO();
                    int id = Integer.parseInt(req.getParameter("id"));

                    tipoUsado = dao.buscarPorId(id);

                    if (tipoUsado != null) {
                        json.put("resultado", "exito")
                                .put("ID", tipoUsado.getIdTipoUsado())
                                .put("ANYOS", tipoUsado.getAnyos())
                                .put("PORCENTAJE", tipoUsado.getPorcentaje());
                    } else {
                        json.put("resultado", "no_encontrado")
                                .put("mensaje", "No existe el tipo usado solicitado.");
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
                    dao = new TipoUsado_DAO();
                    tipoUsado = new TipoUsado();

                    // Obtener parámetros
                    String anyosStr = req.getParameter("txt_anyos");
                    String porcentajeStr = req.getParameter("txt_porcentaje");
                    int idOriginal = Integer.parseInt(req.getParameter("txt_id"));

                    // Validar años
                    int anyos;
                    try {
                        anyos = Integer.parseInt(anyosStr);
                        if (anyos <= 0) {
                            json.put("resultado", "error")
                                    .put("mensaje", "Los años deben ser un número positivo");
                            array.put(json);
                            out.write(array.toString());
                            return;
                        }
                    } catch (NumberFormatException e) {
                        json.put("resultado", "error")
                                .put("mensaje", "Los años deben ser un número válido");
                        array.put(json);
                        out.write(array.toString());
                        return;
                    }

                    // Validar porcentaje (20-80)
                    int porcentaje;
                    try {
                        porcentaje = Integer.parseInt(porcentajeStr);
                        if (porcentaje < 20 || porcentaje > 80) {
                            json.put("resultado", "error")
                                    .put("mensaje", "El porcentaje debe estar entre 20 y 80");
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

                    // Verificar si los años ya existen (excluyendo el actual)
                    if (dao.existeAnyosExcluyendo(anyos, idOriginal)) {
                        json.put("resultado", "error")
                                .put("mensaje", "Ya existe otro registro con " + anyos + " años");
                        array.put(json);
                        out.write(array.toString());
                        return;
                    }

                    tipoUsado.setIdTipoUsado(idOriginal);
                    tipoUsado.setAnyos(anyos);
                    tipoUsado.setPorcentaje(porcentaje);

                    String r = dao.modificar(tipoUsado);

                    if ("exito".equals(r)) {
                        json.put("resultado", "exito")
                                .put("mensaje", "Registro actualizado correctamente.");
                    } else if ("error_porcentaje_invalido".equals(r)) {
                        json.put("resultado", "error")
                                .put("mensaje", "El porcentaje debe estar entre 20 y 80");
                    } else if ("error_anyos_invalido".equals(r)) {
                        json.put("resultado", "error")
                                .put("mensaje", "Los años deben ser un número positivo");
                    } else if ("error_anyos_duplicado".equals(r)) {
                        json.put("resultado", "error")
                                .put("mensaje", "Ya existe un registro con esos años");
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
                    dao = new TipoUsado_DAO();
                    int id = Integer.parseInt(req.getParameter("id"));

                    // Primero verificar si existe
                    TipoUsado tipo = dao.buscarPorId(id);
                    if (tipo == null) {
                        json.put("resultado", "error")
                                .put("mensaje", "El tipo usado no existe");
                        array.put(json);
                        out.write(array.toString());
                        return;
                    }

                    String r = dao.eliminar(id);

                    if ("exito".equals(r)) {
                        json.put("resultado", "exito")
                                .put("mensaje", "Tipo usado eliminado correctamente.");
                    } else if ("error_relacionado".equals(r)) {
                        json.put("resultado", "error")
                                .put("mensaje", "No se puede eliminar porque está relacionado con otros registros.");
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
