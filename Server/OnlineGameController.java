
package TicyTacyToe.Server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OnlineGameController {

    private static final long CHALLENGE_TIMEOUT_MILLIS = 30_000;
    private static final long ACCEPT_TIMEOUT_MILLIS = 15_000;

    private final ServerConnection connection;
    private final java.util.Scanner scanner;
    private final String playerName;
    private final int playerMode;
    private final Consumer<String> notificationHandler;

    private final String gameType = "tic-tac-toe";

    private volatile boolean tournamentMode;

    private final List<IncomingChallenge> pendingChallenges =
            new CopyOnWriteArrayList<>();

    public static class IncomingChallenge {

        private final String number;
        private final String challenger;

        public IncomingChallenge(String number, String challenger) {
            this.number = number;
            this.challenger = challenger;
        }

        public String getNumber() {
            return number;
        }

        public String getChallenger() {
            return challenger;
        }
    }

    public OnlineGameController(
            ServerConnection connection,
            java.util.Scanner scanner,
            String playerName,
            int playerMode,
            Consumer<String> notificationHandler
    ) {
        this.connection = connection;
        this.scanner = scanner;
        this.playerName = playerName;
        this.playerMode = playerMode;
        this.notificationHandler = notificationHandler;

        connection.setChallengeHandler(this::receiveChallenge);
    }

    public boolean isOwnName(String name) {
        return name.equalsIgnoreCase(playerName);
    }

    public void findGame() {
        connection.sendCommand("subscribe " + gameType);
        waitForGame(0, null);
    }

    public void challengePlayer(String opponentName) {
        connection.sendCommand(
                "challenge \"" + opponentName + "\" \"" + gameType + "\""
        );

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

    public List<IncomingChallenge> getPendingChallenges() {
        return new ArrayList<>(pendingChallenges);
    }

    public void acceptChallenge(String challengeNumber) {
        IncomingChallenge challenge = findChallenge(challengeNumber);

        if (challenge == null) {
            notificationHandler.accept(
                    "This challenge is no longer available."
            );
            return;
        }

        pendingChallenges.remove(challenge);

        connection.sendCommand("challenge accept " + challengeNumber);

        notificationHandler.accept("Challenge accepted.");

        waitForGame(
                ACCEPT_TIMEOUT_MILLIS,
                "The game did not start. "
                        + "The challenge may no longer be valid."
        );
    }

    public void declineChallenge(String challengeNumber) {
        IncomingChallenge challenge = findChallenge(challengeNumber);

        if (challenge != null) {
            pendingChallenges.remove(challenge);
        }
    }

    private IncomingChallenge findChallenge(String challengeNumber) {
        for (IncomingChallenge challenge : pendingChallenges) {
            if (challenge.getNumber().equals(challengeNumber)) {
                return challenge;
            }
        }

        return null;
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

        // Do not accept or queue incoming challenges during a tournament.
        if (tournamentMode) {
            return;
        }

        String challenger = extractValue(message, "CHALLENGER");
        String receivedGameType = extractValue(message, "GAMETYPE");

        if (challengeNumber == null) {
            notificationHandler.accept(
                    "Received a challenge without a number."
            );
            return;
        }

        if (receivedGameType != null
                && !receivedGameType.equalsIgnoreCase(gameType)) {
            notificationHandler.accept(
                    "Ignored a challenge for another game ("
                            + receivedGameType + ")."
            );
            return;
        }

        for (IncomingChallenge existing : pendingChallenges) {
            if (existing.getNumber().equals(challengeNumber)) {
                return;
            }
        }

        String name = challenger == null ? "Unknown" : challenger;

        pendingChallenges.add(
                new IncomingChallenge(challengeNumber, name)
        );

        notificationHandler.accept(
                ">> " + name + " challenged you! Open 'Incoming challenges' "
                        + "in the menu to accept or decline, or just ignore it."
        );
    }

    private void withdrawChallenge(String challengeNumber) {
        if (challengeNumber == null) {
            return;
        }

        for (IncomingChallenge challenge : pendingChallenges) {
            if (challenge.getNumber().equals(challengeNumber)) {
                pendingChallenges.remove(challenge);

                notificationHandler.accept(
                        ">> The challenge from "
                                + challenge.getChallenger()
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
                    playerMode,
                    tournamentMode,
                    () -> tournamentMode = false
            );

            gameSequence.start();

        } catch (IOException e) {
            notificationHandler.accept(
                    "Error during the online game: " + e.getMessage()
            );
        }
    }

    public void waitForTournamentMatch() {
        tournamentMode = true;
        pendingChallenges.clear();

        // The server should already know about tournament participation.
        // Do not subscribe to ordinary matchmaking here.
        waitForGame(0, null);
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
