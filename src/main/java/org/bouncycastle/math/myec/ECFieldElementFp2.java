/**
 *
 * ECFieldElementFp2.java
 *
 * The class ECFieldElementFp2 captures the finite field F_p^2 as F_p[X]/(X^2 + 1).
 * Mathematically, its carrier set is F_p x F_p, with suitable operations
 * making it a field. The elements in this product will be pairs (c1, c0).
 * The additive structure is obtained coordinate-wise.
 * The multiplicative structure requires reasoning modulo X^2 + 1.
 * 
 *
 * An object of this class can be thought of as either:
 *
 * - a polynomial c1 * X + c0
 * 
 * - a complex number c0 + i*c1.
 * 
 * The first view is followed.
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

package org.bouncycastle.math.myec;

import org.bouncycastle.math.ec.ECFieldElement;

import java.math.BigInteger;

public class ECFieldElementFp2 extends ECFieldElement {

    /**
     * Abbreviations, for convenience
     */
    private static final BigInteger ONE = BigInteger.valueOf(1),
	    TWO = BigInteger.valueOf(2), THREE = BigInteger.valueOf(3),
	    FOUR = BigInteger.valueOf(4);

    /**
     * Coefficents; must be in F_p, ie. in range [0, 1, ..., p-1] for the same
     * prime p.
     * 
     * The associated polynomial is c1 * X + c0
     */
    private Fp c1, c0;

    /**
     * Returns the field name for this field.
     * 
     * @return the string "F_p^2".
     */
    public String getFieldName() {
	return "F_p^2";
    }

    /**
     * Returns the size of the field F_p^2, i.e. the number of bits of p^2.
     * 
     * @return twice the number of bits of the prime p
     */
    public int getFieldSize() {
	return 2 * this.getPrime().bitLength();
    }

    /**
     * Returns the X-coefficient of the polynomial
     * 
     * @return c1 of polynomial c1 * X + c0
     */
    public Fp getCoeff1() {
	return c1;
    }

    /**
     * Returns the constant of the polynomial
     * 
     * @return c0 of polynomial c1 * X + c0
     */
    public Fp getCoeff0() {
	return c0;
    }

    /**
     * Tells whether the current field is zero (additive unit).
     * 
     * @return true if the current field is zero, false otherwise.
     */
    public boolean isZero() {
	return (c1.toBigInteger().signum() == 0)
		&& (c0.toBigInteger().signum() == 0);
    }

    /**
     * Tells whether the current field is one (multiplicative unit).
     * 
     * @return true if the current field is one, false otherwise.
     */
    public boolean isOne() {
	return (c1.toBigInteger().signum() == 0)
		&& (c0.toBigInteger().equals(ONE));
    }

    /**
     * Returns the value of the polynomial c1 * X + c0 at X=p.
     * 
     * @return Polynomial evaluation at p.
     */
    public BigInteger toBigInteger() {
	return c0.toBigInteger().add(
		c1.toBigInteger().multiply(this.getPrime()));
    }

    /**
     * Returns the prime p of the field F_p^2.
     * 
     * @return the prime.
     */
    public BigInteger getPrime() {
	return c0.getQ();
    }

    /**
     * Creates a field element from an integer x and prime q with x / q as first
     * coefficient and x % q as zeroth coefficient / constant.
     * 
     */
    public ECFieldElementFp2(BigInteger p, BigInteger x) {
	if (p.signum() == 1 && p.mod(FOUR).equals(THREE)) {
	    c1 = new Fp(p, x.divide(p));
	    c0 = new Fp(p, x.mod(p));
	} else {
	    throw new IllegalArgumentException("Wrong prime modulus");
	}
    }

    /**
     * Creates a field element from two coefficients, either with or without
     * parameter checking.
     * 
     * @param c1
     *            first coefficient
     * @param c0
     *            zero-th coefficient / constant
     * @param check
     *            boolean telling whether parameter checking should happen
     *            (check = true) <br>
     *            or not (check = false).
     */
    public ECFieldElementFp2(ECFieldElement c1, ECFieldElement c0, boolean check) {
	if (check) {
	    if (c0 instanceof Fp
		    && c1 instanceof Fp
		    && ((Fp) c0).getQ().equals(
			    ((Fp) c1).getQ())
		    && ((Fp) c0).getQ().signum() == 1
		    && ((Fp) c0).getQ().mod(FOUR).equals(THREE)) {
		this.c1 = (Fp) c1;
		this.c0 = (Fp) c0;
	    } else {
		throw new IllegalArgumentException(
			"Error with primes in ECFieldElementFp2 creation.");
	    }
	} else {
	    this.c1 = (Fp) c1;
	    this.c0 = (Fp) c0;
	}
    }

    /**
     * Creates a field element from an element f of F_p, as (0, f) = 0 * X + f.
     * 
     */
    public ECFieldElementFp2(Fp f) {
	c1 = new Fp(f.getQ(), ZERO);
	c0 = f;
    }

    /**
     * Adds argument to current field element.
     * 
     * @param a
     *            the field element that is added to the current field
     * @return the sum
     * @throws IllegalArgumentException
     *             if the argument is not a FiniteField_p_Element or if a
     *             FiniteField_p_Element but for a different prime
     */
    public ECFieldElement add(ECFieldElement a) {
	if (a instanceof ECFieldElementFp2) {
	    ECFieldElementFp2 f = (ECFieldElementFp2) a;
	    if (f.getPrime().equals(this.getPrime())) {
		return new ECFieldElementFp2(c1.add(f.getCoeff1()), c0.add(f
			.getCoeff0()), false);
	    } else {
		throw new IllegalArgumentException(
			"F_p^2 addition must have argument p = "
				+ this.getPrime());
	    }
	} else {
	    throw new IllegalArgumentException(
		    "F_p^2 addition must have argument of type F_p^2.");
	}
    }

	public ECFieldElement addOne() {
		return null;
	}

	/**
     * Subtracts argument from the current field element.
     * 
     * @param a
     *            the field element that is subtracted from the current field
     * @return the difference
     * @throws IllegalArgumentException
     *             if the argument is not a FiniteField_p_Element or if a
     *             FiniteField_p_Element but for a different prime
     */
    public ECFieldElement subtract(ECFieldElement a) {
	if (a instanceof ECFieldElementFp2) {
	    ECFieldElementFp2 f = (ECFieldElementFp2) a;
	    if (f.getPrime().equals(this.getPrime())) {
		return new ECFieldElementFp2(c1.subtract(f.getCoeff1()), c0
			.subtract(f.getCoeff0()), false);
	    } else {
		throw new IllegalArgumentException(
			"F_p^2 subtraction must have prime p = "
				+ this.getPrime());
	    }
	} else {
	    throw new IllegalArgumentException(
		    "F_p^2 subtraction must have argument of type F_p^2.");
	}
    }

    /**
     * Multiplies argument with the current field element.
     * 
     * @param a
     *            the field element that is multiplied with the current field
     * @return the product
     * @throws IllegalArgumentException
     *             if the argument is not a FiniteField_p_Element or if a
     *             FiniteField_p_Element but for a different prime
     */
    public ECFieldElement multiply(ECFieldElement a) {
	if (a instanceof ECFieldElementFp2) {
	    ECFieldElementFp2 f = (ECFieldElementFp2) a;
	    if (f.getPrime().equals(this.getPrime())) {
		// (x + yi)(u + vi) = (xu - yv) + ((x + y)(u + v) - xu - yv)i
		ECFieldElement f1 = f.getCoeff1(), f0 = f.getCoeff0(), d1 = c1
			.multiply(f1), d0 = c0.multiply(f0), mix = c1.add(c0)
			.multiply(f1.add(f0));
		return new ECFieldElementFp2(mix.subtract(d1).subtract(d0), d0
			.subtract(d1), false);
	    } else {
		throw new IllegalArgumentException(
			"F_p^2 multiplication must have prime p = "
				+ this.getPrime());
	    }
	} else {
	    throw new IllegalArgumentException(
		    "F_p^2 multiplication must have argument of type F_p^2.");
	}
    }

    /**
     * Multiplies the current field element with a scalar argument (repeated
     * addition).
     * 
     * @param scalar
     *            the field element that is multiplied with the current field
     * @return the scalar product
     */
    public ECFieldElement multiply(BigInteger scalar) {
	BigInteger p = this.getPrime();
	return new ECFieldElementFp2(new Fp(p, c1.toBigInteger()
		.multiply(scalar).mod(p)), new Fp(p, c0
		.toBigInteger().multiply(scalar).mod(p)), false);
    }

    /**
     * Divides the current field element by the argument.
     * 
     * @param a
     *            the field element that is multiplied with the current field
     * @return the quotient
     * @throws IllegalArgumentException
     *             if the argument is not a FiniteField_p_Element or if a
     *             FiniteField_p_Element but for a different prime
     */
    public ECFieldElement divide(ECFieldElement a) {
	if (a instanceof ECFieldElementFp2) {
	    ECFieldElementFp2 f = (ECFieldElementFp2) a;
	    BigInteger p = this.getPrime();
	    if (f.getPrime().equals(p)) {
		// new coefficients d1 = (c1 * f0 - c0 * f1) / denom, d0 = (2 *
		// c1 * f1 + c0 * f0) / denom
		// where denom = 2f1^2 + f0^{2}
		ECFieldElement f1 = f.getCoeff1(), f0 = f.getCoeff0(), denom = f1
			.square().add(f0.square()), d1 = c1.multiply(f0)
			.subtract(c0.multiply(f1)).divide(denom), d0 = c1
			.multiply(f1).add(c0.multiply(f0)).divide(denom);
		return new ECFieldElementFp2(d1, d0, false);
	    } else {
		throw new IllegalArgumentException(
			"F_p^2 division must have argument of type F_p^2 for p = "
				+ p);
	    }
	} else {
	    throw new IllegalArgumentException(
		    "F_p^2 division must have argument of type F_p^2.");
	}
    }

    /**
     * Negates the current field element.
     * 
     * @return the negated field.
     */
    public ECFieldElement negate() {
	return new ECFieldElementFp2(c1.negate(), c0.negate(), false);
    }

    /**
     * Conjugates the current field element.
     * 
     * @return the conjugated field.
     */
    public ECFieldElement conjugate() {
	return new ECFieldElementFp2(c1.negate(), c0, false);
    }

    /**
     * Squares the current field element.
     * 
     * @return the squared field.
     */
    public ECFieldElement square() {
	return this.multiply(this);
    }

    /**
     * Yields the multiplicative inverse of the current field element.
     * 
     * @return the inverted field.
     * @throws IllegalArgumentException
     *             if the current field is zero.
     */
    public ECFieldElement invert() {
	if (this.isZero()) {
	    throw new IllegalArgumentException(
		    "Inverse of zero in F_p^2 is not defined.");
	} else {
	    BigInteger p = this.getPrime();
	    Fp _0 = new Fp(p, ZERO);
	    Fp _1 = new Fp(p, ONE);
	    ECFieldElementFp2 UNIT = new ECFieldElementFp2(_0, _1, false);
	    return UNIT.divide(this);
	}
    }

    /**
     * Multiplies the current field element with X+1.
     * 
     * @return the product with X+1.
     */
    public ECFieldElement multiplyV() {
	return new ECFieldElementFp2(c0.add(c1), c0.subtract(c1), false);
    }

    /**
     * Multiplies the current field element with X.
     * 
     * @return the product with X.
     */
    public ECFieldElement multiplyI() {
	return new ECFieldElementFp2(c0, c1.negate(), false);
    }

    /**
     * Divides the current field element by X+1.
     * 
     * @return the quotient by X+1.
     */
    public ECFieldElement divideV() {
	Fp _1 = new Fp(this.getPrime(), ONE);
	ECFieldElementFp2 V = new ECFieldElementFp2(_1, _1, false);
	return this.divide(V);
	// BigInteger p = this.getPrime(), i0 = c0.toBigInteger(), i1 =
	// c1.toBigInteger();
	// BigInteger p10 = i1.add(i0);
	// if (p10.compareTo(p) >= 0) {
	// p10 = p10.subtract(p);
	// }
	// BigInteger m10 = i1.subtract(i0);
	// if (m10.signum() < 0) {
	// m10 = m10.add(p);
	// }
	// BigInteger j1 = (p10.testBit(0) ? p10.add(p) : p10).shiftRight(1),
	// j0 = (m10.testBit(0) ? m10.add(p) : m10).shiftRight(1);
	// return new ECFieldElementFp2(new ECFieldElement.Fp(p, j1),
	// new ECFieldElement.Fp(p, j0),
	// false);
    }

    /**
     * Tries to compute the square root of the current field element, if any.
     * 
     * @return the square root, if any.
     * @throws IllegalArgumentException
     *             if the calculation does not yield the square root.
     */
    public ECFieldElement sqrt() {
	/*
	 * Solving (d1 * X + d0)^2 = (c1 * X + c0) yields d0 = sqrt( 1/2 * (c0 +
	 * sqrt( c0^2 + c1^2 )) ) d1 = c1 / (2 * d0) The sqrt of
	 * ECFieldElement.Fp is not used.
	 */
	BigInteger p = this.getPrime(),
	// since p = 3 mod 4, we have p = 4*n + 3, so p1 = n+1; sqrt(x) = x^p1
	// mod p.
	p1 = p.subtract(THREE).shiftRight(2).add(BigInteger.ONE), C0 = c0
		.toBigInteger(), C1 = c1.toBigInteger();
	BigInteger // several steps
	s1 = C0.multiply(C0).add(C1.multiply(C1)).mod(p), s2 = s1.modPow(p1, p), s3 = C0
		.add(s2).multiply(TWO.modInverse(p)).mod(p), D0 = s3.modPow(p1,
		p), D1 = C1.multiply(D0.multiply(TWO).modInverse(p)).mod(p);
	ECFieldElement r = new ECFieldElementFp2(new Fp(p, D1),
		new Fp(p, D0), false);
	if (r.multiply(r).equals(this)) {
	    return r;
	} else {
	    throw new IllegalArgumentException(
		    "Square root cannot be computed.");
	}
    }

    /**
     * Raises the current field element to the power k, given as argument
     * 
     * @return the power this^k
     */
    public ECFieldElement pow(BigInteger k) {
	ECFieldElement u = this;
	for (int i = k.bitLength() - 2; i >= 0; i--) {
	    u = u.square();
	    if (k.testBit(i)) {
		u = u.multiply(this);
	    }
	}
	return u;
    }

    /**
     * Raises the current field element to the power k, given as argument, using
     * a clever method.
     * 
     * @return the power this^k
     */
    public ECFieldElement fastpow(BigInteger k) {
	ECFieldElement P = this;
	if (k.signum() < 0) {
	    k = k.negate();
	    P = P.invert();
	}
	;
	byte[] e = k.toByteArray();
	ECFieldElement[] mP = new ECFieldElementFp2[16];
	mP[0] = new ECFieldElementFp2(this.getPrime(), ONE);
	mP[1] = P;
	for (int m = 1; m <= 7; m++) {
	    mP[2 * m] = mP[m].square();
	    mP[2 * m + 1] = mP[2 * m].multiply(P);
	}
	;
	ECFieldElement A = mP[0];
	for (int i = 0; i < e.length; i++) {
	    int u = e[i] & 0xff;
	    A = A.square().square().square().square().multiply(mP[u >>> 4])
		    .square().square().square().square().multiply(mP[u & 0xf]);
	}
	;
	return A;
    }

    /**
     * Equality test
     * 
     * @return true if the argument is in F_p^2 with the same prime and
     *         coefficients as the current field; <br>
     *         false otherwise.
     */
    public boolean equals(Object other) {
	if (other == this) {
	    return true;
	}
	;
	if (!(other instanceof ECFieldElementFp2)) {
	    return false;
	} else {
	    ECFieldElementFp2 o = (ECFieldElementFp2) other;
	    return this.getPrime().equals(o.getPrime())
		    && this.getCoeff0().equals(o.getCoeff0())
		    && this.getCoeff1().equals(o.getCoeff1());
	}
    }

    /**
     * Formats the current field as polynomial.
     * 
     * @return the polynomial, as string.
     */
    public String toString() {
	return "(" + c1.toBigInteger() + " * X + " + c0.toBigInteger() + ")";
    }

}
