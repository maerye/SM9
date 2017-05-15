package src.field.curve;

import src.api.Element;
import src.field.base.AbstractPointElement;
import src.util.math.BigIntegerUtils;

import java.math.BigInteger;

/**
 * @author Angelo De Caro (jpbclib@gmail.com)
 */
public class CurveElement<E extends Element, F extends CurveField> extends AbstractPointElement<E, F> {

    protected int infFlag;


    public CurveElement(F field) {
        super(field);

        this.x = (E) field.getTargetField().newElement();
        this.y = (E) field.getTargetField().newElement();
        this.infFlag = 1;
    }

    public CurveElement(CurveElement<E, F> curveElement) {
        super(curveElement.getField());

        this.x = (E) curveElement.x.duplicate();
        this.y = (E) curveElement.y.duplicate();
        this.infFlag = curveElement.infFlag;
    }


    public E getX() {
        return x;
    }

    public E getY() {
        return y;
    }

    public F getField() {
        return field;
    }

    public Element getImmutable() {
        return new ImmutableCurveElement<E, F>(this);
    }

    public CurveElement<E,F> duplicate() {
        return new CurveElement<E,F>(this);
    }

    public CurveElement set(Element e) {
        CurveElement element = (CurveElement) e;

        if (element.infFlag != 0) {
            infFlag = 1;
            return this;
        }

        this.x.set(element.x);
        this.y.set(element.y);
        this.infFlag = 0;

        return this;
    }
    public void setInfFlag(int flag){
        this.infFlag=flag;
    }
    public CurveElement set(int value) {
        if (value == 0 || value == 1)
            setToZero();
        else
            throw new IllegalStateException("Value not supported.");

        return this;
    }

    public CurveElement set(BigInteger value) {
        if (BigInteger.ZERO.equals(value) || BigInteger.ONE.equals(value))
            setToZero();
        else
            throw new IllegalStateException("Value not supported.");

        return this;
    }

    public boolean isZero() {
        return infFlag == 1;
    }

    public boolean isOne() {
        return infFlag == 1;
    }

    public CurveElement twice() {
        if (infFlag != 0)
            return this;

        if (y.isZero()) {
            infFlag = 1;
            return this;
        } else {
            twiceInternal();
            return this;
        }
    }

    public CurveElement setToZero() {
        infFlag = 1;

        return this;
    }

    public CurveElement setToOne() {
        infFlag = 1;

        return this;
    }

    public CurveElement setToRandom() {
        set(getField().getGenPow().pow(BigIntegerUtils.getRandom(field.getTargetField().getOrder(), field.getRandom())));

        return this;
    }

    public int setFromBytes(byte[] source, int offset) {
        int len;

        infFlag = 0;
        len = x.setFromBytes(source, offset);
        len += y.setFromBytes(source, offset + len);

        //if point does not lie on curve, set it to O
        if (!isValid())
            setToZero();

        return len;
    }

    public CurveElement square() {
        if (infFlag != 0) {
            infFlag = 1;
            return this;
        }

        if (y.isZero()) {
            infFlag = 1;
            return this;
        }

        twiceInternal();

        return this;
    }

    public CurveElement invert() {
        if (infFlag != 0) {
            infFlag = 1;
            return this;
        }

        infFlag = 0;
        y.negate();

        return this;
    }

    public CurveElement negate() {
        return invert();
    }

    public CurveElement add(Element e) {
        mul(e);

        return this;
    }

    public CurveElement mul(Element e) {
//        counter++;
        // Apply the Chord-Tangent Law of Composition
        // Consider P1 = this = (x1, y1);
        //          P2 = e = (x2, y2);

        if (infFlag != 0) {
            set(e);
            return this;
        }

        CurveElement element = (CurveElement) e;

        if (element.infFlag != 0)
            return this;

        if (x.isEqual(element.x)) {
            if (y.isEqual(element.y)) {
                if (y.isZero()) {
                    infFlag = 1;
                    return this;
                } else {
                    twiceInternal();
                    return this;
                }
            }

            infFlag = 1;
            return this;
        } else {
            // P1 != P2, so the slope of the line L through P1 and P2 is
            // lambda = (y2-y1)/(x2-x1)
            Element lambda = element.y.duplicate().sub(y).mul(element.x.duplicate().sub(x).invert());

            // x3 = lambda^2 - x1 - x2
            Element x3 = lambda.duplicate().square().sub(x).sub(element.x);

            //y3 = (x1-x3)lambda - y1
            Element y3 = x.duplicate().sub(x3).mul(lambda).sub(y);

            x.set(x3);
            y.set(y3);
            infFlag = 0;
        }

        return this;
    }

    public CurveElement mul(BigInteger n) {
        return (CurveElement) pow(n);
    }
    public CurveElement mul0(BigInteger n){

        String n2=n.toString(2);
        String [] ns=n2.split("");
        assert (n2.length()==ns.length);

        CurveElement q=field.newElement();
        q.getX().setToZero();
        q.getY().setToZero();
        q.setToZero();

        for (int j=0;j<ns.length;j++){
            q=q.twice();
            if(ns[j].equals("1"))
            {
                q=q.add(this);
            }
        }

        return q;
    }

