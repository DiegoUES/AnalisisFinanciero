<%-- 
    Document   : index
    Created on : 26 nov 2025
    Author     : Marlo
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%
    // ======================================
    // OBTENER ROL DE LA SESIÓN (MISMA LÓGICA QUE EN Usuarios.jsp)
    // ======================================
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
                    // Si falla el cast, rol se queda vacío
                }
            }
        }
    }

    boolean esAdmin   = "ADMIN".equalsIgnoreCase(rol) || "ADMINISTRADOR".equalsIgnoreCase(rol);
    boolean esUsuario = "USUARIO".equalsIgnoreCase(rol) || "USER".equalsIgnoreCase(rol);
%>

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
                    <!-- Inicio (todos los roles) -->
                    <li class="nav-item">
                        <a href="index.jsp" class="nav-link active">
                            <i class="bi bi-house-door me-2"></i> Inicio
                        </a>
                    </li>

                    <!-- Usuarios: SOLO ADMIN -->
                    <% if (esAdmin) { %>
                    <li class="nav-item">
                        <a href="Usuarios.jsp" class="nav-link text-white">
                            <i class="bi bi-building me-2"></i> Usuarios
                        </a>
                    </li>
                    <% } %>

                    <!-- Instituciones: ADMIN + USUARIO -->
                    <li class="nav-item">
                        <a href="Institucion.jsp" class="nav-link text-white">
                            <i class="bi bi-building me-2"></i> Instituciones
                        </a>
                    </li>

                    <!-- Activo Fijo: ADMIN + USUARIO -->
                    <li class="nav-item">
                        <a href="Activo.jsp" class="nav-link text-white">
                            <i class="bi bi-box-seam me-2"></i>Activo Fijo
                        </a>
                    </li>

                    <!-- Activo Baja: SOLO ADMIN -->
                    <% if (esAdmin) { %>
                    <li class="nav-item">
                        <a href="ActivoBaja.jsp" class="nav-link text-white">
                            <i class="bi bi-box-seam me-2"></i>Activo Baja
                        </a>
                    </li>
                    <% } %>

                    <!-- Depreciación: ADMIN + USUARIO -->
                    <li class="nav-item">
                        <a href="DepreciacionActivo.jsp" class="nav-link text-white">
                            <i class="bi bi-box-seam me-2"></i>Depreciacion
                        </a>
                    </li>

                    <!-- Tipo Categoria: ADMIN + USUARIO -->
                    <li class="nav-item">
                        <a href="TipoCategoria.jsp" class="nav-link text-white">
                            <i class="bi bi-box-seam me-2"></i>Tipo Categoria
                        </a>
                    </li>

                    <!-- Tipo Usado: ADMIN + USUARIO -->
                    <li class="nav-item">
                        <a href="TipoUsado.jsp" class="nav-link text-white">
                            <i class="bi bi-box-seam me-2"></i>Tipo Usado
                        </a>
                    </li>

                    <!-- Unidad: SOLO ADMIN -->
                    <% if (esAdmin) { %>
                    <li class="nav-item">
                        <a href="Unidad.jsp" class="nav-link text-white">
                            <i class="bi bi-box-seam me-2"></i>Unidad
                        </a>
                    </li>
                    <% } %>
                </ul>
            </nav>
            <!--FIN MENÚ LATERAL-->

            <!--CONTENIDO PRINCIPAL-->
            <div class="flex-grow-1">
                <div class="main-wrapper">
                    <div class="page-wrapper">
                        <div class="content">

                            <div class="m-3">
                                <div class="container-fluid">

                                    <!-- TARJETA DE BIENVENIDA -->
                                    <div class="row">
                                        <div class="col-12">
                                            <div class="card mb-3">
                                                <div class="card-body">
                                                    <h3 class="fw-bold mb-2">
                                                        Bienvenido al sistema
                                                    </h3>
                                                    <p class="text-muted mb-0">
                                                        Usa el menú lateral para navegar entre los módulos
                                                        disponibles según tu rol, como 
                                                        <strong>Instituciones</strong>, 
                                                        <strong>Activo Fijo</strong> y otros.
                                                    </p>
                                                </div>
                                            </div>
                                        </div>
                                    </div>

                                    <!-- TARJETAS DE ACCESO RÁPIDO -->
                                    <div class="row">

                                        <!-- PARA INSTITUCIONES (ADMIN + USUARIO) -->
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

                                        <!-- PARA UNIDADES (SOLO ADMIN) -->
                                        <% if (esAdmin) { %>
                                        <div class="col-md-4 mb-3">
                                            <div class="card shadow-sm">
                                                <div class="card-body">
                                                    <div class="d-flex align-items-center">
                                                        <div class="me-3">
                                                            <i class="bi bi-diagram-3 fs-1 text-success"></i>
                                                        </div>
                                                        <div>
                                                            <h5 class="card-title mb-1">Unidades</h5>
                                                            <p class="card-text small text-muted">
                                                                Gestiona las unidades registradas.
                                                            </p>
                                                            <a href="Unidad.jsp" class="btn btn-sm btn-outline-success">
                                                                Ir al módulo
                                                            </a>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                        <% } %>

                                        <!-- Espacio libre para futuras secciones -->

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
