package dev.gerits.aoc2024.day24;

import dev.gerits.aoc2024.AdventDay;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.BiFunction;

public class Day24 implements AdventDay {

    public static final String EXAMPLE = """
            x00: 1
            x01: 0
            x02: 1
            x03: 1
            x04: 0
            y00: 1
            y01: 1
            y02: 1
            y03: 1
            y04: 1
            
            ntg XOR fgs -> mjb
            y02 OR x01 -> tnw
            kwq OR kpj -> z05
            x00 OR x03 -> fst
            tgd XOR rvg -> z01
            vdt OR tnw -> bfw
            bfw AND frj -> z10
            ffh OR nrd -> bqk
            y00 AND y03 -> djm
            y03 OR y00 -> psh
            bqk OR frj -> z08
            tnw OR fst -> frj
            gnj AND tgd -> z11
            bfw XOR mjb -> z00
            x03 OR x00 -> vdt
            gnj AND wpb -> z02
            x04 AND y00 -> kjc
            djm OR pbm -> qhw
            nrd AND vdt -> hwm
            kjc AND fst -> rvg
            y04 OR y02 -> fgs
            y01 AND x02 -> pbm
            ntg OR kjc -> kwq
            psh XOR fgs -> tgd
            qhw XOR tgd -> z09
            pbm OR djm -> kpj
            x03 XOR y03 -> ffh
            x00 XOR y04 -> ntg
            bfw OR bqk -> z06
            nrd XOR fgs -> wpb
            frj XOR qhw -> z04
            bqk OR frj -> z07
            y03 OR x01 -> nrd
            hwm AND bqk -> z03
            tgd XOR rvg -> z12
            tnw OR pbm -> gnj
            """;

    public static void main(String[] args) throws Exception {
        new Day24().run();
    }

    @Override
    public void run() throws Exception {
//        List<String> input = EXAMPLE.lines().toList();
        List<String> input = Files.readAllLines(Path.of(Day24.class.getResource("input.txt").toURI()));

        Map<String, Boolean> inputWires = new HashMap<>();
        Map<String, Boolean> outputWires = new HashMap<>();
        List<Processor> processors = new ArrayList<>();

        parseInput(input, inputWires, processors, outputWires);

        for (Map.Entry<String, Boolean> inputWire : inputWires.entrySet()) {
            processWire(inputWire.getKey(), inputWire.getValue(), processors, outputWires);
        }

        System.out.println(outputWires.entrySet().stream()
                .sorted((a, b) -> b.getKey().compareTo(a.getKey()))
                .toList());

        String result = outputWires.entrySet().stream()
                .sorted((a, b) -> b.getKey().compareTo(a.getKey()))
                .map(entry -> Boolean.TRUE.equals(entry.getValue()) ? "1" : "0")
                .reduce("", (a, b) -> a + b);

        System.out.println("Result part 1: " + Long.parseLong(result, 2));
    }

    private void processWire(String key, Boolean value, List<Processor> processors, Map<String, Boolean> outputWires) {
        processors.stream()
                .filter(processor -> processor.inputWire1.equals(key) || processor.inputWire2.equals(key))
                .forEach(processor -> {
                    Optional<Boolean> result = processor.tryOperation(key, value);
                    result.ifPresent(resultValue -> {
                        if (processor.outputWire.startsWith("z")) {
                            outputWires.put(processor.outputWire, resultValue);
                        } else {
                            processWire(processor.outputWire, resultValue, processors, outputWires);
                        }
                    });
                });
    }

    private void parseInput(List<String> input, Map<String, Boolean> inputWires, List<Processor> processors, Map<String, Boolean> outputWires) {
        boolean initialValuesParsed = false;
        for (String line : input) {
            if (line.isEmpty()) {
                initialValuesParsed = true;
                continue;
            }

            if (!initialValuesParsed) {
                String[] parts = line.split(": ");
                inputWires.put(parts[0], Integer.parseInt(parts[1]) == 1);
            } else {
                String[] parts = line.split(" -> ");
                String[] left = parts[0].split(" ");
                String right = parts[1];

                processors.add(new Processor(
                        left[0],
                        left[2],
                        Operator.valueOf(left[1]),
                        right
                ));

                if (left[0].startsWith("z")) {
                    outputWires.put(left[0], null);
                }
                if (left[2].startsWith("z")) {
                    outputWires.put(left[2], null);
                }
            }
        }
    }

    private enum Operator {
        AND((a, b) -> a && b),
        OR((a, b) -> a || b),
        XOR((a, b) -> a ^ b);

        private BiFunction<Boolean, Boolean, Boolean> operation;

        Operator(BiFunction<Boolean, Boolean, Boolean> operations) {
            this.operation = operations;
        }

        public Boolean apply(Boolean a, Boolean b) {
            return operation.apply(a, b);
        }
    }

    private class Processor {
        private final String inputWire1;
        private final String inputWire2;
        private final Operator operator;
        private final String outputWire;

        private Boolean valueWire1;
        private Boolean valueWire2;


        public Processor(
                String inputWire1,
                String inputWire2,
                Operator operator,
                String outputWire
        ) {
            this.inputWire1 = inputWire1;
            this.inputWire2 = inputWire2;
            this.operator = operator;
            this.outputWire = outputWire;
        }

        public Optional<Boolean> tryOperation(String key, Boolean value) {
            if (key.equals(inputWire1)) {
                valueWire1 = value;
            } else if (key.equals(inputWire2)) {
                valueWire2 = value;
            }

            if (valueWire1 != null && valueWire2 != null) {
                Boolean result = operator.apply(valueWire1, valueWire2);
                valueWire1 = null;
                valueWire2 = null;
                return Optional.of(result);
            }
            return Optional.empty();
        }
    }
}
