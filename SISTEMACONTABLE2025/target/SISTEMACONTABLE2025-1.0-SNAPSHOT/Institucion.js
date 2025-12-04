$(function () {

    // Activar Parsley
    $('#form_institucion').parsley();

    // Cargar tabla al iniciar
    cargarTabla();

    // Botón: abrir modal para registrar
    $(document).on("click", "#btn_nueva_institucion", function (e) {
        e.preventDefault();
        console.log("Click en btn_nueva_institucion");

        $("#form_institucion").trigger("reset");
        $("#opcion").val("si_registro");
        $("#tituloModal").text("Registrar Institución");

        $("#grupo_codigo").show();
        $("#txt_id").val("");

        $("#modalInstitucion").modal("show");
    });

    // Submit del formulario (registrar / actualizar)
    $(document).on("submit", "#form_institucion", function (e) {
        e.preventDefault();

        mostrar_cargando("Procesando solicitud", "Por favor espere...");

        var datos = $("#form_institucion").serialize();
        console.log("Datos enviados =", datos);

        $.ajax({
            dataType: "json",
            method: "POST",
            url: "RegInstitucion",
            data: datos
        }).done(function (json) {
            Swal.close();

            if (json[0].resultado === "exito") {
                Swal.fire("Éxito", json[0].mensaje, "success");
                $("#modalInstitucion").modal("hide");
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
    $(document).on("click", ".btn_editar", function (e) {
        e.preventDefault();

        const id = $(this).data("id");
        console.log("Editar institución ID =", id);

        $.ajax({
            dataType: "json",
            method: "POST",
            url: "RegInstitucion",
            data: {opcion: "si_institucion_especifica", id: id}
        }).done(function (json) {

            if (json[0].resultado === "exito") {

                // Cargar datos en el formulario
                $("#opcion").val("si_actualizo");
                $("#txt_id").val(json[0].ID);
                $("#txt_nombre").val(json[0].NOMBRE);

                $("#grupo_codigo").hide();

                $("#tituloModal").text("Editar Institución");
                $("#modalInstitucion").modal("show");

            } else {
                Swal.fire("Error", json[0].mensaje, "warning");
            }

        }).fail(function () {
            Swal.fire("Error", "Error al cargar institución", "error");
        });
    });

});

// ===================== CARGAR TABLA ===================== //
function cargarTabla() {

    mostrar_cargando("Cargando datos", "Espere un momento...");

    $.ajax({
        dataType: "json",
        method: "POST",
        url: "RegInstitucion",
        data: {opcion: "cargarTabla"}
    }).done(function (json) {

        Swal.close();

        if (json && json[0] && json[0].resultado === "exito") {

            $("#tablaInstituciones").empty().html(json[0].tabla);

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
