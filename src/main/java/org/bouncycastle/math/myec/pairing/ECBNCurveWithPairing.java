/**
 *
 * ECBNCurveWithAtePairing.java
 *
 * Ate pairing on curves for BNCurve over F_p.
 * 
 * Copyright (C) Paulo S. L. M. Barreto.
 * Adapted by Bart Jacobs (www.cs.ru.nl/~bart), march 2009.
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
 *
 */

package org.bouncycastle.math.myec.pairing;

import java.math.BigInteger;
import org.bouncycastle.math.ec.*;
import org.bouncycastle.math.myec.*;

public class ECBNCurveWithPairing extends ECBNCurve {

    /**
     * Twisted curve y^2 = x^3 + 3*(1+i) over F_p^2 , wrt. current curve y^2 =
     * x^3 + 3.over F_p
     */
    private ECCurveFp2 twistedCurve;

    /**
     * Returns the twisted curve, associated with the current one.
     */
    public ECCurveFp2 getTwistedCurve() {
	return twistedCurve;
    }

    public ECBNCurveWithPairing(BigInteger fieldBits) {
	super(fieldBits);
	BigInteger q = this.getQ();
	twistedCurve = new ECCurveFp2(q,
	// A parameter is 0
		new ECFieldElementFp2(q, BigInteger.valueOf(0)),
		// B parameter 3*(1+i)
		new ECFieldElementFp2(new ECFieldElement.Fp(q, BigInteger
			.valueOf(3)), new ECFieldElement.Fp(q, BigInteger
			.valueOf(3)), false));
    }

