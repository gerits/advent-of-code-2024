package dev.gerits.aoc2024.day11;

import dev.gerits.aoc2024.AdventDay;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day11 implements AdventDay {

    public static void main(String[] args) throws Exception {
        new Day11().run();
    }

    @Override
    public void run() throws Exception {
        String input = Files.readString(Path.of(Day11.class.getResource("input.txt").toURI()));

        part1(input);
        part2(input);
    }

    private void part1(String input) {
        long total = countItemsAfterBlinking(input, 25);
        System.out.printf("Answer part 1: %d%n", total);
    }

    private void part2(String input) {
        long total = countItemsAfterBlinking(input, 75);
        System.out.printf("Answer part 2: %d%n", total);
    }

    private long countItemsAfterBlinking(String input, int blinks) {
        Map<Long, Long> values = Arrays.stream(input.split(" "))
                .map(Long::valueOf)
                .collect(Collectors.groupingBy(Long::longValue, Collectors.counting()));

        for (int i = 0; i < blinks; i++) {
            Map<Long, Long> nextValues = new HashMap<>();
            for (Map.Entry<Long, Long> value : values.entrySet()) {
                blinkItem(value.getKey()).forEach(item -> {
                    nextValues.put(item, nextValues.getOrDefault(item, 0L) + value.getValue());
                });
            }
            values = nextValues;
        }

        return values.values().stream()
                .reduce(0L, Long::sum);
    }

    private Stream<Long> blinkItem(Long item) {
        if (item == 0) {
            return Stream.of(1L);
        }

        String stringValue = String.valueOf(item);
        if (stringValue.length() % 2 == 0) {
            return Stream.of(
                    Long.valueOf(stringValue.substring(0, (stringValue.length() / 2))),
                    Long.valueOf(stringValue.substring(stringValue.length() / 2))
            );
        }

        return Stream.of(item * 2024);
    }
}
