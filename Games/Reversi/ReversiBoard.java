package TicyTacyToe.Games.Reversi;

import TicyTacyToe.Engine.Board;
import TicyTacyToe.Engine.Move;

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
    public void doMove(Move move, char playerSymbol) {

    }
}