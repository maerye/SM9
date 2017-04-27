/**
 * ECFieldElementFp12.java
 *
 * Arithmetic in the finite extension field GF(p^12) with p = 3 (mod 4) and p = 4 (mod 9).
 *
 * Original version Copyright (C) Paulo S. L. M. Barreto.
 *
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
 */

package org.bouncycastle.math.myec;

import org.bouncycastle.math.ec.ECFieldElement;

import java.math.BigInteger;

public class ECFieldElementFp12 extends ECFieldElement {

    /**
     * Convenient BigInteger constants
     */
    private static final BigInteger ZERO = BigInteger.valueOf(0L),
	    THREE = BigInteger.valueOf(3L), FOUR = BigInteger.valueOf(4L),
	    NINE = BigInteger.valueOf(9L);

    /**
     * Coefficients; array of length 6, each involving the same prime.
     */
    private ECFieldElementFp2[] c = new ECFieldElementFp2[6];

    /**
     * Returns the field name for this field.
     * 
     * @return the string "F_p^12".
     */
    public String getFieldName() {
	return "F_p^12";
    }

    /**
     * Returns the size of the field F_p^2, i.e. the number of bits of p^2.
     * 
     * @return twice the number of bits of the prime p
     */
    public int getFieldSize() {
	return 12 * this.getPrime().bitLength();
    }

    /**
     * Returns the X^i-the coefficient of the polynomial
     * 
     * @return c_i of polynomial c5 * X^5 + c4 * X^4 + c3 * X^3 + c2 * X^2 + c1
     *         * X + c0
     */
    public ECFieldElementFp2 getCoeff(int i) {
	if (i >= 0 && i < 6) {
	    return c[i];
	} else {
	    throw new IllegalArgumentException(
		    "Index for F_p^12 index must be in [0,1,..,5].");
	}
    }

    /**
     * Tells whether the current field is zero (additive unit).
     * 
     * @return true if the current field is zero, false otherwise.
     */
    public boolean isZero() {
	boolean iszero = true;
	for (int i = 5; i >= 0; i--) {
	    if (!c[i].isZero()) {
		iszero = false;
		break;
	    }
	}
	;
	return iszero;
    }

    /**
     * Tells whether the current field is one (multiplicative unit).
     * 
     * @return true if the current field is one, false otherwise.
     */
    public boolean isOne() {
	return true; // c1.toBigInteger().signum() == 0 &&
	// c0.toBigInteger().equals(ONE);
    }

    /**
     * Returns the value of the polynomial at X=p.
     * 
     * @return Polynomial evaluation at p.
     */
    public BigInteger toBigInteger() {
	return ZERO; // c0.toBigInteger().add(c1.toBigInteger().multiply(this.getPrime()));
    }

    /**
     * Returns the prime p of the field F_p^12.
     * 
     * @return the prime.
     */
    public BigInteger getPrime() {
	return c[0].getPrime();
    }

    /**
     * Creates a field element from six coefficients, either with or without
     * parameter checking.
     * 
     * @param ci
     *            i-th coefficient
     * @param check
     *            boolean telling whether parameter checking should happen
     *            (check = true) <br>
     *            or not (check = false).
     */
    public ECFieldElementFp12(ECFieldElement c5, ECFieldElement c4,
	    ECFieldElement c3, ECFieldElement c2, ECFieldElement c1,
	    ECFieldElement c0, boolean check) {
	if (check) {
	    if (c5 instanceof ECFieldElementFp2
		    && c4 instanceof ECFieldElementFp2
		    && c3 instanceof ECFieldElementFp2
		    && c2 instanceof ECFieldElementFp2
		    && c1 instanceof ECFieldElementFp2
		    && c0 instanceof ECFieldElementFp2
		    && ((ECFieldElementFp2) c4).getPrime().equals(
			    ((ECFieldElementFp2) c5).getPrime())
		    && ((ECFieldElementFp2) c3).getPrime().equals(
			    ((ECFieldElementFp2) c5).getPrime())
		    && ((ECFieldElementFp2) c2).getPrime().equals(
			    ((ECFieldElementFp2) c5).getPrime())
		    && ((ECFieldElementFp2) c1).getPrime().equals(
			    ((ECFieldElementFp2) c5).getPrime())
		    && ((ECFieldElementFp2) c0).getPrime().equals(
			    ((ECFieldElementFp2) c5).getPrime())
		    && ((ECFieldElementFp2) c5).getPrime().signum() == 1
		    && ((ECFieldElementFp2) c5).getPrime().mod(FOUR).equals(
			    THREE)
		    && ((ECFieldElementFp2) c5).getPrime().mod(NINE).equals(
			    FOUR)) {
		c[5] = (ECFieldElementFp2) c5;
		c[4] = (ECFieldElementFp2) c4;
		c[3] = (ECFieldElementFp2) c3;
		c[2] = (ECFieldElementFp2) c2;
		c[1] = (ECFieldElementFp2) c1;
		c[0] = (ECFieldElementFp2) c0;
	    } else {
		throw new IllegalArgumentException(
			"Error with primes in ECFieldElementFp2 creation.");
	    }
	} else {
	    c[5] = (ECFieldElementFp2) c5;
	    c[4] = (ECFieldElementFp2) c4;
	    c[3] = (ECFieldElementFp2) c3;
	    c[2] = (ECFieldElementFp2) c2;
	    c[1] = (ECFieldElementFp2) c1;
	    c[0] = (ECFieldElementFp2) c0;
	}
    }

