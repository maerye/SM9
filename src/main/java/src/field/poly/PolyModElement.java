package src.field.poly;


import src.api.Element;
import src.api.Field;
import src.api.Polynomial;
import src.util.math.BigIntegerUtils;

import java.math.BigInteger;
import java.util.List;

/**
 * @author Angelo De Caro (jpbclib@gmail.com)
 */
public class PolyModElement<E extends Element> extends AbstractPolyElement<E, PolyModField> {


    public PolyModElement(PolyModField field) {
        super(field);

        for (int i = 0; i < field.n; i++)
            coefficients.add((E) field.getTargetField().newElement());
    }

    public PolyModElement(PolyModElement<E> source) {
        super(source.getField());

        for (int i = 0, n = source.getSize(); i < n; i++)
            coefficients.add((E) source.getCoefficient(i).duplicate());
    }


    @Override
    public Element getImmutable() {
        return new ImmutablePolyModElement<E>(this);
    }

    public PolyModField getField() {
        return field;
    }

    public PolyModElement<E> duplicate() {
        return new PolyModElement<E>(this);
    }

    public PolyModElement<E> set(Element e) {
        PolyModElement<E> element = (PolyModElement<E>) e;

        for (int i = 0; i < coefficients.size(); i++) {
            coefficients.get(i).set(element.coefficients.get(i));
        }

        return this;
    }

    public PolyModElement<E> set(int value) {
        coefficients.get(0).set(value);

        for (int i = 1; i < field.n; i++) {
            coefficients.get(i).setToZero();
        }

        return this;
    }

    public PolyModElement<E> set(BigInteger value) {
        coefficients.get(0).set(value);

        for (int i = 1; i < field.n; i++) {
            coefficients.get(i).setToZero();
        }

        return this;
    }

    public PolyModElement<E> setToRandom() {
        for (int i = 0; i < field.n; i++) {
            coefficients.get(i).setToRandom();
        }

        return this;
    }

    public PolyModElement<E> setFromHash(byte[] source, int offset, int length) {
        for (int i = 0; i < field.n; i++) {
            coefficients.get(i).setFromHash(source, offset, length);
        }

        return this;
    }

    public PolyModElement<E> setToZero() {
        for (int i = 0; i < field.n; i++) {
            coefficients.get(i).setToZero();
        }

        return this;
    }

    public boolean isZero() {
        for (int i = 0; i < field.n; i++) {
            if (!coefficients.get(i).isZero())
                return false;
        }
        return true;
    }

    public PolyModElement<E> setToOne() {
        coefficients.get(0).setToOne();

        for (int i = 1; i < field.n; i++) {
            coefficients.get(i).setToZero();
        }

        return this;
    }

    public boolean isOne() {
        if (!coefficients.get(0).isOne())
            return false;

        for (int i = 1; i < field.n; i++) {
            if (!coefficients.get(i).isZero())
                return false;
        }

        return true;
    }

    public PolyModElement<E> map(Element e) {
        coefficients.get(0).set(e);
        for (int i = 1; i < field.n; i++) {
            coefficients.get(i).setToZero();
        }

        return this;
    }

    public PolyModElement<E> twice() {
        for (int i = 0; i < field.n; i++) {
            coefficients.get(i).twice();
        }

        return this;
    }

    public PolyModElement<E> square() {
        switch (field.n) {
            case 3:
                PolyModElement<E> p0 = field.newElement();
                Element c0 = field.getTargetField().newElement();
                Element c2 = field.getTargetField().newElement();

                Element c3 = p0.coefficients.get(0);
                Element c1 = p0.coefficients.get(1);

                c3.set(coefficients.get(0)).mul(coefficients.get(1));
                c1.set(coefficients.get(0)).mul(coefficients.get(2));
                coefficients.get(0).square();

                c2.set(coefficients.get(1)).mul(coefficients.get(2));
                c0.set(coefficients.get(2)).square();
                coefficients.get(2).set(coefficients.get(1)).square();

                coefficients.get(1).set(c3).add(c3);

                c1.add(c1);

                coefficients.get(2).add(c1);
                p0.set(field.xpwr[1]);
                p0.polymodConstMul(c0);
                add(p0);

                c2.add(c2);
                p0.set(field.xpwr[0]);
                p0.polymodConstMul(c2);
                add(p0);

                return this;

            default:
                squareInternal();
        }
        return this;
    }

    public PolyModElement<E> invert() {
        setFromPolyTruncate(polyInvert(field.irreduciblePoly.getField().newElement().setFromPolyMod(this)));
        return this;
    }

    public PolyModElement<E> negate() {
        for (Element e : coefficients) {
            e.negate();
        }

        return this;
    }

