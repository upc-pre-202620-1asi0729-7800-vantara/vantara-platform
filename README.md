# vantara-platform

Backend de **Vantara / Hatarium**, la plataforma de gestión ganadera.

Proyecto Spring Boot organizado por *bounded contexts* y capas DDD
(`domain`, `application`, `infrastructure`, `interfaces`). `develop` contiene el
shared kernel y los bounded contexts **IAM** (identidad y acceso), **Profile**
(datos personales y preferencias del usuario) y **Payments** (pagos y
comprobantes), además de los demás contextos integrados desde sus ramas
`feature/<contexto>`.

## Stack

- Java 27
- Spring Boot 4.1.2 (Web MVC, Data JPA, Validation, Security)
- JWT (jjwt 0.12.6) y BCrypt
- Flyway (el esquema se crea con migraciones; Hibernate solo valida)
- MySQL 8 en desarrollo/producción, H2 en memoria para las pruebas
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
   $env:JWT_SECRET="una-clave-de-al-menos-32-caracteres"   # obligatoria
   ```

   Opcionales: `SPRING_PROFILES_ACTIVE` (`dev` por defecto), `SERVER_PORT` (`8080`),
   `JWT_EXPIRATION_DAYS` (`7`), `CORS_ALLOWED_ORIGINS` (lista de orígenes separados por coma, sin comodines;
   por defecto `http://localhost:4200`; en producción debe ser el origen de la web app) y `NOTIFICATIONS_EMAIL_LOG_CODES` (`false`; en `true` los códigos
   de verificación se escriben en el log, solo para desarrollo local, porque aún no hay proveedor de correo).

3. Levantar la aplicación:

   ```powershell
   .\mvnw.cmd spring-boot:run
   ```

   Por defecto usa el perfil `dev` y el puerto `8080`.

## API de IAM

| Método y ruta | Acceso | Descripción |
|---|---|---|
| `POST /api/v1/authentication/sign-up` | público | Registra la cuenta (pendiente de verificación) y envía un código de 6 dígitos |
| `POST /api/v1/authentication/verify-email` | público | Activa la cuenta con el código |
| `POST /api/v1/authentication/send-verification-code` | público | Reenvía el código (máximo uno por minuto) |
| `POST /api/v1/authentication/sign-in` | público | Devuelve el token JWT |
| `POST /api/v1/authentication/request-password-recovery` | público | Siempre responde 202, exista o no el correo |
| `POST /api/v1/authentication/recover-password` | público | Cambia la contraseña con el código |
| `POST /api/v1/contact-requests` | público | Formulario de contacto |
| `GET /api/v1/users/me`, `GET /api/v1/users/{id}` | token | La cuenta propia (otro id responde 404) |
| `GET /api/v1/roles` | token | Roles de la plataforma |

Las rutas protegidas esperan `Authorization: Bearer <token>`.

## API de Profile

| Método y ruta | Acceso | Descripción |
|---|---|---|
| `GET /api/v1/profiles/me` | token | Perfil del usuario autenticado; responde `404` hasta que lo guarde por primera vez |
| `PUT /api/v1/profiles/me` | token | Crea o actualiza el perfil propio: nombre, organización, teléfono, foto, tema e idioma |

El `userId` no lo envía el cliente: se resuelve del token con la ACL de IAM
(`IamContextFacade`), y el correo electrónico no se guarda en el perfil porque
es propiedad del contexto IAM.

## Pruebas

```powershell
$env:JAVA_HOME="C:\ruta\al\jdk-27"
.\mvnw.cmd -B -Dmaven.artifact.threads=1 clean test
```

Las pruebas usan H2 en memoria (perfil `test`) con el esquema de Flyway; no necesitan MySQL ni Docker.

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
│   ├── application/result/                          # Result<T, E> y ApplicationError
│   ├── domain/model/aggregates/                     # AbstractDomainAggregateRoot<T>
│   ├── domain/model/events/                         # contrato DomainEvent
│   ├── infrastructure/documentation/openapi/configuration/   # OpenApiConfiguration
│   ├── infrastructure/i18n/configuration/           # LocaleConfiguration (Accept-Language)
│   ├── infrastructure/persistence/jpa/configuration/strategy/ # naming snake_case + tablas en plural
│   ├── infrastructure/persistence/jpa/entities/     # AuditableAbstractPersistenceEntity
│   ├── infrastructure/events/                       # DomainEventPublisher
│   ├── infrastructure/time/configuration/           # Clock
│   └── interfaces/rest/                             # GlobalExceptionHandler, resources/, transform/
├── iam/                                             # bounded context Identity and Access Management
│   └── domain/  application/  infrastructure/  interfaces/   # ver la estructura siguiente
├── profile/                                         # bounded context Profile Management
│   └── domain/  application/  infrastructure/  interfaces/
└── payments/
    ├── domain/model/aggregates/                     # Payment
    ├── domain/model/entities/                       # PaymentTransaction, PaymentReceipt
    ├── domain/model/valueobjects/                   # PaymentStatus, TransactionStatus, PaymentMethod
    ├── domain/repositories/                         # puertos de persistencia
    ├── application/                                 # comandos, queries y puertos
    ├── infrastructure/persistence/jpa/              # entidades, mappers y adaptadores
    ├── infrastructure/payment/                      # FakePaymentGateway
    ├── infrastructure/receipt/                      # PDF y numeración de comprobantes
    └── interfaces/rest/                             # controladores, resources y mappers
```

La base en `main` y `develop` contiene únicamente el shared kernel. Los bounded
contexts y sus pruebas se agregan en ramas `feature/<contexto>`.

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
│   ├── persistence/jpa/{adapters,assemblers,converters,embeddables,entities,repositories}/
│   └── hashing/, tokens/, email/, authorization/   # adaptadores de los puertos (solo IAM)
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
