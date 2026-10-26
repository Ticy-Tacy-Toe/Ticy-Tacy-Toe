package TicyTacyToe.Server;

import TicyTacyToe.Board;

import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GameSequence {

    private final ServerConnection connection;
    private final Board board;
    private final Scanner scanner;
    private final String playerName;

    private char playerSymbol;
    private char opponentSymbol;

    public GameSequence(
            ServerConnection connection,
            Scanner scanner,
            String playerName
    ) {
        this.connection = connection;
        this.scanner = scanner;
        this.playerName = playerName;
        this.board = new Board();
    }

    public void start() throws IOException {
        System.out.println("Waiting for the game to start...");

        while (true) {
            String message = connection.readResponse();

            if (message.equals(ServerConnection.CONNECTION_LOST)) {
                if (reconnect()) {
                    continue;
                }
                return;
            }

            System.out.println(message);

            if (message.startsWith("ERR")) {
                System.out.println("Server rejected the command: " + message);
                continue;
            }

            if (message.contains("SVR GAME MATCH")) {
                String firstPlayer = extractValue(message, "PLAYERTOMOVE");

                if (firstPlayer == null) {
                    System.out.println("Could not determine the first player.");
                    continue;
                }

                if (firstPlayer.equalsIgnoreCase(playerName)) {
                    playerSymbol = 'X';
                    opponentSymbol = 'O';
                } else {
                    playerSymbol = 'O';
                    opponentSymbol = 'X';
                }

                System.out.println("Match started!");
                System.out.println("Your symbol: " + playerSymbol);
                board.clearBoard();
                board.printboard();

            } else if (message.contains("SVR GAME YOURTURN")) {
                playMove();

            } else if (message.contains("SVR GAME MOVE")) {
                processMove(message);

            } else if (message.contains("SVR GAME WIN")) {
                System.out.println("You won!");
                board.printboard();
                returnToLobbyOrRematch();

            } else if (message.contains("SVR GAME LOSS")) {
                System.out.println("You lost!");
                board.printboard();
                returnToLobbyOrRematch();

            } else if (message.contains("SVR GAME DRAW")) {
                System.out.println("The game is a draw!");
                board.printboard();
                returnToLobbyOrRematch();
            }
        }
    }

    private void playMove() {
        board.printboard();

        while (true) {
            System.out.print(
                    "Enter move (row, column), or type 'forfeit': "
            );

            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("forfeit")) {
                connection.sendCommand("forfeit");
                System.out.println("Forfeit requested.");
                return;
            }

            String[] coordinates = input.replace(" ", "").split(",");

            if (coordinates.length != 2) {
                System.out.println("Invalid input. Example: 1,3");
                continue;
            }

            try {
                int row = Integer.parseInt(coordinates[0]) - 1;
                int column = Integer.parseInt(coordinates[1]) - 1;

                if (row < 0 || row > 2 || column < 0 || column > 2) {
                    System.out.println("Enter row and column numbers from 1 to 3.");
                    continue;
                }

                if (!board.checkMove(row, column)) {
                    System.out.println("That position is already occupied.");
                    continue;
                }

                int position = row * 3 + column;
                connection.sendCommand("move " + position);
                return;

            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Example: 1,3");
            }
        }
    }

    private void processMove(String message) {
        String player = extractValue(message, "PLAYER");
        String serverMove = extractValue(message, "MOVE");

        if (player == null || serverMove == null) {
            System.out.println("Could not read the server's move.");
            return;
        }

        try {
            int position = Integer.parseInt(serverMove);

            if (position < 0 || position > 8) {
                System.out.println("Invalid move received from server.");
                return;
            }

            int row = position / 3;
            int column = position % 3;

            char symbol = player.equalsIgnoreCase(playerName)
                    ? playerSymbol
                    : opponentSymbol;

            if (board.checkMove(row, column)) {
                board.doMove(new int[]{row, column}, symbol);
                board.printboard();
            } else {
                System.out.println(
                        "The server reported an occupied position. "
                                + "The local board may be out of sync."
                );
            }

        } catch (NumberFormatException e) {
            System.out.println("Could not read the server's move.");
        }
    }

    private void returnToLobbyOrRematch() {
        System.out.println("\n=== Match Finished ===");
        System.out.println("1. Rematch (find another opponent)");
        System.out.println("2. Return to lobby");

        while (true) {
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine().trim();

            if (choice.equals("1")) {
                board.clearBoard();
                connection.sendCommand("subscribe tic-tac-toe");
                System.out.println("Searching for another opponent...");
                return;

            } else if (choice.equals("2")) {
                System.out.println("Returning to the lobby...");
                return;

            } else {
                System.out.println("Invalid option. Enter 1 or 2.");
            }
        }
    }

    private boolean reconnect() {
        System.out.println("Connection to the server was lost.");
        System.out.print("Attempt to reconnect? (y/n): ");

        String answer = scanner.nextLine().trim();

        if (!answer.equalsIgnoreCase("y")) {
            System.out.println("Returning from the online match.");
            return false;
        }

        System.out.println("Reconnecting...");

        if (connection.reconnect(playerName)) {
            System.out.println("Reconnected to the server.");
            System.out.println(
                    "Restoring the previous match depends on server support."
            );
            return true;
        }

        System.out.println("Could not reconnect to the server.");
        return false;
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
}