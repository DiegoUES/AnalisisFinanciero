$(function () {
    cargarTabla();
    cargarRoles();

    // Nuevo
    $('#btnNuevo').on('click', function () {
        const form = $('#formUsuario')[0];
        form.reset();
        $('#id').val('');
        $('#grupoContrasena').show();
        $('#contrasena').prop('required', true).prop('disabled', false);
        $('#modalUsuarioLabel').text('Nuevo usuario');
        $('#modalUsuario').modal('show');
    });

    // Guardar / Actualizar
    $('#formUsuario').on('submit', function (e) {
        e.preventDefault();

        if (!this.checkValidity()) {
            this.reportValidity();
            return;
        }

        const id = $('#id').val();
        const accion = id ? 'actualizar' : 'guardar';

        const datos = $(this).serialize() + '&accion=' + accion;

        Swal.fire({
            title: 'Confirmar',
            text: '¿Desea guardar la información del usuario?',
            icon: 'question',
            showCancelButton: true,
            confirmButtonText: 'Sí, guardar',
            cancelButtonText: 'Cancelar'
        }).then((res) => {
            if (!res.isConfirmed) return;

            $.ajax({
                url: USUARIO_ENDPOINT,
                method: 'POST',
                dataType: 'json',
                data: datos
            }).done(function (json) {
                if (!Array.isArray(json) || !json.length) {
                    Swal.fire('Error', 'Respuesta inválida del servidor', 'error');
                    return;
                }

                const r = json[0];

                if (r.resultado === 'ok') {
                    Swal.fire('Correcto', r.mensaje || 'Operación realizada', 'success');
                    $('#modalUsuario').modal('hide');
                    cargarTabla();
                } else if (r.resultado === 'error_validacion') {
                    Swal.fire('Datos inválidos', r.mensaje || 'Revise los campos', 'warning');
                } else {
                    Swal.fire('Error', r.mensaje || 'No se pudo procesar la solicitud', 'error');
                }
            }).fail(function () {
                Swal.fire('Error', 'No se pudo contactar con el servidor', 'error');
            });
        });
    });

    // Editar
    $(document).on('click', '.btn-editar', function () {
        const id = $(this).data('id');

        $.ajax({
            url: USUARIO_ENDPOINT,
            method: 'POST',
            dataType: 'json',
            data: {accion: 'obtener', id: id}
        }).done(function (json) {
            if (!Array.isArray(json) || !json.length) {
                Swal.fire('Error', 'Respuesta inválida del servidor', 'error');
                return;
            }
            const r = json[0];
            if (r.resultado !== 'ok') {
                Swal.fire('Error', r.mensaje || 'No se pudo obtener el registro', 'error');
                return;
            }

            const form = $('#formUsuario')[0];
            form.reset();

            $('#id').val(r.id);
            $('#nombre').val(r.nombre);
            $('#usuario').val(r.usuario);
            $('#idrol').val(r.idrol);

            $('#grupoContrasena').hide();
            $('#contrasena').prop('required', false).prop('disabled', true).val('');

            $('#modalUsuarioLabel').text('Editar usuario');
            $('#modalUsuario').modal('show');
        }).fail(function () {
            Swal.fire('Error', 'No se pudo contactar con el servidor', 'error');
        });
    });

    // Eliminar
    $(document).on('click', '.btn-eliminar', function () {
        const id = $(this).data('id');

        Swal.fire({
            title: 'Eliminar usuario',
            text: '¿Seguro que desea eliminar este usuario?',
            icon: 'warning',
            showCancelButton: true,
            confirmButtonText: 'Sí, eliminar',
            cancelButtonText: 'Cancelar'
        }).then((res) => {
            if (!res.isConfirmed) return;

            $.ajax({
                url: USUARIO_ENDPOINT,
                method: 'POST',
                dataType: 'json',
                data: {accion: 'eliminar', id: id}
            }).done(function (json) {
                if (!Array.isArray(json) || !json.length) {
                    Swal.fire('Error', 'Respuesta inválida del servidor', 'error');
                    return;
                }
                const r = json[0];
                if (r.resultado === 'ok') {
                    Swal.fire('Eliminado', r.mensaje || 'Registro eliminado', 'success');
                    cargarTabla();
                } else {
                    Swal.fire('Error', r.mensaje || 'No se pudo eliminar', 'error');
                }
            }).fail(function () {
                Swal.fire('Error', 'No se pudo contactar con el servidor', 'error');
            });
        });
    });

});

// ================== Funciones ==================

function cargarTabla() {
    $.ajax({
        url: USUARIO_ENDPOINT,
        method: 'POST',
        dataType: 'json',
        data: {accion: 'listar'}
    }).done(function (json) {
        if (!Array.isArray(json) || !json.length) {
            $('#contenedorTabla').html('<p class="text-danger mb-0">Respuesta inválida del servidor.</p>');
            return;
        }
        const r = json[0];

        if (r.resultado !== 'ok') {
            $('#contenedorTabla').html('<p class="text-danger mb-0">' + (r.mensaje || 'No se pudieron cargar los usuarios') + '</p>');
            return;
        }

        $('#contenedorTabla').html(r.tabla);

        if ($.fn.DataTable.isDataTable('#tablaUsuarios')) {
            $('#tablaUsuarios').DataTable().destroy();
        }

        $('#tablaUsuarios').DataTable({
            language: {
                url: 'https://cdn.datatables.net/plug-ins/1.13.8/i18n/es-ES.json'
            }
        });
    }).fail(function () {
        $('#contenedorTabla').html('<p class="text-danger mb-0">Error al cargar los usuarios.</p>');
    });
}

function cargarRoles() {
    $.ajax({
        url: USUARIO_ENDPOINT,
        method: 'POST',
        dataType: 'json',
        data: {accion: 'roles'}
    }).done(function (json) {
        if (!Array.isArray(json) || !json.length) {
            console.error('Respuesta inválida al cargar roles');
            return;
        }
        const r = json[0];
        if (r.resultado === 'ok') {
            $('#idrol').html('<option value="">Seleccione un rol...</option>' + (r.roles || ''));
        } else {
            console.error('No se pudieron cargar los roles: ' + (r.mensaje || ''));
        }
    }).fail(function () {
        console.error('Error AJAX al cargar roles');
    });
}
