package src;

import org.bouncycastle.math.myec.bncurves.BNPoint;

/**
 * Created by mzy on 2017/4/24.
 */
public class EncapsulatedKey {
    private byte[]k;
    private BNPoint c;

    public EncapsulatedKey(byte[] k, BNPoint c) {
        this.k = k;
        this.c = c;
    }

    public byte[] getK() {
        return k;
    }

    public BNPoint getC() {
        return c;
    }
}
