package dev.gerits.aoc2024.day10;

import dev.gerits.aoc2024.AdventDay;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Day10 implements AdventDay {

    public static final Integer MAX_NODE_VALUE = 9;
    private static final List<Position> DIRECTIONS = List.of(
            new Position(1, 0),
            new Position(0, 1),
            new Position(-1, 0),
            new Position(0, -1)
    );

    public static void main(String[] args) throws Exception {
        new Day10().run();
    }

    @Override
    public void run() throws Exception {
        List<String> input = Files.readAllLines(Path.of(Day10.class.getResource("input.txt").toURI()));

        part1(input);
        part2(input);
    }

    private void part1(List<String> input) {
        Tree tree = buildTree(input);

        Integer result = tree.nodes.stream()
                .map(node -> node.findReachableNines().size())
                .reduce(0, Integer::sum);

        System.out.printf("Answer part 1: %d%n", result);
    }

    private void part2(List<String> input) {
        Tree tree = buildTree(input);

        Integer result = tree.nodes.stream()
                .map(Node::countPaths)
                .reduce(0, Integer::sum);

        System.out.printf("Answer part 2: %d%n", result);
    }

    private Tree buildTree(List<String> input) {
        Tree tree = new Tree();

        for (int y = 0; y < input.size(); y++) {
            for (int x = 0; x < input.getFirst().length(); x++) {
                Position position = new Position(x, y);
                int value = Character.getNumericValue(input.get(y).charAt(x));

                if (value == 0) {
                    Node node = tree.addNode(position, value);
                    findNextNodes(node, input, position, value);
                }
            }
        }

        tree.cleanupDeadTracks();
        return tree;
    }

    private void findNextNodes(Node node, List<String> input, Position position, int value) {
        for (Position direction : DIRECTIONS) {
            Position nextPosition = position.add(direction);
            if (isInBounds(input, nextPosition)) {
                int nextValue = Character.getNumericValue(
                        input.get(nextPosition.y()).charAt(nextPosition.x())
                );
                if (nextValue == value + 1) {
                    Node newNode = node.addNode(nextPosition, nextValue);
                    findNextNodes(newNode, input, nextPosition, nextValue);
                }
            }
        }
    }

    private boolean isInBounds(List<String> input, Position position) {
        if (position.x() < 0 || position.y() < 0) {
            return false;
        }
        if (position.y() > input.size() - 1) {
            return false;
        }
        if (position.x() > input.getFirst().length() - 1) {
            return false;
        }
        return true;
    }

    private record Position(int x, int y) {
        public Position add(Position direction) {
            return new Position(this.x() + direction.x(), this.y() + direction.y());
        }
    }

    private class Tree extends Node {
        public Tree() {
            super(null, null, null);
        }
    }

    private class Node {
        public final Set<Node> nodes = new HashSet<>();

        public final Position position;
        public final Integer value;
        public final Node parent;

        public Node(Position position, Integer value, Node parent) {
            this.position = position;
            this.value = value;
            this.parent = parent;
        }

        public Node addNode(Position position, Integer value) {
            Node node = new Node(position, value, this);
            nodes.add(node);
            return node;
        }

        public void cleanupDeadTracks() {
            Set<Node> nodesToRemove = new HashSet<>();
            for (Node node : nodes) {
                if (MAX_NODE_VALUE.equals(node.value)) {
                    continue;
                }
                node.cleanupDeadTracks();
                if (node.nodes.isEmpty()) {
                    nodesToRemove.add(node);
                }
            }
            nodes.removeAll(nodesToRemove);
        }

        public Integer countPaths() {
            if (MAX_NODE_VALUE.equals(value)) {
                return 1;
            }
            return nodes.stream()
                    .map(Node::countPaths)
                    .reduce(0, Integer::sum);
        }

        public Set<Position> findReachableNines() {
            if (MAX_NODE_VALUE.equals(value)) {
                return Set.of(position);
            }
            return nodes.stream()
                    .flatMap((Node node) -> node.findReachableNines().stream())
                    .collect(Collectors.toSet());
        }
    }
}
