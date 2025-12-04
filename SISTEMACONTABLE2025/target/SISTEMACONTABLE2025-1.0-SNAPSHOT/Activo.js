$(function () {
    // Obtener el contextPath
    const contextPath = window.location.pathname.split('/')[1];
    const baseUrl = '/' + contextPath;

    console.log("Context Path:", contextPath);
    console.log("Base URL:", baseUrl);

    // Activar Parsley en todos los formularios
    $('#form_activo_nuevo').parsley();
    $('#form_activo_usado').parsley();
    $('#form_dar_baja').parsley();

    // Cargar combos y tablas al iniciar
    cargarCombos();
    cargarTablas();

    // Botón: abrir modal para registrar activo NUEVO
    $(document).on("click", "#btn_nuevo_activo", function (e) {
        e.preventDefault();
        console.log("Click en btn_nuevo_activo");

        $("#form_activo_nuevo").trigger("reset");
        $("#opcion_nuevo").val("si_registro_nuevo");
        $("#tituloModalNuevo").text("Registrar Activo Nuevo");

        // Establecer fecha actual
        establecerFechaActual('nuevo');
        
        // Cargar combos específicos para nuevo
        cargarCombosParaModal('nuevo');

        $("#modalActivoNuevo").modal("show");
    });

    // Botón: abrir modal para registrar activo USADO
    $(document).on("click", "#btn_nuevo_activo_usado", function (e) {
        e.preventDefault();
        console.log("Click en btn_nuevo_activo_usado");

        $("#form_activo_usado").trigger("reset");
        $("#opcion_usado").val("si_registro_usado");
        $("#tituloModalUsado").text("Registrar Activo Usado");

        // Establecer fecha actual
        establecerFechaActual('usado');
        
        // Cargar combos específicos para usado
        cargarCombosParaModal('usado');

        $("#modalActivoUsado").modal("show");
    });

    // Botón: Dar de Baja en las tablas
    $(document).on("click", ".btn_dar_baja", function (e) {
        e.preventDefault();
        const idActivo = $(this).data("id");
        console.log("Dar de baja activo ID:", idActivo);
        
        cargarDatosActivoParaBaja(idActivo);
    });

    // Generar código automáticamente para NUEVO
    $(document).on("change", "#cmb_unidad_nuevo, #cmb_tipo_nuevo, #txt_correlativo_nuevo", function () {
        generarCodigo('nuevo');
    });

    // Generar código automáticamente para USADO
    $(document).on("change", "#cmb_unidad_usado, #cmb_tipo_usado_combo, #txt_correlativo_usado", function () {
        generarCodigo('usado');
    });

    // Si el usuario modifica manualmente el correlativo
    $(document).on("blur", "#txt_correlativo_nuevo, #txt_correlativo_usado", function () {
        const valor = $(this).val();
        if (valor) {
            const tipo = $(this).attr('id') === 'txt_correlativo_nuevo' ? 'nuevo' : 'usado';
            generarCodigo(tipo);
        }
    });

    // Submit del formulario ACTIVO NUEVO
    $(document).on("submit", "#form_activo_nuevo", function (e) {
        e.preventDefault();
        procesarFormulario('nuevo');
    });

    // Submit del formulario ACTIVO USADO
    $(document).on("submit", "#form_activo_usado", function (e) {
        e.preventDefault();
        procesarFormulario('usado');
    });

    // Submit del formulario DAR DE BAJA
    $(document).on("submit", "#form_dar_baja", function (e) {
        e.preventDefault();
        procesarDarDeBaja();
    });

});

