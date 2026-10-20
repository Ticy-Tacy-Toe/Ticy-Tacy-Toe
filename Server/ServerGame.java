
package TicyTacyToe.Server;

import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServerGame {

    private final ServerConnection connection;
    private final Scanner scanner;

    public ServerGame(ServerConnection connection, Scanner scanner) {
        this.connection = connection;
        this.scanner = scanner;

        connection.setChallengeHandler(this::receiveChallenge);
    }

    public void start() {
        System.out.println("1. Find a game");
        System.out.println("2. Challenge a player");

        int choice = 0;

        while (choice != 1 && choice != 2) {
            synchronized (scanner) {
                System.out.print("Choose an option: ");

                if (scanner.hasNextInt()) {
                    choice = scanner.nextInt();
                    scanner.nextLine();
                } else {
                    scanner.nextLine();
                }
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
        connection.sendCommand("subscribe tictactoe");

        System.out.println("Searching for a Tic-Tac-Toe opponent...");
        System.out.println("Waiting for an opponent or an incoming challenge.");

        readServerMessages();
    }

    private void challengePlayer() {
        String playerName;

        synchronized (scanner) {
            System.out.print("Enter the player's name: ");
            playerName = scanner.nextLine().trim();
        }

        if (playerName.isEmpty()) {
            System.out.println("Player name cannot be empty.");
            return;
        }

        connection.sendCommand(
                "challenge \"" + playerName + "\" \"tictactoe\""
        );

        System.out.println("Challenge sent to " + playerName + ".");
        System.out.println("Waiting for the response...");

        readServerMessages();
    }

    private void receiveChallenge(String message) {
        String challenger = extractValue(message, "CHALLENGER");
        String challengeNumber = extractValue(message, "CHALLENGENUMBER");

        if (challenger == null || challengeNumber == null) {
            System.out.println("Received a challenge, but could not read its details.");
            System.out.println(message);
            return;
        }

        System.out.println();
        System.out.println("You received a Tic-Tac-Toe challenge!");
        System.out.println("From: " + challenger);

        String answer;

        synchronized (scanner) {
            System.out.print("Accept this challenge? (yes/no): ");
            answer = scanner.nextLine().trim();
        }

        if (answer.equalsIgnoreCase("yes")
                || answer.equalsIgnoreCase("y")) {

            connection.sendCommand("challenge accept " + challengeNumber);
            System.out.println("Challenge accepted!");

        } else {
            System.out.println("Challenge declined.");
        }
    }

    private String extractValue(String message, String key) {
        Pattern pattern = Pattern.compile(
                key + "\\s*:\\s*\"([^\"]+)\"",
                Pattern.CASE_INSENSITIVE
        );

        Matcher matcher = pattern.matcher(message);

        if (matcher.find()) {
            return matcher.group(1);
        }

        return null;
    }

    private void readServerMessages() {
        try {
            while (true) {
                String message = connection.readResponse();

                if (message == null) {
                    System.out.println("Connection closed by server.");
                    return;
                }

                System.out.println("Server: " + message);

                if (message.contains("SVR GAME MATCH")) {
                    System.out.println("Match found! Ready to start the game.");
                    return;
                }

                if (message.contains("SVR GAME WIN")
                        || message.contains("SVR GAME LOSS")
                        || message.contains("SVR GAME DRAW")) {
                    System.out.println("Game finished.");
                    return;
                }
            }
        } catch (IOException e) {
            System.out.println("Error communicating with server.");
        }
    }
}
