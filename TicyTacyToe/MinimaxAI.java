package TicyTacyToe;

public class MinimaxAI {
    public static int miniMax(Board board, int depth, boolean isMaximizing, char aiSymbol, Player humanPlayer) {
        char winner = getWinner(board);
        boolean isFull = board.checkFull();

        if (winner == aiSymbol) {
            return 10 - depth;
        } else if (winner != aiSymbol && winner != ' ') {
            return depth - 10;
        } else if (isFull && winner == ' ') {
            return 0;
        } else {
            String[][] grid = board.getBoard();
            int best;
            if (isMaximizing) {
                best = Integer.MIN_VALUE;
            } else {
                best = Integer.MAX_VALUE;
            }

            for (int row = 0; row < grid.length; row++) {
                for (int col = 0; col < grid.length; col++) {
                    if (grid[row][col].equals(" ")) {
                        if (isMaximizing) {
                            grid[row][col] = String.valueOf(aiSymbol);
                        } else {
                            grid[row][col] = String.valueOf(humanPlayer.getSymbol());
                        }

                        int points = miniMax(board, depth + 1, !isMaximizing, aiSymbol, humanPlayer);
                        grid[row][col] = " ";

                        if (isMaximizing) {
                            best = Math.max(best, points);
                        } else {
                            best = Math.min(best, points);
                        }
                    }
                }
            }
            return best;
        }
    }
public static char getWinner(Board board) {
        String[][] grid = board.getBoard();

        for (int row = 0; row < grid.length; row++) {
            if (!grid[row][0].equals(" ") && grid[row][0].equals(grid[row][1]) && grid[row][1].equals(grid[row][2])) {
                return grid[row][0].charAt(0);
            }
        }

        for (int row = 0; row < grid.length; row++) {
            if (!grid[0][row].equals(" ") && grid[0][row].equals(grid[1][row]) && grid[1][row].equals(grid[2][row])) {
                return grid[0][row].charAt(0);
            }
        }

        if (!grid[0][0].equals(" ") &&
            grid[0][0].equals(grid[1][1]) &&
            grid[1][1].equals(grid[2][2])) {
            return grid[0][0].charAt(0);
        }

        if (!grid[0][2].equals(" ") &&
            grid[0][2].equals(grid[1][1]) &&
            grid[1][1].equals(grid[2][0])) {
            return grid[0][2].charAt(0);
        }
        return ' ';
    }

    public static int[] bestMove(Board board, char aiSymbol, Player humanPlayer) {
        int bestScore = Integer.MIN_VALUE;
        int[] bestMove = new int[]{-1, -1};
        String[][] grid = board.getBoard();

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (board.checkMove(row, col)) {
                    grid[row][col] = String.valueOf(aiSymbol);
                    int score = miniMax(board, 0, false, aiSymbol, humanPlayer);
                    grid[row][col] = " ";

                    if (score > bestScore) {
                        bestScore = score;
                        bestMove[0] = row;
                        bestMove[1] = col;
                    }
                }
            }
        }
        return bestMove;
    }
}