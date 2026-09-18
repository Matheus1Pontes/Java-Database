package Board.Project.Persistence.Config;

import lombok.NoArgsConstructor;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class ConnectionConfig {
    // connecting to MySQL database, the name of the database is board
    // user and password will be different for each person
    // not good practice to connect to a root user for security reasons, in the future use SSH keys
    public static Connection getConnection() throws SQLException {
        var url = "jdbc:mysql://localhost:3306/board?allowPublicKeyRetrieval=true&useSSL=false";
        var user = "root";
        var password = "admin@123";

        var connection = DriverManager.getConnection(url, user, password);
        // will not commit any SQL until manually commiting in the code
        connection.setAutoCommit(false);
        return connection;
    }
}
