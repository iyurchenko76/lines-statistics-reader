package org.test.input.file;

import org.test.db.dao.LineStatisticsDao;
import org.test.input.LineStreamer;
import org.test.stat.LineStatisticsBuilder;

import java.io.Closeable;
import java.io.IOException;
import java.sql.SQLException;

public class StatisticsCollector implements Closeable {
    private final int fileId;
    private final LineStreamer streamer;
    private final LineStatisticsDao lineStatisticsDao = new LineStatisticsDao();
    private final LineStatisticsBuilder lineStatisticsBuilder = new LineStatisticsBuilder();

    public StatisticsCollector(LineStreamer streamer, int fileId) {
        this.streamer = streamer;
        this.fileId = fileId;
    }

    public long collect() {
        try {
            lineStatisticsDao.removeAllLinesForFileId(fileId);
            lineStatisticsDao.beginBatch(100);
            int result = streamer.stream(false)
                    .map(lineStatisticsBuilder::buildStatistics)
                    .map(lineStatistics -> {
                        try {
                            return lineStatisticsDao.saveLineStatistics(fileId, lineStatistics);
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }).reduce(0, Integer::sum);
            result += lineStatisticsDao.endBatch();
            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws IOException {
        streamer.close();
    }
}
