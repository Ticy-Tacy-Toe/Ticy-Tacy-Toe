
package TicyTacyToe.Server;

import TicyTacyToe.Board;
import TicyTacyToe.Move.Move;

import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GameSequence {

    private final ServerConnection connection;
    private final Board board;
    private final Move move;
    private final String playerName;

    private char playerSymbol;
    private char opponentSymbol;

    public GameSequence(
            ServerConnection connection,
            Scanner scanner,
            String playerName
    ) {
        this.connection = connection;
        this.playerName = playerName;
        this.board = new Board();
        this.move = new Move(scanner);
    }

    public void start() throws IOException {
        System.out.println("Waiting for the game to start...");

        while (true) {
            String message = connection.readResponse();

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
                board.printboard();
            } else if (message.contains("SVR GAME YOURTURN")) {
                playMove();
            } else if (message.contains("SVR GAME MOVE")) {
                processOpponentMove(message);
            } else if (message.contains("SVR GAME WIN")) {
                System.out.println("You won!");
                board.printboard();
                break;
            } else if (message.contains("SVR GAME LOSS")) {
                System.out.println("You lost!");
                board.printboard();
                break;
            } else if (message.contains("SVR GAME DRAW")) {
                System.out.println("The game is a draw!");
                board.printboard();
                break;
            }
        }
    }

    private void playMove() {
        board.printboard();

        while (true) {
            int[] coordinates = move.getMove();

            int row = coordinates[0];
            int column = coordinates[1];

            if (!board.checkMove(row, column)) {
                System.out.println("That position is already occupied.");
                continue;
            }

            int position = row * 3 + column;

            connection.sendCommand("move " + position);

            board.doMove(coordinates, playerSymbol);
            board.printboard();

            break;
        }
    }

    private void processOpponentMove(String message) {
        String player = extractValue(message, "PLAYER");
        String serverMove = extractValue(message, "MOVE");

        if (player == null || serverMove == null) {
            return;
        }

        if (player.equalsIgnoreCase(playerName)) {
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

            if (board.checkMove(row, column)) {
                board.doMove(new int[]{row, column}, opponentSymbol);
                board.printboard();
            } else {
                System.out.println("Received a move for an occupied position.");
            }

        } catch (NumberFormatException e) {
            System.out.println("Could not read the opponent's move.");
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
}
