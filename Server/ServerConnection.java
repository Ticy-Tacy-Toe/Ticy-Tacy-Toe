
package TicyTacyToe.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerConnection {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public boolean connectServer(String playerName) {
        try {
            socket = new Socket("localhost", 7789);

            in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));

            out = new PrintWriter(socket.getOutputStream(), true);

            System.out.println("Successfully connected to the game server");
            String greeting = in.readLine();
            System.out.println(greeting);

            String copyright = in.readLine();
            System.out.println(copyright);

            out.println("login " + playerName);

            String response = in.readLine();
            System.out.println("Login response: " + response);

            if (response == null || !response.equalsIgnoreCase("OK")) {
                disconnect();
                return false;
            }

            return true;

        } catch (IOException e) {
            System.out.println("Couldn't connect to the server.");
            e.printStackTrace();
            disconnect();
            return false;
        }
    }

    public void sendCommand(String command) {
        if (out != null) {
            out.println(command);
        }
    }

    public String readResponse() throws IOException {
        return in.readLine();
    }

    public void disconnect() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            System.out.println("Error closing connection.");
        }
    }
}
