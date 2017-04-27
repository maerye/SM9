package src;

import mcl.bn254.Ec1;

/**
 * Created by mzy on 2017/4/24.
 */
public class EncapsulatedKey {
    private byte[]k;
    private Ec1 c;

    public EncapsulatedKey(byte[] k, Ec1 c) {
        this.k = k;
        this.c = c;
    }

    public byte[] getK() {
        return k;
    }

    public Ec1 getC() {
        return c;
    }
}
