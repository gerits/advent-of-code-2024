package dev.gerits.aoc2024.day9;

import dev.gerits.aoc2024.AdventDay;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class Day9 implements AdventDay {
    public static void main(String[] args) throws Exception {
        new Day9().run();
    }

    @Override
    public void run() throws Exception {
        String input = Files.readString(Path.of(Day9.class.getResource("input.txt").toURI()));

        part1(input);
        part2(input);
    }

    private void part1(String input) {
        long result = 0L;

        List<Long> values = new ArrayList<>();
        Long index = 0L;
        for (int i = 0; i < input.length(); i++) {
            boolean isValue = i % 2 == 0;

            Long finalIndex = index;
            IntStream.rangeClosed(0, Integer.parseInt(String.valueOf(input.charAt(i))) - 1)
                    .mapToObj(val -> isValue ? finalIndex : -1)
                    .forEach(values::add);
            if (isValue) {
                index++;
            }
        }

        while (values.contains(-1L)) {
            values.set(values.indexOf(-1L), values.getLast());
            values.removeLast();
        }

        for (int i = 0; i < values.size(); i++) {
            result += i * values.get(i);
        }

        System.out.printf("Answer part 1: %d%n", result);
    }

    private void part2(String input) {
        List<Item> values = new ArrayList<>();
        Long index = 0L;
        for (int i = 0; i < input.length(); i++) {
            boolean isValue = i % 2 == 0;

            long size = Long.parseLong(String.valueOf(input.charAt(i)));
            if (isValue) {
                values.add(new File(index, size));
            } else {
                values.add(new Space(size));
            }
            if (isValue) {
                index++;
            }
        }

        while (notAllFilesMoved(values)) {
            int fileIndex = findLastNonMovedFile(values);
            if (fileIndex == -1) {
                continue;
            }
            File file = (File) values.get(fileIndex);
            int spaceOfSize = findSpaceOfSize(values, file.size);
            file.isMoved = true;
            if (spaceOfSize == -1 || spaceOfSize > fileIndex) {
                continue;
            }
            Item space = values.get(spaceOfSize);
            values.remove(file);
            if (space.size.equals(file.size)) {
                values.set(spaceOfSize, file);
                values.add(fileIndex, new Space(file.size));
                compactSpaces(values);
                continue;
            }
            space.size -= file.size;
            values.add(spaceOfSize, file);
            values.add(fileIndex, new Space(file.size));

            compactSpaces(values);
        }

        System.out.println("After:");
        for (int i = 0; i < values.size(); i++) {
            System.out.println("%d - %s".formatted(i, values.get(i).toString()));
        }

        long result = 0L;
        Long counter = 0L;
        for (Item item : values) {
            if (item instanceof Space) {
                counter += item.size;
                continue;
            }
            for (int i = 0; i < item.size; i++) {
                result += counter * ((File) item).id;
                counter++;
            }
        }

        System.out.printf("Answer part 2: %d%n", result);
    }

    private void compactSpaces(List<Item> values) {
        for (int i = values.size() - 2; i >= 0; i--) {
            if (values.get(i) instanceof Space space1 && values.get(i + 1) instanceof Space space2) {
                space1.size += space2.size;
                values.remove(i + 1);
            }
            if (values.get(i) instanceof Space space) {
                if (space.size == 0) {
                    values.remove(i);
                }
            }
        }
    }

    private int findLastNonMovedFile(List<Item> values) {
        for (int i = values.size() - 1; i >= 0; i--) {
            if (values.get(i) instanceof File file) {
                if (!file.isMoved) {
                    return i;
                }
            }
        }
        return -1;
    }

    private boolean notAllFilesMoved(List<Item> values) {
        return values.stream()
                .filter(File.class::isInstance)
                .anyMatch(item -> !item.isMoved);
    }

    private int findSpaceOfSize(List<Item> values, Long size) {
        for (int i = 0; i < values.size(); i++) {
            Item item = values.get(i);
            if (item instanceof Space) {
                if (item.size >= size) {
                    return i;
                }
            }
        }
        return -1;
    }

    private abstract class Item {
        public Long size;
        public boolean isMoved;

        public Item(Long size, boolean isMoved) {
            this.size = size;
            this.isMoved = isMoved;
        }

        @Override
        public String toString() {
            return size.toString();
        }
    }

    private class File extends Item {
        public Long id;

        public File(Long id, Long size) {
            super(size, false);
            this.id = id;
        }

        @Override
        public String toString() {
            return "file: %d - %s".formatted(id, super.toString());
        }
    }

    private class Space extends Item {
        public Space(Long size) {
            super(size, false);
        }

        @Override
        public String toString() {
            return "space: %s".formatted(super.toString());
        }
    }
}
