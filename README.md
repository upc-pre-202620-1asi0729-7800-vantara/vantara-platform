# vantara-platform

Backend de **Vantara / Hatarium**, la plataforma de gestión ganadera.

Proyecto Spring Boot organizado por *bounded contexts* y capas DDD
(`domain`, `application`, `infrastructure`, `interfaces`). Esta rama contiene
únicamente el shared kernel: no incluye bounded contexts ni pruebas.

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

## Estructura base

```text
src/main/java/com/vantara/platform/
├── VantaraPlatformApplication.java
└── shared/
    ├── application/result/              # Result<T, E> para resultados de casos de uso
    ├── domain/aggregates/               # raíz de agregados con eventos de dominio
    ├── domain/events/                   # contrato DomainEvent
    ├── infrastructure/configuration/    # OpenAPI y Clock
    ├── infrastructure/persistence/jpa/  # naming strategy snake_case + plural
    └── interfaces/rest/                 # exception handler global
```

Esta rama contiene únicamente el shared kernel. Los bounded contexts y sus
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
