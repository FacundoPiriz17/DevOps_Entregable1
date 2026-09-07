# PlayHub — DevOps Entregable 1

Proyecto académico de una tienda de videojuegos, con backend en Spring Boot, frontend en Next.js y base de datos PostgreSQL. Incluye migraciones versionadas con Flyway, autenticación JWT, y despliegue en Kubernetes con estrategia Blue/Green.

La aplicación está compuesta por:

- **Frontend:** Next.js
- **Backend:** Spring Boot
- **Base de datos:** PostgreSQL
- **Migraciones:** Flyway
- **Autenticación:** JWT
- **Contenedores:** Docker
- **Orquestación:** Kubernetes
- **Cluster local:** Minikube
- **Estrategia de despliegue:** Blue/Green
- **Documentación técnica:** Doxygen

El proyecto incluye scripts para automatizar el despliegue, cambio de versión y limpieza del entorno Kubernetes tanto en **Windows PowerShell** como en **Linux/macOS**.

## Arquitectura

```text
                         ┌─────────────────────┐
                         │       Usuario       │
                         └──────────┬──────────┘
                                    │
                                    ▼
                         ┌─────────────────────┐
                         │  Frontend - Next.js │
                         │      Puerto 5745    │
                         └──────────┬──────────┘
                                    │
                              HTTP / REST
                                    │
                                    ▼
                       ┌─────────────────────────┐
                       │ Backend - Spring Boot   │
                       │       Puerto 8080       │
                       │        JWT / JPA        │
                       └────────────┬────────────┘
                                    │
                                   JDBC
                                    │
                                    ▼
                         ┌─────────────────────┐
                         │     PostgreSQL      │
                         │       5432          │
                         └─────────────────────┘
```

En Kubernetes se utilizan además:

```text
                    ┌────────────────────┐
                    │   Service estable  │
                    └─────────┬──────────┘
                              │
                     selector: slot
                              │
               ┌──────────────┴──────────────┐
               │                             │
               ▼                             ▼
        ┌─────────────┐               ┌─────────────┐
        │    BLUE     │               │    GREEN    │
        │     v1      │               │     v2      │
        └─────────────┘               └─────────────┘
```

El cambio Blue/Green se realiza modificando el selector `slot` de los Services estables de frontend y backend.

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
   - **Frontend** (Next.js) en `http://localhost:5745`.

   El modo `watch` de Docker Compose está habilitado: los cambios en `backend/src` y `frontend/` se sincronizan automáticamente en los contenedores.

## Cómo probar la aplicación

- **Frontend**: abrir `http://localhost:5745` en el navegador y navegar la tienda (registro/login, catálogo de juegos, biblioteca del usuario, administración según el rol).
- **API / Swagger UI**: con el backend corriendo, la documentación interactiva de la API REST está disponible en:

  ```text
  http://localhost:8080/swagger-ui.html
  ```

  Ahí se puede ver cada endpoint, probarlo directamente, y autenticar las peticiones protegidas con el Bearer token JWT obtenido al hacer login.

- **Usuario administrador**: se crea automáticamente al iniciar el backend, usando las credenciales `ADMIN_EMAIL` / `ADMIN_PASSWORD` definidas en `.env`.
- **Escenarios de comportamiento (BDD)**: en `features/` hay especificaciones Gherkin (`.feature`) que describen los flujos principales de la aplicación (registro/login, catálogo, biblioteca, administración de usuarios y juegos), útiles como guía para probar la app manualmente.

## Usuarios de prueba

La migración de datos de demostración incluye los siguientes usuarios:

| Nombre | Email | Rol | Contraseña |
|---|---|---|---|
| Facundo | `facundo@playhub.test` | Administrador | `PlayHub123` |
| Santiago | `santiago@playhub.test` | Administrador | `PlayHub123` |
| Agostina | `agostina@playhub.test` | Usuario general | `PlayHub123` |
| Agustín | `agustin@playhub.test` | Usuario general | `PlayHub123` |

### Usuarios administradores

Permiten probar funcionalidades administrativas, como la gestión de juegos y usuarios.

### Usuarios generales

Permiten probar los flujos habituales de la tienda, incluyendo catálogo, biblioteca, deseados y carrito.

## Despliegue en Kubernetes con Minikube

## 1. Iniciar Minikube utilizando Docker

```bash
minikube start --driver=docker
```

Comprobar estado:

```bash
minikube status
```

Comprobar el contexto actual:

```bash
kubectl config current-context
```

Debería aparecer:

```text
minikube
```

Si fuera necesario:

```bash
kubectl config use-context minikube
```

Comprobar conexión:

```bash
kubectl cluster-info
```

---

## Desplegar PlayHub

Los scripts de despliegue realizan automáticamente:

