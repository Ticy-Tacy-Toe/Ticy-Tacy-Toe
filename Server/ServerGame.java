
package TicyTacyToe.Server;

import java.io.IOException;
import java.util.Scanner;

public class ServerGame {

    private final ServerConnection connection;
    private final Scanner scanner;

    public ServerGame(ServerConnection connection, Scanner scanner) {
        this.connection = connection;
        this.scanner = scanner;
    }

    public void start() {
        System.out.println("1. Find a game");
        System.out.println("2. Challenge a player");

        int choice = 0;

        while (choice != 1 && choice != 2) {
            System.out.print("Choose an option: ");

            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                scanner.nextLine();
            } else {
                scanner.nextLine();
            }

            if (choice != 1 && choice != 2) {
                System.out.println("Invalid input.");
            }
        }

        if (choice == 1) {
            findGame();
        } else {
            challengePlayer();
        }
    }

    private void findGame() {
        connection.sendCommand("get gamelist");

        try {
            String response = connection.readResponse();
            System.out.println(response);

            String gameList = connection.readResponse();
            System.out.println(gameList);

            System.out.print("Enter the game type: ");
            String gameType = scanner.nextLine();

            connection.sendCommand("subscribe " + gameType);
            System.out.println(connection.readResponse());

            System.out.println("Waiting for an opponent...");
        } catch (IOException e) {
            System.out.println("Error communicating with server.");
        }
    }

    private void challengePlayer() {
        System.out.print("Enter the player's name: ");
        String playerName = scanner.nextLine();

        System.out.print("Enter the game type: ");
        String gameType = scanner.nextLine();

        connection.sendCommand(
                "challenge \"" + playerName + "\" \"" + gameType + "\""
        );

        try {
            System.out.println(connection.readResponse());
            System.out.println("Challenge sent.");
        } catch (IOException e) {
            System.out.println("Error communicating with server.");
        }
    }
}
