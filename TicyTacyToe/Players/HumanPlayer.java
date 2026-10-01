package TicyTacyToe.Players;

import TicyTacyToe.Board;
import TicyTacyToe.Move.ValidateMove;
import java.util.Scanner;

public class HumanPlayer extends Player {
    
    private Scanner scanner;

    public HumanPlayer(Scanner scanner, int number) {
        super(askName(scanner), chooseSymbol(scanner), number);
        this.scanner = scanner;
    }

    private static String askName(Scanner scanner) {
        System.out.println("What is your name?: ");
        return scanner.nextLine();
    }

    private static char chooseSymbol(Scanner scanner) {
        char symbol = ' ';

        while (symbol != 'X' && symbol != 'O') {
            System.out.println("Choose X or O:");
            String input = scanner.nextLine().toUpperCase();
            symbol = input.charAt(0);
        }

        return symbol;
    }

    private int[] parseInput(String input) {
        String[] parts = input.split(",");
        int row = Integer.parseInt(parts[0].trim()) - 1;
        int col = Integer.parseInt(parts[1].trim()) - 1;
        return new int[]{row, col};
    }
    
    @Override
    public int[] makeMove(Board board) {
        System.out.println("\nEnter your move (example 1,3):");
        String input = scanner.nextLine();
        if (ValidateMove.validateMove(input, board, getSymbol())) {
            return parseInput(input);
        }
        return makeMove(board);
    }

}