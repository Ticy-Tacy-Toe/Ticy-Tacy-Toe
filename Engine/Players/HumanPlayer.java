
package TicyTacyToe.Engine.Players;

import TicyTacyToe.Board;
import TicyTacyToe.Move.*;

public class HumanPlayer extends Player {

    public HumanPlayer(String name, int number) {
        super(name, number);
    }

    @Override
    public int[] makeMove(Board board) {
        Move move = new Move(scanner);
        return move.getMove();
    }
}
