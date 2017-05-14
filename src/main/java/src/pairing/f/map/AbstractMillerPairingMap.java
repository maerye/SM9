package src.pairing.f.map;


import src.api.*;
import src.util.io.PairingStreamReader;
import src.util.io.PairingStreamWriter;

import java.io.IOException;
import java.math.BigInteger;

/**
 * @author Angelo De Caro (jpbclib@gmail.com)
 */
public abstract class AbstractMillerPairingMap<E extends Element> extends AbstractPairingMap {


    protected AbstractMillerPairingMap(final Pairing pairing) {
        super(pairing);
    }


    protected final void lineStep(final Point<E> f0,
                                  final Element a, final Element b, final Element c,
                                  final Element Vx, final Element Vy,
                                  final Element V1x, final Element V1y,
                                  final Element e0,
                                  final E Qx, final E Qy,
                                  final Element f) {
        // computeLine(a, b, c, Vx, Vy, V1x, V1y, e0);
        // a = -(V1y - Vy) / (V1x - Vx);
        // b = 1;
        // c = -(Vy + a * Vx);
        //
        // but we will multiply by V1x - Vx to avoid division, so
        //
        // a = -(V1y - Vy)
        // b = V1x - Vx
        // c = -(Vy b + a Vx);

        a.set(Vy).sub(V1y);
        b.set(V1x).sub(Vx);
        c.set(Vx).mul(V1y).sub(e0.set(Vy).mul(V1x));

        millerStep(f0, a, b, c, Qx, Qy);
        f.mul(f0);
    }

    protected final void lineStep(final Point<E> f0,
                                  final Element a, final Element b, final Element c,
                                  final Element[] Vs,
                                  final Element[] V1s,
                                  final Element e0,
                                  final Element[] Qs,
                                  final Element f) {
        // computeLine(a, b, c, Vx, Vy, V1x, V1y, e0);
        // a = -(V1y - Vy) / (V1x - Vx);
        // b = 1;
        // c = -(Vy + a * Vx);
        //
        // but we will multiply by V1x - Vx to avoid division, so
        //
        // a = -(V1y - Vy)
        // b = V1x - Vx
        // c = -(Vy b + a Vx);

        for (int i = 0; i < Vs.length; i++) {
            Point V = (Point) Vs[i];
            Point V1 = (Point) V1s[i];
            Point Q = (Point) Qs[i];

            a.set(V.getY()).sub(V1.getY());
            b.set(V1.getX()).sub(V.getX());
            c.set(V.getX()).mul(V1.getY()).sub(e0.set(V.getY()).mul(V1.getX()));

            millerStep(f0, a, b, c, (E) Q.getX(), (E) Q.getY());
            f.mul(f0);
        }
    }

    protected final void tangentStep(final Point<E> f0,
                                     final Element a, final Element b, final Element c,
                                     final Element Vx, final Element Vy,
                                     final Element curveA,
                                     final Element e0,
                                     final E Qx, final E Qy,
                                     final Element f) {
        //computeTangent(a, b, c, Vx, Vy, curveA, e0);
        //a = -slope_tangent(V.x, V.y);
        //b = 1;
        //c = -(V.y + aV.x);
        //but we multiply by -2*V.y to avoid division so:
        //a = -(3 Vx^2 + cc->a)
        //b = 2 * Vy
        //c = -(2 Vy^2 + a Vx);

        a.set(Vx).square().mul(3).add(curveA).negate();
        b.set(Vy).twice();
        c.set(a).mul(Vx).add(e0.set(b).mul(Vy)).negate();

        millerStep(f0, a, b, c, Qx, Qy);
        f.mul(f0);
    }

    protected final void tangentStep(final Point<E> f0,
                                     final Element a, final Element b, final Element c,
                                     final Element[] Vs,
                                     final Element curveA,
                                     final Element e0,
                                     final Element[] Qs,
                                     final Element f) {
        //computeTangent(a, b, c, Vx, Vy, curveA, e0);
        //a = -slope_tangent(V.x, V.y);
        //b = 1;
        //c = -(V.y + aV.x);
        //but we multiply by -2*V.y to avoid division so:
        //a = -(3 Vx^2 + cc->a)
        //b = 2 * Vy
        //c = -(2 Vy^2 + a Vx);

        for (int i = 0; i < Vs.length; i++) {
            Point V = (Point) Vs[i];
            Point Q = (Point) Qs[i];

            a.set(V.getX()).square().mul(3).add(curveA).negate();
            b.set(V.getY()).twice();
            c.set(a).mul(V.getX()).add(e0.set(b).mul(V.getY())).negate();

            millerStep(f0, a, b, c, (E) Q.getX(), (E) Q.getY());
            f.mul(f0);
        }
    }

