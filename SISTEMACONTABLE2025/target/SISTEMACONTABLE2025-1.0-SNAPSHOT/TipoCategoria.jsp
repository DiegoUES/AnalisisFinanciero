<%-- 
    Document   : TipoCategoria
    Created on : 25 nov 2025, 11:58:51 p. m.
    Author     : Marlo
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">

    <head>
        <meta charset="UTF-8">
        <title>Catálogo de Tipos de Activo</title>

        <!-- jQuery -->
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.0/jquery.min.js"></script>

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
                    
                    <li class="nav-item">
                        <a href="TipoCategoria.jsp" class="nav-link active">
                            <i class="bi bi-tags me-2"></i> Tipos de Activo
                        </a>
                    </li>
                    
                    <li class="nav-item">
                        <a href="Institucion.jsp" class="nav-link text-white">
                            <i class="bi bi-building me-2"></i> Instituciones
                        </a>
                    </li>
                    
                    <li class="nav-item">
                        <a href="Unidad.jsp" class="nav-link text-white">
                            <i class="bi bi-building me-2"></i> Unidades
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
                                                <span class="text-muted fw-light">CRUD /</span> Tipos de Activo
                                            </h4>

                                            <div class="mb-3">
                                                <button type="button"
                                                        id="btn_nuevo_tipo"
                                                        class="btn btn-outline-primary">
                                                    Nuevo Tipo de Activo
                                                </button>
                                            </div>

                                            <!-- TABLA -->
                                            <div id="tablaTiposActivo"></div>

                                        </div>
                                    </div>
                                </div>
                            </div>

                            <!--MODAL TIPO DE ACTIVO-->
                            <div class="modal fade" id="modalTipoActivo" tabindex="-1" aria-hidden="true">
                                <div class="modal-dialog modal-lg">
                                    <div class="modal-content">

                                        <div class="modal-header" style="background:#7FB3D5;">
                                            <h5 class="modal-title" id="tituloModal">Registrar Tipo de Activo</h5>
                                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                                        </div>

                                        <div class="modal-body">

                                            <form id="form_tipo_activo">

                                                <input type="hidden" id="opcion" name="opcion" value="si_registro">
                                                <input type="hidden" id="txt_id" name="txt_id">

                                                <div class="row">
                                                    <div class="col-md-4">
                                                        <label for="txt_codigo" class="form-label">Código</label>
                                                        <input type="text" class="form-control"
                                                               id="txt_codigo" name="txt_codigo" 
                                                               pattern="^\d{4}$"
                                                               maxlength="4"
                                                               required>
                                                    </div>
                                                    
                                                    <div class="col-md-8">
                                                        <label for="txt_nombre" class="form-label">Nombre</label>
                                                        <input type="text" class="form-control"
                                                               id="txt_nombre" name="txt_nombre" 
                                                               maxlength="100"
                                                               required>
                                                    </div>
                                                </div>

                                                <div class="row mt-3">
                                                    <div class="col-md-6">
                                                        <label for="txt_porcentaje" class="form-label">Porcentaje</label>
                                                        <input type="number" class="form-control"
                                                               id="txt_porcentaje" name="txt_porcentaje"
                                                               step="0.01"
                                                               min="0"
                                                               max="100"
                                                               required>
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

        <!-- JS DE LA VISTA-->
        <script src="TipoCategoria.js"></script>

    </body>
</html>