    public ECFieldElementFp12 AtePairing(ECPoint P, ECPoint Q) {
	long start = System.nanoTime();
	if (
	// Lots of parameter checks, just to be sure
	P instanceof ECPoint.Fp
		&& Q instanceof ECPointFp2
		&& P.getCurve() instanceof ECBNCurveWithPairing
		&& Q.getCurve()
			.equals(
				((ECBNCurveWithPairing) P.getCurve())
					.getTwistedCurve())
		&& curveContains(P.getCurve(), P.getX(), P.getY())
		&& curveContains(Q.getCurve(), Q.getX(), Q.getY())) {
	    BigInteger q = this.getQ(), trace_min_one = this
		    .getTrace().subtract(BigInteger.valueOf(1));
	    ECCurve curve12 = new ECCurveFp12(q, new ECFieldElementFp12(
		    (ECFieldElement.Fp) this.getA()), new ECFieldElementFp12(
		    (ECFieldElement.Fp) this.getB()));
	    ECFieldElementFp2 zero = new ECFieldElementFp2(q, BigInteger
		    .valueOf(0)), one = new ECFieldElementFp2(q, BigInteger
		    .valueOf(1));
	    ECFieldElement f = new ECFieldElementFp12(zero, zero, zero, zero,
		    zero, one, false);
	    ECFieldElementFp2[] w = new ECFieldElementFp2[6];
	    w[2] = w[4] = w[5] = zero;
	    ECFieldElement lam, N, D, lP;
	    ECFieldElement // in Fp12
	    PX = new ECFieldElementFp12(new ECFieldElementFp2(
		    (ECFieldElement.Fp) P.getX()), 0), PY = new ECFieldElementFp12(
		    new ECFieldElementFp2((ECFieldElement.Fp) P.getY()), 0),
	    // twisted version
	    QX = new ECFieldElementFp12((ECFieldElementFp2) Q.getX(), 2), QY = new ECFieldElementFp12(
		    (ECFieldElementFp2) Q.getY(), 3);
	    System.out.println("Curve12 contains P: "
		    + curveContains(curve12, PX, PY));
	    System.out.println("Curve12 contains Q: "
		    + curveContains(curve12, QX, QY));
	    ECFieldElement Tx, Ty;
	    ECPoint Q12 = new ECPointFp12(curve12, QX, QY), T = Q12;
	    if (P.isInfinity() || Q.isInfinity()) {
		return (ECFieldElementFp12) f;
	    } else {
		for (int i = trace_min_one.bitLength() - 2; i >= 0; i--) {
		    /*
		     * Loop invariant: T.equals(Q.multiply(ord.shiftRight(i+1)))
		     */
		    System.out.println(i);
		    // if ( ! T.equals(Q.multiply(ord.shiftRight(i+1))) )
		    // { System.out.println("Invariant check fails");};
		    Tx = T.getX();
		    Ty = T.getY();
		    // Tx = new ECFieldElementFp12((ECFieldElementFp2)T.getX(),
		    // 2);
		    // Ty = new ECFieldElementFp12((ECFieldElementFp2)T.getY(),
		    // 3);
		    // lambda = n/d
		    // Question: add(A) missing in n?? Magma: 3*U[1]^2, so
		    // U[1]=Tx
		    // n =
		    // ((ECFieldElementFp2)Tx.square()).multiply(BigInteger.valueOf(3));
		    // d =
		    // ((ECFieldElementFp2)Ty).multiply(BigInteger.valueOf(2));
		    // lam = n.divide(d);
		    N = ((ECFieldElementFp12) Tx.square()).multiply(BigInteger
			    .valueOf(3));
		    D = ((ECFieldElementFp12) Ty).multiply(BigInteger
			    .valueOf(2));
		    lam = N.divide(D);
		    // w[0] = d * (q - Py)
		    // w[0] =
		    // (ECFieldElementFp2)((ECFieldElementFp2)d).multiply(Py.negate().toBigInteger());
		    // w[0] = new
		    // ECFieldElementFp2((ECFieldElement.Fp)Py.negate());
		    // w[1] = n * Px
		    // w[1] =
		    // (ECFieldElementFp2)(((ECFieldElementFp2)n).multiply(Px.toBigInteger()));
		    // w[1] =
		    // (ECFieldElementFp2)(((ECFieldElementFp2)lam).multiply(Px.toBigInteger()));
		    // w[3] = d * Ty - n * Tx
		    // w[3] =
		    // (ECFieldElementFp2)(((ECFieldElementFp2)d).multiply(Ty.toBigInteger()).subtract(((ECFieldElementFp2)n).multiply(Tx.toBigInteger())));
		    // w[3] =
		    // (ECFieldElementFp2)(Ty.subtract(((ECFieldElementFp2)lam).multiply(Tx.toBigInteger())));
		    // w[2] = w[4] = w[5] = zero;
		    lP = PY.subtract(Ty)
			    .subtract(lam.multiply(PX.subtract(Tx)));
		    // vP =
		    // PX.subtract(lam.multiply(lam).subtract(((ECFieldElementFp12)Tx).multiply(BigInteger.valueOf(2))));
		    // f = f.square().multiply(new ECFieldElementFp12(w,
		    // false));
		    // W =
		    // N.multiply(PX.subtract(Tx)).add(D.multiply(Ty.subtract(PY)));
		    // f = f.square().multiply(W);
		    f = f.multiply(f).multiply(lP);// .divide(vP);
						   // //.multiply(((ECFieldElementFp2)vQ).conjugate());
		    T = T.twice();
		    if (trace_min_one.testBit(i)) {
			Tx = T.getX();
			Ty = T.getY();
			// Tx = new
			// ECFieldElementFp12((ECFieldElementFp2)T.getX(), 0);
			// Ty = new
			// ECFieldElementFp12((ECFieldElementFp2)T.getY(), 0);
			// n = Qy.subtract(Ty);
			// d = Qx.subtract(Tx);
			// lam = n.divide(d);
			D = QX.subtract(Tx);
			N = QY.subtract(Ty);
			lam = N.divide(D);
			lP = PY.subtract(Ty).subtract(
				lam.multiply(PX.subtract(Tx)));
			// vP =
			// PX.subtract(lam.multiply(lam).subtract(Tx).subtract(PX));
			// w[0] =
			// (ECFieldElementFp2)((ECFieldElementFp2)d).multiply(Py.negate().toBigInteger());
			// w[0] = new
			// ECFieldElementFp2((ECFieldElement.Fp)Py.negate());
			// w[1] =
			// (ECFieldElementFp2)(((ECFieldElementFp2)n).multiply(Px.toBigInteger()));
			// w[1] =
			// (ECFieldElementFp2)(((ECFieldElementFp2)lam).multiply(Px.toBigInteger()));
			// w[3] =
			// (ECFieldElementFp2)(((ECFieldElementFp2)d).multiply(Ty.toBigInteger()).subtract(((ECFieldElementFp2)n).multiply(Tx.toBigInteger())));
			// w[3] =
			// (ECFieldElementFp2)(Ty.subtract(((ECFieldElementFp2)lam).multiply(Tx.toBigInteger())));
			// w[2] = w[4] = w[5] = zero;
			// f = f.multiply(new ECFieldElementFp12(w, false));
			// W =
			// N.multiply(PX.subtract(Tx)).add(D.multiply(Ty.subtract(PY)));
			// f = f.multiply(W);
			f = f.multiply(lP);// .divide(vP);
					   // //.multiply(((ECFieldElementFp2)vQ).conjugate());
			T = (ECPointFp12) T.add(Q12);
		    }
		}
		;
		/*
		 * The next exponent takes 90% of computation time
		 */
		// f = ((ECFieldElementFp12)f).pow(finpow);
		f = finalExp(f);
		long duration = System.nanoTime() - start;
		System.out.format("## AtePairing %.1f millisec\n",
			duration / 1000000.0);
		return (ECFieldElementFp12) f;
	    }
	} else {
	    throw new IllegalArgumentException(
		    "Ate pairing over F_p^2 requires points over F_p^2");
	}
    }

