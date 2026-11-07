# vantara-platform

Backend de **Vantara / Hatarium**, la plataforma de gestión ganadera.

Proyecto Spring Boot organizado por *bounded contexts* y capas DDD
(`domain`, `application`, `infrastructure`, `interfaces`). La base en `main` y
`develop` contiene únicamente el shared kernel. Esta rama `feature/payments`
agrega el bounded context de pagos y sus pruebas.

## Stack

- Java 21 (se puede compilar con un JDK superior usando `--release 21`)
- Spring Boot 3.5.7 (Web, Data JPA, Validation)
- MySQL 8 en desarrollo/producción
- springdoc-openapi / Swagger UI
- Maven

## Requisitos

- JDK 21 o superior
- Maven 3.9+ **o** usar el wrapper incluido (`mvnw` / `mvnw.cmd`)
- MySQL 8 para ejecutar con el perfil `dev`

## Ejecutar en local

1. Crear la base de datos (o dejar que `createDatabaseIfNotExist=true` la cree):

   ```sql
   CREATE DATABASE vantara_platform;
   ```

2. Configurar credenciales mediante variables de entorno (no se versionan secretos):

   ```powershell
   $env:DATABASE_URL="jdbc:mysql://localhost:3306/vantara_platform?createDatabaseIfNotExist=true&allowPublicKeyRetrieval=true&useSSL=false"
   $env:DATABASE_USER="root"
   $env:DATABASE_PASSWORD="tu_password"
   ```

3. Levantar la aplicación:

   ```powershell
   .\mvnw.cmd spring-boot:run
   ```

   Por defecto usa el perfil `dev` y el puerto `8080`.

## Swagger / OpenAPI

Con la aplicación levantada:

- Swagger UI: <http://localhost:8080/swagger-ui.html>
- OpenAPI JSON: <http://localhost:8080/v3/api-docs>

## Payments

Esta rama agrega el bounded context de pagos:

- Checkout compartido para suscripciones y citas veterinarias.
- Pago simulado como `paid` con `FakePay`.
- Comprobantes en PDF generados automáticamente con OpenPDF.
- Método de pago no sensible: marca, últimos cuatro dígitos y titular.

Endpoints principales:

- `GET/POST /api/v1/payments`
- `GET /api/v1/payments/{id}`
- `GET /api/v1/transactions?paymentId=`
- `GET /api/v1/receipts?paymentId=`
- `GET /api/v1/receipts/{id}/document`

Ver [docs/payments.md](docs/payments.md) para el contrato, los ejemplos y el
modelo de datos.

## Estructura base

```text
src/main/java/com/vantara/platform/
├── VantaraPlatformApplication.java
├── shared/
│   ├── application/result/              # Result<T, E> para resultados de casos de uso
│   ├── domain/aggregates/               # raíz de agregados con eventos de dominio
│   ├── domain/events/                   # contrato DomainEvent
│   ├── infrastructure/configuration/    # OpenAPI y Clock
│   ├── infrastructure/persistence/jpa/  # naming strategy snake_case + plural
│   └── interfaces/rest/                 # exception handler global
└── payments/
    ├── domain/model/aggregates/         # Payment
    ├── domain/model/entities/           # PaymentTransaction, PaymentReceipt
    ├── domain/model/valueobjects/       # PaymentStatus, TransactionStatus, PaymentMethod
    ├── domain/repositories/             # puertos de persistencia
    ├── application/                     # comandos, queries y puertos
    ├── infrastructure/persistence/jpa/  # entidades, mappers y adaptadores
    ├── infrastructure/payment/          # FakePaymentGateway
    ├── infrastructure/receipt/          # PDF y numeración de comprobantes
    └── interfaces/rest/                 # controladores, resources y mappers
```

La base contiene únicamente el shared kernel. Los bounded contexts y sus
pruebas se agregan en ramas `feature/<contexto>`.

Cada bounded context que se agregue debe seguir la misma estructura:

```text
<context>/
├── domain/model/aggregates
├── domain/model/valueobjects
├── domain/model/events
├── domain/repositories
├── application/commands
├── application/queries
├── application/commandservices
├── application/queryservices
├── application/internal/...
├── infrastructure/persistence/jpa/...
└── interfaces/rest/...
```

## Convenciones

- Mantener el dominio como Java puro: sin Spring, JPA ni HTTP.
- La capa `application` depende de los puertos del dominio, no de JPA.
- La capa `infrastructure` implementa los puertos con JPA y mappers propios.
- La capa `interfaces` traduce entre Resources HTTP, comandos y consultas.
- Mensajes de error localizados en `messages.properties` y `messages_es.properties`.
- Commits con [Conventional Commits](https://www.conventionalcommits.org/).
- Git Flow: `main` para releases, `develop` para integración y
  `feature/<descripcion>` para trabajo nuevo.
