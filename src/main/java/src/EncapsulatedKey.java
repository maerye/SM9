package src;

import iaik.security.ec.math.curve.ECPoint;
import mcl.bn254.Ec1;
import src.field.curve.CurveElement;

/**
 * Created by mzy on 2017/4/24.
 */
public class EncapsulatedKey {
    private byte[]k;
    private CurveElement c;

    public EncapsulatedKey(byte[] k, CurveElement c) {
        this.k = k;
        this.c = c;
    }

    public byte[] getK() {
        return k;
    }

    public CurveElement getC() {
        return c;
    }
}
