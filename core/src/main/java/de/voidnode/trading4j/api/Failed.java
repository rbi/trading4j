package de.voidnode.trading4j.api;

/**
 * Indicates that some operation has failed.
 * 
 * @author Raik Bieniek
 */
public class Failed {

    private final String reason;

    /**
     * Initializes an instance with all its dependencies.
     * 
     * @param reason
     *            see #getReas
     */
    public Failed(final String reason) {
        this.reason = reason;
    }

    /**
     * A human readable {@link String} describing the failure that occurred.
     * 
     * @return The reason.
     */
    public String getReason() {
        return reason;
    }

    /**
     * A human readable {@link String} describing the failure that occurred.
     * 
     * @return The failure
     */
    @Override
    public String toString() {
        return reason;
    }

    /**
     * A {@link Failed} instance is only equal to other {@link Failed} instances with exactly the same
     * {@link #getReason()}.
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Failed other = (Failed) obj;
        if (reason == null) {
            if (other.reason != null) {
                return false;
            }
        } else if (!reason.equals(other.reason)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((reason == null) ? 0 : reason.hashCode());
        return result;
    }
}