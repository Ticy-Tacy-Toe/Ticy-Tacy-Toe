package TicyTacyToe.AI;

import TicyTacyToe.Board;
import java.util.Arrays;

public class RandomAI {
    public static void main(String[] args) {

        Board board = new Board();
        
        System.out.println(Arrays.toString(aiChoice(board)));
    }

    public static int[] aiChoice(Board board) {
        int row;
        int column;

        do { 
            row = (int) (Math.random() * 3);
            column = (int) (Math.random() * 3);
        } while (!board.checkMove(row, column));

        return new int[]{row, column};
        
    }

}