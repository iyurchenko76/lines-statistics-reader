package org.test.input;

import org.test.input.parallel.RandomAccessFileLinesStreamer;
import org.test.input.stream.ReadableLinesStreamer;

import java.io.File;
import java.io.IOException;

public class Streamers {
    private Streamers() {}

    public static LineStreamer getReadableStreamer(Readable readable) {
        return new ReadableLinesStreamer(readable);
    }

    public static LineStreamer getFileStreamer(File file) {
        try {
            return new RandomAccessFileLinesStreamer(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
