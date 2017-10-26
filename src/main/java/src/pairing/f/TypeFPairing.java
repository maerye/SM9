package src.pairing.f;


import mcl.bn254.Fp12;
import src.api.*;
import src.field.curve.CurveField;
import src.field.gt.GTFiniteField;
import src.field.poly.PolyElement;
import src.field.poly.PolyField;
import src.field.poly.PolyModElement;
import src.field.poly.PolyModField;
import src.field.quadratic.QuadraticField;
import src.field.z.ZrField;
import src.util.math.BigIntegerUtils;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.List;

/**
 * @author Angelo De Caro (jpbclib@gmail.com)
 */
public class TypeFPairing extends AbstractPairing {
    protected PairingParameters curveParams;

    protected BigInteger x;
    protected BigInteger q;
    protected BigInteger r;
    protected BigInteger b;
    protected BigInteger beta;
    protected BigInteger alpha0;
    protected BigInteger alpha1;

    protected Element xPowq2, xPowq6, xPowq8;
    protected Element negAlpha, negAlphaInv;
    protected BigInteger tateExp;

    protected Field Fq, Fq2x;
    protected Field Fq2;
    protected Field Fq4x;
    protected PolyModField Fq4,Fq12;
    protected CurveField Eq, etwist;


    public TypeFPairing(PairingParameters curveParams) {
        this(new SecureRandom(), curveParams);
    }

    public TypeFPairing(SecureRandom random, PairingParameters curveParams) {
        super(random);

        this.curveParams = curveParams;

        initParams();
        initMap();
        initFields();
    }

    public boolean isSymmetric() {
        return false;
    }


    protected void initParams() {
        // validate the type
        String type = curveParams.getString("type");
        if (type == null || !type.equalsIgnoreCase("f"))
            throw new IllegalArgumentException("Type not valid. Found '" + type + "'. Expected 'f'.");

        // load params
        q = curveParams.getBigInteger("q");
        r = curveParams.getBigInteger("r");
        b = curveParams.getBigInteger("b");
        beta = curveParams.getBigInteger("beta");
        alpha0 = curveParams.getBigInteger("alpha0");
        alpha1 = curveParams.getBigInteger("alpha1");
        x=curveParams.getBigInteger("x");
    }


    protected void initFields() {
        // Init Zr
        Zr = initFp(r, null);

        // Init Fq
        Fq = initFp(q, beta);

        // Init Fq2
        Fq2 = initQuadratic();

        // Init Fq2x
        Fq2x = initPoly();

        Point tmp = (Point) Fq2.newElement();
        tmp.getX().set(alpha0);
        tmp.getY().set(alpha1);
        negAlpha = tmp;



        PolyElement irreduciblePoly = (PolyElement) Fq2x.newElement();
        List<Element> irreduciblePolyCoeff = irreduciblePoly.getCoefficients();
        irreduciblePolyCoeff.add(negAlpha.duplicate());
        for (int i = 1; i < 6; i++)
            irreduciblePolyCoeff.add(Fq2.newElement());
        irreduciblePolyCoeff.add(Fq2.newOneElement());

        // init Fq12
       // Fq12 = initPolyMod(irreduciblePoly);

        PolyElement irreduciblePoly2 = (PolyElement) Fq2x.newElement();
        List<Element> irreduciblePolyCoeff2 = irreduciblePoly2.getCoefficients();
        irreduciblePolyCoeff2.add(negAlpha.duplicate());
        irreduciblePolyCoeff2.add(Fq2.newElement());
        irreduciblePolyCoeff2.add(Fq2.newOneElement());
        Fq4=new PolyModField(random,irreduciblePoly2);
        Fq4x=new PolyField(random,Fq4);


        Polynomial v=(Polynomial) Fq4.newElement();
        v.getCoefficient(1).set(1);
        v.negate();

        PolyElement irreduc2=(PolyElement)Fq4x.newElement();
        List<Element> irreduc2coeff=irreduc2.getCoefficients();
        irreduc2coeff.add(v);
        irreduc2coeff.add(Fq4.newElement());
        irreduc2coeff.add(Fq4.newElement());
        irreduc2coeff.add(Fq4.newOneElement());

        Fq12=new PolyModField(random,irreduc2);


        negAlphaInv = negAlpha.negate().duplicate().invert();

        // Initialize the curve Y^2 = X^3 + b.
        Eq = initEq();

        // Initialize the curve Y^2 = X^3 - alpha0 b - alpha1 sqrt(beta) b.
        etwist = initEqMap();

        // ndonr temporarily holds the trace.
        BigInteger ndonr = q.subtract(r).add(BigInteger.ONE);

        // TODO: We can use a smaller quotientCmp, but I have to figure out
        // BN curves again.
        ndonr = BigIntegerUtils.pbc_mpz_curve_order_extn(q, ndonr, 12);
        ndonr = ndonr.divide(r);
        ndonr = ndonr.divide(r);
        etwist.setQuotientCmp(ndonr);

        /*
            For k = 12, the final exponent becomes
            (q^12-1)/r = (p^6 - 1 )(p^2+1) ( (p^4-p^2+1)/r )
            Lets compute the final factor (p^4-p^2+1)/r
        */
        tateExp = q.multiply(q).subtract(BigInteger.ONE).multiply(q).multiply(q).add(BigInteger.ONE).divide(r);

        PolyModElement polyModElement = Fq12.newElement();
        polyModElement.getCoefficient(1).setToOne();
        polyModElement.pow(q);
        polyModElement.pow(q);
        xPowq2 = polyModElement.getCoefficient(1).duplicate();
        polyModElement.pow(q);
        polyModElement.pow(q);
        polyModElement.pow(q);
        polyModElement.pow(q);
        xPowq6 = polyModElement.getCoefficient(1).duplicate();
        polyModElement.pow(q);
        polyModElement.pow(q);
        xPowq8 = polyModElement.getCoefficient(1).duplicate();

        // Init G1, G2, GT
        G1 = Eq;
        G2 = etwist;
        GT = initGT();
    }

    protected Field initFp(BigInteger order, BigInteger nqr) {
        return new ZrField(random, order, nqr);
    }

    protected CurveField initEq() {
        return new CurveField(random, Fq.newElement(), Fq.newElement().set(b), r);
    }

    protected CurveField initEqMap() {
        Point tmp = (Point) Fq2.newElement();
        tmp.getX().set(Fq.newElement().set(alpha0).negate().mul(b));
        tmp.getY().set(Fq.newElement().set(alpha1).negate().mul(b));
        return new CurveField(random, Fq2.newElement(), tmp, r);
    }

    protected PolyField initPoly() {
        return new PolyField(random, Fq2);
    }

    protected PolyModField initPolyMod(PolyElement irred) {
        return new PolyModField(random, irred);
    }

    protected QuadraticField initQuadratic() {
        return new QuadraticField(random, Fq);
    }

    protected Field initGT() {
        return new GTFiniteField(random, r, pairingMap, Fq12);
    }

    public  PolyModField getFp12(){return Fq12;}
    public PolyModField getFq4(){return Fq4;}
    public Field getFq2(){return Fq2;}
    public BigInteger getR(){return r;}
    protected void initMap() {
      //  pairingMap = new TypeFTateNoDenomMillerPairingMap(this);
       pairingMap=new TypeFRatePairingMap(this);
    }


}
