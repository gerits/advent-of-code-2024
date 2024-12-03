package dev.gerits.aoc2024;

import dev.gerits.aoc2024.day1.Day1;
import dev.gerits.aoc2024.day2.Day2;
import dev.gerits.aoc2024.day3.Day3;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;

import java.util.Map;

public class App {
    private static Map<String, AdventDay> DAYS = Map.of(
            "1", new Day1(),
            "2", new Day2(),
            "3", new Day3()
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
