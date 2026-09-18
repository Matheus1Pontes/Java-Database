package Board.Project.Persistence.DAO;

import Board.Project.Persistence.Entity.BoardColumnEntity;
import Board.Project.Persistence.Entity.BoardEntity;
import Board.Project.Persistence.Entity.KindEnum;
import lombok.AllArgsConstructor;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

@AllArgsConstructor
public class BoardColumnDAO {

    // Connection used to communicate with the database
    private final Connection connection;

    // Inserts a new board column into the database
    public BoardColumnEntity insert(final BoardColumnEntity entity) throws SQLException {
        // `order` is escaped because ORDER is an SQL keyword
        var sql = "INSERT INTO BOARD_COLUMNS (name, kind, `order`, board_id) " +
                  "VALUES (?, ?, ?, ?)";

        // RETURN_GENERATED_KEYS allows to retrieve the new column ID
        try (var statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, entity.getName());
            statement.setString(2, entity.getKind().name());
            statement.setInt(3, entity.getOrder());

            // Uses the ID of the board that owns this column
            statement.setLong(4, entity.getBoard().getId());

            statement.executeUpdate();

            // Retrieve and assign the generated column ID
            try (var resultSet = statement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    entity.setId(resultSet.getLong(1));
                }
            }
            return entity;
        }
    }

    // Updates a column's name, kind, and order
    public BoardColumnEntity update(final BoardColumnEntity entity) throws SQLException {

        var sql = "UPDATE BOARD_COLUMNS " +
                  "SET name = ?, kind = ?, `order` = ? " +
                  "WHERE id = ?";

        try (var statement = connection.prepareStatement(sql)) {
            statement.setString(1, entity.getName());
            statement.setString(2, entity.getKind().name());
            statement.setInt(3, entity.getOrder());
            statement.setLong(4, entity.getId());

            statement.executeUpdate();
            return entity;
        }
    }

    // Deletes a board column using its ID
    public void delete(final long id) throws SQLException {

        var sql = "DELETE FROM BOARD_COLUMNS " +
                  "WHERE id = ?";

        try (var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.executeUpdate();
        }
    }

    // Retrieves every board column from the database
    public List<BoardColumnEntity> findAll() throws SQLException {

        var sql = "SELECT * " +
                  "FROM BOARD_COLUMNS";

        var board_columns = new ArrayList<BoardColumnEntity>();

        try (var statement = connection.prepareStatement(sql)) {
            var resultSet = statement.executeQuery();

            // Convert every database row into a BoardColumnEntity
            while (resultSet.next()) {
                var singularBoardColumn = new BoardColumnEntity();
                var singularBoard = new BoardEntity();

                singularBoardColumn.setId(resultSet.getLong("id"));
                singularBoardColumn.setName(resultSet.getString("name"));
                singularBoardColumn.setKind(KindEnum.valueOf(resultSet.getString("kind")));
                singularBoardColumn.setOrder(resultSet.getInt("order"));

                // Recreate the board relationship using board_id
                singularBoard.setId(resultSet.getLong("board_id"));
                singularBoardColumn.setBoard(singularBoard);

                board_columns.add(singularBoardColumn);
            }
        }
        return board_columns;
    }

    // Finds one board column by its ID
    public Optional<BoardColumnEntity> findById(final long id) throws SQLException {

        var sql = "SELECT id, name, kind, `order`, board_id " +
                  "FROM BOARD_COLUMNS " +
                  "WHERE id = ?";

        try (var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);

            var resultSet = statement.executeQuery();

            try (resultSet) {
                if (resultSet.next()) {
                    var singularBoardColumn = new BoardColumnEntity();
                    var singularBoard = new BoardEntity();

                    singularBoardColumn.setId(resultSet.getLong("id"));
                    singularBoardColumn.setName(resultSet.getString("name"));
                    singularBoardColumn.setKind(KindEnum.valueOf(resultSet.getString("kind")));
                    singularBoardColumn.setOrder(resultSet.getInt("order"));

                    singularBoard.setId(resultSet.getLong("board_id"));
                    singularBoardColumn.setBoard(singularBoard);

                    return Optional.of(singularBoardColumn);
                }
            }

        }
        // Returned when no column matches the given ID
        return Optional.empty();
    }

    // Finds all columns belonging to one specific board
    public List<BoardColumnEntity> findByBoardId(final long board_id) throws SQLException {

        var sql = "SELECT * " +
                  "FROM BOARD_COLUMNS " +
                  "WHERE board_id = ?";

        var board_columns = new ArrayList<BoardColumnEntity>();

        try (var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, board_id);

            var resultSet = statement.executeQuery();

            try (resultSet) {
                while (resultSet.next()) {
                    var singularBoardColumn = new BoardColumnEntity();
                    var singularBoard = new BoardEntity();

                    singularBoardColumn.setId(resultSet.getLong("id"));
                    singularBoardColumn.setName(resultSet.getString("name"));
                    singularBoardColumn.setKind(KindEnum.valueOf(resultSet.getString("kind")));
                    singularBoardColumn.setOrder(resultSet.getInt("order"));

                    // Associate the column with its board
                    singularBoard.setId(resultSet.getLong("board_id"));
                    singularBoardColumn.setBoard(singularBoard);

                    board_columns.add(singularBoardColumn);
                }
            }
        }
        return board_columns;
    }

    // Checks whether a column exists with the given ID
    public boolean exists(final long id) throws SQLException {

        var sql = "SELECT 1 " +
                  "FROM BOARD_COLUMNS " +
                  "WHERE id = ?";

        try (var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);

            statement.executeQuery();

            return statement.getResultSet().next();

        }
    }

}
