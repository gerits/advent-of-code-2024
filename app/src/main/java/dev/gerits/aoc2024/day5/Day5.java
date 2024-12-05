package dev.gerits.aoc2024.day5;

import dev.gerits.aoc2024.AdventDay;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Day5 implements AdventDay {

    public static void main(String[] args) throws Exception {
        new Day5().run();
    }

    @Override
    public void run() throws Exception {
        String input = Files.readString(Path.of(Day5.class.getResource("input.txt").toURI()));

        String[] split = input.split("\\n\\n");
        List<SortKey> pageOrderingRules = split[0].lines().map(line -> {
            String[] rule = line.split("\\|");
            return new SortKey(Integer.valueOf(rule[0]), Integer.valueOf(rule[1]));
        }).toList();
        String pages = split[1];

        int result1 = pages.lines()
                .map(line -> Arrays.stream(line.split(","))
                        .map(Integer::valueOf)
                        .toList()
                )
                .filter(line -> line.stream()
                        .allMatch(value -> {
                            if (pageOrderingRules.stream().noneMatch(sortKey -> sortKey.page() == value)) {
                                return true;
                            }
                            return pageOrderingRules.stream()
                                    .filter(item -> item.page() == value)
                                    .allMatch(sortKey -> isBefore(line, value, sortKey));
                        }))
                .map(values -> {
                    Integer middleValue = values.get(values.size() / 2);
                    System.out.println("valid line:" + values + " with key " + middleValue);
                    return middleValue;
                })
                .reduce(0, Integer::sum);

        System.out.printf("Answer part 1: %d%n", result1);


        int result2 = pages.lines()
                .map(line -> Arrays.stream(line.split(","))
                        .map(Integer::valueOf)
                        .toList()
                )
                .filter(line -> !line.stream()
                        .allMatch(value -> {
                            if (pageOrderingRules.stream().noneMatch(sortKey -> sortKey.page() == value)) {
                                return true;
                            }
                            return pageOrderingRules.stream()
                                    .filter(item -> item.page() == value)
                                    .allMatch(sortKey -> isBefore(line, value, sortKey));
                        }))
                .map(values -> {
                    ArrayList<Integer> line = new ArrayList<>(values);

                    line.sort((o1, o2) -> {
                        List<SortKey> sortKey = pageOrderingRules.stream()
                                .filter(value -> o1.equals(value.page()))
                                .toList();

                        return sortKey.stream()
                                .map(SortKey::before)
                                .map(before -> before.equals(o2) ? -1 : 0)
                                .filter(value -> value == -1)
                                .findFirst()
                                .orElse(0);
                    });
                    Integer middleValue = line.get(line.size() / 2);
                    System.out.println("invalid line:" + line + " with key " + middleValue);
                    return middleValue;
                })
                .reduce(0, Integer::sum);

        System.out.printf("Answer part 2: %d%n", result2);
    }

    private boolean isBefore(List<Integer> line, Integer value, SortKey sortKey) {
        return !line.contains(sortKey.before()) ||
                line.indexOf(value) < line.indexOf(sortKey.before());
    }

    private record SortKey(int page, int before) {
    }
}
