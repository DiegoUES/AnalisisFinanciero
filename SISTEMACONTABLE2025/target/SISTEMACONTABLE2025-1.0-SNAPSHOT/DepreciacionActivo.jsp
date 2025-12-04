<%-- 
    Document   : DepreciacionActivo
    Created on : 28 nov 2025
    Author     : alexa
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="com.ues.edu.modelo.Activo"%>
<%@page import="com.ues.edu.modelo.dao.Activo_DAO"%>
<!DOCTYPE html>
<html lang="es">

<head>
    <meta charset="UTF-8">
    <title>Depreciación del Activo</title>

    <!-- jQuery -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.0/jquery.min.js"></script>

    <!-- SweetAlert2 -->
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>

    <!-- Bootstrap CSS + JS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/js/bootstrap.bundle.min.js"></script>

    <!-- Bootstrap Icons -->
    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">

    <!-- Select2 (combo con búsqueda) -->
    <link href="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/css/select2.min.css" rel="stylesheet" />
    <script src="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/js/select2.min.js"></script>

    <!-- ESTILO PARA EL MENÚ LATERAL -->
    <style>
        body {
            margin: 0;
        }

        .layout-wrapper {
            display: flex;
            min-height: 100vh;
        }

        #sidebar {
            width: 230px;
        }

        #sidebar .nav-link {
            color: #ffffff;
        }

        #sidebar .nav-link.active {
            background-color: #0d6efd;
        }

        #sidebar .nav-link:hover {
            background-color: rgba(255, 255, 255, 0.1);
        }

        .valor-num {
            text-align: right;
        }
    </style>
</head>

<body>

<%
    // ==============================
    // TOMAR ATRIBUTOS DEL SERVLET
    // ==============================
    List<Activo> listaActivos = (List<Activo>) request.getAttribute("listaActivos");
    Activo activoSel          = (Activo) request.getAttribute("activo");

    Double valorResidual      = (Double) request.getAttribute("valorResidual");
    Double valorSujeto        = (Double) request.getAttribute("valorSujeto");
    Double anual              = (Double) request.getAttribute("anual");
    Double mensual            = (Double) request.getAttribute("mensual");
    Double diaria             = (Double) request.getAttribute("diaria");
    Double acumulada          = (Double) request.getAttribute("acumulada");
    Double libros             = (Double) request.getAttribute("libros");

    // SI ENTRARON DIRECTO AL JSP (SIN PASAR POR EL SERVLET),
    // CARGAMOS LA LISTA DESDE EL DAO PARA QUE EL COMBO NO QUEDE VACÍO
    if (listaActivos == null) {
        Activo_DAO daoTmp = new Activo_DAO();
        listaActivos = daoTmp.listarActivos();
    }
%>

