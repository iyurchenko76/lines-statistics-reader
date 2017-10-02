package org.test.stream;

import org.junit.Test;

import java.io.StringReader;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class LinesIteratorTest {
    @Test
    public void shouldHasNextForEachLine() throws Exception {
        String first = "first";
        String second = "second";
        String third = "third";
        StringReader input = new StringReader(first + '\n' + second + '\n' + third);
        LinesIterator it = new LinesIterator(input);

        assertTrue(it.hasNext());
        Line line1 = it.next();
        assertThat(line1.getSource(), is(first));
        assertThat(line1.getStartPosition(), is(0L));

        assertTrue(it.hasNext());
        Line line2 = it.next();
        assertThat(line2.getSource(), is(second));
        assertThat(line2.getStartPosition(), is(1L));

        assertTrue(it.hasNext());
        Line line3 = it.next();
        assertThat(line3.getSource(), is(third));
        assertThat(line3.getStartPosition(), is(2L));

        assertFalse(it.hasNext());
    }

    @Test
    public void shouldIgnoreEmptyLines() throws Exception {
        String lineText = "text";
        LinesIterator it = new LinesIterator(new StringReader(lineText + '\n'));

        assertTrue(it.hasNext());
        assertThat(it.next().getSource(), is(lineText));

        assertFalse(it.hasNext());
    }
}