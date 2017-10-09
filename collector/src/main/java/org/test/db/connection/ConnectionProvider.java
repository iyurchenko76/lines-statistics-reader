package org.test.db.connection;

import org.apache.commons.dbcp2.BasicDataSource;
import org.test.migration.LiquibaseMigrations;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionProvider {
    private static final String DB_URL_PROP = "db.url";
    private static final String DB_USERNAME_PROP = "db.username";
    private static final String DB_PASSWORD_PROP = "db.password";

    private static final String DB_URL_DEFAULT = "jdbc:h2:mem:single";
    private static final String DB_USERNAME_DEFAULT = "";
    private static final String DB_PASSWORD_DEFAULT = "";

    private static final String DB_PROPERTIES_FILE_NAME_PROP = "db.prop.location";
    private static final String DB_PROPERTIES_FILE_NAME = "db.properties";

    private final String dbUrl;
    private final String dbUserName;
    private final String dbPassword;

    private final BasicDataSource dataSource;

    private static final ConnectionProvider provider = new ConnectionProvider();


    private ConnectionProvider() {
        String propFileName = System.getProperty(DB_PROPERTIES_FILE_NAME_PROP, DB_PROPERTIES_FILE_NAME);
        Properties properties = new Properties();
        try {
            if (new File(propFileName).exists()) {
                properties.load(new FileInputStream(propFileName));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        dbUrl = System.getProperty(DB_URL_PROP, properties.getProperty(DB_URL_PROP, DB_URL_DEFAULT));
        dbUserName = System.getProperty(DB_USERNAME_PROP, properties.getProperty(DB_USERNAME_PROP, DB_USERNAME_DEFAULT));
        dbPassword = System.getProperty(DB_PASSWORD_PROP, properties.getProperty(DB_PASSWORD_PROP, DB_PASSWORD_DEFAULT));

        try (LiquibaseMigrations migrations = new LiquibaseMigrations(dbUrl, dbUserName, dbPassword)) {
            migrations.update();
            dataSource = new BasicDataSource();
            dataSource.setUrl(dbUrl);
            dataSource.setUsername(dbUserName);
            dataSource.setPassword(dbPassword);
            dataSource.setConnectionProperties("poolPreparedStatements=true;maxOpenPreparedStatements=20");
            dataSource.getConnection();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static ConnectionProvider getProvider() {
        return provider;
    }

    public static Connection getProviderConnection() throws SQLException {
        return getProvider().getConnection();
    }
}