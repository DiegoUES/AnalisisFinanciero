<%-- 
    Document   : Usuarios
    Created on : 26 nov 2025
    Author     : Marlo
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%
    // ==============================
    // CONTEXTO Y ROL DE USUARIO
    // ==============================
    String ctx = request.getContextPath();

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

    // ESTA PÁGINA ES SOLO PARA ADMIN
    if (!esAdmin) {
        response.sendRedirect("index.jsp");
        return;
    }
%>

<!DOCTYPE html>
<html lang="es">

    <head>
        <meta charset="UTF-8">
        <title>Gestión de usuarios</title>

        <!-- jQuery -->
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.0/jquery.min.js"></script>

        <!-- Parsley (igual que en Institucion.jsp, por si luego lo usas) -->
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

        <!-- ESTILO PARA EL MENÚ LATERAL (IGUAL QUE INSTITUCION.JSP) -->
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
                    <!-- Inicio (todos los roles) -->
                    <li class="nav-item">
                        <a href="index.jsp" class="nav-link text-white">
                            <i class="bi bi-house-door me-2"></i> Inicio
                        </a>
                    </li>

                    <!-- Usuarios: SOLO ADMIN (PÁGINA ACTUAL) -->
                    <% if (esAdmin) { %>
                    <li class="nav-item">
                        <a href="Usuarios.jsp" class="nav-link active">
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

                            <div class="m-3 embed-responsive" style="height: 630px;">
                                <div class="container-fluid">
                                    <div class="card">
                                        <div class="card-body">

                                            <!-- TÍTULO COMO EN INSTITUCIONES -->
                                            <h4 class="fw-bold py-3 mb-2">
                                                <span class="text-muted fw-light">CRUD /</span> Usuarios
                                            </h4>

                                            <!-- BOTÓN NUEVO USUARIO -->
                                            <div class="mb-3">
                                                <button type="button"
                                                        id="btnNuevo"
                                                        class="btn btn-outline-primary">
                                                    Nuevo usuario
                                                </button>
                                            </div>

                                            <!-- TABLA -->
                                            <div id="contenedorTabla">
                                                <p class="text-muted mb-0">Cargando usuarios...</p>
                                            </div>

                                        </div>
                                    </div>
                                </div>
                            </div>

                            <!--MODAL USUARIO-->
                            <div class="modal fade" id="modalUsuario" tabindex="-1" aria-hidden="true">
                                <div class="modal-dialog">
                                    <div class="modal-content">

                                        <div class="modal-header" style="background:#7FB3D5;">
                                            <h5 class="modal-title" id="modalUsuarioLabel">Registrar usuario</h5>
                                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                                        </div>

                                        <div class="modal-body">

                                            <form id="formUsuario">

                                                <input type="hidden" id="id" name="id"/>

                                                <div class="row">
                                                    <div class="col-md-12 mb-3">
                                                        <label for="nombre" class="form-label">Nombre *</label>
                                                        <input type="text" class="form-control" id="nombre" name="nombre"
                                                               maxlength="100" required>
                                                    </div>

                                                    <div class="col-md-12 mb-3">
                                                        <label for="usuario" class="form-label">Usuario *</label>
                                                        <input type="text" class="form-control" id="usuario" name="usuario"
                                                               maxlength="100" required>
                                                    </div>

                                                    <div class="col-md-12 mb-3" id="grupoContrasena">
                                                        <label for="contrasena" class="form-label">Contraseña *</label>
                                                        <input type="password" class="form-control" id="contrasena" name="contrasena"
                                                               minlength="4" maxlength="100" required>
                                                        <div class="form-text"></div>
                                                    </div>

                                                    <div class="col-md-12 mb-3">
                                                        <label for="idrol" class="form-label">Rol *</label>
                                                        <select class="form-select" id="idrol" name="idrol" required>
                                                            <option value="">Seleccione un rol...</option>
                                                        </select>
                                                    </div>
                                                </div>

                                                <div class="modal-footer mt-3">
                                                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                                                        Cerrar
                                                    </button>
                                                    <button type="submit" class="btn btn-primary">
                                                        Guardar
                                                    </button>
                                                </div>

                                            </form>

                                        </div>

                                    </div>
                                </div>
                            </div>
                            <!--FIN MODAL-->

                        </div>
                    </div>
                </div>
            </div>
            <!--FIN CONTENIDO PRINCIPAL-->

        </div>

        <!-- JS ESPECÍFICO DE ESTE MÓDULO -->
        <script>
            // URL absoluta al servlet, para que Usuarios.js la use
            const USUARIO_ENDPOINT = '<%=ctx%>/UsuarioServlet';
        </script>
        <script src="<%=ctx%>/Usuarios.js"></script>

    </body>
</html>
