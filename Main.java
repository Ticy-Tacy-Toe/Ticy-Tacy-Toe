package TicyTacyToe;

import java.util.Scanner;
import TicyTacyToe.CLI.CLI;
import TicyTacyToe.Server.*;

public class Main {
    public static void main(String[] args) {
        Board board = new Board();
        Scanner scanner = new Scanner(System.in);

        CLI.intro(scanner);
        boolean result = CLI.offORon();

        if (result) {
            System.out.println("\nChoose your online player:");
            System.out.println("1. Human");
            System.out.println("2. Random AI");
            System.out.println("3. Minimax AI");
            System.out.print("Choice: ");

            int playerMode;

            switch (scanner.nextLine().trim()) {
                case "1":
                    playerMode = TicyTacyToe.Server.GameSequence.HUMAN;
                    break;
                case "2":
                    playerMode = TicyTacyToe.Server.GameSequence.RANDOM_AI;
                    break;
                case "3":
                    playerMode = TicyTacyToe.Server.GameSequence.MINMAX_AI;
                    break;
                default:
                    System.out.println("Invalid choice.");
                    return;
            }

            System.out.print("Enter your player name: ");
            String playerName = scanner.nextLine().trim();

            if (playerName.isEmpty()) {
                System.out.println("Player name cannot be empty.");
                return;
            }

            ServerConnection connection = new ServerConnection();

            if (connection.connectServer(playerName)) {
                ServerMenu serverMenu = new ServerMenu(
                        connection,
                        scanner,
                        playerName,
                        playerMode
                );

                serverMenu.start();
            } else {
                System.out.println("Could not log in to the game server.");
            }

            connection.disconnect();

        } else {
            OfflineGameController.run(board, scanner);
            board.printboard();
        }
    }
}
