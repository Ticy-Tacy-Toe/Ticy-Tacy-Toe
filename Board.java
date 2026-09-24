package TicyTacyToe;

public class Board {

    private final String[][] board;

    public Board() {
        board = new String[][] {
                { " ", " ", " " },
                { " ", " ", " " },
                { " ", " ", " " }
        };
    }

    public void printboard() {
        for (String[] row : board) {
            for (String cell : row) {
                System.out.print("[" + cell + "]");
            }
            System.out.println();
        }
    }

    public String[][] clearBoard() {
        int n = board.length;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                board[i][j] = " ";
            }
        }
        return board;
    }

    public String[][] getBoard() {
        return board;
    }

    public boolean checkRow() {
        for (int i = 0; i < board.length; i++) {
            // Ensure cell is not empty, then compare across all 3 columns
            if (!board[i][0].equals(" ") &&
                    board[i][0].equals(board[i][1]) &&
                    board[i][1].equals(board[i][2])) {
                return true; // Return true as soon as ANY row wins
            }
        }

        return false; // Return false ONLY after all rows have been checked
    }

    public boolean checkColumn() {
        for (int i = 0; i < board.length; i++) {
            // Ensure cell is not empty, then compare across all 3 columns
            if (!board[0][i].equals(" ") &&
                    board[0][i].equals(board[1][i]) &&
                    board[1][i].equals(board[2][i])) {
                return true; // Return true as soon as ANY row wins
            }
        }

        return false; // Return false ONLY after all rows have been checked
    }

    public boolean checkDiagonal() {
        for (int i = 0; i < board.length; i++) {
            if (!board[i][i].equals(" ") &&
                    board[0][0].equals(board[1][1]) &&
                    board[1][1].equals(board[2][2])) {
                return true; // Return true as soon as ANY row wins
            }
        }
        if (!board[0][2].equals(" ") &&
                !board[1][1].equals(" ") &&
                !board[2][0].equals(" ") &&
                board[0][2].equals(board[1][1]) &&
                board[1][1].equals(board[2][0])) {
            return true; // Return true as soon as ANY row wins
        } else {
            return false;
        }
    }

    public boolean checkFull() {
        int full = 0;
        int n = board.length;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (board[i][j] != " ") {
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

}
