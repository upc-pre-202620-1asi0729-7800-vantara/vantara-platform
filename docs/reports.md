# Reports: contrato REST y reglas

Bounded context `reports`. Es un **lector**: no tiene persistencia, no crea tablas ni modifica animales,
lotes, planes ni registros de alimentación.

## Endpoints

| Método y ruta | Acceso | Parámetros (query) |
|---|---|---|
| `GET /api/v1/reports/livestock` | token | `startsOn`, `endsOn` (ISO `yyyy-MM-dd`, opcionales), `lotId`, `breed`, `sex`, `status` |
| `GET /api/v1/reports/feeding` | token | `startsOn`, `endsOn` (ISO `yyyy-MM-dd`, opcionales), `lotId` |

No existe parámetro de ranchero: el ranchero se resuelve siempre desde el token.

### Respuesta de `GET /api/v1/reports/livestock` (200)

```json
{
  "totalAnimals": 12,
  "averageWeight": 365.42,
  "totalLots": 3,
  "males": 4,
  "females": 8,
  "animalsByLot": [
    { "lotId": "lot-001", "lotName": "Lote A", "animalCount": 5, "averageWeight": 380.00, "males": 2, "females": 3 }
  ],
  "animalsByBreed": [
    { "breed": "Holstein", "count": 7 }
  ]
}
```

### Respuesta de `GET /api/v1/reports/feeding` (200)

```json
{
  "totalPlans": 2,
  "animalsWithPlan": 9,
  "totalRegisteredConsumption": 468,
  "totalFeedingRecords": 3,
  "consumptionUnits": ["kg"],
  "consumptionByFeed": [
    { "feedName": "Pasto elefante", "quantity": 196, "unit": "kg" }
  ],
  "consumptionByLot": [
    { "lotId": "lot-001", "lotName": "Lote A", "planName": "Plan de engorde - Lote A", "quantity": 196, "unit": "kg" }
  ]
}
```

`consumptionUnits` es un campo adicional que el front no tiene. Sirve para detectar cuando el consumo total mezcla unidades.

### Errores

| Código | Cuándo |
|---|---|
| 400 | Fecha mal formada o `endsOn` anterior a `startsOn` |
| 401 | Sin token válido, o token cuyo correo no tiene cuenta IAM |
| 503 `REPORT_SOURCE_UNAVAILABLE` | No hay fuente de datos de ganado disponible (integración pendiente, ver abajo) |

## Reglas de cálculo

### Ganado

- El periodo se compara contra `registeredAt` (inclusivo en ambos extremos).
- El sexo se compara sin distinguir mayúsculas. `males` cuenta `Macho` y `females` cuenta `Hembra`.
  Cualquier otro valor cuenta en los totales pero no en machos ni hembras, así que la suma de ambos puede no
  igualar `totalAnimals`.
- `averageWeight` es el promedio del peso en kilogramos con dos decimales. Vale `0` si no hay animales.
- `totalLots` cuenta los lotes distintos de los animales que cumplen los filtros.
- `animalsByLot` solo incluye lotes con animales filtrados, ordenados por nombre. `lotName` es `null` si el lote
  no pertenece al ranchero.
- `animalsByBreed` va ordenado por raza.

### Alimentación

- Un plan entra si `startsOn` cae dentro del periodo (inclusivo) y su lote cumple `lotId`.
- Un registro entra si la fecha de `feedAt` (solo la fecha) cae dentro del periodo, su lote es del ranchero y pertenece
  a un plan seleccionado.
- `animalsWithPlan` cuenta los animales distintos cubiertos por los planes: los individuales y todos los animales del
  lote cuando el plan es de lote.
- `consumptionByFeed` agrupa por alimento **y unidad**: nunca mezcla unidades distintas.
- `consumptionByLot` produce una fila por lote y unidad. Si el lote no tiene registros, una fila con `quantity` `0` y
  `unit` `null`. `planName` es el plan del lote con fecha de inicio más temprana.
- `totalRegisteredConsumption` suma las cantidades de todos los registros. Si `consumptionUnits` tiene más de una
  unidad, esa suma mezcla unidades y el cliente debe advertirlo.

## Acceso por usuario

1. El token JWT identifica la cuenta por su correo (`sub`). `Principal.getName()` devuelve ese correo.
2. `IamContextFacade.fetchUserIdByEmail(correo)` devuelve el id IAM. El dato de ranchero es `String.valueOf(id)`.
3. Toda la lectura pasa por el puerto `LivestockDataSource`, que recibe ese `rancherId`. La implementación debe devolver
   solo los datos de ese ranchero.
4. Los cálculos vuelven a filtrar: animales por `rancherId`, y planes y registros solo si su lote pertenece al ranchero.
   Así, aunque la fuente devolviera datos ajenos, no aparecen en el reporte.

## Integración pendiente (bloqueo)

Livestock no publica lecturas por ranchero. Hoy:

- `LivestockContextFacade` (`livestockmanagement.interfaces.acl`) solo tiene existencia, escritura y búsqueda por
  arete. No hay listados.
- `GET /api/v1/animals`, `/lots`, `/feedingPlans` y `/feedingLogs` devuelven los datos de **todos** los rancheros a
  cualquier usuario autenticado. La regla `anyRequest().authenticated()` no filtra por dueño.

Por eso Reports no usa esos endpoints ni los repositorios de livestock. Queda fail-closed: sin fuente, responde 503.

Cambio mínimo propuesto, fuera de esta rama, en `livestockmanagement`:

1. Publicar en `LivestockContextFacade`, solo con tipos JDK, las lecturas del ranchero: animales, lotes, planes,
   ítems de plan y registros de alimentación. Los repositorios ya tienen `findAllByRancherId` para animales.
2. Implementar `LivestockDataSource` (puerto de `reports.application.internal.outboundservices.livestock`) en
   `reports.infrastructure.acl` delegando en esas lecturas. Es una sola clase.
3. Fijar en livestock el `rancherId` desde el principal al crear animales y lotes. Hoy el cliente lo envía en el cuerpo
   y puede crear datos a nombre de otro ranchero. Es un problema de livestock, no de Reports.

## Coordinación con el frontend

- El front llama a la API falsa (`vantara-fake-api`) y calcula los reportes en el cliente (`ReportsAssembler`). No
  consume estos endpoints. Conectarlo requiere cambiar `ReportsApi` y `ReportsStore`, y no se hizo en este alcance.
- `rancherId` debe ser el id numérico de IAM. Los animales y lotes del backend lo guardan como texto. Los ids del front
  (`usr-002`) no coinciden con eso y hay que alinearlos.
- El sexo se envía como `Macho` o `Hembra`. El backend lo acepta sin distinguir mayúsculas.
- Diferencias corregidas respecto del ensamblador del front:
  - El front compara la fecha final de registros de alimentación como texto ISO con hora, lo que excluye los registros
    del último día. Aquí se compara por fecha.
  - El front suma unidades distintas y asigna `kg` por defecto a lotes sin registros. Aquí se separa por unidad y se
    devuelve `null` cuando no hay unidad.
- `/api/v1/reports/*` no colisiona con nada: la ruta `/reports` del front (`reportShares`) no está implementada.

## Fuera de alcance

- Persistencia de reportes, tablas `reports` o `reportShares`.
- Generación programada y exportación PDF/CSV en servidor. El front exporta localmente.
- La pantalla `/veterinary/reports`, que pertenece a veterinaria.
