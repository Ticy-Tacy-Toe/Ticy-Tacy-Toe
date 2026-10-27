
package TicyTacyToe.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class ServerConnection {

    private static final String SERVER_IP = "10.200.64.90";
    private static final int SERVER_PORT = 7789;

    private static final int RESTART_ATTEMPTS = 3;
    private static final long RESTART_DELAY_MILLIS = 500;

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
                    newSocket.getOutputStream(), true
            );

            // Read the server greeting.
            String greeting = newIn.readLine();
            String instructions = newIn.readLine();

            if (greeting == null || instructions == null) {
                newSocket.close();
                manuallyDisconnected = true;
                return false;
            }

            newOut.println("login " + playerName);

            String response = newIn.readLine();

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
                    ),
                    "Server-Message-Listener"
            );

            listener.setDaemon(true);
            listener.start();

            return true;

        } catch (IOException e) {
            manuallyDisconnected = true;

            if (newSocket != null) {
                try {
                    newSocket.close();
                } catch (IOException ignored) {
                    // The socket is already closed.
                }
            }

            System.out.println(
                    "Could not connect to the game server at "
                            + SERVER_IP + ":" + SERVER_PORT
            );
            System.out.println(
                    "Check that the server is running and the IP is correct."
            );

            return false;
        }
    }

    public boolean reconnect(String playerName) {
        System.out.println("Attempting to reconnect...");
        return connectServer(playerName);
    }

    /**
     * Disconnects and logs in again with the same player name.
     */
    public synchronized boolean restart(String playerName) {
        disconnect();

        for (int attempt = 1; attempt <= RESTART_ATTEMPTS; attempt++) {
            try {
                Thread.sleep(RESTART_DELAY_MILLIS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }

            if (connectServer(playerName)) {
                return true;
            }
        }

        return false;
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
                        String challenge = message;

                        Thread challengeThread = new Thread(
                                () -> handler.accept(challenge),
                                "Incoming-Challenge"
                        );

                        challengeThread.setDaemon(true);
                        challengeThread.start();
                    } else {
                        responses.offer(message);
                    }
                } else {
                    responses.offer(message);
                }
            }

        } catch (IOException ignored) {
            // The connection may have closed.
        } finally {
            if (!manuallyDisconnected && socket == connectedSocket) {
                responses.offer(CONNECTION_LOST);
            }
        }
    }

    public void setChallengeHandler(Consumer<String> handler) {
        this.challengeHandler = handler;
    }

    public synchronized void sendCommand(String command) {
        if (out == null || socket == null
                || socket.isClosed() || !socket.isConnected()) {
            responses.offer(CONNECTION_LOST);
            return;
        }

        out.println(command);

        if (out.checkError()) {
            responses.offer(CONNECTION_LOST);
        }
    }

    public String readResponse() throws IOException {
        try {
            return responses.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

            throw new IOException(
                    "Interrupted while waiting for server response.", e
            );
        }
    }

    public String pollResponse(long timeoutMillis) throws IOException {
        try {
            return responses.poll(
                    timeoutMillis,
                    TimeUnit.MILLISECONDS
            );
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

            throw new IOException(
                    "Interrupted while waiting for server response.", e
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
            } catch (IOException ignored) {
                // The socket is already closed.
            }
        }

        socket = null;
        in = null;
        out = null;
    }
}
