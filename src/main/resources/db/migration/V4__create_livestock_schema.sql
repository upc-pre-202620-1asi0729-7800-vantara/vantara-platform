-- Livestock management bounded context: lots, animals, movements, feeding plans and feeding logs.
-- Identifiers are UUID strings published by the context itself, so they are not auto-incremented.

CREATE TABLE lots (
    id         VARCHAR(36)  NOT NULL,
    rancher_id VARCHAR(64)  NOT NULL,
    name       VARCHAR(128) NOT NULL,
    purpose    VARCHAR(512) NOT NULL,
    status     VARCHAR(32)  NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE animals (
    id            VARCHAR(36)   NOT NULL,
    rancher_id    VARCHAR(64)   NOT NULL,
    lot_id        VARCHAR(36)   NOT NULL,
    ear_tag       VARCHAR(64)   NOT NULL,
    name          VARCHAR(128)  NOT NULL,
    species       VARCHAR(64)   NOT NULL,
    breed         VARCHAR(128)  NOT NULL,
    sex           VARCHAR(32)   NOT NULL,
    birth_date    DATE          NOT NULL,
    mother_id     VARCHAR(36),
    father_id     VARCHAR(36),
    weight        DECIMAL(12,2) NOT NULL,
    status        VARCHAR(32)   NOT NULL,
    registered_at DATE          NOT NULL,
    photo_url     MEDIUMTEXT,
    weaned_on     DATE,
    PRIMARY KEY (id),
    CONSTRAINT uk_animal_rancher_ear_tag UNIQUE (rancher_id, ear_tag)
);

CREATE TABLE movements (
    id                 VARCHAR(36)   NOT NULL,
    animal_id          VARCHAR(36)   NOT NULL,
    origin_lot_id      VARCHAR(36),
    destination_lot_id VARCHAR(36)   NOT NULL,
    movement_type      VARCHAR(32)   NOT NULL,
    occurred_at        DATETIME(6)   NOT NULL,
    reason             VARCHAR(1024) NOT NULL,
    recorded_by        VARCHAR(64)   NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE feeding_plans (
    id                     VARCHAR(36)   NOT NULL,
    lot_id                 VARCHAR(36)   NOT NULL,
    animal_id              VARCHAR(36),
    name                   VARCHAR(128)  NOT NULL,
    starts_on              DATE          NOT NULL,
    ends_on                DATE          NOT NULL,
    objective              VARCHAR(512)  NOT NULL,
    current_average_weight DECIMAL(12,2) NOT NULL,
    target_weight          DECIMAL(12,2) NOT NULL,
    frequency              VARCHAR(64)   NOT NULL,
    instructions           VARCHAR(4000),
    status                 VARCHAR(32)   NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE feeding_plan_days (
    feeding_plan_id VARCHAR(36)  NOT NULL,
    day_of_week     VARCHAR(255) NOT NULL,
    CONSTRAINT fk_feeding_plan_days_plan FOREIGN KEY (feeding_plan_id) REFERENCES feeding_plans (id)
);

CREATE TABLE feeding_plan_schedules (
    feeding_plan_id     VARCHAR(36) NOT NULL,
    time                VARCHAR(255),
    quantity_per_animal DECIMAL(38,2),
    CONSTRAINT fk_feeding_plan_schedules_plan FOREIGN KEY (feeding_plan_id) REFERENCES feeding_plans (id)
);

CREATE TABLE feeding_plan_items (
    id              VARCHAR(36)   NOT NULL,
    feeding_plan_id VARCHAR(36)   NOT NULL,
    feed_name       VARCHAR(128)  NOT NULL,
    daily_quantity  DECIMAL(12,2) NOT NULL,
    unit            VARCHAR(32)   NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_feeding_plan_items_plan FOREIGN KEY (feeding_plan_id) REFERENCES feeding_plans (id)
);

CREATE TABLE feeding_logs (
    id           VARCHAR(36)   NOT NULL,
    plan_item_id VARCHAR(36)   NOT NULL,
    animal_id    VARCHAR(36),
    lot_id       VARCHAR(36)   NOT NULL,
    feed_name    VARCHAR(128)  NOT NULL,
    feed_at      DATETIME(6)   NOT NULL,
    quantity     DECIMAL(12,2) NOT NULL,
    unit         VARCHAR(32)   NOT NULL,
    recorded_by  VARCHAR(64)   NOT NULL,
    notes        VARCHAR(4000),
    PRIMARY KEY (id)
);
