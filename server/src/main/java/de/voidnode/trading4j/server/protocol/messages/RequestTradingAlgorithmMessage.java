package de.voidnode.trading4j.server.protocol.messages;

import java.util.Optional;

import de.voidnode.trading4j.api.ExpertAdvisor;
import de.voidnode.trading4j.api.Indicator;

/**
 * A message requesting the trading algorithm that should be used for the rest of the connection.
 * 
 * <p>
 * This is usually the first message of a connection. Trading algorithm can be {@link ExpertAdvisor}s or
 * {@link Indicator}s.
 * </p>
 * 
 * @author Raik Bieniek
 */
public class RequestTradingAlgorithmMessage implements Message {

    private final int algorithmNumber;
    private final AlgorithmType algorithmType;

    /**
     * Initializes the message.
     * 
     * @param algorithmType
     *            The type of the algorithm that is requested.
     * @param algorithmNumber
     *            The number of the requested algorithm.
     */
    public RequestTradingAlgorithmMessage(final AlgorithmType algorithmType, final int algorithmNumber) {
        this.algorithmType = algorithmType;
        this.algorithmNumber = algorithmNumber;
    }

    /**
     * The type of the algorithm that is requested.
     * 
     * @return The type of the algorithm
     */
    public AlgorithmType getAlgorithmType() {
        return algorithmType;
    }

    /**
     * The number of the algorithm that is requested.
     * 
     * @return The number
     */
    public int getAlgorithmNumber() {
        return algorithmNumber;
    }

    /**
     * A well-known kind of trading related algorithms.
     */
    public enum AlgorithmType {

        /**
         * Algorithms that do automatic trading.
         */
        EXPERT_ADVISOR(0),

        /**
         * Algorithms that indicate the current trend.
         */
        TREND_INDICATOR(1);

        private final int algorithmTypeNumber;

        /**
         * Initializes the enum constant with all its data.
         * 
         * @param algorithmTypeNumber
         *            The number of the type.
         */
        AlgorithmType(final int algorithmTypeNumber) {
            this.algorithmTypeNumber = algorithmTypeNumber;

        }

        /**
         * The number that represents this type when transfered over the network.
         * 
         * @return The number
         */
        public int getAlgorithmTypeNumber() {
            return algorithmTypeNumber;
        }

        /**
         * Looks up the {@link AlgorithmType} associated with a given number.
         * 
         * @param number
         *            The number of the {@link AlgorithmType} that is looked for.
         * @return The {@link AlgorithmType} with the given number of an empty {@link Optional} if there is no type with
         *         the given number.
         */
        public static Optional<AlgorithmType> getAlgorithmTypeByNumber(final int number) {
            if (number < 0 || number >= AlgorithmType.values().length) {
                Optional.empty();
            }
            return Optional.of(AlgorithmType.values()[number]);
        }
    }
}
