package dev.gerits.aoc2024;

import dev.gerits.aoc2024.day1.Day1;
import dev.gerits.aoc2024.day10.Day10;
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
    private static Map<String, AdventDay> DAYS = Map.of(
            "1", new Day1(),
            "2", new Day2(),
            "3", new Day3(),
            "4", new Day4(),
            "5", new Day5(),
            "6", new Day6(),
            "7", new Day7(),
            "8", new Day8(),
            "9", new Day9(),
            "10", new Day10()
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
