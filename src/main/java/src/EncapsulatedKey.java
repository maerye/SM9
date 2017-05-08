package src;

import iaik.security.ec.math.curve.ECPoint;
import mcl.bn254.Ec1;

/**
 * Created by mzy on 2017/4/24.
 */
public class EncapsulatedKey {
    private byte[]k;
    private ECPoint c;

    public EncapsulatedKey(byte[] k, ECPoint c) {
        this.k = k;
        this.c = c;
    }

    public byte[] getK() {
        return k;
    }

    public ECPoint getC() {
        return c;
    }
}
