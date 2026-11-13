-- =============================================================================
-- Vantara / Hatarium - Bounded context: veterinary-and-health
-- Motor: MySQL 8.0.16 o superior (necesario para CHECK constraints).
--
-- Contiene SOLO las tablas que pertenecen a este bounded context:
--   appointments, appointment_animals, medical_records,
--   treatments, medications, vaccines
--
-- Datos de demostracion tomados de hatarium/server/db.json (front).
--
-- Referencias a otros bounded contexts (sin FOREIGN KEY en este script,
-- porque esas tablas viven en otros modulos):
--   appointments.rancher_id, appointments.veterinarian_id,
--   appointments.requested_by, medical_records.veterinarian_id,
--   vaccines.veterinarian_id, treatments/vaccines/medical_records.animal_id
--     -> users.id (IAM)  /  animals.id (livestock-management)
--   Nombre del veterinario -> profile_profile.full_name (profile-management),
--   unido por profile_profile.user_id = users.id.
--
-- Criterio de IDs de usuario (BIGINT): usr-002 -> 2 (ganadero),
-- usr-003 -> 3 (veterinario), segun el orden de insercion de usuarios en IAM.
-- =============================================================================

SET NAMES utf8mb4;

CREATE DATABASE IF NOT EXISTS vantara_platform
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE vantara_platform;

-- -----------------------------------------------------------------------------
-- Limpieza para re-ejecutar el script (solo tablas de este bounded context)
-- -----------------------------------------------------------------------------
DROP TABLE IF EXISTS treatments;
DROP TABLE IF EXISTS medical_records;
DROP TABLE IF EXISTS appointment_animals;
DROP TABLE IF EXISTS appointments;
DROP TABLE IF EXISTS vaccines;
DROP TABLE IF EXISTS medications;