// ===================== CARGAR DATOS PARA DAR DE BAJA ===================== //
function cargarDatosActivoParaBaja(idActivo) {
    console.log("Cargando datos del activo para baja ID:", idActivo);
    mostrar_cargando("Cargando información", "Espere un momento...");

    const contextPath = window.location.pathname.split('/')[1];
    const baseUrl = '/' + contextPath;

    $.ajax({
        dataType: "json",
        method: "POST",
        url: baseUrl + "/RegActivo",
        data: {
            opcion: "obtenerActivoPorId",
            idActivo: idActivo
        }
    }).done(function (json) {
        Swal.close();
        console.log("Datos activo para baja:", json);

        if (json && json[0] && json[0].resultado === "exito") {
            const activo = json[0];
            
            // Llenar los datos en el modal
            $("#idActivoBaja").val(activo.id);
            $("#codigoActivoBaja").text(activo.codigo);
            $("#nombreActivoBaja").text(activo.nombre);
            $("#nuevoEstado").val("false");
            
            // Limpiar y mostrar el modal
            $("#descripcionEstado").val("");
            $("#modalDarBaja").modal("show");
            
        } else {
            Swal.fire({
                title: "Error",
                text: json[0].mensaje || "No se pudo cargar la información del activo",
                icon: "error",
                confirmButtonText: "Aceptar"
            });
        }

    }).fail(function (jqXHR, textStatus, errorThrown) {
        Swal.close();
        console.error("Error cargando datos activo:", textStatus, errorThrown);
        Swal.fire({
            title: "Error",
            text: "Error al cargar la información del activo",
            icon: "error",
            confirmButtonText: "Aceptar"
        });
    });
}

// ===================== PROCESAR DAR DE BAJA ===================== //
function procesarDarDeBaja() {
    // Validar el formulario con Parsley
    if (!$("#form_dar_baja").parsley().validate()) {
        console.log("Validación falló en dar de baja");
        return;
    }

    mostrar_cargando("Procesando baja", "Por favor espere...");

    const contextPath = window.location.pathname.split('/')[1];
    const baseUrl = '/' + contextPath;

    var datos = $("#form_dar_baja").serialize();
    console.log("Datos dar de baja:", datos);

    $.ajax({
        dataType: "json",
        method: "POST",
        url: baseUrl + "/RegActivo",
        data: {
            opcion: "actualizarEstadoActivo",
            idActivo: $("#idActivoBaja").val(),
            descripcionEstado: $("#descripcionEstado").val(),
            nuevoEstado: $("#nuevoEstado").val()
        }
    }).done(function (json) {
        Swal.close();
        console.log("Respuesta dar de baja:", json);

        if (json[0].resultado === "exito") {
            Swal.fire({
                title: "Éxito",
                text: json[0].mensaje,
                icon: "success",
                confirmButtonText: "Aceptar"
            }).then((result) => {
                // Cuando el usuario hace clic en "Aceptar"
                if (result.isConfirmed) {
                    $("#modalDarBaja").modal("hide");
                    cargarTablas(); // Recargar las tablas para que desaparezca el activo dado de baja
                    
                    // Redirigir a ActivoBaja.jsp después de 1 segundo
                    setTimeout(() => {
                        window.location.href = "ActivoBaja.jsp";
                    }, 1000);
                }
            });
        } else {
            Swal.fire({
                title: "Error",
                text: json[0].mensaje || "No se pudo realizar la baja",
                icon: "error",
                confirmButtonText: "Aceptar"
            });
        }

    }).fail(function (jqXHR, textStatus, errorThrown) {
        Swal.close();
        console.error("Error en dar de baja:", textStatus, errorThrown);
        Swal.fire({
            title: "Error",
            text: "Error en la solicitud: " + textStatus,
            icon: "error",
            confirmButtonText: "Aceptar"
        });
    });
}

// ===================== ESTABLECER FECHA ACTUAL ===================== //
function establecerFechaActual(tipo) {
    const prefix = (tipo === 'nuevo') ? '_nuevo' : '_usado';
    
    // Obtener fecha actual
    const hoy = new Date();
    
    // Formatear a YYYY-MM-DD (formato requerido por input type="date")
    const año = hoy.getFullYear();
    const mes = String(hoy.getMonth() + 1).padStart(2, '0');
    const dia = String(hoy.getDate()).padStart(2, '0');
    const fechaFormateada = `${año}-${mes}-${dia}`;
    
    // Establecer la fecha en el campo correspondiente
    $(`#txt_fecha_compra${prefix}`).val(fechaFormateada);
    
    console.log("Fecha actual establecida para", tipo + ":", fechaFormateada);
}

