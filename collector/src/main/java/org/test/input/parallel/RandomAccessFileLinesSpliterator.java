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
    private final BufferedReader reader;
    private long endPosition;
    private long counterIndicator;
    private LimitedInputStream limitedInputStream;
    private final long startPosition;

    public RandomAccessFileLinesSpliterator(File file) throws IOException {
        this.file = file;
        this.endPosition = file.length() - 1;
        this.randomAccessFile = new RandomAccessFile(file, FILE_OPEN_MODE);
        this.reader = getBufferedReader(randomAccessFile.getFD(), endPosition);
        this.counterIndicator = 0;
        this.startPosition = 0;
    }

    private RandomAccessFileLinesSpliterator(File file, RandomAccessFile randomAccessFile, long endPos) throws IOException {
        this.file = file;
        this.startPosition = randomAccessFile.getFilePointer();
        this.endPosition = endPos;
        this.randomAccessFile = randomAccessFile;
        this.reader = getBufferedReader(randomAccessFile.getFD(), endPosition - startPosition);
        this.counterIndicator = startPosition;
    }

    private BufferedReader getBufferedReader(FileDescriptor fileDescriptor, long endPos) throws IOException {
        this.limitedInputStream = new LimitedInputStream(new FileInputStream(fileDescriptor), endPos);
        return new BufferedReader(new InputStreamReader(limitedInputStream));
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
                action.accept(new Line(counterIndicator, lineText));
                counterIndicator += 1;
                return true;
            } else {
                if (randomAccessFile.getFilePointer() > endPosition) {
                    throw new IllegalStateException(String.format("End position (%d) is exceeded by %d byte(s)", endPosition, randomAccessFile.getFilePointer() - endPosition));
                }
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
                limitedInputStream.setLimit(endPosition - startPosition);
                if (splitRandomAccessFile.getFilePointer() <= endPosition) {
                    throw new IllegalStateException(String.format("The new iterator's position is before previous end"));
                }
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
        return endPosition + 1 - startPosition;
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
