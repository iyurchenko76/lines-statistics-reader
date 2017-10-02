package org.test.stream;

import org.junit.Test;

import java.io.StringReader;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class LinesStreamerTest {
    private final LinesStreamer streamer = new LinesStreamer();

    @Test
    public void shouldReturnStreamOfLines() throws Exception {
        // GIVEN
        String first = "first";
        String second = "second";
        String third = "third";
        String sourceText = first + '\n' + second + '\n' + third + '\n';
        // WHEN
        Stream<Line> result = streamer.stream(new StringReader(sourceText), false);
        // THEN
        List<String> resultWordsList = result.map(Line::getSource).collect(toList());
        assertThat(resultWordsList, hasItems(first, second, third));
        assertThat(resultWordsList.size(), is(3));
    }

    @Test
    public void shouldBeAbleToProcessInParallel() throws Exception {
        // WHEN
        Stream<Line> result = streamer.stream(new StringReader("some\nlines"), true);
        // THEN
        assertTrue(result.isParallel());
    }
}