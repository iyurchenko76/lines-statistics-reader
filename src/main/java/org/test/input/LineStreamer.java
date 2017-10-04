package org.test.input;

import org.test.input.pojo.Line;

import java.io.Closeable;
import java.util.stream.Stream;

public interface LineStreamer extends Closeable {
    Stream<Line> stream(boolean parallel);
}
