package Board.Project;

import Board.Project.Persistence.Migrations.MigrationStrategy;
import Board.Project.UI.MainMenu;
import java.sql.SQLException;
import static Board.Project.Persistence.Config.ConnectionConfig.getConnection;

public class Main {
    public static void main(String[] args) throws  SQLException {

        try (var connection = getConnection()) {
            new MigrationStrategy(connection).executeMigration();
        }

        new MainMenu().execute();
    }
}
