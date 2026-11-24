package TicyTacyToe;

import TicyTacyToe.Engine.Board;
import java.util.Objects;

public class BoardTicyTacyToe implements Board {

    private final String[][] board;

    public BoardTicyTacyToe() {
        board = new String[][] {
                { " ", " ", " " },
                { " ", " ", " " },
                { " ", " ", " " }
        };
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
        int n = board.length;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                board[i][j] = " ";
            }
        }
    }

    @Override
    public String[][] getBoard() {
        return board;
    }

    @Override
    public boolean checkWin() {
        return checkRow() || checkColumn() || checkDiagonal();
    }

    private boolean checkRow() {
        for (int i = 0; i < board.length; i++) {
            if (!board[i][0].equals(" ") &&
                    board[i][0].equals(board[i][1]) &&
                    board[i][1].equals(board[i][2])) {
                return true;
            }
        }

        return false;
    }

    private boolean checkColumn() {
        for (int i = 0; i < board.length; i++) {
            if (!board[0][i].equals(" ") &&
                    board[0][i].equals(board[1][i]) &&
                    board[1][i].equals(board[2][i])) {
                return true;
            }
        }

        return false;
    }

    private boolean checkDiagonal() {
        for (int i = 0; i < board.length; i++) {
            if (!board[i][i].equals(" ") &&
                    board[0][0].equals(board[1][1]) &&
                    board[1][1].equals(board[2][2])) {
                return true;
            }
        }
        if (!board[0][2].equals(" ") &&
                !board[1][1].equals(" ") &&
                !board[2][0].equals(" ") &&
                board[0][2].equals(board[1][1]) &&
                board[1][1].equals(board[2][0])) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkFull() {
        int full = 0;
        int n = board.length;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (!board[i][j].equals(" ")) {
                    full++;
                }

            }
        }
        if (full == n * n) {
            return true;
        } else {
            return false;
        }

    }
  public boolean checkMove(int row, int column) {
        if (row >= 0 && row <= 2 && column >= 0 && column <= 2) {
            return Objects.equals(board[row][column], " ");
        } else {
            return false;
        }
    }

    public void doMove(int[] input, char playerSymbol){
        int row = input[0];
        int column = input[1];
        if (checkMove(row, column)) {
            board[row][column] = String.valueOf(playerSymbol);
        }
    }
}