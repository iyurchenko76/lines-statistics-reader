package org.test.db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.function.Consumer;

import static org.test.db.connection.ConnectionProvider.getProviderConnection;

abstract public class AbstractDao implements AutoCloseable {
    private ThreadLocal<Connection> connectionHolder = new ThreadLocal<>();
    private ThreadLocal<BatchCounter> batchCounterHolder = new ThreadLocal<>();

    private volatile boolean batchIsEnabled;


    protected Connection getConnection() throws SQLException {
        Connection connection = connectionHolder.get();
        if (connection == null) {
            connection = getProviderConnection();
            connectionHolder.set(connection);
        }
        return connection;
    }

    protected PreparedStatement prepareStatement(String sql) throws SQLException {
        return getConnection().prepareStatement(sql);
    }

    protected void executeInBatch(String sql, Consumer<PreparedStatement> consumer) {
        // TODO: implement batch storing
    }

    public void beginTransaction() throws SQLException {
        getConnection().setAutoCommit(false);
    }

    public void commitTransaction() throws SQLException {
        Connection connection = getConnection();
        connection.commit();
        connection.setAutoCommit(true);
    }

    public void rollbackTransaction() throws SQLException {
        Connection connection = getConnection();
        connection.rollback();
        connection.setAutoCommit(true);
    }

    @Override
    public void close() throws Exception {
        try {
            Connection connection = connectionHolder.get();
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            connectionHolder.remove();
        }
    }
}
