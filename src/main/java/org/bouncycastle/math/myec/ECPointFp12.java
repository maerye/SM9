/**
 *
 * ECPointFp12.java
 *
 * Point on elliptic curve over field F_p^12
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

import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECPoint;

import java.math.BigInteger;

public class ECPointFp12 extends ECPoint.Fp {

    /**
     * Creates a point like in the super class.
     */
    public ECPointFp12(ECCurve curve, ECFieldElement x, ECFieldElement y) {
	super(curve, (ECFieldElementFp12) x, (ECFieldElementFp12) y);
    }

    /**
     * Creates a point in the F_p^12 plane from a point in the F_p plane <br>
     * via the inclusion F_p --> F_p^2.
     */
    public ECPointFp12(ECPointFp2 P) {
	super(new ECCurveFp12((ECCurveFp2) P.getCurve()),
		new ECFieldElementFp12((ECFieldElementFp2) P.getX(), 0),
		new ECFieldElementFp12((ECFieldElementFp2) P.getY(), 0));
    }

    /**
     * Creates a point in the F_p^12 plane from a point in the F_p plane <br>
     * via the inclusion F_p --> F_p^2.
     */
    public ECPointFp12(Fp P) {
	super(new ECCurveFp12((ECCurve.Fp) P.getCurve()),
		new ECFieldElementFp12((ECFieldElement.Fp) P.getX()),
		new ECFieldElementFp12((ECFieldElement.Fp) P.getY()));
    }

    /**
     * Adaptation of the super method to ensure that an ECPointFp12 is used as
     * argument and is returned.
     */
    public ECPoint add(ECPoint b) {
	if (!(b instanceof ECPointFp12)) {
	    throw new IllegalArgumentException(
		    "Argument must be an instance of ECPointFp12 in ECPointFp12 addition.");
	} else {
	    ECPoint R = super.add(b);
	    return new ECPointFp12(R.getCurve(), R.getX(), R.getY());
	}
    }

    /**
     * Adaptation of the super method to ensure that an ECPointFp12 is used as
     * argument and is returned.
     */
    public ECPoint subtract(ECPoint b) {
	if (!(b instanceof ECPointFp12)) {
	    throw new IllegalArgumentException(
		    "Argument must be an instance of ECPointFp12 in ECPointFp12 subtraction.");
	} else {
	    ECPoint R = super.subtract(b);
	    return new ECPointFp12(R.getCurve(), R.getX(), R.getY());
	}
    }

    /**
     * Adaptation of the super method to ensure that an ECPointFp12 is used as
     * argument and is returned.
     */
    public ECPoint multiply(BigInteger k) {
	ECPoint R = super.multiply(k);
	return new ECPointFp12(R.getCurve(), R.getX(), R.getY());
    }

    /**
     * Adaptation of the super method to ensure that an ECPointFp12 is returned.
     */
    public ECPoint negate() {
	ECPoint R = super.negate();
	return new ECPointFp12(R.getCurve(), R.getX(), R.getY());
    }

    /**
     * Adaptation of the super method to ensure that an ECPointFp12 is returned.
     */
    public ECPoint twice() {
	if (this.isInfinity()) {
	    // Twice identity element (point at infinity) is identity
	    return this;
	}

	// We have to repeat this implementation, the meaning of isZero here is
	// different than in
	// regular ECPoints
	if (((ECFieldElementFp12) this.y).isZero()) {
	    // if y1 == 0, then (x1, y1) == (x1, -y1)
	    // and hence this = -this and thus 2(x1, y1) == infinity
	    return this.curve.getInfinity();
	}

	ECFieldElement TWO = this.curve.fromBigInteger(BigInteger.valueOf(2));
	ECFieldElement THREE = this.curve.fromBigInteger(BigInteger.valueOf(3));
	ECFieldElement gamma = this.x.square().multiply(THREE).add(curve.getA())
		.divide(y.multiply(TWO));

	ECFieldElement x3 = gamma.square().subtract(this.x.multiply(TWO));
	ECFieldElement y3 = gamma.multiply(this.x.subtract(x3))
		.subtract(this.y);

	return new ECPointFp12(curve, x3, y3);

    }

    public boolean isZero() {
	return ((ECFieldElementFp12) this.getX()).isZero()
		&& ((ECFieldElementFp12) this.getY()).isZero();
    }

}