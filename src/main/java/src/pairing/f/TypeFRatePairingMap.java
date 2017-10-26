package src.pairing.f;

import com.sun.xml.internal.bind.v2.model.core.EnumLeafInfo;
import mcl.bn254.Fp;
import src.api.Element;
import src.api.Pairing;
import src.api.Point;
import src.api.Polynomial;
import src.field.gt.GTFiniteElement;
import src.field.gt.GTFiniteField;
import src.field.poly.PolyModField;
import src.pairing.f.map.AbstractPairingMap;

import java.math.BigInteger;

/**
 * Created by mzy on 2017/5/15.
 */
public class TypeFRatePairingMap extends AbstractPairingMap{

    private TypeFPairing pairingdata;

    public TypeFRatePairingMap(TypeFPairing pairing){
        super(pairing);
        this.pairingdata=pairing;
    }

    public Element pairing(Point P, Point Q) {

        BigInteger a=pairingdata.x.multiply(BigInteger.valueOf(6)).add(BigInteger.valueOf(2));

        Point t=(Point) Q.duplicate();
        Polynomial f=(Polynomial) pairingdata.Fq12.newOneElement();

          for (int i=a.bitLength()-2;i>=0;i--){
            f.square();
              f.mul(line(t,P));
              t.add(t);
            if(a.testBit(i)){
                f.mul(line(t,Q,P));
                t.add(Q);
            }
        }


        Point Q11=fobasmiracl(Q);
        Point Q22=fobasmiracl(Q11);
        f.mul(line(t,Q11,P));
        t.add(Q11);
        f.mul(line(t,(Point) Q22.negate(),P));
        t.add(Q22);
        BigInteger q=pairingdata.q;
        Element e=f.duplicate().pow(q.pow(8).add(q.pow(6)).subtract(q.pow(2)).subtract(BigInteger.ONE)).pow(pairingdata.tateExp);

        return new GTFiniteElement(this,(GTFiniteField) pairingdata.getGT(),e);


    }



    public void finalPow(Element element) {
        element.set(tateExp((Polynomial) element));
    }


    public Element tateExp(Polynomial element) {

        Polynomial x = pairingdata.Fq12.newElement();
        Polynomial y = pairingdata.Fq12.newElement();

        qPower(element, y, pairingdata.xPowq8);
        qPower(element, x, pairingdata.xPowq6);
        y.mul(x);
        qPower(element, x, pairingdata.xPowq2);

        return y.mul(x.mul(element).invert()).pow(pairingdata.tateExp);
    }

    private void qPower(Polynomial element, Polynomial e1, Element e) {
        e1.getCoefficient(0).set(element.getCoefficient(0));
        e1.getCoefficient(1).set(element.getCoefficient(1).duplicate().mul(e));

        Element epow = e.duplicate().square();
        e1.getCoefficient(2).set(element.getCoefficient(2).duplicate().mul(epow));
        e1.getCoefficient(3).set(element.getCoefficient(3).duplicate().mul(epow.mul(e)));
        e1.getCoefficient(4).set(element.getCoefficient(4).duplicate().mul(epow.mul(e)));
        e1.getCoefficient(5).set(element.getCoefficient(5).duplicate().mul(epow.mul(e)));
    }



    /**
     *
     * @param A point at E(Fp2)
     * @param B point at E(Fp2)
     * @param C point at E(Fp)
     * @return Fp12 Element
     */
    public Element line(Point A, Point B, Point C){
        Element ax=A.getX().duplicate();
        Element ay=A.getY().duplicate();
        Element bx=B.getX().duplicate();
        Element by=B.getY().duplicate();
        Element cx=C.getX().duplicate();
        Element cy=C.getY().duplicate();
        Point lamda=(Point) ax.getField().newElement();
        lamda=(Point)ay.duplicate().sub(by).div(ax.duplicate().sub(bx));
        Element cof3=by.duplicate().sub(lamda.duplicate().mul(bx));
        Element cof5=lamda.duplicate().mulZn(cx);

        Polynomial result=pairingdata.Fq12.newElement();
        Element betaInvert=pairingdata.negAlphaInv;

        Point tempfp2=(Point)ax.getField().newElement();
        tempfp2.getX().set(cy.negate());
        tempfp2.getY().setToZero();

//        result.getCoefficient(0).set(tempfp2);
//        result.getCoefficient(3).set(cof3.mul(betaInvert));
//        result.getCoefficient(5).set(cof5.mul(betaInvert));
//        result.getCoefficient(0).set(tempfp2.mul(pairingdata.negAlpha));
//        result.getCoefficient(3).set(cof3);
//        result.getCoefficient(5).set(cof5);
        Polynomial r0=(Polynomial) result.getCoefficient(0);
        Polynomial r2=(Polynomial) result.getCoefficient(2);
        r0.getCoefficient(0).set(tempfp2);
        r0.getCoefficient(1).set(cof3.mul(betaInvert));
        r2.getCoefficient(1).set(cof5.mul(betaInvert));
        return result;
    }