    public CurveElement mulZn(Element e) {
        return powZn(e);
    }

    public boolean isSqr() {
        return BigIntegerUtils.isOdd(field.getOrder()) || duplicate().pow(field.getOrder().subtract(BigInteger.ONE).divide(BigIntegerUtils.TWO)).isOne();
    }

    public boolean isEqual(Element e) {
        if (e == this)
            return true;

        if (!(e instanceof CurveElement))
            return false;

        CurveElement element = (CurveElement) e;

        if (field.quotientCmp != null) {
            // If we're working with a quotient group we must account for different
            // representatives of the same coset.
            return this.duplicate().div(element).pow(field.quotientCmp).isOne();
        }

        return isEqual(element);
    }

    public CurveElement powZn(Element e) {
        pow(e.toBigInteger());
        return this;
    }

    public BigInteger toBigInteger() {
        if (isOne())
            return BigInteger.ZERO;
        else
            throw new IllegalStateException("Cannot convert to BigInteger.");
    }

    public byte[] toBytes() {
        byte[] xBytes = x.toBytes();
        byte[] yBytes = y.toBytes();

        byte[] result = new byte[xBytes.length + yBytes.length];
        System.arraycopy(xBytes, 0, result, 0, xBytes.length);
        System.arraycopy(yBytes, 0, result, xBytes.length, yBytes.length);

        return result;
    }

    public CurveElement setFromHash(byte[] source, int offset, int length) {
        infFlag = 0;
        x.setFromHash(source, offset, length);

        Element t = field.getTargetField().newElement();
        for (; ;) {
            t.set(x).square().add(field.a).mul(x).add(field.b);
            if (t.isSqr())
                break;

            x.square().add(t.setToOne());
        }
        y.set(t).sqrt();
        if (y.sign() < 0)
            y.negate();

        if (field.cofac != null)
            mul(field.cofac);

        return this;
    }

    public int sign() {
        if (infFlag != 0)
            return 0;

        return y.sign();
    }

    public String toString() {
        return String.format("%s,%s,%d", x, y, infFlag);
    }

    public int getLengthInBytesCompressed() {
        return x.getLengthInBytes() + 1;
    }

    public byte[] toBytesCompressed() {
        byte[] xBytes = x.toBytes();
        byte[] result = new byte[getLengthInBytesCompressed()];
        System.arraycopy(xBytes, 0, result, 0, xBytes.length);

        if (y.sign() > 0)
            result[xBytes.length] = 1;
        else
            result[xBytes.length] = 0;

        return result;
    }

    public int setFromBytesCompressed(byte[] source) {
        return setFromBytesCompressed(source, 0);
    }

    public int setFromBytesCompressed(byte[] source, int offset) {
        int len = x.setFromBytes(source, offset);
        setPointFromX();

        if (source[offset + len] == 1) {
            if (y.sign() < 0)
                y.negate();
        } else if (y.sign() > 0)
            y.negate();
        return len + 1;
    }

    public int getLengthInBytesX() {
        return x.getLengthInBytes();
    }

    public byte[] toBytesX() {
        return x.toBytes();
    }

    public int setFromBytesX(byte[] source) {
        return setFromBytesX(source, 0);
    }

    public int setFromBytesX(byte[] source, int offset) {
        int len = x.setFromBytes(source, offset);
        setPointFromX();
        return len;
    }


    public boolean isValid() {
        Element t0, t1;

        if (infFlag != 0)
            return true;

        t0 = field.getTargetField().newElement();
        t1 = field.getTargetField().newElement();
        t0.set(x).square().add(getField().getA()).mul(x).add(getField().getB());
        t1.set(y).square();

        return t0.isEqual(t1);
    }


    protected void twiceInternal() {
        // We have P1 = P2 so the tangent line T at P1 ha slope
        //lambda = (3x^2 + a) / 2y
        Element lambda = x.duplicate().square().mul(3).add(getField().a).mul(y.duplicate().twice().invert());

        // x3 = lambda^2 - 2x
        Element x3 = lambda.duplicate().square().sub(x.duplicate().twice());

        // y3 = (x - x3) lambda - y
        Element y3 = x.duplicate().sub(x3).mul(lambda).sub(y);

        x.set(x3);
        y.set(y3);
        infFlag = 0;
    }

    public void setPointFromX() {
        infFlag = 0;
        y.set(x.duplicate().square().add(field.a).mul(x).add(field.b).sqrt());
    }


    protected boolean isEqual(CurveElement element) {
        if (this.infFlag != 0 || element.infFlag != 0) {
            return (this.infFlag != 0 && element.infFlag != 0);
        }

        return x.isEqual(element.x) && y.isEqual(element.y);
    }


    public void setX(E x) {
        this.x = x;
    }

    public void setY(E y) {
        this.y = y;
    }


}
