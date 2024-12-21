package dev.gerits.aoc2024.day19;

import dev.gerits.aoc2024.AdventDay;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Day19 implements AdventDay {
    public static void main(String[] args) throws Exception {
        new Day19().run();
    }

    @Override
    public void run() throws Exception {
        List<String> input = Files.readAllLines(Path.of(Day19.class.getResource("input.txt").toURI()));

        List<String> towelPatterns = Arrays.stream(input.getFirst().split(", ")).toList();
        List<String> desiredDesigns = input.subList(2, input.size());

        part1(towelPatterns, desiredDesigns);
        part2(towelPatterns, desiredDesigns);
    }

    private void part1(List<String> towelPatterns, List<String> desiredDesigns) {
        long possibleDesigns = 0;

        List<String> matchingPatterns = new ArrayList<>(towelPatterns);
        matchingPatterns.sort(Comparator.reverseOrder());
        for (String desiredDesign : desiredDesigns) {
            if (matchPatterns(desiredDesign, matchingPatterns)) {
                possibleDesigns++;
            }
        }

        System.out.println("Result part 1: " + possibleDesigns);
    }

    private void part2(List<String> towelPatterns, List<String> desiredDesigns) {
        long possibleDesigns = 0;

        List<String> matchingPatterns = new ArrayList<>(towelPatterns);
        matchingPatterns.sort(Comparator.reverseOrder());
        for (String desiredDesign : desiredDesigns) {
            possibleDesigns += countPossiblePatterns(desiredDesign, matchingPatterns, new HashMap<>());
        }

        System.out.println("Result part 2: " + possibleDesigns);
    }

    private boolean matchPatterns(String desiredDesign, List<String> matchingPatterns) {
        if (desiredDesign.isEmpty()) {
            return true;
        }

        for (String matchingPattern : matchingPatterns) {
            if (desiredDesign.startsWith(matchingPattern)
                    && matchPatterns(desiredDesign.substring(matchingPattern.length()), matchingPatterns)) {
                return true;
            }
        }

        return false;
    }

    private long countPossiblePatterns(String desiredDesign, List<String> matchingPatterns, Map<String, Long> cache) {
        if (desiredDesign.isEmpty()) {
            return 1;
        }

        if (cache.containsKey(desiredDesign)) {
            return cache.get(desiredDesign);
        }

        long possiblePatterns = 0;
        for (String matchingPattern : matchingPatterns) {
            if (desiredDesign.startsWith(matchingPattern)) {
                possiblePatterns += countPossiblePatterns(
                        desiredDesign.substring(matchingPattern.length()),
                        matchingPatterns,
                        cache
                );
            }
        }

        cache.put(desiredDesign, possiblePatterns);
        return possiblePatterns;
    }

}
