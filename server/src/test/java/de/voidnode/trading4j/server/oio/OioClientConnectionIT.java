package de.voidnode.trading4j.server.oio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import de.voidnode.trading4j.server.protocol.exceptions.NormalCloseException;

import org.assertj.core.data.Offset;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;

/**
 * Checks if {@link OioClientConnection} works as expected.
 * 
 * @author Raik Bieniek
 */
public class OioClientConnectionIT {

    private static final String LOCALHOST = "localhost";
    private static final int EXAMPLE_PORT = 18521;
    private static final Offset<Double> TOLERANCE = offset(0.000001);

    private OioClientConnection cut;

    private ServerSocket server;
    private Socket client;

    /**
     * Sets up the class to test and its dependencies.
     * 
     * @throws IOException
     *             when the test cannot be executed because of network errors.
     */
    @Before
    public void setUpClassToTestAndDependencies() throws IOException {
        server = new ServerSocket(EXAMPLE_PORT);
        client = new Socket(LOCALHOST, EXAMPLE_PORT);

        final Socket clientSocket = server.accept();
        cut = new OioClientConnection(clientSocket);
    }

    /**
     * When the client closed the connection, the next read or write should result in a {@link NormalCloseException}.
     * 
     * @throws Exception
     *             not expected to leave the test.
     */
    @Test(expected = NormalCloseException.class)
    public void shouldThrowANormalCloseExceptionWhenTheClientClosedTheConnection() throws Exception {
        client.close();
        cut.tryReceiveByte();
    }

    // /////////////
    // / Reading ///
    // /////////////

    /**
     * The connection should be able to read {@link Byte}s over the network.
     * 
     * @throws Exception
     *             not expected to leave the test.
     */
    @Test
    public void shouldBeAbleToReadBytes() throws Exception {
        client.getOutputStream().write(5);
        assertThat(cut.tryReceiveByte()).isEqualTo((byte) 5);
    }

