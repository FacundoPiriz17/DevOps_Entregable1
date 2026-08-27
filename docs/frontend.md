# Arquitectura del frontend {#frontend_overview}

El frontend utiliza Next.js con App Router y JavaScript. El código documentable se encuentra en `frontend/src`.

## Organización actual

- `app/layout.js`: estructura raíz, metadatos y fuentes compartidas.
- `app/page.js`: página pública inicial.
- `app/globals.css`: estilos globales; no forma parte del análisis semántico de Doxygen.

Al incorporar componentes, hooks o clientes HTTP, deben ubicarse en módulos con responsabilidades claras y documentar únicamente su contrato, restricciones o efectos secundarios relevantes.

## Comentarios JavaScript

Doxygen procesa los archivos `.js` con su analizador JavaScript y reconoce comentarios compatibles con JSDoc. Los componentes exportados deben documentar sus propiedades y su valor de retorno cuando no sean evidentes.

Las dependencias instaladas y la salida de Next.js (`node_modules` y `.next`) están excluidas deliberadamente de la generación.
