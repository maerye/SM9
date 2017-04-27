/**
 *
 * ECCurveWithPairing.java
 *
 * Pairings (Tate, ate, R-ate) on (BN-)curves over field F_p^2
 * 
 * Copyright (C) Pim Vullers, October 2009. Based on work by Bart Jacobs.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.bouncycastle.math.myec.pairing;

import java.math.BigInteger;
import java.security.SecureRandom;

import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.myec.ECCurveFp12;
import org.bouncycastle.math.myec.ECCurveFp2;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.myec.ECFieldElementFp12;
import org.bouncycastle.math.myec.ECFieldElementFp2;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.myec.ECPointFp12;
import org.bouncycastle.math.myec.ECPointFp2;

/**
 * Elliptic Curve class with support for Pairings
 * 
 * Earlier work by Bart Jacobs based on:
 *  
 *   "Implementing Cryptographic Pairings"
 *   Michael Scott
 *   On-line available at
 *     ftp://ftp.computing.dcu.ie/pub/resources/crypto/pairings.pdf
 * 
 * Reformatted and extended by Pim Vullers based on:
 * 
 *   "Software Implementation of Pairings"
 *   Darrel Hankerson, Alfred Menezes and Michael Scott 
 *   Chapter in 
 *     "Identity-based Cryptography"
 *     Marc Joye and Gregory Neven (editors)
 */
public class ECCurveWithPairing extends ECCurve.Fp {

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
        _8 = BigInteger.valueOf(8L),
        _9 = BigInteger.valueOf(9L);

    /**
     * BN index -- the curve BN(u) is defined by the following parameters:
     *
     * t = 6*u^2 + 1
     * p = 36*u^4 + 36*u^3 + 24*u^2 + 6*u + 1
     * r = 36*u^4 + 36*u^3 + 18*u^2 + 6*u + 1
     *
     * BN(u)/GF(p): y^2 = x^3 + b, #BN(u)(GF(p)) = r, r = p + 1 - t
     *
     * Restrictions: p = 3 (mod 4) and p = 4 (mod 9)
     */
    private BigInteger u = null;
    public BigInteger getU() { return u; }

    
    /**
     * Prime of the underlying finite field F_p
     */
    private BigInteger p = null;    
    public BigInteger getP() { return p; }
    
    /**
     * Trace of the Frobenius endomorphism
     */
    private BigInteger t = null;
    public BigInteger getT() {return t; }
    
    /**
     * Prime curve order
     */
    private BigInteger r = null;
    public BigInteger getR() { return r; }

    /**
     * Primitive cube root of unity mod p
     */
    private BigInteger zeta = null;
    public BigInteger getZeta() { return zeta; }
    
    /**
     * Standard generator
     */
    private ECPoint g = null;
    public ECPoint getG() { return g; }
    
    /**
     * Twisted curve, with respect to this.curve, i.e. with negated B parameter
     */
    private ECCurveFp2 twistedCurve;
    public ECCurveFp2 getTwistedCurve() { return twistedCurve; }

    /**
     * Helper class for the return values of the Miller operation
     */
    private class MillerResult {
	public ECFieldElementFp12 f;
	public ECPoint T;
	
	public MillerResult(ECFieldElementFp12 f, ECPoint T) {
	    this.f = f;
	    this.T = T;
	}
    }
    
    /**
     * Construct an elliptic curve y^2 = x^3 + ax + b over F_q
     * 
     * @param q Prime of the underlying finite field F_p
     * @param a Parameter defining the curve: y^2 = x^3 + ax + b (mod q)
     * @param b Parameter defining the curve: y^2 = x^3 + ax + b (mod q)
     */
//    public ECCurveWithPairing(BigInteger q, BigInteger a, BigInteger b) {
//	super(q, a, b);
//	
//	p = q;
//	twistedCurve = new ECCurveFp2(p, 
//		new ECFieldElementFp2(p, _0), new ECFieldElementFp2(new ECFieldElement.Fp(p, _3), new ECFieldElement.Fp(p, _3), false));
//    }
       
    /**
     * Construct a BN-curve y^2 = x^3 + 3 over F_p(u)
     *   p(u) = 36 u^4 + 36 u^3 + 24 u^2 + 6u + 1
     *   r(u) = 36 u^4 + 36 u^3 + 18 u^2 + 6u + 1
     * 
     * @param u BN index parameter
     */
    public ECCurveWithPairing(BigInteger u) {
	super(BNq(u), _0, _3);
	
	// Save BN index
	this.u = u;
	
	// Set BN values
	p = BNq(u);
	t = BNt(u);
	r = BNr(u);
	zeta = BNzeta(u);
	g = new ECPoint.Fp(this, fromBigInteger(_1), fromBigInteger(_2));
	
	// Twisted curve, A = 0, B = 3*(X + 1) 
	twistedCurve = new ECCurveFp2(p, 
		new ECFieldElementFp2(p, _0),
		new ECFieldElementFp2(
			new ECFieldElement.Fp(p, _3), 
			new ECFieldElement.Fp(p, _3), false));
    }
    
