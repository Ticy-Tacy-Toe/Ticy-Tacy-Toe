package TicyTacyToe.CLI.MainMenuCLI;

import TicyTacyToe.CLI.ServerGameCLI.ServerGameCLI;
import TicyTacyToe.Server.OnlineGameController;
import TicyTacyToe.Server.ServerConnection;

import java.util.Scanner;

public class MainMenuCLI {

    private final ServerGameCLI serverGameCLI;
    private final Scanner scanner;

    public MainMenuCLI(
            ServerConnection connection,
            Scanner scanner,
            String playerName,
            int playerMode
    ) {
        this.scanner = scanner;

        OnlineGameController gameController =
                new OnlineGameController(
                        connection,
                        scanner,
                        playerName,
                        playerMode,
                        message -> System.out.println("\n" + message)
                );

        this.serverGameCLI = new ServerGameCLI(
                gameController,
                scanner
        );
    }

    public void start() {
        boolean running = true;

        while (running) {
            System.out.println("\n=== Online Tic-Tac-Toe ===");
            System.out.println("1. Find a game");
            System.out.println("2. Challenge a player");
            System.out.println(
                    "3. Incoming challenges ("
                            + serverGameCLI.pendingChallengeCount() + ")"
            );
            System.out.println("4. Wait for tournament match");
            System.out.println("5. Quit");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    serverGameCLI.findGame();
                    break;

                case "2":
                    serverGameCLI.challengePlayer();
                    break;

                case "3":
                    serverGameCLI.handleIncomingChallenges();
                    break;

                case "4":
                    serverGameCLI.waitForTournamentMatch();
                    break;

                case "5":
                    running = false;
                    break;

                default:
                    System.out.println("Invalid option.");
                    break;
            }
        }
    }
}
