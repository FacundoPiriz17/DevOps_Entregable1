# Arquitectura del backend {#backend_overview}

El backend utiliza Spring Boot y organiza el código por funcionalidad dentro de `com.devops.backend`.

## Capas principales

- `common.config`: configuración transversal de la aplicación y OpenAPI.
- `common.security`: JWT, autorización, CORS y respuestas de seguridad.
- `common.exception`: contrato común y traducción de errores de dominio.
- `modules.*.controller`: adaptadores HTTP de cada funcionalidad.
- `modules.*.service`: casos de uso y reglas de negocio.
- `modules.*.repository`: acceso a persistencia mediante Spring Data JPA.
- `modules.*.entity`: estado persistente del dominio.
- `modules.*.dto`: contratos de entrada y salida.

## API REST

Swagger/OpenAPI documenta rutas, esquemas y autenticación Bearer. Doxygen complementa esa información mostrando la implementación, las dependencias entre clases y los métodos internos; no reemplaza el contrato OpenAPI.

## Persistencia

La ejecución normal utiliza PostgreSQL y Flyway. Las migraciones están en `backend/src/main/resources/db/migration` y deben seguir siendo la fuente de verdad del esquema productivo.

## Seguridad

Spring Security protege los recursos mediante JWT y roles. Las clases bajo `common.security` concentran la conversión de autoridades, el acceso al usuario actual y las respuestas JSON para autenticación o autorización fallidas.