    /**
     * Creates a field element from six coefficients, either with or without
     * parameter checking.
     * 
     * @param ci
     *            i-th coefficient
     * @param check
     *            boolean telling whether parameter checking should happen
     *            (check = true) <br>
     *            or not (check = false).
     */
    public ECFieldElementFp12(ECFieldElement[] cs, boolean check) {
	this(cs[5], cs[4], cs[3], cs[2], cs[1], cs[0], check);
    }

    // if ( check )
    // { if ( cs == null || cs.length < 6 )
    // {
    // throw new
    // IllegalArgumentException("Wrong coefficient array in ECFieldElementFp12 creation.");
    // }
    // else
    // {
    // new ECFieldElementFp12(cs[5], cs[4], cs[3], cs[2], cs[1], cs[0], check);
    // }
    // }
    // }

    /**
     * Used e.g. for The standard isomorphism psi: E'(F_{p^2}) -> E(F_{p^12}) is
     * defined as psi(x', y') = (x'*z^2, y'*z^3)
     * 
     * if b out of range, nothing happens.
     */
    public ECFieldElementFp12(ECFieldElementFp2 x, int b) {
	Fp zero1 = new Fp(x.getPrime(),
		BigInteger.valueOf(0));
	ECFieldElementFp2 zero2 = new ECFieldElementFp2(zero1, zero1, true);
	for (int i = 5; i >= 0; i--) {
	    if (b == i) {
		c[i] = x;
	    } else {
		c[i] = zero2;
	    }
	}
    }