    /**
     * The connection should be able to read {@link Integer}s over the network.
     * 
     * @throws Exception
     *             not expected to leave the test.
     */
    @Test
    public void shouldBeAbleToReadIntegers() throws Exception {
        client.getOutputStream().write(new byte[] { (byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04 });
        assertThat(cut.tryReceiveInteger()).isEqualTo(0x01020304);
    }

    /**
     * The connection should be able to read {@link Long}s over the network.
     * 
     * @throws Exception
     *             not expected to leave the test.
     */
    @Test
    public void shouldBeAbleToReadLongss() throws Exception {
        client.getOutputStream().write(
                new byte[] { (byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04, (byte) 0x15, (byte) 0x16, (byte) 0x17,
                        (byte) 0x18 });
        assertThat(cut.tryReceiveLong()).isEqualTo(0x0102030415161718L);
    }

    /**
     * The connection should be able to read {@link Double}s over the network.
     * 
     * @throws Exception
     *             not expected to leave the test.
     */
    @Test
    public void shouldBeAbleToReadDoubles() throws Exception {
        // converted with http://www.binaryconvert.com/
        client.getOutputStream().write(
                new byte[] { (byte) 0x40, (byte) 0x39, (byte) 0x25, (byte) 0xF0, (byte) 0x6F, (byte) 0x69, (byte) 0x44,
                        (byte) 0x67 });
        assertThat(cut.tryReceiveDouble()).isCloseTo(25.1482, TOLERANCE);
    }

    /**
     * The connection should be able to length prepended UTF-8 {@link String}s over the network.
     * 
     * @throws Exception
     *             not expected to leave the test.
     */
    @Test
    public void shouldBeAbleToReadUtf8Strings() throws Exception {
        client.getOutputStream().write(new byte[] {
                // 13 bytes
                0x00, 0x0D, //
                'H', 'e', 'l', 'l', 'o', ' ', 'w', //
                // UTF-8 representation of ö
                (byte) 0xC3, (byte) 0xB6, //
                'r', 'l', 'd', '!' });
        assertThat(cut.tryReceiveString()).isEqualTo("Hello wörld!");
    }

    // /////////////
    // / Writing ///
    // /////////////

    /**
     * The connection should be able to write {@link Byte}s to the network.
     * 
     * @throws Exception
     *             not expected to leave the test.
     */
    @Test
    public void shouldBeAbleToWriteBytes() throws Exception {
        cut.trySendByte((byte) 6);
        assertThat(client.getInputStream().read()).isEqualTo(6);
    }

    /**
     * The connection should be able to write {@link Integer}s to the network.
     * 
     * @throws Exception
     *             not expected to leave the test.
     */
    @Test
    public void shouldBeAbleToWriteIntegers() throws Exception {
        cut.trySendInteger(0x20212223);

        assertThat(client.getInputStream().read()).isEqualTo(0x20);
        assertThat(client.getInputStream().read()).isEqualTo(0x21);
        assertThat(client.getInputStream().read()).isEqualTo(0x22);
        assertThat(client.getInputStream().read()).isEqualTo(0x23);
    }

    /**
     * The connection should be able to write {@link Long}s to the network.
     * 
     * @throws Exception
     *             not expected to leave the test.
     */
    @Test
    public void shouldBeAbleToWriteLongs() throws Exception {
        cut.trySendLong(0x3031323334353637L);

        assertThat(client.getInputStream().read()).isEqualTo(0x30);
        assertThat(client.getInputStream().read()).isEqualTo(0x31);
        assertThat(client.getInputStream().read()).isEqualTo(0x32);
        assertThat(client.getInputStream().read()).isEqualTo(0x33);
        assertThat(client.getInputStream().read()).isEqualTo(0x34);
        assertThat(client.getInputStream().read()).isEqualTo(0x35);
        assertThat(client.getInputStream().read()).isEqualTo(0x36);
        assertThat(client.getInputStream().read()).isEqualTo(0x37);
    }

    /**
     * The connection should be able to write {@link Double}s to the network.
     * 
     * @throws Exception
     *             not expected to leave the test.
     */
    @Test
    public void shouldBeAbleToWriteDoubles() throws Exception {
        // converted with http://www.binaryconvert.com/
        cut.trySendDouble(851.254);

        assertThat(client.getInputStream().read()).isEqualTo(0x40);
        assertThat(client.getInputStream().read()).isEqualTo(0x8A);
        assertThat(client.getInputStream().read()).isEqualTo(0x9A);
        assertThat(client.getInputStream().read()).isEqualTo(0x08);
        assertThat(client.getInputStream().read()).isEqualTo(0x31);
        assertThat(client.getInputStream().read()).isEqualTo(0x26);
        assertThat(client.getInputStream().read()).isEqualTo(0xE9);
        assertThat(client.getInputStream().read()).isEqualTo(0x79);
    }

    // ///////////////
    // / Buffering ///
    // ///////////////

    /**
     * As long as there are more bytes available to read, the connection should buffer the data to send.
     * 
     * <p>
     * The buffer whole buffer should be send on write operations when there are no more bytes to read.
     * </p>
     * 
     * @throws Exception
     *             not expected to leave the test.
     */
    @Test
    public void shouldBufferDataToSendAsLongAsDataIsAvailableForReadAndBufferIsNotFull() throws Exception {
        // more data to read
        client.getOutputStream().write(42);

        // send
        cut.trySendByte((byte) 43);

        // data has not reached the client yet
        assertThat(client.getInputStream().available()).isEqualTo(0);

        // drain read buffer
        cut.tryReceiveByte();

        // send more
        cut.trySendByte((byte) 43);

        // The send data should have reached the client.
        assertThat(client.getInputStream().available()).isEqualTo(2);
    }

    /**
     * When the buffer is full it should be send, even if there is more to read.
     * 
     * @throws Exception
     *             not expected to leave the test.
     */
    @Test
    public void shouldSendBufferedDataWhenBufferIsFullEvenIfThereIsMoreToRead() throws Exception {
        // more data to read
        client.getOutputStream().write(42);

        for (int i = 0; i < OioClientConnection.WRITE_BUFFER_SIZE + 1; i++) {
            // let the buffer run full
            cut.trySendByte((byte) 43);
        }

        // A buffer full of data should have reached the client
        assertThat(client.getInputStream().available()).isEqualTo(OioClientConnection.WRITE_BUFFER_SIZE);
    }

    /**
     * Closes the connection after the test.
     * 
     * @throws IOException
     *             when closing the connection failed due to network errors.
     */
    @After
    public void disconnect() throws IOException {
        client.close();
        server.close();
    }
}
