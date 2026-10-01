package TicyTacyToe.Move;

import TicyTacyToe.Board;

public class ValidateMove {

    public static boolean validateMove(String input, Board board, char playerSymbol) {
        try {
            String[] coordinates = input.split(",");
            if (coordinates.length != 2) {
                System.out.println("Invalid move!");
                return false;
            }

            int row = Integer.parseInt(coordinates[0].trim()) - 1;
            int col = Integer.parseInt(coordinates[1].trim()) - 1;

            if (board.checkMove(row, col)) {
                board.doMove(row, col, playerSymbol);
                return true;
            } else {
                System.out.println("Invalid move!");
                return false;
            }
        } catch (Exception e) {
            System.out.println("Invalid move!");
            return false;
        }
    }
}