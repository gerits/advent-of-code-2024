package dev.gerits.aoc2024.day6;

import dev.gerits.aoc2024.AdventDay;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Day6 implements AdventDay {

    private static final List<Position> DIRECTIONS = List.of(
            new Position(0, -1),
            new Position(1, 0),
            new Position(0, 1),
            new Position(-1, 0)
    );

    public static void main(String[] args) throws Exception {
        new Day6().run();
    }

    @Override
    public void run() throws Exception {
        List<String> input = Files.readAllLines(Path.of(Day6.class.getResource("input.txt").toURI()));

        part1(input);
        part2(input);
    }

    private void part1(List<String> input) {
        Position currentPosition = determineStartPosition(input);

        Set<Position> result1 = new HashSet<>();
        result1.add(currentPosition);

        int direction = 0;

        while (true) {
            Position currentDirection = DIRECTIONS.get(direction);
            Position nextPosition = new Position(
                    currentPosition.x() + currentDirection.x(),
                    currentPosition.y() + currentDirection.y()
            );

            if (!isOnMap(input, nextPosition)) {
                break;
            }

            if (isWall(input, nextPosition)) {
                direction++;
                if (direction >= DIRECTIONS.size()) {
                    direction = 0;
                }
            } else {
                result1.add(nextPosition);
                currentPosition = nextPosition;
            }
        }

        System.out.printf("Answer part 1: %d%n", result1.size());
    }

    private void part2(List<String> input) {
        int loops = 0;

        for (int y = 0; y < input.size(); y++) {
            for (int x = 0; x < input.getFirst().length(); x++) {
                ArrayList<String> inputNewWall = new ArrayList<>(input);
                char[] charArray = inputNewWall.get(y).toCharArray();
                if (charArray[x] == '^') {
                    continue;
                }
                charArray[x] = '#';
                inputNewWall.set(y, new String(charArray));

                if (findLoop(inputNewWall)) {
                    loops++;
                }

            }
        }

        System.out.printf("Answer part 2: %d%n", loops);
    }

    private boolean findLoop(ArrayList<String> input) {
        Position currentPosition = determineStartPosition(input);

        Set<PositionWithDirection> walls = new HashSet<>();

        int direction = 0;

        while (true) {
            Position currentDirection = DIRECTIONS.get(direction);
            Position nextPosition = new Position(
                    currentPosition.x() + currentDirection.x(),
                    currentPosition.y() + currentDirection.y()
            );

            if (!isOnMap(input, nextPosition)) {
                return false;
            }

            if (isWall(input, nextPosition)) {
                PositionWithDirection wall = new PositionWithDirection(nextPosition.x(), nextPosition.y(), direction);
                if (walls.contains(wall)) {
                    return true;
                }
                walls.add(wall);

                direction++;
                if (direction >= DIRECTIONS.size()) {
                    direction = 0;
                }
            } else {
                currentPosition = nextPosition;
            }
        }
    }

    private boolean isOnMap(List<String> input, Position position) {
        if (position.y() >= input.size() || position.y() < 0) {
            return false;
        }

        return position.x() < input.getFirst().length() && position.x() >= 0;
    }

    private boolean isWall(List<String> input, Position nextPosition) {
        char nextItem = input.get(nextPosition.y()).charAt(nextPosition.x());

        return nextItem == '#';
    }

    private Position determineStartPosition(List<String> input) {
        for (int y = 0; y < input.size(); y++) {
            int x = input.get(y).indexOf('^');
            if (x > -1) {
                return new Position(x, y);
            }
        }
        return null;
    }

    private record Position(int x, int y) {
    }

    private record PositionWithDirection(int x, int y, int direction) {
    }
}
