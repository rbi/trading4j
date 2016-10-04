package de.voidnode.trading4j.server.oio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import de.voidnode.trading4j.server.protocol.ClientCommunicator;
import de.voidnode.trading4j.server.reporting.AdmininstratorNotifier;

/**
 * A TCP server that accepts clients for trading strategies based on old Java IO
 * mechanisms.
 * 
 * @author Raik Bieniek
 */
public class OioServer {

    private static final int PORT = 6474;

    private final ClientCommunicator clientCommunicator;
    private final AdmininstratorNotifier admin;

    private ServerSocket serverSocket;

    /**
     * Initializes this class with all its dependencies.
     * 
     * @param clientCommunicator
     *            used handle the communication with new clients.
     * @param admin
     *            used to inform the administrator of errors with the
     *            connection.
     */
    public OioServer(final ClientCommunicator clientCommunicator, final AdmininstratorNotifier admin) {
        this.clientCommunicator = clientCommunicator;
        this.admin = admin;
    }

    /**
     * Starts listening for incoming client connections.
     * 
     * <p>
     * This method will block until the current thread is terminated from the
     * outside (e.g. with STRG+C).
     * </p>
     * 
     */
    public void start() {
        serverSocket = createServerSocket();
        if (serverSocket != null) {
            admin.informalEvent("Listening for connections on port " + PORT + ".");
            acceptNewClients();
            closeServerSocket();
        }

    }

    private ServerSocket createServerSocket() {
        try {
            return new ServerSocket(PORT);
        } catch (final IOException e) {
            handleServerError(e);
        }
        return null;
    }

    private void acceptNewClients() {
        while (!serverSocket.isClosed()) {
            try {
                final Socket clientSocket = serverSocket.accept();
                handleNewClient(clientSocket);
            } catch (final IOException e) {
                handleServerError(e);
            }
        }
    }

    private void handleNewClient(final Socket clientSocket) {
        admin.informalEvent(String.format("A client connected from '%s'.", clientSocket.getRemoteSocketAddress()));
        Thread clientThread = null;
        try {
            clientThread = new Thread(
                    clientCommunicator.newClientCommunicationHandler(new OioClientConnection(clientSocket)));
        } catch (final IOException e) {
            handleClientInitialisationError(clientSocket, e);
            return;
        }
        // Give threads that make trading decisions high priority.
        clientThread.setPriority(Thread.MAX_PRIORITY - 1);
        clientThread.setDaemon(true);
        clientThread.start();
    }

    private void closeServerSocket() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            admin.unrecoverableError("Failed to close the server socket.", e);
        }
    }

    private void handleServerError(final IOException e) {
        admin.unrecoverableError("An error occured in the main server socket.", e);
    }

    private void handleClientInitialisationError(final Socket clientSocket, final IOException e) {
        final String addr = clientSocket.getRemoteSocketAddress().toString();
        admin.unexpectedEvent(String.format(
                "Can't initialize connection of client '%s' correctly. Closing the connection to it.", addr), e);
        try {
            clientSocket.close();
        } catch (IOException e1) {
            admin.unexpectedEvent(String.format("Could not close the connection to the client '%s'.", addr), e1);
        }

    }
}
