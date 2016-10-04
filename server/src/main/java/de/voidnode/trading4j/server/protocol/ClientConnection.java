package de.voidnode.trading4j.server.protocol;

/**
 * A connection to a trading client.
 * 
 * @author Raik Bieniek
 */
public interface ClientConnection {

    /**
     * Trys to receive a single {@link Byte} from the client.
     * 
     * @return the received byte
     * @throws NormalCloseException
     *             When receiving was not possible because the connection the the client was closed in a normal way.
     * @throws AbnormalCloseException
     *             When receiving was not possible because the connection the the client was closed in an abnormal way.
     */
    byte tryReceiveByte() throws AbnormalCloseException, NormalCloseException;

    /**
     * Trys to receive a single {@link Double} value from the client.
     * 
     * @return the received double
     * @throws NormalCloseException
     *             When receiving was not possible because the connection the the client was closed in a normal way.
     * @throws AbnormalCloseException
     *             When receiving was not possible because the connection the the client was closed in an abnormal way.
     */
    double tryReceiveDouble() throws AbnormalCloseException, NormalCloseException;

    /**
     * Trys to receive a single {@link Integer} value from the client.
     * 
     * @return the received int
     * @throws NormalCloseException
     *             When receiving was not possible because the connection the the client was closed in a normal way.
     * @throws AbnormalCloseException
     *             When receiving was not possible because the connection the the client was closed in an abnormal way.
     */
    int tryReceiveInteger() throws AbnormalCloseException, NormalCloseException;

    /**
     * Trys to receive a single {@link Long} value from the client.
     * 
     * @return the received long
     * @throws NormalCloseException
     *             When receiving was not possible because the connection the the client was closed in a normal way.
     * @throws AbnormalCloseException
     *             When receiving was not possible because the connection the the client was closed in an abnormal way.
     */
    long tryReceiveLong() throws AbnormalCloseException, NormalCloseException;

    /**
     * Tries to receive a single {@link String} from the client.
     *
     * @return The received {@link String}. 
     * @throws NormalCloseException
     *             When receiving was not possible because the connection the the client was closed in a normal way.
     * @throws AbnormalCloseException
     *             When receiving was not possible because the connection the the client was closed in an abnormal way.
     */
    String tryReceiveString() throws AbnormalCloseException, NormalCloseException;

    /**
     * Trys to send a {@link Byte} of data to the client.
     * 
     * @param data
     *            The byte to send
     * @throws NormalCloseException
     *             When receiving was not possible because the connection the the client was closed in a normal way.
     * @throws AbnormalCloseException
     *             When receiving was not possible because the connection the the client was closed in an abnormal way.
     */
    void trySendByte(byte data) throws AbnormalCloseException, NormalCloseException;

    /**
     * Trys to send an {@link Integer} to the client.
     * 
     * @param data
     *            The {@link Integer} to send
     * @throws NormalCloseException
     *             When receiving was not possible because the connection the the client was closed in a normal way.
     * @throws AbnormalCloseException
     *             When receiving was not possible because the connection the the client was closed in an abnormal way.
     */
    void trySendInteger(int data) throws AbnormalCloseException, NormalCloseException;

    /**
     * Trys to send a {@link Double} to the client.
     * 
     * @param data
     *            The {@link Double} to send
     * @throws NormalCloseException
     *             When receiving was not possible because the connection the the client was closed in a normal way.
     * @throws AbnormalCloseException
     *             When receiving was not possible because the connection the the client was closed in an abnormal way.
     */
    void trySendDouble(double data) throws AbnormalCloseException, NormalCloseException;

    /**
     * Trys to send a {@link Long} to the client.
     * 
     * @param data
     *            The {@link Long} to send
     * @throws NormalCloseException
     *             When receiving was not possible because the connection the the client was closed in a normal way.
     * @throws AbnormalCloseException
     *             When receiving was not possible because the connection the the client was closed in an abnormal way.
     */
    void trySendLong(long data) throws AbnormalCloseException, NormalCloseException;

    /**
     * Close the connection to the client.
     * 
     * @throws Exception
     *             When closing the connection failed.
     */
    void close() throws Exception;

    /**
     * A human readable identifier for this specific client connection.
     * 
     * <p>
     * E.g. this could be a combination of the source IP address and port of the client.
     * </p>
     * 
     * @return The identifier
     */
    @Override
    String toString();
}