    public ECFieldElementFp12(Fp x) {
	Fp zero1 = new Fp(x.getQ(), BigInteger
		.valueOf(0));
	ECFieldElementFp2 zero2 = new ECFieldElementFp2(zero1, zero1, true);
	for (int i = 5; i > 0; i--) {
	    c[i] = zero2;
	}
	;
	c[0] = new ECFieldElementFp2(zero1, x, true);
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
	if (a instanceof ECFieldElementFp12) {
	    ECFieldElementFp12 f = (ECFieldElementFp12) a;
	    if (f.getPrime().equals(this.getPrime())) {
		ECFieldElement[] d = new ECFieldElementFp2[6];
		for (int i = 5; i >= 0; i--) {
		    d[i] = c[i].add(f.getCoeff(i));
		}
		;
		return new ECFieldElementFp12(d, false);
	    } else {
		throw new IllegalArgumentException(
			"F_p^12 addition must have argument p = "
				+ this.getPrime());
	    }
	} else {
	    throw new IllegalArgumentException(
		    "F_p^12 addition must have argument of type F_p^12.");
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
	if (a instanceof ECFieldElementFp12) {
	    ECFieldElementFp12 f = (ECFieldElementFp12) a;
	    if (f.getPrime().equals(this.getPrime())) {
		ECFieldElement[] d = new ECFieldElementFp2[6];
		for (int i = 5; i >= 0; i--) {
		    d[i] = c[i].subtract(f.getCoeff(i));
		}
		;
		return new ECFieldElementFp12(d, false);
	    } else {
		throw new IllegalArgumentException(
			"F_p^12 subtraction must have argument p = "
				+ this.getPrime());
	    }
	} else {
	    throw new IllegalArgumentException(
		    "F_p^12 subtraction must have argument of type F_p^12.");
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
    // public ECFieldElement multiply(ECFieldElement a)
    // {
    // if ( a instanceof ECFieldElementFp12 )
    // {
    // ECFieldElementFp12 f = (ECFieldElementFp12)a;
    // if ( f.getPrime().equals(this.getPrime()) )
    // {
    // ECFieldElement[] w = new ECFieldElementFp2[6];
    // ECFieldElement f0 = f.getCoeff(0), f1 = f.getCoeff(1), f2 =
    // f.getCoeff(2),
    // f3 = f.getCoeff(3), f4 = f.getCoeff(4), f5 = f.getCoeff(5);
    // w[0] = f0.multiply(c[0]).add(((ECFieldElementFp2)
    // (f5.multiply(c[1]).add(f4.multiply(c[2])).add(f3.multiply(c[3])).add(f2.multiply(c[4])).add(f1.multiply(c[5])))).divideV());
    // w[1] = f1.multiply(c[0]).add(f0.multiply(c[1])).add(((ECFieldElementFp2)
    // (f5.multiply(c[2]).add(f4.multiply(c[3])).add(f3.multiply(c[2])).add(f2.multiply(c[1])))).divideV());
    // return new ECFieldElementFp12(w, false);
    // }
    // else
    // {
    // throw new
    // IllegalArgumentException("F_p^12 multiplication must have argument p = "
    // + this.getPrime());
    // }
    // }
    // else
    // {
    // throw new
    // IllegalArgumentException("F_p^12 multiplication must have argument of type F_p^12.");
    // }
    // }

    public ECFieldElement multiply(ECFieldElement a) {
	if (a instanceof ECFieldElementFp12) {
	    ECFieldElementFp12 f = (ECFieldElementFp12) a;
	    if (f.getPrime().equals(this.getPrime())) {
		ECFieldElement[] w = new ECFieldElementFp2[6];
		ECFieldElementFp2 f0 = f.getCoeff(0), f1 = f.getCoeff(1), f2 = f
			.getCoeff(2), f3 = f.getCoeff(3), f4 = f.getCoeff(4), f5 = f
			.getCoeff(5);
		if (f1.isZero() && f4.isZero() && f5.isZero()) {
		    ECFieldElement d00 = c[0].multiply(f0), d22 = c[2]
			    .multiply(f2), d33 = c[3].multiply(f3), s01 = c[0]
			    .add(c[1]), d01 = s01.multiply(f0).subtract(d00), d02 = c[0]
			    .add(c[2]).multiply(f0.add(f2)).subtract(
				    d00.add(d22)), d04 = c[0].add(c[4])
			    .multiply(f0).subtract(d00), d13 = c[1].add(c[3])
			    .multiply(f3).subtract(d33), s23 = c[2].add(c[3]), t23 = f2
			    .add(f3), u23 = d22.add(d33), d23 = s23.multiply(
			    t23).subtract(u23), d24 = c[2].add(c[4]).multiply(
			    f2).subtract(d22), d35 = c[3].add(c[5])
			    .multiply(f3).subtract(d33), u01 = d00.add(d01), d03 = s01
			    .add(s23).multiply(f0.add(t23)).subtract(
				    u01.add(u23).add(d02).add(d13).add(d23)), s45 = c[4]
			    .add(c[5]), d05 = s01.add(s45).multiply(f0)
			    .subtract(u01.add(d04)), d25 = s23.add(s45)
			    .multiply(t23).subtract(
				    u23.add(d23).add(d24).add(d35));
		    w[0] = (ECFieldElementFp2) ((ECFieldElementFp2) d24
			    .add(d33)).divideV().add(d00);
		    w[1] = (ECFieldElementFp2) ((ECFieldElementFp2) d25)
			    .divideV().add(d01);
		    w[2] = (ECFieldElementFp2) ((ECFieldElementFp2) d35)
			    .divideV().add(d02);
		    w[3] = (ECFieldElementFp2) d03;
		    w[4] = (ECFieldElementFp2) d04.add(d13).add(d22);
		    w[5] = (ECFieldElementFp2) d05.add(d23);
		} else {
		    ECFieldElement d00 = c[0].multiply(f0), d11 = c[1]
			    .multiply(f1), d22 = c[2].multiply(f2), d33 = c[3]
			    .multiply(f3), d44 = c[4].multiply(f4), d55 = c[5]
			    .multiply(f5), s01 = c[0].add(c[1]), t01 = f0
			    .add(f1), u01 = d00.add(d11), d01 = s01.multiply(
			    t01).subtract(u01), d02 = c[0].add(c[2]).multiply(
			    f0.add(f2)).subtract(d00.add(d22)), d04 = c[0].add(
			    c[4]).multiply(f0.add(f4)).subtract(d00.add(d44)), d13 = c[1]
			    .add(c[3]).multiply(f1.add(f3)).subtract(
				    d11.add(d33)), d15 = c[1].add(c[5])
			    .multiply(f1.add(f5)).subtract(d11.add(d55)), s23 = c[2]
			    .add(c[3]), t23 = f2.add(f3), u23 = d22.add(d33), d23 = s23
			    .multiply(t23).subtract(u23), d24 = c[2].add(c[4])
			    .multiply(f2.add(f4)).subtract(d22.add(d44)), d35 = c[3]
			    .add(c[5]).multiply(f3.add(f5)).subtract(
				    d33.add(d55)), s45 = c[4].add(c[5]), t45 = f4
			    .add(f5), u45 = d44.add(d55), d45 = s45.multiply(
			    t45).subtract(u45);
		    u01 = u01.add(d01);
		    u23 = u23.add(d23);
		    u45 = u45.add(d45);
		    ECFieldElement d03 = s01.add(s23).multiply(t01.add(t23))
			    .subtract(u01.add(u23).add(d02).add(d13)), d05 = s01
			    .add(s45).multiply(t01.add(t45)).subtract(
				    u01.add(u45).add(d04).add(d15)), d25 = s23
			    .add(s45).multiply(t23.add(t45)).subtract(
				    u23.add(u45).add(d24).add(d35));
		    w[0] = ((ECFieldElementFp2) d15.add(d24).add(d33))
			    .divideV().add(d00);
		    w[1] = ((ECFieldElementFp2) d25).divideV().add(d01);
		    w[2] = ((ECFieldElementFp2) d35.add(d44)).divideV()
			    .add(d02).add(d11);
		    w[3] = ((ECFieldElementFp2) d45).divideV().add(d03);
		    w[4] = ((ECFieldElementFp2) d55).divideV().add(d04)
			    .add(d13).add(d22);
		    w[5] = d05.add(d23);
		}
		;
		return new ECFieldElementFp12(w, false);
	    } else {
		throw new IllegalArgumentException(
			"F_p^12 multiplication must have argument p = "
				+ this.getPrime());
	    }
	} else {
	    throw new IllegalArgumentException(
		    "F_p^12 multiplication must have argument of type F_p^12.");
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
	ECFieldElement[] d = new ECFieldElementFp2[6];
	for (int i = 5; i >= 0; i--) {
	    d[i] = c[i].multiply(scalar);
	}
	;
	return new ECFieldElementFp12(d, false);
    }

    /**
     * Multiplies the current field element with a F_p^2 polynomial, as scalar.
     * 
     * @param scalar
     *            the field element that is multiplied with the current field
     * @return the scalar product
     */
    public ECFieldElement multiply(ECFieldElementFp2 scalar) {
	ECFieldElement[] d = new ECFieldElementFp2[6];
	for (int i = 5; i >= 0; i--) {
	    d[i] = c[i].multiply(scalar);
	}
	;
	return new ECFieldElementFp12(d, false);
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
	if (a instanceof ECFieldElementFp12) {
	    ECFieldElementFp12 f = (ECFieldElementFp12) a;
	    if (f.getPrime().equals(this.getPrime())) {
		return this.multiply(f.invert());
	    } else {
		throw new IllegalArgumentException(
			"F_p^12 subtraction must have argument p = "
				+ this.getPrime());
	    }
	} else {
	    throw new IllegalArgumentException(
		    "F_p^12 subtraction must have argument of type F_p^12.");
	}
    }

    // /**
    // * Takes the k-th power current field element, with argument k.
    // *
    // * @param k the power factor
    // * @return the power
    // */
    // public ECFieldElement plainExp(BigInteger k) {
    // ECFieldElement w = this;
    // for (int i = k.bitLength()-2; i >= 0; i--) {
    // w = w.square();
    // if (k.testBit(i)) {
    // w = w.multiply(this);
    // }
    // }
    // return w;
    // }

    /**
     * Negates the current field element, coefficient-wise.
     * 
     * @return the negated field.
     */
    public ECFieldElement negate() {
	return new ECFieldElementFp12(c[5].negate(), c[4].negate(), c[3]
		.negate(), c[2].negate(), c[1].negate(), c[0].negate(), false);
    }

    /**
     * Squares the current field element.
     * 
     * @return the square.
     */
    public ECFieldElement square() {
	// ECFieldElement
	// d00 = c[0].square(),
	// d11 = c[1].square(),
	// d22 = c[2].square(),
	// d33 = c[3].square(),
	// d44 = c[4].square(),
	// d55 = c[5].square(),
	// s01 = c[0].add(c[1]),
	// t01 = d00.add(d11),
	// d01 = s01.square().subtract(t01),
	// d02 = c[0].add(c[2]).square().subtract(d00.add(d22)),
	// d04 = c[0].add(c[4]).square().subtract(d00.add(d44)),
	// d13 = c[1].add(c[3]).square().subtract(d11.add(d33)),
	// d15 = c[1].add(c[5]).square().subtract(d11.add(d55)),
	// s23 = c[2].add(c[3]),
	// t23 = d22.add(d33),
	// d23 = s23.square().subtract(t23),
	// d24 = c[2].add(c[4]).square().subtract(d22.add(d44)),
	// d35 = c[3].add(c[5]).square().subtract(d33.add(d55)),
	// s45 = c[4].add(c[5]),
	// t45 = d44.add(d55),
	// d45 = s45.square().subtract(t45);
	// t01 = t01.add(d01);
	// t23 = t23.add(d23);
	// t45 = t45.add(d45);
	// ECFieldElement
	// d03 = s01.add(s23).square().subtract(t01.add(t23).add(d02).add(d13)),
	// d05 = s01.add(s45).square().subtract(t01.add(t45).add(d04).add(d15)),
	// d25 = s23.add(s45).square().subtract(t23.add(t45).add(d24).add(d35));
	// ECFieldElementFp2[] w = new ECFieldElementFp2[6];
	// w[0] =
	// (ECFieldElementFp2)((ECFieldElementFp2)d15.add(d24).add(d33)).divideV().add(d00);
	// w[1] =
	// (ECFieldElementFp2)((ECFieldElementFp2)d25).divideV().add(d01);
	// w[2] =
	// (ECFieldElementFp2)((ECFieldElementFp2)d35.add(d44)).divideV().add(d02).add(d11);
	// w[3] =
	// (ECFieldElementFp2)((ECFieldElementFp2)d45).divideV().add(d03);
	// w[4] =
	// (ECFieldElementFp2)((ECFieldElementFp2)d55).divideV().add(d04).add(d13).add(d22);
	// w[5] = (ECFieldElementFp2)d05.add(d23);
	// return new ECFieldElementFp12(w, false);
	return this.multiply(this);
    }

    /**
     * Takes the multiplicative inverse of the current field element.
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
	    ECFieldElement c = this.conjugate(1);
	    for (int i = 2; i < 6; i++) {
		c = c.multiply(this.conjugate(i));
	    }
	    ECFieldElement n = c.multiply(this);
	    // System.out.println("Only constant must be non-zero in: " + n);
	    ECFieldElementFp2 constant = ((ECFieldElementFp12) n).getCoeff(0), scalar = ((ECFieldElementFp2) (constant
		    .invert()));
	    c = ((ECFieldElementFp12) c).multiply(scalar);
	    return c;
	}
    }

    public ECFieldElement invert(BigInteger zeta) {
	if (this.isZero()) {
	    throw new IllegalArgumentException(
		    "Inverse of zero in F_p^2 is not defined.");
	} else {
	    ECFieldElement c = this.conjugate(1, zeta);
	    for (int i = 2; i < 6; i++) {
		c = c.multiply(this.conjugate(i, zeta));
	    }
	    ECFieldElement n = c.multiply(this);
	    // System.out.println("Only constant must be non-zero in: " + n);
	    ECFieldElementFp2 constant = ((ECFieldElementFp12) n).getCoeff(0), scalar = ((ECFieldElementFp2) (constant
		    .invert()));
	    c = ((ECFieldElementFp12) c).multiply(scalar);
	    return c;
	}
    }

    public ECFieldElement sqrt() {
	return this;
    }

    public boolean equals(Object other) {
	if (other == this) {
	    return true;
	}
	;
	if (!(other instanceof ECFieldElementFp12)) {
	    return false;
	} else {
	    ECFieldElementFp12 o = (ECFieldElementFp12) other;
	    return this.getPrime().equals(o.getPrime())
		    && this.getCoeff(0).equals(o.getCoeff(0))
		    && this.getCoeff(1).equals(o.getCoeff(1))
		    && this.getCoeff(2).equals(o.getCoeff(2))
		    && this.getCoeff(3).equals(o.getCoeff(3))
		    && this.getCoeff(4).equals(o.getCoeff(4))
		    && this.getCoeff(5).equals(o.getCoeff(5));
	}
    }

    // public BNField12 frobenius() {
    // /*
    // * z^p = sigma*(1+i)*z
    // * (z^2)^p = 2*sigma^2*i*z^2
    // * (z^3)^p = -2*sigma^3*(i-1)*z^3
    // * (z^4)^p = -4*sigma^4*z^4
    // * (z^5)^p = -4*sigma^5*(1+i)*z^5
    // */
    // BNField2[] w = new BNField2[6];
    // w[0] = v[0].conjugate();
    // w[1] = v[1].conjugate().multiply(bn.sigma).multiplyV();
    // w[2] = v[2].conjugate().multiply(bn.mzeta0).multiplyI();
    // w[3] = v[3].multiplyV().conjugate().multiply(bn.zeta0sigma);
    // w[4] = v[4].conjugate().multiply(bn.zeta1);
    // w[5] = v[5].conjugate().multiply(bn.zeta1sigma).multiplyV();
    // return new BNField12(bn, w);
    // }

    /**
     * Compute this^((p^2)^m), the m-th conjugate of this over GF(p^2).
     */
    public ECFieldElement conjugate(int m) {
	// brute force manner
	BigInteger p_square = this.getPrime().pow(2);
	return this.pow(p_square.pow(m));
    }

    /**
     * Compute this^((p^2)^m), the m-th conjugate of this over GF(p^2), using
     * auxiliary zeta argument (primitive cube root of unity mod p)
     */
    public ECFieldElement conjugate(int m, BigInteger zeta) {
	/*
	 * z^(p^2) = -zeta*z z^(p^4) = -(zeta+1)*z = zeta^2*z z^(p^6) = -z
	 * z^(p^8) = zeta*z z^(p^10) = (zeta+1)*z = -zeta^2*z
	 * 
	 * v = v_0 + v_1 z + v_2 z^2 + v_3 z^3 + v_4 z^4 + v_5 z^5 => v^(p^2) =
	 * v_0 - v_1zeta z - v_2(zeta+1) z^2 - v_3 z^3 + v_4zeta z^4 +
	 * v_5(zeta+1) z^5 v^(p^4) = v_0 - v_1(zeta+1) z + v_2zeta z^2 + v_3 z^3
	 * - v_4 z^4(zeta+1) + v_5zeta z^5 v^(p^6) = v_0 - v_1 z + v_2 z^2 - v_3
	 * z^3 + v_4 z^4 - v_5 z^5 v^(p^8) = v_0 + v_1zeta z - v_2(zeta+1) z^2 +
	 * v_3 z^3 + v_4zeta z^4 - v_5(zeta+1) z^5 v^(p^10) = v_0 + v_1(zeta+1)
	 * z + v_2zeta z^2 - v_3 z^3 - v_4 z^4(zeta+1) - v_5zeta z^5
	 */
	BigInteger zeta1 = zeta.add(BigInteger.valueOf(1)), mzeta = zeta
		.negate(), mzeta1 = zeta1.negate();
	ECFieldElementFp2[] w = new ECFieldElementFp2[6];
	w[0] = c[0];
	switch (m) {
	    default: // only to make the compiler happy
	    case 0:
		return this;
	    case 1:
		w[1] = (ECFieldElementFp2) c[1].multiply(mzeta);
		w[2] = (ECFieldElementFp2) c[2].multiply(mzeta1);
		w[3] = (ECFieldElementFp2) c[3].negate();
		w[4] = (ECFieldElementFp2) c[4].multiply(zeta);
		w[5] = (ECFieldElementFp2) c[5].multiply(zeta1);
		return new ECFieldElementFp12(w, false);
	    case 2:
		w[1] = (ECFieldElementFp2) c[1].multiply(mzeta1);
		w[2] = (ECFieldElementFp2) c[2].multiply(zeta);
		w[3] = (ECFieldElementFp2) c[3];
		w[4] = (ECFieldElementFp2) c[4].multiply(mzeta1);
		w[5] = (ECFieldElementFp2) c[5].multiply(zeta);
		return new ECFieldElementFp12(w, false);
	    case 3:
		w[1] = (ECFieldElementFp2) c[1].negate();
		w[2] = c[2];
		w[3] = (ECFieldElementFp2) c[3].negate();
		w[4] = c[4];
		w[5] = (ECFieldElementFp2) c[5].negate();
		return new ECFieldElementFp12(w, false);
	    case 4:
		w[1] = (ECFieldElementFp2) c[1].multiply(zeta);
		w[2] = (ECFieldElementFp2) c[2].multiply(mzeta1);
		w[3] = c[3];
		w[4] = (ECFieldElementFp2) c[4].multiply(zeta);
		w[5] = (ECFieldElementFp2) c[5].multiply(mzeta1);
		return new ECFieldElementFp12(w, false);
	    case 5:
		w[1] = (ECFieldElementFp2) c[1].multiply(zeta1);
		w[2] = (ECFieldElementFp2) c[2].multiply(zeta);
		w[3] = (ECFieldElementFp2) c[3].negate();
		w[4] = (ECFieldElementFp2) c[4].multiply(mzeta1);
		w[5] = (ECFieldElementFp2) c[5].multiply(mzeta);
		return new ECFieldElementFp12(w, false);
	}
    }

    public ECFieldElement frobenius(BigInteger zeta) {
	/*
	 * z^p = sigma*(1+i)*z (z^2)^p = 2*sigma^2*i*z^2 (z^3)^p =
	 * -2*sigma^3*(i-1)*z^3 (z^4)^p = -4*sigma^4*z^4 (z^5)^p =
	 * -4*sigma^5*(1+i)*z^5
	 */
	BigInteger p = this.getPrime(), zeta1 = zeta.add(BigInteger.valueOf(1)), sigma = p
		.subtract(BigInteger.valueOf(4)).modPow(
			p.subtract(BigInteger.valueOf(1)).subtract(
				p.add(BigInteger.valueOf(5)).divide(
					BigInteger.valueOf(24))), p), // (-1/4)^((p+5)/24)
	zetasigma = zeta.multiply(sigma).mod(p), zeta1sigma = zeta1.multiply(
		sigma).mod(p);
	ECFieldElementFp2[] w = new ECFieldElementFp2[6];
	w[0] = (ECFieldElementFp2) c[0].conjugate();
	w[1] = (ECFieldElementFp2) ((ECFieldElementFp2) ((ECFieldElementFp2) c[1]
		.conjugate()).multiply(sigma)).multiplyV();
	w[2] = (ECFieldElementFp2) ((ECFieldElementFp2) ((ECFieldElementFp2) c[2]
		.conjugate()).multiply(zeta.negate())).multiplyI();
	w[3] = (ECFieldElementFp2) ((ECFieldElementFp2) ((ECFieldElementFp2) c[3]
		.multiplyV()).conjugate()).multiply(zetasigma);
	w[4] = (ECFieldElementFp2) ((ECFieldElementFp2) c[4].conjugate())
		.multiply(zeta1);
	w[5] = (ECFieldElementFp2) ((ECFieldElementFp2) ((ECFieldElementFp2) c[5]
		.conjugate()).multiply(zeta1sigma)).multiplyV();
	return new ECFieldElementFp12(w, false);
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

    // /*
    // public BNField2 norm2() {
    // BNField12 c = this;
    // for (int i = 1; i < 6; i++) {
    // c = c.multiply(conjugate(i));
    // }
    // assert (c.v[1].isZero() && c.v[2].isZero() && c.v[3].isZero() &&
    // c.v[4].isZero() && c.v[5].isZero());
    // return c.v[0];
    // }

    // public BigInteger norm() {
    // return norm2().norm();
    // }
    // //*/

    // public BNField12 multiply(BigInteger k) {
    // BNField2[] w = new BNField2[6];
    // for (int i = 0; i < 6; i++) {
    // w[i] = v[i].multiply(k);
    // }
    // return new BNField12(bn, w);
    // }

    // public BNField12 multiply(BNField2 k) {
    // BNField2[] w = new BNField2[6];
    // for (int i = 0; i < 6; i++) {
    // w[i] = v[i].multiply(k);
    // }
    // return new BNField12(bn, w);
    // }

    // public BNField12 square() {
    // BNField2
    // d00 = v[0].square(),
    // d11 = v[1].square(),
    // d22 = v[2].square(),
    // d33 = v[3].square(),
    // d44 = v[4].square(),
    // d55 = v[5].square(),
    // s01 = v[0].add(v[1]),
    // t01 = d00.add(d11),
    // d01 = s01.square().subtract(t01),
    // d02 = v[0].add(v[2]).square().subtract(d00.add(d22)),
    // d04 = v[0].add(v[4]).square().subtract(d00.add(d44)),
    // d13 = v[1].add(v[3]).square().subtract(d11.add(d33)),
    // d15 = v[1].add(v[5]).square().subtract(d11.add(d55)),
    // s23 = v[2].add(v[3]),
    // t23 = d22.add(d33),
    // d23 = s23.square().subtract(t23),
    // d24 = v[2].add(v[4]).square().subtract(d22.add(d44)),
    // d35 = v[3].add(v[5]).square().subtract(d33.add(d55)),
    // s45 = v[4].add(v[5]),
    // t45 = d44.add(d55),
    // d45 = s45.square().subtract(t45);
    // t01 = t01.add(d01);
    // t23 = t23.add(d23);
    // t45 = t45.add(d45);
    // BNField2
    // d03 = s01.add(s23).square().subtract(t01.add(t23).add(d02).add(d13)),
    // d05 = s01.add(s45).square().subtract(t01.add(t45).add(d04).add(d15)),
    // d25 = s23.add(s45).square().subtract(t23.add(t45).add(d24).add(d35));
    // BNField2[] w = new BNField2[6];
    // w[0] = d15.add(d24).add(d33).divideV().add(d00);
    // w[1] = d25.divideV().add(d01);
    // w[2] = d35.add(d44).divideV().add(d02).add(d11);
    // w[3] = d45.divideV().add(d03);
    // w[4] = d55.divideV().add(d04).add(d13).add(d22);
    // w[5] = d05.add(d23);
    // return new BNField12(bn, w);
    // }

    // public BNField12 inverse() throws ArithmeticException {
    // BNField12 c = conjugate(1);
    // for (int i = 2; i < 6; i++) {
    // c = c.multiply(conjugate(i));
    // }
    // BNField12 n = c.multiply(this);
    // assert (n.v[1].isZero() && n.v[2].isZero() && n.v[3].isZero() &&
    // n.v[4].isZero() && n.v[5].isZero());
    // c = c.multiply(n.v[0].inverse());
    // return c;
    // }

    // private BNField12 plainExp(BigInteger k) {
    // BNField12 w = this;
    // for (int i = k.bitLength()-2; i >= 0; i--) {
    // w = w.square();
    // if (k.testBit(i)) {
    // w = w.multiply(this);
    // }
    // }
    // return w;
    // }

    // public BNField12 finExp() {
    // BNField12 f = this;
    // // p^12 - 1 = (p^6 - 1)*(p^2 + 1)*(p^4 - p^2 + 1)
    // try {
    // f = f.conjugate(3).multiply(f.inverse()); // f = f^(p^6 - 1)
    // } catch (ArithmeticException x) {
    // f = this; // this can only happen when this instance is not invertible,
    // i.e. zero
    // }
    // f = f.conjugate(1).multiply(f); // f = f^(p^2 + 1)
    // assert (f.inverse().equals(f.conjugate(3)));
    // BNField12 a;
    // if (bn.u.signum() >= 0) {
    // a = f.plainExp(bn.optOrd.add(_3)).conjugate(3);
    // } else {
    // a = f.plainExp(bn.optOrd.add(_3).negate());
    // }
    // BNField12 b = a.frobenius().multiply(a);
    // BNField12 c = f.frobenius();
    // BNField12 d = f.conjugate(1);
    // BNField12 e = c.multiply(f);
    // f =
    // c.conjugate(1).multiply(b.multiply(c.square()).multiply(d).plainExp(bn.t)).multiply(b).multiply(a).multiply(f.multiply(e.square()).square().square()).multiply(e);
    // return f;
    // }

    // /**
    // * Compute this^ks + Y^kr.
    // *
    // */
    // public BNField12 simultaneous(BigInteger ks, BigInteger kr, BNField12 Y)
    // {
    // BNField12[] hV = new BNField12[16];
    // BNField12 P = this;
    // if (ks.signum() < 0) {
    // ks = ks.negate();
    // P = P.conjugate(3);
    // }
    // if (kr.signum() < 0) {
    // kr = kr.negate();
    // Y = Y.conjugate(3);
    // }
    // hV[0] = bn.Fp12_1;
    // hV[1] = P;
    // hV[2] = Y;
    // hV[3] = P.multiply(Y);
    // for (int i = 4; i < 16; i += 4) {
    // hV[i] = hV[i >> 2].square();
    // hV[i + 1] = hV[i].multiply(hV[1]);
    // hV[i + 2] = hV[i].multiply(hV[2]);
    // hV[i + 3] = hV[i].multiply(hV[3]);
    // }
    // int t = Math.max(kr.bitLength(), ks.bitLength());
    // BNField12 R = bn.Fp12_1;
    // for (int i = (((t + 1) >> 1) << 1) - 1; i >= 0; i -= 2) {
    // int j = (kr.testBit(i ) ? 8 : 0) |
    // (ks.testBit(i ) ? 4 : 0) |
    // (kr.testBit(i-1) ? 2 : 0) |
    // (ks.testBit(i-1) ? 1 : 0);
    // R = R.square().square().multiply(hV[j]);
    // }
    // return R;
    // }

    // public BNField12 exp(BigInteger k) {
    // BNField12 P = this;
    // if (k.signum() < 0) {
    // k = k.negate();
    // P = P.conjugate(3);
    // }
    // BigInteger r = bn.u.shiftLeft(1).add(_1); // 2*u + 1
    // BigInteger t = bn.u.multiply(_3).add(_1).multiply(bn.u.shiftLeft(1)); //
    // (3*u + 1)*2*u = 6*u^2 + 2*u
    // BigInteger halfn = bn.n.shiftRight(1);
    // BigInteger kr = k.multiply(r);
    // if (kr.mod(bn.n).compareTo(halfn) <= 0) {
    // kr = kr.divide(bn.n);
    // } else {
    // kr = kr.divide(bn.n).add(_1);
    // }
    // BigInteger kt = k.multiply(t);
    // if (kt.mod(bn.n).compareTo(halfn) <= 0) {
    // kt = kt.divide(bn.n);
    // } else {
    // kt = kt.divide(bn.n).add(_1);
    // }
    // // [k - (kr*B_11 + kt*B_21), -(kr*B_12 + kt*B_22)]
    // /*
    // * [kr, kt]*[2*u + 1 6*u^2 + 2*u]
    // * [6*u^2 + 4*u + 1 -(2*u + 1)]
    // */
    // BigInteger sr = k.subtract(kr.multiply(r).add(kt.multiply(t.add(r))));
    // BigInteger st = kr.multiply(t).subtract(kt.multiply(r));
    // BNField12 f = P.conjugate(1);
    // BNField2[] w = new BNField2[6];
    // /*
    // f^rho[0] = f^{p^2}[0] = f[0]
    // f^rho[1] = -f^{p^2}[1]
    // f^rho[2] = f^{p^2}[2]
    // f^rho[3] = f[3]
    // f^rho[4] = f^{p^2}[4]
    // f^rho[5] = -f^{p^2}[5]
    // */
    // w[0] = P.v[0];
    // w[1] = f.v[1].negate();
    // w[2] = f.v[2];
    // w[3] = P.v[3];
    // w[4] = f.v[4];
    // w[5] = f.v[5].negate();
    // BNField12 y = new BNField12(bn, w);
    // return P.simultaneous(sr, st, y);
    // }

    // public BNField12 simultaneous(BigInteger kP, BNField12 P, BigInteger kQ,
    // BNField12 Q, BigInteger kR, BNField12 R, BigInteger kS, BNField12 S) {
    // BNField12[] hV = new BNField12[16];
    // if (kP.signum() < 0) {
    // kP = kP.negate(); P = P.conjugate(3);
    // }
    // if (kQ.signum() < 0) {
    // kQ = kQ.negate(); Q = Q.conjugate(3);
    // }
    // if (kR.signum() < 0) {
    // kR = kR.negate(); R = R.conjugate(3);
    // }
    // if (kS.signum() < 0) {
    // kS = kS.negate(); S = S.conjugate(3);
    // }
    // hV[0] = bn.Fp12_1;
    // hV[1] = P; hV[2] = Q; hV[4] = R; hV[8] = S;
    // for (int i = 2; i < 16; i <<= 1) {
    // for (int j = 1; j < i; j++) {
    // hV[i + j] = hV[i].multiply(hV[j]);
    // }
    // }
    // int t = Math.max(Math.max(kP.bitLength(), kQ.bitLength()),
    // Math.max(kR.bitLength(), kS.bitLength()));
    // BNField12 V = bn.Fp12_1;
    // for (int i = t - 1; i >= 0; i--) {
    // int j = (kS.testBit(i) ? 8 : 0) |
    // (kR.testBit(i) ? 4 : 0) |
    // (kQ.testBit(i) ? 2 : 0) |
    // (kP.testBit(i) ? 1 : 0);
    // V = V.square().multiply(hV[j]);
    // }
    // return V;
    // }

    // public BNField12 fastSimultaneous(BigInteger ks, BigInteger kr, BNField12
    // y) {
    // BigInteger m = bn.t.subtract(_1).abs();
    // BNField12 g = this;
    // if (ks.signum() < 0) {
    // g = g.conjugate(3);
    // ks = ks.negate();
    // }
    // if (kr.signum() < 0) {
    // y = y.conjugate(3);
    // kr = kr.negate();
    // }
    // BNField12 gg = g.frobenius().conjugate((bn.t.signum() > 0) ? 0 : 3);
    // BNField12 yy = y.frobenius().conjugate((bn.t.signum() > 0) ? 0 : 3);
    // return simultaneous(ks.mod(m), g, ks.divide(m), gg, kr.mod(m), y,
    // kr.divide(m), yy);
    // }

    public String toString() {
	String s = "";
	for (int i = 5; i >= 0; i--) {
	    if (!c[i].isZero()) {
		if (!s.isEmpty()) {
		    s += "+";
		}
		if (i == 0) {
		    s += c[i];
		} else if (i == 1) {
		    s += c[i] + " * X ";
		} else {
		    s += c[i] + " * X^" + i + " ";
		}
	    }
	}
	return s;
	// return "( " + c[5] + " * X^5  +  " + c[4] + " * X^4  +  " + c[3] +
	// " * X^3  +  " +
	// c[2] + " * X^2  +  " + c[1] + " * X  +  " + c[0] + " )";
    }
}
