package src;

import iaik.security.ec.math.curve.ECPoint;
import mcl.bn254.Ec1;

import java.math.BigInteger;


/**
 * Created by mzy on 2017/4/19.
 */
public class Signature {
    public BigInteger h;
    ECPoint s;

    public Signature (BigInteger h, ECPoint s)
    {
        this.h=h;
        this.s=s;
    }
}
