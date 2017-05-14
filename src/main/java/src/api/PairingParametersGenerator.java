package src.api;

/**
 * This interface lets the user to generate all the necessary parameters
 * to initialize a pairing.
 *
 * @author Angelo De Caro (jpbclib@gmail.com)
 * @since 2.0.0
 */
public interface PairingParametersGenerator<P extends PairingParameters> {

    /**
     * Generates the parameters.
     *
     * @return a map with all the necessary parameters.
     * @since 2.0.0
     */
    P generate();

}
