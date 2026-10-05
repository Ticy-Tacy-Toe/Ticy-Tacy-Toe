package TicyTacyToe.Players;

import TicyTacyToe.Board;
import TicyTacyToe.Move.*;
import java.util.Scanner;
//test
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
            String input = scanner.nextLine().trim().toUpperCase();

            if (input.equals("X")) {
                symbol = 'X';
            } else if (input.equals("O")) {
                symbol = 'O';
            } else {
                System.out.println("Invalid input");
            }
        }

        return symbol;
    }

    
    //@Override
    //public int[] makeMove(Board board) {
    //    System.out.println("\nEnter your move (example 1,3):");
    //    String input = scanner.nextLine();
    //    if (ValidateMove.validateMove(input, board, getSymbol())) {
    //        return parseInput(input);
    //    }
    //    return makeMove(board);
    //}

    @Override 
    public int[] makeMove(Board board) {
        Move move = new Move();
        return move.getMove();
    }

}