package org.test.stream;

import java.util.Iterator;
import java.util.Scanner;

public class LinesIterator implements Iterator<Line> {
    private final Scanner scanner;
    private long position;

    public LinesIterator(Readable readable) {
        scanner = new Scanner(readable);
    }

    @Override
    public boolean hasNext() {
        return scanner.hasNext();
    }

    @Override
    public Line next() {
        return new Line(position++, scanner.nextLine());
    }
}
