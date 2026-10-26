package TicyTacyToe.CLI;

import java.util.Scanner;

public class CLI {

    public static void intro(Scanner scanner) {
        System.out.println("Gday welcome to Ticy Tacy Toe!");
        System.out.println("Rules: Player 1 and player 2, represented by X and O, take turns\n" +
                "marking the spaces in a 3*3 grid. The player who succeeds in placing\n" +
                "three of their marks in a horizontal, vertical, or diagonal row wins");
        System.out.println("Press enter to continue. :)");

        scanner.nextLine();
    }

    public static int chooseAI(Scanner scanner) {
        while (true) {
            System.out.println("Kies AI: \n1 = random, \n2 = minimax, \n3 = play localy");
            String input = scanner.nextLine();

            if (input.equals("1")) {
                return 1;
            }

            if (input.equals("2")) {
                return 2;
            }
            if (input.equals("3")) {
                return 3;
            }

            System.out.println("Invalid input");
        }
    }

    public static void invalidMove() {
        System.out.println("Invalid move");
    }

    public static void printScores(String humanName, int humanScore,
                                   String aiName, int aiScore) {
        System.out.println(humanName + ": " + humanScore + " points");
        System.out.println(aiName + ": " + aiScore + " points");
    }

    public static void printGameResult(String result) {
        System.out.println(result);
    }

    public static boolean wantsToPlayAgain(Scanner scanner) {
        System.out.println("Do you want to play again? Y/N");

        String input = scanner.nextLine().toUpperCase();

        return !input.isEmpty() && input.charAt(0) == 'Y';
    }


    public static boolean offORon() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Do you want to play online or offline?");
            String answer = scanner.nextLine().trim().toLowerCase();

            if (answer.equals("online")) {
                return true;
            } else if (answer.equals("offline")) {
                return false;
            } else {
                System.out.println("Invalid input. Please enter online or offline.");
            }
        }
    }


}
