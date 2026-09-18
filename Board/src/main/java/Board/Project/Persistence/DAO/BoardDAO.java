package Board.Project.Persistence.DAO;

import Board.Project.Persistence.Entity.BoardEntity;
import lombok.AllArgsConstructor;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class BoardDAO {

    // Connection used to communicate with the database
    private final Connection connection;

    // Inserts a new board into the BOARDS table
    public BoardEntity insert(final BoardEntity entity) throws SQLException {

        var sql = "INSERT INTO BOARDS (name) " +
                  "VALUES (?)";

        // RETURN_GENERATED_KEYS allows to retrieve the new board ID
        try (var statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, entity.getName());
            statement.executeUpdate();

            // Retrieve the auto-generated ID
            try (var resultSet = statement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    entity.setId(resultSet.getLong(1));
                }
            }
            return entity;
        }
    }

    // Updates an existing board's name
    public BoardEntity update(final BoardEntity entity) throws SQLException {

        var sql = "UPDATE BOARDS " +
                  "SET name = ? " +
                  "WHERE id = ?";

        try (var statement = connection.prepareStatement(sql)) {

            statement.setString(1, entity.getName());
            statement.setLong(2, entity.getId());

            statement.executeUpdate();

            return entity;
        }
    }

    // Deletes a board using its ID
    public void delete(final long id) throws SQLException {

        var sql = "DELETE FROM BOARDS " +
                  "WHERE id = ?";

        try (var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.executeUpdate();
        }
    }

    // Retrieves every board from the BOARDS table
    public List<BoardEntity> findAll() throws SQLException {

        var sql = "SELECT * " +
                  "FROM BOARDS";

        var boards = new ArrayList<BoardEntity>();

        try (var statement = connection.prepareStatement(sql)) {
            var resultSet = statement.executeQuery();

            // Convert every database row into a BoardEntity
            while (resultSet.next()) {
                var singularBoard = new BoardEntity();

                singularBoard.setId(resultSet.getLong("id"));
                singularBoard.setName(resultSet.getString("name"));

                boards.add(singularBoard);
            }
        }
        return boards;
    }

    // Finds one board by ID
    // Optional is empty when no board is found
    public Optional<BoardEntity> findById(final long id) throws SQLException {

        var sql = "SELECT id, name " +
                  "FROM BOARDS " +
                  "WHERE id = ?";

        try (var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);

            var resultSet = statement.executeQuery();

            try (resultSet) {
                if (resultSet.next()) {
                    var board = new BoardEntity();

                    board.setId(resultSet.getLong("id"));
                    board.setName(resultSet.getString("name"));

                    return Optional.of(board);
                }
            }

        }
        return Optional.empty();
    }

    // Checks whether a board exists with the given ID
    public boolean exists(final long id) throws SQLException {

        var sql = "SELECT 1 " +
                  "FROM BOARDS " +
                  "WHERE id = ?";

        try (var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);

            statement.executeQuery();

            return statement.getResultSet().next();
        }
    }

}
