package Database;

import com.noelherrick.jell.Jell;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JellFactory {
    public static Jell setUpConnection(String url, String username, String password) throws SQLException {
        Connection conn = DriverManager.getConnection(url, username, password);
        return new Jell(conn);
    }
}
