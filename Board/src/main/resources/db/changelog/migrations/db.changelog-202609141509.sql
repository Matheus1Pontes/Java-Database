--liquibase formatted sql
--changeset Matheus:202609141509
--comment: board_columns table create

CREATE TABLE BOARD_COLUMNS (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    kind VARCHAR(10) NOT NULL,
    `order` INT NOT NULL,
    board_id BIGINT NOT NULL,
    -- board column table has a foreign key, board id, will delete upon deleting the board id
    CONSTRAINT boards__boards_columns_fk FOREIGN KEY (board_id) REFERENCES BOARDS(id) ON DELETE CASCADE,
    CONSTRAINT id_order_uk UNIQUE KEY unique_board_id_order (board_id, `order`)
)engine=InnoDB;

--rollback DROP TABLE BOARD_COLUMNS