    public PolyModElement<E> add(Element e) {
        PolyModElement<E> element = (PolyModElement<E>) e;

        for (int i = 0; i < field.n; i++) {
            coefficients.get(i).add(element.coefficients.get(i));
        }

        return this;
    }

    public PolyModElement<E> sub(Element e) {
        PolyModElement<E> element = (PolyModElement<E>) e;

        for (int i = 0; i < field.n; i++) {
            coefficients.get(i).sub(element.coefficients.get(i));
        }

        return this;
    }

    public PolyModElement<E> mul(Element e) {
        Polynomial<E> element = (Polynomial<E>) e;

        switch (field.n) {
            case 3:
                PolyModElement<E> p0 = field.newElement();
                Element c3 = field.getTargetField().newElement();
                Element c4 = field.getTargetField().newElement();

                kar_poly_2(coefficients, c3, c4, coefficients, element.getCoefficients(), p0.coefficients);

                p0.set(field.xpwr[0]).polymodConstMul(c3);
                add(p0);
                p0.set(field.xpwr[1]).polymodConstMul(c4);
                add(p0);

                return this;
//            case 6:
            // TODO: port the PBC code
//                throw new IllegalStateException("Not Implemented yet!");
/*
            mfptr p = res->field->data;
            element_t *dst = res->data, *s0, *s1 = e->data, *s2 = f->data;
            element_t *a0, *a1, *b0, *b1;
            element_t p0, p1, p2, p3;

            a0 = s1;
            a1 = &s1[3];
            b0 = s2;
            b1 = &s2[3];

            element_init(p0, res->field);
            element_init(p1, res->field);
            element_init(p2, res->field);
            element_init(p3, res->field);

            s0 = p0->data;
            s1 = p1->data;
            s2 = p2->data;
            element_add(s0[0], a0[0], a1[0]);
            element_add(s0[1], a0[1], a1[1]);
            element_add(s0[2], a0[2], a1[2]);

            element_add(s1[0], b0[0], b1[0]);
            element_add(s1[1], b0[1], b1[1]);
            element_add(s1[2], b0[2], b1[2]);

            kar_poly_2(s2, s2[3], s2[4], s0, s1, p3->data);
            kar_poly_2(s0, s0[3], s0[4], a0, b0, p3->data);
            kar_poly_2(s1, s1[3], s1[4], a1, b1, p3->data);

            element_set(dst[0], s0[0]);
            element_set(dst[1], s0[1]);
            element_set(dst[2], s0[2]);

            element_sub(dst[3], s0[3], s0[0]);
            element_sub(dst[3], dst[3], s1[0]);
            element_add(dst[3], dst[3], s2[0]);

            element_sub(dst[4], s0[4], s0[1]);
            element_sub(dst[4], dst[4], s1[1]);
            element_add(dst[4], dst[4], s2[1]);

            element_sub(dst[5], s2[2], s0[2]);
            element_sub(dst[5], dst[5], s1[2]);

            // Start reusing part of s0 as scratch space(!)
            element_sub(s0[0], s2[3], s0[3]);
            element_sub(s0[0], s0[0], s1[3]);
            element_add(s0[0], s0[0], s1[0]);

            element_sub(s0[1], s2[4], s0[4]);
            element_sub(s0[1], s0[1], s1[4]);
            element_add(s0[1], s0[1], s1[1]);

            polymod_const_mul(p3, s0[0], p->xpwr[0]);
            element_add(res, res, p3);
            polymod_const_mul(p3, s0[1], p->xpwr[1]);
            element_add(res, res, p3);
            polymod_const_mul(p3, s1[2], p->xpwr[2]);
            element_add(res, res, p3);
            polymod_const_mul(p3, s1[3], p->xpwr[3]);
            element_add(res, res, p3);
            polymod_const_mul(p3, s1[4], p->xpwr[4]);
            element_add(res, res, p3);

            element_clear(p0);
            element_clear(p1);
            element_clear(p2);
            element_clear(p3);
*/
            default:

                Element[] high = new Element[field.n - 1];
                for (int i = 0, size = field.n - 1; i < size; i++) {
                    high[i] = field.getTargetField().newElement().setToZero();
                }
                PolyModElement<E> prod = field.newElement();
                p0 = field.newElement();
                Element c0 = field.getTargetField().newElement();

                for (int i = 0; i < field.n; i++) {
                    int ni = field.n - i;

                    int j = 0;
                    for (; j < ni; j++) {
                        c0.set(coefficients.get(i)).mul(element.getCoefficient(j));
                        prod.coefficients.get(i + j).add(c0);
                    }
                    for (; j < field.n; j++) {
                        c0.set(coefficients.get(i)).mul(element.getCoefficient(j));
                        high[j - ni].add(c0);
                    }
                }

                for (int i = 0, size = field.n - 1; i < size; i++) {
                    p0.set(field.xpwr[i]).polymodConstMul(high[i]);
                    prod.add(p0);
                }

                set(prod);

                return this;
        }
    }

