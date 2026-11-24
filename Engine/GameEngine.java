package TicyTacyToe.Engine;

public class GameEngine {

    private Game game;

    public GameEngine(Game game) {
        this.game = game;
    }

    public boolean makeMove(Move move) {
        if (game.isGameOver()) {
            return false;
        }

        return game.makeMove(move);
    }

    public boolean isGameOver() {
        return game.isGameOver();
    }

    public Board getBoard() {
        return game.getBoard();
    }

    public GameState getGameState() {
        return game.getState();
    }
}