package dev.gerits.aoc2024;

import dev.gerits.aoc2024.day1.Day1;
import dev.gerits.aoc2024.day10.Day10;
import dev.gerits.aoc2024.day11.Day11;
import dev.gerits.aoc2024.day12.Day12;
import dev.gerits.aoc2024.day13.Day13;
import dev.gerits.aoc2024.day2.Day2;
import dev.gerits.aoc2024.day3.Day3;
import dev.gerits.aoc2024.day4.Day4;
import dev.gerits.aoc2024.day5.Day5;
import dev.gerits.aoc2024.day6.Day6;
import dev.gerits.aoc2024.day7.Day7;
import dev.gerits.aoc2024.day8.Day8;
import dev.gerits.aoc2024.day9.Day9;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;

import java.util.Map;

public class App {
    private static Map<String, AdventDay> DAYS = Map.ofEntries(
            Map.entry("1", new Day1()),
            Map.entry("2", new Day2()),
            Map.entry("3", new Day3()),
            Map.entry("4", new Day4()),
            Map.entry("5", new Day5()),
            Map.entry("6", new Day6()),
            Map.entry("7", new Day7()),
            Map.entry("8", new Day8()),
            Map.entry("9", new Day9()),
            Map.entry("10", new Day10()),
            Map.entry("11", new Day11()),
            Map.entry("12", new Day12()),
            Map.entry("13", new Day13())
    );

    public static void main(String[] args) throws Exception {
        Options options = new Options();

        DefaultParser parser = new DefaultParser();
        CommandLine commandLine = parser.parse(options, args);

        String day = commandLine.getArgList().stream()
                .findFirst()
                .orElse(null);

        if (day == null || DAYS.get(day) == null) {
            System.out.println("Invalid DAY provided");
            return;
        }

        DAYS.get(day).run();
    }
}
