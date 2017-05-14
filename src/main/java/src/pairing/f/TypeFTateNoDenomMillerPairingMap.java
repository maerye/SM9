package src.pairing.f;


import src.api.Element;
import src.api.Point;
import src.api.Polynomial;
import src.field.gt.GTFiniteElement;
import src.field.gt.GTFiniteField;
import src.pairing.f.map.AbstractMillerPairingMap;

public class TypeFTateNoDenomMillerPairingMap extends AbstractMillerPairingMap {
    protected TypeFPairing pairingData;


    public TypeFTateNoDenomMillerPairingMap(TypeFPairing pairing) {
        super(pairing);

        this.pairingData = pairing;
    }


    public Element pairing(Point in1, Point in2) {
        //map from twist: (x, y) --> (v^-2 x, v^-3 y)
        //where v is the sixth root used to construct the twist
        //i.e. v^6 = -alpha
        //thus v^-2 = -alpha^-1 v^4
        //and  v^-3 = -alpha^-1 v^3
        Point x = (Point) in2.getX().duplicate().mul(pairingData.negAlphaInv);
        Point y = (Point) in2.getY().duplicate().mul(pairingData.negAlphaInv);

        return new GTFiniteElement(
                this,
                (GTFiniteField) pairingData.getGT(),
                tateExp((Polynomial) pairing(in1, x, y))
        );
    }

    public void finalPow(Element element) {
        element.set(tateExp((Polynomial) element));
    }


    public Element tateExp(Polynomial element) {
        Polynomial x = pairingData.Fq12.newElement();
        Polynomial y = pairingData.Fq12.newElement();

        qPower(element, y, pairingData.xPowq8);
        qPower(element, x, pairingData.xPowq6);
        y.mul(x);
        qPower(element, x, pairingData.xPowq2);

        return y.mul(x.mul(element).invert()).pow(pairingData.tateExp);
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

    protected Element pairing(Point P, Point Qx, Point Qy) {
        Element Px = P.getX();
        Element Py = P.getY();

        Point Z = (Point) P.duplicate();
        Element Zx = Z.getX();
        Element Zy = Z.getY();

        Element a = Px.getField().newElement();
        Element b = a.duplicate();
        Element c = a.duplicate();
        Element t0 = a.duplicate();
        Element cca = a.duplicate();

        Polynomial e0 = pairingData.Fq12.newElement();
        Polynomial v = (Polynomial) pairingData.Fq12.newOneElement();

        for (int m = pairingData.r.bitLength() - 2; m > 0; m--) {
            computeTangent(a, b, c, Zx, Zy, cca, t0);
            millerStep(e0, v, a, b, c, Qx, Qy);
            Z.twice();

            if (pairingData.r.testBit(m)) {
                computeLine(a, b, c, Zx, Zy, Px, Py, t0);
                millerStep(e0, v, a, b, c, Qx, Qy);
                Z.add(P);
            }

            v.square();
        }
        computeTangent(a, b, c, Zx, Zy, cca, t0);
        millerStep(e0, v, a, b, c, Qx, Qy);

        return v;
    }

    protected void millerStep(Point out_Renamed, Element a, Element b, Element c, Element Qx, Element Qy) {
    }

    protected void millerStep(Polynomial e0, Polynomial v, Element a, Element b, Element c, Element Qx, Element Qy) {
        // a, b, c lie in Fq
        // Qx, Qy lie in Fq^2
        // Qx is coefficient of x^4
        // Qy is coefficient of x^3
        //
        // computes v *= (a Qx x^4 + b Qy x^3 + c)
        //
        // recall x^6 = -alpha thus
        // x^4 (u0 + u1 x^1 + ... + u5 x^5) =
        // u0 x^4 + u1 x^5
        // - alpha u2 - alpha u3 x - alpha u4 x^2 - alpha u5 x^3
        // and
        // x^4 (u0 + u1 x^1 + ... + u5 x^5) =
        // u0 x^3 + u1 x^4 + u2 x^5
        // - alpha u3 - alpha u4 x - alpha u5 x^2

        millerStepTerm(0, 2, 3, 2, e0, v, a, b, c, Qx, Qy);
        millerStepTerm(1, 3, 4, 2, e0, v, a, b, c, Qx, Qy);
        millerStepTerm(2, 4, 5, 2, e0, v, a, b, c, Qx, Qy);
        millerStepTerm(3, 5, 0, 1, e0, v, a, b, c, Qx, Qy);
        millerStepTerm(4, 0, 1, 0, e0, v, a, b, c, Qx, Qy);
        millerStepTerm(5, 1, 2, 0, e0, v, a, b, c, Qx, Qy);
        v.set(e0);
    }

    protected void millerStepTerm(int i, int j, int k, int flag, 
                                  Polynomial e0, Polynomial v,
                                  Element a, Element b, Element c,
                                  Element Qx, Element Qy) {
        Point e2 = (Point) e0.getCoefficient(i);
        Point e1 = (Point) v.getCoefficient(j).duplicate().mul(Qx);
        if (flag == 1)
            e1.mul(pairingData.negAlpha);
        e1.getX().mul(a);
        e1.getY().mul(a);
        e2.set(v.getCoefficient(k)).mul(Qy);
        e2.getX().mul(b);
        e2.getY().mul(b);
        e2.add(e1);
        if (flag == 2)
            e2.mul(pairingData.negAlpha);
        e1.set(v.getCoefficient(i));
        e1.getX().mul(c);
        e1.getY().mul(c);
        e2.add(e1);
    }
}