package TicyTacyToe.Engine.Players;

import TicyTacyToe.Engine.Board;
import TicyTacyToe.Engine.Move;
import TicyTacyToe.Games.TicTacToe.TicTacToeMove;

import java.util.Scanner;

public class HumanPlayer extends Player {

    private final Scanner scanner;

    public HumanPlayer(String name, int number) {
        super(name, number);
        scanner = new Scanner(System.in);
    }

    @Override
    public Move makeMove(Board board) {

        System.out.print("Enter your move (row,column): ");

        String input = scanner.nextLine();

        String[] parts = input.split(",");

        int row = Integer.parseInt(parts[0].trim()) - 1;
        int column = Integer.parseInt(parts[1].trim()) - 1;

        return new TicTacToeMove(row, column);
    }
}