    public ECFieldElementFp12 AtePairingInFp12(ECPoint P, ECPoint Q) {
	long start = System.nanoTime();
	if (
	// Lots of parameter checks, just to be sure
	P instanceof ECPoint.Fp
		&& Q instanceof ECPointFp2
		&& P.getCurve() instanceof ECBNCurveWithPairing
		&& Q.getCurve()
			.equals(
				((ECBNCurveWithPairing) P.getCurve())
					.getTwistedCurve())
		&& curveContains(P.getCurve(), P.getX(), P.getY())
		&& curveContains(Q.getCurve(), Q.getX(), Q.getY())) {
	    BigInteger q = this.getQ();
	    ECCurve curve12 = new ECCurveFp12(q, new ECFieldElementFp12(
		    (ECFieldElement.Fp) this.getA()), new ECFieldElementFp12(
		    (ECFieldElement.Fp) this.getB()));
	    /*
	     * Turn P into point over Fp^12
	     */
	    ECPoint.Fp P1 = (ECPoint.Fp) P;
	    P = new ECPointFp12(curve12, new ECFieldElementFp12(
		    (ECFieldElement.Fp) P1.getX()), new ECFieldElementFp12(
		    (ECFieldElement.Fp) P1.getY()));
	    System.out.println("P contained: "
		    + curveContains(curve12, P.getX(), P.getY()));
	    /*
	     * Turn Q into TWISTED point over Fp^12
	     */
	    ECPointFp2 Q2 = (ECPointFp2) Q;
	    Q = new ECPointFp12(curve12,
		    // Note twisted mapping used, via places at 2 and at 3
		    new ECFieldElementFp12((ECFieldElementFp2) Q2.getX(), 2),
		    new ECFieldElementFp12((ECFieldElementFp2) Q2.getY(), 3));
	    System.out.println("Q contained: "
		    + curveContains(curve12, Q.getX(), Q.getY()));
	    BigInteger trace_min_one = this.getTrace().subtract(
		    BigInteger.valueOf(1));
	    System.out.println("Trace-1 is: " + trace_min_one.testBit(0)); // even
	    // Q.multiply(order.add(BigInteger.valueOf(1))).equals(Q));
	    ECFieldElement m = new ECFieldElementFp12(new ECFieldElement.Fp(q,
		    BigInteger.valueOf(1)));
	    ECPoint T = Q;
	    if (P.isInfinity() || Q.isInfinity()) {
		return (ECFieldElementFp12) m;
	    } else {
		for (int i = trace_min_one.bitLength() - 2; i >= 0; i--) {
		    System.out.println(i + "  ");
		    /*
		     * Loop invariant:
		     * iterQ.equals(Q.multiply(trace_min_one.shiftRight(i+1)))
		     */
		    // if ( !
		    // iterQ.equals(Q.multiply(trace_min_one.shiftRight(i+1))) )
		    // { System.out.println("Invariant check fails");};
		    m = m.square().multiply(lineFunctionInFp12(T, T, P));
		    T = T.twice();
		    if (trace_min_one.testBit(i)) {
			m = m.multiply(lineFunctionInFp12(T, Q, P));
			T = T.add(Q);
		    }
		}
		;
	    }
	    ;
	    /*
	     * The next exponent takes 90% of computation time
	     */
	    // m = ((ECFieldElementFp12)m).pow(finpow);
	    m = finalExp(m);
	    long duration = System.nanoTime() - start;
	    System.out.format("## BN AtePairing in F_p^12 %.1f millisec\n",
		    duration / 1000000.0);
	    return (ECFieldElementFp12) m;

	} else {
	    throw new IllegalArgumentException(
		    "Ate pairing over F_p^2 requires points over F_p^2");
	}
    }

