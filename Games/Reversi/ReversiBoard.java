package TicyTacyToe.Games.Reversi;

import TicyTacyToe.Engine.Board;

public class ReversiBoard implements Board{

    @Override
    public void printBoard() {

    }

    @Override
    public void clearBoard() {

    }

    @Override
    public String[][] getBoard() {
        return new String[0][];
    }

    @Override
    public boolean checkWin() {
        return false;
    }

    @Override
    public boolean checkFull() {
        return false;
    }

    @Override
    public boolean checkMove(int row, int column) {
        return false;
    }

    @Override
    public void doMove(int[] input, char playerSymbol) {

    }
}