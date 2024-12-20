package dev.gerits.aoc2024.day18;

import dev.gerits.aoc2024.AdventDay;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.List;

public class Day18 implements AdventDay {

    public static void main(String[] args) throws Exception {
        new Day18().run();
    }

    @Override
    public void run() throws Exception {
        List<String> input = Files.readAllLines(Path.of(Day18.class.getResource("input.txt").toURI()));

        part1(input);
        part2(input);
    }

    private void part1(List<String> input) {
        Maze maze = new Maze(73, 73);
        createBorder(maze);

        int i = 1024;
        for (String line : input) {
            String[] coordinates = line.split(",");
            maze.set(Integer.parseInt(coordinates[0]) + 1, Integer.parseInt(coordinates[1]) + 1, new Wall());
            i--;
            if (i == 0) {
                break;
            }
        }
        maze.set(1, 1, new Reindeer(1, 1, Direction.EAST, maze, 0));
        maze.set(maze.maze.length - 2, maze.maze[0].length - 2, new Finish());

        int cost = move(maze.reindeer);

        System.out.printf("Result part 1: %d%n", cost);
    }

    private void part2(List<String> input) {
        int i;
        for (i = 1024; i < input.size(); i++) {
            Maze maze = new Maze(73, 73);
            createBorder(maze);

            int j = i;
            for (String line : input) {
                String[] coordinates = line.split(",");
                maze.set(Integer.parseInt(coordinates[0]) + 1, Integer.parseInt(coordinates[1]) + 1, new Wall());
                j--;
                if (j <= 0) {
                    break;
                }
            }
            maze.set(1, 1, new Reindeer(1, 1, Direction.EAST, maze, 0));
            maze.set(maze.maze.length - 2, maze.maze[0].length - 2, new Finish());

            int cost = move(maze.reindeer);

            if (cost == 0) {
                System.out.println(maze);
                break;
            }
        }

        System.out.printf("Result part 2: %s%n", input.get(i - 1));
    }

    private void createBorder(Maze maze) {
        for (int x = 0; x < maze.maze.length; x++) {
            maze.set(x, 0, new Wall());
            maze.set(x, maze.maze[0].length - 1, new Wall());
        }
        for (int y = 0; y < maze.maze[0].length; y++) {
            maze.set(0, y, new Wall());
            maze.set(maze.maze.length - 1, y, new Wall());
        }
    }

    private int move(Reindeer reindeer) {
        int cheapestPath = 0;

        ArrayDeque<Reindeer> stack = new ArrayDeque<>();
        stack.push(reindeer);

        while (!stack.isEmpty()) {
            Reindeer currentPosition = stack.pop();

            Item nextTile = currentPosition.maze.get(currentPosition.x, currentPosition.y);

            if (nextTile instanceof Wall) {
                continue;
            }

            if (cheapestPath != 0 && currentPosition.cost >= cheapestPath) {
                continue;
            }

            if (cheapestPath != 0 && currentPosition.cost + (71 - Math.min(currentPosition.x, currentPosition.y)) > cheapestPath) {
                continue;
            }

            if (nextTile instanceof Finish) {
                cheapestPath = currentPosition.cost;
                continue;
            }

            if (!hasVisited(nextTile, currentPosition.cost)) {
                currentPosition.maze.set(currentPosition.x, currentPosition.y, new Visited(currentPosition.cost));
                stack.push(currentPosition.clone(
                        currentPosition.x + currentPosition.direction.x,
                        currentPosition.y + currentPosition.direction.y));

                Direction counterclockwise = counterclockwise(currentPosition.direction);
                stack.push(currentPosition.clone(
                        currentPosition.x + counterclockwise.x,
                        currentPosition.y + counterclockwise.y,
                        counterclockwise
                ));
                Direction clockwise = clockwise(currentPosition.direction);
                stack.push(currentPosition.clone(
                        currentPosition.x + clockwise.x,
                        currentPosition.y + clockwise.y,
                        clockwise
                ));
            }
        }

        return cheapestPath;
    }

    private Direction clockwise(Direction direction) {
        int directionIndex = Arrays.binarySearch(Direction.values(), direction);
        return Direction.values()[directionIndex == 0 ? 3 : directionIndex - 1];
    }

    private Direction counterclockwise(Direction direction) {
        List<Direction> directions = Arrays.stream(Direction.values()).toList();
        int directionIndex = directions.indexOf(direction);
        return directions.get(directionIndex == 3 ? 0 : directionIndex + 1);
    }

    private boolean hasVisited(Item nextPosition, int cost) {
        if (nextPosition instanceof Visited visited) {
            return visited.cost <= cost;
        }
        return false;
    }

    private enum Direction {
        NORTH(0, -1),
        EAST(1, 0),
        SOUTH(0, 1),
        WEST(-1, 0);

        private final int x;
        private final int y;

        Direction(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    private interface Item {
    }

    private static class Visited implements Item {
        private int cost;

        public Visited(int cost) {
            this.cost = cost;
        }

        @Override
        public String toString() {
            return "X";
        }
    }

    private static class Wall implements Item {
        @Override
        public String toString() {
            return "#";
        }
    }

    private static class Finish implements Item {
        @Override
        public String toString() {
            return "E";
        }
    }

    private static class Maze {
        private final Item[][] maze;

        private Reindeer reindeer;

        private Maze(int width, int height) {
            this.maze = new Item[width][height];
        }

        private void set(int x, int y, Item item) {
            maze[x][y] = item;
            if (item instanceof Reindeer reindeer) {
                this.reindeer = reindeer;
            }
        }

        public Item get(int x, int y) {
            return maze[x][y];
        }

        @Override
        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();
            for (int y = 0; y < maze[0].length; y++) {
                for (int x = 0; x < maze.length; x++) {
                    if (maze[x][y] == null) {
                        stringBuilder.append(".");
                    } else {
                        stringBuilder.append(maze[x][y]);
                    }
                }
                stringBuilder.append("\n");
            }
            return stringBuilder.toString();
        }
    }

    private static class Reindeer implements Item {
        private final int x;
        private final int y;
        private final Direction direction;
        private final Maze maze;
        private final int cost;

        public Reindeer(int x, int y, Direction direction, Maze maze, int cost) {
            this.x = x;
            this.y = y;
            this.direction = direction;
            this.maze = maze;
            this.cost = cost;
        }

        public Reindeer clone(int x, int y) {
            return new Reindeer(x, y, direction, maze, cost + 1);
        }

        public Reindeer clone(int x, int y, Direction direction) {
            return new Reindeer(x, y, direction, maze, cost + 1);
        }

        @Override
        public String toString() {
            return "R";
        }
    }
}
