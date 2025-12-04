<%@page contentType="text/html" pageEncoding="UTF-8"%>
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

        <!<!-- AQUI COMIENZA EL MENÚ LATERAL -->
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
                        <a href="Institucion.jsp" class="nav-link text-white">
                            <i class="bi bi-building me-2"></i> Instituciones
                        </a>
                    </li>

                    <li class="nav-item">
                        <a href="Activo.jsp" class="nav-link active">
                            <i class="bi bi-box-seam me-2"></i> Activo Fijo
                        </a>
                    </li>
                    <li class="nav-item">
                        <a href="ActivoBaja.jsp" class="nav-link">
                            <i class="bi bi-box-arrow-down me-2"></i> Activos de Baja
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
                                                <span class="text-muted fw-light">BIENVENIDO A: </span> Activo Fijo
                                            </h4>

                                            <div class="mb-3 btn-group-activos">
                                                <button type="button"
                                                        id="btn_nuevo_activo"
                                                        class="btn btn-outline-primary">
                                                    <i class="bi bi-plus-circle"></i> Nuevo Activo
                                                </button>
                                                <button type="button"
                                                        id="btn_nuevo_activo_usado"
                                                        class="btn btn-outline-success">
                                                    <i class="bi bi-arrow-repeat"></i> Nuevo Activo Usado
                                                </button>
                                            </div>

                                            <!-- PESTAÑAS -->
                                            <ul class="nav nav-tabs" id="myTab" role="tablist">
                                                <li class="nav-item" role="presentation">
                                                    <button class="nav-link active" id="nuevos-tab" data-bs-toggle="tab" 
                                                            data-bs-target="#nuevos" type="button" role="tab">
                                                        <i class="bi bi-box-seam"></i> Activos Nuevos
                                                    </button>
                                                </li>
                                                <li class="nav-item" role="presentation">
                                                    <button class="nav-link" id="usados-tab" data-bs-toggle="tab" 
                                                            data-bs-target="#usados" type="button" role="tab">
                                                        <i class="bi bi-arrow-repeat"></i> Activos Usados
                                                    </button>
                                                </li>
                                            </ul>

                                            <div class="tab-content" id="myTabContent">
                                                <!-- TABLA ACTIVOS NUEVOS -->
                                                <div class="tab-pane fade show active" id="nuevos" role="tabpanel">
                                                    <div id="tablaActivosNuevos"></div>
                                                </div>

                                                <!-- TABLA ACTIVOS USADOS -->
                                                <div class="tab-pane fade" id="usados" role="tabpanel">
                                                    <div id="tablaActivosUsados"></div>
                                                </div>
                                            </div>

                                        </div>
                                    </div>
                                </div>
                            </div>

                            <!--MODAL ACTIVO NUEVO-->
                            <div class="modal fade" id="modalActivoNuevo" tabindex="-1" aria-hidden="true">
                                <div class="modal-dialog modal-lg">
                                    <div class="modal-content">

                                        <div class="modal-header" style="background:#7FB3D5;">
                                            <h5 class="modal-title" id="tituloModalNuevo">Registrar Activo Nuevo</h5>
                                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                                        </div>

                                        <div class="modal-body">

                                            <form id="form_activo_nuevo">

                                                <input type="hidden" id="opcion_nuevo" name="opcion" value="si_registro_nuevo">
                                                <input type="hidden" id="txt_id_nuevo" name="txt_id">

                                                <div class="row mb-3">
                                                    <div class="col-md-6">
                                                        <label for="txt_nombre_nuevo" class="form-label">Nombre del Activo</label>
                                                        <input type="text" class="form-control" 
                                                               id="txt_nombre_nuevo" name="txt_nombre" required>
                                                    </div>
                                                    <div class="col-md-6">
                                                        <label for="cmb_unidad_nuevo" class="form-label">Unidad</label>
                                                        <select class="form-select" id="cmb_unidad_nuevo" name="cmb_unidad" required>
                                                            <option value="">Seleccionar unidad</option>
                                                        </select>
                                                    </div>
                                                </div>

                                                <div class="row mb-3">
                                                    <div class="col-md-6">
                                                        <label for="cmb_tipo_nuevo" class="form-label">Tipo Categoría</label>
                                                        <select class="form-select" id="cmb_tipo_nuevo" name="cmb_tipo" required>
                                                            <option value="">Seleccionar tipo</option>
                                                        </select>
                                                    </div>
                                                    <div class="col-md-6">
                                                        <label for="txt_codigo_nuevo" class="form-label">Código</label>
                                                        <input type="text" class="form-control" 
                                                               id="txt_codigo_nuevo" name="txt_codigo" readonly>
                                                    </div>
                                                </div>

                                                <div class="row mb-3">
                                                    <div class="col-md-6">
                                                        <label for="txt_correlativo_nuevo" class="form-label">Correlativo</label>
                                                        <input type="number" class="form-control" 
                                                               id="txt_correlativo_nuevo" name="txt_correlativo" required>
                                                    </div>
                                                    <div class="col-md-6">
                                                        <label for="txt_precio_adquisicion_nuevo" class="form-label">Precio Adquisición</label>
                                                        <input type="number" step="0.01" class="form-control" 
                                                               id="txt_precio_adquisicion_nuevo" name="txt_precio_adquisicion" required>
                                                    </div>
                                                </div>

                                                <div class="row mb-3">
                                                    <div class="col-12">
                                                        <label for="txt_caracteristicas_nuevo" class="form-label">Características</label>
                                                        <textarea class="form-control" id="txt_caracteristicas_nuevo" 
                                                                  name="txt_caracteristicas" rows="3"></textarea>
                                                    </div>
                                                </div>

                                                <div class="row mb-3">
                                                    <div class="col-md-6">
                                                        <label for="txt_fecha_compra_nuevo" class="form-label">Fecha de Compra</label>
                                                        <input type="date" class="form-control" 
                                                               id="txt_fecha_compra_nuevo" name="txt_fecha_compra" required>
                                                    </div>
                                                    <div class="col-md-6">
                                                        <label for="txt_vida_util_nuevo" class="form-label">Vida Útil (años)</label>
                                                        <input type="number" class="form-control" 
                                                               id="txt_vida_util_nuevo" name="txt_vida_util" required>
                                                    </div>
                                                </div>

                                                <div class="modal-footer mt-3">
                                                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                                                        Cerrar
                                                    </button>
                                                    <button type="submit" class="btn btn-primary">
                                                        Guardar Activo Nuevo
                                                    </button>
                                                </div>

                                            </form>

                                        </div>

                                    </div>
                                </div>
                            </div>
                            <!--FIN MODAL ACTIVO NUEVO-->

                            <!--MODAL ACTIVO USADO-->
                            <div class="modal fade" id="modalActivoUsado" tabindex="-1" aria-hidden="true">
                                <div class="modal-dialog modal-lg">
                                    <div class="modal-content">

                                        <div class="modal-header" style="background:#82E0AA;">
                                            <h5 class="modal-title" id="tituloModalUsado">Registrar Activo Usado</h5>
                                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                                        </div>

                                        <div class="modal-body">

                                            <form id="form_activo_usado">

                                                <input type="hidden" id="opcion_usado" name="opcion" value="si_registro_usado">
                                                <input type="hidden" id="txt_id_usado" name="txt_id">

                                                <div class="row mb-3">
                                                    <div class="col-md-6">
                                                        <label for="txt_nombre_usado" class="form-label">Nombre del Activo</label>
                                                        <input type="text" class="form-control" 
                                                               id="txt_nombre_usado" name="txt_nombre" required>
                                                    </div>
                                                    <div class="col-md-6">
                                                        <label for="cmb_unidad_usado" class="form-label">Unidad</label>
                                                        <select class="form-select" id="cmb_unidad_usado" name="cmb_unidad" required>
                                                            <option value="">Seleccionar unidad</option>
                                                        </select>
                                                    </div>
                                                </div>

                                                <div class="row mb-3">
                                                    <div class="col-md-6">
                                                        <label for="cmb_tipo_usado_combo" class="form-label">Tipo Categoría</label>
                                                        <select class="form-select" id="cmb_tipo_usado_combo" name="cmb_tipo" required>
                                                            <option value="">Seleccionar tipo</option>
                                                        </select>
                                                    </div>
                                                    <div class="col-md-6">
                                                        <label for="cmb_tipo_usado_porcentaje" class="form-label">Tipo Usado</label>
                                                        <select class="form-select" id="cmb_tipo_usado_porcentaje" name="cmb_tipo_usado" required>
                                                            <option value="">Seleccionar tipo usado</option>
                                                        </select>
                                                    </div>
                                                </div>

                                                <div class="row mb-3">
                                                    <div class="col-md-6">
                                                        <label for="txt_codigo_usado" class="form-label">Código</label>
                                                        <input type="text" class="form-control" 
                                                               id="txt_codigo_usado" name="txt_codigo" readonly>
                                                    </div>
                                                    <div class="col-md-6">
                                                        <label for="txt_correlativo_usado" class="form-label">Correlativo</label>
                                                        <input type="number" class="form-control" 
                                                               id="txt_correlativo_usado" name="txt_correlativo" required>
                                                    </div>
                                                </div>

                                                <div class="row mb-3">
                                                    <div class="col-md-6">
                                                        <label for="txt_precio_adquisicion_usado" class="form-label">Precio Adquisición</label>
                                                        <input type="number" step="0.01" class="form-control" 
                                                               id="txt_precio_adquisicion_usado" name="txt_precio_adquisicion" required>
                                                    </div>
                                                    <div class="col-md-6">
                                                        <label for="txt_precio_usado" class="form-label">Precio Usado</label>
                                                        <input type="number" step="0.01" class="form-control" 
                                                               id="txt_precio_usado" name="txt_precio_usado" required>
                                                    </div>
                                                </div>

                                                <div class="row mb-3">
                                                    <div class="col-12">
                                                        <label for="txt_caracteristicas_usado" class="form-label">Características</label>
                                                        <textarea class="form-control" id="txt_caracteristicas_usado" 
                                                                  name="txt_caracteristicas" rows="3"></textarea>
                                                    </div>
                                                </div>

                                                <div class="row mb-3">
                                                    <div class="col-md-6">
                                                        <label for="txt_fecha_compra_usado" class="form-label">Fecha de Compra</label>
                                                        <input type="date" class="form-control" 
                                                               id="txt_fecha_compra_usado" name="txt_fecha_compra" required>
                                                    </div>
                                                    <div class="col-md-6">
                                                        <label for="txt_vida_util_usado" class="form-label">Vida Útil (años)</label>
                                                        <input type="number" class="form-control" 
                                                               id="txt_vida_util_usado" name="txt_vida_util" required>
                                                    </div>
                                                </div>

                                                <div class="modal-footer mt-3">
                                                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                                                        Cerrar
                                                    </button>
                                                    <button type="submit" class="btn btn-success">
                                                        Guardar Activo Usado
                                                    </button>
                                                </div>

                                            </form>

                                        </div>

                                    </div>
                                </div>
                            </div>
                            <!--FIN MODAL ACTIVO USADO-->
                            <!--MODAL DAR DE BAJA ACTIVO-->
                            <div class="modal fade" id="modalDarBaja" tabindex="-1" aria-hidden="true">
                                <div class="modal-dialog">
                                    <div class="modal-content">

                                        <div class="modal-header" style="background:#F1948A;">
                                            <h5 class="modal-title" id="tituloModalBaja">Dar de Baja Activo</h5>
                                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                                        </div>

                                        <div class="modal-body">

                                            <form id="form_dar_baja">

                                                <input type="hidden" id="idActivoBaja" name="idActivo">
                                                <input type="hidden" id="nuevoEstado" name="nuevoEstado" value="false">

                                                <div class="mb-3">
                                                    <label class="form-label"><strong>Activo a dar de baja:</strong></label>
                                                    <div class="card bg-light">
                                                        <div class="card-body">
                                                            <p class="mb-1"><strong>Código:</strong> <span id="codigoActivoBaja"></span></p>
                                                            <p class="mb-0"><strong>Nombre:</strong> <span id="nombreActivoBaja"></span></p>
                                                        </div>
                                                    </div>
                                                </div>

                                                <div class="mb-3">
                                                    <label for="descripcionEstado" class="form-label">Motivo de la baja</label>
                                                    <textarea class="form-control" id="descripcionEstado" 
                                                              name="descripcionEstado" rows="3" 
                                                              placeholder="Describa el motivo de la baja..." required></textarea>
                                                </div>

                                                <div class="alert alert-warning">
                                                    <i class="bi bi-exclamation-triangle"></i>
                                                    <strong>Advertencia:</strong> Esta acción desactivará el activo y ya no aparecerá en las listas principales.
                                                </div>

                                                <div class="modal-footer mt-3">
                                                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                                                        Cancelar
                                                    </button>
                                                    <button type="submit" class="btn btn-danger">
                                                        <i class="bi bi-arrow-down-circle"></i> Confirmar Baja
                                                    </button>
                                                </div>

                                            </form>

                                        </div>

                                    </div>
                                </div>
                            </div>
                            <!--FIN MODAL DAR DE BAJA-->


                        </div>
                    </div>
                </div>
            </div>
            <!--FIN CONTENIDO PRINCIPAL-->

        </div>

        <!-- JS DE LA VISTA-->
        <script src="Activo.js"></script>

    </body>
</html>