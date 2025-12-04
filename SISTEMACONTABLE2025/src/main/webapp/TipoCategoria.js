// TipoActivo.js
$(function () {
    
    // Cargar tabla al iniciar
    cargarTablaTiposActivo();

    // Botón: abrir modal para registrar
    $(document).on("click", "#btn_nuevo_tipo", function (e) {
        e.preventDefault();
        console.log("Click en btn_nuevo_tipo");
        $("#txt_codigo").closest('.col-md-4').show(); // Oculta toda la columna del código
            $("#txt_porcentaje").closest('.row').show(); // Oculta la fila del porcentaje
            
            // También ocultar las etiquetas si están en el mismo div
            $("#txt_codigo").prev('label').show();
            $("#txt_porcentaje").prev('label').show();
        $("#form_tipo_activo").trigger("reset");
        $("#opcion").val("si_registro");
        $("#tituloModal").text("Registrar Tipo de Activo");
        
        // Limpiar campo id oculto
        $("#txt_id").val("");
        
        $("#modalTipoActivo").modal("show");
    });

    // Validación del código (solo números y exactamente 4)
    $(document).on("input", "#txt_codigo", function() {
        // Solo números
        $(this).val($(this).val().replace(/[^\d]/g, ''));
        
        // Máximo 4 dígitos
        if ($(this).val().length > 4) {
            $(this).val($(this).val().substring(0, 4));
        }
        
        // Validar que no sea 0000
        if ($(this).val() === "0000") {
            $(this).val("");
            Swal.fire("Advertencia", "El código 0000 no es válido", "warning");
        }
    });

    // Validación del porcentaje (0-100)
    $(document).on("change blur", "#txt_porcentaje", function() {
        const value = parseFloat($(this).val());
        if (value < 0) {
            $(this).val(0);
        } else if (value > 100) {
            $(this).val(100);
        }
        
        // Formatear a 2 decimales
        if (!isNaN(value)) {
            $(this).val(value.toFixed(2));
        }
    });

    // Validación del nombre (máximo 100 caracteres)
    $(document).on("input", "#txt_nombre", function() {
        if ($(this).val().length > 100) {
            $(this).val($(this).val().substring(0, 100));
        }
    });

    // Submit del formulario (registrar / actualizar)
    $(document).on("submit", "#form_tipo_activo", function (e) {
        e.preventDefault();

        // Validaciones adicionales antes de enviar
        const codigo = $('#txt_codigo').val();
        const nombre = $('#txt_nombre').val().trim();
        const porcentaje = parseFloat($('#txt_porcentaje').val());

        // Validar código
        if (codigo.length !== 4) {
            Swal.fire("Error", "El código debe tener exactamente 4 números", "error");
            $('#txt_codigo').focus();
            return;
        }

        // Validar que no sea 0000
        if (codigo === "0000") {
            Swal.fire("Error", "El código 0000 no es válido", "error");
            $('#txt_codigo').focus();
            return;
        }

        // Validar nombre
        if (nombre === "") {
            Swal.fire("Error", "El nombre es requerido", "error");
            $('#txt_nombre').focus();
            return;
        }

        // Validar porcentaje
        if (isNaN(porcentaje) || porcentaje < 0 || porcentaje > 100) {
            Swal.fire("Error", "El porcentaje debe estar entre 0 y 100", "error");
            $('#txt_porcentaje').focus();
            return;
        }

        // Asegurar que el porcentaje tenga 2 decimales
        $('#txt_porcentaje').val(porcentaje.toFixed(2));

        mostrar_cargando("Procesando solicitud", "Por favor espere...");

        var datos = $("#form_tipo_activo").serialize();
        console.log("Datos enviados =", datos);

        $.ajax({
            dataType: "json",
            method: "POST",
            url: "RegCatalogoTipo", // Servlet corregido
            data: datos
        }).done(function (json) {
            Swal.close();

            if (json[0].resultado === "exito") {
                Swal.fire("Éxito", json[0].mensaje, "success");
                $("#modalTipoActivo").modal("hide");
                cargarTablaTiposActivo();
            } else {
                Swal.fire("Error", json[0].mensaje || "No se pudo realizar la acción", "error");
                console.log("Detalle error:", json);
            }

        }).fail(function (jqXHR, textStatus, errorThrown) {
            Swal.close();
            Swal.fire("Error", "Error en la solicitud AJAX: " + errorThrown, "error");
            console.log("Error AJAX:", textStatus, errorThrown);
        });

    });

    // Botón EDITAR en la tabla
    $(document).on("click", ".btn_editar", function (e) {
        e.preventDefault();

        const id = $(this).data("id");
        console.log("Editar tipo de activo ID =", id);

        mostrar_cargando("Cargando datos", "Espere un momento...");

        $.ajax({
            dataType: "json",
            method: "POST",
            url: "RegCatalogoTipo",
            data: {opcion: "si_tipo_especifico", id: id}
        }).done(function (json) {
            Swal.close();

            if (json[0].resultado === "exito") {
                // Cargar datos en el formulario
                $("#opcion").val("si_actualizo");
                $("#txt_id").val(json[0].ID); // ID numérico
                $("#txt_codigo").val(json[0].CODIGO); // Código formateado "0001"
                $("#txt_nombre").val(json[0].NOMBRE);
                $("#txt_porcentaje").val(parseFloat(json[0].PORCENTAJE).toFixed(2));
                
                $("#txt_codigo").closest('.col-md-4').hide(); // Oculta toda la columna del código
            $("#txt_porcentaje").closest('.row').hide(); // Oculta la fila del porcentaje
            
            // También ocultar las etiquetas si están en el mismo div
            $("#txt_codigo").prev('label').hide();
            $("#txt_porcentaje").prev('label').hide();

                $("#tituloModal").text("Editar Tipo de Activo");
                $("#modalTipoActivo").modal("show");
            } else {
                Swal.fire("Error", json[0].mensaje, "warning");
            }

        }).fail(function (jqXHR, textStatus, errorThrown) {
            Swal.close();
            Swal.fire("Error", "Error al cargar tipo de activo: " + errorThrown, "error");
        });
    });

    // Botón ELIMINAR en la tabla
    $(document).on("click", ".btn_eliminar", function (e) {
        e.preventDefault();

        const id = $(this).data("id");
        console.log("Eliminar tipo de activo ID =", id);

        // Obtener el código para mostrar en confirmación
        const codigo = $(this).closest('tr').find('td:first').text();

        Swal.fire({
            title: "¿Está seguro de eliminar?",
            html: `Se eliminará el tipo de activo con código: <strong>${codigo}</strong><br>Esta acción no se puede deshacer`,
            icon: "warning",
            showCancelButton: true,
            confirmButtonColor: "#d33",
            cancelButtonColor: "#3085d6",
            confirmButtonText: "Sí, eliminar",
            cancelButtonText: "Cancelar"
        }).then((result) => {
            if (result.isConfirmed) {
                
                mostrar_cargando("Eliminando", "Por favor espere...");

                $.ajax({
                    dataType: "json",
                    method: "POST",
                    url: "RegCatalogoTipo",
                    data: {opcion: "si_elimina", id: id}
                }).done(function (json) {
                    Swal.close();

                    if (json[0].resultado === "exito") {
                        Swal.fire("Éxito", json[0].mensaje, "success");
                        cargarTablaTiposActivo();
                    } else {
                        Swal.fire("Error", json[0].mensaje, "error");
                    }

                }).fail(function (jqXHR, textStatus, errorThrown) {
                    Swal.close();
                    Swal.fire("Error", "Error al eliminar: " + errorThrown, "error");
                });
            }
        });
    });

});

