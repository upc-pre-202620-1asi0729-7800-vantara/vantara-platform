# vantara-platform

Backend de **Vantara / Hatarium**, la plataforma de gestión ganadera.

Proyecto Spring Boot organizado por *bounded contexts* y capas DDD
(`domain`, `application`, `infrastructure`, `interfaces`). Esta rama contiene
únicamente el shared kernel: no incluye bounded contexts ni pruebas.

## Stack

- Java 27
- Spring Boot 4.1.2 (Web MVC, Data JPA, Validation)
- MySQL 8 en desarrollo/producción
- springdoc-openapi / Swagger UI
- Maven

## Requisitos

- JDK 27
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

## Estructura base

```text
src/main/java/com/vantara/platform/
├── VantaraPlatformApplication.java
├── shared/
│   ├── application/result/                          # Result<T, E> y ApplicationError
│   ├── domain/model/aggregates/                     # AbstractDomainAggregateRoot<T>
│   ├── domain/model/events/                         # contrato DomainEvent
│   ├── infrastructure/documentation/openapi/configuration/   # OpenApiConfiguration
│   ├── infrastructure/i18n/configuration/           # LocaleConfiguration (Accept-Language)
│   ├── infrastructure/persistence/jpa/configuration/strategy/ # naming snake_case + tablas en plural
│   ├── infrastructure/persistence/jpa/entities/     # AuditableAbstractPersistenceEntity
│   ├── infrastructure/time/configuration/           # Clock
│   └── interfaces/rest/                             # GlobalExceptionHandler, resources/, transform/
└── iam/                                             # bounded context Identity and Access Management
    └── domain/                                      # (capa en curso) ver la estructura siguiente
```

Los bounded contexts se agregan uno por uno en ramas `feature/<contexto>`.
Cada bounded context sigue esta estructura (la de la referencia de Efrain):

```text
<context>/
├── domain/
│   ├── exceptions/
│   ├── model/{aggregates,entities,valueobjects,commands,queries,events}/
│   └── repositories/
├── application/
│   ├── commandservices/        # contratos de escritura (CQRS)
│   ├── queryservices/          # contratos de lectura (CQRS)
│   └── internal/{commandservices,queryservices,eventhandlers,outboundservices/<puerto>}/
├── infrastructure/
│   └── persistence/jpa/{adapters,assemblers,converters,embeddables,entities,repositories}/
└── interfaces/
    ├── acl/                    # fachadas hacia otros bounded contexts
    └── rest/{resources,transform}/
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
