package src.api;

/**
 * This interface is used to compute the pairing function when pre-processed information has
 * been compute before on the first input which is so fixed for each instance of this interface.
 *
 * @author Angelo De Caro (jpbclib@gmail.com)
 * @since 1.0.0
 */
public interface PairingPreProcessing extends PreProcessing {

    /**
     * Compute the pairing where the second argument is in2. The pre-processed information
     * are used for a fast computation
     *
     * @param in2 the second pairing function argument.
     * @return an element from GT whose value is assigned by this map applied to in1 and in2.
     * @since 1.0.0
     */
    Element pairing(Element in2);

}
