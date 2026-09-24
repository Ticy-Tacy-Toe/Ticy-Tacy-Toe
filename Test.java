package TicyTacyToe;

import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

public class Test {
    public static void main(String[] args) {
        intro();
        // whenever we start the code we call the method intro, to tell the user what it
        // is

        Board board = new Board();

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
        board.printboard();
        while (!end) {

            if (player == 1) {

                int[] aiMove = AI.aiChoice();

                int input1 = aiMove[0];
                int input2 = aiMove[1];

                boolean canMoveAI = checkMove(input1, input2, board);

                if (canMoveAI) {
                    String[][] grid = board.getBoard();
                    grid[input1][input2] = String.valueOf(aiPlayer.getSymbol());
                    boolean win = checkWin(board);
                    if (win == true) {
                        board.printboard();

                    }
                    System.out.println();
                    player = switch_player(player);
                    board.printboard();

                    if (checkWin(board) || board.checkFull()) {

                        boolean test = endingScreen(player, board, humanPlayer, aiPlayer, board.checkFull());

                        if (test == true) {
                            System.out.println("end true");
                            end = true;
                        } else {

                            board.clearBoard();
                        }
                    }

                } else {
                    continue;
                }
            }

            else {
                System.out.println("Enter your move:");
                System.out.println("For example: 1 , 3 \n" +
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
                        String[][] grid = board.getBoard();
                        grid[input1 - 1][input2 - 1] = String.valueOf(humanPlayer.getSymbol());
                        for (String[] row : grid) {
                            for (String cell : row) {
                                System.out.print("[" + cell + "]");
                            }
                            System.out.println();
                        }

                    } else {
                        String[][] grid = board.getBoard();
                        grid[input1 - 1][input2 - 1] = String.valueOf(aiPlayer.getSymbol());
                        for (String[] row : grid) {
                            for (String cell : row) {
                                System.out.print("[" + cell + "]");
                            }
                            System.out.println();
                        }
                    }
                } else {
                    System.out.println("invalid move, please try again");
                }
                if (checkWin(board) || board.checkFull()) {

                    boolean test = endingScreen(player, board, humanPlayer, aiPlayer, board.checkFull());
                    if (test == true) {
                        System.out.println("end true");
                        end = true;
                    } else {
                        board.clearBoard();
                    }
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

    public static void intro() {
        System.out.println("Gday welcome to Ticy Tacy Toe!");
        System.out.println("Rules: Player 1 and player 2, represented by X and O, take turns \n" +
                "          marking the spaces in a 3*3 grid. The player who succeeds in placing \n" +
                "          three of their marks in a horizontal, vertical, or diagonal row wins");

        System.out.println("Press enter to continue. :)");
    }

    // change name of method
    public static int switch_player(int player) {
        if (player == 1) {
            return 2;
        } else {
            return 1;
        }
    }

    public static boolean Input_index(int input1, int input2, Board board) {
        int index = input1 - 1;
        int index2 = input2 - 1;
        System.out.println(index + " " + index2);
        return checkMove(index, index2, board);
    }

    public static boolean checkMove(int row, int column, Board board) {
        String[][] grid = board.getBoard();
        if (row >= 0 && row <= 2 && column >= 0 && column <= 2) {
            return Objects.equals(grid[row][column], " ");
        } else {
            return false;
        }
    }

    public static boolean checkWin(Board board) {
        if (board.checkRow() || board.checkColumn() || board.checkDiagonal()) {
            return true;
        } else {
            return false;
        }
    }

    

    public static boolean endingScreen(int winner, Board board, Player humanPlayer, Player aiPlayer,
            boolean checkFull) {
        if (checkFull && !checkWin(board)) {
            System.out.println("Board is vol");
        } else {
            if (winner == 1) {
                humanPlayer.addPoint();
                System.out.println("Je hebt gewonnen!");
            }

            if (winner == 2) {
                aiPlayer.addPoint();
                System.out.println("Je hebt verloren :(");
            }
            System.out.println(humanPlayer.getName() + ": " + humanPlayer.getScore() + " points");
            System.out.println(aiPlayer.getName() + ": " + aiPlayer.getScore() + " points");
        }

        System.out.println("Wil je nog een keer spelen Y/N");

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