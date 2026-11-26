
package TicyTacyToe.Games.TicTacToe;

import TicyTacyToe.Engine.Board;
import TicyTacyToe.Engine.Move;

public class TicTacToeBoard implements Board {

    private final String[][] board;

    public TicTacToeBoard() {
        board = new String[3][3];
        clearBoard();
    }

    @Override
    public void printBoard() {
        for (String[] row : board) {
            for (String cell : row) {
                System.out.print("[" + cell + "]");
            }
            System.out.println();
        }
    }

    @Override
    public void clearBoard() {
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                board[row][column] = " ";
            }
        }
    }

    @Override
    public String[][] getBoard() {
        return board;
    }

    @Override
    public boolean checkWin() {
        return hasRowWon() || hasColumnWon() || hasDiagonalWon();
    }

    private boolean hasRowWon() {
        for (int row = 0; row < 3; row++) {
            if (!board[row][0].equals(" ")
                    && board[row][0].equals(board[row][1])
                    && board[row][1].equals(board[row][2])) {
                return true;
            }
        }

        return false;
    }

    private boolean hasColumnWon() {
        for (int column = 0; column < 3; column++) {
            if (!board[0][column].equals(" ")
                    && board[0][column].equals(board[1][column])
                    && board[1][column].equals(board[2][column])) {
                return true;
            }
        }

        return false;
    }

    private boolean hasDiagonalWon() {

        // Check top-left to bottom-right
        if (!board[0][0].equals(" ")
                && board[0][0].equals(board[1][1])
                && board[1][1].equals(board[2][2])) {
            return true;
        }

        // Check top-right to bottom-left
        if (!board[0][2].equals(" ")
                && board[0][2].equals(board[1][1])
                && board[1][1].equals(board[2][0])) {
            return true;
        }

        return false;
    }

    @Override
    public boolean checkFull() {
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                if (board[row][column].equals(" ")) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public boolean checkMove(int row, int column) {
        if (row < 0 || row >= 3 || column < 0 || column >= 3) {
            return false;
        }

        return board[row][column].equals(" ");
    }
    @Override
    public void doMove(Move move, char playerSymbol) {

        int row = move.getRow();
        int column = move.getColumn();

        if (checkMove(row, column)) {
            board[row][column] = String.valueOf(playerSymbol);
        }
    }
}
