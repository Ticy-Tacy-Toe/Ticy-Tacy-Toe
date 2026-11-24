package TicyTacyToe.Engine;

public interface Board {
    void printBoard();
    void clearBoard();
    String[][] getBoard();
    boolean checkWin();
    boolean checkFull();
    boolean checkMove(int row, int column);
    void doMove(int[] input, char playerSymbol);
}