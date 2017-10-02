package org.test.stream;

public class Line {
    private final long startPosition;
    private final String source;

    public Line(long startPosition, String source) {
        this.startPosition = startPosition;
        this.source = source;
    }

    public long getStartPosition() {
        return startPosition;
    }

    public String getSource() {
        return source;
    }
}