// ===================== CARGAR COMBOS ===================== //
function cargarCombos() {
    console.log("Cargando combos...");
    mostrar_cargando("Cargando datos", "Espere un momento...");

    const contextPath = window.location.pathname.split('/')[1];
    const baseUrl = '/' + contextPath;

    $.ajax({
        dataType: "json",
        method: "POST",
        url: baseUrl + "/RegActivo",
        data: {opcion: "cargarCombos"}
    }).done(function (json) {
        Swal.close();
        console.log("Combos cargados:", json);

        if (json && json[0] && json[0].resultado === "exito") {
            // Guardar datos globalmente para usarlos después
            window.combosData = json[0];
            console.log("Combos guardados globalmente");

        } else {
            Swal.fire("Error", "No se pudieron cargar los combos", "error");
        }

    }).fail(function (jqXHR, textStatus, errorThrown) {
        Swal.close();
        console.error("Error cargando combos:", textStatus, errorThrown);
        Swal.fire("Error", "Falló la consulta: " + textStatus, "error");
    });
}

// ===================== CARGAR COMBOS PARA MODAL ===================== //

function cargarCombosParaModal(tipo) {
    if (!window.combosData) {
        console.log("No hay datos de combos disponibles");
        return;
    }

    const prefix = (tipo === 'nuevo') ? '_nuevo' : '_usado';
    const tipoComboPrefix = (tipo === 'nuevo') ? '_nuevo' : '_usado_combo';

    // Llenar combo de unidades
    $(`#cmb_unidad${prefix}`).empty().append('<option value="">Seleccionar unidad</option>');
    window.combosData.unidades.forEach(function(unidad) {
        // Guardar el ID de institución como atributo data
        $(`#cmb_unidad${prefix}`).append(
            '<option value="' + unidad.idUnidad + '" ' +
            'data-id-institucion="' + unidad.idInstitucion + '">' + 
            unidad.nombreInstitucion + ' - ' + unidad.nombreUnidad + '</option>'
        );
    });

    // Llenar combo de tipos categoría
    $(`#cmb_tipo${tipoComboPrefix}`).empty().append('<option value="">Seleccionar tipo</option>');
    window.combosData.tiposCategoria.forEach(function(tipoCat) {
        $(`#cmb_tipo${tipoComboPrefix}`).append('<option value="' + tipoCat.idTipo + '">' + tipoCat.nombre + '</option>');
    });

    // Solo para usado, llenar combo de tipos usado
    if (tipo === 'usado') {
        $("#cmb_tipo_usado_porcentaje").empty().append('<option value="">Seleccionar tipo usado</option>');
        window.combosData.tiposUsado.forEach(function(tipoUsado) {
            $("#cmb_tipo_usado_porcentaje").append('<option value="' + tipoUsado.idTipoUsado + '">' + 
                tipoUsado.anyos + ' años (' + tipoUsado.porcentaje + '%)</option>');
        });
    }

    // Cargar el siguiente correlativo automáticamente
    cargarSiguienteCorrelativo(tipo);
    
    console.log("Combos cargados para modal:", tipo);
}

// ===================== CARGAR SIGUIENTE CORRELATIVO ===================== //
function cargarSiguienteCorrelativo(tipo) {
    const prefix = (tipo === 'nuevo') ? '_nuevo' : '_usado';
    
    const contextPath = window.location.pathname.split('/')[1];
    const baseUrl = '/' + contextPath;

    console.log("Cargando siguiente correlativo para:", tipo);

    $.ajax({
        dataType: "json",
        method: "POST",
        url: baseUrl + "/RegActivo",
        data: {opcion: "obtenerSiguienteCorrelativo"}
    }).done(function (json) {
        console.log("Respuesta correlativo:", json);

        if (json && json[0] && json[0].resultado === "exito") {
            const siguienteCorrelativo = json[0].siguienteCorrelativo;
            const correlativoFormateado = json[0].correlativoFormateado;
            
            // Establecer el valor en el campo correspondiente
            $(`#txt_correlativo${prefix}`).val(siguienteCorrelativo);
            
            console.log("Siguiente correlativo cargado:", siguienteCorrelativo, "Formateado:", correlativoFormateado);
            
            // Generar código automáticamente si ya hay unidad y tipo seleccionados
            setTimeout(() => {
                generarCodigo(tipo);
            }, 100);
            
        } else {
            console.error("Error al obtener correlativo:", json);
            // Establecer un valor por defecto en caso de error
            $(`#txt_correlativo${prefix}`).val(1);
        }

    }).fail(function (jqXHR, textStatus, errorThrown) {
        console.error("Error cargando correlativo:", textStatus, errorThrown);
        // Establecer un valor por defecto en caso de error
        $(`#txt_correlativo${prefix}`).val(1);
    });
}

