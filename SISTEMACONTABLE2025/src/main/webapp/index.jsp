<%-- 
    Document   : index
    Created on : 26 nov 2025
    Author     : Marlo
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">

    <head>
        <meta charset="UTF-8">
        <title>Inicio</title>

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
        </style>
    </head>

    <body>

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
                        <a href="index.jsp" class="nav-link active">
                            <i class="bi bi-house-door me-2"></i> Inicio
                        </a>
                    </li>

                    <li class="nav-item">
                        <a href="Institucion.jsp" class="nav-link text-white">
                            <i class="bi bi-building me-2"></i> Instituciones
                        </a>
                    </li>

                    <!-- NUEVO: enlace al módulo de usuarios -->
                    <li class="nav-item">
                        <a href="Usuarios.jsp" class="nav-link text-white">
                            <i class="bi bi-people me-2"></i> Usuarios
                        </a>
                    </li>

                    <!-- Agrega más opciones si las necesitas -->
                </ul>

                <!-- NUEVO: botón de cerrar sesión al final del menú -->
                <div class="mt-auto">
                    <hr class="text-secondary">
                    <a href="LoginServlet?accion=Logout" class="btn btn-outline-light w-100">
                        <i class="bi bi-box-arrow-right me-2"></i> Cerrar sesión
                    </a>
                </div>
            </nav>
            <!--FIN MENÚ LATERAL-->

            <!--CONTENIDO PRINCIPAL-->
            <div class="flex-grow-1">
                <div class="main-wrapper">
                    <div class="page-wrapper">
                        <div class="content">

                            <div class="m-3">
                                <div class="container-fluid">

                                    <div class="row">
                                        <div class="col-12">
                                            <div class="card mb-3">
                                                <div class="card-body">
                                                    <h3 class="fw-bold mb-2">
                                                        Bienvenido al sistema
                                                    </h3>
                                                    <p class="text-muted mb-0">
                                                        Usa el menú lateral para navegar entre los módulos
                                                        de <strong>Instituciones</strong>, <strong>Unidad</strong> u otros.
                                                    </p>
                                                </div>
                                            </div>
                                        </div>
                                    </div>

                                    <!-- PARA INSTITUCIONES -->
                                    <div class="row">
                                        <div class="col-md-4 mb-3">
                                            <div class="card shadow-sm">
                                                <div class="card-body">
                                                    <div class="d-flex align-items-center">
                                                        <div class="me-3">
                                                            <i class="bi bi-building fs-1 text-primary"></i>
                                                        </div>
                                                        <div>
                                                            <h5 class="card-title mb-1">Instituciones</h5>
                                                            <p class="card-text small text-muted">
                                                                Gestiona las instituciones registradas.
                                                            </p>
                                                            <a href="Institucion.jsp" class="btn btn-sm btn-outline-primary">
                                                                Ir al módulo
                                                            </a>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>

                                        <!-- PARA UNIDADES -->
                                        <div class="col-md-4 mb-3">
                                            <div class="card shadow-sm">
                                                <div class="card-body">
                                                    <div class="d-flex align-items-center">
                                                        <div class="me-3">
                                                            <!-- corregido icono -->
                                                            <i class="bi bi-diagram-3 fs-1 text-success"></i>
                                                        </div>
                                                        <div>
                                                            <h5 class="card-title mb-1">Unidades</h5>
                                                            <p class="card-text small text-muted">
                                                                Gestiona las unidades registradas.
                                                            </p>
                                                            <a href="Personas.jsp" class="btn btn-sm btn-outline-success">
                                                                Ir al módulo
                                                            </a>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>

                                        <!-- libre para futuras secciones -->

                                    </div>

                                </div>
                            </div>

                        </div>
                    </div>
                </div>
            </div>
            <!--FIN CONTENIDO PRINCIPAL-->

        </div>

    </body>
</html>
