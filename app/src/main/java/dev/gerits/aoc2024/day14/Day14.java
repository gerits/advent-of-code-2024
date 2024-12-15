package dev.gerits.aoc2024.day14;

import dev.gerits.aoc2024.AdventDay;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day14 implements AdventDay {

    private static final Pattern LINE_PATTERN = Pattern.compile("p=(-?\\d{1,3}),(-?\\d{1,3})\\s+v=(-?\\d{1,3}),(-?\\d{1,3})");

    public static void main(String[] args) throws Exception {
        new Day14().run();
    }

    @Override
    public void run() throws Exception {
        List<String> input = Files.readAllLines(Path.of(Day14.class.getResource("input.txt").toURI()));

        List<Robot> robots = parseRobots(input);
        int gridWidth = 101;
        int gridHeight = 103;

        part1(robots, gridWidth, gridHeight);
    }

    private void part1(List<Robot> robots, int gridWidth, int gridHeight) {
        moveRobots(robots, 100, gridWidth, gridHeight);
        int count = countQuadrants(robots, gridWidth, gridHeight);

        System.out.printf("Result part 1: %d%n", count);
    }

    private int countQuadrants(List<Robot> robots, int gridWidth, int gridHeight) {
        Map<Integer, List<Robot>> quadrants = robots.stream()
                .collect(Collectors.groupingBy(robot -> {
                    if (robot.x < gridWidth / 2 && robot.y < gridHeight / 2) return 1;
                    if (robot.x > gridWidth / 2 && robot.y < gridHeight / 2) return 2;
                    if (robot.x < gridWidth / 2 && robot.y > gridHeight / 2) return 3;
                    if (robot.x > gridWidth / 2 && robot.y > gridHeight / 2) return 4;
                    return 0;
                }));

        return quadrants.entrySet().stream()
                .filter(entry -> entry.getKey() != 0)
                .map(Map.Entry::getValue)
                .map(List::size)
                .filter(value -> value != 0)
                .reduce(1, (v1, v2) -> v1 * v2);
    }

    private void moveRobots(List<Robot> robots, int moves, int gridWidth, int gridHeight) {
        robots.forEach(robot -> {
            robot.x = (robot.x + (robot.velocity.x() * moves)) % gridWidth;
            robot.y = (robot.y + (robot.velocity.y() * moves)) % gridHeight;

            if (robot.x < 0) {
                robot.x = gridWidth + robot.x;
            }
            if (robot.y < 0) {
                robot.y = gridHeight + robot.y;
            }
        });
    }

    private List<Robot> parseRobots(List<String> input) {
        return input.stream().map(line -> {
            Matcher matcher = LINE_PATTERN.matcher(line);

            if (!matcher.matches()) {
                throw new IllegalArgumentException();
            }
            int x = Integer.parseInt(matcher.group(1));
            int y = Integer.parseInt(matcher.group(2));
            Velocity velocity = new Velocity(Integer.parseInt(matcher.group(3)), Integer.parseInt(matcher.group(4)));
            return new Robot(x, y, velocity);
        }).toList();
    }

    private record Velocity(int x, int y) {
    }

    private class Robot implements Comparable<Robot> {
        private final Velocity velocity;
        private int x;
        private int y;

        public Robot(int x, int y, Velocity velocity) {
            this.x = x;
            this.y = y;
            this.velocity = velocity;
        }

        @Override
        public int compareTo(Robot o) {
            int compareY = Objects.compare(this.y, o.y, Integer::compareTo);
            if (compareY == 0) {
                return Objects.compare(this.x, o.x, Integer::compareTo);
            }
            return compareY;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Robot robot) {
                return Objects.equals(this.x, robot.x) && Objects.equals(this.y, robot.y);
            }
            return false;
        }
    }
}