    /**
     * Compute the Tate pairing value (t_r: G_1 x G_2 -> mu_r) defined by:
     *   t_r(P, Q) = f_(r, P)(Q)^((p^12 - 1)/r) 
     *     where f_(r, P) is the Miller function
     *
     * For efficiency reasons, no checks are performed on the parameter points.
     *
     * @param P Point that must <br><ul>
     *           <li> be on the current curve
     *           <li> be instances of F_p^2, i.e. of ECFieldElementFp2
     *           <li> have order r
     *           </ul>
     * @param Q Point that must <br><ul>
     *           <li> be on the current curve
     *           <li> be instances of F_p^2, i.e. of ECFieldElementFp2
     *           </ul>
     * @return Result of the pairing
     */
    public ECFieldElementFp12 TatePairing (ECPoint P, ECPoint Q) {
	long start = System.nanoTime();

	if (!(P instanceof ECPoint.Fp && Q instanceof ECPointFp2)) {
	    throw new IllegalArgumentException("Tate pairing over F_p^12 requires points over F_p and F_p^2");
	}
	if (!(P.getCurve() instanceof ECCurveWithPairing && Q.getCurve().equals(((ECCurveWithPairing)P.getCurve()).getTwistedCurve()))) {
	    throw new IllegalArgumentException("Tate pairing over F_p^12 requires the curve of Q to be the twisted curve of P");
	}
	if (!(contains(P.getCurve(), P.getX(), P.getY()) && contains(Q.getCurve(), Q.getX(), Q.getY()))) {
	    throw new IllegalArgumentException("Tate pairing over F_p^12 requires the points to be on their curves");
	}

	// Miller loop
	long miller = System.nanoTime();
	MillerResult mil = millerOperation(r.subtract(_1), P, Q);
	System.out.format("## TatePairing: Miller operation %.1f millisec\n", (System.nanoTime() - miller) / 1000000.0);

	// Final exponentiation
	long expon = System.nanoTime();
	ECFieldElementFp12 f = finalExponentiation(mil.f);
	System.out.format("## TatePairing: Final exponentiation %.1f millisec\n", (System.nanoTime() - expon) / 1000000.0);
	
	System.out.format("## TatePairing %.1f millisec\n", (System.nanoTime() - start) / 1000000.0);

	return f;
    }

    public ECFieldElementFp12 TatePairingFp12(ECPoint P, ECPoint Q) {
	long start = System.nanoTime();

	if (!(P instanceof ECPoint.Fp && Q instanceof ECPointFp2)) {
	    throw new IllegalArgumentException("Tate pairing over F_p^12 requires points over F_p and F_p^2");
	}
	if (!(P.getCurve() instanceof ECCurveWithPairing && Q.getCurve().equals(((ECCurveWithPairing)P.getCurve()).getTwistedCurve()))) {
	    throw new IllegalArgumentException("Tate pairing over F_p^12 requires the curve of Q to be the twisted curve of P");
	}
	if (!(contains(P.getCurve(), P.getX(), P.getY()) && contains(Q.getCurve(), Q.getX(), Q.getY()))) {
	    throw new IllegalArgumentException("Tate pairing over F_p^12 requires the points to be on their curves");
	}

	ECCurve curve12 = new ECCurveFp12(p,
		new ECFieldElementFp12((ECFieldElement.Fp) getA()),
		new ECFieldElementFp12((ECFieldElement.Fp) getB()));
	ECPointFp12 P12 = new ECPointFp12(curve12,
		new ECFieldElementFp12((ECFieldElement.Fp) P.getX()),
		new ECFieldElementFp12((ECFieldElement.Fp) P.getY()));
	ECPointFp12 Q12 = new ECPointFp12(curve12, 
		new ECFieldElementFp12((ECFieldElementFp2) Q.getX(), 2),
		new ECFieldElementFp12((ECFieldElementFp2) Q.getY(), 3));
	
	// Miller operation
	long miller = System.nanoTime();
	MillerResult mil = millerOperation(r.subtract(_1), P12, Q12);
	System.out.format("## TatePairingFp12: Miller operation %.1f millisec\n", (System.nanoTime() - miller) / 1000000.0);

	// Final exponentiation
	long expon = System.nanoTime();
	ECFieldElementFp12 f = finalExponentiation(mil.f);
	System.out.format("## TatePairingFp12: Final exponentiation %.1f millisec\n", (System.nanoTime() - expon) / 1000000.0);
	
	System.out.format("## TatePairingFp12 %.1f millisec\n", (System.nanoTime() - start) / 1000000.0);

	return f;
    }
    
