package org.test.input.parallel;

import org.junit.Test;
import org.test.input.pojo.Line;

import java.io.File;
import java.util.Spliterator;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

public class RandomAccessFileLinesSpliteratorTest {
    private static final String FILE_NAME = ClassLoader.getSystemClassLoader()
            .getResource("input/pg19699.txt").getFile();

    @Test
    public void shouldBeAbleToReadFile() throws Exception {
        // GIVEN
        File file = new File(FILE_NAME);
        RandomAccessFileLinesSpliterator spliterator = new RandomAccessFileLinesSpliterator(file);
        // WHEN
        spliterator.tryAdvance(line ->
        // THEN
                assertThat(line.getSource(), not(isEmptyString())));
        assertThat(spliterator.estimateSize(), is(file.length()));
    }

    @Test
    public void shouldBeAbleToSplit() throws Exception {
        // GIVEN
        File file = new File(FILE_NAME);
        RandomAccessFileLinesSpliterator spliterator1 = new RandomAccessFileLinesSpliterator(file);
        // WHEN
        Spliterator<Line> spliterator2 = spliterator1.trySplit();
        Spliterator<Line> spliterator3 = spliterator2.trySplit();
        Spliterator<Line> spliterator4 = spliterator1.trySplit();
        // THEN
        long est = Stream.of(spliterator1, spliterator2, spliterator3, spliterator4)
                .map(Spliterator::estimateSize)
                .reduce(0L, Long::sum);
        assertThat(est, is(file.length()));
    }
}