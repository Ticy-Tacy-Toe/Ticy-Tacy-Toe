package TicyTacyToe;

import java.util.Scanner;

public class Player {
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

    public static Player createPlayer(Scanner scanner, int number) {
        System.out.println("What is your name?: ");
        String name = scanner.nextLine();

        char symbol = chooseSymbol(scanner);
        return new Player(name, symbol, number);
    }

    public static char chooseSymbol(Scanner scanner) {
        char symbol = ' ';

        while (symbol != 'X' && symbol != 'O') {
            System.out.println("Choose X or O:");
            String input = scanner.nextLine().toUpperCase();
            symbol = input.charAt(0);
        }

        return symbol;
    }

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