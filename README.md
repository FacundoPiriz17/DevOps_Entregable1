# DevOps_Entregable1

Proyecto académico de una tienda de videojuegos con backend Spring Boot y frontend Next.js.

## Documentación técnica

La documentación interna del backend y del frontend se genera con Doxygen. Swagger/OpenAPI continúa siendo la fuente de verdad para el contrato HTTP de la API.

Requisitos:

- Doxygen disponible en el `PATH`.
- PowerShell 7 en Windows o un shell compatible con POSIX en Linux/macOS.

Generar la documentación en Windows:

```powershell
.\scripts\generate-docs.ps1
```

Generarla en Linux o macOS:

```bash
sh scripts/generate-docs.sh
```

La página inicial se crea en `docs/generated/html/index.html`. Este directorio es un artefacto generado y no se versiona.

La guía para escribir comentarios y ampliar la documentación está en [`docs/documentation-guide.md`](docs/documentation-guide.md).
