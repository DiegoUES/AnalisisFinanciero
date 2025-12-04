<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    String ctx = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="es">

    <head>
        <meta charset="UTF-8">
        <title>Iniciar sesión</title>

        <!-- ContextPath para JS (lo usa login.js) -->
        <script>
            window.APP_CTX = '<%= ctx %>';
        </script>

        <!-- jQuery -->
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.0/jquery.min.js"></script>

        <!-- Parsley (validación de formularios, opcional) -->
        <script src="http://parsleyjs.org/dist/parsley.js"></script>

        <!-- SweetAlert2 -->
        <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>

        <!-- Bootstrap CSS + JS -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/js/bootstrap.bundle.min.js"></script>

        <!-- Bootstrap Icons -->
        <link rel="stylesheet"
              href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">

        <style>
            body {
                margin: 0;
                min-height: 100vh;
                display: flex;
                align-items: center;
                justify-content: center;
                background: #f3f4f6;
                font-family: system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif;
            }

            .login-wrapper {
                width: 100%;
                max-width: 420px;
            }

            .logo-circle {
                width: 64px;
                height: 64px;
                border-radius: 50%;
                display: flex;
                align-items: center;
                justify-content: center;
                background: #0d6efd1a;
                margin: 0 auto;
            }
        </style>
    </head>

    <body>

        <div class="login-wrapper px-3">
            <div class="card shadow-sm">
                <div class="card-body">

                    <div class="text-center mb-3">
                        <div class="logo-circle mb-2">
                            <i class="bi bi-stack fs-2 text-primary"></i>
                        </div>
                        <h4 class="fw-bold mb-0">Iniciar sesión</h4>
                        <p class="text-muted small mb-0">
                            Ingresa tus credenciales para acceder al sistema.
                        </p>
                    </div>

                    <!-- IMPORTANTE: id y names iguales a los que usa login.js -->
                    <form id="loginForm"
                          method="post"
                          autocomplete="off"
                          data-parsley-validate>

                        <div class="mb-3">
                            <label for="usuario" class="form-label">Usuario</label>
                            <div class="input-group">
                                <span class="input-group-text">
                                    <i class="bi bi-person"></i>
                                </span>
                                <input type="text"
                                       class="form-control"
                                       id="usuario"
                                       name="usuario"
                                       required
                                       data-parsley-required-message="Ingrese su usuario"
                                       autocomplete="username">
                            </div>
                        </div>

                        <div class="mb-3">
                            <label for="contrasena" class="form-label">Contraseña</label>
                            <div class="input-group" id="passwordWrapper">
                                <span class="input-group-text">
                                    <i class="bi bi-lock"></i>
                                </span>
                                <!-- name="contrasena" para que tu login.js la tome bien -->
                                <input type="password"
                                       class="form-control"
                                       id="contrasena"
                                       name="contrasena"
                                       required
                                       data-parsley-required-message="Ingrese su contraseña"
                                       autocomplete="current-password">
                                <button class="btn btn-outline-secondary" type="button" id="btnTogglePassword">
                                    <i class="bi bi-eye"></i>
                                </button>
                            </div>
                        </div>

                        <div class="d-flex justify-content-between align-items-center mb-3">
                            <div class="form-check">
                                <input class="form-check-input" type="checkbox" id="recordar" name="recordar">
                                <label class="form-check-label small" for="recordar">
                                    Recordar sesión
                                </label>
                            </div>
                        </div>

                        <div class="d-grid mb-2">
                            <button type="submit" id="btnLogin" class="btn btn-primary">
                                <i class="bi bi-box-arrow-in-right me-1"></i> Ingresar
                            </button>
                        </div>

                    </form>

                </div>

                <div class="card-footer text-center small text-muted">
                    &copy;
                    <%
                        try {
                            out.print(java.time.Year.now().getValue());
                        } catch (Exception e) {
                            out.print("2025");
                        }
                    %> Sistema Contable 2025
                </div>
            </div>
        </div>

        <script>
            $(function () {
                // Mostrar/ocultar contraseña (usa #contrasena, no cambié el name)
                $('#btnTogglePassword').on('click', function () {
                    const input = $('#contrasena');
                    const icon = $(this).find('i');

                    if (input.attr('type') === 'password') {
                        input.attr('type', 'text');
                        icon.removeClass('bi-eye').addClass('bi-eye-slash');
                    } else {
                        input.attr('type', 'password');
                        icon.removeClass('bi-eye-slash').addClass('bi-eye');
                    }
                });

                // Activar Parsley si lo quieres usar
                $('#loginForm').parsley();

                // Si algún día devuelves error/mensaje en atributos JSP:
                <% 
                    String error = (String) request.getAttribute("error");
                    String mensaje = (String) request.getAttribute("mensaje");
                    if (error != null) {
                %>
                Swal.fire({
                    icon: 'error',
                    title: 'Error',
                    text: '<%= error %>'
                });
                <% } else if (mensaje != null) { %>
                Swal.fire({
                    icon: 'info',
                    title: 'Información',
                    text: '<%= mensaje %>'
                });
                <% } %>
            });
        </script>

        <!-- Tu JS de login con AJAX / roles -->
        <script src="<%= ctx %>/login.js"></script>

    </body>
</html>
