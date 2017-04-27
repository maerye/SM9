/**
 * BNField12.java
 *
 * Arithmetic in the finite extension field GF(p^12) with p = 3 (mod 4) and p = 4 (mod 9).
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

public class BNField12 {

    /**
     * Convenient BigInteger constants
     */
    private static final BigInteger
        _1 = BigInteger.valueOf(1L),
        _3 = BigInteger.valueOf(3L);

    public static final String differentFields =
        "Operands are in different finite fields";

    /**
     * BN parameters (singleton)
     */
    public BNParams bn;

    /**
     * Components
     */
    public BNField2[] v;
    public BNField12 (){
        v=new BNField2[6];
    }

    BNField12(BNParams bn, BigInteger k) {
        this.bn = bn;
        v = new BNField2[6];
        v[0] = new BNField2(bn, k); // caveat: no modular reduction!
        for (int i = 1; i < 6; i++) {
        	v[i] = new BNField2(bn);
        }
    }

    public BNField12(BNParams bn, BNField2[] v) {
        this.bn = bn;
        this.v = v;
    }

    public boolean isZero() {
        for (int i = 0; i < 6; i++) {
	        if (!v[i].isZero()) {
	        	return false;
	        }
        }
        return true;
    }

    public boolean isOne() {
        if (!v[0].isOne()) {
        	return false;
        }
        for (int i = 1; i < 6; i++) {
	        if (!v[i].isZero()) {
	        	return false;
	        }
        }
        return true;
    }

    public boolean equals(Object o) {
        if (!(o instanceof BNField12)) {
            return false;
        }
        BNField12 w = (BNField12)o;
        if (bn != w.bn) { // singleton comparison
            return false;
        }
        for (int i = 0; i < 6; i++) {
	        if (!v[i].equals(w.v[i])) {
	        	return false;
	        }
        }
        return true;
    }

    public BNField12 negate() {
    	BNField2[] w = new BNField2[6];
    	for (int i = 0; i < 6; i++) {
    		w[i] = v[i].negate();
    	}
        return new BNField12(bn, w);
    }

    public BNField12 frobenius() {
    	/*
    	 * z^p = sigma*(1+i)*z
    	 * (z^2)^p = 2*sigma^2*i*z^2
    	 * (z^3)^p = -2*sigma^3*(i-1)*z^3
    	 * (z^4)^p = -4*sigma^4*z^4
    	 * (z^5)^p = -4*sigma^5*(1+i)*z^5
    	 */
        BNField2[] w = new BNField2[6];
    	w[0] = v[0].conjugate();
    	w[1] = v[1].conjugate().multiply(bn.sigma).multiplyV();
    	w[2] = v[2].conjugate().multiply(bn.mzeta0).multiplyI();
        w[3] = v[3].multiplyV().conjugate().multiply(bn.zeta0sigma);
        w[4] = v[4].conjugate().multiply(bn.zeta1);
        w[5] = v[5].conjugate().multiply(bn.zeta1sigma).multiplyV();
        return new BNField12(bn, w);
    }

	/**
	 * Compute this^((p^2)^m), the m-th conjugate of this over GF(p^2).
	 */
    public BNField12 conjugate(int m) {
    	/*
    	 * z^(p^2)  = -zeta*z
    	 * z^(p^4)  = -(zeta+1)*z = zeta^2*z
    	 * z^(p^6)  = -z
         * z^(p^8)  = zeta*z
         * z^(p^10) = (zeta+1)*z = -zeta^2*z
         *
    	 * v        = v_0 + v_1 z + v_2 z^2 + v_3 z^3 + v_4 z^4 + v_5 z^5 =>
         * v^(p^2)  = v_0 - v_1zeta z - v_2(zeta+1) z^2 - v_3 z^3 + v_4zeta z^4 + v_5(zeta+1) z^5
         * v^(p^4)  = v_0 - v_1(zeta+1) z + v_2zeta z^2 + v_3 z^3 - v_4 z^4(zeta+1) + v_5zeta z^5
         * v^(p^6)  = v_0 - v_1 z + v_2 z^2 - v_3 z^3 + v_4 z^4 - v_5 z^5
         * v^(p^8)  = v_0 + v_1zeta z - v_2(zeta+1) z^2 + v_3 z^3 + v_4zeta z^4 - v_5(zeta+1) z^5
         * v^(p^10) = v_0 + v_1(zeta+1) z + v_2zeta z^2 - v_3 z^3 - v_4 z^4(zeta+1) - v_5zeta z^5
         */
        BNField2[] w;
    	switch (m) {
    	default: // only to make the compiler happy
    	case 0:
    		return this;
    	case 1:
	    	w = new BNField2[6];
	    	w[0] = v[0];
            w[1] = v[1].multiply(bn.mzeta0);
            w[2] = v[2].multiply(bn.mzeta1);
	    	w[3] = v[3].negate();
	    	w[4] = v[4].multiply(bn.zeta0);
            w[5] = v[5].multiply(bn.zeta1);
        	return new BNField12(bn, w);
    	case 2:
	    	w = new BNField2[6];
            w[0] = v[0];
            w[1] = v[1].multiply(bn.mzeta1);
            w[2] = v[2].multiply(bn.zeta0);
            w[3] = v[3];
            w[4] = v[4].multiply(bn.mzeta1);
            w[5] = v[5].multiply(bn.zeta0);
            return new BNField12(bn, w);
    	case 3:
	    	w = new BNField2[6];
            w[0] = v[0];
            w[1] = v[1].negate();
            w[2] = v[2];
            w[3] = v[3].negate();
            w[4] = v[4];
            w[5] = v[5].negate();
            return new BNField12(bn, w);
    	case 4:
	    	w = new BNField2[6];
            w[0] = v[0];
            w[1] = v[1].multiply(bn.zeta0);
            w[2] = v[2].multiply(bn.mzeta1);
            w[3] = v[3];
            w[4] = v[4].multiply(bn.zeta0);
            w[5] = v[5].multiply(bn.mzeta1);
            return new BNField12(bn, w);
    	case 5:
	    	w = new BNField2[6];
            w[0] = v[0];
            w[1] = v[1].multiply(bn.zeta1);
            w[2] = v[2].multiply(bn.zeta0);
            w[3] = v[3].negate();
            w[4] = v[4].multiply(bn.mzeta1);
            w[5] = v[5].multiply(bn.mzeta0);
            return new BNField12(bn, w);
    	}
    }

    /*
    public BNField2 norm2() {
    	BNField12 c = this;
    	for (int i = 1; i < 6; i++) {
    		c = c.multiply(conjugate(i));
    	}
    	assert (c.v[1].isZero() && c.v[2].isZero() && c.v[3].isZero() && c.v[4].isZero() && c.v[5].isZero());
        return c.v[0];
    }

    public BigInteger norm() {
    	return norm2().norm();
    }
    //*/

    public BNField12 add(BNField12 k) {
        if (bn != k.bn) { // singleton comparison
            throw new IllegalArgumentException(differentFields);
        }
    	BNField2[] w = new BNField2[6];
    	for (int i = 0; i < 6; i++) {
    		w[i] = v[i].add(k.v[i]);
    	}
        return new BNField12(bn, w);
    }

    public BNField12 subtract(BNField12 k) {
        if (bn != k.bn) { // singleton comparison
            throw new IllegalArgumentException(differentFields);
        }
    	BNField2[] w = new BNField2[6];
    	for (int i = 0; i < 6; i++) {
    		w[i] = v[i].subtract(k.v[i]);
    	}
        return new BNField12(bn, w);
    }

    public BNField12 multiply(BNField12 k) {
        if (bn != k.bn) { // singleton comparison
            throw new IllegalArgumentException(differentFields);
        }
        if (k == this) {
            return square();
        }
        BNField2[] w = new BNField2[6];
        if (k.v[2].isZero() && k.v[4].isZero() && k.v[5].isZero()) {
            BNField2
                d00 = v[0].multiply(k.v[0]),
                d11 = v[1].multiply(k.v[1]),
                d33 = v[3].multiply(k.v[3]),
                s01 = v[0].add(v[1]),
                t01 = k.v[0].add(k.v[1]),
                u01 = d00.add(d11),
                d01 = s01.multiply(t01).subtract(u01),
                d02 = v[0].add(v[2]).multiply(k.v[0]).subtract(d00),
                d04 = v[0].add(v[4]).multiply(k.v[0]).subtract(d00),
                d13 = v[1].add(v[3]).multiply(k.v[1].add(k.v[3])).subtract(d11.add(d33)),
                d15 = v[1].add(v[5]).multiply(k.v[1]).subtract(d11),
                s23 = v[2].add(v[3]),
                d23 = s23.multiply(k.v[3]).subtract(d33),
                d35 = v[3].add(v[5]).multiply(k.v[3]).subtract(d33);
            u01 = u01.add(d01);
            BNField2
                u23 = d33.add(d23),
                d03 = s01.add(s23).multiply(t01.add(k.v[3])).subtract(u01.add(u23).add(d02).add(d13)),
                s45 = v[4].add(v[5]),
                d05 = s01.add(s45).multiply(t01).subtract(u01.add(d04).add(d15)),
                d25 = s23.add(s45).multiply(k.v[3]).subtract(u23.add(d35));
            w[0] = d15.add(d33).divideV().add(d00);
            w[1] = d25.divideV().add(d01);
            w[2] = d35.divideV().add(d02).add(d11);
            w[3] = d03;
            w[4] = d04.add(d13);
            w[5] = d05.add(d23);
        } else if (k.v[1].isZero() && k.v[4].isZero() && k.v[5].isZero()) {
            BNField2
                d00 = v[0].multiply(k.v[0]),
                d22 = v[2].multiply(k.v[2]),
                d33 = v[3].multiply(k.v[3]),
                s01 = v[0].add(v[1]),
                d01 = s01.multiply(k.v[0]).subtract(d00),
                d02 = v[0].add(v[2]).multiply(k.v[0].add(k.v[2])).subtract(d00.add(d22)),
                d04 = v[0].add(v[4]).multiply(k.v[0]).subtract(d00),
                d13 = v[1].add(v[3]).multiply(k.v[3]).subtract(d33),
                s23 = v[2].add(v[3]),
                t23 = k.v[2].add(k.v[3]),
                u23 = d22.add(d33),
                d23 = s23.multiply(t23).subtract(u23),
                d24 = v[2].add(v[4]).multiply(k.v[2]).subtract(d22),
                d35 = v[3].add(v[5]).multiply(k.v[3]).subtract(d33),
                u01 = d00.add(d01),
                d03 = s01.add(s23).multiply(k.v[0].add(t23)).subtract(u01.add(u23).add(d02).add(d13).add(d23)),
                s45 = v[4].add(v[5]),
                d05 = s01.add(s45).multiply(k.v[0]).subtract(u01.add(d04)),
                d25 = s23.add(s45).multiply(t23).subtract(u23.add(d23).add(d24).add(d35));
            w[0] = d24.add(d33).divideV().add(d00);
            w[1] = d25.divideV().add(d01);
            w[2] = d35.divideV().add(d02);
            w[3] = d03;
            w[4] = d04.add(d13).add(d22);
            w[5] = d05.add(d23);
        } else {
            BNField2
                d00 = v[0].multiply(k.v[0]),
                d11 = v[1].multiply(k.v[1]),
                d22 = v[2].multiply(k.v[2]),
                d33 = v[3].multiply(k.v[3]),
                d44 = v[4].multiply(k.v[4]),
                d55 = v[5].multiply(k.v[5]),
                s01 = v[0].add(v[1]),
                t01 = k.v[0].add(k.v[1]),
                u01 = d00.add(d11),
                d01 = s01.multiply(t01).subtract(u01),
                d02 = v[0].add(v[2]).multiply(k.v[0].add(k.v[2])).subtract(d00.add(d22)),
                d04 = v[0].add(v[4]).multiply(k.v[0].add(k.v[4])).subtract(d00.add(d44)),
                d13 = v[1].add(v[3]).multiply(k.v[1].add(k.v[3])).subtract(d11.add(d33)),
                d15 = v[1].add(v[5]).multiply(k.v[1].add(k.v[5])).subtract(d11.add(d55)),
                s23 = v[2].add(v[3]),
                t23 = k.v[2].add(k.v[3]),
                u23 = d22.add(d33),
                d23 = s23.multiply(t23).subtract(u23),
                d24 = v[2].add(v[4]).multiply(k.v[2].add(k.v[4])).subtract(d22.add(d44)),
                d35 = v[3].add(v[5]).multiply(k.v[3].add(k.v[5])).subtract(d33.add(d55)),
                s45 = v[4].add(v[5]),
                t45 = k.v[4].add(k.v[5]),
                u45 = d44.add(d55),
                d45 = s45.multiply(t45).subtract(u45);
            u01 = u01.add(d01);
            u23 = u23.add(d23);
            u45 = u45.add(d45);
            BNField2
                d03 = s01.add(s23).multiply(t01.add(t23)).subtract(u01.add(u23).add(d02).add(d13)),
                d05 = s01.add(s45).multiply(t01.add(t45)).subtract(u01.add(u45).add(d04).add(d15)),
                d25 = s23.add(s45).multiply(t23.add(t45)).subtract(u23.add(u45).add(d24).add(d35));
            w[0] = d15.add(d24).add(d33).divideV().add(d00);
            w[1] = d25.divideV().add(d01);
            w[2] = d35.add(d44).divideV().add(d02).add(d11);
            w[3] = d45.divideV().add(d03);
            w[4] = d55.divideV().add(d04).add(d13).add(d22);
            w[5] = d05.add(d23);
        }
        return new BNField12(bn, w);
    }

    public BNField12 multiply(BigInteger k) {
    	BNField2[] w = new BNField2[6];
    	for (int i = 0; i < 6; i++) {
    		w[i] = v[i].multiply(k);
    	}
        return new BNField12(bn, w);
    }

    public BNField12 multiply(BNField2 k) {
    	BNField2[] w = new BNField2[6];
    	for (int i = 0; i < 6; i++) {
    		w[i] = v[i].multiply(k);
    	}
        return new BNField12(bn, w);
    }

    public BNField12 square() {
        BNField2
            d00 = v[0].square(),
            d11 = v[1].square(),
            d22 = v[2].square(),
            d33 = v[3].square(),
            d44 = v[4].square(),
            d55 = v[5].square(),
            s01 = v[0].add(v[1]),
            t01 = d00.add(d11),
            d01 = s01.square().subtract(t01),
            d02 = v[0].add(v[2]).square().subtract(d00.add(d22)),
            d04 = v[0].add(v[4]).square().subtract(d00.add(d44)),
            d13 = v[1].add(v[3]).square().subtract(d11.add(d33)),
            d15 = v[1].add(v[5]).square().subtract(d11.add(d55)),
            s23 = v[2].add(v[3]),
            t23 = d22.add(d33),
            d23 = s23.square().subtract(t23),
            d24 = v[2].add(v[4]).square().subtract(d22.add(d44)),
            d35 = v[3].add(v[5]).square().subtract(d33.add(d55)),
            s45 = v[4].add(v[5]),
            t45 = d44.add(d55),
            d45 = s45.square().subtract(t45);
        t01 = t01.add(d01);
        t23 = t23.add(d23);
        t45 = t45.add(d45);
        BNField2
            d03 = s01.add(s23).square().subtract(t01.add(t23).add(d02).add(d13)),
            d05 = s01.add(s45).square().subtract(t01.add(t45).add(d04).add(d15)),
            d25 = s23.add(s45).square().subtract(t23.add(t45).add(d24).add(d35));
        BNField2[] w = new BNField2[6];
        w[0] = d15.add(d24).add(d33).divideV().add(d00);
        w[1] = d25.divideV().add(d01);
        w[2] = d35.add(d44).divideV().add(d02).add(d11);
        w[3] = d45.divideV().add(d03);
        w[4] = d55.divideV().add(d04).add(d13).add(d22);
        w[5] = d05.add(d23);
        return new BNField12(bn, w);
    }

    public BNField12 inverse() throws ArithmeticException {
    	BNField12 c = conjugate(1);
    	for (int i = 2; i < 6; i++) {
    		c = c.multiply(conjugate(i));
    	}
    	BNField12 n = c.multiply(this);
    	assert (n.v[1].isZero() && n.v[2].isZero() && n.v[3].isZero() && n.v[4].isZero() && n.v[5].isZero());
    	c = c.multiply(n.v[0].inverse());
        return c;
    }

    private BNField12 plainExp(BigInteger k) {
        BNField12 w = this;
        for (int i = k.bitLength()-2; i >= 0; i--) {
            w = w.square();
            if (k.testBit(i)) {
                w = w.multiply(this);
            }
        }
        return w;
    }

    public BNField12 finExp() {
    	BNField12 f = this;
        // p^12 - 1 = (p^6 - 1)*(p^2 + 1)*(p^4 - p^2 + 1)
        try {
            f = f.conjugate(3).multiply(f.inverse()); // f = f^(p^6 - 1)
        } catch (ArithmeticException x) {
            f = this; // this can only happen when this instance is not invertible, i.e. zero
        }
        f = f.conjugate(1).multiply(f); // f = f^(p^2 + 1)
        assert (f.inverse().equals(f.conjugate(3)));
        BNField12 a;
        if (bn.u.signum() >= 0) {
        	a = f.plainExp(bn.optOrd.add(_3)).conjugate(3);
        } else {
            a = f.plainExp(bn.optOrd.add(_3).negate());
        }
		BNField12 b = a.frobenius().multiply(a);
		BNField12 c = f.frobenius();
		BNField12 d = f.conjugate(1);
		BNField12 e = c.multiply(f);
		f = c.conjugate(1).multiply(b.multiply(c.square()).multiply(d).plainExp(bn.t)).multiply(b).multiply(a).multiply(f.multiply(e.square()).square().square()).multiply(e);
		return f;
    }

    /**
     * Compute this^ks + Y^kr.
     *
     */
    public BNField12 simultaneous(BigInteger ks, BigInteger kr, BNField12 Y) {
        BNField12[] hV = new BNField12[16];
        BNField12 P = this;
        if (ks.signum() < 0) {
        	ks = ks.negate();
        	P = P.conjugate(3);
        }
        if (kr.signum() < 0) {
        	kr = kr.negate();
        	Y = Y.conjugate(3);
        }
        hV[0] = bn.Fp12_1;
        hV[1] = P;
        hV[2] = Y;
        hV[3] = P.multiply(Y);
        for (int i = 4; i < 16; i += 4) {
            hV[i] = hV[i >> 2].square();
            hV[i + 1] = hV[i].multiply(hV[1]);
            hV[i + 2] = hV[i].multiply(hV[2]);
            hV[i + 3] = hV[i].multiply(hV[3]);
        }
        int t = Math.max(kr.bitLength(), ks.bitLength());
        BNField12 R = bn.Fp12_1;
        for (int i = (((t + 1) >> 1) << 1) - 1; i >= 0; i -= 2) {
            int j = (kr.testBit(i  ) ? 8 : 0) |
                    (ks.testBit(i  ) ? 4 : 0) |
                    (kr.testBit(i-1) ? 2 : 0) |
                    (ks.testBit(i-1) ? 1 : 0);
            R = R.square().square().multiply(hV[j]);
        }
        return R;
    }

	public BNField12 exp(BigInteger k) {
		BNField12 P = this;
        if (k.signum() < 0) {
            k = k.negate();
            P = P.conjugate(3);
        }
		BigInteger r = bn.u.shiftLeft(1).add(_1); // 2*u + 1
		BigInteger t = bn.u.multiply(_3).add(_1).multiply(bn.u.shiftLeft(1)); // (3*u + 1)*2*u = 6*u^2 + 2*u
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
		BNField12 f = P.conjugate(1);
		BNField2[] w = new BNField2[6];
		/*
		f^rho[0] = f^{p^2}[0] = f[0]
		f^rho[1] = -f^{p^2}[1]
		f^rho[2] = f^{p^2}[2]
		f^rho[3] = f[3]
		f^rho[4] = f^{p^2}[4]
		f^rho[5] = -f^{p^2}[5]
		 */
		w[0] = P.v[0];
		w[1] = f.v[1].negate();
		w[2] = f.v[2];
		w[3] = P.v[3];
		w[4] = f.v[4];
		w[5] = f.v[5].negate();
		BNField12 y = new BNField12(bn, w);
		return P.simultaneous(sr, st, y);
	}

    public BNField12 simultaneous(BigInteger kP, BNField12 P, BigInteger kQ, BNField12 Q, BigInteger kR, BNField12 R, BigInteger kS, BNField12 S) {
        BNField12[] hV = new BNField12[16];
        if (kP.signum() < 0) {
        	kP = kP.negate(); P = P.conjugate(3);
        }
        if (kQ.signum() < 0) {
        	kQ = kQ.negate(); Q = Q.conjugate(3);
        }
        if (kR.signum() < 0) {
        	kR = kR.negate(); R = R.conjugate(3);
        }
        if (kS.signum() < 0) {
        	kS = kS.negate(); S = S.conjugate(3);
        }
        hV[0] = bn.Fp12_1;
        hV[1] = P; hV[2] = Q; hV[4] = R; hV[8] = S;
		for (int i = 2; i < 16; i <<= 1) {
			for (int j = 1; j < i; j++) {
		        hV[i + j] = hV[i].multiply(hV[j]);
			}
		}
        int t = Math.max(Math.max(kP.bitLength(), kQ.bitLength()), Math.max(kR.bitLength(), kS.bitLength()));
        BNField12 V = bn.Fp12_1;
        for (int i = t - 1; i >= 0; i--) {
            int j = (kS.testBit(i) ?   8 : 0) |
                    (kR.testBit(i) ?   4 : 0) |
                    (kQ.testBit(i) ?   2 : 0) |
                    (kP.testBit(i) ?   1 : 0);
            V = V.square().multiply(hV[j]);
        }
        return V;
    }

    public BNField12 fastSimultaneous(BigInteger ks, BigInteger kr, BNField12 y) {
		BigInteger m = bn.t.subtract(_1).abs();
    	BNField12 g = this;
		if (ks.signum() < 0) {
			g = g.conjugate(3);
			ks = ks.negate();
		}
		if (kr.signum() < 0) {
			y = y.conjugate(3);
			kr = kr.negate();
		}
		BNField12 gg = g.frobenius().conjugate((bn.t.signum() > 0) ? 0 : 3);
		BNField12 yy = y.frobenius().conjugate((bn.t.signum() > 0) ? 0 : 3);
		return simultaneous(ks.mod(m), g, ks.divide(m), gg, kr.mod(m), y, kr.divide(m), yy);
    }

    public String toString() {
        return "(" + v[0] + ", " + v[1] + ", " + v[2] + ", " + v[3] + ", " + v[4] + ", " + v[5] + ")";
    }
}
