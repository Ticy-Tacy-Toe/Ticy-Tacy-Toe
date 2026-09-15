package TicyTacyToe;
import java.util.Scanner;

public class Test {
    public static void main(String[] args) {
        intro();

        String[][] board = createGrid();

        Scanner userStart = new Scanner(System.in);

        userStart.nextLine();

        int player = 2;

        player(player);

        boolean end = false;
        while (!end) {

            for (String[] row : board) {
                for (String cell : row) {
                    System.out.print("[" + cell + "]");
                }
                System.out.println();
            }
            System.out.println("Enter your move:");
            System.out.println("For example: 0, 2:");
            String input = userStart.nextLine();


            int input1 = Character.getNumericValue(input.charAt(0));
            int input2 = Character.getNumericValue(input.charAt(2));

            board[input1][input2] = "X";

        }
    }


    public static void intro() {
        System.out.println("Gday welcome to Ticy Tacy Toe!");
        System.out.println("Rules: Player 1 and player 2, represented by X and O, take turns \n" +
                "          marking the spaces in a 3*3 grid. The player who succeeds in placing \n" +
                "          three of their marks in a horizontal, vertical, or diagonal row wins");

        System.out.println("Press enter to continue. :)");
    }

    public static String[][] createGrid() {
        String[][] board = {
                {" ", " ", " "},
                {" ", " ", " "},
                {" ", " ", " "}
        };
        return board;
    }

    public static int player(int turn) {
        if (turn == 1) {
            return 2;
        }
        else {
            return 1;
        }
    }
}