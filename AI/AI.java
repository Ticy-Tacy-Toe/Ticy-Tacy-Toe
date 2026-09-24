package TicyTacyToe.AI;

import java.util.Arrays;

public class AI {
    public static void main() {

        System.out.println(Arrays.toString(aiChoice()));
    }

    public static int[] aiChoice() {
        int random = (int) (Math.random() * 3);
        int random2 = (int) (Math.random() * 3);

        return new int[]{random, random2};
    }

}