    /**
     * Compute the ate pairing value (a_r: G_1 x G_2 -> mu_r) defined by:
     *   a_r(P, Q) = f_(t - 1, P)(Q)^((p^12 - 1)/r) 
     *     where f_(t - 1, P) is the Miller function
     *
     * For efficiency reasons, no checks are performed on the parameter points.
     *
     * @param Q Point that must <br><ul>
     *            <li>be on the current curve</li>
     *            <li>be instances of F_p^2, i.e. of ECFieldElementFp2</li>
     *            <li>have order r</li></ul>
     * @param P Point that must <br><ul>
     *            <li>be on the current curve</li>
     *            <li>be instances of F_p^2, i.e. of ECFieldElementFp2</li></ul>
     */
    public ECFieldElementFp12 atePairing(ECPoint P, ECPoint Q) {
	long start = System.nanoTime();

	if (!(P instanceof ECPoint.Fp && Q instanceof ECPointFp2)) {
	    throw new IllegalArgumentException("ate pairing over F_p^12 requires points over F_p and F_p^2");
	}
	if (!(P.getCurve() instanceof ECCurveWithPairing && Q.getCurve().equals(((ECCurveWithPairing)P.getCurve()).getTwistedCurve()))) {
	    throw new IllegalArgumentException("ate pairing over F_p^12 requires the curve of Q to be the twisted curve of P");
	}
	if (!(contains(P.getCurve(), P.getX(), P.getY()) && contains(Q.getCurve(), Q.getX(), Q.getY()))) {
	    throw new IllegalArgumentException("ate pairing over F_p^12 requires the points to be on their curves");
	}
	
	// Miller loop
	long miller = System.nanoTime();
	MillerResult mil = millerOperation(t.subtract(_1), Q, P);
	System.out.format("## atePairing: Miller operation %.1f millisec\n", (System.nanoTime() - miller) / 1000000.0);

	// Final exponentiation
	long expon = System.nanoTime();
	ECFieldElementFp12 f = finalExponentiation((ECFieldElementFp12) mil.f);
	System.out.format("## atePairing: Final exponentiation %.1f millisec\n", (System.nanoTime() - expon) / 1000000.0);

	System.out.format("## atePairing %.1f millisec\n", (System.nanoTime() - start) / 1000000.0);

	return f;
    }

    public ECFieldElementFp12 atePairingFp12(ECPoint P, ECPoint Q) {
	long start = System.nanoTime();

	if (!(P instanceof ECPoint.Fp && Q instanceof ECPointFp2)) {
	    throw new IllegalArgumentException("ate pairing over F_p^12 requires points over F_p and F_p^2");
	}
	if (!(P.getCurve() instanceof ECCurveWithPairing && Q.getCurve().equals(((ECCurveWithPairing)P.getCurve()).getTwistedCurve()))) {
	    throw new IllegalArgumentException("ate pairing over F_p^12 requires the curve of Q to be the twisted curve of P");
	}
	if (!(contains(P.getCurve(), P.getX(), P.getY()) && contains(Q.getCurve(), Q.getX(), Q.getY()))) {
	    throw new IllegalArgumentException("ate pairing over F_p^12 requires the points to be on their curves");
	}

	ECCurve curve12 = new ECCurveFp12(p,
		new ECFieldElementFp12((ECFieldElement.Fp) getA()),
		new ECFieldElementFp12((ECFieldElement.Fp) getB()));
	ECPointFp12 P12 = new ECPointFp12(curve12,
		new ECFieldElementFp12((ECFieldElement.Fp) P.getX()),
		new ECFieldElementFp12((ECFieldElement.Fp) P.getY()));
	ECPointFp12 Q12 = new ECPointFp12(curve12, 
		new ECFieldElementFp12((ECFieldElementFp2) Q.getX(), 2),
		new ECFieldElementFp12((ECFieldElementFp2) Q.getY(), 3));
	
	// Miller operation
	long miller = System.nanoTime();
	MillerResult mil = millerOperation(t.subtract(_1), Q12, P12);
	System.out.format("## atePairingFp12: Miller operation %.1f millisec\n", (System.nanoTime() - miller) / 1000000.0);

	// Final exponentiation
	long expon = System.nanoTime();
	ECFieldElementFp12 f = finalExponentiation((ECFieldElementFp12) mil.f);
	System.out.format("## atePairingFp12: Final exponentiation %.1f millisec\n", (System.nanoTime() - expon) / 1000000.0);

	System.out.format("## atePairingFp12 %.1f millisec\n", (System.nanoTime() - start) / 1000000.0);

	return f;
    }

