package TicyTacyToe.Server;

import TicyTacyToe.Board;
import TicyTacyToe.AI.MinmaxAI;
import TicyTacyToe.AI.RandomAI;

import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GameSequence {

    private static final long POLL_INTERVAL_MILLIS = 200;

    public static final int HUMAN = 0;
    public static final int RANDOM_AI = 1;
    public static final int MINMAX_AI = 2;

    private final int playerMode;
    private final boolean tournamentMode;
    private final Runnable tournamentExitHandler;

    private final ServerConnection connection;
    private final Board board;
    private final Scanner scanner;
    private final String playerName;

    private long waitTimeoutMillis;
    private String timeoutMessage;
    private long waitStartedAt;

    private boolean inMatch;

    private char playerSymbol;
    private char opponentSymbol;

    public GameSequence(
            ServerConnection connection,
            Scanner scanner,
            String playerName
    ) {
        this(
                connection,
                scanner,
                playerName,
                0,
                null,
                HUMAN,
                false,
                null
        );
    }

    public GameSequence(
            ServerConnection connection,
            Scanner scanner,
            String playerName,
            long waitTimeoutMillis,
            String timeoutMessage
    ) {
        this(
                connection,
                scanner,
                playerName,
                waitTimeoutMillis,
                timeoutMessage,
                HUMAN,
                false,
                null
        );
    }

    public GameSequence(
            ServerConnection connection,
            Scanner scanner,
            String playerName,
            long waitTimeoutMillis,
            String timeoutMessage,
            int playerMode
    ) {
        this(
                connection,
                scanner,
                playerName,
                waitTimeoutMillis,
                timeoutMessage,
                playerMode,
                false,
                null
        );
    }

    public GameSequence(
            ServerConnection connection,
            Scanner scanner,
            String playerName,
            long waitTimeoutMillis,
            String timeoutMessage,
            int playerMode,
            boolean tournamentMode
    ) {
        this(
                connection,
                scanner,
                playerName,
                waitTimeoutMillis,
                timeoutMessage,
                playerMode,
                tournamentMode,
                null
        );
    }

    public GameSequence(
            ServerConnection connection,
            Scanner scanner,
            String playerName,
            long waitTimeoutMillis,
            String timeoutMessage,
            int playerMode,
            boolean tournamentMode,
            Runnable tournamentExitHandler
    ) {
        this.connection = connection;
        this.scanner = scanner;
        this.playerName = playerName;
        this.board = new Board();
        this.waitTimeoutMillis = waitTimeoutMillis;
        this.timeoutMessage = timeoutMessage;
        this.playerMode = playerMode;
        this.tournamentMode = tournamentMode;
        this.tournamentExitHandler = tournamentExitHandler;
    }

    public void start() throws IOException {
        System.out.println(
                "Waiting for the game to start... "
                        + "(type 'cancel' to stop waiting)"
        );

        waitStartedAt = System.currentTimeMillis();

        while (true) {
            String message = nextMessage();

            if (message == null) {
                return;
            }

            if (message.equals(ServerConnection.CONNECTION_LOST)) {
                reconnect();
                return;
            }

            System.out.println(message);

            if (message.startsWith("ERR")) {
                System.out.println(
                        "Server rejected the command: " + message
                );

                if (!inMatch) {
                    System.out.println("Returning to the lobby.");
                    return;
                }

                continue;
            }

            if (message.contains("SVR GAME MATCH")) {
                String firstPlayer = extractValue(
                        message,
                        "PLAYERTOMOVE"
                );

                if (firstPlayer == null) {
                    System.out.println(
                            "Could not determine the first player."
                    );
                    continue;
                }

                if (firstPlayer.equalsIgnoreCase(playerName)) {
                    playerSymbol = 'X';
                    opponentSymbol = 'O';
                } else {
                    playerSymbol = 'O';
                    opponentSymbol = 'X';
                }

                inMatch = true;
                waitTimeoutMillis = 0;

                board.clearBoard();

                System.out.println("Match started!");
                System.out.println("Your symbol: " + playerSymbol);
                board.printboard();

            } else if (message.contains("SVR GAME YOURTURN")) {
                playMove();

            } else if (message.contains("SVR GAME MOVE")) {
                processMove(message);

            } else if (message.contains("SVR GAME WIN")) {
                inMatch = false;
                System.out.println("You won!");
                board.printboard();

                if (continueAfterMatch()) {
                    continue;
                }

                return;

            } else if (message.contains("SVR GAME LOSS")) {
                inMatch = false;
                System.out.println("You lost!");
                board.printboard();

                if (continueAfterMatch()) {
                    continue;
                }

                return;

            } else if (message.contains("SVR GAME DRAW")) {
                inMatch = false;
                System.out.println("The game is a draw!");
                board.printboard();

                if (continueAfterMatch()) {
                    continue;
                }

                return;
            }
        }
    }

    private boolean continueAfterMatch() {
        if (tournamentMode) {
            return continueTournament();
        }

        return returnToLobbyOrRematch();
    }

    private boolean continueTournament() {
        System.out.println("\n=== Tournament Match Finished ===");
        System.out.println("1. Continue tournament");
        System.out.println("2. Quit tournament mode");

        while (true) {
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine().trim();

            if (choice.equals("1")) {
                board.clearBoard();
                waitStartedAt = System.currentTimeMillis();

                System.out.println(
                        "Waiting for your next tournament match..."
                );

                // Do not subscribe to ordinary matchmaking here.
                // Continue listening for the next tournament match.
                return true;

            } else if (choice.equals("2")) {
                System.out.println("Leaving tournament mode.");

                if (tournamentExitHandler != null) {
                    tournamentExitHandler.run();
                }

                return false;

            } else {
                System.out.println("Invalid option. Enter 1 or 2.");
            }
        }
    }

    private String nextMessage() throws IOException {
        if (inMatch) {
            return connection.readResponse();
        }

        while (true) {
            String message = connection.pollResponse(
                    POLL_INTERVAL_MILLIS
            );

            if (message != null) {
                return message;
            }

            if (playerWantsToCancel()) {
                System.out.println("Waiting cancelled.");
                withdrawRequest();
                return null;
            }

            if (waitTimeoutMillis > 0
                    && System.currentTimeMillis() - waitStartedAt
                    >= waitTimeoutMillis) {

                System.out.println(
                        timeoutMessage != null
                                ? timeoutMessage
                                : "No response from the server."
                );

                withdrawRequest();
                return null;
            }
        }
    }

    private boolean playerWantsToCancel() throws IOException {
        while (System.in.available() > 0) {
            String line = scanner.nextLine().trim();

            if (line.equalsIgnoreCase("cancel")
                    || line.equalsIgnoreCase("c")) {
                return true;
            }

            if (!line.isEmpty()) {
                System.out.println(
                        "Still waiting... type 'cancel' to stop waiting."
                );
            }
        }

        return false;
    }

    private void withdrawRequest() {
        System.out.println("Withdrawing your request...");

        if (connection.restart(playerName)) {
            System.out.println("Back in the lobby.");
        } else {
            System.out.println(
                    "Could not log in again. Check the server and try again."
            );
        }
    }

    private void playMove() {
        if (playerMode == HUMAN) {
            playHumanMove();
            return;
        }

        int[] move;

        if (playerMode == RANDOM_AI) {
            move = RandomAI.aiChoice(board);
        } else if (playerMode == MINMAX_AI) {
            move = MinmaxAI.bestMove(
                    board,
                    playerSymbol,
                    opponentSymbol
            );
        } else {
            System.out.println("Unknown player mode.");
            connection.sendCommand("forfeit");
            return;
        }

        if (move == null
                || move.length < 2
                || move[0] < 0 || move[0] > 2
                || move[1] < 0 || move[1] > 2
                || !board.checkMove(move[0], move[1])) {

            System.out.println("AI could not find a valid move.");
            connection.sendCommand("forfeit");
            return;
        }

        int position = move[0] * 3 + move[1];

        System.out.println(
                "AI plays row " + (move[0] + 1)
                        + ", column " + (move[1] + 1)
        );

        connection.sendCommand("move " + position);
    }

    private void playHumanMove() {
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

            String[] coordinates = input
                    .replace(" ", "")
                    .split(",");

            if (coordinates.length != 2) {
                System.out.println("Invalid input. Example: 1,3");
                continue;
            }

            try {
                int row = Integer.parseInt(coordinates[0]) - 1;
                int column = Integer.parseInt(coordinates[1]) - 1;

                if (row < 0 || row > 2
                        || column < 0 || column > 2) {
                    System.out.println(
                            "Enter row and column numbers from 1 to 3."
                    );
                    continue;
                }

                if (!board.checkMove(row, column)) {
                    System.out.println(
                            "That position is already occupied."
                    );
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
                System.out.println(
                        "Invalid move received from server."
                );
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

    private boolean returnToLobbyOrRematch() {
        System.out.println("\n=== Match Finished ===");
        System.out.println("1. Rematch (find another opponent)");
        System.out.println("2. Return to lobby");

        while (true) {
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine().trim();

            if (choice.equals("1")) {
                board.clearBoard();
                connection.sendCommand("subscribe tic-tac-toe");

                System.out.println(
                        "Searching for another opponent... "
                                + "(type 'cancel' to stop searching)"
                );

                waitStartedAt = System.currentTimeMillis();
                return true;

            } else if (choice.equals("2")) {
                System.out.println("Returning to the lobby...");
                return false;

            } else {
                System.out.println(
                        "Invalid option. Enter 1 or 2."
                );
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

        if (connection.reconnect(playerName)) {
            System.out.println("Reconnected to the server.");
            System.out.println(
                    "The previous match may not be recoverable."
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
