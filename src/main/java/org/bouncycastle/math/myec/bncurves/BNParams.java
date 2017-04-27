/**
 * BNParams.java
 *
 * Parameters for Barreto-Naehrig (BN) pairing-friendly elliptic curves.
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

public class BNParams {

    /**
     * Convenient BigInteger constants
     */
    static final BigInteger
        _0 = BigInteger.valueOf(0L),
        _1 = BigInteger.valueOf(1L),
        _2 = BigInteger.valueOf(2L),
        _3 = BigInteger.valueOf(3L),
        _4 = BigInteger.valueOf(4L),
        _5 = BigInteger.valueOf(5L),
        _6 = BigInteger.valueOf(6L),
        _7 = BigInteger.valueOf(7L),
        _9 = BigInteger.valueOf(9L),
        _24 = BigInteger.valueOf(24L);

    /**
     * Rabin-Miller certainty used for primality testing
     */
    static final int PRIMALITY_CERTAINTY = 20;

    /**
     * Invalid parameters error message
     */
    public static final String invalidParams =
        "The specified parameters do not properly define a suitable BN curve";

    /**
     * BN index -- the curve BN(u) is defined by the following parameters:
     *
     * t = 6*u^2 + 1
     * p = 36*u^4 + 36*u^3 + 24*u^2 + 6*u + 1
     * n = 36*u^4 + 36*u^3 + 18*u^2 + 6*u + 1
     *
     * BN(u)/GF(p): y^2 = x^3 + b, #BN(u)(GF(p)) = n, n = p + 1 - t.
     *
     * Restrictions: p = 3 (mod 4) and p = 4 (mod 9).
     */
    BigInteger u;

    /**
     * Size of the underlying finite field GF(p)
     */
    BigInteger p;

    /**
     * Trace of the Frobenius endomorphism
     **/
    BigInteger t;

    /**
     * Primitive cube root of unity mod p
     */
    BigInteger zeta;

    BigInteger zeta0;
    BigInteger zeta1;
    BigInteger mzeta0;
    BigInteger mzeta1;

	/**
	 * (t - 1)^2 mod n, order of eta (also called twisted ate) pairing for BN curves
	 */
	BigInteger rho;

    /**
     * Order of optimal pairing: (6*u + 2)
     */
    BigInteger optOrd;

    BigInteger sqrtExponent;
    BigInteger cbrtExponent;
    BigInteger sqrtExponent2;
    BigInteger cbrtExponent2;
    BigInteger invSqrtMinus2;

    /**
     * Prime curve order
     */
    BigInteger n;

    /**
     * Cofactor of twist (curve order = ht*n)
     */
    BigInteger ht;

    BigInteger sigma;
    BigInteger zeta0sigma;
    BigInteger zeta1sigma;

    BigInteger[] omega;

    BNField2 sqrtI;
    BNField2 Fp2_0;
    BNField2 Fp2_1;
    BNField12 Fp12_0;
    BNField12 Fp12_1;

	BigInteger[] latInv;
	BigInteger[][] latRed;

    public boolean equals(Object o) {
        if (!(o instanceof BNParams)) {
            return false;
        }
        return (u.compareTo(((BNParams)o).u) == 0);
    }

    /**
     * Compute BN parameters for a given field size, which must be a multiple
     * of 8 between 56 and 512 (inclusive).
     *
     * The BN parameter u is the largest one with the smallest possible Hamming
     * weight, leading to a field prime p satisfying both p = 3 (mod 4) and
     * p = 4 (mod 9), speeding up the computation of square and cube roots in
     * both F_p and F_{p^2}. Besides, for i \in F_{p^2} such that i^2 + 1 = 0,
     * the element v = 1 + i is neither a square nor a cube, so that one can
     * represent F_{p^2m} as F_{p^2}[z]/(z^m - 1/v) or F_{p^2}[z]/(z^m - v) for
     * m = 2, 3, 6.
     *
     * The standard curve is E(F_p): y^2 = x^3 + 3, whose default generator is
     * G = (1, 2). Its (sextic) twist is E'(F_{p^2}): y'^2 = x'^3 + 3v, whose
     * default generator has the form G' = [p-1+t]*(1, y') for some y'.
     *
     * The standard isomorphism psi: E'(F_{p^2}) -> E(F_{p^12}) is defined as
     * psi(x', y') = (x'*z^2, y'*z^3) for the first representation of F_{p^12}
     * above, and as psi(x', y') = (x'/z^2, y'/z^3) = (x'*z^4/v, y'*z^3/v) for
     * the second representation.
     */
    public BNParams(int fieldBits) {
        switch (fieldBits) {
        case 27:
            u = BigInteger.valueOf(-41L); // debugging only
            break;
        case 56:
            u = new BigInteger("1011001111011", 2); // Hamming weight 9
            break;
        case 64:
            u = new BigInteger("110010000111111", 2); // Hamming weight 9
            break;
        case 72:
            u = new BigInteger("10110000111001011", 2); // Hamming weight 9
            break;
        case 80:
            u = new BigInteger("1101000010001011011", 2); // Hamming weight 9
            break;
        case 88:
            u = new BigInteger("110000010010001001111", 2); // Hamming weight 9
            break;
        case 96:
            u = new BigInteger("11010000000000000010111", 2); // Hamming weight 7
            break;
        case 104:
            u = new BigInteger("1101000000000000000100011", 2); // Hamming weight 6
            break;
        case 112:
            u = new BigInteger("101100100000000100000000011", 2); // Hamming weight 7
            break;
        case 120:
            u = new BigInteger("11000000100000000100100000011", 2); // Hamming weight 7
            break;
        case 128:
            u = new BigInteger("1100100000100000000000001000111", 2); // Hamming weight 8
            break;
        case 136:
            u = new BigInteger("110001000000000100000100000000111", 2); // Hamming weight 8
            break;
        case 144:
            u = new BigInteger("11000100000000000000100000100000011", 2); // Hamming weight 7
            break;
        case 152:
            u = new BigInteger("1100100000000000000000100000000100011", 2); // Hamming weight 7
            break;
        case 160:
            u = new BigInteger("110100001000000000000100010000000000011", 2); // *** ISO, Hamming weight 8
            break;
        case 168:
            u = new BigInteger("11000010010000100000000000000000000000011", 2); // Hamming weight 7
            break;
        case 176:
            u = new BigInteger("1100000000000000000000100000000000001001011", 2); // Hamming weight 7
            break;
        case 184:
            u = new BigInteger("110000000000100000000000001000000001000000011", 2); // Hamming weight 7
            break;
        case 192:
            u = new BigInteger("11000000000000000001000000000000000010000010011", 2); // *** ISO, Hamming weight 7
            break;
        case 200:
            u = new BigInteger("1101000100000100000000000000000000100000000000011", 2); // Hamming weight 8
            break;
        case 208:
            u = new BigInteger("110000000000000000000000000000000000000000100000011", 2); // Hamming weight 5
            break;
        case 216:
            u = new BigInteger("11000000001000000000000010000000000100000000000000011", 2); // Hamming weight 7
            break;
        case 224:
            u = new BigInteger("1100000000000000000000100000001000000000000001000000011", 2); // *** ISO, Hamming weight 7
            break;
        case 232:
            u = new BigInteger("110000100000000000001000000000000001000000000000000000011", 2); // Hamming weight 7
            break;
        case 240:
            u = new BigInteger("11010000000000000010000000000000000000000000000000000000111", 2); // Hamming weight 7
            break;
        case 248:
            u = new BigInteger("1101000000000000000010000000000000000000000010000000000000011", 2); // Hamming weight 7
            break;
        case 256:
            u = new BigInteger("110000010000000000000000000000000000000000001000000000001000011", 2); // *** ISO, Hamming weight 7
            break;
        case 264:
            u = new BigInteger("11000000000000000001000000000000000000000000000000000100000000011", 2); // Hamming weight 6
            break;
        case 272:
            u = new BigInteger("1100000100000000000010000000000000000000000000000000000000001000011", 2); // Hamming weight 7
            break;
        case 280:
            u = new BigInteger("110000000000001000000000000000000000000000000000000000000000011000011", 2); // Hamming weight 7
            break;
        case 288:
            u = new BigInteger("11000000000000000000000000000000000100000001000000000000000000000000011", 2); // Hamming weight 6
            break;
        case 296:
            u = new BigInteger("1100000000000000000000100000000000000000100000000000000000000000000100011", 2); // Hamming weight 7
            break;
        case 304:
            u = new BigInteger("110000000000000100000000000000000000000000000000000000000001000000000000011", 2); // Hamming weight 6
            break;
        case 312:
            u = new BigInteger("11000000100000000000000000000010000000000000000000000000000000010000000000011", 2); // Hamming weight 7
            break;
        case 320:
            u = new BigInteger("1100000000000000000000000000000000000100100000000000000000100000000000000000011", 2); // Hamming weight 7
            break;
        case 328:
            u = new BigInteger("110000000000000000000000000000000000000000100001000000000000000000000000001000011", 2); // Hamming weight 7
            break;
        case 336:
            u = new BigInteger("11000100100000000000000000000000000000000000000000000000000000100000000000000000011", 2); // Hamming weight 7
            break;
        case 344:
            u = new BigInteger("1100000010000000000000000000000000000000000000000000000000001000000000000100000000011", 2); // Hamming weight 7
            break;
        case 352:
            u = new BigInteger("110000000000000001000000000000000000000000000000000000000000000000000000001000000000111", 2); // Hamming weight 7
            break;
        case 360:
            u = new BigInteger("11010000000100000001000000000000000000000000000000000000000000000010000000000000000000011", 2); // Hamming weight 8
            break;
        case 368:
            u = new BigInteger("1100100000000000000000000000000000000000001001000000000000000000000000000000000000000000011", 2); // Hamming weight 7
            break;
        case 376:
            u = new BigInteger("110001001000000000000000000000000000000000000000000000000000000000000000000000000000000000111", 2); // Hamming weight 7
            break;
        case 384:
            u = new BigInteger("11001000000000000010000000000000000000000000000000000000000000000100000000000000000000000000011", 2); // *** ISO, Hamming weight 7
            break;
        case 392:
            u = new BigInteger("1100001000000001000000000000000000000000000000000000000000000000000000000000000000100000000000011", 2); // Hamming weight 7
            break;
        case 400:
            u = new BigInteger("110000000000000000000000000000000000000000000000000000000000000000000000000000000100000001000000011", 2); // Hamming weight 6
            break;
        case 408:
            u = new BigInteger("11000010000000000000000000000000110000000000000000000000000000000000000000000000000000000000000000011", 2); // Hamming weight 7
            break;
        case 416:
            u = new BigInteger("1100000000100000000000100000000000000000000000000000000100000000000000000000000000000000000000000000011", 2); // Hamming weight 7
            break;
        case 424:
            u = new BigInteger("110001000000000000000000000000000000000000000000000000000000001000000000000000000000000000000000100000011", 2); // Hamming weight 7
            break;
        case 432:
            u = new BigInteger("11000100000000000000000000000000000000000000000000000000100000100000000000000000000000000000000000000000011", 2); // Hamming weight 7
            break;
        case 440:
            u = new BigInteger("1100000000000000000000000000000100000000000000000000000000000000000000000000100000100000000000000000000000011", 2); // Hamming weight 7
            break;
        case 448:
            u = new BigInteger("110000000000000000000000000000000000000000000000000000000000000000000000000000000100000000000001000000000000011", 2); // Hamming weight 6
            break;
        case 456:
            u = new BigInteger("11001000000000000000000000000000000000000000000000000010000000000000000000000000000000000000000100000000000000011", 2); // Hamming weight 7
            break;
        case 464:
            u = new BigInteger("1100000000000000000000000000000000000000000000000000000100000000001000000000000000000010000000000000000000000000011", 2); // Hamming weight 7
            break;
        case 472:
            u = new BigInteger("110000000000100000100100000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000011", 2); // Hamming weight 7
            break;
        case 480:
            u = new BigInteger("11000000000000000000000100000001000000000000000000000000000000000000000000000000000000000000000000000000000000000000011", 2); // Hamming weight 6
            break;
        case 488:
            u = new BigInteger("1100000001000000000000000000000000000000001000000000000000000000000000000000100000000000000000000000000000000000000000011", 2); // Hamming weight 7
            break;
        case 496:
            u = new BigInteger("110000100000000000000000000000000000000000000000000000000000000001000000000000000000001000000000000000000000000000000000011", 2); // Hamming weight 7
            break;
        case 504:
            u = new BigInteger("11000000000000000010000000000000010000000000000000000000000000000000000000100000000000000000000000000000000000000000000000011", 2); // Hamming weight 7
            break;
        case 512:
            u = new BigInteger("1100001000000000000000000000000000000000000000000000000000000000000000000000000000010000000000000000000000001000000000000000011", 2); // *** ISO, Hamming weight 7
            break;
        default:
            throw new IllegalArgumentException(invalidParams + ": " + "Field size in bits must be a multiple of 8 between 56 and 512");
        }
        //p = 36*u^4 + 36*u^3 + 24*u^2 + 6*u + 1 = (((u + 1)*6*u + 4)*u + 1)*6*u + 1
        p = u.add(_1).multiply(_6.multiply(u)).add(_4).multiply(u).add(_1).multiply(_6.multiply(u)).add(_1);
        assert (p.mod(_4).intValue() == 3 || p.mod(_9).intValue() == 4);
        assert (p.isProbablePrime(PRIMALITY_CERTAINTY));
        t = _6.multiply(u).multiply(u).add(_1); // 6*u^2 + 1
        ht = p.subtract(_1).add(t);
        //n = 36*u^4 + 36*u^3 + 18*u^2 + 6*u + 1
        n = p.add(_1).subtract(t);
        assert (n.isProbablePrime(PRIMALITY_CERTAINTY));
        // zeta = 18*u^3 + 18*u^2 + 9*u + 1;
        zeta = _9.multiply(u).multiply(u.shiftLeft(1).multiply(u.add(_1)).add(_1)).add(_1);
        zeta0 = zeta;
        zeta1 = zeta.add(_1);
        mzeta0 = zeta0.negate();
        mzeta1 = zeta1.negate();
        // rho = |36*u^3 + 18*u^2 + 6*u + 1| = |6*u*(3*u*(2*u + 1) + 1) + 1)|;
        rho = _6.multiply(u).multiply(_3.multiply(u).multiply(u.shiftLeft(1).add(_1)).add(_1)).add(_1);
        if (rho.signum() < 0) {
            rho = rho.negate();
        }
        optOrd = _6.multiply(u).add(_2);
        if (optOrd.signum() < 0) {
            optOrd = optOrd.negate();
        }
        sqrtExponent = p.add(_1).shiftRight(2); // (p + 1)/4
        cbrtExponent = p.add(p).add(_1).divide(_9); // (2*p + 1)/9
        sqrtExponent2 = p.multiply(p).add(_7).shiftRight(4); // (p^2 + 7)/16
        cbrtExponent2 = p.multiply(p).add(_2).divide(_9); // (p^2 + 2)/9
        sigma = p.subtract(_4).modPow(p.subtract(_1).subtract(p.add(_5).divide(_24)), p); // (-1/4)^((p+5)/24)
        zeta0sigma = zeta0.multiply(sigma).mod(p);
        zeta1sigma = zeta1.multiply(sigma).mod(p);
        //  2*sigma^2 = -zeta
        // -2*sigma^3 = -sigma*(2*sigma^2) = sigma*zeta
        // -4*sigma^4 = zeta+1
        // -4*sigma^5 = -4*sigma^4*sigma = (zeta+1)*sigma
        //  8*sigma^6 = -1
        //-16*sigma^9 = -2*sigma^3*(8*sigma^6) = 2*sigma^3
        invSqrtMinus2 = p.subtract(_2).modPow(p.subtract(_1).subtract(p.add(_1).shiftRight(2)), p); // 1/sqrt(-2) = (-2)^{-(p+1)/4}
        sqrtI = new BNField2(this, invSqrtMinus2, p.subtract(invSqrtMinus2), false); // sqrt(i) = (1 - i)/sqrt(-2)
        Fp2_0 = new BNField2(this, _0);
        Fp2_1 = new BNField2(this, _1);
        Fp12_0 = new BNField12(this, _0);
        Fp12_1 = new BNField12(this, _1);

        latInv = new BigInteger[4];
		latInv[0] = u.shiftLeft(1).add(_3).multiply(u).add(_1); // 2*u^2 + 3*u + 1 = (2*u + 3)*u + 1
		latInv[1] = u.multiply(_3).add(_2).multiply(u).multiply(u).shiftLeft(2).add(u); // 12*u^3 + 8*u^2 + u = ((3*u + 2)*u)*4*u + u
		latInv[2] = u.multiply(_3).add(_2).multiply(u).multiply(u).shiftLeft(1).add(u); //  6*u^3 + 4*u^2 + u = ((3*u + 2)*u)*2*u + u
		latInv[3] = u.multiply(u).shiftLeft(1).add(u).negate(); // -(2*u^2 + u)

        latRed = new BigInteger[4][4];
		/*
		    u+1,       u,      u,  -2*u,
		    2*u+1,    -u, -(u+1),    -u,
		    2*u,   2*u+1,  2*u+1, 2*u+1,
		    u-1,   4*u+2, -2*u+1,   u-1
		 */

		latRed[0][0] = u.add(_1);
		latRed[0][1] = u;
		latRed[0][2] = u;
		latRed[0][3] = u.shiftLeft(1).negate();

		latRed[1][0] = u.shiftLeft(1).add(_1);
		latRed[1][1] = u.negate();
		latRed[1][2] = u.add(_1).negate();
		latRed[1][3] = u.negate();

		latRed[2][0] = u.shiftLeft(1);
		latRed[2][1] = u.shiftLeft(1).add(_1);
		latRed[2][2] = u.shiftLeft(1).add(_1);
		latRed[2][3] = u.shiftLeft(1).add(_1);

		latRed[3][0] = u.subtract(_1);
		latRed[3][1] = u.shiftLeft(2).add(_2);
		latRed[3][2] = u.shiftLeft(1).negate().add(_1);
		latRed[3][3] = u.subtract(_1);
    }

    /**
     * Compute a square root of v (mod p) where p = 3 (mod 4).
     *
     * @return  a square root of v (mod p) if one exists, or null otherwise.
     *
     * @exception   IllegalArgumentException    if the size p of the underlying finite field does not satisfy p = 3 (mod 4).
     */
    BigInteger sqrt(BigInteger v) {
        if (!p.testBit(1)) {
            throw new IllegalArgumentException("This implementation is optimized for, and only works with, prime fields GF(p) where p = 3 (mod 4)");
        }
        if (v.signum() == 0) {
            return _0;
        }
        BigInteger r = v.modPow(sqrtExponent, p); // r = v^{(p + 1)/4}
        // test solution:
        return r.multiply(r).subtract(v).mod(p).signum() == 0 ? r : null;
    }

    /**
     * Compute a cube root of v (mod p) where p = 4 (mod 9).
     *
     * @return  a cube root of v (mod p) if one exists, or null otherwise.
     *
     * @exception   IllegalArgumentException    if the size p of the underlying finite field does not satisfy p = 4 (mod 9).
     */
    BigInteger cbrt(BigInteger v) {
        if (p.mod(_9).intValue() != 4) {
            throw new IllegalArgumentException("This implementation is optimized for, and only works with, prime fields GF(p) where p = 4 (mod 9)");
        }
        if (v.signum() == 0) {
            return _0;
        }
        BigInteger r = v.modPow(cbrtExponent, p); // r = v^{(2p + 1)/9}
        return r.multiply(r).multiply(r).subtract(v).mod(p).signum() == 0 ? r : null;
    }

    public BigInteger getN()
    {
        return n;
    }

}