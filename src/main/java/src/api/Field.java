package src.api;

import java.math.BigInteger;

/**
 * Represents an algebraic structure.
 *
 * @author Angelo De Caro (jpbclib@gmail.com)
 * @since 1.0.0
 */
public interface Field<E extends Element> {

    /**
     * Returns a new element which lies in this field.
     *
     * @return a new element which lies in this field.
     * @since 1.0.0
     */
    E newElement();

    /**
     * Returns a new element whose value is passed as parameter.
     *
     * @param value the value of the new element.
     * @return a new element whose value is passed as parameter.
     * @see Element#set(int)
     * @since 1.0.0
     */
    E newElement(int value);

    /**
     * Returns a new element whose value is passed as parameter.
     * 
     * @param value the value of the new element.
     * @return a new element whose value is passed as parameter.
     * @see Element#set(BigInteger)
     * @since 1.0.0
     */
    E newElement(BigInteger value);

    /**
     * Returns a new element whose value is copied from the passed element.
     *
     * @param e the element whose value is copied.
     * @return a new element whose value is copied from the passed element.
     * @since 2.0.0
     */
    E newElement(E e);

    /**
     * Returns a new element whose value is set deterministically from the length bytes stored
     * in the source parameter starting from the passed offset.
     *
     * @param source the buffer data.
     * @param offset the starting offset.
     * @param length the number of bytes to be used.
     * @return this element modified.
     * @since 2.0.0
     */
    E newElementFromHash(byte[] source, int offset, int length);

    /**
     * Returns a new element whose value is set from the buffer source.
     *
     * @param source the source of bytes.
     * @return the number of bytes read.
     * @since 2.0.0
     */
    E newElementFromBytes(byte[] source);

    /**
     * Returns a new element whose value is set from the buffer source starting from offset.
     *
     * @param source the source of bytes.
     * @param offset the starting offset.
     * @return the number of bytes read.
     * @since 2.0.0
     */
    E newElementFromBytes(byte[] source, int offset);

    /**
     * Returns a new element whose value is zero.
     *
     * @return a new element whose value is zero.
     * @since 1.0.0
     */
    E newZeroElement();

    /**
     * Returns a new element whose value is one.
     *
     * @return a new element whose value is one.
     * @since 1.0.0
     */
    E newOneElement();

    /**
     * Returns a new random element.
     *
     * @return a new random element.
     * @since 1.0.0
     */
    E newRandomElement();

    /**
     * Returns the order of this field.
     *
     * @return the order of this field. Returns 0 for infinite order.
     * @since 1.0.0
     */
    BigInteger getOrder();

    /**
     * Returns <tt>true></tt> if the order is odd,
     * false otherwise.
     *
     * @return <tt>true></tt> if the order is odd,
     * false otherwise.
     * @since 1.2.0
     */
    boolean isOrderOdd();

    /**
     * Returns a quadratic non-residue in this field. It returns always the same element.
     *
     * @return a quadratic non-residue in this field.
     * @since 1.0.0
     */
    E getNqr();

    /**
     * Returns the length in bytes needed to represent an element of this Field.
     *
     * @return the length in bytes needed to represent an element of this Field.
     * @since 1.0.0
     */
    int getLengthInBytes();

    /**
     * Returns the length in bytes needed to represent an element of this Field.
     *
     * @return the length in bytes needed to represent an element of this Field.
     * @since 1.0.0
     */
    int getLengthInBytes(Element e);

    /**
     * Returns the length in bytes needed to canonical represent an element of this Field.
     *
     * @return the length in bytes needed to canonical represent an element of this Field.
     * @since 2.0.0
     */
    int getCanonicalRepresentationLengthInBytes();

    /**
     * Computes the component-wise twice.
     *
     * @param elements the vector of elements to be twiced.
     * @return elements twiced.
     * @since 1.1.0
     */
    Element[] twice(Element[] elements);

    /**
     * Computes the component-wise addition between a and b.
     *
     * @param a an array of elements of the field
     * @param b another array of elements of the field to be added to a
     * @return the vector a modified by adding b.
     * @since 1.1.0
     */
    Element[] add(Element[] a, Element[] b);

    /**
     * Reads an ElementPowPreProcessing from the buffer source.
     *
     * @param source the source of bytes.
     * @return the ElementPowPreProcessing instance.
     * @since 1.2.0
     */
    ElementPowPreProcessing getElementPowPreProcessingFromBytes(byte[] source);

    /**
     * Reads an ElementPowPreProcessing from the buffer source starting from offset.
     *
     * @param source the source of bytes.
     * @param offset the starting offset.
     * @return the ElementPowPreProcessing instance.
     * @since 1.2.0
     */
    ElementPowPreProcessing getElementPowPreProcessingFromBytes(byte[] source, int offset);
}
