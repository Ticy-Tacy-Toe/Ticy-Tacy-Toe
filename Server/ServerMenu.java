package TicyTacyToe.Server;

import java.util.Scanner;

public class ServerMenu {

    private final ServerGame serverGame;
    private final Scanner scanner;

    public ServerMenu(
            ServerConnection connection,
            Scanner scanner,
            String playerName
    ) {
        this.scanner = scanner;
        this.serverGame = new ServerGame(
                connection, scanner, playerName
        );
    }

    public void start() {
        boolean running = true;

        while (running) {
            System.out.println("\n=== Online Tic-Tac-Toe ===");
            System.out.println("1. Find a game");
            System.out.println("2. Challenge a player");
            System.out.println("3. Back");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    serverGame.findGame();
                    break;

                case "2":
                    serverGame.challengePlayer();
                    break;

                case "3":
                    running = false;
                    break;

                default:
                    System.out.println("Invalid option.");
                    break;
            }
        }
    }
}