package org.test.stat;

import org.test.input.pojo.Line;

public class LineStatistics {

    private final Line line;
    private final String longestWord;
    private final String shortestWord;
    private final int length;
    private final int averageWordLength;

    LineStatistics(Line line, String longestWord, String shortestWord,
                   int length, int averageWordLength) {
        this.line = line;
        this.longestWord = longestWord;
        this.shortestWord = shortestWord;
        this.length = length;
        this.averageWordLength = averageWordLength;
    }

    public Line getLine() {
        return line;
    }

    public int getAverageWordLength() {
        return averageWordLength;
    }

    public int getLength() {
        return length;
    }

    public String getShortestWord() {
        return shortestWord;
    }

    public String getLongestWord() {
        return longestWord;
    }
}
