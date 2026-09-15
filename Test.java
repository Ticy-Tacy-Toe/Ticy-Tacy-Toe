package TicyTacyToe;

public class Test {
    public static void main(String[] args) {
        Intro();
        String[][] board = createGrid();

        for (String[] row : board) {
            for (String cell : row) {
                System.out.print("[" + cell + "]");
            }
            System.out.println();
        }

        System.out.println(board);
    }


    public static void Intro() {
        System.out.println("Gday welcome to Ticy Tacy Toe!");
        System.out.println("Rules: Player 1 and player 2, represented by X and O, take turns \"\n" +
                "          \"marking the spaces in a 3*3 grid. The player who succeeds in placing \"\n" +
                "          \"three of their marks in a horizontal, vertical, or diagonal row wins");

        System.out.println("Press enter to continue. :)");
    }

    public static String[][] createGrid() {
        String[][] board = {
                {" ", " ", " "},
                {" ", " ", " "},
                {" ", " ", " "}
        };
        System.out.println("here's the playboard");

        return board;
    }


}
