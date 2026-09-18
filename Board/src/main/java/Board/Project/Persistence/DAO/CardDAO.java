package Board.Project.Persistence.DAO;

import Board.Project.Persistence.Entity.BoardColumnEntity;
import Board.Project.Persistence.Entity.CardEntity;
import lombok.AllArgsConstructor;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static java.time.ZoneOffset.UTC;

@AllArgsConstructor
public class CardDAO {

    // Connection used to execute SQL commands
    private final Connection connection;

    // Inserts a new card into the CARDS table
    public CardEntity insert(final CardEntity entity) throws SQLException {

        var sql = "INSERT INTO CARDS (title, description, createdAt, board_column_id) " +
                  "VALUES (?, ?, ?, ?)";

        // Request the generated card ID
        try (var statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, entity.getTitle());
            statement.setString(2, entity.getDescription());

            // Convert OffsetDateTime into a SQL Timestamp
            statement.setTimestamp(3, Timestamp.valueOf(entity.getCreatedAt().toLocalDateTime()));

            // Assign the card to a board column
            statement.setLong(4, entity.getBoardColumn().getId());

            statement.executeUpdate();

            // Retrieve the generated card ID
            try (var resultSet = statement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    entity.setId(resultSet.getLong(1));
                }
            }
            return entity;
        }
    }

    // Updates an existing card
    public CardEntity update(final CardEntity entity) throws SQLException {

        var sql = "UPDATE CARDS " +
                  "SET title = ?, description = ?, createdAt = ?, board_column_id = ? " +
                  "WHERE id = ?";

        try (var statement = connection.prepareStatement(sql)) {
            statement.setString(1, entity.getTitle());
            statement.setString(2, entity.getDescription());
            statement.setTimestamp(3, Timestamp.valueOf(entity.getCreatedAt().toLocalDateTime()));

            // Allows the card to be moved to another column
            statement.setLong(4, entity.getBoardColumn().getId());

            // Identifies which card should be updated
            statement.setLong(5, entity.getId());

            statement.executeUpdate();

            return entity;
        }
    }

    // Deletes a card using its ID
    public void delete(final long id) throws SQLException {

        var sql = "DELETE FROM CARDS " +
                  "WHERE id = ?";

        try (var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.executeUpdate();
        }
    }

    // Retrieves every card from the database
    public List<CardEntity> findAll() throws SQLException {

        var sql = "SELECT * " +
                  "FROM CARDS";

        var cards = new ArrayList<CardEntity>();

        try (var statement = connection.prepareStatement(sql)) {
            var resultSet = statement.executeQuery();

            while (resultSet.next()) {
                var singularCard = new CardEntity();
                var singularBoardColumn = new BoardColumnEntity();

                singularCard.setId(resultSet.getLong("id"));
                singularCard.setTitle(resultSet.getString("title"));
                singularCard.setDescription(resultSet.getString("description"));

                // Convert SQL Timestamp into OffsetDateTime
                singularCard.setCreatedAt(resultSet.getTimestamp("createdAt").toInstant().atOffset(UTC));

                // Store the card's board column ID
                singularBoardColumn.setId(resultSet.getLong("board_column_id"));
                singularCard.setBoardColumn(singularBoardColumn);

                cards.add(singularCard);
            }
        }
        return cards;
    }

    // Finds one card by its ID
    public Optional<CardEntity> findById(final long id) throws SQLException {

        var sql = "SELECT id, title, description, createdAt, board_column_id " +
                  "FROM CARDS " +
                  "WHERE id = ?";

        try (var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);

            var resultSet = statement.executeQuery();

            try (resultSet) {
                if (resultSet.next()) {
                    var singularCard = new CardEntity();
                    var singularBoardColumn = new BoardColumnEntity();

                    singularCard.setId(resultSet.getLong("id"));
                    singularCard.setTitle(resultSet.getString("title"));
                    singularCard.setDescription(resultSet.getString("description"));
                    singularCard.setCreatedAt(resultSet.getTimestamp("createdAt").toInstant().atOffset(UTC));

                    // Initially, only the column ID is loaded.
                    // CardService can load the complete column afterward.
                    singularBoardColumn.setId(resultSet.getLong("board_column_id"));
                    singularCard.setBoardColumn(singularBoardColumn);

                    return Optional.of(singularCard);
                }
            }

        }
        // Returned when no card is found
        return Optional.empty();
    }

    // Finds all cards belonging to a specific board column
    public List<CardEntity> findByBoardColumnId(final long boardColumnId) throws SQLException {

        var sql = "SELECT * " +
                  "FROM CARDS " +
                  "WHERE board_column_id = ?";

        var cards = new ArrayList<CardEntity>();

        try (var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, boardColumnId);

            var resultSet = statement.executeQuery();

            try (resultSet) {
                while (resultSet.next()) {
                    var singularCard = new CardEntity();
                    var singularBoardColumn = new BoardColumnEntity();

                    singularCard.setId(resultSet.getLong("id"));
                    singularCard.setTitle(resultSet.getString("title"));
                    singularCard.setDescription(resultSet.getString("description"));
                    singularCard.setCreatedAt(resultSet.getTimestamp("createdAt").toInstant().atOffset(UTC));

                    singularBoardColumn.setId(resultSet.getLong("board_column_id"));
                    singularCard.setBoardColumn(singularBoardColumn);

                    cards.add(singularCard);
                }
            }

        }
        return cards;
    }

    // Checks whether a card exists with the given ID
    public boolean exists(final long id) throws SQLException {

        var sql = "SELECT 1 " +
                  "FROM CARDS " +
                  "WHERE id = ?";

        try (var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);

            statement.executeQuery();

            return statement.getResultSet().next();
        }
    }

}
