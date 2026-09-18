package Board.Project.Persistence.DAO;

import Board.Project.Persistence.Entity.BlockEntity;
import Board.Project.Persistence.Entity.CardEntity;
import lombok.AllArgsConstructor;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.time.ZoneOffset.UTC;

@AllArgsConstructor
public class BlockDAO {

    // Connection used to execute database commands
    private final Connection connection;

    // Inserts a new block
    public BlockEntity insert(final BlockEntity entity) throws SQLException {

        var sql = "INSERT INTO BLOCKS (blockedCause, blockedAt, unblockedCause, unblockedAt, card_id) " +
                  "VALUES (?, ?, ?, ?, ?)";

        // Request the generated block ID
        try (var statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, entity.getBlockedCause());
            // Store the time when the card was blocked
            statement.setTimestamp(2, Timestamp.from(entity.getBlockedAt().toInstant()));

            // A new block does not have an unblocking cause yet
            if (entity.getUnblockedCause() == null) {
                statement.setNull(3, java.sql.Types.VARCHAR);
            } else {
                statement.setString(3, entity.getUnblockedCause());
            }

            // A new block does not have an unblocking time yet
            if (entity.getUnblockedAt() == null) {
                statement.setNull(4, java.sql.Types.TIMESTAMP);
            } else {
                statement.setTimestamp(
                        4,
                        Timestamp.from(entity.getUnblockedAt().toInstant())
                );
            }

            // Associate the block with a card
            statement.setLong(5, entity.getCard().getId());

            statement.executeUpdate();

            // Retrieve and assign the generated block ID
            try (var resultSet = statement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    entity.setId(resultSet.getLong(1));
                }
            }
            return entity;
        }
    }

    // Updates an existing block record
    public BlockEntity update(final BlockEntity entity) throws SQLException {

        var sql = "UPDATE BLOCKS " +
                  "SET blockedCause = ?, blockedAt = ?, unblockedCause = ?, unblockedAt = ? " +
                  "WHERE id = ?";

        try (var statement = connection.prepareStatement(sql)) {
            statement.setString(1, entity.getBlockedCause());

            // Update the original blocking time
            statement.setTimestamp(2, Timestamp.valueOf(entity.getBlockedAt().toLocalDateTime()));
            statement.setString(3, entity.getUnblockedCause());

            // Update the unblocking time
            statement.setTimestamp(4, Timestamp.valueOf(entity.getUnblockedAt().toLocalDateTime()));

            // Identify which block record should be updated
            statement.setLong(5, entity.getId());

            statement.executeUpdate();

            return entity;
        }
    }

    // Deletes a block record by its ID
    public void delete(final long id) throws SQLException {

        var sql = "DELETE FROM BLOCKS " +
                  "WHERE id = ?";

        try (var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.executeUpdate();
        }
    }

    // Retrieves every block record
    public List<BlockEntity> findAll() throws SQLException {

        var sql = "SELECT * " +
                  "FROM BLOCKS";

        var blocks = new ArrayList<BlockEntity>();

        try (var statement = connection.prepareStatement(sql)) {
            var resultSet = statement.executeQuery();

            while (resultSet.next()) {
                var singularBlock = new BlockEntity();
                var singularCard = new CardEntity();

                singularBlock.setId(resultSet.getLong("id"));
                singularBlock.setBlockedCause(resultSet.getString("blockedCause"));
                singularBlock.setBlockedAt(resultSet.getTimestamp("blockedAt").toInstant().atOffset(UTC));
                singularBlock.setUnblockedCause(resultSet.getString("unblockedCause"));
                singularBlock.setUnblockedAt(resultSet.getTimestamp("unblockedAt").toInstant().atOffset(UTC));

                // Recreate the card relationship using card_id
                singularCard.setId(resultSet.getLong("card_id"));
                singularBlock.setCard(singularCard);

                blocks.add(singularBlock);
            }
        }
        return blocks;
    }

    // Finds one block record by its ID
    public Optional<BlockEntity> findById(final long id) throws SQLException {

        var sql = "SELECT id, blockedCause, blockedAt, unblockedCause, unblockedAt, card_id " +
                  "FROM BLOCKS " +
                  "WHERE id = ?";

        try (var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);

            var resultSet = statement.executeQuery();

            try (resultSet) {
                if (resultSet.next()) {
                    var singularBlock = new BlockEntity();
                    var singularCard = new CardEntity();

                    singularBlock.setId(resultSet.getLong("id"));
                    singularBlock.setBlockedCause(resultSet.getString("blockedCause"));
                    singularBlock.setBlockedAt(resultSet.getTimestamp("blockedAt").toInstant().atOffset(UTC));
                    singularBlock.setUnblockedCause(resultSet.getString("unblockedCause"));
                    singularBlock.setUnblockedAt(resultSet.getTimestamp("unblockedAt").toInstant().atOffset(UTC));

                    // Recreate the card relationship
                    singularCard.setId(resultSet.getLong("card_id"));
                    singularBlock.setCard(singularCard);

                    return Optional.of(singularBlock);
                }
            }

        }
        // Returned when no block is found
        return Optional.empty();
    }

    // Finds all blocks belonging to a specific card
    public List<BlockEntity> findByCardId(final long cardId) throws SQLException {

        var sql = "SELECT * " +
                  "FROM BLOCKS " +
                  "WHERE card_id = ?";

        var blocks = new ArrayList<BlockEntity>();

        try (var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, cardId);

            var resultSet = statement.executeQuery();

            try (resultSet) {
                while (resultSet.next()) {
                    var singularBlock = new BlockEntity();
                    var singularCard = new CardEntity();

                    singularBlock.setId(resultSet.getLong("id"));
                    singularBlock.setBlockedCause(resultSet.getString("blockedCause"));
                    singularBlock.setBlockedAt(resultSet.getTimestamp("blockedAt").toInstant().atOffset(UTC));
                    singularBlock.setUnblockedCause(resultSet.getString("unblockedCause"));
                    singularBlock.setUnblockedAt(resultSet.getTimestamp("unblockedAt").toInstant().atOffset(UTC));

                    // Recreate the card relationship
                    singularCard.setId(resultSet.getLong("card_id"));
                    singularBlock.setCard(singularCard);

                    blocks.add(singularBlock);
                }
            }

        }
        return blocks;
    }

    // Marks the active block as unblocked for a specific card
    public void unblockByCardId(final long cardId, final String cause, final OffsetDateTime unblockedAt) throws SQLException {
        var sql = "UPDATE BLOCKS " +
                  "SET unblockedCause = ?, unblockedAt = ? " +
                  "WHERE card_id = ? " +
                  "AND unblockedAt IS NULL";

        try (var statement = connection.prepareStatement(sql)) {
            statement.setString(1, cause);
            statement.setTimestamp(2, Timestamp.from(unblockedAt.toInstant()));
            statement.setLong(3, cardId);

            statement.executeUpdate();
        }
    }

    // Checks whether a block record exists with the given ID
    public boolean exists(final long id) throws SQLException {
        var sql = "SELECT 1 " +
                  "FROM BLOCKS " +
                  "WHERE id = ?";

        try (var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);

            statement.executeQuery();

            return statement.getResultSet().next();
        }
    }

    // Checks whether a card currently has an active block
    public boolean isBlocked(final long cardId) throws SQLException {
        var sql = "SELECT 1 " +
                  "FROM BLOCKS " +
                  "WHERE card_id = ? " +
                  "AND unblockedAt is NULL";

        try (var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, cardId);

            var resultSet = statement.executeQuery();
            return resultSet.next();
        }
    }

}
