package TicyTacyToe;

import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

public class Test {
    public static void main(String[] args) {
        intro();
        // whenever we start the code we call the method intro, to tell the user what it
        // is

        String[][] board = createGrid();

        Scanner Scanner = new Scanner(System.in); // create a scanner so we can detect input from the user
        // change Scanner to scanner

        Scanner.nextLine();
        // wait for the user to press enter to continue

        Player humanPlayer = Player.createPlayer(Scanner, 2);
        char aiSymbol = (humanPlayer.getSymbol() == 'X') ? 'O' : 'X';
        Player aiPlayer = new Player("AI", aiSymbol, 1);

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

            if (player == 1) {

                int[] aiMove = AI.aiChoice();

                int input1 = aiMove[0];
                int input2 = aiMove[1];

                boolean canMoveAI = checkMove(input1, input2, board);

                if (canMoveAI) {
                    board[input1][input2] = String.valueOf(aiPlayer.getSymbol());
                    boolean boolean1 = checkWin(board);
                    if (boolean1 == true) {
                        for (String[] row : board) {
                            for (String cell : row) {
                                System.out.print("[" + cell + "]");
                            }
                            System.out.println();
                        }
                    }
                    player = switch_player(player);

                    if (checkWin(board)) {
                        aiPlayer.addPoint(); 
                        boolean test = endingScreen(player, board, humanPlayer, aiPlayer); 
                        System.out.println(test + "Bool");
                        if (test == true) {
                            System.out.println("end true");
                            end = true;
                        } else {
                            System.out.println("Clearboard");
                            clearBoard(board);
                        }
                    }

                } else {
                    continue;
                }

            }

            else {
                System.out.println("Enter your move:");
                System.out.println("For example: 1 to 3, \n" +
                        "first is for the row, the second is column \n" +
                        "be sure to put a , in between the numbers"); // can be in 1 print
                String input = Scanner.nextLine();

                String[] coordinates = input.split(",");

                int input1 = Integer.parseInt(coordinates[0].trim());
                int input2 = Integer.parseInt(coordinates[1].trim());

                boolean canMove = Input_index(input1, input2, board);

                if (canMove) {
                    // System.out.println(player);
                    player = switch_player(player);
                    // System.out.println(player + "turn value");
                    if (player == 1) {
                        board[input1 - 1][input2 - 1] = String.valueOf(humanPlayer.getSymbol());

                    } else {
                        board[input1 - 1][input2 - 1] = String.valueOf(aiPlayer.getSymbol());
                    }
                } else {
                    System.out.println("invalid move, please try again");
                }
                if (checkWin(board)) {
                    humanPlayer.addPoint();
                    boolean test = endingScreen(player, board, humanPlayer, aiPlayer);
                    System.out.println(test + "Bool");
                    if (test == true) {
                        System.out.println("end true");
                        end = true;
                    } else {
                        System.out.println("Clearboard");
                        clearBoard(board);
                    }
                }
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


    public static boolean Input_index(int input1, int input2, String[][] board) {
        int index = input1 - 1;
        int index2 = input2 - 1;
        System.out.println(index + " " + index2);
        return checkMove(index, index2, board);
    }

    public static boolean checkMove(int row, int column, String[][] board) {

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

    public static boolean checkWin(String[][] board) {
        if (checkRow(board) || checkColumn(board) || chechDiagonal(board)) {
            return true;
        } else {
            return false;
        }
    }

    public static String[][] clearBoard(String[][] board) {
        int n = board.length;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                board[i][j] = " ";
            }
        }
        return board;
    }

    public static boolean aisChoice() {
        System.out.println(Arrays.toString(AI.aiChoice()));
        return false;
    }

    public static boolean endingScreen(int winner, String[][] board, Player humanPlayer, Player aiPlayer) {
        if (winner == 1) {
            System.out.println("You won! :)");
        }

        if (winner == 2) {
            System.out.println("You lost :(");
        }

        System.out.println(humanPlayer.getName() + ": " + humanPlayer.getScore() + " points");
        System.out.println(aiPlayer.getName() + ": " + aiPlayer.getScore() + " points");

        System.out.println("Do you want to play again?: Y/N");

        Scanner scanner = new Scanner(System.in);
        String doorspelen = scanner.nextLine().toUpperCase();

        char verder = doorspelen.charAt(0);

        if (verder == 'Y') {
            System.out.println("user pressed Yes");
            return false;
        } else {
            System.out.println("ending");
            return true;
        }
    }

}