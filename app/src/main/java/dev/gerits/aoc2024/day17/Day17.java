package dev.gerits.aoc2024.day17;

import dev.gerits.aoc2024.AdventDay;

import java.util.ArrayList;
import java.util.List;

public class Day17 implements AdventDay {

    private static final List<Command> COMMANDS = List.of(
            new CommandAdv(),
            new CommandBxl(),
            new CommandBst(),
            new CommandJnz(),
            new CommandBxc(),
            new CommandOut(),
            new CommandBdv(),
            new CommandCdv()
    );

    public static void main(String[] args) throws Exception {
        new Day17().run();
    }

    @Override
    public void run() throws Exception {
        Computer computer = new Computer(47006051, 0, 0);

        List<Integer> program = List.of(2, 4, 1, 3, 7, 5, 1, 5, 0, 3, 4, 3, 5, 5, 3, 0);

        part1(computer, program);
    }

    private void part1(Computer computer, List<Integer> program) {
        while (computer.instructionPointer < program.size()) {
            int instruction = program.get(computer.instructionPointer);
            int operand = program.get(computer.instructionPointer + 1);

            COMMANDS.get(instruction).execute(computer, operand);
        }

        System.out.printf("Result part 1: %s%n", String.join(",", computer.output.stream()
                .map(String::valueOf)
                .toList()));
    }

    private interface Command {
        void execute(Computer computer, int operand);
    }

    private static class CommandAdv implements Command {
        @Override
        public void execute(Computer computer, int operand) {
            long comboOperand = OperandUtils.getOperand(operand, computer);
            computer.registerA = (long) Math.floor(
                    computer.registerA / Math.pow(2, comboOperand)
            );
            computer.instructionPointer += 2;
        }
    }

    private static class CommandBxl implements Command {
        @Override
        public void execute(Computer computer, int operand) {
            computer.registerB = computer.registerB ^ operand;
            computer.instructionPointer += 2;
        }
    }

    private static class CommandBst implements Command {
        @Override
        public void execute(Computer computer, int operand) {
            long comboOperand = OperandUtils.getOperand(operand, computer);

            String binaryResult = "000" + Integer.toBinaryString((int) (comboOperand % 8));
            computer.registerB = Integer.parseInt(binaryResult.substring(binaryResult.length() - 3), 2);
            computer.instructionPointer += 2;
        }
    }

    private static class CommandOut implements Command {
        @Override
        public void execute(Computer computer, int operand) {
            long comboOperand = OperandUtils.getOperand(operand, computer);
            computer.output.add((int) (comboOperand % 8));
            computer.instructionPointer += 2;
        }
    }

    private static class CommandJnz implements Command {
        @Override
        public void execute(Computer computer, int operand) {
            if (computer.registerA != 0 && computer.instructionPointer != operand) {
                computer.instructionPointer = operand;
            } else {
                computer.instructionPointer += 2;
            }
        }
    }

    private static class CommandBxc implements Command {
        @Override
        public void execute(Computer computer, int operand) {
            computer.registerB = computer.registerB ^ computer.registerC;
            computer.instructionPointer += 2;
        }
    }

    private static class CommandBdv implements Command {
        @Override
        public void execute(Computer computer, int operand) {
            long comboOperand = OperandUtils.getOperand(operand, computer);
            computer.registerB = (int) Math.floor(
                    computer.registerA / Math.pow(2, comboOperand)
            );
            computer.instructionPointer += 2;
        }
    }

    private static class CommandCdv implements Command {
        @Override
        public void execute(Computer computer, int operand) {
            long comboOperand = OperandUtils.getOperand(operand, computer);
            computer.registerC = (long) Math.floor(
                    computer.registerA / Math.pow(2, comboOperand)
            );
            computer.instructionPointer += 2;
        }
    }

    private static class OperandUtils {
        public static long getOperand(int comboOperand, Computer computer) {
            return switch (comboOperand) {
                case 0, 1, 2, 3 -> comboOperand;
                case 4 -> computer.registerA;
                case 5 -> computer.registerB;
                case 6 -> computer.registerC;
                default -> throw new IllegalArgumentException("Invalid operand");
            };
        }

    }

    private static class Computer {
        long registerA;
        long registerB;
        long registerC;
        int instructionPointer = 0;
        List<Integer> output = new ArrayList<>();

        public Computer(long registerA, long registerB, long registerC) {
            this.registerA = registerA;
            this.registerB = registerB;
            this.registerC = registerC;
        }

        public void reset(long registerA, long registerB, long registerC) {
            this.registerA = registerA;
            this.registerB = registerB;
            this.registerC = registerC;
            this.instructionPointer = 0;
            this.output.clear();
        }
    }
}
