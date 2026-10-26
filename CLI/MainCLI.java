
package TicyTacyToe.CLI.MainCLI;

import TicyTacyToe.Board;
import TicyTacyToe.CLI.CLIstartUp;
import TicyTacyToe.OfflineGameController;
import TicyTacyToe.Server.ServerConnection;
import TicyTacyToe.Server.ServerMenu;
import TicyTacyToe.Server.GameSequence;

import java.util.Scanner;

public class MainCLI {

    private final Scanner scanner;

    public MainCLI() {
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        Board board = new Board();

        CLIstartUp.intro(scanner);
        boolean online = CLIstartUp.offORon();

        if (online) {
            startOnlineGame();
        } else {
            OfflineGameController.run(board, scanner);
            board.printboard();
        }
    }

    private void startOnlineGame() {
        System.out.print("Enter your player name: ");
        String playerName = scanner.nextLine().trim();

        if (playerName.isEmpty()) {
            System.out.println("Player name cannot be empty.");
            return;
        }

        int playerMode = chooseOnlinePlayerMode();

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
            System.out.println(
                    "Could not log in to the game server."
            );
        }

        connection.disconnect();
    }

    private int chooseOnlinePlayerMode() {
        System.out.println("\nChoose your online player:");
        System.out.println("1. Human");
        System.out.println("2. Random AI");
        System.out.println("3. Minmax AI");

        while (true) {
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    return GameSequence.HUMAN;

                case "2":
                    return GameSequence.RANDOM_AI;

                case "3":
                    return GameSequence.MINMAX_AI;

                default:
                    System.out.println(
                            "Invalid option. Enter 1, 2, or 3."
                    );
            }
        }
    }
}
