package TicyTacyToe.Engine;

import TicyTacyToe.Engine.Players.Player;

public interface Game {

    boolean makeMove(Move move);

    boolean isGameOver();

    Player getCurrentPlayer();

    Board getBoard();

    GameState getState();
}