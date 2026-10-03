# VetTurno

API REST para la agenda digital de la **Veterinaria Huellitas** (proyecto del Taller evaluativo — Módulo 3).

**Repositorio:** https://github.com/JuanezAC/VetTurno

## Historia

La Veterinaria Huellitas agenda las citas de sus pacientes por teléfono y cuaderno. Cuando la recepción se llena, se pierden llamadas, se duplican horarios y no hay forma de saber qué mascota vino la última vez. **Doña Marta**, dueña de la clínica, pidió un sistema sencillo para registrar propietarios, sus mascotas, el equipo veterinario y la agenda de citas, sin perder la regla más importante: **un veterinario no puede tener dos citas al mismo tiempo** y toda cita debe ser a futuro.

VetTurno es el MVP de ese pedido: una API organizada por capas, con autenticación por roles y documentación interactiva. No incluye interfaz gráfica; Swagger UI es la vitrina y la mesa de recepción.

## Alcance

| Incluido en el MVP | Fuera del alcance |
|---|---|
| Registro e inicio de sesión de usuarios | Interfaz web o móvil |
| Propietarios, mascotas y veterinarios (alta y consulta) | Agenda de horarios repetitivos (recurrencia) |
| Agenda de citas con fecha futura y sin horario duplicado | Notificaciones, correo o SMS |
| Filtro de citas por veterinario | Historial clínico y facturación |
| Roles USER/ADMIN y rutas protegidas con JWT | Despliegue en la nube (Docker es actividad extra) |

## Tecnologías

| Componente | Versión / detalle |
|---|---|
| Java | 17 (compilado y probado con JDK 25) |
| Spring Boot | 4.1.1 (WebMVC, Data JPA, Security, Validation) |
| Hibernate / JPA | Gestión del esquema con `ddl-auto=update` |
| MySQL | 8.0, base de datos `vetturno` |
| JWT | jjwt 0.13.0, algoritmo HS384, expiración 1 hora |
| Documentación | springdoc-openapi 3.1.0 (OpenAPI + Swagger UI) |
| Contraseñas | BCrypt (nunca se almacenan ni se transmiten en claro) |

## Arquitectura

Patrón en tres capas con DTO de entrada y salida: el **controller** recibe y responde, el **service** aplica las reglas de negocio, el **repository** persiste en MySQL. Las dependencias se inyectan por constructor.

```
src/main/java/com/devsenior/VetTurno/
├── config/        OpenApiConfig (identidad de la API y esquema Bearer)
├── controller/    Auth, Propietario, Mascota, Veterinario, Cita
├── service/       AuthService, PropietarioService, MascotaService,
│                  VeterinarioService, CitaService (reglas de agenda)
├── repository/    JpaRepository de cada entidad
├── model/         Propietario, Mascota, Veterinario, Cita, Usuario
├── dto/           Entrada (Request) y salida (DTO) sin exponer entidades
├── security/      SecurityConfig, JwtService, JwtAuthFilter, UsuarioDetailsService
├── exception/     NegocioException, EmailYaRegistradoException, ApiError,
│                  GlobalExceptionHandler
└── config/        OpenApiConfig
```

## Modelo de datos

| Entidad | Campos clave | Relación |
|---|---|---|
| `Propietario` | nombre, teléfono, email | Uno a muchas con `Mascota` |
| `Mascota` | nombre, especie, raza | `@ManyToOne` → `Propietario` (FK `propietario_id`) |
| `Veterinario` | nombre, especialidad | Uno a muchas con `Cita` |
| `Cita` | fechaHora, motivo | `@ManyToOne` → `Mascota` y `@ManyToOne` → `Veterinario` |
| `Usuario` | email, password (BCrypt), rol `USER`/`ADMIN` | Referencia lógica en el JWT (el rol se lee de la BD) |

