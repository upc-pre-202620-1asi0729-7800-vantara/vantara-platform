CREATE TABLE notifications (
    id                  CHAR(36)      NOT NULL,
    recipient_user_id   BIGINT        NOT NULL,
    type                VARCHAR(40)   NOT NULL,
    title               VARCHAR(160)  NOT NULL,
    description         VARCHAR(2000) NOT NULL,
    related_entity_type VARCHAR(64)   NOT NULL,
    related_entity_id   VARCHAR(128)  NOT NULL,
    created_at          DATETIME(6)   NOT NULL,
    scheduled_at        DATETIME(6),
    sent_at             DATETIME(6),
    read_at             DATETIME(6),
    PRIMARY KEY (id),
    INDEX ix_notifications_recipient_created (recipient_user_id, created_at, id)
);
