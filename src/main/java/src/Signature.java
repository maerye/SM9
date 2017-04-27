package src;

import mcl.bn254.Ec1;
import org.bouncycastle.math.myec.bncurves.BNPoint;

import java.math.BigInteger;
import java.util.logging.SimpleFormatter;

/**
 * Created by mzy on 2017/4/19.
 */
public class Signature {
    public BigInteger h;
    Ec1 s;

    public Signature (BigInteger h, Ec1 s)
    {
        this.h=h;
        this.s=s;
    }
}
