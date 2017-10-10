package org.test.stat.collector;

import org.test.db.dao.LineStatisticsDao;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;

class LineStatisticsWorkerThreadFactory implements ForkJoinPool.ForkJoinWorkerThreadFactory {
    private final LineStatisticsDao lineStatisticsDao;

    public LineStatisticsWorkerThreadFactory(LineStatisticsDao lineStatisticsDao) {
        this.lineStatisticsDao = lineStatisticsDao;
    }

    @Override
    public ForkJoinWorkerThread newThread(ForkJoinPool pool) {
//        pool.getPoolSize();
        return new LineStatisticsWorkerThread(lineStatisticsDao, pool);
    }
}
