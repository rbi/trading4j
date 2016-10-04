package de.voidnode.trading4j.domain.environment;

import de.voidnode.trading4j.domain.Volume;

/**
 * Describes constraints for {@link Volume} sizes.
 * 
 * @author Raik Bieniek
 */
public class VolumeConstraints {

    private final Volume minimalVolume;
    private final Volume allowedStepSize;
    private final Volume maximalVolume;

    /**
     * Initializes an instance with all it's data.
     * 
     * @param minimalVolume
     *            see {@link #getMinimalVolume()}
     * @param maximalVolume
     *            see {@link #getMaximalVolume()}
     * @param allowedStepSize
     *            see {@link #getAllowedStepSize()}
     */
    public VolumeConstraints(final Volume minimalVolume, final Volume maximalVolume, final Volume allowedStepSize) {
        this.minimalVolume = minimalVolume;
        this.maximalVolume = maximalVolume;
        this.allowedStepSize = allowedStepSize;
    }

    /**
     * The minimal allowed volume for a trade.
     * 
     * @return The minimal allowed volume.
     */
    public Volume getMinimalVolume() {
        return minimalVolume;
    }

    /**
     * The maximal allowed volume for a trade.
     * 
     * @return The maximal allowed volume.
     */
    public Volume getMaximalVolume() {
        return maximalVolume;
    }

    /**
     * The difference between two allowed consecutive {@link Volume} values.
     * 
     * <p>
     * Only multiples of this size can be valid {@link Volume} values.
     * </p>
     * 
     * @return The minimal allowed step size.
     */
    public Volume getAllowedStepSize() {
        return allowedStepSize;
    }

    @Override
    public String toString() {
        return "VolumeConstraints [minimalVolume=" + minimalVolume + ", allowedStepSize=" + allowedStepSize
                + ", maximalVolume=" + maximalVolume + "]";
    }
}
