package org.test.input.file;

import org.test.db.dao.FileDao;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.stream.Stream;

public class DirectoryStatisticsCollector {
    private final FileDao fileDao = new FileDao();
    private final FileStatisticsCollector fileStatisticsCollector = new FileStatisticsCollector();

    public void collect(String dirName) {
        collect(dirName, pathname -> true);
    }

    public void collect(String dirName, FileFilter fileFilter) {
        streamFiles(dirName, fileFilter)
                .forEach(file -> fileStatisticsCollector.collect(file.getAbsolutePath(), true));
    }

    Stream<File> streamFiles(String dirName, FileFilter fileFilter) {
        File dir = new File(dirName);
        return Arrays.stream(dir.listFiles(fileFilter)).flatMap(file -> {
            if (file.isDirectory()) {
                return streamFiles(file.getAbsolutePath(), fileFilter);
            } else {
                return Stream.of(file);
            }
        });
    }
}