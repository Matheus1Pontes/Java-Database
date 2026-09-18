package Board.Project.Service;

import Board.Project.Persistence.DAO.BlockDAO;
import Board.Project.Persistence.Entity.BlockEntity;
import lombok.AllArgsConstructor;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class BlockService {

    private final Connection connection;

    public BlockEntity insert(final BlockEntity entity) throws SQLException {

        var blockDAO = new BlockDAO(connection);

        try {
            var insertedEntity = blockDAO.insert(entity);
            connection.commit();
            return insertedEntity;

        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }

    public BlockEntity update(final BlockEntity entity) throws SQLException {

        var blockDAO = new BlockDAO(connection);

        try {
            var updatedEntity = blockDAO.update(entity);
            connection.commit();
            return updatedEntity;

        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }

    public List<BlockEntity> findAll() throws SQLException {

        var blockDAO = new BlockDAO(connection);
        return blockDAO.findAll();

    }

    public Optional<BlockEntity> findById(final long id) throws SQLException {

        var blockDAO = new BlockDAO(connection);

        try {
            return blockDAO.findById(id);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public List<BlockEntity> findByCardId(final long card_id) throws SQLException {

        var blockDAO = new BlockDAO(connection);

        try {
            return blockDAO.findByCardId(card_id);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    public void delete(final long id) throws SQLException {

        var blockDAO = new BlockDAO(connection);

        try {
            blockDAO.delete(id);
            connection.commit();

        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }

    public void unblock(long cardId, String cause) throws SQLException {

        var blockDAO = new BlockDAO(connection);

        try {
            blockDAO.unblockByCardId(cardId, cause, OffsetDateTime.now());
            connection.commit();

        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }

    public boolean isBlocked(long cardId) throws SQLException {
        var blockDAO = new BlockDAO(connection);
        try {
            return blockDAO.isBlocked(cardId);

        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }

}
