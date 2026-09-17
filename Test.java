package TicyTacyToe;

import java.util.Objects;
import java.util.Scanner;

public class Test {
    public static void main(String[] args) {
        intro();
        // whenever we start the code we call the method intro, to tell the user what it
        // is

        String[][] board = createGrid();

        Scanner userStart = new Scanner(System.in); // create a scanner so we can detect input from the user
        // change userStart to scanner

        userStart.nextLine();
        // wait for the user to press enter to continue

        int player = 2;

        // doesn't do anything rn

        boolean end = false; // a boolean so we can go in a while to run the game until the user wants to
                             // quit
        // and the value gets changed to true
        while (!end) {

            for (String[] row : board) {
                for (String cell : row) {
                    System.out.print("[" + cell + "]");
                }
                System.out.println();
            }
            System.out.println("Enter your move:");
            System.out.println("For example: 1 to 3, \n" +
                    "first is for the row, the second is column \n" +
                    "be sure to put a , in between the numbers"); // can be in 1 print
            String input = userStart.nextLine();

            String[] coordinates = input.split(",");

            int input1 = Integer.parseInt(coordinates[0].trim());
            int input2 = Integer.parseInt(coordinates[1].trim());

            boolean canMove = Input_index(input1, input2, board);

            if (canMove) {
                System.out.println(player);
                player = switch_player(player);
                System.out.println(player + "turn value");
                if (player == 1) {
                    board[input1 - 1][input2 - 1] = "X";

                } else {
                    board[input1 - 1][input2 - 1] = "O";
                }
            } else {
                System.out.println("invalid move, please try again");
            }
            if (checkWin(board)){
                System.out.println("Je hebt gewonnen!");
                end = true;
            }
            

            // check horizontal win
            // check vertical win
            // check diagonal win
            // if checkWin return true:
            // winner receives winning messages + score goes up
            // loser receives a losing messages :(
            // play again or close game

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
                { " ", " ", " " },
                { " ", " ", " " },
                { " ", " ", " " }
        };
        return board;
    }

    // change name of method
    public static int switch_player(int player) {
        if (player == 1) {
            return 2;
        } else {
            return 1;
        }
    }

    // method can be way more compact
    // immediantly go into the while loop
    // use char instead of string
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

    public static boolean Input_index(int input1, int input2, String[][] board) {
        int index = input1 - 1;
        int index2 = input2 - 1;
        System.out.println(index + " " + index2);
        return checkMove(index, index2, board);
    }

    public static boolean checkMove(int row, int column, String[][] board) {
        System.out.println("method called");

        if (row >= 0 && row <= 2 && column >= 0 && column <= 2) {
            return Objects.equals(board[row][column], " ");
        } else {
            return false;
        }
    }

    public static boolean checkRow(String[][] board) {
        for (int i = 0; i < board.length; i++) {
            // Ensure cell is not empty, then compare across all 3 columns
            if (!board[i][0].equals(" ") &&
                    board[i][0].equals(board[i][1]) &&
                    board[i][1].equals(board[i][2])) {
                return true; // Return true as soon as ANY row wins
            }
        }

        return false; // Return false ONLY after all rows have been checked
    }

    public static boolean checkColumn(String[][] board) {
        for (int i = 0; i < board.length; i++) {
            // Ensure cell is not empty, then compare across all 3 columns
            if (!board[0][i].equals(" ") &&
                    board[0][i].equals(board[1][i]) &&
                    board[1][i].equals(board[2][i])) {
                return true; // Return true as soon as ANY row wins
            }
        }

        return false; // Return false ONLY after all rows have been checked
    }

    public static boolean chechDiagonal(String[][] board) {
        for (int i = 0; i < board.length; i++) {
            if (!board[i][i].equals(" ") &&
                    board[0][0].equals(board[1][1]) &&
                    board[1][1].equals(board[2][2])) {
                return true; // Return true as soon as ANY row wins
            }
        }
        if (!board[0][2].equals(" ") &&
                !board[1][1].equals(" ") &&
                !board[2][0].equals(" ") &&
                board[0][2].equals(board[1][1]) &&
                board[1][1].equals(board[2][0])) {
            return true; // Return true as soon as ANY row wins
        } else {
            return false;
        }
    }

    public static boolean checkWin(String[][] board){
        if (checkRow(board) || checkColumn(board) || chechDiagonal(board)) {
            return true;
        }
        else {
            return false;
        }
    }

}