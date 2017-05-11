package src;

import org.bouncycastle.math.myec.bncurves.BNPoint;
import java.math.BigInteger;

/**
 * Created by mzy on 2017/4/19.
 */
public class Signature {
    public BigInteger h;
    BNPoint s;

    public Signature (BigInteger h, BNPoint s)
    {
        this.h=h;
        this.s=s;
    }
}
