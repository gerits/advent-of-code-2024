package dev.gerits.aoc2024.day3;

import dev.gerits.aoc2024.AdventDay;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day3 implements AdventDay {

    private static final Pattern REGEX_PART_1 = Pattern.compile("mul\\(([0-9]{1,3}),([0-9]{1,3})\\)");
    private static final Pattern REGEX_PART_2 = Pattern.compile("(don't\\(\\)|do\\(\\)|mul\\(([0-9]{1,3}),([0-9]{1,3})\\))");

    public static void main(String[] args) throws Exception {
       new Day3().run();
    }

    @Override
    public void run() throws Exception {
        String input = Files.readString(Path.of(Day3.class.getResource("input.txt").toURI()));

        int result1 = 0;

        Matcher matcher = REGEX_PART_1.matcher(input);
        while (matcher.find()) {
            Integer number1 = Integer.valueOf(matcher.group(1));
            Integer number2 = Integer.valueOf(matcher.group(2));

            result1 += (number1 * number2);
        }

        System.out.printf("Answer part 1: %d%n", result1);

        int result2 = 0;
        boolean enabled = true;

        Matcher matcher2 = REGEX_PART_2.matcher(input);
        while (matcher2.find()) {
            String action = matcher2.group(1);

            if (!action.startsWith("mul")) {
                enabled = action.equals("do()");
                continue;
            }

            if (enabled) {
                Integer number1 = Integer.valueOf(matcher2.group(2));
                Integer number2 = Integer.valueOf(matcher2.group(3));

                result2 += (number1 * number2);
            }
        }

        System.out.printf("Answer part 2: %d%n", result2);
    }
}
