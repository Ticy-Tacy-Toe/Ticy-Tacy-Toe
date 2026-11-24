package TicyTacyToe.Engine.Players;

import TicyTacyToe.Engine.Board;

public class AIPlayer extends Player {

    private AIStrategy strategy;

    @Override
    public Move makeMove(GameBoard board) {
        return strategy.determineMove(board);
    }
}