1. Validación de los manifiestos.
2. Construcción de las imágenes Docker.
3. Carga de las imágenes dentro del cluster local.
4. Despliegue de PostgreSQL.
5. Espera hasta que PostgreSQL esté disponible.
6. Creación del ConfigMap con las migraciones SQL.
7. Ejecución del Job de Flyway.
8. Creación de los Services.
9. Despliegue del slot Blue.
10. Opcionalmente, despliegue del slot Green.
11. Verificación del estado de los Deployments.

---

## Linux/macOS

Dar permisos de ejecución la primera vez:

```bash
chmod +x scripts/*.sh
```

Desplegar únicamente Blue:

```bash
./scripts/deploy.sh --cluster-type minikube
```

Desplegar Blue y Green:

```bash
./scripts/deploy.sh --cluster-type minikube --deploy-green
```

---

## Windows PowerShell

Desplegar únicamente Blue:

```powershell
.\scripts\deploy.ps1 -ClusterType minikube
```

Desplegar Blue y Green:

```powershell
.\scripts\deploy.ps1 -ClusterType minikube -DeployGreen
```

---

## Opciones adicionales de `deploy`

### Linux/macOS

```bash
./scripts/deploy.sh --help
```

Opciones disponibles:

```text
--deploy-green
--cluster-type
--cluster-name
--frontend-api-url
--skip-build
--skip-image-load
```

Por ejemplo:

```bash
./scripts/deploy.sh 
  --cluster-type minikube 
  --deploy-green 
  --frontend-api-url http://localhost:8080
```

### Windows PowerShell

Los equivalentes principales son:

```text
-DeployGreen
-ClusterType
-ClusterName
-FrontendApiUrl
-SkipBuild
-SkipImageLoad
```

---

## Estrategia Blue/Green

PlayHub mantiene dos versiones independientes de frontend y backend:

| Slot | Backend | Frontend |
|---|---|---|
| Blue | `playhub-backend-blue` | `playhub-frontend-blue` |
| Green | `playhub-backend-green` | `playhub-frontend-green` |

Los Services estables son:

```text
playhub-backend
playhub-frontend
```

Estos Services seleccionan los Pods utilizando la etiqueta:

```yaml
slot: blue
```

o:

```yaml
slot: green
```

---

## Linux/macOS

```bash
./scripts/switch-slot.sh green
```

## Windows PowerShell

```powershell
.\scripts\switch-slot.ps1 green
```

> ACLARACIÓN: La versión green tuvo que haber sido desplegada previamente para que el script switch-slot.ps1

De esta forma el tráfico puede cambiar entre ambas versiones sin modificar la URL utilizada por el cliente.

## Flujo completo recomendado para una demo

## 1. Iniciar Minikube

```bash
minikube start --driver=docker
```

## 2. Desplegar Blue y Green

### Windows

```powershell
.\scripts\deploy.ps1 -ClusterType minikube -DeployGreen
```

### Linux/macOS

```bash
./scripts/deploy.sh --cluster-type minikube --deploy-green
```

## 3. Comprobar recursos

```bash
kubectl get pods
kubectl get services
kubectl get jobs
kubectl get pvc
```

## 4. Exponer backend

```bash
kubectl port-forward service/playhub-backend 8080:8080
```

## 5. Exponer frontend

```bash
kubectl port-forward service/playhub-frontend 5745:5745
```

## 6. Abrir la aplicación

```text
http://localhost:5745
```

## 7. Probar Swagger

```text
http://localhost:8080/swagger-ui.html
```

## 8. Iniciar sesión

Por ejemplo:

```text
facundo@playhub.test
PlayHub123
```

## 9. Verificar el slot actual

```bash
kubectl get service playhub-backend playhub-frontend 
  -o custom-columns='SERVICE:.metadata.name,SLOT:.spec.selector.slot'
```

## 10. Cambiar a Green

```bash
./scripts/switch-slot.sh green
```

o:

```powershell
.\scripts\switch-slot.ps1 green
```

## 11. Reiniciar los port-forward

```bash
kubectl port-forward service/playhub-backend 8080:8080
```

```bash
kubectl port-forward service/playhub-frontend 5745:5745
```

## 12. Mostrar rollback

```bash
./scripts/switch-slot.sh blue
```

o:

```powershell
.\scripts\switch-slot.ps1 blue
```

---

## Documentación adicional

- Documentación técnica generada con Doxygen (arquitectura de backend/frontend, convenciones): ver `docs/`.
- Manifiestos de Kubernetes: `k8s/`.

### Documentación con Doxygen

El proyecto incluye un archivo:

```text
Doxyfile
```

Para generar la documentación:

```text
doxygen Doxyfile
```

El resultado se genera en:

```text
docs/generated/html/
```

La página principal puede abrirse desde:

```text
docs/generated/html/index.html
```
