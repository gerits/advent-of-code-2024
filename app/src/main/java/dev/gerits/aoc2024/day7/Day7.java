package dev.gerits.aoc2024.day7;

import dev.gerits.aoc2024.AdventDay;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.function.LongBinaryOperator;
import java.util.function.Predicate;

public class Day7 implements AdventDay {

    private static final List<LongBinaryOperator> OPERATORS1 = List.of(
            Math::addExact,
            Math::multiplyExact
    );
    private static final List<LongBinaryOperator> OPERATORS2 = List.of(
            Math::addExact,
            Math::multiplyExact,
            (a, b) -> (a * (long) Math.pow(10, String.valueOf(b).length())) + b
    );

    public static void main(String[] args) throws Exception {
        new Day7().run();
    }

    @Override
    public void run() throws Exception {
        List<String> input = Files.readAllLines(Path.of(Day7.class.getResource("input.txt").toURI()));

        part1(input);
        part2(input);
    }

    private void part1(List<String> input) {
        long result = 0;

        for (String line : input) {
            String[] split = line.split(":");
            Long expectedResult = Long.valueOf(split[0]);
            List<Long> numbers = Arrays.stream(split[1].split(" "))
                    .filter(Predicate.not(String::isBlank))
                    .map(Long::valueOf)
                    .toList();

            result += tryEquation(new EquationInput(numbers, expectedResult, OPERATORS1), 1, numbers.get(0));
        }

        System.out.printf("Answer part 1: %d%n", result);
    }

    private void part2(List<String> input) {
        long result = 0;

        for (String line : input) {
            String[] split = line.split(":");
            Long expectedResult = Long.valueOf(split[0]);
            List<Long> numbers = Arrays.stream(split[1].split(" "))
                    .filter(Predicate.not(String::isBlank))
                    .map(Long::valueOf)
                    .toList();

            result += tryEquation(new EquationInput(numbers, expectedResult, OPERATORS2), 1, numbers.get(0));
        }

        System.out.printf("Answer part 2: %d%n", result);
    }

    private long tryEquation(EquationInput input, int position, Long number1) {
        long result = 0;
        long number2 = input.numbers().get(position);

        for (LongBinaryOperator operator : input.operators()) {
            long tempResult = operator.applyAsLong(number1, number2);
            if (position < input.numbers().size() - 1) {
                long equationResult = tryEquation(input, position + 1, tempResult);
                if (equationResult != 0L) {
                    result = equationResult;
                }
            } else {
                if (tempResult == input.expectedResult()) {
                    result = input.expectedResult();
                    break;
                }
            }
        }
        return result;
    }

    private record EquationInput(
            List<Long> numbers,
            Long expectedResult,
            List<LongBinaryOperator> operators
    ) {
    }
}
