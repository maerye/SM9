package src.api;

/**
 * Common interface for all pre-processing interfaces.
 *
 * @author Angelo De Caro (jpbclib@gmail.com)
 * @since 1.0.0
 */
public interface PreProcessing {

    /**
     * Converts the object to bytes.
     *
     * @return the bytes written.
     * @since 1.2.0
     */
    byte[] toBytes();

}
