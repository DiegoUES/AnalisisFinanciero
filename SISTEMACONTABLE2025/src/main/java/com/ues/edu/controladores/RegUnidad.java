/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ues.edu.controladores;

import com.ues.edu.modelo.Institucion;
import com.ues.edu.modelo.Unidad;
import com.ues.edu.modelo.dao.Unidad_DAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Marlo
 */
@WebServlet(name = "RegUnidad", urlPatterns = {"/RegUnidad"})
public class RegUnidad extends HttpServlet {

    private Unidad_DAO dao;
    private ArrayList<Unidad> listaUnidades;
    private ArrayList<Institucion> listaInstituciones;
    private Unidad unidad = null;

    // NVL UTIL
    private static String nvl(String s) {
        return (s == null) ? "" : s;
    }

    private static String nvl(Object o) {
        return (o == null) ? "" : String.valueOf(o);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json; charset=UTF-8");

        PrintWriter out = resp.getWriter();
        String filtro = req.getParameter("opcion");

        System.out.println("SERVLET UNIDAD recibe opcion = " + filtro);

        if (filtro == null) {
            out.write(new JSONObject()
                    .put("resultado", "error")
                    .put("mensaje", "Parámetro 'opcion' es requerido")
                    .toString());
            return;
        }

        switch (filtro) {
            case "cargarComboInstitucion": {
                JSONArray array = new JSONArray();
                JSONObject json = new JSONObject();
                String comboInstitucion = "";

                this.dao = new Unidad_DAO();
                try {
                    this.listaInstituciones = dao.CargarComboInstitucion();

                    for (Institucion institucion : this.listaInstituciones) {
                        comboInstitucion += "<option value=\""
                                + institucion.getId()
                                + "\">"
                                + institucion.getNombre()
                                + "</option>";
                    }

                    json.put("resultado", "exito");
                    json.put("institucion", comboInstitucion);

                } catch (Exception e) {
                    json.put("resultado", "error_sql")
                            .put("error_mostrado", e.getMessage());
                }

                array.put(json);
                resp.getWriter().write(array.toString());
                break;
            }

            case "cargarTabla": {

                JSONArray array = new JSONArray();
                JSONObject json = new JSONObject();

                try {
                    dao = new Unidad_DAO();
                    listaUnidades = dao.mostrar();

                    StringBuilder html = new StringBuilder(1024);

                    html.append("<table id='tabla_idServlet' ")
                            .append("class='display nowrap table table-striped align-middle' style='width:100%'>");

                    html.append("<thead><tr>")
                            .append("<th>Código Unidad</th>")
                            .append("<th>Nombre Unidad</th>")
                            .append("<th>Nombre Institución</th>")
                            .append("<th class='text-center'>Acciones</th>")
                            .append("</tr></thead>");

                    html.append("<tbody>");

                    for (Unidad uni : listaUnidades) {

                        html.append("<tr>")
                                .append("<td>").append(nvl(uni.getId())).append("</td>")
                                .append("<td>").append(nvl(uni.getNombre())).append("</td>")
                                .append("<td>").append(nvl(uni.getInstitucion().getNombre())).append("</td>")
                                .append("<td class='text-center'>")
                                .append("<div class='btn-group'>")
                                // BOTÓN EDITAR
                                .append("<button class='btn btn-sm btn-outline-primary btn_editar_unidad' ")
                                .append("data-id='").append(uni.getId()).append("'>")
                                .append("<i class='bi bi-pencil-square'></i></button>")
                                .append("</div></td>")
                                .append("</tr>");
                    }

                    html.append("</tbody></table>");

                    json.put("resultado", "exito")
                            .put("tabla", html.toString());

                } catch (Exception e) {
                    json.put("resultado", "error_sql")
                            .put("mensaje", e.getMessage());
                }

                array.put(json);
                out.write(array.toString());
                break;
            }

            case "si_registro": {

                JSONArray array = new JSONArray();
                JSONObject json = new JSONObject();

                try {
                    dao = new Unidad_DAO();
                    unidad = new Unidad();
                    
                    unidad.setId(Integer.parseInt(req.getParameter("txt_id")));

                    unidad.setNombre(req.getParameter("txt_nombre"));

                    Institucion institucion = new Institucion();
                    institucion.setId(Integer.parseInt(req.getParameter("cmb_institucion")));
                    unidad.setInstitucion(institucion);

                    String r = dao.insertar(unidad);

                    if ("exito".equals(r)) {
                        json.put("resultado", "exito")
                                .put("mensaje", "Unidad registrada correctamente.");
                    } else {
                        json.put("resultado", "error_sql")
                                .put("detalle", r);
                    }

                } catch (Exception e) {
                    json.put("resultado", "error_sql")
                            .put("error_mostrado", e.getMessage());
                }

                array.put(json);
                out.write(array.toString());
                break;
            }

            
            case "cargarDatos": {

                JSONArray array = new JSONArray();
                JSONObject json = new JSONObject();

                try {
                    dao = new Unidad_DAO();
                    int id = Integer.parseInt(req.getParameter("id"));

                    Unidad u = dao.cargarDatos(id);

                    if (u != null) {
                        json.put("resultado", "exito")
                                .put("ID", u.getId())
                                .put("ID_INSTITUCION", u.getInstitucion().getId())
                                .put("NOMBRE_INSTITUCION", nvl(u.getInstitucion().getNombre()));
                    } else {
                        json.put("resultado", "no_encontrado")
                                .put("mensaje", "No existe la unidad solicitada.");
                    }

                } catch (Exception e) {
                    json.put("resultado", "error_sql")
                            .put("error_mostrado", e.getMessage());
                }

                array.put(json);
                out.write(array.toString());
                break;
            }

            case "si_actualizo": {

                JSONArray array = new JSONArray();
                JSONObject json = new JSONObject();

                try {
                    dao = new Unidad_DAO();
                    unidad = new Unidad();

                    unidad.setId(Integer.parseInt(req.getParameter("txt_id")));

                    int idInstitucion = Integer.parseInt(req.getParameter("cmb_institucion"));
                    Institucion inst = new Institucion();
                    inst.setId(idInstitucion);
                    unidad.setInstitucion(inst);

                    String r = dao.modificar(unidad);

                    if ("exito".equals(r)) {
                        json.put("resultado", "exito")
                                .put("mensaje", "Registro actualizado correctamente.");
                    } else {
                        json.put("resultado", "error_sql")
                                .put("detalle", r);
                    }

                } catch (Exception e) {
                    json.put("resultado", "error_sql")
                            .put("error_mostrado", e.getMessage());
                }

                array.put(json);
                out.write(array.toString());
                break;
            }
        }
    }

}
