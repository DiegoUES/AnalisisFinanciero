<%-- 
    Document   : ActivoBaja
    Created on : 1 dic 2025, 10:23:04 a. m.
    Author     : mayel
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    // ==============================
    // OBTENER ROL DESDE LA SESIÓN
    // ==============================
    String rol = "";
    if (session != null) {
        Object rolAttr = session.getAttribute("rol");
        if (rolAttr != null) {
            rol = rolAttr.toString();
        } else {
            Object usuarioAttr = session.getAttribute("usuario");
            if (usuarioAttr != null) {
                try {
                    com.ues.edu.modelo.Usuario u = (com.ues.edu.modelo.Usuario) usuarioAttr;
                    if (u.getRol() != null && u.getRol().getNombre() != null) {
                        rol = u.getRol().getNombre();
                    }
                } catch (Exception e) {
                    // Si falla el cast, rol queda vacío
                }
            }
        }
    }

    boolean esAdmin = "ADMIN".equalsIgnoreCase(rol) || "ADMINISTRADOR".equalsIgnoreCase(rol);

    // Solo ADMIN puede entrar a ActivoBaja.jsp
    if (!esAdmin) {
        response.sendRedirect("index.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="es">

    <head>
        <meta charset="UTF-8">
        <title>Gestión de Activo Fijo</title>

        <!-- jQuery -->
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.0/jquery.min.js"></script>

        <!-- Parsley -->
        <script src="http://parsleyjs.org/dist/parsley.js"></script>

        <!-- SweetAlert2 -->
        <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>

        <!-- Bootstrap CSS + JS -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/js/bootstrap.bundle.min.js"></script>

        <!-- DataTables -->
        <link rel="stylesheet" href="https://cdn.datatables.net/1.10.25/css/jquery.dataTables.min.css">
        <script src="https://cdn.datatables.net/1.10.25/js/jquery.dataTables.min.js"></script>

        <!-- Bootstrap Icons -->
        <link rel="stylesheet"
              href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">

        <!-- ESTILO PARA EL MENÚ LATERAL -->
        <style>
            body {
                margin: 0;
            }

            .layout-wrapper {
                display: flex;
                min-height: 100vh; /* Ocupa toda la altura */
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

            .tab-content {
                padding: 20px;
                border: 1px solid #dee2e6;
                border-top: none;
                border-radius: 0 0 0.375rem 0.375rem;
            }

            .btn-group-activos {
                display: flex;
                gap: 10px;
            }
        </style>
    </head>

    <body>

        <!-- AQUI COMIENZA EL MENÚ LATERAL -->
        <div class="layout-wrapper">

            <!--MENÚ LATERAL-->
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

                    <!-- Solo ADMIN ve Usuarios -->
                    <li class="nav-item">
                        <a href="Usuarios.jsp" class="nav-link text-white">
                            <i class="bi bi-building me-2"></i> Usuarios
                        </a>
                    </li>

                    <li class="nav-item">
                        <a href="Institucion.jsp" class="nav-link text-white">
                            <i class="bi bi-building me-2"></i> Instituciones
                        </a>
                    </li>

                    <li class="nav-item">
                        <a href="Activo.jsp" class="nav-link text-white">
                            <i class="bi bi-box-seam me-2"></i>Activo Fijo
                        </a>
                    </li>

                    <!-- Activo Baja (página actual) -->
                    <li class="nav-item">
                        <a href="ActivoBaja.jsp" class="nav-link active">
                            <i class="bi bi-box-seam me-2"></i>Activo Baja
                        </a>
                    </li>

                    <li class="nav-item">
                        <a href="DepreciacionActivo.jsp" class="nav-link text-white">
                            <i class="bi bi-box-seam me-2"></i>Depreciacion
                        </a>
                    </li>

                    <li class="nav-item">
                        <a href="TipoCategoria.jsp" class="nav-link text-white">
                            <i class="bi bi-box-seam me-2"></i>Tipo Categoria
                        </a>
                    </li>

                    <li class="nav-item">
                        <a href="TipoUsado.jsp" class="nav-link text-white">
                            <i class="bi bi-box-seam me-2"></i>Tipo Usado
                        </a>
                    </li>

                    <!-- Solo ADMIN ve Unidad -->
                    <li class="nav-item">
                        <a href="Unidad.jsp" class="nav-link text-white">
                            <i class="bi bi-box-seam me-2"></i>Unidad
                        </a>
                    </li>
                </ul>
            </nav>
            <!--FIN MENÚ LATERAL-->

            <!--CONTENIDO PRINCIPAL-->
            <div class="flex-grow-1">
                <div class="main-wrapper">
                    <div class="page-wrapper">
                        <div class="content">

                            <div class="m-3 embed-responsive" style="height: 630px;">
                                <div class="container-fluid">
                                    <div class="card">
                                        <div class="card-body">

                                            <h4 class="fw-bold py-3 mb-2">
                                                <span class="text-muted fw-light">BIENVENIDO A: </span> Activo Fijo dados de Baja
                                            </h4>

                                            <div class="mb-3 btn-group-activos">
                                                <button type="button"
                                                        id="btn_irAActivoPrincipal"
                                                        class="btn btn-outline-success">
                                                    <i class="bi bi-arrow-repeat"></i> Ir a Activo Fijo
                                                </button>
                                            </div>

                                            <div class="tab-content" id="myTabContent">
                                                <!-- TABLA ACTIVOS DE BAJA -->
                                                <div class="tab-pane fade show active" id="baja" role="tabpanel">
                                                    <div id="tablaActivosBaja"></div>
                                                </div>
                                            </div>

                                        </div>
                                    </div>
                                </div>
                            </div>

                        </div>
                    </div>
                </div>
            </div>
            <!--FIN CONTENIDO PRINCIPAL-->

        </div>

        <!-- JS DE LA VISTA-->
        <script src="ActivoBaja.js"></script>

    </body>
</html>
