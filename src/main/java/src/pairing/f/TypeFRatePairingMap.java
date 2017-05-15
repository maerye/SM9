package src.pairing.f;

import src.api.Element;
import src.api.Pairing;
import src.api.Point;
import src.pairing.f.map.AbstractPairingMap;

/**
 * Created by mzy on 2017/5/15.
 */
public class TypeFRatePairingMap extends AbstractPairingMap{

    private TypeFPairing pairingdata;

    public TypeFRatePairingMap(TypeFPairing pairing){
        super(pairing);
        this.pairingdata=pairing;
    }

    public Element pairing(Point in1, Point in2) {
        return null;
    }

    public void finalPow(Element element) {

    }

    /**
     * Compute the tangent line L (aX + bY + c) through the points V = (Vx, Vy) e V1 = (V1x, V1y).
     *
     * @param a    the coefficient of X of tangent line T.
     * @param b    the coefficient of Y of tangent line T.
     * @param c    the constant term f tangent line T.
     * @param Vx   V's x.
     * @param Vy   V's y.
     * @param V1x  V1's x.
     * @param V1y  V1's y.
     * @param temp temp element.
     */
    protected final void computeLine(final Element a, final Element b, final Element c,
                                     final Element Vx, final Element Vy,
                                     final Element V1x, final Element V1y,
                                     final Element temp) {

        // a = -(V1y - Vy) / (V1x - Vx);
        // b = 1;
        // c = -(Vy + a * Vx);
        //
        // but we will multiply by V1x - Vx to avoid division, so
        //
        // a = -(V1y - Vy)
        // b = V1x - Vx
        //c = -(2 Vy^2 + a Vx);

        a.set(Vy).sub(V1y);
        b.set(V1x).sub(Vx);
        c.set(Vx).mul(V1y).sub(temp.set(Vy).mul(V1x));
    }

    protected final void computeTangent(final Element a, final Element b, final Element c,
                                        final Element Vx, final Element Vy,
                                        final Element curveA,
                                        final Element temp) {
        //a = -slope_tangent(V.x, V.y);
        //b = 1;
        //c = -(V.y + aV.x);
        //but we multiply by -2*V.y to avoid division so:
        //a = -(3 Vx^2 + cc->a)
        //b = 2 * Vy

        //c = -(Vy b + a Vx);

        a.set(Vx).square().mul(3).add(curveA).negate();
        b.set(Vy).twice();
        c.set(a).mul(Vx).add(temp.set(b).mul(Vy)).negate();
    }
}
