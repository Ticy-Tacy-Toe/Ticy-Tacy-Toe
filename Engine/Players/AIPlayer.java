package TicyTacyToe.Engine.Players;

import TicyTacyToe.AI.MinmaxAI;
import TicyTacyToe.Engine.Board;

public class AIPlayer extends Player {

    private Player humanPlayer;

    public AIPlayer(char symbol, int number, Player humanPlayer) {
        super("AI", symbol, number);
        this.humanPlayer = humanPlayer;
    }

    @Override
    public int[] makeMove(Board board) {
        return MinmaxAI.bestMove(board, getSymbol(), humanPlayer);
    } 
}