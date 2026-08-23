# language: es
Característica: Supervisión de usuarios y utilización de videojuegos
  Como administrador
  Quiero consultar información básica de los usuarios y su utilización de los videojuegos, y dar de baja cuentas cuando corresponda
  Para mantener el sistema bajo control

  Antecedentes:
    Dado que el administrador ha iniciado sesión con permisos de administrador

  Escenario: Consultar información básica de un usuario registrado
    Dado que existe un usuario registrado en el sistema
    Cuando el administrador consulta su información
    Entonces se muestran sus datos básicos de cuenta

  Escenario: Consultar la utilización de videojuegos de un usuario
    Dado que un usuario tiene videojuegos en su biblioteca con tiempo de juego registrado
    Cuando el administrador consulta la utilización de videojuegos de ese usuario
    Entonces se muestra el tiempo jugado por videojuego

  Escenario: Dar de baja una cuenta de usuario
    Dado que existe una cuenta de usuario activa
    Cuando el administrador da de baja esa cuenta
    Entonces la cuenta queda marcada como inactiva
    Y el usuario ya no puede iniciar sesión
    Y su historial de biblioteca y sesiones se conserva

  Escenario: Un usuario sin permisos de administrador intenta consultar información de otro usuario
    Dado que un usuario no tiene permisos de administrador
    Cuando intenta consultar la información de otro usuario
    Entonces el sistema deniega el acceso
