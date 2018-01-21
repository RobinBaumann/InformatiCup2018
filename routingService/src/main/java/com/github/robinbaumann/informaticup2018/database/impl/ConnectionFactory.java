package com.github.robinbaumann.informaticup2018.database.impl;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.text.MessageFormat;

public class ConnectionFactory {
    private static DataSource dataSource;

    static {
        String host = System.getProperty("infocup.host", "localhost:3333");
        String user = System.getProperty("infocup.user", "infocup");
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(MessageFormat.format("jdbc:postgresql://{0}/infocup", host));
        config.setUsername(user);
        dataSource = new HikariDataSource(config);
    }

    public static DataSource getDataSource() {
        return dataSource;
    }
}
