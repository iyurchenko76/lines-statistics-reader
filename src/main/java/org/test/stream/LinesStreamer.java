package org.test.stream;

import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class LinesStreamer {
    public Stream<Line> stream(Readable readable, boolean parallel) {
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(
                        new LinesIterator(readable), 0), parallel);
    }
}
