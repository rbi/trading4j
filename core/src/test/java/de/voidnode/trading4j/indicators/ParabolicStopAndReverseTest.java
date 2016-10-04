package de.voidnode.trading4j.indicators;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Double.parseDouble;
import static java.util.stream.Collectors.toCollection;

import de.voidnode.trading4j.domain.Ratio;
import de.voidnode.trading4j.domain.TimeFrame.M1;
import de.voidnode.trading4j.domain.marketdata.DatedCandleStick;
import de.voidnode.trading4j.domain.monetary.Price;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Checks if {@link ParabolicStopAndReverse} works as expected.
 * 
 * @author Raik Bieniek
 */
public class ParabolicStopAndReverseTest {

    private static final DateTimeFormatter DAY_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static List<TestData> testData;

    private final ParabolicStopAndReverse<DatedCandleStick<M1>> cut = new ParabolicStopAndReverse<>(new Ratio(0.02),
            new Ratio(0.2));

    /**
     * Sets up the test data.
     * 
     * @throws IOException
     *             When reading the test data failed
     */
    @BeforeClass
    public static void setUpTestData() throws IOException {
        final Path testDataFile = Paths
                .get(ParabolicStopAndReverseTest.class.getResource("/Parabolic-SAR-Testdata.csv").getPath());

        testData = Files.lines(testDataFile)
                // skip the header
                .skip(1)
                // convert all other lines to test data.
                .map(line -> {
                    final String[] parts = line.split(",");
                    // read the day and the time
                    final Instant time = LocalDate.parse(parts[0], DAY_FORMATTER)
                            .atTime(LocalTime.parse(parts[1], TIME_FORMATTER)).atOffset(ZoneOffset.UTC)
                            .toInstant();
                    // read the rest of the test data
                    return new TestData(
                            new DatedCandleStick<>(time, parseDouble(parts[2]), parseDouble(parts[3]),
                                    parseDouble(parts[4]), parseDouble(parts[5])),
                            new Price(parseDouble(parts[6])), new Price(parseDouble(parts[7])));

                }).collect(toCollection(() -> new ArrayList<>()));
    }

    /**
     * As the Parabolic SAR needs some time to "swing in", the first 60 results should be empty.
     * 
     * <p>
     * After that, results should be returned.
     * </p>
     * 
     * @throws IOException
     *             If reading the test data failed.
     */
    @Test
    public void noResultForFirst60DataPoints() throws IOException {
        for (int i = 0; i < 60; i++) {
            assertThat(cut.indicate(testData.get(i).marketData)).isEmpty();
        }

        for (int i = 60; i < 70; i++) {
            assertThat(cut.indicate(testData.get(i).marketData)).isPresent();
        }
    }

    /**
     * The Parapolic SAR with increasment=0.02 and maximalAcceleration=0.2 is calculated correctly on the test data set.
     */
    @Test
    public void parabolicSar00202IsCalculatedCorrectly() {
        // let the cut swing-in
        for (int i = 0; i < 94; i++) {
            cut.indicate(testData.get(i).marketData);
        }
        for (int i = 94; i < testData.size(); i++) {
            final TestData datum = testData.get(i);
            assertThat(cut.indicate(datum.marketData).get()).isEqualTo(datum.psar00202);
        }
    }

    /**
     * The Parapolic SAR with increasment=0.04 and maximalAcceleration=0.4 is calculated correctly on the test data set.
     */
    @Test
    public void parabolicSar00404IsCalculatedCorrectly() {
        final ParabolicStopAndReverse<DatedCandleStick<M1>> cut = new ParabolicStopAndReverse<>(new Ratio(0.04),
                new Ratio(0.4));
        // let the cut swing-in
        for (int i = 0; i < 94; i++) {
            cut.indicate(testData.get(i).marketData);
        }
        for (int i = 94; i < testData.size(); i++) {
            final TestData datum = testData.get(i);
            assertThat(cut.indicate(datum.marketData).get()).isEqualTo(datum.psar00404);
        }
    }

    /**
     * The structure of a single line in the test data file.
     */
    private static final class TestData {
        private final DatedCandleStick<M1> marketData;
        private final Price psar00202;
        private final Price psar00404;

        TestData(final DatedCandleStick<M1> marketData, final Price psar00202, final Price psar00404) {
            this.marketData = marketData;
            this.psar00202 = psar00202;
            this.psar00404 = psar00404;
        }
    }
}
