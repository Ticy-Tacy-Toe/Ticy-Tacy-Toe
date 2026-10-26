package TicyTacyToe.Move;

import java.util.Scanner;

public class Move implements MoverInterface {

    private final Scanner scanner;

    public Move(Scanner scanner) {
        this.scanner = scanner;
    }

    @Override
    public int[] getMove() {
        while (true) {
            System.out.print("Enter move (row, column): example: 1, 3 ");

            String[] input = scanner.nextLine()
                    .replace(" ", "")
                    .split(",");

            if (input.length == 2) {
                try {
                    int row = Integer.parseInt(input[0]);
                    int column = Integer.parseInt(input[1]);

                    if (row >= 1 && row <= 3
                            && column >= 1 && column <= 3) {
                        return new int[]{row - 1, column - 1};
                    }
                } catch (NumberFormatException ignored) {
                    // Invalid input.
                }
            }

            System.out.println("Invalid input.");
        }
    }
}