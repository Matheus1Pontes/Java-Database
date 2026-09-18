package Board.Project.Service;

import Board.Project.Persistence.DAO.BoardColumnDAO;
import Board.Project.Persistence.DAO.CardDAO;
import Board.Project.Persistence.Entity.CardEntity;
import lombok.AllArgsConstructor;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class CardService {

    private final Connection connection;

    public CardEntity insert(final CardEntity entity) throws SQLException {

        var cardDAO = new CardDAO(connection);

        try {
            var insertedEntity = cardDAO.insert(entity);
            connection.commit();
            return insertedEntity;

        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }

    public CardEntity update(final CardEntity entity) throws SQLException {

        var cardDAO = new CardDAO(connection);

        try {
            var updatedEntity = cardDAO.update(entity);
            connection.commit();
            return updatedEntity;

        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }

    public List<CardEntity> findAll() throws SQLException {

        var cardDAO = new CardDAO(connection);
        return cardDAO.findAll();
    }

    public Optional<CardEntity> findById(final long id) throws SQLException {

        var cardDAO = new CardDAO(connection);
        var boardColumnDAO = new BoardColumnDAO(connection);

        var card = cardDAO.findById(id);

        if (card.isPresent()) {
            var cardEntity = card.get();

            var column = boardColumnDAO.findById(cardEntity.getBoardColumn().getId());

            if (column.isPresent()) {
                cardEntity.setBoardColumn(column.get());
            }
        }
        return card;
    }

    public List<CardEntity> findByBoardColumnId(final long board_column_id) throws SQLException {

        var cardDAO = new CardDAO(connection);

        try {
            return cardDAO.findByBoardColumnId(board_column_id);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    public void delete(final long id) throws SQLException {

        var cardDAO = new CardDAO(connection);

        try {
            cardDAO.delete(id);
            connection.commit();

        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }

}
