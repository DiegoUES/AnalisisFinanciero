// login.js - login simple con roles

$(function () {
    const CTX = (typeof window.APP_CTX === 'string') ? window.APP_CTX : '';
    const URL_LOGIN = CTX + '/LoginServlet';

    $('#loginForm').on('submit', function (e) {
        e.preventDefault();

        const usuario    = $.trim($('#usuario').val());
        const contrasena = $.trim($('#contrasena').val());
        const $btn       = $('#btnLogin');

        if (!usuario || !contrasena) {
            Swal.fire({
                icon: 'warning',
                title: 'Campos requeridos',
                text: 'Debe ingresar usuario y contraseña.'
            });
            return;
        }

        $btn.prop('disabled', true);

        $.ajax({
            url: URL_LOGIN,
            type: 'POST',
            dataType: 'json',
            data: {
                usuario: usuario,
                contrasena: contrasena
            }
        }).done(function (json) {
            if (!json || !json.resultado) {
                Swal.fire('Error', 'Respuesta inválida del servidor.', 'error');
                return;
            }

            if (json.resultado === 'ok') {
                const nombre = json.usuario || '';
                const rol    = json.rol || '';

                Swal.fire({
                    icon: 'success',
                    title: 'Bienvenido',
                    html: nombre
                        ? ('Usuario: <b>' + nombre + '</b><br>Rol: <b>' + rol + '</b>')
                        : 'Inicio de sesión correcto.',
                    timer: 1300,
                    timerProgressBar: true,
                    showConfirmButton: false
                }).then(() => {
                    window.location.href = CTX + '/index.jsp';
                });
            } else {
                Swal.fire({
                    icon: 'error',
                    title: 'No se pudo iniciar sesión',
                    text: json.mensaje || 'Usuario o contraseña incorrectos.'
                });
            }
        }).fail(function () {
            Swal.fire('Error', 'No se pudo contactar con el servidor.', 'error');
        }).always(function () {
            $btn.prop('disabled', false);
        });
    });
});
