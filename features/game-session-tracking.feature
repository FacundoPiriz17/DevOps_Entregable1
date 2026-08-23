# language: es
Característica: Registro de sesiones de juego simuladas
  Como usuario registrado
  Quiero iniciar y finalizar sesiones de juego, y consultar el tiempo jugado
  Para ver cuánto tiempo dediqué a cada videojuego

  Antecedentes:
    Dado que el usuario ha iniciado sesión

  Escenario: Iniciar una sesión de juego
    Dado que el videojuego forma parte de la biblioteca del usuario
    Y no tiene una sesión activa para ese videojuego
    Cuando el usuario inicia el videojuego
    Entonces se registra el comienzo de una nueva sesión
    Y la sesión queda marcada como activa

  Escenario: Finalizar una sesión de juego activa
    Dado que el usuario tiene una sesión activa iniciada para un videojuego de su biblioteca
    Cuando el usuario detiene el videojuego
    Entonces se registra la finalización de la sesión
    Y se calcula la duración transcurrida
    Y la sesión queda marcada como finalizada

  Escenario: Intentar finalizar una sesión que no está activa
    Dado que el usuario no tiene ninguna sesión activa para un videojuego de su biblioteca
    Cuando el usuario intenta detener el videojuego
    Entonces el sistema informa que no hay una sesión activa
    Y no se registra ninguna finalización

  Escenario: Intentar iniciar un videojuego que no está en la biblioteca
    Dado que el videojuego no forma parte de la biblioteca del usuario
    Cuando el usuario intenta iniciar el videojuego
    Entonces el sistema rechaza la acción
    Y informa que el videojuego no está disponible en su biblioteca

  Escenario: Consultar el tiempo jugado durante una sesión
    Dado que el usuario finalizó una sesión con una duración registrada
    Cuando el usuario consulta esa sesión
    Entonces se muestra el tiempo jugado durante esa sesión

  Escenario: Consultar el tiempo total acumulado en un videojuego
    Dado que el usuario tiene las siguientes sesiones finalizadas para un videojuego de su biblioteca:
      | Fecha      | Duración |
      | 2026-08-10 | 45 min   |
      | 2026-08-15 | 90 min   |
      | 2026-08-20 | 30 min   |
    Cuando el usuario consulta el tiempo total acumulado en ese videojuego
    Entonces se muestra un total de 165 minutos jugados
