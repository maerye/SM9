package src;

import org.bouncycastle.math.myec.bncurves.BNPoint;

/**
 * Created by mzy on 2017/4/17.
 */
public class Sm9SignPrivateKey {


    private BNPoint ds;

    public Sm9SignPrivateKey(BNPoint point)
    {
        this.ds=point;
    }
    public BNPoint getDs() {
        return ds;
    }
}
