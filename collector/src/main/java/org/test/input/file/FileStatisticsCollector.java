package org.test.input.file;

import org.test.db.dao.FileDao;

import java.io.File;

import static org.test.stat.collector.Collectors.getFileStatisticsCollector;

public class FileStatisticsCollector {
    private final FileDao fileDao = new FileDao();


    public void collect(String fileName, boolean parallel) {
        getFileStatisticsCollector(new File(fileName), fileDao).collect(parallel);
    }
}
