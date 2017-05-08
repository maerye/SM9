package src;

import iaik.security.ec.math.curve.ECPoint;
import mcl.bn254.Ec1;


/**
 * Created by mzy on 2017/4/17.
 */
public class Sm9SignPrivateKey {


    private ECPoint ds;

    public Sm9SignPrivateKey(ECPoint point)
    {
        this.ds=point;
    }
    public ECPoint getDs() {
        return ds;
    }
}