Llaves foráneas gestionadas por Hibernate. La relación Propietario ↔ Mascotas es **unidireccional** (ver [Decisiones de diseño](#decisiones-de-diseño)).

## Configuración

### Requisitos

1. JDK 17 o superior.
2. MySQL 8 corriendo en `localhost:3306`.
3. Git (el proyecto incluye Maven Wrapper, no requiere instalar Maven).

### Base de datos

```sql
CREATE DATABASE vetturno;
```

Hibernate crea las tablas automáticamente al iniciar (`spring.jpa.hibernate.ddl-auto=update`).

### Archivo de configuración

Copie la plantilla y complete sus valores reales:

```bash
cp application.properties.example src/main/resources/application.properties
```

| Variable | Descripción |
|---|---|
| `spring.datasource.username` / `password` | Credenciales de MySQL locales |
| `jwt.secret` | Clave secreta en Base64 (generar con `openssl rand -base64 48`) |
| `jwt.expiracion-ms` | Vigencia del token (3600000 = 1 hora) |

> ⚠️ `application.properties` está en `.gitignore`: **nunca se publica**. El repositorio solo contiene la plantilla `application.properties.example` sin valores reales.

### Cómo ejecutar

```bash
./mvnw.cmd spring-boot:run    # Windows (o ./mvnw spring-boot:run en Linux/macOS)
```

Espere el mensaje `Started VetTurnoApplication`. Luego abra:

- Swagger UI: http://localhost:8080/swagger-ui.html
- Contrato OpenAPI: http://localhost:8080/v3/api-docs

## Endpoints

| Método | Ruta | Acceso | Éxito | Descripción |
|---|---|---|---|---|
| POST | `/api/auth/register` | Público | 200 | Registra usuario con rol `USER` y devuelve token |
| POST | `/api/auth/login` | Público | 200 | Devuelve token JWT |
| GET | `/api/propietarios` | Cualquier usuario | 200 | Lista propietarios |
| POST | `/api/propietarios` | Cualquier usuario | 201 | Crea propietario (nombre y teléfono obligatorios; email con formato válido) |
| GET | `/api/mascotas` | Cualquier usuario | 200 | Lista mascotas con el nombre del propietario |
| POST | `/api/mascotas` | Cualquier usuario | 201 | Crea mascota; el propietario debe existir |
| GET | `/api/veterinarios` | Cualquier usuario | 200 | Lista veterinarios |
| POST | `/api/veterinarios` | **Solo ADMIN** | 201 | Da de alta un veterinario |
| GET | `/api/citas` | Cualquier usuario | 200 | Lista todas las citas |
| GET | `/api/citas/veterinario/{id}` | Cualquier usuario | 200 | Filtra citas por veterinario |
| POST | `/api/citas` | Cualquier usuario | 201 | Agenda cita futura; rechaza horario duplicado |

### Ejemplo de solicitud y respuesta

```http
POST /api/citas
Authorization: Bearer <token>
Content-Type: application/json

{
  "fechaHora": "2027-06-15T11:00:00",
  "motivo": "Vacunación anual",
  "mascotaId": 1,
  "veterinarioId": 1
}
```

```json
{
  "id": 4,
  "fechaHora": "2027-06-15T11:00:00",
  "motivo": "Vacunación anual",
  "mascota": { "id": 1, "nombre": "Luna" },
  "propietario": { "id": 1, "nombre": "Marta Ruiz" },
  "veterinario": { "id": 1, "nombre": "Dr. Andres" }
}
```

## Roles y seguridad

| Ruta | USER | ADMIN |
|---|---|---|
| Lectura (GET) de propietarios, mascotas, veterinarios y citas | ✔ | ✔ |
| Crear propietarios, mascotas y citas (POST) | ✔ | ✔ |
| Crear veterinarios (POST) | ✘ 403 | ✔ 201 |
| Registro y login | ✔ (públicos) | ✔ (públicos) |

- **Registro:** crea usuarios con rol `USER` y contraseña codificada con BCrypt.
- **Para probar ADMIN:** registre un usuario y promuévalo directamente en MySQL:
  `UPDATE usuarios SET rol='ADMIN' WHERE email='<su-email>';`
- **Login:** devuelve un JWT (HS384, 1 hora) con el email como sujeto.
- **Protección:** todas las rutas exigen `Authorization: Bearer <token>` salvo `/api/auth/**` y las rutas técnicas de Swagger. Sesiones **stateless**: cada petición lleva su token y Spring Security lo valida en `JwtAuthFilter`.
- **Sin token** → `401` · **token con rol insuficiente** → `403`.

### Orden del flujo en Swagger

1. Abra http://localhost:8080/swagger-ui.html (sin iniciar sesión en la app).
2. Pulse **Authorize**, escriba `Bearer <token>` (o solo el token) y confirme.
3. Registre un usuario en `POST /api/auth/register`, inicie sesión en `POST /api/auth/login` y copie el `token`.
4. Pruebe `GET /api/citas` (debe responder 200) y luego `POST /api/veterinarios` según su rol.

## Errores y respuestas

Todos los errores responden en el formato `ApiError`:

```json
{
  "status": 400,
  "mensaje": "Los datos enviados no son válidos",
  "errores": { "email": "El email no tiene un formato válido" },
  "timestamp": "2026-10-02T20:20:49.093261300"
}
```

| Código | Cuándo ocurre |
|---|---|
| 400 | Validación de campos (`errores` detalla cada campo), fecha no futura, horario duplicado o referencia inexistente |
| 401 | Sin token o credenciales inválidas |
| 403 | Rol insuficiente (POST `/api/veterinarios` con USER) |
| 404 | Ruta inexistente |
| 409 | Registro con email ya existente |
| 500 | Error imprevisto con mensaje genérico, **sin trazas ni nombres internos** |

### Errores frecuentes

| Síntoma | Causa y solución |
|---|---|
| La app no inicia y menciona acceso denegado | Credenciales de MySQL incorrectas en `application.properties` |
| `Address already in use: 8080` | Otro proceso usa el puerto 8080 (o quedó una instancia anterior) |
| Swagger responde 401 | Falta pulsar **Authorize** y pegar el token |
| 403 al crear veterinario | El usuario tiene rol `USER`; promuévalo a `ADMIN` en MySQL |
| 400 "La fecha de la cita debe ser futura" | Se envió una fecha pasada o actual |
| 400 "El veterinario ya tiene una cita agendada en ese horario" | Ese veterinario ya tiene cita a esa hora exacta |

## Matriz de pruebas manuales

Resultados reales obtenidos el **2026-10-02** contra `http://localhost:8080`. La salida cruda (con cuerpos JSON, estados HTTP y consultas SQL de verificación) está en [`docs/evidencias/matriz-15-pruebas.txt`](docs/evidencias/matriz-15-pruebas.txt).

| # | Escenario | Resultado esperado | Resultado real | Estado |
|---|---|---|---|---|
| 1 | La aplicación inicia con MySQL disponible | Servidor activo y esquema accesible | `Started VetTurnoApplication in 5.518 s`; 5 tablas (`citas`, `mascotas`, `propietarios`, `usuarios`, `veterinarios`) | ✅ 200 |
| 2 | Registro válido de Paula | 200 y token; contraseña hasheada | 200 + token JWT; en BD: `$2a$10$mKQAL...` (BCrypt, irreversible) | ✅ 200 |
| 3 | Registro con email inválido y clave corta | 400 con errores por campo | 400 con `errores.email` y `errores.password` | ✅ 400 |
| 4 | Login con credenciales válidas | 200 y JWT vigente | 200 + token HS384 con expiración de 1 hora | ✅ 200 |
| 5 | GET `/api/citas` sin token | Acceso rechazado | 401 `Autenticacion requerida...` | ✅ 401 |
| 6 | POST `/api/veterinarios` con USER | 403 Forbidden | 403 `Acceso denegado...` | ✅ 403 |
| 7 | POST `/api/veterinarios` con ADMIN | 201 y veterinario persistido | 201 (`Dra. Sofia`, id 7) y fila confirmada con SQL | ✅ 201 |
| 8 | Creación válida de propietario | 201 y DTO sin colecciones anidadas | 201 plano `{"id":3,"nombre":"Ana Gomez","telefono":"300555","email":"..."}` | ✅ 201 |
| 9 | Mascota con propietario existente | 201 y relación correcta | 201 con `propietarioId:3` y `propietarioNombre:"Ana Gomez"` | ✅ 201 |
| 10 | Mascota con propietario inexistente | 400 controlado; no se inserta fila | 400 `Propietario no encontrado con id 999`; filas `Rocky`: 0 antes y 0 después | ✅ 400 |
| 11 | Cita futura con referencias válidas | 201 y cita persistida | 201 (id 4) y fila `2027-06-15 11:00:00` confirmada con SQL | ✅ 201 |
| 12 | Cita con fecha pasada | 400 con mensaje claro | 400 `La fecha de la cita debe ser futura` | ✅ 400 |
| 13 | Segundo intento con mismo veterinario y horario | 400; se conserva una sola cita | 400 `El veterinario ya tiene una cita agendada en ese horario`; COUNT = 1 | ✅ 400 |
| 14 | Filtro de citas por veterinario | 200 y solo coincidencias | 200 con 4 citas, todas del Dr. Andres (veterinario 1) | ✅ 200 |
| 15 | Reinicio y prueba desde Swagger con Authorize | Datos persisten y el flujo protegido funciona | Tras reiniciar: 200 con las mismas 4 citas y 2 usuarios en BD | ✅ 200 |

## Evidencias

Capturas en [`docs/evidencias/`](docs/evidencias/) con nombres descriptivos:

| Archivo | Qué muestra |
|---|---|
| `01-swagger-autorize.png` | Swagger UI con el título **VetTurno**, su descripción y el botón **Authorize** |
| `02-swagger-flujo.png` | `POST /api/auth/login` ejecutado desde Swagger con respuesta 200 y token |
| `03-400-multicampo.png` | Respuesta 400 con varios errores de campo (formato `ApiError`) — matriz #3 |
| `04-403-user-veterinarios.png` | POST `/api/veterinarios` con rol USER → 403 Forbidden — matriz #6 |
| `05-mysql-tablas.png` | Esquema `vetturno` con las 5 tablas — matriz #1 |
| `06-github-readme.png` | Repositorio publicado con este README visible |
| `07-citas-sin-token.png` | `GET /api/citas` sin token → 401 — matriz #5 |
| `08-registro-valido.png` | Registro válido con respuesta 200 y token — matriz #2 |
| `09-hash-bcrypt.png` | Contraseña guardada como hash BCrypt (`$2a$10$...`) en MySQL — matriz #2 |
| `10-propietario-201.png` | Creación válida de propietario → 201 con DTO plano — matriz #8 |
| `11-mascota-relacion.png` | Mascota con propietario existente → 201 con relación — matriz #9 |
| `12-mascota-400.png` | Mascota con propietario inexistente → 400 controlado — matriz #10 |
| `13-cita-201.png` | Cita futura con referencias válidas → 201 persistida — matriz #11 |
| `14-cita-duplicada-400.png` | Mismo veterinario y horario → 400 por duplicado — matriz #13 |
| `15-cita-pasada-400.png` | Cita con fecha pasada → 400 — matriz #12 |
| `16-filtro-vet.png` | Filtro de citas por veterinario → 200 con coincidencias — matriz #14 |
| `17-vet-admin-201.png` | POST `/api/veterinarios` con ADMIN → 201 — matriz #7 |
| `matriz-15-pruebas.txt` | Salida cruda de las 15 pruebas con estados HTTP y SQL de verificación |

> Las capturas `07` a `17` completan la matriz de pruebas; la prueba #15 (reinicio con datos persistentes) queda documentada en `matriz-15-pruebas.txt` y en la sección de resultados de arriba.

Cada captura describe con texto qué se observa; los mensajes de error no dependen del color y nunca aparecen contraseñas ni tokens completos.

## Nota sobre uso de IA

El asistente de IA se usó para **comprender y diagnosticar**, no para copiar soluciones: analogías de las capas controller/service/repository, revisión de cardinalidades y DTO, hipótesis sobre recursión JSON, contraste de las reglas de fecha y horario, auditoría de rutas y roles frente a la regla USER/ADMIN, y revisión de validaciones ausentes. Cada sugerencia se **verificó contra el comportamiento real de VetTurno** ejecutando las pruebas de esta matriz; los errores encontrados (por ejemplo, el 403 que se convertía en 401) se corrigieron y se documentaron con la respuesta HTTP real.

## Historial en Git

Cada parte del taller corresponde a un commit verificable en el repositorio:

| Commit | Contenido |
|---|---|
| [`a059b98`](https://github.com/JuanezAC/VetTurno/commit/a059b98) | Base del taller y proyecto inicial |
| [`59ee195`](https://github.com/JuanezAC/VetTurno/commit/59ee195) | Parte 1: proyecto Maven con Java 17, dependencias y paquetes |
| [`a6190d3`](https://github.com/JuanezAC/VetTurno/commit/a6190d3) | Parte 2: entidades JPA y repositorios |
| [`68ebcc6`](https://github.com/JuanezAC/VetTurno/commit/68ebcc6) | Parte 3: flujo REST con DTO, services y controllers |
| [`b40312e`](https://github.com/JuanezAC/VetTurno/commit/b40312e) | Parte 4: agenda de citas con fecha futura y horario duplicado |
| [`0a3159b`](https://github.com/JuanezAC/VetTurno/commit/0a3159b) | Parte 5: autenticación JWT con roles USER/ADMIN |
| [`ddc8cf8`](https://github.com/JuanezAC/VetTurno/commit/ddc8cf8) | Parte 6: validaciones y manejador global de errores |
| [`9b995cb`](https://github.com/JuanezAC/VetTurno/commit/9b995cb) | Parte 7: Swagger, matriz de pruebas y evidencias |

## Decisiones de diseño

### Relación Propietario ↔ Mascotas: unidireccional

**Decisión:** la relación se modela únicamente desde `Mascota` hacia `Propietario` (`@ManyToOne` con llave foránea `propietario_id` en la tabla `mascotas`). `Propietario` **no** mantiene una colección `List<Mascota>`.

**Justificación:**

- **El contrato de la API no lo requiere.** Ningún endpoint obligatorio necesita navegar desde el responsable hacia sus mascotas: `GET /api/mascotas` lista todas las pacientes y, en cada una, `MascotaDTO` ya incluye el nombre del propietario. La información se obtiene del lado que sí tiene la referencia.
- **La llave foránea vive en un solo lugar.** Con relación unidireccional, el estado se almacena únicamente en `mascotas.propietario_id`; no hay dos colecciones que mantener sincronizadas ni ambigüedad sobre cuál es el dueño de la relación (ese rol lo cumple `Mascota`).
- **Se evita cargar datos que no se usan.** Una colección en `Propietario` se traería (o se diferiría) en cada consulta de responsables sin aportar al flujo de recepción, que nunca la necesita.
- **Se previene la recursión JSON.** El ciclo `Propietario → mascotas → Propietario` es el clásico origen de `HttpMessageNotWritableException`; al no existir el lado inverso, esas respuestas planas se logran sin trabajo adicional.
- **Camino de evolución abierto.** Si más adelante fuera necesario listar las mascotas de un responsable, se resuelve con una consulta derivada en `MascotaRepository` (`findByPropietarioId(Long id)`) sin exponer colecciones en la entidad.

### Reglas de agenda en el service

La fecha futura y la prevención de horario duplicado viven en `CitaService`, no en el controller ni en el repository: son reglas de negocio del dominio "veterinaria", aplican a cualquier llamador futuro (otro controller, un job) y se centralizan en un solo lugar testeable. El repository solo expone la consulta derivada `findByVeterinarioIdAndFechaHora` que la regla necesita.

### Errores centralizados en un manejador global

`GlobalExceptionHandler` traduce validaciones (`400`), reglas de negocio (`400`), email duplicado (`409`), credenciales (`401`) e imprevistos (`500` genérico) al mismo formato `ApiError`. Las respuestas 401/403 de **seguridad** no pasan por ahí: las produce la cadena de filtros de Spring Security antes del controller, por eso el manejador global **no reemplaza** las reglas de seguridad.