// ===================== PROCESAR FORMULARIO ===================== //
function procesarFormulario(tipo) {
    const formId = (tipo === 'nuevo') ? '#form_activo_nuevo' : '#form_activo_usado';
    const modalId = (tipo === 'nuevo') ? '#modalActivoNuevo' : '#modalActivoUsado';
    const tipoTexto = (tipo === 'nuevo') ? 'nuevo' : 'usado';

    // Validar el formulario con Parsley
    if (!$(formId).parsley().validate()) {
        console.log("Validación falló");
        return;
    }

    mostrar_cargando("Procesando solicitud", "Por favor espere...");

    const contextPath = window.location.pathname.split('/')[1];
    const baseUrl = '/' + contextPath;

    var datos = $(formId).serialize();
    console.log("Datos enviados (" + tipoTexto + "):", datos);

    $.ajax({
        dataType: "json",
        method: "POST",
        url: baseUrl + "/RegActivo",
        data: datos
    }).done(function (json) {
        Swal.close();
        console.log("Respuesta servidor (" + tipoTexto + "):", json);

        if (json[0].resultado === "exito") {
            Swal.fire({
                title: "Éxito",
                text: json[0].mensaje,
                icon: "success",
                confirmButtonText: "Aceptar"
            });
            $(modalId).modal("hide");
            cargarTablas();
        } else {
            Swal.fire({
                title: "Error",
                text: json[0].mensaje || "No se pudo realizar la acción",
                icon: "error",
                confirmButtonText: "Aceptar"
            });
            console.log("Detalle error:", json);
        }

    }).fail(function (jqXHR, textStatus, errorThrown) {
        Swal.close();
        console.error("Error AJAX (" + tipoTexto + "):", textStatus, errorThrown);
        Swal.fire({
            title: "Error",
            text: "Error en la solicitud AJAX: " + textStatus,
            icon: "error",
            confirmButtonText: "Aceptar"
        });
    });
}

// ===================== CARGAR TABLAS ===================== //
function cargarTablas() {
    const contextPath = window.location.pathname.split('/')[1];
    const baseUrl = '/' + contextPath;

    console.log("Cargando tablas...");

    // Cargar tabla de activos nuevos
    $.ajax({
        dataType: "json",
        method: "POST",
        url: baseUrl + "/RegActivo",
        data: {opcion: "cargarTablaNuevos"}
    }).done(function (json) {
        console.log("Tabla nuevos:", json);
        if (json && json[0] && json[0].resultado === "exito") {
            $("#tablaActivosNuevos").empty().html(json[0].tabla);
            inicializarDataTable("tablaActivosNuevos_id");
            console.log("Tabla de activos nuevos cargada exitosamente");
        } else {
            console.error("Error en respuesta tabla nuevos:", json);
            $("#tablaActivosNuevos").html('<div class="alert alert-warning">No se pudieron cargar los activos nuevos</div>');
        }
    }).fail(function(jqXHR, textStatus) {
        console.error("Error cargando tabla nuevos:", textStatus);
        $("#tablaActivosNuevos").html('<div class="alert alert-danger">Error al cargar activos nuevos</div>');
    });

    // Cargar tabla de activos usados
    $.ajax({
        dataType: "json",
        method: "POST",
        url: baseUrl + "/RegActivo",
        data: {opcion: "cargarTablaUsados"}
    }).done(function (json) {
        console.log("Tabla usados:", json);
        if (json && json[0] && json[0].resultado === "exito") {
            $("#tablaActivosUsados").empty().html(json[0].tabla);
            inicializarDataTable("tablaActivosUsados_id");
            console.log("Tabla de activos usados cargada exitosamente");
        } else {
            console.error("Error en respuesta tabla usados:", json);
            $("#tablaActivosUsados").html('<div class="alert alert-warning">No se pudieron cargar los activos usados</div>');
        }
    }).fail(function(jqXHR, textStatus) {
        console.error("Error cargando tabla usados:", textStatus);
        $("#tablaActivosUsados").html('<div class="alert alert-danger">Error al cargar activos usados</div>');
    });
}