<div class="layout-wrapper">

    <!-- MENÚ LATERAL -->
    <nav id="sidebar" class="bg-dark text-white p-3 d-flex flex-column">
        <div class="d-flex align-items-center mb-3">
            <i class="bi bi-stack me-2 fs-4"></i>
            <span class="fs-5 fw-semibold">Menú</span>
        </div>
        <hr class="text-secondary">

        <ul class="nav nav-pills flex-column mb-auto">
            <li class="nav-item">
                <a href="index.jsp" class="nav-link text-white">
                    <i class="bi bi-house-door me-2"></i> Inicio
                </a>
            </li>

            <li class="nav-item">
                <a href="Institucion.jsp" class="nav-link text-white">
                    <i class="bi bi-building me-2"></i> Instituciones
                </a>
            </li>
        </ul>
    </nav>
    <!-- FIN MENÚ LATERAL -->

    <!-- CONTENIDO PRINCIPAL -->
    <div class="flex-grow-1">
        <div class="main-wrapper">
            <div class="page-wrapper">
                <div class="content">

                    <div class="m-3">
                        <div class="container-fluid">

                            <div class="card">
                                <div class="card-body">

                                    <!-- TÍTULO -->
                                    <h4 class="fw-bold py-3 mb-3">
                                        <span class="text-muted fw-light">BIENVENIDO A:</span> Depreciación de Activo Fijo
                                    </h4>

                                    <!-- COMBO BUSCADOR DE ACTIVO -->
                                    <form id="formActivo" method="get" action="depreciacion" class="row g-3 mb-3">
                                        <div class="col-md-8">
                                            <label for="selectActivo" class="form-label">
                                                Seleccione un activo para calcular la depreciación
                                            </label>
                                            <select id="selectActivo" name="id" class="form-select">
                                                <option value="">-- Buscar / seleccionar --</option>
                                                <%
                                                    if (listaActivos != null) {
                                                        for (Activo a : listaActivos) {
                                                            boolean selected = (activoSel != null && a.getId() == activoSel.getId());
                                                %>
                                                    <option value="<%= a.getId() %>" <%= selected ? "selected" : "" %>>
                                                        <%= a.getNombre() %>
                                                        <%
                                                            String cod = a.getCodigo();
                                                            if (cod != null && !cod.isEmpty()) {
                                                        %>
                                                            ( <%= cod %> )
                                                        <%
                                                            }
                                                        %>
                                                    </option>
                                                <%
                                                        }
                                                    }
                                                %>
                                            </select>
                                        </div>
                                    </form>

                                    <% if (activoSel == null) { %>
                                        <div class="alert alert-info">
                                            Seleccione un activo del listado para ver su depreciación.
                                        </div>
                                    <% } %>

                                    <!-- DATOS BÁSICOS DEL ACTIVO -->
                                    <% if (activoSel != null) { %>
                                        <div class="mb-3">
                                            <p class="mb-1">
                                                <strong>Activo:</strong> <%= activoSel.getNombre() %>
                                            </p>
                                            <p class="mb-1">
                                                <strong>Código:</strong> <%= activoSel.getCodigo() != null ? activoSel.getCodigo() : "" %>
                                            </p>
                                            <p class="mb-0">
                                                <strong>Estado del activo:</strong> <%= activoSel.getEstadoDeCompra()!= null ? activoSel.getEstadoDeCompra(): "" %>
                                            </p>
                                        </div>
                                    <% } %>

                                    <!-- TABLA DE DEPRECIACIÓN -->
                                    <div class="table-responsive">
                                        <table class="table table-bordered align-middle">
                                            <thead class="table-light">
                                            <tr>
                                                <th style="width: 40%;">Campo</th>
                                                <th style="width: 60%;">Valor</th>
                                            </tr>
                                            </thead>
                                            <tbody>
                                            <tr>
                                                <td>Fecha de compra</td>
                                                <td><%= (activoSel != null && activoSel.getFechaCompra() != null) ? activoSel.getFechaCompra() : "" %></td>
                                            </tr>
                                            <tr>
                                                <td>Estado de compra</td>
                                                <td><%= (activoSel != null && activoSel.getEstadoDeCompra() != null) ? activoSel.getEstadoDeCompra() : "" %></td>
                                            </tr>
                                            <tr>
                                                <td>Vida útil (años)</td>
                                                <td>
                                                    <%
                                                        if (activoSel != null) {
                                                            int vu = activoSel.getVidaUtil();
                                                            int au = activoSel.getAniosUso();
                                                            if (vu <= 0 && au > 0) {
                                                                out.print(au);   // respaldo: años de uso
                                                            } else if (vu > 0) {
                                                                out.print(vu);
                                                            }
                                                        }
                                                    %>
                                                </td>
                                            </tr>
                                            
                                            <tr>
                                                <td>Precio de adquisición</td>
                                                <td class="valor-num"><%= (activoSel != null) ? activoSel.getPrecioAdquisicion() : "" %></td>
                                            </tr>
                                            <tr>
                                                <td>Valor residual</td>
                                                <td class="valor-num"><%= (valorResidual != null) ? String.format("%.2f", valorResidual) : "" %></td>
                                            </tr>
                                            <tr>
                                                <td>Valor sujeto a depreciación (importe depreciable)</td>
                                                <td class="valor-num"><%= (valorSujeto != null) ? String.format("%.2f", valorSujeto) : "" %></td>
                                            </tr>
                                            <tr>
                                                <td>Depreciación anual</td>
                                                <td class="valor-num"><%= (anual != null) ? String.format("%.2f", anual) : "" %></td>
                                            </tr>
                                            <tr>
                                                <td>Depreciación mensual</td>
                                                <td class="valor-num"><%= (mensual != null) ? String.format("%.2f", mensual) : "" %></td>
                                            </tr>
                                            <tr>
                                                <td>Depreciación diaria</td>
                                                <td class="valor-num"><%= (diaria != null) ? String.format("%.2f", diaria) : "" %></td>
                                            </tr>
                                            
                                            
                                            </tbody>
                                        </table>
                                    </div>

                                    <!-- BOTONES -->
                                    <div class="mt-3">
                                        <a href="index.jsp" class="btn btn-outline-secondary">
                                            <i class="bi bi-arrow-left"></i> Volver al inicio
                                        </a>
                                    </div>

                                </div>
                            </div>

                        </div>
                    </div>

                </div>
            </div>
        </div>
    </div>
    <!-- FIN CONTENIDO PRINCIPAL -->

</div>

<!-- INICIALIZAR SELECT2 -->
<script>
    $(function () {
        $('#selectActivo').select2({
            placeholder: 'Buscar activo por nombre',
            allowClear: true,
            width: '100%'
        });

        $('#selectActivo').on('change', function () {
            if (this.value) {
                $('#formActivo').submit();
            }
        });
    });
</script>

</body>
</html>
