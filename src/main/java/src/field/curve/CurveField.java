package src.field.curve;

import src.api.Element;
import src.api.ElementPow;
import src.api.Field;
import src.field.base.AbstractFieldOver;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * @author Angelo De Caro (jpbclib@gmail.com)
 */
public class CurveField<F extends Field> extends AbstractFieldOver<F, CurveElement> {

    public static <F extends Field> CurveField<F> newCurveFieldJ(SecureRandom random, Element j, BigInteger order, BigInteger cofac) {
        // Assumes j != 0, 1728

        Element a, b;

        a = j.getField().newElement();
        b = j.getField().newElement();

        a.set(1728).sub(j).invert().mul(j);

        //b = 2 j / (1728 - j)
        b.set(a).add(a);
        //a = 3 j / (1728 - j)
        a.add(b);

        return new CurveField<F>(random, a, b, order, cofac);
    }

    protected Element a, b;
    protected Element gen, genNoCofac;
    protected ElementPow genPow;
    protected BigInteger order, cofac;

    // A non-NULL quotientCmp means we are working with the quotient group of
    // order #E / quotientCmp, and the points are actually coset
    // representatives. Thus for a comparison, we must multiply by quotientCmp
    // before comparing.
    protected BigInteger quotientCmp = null;


    public CurveField(SecureRandom random, Element a, Element b, BigInteger order) {
        this(random, a, b, order, (BigInteger) null);
    }

    public CurveField(SecureRandom random, Element a, Element b, BigInteger order, byte[] gen) {
        super(random, (F) a.getField());

        this.a = a;
        this.b = b;
        this.order = order;
        this.gen = newElementFromBytes(gen);
    }

    public CurveField(SecureRandom random, Element a, Element b, BigInteger order, BigInteger cofac) {
        super(random, (F) a.getField());

        this.a = a;
        this.b = b;
        this.order = order;
        this.cofac = cofac;

        initGen();
    }

    public CurveField(SecureRandom random, Element a, Element b, BigInteger order, BigInteger cofac, byte[] genNoCofac) {
        super(random, (F) a.getField());

        this.random = random;
        this.a = a;
        this.b = b;
        this.order = order;
        this.cofac = cofac;

        initGen(genNoCofac);
    }

    public CurveField(SecureRandom random, Element b, BigInteger order, BigInteger cofac) {
        this(random, b.getField().newZeroElement(), b, order, cofac);
    }



    public CurveElement newElement() {
        return new CurveElement(this);
    }

    public BigInteger getOrder() {
        return order;
    }

    public CurveElement getNqr() {
        throw new IllegalStateException("Not Implemented yet!");
    }

