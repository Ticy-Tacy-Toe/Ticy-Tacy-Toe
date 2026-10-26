package TicyTacyToe.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

public class ServerConnection {

    public static final int DEFAULT_PORT = 7789;

    private static final int CONNECT_TIMEOUT_MS = 5000;
    private static final String CHALLENGE_MARKER = "SVR GAME CHALLENGE";

    /** Pushed onto the queue when the connection dies so readResponse() never hangs. */
    private static final String CLOSED_SENTINEL = "\u0000CONNECTION_CLOSED\u0000";

    private final String host;
    private final int port;

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    private final BlockingQueue<String> responses = new LinkedBlockingQueue<>();

    private volatile Consumer<String> challengeHandler;
    private volatile boolean connected;

    /**
     * @param address "192.168.1.42", "myhost", or "192.168.1.42:7789".
     *                Blank/null falls back to localhost.
     */
    public ServerConnection(String address) {
        String target = (address == null || address.isBlank()) ? "localhost" : address.trim();
        String parsedHost = target;
        int parsedPort = DEFAULT_PORT;

        int colon = target.lastIndexOf(':');
        // Only treat it as host:port if there's exactly one colon (skips IPv6 literals).
        if (colon > 0 && target.indexOf(':') == colon) {
            parsedHost = target.substring(0, colon);
            try {
                parsedPort = Integer.parseInt(target.substring(colon + 1));
            } catch (NumberFormatException e) {
                parsedHost = target;
            }
        }

        this.host = parsedHost;
        this.port = parsedPort;
    }

    public ServerConnection(String host, int port) {
        this.host = (host == null || host.isBlank()) ? "localhost" : host.trim();
        this.port = port;
    }

    /** Convenience for main(): first arg if present, otherwise localhost. */
    public static ServerConnection fromArgs(String[] args) {
        return new ServerConnection(args.length > 0 ? args[0] : "localhost");
    }

    public boolean connectServer(String playerName) {
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(host, port), CONNECT_TIMEOUT_MS);

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            System.out.println("Connected to the game server at " + host + ":" + port);

            // Server greeting (two lines)
            System.out.println(in.readLine());
            System.out.println(in.readLine());

            out.println("login " + playerName);

            String response = in.readLine();
            System.out.println("Login response: " + response);

            if (response == null || !response.equalsIgnoreCase("OK")) {
                disconnect();
                return false;
            }

            connected = true;

            Thread listener = new Thread(this::listenForMessages, "server-listener");
            listener.setDaemon(true);
            listener.start();

            return true;

        } catch (IOException e) {
            System.out.println("Could not connect to the game server at " + host + ":" + port);
            System.out.println("Check that the server is running and the IP is correct.");
            disconnect();
            return false;
        }
    }

    private void listenForMessages() {
        try {
            String message;
            while ((message = in.readLine()) != null) {
                dispatch(message);
            }
        } catch (IOException e) {
            if (connected) {
                System.out.println("Lost connection to the server.");
            }
        } finally {
            connected = false;
            responses.offer(CLOSED_SENTINEL);
        }
    }

    private void dispatch(String message) {
        if (message.contains(CHALLENGE_MARKER)) {
            Consumer<String> handler = challengeHandler;
            if (handler != null) {
                handler.accept(message);
                return;
            }
        }
        responses.offer(message);
    }

    public void setChallengeHandler(Consumer<String> handler) {
        this.challengeHandler = handler;
    }

    public synchronized void sendCommand(String command) {
        if (out != null) {
            out.println(command);
        }
    }

    /** Blocks until the next non-challenge server message arrives. */
    public String readResponse() throws IOException {
        try {
            String message = responses.take();
            if (CLOSED_SENTINEL.equals(message)) {
                responses.offer(CLOSED_SENTINEL); // keep it for any other waiting callers
                throw new IOException("Connection to the server was closed.");
            }
            return message;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Interrupted while waiting for server response.", e);
        }
    }

    public boolean isConnected() {
        return connected;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public void disconnect() {
        connected = false;
        closeQuietly(in);
        closeQuietly(out);
        closeQuietly(socket);
    }

    private static void closeQuietly(AutoCloseable resource) {
        if (resource == null) {
            return;
        }
        try {
            resource.close();
        } catch (Exception ignored) {
            // nothing useful to do on close failure
        }
    }
}