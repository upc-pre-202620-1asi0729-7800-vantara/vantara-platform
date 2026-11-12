# Changelog

Formato basado en [Keep a Changelog](https://keepachangelog.com/es-ES/1.1.0/).

## [Unreleased]

### Added
- Bounded context IAM completo:
  - `infrastructure`: entidades JPA, converters, assemblers y adaptadores de repositorio con publicación de
    eventos de dominio; BCrypt, JWT (jjwt 0.12.6), seguridad stateless con Spring Security, adaptador de
    correo que solo escribe en el log y generador de códigos con `SecureRandom`.
  - `application`: puertos de salida, servicios de comandos y consultas (`Result<T, ApplicationError>`),
    siembra idempotente de roles y manejadores de eventos.
  - `interfaces`: recursos, assemblers REST, controladores (`authentication`, `users`, `roles`,
    `contact-requests`) y `IamContextFacade` (ACL).
- Migración Flyway `V1__create_iam_schema.sql`; Hibernate solo valida el esquema.
- Pruebas de persistencia (H2), de servicios (Mockito), de JWT y de los endpoints (MockMvc).
- `INVALID_CREDENTIALS` y `UNAUTHORIZED` se responden con 401; las violaciones de unicidad con 409.

### Security
- El inicio de sesión responde el mismo error para un correo inexistente y una contraseña incorrecta.
- La recuperación de contraseña no revela qué correos están registrados.
- Cada intento de verificación se guarda; tras 5 fallos el código queda bloqueado.
- Los códigos nunca se escriben en los logs salvo con `NOTIFICATIONS_EMAIL_LOG_CODES=true` (solo desarrollo).
