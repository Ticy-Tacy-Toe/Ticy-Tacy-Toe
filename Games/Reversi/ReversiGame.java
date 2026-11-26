package TicyTacyToe.Games.Reversi;

import TicyTacyToe.Engine.Board;
import TicyTacyToe.Engine.Game;
import TicyTacyToe.Engine.GameState;
import TicyTacyToe.Engine.Move;
import TicyTacyToe.Engine.Players.Player;

public class ReversiGame implements Game {
    @Override
    public boolean makeMove(Move move) {
        return false;
    }

    @Override
    public boolean isGameOver() {
        return false;
    }

    @Override
    public Player getCurrentPlayer() {
        return null;
    }

    @Override
    public Board getBoard() {
        return null;
    }

    @Override
    public GameState getState() {
        return null;
    }
}