    /**
     * Compute the R-ate pairing value (R_r: G_1 x G_2 -> mu_r) defined by:
     *   R_r(P, Q) = (
     *   	      f * (f * l_(aQ, Q)(P))^p * l_(pi(aQ + Q), aQ)(P)
     *               )^((p^12 - 1)/r) 
     *     where a = 6u + 2, 
     *     	 f = f_(a, P) (the Miller function), 
     *           l_(A, B) denotes the line through A and B, and 
     *           pi: (x, y) |-> (x^p, y^p) is the Frobenius map
     *
     * For efficiency reasons, no checks are performed on the parameter points.
     *
     * @param P Point that must <br><ul>
     *            <li>be on the current curve</li>
     *            <li>be instances of F_p^2, i.e. of ECFieldElementFp2</li>
     *            <li>have order r</li></ul>
     * @param Q Point that must <br><ul>
     *            <li>be on the current curve</li>
     *            <li>be instances of F_p^2, i.e. of ECFieldElementFp2</li></ul>
     */
    public ECFieldElementFp12 R_atePairing(ECPoint P, ECPoint Q) {
	long start = System.nanoTime();

	if (!(P instanceof ECPoint.Fp && Q instanceof ECPointFp2)) {
	    throw new IllegalArgumentException("R-ate pairing over F_p^12 requires points over F_p and F_p^2");
	}
	if (!(P.getCurve() instanceof ECCurveWithPairing && Q.getCurve().equals(((ECCurveWithPairing)P.getCurve()).getTwistedCurve()))) {
	    throw new IllegalArgumentException("R-ate pairing over F_p^12 requires the curve of Q to be the twisted curve of P");
	}
	if (!(contains(P.getCurve(), P.getX(), P.getY()) && contains(Q.getCurve(), Q.getX(), Q.getY()))) {
	    throw new IllegalArgumentException("R-ate pairing over F_p^12 requires the points to be on their curves");
	}

	BigInteger sixUplus2 = u.multiply(_6).add(_2);
	boolean negatedIndex = false;
	if (sixUplus2.signum() < 0) { 
	    sixUplus2 = sixUplus2.negate(); 
	    negatedIndex = true;
	}

	// Miller loop
	long miller = System.nanoTime();
	MillerResult mil = millerOperation(sixUplus2, Q, P);
	System.out.format("## R-atePairing: Miller operation %.1f millisec\n", (System.nanoTime() - miller) / 1000000.0);

	// R-ate operation
	long r_ate = System.nanoTime();
	ECFieldElementFp12 f = R_ateOperation(mil.f, mil.T, P, Q, negatedIndex);
	System.out.format("## R-atePairing: R-ate operation %.1f millisec\n", (System.nanoTime() - r_ate) / 1000000.0);

	// Final exponentiation
	long expon = System.nanoTime();
	f = finalExponentiation((ECFieldElementFp12) f);
	System.out.format("## R-atePairing: Final exponentiation %.1f millisec\n", (System.nanoTime() - expon) / 1000000.0);

	System.out.format("## R-atePairing %.1f millisec\n", (System.nanoTime() - start) / 1000000.0);

	return (ECFieldElementFp12)f;
    }
    
    public ECFieldElementFp12 R_atePairingFp12(ECPoint P, ECPoint Q) {
	long start = System.nanoTime();

	if (!(P instanceof ECPoint.Fp && Q instanceof ECPointFp2)) {
	    throw new IllegalArgumentException("R-ate pairing over F_p^12 requires points over F_p and F_p^2");
	}
	if (!(P.getCurve() instanceof ECCurveWithPairing && Q.getCurve().equals(((ECCurveWithPairing)P.getCurve()).getTwistedCurve()))) {
	    throw new IllegalArgumentException("R-ate pairing over F_p^12 requires the curve of Q to be the twisted curve of P");
	}
	if (!(contains(P.getCurve(), P.getX(), P.getY()) && contains(Q.getCurve(), Q.getX(), Q.getY()))) {
	    throw new IllegalArgumentException("R-ate pairing over F_p^12 requires the points to be on their curves");
	}

	ECCurve curve12 = new ECCurveFp12(p,
		new ECFieldElementFp12((ECFieldElement.Fp)getA()),
		new ECFieldElementFp12((ECFieldElement.Fp)getB()));
	ECPointFp12 P12 = new ECPointFp12(curve12,
		new ECFieldElementFp12((ECFieldElement.Fp) P.getX()),
		new ECFieldElementFp12((ECFieldElement.Fp) P.getY()));
	ECPointFp12 Q12 = new ECPointFp12(curve12, 
		new ECFieldElementFp12((ECFieldElementFp2) Q.getX(), 2),
		new ECFieldElementFp12((ECFieldElementFp2) Q.getY(), 3));
	
	BigInteger sixUplus2 = u.multiply(_6).add(_2);
	boolean negatedIndex = false;
	if (sixUplus2.signum() < 0) { 
	    sixUplus2 = sixUplus2.negate(); 
	    negatedIndex = true;
	}

	// Miller operation
	long miller = System.nanoTime();
	MillerResult mil = millerOperation(sixUplus2, Q12, P12);	
	System.out.format("## R-atePairingFp12: Miller operation %.1f millisec\n", (System.nanoTime() - miller) / 1000000.0);

	// R-ate operation
	long r_ate = System.nanoTime();
	ECFieldElementFp12 f = R_ateOperationFp12(mil.f, (ECPointFp12) mil.T, P12, Q12, negatedIndex);
	System.out.format("## R-atePairingFp12: R-ate operation %.1f millisec\n", (System.nanoTime() - r_ate) / 1000000.0);
	
	// Final exponentiation
	long expon = System.nanoTime();
	f = finalExponentiation((ECFieldElementFp12) f);
	System.out.format("## R-atePairingFp12: Final exponentiation %.1f millisec\n", (System.nanoTime() - expon) / 1000000.0);

	System.out.format("## R-atePairingFp12 %.1f millisec\n", (System.nanoTime() - start) / 1000000.0);

	return (ECFieldElementFp12)f;
    }    
    
