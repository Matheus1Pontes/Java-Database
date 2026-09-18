package Board.Project.Persistence.Migrations;

import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.AllArgsConstructor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.SQLException;

import static Board.Project.Persistence.Config.ConnectionConfig.getConnection;

@AllArgsConstructor
public class MigrationStrategy {

    // Database connection passed into this class
    private final Connection connection;

    public void executeMigration() {

        var originalOut = System.out;
        var originalError = System.err;

        try (var fos = new FileOutputStream("liquibase.log")) {
            // Redirect normal output and error messages to liquibase.log
            System.setOut(new PrintStream(fos));
            System.setErr(new PrintStream(fos));

            try (
                    // Open a database connection for Liquibase
                    var connection = getConnection();
                    // Wrap the JDBC connection for Liquibase
                    var jdbcConnection = new JdbcConnection(connection);
            ) {
                // Create Liquibase using the master changelog file
                var liquibase = new Liquibase(
                        "db/changelog/db.changelog-master.yml",
                        new ClassLoaderResourceAccessor(),
                        jdbcConnection);
                // Execute all pending database migrations
                liquibase.update();

            } catch (SQLException | LiquibaseException ex) {
                // Print database or Liquibase errors to the log file
                ex.printStackTrace();
            }
            // Restore the original error output stream
            System.setErr(originalError);

        } catch (IOException ex) {
            // Handle errors related to creating or writing to the log file
            ex.printStackTrace();
        } finally {
            // Always restore the original console output streams
            System.setOut(originalOut);
            System.setErr(originalError);
        }
    }
}
