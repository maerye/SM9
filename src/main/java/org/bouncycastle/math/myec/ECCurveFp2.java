package org.bouncycastle.math.myec;

import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.field.FiniteFields;

import java.math.BigInteger;

/**
 * Class for an elliptic curve over F_p^2
 */
public class ECCurveFp2 extends ECCurve {
    ECPoint infinity;

    public ECPoint getInfinity() {
	return infinity;
    }

	protected ECPoint decompressPoint(int yTilde, BigInteger X1) {
		return null;
	}

	BigInteger q;

    public ECCurveFp2(BigInteger q, ECFieldElementFp2 a, ECFieldElementFp2 b) {

    	super(FiniteFields.getPrimeField(q));
	this.q = q;
	this.a = a;
	this.b = b;
	infinity = new ECPointFp2(this, null, null);
    }

    public ECCurveFp2(Fp c) {

    	super(FiniteFields.getPrimeField(c.getQ()));
	q = c.getQ();

	a = new ECFieldElementFp2((ECFieldElement.Fp) c.getA());
	b = new ECFieldElementFp2((ECFieldElement.Fp) (c.getB()));
	infinity = new ECPointFp2(this, null, null);
    }

    public BigInteger getQ() {
	return q;
    }

    public int getFieldSize() {
	// number of bits of q^2
	return 2 * q.bitLength();
    }

    public ECFieldElement fromBigInteger(BigInteger x) {
	return new ECFieldElementFp2(q, x);
    }

	public boolean isValidFieldElement(BigInteger x) {
		return false;
	}

	protected ECCurve cloneCurve() {
		return null;
	}

	protected ECPoint createRawPoint(ECFieldElement x, ECFieldElement y, boolean withCompression) {
		return null;
	}

	protected ECPoint createRawPoint(ECFieldElement x, ECFieldElement y, ECFieldElement[] zs, boolean withCompression) {
		return null;
	}

	public ECPoint decodePoint(byte[] encoded) {
	return null;
    }

    public boolean equals(Object o) {
	if (o == this) {
	    return true;
	}
	if (!(o instanceof ECCurveFp2)) {
	    return false;
	}
	ECCurveFp2 other = (ECCurveFp2) o;
	return q.equals(other.q) && a.equals(other.a) && b.equals(other.b);
    }

}
