package dev.gerits.aoc2024.day22;

import dev.gerits.aoc2024.AdventDay;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Day22 implements AdventDay {

    public static void main(String[] args) throws Exception {
        new Day22().run();
    }

    @Override
    public void run() throws Exception {
//        List<String> input = """
//                1
//                2
//                3
//                2024
//                """.lines().toList();
        List<String> input = Files.readAllLines(Path.of(Day22.class.getResource("input.txt").toURI()));

        part1(input);
        part2(input);
    }

    private void part1(List<String> input) {
        Long total = input.stream()
                .map(Long::parseLong)
                .map(initialSecret -> calculateSecret(initialSecret, 2000))
                .reduce(0L, Long::sum);

        System.out.println("Result part 1: " + total);
    }

    private void part2(List<String> input) {
        List<List<Value>> values = input.stream()
                .map(Long::parseLong)
                .map(initialSecret -> calculateValues(initialSecret, 2000))
                .toList();

        List<Sequence> uniqueSequences = values.stream()
                .flatMap(value -> value.stream().map(Value::sequence))
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        long highestValue = 0;
        for (Sequence sequence : uniqueSequences) {
            if (!sequence.isFull()) {
                continue;
            }
            Long firstOccurrenceOfSequence = values.stream()
                    .map(value -> value.stream()
                            .filter(value1 -> sequence.equals(value1.sequence()))
                            .findFirst())
                    .flatMap(Optional::stream)
                    .map(Value::value)
                    .reduce(0L, Long::sum);
            if (firstOccurrenceOfSequence > highestValue) {
                highestValue = firstOccurrenceOfSequence;
            }
        }

        System.out.println("Result part 2: " + highestValue);
    }

    private long calculateSecret(long initialSecret, int iterations) {
        long secret = initialSecret;
        for (int i = 0; i < iterations; i++) {
            secret = computeNextSecret(secret);
        }
        return secret;
    }

    private List<Value> calculateValues(long initialSecret, int iterations) {
        long secret = initialSecret;

        List<Value> values = new ArrayList<>();
        for (int i = 0; i < iterations; i++) {
            secret = computeNextSecret(secret);
            if (values.isEmpty()) {
                values.add(new Value(secret % 10, null));
                continue;
            }

            long diff = values.getLast().value - (secret % 10);
            values.add(new Value(secret % 10, nextSequences(values.getLast().sequence, diff)));
        }
        return values;
    }

    private Sequence nextSequences(Sequence sequence, long diff) {
        if (sequence == null) {
            return new Sequence(diff, null, null, null);
        }
        if (!sequence.isFull()) {
            if (sequence.seq2 == null) {
                return new Sequence(sequence.seq1, diff, null, null);
            } else if (sequence.seq3 == null) {
                return new Sequence(sequence.seq1, sequence.seq2, diff, null);
            } else if (sequence.seq4 == null) {
                return new Sequence(sequence.seq1, sequence.seq2, sequence.seq3, diff);
            }
        }
        return new Sequence(sequence.seq2, sequence.seq3, sequence.seq4, diff);
    }

    private long computeNextSecret(long secret) {
        secret = prune(mix(secret, secret * 64));
        secret = prune(mix(secret, secret / 32));
        secret = prune(mix(secret, secret * 2048));
        return secret;
    }

    private long mix(long secret, long newSecret) {
        return secret ^ newSecret;
    }

    private long prune(long secret) {
        return secret % 16777216;
    }

    private record Sequence(Long seq1, Long seq2, Long seq3, Long seq4) {
        public boolean isFull() {
            return seq1 != null && seq2 != null && seq3 != null && seq4 != null;
        }
    }

    private record Value(long value, Sequence sequence) {
    }
}
