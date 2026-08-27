# Convenciones de documentación {#documentation_guide}

La documentación debe explicar decisiones, contratos y restricciones. No debe repetir literalmente lo que ya expresa el código.

## Java

Utilizar comentarios Javadoc/Doxygen en clases públicas y en métodos con reglas de negocio relevantes:

```java
/**
 * Añade un videojuego activo a la biblioteca del usuario.
 *
 * @param userId identificador del usuario autenticado
 * @param gameId identificador del videojuego
 * @return entrada creada en la biblioteca
 * @throws ApiException si el juego no existe o ya pertenece a la biblioteca
 */
```

No es necesario documentar getters, setters o constructores evidentes.

## JavaScript

Usar comentarios compatibles con JSDoc en componentes y funciones reutilizables:

```javascript
/**
 * Presenta la información resumida de un videojuego.
 * @param {Object} props propiedades del componente
 * @param {string} props.name nombre visible
 * @returns {JSX.Element} tarjeta renderizada
 */
```

Antes de integrar cambios se debe comprobar que Doxygen finaliza sin errores de documentación.
