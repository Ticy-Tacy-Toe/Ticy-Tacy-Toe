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
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter your move (row, col):");
            String input = scanner.nextLine();
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
            return turn = 2;
        }
        else {
            return turn = 1;
        }
    }

    public static void userInput() {
        System.out.println("which row?");
        Scanner choiceRow = new Scanner(System.in);
        System.out.println("which column?");
        Scanner choiceColumn = new Scanner(System.in);


    }
}
