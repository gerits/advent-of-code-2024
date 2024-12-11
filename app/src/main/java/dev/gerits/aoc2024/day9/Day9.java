package dev.gerits.aoc2024.day9;

import dev.gerits.aoc2024.AdventDay;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

public class Day9 implements AdventDay {
    public static void main(String[] args) throws Exception {
        new Day9().run();
    }

    @Override
    public void run() throws Exception {
        String input = Files.readString(Path.of(Day9.class.getResource("input.txt").toURI()));

        part1(input);
        part2(input);
    }

    private void part1(String input) {
        long result = 0L;

        List<Long> values = new ArrayList<>();
        long index = 0L;
        for (int i = 0; i < input.length(); i++) {
            boolean isValue = i % 2 == 0;

            long finalIndex = index;
            IntStream.rangeClosed(0, Integer.parseInt(String.valueOf(input.charAt(i))) - 1)
                    .mapToObj(val -> isValue ? finalIndex : -1)
                    .forEach(values::add);
            if (isValue) {
                index++;
            }
        }

        while (values.contains(-1L)) {
            values.set(values.indexOf(-1L), values.getLast());
            values.removeLast();
        }

        for (int i = 0; i < values.size(); i++) {
            result += i * values.get(i);
        }

        System.out.printf("Answer part 1: %d%n", result);
    }

    private void part2(String input) {
        String string = convertToString(input);

        Set<Character> movedCharacters = new HashSet<>();
        while (hasUnmovedCharacters(string, movedCharacters)) {
            Range range = findLastUnmovedValue(string, movedCharacters);
            char value = string.charAt(range.end());

            int length = range.end() - range.start() + 1;
            String spaceString = IntStream.range(0, length).mapToObj(i -> String.valueOf((char) 0)).reduce("", (a, b) -> a + b);

            int i = string.indexOf(spaceString);
            if (i == -1 || i > range.start()) {
                movedCharacters.add(value);
                continue;
            }
            string = string.substring(0, i) +
                    string.substring(range.start(), range.end() + 1) +
                    string.substring(i + length, range.start()) +
                    spaceString +
                    string.substring(range.end() + 1);

            movedCharacters.add(value);
        }

        long result = 0;
        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) == (char) 0) {
                continue;
            }
            result += i * (((long) string.charAt(i)) - 1);
        }

        System.out.printf("Answer part 2: %d%n", result);
    }

    private String convertToString(String input) {
        StringBuilder stringBuilder = new StringBuilder();

        long index = 1;
        for (int i = 0; i < input.length(); i++) {
            boolean isValue = i % 2 == 0;

            for (int j = 0; j < Character.getNumericValue(input.charAt(i)); j++) {
                stringBuilder.append(isValue ? (char) index : (char) 0);
            }
            if (isValue) {
                index++;
            }
        }
        return stringBuilder.toString();
    }

    private Range findLastUnmovedValue(String string, Set<Character> movedCharacters) {
        int end = string.length() - 1;
        while (string.charAt(end) == (char) 0 || movedCharacters.contains(string.charAt(end))) {
            end--;
        }
        int start = end;
        while (start - 1 > 0 && string.charAt(start - 1) == string.charAt(end)) {
            start--;
        }
        return new Range(start, end);
    }

    private boolean hasUnmovedCharacters(String string, Set<Character> movedCharachters) {
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            if (c == (char) 0) {
                continue;
            }
            if (movedCharachters.contains(c)) {
                continue;
            }
            return true;
        }
        return false;
    }

    private record Range(int start, int end) {
    }
}
