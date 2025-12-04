/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */
$(function () {
    // Obtener el contextPath
    const contextPath = window.location.pathname.split('/')[1];
    const baseUrl = '/' + contextPath;

    console.log("Context Path:", contextPath);
    console.log("Base URL:", baseUrl);

    // Cargar tabla al iniciar
    cargarTablaBaja();

    // Botón: Ir a Activo Fijo Principal
    $(document).on("click", "#btn_irAActivoPrincipal", function (e) {
        e.preventDefault();
        console.log("Redirigiendo a Activo.jsp");
        window.location.href = "Activo.jsp";
    });

});

// ===================== CARGAR TABLA DE BAJAS ===================== //
function cargarTablaBaja() {
    const contextPath = window.location.pathname.split('/')[1];
    const baseUrl = '/' + contextPath;

    console.log("Cargando tabla de bajas...");

    mostrar_cargando("Cargando activos dados de baja", "Espere un momento...");

    // Cargar tabla de activos dados de baja
    $.ajax({
        dataType: "json",
        method: "POST",
        url: baseUrl + "/RegActivoBaja",
        data: {opcion: "cargarTablaBaja"}
    }).done(function (json) {
        Swal.close();
        console.log("Tabla bajas:", json);
        
        if (json && json[0] && json[0].resultado === "exito") {
            $("#tablaActivosBaja").empty().html(json[0].tabla);
            inicializarDataTable("tablaActivosBaja_id");
            console.log("Tabla de activos dados de baja cargada exitosamente");
        } else {
            console.error("Error en respuesta tabla bajas:", json);
            $("#tablaActivosBaja").html('<div class="alert alert-warning">No hay activos dados de baja</div>');
        }

    }).fail(function(jqXHR, textStatus) {
        Swal.close();
        console.error("Error cargando tabla bajas:", textStatus);
        $("#tablaActivosBaja").html('<div class="alert alert-danger">Error al cargar activos dados de baja</div>');
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

// ===================== INICIALIZACIÓN AL CARGAR LA PÁGINA ===================== //
$(document).ready(function() {
    console.log("Página de Activos Dados de Baja cargada correctamente");
    
    // Ajustar DataTables cuando la página esté completamente cargada
    setTimeout(function() {
        $.fn.dataTable.tables({ visible: true, api: true }).columns.adjust();
    }, 500);
});

