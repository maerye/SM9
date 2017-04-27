package src;

import mcl.bn254.Ec1;
import mcl.bn254.Ec2;
import org.bouncycastle.math.myec.bncurves.BNPoint;

/**
 * Created by mzy on 2017/4/17.
 */
public class Sm9SignPrivateKey extends Sm9Key{


    private Ec1 ds;

    public Sm9SignPrivateKey(Ec1 point)
    {
        this.ds=point;
    }
    public Ec1 getDs() {
        return ds;
    }
}
