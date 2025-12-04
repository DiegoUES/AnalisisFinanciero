/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ues.edu.controladores;

import com.ues.edu.modelo.Institucion;
import com.ues.edu.modelo.dao.Institucion_DAO;
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
@WebServlet(name = "RegInstitucion", urlPatterns = {"/RegInstitucion"})
public class RegInstitucion extends HttpServlet{
    private Institucion_DAO dao;
    private ArrayList<Institucion> listaInstituciones;
    private Institucion institucion = null;

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

        System.out.println("SERVLET INSTITUCION recibe opcion = " + filtro);

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
                    dao = new Institucion_DAO();
                    listaInstituciones = dao.mostrar();

                    StringBuilder html = new StringBuilder(1024);

                    html.append("<table id='tabla_idServlet' ")
                        .append("class='display nowrap table table-striped align-middle' style='width:100%'>");

                    html.append("<thead><tr>")
                        .append("<th>Código Institución</th>")    
                        .append("<th>Nombre</th>")
                        .append("<th class='text-center'>Acciones</th>")
                        .append("</tr></thead>");

                    html.append("<tbody>");

                    for (Institucion inst : listaInstituciones) {

                        html.append("<tr>")
                            .append("<td>").append(nvl(inst.getId())).append("</td>")    
                            .append("<td>").append(nvl(inst.getNombre())).append("</td>")
                            .append("<td class='text-center'>")
                            .append("<div class='btn-group'>")
                            // BOTÓN EDITAR
                            .append("<button class='btn btn-sm btn-outline-primary btn_editar' ")
                            .append("data-id='").append(inst.getId()).append("'>")
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
                    dao = new Institucion_DAO();
                    institucion = new Institucion();

                    institucion.setId(Integer.parseInt(req.getParameter("txt_id")));
                    institucion.setNombre(req.getParameter("txt_nombre"));

                    String r = dao.insertar(institucion);

                    if ("exito".equals(r)) {
                        json.put("resultado", "exito")
                            .put("mensaje", "Institución registrada correctamente.");
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

            case "si_institucion_especifica": {

                JSONArray array = new JSONArray();
                JSONObject json = new JSONObject();

                try {
                    dao = new Institucion_DAO();
                    int id = Integer.parseInt(req.getParameter("id"));

                    listaInstituciones = dao.mostrar();

                    Institucion inst = listaInstituciones.stream()
                            .filter(x -> x.getId() == id)
                            .findFirst()
                            .orElse(null);

                    if (inst != null) {
                        json.put("resultado", "exito")
                            .put("ID", inst.getId())
                            .put("NOMBRE", inst.getNombre());
                    } else {
                        json.put("resultado", "no_encontrado")
                            .put("mensaje", "No existe la institución solicitada.");
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
                    dao = new Institucion_DAO();
                    institucion = new Institucion();

                    institucion.setId(Integer.parseInt(req.getParameter("txt_id")));
                    institucion.setNombre(req.getParameter("txt_nombre"));

                    String r = dao.modificar(institucion);

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
