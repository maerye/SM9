/**
 *
 * ECPointFp2.java
 *
 * Point on elliptic curve over field F_p^2, i.e. over ECFieldElementFp2.
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

public class ECPointFp2 extends ECPoint.Fp {

    /**
     * Creates a point like in the super class.
     */
    public ECPointFp2(ECCurve curve, ECFieldElement x, ECFieldElement y) {
	super((ECCurveFp2) curve, (ECFieldElementFp2) x, (ECFieldElementFp2) y);
    }

    /**
     * Creates a point in the F_p^2 plane from a point in the F_p plane <br>
     * via the inclusion F_p --> F_p^2.
     */
    public ECPointFp2(Fp P) {
	super(new ECCurveFp2((ECCurve.Fp) P.getCurve()), new ECFieldElementFp2(
		(ECFieldElement.Fp) P.getX()), new ECFieldElementFp2(
		(ECFieldElement.Fp) P.getY()));
    }

    /**
     * Adaptation of the super method to ensure that an ECPointFp2 is used as
     * argument and is returned.
     */
    public ECPoint add(ECPoint b) {
	if (!(b instanceof ECPointFp2)) {
	    throw new IllegalArgumentException(
		    "Argument must be an instance of ECPointFp2 in ECPointFp2 addition.");
	} else {
	    ECPoint R = super.add(b);
	    return new ECPointFp2(R.getCurve(), R.getX(), R.getY());
	}
    }

    /**
     * Adaptation of the super method to ensure that an ECPointFp2 is used as
     * argument and is returned.
     */
    public ECPoint subtract(ECPoint b) {
	if (!(b instanceof ECPointFp2)) {
	    throw new IllegalArgumentException(
		    "Argument must be an instance of ECPointFp2 in ECPointFp2 subtraction.");
	} else {
	    ECPoint R = super.subtract(b);
	    return new ECPointFp2(R.getCurve(), R.getX(), R.getY());
	}
    }

    /**
     * Adaptation of the super method to ensure that an ECPointFp2 is used as
     * argument and is returned.
     */
    public ECPoint multiply(BigInteger k) {
	ECPoint R = super.multiply(k);
	return new ECPointFp2(R.getCurve(), R.getX(), R.getY());
    }

    /**
     * Adaptation of the super method to ensure that an ECPointFp2 is returned.
     */
    public ECPoint negate() {
	ECPoint R = super.negate();
	return new ECPointFp2(R.getCurve(), R.getX(), R.getY());
    }

    /**
     * Adaptation of the super method to ensure that an ECPointFp2 is returned.
     */
    public ECPoint twice() {
	ECPoint R = super.twice();
	return new ECPointFp2(R.getCurve(), R.getX(), R.getY());
    }
}