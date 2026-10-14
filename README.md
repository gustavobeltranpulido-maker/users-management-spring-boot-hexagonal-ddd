# Users Management — Spring Boot, Arquitectura Hexagonal y DDD

Aplicación de gestión de usuarios construida con Java 17 y Spring Boot. La API REST es el punto de entrada activo. El código de la antigua CLI se conserva como adaptador inactivo y no posee un contenedor de dependencias independiente.

Spring es el único *composition root*: `Main` inicia el contexto y las dependencias se resuelven mediante configuración y component scanning de Spring.

## Verificación

```bash
./mvnw clean test
./mvnw clean package
```

En Windows se puede utilizar `mvnw.cmd`.

## Secretos para desarrollo local

Copiar `application-secrets.properties.example` como
`.environments/application-secrets.properties` y reemplazar los valores de ejemplo.
Este archivo contiene la conexion a PostgreSQL, la semilla JWT y, opcionalmente, las
credenciales de Kafka. La carpeta `.environments` esta excluida de Git y del contexto
de Docker.

En produccion no se utiliza este archivo: los secretos deben configurarse mediante las
variables de entorno `DB_*`, `JWT_SECRET` y `KAFKA_*` del proveedor de despliegue.

## Docker Compose local

Copiar `compose.env.example` como `.env`, reemplazar todos los valores y ejecutar:

```bash
docker compose up --build
```

Compose se detiene con un error si falta `DB_PASSWORD` o `JWT_SECRET`. El archivo
`.env` esta excluido de Git y del contexto de construccion Docker. Los puertos de
PostgreSQL y la API se publican solo en `127.0.0.1`.

## Despliegue en Render

El archivo `render.yaml` define el servicio web, el build con Docker y el despliegue
automatico de cada commit que llegue a la rama `main`.

1. Crear un Blueprint en Render y seleccionar este repositorio.
2. Completar en el panel los secretos marcados como requeridos: `DB_HOST`,
   `DB_USERNAME`, `DB_PASSWORD`, `KAFKA_BOOTSTRAP_SERVERS`, `KAFKA_USERNAME`,
   `KAFKA_PASSWORD` y `KAFKA_CA_CERT`.
3. La base de datos es PostgreSQL en Supabase. Usar el **Session pooler** (IPv4,
   puerto 5432) con `DB_SSL_MODE=require` y ejecutar `src/main/resources/schema.sql`
   una vez en el SQL Editor para crear el esquema.
4. Desplegar el Blueprint. La API quedara disponible en el subdominio
   `onrender.com` asignado por Render y Swagger UI en `/swagger-ui.html`.

Las credenciales nunca deben guardarse en `application.properties` ni en
`render.yaml`.

## Cuenta por defecto

Al crear un usuario se inserta tambien su cuenta `SAVINGS` (estado `ACTIVE`) en la
tabla `accounts`, dentro de la misma transaccion: si alguna de las dos inserciones
falla, no se guarda ninguna. Si el usuario ya tiene una cuenta de ese tipo, no se
duplica (`UNIQUE (user_id, type)`).

## Eventos de dominio (Kafka)

Este servicio no envia correos. Tras crear, actualizar o eliminar un usuario publica
un evento en el topico `KAFKA_TOPIC_USER_EVENTS` (Aiven Kafka, `SASL_SSL` +
`SCRAM-SHA-256`). El microservicio de notificaciones consume esos eventos y envia el
correo correspondiente.

Cada mensaje usa el id del usuario como clave y este formato JSON:

```json
{
  "eventName": "user.created",
  "occurredOn": "2026-10-13T21:49:33.123",
  "payload": { "id": "u-01", "name": "...", "email": "...", "role": "ADMIN", "status": "PENDING" }
}
```

Eventos: `user.created`, `user.updated` (mismo payload) y `user.deleted` (solo `id`).
La publicacion es asincrona: si el broker no responde, el caso de uso termina igual y
el fallo queda registrado en el log. Con `KAFKA_ENABLED=false` (valor por defecto) los
eventos se omiten, lo que permite ejecutar la aplicacion en local sin broker.
