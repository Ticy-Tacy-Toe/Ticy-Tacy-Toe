package TicyTacyToe.Games.TicTacToe;

import TicyTacyToe.Engine.Move;

public class TicTacToeMove implements Move {

    private final int row;
    private final int column;

    public TicTacToeMove(int row, int column) {
        this.row = row;
        this.column = column;
    }

    @Override
    public int getRow() {
        return row;
    }

    @Override
    public int getColumn() {
        return column;
    }
}