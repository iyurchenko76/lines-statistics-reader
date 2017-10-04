package org.test.input.parallel;

import org.test.input.pojo.Line;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.Spliterator;
import java.util.function.Consumer;

public class RandomAccessFileLinesSpliterator implements Spliterator<Line>, Closeable {
    private static final String FILE_OPEN_MODE = "r";
    private static final int BUFFER_SIZE = 512;
    private static final int MIN_BATCH_SIZE = 2048;

    private final File file;
    private final RandomAccessFile randomAccessFile;
    private final FencedInputStream fencedInputStream;
    private final BufferedReader reader;
    private long endPosition;
    private long counterIndicator;

    public RandomAccessFileLinesSpliterator(File file) throws IOException {
        this.file = file;
        this.endPosition = file.length() - 1;
        this.randomAccessFile = new RandomAccessFile(file, FILE_OPEN_MODE);
        this.fencedInputStream = getFencedInputStream(randomAccessFile.getFD(), endPosition);
        this.reader = new BufferedReader(new InputStreamReader(fencedInputStream));
        this.counterIndicator = 0;
    }

    private RandomAccessFileLinesSpliterator(File file, RandomAccessFile randomAccessFile, long endPos) throws IOException {
        this.file = file;
        this.endPosition = endPos;
        this.randomAccessFile = randomAccessFile;
        this.fencedInputStream = getFencedInputStream(randomAccessFile.getFD(), endPos);
        this.reader = new BufferedReader(new InputStreamReader(fencedInputStream));
        this.counterIndicator = randomAccessFile.getFilePointer();
    }

    private FencedInputStream getFencedInputStream(FileDescriptor fileDescriptor, long endPos) throws IOException {
        return new FencedInputStream(new FileInputStream(fileDescriptor), endPos);
    }

    private long navigateFinalEol(RandomAccessFile randomAccessFile, long endPos) throws IOException {
        long fileLength = randomAccessFile.length();
        if (fileLength <= endPos - 1) {
            return endPos;
        }
        long pos = endPos;
        randomAccessFile.seek(pos);
        byte[] buffer = new byte[BUFFER_SIZE];
        int count;
        while ((count = randomAccessFile.read(buffer)) > 0) {
            int eolPos = findEOL(buffer, count);
            if (eolPos >= 0) {
                pos = pos + eolPos;
                randomAccessFile.seek(pos + 1);
                break;
            } else {
                pos = pos + count;
            }
        }
        return pos;
    }

    private int findEOL(byte[] buffer, int length) {
        for (int i = 0; i < length; i++) {
            if (buffer[i] == 10 || buffer[i] == 13) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public boolean tryAdvance(Consumer<? super Line> action) {
        try {
            String lineText = reader.readLine();
            if (lineText != null) {
                action.accept(new Line(counterIndicator++, lineText));
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Spliterator<Line> trySplit() {
        try {
            long currentPos = randomAccessFile.getFilePointer();
            if (endPosition - currentPos > MIN_BATCH_SIZE) {
                long splitPos = currentPos + (endPosition - currentPos)/2;
                RandomAccessFile splitRandomAccessFile = new RandomAccessFile(file, FILE_OPEN_MODE);
                long splitEndPos = endPosition;
                endPosition = navigateFinalEol(splitRandomAccessFile, splitPos);
                fencedInputStream.fence(endPosition);
                return new RandomAccessFileLinesSpliterator(file, splitRandomAccessFile, splitEndPos);
            } else {
                return null;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long estimateSize() {
        try {
            return endPosition + 1 - randomAccessFile.getFilePointer();
        } catch (IOException e) {
            return -1;
        }
    }

    @Override
    public int characteristics() {
        return Spliterator.CONCURRENT;
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }
}
