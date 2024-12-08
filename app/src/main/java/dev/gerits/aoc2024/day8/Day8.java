package dev.gerits.aoc2024.day8;

import dev.gerits.aoc2024.AdventDay;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Day8 implements AdventDay {

    public static void main(String[] args) throws Exception {
        new Day8().run();
    }

    private static Map<Character, List<Position>> createAntennaMap(List<String> input) {
        Map<Character, List<Position>> antennas = new HashMap<>();

        for (int y = 0; y < input.size(); y++) {
            for (int x = 0; x < input.getFirst().length(); x++) {
                char charAtLocation = input.get(y).charAt(x);
                if (charAtLocation != '.') {
                    antennas.putIfAbsent(charAtLocation, new ArrayList<>());
                    antennas.get(charAtLocation).add(new Position(x, y));
                }
            }
        }
        return antennas;
    }

    @Override
    public void run() throws Exception {
        List<String> input = Files.readAllLines(Path.of(Day8.class.getResource("input.txt").toURI()));

        part1(input);
        part2(input);
    }

    private void part1(List<String> input) {
        Set<Position> antinodes = new HashSet<>();
        Map<Character, List<Position>> antennas = createAntennaMap(input);

        antennas.forEach((key, antennasOfSingleType) -> {
            antinodes.addAll(computeAntinodes(input, antennasOfSingleType, false));
        });

        printResult(input, antinodes);
        System.out.printf("Answer part 1: %d%n", antinodes.size());
    }

    private void part2(List<String> input) {
        Set<Position> antinodes = new HashSet<>();
        Map<Character, List<Position>> antennas = createAntennaMap(input);

        antennas.forEach((key, antennasOfSingleType) -> {
            antinodes.addAll(computeAntinodes(input, antennasOfSingleType, true));
            antinodes.addAll(antennasOfSingleType);
        });

        printResult(input, antinodes);
        System.out.printf("Answer part 1: %d%n", antinodes.size());
    }

    private static void printResult(List<String> input, Set<Position> antinodes) {
        ArrayList<String> resultList = new ArrayList<>(input);
        antinodes.forEach(antinode -> {
            String line = resultList.get(antinode.y());
            resultList.set(antinode.y(),
                    line.substring(0, antinode.x()) + "#" + line.substring(antinode.x() + 1));
        });
        resultList.forEach(System.out::println);
    }

    private Set<Position> computeAntinodes(List<String> input, Collection<Position> positions, boolean resonate) {
        Set<Position> antinodes = new HashSet<>();
        positions.forEach(lookupPosition -> {
            positions.forEach(matchPosition -> {
                if (lookupPosition.equals(matchPosition)) {
                    return;
                }

                int xDifference = lookupPosition.x() - matchPosition.x();
                int yDifference = lookupPosition.y() - matchPosition.y();

                if (resonate) {
                    Position lastAntennaPosition = lookupPosition;
                    Position antiNode;
                    do {
                        antiNode = computeAntiNode(input, lastAntennaPosition, xDifference, yDifference);
                        if (antiNode != null) {
                            antinodes.add(antiNode);
                            lastAntennaPosition = antiNode;
                        }
                    } while (antiNode != null);
                } else {
                    Position antiNode = computeAntiNode(input, lookupPosition, xDifference, yDifference);
                    if (antiNode != null) {
                        antinodes.add(antiNode);
                    }
                }
            });
        });

        return antinodes;
    }

    private Position computeAntiNode(List<String> input, Position lookupPosition, int xDifference, int yDifference) {
        Position antinode = new Position(
                lookupPosition.x() + xDifference,
                lookupPosition.y() + yDifference
        );
        if (isWithinBounds(input, antinode)) {
            return antinode;
        }
        return null;
    }

    private boolean isWithinBounds(List<String> input, Position antinode) {
        if (antinode.x() < 0 || antinode.y() < 0) {
            return false;
        }
        if (antinode.y() >= input.size()) {
            return false;
        }
        if (antinode.x() >= input.getFirst().length()) {
            return false;
        }
        return true;
    }

    private record Position(int x, int y) {
    }
}