    ECFieldElement lineFunctionInFp12(ECPoint U, ECPoint V, ECPoint R) {
	if (((ECPointFp12) U).isZero() || ((ECPointFp12) V).isZero()
		|| ((ECPointFp12) R).isZero() || U.negate().equals(V)) {
	    return new ECFieldElementFp12(new ECFieldElementFp2(this.getQ(),
		    BigInteger.valueOf(1)), 0);
	} else {
	    ECFieldElement n, d; // nominator and denominator in lambda = n/d
	    if (U.equals(V)) {
		n = ((ECFieldElementFp12) U.getX().square())
			.multiply(BigInteger.valueOf(3));
		d = ((ECFieldElementFp12) U.getY()).multiply(BigInteger
			.valueOf(2));
	    } else {
		n = V.getY().subtract(U.getY());
		d = V.getX().subtract(U.getX());
	    }
	    ;
	    return n.multiply(R.getX().subtract(U.getX())).add(
		    d.multiply(U.getY().subtract(R.getY())));
	}
    }

    public ECFieldElementFp12 TatePairingInFp12(ECPoint P, ECPoint Q) {
	long start = System.nanoTime();
	ECCurve curve12 = new ECCurveFp12(this.getQ(), new ECFieldElementFp12(
		(ECFieldElement.Fp) this.getA()), new ECFieldElementFp12(
		(ECFieldElement.Fp) this.getB()));
	if (P instanceof ECPoint.Fp) {
	    ECPoint.Fp P1 = (ECPoint.Fp) P;
	    P = new ECPointFp12(curve12, new ECFieldElementFp12(
		    (ECFieldElement.Fp) P1.getX()), new ECFieldElementFp12(
		    (ECFieldElement.Fp) P1.getY()));
	}
	;
	if (Q instanceof ECPointFp2) {
	    ECPointFp2 Q2 = (ECPointFp2) Q;
	    Q = new ECPointFp12(curve12,
		    // Note twisted mapping used, via places at 2 and at 3
		    new ECFieldElementFp12((ECFieldElementFp2) Q2.getX(), 2),
		    new ECFieldElementFp12((ECFieldElementFp2) Q2.getY(), 3));

	}
	;
	if (!(P instanceof ECPointFp12) || !(Q instanceof ECPointFp12)) {
	    throw new IllegalArgumentException(
		    "Tate pairing over F_p^12 requires points over F_p^12");
	} else {
	    BigInteger r = this.getOrder();
	    // recursive variable, with initial values: one and P.
	    ECFieldElement m = new ECFieldElementFp12(new ECFieldElement.Fp(
		    this.getQ(), BigInteger.valueOf(1)));
	    ECPoint iterP = P;
	    for (int i = r.bitLength() - 2; i >= 0; i--) {
		System.out.println(i);
		/*
		 * Loop invariant is: iterP = [ r.shiftRight(i+1) ] P
		 * 
		 * Including this invariant as check in the next line leads to
		 * enormous slowdown
		 */
		// if ( !( iterP.equals(P.multiply(r.shiftRight(i+1))) ) ) {
		// System.out.println("Invariant broken!!"); break; };
		m = m.square().multiply(lineFunctionInFp12(iterP, iterP, Q));
		iterP = iterP.twice();
		if (r.testBit(i)) {
		    if (i == 0) {
			/*
			 * Final step in pairing computation, where iterP =
			 * [r-1]P holds. Nothing needs to be done.
			 */
		    } else {
			m = m.multiply(lineFunctionInFp12(iterP, P, Q));
			iterP = iterP.add(P);
		    }
		}
		;
	    }
	    ;
	    // Make sure that m is of order r in F_p^12, via m <- m^((p^12 -
	    // 1)/r)
	    m = finalExp(m);
	    long duration = System.nanoTime() - start;
	    System.out.format("## BN TatePairing in F_p^12 %.1f millisec\n",
		    duration / 1000000.0);
	    return (ECFieldElementFp12) m;
	}
    }