    public PolyModElement<E> mul(int z) {
        for (int i = 0; i < field.n; i++) {
            coefficients.get(i).mul(z);
        }

        return this;
    }

    public PolyModElement<E> mul(BigInteger n) {
        for (int i = 0; i < field.n; i++) {
            coefficients.get(i).mul(n);
        }

        return this;
    }

    public PolyModElement<E> powZn(Element e) {
        // TODO: port the PBC code
        return (PolyModElement<E>) pow(e.toBigInteger());
    }

    public PolyModElement<E> sqrt() {
        PolyField polyField = new PolyField(field.getRandom(), field);

        PolyElement f = polyField.newElement();
        PolyElement r = polyField.newElement();
        PolyElement s = polyField.newElement();
        Element e0 = field.newElement();

        f.ensureSize(3);
        f.getCoefficient(2).setToOne();
        f.getCoefficient(0).set(this).negate();

        BigInteger z = field.getOrder().subtract(BigInteger.ONE).divide(BigIntegerUtils.TWO);

        while (true) {
            int i;
            Element x;
            Element e1, e2;

            r.ensureSize(2);
            r.getCoefficient(1).setToOne();
            x = r.getCoefficient(0);
            x.setToRandom();
            e0.set(x).mul(x);

            if (e0.isEqual(this)) {
                set(x);
                break;
            }

            s.setToOne();
            for (i = z.bitLength() - 1; i >= 0; i--) {
                s.mul(s);

                if (s.getDegree() == 2) {
                    e1 = s.getCoefficient(0);
                    e2 = s.getCoefficient(2);
                    e0.set(e2).mul(this);
                    e1.add(e0);
                    s.ensureSize(2);
                    s.removeLeadingZeroes();
                }

                if (z.testBit(i)) {
                    s.mul(r);
                    if (s.getDegree() == 2) {
                        e1 = s.getCoefficient(0);
                        e2 = s.getCoefficient(2);
                        e0.set(e2).mul(this);
                        e1.add(e0);
                        s.ensureSize(2);
                        s.removeLeadingZeroes();
                    }
                }
            }

            if (s.getDegree() < 1)
                continue;

            e0.setToOne();
            e1 = s.getCoefficient(0);
            e2 = s.getCoefficient(1);
            e1.add(e0);
            e0.set(e2).invert();
            e0.mul(e1);
            e2.set(e0).mul(e0);

            if (e2.isEqual(this)) {
                set(e0);
                break;
            }
        }

        return this;
    }

    public boolean isSqr() {
        BigInteger z = field.getOrder().subtract(BigInteger.ONE).divide(BigIntegerUtils.TWO);
        return field.newElement().set(this).pow(z).isOne();
    }

    public int sign() {
        int res = 0;
        for (int i = 0, size = coefficients.size(); i < size; i++) {
            res = coefficients.get(i).sign();
            if (res != 0)
                break;
        }
        return res;
    }

    public boolean isEqual(Element e) {
        if (e == this)
            return true;

        if (!(e instanceof PolyModElement))
            return false;

        PolyModElement<E> element = (PolyModElement<E>) e;

        for (int i = 0; i < field.n; i++) {
            if (!coefficients.get(i).isEqual(element.coefficients.get(i)))
                return false;
        }

        return true;
    }

    public int setFromBytes(byte[] source) {
        return setFromBytes(source, 0);
    }

    public int setFromBytes(byte[] source, int offset) {
        int len = offset;
        for (int i = 0, size = coefficients.size(); i < size; i++) {
            len += coefficients.get(i).setFromBytes(source, len);
        }
        return len - offset;
    }

    public byte[] toBytes() {
        byte[] buffer = new byte[field.getLengthInBytes()];
        int targetLB = field.getTargetField().getLengthInBytes();

        for (int len = 0, i = 0, size = coefficients.size(); i < size; i++, len += targetLB) {
            byte[] temp = coefficients.get(i).toBytes();
            System.arraycopy(temp, 0, buffer, len, targetLB);
        }
        return buffer;
    }

    public BigInteger toBigInteger() {
        return coefficients.get(0).toBigInteger();
    }


