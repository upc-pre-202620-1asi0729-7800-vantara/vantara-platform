-- Veterinary and health bounded context: appointments, medical records, medications, treatments and vaccines.
-- animal_id values are livestock identifiers and *_id of people are account ids; there are no foreign keys across contexts.

CREATE TABLE appointments (
    id                  VARCHAR(36)  NOT NULL,
    rancher_id          BIGINT       NOT NULL,
    veterinarian_id     BIGINT       NOT NULL,
    requested_by        BIGINT       NOT NULL,
    scheduled_at        DATETIME(6)  NOT NULL,
    reason              VARCHAR(255) NOT NULL,
    origin              VARCHAR(20)  NOT NULL,
    status              VARCHAR(20)  NOT NULL,
    cancellation_reason VARCHAR(255),
    created_at          DATE         NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE appointment_animals (
    id             VARCHAR(36) NOT NULL,
    appointment_id VARCHAR(36) NOT NULL,
    animal_id      VARCHAR(36) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_appointment_animals_appointment_animal UNIQUE (appointment_id, animal_id)
);

CREATE TABLE medical_records (
    id              VARCHAR(36)   NOT NULL,
    animal_id       VARCHAR(36)   NOT NULL,
    appointment_id  VARCHAR(36),
    veterinarian_id BIGINT        NOT NULL,
    recorded_at     DATETIME(6)   NOT NULL,
    symptoms        VARCHAR(1000) NOT NULL,
    diagnosis       VARCHAR(1000) NOT NULL,
    health_status   VARCHAR(20)   NOT NULL,
    severity        VARCHAR(10)   NOT NULL,
    notes           VARCHAR(1000),
    PRIMARY KEY (id)
);

CREATE TABLE medications (
    id                VARCHAR(36)  NOT NULL,
    name              VARCHAR(120) NOT NULL,
    active_ingredient VARCHAR(120) NOT NULL,
    presentation      VARCHAR(120) NOT NULL,
    category          VARCHAR(64)  NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE treatments (
    id               VARCHAR(36)   NOT NULL,
    animal_id        VARCHAR(36)   NOT NULL,
    medical_record_id VARCHAR(36)  NOT NULL,
    medication_id    VARCHAR(36)   NOT NULL,
    treatment_type   VARCHAR(64)   NOT NULL,
    route            VARCHAR(64)   NOT NULL,
    dose_amount      DECIMAL(10,2) NOT NULL,
    dose_unit        VARCHAR(16)   NOT NULL,
    frequency_hours  INTEGER       NOT NULL,
    duration_days    INTEGER       NOT NULL,
    scheduled_at     DATETIME(6)   NOT NULL,
    administered_at  DATETIME(6),
    next_date        DATE,
    status           VARCHAR(20)   NOT NULL,
    instructions     VARCHAR(1000),
    PRIMARY KEY (id)
);

CREATE TABLE vaccines (
    id               VARCHAR(36)   NOT NULL,
    animal_id        VARCHAR(36)   NOT NULL,
    veterinarian_id  BIGINT        NOT NULL,
    name             VARCHAR(120)  NOT NULL,
    vaccine_type     VARCHAR(64),
    laboratory       VARCHAR(120),
    lot_number       VARCHAR(64),
    route            VARCHAR(64),
    application_site VARCHAR(120),
    dose_amount      DECIMAL(10,2) NOT NULL,
    dose_unit        VARCHAR(16)   NOT NULL,
    applied_at       DATE          NOT NULL,
    next_dose_at     DATE,
    status           VARCHAR(20)   NOT NULL,
    observations     VARCHAR(1000),
    PRIMARY KEY (id)
);
