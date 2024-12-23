package dev.gerits.aoc2024.day23;

import dev.gerits.aoc2024.AdventDay;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day23 implements AdventDay {

    public static void main(String[] args) throws Exception {
        new Day23().run();
    }

    @Override
    public void run() throws Exception {
        List<List<String>> input = Files.readAllLines(Path.of(Day23.class.getResource("input.txt").toURI())).stream()
                .map(line -> Arrays.stream(line.split("-")).sorted(String::compareTo).toList())
                .sorted(Comparator.comparing((List<String> l) -> l.getFirst()).thenComparing(l -> l.get(1)))
                .toList();

        part1(input);
    }

    private void part1(List<List<String>> input) {
        Set<List<String>> pairs = new HashSet<>();

        for (List<String> pair : input) {
            Set<List<String>> firstLevelPairs = input.stream()
                    .filter(p -> p != pair && p.contains(pair.getLast()))
                    .collect(Collectors.toSet());

            for (List<String> firstLevelPair : firstLevelPairs) {
                input.stream()
                        .filter(p ->
                                p != pair &&
                                        p != firstLevelPair &&
                                        p.contains(firstLevelPair.getLast()) &&
                                        p.contains(pair.getFirst()))
                        .forEach(p -> pairs.add(Stream.of(
                                pair.getFirst(),
                                pair.getLast(),
                                p.getLast()
                        ).sorted().toList()));
            }
        }

        Set<List<String>> pairsWithChiefHistorian = pairs.stream()
                .filter(p -> p.stream()
                        .anyMatch(pc -> pc.startsWith("t")))
                .collect(Collectors.toSet());
        System.out.println("Result part 1: " + pairsWithChiefHistorian.size());
    }
}
