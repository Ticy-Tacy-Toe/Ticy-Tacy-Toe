package TicyTacyToe;

import java.util.Scanner;
import TicyTacyToe.CLI.CLI;

import TicyTacyToe.Server.ServerConnection;

public class Main {
    public static void main(String[] args) {

        Board board = new Board();
        Scanner scanner = new Scanner(System.in);

        CLI.intro(scanner);

        System.out.print("Enter your player name: ");
        String playerName = scanner.nextLine();

        ServerConnection connection = new ServerConnection();

        if (connection.connectServer(playerName)) {
            GameSequence.run(board, scanner);
        } else {
            System.out.println("Could not log in to the game server.");
        }

        board.printboard();
        connection.disconnect();


    }

}