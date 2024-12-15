package dev.gerits.aoc2024.day15;

import dev.gerits.aoc2024.AdventDay;
import dev.gerits.aoc2024.common.Position;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Day15 implements AdventDay {

    public static void main(String[] args) throws Exception {
        new Day15().run();
    }

    @Override
    public void run() throws Exception {
        List<String> data = Files.readAllLines(Path.of(Day15.class.getResource("input.txt").toURI()));

        int gridHeight = findGridHeight(data);
        Grid grid = parseGrid(data.getFirst().length(), gridHeight, data);
        List<Direction> moves = parseMoves(data);

        moves.forEach(grid::moveRobot);
        System.out.println(grid);

        System.out.printf("Result part 1: %d%n", grid.countValueOfBoxes());
    }

    private List<Direction> parseMoves(List<String> data) {
        List<Direction> directions = new ArrayList<>();
        boolean startParsing = false;
        for (int i = 0; i < data.size(); i++) {
            String line = data.get(i);
            if (line.isEmpty()) {
                startParsing = true;
                continue;
            }
            if (startParsing) {
                directions.addAll(line.chars().mapToObj(item -> switch (item) {
                    case 'v' -> Direction.DOWN;
                    case '>' -> Direction.RIGHT;
                    case '^' -> Direction.UP;
                    case '<' -> Direction.LEFT;
                    default -> throw new IllegalStateException("Unexpected value: " + item);
                }).toList());
            }
        }
        return directions;
    }

    private Grid parseGrid(int gridWidth, int gridHeight, List<String> input) {
        Grid grid = new Grid(gridWidth, gridHeight);

        for (int y = 0; y < gridHeight; y++) {
            for (int x = 0; x < gridWidth; x++) {
                char field = input.get(y).charAt(x);
                switch (field) {
                    case '#':
                        grid.set(x, y, new Wall());
                        continue;
                    case 'O':
                        grid.set(x, y, new Box());
                        continue;
                    case '@':
                        grid.setRobot(x, y);
                        continue;
                    default:
                }
            }
        }

        return grid;
    }

    private int findGridHeight(List<String> data) {
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).isEmpty()) {
                return i;
            }
        }
        return 0;
    }


    private enum Direction {
        UP(0, -1),
        RIGHT(1, 0),
        DOWN(0, 1),
        LEFT(-1, 0);

        private final int x;
        private final int y;

        Direction(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    private class Grid {
        private Item[][] grid;
        private Position robotPosition;

        public Grid(int width, int height) {
            grid = new Item[width][height];
        }

        public void moveRobot(Direction direction) {
            moveItem(robotPosition.x(), robotPosition.y(), direction);
        }

        private boolean moveItem(int originalX, int originalY, Direction direction) {
            int targetY = originalY + direction.y;
            int targetX = originalX + direction.x;
            if (!isFreeOrMovedBox(targetX, targetY, direction)) {
                return false;
            }
            Item item = grid[originalY][originalX];
            grid[originalY][originalX] = null;
            grid[targetY][targetX] = item;
            if (item instanceof Robot) {
                robotPosition.x(targetX);
                robotPosition.y(targetY);
            }
            return true;
        }

        private boolean isFreeOrMovedBox(int targetX, int targetY, Direction direction) {
            if (grid[targetY][targetX] instanceof Box) {
                return moveItem(targetX, targetY, direction);
            }
            if (grid[targetY][targetX] instanceof Wall) {
                return false;
            }
            return true;
        }

        public void set(int x, int y, Item item) {
            grid[y][x] = item;
        }

        public void setRobot(int x, int y) {
            set(x, y, new Robot());
            robotPosition = new Position(x, y);
        }

        public int countValueOfBoxes() {
            int result = 0;
            for (int y = 0; y < grid.length; y++) {
                for (int x = 0; x < grid[0].length; x++) {
                    if (grid[y][x] instanceof Box) {
                        result += (100 * y) + x;
                    }
                }
            }
            return result;
        }

        @Override
        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();
            for (Item[] gridLine : grid) {
                for (Item gridItem : gridLine) {
                    if (gridItem != null) {
                        stringBuilder.append(gridItem);
                    } else {
                        stringBuilder.append('.');
                    }
                }
                stringBuilder.append('\n');
            }
            return stringBuilder.toString();
        }
    }

    private interface Item {
    }

    private class Wall implements Item {
        @Override
        public String toString() {
            return "#";
        }
    }

    private class Robot implements Item {

        @Override
        public String toString() {
            return "@";
        }
    }

    private class Box implements Item {
        @Override
        public String toString() {
            return "O";
        }
    }

}
