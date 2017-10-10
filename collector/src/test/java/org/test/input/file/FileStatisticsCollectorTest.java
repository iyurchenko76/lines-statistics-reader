package org.test.input.file;

import org.junit.Test;
import org.test.db.dao.FileDao;
import org.test.db.dao.LineStatisticsDao;

import java.io.File;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.stream;
import static java.util.function.Function.identity;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class FileStatisticsCollectorTest {
    private static final String DIR_NAME = ClassLoader.getSystemClassLoader().getResource("input/rootDir").getFile();
    private static final String[] fileResources = {"input/rootDir/dir01/dir11/10001.txt",
            "input/rootDir/dir01/dir21/10002.txt",
            "input/rootDir/dir02/12222.txt",
            "input/rootDir/dir01/12333.txt",
            "input/rootDir/utrkj10.txt"};
    private final DirectoryStatisticsCollector directoryStatisticsCollector = new DirectoryStatisticsCollector();
    private final FileDao fileDao = new FileDao();
    private final LineStatisticsDao lineStatisticsDao = new LineStatisticsDao();

    @Test
    public void shouldFindAllFilesInDir() throws Exception {
        // WHEN
        Stream<File> fileStream = directoryStatisticsCollector.streamFiles(DIR_NAME, pathname -> true);
        // THEN
        List<File> fileList = fileStream.collect(Collectors.toList());
        assertThat(fileList, hasSize(fileResources.length));
    }

    @Test
    public void shouldCollectStatisticsForWholeDir() throws Exception {
        // GIVEN
        List<String> fileNames = stream(fileResources)
                .map(resource -> new File(ClassLoader.getSystemClassLoader().getResource(resource).getFile()).getAbsolutePath())
                .collect(Collectors.toList());
        // WHEN
        directoryStatisticsCollector.collect(DIR_NAME);
        // THEN
        Map<String, Integer> fileIdMap = fileNames.stream()
                .collect(Collectors.toMap(identity(), fileName -> {
                    try {
                        return fileDao.findFileByName(fileName);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }));
        assertThat(fileIdMap.values(), hasSize(fileResources.length));
        assertThat(fileIdMap.values(), everyItem(greaterThanOrEqualTo(0)));
        assertThat(fileIdMap.keySet(), containsInAnyOrder(fileNames.stream().map(name -> is(name)).collect(Collectors.toList())));
        long lineCount = fileIdMap.values().stream()
                .flatMap(fileId -> {
                    try {
                        return lineStatisticsDao.getLineStatisticsByFileId(fileId);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                })
                .count();
        assertThat(lineCount, is(39336L));
    }
}