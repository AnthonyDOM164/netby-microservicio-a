# Netby Microservicio A - Orquestador de Credito

Microservicio Quarkus encargado de recibir solicitudes de evaluacion de credito,
validar la cedula ecuatoriana, consultar el servicio de riesgos mock
(`netby-microservicio-b`) por gRPC, aplicar la regla de negocio y persistir el
resultado en PostgreSQL.

## Estado en nube

El servicio publicado para revision esta disponible en:

```txt
http://217.216.55.37:8080
```

Swagger UI:

```txt
http://217.216.55.37:8080/q/swagger-ui
```

## Arquitectura

El proyecto mantiene separacion por capas:

- `domain`: reglas de negocio, modelos y validacion de cedula.
- `application`: casos de uso y puertos de entrada/salida.
- `infrastructure/rest`: API REST expuesta al frontend.
- `infrastructure/grpc`: clientes gRPC hacia el microservicio B.
- `infrastructure/persistence`: entidades y repositorios con Hibernate/Panache.

La comunicacion frontend -> A es REST. La comunicacion A -> B usa gRPC porque
los microservicios tienen contratos internos estables, bajo acoplamiento al
frontend y llamadas punto a punto con payloads pequenos. REST queda reservado
para el borde publico consumido por la UI.

## Regla de negocio

Una evaluacion queda `APROBADO` cuando:

```txt
score > 70 && (deudaMensual + montoSolicitado) < salarioMensual * 0.40
```

En cualquier otro caso queda `RECHAZADO`.

## Endpoints REST

### Crear evaluacion

```http
POST /v1/credit-evaluations
Content-Type: application/json
```

Body:

```json
{
  "cedula": "0102030405",
  "montoSolicitado": 1000,
  "tiempoAnios": 2,
  "salarioMensual": 4000
}
```

Respuesta esperada:

```json
{
  "id": "uuid",
  "cedula": "0102030405",
  "montoSolicitado": 1000,
  "evaluatedAt": "2026-07-08T00:00:00Z",
  "status": "APROBADO"
}
```

### Listar evaluaciones recientes

```http
GET /v1/credit-evaluations?limit=12
```

## Requisitos

- Java 21.
- Docker y Docker Compose para PostgreSQL o despliegue local en contenedores.
- Microservicio B ejecutandose en gRPC puerto `9000`.
- Maven Wrapper incluido en el repositorio.

Si `./mvnw` no ejecuta en Linux/macOS, corregir una sola vez:

```bash
chmod +x mvnw
```

## Variables de entorno

No se deben subir valores sensibles al repositorio. Configurar las variables en
la maquina, servidor o proveedor de despliegue.

| Variable | Uso | Ejemplo local |
| --- | --- | --- |
| `DB_JDBC_URL` | URL JDBC de PostgreSQL | `jdbc:postgresql://localhost:5432/credit_evaluations` |
| `DB_USERNAME` | Usuario de PostgreSQL | `credit_user` |
| `DB_PASSWORD` | Password de PostgreSQL | `change-me-local-only` |
| `RISK_SCORE_GRPC_HOST` | Host del servicio Score de B | `localhost` |
| `RISK_SCORE_GRPC_PORT` | Puerto gRPC del servicio Score | `9000` |
| `RISK_DEUDAS_GRPC_HOST` | Host del servicio Deudas de B | `localhost` |
| `RISK_DEUDAS_GRPC_PORT` | Puerto gRPC del servicio Deudas | `9000` |
| `CORS_ALLOWED_ORIGINS` | Origenes permitidos para la UI | `http://localhost:4200,http://localhost:8088` |

## Ejecucion local sin Docker

1. Levantar PostgreSQL local.
2. Crear la base `credit_evaluations`.
3. Levantar `netby-microservicio-b` en el puerto gRPC `9000`.
4. Exportar variables:

```bash
export DB_JDBC_URL=jdbc:postgresql://localhost:5432/credit_evaluations
export DB_USERNAME=credit_user
export DB_PASSWORD=change-me-local-only
export RISK_SCORE_GRPC_HOST=localhost
export RISK_SCORE_GRPC_PORT=9000
export RISK_DEUDAS_GRPC_HOST=localhost
export RISK_DEUDAS_GRPC_PORT=9000
export CORS_ALLOWED_ORIGINS=http://localhost:4200,http://localhost:8088
```

5. Ejecutar:

```bash
./mvnw quarkus:dev
```

## Ejecucion con Docker Compose

Este repositorio levanta PostgreSQL y el microservicio A. El microservicio B debe
estar ejecutandose antes en el host o en otro contenedor con el puerto `9000`
publicado.

```bash
docker compose up -d --build
```

Servicios por defecto:

- API A: `http://localhost:8080`
- PostgreSQL: `localhost:5432`

Para detener:

```bash
docker compose down
```

## Prueba rapida

```bash
curl -X POST http://localhost:8080/v1/credit-evaluations \
  -H "Content-Type: application/json" \
  -d '{
    "cedula": "0102030405",
    "montoSolicitado": 1000,
    "tiempoAnios": 2,
    "salarioMensual": 4000
  }'
```

Listar las ultimas evaluaciones:

```bash
curl "http://localhost:8080/v1/credit-evaluations?limit=12"
```

## Seguridad y persistencia

- La cedula se valida con algoritmo Modulo 10.
- Los montos y plazo se validan con Bean Validation.
- La persistencia usa Hibernate/Panache y consultas parametrizadas.
- No se concatenan queries SQL con datos ingresados por el usuario.
- Flyway crea y valida el esquema en PostgreSQL.

## Build

```bash
./mvnw -DskipTests package
```

El artefacto queda en:

```txt
target/quarkus-app/quarkus-run.jar
```
