package TicyTacyToe;

import TicyTacyToe.Move.ValidateMove;
import java.util.Scanner;

public class Test {
    public static void main(String[] args) {
        intro();

        Board board = new Board();
        Scanner scanner = new Scanner(System.in);

        scanner.nextLine();
        Player humanPlayer = Player.createPlayer(scanner, 2);
        char aiSymbol = (humanPlayer.getSymbol() == 'X') ? 'O' : 'X';
        Player aiPlayer = new Player("AI", aiSymbol, 1);

        int player = 2;
        boolean end = false;

        board.printboard();

        while (!end) {
            boolean validMove = false;

            if (player == 1) { //ai's turn

                while (!validMove) {
                    //Domme AI
                    int[] input = AI.aiChoice();
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
                    boolean wantsToStop = endingScreen(player, board, humanPlayer, aiPlayer, board.checkFull());
                    if (wantsToStop) {
                        end = true;
                    } else {
                        board.clearBoard();
                        board.printboard();
                    }
                } else {
                    player = switch_player(player); 
                }
            }
        }
    }

    public static void intro() {
        System.out.println("Gday welcome to Ticy Tacy Toe!");
        System.out.println("Rules: Player 1 and player 2, represented by X and O, take turns \n" +
                "marking the spaces in a 3*3 grid. The player who succeeds in placing \n" +
                "three of their marks in a horizontal, vertical, or diagonal row wins");
        System.out.println("Press enter to continue. :)");
    }

    public static int switch_player(int player) {
        return (player == 1) ? 2 : 1; 
    }

    public static boolean checkWin(Board board) {
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