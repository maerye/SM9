package src.api;

import java.math.BigInteger;

/**
 * Elements of groups, rings and fields are accessible using the <code>Element</code>
 * interface. You can obtain an instance of an Element starting from an algebraic structure, such as a particular
 * finite field or elliptic curve group, represented by the <code>Field</code> interface.
 *
 * @author Angelo De Caro (jpbclib@gmail.com)
 * @see Field
 * @since 1.0.0
 */
public interface Element extends ElementPow {

    /**
     * Returns the field to which this element lie.
     *
     * @return the field to which this element lie.
     * @since 1.0.0
     */
    Field getField();

    /**
     * Returns the length in bytes necessary to represent this element.
     *
     * @return the length in bytes necessary to represent this element.
     * @see it.unisa.dia.gas.jpbc.Field#getLengthInBytes()
     * @since 1.0.0
     */
    int getLengthInBytes();

    /**
     * Returns <tt>true</tt> if this element is immutable, <tt>false</tt> otherwise.
     *
     * @return <tt>true</tt> if this element is immutable, <tt>false</tt> otherwise.
     * @see #getImmutable() 
     */
    boolean isImmutable();

    /**
     * Returns an immutable copy of this element if the
     * element is not already immutable.
     * <br/>
     * For immutable elements the internal value cannot be modified after it is created,
     * any method designed to modify the internal state of the element will return
     * a new element whose internal value represents the computation executed.
     *
     * @return an immutable copy of this element if the
     * element is not already immutable.
     * @see #isImmutable()
     */
    Element getImmutable();

    /**
     * Returns a copy of this element. If this element
     * is immutable then the copy is mutable.
     *
     * @return a copy of this element.
     * @since 1.0.0
     */
    Element duplicate();

    /**
     * Sets this element to value.
     *
     * @param value the new value of this element.
     * @return this element set to value.
     * @since 1.0.0
     */
    Element set(Element value);

    /**
     * Sets this element to value.
     *
     * @param value the new value of this element.
     * @return this element set to value.
     * @since 1.0.0
     */
    Element set(int value);

    /**
     * Sets this element to value.
     *
     * @param value the new value of this element.
     * @return this element set to value.
     * @since 1.0.0
     */
    Element set(BigInteger value);

    /**
     * Converts this to a BigInteger if such operation makes sense.
     *
     * @return a BigInteger which represents this element.
     * @since 1.0.0
     */
    BigInteger toBigInteger();

    /**
     * If this element lies in a finite algebraic structure, assigns a uniformly random element to it.
     *
     * @return this.
     * @since 1.0.0
     */
    Element setToRandom();

    /**
     * Sets this element deterministically from the length bytes stored in the source parameter starting from the passed offset.
     *
     * @param source the buffer data.
     * @param offset the starting offset.
     * @param length the number of bytes to be used.
     * @return this element modified.
     * @since 1.0.0
     */
    Element setFromHash(byte[] source, int offset, int length);

    /**
     * Reads this element from the buffer source.
     *
     * @param source the source of bytes.
     * @return the number of bytes read.
     * @since 1.0.0
     */
    int setFromBytes(byte[] source);

    /**
     * Reads this element from the buffer source starting from offset.
     *
     * @param source the source of bytes.
     * @param offset the starting offset.
     * @return the number of bytes read.
     * @since 1.0.0
     */
    int setFromBytes(byte[] source, int offset);

    /**
     * Converts this element to bytes. The number of bytes it will
     * write can be determined calling getLengthInBytes().
     *
     * @return the bytes written.
     * @since 1.0.0
     */
    byte[] toBytes();

    /**
     * Returns the canonical representation of this element.
     * In most of the cases the output of this method
     * is the same as that of the #toBytes method.
     *
     * @return the canonical representation of this element.
     * @since 2.0.0
     */
    byte[] toCanonicalRepresentation();

    /**
     * Sets this element to zero.
     *
     * @return this element set to zero.
     * @since 1.0.0
     */
    Element setToZero();

