package src.field.quadratic;


import src.api.Element;
import src.field.base.AbstractPointElement;

import java.math.BigInteger;

/**
 * @author Angelo De Caro (jpbclib@gmail.com)
 */
public class QuadraticElement<E extends Element> extends AbstractPointElement<E, QuadraticField> {


    public QuadraticElement(QuadraticField field) {
        super(field);

        this.x = (E) field.getTargetField().newElement();
        this.y = (E) field.getTargetField().newElement();
    }

    public QuadraticElement(QuadraticElement element) {
        super(element.getField());

        this.x = (E) element.x.duplicate();
        this.y = (E) element.y.duplicate();
    }


    public QuadraticField getField() {
        return field;
    }

    @Override
    public Element getImmutable() {
        return new ImmutableQuadraticElement<E>(this);
    }

    public QuadraticElement duplicate() {
        return new QuadraticElement(this);
    }

    public QuadraticElement set(Element e) {
        QuadraticElement element = (QuadraticElement) e;

        this.x.set(element.x);
        this.y.set(element.y);

        return this;
    }

    public QuadraticElement set(int value) {
        x.set(value);
        y.setToZero();

        return this;
    }

    public QuadraticElement set(BigInteger value) {
        x.set(value);
        y.setToZero();

        return this;
    }

    public boolean isZero() {
        return x.isZero() && y.isZero();
    }

    public boolean isOne() {
        return x.isOne() && y.isZero();
    }

    public QuadraticElement setToZero() {
        x.setToZero();
        y.setToZero();

        return this;
    }

    public QuadraticElement setToOne() {
        x.setToOne();
        y.setToZero();

        return this;
    }

    public QuadraticElement setToRandom() {
        x.setToRandom();
        y.setToRandom();

        return this;
    }

    public int setFromBytes(byte[] source, int offset) {
        int len;

        len = x.setFromBytes(source, offset);
        len += y.setFromBytes(source, offset + len);

        return len;
    }

    public QuadraticElement twice() {
        x.twice();
        y.twice();

        return this;
    }

    public QuadraticElement mul(int z) {
        x.mul(z);
        y.mul(z);

        return this;
    }

    public QuadraticElement square() {
        Element e0 = x.duplicate().square();
        Element e1 = y.duplicate().square();
        e1.mul(field.getTargetField().getNqr());
        e0.add(e1);
        e1.set(x).mul(y);
        e1.twice();
        x.set(e0);
        y.set(e1);

        return this;
    }

    public QuadraticElement invert() {
        Element e0 = x.duplicate().square();
        Element e1 = y.duplicate().square();
        e1.mul(field.getTargetField().getNqr());
        e0.sub(e1);
        e0.invert();
        x.mul(e0);
        e0.negate();
        y.mul(e0);

        return this;
    }

    public QuadraticElement negate() {
        x.negate();
        y.negate();

        return this;
    }

    public QuadraticElement add(Element e) {
        QuadraticElement element = (QuadraticElement) e;

        x.add(element.x);
        y.add(element.y);

        return this;
    }

    public QuadraticElement sub(Element e) {
        QuadraticElement element = (QuadraticElement) e;

        x.sub(element.x);
        y.sub(element.y);

        return this;
    }

    public QuadraticElement mul(Element e) {
        QuadraticElement element = (QuadraticElement) e;

        Element e0 = x.duplicate().add(y);
        Element e1 = element.x.duplicate().add(element.y);
        Element e2 = e0.duplicate().mul(e1);

        e0.set(x).mul(element.x);
        e1.set(y).mul(element.y);
        
        x.set(e1).mul(field.getTargetField().getNqr()).add(e0);
        e2.sub(e0);
        y.set(e2).sub(e1);

        return this;
    }

    public QuadraticElement mul(BigInteger n) {
        x.mul(n);
        y.mul(n);

        return this;
    }

    public QuadraticElement mulZn(Element e) {
        x.mulZn(e);
        y.mulZn(e);

        return this;
    }

    public boolean isSqr() {
        Element e0 = x.duplicate().square();
        Element e1 = y.duplicate().square();
        e1.mul(field.getTargetField().getNqr());
        e0.sub(e1);

        return e0.isSqr();
    }

    public QuadraticElement sqrt() {
        Element e0 = x.duplicate().square();
        Element e1 = y.duplicate().square();
        e1.mul(field.getTargetField().getNqr());
        e0.sub(e1);
        e0.sqrt();
        e1.set(x).add(e0);

        Element e2 = x.getField().newElement().set(2).invert();
        e1.mul(e2);

        if (!e1.isSqr())
            e1.sub(e0);

        e0.set(e1).sqrt();
        e1.set(e0).add(e0);
        e1.invert();
        y.mul(e1);
        x.set(e0);

        return this;
    }

    public boolean isEqual(Element e) {
        if (e == this)
            return true;

        if (!(e instanceof QuadraticElement))
            return false;
        
        QuadraticElement element = (QuadraticElement) e;
        return x.isEqual(element.x) && y.isEqual(element.y);
    }

    public QuadraticElement powZn(Element n) {
        pow(n.toBigInteger());

        return this;
    }

    public BigInteger toBigInteger() {
        return x.toBigInteger();
    }

    public byte[] toBytes() {
        byte[] xBytes = x.toBytes();
        byte[] yBytes = y.toBytes();

        byte[] result = new byte[xBytes.length + yBytes.length];
        System.arraycopy(xBytes, 0, result, 0, xBytes.length);
        System.arraycopy(yBytes, 0, result, xBytes.length, yBytes.length);

        return result;
    }

    public QuadraticElement setFromHash(byte[] source, int offset, int length) {
        int k = length / 2;
        x.setFromHash(source, offset, k);
        y.setFromHash(source, offset + k, k);

        return this;
    }

    public int sign() {
        int res = x.sign();
        if (res == 0)
            return y.sign();
        return res;
    }


    public String toString() {
        return String.format("{x=%s,y=%s}", x, y);
    }

}