# PlayHub {#mainpage}

PlayHub es una aplicación académica compuesta por una API REST en Spring Boot y una interfaz web en Next.js.

Esta documentación describe la estructura interna y las responsabilidades del código. El contrato ejecutable de los endpoints se consulta en Swagger UI cuando el backend está iniciado:

```text
http://localhost:8080/swagger-ui.html
```

## Contenido

- @ref backend_overview "Arquitectura del backend"
- @ref frontend_overview "Arquitectura del frontend"
- @ref documentation_guide "Convenciones de documentación"

## Alcance de Doxygen

Doxygen analiza automáticamente:

- `backend/src/main/java`: clases, interfaces, enumeraciones, paquetes y relaciones Java.
- `frontend/src`: módulos, componentes y funciones JavaScript.
- `docs`: decisiones y explicaciones que no pertenecen a una clase concreta.

La salida se genera en `docs/generated/html` y no debe incluirse en Git.
