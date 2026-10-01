# VetTurno

API REST para la agenda digital de la **Veterinaria Huellitas** (proyecto del Taller evaluativo — Módulo 3).

> Nota: este README es un borrador. La historia completa, configuración, endpoints, roles y la matriz de pruebas se organizan en la **Parte 7** del taller.

## Decisiones de diseño

### Relación Propietario ↔ Mascotas: unidireccional

**Decisión:** la relación se modela únicamente desde `Mascota` hacia `Propietario` (`@ManyToOne` con llave foránea `propietario_id` en la tabla `mascotas`). `Propietario` **no** mantiene una colección `List<Mascota>`.

**Justificación:**

- **El contrato de la API no lo requiere.** Ningún endpoint obligatorio necesita navegar desde el responsable hacia sus mascotas: `GET /api/mascotas` lista todas las pacientes y, en cada una, `MascotaDTO` ya incluye el nombre del propietario. La información se obtiene del lado que sí tiene la referencia.
- **La llave foránea vive en un solo lugar.** Con relación unidireccional, el estado se almacena únicamente en `mascotas.propietario_id`; no hay dos colecciones que mantener sincronizadas ni ambigüedad sobre cuál es el dueño de la relación (ese rol lo cumple `Mascota`).
- **Se evita cargar datos que no se usan.** Una colección en `Propietario` se traería (o se diferiría) en cada consulta de responsables sin aportar al flujo de recepción, que nunca la necesita.
- **Se previene la recursión JSON.** El ciclo `Propietario → mascotas → Propietario` es el clásico origen de `HttpMessageNotWritableException`; al no existir el lado inverso, esas respuestas planas se logran sin trabajo adicional.
- **Camino de evolución abierto.** Si más adelante fuera necesario listar las mascotas de un responsable, se resuelve con una consulta derivada en `MascotaRepository` (`findByPropietarioId(Long id)`) sin exponer colecciones en la entidad.
