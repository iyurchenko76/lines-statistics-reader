package org.test.input.stream;

import org.junit.Test;
import org.test.input.pojo.Line;
import org.test.input.stream.ReadableLinesStreamer;

import java.io.File;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ReadableLinesStreamerTest {

    @Test
    public void shouldReturnStreamOfLines() throws Exception {
        // GIVEN
        String first = "first";
        String second = "second";
        String third = "third";
        String sourceText = first + '\n' + second + '\n' + third + '\n';
        ReadableLinesStreamer streamer = new ReadableLinesStreamer(new StringReader(sourceText));
        // WHEN
        Stream<Line> result = streamer.stream(false);
        // THEN
        List<String> resultWordsList = result.map(Line::getSource).collect(toList());
        assertThat(resultWordsList, hasItems(first, second, third));
        assertThat(resultWordsList.size(), is(3));
    }

    @Test
    public void shouldBeAbleToProcessInParallel() throws Exception {
        // WHEN
        ReadableLinesStreamer streamer = new ReadableLinesStreamer(new StringReader("some\nlines"));
        Stream<Line> result = streamer.stream(true);
        // THEN
        assertTrue(result.isParallel());
    }

    @Test
    public void shouldCloseReader() throws Exception {
        // GIVEN
        Reader reader = mock(Reader.class);
        ReadableLinesStreamer streamer = new ReadableLinesStreamer(reader);
        // WHEN
        streamer.close();
        // THEN
        verify(reader).close();
    }

    @Test
    public void shouldReadFile() throws Exception {
        // GIVEN
        File file = new File(ClassLoader.getSystemClassLoader().getResource("input/pg19699.txt").getFile());
        ReadableLinesStreamer streamer = new ReadableLinesStreamer(file);
        // WHEN
        Stream<Line> result = streamer.stream(false);
        // THEN
        assertThat(result.count(), is(29625L));
    }
}