package org.test.input.pojo;

public class Line {
    private final long posIndicator;
    private final String source;

    public Line(long posIndicator, String source) {
        this.posIndicator = posIndicator;
        this.source = source;
    }

    public long getPosIndicator() {
        return posIndicator;
    }

    public String getSource() {
        return source;
    }
}
