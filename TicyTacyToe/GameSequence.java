package TicyTacyToe;

import TicyTacyToe.AI.RandomAI;
import TicyTacyToe.Move.ValidateMove;
import TicyTacyToe.Players.AIPlayer;
import TicyTacyToe.Players.HumanPlayer;
import TicyTacyToe.Players.Player;
import java.util.Scanner;

public class GameSequence {

    public static void run(Board board, Scanner scanner) {
        Player humanPlayer = new HumanPlayer(scanner, 2);
        char aiSymbol = (humanPlayer.getSymbol() == 'X') ? 'O' : 'X';
        Player aiPlayer = new AIPlayer(aiSymbol, 1, humanPlayer);

        int playerID = 1;
        boolean end = false;

        while (!end) {
            boolean validMove = false;

            if (playerID == 1) { // ai's turn
                while (!validMove) {
                    // Domme AI
                    int[] input = RandomAI.aiChoice();
                    // slimme AI
                    //int[] aiMove = MinimaxAI.bestMove(board, aiPlayer.getSymbol(), humanPlayer);
                    int row = input[0];
                    int col = input[1];

                    if (board.checkMove(row, col)) {
                        board.doMove(row, col, aiPlayer.getSymbol());
                        validMove = true;
                    }
                }
                System.out.println(" ");
            } else { // our turn
                System.out.println("\nEnter your move (example 1,3):");
                String input = scanner.nextLine();
                validMove = ValidateMove.validateMove(input, board, humanPlayer.getSymbol());
            }

            if (validMove) {
                board.printboard();

                if (checkWin(board) || board.checkFull()) {
                    boolean wantsToStop = endingScreen(playerID, board, humanPlayer, aiPlayer,
                            board.checkFull(), scanner);
                    if (wantsToStop) {
                        end = true;
                    } else {
                        board.clearBoard();
                        board.printboard();
                    }
                } else {
                    playerID = switchPlayer(playerID);
                }
            }
        }
    }

    private static int switchPlayer(int player) {
        return (player == 1) ? 2 : 1;
    }

    private static boolean checkWin(Board board) {
        return board.checkRow() || board.checkColumn() || board.checkDiagonal();
    }

    private static boolean endingScreen(int winner, Board board, Player humanPlayer, Player aiPlayer,
            boolean checkFull, Scanner scanner) {
        if (checkFull && !checkWin(board)) {
            System.out.println("Board is full - draw!");
        } else {
            if (winner == 2) {
                humanPlayer.addPoint();
                System.out.println("you have won!");
            } else if (winner == 1) {
                aiPlayer.addPoint();
                System.out.println("you lost :(");
            }
            System.out.println(humanPlayer.getName() + ": " + humanPlayer.getScore() + " points");
            System.out.println(aiPlayer.getName() + ": " + aiPlayer.getScore() + " points");
        }

        System.out.println("Do you want to play again? Y/N");
        String doorspelen = scanner.nextLine().toUpperCase();

        if (!doorspelen.isEmpty() && doorspelen.charAt(0) == 'Y') {
            return false; // Speel door
        } else {
            System.out.println("Goodbye!");
            return true;
        }
    }
}