    /**
     * Miller operation (f_(L, P)(Q))
     * 
     * @param L Loop counter
     * @param P Iteration point
     * @param Q ...
     * @return result of the Miller operation 
     */
    private MillerResult millerOperation(BigInteger L, ECPoint P, ECPoint Q) {
	ECFieldElement f = new ECFieldElementFp12(new ECFieldElement.Fp(p, _1));
	ECPoint T = P;
	
	for (int i = L.bitLength() - 2; i >= 0; i--) {
	    f = f.square().multiply(gl(T, T, Q));
	    T = T.twice();
	    
	    if ( L.testBit(i) ) {
		f = f.multiply(gl(T, P, Q));
		T = T.add(P);
	    }
	}
	
	return new MillerResult((ECFieldElementFp12) f, T);
    }
    
    /**
     * The additional operation for R-ate
     * 
     * @param f Result f of the Miller operation 
     * @param T Result T of the Miller operation
     * @param P Input point P
     * @param Q Input point Q
     * @param negated Whether the Miller loop counter has been negated
     * @return
     */
    private ECFieldElementFp12 R_ateOperation(ECFieldElementFp12 f, ECPoint T, ECPoint P, ECPoint Q, boolean negated) {
	BigInteger 
	sigma = p.subtract(BigInteger.valueOf(4)).modPow(p.subtract(BigInteger.valueOf(1)).subtract(p.add(BigInteger.valueOf(5)).divide(BigInteger.valueOf(24))), p),
	zetasigma = zeta.multiply(sigma).mod(p), zeta1 = zeta.add(BigInteger.valueOf(1)), mzeta1 = zeta1.negate();
	ECFieldElementFp2 Qx = (ECFieldElementFp2) Q.getX(), Qy = (ECFieldElementFp2) Q.getY();

	ECPoint Q1 = new ECPointFp2(Q.getCurve(),
		((ECFieldElementFp2) ((ECFieldElementFp2) Qx.conjugate()).multiply(zeta.negate())).multiplyI(),
		((ECFieldElementFp2) ((ECFieldElementFp2) Qy.multiplyV()).conjugate()).multiply(zetasigma));
	ECPoint Q2 = new ECPointFp2(Q.getCurve(),
		Qx.multiply(mzeta1),
		Qy.negate());
	ECPoint Q3 = new ECPointFp2(Q.getCurve(),
		((ECFieldElementFp2) ((ECFieldElementFp2) ((ECFieldElementFp2) Qx.conjugate()).multiply(zeta.negate())).multiplyI()).multiply(mzeta1),
		((ECFieldElementFp2) ((ECFieldElementFp2) ((ECFieldElementFp2) Qy.multiplyV()).conjugate()).multiply(zetasigma)).negate());

	ECFieldElementFp12 g1 = (ECFieldElementFp12) gl(Q3, Q2.negate(), P);
	ECFieldElementFp12 g2 = (ECFieldElementFp12) gl(Q3.subtract(Q2), Q1, P);

	if (negated) {
	    return (ECFieldElementFp12) g1.multiply(g2).multiply(  gl( Q1.subtract(Q2).add(Q3), T.negate(), P)).divide(f);
	} else {
	    return (ECFieldElementFp12) f.multiply(g1).multiply(g2).multiply(gl(Q1.subtract(Q2).add(Q3), T, P));
	}
    }

