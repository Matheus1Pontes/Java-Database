--liquibase formatted sql
--changeset Matheus:202609141514
--comment: cards table create

CREATE TABLE CARDS (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    createdAt TIMESTAMP NOT NULL,
    board_column_id BIGINT NOT NULL,
    -- card table has a foreign key, board column id, will delete upon deleting board column id
    CONSTRAINT board_columns__cards_fk FOREIGN KEY (board_column_id) REFERENCES BOARD_COLUMNS(id) ON DELETE CASCADE
)engine=InnoDB;

--rollback DROP TABLE CARDS