# PlayHub — DevOps Entregable 1

Proyecto académico de una tienda de videojuegos, con backend en Spring Boot, frontend en Next.js y base de datos PostgreSQL. Incluye migraciones versionadas con Flyway, autenticación JWT, y despliegue en Kubernetes con estrategia Blue/Green.

## Arquitectura

```text
Usuario
   |
   v
Frontend (Next.js)
   |  HTTP / REST
   v
Backend (Spring Boot)
   |  JPA
   v
PostgreSQL
```

## Requisitos previos

- [Docker](https://www.docker.com/) y Docker Compose (para correr la app localmente).
- Para el despliegue en Kubernetes: [minikube](https://minikube.sigs.k8s.io/) (u otro cluster) y `kubectl`.

## Cómo correr la aplicación (local, con Docker Compose)

1. Copiar `.env.example` a `.env` y completar las variables:

   ```bash
   cp .env.example .env
   ```

   Variables requeridas:
   - `DB_PASSWORD`: contraseña de PostgreSQL.
   - `JWT_SECRET`: secreto usado para firmar los tokens JWT.
   - `ADMIN_EMAIL` / `ADMIN_PASSWORD`: credenciales del usuario administrador que se crea al iniciar.

2. Levantar los servicios:

   ```bash
   docker compose up --build
   ```

   Esto levanta:
   - **PostgreSQL** en el puerto `5433`.
   - **Backend** (Spring Boot) en `http://localhost:8080`.
   - **Frontend** (Next.js) en `http://localhost:6996`.

   El modo `watch` de Docker Compose está habilitado: los cambios en `backend/src` y `frontend/` se sincronizan automáticamente en los contenedores.

## Cómo probar la aplicación

- **Frontend**: abrir `http://localhost:6996` en el navegador y navegar la tienda (registro/login, catálogo de juegos, biblioteca del usuario, administración según el rol).
- **API / Swagger UI**: con el backend corriendo, la documentación interactiva de la API REST está disponible en:

  ```text
  http://localhost:8080/swagger-ui.html
  ```

  Ahí se puede ver cada endpoint, probarlo directamente, y autenticar las peticiones protegidas con el Bearer token JWT obtenido al hacer login.

- **Usuario administrador**: se crea automáticamente al iniciar el backend, usando las credenciales `ADMIN_EMAIL` / `ADMIN_PASSWORD` definidas en `.env`.
- **Escenarios de comportamiento (BDD)**: en `features/` hay especificaciones Gherkin (`.feature`) que describen los flujos principales de la aplicación (registro/login, catálogo, biblioteca, administración de usuarios y juegos), útiles como guía para probar la app manualmente.

## Despliegue en Kubernetes (Blue/Green)

El proyecto también puede desplegarse en un cluster de Kubernetes usando la estrategia Blue/Green, mediante los scripts de la carpeta [`scripts/`](scripts/README.md). Resumen rápido:

```bash
# Inicializar minikube
minikube start

# Desplegar el slot Blue (activo)
./scripts/deploy.sh          # Linux/macOS
.\scripts\deploy.ps1         # Windows PowerShell

# Alternar entre slots Blue/Green
./scripts/switch-slot.sh green
```

Ver [`scripts/README.md`](scripts/README.md) para el detalle completo de cada script, requisitos, y el flujo típico de despliegue Blue/Green.

## Documentación adicional

- Documentación técnica generada con Doxygen (arquitectura de backend/frontend, convenciones): ver `docs/`.
- Manifiestos de Kubernetes: `k8s/`.
