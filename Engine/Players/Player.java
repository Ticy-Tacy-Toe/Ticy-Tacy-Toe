package TicyTacyToe.Engine.Players;

import TicyTacyToe.Engine.Board;
import TicyTacyToe.Engine.Move;

public abstract class Player {
    private final String name;
    private final int number;
    private int score;

    public Player(String name, int number) {
        this.name = name;
        this.number = number;
        this.score = 0;
    }

    public abstract Move makeMove(Board board);

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