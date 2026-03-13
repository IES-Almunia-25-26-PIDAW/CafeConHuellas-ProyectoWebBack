-- ============================================================
-- V1__init_schema.sql
-- Migración inicial: crea el esquema completo de Café con Huellas
-- Flyway ejecuta este script UNA sola vez al arrancar la app
-- ============================================================

CREATE TABLE IF NOT EXISTS User (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name    VARCHAR(255) NOT NULL,
    last_name_1   VARCHAR(255) NOT NULL,
    last_name_2   VARCHAR(255),
    email         VARCHAR(255) NOT NULL UNIQUE,
    password      VARCHAR(255) NOT NULL,
    phone         VARCHAR(255),
    role          ENUM('ADMIN', 'USER') NOT NULL,
    image_url     VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS Pet (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    breed       VARCHAR(255) NOT NULL,
    category    ENUM('GATO', 'PERRO') NOT NULL,
    age         INT NOT NULL,
    weight      DECIMAL(19, 2) NOT NULL,
    neutered    TINYINT(1) NOT NULL,
    is_ppp      TINYINT(1) NOT NULL DEFAULT 0,
    image_url   VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS Pet_Image (
    id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    pet_id    BIGINT NOT NULL,
    image_url VARCHAR(255) NOT NULL,
    CONSTRAINT fk_pet_image_pet FOREIGN KEY (pet_id) REFERENCES Pet(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Vaccine (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    description TEXT
);

CREATE TABLE IF NOT EXISTS Pet_Vaccine (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    pet_id           BIGINT NOT NULL,
    vaccine_id       BIGINT NOT NULL,
    date_administered DATE NOT NULL,
    next_dose_date    DATE,
    notes             TEXT,
    CONSTRAINT fk_pet_vaccine_pet     FOREIGN KEY (pet_id)     REFERENCES Pet(id),
    CONSTRAINT fk_pet_vaccine_vaccine FOREIGN KEY (vaccine_id) REFERENCES Vaccine(id)
);

CREATE TABLE IF NOT EXISTS User_Pet_Favorites (
    id      BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    pet_id  BIGINT NOT NULL,
    CONSTRAINT fk_favorites_user FOREIGN KEY (user_id) REFERENCES User(id),
    CONSTRAINT fk_favorites_pet  FOREIGN KEY (pet_id)  REFERENCES Pet(id)
);

CREATE TABLE IF NOT EXISTS User_Pet_Relationship (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id           BIGINT NOT NULL,
    pet_id            BIGINT NOT NULL,
    relationship_type ENUM('ADOPCION', 'ACOGIDA', 'PASEO', 'VOLUNTARIADO') NOT NULL,
    start_date        DATE NOT NULL,
    end_date          DATE,
    active            TINYINT(1) NOT NULL,
    CONSTRAINT fk_relationship_user FOREIGN KEY (user_id) REFERENCES User(id),
    CONSTRAINT fk_relationship_pet  FOREIGN KEY (pet_id)  REFERENCES Pet(id)
);

CREATE TABLE IF NOT EXISTS Adoption_Detail (
    id                        BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_pet_relationship_id  BIGINT NOT NULL UNIQUE,
    adoption_date             DATE NOT NULL,
    place                     VARCHAR(255) NOT NULL,
    conditions                TEXT,
    issues                    TEXT,
    notes                     TEXT,
    CONSTRAINT fk_adoption_detail_relationship FOREIGN KEY (user_pet_relationship_id) REFERENCES User_Pet_Relationship(id)
);

CREATE TABLE IF NOT EXISTS Adoption_Form_Token (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    token      VARCHAR(255) NOT NULL UNIQUE,
    user_id    BIGINT NOT NULL,
    pet_id     BIGINT NOT NULL,
    created_at DATETIME NOT NULL,
    expires_at DATETIME NOT NULL,
    used       TINYINT(1) NOT NULL DEFAULT 0,
    CONSTRAINT fk_token_user FOREIGN KEY (user_id) REFERENCES User(id),
    CONSTRAINT fk_token_pet  FOREIGN KEY (pet_id)  REFERENCES Pet(id)
);

CREATE TABLE IF NOT EXISTS Adoption_Request (
    id                      BIGINT AUTO_INCREMENT PRIMARY KEY,
    adoption_form_token_id  BIGINT NOT NULL UNIQUE,
    address                 VARCHAR(255) NOT NULL,
    city                    VARCHAR(255) NOT NULL,
    housing_type            VARCHAR(255) NOT NULL,
    has_garden              TINYINT(1) NOT NULL,
    has_other_pets          TINYINT(1) NOT NULL,
    has_children            TINYINT(1) NOT NULL,
    hours_alone_per_day     INT NOT NULL,
    experience_with_pets    TINYINT(1) NOT NULL,
    reason_for_adoption     TEXT NOT NULL,
    agrees_to_follow_up     TINYINT(1) NOT NULL,
    additional_info         TEXT,
    status                  ENUM('PENDIENTE', 'APROBADA', 'DENEGADA') NOT NULL,
    submitted_at            DATETIME NOT NULL,
    CONSTRAINT fk_adoption_request_token FOREIGN KEY (adoption_form_token_id) REFERENCES Adoption_Form_Token(id)
);

CREATE TABLE IF NOT EXISTS Donation (
    id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id  BIGINT,
    date     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    category ENUM('MONETARIA','ALIMENTACION','MATERIAL','JUGUETES','MEDICAMENTOS','SUSCRIPCION','OTROS') NOT NULL,
    method   ENUM('EFECTIVO','TRANSFERENCIA','TARJETA','BIZUM','ESPECIE') NOT NULL,
    amount   DECIMAL(19, 2) NOT NULL,
    notes    TEXT,
    CONSTRAINT fk_donation_user FOREIGN KEY (user_id) REFERENCES User(id)
);

CREATE TABLE IF NOT EXISTS Event (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(100) NOT NULL,
    description   TEXT NOT NULL,
    event_date    DATETIME NOT NULL,
    location      VARCHAR(255) NOT NULL,
    image_url     VARCHAR(255),
    event_type    ENUM('ADOPCION','MERCADILLO','EDUCACION','RECAUDACION','OTRO') NOT NULL,
    status        ENUM('PROGRAMADO','EN_CURSO','FINALIZADO','CANCELADO') NOT NULL,
    max_capacity  INT,
    created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
