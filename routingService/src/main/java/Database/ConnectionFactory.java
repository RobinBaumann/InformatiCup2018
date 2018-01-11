package Database;

import org.postgresql.ds.PGPoolingDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionFactory {
    private static DataSource dataSource;

    static {
        PGPoolingDataSource source = new PGPoolingDataSource();
        source.setDataSourceName("infocup datasource");
        source.setServerName("localhost");
        source.setDatabaseName("infocup");
        source.setUser("infocup");
        source.setPortNumber(3333);
        source.setMaxConnections(10);
        ConnectionFactory.dataSource = source;
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