    public int getLengthInBytes() {
        return getTargetField().getLengthInBytes() * 2;
    }


    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CurveField)) return false;

        CurveField that = (CurveField) o;

        if (!a.equals(that.a)) return false;
        if (!b.equals(that.b)) return false;
        if (cofac != null ? !cofac.equals(that.cofac) : that.cofac != null) return false;
        if (!order.equals(that.order)) return false;

        return true;
    }

    public int hashCode() {
        int result = a.hashCode();
        result = 31 * result + b.hashCode();
        result = 31 * result + order.hashCode();
        result = 31 * result + (cofac != null ? cofac.hashCode() : 0);
        return result;
    }


    public Element getA() {
        return a;
    }

    public Element getB() {
        return b;
    }

    public BigInteger getQuotientCmp() {
        return quotientCmp;
    }

    public void setQuotientCmp(BigInteger quotientCmp) {
        this.quotientCmp = quotientCmp;
    }

    /**
     * Existing points are invalidated as this mangles c.
     * Consider the curve E′ given by Y^2 = X^3 + a v^2 X + v^3 b, which
     * we call the (quadratic) twist of the curve E, where
     * v is a quadratic nonresidue in Fq
     *
     * @return the twisted curve.
     */
    public CurveField twist() {
        Element nqr = getTargetField().getNqr();

        a.mul(nqr).mul(nqr);
        b.mul(nqr).mul(nqr).mul(nqr);

        initGen();

        return this;
    }

    public Element getGenNoCofac() {
        return genNoCofac;
    }

    public Element getGen() {
        return gen;
    }

    protected void initGen() {
        genNoCofac = getCurveRandomNoCofacSolvefory();

        if (cofac != null) {
            gen = genNoCofac.duplicate().mul(cofac);
        } else {
            gen = genNoCofac.duplicate();
        }
    }

    protected void initGen(byte[] genNoCofac) {
        if (genNoCofac == null) {
            this.genNoCofac = getCurveRandomNoCofacSolvefory();
        } else {
            this.genNoCofac = newElementFromBytes(genNoCofac);
        }

        if (cofac != null) {
            gen = this.genNoCofac.duplicate().mul(cofac);
        } else {
            gen = this.genNoCofac.duplicate();
        }
    }

    protected CurveElement getCurveRandomNoCofacSolvefory() {
        //TODO(-): with 0.5 probability negate y-coord

        CurveElement element = new CurveElement(this);
        element.infFlag = 0;

        Element t = targetField.newElement();
        do {
            t.set(element.getX().setToRandom()).square().add(a).mul(element.getX()).add(b);
        } while (!t.isSqr());

        element.getY().set(t.sqrt());
        return element;
    }

    public Element[] twice(Element[] elements) {
        int i;
        int n = elements.length;

        Element[] table = new Element[n];  //a big problem?
        Element e0, e1, e2;
        CurveElement q, r;

        q = (CurveElement) elements[0];
        e0 = q.getX().getField().newElement();
        e1 = e0.duplicate();
        e2 = e0.duplicate();

        for (i = 0; i < n; i++) {
            q = (CurveElement) elements[i];
            table[i] = q.getY().getField().newElement();

            if (q.infFlag != 0) {
                q.infFlag = 1;
                continue;
            }

            if (q.getY().isZero()) {
                q.infFlag = 1;
                continue;
            }
        }

        //to compute 1/2y multi. see Cohen's GTM139 Algorithm 10.3.4
        for (i = 0; i < n; i++) {
            q = (CurveElement) elements[i];
            table[i].set(q.getY()).twice();
            if (i > 0)
                table[i].mul(table[i - 1]);
        }
        e2.set(table[n - 1]).invert();
        for (i = n - 1; i > 0; i--) {
            q = (CurveElement) elements[i];
            table[i].set(table[i - 1]).mul(e2);
            e2.mul(q.getY()).twice(); //e2=e2*2y_j
        }
        table[0].set(e2); //e2 no longer used.

        for (i = 0; i < n; i++) {
            q = (CurveElement) elements[i];
            if (q.infFlag != 0)
                continue;

            //e2=lambda = (3x^2 + a) / 2y
            e2.set(q.getX()).square().mul(3).add(a).mul(table[i]); //Recall that table[i]=1/2y_i

            //x1 = lambda^2 - 2x
            e1.set(q.getX()).twice();
            e0.set(e2).square().sub(e1);

            //y1 = (x - x1)lambda - y
            e1.set(q.getX()).sub(e0).mul(e2).sub(q.getY());

            q.getX().set(e0);
            q.getY().set(e1);
            q.infFlag = 0;
        }

        return elements;
    }


    public Element[] add(Element[] a, Element[] b) {
        int n = a.length;
        Element[] table = new Element[n];
        CurveElement p, q, r;
        Element e0, e1, e2;

        p = (CurveElement) a[0];
        q = (CurveElement) b[0];

        e0 = p.getX().getField().newElement();
        e1 = e0.duplicate();
        e2 = e0.duplicate();

        table[0] = q.getX().duplicate().sub(p.getX());
        for (int i = 1; i < n; i++) {
            p = (CurveElement) a[i];
            q = (CurveElement) b[i];

            table[i] = q.getX().duplicate().sub(p.getX()).mul(table[i-1]);
        }
        e2.set(table[n-1]).invert();
        for (int i = n - 1; i > 0; i--) {
            p = (CurveElement) a[i];
            q = (CurveElement) b[i];

            table[i].set(table[i-1]).mul(e2);
            e1.set(q.getX()).sub(p.getX());
            e2.mul(e1); //e2=e2*(x2_j-x1_j)
        }
        table[0].set(e2);  //e2 no longer used.

        for (int i = 0; i < n; i++) {
            p = (CurveElement) a[i];
            q = (CurveElement) b[i];
            if (p.infFlag != 0) {
                a[i].set(b[i]);
                continue;
            }
            if (q.infFlag != 0) {
                continue;
            }

            if (p.getX().isEqual(q.getX())) { //a[i]=b[i]
                if (p.getY().isEqual(q.getY())) {
                    if (p.getY().isZero()) {
                        p.infFlag = 1;
                        continue;
                    } else {
                        p.twice();
                        continue;
                    }
                }
                //points are inverses of each other
                p.infFlag = 1;
                continue;
            } else {
                //lambda = (y2-y1)/(x2-x1)
                e2.set(q.getY()).sub(p.getY()).mul(table[i]);

                //x3 = lambda^2 - x1 - x2
                e0.set(e2).square().sub(p.getX()).sub(q.getX());

                //y3 = (x1-x3)lambda - y1
                e1.set(p.getX()).sub(e0).mul(e2).sub(p.getY());

                p.getX().set(e0);
                p.getY().set(e1);
                p.infFlag = 0;
            }
        }

        return a;
    }

    public ElementPow getGenPow() {
        if (genPow == null)
            genPow = gen.getElementPowPreProcessing();
        return genPow;
    }

}