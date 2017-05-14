package src.field.z;


import src.api.Element;
import src.util.Arrays;
import src.util.math.BigIntegerUtils;

import java.math.BigInteger;

/**
 * @author Angelo De Caro (jpbclib@gmail.com)
 */
public class SymmetricZrElement<F extends SymmetricZrField> extends AbstractZElement<F> {

    protected BigInteger order;
    protected BigInteger halfOrder;


    public SymmetricZrElement(F field) {
        super(field);

        this.value = BigInteger.ZERO;

        this.order = field.getOrder();
        this.halfOrder = field.halfOrder;
    }

    public SymmetricZrElement(F field, BigInteger value) {
        super(field);

        this.order = field.getOrder();
        this.halfOrder = field.halfOrder;
        set(value);
    }

    public SymmetricZrElement(SymmetricZrElement<F> zrElement) {
        super(zrElement.getField());

        this.order = zrElement.field.getOrder();
        this.halfOrder = zrElement.field.halfOrder;
        this.value = zrElement.value.mod(order);
    }


    public F getField() {
        return field;
    }

    @Override
    public Element getImmutable() {
        throw new IllegalArgumentException("Not implemented yet!!!");
    }

    public SymmetricZrElement duplicate() {
        return new SymmetricZrElement(this);
    }

    public SymmetricZrElement set(Element value) {
        this.value = ((AbstractZElement) value).value.mod(order);

        return mod();
    }

    public SymmetricZrElement set(int value) {
        this.value = BigInteger.valueOf(value).mod(order);

        return mod();
    }

    public SymmetricZrElement set(BigInteger value) {
        this.value = value.mod(order);

        return mod();
    }

    public boolean isZero() {
        return BigInteger.ZERO.equals(value);
    }

    public boolean isOne() {
        return BigInteger.ONE.equals(value);
    }

    public SymmetricZrElement twice() {
//        this.value = value.multiply(BigIntegerUtils.TWO).mod(order);
        this.value = value.add(value).mod(order);

        return mod();
    }

    public SymmetricZrElement mul(int z) {
        this.value = this.value.multiply(BigInteger.valueOf(z)).mod(order);

        return mod();
    }

    public SymmetricZrElement setToZero() {
        this.value = BigInteger.ZERO;

        return this;
    }

    public SymmetricZrElement setToOne() {
        this.value = BigInteger.ONE;

        return this;
    }

    public SymmetricZrElement setToRandom() {
        this.value = BigIntegerUtils.getRandom(order, field.getRandom());

        return mod();
    }

    public SymmetricZrElement setFromHash(byte[] source, int offset, int length) {
        int i = 0, n, count = (order.bitLength() + 7) / 8;
        byte[] buf = new byte[count];

        byte counter = 0;
        boolean done = false;

        for (; ; ) {
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
        mod();

        return buffer.length;
    }

    public SymmetricZrElement square() {
//        value = value.modPow(BigIntegerUtils.TWO, order);
        value = value.multiply(value).mod(order);

        return mod();
    }

    public SymmetricZrElement invert() {
        value = value.modInverse(order);

        return mod();
    }

    public SymmetricZrElement halve() {
        value = value.multiply(field.twoInverse).mod(order);

        return mod();
    }

    public SymmetricZrElement negate() {
        if (isZero()) {
            value = BigInteger.ZERO;
            return this;
        }

        value = order.subtract(value);

        return mod();
    }

    public SymmetricZrElement add(Element element) {
        value = value.add(((AbstractZElement) element).value).mod(order);

        return mod();
    }

    public SymmetricZrElement sub(Element element) {
        value = value.subtract(((SymmetricZrElement) element).value).mod(order);

        return mod();
    }

    public SymmetricZrElement div(Element element) {
        value = value.multiply(((SymmetricZrElement) element).value.modInverse(order)).mod(order);

        return mod();
    }

    public SymmetricZrElement mul(Element element) {
        value = value.multiply(((AbstractZElement) element).value).mod(order);

        return mod();
    }

    public SymmetricZrElement mul(BigInteger n) {
        this.value = this.value.multiply(n).mod(order);

        return mod();
    }

    public SymmetricZrElement mulZn(Element z) {
        this.value = this.value.multiply(z.toBigInteger()).mod(order);

        return mod();
    }

    public boolean isSqr() {
        return BigInteger.ZERO.equals(value) || BigIntegerUtils.legendre(value, order) == 1;
    }

    public SymmetricZrElement sqrt() {
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


    public SymmetricZrElement pow(BigInteger n) {
        this.value = this.value.modPow(n, order);

        return mod();
    }

    public SymmetricZrElement powZn(Element n) {
        return pow(n.toBigInteger());
    }

    public boolean isEqual(Element e) {
        return this == e || (e instanceof SymmetricZrElement && value.compareTo(((SymmetricZrElement) e).value) == 0);
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

    private final SymmetricZrElement mod() {
        if (this.value.compareTo(halfOrder) > 0)
            this.value = this.value.subtract(order);

        return this;
    }


}
