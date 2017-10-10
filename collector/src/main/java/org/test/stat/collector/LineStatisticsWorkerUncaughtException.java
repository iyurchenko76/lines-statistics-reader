package org.test.stat.collector;

class LineStatisticsWorkerUncaughtException implements Thread.UncaughtExceptionHandler {

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        throw new RuntimeException(e);
    }
}
