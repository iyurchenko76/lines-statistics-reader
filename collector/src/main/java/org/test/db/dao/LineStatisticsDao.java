package org.test.db.dao;

import org.test.input.pojo.Line;
import org.test.stat.pojo.LineStatistics;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class LineStatisticsDao extends AbstractDao {
    private static final String REMOVE_ALL_LINES_BY_FILE_ID_SQL = "delete from lines where file_id = ?";
    private static final String SAVE_LINE_STATISTICS_SQL = "insert into lines" +
            " (file_id, line_pos_indicator, line_src, line_longest_word, line_shortest_word, line_length," +
            " line_avg_word_length)" +
            " values (?, ?, ?, ?, ?, ?, ?)";
    private static final String FIND_ALL_LINES_BY_FILE_ID_SQL = "select line_pos_indicator, line_src, line_longest_word," +
            " line_shortest_word, line_length, line_avg_word_length from lines where file_id = ?";

    public int removeAllLinesForFileId(int fileId) throws SQLException {
        PreparedStatement statement = prepareStatement(REMOVE_ALL_LINES_BY_FILE_ID_SQL);
            statement.setInt(1, fileId);
            return statement.executeUpdate();
    }

    public int saveLineStatistics(int fileId, LineStatistics lineStatistics) throws SQLException {
        PreparedStatement statement = prepareStatement(SAVE_LINE_STATISTICS_SQL);
        statement.setInt(1, fileId);
        statement.setLong(2, lineStatistics.getLine().getPosIndicator());
        statement.setString(3, lineStatistics.getLine().getSource());
        statement.setString(4, lineStatistics.getLongestWord());
        statement.setString(5, lineStatistics.getShortestWord());
        statement.setInt(6, lineStatistics.getLength());
        statement.setInt(7, lineStatistics.getAverageWordLength());

        if (batchIsEnabled()) {
            return executeInBatch(statement);
        } else {
            return executeSingle(statement);
        }
    }

    public Stream<LineStatistics> getLineStatisticsByFileId(int fileId) throws SQLException {
        PreparedStatement statement = prepareStatement(FIND_ALL_LINES_BY_FILE_ID_SQL);
        statement.setInt(1, fileId);
        ResultSet resultSet = statement.executeQuery();
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(new Iterator<LineStatistics>() {
            private boolean hasNext = resultSet.first();

            @Override
            public boolean hasNext() {
                return hasNext;
            }

            @Override
            public LineStatistics next() {
                try {
                    LineStatistics lineStatistics = new LineStatistics(
                            new Line(resultSet.getLong(1), resultSet.getString(2)),
                            resultSet.getString(3),
                            resultSet.getString(4),
                            resultSet.getInt(5),
                            resultSet.getInt(6));
                    hasNext = resultSet.next();
                    if (!hasNext) {
                        resultSet.close();
                    }
                    return lineStatistics;
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }, 0), false);
    }
}