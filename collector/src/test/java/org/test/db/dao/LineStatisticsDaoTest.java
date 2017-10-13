package org.test.db.dao;

import org.junit.Test;
import org.test.input.pojo.Line;
import org.test.stat.pojo.LineStatistics;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class LineStatisticsDaoTest {
    private final LineStatisticsDao lineStatisticsDao = new LineStatisticsDao();

    @Test
    public void shouldStoreLineForFileId() throws Exception {
        // GIVEN
        FileDao fileDao = new FileDao();
        int fileId = fileDao.saveFile("testFile1" + System.currentTimeMillis());
        LineStatistics lineStatistics = new LineStatistics(new Line(1, "ew rwerwe"),
                "rwerwe", "ew", 9, 4);
        // WHEN
        int result = lineStatisticsDao.saveLineStatistics(fileId, lineStatistics);
        // THEN
        assertThat(result, is(1));
    }

    @Test
    public void shouldRemoveAllLinesForFileId() throws Exception {
        // GIVEN
        FileDao fileDao = new FileDao();
        int fileId = fileDao.saveFile("testFile2" + System.currentTimeMillis());
        LineStatistics lineStatistics1 = new LineStatistics(new Line(1, "ew rwerwe"),
                "rwerwe", "ew", 9, 4);
        LineStatistics lineStatistics2 = new LineStatistics(new Line(2, "ew rwerwe"),
                "rwerwe", "ew", 9, 4);
        lineStatisticsDao.saveLineStatistics(fileId, lineStatistics1);
        lineStatisticsDao.saveLineStatistics(fileId, lineStatistics2);
        // WHEN
        int result = lineStatisticsDao.removeAllLinesForFileId(fileId);
        // THEN
        assertThat(result, is(2));
    }

    @Test
    public void shouldReturnStreamOfLines() throws Exception {
        // GIVEN
        FileDao fileDao = new FileDao();
        int fileId = fileDao.saveFile("testFile3" + System.currentTimeMillis());
        LineStatistics lineStatistics1 = new LineStatistics(new Line(1, "ew rwerwe"),
                "rwerwe", "ew", 9, 4);
        LineStatistics lineStatistics2 = new LineStatistics(new Line(2, "ew rwerwe"),
                "rwerwe", "ew", 9, 4);
        LineStatistics lineStatistics3 = new LineStatistics(new Line(3, "ew rwerwe"),
                "rwerwe", "ew", 9, 4);
        LineStatistics lineStatistics4 = new LineStatistics(new Line(4, "ew rwerwe"),
                "rwerwe", "ew", 9, 4);
        lineStatisticsDao.saveLineStatistics(fileId, lineStatistics1);
        lineStatisticsDao.saveLineStatistics(fileId, lineStatistics2);
        lineStatisticsDao.saveLineStatistics(fileId, lineStatistics3);
        lineStatisticsDao.saveLineStatistics(fileId, lineStatistics4);
        // WHEN
        List<LineStatistics> statisticsList = lineStatisticsDao.getLineStatisticsByFileId(fileId)
                .collect(Collectors.toList());
        // THEN
        assertThat(statisticsList, hasSize(4));
        List<Long> positions = statisticsList.stream().map(l -> l.getLine().getPosIndicator()).collect(Collectors.toList());
        assertThat(positions, containsInAnyOrder(is(1L), is(2L), is(3L), is(4L)));
    }
}