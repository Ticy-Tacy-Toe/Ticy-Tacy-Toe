package TicyTacyToe.Players;

import TicyTacyToe.Board;
public abstract class Player {
    private String name;
    private char symbol;
    private int number;
    private int score;

    public Player(String name, char symbol, int number) {
        this.name = name;
        this.symbol = symbol;
        this.number = number;
        this.score = 0;
    }

    public abstract int[] makeMove(Board board);

    public String getName() {
        return name;
    }

    public char getSymbol() {
        return symbol;
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