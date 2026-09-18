--liquibase formatted sql
--changeset Matheus:202609141517
--comment: blocks table create

CREATE TABLE BLOCKS (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    blockedCause VARCHAR(255) NOT NULL,
    blockedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    unblockedCause VARCHAR(255) NULL,
    unblockedAt TIMESTAMP NULL,
    card_id BIGINT NOT NULL,
    -- block table has a foreign key, card id, will delete upon deleting card id
    CONSTRAINT cards_block_fk FOREIGN KEY (card_id) REFERENCES CARDS(id) ON DELETE CASCADE
)engine=InnoDB;

--rollback DROP TABLE BLOCKS