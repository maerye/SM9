package src.field.z;


import src.api.Element;
import src.util.Arrays;

import java.math.BigInteger;

/**
 * @author Angelo De Caro (jpbclib@gmail.com)
 */
public class ZElement extends AbstractZElement<ZField> {


    public ZElement(ZField field) {
        super(field);

        this.value = BigInteger.ZERO;
    }

    public ZElement(ZField field, BigInteger value) {
        super(field);

        this.value = value;
    }

    public ZElement(ZElement zrElement) {
        super(zrElement.getField());

        this.value = zrElement.value;
    }


    public ZField getField() {
        return field;
    }

    @Override
    public Element getImmutable() {
        throw new IllegalStateException("Not implemented yet!!!");
    }

    public ZElement duplicate() {
        return new ZElement(this);
    }

    public ZElement set(Element value) {
        this.value = ((AbstractZElement) value).value;

        return this;
    }

    public ZElement set(int value) {
        this.value = BigInteger.valueOf(value);

        return this;
    }

    public ZElement set(BigInteger value) {
        this.value = value;

        return this;
    }

    public boolean isZero() {
        return BigInteger.ZERO.equals(value);
    }

    public boolean isOne() {
        return BigInteger.ONE.equals(value);
    }

    public ZElement twice() {
//        this.value = value.multiply(BigIntegerUtils.TWO);
        this.value = value.add(value);

        return this;
    }

    public ZElement mul(int z) {
        this.value = this.value.multiply(BigInteger.valueOf(z));

        return this;
    }

    public ZElement setToZero() {
        this.value = BigInteger.ZERO;

        return this;
    }

    public ZElement setToOne() {
        this.value = BigInteger.ONE;

        return this;
    }

    public ZElement setToRandom() {
        throw new IllegalStateException("Not implemented yet!!!");
    }

    public ZElement setFromHash(byte[] source, int offset, int length) {
        throw new IllegalStateException("Not implemented yet!!!");
    }

    public int setFromBytes(byte[] source) {
        return setFromBytes(source, 0);
    }

    public int setFromBytes(byte[] source, int offset) {
        byte[] buffer = Arrays.copyOf(source, offset, field.getLengthInBytes());
        value = new BigInteger(1, buffer);

        return buffer.length;
    }

    public ZElement square() {
//        value = value.modPow(BigIntegerUtils.TWO, order);
        value = value.multiply(value);

        return this;
    }

    public ZElement invert() {
        throw new IllegalStateException("Not implemented yet!!!");
    }

    public ZElement halve() {
        throw new IllegalStateException("Not implemented yet!!!");
    }

    public ZElement negate() {
        value = value.multiply(BigInteger.valueOf(-1));

        return this;
    }

    public ZElement add(Element element) {
        value = value.add(((AbstractZElement)element).value);

        return this;
    }

    public ZElement sub(Element element) {
        value = value.subtract(((AbstractZElement)element).value);

        return this;
    }

    public ZElement div(Element element) {
        throw new IllegalStateException("Not implemented yet!!!");
    }

    public ZElement mul(Element element) {
        value = value.multiply(((AbstractZElement)element).value);

        return this;
    }

    public ZElement mul(BigInteger n) {
        this.value = this.value.multiply(n);

        return this;
    }

    public ZElement mulZn(Element z) {
        this.value = this.value.multiply(z.toBigInteger());

        return this;
    }

    public boolean isSqr() {
        throw new IllegalStateException("Not implemented yet!!!");
    }

    public ZElement sqrt() {
        throw new IllegalStateException("Not implemented yet!!!");
    }


    public ZElement pow(BigInteger n) {
        throw new IllegalStateException("Not implemented yet!!!");
    }

    public ZElement powZn(Element n) {
        return pow(n.toBigInteger());
    }

    public boolean isEqual(Element e) {
        return this == e || (e instanceof ZElement && value.compareTo(((ZElement) e).value) == 0);

    }

    public BigInteger toBigInteger() {
        return value;
    }

    @Override
    public byte[] toBytes() {
        throw new IllegalStateException("Not implemented yet!!!");
    }

    public int sign() {
        return value.signum();
    }

    public String toString() {
        return value.toString();
    }

}
