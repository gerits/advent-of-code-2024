package dev.gerits.aoc2024.day12;

import dev.gerits.aoc2024.AdventDay;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class Day12 implements AdventDay {

    private static final Map<Surrounding, List<Surrounding>> OPPOSITES = Map.of(
            Surrounding.TOP, List.of(Surrounding.LEFT, Surrounding.RIGHT),
            Surrounding.BOTTOM, List.of(Surrounding.LEFT, Surrounding.RIGHT),
            Surrounding.RIGHT, List.of(Surrounding.TOP, Surrounding.BOTTOM),
            Surrounding.LEFT, List.of(Surrounding.TOP, Surrounding.BOTTOM)
    );

    public static void main(String[] args) throws Exception {
        new Day12().run();
    }

    @Override
    public void run() throws Exception {
        List<String> input = Files.readAllLines(Path.of(Day12.class.getResource("input.txt").toURI()));

        part1(input);
        part2(input);
    }

    private void part1(List<String> input) {
        Position[][] inputObject = toPosition(input);

        Map<UUID, List<Position>> positionsPerRegion = Arrays.stream(inputObject)
                .flatMap(Arrays::stream)
                .collect(Collectors.groupingBy(position -> position.region));

        long total = positionsPerRegion.values().stream()
                .map(items -> {
                    int size = items.size();
                    long perimeter = items.stream()
                            .map(item -> {
                                return item.borders.size();
                            }).reduce(0, Integer::sum);
                    return size * perimeter;
                })
                .reduce(0L, Long::sum);

        System.out.printf("Answer part 1: %d%n", total);
    }

    private void part2(List<String> input) {
        Position[][] inputObject = toPosition(input);

        Map<UUID, List<Position>> positionsPerRegion = Arrays.stream(inputObject)
                .flatMap(Arrays::stream)
                .collect(Collectors.groupingBy(position -> position.region));

        long total = positionsPerRegion.values().stream()
                .map(items -> {
                    int size = items.size();
                    long perimeter = items.stream()
                            .flatMap(position -> position.borders.values().stream())
                            .distinct().count();
                    return size * perimeter;
                })
                .reduce(0L, Long::sum);

        System.out.printf("Answer part 2: %d%n", total);
    }

    private Position[][] toPosition(List<String> input) {
        Position[][] inputObject = new Position[input.size()][input.getFirst().length()];
        for (int y = 0; y < input.size(); y++) {
            for (int x = 0; x < input.getFirst().length(); x++) {
                Position position = new Position();
                position.x = x;
                position.y = y;
                position.code = input.get(y).charAt(x);
                inputObject[y][x] = position;
            }
        }

        for (Position[] value : inputObject) {
            for (Position position : value) {
                for (Surrounding surrounding : Surrounding.values()) {
                    UUID border = isBorder(inputObject, position, surrounding);
                    if (border != null) {
                        position.borders.put(surrounding, border);
                    }
                }
            }
        }
        for (Position[] positions : inputObject) {
            for (Position position : positions) {
                if (position.region == null) {
                    position.region = UUID.randomUUID();
                    scanSurrounding(inputObject, position);
                }
            }
        }
        return inputObject;
    }

    private void scanSurrounding(Position[][] input, Position position) {
        for (Surrounding surrounding : Surrounding.values()) {
            int surroundingX = position.x + surrounding.x;
            int surroundingY = position.y + surrounding.y;

            if (isOnMap(input, surroundingX, surroundingY)) {
                Position surroundingPosition = input[surroundingY][surroundingX];
                if (surroundingPosition.code == position.code && surroundingPosition.region == null) {
                    surroundingPosition.region = position.region;
                    scanSurrounding(input, surroundingPosition);
                }
            }
        }
    }

    private boolean isOnMap(Position[][] inputObject, int surroundingX, int surroundingY) {
        if (surroundingX < 0 || surroundingY < 0) {
            return false;
        }
        if (surroundingY >= inputObject.length) {
            return false;
        }
        if (surroundingX >= inputObject[surroundingY].length) {
            return false;
        }
        return true;
    }

    private UUID isBorder(Position[][] input, Position position, Surrounding surrounding) {
        int surroundingX = position.x + surrounding.x;
        int surroundingY = position.y + surrounding.y;

        Position surroundingPosition = safeGet(input, surroundingX, surroundingY);

        if (surroundingPosition != null && position.code == surroundingPosition.code) {
            return null;
        }

        return OPPOSITES.get(surrounding).stream()
                .map(opposite -> {
                    int oppositeX = position.x + opposite.x;
                    int oppositeY = position.y + opposite.y;

                    Position oppositePosition = safeGet(input, oppositeX, oppositeY);
                    if (oppositePosition == null || position.code != oppositePosition.code) {
                        return null;
                    }
                    return oppositePosition.borders.get(surrounding);
                })
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(UUID.randomUUID());
    }

    private Position safeGet(Position[][] input, int x, int y) {
        if (y < 0 || x < 0) {
            return null;
        }

        if (y >= input.length || x >= input[y].length) {
            return null;
        }

        return input[y][x];
    }

    private enum Surrounding {
        TOP(0, -1),
        RIGHT(1, 0),
        BOTTOM(0, 1),
        LEFT(-1, 0);

        private final int x;
        private final int y;

        Surrounding(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    private class Position {
        public UUID region;
        public char code;
        public int x;
        public int y;
        public Map<Surrounding, UUID> borders = new HashMap<>();
    }
}
