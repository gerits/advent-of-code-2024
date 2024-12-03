package dev.gerits.aoc2024.day2;

import dev.gerits.aoc2024.AdventDay;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Day2 implements AdventDay {

    public static void main(String[] args) throws Exception {
        new Day2().run();
    }

    @Override
    public void run() throws Exception {
        List<String> lines = Files.readAllLines(Path.of(Day2.class.getResource("input.txt").toURI()));

        long safeLines1 = lines.stream()
                .map(line -> line.split(" "))
                .filter(this::filterSafeReports)
                .count();

        System.out.printf("Answer part 1: %d%n", safeLines1);

        long safeLines2 = lines.stream()
                .map(line -> line.split(" "))
                .filter(this::tolerantFilterSafeReports)
                .count();

        System.out.printf("Answer part 2: %d%n", safeLines2);
    }


    private boolean tolerantFilterSafeReports(String[] levels) {
        if (filterSafeReports(levels)) {
            return true;
        }

        for (int i = 0; i < levels.length; i++) {
            List<String> currentLevels = new ArrayList<>(Arrays.asList(levels));
            currentLevels.remove(i);
            if (filterSafeReports(currentLevels.toArray(new String[]{}))) {
                return true;
            }
        }

        return false;
    }

    private boolean filterSafeReports(String[] levels) {
        Boolean increasing = null;
        Integer prevLevel = null;
        for (String levelStr : levels) {
            Integer level = Integer.valueOf(levelStr);
            if (prevLevel == null) {
                prevLevel = level;
                continue;
            }
            if (level.equals(prevLevel)) {
                return false;
            }
            if (increasing == null) {
                increasing = level > prevLevel;
            }

            if (Boolean.TRUE.equals(increasing) && prevLevel >= level ||
                    Boolean.FALSE.equals(increasing) && prevLevel <= level) {
                return false;
            }

            if (Math.abs(prevLevel - level) > 3) {
                return false;
            }

            prevLevel = level;
        }
        return true;
    }
}