    public ECFieldElementFp12 TatePairing(ECPoint P, ECPoint Q) {
	long start = System.nanoTime();
	if (!(P instanceof ECPoint.Fp) || !(Q instanceof ECPointFp2)) {
	    throw new IllegalArgumentException(
		    "Tate pairing over F_p^12 requires points over F_p and F_p^2");
	} else {
	    // Shorthands
	    ECFieldElement Px = P.getX(), Py = P.getY(), Qx = Q.getX(), Qy = Q
		    .getY(), Rx, Ry;
	    BigInteger q = ((ECFieldElement.Fp) Px).getQ(), r = this.getOrder();
	    // Abbreviations for constants, for convenience
	    ECFieldElement ZERO_Fp = new ECFieldElement.Fp(q, BigInteger
		    .valueOf(0)), THREE_Fp = new ECFieldElement.Fp(q,
		    BigInteger.valueOf(3)), TWO_Fp = new ECFieldElement.Fp(q,
		    BigInteger.valueOf(2)), ZERO_Fp2 = new ECFieldElementFp2(q,
		    BigInteger.valueOf(0)), ONE_Fp12 = new ECFieldElementFp12(
		    new ECFieldElement.Fp(q, BigInteger.valueOf(1)));
	    // local variables used in this method
	    ECFieldElement N, D;
	    ECFieldElementFp2[] w = new ECFieldElementFp2[6];
	    w[1] = w[4] = w[5] = (ECFieldElementFp2) ZERO_Fp2;
	    ECPoint R = P;
	    ECFieldElement m = ONE_Fp12;
	    for (int i = r.bitLength() - 2; i >= 0; i--) {
		/*
		 * Loop invariant is: R = [ r.shiftRight(i+1) ] P
		 * 
		 * Including this invariant as check in the next line leads to
		 * enormous slowdown
		 */
		// if ( !( R.equals(P.multiply(r.shiftRight(i+1))) ) ) {
		// System.out.println("Invariant broken!!"); break; };
		Rx = R.getX();
		Ry = R.getY();
		N = ((ECFieldElement.Fp) Rx.multiply(Rx)).multiply(THREE_Fp);
		D = ((ECFieldElement.Fp) Ry).multiply(TWO_Fp);
		w[0] = new ECFieldElementFp2(ZERO_Fp, Ry.negate().multiply(D)
			.add(Rx.multiply(N)), false);
		w[2] = (ECFieldElementFp2) ((ECFieldElementFp2) Qx.negate())
			.multiply(N.toBigInteger());
		w[3] = (ECFieldElementFp2) ((ECFieldElementFp2) Qy).multiply(D
			.toBigInteger());
		m = m.square().multiply(new ECFieldElementFp12(w, false));
		R = R.twice();
		if (r.testBit(i)) {
		    if (i == 0) {
			/*
			 * System.out.println("Final step in pairing computation, where R = [r-1]P holds: "
			 * +
			 * R.equals(P.multiply(r.subtract(BigInteger.valueOf(1)
			 * )))); Nothing needs to be done: lQ and vQ values are
			 * both 1.
			 */
		    } else {
			Rx = R.getX();
			Ry = R.getY();
			N = Py.subtract(Ry);
			D = Px.subtract(Rx);
			w[0] = new ECFieldElementFp2(ZERO_Fp, Ry.negate()
				.multiply(D).add(Rx.multiply(N)), false);
			w[2] = (ECFieldElementFp2) ((ECFieldElementFp2) Qx
				.negate()).multiply(N.toBigInteger());
			w[3] = (ECFieldElementFp2) ((ECFieldElementFp2) Qy)
				.multiply(D.toBigInteger());
			m = m.multiply(new ECFieldElementFp12(w, false));
			R = R.add(P);
		    }
		}
		;
	    }
	    ;
	    long duration = System.nanoTime() - start;
	    // Final exponent: m <- m^((p^12 - 1)/r)
	    m = finalExp(m);
	    duration = System.nanoTime() - start;
	    System.out.format("## BN TatePairing %.1f millisec\n",
		    duration / 1000000.0);
	    return (ECFieldElementFp12) m;
	}
    }

