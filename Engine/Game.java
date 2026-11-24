package TicyTacyToe.Engine;

public interface Game {

    boolean makeMove(Move move);

    boolean isGameOver();

    Player getCurrentPlayer();

    Board getBoard();

    GameState getState();
}