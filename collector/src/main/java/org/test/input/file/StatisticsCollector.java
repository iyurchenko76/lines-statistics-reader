package org.test.input.file;

import org.test.db.dao.LineStatisticsDao;
import org.test.input.LineStreamer;
import org.test.stat.LineStatisticsBuilder;

import java.io.Closeable;
import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;

public class StatisticsCollector implements Closeable {
    private final int fileId;
    private final LineStreamer streamer;
    private final LineStatisticsDao lineStatisticsDao = new LineStatisticsDao();
    private final LineStatisticsBuilder lineStatisticsBuilder = new LineStatisticsBuilder();

    public StatisticsCollector(LineStreamer streamer, int fileId) {
        this.streamer = streamer;
        this.fileId = fileId;
    }

    public long collect(boolean parallel) {
        try {
            lineStatisticsDao.removeAllLinesForFileId(fileId);
            lineStatisticsDao.beginBatch(100);
            if (parallel) {
                return new ForkJoinPool(20).submit(() -> process(true)).get();
            } else {
                return process(false);
            }
        } catch (SQLException | InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                lineStatisticsDao.endBatch();
            } catch (SQLException e) {
                throw new  RuntimeException(e);
            }
        }
    }

    private int process(boolean parallel) {
        return streamer.stream(parallel)
                .map(lineStatisticsBuilder::buildStatistics)
                .map(lineStatistics -> {
                    try {
                        System.out.println(Thread.currentThread().getName() + ": " + lineStatistics.getLine().getPosIndicator());
                        return lineStatisticsDao.saveLineStatistics(fileId, lineStatistics);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }).reduce(0, Integer::sum);
    }

    @Override
    public void close() throws IOException {
        streamer.close();
    }
}
