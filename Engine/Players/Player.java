package TicyTacyToe.Engine.Players;

import TicyTacyToe.Engine.Board;

public abstract class Player {
    private String name;
    private int number;
    private int score;

    public Player(String name, int number) {
        this.name = name;
        this.number = number;
        this.score = 0;
    }

    public abstract int[] makeMove(Board board);

    public String getName() {
        return name;
    }

    public int getNumber() {
        return number;
    }

    public int getScore() {
        return score;
    }

    public void addPoint() {
        score++;
    }
}