    private ECFieldElementFp12 R_ateOperationFp12(ECFieldElementFp12 f, ECPointFp12 T, ECPointFp12 P, ECPointFp12 Q, boolean negated) {
	ECPointFp12 Q1 = new ECPointFp12(Q.getCurve(),
		((ECFieldElementFp12)Q.getX()).frobenius(zeta),
		((ECFieldElementFp12)Q.getY()).frobenius(zeta));
	ECPointFp12 Q2 = new ECPointFp12(Q.getCurve(),
		((ECFieldElementFp12)Q.getX()).conjugate(1, zeta),
		((ECFieldElementFp12)Q.getY()).conjugate(1, zeta));
	ECPointFp12 Q3 = new ECPointFp12(Q.getCurve(),
		((ECFieldElementFp12)((ECFieldElementFp12)Q.getX()).frobenius(zeta)).conjugate(1, zeta),
		((ECFieldElementFp12)((ECFieldElementFp12)Q.getY()).frobenius(zeta)).conjugate(1, zeta));

	if (negated) {
	    return (ECFieldElementFp12) glFp12(Q3, (ECPointFp12) Q2.negate(), P)
	    	.multiply(glFp12((ECPointFp12) Q3.subtract(Q2), Q1, P))
	    	.multiply(glFp12((ECPointFp12) Q1.subtract(Q2).add(Q3), (ECPointFp12) T.negate(), P))
	    	.divide(f);
	} else {
	    return (ECFieldElementFp12) f
	    	.multiply(glFp12(Q3, (ECPointFp12)Q2.negate(), P))
	    	.multiply(glFp12((ECPointFp12)Q3.subtract(Q2), Q1, P))
	    	.multiply(glFp12((ECPointFp12)Q1.subtract(Q2).add(Q3), T, P));
	}
    }
    
    /**
     * Final exponentiation (f^((p^12 - 1)/r)) of the pairings
     *
     * @param f Field element to be exponentiated  
     * @return result of the final exponentiation
     */
    private ECFieldElementFp12 finalExponentiation(ECFieldElementFp12 f) {
	// Auxiliary variables used for exponentiation
	ECFieldElementFp12 a, b, fp, fp2, fp3;

	// f <- f^(p^6 - 1)
	f = (ECFieldElementFp12) f.conjugate(3, zeta).multiply(f.invert(zeta));
	
	// f <- f^(p^2 + 1)
	f = (ECFieldElementFp12) f.conjugate(1, zeta).multiply(f);
	
	// a <- f^-(6z + 5)
	if (u.signum() < 0) {
	  a = (ECFieldElementFp12) f.pow(u.multiply(_6).add(_5).negate().mod(p));
	} else {
	  a = (ECFieldElementFp12) ((ECFieldElementFp12) f.invert(zeta)).pow(u.multiply(_6).add(_5).mod(p));
	}
	
	// b <- a^p
	b = (ECFieldElementFp12) a.frobenius(zeta);
	
	// b <- a*b
	b = (ECFieldElementFp12) a.multiply(b);
	
	// fp <- f^p
	fp = (ECFieldElementFp12) f.frobenius(zeta);
	
	// fp2 <- f^(p^2)
	fp2 = (ECFieldElementFp12) f.conjugate(1, zeta);
	
	// fp3 <- f^(p^3)
	fp3 = (ECFieldElementFp12) fp.conjugate(1, zeta);
	
	// f <- fp3 * (b * fp^2 * fp2)^(6z^2 + 1) * b * (fp * f)^9 * a * f^4
	f = (ECFieldElementFp12) fp3
		.multiply( /* (b * fp^2 * fp2)^(6z^2 + 1) */
			((ECFieldElementFp12)b.multiply(fp.square()).multiply(fp2)).pow(t)
		).multiply( /* b */
			b
		).multiply( /* (fp * f)^9 */
			((ECFieldElementFp12)fp.multiply(f)).pow(_9)
		).multiply( /* a */
			a
		).multiply( /* f^4 */
			f.square().square()
		);
	
	// f = f^((p^12 - 1)/r)
	return f;
    }
    
    /**
     * Line functions
     * 
     * @param U
     * @param V
     * @param Q
     * @return
     */   
    private ECFieldElement gl(ECPoint U, ECPoint V, ECPoint Q) {
	if (U instanceof ECPointFp12) {
	    return glFp12((ECPointFp12) U, (ECPointFp12) V, (ECPointFp12)Q); 
	} else if (U instanceof ECPointFp2) {
	    return glAte(U, V, Q);
	} else { // if (U instanceof ECPoint.Fp) {
	    return glTate(U, V, Q);
	}
    }

