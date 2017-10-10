package org.test.stat.collector;

import org.test.db.dao.FileDao;
import org.test.input.Streamers;

import java.io.File;
import java.sql.SQLException;

public class Collectors {
    private Collectors() {}

    public static StatisticsCollector getFileStatisticsCollector(File file, FileDao fileDao) {
        try {
            int fileId = fileDao.saveFile(file.getAbsolutePath());
            fileDao.commitTransaction();
            return new StatisticsCollector(Streamers.getFileStreamer(file), fileId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static StatisticsCollector getReadableStatisticsCollector(Readable readable, String virtualFileName, FileDao fileDao) {
        try {
            int fileId = fileDao.saveFile(virtualFileName);
            fileDao.commitTransaction();
            return new StatisticsCollector(Streamers.getReadableStreamer(readable), fileId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
