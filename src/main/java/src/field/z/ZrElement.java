package src.field.z;


import src.api.Element;
import src.util.Arrays;
import src.util.math.BigIntegerUtils;

import java.math.BigInteger;

/**
 * @author Angelo De Caro (jpbclib@gmail.com)
 */
public class ZrElement<F extends ZrField> extends AbstractZElement<F> {

    protected BigInteger order;


    public ZrElement(F field) {
        super(field);

        this.value = BigInteger.ZERO;
        this.order = field.getOrder();
    }

    public ZrElement(F field, BigInteger value) {
        super(field);

        this.value = value;
        this.order = field.getOrder();
    }

    public ZrElement(ZrElement<F> zrElement) {
        super(zrElement.getField());

        this.value = zrElement.value;
        this.order = zrElement.field.getOrder();
    }


    public F getField() {
        return field;
    }

    @Override
    public Element getImmutable() {
        return new ImmutableZrElement(this);
    }

    public ZrElement<F> duplicate() {
        return new ZrElement<F>(this);
    }

    public ZrElement set(Element value) {
        this.value = value.toBigInteger().mod(order);

        return this;
    }

    public ZrElement set(int value) {
        this.value = BigInteger.valueOf(value).mod(order);

        return this;
    }

    public ZrElement set(BigInteger value) {
        this.value = value.mod(order);

        return this;
    }

    public boolean isZero() {
        return BigInteger.ZERO.equals(value);
    }

    public boolean isOne() {
        return BigInteger.ONE.equals(value);
    }

    public ZrElement twice() {
//        this.value = value.multiply(BigIntegerUtils.TWO).mod(order);
        this.value = value.add(value).mod(order);

        return this;
    }

    public ZrElement mul(int z) {
        this.value = this.value.multiply(BigInteger.valueOf(z)).mod(order);

        return this;
    }

    public ZrElement setToZero() {
        this.value = BigInteger.ZERO;

        return this;
    }

    public ZrElement setToOne() {
        this.value = BigInteger.ONE;

        return this;
    }

    public ZrElement setToRandom() {
        this.value = BigIntegerUtils.getRandom(order, field.getRandom());

        return this;
    }

    public ZrElement setFromHash(byte[] source, int offset, int length) {
        int i = 0, n, count = (order.bitLength() + 7) / 8;
        byte[] buf = new byte[count];

        byte counter = 0;
        boolean done = false;

        for (;;) {
            if (length >= count - i) {
                n = count - i;
                done = true;
            } else n = length;

            System.arraycopy(source, offset, buf, i, n);
            i += n;

            if (done)
                break;

            buf[i] = counter;
            counter++;
            i++;

            if (i == count) break;
        }
        assert (i == count);

        //mpz_import(z, count, 1, 1, 1, 0, buf);
        BigInteger z = new BigInteger(1, buf);

        while (z.compareTo(order) > 0) {
            z = z.divide(BigIntegerUtils.TWO);
        }

        this.value = z;

        return this;
    }

    public int setFromBytes(byte[] source) {
        return setFromBytes(source, 0);
    }

    public int setFromBytes(byte[] source, int offset) {
        byte[] buffer = Arrays.copyOf(source, offset, field.getLengthInBytes());
        value = new BigInteger(1, buffer).mod(order);

        return buffer.length;
    }

    public ZrElement square() {
//        value = value.modPow(BigIntegerUtils.TWO, order);
        value = value.multiply(value).mod(order);

        return this;
    }

    public ZrElement invert() {
        try {
            value = value.modInverse(order);
        } catch (Exception e) {
            e.printStackTrace();
            throw (RuntimeException) e;
        }

        return this;
    }

    public ZrElement halve() {
        value = value.multiply(((ZrField) field).twoInverse).mod(order);

        return this;
    }

    public ZrElement negate() {
        if (isZero()) {
            value = BigInteger.ZERO;
            return this;
        }

        value = order.subtract(value);

        return this;
    }

    public ZrElement add(Element element) {
        value = value.add(((AbstractZElement)element).value).mod(order);

        return this;
    }

    public ZrElement sub(Element element) {
        value = value.subtract(((ZrElement)element).value).mod(order);

        return this;
    }

    public ZrElement div(Element element) {
        value = value.multiply(((ZrElement)element).value.modInverse(order)).mod(order);

        return this;
    }

    public ZrElement mul(Element element) {
        value = value.multiply(((AbstractZElement)element).value).mod(order);

        return this;
    }

    public ZrElement mul(BigInteger n) {
        this.value = this.value.multiply(n).mod(order);

        return this;
    }

    public ZrElement mulZn(Element z) {
        this.value = this.value.multiply(z.toBigInteger()).mod(order);

        return this;
    }

    public boolean isSqr() {
        return BigInteger.ZERO.equals(value) || BigIntegerUtils.legendre(value, order) == 1;
    }

    public ZrElement sqrt() {
        // Apply the Tonelli-Shanks Algorithm

        Element e0 = field.newElement();
        Element nqr = field.getNqr();
        Element gInv = nqr.duplicate().invert();

        // let q be the order of the field
        // q - 1 = 2^s t, for some t odd
        BigInteger t = order.subtract(BigInteger.ONE);
        int s = BigIntegerUtils.scanOne(t, 0);
        t = t.divide(BigInteger.valueOf(2 << (s - 1)));

        BigInteger e = BigInteger.ZERO;
        BigInteger orderMinusOne = order.subtract(BigInteger.ONE);

        for (int i = 2; i <= s; i++) {
            e0.set(gInv).pow(e);
            e0.mul(this).pow(orderMinusOne.divide(BigInteger.valueOf(2 << (i - 1))));

            if (!e0.isOne())
                e = e.setBit(i - 1);
        }
        e0.set(gInv).pow(e);
        e0.mul(this);
        t = t.add(BigInteger.ONE);
        t = t.divide(BigIntegerUtils.TWO);
        e = e.divide(BigIntegerUtils.TWO);

        // TODO(-):
        // (suggested by Hovav Shacham) replace next three lines with
        //  element_pow2_mpz(x, e0, t, nqr, e);
        // once sliding windows are implemented for pow2

        e0.pow(t);
        set(nqr).pow(e).mul(e0);

        return this;
    }


    public ZrElement pow(BigInteger n) {
        this.value = this.value.modPow(n, order);

        return this;
    }

    public ZrElement powZn(Element n) {
        return pow(n.toBigInteger());
    }

    public boolean isEqual(Element e) {
        return this == e || (e instanceof  ZrElement && value.compareTo(((ZrElement) e).value) == 0);

    }

    public BigInteger toBigInteger() {
        return value;
    }

    @Override
    public byte[] toBytes() {
        byte[] bytes = value.toByteArray();

        if (bytes.length > field.getLengthInBytes()) {
            // strip the zero prefix
            if (bytes[0] == 0 && bytes.length == field.getLengthInBytes() + 1) {
                // Remove it
                bytes = Arrays.copyOfRange(bytes, 1, bytes.length);
            } else
                throw new IllegalStateException("result has more than FixedLengthInBytes.");
        } else if (bytes.length < field.getLengthInBytes()) {
            byte[] result = new byte[field.getLengthInBytes()];
            System.arraycopy(bytes, 0, result, field.getLengthInBytes() - bytes.length, bytes.length);
            return result;
        }
        return bytes;
    }

    public int sign() {
        if (isZero())
            return 0;

        if (field.isOrderOdd()) {
            return BigIntegerUtils.isOdd(value) ? 1 : -1;
        } else {
            return value.add(value).compareTo(order);
        }
    }

    public String toString() {
        return value.toString();
    }

}
