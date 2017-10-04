package org.test.input.parallel;

import org.test.input.LineStreamer;
import org.test.input.pojo.Line;

import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class RandomAccessFileLinesStreamer implements LineStreamer {
    private final RandomAccessFileLinesSpliterator spliterator;

    public RandomAccessFileLinesStreamer(File file) throws IOException {
        spliterator = new RandomAccessFileLinesSpliterator(file);
    }


    @Override
    public Stream<Line> stream(boolean parallel) {
        return StreamSupport.stream(spliterator, parallel);
    }

    @Override
    public void close() throws IOException {
        spliterator.close();
    }
}
