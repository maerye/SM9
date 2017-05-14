package src.api;

import java.math.BigInteger;

/**
 * Common interface for the exponentiation.
 *
 * @author Angelo De Caro (jpbclib@gmail.com)
 * @since 1.2.0
 */
public interface ElementPow {

    /**
     * Compute the power to n.
     *
     * @param n the exponent of the power.
     * @return the computed power.
     * @since 1.2.0
     */
    Element pow(BigInteger n);

    /**
     * Compute the power to n, where n is an element of a ring Z_N for some N.
     *
     * @param n the exponent of the power.
     * @return the computed power.
     * @since 1.2.0
     */
    Element powZn(Element n);

}
