package TicyTacyToe.Games.TicTacToe;

import TicyTacyToe.Engine.Board;
import TicyTacyToe.Engine.Game;
import TicyTacyToe.Engine.GameState;
import TicyTacyToe.Engine.Move;
import TicyTacyToe.Engine.Players.Player;

public class TicTacToeGame implements Game {

    private final Board board;
    private final Player player1;
    private final Player player2;

    private Player currentPlayer;
    private GameState state;

    public TicTacToeGame(Player player1, Player player2) {
        this.board = new TicTacToeBoard();
        this.player1 = player1;
        this.player2 = player2;

        this.currentPlayer = player1;
        this.state = GameState.PLAYING;
    }

    @Override
    public boolean makeMove(Move move) {

        if (state != GameState.PLAYING) {
            return false;
        }

        if (!board.checkMove(move.getRow(), move.getColumn())) {
            return false;
        }

        char symbol;

        if (currentPlayer == player1) {
            symbol = 'X';
        } else {
            symbol = 'O';
        }

        board.doMove(move, symbol);

        if (board.checkWin()) {
            if (currentPlayer == player1) {
                state = GameState.PLAYER1_WON;
            } else {
                state = GameState.PLAYER2_WON;
            }

            currentPlayer.addPoint();
            return true;
        }

        if (board.checkFull()) {
            state = GameState.DRAW;
            return true;
        }

        switchPlayer();

        return true;
    }


    private void switchPlayer() {
        if (currentPlayer == player1) {
            currentPlayer = player2;
        } else {
            currentPlayer = player1;
        }
    }

    @Override
    public boolean isGameOver() {
        return state != GameState.PLAYING;
    }

    @Override
    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    @Override
    public Board getBoard() {
        return board;
    }

    @Override
    public GameState getState() {
        return state;
    }
}