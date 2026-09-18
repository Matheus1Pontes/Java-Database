--liquibase formatted sql
--changeset Matheus:202609141429
--comment: boards table create

CREATE TABLE BOARDS (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
)engine=InnoDB;

--rollback DROP TABLE BOARDS