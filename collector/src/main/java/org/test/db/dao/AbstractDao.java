package org.test.db.dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.test.db.connection.ConnectionProvider.getProviderConnection;

abstract public class AbstractDao<T> implements AutoCloseable {
    private ThreadLocal<Connection> connectionHolder;

    abstract public Object save(T object) throws SQLException;

    abstract public void remove(Object ... id) throws SQLException;

    abstract public T find(Object ... id) throws SQLException;

    protected Connection getConnection() throws SQLException {
        if (connectionHolder.get() == null) {
            connectionHolder.set(getProviderConnection());
        }
        return connectionHolder.get();
    }

    protected PreparedStatement prepareStatement(String sql) throws SQLException {
        return getConnection().prepareStatement(sql);
    }

    public void beginTransaction() throws SQLException {
        getConnection().setAutoCommit(false);
    }

    public void commitTransaction() throws SQLException {
        getConnection().commit();
    }

    public void rollbackTransaction() throws SQLException {
        getConnection().rollback();
    }

    @Override
    public void close() throws Exception {
        try {
            Connection connection = connectionHolder.get();
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            connectionHolder.remove();
        }
    }
}