    /**
     * Returns true if n is zero, false otherwise.
     *
     * @return true if n is zero, false otherwise.
     * @since 1.0.0
     */
    boolean isZero();

    /**
     * Sets this element to one.
     *
     * @return this element set to one.
     * @since 1.0.0
     */
    Element setToOne();

    /**
     * Returns <tt>true</tt> if this and value have the same value, <tt>false</tt> otherwise.
     *
     * @param value the element to be compared.
     * @return <tt>true</tt> if this and value have the same value, <tt>false</tt> otherwise.
     */
    boolean isEqual(Element value);

    /**
     * Returns true if n is one, false otherwise.
     *
     * @return true if n is one, false otherwise.
     * @since 1.0.0
     */
    boolean isOne();

    /**
     * Sets this = this + this.
     *
     * @return this + this.
     * @since 1.0.0
     */
    Element twice();

    /**
     * Se this = this^2.
     *
     * @return this^2.
     * @since 1.0.0
     */
    Element square();

    /**
     * Sets this to the inverse of itself.
     *
     * @return the inverse of itself.
     * @since 1.0.0
     */
    Element invert();

    /**
     * Sets this = this / 2.
     *
     * @return this / 2.
     * @since 1.0.0
     */
    Element halve();

    /**
     * Set this = -this.
     *
     * @return -this.
     * @since 1.0.0
     */
    Element negate();

    /**
     * Sets this = this + element.
     *
     * @param element the value to be added.
     * @return this + element.
     * @since 1.0.0
     */
    Element add(Element element);

    /**
     * Sets this = this - element.
     *
     * @param element the value to be subtracted.
     * @return this - element.
     * @since 1.0.0
     */
    Element sub(Element element);

    /**
     * Sets this = this * element.
     *
     * @param element the value to be multiplied
     * @return this * element.
     * @since 1.0.0
     */
    Element mul(Element element);

    /**
     * Sets this = this * z, that is this + this + ... + this where there are z this's.
     *
     * @param z the value to be multiplied
     * @return this * z
     * @since 1.0.0
     */
    Element mul(int z);

    /**
     * Sets this = this * n, that is this + this + ... + this where there are n this's.
     *
     * @param n the value to be multiplied
     * @return this * n
     * @since 1.0.0
     */
    Element mul(BigInteger n);

    /**
     * Sets this = this * z, that is this + this + … + this where there are z this's and
     * z is an element of a ring Z_N for some N.
     *
     * @param z the value to be multiplied
     * @return this * z
     * @since 1.0.0
     */
    Element mulZn(Element z);

    /**
     * Sets this = this / element
     *
     * @param element is the divisor.
     * @return this / element
     * @since 1.0.0
     */
    Element div(Element element);

    /**
     * Sets this = this^n.
     *
     * @param n the exponent of the power.
     * @return this^n.
     * @since 1.0.0
     */
    Element pow(BigInteger n);

    /**
     * Sets this = this^n, where n is an element of a ring Z_N  for some N
     * (typically the order of the algebraic structure n lies in).
     *
     * @param n the exponent of the power.
     * @return this^n
     * @since 1.0.0
     */
    Element powZn(Element n);

    /**
     * Prepare to exponentiate this element and returns pre-processing information.
     *
     * @return the pre-processing information used to execute the exponentation of this element.
     * @see it.unisa.dia.gas.jpbc.ElementPowPreProcessing
     */
    ElementPowPreProcessing getElementPowPreProcessing();

    /**
     * Sets this = sqrt(this).
     *
     * @return the square radix of this element.
     * @since 1.0.0
     */
    Element sqrt();

    /**
     * Returns true if this element is a perfect square (quadratic residue), false otherwise.
     *
     * @return true if this element is a perfect square (quadratic residue), false otherwise.
     * @since 1.0.0
     */
    boolean isSqr();

    /**
     * If this element is zero, returns 0. For a non zero value the behaviour depends on the algebraic structure.
     *
     * @return 0 is this element is zero, otherwise the behaviour depends on the algebraic structure.
     * @since 1.0.0
     */
    int sign();

}