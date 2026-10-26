package TicyTacyToe;

import TicyTacyToe.AI.MinmaxAI;
import TicyTacyToe.AI.RandomAI;
import TicyTacyToe.CLI.CLI;
import TicyTacyToe.Move.*;
import TicyTacyToe.Players.AIPlayer;
import TicyTacyToe.Players.HumanPlayer;
import TicyTacyToe.Players.Player;

import java.util.Scanner;

public class OfflineGameController {

    public static void run(Board board, Scanner scanner) {
        int aiOption = CLI.chooseAI(scanner);

        Player humanPlayer;
        Player secondPlayer;

        if (aiOption == 3) {
            humanPlayer = new HumanPlayer(scanner, 1);
            secondPlayer = new HumanPlayer(scanner, 2);

            if (humanPlayer.getSymbol() == secondPlayer.getSymbol()) {
                System.out.println(
                        "Both players cannot use the same symbol. Choose again."
                );
                return;
            }
        } else {
            humanPlayer = new HumanPlayer(scanner, 2);
            secondPlayer = createAIPlayer(humanPlayer);
        }

        int currentPlayer = getStartingPlayer(humanPlayer, secondPlayer);
        boolean gameOver = false;

        while (!gameOver) {
            playTurn(
                    board,
                    humanPlayer,
                    secondPlayer,
                    aiOption,
                    currentPlayer,
                    scanner
            );

            board.printboard();

            if (isGameOver(board)) {
                gameOver = endingScreen(
                        currentPlayer,
                        board,
                        humanPlayer,
                        secondPlayer,
                        scanner,
                        aiOption
                );

                if (!gameOver) {
                    board.clearBoard();
                    board.printboard();
                    currentPlayer = getStartingPlayer(humanPlayer, secondPlayer);
                }
            } else {
                currentPlayer = switchPlayer(currentPlayer);
            }
        }
    }

    private static Player createAIPlayer(Player humanPlayer) {
        char aiSymbol = (humanPlayer.getSymbol() == 'X') ? 'O' : 'X';
        return new AIPlayer(aiSymbol, 1, humanPlayer);
    }

    private static void playTurn(
            Board board,
            Player humanPlayer,
            Player secondPlayer,
            int aiOption,
            int currentPlayer,
            Scanner scanner
    ) {
        if (currentPlayer == 1) {
            if (aiOption == 3) {
                playHumanTurn(board, secondPlayer, scanner);
            } else {
                playAITurn(
                        board,
                        secondPlayer,
                        humanPlayer,
                        aiOption
                );
            }
        } else {
            playHumanTurn(board, humanPlayer, scanner);
        }
    }

    private static void playAITurn(
            Board board,
            Player aiPlayer,
            Player humanPlayer,
            int aiOption
    ) {
        boolean validMove = false;

        while (!validMove) {
            int[] move = getAIMove(
                    board,
                    aiPlayer,
                    humanPlayer,
                    aiOption
            );

            if (move != null
                    && move.length >= 2
                    && board.checkMove(move[0], move[1])) {
                board.doMove(move, aiPlayer.getSymbol());
                validMove = true;
            }
        }

        System.out.println();
    }

    private static int[] getAIMove(
            Board board,
            Player aiPlayer,
            Player humanPlayer,
            int aiOption
    ) {
        if (aiOption == 1) {
            return RandomAI.aiChoice(board);
        }

        if (aiOption == 2) {
            return MinmaxAI.bestMove(
                    board,
                    aiPlayer.getSymbol(),
                    humanPlayer
            );
        }

        return null;
    }

    private static void playHumanTurn(
            Board board,
            Player player,
            Scanner scanner
    ) {
        boolean validMove = false;

        while (!validMove) {
            Move move = new Move(scanner);
            int[] result = move.getMove();

            if (board.checkMove(result[0], result[1])) {
                board.doMove(result, player.getSymbol());
                validMove = true;
            } else {
                CLI.invalidMove();
            }
        }
    }

    private static boolean isGameOver(Board board) {
        return hasWon(board) || board.checkFull();
    }

    private static int switchPlayer(int player) {
        return (player == 1) ? 2 : 1;
    }

    private static boolean hasWon(Board board) {
        return board.checkRow()
                || board.checkColumn()
                || board.checkDiagonal();
    }

    public static boolean endingScreen(
            int winner,
            Board board,
            Player humanPlayer,
            Player secondPlayer,
            Scanner scanner,
            int aiOption
    ) {
        if (board.checkFull() && !hasWon(board)) {
            CLI.printGameResult("Board is full - draw!");
        } else if (aiOption == 3) {
            Player winningPlayer = (winner == 1)
                    ? secondPlayer
                    : humanPlayer;

            winningPlayer.addPoint();

            CLI.printGameResult(
                    winningPlayer.getName() + " has won!"
            );
        } else if (winner == 2) {
            humanPlayer.addPoint();
            CLI.printGameResult("You have won!");
        } else {
            secondPlayer.addPoint();
            CLI.printGameResult("You lost :(");
        }

        printScores(humanPlayer, secondPlayer);

        return !CLI.wantsToPlayAgain(scanner);
    }

    private static void printScores(
            Player humanPlayer,
            Player secondPlayer
    ) {
        CLI.printScores(
                humanPlayer.getName(),
                humanPlayer.getScore(),
                secondPlayer.getName(),
                secondPlayer.getScore()
        );
    }

    private static int getStartingPlayer(Player firstPlayer, Player secondPlayer) {
        return firstPlayer.getSymbol() == 'X' ? 1 : 2;
    }
}
