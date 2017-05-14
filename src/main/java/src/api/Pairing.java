package src.api;

/**
 * This interface gives access to the pairing functions.
 * Pairings involve three groups of prime order r called G1, G2, and GT.
 * The pairing is a bilinear map that takes two elements as input, one from G1 and one from G2, and outputs an element of GT.
 * Sometimes G1 and G2 are the same group (i.e. the pairing is symmetric) so their elements can be mixed freely.
 * In this case the #isSymmetric() function returns true.
 *
 * @author Angelo De Caro (jpbclib@gmail.com)
 * @since 1.0.0
 */
public interface Pairing {

    /**
     * Returns true if this pairing is symmetric, false otherwise.
     *
     * @return true if this pairing is symmetric, false otherwise.
     * @since 1.0.0
     */
    boolean isSymmetric();

    /**
     * Returns the degree of the pairing.
     * For bilinear maps, this value is 2.
     * For multilinear maps, this value represents
     * the degree of linearity supported.
     *
     * @return the degree of the pairing.
     * @since 2.0.0
     */
    int getDegree();

    /**
     * Return the G1 group.
     *
     * @return the G1 group.
     * @since 1.0.0
     */
    Field getG1();

    /**
     * Return the G2 group.
     *
     * @return the G2 group.
     * @since 1.0.0
     */
    Field getG2();

    /**
     * Return the GT group which is the group of rth roots of unity.
     *
     * @return the GT group.
     * @since 1.0.0
     */
    Field getGT();

    /**
     * Return the Zr group.
     *
     * @return the Zr group.
     * @since 1.0.0
     */
    Field getZr();

    /**
     * Returns the field at level <tt>index</tt>.
     * For bilinear maps, Zr has index 0, G1 has index 1,
     * G2 has index 2 and GT has index 3.
     *
     * @return Returns the field at level <tt>index</tt>
     * @since 2.0.0
     */
    Field getFieldAt(int index);

    /**
     * Returns the index of the field if it belongs
     * to this pairing, otherwise it returns -1.
     *
     * @param field the field whose index has to be determine.
     * @return the index of the field if it belongs
     * to this pairing, otherwise it returns Unknown.
     * @see #getFieldAt(int)
     * @since 2.0.0
     */
    int getFieldIndex(Field field);

    /**
     * Applies the bilinear map. It returns e(in1, in2). g1 must be in the group G1, g2 must be in the group G2.
     *
     * @param in1 an element from G1.
     * @param in2 an element from G2.
     * @return an element from GT whose value is assigned by this map applied to in1 and in2.
     * @since 1.0.0
     */
    Element pairing(Element in1, Element in2);

    /**
     * Returns <tt>true</tt> if optimized
     * product of pairing is supported,
     * <tt>false</tt> otherwise.
     *
     * If optimized product of pairing is supported then
     * invoking the #pairing(Element[], Element[]) method should
     * guarantee better performance.
     *
     * @return <tt>true</tt> if optimized
     * product of pairing is supported,
     * <tt>false</tt> otherwise.
     * @since 2.0.0
     * @see #pairing(Element[], Element[])
     */
    boolean isProductPairingSupported();

    /**
     * Computes the product of pairings, that is
     * 'e'('in1'[0], 'in2'[0]) ... 'e'('in1'[n-1], 'in2'[n-1]).
     *
     * @param in1 must have at least 'n' elements belonging to the groups G1
     * @param in2 must have at least 'n' elements belonging to the groups G2
     * @return the product of pairings, that is 'e'('in1'[0], 'in2'[0]) ... 'e'('in1'[n-1], 'in2'[n-1]). 
     * @since 1.1.0
     */
    Element pairing(Element[] in1, Element[] in2);

    /**
     * Returns the length in bytes needed to represent a PairingPreProcessing structure.
     *
     * @return the length in bytes needed to represent a PairingPreProcessing structure.
     * @since 1.2.0
     * @see #getPairingPreProcessingFromBytes(byte[])
     * @see #getPairingPreProcessingFromElement(Element)
     * @see PairingPreProcessing
     */
    int getPairingPreProcessingLengthInBytes();

    /**
     * Get ready to perform a pairing whose first input is in1, returns the results of time-saving pre-computation.
     *
     * @param in1 the first input of a pairing execution, used to pre-compute the pairing.
     * @return the results of time-saving pre-computation.
     * @since 1.0.0
     */
    PairingPreProcessing getPairingPreProcessingFromElement(Element in1);

    /**
     * Reads a PairingPreProcessing from the buffer source.
     *
     * @param source the source of bytes.
     * @return the PairingPreProcessing instance.
     * @since 1.2.0
     */
    PairingPreProcessing getPairingPreProcessingFromBytes(byte[] source);

    /**
     * Reads a PairingPreProcessing from the buffer source starting from offset.
     *
     * @param source the source of bytes.
     * @param offset the starting offset.
     * @return the PairingPreProcessing instance.
     * @since 1.2.0
     */
    PairingPreProcessing getPairingPreProcessingFromBytes(byte[] source, int offset);

}
