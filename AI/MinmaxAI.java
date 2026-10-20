package TicyTacyToe.AI;

import TicyTacyToe.Board;
import TicyTacyToe.Players.Player;
import java.util.ArrayList;
import java.util.List;
//test
public class MinmaxAI {
    public static int miniMax(Board board, int depth, boolean isMaximizing, char aiSymbol, Player humanPlayer) {
        char winner = getWinner(board);
        if (winner != ' ') return winnerScore(winner, aiSymbol, depth);
        if (board.checkFull()) return 0;

        char symbol = isMaximizing ? aiSymbol : humanPlayer.getSymbol();
        int best = isMaximizing ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        for (int[] cell : emptyCells(board)) {
            board.getBoard()[cell[0]][cell[1]] = String.valueOf(symbol);
            int points = miniMax(board, depth + 1, !isMaximizing, aiSymbol, humanPlayer);
            board.getBoard()[cell[0]][cell[1]] = " ";
            best = isMaximizing ? Math.max(best, points) : Math.min(best, points);
        }
        return best;
    }

    private static int winnerScore(char winner, char aiSymbol, int depth) {
        return winner == aiSymbol ? 10 - depth : depth - 10;
    }

    private static List<int[]> emptyCells(Board board) {
        List<int[]> cells = new ArrayList<>();
        String[][] grid = board.getBoard();

        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid.length; col++) {
                if (grid[row][col].equals(" ")) cells.add(new int[]{row, col});
            }
        }
        return cells;
    }
    public static char getWinner(Board board) {
        String[][] grid = board.getBoard();

        for (int i = 0; i < grid.length; i++) {
            char rowWinner = lineWinner(grid[i][0], grid[i][1], grid[i][2]);
            if (rowWinner != ' ') return rowWinner;

            char colWinner = lineWinner(grid[0][i], grid[1][i], grid[2][i]);
            if (colWinner != ' ') return colWinner;
        }

        char diagonal = lineWinner(grid[0][0], grid[1][1], grid[2][2]);
        if (diagonal != ' ') return diagonal;

        return lineWinner(grid[0][2], grid[1][1], grid[2][0]);
    }

    private static char lineWinner(String a, String b, String c) {
    if (!a.equals(" ") && a.equals(b) && b.equals(c)) {
        return a.charAt(0);
    }
    return ' ';
    }

public static int[] bestMove(Board board, char aiSymbol, Player humanPlayer) {
    int bestScore = Integer.MIN_VALUE;
    int[] bestMove = {-1, -1};
    String[][] grid = board.getBoard();

    for (int[] cell : emptyCells(board)) {
        grid[cell[0]][cell[1]] = String.valueOf(aiSymbol);
        int score = miniMax(board, 1, false, aiSymbol, humanPlayer);
        grid[cell[0]][cell[1]] = " ";

        if (score > bestScore) {
            bestScore = score;
            bestMove = cell;
        }
    }
    return bestMove;
}
}