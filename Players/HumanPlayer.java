
package TicyTacyToe.Players;

import TicyTacyToe.Board;
import TicyTacyToe.Move.*;

import java.util.Scanner;

public class HumanPlayer extends Player {

    private final Scanner scanner;

    public HumanPlayer(Scanner scanner, int number) {
        super(askName(scanner, number), chooseSymbol(scanner, number), number);
        this.scanner = scanner;
    }

    private static String askName(Scanner scanner, int number) {
        System.out.print("Name player " + number + "? ");
        return scanner.nextLine().trim();
    }

    private static char chooseSymbol(Scanner scanner, int number) {
        char symbol = ' ';

        while (symbol != 'X' && symbol != 'O') {
            System.out.print("Player " + number + ", choose X or O: ");

            String input = scanner.nextLine().trim().toUpperCase();

            if (input.equals("X")) {
                symbol = 'X';
            } else if (input.equals("O")) {
                symbol = 'O';
            } else {
                System.out.println("Invalid input. Choose X or O.");
            }
        }

        return symbol;
    }

    @Override
    public int[] makeMove(Board board) {
        Move move = new Move(scanner);
        return move.getMove();
    }
}
