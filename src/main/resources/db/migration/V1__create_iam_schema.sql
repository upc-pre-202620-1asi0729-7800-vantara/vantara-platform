-- IAM bounded context: accounts, roles, verification codes and contact requests.

CREATE TABLE roles (
    id         BIGINT      NOT NULL AUTO_INCREMENT,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    name       VARCHAR(30) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_roles_name UNIQUE (name)
);

CREATE TABLE users (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    created_at      DATETIME(6)  NOT NULL,
    updated_at      DATETIME(6)  NOT NULL,
    email           VARCHAR(254) NOT NULL,
    password_hash   VARCHAR(255) NOT NULL,
    status          VARCHAR(30)  NOT NULL,
    last_sign_in_at DATETIME(6),
    PRIMARY KEY (id),
    CONSTRAINT uk_users_email UNIQUE (email)
);

CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles (id)
);

CREATE TABLE verification_codes (
    id              BIGINT      NOT NULL AUTO_INCREMENT,
    created_at      DATETIME(6) NOT NULL,
    updated_at      DATETIME(6) NOT NULL,
    user_id         BIGINT      NOT NULL,
    code            VARCHAR(6)  NOT NULL,
    purpose         VARCHAR(30) NOT NULL,
    issued_at       DATETIME(6) NOT NULL,
    expires_at      DATETIME(6) NOT NULL,
    consumed_at     DATETIME(6),
    failed_attempts INT         NOT NULL,
    PRIMARY KEY (id)
);

CREATE INDEX idx_verification_codes_user_purpose ON verification_codes (user_id, purpose);

CREATE TABLE contact_requests (
    id         BIGINT        NOT NULL AUTO_INCREMENT,
    created_at DATETIME(6)   NOT NULL,
    updated_at DATETIME(6)   NOT NULL,
    full_name  VARCHAR(120)  NOT NULL,
    email      VARCHAR(254)  NOT NULL,
    message    VARCHAR(1000) NOT NULL,
    sent_at    DATETIME(6)   NOT NULL,
    PRIMARY KEY (id)
);
