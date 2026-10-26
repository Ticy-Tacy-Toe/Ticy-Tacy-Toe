package TicyTacyToe.Server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServerGame {

    private static final long CHALLENGE_TIMEOUT_MILLIS = 30_000;
    private static final long ACCEPT_TIMEOUT_MILLIS = 15_000;

    private final ServerConnection connection;
    private final Scanner scanner;
    private final String playerName;
    private final int playerMode;

    private final String gameType = "tic-tac-toe";

    private final List<IncomingChallenge> pendingChallenges =
            new CopyOnWriteArrayList<>();

    private static class IncomingChallenge {
        private final String number;
        private final String challenger;

        private IncomingChallenge(String number, String challenger) {
            this.number = number;
            this.challenger = challenger;
        }
    }

    public ServerGame(
            ServerConnection connection,
            Scanner scanner,
            String playerName,
            int playerMode
    ) {
        this.connection = connection;
        this.scanner = scanner;
        this.playerName = playerName;
        this.playerMode = playerMode;

        connection.setChallengeHandler(this::receiveChallenge);
    }

    public void findGame() {
        System.out.println("Searching for an opponent...");
        connection.sendCommand("subscribe " + gameType);
        waitForGame(0, null);
    }

    public void challengePlayer() {
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

        waitForGame(
                CHALLENGE_TIMEOUT_MILLIS,
                opponentName + " did not accept within "
                        + (CHALLENGE_TIMEOUT_MILLIS / 1000)
                        + " seconds: the challenge was declined or ignored."
        );
    }

    public int pendingChallengeCount() {
        return pendingChallenges.size();
    }

    public void handleIncomingChallenges() {
        if (pendingChallenges.isEmpty()) {
            System.out.println("No incoming challenges.");
            return;
        }

        for (IncomingChallenge challenge :
                new ArrayList<>(pendingChallenges)) {

            if (!pendingChallenges.contains(challenge)) {
                continue;
            }

            System.out.println(
                    "\nChallenge from " + challenge.challenger
                            + " (#" + challenge.number + ")"
            );

            System.out.print(
                    "y = accept, n = decline, Enter = decide later: "
            );

            String answer = scanner.nextLine().trim();

            if (answer.equalsIgnoreCase("y")
                    || answer.equalsIgnoreCase("yes")) {

                pendingChallenges.remove(challenge);

                connection.sendCommand(
                        "challenge accept " + challenge.number
                );

                System.out.println("Challenge accepted.");

                waitForGame(
                        ACCEPT_TIMEOUT_MILLIS,
                        "The game did not start. "
                                + "The challenge may no longer be valid."
                );

                return;

            } else if (answer.equalsIgnoreCase("n")
                    || answer.equalsIgnoreCase("no")) {

                pendingChallenges.remove(challenge);

                System.out.println(
                        "Challenge from " + challenge.challenger
                                + " declined."
                );

            } else {
                System.out.println("Challenge left open.");
            }
        }
    }

    private void receiveChallenge(String message) {
        String challengeNumber = extractValue(
                message,
                "CHALLENGENUMBER"
        );

        if (message.toUpperCase(Locale.ROOT)
                .contains("CHALLENGE CANCELLED")) {
            withdrawChallenge(challengeNumber);
            return;
        }

        String challenger = extractValue(message, "CHALLENGER");
        String receivedGameType = extractValue(message, "GAMETYPE");

        if (challengeNumber == null) {
            System.out.println(
                    "\nReceived a challenge without a number."
            );
            return;
        }

        if (receivedGameType != null
                && !receivedGameType.equalsIgnoreCase(gameType)) {

            System.out.println(
                    "\nIgnored a challenge for another game ("
                            + receivedGameType + ")."
            );
            return;
        }

        for (IncomingChallenge existing : pendingChallenges) {
            if (existing.number.equals(challengeNumber)) {
                return;
            }
        }

        String name = challenger == null ? "Unknown" : challenger;

        pendingChallenges.add(
                new IncomingChallenge(challengeNumber, name)
        );

        System.out.println(
                "\n>> " + name + " challenged you! Open 'Incoming "
                        + "challenges' in the menu to accept or decline, "
                        + "or just ignore it."
        );
    }

    private void withdrawChallenge(String challengeNumber) {
        if (challengeNumber == null) {
            return;
        }

        for (IncomingChallenge challenge : pendingChallenges) {
            if (challenge.number.equals(challengeNumber)) {
                pendingChallenges.remove(challenge);

                System.out.println(
                        "\n>> The challenge from " + challenge.challenger
                                + " was withdrawn."
                );

                return;
            }
        }
    }

    private void waitForGame(
            long timeoutMillis,
            String timeoutMessage
    ) {
        try {
            GameSequence gameSequence = new GameSequence(
                    connection,
                    scanner,
                    playerName,
                    timeoutMillis,
                    timeoutMessage,
                    playerMode
            );

            gameSequence.start();

        } catch (IOException e) {
            System.out.println(
                    "Error during the online game: " + e.getMessage()
            );
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

    public void waitForTournamentMatch() {
        System.out.println("Waiting for a tournament match...");
        System.out.println(
                "The tournament server must assign your match."
        );

        waitForGame(0, null);
    }
}
