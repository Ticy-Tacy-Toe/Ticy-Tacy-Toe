package TicyTacyToe;

public class Board {

    private String[][] board;

    public Board() {
        board = new String[][] {
                { " ", " ", " " },
                { " ", " ", " " },
                { " ", " ", " " }
    };
}

    public  void printboard() {
        for (String[] row : board) {
            for (String cell : row) {
                System.out.print("[" + cell + "]");
            }
            System.out.println();
        }
    }

    public  String[][] clearBoard() {
        int n = board.length;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                board[i][j] = " ";
            }
        }
        return board;
    }

        public String[][] getBoard() {
            return board;
        }

}
