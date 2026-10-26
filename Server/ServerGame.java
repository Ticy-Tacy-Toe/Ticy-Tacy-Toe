package TicyTacyToe.Server;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServerGame {

    private final ServerConnection connection;
    private final Scanner scanner;
    private final String playerName;

    private String gameType = "tic-tac-toe";
    private boolean gameStarted = false;

    public ServerGame(ServerConnection connection, Scanner scanner, String playerName) {
        this.connection = connection;
        this.scanner = scanner;
        this.playerName = playerName;

        connection.setChallengeHandler(this::receiveChallenge);
    }

    public void start() {
        boolean running = true;

        while (running) {
            System.out.println("\n=== Online Tic-Tac-Toe ===");
            System.out.println("1. Find a game");
            System.out.println("2. Challenge a player");
            System.out.println("3. Show supported game types");
            System.out.println("4. Back");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    findGame();
                    break;

                case "2":
                    challengePlayer();
                    break;

                case "3":
                    showGameTypes();
                    break;

                case "4":
                    running = false;
                    break;

                default:
                    System.out.println("Invalid option.");
                    break;
            }
        }
    }

    private void findGame() {
        System.out.println("Searching for an opponent...");
        connection.sendCommand("subscribe " + gameType);
        waitForGame();
    }

    private void challengePlayer() {
        System.out.print("Enter the opponent's player name: ");
        String opponentName = scanner.nextLine().trim();

        if (opponentName.isEmpty()) {
            System.out.println("Player name cannot be empty.");
            return;
        }

        if (opponentName.equalsIgnoreCase(playerName)) {
            System.out.println("You cannot challenge yourself.");
            return;
        }

        connection.sendCommand(
                "challenge \"" + opponentName + "\" \"" + gameType + "\""
        );

        System.out.println("Challenge sent to " + opponentName + ".");
        waitForGame();
    }

    private void showGameTypes() {
        System.out.println("Requesting supported game types...");
        connection.sendCommand("get gamelist");
    }

    private void receiveChallenge(String message) {
        System.out.println("\nIncoming challenge:");
        System.out.println(message);

        String challengeNumber = extractValue(message, "CHALLENGENUMBER");
        String challenger = extractValue(message, "CHALLENGER");
        String receivedGameType = extractValue(message, "GAMETYPE");

        if (challengeNumber == null) {
            System.out.println("Could not read the challenge number.");
            return;
        }

        if (receivedGameType != null
                && !receivedGameType.equalsIgnoreCase(gameType)) {
            System.out.println("This challenge is for another game.");
            return;
        }

        System.out.println("Player: " +
                (challenger != null ? challenger : "Unknown"));
        System.out.print("Accept challenge? (yes/no): ");

        String answer = scanner.nextLine().trim();

        if (answer.equalsIgnoreCase("yes")
                || answer.equalsIgnoreCase("y")) {
            connection.sendCommand("challenge accept " + challengeNumber);
            System.out.println("Challenge accepted.");
            waitForGame();
        } else {
            System.out.println("Challenge declined.");
        }
    }

    private String extractValue(String message, String key) {
        Pattern pattern = Pattern.compile(
                key + "\\s*:\\s*\"?([^\",}]+)\"?",
                Pattern.CASE_INSENSITIVE
        );

        Matcher matcher = pattern.matcher(message);

        if (matcher.find()) {
            return matcher.group(1).trim();
        }

        return null;
    }

    private void waitForGame() {
        System.out.println("Waiting for the server to start the game...");
        System.out.println("Return to the game menu when the match begins.");
    }
}