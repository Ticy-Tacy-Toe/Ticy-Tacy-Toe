
package TicyTacyToe;

import java.util.Scanner;
import TicyTacyToe.CLI.CLI;
import TicyTacyToe.Server.*;
//cinema new line
public class Main {

    public static void main(String[] args) {

        Board board = new Board();
        Scanner scanner = new Scanner(System.in);

        CLI.intro(scanner);

        boolean result = CLI.offORon();

        if (result) {
            System.out.print("Enter your player name: ");
            String playerName = scanner.nextLine();

            ServerConnection connection = new ServerConnection();

            if (connection.connectServer(playerName)) {
                ServerMenu serverMenu =
                        new ServerMenu(connection, scanner, playerName);

                serverMenu.start();
            } else {
                System.out.println("Could not log in to the game server.");
            }

            connection.disconnect();

        } else {
            GameSequence.run(board, scanner);
            board.printboard();
        }
    }
}
