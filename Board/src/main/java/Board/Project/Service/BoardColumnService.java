package Board.Project.Service;

import Board.Project.Persistence.DAO.BoardColumnDAO;
import Board.Project.Persistence.Entity.BoardColumnEntity;
import lombok.AllArgsConstructor;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class BoardColumnService {

    private final Connection connection;

    // Inserts the board column
    public BoardColumnEntity insert(final BoardColumnEntity entity) throws SQLException {

        var boardColumnDAO = new BoardColumnDAO(connection);

        try {
            var insertedEntity = boardColumnDAO.insert(entity);
            connection.commit();
            return insertedEntity;

        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }

    // Updates the board column
    public BoardColumnEntity update(final BoardColumnEntity entity) throws SQLException {

        var boardColumnDAO = new BoardColumnDAO(connection);

        try {
            var updatedEntity = boardColumnDAO.update(entity);
            connection.commit();
            return updatedEntity;

        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }

    // Returns all board columns
    public List<BoardColumnEntity> findAll() throws SQLException {

        var boardColumnDAO = new BoardColumnDAO(connection);
        return boardColumnDAO.findAll();


    }

    // Finds the column by its ID
    public Optional<BoardColumnEntity> findById(final long id) throws SQLException {

        var boardColumnDAO = new BoardColumnDAO(connection);

        try {
            return boardColumnDAO.findById(id);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    // Returns all columns belonging to the board
    public List<BoardColumnEntity> findByBoardId(final long board_id) throws SQLException {

        var boardColumnDAO = new BoardColumnDAO(connection);

        try {
            return boardColumnDAO.findByBoardId(board_id);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    // Deletes the board column
    public void delete(final long id) throws SQLException {

        var boardColumnDAO = new BoardColumnDAO(connection);

        try {
            boardColumnDAO.delete(id);
            connection.commit();

        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }

}
