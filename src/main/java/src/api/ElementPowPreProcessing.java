package src.api;

import java.math.BigInteger;

/**
 * @author Angelo De Caro (jpbclib@gmail.com)
 * @since 1.0.0
 */
public interface ElementPowPreProcessing extends ElementPow, PreProcessing {

    /**
     * Returns the field the pre-processed element belongs to.
     *
     * @return Returns the field the pre-processed element belongs to.
     * @since 1.2.0
     */
    Field getField();

    /**
     * Compute the power to n using the pre-processed information.
     *
     * @param n the exponent of the power.
     * @return a new element whose value is the computed power.
     * @since 1.0.0
     */
    Element pow(BigInteger n);

    /**
     * Compute the power to n, where n is an element of a ring Z_N for some N,
     * using the pre-processed information, 
     *
     * @param n the exponent of the power.
     * @return a new element whose value is the computed power.
     * @since 1.0.0
     */
    Element powZn(Element n);

}
