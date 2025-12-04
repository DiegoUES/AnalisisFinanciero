package com.ues.edu.controladores;

import com.ues.edu.modelo.Activo;
import com.ues.edu.modelo.dao.ActivoDao;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

@WebServlet(name = "RegActivoBaja", urlPatterns = {"/RegActivoBaja"})
public class RegActivoBaja extends HttpServlet {
    private ActivoDao dao;
    private ArrayList<Activo> listaActivosDeBaja;

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

        System.out.println("SERVLET ACTIVO BAJA recibe opcion = " + filtro);

        if (filtro == null) {
            out.write(new JSONObject()
                    .put("resultado", "error")
                    .put("mensaje", "Parámetro 'opcion' es requerido")
                    .toString());
            return;
        }

        switch (filtro) {
            case "cargarTablaBaja": {
                JSONArray array = new JSONArray();
                JSONObject json = new JSONObject();

                try {
                    dao = new ActivoDao();
                    listaActivosDeBaja = dao.mostrarDeBaja();

                    StringBuilder html = new StringBuilder(1024);

                    html.append("<table id='tablaActivosBaja_id' ")
                        .append("class='display nowrap table table-striped align-middle' style='width:100%'>");

                    html.append("<thead><tr>")
                        .append("<th>Nombre</th>")
                        .append("<th>Unidad</th>")
                        .append("<th>Código</th>")
                        .append("<th>Fecha Compra</th>")
                        .append("<th>Estado Compra</th>")
                        .append("<th>Motivo de Baja</th>")
                        .append("</tr></thead>");

                    html.append("<tbody>");

                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    
                    for (Activo activo : listaActivosDeBaja) {
                        html.append("<tr>")
                            .append("<td>").append(nvl(activo.getNombre())).append("</td>")
                            .append("<td>").append(nvl(activo.getUnidad().getNombre())).append("</td>")
                            .append("<td>").append(nvl(activo.getCodigo())).append("</td>")
                            .append("<td>").append(activo.getFechaCompra() != null ? sdf.format(activo.getFechaCompra()) : "").append("</td>")
                            .append("<td>").append(nvl(activo.getEstadoDeCompra())).append("</td>")
                            .append("<td>").append(nvl(activo.getDescripcionEstado())).append("</td>")
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
        }
    }
}