package org.test.db.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FileDao extends AbstractDao {
    private static final String INSERT_FILE_SQL = "insert into files (file_name) values (?)";
    private static final String FIND_FILE_BY_NAME_SQL = "select file_id from files where file_name = ?";

    public int saveFile(String fileName) throws SQLException {
        PreparedStatement preparedStatement = prepareStatement(INSERT_FILE_SQL);
        preparedStatement.setString(1, fileName);
        preparedStatement.executeUpdate();
        return findFileByName(fileName);
    }

    public int findFileByName(String fileName) throws SQLException {
        PreparedStatement preparedStatement = prepareStatement(FIND_FILE_BY_NAME_SQL);
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            resultSet.first();
            return resultSet.getInt(1);
        }
    }
}
