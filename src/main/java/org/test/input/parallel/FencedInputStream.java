package org.test.input.parallel;

import java.io.IOException;
import java.io.InputStream;

import static java.lang.Math.min;

public class FencedInputStream extends InputStream {
    private final InputStream input;
    private long maxBytes;
    private long counter = 0;

    public FencedInputStream(InputStream input, long maxBytes) {
        this.input = input;
        this.maxBytes = maxBytes;
    }

    @Override
    public int read() throws IOException {
        if (counter < maxBytes) {
            int result = input.read();
            counter++;
            return result;
        } else {
            return -1;
        }
    }

    @Override
    public int read(byte[] b) throws IOException {
        long restBytes = maxBytes - counter;
        int count;
        if (restBytes == 0) {
            return -1;
        } else {
            count = input.read(b, 0, min(b.length, (int)restBytes));
        }

        if (count > 0) {
            counter += count;
        }
        return count;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        long restBytes = maxBytes - counter;
        int count;
        if (restBytes == 0) {
            return -1;
        } else {
            count = input.read(b, off, min(len, (int)restBytes));
        }

        if (count > 0) {
            counter += count;
        }
        return count;
    }

    @Override
    public long skip(long n) throws IOException {
        long restBytes = maxBytes - counter;
        long skipped = input.skip(min(n, restBytes));
        counter += skipped;
        return skipped;
    }

    @Override
    public int available() throws IOException {
        return min((int) (maxBytes - counter), input.available());
    }

    @Override
    public void close() throws IOException {
        input.close();
    }

    @Override
    public synchronized void mark(int readlimit) {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized void reset() throws IOException {
        throw new UnsupportedOperationException();
    }

    public void fence(long newLimit) {
        maxBytes = newLimit;
    }
}
