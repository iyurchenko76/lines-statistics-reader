package org.test.stat;

import org.junit.Test;
import org.test.input.pojo.Line;
import org.test.stat.pojo.LineStatistics;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class LineStatisticsBuilderTest {
    private final LineStatisticsBuilder builder = new LineStatisticsBuilder();

    @Test
    public void shouldSuccessfullyBuildStatistics() throws Exception {
        // GIVEN
        Line line = new Line(0, "one two-three four five ");
        // WHEN
        LineStatistics result = builder.buildStatistics(line);
        // THEN
        assertThat(result.getLine().getSource(), is(line.getSource()));
        assertThat(result.getLength(), is(line.getSource().length()));
        assertThat(result.getAverageWordLength(), is(5));
        assertThat(result.getLongestWord(), is("two-three"));
        assertThat(result.getShortestWord(), is("one"));
    }

    @Test
    public void shouldProcessEmptyLines() throws Exception {
        // GIVEN
        Line line = new Line(0, " ");
        // WHEN
        LineStatistics result = builder.buildStatistics(line);
        // THEN
        assertThat(result.getShortestWord(), is(""));
        assertThat(result.getLongestWord(), is(""));
        assertThat(result.getAverageWordLength(), is(0));
        assertThat(result.getLength(), is(line.getSource().length()));
        assertThat(result.getLine().getSource(), is(line.getSource()));
    }

    @Test
    public void shouldProcessLinesWithManyShortWords() throws Exception {
        // GIVEN
        Line line = new Line(0, "a bb cc dd ee ff gg hh ii jj kkk");
        // WHEN
        LineStatistics result = builder.buildStatistics(line);
        // THEN
        assertThat(result.getShortestWord(), is("a"));
        assertThat(result.getLongestWord(), is("kkk"));
        assertThat(result.getAverageWordLength(), is(2));
        assertThat(result.getLength(), is(line.getSource().length()));
        assertThat(result.getLine().getSource(), is(line.getSource()));
    }
}