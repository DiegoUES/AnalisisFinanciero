// TipoUsado.js
$(function () {
    
    // Cargar tabla al iniciar
    cargarTablaTiposUsado();

    // Botón: abrir modal para registrar
    $(document).on("click", "#btn_nuevo_tipo_usado", function (e) {
        e.preventDefault();
        console.log("Click en btn_nuevo_tipo_usado");

        $("#form_tipo_usado").trigger("reset");
        $("#opcion").val("si_registro");
        $("#tituloModal").text("Registrar Tipo de Usado");
        
        // Limpiar campo id oculto
        $("#txt_id").val("");

        $("#modalTipoUsado").modal("show");
    });

    // Validación de años (solo números positivos)
    $(document).on("input", "#txt_anyos", function() {
        // Solo números
        $(this).val($(this).val().replace(/[^\d]/g, ''));
        
        // Validar que no sea 0 o negativo
        const valor = parseInt($(this).val());
        if (valor < 1) {
            $(this).val("");
        }
    });

    // Validación del porcentaje (20-80)
    $(document).on("change blur", "#txt_porcentaje", function() {
        const value = parseInt($(this).val());
        if (value < 20) {
            $(this).val(20);
        } else if (value > 80) {
            $(this).val(80);
        }
    });

    // Submit del formulario (registrar / actualizar)
    $(document).on("submit", "#form_tipo_usado", function (e) {
        e.preventDefault();

        // Validaciones adicionales antes de enviar
        const anyos = $('#txt_anyos').val();
        const porcentaje = parseInt($('#txt_porcentaje').val());

        // Validar años
        if (anyos === "" || parseInt(anyos) < 1) {
            Swal.fire("Error", "Los años deben ser un número positivo", "error");
            $('#txt_anyos').focus();
            return;
        }

        // Validar porcentaje
        if (isNaN(porcentaje) || porcentaje < 20 || porcentaje > 80) {
            Swal.fire("Error", "El porcentaje debe estar entre 20 y 80", "error");
            $('#txt_porcentaje').focus();
            return;
        }

        mostrar_cargando("Procesando solicitud", "Por favor espere...");

        var datos = $("#form_tipo_usado").serialize();
        console.log("Datos enviados =", datos);

        $.ajax({
            dataType: "json",
            method: "POST",
            url: "RegTipoUsado",
            data: datos
        }).done(function (json) {
            Swal.close();

            if (json[0].resultado === "exito") {
                Swal.fire("Éxito", json[0].mensaje, "success");
                $("#modalTipoUsado").modal("hide");
                cargarTablaTiposUsado();
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
        console.log("Editar tipo usado ID =", id);

        mostrar_cargando("Cargando datos", "Espere un momento...");

        $.ajax({
            dataType: "json",
            method: "POST",
            url: "RegTipoUsado",
            data: {opcion: "si_tipo_especifico", id: id}
        }).done(function (json) {
            Swal.close();

            if (json[0].resultado === "exito") {
                // Cargar datos en el formulario
                $("#opcion").val("si_actualizo");
                $("#txt_id").val(json[0].ID);
                $("#txt_anyos").val(json[0].ANYOS);
                $("#txt_porcentaje").val(json[0].PORCENTAJE);

                $("#tituloModal").text("Editar Tipo de Usado");
                $("#modalTipoUsado").modal("show");
            } else {
                Swal.fire("Error", json[0].mensaje, "warning");
            }

        }).fail(function (jqXHR, textStatus, errorThrown) {
            Swal.close();
            Swal.fire("Error", "Error al cargar tipo usado: " + errorThrown, "error");
        });
    });

    // Botón ELIMINAR en la tabla
    $(document).on("click", ".btn_eliminar", function (e) {
        e.preventDefault();

        const id = $(this).data("id");
        console.log("Eliminar tipo usado ID =", id);

        // Obtener los años para mostrar en confirmación
        const anyos = $(this).closest('tr').find('td:first').text();

        Swal.fire({
            title: "¿Está seguro de eliminar?",
            html: `Se eliminará el tipo usado de: <strong>${anyos}</strong><br>Esta acción no se puede deshacer`,
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
                    url: "RegTipoUsado",
                    data: {opcion: "si_elimina", id: id}
                }).done(function (json) {
                    Swal.close();

                    if (json[0].resultado === "exito") {
                        Swal.fire("Éxito", json[0].mensaje, "success");
                        cargarTablaTiposUsado();
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
function cargarTablaTiposUsado() {

    mostrar_cargando("Cargando datos", "Espere un momento...");

    $.ajax({
        dataType: "json",
        method: "POST",
        url: "RegTipoUsado",
        data: {opcion: "cargarTabla"}
    }).done(function (json) {

        Swal.close();

        if (json && json[0] && json[0].resultado === "exito") {

            $("#tablaTiposUsado").empty().html(json[0].tabla);

            const dt = $("#tabla_idServlet").DataTable({
                language: {
                    url: "https://cdn.datatables.net/plug-ins/1.12.1/i18n/es-ES.json"
                },
                responsive: true,
                order: [[0, 'asc']], // Ordenar por años (columna 0)
                pageLength: 10,
                lengthMenu: [5, 10, 25, 50],
                columnDefs: [
                    {
                        targets: [2], // Columna de acciones (índice 2)
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

// ===================== FUNCIONES AUXILIARES ===================== //
$(document).ready(function() {
    // Auto-enfoque en el campo años al abrir modal
    $('#modalTipoUsado').on('shown.bs.modal', function () {
        $('#txt_anyos').focus();
    });
    
    // Prevenir entradas no numéricas en años
    $('#txt_anyos').on('keypress', function(e) {
        const charCode = e.which ? e.which : e.keyCode;
        if (charCode < 48 || charCode > 57) {
            return false;
        }
        return true;
    });
    
    // Prevenir entradas no numéricas en porcentaje
    $('#txt_porcentaje').on('keypress', function(e) {
        const charCode = e.which ? e.which : e.keyCode;
        if (charCode < 48 || charCode > 57) {
            return false;
        }
        return true;
    });
});