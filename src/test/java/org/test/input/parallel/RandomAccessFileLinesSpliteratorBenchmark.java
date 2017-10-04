package org.test.input.parallel;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.infra.Blackhole;

import java.io.File;
import java.io.IOException;
import java.util.stream.Collectors;

public class RandomAccessFileLinesSpliteratorBenchmark {
    @Benchmark
    public void readFileSequentially(Blackhole blackhole) throws IOException {
        collectSizes(blackhole, false);
    }

    @Benchmark
    public void readFileInParallel(Blackhole blackhole) throws IOException {
        collectSizes(blackhole, true);
    }

    private void collectSizes(Blackhole blackhole, boolean parallel) throws IOException {
        try (RandomAccessFileLinesStreamer streamer = new RandomAccessFileLinesStreamer(new File(
                ClassLoader.getSystemClassLoader()
                        .getResource("input/pg19699.txt").getFile()))) {
            blackhole.consume(streamer.stream(parallel)
                    .map(line -> line.getSource().length())
                    .collect(Collectors.toList()));
        }
    }
}
