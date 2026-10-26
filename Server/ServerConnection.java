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

    private static final String SERVER_IP = "10.243.200.90";
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
                    ),
                    "Server-Message-Listener"
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
                    // Socket already closed.
                }
            }

            return false;
        }
    }

    public boolean reconnect(String playerName) {
        System.out.println("Attempting to reconnect...");
        return connectServer(playerName);
    }

    /**
     * Drops the current connection on purpose and logs in again with the
     * same name.
     *
     * The protocol has no "unsubscribe" or "cancel challenge" command.
     * Disconnecting is the only way to withdraw a subscription or an
     * outgoing challenge. Because disconnect() marks the drop as manual,
     * the old listener thread does not report CONNECTION_LOST.
     *
     * The server may need a moment to notice the closed socket, so the
     * login is retried a few times (a name must be unique on the server).
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
                System.out.println("[SERVER] " + message);

                if (message.contains("SVR GAME CHALLENGE")) {
                    Consumer<String> handler = challengeHandler;

                    if (handler != null) {
                        String challenge = message;

                        // Never block the socket listener with user input.
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

        } catch (IOException e) {
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
            System.out.println(
                    "Cannot send command: not connected to the server."
            );
            responses.offer(CONNECTION_LOST);
            return;
        }

        System.out.println("[CLIENT] " + command);
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

    /**
     * Like readResponse(), but gives up after the timeout.
     *
     * @return the next message, or null if none arrived in time
     */
    public String pollResponse(long timeoutMillis) throws IOException {
        try {
            return responses.poll(timeoutMillis, TimeUnit.MILLISECONDS);
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
            } catch (IOException e) {
                System.out.println("Error closing connection.");
            }
        }

        socket = null;
        in = null;
        out = null;
    }
}