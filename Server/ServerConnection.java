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

    private static final String SERVER_IP = "10.243.200.90";
    private static final int SERVER_PORT = 7789;

    public static final String CONNECTION_LOST = "CONNECTION_LOST";

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    private volatile boolean manuallyDisconnected;

    private final BlockingQueue<String> responses =
            new LinkedBlockingQueue<>();

    private volatile Consumer<String> challengeHandler;

    public synchronized boolean connectServer(String playerName) {
        closeSocket();

        responses.clear();
        manuallyDisconnected = false;

        Socket newSocket = null;

        try {
            newSocket = new Socket(SERVER_IP, SERVER_PORT);

            BufferedReader newIn = new BufferedReader(
                    new InputStreamReader(newSocket.getInputStream())
            );

            PrintWriter newOut = new PrintWriter(
                    newSocket.getOutputStream(),
                    true
            );

            System.out.println("Connected to the game server.");

            System.out.println(newIn.readLine());
            System.out.println(newIn.readLine());

            newOut.println("login " + playerName);

            String response = newIn.readLine();

            System.out.println("Login response: " + response);

            if (response == null || !response.equalsIgnoreCase("OK")) {
                newSocket.close();
                manuallyDisconnected = true;
                return false;
            }

            socket = newSocket;
            in = newIn;
            out = newOut;

            Socket connectedSocket = newSocket;
            BufferedReader connectedReader = newIn;

            Thread listener = new Thread(
                    () -> listenForMessages(
                            connectedReader,
                            connectedSocket
                    )
            );

            listener.setDaemon(true);
            listener.start();

            return true;

        } catch (IOException e) {
            System.out.println(
                    "Could not connect to the game server at "
                            + SERVER_IP + ":" + SERVER_PORT
            );

            System.out.println(
                    "Check that the server is running and the IP is correct."
            );

            manuallyDisconnected = true;

            if (newSocket != null) {
                try {
                    newSocket.close();
                } catch (IOException ignored) {
                    // The socket may already be closed.
                }
            }

            return false;
        }
    }

    public boolean reconnect(String playerName) {
        System.out.println("Attempting to reconnect...");
        return connectServer(playerName);
    }

    private void listenForMessages(
            BufferedReader reader,
            Socket connectedSocket
    ) {
        try {
            String message;

            while ((message = reader.readLine()) != null) {
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
            // The connection may have been closed intentionally.
        } finally {
            if (!manuallyDisconnected && socket == connectedSocket) {
                responses.offer(CONNECTION_LOST);
            }
        }
    }

    public void setChallengeHandler(Consumer<String> handler) {
        this.challengeHandler = handler;
    }

    public void sendCommand(String command) {
        if (out != null && socket != null && socket.isConnected()
                && !socket.isClosed()) {
            out.println(command);

            if (out.checkError()) {
                responses.offer(CONNECTION_LOST);
            }
        } else {
            responses.offer(CONNECTION_LOST);
        }
    }

    public String readResponse() throws IOException {
        try {
            return responses.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

            throw new IOException(
                    "Interrupted while waiting for server response.",
                    e
            );
        }
    }

    public synchronized void disconnect() {
        manuallyDisconnected = true;
        closeSocket();
    }

    private void closeSocket() {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                System.out.println("Error closing connection.");
            }
        }

        socket = null;
        in = null;
        out = null;
    }
}