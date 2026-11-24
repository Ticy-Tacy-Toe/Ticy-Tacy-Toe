
package TicyTacyToe.Engine.Players;

import TicyTacyToe.Engine.Board;
import TicyTacyToe.Move.*;
import java.util.Scanner;

public class HumanPlayer extends Player {
    private Scanner scanner;
    
    public HumanPlayer(String name, int number) {
        super(name, number);

    }

    @Override
    public int[] makeMove(Board board) {
        Move move = new Move(scanner);
        return move.getMove();
    }
}