    protected final void tangentStepProjective(final Point<E> f0,
                                               final Element a, final Element b,final  Element c,
                                               final Element Vx, final Element Vy, final Element z,
                                               final Element z2,
                                               final Element e0,
                                               final E Qx, final E Qy,
                                               final Element f) {
        // Compute the tangent line T (aX + bY + c) at point V = (Vx, Vy, z)

        a.set(z2).square();
        b.set(Vx).square();
        a.add(b.add(e0.set(b).twice())).negate();
        // Now:
        // a = -(3x^2 + cca z^4)     with cca = 1

        b.set(e0.set(Vy).twice()).mul(z2).mul(z);
        // Now:
        // b = 2 y z^3

        c.set(Vx).mul(a);
        a.mul(z2);
        c.add(e0.mul(Vy)).negate();

        // Now:
        // a = -3x^2 z^2 - z^6
        // c = 3x^3 + z^4 x - 2x^2 y

        millerStep(f0, a, b, c, Qx, Qy);
        f.mul(f0);
    }

    protected abstract void millerStep(Point<E> out,
                                       Element a, Element b, Element c,
                                       E Qx, E Qy);


    protected final void computeTangent(final Element a, final Element b, final Element c,
                                        final Element Vx, final Element Vy,
                                        final Element curveA,
                                        final Element temp) {
        //a = -slope_tangent(V.x, V.y);
        //b = 1;
        //c = -(V.y + aV.x);
        //but we multiply by -2*V.y to avoid division so:
        //a = -(3 Vx^2 + cc->a)
        //b = 2 * Vy
        //c = -(2 Vy^2 + a Vx);

        a.set(Vx).square().mul(3).add(curveA).negate();
        b.set(Vy).twice();
        c.set(a).mul(Vx).add(temp.set(b).mul(Vy)).negate();
    }

    protected final void computeTangent(final MillerPreProcessingInfo info,
                                        final Element a, final Element b, final Element c,
                                        final Element Vx, final Element Vy,
                                        final Element curveA,
                                        final Element temp) {
        //a = -slope_tangent(Z.x, Z.y);
        //b = 1;
        //c = -(Z.y + a * Z.x);
        //but we multiply by 2*Z.y to avoid division

        //a = -Vx * (3 Vx + twicea_2) - a_4;
        //Common curves: a2 = 0 (and cc->a is a_4), so
        //a = -(3 Vx^2 + cc->a)
        //b = 2 * Vy
        //c = -(2 Vy^2 + a Vx);

        a.set(Vx).square();
//        a.add(a).add(a);
        a.mul(3);
        a.add(curveA);
        a.negate();

        b.set(Vy).twice();

        temp.set(b).mul(Vy);
        c.set(a).mul(Vx);
        c.add(temp).negate();

        info.addRow(a, b, c);
    }

    /**
     * Compute the tangent line L (aX + bY + c) through the points V = (Vx, Vy) e V1 = (V1x, V1y).
     *
     * @param a    the coefficient of X of tangent line T.
     * @param b    the coefficient of Y of tangent line T.
     * @param c    the constant term f tangent line T.
     * @param Vx   V's x.
     * @param Vy   V's y.
     * @param V1x  V1's x.
     * @param V1y  V1's y.
     * @param temp temp element.
     */
    protected final void computeLine(final Element a, final Element b, final Element c,
                                     final Element Vx, final Element Vy,
                                     final Element V1x, final Element V1y,
                                     final Element temp) {

        // a = -(V1y - Vy) / (V1x - Vx);
        // b = 1;
        // c = -(Vy + a * Vx);
        //
        // but we will multiply by V1x - Vx to avoid division, so
        //
        // a = -(V1y - Vy)
        // b = V1x - Vx
        // c = -(Vy b + a Vx);

        a.set(Vy).sub(V1y);
        b.set(V1x).sub(Vx);
        c.set(Vx).mul(V1y).sub(temp.set(Vy).mul(V1x));
    }

    protected final void computeLine(final MillerPreProcessingInfo info,
                                     final Element a, final Element b, final Element c,
                                     final Element Vx, final Element Vy,
                                     final Element V1x, final Element V1y,
                                     final Element temp) {
        // a = -(V1y - Vy) / (V1x - Vx);
        // b = 1;
        // c = -(Vy + a * Vx);
        //
        // but we will multiply by V1x - Vx to avoid division, so
        //
        // a = -(V1y - Vy)
        // b = V1x - Vx
        // c = -(Vy b + a Vx);

        a.set(Vy).sub(V1y);
        b.set(V1x).sub(Vx);
        c.set(Vx).mul(V1y).sub(temp.set(Vy).mul(V1x));

        info.addRow(a, b, c);
    }


