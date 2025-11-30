package com.ues.edu.controladores;

import com.ues.edu.modelo.Activo;
import com.ues.edu.modelo.TipoUsado;
import com.ues.edu.modelo.dao.Activo_DAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/depreciacion")
public class RegDepreciacion extends HttpServlet {

    private Activo_DAO activoDao = new Activo_DAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idParam = request.getParameter("id");

        Activo activo = null;
        Double valorSujeto = null;
        Double depreciacionAnual = null;
        Double depreciacionAcumulada = null;
        Double valorEnLibros = null;

        // ==============================
        // SI ENTRA CON ID → CALCULAMOS
        // ==============================
        if (idParam != null && !idParam.trim().isEmpty()) {

            try {
                int id = Integer.parseInt(idParam);
                activo = activoDao.obtenerActivo(id);
            } catch (NumberFormatException ex) {
                // id inválido, lo dejamos como null
            }

            if (activo != null) {

                // 1. Valor sujeto a depreciación
                double valor = activo.getPrecioAdquisicion();

                String estadoCompra = activo.getEstadoDeCompra();
                if (estadoCompra != null && estadoCompra.equalsIgnoreCase("usado")) {

                    TipoUsado tu = activo.getTipoUsado();

                    if (tu != null && tu.getPorcentaje() > 0) {
                        // Usamos la tabla tipousado: 80,60,40,20
                        valor = valor * (tu.getPorcentaje() / 100.0);
                    } else {
                        // Respaldo por años de uso
                        int u = activo.getAniosUso();
                        if (u == 1)       valor *= 0.80;
                        else if (u == 2)  valor *= 0.60;
                        else if (u == 3)  valor *= 0.40;
                        else              valor *= 0.20;
                    }
                }

                // 2. Porcentaje anual según categoría
                double porcentajeAnual = 0.0;
                if (activo.getTipoCategoria() != null) {
                    porcentajeAnual = activo.getTipoCategoria().getPorcentaje();
                }

                double anual = valor * (porcentajeAnual / 100.0);

                // 3. Depreciación acumulada
                int aniosUso = activo.getAniosUso();
                double acumulada = anual * aniosUso;

                // 4. Valor en libros
                double libros = valor - acumulada;
                if (libros < 0) {
                    libros = 0;
                }

                valorSujeto = valor;
                depreciacionAnual = anual;
                depreciacionAcumulada = acumulada;
                valorEnLibros = libros;
            }
        }

        // LISTA DE ACTIVOS PARA EL COMBO
        List<Activo> listaActivos = activoDao.listarActivos();

        // Atributos para la vista
        request.setAttribute("listaActivos", listaActivos);
        request.setAttribute("activo", activo);
        request.setAttribute("valorSujeto", valorSujeto);
        request.setAttribute("anual", depreciacionAnual);
        request.setAttribute("acumulada", depreciacionAcumulada);
        request.setAttribute("libros", valorEnLibros);

        request.getRequestDispatcher("DepreciacionActivo.jsp").forward(request, response);
    }
}