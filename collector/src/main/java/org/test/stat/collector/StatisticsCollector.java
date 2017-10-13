package org.test.stat.collector;

import org.test.db.dao.LineStatisticsDao;
import org.test.input.LineStreamer;
import org.test.stat.builder.LineStatisticsBuilder;

import java.io.Closeable;
import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

public class StatisticsCollector implements Closeable {
    private final int fileId;
    private final LineStreamer streamer;
    private final LineStatisticsDao lineStatisticsDao = new LineStatisticsDao();
    private final LineStatisticsBuilder lineStatisticsBuilder = new LineStatisticsBuilder();
    private final ForkJoinPool pool = new ForkJoinPool(10,
            new LineStatisticsWorkerThreadFactory(lineStatisticsDao),
            new LineStatisticsWorkerUncaughtException(), true);

    public StatisticsCollector(LineStreamer streamer, int fileId) {
        this.streamer = streamer;
        this.fileId = fileId;
    }

    public long collect(boolean parallel) {
        try {
            lineStatisticsDao.removeAllLinesForFileId(fileId);
            if (!parallel) {
                lineStatisticsDao.beginBatch(100);
                return process(false) + lineStatisticsDao.endBatch();
            } else {
                return collectParallel();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private int process(boolean parallel) {
        return streamer.stream(parallel)
                .map(lineStatisticsBuilder::buildStatistics)
                .map(lineStatistics -> {
                    try {
                        return lineStatisticsDao.saveLineStatistics(fileId, lineStatistics);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }).reduce(0, Integer::sum);
    }

    public int collectParallel() {
        try {
            int result = pool.submit(() -> process(true)).get();
            pool.shutdown();
            pool.awaitTermination(1, TimeUnit.SECONDS);
            return result;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws IOException {
        streamer.close();
    }
}
