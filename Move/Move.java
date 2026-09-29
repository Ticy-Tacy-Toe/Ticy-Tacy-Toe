package TicyTacyToe.Move;

import java.util.Scanner;

public class Move implements MoveInterface {

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
                    return new int[]{
                            Integer.parseInt(input[0]) - 1,
                            Integer.parseInt(input[1]) - 1
                    };
                } catch (NumberFormatException ignored) {
                }

            }

            System.out.println("Invalid input.");
        }
    }
}