package org.test.stat;

import org.test.input.pojo.Line;

import java.util.Arrays;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LineStatisticsBuilder {

    private static final Pattern WORD_PATTERN = Pattern.compile("[^ ]+");

    private static final Comparator<String> WORDS_LENGTH_COMPARATOR = (s1, s2) -> {
        if (s1 == null) {
            return 1;
        } else if (s2 == null) {
            return -1;
        } else {
            return Integer.compare(s1.length(), s2.length());
        }
    };

    public LineStatistics buildStatistics(Line line) {
        String[] words = new String[line.getSource().length() / 5];
        Matcher matcher = WORD_PATTERN.matcher(line.getSource());
        int counter = 0;
        while (matcher.find()) {
            counter++;
            if (words.length < counter) {
                words = resizeArray(words, counter + 10);
            }
            words[counter - 1] = matcher.group();
        }
        if (counter == 0) {
            return new LineStatistics(line, "", "", line.getSource().length(), 0);
        }
        Arrays.sort(words, WORDS_LENGTH_COMPARATOR);
        int averageWordLength = Arrays.stream(words)
                .filter(s -> s != null)
                .map(s -> s.length())
                .reduce(0, Integer::sum) / counter;
        return new LineStatistics(line, words[counter - 1], words[0], line.getSource().length(), averageWordLength);
    }

    private String[] resizeArray(String[] src, int newLength) {
        String[] result = new String[newLength];
        System.arraycopy(src, 0, result, 0, src.length);
        return result;
    }
}
