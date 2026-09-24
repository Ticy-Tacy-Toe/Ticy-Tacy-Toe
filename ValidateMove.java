package TicyTacyToe;

import java.util.Scanner;

public class Move {

    public static void getMove() {
        Scanner scanner = new Scanner(System.in);

        String input = scanner.nextLine();

        if (!validateInput(input)) {
            System.out.println("Invalid input. Please enter two numbers separated by a comma.");
            return;
        } 
        String[] coordinates = input.split(",");

        int input1 = Integer.parseInt(coordinates[0].trim());
        int input2 = Integer.parseInt(coordinates[1].trim());

        System.out.println(input1 + ", " + input2);
    }

    public static boolean validateInput(String input) {
      if (input.isEmpty()) {
          return false;
      } else {
            String newInput = input.replace(" ", "");
            String[] coordinates = newInput.split(",");

            if (coordinates.length != 2) {
                return false;
            }

            try {
                Integer.parseInt(coordinates[0].trim());
                Integer.parseInt(coordinates[1].trim());

            } catch (NumberFormatException e) {
                return false;
            }

            return true;
        }
    }
}

