package de.voidnode.trading4j.server.oio;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import de.voidnode.trading4j.server.protocol.ClientConnection;
import de.voidnode.trading4j.server.protocol.exceptions.AbnormalCloseException;
import de.voidnode.trading4j.server.protocol.exceptions.NormalCloseException;

/**
 * A TCP client that can be used to send and receive data from a connected client.
 * 
 * @author Raik Bieniek
 */
public class OioClientConnection implements ClientConnection {

    /**
     * As long as there is data available for read, data to write is buffered up to the amount of bytes of this limit.
     */
    static final int WRITE_BUFFER_SIZE = 1200;

    private final Socket clientSocket;
    private final DataInputStream clientInput;
    private final DataOutputStream clientOutput;

    private OutputStream bufferedClientOutput;

    /**
     * Initializes the connection.
     * 
     * @param clientSocket
     *            The socket to the client.
     * @throws IOException
     *             When initialization of input and output streams failed.
     */
    public OioClientConnection(final Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        this.clientInput = new DataInputStream(clientSocket.getInputStream());
        this.bufferedClientOutput = new BufferedOutputStream(clientSocket.getOutputStream(), WRITE_BUFFER_SIZE);
        this.clientOutput = new DataOutputStream(bufferedClientOutput);
    }

    @Override
    public byte tryReceiveByte() throws AbnormalCloseException, NormalCloseException {
        try {
            return clientInput.readByte();
        } catch (final IOException e) {
            wrapException(e);
        }
        throw new AbnormalCloseException();
    }

    @Override
    public long tryReceiveLong() throws AbnormalCloseException, NormalCloseException {
        try {
            return clientInput.readLong();
        } catch (IOException e) {
            wrapException(e);
        }
        throw new AbnormalCloseException();
    }

    @Override
    public double tryReceiveDouble() throws AbnormalCloseException, NormalCloseException {
        try {
            return clientInput.readDouble();
        } catch (IOException e) {
            wrapException(e);
        }
        throw new AbnormalCloseException();
    }

    @Override
    public int tryReceiveInteger() throws AbnormalCloseException, NormalCloseException {
        try {
            return clientInput.readInt();
        } catch (IOException e) {
            wrapException(e);
        }
        throw new AbnormalCloseException();
    }

    @Override
    public String tryReceiveString() throws AbnormalCloseException, NormalCloseException {
        try {
            return clientInput.readUTF();
        } catch (IOException e) {
            wrapException(e);
        }
        throw new AbnormalCloseException();
    }

    @Override
    public void trySendByte(final byte data) throws AbnormalCloseException, NormalCloseException {
        trySend(() -> clientOutput.write(data));
    }

    @Override
    public void trySendInteger(final int data) throws AbnormalCloseException, NormalCloseException {
        trySend(() -> clientOutput.writeInt(data));
    }

    @Override
    public void trySendDouble(final double data) throws AbnormalCloseException, NormalCloseException {
        trySend(() -> clientOutput.writeDouble(data));
    }

    @Override
    public void trySendLong(final long data) throws AbnormalCloseException, NormalCloseException {
        trySend(() -> clientOutput.writeLong(data));
    }

    private boolean noMoreBytesReadable() throws IOException {
        return clientInput.available() <= 0;
    }

    private void trySend(final IoOperation ioop) throws AbnormalCloseException, NormalCloseException {
        try {
            ioop.run();
            if (noMoreBytesReadable()) {
                bufferedClientOutput.flush();
            }
        } catch (IOException e) {
            wrapException(e);
        }
    }

    private void wrapException(final IOException e) throws AbnormalCloseException, NormalCloseException {
        if (e instanceof EOFException) {
            throw new NormalCloseException(e);
        }
        throw new AbnormalCloseException(e);
    }

    @Override
    public void close() throws Exception {
        clientSocket.close();
    }

    @Override
    public String toString() {
        return clientSocket.getRemoteSocketAddress().toString();
    }
    
    /**
     * A piece of I/O related code that can throw {@link IOException}s.
     */
    @FunctionalInterface
    private interface IoOperation {

        /**
         * Executes the I/O operation.
         * 
         * @throws IOException
         *             When the successful execution failed.
         */
        void run() throws IOException;
    }
}
