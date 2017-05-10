/**
 * BNPoint.java
 *
 * Arithmetic in the group of points on a BN elliptic curve over GF(p).
 *
 * A point of an elliptic curve is only meaningful when suitably attached
 * to some curve.  Hence, there must be no public means to create a point
 * by itself (i.e. concrete subclasses of BNPoint shall have no public
 * constructor); the proper way to do this is to invoke the factory method
 * pointFactory() of the desired BNCurve subclass.
 *
 * Copyright (C) Paulo S. L. M. Barreto.
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
import java.security.SecureRandom;

public class BNPoint {

    /**
     * Convenient BigInteger constants
     */
    private static final BigInteger
        _0 = BigInteger.valueOf(0L),
        _1 = BigInteger.valueOf(1L),
        _3 = BigInteger.valueOf(3L);

    public static final String differentCurves =
        "Cannot combine points from different elliptic curves";
    public static final String invalidCPSyntax =
        "Syntax error in curve point description";
    public static final String pointNotOnCurve =
        "The given point does not belong to the given elliptic curve";

    public BNCurve getE() {
        return E;
    }

    /**
     * The underlying elliptic curve, given by its parameters
     */
     BNCurve E;

    /**
     * The projective x-coordinate
     */
    BigInteger x;

    /**
     * The projective y-coordinate
     */
    BigInteger y;

    /**
     * The projective z-coordinate
     */
    BigInteger z;

    /**
     * Numerator of the line slope if this point was the result of a group operation
     */
    BigInteger m;

    /**
     * Flag/mask for compressed, expanded, or hybrid point representation
     */
    public static final int
        COMPRESSED  = 2,
        EXPANDED    = 4,
        HYBRID      = COMPRESSED | EXPANDED;

    /**
     * Create an instance of the BNCurve point at infinity on curve E.
     *
     * @param   E   the elliptic curve where the created point is located.
     */
    BNPoint(BNCurve E) {
        this.E = E;
        /*
         * the point at infinity is represented as (1, 1, 0) after IEEE Std 1363:2000
         * (notice that this triple satisfies the projective curve equation y^2 = x^3 + b.z^6)
         */
        x = _1;
        y = _1;
        z = _0;
        m = _0;
    }

    /**
     * Create a normalized BNCurve point from given affine coordinates and a curve
     *
     * @param   E   the underlying elliptic curve.
     * @param   x   the affine x-coordinate (mod p).
     * @param   y   the affine y-coordinate (mod p).
     */
    public BNPoint(BNCurve E, BigInteger x, BigInteger y) {
        this.E = E;
        BigInteger p = E.bn.p; // shorthand
        this.x = x.mod(p);
        this.y = y.mod(p);
        this.z = _1; // normalized
        this.m = _0;
        if (!E.contains(this)) {
            throw new IllegalArgumentException(pointNotOnCurve);
        }
    }

    /**
     * Create an BNCurve point from a given affine x-coordinate, a y-bit, and a curve
     *
     * @param   E       the underlying elliptic curve.
     * @param   x       the affine x-coordinate (mod p).
     * @param   yBit    the least significant bit of the y-coordinate.
     */
    BNPoint(BNCurve E, BigInteger x, int yBit) {
        this.E = E;
        BigInteger p = E.bn.p; // shorthand
        this.x = x.mod(p);
        if (x.signum() == 0) {
            throw new IllegalArgumentException(pointNotOnCurve); // otherwise the curve order would not be prime
            //this.y = E.sqrt(E.b);
        } else {
            this.y = E.bn.sqrt(x.multiply(x).multiply(x).add(E.b).mod(p));
            if (y == null) {
                throw new IllegalArgumentException(pointNotOnCurve);
            }
            if (y.testBit(0) != ((yBit & 1) == 1)) {
                y = p.subtract(y);
            }
        }
        this.z = _1; // normalized
        this.m = _0;
        assert (!E.contains(this));
    }

    /**
     * Create an BNCurve point from a given x-trit, an affine y-coordinate, and a curve
     *
     * @param   E       the underlying elliptic curve.
     * @param   xTrit   the least significant trit of the x-coordinate.
     * @param   y       the affine y-coordinate (mod p).
     */
    BNPoint(BNCurve E, int xTrit, BigInteger y) {
        this.E = E;
        BigInteger p = E.bn.p; // shorthand
        this.y = y.mod(p);
        if (y.signum() == 0) {
            throw new IllegalArgumentException(pointNotOnCurve); // otherwise the curve order would not be prime
            //this.x = E.cbrt(p.subtract(E.b));
        } else {
            this.x = E.bn.cbrt(y.multiply(y).subtract(E.b).mod(p));
            if (x == null) {
                throw new IllegalArgumentException(pointNotOnCurve);
            }
            // either x, zeta*x, or zeta^2*x is the desired x-coordinate:
            if (x.mod(_3).intValue() != xTrit) {
                BigInteger zeta = E.bn.zeta; // shorthand
                x = zeta.multiply(x).mod(p);
                if (x.mod(_3).intValue() != xTrit) {
                    x = zeta.multiply(x).mod(p);
                    if (x.mod(_3).intValue() != xTrit) {
                        throw new IllegalArgumentException(pointNotOnCurve);
                    }
                }
            }
        }
        this.z = _1; // normalized
        this.m = _0;
        assert (!E.contains(this));
    }

    /**
     * Create an BNCurve point from given projective coordinates and a curve.
     *
     * @param   E   the underlying elliptic curve.
     * @param   x   the affine x-coordinate (mod p).
     * @param   y   the affine y-coordinate (mod p).
     * @param   z   the affine z-coordinate (mod p).
     */
    private BNPoint(BNCurve E, BigInteger x, BigInteger y, BigInteger z) {
        this.E = E;
        this.x = x;
        this.y = y;
        this.z = z;
        this.m = _0;
    }

    private BNPoint(BNCurve E, BigInteger x, BigInteger y, BigInteger z, BigInteger m) {
        this.E = E;
        this.x = x;
        this.y = y;
        this.z = z;
        this.m = m;
    }

    /**
     * Create a clone of a given point.
     *
     * @param   Q   the point to be cloned.
     */
    @SuppressWarnings("unused")
    private BNPoint(BNPoint Q) {
        this.E = Q.E;
        this.x = Q.x;
        this.y = Q.y;
        this.z = Q.z;
        this.m = Q.m;
    }

    /*
     * performing arithmetic operations on elliptic curve points
     * generally implies knowing the nature of these points (more precisely,
     * the nature of the finite field to which their coordinates belong),
     * hence they are done by the underlying elliptic curve.
     */

    /**
     * Check whether this is the point at infinity (i.e. the BNCurve group zero element).
     *
     * @return  true if this is the point at infinity, otherwise false.
     */
    public boolean isZero() {
        return z.signum() == 0;
    }

    /**
     * Compare this point to a given object.
     *
     * @param   Q   the elliptic curve point to be compared to this.
     *
     * @return  true if this point and Q are equal, otherwise false.
     */
    public boolean equals(Object Q) {
        if (!(Q instanceof BNPoint && this.isOnSameCurve((BNPoint)Q))) {
            return false;
        }
        BNPoint P = (BNPoint)Q;
        if (z.signum() == 0 || P.z.signum() == 0) {
            return z.equals(P.z);
        }
        BigInteger p = E.bn.p; // shorthand
        BigInteger
            z2 = z.multiply(z).mod(p),
            z3 = z.multiply(z2).mod(p),
            pz2 = P.z.multiply(P.z).mod(p),
            pz3 = P.z.multiply(pz2).mod(p);
        return
            x.multiply(pz2).subtract(P.x.multiply(z2)).mod(p).signum() == 0 &&
            y.multiply(pz3).subtract(P.y.multiply(z3)).mod(p).signum() == 0;
    }

    /**
     * Check whether Q lays on the same curve as this point.
     *
     * @param   Q   an elliptic curve point.
     *
     * @return  true if Q lays on the same curve as this point, otherwise false.
     */
    public boolean isOnSameCurve(BNPoint Q) {
        return E.bn == Q.E.bn; // singleton comparison
    }

    /**
     * Compute a random point on the same curve as this.
     *
     * @param    rand    a cryptographically strong pseudo-random number generator.
     *
     * @return  a random point on the same curve as this.
     */
    public BNPoint randomize(SecureRandom rand) {
        return E.pointFactory(rand);
    }

    /**
     * Normalize this point.
     *
     * @return  a normalized point equivalent to this.
     */
    public BNPoint normalize() {
        if (z.signum() == 0 || z.compareTo(_1) == 0) {
            return this; // already normalized
        }
        BigInteger p = E.bn.p; // shorthand
        BigInteger zinv = null;
        try {
        	zinv = z.modInverse(p);
        } catch (ArithmeticException a) {
        }
        BigInteger zinv2 = zinv.multiply(zinv); // mod p
        return new BNPoint(E, x.multiply(zinv2).mod(p), y.multiply(zinv).multiply(zinv2).mod(p), _1);
    }

    public boolean isNormal() {
        return (z.signum() == 0 || z.compareTo(_1) == 0);
    }

    /**
     * Compute -this.
     *
     * @return  -this.
     */
    public BNPoint negate() {
        return new BNPoint(E, x, (y.signum() != 0) ? E.bn.p.subtract(y) : y, z);
    }

    /**
     * Check if a point equals -this.
     */
    public boolean opposite(BNPoint P) {
        if (!isOnSameCurve(P)) {
            return false;
        }
        if (z.signum() == 0 || P.isZero()) {
            return z.compareTo(P.z) == 0;
        }
        BigInteger p = E.bn.p; // shorthand
        BigInteger
            z2 = z.multiply(z),//.mod(p),
            z3 = z.multiply(z2).mod(p),
            pz2 = P.z.multiply(P.z),//.mod(p),
            pz3 = P.z.multiply(pz2).mod(p);
        return
            x.multiply(pz2).subtract(P.x.multiply(z2)).mod(p).signum() == 0 &&
            y.multiply(pz3).add(P.y.multiply(z3)).mod(p).signum() == 0;
    }

    /**
     * Compute this + Q.
     *
     * @return  this + Q.
     *
     * @param   Q   an elliptic curve point.
     */
    public BNPoint add(BNPoint Q) {
        assert (isOnSameCurve(Q));
        if (this.isZero()) {
            return Q;
        }
        if (Q.isZero()) {
            return this;
        }
        BigInteger p = E.bn.p; // shorthand
        BigInteger t1, t2, t3, t4, t5, t6, t7, t8;
        t1 = x;
        t2 = y;
        t3 = z;
        t4 = Q.x;
        t5 = Q.y;
        t6 = Q.z;
        if (t6.compareTo(_1) != 0) {
            t7 = t6.multiply(t6); // t7 = z1^2
            // u0 = x0.z1^2
            t1 = t1.multiply(t7).mod(p);
            // s0 = y0.z1^3 = y0.z1^2.z1
            t2 = t2.multiply(t7).multiply(t6).mod(p);
        }
        if (t3.compareTo(_1) != 0) {
            t7 = t3.multiply(t3); // t7 = z0^2
            // u1 = x1.z0^2
            t4 = t4.multiply(t7).mod(p);
            // s1 = y1.z0^3 = y1.z0^2.z0
            t5 = t5.multiply(t7).multiply(t3).mod(p);
        }
        // W = u0 - u1
        t7 = t1.subtract(t4).mod(p);
        // R = s0 - s1
        t8 = t2.subtract(t5).mod(p);
        if (t7.signum() == 0) {
            return (t8.signum() == 0) ? Q.twice(1) : E.infinity;
        }
        // T = u0 + u1
        t1 = t1.add(t4);//.mod(p);
        // M = s0 + s1
        t2 = t2.add(t5);//.mod(p);
        // z2 = z0.z1.W
        if (!t6.equals(_1)) {
            t3 = t3.multiply(t6); // no need to reduce here
        }
        t3 = t3.multiply(t7).mod(p);
        // x2 = R^2 - T.W^2
        t5 = t7.multiply(t7).mod(p); // t5 = W^2
        t6 = t1.multiply(t5);//.mod(p); // t6 = T.W^2
        t1 = t8.multiply(t8).subtract(t6).mod(p);
        // 2.y2 = (T.W^2 - 2.x2).R - M.W^2.W
        t2 = t6.subtract(t1.shiftLeft(1)).multiply(t8).subtract(t2.multiply(t5).multiply(t7)).mod(p);
        t2 = (t2.testBit(0) ? t2.add(p) : t2).shiftRight(1).mod(p);
    	return new BNPoint(E, t1, t2, t3);
    }

    /**
     * Left-shift this point by a given distance n, i.e. compute (2^^n)*this.
     *
     * @param    n    the shift amount.
     *
     * @return (2^^n)*this.
     */
    public BNPoint twice(int n) {
        // P1363 section A.10.4
        BigInteger t1, t2, t3, t4, t5, M = _0;
        t1 = x;
        t2 = y;
        t3 = z;
        BigInteger p = E.bn.p; // shorthand
        while (n-- > 0) {
            if (t2.signum() == 0 || t3.signum() == 0) {
                return E.infinity;
            }
            t4 = t3.multiply(t3); // t4 = z^2 (no need to reduce: z is often 1)
            // M = 3.x^2
            M = t4 = _3.multiply(t1.multiply(t1)).mod(p);
            // z2 = 2.y.z
            t3 = t3.multiply(t2).shiftLeft(1).mod(p);
            // S = 4.x.y^2
            t2 = t2.multiply(t2).mod(p); // t2 = y^2
            t5 = t1.multiply(t2).shiftLeft(2);//.mod(p);
            // x2 = M^2 - 2.S
            t1 = t4.multiply(t4).subtract(t5.shiftLeft(1)).mod(p);
            // T = 8.(y^2)^2
            t2 = t2.multiply(t2).shiftLeft(3);//.mod(p);
            // y2 = M(S - x2) - T
            t2 = t4.multiply(t5.subtract(t1)).subtract(t2).mod(p);
        }
        return new BNPoint(E, t1, t2, t3, M);
    }

    /**
     * Compute k*this
     *
     * This method implements the quaternary window multiplication algorithm.
     *
     * Reference:
     *
     * Alfred J. Menezes, Paul C. van Oorschot, Scott A. Vanstone,
     *      "Handbook of Applied Cryptography", CRC Press (1997),
     *      section 14.6 (Exponentiation), algorithm 14.82
     *
     * @param   k   scalar by which this point is to be multiplied
     *
     * @return  k*this
     */
    //*
    public BNPoint multiply0(BigInteger k) {
        BNParams bn = E.bn; // shorthand
        BNPoint P = this.normalize();
        if (k.signum() < 0) {
            k = k.negate();
            P = P.negate();
        }
		k = k.mod(bn.n);
        byte[] e = k.toByteArray();
        BNPoint[] mP = new BNPoint[16];
        mP[0] = E.infinity;
        mP[1] = P;
        for (int i = 1; i <= 7; i++) {
            mP[2*i    ] = mP[  i].twice(1);
            mP[2*i + 1] = mP[2*i].add(P);
        }
        BNPoint A = E.infinity;
        for (int i = 0; i < e.length; i++) {
            int u = e[i] & 0xff;
            A = A.twice(4).add(mP[u >>> 4]).twice(4).add(mP[u & 0xf]);
        }
        return A;
    }
    //*/

    /**
     * Compute k*this
     *
     * This method implements the GLV strategy.
     *
     * @param   k   scalar by which this point is to be multiplied
     *
     * @return  k*this
     */
	public BNPoint multiply(BigInteger k) {
		BNParams bn = E.bn;
		BNPoint P = this.normalize();
        if (k.signum() < 0) {
            k = k.negate();
            P = P.negate();
        }
		/*
		BigInteger r = bn.rho;
		BigInteger t = _1;
		BigInteger q = bn.n, s = _0, b = _3.multiply(bn.u).multiply(bn.u.shiftLeft(1).add(_1)).add(_1); // 3*u*(2*u + 1) + 1 = 6*u^2 + 3*u + 1
		while (r.compareTo(b) >= 0) {
		    if (r.compareTo(q) < 0) {
				BigInteger swap;
		        swap = r; r = q; q = swap;
		        swap = t; t = s; s = swap;
		    }
		    BigInteger c = r.divide(q);
		    r = r.subtract(c.multiply(q));
		    t = t.subtract(c.multiply(s));
		}
		//*/
		//*
		BigInteger r = bn.u.shiftLeft(1).add(_1); // 2*u + 1
		BigInteger t = bn.u.multiply(_3).add(_1).multiply(bn.u.shiftLeft(1)); // (3*u + 1)*2*u = 6*u^2 + 2*u
		//*/
		BigInteger halfn = bn.n.shiftRight(1);
		BigInteger kr = k.multiply(r);
		if (kr.mod(bn.n).compareTo(halfn) <= 0) {
			kr = kr.divide(bn.n);
		} else {
			kr = kr.divide(bn.n).add(_1);
		}
		BigInteger kt = k.multiply(t);
		if (kt.mod(bn.n).compareTo(halfn) <= 0) {
			kt = kt.divide(bn.n);
		} else {
			kt = kt.divide(bn.n).add(_1);
		}
		// [k - (kr*B_11 + kt*B_21), -(kr*B_12 + kt*B_22)]
		/*
		 * [kr, kt]*[2*u + 1          6*u^2 + 2*u]
		 *          [6*u^2 + 4*u + 1   -(2*u + 1)]
		 */
		BigInteger sr = k.subtract(kr.multiply(r).add(kt.multiply(t.add(r))));
		BigInteger st = kr.multiply(t).subtract(kt.multiply(r));
		BNPoint Y = new BNPoint(E, P.x.multiply(bn.zeta), P.y, P.z);
		//assert (Y.equals(P.multiply(bn.rho)));
		//assert (sr.add(bn.rho.multiply(st)).mod(bn.n).compareTo(k) == 0);
		return P.simultaneous(sr, st, Y);
	}

    /**
     * Compute ks*this + kr*Y.  This is useful in the verification part of several signature algorithms,
     * and (hopely) faster than two scalar multiplications.
     *
     * @param   ks  scalar by which this point is to be multiplied.
     * @param   kr  scalar by which Y is to be multiplied.
     * @param   Y   a curve point.
     *
     * @return  ks*this + kr*Y
     */
    public BNPoint simultaneous(BigInteger ks, BigInteger kr, BNPoint Y) {
        assert (isOnSameCurve(Y));
        BNPoint[] hV = new BNPoint[16];
        BNPoint P = this.normalize();
        Y = Y.normalize();
        if (ks.signum() < 0) {
        	ks = ks.negate();
        	P = P.negate();
        }
        if (kr.signum() < 0) {
        	kr = kr.negate();
        	Y = Y.negate();
        }
        hV[0] = E.infinity;
        hV[1] = P;
        hV[2] = Y;
        hV[3] = P.add(Y);
        for (int i = 4; i < 16; i += 4) {
            hV[i] = hV[i >> 2].twice(1);
            hV[i + 1] = hV[i].add(hV[1]);
            hV[i + 2] = hV[i].add(hV[2]);
            hV[i + 3] = hV[i].add(hV[3]);
        }
        int t = Math.max(kr.bitLength(), ks.bitLength());
        BNPoint R = E.infinity;
        for (int i = (((t + 1) >> 1) << 1) - 1; i >= 0; i -= 2) {
            int j = (kr.testBit(i  ) ? 8 : 0) |
                    (ks.testBit(i  ) ? 4 : 0) |
                    (kr.testBit(i-1) ? 2 : 0) |
                    (ks.testBit(i-1) ? 1 : 0);
            R = R.twice(2).add(hV[j]);
        }
        return R;
    }

    public BNPoint simultaneous(BigInteger kP, BNPoint P, BigInteger kQ, BNPoint Q, BigInteger kR, BNPoint R, BigInteger kS, BNPoint S) {
        BNPoint[] hV = new BNPoint[16];
        P = P.normalize(); Q = Q.normalize(); R = R.normalize(); S = S.normalize();
        if (kP.signum() < 0) {
        	kP = kP.negate(); P = P.negate();
        }
        if (kQ.signum() < 0) {
        	kQ = kQ.negate(); Q = Q.negate(); 
        }
        if (kR.signum() < 0) {
        	kR = kR.negate(); R = R.negate();
        }
        if (kS.signum() < 0) {
        	kS = kS.negate(); S = S.negate();
        }
        hV[ 0] = E.infinity;
        hV[ 1] = P; hV[ 2] = Q; hV[ 4] = R; hV[ 8] = S;
		for (int i = 2; i < 16; i <<= 1) {
			for (int j = 1; j < i; j++) {
		        hV[i + j] = hV[i].add(hV[j]);
			}
		}
        int t = Math.max(Math.max(kP.bitLength(), kQ.bitLength()), Math.max(kR.bitLength(), kS.bitLength()));
        BNPoint V = E.infinity;
        for (int i = t - 1; i >= 0; i--) {
            int j = (kS.testBit(i) ?   8 : 0) |
                    (kR.testBit(i) ?   4 : 0) |
                    (kQ.testBit(i) ?   2 : 0) |
                    (kP.testBit(i) ?   1 : 0);
            V = V.twice(1).add(hV[j]);
        }
        return V;
    }

    /**
     * Convert this curve point to a byte array.
     * This is the ANSI X9.62 Point-to-Octet-String Conversion primitive
     *
     * @param   formFlags   the desired form of the octet string representation
     *                      (BNPoint.COMPRESSED, BNPoint.EXPANDED, BNPoint.HYBRID)
     *
     * @return  this point converted to a byte array using
     *          the algorithm defined in section 4.3.6 of ANSI X9.62
     */
    public byte[] toByteArray(int formFlags) {
        byte[] result;
        if (this.isZero()) {
            result = new byte[1];
            result[0] = (byte)0;
            return result;
        }
        BNPoint P = this.normalize();
        byte[] osX = null, osY = null;
        osX = P.x.toByteArray();
        int pc = 0, resLen = 1 + osX.length;
        if ((formFlags & COMPRESSED) != 0) {
            pc |= COMPRESSED | (P.y.testBit(0) ? 1 : 0);
        }
        if ((formFlags & EXPANDED) != 0) {
            pc |= EXPANDED;
            osY = P.y.toByteArray();
            resLen += osY.length;
        }
        result = new byte[resLen];
        result[0] = (byte)pc;
        System.arraycopy(osX, 0, result, 1, osX.length);
        if (osY != null) {
            System.arraycopy(osY, 0, result, 1 + osX.length, osY.length);
        }
        return result;
    }

    public String toString() {
        return this.isZero() ? "O" : "[" + x + " : " + y + " : " + z + "]";
    }

    public  BigInteger getX() {
        return x;
    }
    public BigInteger getY(){
        return y;
    }
}
