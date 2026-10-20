
package TicyTacyToe.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

public class ServerConnection {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    private final BlockingQueue<String> responses = new LinkedBlockingQueue<>();
    private Consumer<String> challengeHandler;

    public boolean connectServer(String playerName) {
        try {
            socket = new Socket("localhost", 7789);
            in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream())
            );
            out = new PrintWriter(socket.getOutputStream(), true);

            System.out.println("Successfully connected to the game server");
            System.out.println(in.readLine());
            System.out.println(in.readLine());

            out.println("login " + playerName);
            String response = in.readLine();

            System.out.println("Login response: " + response);

            if (response == null || !response.equalsIgnoreCase("OK")) {
                disconnect();
                return false;
            }

            Thread listener = new Thread(() -> {
                try {
                    String message;

                    while ((message = in.readLine()) != null) {
                        if (message.contains("SVR GAME CHALLENGE")) {
                            Consumer<String> handler = challengeHandler;

                            if (handler != null) {
                                handler.accept(message);
                            } else {
                                responses.offer(message);
                            }
                        } else {
                            responses.offer(message);
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Disconnected from the game server.");
                }
            });

            listener.setDaemon(true);
            listener.start();

            return true;

        } catch (IOException e) {
            System.out.println("Couldn't connect to the server.");
            disconnect();
            return false;
        }
    }

    public void setChallengeHandler(Consumer<String> handler) {
        this.challengeHandler = handler;
    }

    public void sendCommand(String command) {
        if (out != null) {
            out.println(command);
        }
    }

    public String readResponse() throws IOException {
        try {
            return responses.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Interrupted while waiting for server response.", e);
        }
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
