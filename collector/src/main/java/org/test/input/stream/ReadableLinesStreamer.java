package org.test.input.stream;

import org.test.input.LineStreamer;
import org.test.input.pojo.Line;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class ReadableLinesStreamer implements LineStreamer {
    private final Readable readable;

    public ReadableLinesStreamer(Readable readable) {
        this.readable = readable;
    }

    public ReadableLinesStreamer(File file) throws FileNotFoundException {
        this(new BufferedReader(new InputStreamReader(new FileInputStream(file))));
    }

    @Override
    public Stream<Line> stream(boolean parallel) {
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(
                        new LinesIterator(readable), 0), parallel);
    }

    @Override
    public void close() throws IOException {
        if (readable instanceof Closeable) {
            ((Closeable)readable).close();
        }
    }
}
