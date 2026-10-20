package TicyTacyToe;

import java.util.Scanner;
import TicyTacyToe.CLI.CLI;

public class Main {
    public static void main(String[] args) {

        Board board = new Board();
        Scanner scanner = new Scanner(System.in);

        CLI.intro(scanner);

        GameSequence.run(board, scanner);

        board.printboard();
    }

}