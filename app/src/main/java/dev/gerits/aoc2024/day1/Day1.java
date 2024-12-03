package dev.gerits.aoc2024.day1;

import dev.gerits.aoc2024.AdventDay;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class Day1 implements AdventDay {

    public static final String WORD_SEPARATOR = " {3}";

    public static void main(String[] args) throws Exception {
        new Day1().run();
    }

    @Override
    public void run() throws Exception {
        Path path = Path.of(Day1.class.getResource("input.txt").toURI());

        List<Integer> list1 = new ArrayList<>();
        List<Integer> list2 = new ArrayList<>();

        Files.readAllLines(path).stream()
                .map(item -> item.split(WORD_SEPARATOR))
                .map(item -> List.of(Integer.valueOf(item[0]), Integer.valueOf(item[1])))
                .forEach(itemPair -> {
                    list1.add(itemPair.get(0));
                    list2.add(itemPair.get(1));
                });

        Collections.sort(list1);
        Collections.sort(list2);

        int difference = 0;

        for (int i = 0; i < list1.size(); ++i) {
            difference += Math.abs(list1.get(i) - list2.get(i));
        }

        System.out.printf("Answer part 1: %d%n", difference);

        AtomicReference<Long> similarity = new AtomicReference<>(0L);

        list1.forEach(item -> {
            similarity.updateAndGet(v -> v + item * list2.stream().filter(list2item -> list2item.equals(item)).count());
        });

        System.out.printf("Answer part 2: %d%n", similarity.get());
    }
}