/**
 * BNPairing.java
 *
 * Bilinear pairings over Barreto-Naehrig (BN) elliptic curves.
 *
 * Copyright (C) Paulo S. L. M. Barreto, Michael Naehrig and Peter Schwabe.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.bouncycastle.math.myec.bncurves;

import java.math.BigInteger;

public class BNPairing {

    /**
     * Convenient BigInteger constants
     */
    private static final BigInteger
        _0 = BigInteger.valueOf(0L),
        _1 = BigInteger.valueOf(1L),
        _3 = BigInteger.valueOf(3L);

    public static final String incompatibleCurves =
        "Cannot compute pairings of points from incompatible elliptic curves";

    public BNCurve E;
    public BNCurve2 E2;
    public BNParams bn;

    public BNField12 Fp12_0, Fp12_1;

    /**
     * Powers of the base element g by simple multiples of powers of 16.
     */
    protected BNField12[][] gp16 = null;

    public BNPairing(BNCurve2 Et) {
        E2 = Et;
        E = Et.E;
        bn = E.bn;
        Fp12_0 = new BNField12(bn, _0);
        Fp12_1 = new BNField12(bn, _1);
        gp16 = new BNField12[(bn.n.bitLength() + 3)/4][16];
    }

    BNField12 gl(BNPoint V, BNPoint P, BNPoint A, BNPoint2 Q) {
        BigInteger n, d;
        BigInteger p = bn.p;
        if (V.isZero() || P.isZero() || Q.isZero() || V.opposite(P)) {
            return Fp12_1;
        }
        BigInteger Vz3 = V.z.multiply(V.z).multiply(V.z).mod(p);
        if (A != null) {
            n = A.m;
            d = A.z;
        } else if (V.equals(P)) {
            // y = Y/Z^3 => 1/2y = Z^3/2Y
            // x = X/Z^2 => 3x^2 = 3X^2/Z^4 =>
            // => lambda = 3x^2/2y = 3X^2/(2Y*Z)
            n = V.x.multiply(V.x).multiply(_3);//.mod(p);
            d = V.y.multiply(V.z).shiftLeft(1);//.mod(p);
        } else {
            // lambda = (P.y - V.y)/(P.x - V.x) // P.Z = 1
            // = (P.Y - V.Y/V.Z^3) / (P.X - V.X/V.Z^2)
            // = (P.Y*V.Z^3 - V.Y) / (P.X*V.Z^3 - V.X*V.Z)
            assert (P.z.compareTo(_1) == 0);
            n = P.y.multiply(Vz3).subtract(V.y);//.mod(p);
            d = P.x.multiply(Vz3).subtract(V.x.multiply(V.z));//.mod(p);
        }
        // lambda = n/d
        BNField2[] w = new BNField2[6];
        //n*(Qt[1]*z^2 - V.x) + d*(V.y - Qt[2]*z^3);
        //n*Q.x*z^2 - n*V.x + d*V.y - d*Q.y*z^3;
        //(d*V.y - n*V.x) + n*Q.x*z^2 - d*Q.y*z^3;
        //(d*V.Y/V.Z^3 - n*V.X/V.Z^2) + n*Q.x*z^2 - d*Q.y*z^3;
        //(d*V.Y - n*V.X*V.Z) + n*Q.x*V.Z^3*z^2 - d*Q.y*V.Z^3*z^3;
        w[0] = new BNField2(bn, d.multiply(V.y).subtract(n.multiply(V.x).multiply(V.z)).mod(bn.p));
        w[2] = Q.x.multiply(n.multiply(Vz3));
        w[3] = Q.y.multiply(p.subtract(d).multiply(Vz3));
        w[1] = w[4] = w[5] = E2.Fp2_0;
        return new BNField12(bn, w);
    }

	/**
	 * Compute the eta (sometimes called twisted ate) pairing for points P and Q on BN curves E and E'.
	 */
    public BNField12 eta(BNPoint P, BNPoint2 Q) {
        if (!E.contains(P) || !E2.contains(Q)) {
            throw new IllegalArgumentException(incompatibleCurves);
        }
        BNField12 f = Fp12_1;
        P = P.normalize();
        Q = Q.normalize();
        if (!P.isZero() && !Q.isZero()) {
            BNParams bn = E.bn;
            BNPoint V = P;
            BigInteger ord = bn.rho; // the Tate pairing would have order bn.n instead of bn.rho
            for (int i = ord.bitLength() - 2; i >= 0; i--) {
            	BNPoint A = V.twice(1);
                f = f.square().multiply(gl(V, V, A, Q));
                V = A;
                if (ord.testBit(i)) {
                    f = f.multiply(gl(V, P, null, Q));
                    V = V.add(P);
                }
            }
            f = f.finExp();
        }
        return f;
    }

    BNField12 gl(BNPoint2 T, BNPoint2 Q, BNPoint2 A, BNPoint P) {
        BNField2 n, d;
        if (T.isZero() || P.isZero() || Q.isZero() || T.opposite(Q)) {
            return Fp12_1;
        }
        BNField2 Tz3 = T.z.cube();
        if (A != null) {
            n = A.m;
            d = A.z;
        } else if (T.equals(Q)) {
            // y = Y/Z^3 => 1/2y = Z^3/2Y
            // x = X/Z^2 => 3x^2 = 3X^2/Z^4 =>
            // => lambda = 3x^2/2y = 3X^2/(2Y*Z)
            n = T.x.square().multiply(_3);
            d = T.y.multiply(T.z).twice(1);
        } else {
            // lambda = (Q.y - T.y)/(Q.x - T.x) // Q.Z = 1
            // = (Q.Y - T.Y/T.Z^3) / (Q.X - T.X/T.Z^2)
            // = (Q.Y*T.Z^3 - T.Y) / (Q.X*T.Z^3 - T.X*T.Z)
            assert (Q.z.isOne());
            n = Q.y.multiply(Tz3).subtract(T.y);
            d = Q.x.multiply(Tz3).subtract(T.x.multiply(T.z));
        }
        // lambda = n/d
        BNField2[] w = new BNField2[6];
        //n*(P.x - T.x*z^2)*z + d*(T.y*z^3 - P.y);
        //-d*P.y + n*P.x*z + (d*T.y - n*T.x)*z^3;
        //-d*P.y + n*P.x*z + (d*T.Y/T.Z^3 - n*T.X/T.Z^2)*z^3;
        //-d*P.y*T.Z^3 + n*P.x*T.Z^3*z + (d*T.Y - n*T.X*T.Z)*z^3;
        w[0] = d.multiply(bn.p.subtract(P.y)).multiply(Tz3);
        w[1] = n.multiply(P.x).multiply(Tz3);
        w[3] = d.multiply(T.y).subtract(n.multiply(T.x).multiply(T.z));
        w[2] = w[4] = w[5] = E2.Fp2_0;
        return new BNField12(bn, w);
    }

    public BNField12 ate(BNPoint2 Q, BNPoint P) {
        if (!E.contains(P) || !E2.contains(Q)) {
            throw new IllegalArgumentException(incompatibleCurves);
        }
        BNField12 f = Fp12_1;
        P = P.normalize();
        Q = Q.normalize();
        if (!P.isZero() && !Q.isZero()) {
            BNParams bn = E.bn;
            BNPoint2 T = Q;
            BigInteger ord = bn.t.subtract(_1);
            for (int i = ord.bitLength() - 2; i >= 0; i--) {
                BNPoint2 A = T.twice(1);
                f = f.square().multiply(gl(T, T, A, P));
                T = A;
                if (ord.testBit(i)) {
                    f = f.multiply(gl(T, Q, null, P));
                    T = T.add(Q);
                }
            }
            f = f.finExp();
        }
        return f;
    }

    public BNField12 opt(BNPoint2 Q, BNPoint P) {
        if (!E.contains(P) || !E2.contains(Q)) {
            throw new IllegalArgumentException(incompatibleCurves);
        }
        BNField12 f = Fp12_1;
        P = P.normalize();
        Q = Q.normalize();
        if (!P.isZero() && !Q.isZero()) {
            BNParams bn = E.bn;
            BNPoint2 T = Q;
            BigInteger ord = bn.optOrd; // 6u+2
            for (int i = ord.bitLength() - 2; i >= 0; i--) {
                BNPoint2 A = T.twice(1);
                f = f.square().multiply(gl(T, T, A, P));
                T = A;
                if (ord.testBit(i)) {
                    f = f.multiply(gl(T, Q, null, P));
                    T = T.add(Q);
                }
            }
            // now T = [6u+2]Q
            // optimal pairing: f = f_{6u+2,Q}(P)*l_{Q3,-Q2}(P)*l_{-Q2+Q3,Q1}(P)*l_{Q1-Q2+Q3,[6u+2]Q}(P)
            BNPoint2 Q1 = Q.frobex(1);
            BNPoint2 Q2 = Q.frobex(2).negate();
            BNPoint2 Q3 = Q.frobex(3);
            BNPoint2 Q4 = Q2.add(Q3);
            BNPoint2 Q5 = Q4.add(Q1);
            f = f.multiply(gl(Q2, Q3, null, P)).multiply(gl(Q4, Q1, null, P)).multiply(gl(Q5, T, null, P));
            f = f.finExp();
        }
        return f;
    }

    protected BNField12 g = null;

    public void gSet(BNField12 g) {
        this.g = g;
        BNField12[] gp16i = gp16[0];
        gp16i[0] = Fp12_1;
        gp16i[1] = g;
        for (int i = 1, j = 2; i <= 7; i++, j += 2) {
            gp16i[j  ] = gp16i[i].square();
            gp16i[j+1] = gp16i[j].multiply(g);
        }
        for (int i = 1; i < gp16.length; i++) {
            BNField12[] gp16h = gp16i;
            gp16i = gp16[i];
            gp16i[0] = gp16h[0];
            for (int j = 1; j < 16; j++) {
                gp16i[j] = gp16h[j].square().square().square().square();
            }
        }
    }

    public BNField12 gPower(BigInteger k) {
        if (g == null) {
            throw new IllegalArgumentException("not initialized");
        }
        k = k.mod(bn.n);
        BNField12 A = Fp12_1;
        for (int i = 0, w = 0; i < gp16.length; i++, w >>>= 4) {
            if ((i & 7) == 0) {
                w = k.intValue();
                k = k.shiftRight(32);
            }
            A = A.multiply(gp16[i][w & 0xf]);
        }
        return A;
    }

    public String toString() {
        return "Bilinear pairings over " + E + " and " + E2;
    }
}
