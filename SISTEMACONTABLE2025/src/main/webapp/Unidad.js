$(function () {

    // Activar Parsley
    $('#form_unidad').parsley();

    // Cargar tabla al iniciar
    cargarTabla();

    // Botón: abrir modal para registrar
    $(document).on("click", "#btn_nueva_unidad", function (e) {
        e.preventDefault();
        console.log("Click en btn_nueva_unidad");

        $("#form_unidad").trigger("reset");
        $("#opcion").val("si_registro");
        $("#tituloModalUnidad").text("Registrar Unidad");

        // mostrar campo nombre y habilitarlo
        $("#grupo_nombre_unidad").show();
        $("#nombre").prop("disabled", false).attr("required", true);


        // Cargar combo de instituciones
        cargarComboInstitucion();

        $("#modalUnidad").modal("show");
    });

    // Submit del formulario (registrar / actualizar)
    $(document).on("submit", "#form_unidad", function (e) {
        e.preventDefault();

        mostrar_cargando("Procesando solicitud", "Por favor espere...");

        var datos = $("#form_unidad").serialize();
        console.log("Datos enviados =", datos);

        $.ajax({
            dataType: "json",
            method: "POST",
            url: "RegUnidad",
            data: datos
        }).done(function (json) {
            Swal.close();

            if (json[0].resultado === "exito") {
                Swal.fire("Éxito", json[0].mensaje, "success");
                $("#modalUnidad").modal("hide");
                cargarTabla();
            } else {
                Swal.fire("Error", "No se pudo realizar la acción", "error");
                console.log("Detalle error:", json);
            }

        }).fail(function () {
            Swal.close();
            Swal.fire("Error", "Error en la solicitud AJAX", "error");
        });

    });

    // Botón EDITAR en la tabla
    $(document).on("click", ".btn_editar_unidad", function (e) {
        e.preventDefault();

        const id = $(this).data("id");
        console.log("Editar unidad ID =", id);

        $.ajax({
            dataType: "json",
            method: "POST",
            url: "RegUnidad",
            data: {opcion: "cargarDatos", id: id}
        }).done(function (json) {

            if (json[0].resultado === "exito") {

                // Cargar datos en el formulario
                $("#opcion").val("si_actualizo");
                $("#id").val(json[0].ID);
                //$("#nombre").val(json[0].NOMBRE);

                $("#tituloModalUnidad").text("Editar Unidad");

                // OCULTAR campo nombre y deshabilitarlo para que Parsley no lo valide
                $("#grupo_nombre_unidad").hide();
                $("#nombre").prop("disabled", true).removeAttr("required");


                // Cargar combo y seleccionar la institución de la unidad
                const idInst = json[0].ID_INSTITUCION;
                cargarComboInstitucion(idInst);

                $("#modalUnidad").modal("show");

            } else {
                Swal.fire("Error", json[0].mensaje, "warning");
            }

        }).fail(function () {
            Swal.fire("Error", "Error al cargar unidad", "error");
        });
    });

});

// ===================== CARGAR TABLA ===================== //
function cargarTabla() {

    mostrar_cargando("Cargando datos", "Espere un momento...");

    $.ajax({
        dataType: "json",
        method: "POST",
        url: "RegUnidad",
        data: {opcion: "cargarTabla"}
    }).done(function (json) {

        Swal.close();

        if (json && json[0] && json[0].resultado === "exito") {

            $("#tablaUnidades").empty().html(json[0].tabla);

            const dt = $("#tabla_idServlet").DataTable({
                language: {
                    url: "https://cdn.datatables.net/plug-ins/1.12.1/i18n/es-ES.json"
                },
                responsive: true,
                order: [[0, 'asc']]
            });

            $("#txtSearch").on("keyup change", function () {
                dt.search(this.value).draw();
            });

        } else {
            Swal.fire("Error", "No se pudo cargar la tabla", "error");
        }

    }).fail(function (jqXHR, textStatus) {
        Swal.close();
        Swal.fire("Error", "Falló la consulta: " + textStatus, "error");
    });
}

// ===================== CARGAR COMBO INSTITUCIONES ===================== //
function cargarComboInstitucion(selectedId) {

    $.ajax({
        dataType: "json",
        method: "POST",
        url: "RegUnidad",
        data: {opcion: "cargarComboInstitucion"}
    }).done(function (json) {

        if (json && json[0] && json[0].resultado === "exito") {

            // $("#institucion").html(json[0].institucion);
            // Agregamos nuevamente la opción por defecto
            $("#institucion").html(
                    '<option value="">Seleccione...</option>' + json[0].institucion
                    );

            if (selectedId !== undefined && selectedId !== null && selectedId !== "") {
                $("#institucion").val(String(selectedId));
            }

        } else {
            console.log("No se pudo cargar el combo de instituciones", json);
        }

    }).fail(function (jqXHR, textStatus) {
        console.log("Error al cargar combo instituciones:", textStatus);
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