-- -----------------------------------------------------------------------------
-- appointments: citas entre ganadero y veterinario
-- -----------------------------------------------------------------------------
CREATE TABLE appointments (
    id                  VARCHAR(36)  NOT NULL,
    rancher_id          BIGINT       NOT NULL,
    veterinarian_id     BIGINT       NOT NULL,
    requested_by        BIGINT       NOT NULL,
    origin              VARCHAR(20)  NOT NULL,
    scheduled_at        DATETIME(6)  NOT NULL,
    reason              VARCHAR(255) NOT NULL,
    status              VARCHAR(20)  NOT NULL,
    cancellation_reason VARCHAR(255) NULL,
    created_at          DATE         NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT ck_appointments_origin CHECK (origin IN ('RANCHER_REQUEST', 'TECHNICAL_VISIT')),
    CONSTRAINT ck_appointments_status CHECK (status IN ('scheduled', 'completed', 'cancelled')),
    INDEX idx_appointments_rancher (rancher_id),
    INDEX idx_appointments_veterinarian_schedule (veterinarian_id, scheduled_at)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- appointment_animals: relacion N:M entre citas y animales (animal = livestock)
-- -----------------------------------------------------------------------------
CREATE TABLE appointment_animals (
    id             VARCHAR(36) NOT NULL,
    appointment_id VARCHAR(36) NOT NULL,
    animal_id      VARCHAR(36) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_appointment_animals_appointment_animal UNIQUE (appointment_id, animal_id),
    INDEX idx_appointment_animals_animal (animal_id),
    CONSTRAINT fk_appointment_animals_appointment
        FOREIGN KEY (appointment_id) REFERENCES appointments (id) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- medical_records: historias clinicas (diagnosticos)
-- -----------------------------------------------------------------------------
CREATE TABLE medical_records (
    id              VARCHAR(36)   NOT NULL,
    appointment_id  VARCHAR(36)   NULL,
    animal_id       VARCHAR(36)   NOT NULL,
    veterinarian_id BIGINT        NOT NULL,
    recorded_at     DATETIME(6)   NOT NULL,
    symptoms        VARCHAR(1000) NOT NULL,
    diagnosis       VARCHAR(1000) NOT NULL,
    severity        VARCHAR(10)   NOT NULL,
    health_status   VARCHAR(20)   NOT NULL,
    notes           VARCHAR(1000) NULL,
    PRIMARY KEY (id),
    CONSTRAINT ck_medical_records_severity CHECK (severity IN ('low', 'medium', 'high')),
    CONSTRAINT ck_medical_records_health_status CHECK (health_status IN ('healthy', 'follow_up', 'critical')),
    INDEX idx_medical_records_animal (animal_id),
    INDEX idx_medical_records_appointment (appointment_id),
    CONSTRAINT fk_medical_records_appointment
        FOREIGN KEY (appointment_id) REFERENCES appointments (id) ON DELETE SET NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- medications: catalogo de medicamentos (datos de referencia)
-- -----------------------------------------------------------------------------
CREATE TABLE medications (
    id                VARCHAR(36)  NOT NULL,
    name              VARCHAR(120) NOT NULL,
    category          VARCHAR(64)  NOT NULL,
    active_ingredient VARCHAR(120) NOT NULL,
    presentation      VARCHAR(120) NOT NULL,
    PRIMARY KEY (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- treatments: tratamientos prescritos dentro de una historia clinica
-- -----------------------------------------------------------------------------
CREATE TABLE treatments (
    id                VARCHAR(36)   NOT NULL,
    medical_record_id VARCHAR(36)   NOT NULL,
    animal_id         VARCHAR(36)   NOT NULL,
    medication_id     VARCHAR(36)   NOT NULL,
    treatment_type    VARCHAR(64)   NOT NULL,
    dose_amount       DECIMAL(10,2) NOT NULL,
    dose_unit         VARCHAR(16)   NOT NULL,
    route             VARCHAR(64)   NOT NULL,
    frequency_hours   INT           NOT NULL,
    duration_days     INT           NOT NULL,
    scheduled_at      DATETIME(6)   NOT NULL,
    administered_at   DATETIME(6)   NULL,
    next_date         DATE          NULL,
    status            VARCHAR(20)   NOT NULL,
    instructions      VARCHAR(1000) NULL,
    PRIMARY KEY (id),
    CONSTRAINT ck_treatments_dose_amount CHECK (dose_amount > 0),
    CONSTRAINT ck_treatments_frequency_hours CHECK (frequency_hours > 0),
    CONSTRAINT ck_treatments_duration_days CHECK (duration_days > 0),
    CONSTRAINT ck_treatments_status CHECK (status IN ('active', 'completed', 'cancelled')),
    INDEX idx_treatments_animal (animal_id),
    INDEX idx_treatments_medical_record (medical_record_id),
    INDEX idx_treatments_medication (medication_id),
    CONSTRAINT fk_treatments_medical_record
        FOREIGN KEY (medical_record_id) REFERENCES medical_records (id) ON DELETE RESTRICT,
    CONSTRAINT fk_treatments_medication
        FOREIGN KEY (medication_id) REFERENCES medications (id) ON DELETE RESTRICT
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- vaccines: dosis de vacunas aplicadas a animales
-- -----------------------------------------------------------------------------
CREATE TABLE vaccines (
    id               VARCHAR(36)   NOT NULL,
    animal_id        VARCHAR(36)   NOT NULL,
    veterinarian_id  BIGINT        NOT NULL,
    vaccine_type     VARCHAR(64)   NULL,
    name             VARCHAR(120)  NOT NULL,
    dose_amount      DECIMAL(10,2) NOT NULL,
    dose_unit        VARCHAR(16)   NOT NULL,
    laboratory       VARCHAR(120)  NULL,
    lot_number       VARCHAR(64)   NULL,
    applied_at       DATE          NOT NULL,
    next_dose_at     DATE          NULL,
    route            VARCHAR(64)   NULL,
    application_site VARCHAR(120)  NULL,
    status           VARCHAR(20)   NOT NULL,
    observations     VARCHAR(1000) NULL,
    PRIMARY KEY (id),
    CONSTRAINT ck_vaccines_dose_amount CHECK (dose_amount > 0),
    CONSTRAINT ck_vaccines_status CHECK (status IN ('completed', 'pending')),
    CONSTRAINT ck_vaccines_next_dose CHECK (next_dose_at IS NULL OR next_dose_at > applied_at),
    INDEX idx_vaccines_animal (animal_id),
    INDEX idx_vaccines_veterinarian (veterinarian_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

-- =============================================================================
-- DATOS (desde db.json)
-- =============================================================================

-- appointments (db.json: appointments). usr-004 no existe en db.json: se asigna
-- al unico veterinario registrado (usr-003). REVISAR con el equipo de IAM.
INSERT INTO appointments
    (id, rancher_id, veterinarian_id, requested_by, origin, scheduled_at, reason, status, cancellation_reason, created_at)
VALUES
    ('app-001',   2, 3, 2, 'RANCHER_REQUEST', '2026-12-08 10:00:00', 'Control veterinario',                                               'scheduled', NULL, '2026-09-08'),
    ('app-002',   2, 3, 2, 'RANCHER_REQUEST', '2026-09-20 09:30:00', 'Revisión rutinaria',                                                'completed', NULL, '2026-09-01'),
    ('fmeqMr0',   2, 3, 3, 'TECHNICAL_VISIT', '2026-10-06 10:30:00', 'Franco dejo la escuela',                                            'scheduled', NULL, '2026-10-06'),
    ('HtsBSSY',   2, 3, 3, 'TECHNICAL_VISIT', '2026-10-06 10:30:00', 'estuvo en un accidente',                                            'scheduled', NULL, '2026-10-06'),
    ('PLWW4TC',   2, 3, 3, 'TECHNICAL_VISIT', '2026-10-06 10:30:00', 'Revisión periódica de salud y control de peso',                    'scheduled', NULL, '2026-10-06'),
    ('2Nk9IXn',   2, 3, 3, 'TECHNICAL_VISIT', '2026-10-06 11:20:00', 'Martin falto a clase',                                             'scheduled', NULL, '2026-10-06'),
    ('fCV7eMo',   2, 3, 3, 'TECHNICAL_VISIT', '2026-11-02 10:30:00', 'Revisión periódica de salud fgdgy control de peso',                 'completed', NULL, '2026-10-31'),
    ('8BlbyYW',   2, 3, 3, 'TECHNICAL_VISIT', '2026-11-05 10:30:00', 'chequeo diario',                                                    'completed', NULL, '2026-10-31'),
    ('NxNL9qZ',   2, 3, 3, 'TECHNICAL_VISIT', '2026-10-31 22:30:00', 'Revisión periódica de salud y control de peso',                    'completed', NULL, '2026-10-31'),
    ('UsDjXr2',   2, 3, 3, 'TECHNICAL_VISIT', '2026-10-31 10:30:00', 'Revisión periódica de salud y control de peso',                    'scheduled', NULL, '2026-10-31'),
    ('c-V6eh2',   2, 3, 3, 'TECHNICAL_VISIT', '2026-11-01 10:30:00', 'Revisión periódica de salud y control de peso',                    'scheduled', NULL, '2026-10-31');

-- appointment_animals (db.json: appointmentAnimals)
INSERT INTO appointment_animals (id, appointment_id, animal_id) VALUES
    ('appa-001', 'app-001',  'anm-047'),
    ('appa-002', 'app-002',  'anm-047'),
    ('WdxpTOH',  'fmeqMr0',  'anm-073'),
    ('XCLHvm9',  'HtsBSSY',  'anm-031'),
    ('PtZJ_8f',  'PLWW4TC',  'anm-birth-O0fIBIm'),
    ('9gAoMK5',  '2Nk9IXn',  'anm-birth-O0fIBIm'),
    ('3W_pOXa',  'fCV7eMo',  'anm-birth-O0fIBIm'),
    ('DZjhstk',  '8BlbyYW',  'anm-22'),
    ('TNZ7e2g',  'NxNL9qZ',  'anm-demo-paloma'),
    ('Y85-fRi',  'UsDjXr2',  'anm-011'),
    ('MVyX166',  'c-V6eh2',  'anm-22');

-- medical_records (db.json: medicalRecords). Fechas sin hora se guardan a medianoche.
INSERT INTO medical_records
    (id, appointment_id, animal_id, veterinarian_id, recorded_at, symptoms, diagnosis, severity, health_status, notes)
VALUES
    ('mr-001',  'app-002', 'anm-047',            3, '2026-09-20 00:00:00', 'Sin síntomas',                          'Estado general óptimo',     'low',    'healthy',   'Peso dentro del rango esperado.'),
    ('mr-002',  NULL,      'anm-047',            3, '2026-09-07 00:00:00', 'Sin síntomas',                          'Aplicación preventiva',     'low',    'healthy',   'Vacunación aplicada correctamente.'),
    ('mr-003',  NULL,      'anm-018',            3, '2026-09-06 00:00:00', 'Inflamación leve',                      'Inflamación localizada',    'medium', 'follow_up', 'Animal en seguimiento.'),
    ('yi5wURy', 'fCV7eMo', 'anm-birth-O0fIBIm',  3, '2026-11-02 13:33:00', 'desidratacion, fiebre y dolor',         'presenta fiebre',           'low',    'healthy',   'se realizo evaluacion y presenta un infeccion · Causa probable: cancer · Tipo: Clinico'),
    ('Zt7JMhk', 'NxNL9qZ', 'anm-demo-paloma',    3, '2026-10-31 10:30:00', 'da',                                    'da',                        'low',    'healthy',   'da · Causa probable: da · Tipo: Complementario'),
    ('UbhfToY', '8BlbyYW', 'anm-22',             3, '2026-11-03 10:30:00', 'd',                                     'd',                         'low',    'healthy',   'd · Causa probable: d · Tipo: Clinico');

-- medications (db.json: medications)
INSERT INTO medications (id, name, category, active_ingredient, presentation) VALUES
    ('med-001', 'Antibiótico preventivo', 'antibiotic',         'Oxitetraciclina', 'Inyectable'),
    ('med-002', 'Antiinflamatorio',       'anti-inflammatory',  'Meloxicam',       'Inyectable');

-- treatments (db.json: treatments). "10 ml" se separa en cantidad 10.00 y unidad ml.
-- Las fechas ISO en UTC (sufijo Z) se guardan sin zona, en UTC.
INSERT INTO treatments
    (id, medical_record_id, animal_id, medication_id, treatment_type, dose_amount, dose_unit, route,
     frequency_hours, duration_days, scheduled_at, administered_at, next_date, status, instructions)
VALUES
    ('trt-001', 'mr-003',   'anm-018',           'med-001', 'Preventivo', 10.00, 'ml', 'Intramuscular', 24, 5, '2026-08-15 00:00:00',     '2026-08-20 00:00:00',     '2026-08-21', 'completed', 'Completar esquema indicado.'),
    ('WP5bqb-', 'yi5wURy',  'anm-birth-O0fIBIm', 'med-002', 'Curativo',   5.00, 'mL', 'Intramuscular', 24, 7, '2026-10-31 23:28:18.524', '2026-10-31 23:28:18.524', '2026-11-07', 'active',    'inyectar meidante via intramuscular por el encargado · niguna'),
    ('ts_rqTi', 'Zt7JMhk',  'anm-demo-paloma',   'med-001', 'Preventivo', 5.00, 'mL', 'Intramuscular', 24, 7, '2026-10-31 23:51:33.161', '2026-10-31 23:51:33.161', '2026-11-07', 'active',    'Completar esquema indicado cada 24 horas. · Monitorear la respuesta del animal.'),
    ('kDYwuuh', 'UbhfToY',  'anm-22',            'med-001', 'Preventivo', 5.00, 'mL', 'Intramuscular', 24, 7, '2026-10-31 23:53:45.290', '2026-10-31 23:53:45.290', '2026-11-07', 'active',    'Completar esquema indicado cada 24 horas. · Monitorear la respuesta del animal.');

-- vaccines (db.json: vaccines). Sin vaccine_type en db.json: queda NULL.
INSERT INTO vaccines
    (id, animal_id, veterinarian_id, vaccine_type, name, dose_amount, dose_unit, laboratory, lot_number,
     applied_at, next_dose_at, route, application_site, status, observations)
VALUES
    ('vac-001', 'anm-047',           3, NULL, 'Fiebre Aftosa', 2.00, 'ml', 'Biogénesis Bag',                  'FA-2026-001', '2026-09-07', '2027-03-07', 'Subcutánea', 'Cuello (lado izquierdo)', 'completed', 'Primera dosis del ciclo anual. Aplicada sin complicaciones.'),
    ('vac-002', 'anm-047',           3, NULL, 'Brucelosis',    2.00, 'ml', 'Laboratorio Veterinario Nacional', 'BR-2026-015', '2026-03-12', '2027-03-12', 'Subcutánea', 'Cuello',                  'completed', 'Vacuna aplicada correctamente.'),
    ('vac-003', 'anm-047',           3, NULL, 'Carbunco',      2.00, 'ml', 'Laboratorio Veterinario Nacional', 'CA-2026-008', '2026-01-10', '2027-01-10', 'Subcutánea', 'Cuello',                  'completed', 'Vacuna anual aplicada.'),
    ('EsgKHLd', 'anm-birth-O0fIBIm', 3, NULL, 'Fiebre Aftosa', 2.00, 'mL', 'Laboratorio Veterinario Nacional', 'L2026-017', '2026-10-31', '2027-03-07', 'Subcutánea', 'Cuello (lado izquierdo)', 'completed', 'Verificada cadena de frío antes de la aplicación.');

-- =============================================================================
-- VERIFICACION (esperado: appointments 11, appointment_animals 11,
-- medical_records 6, medications 2, treatments 4, vaccines 4)
-- =============================================================================
SELECT 'appointments' AS tabla, COUNT(*) AS filas FROM appointments
UNION ALL SELECT 'appointment_animals', COUNT(*) FROM appointment_animals
UNION ALL SELECT 'medical_records', COUNT(*) FROM medical_records
UNION ALL SELECT 'medications', COUNT(*) FROM medications
UNION ALL SELECT 'treatments', COUNT(*) FROM treatments
UNION ALL SELECT 'vaccines', COUNT(*) FROM vaccines;
