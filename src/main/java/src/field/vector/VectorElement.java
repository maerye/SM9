package src.field.vector;


import src.api.Element;
import src.api.ElementPowPreProcessing;
import src.field.base.AbstractVectorElement;

import java.math.BigInteger;
import java.util.List;

/**
 * @author Angelo De Caro (jpbclib@gmail.com)
 */
public class VectorElement<E extends Element> extends AbstractVectorElement<E, VectorField> {


    public VectorElement(VectorField field) {
        super(field);

        for (int i = 0; i < field.n; i++)
            coeff.add((E) field.getTargetField().newElement());
    }

    public VectorElement(VectorElement element) {
        super(element.getField());

        for (int i = 0; i < field.n; i++)
            coeff.add((E) element.getAt(i).duplicate());
    }


    public VectorElement(VectorField field, List<E> coeff) {
        super(field);
        this.field = field;

        this.coeff = coeff;
    }


    public VectorField getField() {
        return field;
    }

    public VectorElement<E> duplicate() {
        return new VectorElement<E>(this);
    }

    public VectorElement<E> getImmutable() {
        return new ImmutableVectorElement<E>(this);
    }

    public VectorElement<E> set(Element e) {
        VectorElement<E> element = (VectorElement<E>) e;

        for (int i = 0; i < coeff.size(); i++) {
            coeff.get(i).set(element.coeff.get(i));
        }

        return this;
    }

    public VectorElement<E> set(int value) {
        coeff.get(0).set(value);

        for (int i = 1; i < field.n; i++) {
            coeff.get(i).setToZero();
        }

        return this;
    }

    public VectorElement<E> set(BigInteger value) {
        coeff.get(0).set(value);

        for (int i = 1; i < field.n; i++) {
            coeff.get(i).setToZero();
        }

        return this;
    }

    public VectorElement<E> setToRandom() {
        for (int i = 0; i < field.n; i++) {
            coeff.get(i).setToRandom();
        }

        return this;
    }

    public VectorElement<E> setFromHash(byte[] source, int offset, int length) {
        for (int i = 0; i < field.n; i++) {
            coeff.get(i).setFromHash(source, offset, length);
        }

        return this;
    }

    public VectorElement<E> setToZero() {
        for (int i = 0; i < field.n; i++) {
            coeff.get(i).setToZero();
        }

        return this;
    }

    public boolean isZero() {
        for (int i = 0; i < field.n; i++) {
            if (!coeff.get(i).isZero())
                return false;
        }
        return true;
    }

    public VectorElement<E> setToOne() {
        coeff.get(0).setToOne();

        for (int i = 1; i < field.n; i++) {
            coeff.get(i).setToZero();
        }

        return this;
    }

    public boolean isOne() {
        if (!coeff.get(0).isOne())
            return false;

        for (int i = 1; i < field.n; i++) {
            if (!coeff.get(i).isZero())
                return false;
        }

        return true;
    }

    public VectorElement<E> map(Element e) {
        coeff.get(0).set(e);
        for (int i = 1; i < field.n; i++) {
            coeff.get(i).setToZero();
        }

        return this;
    }

    public VectorElement<E> twice() {
        for (int i = 0; i < field.n; i++) {
            coeff.get(i).twice();
        }

        return this;
    }

    public VectorElement<E> square() {
        for (int i = 0; i < field.n; i++) {
            coeff.get(i).square();
        }

        return this;
    }

    public VectorElement<E> invert() {
        throw new IllegalStateException("Not implemented yet!!!");
    }

    public VectorElement<E> negate() {
        for (Element e : coeff) {
            e.negate();
        }

        return this;
    }

    public VectorElement<E> add(Element e) {
        VectorElement<E> element = (VectorElement<E>) e;

        for (int i = 0; i < field.n; i++) {
            coeff.get(i).add(element.coeff.get(i));
        }

        return this;
    }

    public VectorElement<E> sub(Element e) {
        VectorElement<E> element = (VectorElement<E>) e;

        for (int i = 0; i < field.n; i++) {
            coeff.get(i).sub(element.coeff.get(i));
        }

        return this;
    }

    public VectorElement<E> mul(Element e) {
        throw new IllegalStateException("Not implemented yet!!!");
    }

    public VectorElement<E> mul(int z) {
        for (int i = 0; i < field.n; i++) {
            coeff.get(i).mul(z);
        }

        return this;
    }

    public VectorElement<E> mul(BigInteger n) {
        for (int i = 0; i < field.n; i++) {
            coeff.get(i).mul(n);
        }

        return this;
    }

    public VectorElement<E> powZn(Element e) {
        for (int i = 0; i < field.n; i++) {
            coeff.get(i).powZn(e);
        }

        return this;
    }

    public ElementPowPreProcessing getElementPowPreProcessing() {
        return new VectorElementPowPreProcessing(this);
    }

    public VectorElement<E> sqrt() {
        throw new IllegalStateException("Not implemented yet!!!");
    }

    public boolean isSqr() {
        throw new IllegalStateException("Not implemented yet!!!");
    }

    public int sign() {
        throw new IllegalStateException("Not implemented yet!!!");
    }

    public boolean isEqual(Element e) {
        if (e == this)
            return true;
        if ((e instanceof VectorElement))
            return false;

        VectorElement<E> element = (VectorElement<E>) e;

        for (int i = 0; i < field.n; i++) {
            if (!coeff.get(i).isEqual(element.coeff.get(i)))
                return false;
        }

        return true;
    }

    public int setFromBytes(byte[] source) {
        return setFromBytes(source, 0);
    }

    public int setFromBytes(byte[] source, int offset) {
        int len = offset;
        for (int i = 0, size = coeff.size(); i < size; i++) {
            len += coeff.get(i).setFromBytes(source, len);
        }
        return len - offset;
    }

    public byte[] toBytes() {
        byte[] buffer = new byte[field.getLengthInBytes()];
        int targetLB = field.getTargetField().getLengthInBytes();

        for (int len = 0, i = 0, size = coeff.size(); i < size; i++, len += targetLB) {
            byte[] temp = coeff.get(i).toBytes();
            System.arraycopy(temp, 0, buffer, len, targetLB);
        }
        return buffer;
    }

    public BigInteger toBigInteger() {
        return coeff.get(0).toBigInteger();
    }


    public String toString() {
        StringBuffer buffer = new StringBuffer("[");
        for (Element e : coeff) {
            buffer.append(e).append(", ");
        }
        buffer.append("]");
        return buffer.toString();
    }

}