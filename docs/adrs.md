# Decisiones de arquitectura (ADR)

## ADR-001: Intentos fallidos de verificación se guardan
`VerificationCode.verify` devuelve un `VerificationOutcome` en lugar de lanzar una excepción. Una excepción
haría `rollback` y el contador de intentos no se guardaría, permitiendo adivinar el código de 6 dígitos.
El servicio siempre guarda el código y luego traduce el resultado a un `ApplicationError`. Máximo 5 intentos.

## ADR-002: Sesión sin estado, sin cierre de sesión en el servidor
Se usa JWT. El cliente descarta el token; no existe `SignOutCommand`. El token vence a los
`authorization.jwt.expiration.days` días (7 por defecto).

## ADR-003: Un inicio de sesión fallido no es un evento de dominio
Se resuelve como `ApplicationError` (`INVALID_CREDENTIALS`, 401), con el mismo mensaje para correo
inexistente y contraseña incorrecta; se compara contra un hash ficticio para igualar tiempos.

## ADR-004: Rol por defecto y estados de cuenta
El rol por defecto es `ROLE_RANCHER`. Los estados son `PENDING_VERIFICATION` y `ACTIVE`; `SUSPENDED` no
existe porque no está en el Event Storming. Una cuenta tiene al menos un rol, y el modelo permite varios.

## ADR-005: Contraseñas
`RawPassword` valida (8 a 72 bytes UTF-8, una letra y un dígito). Se cifran con BCrypt mediante el puerto
`HashingService`; el dominio solo conoce `PasswordHash`.

## ADR-006: Códigos y correos fuera del dominio
Generar el código (`VerificationCodeGenerator`, `SecureRandom`) y enviar correos (`EmailService`) son puertos
de salida de `application`. El evento `VerificationCodeIssuedEvent` no lleva el código: lo entrega el caso de
uso que lo generó. El adaptador actual solo registra en el log hasta elegir proveedor de correo.

## ADR-007: Límite de reenvío
Un nuevo código del mismo propósito solo se emite pasado 1 minuto desde el anterior. Para la recuperación de
contraseña la solicitud limitada responde igual de exitosa, sin enviar correo. Pendiente: límite de intentos
de inicio de sesión.

## ADR-008: Esquema con Flyway
Las migraciones crean el esquema y Hibernate solo valida (`ddl-auto=validate`). Las pruebas aplican las
mismas migraciones sobre H2 en modo MySQL.

## ADR-009: Lectura de cuentas
No hay rol administrador, por eso no se expone el listado de usuarios: un usuario solo lee su propia cuenta
(`/users/me`; otro id responde 404).

## ADR-010: Eventos de dominio
Los adaptadores de repositorio publican `domainEvents()` con `ApplicationEventPublisher` y luego los limpian.
Los manejadores que envían correos usan `@TransactionalEventListener(AFTER_COMMIT)`.