// ===================== INICIALIZAR DATATABLE ===================== //
function inicializarDataTable(idTabla) {
    if ($.fn.DataTable.isDataTable('#' + idTabla)) {
        $('#' + idTabla).DataTable().destroy();
    }
    
    const dt = $('#' + idTabla).DataTable({
        language: {
            url: "https://cdn.datatables.net/plug-ins/1.12.1/i18n/es-ES.json"
        },
        responsive: true,
        order: [[0, 'asc']],
        pageLength: 10,
        autoWidth: false,
        dom: '<"top"lf>rt<"bottom"ip><"clear">'
    });
    
    console.log("DataTable inicializado:", idTabla);
}

// ===================== GENERAR CÓDIGO ===================== //
// ===================== GENERAR CÓDIGO ===================== //
function generarCodigo(tipo) {
    const prefix = (tipo === 'nuevo') ? '_nuevo' : '_usado';
    const tipoComboPrefix = (tipo === 'nuevo') ? '_nuevo' : '_usado_combo';

    const unidadSelect = $(`#cmb_unidad${prefix}`);
    const unidad = unidadSelect.val();
    const tipoCat = $(`#cmb_tipo${tipoComboPrefix}`).val();
    const correlativo = $(`#txt_correlativo${prefix}`).val();

    console.log("Generando código para", tipo, {
        unidad: unidad,
        tipoCat: tipoCat,
        correlativo: correlativo
    });

    if (unidad && tipoCat && correlativo) {
        // Obtener el ID de institución del option seleccionado
        const idInstitucion = unidadSelect.find('option:selected').data('id-institucion');
        
        console.log("ID Institución obtenido:", idInstitucion);
        
        if (!idInstitucion) {
            console.error("No se encontró ID de institución para la unidad seleccionada");
            return;
        }

        // Formato: 2322-5676-8871-0001 (ejemplo de la imagen)
        // Usar ID de institución formateado a 4 dígitos
        const codigoInstitucion = idInstitucion.toString().padStart(4, '0');
        const codigoUnidad = unidad.toString().padStart(4, '0');
        const codigoTipo = tipoCat.toString().padStart(4, '0');
        const codigoCorrelativo = correlativo.toString().padStart(4, '0');
        
        const codigoCompleto = codigoInstitucion + "-" + codigoUnidad + "-" + 
                              codigoTipo + "-" + codigoCorrelativo;
        
        $(`#txt_codigo${prefix}`).val(codigoCompleto);
        
        console.log("Código generado para", tipo + ":", codigoCompleto);
        console.log("Componentes:", {
            codigoInstitucion: codigoInstitucion,
            codigoUnidad: codigoUnidad,
            codigoTipo: codigoTipo,
            codigoCorrelativo: codigoCorrelativo
        });
    } else {
        console.log("Faltan datos para generar código para", tipo + ":", {
            unidad: unidad,
            tipoCat: tipoCat,
            correlativo: correlativo
        });
    }
}
// ===================== SWEET ALERT 2 LOADING ===================== //
function mostrar_cargando(titulo, mensaje = "") {
    Swal.fire({
        title: titulo,
        html: mensaje,
        allowOutsideClick: false,
        allowEscapeKey: false,
        didOpen: () => {
            Swal.showLoading();
        }
    });
}

// ===================== FUNCIÓN PARA RECARGAR TABLAS ===================== //
function recargarTablas() {
    console.log("Recargando tablas...");
    cargarTablas();
}

// ===================== MANEJAR CAMBIOS DE PESTAÑA ===================== //
$(document).on('shown.bs.tab', 'button[data-bs-toggle="tab"]', function (e) {
    // Cuando se cambia de pestaña, asegurarse de que las tablas se redimensionen
    setTimeout(function() {
        $.fn.dataTable.tables({ visible: true, api: true }).columns.adjust();
    }, 100);
});

// ===================== INICIALIZACIÓN AL CARGAR LA PÁGINA ===================== //
$(document).ready(function() {
    console.log("Página de Activos cargada correctamente");
    
    // Ajustar DataTables cuando la página esté completamente cargada
    setTimeout(function() {
        $.fn.dataTable.tables({ visible: true, api: true }).columns.adjust();
    }, 500);
});