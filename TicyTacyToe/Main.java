package TicyTacyToe;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        intro();

        Board board = new Board();
        Scanner scanner = new Scanner(System.in);

        scanner.nextLine();

        GameSequence.run(board, scanner);

        board.printboard();
    }

    public static void intro() {
        System.out.println("Gday welcome to Ticy Tacy Toe!");
        System.out.println("Rules: Player 1 and player 2, represented by X and O, take turns \n" +
                "marking the spaces in a 3*3 grid. The player who succeeds in placing \n" +
                "three of their marks in a horizontal, vertical, or diagonal row wins");
        System.out.println("Press enter to continue. :)");
    }
}