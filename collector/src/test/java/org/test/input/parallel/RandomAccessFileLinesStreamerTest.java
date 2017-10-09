package org.test.input.parallel;

import org.junit.Test;
import org.test.input.pojo.Line;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class RandomAccessFileLinesStreamerTest {
    private static final String FILE_NAME = ClassLoader.getSystemClassLoader()
            .getResource("input/pg19699.txt").getFile();

    @Test
    public void shouldBeAbleToReadFileInParallel() throws Exception {
        // GIVEN
        File file = new File(FILE_NAME);
        RandomAccessFileLinesStreamer streamer = new RandomAccessFileLinesStreamer(file);
        // WHEN
        Stream<Line> lineStream = streamer.stream(true);
        // THEN
        assertTrue(lineStream.isParallel());
        List<Line> lineList = lineStream.collect(Collectors.toList());
        assertThat(lineList, hasSize(29625));
    }

    @Test
    public void shouldBeAbleToReadFileSequentially() throws Exception {
        // GIVEN
        File file = new File(FILE_NAME);
        RandomAccessFileLinesStreamer streamer = new RandomAccessFileLinesStreamer(file);
        // WHEN
        Stream<Line> lineStream = streamer.stream(false);
        // THEN
        assertFalse(lineStream.isParallel());
        List<Line> lineList = lineStream.collect(Collectors.toList());
        assertThat(lineList, hasSize(29625));
    }

}