    protected final Element lucasEven(final Point in, final BigInteger cofactor) {
        //assumes cofactor is even
        //mangles in
        //in cannot be out
        if (in.isOne()) {
            return in.duplicate();
        }

        Point out = (Point) in.getField().newElement();
        Point temp = (Point) in.getField().newElement();

        Element in0 = in.getX();
        Element in1 = in.getY();

        Element v0 = out.getX();
        Element v1 = out.getY();

        Element t0 = temp.getX();
        Element t1 = temp.getY();

        t0.set(2);
        t1.set(in0).twice();
        v0.set(t0);
        v1.set(t1);

        int j = cofactor.bitLength() - 1;
        while (true) {
            if (j == 0) {
                v1.mul(v0).sub(t1);
                v0.square().sub(t0);
                break;
            }

            if (cofactor.testBit(j)) {
                v0.mul(v1).sub(t1);
                v1.square().sub(t0);
            } else {
                v1.mul(v0).sub(t1);
                v0.square().sub(t0);
            }

            j--;
        }

        v0.twice();
        in0.set(t1).mul(v1).sub(v0);

        t1.square().sub(t0).sub(t0);

        v0.set(v1).halve();
        v1.set(in0).div(t1);
        v1.mul(in1);

        return out;
    }

    protected final void lucasOdd(final Point out, final Point in, final Point temp, final BigInteger cofactor) {
        //assumes cofactor is odd
        //overwrites in and temp, out must not be in
        //luckily this touchy routine is only used internally
        //TODO: rewrite to allow (out == in)? would simplify a_finalpow()

        Element in0 = in.getX();
        Element in1 = in.getY();

        Element v0 = out.getX();
        Element v1 = out.getY();

        Element t0 = temp.getX();
        Element t1 = temp.getY();

        t0.set(2);
        t1.set(in0).twice();

        v0.set(t0);
        v1.set(t1);

        int j = cofactor.bitLength() - 1;
        for (; ; ) {
            if (j == 0) {
                v1.mul(v0).sub(t1);
                v0.square().sub(t0);

                break;
            }

            if (cofactor.testBit(j)) {
                v0.mul(v1).sub(t1);
                v1.square().sub(t0);

            } else {
                v1.mul(v0).sub(t1);
                v0.square().sub(t0);
            }
            j--;
        }

        v1.twice().sub(in0.set(v0).mul(t1));

        t1.square().sub(t0).sub(t0);
        v1.div(t1);

        v0.halve();
        v1.mul(in1);
    }


    public static class MillerPreProcessingInfo {
        public int numRow = 0;
        public final Element[][] table;

        public MillerPreProcessingInfo(int size) {
            this.table = new Element[size][3];
        }

        public MillerPreProcessingInfo(Pairing pairing, byte[] source, int offset) {
            PairingStreamReader in = new PairingStreamReader(pairing, source, offset);

            this.numRow = in.readInt();
            this.table = new Element[numRow][3];
            Field field = ((FieldOver) pairing.getG1()).getTargetField();
            for (int i = 0; i < numRow; i++) {
                table[i][0] = in.readFieldElement(field);
                table[i][1] = in.readFieldElement(field);
                table[i][2] = in.readFieldElement(field);
            }
        }

        public void addRow(Element a, Element b, Element c) {
            table[numRow][0] = a.duplicate();
            table[numRow][1] = b.duplicate();
            table[numRow][2] = c.duplicate();

            numRow++;
        }

        public byte[] toBytes() {
            try {
                PairingStreamWriter out = new PairingStreamWriter(table[0][0].getField().getLengthInBytes() * numRow * 3 + 4);

                out.writeInt(numRow);
                for (int i = 0; i < numRow; i++)  {
                    for (Element element : table[i])
                        out.write(element);
                }
                return out.toBytes();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static class JacobPoint {

        private Element x;
        private Element y;
        private Element z;

        public JacobPoint(Element x, Element y, Element z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public Element getX() {
            return this.x;
        }

        public Element getY() {
            return this.y;
        }

        public Element getZ() {
            return this.z;
        }

        public boolean isInfinity() {
            //return this.equals(JacobPoint.INFINITY);
            return this.z.isZero();
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((x == null) ? 0 : x.hashCode());
            result = prime * result + ((y == null) ? 0 : y.hashCode());
            result = prime * result + ((z == null) ? 0 : z.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            JacobPoint other = (JacobPoint) obj;
            if (x == null) {
                if (other.x != null)
                    return false;
            } else if (!x.equals(other.x))
                return false;
            if (y == null) {
                if (other.y != null)
                    return false;
            } else if (!y.equals(other.y))
                return false;
            if (z == null) {
                if (other.z != null)
                    return false;
            } else if (!z.equals(other.z))
                return false;
            return true;
        }

        @Override
        public String toString() {
            return "[" + x + "," + y + "," + z + "]";
        }

        public void setX(Element newX) {
            this.x = newX;
        }

        public void setY(Element newY) {
            this.y = newY;
        }

        public void setZ(Element newZ) {
            this.z = newZ;
        }


    }

}
