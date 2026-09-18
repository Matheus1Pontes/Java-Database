package Board.Project.Service;

import Board.Project.Persistence.DAO.BoardColumnDAO;
import Board.Project.Persistence.DAO.BoardDAO;
import Board.Project.Persistence.Entity.BoardEntity;
import lombok.AllArgsConstructor;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class BoardService {

    private final Connection connection;

    // Inserts a board and its columns
    public BoardEntity insert(BoardEntity entity) throws SQLException {

        var boardDAO = new BoardDAO(connection);
        var boardColumnDAO = new BoardColumnDAO(connection);

        try {
            // Insert the board first to generate its ID
            var insertedEntity = boardDAO.insert(entity);

            // Insert each column associated with the board
            for (var column : entity.getColumns()) {
                column.setBoard(insertedEntity);
                boardColumnDAO.insert(column);
            }

            connection.commit();
            return insertedEntity;

        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }

    // Updates the board
    public BoardEntity update(final BoardEntity entity) throws SQLException {

        var boardDAO = new BoardDAO(connection);

        try {
            var updatedEntity = boardDAO.update(entity);
            connection.commit();
            return updatedEntity;

        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }

    // Returns all boards
    public List<BoardEntity> findAll() throws SQLException {

        var boardDAO = new BoardDAO(connection);
        return boardDAO.findAll();
    }

    // Finds the board and loads its columns
    public Optional<BoardEntity> findById(final long id) throws SQLException {

        var boardDAO = new BoardDAO(connection);
        var boardColumnDAO = new BoardColumnDAO(connection);

        var board = boardDAO.findById(id);

        if (board.isPresent()) {
            var boardEntity = board.get();

            var columns = boardColumnDAO.findByBoardId(id);
            boardEntity.setColumns(columns);

            // Connect each column back to the board
            for (var column : columns) {
                column.setBoard(boardEntity);
            }
        }
        return board;
    }

    // Deletes the board
    public void delete(final long id) throws SQLException {

        var boardDAO = new BoardDAO(connection);

        try {
            boardDAO.delete(id);
            connection.commit();

        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }

}
