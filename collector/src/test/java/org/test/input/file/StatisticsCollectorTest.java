package org.test.input.file;

import org.junit.Test;
import org.test.db.dao.FileDao;
import org.test.db.dao.LineStatisticsDao;
import org.test.input.parallel.RandomAccessFileLinesStreamer;
import org.test.input.stream.ReadableLinesStreamer;

import java.io.File;
import java.io.StringReader;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class StatisticsCollectorTest {
    @Test
    public void shouldCollectLinesFromInput() throws Exception {
        // GIVEN
        FileDao fileDao = new FileDao();
        LineStatisticsDao lineStatisticsDao = new LineStatisticsDao();
        int testFileId = fileDao.saveFile("testFile");
        ReadableLinesStreamer streamer = new ReadableLinesStreamer(new StringReader("line 1\nline 2\nline 3"));
        StatisticsCollector collector = new StatisticsCollector(streamer, testFileId);
        // WHEN
        long result = collector.collect(false);
        // THEN
        assertThat(result, is(3L));
        assertThat(lineStatisticsDao.getLineStatisticsByFileId(testFileId).count(), is(3L));
    }

    @Test
    public void shouldBeAbleToSaveStatisticsInParallel() throws Exception {
        // GIVEN
        FileDao fileDao = new FileDao();
        LineStatisticsDao lineStatisticsDao = new LineStatisticsDao();
        String fileName = ClassLoader.getSystemClassLoader().getResource("input/pg19699.txt").getFile();
        int fileId = fileDao.saveFile(fileName);
        File file = new File(fileName);
        RandomAccessFileLinesStreamer streamer = new RandomAccessFileLinesStreamer(file);
        StatisticsCollector collector = new StatisticsCollector(streamer, fileId);
        // WHEN
        long result = collector.collect(true);
        // THEN
        assertThat(result, is(29625L));
        assertThat(lineStatisticsDao.getLineStatisticsByFileId(fileId).count(), is(29625L));
    }
}