    private ECFieldElement glTate(ECPoint U, ECPoint V, ECPoint Q) {
	ECFieldElement N, D, 
		Ux = U.getX(), Uy = U.getY(), 
		Vx = V.getX(), Vy = V.getY(),
		Qx = Q.getX(), Qy = Q.getY(),
		__0 = new ECFieldElement.Fp(p, _0),
		__2 = new ECFieldElement.Fp(p, _2),
		__3 = new ECFieldElement.Fp(p, _3);

	if (U.equals(V)) {
	    N = Ux.square().multiply(__3);
	    D = Uy.multiply(__2);
	} else {
	    N = Vy.subtract(Uy);
	    D = Vx.subtract(Ux);
	}
	
	ECFieldElementFp2[] w = new ECFieldElementFp2[6];
	w[1] = w[4] = w[5] = new ECFieldElementFp2(p, _0);

	w[0] = new ECFieldElementFp2(__0, Uy.negate().multiply(D).add(Ux.multiply(N)), false);
	w[2] = (ECFieldElementFp2)((ECFieldElementFp2)Qx.negate()).multiply(N.toBigInteger());
	w[3] = (ECFieldElementFp2)((ECFieldElementFp2)Qy).multiply(D.toBigInteger());

	return new ECFieldElementFp12(w, false);
    }
    
    private ECFieldElement glAte(ECPoint U, ECPoint V, ECPoint Q) {
  	ECFieldElement Ux = U.getX(), Uy = U.getY(), Vx = V.getX(), Vy = V.getY(),
  	Qx = new ECFieldElementFp2((ECFieldElement.Fp)Q.getX()), Qy = new ECFieldElementFp2((ECFieldElement.Fp)Q.getY()),N, D, 
  		__2 = new ECFieldElementFp2(p, _2),
  		__3 = new ECFieldElementFp2(p, _3);

  	ECFieldElementFp2[] w = new ECFieldElementFp2[6];
	w[1] = w[2] = w[5] = new ECFieldElementFp2(p, _0);

	if (U.equals(V)) {
	    N = Ux.square().multiply(__3);
	    D = Uy.multiply(__2);
	} else {
	    N = Vy.subtract(Uy);
	    D = Vx.subtract(Ux);
	}
	
	w[0] = (ECFieldElementFp2) ((ECFieldElementFp2) N.multiply(Ux.negate())).divideV().add(((ECFieldElementFp2)D.multiply(Uy)).divideV());
	w[3] = (ECFieldElementFp2) D.multiply(Qy.negate());
	w[4] = (ECFieldElementFp2) N.multiply(Qx);
	
	return new ECFieldElementFp12(w, false);
    }

    private ECFieldElement glFp12(ECPointFp12 U, ECPointFp12 V, ECPointFp12 Q) {
	if (U.isZero() || V.isZero() || Q.isZero() || U.negate().equals(V)) {
	     return new ECFieldElementFp12(new ECFieldElementFp2(p, _1), 0);
	}
	
	ECFieldElement m, s,
		Ux = U.getX(), Uy = U.getY(), 
		Vx = V.getX(), Vy = V.getY(),
		Qx = Q.getX(), Qy = Q.getY();
	if (U.equals(V)) {
	    m = ((ECFieldElementFp12) Ux.square()).multiply(_3);
	    s = ((ECFieldElementFp12) Uy).multiply(_2);
	} else {
	    m = Vy.subtract(Uy);
	    s = Vx.subtract(Ux);
	}

	m = m.multiply(((ECFieldElementFp12) Qx).subtract(Ux));
	s = s.multiply(Uy.subtract((ECFieldElementFp12)Qy));

	return m.add(s);
    }
        
    /**
     * Calculate the prime of the underlying finite field F_q for BN-curves
     * 
     * @param u BN-index of the curve
     * @return Prime of the underlying finite field F_q
     */
    static public BigInteger BNq(BigInteger u) {
	// q = q(u) = 36 u^4 + 36 u^3 + 24 u^2 + 6u + 1
	return u.add(_1).multiply(_6.multiply(u)).add(_4).multiply(u).add(_1).multiply(_6.multiply(u)).add(_1);
    }
    
    /**
     * Calculate the prime curve order for BN-curves
     * 
     * @param u BN-index of the curve
     * @return Prime curve order 
     */
    static public BigInteger BNr(BigInteger u) {
	// n = n(u) = 36 u^4 + 36 u^3 + 18 u^2 + 6u + 1 = p + 1 - t
	return u.add(_1).multiply(_6.multiply(u)).add(_3).multiply(u).add(_1).multiply(_6.multiply(u)).add(_1);
    }
    
    /**
     * Calculate the trace of the Frobenius endomorphism for BN-curves
     * 
     * @param u BN-index of the curve
     * @return Trace of the Frobenius endomorphism
     */
    static public BigInteger BNt(BigInteger u) {
	// t = t(u) = 6 u^2 + 1
	return _6.multiply(u).multiply(u).add(_1);
    }
    
