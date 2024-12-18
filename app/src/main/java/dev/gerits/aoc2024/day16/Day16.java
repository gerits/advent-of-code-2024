package dev.gerits.aoc2024.day16;

import dev.gerits.aoc2024.AdventDay;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.List;

public class Day16 implements AdventDay {

    public static void main(String[] args) throws Exception {
        new Day16().run();
    }

    @Override
    public void run() throws Exception {
        List<String> input = Files.readAllLines(Path.of(Day16.class.getResource("input.txt").toURI()));

        Maze maze = parseMaze(input);

        int cost = move(maze.reindeer);

        System.out.printf("Result part 1: %d%n", cost);
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

            if (cheapestPath != 0 && currentPosition.cost > cheapestPath) {
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

                if (!currentPosition.rotated) {
                    Direction clockwise = clockwise(currentPosition.direction);
                    stack.push(currentPosition.clone(
                            currentPosition.x + clockwise.x,
                            currentPosition.y + clockwise.y,
                            clockwise
                    ));
                    Direction counterclockwise = counterclockwise(currentPosition.direction);
                    stack.push(currentPosition.clone(
                            currentPosition.x + counterclockwise.x,
                            currentPosition.y + counterclockwise.y,
                            counterclockwise
                    ));
                }
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
            return visited.cost < cost;
        }
        return false;
    }

    private Maze parseMaze(List<String> input) {
        int height = input.size();
        int width = input.getFirst().length();

        Maze maze = new Maze(width, height);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                maze.set(x, y, switch (input.get(y).charAt(x)) {
                    case '#' -> new Wall();
                    case 'E' -> new Finish();
                    case 'S' -> new Reindeer(x, y, Direction.EAST, maze, false, 0);
                    default -> null;
                });
            }
        }
        return maze;
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

        public Maze clone() {
            Maze clone = new Maze(maze.length, maze[0].length);
            for (int i = 0; i < maze.length; i++)
                clone.maze[i] = maze[i].clone();
            return clone;
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
        private final boolean rotated;
        private final int cost;

        public Reindeer(int x, int y, Direction direction, Maze maze, boolean rotated, int cost) {
            this.x = x;
            this.y = y;
            this.direction = direction;
            this.maze = maze;
            this.rotated = rotated;
            this.cost = cost;
        }

        public Reindeer clone(int x, int y) {
            return new Reindeer(x, y, direction, maze, false, cost + 1);
        }

        public Reindeer clone(int x, int y, Direction direction) {
            return new Reindeer(x, y, direction, maze, true, cost + 1001);
        }

        @Override
        public String toString() {
            return "R";
        }
    }
}
