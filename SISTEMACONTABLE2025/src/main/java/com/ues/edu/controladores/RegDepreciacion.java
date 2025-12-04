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

    private final Activo_DAO activoDao = new Activo_DAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idParam = request.getParameter("id");

        Activo activo = null;

        // Atributos que enviaremos al JSP
        Double valorResidualAttr          = null;
        Double valorSujetoAttr            = null; // Importe depreciable
        Double depreciacionAnualAttr      = null;
        Double depreciacionMensualAttr    = null;
        Double depreciacionDiariaAttr     = null;
        Double depreciacionAcumuladaAttr  = null;
        Double valorEnLibrosAttr          = null;

        // ==============================
        // SI HAY ID → BUSCAR ACTIVO
        // ==============================
        if (idParam != null && !idParam.trim().isEmpty()) {

            try {
                int id = Integer.parseInt(idParam);
                activo = activoDao.obtenerActivo(id);
            } catch (Exception e) {
                activo = null;
            }

            if (activo != null) {

                double precio = activo.getPrecioAdquisicion();
                double valorResidual = 0.0;
                double importeDepreciable;
                double depAnual;
                double depMensual;
                double depDiaria;
                double depAcumulada;
                double valorLibros;

                // ==============================
                // VIDA ÚTIL Y AÑOS DE USO
                // ==============================
                int vidaUtil = activo.getVidaUtil();   // viene de la BD (vidautil)
                int aniosUso = activo.getAniosUso();   // calculado con age()

                // Si vida útil viene 0 o negativa → usamos años de uso como respaldo
                if (vidaUtil <= 0) {
                    if (aniosUso > 0) {
                        vidaUtil = aniosUso;
                    } else {
                        vidaUtil = 1;  // mínimo 1 para evitar división entre 0
                    }
                    // Actualizamos el objeto para que la JSP no muestre 0
                    activo.setVidaUtil(vidaUtil);
                }

                // ==============================
                // 1. VALOR RESIDUAL
                // ==============================
                String estadoCompra = activo.getEstadoDeCompra();
                estadoCompra = (estadoCompra != null)
                        ? estadoCompra.trim().toLowerCase()
                        : "";

                /*
                 * NUEVO  → usa porcentaje de TipoCategoria como % de valor residual
                 * USADO  → usa porcentaje de TipoUsado como % de valor residual
                 *
                 * Ejemplo cuaderno:
                 * precio = 3500
                 * porcentaje = 20%
                 * valorResidual = 3500 * 20% = 700
                 */
                if ("nuevo".equals(estadoCompra)) {

                    if (activo.getTipoCategoria() != null) {
                        double p = activo.getTipoCategoria().getPorcentaje(); // ej. 20
                        valorResidual = precio * (p / 100.0);
                    }

                } else if ("usado".equals(estadoCompra)) {

                    TipoUsado tu = activo.getTipoUsado();

                    if (tu != null && tu.getPorcentaje() > 0) {
                        // ej. 20, 30, etc. (lo que definas como % residual)
                        valorResidual = precio * (tu.getPorcentaje() / 100.0);
                    } else {
                        // Respaldo si no viene TipoUsado configurado
                        if      (aniosUso == 1) valorResidual = precio * 0.80;
                        else if (aniosUso == 2) valorResidual = precio * 0.60;
                        else if (aniosUso == 3) valorResidual = precio * 0.40;
                        else                    valorResidual = precio * 0.20;
                    }
                }

                // ==============================
                // 2. IMPORTE DEPRECIABLE
                // ==============================
                importeDepreciable = precio - valorResidual;
                if (importeDepreciable < 0) {
                    importeDepreciable = 0;
                }

                // ==============================
                // 3. DEPRECIACIÓN ANUAL
                // ==============================
                // Fórmula: Importe depreciable ÷ vida útil
                // Ejemplo: 2,800 ÷ 2 = 1,400
                depAnual = importeDepreciable / vidaUtil;

                // ==============================
                // 4. DEPRECIACIÓN MENSUAL
                // ==============================
                // Fórmula: Depreciación anual ÷ 12
                // Ejemplo: 1,400 ÷ 12 = 116.67
                depMensual = depAnual / 12.0;

                // ==============================
                // 5. DEPRECIACIÓN DIARIA
                // ==============================
                // Política: 30 días por mes
                // Fórmula: Depreciación mensual ÷ 30
                // Ejemplo: 116.67 ÷ 30 = 3.89
                depDiaria = depMensual / 30.0;

                // ==============================
                // 6. DEPRECIACIÓN ACUMULADA
                // ==============================
                // Fórmula simple: depAnual × años de uso
                // Ejemplo: 1,400 × 2 = 2,800
                depAcumulada = depAnual * aniosUso;

                // No puede superar el importe depreciable
                if (depAcumulada > importeDepreciable) {
                    depAcumulada = importeDepreciable;
                }

                // ==============================
                // 7. VALOR EN LIBROS
                // ==============================
                // Valor en libros = precio de adquisición – depreciación acumulada
                // Ejemplo: 3,500 – 2,800 = 700
                valorLibros = precio - depAcumulada;
                if (valorLibros < 0) {
                    valorLibros = 0;
                }

                // ==============================
                // ENVIAR A LA VISTA
                // ==============================
                valorResidualAttr          = valorResidual;
                valorSujetoAttr            = importeDepreciable;
                depreciacionAnualAttr      = depAnual;
                depreciacionMensualAttr    = depMensual;
                depreciacionDiariaAttr     = depDiaria;
                depreciacionAcumuladaAttr  = depAcumulada;
                valorEnLibrosAttr          = valorLibros;
            }
        }

        // LISTA PARA EL COMBO
        List<Activo> listaActivos = activoDao.listarActivos();

        request.setAttribute("listaActivos", listaActivos);
        request.setAttribute("activo", activo);

        // Valores de cálculo
        request.setAttribute("valorResidual", valorResidualAttr);
        request.setAttribute("valorSujeto", valorSujetoAttr);
        request.setAttribute("anual",  depreciacionAnualAttr);
        request.setAttribute("mensual", depreciacionMensualAttr);
        request.setAttribute("diaria",  depreciacionDiariaAttr);
        request.setAttribute("acumulada", depreciacionAcumuladaAttr);
        request.setAttribute("libros", valorEnLibrosAttr);

        request.getRequestDispatcher("DepreciacionActivo.jsp").forward(request, response);
    }
}
