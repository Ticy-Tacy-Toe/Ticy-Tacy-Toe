package TicyTacyToe;

import TicyTacyToe.AI.MinmaxAI;
import TicyTacyToe.AI.RandomAI;
import TicyTacyToe.Move.*;
import TicyTacyToe.Players.AIPlayer;
import TicyTacyToe.Players.HumanPlayer;
import TicyTacyToe.Players.Player;

import java.util.Scanner;

public class GameSequence {

    public static void run(Board board, Scanner scanner) {
        Player humanPlayer = new HumanPlayer(scanner, 2);
        Player aiPlayer = createAIPlayer(humanPlayer);

        int aiOption = chooseAI(scanner);
        int currentPlayer = 1;
        boolean gameOver = false;

        while (!gameOver) {
            playTurn(board, humanPlayer, aiPlayer, aiOption, currentPlayer);
            board.printboard();

            if (isGameOver(board)) {
                gameOver = endingScreen(
                        currentPlayer,
                        board,
                        humanPlayer,
                        aiPlayer,
                        scanner
                );

                if (!gameOver) {
                    board.clearBoard();
                    board.printboard();
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

    private static int chooseAI(Scanner scanner) {
        while (true) {
            System.out.println("Kies AI: 1 = random, 2 = minimax");
            String input = scanner.nextLine();

            if (input.equals("1")) {
                return 1;
            }

            if (input.equals("2")) {
                return 2;
            }

            System.out.println("Invalid input");
        }
    }

    private static void playTurn(
            Board board,
            Player humanPlayer,
            Player aiPlayer,
            int aiOption,
            int currentPlayer
    ) {
        if (currentPlayer == 1) {
            playAITurn(board, aiPlayer, humanPlayer, aiOption);
        } else {
            playHumanTurn(board, humanPlayer);
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

            if (board.checkMove(move[0], move[1])) {
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
            return RandomAI.aiChoice();
        }

        return MinmaxAI.bestMove(
                board,
                aiPlayer.getSymbol(),
                humanPlayer
        );
    }

    private static void playHumanTurn(
            Board board,
            Player humanPlayer
    ) {
        boolean validMove = false;

        while (!validMove) {
            Move move = new Move();
            int[] result = move.getMove();

            if (board.checkMove(result[0], result[1])) {
                board.doMove(result, humanPlayer.getSymbol());
                validMove = true;
            } else {
                System.out.println("Invalid move");
            }
        }
    }

    private static boolean isGameOver(Board board) {
        return checkWin(board) || board.checkFull();
    }

    private static int switchPlayer(int player) {
        return (player == 1) ? 2 : 1;
    }

    private static boolean checkWin(Board board) {
        return board.checkRow()
                || board.checkColumn()
                || board.checkDiagonal();
    }

    public static boolean endingScreen(
            int winner,
            Board board,
            Player humanPlayer,
            Player aiPlayer,
            Scanner scanner
    ) {
        if (board.checkFull() && !checkWin(board)) {
            System.out.println("Board is full - draw!");
        } else if (winner == 2) {
            humanPlayer.addPoint();
            System.out.println("you have won!");
        } else {
            aiPlayer.addPoint();
            System.out.println("you lost :(");
        }

        printScores(humanPlayer, aiPlayer);

        return !wantsToPlayAgain(scanner);
    }

    private static void printScores(
            Player humanPlayer,
            Player aiPlayer
    ) {
        System.out.println(
                humanPlayer.getName()
                        + ": "
                        + humanPlayer.getScore()
                        + " points"
        );

        System.out.println(
                aiPlayer.getName()
                        + ": "
                        + aiPlayer.getScore()
                        + " points"
        );
    }

    private static boolean wantsToPlayAgain(Scanner scanner) {
        System.out.println("Do you want to play again? Y/N");

        String input = scanner.nextLine().toUpperCase();

        return !input.isEmpty() && input.charAt(0) == 'Y';
    }
}

