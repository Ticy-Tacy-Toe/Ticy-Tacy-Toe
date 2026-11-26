package TicyTacyToe.CLI;

import TicyTacyToe.Engine.GameEngine;
import TicyTacyToe.Engine.GameState;
import TicyTacyToe.Engine.Players.HumanPlayer;
import TicyTacyToe.Engine.Players.Player;
import TicyTacyToe.Games.TicTacToe.TicTacToeGame;

public class TicTacToeCLI {

    public void start() {

        Player player1 = new HumanPlayer("Player 1", 1);
        Player player2 = new HumanPlayer("Player 2", 2);

        TicTacToeGame game = new TicTacToeGame(player1, player2);
        GameEngine engine = new GameEngine(game);

        while (!engine.isGameOver()) {

            engine.getBoard().printBoard();

            Player currentPlayer = game.getCurrentPlayer();

            System.out.println();
            System.out.println(currentPlayer.getName() + "'s turn");

            engine.makeMove(currentPlayer.makeMove(engine.getBoard()));
        }

        engine.getBoard().printBoard();

        System.out.println();

        GameState state = engine.getGameState();

        if (state == GameState.PLAYER1_WON) {
            System.out.println("Player 1 wins!");
        } else if (state == GameState.PLAYER2_WON) {
            System.out.println("Player 2 wins!");
        } else if (state == GameState.DRAW) {
            System.out.println("It's a draw!");
        }
    }
}