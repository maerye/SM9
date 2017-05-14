package src.api;

/**
 * This interface represents an element with two coordinates.
 * (A point over an elliptic curve).
 *
 * @author Angelo De Caro (jpbclib@gmail.com)
 * @since 1.0.0
 */
public interface Point<E extends Element> extends Element, Vector<E> {

    /**              
     * Returns the x-coordinate.
     *
     * @return the x-coordinate.
     * @since 1.0.0
     */
    E getX();

    /**
     * Returns the y-coordinate.
     *
     * @return the y-coordinate.
     * @since 1.0.0
     */
    E getY();

    /**
     * Returns the length in bytes needed to represent this element in a compressed way.
     *
     * @return the length in bytes needed to represent this element in a compressed way.
     */
    int getLengthInBytesCompressed();

    /**
     * Converts this element to bytes. The number of bytes it will write can be determined calling getLengthInBytesCompressed().
     *
     * @return the bytes written.
     * @since 1.0.0
     */
    byte[] toBytesCompressed();

    /**
     * Reads this element from the buffer source. staring from the passed offset.
     *
     * @param source the source of bytes.
     * @return the number of bytes read.
     * @since 1.0.0
     */
    int setFromBytesCompressed(byte[] source);

    /**
     * Reads the x-coordinate from the buffer source staring from the passed offset. The y-coordinate is then recomputed.
     * Pay attention. the y-coordinate could be different from the element which originates the buffer source.
     *
     * @param source the source of bytes.
     * @param offset the starting offset.
     * @return the number of bytes read.
     * @since 1.0.0
     */
    int setFromBytesCompressed(byte[] source, int offset);

    /**
     * Returns the length in bytes needed to represent the x coordinate of this element.
     *
     * @return the length in bytes needed to represent the x coordinate of this element.
     * @since 1.0.0
     */
    int getLengthInBytesX();

    /**
     * Converts the x-coordinate to bytes. The number of bytes it will write can be determined calling getLengthInBytesX().
     *
     * @return the bytes written.
     * @since 1.0.0
     */
    byte[] toBytesX();

    /**
     * Reads the x-coordinate from the buffer source. The y-coordinate is then recomputed.
     * Pay attention. the y-coordinate could be different from the element which originates the buffer source.
     *
     * @param source the source of bytes.
     * @return the number of bytes read.
     * @since 1.0.0
     */
    int setFromBytesX(byte[] source);

    /**
     * Reads the x-coordinate from the buffer source staring from the passed offset. The y-coordinate is then recomputed.
     * Pay attention. the y-coordinate could be different from the element which originates the buffer source.
     *
     * @param source the source of bytes.
     * @param offset the starting offset.
     * @return the number of bytes read.
     * @since 1.0.0
     */
    int setFromBytesX(byte[] source, int offset);

}
