package de.voidnode.trading4j.server.protocol.messages;

import java.util.Optional;

import static de.voidnode.trading4j.server.protocol.messages.MessageType.NEW_MARKET_DATA_SIMPLE;
import static de.voidnode.trading4j.server.protocol.messages.MessageType.PENDING_ORDER_CONDITIONALY_CLOSED;
import static de.voidnode.trading4j.server.protocol.messages.MessageType.REQUEST_TRADING_ALGORITHM;
import static de.voidnode.trading4j.server.protocol.messages.MessageType.RESPONSE_PLACE_PENDING_ORDER;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests if {@link MessageType} works as expected.
 * 
 * @author Raik Bieniek
 */
public class MessageTypeTest {

    /**
     * When a {@link Class} implementing {@link Message} is passed to {@link MessageType}, the correct {@link Enum}
     * constant should be returned.
     */
    @Test
    public void shouldReturnCorrectEnumConstantForMessagClass() {
        assertThat(MessageType.forMessageClass(NewMarketDataSimpleMessage.class)).isEqualTo(NEW_MARKET_DATA_SIMPLE);
        assertThat(MessageType.forMessageClass(RequestTradingAlgorithmMessage.class)).isEqualTo(
                REQUEST_TRADING_ALGORITHM);
    }

    /**
     * When the {@link Class} passed to {@link MessageType} does implement {@link Message} but is unknown to
     * {@link MessageType}, an exception should be thrown.
     */
    @Test(expected = IllegalArgumentException.class)
    public void shouldFailForUnknownMessageClasses() {
        MessageType.forMessageClass(UnknownMessageClass.class);
    }

    /**
     * When the {@link MessageType} is queried for the {@link MessageType} with a given number, the correct
     * {@link MessageType} should be returned.
     */
    @Test
    public void shouldReturnCorrectEnumConstantForMessageNumber() {
        assertThat(MessageType.forMessageNumber((byte) 4)).isEqualTo(Optional.of(RESPONSE_PLACE_PENDING_ORDER));
        assertThat(MessageType.forMessageNumber((byte) 6)).isEqualTo(Optional.of(PENDING_ORDER_CONDITIONALY_CLOSED));
    }

    /**
     * When the {@link MessageType} is queried for the {@link MessageType} with a number that is not assigned to any
     * {@link MessageType} an empty {@link Optional} should be retuned.
     */
    @Test
    public void shouldReturnAnEmptyOptionalWhenThePassedNumberIsNotAssignedToAnyMessag() {
        assertThat(MessageType.forMessageNumber((byte) 124)).isEqualTo(Optional.empty());
    }

    /**
     * An instance of {@link Message} that is unknown to {@link MessageType}s.
     */
    private static class UnknownMessageClass implements Message {

    }
}
