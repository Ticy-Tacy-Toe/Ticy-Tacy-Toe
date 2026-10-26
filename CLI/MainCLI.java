
package TicyTacyToe.CLI.MainCLI;

import TicyTacyToe.Board;
import TicyTacyToe.CLI.CLI;
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

        CLI.intro(scanner);
        boolean online = CLI.offORon();

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

        ServerConnection connection = new ServerConnection();

        int playerMode = GameSequence.HUMAN;

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
}
