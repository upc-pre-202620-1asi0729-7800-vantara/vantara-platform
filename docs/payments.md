# Payments bounded context

Contexto de pagos de Vantara / Hatarium. Esta primera fase cubre el checkout
compartido de suscripciones y citas veterinarias, además del historial de pagos,
transacciones y comprobantes.

## Alcance

- `Payment`: pago realizado por un rancher.
- `PaymentTransaction`: intento de cobro simulado con `FakePay`.
- `PaymentReceipt`: comprobante emitido automáticamente cuando el pago queda `paid`.
- No se modelan planes, suscripciones ni métodos de pago guardados. El plan se
  conserva como texto en `concept`.
- `appointmentId` es opcional:
  - `null` para suscripciones o pagos independientes.
  - Con valor para pagos asociados a una cita veterinaria.

## Endpoints

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/api/v1/payments` | Lista pagos ordenados por `createdAt` ascendente. |
| `GET` | `/api/v1/payments/{id}` | Obtiene un pago por ID. |
| `POST` | `/api/v1/payments` | Crea un pago simulado como `paid`. |
| `GET` | `/api/v1/transactions?paymentId=` | Lista transacciones; devuelve `[]` si no hay. |
| `GET` | `/api/v1/receipts?paymentId=` | Lista comprobantes; devuelve `[]` si no hay. |
| `GET` | `/api/v1/receipts/{id}/document` | Descarga el comprobante en PDF. |

## Crear un pago

```json
{
  "rancherId": "usr-002",
  "appointmentId": null,
  "concept": "Suscripción Profesional",
  "amount": "49.90",
  "currency": "PEN",
  "paymentMethod": {
    "brand": "VISA",
    "last4": "4582",
    "holderName": "JUAN QUISPE"
  }
}
```

El backend ignora `status`, `createdAt`, `confirmedAt` y `cancelledAt` si el
frontend los envía. Esos valores se derivan del flujo de pago.

Respuesta `201 Created`:

```json
{
  "id": "b0c1f2a3-...",
  "rancherId": "usr-002",
  "appointmentId": null,
  "concept": "Suscripción Profesional",
  "amount": "49.90",
  "currency": "PEN",
  "status": "paid",
  "createdAt": "2026-11-07T12:00:00Z",
  "confirmedAt": "2026-11-07T12:00:00Z",
  "cancelledAt": null,
  "paymentMethod": {
    "brand": "VISA",
    "last4": "4582",
    "holderName": "JUAN QUISPE"
  }
}
```

### Método de pago

Solo se aceptan datos no sensibles: marca, últimos cuatro dígitos y titular
opcional. No se debe enviar ni almacenar el número completo de la tarjeta ni el
CVV.

## Comprobante

Al crear un pago aprobado se genera automáticamente:

1. Una `PaymentTransaction` con `provider = "FakePay"` y `status = "approved"`.
2. Un `PaymentReceipt` con número correlativo (`B001-000001`) y
   `documentUrl = "/api/v1/receipts/{id}/document"`.

El PDF se genera con OpenPDF y se devuelve como `application/pdf`.

## Persistencia

La estrategia de nombres de Hibernate pluraliza las tablas. Las tablas físicas
resultantes son:

- `payment_payments`
- `payment_transactions`
- `payment_receipts`

Campos principales de `payment_payments`:

| Columna | Tipo | Notas |
|---|---|---|
| `id` | `VARCHAR(36)` | UUID generado por el dominio. |
| `rancher_id` | `VARCHAR(36)` | Referencia lógica al contexto de usuarios. |
| `appointment_id` | `VARCHAR(36)` NULL | Referencia lógica al contexto veterinario. |
| `concept` | `VARCHAR(200)` | Texto del plan o servicio. |
| `amount` | `DECIMAL(12,2)` | Monto positivo. |
| `currency` | `VARCHAR(3)` | Código ISO-4217. |
| `status` | `VARCHAR(20)` | `pending`, `paid`, `cancelled` o `failed`. |
| `payment_method_brand` | `VARCHAR(20)` NULL | Marca de la tarjeta. |
| `payment_method_last4` | `VARCHAR(4)` NULL | Últimos cuatro dígitos. |
| `payment_method_holder` | `VARCHAR(160)` NULL | Titular. |
| `created_at` | `DATETIME(6)` | Fecha de creación. |
| `confirmed_at` | `DATETIME(6)` NULL | Fecha de confirmación. |
| `cancelled_at` | `DATETIME(6)` NULL | Fecha de cancelación. |

No se crean claves foráneas hacia usuarios, citas o suscripciones en esta fase:
son referencias lógicas entre bounded contexts.

## Pruebas

```powershell
$env:JAVA_HOME="C:\Users\juand\.jdks\openjdk-27.0.1.0.3"
.\mvnw.cmd test
```

Las pruebas cubren las reglas del agregado `Payment`, el value object
`PaymentMethod`, la entidad `PaymentReceipt` y el contrato REST completo
(creación, listado, transacciones, comprobantes y PDF).

## Integración local con el frontend

1. Configurar las credenciales de MySQL en la misma terminal (no se versionan):

   ```powershell
   $env:DATABASE_USER="root"
   $env:DATABASE_PASSWORD="TU_PASSWORD"
   ```

2. Levantar el backend:

   ```powershell
   .\mvnw.cmd spring-boot:run
   ```

3. El frontend Angular (`http://localhost:4200`) consume
   `http://localhost:8080/api/v1`. El backend permite ese origen por defecto;
   para otros orígenes usar `CORS_ALLOWED_ORIGINS`.

4. Verificar CORS con un preflight:

   ```powershell
   curl.exe -i -X OPTIONS http://localhost:8080/api/v1/payments `
     -H "Origin: http://localhost:4200" `
     -H "Access-Control-Request-Method: POST"
   ```