// ===================== CARGAR TABLA ===================== //
function cargarTablaTiposActivo() {

    mostrar_cargando("Cargando datos", "Espere un momento...");

    $.ajax({
        dataType: "json",
        method: "POST",
        url: "RegCatalogoTipo",
        data: {opcion: "cargarTabla"}
    }).done(function (json) {

        Swal.close();

        if (json && json[0] && json[0].resultado === "exito") {

            $("#tablaTiposActivo").empty().html(json[0].tabla);

            const dt = $("#tabla_idServlet").DataTable({
                language: {
                    url: "https://cdn.datatables.net/plug-ins/1.12.1/i18n/es-ES.json"
                },
                responsive: true,
                order: [[0, 'asc']], // Ordenar por código (columna 0)
                pageLength: 10,
                lengthMenu: [5, 10, 25, 50],
                columnDefs: [
                    {
                        targets: [3], // Columna de acciones
                        orderable: false,
                        searchable: false
                    }
                ]
            });

            // Si existe un campo de búsqueda general
            $("#txtSearch").on("keyup change", function () {
                dt.search(this.value).draw();
            });

        } else {
            Swal.fire("Error", json[0].mensaje || "No se pudo cargar la tabla", "error");
        }

    }).fail(function (jqXHR, textStatus, errorThrown) {
        Swal.close();
        Swal.fire("Error", "Falló la consulta: " + errorThrown, "error");
        console.log("Error carga tabla:", textStatus, errorThrown);
    });
}

// ===================== SWEET ALERT 2 LOADING ===================== //
function mostrar_cargando(titulo, mensaje = "") {
    Swal.fire({
        title: titulo,
        html: mensaje,
        allowOutsideClick: false,
        allowEscapeKey: false,
        didOpen: () => Swal.showLoading()
    });
}

// ===================== FUNCIÓN AUXILIAR PARA AUTOCOMPLETAR CÓDIGO ===================== //
// Si quieres que el código se autocomplete con ceros a la izquierda mientras se escribe
$(document).ready(function() {
    // Auto-completar ceros mientras se escribe (opcional)
    $('#txt_codigo').on('blur', function() {
        let codigo = $(this).val();
        if (codigo.length > 0 && codigo.length < 4) {
            // Rellenar con ceros a la izquierda
            codigo = codigo.padStart(4, '0');
            $(this).val(codigo);
        }
    });
    
    // Auto-enfoque en el campo código al abrir modal
    $('#modalTipoActivo').on('shown.bs.modal', function () {
        $('#txt_codigo').focus();
    });
});