    /**
     * f <- f^((p^12 - 1)/r)
     * 
     */
    private ECFieldElement finalExp(ECFieldElement f) {
	BigInteger zeta = this.getZeta(), u = this.getIndex(), t = this
		.getTrace();
	f = ((ECFieldElementFp12) f).conjugate(3, zeta).multiply(
		((ECFieldElementFp12) f).invert(zeta));
	// f <- f^2 + 1
	f = ((ECFieldElementFp12) f).conjugate(1, zeta).multiply(f);
	// finv = f^{-1}
	ECFieldElement finv = ((ECFieldElementFp12) f).invert(zeta);
	// fp = f^p
	ECFieldElement fp = ((ECFieldElementFp12) f).frobenius(zeta);
	// fp3 = f^(p^3)
	ECFieldElement fp3 = ((ECFieldElementFp12) fp).conjugate(1, zeta);
	// fp2 = f^(p^2)
	ECFieldElement fp2 = ((ECFieldElementFp12) f).conjugate(1, zeta);
	// e = f^(p+1)
	ECFieldElement fp1 = ((ECFieldElementFp12) fp).multiply(f);
	ECFieldElement a = ((ECFieldElementFp12) finv).pow(u.multiply(
		BigInteger.valueOf(6)).add(BigInteger.valueOf(5)));
	// b = a^(p+1)
	ECFieldElement b = ((ECFieldElementFp12) ((ECFieldElementFp12) a)
		.frobenius(zeta)).multiply(a);
	f = fp3.multiply(
		((ECFieldElementFp12) b.multiply(fp.square()).multiply(fp2))
			.pow(t)).multiply(b).multiply(a).multiply(
		((ECFieldElementFp12) fp1).pow(BigInteger.valueOf(9)))
		.multiply(f.square().square());
	return f;
    }

}