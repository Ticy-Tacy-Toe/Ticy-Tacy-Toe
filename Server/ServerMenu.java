//minor change
package TicyTacyToe.Server;

import TicyTacyToe.CLI.MainMenuCLI.MainMenuCLI;

import java.util.Scanner;

public class ServerMenu {

    private final MainMenuCLI mainMenuCLI;

    public ServerMenu(
            ServerConnection connection,
            Scanner scanner,
            String playerName,
            int playerMode
    ) {
        this.mainMenuCLI = new MainMenuCLI(
                connection,
                scanner,
                playerName,
                playerMode
        );
    }

    public void start() {
        mainMenuCLI.start();
    }
}
