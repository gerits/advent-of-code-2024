package dev.gerits.aoc2024.day4;

import dev.gerits.aoc2024.AdventDay;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Day4 implements AdventDay {

    private static final List<Map.Entry<Integer, Integer>> DIRECTIONS = List.of(
            Map.entry(1, 0),
            Map.entry(1, 1),
            Map.entry(0, 1),
            Map.entry(-1, 1),
            Map.entry(-1, 0),
            Map.entry(-1, -1),
            Map.entry(0, -1),
            Map.entry(1, -1)
    );

    private static final String WORD_TO_FIND = "XMAS";

    public static void main(String[] args) throws Exception {
        new Day4().run();
    }

    @Override
    public void run() throws Exception {
        List<String> input = Files.readAllLines(Path.of(Day4.class.getResource("input.txt").toURI()));

        int result1 = 0;

        for (int y = 0; y < input.size(); y++) {
            String currentLine = input.get(y);
            for (int x = 0; x < currentLine.length(); x++) {
                if (currentLine.charAt(x) == WORD_TO_FIND.charAt(0)) {
                    for (Map.Entry<Integer, Integer> direction : DIRECTIONS) {
                        if (tryFindNextLetter(input, direction, x, y, 1)) {
                            result1++;
                        }
                    }
                }
            }
        }

        System.out.printf("Answer part 1: %d%n", result1);

        int result2 = 0;

        for (int y = 0; y < input.size(); y++) {
            String currentLine = input.get(y);
            for (int x = 0; x < currentLine.length(); x++) {
                if (currentLine.charAt(x) == 'A') {
                    Optional<Character> corner1 = safeGetCharacter(input, x + 1, y - 1);
                    Optional<Character> corner2 = safeGetCharacter(input, x + 1, y + 1);
                    Optional<Character> corner3 = safeGetCharacter(input, x - 1, y + 1);
                    Optional<Character> corner4 = safeGetCharacter(input, x - 1, y - 1);

                    if (corner1.isEmpty() || corner2.isEmpty() || corner3.isEmpty() || corner4.isEmpty()) {
                        continue;
                    }

                    // M.M
                    // .A.
                    // S.S
                    if (corner1.get() == 'M' && corner2.get() == 'S' && corner3.get() == 'S' && corner4.get() == 'M') {
                        result2++;
                    }
                    // S.M
                    // .A.
                    // S.M
                    if (corner1.get() == 'M' && corner2.get() == 'M' && corner3.get() == 'S' && corner4.get() == 'S') {
                        result2++;
                    }
                    // S.S
                    // .A.
                    // M.M
                    if (corner1.get() == 'S' && corner2.get() == 'M' && corner3.get() == 'M' && corner4.get() == 'S') {
                        result2++;
                    }
                    // M.S
                    // .A.
                    // M.S
                    if (corner1.get() == 'S' && corner2.get() == 'S' && corner3.get() == 'M' && corner4.get() == 'M') {
                        result2++;
                    }
                }
            }
        }

        System.out.printf("Answer part 2: %d%n", result2);
    }

    private Optional<Character> safeGetCharacter(List<String> input, int x, int y) {
        if (y < 0 || y >= input.size()) {
            return Optional.empty();
        }
        if (x < 0 || x >= input.getFirst().length()) {
            return Optional.empty();
        }
        return Optional.of(input.get(y).charAt(x));
    }

    private boolean tryFindNextLetter(List<String> input, Map.Entry<Integer, Integer> direction, int x, int y, int wordPosition) {
        int nextX = x + direction.getKey();
        if (nextX < 0 || nextX >= input.getFirst().length()) {
            return false;
        }
        int nextY = y + direction.getValue();
        if (nextY < 0 || nextY >= input.size()) {
            return false;
        }
        if (input.get(nextY).charAt(nextX) == WORD_TO_FIND.charAt(wordPosition)) {
            if (wordPosition == WORD_TO_FIND.length() - 1) {
                return true;
            }
            return tryFindNextLetter(input, direction, nextX, nextY, ++wordPosition);
        }
        return false;
    }
}
