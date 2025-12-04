/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ues.edu.controladores;

import com.ues.edu.modelo.Activo;
import com.ues.edu.modelo.TipoCategoria;
import com.ues.edu.modelo.TipoUsado;
import com.ues.edu.modelo.Unidad;
import com.ues.edu.modelo.UnidadComboDTO;
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
import java.util.Date;
import org.json.JSONArray;
import org.json.JSONObject;

@WebServlet(name = "RegActivo", urlPatterns = {"/RegActivo"})
public class RegActivo extends HttpServlet {

    private ActivoDao dao;
    private ArrayList<Activo> listaActivosNuevos;
    private ArrayList<Activo> listaActivosUsados;
    private ArrayList<UnidadComboDTO> listaUnidadesCombo;
    private ArrayList<TipoCategoria> listaTipoCategoria;
    private ArrayList<TipoUsado> listaTipoUsado;

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

        System.out.println("SERVLET ACTIVO recibe opcion = " + filtro);

        if (filtro == null) {
            out.write(new JSONObject()
                    .put("resultado", "error")
                    .put("mensaje", "Parámetro 'opcion' es requerido")
                    .toString());
            return;
        }

        switch (filtro) {
            case "cargarCombos": {
                JSONArray array = new JSONArray();
                JSONObject json = new JSONObject();

                try {
                    dao = new ActivoDao();

                    // Obtener datos para los combos
                    listaUnidadesCombo = dao.obtenerUnidadesConInstitucion();
                    listaTipoCategoria = dao.obtenerTipoCategoria();
                    listaTipoUsado = dao.obtenerTipoUsado();

                    // Unidades
                    JSONArray unidadesArray = new JSONArray();
                    for (UnidadComboDTO unidad : listaUnidadesCombo) {
                        JSONObject unidadJson = new JSONObject();
                        unidadJson.put("idUnidad", unidad.getIdUnidad());
                        unidadJson.put("nombreUnidad", unidad.getNombreUnidad());
                        unidadJson.put("nombreInstitucion", unidad.getNombreInstitucion());
                        unidadJson.put("idInstitucion", unidad.getIdInstitucion());  // ✅ USAR ID COMO CÓDIGO
                        unidadesArray.put(unidadJson);
                    }

                    // Tipos Categoría
                    JSONArray tiposArray = new JSONArray();
                    for (TipoCategoria tipo : listaTipoCategoria) {
                        JSONObject tipoJson = new JSONObject();
                        tipoJson.put("idTipo", tipo.getIdTipo());
                        tipoJson.put("nombre", tipo.getNombre());
                        tiposArray.put(tipoJson);
                    }

                    // Tipos Usado
                    JSONArray tiposUsadoArray = new JSONArray();
                    for (TipoUsado tipoUsado : listaTipoUsado) {
                        JSONObject tipoUsadoJson = new JSONObject();
                        tipoUsadoJson.put("idTipoUsado", tipoUsado.getIdTipoUsado());
                        tipoUsadoJson.put("porcentaje", tipoUsado.getPorcentaje());
                        tipoUsadoJson.put("anyos", tipoUsado.getAnyos());
                        tiposUsadoArray.put(tipoUsadoJson);
                    }

                    json.put("resultado", "exito")
                            .put("unidades", unidadesArray)
                            .put("tiposCategoria", tiposArray)
                            .put("tiposUsado", tiposUsadoArray);

                } catch (Exception e) {
                    json.put("resultado", "error_sql")
                            .put("mensaje", e.getMessage());
                    e.printStackTrace();
                }

                array.put(json);
                out.write(array.toString());
                break;
            }
            case "obtenerSiguienteCorrelativo": {
                JSONArray array = new JSONArray();
                JSONObject json = new JSONObject();

                try {
                    dao = new ActivoDao();
                    int ultimoCorrelativo = dao.obtenerUltimoCorrelativo();
                    int siguienteCorrelativo = ultimoCorrelativo + 1;

                    // Formatear a 4 dígitos con ceros a la izquierda
                    String correlativoFormateado = String.format("%04d", siguienteCorrelativo);

                    json.put("resultado", "exito")
                            .put("siguienteCorrelativo", siguienteCorrelativo)
                            .put("correlativoFormateado", correlativoFormateado);

                } catch (Exception e) {
                    json.put("resultado", "error_sql")
                            .put("mensaje", e.getMessage());
                    e.printStackTrace();
                }

                array.put(json);
                out.write(array.toString());
                break;
            }
            case "cargarTablaNuevos": {
                JSONArray array = new JSONArray();
                JSONObject json = new JSONObject();

                try {
                    dao = new ActivoDao();
                    listaActivosNuevos = dao.mostrarNuevos();

                    StringBuilder html = new StringBuilder(1024);

                    html.append("<table id='tablaActivosNuevos_id' ")
                            .append("class='display nowrap table table-striped align-middle' style='width:100%'>");

                    html.append("<thead><tr>")
                            .append("<th>Nombre</th>")
                            .append("<th>Unidad</th>")
                            .append("<th>Tipo</th>")
                            .append("<th>Código</th>")
                            .append("<th>Características</th>")
                            .append("<th>Fecha Compra</th>")
                            .append("<th>Vida Útil</th>")
                            .append("<th>Precio Adquisición</th>")
                            .append("<th class='text-center'>Dar de Baja</th>")
                            .append("</tr></thead>");

                    html.append("<tbody>");

                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

                    for (Activo activo : listaActivosNuevos) {
                        html.append("<tr>")
                                .append("<td>").append(nvl(activo.getNombre())).append("</td>")
                                .append("<td>").append(nvl(activo.getUnidad().getNombre())).append("</td>")
                                .append("<td>").append(nvl(activo.getTipoCategoria().getNombre())).append("</td>")
                                .append("<td>").append(nvl(activo.getCodigo())).append("</td>")
                                .append("<td>").append(nvl(activo.getCaracteristicas())).append("</td>")
                                .append("<td>").append(activo.getFechaCompra() != null ? sdf.format(activo.getFechaCompra()) : "").append("</td>")
                                .append("<td>").append(nvl(activo.getVidaUtil())).append(" años</td>")
                                .append("<td>$").append(nvl(activo.getPrecioAdquisicion())).append("</td>")
                                .append("<td class='text-center'>")
                                .append("<div class='btn-group'>")
                                .append("<button class='btn btn-sm btn-outline-danger btn_dar_baja' ")
                                .append("data-id='").append(activo.getId()).append("' title='Dar de Baja'>")
                                .append("<i class='bi bi-arrow-down-circle'></i></button>")
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

            case "cargarTablaUsados": {
                JSONArray array = new JSONArray();
                JSONObject json = new JSONObject();

                try {
                    dao = new ActivoDao();
                    listaActivosUsados = dao.mostrarUsados();

                    StringBuilder html = new StringBuilder(1024);

                    html.append("<table id='tablaActivosUsados_id' ")
                            .append("class='display nowrap table table-striped align-middle' style='width:100%'>");

                    html.append("<thead><tr>")
                            .append("<th>Nombre</th>")
                            .append("<th>Unidad</th>")
                            .append("<th>Tipo</th>")
                            .append("<th>Código</th>")
                            .append("<th>Características</th>")
                            .append("<th>Fecha Compra</th>")
                            .append("<th>Vida Útil</th>")
                            .append("<th>Precio Adquisición</th>")
                            .append("<th>Precio Usado</th>")
                            .append("<th class='text-center'>Dar de Baja</th>")
                            .append("</tr></thead>");

                    html.append("<tbody>");

                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

                    for (Activo activo : listaActivosUsados) {
                        html.append("<tr>")
                                .append("<td>").append(nvl(activo.getNombre())).append("</td>")
                                .append("<td>").append(nvl(activo.getUnidad().getNombre())).append("</td>")
                                .append("<td>").append(nvl(activo.getTipoCategoria().getNombre())).append("</td>")
                                .append("<td>").append(nvl(activo.getCodigo())).append("</td>")
                                .append("<td>").append(nvl(activo.getCaracteristicas())).append("</td>")
                                .append("<td>").append(activo.getFechaCompra() != null ? sdf.format(activo.getFechaCompra()) : "").append("</td>")
                                .append("<td>").append(nvl(activo.getVidaUtil())).append(" años</td>")
                                .append("<td>$").append(nvl(activo.getPrecioAdquisicion())).append("</td>")
                                .append("<td>$").append(nvl(activo.getPrecioUsado())).append("</td>")
                                .append("<td class='text-center'>")
                                .append("<div class='btn-group'>")
                                .append("<button class='btn btn-sm btn-outline-danger btn_dar_baja' ")
                                .append("data-id='").append(activo.getId()).append("' title='Dar de Baja'>")
                                .append("<i class='bi bi-arrow-down-circle'></i></button>")
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

            case "si_registro_nuevo": {
                JSONArray array = new JSONArray();
                JSONObject json = new JSONObject();

                try {
                    dao = new ActivoDao();
                    Activo activo = new Activo();

                    // Configurar el activo
                    activo.setNombre(req.getParameter("txt_nombre"));

                    Unidad unidad = new Unidad();
                    unidad.setId(Integer.parseInt(req.getParameter("cmb_unidad")));
                    activo.setUnidad(unidad);

                    TipoCategoria tipo = new TipoCategoria();
                    tipo.setIdTipo(Integer.parseInt(req.getParameter("cmb_tipo")));
                    activo.setTipoCategoria(tipo);

                    activo.setCodigo(req.getParameter("txt_codigo"));
                    activo.setCorrelativo(Integer.parseInt(req.getParameter("txt_correlativo")));
                    activo.setCaracteristicas(req.getParameter("txt_caracteristicas"));

                    // Convertir fecha
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date fechaCompra = sdf.parse(req.getParameter("txt_fecha_compra"));
                    activo.setFechaCompra(fechaCompra);

                    activo.setVidaUtil(Integer.parseInt(req.getParameter("txt_vida_util")));
                    activo.setEstadoActivo(true); // Siempre true según el DAO
                    activo.setPrecioAdquisicion(Double.parseDouble(req.getParameter("txt_precio_adquisicion")));
                    activo.setEstadoDeCompra("Nuevo");

                    String r = dao.insertarNuevo(activo);

                    if ("exito".equals(r)) {
                        json.put("resultado", "exito")
                                .put("mensaje", "Activo nuevo registrado correctamente.");
                    } else {
                        json.put("resultado", "error_sql")
                                .put("mensaje", r);
                    }

                } catch (Exception e) {
                    json.put("resultado", "error_sql")
                            .put("mensaje", e.getMessage());
                    e.printStackTrace();
                }

                array.put(json);
                out.write(array.toString());
                break;
            }

            case "si_registro_usado": {
                JSONArray array = new JSONArray();
                JSONObject json = new JSONObject();

                try {
                    dao = new ActivoDao();
                    Activo activo = new Activo();

                    // Configurar el activo
                    activo.setNombre(req.getParameter("txt_nombre"));

                    Unidad unidad = new Unidad();
                    unidad.setId(Integer.parseInt(req.getParameter("cmb_unidad")));
                    activo.setUnidad(unidad);

                    TipoCategoria tipo = new TipoCategoria();
                    tipo.setIdTipo(Integer.parseInt(req.getParameter("cmb_tipo")));
                    activo.setTipoCategoria(tipo);

                    TipoUsado tipoUsado = new TipoUsado();
                    tipoUsado.setIdTipoUsado(Integer.parseInt(req.getParameter("cmb_tipo_usado")));
                    activo.setTipoUsado(tipoUsado);

                    activo.setCodigo(req.getParameter("txt_codigo"));
                    activo.setCorrelativo(Integer.parseInt(req.getParameter("txt_correlativo")));
                    activo.setCaracteristicas(req.getParameter("txt_caracteristicas"));

                    // Convertir fecha
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date fechaCompra = sdf.parse(req.getParameter("txt_fecha_compra"));
                    activo.setFechaCompra(fechaCompra);

                    activo.setVidaUtil(Integer.parseInt(req.getParameter("txt_vida_util")));
                    activo.setEstadoActivo(true); // Siempre true según el DAO
                    activo.setPrecioAdquisicion(Double.parseDouble(req.getParameter("txt_precio_adquisicion")));
                    activo.setPrecioUsado(Double.parseDouble(req.getParameter("txt_precio_usado")));
                    activo.setEstadoDeCompra("Usado");

                    String r = dao.insertarUsado(activo);

                    if ("exito".equals(r)) {
                        json.put("resultado", "exito")
                                .put("mensaje", "Activo usado registrado correctamente.");
                    } else {
                        json.put("resultado", "error_sql")
                                .put("mensaje", r);
                    }

                } catch (Exception e) {
                    json.put("resultado", "error_sql")
                            .put("mensaje", e.getMessage());
                    e.printStackTrace();
                }

                array.put(json);
                out.write(array.toString());
                break;
            }
            case "obtenerActivoPorId": {
                JSONArray array = new JSONArray();
                JSONObject json = new JSONObject();

                try {
                    dao = new ActivoDao();
                    int idActivo = Integer.parseInt(req.getParameter("idActivo"));

                    Activo activo = dao.obtenerActivoPorId(idActivo);

                    if (activo != null) {
                        json.put("resultado", "exito")
                                .put("id", activo.getId())
                                .put("nombre", activo.getNombre())
                                .put("codigo", activo.getCodigo())
                                .put("estadoActual", activo.getEstadoActivo());
                    } else {
                        json.put("resultado", "no_encontrado")
                                .put("mensaje", "No se encontró el activo solicitado.");
                    }

                } catch (Exception e) {
                    json.put("resultado", "error_sql")
                            .put("mensaje", e.getMessage());
                    e.printStackTrace();
                }

                array.put(json);
                out.write(array.toString());
                break;
            }

            case "actualizarEstadoActivo": {
                JSONArray array = new JSONArray();
                JSONObject json = new JSONObject();

                try {
                    dao = new ActivoDao();
                    int idActivo = Integer.parseInt(req.getParameter("idActivo"));
                    String descripcionEstado = req.getParameter("descripcionEstado");
                    boolean nuevoEstado = false;

                    boolean actualizado = dao.actualizarEstadoActivo(idActivo, descripcionEstado, nuevoEstado);

                    if (actualizado) {
                        String mensaje = nuevoEstado
                                ? "Activo reactivado correctamente."
                                : "Activo dado de baja correctamente.";

                        json.put("resultado", "exito")
                                .put("mensaje", mensaje);
                    } else {
                        json.put("resultado", "error_actualizacion")
                                .put("mensaje", "No se pudo actualizar el estado del activo.");
                    }

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
