package TicyTacyToe.Move;

import java.util.Scanner;

public class Move implements MoverInterface {

    private final Scanner scanner = new Scanner(System.in);

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

                    if (row > 0 && row <= 3 && column > 0 && column <=3) {
                        return new int[]{row - 1, column - 1};
                    }
                } catch (NumberFormatException ignored) {
                    // Input wasn't a number.
                }
            }

            System.out.println("Invalid input.");
        }
    }
}