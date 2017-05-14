package src.field.poly;


import src.api.Element;
import src.api.Field;
import src.field.base.AbstractFieldOver;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.List;

/**
 * @author Angelo De Caro (jpbclib@gmail.com)
 */
public class PolyModField<F extends Field> extends AbstractFieldOver<F, PolyModElement> {
    protected PolyElement irreduciblePoly;
    protected PolyModElement nqr;
    protected BigInteger order;
    protected int n;
    protected int fixedLengthInBytes;

    protected PolyModElement[] xpwr;


    public PolyModField(SecureRandom random, F targetField, int cyclotomicPolyDegree) {
        super(random, targetField);

        PolyField polyField = new PolyField(random, targetField);
        irreduciblePoly = polyField.newElement();

        List<Element> coefficients = irreduciblePoly.getCoefficients();
        coefficients.add(polyField.getTargetField().newElement().setToOne());
        for (int i = 1; i < cyclotomicPolyDegree; i++) {
            coefficients.add(polyField.getTargetField().newZeroElement());
        }
        coefficients.add(polyField.getTargetField().newElement().setToOne());

        init(null);
    }

    public PolyModField(SecureRandom random, PolyElement irreduciblePoly) {
        this(random, irreduciblePoly, null);
    }

    public PolyModField(SecureRandom random, PolyElement irreduciblePoly, BigInteger nqr) {
        super(random, (F) irreduciblePoly.getField().getTargetField());

        this.irreduciblePoly = irreduciblePoly;
        init(nqr);
    }

    
    protected void init(BigInteger nqr) {
        this.n = irreduciblePoly.getDegree();

        this.order = targetField.getOrder().pow(irreduciblePoly.getDegree());
        if (nqr != null) {
            this.nqr = newElement();
            this.nqr.getCoefficient(0).set(nqr);
        }

//        if (order.compareTo(BigInteger.ZERO) != 0)
            computeXPowers();

        if (targetField.getLengthInBytes() < 0) {
            //f->length_in_bytes = fq_length_in_bytes;
            fixedLengthInBytes = -1;
        } else {
            fixedLengthInBytes = targetField.getLengthInBytes() * n;
        }
    } 
    

    public PolyModElement newElement() {
        return new PolyModElement(this);
    }

    public BigInteger getOrder() {
        return order;
    }

    public PolyModElement getNqr() {
        return nqr;
    }

    public int getLengthInBytes() {
        return fixedLengthInBytes;
    }

    public int getN() {
        return n;
    }


    /**
     * compute x^n,...,x^{2n-2} mod poly
     */
    protected void computeXPowers() {
        xpwr = new PolyModElement[n];

        for (int i = 0; i < n; i++) {
            xpwr[i] = newElement();
        }

        xpwr[0].setFromPolyTruncate(irreduciblePoly).negate();
        PolyModElement p0 = newElement();

        for (int i = 1; i < n; i++) {
            List<Element> coeff = xpwr[i - 1].getCoefficients();
            List<Element> coeff1 = xpwr[i].getCoefficients();

            coeff1.get(0).setToZero();

            for (int j = 1; j < n; j++) {
                coeff1.get(j).set(coeff.get(j - 1));
            }
            p0.set(xpwr[0]).polymodConstMul(coeff.get(n - 1));

            xpwr[i].add(p0);
        }

//        for (PolyModElement polyModElement : xpwr) {
//            System.out.println("xprw = " + polyModElement);
//        }

        /*
        polymod_field_data_ptr p = field - > data;
        element_t p0;
        element_ptr pwrn;
        element_t * coefficients,*coeff1;
        int i, j;
        int n = p - > n;
        element_t * xpwr;

        xpwr = p - > xpwr;

        element_init(p0, field);
        for (i = 0; i < n; i++) {
            element_init(xpwr[i], field);
        }
        pwrn = xpwr[0];
        element_poly_to_polymod_truncate(pwrn, poly);
        element_neg(pwrn, pwrn);

        for (i = 1; i < n; i++) {
            coefficients = xpwr[i - 1] - > data;
            coeff1 = xpwr[i] - > data;

            element_set0(coeff1[0]);
            for (j = 1; j < n; j++) {
                element_set(coeff1[j], coefficients[j - 1]);
            }
            polymod_const_mul(p0, coefficients[n - 1], pwrn);
            element_add(xpwr[i], xpwr[i], p0);
        }
        element_clear(p0);
        */
    }

}
