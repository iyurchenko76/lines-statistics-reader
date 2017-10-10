package org.test.stat.collector;

import org.test.db.dao.LineStatisticsDao;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;

class LineStatisticsWorkerThread extends ForkJoinWorkerThread {
    private final LineStatisticsDao lineStatisticsDao;

    /**
     * Creates a ForkJoinWorkerThread operating in the given pool.
     *
     * @param pool the pool this thread works in
     * @throws NullPointerException if pool is null
     */
    protected LineStatisticsWorkerThread(LineStatisticsDao lineStatisticsDao, ForkJoinPool pool) {
        super(pool);
        this.lineStatisticsDao = lineStatisticsDao;
    }

    @Override
    protected void onStart() {
        super.onStart();
        lineStatisticsDao.beginBatch(100);
    }

    @Override
    protected void onTermination(Throwable exception) {
        super.onTermination(exception);
        try {
            lineStatisticsDao.endBatch();
            lineStatisticsDao.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