    /**
     * Calculate the primitive cube root of unity mod p for BN-curves
     * 
     * @param u BN-index of the curve
     * @return Primitive cube root of unity mod p
     */
    static public BigInteger BNzeta(BigInteger u) {
	// zeta = 18 u^3 + 18 u^2 + 9u + 1;
	return _9.multiply(u).multiply(u.shiftLeft(1).multiply(u.add(_1)).add(_1)).add(_1);
    }
    
    /**
     * Find a new random point on the specified curve
     * 
     * A new point is sought on the parameter curve over F_p by just picking an
     * arbitrary x and trying if there is a corresponding Y via a square root.
     * 
     * @param curve Curve which the point should be on
     * @return Point on the curve
     */
    static public ECPoint FindNewPoint(ECCurve curve) {
	ECPoint result = null;
	SecureRandom random = new SecureRandom();
	if (curve instanceof Fp) {
	    BigInteger q = ((Fp)curve).getQ();
	    while (result == null) {
		ECFieldElement x = new ECFieldElement.Fp(q, new BigInteger(q.bitLength() - 1, random));
		try {
		    ECFieldElement y = x.multiply(x).multiply(x).add(
			    curve.getA().multiply(x)).add(curve.getB()).sqrt();

		    if (contains(curve, x, y)) {
			result = new ECPoint.Fp(curve, x, y);
		    }
		} catch (Exception e) {
		    // This one didn't work; we just try again
		    continue;
		}
	    }
	} else if (curve instanceof ECCurveFp2) {
	    BigInteger q = ((ECCurveFp2)curve).getQ();		
	    while (result == null) {
		ECFieldElement x = new ECFieldElementFp2(q, new BigInteger(q.bitLength() - 1, random));
		try {
		    ECFieldElement y = x.multiply(x).multiply(x).add(
			    curve.getA().multiply(x)).add(curve.getB()).sqrt();

		    if (contains(curve, x, y)) {
			result = new ECPointFp2(curve, x, y);
		    }
		} catch (Exception e) {
		    // This one didn't work; we just try again
		    continue;
		}
	    }
	} else {
	    System.err.println("FindNewPoint only supports Fp and Fp2 curves");
	}

	return result;
    }

    /**
     * Find a new random point on this curve
     * 
     * A new point is sought on the parameter curve over F_p by just picking an
     * arbitrary x and trying if there is a corresponding Y via a square root.
     * 
     * @return Point on this curve
     */
    public ECPoint FindNewPoint() {
	return FindNewPoint(this);
    }
    
    /**
     * Reconstruct a point from its x-coordinate using y^2 = x^3 + ax + b
     * 
     * @param c Curve on which should contain the point
     * @param x X-coordinate of the point to be reconstructed
     * @param negate Whether or not to negate the calculated y-coordinate
     * @return Reconstructed point on the curve 
     */
    public static ECPoint reconstructPoint(ECCurve c, BigInteger x, boolean negate) {	
	ECFieldElement xe = c.fromBigInteger(x);
	ECFieldElement ye = xe.multiply(xe).multiply(xe).add(
		    c.getA().multiply(xe)).add(c.getB()).sqrt();
	if (negate) {
	    return c.createPoint(xe.toBigInteger(), ye.toBigInteger().negate(), false);
	} else {
	    return c.createPoint(xe.toBigInteger(), ye.toBigInteger(), false);
	}
    }
    
    /**
     * Reconstruct a point on this curve from its x-coordinate
     * 
     * @param x X-coordinate of the point to be reconstructed
     * @param negate Whether or not to negate the calculated y-coordinate
     * @return Reconstructed point on the curve 
     */
    public ECPoint reconstructPoint(BigInteger x, boolean negate) {
	return reconstructPoint(this, x, negate);
    }
    
    /**
     * Verify whether a point (x, y) is contained in a curve
     * 
     * @param curve Elliptic curve which the point should be contained in
     * @param x X coordinate of the point
     * @param y Y coordinate of the point
     * @return Whether the point is contained in the curve
     */
    public static boolean contains(ECCurve curve, ECFieldElement x, ECFieldElement y) {
        ECFieldElement LHS = y.multiply(y);
        ECFieldElement RHS = x.multiply(x).multiply(x).add(curve.getA().multiply(x)).add(curve.getB());
        return LHS.equals(RHS);
    }
    
    /**
     * Verify whether a point (x, y) is contained in this curve
     * 
     * @param x X coordinate of the point
     * @param y Y coordinate of the point
     * @return Whether the point is contained in this curve
     */
    public boolean contains(ECFieldElement x, ECFieldElement y) {
	return contains(this, x, y);
    }    
}
