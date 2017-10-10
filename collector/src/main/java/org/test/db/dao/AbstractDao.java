package org.test.db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.test.db.connection.ConnectionProvider.getProviderConnection;

abstract public class AbstractDao implements AutoCloseable {
    private final ThreadLocal<Map<PreparedStatement, BatchCounter>> batchStatementMap =
            ThreadLocal.withInitial(HashMap<PreparedStatement, BatchCounter>::new);
    private final ThreadLocal<Connection> connectionHolder = new ThreadLocal<>();
    private final ThreadLocal<Map<String, PreparedStatement>> statementHolder =
            ThreadLocal.withInitial(HashMap<String, PreparedStatement>::new);
    private boolean batchIsEnabled;
    private int batchSize;


    protected Connection getConnection() throws SQLException {
        Connection connection = connectionHolder.get();
        if (connection == null) {
            connection = getProviderConnection();
            connectionHolder.set(connection);
        }
        return connection;
    }

    protected PreparedStatement prepareStatement(String sql) throws SQLException {
        PreparedStatement statement = statementHolder.get().get(sql);
        if (statement == null) {
            statement = getConnection().prepareStatement(sql);
            statementHolder.get().put(sql, statement);
        }
        return statement;
    }

    public synchronized void beginBatch(int size) {
        if (batchIsEnabled) {
            throw new IllegalStateException("Batch is already started");
        }
        this.batchIsEnabled = true;
        this.batchSize = size;
    }

    public boolean batchIsEnabled() {
        return batchIsEnabled;
    }

    public int endBatch() throws SQLException {
        batchIsEnabled = false;
        int result = batchStatementMap.get().entrySet().stream()
                .filter(entry -> entry.getValue().getValue() > 0)
                .map(entry -> {
                    try {
                        return Arrays.stream(entry.getKey().executeBatch()).sum();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }).reduce(0, Integer::sum);
        commitTransaction();
        batchStatementMap.get().clear();
        return result;
    }

    protected int executeInBatch(PreparedStatement statement) throws SQLException {
        BatchCounter counter = batchStatementMap.get().get(statement);
        if (counter == null) {
            counter = new BatchCounter();
            batchStatementMap.get().put(statement, counter);
        }
        if (counter.getValue() == 0) {
            beginTransaction();
        }
        statement.addBatch();
        int result = 0;
        if (counter.incrementAndGet() == batchSize) {
            result = Arrays.stream(statement.executeBatch()).sum();
            commitTransaction();
            batchStatementMap.get().remove(statement);
        }
        return result;
    }

    protected int executeSingle(PreparedStatement statement) throws SQLException {
        return statement.executeUpdate();
    }

    public void beginTransaction() throws SQLException {
        getConnection().setAutoCommit(false);
    }

    public void commitTransaction() throws SQLException {
        Connection connection = getConnection();
        connection.commit();
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