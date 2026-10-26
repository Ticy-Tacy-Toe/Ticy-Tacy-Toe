
package TicyTacyToe.CLI.ServerGameCLI;

import TicyTacyToe.Server.OnlineGameController;

import java.util.List;
import java.util.Scanner;

public class ServerGameCLI {

    private final OnlineGameController gameController;
    private final Scanner scanner;

    public ServerGameCLI(
            OnlineGameController gameController,
            Scanner scanner
    ) {
        this.gameController = gameController;
        this.scanner = scanner;
    }

    public void findGame() {
        System.out.println("Searching for an opponent...");
        gameController.findGame();
    }

    public void challengePlayer() {
        System.out.print("Enter the opponent's player name: ");
        String opponentName = scanner.nextLine().trim();

        if (opponentName.isEmpty()) {
            System.out.println("Player name cannot be empty.");
            return;
        }

        if (gameController.isOwnName(opponentName)) {
            System.out.println("You cannot challenge yourself.");
            return;
        }

        System.out.println("Challenge sent to " + opponentName + ".");
        gameController.challengePlayer(opponentName);
    }

    public void handleIncomingChallenges() {
        List<OnlineGameController.IncomingChallenge> challenges =
                gameController.getPendingChallenges();

        if (challenges.isEmpty()) {
            System.out.println("No incoming challenges.");
            return;
        }

        for (OnlineGameController.IncomingChallenge challenge : challenges) {
            System.out.println(
                    "\nChallenge from " + challenge.getChallenger()
                            + " (#" + challenge.getNumber() + ")"
            );

            System.out.print(
                    "y = accept, n = decline, Enter = decide later: "
            );

            String answer = scanner.nextLine().trim();

            if (answer.equalsIgnoreCase("y")
                    || answer.equalsIgnoreCase("yes")) {

                gameController.acceptChallenge(challenge.getNumber());
                return;

            } else if (answer.equalsIgnoreCase("n")
                    || answer.equalsIgnoreCase("no")) {

                gameController.declineChallenge(challenge.getNumber());

                System.out.println(
                        "Challenge from " + challenge.getChallenger()
                                + " declined."
                );

            } else {
                System.out.println("Challenge left open.");
            }
        }
    }

    public void waitForTournamentMatch() {
        System.out.println("Waiting for a tournament match...");
        System.out.println(
                "The tournament server must assign your match."
        );

        gameController.waitForTournamentMatch();
    }

    public int pendingChallengeCount() {
        return gameController.pendingChallengeCount();
    }

    public void showNotification(String message) {
        System.out.println(message);
    }
}
