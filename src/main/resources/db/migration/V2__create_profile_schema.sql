-- Profile bounded context: personal data and display preferences of an account.

CREATE TABLE profile_profiles (
    id                BIGINT       NOT NULL AUTO_INCREMENT,
    created_at        DATETIME(6)  NOT NULL,
    updated_at        DATETIME(6)  NOT NULL,
    user_id           BIGINT       NOT NULL,
    full_name         VARCHAR(120) NOT NULL,
    organization_name VARCHAR(120),
    phone             VARCHAR(30),
    photo_url         VARCHAR(500),
    theme             VARCHAR(10)  NOT NULL,
    locale            VARCHAR(5)   NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_profile_profiles_user UNIQUE (user_id)
);
