-- Reproductive bounded context: one record per pregnancy cycle of an animal.
-- animal_id and calf_id are livestock identifiers (UUID strings); there is no foreign key across contexts.

CREATE TABLE reproductive_records (
    id                   BIGINT        NOT NULL AUTO_INCREMENT,
    created_at           DATETIME(6)   NOT NULL,
    updated_at           DATETIME(6)   NOT NULL,
    animal_id            VARCHAR(36)   NOT NULL,
    status               ENUM ('CONFIRMED', 'DRY_OFF', 'ENDED', 'WEANED') NOT NULL,
    confirmed_on         DATE          NOT NULL,
    expected_calving_on  DATE,
    calved_on            DATE,
    calf_id              VARCHAR(36),
    calving_outcome      VARCHAR(120),
    birth_weight_kg      DECIMAL(7,2),
    dry_off_on           DATE,
    weaned_on            DATE,
    weaning_weight_kg    DECIMAL(7,2),
    PRIMARY KEY (id)
);
