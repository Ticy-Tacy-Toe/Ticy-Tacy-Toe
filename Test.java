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

            // input int - 1 so the user can select field 1 to 3
            // check if input is valid so between 1 and 3
            // check if the field is empty or not so is it a space or not
            // if not we do the move, board[input1][input2]
            // check horizontal win
            // check vertical win
            // check diagonal win
            // if checkWin return true:
            //       winner receives winning messages + score goes up
            //       loser receives a losing messages :(
            //play again or close game

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

    public static char chooseSymbol(Scanner scanner) {
        System.out.println("Choose X or O:");

        String input = scanner.nextLine();
        input = input.toUpperCase();

        char symbol = input.charAt(0);

        while (symbol != 'X' && symbol != 'O') {
            System.out.println("Invalid choice. Choose X or O:");

            input = scanner.nextLine();
            input = input.toUpperCase();

            symbol = input.charAt(0);

            System.out.println(symbol);
        }

        return symbol;
    }
}