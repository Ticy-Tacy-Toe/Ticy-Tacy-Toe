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
        char aiSymbol = (humanPlayer.getSymbol() == 'X') ? 'O' : 'X';
        Player aiPlayer = new AIPlayer(aiSymbol, 1, humanPlayer);

        int option = 0;
        while (option != 1 && option != 2) {
        System.out.println("Kies AI: 1 = random, 2 = minimax");
        String line = scanner.nextLine();
        if (line.equals("1")) {
            option = 1;
        } else if (line.equals("2")) {
            option = 2;
        } else {
            System.out.println("Invalid input");
        }
    }

        int playerID = 1;
        boolean end = false;

        while (!end) {
            boolean validMove = false;

            if (playerID == 1) { // ai's turn
                while (!validMove) {
                    int[] aiMove;

                    if (option == 1) {
                        aiMove = RandomAI.aiChoice();
                    } else {
                        aiMove = MinmaxAI.bestMove(board, aiSymbol, humanPlayer);
                    }
                    
                    int row = aiMove[0];
                    int col = aiMove[1];

                    if (board.checkMove(row, col)) {
                        board.doMove(aiMove, aiPlayer.getSymbol());
                        validMove = true;
                    }
                }
                System.out.println(" ");
            } else { // our turn
                Move move = new Move();
                int[] result = move.getMove();

                int row = result[0];
                int column = result[1];

                if (board.checkMove(row, column)) {
                    board.doMove(result, humanPlayer.getSymbol());
                }

            }

            board.printboard();

            if (checkWin(board) || board.checkFull()) {
                boolean wantsToStop = endingScreen(playerID, board, humanPlayer, aiPlayer,
                        board.checkFull());
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

    private static int switchPlayer(int player) {
        return (player == 1) ? 2 : 1;
    }

    private static boolean checkWin(Board board) {
        return board.checkRow() || board.checkColumn() || board.checkDiagonal();
    }

    public static boolean endingScreen(int winner, Board board, Player humanPlayer, Player aiPlayer,
            boolean checkFull) {
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
        Scanner scanner = new Scanner(System.in);
        String doorspelen = scanner.nextLine().toUpperCase();

        if (!doorspelen.isEmpty() && doorspelen.charAt(0) == 'Y') {
            return false; // Speel door
        } else {
            System.out.println("Goodbye!");
            return true;
        }
    }
}