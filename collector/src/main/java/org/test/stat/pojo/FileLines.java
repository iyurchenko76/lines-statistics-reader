package org.test.stat.pojo;

import java.util.stream.Stream;

public class FileLines {
    private final String fileName;
    private final Stream<LineStatistics> lines;

    public FileLines(String fileName, Stream<LineStatistics> lines) {
        this.fileName = fileName;
        this.lines = lines;
    }

    public String getFileName() {
        return fileName;
    }

    public Stream<LineStatistics> getLines() {
        return lines;
    }
}