    public Element line(Point A,Point C){
        Element ax=A.getX().duplicate();
        Element ay=A.getY().duplicate();
        Element cx=C.getX().duplicate();
        Element cy=C.getY().duplicate();

       Element lamda= ax.getField().newElement();
       lamda=ax.duplicate().square().mul(3).div(ay.duplicate().mul(2));

       Element cof3=ay.duplicate().sub(lamda.duplicate().mul(ax));
       Element cof5=lamda.duplicate().mulZn(cx);

       Polynomial result=pairingdata.Fq12.newElement();
       Element betaInvert=pairingdata.negAlphaInv;

       Point tempfp2=(Point)ax.getField().newElement();
       tempfp2.getX().set(cy.negate());
       tempfp2.getY().setToZero();
//        result.getCoefficient(0).set(tempfp2);
//       result.getCoefficient(3).set(cof3.mul(betaInvert));
//       result.getCoefficient(5).set(cof5.mul(betaInvert));
        Polynomial r0=(Polynomial) result.getCoefficient(0);
        Polynomial r2=(Polynomial) result.getCoefficient(2);
        r0.getCoefficient(0).set(tempfp2);
        r0.getCoefficient(1).set(cof3.mul(betaInvert));
        r2.getCoefficient(1).set(cof5.mul(betaInvert));
        return result;

    }

    public Element lineasFp12(Point A, Point C){
        Element ax=A.getX().duplicate();
        Element ay=A.getY().duplicate();
        Element cx=C.getX().duplicate();
        Element cy=C.getY().duplicate();

        Polynomial ax12,ay12,cx12,cy12,lamdaT,lamdaB,lamda;
        ax12=pairingdata.Fq12.newElement();
        ay12=pairingdata.Fq12.newElement();
        cx12=pairingdata.Fq12.newElement();
        cy12=pairingdata.Fq12.newElement();
        ax12.getCoefficient(4).set(ax.mul(pairingdata.negAlphaInv));
        ay12.getCoefficient(3).set(ay.mul(pairingdata.negAlphaInv));
        Point tmpfp2=(Point) ax.getField().newElement();
        tmpfp2.getX().set(cx);
        tmpfp2.getY().setToZero();
        cx12.getCoefficient(0).set(tmpfp2);

        tmpfp2.getX().set(cy);
        cy12.getCoefficient(0).set(tmpfp2);

        lamdaT=(Polynomial) (ax12.duplicate().square()).mul(3);
        lamdaB=(Polynomial) ay12.duplicate().twice();
        lamda=(Polynomial) lamdaT.div(lamdaB);

       // return cy12.sub(ay12).sub(lamda.mul(cx12.sub(ax12)));
        return lamda.mul(cx12.sub(ax12)).sub(cy12).add(ay12);

    }

    /**
     *q is point in Fp2
     */

    public Point fob(Point q,BigInteger p){
        Element t1=q.getX().duplicate().pow(p);
        Element t2=q.getY().duplicate().pow(p);

        Point result=(Point) q.getField().newRandomElement();
        result.getX().set(t1);
        result.getY().set(t2);
        return result;
    }
    public Point fob2(Point q,BigInteger p){
        Element t1=q.getX().duplicate().pow(p.pow(2));
        Element t2=q.getY().duplicate().pow(p.pow(2));

        Point result=(Point) q.getField().newRandomElement();
        result.getX().set(t1);
        result.getY().set(t2);
        return result;
    }

    public Point fobasmiracl(Point point){
        Point px=(Point) point.getX().duplicate();
        Point py=(Point)point.getY().duplicate();

       BigInteger q= pairingdata.q;
       //x the frob constant
       Point x=(Point) pairingdata.Fq2.newElement();
       x.getX().setToZero();
       x.getY().setToOne();
      // System.out.println(q.subtract(BigInteger.ONE).divide(BigInteger.valueOf(6)).toString(16));
       x.pow(q.subtract(BigInteger.ONE).divide(BigInteger.valueOf(6)));

       Point w,r;
       r=(Point)x.duplicate().invert();
       w=(Point)r.duplicate().square();

       px.getY().negate();
       px.mul(w);

       py.getY().negate();
       py.mul(w).mul(r);

       Point result=(Point) point.getField().newRandomElement();
       result.getX().set(px);
       result.getY().set(py);
       return result;



    }
}
