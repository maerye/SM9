/**
 *
 * ECCurveFp2WithTatePairing.java
 *
 * Tate pairing on curves over field F_p^2
 * 
 * Copyright (C) Bart Jacobs (www.cs.ru.nl/~bart), march 2009.
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
import org.bouncycastle.math.myec.ECFieldElementFp2;
import org.bouncycastle.math.myec.ECPointFp2;

public class ECCurveWithTatePairing extends ECCurve.Fp {

    /**
     * Parameter of the pairing
     */
    private BigInteger r;

    /**
     * Twisted curve, wrt. to this.curve, i.e. with negated B parameter
     */
    private Fp twistedCurve;

    /**
     * Returns the order r
     */
    public BigInteger getR() {
	return r;
    }

    /**
     * Returns the twisted curve, associated with the current one.
     */
    public Fp getTwistedCurve() {
	return twistedCurve;
    }

    /**
     * Creates a curve from standard curve parameter, with an extra parameter
     * for pairing
     */
    public ECCurveWithTatePairing(BigInteger q, BigInteger a, BigInteger b,
	    BigInteger r) {
	super(q, a, b);
	this.r = r;
	this.twistedCurve = new Fp(q, a, b.negate());
    }

    // /**
    // * Creates a curve from a given curve over F_p, with an extra parameter
    // for pairing
    // */
    // public ECCurveWithTatePairing(ECCurve c, BigInteger r)
    // {
    // super((ECCurve.Fp)c);
    // this.r = r;
    // }

    /**
     * Computes the pairing value for two points on the curve. <br>
     * For reasons of efficiency, no checks are performed on the parameter
     * points.
     * 
     * @param P
     *            Point that must <br>
     *            <ul>
     *            <li>be on the current curve
     *            <li>be instances of F_p^2, i.e. of ECFieldElementFp2
     *            <li>have order r
     *            </ul>
     * @param Q
     *            Point that must <br>
     *            <ul>
     *            <li>be on the current curve
     *            <li>be instances of F_p^2, i.e. of ECFieldElementFp2
     *            </ul>
     * 
     *            Conjugates are used as described in the article Michael Scott,
     *            Implementing Cryptographic Pairings.
     */
    public ECFieldElementFp2 TatePairing(ECPoint P, ECPoint Q) {
	long start = System.nanoTime();
	if (P instanceof ECPoint.Fp) {
	    P = new ECPointFp2((ECPoint.Fp) P);
	}
	;
	if (!(P instanceof ECPointFp2) || !(Q instanceof ECPointFp2)) {
	    throw new IllegalArgumentException(
		    "Tate pairing over F_p^2 requires points over F_p^2");
	} else {
	    ECFieldElement Px = P.getX(), Py = P.getY(), Qx = Q.getX(), Qy = Q
		    .getY();
	    // System.out.println(P + "     and     " + Q);
	    // curve and curve parameter
	    // ECFieldElement A = new
	    // ECFieldElementFp2((ECFieldElement.Fp)this.getA());
	    ECFieldElement A = P.getCurve().getA();
	    BigInteger p = ((ECFieldElementFp2) Px).getPrime();
	    // Abreviations for constants, for convenience
	    ECFieldElementFp2 ONE = new ECFieldElementFp2(p, BigInteger
		    .valueOf(1)), TWO = new ECFieldElementFp2(p, BigInteger
		    .valueOf(2)), THREE = new ECFieldElementFp2(p, BigInteger
		    .valueOf(3));
	    // local variables used in this method
	    ECFieldElement lambda, R1X, R1Y, lQ;
	    ECPoint R = P;
	    ECFieldElement m = ONE;
	    for (int i = r.bitLength() - 2; i >= 0; i--) {
		/*
		 * Loop invariant is: R = [ r.shiftRight(i+1) ] P
		 * 
		 * Including this invariant as check in the next line leads to
		 * enormous slowdown
		 */
		// if ( !( R.equals(P.multiply(r.shiftRight(i+1))) ) ) {
		// System.out.println("Invariant broken!!"); break; };
		// slope for tangent line at R, given standardly as lambda = (3
		// X_R ^ 2 + A) / 2 Y_R
		lambda = THREE.multiply(R.getX()).multiply(R.getX()).add(A)
			.divide(TWO.multiply(R.getY()));
		// X and Y for 2R
		R1X = lambda.multiply(lambda).subtract(TWO.multiply(R.getX()));
		R1Y = lambda.multiply(R.getX().subtract(R1X))
			.subtract(R.getY());
		// lQ = Y-distance of Q to tangent line at R
		// = Y_Q - Y_R - lambda(X_Q - X_R)
		lQ = Qy.subtract(R.getY()).subtract(
			lambda.multiply(Qx.subtract(R.getX())));
		// vQ = X-distance of Q to 2R
		// = X_Q - X_2R
		// vQ = Qx.subtract(R1X);
		m = m.multiply(m).multiply(lQ); // .multiply(((ECFieldElementFp2)vQ).conjugate());
		// 2R -> R
		R = new ECPointFp2(this, (ECFieldElementFp2) R1X,
			(ECFieldElementFp2) R1Y);
		// R = R.twice();
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
			// slope for line through R and P
			lambda = Py.subtract(R.getY()).divide(
				Px.subtract(R.getX()));
			// X and Y for R+P
			R1X = lambda.multiply(lambda).subtract(R.getX())
				.subtract(Px);
			R1Y = lambda.multiply(Px.subtract(R1X)).subtract(Py);
			// lQ = Y-distance of Q to line through R and P
			// = Y_Q - Y_R - lambda(X_Q - X_R)
			lQ = Qy.subtract(R.getY()).subtract(
				lambda.multiply(Qx.subtract(R.getX())));
			// vQ = X-distance of Q to R+P
			// = X_Q - X_R+P
			// vQ = Qx.subtract(R1X);
			m = m.multiply(lQ); // .multiply(((ECFieldElementFp2)vQ).conjugate());
			// R+P -> R
			R = new ECPointFp2(this, (ECFieldElementFp2) R1X,
				(ECFieldElementFp2) R1Y);
			// R = R.add(P);
		    }
		}
		;
	    }
	    ;
	    // Final exponentiation
	    m = ((ECFieldElementFp2) m).fastpow(p.pow(12).subtract(
		    BigInteger.ONE).divide(r));
	    // Make sure that m is of order r in F_p^2
	    m = ((ECFieldElementFp2) m).fastpow(p.subtract(
		    BigInteger.valueOf(1)).multiply(
		    p.add(BigInteger.valueOf(1)).divide(r)));
	    long duration = System.nanoTime() - start;
	    System.out.format("## TatePairing %.1f millisec\n",
		    duration / 1000000.0);
	    return (ECFieldElementFp2) m;
	}
    }

    // reduced Tate Pairing

}
