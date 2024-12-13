package dev.gerits.aoc2024.day13;

import dev.gerits.aoc2024.AdventDay;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day13 implements AdventDay {

    private static final Pattern BUTTONA_PATTERN = Pattern.compile("Button A: X\\+(.*), Y\\+(.*)");
    private static final Pattern BUTTONB_PATTERN = Pattern.compile("Button B: X\\+(.*), Y\\+(.*)");
    private static final Pattern PRIZE_PATTERN = Pattern.compile("Prize: X=(.*), Y=(.*)");

    public static void main(String[] args) throws Exception {
        new Day13().run();
    }

    @Override
    public void run() throws Exception {
        List<String> input = Files.readAllLines(Path.of(Day13.class.getResource("input.txt").toURI()));

        List<SlotMachine> slotMachines = parseInput(input);

        part1(slotMachines);
        part2(slotMachines);

    }

    private void part1(List<SlotMachine> slotMachines) {
        long result = 0;
        for (SlotMachine slotMachine : slotMachines) {
            result += calculateTokens(slotMachine, 0L);
        }
        System.out.printf("Result part 1: %s%n", result);
    }

    private void part2(List<SlotMachine> slotMachines) {
        long result = 0;
        for (SlotMachine slotMachine : slotMachines) {
            result += calculateTokens(slotMachine, 10000000000000L);
        }
        System.out.printf("Result part 2: %s%n", result);
    }

    private long calculateTokens(SlotMachine slotMachine, long offset) {
        BigDecimal ax = BigDecimal.valueOf(slotMachine.buttonA().x());
        BigDecimal ay = BigDecimal.valueOf(slotMachine.buttonA().y());
        BigDecimal bx = BigDecimal.valueOf(slotMachine.buttonB().x());
        BigDecimal by = BigDecimal.valueOf(slotMachine.buttonB().y());
        BigDecimal prizeX = BigDecimal.valueOf(slotMachine.prize().x()).add(BigDecimal.valueOf(offset));
        BigDecimal prizeY = BigDecimal.valueOf(slotMachine.prize().y()).add(BigDecimal.valueOf(offset));

        BigDecimal det = BigDecimal.ONE.divide(ax.multiply(by).subtract(ay.multiply(bx)), 10000, RoundingMode.HALF_EVEN);

        BigDecimal a = by.multiply(det);
        BigDecimal b = ay.negate().multiply(det);
        BigDecimal c = bx.negate().multiply(det);
        BigDecimal d = ax.multiply(det);

        BigDecimal ra = a.multiply(prizeX).add(c.multiply(prizeY));
        BigDecimal rb = b.multiply(prizeX).add(d.multiply(prizeY));

        if (ra.setScale(2, RoundingMode.HALF_EVEN).compareTo(ra.setScale(0, RoundingMode.HALF_EVEN)) == 0
                && rb.setScale(2, RoundingMode.HALF_EVEN).compareTo(rb.setScale(0, RoundingMode.HALF_EVEN)) == 0) {
            return ra.setScale(0, RoundingMode.HALF_EVEN).multiply(BigDecimal.valueOf(3))
                    .add(rb.setScale(0, RoundingMode.HALF_EVEN)).longValue();
        }
        return 0L;
    }

    private List<SlotMachine> parseInput(List<String> input) {
        List<SlotMachine> slotMachines = new ArrayList<>();

        int index = 0;
        while (index < input.size()) {
            slotMachines.add(new SlotMachine(
                    toPosition(input.get(index++), BUTTONA_PATTERN),
                    toPosition(input.get(index++), BUTTONB_PATTERN),
                    toPosition(input.get(index++), PRIZE_PATTERN)
            ));
            index++;
        }

        return slotMachines;
    }

    private Position toPosition(String line, Pattern pattern) {
        Matcher matcher = pattern.matcher(line);
        if (!matcher.find()) {
            return null;
        }
        return new Position(
                Integer.parseInt(matcher.group(1)),
                Integer.parseInt(matcher.group(2))
        );
    }

    public record SlotMachine(
            Position buttonA,
            Position buttonB,
            Position prize
    ) {
    }

    public record Position(
            int x,
            int y
    ) {
    }

    public record PushResult(
            int a,
            int b
    ) {
    }
}