    public String toString() {
        StringBuilder buffer = new StringBuilder("[");
        for (Element e : coefficients) {
            buffer.append(e).append(", ");
        }
        buffer.append("]");
        return buffer.toString();
    }


    public PolyModElement<E> setFromPolyTruncate(PolyElement<E> element) {
        int n = element.getCoefficients().size();
        if (n > field.n)
            n = field.n;

        int i = 0;
        for (; i < n; i++) {
            coefficients.get(i).set(element.getCoefficients().get(i));
        }

        for (; i < field.n; i++) {
            coefficients.get(i).setToZero();
        }

        return this;
    }

    public PolyModElement<E> polymodConstMul(Element e) {
        //a lies in R, e in R[x]
        for (int i = 0, n = coefficients.size(); i < n; i++) {
            coefficients.get(i).mul(e);
        }

        return this;
    }


    protected void squareInternal() {
        List<E> dst;
        List<E> src = coefficients;

        int n = field.n;

        PolyModElement<E> prod, p0;
        Element c0;
        int i, j;

        Element high[] = new Element[n - 1];

        for (i = 0; i < n - 1; i++) {
            high[i] = field.getTargetField().newElement().setToZero();
        }

        prod = field.newElement();
        dst = prod.coefficients;
        p0 = field.newElement();
        c0 = field.getTargetField().newElement();

        for (i = 0; i < n; i++) {
            int twicei = 2 * i;

            c0.set(src.get(i)).square();

            if (twicei < n) {
                dst.get(twicei).add(c0);
            } else {
                high[twicei - n].add(c0);
            }

            for (j = i + 1; j < n - i; j++) {
                c0.set(src.get(i)).mul(src.get(j));
                c0.add(c0);
                dst.get(i + j).add(c0);
            }

            for (; j < n; j++) {
                c0.set(src.get(i)).mul(src.get(j));
                c0.add(c0);
                high[i + j - n].add(c0);
            }
        }

        for (i = 0; i < n - 1; i++) {
            p0.set(field.xpwr[i]).polymodConstMul(high[i]);
            prod.add(p0);
        }

        set(prod);
    }

    /**
     * Karatsuba for degree 2 polynomials
     *
     * @param dst
     * @param c3
     * @param c4
     * @param s1
     * @param s2
     * @param scratch
     */
    protected void kar_poly_2(List<E> dst, Element c3, Element c4, List<E> s1, List<E> s2, List<E> scratch) {
        Element c01, c02, c12;

        c12 = scratch.get(0);
        c02 = scratch.get(1);
        c01 = scratch.get(2);

        c3.set(s1.get(0)).add(s1.get(1));
        c4.set(s2.get(0)).add(s2.get(1));
        c01.set(c3).mul(c4);
        c3.set(s1.get(0)).add(s1.get(2));
        c4.set(s2.get(0)).add(s2.get(2));
        c02.set(c3).mul(c4);
        c3.set(s1.get(1)).add(s1.get(2));
        c4.set(s2.get(1)).add(s2.get(2));
        c12.set(c3).mul(c4);
        dst.get(1).set(s1.get(1)).mul(s2.get(1));

        //constant term
        dst.get(0).set(s1.get(0)).mul(s2.get(0));

        //coefficient of x^4
        c4.set(s1.get(2)).mul(s2.get(2));

        //coefficient of x^3
        c3.set(dst.get(1)).add(c4);
        c3.set(c12.duplicate().sub(c3));

        //coefficient of x^2
        dst.get(2).set(c4).add(dst.get(0));
        c02.sub(dst.get(2));
        dst.get(2).set(dst.get(1)).add(c02);

        //coefficient of x
        c01.sub(dst.get(0));
        dst.set(1, (E) c01.duplicate().sub(dst.get(1)));
    }

    protected PolyElement polyInvert(PolyElement f) {
        PolyField<Field> polyField = f.getField();

        PolyElement q = polyField.newElement();

        PolyElement b0 = polyField.newZeroElement();
        PolyElement b1 = polyField.newOneElement();
        PolyElement b2 = polyField.newElement();

        PolyElement r0 = field.irreduciblePoly.duplicate();
        PolyElement r1 = f.duplicate();
        PolyElement r2 = polyField.newElement();

        Element inv = f.getField().getTargetField().newElement();

        while (true) {
            PolyUtils.div(q, r2, r0, r1);
            if (r2.isZero())
                break;

            b2.set(b1).mul(q);
            b2.set(b0.duplicate().sub(b2));

            b0.set(b1);
            b1.set(b2);

            r0.set(r1);
            r1.set(r2);
        }

        inv.set(r1.getCoefficient(0)).invert();
        return PolyUtils.constMul(inv, b1);
    }

}